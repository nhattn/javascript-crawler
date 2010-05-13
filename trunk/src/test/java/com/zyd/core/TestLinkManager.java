package com.zyd.core;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;

import com.zyd.ATestUtil;
import com.zyd.Constants;
import com.zyd.core.busi.LinkManager;
import com.zyd.core.dom.Link;

public class TestLinkManager extends TestCase {
    LinkManager linkMan;
    ArrayList<String> allLinks;

    @Override
    protected void setUp() throws Exception {
        Constants.WATCH_LIST = new Link[0];
        ApplicationContext context = ATestUtil.setUpSpring();
        linkMan = (LinkManager) context.getBean("linkManager");
        linkMan.deleteAllLinks();
        allLinks = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            allLinks.add("http://link.com/link_" + i);
        }
        for (int i = 100; i < 200; i++) {
            allLinks.add("http://link.com/link_" + i);
        }
        for (String s : allLinks) {
            linkMan.addLink(s);
        }
    }

    public void testStoreAndLoad() throws Exception {
        linkMan.storeToDb();
        linkMan.clearCache();
        linkMan.loadFromDb();
        assertEquals(200, linkMan.getWaiting().size());
        assertEquals(0, linkMan.getProcessed().size());
        assertEquals(0, linkMan.getError().size());
        assertEquals(0, linkMan.getProcessing().size());
    }

    public void testProcessingAndWaiting() {
        ArrayList<Link> list = new ArrayList<Link>();
        for (int i = 0; i < 50; i++) {
            list.add(linkMan.nextLink());
        }
        // make sure waiting and processing adds up
        assertEquals(150, linkMan.getWaiting().size());
        assertEquals(0, linkMan.getProcessed().size());
        assertEquals(0, linkMan.getError().size());
        assertEquals(50, linkMan.getProcessing().size());

        // when storing to db, processing should become waiting
        linkMan.storeToDb();
        linkMan.clearCache();
        linkMan.loadFromDb();
        assertEquals(200, linkMan.getWaiting().size());
        assertEquals(0, linkMan.getProcessed().size());
        assertEquals(0, linkMan.getError().size());
        assertEquals(0, linkMan.getProcessing().size());
    }

    public void testLinkError() {
        ArrayList<Link> errorList = new ArrayList<Link>();
        ArrayList<Link> rightList = new ArrayList<Link>();
        for (int i = 0; i < 25; i++) {
            errorList.add(linkMan.nextLink());
        }
        for (int i = 0; i < 25; i++) {
            rightList.add(linkMan.nextLink());
        }

        for (Link link : errorList) {
            linkMan.linkError(link.url, "error " + link.url);
        }
        //make sure waiting and error and processing adds up
        assertEquals(150, linkMan.getWaiting().size());
        assertEquals(0, linkMan.getProcessed().size());
        assertEquals(25, linkMan.getError().size());
        assertEquals(25, linkMan.getProcessing().size());

        for (Link link : rightList) {
            linkMan.linkFinished(link.url);
        }

        // make sure waiting and processed and error adds up
        assertEquals(150, linkMan.getWaiting().size());
        assertEquals(25, linkMan.getProcessed().size());
        assertEquals(25, linkMan.getError().size());
        assertEquals(0, linkMan.getProcessing().size());

        // make sure try count is correct for error links
        ArrayList<Link> allLinkList = new ArrayList<Link>();
        while (true) {
            Link link = linkMan.nextLink();
            if (link.equals(LinkManager.IdlePageUrl)) {
                break;
            }
            allLinkList.add(link);
            linkMan.linkFinished(link.url);
        }
        for (Link link : errorList) {
            assertEquals(link.tryCount, 2);
        }
        assertEquals(0, linkMan.getWaiting().size());
        assertEquals(200, linkMan.getProcessed().size());
        assertEquals(0, linkMan.getError().size());
        assertEquals(0, linkMan.getProcessing().size());

        //store it, then load
        linkMan.storeToDb();
        linkMan.clearCache();
        linkMan.loadFromDb();
        assertEquals(0, linkMan.getProcessing().size());
        assertEquals(200, linkMan.getProcessed().size());
        assertEquals(0, linkMan.getError().size());
        assertEquals(0, linkMan.getProcessing().size());
        // there should be 25 links with try count 2
        int count = 0;
        for (Object objs : linkMan.getProcessed()) {
            Link link = (Link) objs;
            if (link.tryCount == 2) {
                count++;
            }
        }

        assertEquals(25, count);
    }
}

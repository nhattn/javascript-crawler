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
        linkMan.clearCache();
        linkMan.loadFromDb();
        assertEquals(200, linkMan.getWaiting().size());
        assertEquals(0, linkMan.getProcessed().size());
        assertEquals(0, linkMan.getError().size());
        assertEquals(0, linkMan.getProcessing().size());
    }

    public void testLinkError() {
        Constants.LINK_MAX_TRY = 3;
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

        for (Link link : errorList) {
            assertEquals(link.tryCount, 1);
        }

        for (Link link : rightList) {
            linkMan.linkFinished(link.url);
        }

        // make sure waiting and processed and error adds up
        assertEquals(150, linkMan.getWaiting().size());
        assertEquals(25, linkMan.getProcessed().size());
        assertEquals(25, linkMan.getError().size());
        assertEquals(0, linkMan.getProcessing().size());

        for (Link link : rightList) {
            assertEquals(link.tryCount, 1);
        }

        // make sure try count is correct for error links
        ArrayList<Link> allLinkList = new ArrayList<Link>();
        int i = 0;
        while (true) {
            i++;
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

    /**
     * test when link is given up
     */

    public void testLinkError2() {
        Constants.LINK_MAX_TRY = 1;
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
        assertEquals(25, linkMan.getProcessed().size());
        assertEquals(0, linkMan.getError().size());
        assertEquals(25, linkMan.getProcessing().size());

        for (Link link : errorList) {
            assertEquals(link.tryCount, 1);
            assertNotNull(link.processTime);
            assertEquals(link.isError, 1);
            assertTrue(link.startTime == null);
        }

        for (Link link : rightList) {
            linkMan.linkFinished(link.url);
            assertEquals(link.tryCount, 1);
            assertNotNull(link.processTime);
            assertEquals(link.isError, 0);
            assertTrue(link.startTime == null);
        }

        // make sure waiting and processed and error adds up
        assertEquals(150, linkMan.getWaiting().size());
        assertEquals(50, linkMan.getProcessed().size());
        assertEquals(0, linkMan.getError().size());
        assertEquals(0, linkMan.getProcessing().size());

        for (Link link : rightList) {
            assertEquals(link.tryCount, 1);
        }
    }
}

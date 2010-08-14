package com.zyd.linkmanager;

import java.util.ArrayList;
import java.util.HashSet;

import junit.framework.TestCase;

import com.zyd.ATestUtil;
import com.zyd.Constants;
import com.zyd.core.busi.WorkerThread;
import com.zyd.core.util.SpringContext;
import com.zyd.linkmanager.mysql.DbHelper;
import com.zyd.linkmanager.mysql.LinkTableInfo;
import com.zyd.linkmanager.mysql.LinkTableMapper;
import com.zyd.linkmanager.mysql.MysqlLinkManager;

public class TestNewLinkManager extends TestCase {
    int linkSize = 1000;
    String[] domains = new String[] { "domain_1.com", "domain_2.com", "domain_3.com", "domain_4.com" };
    HashSet<String> domainSet;
    ArrayList<String> links;
    int totalLinkCount;

    protected void setUp() throws Exception {
        TestDbHelper.deleteAllLinkTableInfo();
        ATestUtil.setUpSpring();
        links = new ArrayList<String>();
        for (String domain : domains) {
            for (int i = 0; i < linkSize; i++) {
                String link = "http://" + domain + "/link_" + i;
                links.add(link);
            }
        }
        domainSet = new HashSet<String>();
        for (String s : domains) {
            domainSet.add(s);
        }
        totalLinkCount = linkSize * domains.length;
    }

    public void testAdd() throws Exception {
        LinkManager linkMan = new MysqlLinkManager();
        // adding links
        for (String s : links) {
            Link l = linkMan.addLink(s);
            assertNotNull(l);
            assertTrue(l.getId() > 0);
        }

        // test get next link 
        HashSet<String> linkUrls = new HashSet<String>(links);
        String previousDomain = "";
        for (int i = 0; i < totalLinkCount; i++) {
            Link link = linkMan.roundRobinNextLink();
            assertNotNull(link);
            String domain = LinkTableMapper.mapUrl(link.getUrl());
            assertNotSame(domain, previousDomain);
            previousDomain = domain;

            assertTrue(link.getUrl(), linkUrls.remove(link.getUrl()));
        }

        // make sure link is exhausted
        assertNull(linkMan.roundRobinNextLink());
        assertEquals(0, linkUrls.size());

        // make sure duplicate link can not be added
        for (String s : links) {
            Link l = linkMan.addLink(s);
            assertNull(l);
        }

        // finish links
        for (String s : links) {
            Link link = linkMan.linkFinished(s);
            assertNotNull(link);
            assertEquals(Link.STATE_FINISHED_OK, link.getState());
        }

        assertNull(linkMan.roundRobinNextLink());

        // make sure duplicate link can not be added
        for (String s : links) {
            Link l = linkMan.addLink(s);
            assertNull(l);
        }
    }

    public void testLinkExpire() throws Exception {
        LinkManager linkman = new MysqlLinkManager();
        Constants.WORKER_THREAD_EXECUTION_INTERVAL = 1 * 1000;
        Constants.LINK_PROCESSING_EXPIRE = 2 * 1000;
        Constants.LINK_MONITOR_SCAN_INTERVAL = 1 * 1000;
        WorkerThread wt = (WorkerThread) SpringContext.getContext().getBean("workerThread");
        wt.registerWork(linkman);

        HashSet<String> expiredLinks = new HashSet<String>();
        wt.start();

        for (int i = 0; i < 1000; i++) {
            linkman.addLink("http://www.test.com/link_" + i);
        }
        for (int i = 0; i < 500; i++) {
            Link l = linkman.roundRobinNextLink();
            assertNotNull(l);
            expiredLinks.add(l.getUrl());
        }
        for (int i = 0; i < 8000; i++) {
            linkman.addLink("http://www.test2.com/linkxnew_" + i);
        }

        for (int i = 0; i < 500; i++) {
            Link l = linkman.roundRobinNextLink();
            assertNotNull(l);
            expiredLinks.add(l.getUrl());
        }

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
        }
        wt.stop();

        LinkTableInfo info = DbHelper.getLinkTableInfoByUid(LinkTableMapper.mapUrl("http://www.test.com"));
        ArrayList<Link> links = DbHelper.loadLinkByState(info.getTableName(), Link.STATE_FINISHED_TIME_OUT, 10000);
        assertTrue(links.size() > 0);
        for (Link link : links) {
            assertTrue(expiredLinks.remove(link.getUrl()));
        }

        info = DbHelper.getLinkTableInfoByUid(LinkTableMapper.mapUrl("http://www.test2.com"));
        links = DbHelper.loadLinkByState(info.getTableName(), Link.STATE_FINISHED_TIME_OUT, 10000);
        assertTrue(links.size() > 0);
        for (Link link : links) {
            assertTrue(expiredLinks.remove(link.getUrl()));
        }
        assertTrue(expiredLinks.size() == 0);
    }

    public void testLinkManagerRestart() throws Exception {
        LinkManager linkman = new MysqlLinkManager();
        HashSet<String> processingLinks = new HashSet<String>();
        for (int i = 0; i < 1000; i++) {
            linkman.addLink("http://www.test.com/link_" + i);
        }
        for (int i = 0; i < 1000; i++) {
            linkman.addLink("http://www.test3.com/link_" + i);
        }
        for (int i = 0; i < 100; i++) {
            processingLinks.add(linkman.roundRobinNextLink().getUrl());
        }
        // construct  a new linkmanager, init it to load linkstore
        linkman = new MysqlLinkManager();
        assertNotNull(linkman.getLink("http://www.test.com/link_1"));
        assertNotNull(linkman.getLink("http://www.test3.com/link_1"));

        while (true) {
            Link link = linkman.roundRobinNextLink();
            if (link == null)
                break;
            processingLinks.remove(link.getUrl());
        }
        assertTrue(processingLinks.size() == 0);
    }
}

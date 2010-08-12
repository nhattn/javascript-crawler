package com.zyd.core.nottest;

import java.util.HashMap;
import java.util.HashSet;

import junit.framework.TestCase;

import com.zyd.ATestUtil;
import com.zyd.linkmanager.Link;
import com.zyd.linkmanager.LinkManager;
import com.zyd.linkmanager.mysql.MysqlLinkManager;

public class NotTestNewLinkManager extends TestCase {
/*
    LinkManager man;

    HashSet<String> domains;

    int totalLink = 0;

    @Override
    protected void setUp() throws Exception {
        ATestUtil.setUpSpring();
        man = new MysqlLinkManager();
        domains = new HashSet<String>();
        domains.add("www.domain1.com");
        domains.add("wwww1.domain1.com");
        domains.add("www.domain1.com.cn");
        domains.add("wwww.domain1.cn");
        totalLink = 0;
        man.cleanAll();
    }

    public void generateSomeLinks() {
        for (String domain : domains) {
            for (int i = 0; i < 299; i++) {
                String url = "http://" + domain + "/" + i;
                long id = man.addLink(url).getId();
                assertTrue(id > 0);
                totalLink++;
            }
        }
    }

    public void testAddLink() throws Exception {
        generateSomeLinks();
        int counter = 0;
        while (man.nextUnprocessedLink() != null) {
            counter++;
        }
        assertEquals(counter, totalLink);
    }

    public void testGetLink() throws Exception {
        generateSomeLinks();
        for (String domain : domains) {
            for (int i = 0; i < 299; i++) {
                String url = "http://" + domain + "/" + i;
                Link link = man.getLink(url);
                assertNotNull(link);
                assertTrue(link.getId() > 0);
                assertEquals(link, link.getUrl());
            }
        }
    }

    public void testGetUnprocessedLink() {
        generateSomeLinks();
        for (String domain : domains) {
            for (int i = 0; i < 299; i++) {
                Link link = man.nextUnprocessedLink();
                assertNotNull(link);
                assertTrue(link.getState() == Link.STATE_NOT_PROCESSED);
                man.linkFinished(link.getUrl());

                link = man.getLink(link.getUrl());
                assertEquals(link.getState(), link.STATE_FINISHED_OK);
            }
        }
    }

    public void testLinkTimeOut() throws Exception {
        int expire = 5 * 1000;
        HashMap<String, String> config = new HashMap<String, String>();
        config.put("LINK_PROCESSING_EXPIRE", Integer.toString(expire));
        config.put("LINK_MONITOR_SLEEP", Integer.toString(expire));
        config.put("LINK_MAX_TRY", Integer.toString(1));
        ATestUtil.updateServerConfigure(config);

        HashSet<String> urls = new HashSet<String>();
        for (int i = 0; i < 20; i++) {
            urls.add(man.roundRobinNextLink().getUrl());
        }
        try {
            Thread.sleep(expire * 3);
        } catch (Exception e) {
        }
        for (String url : urls) {
            Link link = man.getLink(url);
            assertEquals(Link.STATE_FINISHED_ERROR, link.getState());
        }
        assertTrue(ATestUtil.reststoreServerConfigure());
    }
    */
}

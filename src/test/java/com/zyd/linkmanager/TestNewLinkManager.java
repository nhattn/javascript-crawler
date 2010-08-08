package com.zyd.linkmanager;

import java.util.ArrayList;
import java.util.HashSet;

import junit.framework.TestCase;

import com.zyd.ATestUtil;
import com.zyd.core.Utils;
import com.zyd.linkmanager.mysql.MysqlLinkManager;

public class TestNewLinkManager extends TestCase {
    int linkSize = 1000;
    String[] domains = new String[] { "domain_1.com", "domain_2.com", "domain_3.com", "domain_4.com" };
    HashSet<String> domainSet;
    ArrayList<String> links;
    LinkManager linkMan = null;;
    int totalLinkCount;

    @Override
    protected void setUp() throws Exception {
        linkMan = new MysqlLinkManager();
        ATestUtil.setUpSpring();
        TestDbHelper.deleteAllLinkTableInfo();

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
        // adding links
        for (String s : links) {
            //            System.out.println(s);
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
            String domain = Utils.getShortestDomain(link.getUrl());
            assertNotNull(domain, previousDomain);
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

}

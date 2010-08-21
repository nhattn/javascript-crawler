package com.zyd.linkmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import junit.framework.TestCase;

import com.tj.common.util.test.CommonTestUtil;
import com.zyd.ATestConstants;
import com.zyd.ATestUtil;
import com.zyd.Constants;
import com.zyd.core.Utils;
import com.zyd.linkmanager.Link;
import com.zyd.linkmanager.mysql.DbHelper;
import com.zyd.linkmanager.mysql.LinkTableInfo;
import com.zyd.linkmanager.mysql.LinkTableMapper;

public class TestLinkManager extends TestCase {
    int expire = 3 * 1000;
    int sleep = 2 * 1000;
    public static HashSet<String> watchedList;

    @Override
    protected void setUp() throws Exception {
        assertTrue(ATestUtil.clearServerData("Link"));
        watchedList = new HashSet<String>();
        for (Link link : Constants.WATCH_LIST) {
            watchedList.add(link.getUrl());
        }
    }

    /**
     * Make sure when link is processed fro too long, it's treated as timeout.
     * this only works when testing and server is running on the same machine.
     * @throws Exception
     */
    public void nottestFlushExpiredLink() throws Exception {
        ATestUtil.createSomeLinks();
        HashMap<String, String> config = new HashMap<String, String>();
        config.put("LINK_PROCESSING_EXPIRE", Integer.toString(expire));
        config.put("WORKER_THREAD_EXECUTION_INTERVAL", Integer.toString(sleep));
        config.put("LINK_MONITOR_SCAN_INTERVAL", Integer.toString(sleep));

        String s = null;
        ATestUtil.updateServerConfigure(config);
        for (int i = 0; i < 5; i++) {
            ATestUtil.getNextLink();
        }
        try {
            System.err.println("Wait for thread sleep, make sure LinkManager is going through enough cycles, sleep for " + sleep * 3 / 1000 + "seconds");
            Thread.sleep(sleep * 3);
        } catch (Exception e) {
        }
        LinkTableInfo info = DbHelper.getLinkTableInfoByUid(LinkTableMapper.mapUrl(ATestConstants.OBJECT_REFERER_PREFIX));
        ArrayList<Link> links = DbHelper.loadLinkByState(info.getTableName(), Link.STATE_FINISHED_TIME_OUT, 10000);
        assertEquals(5, links.size());
        assertTrue(ATestUtil.reststoreServerConfigure());
        ATestUtil.clearServerData("Link");
    }

    /**
     * Make sure duplicate links won't be accepted,
     * links that is not processed, processing, processed, will be counted
     * @throws Exception
     */
    public void testDuplicateLinks() throws Exception {
        for (int i = 0; i < 100; i++) {
            String link = "http://www.test.com/link_" + i;
            assertTrue(ATestUtil.createLink(link));
        }

        for (int i = 0; i < 100; i++) {
            String link = "http://www.test.com/link_" + i;
            assertFalse(ATestUtil.createLink(link));
        }

        HashSet<String> processing = new HashSet<String>();
        for (int i = 0; i < 50; i++) {
            String link = ATestUtil.getNextLink();
            if (watchedList.contains(link))
                link = ATestUtil.getNextLink();
            assertNotNull(link);
            assertTrue(link, "test.com".equals(Utils.getDomain(link)));
            processing.add(link);
        }
        // make sure processing link is counted
        for (String l : processing) {
            assertFalse(l, ATestUtil.createLink(l));
        }

        // make sure processed link is counted
        for (String l : processing) {
            assertTrue(ATestUtil.createbjectWithReferer(l));
        }

        for (String l : processing) {
            assertFalse(ATestUtil.createLink(l));
        }
        assertTrue(ATestUtil.reststoreServerConfigure());
        ATestUtil.clearServerData("Link");
    }

    public void testDifferentDomain() throws Exception {
        int linkSize = 1000;
        String[] domains = new String[] { "domain_1.com", "domain_2.com", "domain_3.com", "domain_4.com" };
        ArrayList<String> links = new ArrayList<String>();
        for (String domain : domains) {
            for (int i = 0; i < linkSize; i++) {
                String link = "http://" + domain + "/link_" + i;
                links.add(link);
            }
        }
        HashSet<String> domainSet = new HashSet<String>();
        for (String s : domains) {
            domainSet.add(s);
        }
        int totalLinkCount = linkSize * domains.length;
        for (String link : links) {
            assertTrue(ATestUtil.createLink(link));
        }
        String pdomain = null;
        HashMap<String, Integer> count = new HashMap<String, Integer>();
        for (int i = 0; i < 1000; i++) {
            String s = ATestUtil.getNextLink();
            if (watchedList.contains(s)) {
                s = ATestUtil.getNextLink();
            }
            String domain = Utils.getDomain(s);
            assertTrue(domain, domainSet.contains(domain));
            assertTrue(domain.equals(pdomain) == false);
            Integer num = count.get(domain);
            if (num == null) {
                num = 0;
            }
            count.put(domain, (num + 1));
            pdomain = s;
        }
        assertEquals(domains.length, count.size());
        int average = 1000 / domains.length;
        for (Integer i : count.values()) {
            assertTrue(count.toString(), Math.abs(average - i) < 5);
        }
    }

    public void testManyDomain() throws Exception {
        HashSet<String> set1 = new HashSet<String>(), set2 = new HashSet<String>();
        String domainHeader = "xdomain.com";
        HashSet<String> domains = new HashSet<String>();
        int total = 0;
        for (int i = 0; i < 50; i++) {
            domains.add(i + domainHeader);
        }

        for (String domain : domains) {
            int count = CommonTestUtil.nextInt(100);
            for (int i = 0; i < count; i++) {
                total++;
                String url = "http://" + domain + "/" + CommonTestUtil.getNonRepeatString();
                assertTrue(url, set1.add(url));
                assertTrue(ATestUtil.createLink(url));
            }
        }

        for (int i = 0; i < total; i++) {
            String s = ATestUtil.getNextLink();
            if (watchedList.contains(s)) {
                s = ATestUtil.getNextLink();
            }
            assertTrue(s, LinkTableMapper.mapUrl(s).indexOf("xdomain.com") != -1);
            assertTrue(set2.add(s));
        }
        String s = ATestUtil.getNextLink();
        assertEquals(s, -1, LinkTableMapper.mapUrl(s).indexOf("xdomain.com"));
        testDifferentDomain();
    }

    public void testCommonDomainPrefixSuffix() throws Exception {
        String[] url = new String[] { "http://www.aa.com/url1", "http://www.aa.com.cn/url1", "http://www.a.com.cn/url1", "http://11a.com/1", "http://1a.com.cn/1", "http://1a.com/1",
                "http://www.a.com/url1" };
        HashSet<String> urlSet = new HashSet<String>();
        for (String s : url) {
            urlSet.add(s);
        }

        for (String s : url) {
            ATestUtil.createLink(s);
            try {
                Thread.sleep(50);
            } catch (Exception e) {

            }
            String url1 = ATestUtil.getNextLink();
            assertEquals(url1, s, url1);
            for (int i = 0; i < url.length; i++) {
                String waitUrl = ATestUtil.getNextLink();
                assertNotNull(waitUrl);
                assertFalse(urlSet.contains(waitUrl));
            }
        }
    }

    /**
     * make sure adding a very long link won't crash the system.
     * @throws Exception
     */
    public void testLongLinkError() throws Exception {
        StringBuffer buf = new StringBuffer("http://www.test.com/a");
        for (int i = 0; i < 10000; i++) {
            buf.append("a");
        }
        assertFalse(ATestUtil.createLink(buf.toString()));
        ATestUtil.createSomeLinks();
    }

}

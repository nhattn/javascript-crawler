package com.zyd.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import junit.framework.TestCase;

import com.tj.common.util.test.CommonTestUtil;
import com.tj.common.util.test.HttpTestUtil;
import com.zyd.ATestUtil;
import com.zyd.Constants;
import com.zyd.core.Utils;

public class TestLinkManager extends TestCase {
    int expire = 5 * 1000;
    int sleep = 5 * 1000;

    @Override
    protected void setUp() throws Exception {
        assertTrue(ATestUtil.clearServerData());
        ATestUtil.stopReturningWatchedLink();
    }

    public void testFlushExpiredLink() throws Exception {
        ATestUtil.createSomeLinks();
        HashMap<String, String> config = new HashMap<String, String>();
        config.put("LINK_PROCESSING_EXPIRE", Integer.toString(expire));
        config.put("LINK_MONITOR_SLEEP", Integer.toString(expire));
        config.put("LINK_MAX_TRY", Integer.toString(1));

        String s = null;
        ATestUtil.updateServerConfigure(config);
        for (int i = 0; i < 5; i++) {
            ATestUtil.getNextLink();
        }
        try {
            System.err.println("Wait for thread sleep, make sure LinkManager is going through enough cycles");
            Thread.sleep(sleep * 3);
        } catch (Exception e) {
        }
        s = HttpTestUtil.httpGetForString(Constants.ServerUrl + "/service/controller?action=LinkSnapshot", null);
        assertNotNull(s);
        s = s.replaceAll(" ", "");
        assertTrue(s, s.indexOf("error:5") > 0);
        assertTrue(ATestUtil.reststoreServerConfigure());
        ATestUtil.clearServerData();
    }

    /**
     * 
     * @throws Exception
     */
    public void testLinkManagerPurgeLinks() throws Exception {
        String s = null;
        assertTrue(ATestUtil.createSomeObject() > 0);
        HashMap<String, String> configure = new HashMap<String, String>();
        configure.put("LINK_MONITOR_SLEEP", sleep + "");
        configure.put("LINK_LOAD_BEFORE", "1");
        ATestUtil.updateServerConfigure(configure);

        try {
            System.err.println("Wait for thread sleep, make sure LinkManager is going through enough cycles");
            Thread.sleep(sleep * 3);
        } catch (Exception e) {
        }
        s = HttpTestUtil.httpGetForString(Constants.ServerUrl + "/service/controller?action=LinkSnapshot", null);
        assertNotNull(s);
        s = s.replaceAll(" ", "");
        assertTrue(s, s.indexOf("processed:0") != -1);
        assertTrue(s, s.indexOf("error:0") != -1);
        assertTrue(s, s.indexOf("processing:0") != -1);
        assertTrue(s, s.indexOf("waiting:0") != -1);
        assertTrue(ATestUtil.reststoreServerConfigure());
        ATestUtil.clearServerData();
    }

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
            assertNotNull(link);
            assertTrue(link, "test.com".equals(Utils.getShortestDomain(link)));
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
        ATestUtil.clearServerData();
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
            String domain = Utils.getShortestDomain(s);
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
        for (int i = 0; i < 100; i++) {
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
            assertTrue(s, Utils.getShortestDomain(s).indexOf("xdomain.com") != -1);
            assertTrue(set2.add(s));
        }
        String s = ATestUtil.getNextLink();
        assertEquals(s, -1, Utils.getShortestDomain(s).indexOf("xdomain.com"));
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

}

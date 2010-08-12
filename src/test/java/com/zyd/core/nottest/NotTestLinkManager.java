package com.zyd.core.nottest;

import junit.framework.TestCase;

public class NotTestLinkManager extends TestCase {
    /*
    int linkSize = 1000;
    String[] domains = new String[] { "domain_1.com", "domain_2.com", "domain_3.com", "domain_4.com" };
    HashSet<String> domainSet;
    ArrayList<String> links;
    LinkManager linkMan = null;;
    int totalLinkCount;

    @Override
    protected void setUp() throws Exception {
        ATestUtil.setUpSpring();
        linkMan = (LinkManager) SpringContext.getContext().getBean("linkManager");
        linkMan.cleanAll();
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
        linkMan.cleanAll();
    }

    public void testAdd() throws Exception {
        // add link from different domain, 
        for (String link : links) {
            assertNotNull(linkMan.add(link));
        }
        checkSize(new int[] { 0, links.size(), 0, 0 }, linkMan.getAllQueueSize());
        // make sure it stores correctly and loaded again correctly
        linkMan.clearCache();
        linkMan.loadFromDb();
        checkSize(new int[] { 0, links.size(), 0, 0 }, linkMan.getAllQueueSize());
        linkMan.cleanAll();
    }

    public void testNextLink() {
        // make sure it returns link from different domain
        for (String link : links) {
            assertNotNull(linkMan.add(link));
        }
        HashMap<String, Integer> count = new HashMap<String, Integer>();
        String pdomain = null;
        for (int i = 0; i < 2000; i++) {
            Link link = linkMan.next();
            String domain = Utils.getShortestDomain(link.url);
            Integer num = count.get(domain);
            if (num == null) {
                num = 0;
            }
            count.put(domain, (num + 1));
            assertTrue(domain.equals(pdomain) == false);
            assertTrue(domain + ":" + domainSet.toString(), domainSet.contains(domain));
            pdomain = domain;
        }
        assertEquals(domains.length, count.size());
        int average = 2000 / domains.length;
        for (Integer i : count.values()) {
            assertTrue(Math.abs(average - i) < 5);
        }

        // make sure it's not returning watched link until all added link is finished

        linkMan.cleanAll();
    }

    public void testNextLinkExhausted() {
        int count = 0, extra = 100;
        for (String link : links) {
            assertNotNull(linkMan.add(link));
        }
        for (int i = 0; i < extra; i++) {
            linkMan.add("http://" + domains[0] + "/link_xyz_" + i);
        }
        for (int i = 0; i < extra; i++) {
            linkMan.add("http://" + domains[2] + "/link_xyz_" + i);
        }
        while (true) {
            Link link = linkMan.next();
            String url = link.url;
            if (domainSet.contains(Utils.getShortestDomain(url)) == false) {
                break;
            }
            count++;
        }
        assertEquals(domains.length * linkSize + extra * 2, count);
        linkMan.cleanAll();
    }

    public void testLinkProcessingAndFinishing() {
        for (String link : links) {
            assertNotNull(linkMan.add(link));
        }
        // make sure link being processed is calcuated correctly
        int count = 583;
        HashSet<Link> links = new HashSet<Link>();
        for (int i = 0; i < count; i++) {
            links.add(linkMan.next());
        }

        checkSize(new int[] { count, totalLinkCount - count, 0, 0 }, linkMan.getAllQueueSize());
        // make sure link being processed is treated as waiting when interrupted and loaded again
        linkMan.clearCache();
        linkMan.loadFromDb();
        checkSize(new int[] { 0, totalLinkCount, 0, 0 }, linkMan.getAllQueueSize());

        // make sure link is finished correctly and store correctly
        links = new HashSet<Link>();
        for (int i = 0; i < count; i++) {
            links.add(linkMan.next());
        }

        for (Link link : links) {
            linkMan.linkFinished(link.url);
        }
        checkSize(new int[] { 0, totalLinkCount - count, count, 0 }, linkMan.getAllQueueSize());
        linkMan.clearCache();
        linkMan.loadFromDb();
        checkSize(new int[] { 0, totalLinkCount - count, count, 0 }, linkMan.getAllQueueSize());
        linkMan.cleanAll();
    }

    public void testLinkError() {
        for (String link : links) {
            assertNotNull(linkMan.add(link));
        }

        // make sure link  with an error is stored correctly
        int old = Constants.LINK_MAX_TRY;
        Constants.LINK_MAX_TRY = 1;
        int count = totalLinkCount / 2;
        HashSet<Link> mlinks = new HashSet<Link>();
        for (int i = 0; i < count; i++) {
            mlinks.add(linkMan.next());
        }
        for (Link link : mlinks) {
            linkMan.linkError(link.url, "error");
        }
        checkSize(new int[] { 0, totalLinkCount - count, 0, count }, linkMan.getAllQueueSize());
        linkMan.clearCache();
        linkMan.loadFromDb();
        checkSize(new int[] { 0, totalLinkCount - count, 0, count }, linkMan.getAllQueueSize());

        // make sure link with error less than maxtry is put back in waiting list
        Constants.LINK_MAX_TRY = 2;
        linkMan.cleanAll();
        for (String link : links) {
            assertNotNull(linkMan.add(link));
        }
        mlinks.clear();
        for (int i = 0; i < count; i++) {
            mlinks.add(linkMan.next());
        }
        for (Link link : mlinks) {
            linkMan.linkError(link.url, "error");
        }
        checkSize(new int[] { 0, totalLinkCount, 0, 0 }, linkMan.getAllQueueSize());
        Constants.LINK_MAX_TRY = old;
        linkMan.cleanAll();
    }

    public void testDuplicates() {
        for (String link : links) {
            assertNotNull(linkMan.add(link));
        }
        // check link that is finished can not be duplicated
        Link link = linkMan.next();
        String url = link.url;
        linkMan.linkFinished(url);
        assertTrue(linkMan.add(url) == null);

        // check same for errors
        int old = Constants.LINK_MAX_TRY;
        Constants.LINK_MAX_TRY = 1;
        link = linkMan.next();
        url = link.url;
        linkMan.linkError(url, "error");
        assertTrue(linkMan.add(url) == null);
        Constants.LINK_MAX_TRY = old;
        // check same for processing
        link = linkMan.next();
        url = link.url;
        assertTrue(linkMan.add(url) == null);
        linkMan.cleanAll();
    }

    private void checkSize(int[] sizes, int[] actual) {
        assertEquals(sizes.length, actual.length);
        assertEquals("processing", sizes[0], actual[0]);
        assertEquals("waiting", sizes[1], actual[1]);
        assertEquals("processed", sizes[2], actual[2]);
        assertEquals("error", sizes[3], actual[3]);

    }
*/
}

package com.zyd.core;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.zyd.ATestUtil;
import com.zyd.Constants;
import com.zyd.core.busi.LinkStore;
import com.zyd.core.dom.Link;

public class TestLinkStore extends TestCase {
    String url = "http://www.aaa.com/href/";
    ArrayList<String> links = new ArrayList<String>();
    int totalLinkSize = 1000;

    @Override
    protected void setUp() throws Exception {
        ATestUtil.setUpSpring();
        for (int i = 0; i < totalLinkSize; i++) {
            links.add(url + "_link_" + i);
        }
    }

    public void testCreateLink() throws Exception {
        LinkStore store = new LinkStore(Utils.getShortestDomain(url));
        store.cleanAll();
        for (String l : links) {
            store.addLink(l);
        }
        checkSize(new int[] { 0, totalLinkSize, 0, 0 }, store.getAllQueueSize());

        store.clearCache();
        store.loadFromDb();
        checkSize(store.getAllQueueSize(), new int[] { 0, totalLinkSize, 0, 0 });
    }

    public void testProcessingLink() throws Exception {
        LinkStore store = new LinkStore(Utils.getShortestDomain(url));
        store.cleanAll();
        for (String l : links) {
            store.addLink(l);
        }

        for (int i = 0; i < 100; i++) {
            assertNotNull(store.next());
        }

        checkSize(new int[] { 100, totalLinkSize - 100, 0, 0 }, store.getAllQueueSize());

        // make sure if processing links is canceled in the middle, it will still be in the waiting queue next time loaded
        store.clearCache();
        store.loadFromDb();

        checkSize(store.getAllQueueSize(), new int[] { 0, 1000, 0, 0 });

        // make sure if link is processed, or error it's store correctly        
        ArrayList<Link> processing = new ArrayList<Link>(), error = new ArrayList<Link>();
        for (int i = 0; i < 100; i++) {
            processing.add(store.next());
            error.add(store.next());
        }
        int oldTry = Constants.LINK_MAX_TRY;
        Constants.LINK_MAX_TRY = 1;
        for (Link link : error) {
            store.linkError(link.url, "error");
        }
        Constants.LINK_MAX_TRY = oldTry;
        for (Link link : processing) {
            store.linkFinished(link.url);
        }
        checkSize(new int[] { 0, 800, 100, 100 }, store.getAllQueueSize());

        store.clearCache();
        store.loadFromDb();
        checkSize(new int[] { 0, 800, 100, 100 }, store.getAllQueueSize());
    }

    public void testLinkError() {
        LinkStore store = new LinkStore(Utils.getShortestDomain(url));
        store.cleanAll();
        for (String l : links) {
            store.addLink(l);
        }

        ArrayList<Link> error = new ArrayList<Link>();
        for (int i = 0; i < 1000; i++) {
            error.add(store.next());
        }

        int oldTry = Constants.LINK_MAX_TRY;
        Constants.LINK_MAX_TRY = 2;
        for (Link link : error) {
            store.linkError(link.url, "error");
        }
        checkSize(new int[] { 0, 1000, 0, 0 }, store.getAllQueueSize());
        store.clearCache();
        store.loadFromDb();
        checkSize(new int[] { 0, 1000, 0, 0 }, store.getAllQueueSize());

        error.clear();
        for (int i = 0; i < 1000; i++) {
            Link link = store.next();
            assertNotNull(link);
            assertEquals(2, link.tryCount);
            error.add(link);
        }
        for (Link link : error) {
            store.linkError(link.url, "error");
        }
        checkSize(new int[] { 0, 0, 0, 1000 }, store.getAllQueueSize());
        Constants.LINK_MAX_TRY = oldTry;

        store.clearCache();
        store.loadFromDb();
        checkSize(new int[] { 0, 0, 0, 1000 }, store.getAllQueueSize());
    }

    private void checkSize(int[] sizes, int[] actual) {
        assertEquals(sizes.length, actual.length);
        assertEquals("processing", sizes[0], actual[0]);
        assertEquals("waiting", sizes[1], actual[1]);
        assertEquals("processed", sizes[2], actual[2]);
        assertEquals("error", sizes[3], actual[3]);

    }
}

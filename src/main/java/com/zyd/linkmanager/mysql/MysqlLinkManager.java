package com.zyd.linkmanager.mysql;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.zyd.Constants;
import com.zyd.linkmanager.Link;
import com.zyd.linkmanager.LinkManager;

public class MysqlLinkManager implements LinkManager {
    private static Logger logger = Logger.getLogger(MysqlLinkManager.class);
    /* link store string uid -> linkstore */
    private final HashMap<String, LinkStore> storeMap = new HashMap<String, LinkStore>(100);
    private final HashMap<String, Link> processingLinkMap = new HashMap<String, Link>();
    private final ArrayList<LinkStore> storeList = new ArrayList<LinkStore>();
    private long lastExecuteTime;
    private boolean hasMore = true;
    /* how many are processed so far */
    private long processedLinkCount = 0;
    /* how many are added so far */
    private long addedLinkCount = 0;

    public MysqlLinkManager() {
        lastExecuteTime = System.currentTimeMillis();
    }

    /**
     * returns null if this link is already managed.
     * TODO: synchronization can be moved further down to store level.
     */
    public synchronized Link addLink(String url) {
        String storeUid = LinkTableMapper.mapUrl(url);
        LinkStore store = storeMap.get(storeUid);
        hasMore = true;
        if (store == null) {
            store = createStore(storeUid, true);
        }

        if (store.isLinkManaged(url) == true) {
            return null;
        } else {
            Link r = store.addLink(url);
            if (r != null) {
                hasMore = true;
                addedLinkCount++;
            }
            return r;
        }
    }

    public synchronized Link roundRobinNextLink() {
        for (int i = 0, storeSize = storeList.size(); i < storeSize; i++) {
            LinkStore store = storeList.get((lastStoreIndex + i) % storeSize);
            Link link = store.nextUnprocessedLink();
            if (link != null) {
                lastStoreIndex = (lastStoreIndex + i + 1) % storeSize;
                processingLinkMap.put(link.getUrl(), link);
                link.processStartTime = System.currentTimeMillis();
                return link;
            }
        }
        hasMore = false;
        return null;
    }

    public Link getProcessingLink(String url) {
        return processingLinkMap.get(url);
    }

    public Link linkFinished(String url) {
        return linkFinished(url, Link.STATE_FINISHED_OK, null);
    }

    public Link linkFinishedError(String url, int state, String msg) {
        return linkFinished(url, state, msg);
    }

    private synchronized Link linkFinished(String url, int state, String msg) {
        processedLinkCount++;
        Link link = processingLinkMap.get(url);
        if (link == null) {
            return null;
        }
        String storeUid = LinkTableMapper.mapUrl(url);
        LinkStore store = storeMap.get(storeUid);
        link.setState(state);
        link.setFinishTime(new Date());
        store.LinkFinished(link);
        processingLinkMap.remove(url);
        return link;
    }

    public int cleanExpiredProcessingLink() {
        if (processingLinkMap.size() == 0)
            return 0;
        int counter = 0;
        long now = System.currentTimeMillis();
        // make a copy, not synchronized
        ArrayList<Link> links = new ArrayList<Link>(processingLinkMap.values());
        for (Link link : links) {
            if (now - link.processStartTime > Constants.LINK_PROCESSING_EXPIRE) {
                linkFinishedError(link.getUrl(), Link.STATE_FINISHED_TIME_OUT, null);
                System.out.println(link.getUrl());
                counter++;
            }
        }
        logger.info("Cleaned expried processing link:" + counter);
        return counter;
    }

    public synchronized int cleanExpiredLinkStore() {
        int counter = 0;
        for (int i = storeList.size() - 1; i > -1; i--) {
            LinkStore store = storeList.get(i);
            if (store.isInActiveForTooLong() == true) {
                storeList.remove(i);
                storeMap.remove(store.getStringUid());
                counter++;
            }
        }
        logger.info("Cleaned inactive store : " + counter);
        return counter;
    }

    private int lastStoreIndex = 0;

    private LinkStore createStore(String storeUid, boolean createTableIfNotAlreadyThere) {
        LinkStore store = null;
        // check to see if the table exist
        LinkTableInfo info = DbHelper.getLinkTableInfoByUid(storeUid);
        if (info == null) {
            if (createTableIfNotAlreadyThere == true) {
                info = DbHelper.createLinkTable(storeUid);
            } else {
                return null;
            }
        }
        store = new LinkStore(info.getTableName(), storeUid);
        storeMap.put(storeUid, store);
        storeList.add(store);
        // only runs once when store is first initialized
        store.resetProcessingLinkToUnprocessed();
        // there may be unprocessed links in store.
        hasMore = true;
        return store;
    }

    public void doJob() {
        cleanExpiredProcessingLink();
        cleanExpiredLinkStore();
        lastExecuteTime = System.currentTimeMillis();
    }

    public boolean shouldRun() {        
        return (System.currentTimeMillis() - lastExecuteTime) > Constants.LINK_MONITOR_SCAN_INTERVAL;
    }

    public Link getLink(String url) {
        String storeUid = LinkTableMapper.mapUrl(url);
        LinkStore store = createStore(storeUid, false);
        if (store == null)
            return null;
        return store.getLinkByUrl(url);
    }

    public int getSuggestedLinkRefreshInterval() {
        if (hasMore) {
            return 1;
        } else {
            return 30;
        }
    }

    public void clearAllCache() {
        storeMap.clear();
        storeList.clear();
        lastStoreIndex = 0;
        processingLinkMap.clear();
    }

    public String linkSnapShot() {
        int total = 0;
        for (int i = storeList.size() - 1; i > -1; i--) {
            total += storeList.get(i).getCachedSize();
        }
        StringBuffer buf = new StringBuffer();
        buf.append("Processing Link : " + processingLinkMap.size());
        buf.append("\n");
        buf.append("Cached Link : " + total);
        buf.append("\n");
        buf.append("Processed Link :" + processedLinkCount);
        buf.append("\n");
        buf.append("Added Link :" + addedLinkCount);
        buf.append("\n");
        buf.append("Active Store  : " + storeList.size());
        buf.append("\n");
        return buf.toString();
    }
}

package com.zyd.core.busi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.zyd.Constants;
import com.zyd.core.Utils;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.Client;
import com.zyd.core.dom.Link;

public class LinkManager {
    public final static Link IdlePageUrl = new Link(Constants.IdlePageUrl);
    /* How soon should the client refresh it self, based on the current size of waiting list.*/
    private int suggestedLinkRefreshInterval = 20;
    private static Logger logger = Logger.getLogger(LinkManager.class);
    private HashMap<String, LinkStore> linkStoreMap;
    private ArrayList<LinkStore> linkStoreList;
    private int storeSize = 0;
    private LinkMonitorThread monitor;

    public LinkManager() {
        linkStoreMap = new HashMap<String, LinkStore>();
        linkStoreList = new ArrayList<LinkStore>();
    }

    public Link add(String link) {
        String domain = Utils.getShortestDomain(link);
        if (domain == null) {
            logger.warn("Can not add link, can not get domain name :" + link);
            return null;
        }
        LinkStore store = linkStoreMap.get(domain);
        if (store == null) {
            store = new LinkStore(domain);
            linkStoreMap.put(domain, store);
            linkStoreList.add(store);
            storeSize++;
        }
        return store.addLink(link);
    }

    private int lastStoreIndex = 0;
    private int counter = 0;

    public Link next(Client client) {
        if (counter++ % 8 == 0) {
            updateSuggestedRefreshInterval();
            if (shouldCheckLinkList()) {
                return nextWatchedLink();
            }
        }
        // starting from lastStoreIndex, check to see if any have links to crawl. if do , return it.
        synchronized (this) {
            for (int i = 0; i < storeSize; i++) {
                LinkStore store = linkStoreList.get((lastStoreIndex + i) % storeSize);
                Link link = store.next();
                if (link != null) {
                    lastStoreIndex = (lastStoreIndex + i + 1) % storeSize;
                    return link;
                }
            }
        }
        // return next watched link
        return nextWatchedLink();
    }

    public Link next() {
        return next(null);
    }

    public Link linkFinished(String link) {
        LinkStore store = getLinkStoreByUrl(link, true);
        if (store == null)
            return null;
        return store.linkFinished(link);
    }

    public Link linkError(String link, String msg) {
        LinkStore store = getLinkStoreByUrl(link, true);
        if (store == null)
            return null;
        return store.linkError(link, msg);
    }

    public int getSuggestedLinkRefreshInterval() {
        return suggestedLinkRefreshInterval;
    }

    private int lastCrawlerRefreshInterval = 0;

    private void updateSuggestedRefreshInterval() {
        int n = 0;
        for (int i = 0; i < storeSize; i++) {
            n = n + linkStoreList.get(i).getWaitingSize();
        }

        if (n > 50) {
            suggestedLinkRefreshInterval = 2;
        } else if (n > 20) {
            suggestedLinkRefreshInterval = 20;
        } else if (n > 5) {
            suggestedLinkRefreshInterval = 30;
        } else {
            suggestedLinkRefreshInterval = 60;
        }
        if (suggestedLinkRefreshInterval != lastCrawlerRefreshInterval) {
            logger.info("Updated suggestedLinkRefreshInterval from " + lastCrawlerRefreshInterval + " to " + suggestedLinkRefreshInterval + ", current size of wating list " + n);
            lastCrawlerRefreshInterval = suggestedLinkRefreshInterval;
        }

    }

    public Link getProcessingLink(String link) {
        LinkStore store = getLinkStoreByUrl(link, true);
        if (store == null) {
            return null;
        }
        return store.getProcessingLink(link);
    }

    private LinkStore getLinkStoreByUrl(String link, boolean shouldWarn) {
        String domain = Utils.getShortestDomain(link);
        if (domain == null) {
            logger.warn("Invalid link, can not get domain name :" + link);
            return null;
        }
        LinkStore store = linkStoreMap.get(domain);
        if (store == null) {
            if (shouldWarn) {
                logger.warn("Check this: Invalid link, domain is not in list :" + link);
            }
            return null;
        }
        return store;
    }

    private int lastWatchedLinkIndex = -1;

    private Link nextWatchedLink() {
        if (Constants.WATCH_LIST.length == 0) {
            return IdlePageUrl;
        }
        synchronized (this) {
            lastWatchedLinkIndex = (lastWatchedLinkIndex + 1) % Constants.WATCH_LIST.length;
            return Constants.WATCH_LIST[lastWatchedLinkIndex];
        }
    }

    private long lastLinkListCheckTime = new Date().getTime();

    private boolean shouldCheckLinkList() {
        Date now = new Date();
        if (now.getTime() - lastLinkListCheckTime > Constants.INTERVAL_CHECK_LINK_LIST) {
            lastLinkListCheckTime = now.getTime();
            return true;
        }
        return false;
    }

    public LinkMonitorThread getLinkMonitorThread() {
        return monitor;
    }

    public void startMonitor() {
        if (monitor == null)
            monitor = new LinkMonitorThread();
        monitor.startMonitor();
    }

    public void stopMonitor() {
        monitor.stopMoniter();
    }

    public void loadFromDb() {
        for (LinkStore store : linkStoreList) {
            store.loadFromDb();
        }
    }

    public void clearCache() {
        for (LinkStore store : linkStoreList) {
            store.clearCache();
        }
    }

    public void cleanAll() {
        clearCache();
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        session.createQuery("delete from Link").executeUpdate();
        tx.commit();
    }

    public int[] getAllQueueSize() {
        int[] sum = new int[] { 0, 0, 0, 0 };
        for (LinkStore store : linkStoreList) {
            int sizes[] = store.getAllQueueSize();
            sum[0] += sizes[0];
            sum[1] += sizes[1];
            sum[2] += sizes[2];
            sum[3] += sizes[3];
        }
        return sum;
    }

    public String snapshot() {
        int[] sum = getAllQueueSize();
        StringBuffer buf = new StringBuffer();
        buf.append("suggestedRefreshTime :" + suggestedLinkRefreshInterval);
        buf.append("\n");
        buf.append("waiting    :" + sum[1]);
        buf.append("\n");
        buf.append("processing :" + sum[0]);
        buf.append("\n");
        buf.append("processed  :" + sum[2]);
        buf.append("\n");
        buf.append("error      :" + sum[3]);
        return buf.toString();
    }

    /**
     * Link monitor will do several things:
     * ## every Constants.LINK_MONITOR_SLEEP, it will check all the links that is being processed, 
     *    if any link has been processed for long than Constants.LINK_PROCESSING_EXPIRE, it will 
     *    mark this link as error.
     *    
     * ## every Constants.LINK_FLUSH_CYCLE_LENGTH, it will release all the processed and error links that is older than Constants.LINK_LOAD_BEFORE.
     *
     */
    class LinkMonitorThread extends Thread {
        private long lastFlushOldProssedAndErrorTime, lastCleanOldProcessingLinkTime;

        public LinkMonitorThread() {
            super("LinkMonitorThread - " + (new Date()).toString());
            Date now = new Date();
            lastFlushOldProssedAndErrorTime = now.getTime();
            lastCleanOldProcessingLinkTime = now.getTime();
        }

        private boolean shouldStop;

        public void startMonitor() {
            shouldStop = false;
            this.start();
        }

        public void stopMoniter() {
            shouldStop = true;
            this.interrupt();
        }

        private void clean() {
            long now = new Date().getTime();
            if (now - lastCleanOldProcessingLinkTime > Constants.LINK_PROCESSING_EXPIRE) {
                cleanOutdatedProcessingLink();
            }
            if (now - lastFlushOldProssedAndErrorTime > Constants.LINK_FLUSH_CYCLE_LENGTH) {
                flushOldProssedAndErrorLinks();
            }
        }

        private void flushOldProssedAndErrorLinks() {
            for (LinkStore store : linkStoreList) {
                try {
                    store.purgeProcessedAndError();
                } catch (Exception e) {
                    logger.error("Error while purgeProcessedAndError for LinkStore " + store, e);
                }
            }
        }

        private void cleanOutdatedProcessingLink() {
            for (LinkStore store : linkStoreList) {
                try {
                    store.cleanExpiredProcessing();
                } catch (Exception e) {
                    logger.error("Error while cleanExpiredProcessing for LinkStore " + store, e);
                }
            }
        }

        @Override
        public void run() {
            logger.info("Link manager started");
            while (shouldStop == false) {
                try {
                    try {
                        Thread.sleep(Constants.LINK_MONITOR_SLEEP);
                    } catch (InterruptedException e) {
                    }
                    clean();
                } catch (Exception e) {
                    logger.error("Error while running link manager looping taskes ");
                    logger.error(e.toString());
                }
            }
            logger.info("Link manager stopped");
        }
    }

}

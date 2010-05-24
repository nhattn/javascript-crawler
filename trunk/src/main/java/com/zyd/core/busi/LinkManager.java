package com.zyd.core.busi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.tj.common.CommonUtil;
import com.zyd.Constants;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.Link;

@SuppressWarnings("unchecked")
public class LinkManager {

    private HashMap<String, Link> waiting = new HashMap<String, Link>();
    private HashMap<String, Link> processed = new HashMap<String, Link>();
    private HashMap<String, Link> processing = new HashMap<String, Link>();
    private HashMap<String, Link> error = new HashMap<String, Link>();

    /**
     * How soon should the client refresh it self, based on the current size of waiting list.
     */
    private int suggestedLinkRefreshTime = 20;

    public int getSuggestedLinkRefreshTime() {
        return suggestedLinkRefreshTime;
    }

    public final static Link IdlePageUrl = new Link(Constants.IdlePageUrl);

    private LinkMonitorThread monitor;

    private LinkManager() {
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

    public synchronized Link addLink(String link) {
        if (isLinkManaged(link)) {
            return null;
        }

        Link l = new Link(link);
        l.createTime = new Date();
        saveOrUpdateLink(l, false);
        waiting.put(link, l);
        return l;
    }

    public boolean isLinkManaged(String link) {
        if (processed.containsKey(link) || processing.containsKey(link) || waiting.containsKey(link) || error.containsKey(link)) {
            return true;
        }
        return false;
    }

    /**
     * is this link waiting to be processed
     * @return
     */
    public boolean isLinkProcessing(String link) {
        return processing.containsKey(link);
    }

    public synchronized Link nextLink() {
        if (waiting.size() > 0) {
            String url = waiting.keySet().iterator().next();
            Link link = waiting.remove(url);
            link.startTime = new Date();
            processing.put(url, link);
            return link;
        }

        if (error.size() > 0) {
            // should not try too much of error links
            String url = error.keySet().iterator().next();
            Link link = error.remove(url);
            link.startTime = new Date();
            processing.put(url, link);
            return link;
        }
        return nextWatchedLink();
    }

    public synchronized Link linkError(String url, String msg) {
        Link link = processing.get(url);
        if (link == null) {
            //TODO: should pay special attention in production. Link is in finished but not in our queue, somebody hacking.            
            return null;
        }
        link.tryCount++;
        processing.remove(url);
        link.isError = 1;
        link.startTime = null;
        if (link.tryCount < Constants.LINK_MAX_TRY) {
            // ok, keep trying                     
            error.put(url, link);
        } else {
            // tried to much, giving up            
            link.processTime = new Date();
            link.errorMsg = msg;
            processed.put(url, link);
        }
        saveOrUpdateLink(link, true);
        return link;
    }

    public synchronized Link linkFinished(String url) {
        Link link = processing.get(url);
        if (link == null) {
            //TODO: should pay special attention in production. Link is in finished but not in our queue, somebody hacking. 
            return null;
        }
        link.tryCount++;
        link.processTime = new Date();
        link.startTime = null;
        link.isError = 0;
        saveOrUpdateLink(link, true);
        processing.remove(url);
        processed.put(url, link);
        return link;
    }

    /**
     * links without processTime is waiting to be processed.
     * otherwise processed.
     */
    public synchronized void loadFromDb() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        System.out.println("Going to load links from database :");
        List list = session.createQuery("from Link as link where link.createTime > ? and link.tryCount < ? and link.isError<>1 order by link.id desc").setDate(0,
                CommonUtil.msecefore(Constants.LINK_LOAD_BEFORE)).setInteger(1, Constants.LINK_MAX_TRY).list();
        for (int i = 0, len = list.size(); i < len; i++) {
            Link link = (Link) list.get(i);
            if (link.processTime == null) {
                waiting.put(link.url, link);
            } else {
                processed.put(link.url, link);
            }
        }
        tx.commit();
        System.out.println(snapshot());
    }

    private void saveOrUpdateLink(Link link, boolean isUpdate) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        if (isUpdate)
            session.update(link);
        else
            session.save(link);
        tx.commit();
    }

    public List<String> getAllLinks() {
        return new ArrayList(processed.values());
    }

    public void deleteAllLinks() {
        final String deleteAll = "delete from Link";
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        int r = session.createQuery(deleteAll).executeUpdate();
        session.getTransaction().commit();
        clearCache();
    }

    public void clearCache() {
        waiting.clear();
        processed.clear();
        processing.clear();
        error.clear();
    }

    public Collection getWaiting() {
        return waiting.values();
    }

    public Collection getProcessed() {
        return processed.values();
    }

    public Collection getProcessing() {
        return processing.values();
    }

    public Collection getError() {
        return error.values();
    }

    private int watchedLinkIndex = -1;

    private Link nextWatchedLink() {
        if (Constants.WATCH_LIST.length == 0) {
            return IdlePageUrl;
        }
        synchronized (this) {
            watchedLinkIndex = (watchedLinkIndex + 1) % Constants.WATCH_LIST.length;
            return Constants.WATCH_LIST[watchedLinkIndex];
        }
    }

    public String snapshot() {
        StringBuffer buf = new StringBuffer();
        buf.append("suggestedRefreshTime :" + suggestedLinkRefreshTime);
        buf.append("\n");
        buf.append("waiting    :" + waiting.size());
        buf.append("\n");
        buf.append("processing :" + processing.size());
        buf.append("\n");
        buf.append("processed  :" + processed.size());
        buf.append("\n");
        buf.append("error      :" + error.size());
        return buf.toString();
    }

    /**
     * Link monitor will do several things:
     * ## every Constants.LINK_MONITOR_SLEEP, it will check all the links that is being processed, 
     *    if any link has been processed for long than Constants.LINK_PROCESSING_EXPIRE, it will 
     *    mark this link as error.
     *    
     * ## every Constants.LINK_LOAD_BEFORE * 0.2, it will go through all the links, then release
     *    the links that is older than Constants.LINK_LOAD_BEFORE.
     *
     */
    class LinkMonitorThread extends Thread {
        private long lastLinkFlushTime;
        private LinkedList<Integer> waitingQueueSizeHistory;
        private int lastCrawlerRefreshRage;

        public LinkMonitorThread() {
            super("LinkMonitorThread - " + (new Date()).toString());
            lastLinkFlushTime = new Date().getTime();
            waitingQueueSizeHistory = new LinkedList<Integer>();
            lastCrawlerRefreshRage = suggestedLinkRefreshTime;
        }

        private boolean shouldStop;

        public void startMonitor() {
            shouldStop = false;
            //TODO: potential bug, can be started twice
            this.start();
        }

        public void stopMoniter() {
            shouldStop = true;
            this.interrupt();
        }

        private void clean() {
            cleanOutdatedProcessingLink();
            flushOldProssedLinks();
            updateSuggestedRefreshRate();
        }

        private void updateSuggestedRefreshRate() {
            int n = waiting.size();
            waitingQueueSizeHistory.offerFirst(n);
            if (waitingQueueSizeHistory.size() > 4) {
                waitingQueueSizeHistory.pollLast();
            }
            if (n > 1000) {
                suggestedLinkRefreshTime = 2;
            } else if (n > 800) {
                suggestedLinkRefreshTime = 4;
            } else if (n > 500) {
                suggestedLinkRefreshTime = 5;
            } else if (n > 300) {
                suggestedLinkRefreshTime = 10;
            } else if (n > 50) {
                suggestedLinkRefreshTime = 20;
            } else {
                if (suggestedLinkRefreshTime < 30)
                    suggestedLinkRefreshTime = 30;
                for (int i = 0; i < waitingQueueSizeHistory.size(); i++) {
                    if (waitingQueueSizeHistory.get(i) >= 30)
                        return;
                }
                suggestedLinkRefreshTime += 5;
                if (suggestedLinkRefreshTime > 60) {
                    suggestedLinkRefreshTime = 60;
                }
            }
            if (suggestedLinkRefreshTime != lastCrawlerRefreshRage) {
                System.err.println("Updated suggestedLinkRefreshTime from " + lastCrawlerRefreshRage + " to " + suggestedLinkRefreshTime + ", current size of wating list " + n);
                lastCrawlerRefreshRage = suggestedLinkRefreshTime;
            }
        }

        private void flushOldProssedLinks() {
            long now = new Date().getTime();
            if (now - lastLinkFlushTime > Constants.LINK_FLUSH_CYCLE_LENGTH) {
                //                System.err.println("Will start flushing old processed links");
                int processedCount = 0;
                HashMap<String, Link> p;

                synchronized (processed) {
                    p = (HashMap<String, Link>) processed.clone();
                }
                processedCount = purgeExpiredLinks(p, now);
                synchronized (processed) {
                    processed.clear();
                    processed = p;
                }
                p = null;
                System.err.println("Flushed " + processedCount + " old processed link.");
            }
        }

        private void cleanOutdatedProcessingLink() {
            //            System.err.println("Start cleaning outdated processing link");
            long now = new Date().getTime();
            int count = 0;
            if (processing.size() != 0) {
                HashMap<String, Link> ps;
                synchronized (processing) {
                    ps = (HashMap<String, Link>) processing.clone();
                }
                for (Link link : ps.values()) {
                    Date start = link.startTime;
                    if (start == null) {
                        continue;
                    }
                    if (now - start.getTime() > Constants.LINK_PROCESSING_EXPIRE) {
                        count++;
                        linkError(link.url, "Url has been processed for too long, expired. First started on " + start.toString());
                    }
                }
            }
            System.err.println("End cleaning outdated processing link, cleaned " + count + " links.");

        }

        private int purgeExpiredLinks(HashMap<String, Link> p, long now) {
            int count = 0;
            ArrayList<Link> links = new ArrayList<Link>(p.values());
            for (int i = links.size() - 1; i > -1; i--) {
                Link link = links.get(i);
                Date time = link.processTime;
                if (now - time.getTime() > Constants.LINK_LOAD_BEFORE) {
                    p.remove(link.url);
                    count++;
                }
            }
            return count;
        }

        @Override
        public void run() {
            System.err.println("Link manager started");
            while (shouldStop == false) {
                try {
                    try {
                        Thread.sleep(Constants.LINK_MONITOR_SLEEP);
                    } catch (InterruptedException e) {
                    }
                    clean();
                } catch (Exception e) {
                    System.err.println(e.toString());
                    e.printStackTrace();
                }
            }
            System.err.println("Link manager stopped");
        }
    }
}

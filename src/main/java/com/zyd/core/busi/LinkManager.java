package com.zyd.core.busi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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

    public final static Link IdlePageUrl = new Link(Constants.IdlePageUrl);

    private LinkManager() {
    }

    public synchronized Link addLink(String link) {
        if (isLinkManaged(link)) {
            return null;
        }
        Link l = new Link(link);
        l.createTime = new Date();
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
            link.tryCount++;
            return link;
        }

        if (error.size() > 0) {
            String url = error.keySet().iterator().next();
            Link link = error.remove(url);
            link.startTime = new Date();
            processing.put(url, link);
            link.tryCount++;
            return link;
        }
        return nextWatchedLink();
    }

    public synchronized Link linkError(String url, String msg) {
        Link link = processing.get(url);
        if (link == null) {
            System.err.println("Link error, but link is not in the processing queue, this should not happen");
            return null;
        }
        processing.remove(url);
        error.put(url, link);
        return link;
    }

    public synchronized Link linkFinished(String url) {
        Link link = processing.get(url);
        if (link == null) {
            System.err.println("Link finished, but link is not in the processing queue, this should not happen");
            return null;
        }
        processing.remove(url);
        processed.put(url, link);
        link.processTime = new Date();
        return link;
    }

    /**
     * links without processTime is waiting to be processed.
     * otherwise processed.
     */
    public synchronized void loadFromDb() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        System.out.println("Going to load links from database:");
        List list = session.createQuery("from Link as link where link.createTime > ? order by link.id desc").setDate(0, CommonUtil.daysBefore(5)).list();
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

    /*
     * waiting & processing is treated as waiting
     */
    public synchronized void storeToDb() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        int counter = 0;
        HashMap[] values = new HashMap[] { waiting, processing, processed, error };
        System.err.println("Going to store links to database: ");
        System.err.println(snapshot());
        for (int j = 0; j < values.length; j++) {
            Collection links = values[j].values();
            for (Object o : links) {
                session.saveOrUpdate(o);
                if (++counter % 20 == 0) {
                    session.flush();
                    session.clear();
                }
            }
        }
        tx.commit();
        System.err.println("Successfully stored links to database");
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
        buf.append("Waiting    :" + waiting.size());
        buf.append("\n");
        buf.append("Processing :" + processing.size());
        buf.append("\n");
        buf.append("processed  :" + processed.size());
        buf.append("\n");
        buf.append("Error      :" + error.size());
        return buf.toString();
    }
}

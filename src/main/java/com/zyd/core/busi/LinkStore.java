package com.zyd.core.busi;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;

import com.tj.common.CommonUtil;
import com.zyd.Constants;
import com.zyd.core.Utils;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.Link;

public class LinkStore {
    private static Logger logger = Logger.getLogger(LinkStore.class);
    HashSet<String> processed;
    HashMap<String, Link> processing;
    HashSet<String> error;
    HashMap<String, Link> waiting;
    private String domain;

    public LinkStore(String domain) {
        this.domain = domain;
        processed = new HashSet<String>();
        error = new HashSet<String>();
        processing = new HashMap<String, Link>();
        waiting = new HashMap<String, Link>();
        loadFromDb();
    }

    public synchronized boolean shouldManage(String url) {
        return domain.equals(Utils.getShortestDomain(url));
    }

    private boolean isLinkManaged(String url) {
        if (processing.containsKey(url) || waiting.containsKey(url)) {
            return true;
        }
        String hash = Utils.stringHash(url);
        if (processed.contains(hash) || error.contains(hash)) {
            return true;
        } else {
            return false;
        }
    }

    public synchronized Link addLink(String url) {
        if (isLinkManaged(url) == true) {
            return null;
        }
        String hash = Utils.stringHash(url);
        Link link = new Link(url, new Date(), hash);
        saveOrUpdateLink(link, false);
        waiting.put(url, link);
        return link;
    }

    public synchronized Link next() {
        Link r = null;
        if (waiting.size() > 0) {
            String key = waiting.keySet().iterator().next();
            r = waiting.get(key);
            waiting.remove(key);
        }
        if (r != null) {
            processing.put(r.url, r);
            r.startTime = new Date();
            r.tryCount++;
        }
        return r;
    }

    public synchronized Link linkFinished(String url) {
        Link link = processing.get(url);
        if (link == null) {
            logger.warn("Check this: link is finished by not in our processing queue" + url);
            return null;
        }
        link.processTime = new Date();
        saveOrUpdateLink(link, true);
        processing.remove(link.url);
        processed.add(link.hash);
        link.cleanup();
        link = null;
        return link;
    }

    public Link getProcessingLink(String link) {
        return processing.get(link);
    }

    public synchronized Link linkError(String url, String msg) {
        Link link = processing.get(url);
        if (link == null) {
            logger.warn("Check this: link is finished and has an error by not in our processing queue" + url);
            return null;
        }

        if (link.tryCount >= Constants.LINK_MAX_TRY) {
            // has tried too many times, will give up
            link.errorMsg = msg;
            link.isError = 1;
            link.processTime = new Date();
            saveOrUpdateLink(link, true);
            processing.remove(link.url);
            error.add(link.hash);
            link.cleanup();
            link = null;
            return link;
        } else {
            // will keep trying
            link.errorMsg = msg;
            saveOrUpdateLink(link, true);
            link.startTime = null;
            // moving back to waiting queue            
            processing.remove(url);
            waiting.put(url, link);
        }
        return link;
    }

    /**
     * check all the links that is in the processing queue, 
     * if it's been processed for long than Constants.LINK_PROCESSING_EXPIRE, then it will remove this link from processing queue and think it as an error.
     */
    public void cleanExpiredProcessing() {
        HashMap<String, Link> ps;
        long now = new Date().getTime();
        int count = 0;
        synchronized (this) {
            ps = (HashMap<String, Link>) processing.clone();
        }
        for (Link link : ps.values()) {
            Date start = link.startTime;
            if (start == null) {
                continue;
            }
            if (now - start.getTime() > Constants.LINK_PROCESSING_EXPIRE) {
                count++;
                linkError(link.url, "Expired, started on " + start.toString());
            }
        }
        if (count > 0) {
            logger.info("LinkStore[" + domain + "] cleaned " + count + " expired link");
        }
    }

    /**
     * For every Constant.LINK_FLUSH_CYCLE_LENGTH, links in the error and processed queue will be all purged out, and reloaded. 
     * The reloaded dataset contains links that is youger than Constants.LINK_LOAD_BEFORE.  
     * reloaded because we don't keep a time stamp for we the link is created, it is stored in db only.
     */
    public void purgeProcessedAndError() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        final HashSet<String> error = new HashSet<String>(), processed = new HashSet<String>();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                logger.info("[LinkStore, domain=" + domain + "] going to reload links from database.");
                java.sql.PreparedStatement stmt = connection.prepareStatement("select hash, isError from Link where createTime > ?");
                stmt.setTimestamp(1, new java.sql.Timestamp(CommonUtil.msecefore(Constants.LINK_LOAD_BEFORE).getTime()));
                ResultSet r = stmt.executeQuery();
                while (r.next()) {
                    if (r.getInt(2) == 0) {
                        processed.add(r.getString(1));
                    } else {
                        error.add(r.getString(1));
                    }
                }
            }
        });
        tx.commit();
        synchronized (this) {
            this.error.clear();
            this.error = error;
            this.processed.clear();
            this.processed = processed;
        }
        logger.info(toString());
    }

    public int getWaitingSize() {
        return waiting.size();
    }

    @Override
    public String toString() {
        int[] sizes = getAllQueueSize();
        StringBuffer buf = new StringBuffer();
        buf.append("[LinkStore, domain=");
        buf.append(domain);
        buf.append(", processing=" + sizes[0]);
        buf.append(", waiting=" + sizes[1]);
        buf.append(", processed=" + sizes[2]);
        buf.append(", error=" + sizes[3]);
        buf.append("]");
        return buf.toString();
    }

    /**
     * 
     * @return the sizes of processing, waiting, processed, error
     */
    public int[] getAllQueueSize() {
        int[] r = new int[4];
        r[0] = processing.size();
        r[1] = waiting.size();
        r[2] = processed.size();
        r[3] = error.size();
        return r;
    }

    public void clearCache() {
        processed.clear();
        waiting.clear();
        processing.clear();
        error.clear();
    }

    public void cleanAll() {
        clearCache();
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        session.createQuery("delete from Link as link where link.url like ?").setString(0, "%" + domain + "%").executeUpdate();
        tx.commit();
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

    public void loadFromDb() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        logger.info("Going to load links from database for LinkStore - " + domain);
        // must have the two like here to previous domains with common suffix/prefix  domain.com, adomain.com, domain.com.cn
        //TODO: this has to be better, how to deal with subdomains , need a different model.
        List list = session.createQuery("from Link as link where link.createTime < ? and (link.url like ? or link.url like ?) order by link.id desc").setDate(0,
                CommonUtil.msecefore(Constants.LINK_LOAD_BEFORE)).setString(1, "%." + domain + "/%").setString(2, "%/" + domain + "/%").list();
        tx.commit();
        for (int i = 0, len = list.size(); i < len; i++) {
            Link link = (Link) list.get(i);
            if (link.isError == 1) {
                error.add(link.hash);
                link.cleanup();
            } else if (link.processTime == null) {
                waiting.put(link.url, link);
            } else {
                processed.add(link.hash);
                link.cleanup();
            }
        }
        logger.info(toString());
    }
}

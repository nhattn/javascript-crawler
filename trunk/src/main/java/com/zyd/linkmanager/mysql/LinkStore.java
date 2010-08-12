package com.zyd.linkmanager.mysql;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.zyd.Constants;
import com.zyd.linkmanager.Link;

public class LinkStore {
    Logger logger = org.apache.log4j.Logger.getLogger(LinkStore.class);
    private String tableName;
    private String tableStringUid;
    private ArrayList<Link> cachedUnprocessedLink = new ArrayList<Link>();
    public boolean hasMoreUnprocessed = true;
    private long lastAccessTime = System.currentTimeMillis();

    /**
     * @param tableName name of the table in db, in the format of LinkTable_xxx
     * @param tableUid string uid used uniquely identify the table.
     */
    public LinkStore(String tableName, String tableUid) {
        this.tableName = tableName;
        this.tableStringUid = tableUid;
    }

    public Link addLink(String url) {
        if (url == null || url.length() > Constants.URL_MAX_LENGTH) {
            logger.warn("Link to long or is null, ignored : " + url);
            return null;
        }

        Link link = DbHelper.addNewLinkToTable(tableName, url);
        if (cachedUnprocessedLink.size() < 100) {
            cachedUnprocessedLink.add(link);
        }
        hasMoreUnprocessed = true;
        return link;
    }

    public boolean isLinkManaged(String url) {
        return DbHelper.containsLink(url, tableName);
    }

    /**
     * link must have an id, state marked as finished or error, and finish time.
     * @param link
     */
    public void LinkFinished(Link link) {
        DbHelper.updateLinkStatus(link.getId(), link.getState(), link.getFinishTime(), tableName);
    }

    public Link getLinkByUrl(String url) {
        return DbHelper.getLinkByUrl(url, tableName);
    }

    public Link nextUnprocessedLink() {
        if (cachedUnprocessedLink.size() == 0) {
            ArrayList<Link> links = DbHelper.loadUnprocessedLink(tableName, 20);
            if (links.size() == 0) {
                hasMoreUnprocessed = false;
                return null;
            }
            cachedUnprocessedLink.addAll(links);
        }
        Link r = cachedUnprocessedLink.remove(cachedUnprocessedLink.size() - 1);
        DbHelper.updateLinkStatus(r, Link.STATE_PROCESSING, tableName);
        lastAccessTime = System.currentTimeMillis();
        return r;
    }

    /**
     * this method should be called the first time a link store is initialized, eg. after a shutdown. 
     * It will reset all processing links that is left as processing in link store.
     */
    public int resetProcessingLinkToUnprocessed() {
        return DbHelper.updateLinkState(Link.STATE_PROCESSING, Link.STATE_NOT_PROCESSED, tableName);
    }

    public String getStringUid() {
        return tableStringUid;
    }

    public int getCachedSize() {
        return cachedUnprocessedLink.size();
    }

    public boolean isInActiveForTooLong() {
        if (hasMoreUnprocessed == false && (System.currentTimeMillis() - lastAccessTime) > Constants.LINK_STORE_MAX_INACTIVE_INTERVAL) {
            return true;
        }
        return false;
    }
}

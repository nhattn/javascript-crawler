package com.zyd.linkmanager.mysql;

import java.util.ArrayList;
import com.zyd.linkmanager.Link;

public class LinkStore {
    private String tableName;
    private String tableStringUid;
    private ArrayList<Link> cachedUnprocessedLink = new ArrayList<Link>();
    public boolean hasMoreUnprocessed = true;

    /**
     * @param tableName name of the table in db, in the format of LinkTable_xxx
     * @param tableUid string uid used uniquely identify the table.
     */
    public LinkStore(String tableName, String tableUid) {
        this.tableName = tableName;
        this.tableStringUid = tableUid;
    }

    public Link addLink(String url) {
        Link link = DbHelper.addNewLinkToTable(tableName, url);
        synchronized (this) {
            if (cachedUnprocessedLink.size() < 100) {
                cachedUnprocessedLink.add(link);
            }
        }
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

    public synchronized Link nextUnprocessedLink() {
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
        return r;
    }
}

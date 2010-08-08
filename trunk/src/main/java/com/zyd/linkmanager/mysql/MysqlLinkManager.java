package com.zyd.linkmanager.mysql;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.zyd.linkmanager.Link;
import com.zyd.linkmanager.LinkManager;

public class MysqlLinkManager implements LinkManager {
    // link store string uid -> linkstore
    private final static HashMap<String, LinkStore> storeMap = new HashMap<String, LinkStore>(100);
    private final static HashMap<String, Link> processingLinkMap = new HashMap<String, Link>();
    private final static ArrayList<LinkStore> storeList = new ArrayList<LinkStore>();

    /**
     * returns null if this link is already managed.
     */
    public Link addLink(String url) {
        String storeUid = LinkTableMapper.mapUrl(url);
        LinkStore store = storeMap.get(storeUid);

        if (store == null) {
            store = createStore(storeUid);
            return store.addLink(url);
        }

        if (store.isLinkManaged(url) == true) {
            return null;
        } else {
            return store.addLink(url);
        }
    }

    private static LinkStore createStore(String storeUid) {
        LinkStore store = null;
        // check to see if the table exist
        LinkTableInfo info = DbHelper.getLinkTableInfoByUid(storeUid);
        if (info == null) {
            // create table 
            info = DbHelper.createLinkTable(storeUid);
        }
        store = new LinkStore(info.getTableName(), storeUid);
        storeMap.put(storeUid, store);
        storeList.add(store);
        return store;
    }

    public Link getLink(String url) {
        String storeUid = LinkTableMapper.mapUrl(url);
        LinkStore store = storeMap.get(storeUid);
        if (store == null) {
            store = createStore(storeUid);
            storeMap.put(storeUid, store);
        }
        throw new UnsupportedOperationException();
    }

    public Link linkFinished(String url) {
        Link link = processingLinkMap.get(url);
        if (link == null) {
            return null;
        }
        String storeUid = LinkTableMapper.mapUrl(url);
        LinkStore store = storeMap.get(storeUid);
        link.setState(Link.STATE_FINISHED_OK);
        link.setFinishTime(new Date());
        store.LinkFinished(link);
        processingLinkMap.remove(url);
        return link;
    }

    private int lastStoreIndex = 0;

    public Link roundRobinNextLink() {
        synchronized (this) {
            for (int i = 0, storeSize = storeList.size(); i < storeSize; i++) {
                com.zyd.linkmanager.mysql.LinkStore store = storeList.get((lastStoreIndex + i) % storeSize);
                Link link = store.nextUnprocessedLink();
                if (link != null) {
                    lastStoreIndex = (lastStoreIndex + i + 1) % storeSize;
                    processingLinkMap.put(link.getUrl(), link);
                    return link;
                }
            }
        }
        return null;
    }
}

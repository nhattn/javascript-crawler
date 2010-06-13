package com.zyd.core.busi;

import com.zyd.core.objecthandler.ObjectManager;

public class CrawlerManager {

    private LinkManager linkManager;
    private ObjectManager objectManager;

    private CrawlerManager() {
    }

    public void setLinkManager(LinkManager lm) {
        this.linkManager = lm;
    }

    public void setObjectManager(ObjectManager om) {
        objectManager = om;
    }

    public void clearAll() {
        this.linkManager.cleanAll();
        this.objectManager.deleteAllObjects();
    }
}

package com.zyd.web.core;

import com.zyd.web.dom.WebSite;

public class SiteManager {

    private static SiteManager instance = new SiteManager();

    private SiteManager() {
    }

    public static SiteManager getInstance() {
        return instance;
    }

    /**
     * get an existing site from database, or add a new site then load it and return.
     * @param domain domain names like www.yahoo.com
     * @return
     */
    public WebSite getSite(String domain){
        WebSite ret = new WebSite();
        return ret;
    }
}

package com.zyd.linkmanager.mysql;

import com.zyd.core.Utils;

public class LinkTableMapper {
    /**
     * Given a url, map it to a unique identifer name, which is further used to select a {@link LinkStore}
     * @param url
     * @return
     */

    public static String mapUrl(String url) {
        return Utils.getShortestDomain(url);
    }
}

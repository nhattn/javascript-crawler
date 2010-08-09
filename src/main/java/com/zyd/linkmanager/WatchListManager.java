package com.zyd.linkmanager;

import com.zyd.Constants;
 
public class WatchListManager {
    private static int lastWatchedLinkIndex = -1;
    private final static Link IdlePageUrl = new Link(Constants.IdlePageUrl);

    public final static Link nextWatchedLink() {
        if (Constants.WATCH_LIST.length == 0) {
            return IdlePageUrl;
        }
        synchronized (WatchListManager.class) {
            lastWatchedLinkIndex = (lastWatchedLinkIndex + 1) % Constants.WATCH_LIST.length;
            return Constants.WATCH_LIST[lastWatchedLinkIndex];
        }
    }

}

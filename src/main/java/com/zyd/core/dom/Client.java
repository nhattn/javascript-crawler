package com.zyd.core.dom;

import java.util.Date;

public class Client {
    public String ip;
    public int processedCount = 0;
    public Date lastAccess;
    public String lastSite;

    @Override
    public String toString() {
        return "[ip: " + ip + ", lastAccess: " + lastAccess.toString() + ", processedCount: " + processedCount + ", lastSite: " + lastSite + "]";
    }
}

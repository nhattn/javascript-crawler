package com.zyd.core.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.zyd.Constants;
import com.zyd.core.dom.Counter;

/**
 * count access from each ip address, then group it periodically and determins which ones should be blocked.
 */
public class IpCounter implements com.zyd.core.busi.WorkerThread.Job {
    private static Logger logger = Logger.getLogger(IpCounter.class);
    private HashSet<String> blocked = new HashSet<String>();
    protected ArrayList<String> iplist, iplist1, iplist2;
    private long lastCheckTime;

    public IpCounter() {
        iplist1 = new ArrayList<String>(5000);
        iplist2 = new ArrayList<String>(5000);
        iplist = iplist1;
        lastCheckTime = System.currentTimeMillis();
    }

    public void logAccess(String ip) {
        try {
            iplist.add(ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkIp() {
        if (iplist.size() == 0)
            return;
        logger.info("Start checking ip in ip counter");
        ArrayList<String> oldList;

        oldList = iplist;
        if (iplist.equals(iplist1)) {
            iplist = iplist2;
        } else {
            iplist = iplist1;
        }

        HashMap<String, Counter> mapping = new HashMap<String, Counter>();
        for (int i = 0, len = oldList.size(); i < len; i++) {
            String ip = oldList.get(i);
            Counter c = mapping.get(ip);
            if (c == null) {
                c = new Counter();
                mapping.put(ip, c);
            } else {
                c.total++;
            }
        }

        Set<Entry<String, Counter>> entries = mapping.entrySet();
        for (Entry<String, Counter> e : entries) {
            if (e.getValue().total > Constants.IpBlockerMaxAccessPerIntervalCycle) {
                logger.warn("ip accessing to much :" + e.getValue().total + ", " + e.getKey() + ", added to block list.");
                logger.debug("Added ip to block list :" + e.getKey());
                blocked.add(e.getKey());
            }
        }
        oldList.clear();
        logger.info("Done checking ip in ip counter");
    }

    public void reset() {
        blocked.clear();
        iplist1.clear();
        iplist2.clear();
    }

    public String report() {
        StringBuffer buf = new StringBuffer();
        buf.append("Blocked:" + blocked.toString());
        return buf.toString();
    }

    public HashSet<String> getBlockedList() {
        return blocked;
    }

    public void clearBlockedList() {
        blocked.clear();
    }

    public void doJob() {
        try {
            checkIp();
        } finally {
            lastCheckTime = System.currentTimeMillis();
        }
    }

    public boolean shouldRun() {
        return (System.currentTimeMillis() - lastCheckTime) > Constants.IpCounterExecuteInterval;
    }

}

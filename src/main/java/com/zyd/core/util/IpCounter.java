package com.zyd.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.zyd.Constants;

public class IpCounter {
    private static Logger logger = Logger.getLogger(IpCounter.class);
    private HashSet<String> blocked = new HashSet<String>();
    protected ArrayList<String> iplist, iplist1, iplist2;
    CheckerThread checkerThread;

    public IpCounter() {
        iplist1 = new ArrayList<String>(5000);
        iplist2 = new ArrayList<String>(5000);
        iplist = iplist1;
        checkerThread = new CheckerThread();
    }

    public void start() {
        checkerThread.startChecker();
    }

    public void stop() {
        checkerThread.stopChecker();
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
        ArrayList<String> oldList = iplist;

        if (iplist.equals(iplist1)) {
            iplist = iplist1;
        } else {
            iplist = iplist1;
        }

        long time = System.currentTimeMillis();
        HashMap<String, Counter> mapping = new HashMap();
        for (String ip : oldList) {
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
            if (e.getValue().total > Constants.IpBlockerMaxAccessPerInterval) {
                logger.warn("ip accessing to much :" + e.getValue().total + ", " + e.getKey() + ", added to block list.");
                blocked.add(e.getKey());
            }
        }
        oldList.clear();
    }

    class Counter {
        long total = 1;
    }

    class CheckerThread extends Thread {
        public CheckerThread() {
            super("Ip counter - checker");
        }

        private boolean shouldStop = false;

        public void run() {
            logger.info("IpCounter thread started");
            while (shouldStop == false) {
                try {
                    checkIp();
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Error e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(Constants.IpBlockerSleepInterval);
                } catch (Exception e) {
                }
            }
            logger.info("IpCounter thread stopped");
        }

        public void startChecker() {
            this.start();
        }

        public void stopChecker() {
            shouldStop = true;
            interrupt();
        }
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

}

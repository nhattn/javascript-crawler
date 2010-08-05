package com.zyd.core.busi;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.zyd.Constants;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.util.IpCounter;
import com.zyd.core.util.SpringContext;

public class AccessController extends Thread {
    private static Logger logger = Logger.getLogger(AccessController.class);

    private HashSet<String> blocked = new HashSet<String>();
    private boolean shouldStop = false;
    private IpCounter ipCounter;

    public AccessController() {
        super("Access Blocker");
        ipCounter = (IpCounter) SpringContext.getContext().getBean("ipCounter");
    }

    public boolean isIpBlocked(String ip) {
        return blocked.contains(ip);
    }

    public void startAccessBlocker() {
        this.start();
    }

    public void stopAccessBlocker() {
        this.shouldStop = true;
        this.interrupt();
    }

    public void loadedBlockedListFromDb() {
        List list = HibernateUtil.loadObject("BlockedIp");
        for (Object o : list) {
            blocked.add(((Map) o).get("ip").toString());
        }
        logger.info("List of already blocked ip");
        logger.info(blocked);
    }

    private void checkBlockList() {
        HashSet<String> blockList = ipCounter.getBlockedList();
        for (String ip : blockList) {
            if (blocked.contains(ip) == false) {
                logger.info("writting ip to block list table " + blockList);
                HibernateUtil.saveObject("BlockedIp", "ip", ip, "createTime", new Date());
                blocked.add(ip);
            }
        }
        ipCounter.clearBlockedList();
    }

    @Override
    public void run() {
        logger.info("Access blocker thread started.");
        loadedBlockedListFromDb();
        while (shouldStop == false) {
            try {
                checkBlockList();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(Constants.AccessControllerSleepInterval);
            } catch (Exception e) {
            }
        }
        logger.info("Access blocker thread stopped.");
    }
}

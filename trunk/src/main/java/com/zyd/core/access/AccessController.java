package com.zyd.core.access;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.zyd.Constants;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.util.SpringContext;

/**
 * stops crawler.
 * Takes blocked ip address from IpCounter, grants access.
 *
 */
public class AccessController implements com.zyd.core.busi.WorkerThread.Job {
    public final static String HibernateEntityName = "BlockedIp";
    public final static String TableName = "IpBlockList";

    private static Logger logger = Logger.getLogger(AccessController.class);

    private HashSet<String> blocked = new HashSet<String>();
    private IpCounter ipCounter;
    private long lastCheckTime;

    public AccessController() {
        loadedBlockedListFromDb();
        ipCounter = (IpCounter) SpringContext.getContext().getBean("ipCounter");
        lastCheckTime = System.currentTimeMillis();
    }

    public boolean isIpBlocked(String ip) {
        return blocked.contains(ip);
    }

    public void loadedBlockedListFromDb() {
        List list = HibernateUtil.loadObject(HibernateEntityName);
        for (Object o : list) {
            blocked.add(((Map) o).get("ip").toString());
        }
        logger.info("List of already blocked ip");
        logger.info(blocked);
    }

    private void checkBlockList() {
        logger.info("Start checking blocked list");
        int counter = 0;
        HashSet<String> blockList = ipCounter.getBlockedList();
        for (String ip : blockList) {
            if (blocked.contains(ip) == false) {
                logger.info("writting ip to block list table " + ip);
                HibernateUtil.saveObject("BlockedIp", "ip", ip, "createTime", new Date());
                blocked.add(ip);
                counter++;
            }
        }
        ipCounter.clearBlockedList();
        logger.info("Done checking blocked list, added new ip :" + counter);
    }

    public void doJob() {
        try {
            checkBlockList();
        } finally {
            lastCheckTime = System.currentTimeMillis();
        }
    }

    public boolean shouldRun() {
        return (System.currentTimeMillis() - lastCheckTime) > Constants.ACCESS_CONTROLLER_EXECUTION_INTERVAL;
    }
}
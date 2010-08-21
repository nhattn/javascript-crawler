package com.zyd.web.listener;

import java.text.ParseException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;

import com.zyd.Constants;
import com.zyd.core.access.AccessController;
import com.zyd.core.access.AuthorizationController;
import com.zyd.core.access.IpCounter;
import com.zyd.core.busi.JobManager;
import com.zyd.core.busi.house.HouseStatasticsManager;
import com.zyd.core.util.SpringContext;
import com.zyd.linkmanager.mysql.MysqlLinkManager;
import com.zyd.web.service.zyd;

public class ZydContextListener implements ServletContextListener {
    private static Logger logger = Logger.getLogger(ZydContextListener.class);

    public ZydContextListener() {
    }

    public void contextDestroyed(ServletContextEvent arg0) {
        ((JobManager) SpringContext.getContext().getBean("jobManager")).stopScheduler();
    }

    public void contextInitialized(ServletContextEvent arg0) {
        if (Constants.SERVER_DOMAIN == null) {
            // do nothing just to initialize 
        }
        JobManager jobMan = ((JobManager) SpringContext.getContext().getBean("jobManager"));
        jobMan.registerJob(IpCounter.class, Constants.IPCOUNTER_CHECK_INTERVAL);
        jobMan.registerJob(AccessController.class, Constants.ACCESS_CONTROLLER_EXECUTION_INTERVAL);
        jobMan.registerJob(AuthorizationController.class, Constants.AUTHORIZATION_CONTROLLER_EXECUTION_INTERVAL);
        jobMan.registerJob(MysqlLinkManager.class, Constants.LINK_MONITOR_SCAN_INTERVAL);
        try {
            // running every day at 1 seconds past 1
            CronTrigger trigger = new CronTrigger(HouseStatasticsManager.class.getCanonicalName(), null, "1 0 0 * * ?");
            trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
            jobMan.registerJob(HouseStatasticsManager.class, trigger);
        } catch (ParseException e) {
            logger.fatal("Can not register HouseStatisticsManager ", e);
        }
        jobMan.startScheduler();
    }
}

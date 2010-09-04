package com.zyd.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.CronTrigger;

import com.zyd.Constants;
import com.zyd.core.access.AccessController;
import com.zyd.core.access.AuthorizationController;
import com.zyd.core.access.IpCounter;
import com.zyd.core.busi.JobManager;
import com.zyd.core.busi.house.HouseStatasticsManager;
import com.zyd.core.util.SpringContext;
import com.zyd.linkmanager.mysql.MysqlLinkManager;
import com.zyd.linkmanager.watchlist.InjectableWatchlist;

public class ZydContextListener implements ServletContextListener {

    public ZydContextListener() {
    }

    public void contextDestroyed(ServletContextEvent arg0) {
        ((JobManager) SpringContext.getContext().getBean("jobManager")).stopScheduler();
        InjectableWatchlist.stopAll();
    }

    public void contextInitialized(ServletContextEvent arg0) {
        if (Constants.SERVER_DOMAIN == null) {
            // do nothing just to initialize 
        }

        JobManager jobMan = ((JobManager) SpringContext.getContext().getBean("jobManager"));
        jobMan.registerJob(IpCounter.PeriodicalJob.class, Constants.IPCOUNTER_CHECK_INTERVAL);
        jobMan.registerJob(AccessController.PeriodicalJob.class, Constants.ACCESS_CONTROLLER_EXECUTION_INTERVAL);
        jobMan.registerJob(AuthorizationController.PeriodicalJob.class, Constants.AUTHORIZATION_CONTROLLER_EXECUTION_INTERVAL);
        jobMan.registerJob(MysqlLinkManager.CleanLinkJob.class, Constants.LINK_MONITOR_SCAN_INTERVAL);
        jobMan.registerCronJob(HouseStatasticsManager.DailyHouseJob.class, "1 0 0 * * ?", CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
        jobMan.registerCronJob(HouseStatasticsManager.WeeklyHouseJob.class, "1 0 0 ? * MON", CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
        jobMan.registerCronJob(HouseStatasticsManager.MonthlyHouseJob.class, "1 0 0 1 * ?", CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);

        // register injection link manager
        //        jobMan.registerCronJob(GoogleFilm.class, GoogleFilm.CronDef, CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
        //        jobMan.registerCronJob(Weathercn.class, Weathercn.CronDef, CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);

        jobMan.startScheduler();
    }
}

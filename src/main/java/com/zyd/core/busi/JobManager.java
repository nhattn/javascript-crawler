package com.zyd.core.busi;

import java.text.ParseException;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class JobManager {
    private final static Logger logger = Logger.getLogger(JobManager.class);
    Scheduler scheduler;

    public JobManager() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            logger.fatal("!!! Can not initialize scheduler. ", e);
        }
    }

    public void startScheduler() {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            logger.fatal("!!! Can not start scheduler. ", e);
        }
    }

    public void stopScheduler() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            logger.fatal("!!! Exception when shutting down scheduler. ", e);
        }
    }

    public void registerJob(Class jobClass, int interval) {
        SimpleTrigger trigger = new SimpleTrigger(jobClass.getCanonicalName(), SimpleTrigger.REPEAT_INDEFINITELY, interval);
        trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
        registerJob(jobClass, trigger);
    }

    public void registerCronJob(Class jobClass, String cronTriggerSpec, int misfirePolicy) {
        CronTrigger trigger = null;
        try {
            trigger = new CronTrigger(jobClass.getCanonicalName(), null, cronTriggerSpec);
        } catch (ParseException e) {
            logger.fatal("Can not instiate cron trigger with specification: " + cronTriggerSpec, e);
            return;
        }
        trigger.setMisfireInstruction(misfirePolicy);
        registerJob(jobClass, trigger);
    }

    public void registerJob(Class jobClass, Trigger trigger) {
        String className = jobClass.getCanonicalName();
        try {
            scheduler.scheduleJob(new JobDetail(className, jobClass), trigger);
            logger.info("scheduled job " + className);
        } catch (SchedulerException e) {
            logger.fatal("!!! Exception when scheduling a job, " + className, e);
        }
    }
}

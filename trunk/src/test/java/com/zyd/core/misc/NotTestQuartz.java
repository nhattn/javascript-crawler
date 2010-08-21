package com.zyd.core.misc;

import junit.framework.TestCase;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

public class NotTestQuartz extends TestCase {

    public void testit() throws Exception {

        try {
            // Grab the Scheduler instance from the Factory 
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            scheduler.start();

            JobDetail job = new JobDetail("job1", "group1", SimpleJob.class);

            //            Trigger trigger = new SimpleTrigger("trigger1", null, new Date(), null, 3, 1000);
            Trigger trigger = TriggerUtils.makeDailyTrigger("Daily Trigger", 17, 04);
            scheduler.scheduleJob(job, trigger);
            trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
//            Thread.sleep(10000000);
            scheduler.shutdown();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

}
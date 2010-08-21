package com.zyd.core.misc;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SimpleJob implements org.quartz.Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("running");
    }

}
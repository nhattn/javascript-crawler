package com.zyd.linkmanager.watchlist;

import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.zyd.core.util.SpringContext;
import com.zyd.linkmanager.LinkManager;

public abstract class InjectableWatchlist implements Job, Runnable {
    private static Logger logger = Logger.getLogger(InjectableWatchlist.class);

    public abstract int getInjectInveral();

    public abstract String nextLink();

    public abstract String getInfo();

    private static HashMap<String, InjectableWatchlist> runningInstances = new HashMap<String, InjectableWatchlist>();

    private Thread currentThread = null;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        synchronized (InjectableWatchlist.class) {
            String className = this.getClass().getCanonicalName();
            if (runningInstances.get(className) != null) {
                logger.debug("Another instance of " + className + " is running, will not execute this one:");
                logger.debug(runningInstances.get(className).getInfo());
                return;
            }
            Thread thread = new Thread(this);
            runningInstances.put(className, this);
            thread.start();
            currentThread = thread;
        }
    }

    public void run() {
        int interval = getInjectInveral() * 1000;
        LinkManager linkManager = (LinkManager) SpringContext.getContext().getBean("linkManager");
        logger.debug("InjectableWatchlist starting " + this.getClass().getCanonicalName());
        while (Thread.interrupted() == false) {
            String link = nextLink();
            if (link == null) {
                break;
            }
            linkManager.addPriorityLink(link);
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ie) {
                break;
            }
        }
        synchronized (InjectableWatchlist.class) {
            runningInstances.remove(this.getClass().getCanonicalName());
        }
        logger.debug("InjectableWatchlist finished " + this.getClass().getCanonicalName());
    }

    public static void stopAll() {
        synchronized (InjectableWatchlist.class) {
            Collection<InjectableWatchlist> values = runningInstances.values();
            for (InjectableWatchlist item : values) {
                item.currentThread.interrupt();
            }
        }
    }
}

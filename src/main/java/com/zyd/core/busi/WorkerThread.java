package com.zyd.core.busi;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.zyd.Constants;

public class WorkerThread implements Runnable {
    Logger logger = Logger.getLogger(WorkerThread.class);
    private Thread workerThread;
    private boolean shouldStop;
    private ArrayList<Job> jobs = new ArrayList<Job>();

    public synchronized void registerWork(Job job) {
        jobs.add(job);
    }

    /**
     * Can be called only once
     */
    public void start() {
        shouldStop = false;
        workerThread = new Thread(this, "Worker thread");
        workerThread.start();
    }

    public void stop() {
        shouldStop = true;
        workerThread.interrupt();
    }

    public void wakeUp() {
        workerThread.interrupt();
    }

    /**
     * if should stop is set to be true, then it will finish the current job, and stop working on any of the unfinished jobs.
     */
    public void run() {
        logger.info("WorkerThread started, execute every " + Constants.WORKER_THREAD_EXECUTION_INTERVAL / 1000 + " seconds.");
        while (shouldStop == false) {
            for (Job job : jobs) {
                try {
                    if (job.shouldRun())
                        job.doJob();
                } catch (Exception e) {
                    logger.error("Exception while executing jobs", e);
                    e.printStackTrace();
                } catch (Error e) {
                    logger.error("Error while executing jobs", e);
                    e.printStackTrace();
                }
                if (shouldStop == true)
                    break;
            }
            try {
                Thread.sleep(Constants.WORKER_THREAD_EXECUTION_INTERVAL);
            } catch (Exception e) {

            }
        }
        logger.info("WorkerThread stopped");
    }

    public interface Job {
        public void doJob();

        public boolean shouldRun();

    }
}

package com.zyd.core.workerThread;

import com.zyd.ATestUtil;
import com.zyd.Constants;
import com.zyd.core.busi.WorkerThread;
import com.zyd.core.util.SpringContext;

import junit.framework.TestCase;

public class TestWorkerThread extends TestCase {

    static class TestJob implements WorkerThread.Job {
        public int counter = 0;
        public boolean run = true;

        public void doJob() {
            counter++;
        }

        public boolean shouldRun() {
            return run;
        }
    }

    public void testWorkeThreadRunInTime() {
        ATestUtil.setUpSpring();
        Constants.WORKER_THREAD_EXECUTION_INTERVAL = 1 * 1000;
        WorkerThread wt = (WorkerThread) SpringContext.getContext().getBean("workerThread");

        TestJob job1 = new TestJob(), job2 = new TestJob(), job3 = new TestJob();
        job1.run = true;
        job2.run = true;
        job3.run = false;
        wt.registerWork(job1);
        wt.registerWork(job3);
        wt.registerWork(job2);
        wt.start();
        try {
            System.out.println("Wait seven seconds");
            Thread.sleep(7 * 1000);
        } catch (Exception e) {

        }
        assertTrue(job1.counter > 5);
        assertTrue(job2.counter > 5);
        assertTrue(job3.counter == 0);
    }
}

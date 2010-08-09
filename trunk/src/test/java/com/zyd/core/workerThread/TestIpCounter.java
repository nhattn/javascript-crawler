package com.zyd.core.workerThread;

import java.util.ArrayList;
import java.util.HashSet;

import junit.framework.TestCase;

import com.zyd.ATestUtil;
import com.zyd.Constants;
import com.zyd.core.access.IpCounter;
import com.zyd.core.busi.WorkerThread;
import com.zyd.core.util.SpringContext;

public class TestIpCounter extends TestCase {
    static int counterThreadSleepTime = 5 * 1000;
    static int maxAllowedAccessPerCycle = 5;
    static int workerThreadSleepTime = 3 * 1000;
    IpCounter counter;

    class TestIpAccessThread extends Thread {
        public String ip;
        public int sleeptime;

        public TestIpAccessThread(String ip, int sleepTime) {
            this.ip = ip;
            sleeptime = sleepTime;
        }

        @Override
        public void run() {
            while (true) {
                counter.logAccess(ip);
                try {
                    Thread.sleep(sleeptime);
                } catch (Exception e) {
                }
            }

        }
    }

    public void testWorkeThreadRunInTime() {
        ATestUtil.setUpSpring();
        counter = (IpCounter) SpringContext.getContext().getBean("ipCounter");
        Constants.IPCOUNTER_CHECK_INTERVAL = 4 * 1000;
        Constants.IPCOUNTER_MAX_ACCESS_PER_CYCLE = 6;
        Constants.WORKER_THREAD_EXECUTION_INTERVAL = 2 * 1000;

        ArrayList<TestIpAccessThread> ip1 = new ArrayList<TestIpAccessThread>(), ip2 = new ArrayList<TestIpAccessThread>();
        for (int i = 0; i < 15; i++) {
            ip1.add(new TestIpAccessThread("ipshot" + (i * 10 + 1), i * 10 + 1));
        }

        for (int i = 0; i < 15; i++) {
            ip2.add(new TestIpAccessThread("iplong" + (i + 1000), (i + 1000)));
        }

        Constants.WORKER_THREAD_EXECUTION_INTERVAL = 1 * 1000;
        WorkerThread wt = (WorkerThread) SpringContext.getContext().getBean("workerThread");
        wt.registerWork(counter);
        wt.start();
        for (int i = 0; i < 15; i++) {
            ip1.get(i).start();
            ip2.get(i).start();
        }

        try {
            System.out.println("Wait 5seconds");
            Thread.sleep(5 * 1000);
        } catch (Exception e) {

        }
        HashSet<String> blocked = counter.getBlockedList();
        System.out.println(blocked);
        for (int i = 0; i < 15; i++) {
            assertTrue(blocked.contains(ip1.get(i).ip));
            assertFalse(blocked.contains(ip2.get(i).ip));
        }
    }

}

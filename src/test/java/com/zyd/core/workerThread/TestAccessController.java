package com.zyd.core.workerThread;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.zyd.ATestUtil;
import com.zyd.Constants;
import com.zyd.core.access.AccessController;
import com.zyd.core.access.IpCounter;
import com.zyd.core.busi.WorkerThread;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.util.SpringContext;

public class TestAccessController extends TestCase {
    static int counterThreadSleepTime = 5 * 1000;
    static int maxAllowedAccessPerCycle = 5;
    static int workerThreadSleepTime = 3 * 1000;
    IpCounter counter;
    AccessController accessController;

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
        // must be called before setupspring
        HibernateUtil.deleteAllObject(AccessController.HibernateEntityName);
        ATestUtil.setUpSpring();
        counter = (IpCounter) SpringContext.getContext().getBean("ipCounter");
        accessController = (AccessController) SpringContext.getContext().getBean("accessController");
        
        Constants.IpCounterExecuteInterval = 4 * 1000;
        Constants.IpBlockerMaxAccessPerIntervalCycle = 6;
        Constants.WorkerThreadSleepInterval = 2 * 1000;
        Constants.AccessControllerExecuteInterval = 2 * 1000;

        ArrayList<TestIpAccessThread> ip1 = new ArrayList<TestIpAccessThread>();
        for (int i = 0; i < 15; i++) {
            ip1.add(new TestIpAccessThread("ipshot" + (i * 10 + 1), i * 10 + 1));
        }

        WorkerThread wt = (WorkerThread) SpringContext.getContext().getBean("workerThread");
        wt.registerWork(counter);
        wt.registerWork(accessController);
        wt.start();
        for (int i = 0; i < 15; i++) {
            ip1.get(i).start();
        }

        try {
            System.out.println("Wait 10 seconds");
            Thread.sleep(10 * 1000);
        } catch (Exception e) {

        }

        for (int i = 0; i < 15; i++) {
            assertTrue(accessController.isIpBlocked(ip1.get(i).ip));
        }

        // start a new access controller, force loading data from db
        accessController = new AccessController();

        for (int i = 0; i < 15; i++) {
            assertTrue("index "+i+", "+ip1.get(i).ip, accessController.isIpBlocked(ip1.get(i).ip));
        }

    }
}

package com.zyd.core.workerThread;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.zyd.ATestUtil;
import com.zyd.Constants;
import com.zyd.core.access.AccessController;
import com.zyd.core.access.IpCounter;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.util.SpringContext;

public class NotTestAccessController extends TestCase {
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

    public void nottestWorkeThreadRunInTime() {
        // must be called before setupspring
        HibernateUtil.deleteAllObject(AccessController.HibernateEntityName);
        ATestUtil.setUpSpring();
        counter = (IpCounter) SpringContext.getContext().getBean("ipCounter");
        accessController = (AccessController) SpringContext.getContext().getBean("accessController");
        
        Constants.IPCOUNTER_CHECK_INTERVAL = 4 * 1000;
        Constants.IPCOUNTER_MAX_ACCESS_PER_CYCLE = 6;
        Constants.WORKER_THREAD_EXECUTION_INTERVAL = 2 * 1000;
        Constants.ACCESS_CONTROLLER_EXECUTION_INTERVAL = 2 * 1000;

        ArrayList<TestIpAccessThread> ip1 = new ArrayList<TestIpAccessThread>();
        for (int i = 0; i < 15; i++) {
            ip1.add(new TestIpAccessThread("ipshot" + (i * 10 + 1), i * 10 + 1));
        }

//        WorkerThread wt = (WorkerThread) SpringContext.getContext().getBean("workerThread");
//        wt.registerWork(counter);
//        wt.registerWork(accessController);
//        wt.start();
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

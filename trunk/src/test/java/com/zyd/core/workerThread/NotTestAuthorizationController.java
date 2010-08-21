package com.zyd.core.workerThread;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.zyd.ATestUtil;
import com.zyd.Constants;
import com.zyd.core.access.AuthorizationController;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.access.ClientInfo;
import com.zyd.core.util.SpringContext;

public class NotTestAuthorizationController extends TestCase {

    AuthorizationController ac;

    class TestAccessThread extends Thread {
        public String ip;
        public String id, key;
        public int sleeptime;
        public boolean shouldStop = false;
        public int accessedCount = 0;

        public TestAccessThread(String clientId, String clientKey, String ip, int sleepTime) {
            this.ip = ip;
            this.id = clientId;
            this.key = clientKey;
            sleeptime = sleepTime;
        }

        @Override
        public void run() {
            while (shouldStop == false) {
                try {
                    assertTrue(ac.logAccess(id, ip));
                    accessedCount++;
                    Thread.sleep(sleeptime);
                } catch (Exception e) {
                }
            }
        }
    }

    public void testWorkeThreadRunInTime() {
        HibernateUtil.deleteAllObject(AuthorizationController.HibernateEntityName);
        ATestUtil.setUpSpring();
        ac = (AuthorizationController) SpringContext.getContext().getBean("authorizationController");
        Constants.WORKER_THREAD_EXECUTION_INTERVAL = 2 * 1000;
        Constants.AUTHORIZATION_CONTROLLER_EXECUTION_INTERVAL = 3 * 1000;

        ArrayList<TestAccessThread> clientGroup1 = new ArrayList<TestAccessThread>();
        for (int i = 0; i < 15; i++) {
            String id = "id" + i, ip = "ip" + i, key = "key" + i;
            clientGroup1.add(new TestAccessThread(id, key, ip, 100 + i));
            ClientInfo cinfo = new ClientInfo();
            cinfo.ip = ip;
            cinfo.lastAccessTime = System.currentTimeMillis();
            cinfo.total = 0;
            cinfo.totalSinceLastCycle = 0;
            cinfo.setClientId(id);
            cinfo.setClientkey(key);
            cinfo.setEmail("email" + i);
            cinfo.setLevel(i);
            ac.createClientInfo(cinfo);
            ac.authorize(id, key, ip);
        }

//        WorkerThread wt = (WorkerThread) SpringContext.getContext().getBean("workerThread");
//        wt.registerWork(ac);

//        wt.start();
        for (int i = 0; i < 15; i++) {
            clientGroup1.get(i).start();
        }

        try {
            System.out.println("Wait 5seconds");
            Thread.sleep(5 * 1000);
        } catch (Exception e) {
        }

        // make sure data is saved into db
        for (TestAccessThread tc : clientGroup1) {
            tc.shouldStop = true;
            tc.interrupt();
        }

        try {
            System.out.println("Wait 5seconds");
            Thread.sleep(5 * 1000);            
        } catch (Exception e) {
        }
//        wt.stop();
        for (TestAccessThread tc : clientGroup1) {
            tc.shouldStop = true;
            tc.interrupt();
            ClientInfo info = ac.loadClientInfoByClientId(tc.id);
            assertEquals(info.totalSinceLastCycle, tc.accessedCount);
        }

    }

}

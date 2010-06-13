package com.zyd.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;

import com.tj.common.util.test.CommonTestUtil;
import com.zyd.ATestConstants;
import com.zyd.ATestUtil;
import com.zyd.Constants;
import com.zyd.core.objecthandler.House;
import com.zyd.core.objecthandler.ObjectManager;

@SuppressWarnings("unchecked")
public class TestHibernateExceptionHandling extends TestCase {

    ObjectManager om = null;

    @Override
    protected void setUp() throws Exception {
        ApplicationContext ctx = ATestUtil.setUpSpring();
        om = (ObjectManager) ctx.getBean("objectManager");
        ATestUtil.stopReturningWatchedLink();
        ATestUtil.clearServerData();
        
    }

    public void testSingleThread() throws Exception {
        Map map = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectManipulation.class, "house1.prop", Constants.Encoding_DEFAULT_SYSTEM);
        map.put(com.zyd.core.objecthandler.Handler.Parameter.PARAMETER_OBJECT_ID, "House");
        map.put(com.zyd.core.objecthandler.House.Columns.Contact, "this is a very long string it should over flow");
        boolean result = true;
        try {
            result = ATestUtil.createObject(map);
        } catch (Exception e) {
            result = false;
        }
        assertFalse(result);

        int errorCount = 0;
        for (int i = 0; i < 50; i++) {
            map = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectManipulation.class, "house1.prop", Constants.Encoding_DEFAULT_SYSTEM);
            map.put(House.Columns.Address, "address" + CommonTestUtil.getNonRepeatString());
            String referer = ATestConstants.OBJECT_REFERER_PREFIX + CommonTestUtil.getNonRepeatString();
            if (ATestUtil.createObject(map, referer) == false) {
                errorCount++;
            }
            try {
                //Thread.sleep(5000);
            } catch (Exception e) {
            }
        }
        assertEquals(0, errorCount);
    }

    public void testMultiThread() throws Exception {
        Map map = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectManipulation.class, "house1.prop", Constants.Encoding_DEFAULT_SYSTEM);
        map.put(com.zyd.core.objecthandler.Handler.Parameter.PARAMETER_OBJECT_ID, "House");
        map.put(com.zyd.core.objecthandler.House.Columns.Contact, "this is a very long string it should over flow");
        boolean result = true;
        try {
            result = ATestUtil.createObject(map);
        } catch (Exception e) {
            result = false;
        }
        assertFalse(result);
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
            new WorkerThread("worker " + i).start();
        }
        while (threadCount > 0) {
            System.out.println("remaing thread :" + threadCount);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }

    int threadCount = 0;

    private synchronized void addThreadCount() {
        threadCount++;
    }

    private synchronized void descreaseThreadCount() {
        threadCount--;
    }

    class WorkerThread extends Thread {
        public WorkerThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            addThreadCount();
            int errorCount = 0;
            try {
                for (int i = 0; i < 50; i++) {
                    Map map = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectManipulation.class, "house1.prop", Constants.Encoding_DEFAULT_SYSTEM);
                    map.put(House.Columns.Address, "address" + CommonTestUtil.getNonRepeatString());
                    if (ATestUtil.createObject(map) == false) {
                        errorCount++;
                    }
                    Date d = new Date();
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                    }                    
                }
            } catch (Exception e) {
                errorCount++;
            } catch (Error e) {
                errorCount++;
            }
            if (errorCount != 0) {
                System.out.println("Should not happen, can not create links, number of links failed :" + errorCount);
            }
            descreaseThreadCount();
        }
    }
}

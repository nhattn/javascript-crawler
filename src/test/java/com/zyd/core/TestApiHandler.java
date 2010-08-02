package com.zyd.core;

import java.util.ArrayList;
import java.util.HashMap;

import com.tj.common.util.test.CommonTestUtil;
import com.tj.common.util.test.HttpTestUtil;
import com.zyd.ATestConstants;
import com.zyd.ATestUtil;
import com.zyd.core.busi.api.ApiHandler;
import com.zyd.web.service.api;

import junit.framework.TestCase;

public class TestApiHandler extends TestCase {
    @Override
    protected void setUp() throws Exception {
        ATestUtil.setUpSpring();
    }

    public void atestList() {
        ApiHandler handler = new ApiHandler();
        System.out.println(handler.query("com.zuiyidong.layer.restaurant", 0, 200, 0, 200, 100, 0, null, null));
    }

    public void testPerformance() throws Exception {
        ArrayList<TestThread> tr = new ArrayList<TestThread>();
        for (int i = 0; i < 20; i++) {
            TestThread s = new TestThread();
            tr.add(s);
            new Thread(s).start();
        }
        long start = System.currentTimeMillis();
        while (true) {
            try {
                Thread.sleep(1000);
                int total = 0;
                for (TestThread ts : tr) {
                    total = ts.count + total;
                }
                System.out.println("request/second = " + (total / ((System.currentTimeMillis() - start) / 1000))+", total request "+total );
            } catch (Exception e) {
            }
        }
    }

}

class TestThread implements Runnable {
    public int count = 0;

    public void run() {
        while (true) {
            try {
                query();
            } catch (Exception e) {
                e.printStackTrace();
            }
            count++;
        }
    }

    public void query() throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("layer", "com.zuiyidong.layer.restaurant");
        double lng = (double) CommonTestUtil.nextInt(90);
        double lat = (double) CommonTestUtil.nextInt(180);
        params.put("lng", Double.toString(lng) + "," + Double.toString(lng + CommonTestUtil.randomDouble() * 10));
        params.put("lat", "0,90");
        params.put("start", Integer.toString(CommonTestUtil.nextInt(10)));
        params.put("count", Integer.toString(CommonTestUtil.nextInt(20)));
        String s = HttpTestUtil.httpGetForString(ATestConstants.SERVER_URL + "/service/api", params);
        if (s.indexOf("xml") <= 0) {
            System.err.println("error :  " + s);
        }
    }
}

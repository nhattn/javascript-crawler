package com.zyd.web.api;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;

import com.tj.common.util.test.CommonTestUtil;
import com.tj.common.util.test.HttpTestUtil;
import com.zyd.ATestUtil;
import com.zyd.core.access.AuthorizationController;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.access.ClientInfo;
import com.zyd.core.util.SpringContext;

public class TestApiCall extends TestCase {
    public static String host = "localhost:8080";
    public static String clientId = "testx";
    public static String clientKey = "testx";

    @Override
    protected void setUp() throws Exception {
        ATestUtil.setUpSpring();
    }

    public void testClientKeyLogin() throws Exception {
        HibernateUtil.deleteAllObject("ClientInfo");
        // create client
        AuthorizationController ac = (AuthorizationController) (SpringContext.getContext().getBean("authorizationController"));
        ClientInfo cinfo = new ClientInfo();
        cinfo.setClientId(clientId);
        cinfo.setClientkey(clientKey);
        ac.createClientInfo(cinfo);

        SimpleTestThread.auth();
        SimpleTestThread.query();
        HibernateUtil.deleteAllObject("ClientInfo");
    }

    public void notTestPerformance() throws Exception {
        AuthorizationController ac = (AuthorizationController) (SpringContext.getContext().getBean("authorizationController"));
        ClientInfo cinfo = new ClientInfo();
        cinfo.setClientId(clientId);
        cinfo.setClientkey(clientKey);
        ac.createClientInfo(cinfo);
        ArrayList<SimpleTestThread> tr = new ArrayList<SimpleTestThread>();
        for (int i = 0; i < 20; i++) {
            SimpleTestThread s = new SimpleTestThread();
            tr.add(s);
            new Thread(s).start();
        }
        long start = System.currentTimeMillis();
        while (true) {
            try {
                Thread.sleep(1000);
                int total = 0;
                for (SimpleTestThread ts : tr) {
                    total = ts.count + total;
                }
                System.out.println("request/second = " + (total / ((System.currentTimeMillis() - start) / 1000)) + ", total request " + total);
            } catch (Exception e) {
            }
        }
    }

    static class SimpleTestThread implements Runnable {
        public int count = 0;

        public void run() {

            try {
                auth();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            while (true) {
                try {
                    query();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                count++;
            }
        }

        public static void auth() throws Exception {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("clientId", TestApiCall.clientId);
            params.put("clientKey", TestApiCall.clientKey);
            String s = HttpTestUtil.httpPostForString("http://" + TestApiCall.host + "/service/auth", params);
            assertTrue(s, s.indexOf("OK") >= 0);
        }

        public static void query() throws Exception {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("layer", "com.zuiyidong.layer.restaurant");
            double lng = (double) CommonTestUtil.nextInt(90);
            double lat = (double) CommonTestUtil.nextInt(180);
            params.put("lng", Double.toString(lng) + "," + Double.toString(lng + CommonTestUtil.randomDouble() * 10));
            params.put("lat", "0,90");
            params.put("start", Integer.toString(CommonTestUtil.nextInt(10)));
            params.put("count", Integer.toString(CommonTestUtil.nextInt(20)));
            params.put("clientId", TestApiCall.clientId);
            String s = HttpTestUtil.httpGetForString("http://" + TestApiCall.host + "/service/api", params);
            assertTrue(s.indexOf("xml") > 0);

        }
    }

}

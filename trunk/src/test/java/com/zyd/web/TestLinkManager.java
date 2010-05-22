package com.zyd.web;

import java.util.HashMap;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tj.common.util.test.HttpTestUtil;
import com.zyd.ATestConstants;
import com.zyd.ATestUtil;
import com.zyd.Constants;

public class TestLinkManager extends TestCase {
    int expire = 5 * 1000;
    int sleep = 5 * 1000;

    @Override
    protected void setUp() throws Exception {
        assertTrue(ATestUtil.clearServerData());
        createSomeLinks();
    }

    public void testLinkProcessingExpire() throws Exception {
        String s = HttpTestUtil.httpGetForString(Constants.ServerUrl + "/service/controller?action=UpdateLinkScannerParameter&expire=" + expire + "&sleep=" + sleep, null);
        JSONObject o = new JSONObject(s);
        assertTrue(o.getBoolean("result"));
        for (int i = 0; i < 5; i++) {
            s = HttpTestUtil.httpGetForString(Constants.ServerUrl + "/service/link?action=redirect", null);
            assertNotNull(s);
            assertTrue(s.indexOf("window.location") > 0);
        }
        try {
            System.err.println("Wait for thread sleep, make sure LinkManager is going through enough cycles");
            Thread.sleep(sleep * 5);
        } catch (Exception e) {
        }
        s = HttpTestUtil.httpGetForString(Constants.ServerUrl + "/service/controller?action=LinkSnapshot", null);
        assertNotNull(s);
        s = s.replaceAll(" ", "");
        assertTrue(s, s.indexOf("Error:5") > 0);
    }

    public static void createSomeLinks() throws Exception {
        JSONArray arr = new JSONArray();
        for (int i = 0; i < ATestConstants.TEST_LINK_COUNT; i++) {
            arr.put("http://www.zuiyidong.com/link_" + i);
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("data", arr.toString());
        String s = HttpTestUtil.httpPostForString(ATestConstants.SERVICE_LINK_URL, params);
        JSONObject o = new JSONObject(s);
        assertEquals(s, ATestConstants.TEST_LINK_COUNT, o.getInt("result"));
    }

    public static void main(String[] args) {
        System.out.println("add   bb".replaceAll(" ", ""));
    }
}

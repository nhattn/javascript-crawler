package com.zyd.web.service;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.zyd.ncore.busi.ATestUtil;

public class TestTestUtil extends TestCase {

    public void atestHttpGet() {
        String url = "http://localhost:8080/crawler/service/controller";
        Map<String, String> ps = new HashMap<String, String>();
        ps.put("action", "actionvaleu");
        System.out.println(ATestUtil.getAndGetString(url, ps));
    }

    public void atestHttpPost() {
        String url = "http://localhost:8080/crawler/service/controller";
        Map<String, String> ps = new HashMap<String, String>();
        ps.put("action", "actionvaleu");
        System.out.println(ATestUtil.postAndGetString(url, ps));
    }

    public void atestClearCache() throws Exception {
        assertTrue(ATestUtil.clearServerData());
    }
}

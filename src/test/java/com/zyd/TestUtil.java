package com.zyd;

import org.json.JSONObject;

import com.tj.common.util.test.HttpTestUtil;

public class TestUtil {
    public static boolean clearServerData() throws Exception {
        String s = HttpTestUtil.httpGetForString(Config.ServerUrl + "/service/controller?action=ClearAllData", null);
        JSONObject o = new JSONObject(s);
        return o.getBoolean("result");
    }
}

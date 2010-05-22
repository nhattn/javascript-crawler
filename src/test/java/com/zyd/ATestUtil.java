package com.zyd;

import java.util.HashMap;

import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tj.common.util.test.HttpTestUtil;

public class ATestUtil {
    public static boolean clearServerData() throws Exception {
        String s = HttpTestUtil.httpGetForString(Constants.ServerUrl + "/service/controller?action=ClearAllData", null);
        JSONObject o = new JSONObject(s);
        return o.getBoolean("result");
    }

    public static boolean createObject(HashMap v) throws Exception {
        String r = HttpTestUtil.httpPostForString(ATestConstants.SERVICE_OBJECT_URL, v);
        JSONObject obj = new JSONObject(r);
        return obj.getBoolean("result");
    }

    public static ApplicationContext setUpSpring() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:**/ContextConfig.xml");
        return ctx;
    }

    public static void main(String[] args) {
        // 6,371km

        //        double lat1 = -121.903938;
        //        double lat2 = -121.924778;
        //        double lon1 = 37.250742;
        //        double lon2 = 37.250742;

        double lat1 = -122.200284;
        double lat2 = -122.200198;
        double lon1 = 39.913423;
        double lon2 = 39.814536;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        System.out.println(6371 * 1000 * c);
    }
}

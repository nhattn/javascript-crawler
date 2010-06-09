package com.zyd;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tj.common.util.test.CommonTestUtil;
import com.tj.common.util.test.HttpTestUtil;
import com.zyd.core.objecthandler.House;
import com.zyd.web.TestObjectManipulation;

@SuppressWarnings("unchecked")
public class ATestUtil {
    static {
        if (Constants.APPLICATION_CONTEXT == null) {
        }
    }

    public static boolean clearServerData() throws Exception {
        String s = HttpTestUtil.httpGetForString(Constants.ServerUrl + "/service/controller?action=ClearAllData", null);
        JSONObject o = new JSONObject(s);
        return o.getBoolean("result");
    }

    public static ApplicationContext setUpSpring() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:**/ContextConfig.xml");
        return ctx;
    }

    public static boolean updateServerConfigure(HashMap<String, String> propertyValues) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(bos));
        for (String k : propertyValues.keySet()) {
            writer.write(k);
            writer.write("=");
            writer.write(propertyValues.get(k));
            writer.newLine();
        }
        writer.close();
        bos.close();
        String content = bos.toString();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("content", content);
        params.put("action", "UpdateConfigure");
        String r = HttpTestUtil.httpPostForString(ATestConstants.CONTROLLER_URL, params);
        JSONObject obj = new JSONObject(r);
        return obj.getBoolean("result");
    }

    public static boolean reststoreServerConfigure() throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("action", "ReloadConfigure");
        String r = HttpTestUtil.httpPostForString(ATestConstants.CONTROLLER_URL, params);
        JSONObject obj = new JSONObject(r);
        return obj.getBoolean("result");
    }

    public static HashMap<String, String> getServerConfigure() throws Exception {
        HashMap<String, String> r = new HashMap<String, String>();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("action", "ConfigureSnapshot");
        String s = HttpTestUtil.httpGetForString(ATestConstants.CONTROLLER_URL, params);
        StringTokenizer tokens = new StringTokenizer(s, "\n");
        while (tokens.hasMoreElements()) {
            String token = tokens.nextToken().trim();
            if (token.length() == 0) {
                continue;
            }
            StringTokenizer ts = new StringTokenizer(token, ":");
            if (ts.countTokens() != 2) {
                continue;
            }
            String ka = ts.nextToken().trim();
            String kb = ts.nextToken().trim();
            r.put(ka, kb);
        }
        return r;
    }

    public static void main(String[] args) {

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

    /**
     * NOTE : this have some problem.
     * @param v
     * @param referer
     * @return
     * @throws Exception
     */
    public static boolean createObject(Map v, String referer) throws Exception {
        createLink(referer);
        String l = getNextLink();
        String r = HttpTestUtil.httpPostForString(ATestConstants.SERVICE_OBJECT_URL, v, l);
        JSONObject obj = new JSONObject(r);
        return obj.getBoolean("result");
    }

    public static boolean createObject(Map v) throws Exception {
        return createObject(v, ATestConstants.OBJECT_REFERER_PREFIX + CommonTestUtil.getNonRepeatString());
    }

    public static int createSomeObject() throws Exception {
        Map map = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectManipulation.class, TestObjectManipulation.testFile1, Constants.Encoding_DEFAULT_SYSTEM);
        Set keys = map.keySet();
        Set changableValues = new HashSet();
        changableValues.add("address");
        changableValues.add("description2");
        changableValues.add("district1");
        changableValues.add("district5");
        changableValues.add("equipment");
        int num = ATestConstants.TEST_OBJECT_COUNT;
        for (int i = 0; i < num; i++) {
            HashMap nv = new HashMap();
            for (Object k : keys) {
                String s = (String) k;
                String value = (String) map.get(k);
                if (changableValues.contains(k) == true) {
                    nv.put(s, i + " -- " + value);
                } else {
                    nv.put(k, value);
                }
            }
            nv.put(House.Columns.Price, (1000 + i) + "");
            nv.put(House.Columns.Long, i + "");
            nv.put(House.Columns.Lat, i + "");
            nv.put(House.Columns.RentalType, new String[] { "出租", "出售", "合租", "短租" }[i % 4]);
            String referer = ATestConstants.OBJECT_REFERER_PREFIX + i;
            if (createObject(nv, referer) == false) {
                throw new Exception("Can not create object :" + nv.toString());
            }
        }
        return num;
    }

    public static boolean createLink(String link) throws Exception {
        JSONArray arr = new JSONArray();
        arr.put(link);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("data", arr.toString());
        String s = HttpTestUtil.httpPostForString(ATestConstants.SERVICE_LINK_URL, params);
        JSONObject o = new JSONObject(s);
        if (1 != o.getInt("result")) {
            throw new Exception("Links are not created: raw response is: " + s);
        }
        return true;
    }

    public static int createSomeLinks() throws Exception {
        for (int i = 0; i < ATestConstants.TEST_LINK_COUNT; i++) {
            if (createLink(ATestConstants.OBJECT_REFERER_PREFIX + i) == false) {
                throw new Exception("Can not create link");
            }
        }
        return ATestConstants.TEST_LINK_COUNT;
    }

    public static String getNextLink() throws Exception {
        String s = null;
        s = HttpTestUtil.httpGetForString(Constants.ServerUrl + "/service/link?action=redirect", null);
        if (s == null) {
            throw new Exception("Can not get next link");
        }
        int i = s.indexOf("window.location");
        if (i == -1) {
            throw new Exception("Can not get next link: " + s);
        }
        i = s.indexOf("\'", i + 1);

        if (i == -1) {
            throw new Exception("Can not get next link: " + s);
        }
        int j = s.indexOf("\'", i + 1);
        if (j == -1) {
            throw new Exception("Can not get next link: " + s);
        }
        return s.substring(i + 1, j);
    }

}

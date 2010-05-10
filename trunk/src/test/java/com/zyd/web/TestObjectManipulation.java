package com.zyd.web;

import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.tj.common.util.test.CommonTestUtil;
import com.tj.common.util.test.HttpTestUtil;
import com.zyd.ATestConstants;
import com.zyd.ATestUtil;
import com.zyd.Config;
import com.zyd.core.objecthandler.Handler;
import com.zyd.core.objecthandler.House;

@SuppressWarnings("unchecked")
public class TestObjectManipulation extends TestCase {
    public static String testFile1 = "house1.prop";
    public static String testFile2 = "house2.prop";

    @Override
    protected void setUp() throws Exception {
        assertTrue(ATestUtil.clearServerData());
    }

    public void testCreateDuplicates() throws Exception {
        HashMap val = getHouseConfig();
        assertTrue(ATestUtil.createObject(val));

        //  send again
        val = getHouseConfig();
        assertFalse(ATestUtil.createObject(val));

        // change address but number is the same 
        val = getHouseConfig();
        val.put(House.Columns.Address, "上海市陆家嘴路7号8单元201");
        assertFalse(ATestUtil.createObject(val));

        //  change tel number
        val = getHouseConfig();
        val.put(House.Columns.Tel, "1112233");
        assertTrue(ATestUtil.createObject(val));

        // change address
        val = getHouseConfig();
        val.put(House.Columns.Address, "上海市陆家嘴路七号八单元204");
        assertTrue(ATestUtil.createObject(val));
    }

    public void testCreateObjectWithNoImageTel() throws Exception {
        Map map = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectManipulation.class, testFile1, "GBK");
        String r = HttpTestUtil.httpPostForString(ATestConstants.SERVICE_OBJECT_URL, map);
        JSONObject obj = new JSONObject(r);
        assertTrue(obj.getBoolean("result"));
    }

    public void testCreateObjectWithImageTel() throws Exception {
        Map map = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectManipulation.class, testFile2, "GBK");
        String r = HttpTestUtil.httpPostForString(ATestConstants.SERVICE_OBJECT_URL, map);
        JSONObject obj = new JSONObject(r);
        assertTrue(obj.getBoolean("result"));
    }

    public void testCreateObjectFail() throws Exception {
        {
            Map map = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectManipulation.class, testFile2, "GBK");
            map.remove(House.Columns.Lat);
            String r = HttpTestUtil.httpPostForString(ATestConstants.SERVICE_OBJECT_URL, map);
            JSONObject obj = new JSONObject(r);
            assertFalse(obj.getBoolean("result"));
        }
        {
            Map map = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectManipulation.class, testFile2, "GBK");
            map.remove(House.Columns.Long);
            String r = HttpTestUtil.httpPostForString(ATestConstants.SERVICE_OBJECT_URL, map);
            JSONObject obj = new JSONObject(r);
            assertFalse(obj.getBoolean("result"));
        }
        {
            Map map = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectManipulation.class, testFile2, "GBK");
            map.remove(House.Columns.Address);
            String r = HttpTestUtil.httpPostForString(ATestConstants.SERVICE_OBJECT_URL, map);
            JSONObject obj = new JSONObject(r);
            assertFalse(obj.getBoolean("result"));
        }
    }

    public static int createSomeObject() throws Exception {
        Map map = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectManipulation.class, testFile1, "GBK");
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
            String r = HttpTestUtil.httpPostForString(ATestConstants.SERVICE_OBJECT_URL, nv);
            JSONObject obj = new JSONObject(r);
            assertTrue(obj.getBoolean("result"));
        }
        return num;
    }

    public static void testCreateCustomObject() throws Exception {
        createSomeObject();
    }

    public void testListObject() throws Exception {
        int num = createSomeObject();
        String s = HttpTestUtil.httpGetForString(ATestConstants.SERVICE_OBJECT_URL + "?objectid=House", null);
        assertNotNull(s);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(s));
        Document docx = db.parse(is);
        NodeList nodes = docx.getElementsByTagName("object");
        assertEquals(nodes.getLength(), Config.LENGTH_PAGE_SIZE);
    }

    private static HashMap getHouseConfig() {
        HashMap val = new HashMap(), oldval;
        val.put(House.Columns.Tel, "13911212");
        val.put(House.Columns.Long, "31.111");
        val.put(House.Columns.Lat, "31.111");
        val.put(House.Columns.Address, "上海市陆家嘴路七号八单元201");
        val.put(Handler.Parameter.PARAMETER_OBJECT_ID, "House");
        return val;
    }

}

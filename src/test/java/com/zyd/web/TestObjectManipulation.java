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
import com.zyd.TestConstants;
import com.zyd.TestUtil;

@SuppressWarnings("unchecked")
public class TestObjectManipulation extends TestCase {
    public static String testFile1 = "house1.prop";
    public static String testFile2 = "house2.prop";

    @Override
    protected void setUp() throws Exception {
        assertTrue(TestUtil.clearServerData());
    }

    public void testCreateObjectWithNoImageTel() throws Exception {
        Map map = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectManipulation.class, testFile1, "GBK");
        String r = HttpTestUtil.httpPostForString(TestConstants.SERVICE_OBJECT_URL, map);
        JSONObject obj = new JSONObject(r);
        assertTrue(obj.getBoolean("result"));
    }

    public void testCreateObjectWithImageTel() throws Exception {
        Map map = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectManipulation.class, testFile2, "GBK");
        String r = HttpTestUtil.httpPostForString(TestConstants.SERVICE_OBJECT_URL, map);
        JSONObject obj = new JSONObject(r);
        assertTrue(obj.getBoolean("result"));
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
        int num = 40;
        for (int i = 0; i < num; i++) {
            HashMap nv = new HashMap();
            for (Object k : keys) {
                String s = (String) k;
                String value = (String) map.get(k);
                if (changableValues.contains(k) == true) {
                    nv.put(s, i + " -- " + value);
                } else if ("lo".equals(s) || "la".equals(s)) {
                    nv.put(s, Double.toString(Double.parseDouble(value) + CommonTestUtil.nextInt(10) + CommonTestUtil.randomDouble()));
                } else {
                    nv.put(k, value);
                }
            }
            String r = HttpTestUtil.httpPostForString(TestConstants.SERVICE_OBJECT_URL, nv);
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
        String s = HttpTestUtil.httpGetForString(TestConstants.SERVICE_OBJECT_URL + "?objectid=House", null);
        assertNotNull(s);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(s));
        Document docx = db.parse(is);
        NodeList nodes = docx.getElementsByTagName("object");
        assertEquals(num, nodes.getLength());
    }

}

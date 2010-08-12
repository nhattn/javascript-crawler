package com.zyd.web.house;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.tj.common.util.test.CommonTestUtil;
import com.tj.common.util.test.HttpTestUtil;
import com.zyd.ATestConstants;
import com.zyd.ATestUtil;
import com.zyd.Constants;
import com.zyd.core.objecthandler.Handler;
import com.zyd.core.objecthandler.House;

@SuppressWarnings("unchecked")
public class TestObjectManipulation extends TestCase {
    public static String testFile1 = "house1.prop";
    public static String testFile2 = "house2.prop";

    @Override
    protected void setUp() throws Exception {
        assertTrue(ATestUtil.clearServerData("House"));
        assertTrue(ATestUtil.clearServerData("Link"));
    }

    public void testCreateDuplicatesHouses() throws Exception {
        String link1 = "http://www.test.com/href1";
        String link2 = "http://www.test.com/href2";

        // first attempt
        HashMap val = getHouseConfig();
        assertTrue(ATestUtil.createObject(val, link1));

        //  same object, different url,  work
        val = getHouseConfig();
        assertTrue(ATestUtil.createObject(val, link2));
    }

    public void testCreateObjectWithNoImageTel() throws Exception {
        Map map = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectManipulation.class, testFile1, Constants.Encoding_DEFAULT_SYSTEM);
        assertTrue(ATestUtil.createObject(map));
    }

    public void testCreateObjectWithImageTel() throws Exception {
        Map map = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectManipulation.class, testFile2, Constants.Encoding_DEFAULT_SYSTEM);
        assertTrue(ATestUtil.createObject(map));
    }

 

    public void testCreateObjectFail() throws Exception {
        Map map = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectManipulation.class, testFile2, Constants.Encoding_DEFAULT_SYSTEM);
        map.remove(House.Columns.Address);
        assertTrue(ATestUtil.createObject(map)); // not anymore
    }

    public static void testCreateCustomObject() throws Exception {
        ATestUtil.createSomeObject();
    }

    public void testListObject() throws Exception {
        int num = ATestUtil.createSomeObject();
        String s = HttpTestUtil.httpGetForString(ATestConstants.SERVICE_OBJECT_URL + "?objectid=House", null);
        assertNotNull(s);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(s));
        Document docx = db.parse(is);
        NodeList nodes = docx.getElementsByTagName("object");
        assertEquals(nodes.getLength(), Constants.LENGTH_PAGE_SIZE);
    }

    private static HashMap getHouseConfig() {
        HashMap val = new HashMap();
        val.put(House.Columns.Tel, "13911212");
        val.put(House.Columns.Lng, "31.111");
        val.put(House.Columns.Lat, "31.111");
        val.put(House.Columns.Address, "上海市陆家嘴路七号八单元201");
        val.put(Handler.Parameter.PARAMETER_OBJECT_ID, "House");
        return val;
    }

}

package com.zyd.web;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.tj.common.util.test.CommonTestUtil;
import com.tj.common.util.test.HttpTestUtil;
import com.zyd.ATestConstants;
import com.zyd.ATestUtil;
import com.zyd.Constants;
import com.zyd.core.objecthandler.Handler;
import com.zyd.core.objecthandler.House;
import com.zyd.core.objecthandler.House.Columns;

public class TestObjectQuery extends TestCase {
    @Override
    protected void setUp() throws Exception {
        assertTrue(ATestUtil.clearServerData());
        ATestUtil.stopReturningWatchedLink();
    }

    public void testQueryObject() throws Exception {
        ATestUtil.createSomeObject();
        HashMap<String, String> p = new HashMap<String, String>();
        p.put("lo", "0-18");
        p.put("la", "0-18");
        p.put("objectid", "House");
        String s = HttpTestUtil.httpGetForString(ATestConstants.SERVICE_OBJECT_URL, p);
        assertNotNull(s);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(s));
        Document docx = db.parse(is);
        NodeList nodes = docx.getElementsByTagName("object");
        assertEquals(19, nodes.getLength());
    }

    public void testQueryPagingAndSorting() throws Exception {
        ATestUtil.createSomeObject();
        HashMap<String, String> p = new HashMap<String, String>();
        int pageSize = 5, start = 0;
        p.put("objectid", "House");
        p.put("count", "" + pageSize);
        p.put(Handler.Parameter.PARAMETER_ORDER_BY, House.Columns.Long);
        p.put(Handler.Parameter.PARAMETER_ORDER, Handler.Parameter.PARAMETER_VALUE_ORDER_ASC);

        int totalPage = (int) (ATestConstants.TEST_OBJECT_COUNT / pageSize) + (ATestConstants.TEST_OBJECT_COUNT % pageSize == 0 ? 0 : 1);
        for (int i = 0; i < totalPage; i++) {
            p.put("start", "" + start);
            start = start + pageSize;
            String s = HttpTestUtil.httpGetForString(ATestConstants.SERVICE_OBJECT_URL, p);
            assertNotNull(s);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(s));
            Document docx = db.parse(is);
            NodeList nodes = docx.getElementsByTagName("object");
            assertTrue((nodes.getLength() == pageSize) || (nodes.getLength() == ATestConstants.TEST_OBJECT_COUNT % pageSize));
            NodeList longs = docx.getElementsByTagName(House.Columns.Long);
            Double ld = -1d;
            for (int y = 0; y < longs.getLength(); y++) {
                Node n = longs.item(y);
                double textContentDouble = Double.parseDouble(n.getTextContent());
                assertTrue(textContentDouble > ld);
                ld = textContentDouble;
            }
        }

        p.put("start", "" + start);
        String s = HttpTestUtil.httpGetForString(ATestConstants.SERVICE_OBJECT_URL, p);
        assertNotNull(s);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(s));
        Document docx = db.parse(is);
        NodeList nodes = docx.getElementsByTagName("object");
        assertEquals(0, nodes.getLength());
    }

    public void testQueryByPrice() throws Exception {
        ATestUtil.createSomeObject();
        HashMap<String, String> p = new HashMap<String, String>();
        int pageSize = 5, start = 0;
        p.put("objectid", "House");
        p.put("count", "" + pageSize);
        p.put(House.Columns.Price, "1001-1050");

        p.put(Handler.Parameter.PARAMETER_START, "0");
        p.put(Handler.Parameter.PARAMETER_COUNT, "100000");
        String s = HttpTestUtil.httpGetForString(ATestConstants.SERVICE_OBJECT_URL, p);
        assertNotNull(s);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(s));
        Document docx = db.parse(is);
        NodeList nodes = docx.getElementsByTagName("object");
        assertEquals(50, nodes.getLength());
        nodes = docx.getElementsByTagName("price");
        for (int i = 0; i < nodes.getLength(); i++) {
            double d = Double.parseDouble(nodes.item(i).getTextContent());
            assertTrue(d >= 1000d);
            assertTrue(d <= 1050d);
        }
    }

    public void testQueryByRentalType() throws Exception {
        ATestUtil.createSomeObject();
        HashMap<String, String> p = new HashMap<String, String>();
        String[] types = new String[] { "出租", "出售", "合租", "短租" };
        int total = 0;
        for (int i = 0; i < types.length; i++) {
            p.clear();
            p.put("objectid", "House");
            p.put(House.Columns.RentalType, URLEncoder.encode(types[i], "UTF-8"));
            p.put(Handler.Parameter.PARAMETER_COUNT, "1000");
            String s = HttpTestUtil.httpGetForString(ATestConstants.SERVICE_OBJECT_URL, p);
            assertNotNull(s);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(s));
            Document docx = db.parse(is);
            NodeList nodes = docx.getElementsByTagName("object");
            int length = nodes.getLength();
            assertTrue(length > 0);
            total = total + length;
        }
        assertEquals(ATestConstants.TEST_OBJECT_COUNT, total);
    }

    public void testQueryByLike() throws Exception {
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap map = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectManipulation.class, TestObjectManipulation.testFile1, Constants.Encoding_DEFAULT_SYSTEM);
        String[] description = new String[] { "小区位于金汇路上，旁边有虹桥世贸购物中心和大通阳商场", "宛南六村南摸学区房闹中取静新装修", "上海浦江世博家园二街坊朝南好房出租", "精装修房..新装修" };
        for (String s : description) {
            HashMap m = (HashMap) map.clone();
            m.put(Columns.Description2, s);
            ATestUtil.createObject(m);
        }
        HashMap<String, String> p = new HashMap<String, String>();
        int total = 0;
        for (int i = 0; i < description.length; i++) {
            p.clear();
            p.put("objectid", "House");
            p.put(House.Columns.Description2, URLEncoder.encode("%" + description[i].substring(3, 5) + "%", "UTF-8"));
            p.put(Handler.Parameter.PARAMETER_COUNT, "1000");
            String s = HttpTestUtil.httpGetForString(ATestConstants.SERVICE_OBJECT_URL, p);
            assertNotNull(s);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(s));
            Document docx = db.parse(is);
            NodeList nodes = docx.getElementsByTagName("object");
            int length = nodes.getLength();
            assertTrue(length > 0);
            total = total + length;
        }
        assertEquals(description.length, total);
    }

}

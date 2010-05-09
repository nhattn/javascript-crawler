package com.zyd.web;

import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.tj.common.util.test.HttpTestUtil;
import com.zyd.TestConstants;
import com.zyd.TestUtil;

import junit.framework.TestCase;

public class TestObjectQuery extends TestCase {
    @Override
    protected void setUp() throws Exception {
        assertTrue(TestUtil.clearServerData());
    }

    public void testQueryObject() throws Exception {
        TestObjectManipulation.createSomeObject();
        HashMap<String, String> p = new HashMap<String, String>();
        p.put("lo", "31.0-33.0");
        p.put("la", "121-123");
        p.put("objectid", "House");
        String s = HttpTestUtil.httpGetForString(TestConstants.SERVICE_OBJECT_URL, p);
        assertNotNull(s);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(s));
        Document docx = db.parse(is);
        NodeList nodes = docx.getElementsByTagName("object");
        System.out.println(nodes.getLength());
    }
}

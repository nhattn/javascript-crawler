package com.zyd.web;

import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.tj.common.util.test.CommonTestUtil;
import com.tj.common.util.test.HttpTestUtil;
import com.zyd.ATestConstants;
import com.zyd.ATestUtil;
import com.zyd.core.objecthandler.AppLog;
import com.zyd.core.objecthandler.Handler;

public class TestAppLog extends TestCase {
    @Override
    protected void setUp() throws Exception {
        assertTrue(ATestUtil.clearServerData());
    }

    public static String httpPut(String urls, Map params, String referer) throws Exception {
        StringBuffer buf = new StringBuffer(urls);
        buf.append('?');
        for (Object o : params.keySet()) {
            buf.append(o.toString());
            buf.append('=');
            buf.append(params.get(o));
            buf.append('&');
        }
        buf.deleteCharAt(buf.length() - 1);
        URL url = new URL(buf.toString());
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setRequestMethod("PUT");
        String s = IOUtils.toString(httpCon.getInputStream());
        return s;
    }

    public void testLog() throws Exception {
        Map values = CommonTestUtil.loadValueMapFromClassPathFile(TestAppLog.class, "applog.prop", Charset.defaultCharset().toString());
        String s = httpPut(ATestConstants.APPLOG_URL, values, null);//HttpTestUtil.httpPutForString(ATestConstants.APPLOG_URL, values, null);
        assertEquals("ok", s.trim());
        values = new HashMap();
        values.put(Handler.Parameter.PARAMETER_OBJECT_ID, AppLog.name);
        s = HttpTestUtil.httpGetForString(ATestConstants.APPLOG_URL, values);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(s));
        Document docx = db.parse(is);
        NodeList nodes = docx.getElementsByTagName("object");
        assertEquals(1, nodes.getLength());
    }
}

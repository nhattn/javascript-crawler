package com.zyd.web;

import java.io.InputStream;
import java.util.HashMap;

import junit.framework.TestCase;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;

import com.tj.common.CommonUtil;
import com.tj.common.util.test.CommonTestUtil;
import com.zyd.ATestConstants;
import com.zyd.ATestUtil;
import com.zyd.core.objecthandler.Handler;
import com.zyd.core.objecthandler.House;

@SuppressWarnings("unchecked")
public class TestEncoding extends TestCase {
    static String referer = "http://localhost.com/testreferer.html";
    static String[] encoding = new String[] { "GBK", "ISO-8859-1", "UTF-8" };
    HashMap<String, String> params = null;

    @Override
    protected void setUp() throws Exception {
        ATestUtil.clearServerData();
        params = new HashMap<String, String>();
        params.put(House.Columns.Description1, "你应该看到这段中文文");
        params.put(Handler.Parameter.PARAMETER_OBJECT_ID, House.name);
        params.put(House.Columns.Lat, "11.11");
        params.put(House.Columns.Long, "22.22");
        params.put(House.Columns.Tel, "2222");
        params.put(House.Columns.Address, "2222");
    }

    public void testPost() throws Exception {
        for (int i = 0; i < encoding.length; i++) {
            ATestUtil.createLink(referer + i);
            String l = ATestUtil.getNextLink();
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                // TODO: handle exception
            }
            String charset = encoding[i];
            doPost(ATestConstants.SERVICE_OBJECT_URL, charset, "application/x-www-form-urlencoded", l, params);
        }
    }

    public static String doPost(String url, String charset, String contentType, String referer, HashMap params) throws Exception {
        String r = null;
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);
        method.addRequestHeader(new Header("Referer", referer));
        method.addRequestHeader(new Header("Content-Type", contentType + "; charset=" + charset));
        NameValuePair[] ps = new NameValuePair[params.size()];
        int counter = 0;
        System.out.println(params);
        for (Object sk : params.keySet()) {
            String k = (String) sk;
            if (charset == null) {
                ps[counter++] = new NameValuePair(k, (String) params.get(k));
            } else {
                String ns = new String(((String) params.get(k)).getBytes("iso-8859-1"), charset);
                ps[counter++] = new NameValuePair(k, ns);
            }
        }
        method.setRequestBody(ps);
        int statusCode = client.executeMethod(method);
        if (statusCode != HttpStatus.SC_OK) {
            throw new Exception("Http request returns status code other than OK: " + statusCode + ", url is " + url);
        }
        InputStream ins = method.getResponseBodyAsStream();
        r = IOUtils.toString(ins);
        ins.close();
        method.releaseConnection();
        return r;
    }

    //"Content-Type: text/html; charset=UTF-8"
}

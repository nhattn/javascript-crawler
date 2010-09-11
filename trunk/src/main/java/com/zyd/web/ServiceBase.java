package com.zyd.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zyd.Constants;
import com.zyd.core.Utils;
import com.zyd.core.dom.XmlParcel;
import com.zyd.core.objecthandler.SearchResult;

public class ServiceBase {
    private static Logger logger = Logger.getLogger(ServiceBase.class);
    public static String RESULT_NO_CHANGE;
    public static String RESULT_CHANGE;
    public final static String ResponseType_Html = "html";
    public final static String ResponseType_Xml = "xml";
    public final static String ResponseType_Js = "js";
    public final static String ResponseType_Text = "text";

    static {
        try {
            RESULT_NO_CHANGE = Utils.stringArrayToJsonString(new String[] { "result", "false" });
            RESULT_CHANGE = Utils.stringArrayToJsonString(new String[] { "result", "true" });
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        serviceNotFound(req, resp);
    }

    public void put(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        serviceNotFound(req, resp);
    }

    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        serviceNotFound(req, resp);
    }

    private void serviceNotFound(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();
        String msg = "The requested service: " + uri + ", method:" + req.getMethod() + " is not found.";
        logger.warn("Client requested a wrong uri : " + uri + ", client ip " + req.getRemoteAddr());
        resp.getWriter().write(msg);
    }

    /**
     * @param type one of "html", "xml", "js", "text"     
     */
    protected void setResponseType(String type, HttpServletResponse resp) {
        String types = ResponseTypes.get(type);
        if (types == null) {
            logger.debug("Invalid response type, will set to text, requested type is :" + type);
            types = ResponseTypes.get("text");
        }
        resp.setHeader("Content-Type", types);
    }

    protected void output(String s, String encoding, HttpServletResponse response) throws IOException {
        Writer writer = new OutputStreamWriter(response.getOutputStream(), encoding);
        writer.write(s);
        writer.flush();
        writer.close();
    }

    protected void setStatus(int code, String message, HttpServletResponse response) throws IOException {
        response.setStatus(code);
        response.getWriter().write(message);
    }

    protected void output(String s, HttpServletResponse response) throws IOException {
        output(s, "GBK", response);
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, String> requestParameterToMap(HttpServletRequest req) {
        HashMap<String, String> values = new HashMap<String, String>();
        Enumeration<String> names = req.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = req.getParameter(name);
            if (value == null) {
                continue;
            }
            value = value.trim();
            if (value.length() == 0) {
                continue;
            }
            values.put(name, value);
        }
        return values;
    }

    private static Map<String, String> ResponseTypes;

    static {
        ResponseTypes = new HashMap<String, String>();
        ResponseTypes.put(ResponseType_Html, "text/html; charset=" + Constants.ENCODING_OUT_GOING_CONTENT);
        ResponseTypes.put(ResponseType_Xml, "text/xml; charset=" + Constants.ENCODING_OUT_GOING_CONTENT);
        ResponseTypes.put(ResponseType_Js, "application/javascript; charset=" + Constants.ENCODING_OUT_GOING_CONTENT);
        ResponseTypes.put(ResponseType_Text, "text/plain; charset=" + Constants.ENCODING_OUT_GOING_CONTENT);
    }

    public static String toXmlString(SearchResult result, String encoding) {
        if (result == null)
            result = SearchResult.NullResult;
        List list = result.result;
        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buf.append("<objects start=\"" + result.start + "\" count=\"" + result.count + "\" total=\"" + result.totalResult + "\">");
        if (list != null && list.size() > 0) {
            if (list.get(0) instanceof XmlParcel) {
                for (Object p : list) {
                    buf.append(((XmlParcel) p).toXml());
                }
            } else {
                Set cdataColumns = result.cdataColumns;
                for (int i = 0, len = list.size(); i < len; i++) {
                    buf.append("<object>");
                    HashMap map = (HashMap) list.get(i);
                    String objectId = (String) map.remove("$type$");
                    buf.append("<type>");
                    buf.append(objectId);
                    buf.append("</type>");
                    Set keys = map.keySet();
                    for (Object k : keys) {
                        buf.append('<');
                        buf.append(k);
                        buf.append('>');
                        Object o = map.get(k);
                        if (o == null) {
                            o = "";
                        }
                        if (cdataColumns != null && cdataColumns.contains(k)) {
                            buf.append("<![CDATA[");
                            buf.append(o.toString());
                            buf.append("]]>");
                        } else {
                            buf.append(o.toString());
                        }
                        buf.append("</");
                        buf.append(k);
                        buf.append('>');
                    }
                    buf.append("</object>");
                }
            }
        }
        buf.append("</objects>");
        return buf.toString();
    }
}

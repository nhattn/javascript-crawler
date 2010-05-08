package com.zyd.web.service;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.Config;
import com.zyd.core.HandlerManager;
import com.zyd.core.busi.LinkManager;
import com.zyd.core.util.SpringContext;
import com.zyd.web.ServiceBase;

public class object extends ServiceBase {
    private LinkManager linkManager;

    public object() {
        linkManager = (LinkManager) SpringContext.getContext().getBean("linkManager");
    }

    /**
     * method: post
     * description: create a new object 
     * this will call appropriate handler based on the appid of the parameter
     * this will return a json object like such indicating wheather or not something has changed, i.e. the object
     * has been added.
     * {
     *  result: 'true'/'false'
     * }
     * 
     * 
     */
    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String referer = req.getHeader("Referer");
        if (referer != null && linkManager.isLinkProcessed(referer)==true) {
            setResponseType("js", resp);
            output(RESULT_NO_CHANGE, resp);
            return;
        }
        setResponseType("js", resp);
        HashMap<String, Object> values = new HashMap<String, Object>();
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

        boolean result = (Boolean) HandlerManager.getInstance().getHandler((String) values.get(Config.NAME_APP_PARAMETER)).process(values);
        if (result) {
            linkManager.linkProcessed(referer);
        }
        setResponseType("js", resp);
        output(RESULT_CHANGE, resp);
    }

    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String app = req.getParameter(Config.NAME_APP_PARAMETER);
        HashMap<String, String> params = new HashMap<String, String>();
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
            params.put(name, value);
        }
        List objects = HandlerManager.getInstance().getHandler(app).load(params);
        //        resp.getWriter().write(toXmlString(objects));
        resp.setContentType("text/xml");
        resp.getWriter().write(toXmlString(objects));
    }

    static String toXmlString(List list) {
        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        buf.append("<objects>\n");
        for (int i = 0, len = list.size(); i < len; i++) {
            buf.append("<object>");
            HashMap map = (HashMap) list.get(i);
            map.remove("$type$");
            Set keys = map.keySet();
            for (Object k : keys) {
                buf.append('<');
                buf.append(k);
                buf.append('>');
                Object o = map.get(k);
                if (o != null) {
                    try {
                        buf.append(o.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                buf.append("</");
                buf.append(k);
                buf.append('>');
            }
            buf.append("</object>\n");
        }
        buf.append("</objects>");
        return buf.toString();
    }
}

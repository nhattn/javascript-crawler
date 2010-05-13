package com.zyd.web.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import com.zyd.core.busi.LinkManager;
import com.zyd.core.objecthandler.ObjectManager;
import com.zyd.core.util.SpringContext;
import com.zyd.web.ServiceBase;

@SuppressWarnings("unchecked")
public class object extends ServiceBase {
    private LinkManager linkManager;
    private ObjectManager objectManager;

    public object() {
        linkManager = (LinkManager) SpringContext.getContext().getBean("linkManager");
        objectManager = (ObjectManager) SpringContext.getContext().getBean("objectManager");
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
     * Also, the referer url will be marked as processed.
     */
    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setResponseType("js", resp);
        String referer = req.getHeader("Referer");
        if (referer != null && linkManager.isLinkProcessing(referer) == false) {
            //TODO: should be strict like this: if (referer == null || linkManager.isLinkProcessing(referer) == false) {
            //this is a link that we didn't process, come from somewhere else. ignore
            output(RESULT_NO_CHANGE, resp);
        } else {
            HashMap values = requestParameterToMap(req);
            boolean result = (Boolean) objectManager.create(values);
            linkManager.linkFinished(referer);
            output(result ? RESULT_CHANGE : RESULT_NO_CHANGE, resp);
            if (result == false) {
                System.err.println("Failed to handle url - " + referer);
            }
        }
    }

    /**
     * Get a list of objects
     * TODO if there is no result how to notify
     */
    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/xml");
        HashMap params = requestParameterToMap(req);
        List objects = objectManager.query(params);
        resp.getWriter().write(toXmlString(objects));
    }

    static String toXmlString(List list) {
        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buf.append("<objects>");
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
                        //                        buf.append(StringEscapeUtils.escapeXml(o.toString()));
                        buf.append(o.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                buf.append("</");
                buf.append(k);
                buf.append('>');
            }
            buf.append("</object>");
        }
        buf.append("</objects>");
        return buf.toString();
    }
}

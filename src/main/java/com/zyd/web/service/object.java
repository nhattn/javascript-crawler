package com.zyd.web.service;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.Config;
import com.zyd.core.HandlerManager;
import com.zyd.core.Utils;
import com.zyd.web.ServiceBase;

public class object extends ServiceBase {
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
        setResponseType("js", resp);
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
                
        boolean result = (Boolean) HandlerManager.getInstance().getHandler(values.get(Config.NAME_APP_PARAMETER)).process(values);
        String s = Utils.stringArrayToJsonString(new String[] { "result", Boolean.toString(result) });
        output(s, resp);
    }
}

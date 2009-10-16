package com.zyd.web.service;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;

import com.zyd.ncore.Utils;
import com.zyd.ncore.busi.LinkManager;
import com.zyd.ncore.busi.TemplateManager;
import com.zyd.web.ServiceBase;

public class link extends ServiceBase {
    /**
     * method: get
     * description: get link to crawl
     * parameter :action, list - shows the current list,
     *                    get  - get a link, this will remove the link as well
     *                    redirect - returns an html page that automatically redirect to next link.
     *            count, how many links to return, if action is list, default to 20, if action is get, default to 1. -1 will return all.
     *            
     *          
     */
    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action"), counts = req.getParameter("count");
        LinkManager lm = LinkManager.getInstance();
        String s = "";
        if (StringUtils.isNotBlank(action) ==false|| (action.equals("list") == false && action.equals("get") == false && action.equals("redirect") == false)) {
            action = "list";
        }
        if (StringUtils.isNotBlank(counts) == false || StringUtils.isNumeric(counts) == false) {
            if (action.equals("list"))
                counts = "20";
            else
                counts = "1";
        }

        if ("list".equals(action)) {
            //TODO: count is ignored
            setResponseType("text", resp);
            s = Utils.stringToFlatList(lm.getAllLinks());
        } else if ("get".equals(action)) {
            setResponseType("js", resp);
            s = Utils.stringArrayToJsonString(new String[] { "result", lm.nextLink() });
        } else if ("redirect".equals(action)) {
            setResponseType("html", resp);
            String l = lm.nextLink();
            ArrayList<String> p = new ArrayList<String>();
            p.add(l);
            s = TemplateManager.getInstance().getTemplate("redirect", p);
        } else {
            s = "No action specified";
        }
        output(s, resp);
    }

    /**
     * method: post
     * description: add link(s) to crawl
     * parameters: data, containing json array of links.
     * response: how many requests are added, some requests might be in system already.
     *           An example response is "{'result':'2'}".
     */
    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setResponseType("js", resp);
        String data = req.getParameter("data");
        if (StringUtils.isNotBlank(data) == false) {
            data = "[]";
        }

        com.zyd.ncore.busi.LinkManager lm = com.zyd.ncore.busi.LinkManager.getInstance();
        int count = 0;
        try {
            JSONArray arr = new JSONArray(data);
            for (int i = 0; i < arr.length(); i++) {
                if (lm.addLink(arr.getString(i))) {
                    count++;
                }
            }
        } catch (JSONException e) {
            throw new IOException(e);
        }
        String s = Utils.stringArrayToJsonString(new String[] { "result", Integer.toString(count) });
        output(s, resp);
    }
}

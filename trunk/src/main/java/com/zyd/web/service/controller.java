package com.zyd.web.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.Constants;
import com.zyd.core.Utils;
import com.zyd.core.busi.CrawlerManager;
import com.zyd.core.busi.LinkManager;
import com.zyd.core.util.SpringContext;
import com.zyd.web.ServiceBase;

public class controller extends ServiceBase {

    public controller() {
    }

    /**
     * method: get description: perform various control functions parameters:
     * action, 'ClearAllData' will clear all data from the system, only used for
     * test.
     */
    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("ClearAllData".equals(action)) {
            setResponseType("js", resp);
            ((CrawlerManager) SpringContext.getContext().getBean("crawlerManager")).clearAll();
            output(Utils.stringArrayToJsonString(new String[] { "result", "true" }), resp);
        } else if ("LinkSnapshot".equals(action)) {
            setResponseType("text", resp);
            output(((LinkManager) SpringContext.getContext().getBean("linkManager")).snapshot(), resp);
        } else if ("UpdateLinkScannerParameter".equals(action)) {
            setResponseType("js", resp);
            String exp = req.getParameter("expire");
            String sleep = req.getParameter("sleep");
            Constants.LINK_MONITOR_SLEEP = Integer.parseInt(sleep);
            Constants.LINK_PROCESSING_EXPIRE = Integer.parseInt(exp);

            System.err.println("LINK_MONITOR_SLEEP updated to :" + Constants.LINK_MONITOR_SLEEP);
            System.err.println("LINK_PROCESSING_EXPIRE updated to :" + Constants.LINK_PROCESSING_EXPIRE);
            ((Thread) ((LinkManager) SpringContext.getContext().getBean("linkManager")).getLinkUpdateThread()).interrupt();
            output(Utils.stringArrayToJsonString(new String[] { "result", "true" }), resp);
        } else {
            setResponseType("text", resp);
            output("Invalid request:" + req.getRequestURI(), resp);
        }
    }

    /**
     * method: post *
     */
    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }
}

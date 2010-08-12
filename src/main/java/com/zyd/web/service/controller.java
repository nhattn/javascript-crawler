package com.zyd.web.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.Constants;
import com.zyd.core.Utils;
import com.zyd.core.busi.WorkerThread;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.util.SpringContext;
import com.zyd.linkmanager.LinkManager;
import com.zyd.linkmanager.mysql.DbHelper;
import com.zyd.web.ServiceBase;

public class controller extends ServiceBase {

    public controller() {
    }

    /**
     * method: get description: perform various control functions parameters:
     * action: 
     *         "ClearAllData" will clear all data from the system, only used for test. must provide entity parameter, which is 
     *               the hibernate entity name in the mapping files. to clean all link table, set entity to be "Link"
     *         
     *         "ConfigureSnapshot" will give an snapshot of current configuration.
     *         
     *         "LinkSnapshot", will give an snap shot of current LinkManager. not working for now.
     */
    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("ClearAllData".equals(action)) {
            setResponseType("js", resp);
            String entity = req.getParameter("entity");
            if (entity == null) {
                output(Utils.stringArrayToJsonString(new String[] { "result", "false", "msg", "missing entity" }), resp);
            } else {
                if (entity.equals("Link")) {
                    DbHelper.clearAllLinkTable();
                    ((LinkManager) SpringContext.getContext().getBean("linkManager")).clearAllCache();
                } else {
                    HibernateUtil.deleteAllObject(entity);
                }
                output(Utils.stringArrayToJsonString(new String[] { "result", "true" }), resp);
            }
        } else if ("LinkSnapshot".equals(action)) {
            setResponseType("text", resp);
            output(((LinkManager) SpringContext.getContext().getBean("linkManager")).linkSnapShot(), resp);
        } else if ("ConfigureSnapshot".equals(action)) {
            setResponseType("text", resp);
            output(Constants.snapShotValues(), resp);
        } else {
            setResponseType("text", resp);
            output("Invalid request:" + req.getRequestURI(), resp);
        }
    }

    private void wakeUpThreads() {
        ((WorkerThread) SpringContext.getContext().getBean("workerThread")).wakeUp();
    }

    /**
     *  action : "UpdateConfigure" will update system configuration, it takes a configuration file as parameter,
     *            Then update Constants. It only updates those values that is contained in the configuration file.
     *            The rest will be left there unchanged.
     *            parameter: content, is a string containing the content of configuration file.
     *            
     */
    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("UpdateConfigure".equals(action)) {
            setResponseType("js", resp);
            String content = req.getParameter("content");
            if (content != null) {
                ByteArrayInputStream ins = new ByteArrayInputStream(content.getBytes());
                Constants.loadValueFromStream(ins);
                wakeUpThreads();
                output(Utils.stringArrayToJsonString(new String[] { "result", "true" }), resp);
                return;
            }
        } else if ("ReloadConfigure".equals(action)) {
            Constants.loadValues();
            wakeUpThreads();
            output(Utils.stringArrayToJsonString(new String[] { "result", "true" }), resp);
            return;
        }
        setResponseType("text", resp);
        output("Invalid request:" + req.getRequestURI(), resp);
    }
}

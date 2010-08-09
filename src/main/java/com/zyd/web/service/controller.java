package com.zyd.web.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.Constants;
import com.zyd.core.Utils;
import com.zyd.core.busi.WorkerThread;
import com.zyd.core.objecthandler.ObjectManager;
import com.zyd.core.util.SpringContext;
import com.zyd.linkmanager.mysql.DbHelper;
import com.zyd.web.ServiceBase;

public class controller extends ServiceBase {

    public controller() {
    }

    /**
     * method: get description: perform various control functions parameters:
     * action: 
     *         "ClearAllData" will clear all data from the system, only used for test.
     *         
     *         "ConfigureSnapshot" will give an snapshot of current configuration.
     *         
     *         "LinkSnapshot", will give an snap shot of current LinkManager.
     */
    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("ClearAllData".equals(action)) {
            setResponseType("js", resp);
            this.cleanAllData();
            output(Utils.stringArrayToJsonString(new String[] { "result", "true" }), resp);
            return;
        } else if ("LinkSnapshot".equals(action)) {
            setResponseType("text", resp);
            //            output(((LinkManager) SpringContext.getContext().getBean("linkManager")).snapshot(), resp);
            throw new UnsupportedOperationException();
        } else if ("ConfigureSnapshot".equals(action)) {
            setResponseType("text", resp);
            output(Constants.snapShotValues(), resp);
            return;
        }
        setResponseType("text", resp);
        output("Invalid request:" + req.getRequestURI(), resp);
    }

    private void cleanAllData() {
        DbHelper.clearAllLinkTable();
        ((ObjectManager) (SpringContext.getContext().getBean("objectManager"))).deleteAllObjects();
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

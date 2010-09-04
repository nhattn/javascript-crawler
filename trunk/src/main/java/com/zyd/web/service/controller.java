package com.zyd.web.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.Constants;
import com.zyd.core.Utils;
import com.zyd.core.busi.ClientManager;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.util.SpringContext;
import com.zyd.linkmanager.LinkManager;
import com.zyd.linkmanager.mysql.DbHelper;
import com.zyd.linkmanager.watchlist.InjectableWatchlist;
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
            output(getSysinfo(), resp);
        } else if ("ConfigureSnapshot".equals(action)) {
            setResponseType("text", resp);
            output(Constants.snapShotValues(), resp);
        } else {
            setResponseType("text", resp);
            output("Invalid request:" + req.getRequestURI(), resp);
        }
    }

    private String getSysinfo() {
        StringBuffer buf = new StringBuffer();
        buf.append("#############    Link Status   ############");
        buf.append(Constants.LINE_SEPARATOR);
        buf.append(Constants.LINE_SEPARATOR);
        buf.append(((LinkManager) SpringContext.getContext().getBean("linkManager")).linkSnapShot());
        buf.append(Constants.LINE_SEPARATOR);
        buf.append(Constants.LINE_SEPARATOR);
        
        buf.append("#############    Client Status   ############");
        buf.append(Constants.LINE_SEPARATOR);
        buf.append(Constants.LINE_SEPARATOR);
        buf.append(((ClientManager) SpringContext.getContext().getBean("clientManager")).getClientReport());
        buf.append(Constants.LINE_SEPARATOR);
        buf.append(Constants.LINE_SEPARATOR);
        
        buf.append("#############    InjectedWatchList    ############");
        buf.append(Constants.LINE_SEPARATOR);
        buf.append(Constants.LINE_SEPARATOR);
        buf.append(InjectableWatchlist.dumpStatus());

        return buf.toString();
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
                output(Utils.stringArrayToJsonString(new String[] { "result", "true" }), resp);
                return;
            }
        } else if ("ReloadConfigure".equals(action)) {
            Constants.loadValues();
            output(Utils.stringArrayToJsonString(new String[] { "result", "true" }), resp);
            return;
        }
        setResponseType("text", resp);
        output("Invalid request:" + req.getRequestURI(), resp);
    }
}

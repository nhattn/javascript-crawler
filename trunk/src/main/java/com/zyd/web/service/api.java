package com.zyd.web.service;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;

import com.zyd.core.StringUtil;
import com.zyd.core.access.AccessController;
import com.zyd.core.access.AuthorizationController;
import com.zyd.core.access.IpCounter;
import com.zyd.core.access.ParameterController;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.DatabaseColumnInfo;
import com.zyd.core.objecthandler.Handler;
import com.zyd.core.objecthandler.ObjectHelper;
import com.zyd.core.objecthandler.SearchResult;
import com.zyd.core.util.SpringContext;
import com.zyd.web.ServiceBase;

/**
 * api takes the following parameters:
 * 
 * lng - longitude range, must be a range separated by ',' , like "91.2,91.3", or "-91.2,-91.1",
 *        smaller ones much be in front.
 * lat - latitude range, same as longtitude
 * layer - the layer to query against. layer must be a comma separated strings. Available ones are:
 *         com.zuiyidong.layer.restaurant
 *         com.zuiyidong.layer.busstation
 * 
 * count - optional. How many result you want to return, default to 20.
 * start - optional. The start index of the first result, default to 0.
 * key   - optinal. keyword to filter the result. Which columns to filter depends on the layer.
 * format - optional. json or xml. default to xml. 
 * separater - optional, how to separate the range object. Default to ','
 */
public class api extends ServiceBase {
    private static Logger logger = Logger.getLogger(object.class);
    private IpCounter ipCounter;
    private AccessController accessController;
    private AuthorizationController authorizationController;

    public final static boolean DoAuth = false;

    public api() {
        ipCounter = (IpCounter) SpringContext.getContext().getBean("ipCounter");
        accessController = (AccessController) SpringContext.getContext().getBean("accessController");
        authorizationController = (AuthorizationController) SpringContext.getContext().getBean("authorizationController");
    }

    private boolean doAuth(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        String ip = req.getRemoteAddr();
        if (accessController.isIpBlocked(ip) && req.getParameter("accessCheck") == null) {
            logger.warn("blocked access from " + ip);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }

        String clientId = req.getParameter("clientId");
        if (clientId == null) {
            logger.warn("Tyring to access without client id, blocked access from " + ip);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }

        if (authorizationController.logAccess(clientId, ip) == false) {
            logger.warn("Tyring to access without invalid  clientId, blocked access from " + ip + ", clientid " + clientId);
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        //TODO: this is stupid, it's logged twice!!!!!!!!!!!!!!! fix this
        ipCounter.logAccess(ip);
        return true;
    }

    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (DoAuth) {
            if (doAuth(req, resp) == false)
                return;
        }

        String layer = req.getParameter("layer");
        if (layer == null || ParameterController.isLayerAllowed(layer) == false) {
            setStatus(HttpServletResponse.SC_BAD_REQUEST, "Invalid layer - " + layer, resp);
            return;
        }
        HashMap<String, String> params = requestParameterToMap(req);
        if (ParameterController.isParameterAllowed(params.keySet(), layer) == false) {
            setStatus(HttpServletResponse.SC_BAD_REQUEST, "One of these parameters is invalid - " + params.keySet(), resp);
            return;
        }
        String tableName = HibernateUtil.getTableName(layer);
        HashMap<String, DatabaseColumnInfo> meta = HibernateUtil.getTableMetaData(tableName);
        if (params.get(Handler.Parameter.PARAMETER_SEPARATOR) == null) {
            params.put(Handler.Parameter.PARAMETER_SEPARATOR, ",");
        }
        SearchResult r = ObjectHelper.defaultQuery(params, layer, meta);
        String format = req.getParameter("format");
        if (format == null || "xml".equals(format)) {
            setResponseType("xml", resp);
            resp.getWriter().write(StringUtil.toXmlString(r.result));
        } else {
            try {
                setResponseType("js", resp);
                resp.getWriter().write(StringUtil.toJsonString(r.result));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

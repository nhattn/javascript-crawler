package com.zyd.web.service;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

import com.zyd.core.StringUtil;
import com.zyd.core.busi.api.ParameterController;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.DatabaseColumnInfo;
import com.zyd.core.objecthandler.ObjectHelper;
import com.zyd.core.objecthandler.SearchResult;
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
 * 
 */
public class api extends ServiceBase {
    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        SearchResult r = ObjectHelper.defaultQuery(params, layer, meta, ",");
        String format = req.getParameter("format");
        if (format == null || "xml".equals(format)) {
            resp.getWriter().write(StringUtil.toXmlString(r.result));
        } else {
            try {
                resp.getWriter().write(StringUtil.toJsonString(r.result));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.zyd.web.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.core.busi.api.ApiHandler;
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
 * @author yang
 *
 */
public class api extends ServiceBase {
    private ApiHandler apiHandler;

    public api() {
        apiHandler = (ApiHandler) SpringContext.getContext().getBean("apiHandler");
    }

    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String slng = req.getParameter("lng"), slat = req.getParameter("lat"), layer = req.getParameter("layer");
        if (slng == null || slat == null || layer == null) {
            setStatus(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameter", resp);
            return;
        }
        int index1, index2;
        index1 = slng.indexOf(',');
        index2 = slat.indexOf(',');
        if (index1 == -1 || index2 == -1) {
            setStatus(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameter", resp);
            return;
        }
        double lat1, lat2, lng1, lng2;
        try {
            lng1 = Double.parseDouble(slng.substring(0, index1));
            lng2 = Double.parseDouble(slng.substring(index1 + 1, slng.length()));
            
            lat1 = Double.parseDouble(slat.substring(0, index2));
            lat2 = Double.parseDouble(slat.substring(index2 + 1, slat.length()));
        } catch (NumberFormatException ne) {
            setStatus(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameter", resp);
            return;
        } catch (StringIndexOutOfBoundsException se) {
            setStatus(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameter", resp);
            return;
        }
        String count = req.getParameter("count");
        String start = req.getParameter("start");
        String key = req.getParameter("key");
        String format = req.getParameter("format");
        if ("json".equals(format)) {
            setResponseType("json", resp);
        } else if (format == null || "xml".equals(format)) {
            setResponseType("xml", resp);
        } else {
            setStatus(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameter", resp);
            return;
        }
        String result = apiHandler.query(layer, lng1, lng2, lat1, lat2, count == null ? 20 : Integer.parseInt(count), start == null ? 0 : Integer.parseInt(start), key, format);
        output(result, resp);
    }
}

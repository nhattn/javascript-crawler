package com.zyd.web.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.core.objecthandler.Handler;

public class che08a extends api {
    public static int counter = 0;

    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (counter++ > 2000) {
            output("Reached 2000 access limit, accessblocked", resp);
            return;
        }
        if (req.getParameter(Handler.Columns.Lat) == null || req.getParameter(Handler.Columns.Lng) == null) {
            output("Must specify lng, lat", resp);
            return;
        }
        super.get(req, resp);
    }
}

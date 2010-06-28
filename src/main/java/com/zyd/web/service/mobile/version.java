package com.zyd.web.service.mobile;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.Constants;
import com.zyd.web.ServiceBase;

public class version extends ServiceBase {
    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("app");
        String r = "";
        if ("HouseLoc".equals(name)) {
            r = "version=3.0\nname=我居\nnumber=3\nurl=" + Constants.ServerUrl + "/releases/HouseLoc_Current.apk";
        } else {
            r = "wrong parameter";
        }
        setResponseType("text", resp);
        resp.getWriter().write(r);
    }
}

package com.zyd.web.service;

import java.io.IOException;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class z extends object {
    private static Logger logger = Logger.getLogger(z.class);

    public static HashSet<String> banList = new HashSet<String>();

    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ip = req.getRemoteAddr();
        if (banList.contains(ip)) {
            resp.setStatus(404);
            logger.warn("trying to hack server:" + ip);
            return;
        }
        if ("gzip,deflate".equals(req.getHeader("Accept-Encoding")) == false) {
            logger.warn("trying to hack server:" + ip);
            banList.add(ip);
            resp.setStatus(404);
            return;
        }
        super.get(req, resp);
    }

}

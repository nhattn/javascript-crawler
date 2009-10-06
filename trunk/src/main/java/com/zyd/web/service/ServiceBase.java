package com.zyd.web.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServiceBase {
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        serviceNotFound(req, resp);
    }

    public void put(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        serviceNotFound(req, resp);
    }

    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        serviceNotFound(req, resp);
    }

    private void serviceNotFound(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String msg = "The requested service: " + req.getRequestURI() + ", method:" + req.getMethod() + " is not found.";
        resp.getWriter().write(msg);
        resp.flushBuffer();
    }
}

package com.zyd.web.service.crawler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.web.service.ServiceBase;

public class booklist extends ServiceBase {
    @Override
    public void put(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write("dkdkdk");
    }
    
    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("got data");
        System.out.println(req.getParameter("data"));
        resp.getWriter().write("dkdkdk");
    }
    
    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.getWriter().write("dkdkdk");
    }
}

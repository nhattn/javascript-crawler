package com.zyd.web.service;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.zyd.web.ServiceBase;

public class sync extends ServiceBase {
    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getParameter("format"));
        InputStream ins = req.getInputStream();
        String s = IOUtils.toString(ins);
        System.out.println(s);
    }
}

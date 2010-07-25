package com.zyd.web.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class zyd extends object {
    private static Logger logger = Logger.getLogger(zyd.class);

    public static HashSet<String> banList = new HashSet<String>();
    public static Hashtable<String, Integer> ipcounter = new Hashtable<String, Integer>();

    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.get(req, resp);
    }

}

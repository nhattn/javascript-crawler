package com.zyd.web.service.crawler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.web.Util;
import com.zyd.web.core.LinkManager;
import com.zyd.web.core.TemplateManager;
import com.zyd.web.service.ServiceBase;

public class link extends ServiceBase {

    /**
     * request parameters 
     * action - list/get
     *          list lists all links
     *          get returns one link then removes the link as well
     */
    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-Type", "text/plain; charset=UTF-8");
        String action = req.getParameter("action");
        LinkManager lm = LinkManager.getInstance();
        if (action == null) {
            action = "list";
        }
        if ("list".equals(action)) {
            try {
                resp.getWriter().write(Util.stringToFlatList(lm.getAllLinks()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if ("get".equals(action)) {
            resp.getWriter().write(lm.nextLink());
        } else if ("redirect".equals(action)) {
            resp.setHeader("Content-Type", "text/html; charset=UTF-8");
            String l = lm.nextLink();
            ArrayList<String> p = new ArrayList<String>();
            p.add(l);
            String s = TemplateManager.getInstance().getTemplate("redirect", p);
            resp.getWriter().write(s);
        } else {
            resp.getWriter().write("No action specified");
        }
    }
}

package com.zyd.web.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.core.util.SpringContext;
import com.zyd.linkmanager.Link;
import com.zyd.linkmanager.LinkManager;
import com.zyd.web.ServiceBase;

public class glink extends ServiceBase {
    LinkManager linkMan;

    public glink() {
        linkMan = ((LinkManager) SpringContext.getContext().getBean("linkManager"));
    }

    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id == null) {
            setResponseType(ResponseType_Text, resp);
            output("id required.", resp);
            return;
        }
        Link link = linkMan.getLinkById(id);
        if (link == null) {
            setResponseType(ResponseType_Text, resp);
            output("no such link " + id, resp);
            return;
        } else {
            resp.sendRedirect(link.getUrl());
        }
    }
}

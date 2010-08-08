package com.zyd.web.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zyd.core.access.AuthorizationController;
import com.zyd.core.util.SpringContext;
import com.zyd.web.ServiceBase;

public class auth extends ServiceBase {
    private static Logger logger = Logger.getLogger(auth.class);
    private AuthorizationController authorizationController;

    public auth() {
        authorizationController = (AuthorizationController) SpringContext.getContext().getBean("authorizationController");
    }

    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String key = req.getParameter("clientKey"), id = req.getParameter("clientId");
        if (authorizationController.authorize(id, key, req.getRemoteAddr()) == false) {
            logger.info("Client logging in with wrong id and key " + id + ", " + key + ", " + req.getRemoteAddr());
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            setResponseType("text", resp);
            output("OK", resp);
        }
    }

    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        post(req, resp);
    }
}

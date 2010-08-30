package com.zyd.web.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.core.objecthandler.Handler;

public class che08b extends object {

    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (che08a.counter++ > 2000) {
            output("Reached 2000 access limit, accessblocked", resp);
            return;
        }
        if (req.getParameter(Handler.Parameter.PARAMETER_COUNT) != null) {
            output("Count not allowed.", resp);
            return;
        }
        super.get(req, resp);
    }
}

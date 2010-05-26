package com.zyd.web.service.mobile;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.core.objecthandler.ObjectManager;
import com.zyd.core.objecthandler.SearchResult;
import com.zyd.core.objecthandler.AppLog.Columns;
import com.zyd.core.objecthandler.Handler.Parameter;
import com.zyd.core.util.SpringContext;
import com.zyd.web.ServiceBase;

public class log extends ServiceBase {
    private ObjectManager objectManager;

    public log() {
        objectManager = (ObjectManager) SpringContext.getContext().getBean("objectManager");
    }

    @Override
    public void put(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HashMap<String, String> values = requestParameterToMap(req);
        values.put(Parameter.PARAMETER_OBJECT_ID, "AppLog");
        values.put(Columns.Ip, req.getRemoteAddr());
        boolean result = (Boolean) objectManager.create(values);
        if (result == false) {
            System.err.println("Can not create applog :" + values);
        }
        setResponseType("text", resp);
        output("ok", resp);
    }

    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/xml");
        HashMap params = requestParameterToMap(req);
        SearchResult result = objectManager.query(params);
        resp.getWriter().write(toXmlString(result, null));
    }
}

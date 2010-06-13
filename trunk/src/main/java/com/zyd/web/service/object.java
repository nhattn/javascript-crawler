package com.zyd.web.service;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zyd.core.busi.ClientManager;
import com.zyd.core.busi.LinkManager;
import com.zyd.core.objecthandler.Handler;
import com.zyd.core.objecthandler.ObjectManager;
import com.zyd.core.objecthandler.SearchResult;
import com.zyd.core.util.SpringContext;
import com.zyd.web.ServiceBase;

@SuppressWarnings("unchecked")
public class object extends ServiceBase {
    private static Logger logger = Logger.getLogger(object.class);
    private LinkManager linkManager;
    private ObjectManager objectManager;
    private ClientManager clientManager;

    public object() {
        linkManager = (LinkManager) SpringContext.getContext().getBean("linkManager");
        objectManager = (ObjectManager) SpringContext.getContext().getBean("objectManager");
        clientManager = (ClientManager) SpringContext.getContext().getBean("clientManager");
    }

    /**
     * method: post
     * description: create a new object 
     * this will call appropriate handler based on the appid of the parameter
     * this will return a json object like such indicating wheather or not something has changed, i.e. the object
     * has been added.
     * {
     *  result: 'true'/'false'
     * }
     * 
     * Also, the referer url will be marked as processed.
     */
    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setResponseType("js", resp);
        String referer = req.getHeader("Referer");
        if (referer == null || linkManager.getProcessingLink(referer) == null) {
            if (referer == null) {
                logger.warn("Can not process link, no refeerer");
            } else {
                logger.warn("Can not process link, link is not in processing list:" + referer);
            }
            output(RESULT_NO_CHANGE, resp);
        } else {
            HashMap values = requestParameterToMap(req);
            values.put(Handler.Columns.Referer, referer);
            boolean result = (Boolean) objectManager.create(values);
            linkManager.linkFinished(referer);
            output(result ? RESULT_CHANGE : RESULT_NO_CHANGE, resp);
            if (result == false) {
                logger.warn("Failed to handle url - " + referer);
            }
            clientManager.logRequest(req);
        }
    }

    /**
     * Get a list of objects
     * TODO if there is no result how to notify
     */
    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/xml");
        HashMap params = requestParameterToMap(req);
        SearchResult result = objectManager.query(params);
        resp.getWriter().write(toXmlString(result, null));
    }

}

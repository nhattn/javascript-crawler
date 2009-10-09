package com.zyd.web.servlet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.web.service.ServiceBase;

public class GateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final static String ServicePackage = "com.zyd.web.";
    private final static ServiceBase defaultService = new ServiceBase();
    private static HashMap<String, ServiceBase> serviceMap = new HashMap<String, ServiceBase>();

    /**
     * Looking up services in com.zyd.web.service directory, based on the urls
     * a url like "http://localhost:8080/crawler/service/crawler/booklist", 
     * will be matched to com.zdy.web.service.crawler.booklist.
     * @param req
     * @return
     */
    private ServiceBase lookupService(HttpServletRequest req) {
        ServiceBase service = defaultService;
        String className = translateURIToClassName(req.getRequestURI(), req.getContextPath());
        synchronized (this) {
            if (serviceMap.containsKey(className)) {
                service = serviceMap.get(className);
            } else {
                try {
                    service = (ServiceBase) Class.forName(className).newInstance();
                    serviceMap.put(className, service);
                } catch (Exception e) {
                    // TODO: add an entry for not matched path, save lookup time.
                    System.err.println("Error trying to look up service for url:" + req.getRequestURL());
                    e.printStackTrace(); // TODO: delete me
                }
            }
        }
        return service;
    }

    private String translateURIToClassName(String uri, String context) {
        uri = uri.substring(context.length()).replace('/', '.');
        if (uri.startsWith(".")) {
            uri = uri.substring(1);
        }
        return ServicePackage + uri;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServiceBase service = lookupService(req);
        allowCrossDomain(resp);
        service.get(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServiceBase service = lookupService(req);
        allowCrossDomain(resp);
        service.put(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServiceBase service = lookupService(req);
        allowCrossDomain(resp);
        service.post(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        allowCrossDomain(resp);
    }

    private void allowCrossDomain(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Methods", "GET");
        resp.addHeader("Access-Control-Allow-Methods", "PUT");
        resp.addHeader("Access-Control-Allow-Methods", "POST");
        //resp.setHeader("Access-Control-Allow-Headers", "my-header");
        resp.setHeader("Access-Control-Max-Age", "0");
    }
}

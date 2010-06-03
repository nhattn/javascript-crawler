package com.zyd.web.service.file;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.Constants;
import com.zyd.core.busi.LinkManager;
import com.zyd.core.busi.TemplateManager;
import com.zyd.core.util.SpringContext;
import com.zyd.web.ServiceBase;

public class crawlerconfig extends ServiceBase {
    LinkManager linkManager;
    private String content;
    private int lastRefreshRate = -1;
    private TemplateManager templateManager;

    public crawlerconfig() {
        linkManager = (LinkManager) SpringContext.getContext().getBean("linkManager");
        templateManager = (TemplateManager) SpringContext.getContext().getBean("templateManager");
        lastRefreshRate = linkManager.getSuggestedLinkRefreshTime();
        content = generateContent(lastRefreshRate * 1000);
    }

    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setResponseType("js", resp);
        int i = linkManager.getSuggestedLinkRefreshTime();
        if (i != lastRefreshRate) {
            lastRefreshRate = i;
            content = generateContent(lastRefreshRate * 1000);
        }
        output(content, resp);
    }

    private String generateContent(int refreshRate) {
        ArrayList<String> values = new ArrayList<String>();
        values.add(Integer.toString(refreshRate));
        values.add("43200000");
        values.add("43200000");
        values.add(Constants.VERSION_STRING);
        return templateManager.getTemplate("crawlerconfig", values);
    }
}

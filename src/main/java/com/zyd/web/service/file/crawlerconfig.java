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
        lastRefreshRate = linkManager.getSuggestedLinkRefreshInterval();
        content = generateContent(lastRefreshRate * 1000);
    }

    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setResponseType("js", resp);
        int i = linkManager.getSuggestedLinkRefreshInterval();
        if (i != lastRefreshRate) {
            lastRefreshRate = i;
            content = generateContent(lastRefreshRate * 1000);
        }
        output(content, resp);
    }

    private String generateContent(int refreshRate) {
        ArrayList<String> values = new ArrayList<String>();
        values.add(Integer.toString(refreshRate));
        values.add("86400000");  //ganji.com 24hours
        values.add("102000000"); //koubei.com 24+
        values.add(Constants.VERSION_STRING);
        return templateManager.getTemplate("crawlerconfig", values);
    }
}

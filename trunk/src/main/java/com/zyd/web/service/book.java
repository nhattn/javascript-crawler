package com.zyd.web.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.zyd.ncore.Utils;
import com.zyd.ncore.busi.CrawlerManager;
import com.zyd.ncore.dom.Book;
import com.zyd.web.ServiceBase;

public class book extends ServiceBase {
    /**
     * method: post
     * description: add a new book
     * parameters: data, a json string cotaining a single book
     * response: a json string "{'result':'true'}" or "{'result':'false'}", indicating wheather or not
     * anything is changed because of this call. like book doesn't exist before, or chapters are added etc.
     */
    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setResponseType("js", resp);
        String data = req.getParameter("data"), fromUrl = req.getHeader("referer");
        boolean changed = false;
        if (StringUtils.isNotBlank(data)) {
            changed = CrawlerManager.getInstance().processBook(data, fromUrl);
        }
        String s = Utils.stringArrayToJsonString(new String[] { "result", Boolean.toString(changed) });
        output(s, resp);
    }

    /**
     * method: get
     * description: get a book information
     * parameters: name, the name of the book; author; withChapter, whether or not also return the chapter list with this request, 'true' or 'false'. 
     * response: a json string describing the book. fields has the same name-value as {@link Book} 
     */
    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        super.get(req, resp);
    }
}

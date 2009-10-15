package com.zyd.web.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.zyd.Config;
import com.zyd.ncore.Utils;
import com.zyd.ncore.busi.BookManager;
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
        //        req.setCharacterEncoding("GBK");
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
     * parameters: name, the name of the book; author. these two is necessary if no id is there. 
     *             or id, the id of the book.
     *             
     *             withChapter, whether or not also return the chapter list with this request, 'true' or 'false', default to "false"
     *             format, "json" or "xml", default to "xml"
     * response: a json/xml string describing the book. fields has the same name-value as {@link Book} 
     */
    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name"), author = req.getParameter("author"), id = req.getParameter("id"), format = req.getParameter("format");
        BookManager bm = BookManager.getInstance();
        Book book = null;

        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(author)) {
            book = new Book();
            book.setName(name);
            book.setAuthor(author);
        } else if (StringUtils.isNotBlank(id)) {
            book = new Book();
            book.setId(id);
        }
        if (book == null) {
            throw new ServletException("Invalid request: not enough parameter");
        }
        book = bm.findBook(book);
        String content = "no book found";
        if (book != null) {
            String withChapter = req.getParameter("withChapter");
            if (StringUtils.isNotBlank(withChapter)) {
                if ("true".equals(withChapter)) {
                    bm.loadBookChapter(book);
                } else if ("false".equals(withChapter)) {
                } else {
                    throw new ServletException("Invalid request parameter value for withChapter:" + withChapter);
                }
            }
            if ("xml".equals(format)) {
                setResponseType("xml", resp);
                content = book.toXMLString(Config.Encoding, true);
            } else if (format == null || "json".equals(format)) {
                setResponseType("js", resp);
                content = book.toJsonString();
            } else {
                throw new ServletException("Invalid request parameter value for format:" + format);
            }
        }
        output(content, resp);
    }
}

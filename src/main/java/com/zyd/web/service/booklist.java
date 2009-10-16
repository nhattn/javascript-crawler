package com.zyd.web.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.zyd.core.Utils;
import com.zyd.core.busi.BookManager;
import com.zyd.core.busi.CrawlerManager;
import com.zyd.core.dom.Book;
import com.zyd.core.dom.BookFilter;
import com.zyd.web.ServiceBase;

public class booklist extends ServiceBase {

    /**
     * method: post
     * description: add a list of books 
     * parameters: data, a json string cotaining a list of books
     * response: a json string "{'result':'n'}", indicating how many books is added because of this call. 
     */
    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setResponseType("js", resp);
        String data = req.getParameter("data"), fromUrl = req.getHeader("referer");
        int addedCount = 0;
        if (StringUtils.isNotBlank(data)) {
            addedCount = CrawlerManager.getInstance().processBookList(data, fromUrl);
        }
        String s = Utils.stringArrayToJsonString(new String[] { "result", Integer.toString(addedCount) });
        output(s, resp);
    }

    /**
     * method: get
     * description: get a list of books
     * parameter: format, "xml" or "json", default to "xml". 
     *            start, start index from total books. 
     *            count, how many books to return.
     *            
     */
    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String format = req.getParameter("format"), starts = req.getParameter("start"), counts = req.getParameter("count");
        if (StringUtils.isNotBlank(format) == false || ("xml".equals(format) == false && "json".equals(format) == false)) {
            format = "xml";
        }
        if (StringUtils.isNotBlank(starts) == false || StringUtils.isNumeric(starts) == false) {
            starts = "0";
        }
        if (StringUtils.isNotBlank(counts) == false || StringUtils.isNotBlank(counts) == false) {
            counts = "20";
        }
        int start = Integer.parseInt(starts), count = Integer.parseInt(counts);

        BookFilter filter = new BookFilter();
        filter.setStart(start);
        filter.setCount(count);
        List<Book> bookList = BookManager.getInstance().listBook(filter);
        String s;
        if ("json".equals(format)) {
            throw new UnsupportedOperationException();
        } else {
            s = Utils.objToXml(bookList, "UTF-8");
        }
        output(s,"UTF-8", resp);
    }
}

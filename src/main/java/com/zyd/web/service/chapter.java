package com.zyd.web.service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.web.ServiceBase;
import com.zyd.web.core.BookManager;
import com.zyd.web.core.LinkManager;
import com.zyd.web.core.TemplateManager;
import com.zyd.web.dom.Book;
import com.zyd.web.dom.Chapter;

public class chapter extends ServiceBase {
    /**
     * method: post
     * description a
     */
    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-Type", "application/javascript; charset=GBK");

        String data = req.getParameter("data");
        BookManager bm = BookManager.getInstance();
        Chapter chapter = bm.parseChapter(data);
        if ( "y".equals(chapter.linkBookByUrl )) {
            String referer = req.getHeader("Referer");
            Book book = LinkManager.getInstance().getBookForLink(referer);
            book.addChapter(chapter);
        } else {
            throw new UnsupportedOperationException();
        }
        Writer writer = new OutputStreamWriter(resp.getOutputStream(), "GBK");
        String s = TemplateManager.getInstance().getNextAction("Goto.Next.Link");
        writer.write(s);
        writer.flush();
        writer.close();
    }
}

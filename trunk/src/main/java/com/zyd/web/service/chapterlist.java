package com.zyd.web.service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.web.ServiceBase;
import com.zyd.web.core.BookManager;
import com.zyd.web.core.LinkManager;
import com.zyd.web.core.TemplateManager;
import com.zyd.web.dom.Book;
import com.zyd.web.dom.Chapter;

public class chapterlist extends ServiceBase {
    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-Type", "application/javascript; charset=GBK");
        Cookie userCookie = new Cookie("user", "uid1234");
        resp.addCookie(userCookie);

        String data = req.getParameter("data");

        BookManager bm = BookManager.getInstance();
        LinkManager lm = LinkManager.getInstance();
        Book nbook = bm.parseBook(data);
        Book obook = bm.getBookByNameAuthor(nbook);
        if (obook == null) {
            bm.addBook(nbook);
            obook = nbook;
        } else {
            bm.updateBook(obook, nbook);
        }
        
        if (nbook.chapters != null && false) {
            //TODO: do not grab chapter text for now
            List<Chapter> list = obook.chapters;
            boolean linkBook = false;
            if ("y".equals(nbook.linkWithChapterUrl))
                linkBook = true;

            for (Chapter c : list) {
                if (c.link != null) {
                    if (linkBook == true)
                        lm.addLink(c.link, obook);
                    else
                        lm.addLink(c.link);
                }
            }
        }

        String s = TemplateManager.getInstance().getNextAction("Goto.Next.Link");
        Writer writer = new OutputStreamWriter(resp.getOutputStream(), "GBK");
        writer.write(s);
        writer.flush();
        writer.close();
    }
}

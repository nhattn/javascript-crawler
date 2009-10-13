package com.zyd.web.service.crawler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.web.core.BookManager;
import com.zyd.web.core.LinkManager;
import com.zyd.web.core.TemplateManager;
import com.zyd.web.dom.Book;
import com.zyd.web.service.ServiceBase;

public class book extends ServiceBase {
    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-Type", "application/javascript; charset=GBK");

        String data = req.getParameter("data");
        BookManager bm = BookManager.getInstance();
        Book nbook = bm.parseBook(data);
        Book obook = bm.getBookByNameAuthor(nbook);
        bm.updateBook(obook, nbook);
        LinkManager.getInstance().addLink(obook.getAllChapterLink());
        
        String s = TemplateManager.getInstance().getNextAction("Goto.XPath.Link", "/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[1]/table/tbody/tr[3]/td/a/@href");
        Writer writer = new OutputStreamWriter(resp.getOutputStream(), "GBK");
        writer.write(s);
        writer.flush();
        writer.close();
    }
}

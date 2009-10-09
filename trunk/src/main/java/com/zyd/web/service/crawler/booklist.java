package com.zyd.web.service.crawler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import com.zyd.web.Util;
import com.zyd.web.core.BookManager;
import com.zyd.web.core.LinkManager;
import com.zyd.web.core.SiteManager;
import com.zyd.web.dom.Book;
import com.zyd.web.dom.WebSite;
import com.zyd.web.service.ServiceBase;

public class booklist extends ServiceBase {
    public final static String nextLinkFile = "E:\\workspace\\crawler\\src\\main\\webapp\\temp\\nextlink.js";

    public booklist() {
    }

    @Override
    public void put(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write("put");
    }

    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-Type", "application/javascript; charset=GBK");
        String data = req.getParameter("data");

        // add book info
        BookManager bm = BookManager.getInstance();
        List<Book> books = bm.parseBookList(data);
        String domain = req.getRemoteHost();
        WebSite site = SiteManager.getInstance().getSite(domain);
        for (Book b : books) {
            b.site = site;
        }
        bm.addBookList(books);
        
        // add links for analysis
        LinkManager lm = LinkManager.getInstance();
        for(Book b: books){
            lm.addLink(b.tempLink);
        }

        String s = FileUtils.readFileToString(new File(nextLinkFile), "GBK");
        Writer writer = new OutputStreamWriter(resp.getOutputStream(), "GBK");
        writer.write(s);
        writer.flush();
        writer.close();
    }

    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-Type", "application/xhtml+xml; charset=UTF-8");
        Book[] books = (new ArrayList<Book>(BookManager.getInstance().getAllBooks())).toArray(new Book[0]);
        String s = "";
        try {
            s = Util.objToXml(books);
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.getWriter().write(s);
    }
}

package com.zyd.ncore.busi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.zyd.ncore.dom.Book;
import com.zyd.ncore.dom.Chapter;
import com.zyd.ncore.dom.Site;

public class TestUtil {
    static Random rand = new Random();
    static HashSet<String> usedString = new HashSet<String>();
    static String[] domains = new String[] { "http://www.qidian.com", "http://17k.com", "http://aa.kanshu.com" };

    public static String getNoRepeatString() {
        StringBuffer buf = new StringBuffer();
        Date d = new Date();
        buf.append(Long.toString(d.getTime() + rand.nextInt(100000)));
        String s = StringUtils.rightPad(buf.toString(), 15);
        if (usedString.contains(s)) {
            return getNoRepeatString();
        } else {
            usedString.add(s);
            return s;
        }
    }

    public static List<Book> getBookList(int count) {
        List<Book> books = new ArrayList<Book>();
        for (int i = 0; i < count; i++) {
            Book b = new Book();
            b.setId(Integer.toString(10000 + i));
            b.setName("С˵����" + i);
            b.setAuthor("����" + i);
            b.setAllChapterUrl(domains[i % 3] + "/all_chapter_" + i);
            b.setCoverUrl(domains[i % 3] + "/cover_" + i);
            books.add(b);
        }
        return books;
    }

    public static Chapter getChapter() {
        Chapter c = new Chapter();
        String s = getNoRepeatString();
        c.setId(s);
        c.setName("�½�����" + s);
        c.setContent("����" + s);
        c.setDescription("�½ڼ��" + s);
        return c;
    }

    public static List<Site> getSiteList() {
        List<Site> r = new ArrayList<Site>();
        SiteManager sm = SiteManager.getInstance();
        for (String s : domains) {
            r.add(sm.addSite(s));
        }
        return r;
    }

    public static void buildModel(int bookCount, int chapterPerBook) {
        List<Book> books = getBookList(bookCount);
        BookManager bm = BookManager.getInstance();
        for (Book book : books) {
            bm.addBook(book);
            for (int i = 0; i < chapterPerBook; i++) {
                Chapter c = getChapter();
                c.setName(book.getName() + ":+�½�" + i);
                c.setChapterUrl(book.getAllChapterUrl()+"/chapter_"+i);
                bm.addChapterToBook(book, c);
            }
        }
    }
}
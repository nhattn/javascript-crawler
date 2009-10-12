package com.zyd.ncore.busi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.zyd.ncore.dom.Book;
import com.zyd.ncore.dom.Chapter;

public class Util {
    static Random rand = new Random();
    static HashSet<String> usedString = new HashSet<String>();

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
            b.setName("小说书名" + i);
            b.setAuthor("作者" + i);
            books.add(b);
        }
        return books;
    }

    public static Chapter getChapter() {
        Chapter c = new Chapter();
        String s = getNoRepeatString();
        c.setId(s);
        c.setName("章节名称" + s);
        c.setContent("内容" + s);
        c.setDescription("章节简介" + s);
        return c;
    }

    public static void buildModel(int bookCount, int chapterPerBook) {
        List<Book> books = getBookList(bookCount);
        BookManager bm = BookManager.getInstance();
        for (Book book : books) {
            bm.addBook(book);
            for (int i = 0; i < chapterPerBook; i++) {
                Chapter c = getChapter();
                c.setName(book.getName() + ":+章节" + i);
                bm.addChapterToBook(book, c);
            }
        }
    }
}

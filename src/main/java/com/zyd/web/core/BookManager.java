package com.zyd.web.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zyd.web.Util;
import com.zyd.web.dom.Book;
import com.zyd.web.dom.Chapter;

public class BookManager {

    private static BookManager instance = new BookManager();

    private static HashMap<String, Book> bookCache = new HashMap<String, Book>();
    private static List<Book> bookList = new ArrayList<Book>();

    private BookManager() {
    }

    public static BookManager getInstance() {
        return instance;
    }

    public void clearBooks() {
        bookCache.clear();
    }

    /**
     * 
     * @param bookList
     * @return the number of book that is actually added.
     */
    public int addBookList(List<Book> bookList) {
        int counter = 0;
        for (Book book : bookList) {
            if (addBook(book))
                counter++;
        }
        return counter;
    }

    public boolean hasBook(Book book) {
        String k = book.name + "$" + book.author;
        return bookCache.containsKey(k);
    }

    public Book getBookByNameAuthor(Book book) {
        String k = book.name + "$" + book.author;
        return bookCache.get(k);
    }

    /**
     * returns true if book is not in the system
     * false if book is already in the system.
     * @param book the book to add, the book will be loaded with a ID.
     * @return
     */
    public boolean addBook(Book book) {
        Book book1 = getBookByNameAuthor(book);
        if (book1 != null) {
            book.id = book1.id;
            return false;
        } else {
            String k = book.name + "$" + book.author;
            book.id = Util.getUniqueBookId();
            bookCache.put(k, book);
            bookList.add(book);
            return true;
        }
    }

    /**
     * 
     * @param s is a json string
     * @return
     */
    public List<Book> parseBookList(String s) {
        List<Book> ret = new ArrayList<Book>();
        JSONArray arr = null;
        try {
            arr = new JSONArray(s);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = (JSONObject) arr.get(i);
                Book b = new Book();
                b.name = obj.getString("name");
                b.author = obj.getString("author");
                b.cat1 = obj.getString("cat1");
                b.cat2 = obj.getString("cat2");
                b.totalChar = obj.getString("totalChar");
                b.tempLink = obj.getString("nextLink");
                b.updateTime = obj.getString("updateTime");
                ret.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public Book parseBook(String s) {
        Book b = new Book();
        JSONObject obj = null;
        try {
            obj = new JSONObject(s);
            if (obj.has("name"))
                b.name = obj.getString("name");
            if (obj.has("author"))
                b.author = obj.getString("author");
            if (obj.has("cat1"))
                b.cat1 = obj.getString("cat1");
            if (obj.has("cat2"))
                b.cat2 = obj.getString("cat2");
            if (obj.has("totalChar"))
                b.totalChar = obj.getString("totalChar");
            if (obj.has("allChapterLink"))
                b.allChapterLink = obj.getString("allChapterLink");
            if (obj.has("description"))
                b.description = obj.getString("description");
            if (obj.has("updateTime"))
                b.updateTime = obj.getString("updateTime");
            if (obj.has("linkWithChapterUrl"))
                b.linkWithChapterUrl = obj.getString("linkWithChapterUrl");

            if (obj.has("chapters")) {
                List<Chapter> list = new ArrayList<Chapter>();
                JSONArray chapters = obj.getJSONArray("chapters");
                int len = chapters.length();
                for (int i = 0; i < len; i++) {
                    JSONObject c = chapters.getJSONObject(i);
                    Chapter chapter = new Chapter();
                    if (c.has("volume"))
                        chapter.volume = c.getString("volume");
                    if (c.has("updateTime"))
                        chapter.updateTime = c.getString("updateTime");
                    if (c.has("totalChar"))
                        chapter.totalChar = c.getString("totalChar");
                    if (c.has("name"))
                        chapter.name = c.getString("name");
                    if (c.has("link"))
                        chapter.link = c.getString("link");
                    chapter.book = b;
                    list.add(chapter);
                }
                b.chapters = list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    public Chapter parseChapter(String s) {
        Chapter r = new Chapter();
        try {
            JSONObject o = new JSONObject(s);
            if (o.has("content"))
                r.content = o.getString("content");
            if (o.has("name"))
                r.name = o.getString("name");
            if (o.has("linkBookByUrl"))
                r.linkBookByUrl = o.getString("linkBookByUrl");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    public List<Book> getAllBooks() {
        return bookList;
    }

    public void updateBook(Book o, Book n) {
        if (o.getAllChapterLink() == null)
            o.setAllChapterLink(n.allChapterLink);
        if (o.getAuthor() == null)
            o.setAuthor(n.getAuthor());
        if (o.getCat1() == null)
            o.setCat1(n.getCat1());
        if (o.getCat2() == null)
            o.setCat1(n.getCat2());
        if (o.getChapters() == null || o.getChapters().size() == 0)
            o.setChapters(n.getChapters());
        if (o.getDescription() == null)
            o.setDescription(n.getDescription());
        if (o.getHit() == null)
            o.setHit(n.getHit());
        if (o.getId() == null)
            o.setId(n.getId());
        if (o.getName() == null)
            o.setName(n.getName());
        if (o.getSite() == null)
            o.setSite(n.getSite());
        if (o.getTempLink() == null)
            o.setTempLink(n.getTempLink());
        if (o.getTotalChar() == null)
            o.setTotalChar(n.getTotalChar());
        if (o.getUpdateTime() == null)
            o.setUpdateTime(n.getUpdateTime());
        if (o.getLinkWithChapterUrl() == null)
            o.setUpdateTime(n.getLinkWithChapterUrl());
        // process chapters
        if (n.getChapters() == null) {
        } else if (o.getChapters() == null) {
            o.setChapters(n.getChapters());
        } else {
            //TODO: sync two chapters
        }

    }
}

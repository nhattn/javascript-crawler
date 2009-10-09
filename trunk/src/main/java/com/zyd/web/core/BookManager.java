package com.zyd.web.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zyd.web.Util;
import com.zyd.web.dom.Book;
import com.zyd.web.service.crawler.booklist;

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
                ret.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    public List<Book> getAllBooks(){
        return bookList;
    }
}

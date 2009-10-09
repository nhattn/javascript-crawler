package com.zyd.test;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;

import com.zyd.web.dom.Book;

public class TestBeanToXML {
    public static void main(String[] args) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLEncoder enc = new XMLEncoder(out);
        Book book = new Book();
        book.author = "зїеп";
        book.name = "bookname";
        
        Book[] books = new Book[1];
        books[0]= book;
        enc.writeObject(books);
        enc.close();
        System.out.println(new String(out.toByteArray(),"GBK"));
    }
}

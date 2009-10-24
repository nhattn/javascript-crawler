package com.zyd.ncore.dao;

import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

import com.zyd.core.dao.BookDao;
import com.zyd.core.dom.Book;
import com.zyd.core.dom.BookFilter;
import com.zyd.ncore.busi.ATestUtil;

public class TestBookDao extends TestCase {
    BookDao dao;

    @Override
    protected void setUp() throws Exception {
        dao = new BookDao();
    }

    public void testSingleOperations() throws Exception {
        int bookCount = 100;
        List<Book> books = ATestUtil.getBookList(bookCount);
        // add books
        for (Book book : books) {
            book.setId(null);
            assertNotNull(dao.addBook(book));
        }

        // test find book
        for (Book book : books) {
            Book foundBook = dao.findBookById(book);
            assertNotNull(foundBook);
            assertTrue(foundBook.equals(book));

            foundBook = dao.findBookByNameAuthor(book);
            assertNotNull(foundBook);
            assertTrue(foundBook.equals(book));
        }

        // make sure books are added correctly by comparing book count
        BookFilter filter = new BookFilter();
        filter.setStart(0);
        filter.setCount(Integer.MAX_VALUE);

        List<Book> books2 = dao.listBook(filter);

        assertEquals(bookCount, books2.size());
        HashSet<Book> bookSet = new HashSet<Book>(books);
        bookSet.removeAll(books2);
        assertEquals(0, bookSet.size());
    }

    public void testDeleteBooks() throws Exception {
        System.out.println(dao.deleteAllBooks());
    }

    public static void main(String[] args) throws Exception {
        TestBookDao d = new TestBookDao();
        d.setUp();
        d.testSingleOperations();
    }
}

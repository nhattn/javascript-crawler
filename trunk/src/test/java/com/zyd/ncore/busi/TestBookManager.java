package com.zyd.ncore.busi;

import java.util.List;

import junit.framework.TestCase;

import com.zyd.ncore.dom.Book;
import com.zyd.ncore.dom.BookFilter;
import com.zyd.ncore.dom.Chapter;

public class TestBookManager extends TestCase {
    BookManager bm;

    @Override
    protected void setUp() throws Exception {
        bm = BookManager.getInstance();
    }

    public void testAddBook() {
        bm.clearBooks();
        List<Book> books = ATestUtil.getBookList(100);
        int len = books.size();

        for (Book b : books) {
            b.setId(null);
            assertNotNull("Add book" + bm.addBook(b));
            assertNotNull("Set book id", b.getId());
        }

        assertEquals("Book total1", len, bm.getBookCount());

        for (Book b : books) {
            assertNull("Add duplicate book", bm.addBook(b));
        }

        assertEquals("Book total2", len, bm.getBookCount());
    }

    public void testFindBook() {
        bm.clearBooks();
        List<Book> books = ATestUtil.getBookList(100);

        for (Book b : books) {
            bm.addBook(b);
        }

        for (Book b : books) {
            assertNotNull("Find book1", bm.findBook(b));
        }

        for (Book b : books) {
            b.setName(b.getName() + ' ');
            assertNull("Find book2", bm.findBook(b));
        }

        assertNull("Find book3", bm.findBook(new Book()));
    }

    public void testAddChapterToBook() {
        bm.clearBooks();
        List<Book> books = ATestUtil.getBookList(100);
        for (Book b : books) {
            bm.addBook(b);
            for (int i = 0; i < 10; i++) {
                Chapter c = ATestUtil.getChapter();
                c.setId(null);
                c = bm.addChapterToBook(b, c);
                assertNotNull("Add chapter1", c);
                assertNotNull("Chapter Id is null", c.getId());
                // add again, should fail
                assertNull("Add chapter2", bm.addChapterToBook(b, c));
            }
        }
    }

    public void testFindChapterInBook() {
        bm.clearBooks();
        List<Book> books = ATestUtil.getBookList(100);
        for (Book b : books) {
            bm.addBook(b);
            for (int i = 0; i < 10; i++) {
                Chapter c = ATestUtil.getChapter();
                c.setName(b.getName() + ":chapter:" + i);
                bm.addChapterToBook(b, c);
            }
        }
        for (Book b : books) {
            for (int i = 0; i < 10; i++) {
                Chapter c = new Chapter();
                c.setName(b.getName() + ":chapter:" + i);
                Chapter nc = bm.findChapterInBook(b, c);
                assertNotNull("Find chapter in book 1", nc);
                assertTrue("Find chapter in book 2", c.getName().startsWith(b.getName()));
            }
        }
    }

    public void testListBook() {
        bm.clearBooks();
        // make sure returns correct number of books
        ATestUtil.buildModel(100, 50);
        BookFilter filter = new BookFilter();
        filter.setStart(0);
        filter.setCount(50);
        List<Book> books = bm.listBook(filter);
        assertEquals("List book1", 50, books.size());

        filter = new BookFilter();
        filter.setStart(0);
        filter.setCount(100);
        books = bm.listBook(filter);
        assertEquals("List book2", 100, books.size());

        filter = new BookFilter();
        filter.setStart(0);
        filter.setCount(102);
        books = bm.listBook(filter);
        assertEquals("List book3", 100, books.size());
    }

    public void testLoadBookChapter() {
        bm.clearBooks();
        ATestUtil.buildModel(100, 50);
        BookFilter filter = new BookFilter();
        filter.setStart(0);
        filter.setCount(100);
        List<Book> books = bm.listBook(filter);

        for (Book book : books) {
            List<Chapter> chapters = bm.loadBookChapter(book);
            assertNotNull("Load book chapter1", chapters);
            assertNotNull("Load book chapter1", book.getChapters());
            if (chapters.size() != 50) {
                chapters.size();
            }

            assertEquals("Chapter count", 50, chapters.size());
            for (Chapter c : chapters) {
                assertTrue("Chapter name", c.getName().startsWith(book.getName()));
            }
        }
    }
}

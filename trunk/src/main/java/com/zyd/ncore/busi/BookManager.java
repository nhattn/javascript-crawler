package com.zyd.ncore.busi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.zyd.ncore.Utils;
import com.zyd.ncore.dom.Book;
import com.zyd.ncore.dom.BookFilter;
import com.zyd.ncore.dom.Chapter;

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
        bookList.clear();
    }

    /**
     * 
     * @param book  
     * @return the new added book, with its id set. If book already exists, will return null.
     */
    public Book addBook(Book book) {
        Book r = findBook(book);
        if (r != null)
            return null;
        book.setId(Utils.nextBookId());
        bookCache.put(book.getName() + "$" + book.getAuthor(), book);
        bookList.add(book);
        return book;
    }

    private Book getBookByNameAuthor(String name, String author) {
        String k = name + "$" + author;
        return bookCache.get(k);
    }

    /**
     * find a book by name + author
     * TODO: what if different websites have same book with same name&author, 
     * but acutally are different content.
     * @param book must have name & author
     * @return the same book instance with everything populated or a new book instance. the book instance will not contain chapter information. 
     * to load chapter information, call loadBookChapter method.
     * should not modify it.
     */
    public Book findBook(Book book) {
        if (book.getName() == null || book.getAuthor() == null)
            return null;
        return getBookByNameAuthor(book.getName(), book.getAuthor());

    }

    /**
     * chapter should not be in the book already.
     * @param book must be a valid book instance got from findBook.
     * @param chapter must have name
     * @return the chapter object created, or if chapter is already 
     * in the book, return null.
     */
    public Chapter addChapterToBook(Book book, Chapter chapter) {
        Chapter nchapter = findChapterInBook(book, chapter);
        if (nchapter != null)
            return null;
        chapter.setId(Utils.nextChapterId());
        if (book.getChapters() == null) {
            book.setChapters(new ArrayList<Chapter>());
        }
        book.getChapters().add(chapter);
        chapter.setBook(book);
        return chapter;
    }

    /**
     * 
     * @param book must be a valid instance got from findBook
     * @param chapter must have name
     * @return chapter as found, or null if not found
     */
    public Chapter findChapterInBook(Book book, Chapter chapter) {
        List<Chapter> chapters = book.getChapters();
        if (chapters == null || chapters.size() == 0)
            return null;

        for (Chapter c : chapters) {
            if (c.getName() != null && c.getName().equals(chapter.getName())) {
                return c;
            }
        }
        return null;
    }

    public int getBookCount() {
        return bookCache.size();
    }

    /**
     * Get a list of books meets the criteria of the filter.
     * only start and count are supported now.
     * @param filter
     * @return
     */
    public List<Book> listBook(BookFilter filter) {
        List<Book> ret = new ArrayList<Book>();
        for (int i = filter.start; i < filter.start + filter.count; i++) {
            if (bookList.size() <= i)
                return ret;
            ret.add(bookList.get(i));
        }
        return ret;
    }

    /**
     * Given a book, load all chapters of the book, Book.chapter will be filled.
     * @param book should be a valid book loaded from findBook
     * @return also returns the same chapters loaded
     */
    public List<Chapter> loadBookChapter(Book book) {
        Book b = findBook(book);
        if (b != null)
            return b.chapters;
        return null;
    }

    /**
     * compares and updates oldBook with content in the newBook.
     * This won't update the chapter information
     * 
     * if both oldBook and newBook have an attribute but with different value,
     * the value in old book takes precedence.
     * 
     * this call will update the book store as well as oldBook.
     * 
     * if this book has a new all chapter url or cover url, 
     * also need to update the booksite table.
     * @param oldBook
     * @param newBook
     * @return
     */
    public boolean compareAndUpdateBook(Book oldBook, Book newBook) {
        boolean changed = false;
        if (!Utils.strictEqual(oldBook.getTotalChar(), newBook.getTotalChar()) && newBook.getTotalChar() != 0) {
            oldBook.setTotalChar(newBook.getTotalChar());
            changed = true;
        }
        if (!Utils.strictEqual(oldBook.getUpdateTime(), newBook.getUpdateTime()) && newBook.getUpdateTime() != null) {
            oldBook.setUpdateTime(newBook.getUpdateTime());
        }
        return changed;
    }
}

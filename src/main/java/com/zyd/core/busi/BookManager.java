package com.zyd.core.busi;

import java.util.Collections;
import java.util.List;

import com.zyd.core.Utils;
import com.zyd.core.dao.BookDao;
import com.zyd.core.dom.Book;
import com.zyd.core.dom.BookFilter;
import com.zyd.core.dom.Chapter;

public class BookManager {

	private static BookManager instance = new BookManager();
	private BookDao dao;

	private BookManager() {
		if (instance == null)
			instance = this;
	}

	public void setBookDao(BookDao dao) {
		this.dao = dao;
	}

	public static BookManager getInstance() {
		return instance;
	}

	public void clearBooks() {
		dao.deleteAllBooks();
		dao.deleteAllChapters();
	}

	/**
	 * 
	 * @param book
	 * @return the new added book, with its id set. If book already exists, will
	 *         return null.
	 */
	public Book addBook(Book book) {
		Book r = findBook(book, false);
		if (r != null)
			return null;
		return dao.addBook(book);
	}

	/**
	 * find a book by name + author TODO: what if different websites have same
	 * book with same name&author, but acutally are different content.
	 * 
	 * @param book
	 *            must have name & author
	 * @return the book found. the book instance will not contain chapter
	 *         information. to load chapter information, call loadBookChapter
	 *         method. should not modify it.
	 */
	public Book findBook(Book book, boolean loadChapter) {
		if (book.getName() != null && book.getAuthor() != null) {
			List<Book> books = dao.findBookByNameAuthor(book);
			if (books != null && books.size() > 0) {
				return books.get(0);
			} else {
				return null;
			}
		} else if (book.getId() != null) {
			return dao.findBookById(book);
		}
		return null;
	}

	/**
	 * chapter should not be in the book already.
	 * 
	 * @param book
	 *            must be a valid book instance got from findBook.
	 * @param chapter
	 *            must have name
	 * @return the chapter object created, or if chapter is already in the book,
	 *         return null.
	 */
	public Chapter addChapterToBook(Book book, Chapter chapter) {
		Chapter nchapter = findChapterInBook(book, chapter);
		if (nchapter != null)
			return null;
		dao.addChapterToBook(book, chapter);
		return chapter;
	}

	/**
	 * 
	 * @param book
	 *            must be a valid instance got from findBook
	 * @param chapter
	 *            must have name
	 * @return chapter as found, or null if not found
	 */
	public Chapter findChapterInBook(Book book, Chapter chapter) {
		List<Chapter> list = dao.findChapterInBookByName(book, chapter, false);
		if (list.size() == 0)
			return null;
		else if (list.size() == 1)
			return list.get(0);
		else {
			System.out
					.println("Error, more than one chapter matched the same name:"
							+ chapter.getName());
			return null;
		}
	}

	public List<Chapter> findChapterInBook(Book book, Chapter chapter,
			boolean useLikeInChapterName) {
		List<Chapter> list = dao.findChapterInBookByName(book, chapter,
				useLikeInChapterName);
		return list;
	}

	public int getBookCount() {
		return dao.getBookCount();
	}

	/**
	 * Get a list of books meets the criteria of the filter. only start and
	 * count are supported now.
	 * 
	 * @param filter
	 * @return
	 */
	public List<Book> listBook(BookFilter filter) {
		if (filter.getStart() < 0)
			filter.setStart(0);
		if (filter.getCount() == 0)
			return (List<Book>) Collections.EMPTY_LIST;
		return dao.listBook(filter);
	}

	/**
	 * Given a book, load all chapters of the book, Book.chapter will be filled.
	 * 
	 * @param book
	 *            should be a valid book loaded from findBook
	 * @return also returns the same chapters loaded
	 */
	public List<Chapter> loadBookChapter(Book book) {
		Book b = findBook(book, true);
		if (b != null)
			return b.chapters;
		return null;
	}
	
	public List<Chapter> loadBookChapters(String bookId){
		List<Chapter> chapters;
		String LoadBookChaptersSql = "select * from chapter where book_id=?";
		
		return null;
	}

	/**
	 * compares and updates oldBook with content in the newBook. This won't
	 * update the chapter information
	 * 
	 * if both oldBook and newBook have an attribute but with different value,
	 * the value in old book takes precedence.
	 * 
	 * this call will update the book store as well as oldBook.
	 * 
	 * if this book has a new all chapter url or cover url, also need to update
	 * the booksite table. //TODO: not implemented
	 * 
	 * @param oldBook
	 * @param newBook
	 * @return
	 */
	public boolean compareAndUpdateBook(Book oldBook, Book newBook) {
		boolean changed = false;
		if (!Utils.strictEqual(oldBook.getTotalChar(), newBook.getTotalChar())
				&& newBook.getTotalChar() != 0) {
			oldBook.setTotalChar(newBook.getTotalChar());
			changed = true;
		}
		if (!Utils
				.strictEqual(oldBook.getUpdateTime(), newBook.getUpdateTime())
				&& newBook.getUpdateTime() != null) {
			if (oldBook.getUpdateTime() == null
					|| (oldBook.getUpdateTime().getTime() < newBook
							.getUpdateTime().getTime())) {
				oldBook.setUpdateTime(newBook.getUpdateTime());
				changed = true;
			}
		}
		if (!Utils.strictEqual(oldBook.getHit(), newBook.getHit())
				&& newBook.getHit() != 0) {
			oldBook.setHit(newBook.getHit());
			changed = true;
		}
		if (oldBook.isFinished() != newBook.isFinished()
				&& newBook.isFinished() == true) {
			oldBook.setFinished(true);
			changed = true;
		}
		if (oldBook.getTotalChar() != newBook.getTotalChar()
				&& newBook.getTotalChar() > oldBook.getTotalChar()) {
			oldBook.setTotalChar(newBook.getTotalChar());
			changed = true;
		}
		return changed;
	}
}

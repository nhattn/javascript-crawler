package com.zyd.test.book.ncore.busi;

import java.util.List;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;

import com.zyd.core.busi.BookManager;
import com.zyd.core.dom.Book;
import com.zyd.core.dom.BookFilter;
import com.zyd.core.dom.Chapter;

public class TestBookManager extends TestCase {
	BookManager bm;

	@Override
	protected void setUp() throws Exception {
		ApplicationContext ctx = ATestUtil.setUpSpring();
		bm = (BookManager) ctx.getBean("bookManager");
	}

	public void testAddBook() {
		bm.deleteAllBooks();
		List<Book> books = ATestUtil.getBookList(100, false);
		int len = books.size();

		for (Book b : books) {
			b.setId(null);
			assertNotNull("Add book", bm.addBook(b));
			assertNotNull("Set book id", b.getId());
		}

		assertEquals("Book total1", len, bm.getBookCount());

		for (Book b : books) {
			assertNull("Add duplicate book", bm.addBook(b));
		}

		assertEquals("Book total2", len, bm.getBookCount());
	}

	public void testFindBook() {
		bm.deleteAllBooks();
		List<Book> books = ATestUtil.getBookList(100, false);

		for (Book b : books) {
			bm.addBook(b);
		}

		for (Book b : books) {
			assertNotNull("Find book1", bm.findBook(b));
		}

		for (Book b : books) {
			b.setName(b.getName() + " x ");
			assertNull("Find book2", bm.findBook(b));
		}

		assertNull("Find book3", bm.findBook(new Book()));
	}

	public void testAddChapterToBook() {
		bm.deleteAllBooks();
		List<Book> books = ATestUtil.getBookList(100, false);
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
		bm.deleteAllBooks();
		List<Book> books = ATestUtil.getBookList(100, true);
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
		bm.deleteAllBooks();
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
		int bookCount = 50, chapterCount = 100;
		bm.deleteAllBooks();
		ATestUtil.buildModel(bookCount, chapterCount);
		BookFilter filter = new BookFilter();
		filter.setStart(0);
		filter.setCount(100);
		List<Book> books = bm.listBook(filter);

		for (Book book : books) {
			List<Chapter> chapters = bm.loadBookChapter(book);
			assertNotNull("Load book chapter1", chapters);
			assertNotNull("Load book chapter1", book.getChapters());
			if (chapters.size() != chapterCount) {
				chapters.size();
			}

			assertEquals("Chapter count", chapterCount, chapters.size());
			for (Chapter c : chapters) {
				assertTrue("Chapter name", c.getName().startsWith(book.getName()));
			}
		}
	}
}

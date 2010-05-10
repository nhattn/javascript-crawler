package com.zyd.test.book.dao;

import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.springframework.context.ApplicationContext;

import com.zyd.core.dao.BookDao;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.Book;
import com.zyd.core.dom.BookFilter;
import com.zyd.core.dom.Chapter;
import com.zyd.test.book.ncore.busi.ATestUtil;

public class TestBookDao extends TestCase {
	BookDao dao;

	@Override
	protected void setUp() throws Exception {
		ApplicationContext ctx = ATestUtil.setUpSpring();
		dao = (BookDao) ctx.getBean("bookDao");
	}

	public void testSingleOperations() throws Exception {
		ATestUtil.clearData();
		int bookCount = 100;
		List<Book> books = ATestUtil.getBookList(bookCount, false);
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

			List<Book> foundBooks = dao.findBookByNameAuthor(book);
			assertNotNull(foundBooks);
			assertTrue(foundBooks.get(0).equals(book));
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

		filter.setStart(0);
		filter.setCount(10);
		List<Book> books3 = dao.listBook(filter);
		assertEquals(10, books3.size());

	}

	public void testDeleteBooks() throws Exception {
		dao.deleteAllBooks();
		int bookCount = 100;
		List<Book> books = ATestUtil.getBookList(bookCount, false);
		// add books
		for (Book book : books) {
			book.setId(null);
			assertNotNull(dao.addBook(book));
		}
		assertEquals(bookCount, dao.deleteAllBooks());
	}

	public static void main1(String[] args) {
		Book b = new Book();
		Chapter c = new Chapter();
		BookDao dao = new BookDao();

		b.setName("book name");
		b.setAuthor("book author");
		b.setId("bookid");

		c.setName("Chapter");
		c.setId("chapterid");
		c.setSequence(1);
		c.setBook(b);
		b.addChapter(c);

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.save(b);
		session.getTransaction().commit();
	}

	public static void main2(String[] args) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		// Book book = (Book) session.load(Book.class, "bookid");
		Book book = (Book) session.createQuery("from Book").list().iterator().next();
		List<Chapter> cc = book.getChapters();
		System.out.println(cc);
		Chapter c = ATestUtil.getChapter();
		c.setSequence(5);
		c.setBook(book);
		book.addChapter(c);
		session.getTransaction().commit();

	}

	public static void mainx(String[] args) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		System.out.println(session.createQuery("select count(*) from Book").uniqueResult());
		Book book = new Book();
		book.setId("bookid");
		List list = session.createQuery("from Chapter c where c.book.name = :book").setParameter("book", "book name").list();
		System.out.println(list);
		session.getTransaction().commit();

	}

}

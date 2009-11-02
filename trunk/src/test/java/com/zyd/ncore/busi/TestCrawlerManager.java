package com.zyd.ncore.busi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;

import junit.framework.TestCase;

import com.zyd.core.busi.BookManager;
import com.zyd.core.busi.CrawlerManager;
import com.zyd.core.busi.SiteManager;
import com.zyd.core.dom.Book;
import com.zyd.core.dom.BookFilter;
import com.zyd.core.dom.BookSite;
import com.zyd.core.dom.Chapter;
import com.zyd.core.dom.ChapterSite;
import com.zyd.core.dom.Site;

public class TestCrawlerManager extends TestCase {
	CrawlerManager cm;
	BookManager bm;
	SiteManager sm;

	@Override
	protected void setUp() throws Exception {
		ApplicationContext ctx = ATestUtil.setUpSpring();
		bm = (BookManager) ctx.getBean("bookManager");
		sm = (SiteManager) ctx.getBean("siteManager");
		cm = (CrawlerManager) ctx.getBean("crawlerManager");
	}

	public void testProcessBookList() {
		cm.clearAll();
		String url = "http://www.qidian.com/Book/BookStore.aspx?Type=Cmd&F=W";
		String jsonList = ATestData.book4;
		assertEquals(100, cm.processBookList(jsonList, url));
		// validate book count
		BookFilter filter = new BookFilter();
		filter.setStart(0);
		filter.setCount(Integer.MAX_VALUE);
		assertNotNull(bm.listBook(filter));
		assertEquals(100, ATestUtil.getUniqueObjectCount(bm.listBook(filter)));
		assertEquals(100, bm.getBookCount());

		// valid site count
		assertEquals(1, sm.getSiteCount());

		// valid book-site link
		Site site = sm.findSiteByUrl(url);
		List<Book> books = bm.listBook(filter);
		List<Book> foundBooks = new ArrayList<Book>();
		for (Book book : books) {
			BookSite bs = sm.findBookInSite(book, site);
			foundBooks.add(bs.getBook());
			assertEquals(bs.getSite(), site);
		}
		assertEquals(books.size(), ATestUtil.getUniqueObjectCount(foundBooks));

		// ***** add some more book from different site
		url = url.replace("www.qidian.com", "www.17k.com");
		jsonList = jsonList.replace("www.qidian.com", "www.17k.com");
		cm.processBookList(jsonList, url);

		// total book should not change
		assertNotNull(bm.listBook(filter));
		assertEquals(100, ATestUtil.getUniqueObjectCount(bm.listBook(filter)));
		assertEquals(100, bm.getBookCount());

		// valid site count
		assertEquals(2, sm.getSiteCount());

		// valid book-site link
		Site site2 = sm.findSiteByUrl(url);
		books = bm.listBook(filter);
		foundBooks = new ArrayList<Book>();
		for (Book book : books) {
			BookSite bs = sm.findBookInSite(book, site);
			foundBooks.add(bs.getBook());
			assertNotNull(bs.getSite());
			assertEquals(bs.getSite(), site);
			assertEquals(bs.getSite().getDomainName(), "www.qidian.com");

			bs = sm.findBookInSite(book, site2);
			assertNotNull(bs.getSite());
			assertEquals(bs.getSite(), site2);
			assertEquals(bs.getSite().getDomainName(), "www.17k.com");
			// find sites for book

			List<BookSite> bookSites = sm.findSiteForBook(book);
			assertNotNull(bookSites);
			assertEquals(2, bookSites.size());
			int counter = 0;
			for (BookSite bookSite : bookSites) {
				assertNotNull(bookSite.getSite());
				assertNotNull(bookSite.getSite().getDomainName());
				if (bookSite.getSite().getDomainName().equals("www.qidian.com")) {
					counter = counter + 1;
				} else if (bookSite.getSite().getDomainName().equals("www.17k.com")) {
					counter = counter + 2;
				}
			}
			assertEquals(3, counter);
		}
		assertEquals(books.size(), ATestUtil.getUniqueObjectCount(foundBooks));
	}

	public void testProcessChapter() {
		cm.clearAll();
		String s = ATestData.bookchapters;
		String url = "http://www.qidian.com";
		assertEquals(true, cm.processBook(s, url));
		BookFilter filter = new BookFilter();
		filter.setStart(0);
		filter.setCount(Integer.MAX_VALUE);

		List<Book> books = bm.listBook(filter);
		assertNotNull(books);
		assertEquals(1, books.size());

		Book book = books.get(0);
		List<Chapter> chapters = bm.loadBookChapter(book);
		assertNotNull(chapters);
		assertEquals(500, chapters.size());
		Site site1 = sm.findSiteByUrl(url);
		assertNotNull(site1);
		Set<Chapter> foundChapters = new HashSet<Chapter>();

		// find books
		for (Chapter chapter : chapters) {
			Chapter c = bm.findChapterInBook(book, chapter);
			foundChapters.add(c);

			ChapterSite cs = sm.findChapterInSite(c, site1);
			assertNotNull(cs);
			assertNotNull(cs.getSite());
			assertEquals(site1.getDomainName(), cs.getSite().getDomainName());
		}
		assertEquals(500, foundChapters.size());

		// add someMore chapters from another site

		s = s.replace("qidian", "17k");
		url = url.replace("qidian", "17k");
		cm.processBook(s, url);
		Site site2 = sm.findSiteByUrl(url);
		assertNotNull(site2);
		assertEquals("www.17k.com", site2.getDomainName());

		books = bm.listBook(filter);
		assertNotNull(books);
		assertEquals(1, books.size());

		book = books.get(0);
		chapters = bm.loadBookChapter(book);
		assertNotNull(chapters);
		assertEquals(500, chapters.size());
		assertNotNull(site1);

		foundChapters = new HashSet<Chapter>();
		for (Chapter chapter : chapters) {
			// find chapters
			Chapter c = bm.findChapterInBook(book, chapter);
			ChapterSite cs1 = sm.findChapterInSite(chapter, site1);
			ChapterSite cs2 = sm.findChapterInSite(chapter, site2);
			assertNotNull(cs1);
			assertNotNull(cs2);
			assertEquals(cs1.getSite(), site1);
			assertEquals(cs2.getSite(), site2);
			foundChapters.add(chapter);

			// find which sites this chapter is in
			List<ChapterSite> chapterSites = sm.findSiteForChapter(chapter);
			assertNotNull(chapterSites);
			assertEquals(2, chapterSites.size());
			int count = 0;
			for (ChapterSite cs : chapterSites) {
				assertNotNull(cs.getSite());
				assertNotNull(cs.getSite().getDomainName());
				if (cs.getSite().getDomainName().equals("www.17k.com")) {
					count = count + 1;
				} else if (cs.getSite().getDomainName().equals("www.qidian.com")) {
					count = count + 2;
				}

			}
			assertEquals(3, count);
		}
		assertEquals(500, foundChapters.size());
	}
}

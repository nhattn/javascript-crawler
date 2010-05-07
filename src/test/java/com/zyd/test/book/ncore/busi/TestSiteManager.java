package com.zyd.test.book.ncore.busi;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import junit.framework.TestCase;

import com.zyd.core.busi.BookManager;
import com.zyd.core.busi.SiteManager;
import com.zyd.core.dom.Book;
import com.zyd.core.dom.BookSite;
import com.zyd.core.dom.Chapter;
import com.zyd.core.dom.ChapterSite;
import com.zyd.core.dom.Site;

public class TestSiteManager extends TestCase {
	BookManager bm;
	SiteManager sm;

	@Override
	protected void setUp() throws Exception {
		ApplicationContext ctx = ATestUtil.setUpSpring();
		bm = (BookManager) ctx.getBean("bookManager");
		sm = (SiteManager) ctx.getBean("siteManager");
	}

	public void testAddSite() {
		ATestUtil.clearData();
		Site site = sm.addSite("http://www.aa.com");
		assertNotNull("Add Site 0", site);
		assertNotNull("Add site, site id " + site.getId());
		assertEquals("Add site 1", "www.aa.com", site.getDomainName());

		site = sm.addSite("http://bb.com");
		assertEquals("Add site 2", "www.bb.com", site.getDomainName());

		site = sm.addSite("https://cc.dd.com");
		assertEquals("Add site 3", "www.dd.com", site.getDomainName());

		site = sm.addSite("https://ii.ff.com/ur1/ur2");
		assertEquals("Add site 3", "www.ff.com", site.getDomainName());

		assertNull("Add site 4", sm.addSite("aa.com"));

		// test load site
		assertNotNull("Load site 1", sm.findSiteByUrl("http://www.aa.com"));
		assertNotNull("Load site 2", sm.findSiteByUrl("http://bb.com"));
		assertNotNull("Load site 3", sm.findSiteByUrl("https://cc.dd.com"));
		assertNotNull("Load site 3", sm.findSiteByUrl("https://ii.ff.com/ur1/ur2"));

		assertEquals("Load site 4", "www.aa.com", sm.findSiteByUrl("http://www.aa.com").getDomainName());
		assertEquals("Load site 5", "www.aa.com", sm.findSiteByUrl("https://bb.aa.com").getDomainName());
	}

	public void testAddBookToSite() {
		ATestUtil.clearData();
		List<Book> books = ATestUtil.getBookList(100, true);
		List<Site> sites = ATestUtil.getSiteList(true);

		for (int i = 0; i < books.size(); i++) {
			Book b = books.get(i);
			Site s1 = sites.get(i % sites.size());
			BookSite bs1 = sm.addBookToSite(b, s1);
			assertNotNull("Add book to site 1 ", bs1);
			assertNotNull("Add book to site 2 ", bs1.getId());

			// test find a book
			BookSite bs2 = sm.findBookInSite(b, s1);
			assertNotNull("Add book to site 3 ", bs1);
			assertEquals("Add book to site 4", bs1.getId(), bs2.getId());
			assertEquals("Add book to site 5", b.getId(), bs1.getBook().getId());
			assertEquals("Add book to site 6", s1.getId(), bs1.getSite().getId());

			// test add book to another site
			Site s2 = sites.get((i + 1) % sites.size());
			BookSite bs3 = sm.addBookToSite(b, s2);
			BookSite bs4 = sm.findBookInSite(b, s2);

			assertNotNull("Add book to site 7 ", bs3);
			assertNotNull("Add book to site 8 ", bs4);
			assertEquals("Add book to site 9", bs3.getId(), bs4.getId());
			assertEquals("Add book to site 10", b.getId(), bs3.getBook().getId());
			assertEquals("Add book to site 11", s2.getId(), bs3.getSite().getId());

			List<BookSite> bookSites = sm.findSiteForBook(b);
			assertNotNull(bookSites);
			assertEquals(2, bookSites.size());

			int counter = 0;
			for (BookSite bookSite : bookSites) {
				if (bookSite.getId().equals(bs1.getId())) {
					counter = counter + 1;
				} else if (bookSite.getId().equals(bs3.getId())) {
					counter = counter + 2;
				}
			}
			assertEquals(3, counter);
		}
	}

	public void testAddChapterToSite() {
		ATestUtil.clearData();
		List<Chapter> chapters = new ArrayList<Chapter>();
		List<Site> sites = ATestUtil.getSiteList(true);

		for (int i = 0; i < 100; i++) {
			Chapter c = ATestUtil.getChapter();
			Site s1 = sites.get(i % sites.size());
			ChapterSite cs1 = sm.addChapterToSite(c, s1);
			assertNotNull(cs1);
			assertNotNull(cs1.getId());

			// test find a chapter
			ChapterSite cs2 = sm.findChapterInSite(c, s1);
			assertNotNull(cs2);
			assertEquals(cs1.getId(), cs2.getId());
			assertEquals(c.getId(), cs1.getChapter().getId());
			assertEquals(s1.getId(), cs1.getSite().getId());

			// test add chapter to another site
			Site s2 = sites.get((i + 1) % sites.size());
			ChapterSite cs3 = sm.addChapterToSite(c, s2);
			ChapterSite cs4 = sm.findChapterInSite(c, s2);

			assertNotNull(cs3);
			assertNotNull(cs4);
			assertEquals(cs3.getId(), cs4.getId());
			assertEquals(c.getId(), cs3.getChapter().getId());
			assertEquals(s2.getId(), cs3.getSite().getId());

			List<ChapterSite> chapterSites = sm.findSiteForChapter(c);
			assertNotNull(chapterSites);
			assertEquals(2, chapterSites.size());

			int counter = 0;
			for (ChapterSite chapterSite : chapterSites) {
				if (chapterSite.getId().equals(cs1.getId())) {
					counter = counter + 1;
				} else if (chapterSite.getId().equals(cs3.getId())) {
					counter = counter + 2;
				}
			}
			assertEquals(3, counter);
		}

	}
}

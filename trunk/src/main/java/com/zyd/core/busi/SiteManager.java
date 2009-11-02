package com.zyd.core.busi;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.zyd.core.Utils;
import com.zyd.core.dao.SiteDao;
import com.zyd.core.dom.Book;
import com.zyd.core.dom.BookSite;
import com.zyd.core.dom.Chapter;
import com.zyd.core.dom.ChapterSite;
import com.zyd.core.dom.Site;

public class SiteManager {
	private static SiteManager instance = new SiteManager();

	private JdbcTemplate jt = null;
	private SiteDao dao = null;

	public void setDataSource(DataSource ds) {
		jt = new JdbcTemplate(ds);
	}

	public void setSiteDao(SiteDao dao) {
		this.dao = dao;
	}

	/**
	 * book and site must be loaded with system call, having valid values.
	 * 
	 * @param book
	 * @param site
	 * @return the new created object , or null if it's already there.
	 */
	public BookSite addBookToSite(Book book, Site site) {
		if (findBookInSite(book, site) != null)
			return null;
		BookSite bookSite = new BookSite();
		bookSite.setBook(book);
		bookSite.setSite(site);
		bookSite.setAllChapterUrl(book.getAllChapterUrl());
		bookSite.setCoverUrl(book.getCoverUrl());
		bookSite.setUpdateTime(new Date());
		bookSite = dao.addBookSite(bookSite);
		return bookSite;
	}

	/**
	 * update book information on that site. this won't update chapter
	 * information.
	 * 
	 * @param book
	 * @param site
	 * @return
	 */
	public boolean updateBookSite(Book book, Site site) {
		/*
		 * BookSite bookSite = findBookInSite(book, site); if (bookSite == null)
		 * { // TODO: error or add book to site return false; }
		 * bookSite.setAllChapterUrl(Utils.getUpdateObject(bookSite
		 * .getAllChapterUrl(), book.getAllChapterUrl()));
		 * bookSite.setCoverUrl(Utils.getUpdateObject(bookSite.getCoverUrl(),
		 * book .getCoverUrl()));
		 * bookSite.setUpdateTime(Utils.getUpdateObject(bookSite
		 * .getUpdateTime(), book.getUpdateTime())); // TODO: should tell if
		 * book is changed
		 */
		throw new UnsupportedOperationException();
	}

	public BookSite findBookInSite(Book book, Site site) {
		return dao.findBookInSite(book, site);
	}

	public List<BookSite> findSiteForBook(Book book) {
		return dao.findSiteForBook(book);
	}

	/**
	 * add a chapter to a particular site. the chapter must have an book
	 * associated with it.
	 * 
	 * @param chapter
	 * @param site
	 * @return
	 */
	public ChapterSite addChapterToSite(Chapter chapter, Site site) {
		if (findChapterInSite(chapter, site) != null)
			return null;
		ChapterSite chapterSite = new ChapterSite();
		chapterSite.setChapter(chapter);
		chapterSite.setSite(site);
		chapterSite.setId(Utils.nextChapterSiteId());
		chapterSite.setUpdateTime(chapter.getUpdateTime());
		chapterSite.setUrl(chapter.getChapterUrl());
		chapterSite = dao.addChapterSite(chapterSite);
		return chapterSite;
	}

	/**
	 * find a chapter on a particular site.
	 * 
	 * @param chapter
	 *            chapter to add, must have an id
	 * @param book
	 *            book for this chapter must have an id
	 * @param site
	 *            must have an id
	 * @return
	 */
	public ChapterSite findChapterInSite(Chapter chapter, Site site) {
		if (chapter == null || site == null)
			return null;
		ChapterSite r = dao.findChapterInSite(chapter, site);
		return r;
	}

	public List<ChapterSite> findSiteForChapter(Chapter chapter) {
		if (chapter == null)
			return Collections.EMPTY_LIST;
		List<ChapterSite> r = dao.findSiteForChapter(chapter);
		return r;
	}

	/**
	 * s is an url with domain name. returns the site associated with it.
	 * 
	 * @param s
	 * @return
	 */
	public Site findSiteByUrl(String s) {
		if (s == null)
			return null;
		String domain = Utils.getDomain(s);
		return dao.findSiteByDomainName(domain);
	}

	/**
	 * 
	 * @param url
	 * @return the new added site with everything set(id)
	 */
	public Site addSite(String url) {
		if (findSiteByUrl(url) != null)
			return null;
		String domain = Utils.getDomain(url);
		Site site = new Site();
		site.setId(Utils.nextSiteId());
		site.setName(domain);
		site.setDomainName(domain);
		site.setUrl("http://" + domain);
		return dao.addSite(site);
	}

	public int getSiteCount() {
		return dao.getSiteCount();
	}

	public void deleteAllSites() {
		int n = dao.deleteAllSites();
	}
}

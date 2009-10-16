package com.zyd.core.busi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.zyd.core.Utils;
import com.zyd.core.dom.Book;
import com.zyd.core.dom.BookSite;
import com.zyd.core.dom.Chapter;
import com.zyd.core.dom.ChapterSite;
import com.zyd.core.dom.Site;

public class SiteManager {
    private static SiteManager instance = new SiteManager();

    private static HashMap<String, HashMap<String, BookSite>> siteBookCache = new HashMap<String, HashMap<String, BookSite>>();
    private static HashMap<String, HashMap<String, ChapterSite>> siteChapterCache = new HashMap<String, HashMap<String, ChapterSite>>();
    private static HashMap<String, Site> siteCache = new HashMap<String, Site>();

    public static SiteManager getInstance() {
        return instance;
    }

    /**
     * book and site must be loaded with system call, having valid values.
     * @param book
     * @param site
     * @return the new created object , or null if it's already there.
     */
    public BookSite addBookToSite(Book book, Site site) {
        if (findBookInSite(book, site) != null)
            return null;
        String siteKey = site.getDomainName();
        HashMap<String, BookSite> bookMap = siteBookCache.get(siteKey);
        if (bookMap == null) {
            bookMap = new HashMap<String, BookSite>();
            siteBookCache.put(siteKey, bookMap);
        }
        BookSite bookSite = new BookSite();
        bookSite.setId(Utils.nextBookSiteId());
        bookSite.setBook(book);
        bookSite.setSite(site);
        bookSite.setAllChapterUrl(book.getAllChapterUrl());
        bookSite.setCoverUrl(book.getCoverUrl());
        bookMap.put(book.getId(), bookSite);
        return bookSite;
    }

    /**
     * update book information on that site. 
     * this won't update chapter information.
     * @param book
     * @param site
     * @return
     */
    public boolean updateBookSite(Book book, Site site) {
        BookSite bookSite = findBookInSite(book, site);
        if (bookSite == null) {
            //TODO: error or add book to site
            return false;
        }
        bookSite.setAllChapterUrl(Utils.getUpdateObject(bookSite.getAllChapterUrl(), book.getAllChapterUrl()));
        bookSite.setCoverUrl(Utils.getUpdateObject(bookSite.getCoverUrl(), book.getCoverUrl()));
        bookSite.setUpdateTime(Utils.getUpdateObject(bookSite.getUpdateTime(), book.getUpdateTime()));
        //TODO: should tell if book is changed
        throw new UnsupportedOperationException();
    }

    public BookSite findBookInSite(Book book, Site site) {
        String siteKey = site.getDomainName();
        HashMap<String, BookSite> bookMap = siteBookCache.get(siteKey);
        if (bookMap == null)
            return null;
        return bookMap.get(book.getId());
    }

    public List<BookSite> findSiteForBook(Book book) {
        List<BookSite> r = new ArrayList<BookSite>();
        Collection<HashMap<String, BookSite>> values = siteBookCache.values();
        String k = book.getId();
        for (HashMap<String, BookSite> map : values) {
            BookSite s = map.get(k);
            if (s != null) {
                r.add(s);
            }

        }
        return r;
    }

    /**
     * add a chapter to a particular site. the chapter must have an book associated with it.
     * @param chapter
     * @param site
     * @return
     */
    public ChapterSite addChapterToSite(Chapter chapter, Site site) {
        if (findChapterInSite(chapter, site) != null)
            return null;
        String siteKey = site.getDomainName();
        HashMap<String, ChapterSite> chapterMap = siteChapterCache.get(siteKey);
        if (chapterMap == null) {
            chapterMap = new HashMap<String, ChapterSite>();
            siteChapterCache.put(siteKey, chapterMap);
        }
        ChapterSite chapterSite = new ChapterSite();
        chapterSite.setChapter(chapter);
        chapterSite.setSite(site);
        chapterSite.setId(Utils.nextChapterSiteId());
        chapterSite.setUpdateTime(chapter.getUpdateTime());
        chapterSite.setUrl(chapter.getChapterUrl());

        chapterMap.put(chapter.getId(), chapterSite);
        return chapterSite;
    }

    /**
     * find a chapter on a particular site.
     * @param chapter chapter to add, must have an id
     * @param book book for this chapter must have an id
     * @param site must have an id
     * @return
     */
    public ChapterSite findChapterInSite(Chapter chapter, Site site) {
        String siteKey = site.getDomainName();
        HashMap<String, ChapterSite> chapterMap = siteChapterCache.get(siteKey);
        if (chapterMap == null)
            return null;
        return chapterMap.get(chapter.getId());
    }

    public List<ChapterSite> findSiteForChapter(Chapter chapter) {
        List<ChapterSite> r = new ArrayList<ChapterSite>();
        Collection<HashMap<String, ChapterSite>> values = siteChapterCache.values();
        String k = chapter.getId();
        for (HashMap<String, ChapterSite> map : values) {
            ChapterSite s = map.get(k);
            if (s != null)
                r.add(s);
        }
        return r;
    }

    /**
     * s is an url with domain name.  returns the site associated with it.
     * @param s
     * @return
     */
    public Site findSiteByUrl(String s) {
        if (s == null)
            return null;
        String domain = Utils.getDomain(s);
        return siteCache.get(domain);
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
        siteCache.put(domain, site);
        return site;
    }

    public int getSiteCount() {
        return siteCache.size();
    }

    public void clearSites() {
        siteCache.clear();
    }

    public void clearBook() {
        siteBookCache.clear();
    }

    public void clearChapter() {
        siteChapterCache.clear();
    }

    public void clearCache() {
        clearSites();
        clearBook();
        clearChapter();
    }
}

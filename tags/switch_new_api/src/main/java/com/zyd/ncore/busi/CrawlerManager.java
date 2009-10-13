package com.zyd.ncore.busi;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.zyd.ncore.dom.Book;
import com.zyd.ncore.dom.BookSite;
import com.zyd.ncore.dom.Chapter;
import com.zyd.ncore.dom.ChapterSite;
import com.zyd.ncore.dom.Site;

public class CrawlerManager {
    private static CrawlerManager instance = new CrawlerManager();

    private BookManager bookManager;
    private JSonMapper json;
    private SiteManager siteManager;
    private LinkManager linkManager;

    private CrawlerManager() {
        bookManager = BookManager.getInstance();
        json = JSonMapper.getInstance();
        siteManager = SiteManager.getInstance();
        linkManager = LinkManager.getInstance();
    }

    public static CrawlerManager getInstance() {
        return instance;
    }

    /**
     * processes a request with a book list, the request should contain "data" as parameter,
     * with a json string, containing a list of books. These books should not contain any chapter 
     * information, or these chapters will be ignored.
     * @param s a json object posted from browser
     * @return the number of books added for this call.
     */
    public int processBookList(HttpServletRequest req) {
        String fromUrl = req.getHeader("Referer"), jsonBookListString = req.getParameter("data");
        return processBookList(jsonBookListString, fromUrl);
    }

    public int processBookList(String jsonBookListString, String fromUrl) {
        List<Book> books = json.parseBookList(jsonBookListString), booksWithId = new ArrayList<Book>();

        // process books
        int added = 0;
        for (Book book : books) {
            AResult r = addBookIfNecessary(book);
            if (r.added == true)
                added++;
            booksWithId.add(r.book);
        }

        // process sites
        Site site = createSiteIfNecessary(fromUrl).site;

        for (Book book : booksWithId) {
            addBookToSiteIfNecessary(book, site);
        }
        return added;
    }

    /**
     * process a request with a book, the reuqest should conain "data" as parameter,
     * with a json string, containing a list of chapters or more information of the book.
     * the book should at least have an author and name. or it will be ignored.  
     * @param s
     * @return whether or not anything has changed.
     */
    public boolean processBook(HttpServletRequest req) {
        String fromUrl = req.getHeader("Referer"), jsonBookListString = req.getParameter("data");
        return processBook(jsonBookListString, fromUrl);
    }

    public boolean processBook(String jsonBookListString, String fromUrl) {
        Book book = json.parseBook(jsonBookListString), bookWithId = null;
        boolean changed = false;

        AResult r = addBookIfNecessary(book);
        bookWithId = r.book;
        changed = r.added || r.updated;

        r = createSiteIfNecessary(fromUrl);
        Site site = r.site;
        changed = changed || r.added || r.updated;

        r = addBookToSiteIfNecessary(bookWithId, site);
        changed = changed || r.added;

        // process chapter        
        List<Chapter> chapters = bookManager.loadBookChapter(bookWithId);
        if (chapters != null && chapters.size() > 0) {
            for (Chapter chapter : chapters) {
                r = addChapterToBookIfNecessary(bookWithId, chapter);
                Chapter chapterWithId = r.chapter;
                changed = changed || r.added;

                r = addChapterToSiteIfNecessary(bookWithId, chapterWithId, site);
                changed = changed || r.added;
            }
        }
        return changed;
    }

    private AResult addBookIfNecessary(Book book) {
        AResult r = new AResult();
        Book bookWithId = bookManager.findBook(book);
        if (bookWithId == null) {
            bookWithId = bookManager.addBook(book);
            r.added = true;
        } else {
            // if book is already in system, update it if necessary.
            r.updated = bookManager.compareAndUpdateBook(bookWithId, book);
        }
        if (book.getUrlToCrawl() != null) {
            linkManager.addLink(book.getUrlToCrawl());
        }
        r.book = bookWithId;
        return r;
    }

    private AResult addBookToSiteIfNecessary(Book book, Site site) {
        AResult r = new AResult();
        BookSite bookSite = siteManager.findBookInSite(book, site);
        if (bookSite == null) {
            bookSite = siteManager.addBookToSite(book, site);
            r.added = true;
        }
        r.bookSite = bookSite;
        return r;
    }

    private AResult addChapterToBookIfNecessary(Book book, Chapter chapter) {
        AResult r = new AResult();
        Chapter chapterWithId = bookManager.findChapterInBook(book, chapter);
        if (chapterWithId == null) {
            chapterWithId = bookManager.addChapterToBook(book, chapter);
            r.added = true;
        }
        r.chapter = chapterWithId;
        return r;
    }

    private AResult addChapterToSiteIfNecessary(Book book, Chapter chapter, Site site) {
        AResult r = new AResult();
        ChapterSite chapterSite = siteManager.findChapterInSite(chapter, site);
        if (chapterSite == null) {
            r.chapterSite = siteManager.addChapterToSite(chapter, site);
            r.added = true;
        }
        r.chapterSite = chapterSite;
        return r;
    }

    private AResult createSiteIfNecessary(String url) {
        AResult r = new AResult();
        Site site = siteManager.findSiteByUrl(url);
        if (site == null) {
            site = siteManager.addSite(url);
            r.added = true;
        }
        r.site = site;
        return r;
    }

    protected static class AResult {
        Book book = null;
        Site site = null;
        Chapter chapter = null;
        BookSite bookSite = null;
        ChapterSite chapterSite = null;
        boolean added = false;
        boolean updated = false;
    }

    public void clearAll() {
        this.siteManager.clearSites();
        this.linkManager.clearLinks();
        this.bookManager.clearBooks();
    }
}

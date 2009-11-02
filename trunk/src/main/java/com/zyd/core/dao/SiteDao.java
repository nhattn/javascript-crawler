package com.zyd.core.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.zyd.core.Utils;
import com.zyd.core.dom.Book;
import com.zyd.core.dom.BookSite;
import com.zyd.core.dom.Chapter;
import com.zyd.core.dom.ChapterSite;
import com.zyd.core.dom.Site;

public class SiteDao {
	private JdbcTemplate jt = null;
	private BookDao bookDao;

	public void setDataSource(DataSource ds) {
		jt = new JdbcTemplate(ds);
	}

	public void setBookDao(BookDao dao) {
		this.bookDao = dao;
	}

	public Site addSite(Site site) {
		final String AddSiteSql = "insert into site(site_id, name, domainName, url) values(?,?,?,?)";
		site.setId(Utils.nextSiteId());
		Object[] values = new Object[] { site.getId(), site.getName(), site.getDomainName(), site.getUrl() };
		int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };
		jt.update(AddSiteSql, values, types);
		return site;
	}

	public Site findSiteByDomainName(String domainName) {
		if (StringUtils.isBlank(domainName))
			return null;
		final String FindSiteByDomainNameSql = "select * from site where domainName=?";
		Object[] values = new Object[] { domainName };
		int[] types = new int[] { Types.VARCHAR };
		try {
			return (Site) jt.queryForObject(FindSiteByDomainNameSql, values, types, new SiteRowMapper());
		} catch (EmptyResultDataAccessException e) {
		}
		return null;
	}

	/**
	 * fill the passed site object with data from db.
	 * 
	 * @param site
	 *            must have an id
	 */
	public Site loadSiteById(Site site) {
		if (StringUtils.isBlank(site.getId()))
			return null;
		final String FindSiteByDomainNameSql = "select * from site where site_id=?";
		Object[] values = new Object[] { site.getId() };
		int[] types = new int[] { Types.VARCHAR };
		try {
			return (Site) jt.queryForObject(FindSiteByDomainNameSql, values, types, new SiteRowMapper(site));
		} catch (EmptyResultDataAccessException e) {
		}
		return null;
	}

	/**
	 * the list of ChapterSite will be returned
	 * 
	 * @param chapterId
	 * @return
	 */
	public List<ChapterSite> findSiteForChapter(Chapter chapter) {
		if (chapter == null || StringUtils.isBlank(chapter.getId()))
			return Collections.EMPTY_LIST;
		final String FindSiteForChapterSql = "select * from chaptersite where chapter_id=?";
		Object[] values = new Object[] { chapter.getId() };
		int[] types = new int[] { Types.VARCHAR };
		@SuppressWarnings("unchecked")
		List<ChapterSite> r = (List<ChapterSite>) jt.query(FindSiteForChapterSql, values, types, new ChapterSiteResultsetExtractor(chapter, null));
		return r;
	}

	public ChapterSite findChapterInSite(Chapter chapter, Site site) {
		String chapterId = chapter.getId(), siteId = site.getId();
		if (StringUtils.isBlank(chapterId) || StringUtils.isBlank(siteId))
			return null;
		final String FindChapterInSiteSql = "select * from chaptersite where chapter_id=? and site_id=?";
		Object[] values = new Object[] { chapterId, siteId };
		int[] types = new int[] { Types.VARCHAR, Types.VARCHAR };

		try {
			return (ChapterSite) jt.queryForObject(FindChapterInSiteSql, values, types, new ChapterSiteRowMapper(chapter, site));
		} catch (EmptyResultDataAccessException e) {
		}
		return null;
	}

	public ChapterSite addChapterSite(ChapterSite chapterSite) {
		if (chapterSite == null || chapterSite.getChapter() == null || chapterSite.getSite() == null)
			return null;
		final String AddChapterToSiteSql = "insert into chaptersite(chaptersite_id, chapter_id, site_id, updatetime, url) values(?,?,?,?,?)";
		chapterSite.setId(Utils.nextChapterSiteId());
		Object[] values = new Object[] { chapterSite.getId(), chapterSite.getChapter().getId(), chapterSite.getSite().getId(), chapterSite.getUpdateTime(), chapterSite.getUrl() };
		int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.VARCHAR };
		jt.update(AddChapterToSiteSql, values, types);
		return chapterSite;
	}

	public List<BookSite> findSiteForBook(Book book) {
		if (book == null || StringUtils.isBlank(book.getId()))
			return Collections.EMPTY_LIST;
		final String FindSiteForBook = "select * from booksite where book_id=?";
		Object[] values = new Object[] { book.getId() };
		int types[] = new int[] { Types.VARCHAR };
		@SuppressWarnings("unchecked")
		List<BookSite> r = (List<BookSite>) jt.query(FindSiteForBook, values, types, new BookSiteResultSetExtractor(book, null));
		return r;
	}

	public BookSite findBookInSite(Book book, Site site) {
		final String FindBookInSiteSql = "select * from booksite where book_id=? and site_id=?";
		Object[] values = new Object[] { book.getId(), site.getId() };
		int types[] = new int[] { Types.VARCHAR, Types.VARCHAR };
		try {
			return (BookSite) jt.queryForObject(FindBookInSiteSql, values, types, new BookSiteRowMapper(book, site));
		} catch (EmptyResultDataAccessException e) {
		}
		return null;
	}

	public BookSite addBookSite(BookSite bookSite) {
		final String AddBookToSiteSql = "insert into booksite(booksite_id, book_id, site_id, coverurl, allchapterurl, updatetime) values(?,?,?,?,?,?)";
		bookSite.setId(Utils.nextBookSiteId());
		Object[] values = new Object[] { bookSite.getId(), bookSite.getBook().getId(), bookSite.getSite().getId(), bookSite.getCoverUrl(), bookSite.getAllChapterUrl(), bookSite.getUpdateTime() };
		int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.DATE };
		jt.update(AddBookToSiteSql, values, types);
		return bookSite;
	}

	public int getSiteCount() {
		final String GetSiteCountSql = "select count(0) from site";
		return jt.queryForInt(GetSiteCountSql);
	}

	public int deleteAllSites() {
		final String deleteAllSites = "delete from site";
		return jt.update(deleteAllSites);
	}

	public int deleteAllChapterSite() {
		final String DeleteAllChapterSiteSql = "delete from chaptersite";
		return jt.update(DeleteAllChapterSiteSql);
	}

	public int deleteAllBookSite() {
		final String DeleteAllBookSiteSql = "delete from booksite";
		return jt.update(DeleteAllBookSiteSql);
	}

	private final class BookSiteRowMapper implements RowMapper {
		private Book book;
		private Site site;

		public BookSiteRowMapper() {
		}

		public BookSiteRowMapper(Book book, Site site) {
			this.book = book;
			this.site = site;
		}

		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			BookSite r = new BookSite();
			r.setId(rs.getString("booksite_id"));
			Book book;
			if (this.book == null) {
				book = new Book();
				book.setId(rs.getString("book_id"));
				book = bookDao.loadBookById(book);
			} else {
				book = this.book;
			}
			r.setBook(book);
			Site site;
			if (this.site == null) {
				site = new Site();
				site.setId(rs.getString("site_id"));
				site = loadSiteById(site);
			} else {
				site = this.site;
			}
			r.setSite(site);
			r.setCoverUrl(rs.getString("coverurl"));
			r.setAllChapterUrl(rs.getString("allchapterurl"));
			r.setUpdateTime(rs.getDate("updatetime"));
			return r;
		}
	}

	private final class BookSiteResultSetExtractor implements ResultSetExtractor {
		private Book book;
		private Site site;

		public BookSiteResultSetExtractor() {
		}

		public BookSiteResultSetExtractor(Book book, Site site) {
			this.book = book;
			this.site = site;
		}

		public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
			BookSiteRowMapper mapper = new BookSiteRowMapper(book, site);
			List<BookSite> r = new ArrayList<BookSite>();
			int counter = 0;
			while (rs.next()) {
				r.add((BookSite) mapper.mapRow(rs, counter++));
			}
			return r;
		}

	}

	private final class ChapterSiteRowMapper implements RowMapper {
		private Chapter chapter;
		private Site site;

		public ChapterSiteRowMapper() {
		}

		public ChapterSiteRowMapper(Chapter chapter, Site site) {
			this.chapter = chapter;
			this.site = site;
		}

		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			ChapterSite r = new ChapterSite();
			r.setId(rs.getString("chaptersite_id"));
			r.setUpdateTime(rs.getDate("updateTime"));
			r.setUrl(rs.getString("url"));
			Chapter c;
			if (this.chapter == null) {
				c = new Chapter();
				c.setId(rs.getString("chapter_id"));
				c = bookDao.loadChapterById(c);
			} else {
				c = this.chapter;
			}
			r.setChapter(c);
			Site s;
			if (this.site == null) {
				s = new Site();
				s.setId(rs.getString("site_id"));
				loadSiteById(s);
			} else {
				s = this.site;
			}
			r.setSite(s);
			return r;
		}
	}

	private final class ChapterSiteResultsetExtractor implements ResultSetExtractor {
		private Chapter chapter;
		private Site site;

		public ChapterSiteResultsetExtractor() {
		}

		public ChapterSiteResultsetExtractor(Chapter chapter, Site site) {
			this.chapter = chapter;
			this.site = site;
		}

		public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
			ChapterSiteRowMapper mapper = new ChapterSiteRowMapper(this.chapter, this.site);
			List<ChapterSite> r = new ArrayList<ChapterSite>();
			int counter = 0;
			while (rs.next()) {
				r.add((ChapterSite) mapper.mapRow(rs, counter++));
			}
			return r;
		}
	}

	private final class SiteResultSetExtractor implements ResultSetExtractor {

		public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
			SiteRowMapper mapper = new SiteRowMapper();
			List<Site> r = new ArrayList<Site>();
			int counter = 0;
			while (rs.next()) {
				Site s = (Site) mapper.mapRow(rs, counter++);
				r.add(s);
			}
			return r;
		}
	}

	private final class SiteRowMapper implements RowMapper {
		private Site site;

		public SiteRowMapper() {
		}

		public SiteRowMapper(Site s) {
			this.site = s;
		}

		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			Site r;
			if (this.site != null) {
				r = this.site;
			} else {
				r = new Site();
			}
			r.setId(rs.getString("site_id"));
			r.setDomainName(rs.getString("domainName"));
			r.setName(rs.getString("name"));
			r.setUrl(rs.getString("url"));
			return r;
		}

	}
}

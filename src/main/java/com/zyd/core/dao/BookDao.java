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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.zyd.core.Utils;
import com.zyd.core.dom.Book;
import com.zyd.core.dom.BookFilter;
import com.zyd.core.dom.Chapter;

public class BookDao {
	private JdbcTemplate jt = null;

	public void setDataSource(DataSource ds) {
		jt = new JdbcTemplate(ds);
	}

	/**
	 * returns the new added book with an id if book is added successfully.
	 * 
	 * @param book
	 * @return
	 */
	public Book addBook(Book book) {
		book.setId(Utils.nextBookId());
		final String AddBookSql = "insert into book(book_id, name, author, description, totalChar, hit, finished, updateTime) values(?,?,?,?,?,?,?,?)";
		Object[] values = new Object[] { book.getId(), book.getName(),
				book.getAuthor(), book.getDescription(), book.getTotalChar(),
				book.getHit(), book.isFinished(), book.getUpdateTime() };
		int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.BOOLEAN,
				Types.DATE };
		jt.update(AddBookSql, values, types);
		return book;
	}

	public Book findBookById(Book book) {
		if (book == null)
			return null;
		return findBookById(book.getId());
	}

	public Book findBookById(String bookId) {
		if (StringUtils.isBlank(bookId))
			return null;
		final String FindBookById = "select * from book where book_id=?";
		Object[] values = new Object[] { bookId };
		int[] types = new int[] { Types.VARCHAR };
		Book b = (Book) jt.queryForObject(FindBookById, values, types,
				new BookRowMapper());
		return b;
	}

	@SuppressWarnings("unchecked")
	public List<Book> findBookByNameAuthor(Book book) {
		if (book == null)
			return (List<Book>) Collections.EMPTY_LIST;
		return findBooksByNameAuthor(book.getName(), book.getAuthor());
	}

	public List<Book> findBooksByNameAuthor(String name, String author) {
		if (StringUtils.isBlank(name) || StringUtils.isBlank(author))
			return null;
		final String FindBookByNameAuthor = "select * from book where name=? and author=?";
		Object[] values = new Object[] { name, author };
		int[] types = new int[] { Types.VARCHAR, Types.VARCHAR };
		@SuppressWarnings("unchecked")
		List<Book> r = (List<Book>) jt.query(FindBookByNameAuthor, values,
				types, new BookListExtractor());
		return r;
	}

	public int deleteAllBooks() {
		final String DeleteAllBooks = "delete from book";
		int r = jt.update(DeleteAllBooks);
		return r;
	}

	public int deleteAllChapters() {
		final String DeleteAllChapters = "delete from chapter";
		int r = jt.update(DeleteAllChapters);
		return r;
	}

	public List<Book> listBook(BookFilter filter) {
		int start = filter.getStart(), count = filter.getCount();

		final String ListBookSql = "select * from book limit ?, ?";
		Object[] values = new Object[] { start, count };
		int[] types = new int[] { Types.INTEGER, Types.INTEGER };
		@SuppressWarnings("unchecked")
		List<Book> r = (List<Book>) jt.query(ListBookSql, values, types,
				new BookListExtractor());
		return r;
	}

	/**
	 * book has to have an id, chapter need to have a name.
	 * 
	 * @param book
	 * @param chapter
	 * @param whether
	 *            name is exact name
	 * @return
	 */
	public List<Chapter> findChapterInBookByName(Book book, Chapter chapter,
			boolean useLike) {
		if (book == null || chapter == null)
			return Collections.EMPTY_LIST;
		return findChapterInBookByName(book.getId(), chapter.getName(), useLike);
	}

	public List<Chapter> findChapterInBookByName(String bookId,
			String chapterName, boolean useLike) {
		if (StringUtils.isBlank(bookId) || StringUtils.isBlank(chapterName))
			return Collections.EMPTY_LIST;
		final String FindChapterInBookByNameSql = "select * from chapter where book_id=? and name ";
		String sql;
		if (useLike == true) {
			sql = FindChapterInBookByNameSql + " like ?";
			chapterName = "%" + chapterName + "%";
		} else {
			sql = FindChapterInBookByNameSql + " = ?";
		}
		Object[] values = new Object[] { bookId, chapterName };
		int[] types = new int[] { Types.VARCHAR, Types.VARCHAR };
		@SuppressWarnings("unchecked")
		List<Chapter> r = (List<Chapter>) jt.query(sql, values, types,
				new ChapterlistExtractor());
		return r;
	}

	/**
	 * book has to have an id, chapter need to have an name at least.
	 * 
	 * @param book
	 * @param chapter
	 * @return
	 */
	public Chapter addChapterToBook(Book book, Chapter chapter) {
		if (book == null || chapter == null)
			return null;
		return addChapterToBook(book.getId(), chapter);
	}

	public Chapter addChapterToBook(String bookId, Chapter chapter) {
		if (StringUtils.isBlank(bookId) || chapter == null)
			return null;
		final String AddChapterToBookSql = "insert into chapter(chapter_id, name, description, totalChar, hit, updateTime, isPicture, hasContent, book_id, sequence) values(?,?,?,?,?,?,?,?,?,?)";
		chapter.setId(Utils.nextChapterId());
		final Object[] values = new Object[] { chapter.getId(),
				chapter.getName(), chapter.getDescription(),
				chapter.getTotalChar(), chapter.getHit(),
				chapter.getUpdateTime(), chapter.getPicture(),
				chapter.isHasContent(), bookId, chapter.getSequence() };
		final int[] types = new int[] { Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.DATE,
				Types.BOOLEAN, Types.BOOLEAN, Types.VARCHAR, Types.INTEGER };
		jt.update(AddChapterToBookSql, values, types);
		return chapter;
	}

	public int getBookCount() {
		String GetBookCountSql = "select count(0) from book";
		return jt.queryForInt(GetBookCountSql);
	}

	private static final class BookRowMapper implements RowMapper {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			Book b = new Book();
			b.setId(rs.getString("book_id"));
			b.setName(rs.getString("name"));
			b.setAuthor(rs.getString("author"));
			b.setDescription(rs.getString("description"));
			b.setTotalChar(rs.getInt("totalChar"));
			b.setHit(rs.getInt("hit"));
			b.setFinished(rs.getBoolean("finished"));
			b.setUpdateTime(rs.getDate("updateTime"));
			return b;
		}
	}

	private static final class BookListExtractor implements ResultSetExtractor {
		public Object extractData(ResultSet rs) throws SQLException,
				DataAccessException {
			List<Book> r = new ArrayList<Book>();
			BookRowMapper mapper = new BookRowMapper();
			int counter = 0;
			while (rs.next()) {
				r.add((Book) mapper.mapRow(rs, counter++));
			}
			return r;
		}
	}

	private static final class ChapterRowMapper implements RowMapper {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			Chapter c = new Chapter();
			c.setId(rs.getString("chapter_id"));
			c.setName(rs.getString("name"));
			c.setDescription(rs.getString("description"));
			c.setTotalChar(rs.getInt("totalChar"));
			c.setHit(rs.getInt("hit"));
			c.setUpdateTime(rs.getDate("updateTime"));
			c.setPicture(rs.getBoolean("isPicture"));
			c.setHasContent(rs.getBoolean("hasContent"));
			return c;
		}
	}

	private static final class ChapterlistExtractor implements
			ResultSetExtractor {
		public Object extractData(ResultSet rs) throws SQLException,
				DataAccessException {
			List<Chapter> r = new ArrayList<Chapter>();
			ChapterRowMapper mapper = new ChapterRowMapper();
			int count = 0;
			while (rs.next()) {
				r.add((Chapter) mapper.mapRow(rs, count++));
			}
			return r;
		}
	}
}

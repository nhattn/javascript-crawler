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
import com.zyd.core.dom.Chapter;
import com.zyd.core.dom.ChapterSite;
import com.zyd.core.dom.Site;

public class SiteDao {
	private JdbcTemplate jt = null;

	public void setDataSource(DataSource ds) {
		jt = new JdbcTemplate(ds);
	}

	public Site addSite(Site site) {
		final String AddSiteSql = "insert into site(site_id, name, domainName, url) values(?,?,?,?)";
		site.setId(Utils.nextSiteId());
		Object[] values = new Object[] { site.getId(), site.getName(),
				site.getDomainName(), site.getUrl() };
		int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR };
		jt.update(AddSiteSql, values, types);
		return site;
	}

	public Site findSiteByDomainName(String domainName) {
		if (StringUtils.isBlank(domainName))
			return null;
		final String FindSiteByDomainNameSql = "select * from site where domainName=?";
		Object[] values = new Object[] { domainName };
		int[] types = new int[] { Types.VARCHAR };
		Site r = (Site) jt.queryForObject(FindSiteByDomainNameSql, values,
				types, new SiteRowMapper());
		return r;
	}

	/**
	 * fill the passed site object with data from db.
	 * 
	 * @param site
	 *            must have an id
	 */
	public void loadSiteById(Site site) {
		if (StringUtils.isBlank(site.getId()))
			return;
		final String FindSiteByDomainNameSql = "select * from site where domainName=?";
		Object[] values = new Object[] { site.getId() };
		int[] types = new int[] { Types.VARCHAR };
		jt.queryForObject(FindSiteByDomainNameSql, values, types,
				new SiteRowMapper(site));
	}

	/**
	 * the list of ChapterSite will be returned, but Site/Chapter won't contain
	 * anything, just an id.
	 * 
	 * @param chapterId
	 * @return
	 */
	public List<ChapterSite> findSiteForChapter(String chapterId) {
		if (StringUtils.isBlank(chapterId))
			return Collections.EMPTY_LIST;
		final String FindSiteForChapterSql = "select * from chaptersite where chapter_id=?";
		Object[] values = new Object[] { chapterId };
		int[] types = new int[] { Types.VARCHAR };
		@SuppressWarnings("unchecked")
		List<ChapterSite> r = (List<ChapterSite>) jt.query(
				FindSiteForChapterSql, values, types,
				new SiteChapterResultsetExtractor());
		return r;
	}

	public ChapterSite findChapterInSite(String chapterId, String siteId) {
		if (StringUtils.isBlank(chapterId) || StringUtils.isBlank(siteId))
			return null;
		final String FindChapterInSiteSql = "select * from chaptersite where chapter_id=? and site_id=?";
		Object[] values = new Object[] { chapterId, siteId };
		int[] types = new int[] { Types.VARCHAR, Types.VARCHAR };
		@SuppressWarnings("unchecked")
		ChapterSite r = (ChapterSite) jt.query(FindChapterInSiteSql, values,
				types, new SiteChapterRowMapper());
		return r;
	}

	public ChapterSite addChapterToSite(Chapter chapter, String siteId) {
		return null;
	}

	private static final class SiteChapterRowMapper implements RowMapper {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			ChapterSite r = new ChapterSite();
			r.setId(rs.getString("chaptersite_id"));
			r.setUpdateTime(rs.getDate("updateTiime"));
			r.setUrl(rs.getString("url"));
			Chapter c = new Chapter();
			c.setId(rs.getString("chapter_id"));
			r.setChapter(c);
			Site s = new Site();
			s.setId(rs.getString("site_id"));
			r.setSite(s);
			return r;
		}
	}

	private static final class SiteChapterResultsetExtractor implements
			ResultSetExtractor {
		public Object extractData(ResultSet rs) throws SQLException,
				DataAccessException {
			SiteChapterRowMapper mapper = new SiteChapterRowMapper();
			List<ChapterSite> r = new ArrayList<ChapterSite>();
			int counter = 0;
			while (rs.next()) {
				r.add((ChapterSite) mapper.mapRow(rs, counter++));
			}
			return r;
		}
	}

	private static final class SiteRowMapper implements RowMapper {
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

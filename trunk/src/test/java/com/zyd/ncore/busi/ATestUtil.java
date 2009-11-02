package com.zyd.ncore.busi;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zyd.Config;
import com.zyd.core.Utils;
import com.zyd.core.busi.BookManager;
import com.zyd.core.busi.SiteManager;
import com.zyd.core.dom.Book;
import com.zyd.core.dom.Chapter;
import com.zyd.core.dom.Site;
import com.zyd.core.util.SpringContext;

public class ATestUtil {
	static Random rand = new Random();
	static HashSet<String> usedString = new HashSet<String>();
	static String[] domains = new String[] { "http://www.qidian.com", "http://17k.com", "http://aa.kanshu.com" };

	public static String BookUrl = Config.ServerUrl + "/service/book";
	public static String BookListUrl = Config.ServerUrl + "/service/booklist";
	public static String ControllerUrl = Config.ServerUrl + "/service/controller";
	public static String ChapterUrl = Config.ServerUrl + "/service/chapter";

	public static String getNoRepeatString() {
		StringBuffer buf = new StringBuffer();
		Date d = new Date();
		buf.append(Long.toString(d.getTime() + rand.nextInt(100000)));
		String s = StringUtils.rightPad(buf.toString(), 15);
		if (usedString.contains(s)) {
			return getNoRepeatString();
		} else {
			usedString.add(s);
			return s;
		}
	}

	public static List<Book> getBookList(int count, boolean shoudPersistent) {
		BookManager bm = (BookManager) SpringContext.getContext().getBean("bookManager");
		List<Book> books = new ArrayList<Book>();
		for (int i = 0; i < count; i++) {
			Book b = new Book();
			b.setName("小说书名" + i);
			b.setAuthor("作者" + i);
			b.setAllChapterUrl(domains[i % 3] + "/all_chapter_" + i);
			b.setCoverUrl(domains[i % 3] + "/cover_" + i);
			if (shoudPersistent) {
				b = bm.addBook(b);
			} else {
				b.setId(Utils.nextBookId());
			}
			books.add(b);
		}
		return books;
	}

	public static Chapter getChapter() {
		// BookManager bm =
		// (BookManager)SpringContext.getContext().getBean("bookManager");
		Chapter c = new Chapter();
		String s = getNoRepeatString();
		c.setId(s);
		c.setName("章节名称" + s);
		c.setContent("内容" + s);
		c.setDescription("章节简介" + s);
		// if(shouldPersistent){
		// bm.addChapterToBook(book, chapter)
		// }
		return c;
	}

	public static List<Site> getSiteList(boolean persistent) {
		if (persistent == false) {
			throw new UnsupportedOperationException();
		}
		List<Site> r = new ArrayList<Site>();
		SiteManager sm = (SiteManager) SpringContext.getContext().getBean("siteManager");
		for (String s : domains) {
			r.add(sm.addSite(s));
		}
		return r;
	}

	public static void buildModel(int bookCount, int chapterPerBook) {
		List<Book> books = getBookList(bookCount, true);
		BookManager bm = (BookManager) SpringContext.getContext().getBean("bookManager");
		for (Book book : books) {
			bm.addBook(book);
			for (int i = 0; i < chapterPerBook; i++) {
				Chapter c = getChapter();
				c.setName(book.getName() + ":+章节" + i);
				c.setChapterUrl(book.getAllChapterUrl() + "/chapter_" + i);
				bm.addChapterToBook(book, c);
			}
		}
	}

	public static <T> int getUniqueObjectCount(Collection<T> cols) {
		return (new HashSet<T>(cols)).size();
	}

	public static String postAndGetString(String url, Map<String, String> params) {
		String r = null;
		try {
			HttpClient client = new HttpClient();

			PostMethod method = new PostMethod(url);
			// method.getParams().setContentCharset("UTF-8");
			method.addRequestHeader("Referer", "http://www.test.com");
			method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			if (params != null) {
				for (String k : params.keySet()) {
					method.setParameter(k, params.get(k));
				}
			}
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			InputStream ins = method.getResponseBodyAsStream();
			r = IOUtils.toString(ins);
			ins.close();
			method.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}

	public static String getAndGetString(String url, Map<String, String> params) {
		String r = null;
		try {
			HttpClient client = new HttpClient();

			if (params != null) {
				StringBuffer buf = new StringBuffer();
				for (String k : params.keySet()) {
					buf.append(k);
					buf.append('=');
					buf.append(URLEncoder.encode(params.get(k)));
					buf.append("&");
				}
				if (buf.length() != 0) {
					buf.deleteCharAt(buf.length() - 1);
				}
				if (url.indexOf('?') > 0) {
					url = url + "&" + buf.toString();
				} else {
					url = url + "?" + buf.toString();
				}
			}

			GetMethod method = new GetMethod(url);
			method.addRequestHeader("Referer", "http://www.test.com");
			method.addRequestHeader("Content-Type", "text/plain; charset=UTF-8");

			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			InputStream ins = method.getResponseBodyAsStream();
			r = IOUtils.toString(ins);
			ins.close();
			method.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}

	public static boolean clearServerData() throws Exception {
		String s = getAndGetString(Config.ServerUrl + "/service/controller?action=ClearAllData", null);
		JSONObject o = new JSONObject(s);
		return o.getBoolean("result");
	}

	public static void tryEncoding(String s) {
		String encoding[] = new String[] { "GBK", "UTF-8", "ISO-8859-1" };
		for (String a : encoding) {
			for (String b : encoding) {
				try {
					String sss = a + ":" + b + ":" + new String(s.getBytes(a), b);
					if (sss.indexOf('?') >= 0) {
						sss = "***" + sss;
					}
					System.out.println(sss);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static ApplicationContext setUpSpring() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:**/ContextConfig.xml");
		return ctx;
	}

	public static void clearData() {
		BookManager bm = (BookManager) SpringContext.getContext().getBean("bookManager");
		bm.deleteAllBook();

		SiteManager sm = (SiteManager) SpringContext.getContext().getBean("siteManager");
		sm.deleteAllSites();
	}
}

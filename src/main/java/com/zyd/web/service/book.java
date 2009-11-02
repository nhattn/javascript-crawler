package com.zyd.web.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.zyd.Config;
import com.zyd.core.Utils;
import com.zyd.core.busi.BookManager;
import com.zyd.core.busi.CrawlerManager;
import com.zyd.core.dom.Book;
import com.zyd.core.util.SpringContext;
import com.zyd.web.ServiceBase;

public class book extends ServiceBase {
	private CrawlerManager cm;
	private BookManager bm;

	public book() {
		cm = (CrawlerManager) SpringContext.getContext().getBean("crawlerManager");
		bm = (BookManager) SpringContext.getContext().getBean("bookManager");
	}

	/**
	 * method: post description: add a new book parameters: data, a json string
	 * cotaining a single book response: a json string "{'result':'true'}" or
	 * "{'result':'false'}", indicating wheather or not anything is changed
	 * because of this call. like book doesn't exist before, or chapters are
	 * added etc.
	 */
	@Override
	public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		setResponseType("js", resp);
		String data = param(req, "data"), fromUrl = req.getHeader("referer");
		boolean changed = false;
		if (StringUtils.isNotBlank(data)) {
			changed = cm.processBook(data, fromUrl);
		}
		String s = Utils.stringArrayToJsonString(new String[] { "result", Boolean.toString(changed) });
		output(s, resp);
	}

	/**
	 * method: get description: get a book information parameters: name, the
	 * name of the book; author. these two is necessary if no id is there. or
	 * id, the id of the book.
	 * 
	 * withChapter, whether or not also return the chapter list with this
	 * request, 'true' or 'false', default to "false" TODO: format, "json" or
	 * "xml", default to "xml" response: a json/xml string describing the book.
	 * fields has the same name-value as {@link Book}
	 */
	@Override
	public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = param(req, "name"), author = param(req, "author"), id = req.getParameter("id"), format = req.getParameter("format");
		if (name != null)
			name = new String(name.getBytes("iso-8859-1"));
		if (author != null)
			author = new String(author.getBytes("iso-8859-1"));

		Book book = null;

		if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(author)) {
			book = new Book();
			book.setName(name);
			book.setAuthor(author);
		} else if (StringUtils.isNotBlank(id)) {
			book = new Book();
			book.setId(id);
		}
		if (book == null) {
			throw new ServletException("Invalid request: not enough parameter");
		}
		book = bm.findBook(book);
		String content = "no book found";
		boolean withChapter = false;
		if (book != null) {
			String s = req.getParameter("withChapter");
			if (StringUtils.isNotBlank(s)) {
				if ("true".equals(s)) {
					withChapter = true;
					bm.loadBookChapter(book);
				} else if ("false".equals(s)) {
					withChapter = false;
				} else {
					throw new ServletException("Invalid request parameter value for withChapter:" + s);
				}
			}
			if ("xml".equals(format)) {
				setResponseType("xml", resp);
				content = book.toXMLString(withChapter, true, Config.Encoding);
			} else if (format == null || "json".equals(format)) {
				setResponseType("js", resp);
				content = book.toJsonString(withChapter);
			} else {
				throw new ServletException("Invalid request parameter value for format:" + format);
			}
		}
		output(content, resp);
	}
}

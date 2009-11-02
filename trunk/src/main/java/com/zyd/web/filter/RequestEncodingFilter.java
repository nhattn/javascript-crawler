package com.zyd.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class RequestEncodingFilter implements Filter {

	private String encoding = null;

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		/*
		 * String pageEncoding = req.getParameter("encoding"); if (pageEncoding
		 * != null) { } else { if (req.getCharacterEncoding() == null)
		 * req.setCharacterEncoding(this.encoding); }
		req.setCharacterEncoding("GBK");
		 */
		chain.doFilter(req, res);
	}

	public void init(FilterConfig config) throws ServletException {
		this.encoding = config.getInitParameter("encoding");
	}

	public void destroy() {
	}
}

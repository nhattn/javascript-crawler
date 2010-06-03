package com.zyd.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.zyd.Constants;

public class ServiceEncodingFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        res.setCharacterEncoding(Constants.ENCODING_OUT_GOING_CONTENT);
        if (req.getCharacterEncoding() == null) {
            req.setCharacterEncoding(Constants.ENCODING_INCOMING_CONTENT);
        }
        chain.doFilter(req, res);
    }

    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

}

package com.zyd.web.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.core.Utils;
import com.zyd.core.busi.CrawlerManager;
import com.zyd.core.util.SpringContext;
import com.zyd.web.ServiceBase;

public class controller extends ServiceBase {
	private CrawlerManager cm;

	public controller() {
		cm = (CrawlerManager) SpringContext.getContext().getBean("crawlerManager");
	}

	/**
	 * method: get description: perform various control functions parameters:
	 * action, 'ClearAllData' will clear all data from the system, only used for
	 * test.
	 */
	@Override
	public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		if ("ClearAllData".equals(action)) {
			setResponseType("js", resp);
			cm.clearAll();
			output(Utils.stringArrayToJsonString(new String[] { "result", "true" }), resp);
		} else {
			setResponseType("text", resp);
			output("Invalid request:" + req.getRequestURI(), resp);
		}
	}

	/**
	 * method: post *
	 */
	@Override
	public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getParameter("");
	}
}

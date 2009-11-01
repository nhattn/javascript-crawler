package com.zyd.core.dom;

import java.util.Date;

public class ChapterSite {
	public String id;
	public Chapter chapter;
	public Site site;
	public Date updateTime;
	public String url;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Chapter getChapter() {
		return chapter;
	}

	public void setChapter(Chapter chapter) {
		this.chapter = chapter;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}

package com.zyd.core.dom;

import java.util.Date;

public class BookSite {
    public String id;
    public Book book;
    public Site site;
    public String coverUrl;
    public String allChapterUrl;
    public Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getAllChapterUrl() {
        return allChapterUrl;
    }

    public void setAllChapterUrl(String allChapterUrl) {
        this.allChapterUrl = allChapterUrl;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}

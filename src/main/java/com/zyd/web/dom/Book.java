package com.zyd.web.dom;

import java.util.ArrayList;
import java.util.List;

public class Book {
    public String id = null;
    public String name = null;
    public String author = null;
    public String description = null;
    public String cat1 = null;
    public String cat2 = null;
    public String allChapterLink = null;
    public String updateTime = null;
    public String hit = null;
    public String totalChar = null;

    public WebSite site = null;
    public List<Chapter> chapters = null;

    public String tempLink = null;

    public String linkWithChapterUrl = null;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCat1() {
        return cat1;
    }

    public void setCat1(String cat1) {
        this.cat1 = cat1;
    }

    public String getCat2() {
        return cat2;
    }

    public void setCat2(String cat2) {
        this.cat2 = cat2;
    }

    public String getAllChapterLink() {
        return allChapterLink;
    }

    public void setAllChapterLink(String allChapterLink) {
        this.allChapterLink = allChapterLink;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getLinkWithChapterUrl() {
        return linkWithChapterUrl;
    }

    public void setLinkWithChapterUrl(String linkWithChapterUrl) {
        this.linkWithChapterUrl = linkWithChapterUrl;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getHit() {
        return hit;
    }

    public void setHit(String hit) {
        this.hit = hit;
    }

    public String getTotalChar() {
        return totalChar;
    }

    public void setTotalChar(String totalChar) {
        this.totalChar = totalChar;
    }

    public WebSite getSite() {
        return site;
    }

    public void setSite(WebSite site) {
        this.site = site;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public String getTempLink() {
        return tempLink;
    }

    public void setTempLink(String tempLink) {
        this.tempLink = tempLink;
    }

    public boolean addChapter(Chapter chapter) {
        if (this.chapters == null) {
            this.chapters = new ArrayList<Chapter>();
        }
        for (Chapter c : this.chapters) {
            if (c.name == chapter.name) {
                return false;
            }
        }
        this.chapters.add(chapter);
        chapter.book = this;
        return true;
    }
}

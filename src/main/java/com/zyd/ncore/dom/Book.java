package com.zyd.ncore.dom;

import java.util.Date;
import java.util.List;

public class Book {
    public String id;
    public String name;
    public String author;
    public String description;
    public String category;
    public int totalChar;
    public int hit;
    public boolean finished;
    public Date updateTime;
    public List<Chapter> chapters;

    // Site related info
    public String allChapterUrl;
    public String urlToGrawl;
    public String coverUrl;

    public String getUrlToGrawl() {
        return urlToGrawl;
    }

    public void setUrlToGrawl(String urlToGrawl) {
        this.urlToGrawl = urlToGrawl;
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

    public String getUrlToCrawl() {
        return urlToGrawl;
    }

    public void seUrlToCrawl(String tempUrl) {
        this.urlToGrawl = tempUrl;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getTotalChar() {
        return totalChar;
    }

    public void setTotalChar(int totalChar) {
        this.totalChar = totalChar;
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

}

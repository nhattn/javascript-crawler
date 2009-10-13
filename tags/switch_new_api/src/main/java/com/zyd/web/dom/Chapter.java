package com.zyd.web.dom;

public class Chapter {
    public String id;
    public String name;
    public String updateTime;
    public String link;
    public Book book;
    public String hit;
    public String totalChar;
    public String volume;
    public String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLinkBookByUrl() {
        return linkBookByUrl;
    }

    public void setLinkBookByUrl(String linkBookByUrl) {
        this.linkBookByUrl = linkBookByUrl;
    }

    public String linkBookByUrl;

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

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
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

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}

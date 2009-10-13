package com.zyd.ncore.dom;

import java.util.Date;

public class Chapter {
    public String id;
    public String name;
    public String description;
    public String content;
    public String volume;

    // site related info
    public Date updateTime;
    public String chapterUrl;

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    /**
     * the sequence of this chapter in the book.
     * sometimes chapters have the same name.
     */
    public int sequence;
    public int hit;
    public int totalChar;
    public boolean hasContent;
    public boolean isPicture;
    public Book book;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public int getTotalChar() {
        return totalChar;
    }

    public void setTotalChar(int totalChar) {
        this.totalChar = totalChar;
    }

    public boolean isHasContent() {
        return hasContent;
    }

    public void setHasContent(boolean hasContent) {
        this.hasContent = hasContent;
    }

    public boolean isPicture() {
        return isPicture;
    }

    public void setPicture(boolean isPicture) {
        this.isPicture = isPicture;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}

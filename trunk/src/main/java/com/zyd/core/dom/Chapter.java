package com.zyd.core.dom;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.zyd.core.Utils;

public class Chapter {
    public String id;
    public String name;
    public String description;
    public String content;
    public String volume;
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

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return "[chapter:" + this.name + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj instanceof Chapter == false)
            return false;
        return Utils.strictEqual(this.name, ((Chapter) obj).getName());
    }

    /**
     * 
     * @param withHeader  weather or not add a "<?xml..." header
     * @param encoding what encoding to add in the header
     * @return
     */
    public String toXMLString(boolean withHeader, String encoding) {
        StringBuffer buf = new StringBuffer();
        if (withHeader) {
            buf.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
        }
        buf.append("<chapter>");

        buf.append("<id>");
        buf.append(this.getId());
        buf.append("</id>");

        buf.append("<name>");
        buf.append(this.getName());
        buf.append("</name>");

        buf.append("<description>");
        buf.append(this.getDescription());
        buf.append("</description>");

        buf.append("<totalChar>");
        buf.append(this.getTotalChar());
        buf.append("</totalChar>");

        buf.append("<hit>");
        buf.append(this.getHit());
        buf.append("</hit>");

        buf.append("<updateTime>");
        buf.append(this.getUpdateTime());
        buf.append("</updateTime>");

        buf.append("<isPicture>");
        buf.append(this.isPicture());
        buf.append("</isPicture>");

        buf.append("<content>");
        buf.append(this.getContent());
        buf.append("</content>");

        buf.append("<volume>");
        buf.append(this.getVolume());
        buf.append("</volume>");

        buf.append("<sequence>");
        buf.append(this.getSequence());
        buf.append("</sequence>");

        Book book = this.getBook();
        if (book != null && book.getId() != null) {
            buf.append("<parent>");
            buf.append(book.getId());
            buf.append("</parent>");
        }

        buf.append("</chapter>");
        return buf.toString();
    }

    public String toXMLString() {
        return toXMLString(false, null);
    }

    public JSONObject toJsonObject() {
        JSONObject js = new JSONObject();
        try {
            js.put("id", this.getId());
            js.put("name", this.getName());
            js.put("description", this.getDescription());
            js.put("totalChar", this.getTotalChar());
            js.put("hit", this.getHit());
            js.put("updateTime", this.getUpdateTime());
            js.put("isPicture", this.isPicture);
            js.put("content", this.getContent());
            js.put("volume", this.getVolume());
            js.put("sequence", this.getSequence());
            Book book = this.getBook();
            if (book != null && book.getId() != null) {
                js.put("parent", this.getBook().getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return js;
    }

    public String toJsonString() {
        return toJsonObject().toString();
    }
}

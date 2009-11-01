package com.zyd.core.dom;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.zyd.core.Utils;

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
    public List<Chapter> chapters = new ArrayList<Chapter>();

    // Site related info
    public String allChapterUrl;
    public String urlToCrawl;
    public String coverUrl;

    private Set<Chapter> chapterset = new HashSet<Chapter>();

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
        return urlToCrawl;
    }

    public Set<Chapter> getChapterset() {
        return chapterset;
    }

    public void setChapterset(Set<Chapter> chapterset) {
        this.chapterset = chapterset;
    }

    public void setUrlToCrawl(String urlToCrawl) {
        this.urlToCrawl = urlToCrawl;
    }

    public void seUrlToCrawl(String tempUrl) {
        this.urlToCrawl = tempUrl;
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

    public void addChapter(Chapter c) {
        chapters.add(c);
        chapterset.add(c);
        c.setBook(this);
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    @Override
    public int hashCode() {
        StringBuffer buf = new StringBuffer();
        buf.append(name);
        buf.append('$');
        buf.append(author);
        buf.append('$');
        return buf.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj instanceof Book == false) {
            return false;
        }
        Book ob = (Book) obj;
        if (Utils.strictEqual(name, ob.getName()) == false)
            return false;
        if (Utils.strictEqual(author, ob.getAuthor()) == false)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append('[');
        buf.append(name);
        buf.append(',');
        buf.append(author);
        buf.append(']');
        return buf.toString();

    }

    public String toXMLString(String encoding) {
        return toXMLString(false, false, encoding);
    }

    public String toXMLString(boolean withChapter, boolean withHeader, String encoding) {
        StringBuffer buf = new StringBuffer();
        if (withHeader) {
            buf.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
        }
        buf.append("<book>");

        buf.append("<id>");
        buf.append(this.getId());
        buf.append("</id>");

        buf.append("<name>");
        buf.append(this.getName());
        buf.append("</name>");

        buf.append("<author>");
        buf.append(this.getAuthor());
        buf.append("</author>");

        buf.append("<description>");
        buf.append(StringEscapeUtils.escapeXml(this.getDescription()));
        buf.append("</description>");

        buf.append("<category>");
        buf.append(this.getCategory());
        buf.append("</category>");

        buf.append("<totalChar>");
        buf.append(this.getTotalChar());
        buf.append("</totalChar>");

        buf.append("<hit>");
        buf.append(this.getHit());
        buf.append("</hit>");

        buf.append("<finished>");
        buf.append(this.isFinished());
        buf.append("</finished>");

        buf.append("<updateTime>");
        buf.append(this.getUpdateTime());
        buf.append("</updateTime>");
        if (withChapter == true) {
            List<Chapter> chapters = this.getChapters();
            if (chapters != null && chapters.size() != 0) {
                buf.append("<chapters>");
                for (Chapter c : chapters) {
                    buf.append(c.toXMLString());
                }
                buf.append("</chapters>");
            }
        }
        buf.append("</book>");
        return buf.toString();
    }

    public JSONObject toJsonObject(boolean withChapter) {
        JSONObject js = new JSONObject();
        try {
            js.put("id", this.getId());
            js.put("name", this.getName());
            js.put("author", this.getAuthor());
            js.put("description", StringEscapeUtils.escapeXml(this.getDescription()));
            js.put("category", this.getCategory());
            js.put("totalChar", this.getTotalChar());
            js.put("finished", this.isFinished());
            js.put("updateTime", this.getUpdateTime());
            if (withChapter == true) {
                List<Chapter> chapters = this.getChapters();
                if (chapters != null && chapters.size() != 0) {
                    JSONArray arr = new JSONArray();
                    for (Chapter c : chapters) {
                        arr.put(c.toJsonObject());
                    }
                    js.put("chapters", arr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return js;
    }

    public String toJsonString(boolean withChapter) {
        return toJsonObject(withChapter).toString();
    }

    public String toJsonString() {
        return toJsonString(false);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(PropertyUtils.describe(new Book()));
    }
}

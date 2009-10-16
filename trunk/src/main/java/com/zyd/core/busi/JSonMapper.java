package com.zyd.core.busi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zyd.core.Utils;
import com.zyd.core.dom.Book;
import com.zyd.core.dom.Chapter;

public class JSonMapper {
    private static JSonMapper instance = new JSonMapper();

    private JSonMapper() {
    }

    public static JSonMapper getInstance() {
        return instance;
    }

    public Book parseBook(String s) {
        Book b = null;
        try {
            JSONObject obj = new JSONObject(s);
            b = jsonObjectToBook(obj);
        } catch (Exception e) {
            System.err.println(s);
            e.printStackTrace();
            b = new Book();
            b.setName("Error_Book_Name");
            b.setAuthor("Error_Book_Author");
            b.setDescription(e.getMessage());
        }
        return b;
    }

    public List<Book> parseBookList(String s) {
        List<Book> list = new ArrayList<Book>(200);
        try {
            JSONArray arr = new JSONArray(s);
            int len = arr.length();
            for (int i = 0; i < len; i++) {
                Book b = jsonObjectToBook(arr.getJSONObject(i));
                list.add(b);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Chapter parseChapter(String s) {
        throw new UnsupportedOperationException();
    }

    private Book jsonObjectToBook(JSONObject obj) throws JSONException {
        Book b = new Book();
        if (obj.has("name"))
            b.setName(obj.getString("name"));
        if (obj.has("author"))
            b.setAuthor(obj.getString("author"));
        if (obj.has("category"))
            b.setCategory(obj.getString("category"));
        if (obj.has("totalChar")) {
            b.setTotalChar(Integer.parseInt(obj.getString("totalChar")));
        }
        if (obj.has("allChapterUrl"))
            b.setAllChapterUrl(obj.getString("allChapterUrl"));
        if (obj.has("coverUrl"))
            b.setCoverUrl(obj.getString("coverUrl"));
        if (obj.has("description"))
            b.setDescription(obj.getString("description"));
        if (obj.has("updateTime"))
            b.setUpdateTime(Utils.parseDate(obj.getString("updateTime")));
        if (obj.has("finished"))
            b.setFinished("y".equals(obj.getString("finished")) ? true : false);
        if (obj.has("hit"))
            b.setHit(Integer.parseInt(obj.getString("hit")));

        if (obj.has("chapters")) {
            b.setChapters(jsonArrayToChapterList(obj.getJSONArray("chapters"), b));
        }
        return b;
    }

    private List<Chapter> jsonArrayToChapterList(JSONArray chapters, Book book) throws JSONException {
        List<Chapter> list = new ArrayList<Chapter>();
        int len = chapters.length();
        for (int i = 0; i < len; i++) {
            JSONObject c = chapters.getJSONObject(i);
            list.add(jsonObjectToChapter(c, book));
        }
        return list;
    }

    private Chapter jsonObjectToChapter(JSONObject c, Book book) throws JSONException {
        Chapter chapter = new Chapter();
        if (c.has("volume"))
            chapter.setVolume(c.getString("volume"));
        if (c.has("updateTime"))
            chapter.setUpdateTime(Utils.parseDate(c.getString("updateTime")));
        if (c.has("totalChar"))
            chapter.setTotalChar(Integer.parseInt(c.getString("totalChar")));
        if (c.has("name"))
            chapter.setName(c.getString("name"));
        if (c.has("chapterUrl"))
            chapter.setChapterUrl(c.getString("chapterUrl"));
        if (c.has("content"))
            chapter.setContent(c.getString("content"));
        if (c.has("hit"))
            chapter.setHit(Integer.parseInt(c.getString("hit")));
        if (c.has("sequence"))
            chapter.setSequence(Integer.parseInt(c.getString("sequence")));
        chapter.setBook(book);
        return chapter;
    }
}

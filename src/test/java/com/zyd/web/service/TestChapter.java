package com.zyd.web.service;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.json.JSONObject;

import com.zyd.ncore.busi.ATestData;
import com.zyd.ncore.busi.ATestUtil;
import com.zyd.ncore.busi.JSonMapper;
import com.zyd.ncore.dom.Book;
import com.zyd.ncore.dom.Chapter;

public class TestChapter extends TestCase {
    JSonMapper jm;

    @Override
    protected void setUp() throws Exception {
        jm = JSonMapper.getInstance();
        System.out.println(Charset.defaultCharset());
        System.setProperty("file.encoding", "GBK");
        System.out.println(Charset.defaultCharset());
    }

    public void testAddChapter() throws Exception {
        assertTrue(ATestUtil.clearServerData());
        String s = ATestData.book2;
        Map<String, String> ps = new HashMap<String, String>();
        ps.put("data", s);

        // add one book without chapter info
        String r = ATestUtil.postAndGetString(ATestUtil.BookUrl, ps);
        assertNotNull(r);
        JSONObject obj = new JSONObject(r);
        assertTrue(obj.getBoolean("result"));

        // verify there is no chapter
        Book book = jm.parseBook(s);

        ps.clear();
        ps.put("name", book.getName());
        ps.put("author", book.getAuthor());
        ps.put("format", "json");
        ps.put("withChapter", "true");
        r = ATestUtil.getAndGetString(ATestUtil.BookUrl, ps);
        assertNotNull(r);
        obj = new JSONObject(r);

        if (obj.has("chapters")) {
            assertEquals(obj.getJSONArray("chapters").length(), 0);
        }

        // add chapters
        ps.clear();
        ps.put("data", ATestData.book2_chapters);
        r = ATestUtil.postAndGetString(ATestUtil.BookUrl, ps);
        assertNotNull(r);
        obj = new JSONObject(r);
        assertTrue(obj.getBoolean("result"));
        // verify there is chapter
        System.out.println(ATestData.book2_chapters);
        book = jm.parseBook(ATestData.book2_chapters);

        ps.clear();
        ps.put("name", book.getName());
        ps.put("author", book.getAuthor());
        ps.put("format", "json");
        ps.put("withChapter", "true");
        r = ATestUtil.getAndGetString(ATestUtil.BookUrl, ps);
        obj = new JSONObject(r);

        assertTrue(obj.has("chapters"));
        assertEquals(obj.getJSONArray("chapters").length(), book.getChapters().size());

        // add one more chapter
        int len = obj.getJSONArray("chapters").length();
        Chapter chapter = new Chapter();
        String s1 = "≤‚ ‘124", s2 = "ƒ⁄»›123";
        Date t1 = new Date();
        chapter.setName(s1);
        chapter.setContent(s2);
        chapter.setUpdateTime(t1);
        book.getChapters().add(chapter);
        String ns = book.toJsonString();

        ps.clear();
        ps.put("data", ns);
        r = ATestUtil.postAndGetString(ATestUtil.BookUrl, ps);
        assertNotNull(r);
        obj = new JSONObject(r);
        assertTrue(obj.getBoolean("result"));

        // verify there is the new added chapter        
        ps.clear();
        ps.put("name", book.getName());
        ps.put("author", book.getAuthor());
        ps.put("format", "json");
        ps.put("withChapter", "true");
        r = ATestUtil.getAndGetString(ATestUtil.BookUrl, ps);
        obj = new JSONObject(r);
        System.out.println(r);
        assertTrue(obj.has("chapters"));
        assertEquals(obj.getJSONArray("chapters").length(), book.getChapters().size());
        //verify every chapter is there
        Set<String> set = new HashSet<String>();
        for (Chapter c : book.getChapters()) {
            set.add(c.getName());
        }

        Book book2 = jm.parseBook(r);

        for (Chapter c : book2.getChapters()) {
            assertTrue(set.remove(c.getName()));
        }
        
    }

}

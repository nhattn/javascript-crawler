package com.zyd.web.service;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.json.JSONObject;

import com.zyd.core.busi.JSonMapper;
import com.zyd.core.dom.Book;
import com.zyd.ncore.busi.ATestData;
import com.zyd.ncore.busi.ATestUtil;

public class TestBook extends TestCase {
    JSonMapper jm;

    @Override
    protected void setUp() throws Exception {
        jm = JSonMapper.getInstance();
    }

    public void testAddBook() throws Exception {
        assertTrue(ATestUtil.clearServerData());
        String s = ATestData.book1;
        Map<String, String> ps = new HashMap<String, String>();
        ps.put("data", s);
        // add one book
        String r = ATestUtil.postAndGetString(ATestUtil.BookUrl, ps);
        assertNotNull(r);
        JSONObject obj = new JSONObject(r);
        assertTrue(obj.getBoolean("result"));

        // change name, add again
        Book book = jm.parseBook(s);
        s = s.replace(book.getName(), book.getName() + ":new");
        ps.clear();
        ps.put("data", s);
        r = ATestUtil.postAndGetString(ATestUtil.BookUrl, ps);
        assertNotNull(r);
        obj = new JSONObject(r);
        assertTrue(obj.getBoolean("result"));
    }

    public void testAddBook2() throws Exception {
        assertTrue(ATestUtil.clearServerData());
        String s = ATestData.book3;
        Map<String, String> ps = new HashMap<String, String>();
        ps.put("data", s);
        // add one book
        String r = ATestUtil.postAndGetString(ATestUtil.BookUrl, ps);
        assertNotNull(r);
        JSONObject obj = new JSONObject(r);
        assertTrue(obj.getBoolean("result"));

        // change name, add again
        Book book = jm.parseBook(s);
        s = s.replace(book.getName(), book.getName() + ":new");
        ps.clear();
        ps.put("data", s);
        r = ATestUtil.postAndGetString(ATestUtil.BookUrl, ps);
        assertNotNull(r);
        obj = new JSONObject(r);
        assertTrue(obj.getBoolean("result"));
    }

    public void testAddDuplicateBook() throws Exception {
        assertTrue(ATestUtil.clearServerData());
        String s = ATestData.book1;
        Map<String, String> ps = new HashMap<String, String>();
        ps.put("data", s);
        String r = ATestUtil.postAndGetString(ATestUtil.BookUrl, ps);
        assertNotNull(r);
        JSONObject obj = new JSONObject(r);
        assertTrue(obj.getBoolean("result"));

        // add duplicate 
        r = ATestUtil.postAndGetString(ATestUtil.BookUrl, ps);
        assertNotNull(r);
        obj = new JSONObject(r);
        assertTrue(obj.has("result"));
        assertFalse(obj.getBoolean("result"));
    }

    public void testUpdateBook() throws Exception {
        assertTrue(ATestUtil.clearServerData());
        String s = ATestData.book2;
        Map<String, String> ps = new HashMap<String, String>();
        ps.put("data", s);
        String r = ATestUtil.postAndGetString(ATestUtil.BookUrl, ps);
        assertNotNull(r);
        JSONObject obj = new JSONObject(r);
        assertTrue(obj.getBoolean("result"));

        JSONObject b = new JSONObject(s);
        assertTrue(b.has("totalChar"));
        b.put("totalChar", 100000000);

        ps.clear();
        ps.put("data", b.toString());
        r = ATestUtil.postAndGetString(ATestUtil.BookUrl, ps);
        assertNotNull(r);
        obj = new JSONObject(r);
        assertTrue(obj.getBoolean("result"));
    }
}

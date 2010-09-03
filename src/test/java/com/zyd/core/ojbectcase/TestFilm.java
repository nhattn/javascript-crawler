package com.zyd.core.ojbectcase;

import java.util.HashMap;

import junit.framework.TestCase;

import org.json.JSONObject;

import com.tj.common.util.test.CommonTestUtil;
import com.zyd.ATestUtil;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.objecthandler.Film;

public class TestFilm extends TestCase {
    @Override
    protected void setUp() throws Exception {
        ATestUtil.setUpSpring();
        HibernateUtil.deleteAllObject(HibernateUtil.EntityNames.Film);
    }

    public void testCreate() throws Exception {
        HashMap values = CommonTestUtil.loadValueMapFromClassPathFile(TestFilm.class, "film.prop", "UTF-8");
        JSONObject obj = new JSONObject(values.get("jsondata").toString());
        Film film = new Film();
        assertEquals(Boolean.TRUE, film.create(obj));
        //        assertEquals(Boolean.FALSE, film.create(obj));
    }
}

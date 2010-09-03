package com.zyd.core.ojbectcase;

import java.util.HashMap;

import junit.framework.TestCase;

import org.json.JSONObject;

import com.tj.common.util.test.CommonTestUtil;
import com.zyd.ATestUtil;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.objecthandler.Weather;

public class TestWeather extends TestCase {
    @Override
    protected void setUp() throws Exception {
        ATestUtil.setUpSpring();
        HibernateUtil.deleteAllObject(HibernateUtil.EntityNames.Weather);
    }

    public void testCreate() throws Exception {
        HashMap values = CommonTestUtil.loadValueMapFromClassPathFile(TestWeather.class, "weather.prop", "UTF-8");
        Weather weather = new Weather();
        assertEquals(Boolean.TRUE, weather.create(new JSONObject(values.get("jsondata").toString())));
        System.out.println("-----------------------");
        assertEquals(Boolean.FALSE, weather.create(new JSONObject(values.get("jsondata").toString())));

        values = CommonTestUtil.loadValueMapFromClassPathFile(TestWeather.class, "weather.prop", "UTF-8");
        assertEquals(Boolean.TRUE, weather.create(new JSONObject(CommonTestUtil.loadValueMapFromClassPathFile(TestWeather.class, "weather2.prop", "UTF-8").get(
                "jsondata").toString())));
    }
}

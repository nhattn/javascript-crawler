package com.zyd.core.ojbectcase;

import java.util.HashMap;

import junit.framework.TestCase;

import org.json.JSONObject;

import com.tj.common.util.test.CommonTestUtil;
import com.zyd.ATestUtil;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.objecthandler.Train;

public class TestTrain extends TestCase {
    @Override
    protected void setUp() throws Exception {
        ATestUtil.setUpSpring();
        HibernateUtil.deleteAllObject(HibernateUtil.EntityNames.Train);
        HibernateUtil.deleteAllObject(HibernateUtil.EntityNames.TrainStation);
    }

    public void testCreate() throws Exception {
        HashMap values = CommonTestUtil.loadValueMapFromClassPathFile(TestFilm.class, "huochepiao2.prop", "UTF-8");
        JSONObject obj = new JSONObject(values.get("jsondata").toString());
        Train train = new Train();
        assertEquals(Boolean.TRUE, train.create(obj));
    }

}

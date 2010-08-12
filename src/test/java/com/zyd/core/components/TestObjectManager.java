package com.zyd.core.components;

import java.util.HashMap;

import junit.framework.TestCase;

import com.tj.common.util.test.CommonTestUtil;
import com.zyd.ATestUtil;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.objecthandler.DefaultHandler;
import com.zyd.core.objecthandler.Handler;
import com.zyd.core.objecthandler.SearchResult;

public class TestObjectManager extends TestCase {
    DefaultHandler hanlder = null;

    @Override
    protected void setUp() throws Exception {
        ATestUtil.setUpSpring();
        hanlder = new DefaultHandler();
       HibernateUtil.deleteAllObject("GroupBuy");
    }

    public void testDefaultCreate() throws Exception {
        HashMap<String, String> values = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectHelper.class, "groupbuy.prop", "utf8");
        assertEquals(Boolean.TRUE, hanlder.create(values));
    }

    public void testQueryDefault() throws Exception {
        testDefaultCreate();
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(Handler.Parameter.PARAMETER_OBJECT_ID, "GroupBuy");
        SearchResult result = hanlder.query(values);
        assertEquals(1, result.count);

        System.out.println(result.result.get(0));
    }
}

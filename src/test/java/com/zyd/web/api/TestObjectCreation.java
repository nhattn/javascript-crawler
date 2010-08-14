package com.zyd.web.api;

import java.util.HashMap;

import junit.framework.TestCase;

import com.tj.common.util.test.CommonTestUtil;
import com.zyd.ATestUtil;
import com.zyd.Constants;
import com.zyd.core.components.TestObjectHelper;
import com.zyd.core.db.HibernateUtil;

/**
 * through /service/object gate
 * 
 *
 */
public class TestObjectCreation extends TestCase {
    @Override
    protected void setUp() throws Exception {
    }

    public void testCreateHouse() throws Exception {
        HibernateUtil.deleteAllObject(HibernateUtil.EntityNames.House);        
        HashMap<String, String> values = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectHelper.class, "house1.prop", Constants.Encoding_DEFAULT_SYSTEM);
        assertTrue(ATestUtil.createObject(values));
    }

    public void testCreateGroupBuy() throws Exception {
        HibernateUtil.deleteAllObject(HibernateUtil.EntityNames.GroupBuy);       
        HashMap<String, String> values = CommonTestUtil.loadValueMapFromClassPathFile(TestObjectHelper.class, "groupbuy.prop", Constants.Encoding_DEFAULT_SYSTEM);
        assertTrue(ATestUtil.createObject(values));
    }
}

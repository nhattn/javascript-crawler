package com.zyd.core;

import java.util.HashMap;

import junit.framework.TestCase;

import com.tj.common.util.test.CommonTestUtil;
import com.zyd.ATestUtil;
import com.zyd.core.objecthandler.Bus;

public class TestAiBangBusHandler extends TestCase {
    Bus busHandler = new Bus();

    @Override
    protected void setUp() throws Exception {
        ATestUtil.setUpSpring();
        //busHandler.deleteAll();
    }

    public void testCreateBus() throws Exception {
        HashMap values = CommonTestUtil.loadValueMapFromClassPathFile(TestAiBangBusHandler.class, "aibangbus.prop", "UTF-8");
        assertTrue(((Boolean) busHandler.create(values)).booleanValue());
    }

}

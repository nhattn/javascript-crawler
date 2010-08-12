package com.zyd.web;

import java.util.HashMap;

import com.zyd.ATestUtil;
import com.zyd.Constants;

import junit.framework.TestCase;

public class TestController extends TestCase {

    /**
     * make sure UpdateConfigure works as supposed to.
     * This test  may fail if running on different code base than the server.
     * @throws Exception
     */
    public void testUpdateConfigure() throws Exception {
        HashMap<String, String> oldConfigure = ATestUtil.getServerConfigure(), oldConfigureChanged, newConfigure = new HashMap<String, String>();
        // restore first, some other test may have changed default configuration.

        ATestUtil.reststoreServerConfigure();
        assertEquals(Constants.LENGTH_PAGE_SIZE, Integer.parseInt(oldConfigure.get("LENGTH_PAGE_SIZE")));
        assertEquals(Constants.MAX_PAGE_SIZE, Integer.parseInt(oldConfigure.get("MAX_PAGE_SIZE")));

        newConfigure.put("LENGTH_PAGE_SIZE", Integer.toString(Constants.LENGTH_PAGE_SIZE * 2));
        newConfigure.put("MAX_PAGE_SIZE", Integer.toString(Constants.MAX_PAGE_SIZE * 2));
        ATestUtil.updateServerConfigure(newConfigure);

        oldConfigureChanged = ATestUtil.getServerConfigure();
        assertEquals(oldConfigureChanged.get("LENGTH_PAGE_SIZE"), Integer.toString(Constants.LENGTH_PAGE_SIZE * 2));
        assertEquals(oldConfigureChanged.get("MAX_PAGE_SIZE"), Integer.toString(Constants.MAX_PAGE_SIZE * 2));

        ATestUtil.reststoreServerConfigure();
    }
}

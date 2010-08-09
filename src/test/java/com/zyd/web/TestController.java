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
        assertEquals(Constants.LINK_MONITOR_SCAN_INTERVAL, Integer.parseInt(oldConfigure.get("LINK_MONITOR_SLEEP")));
        assertEquals(Constants.LINK_PROCESSING_EXPIRE, Integer.parseInt(oldConfigure.get("LINK_PROCESSING_EXPIRE")));

        newConfigure.put("LINK_MONITOR_SLEEP", Integer.toString(Constants.LINK_MONITOR_SCAN_INTERVAL * 2));
        newConfigure.put("LINK_PROCESSING_EXPIRE", Integer.toString(Constants.LINK_PROCESSING_EXPIRE * 2));
        ATestUtil.updateServerConfigure(newConfigure);

        oldConfigureChanged = ATestUtil.getServerConfigure();
        assertEquals(oldConfigureChanged.get("LINK_MONITOR_SLEEP"), Integer.toString(Constants.LINK_MONITOR_SCAN_INTERVAL * 2));
        assertEquals(oldConfigureChanged.get("LINK_PROCESSING_EXPIRE"), Integer.toString(Constants.LINK_PROCESSING_EXPIRE * 2));

        ATestUtil.reststoreServerConfigure();
    }
}

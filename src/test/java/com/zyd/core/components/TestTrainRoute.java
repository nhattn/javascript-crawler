package com.zyd.core.components;

import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import com.zyd.ATestUtil;
import com.zyd.core.objecthandler.SearchResult;
import com.zyd.core.objecthandler.TrainRoute;

public class TestTrainRoute extends TestCase {
    @Override
    protected void setUp() throws Exception {
        ATestUtil.setUpSpring();
    }

    public void testQueryTrainRoute() throws Exception {
        HashMap<String, String> p = new HashMap<String, String>();
        p.put("src", "上海");
        p.put("dest", "北京");

        TrainRoute r = new TrainRoute();
        SearchResult result = r.query(p);
        assertNotNull(result);
        List list = result.result;
        System.out.println(list);        
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }
}

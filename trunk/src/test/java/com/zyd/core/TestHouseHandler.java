package com.zyd.core;

import java.util.HashMap;

import junit.framework.TestCase;

import com.zyd.core.objecthandler.House;
import com.zyd.core.objecthandler.SearchResult;
import com.zyd.core.objecthandler.Handler.Columns;

public class TestHouseHandler extends TestCase {
    @Override
    protected void setUp() throws Exception {
    }

    public void testQuery() {
        double d = 31.115685;
        House house = new House();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Columns.Lat, Double.toString(d));
        SearchResult r  = house.query(params);
        
        System.out.println(r.result);
    }

}

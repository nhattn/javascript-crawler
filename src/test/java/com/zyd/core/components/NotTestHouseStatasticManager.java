package com.zyd.core.components;

import junit.framework.TestCase;

import com.zyd.core.busi.house.HouseStatasticsManager;
import com.zyd.core.db.HibernateUtil;

public class NotTestHouseStatasticManager extends TestCase {
    public void nottestUpdateLastDayData() throws Exception {
        HibernateUtil.deleteAllObject(HibernateUtil.EntityNames.House_Data_Day);
        HouseStatasticsManager man = new HouseStatasticsManager();
//        assertTrue(man.updateDatabase());
    }
}

package com.zyd.core;

import com.zyd.ATestUtil;
import com.zyd.core.busi.userreg.SiteRegisterManager;

import junit.framework.TestCase;

public class TestSiteRegister extends TestCase {
    SiteRegisterManager m;

    @Override
    protected void setUp() throws Exception {
        ATestUtil.setUpSpring();
        m = new SiteRegisterManager();
    }

    public void testNextUser() throws Exception {
        String s = m.nextUser();
        assertNotNull(s);
        System.out.println(s);

        String s2 = m.nextUser();
        assertNotNull(s2);
        assertNotSame(s, s2);
    }
    
    public void testAddUser() throws Exception{
        m.addSiteUser("uname", "password", "dkdk@dld.com", "site");
    }
}

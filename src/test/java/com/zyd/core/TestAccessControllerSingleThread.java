package com.zyd.core;

import java.util.Date;

import junit.framework.TestCase;

import com.zyd.ATestUtil;
import com.zyd.core.access.AuthorizationController;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.access.ClientInfo;
import com.zyd.core.util.SpringContext;

public class TestAccessControllerSingleThread extends TestCase {

    AuthorizationController ac;

    @Override
    protected void setUp() throws Exception {
        ATestUtil.setUpSpring();
        ac = (AuthorizationController) SpringContext.getContext().getBean("authorizationController");
        HibernateUtil.deleteAllObject(ClientInfo.class.getName());
    }

    public void testCreateClient() throws Exception {
        ClientInfo info = new ClientInfo();
        info.setClientId("clientid");
        info.setClientkey("clientkey");
        info.setCompanyName("companyname");
        info.setCreateTime(new Date());
        info.setEmail("email@email.com");
        info.setLevel(1);
        info.setTotal(0);
        info.setTotalSinceLastCycle(0);
        ac.createClientInfo(info);
    }

    public void testClientLoggingIn() throws Exception {
        testCreateClient();
        assertTrue("loggin in use correct key and id", ac.authorize("clientid", "clientkey", "111.111.111.111"));
        assertFalse("Logging in with wrong id and key", ac.authorize("clientid1", "clientkey", "111.111.111.111"));
    }

    public void testClientAccess() throws Exception {
        testCreateClient();
        ac.authorize("clientid", "clientkey", "111.111.111.111");
        assertTrue("access with client key", ac.logAccess("clientid", "111.111.11.11"));
        assertFalse("access with wrong key", ac.logAccess("clientid1", "111.11.11.11"));
    }

}

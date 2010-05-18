package com.zyd.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.zyd.Constants;
import com.zyd.core.busi.LinkManager;
import com.zyd.core.util.SpringContext;

public class ZydContextListener implements ServletContextListener {

    public ZydContextListener() {

    }

    public void contextDestroyed(ServletContextEvent arg0) {
    }

    public void contextInitialized(ServletContextEvent arg0) {
        if (Constants.SERVER_DOMAIN == null) {
            // do nothing just to initialize 
        }
        ((LinkManager) SpringContext.getContext().getBean("linkManager")).loadFromDb();
    }

}

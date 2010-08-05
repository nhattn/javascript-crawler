package com.zyd.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.zyd.Constants;
import com.zyd.core.busi.AccessController;
import com.zyd.core.busi.LinkManager;
import com.zyd.core.util.IpCounter;
import com.zyd.core.util.SpringContext;

public class ZydContextListener implements ServletContextListener {
    public ZydContextListener() {
    }

    public void contextDestroyed(ServletContextEvent arg0) {
        ((LinkManager) SpringContext.getContext().getBean("linkManager")).stopMonitor();
        ((IpCounter) SpringContext.getContext().getBean("ipCounter")).stop();
        ((AccessController) SpringContext.getContext().getBean("accessController")).stopAccessBlocker();
    }

    public void contextInitialized(ServletContextEvent arg0) {
        if (Constants.SERVER_DOMAIN == null) {
            // do nothing just to initialize 
        }
        ((LinkManager) SpringContext.getContext().getBean("linkManager")).loadFromDb();
        ((LinkManager) SpringContext.getContext().getBean("linkManager")).startMonitor();
        ((IpCounter) SpringContext.getContext().getBean("ipCounter")).start();
        ((AccessController) SpringContext.getContext().getBean("accessController")).startAccessBlocker();
    }

}

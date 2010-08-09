package com.zyd.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.zyd.Constants;
import com.zyd.core.access.AccessController;
import com.zyd.core.access.AuthorizationController;
import com.zyd.core.access.IpCounter;
import com.zyd.core.busi.WorkerThread;
import com.zyd.core.util.SpringContext;

public class ZydContextListener implements ServletContextListener {
    public ZydContextListener() {
    }

    public void contextDestroyed(ServletContextEvent arg0) {
        ((WorkerThread) SpringContext.getContext().getBean("workerThread")).stop();
    }

    public void contextInitialized(ServletContextEvent arg0) {
        if (Constants.SERVER_DOMAIN == null) {
            // do nothing just to initialize 
        }
        WorkerThread workerThread = ((WorkerThread) SpringContext.getContext().getBean("workerThread"));
        workerThread.registerWork(((IpCounter) SpringContext.getContext().getBean("ipCounter")));
        workerThread.registerWork(((AccessController) SpringContext.getContext().getBean("accessController")));
        workerThread.registerWork(((AuthorizationController) SpringContext.getContext().getBean("authorizationController")));
        workerThread.registerWork(((com.zyd.linkmanager.LinkManager) SpringContext.getContext().getBean("linkManager")));
        workerThread.start();
    }
}

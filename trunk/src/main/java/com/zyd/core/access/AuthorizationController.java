package com.zyd.core.access;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.zyd.Constants;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.access.ClientInfo;
import com.zyd.core.util.SpringContext;

/**
 * control who can access the service, takes data from ClienInfo table.
 */
public class AuthorizationController implements Job {
    public final static String HibernateEntityName = "ClientInfo";
    public final static String TableName = "ClientInfo";

    private static Logger logger = Logger.getLogger(AuthorizationController.class);

    // clientid -> clientInfo
    private HashMap<String, ClientInfo> authorizedClient = new HashMap<String, ClientInfo>();

    public AuthorizationController() {
    }

    /*
     * takes user info from db and see who can access
     */
    public boolean authorize(String clientId, String key, String ip) {
        if (clientId != null && key != null) {
            ClientInfo client = authorizedClient.get(clientId);
            if (client == null) {
                client = loadClientInfoByClientId(clientId);
            }
            if (client == null)
                return false;
            if (client.getClientkey().equals(key) == false) {
                return false;
            }
            client.ip = ip;
            logger.info("Client logging in:" + clientId);
            synchronized (authorizedClient) {
                authorizedClient.put(clientId, client);
            }
        }
        return true;
    }

    /**
     * logs access by clientId. record how many times each client is accessed
     * @param clientId
     * @return true if the client is allowed to access, false otherwise
     */
    public boolean logAccess(String clientId, String ip) {
        ClientInfo c = authorizedClient.get(clientId);
        if (c == null) {
            logger.info("blocked access from ip:" + ip + ", client:" + clientId);
            return false;
        }
        c.lastAccessTime = System.currentTimeMillis();
        c.totalSinceLastCycle++;
        c.ip = ip;
        return true;
    }

    public ClientInfo loadClientInfoByClientId(String clientId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query q = session.createQuery("from ClientInfo as c where c.clientId=:clientId");
        q.setString("clientId", clientId);
        List list = q.list();
        session.getTransaction().commit();
        if (list.size() == 0) {
            return null;
        }
        return (ClientInfo) (list.get(0));
    }

    public void createClientInfo(ClientInfo clientInfo) {
        HibernateUtil.saveObject(clientInfo);
    }

    /** 
     * remove inactive client from cache
     */
    private void purgeInactiveClient() {
        ArrayList<String> keys = new ArrayList<String>(authorizedClient.keySet());
        long now = System.currentTimeMillis();
        int counter = 0;
        for (String clientId : keys) {
            ClientInfo clientInfo = authorizedClient.get(clientId);
            if (now - clientInfo.lastAccessTime > Constants.AUTHORIZATION_CONTROLLER_EXECUTION_INTERVAL) {
                HibernateUtil.updateObject(clientInfo);
                authorizedClient.remove(clientId);
                counter++;
            }
        }
        logger.info("Purged " + counter + " inactive client");
    }

    /**
     * Write access counts back to db.
     * Can safely write everything back to db, because this method is called after purgeInactiveClient from last cycle, so every in active client have been removed from
     * authroizedClient. Just save everything.
     * 
     * However, in a single cycle, this method must be called before purgeInactiveClient, or access data won't be saved, it will be cleaned by that method.
     */
    private void writeDataToDb() {
        Collection<ClientInfo> clients = authorizedClient.values();
        if (clients.isEmpty() == true)
            return;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        int counter = 0;
        for (ClientInfo client : clients) {
            session.update(client);
            if (++counter % 20 == 0) {
                session.flush();
                session.clear();
            }
        }
        session.getTransaction().commit();
        logger.info("Write client accessed infomation to database, total client:" + counter);
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        AuthorizationController ac = ((AuthorizationController) SpringContext.getContext().getBean("authorizationController"));
        ac.writeDataToDb();
        ac.purgeInactiveClient();
    }

}

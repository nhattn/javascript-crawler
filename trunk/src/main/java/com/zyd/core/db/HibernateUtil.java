package com.zyd.core.db;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.persister.entity.SingleTableEntityPersister;

import com.zyd.core.dom.DatabaseColumnInfo;
import com.zyd.core.objecthandler.ObjectHelper;

public class HibernateUtil {
    private static Logger logger = Logger.getLogger(HibernateUtil.class);
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private final static HashMap<String, String> entityMapping = new HashMap<String, String>();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            logger.fatal("Initial SessionFactory creation failed.");
            logger.fatal(ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static String getTableName(String entityId) {
        String tableName = entityMapping.get(entityId);
        if (tableName == null) {
            SingleTableEntityPersister s = ((SingleTableEntityPersister) getSessionFactory().getClassMetadata(entityId));
            if (s == null) {
                return null;
            }
            tableName = s.getTableName();
            entityMapping.put(entityId, tableName);
        }
        return tableName;
    }

    private static HashMap<String, HashMap<String, DatabaseColumnInfo>> metaMapping = new HashMap<String, HashMap<String, DatabaseColumnInfo>>();

    public synchronized static HashMap<String, DatabaseColumnInfo> getTableMetaData(String tableName) {
        HashMap<String, DatabaseColumnInfo> meta = metaMapping.get(tableName);
        if (meta == null) {
            meta = ObjectHelper.getTableMetaData(tableName);
            metaMapping.put(tableName, meta);
        }
        return meta;
    }

}
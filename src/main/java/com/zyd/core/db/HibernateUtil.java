package com.zyd.core.db;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
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
        } catch (HibernateException ex) {
            logger.fatal("Initial SessionFactory creation failed.");
            logger.fatal(ex);
            ex.printStackTrace();
            throw ex;
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

    /**
     * 
     * @param objectid an entity id in hibernate mapping file
     * @param values an array like name1, value1, name2, value2 etc..
     */
    public static void saveObject(String objectid, Object... values) {
        HashMap v = new HashMap();
        for (int i = 0; i < values.length; i++) {
            v.put(values[i++], values[i]);
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.save(objectid, v);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("Can not save object " + objectid, e);
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void saveObject(String entityName, HashMap values) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.save(entityName, values);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("Can not save object " + entityName, e);
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void saveObject(Object object) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.save(object);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("Can not save object " + object, e);
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void updateObject(Object object) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(object);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("Can not save object " + object, e);
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void updateObject(String entityId, HashMap values) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(entityId, values);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("Can not save object " + entityId, e);
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static List loadObject(String objectid) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try {
            List r = session.createQuery("from " + objectid).list();
            session.getTransaction().commit();
            return r;
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            return null;
        }
    }

    public static int deleteAllObject(String objectid) {
        int r = 0;
        final String deleteAll = "delete from " + objectid;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try {
            r = session.createQuery(deleteAll).executeUpdate();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return r;
    }

    public static class EntityNames {
        public final static String Film = "Film";
        public final static String Weather = "Weather";
        public final static String House = "House";
        public final static String House_CityList = "House_CityList";
        public final static String Train = "com.zuiyidong.layer.train";
        public final static String TrainStation = "com.zuiyidong.layer.trainstation";
        public final static String TrainSecondHandTicket = "TrainTicket";
        public final static String TrainTicketOffice = "com.zuiyidong.layer.train.ticketoffice";
        public final static String House_Data_Day = "House_Data";
        public final static String GroupBuy = "GroupBuy";
        public final static String BusLine = "com.zuiyidong.layer.busline";
        public final static String BusStation = "com.zuiyidong.layer.busstation";
    }
}
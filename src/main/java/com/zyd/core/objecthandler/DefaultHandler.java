package com.zyd.core.objecthandler;

import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.persister.entity.SingleTableEntityPersister;

import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.DatabaseColumnInfo;

public class DefaultHandler extends Handler {
    private static Logger logger = Logger.getLogger(DefaultHandler.class);

    /**
     * If you are using this one to create object, note that you have to put object name the same as the table name,
     * into request parameter, Parameter.PARAMETER_OBJECT_ID. 
     * That's how they get matched.
     */
    @Override
    public Object create(HashMap values) {
        String tableName = (String) values.get(Parameter.PARAMETER_OBJECT_ID);
        HashMap<String, DatabaseColumnInfo> meta = ObjectHelper.getTableMetaData((String) values.get(Parameter.PARAMETER_OBJECT_ID));
        ObjectHelper.nomorlizedParameters(values, meta);
        Date now = new Date();
        values.put(Columns.CreateTime, now);
        values.put(Columns.UpdateTime, now);

        Session session = null;
        boolean r = false;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            session.save(tableName, values);
            session.getTransaction().commit();
            r = true;
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null)
                session.getTransaction().rollback();
            logger.error("Exception when saving object to table" + tableName);
            logger.error(e);
            logger.debug("Values trying to save are:");
            logger.debug(values);
        }
        return r;

    }

    @Override
    public int deleteAll() {
        throw new UnsupportedOperationException("deleteAll not supported yet");
    }

    /**
     * 
     * @param objectName shuold be the same as that set in hibernate mapping file. 
     * @return
     */
    public final static int deleteAll(String objectName) {
        final String deleteAll = "delete from " + objectName;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        int r = session.createQuery(deleteAll).executeUpdate();
        session.getTransaction().commit();
        return r;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("getName not supported yet");
    }

    @Override
    public SearchResult query(HashMap params) {
        String objectName = (String) params.get(Parameter.PARAMETER_OBJECT_ID);
        //TODO: this may not work
        String tableName = ((SingleTableEntityPersister) HibernateUtil.getSessionFactory().getClassMetadata(objectName)).getTableName();
        HashMap<String, DatabaseColumnInfo> meta = HibernateUtil.getTableMetaData(tableName);
        return ObjectHelper.defaultQuery(params, objectName, meta, "-");
    }

}

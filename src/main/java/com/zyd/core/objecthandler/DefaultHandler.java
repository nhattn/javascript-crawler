package com.zyd.core.objecthandler;

import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.persister.entity.SingleTableEntityPersister;

import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.DatabaseColumnInfo;
import com.zyd.linkmanager.Link;

public class DefaultHandler extends Handler {
    private static Logger logger = Logger.getLogger(DefaultHandler.class);

    /**
     * If you are using this one to create object, note that you have to put object name the same as the entity name in hibernate mapping
     * into request parameter, Parameter.PARAMETER_OBJECT_ID. 
     * That's how they get matched.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object create(HashMap values) {
        String entityName = (String) values.get(Parameter.PARAMETER_OBJECT_ID);
        HashMap<String, DatabaseColumnInfo> tableMetaData = ObjectHelper.getTableMetaData((String) values.get(Parameter.PARAMETER_OBJECT_ID));
        ObjectHelper.nomorlizedParameters(values, tableMetaData);
        Date now = new Date();
        values.put(Columns.CreateTime, now);
        values.put(Columns.UpdateTime, now);
        Link link = (Link) values.remove(Columns.Link);
        if (link != null) {
            values.put(Columns.Link, link.url);
        }
        try {
            HibernateUtil.saveObject(entityName, values);
            return true;
        } catch (Exception e) {
            logger.error("Exception when saving entity" + entityName, e);
            logger.debug("Values trying to save are:");
            logger.debug(values);
            return false;
        }
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
        //TODO: this may not work, calling hibernate internal?        
        String tableName = ((SingleTableEntityPersister) HibernateUtil.getSessionFactory().getClassMetadata(objectName)).getTableName();
        HashMap<String, DatabaseColumnInfo> meta = HibernateUtil.getTableMetaData(tableName);
        return ObjectHelper.defaultQuery(params, objectName, meta);
    }

}

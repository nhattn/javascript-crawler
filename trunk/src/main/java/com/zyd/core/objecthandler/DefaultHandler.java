package com.zyd.core.objecthandler;

import java.util.HashMap;

import org.hibernate.persister.entity.SingleTableEntityPersister;

import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.DatabaseColumnInfo;

public class DefaultHandler extends Handler {

    private static HashMap<String, HashMap<String, DatabaseColumnInfo>> metaMapping = new HashMap<String, HashMap<String, DatabaseColumnInfo>>();

    @Override
    public Object create(HashMap values) {
        throw new UnsupportedOperationException("Create not supported yet");
    }

    @Override
    public int deleteAll() {
        throw new UnsupportedOperationException("Delete not supported yet");
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("getName not supported yet");
    }

    private synchronized static HashMap<String, DatabaseColumnInfo> getTableMetaData(String tableName) {
        HashMap<String, DatabaseColumnInfo> meta = metaMapping.get(tableName);
        if (meta == null) {
            meta = ObjectHelper.getTableMetaData(tableName);
            metaMapping.put(tableName, meta);
        }
        return meta;
    }

    @Override
    public SearchResult query(HashMap params) {
        String objectName = (String) params.get(Parameter.PARAMETER_OBJECT_ID);
        //TODO: this may not work
        String tableName = ((SingleTableEntityPersister) HibernateUtil.getSessionFactory().getClassMetadata(objectName)).getTableName();
        HashMap<String, DatabaseColumnInfo> meta = getTableMetaData(tableName);
        return ObjectHelper.defaultQuery(params, objectName, meta);
    }

}

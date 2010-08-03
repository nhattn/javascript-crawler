package com.zyd.core.objecthandler;

import java.util.HashMap;

import com.zyd.core.dom.DatabaseColumnInfo;

@SuppressWarnings("unchecked")
public abstract class Handler {

    public abstract String getName();

    public abstract Object create(HashMap values);

    public abstract SearchResult query(HashMap params);

    public abstract int deleteAll();

    /**
     * make sure the key specified in columns, exist in values
     * @return null if nothing is missing, or the missing column name
     */
    protected static String checkColumnExistence(String[] columns, HashMap values) {
        for (String c : columns) {
            if (values.containsKey(c) == false) {
                return c;
            }
        }
        return null;
    }

    public final static class Parameter {
        /**
         * start of the record
         */
        public final static String PARAMETER_START = "start";
        /**
         * how many to fetch, page size
         */
        public final static String PARAMETER_COUNT = "count";
        public final static String PARAMETER_ORDER_BY = "orderBy";
        public final static String PARAMETER_SKIP_URL_CHECK = "skipUrlCheck";
        public final static String PARAMETER_ORDER = "order";
        public final static String PARAMETER_VALUE_ORDER_ASC = "asc";
        public final static String PARAMETER_VALUE_ORDER_DESC = "desc";
        public final static String PARAMETER_OBJECT_ID = "objectid";

        protected final static Integer PARAMETER_VALUE_OK_YES = new Integer(1);
        protected final static Integer PARAMETER_VALUE_OK_NO = new Integer(0);
    }

    public static class Columns {
        public final static String ID = "id";
        public final static String Long = "lo";
        public final static String Lat = "la";
        public final static String OK = "ok";
        public final static String Referer = "referer";
        public final static String CreateTime = "createTime";
        public final static String UpdateTime = "updateTime";
    }
}

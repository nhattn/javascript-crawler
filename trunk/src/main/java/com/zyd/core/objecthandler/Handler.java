package com.zyd.core.objecthandler;

import java.util.HashMap;
import java.util.List;

import com.zyd.Config;

public abstract class Handler {
    public String getName() {
        return this.getClass().getName();
    }

    public abstract Object process(HashMap values);

    public abstract List load(HashMap params);

    public abstract int deleteAll();

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
        public final static String PARAMETER_ORDER = "order";
        public final static String PARAMETER_VALUE_ORDER_ASC = "asc";
        public final static String PARAMETER_VALUE_ORDER_DESC = "desc";
        public final static String PARAMETER_OBJECT_ID = Config.PARAMETER_NAME_OBJECT_ID;
    }

    public static class Columns {
        public final static String ID = "id";
        public final static String Long = "lo";
        public final static String Lat = "la";
    }
}

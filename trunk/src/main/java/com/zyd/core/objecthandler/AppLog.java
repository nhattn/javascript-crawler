package com.zyd.core.objecthandler;

import java.util.HashMap;

public class AppLog extends Handler {

    public final static String entityName = "AppLog";

    public String getEntityName() {
        return entityName;
    }

    public static class Columns extends Handler.Columns {
        public final static String ClientId = "clientId";
        public final static String App = "app";
        public final static String Action = "action";
        public final static String CreateTime = "createTime";
        public final static String Ip = "ip";
    }

    @Override
    protected boolean beforeCreate(HashMap values) {
        return true;
    }
}

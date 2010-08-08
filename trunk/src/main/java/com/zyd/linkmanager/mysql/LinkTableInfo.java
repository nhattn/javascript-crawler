package com.zyd.linkmanager.mysql;

public class LinkTableInfo {
    public int id;
    public int tableId;
    public String tableStringUid;

    public String getTableName() {
        return DbHelper.LinkTablePrefix + Integer.toString(tableId);
    }
}

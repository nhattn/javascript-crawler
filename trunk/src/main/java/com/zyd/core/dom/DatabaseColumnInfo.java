package com.zyd.core.dom;

public class DatabaseColumnInfo {
    public String name;
    public int type;
    public int size;

    public DatabaseColumnInfo(String name, int type, int size) {
        this.name = name;
        /** java.sql.Types **/
        this.type = type;
        this.size = size;
    }
}

package com.zyd.core.dom.access;

import java.util.Date;

public class ClientInfo {
    private long id;
    private String clientkey;
    private String clientId;
    private String email;
    private String companyName;
    private int level;
    public long total;
    public long totalSinceLastCycle;
    private Date createTime;

    /** not persistent **/
    public long lastAccessTime;
    public String ip;
    
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClientkey() {
        return clientkey;
    }

    public void setClientkey(String clientkey) {
        this.clientkey = clientkey;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTotalSinceLastCycle() {
        return totalSinceLastCycle;
    }

    public void setTotalSinceLastCycle(long totalSinceLastCycle) {
        this.totalSinceLastCycle = totalSinceLastCycle;
    }

}

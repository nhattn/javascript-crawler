package com.zyd.core.dom.bus;

import java.util.Date;

import com.zyd.core.dom.XmlParcel;

public class Bus implements XmlParcel {
    public long id;
    public String name;
    public String city;
    public String description;
    public String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date updateTime;

    public String toXml() {
        StringBuffer buf = new StringBuffer();
        buf.append("<object>");
        buf.append("<id>");
        buf.append(id);
        buf.append("</id>");
        buf.append("<name>");
        buf.append(name);
        buf.append("</name>");
        buf.append("<city>");
        buf.append(city);
        buf.append("</city>");
        buf.append("<description><![CDATA[");
        buf.append(description);
        buf.append("]]></description>");
        buf.append("</object>");
        return buf.toString();
    }
}

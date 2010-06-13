package com.zyd.core.dom;

import java.util.Date;

import com.zyd.core.Utils;

public class Link {
    public long id;
    public String url;
    public Date createTime;
    public String hash;

    public Date processTime;
    public String errorMsg;
    public int tryCount;
    public int isError;

    // when link is send back for user to process, not persistent
    public Date startTime;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getIsError() {
        return isError;
    }

    public void setIsError(int isError) {
        this.isError = isError;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Link() {
        this(null, null, null);
    }

    public Link(String url) {
        this(url, new Date(), Utils.stringHash(url));
    }

    public Link(String url, Date createTime, String hash) {
        this.url = url;
        this.createTime = createTime;
        this.hash = hash;
        this.tryCount = 0;
        this.isError = 0;
    }

    public int getTryCount() {
        return tryCount;
    }

    public void setTryCount(int tryCount) {
        this.tryCount = tryCount;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }

    @Override
    public int hashCode() {
        if (url != null) {
            return url.hashCode();
        } else {
            return super.hashCode();
        }
    }

    @Override
    public String toString() {
        return "[Link, url=" + url + ", id=" + id + "]";
    }

    public void cleanup() {
        this.id = 0;
        this.isError = 0;
        this.processTime = null;
        this.createTime = null;
        this.errorMsg = null;
        this.startTime = null;
        this.url = null;
    }
}

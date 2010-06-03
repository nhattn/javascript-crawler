package com.zyd.core.dom;

import java.util.Date;

public class Link {
    public long id;
    public String url;
    public Date createTime;

    // when link is been processed, if this is not null, meaning link is processed OK.
    public Date processTime;

    public int tryCount;
    // when link is send back for user to process
    public Date startTime;

    public int isError = 0;
    public String errorMsg;

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
    }

    public Link(String url) {
        this.url = url;
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
}

package com.zyd.core.dom;

public class BookFilter {
    public int start;
    public int count;
    public String sortField;

    public String keyword;
    public String keywordField;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeywordField() {
        return keywordField;
    }

    public void setKeywordField(String keywordField) {
        this.keywordField = keywordField;
    }

}

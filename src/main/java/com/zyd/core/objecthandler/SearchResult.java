package com.zyd.core.objecthandler;

import java.util.List;

@SuppressWarnings("unchecked")
public class SearchResult {
    public List result;
    public int totalResult;
    public int start;
    public int count;

    public SearchResult(List result, int total, int start, int count) {
        this.result = result;
        this.totalResult = total;
        this.start = start;
        this.count = count;
    }

}

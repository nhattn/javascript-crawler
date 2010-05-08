package com.zyd.core;

import java.util.HashMap;
import java.util.List;

public interface Handler {
    public String getName();

    public Object process(HashMap<String, Object> values);

    public List load(HashMap<String, String> params);
}

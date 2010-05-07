package com.zyd.core;

import java.util.HashMap;

public interface Handler {
    public String getName();
    public Object process(HashMap<String, String> values);
}

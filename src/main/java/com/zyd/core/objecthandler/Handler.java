package com.zyd.core.objecthandler;

import java.util.HashMap;
import java.util.List;

public abstract class Handler {
    public String getName() {
        return this.getClass().getName();
    }

    public abstract Object process(HashMap values);

    public abstract List load(HashMap params);
    
    public abstract int deleteAll();
}

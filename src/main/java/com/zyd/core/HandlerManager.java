package com.zyd.core;

import java.util.HashMap;

import com.zyd.core.housing.HouseHandler;

public class HandlerManager {

    private static HandlerManager instance = new HandlerManager();
    private HashMap<String, Handler> mapping;

    private HandlerManager() {
        mapping = new HashMap<String, Handler>();
        mapping.put("housing", new HouseHandler());
    }

    public static HandlerManager getInstance() {
        return instance;
    }

    public Handler getHandler(String name) {
        return mapping.get(name);
    }
}

package com.zyd.core.objecthandler;

import java.util.HashMap;

public class DefaultHandler extends Handler {

    @Override
    public String getEntityName() {
        throw new UnsupportedOperationException("getName not supported yet");
    }

    @Override
    protected boolean beforeCreate(HashMap values) {
        return true;
    }

}

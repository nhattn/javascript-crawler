package com.zyd.core.busi;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.tj.common.CommonUtil;
import com.zyd.Constants;

public class TemplateManager {

    private static HashMap<String, String> templateCache = new HashMap<String, String>();

    private TemplateManager() {
    }

    public String getTemplate(String name) {
        String r = templateCache.get(name);
        if (r == null) {
            try {
                r = loadTemplateFileByName(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return r;
    }

    public String getTemplate(String name, List<String> params) {
        String template = getTemplate(name);
        for (int i = 0; i < params.size(); i++) {
            template = template.replace("$p" + i + "$", params.get(i));
        }
        return template;
    }

    private String loadTemplateFileByName(String name) throws IOException {
        InputStream ins = null;
        try {
            ins = TemplateManager.class.getClassLoader().getResourceAsStream("template/" + name);
            return IOUtils.toString(ins, Constants.Encoding_DEFAULT_SYSTEM);
        } finally {
            CommonUtil.closeStream(ins);
        }
    }

    public String getNextAction(String action, String... paras) {
        String r = "{'action':'No.Action'}";
        JSONObject nextAction = new JSONObject();
        try {
            nextAction.put("action", action);
            if (paras != null && paras.length > 0) {
                for (int i = 0; i < paras.length; i++) {
                    nextAction.put("para" + 1, paras[i]);
                }
            }
            r = nextAction.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return r;
    }
}

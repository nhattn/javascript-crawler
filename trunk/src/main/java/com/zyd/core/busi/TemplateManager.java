package com.zyd.core.busi;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.zyd.Config;

public class TemplateManager {
    private static TemplateManager instance = new TemplateManager();

    private static HashMap<String, String> templateCache = new HashMap<String, String>();

    private TemplateManager() {
    }

    public static TemplateManager getInstance() {
        return instance;
    }

    public String getTemplate(String name) {
        String r = templateCache.get(name);
        if (r == null) {
            try {
                r = loadTemplateFile(name);
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

    private String loadTemplateFile(String name) throws IOException {
        File f = new File(Config.TemplatePath, name);
        String s = FileUtils.readFileToString(f, "GBK");
        return s;
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

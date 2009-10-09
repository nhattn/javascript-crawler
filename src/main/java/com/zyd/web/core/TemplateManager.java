package com.zyd.web.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

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
        String templateHome = "E:\\workspace\\crawler\\src\\main\\webapp\\temp";
        File f = new File(templateHome, name);
        String s = FileUtils.readFileToString(f, "GBK");
        return s;
    }
}

package com.zyd.core.busi;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.tj.common.CommonUtil;
import com.zyd.Constants;

public class TemplateManager {
    private static Logger logger = Logger.getLogger(TemplateManager.class);

    private static HashMap<String, String> templateCache = new HashMap<String, String>();

    private TemplateManager() {
    }

    public String getTemplate(String name) {
        String r = templateCache.get(name);
        if (r == null) {
            try {
                r = loadTemplateFileByName(name);
                templateCache.put(name, r);
            } catch (Exception e) {
                logger.error("Failed load tempate with name :" + name);
                logger.error(e);
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
            String fileName = name;
            ins = TemplateManager.class.getClassLoader().getResourceAsStream("template/" + fileName);
            if (ins == null) {
                fileName = name + "." + Constants.INSTANCE_NAME;
                ins = TemplateManager.class.getClassLoader().getResourceAsStream("template/" + fileName);
            }
            if (ins == null) {
                throw new IOException("Can not load template " + name);
            }
            logger.info("Loading template " + name + " from file " + fileName);
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
            logger.warn("Invalid json parameter:");
            logger.warn(e);
        }
        return r;
    }
}

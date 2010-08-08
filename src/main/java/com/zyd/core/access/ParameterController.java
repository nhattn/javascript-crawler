package com.zyd.core.access;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.tj.common.CommonUtil;
import com.zyd.Constants;

/**
 * Controlls which parameters are open for query.
 * By default, all parameters can be queried, buy some is disabled for performance and security reasons.  
 * Access definition is taken from a file called "allowed.prop" under class path.
 * 
 */
public class ParameterController {
    private static Logger logger = Logger.getLogger(ParameterController.class);
    private static String[] defaultAllowed = new String[] { "start", "count", "lng", "lat", "layer", "format", "clientId" };

    private static HashMap<String, HashSet<String>> mapping = new HashMap<String, HashSet<String>>();

    static {
        try {
            init();
        } catch (IOException e) {
            logger.error("Can not load configuratoin for ParameterController", e);
        }
    }

    private static void init() throws IOException {
        InputStream ins = Constants.class.getClassLoader().getResourceAsStream(Constants.ALLOWED_API_QUERY_PARAMETER_CONFIG_FILE);
        HashMap<String, String> ps = CommonUtil.loadPropertyFile(ins);
        Set<String> layers = ps.keySet();
        for (String layer : layers) {
            HashSet<String> p = new HashSet<String>();
            mapping.put(layer, p);
            String value = ps.get(layer);
            if (value != null && value.trim().length() != 0) {
                StringTokenizer tokens = new StringTokenizer(value, ",");
                while (tokens.hasMoreElements()) {
                    p.add(tokens.nextToken().trim());
                }
            }
            for (String s : defaultAllowed) {
                p.add(s);
            }
        }
    }

    public static boolean isLayerAllowed(String layer) {
        return mapping.containsKey(layer);
    }

    public static boolean isParameterAllowed(Set<String> parameterNames, String layer) {
        HashSet<String> s = mapping.get(layer);
        if (s == null)
            return false;
        for (String s1 : parameterNames) {
            if (s.contains(s1) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isParameterAllowed(String parameterName, String layer) {
        HashSet<String> s = mapping.get(layer);
        if (s == null)
            return false;
        return s.contains(parameterName);
    }
}

package com.zyd.web.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zyd.core.db.HibernateUtil;
import com.zyd.web.ServiceBase;

public class vs extends ServiceBase {
    Logger logger = Logger.getLogger(vs.class);

    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String s = req.getParameter("s");
        String clientId = req.getParameter("clientId");

        if (s != null && clientId != null) {
            try {
                int[] is = restoreLocation(s);
                HashMap values = new HashMap();
                values.put("cellId", new Integer(is[0]));
                values.put("lacId", new Integer(is[1]));
                values.put("lng", new Integer(is[2]));
                values.put("lat", new Integer(is[3]));
                values.put("clientId", clientId.substring(0, clientId.lastIndexOf('-')));
                values.put("createTime", new Date());
                HibernateUtil.saveObject("CellInfo", values);
            } catch (Exception e) {
                logger.warn("Error while saving client info", e);
            }
        } else {
            logger.warn("Trying to hack server, clientid or s is null " + s + ", " + clientId);
        }
        output("ok", resp);
    }

    public static int[] restoreLocation(String content) {
        char[] buf = new char[32];
        int counter = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                buf[j * 8 + i] = content.charAt(counter++);
            }
        }
        content = new String(buf);
        int[] r = new int[] { Integer.MAX_VALUE - Integer.parseInt(content.substring(0, 8), 16),
                Integer.MAX_VALUE - Integer.parseInt(content.substring(8, 16), 16), Integer.MAX_VALUE - Integer.parseInt(content.substring(16, 24), 16),
                Integer.MAX_VALUE - Integer.parseInt(content.substring(24, 32), 16) };
        return r;
    }
}

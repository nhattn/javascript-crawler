package com.zyd.core.busi.api;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zyd.Constants;
import com.zyd.core.Utils;
import com.zyd.core.db.HibernateUtil;

public class ApiHandler {

    public String query(String layer, double lng1, double lng2, double lat1, double lat2, int count, int start, String key, String format) {
        if (isObjectManaged(layer) == false) {
            return null;
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        List list = null;

        try {
            session.beginTransaction();
            Criteria c = session.createCriteria(layer);
            c.add(Restrictions.ge("lng", lng1));
            c.add(Restrictions.le("lng", lng2));
            c.add(Restrictions.ge("lat", lat1));
            c.add(Restrictions.le("lat", lat2));
            c.setFirstResult(start);
            if (count > Constants.MAX_PAGE_SIZE) {
                count = Constants.MAX_PAGE_SIZE;
            }
            c.setMaxResults(count);
            list = c.list();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            return "";
        } catch (Error e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            return "";
        }
        if (format == null || "xml".equals(format)) {
            return toXmlString(list);
        } else {
            try {
                return toJsonString(list);
            } catch (JSONException e) {
                return "";
            }
        }
    }

    public boolean isObjectManaged(String objectId) {
        return HibernateUtil.getSessionFactory().getClassMetadata(objectId) != null;
    }

    private static String toJsonString(List list) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        obj.put("objects", arr);
        for (int i = 0, len = list.size(); i < len; i++) {
            HashMap map = (HashMap) list.get(i);
            String objectId = (String) map.remove("$type$");
            JSONObject item = new JSONObject();
            Set keys = map.keySet();
            for (Object k : keys) {
                Object o = map.get(k);
                if (o == null)
                    continue;
                item.put(k.toString(), o.toString());
            }
            arr.put(item);
        }
        return obj.toString();
    }

    private static String toXmlString(List list) {
        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buf.append("<objects>");
        for (int i = 0, len = list.size(); i < len; i++) {
            buf.append("<object>");
            HashMap map = (HashMap) list.get(i);
            map.remove("$type$");
            Set keys = map.keySet();
            for (Object k : keys) {
                buf.append('<');
                buf.append(k);
                buf.append('>');
                Object o = map.get(k);
                if (o == null) {
                    o = "";
                }
                buf.append(Utils.xmlString(o.toString()));

                buf.append("</");
                buf.append(k);
                buf.append('>');
            }
            buf.append("</object>");
        }
        buf.append("</objects>");
        return buf.toString();
    }
}

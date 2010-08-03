package com.zyd.core;

import java.util.HashMap;

import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@SuppressWarnings("unchecked")
public class StringUtil {
    public static String toJsonString(List list) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        obj.put("objects", arr);
        for (int i = 0, len = list.size(); i < len; i++) {
            HashMap map = (HashMap) list.get(i);
            map.remove("$type$");
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

    public static String toXmlString(List list) {
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

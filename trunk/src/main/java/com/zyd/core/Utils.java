package com.zyd.core;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.zyd.core.dom.Book;

public class Utils {
    static Random rand = new Random();
    static HashSet<String> usedString = new HashSet<String>();

    public static String getUniqueBookId() {
        Date d = new Date();
        StringBuffer buf = new StringBuffer(Long.toString(d.getTime()));
        String l = StringUtils.leftPad(Integer.toString(Math.abs(rand.nextInt())), 15, '0');
        buf.append(l);
        return buf.toString();
    }

    public static String objToXml(Object obj) throws UnsupportedEncodingException {
        return objToXml(obj, "UTF-8");
    }

    public static String objToXml(Object obj, String encoding) throws UnsupportedEncodingException {
        if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            if (list.size() > 0 && list.get(0) instanceof Book) {
                try {
                    return bookListToXml((List<Book>) obj, encoding);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLEncoder enc = new XMLEncoder(out);
        enc.writeObject(obj);
        enc.close();
        return new String(out.toByteArray(), encoding);
    }

    public static String bookListToXml(List<Book> books, String encoding) {
        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
        buf.append("<books>");
        for (Book book : books) {
            buf.append(book.toXMLString(encoding));
        }
        buf.append("</books>");
        return buf.toString();
    }

    public static String stringToFlatList(List<String> list) {
        StringBuffer buf = new StringBuffer();
        for (String s : list) {
            buf.append(s);
            buf.append("\r\n");
        }
        return buf.toString();
    }

    public static String mapToJsonString(HashMap<String, Object> map) throws IOException {
        JSONObject obj = new JSONObject();
        Set<String> names = map.keySet();
        try {
            for (String name : names) {
                obj.put(name, map.get(name).toString());
            }
        } catch (JSONException e) {
            throw new IOException(e);
        }
        return obj.toString();
    }

    /**
     * pass a string array like {"name1", "value1", "name2", "value2"}
     * @param values
     * @return
     * @throws IOException
     */
    public static String stringArrayToJsonString(String[] values) throws IOException {
        JSONObject obj = new JSONObject();

        try {
            if (values.length % 2 != 0)
                throw new IOException("Wrong number of values for method, not even.");
            for (int i = 0; i < values.length; i++) {
                obj.put(values[i++], values[i]);
            }
        } catch (JSONException e) {
            throw new IOException(e);
        }
        return obj.toString();
    }

    private static String getNoRepeatString() {
        StringBuffer buf = new StringBuffer();
        Date d = new Date();
        buf.append(Long.toString(d.getTime() + rand.nextInt(100000)));
        String s = StringUtils.rightPad(buf.toString(), 15, '0');
        if (usedString.contains(s)) {
            return getNoRepeatString();
        } else {
            usedString.add(s);
            return s;
        }
    }

    public synchronized static String nextBookId() {
        return "b" + getNoRepeatString();
    }

    public synchronized static String nextChapterId() {
        return "c" + getNoRepeatString();
    }

    public synchronized static String nextSiteId() {
        return "s" + getNoRepeatString();
    }

    public synchronized static String nextBookSiteId() {
        return "bs" + getNoRepeatString();
    }

    public synchronized static String nextChapterSiteId() {
        return "cs" + getNoRepeatString();
    }

    public static <T> T getUpdateObject(T s1, T s2) {
        if (s1 == null && s2 != null) {
            return s2;
        } else if (s1 == null && s2 == null) {
            return null;
        } else if (s1 != null && s2 != null) {
            return s2;
        } else if (s1 != null && s2 == null) {
            return s1;
        }
        return null;
    }

    public static <T> boolean strictEqual(T o1, T o2) {
        if (o1 == null && o2 == null)
            return true;
        if (o1 != null && o2 != null) {
            return o1.equals(o2);
        }
        return false;
    }

    public static String getDomain(String s) {
        if (s == null)
            return null;
        if (s.startsWith("http://")) {
            s = s.substring("http://".length());
        } else if (s.startsWith("https://")) {
            s = s.substring("https://".length());
        }
        int i = s.indexOf("/");
        if (i != -1) {
            s = s.substring(0, i);
        }
        i = s.lastIndexOf('.', s.length());
        if (i == -1) {
            i = 9 / 0;
        }
        i = s.lastIndexOf('.', i - 1);
        if (i != -1) {
            s = s.substring(i + 1);
        }
        return "www." + s;
    }

    private static DateFormat[] dateFormats = new SimpleDateFormat[] { 
    new SimpleDateFormat("yy-MM-dd HH:mm"), /*09-10-13 13:57*/
    new SimpleDateFormat("yyyy年MM月dd日"), /* 2009年10月15日*/
    new SimpleDateFormat("yyyy-MM-dd"),/*2008-10-17*/
    new SimpleDateFormat("yyyy-MM-d"),/*2008-10-17*/
    new SimpleDateFormat("yyyy-M-dd"),/*2008-1-17*/
    new SimpleDateFormat("yyyy-M-d"),/*2008-1-1*/
    };

    public static Date parseDate(String s) {
        Date date = null;
        for (DateFormat d : dateFormats) {
            try {
                date = d.parse(s);
            } catch (Exception e) {
            }
        }
        if (date == null) {
            //System.err.println("Unable to parse date string :" + s); 
            //TODO: have to report this
            date = new Date();
        }
        return date;
    }

    public static void main(String[] args) {
        System.out.println(getDomain("http://www.aaa.com"));
        System.out.println(getDomain("http://www.aaa.bbb.com"));
        System.out.println(getDomain("www.bbb.com"));
        System.out.println(getDomain("cc.bbb.com"));
        System.out.println(getDomain("bbb.com"));
        System.out.println(getDomain("http://bbb.com"));

        System.out.println(parseDate("09-10-13 13:57"));
    }
}

package com.zyd.ncore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

public class Utils {
    static Random rand = new Random();
    static HashSet<String> usedString = new HashSet<String>();

    private static String getNoRepeatString() {
        StringBuffer buf = new StringBuffer();
        Date d = new Date();
        buf.append(Long.toString(d.getTime() + rand.nextInt(100000)));
        String s = StringUtils.rightPad(buf.toString(), 15);
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

    private static DateFormat[] dateFormats = new SimpleDateFormat[]{
       new SimpleDateFormat("yy-MM-dd HH:mm"), /*09-10-13 13:57*/
    };

    public static Date parseDate(String s) {
        Date date = new Date();
        for (DateFormat d : dateFormats) {
            try {
                date = d.parse(s);
            } catch (Exception e) {
            }
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
package com.zyd.ncore;

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
}

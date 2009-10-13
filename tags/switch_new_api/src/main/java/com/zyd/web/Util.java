package com.zyd.web;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

public class Util {
    private static Random rand = new Random();

    public static String getUniqueBookId() {
        Date d = new Date();
        StringBuffer buf = new StringBuffer(Long.toString(d.getTime()));
        String l = StringUtils.leftPad(Integer.toString(Math.abs(rand.nextInt())), 15, '0');
        buf.append(l);
        return buf.toString();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.print(getUniqueBookId() + ',');
        }
    }

    public static String objToXml(Object obj) throws UnsupportedEncodingException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLEncoder enc = new XMLEncoder(out);
        enc.writeObject(obj);
        enc.close();
        return new String(out.toByteArray(), "UTF-8");
    }
    
    public static String stringToFlatList(List<String> list){
        StringBuffer buf = new StringBuffer();
        for(String s: list){
            buf.append(s);
            buf.append("\r\n");
        }
        return buf.toString();
    }
}

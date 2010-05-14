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
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.zyd.Constants;
import com.zyd.core.dom.Book;

@SuppressWarnings("unchecked")
public class Utils {
    static Random rand = new Random();
    static HashSet<String> usedString = new HashSet<String>();
    private final static HashMap<Character, Character> CHARMAP = new HashMap<Character, Character>();

    static {
        CHARMAP.put('一', '1');
        CHARMAP.put('二', '2');
        CHARMAP.put('三', '3');
        CHARMAP.put('四', '4');
        CHARMAP.put('五', '5');
        CHARMAP.put('六', '6');
        CHARMAP.put('七', '7');
        CHARMAP.put('零', '0');
        CHARMAP.put('八', '8');
        CHARMAP.put('九', '9');
        CHARMAP.put('1', '1');
        CHARMAP.put('7', '7');
        CHARMAP.put('2', '2');
        CHARMAP.put('8', '8');
        CHARMAP.put('3', '3');
        CHARMAP.put('9', '9');
        CHARMAP.put('4', '4');
        CHARMAP.put('0', '0');
        CHARMAP.put('5', '5');
        CHARMAP.put('6', '6');
    }

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
            buf.append("\n");
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
     * 
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

    private static DateFormat[] dateFormats = new SimpleDateFormat[] { new SimpleDateFormat("yy-MM-dd HH:mm"), /* 09-10-13 13:57 */
    new SimpleDateFormat("yyyy年MM月dd日"), /* 2009年10月15日 */
    new SimpleDateFormat("yyyy-MM-dd"),/* 2008-10-17 */
    new SimpleDateFormat("yyyy-MM-d"),/* 2008-10-17 */
    new SimpleDateFormat("yyyy-M-dd"),/* 2008-1-17 */
    new SimpleDateFormat("yyyy-M-d"),/* 2008-1-1 */
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
            // System.err.println("Unable to parse date string :" + s);
            // TODO: have to report this
            date = new Date();
        }
        return date;
    }

    public static <T> T toDBEncoding(T obj) {

        try {
            Map maps = BeanUtils.describe(obj);
            Set set = maps.keySet();
            for (Object o : set) {
                if (o instanceof String) {
                    String s = (String) o;
                    Object v = maps.get(o);
                    if (v != null && v instanceof String) {
                        String value = (String) v;
                        value = new String(value.getBytes(Constants.Encoding_DEFAULT_SYSTEM), Constants.ENCODING_DB);
                        try {
                            BeanUtils.setProperty(obj, s, value);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static <T> T fromDBEncoding(T obj) {
        try {
            Map maps = BeanUtils.describe(obj);
            Set set = maps.keySet();
            for (Object o : set) {
                if (o instanceof String) {
                    String s = (String) o;
                    Object v = maps.get(o);
                    if (v != null && v instanceof String) {
                        String value = (String) v;
                        value = new String(value.getBytes(Constants.ENCODING_DB), Constants.Encoding_DEFAULT_SYSTEM);
                        try {
                            BeanUtils.setProperty(obj, s, value);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static void castValues(Map map, String key, Class clazz) {
        String s = (String) map.get(key);
        if (s == null || s.trim().length() == 0)
            return;
        if (clazz.equals(Integer.class)) {
            map.put(key, Integer.parseInt(s));
        } else if (clazz.equals(Long.class)) {
            map.put(key, Long.parseLong(s));
        } else if (clazz.equals(Float.class)) {
            map.put(key, Float.parseFloat(s));
        } else if (clazz.equals(Double.class)) {
            map.put(key, Double.parseDouble(s));
        } else if (clazz.equals(Date.class)) {
            map.put(key, Long.parseLong(s));
        } else {
            throw new UnsupportedOperationException("not supported class " + clazz);
        }
    }

    private final static Object[] Range_Default = new Object[2];

    /**
     * s is an '-' separated string , like "12.33-31.22" or "-12.00" or "21.0-"
     * clazz is what type of class to try to parse s to.
     * 
     * @param s
     * @param clazz
     * @return an array of objects that has two elements, if neither is missing, will be null, or -1 for
     * primitive types. or null if there s is invalid or blank.
     */

    public static Object[] parseRangeObject(String s, Class clazz) {
        if (s == null || s.indexOf('-') == -1) {
            System.err.println("Invalid range:" + s);
            return null;

        }
        int i = s.indexOf('-');
        String s1 = null, s2 = null;
        if (i == 0) {
            s2 = s.substring(1);
        } else if (i == s.length() - 1) {
            s1 = s.substring(0, s.length() - 1);
        } else {
            s1 = s.substring(0, i);
            s2 = s.substring(i + 1);
        }

        if (clazz.equals(Integer.class)) {
            Integer[] r = new Integer[] { null, null };
            if (s1 != null) {
                r[0] = Integer.parseInt(s1);
            }
            if (s2 != null) {
                r[1] = Integer.parseInt(s2);
            }
            return r;
        }

        if (clazz.equals(Double.class)) {
            Double[] r = new Double[] { null, null };
            if (s1 != null) {
                r[0] = Double.parseDouble(s1);
            }
            if (s2 != null) {
                r[1] = Double.parseDouble(s2);
            }
            return r;
        }

        if (clazz.equals(Date.class)) {
            Date[] r = new Date[] { null, null };
            try {
                if (s1 != null) {
                    r[0] = Constants.DATEFORMAT_DEFAULT.parse(s1);
                }
                if (s2 != null) {
                    r[1] = Constants.DATEFORMAT_DEFAULT.parse(s2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return r;
        }

        return null;
    }

    public static int parseInit(String s, int defaultValue) {
        try {
            defaultValue = Integer.parseInt(s);
        } catch (Exception e) {
            //TODO:
            e.printStackTrace();
        }
        return defaultValue;

    }

    public static int gpsDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return (int) (6371 * 1000 * c);
    }

    public static int gpsDistance2(double lat1, double lon1, double lat2, double lon2) {
        double EARTH_CIRC_METERS = 40030218;
        double radLat1 = lat1;//  Math.toRadians(lat1);
        double radLon1 = lon1; //Math.toRadians(lon1);
        double radLat2 = lat2; //Math.toRadians(lat2);
        double radLon2 = lon2; //Math.toRadians(lon2);        
        double d = Math.acos((Math.cos(radLat1) * Math.cos(radLat2)) + (Math.sin(radLat1) * Math.sin(radLat2)) * (Math.cos(radLon1 - radLon2)));
        return (int) (d * EARTH_CIRC_METERS);
    }

    public static void main1(String[] args) {
        System.out.println(gpsDistance(-86.670048, 36.120197, 33.941722, -118.400517));
        System.out.println(gpsDistance2(-86.670048, 36.120197, 33.941722, -118.400517));
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("os.name"));
        Book b = new Book();
        b.setName("我的名字");
        toDBEncoding(b);
        System.out.println(b);
        System.out.println(getDomain("http://www.aaa.com"));
        System.out.println(getDomain("http://www.aaa.bbb.com"));
        System.out.println(getDomain("www.bbb.com"));
        System.out.println(getDomain("cc.bbb.com"));
        System.out.println(getDomain("bbb.com"));
        System.out.println(getDomain("http://bbb.com"));
        System.out.println(parseDate("09-10-13 13:57"));
        System.out.println(extractNumbers("上海市陆家嘴路七号八单元2  02"));
    }

    /**
     * Given a string contains some numbers/characters/chinese numbers, extract the number part.
     * e.g. 上海市陆家嘴路七号八单元202, will return 78202
     * @param s
     * @return
     */
    public static String extractNumbers(String s) {
        if (s == null)
            return "";
        StringBuffer buf = new StringBuffer();
        HashMap map = CHARMAP;
        for (int i = 0, len = s.length(); i < len; i++) {
            if (map.get(s.charAt(i)) != null) {
                buf.append(map.get(s.charAt(i)));
            }
        }
        return buf.toString();
    }
}

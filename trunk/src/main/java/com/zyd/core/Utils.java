package com.zyd.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.zyd.Constants;
import com.zyd.linkmanager.watchlist.GoogleFilm;

@SuppressWarnings("unchecked")
public class Utils {
    private static Logger logger = Logger.getLogger(Utils.class);
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

    public static String stringToFlatList(List<String> list) {
        StringBuffer buf = new StringBuffer();
        for (String s : list) {
            buf.append(s);
            buf.append("\r\n");
        }
        return buf.toString();
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

    public static String getNoRepeatString() {
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

    private static HashSet<String> TopDomains = new HashSet<String>();
    static {
        TopDomains.add("com");
        TopDomains.add("cn");
        TopDomains.add("org");
        TopDomains.add("info");
        TopDomains.add("net");
    }

    /**
     * Can not handle IP address
     * @param s
     * @return
     */
    public static String getDomain(String s) {
        if (s == null)
            return null;
        if (s.startsWith("http://")) {
            s = s.substring(7);
        } else if (s.startsWith("https://")) {
            s = s.substring(8);
        } else {
            return null;
        }

        int i = s.indexOf("/");
        if (i != -1) {
            s = s.substring(0, i);
        }
        i = s.indexOf(":");
        if (i != -1) {
            s = s.substring(0, i);
        }

        StringBuffer buf = new StringBuffer();
        String[] tokens = s.split("\\.");
        if (tokens == null || tokens.length < 2) {
            return null;
        }
        for (i = tokens.length - 1; i > -1; i--) {
            if (TopDomains.contains(tokens[i])) {
                buf.insert(0, tokens[i]);
                buf.insert(0, '.');
            } else {
                buf.insert(0, tokens[i]);
                buf.insert(0, '.');
                break;
            }
        }
        buf.deleteCharAt(0);
        return buf.toString();
    }

    /**
     * given a url, return the domain name, without the www part
     *  http://www.abc.com/anypage -> abc.com
     *  https://wwww.abc.com.cn/anypay -> abc.com.cn
     *  
     * if s doesn't start with http: or https: then it's not a valid url, will return null
     * @param s
     * @return
     */
    public static String getShortestDomain(String s) {
        if (s == null)
            return null;
        if (s.startsWith("http://")) {
            s = s.substring(7);
        } else if (s.startsWith("https://")) {
            s = s.substring(8);
        } else {
            return null;
        }

        int i = s.indexOf("/");
        if (i != -1) {
            s = s.substring(0, i);
        }
        if (s.startsWith("www.")) {
            s = s.substring(4);
        }
        i = s.indexOf('.');
        if (i == -1) {
            // must contain at least one dot
            return null;
        }
        return new String(s);
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
            throw new UnsupportedOperationException();
        } else {
            throw new UnsupportedOperationException("not supported class " + clazz);
        }
    }

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
            logger.warn("Invalid range string: " + s);
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
                logger.error("Invalid date format for string " + s1 + " or " + s2);
                logger.error(e);
            }
            return r;
        }

        return null;
    }

    public static int parseInt(String s, int defaultValue) {
        try {
            defaultValue = Integer.parseInt(s);
        } catch (Exception e) {
            logger.error("Invalid integer as a string : " + s);

        }
        return defaultValue;
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

    public final static String snapshotHttpRequest(HttpServletRequest req) {
        StringBuffer buf = new StringBuffer();
        buf.append("Request came from ip address :");
        buf.append(req.getRemoteAddr());
        buf.append(Constants.LINE_SEPARATOR);
        Enumeration<String> names = req.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            buf.append(name);
            buf.append('=');
            buf.append(req.getParameter(name));
            buf.append(Constants.LINE_SEPARATOR);
        }
        return buf.toString();
    }

    public static String xmlString(String s) {
        return s.replaceAll("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;");
        //        return s;
    }

    public static void main(String[] args) {

    }

    public static String[] stringArrayFromFile(String fileNameUnderClassPath) {
        InputStream ins = GoogleFilm.class.getClassLoader().getResourceAsStream(fileNameUnderClassPath);
        if (ins != null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(ins, Constants.Encoding_DEFAULT_SYSTEM));
                ArrayList<String> r = new ArrayList<String>();
                while (true) {
                    String s = reader.readLine();
                    if (s == null) {
                        break;
                    }
                    s = s.trim();
                    if (s.length() == 0) {
                        continue;
                    }
                    r.add(s);
                }
                return r.toArray(new String[r.size()]);
            } catch (UnsupportedEncodingException e) {
                logger.debug(e);
            } catch (IOException e) {
                logger.error(e);
            } finally {
                if (ins != null) {
                    try {
                        ins.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return new String[0];
    }
}

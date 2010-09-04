package tools;

import java.util.ArrayList;
import java.util.HashMap;

import com.zyd.core.db.HibernateUtil;

/**
 * bus line is normalized like this
 * 1. all numbers in the bus line is changed to digits
 * 2. first digits are taken out as seq1
 * 3. duplicates names are deleted
 * 4. for each seq1 that is the same, see how many are there 
 *     if there is 1, then set to 11
 *     if there is 2, set 1 and 2
 *     if there is 3, pick the two with the most chars the same
 *     if there is more than 3, for each one, pick the one that has most matched item
 * @author yang
 *
 */
@SuppressWarnings("unchecked")
public class BusNormalizer {
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

    public static void main(String[] args) {
        step1();
    }

    static int seq1counter = 100000;

    public static void step1() {
        ArrayList<HashMap<String, Object>> values = loadValue();
        for (HashMap<String, Object> v : values) {
            String name = (String) v.get("name");
            String nname = replaceFirstNumbers(name);
            v.put("name", nname);
            int n = extractFirstNumbers(nname);
            if (n != -1) {
                v.put("seq1", n);
            } else {
                v.put("seq1", seq1counter++);
            }
            HibernateUtil.updateObject(HibernateUtil.EntityNames.BusLine, v);
        }
    }

    public static ArrayList<HashMap<String, Object>> loadValue() {
        return (ArrayList<HashMap<String, Object>>) HibernateUtil.loadObject(HibernateUtil.EntityNames.BusLine);
    }

    public static String replaceFirstNumbers(String s) {
        if (s == null)
            return "";
        StringBuffer buf = new StringBuffer();
        HashMap map = CHARMAP;
        boolean hit = false;
        for (int i = 0, len = s.length(); i < len; i++) {
            if (map.get(s.charAt(i)) != null) {
                hit = true;
                buf.append(map.get(s.charAt(i)));
                continue;
            } else if (hit == true) {
                buf.append(s.substring(i));
                break;
            }
            buf.append(s.charAt(i));
        }
        return buf.toString();

    }

    public static int extractFirstNumbers(String s) {
        if (s == null)
            return -1;
        StringBuffer buf = new StringBuffer();
        HashMap map = CHARMAP;
        boolean hit = false;
        for (int i = 0, len = s.length(); i < len; i++) {
            if (map.get(s.charAt(i)) != null) {
                buf.append(map.get(s.charAt(i)));
                hit = true;
            } else if (hit == true) {
                break;
            }
        }
        if (buf.length() == 0)
            return -1;
        return Integer.parseInt(buf.toString());
    }

    public static boolean processEntry(HashMap<String, Object> values1, HashMap<String, Object> values2) {
        String name1 = values1.get("name").toString(), name2 = values1.get("name").toString();
        int num1 = extractFirstNumbers(name1), num2 = extractFirstNumbers(name2);
        if (num1 < 0 || num1 != num2)
            return false;

        return true;
    }

    public static float sameRatio(String a, String b) {
        return 0;
    }

    public static void ma1in(String[] args) {
        System.out.println(replaceFirstNumbers("w1一九1i九ew123dkds323"));
    }
}

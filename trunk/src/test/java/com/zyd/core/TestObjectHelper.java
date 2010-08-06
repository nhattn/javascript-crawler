package com.zyd.core;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.zyd.core.objecthandler.ObjectHelper;

import junit.framework.TestCase;

public class TestObjectHelper extends TestCase {
    public static Object[] list = new Object[] { new Object[] { "1-2", Types.INTEGER, 1, 2 }, new Object[] { "1-", Types.INTEGER, 1, null }, new Object[] { "-2", Types.INTEGER, null, 2 },
            new Object[] { "2", Types.INTEGER, 2 },

            new Object[] { "11.1-22.2", Types.FLOAT, 11.1f, 22.2f }, new Object[] { "11.1-", Types.FLOAT, 11.1f, null }, new Object[] { "-22.2", Types.FLOAT, null, 22.2f },
            new Object[] { "11.1", Types.FLOAT, 11.1f },

            new Object[] { "aa-zz", Types.VARCHAR, "aa", "zz" }, new Object[] { "-zz", Types.VARCHAR, null, "zz" }, new Object[] { "aa-", Types.VARCHAR, "aa", null },
            new Object[] { "aa", Types.VARCHAR, "aa" },

            new Object[] { "", Types.VARCHAR }, new Object[] { "--", Types.VARCHAR }, new Object[] { "-", Types.VARCHAR },

    };

    public static Object[] dates = new Object[] { new Object[] { "2000-01-01/2000-12-12", Types.DATE, getDate(2000, 1, 1), getDate(2000, 12, 12) },
            new Object[] { "/2000-12-12", Types.DATE, null, getDate(2000, 12, 12) }, new Object[] { "2000-01-01/", Types.DATE, getDate(2000, 1, 1), null },
            new Object[] { "2000-01-01", Types.DATE, getDate(2000, 1, 1) },

            new Object[] { "2000-01-01 11:11:11:000/2000-12-12 22:22:22:000", Types.TIMESTAMP, getDate(2000, 1, 1, 11, 11, 11), getDate(2000, 12, 12, 22, 22, 22) },
            new Object[] { "/2000-12-12 22:22:22:000", Types.TIMESTAMP, null, getDate(2000, 12, 12, 22, 22, 22) },
            new Object[] { "2000-01-01 11:11:11:000/", Types.TIMESTAMP, getDate(2000, 1, 1, 11, 11, 11), null },
            new Object[] { "2000-01-01 11:11:11:000", Types.TIMESTAMP, getDate(2000, 1, 1, 11, 11, 11) }, };

    static Date getDate(int... dates) {
        Calendar c = Calendar.getInstance();
        int[] fields = new int[] { Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND };
        for (int i = 0; i < dates.length; i++) {
            if (i == 1) {
                c.set(fields[i], dates[i] - 1);
            } else {
                c.set(fields[i], dates[i]);
            }
        }
        for (int i = dates.length; i < fields.length; i++) {
            c.set(fields[i], 0);
        }
        return c.getTime();
    }

    public void testNoneDate() {
        ArrayList<Object[]> objs = new ArrayList<Object[]>();
        for (int i = 0; i < list.length; i++) {
            objs.add((Object[]) list[i]);
        }

        for (int i = 0; i < objs.size(); i++) {
            Object[] os = objs.get(i);
            System.out.println(os[0]);
            Object[] result = ObjectHelper.parseRangeObject((String) os[0], (Integer) os[1], null);
            int len = os.length - 2;
            assertEquals(len, result.length);
            for (int j = 0; j < len; j++) {
                assertEquals(os[j + 2], result[j]);
            }
        }
    }

    public void testDate() {
        ArrayList<Object[]> objs = new ArrayList<Object[]>();
        for (int i = 0; i < dates.length; i++) {
            objs.add((Object[]) dates[i]);
        }

        for (int i = 0; i < objs.size(); i++) {
            Object[] os = objs.get(i);
            System.out.println(os[0]);
            Object[] result = ObjectHelper.parseRangeObject((String) os[0], (Integer) os[1], "/");
            int len = os.length - 2;
            assertEquals(len, result.length);
            for (int j = 0; j < len; j++) {
                assertEquals(os[j + 2], result[j]);
            }
        }
    }

    public void testStringSeparator() {
        Object[] result = ObjectHelper.parseRangeObject("121--200", Types.INTEGER, "--");
        assertEquals(result[0], new Integer(121));
        assertEquals(result[1], new Integer(200));
        result = ObjectHelper.parseRangeObject("121||200", Types.INTEGER, "||");
        assertEquals(result[0], new Integer(121));
        assertEquals(result[1], new Integer(200));
    }

    public static void main(String[] args) {
        System.out.println(getDate(2000, 1, 1));
    }
}

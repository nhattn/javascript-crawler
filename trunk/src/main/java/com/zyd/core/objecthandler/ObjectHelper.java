package com.zyd.core.objecthandler;

import java.sql.Connection;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.zyd.Constants;
import com.zyd.core.dom.DatabaseColumnInfo;
import com.zyd.core.objecthandler.Handler.Columns;
import com.zyd.core.objecthandler.Handler.Parameter;

@SuppressWarnings("unchecked")
public class ObjectHelper {
    private static Logger logger = Logger.getLogger(ObjectHelper.class);

    public static HashMap<String, DatabaseColumnInfo> getTableMetaData(String tableName, Connection connection) throws SQLException {
        ResultSetMetaData meta = connection.createStatement().executeQuery("select * from " + tableName + " where 0>1").getMetaData();
        HashMap<String, DatabaseColumnInfo> r = new HashMap<String, DatabaseColumnInfo>();
        int columnCount = meta.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            String name = meta.getColumnName(i + 1);
            r.put(name, new DatabaseColumnInfo(name, meta.getColumnType(i + 1), meta.getColumnDisplaySize(i + 1)));
        }
        return r;
    }

    public static void parseCommonQueryParameters(Criteria c, HashMap params) {
        String s = (String) params.get(Parameter.PARAMETER_START);
        if (s != null) {
            c.setFirstResult(Integer.parseInt(s));
        }
        s = (String) params.get(Parameter.PARAMETER_COUNT);
        if (s != null) {
            c.setMaxResults(Integer.parseInt(s));
        } else {
            c.setMaxResults(Constants.LENGTH_PAGE_SIZE);
        }
        String orderBy = (String) params.get(Parameter.PARAMETER_ORDER_BY);
        if (orderBy == null) {
            orderBy = Columns.ID;
        }
        String order = (String) params.get(Parameter.PARAMETER_ORDER);
        if (order != null) {
            order = order.toLowerCase();
        }
        if (Parameter.PARAMETER_VALUE_ORDER_ASC.equals(order)) {
            c.addOrder(Order.asc(orderBy));
        } else {
            c.addOrder(Order.desc(orderBy));
        }
    }

    public static void buildHibernateCriteria(Criteria c, HashMap<String, Object[]> params) {
        for (String k : params.keySet()) {
            Object[] p = params.get(k);
            if (p.length == 0) {
                continue;
            } else if (p.length == 1) {
                Object p0 = p[0];
                if (p0 instanceof String && p0.toString().indexOf("%") != -1) {
                    c.add(Restrictions.like(k, p0));
                } else {
                    c.add(Restrictions.eq(k, p[0]));
                }
            } else if (p.length == 2) {
                if (p[0] != null) {
                    c.add(Restrictions.ge(k, p[0]));
                }
                if (p[1] != null) {
                    c.add(Restrictions.le(k, p[1]));
                }
            }
        }
    }

    /**
     * Given a string, which could be a range object or a single object, like '100-1000' or '100' and the type,
     * return a object array converted to that type.
     *  
     * time/timestamp/date should be in this format:
     * 2009-12-01 13:59:59
     * 
     * @param s
     * @param type  java.Sql.Types
     * @param separator default "-"
     * @return and Object array. If the array contains only one element, then this is not a range, if it's two, and the first one is null, then it's an close end range. Or if the second one is
     * null, it's an close start range.
     * 
     */
    public static Object[] parseRangeObject(String s, int type, String separator) {
        if (separator == null) {
            separator = "-";
        }
        if (s.equals(separator)) {
            logger.debug("Wrong parameter value, should not be a single separator as parameter value");
            return Constants.ZERO_OBJECT_LIST;
        }
        if (s.length() == 0) {
            logger.debug("Wrong parameter value, blank string passed");
            return Constants.ZERO_OBJECT_LIST;
        }

        String[] values = s.split(separator);

        if (values.length > 2) {
            logger.debug("Wrong parameter value, should be contains one separator, value is " + s);
            return Constants.ZERO_OBJECT_LIST;
        }
        boolean valid = false;
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null && values[i].length() != 0) {
                valid = true;
                break;
            }
        }
        if (valid == false) {
            return Constants.ZERO_OBJECT_LIST;
        }
        int startIndex, arrayLength;
        if (s.startsWith(separator)) {
            startIndex = 1;
            arrayLength = 2;
        } else if (s.endsWith(separator)) {
            startIndex = 0;
            arrayLength = 2;
        } else if (s.indexOf(separator) == -1) {
            startIndex = 0;
            arrayLength = 1;
        } else {
            startIndex = 0;
            arrayLength = 2;
        }

        Object[] r = new Object[arrayLength];

        switch (type) {
        case Types.BIGINT:
            for (; startIndex < values.length; startIndex++) {
                r[startIndex] = Long.parseLong(values[startIndex]);
            }
            break;
        case Types.INTEGER:
            for (; startIndex < values.length; startIndex++) {
                r[startIndex] = Integer.parseInt(values[startIndex]);
            }
            break;
        case Types.CHAR:
        case Types.VARCHAR:
            for (; startIndex < values.length; startIndex++) {
                r[startIndex] = values[startIndex];
            }
            break;
        case Types.TIME:
            for (; startIndex < values.length; startIndex++) {
                try {
                    r[startIndex] = time.parse(values[startIndex]);
                } catch (ParseException e) {
                    logger.warn("Wrong Time format " + s);
                    r = Constants.ZERO_OBJECT_LIST;
                    break;
                }
            }
            break;
        case Types.TIMESTAMP:
            for (; startIndex < values.length; startIndex++) {
                try {
                    r[startIndex] = timestamp.parse(values[startIndex]);
                } catch (ParseException e) {
                    logger.warn("Wrong Timestamp format " + s);
                    r = Constants.ZERO_OBJECT_LIST;
                    break;
                }
            }
            break;
        case Types.DATE:
            for (; startIndex < values.length; startIndex++) {
                try {
                    r[startIndex] = date.parse(values[startIndex]);
                } catch (ParseException e) {
                    logger.warn("Wrong Date format " + s);
                    r = Constants.ZERO_OBJECT_LIST;
                    break;
                }
            }
            break;
        case Types.DOUBLE:
            for (; startIndex < values.length; startIndex++) {
                r[startIndex] = Double.parseDouble(values[startIndex]);
            }
            break;
        case Types.FLOAT:
            for (; startIndex < values.length; startIndex++) {
                r[startIndex] = Float.parseFloat(values[startIndex]);
            }
            break;
        default:
            throw new UnsupportedOperationException("Type is not supported:" + type);
        }
        return r;
    }

    private static DateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private static DateFormat time = new SimpleDateFormat("HH:mm:ss");
    private static DateFormat date = new SimpleDateFormat("yyyy-MM-dd");

}

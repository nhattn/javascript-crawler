package com.zyd.core.objecthandler;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.tj.common.CommonUtil;
import com.zyd.core.Utils;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.DatabaseColumnInfo;
import com.zyd.core.util.Ocr;

@SuppressWarnings("unchecked")
public class House extends Handler {
    public final static String name = "House";
    private static Logger logger = Logger.getLogger(House.class);
    private final static String[] requiredColumns = new String[] { Columns.Referer };
    private final static HashSet CDataColumns = new HashSet();
    private static HashMap<String, DatabaseColumnInfo> meta = null;

    static {
        meta = ObjectHelper.getTableMetaData(name);
    }

    public String getName() {
        return name;
    }

    public Object create(HashMap values) {
        if (normalizeValeus(values) == false) {
            return false;
        }
        return doSave(values);
    }

    private static boolean normalizeValeus(HashMap values) {
        String missing = checkColumnExistence(requiredColumns, values);
        if (missing != null) {
            logger.warn("Can not add House, missing required paramter - " + missing);
            return false;
        }
        String tel = (String) values.get(Columns.Tel);
        if (tel.length() > 100) {
            String type = CommonUtil.getFileSuffix((String) values.get(Columns.TelImageName));
            values.put(Columns.Tel, Ocr.ocrImageNumber(tel, type));
        }
        ObjectHelper.nomorlizedParameters(values, meta);

        if (values.get(Columns.Lat) != null && values.get(Columns.Long) != null) {
            values.put(Columns.OK, Parameter.PARAMETER_VALUE_OK_YES);
        } else {
            values.put(Columns.OK, Parameter.PARAMETER_VALUE_OK_NO);
        }
        Date now = new Date();
        values.put(Columns.CreateTime, now);
        values.put(Columns.UpdateTime, now);
        return true;
    }

    private static boolean doSave(HashMap values) {
        Session session = null;
        boolean r = false;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            long oid = isUniqueByHashValue(session, values);
            if (oid != -1) {
                logger.debug("House is duplicate, id:" + oid);
                return false;
            } else {
                session.save(name, values);
                r = true;
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null)
                session.getTransaction().rollback();
            logger.error(e);
            logger.debug("Values trying to save are:");
            logger.debug(values);
        }
        return r;
    }

    /**
     * trying to look for the house has the same url, if found, return the id of the entry,
     * if not, return -1
     * @param session
     * @param values
     * @return
     */
    private static long isUniqueByHashValue(Session session, HashMap values) {
        String url = (String) values.get(Columns.Referer);
        String hash = Integer.toString(url.hashCode());
        List houses = session.createQuery("from " + name + " as a where a." + Columns.Hash + " = ?").setString(0, hash).list();
        if (houses.size() != 0) {
            for (int len = houses.size(), i = 0; i < len; i++) {
                HashMap e = (HashMap) houses.get(i);
                if (url.equals(e.get(Columns.Referer))) {
                    return Long.parseLong(e.get(Columns.ID).toString());
                }
            }
        }
        values.put(Columns.Hash, hash);
        return -1;
    }

    public SearchResult query(HashMap params) {
        HashMap<String, Object[]> qparams = new HashMap<String, Object[]>();
        String separator = (String) params.get("separator");
        if (separator == null) {
            separator = "-";
        }
        for (Object o : params.keySet()) {
            String column = (String) o;
            DatabaseColumnInfo info = meta.get(column);
            if (info == null) {
                continue;
            }
            String p = (String) params.get(column);
            qparams.put(column, ObjectHelper.parseRangeObject(p, info.type, separator));
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria c = session.createCriteria(getName());
        ObjectHelper.buildHibernateCriteria(c, qparams);
        ObjectHelper.parseCommonQueryParameters(c, params);
        c.add(Restrictions.eq(Columns.OK, new Integer(1)));
        List list = c.list();
        session.getTransaction().commit();
        SearchResult result = new SearchResult(list, -1, params.get(Handler.Parameter.PARAMETER_START) == null ? 0 : Integer.parseInt((String) params.get(Handler.Parameter.PARAMETER_START)), list
                .size());
        result.cdataColumns = CDataColumns;
        return result;
    }

    @Override
    public int deleteAll() {
        final String deleteAll = "delete from " + getName();
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        int r = session.createQuery(deleteAll).executeUpdate();
        session.getTransaction().commit();
        return r;
    }

    private static boolean isSameAddress(String addr1, String addr2) {
        return Utils.extractNumbers(addr1).equals(Utils.extractNumbers(addr2));
    }

    private static boolean isUniqueByTelAddress(Session session, HashMap values) {
        String tel = (String) values.get(Columns.Tel);
        List houses = session.createQuery("from " + name + " as a where a." + Columns.Tel + " = ?").setString(0, tel).list();
        if (houses.size() == 0) {
            return true;
        }
        String addressNum = Utils.extractNumbers((String) values.get(Columns.Address));
        for (int i = 0, len = houses.size(); i < len; i++) {
            HashMap obj = (HashMap) houses.get(i);
            if (addressNum.equals(Utils.extractNumbers((String) obj.get(Columns.Address)))) {
                logger.warn("Address is not unique:" + obj.get(Columns.Address));
                return false;
            }
        }
        return true;
    }

    public final static class Columns extends Handler.Columns {
        public final static String RentalType = "rentalType";
        public final static String SubRentalType = "subRentalType";
        public final static String Price = "price";
        public final static String PaymentType = "paymentType";
        public final static String PriceUit = "priceUit";
        public final static String Size = "size";
        public final static String HouseType = "houseType";
        public final static String Address = "address";
        public final static String District1 = "district1";
        public final static String District3 = "district3";
        public final static String District5 = "district5";
        public final static String Tel = "tel";
        public final static String Contact = "contact";
        public final static String Photo = "photo";
        public final static String Description1 = "description1";
        public final static String Description2 = "description2";
        public final static String Floor = "floor";
        public final static String TotalFloor = "totalFloor";
        public final static String IsAgent = "isAgent";
        public final static String Equipment = "equipment";
        public final static String Decoration = "decoration";
        public final static String TelImageName = "telImageName";
        public final static String Hash = "hash";
    }
}

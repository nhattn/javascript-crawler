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
import com.zyd.linkmanager.Link;

@SuppressWarnings("unchecked")
public class House extends Handler {
    public final static String name = "House";
    private static Logger logger = Logger.getLogger(House.class);
    private final static HashSet CDataColumns = new HashSet();
    private static HashMap<String, DatabaseColumnInfo> tableMetaData = HibernateUtil.getTableMetaData(name);

    public String getName() {
        return name;
    }

    public Object create(HashMap values) {
        if (normalizeValeus(values) == false) {
            return false;
        }
        try {
            HibernateUtil.saveObject(name, values);
        } catch (Throwable e) {
            logger.error(e);
            logger.debug("Values trying to save are:");
            logger.debug(values);
            return false;
        }
        return true;
    }

    private static boolean normalizeValeus(HashMap values) {
        // ocr tel number
        String tel = (String) values.get(Columns.Tel);
        if (tel.length() > 100) {
            String type = CommonUtil.getFileSuffix((String) values.get(Columns.TelImageName));
            values.put(Columns.Tel, Ocr.ocrImageNumber(tel, type));
        }

        ObjectHelper.nomorlizedParameters(values, tableMetaData);

        if (values.get(Columns.Lat) != null && values.get(Columns.Long) != null) {
            values.put(Columns.OK, Parameter.PARAMETER_VALUE_OK_YES);
        } else {
            values.put(Columns.OK, Parameter.PARAMETER_VALUE_OK_NO);
        }
        Date now = new Date();
        values.put(Columns.CreateTime, now);
        values.put(Columns.UpdateTime, now);
        Link link = (Link) values.remove(Columns.Link);
        if (link != null)
            values.put(Columns.Link, link.getId());
        return true;
    }

    public SearchResult query(HashMap params) {
        HashMap<String, Object[]> qparams = new HashMap<String, Object[]>();
        String separator = (String) params.get("separator");
        if (separator == null) {
            separator = "-";
        }
        for (Object o : params.keySet()) {
            String column = (String) o;
            DatabaseColumnInfo info = tableMetaData.get(column);
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
        return HibernateUtil.deleteAllObject(getName());
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

    }
}

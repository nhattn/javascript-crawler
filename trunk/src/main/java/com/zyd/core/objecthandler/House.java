package com.zyd.core.objecthandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.zyd.core.Utils;
import com.zyd.core.db.HibernateUtil;

public class House extends Handler {

    public String getName() {
        return "House";
    }

    @SuppressWarnings("unchecked")
    public Object process(HashMap values) {
        String tel = (String) values.get(Columns.Tel);
        if (tel != null && tel.length() > 100) {
            values.put(Columns.Tel, Utils.ocrImageNumber(tel).trim());
        }
        Utils.castValues(values, Columns.Lat, Double.class);
        Utils.castValues(values, Columns.Long, Double.class);
        Utils.castValues(values, Columns.RentalType, Integer.class);
        Utils.castValues(values, Columns.IsAgent, Integer.class);
        Utils.castValues(values, Columns.CreateTime, Date.class);
        Utils.castValues(values, Columns.Price, Float.class);

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(getName(), values);
        session.getTransaction().commit();
        return true;
    }

    public List load(HashMap params) {
        Object[] defaults = new Object[] { null, null };

        Object[] los = defaults;
        String s = (String) params.get("lo");
        if (s != null && s.trim().length() != 0) {
            los = Utils.parseRangeObject(s, Double.class);
        }
        Object[] las = defaults;
        s = (String) params.get("la");
        if (s != null && s.trim().length() != 0) {
            las = Utils.parseRangeObject(s, Double.class);
        }
        List result = queryHouse((los[0] == null ? -1d : (Double) los[0]), (los[1] == null ? -1d : (Double) los[1]), (las[0] == null ? -1d : (Double) las[0]),
                (las[1] == null ? -1d : (Double) las[1]), -1d, -1d, null, null, -1, -1, null, null);
        return result;
    }

    private List queryHouse(double loFrom, double loTo, double laFrom, double laTo, double priceFrom, double priceTo, Date createTimeFrom, Date createTimeTo, int limitFrom, int limitTo,
            String orderBy, String orderDirection) {
        /**
         Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List result = session.createQuery("from " + getName()).list();
        session.getTransaction().commit();
        
         */
        List r = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria c = session.createCriteria(getName());
        if (loFrom != -1) {
            c.add(Restrictions.ge(Columns.Long, loFrom));
        }
        if (loTo != -1) {
            c.add(Restrictions.le(Columns.Long, loTo));
        }

        if (laFrom != -1) {
            c.add(Restrictions.ge(Columns.Lat, laFrom));
        }
        if (laTo != -1) {
            c.add(Restrictions.le(Columns.Lat, laTo));
        }

        r = c.list();
        session.getTransaction().commit();
        return r;
    }

    final static class Columns {
        public final static String Long = "lo";
        public final static String Lat = "la";
        public final static String RentalType = "rentalType";
        public final static String SubRentalType = "subRentalType";
        public final static String Price = "price";
        public final static String PaymentType = "paymentType";
        public final static String PriceUit = "priceUit";
        public final static String Size = "size";
        public final static String HouseType = "houseType";
        public final static String CreateTime = "createTime";
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

}

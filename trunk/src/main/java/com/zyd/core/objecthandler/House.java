package com.zyd.core.objecthandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.zyd.Config;
import com.zyd.core.Utils;
import com.zyd.core.db.HibernateUtil;

public class House extends Handler {

    public String getName() {
        return "House";
    }

    @SuppressWarnings("unchecked")
    public Object process(HashMap values) {
        String tel = (String) values.get(Columns.Tel);
        if (tel == null) {
            System.err.println("Can not add House, missing required paramter - " + Columns.Tel);
            return false;
        }
        if (values.containsKey(Columns.Long) == false) {
            System.err.println("Can not add House, missing required paramter - " + Columns.Long);
            return false;
        }
        if (values.containsKey(Columns.Address) == false) {
            System.err.println("Can not add House, missing required paramter - " + Columns.Address);
            return false;
        }
        if (values.containsKey(Columns.Lat) == false) {
            System.err.println("Can not add House, missing required paramter - " + Columns.Lat);
            return false;
        }
        if (tel.length() > 100) {
            values.put(Columns.Tel, Utils.ocrImageNumber(tel).trim());
        }
        Utils.castValues(values, Columns.Lat, Double.class);
        Utils.castValues(values, Columns.Long, Double.class);
        Utils.castValues(values, Columns.RentalType, Integer.class);
        Utils.castValues(values, Columns.IsAgent, Integer.class);
        Utils.castValues(values, Columns.CreateTime, Date.class);
        Utils.castValues(values, Columns.Price, Float.class);

        boolean r = false;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try {
            // make sure house is unique
            if (isUnique(session, values) == false) {
            } else {
                r = true;
                session.save(getName(), values);
            }
        } finally {
            session.getTransaction().commit();
        }
        return r;
    }

    private boolean isUnique(Session session, HashMap values) {
        String tel = (String) values.get(Columns.Tel);
        List houses = session.createQuery("from " + getName() + " as a where a." + Columns.Tel + " = ?").setString(0, tel).list();
        if (houses.size() == 0) {
            return true;
        }
        //        double nlo = (Double) values.get(Columns.Long);
        //        double nla = (Double) values.get(Columns.Lat);
        String addressNum = Utils.extractNumbers((String) values.get(Columns.Address));
        for (int i = 0, len = houses.size(); i < len; i++) {
            HashMap obj = (HashMap) houses.get(i);
            //            double lo = (Double) obj.get(Columns.Long);
            //            double la = (Double) obj.get(Columns.Lat);
            if (addressNum.equals(Utils.extractNumbers((String) obj.get(Columns.Address)))) {
                return false;
            }
        }
        return true;
    }

    public List load(HashMap params) {
        Object[] defaults = new Object[] { null, null };

        //  logitude         
        Object[] los = defaults;
        String s = (String) params.get(Columns.Long);
        if (s != null && s.trim().length() != 0) {
            los = Utils.parseRangeObject(s, Double.class);
        }

        // latitue
        Object[] las = defaults;
        s = (String) params.get(Columns.Lat);
        if (s != null && s.trim().length() != 0) {
            las = Utils.parseRangeObject(s, Double.class);
        }

        // start
        s = (String) params.get("start");
        int start = 0;
        if (s != null && s.trim().length() != 0) {
            start = Utils.parseInit(s, 0);
        }

        //count 
        int count = Config.LENGTH_PAGE_SIZE;
        s = (String) params.get("count");
        if (s != null && s.trim().length() != 0) {
            count = Utils.parseInit(s, 0);
        }

        // order  
        String orderBy = (String) params.get(Parameter.PARAMETER_ORDER_BY);
        if (orderBy == null || orderBy.trim().length() == 0) {
            orderBy = Columns.ID;
        }

        String order = (String) params.get(Parameter.PARAMETER_ORDER);
        if (order == null || order.trim().length() == 0) {
            order = Parameter.PARAMETER_VALUE_ORDER_DESC;
        }
        List result = queryHouse((los[0] == null ? -1d : (Double) los[0]), (los[1] == null ? -1d : (Double) los[1]), (las[0] == null ? -1d : (Double) las[0]),
                (las[1] == null ? -1d : (Double) las[1]), -1d, -1d, null, null, start, count, orderBy, order);
        return result;
    }

    private List queryHouse(double loFrom, double loTo, double laFrom, double laTo, double priceFrom, double priceTo, Date createTimeFrom, Date createTimeTo, int start, int length, String orderBy,
            String orderDirection) {
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

        if (start != 0) {
            c.setFirstResult(start);
        }
        c.setMaxResults(length);
        if (orderDirection.equals(Parameter.PARAMETER_VALUE_ORDER_ASC)) {
            c.addOrder(Order.asc(orderBy));
        } else {
            c.addOrder(Order.desc(orderBy));
        }
        r = c.list();
        session.getTransaction().commit();
        return r;
    }

    public final static class Columns extends Handler.Columns {
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

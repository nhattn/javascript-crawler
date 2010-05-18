package com.zyd.core.objecthandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.zyd.Constants;
import com.zyd.core.Utils;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.util.Ocr;

public class House extends Handler {

    private final static String[] requiredColumns = new String[] { Columns.Tel, Columns.Address };

    public String getName() {
        return "House";
    }

    @SuppressWarnings("unchecked")
    public Object create(HashMap values) {
        String missing = checkColumnExistence(requiredColumns, values);
        if (missing != null) {
            System.err.println("Can not add House, missing required paramter - " + missing);
            return false;
        }
        if (values.get(Columns.Lat) != null && values.get(Columns.Long) != null) {
            values.put(Columns.OK, Parameter.PARAMETER_VALUE_OK_YES);
        } else {
            values.put(Columns.OK, Parameter.PARAMETER_VALUE_OK_NO);
        }
        
        String tel = (String) values.get(Columns.Tel);
        if (tel.length() > 100) {
            values.put(Columns.Tel, Ocr.ocrImageNumber(tel));
        }
        Utils.castValues(values, Columns.Lat, Double.class);
        Utils.castValues(values, Columns.Long, Double.class);
        Utils.castValues(values, Columns.IsAgent, Integer.class);
        Utils.castValues(values, Columns.CreateTime, Date.class);
        Utils.castValues(values, Columns.Price, Double.class);

        boolean r = false;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        // make sure house is unique
        if (isUnique(session, values) == false) {
            System.err.println("House is not unique.");
        } else {
            r = true;
            session.save(getName(), values);
        }
        session.getTransaction().commit();
        // TODO: what to do here if there is an error, how to close transaction

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
            if (addressNum.equals(Utils.extractNumbers((String) obj.get(Columns.Address)))) {
                System.err.println("Address is not unique:" + obj.get(Columns.Address));
                return false;
            }
        }
        return true;
    }

    private final static Double[] nullDoubles = new Double[2];
    private final static Date[] nullDates = new Date[2];

    public SearchResult query(HashMap params) {
        //  logitude         
        Double[] los = nullDoubles;
        String s = (String) params.get(Columns.Long);
        if (s != null && s.trim().length() != 0) {
            los = (Double[]) Utils.parseRangeObject(s, Double.class);
        }

        // latitue
        Double[] las = nullDoubles;
        s = (String) params.get(Columns.Lat);
        if (s != null && s.trim().length() != 0) {
            las = (Double[]) Utils.parseRangeObject(s, Double.class);
        }

        //price 
        Double[] price = nullDoubles;
        s = (String) params.get(Columns.Price);
        if (s != null && s.trim().length() != 0) {
            price = (Double[]) Utils.parseRangeObject(s, Double.class);
        }

        // start
        s = (String) params.get("start");
        int start = 0;
        if (s != null && s.trim().length() != 0) {
            start = Utils.parseInit(s, 0);
        }

        //count 
        int count = Constants.LENGTH_PAGE_SIZE;
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
        SearchResult result = queryHouse((Double[]) los, (Double[]) las, (Double[]) price, start, count, orderBy, order);
        return result;
    }

    private SearchResult queryHouse(Double[] longitudes, Double[] latitudes, Double[] prices, int start, int length, String orderBy, String orderDirection) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria c = session.createCriteria(getName());

        if (longitudes[0] != null) {
            c.add(Restrictions.ge(Columns.Long, longitudes[0]));
        }
        if (longitudes[1] != null) {
            c.add(Restrictions.le(Columns.Long, longitudes[1]));
        }

        if (latitudes[0] != null) {
            c.add(Restrictions.ge(Columns.Lat, latitudes[0]));
        }

        if (latitudes[1] != null) {
            c.add(Restrictions.le(Columns.Lat, latitudes[1]));
        }

        if (prices[0] != null) {
            c.add(Restrictions.ge(Columns.Price, prices[0]));
        }

        if (prices[1] != null) {
            c.add(Restrictions.le(Columns.Price, prices[1]));
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

        c.add(Restrictions.eq(Columns.OK, new Integer(1)));

        /*
         Get total number of row
        Integer totalSize = ((Integer) c.setProjection(Projections.rowCount()).uniqueResult()).intValue();
        c.setProjection(null);
        c.setResultTransformer(Criteria.ROOT_ENTITY);
        */
        List list = c.list();
        session.getTransaction().commit();
        SearchResult result = new SearchResult(list, -1, start, list.size());
        return result;
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

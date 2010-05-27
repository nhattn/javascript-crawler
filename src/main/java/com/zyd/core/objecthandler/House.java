package com.zyd.core.objecthandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.tj.common.CommonUtil;
import com.zyd.Constants;
import com.zyd.core.Utils;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.util.Ocr;

@SuppressWarnings("unchecked")
public class House extends Handler {
    private static Logger logger = Logger.getLogger(House.class);
    private final static String[] requiredColumns = new String[] { Columns.Tel, Columns.Address };

    public String getName() {
        return "House";
    }

    /**
     * Create a new entry on House table.
     * When creating house, the following procedure is taken.
     * 1. check if tel and address is there, if not, not adding
     * 2. try to list all houses under the same tel, 
     *     2.a if there is nothing, add house
     *     2.b if there is already houses under the same tel, check to see if the digits in those house is the same
     *         2.b.1 if it's not the same, add
     *         2.b.2 if it's the same, try to find an house with url as the same, 
     *             2.b.2.a  if found,  then this is an update, update the update time, and all other valeus to the new one
     *             2.b.2.b  if not found, this is a duplicates in different site or same site, add the dup or id field of the other record as this record's dup
     *  NOT IMPLEMENTED.     
     */
    public Object create(HashMap values) {
        Session session = null;
        try {
            String missing = checkColumnExistence(requiredColumns, values);
            if (missing != null) {
                logger.warn("Can not add House, missing required paramter - " + missing);
                return false;
            }

            if (values.get(Columns.Lat) != null && values.get(Columns.Long) != null) {
                values.put(Columns.OK, Parameter.PARAMETER_VALUE_OK_YES);
            } else {
                values.put(Columns.OK, Parameter.PARAMETER_VALUE_OK_NO);
            }

            String tel = (String) values.get(Columns.Tel);
            if (tel.length() > 100) {
                String type = CommonUtil.getFileSuffix((String) values.get(Columns.TelImageName));
                values.put(Columns.Tel, Ocr.ocrImageNumber(tel, type));
            }
            Utils.castValues(values, Columns.Lat, Double.class);
            Utils.castValues(values, Columns.Long, Double.class);
            Utils.castValues(values, Columns.IsAgent, Integer.class);
            Utils.castValues(values, Columns.Price, Double.class);
            values.put(Columns.CreateTime, new Date());

            boolean r = false;
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            if (isUnique(session, values) == false) {
                logger.warn("House is not unique.");
            } else {
                r = true;
                session.save(getName(), values);
            }
            session.getTransaction().commit();
            return r;
        } catch (Exception e) {
            if (session != null)
                session.getTransaction().rollback();
            logger.error("Exception when saving object in handler.House.");
            logger.error(e);
            logger.debug("Values trying to save are:");
            logger.debug(values);
            logger.debug("Thread is " + Thread.currentThread().getName() + " - " + Thread.currentThread().getId());
            return false;
        }
    }

    private boolean isUnique(Session session, HashMap values) {
        String tel = (String) values.get(Columns.Tel);
        List houses = session.createQuery("from " + getName() + " as a where a." + Columns.Tel + " = ?").setString(0, tel).list();
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
        public final static String UpdateTime = "updateTime";
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
        public final static String Url = "url";
        public final static String Dup = "dup";
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

    private void saveOrUpdateHouse(Session session, HashMap values) {
        String tel = (String) values.get(Columns.Tel);
        List houses = session.createQuery("from " + getName() + " as a where a." + Columns.Tel + " = ?").setString(0, tel).list();
        if (houses.size() == 0) {
            // just add
        } else {
            String newAddress = (String) values.get(Columns.Address);
            for (int i = houses.size() - 1; i > -1; i--) {
                HashMap obj = (HashMap) houses.get(i);
                if (isSameAddress(newAddress, (String) obj.get(Columns.Address)) == false) {
                    houses.remove(i);
                }
            }
            if (houses.size() == 0) {
                // ok, no same address, just add                
            } else {
                String url = (String) values.get(Columns.Url);
                for (int i = houses.size() - 1; i > -1; i--) {
                    HashMap obj = (HashMap) houses.get(i);
                    if (url.equals(obj.get(Columns.Url)) == false) {
                        houses.remove(i);
                    }
                }
                if (houses.size() == 0) {
                    // just a duplicate but with different url
                } else {

                }
            }
        }

    }

    private static boolean isSameAddress(String addr1, String addr2) {
        return Utils.extractNumbers(addr1).equals(Utils.extractNumbers(addr2));
    }
}

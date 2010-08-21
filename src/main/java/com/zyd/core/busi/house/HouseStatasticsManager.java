package com.zyd.core.busi.house;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.zyd.core.db.HibernateUtil;
import com.zyd.core.util.SpringContext;

@SuppressWarnings("unchecked")
public class HouseStatasticsManager implements Job {
    private final static Logger logger = Logger.getLogger(HouseStatasticsManager.class);
    private static ArrayList<String> CityList = null;

    public static ArrayList<String> getCityList() {
        if (CityList != null)
            return CityList;
        CityList = loadCityList();
        return CityList;
    }

    /**
     * Caution: this takes long time. Goes through all cities, does a lot fo sum, select.
     * @return
     */
    public HashMap<String, HouseData> getLastDayHouseDataForAllCity() {
        ArrayList<String> cityList = getCityList();
        HashMap<String, HouseData> r = new HashMap<String, HouseData>();
        for (String city : cityList) {
            r.put(city, getLastDayHouseData(city));
        }
        return r;
    }

    /**
     * Write statistics to data base, should only be run once a day.
     * This will not detects duplicates. 
     * @return
     */
    private boolean updateDatabase() {
        HashMap<String, HouseData> data = getLastDayHouseDataForAllCity();
        Set<String> cities = data.keySet();
        HashMap values = new HashMap();
        Date yesterday = getLastDayDateRange()[0];

        for (String city : cities) {
            HouseData house = data.get(city);
            values.clear();
            values.put(Columns.City, city);
            values.put(Columns.AverageSalePrice, house.getAverageSaleUnitPrice());
            values.put(Columns.Date, yesterday);
            values.put(Columns.RentCount, house.totalRentalCount);
            values.put(Columns.SaleCount, house.totalSaleCount);
            values.put(Columns.TotalSalePrice, house.totalSalePrice);
            values.put(Columns.TotalSaleSize, house.totalSaleSize);
            try {
                HibernateUtil.saveObject(HibernateUtil.EntityNames.House_Data_Day, values);
            } catch (HibernateException e) {
                logger.warn("Can not save entry for city : " + city);
                logger.warn(house.toString());
                return false;
            }
        }

        return true;
    }

    private static ArrayList<String> loadCityList() {
        List list = HibernateUtil.loadObject(HibernateUtil.EntityNames.House_CityList);
        ArrayList<String> r = new ArrayList<String>(list.size());
        for (Object o : list) {
            Map map = (Map) o;
            r.add((String) map.get("city"));
        }
        return r;
    }

    public Map getCheapestSale() {
        return null;
    }

    public Map getCheapestRental() {
        return null;
    }

    public Map getLatestSale() {
        return null;
    }

    public Map getLatestRental() {
        return null;
    }

    private static Date[] getLastDayDateRange() {
        Date[] r = new Date[2];
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH) - 1;
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 1);
        r[0] = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        r[1] = cal.getTime();
        return r;
    }

    private final static String sql1 = "select count(id), sum(price), sum(size) from House house where"
            + " house.price is not null AND house.size is not null AND house.rentalType=:rentalType AND"
            + " house.city=:city AND house.updateTime between :timeStart and :timeEnd";

    private final static String sql2 = "select count(id) from House house where"
            + " house.price is not null AND house.size is not null AND house.rentalType <> :rentalType AND"
            + " house.city=:city and house.updateTime between :timeStart and :timeEnd";

    private HouseData getLastDayHouseData(String city) {
        HouseData data = new HouseData();
        Date[] dates = getLastDayDateRange();

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery(sql1);

        query.setParameter("rentalType", "出售");
        query.setParameter("city", city);
        query.setParameter("timeStart", dates[0]);
        query.setParameter("timeEnd", dates[1]);

        Object[] values = (Object[]) query.list().get(0);
        data.totalSaleCount = (int) ((Long) values[0]).longValue();
        data.totalSalePrice = (int) (((Double) values[1]).doubleValue());
        data.totalSaleSize = (int) (((Double) values[2]).doubleValue());

        query = session.createQuery(sql2);

        query.setParameter("rentalType", "出售");
        query.setParameter("city", city);
        query.setParameter("timeStart", dates[0]);
        query.setParameter("timeEnd", dates[1]);

        data.totalRentalCount = (int) ((Long) query.list().get(0)).longValue();
        session.getTransaction().commit();
        return data;
    }

    public final static class Columns {
        public final static String ID = "id";
        public final static String City = "city";
        public final static String Date = "date";
        public final static String SaleCount = "saleCount";
        public final static String RentCount = "rentCount";
        public final static String TotalSaleSize = "totalSaleSize";
        public final static String TotalSalePrice = "totalSalePrice";
        public final static String AverageSalePrice = "averageSalePrice";
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        HouseStatasticsManager man = ((HouseStatasticsManager) SpringContext.getContext().getBean("houseStatasticsManager"));
        man.updateDatabase();
        logger.info("Successfully wrote daily city data into database");
    }
}

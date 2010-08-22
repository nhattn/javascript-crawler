package com.zyd.core.busi.house;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.tj.common.DateUtil;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.util.SpringContext;

@SuppressWarnings("unchecked")
public class HouseStatasticsManager {
    private final static Logger logger = Logger.getLogger(HouseStatasticsManager.class);
    private static ArrayList<String> CityList = null;

    /**
     * Write statistics to data base, should only run once everyday on start of the day, to get
     * previous day's data.
     * 
     * This will not detects duplicates. Be careful not to run twice. 
     * @return
     */
    private boolean generateLastDayData() {
        ArrayList<String> cityList = getCityList();
        for (String city : cityList) {
            HouseData h = getLastDayHouseData(city);
            if (saveHouseData(h) == false) {
                return false;
            }
        }
        return true;
    }

    private boolean generateRangeData(Date[] dateRange) {
        ArrayList<String> cityList = getCityList();
        for (String city : cityList) {
            HouseData h = getRangeHouseData(city, dateRange[0], dateRange[1]);
            if (saveHouseData(h) == false) {
                return false;
            }
        }
        return true;
    }

    private boolean saveHouseData(HouseData house) {
        HashMap values = new HashMap();
        values.put(Columns.City, house.city);
        values.put(Columns.AverageSalePrice, house.getAverageSaleUnitPrice());
        values.put(Columns.Date, house.date);
        values.put(Columns.RentCount, house.totalRentalCount);
        values.put(Columns.SaleCount, house.totalSaleCount);
        values.put(Columns.TotalSalePrice, house.totalSalePrice);
        values.put(Columns.TotalSaleSize, house.totalSaleSize);
        values.put(Columns.Type, new Integer(house.type));
        try {
            HibernateUtil.saveObject(HibernateUtil.EntityNames.House_Data_Day, values);
        } catch (HibernateException e) {
            logger.warn("Can not save house data", e);
            logger.warn(house.toString());
            return false;
        }
        return true;
    }

    private final static String lastDaySql1 = "select count(id), sum(price), sum(size) from House house where"
            + " house.price is not null AND house.size is not null AND house.rentalType=:rentalType AND"
            + " house.city=:city AND house.updateTime between :timeStart and :timeEnd";

    private final static String lastDaySql2 = "select count(id) from House house where"
            + " house.price is not null AND house.size is not null AND house.rentalType <> :rentalType AND"
            + " house.city=:city and house.updateTime between :timeStart and :timeEnd";

    /**
     * Goes into house table, sum up price, count, size etc..
     * @param city
     * @return
     */
    private HouseData getLastDayHouseData(String city) {
        HouseData data = new HouseData();
        Date[] dates = DateUtil.getLastDayDateRange();

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery(lastDaySql1);

        query.setParameter("rentalType", "出售");
        query.setParameter("city", city);
        query.setParameter("timeStart", dates[0]);
        query.setParameter("timeEnd", dates[1]);

        Object[] values = (Object[]) query.list().get(0);
        if (checkNull(values) == false) {
            logger.warn("No daily housing data for city : " + city);
            session.getTransaction().commit();
            return null;
        }
        data.totalSaleCount = (int) ((Long) values[0]).longValue();
        data.totalSalePrice = (int) (((Double) values[1]).doubleValue());
        data.totalSaleSize = (int) (((Double) values[2]).doubleValue());

        query = session.createQuery(lastDaySql2);

        query.setParameter("rentalType", "出售");
        query.setParameter("city", city);
        query.setParameter("timeStart", dates[0]);
        query.setParameter("timeEnd", dates[1]);
        Object obj = query.list().get(0);
        if (obj == null) {
            logger.warn("No daily housing data for city : " + city);
            session.getTransaction().commit();
            return null;
        }
        data.totalRentalCount = (int) ((Long) obj).longValue();
        session.getTransaction().commit();
        data.city = city;
        data.type = HouseData.TYPE_BY_DAY;
        data.date = dates[0];
        return data;
    }

    private final static String rangeHouseDataSql = "select sum(saleCount), sum(totalSaleSize), sum(totalSalePrice), sum(rentCount)"
            + "from House_Data data where data.date between :startDate and :endDate and data.type=1 and data.city=:city";

    private static HouseData getRangeHouseData(String city, Date start, Date end) {
        HouseData data = new HouseData();
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery(rangeHouseDataSql);
        query.setParameter("city", city);
        query.setParameter("startDate", start);
        query.setParameter("endDate", end);

        Object[] values = (Object[]) query.list().get(0);
        if (checkNull(values) == false) {
            logger.warn("No range housing data for city : " + city + ", start date :" + start + ", end date : " + end);
            session.getTransaction().commit();
            return null;
        }
        data.totalSaleCount = (int) ((Long) values[0]).longValue();
        data.totalSalePrice = (int) (((Long) values[1]).doubleValue());
        data.totalSaleSize = (int) (((Long) values[2]).doubleValue());
        data.totalRentalCount = (int) ((Long) values[3]).longValue();
        session.getTransaction().commit();
        return data;
    }

    public static ArrayList<String> getCityList() {
        if (CityList != null)
            return CityList;
        CityList = loadCityList();
        return CityList;
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

    private static boolean checkNull(Object[] o) {
        for (int i = 0, len = o.length; i < len; i++) {
            if (o[i] == null)
                return false;
        }
        return true;
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
        public final static String Type = "type";
    }

    public static class DailyHouseJob implements Job {
        public void execute(JobExecutionContext context) throws JobExecutionException {
            HouseStatasticsManager man = ((HouseStatasticsManager) SpringContext.getContext().getBean("houseStatasticsManager"));
            man.generateLastDayData();
            logger.info("Successfully wrote daily city data into database");
        }
    }

    public static class WeeklyHouseJob implements Job {
        public void execute(JobExecutionContext context) throws JobExecutionException {
            HouseStatasticsManager man = ((HouseStatasticsManager) SpringContext.getContext().getBean("houseStatasticsManager"));
            man.generateRangeData(DateUtil.getLastWeekDateRange());
            logger.info("Successfully wrote weekly city data into database");
        }
    }

    public static class MonthlyHouseJob implements Job {
        public void execute(JobExecutionContext context) throws JobExecutionException {
            HouseStatasticsManager man = ((HouseStatasticsManager) SpringContext.getContext().getBean("houseStatasticsManager"));
            man.generateRangeData(DateUtil.getLastMonthDateRange());
            logger.info("Successfully wrote monthly city data into database");
        }
    }

    public static void main(String[] args) {
        //        new HouseStatasticsManager().generateDataByDay();
        Date[] date = DateUtil.getLastWeekDateRange();
        System.out.println(getRangeHouseData("北京", date[0], date[1]));
    }
}

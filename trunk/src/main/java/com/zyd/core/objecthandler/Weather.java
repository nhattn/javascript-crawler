package com.zyd.core.objecthandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zyd.core.db.HibernateUtil;

@SuppressWarnings("unchecked")
public class Weather extends Handler {
    private static Logger logger = Logger.getLogger(Weather.class);

    @Override
    protected boolean beforeCreate(HashMap values) {
        return false;
    }

    @Override
    public String getEntityName() {
        return HibernateUtil.EntityNames.Weather;
    }

    /**
     * The posted json object should look like this
     * 
     * {
     *    objectid: 'Weather',
     *    data:[array of weather entries]
     * }
     * 
     * weather entry :{
     *    locationId: 
     *    wdate: date of the forcast like 2010-03-4
     *    condationd: ...
     *    rest is like the data base field.... 
     * }
     *
     * when creating, the class will take out each of entries in the array and see if they matches the value in the db.
     * If the data is not there in the first place, it will be inserted, if it's there, and changed, will be updated. 
     */
    public Object create(JSONObject object) {
        boolean changed = false;
        try {
            if (object.has("data") == false) {
                return false;
            }

            JSONArray theaters = object.getJSONArray("data");
            for (int i = 0, len = theaters.length(); i < len; i++) {
                if (processEntry(theaters.getJSONObject(i)) == true) {
                    changed = true;
                }
            }
        } catch (JSONException e) {
            logger.debug(e);
        }
        // always true here or it won't advance to next link
        return changed;
    }

    private boolean processEntry(JSONObject jsonObject) throws JSONException {
        String date = jsonObject.getString(Columns.CastDate);
        String locationId = jsonObject.getString(Columns.LocationId);
        if (date == null || locationId == null) {
            logger.error("date or locationId is missing, can not create");
            return false;
        }
        HashMap values = loadWeatherEntry(date, locationId);
        String condition1 = null, condition2 = null, temp1 = null, temp2 = null;
        String wind1 = null, wind2 = null, strength1 = null, strength2 = null;
        String s = null;
        boolean shouldUpdate = false;
        if (values != null) {
            if (jsonObject.has(Columns.Condition0)) {
                if (jsonObject.getString(Columns.Condition0).equals(values.get(Columns.Condition0)) == false) {
                    shouldUpdate = true;
                    values.put(Columns.Condition0, jsonObject.getString(Columns.Condition0));
                }
            }
            if (jsonObject.has(Columns.Condition1)) {
                if (jsonObject.getString(Columns.Condition1).equals(values.get(Columns.Condition1)) == false) {
                    shouldUpdate = true;
                    values.put(Columns.Condition1, jsonObject.getString(Columns.Condition1));
                }
            }
            if (jsonObject.has(Columns.Wind0)) {
                if (jsonObject.getString(Columns.Wind0).equals(values.get(Columns.Wind0)) == false) {
                    shouldUpdate = true;
                    values.put(Columns.Wind0, jsonObject.getString(Columns.Wind0));
                }
            }
            if (jsonObject.has(Columns.Wind1)) {
                if (jsonObject.getString(Columns.Wind1).equals(values.get(Columns.Wind1)) == false) {
                    shouldUpdate = true;
                    values.put(Columns.Wind1, jsonObject.getString(Columns.Wind1));
                }
            }
            if (jsonObject.has(Columns.Strength0)) {
                if (jsonObject.getString(Columns.Strength0).equals(values.get(Columns.Strength0)) == false) {
                    shouldUpdate = true;
                    values.put(Columns.Strength0, jsonObject.getString(Columns.Strength0));
                }
            }
            if (jsonObject.has(Columns.Strength1)) {
                if (jsonObject.getString(Columns.Strength1).equals(values.get(Columns.Strength1)) == false) {
                    shouldUpdate = true;
                    values.put(Columns.Strength1, jsonObject.getString(Columns.Strength1));
                }
            }
            if (jsonObject.has(Columns.Temp0)) {
                if (new Integer(jsonObject.getInt(Columns.Temp0)).equals(values.get(Columns.Temp0)) == false) {
                    shouldUpdate = true;
                    values.put(Columns.Temp0, new Integer(jsonObject.getInt(Columns.Temp0)));
                }
            }
            if (jsonObject.has(Columns.Temp1)) {
                if (new Integer(jsonObject.getInt(Columns.Temp1)).equals(values.get(Columns.Temp1)) == false) {
                    shouldUpdate = true;
                    values.put(Columns.Temp1, new Integer(jsonObject.getInt(Columns.Temp1)));
                }
            }
            values.put(Columns.UpdateTime, new Date());

            if (shouldUpdate == true) {
                HibernateUtil.updateObject(getEntityName(), values);
            }
            return shouldUpdate;
        } else {
            values = new HashMap();
            if (jsonObject.has(Columns.Condition0)) {
                values.put(Columns.Condition0, jsonObject.get(Columns.Condition0));
            }
            if (jsonObject.has(Columns.Condition1)) {
                values.put(Columns.Condition1, jsonObject.get(Columns.Condition1));
            }
            if (jsonObject.has(Columns.Temp0)) {
                values.put(Columns.Temp0, ObjectHelper.parseInt(jsonObject.getString(Columns.Temp0), 0));
            }
            if (jsonObject.has(Columns.Temp1)) {
                values.put(Columns.Temp1, ObjectHelper.parseInt(jsonObject.getString(Columns.Temp1), 0));
            }
            if (jsonObject.has(Columns.Wind0)) {
                values.put(Columns.Wind0, jsonObject.get(Columns.Wind0));
            }
            if (jsonObject.has(Columns.Wind1)) {
                values.put(Columns.Wind1, jsonObject.get(Columns.Wind1));
            }
            if (jsonObject.has(Columns.Strength1)) {
                values.put(Columns.Strength1, jsonObject.get(Columns.Strength1));
            }
            if (jsonObject.has(Columns.Strength0)) {
                values.put(Columns.Strength0, jsonObject.get(Columns.Strength0));
            }
            values.put(Columns.CastDate, ObjectHelper.parseDate(date, null));
            values.put(Columns.LocationId, locationId);
            Date d = new Date();
            values.put(Columns.CreateTime, d);
            values.put(Columns.UpdateTime, d);
            HibernateUtil.saveObject(getEntityName(), values);
            return true;
        }

    }

    final static String queryWeather = "from Weather w where w.castDate=:castDate and w.locationId=:locationId";

    /**
     * returns a hashmap loaded from hibernate date already exist
     * @param date
     * @param locationId
     * @return
     */
    private HashMap loadWeatherEntry(String date, String locationId) {
        Date ddate = ObjectHelper.parseDate(date, null);
        if (ddate == null)
            return null;
        HashMap r = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction trx = session.beginTransaction();

        try {
            Query q = session.createQuery(queryWeather);
            q.setDate(Columns.CastDate, ddate);
            q.setString(Columns.LocationId, locationId);
            List list = q.list();

            if (list.size() != 0) {
                r = (HashMap) list.get(0);
            }
            trx.commit();
        } catch (HibernateException e) {
            logger.debug(e);
            if (trx != null)
                trx.rollback();
        }
        return r;
    }

    public static class Columns extends Handler.Columns {
        public final static String LocationId = "locationId";
        public final static String CastDate = "castDate";
        public final static String Condition0 = "condition0";
        public final static String Temp0 = "temp0";
        public final static String Wind0 = "wind0";
        public final static String Strength0 = "strength0";
        public final static String Condition1 = "condition1";
        public final static String Temp1 = "temp1";
        public final static String Wind1 = "wind1";
        public final static String Strength1 = "strength1";
    }
}

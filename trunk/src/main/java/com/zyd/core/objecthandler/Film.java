package com.zyd.core.objecthandler;

import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zyd.core.db.HibernateUtil;

public class Film extends Handler {
    Logger logger = Logger.getLogger(Film.class);
    public final static String name = "Film";

    @Override
    protected boolean beforeCreate(HashMap values) {
        return false;
    }

    @Override
    public String getEntityName() {
        return HibernateUtil.EntityNames.Film;
    }

    /**
     * The posted json object should look like this
     * 
     * {
     *    objectid: 'Film',
     *    data:[array of theater]
     * }
     * 
     * theater :{
     *    name: 'theater name',
     *    description :' theater description ',
     *    city: 'theater city',
     *    address :'theater address',
     *    films:[array of films] 
     * }
     * 
     * film: {
     *     name: 'name of film',
     *     description :'description of film',
     *     showTime: 'show time of film' 
     * }
     * 
     */
    @Override
    public Object create(JSONObject object) {
        boolean created = false;
        try {
            if (object.has("data") == false) {
                return false;
            }

            JSONArray theaters = object.getJSONArray("data");
            for (int i = 0, len = theaters.length(); i < len; i++) {
                if (processTheater(theaters.getJSONObject(i)) == true) {
                    created = true;
                }
            }
        } catch (JSONException e) {
            logger.debug(e);
        }
        // always true here or it won't advance to next link
        return created || true;
    }

    private boolean processTheater(JSONObject theater) throws JSONException {
        String theaterName = null, description = null, city = null, address = null;
        boolean created = false;
        if (theater.has(Columns.TheaterName))
            theaterName = theater.getString(Columns.TheaterName);
        if (theater.has(Columns.Description))
            description = theater.getString(Columns.Description);
        if (theater.has(Columns.City))
            city = theater.getString(Columns.City);
        if (theater.has("address"))
            address = theater.getString("address");

        if (theater.has("films")) {
            JSONArray films = theater.getJSONArray("films");
            for (int i = 0, len = films.length(); i < len; i++) {
                if (processMovie(theaterName, city, films.getJSONObject(i)) == true) {
                    created = true;
                }
            }
        }
        return created;
    }

    private boolean processMovie(String theaterName, String city, JSONObject film) throws JSONException {
        String entityName = getEntityName();
        String name = null, description = null, showTime = null, showDate = null;
        if (film.has(Columns.Name))
            name = film.getString(Columns.Name);
        if (film.has(Columns.Description))
            description = film.getString(Columns.Description);
        if (film.has(Columns.ShowTime))
            showTime = film.getString(Columns.ShowTime);
        if (film.has(Columns.ShowDate))
            showDate = film.getString(Columns.ShowDate);

        if (hasMovieAlready(theaterName, city, name, showDate) == true) {
            return false;
        }

        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put(Columns.TheaterName, theaterName);
        values.put(Columns.City, city);
        values.put(Columns.Name, name);
        values.put(Columns.Description, description);
        values.put(Columns.ShowTime, showTime);
        values.put(Columns.ShowDate, showDate);
        ObjectHelper.nomorlizedParameters(values, HibernateUtil.getTableMetaData(HibernateUtil.getTableName(entityName)));
        HibernateUtil.saveObject(entityName, values);
        Date now = new Date();
        values.put(Columns.CreateTime, now);
        values.put(Columns.UpdateTime, now);
        return true;
    }

    final static String queryMoive = "from Film  as f where f.theaterName = :theaterName and f.city = :city and f.name = :name and f.showDate=:showDate";

    private boolean hasMovieAlready(String theaterName, String city, String film, String date) {
        Date ddate = ObjectHelper.parseDate(date, null);
        if (ddate == null) {
            logger.debug("No date, should not happen, will not create");
            return true;
        }

        boolean r = true;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            Query q = session.createQuery(queryMoive);
            q.setString(Columns.TheaterName, theaterName);
            q.setString(Columns.City, city);
            q.setString(Columns.Name, film);
            q.setDate(Columns.ShowDate, ddate);
            if (q.list().size() == 0) {
                r = false;
            } else {
                r = true;
            }
            trx.commit();
        } catch (HibernateException e) {
            logger.debug(e);
            r = false;
            if (trx != null)
                trx.rollback();
        }
        return r;
    }

    public static class Columns extends Handler.Columns {
        public final static String TheaterName = "theaterName";
        public final static String City = "city";
        public final static String Name = "name";
        public final static String Description = "description";
        public final static String ShowTime = "showTime";
        public final static String ShowDate = "showDate";
    }
}

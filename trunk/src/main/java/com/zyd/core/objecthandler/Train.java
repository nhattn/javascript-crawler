package com.zyd.core.objecthandler;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.hibernate.classic.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zyd.core.db.HibernateUtil;

public class Train extends Handler {
    private static Logger logger = Logger.getLogger(Train.class);

    @Override
    protected boolean beforeCreate(HashMap values) {
        // check duplicates
        if (HibernateUtil.EntityNames.Train.equals(values.get(Handler.Parameter.PARAMETER_OBJECT_ID))) {
            String trainNum = values.get("trainNum").toString();
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            int size = session.createQuery("from " + HibernateUtil.EntityNames.Train + " t where t.trainNum=:trainNum").setString("trainNum", trainNum).list()
                    .size();
            session.getTransaction().commit();
            if (size > 0) {
                logger.debug("Duplicate train :" + trainNum);
                return false;
            }
        }
        return true;
    }

    @Override
    public String getEntityName() {
        return null;
    }

    /**
     * Json object is like this 
     * {
     *    objectid : "Object_Train",
     *    data: object contains train data like below
     * }
     * 
     * {
     *     name:..
     *     leaveAt:...
     *     stations:[array of train station object]
     * }
     */
    public Object create(JSONObject object) {
        try {
            if (object.has("data") == false) {
                System.err.println("error: no data, not going to process");
                return true;
            }

            JSONObject train = object.getJSONObject("data");
            JSONArray lines = train.getJSONArray("lines");
            HashMap values = new HashMap();
            ObjectHelper.jsonObjectToHashMap(values, train);

            values.put(Handler.Parameter.PARAMETER_OBJECT_ID, HibernateUtil.EntityNames.Train);
            if (Boolean.FALSE.equals(super.create(values)))
                return false;
            Long trainId = (Long) values.get(Handler.Columns.ID);
            for (int i = 0, len = lines.length(); i < len; i++) {
                values.clear();
                ObjectHelper.jsonObjectToHashMap(values, lines.getJSONObject(i));
                values.put(Handler.Parameter.PARAMETER_OBJECT_ID, HibernateUtil.EntityNames.TrainStation);
                values.put("trainId", trainId);
                if (Boolean.FALSE.equals(super.create(values))) {
                    logger.debug("Failed to create line info ");
                }
            }
        } catch (JSONException e) {
            logger.debug(e);
        }
        return true;
    }

}

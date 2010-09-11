package com.zyd.web.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONException;
import org.json.JSONObject;

import com.zyd.core.db.HibernateUtil;
import com.zyd.web.ServiceBase;

/**
 * this class serves as a client side goecoding server, 
 * it gives a list of addresses, then client do geocoding for that address, and return the geocde back. 
 */
public class clientgeocoding extends ServiceBase {
    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setResponseType(ResponseType_Js, resp);
        JSONObject obj = new JSONObject();
        try {
            String s = nextAddress();
            if (s != null)
                obj.put("address", s);
            else {
                obj.put("address", "false");
            }
        } catch (JSONException e) {
        }
    }

    @Override
    public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String addr = req.getParameter("address");
        String lng = req.getParameter("lng");
        String lat = req.getParameter("lat");
        String ok = req.getParameter("ok");
        if ("false".equals(ok)) {
            System.out.println(addr + " failed");
            return;
        }
        updateAddress(addr, Double.parseDouble(lng), Double.parseDouble(lat));
        output(RESULT_CHANGE, resp);
    }

    private void updateAddress(String address, double lng, double lat) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            Query query = session.createQuery("update " + HibernateUtil.EntityNames.TrainStation + " set lng=:lng, lat=:lat where name=:name");
            query.setDouble("lng", lng);
            query.setDouble("lat", lat);
            query.setString("name", address);
            query.executeUpdate();
            trx.commit();
        } catch (HibernateException ex) {
            ex.printStackTrace();
            trx.rollback();
        }
    }

    private String nextAddress() {
        // 1. selecte next address that lng is null        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List list = session.createQuery("from " + HibernateUtil.EntityNames.TrainStation + " t where t.lng is null").setFetchSize(1).list();
        String name = null;
        if (list.size() == 0) {
            System.out.println("No geocoding to translate, finished");
            System.exit(0);
        } else {
            Map obj = (Map) list.get(0);
            name = (String) obj.get("name");
            session.createQuery("update " + HibernateUtil.EntityNames.TrainStation + " t set lng=:lng where t.name=:name").setDouble("lng", 1000d).setString(
                    "name", name).executeUpdate();
        }
        session.getTransaction().commit();
        return name;
    }
}

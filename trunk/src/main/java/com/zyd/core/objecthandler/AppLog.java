package com.zyd.core.objecthandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.zyd.core.db.HibernateUtil;

@SuppressWarnings("unchecked")
public class AppLog extends Handler {

    public final static String name = "AppLog";

    public String getName() {
        return name;
    }

    @Override
    public Object create(HashMap values) {
        Session session = null;
        Transaction tx = null;
        try {
            values.put(Columns.CreateTime, new Date());
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            tx = session.beginTransaction();
            session.save(getName(), values);
            tx.commit();
            return true;
        } catch (Exception e) {
            System.err.println("Exception when saving object in handler.AppLogHandler: " + e.toString());
            System.err.println("Values trying to save are:");
            System.err.println(values);
            System.err.println("Thread is " + Thread.currentThread().getName() + " - " + Thread.currentThread().getId());
            if (session != null)
                session.getTransaction().rollback();
            return false;
        }
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

    @Override
    public SearchResult query(HashMap params) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria c = session.createCriteria(getName());
        int start = 0;
        String s = (String) params.get(Parameter.PARAMETER_START);

        if (s != null) {
            start = Integer.parseInt(s.trim());
        }
        c.setFirstResult(start);
        s = (String) params.get(Parameter.PARAMETER_COUNT);
        if (s != null) {
            c.setMaxResults(Integer.parseInt(s.trim()));
        } else {
            c.setMaxResults(20);
        }
        String orderBy = (String) params.get(Parameter.PARAMETER_ORDER_BY);
        if (orderBy == null) {
            orderBy = Columns.ID;
        }
        String orderDirection = (String) params.get(Parameter.PARAMETER_ORDER);
        if (orderDirection == null) {
            orderDirection = Parameter.PARAMETER_VALUE_ORDER_DESC;
        }

        if (orderDirection.equals(Parameter.PARAMETER_VALUE_ORDER_ASC)) {
            c.addOrder(Order.asc(orderBy));
        } else {
            c.addOrder(Order.desc(orderBy));
        }

        List list = c.list();
        session.getTransaction().commit();
        SearchResult result = new SearchResult(list, -1, start, list.size());
        return result;
    }

    public static class Columns extends Handler.Columns {
        public final static String ClientId = "clientId";
        public final static String App = "app";
        public final static String Action = "action";
        public final static String CreateTime = "createTime";
        public final static String Ip = "ip";
    }
}

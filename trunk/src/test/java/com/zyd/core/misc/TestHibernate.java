package com.zyd.core.misc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import junit.framework.TestCase;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;

import com.zyd.core.db.HibernateUtil;

public class TestHibernate extends TestCase {
    public void nottestSyncAccess() throws Exception {
        for (int i = 0; i < 1; i++) {
            new Thread() {
                public void run() {
                    try {
                        doit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
            }.start();
        }

        Thread.sleep(1000000);
    }

    public static void doit() throws Exception {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                Statement stmt = connection.createStatement();
                ResultSet r = stmt.executeQuery("select * from Object_House");
                r.setFetchSize(5);
                while (r.next()) {
                    System.out.println(r.getString("price"));
                }
            }
        });
        session.getTransaction().commit();
    }

    // sale
    public void testAggregateFunction() throws Exception {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session
                .createQuery("select count(id), sum(price), sum(size) from House house where house.id < 200000000011000 AND house.price is not null AND house.size is not null AND rentalType=:rentalType AND city=:city");
        query.setParameter("rentalType", "出售");
        query.setParameter("city", "济南");
        List r = query.list();
        session.getTransaction().commit();
        for (Object o : r) {
            Object[] oa = (Object[]) o;
            for (Object x : oa) {
                System.out.println(x);
            }
        }
    }

    // rental
    public void testAggregateFunction2() throws Exception {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session
                .createQuery("select count(id) from House house where house.id < 200000000011000 AND house.price is not null AND house.size is not null AND rentalType is not :rentalType AND city=:city");
        query.setParameter("rentalType", "出售");
        query.setParameter("city", "济南");
        List r = query.list();
        session.getTransaction().commit();
        for (Object o : r) {
            System.out.println("---------");
           System.out.println(o);
        }
    }

}

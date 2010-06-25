package com.zyd.core.db.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Example;

import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.bus.Bus;
import com.zyd.core.dom.bus.BusLine;
import com.zyd.core.dom.bus.BusStop;

@SuppressWarnings("unchecked")
public class BusHelper {
    private static Logger logger = Logger.getLogger(Bus.class);

    public static Bus getBusByName(Bus bus) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List list = session.createCriteria(Bus.class).add(Example.create(bus)).list();
        session.getTransaction().commit();
        if (list.size() == 0) {
            return null;
        } else if (list.size() > 1) {
            logger.warn("has more than one results returned from querying bus:" + bus.toString());
        }
        return (Bus) list.get(0);
    }

    public static Bus addBus(Bus bus) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        bus.id = (Long) session.save(bus);
        session.getTransaction().commit();
        return bus;
    }

    public static BusStop getStopByName(BusStop busStop) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        //NOTE:TODO: bug here, busstop has a default id lo, la of 0
        List list = session.createCriteria(BusStop.class).add(Example.create(busStop)).list();
        session.getTransaction().commit();
        if (list.size() == 0) {
            return null;
        } else if (list.size() > 1) {
            logger.warn("has more than one results returned from querying bus stop:" + busStop.toString());
        }
        return (BusStop) list.get(0);
    }

    public static BusStop addBusStop(BusStop stop) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        stop.id = (Long) session.save(stop);
        session.getTransaction().commit();
        return stop;
    }

    public static void addBusLine(ArrayList<BusLine> stop) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        for (int i = 0; i < stop.size(); i++) {
            session.save(stop.get(i));
        }
        session.getTransaction().commit();
    }

    public static ArrayList<BusStop> getBusLine(long busId) {
        return null;
    }

    public static int deleteAll() {
        int total = 0;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        total += session.createQuery("delete from Bus").executeUpdate();
        total += session.createQuery("delete from BusLine").executeUpdate();
        total += session.createQuery("delete from BusStop").executeUpdate();
        session.getTransaction().commit();
        return total;
    }
}

package com.zyd.core.objecthandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.zyd.core.db.helper.BusHelper;
import com.zyd.core.dom.bus.BusLine;
import com.zyd.core.dom.bus.BusStop;

public class Bus extends Handler {
    private static Logger logger = Logger.getLogger(Bus.class);

    /**
     * values contains:
     * name, city, description,
     * then an string object call stops
     * in the format like name|longitude|latitude,name|longitude|latitude
     */
    @Override
    public Object create(HashMap values) {
        com.zyd.core.dom.bus.Bus bus = new com.zyd.core.dom.bus.Bus();
        bus.name = (String) values.get(Columns.Name);
        bus.city = (String) values.get(Columns.City);
        bus.url = (String) values.get(Columns.Referer);
        com.zyd.core.dom.bus.Bus oldBus = BusHelper.getBusByName(bus);
        try {
            if (oldBus == null) {
                bus.description = (String) values.get(Columns.Description);
                bus.updateTime = new Date();
                addNewBus(values, bus);
            } else {
                //                updateBusIfNecessary(values);
                return false;
            }
        } catch (Exception e) {
            logger.error("Can not create bus", e);
            return false;
        }
        return true;
    }

    public boolean addNewBus(HashMap values, com.zyd.core.dom.bus.Bus bus) throws Exception {
        String s = (String) values.get(Columns.Stops);
        if (s == null) {
            logger.equals("Did not pass stop info, can not create bus");
            return false;
        }

        bus = BusHelper.addBus(bus);
        if (bus == null) {
            logger.error("Can not add bus");
            return false;
        }

        ArrayList<BusLine> busLine = new ArrayList<BusLine>();
        StringTokenizer stops = new StringTokenizer(s, ",");
        String city = bus.city;
        int i = 0;

        while (stops.hasMoreElements()) {
            String s1 = stops.nextToken();
            StringTokenizer stop = new StringTokenizer(s1, "|");
            String stopName = stop.nextToken();
            BusStop busStop = new BusStop();
            busStop.name = stopName;
            busStop.city = city;
            //TODO: bus stop should be reused.
            if (stop.hasMoreElements())
                busStop.lo = Double.parseDouble(stop.nextToken());
            if (stop.hasMoreElements())
                busStop.la = Double.parseDouble(stop.nextToken());

            busStop.updateTime = new Date();
            busStop = BusHelper.addBusStop(busStop);
            if (busStop == null) {
                logger.error("Can not add bus stop");
                return false;
            }
            BusLine lineInfoItem = new BusLine();
            lineInfoItem.busId = bus.id;
            lineInfoItem.seq = i++;
            lineInfoItem.stopId = busStop.id;
            lineInfoItem.updateTime = new Date();
            busLine.add(lineInfoItem);
        }
        BusHelper.addBusLine(busLine);
        return true;
    }

    public void updateBusIfNecessary(HashMap values) {

    }

    @Override
    public int deleteAll() {
        return BusHelper.deleteAll();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public SearchResult query(HashMap params) {
        // TODO Auto-generated method stub
        return null;
    }

    public final static class Columns extends Handler.Columns {
        public final static String Name = "name";
        public final static String City = "city";
        public final static String Description = "description";
        public final static String UpdateTime = "updateTime";
        public final static String BusList = "busList";
        public final static String Seq = "seq";
        public final static String BusId = "busId";
        public final static String StopId = "stopId";
        public final static String Stops = "stops";
    }

}

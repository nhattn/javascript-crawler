package com.zyd.linkmanager.watchlist;

import org.apache.log4j.Logger;

import com.zyd.core.Utils;

public class Weathercn extends InjectableWatchlist {
    public static Logger logger = Logger.getLogger(Weathercn.class);
    public final static String CronDef = "0 0 6,12,18 * * ?";
    public final static int InjectInterval = 5;
    private final static String[] Cities = Utils.stringArrayFromFile("watchlist/weathercity.list");
    private final static String url1 = "http://www.weather.com.cn/html/weather/";
    private final static String url2 = ".shtml";

    private StringBuffer buf = new StringBuffer();
    private int index = 0;

    @Override
    public int getInjectInveral() {
        return InjectInterval;
    }

    @Override
    public String nextLink() {
        if (index == Cities.length) {
            return null;
        }
        buf.delete(0, buf.length());
        buf.append(url1);
        buf.append(Cities[index++]);
        buf.append(url2);
        return buf.toString();
    }

    @Override
    public String getInfo() {
        return "CurrentCityIndex=" + index;
    }

}

package com.zyd.linkmanager.watchlist;

import org.apache.log4j.Logger;

import com.zyd.core.Utils;

public class Weathercn extends InjectableWatchlist {
    public static Logger logger = Logger.getLogger(Weathercn.class);
    public final static String CronDef = "0 0 8 * * ?";
    private final static String[] Cities = Utils.stringArrayFromFile("watchlist/weathercity.list");
    private StringBuffer buf = new StringBuffer();
    private int index = 0;

    @Override
    public int getInjectInveral() {
        return 3;
    }

    @Override
    public String nextLink() {
        if (index == Cities.length) {
            return null;
        }
        buf.delete(0, buf.length());
        buf.append("http://www.weather.com.cn/html/weather/");
        buf.append(Cities[index++]);
        buf.append(".shtml");
        return buf.toString();
    }

    @Override
    public String getInfo() {
        return this.getClass().getCanonicalName() + ", Total=" + Cities.length + ", CurrentCityIndex=" + index + ", Started " + startTime.toString();
    }
}

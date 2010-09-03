package com.zyd.linkmanager.watchlist;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import com.zyd.Constants;
import com.zyd.core.Utils;

public class GoogleFilm extends InjectableWatchlist {
    public static Logger logger = Logger.getLogger(GoogleFilm.class);
    public final static String CronDef = "0 0 6,12,18 * * ?";
    public final static int InjectInterval = 5;
    private final static String[] Cities = Utils.stringArrayFromFile("watchlist/googlefilm.list");
    private final static String url1 = "http://www.google.com.hk/movies?near=";
    private final static String url2 = "&hl=zh-CN&date=";
    private final static int DayRange = 2;

    private int cityIndex = 0;
    private int day = 0;
    private StringBuffer buf = new StringBuffer();

    @Override
    public int getInjectInveral() {
        return InjectInterval;
    }

    @Override
    public String nextLink() {
        if (day == DayRange) {
            cityIndex++;
            day = 0;
        }
        if (cityIndex == Cities.length) {
            return null;
        }
        String url = getUrl(Cities[cityIndex], day);
        day++;
        return url;
    }

    private String getUrl(String city, int day) {
        buf.append(url1);
        try {
            buf.append(URLEncoder.encode(city, Constants.Encoding_DEFAULT_SYSTEM));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        buf.append(url2);
        buf.append(day);
        String r = buf.toString();
        buf.delete(0, buf.length());
        return r;
    }

    @Override
    public String getInfo() {
        return "CurrentCityIndex=" + cityIndex;
    }

}

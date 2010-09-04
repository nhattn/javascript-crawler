package com.zyd.linkmanager.watchlist;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import com.zyd.Constants;
import com.zyd.core.Utils;

public class GoogleFilm extends InjectableWatchlist {
    public static Logger logger = Logger.getLogger(GoogleFilm.class);
    public final static String CronDef = "0 0 6,12,18 * * ?";
    private final static String[] Cities = Utils.stringArrayFromFile("watchlist/googlefilm.list");
    private StringBuffer buf = new StringBuffer();
    private int index = 0;

    @Override
    public int getInjectInveral() {
        return 10;
    }

    @Override
    public String nextLink() {
        if (index == Cities.length)
            return null;
        buf.append("http://www.google.com.hk/movies?near=");
        try {
            buf.append(URLEncoder.encode(Cities[index++], Constants.Encoding_DEFAULT_SYSTEM));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        buf.append("&hl=zh-CN&date=0");
        String r = buf.toString();
        buf.delete(0, buf.length());
        return r;
    }

    @Override
    public String getInfo() {
        return this.getClass().getCanonicalName() + ", Total=" + Cities.length + ", CurrentCityIndex=" + index + ", Started " + startTime.toString();
    }

}

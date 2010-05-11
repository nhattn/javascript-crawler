package com.zyd;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

import com.tj.common.CommonUtil;

public class Constants {
    public static String SERVER_DOMAIN;
    public static String APPLICATION_CONTEXT;
    public static String ENCODING_DB;

    /**
     * how many records to return by default
     */
    public static int LENGTH_PAGE_SIZE;

    /**
     * the encoding all outgoing content will be using, including xml/js/html 
     */
    public static String ENCODING_OUT_GOING_CONTENT;

    /**
     * if incoming request doesn't specify an encoding.
     */
    public static String ENCODING_INCOMING_CONTENT;

    /**
     * if two gps point differs more than this number, in meters, it will be treated as two location.
     */
    public static int THRESHOLD_GPS_LOCATION_DIFF;

    /**
     * These fields are fixed, derived from system
     */
    public static String Encoding_DEFAULT_SYSTEM = Charset.defaultCharset().toString();
    public static SimpleDateFormat DATEFORMAT_DEFAULT = new SimpleDateFormat("yyyy-MM-dd");

    /***
     * These fields are derived, don't put any values
     */
    public static String ServerUrl;
    public static String TemplatePath = "E:\\workspace\\webcrawl\\src\\main\\webapp\\temp";
    public static String IdlePageUrl;

    public static void loadValues() {
        System.err.println("Loading server configuration.....");
        boolean r = CommonUtil.loadStaticPropertyFromFile(Constants.class, Constants.class.getClassLoader().getResourceAsStream("config.prop"));
        if (r == false) {
            System.err.println("Can not load configuration file from config.prop under classpath");
            return;
        }
        initValues();
        System.err.println(snapShotValues());
    }

    public static void initValues() {
        ServerUrl = "http://" + SERVER_DOMAIN + APPLICATION_CONTEXT;
        IdlePageUrl = ServerUrl + "/html/wait.html";
    }

    private static String snapShotValues() {
        try {
            ByteArrayOutputStream bou = new ByteArrayOutputStream();
            BufferedWriter writer = new BufferedWriter(new PrintWriter(bou));
            writer.write("SERVER_DOMAIN : " + SERVER_DOMAIN);
            writer.newLine();

            writer.write("APPLICATION_CONTEXT : " + APPLICATION_CONTEXT);
            writer.newLine();

            writer.write("ServerUrl : " + ServerUrl);
            writer.newLine();

            writer.write("IdlePageUrl : " + IdlePageUrl);
            writer.newLine();

            writer.write("ENCODING_DB : " + SERVER_DOMAIN);
            writer.newLine();

            writer.write("ENCODING_OUT_GOING_CONTENT : " + ENCODING_OUT_GOING_CONTENT);
            writer.newLine();

            writer.write("LENGTH_PAGE_SIZE : " + LENGTH_PAGE_SIZE);
            writer.newLine();

            writer.write("THRESHOLD_GPS_LOCATION_DIFF : " + THRESHOLD_GPS_LOCATION_DIFF);
            writer.newLine();

            writer.close();
            return bou.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static {
        loadValues();
    }

}
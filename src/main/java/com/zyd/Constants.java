package com.zyd;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.tj.common.CommonUtil;
import com.tj.common.OSHelper;
import com.zyd.core.dom.Link;

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

    public static String LINUX_OCR_DIR;

    /**
     * when starting, how long ago should system load the links.
     */
    public static int LINK_LOAD_BEFORE;

    /**
     * How many time should try, before giving up a link.
     */
    public static int LINK_MAX_TRY;

    /**
     * if a link is processed for too long, it will be treated as an error, this defined how long this will happen, in seconds
     */
    public static int LINK_PROCESSING_EXPIRE;

    /**
     * how long will link manager sleep for each cycle
     */
    public static int LINK_MONITOR_SLEEP;

    /**
     * These fields are fixed, derived from system
     */
    public static String Encoding_DEFAULT_SYSTEM = Charset.defaultCharset().toString();
    public static SimpleDateFormat DATEFORMAT_DEFAULT = new SimpleDateFormat("yyyy-MM-dd");
    public static String FILENAME_LINK_WATCH_LIST = "watch.list";
    public static Link[] WATCH_LIST = new Link[0];
    /**
     * how soon to check if links should be checked and flushed out of memomry.
     */
    public static int LINK_FLUSH_CYCLE_LENGTH;

    /***
     * These fields are derived, don't put any values
     */

    // the full server url starting with http://www.domaon.com:port/context
    public static String ServerUrl;
    public static String IdlePageUrl;

    static {
        loadValues();
    }

    public static void loadValues() {
        String configFileName = null;
        if (OSHelper.isLinux() == true) {
            configFileName = "config.prop.linux";
        } else if (OSHelper.isWindows() == true) {
            configFileName = "config.prop.windows";
        } else {
            configFileName = "config.prop";
        }

        InputStream ins = Constants.class.getClassLoader().getResourceAsStream(configFileName);
        if (ins == null) {
            ins = Constants.class.getClassLoader().getResourceAsStream("config.prop");
            if (ins == null) {
                System.err.println("Can not load configuratoin file. System is not properly configured");
                return;
            }
        }
        System.err.println("Loading server configuration from " + configFileName);
        loadValueFromStream(ins);

    }

    public static void loadValueFromStream(InputStream ins) {
        boolean r = CommonUtil.loadStaticPropertyFromFile(Constants.class, ins);
        if (r == false) {
            System.err.println("Serious error, can not load configuration!!!!!");
            return;
        }
        initValues();
        loadWatchList();
        System.err.println(snapShotValues());
    }

    private static void initValues() {
        LINK_FLUSH_CYCLE_LENGTH = (int) (LINK_LOAD_BEFORE * 0.2);
        ServerUrl = "http://" + SERVER_DOMAIN + APPLICATION_CONTEXT;
        IdlePageUrl = ServerUrl + "/html/wait.html";
    }

    public static String snapShotValues() {
        try {
            ByteArrayOutputStream bou = new ByteArrayOutputStream();
            BufferedWriter writer = new BufferedWriter(new PrintWriter(bou));
            writer.write("--------------------Server Configuration Snapshot-------------------------------------");
            writer.newLine();
            writer.write("SERVER_DOMAIN : " + SERVER_DOMAIN);
            writer.newLine();

            writer.write("APPLICATION_CONTEXT : " + APPLICATION_CONTEXT);
            writer.newLine();

            writer.write("ServerUrl : " + ServerUrl);
            writer.newLine();

            writer.write("IdlePageUrl : " + IdlePageUrl);
            writer.newLine();

            writer.write("ENCODING_DB : " + ENCODING_DB);
            writer.newLine();

            writer.write("ENCODING_OUT_GOING_CONTENT : " + ENCODING_OUT_GOING_CONTENT);
            writer.newLine();

            writer.write("Encoding_DEFAULT_SYSTEM : " + Encoding_DEFAULT_SYSTEM);
            writer.newLine();

            writer.write("LENGTH_PAGE_SIZE : " + LENGTH_PAGE_SIZE);
            writer.newLine();

            writer.write("THRESHOLD_GPS_LOCATION_DIFF : " + THRESHOLD_GPS_LOCATION_DIFF);
            writer.newLine();

            writer.write("LINK_LOAD_BEFORE : " + LINK_LOAD_BEFORE);
            writer.newLine();

            writer.write("LINK_PROCESSING_EXPIRE : " + LINK_PROCESSING_EXPIRE);
            writer.newLine();

            writer.write("LINK_MONITOR_SLEEP : " + LINK_MONITOR_SLEEP);
            writer.newLine();

            writer.write("LINK_FLUSH_CYCLE_LENGTH : " + LINK_FLUSH_CYCLE_LENGTH);
            writer.newLine();

            if (OSHelper.isLinux()) {
                writer.write("LINUX_OCR_DIR : " + LINUX_OCR_DIR);
                writer.newLine();
            }

            if (WATCH_LIST != null && WATCH_LIST.length != 0) {
                writer.newLine();
                writer.write("URLs to watch :");
                writer.newLine();
                for (int i = 0; i < WATCH_LIST.length; i++) {
                    writer.write(WATCH_LIST[i].url);
                    writer.newLine();
                }
            }
            writer.write("--------------------End Server Configuration Snapshot-------------------------------------");
            writer.newLine();
            writer.close();
            return bou.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void loadWatchList() {
        InputStream ins = null;
        InputStreamReader reader = null;
        try {
            ins = Constants.class.getClassLoader().getResourceAsStream(FILENAME_LINK_WATCH_LIST);
            if (ins == null) {
                System.err.println("Can not load watch list from file :" + FILENAME_LINK_WATCH_LIST);
                return;
            }
            reader = new InputStreamReader(ins, Encoding_DEFAULT_SYSTEM);
            List list = IOUtils.readLines(ins);
            if (list.size() != 0) {
                WATCH_LIST = new Link[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    String url = (String) list.get(i);
                    WATCH_LIST[i] = new Link(url);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CommonUtil.closeStream(reader);
            CommonUtil.closeStream(ins);
        }
    }

}
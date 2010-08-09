package com.zyd;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.tj.common.CommonUtil;
import com.tj.common.OSHelper;
import com.zyd.linkmanager.Link;

@SuppressWarnings("unchecked")
public class Constants {
    private static Logger logger = Logger.getLogger(Constants.class);

    public static String SERVER_DOMAIN;
    public static String APPLICATION_CONTEXT;
    public static String ENCODING_DB;

    /*
     * the name of the running instance, prod, dev etc.
     */
    public static String INSTANCE_NAME;

    /**
     * How many records to return by default when querying
     */
    public static int LENGTH_PAGE_SIZE;
    /**
     * Max allowed page size
     */
    public static int MAX_PAGE_SIZE;

    /**
     * the encoding all outgoing content will be using, for xml/js/html 
     */
    public static String ENCODING_OUT_GOING_CONTENT;

    /**
     * if incoming request doesn't specify an encoding, what to use .
     */
    public static String ENCODING_INCOMING_CONTENT;

    /**
     * Where to put temp ocr files, only works in linux system 
     */
    public static String LINUX_OCR_DIR;

    /**
     * How many time should try, before giving up a link.
     */
    public static int LINK_MAX_TRY;

    /**
     * if a link is processed for too long, it will be treated as an error, this defined how long this will happen, in seconds
     */
    public static int LINK_PROCESSING_EXPIRE;

    /**
     * how soon will link manager scan links and outdate expried processing links.
     */
    public static int LINK_MONITOR_SCAN_INTERVAL;

    /**
     * how long will access controller pull out data from ipblocker and update it's block list
     */
    public static int ACCESS_CONTROLLER_EXECUTION_INTERVAL;

    /**
     * The cycle length that ipblocker counts the request from each client and decide which one to block. 
     */
    public static int IPCOUNTER_CHECK_INTERVAL;
    /**
     * the maximum request allowed from each ip address per IPCOUNTER_CHECK_INTERVAL.
     */
    public static int IPCOUNTER_MAX_ACCESS_PER_CYCLE;

    /**
     * How long will access controller purge ip address that is not used.
     */
    public static int AUTHORIZATION_CONTROLLER_EXECUTION_INTERVAL;

    public static int WORKER_THREAD_EXECUTION_INTERVAL;

    /*
     * These fields are derived from other values, don't put any values
     */

    /* the full server url starting with http://www.domaon.com:port/context */
    public static String ServerUrl;
    public static String IdlePageUrl;

    /*
     * These fields are fixed, derived from system
     */

    public static String Encoding_DEFAULT_SYSTEM = Charset.defaultCharset().toString();
    public static SimpleDateFormat DATEFORMAT_DEFAULT = new SimpleDateFormat("yyyy-MM-dd");
    public static String FILENAME_LINK_WATCH_LIST = "watch.list";
    public static Link[] WATCH_LIST = new Link[0];
    public static String VERSION_STRING = Long.toString(new Date().getTime()).substring(5);

    /*
     * These are system wide comment variables, should never be changed or reassigned
     */
    public final static Object[] ZERO_OBJECT_LIST = new Object[0];
    public final static String ALLOWED_API_QUERY_PARAMETER_CONFIG_FILE = "allowed.prop";

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
                logger.fatal("Can not load configuratoin file. System is not properly configured");
                return;
            }
        }
        logger.info("Loading server configuration from " + configFileName);
        loadValueFromStream(ins);

    }

    public static void loadValueFromStream(InputStream ins) {
        boolean r = false;
        try {
            r = CommonUtil.loadStaticPropertyFromFile(Constants.class, ins);
        } catch (IOException e) {
            logger.fatal("Serious error, can not load configuration from given stream!!!!!");
        }
        if (r == false) {
            logger.fatal("Serious error, can not load configuration from given stream!!!!!");
            return;
        }
        initValues();
        loadWatchList();
        logger.info(snapShotValues());
    }

    private static void initValues() {
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

            writer.write("INSTANCE_NAME : " + INSTANCE_NAME);
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

            writer.write("LINK_PROCESSING_EXPIRE : " + LINK_PROCESSING_EXPIRE);
            writer.newLine();

            writer.write("LINK_MONITOR_SLEEP : " + LINK_MONITOR_SCAN_INTERVAL);
            writer.newLine();

            if (OSHelper.isLinux()) {
                writer.write("LINUX_OCR_DIR : " + LINUX_OCR_DIR);
                writer.newLine();
            }
            /*
            if (false && WATCH_LIST != null && WATCH_LIST.length != 0) {
                writer.newLine();
                writer.write("URLs to watch :");
                writer.newLine();
                for (int i = 0; i < WATCH_LIST.length; i++) {
                    writer.write(WATCH_LIST[i].url);
                    writer.newLine();
                }
            }*/
            writer.write("--------------------End Server Configuration Snapshot-------------------------------------");
            writer.newLine();
            writer.close();
            return bou.toString();
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    private static void loadWatchList() {
        InputStream ins = null;
        InputStreamReader reader = null;
        try {
            ins = Constants.class.getClassLoader().getResourceAsStream("watch.list");
            if (ins == null) {
                logger.error("Can not load watch list from file watch.list under classpath");
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
            logger.error(e);
        } finally {
            CommonUtil.closeStream(reader);
            CommonUtil.closeStream(ins);
        }
    }
}
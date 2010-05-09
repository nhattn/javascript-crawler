package com.zyd;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

public class Config {
    public final static String Host = "localhost:8080";
    public final static String WebContext = "/crawler";
    public final static String ServerUrl = "http://" + Host + WebContext;
    public final static String TemplatePath = "E:\\workspace\\webcrawl\\src\\main\\webapp\\temp";

    public final static String Encoding = Charset.defaultCharset().toString();
    public final static String Encoding_DB = "ISO-8859-1";
    public final static String PARAMETER_NAME_OBJECT_ID = "objectid";

    /**
     * the encoding all outgoing content will be using, including xml/js/html 
     */
    public final static String ENCODING_OUT_GOING_CONTENT = "GBK";
    /**
     * if incoming request doesn't specify an encoding.
     */
    public final static String ENCODING_INCOMING_CONTENT = "GBK";
    public static String IdlePageUrl = ServerUrl + "/html/wait.html";

    public final static SimpleDateFormat DATEFORMAT_DEFAULT = new SimpleDateFormat("yyyy-MM-dd");
}
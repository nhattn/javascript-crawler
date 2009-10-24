package com.zyd;

import java.nio.charset.Charset;

public class Config {
	public final static String Host = "localhost:8080";
	public final static String WebContext = "/crawler";
	public final static String ServerUrl = "http://" + Host + WebContext;
	public final static String TemplatePath = "E:\\workspace\\crawler\\src\\main\\webapp\\temp";

	public final static String Encoding = Charset.defaultCharset().toString();
	public final static String Encoding_DB = "ISO-8859-1";
}
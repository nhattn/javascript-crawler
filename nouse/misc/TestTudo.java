package misc;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;

public class TestTudo {
	public static String getAndGetString(String url, Map<String, String> params, Map<String, String> headers) {
		String r = null;
		try {
			HttpClient client = new HttpClient();

			if (params != null) {
				StringBuffer buf = new StringBuffer();
				for (String k : params.keySet()) {
					buf.append(k);
					buf.append('=');
					buf.append(URLEncoder.encode(params.get(k)));
					buf.append("&");
				}
				if (buf.length() != 0) {
					buf.deleteCharAt(buf.length() - 1);
				}
				if (url.indexOf('?') > 0) {
					url = url + "&" + buf.toString();
				} else {
					url = url + "?" + buf.toString();
				}
			}

			GetMethod method = new GetMethod(url);
			if (headers != null) {
				for (String s : headers.keySet()) {
					method.addRequestHeader(s, headers.get(s));
				}
			}

			int statusCode = client.executeMethod(method);

			System.out.println("-------");
			System.out.println(method.getResponseHeader("Location"));
			Header[] hs = method.getResponseHeaders();
			InputStream ins = method.getResponseBodyAsStream();
			for (Header h : hs) {
				System.out.println(h.getName() + " : " + h.getValue());
			}
			if (statusCode != HttpStatus.SC_OK) {
				System.out.println(statusCode);
				return null;
			}
			r = IOUtils.toString(ins);
			ins.close();
			method.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}

	public static void main(String[] args) {
		HashMap<String, String> header = new HashMap<String, String>();
		header.put("Host", "www.tudou.com");
		header.put("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5");
		header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		header.put("Accept-Language", "en-us,en;q=0.5");
		header.put("Accept-Encoding", "gzip,deflate");
		header.put("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		header.put("Keep-Alive", "300");
		header.put("Connection", "keep-alive");
		header.put("Referer", "http://localhost/wordpress_cn/?p=17");
		getAndGetString("http://www.tudou.com/v/PrTFHnr2f1g", null, header);
	}
}

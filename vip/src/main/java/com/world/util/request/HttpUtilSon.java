package com.world.util.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class HttpUtilSon {
	
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";
	private static Logger log = Logger.getLogger(HttpUtilSon.class.getName());
	
	/***
	 * 解析置顶结果为json
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	public static JSONObject getJson(String url , Map<String , String> params , int connectTimeout , int readTimeout, boolean isFlag) throws IOException{
		String res = doPost(url, params, DEFAULT_CHARSET, connectTimeout, readTimeout, isFlag);
		JSONObject js = null;
		try {
			js = JSONObject.fromObject(res);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return js;
	}
	
	
	
	/**
	 * http dopost请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static String doPost(String url, Map<String, String> params,int connectTimeout,int readTimeout) throws IOException {
		return doPost(url, params, DEFAULT_CHARSET,connectTimeout,readTimeout, false);
	}
	
	
	public static String doPost(String url, Map<String, String> params,int connectTimeout,int readTimeout, boolean isFlag) throws IOException {
		return doPost(url, params, DEFAULT_CHARSET,connectTimeout,readTimeout, isFlag);
	}

	public static String doPost(String url, Map<String, String> params, String charset,int connectTimeout,int readTimeout, boolean isFlag)
			throws IOException {
		String ctype = "application/x-www-form-urlencoded;charset=" + charset;
		String query = buildQuery(params, charset, isFlag);
		byte[] content={};
		if(query!=null){
			content=query.getBytes(charset);
		}
		return doPost(url, ctype, content, connectTimeout, readTimeout);
	}

	public static String doPost(String url, String ctype, byte[] content,int connectTimeout,int readTimeout) throws IOException {
		HttpURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			try{
				conn = getConnection(new URL(url), METHOD_POST, ctype);	
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
			}catch(IOException e){
				Map<String, String> map=getParamsFromUrl(url);
				throw e;
			}
			try{
				try {
					out = conn.getOutputStream();
					out.write(content);
					rsp = getResponseAsString(conn);
				} catch (ConnectException e) {
					log.error("连接地址：" + url + "不可用！！！" + e.toString(), e);
				}
			}catch(IOException e){
				Map<String, String> map=getParamsFromUrl(url);
				throw e;
			}
			
		}finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}


	public static String doGet(String url, Map<String, String> params) throws IOException {
		return doGet(url, params, DEFAULT_CHARSET);
	}

	public static String doGet(String url, Map<String, String> params, String charset)
			throws IOException {
		HttpURLConnection conn = null;
		String rsp = null;

		try {
			String ctype = "application/x-www-form-urlencoded;charset=" + charset;
			String query = buildQuery(params, charset, false);
			try{
				conn = getConnection(buildGetUrl(url, query), METHOD_GET, ctype);
			}catch(IOException e){
				Map<String, String> map=getParamsFromUrl(url);
				throw e;
			}
			
			try{
				rsp = getResponseAsString(conn);
			}catch(IOException e){
				Map<String, String> map=getParamsFromUrl(url);
				throw e;
			}
			
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	public static HttpURLConnection doGet(String url) {
		HttpURLConnection conn = null;
		try {
			String ctype = "application/x-www-form-urlencoded;charset=iso-8859-1";
			String query = buildQuery(null, "iso-8859-1", false);
			conn = getConnection(buildGetUrl(url, query), METHOD_GET, ctype);
			return conn;
		}catch(Exception ex){
			log.error(ex.toString(), ex);
		}
		return null;
	}
	
	private static HttpURLConnection getConnection(URL url, String method, String ctype) throws IOException {
		HttpURLConnection conn = null;
		if ("https".equals(url.getProtocol())) {
			SSLContext ctx = null;
			try {
				ctx = SSLContext.getInstance("TLS");
				ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
			} catch (Exception e) {
				throw new IOException(e);
			}
			HttpsURLConnection connHttps = (HttpsURLConnection) url.openConnection();
			connHttps.setSSLSocketFactory(ctx.getSocketFactory());
			connHttps.setHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			});
			conn = connHttps;
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}

		conn.setRequestMethod(method);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
		conn.setRequestProperty("User-Agent", "top-sdk-java");
		conn.setRequestProperty("Content-Type", ctype);
		//conn.setRequestProperty("Connection", "Keep-Alive");
		return conn;
	}
	
	
	private static URL buildGetUrl(String strUrl, String query) throws IOException {
		URL url = new URL(strUrl);
		if (StringUtils.isEmpty(query)) {
			return url;
		}

		if (StringUtils.isEmpty(url.getQuery())) {
			if (strUrl.endsWith("?")) {
				strUrl = strUrl + query;
			} else {
				strUrl = strUrl + "?" + query;
			}
		} else {
			if (strUrl.endsWith("&")) {
				strUrl = strUrl + query;
			} else {
				strUrl = strUrl + "&" + query;
			}
		}

		return new URL(strUrl);
	}

	public static String buildQuery(Map<String, String> params, String charset, boolean isFlag) throws IOException {
		if (params == null || params.isEmpty()) {
			return null;
		}
		
		if(isFlag){
			//按map键首字母顺序进行排序
			params = MapSort.sortMapByKey(params);
		}
		
		StringBuilder query = new StringBuilder();
		Set<Entry<String, String>> entries = params.entrySet();
		boolean hasParam = false;

		for (Entry<String, String> entry : entries) {
			String name = entry.getKey();
			String value = entry.getValue();
			if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value)) {
				if (hasParam) {
					query.append("&");
				} else {
					hasParam = true;
				}

				query.append(name).append("=").append(URLEncoder.encode(value, charset));
			}
		}

		return query.toString();
	}

	protected static String getResponseAsString(HttpURLConnection conn) throws IOException {
		String charset = getResponseCharset(conn.getContentType());
		InputStream es = conn.getErrorStream();
		if (es == null) {
			try {
				es = conn.getInputStream();
			} catch (SocketTimeoutException e) {
				log.error("error:" + e.toString() + "URL:" + conn.getURL(), e);
				return null;
			}
			return getStreamAsString(es, charset);
		} else {
			String msg = getStreamAsString(es, charset);
			if (StringUtils.isEmpty(msg)) {
				throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
			} else {
				throw new IOException(msg);
			}
		}
	}

	private static String getStreamAsString(InputStream stream, String charset) throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
			StringWriter writer = new StringWriter();

			char[] chars = new char[256];
			int count = 0;
			while ((count = reader.read(chars)) > 0) {
				writer.write(chars, 0, count);
			}

			return writer.toString();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	private static String getResponseCharset(String ctype) {
		String charset = DEFAULT_CHARSET;

		if (!StringUtils.isEmpty(ctype)) {
			String[] params = ctype.split(";");
			for (String param : params) {
				param = param.trim();
				if (param.startsWith("charset")) {
					String[] pair = param.split("=", 2);
					if (pair.length == 2) {
						if (!StringUtils.isEmpty(pair[1])) {
							charset = pair[1].trim();
						}
					}
					break;
				}
			}
		}

		return charset;
	}

	public static String decode(String value) {
		return decode(value, DEFAULT_CHARSET);
	}

	public static String encode(String value) {
		return encode(value, DEFAULT_CHARSET);
	}

	public static String decode(String value, String charset) {
		String result = null;
		if (!StringUtils.isEmpty(value)) {
			try {
				result = URLDecoder.decode(value, charset);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	public static String encode(String value, String charset) {
		String result = null;
		if (!StringUtils.isEmpty(value)) {
			try {
				result = URLEncoder.encode(value, charset);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	private static Map<String, String> getParamsFromUrl(String url) {
		Map<String,String> map=null;
		if(url!=null&&url.indexOf('?')!=-1){
			map=splitUrlQuery(url.substring(url.indexOf('?')+1));
		}
		if(map==null){
			map=new HashMap<String,String>();
		}
		return map;
	}
	
	public static Map<String, String> splitUrlQuery(String query) {
		Map<String, String> result = new HashMap<String, String>();

		String[] pairs = query.split("&");
		if (pairs != null && pairs.length > 0) {
			for (String pair : pairs) {
				String[] param = pair.split("=", 2);
				if (param != null && param.length == 2) {
					result.put(param[0], param[1]);
				}
			}
		}

		return result;
	}
	
}
 class DefaultTrustManager implements X509TrustManager {
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

	public void checkClientTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
		
	}

	public void checkServerTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
		
	}
	
	public String toStringMap(Map m){
		StringBuilder sbl = new StringBuilder();
		for(Iterator<Entry> i = m.entrySet().iterator(); i.hasNext();){
			Entry e = i.next();
			Object o = e.getValue();
			String v = "";
			if(o == null){
				v = "";
			}else if(o instanceof String[]) {
				String[] s = (String[]) o;
				if(s.length > 0){
					v = s[0];
				}
			}else{
				v=o.toString();
			}
			sbl.append("&").append(e.getKey()).append("=").append(v);
		}
		String s = sbl.toString();
		if(s.length()>0){
			return s.substring(1);
		}
		return "";
	}
}
package com.world.util.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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

import com.auth0.jwt.internal.com.fasterxml.jackson.databind.ObjectMapper;
import com.world.util.sign.EncryptUtils;
import com.world.util.sign.RSACoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

public class HttpUtil {
	
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";
	private static int DEFAULT_CONNECT_TIMEOUT = 5000;
	private static int DEFAULT_READ_TIMEOUT = 8000;
	
	private static Logger log = Logger.getLogger(HttpUtil.class.getName());


	private static final String RANDOM_SOURCE = "23456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQSTUVWXYZ";
	 
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
			if(res!=null && res.startsWith("({")){
				res = res.substring(1, res.length()-1);
			}
			js = JSONObject.parseObject(res);
		} catch (Exception e) {
			log.error("error:" + res, e);
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
		return doPost(url, ctype, content, connectTimeout, readTimeout).getContent();
	}

	public static String doPostv2(String url, String ctype, String contentJson, int connectTimeout,int readTimeout)
			throws Exception {

		//加密
		//1获取一个随即的AES 加密key
		String AESencKey = RandomStringUtils.random(8, RANDOM_SOURCE);
		//2对AESencKey进行RSA 私钥加密
		String tokenEnc = new String(Base64.encodeBase64(RSACoder.encryptByPublicKey(AESencKey.getBytes(), RSACoder.getPublicKey())),"UTF-8");
		//3将加密的token放入header
		String encBody = EncryptUtils.encryptAES(AESencKey, contentJson);

		JSONObject jsonBody = new JSONObject();
		jsonBody.put("body",encBody);


		HttpURLConnection conn = null;
		OutputStream out = null;

		String bodyJson = null;
		try {
			conn = getConnection(new URL(url), METHOD_POST, ctype, "", null);
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
			conn.setRequestProperty("Token", tokenEnc);
			conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");



			out = conn.getOutputStream();
			out.write(jsonBody.toJSONString().getBytes());

			ResponseContent response = getResponseAsString(conn);
			byte[] t = Base64.decodeBase64(response.getHeaderToken());
			String aesKey = new String(RSACoder.decryptByPublicKey(t, RSACoder.getPublicKey()), "UTF-8");


			String encodeContent = response.getContent();
			JSONObject result = JSONObject.parseObject(encodeContent);
			String body = result.getString("body");

			bodyJson = EncryptUtils.decryptAES(aesKey, body);
		}catch (Exception e){
			log.error(e.toString(), e);
			throw e;
		}finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return bodyJson;
	}

	public static ResponseContent doPost(String url, String ctype, byte[] content,int connectTimeout,int readTimeout) throws IOException {
		HttpURLConnection conn = null;
		OutputStream out = null;
		ResponseContent esponse = null;
		try {
			try{
				conn = getConnection(new URL(url), METHOD_POST, ctype, "", null);
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
			}catch(IOException e){
				throw e;
			}
			try{
				try {
					out = conn.getOutputStream();
				} catch (ConnectException e) {
					log.info("连接地址：" + url + "不可用！！！", e);
				}
				out.write(content);
				esponse = getResponseAsString(conn);
			}catch(IOException e){
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

		return esponse;
	}


	public static String doGet(String url, Map<String, String> params) throws IOException {
		return doGet(url, params, DEFAULT_CHARSET, null).getContent();
	}

	public static String doGet(String url, Map<String, String> params, Map<String, String> headerMap) throws IOException {
		return doGet(url, params, DEFAULT_CHARSET, headerMap).getContent();
	}

	public static String doGet(String url, Map<String, String> params, int timeout, int readTimeout) throws IOException {
		DEFAULT_CONNECT_TIMEOUT = timeout;
		DEFAULT_READ_TIMEOUT = readTimeout;
		return doGet(url, params, DEFAULT_CHARSET, null).getContent();
	}

	public static String doGetv2(String url, Map<String, String> params, int timeout, int readTimeout) throws Exception {
		DEFAULT_CONNECT_TIMEOUT = timeout;
		DEFAULT_READ_TIMEOUT = readTimeout;
		ResponseContent response = doGet(url, params, DEFAULT_CHARSET, null);


		byte[] t = Base64.decodeBase64(response.getHeaderToken());
		String aesKey = new String(RSACoder.decryptByPublicKey(t, RSACoder.getPublicKey()), "UTF-8");


		String encodeContent = response.getContent();
		JSONObject content = JSONObject.parseObject(encodeContent);
		String body = content.getString("body");
		//使用token解密
		String bodyJson = EncryptUtils.decryptAES(aesKey, body);

		return bodyJson;
	}

	public static ResponseContent doGet(String url, Map<String, String> params, String charset, Map<String, String> headerMap)
			throws IOException {
		HttpURLConnection conn = null;
		ResponseContent response = null;

		try {
			String ctype = "application/x-www-form-urlencoded;charset=" + charset;
			String query = buildQuery(params, charset, false);
			try{
				conn = getConnection(buildGetUrl(url, query), METHOD_GET, ctype, "", headerMap);
			}catch(IOException e){
				throw e;
			}
			
			try{
				response = getResponseAsString(conn);
			}catch(IOException e){
				throw e;
			}
			
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return response;
	}

	public static HttpURLConnection doGet(String url) {
		HttpURLConnection conn = null;
		try {
			String ctype = "application/x-www-form-urlencoded;charset=iso-8859-1";
			String query = buildQuery(null, "iso-8859-1", false);
			conn = getConnection(buildGetUrl(url, query), METHOD_GET, ctype, "", null);
			return conn;
		}catch(Exception ex){
			log.error(ex.toString(), ex);
		}
		return null;
	}
	
	public static HttpURLConnection getConnection(URL url, String method, String ctype, String token, Map<String, String> headerMap) throws IOException {
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
		//conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
		conn.setRequestProperty("Content-Type", ctype);
		if (headerMap != null && !headerMap.isEmpty()) {
			for (Map.Entry<String, String> entry : headerMap.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}

		conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
		conn.setReadTimeout(DEFAULT_READ_TIMEOUT);


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

	public static ResponseContent getResponseAsString(HttpURLConnection conn) throws IOException {
		ResponseContent response = new ResponseContent();
		String charset = getResponseCharset(conn.getContentType());
		InputStream es = conn.getErrorStream();
		if (es == null) {
			String content = getStreamAsString(conn.getInputStream(), charset);
			String token = conn.getHeaderField("Token");

			response.setContent(content);
			response.setHeaderToken(token);
			response.setContentType(conn.getContentType());
			response.setStatusCode(conn.getResponseCode());

			return response;
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
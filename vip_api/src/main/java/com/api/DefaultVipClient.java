package com.api;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSONObject;
import com.api.util.EncryDigestUtil;
import com.api.util.http.HttpUtil;

public class DefaultVipClient implements VipClient{

	protected static Logger log = Logger.getLogger(DefaultVipClient.class.getName());
	public DefaultVipClient(String serverUrl, String appKey,String appSecret) {
		this.serverUrl = serverUrl;
		this.appKey = appKey;
		//this.appSecret = EncryDigestUtil.digest(appSecret);
		this.appSecret = appSecret;
	}
	
	
	public DefaultVipClient(String serverUrl, String appKey,
			String appSecret, int connectTimeout, int readTimeout,
			boolean needCheckRequest, boolean needEnableParse) {
		this.serverUrl = serverUrl;
		this.appKey = appKey;
		//this.appSecret = EncryDigestUtil.digest(appSecret);
		this.appSecret = appSecret;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.needCheckRequest = needCheckRequest;
		this.needEnableParse = needEnableParse;
	}
	private static final String defaultCharset = "utf-8";
	private String serverUrl;
	private String appKey;
	private String appSecret;
	
	private int connectTimeout = 30000;	//default=3000
	private int readTimeout = 30000;
	private boolean needCheckRequest;
	private boolean needEnableParse;
	
	
	public VipResponse execute(VipRequest request) throws Exception {
		return execute(request, null);
	}

	public VipResponse execute(VipRequest request, int connectTimeout, int readTimeout) throws Exception {
		if(connectTimeout > 0){
			this.setConnectTimeout(connectTimeout);
		}
		if(readTimeout > 0){
			this.setReadTimeout(readTimeout);
		}
		return execute(request, null);
	}
	
	public VipResponse execute(VipRequest request , String session) throws Exception {
		VipResponse cr = new VipResponse();
		
		try{
			Map<String , String> params = request.getTextParams();//new HashMap<String , String>();
			if(params == null){
				params = new HashMap<String , String>();
			}
			params.put("accesskey", appKey);//添加key
			//参数执行加密形成签名
			//log.info("加密：" + HttpUtil.buildQuery(params , defaultCharset) + ":secret:" + appSecret);
			String hash = EncryDigestUtil.hmacSign(HttpUtil.buildQuery(params , defaultCharset), appSecret);
			
			params.put("sign", hash);
			params.put("reqTime",  String.valueOf(System.currentTimeMillis()));
			params.put("tx",  String.valueOf(System.currentTimeMillis() + (long)readTimeout));
			request.setTextParams(params);
			
			JSONObject jo = HttpUtil.getJson(serverUrl + request.getApiMethod(), request.getTextParams(), connectTimeout, readTimeout);
			
			if(jo != null && !jo.isEmpty()){
				cr.setCode(jo.getString("code"));
				cr.setMsg(jo.getString("message"));
				cr.setParams(request.getTextParams());
			}
		}catch(Exception ex){
			log.info("当前请求：" + serverUrl + request.getApiMethod());
			ex.printStackTrace();
		}
		return cr;
	}
	
	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public boolean isNeedCheckRequest() {
		return needCheckRequest;
	}

	public void setNeedCheckRequest(boolean needCheckRequest) {
		this.needCheckRequest = needCheckRequest;
	}

	public boolean isNeedEnableParse() {
		return needEnableParse;
	}

	public void setNeedEnableParse(boolean needEnableParse) {
		this.needEnableParse = needEnableParse;
	}

}

package com.api;

import java.util.HashMap;
import java.util.Map;

import com.api.config.ApiConfig;

public class VipClientFactory {

	private static Map<String, VipClient> map = null;
	
	public static VipClient getClient(String prefix){
		if(map == null){
			map = new HashMap<String, VipClient>();
		}
		return init(prefix, 0, 0);
	}
	
	public static VipClient getClient(String prefix, int connectTimeout, int readTimeout){
		if(map == null){
			map = new HashMap<String, VipClient>();
		}
		return init(prefix, connectTimeout, readTimeout);
	}

	private static synchronized VipClient init(String prefix, int connectTimeout, int readTimeout) {
		String key = prefix + "_" + connectTimeout + "_" + readTimeout;
		VipClient client = map.get(key);
		if(client == null){
			client = new DefaultVipClient(ApiConfig.getValue(prefix + "ServerUrl"), 
					ApiConfig.getValue(prefix + "AppKey"), ApiConfig.getValue(prefix + "AppSecret"));
			map.put(key, client);
		}
		if(connectTimeout > 0){
			client.setConnectTimeout(connectTimeout);
		}
		if(readTimeout > 0){
			client.setReadTimeout(readTimeout);
		}
		return client;
	}
}

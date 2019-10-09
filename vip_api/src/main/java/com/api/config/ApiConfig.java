package com.api.config;

import java.util.ResourceBundle;

public class ApiConfig {
	private static Object lock              = new Object();
	private static ApiConfig config     = null;
	private static ResourceBundle rb        = null;
	private final static String fielName    = "api";

	private ApiConfig() {
		rb = ResourceBundle.getBundle(fielName);
	}

	public static ApiConfig getInstance() {
		synchronized(lock) {
			if(null == config) {
				config = new ApiConfig();
			}
		}
		return (config);
	}

	public static synchronized boolean reloadConfig() {
		try {
			config = new ApiConfig();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static String getValue( String key) {
		if(rb == null){
			getInstance();
		}
		if(rb != null){
			return (rb.getString(key));
		}else{
			return null;
		}
		
	}
}

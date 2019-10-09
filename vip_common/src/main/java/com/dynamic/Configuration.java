package com.dynamic;

import java.util.ResourceBundle;

public class Configuration {
	private static Object lock              = new Object();
	private static Configuration config     = null;
	private static ResourceBundle rb        = null;
	private static final String CONFIG_FILE = "merchantInfo";
	
	private Configuration() {
		rb = ResourceBundle.getBundle(CONFIG_FILE);
	}
	
	public static Configuration getInstance() {
		synchronized(lock) {
			if(null == config) {
				config = new Configuration();
			}
		}
		return (config);
	}
	
	public static String getValue(String key) {
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

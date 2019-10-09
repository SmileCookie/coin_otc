package com.file.config;

import org.apache.log4j.Logger;

import java.util.ResourceBundle;

public class FileConfig {

	private final static Logger log = Logger.getLogger(FileConfig.class.getName());

	private static Object lock              = new Object();
	private static FileConfig config     = null;
	private static ResourceBundle rb        = null;
	private static final String CONFIG_FILE = "file_global";
	
	private FileConfig() {
		rb = ResourceBundle.getBundle(CONFIG_FILE);
	}
	
	public static FileConfig getInstance() {
		synchronized(lock) {
			if(null == config) {
				config = new FileConfig();
			}
		}
		for(String key : config.rb.keySet()){
			log.info("读取配置文件file_global.properties: " + key + "=" + rb.getString(key));
		}

		return (config);
	}
	
	public static String getValue(String key) {
		try {
			try {
				if(rb == null){
					getInstance();
				}
				if(rb != null){
					return (rb.getString(key));
				}else{
					return null;
				}
			} catch (Exception e) {
				return null;
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return null;
	}
}

package com.world.config.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

/***
 * 2015-11-15引进json版配置文件
 * @author apple
 *
 */
public class JsonConfig {
	private static Logger log = Logger.getLogger(JsonConfig.class);
	
	private static Object lock              = new Object();
	private static JsonConfig config     = null;
	private static JSONObject rb        = null;
	private static final String CONFIG_FILE = "/config.json";
	
	public JsonConfig() {
			InputStream is = JsonConfig.class.getResourceAsStream(CONFIG_FILE);
			BufferedReader reader = null;
			if(is != null){
				String laststr = "";
				try{
					InputStreamReader inputStreamReader = new InputStreamReader(is, "UTF-8");
					reader = new BufferedReader(inputStreamReader);
					String tempString = null;
					while((tempString = reader.readLine()) != null){
						tempString = tempString.trim();
						if(!tempString.startsWith("#") && !tempString.startsWith("//")){
							laststr += tempString;
						}
					}
					reader.close();
				}catch(IOException e){
					log.error("读取config.json文件异常", e);
					throw new RuntimeException("读取config.json文件异常");
				}finally{
					if(reader != null){
						try {
							reader.close();
						} catch (IOException e) {
							log.error("关闭流异常", e);
						}
					}
				}
				rb = (JSONObject) JSONObject.parse(laststr);
			}else{
				rb = new JSONObject();
			}

		for (Map.Entry<String, Object> entry : rb.entrySet()) {
			log.info("读取配置文件config.json: " + entry.getKey() + "=" + entry.getValue());
		}

	}
	
	public static JsonConfig getInstance() {
		log.info("初始化单例对象JsonConfig，加载配置文件config.json");
		synchronized(lock) {
			if(null == config) {
				config = new JsonConfig();
			}
		}
		return (config);
	}
	
	public static Object getValue(String key) {
		if(rb == null){
			getInstance();
		}
		try {
			if(rb != null){
				return rb.get(key);
			}else{
				return null;
			}
		} catch (Exception e) {
			log.error(e.toString() + ",key:" + key, e);
		}
		return null;
	}

	/**
	 * 重新加载配置信息
	 * @return
	 */
	public static boolean reloadConfig() {

		try {
			log.info("重新加载配置信息:开始");
			synchronized (lock) {
				config = new JsonConfig();
			}
			log.info("重新加载配置信息:结束");

			return true;
		} catch (Exception e) {
			log.error("重新加载配置信息失败", e);
			return false;
		}


	}
	
}
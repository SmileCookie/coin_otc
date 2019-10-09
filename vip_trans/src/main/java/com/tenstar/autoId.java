package com.tenstar;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class autoId {
	private final static Logger log = Logger.getLogger(autoId.class);

	private static autoId keygen = new autoId();
	private static final int POOL_SIZE = 200;
	private HashMap<String , KeyInfo> keylist = new HashMap<String , KeyInfo>(10);

	private autoId() {}

	public static autoId getInstance() {
		return keygen;
	}

	public synchronized long getNextKey(String keyName,String db) {
		KeyInfo keyinfo;
		if (keylist.containsKey(keyName)) {
			keyinfo = (KeyInfo) keylist.get(keyName);
			//log.info("key found");
		} else {
			keyinfo = new KeyInfo(POOL_SIZE, keyName,db);
			keylist.put(keyName, keyinfo);
			log.info("new key created");
		}
		return keyinfo.getNextKey(db);
	}

	/**
	 * 获取一个自动增长的id
	 * 
	 * @param names
	 * @return
	 */
	public static long getId(String names,String db) {
		try {
			return keygen.getNextKey(names, db);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			return -1;
		}
	}

	/**
	 * 获取一个带日期的id号
	 * 
	 * @param key
	 * @return
	 */
	public static long getDataId(String key,String db) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		long id = getId(key,db);
		if (id <= 0) {
			id = getId(key,db);// 重新获取一次
			if (id <= 0) {
				return -1;
			}
		}
		return Long.valueOf(sdf.format(new Date()) + "" + id);
	}

}

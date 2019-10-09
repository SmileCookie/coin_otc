package com.world.data.mongo;

import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;
import com.world.config.GlobalConfig;
import com.world.web.defense.AccessLog;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class MorphiaMongoUtil implements Dao {
	private static Map<String, MorphiaMongo> mongos = new HashMap<String, MorphiaMongo>();
	protected static Logger log = Logger.getLogger(MorphiaMongoUtil.class.getName());
	private static synchronized void initDb(String dbName, String ip,String user, String pwd) {
		if (mongos.get(dbName) != null) {
			return;
		}
		try {
			log.info("对dbName:" + dbName + ",ip:" + ip + ",user:" + user);
			MongoClient mongo = ConnectPool.getMongo(dbName, ip, user, pwd);

			Morphia morphia = new Morphia();
			morphia.map(AccessLog.class);
			log.info("系统映射所有Mongo数据表");
			MorphiaMongo morphiaMongo = new MorphiaMongo(mongo, morphia);
			
			mongos.put(dbName, morphiaMongo);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

	public static MorphiaMongo getMorphiaMongo() {
		
		return getMorphiaMongo(DEFAULT_DBNAME, GlobalConfig.mongoDbIp, GlobalConfig.mongodbUserName, GlobalConfig.mongodbPwd);
	}

	public static MorphiaMongo getMorphiaMongo(String dbName, String ip, String user, String pwd) {
		if(dbName == null){
			return null;
		}
		
		if (mongos.get(dbName) == null) {
			initDb(dbName, ip, user, pwd);
		}
		return mongos.get(dbName);
	}
	
	
	public synchronized static void closeAll(){
		log.info("注销mongodb连接");
		for(Map.Entry<String, MongoClient> entry : ConnectPool.mongos.entrySet()){
			try {
				MongoClient curMM = entry.getValue();
				if(curMM != null){
						log.info("关闭：" + entry.getKey());
						curMM.close();
						curMM = null;//一定要写这句话，不然系统不会回收，只是关闭了，连接存在。
				}
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
			//只需关闭一次就好
			//break;
		}
	}
	
}
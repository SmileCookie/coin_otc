package com.world.model.dao.worker;

import com.world.cache.Cache;
import com.world.common.VerifiUtil;
import com.world.config.GlobalConfig;
import com.world.controller.Base;
import com.world.model.dao.task.Worker;
import com.world.model.entitys.UserRequestApi;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 同步Memcached API IP访问数据
 * @author Cao Fangfang
 */
public class SyncAPIVisitByIpWorker extends Worker {

	private static final long serialVersionUID = 4180292500703424744L;

	static Logger logger = Logger.getLogger(Base.class);
	//**服务器名称，用来同步数据时标识是哪台服务器*//*
	private final static String SERVER_NAME = UUID.randomUUID().toString();

	public SyncAPIVisitByIpWorker(String name, String des) {
		super(name, des);
	}

	@Override
	public void run() {
		super.run();

		log.info("开始同步API IP访问数据 start...");
		try{
			sync();
		}catch (Exception ex){
			log.error("同步API IP访问数据出错", ex);
		}
		log.info("开始同步API IP访问数据 end...");
	}

	private synchronized void sync() throws Exception{
		String key = "BIT:syncAPIVisitByIPs";
		int expireTimes = 60 * 60 * 24 * 30; // 30天
		Map<String, UserRequestApi> localRquestIps = VerifiUtil.rquestIps;		//本地保存的ip名单
		Map<String, UserRequestApi> cacheRquestIps = (Map<String, UserRequestApi>)Cache.GetObj(key);	//memcache中保存的ip名单
		boolean islocalNotEmpty = localRquestIps != null && !localRquestIps.isEmpty();	//本地保存的ip名单是否为不为空，不为空就是true
		if(cacheRquestIps == null){
			if(islocalNotEmpty){
				Cache.SetObj(key, localRquestIps, expireTimes);
			}
		}else{
			if(!islocalNotEmpty){
				VerifiUtil.rquestIps = cacheRquestIps;
			}else{
				for(Map.Entry<String, UserRequestApi> cacheEntry : cacheRquestIps.entrySet()){
					String ip = cacheEntry.getKey();
					UserRequestApi cacheUserRequestApi= cacheEntry.getValue();

					UserRequestApi localUserRequestApi = localRquestIps.get(ip);
					if(localUserRequestApi == null){ // 同步下来
						localRquestIps.put(ip, cacheUserRequestApi);
					}else if(localUserRequestApi.version != cacheUserRequestApi.version){ // 版本不同时比较，累计
						log.info("本地版本：" + localUserRequestApi.version +
								"，服务器版本: " + cacheUserRequestApi.version);
						if(localUserRequestApi.last == cacheUserRequestApi.last){ // 同一分钟
							UserRequestApi prevUserRequestApi = null;
							boolean isPrevMapNull = cacheUserRequestApi.preMap == null;
							int addTimes = 0;
							int version = 0;
							if( isPrevMapNull || ( !isPrevMapNull && (
									prevUserRequestApi = cacheUserRequestApi.preMap.get(SERVER_NAME)) == null) ){
								addTimes = localUserRequestApi.times;
								if(isPrevMapNull){
									cacheUserRequestApi.preMap = new HashMap<String, UserRequestApi>();
								}
							}else{
								addTimes = localUserRequestApi.times - prevUserRequestApi.times;
								if(addTimes < 0) {
									addTimes = 0;
								}
							}

							cacheUserRequestApi.times += addTimes;
							cacheUserRequestApi.addVersion();

							localUserRequestApi.times = cacheUserRequestApi.times;
							localUserRequestApi.version = cacheUserRequestApi.version;
							localUserRequestApi.unlockMiliSeconds = cacheUserRequestApi.unlockMiliSeconds =
									localUserRequestApi.unlockMiliSeconds >= cacheUserRequestApi.unlockMiliSeconds ?
											localUserRequestApi.unlockMiliSeconds : cacheUserRequestApi.unlockMiliSeconds;

							cacheUserRequestApi.preMap.put(SERVER_NAME, cacheUserRequestApi);
						}else if(localUserRequestApi.last > cacheUserRequestApi.last){ //以本地的为准
							cacheUserRequestApi = localUserRequestApi;
							setTheSamePrev(cacheUserRequestApi);
							cacheRquestIps.put(ip, cacheUserRequestApi);
						}else{ // 以缓存中的为准
							localUserRequestApi = cacheUserRequestApi;
							setTheSamePrev(cacheUserRequestApi);
							localRquestIps.put(ip, localUserRequestApi);
						}
					}
				}

				// 把缓存中没有的加入进去
				for(Map.Entry<String, UserRequestApi> localEntry: localRquestIps.entrySet()){
					String ip = localEntry.getKey();
					if( !cacheRquestIps.containsKey(ip) ){
						cacheRquestIps.put(ip, localEntry.getValue());
					}
				}

				Cache.SetObj(key, cacheRquestIps, expireTimes);
			}
		}
	}

	private void setTheSamePrev(UserRequestApi userRequestApi){
		if(userRequestApi.preMap == null)
			userRequestApi.preMap = new HashMap<String, UserRequestApi>();

		userRequestApi.preMap.put(SERVER_NAME, userRequestApi);
	}

	// 设置同步的版本号
	private void setVersion(Map<String, UserRequestApi> rquestIps){
		for(Map.Entry<String, UserRequestApi> entry: rquestIps.entrySet()){
			entry.getValue().addVersion();
		}
	}
}

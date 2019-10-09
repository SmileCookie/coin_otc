package com.world.web.defense;

import java.util.HashMap;
import java.util.Map;

import com.google.code.morphia.query.QueryResults;
import com.world.cache.Cache;
import org.apache.log4j.Logger;

public class IpDefense {

	private final static Logger log = Logger.getLogger(IpDefense.class.getName());

	/****
	 * 定义一个系统内的防御容器
	 * key ： ip
	 * value : 0 黑名单  1 白名单
	 */
	private static Map<String , Integer> sysIps = new HashMap<String , Integer>();
	
	private static long version = 0;
	
	private static String IpDefenseVersionKey = "ip_defense_ver";
	
	private static IpDefenseDao ipDefenseDao = new IpDefenseDao();
	/***
	 * 判断当前ip是否有访问权限
	 * @param ip
	 * @return
	 */
	public static boolean visit(String ip){
		/*****
		 * 192.168.2.166
		 * 192.168.2.*
		 * 192.168.*.*
		 * 192.*.*.*
		 */
		Integer ipv = sysIps.get(ip);
		
		if(ipv == null){
			ipv = sysIps.get(getIpDuan(ip, 1));
		}
		
		if(ipv == null){
			ipv = sysIps.get(getIpDuan(ip, 2));
		}
		
		if(ipv == null){
			ipv = sysIps.get(getIpDuan(ip, 3));
		}
		
		if(ipv != null && ipv == 0){//黑名单
			return false;
		}
		return true;
	}
	
	/***
	 * 
	 * @param ip
	 * @param level  *号的数量
	 * @return
	 */
	private static String getIpDuan(String ip , int level){
		String[] ips = ip.split("[.]");
		StringBuilder sbr = new StringBuilder();
		if(ips.length == 4){
			switch (level) {
				case 1:
					sbr.append(ips[0]).append(".").append(ips[1]).append(".").append(ips[2]).append(".*");
					break;
				case 2:
					sbr.append(ips[0]).append(".").append(ips[1]).append(".*.*");
					break;
				case 3:
					sbr.append(ips[0]).append(".*.*.*");
					break;
				default:
					break;
			}
		}
		return sbr.toString();
	}
	
	/***
	 * 添加黑名单
	 * @param ip
	 */
	public static void addBlackIp(String ip){
		sysIps.put(ip, 0);
	}
	/***
	 * 添加白名单
	 * @param ip
	 */
	public static void addWhiteIp(String ip){
		sysIps.put(ip, 1);
	}
	/***
	 * 移除黑白名单
	 * @param ip
	 */
	public static void removeIp(String ip){
		sysIps.remove(ip);
	}
	
	/**
	 * 5s同步一次
	 * 同步黑白名单
	 */
	public static void syncFromDisk() {
		boolean needDatabase = false;
		if (version == 0) {// 系统初始化
			needDatabase = true;
		} else {
			long cacheVersion = 0;
			try {
				cacheVersion = (Long) Cache.GetObj(IpDefenseVersionKey);
			} catch (Exception e) {
				cacheVersion = 0;
			}

			if (cacheVersion == 0 || version > cacheVersion) {
				// 重建cacheVersion
				Cache.SetObj(IpDefenseVersionKey, version);
				needDatabase = true;
			} else if (version < cacheVersion) {
				needDatabase = true;
			} else if (version == cacheVersion){
				needDatabase = false;
			}
		}

		if (needDatabase) {// 同步ip数据库 并修改version cacheVersion的版本
			syncFromDatabase();
		}
	}
	
	private synchronized static void syncFromDatabase(){
		
		///调用数据库初始化代码
		//1.mongodb   list
		//2.保存到    sysIps
		//3.保存版本到cache
		//4.version = cacheVer
		
		try {
			sysIps.clear();
			QueryResults<IpDefenseData> rs  = ipDefenseDao.find();
			for (IpDefenseData ipData : rs) {
				sysIps.put(ipData.getIp(), ipData.getType());
			}
			
			//把当前jvm黑名单版本更新到最新
			Long cacheVersion = (Long) Cache.GetObj(IpDefenseVersionKey);
			if(cacheVersion == null){
				cacheVersion = 0L;
			}
			version = cacheVersion;
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		
	}
	
	/***
	 * 发出通知版本已经更新
	 */
	public static synchronized void updateVersion(){
		long cacheVersion =0 ;
		try {
			cacheVersion = (Long) Cache.GetObj(IpDefenseVersionKey);
		} catch (Exception e) {
			cacheVersion = 0;
		}
		Cache.SetObj(IpDefenseVersionKey, cacheVersion +1);
	}
	
}

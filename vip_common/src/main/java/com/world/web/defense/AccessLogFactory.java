package com.world.web.defense;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mongodb.WriteResult;
import com.world.util.WebUtil;
import com.world.util.callback.AsynMethodFactory;
import com.world.util.date.TimeUtil;
import com.world.web.defense.statistics.AccessStatisticsDao;

public class AccessLogFactory {
	
	protected static Logger log = Logger.getLogger(AccessLogFactory.class.getName());

	private static AccessLogDao logDao = null;
	private static AccessStatisticsDao accessStatisticsDao = null;
	
//	private static Queue<AccessLog> logs = new LinkedList<AccessLog>();
	
	private static Map<String, AccessLog> lhmLogs = new LinkedHashMap<String, AccessLog>();
	
	private final static int MAX = 50000;
	private static final int Hour = 60 * 60 * 1000;
	
	private static long lastSaveMinute = 0;
	private static long lastMinuteSaveTimes = 0;
	
	private static List<String> excludeLogs;
	
	
	public static synchronized AccessLogDao init(){
		if(logDao == null){
			logDao = new AccessLogDao();
			accessStatisticsDao = new AccessStatisticsDao();
		}
		return logDao;
	}
	
	
	public static AccessLogDao getAccessLogDao(){
		if(logDao == null){
			logDao = init();
		}
		return logDao;
	}
	
	public static void save(List<AccessLog> als , boolean save , long times){
		long start = System.currentTimeMillis();
		getAccessLogDao().saveList(als);
		long end1 = System.currentTimeMillis();
		log.debug("1保存日志耗时：" + (end1 - start) + ",save:" + save + "," + times);
		//getAccessLogDao() 之后 accessStatisticsDao一定初始化过了
		
		AccessLog al = als.get(0);
		
		
		if(save){
			accessStatisticsDao.inc(al.getMinuteFirst() , times);
		}
		long end = System.currentTimeMillis();
		log.debug("2保存日志耗时：" + (end - end1));
	}
	
	public static void saveToJVM(String urls , String params , String adminName , String userName, String ip){
		//初始化excludeLogs
		if(excludeLogs==null){
			String excludeLogStr = com.world.config.GlobalConfig.getValue("excludeLog");
			if(StringUtils.isNotBlank(excludeLogStr)){
				String[] split = excludeLogStr.split(",");
				excludeLogs = Arrays.asList(split);
			}else{
				excludeLogs = new ArrayList<String>();
			}
		}
		
		if(excludeLogs.contains(urls)){
			return;
		}
		if(lhmLogs.size() > MAX){//防止内存占用过大
			return;
		}
		AccessLog ac = new AccessLog();
		
		ac.setDate(TimeUtil.getNow());
		ac.setMinuteFirst(TimeUtil.getMinuteFirst().getTime());
		ac.setAdminName(adminName);
		ac.setUserName(userName);
		ac.setUrls(urls);
		ac.setIp(ip);
		ac.setParams(params);
//		if(logs == null){
//			logs = new LinkedList<AccessLog>();
//		}
		//log.info("::::" + ac.toStr());
		//log.info("add before:" + logs.size());
		String key = ac.toHash();
		ac.set_id(key);
		AccessLog acHas = lhmLogs.get(key);
		if(acHas != null){
			acHas.setTimes(acHas.getTimes() + 1);
		}else{
			ac.setTimes(1);
			lhmLogs.put(key, ac);
		}
		//lhmLogs.put(ac.toHash(), ac);
		//logs.add(ac);
	}
	
//	public static void saveToMongo(AccessLog al , boolean save , long times){
//		save(al , save , times);
//	}
	
	public static void saveFromJVM(){
		try {
			
//			AccessLog al = null;
//			synchronized (logs) {
//				while((al = logs.poll()) != null){
//					try {
//						//log.info("==========++++++++++++++" + logs.size());
//						//异步保存
//						AsynMethodFactory.addWork(AccessLogFactory.class, "saveToMongo", new Object[]{WebUtil.deepCopyObj(al)});
////						saveToMongo(al);
//					} catch (Exception e) {
//						log.error(e.toString(), e);
//					}
//				}
//			}
			
			long currentMinuteFirst = TimeUtil.getMinuteFirst().getTime();
			
			synchronized (lhmLogs) {
				List<String> removeKeys = new ArrayList<String>();
				
				List<AccessLog> noSaves = new ArrayList<AccessLog>();
				long last = 0;
				boolean save = false;
				//保存过去一分钟的
				for(Map.Entry<String, AccessLog> logEntry : lhmLogs.entrySet()){
					try {
						AccessLog curLog = logEntry.getValue();
						if(curLog.getMinuteFirst() < currentMinuteFirst){
							removeKeys.add(logEntry.getKey());
						}else{
							break;
						}
						
						//log.info("---------------====" + lastSaveMinute);
						if(curLog.getMinuteFirst() == lastSaveMinute){
							lastMinuteSaveTimes += curLog.getTimes();
						}else{
							if(lastSaveMinute != 0){
								save = true;
							}
							lastSaveMinute = curLog.getMinuteFirst();
							lastMinuteSaveTimes = curLog.getTimes();
						}
						noSaves.add(curLog);
						
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
				}
				
				if(noSaves.size() > 1){
					last = lastMinuteSaveTimes;
					//异步保存
					AsynMethodFactory.addWork(AccessLogFactory.class, "save", new Object[]{WebUtil.deepcopy(noSaves) , save , last});
					noSaves = new ArrayList<AccessLog>();
				}
				
				if(removeKeys.size() > 0){
					for(String rk : removeKeys){
						lhmLogs.remove(rk);
					}
				}
				removeKeys = null;
			}
			
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		
	}


	public static int deleteHourAgo() {
		return deleteFromTime(Hour);		
	}
	
	/**
	 * 删除多久前的数据
	 * @param time
	 * @return
	 */
	public static int deleteFromTime(int time) {
		Timestamp hourago = new Timestamp(TimeUtil.getNow().getTime() - time);
		WriteResult result = getAccessLogDao().deleteByQuery(getAccessLogDao().getQuery()
				.filter("date <", hourago ));
		int count = result.getN();
		return count;		
	}
}

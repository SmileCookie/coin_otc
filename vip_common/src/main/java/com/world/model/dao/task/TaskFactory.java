package com.world.model.dao.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.world.config.GlobalConfig;
import com.world.model.dao.auto.AccessLogWorker;
import com.world.model.dao.auto.DeleteExpireBlackWorker;
import com.world.model.dao.auto.SyncIPBlackListWorker;
import com.world.timer.WatchLanguage;

public class TaskFactory {
	public static Logger log = Logger.getLogger(TaskFactory.class.getName());
	////记录工厂内所有的员工
	public static List<Worker> workers = new ArrayList<Worker>();
	
	public static List<Boss> bosses = new ArrayList<Boss>();
	
	public static List<Worker> getWorkers(){
		return workers;
	}
	
	public static void work(Worker worker , long frequency){
		String className = worker.getClass().getSimpleName();
		String open = GlobalConfig.getValue(className);
		boolean ipOpen = false;
		try {
			ipOpen = open == null ? false : Boolean.parseBoolean(open);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		
		if(className.equals("WatchLanguage")){
			ipOpen = true;
		}
		work(worker , frequency , ipOpen);
	}
	
	/***
	 * 工人开工
	 * @param worker   具体的工人
	 * @param frequency  工作频率，多久工作一次
	 */
	public static void work(Worker worker , long frequency , boolean ipOpen){
		String className = worker.getClass().getSimpleName();
		//未开启
		if(!ipOpen){
			log.info("定时器：" + worker.getName() + "未开启。" + className);
			return;
		}else{
			log.info("定时器：" + worker.getName() + "已开启。" + className);
		}
		///跟进最新价格
		Boss boss = new Boss();
		//worker.timer = new Timer();
		worker.workFrequency = frequency;//取一次价格
		boss.setWorker(worker);
		boss.start(worker.name);
		workers.add(worker);
		bosses.add(boss);
	}
	
	public static void startAll(){
		work(new WatchLanguage("en" , "en" , "en"), 2 * 1000);
		work(new WatchLanguage("cn" , "cn" , "cn"), 2 * 1000);
		if(GlobalConfig.openAccessLog){
			work(new AccessLogWorker("AccessLogWorker" , "日志保存/清理定时器"), 5 * 1000);
		}
		
		work(new DeleteExpireBlackWorker("DeleteExpireBlackWorker" , "黑名单清理定时器"), 5 * 1000);
		
		work(new SyncIPBlackListWorker("SyncIPBlackListWorker" , "同步黑名单定时器"), 5 * 1000);
	}
	
	public static void shutdown(){
		try {
			for(Boss w : bosses){
				
				try {
					w.getWorker().stop = true;
					if(w.getWorker().isAutoReplace() && w.service != null){
						//w.service.shutdownNow();
						w.scheduledFuture.cancel(true);
						w.scheduledFuture = null;
						
						 if (!w.service.isShutdown()) {  
				           try {  
					            if(!w.service.isShutdown()) {  
					            	 w.service.shutdownNow();//关闭线程池 (强行关闭)  
					            }  
				            }finally{  
				                if (!w.service.isShutdown()) {  
				                	w.service.shutdownNow();  
				                }  
				            }  
				        }  
				        log.info("所有线程关闭"+w.service.isShutdown());
						
						log.info("service---销毁：" + w.getWorker().getName());
						w.service = null;
					}else{
						if(w.getWorker().timer != null){
							w.getWorker().timer.cancel();
							w.getWorker().timer = null;
						}
						log.info("timer---销毁：" + w.getWorker().getName());
					}
					
					
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}
} 

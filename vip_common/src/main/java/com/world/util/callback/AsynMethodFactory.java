package com.world.util.callback;

import org.apache.log4j.Logger;

import com.googlecode.asyn4j.core.handler.CacheAsynWorkHandler;
import com.googlecode.asyn4j.core.handler.DefaultErrorAsynWorkHandler;
import com.googlecode.asyn4j.core.handler.FileAsynServiceHandler;
import com.googlecode.asyn4j.service.AsynService;
import com.googlecode.asyn4j.service.AsynServiceImpl;

public class AsynMethodFactory {
	protected static Logger log = Logger.getLogger(AsynMethodFactory.class.getName());
	public static AsynService anycService;
	
	private synchronized static void init(){
		if(anycService == null){
			// 初始化异步工作服务
	        anycService = AsynServiceImpl.getService(300, 3000L, 50, 100,1000);
	        //异步工作缓冲处理器
	        anycService.setWorkQueueFullHandler(new CacheAsynWorkHandler(100));
	        //服务启动和关闭处理器
	        anycService.setServiceHandler(new FileAsynServiceHandler());
	        //异步工作执行异常处理器
	        anycService.setErrorAsynWorkHandler(new DefaultErrorAsynWorkHandler());
	        // 启动服务
	        anycService.init();
		}
	}
	
	
	public static void addWork(Object tagerObject, String method,Object... params){
		if(anycService == null){
			init();
		}
		anycService.addWork(tagerObject, method, params , null);
	}
	
	public static int getAsynWorkQueueSize(){
		return anycService.getWorkQueqe().size();
	}
	
	public static long getExcuteNum(){
		return anycService.getExcuteNum();
	}
	
	public static long getBalance(){
        if(anycService == null){
            init();
        }
		return anycService.getBalance();
	}
	
	public static String getStat(){
		return anycService.getRunStatInfo();
	}
	
	
	public static synchronized void stop(){
		try {
			if(anycService != null){
				anycService.close();
				log.info("--==销毁异步执行任务");
			} 
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}
}

package com.world.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.world.config.MemoryConfig;
import com.world.data.mongo.MorphiaMongoUtil;
import com.world.data.mysql.connection;
import com.world.data.voltdb.VoltDbClientFactory;
import com.world.model.dao.task.TaskFactory;
import com.world.util.ClassUtil;
import com.world.util.callback.AsynMethodFactory;
import com.world.util.callback.CallbackMethod;

public class WebContainer {
	static Logger log = Logger.getLogger(WebContainer.class);
	static boolean hasShutdown = false;
	
	///关闭容器之前要做的事情
	private static List<CallbackMethod> beforeCloseTask = new ArrayList<CallbackMethod>();
	
	public synchronized static void addCloseTask(CallbackMethod method){
		beforeCloseTask.add(method);
	}
	
	/***
	 * 容器关闭
	 */
	public synchronized static void shutdown(){
		if(hasShutdown){
			return;
		}
		
		hasShutdown = true;
		
		if(beforeCloseTask.size() > 0){
			for(CallbackMethod cm : beforeCloseTask){
				try {
					long start = System.currentTimeMillis();
					ClassUtil.invokeByClassAndMethod(cm.getCls(), cm.getMethod(), cm.getParams());
					long end = System.currentTimeMillis();
					log.info("http()方法：" + cm.getMethod() + "耗时：" + (end - start));
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
			}
		}
		
		log.info("销毁相关开始");
		TaskFactory.shutdown();
		AsynMethodFactory.stop();
		if (MemoryConfig.mem != null) {
			try {
				log.info("准备销毁memcached连接");
				MemoryConfig.mem.shutdown();
			} catch (Exception ex) {
				log.error(ex.toString(), ex);
			}
		}
		
		connection.close();
		MorphiaMongoUtil.closeAll();
		VoltDbClientFactory.close();
		log.info("销毁相关结束");
	}
	
	/***
	 * 容器关闭
	 */
	public synchronized static void shutdownTask(){
		TaskFactory.shutdown();
		log.info("销毁任务相关结束");
	}
	
	/***
	 * 容器启动
	 */
	public static void start(){
		
	}
}

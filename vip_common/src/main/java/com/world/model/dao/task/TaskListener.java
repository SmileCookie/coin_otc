package com.world.model.dao.task;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.world.web.WebContainer;

public class TaskListener implements ServletContextListener{
	public static Logger log = Logger.getLogger(TaskListener.class.getName());
	
	public static boolean hasStart = false;

	public static long initTime = System.currentTimeMillis();

	public void contextInitialized(ServletContextEvent sce) {
		hasStart = true;
	}

	public void contextDestroyed(ServletContextEvent sce) {
//		List<Worker> workers = TaskFactory.workers;
//		for(Worker w : workers){
//			if(w.timer != null){
//				w.timer.cancel();
//				log.info(w.name + "," + w.des + "停止工作。");
//			}
//		}
		
		log.info("--===litener等待任务销毁");
		try {
			WebContainer.shutdownTask();
		} catch (Exception e) {
			log.error("关闭定时任务失败" + e.toString(), e);
		}
		log.info("定时器销毁");
	}

}

package com.world.timer;

import javax.servlet.ServletContextEvent;

import com.world.model.dao.task.TaskListener;
import com.world.timer.auto.AutoFactory;
public class listener  extends TaskListener{
	//销毁
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
	}

	//初始化监听器
	public void contextInitialized(ServletContextEvent event) {
		
		AutoFactory.start();
		
		log.info("定时器容器已启动");
		log.info("已经添加任务");
		log.info("启动定时器");
	}
}

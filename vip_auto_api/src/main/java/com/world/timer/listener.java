package com.world.timer;

import javax.servlet.ServletContextEvent;

import com.world.model.dao.AutoFactory;
import com.world.model.dao.task.TaskListener;
public class listener  extends TaskListener{
	//销毁
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
	}

	//初始化监听器
	public void contextInitialized(ServletContextEvent event) {
		
		AutoFactory.start();
		
		event.getServletContext().log("定时器容器已启动");
		event.getServletContext().log("已经添加任务"); 
		System.out.println("启动定时器");
	}
}

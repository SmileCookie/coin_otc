package com.tenstar.timer;

import javax.servlet.ServletContextEvent;

import com.socket.IOClient;
import com.tenstar.timer.auto.AutoShualiangTask;
import com.world.model.dao.task.TaskListener;
import com.world.model.daos.auto.AutoFactory;
/****
 * 任务类监听器
 * @author apple
 *
 */
public class listener  extends TaskListener{
	//销毁
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
	}

	//初始化监听器
	public void contextInitialized(ServletContextEvent event) {
		try {
			Class.forName("com.world.data.big.MysqlDownTable");
		} catch (ClassNotFoundException e) {
			log.error(e.toString(), e);
		}

		// 启动socket客户端
		IOClient.start();

		AutoFactory.start();
		
		//开启刷量线程
		//Thread t = new Thread(new AutoShualiangTask());
		//t.start();
		
		log.info("定时器容器已启动");
		log.info("已经添加任务");
		log.info("启动定时器");
	}
}

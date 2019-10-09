package com.world.timer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.world.model.dao.robot.RobotDao;

/**
 * 获取线程池
 * @author zhanglinbo 20161219
 *
 */
public class ExecutorServicePool {

	private static RobotDao robotDao = new RobotDao();
	
	private static ExecutorService executorService;
	public static ExecutorService getExecutorService(){
		if(executorService == null){
			executorService = Executors.newCachedThreadPool();
		}
		return executorService;
	}
	
}

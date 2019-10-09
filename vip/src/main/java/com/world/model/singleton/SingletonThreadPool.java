package com.world.model.singleton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>标题: 单例模式的线程池</p>
 * <p>描述: 将需要执行的线程丢人线程池直接调用addJiFenThread</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version 
 */
public class SingletonThreadPool {
	private static SingletonThreadPool singletonJiFen;
	
//	private static Logger log = Logger.getLogger(SingletonThreadPool.class);
	
	/*创建一个可重用固定线程数的线程池*/
	private static ExecutorService singletonJiFenPool; 
	
	private SingletonThreadPool () {
		
	}
	
	/*懒汉线程安全*/
	public static synchronized SingletonThreadPool getInstance() {
		if (null == singletonJiFen) {
			singletonJiFen = new SingletonThreadPool();
			singletonJiFenPool = Executors.newFixedThreadPool(6);
		}
		return singletonJiFen;
	}
	
	public static void addJiFenThread(Thread JifenManage) {
		getInstance();
		singletonJiFenPool.execute(JifenManage);
	}

}

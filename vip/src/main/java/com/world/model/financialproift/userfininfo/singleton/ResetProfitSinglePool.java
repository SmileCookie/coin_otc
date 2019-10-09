package com.world.model.financialproift.userfininfo.singleton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.world.model.financialproift.userfininfo.thread.ResetProfitThread;

public class ResetProfitSinglePool {
	private static ResetProfitSinglePool resetProfitSinglePool;
	
	/*创建一个可重用固定线程数的线程池*/
	private static ExecutorService resetProfitExe;

	private ResetProfitSinglePool() {
		
	}
	
	/*懒汉线程安全*/
	public static synchronized ResetProfitSinglePool getInstance() {
		if (null == resetProfitSinglePool) {
			resetProfitSinglePool = new ResetProfitSinglePool();
			resetProfitExe = Executors.newFixedThreadPool(6);
		}
		return resetProfitSinglePool;
	}
	
	public static void addRestProfitThread(ResetProfitThread resetProfitThread) {
		getInstance();
		resetProfitExe.execute(resetProfitThread);
	}
}

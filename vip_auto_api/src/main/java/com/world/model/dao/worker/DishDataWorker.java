package com.world.model.dao.worker;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.world.DishData;
import com.world.model.dao.task.Worker;
import com.world.model.market.Market;

public class DishDataWorker extends Worker {

	private static final long serialVersionUID = -5589116084131547078L;

	public DishDataWorker(String name, String des) {
		super(name, des);
	}

	public static ExecutorService executorService;
	
	public static ExecutorService getExecutorService(){
		if(executorService == null){
			executorService = Executors.newCachedThreadPool();
		}
		return executorService;
	}


	@Override
	public void run() {
		super.run();
		log.info("开始缓存数据");
		
		Iterator<String> keySet = Market.getMarketsMap().keySet().iterator();
		while(keySet.hasNext()){
			String market = keySet.next();
			dishData(market);
		}
			
		log.info("结束缓存数据");
	}
	
	

	/**
	 * 启用多线程缓存各盘口数据
	 * @param market
	 */
	private  void dishData(final String market){
		try{
			getExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					long t1 = System.currentTimeMillis();
					DishData.initDishData(market);
					long t2 = System.currentTimeMillis();
					log.info("缓存"+market+" 数据耗时："+(t2-t1));
				}
			});
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

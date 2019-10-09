package com.world.model.daos.auto.worker;

import com.world.model.dao.task.Worker;
import com.world.netty.client.DishAllData;

/**
 * @author apple
 *
 */
public class DishAllDataPushWorker extends Worker{

	private static final long serialVersionUID = 1L;

	public DishAllDataPushWorker(String name, String des) {
		super(name, des);
	}
	
	@Override
	public void run() {
		try {
			super.run();
			
			long s1 = System.currentTimeMillis();
			 DishAllData.pushData();
			long s2 = System.currentTimeMillis();
			
			log.info("推送盘口数据耗时：" + (s2 - s1));
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		
	}
	
}

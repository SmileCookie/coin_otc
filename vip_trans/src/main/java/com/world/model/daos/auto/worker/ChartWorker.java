package com.world.model.daos.auto.worker;

import com.world.model.Market;
import com.world.model.dao.task.Worker;
import com.world.model.daos.chart.ChartManager;

/**
 * @author apple
 *
 */
public class ChartWorker extends Worker{

	private static final long serialVersionUID = 1L;

	Market m;
	public ChartWorker(String name, String des,Market m) {
		super(name, des);
		this.m=m;
	}
	
	@Override
	public void run() {
		try {
			super.run();
			
			long s1 = System.currentTimeMillis();
			
			ChartManager.saveToMysqlNew(m);
			
			
			long s2 = System.currentTimeMillis();
			
			log.info("[保存K线数据] 市场:" + m.market + ", 保存K线数据, 耗时：" + (s2 - s1));
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		
	}
	
}

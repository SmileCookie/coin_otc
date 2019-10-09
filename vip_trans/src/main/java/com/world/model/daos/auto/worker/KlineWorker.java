package com.world.model.daos.auto.worker;

import com.world.model.Market;
import com.world.model.dao.task.Worker;
import com.world.model.daos.chart.ChartManager;

/**
 * @author apple
 *
 */
public class KlineWorker extends Worker{

	private static final long serialVersionUID = 1L;

	Market m;
	public KlineWorker(String name, String des,Market m) {
		super(name, des);
		this.m=m;
	}
	
	@Override
	public void run() {
		try {
			super.run();
			
			long s1 = System.currentTimeMillis();
			
			ChartManager.cacheKline(m);
			
			long s2 = System.currentTimeMillis();
			
			log.info("[K线数据缓存] 缓存市场:"+m.market+" K线数据,耗时：" + (s2 - s1));
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		
	}
	
}

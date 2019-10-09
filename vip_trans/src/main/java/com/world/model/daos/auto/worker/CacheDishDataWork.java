//package com.world.model.daos.auto.worker;
//
//import com.world.dish.DishData;
//import com.world.model.Market;
//import com.world.model.dao.task.Worker;
//
//public class CacheDishDataWork extends Worker{
//
//	private static final long serialVersionUID = 1L;
//
//	Market m;
//	public CacheDishDataWork(String name, String des,Market m) {
//		super(name, des);
//		this.m=m;
//	}
//
//	@Override
//	public void run() {
//		try {
//			super.run();
//
//			long s1 = System.currentTimeMillis();
//
//			DishData.initDishData(m.market);
//			long s2 = System.currentTimeMillis();
//			log.info("[档位深度，合并档位深度，交易数据，行情tiker，k线数据] 同步，市场 :" + m.market + "， 耗时：" + (s2 - s1));
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//		}
//
//	}
//
//
//}

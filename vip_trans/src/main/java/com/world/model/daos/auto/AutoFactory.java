package com.world.model.daos.auto;

import com.world.model.Market;
import com.world.model.dao.task.TaskFactory;
import com.world.model.daos.auto.worker.ChartPacketWorker;
import com.world.model.daos.auto.worker.ChartWorker;
import com.world.model.daos.auto.worker.DishDataWorker;
import com.world.model.daos.auto.worker.KlineWorker;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class AutoFactory extends TaskFactory {

	public static void start() {
		Map<String,Market> markets = Market.markets;
		
		
		//转移委托记录
		//work(new EntrustDataTransfer("entrustdata-transfer" , "转移委托数据"), 10*60*1000);
		//转移成交记录
		//work(new TransDataTransfer("transdata-transfer" , "转移成交数据"), 10*60*1000);
		
		Iterator<Entry<String,Market>> iter = markets.entrySet().iterator();
		
		while(iter.hasNext()){
			Market m = iter.next().getValue();
			if(m.listenerOpen){
				work(new DishDataWorker("DishDataWorker" , "盘口缓存数据生成",m), 500);
				
				work(new KlineWorker("KlineWorker" , "K线数据生成缓存",m), 500);
				
				work(new ChartWorker("ChartWorker" , "K线数据保存",m), 50 * 1000);

                work(new ChartPacketWorker("ChartPacketWorker" , "K线数据打包处理",m), 60 * 1000);
				//30s一次 积分定时器
				//work(new GivingNumberWorker("Giving-Number" , "赠送积分定时器",m), 30 * 1000);
			}
			//将memcache 的数据缓存到内存中，每台服务器都启动缓存
//			work(new CacheDishDataWork("CacheDishDataWork" , "缓存盘口数据到内存",m), 2 * 1000);
		}
		
		//将entrust的计划委托单转移到plan_entrust 10分钟一次
		//work(new EntrustDataPlanTransfer("EntrustDataPlanTransfer","将entrust的计划委托单转移到plan_entrust"),10*60*1000);
	
		//work(new DishAllDataPushWorker("DishAllDataPushWorker","推送盘口数据"),1000);
	}

	
}

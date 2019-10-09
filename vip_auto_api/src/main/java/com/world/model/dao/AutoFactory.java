package com.world.model.dao;

import com.world.model.dao.task.TaskFactory;
import com.world.model.dao.worker.DishDataWorker;
import com.world.model.dao.worker.IpListWorker;
import com.world.model.dao.worker.SyncAPIVisitByIpWorker;

public class AutoFactory extends TaskFactory {

	public static void start() {
		work(new IpListWorker("IpListWorker", "更新ip名单"), 60*1000);
		
		work(new SyncAPIVisitByIpWorker("SyncAPIVisitByIpWorker", "同步API IP访问数据"), 20 * 1000);
		
		work(new DishDataWorker("DishDataWorker", "缓存API行情数据"), 1000);
		
	//	work(new SyncAPIUserWorker("SyncAPIUserWorker", "同步API 用户限制信息数据"), 5 * 60 * 1000);
	}

	
}

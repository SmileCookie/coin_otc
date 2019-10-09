package com.world.model.dao.auto;

import com.world.model.dao.task.Worker;
import com.world.util.date.TimeUtil;
import com.world.web.defense.AccessLogFactory;
import com.world.web.defense.IpDefenseDao;

public class DeleteExpireBlackWorker extends Worker {

	private static final long serialVersionUID = -6510435749241398141L;
	private static final int Min = 60*1000;
	private static final int Day = 24*60*60*1000;
	
	private static long lastMinute = System.currentTimeMillis()+Min;
	IpDefenseDao dao = new IpDefenseDao();

	public DeleteExpireBlackWorker(String name, String des) {
		super(name, des);
	}

	@Override
	public void run() {
		
		// 删除过期的黑白名单数据,每5秒执行一次
		dao.deleteExpireData();
		
		// 删除一个天前的数据,每分钟执行一次
		long now = TimeUtil.getNow().getTime();
		if (now > lastMinute) {
			AccessLogFactory.deleteFromTime(1 * Day);
			
			lastMinute += Min;
		}
	}
}

package com.world.timer.auto;

import com.world.model.dao.task.TaskFactory;

public class AutoFactory extends TaskFactory {

	public static void start() {
		//work(new MarketDataWorker("MarketDataWorker" , "行情数据"),8 * 1000);
		//work(new WebSocketWorker("WebSocketWorker" , "档位深度行情数据"),1*1000);
	}

	
}

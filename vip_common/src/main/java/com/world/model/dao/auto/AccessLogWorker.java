package com.world.model.dao.auto;

import com.world.model.dao.task.Worker;
import com.world.web.defense.AccessLogFactory;

public class AccessLogWorker extends Worker {

	private static final long serialVersionUID = -6510435749241398141L;

	public AccessLogWorker(String name, String des) {
		super(name, des);
	}
	
	public AccessLogWorker(String name, String des , boolean auto) {
		super(name, des, auto);
	}

	@Override
	public void run() {
		super.run();
		//log.info("保存系统日志");
		AccessLogFactory.saveFromJVM();
		
	}
}

package com.world.model.dao.auto;

import com.world.config.GlobalConfig;
import com.world.model.dao.task.Worker;
import com.world.web.cache.page.PageCacheFacotry;

public class PageCacheWorker extends Worker {
	static float second=22;//差不多启动后1分钟开始执行第一次，然后每5分钟执行一次
	/**
	 * 页面缓存定时器
	 */
	private static final long serialVersionUID = -6510435749241398141L;

	public PageCacheWorker(String name, String des) {
		super(name, des);
	}

	@Override
	public void run() {
		super.run();
		if(GlobalConfig.outCacheOpen && second > 60 && second % 20 == 0){//60秒钟
			log.info("开始处理页面缓存");
			PageCacheFacotry.doCache();
		}
		second++;
	}
}

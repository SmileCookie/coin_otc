package com.world.web.cache.page;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.file.config.FileConfig;
import com.world.cache.Cache;
import com.world.util.date.TimeUtil;
import com.world.util.request.HttpUtil;
import com.world.web.Pages;
import com.world.web.competence.FunctionManager;

public class PageCacheFacotry {
	static Logger log = Logger.getLogger(Pages.class.getName());
	/****
	 * 执行所有页面缓存
	 * 每隔5s中处理一次页面缓存
	 */
	public static void doCache(){
		Map<String , CachePage> cachePages = FunctionManager.cachePages;
		if(cachePages.size() > 0){
			for(Entry<String, CachePage> e : cachePages.entrySet()){
				CachePage cp = e.getValue();
				String page = Cache.Get("cn" + cp.getUrl());
				//log.info("处理页面：" + cp.getUrl());
				if((page == null) || cp.isExpired()){//未缓存
					
					String url = FileConfig.getValue("cacheUrl") + cp.getUrl();
					if(page == null){
						//log.info("页面：" + url + "从未缓存，第一次缓存");
					}
					if(cp.isExpired()){
						//log.info("页面：" + url + "缓存已过期，重新缓存");
					}
					try {
						String cont = HttpUtil.doGet(url , null);
						if(cont != null){
							Cache.SetObj("cn" + cp.getUrl(), cont , cp.getCacheTimes());
						}
					} catch (IOException e1) {
						log.error(e1.toString(), e1);
					}
					cp.setExpiredDate(TimeUtil.getAfterDate(cp.getCacheTimes()));
				}else{
					//log.info("页面：" + cp.getUrl() + "缓存中...");
				}
			}
		}
	}
}

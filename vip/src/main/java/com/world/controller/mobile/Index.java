package com.world.controller.mobile;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.web.Page;
import com.world.web.action.BaseAction;

public class Index extends BaseAction {

	//Close By suxinjie 一期屏蔽该功能
	//手机APP下载
	//@Page(Viewer = "")
	public void download() {
		String cacheKey = "last_app_";
		JSONObject aApp = new JSONObject();
		JSONObject iApp = new JSONObject();
		String aObj = Cache.Get(cacheKey + "android");
		String iObj = Cache.Get(cacheKey + "ios");
		if (null != aObj && !"".equals(aObj)) {
			aApp = JSONObject.parseObject(aObj);
		}
		if (null != iObj && !"".equals(iObj)) {
			 iApp = JSONObject.parseObject(iObj);
		}
	
		try {
			String ua = request.getHeader("User-Agent") ; 
			if (ua != null) { 
				if (ua.indexOf("iPhone") >-1 || ua.indexOf("iPad") >-1 ) { 
					response.sendRedirect(iApp.getString("url"));
					return ; 
				}else if(ua.indexOf("ndroid") >-1 && ua.indexOf("WebKit") >-1){
					response.sendRedirect(aApp.getString("url"));
					return ; 
				}
			} 
		} catch (IOException e) {
			log.error("内部异常", e);
		} 
		
	}

}
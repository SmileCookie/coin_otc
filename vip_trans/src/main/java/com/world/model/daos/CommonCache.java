package com.world.model.daos;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.api.user.UserManager;
import com.world.cache.Cache;
import com.world.model.dao.cache.BaseCommonCache;

public class CommonCache implements BaseCommonCache{
	private static Logger log = Logger.getLogger(CommonCache.class.getName());
	///用会员资产信息KEY
	public final static String todayInfoKey = "today_info";
	public final static String ltcTodayInfoKey = "ltc_today_info";
	public static UserManager userManager = UserManager.getInstance();
	public final static int saveTime = 60 * 60;
	
	
	/****
	 * 获取当前用户的资金信息
	 * @param userId
	 * @return  用户资金信息数组    0 可用RMB 1冻结RMB   2 可用BTC 3 冻结BTC  4 可用LTC  5 冻结LTC 6 可用BTQ 7 冻结BTQ 8 资产折合RMB
	 */
	public static JSONArray getUserFunds(String userId){
		JSONArray data = (JSONArray) Cache.GetObj(userFundsKey + userId);
		
		return data;
	}
	
}

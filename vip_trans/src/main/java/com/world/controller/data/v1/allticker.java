package com.world.controller.data.v1;

import com.alibaba.fastjson.JSONObject;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.web.Page;
import com.world.web.action.ApiAction;

import java.util.Iterator;
import java.util.Map.Entry;

public class allticker extends ApiAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5824169662289747286L;
	@Page(Viewer = JSON)
	public void index() {
		if(!checkVersion()){
			return;
		}
		
		try {
			JSONObject result = new JSONObject();
			JSONObject allMarketJson = Market.getAllMarkets();
			Iterator<Entry<String,Object>> iter = allMarketJson.entrySet().iterator();
			//String[] currencyArr = new String[]{"btc_cny", "ltc_cny", "eth_cny", "eth_btc"};
			while(iter.hasNext()){
				Entry<String,Object> market = iter.next();
				String marketName = market.getKey();
				String ticker = DishDataCacheService.getTicker(marketName);
				result.put(marketName, JSONObject.parseObject(ticker) );
			}
			
			//在这里加上时间戳返回
			result.put("date", System.currentTimeMillis()+"");
			log.info("返回ticker=" + result.toString());
			
			response.getWriter().write(result.toJSONString());

		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}

}

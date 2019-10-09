package com.world.controller.data.v1;

import com.alibaba.fastjson.JSONObject;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.web.Page;
import com.world.web.action.ApiAction;

public class ticker extends ApiAction {
	
	@Page(Viewer = JSON)
	public void index() {
		if(!checkVersion()){
			return;
		}
		
		try {
			
			String currency = param("currency");
			String marketName = currency;
			response.setContentType("text/javascript");
			Market m = Market.getMarkeByName(marketName);
			if(m==null){
				JSONObject json = new JSONObject();
				json.put("error", "币种参数无效！");
				response.getWriter().write(json.toString());
				return;
			}
			String data = DishDataCacheService.getTicker(marketName);
			//在这里加上时间戳返回
			JSONObject ticker = JSONObject.parseObject(data);
			ticker.put("date", System.currentTimeMillis()+"");
			data = ticker.toJSONString();
			log.info("返回ticker=" + data);
			
			response.getWriter().write(data);

		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}

}

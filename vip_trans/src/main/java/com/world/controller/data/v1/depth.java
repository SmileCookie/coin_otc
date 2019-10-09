package com.world.controller.data.v1;

import com.alibaba.fastjson.JSONObject;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.web.Page;
import com.world.web.action.ApiAction;
import org.apache.commons.lang.StringUtils;

public class depth extends ApiAction {

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
			//暂时取消档位的参数
			int pageSize = 50;
			
			String data = DishDataCacheService.getDishDepthKline50(marketName);
			if(StringUtils.isNotBlank(data)){
				response.getWriter().write(data);
			}
			
		}catch(Exception ex){
			log.error(ex.toString(), ex);
		}
	}

}

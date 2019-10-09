package com.world.controller.data.v1;

import com.alibaba.fastjson.JSONObject;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.web.Page;
import com.world.web.action.ApiAction;

public class trades extends ApiAction {

	@Page(Viewer = JSON)
	public void index() {
		if(!checkVersion()){
			return;
		}
		
		try {
			String currency = param("currency");
			String marketName = currency;

			int since = request.getParameter("since") == null ? 0 : Integer.parseInt(request.getParameter("since"));
			response.setContentType("text/javascript");
			Market m = Market.getMarkeByName(marketName);
			if(m==null){
				JSONObject json = new JSONObject();
				json.put("error", "币种参数无效！");
				response.getWriter().write(json.toString());
				return;
			}
			String data = DishDataCacheService.getSinceTrade(m.market, since);
			response.getWriter().write(data);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}

}
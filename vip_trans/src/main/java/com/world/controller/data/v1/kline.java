package com.world.controller.data.v1;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.web.Page;
import com.world.web.action.ApiAction;

public class kline extends ApiAction {

	@Page(Viewer = JSON)
	public void index() {
		if(!checkVersion()){
			return;
		}
		
		try {
			
			JSONObject data=new JSONObject();
			response.setContentType("text/javascript");
			String currency = param("currency");
			long since = longParam("since");
			
			Market m = Market.getMarket(currency);
			
			if(m==null){
				data.put("error", "币种参数无效！");
				response.getWriter().write(data.toString());
				return;
			}
			String symbol = m.getNumberBiEn();
			String moneyType = m.getExchangeBiEn();
			
			
			int time=intParam("step");
			
			if(time == 0){
				time = 900;
			}
			String type = param("type");
			if(type.equals("1min")){
				time = 60;
			}else if(type.equals("3min")){
				time = 180;
			}else if(type.equals("5min")){
				time = 300;
			}else if(type.equals("15min")){
				time = 900;
			}else if(type.equals("30min")){
				time = 1800;
			}else if(type.equals("1hour")){
				time = 3600;
			}else if(type.equals("2hour")){
				time = 7200;
			}else if(type.equals("4hour")){
				time = 14400;
			}else if(type.equals("6hour")){
				time = 21600;
			}else if(type.equals("12hour")){
				time = 43200;
			}else if(type.equals("1day")){
				time = 86400;
			}else if(type.equals("3day")){
				time = 259200;
			}else if(type.equals("1week")){
				time = 604800;
			}
			
            String klineData = DishDataCacheService.getKline(m.market, String.valueOf(time));

			StringBuffer sb=new StringBuffer();
			sb.append("[");
			sb.append(klineData);
			sb.append("]");
			
			JSONArray arrays = JSONArray.parseArray(sb.toString());
			
			for(Object o : arrays){
				JSONArray ja = (JSONArray) o;
				if(ja!=null && ja.size()>0){
					ja.set(0, ja.getLong(0) * 1000);
					if(ja.size() == 8){
						ja.remove(1);
						ja.remove(1);
					}
				}
			}
			
			JSONArray newArrays = new JSONArray();
			for(Object o : arrays){
				JSONArray ja = (JSONArray) o;
//				log.info("ja.getLong(0)=" + ja.getLongValue(0));
				if(ja.getLongValue(0) >= since){
					newArrays.add( ja );
				}
			}
			arrays = newArrays;
			
			log.info("arrays长度=" + arrays.size());
			
			data.put("data", arrays==null?new JSONArray():arrays);
			data.put("symbol", symbol);
			data.put("moneyType", moneyType);
			response.getWriter().write(data.toString());
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}

}

package com.world.controller.data;

import com.alibaba.fastjson.JSONObject;
import com.world.controller.IndexServer;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.web.Page;
import com.world.web.Pages;
import org.apache.commons.lang.StringUtils;

public class Index extends Pages{

	private IndexServer server = new IndexServer();
	@Page(Viewer = ".xml" , Cache = 60)
	public void index(){

	}

	@Page(Viewer = JSON)
	public void ticker(){
		try{
			String marketName = GetPrama(0);
			if(marketName.length() ==0){
				marketName = "eth_btc";
			 }

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

		}catch(Exception ex){
			log.error(ex.toString(), ex);
		}

	}

	@Page
	public void depth(){
		try{
			String market = GetPrama(0);
			int pageSize = intParam("pageSize");

			if(market.length() ==0){
				 market = "eth_btc";
			 }
			 Market m=Market.getMarkeByName(market);
			 if(m==null){
				JSONObject json = new JSONObject();
				json.put("error", "币种参数无效！");
				response.getWriter().write(json.toString());
				return;
			 }
			 response.setContentType("text/javascript");

			 String data = DishDataCacheService.getDishDepthKline50(market);
			if(StringUtils.isNotBlank(data)){
				response.getWriter().write(data);
			}
		}catch(Exception ex){
			log.error(ex.toString(), ex);
		}

	}

	@Page
	public void stock(){
		trades();
	}

	@Page
	public void getTrades(){
		trades();
	}

	@Page
	public void trades(){
		/**
		 * 获取委托和交易历史记录数据
		 */
		 try{
			 	String market = GetPrama(0);
			 	if(market.length() ==0){
				 	market = "eth_btc";
			 	}
			 	int since=request.getParameter("since")==null?0:Integer.parseInt(request.getParameter("since"));
			 	Market m=Market.getMarkeByName(market);
				response.setContentType("text/javascript");
				if(m==null){
					JSONObject json = new JSONObject();
					json.put("error", "币种参数无效！");
					response.getWriter().write(json.toString());
					return;
				}
				String data = DishDataCacheService.getSinceTrade(m.market, since);
				response.getWriter().write(data);
			}
			catch(Exception ex){
			}
		}

}

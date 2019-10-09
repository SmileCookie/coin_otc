package com.world.web.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.DishData;
import com.world.model.market.Market;
import com.world.web.Pages;

public class ApiAction extends Pages{
	
	
	private static Map<String,Long> klineReqMap = new HashMap<String,Long>();
	

	
	/**
	 * 检测调用kline的频繁度。如果小于1秒则返回false.
	 * @param ip
	 * @return
	 */
	public boolean checkKlineRequest(String ip){
		Long lastTime = klineReqMap.get(ip);
		if(lastTime == null || lastTime.longValue()==0){
			klineReqMap.put(ip, System.currentTimeMillis());
			return true;
		}else{
			long cha = System.currentTimeMillis()- lastTime.longValue();
			//System.err.println(ip+" 最后一次调用时间："+lastTime +" 当前时间："+ System.currentTimeMillis()+"  间隔："+cha+"毫秒");
			if(cha<1000){//距离上一次请求小于一秒
				return false;
			}else{
				klineReqMap.put(ip, System.currentTimeMillis());
				return true;
			}
		}
	}
	
	/**
	 * 根据市场名称获取ticker 数据
	 * @param marketName 币种市场：btcdefault/etcdefault/ethdefault
	 * @return json 字符串
	 * @author zhanglinbo 20161012
	 */
	public String getTickerData(String marketName){
		String key = "dish_ticker_"+marketName;
		JSONObject ticker = DishData.map.get(key);
		//在这里加上时间戳返回
		ticker.put("date", System.currentTimeMillis()+"");
		String data = ticker.toJSONString();
		return data;
	}
	
	/**
	 * 根据市场名称获取市场深度默认50档 数据
	 * @param marketName 币种市场：btcdefault/etcdefault/ethdefault
	 * @param size 获取市场档位  1-50
	 * @param merge 合并深度 btc(1, 0.1) ltc(0.5,0.3,0.1) eth(0.5,0.3,0.1) etc(0.3,0.1)（合并深度）
	 * @return json 字符串
	 * @author zhanglinbo 20161012
	 */
	public String getDepthData(String marketName,int size,String merge){
		//深度数据
		JSONObject data = new JSONObject();
		JSONArray  asksArr = new JSONArray();
		JSONArray  bidsArr = new JSONArray();
		
		/*if(StringUtils.isNotBlank(merge)){//深度合并数据
			String key = "dish_depth_"+merge.replace(".", "")+"_"+marketName;
			JSONObject depth = DishData.map.get(key);
			JSONArray orgAsksArr = depth.getJSONArray("listUp");//卖盘
			JSONArray orgBidsArr = depth.getJSONArray("listDown");//买盘
			if(orgAsksArr!=null && orgAsksArr.size()>0){
				for(int i=orgAsksArr.size()-1;i>=0;i--){//卖单是倒序排序
					JSONArray arr = new JSONArray();
					arr.add(orgAsksArr.getJSONArray(i).get(0));
					arr.add(orgAsksArr.getJSONArray(i).get(1));
					asksArr.add(arr);
				}
			}
			
			if(orgBidsArr!=null && orgBidsArr.size()>0){
				for(int i=0;i<orgBidsArr.size();i++){//
					JSONArray arr = new JSONArray();
					arr.add(orgBidsArr.getJSONArray(i).get(0));
					arr.add(orgBidsArr.getJSONArray(i).get(1));
					bidsArr.add(arr);
				}
			}
			data.put("asks", asksArr);
			data.put("bids", bidsArr);
			data.put("timestamp", depth.get("timestamp"));
		}else{*/
			String key = "dish_depth_"+marketName;
			JSONObject depth = DishData.map.get(key);
			if(size>0){//指定获取长度 1~50档
				size = size>50?50:size;
				JSONArray orgAsksArr =  depth.getJSONArray("asks");
				JSONArray orgBidsArr =  depth.getJSONArray("bids");
				if(orgAsksArr!=null && orgAsksArr.size()>0){
					int  cursor = 1;//计数器
					int start  = orgAsksArr.size() - size;
					if(start < 0) start =0;//获取数据起点 根据总长度和size决定
					for(int i=start;i<orgAsksArr.size();i++){
						asksArr.add(orgAsksArr.get(i));
						if(cursor>=size){
							break;
						}
						cursor++;
					}
				}
				if(orgBidsArr!=null && orgBidsArr.size()>0){
					int  cursor = 1;//计数器
					for(int i=0;i<orgBidsArr.size();i++){
						bidsArr.add(orgBidsArr.get(i));
						if(cursor>=size){
							break;
						}
						cursor++;
					}
				}
				
				data.put("asks", asksArr);
				data.put("bids", bidsArr);
				data.put("timestamp", depth.get("timestamp"));
			}else{
				data.put("asks", depth.get("asks"));
				data.put("bids", depth.get("bids"));
				data.put("timestamp", depth.get("timestamp"));
			}
		/*}*/
		
		return data.toJSONString();
	}
	
	/**
	 * 根据市场名称获取市场 最后成交交易数据   默认50档 数据
	 * @param marketName 币种市场：btcdefault/etcdefault/ethdefault
	 * @param since 开始时间戳 14652002500
	 * @return json 字符串
	 * @author zhanglinbo 20161012
	 */
	public String getTradesData(String marketName,int since){
		 
		String key = "dish_trades_"+marketName;
		 JSONObject tradesDataObj  = DishData.map.get(key);
		 JSONArray newArrays = new JSONArray();
		 if(since>0){
			 JSONArray arr = tradesDataObj.getJSONArray("data");
			 for(Object o : arr){
				 JSONObject jo = (JSONObject) o;
					if(jo.getLongValue("tid") >= since){	//时间戳大于或等于since才能返回
						newArrays.add( jo );
					}
				}
		 }else{
			 newArrays = tradesDataObj.getJSONArray("data");
		 }
		 
		 return newArrays.toJSONString();
	}
	
	/**
	 * 获取市场k线数据，最大默认获取1000条数据
	 * @param currency  币种市场：btcdefault/etcdefault/ethdefault
	 * @param type k线类型  1min/3min/5min/15min/30min..
	 * @param size 获取数据长度
	 * @param since 时间戳，从哪个时间戳节点后开始获取
	 * @return json 字符串
	 * @author zhanglinbo 201601012
	 */
	public String getKlineData(String currency,String type,int size,long since){
		
		JSONObject data=new JSONObject();
		
		
		String marketName = currency;
		
		JSONObject  market = Market.getMarketByName(marketName);
		
		if(market==null){
			marketName = Market.getDefMarketName();
			market = Market.getMarketByName(marketName);
		}
		
		
		String symbol = market.getString("numberBiEn");
		String moneyType = market.getString("exchangeBiEn");;
		
		
		if(StringUtils.isEmpty(type)){	//默认15分钟
			type = "15min";
		}
		
        String key = "dish_kline_"+marketName+"_"+type;
		JSONObject klineDataObj  = DishData.map.get(key);
		JSONArray arrays = klineDataObj.getJSONArray("data");
		if(arrays==null){
			return "{\"data\":\"null\"}";
		}
		if(size<=0 || size>=1000){	//size最大是1000，返回的数据最多只有1000条
			size=1000;
		}
		int start = 0;//开始获取数据值
		if(arrays.size()>size){
			start = arrays.size() - size; 
		}
		
		int i=0;//总循环标记
		JSONArray newArrays = new JSONArray();
		for(Object o : arrays){
			JSONArray ja = (JSONArray) o;
			i++;
			if(i>start){
				if(ja.getLongValue(0) >= since){	//时间戳大于或等于since才能返回
					newArrays.add( ja );
				}
			}else{
				continue;
			}
			
		}
		data.put("data", newArrays);
		data.put("symbol", symbol);
		data.put("moneyType", moneyType);
		
		return data.toJSONString();
	}
	
	/**
	 * 验证市场深度是否正确
	 * @param marketName btcdefault,ltcdefault,ethdefault,etcdefault,btqdefault
	 * @param merge btc(1, 0.1) ltc(0.5,0.3,0.1) eth(0.5,0.3,0.1) etc(0.3,0.1)
	 * @return 验证结果
	 */
	public boolean validateMerge(String marketName,String merge){
		if(marketName.equals("btcdefault")){
			if(merge.equals("1") || merge.equals("0.1")){
				return true;
			}
		}else if(marketName.equals("ltcdefault") || marketName.equals("ethdefault")){
			if(merge.equals("0.5") || merge.equals("0.3")|| merge.equals("0.1")){
				return true;
			}
		}else if(marketName.equals("etcdefault")){
			if(merge.equals("0.3") || merge.equals("0.1")){
				return true;
			}
		}
		
		return false;
	}
}

package com.world;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;

public class DishData {

	
	private static String[][] periods;
	public static Logger log = Logger.getLogger(DishData.class.getName());

	
	static {
		
		periods = new String[][] { {"1week","604800"}, {"3day","259200"}, {"1day","86400"}, {"12hour","43200"}, {"6hour","21600"},
			{"4hour","14400"}, {"2hour","7200"}, {"1hour","3600"}, {"30min","1800"},
				{"15min","900"}, {"5min","300"}, {"3min","180"}, {"1min","60"} };
		
	}

	
	// 深度数据
	public static java.util.concurrent.ConcurrentMap<String, JSONObject> map = new ConcurrentHashMap<String, JSONObject>();

	/**
	 * 初始化K线盘口数据
	 * @param market 币种 btcdefault\ltcdefault\ethdefault
	 */
	public static void initDishData(String market) {
			try {
					//档位深度
					initDepthData(market);
					//交易数据
					initTrade(market);
					//行情ticker
					initTicker(market);
					//k线数据
					initKlineData(market);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	
	

	
	/**
	 * 初始化档位深度盘口数据
	 * @param market 币种市场
	 */
	private static void initDepthData(String market){
		try {
				JSONObject json = new JSONObject();
				String key = "dish_depth_"+market;
				String depthData = Cache.Get(market+"_datachart50Outer");//
				if(StringUtils.isNotBlank(depthData) && depthData.startsWith("{")){
					json = JSONObject.parseObject(depthData);
				}
				DishData.map.put(key, json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * kx线交易数据
	 * @param market
	 */
	private static void initTrade(String market){
		
			try {
				String key = "dish_trades_"+market;
				String tradesData =  Cache.Get(market+"_OuderTrade_0");;
				if(tradesData!=null){
					JSONObject tradeJson = new JSONObject();
					if(tradesData.startsWith("[")){
						JSONArray arr = JSONObject.parseArray(tradesData);
						tradeJson.put("data", arr);
					}else if(tradesData.startsWith("{")){
						tradeJson = JSONObject.parseObject(tradesData);
					}
					 
					DishData.map.put(key, tradeJson);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}
	
	/**
	 * 初始化行情ticker信息
	 * @param market
	 */
	private static void initTicker(String market){
		String key = "dish_ticker_"+market;
		
		String datas =Cache.Get(market+"_hotdata2");
		if(datas!=null){
			JSONObject tickerJson = new JSONObject();
			 if(datas.startsWith("{")){
				 tickerJson = JSONObject.parseObject(datas);
			}
			 
			DishData.map.put(key, tickerJson);
		}
	}
	
	/**
	 * k线数据
	 * @param klineData 返回K线数据对象
	 * @param coints 币种市场
	 */
	private static void initKlineData(String market){
		try {
			for(int i=0;i<periods.length;i++){
				String key = "dish_kline_"+market+"_"+periods[i][0];
				String time = periods[i][1];//时间
				String name=market+"_getchar"+time;
		        String klineData=Cache.Get(name);
				if(klineData!=null ){
					klineData = "["+klineData+"]";
					JSONObject json = new JSONObject();
					JSONArray arr = JSONObject.parseArray(klineData);
					json.put("data", arr);
					DishData.map.put(key, json);
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}

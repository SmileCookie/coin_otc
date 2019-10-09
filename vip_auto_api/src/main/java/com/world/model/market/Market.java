package com.world.model.market;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.config.json.JsonConfig;
import com.world.util.request.HttpUtil;
import com.world.web.action.Action;

public class Market {
	
	private static Map<String,JSONObject> marketsMap = new HashMap<String,JSONObject>();
	
	private static String defMarketName = "";//默认市场名称 
	
	static{
		try{
			 JSONArray jas = (JSONArray) JsonConfig.getValue("markets");
			 if(jas != null && jas.size() > 0){
				 for(Object o : jas){
					 JSONObject jo = (JSONObject) o;
					 marketsMap.put(jo.getString("market"), jo);
				 }
				
			 }
			}catch(Exception e){
				e.printStackTrace();
		}
	}

	
	/**
	 * 获取盘口配置的市场信息 存放在静态变量map中，key:市场名称如 etc_btc value:JSONObject
	 * @author zhanglinbo  20160929
	 * @return map
	 */
	public static Map<String,JSONObject> getMarketsMap(){
		if(marketsMap.isEmpty()){
			JSONObject markets = null;
			try {
				if(markets==null){
					JSONObject json = HttpUtil.getJson(Action.TRANS_DOMAIN+"/getAllMarket", null, 3000, 3000,false);
					markets = json.getJSONObject("datas");
				}
				
				if(markets!=null ){
					Iterator<Entry<String, Object>> iter = markets.entrySet().iterator();
					while(iter.hasNext()){
						Entry<String, Object> entry = iter.next();
						JSONObject m = (JSONObject)entry.getValue();
						marketsMap.put(entry.getKey(), m);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		if(marketsMap!=null){
			int defNo = 0;
			for(Entry<String, JSONObject> entry :marketsMap.entrySet() ){
				JSONObject m = entry.getValue();
				if(defNo==0 || defNo>m.getIntValue("serNum")){//设置序号最小第一个为默认市场
					defMarketName = entry.getKey();
					defNo = m.getIntValue("serNum");
				}
			}
		}
		
		return marketsMap;
	}
	
	/**
	 * 根据市场名称获取市场详细配置信息对象，返回JSON对象
	 * @param name 市场名称 如:etc_btc
	 * @author zhanglinbo 20160929
	 * @return JOSN对象
	 */
	public static JSONObject getMarketByName(String name){
		if(StringUtils.isBlank(name)){
			return null;
		}
		name = name.toLowerCase();//转为小写
		JSONObject market = null;
			market = getMarketsMap().get(name);
		return market;
	}
	
	/**
	 * 获取默认市场名称
	 * @author zhanglinbo 20160929
	 * @return 默认市场 字符串
	 */
	public static String getDefMarketName(){
		if(StringUtils.isEmpty(defMarketName)){
			getMarketsMap();
		}
		return defMarketName;
	}
	
	
}

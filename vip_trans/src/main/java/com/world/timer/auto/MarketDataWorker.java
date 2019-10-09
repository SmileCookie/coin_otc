package com.world.timer.auto;

import org.apache.commons.lang.StringUtils;

import com.api.util.http.HttpUtil;
import com.tenstar.HTTPTcp;
import com.tenstar.RecordMessage;
import com.world.cache.Cache;
import com.world.controller.IndexServer;
import com.world.model.Market;
import com.world.model.dao.task.Worker;

import net.sf.json.JSONObject;

public class MarketDataWorker extends Worker{
	private static final long serialVersionUID = -5589116084131547078L;

	public MarketDataWorker(String name, String des) {
		super(name, des);
	}
	
	@Override
	public void run() {
		super.run();
		//getDataToMem();
	}
	
	public static final String marketDataKey = "market_data_key_3union";
	
	
	public static synchronized void getDataToMem(){
		//log.info(Cache.Get(marketDataKey));
		JSONObject rtn = new JSONObject();
		IndexServer server = new IndexServer();
		try {
			//当前BTC行情                    btc_cny_hotdata2
			 String data=Cache.Get("btc_cny_hotdata2");
			 if(data==null){
				 
				  Market m=Market.getMarkeByName("btc_cny");
				  RecordMessage myObj = new RecordMessage();
				  RecordMessage rtn2 = server.getticker(myObj,m);
				  data = rtn2.getMessage();
			 }
			 
			 rtn = JSONObject.fromObject(data);
			 if(rtn == null){
				 return;
			 }
			 
			 //JUA.COM 累计理财总额
			 String jua = HttpUtil.doPost("https://www.jua.com/getBtcTotalStorage?callback=?", null , 1000, 2000);
//			 String jua = HttpUtil.doGet("https://www.jua.com/getBtcTotalStorage?callback=?", null);
			 if(StringUtils.isNotEmpty(jua)){
					jua = jua.substring(2);
					jua = jua.substring(0, jua.length() - 1);
			 }else{
				 jua = "{\"des\" : \"\" , \"isSuc\" : true  , \"datas\" : [0,0,0,0]}";
			 }
			 JSONObject json = JSONObject.fromObject(jua);
			 rtn.element("finance", json == null ? 0 : json.getJSONArray("datas").get(0));
			 String rate = HttpUtil.doPost("https://www.jua.com/getTodayRate?coint=btc&callback=?", null , 1000, 2000);
//			 String rate = HttpUtil.doGet("https://www.jua.com/getTodayRate?coint=btc&callback=?", null);
			 if(StringUtils.isNotEmpty(rate)){
				 rate = rate.substring(2);
				 rate = rate.substring(0, rate.length() - 1);
			 }else{
				 return;
			 }
			 JSONObject json3 = JSONObject.fromObject(rate);
			 rtn.element("rate", json == null ? 0 : json3.getJSONArray("datas").get(0));
			 
			 //BW.COM 当前算力
			 String bw = HttpUtil.doPost("https://www.bw.com/totalForce", null , 1000, 2000);
//			 String bw = HttpUtil.doGet("https://www.bw.com/totalForce", null);
			 if(StringUtils.isEmpty(bw)) {
				 rtn.element("force" , 0);
			 }else{
				 JSONObject json2 = JSONObject.fromObject(bw);
				 rtn.element("force", json2 == null ? 0 : json2.getJSONObject("datas").getDouble("force"));
			 }
			 
			 
			 
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		Cache.Set(marketDataKey, rtn.toString(), 60);
	}
}

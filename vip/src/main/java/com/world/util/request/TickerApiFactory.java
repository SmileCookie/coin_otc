package com.world.util.request;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.model.entity.trace.Trade;

public class TickerApiFactory {
	
	public static Logger log = Logger.getLogger(TickerApiFactory.class.getName());
	
	/**
	 * 获取最新价格
	 * @return
	 */
	public static BigDecimal getLastPrice(Trade trade){
		try {
			if(StringUtils.isEmpty(trade.getName())){
				return BigDecimal.ZERO;
			}
			Ticker ticker = getTicker(trade);
			if(ticker == null || ticker.getLast().compareTo(BigDecimal.ZERO) <0){
				return BigDecimal.ZERO;
			} else {
				return ticker.getLast();
			}
		} catch (IOException e) {
			log.error(e.toString(), e);
		}
		return BigDecimal.ZERO;
	}
	
	/**
	 * 获取新最行情
	 * @param web
	 * @return
	 * @throws IOException
	 */
	public static Ticker getTicker(Trade trade) throws IOException{
		String key = trade.getSymbol()+ "_ticker";
		Ticker ticker = (Ticker) Cache.GetObj(key);
		if(ticker == null){
			String tickerUrl = trade.getTickerUrl();
			
			try {
				String result = HttpUtil.doGet(tickerUrl, null);
				if(result.startsWith("{")){
					JSONObject json = JSONObject.parseObject(result);
					if(trade.getName().equalsIgnoreCase("chbtc")){
						ticker = getChbtcTicker(json);
					}else if(trade.getName().equalsIgnoreCase("bter")){
						ticker = getBterTicker(json);
					}
				}
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
				
			if(ticker != null){
				Cache.SetObj(key, ticker , 90);
			}else{
				return null;
			}
		}
		
		return ticker;
	}
	
	private static Ticker getChbtcTicker(JSONObject json){
		Ticker ticker = new Ticker();
		if(json != null){
			json = json.getJSONObject("ticker");
			ticker.setLast(json.getBigDecimal("last"));
			ticker.setBuy(json.getBigDecimal("buy"));
			ticker.setSell(json.getBigDecimal("sell"));
			ticker.setHigh(json.getBigDecimal("high"));
			ticker.setLow(json.getBigDecimal("low"));
			ticker.setVol(json.getBigDecimal("vol"));
		}
		return ticker;
	}
	
	private static Ticker getBterTicker(JSONObject json){
		Ticker ticker = new Ticker();
		if(json != null){
			ticker.setLast(json.getBigDecimal("last"));
			ticker.setBuy(json.getBigDecimal("buy"));
			ticker.setSell(json.getBigDecimal("sell"));
			ticker.setHigh(json.getBigDecimal("high"));
			ticker.setLow(json.getBigDecimal("low"));
			ticker.setVol(json.getBigDecimal("vol_btc"));
		}
		return ticker;
	}
	
}

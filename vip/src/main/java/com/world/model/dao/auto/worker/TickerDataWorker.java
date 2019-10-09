package com.world.model.dao.auto.worker;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.data.database.DatabasesUtil;
import com.world.model.dao.task.Worker;
import com.world.model.dao.trace.TradeDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.trace.Trade;
import com.world.util.date.TimeUtil;
import com.world.util.request.TickerApiFactory;

public class TickerDataWorker extends Worker {
	private static final long serialVersionUID = 654413993790787136L;
	private static TradeDao tradeDao = new TradeDao();
	
	private static String marketNoPriceKey = "market_np_key";

	public TickerDataWorker(String name, String des, boolean autoReplace) {
		this.name = name;
		this.des = des;
		this.autoReplace = autoReplace;
	}

	@Override
	public void run() {
		List<Trade> list = tradeDao.getListForTicker();
		JSONObject prices = (JSONObject) Cache.GetObj(marketNoPriceKey);
		if(prices == null){
			prices = new JSONObject();
		}
		for (Trade trade : list) {
			execute(trade, prices);
		}
		resetCache(prices);
	}

	private void execute(Trade trade, JSONObject prices) {
		BigDecimal lastPrice = TickerApiFactory.getLastPrice(trade);
		try {
			if(lastPrice.compareTo(BigDecimal.ZERO) > 0 && lastPrice.compareTo(trade.getLastPrice()) != 0){
				tradeDao.update("UPDATE trade SET lastPrice = ?, lastTime = ? WHERE id = ?", new Object[]{lastPrice, TimeUtil.getNow(), trade.getId()});
			}
			CoinProps coint = DatabasesUtil.coinProps(trade.getFundsType());
			if(lastPrice.compareTo(BigDecimal.ZERO) > 0){
				prices.put(coint.getStag(), lastPrice);
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

	private static void resetCache(JSONObject prices){
		Cache.SetObj(marketNoPriceKey, prices, 60*5);
	}
	
	private static JSONObject refresh(){
		List<Trade> list = tradeDao.getListForTicker();
		JSONObject prices = new JSONObject();
		for (Trade trade : list) {
			CoinProps coint = DatabasesUtil.coinProps(trade.getFundsType());
			prices.put(coint.getStag(), trade.getLastPrice());
		}
		resetCache(prices);
		return prices;
	}
	
	public static JSONObject getCachePricesByTicker(){
		JSONObject prices = (JSONObject) Cache.GetObj(marketNoPriceKey);
		if(prices == null){
			prices = refresh();
		}
		return prices;
	}
}

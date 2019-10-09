package com.world.model.loan.worker;

import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.model.dao.task.Worker;
import com.world.model.entity.Market;
import com.world.model.loan.MarketPrices;
import com.world.util.string.StringUtil;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PriceWorker extends Worker{
	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(PriceWorker.class.getName());
	public PriceWorker(String name , String des){
		this.name = name;
		this.des = des;
	}

	public static Map<String,String> initPriceMap = new HashMap<String,String>();
	static{
		initPriceMap.put("eth_usdt","275.61");
		initPriceMap.put("ltc_usdt","61.838");
		initPriceMap.put("btc_usdt","3864.5");
		initPriceMap.put("ltc_btc","0.01599");
		initPriceMap.put("eth_btc","0.071397");
		initPriceMap.put("zec_btc","0.0513");
		initPriceMap.put("etc_btc","0.0036976");
		initPriceMap.put("gbc_btc","0.0001259");
//		initPriceMap.put("gbc_usdt","0.4865");
		initPriceMap.put("dash_btc","0.07664");

//		initPriceMap.put("btg_usdt","1");
//		initPriceMap.put("neo_usdt","1");
//		initPriceMap.put("bcd_usdt","1");
//		initPriceMap.put("omg_usdt","1");
//		initPriceMap.put("bth_usdt","1");
//		initPriceMap.put("sbtc_usdt","1");
//		initPriceMap.put("bcx_usdt","1");
//		initPriceMap.put("btp_usdt","1");
//		initPriceMap.put("etf_usdt","1");
//		initPriceMap.put("lbtc_usdt","1");
//		initPriceMap.put("btf_usdt","1");
//		initPriceMap.put("btw_usdt","1");
//		initPriceMap.put("god_usdt","1");
//		initPriceMap.put("bifi_usdt","1");
//		initPriceMap.put("bat_usdt","1");
//		initPriceMap.put("lrc_usdt","1");
//		initPriceMap.put("snt_usdt","1");
//		initPriceMap.put("rep_usdt","1");
//		initPriceMap.put("elf_usdt","1");
//		initPriceMap.put("qtum_usdt","1");
//		initPriceMap.put("usdt_usdt","1");

	}
	public void run() {
		super.run();
		try {
			//产品重造
			LoanAutoFactory.priceProduct.version = System.currentTimeMillis();

			JSONObject prices = MarketPrices.get();
			/*start by xzhang 20170913 当本系统没有当前价格时，则取外网价格*/
			log.info("debug price before - get market price : " + prices.toJSONString());


			Set<String> marketNames = Market.getAllMarketName();
			for (String name : marketNames) {
				if(prices.getBigDecimal(name).compareTo(BigDecimal.ONE) == 0){
					prices.put(name, new BigDecimal(gtetLastPrice(name)));
				}
			}
			log.info("debug price after -  : " + prices.toJSONString());

			LoanAutoFactory.priceProduct.prices = prices;

			log.info("跟进最新价格到虚拟机..." + MarketPrices.lastPricesToString(prices));
		} catch (Exception e) {///保证线程不死
			log.error(e.toString(), e);
		}
	}
	/*start by xzhang 20170913 当本系统没有当前价格时，则取外网价格*/
	//外网没有价格，默认0
	public static String gtetLastPrice(String symbol) {
		String bitfinexMarkets = Cache.Get("market_price_bitfinex");
		if (StringUtil.exist(bitfinexMarkets)) {
			JSONObject jsonObject = JSONObject.parseObject(bitfinexMarkets);
			if (jsonObject.containsKey(symbol.toLowerCase())) {
				return jsonObject.getJSONObject(symbol.toLowerCase()).getString("last");
			}
		}
		if(StringUtil.exist(initPriceMap.get(symbol))){
			return initPriceMap.get(symbol);
		}
		return "1";
	}
	/*end*/
}


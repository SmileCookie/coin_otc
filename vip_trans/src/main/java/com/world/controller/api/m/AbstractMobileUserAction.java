package com.world.controller.api.m;


import com.Lan;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.api.common.SystemCode;
import com.api.util.http.HttpUtil;
import com.tenstar.*;
import com.tenstar.HTTPTcp;
import com.tenstar.Message;
import com.tenstar.MessageCancle;
import com.tenstar.RecordMessage;
import com.world.cache.Cache;
import com.world.controller.IndexServer;
import com.world.controller.Line;
import com.world.data.mysql.Query;
import com.world.dish.DishDataCacheService;
import com.world.model.CacheString;
import com.world.model.Market;
import com.world.model.entity.LegalTenderType;
import com.world.model.entitys.entrust.EntrustBean;
import com.world.model.entitys.entrust.EntrustDao;
import com.world.model.entitys.entrust.PlanEntrustBean;
import com.world.model.entitys.entrust.PlanEntrustDao;
import com.world.model.entitys.record.TransRecord;
import com.world.util.CommonUtil;
import com.world.util.DigitalUtil;
import com.world.util.sign.RSACoder;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.MobileUserAction;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractMobileUserAction extends MobileUserAction {

	protected IndexServer server = new IndexServer(lan);
	public final static String userOperation = "userOperation_";

	public static final String MARKETSETS_KEY = "marketSets_";

	private final String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJG14+94DEgzyd6G8+Ue+lpLKK9uIftpSZ7wvnX3jtw+6SUKldkvL1mYq9W8qIJD7w5t3YQIkVoWIlm5Eba5NcDYgfDC/QnYyr9zfDthlJECvQ8TC0wjy9cOtCC4FntewsqmGxLjTA17Zn0RJpsqXvNFjZEinR6IawvnlhPKJ/IwIDAQAB";
	private final String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIkbXj73gMSDPJ3obz5R76Wksor24h+2lJnvC+dfeO3D7pJQqV2S8vWZir1byogkPvDm3dhAiRWhYiWbkRtrk1wNiB8ML9CdjKv3N8O2GUkQK9DxMLTCPL1w60ILgWe17CyqYbEuNMDXtmfREmmype80WNkSKdHohrC+eWE8on8jAgMBAAECgYA0EsPx0FkEyf9szgnqNn55gBsbsnbhqpu391WjE9y/GUp0IdShqJ1EcIOENeevW2zYXCbn6mLmZzv6oqIzMuFtZ4GGbHvTsMNGtoBJsvIjV36FjdiXU7FAGqtUI+I/kFBvxFuKcil6JBFGKheQle2segoB9hAsKGoUSayAE5yjqQJBAMJllnMTMeuomhZxSQfuq4Ke3BAGGbbUfcCYnCoK1y9LBe3qXmynWYnc2caIHgbMdDiGYcTm1XOZ5lR/a2GP4HUCQQC0jiUFKWmWkx+MgverbA4QBoh+ff5M95c5T/8W2QbrUW7DV++aW4y+4D92Ei6nFcF1V8SSMxgDmqiz6pOqS243AkEAl6vlR6GZWHGyz4HR5kN8Q6yorEPmOjTubJ9lcJQGspqJZMhwpbuoa50JuRGow8svfo6yp4smzUwtXo4P/Q3hpQJAC8AIZrqYNYVjkzhet9gzXhWewmSerRGb1M4A8tKy4ZOOGsZZQHlewnlDiAKM6LDAw0sv7rfGg02IVxUYAQghpwJABiNcbBh7MnDfGaRZzE7SX/UwRn7OmGY7lFMBWadiQ/R5pKpdPrVmwdlTsefzb1acYy41LQCFCPKxVjv7sUduXQ==";
	Line line = new Line();
	private static CacheString topallStr = null;
	PlanEntrustDao planEntrustDao = new PlanEntrustDao();
	EntrustDao entrustDao = new EntrustDao();
//	UserVipLevelDao userVipLevelDao = new UserVipLevelDao();

	/**
	 * 5.3.1  获取行情
	 *
	 * - **请求参数**

	 | 参数名         | 类型     | 是否必须 | 描述      |
	 | :---------- | :----- | :--- | :------ |
	 | exchangeType | String | 是    | 市场类型（兑换货币类型）：<br>CNY：人民币 BTC：比特币，LTC：莱特币 |
	 | currencyTypes | json数组       | 否    | 货币类型数组（不输入表示获取全部）：<br> 例如 ['BTC', 'ETH', 'LTC', 'DAO'] |
	 | step | int       | 否    | k线类型秒数：1分钟K线：1*60 ，3分钟K线：3*60 ，5分钟K线 ：5*60，以此类推 ，如果不传或传0 则返回空数组|
	 | size | int     | 否    |  指定返回K线最新数据条数 |


	 - **返回结果**

	 | 参数名     | 类型     | 是否必须 | 示例      | 描述   |
	 | :------ | :----- | :--- | :------ | :--- |
	 | markets | MarketData[] | 是    | | 行情数据，具体看返回示例json结构  |
	 */
	@Page(Viewer = JSON)
	public void getTickerArray() {
		setLan();

		// 如果用户不传userId,则默认法币类型为 美元
		// 如果用户传了userId,则对用户进行鉴权,取memcached中的法币信息
		String legalTender = "usd_$";
		String cachePriceBtc = "0";
		String cachePriceUsdc = "0";

//		String userId = param("userId");
		String userId = userIdStr();
		if("0".equals(userId)){
			userId = "";
		}
		String token = param("token");

		/** start 20170901 xzhang  新增前端传递货币折算币种,兼容IOS折算币种老版本，优先取传递，其次去缓存。最后去默认*/
		String legal_Tender = param("legal_tender");
		String legalTenderParam = "";
		if(StringUtils.isNotBlank(legal_Tender)){
			if (!LegalTenderType.existKey(legal_Tender)) {
				json(SystemCode.code_1001, "不支持的货币类型");
				return;
			}
			LegalTenderType legalTenderType = LegalTenderType.valueOf(legal_Tender);
			legalTenderParam = legalTenderType.getKey() + "_" + legalTenderType.getValue();
		}
		if(StringUtil.exist(legalTenderParam)){
			legalTender = legalTenderParam;
		}else if (StringUtil.exist(userId)) {
			if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
				json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
				return;
			}
			String legalTenderCache = Cache.Get("user_legal_tender_" + userId);
			legalTender = StringUtil.exist(legalTenderCache) ? legalTenderCache : legalTender;
		}
		/**end **/
		cachePriceBtc = Cache.Get("btc_" + legalTender.split("_")[0].toLowerCase());
		cachePriceUsdc = Cache.Get("usdt_" + legalTender.split("_")[0].toLowerCase());

		try {
			String exchangeType = param("exchangeType").toLowerCase();
			String currencyTypes = request.getParameter("currencyTypes");
			int time = intParam("step");// k线图时间类型
			int size = intParam("size");// 获取k线数据数量
			JSONArray currencyArr = new JSONArray();
			/*if (StringUtils.isBlank(exchangeType)) {
				json(SystemCode.code_1001, L("exchangeType参数不能为空！"));
			}*/

			if (StringUtils.isBlank(currencyTypes)) {// 货币类型数组（不输入表示获取全部）
				Map<String, Market> map = CommonUtil.sortMapByValue(Market.markets);
				Iterator<Map.Entry<String, Market>> iter = map.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, Market> entry = iter.next();
					Market m = entry.getValue();
					if (StringUtils.isNotBlank(exchangeType) && exchangeType.equalsIgnoreCase(m.getExchangeBiEn())) {
						currencyArr.add(m.getNumberBiEn().toLowerCase()+"_"+exchangeType);
					}else if(StringUtils.isBlank(exchangeType)){
						currencyArr.add(m.getNumberBiEn().toLowerCase()+"_"+m.getExchangeBiEn().toLowerCase());
					}
				}
			} else {//本币类型不为空
				currencyTypes = currencyTypes.toLowerCase();
				JSONArray arr = JSONObject.parseArray(currencyTypes);
				if(StringUtils.isNotBlank(exchangeType)){//指定了兑换币种
					for(int i=0;i<arr.size();i++){
						String numberBi = arr.getString(i);
						currencyArr.add(numberBi+"_"+exchangeType);
					}
				}else{//未指定兑换币种
					for(int i=0;i<arr.size();i++){
						String numberBi = arr.getString(i);
						Map<String, Market> map = CommonUtil.sortMapByValue(Market.markets);
						Iterator<Map.Entry<String, Market>> iter = map.entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry<String, Market> entry = iter.next();
							Market m = entry.getValue();
							if (numberBi.equalsIgnoreCase(m.getNumberBiEn())) {
								currencyArr.add(numberBi+"_"+m.getExchangeBiEn().toLowerCase());
							}
						}
					}
				}
			}

			JSONArray btcDatas = new JSONArray();
			JSONArray usdtDatas = new JSONArray();
			for (Object currency : currencyArr) {
				String marketName = currency.toString() ;
				Market m = Market.getMarkeByName(marketName);
				if (m == null) {
					continue;
				}
				JSONArray jarray = new JSONArray();
				// 处理K线数据 start
				if (time > 0) {// 指定时间才进行处理
					String rtn = data(time, m);

					if (null != rtn && !"".equals(rtn)) {
						JSONArray json = JSONObject.parseArray("[" + rtn + "]");
						if (size > json.size() || size == 0) {
							if (json.size() > 0) {
								for (int i = json.size() - 1; i > -1; i--) {
									JSONArray jarry = json.getJSONArray(i);
									JSONArray jarry2 = new JSONArray();
									if(jarry==null || jarry.size()==0){continue;}
									jarry2.add(jarry.get(0));// 时间
									jarry2.add(0);
									jarry2.add(0);
									jarry2.add(jarry.get(1));// 高开
									jarry2.add(jarry.get(4));// 关闭
									jarry2.add(jarry.get(2));// 高
									jarry2.add(jarry.get(3));// 低
									jarry2.add(jarry.get(5));// 总数量
									jarray.add(jarry2);
								}
							}
						} else {
							if (json.size() > 0) {
								for (int i = json.size() - 1; i > json.size() - size - 1; i--) {
									JSONArray jarry = json.getJSONArray(i);
									JSONArray jarry2 = new JSONArray();
									if(jarry==null || jarry.size()==0){continue;}
									jarry2.add(jarry.get(0));// 时间
									jarry2.add(0);
									jarry2.add(0);
									jarry2.add(jarry.get(1));// 高开
									jarry2.add(jarry.get(4));// 关闭
									jarry2.add(jarry.get(2));// 高
									jarry2.add(jarry.get(3));// 低
									jarry2.add(jarry.get(5));// 总数量
									jarray.add(jarry2);
								}
							}
						}
					}
				}

				// 处理K线数据 end

				// 处理Ticker
				String ticker = DishDataCacheService.getTicker(marketName);
				if (StringUtils.isNotBlank(ticker)) {
					JSONObject json = JSONObject.parseObject(ticker);
					JSONObject tickerJson = json.getJSONObject("ticker");
					tickerJson.put("highdollar", 0);
					tickerJson.put("lowdollar", 0);
					tickerJson.put("selldollar", 0);
					tickerJson.put("highdollar", 0);
					tickerJson.put("buydollar", 0);
					tickerJson.put("dollar", 0);

					BigDecimal legalTenderPrice = BigDecimal.ZERO;
					if(marketName.indexOf("_btc")!= -1){
						if (StringUtil.exist(cachePriceBtc)) {
							legalTenderPrice = tickerJson.getBigDecimal("last").multiply(new BigDecimal(cachePriceBtc));
						}
					}else{
						if (StringUtil.exist(cachePriceUsdc)) {
							legalTenderPrice = tickerJson.getBigDecimal("last").multiply(new BigDecimal(cachePriceUsdc));
						}
					}
					tickerJson.put("legal_tender", legalTender.split("_")[1] + " " + DigitalUtil.roundDownStr(legalTenderPrice.doubleValue(), 2));

					json.put("coinName", m.getNumberBi());
					json.put("coinFullNameEn", m.getNumberBiFullName());
					//币的数量保留位数
					json.put("numberBixDian", m.getNumberBixDian());
					//价格保留位数
					json.put("exchangeBixDian", m.getExchangeBixDian());
					json.put("exeByRate", 1);
					json.put("symbol", marketName);
					json.put("moneyType", m.getNumberBiFundsType());// 币种类型
					json.put("time", System.currentTimeMillis() / 1000);
					json.put("type", 100);
					json.put("cName", "比特全球");
					json.put("name", "btcwinex");
					json.put("tline", jarray);
					//获取涨跌幅
					String riseRateTop = getRiseRate(marketName);
					json.put("riseRateTop", riseRateTop);
					if(marketName.endsWith("btc")){
						btcDatas.add(json);
					}else if(marketName.endsWith("usdt")){
						usdtDatas.add(json);
					}
				}else{
					JSONObject json = new JSONObject();
					JSONObject tickerJson = new JSONObject();
					tickerJson.put("highdollar", 0);
					tickerJson.put("lowdollar", 0);
					tickerJson.put("selldollar", 0);
					tickerJson.put("highdollar", 0);
					tickerJson.put("buydollar", 0);
					tickerJson.put("dollar", 0);
					tickerJson.put("legal_tender", legalTender.split("_")[1] + " 0.00");
					tickerJson.put("high", 0);
					tickerJson.put("low", 0);
					tickerJson.put("sell", 0);
					tickerJson.put("high", 0);
					tickerJson.put("buy", 0);
					tickerJson.put("last", 0);
					tickerJson.put("weekRiseRate", 0);
					tickerJson.put("monthRiseRate", 0);

					json.put("coinName", m.getNumberBi());
					json.put("coinFullNameEn", m.getNumberBiFullName());
					//币的数量保留位数
					json.put("numberBixDian", m.getNumberBixDian());
					//价格保留位数
					json.put("exchangeBixDian", m.getExchangeBixDian());
					json.put("exeByRate", 1);
					json.put("symbol", marketName);
					json.put("moneyType", m.getNumberBiFundsType());// 币种类型
					json.put("time", System.currentTimeMillis() / 1000);
					json.put("type", 100);
					json.put("cName", "比特全球");
					json.put("name", "btcwinex");
					json.put("tline", jarray);
					json.put("ticker", tickerJson);
					//获取涨跌幅
					String riseRateTop = getRiseRate(marketName);
					json.put("riseRateTop", riseRateTop);
					if(marketName.endsWith("btc")){
						btcDatas.add(json);
					}else if(marketName.endsWith("usdt")){
						usdtDatas.add(json);
					}
				}
			}
			Map<String, Object> retMap = new HashMap<String, Object>();
			retMap.put("btcDatas", btcDatas);
			retMap.put("usdtDatas", usdtDatas);
			json(SystemCode.code_1000, retMap);

		} catch (JSONException e) {
			// TODO: handle exception
			json(SystemCode.code_1001, L("json解析失败!"));
		} catch (Exception e) {
			// TODO: handle exception
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}

	public String getRiseRate(String marketName){
		String riseRateTop = "";
		try {
			String topall = new Line().getTopall("");
			//解决NEO(ANS)，括号乱码问题
			topall = URLDecoder.decode(topall, "UTF-8");
			if(StringUtils.isNotEmpty(topall)){
				topall = topall.substring(2,topall.length()-2);
				JSONObject jsonObject = JSONObject.parseObject(topall);
				for(Entry<String, Object> entry : jsonObject.entrySet()){
					String key = entry.getKey();
					Object obj = entry.getValue();
					if(key.toLowerCase().contains(marketName.toLowerCase())){
						JSONArray jsonArray = JSONArray.parseArray(obj.toString());
						riseRateTop = jsonArray.get(8).toString();
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return riseRateTop;
	}

	@Page(Viewer = JSON)
	public void getTickerDetail(){
		setLan();
		String marketName = param("market");
		String userId = userIdStr();
		if("0".equals(userId)){
			userId = "";
		}
		String token = param("token");
		// k线图时间类型
		int time = intParam("step");
		// 获取k线数据数量
		int size = intParam("size");
		if(StringUtils.isNotEmpty(marketName)){
			marketName = marketName.replace("/","_").toLowerCase();
		}else{
			json(SystemCode.code_1001, "请求参数为空");
			return;
		}
		Market m = Market.getMarkeByName(marketName);
		if (m == null) {
			json(SystemCode.code_1001, "不支持的货币类型");
			return;
		}
		String legalTender = "usd_$";
		/** start 20170901 xzhang  新增前端传递货币折算币种,兼容IOS折算币种老版本，优先取传递，其次去缓存。最后去默认*/
		String legal_Tender = param("legal_tender");
		String legalTenderParam = "";
		if(StringUtils.isNotBlank(legal_Tender)){
			if (!LegalTenderType.existKey(legal_Tender)) {
				json(SystemCode.code_1001, "不支持的货币类型");
				return;
			}
			LegalTenderType legalTenderType = LegalTenderType.valueOf(legal_Tender);
			legalTenderParam = legalTenderType.getKey() + "_" + legalTenderType.getValue();
		}
		if(StringUtil.exist(legalTenderParam)){
			legalTender = legalTenderParam;
		}else if (StringUtil.exist(userId)) {
			if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
				json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
				return;
			}
			String legalTenderCache = Cache.Get("user_legal_tender_" + userId);
			legalTender = StringUtil.exist(legalTenderCache) ? legalTenderCache : legalTender;
		}
		String cachePriceBtc = Cache.Get("btc_" + legalTender.split("_")[0].toLowerCase());
		String cachePriceUsdc = Cache.Get("usdt_" + legalTender.split("_")[0].toLowerCase());
		JSONArray jarray = new JSONArray();
		// 处理K线数据 start
		if (time > 0) {// 指定时间才进行处理
			String rtn = data(time, m);

			if (null != rtn && !"".equals(rtn)) {
				JSONArray json = JSONObject.parseArray("[" + rtn + "]");
				if (size > json.size() || size == 0) {
					if (json.size() > 0) {
						for (int i = json.size() - 1; i > -1; i--) {
							JSONArray jarry = json.getJSONArray(i);
							JSONArray jarry2 = new JSONArray();
							if(jarry==null || jarry.size()==0){continue;}
							jarry2.add(jarry.get(0));// 时间
							jarry2.add(0);
							jarry2.add(0);
							jarry2.add(jarry.get(1));// 高开
							jarry2.add(jarry.get(4));// 关闭
							jarry2.add(jarry.get(2));// 高
							jarry2.add(jarry.get(3));// 低
							jarry2.add(jarry.get(5));// 总数量
							jarray.add(jarry2);
						}
					}
				} else {
					if (json.size() > 0) {
						for (int i = json.size() - 1; i > json.size() - size - 1; i--) {
							JSONArray jarry = json.getJSONArray(i);
							JSONArray jarry2 = new JSONArray();
							if(jarry==null || jarry.size()==0){continue;}
							jarry2.add(jarry.get(0));// 时间
							jarry2.add(0);
							jarry2.add(0);
							jarry2.add(jarry.get(1));// 高开
							jarry2.add(jarry.get(4));// 关闭
							jarry2.add(jarry.get(2));// 高
							jarry2.add(jarry.get(3));// 低
							jarry2.add(jarry.get(5));// 总数量
							jarray.add(jarry2);
						}
					}
				}
			}
		}

		// 处理K线数据 end

		// 处理Ticker
		String ticker = DishDataCacheService.getTicker(marketName);
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray datas = new JSONArray();
		if (StringUtils.isNotBlank(ticker)) {
			JSONObject json = JSONObject.parseObject(ticker);
			JSONObject tickerJson = json.getJSONObject("ticker");
			tickerJson.put("highdollar", 0);
			tickerJson.put("lowdollar", 0);
			tickerJson.put("selldollar", 0);
			tickerJson.put("highdollar", 0);
			tickerJson.put("buydollar", 0);
			tickerJson.put("dollar", 0);

			BigDecimal legalTenderPrice = BigDecimal.ZERO;
			BigDecimal highPrice = BigDecimal.ZERO;
			BigDecimal lowPrice = BigDecimal.ZERO;
			if(marketName.indexOf("_btc")!= -1){
				if (StringUtil.exist(cachePriceBtc)) {
					legalTenderPrice = tickerJson.getBigDecimal("last").multiply(new BigDecimal(cachePriceBtc));
					highPrice = tickerJson.getBigDecimal("high").multiply(new BigDecimal(cachePriceBtc));
					lowPrice = tickerJson.getBigDecimal("low").multiply(new BigDecimal(cachePriceBtc));
				}
			}else{
				if (StringUtil.exist(cachePriceUsdc)) {
					legalTenderPrice = tickerJson.getBigDecimal("last").multiply(new BigDecimal(cachePriceUsdc));
					highPrice = tickerJson.getBigDecimal("high").multiply(new BigDecimal(cachePriceUsdc));
					lowPrice = tickerJson.getBigDecimal("low").multiply(new BigDecimal(cachePriceUsdc));
				}
			}
			tickerJson.put("legal_tender", legalTender.split("_")[1] + " " + DigitalUtil.roundDownStr(legalTenderPrice.doubleValue(), 2));
			tickerJson.put("high_tender",DigitalUtil.roundDownStr(highPrice.doubleValue(), 2));
			tickerJson.put("low_tender",DigitalUtil.roundDownStr(lowPrice.doubleValue(), 2));
			json.put("coinName", m.getNumberBi());
			json.put("coinFullNameEn", m.getNumberBiFullName());
			json.put("exeByRate", 1);
			json.put("symbol", marketName);
			json.put("moneyType", m.getNumberBiFundsType());// 币种类型
			json.put("time", System.currentTimeMillis() / 1000);
			json.put("type", 100);
			json.put("cName", "比特全球");
			json.put("name", "btcwinex");
			json.put("tline", jarray);
			json.put("ticker", tickerJson);
			//获取涨跌幅
			String riseRateTop = getRiseRate(marketName);
			json.put("riseRateTop", riseRateTop);
			datas.add(json);
		}else{
			JSONObject json = new JSONObject();
			JSONObject tickerJson = new JSONObject();
			tickerJson.put("highdollar", 0);
			tickerJson.put("lowdollar", 0);
			tickerJson.put("selldollar", 0);
			tickerJson.put("highdollar", 0);
			tickerJson.put("buydollar", 0);
			tickerJson.put("dollar", 0);
			tickerJson.put("legal_tender", legalTender.split("_")[1] + " 0.00");
			tickerJson.put("high_tender"," 0.00");
			tickerJson.put("low_tender"," 0.00");
			tickerJson.put("high", 0);
			tickerJson.put("low", 0);
			tickerJson.put("sell", 0);
			tickerJson.put("high", 0);
			tickerJson.put("buy", 0);
			tickerJson.put("last", 0);
			tickerJson.put("weekRiseRate", 0);
			tickerJson.put("monthRiseRate", 0);

			json.put("coinName", m.getNumberBi());
			json.put("coinFullNameEn", m.getNumberBiFullName());
			json.put("exeByRate", 1);
			json.put("symbol", marketName);
			json.put("moneyType", m.getNumberBiFundsType());// 币种类型
			json.put("time", System.currentTimeMillis() / 1000);
			json.put("type", 100);
			json.put("cName", "比特全球");
			json.put("name", "btcwinex");
			json.put("tline", jarray);
			json.put("ticker", tickerJson);
			//获取涨跌幅
			String riseRateTop = getRiseRate(marketName);
			json.put("riseRateTop", riseRateTop);
			datas.add(json);
		}
		retMap.put("data", datas);
		json(SystemCode.code_1000,retMap);
	}

	/**
	 * 获取市场深度图
	 * modify by xwz 20171108
	 * 由显示各50条改为按成交价格x%偏移
	 */
	@Page(Viewer = JSON)
	public void getMarketDepthForApp() {
		setLan();
		String market = param("market");

		if(!StringUtil.exist(market)){
			json(SystemCode.code_1001, L("请求参数为空"));
			return;
		}
		market = market.replace("/","_").toLowerCase();
		Market m=Market.getMarkeByName(market);
		if(m==null){
			json(SystemCode.code_1001, "无市场");
			return;
		}

		String info = Cache.Get(m.market + "_market_depth_200");
		if (StringUtil.exist(info)) {
			info = info.substring(2,info.length()-2);
			JSONObject infoJson =  JSONObject.parseObject(info);
			JSONArray downRtn = infoJson.getJSONArray("listDown");
			JSONArray upRtn = infoJson.getJSONArray("listUp");
			Map<String, Object> retMap = new HashMap<String, Object>();
			retMap.put("downRtn",downRtn.toString());
			retMap.put("upRtn",upRtn.toString());
			json(SystemCode.code_1000, "",retMap);
			return;
		} else {
			long startTime = System.currentTimeMillis();
			String data = DishDataCacheService.getDishDepthData(m.market, 200);
			if (data != null && data.trim().length() > 0) {
//                JSONArray arr = JSONArray.parseArray(data.substring(0,data.length()-1).substring(1));
				JSONObject json = JSONObject.parseObject(data);

				JSONArray listDown = json.getJSONArray("listDown");
				JSONArray listUp = json.getJSONArray("listUp");

				String downRtn="", upRtn="";

				BigDecimal downNum = BigDecimal.ZERO; // down中的总额
				BigDecimal upNum = BigDecimal.ZERO; // up中的总额
				BigDecimal downTotal = BigDecimal.ZERO; // down的总币量
				BigDecimal upTotal = BigDecimal.ZERO; // up中的总币量

				/*start by xwz 20171108 按价格偏移取市场深度*/
				//当前市场价格
				BigDecimal currentPrice = json.getBigDecimal("currentPrice");
				Map<String, BigDecimal> priceLineMap = line.getUpAndDownPrice(m.getMarket(), currentPrice);
				BigDecimal downPriceLine  = priceLineMap.get("downPriceLine");
				BigDecimal upPriceLine  = priceLineMap.get("upPriceLine");
				if(currentPrice.compareTo(BigDecimal.ZERO) > 0){
					for (Object obj : listDown) {
						if(downPriceLine.compareTo(((JSONArray)obj).getBigDecimal(0)) > 0){
							continue;
						}
						downNum = downNum.add(((JSONArray) obj).getBigDecimal(1).multiply(((JSONArray)obj).getBigDecimal(0)));
						downTotal = downTotal.add(((JSONArray) obj).getBigDecimal(1));
						downRtn = downRtn + ",[" + ((JSONArray)obj).getBigDecimal(0) + "," + downTotal + "," + downNum + "]";
					}


					for (Object obj : listUp) {
						if(upPriceLine.compareTo(((JSONArray)obj).getBigDecimal(0)) < 0){
							continue;
						}
						upNum = upNum.add(((JSONArray) obj).getBigDecimal(1).multiply(((JSONArray)obj).getBigDecimal(0)));
						upTotal = upTotal.add(((JSONArray) obj).getBigDecimal(1));
						upRtn = upRtn + ",[" + ((JSONArray)obj).getBigDecimal(0) + "," + upTotal + "," + upNum + "]";
					}
				}

				downRtn = downRtn.trim().length() > 0 ? downRtn.substring(1) : "";
				upRtn = upRtn.trim().length() > 0 ? upRtn.substring(1) : "";

				String rtn = "([{\"listDown\":[" + downRtn + "],\"listUp\":[" + upRtn + "]}])";

//				Cache.Set(m.market + "_market_depth_50", rtn, 2 * 60);
				//缓存数据改为10s一次
				Cache.Set(m.market + "_market_depth_200", rtn, 10);
				Map<String, Object> retMap = new HashMap<String, Object>();
				retMap.put("downRtn",downRtn);
				retMap.put("upRtn",upRtn);
				json(SystemCode.code_1000, "",retMap);
				return;
			}

		}
	}

	/**
	 *涨跌幅
	 */
	@Page(Viewer = ".json")
	public void getTopall1(){
		try{
			String market = param("market");

			if(!StringUtil.exist(market)){
				json(SystemCode.code_1001, L("请求参数为空"));
				return;
			}
			market = market.replace("/","_").toLowerCase();
			Market m = Market.getMarkeByName(market);
			//所有币种的最新价格信息
			String topall = "";
			String currentPrice = "";
			if(topallStr != null && topallStr.isAvailable()){
				topall = topallStr.getStr();
			}else {
				StringBuffer sb = new StringBuffer();
				String data=DishDataCacheService.getHotData(market);
				if (data == null) {
					data = "0,0,0,0,0,0,0,[[]],0,0";
				}

				String key ="";

				key = market+"_hotdata"+"_"+ URLEncoder.encode(m.getNumberBiFullName(), "UTF-8").replaceAll(" ","+");
				String hotdata =  "\""+key+"\":["+data+"]";
				sb.append(",");
				sb.append(hotdata);
				if (sb.length() > 1) {
					topall = sb.substring(1).toString();
				}

				topallStr = new CacheString(topall, System.currentTimeMillis(), 3000);
				topall = topallStr.getStr();
				String[] dataArr = data.split(",");
				if(null != dataArr && dataArr.length > 0){
					currentPrice = dataArr[0];
				}
			}
			//解决NEO(ANS)，括号乱码问题
			topall = java.net.URLDecoder.decode(topall, "UTF-8");

			Map<String, Object> retMap = new HashMap<String, Object>();
			retMap.put("data",currentPrice);
			json(SystemCode.code_1000, "",retMap);
		}catch(Exception ex){
			log.error(ex.toString(), ex);
			jsonForApp(SystemCode.code_1002, L("未知错误导致操作失败。"),"");
		}
	}
	/**
	 *涨跌幅
	 */
	@Page(Viewer = ".json")
	public void getTopall(){
		try{
			setLan();
			String legalTender = StringUtils.isNotEmpty(param("legalTender")) ? param("legalTender") : "";
			String topall = new Line().getTopall(legalTender);
			//解决NEO(ANS)，括号乱码问题
			topall = java.net.URLDecoder.decode(topall, "UTF-8");
			//获取折算汇率
			Map<String, Object> retMap = new HashMap<String, Object>();
			retMap = getExchangeRate(retMap);
			retMap.put("data",topall);
			json(SystemCode.code_1000, "",retMap);
		}catch(Exception ex){
			log.error(ex.toString(), ex);
			jsonForApp(SystemCode.code_1002, L("内部异常"),"");
		}
	}
	/**
	 *币种小数点位数
	 */
	@Page(Viewer = ".json")
	public void getBixDian(){
		try{
			setLan();
			List<Map<String,Object>> list = Market.getBixDian();
			Map<String, Object> retMap = new HashMap<String, Object>();
			retMap.put("data",list);
			json(SystemCode.code_1000, "",retMap);
		}catch(Exception ex){
			log.error(ex.toString(), ex);
			jsonForApp(SystemCode.code_1002, L("内部异常"),"");
		}
	}

	/**
	 * 折算法币
	 */
	public Map<String, Object> getExchangeRate(Map<String, Object> retMap){
		try{
			net.sf.json.JSONObject jobj = new net.sf.json.JSONObject();
			net.sf.json.JSONObject exchangeRateBTC = new net.sf.json.JSONObject();
			exchangeRateBTC.put("USD", new BigDecimal(StringUtil.exist(Cache.Get("btc_usdt"))?Cache.Get("btc_usdt"):"1"));
			exchangeRateBTC.put("CNY", new BigDecimal(StringUtil.exist(Cache.Get("btc_cny"))?Cache.Get("btc_cny"):"1"));
			exchangeRateBTC.put("EUR", new BigDecimal(StringUtil.exist(Cache.Get("btc_eur"))?Cache.Get("btc_eur"):"1"));
			exchangeRateBTC.put("GBP", new BigDecimal(StringUtil.exist(Cache.Get("btc_gbp"))?Cache.Get("btc_gbp"):"1"));
			exchangeRateBTC.put("AUD", new BigDecimal(StringUtil.exist(Cache.Get("btc_aud"))?Cache.Get("btc_aud"):"1"));
			retMap.put("exchangeRateBTC", exchangeRateBTC);
			net.sf.json.JSONObject exchangeRateUSD = new net.sf.json.JSONObject();
			exchangeRateUSD.put("USD", new BigDecimal("1"));
			exchangeRateUSD.put("CNY", new BigDecimal(StringUtil.exist(Cache.Get("usdt_cny"))?Cache.Get("usdt_cny"):"1"));
			exchangeRateUSD.put("EUR", new BigDecimal(StringUtil.exist(Cache.Get("usdt_eur"))?Cache.Get("usdt_eur"):"1"));
			exchangeRateUSD.put("GBP", new BigDecimal(StringUtil.exist(Cache.Get("usdt_gbp"))?Cache.Get("usdt_gbp"):"1"));
			exchangeRateUSD.put("AUD", new BigDecimal(StringUtil.exist(Cache.Get("usdt_aud"))?Cache.Get("usdt_aud"):"1"));
			exchangeRateUSD.put("BTC", new BigDecimal(StringUtil.exist(Cache.Get("usdt_btc"))?Cache.Get("usdt_btc"):"1"));
			retMap.put("exchangeRateUSD", exchangeRateUSD);
			return retMap;
		}catch(Exception ex){
			log.error(ex.toString(), ex);
			jsonForApp(SystemCode.code_1002, L("内部异常"),"");
		}
		return null;
	}


	/**
	 * 获取用户历史委托（已成交和已取消）
	 */
	@Page(Viewer = ".json")
	public void getEntrustHistory() {
		setLan();
		String market = param("market");

		if(!StringUtil.exist(market)){
			json(SystemCode.code_1001, L("请求参数为空"));
			return;
		}
		market = market.replace("/","_").toLowerCase();
		String userId = userIdStr();
		String token = param("token");
		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
			json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
			return;
		}

		Market m = Market.getMarkeByName(market);
		if(m==null){
			JSONObject result = new JSONObject();
			JSONArray entrusts = new JSONArray();
			result.put("records",entrusts);
			jsonForApp(SystemCode.code_1000,"",result.toJSONString());
			return;
		}

		String type = getByParam("type");
		if (StringUtils.isBlank(type)) {
			type = "-1";
		}

		int includeCancel = intParam("includeCancel");
		int timeType = intParam("timeType");
		int pageNum = intParam("pageIndex");
		if (pageNum == 0) {
			pageNum = 1;
		}
		int pageSize = intParam("pageSize");
		if (pageSize == 0) {
			pageSize = 30;
		}

		RecordMessage myObj = new RecordMessage();
		myObj.setUserId(Integer.valueOf(userId));
		myObj.setTypes(Integer.parseInt(type));
		myObj.setPageindex(pageNum);
		myObj.setPageSize(pageSize);
		myObj.setMarket(m.market);
		//用status字段承载includeCancel含义
		myObj.setStatus(includeCancel);
		//用message字段承载timeType
		myObj.setMessage(String.valueOf(timeType));
		try {
			RecordMessage rtn2;

			if (m.listenerOpen) {
				rtn2 = server.getUserEntrustHistory(myObj, m);
			} else {
				String param = HTTPTcp.ObjectToString(myObj);
				String rtn = HTTPTcp.Post(m.ip, m.port, "/server/getUserEntrustHistory", param);
				rtn2 = (RecordMessage) HTTPTcp.StringToObject(rtn);
			}

			JSONObject result = new JSONObject();
			result.put("pageIndex", pageNum);

			String dataJsonStr = "{" + rtn2.getMessage() + "}";
			log.info("dataJsonStr:" + dataJsonStr);
			JSONObject dataJson = JSONObject.parseObject(dataJsonStr);
			int count = dataJson.getInteger("count");
			result.put("count", count);

			JSONArray entrusts = new JSONArray();
			JSONArray records = JSONArray.parseArray(dataJson.getString("record"));
			for (int i = 0; i < records.size() && i < pageSize; i++) {
				JSONArray record = records.getJSONArray(i);
				JSONObject jo = new JSONObject();
				jo.put("entrustId", Integer.parseInt(record.get(0).toString()));
				jo.put("date", record.get(6));
				jo.put("type", record.get(5));
				jo.put("price", record.get(1));
				jo.put("amount", record.get(2));
				jo.put("completeNumber", record.get(3));
				jo.put("completeTotalMoney", record.get(4).toString());
				jo.put("status", record.get(7));
				entrusts.add(jo);
			}
			result.put("records", entrusts);

			jsonForApp(SystemCode.code_1000, "",result.toJSONString());
		} catch (Exception ex2) {
			log.error("内部异常", ex2);
			jsonForApp(SystemCode.code_1002, L("内部异常"),"");
		}
	}

	/**
	 * 获取详情数据
	 */
	@Page(Viewer = ".json" )
	public void getDetails(){
		setLan();
		String userId = userIdStr();
		String token = param("token");
		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
			json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
			return;
		}

		Market m = Market.getMarkeByName(param("market").toLowerCase());
		if(m==null){
			json(SystemCode.code_1001, "无市场");
			return;
		}
		try{
			int userid=Integer.valueOf(userId);
			long entrustId=Long.parseLong(param("entrustId"));
			log.info("获取详情数据:"+entrustId+",userid:"+userid);
			RecordMessage myObj = new RecordMessage();
			myObj.setUserId(userid);
			myObj.setWebId(m.webId);
			//详情
			myObj.setTypes(101);
			//借用来保存id的
			myObj.setMessage(entrustId+"");
			myObj.setMarket(m.market);
			List<TransRecord> list = server.userRecordDetails(myObj,m);
			JSONObject result = new JSONObject();
			result.put("data", list);
			jsonForApp(SystemCode.code_1000, "",result.toJSONString());
		}catch(Exception ex){
			log.error(ex.toString(), ex);
			jsonForApp(SystemCode.code_1002, L("内部异常"),"");
		}
	}

	/**
	 * 获取用户当前委托--计划单,限价单
	 */
	@Page(Viewer = ".json")
	public void getTransRecordNow() {
		try{
			setLan();
			String userId = userIdStr();
			String token = param("token");
			if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
				json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
				return;
			}
			//买入1  卖出 0  不限 -1
			int types = intParam("types");
			//1 计划单   0 限价单
			int tab = intParam("tab");
			Market m = Market.getMarkeByName(param("market").toLowerCase());
			if(m==null){
				json(SystemCode.code_1001, "无市场");
				return;
			}
			int pageIndex = intParam("pageIndex");
			if (pageIndex == 0) {
				pageIndex = 1;
			}
			int pageSize = intParam("pageSize");
			if (pageSize == 0) {
				pageSize = 30;
			}
			if (tab ==1){
				Query<PlanEntrustBean> query = planEntrustDao.getQuery();
				query.setDatabase(m.db);
				query.setSql("select * from  plan_entrust");
				query.setCls(PlanEntrustBean.class);
				query.append(" userId = " + userId);
				query.append(" and status = -1 ");
				if (types >= 0){
					if(types == 1)
						query.append(" and types=1");
					else if(types == 0)
						query.append(" and types=0");

				}
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				int total = query.count();
				if(total > 0){
					query.append("order by submitTime desc");
					List<PlanEntrustBean> plan = planEntrustDao.findPageEntrust(pageIndex, pageSize,m.db);
					for(PlanEntrustBean planEntrustBean : plan){
						Map<String,Object> downloadMap = new HashMap<String,Object>();
						downloadMap.put("id", planEntrustBean.getEntrustId());

						BigDecimal unitPrice = planEntrustBean.getUnitPrice();
						if(unitPrice.compareTo(BigDecimal.ZERO) == 0){
							unitPrice = planEntrustBean.getUnitPriceProfit();
						}
						downloadMap.put("unitPrice", unitPrice);

						if(planEntrustBean.getTriggerPrice().compareTo(BigDecimal.ZERO) == 0){
							downloadMap.put("triggerPrice", planEntrustBean.getTriggerPriceProfit());
						}else{
							downloadMap.put("triggerPrice", planEntrustBean.getTriggerPrice());
						}

						downloadMap.put("types", planEntrustBean.getTypes());

						BigDecimal number = planEntrustBean.getNumbers();
						if(number.compareTo(BigDecimal.ZERO) == 0){
							number = planEntrustBean.getTotalMoney().divide(unitPrice, m.numberBixDian, BigDecimal.ROUND_DOWN);
						}
						downloadMap.put("numbers", number);

						downloadMap.put("completeNumber", planEntrustBean.getCompleteNumber());
						downloadMap.put("completeTotalMoney", planEntrustBean.getCompleteTotalMoney());
						downloadMap.put("submitTime", planEntrustBean.getSubmitTime());
						list.add(downloadMap);
					}
				}

				Map<String, Object> page = new HashMap<String, Object>();
				page.put("pageIndex", pageIndex);
				page.put("totalCount", total);
				page.put("list", list);
				jsonForApp(SystemCode.code_1000, "",net.sf.json.JSONObject.fromObject(page).toString());
			}else if (tab == 0){
				Query<EntrustBean> query = entrustDao.getQuery();
				query.setDatabase(m.db);
				query.setSql("select * from  entrust");
				query.setCls(EntrustBean.class);
				query.append(" userId= " + userId);
				query.append(" and status = 3 ");
				if (types >= 0){
					if(types == 1)
						query.append(" and types=1");
					else if(types == 0)
						query.append(" and types=0");

				}
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				int total = query.count();
				if(total > 0){
					query.append("order by submitTime desc");
					//分页查询
					List<EntrustBean> entrustBeans = entrustDao.findPageEntrust(pageIndex, pageSize,m.db);
					for(EntrustBean entrustBean : entrustBeans){
						Map<String,Object> downloadMap = new HashMap<String,Object>();
						downloadMap.put("id", entrustBean.getEntrustId());
						downloadMap.put("unitPrice", entrustBean.getUnitPrice());
						downloadMap.put("types", entrustBean.getTypes());
						downloadMap.put("numbers", entrustBean.getNumbers());
						downloadMap.put("completeNumber", entrustBean.getCompleteNumber());
						downloadMap.put("completeTotalMoney", entrustBean.getCompleteTotalMoney());
						downloadMap.put("submitTime", entrustBean.getSubmitTime());
						list.add(downloadMap);
					}
				}

				Map<String, Object> page = new HashMap<String, Object>();
				page.put("pageIndex", pageIndex);
				page.put("totalCount", total);
				page.put("list", list);
				jsonForApp(SystemCode.code_1000, "",net.sf.json.JSONObject.fromObject(page).toString());
			}
		}catch (Exception e) {
			log.error("内部异常", e);
			jsonForApp(SystemCode.code_1002, L("内部异常"),"");
		}
	}

	/**
	 * 取消
	 */
	@Page(Viewer = JSON)
	public void doCancle() {
		setLan();
		String userId = userIdStr();
		String token = param("token");
		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
			json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
			return;
		}
		String market = param("market");

		if(!StringUtil.exist(market)){
			json(SystemCode.code_1001, L("请求参数为空"));
			return;
		}
		market = market.replace("/","_").toLowerCase();
		Market m = Market.getMarkeByName(market);
		if (m == null) {
			json(SystemCode.code_1001, L("错误的市场"));
			return;
		}
		try {
			int webid = m.webId;
			long entityId = Long.parseLong(param("entityId"));
			//计划：1；限价：0
			String planType = param("planType");

			log.info("取消订单，用户ID：" + Integer.valueOf(userId) + ",entityId: " + entityId + ",ip:" + ip() + ",planType:" + planType + ",resoureRequest:" + resoureRequest);

			MessageCancle myObj = new MessageCancle();
			myObj.setUserId(Integer.valueOf(userId));
			myObj.setWebId(webid);

			myObj.setEntrustId(entityId);
			myObj.setStatus(0);
			myObj.setMarket(m.market);
			try {

				MessageCancle rtn2 = new MessageCancle();

				if (m.listenerOpen) {
					if (planType != null && "1".equals(planType)) {
						//计划委托取消
						rtn2 = server.canclePlanEntrust(myObj, m);
					} else {
						rtn2 = server.cancle(myObj, m);
					}
				} else {
					//正常委托取消
					String serverPath = "/server/cancle";
					String param = HTTPTcp.ObjectToString(myObj);
					if (planType != null && "1".equals(planType)) {
						//计划委托取消
						serverPath = "/server/canclePlanEntrust";
					}
					String rtn = HTTPTcp.DoRequest2(true, m.ip, m.port, serverPath, param);
					rtn2 = (MessageCancle) HTTPTcp.StringToObject(rtn);
				}
				if(Info.DoCancleSuccess.getNum() == rtn2.getStatus()){
					jsonForApp(SystemCode.code_1000, L(rtn2.getMessage()),null);
				}else{
					jsonForApp(SystemCode.code_1001, L(rtn2.getMessage()),null);
				}


			} catch (Exception ex2) {
				log.error("取消失败，交易大盘忙碌，请稍后再试，或者通知网站！", ex2);
				json(SystemCode.code_1002, L("撤销失败。"));
			}
		} catch (Exception ex) {
			log.error("撤销失败。", ex);
			json(SystemCode.code_1002, L("内部异常"));
		}
	}


	/**
	 * 获取图表数据
	 *
	 * @param time
	 *            时间
	 * @return
	 */
	public String data(int time, Market m) {
		try {
			return DishDataCacheService.getKline(m.market, String.valueOf(time));
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			return null;
		}
	}

	/**
	 * @deprecated
	 */
	@Page(Viewer = JSON)
	public void marketData() {
		/*setLan();
		String url = "https://www.btc123.com/api/getTicker";
		try {
			String symbols = param("symbols").toLowerCase();
			JSONArray symbolsAry = new JSONArray();
			if (StringUtils.isBlank(symbols)) {
				json(SystemCode.code_1001, L("找不到货币对"));
			} else {
				symbolsAry = JSONArray.parseArray(symbols);
			}
			JSONArray datas = new JSONArray();
			Map<String, String> params = new HashMap<String, String>();
			for (Object object : symbolsAry) {
				params.put("symbol", object.toString());
				String rtn = HttpUtil.doGet(url, params);
				JSONObject rtnJson = JSONObject.parseObject(rtn);
				if (rtnJson.getBoolean("isSuc")) {
					JSONObject data = rtnJson.getJSONObject("datas");
					datas.add(data);
				} else {
					continue;
				}
			}
			Map<String, Object> retMap = new HashMap<String, Object>();
			retMap.put("marketDatas", datas);
			json(SystemCode.code_1000, retMap);

		} catch (JSONException e) {
			// TODO: handle exception
			json(SystemCode.code_1001, L("json解析失败!"));
		} catch (Exception e) {
			// TODO: handle exception
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}*/
	}

	/**
	 * 5.3.4 获取首页行情图表（历史时间）
	 *
	 * - **请求参数**
	  | 参数名          | 类型     | 是否必须 | 描述                               |
	  | :----------- | :----- | :--- | :------------------------------- |
	  | currencyType | String | 是    | 货币类型：<br>BTC：比特币，LTC：莱特币，，ETH：Ethereum，ETC：Ethereum Classic|
	  | exchangeType | String | 是    | 兑换货币类型：<br>CNY：人民币 BTC：比特币，LTC：莱特币，，ETH：Ethereum，ETC：Ethereum Classic |
	  | step | String | 是    | 步长时间（秒），如30分钟，则传入1800      |
	  | size| String | 是    | 记录数      |
	   - **返回结果**
	| 参数名             | 类型              | 是否必须 | 示例      | 描述   |
	| :-------------- | :-------------- | :--- | :------ | :--- |
	| chartData | String[][]| 是    |      | 图表数组<br/>数组下标：0，时间戳（long） 1，开盘的交易id(String)  2，关盘的交易id(String) 3，高开(double) 4，关闭(double) 5，高(double) 6，低(double) 7，总数量(double)  |
	 *
	 */
	@Page(Viewer = JSON)
	public void indexMarketChart() {
		setLan();
		try {
			int time = intParam("step");
			String currencyType = param("currencyType").toUpperCase();
			String exchangeType = param("exchangeType").toUpperCase();
			String marketName = getMarketName(currencyType, exchangeType);
			int size = intParam("size");

			Market m = Market.getMarkeByName(marketName);
			if (m == null) {
				json(SystemCode.code_1001);
				return;
			}

			String rtn = data(time, m);
			JSONArray jarray = new JSONArray();
			if (null != rtn && !"".equals(rtn)) {
				JSONArray json = JSONArray.parseArray("[" + rtn + "]");
				if (size > json.size()) {
					if (json.size() > 0) {
						for (int i = json.size() - 1; i > -1; i--) {
							JSONArray jarry = json.getJSONArray(i);
							JSONArray jarry2 = new JSONArray();
							jarry2.add(jarry.get(0));// 时间
							jarry2.add(0);
							jarry2.add(0);
							jarry2.add(jarry.get(1));// 高开
							jarry2.add(jarry.get(4));// 关闭
							jarry2.add(jarry.get(2));// 高
							jarry2.add(jarry.get(3));// 低
							jarry2.add(jarry.get(5));// 总数量
							jarray.add(jarry2);
						}
					}
				} else {
					if (json.size() > 0) {
						for (int i = json.size() - 1; i > json.size() - size - 1; i--) {
							JSONArray jarry = json.getJSONArray(i);
							JSONArray jarry2 = new JSONArray();
							jarry2.add(jarry.get(0));// 时间
							jarry2.add(0);
							jarry2.add(0);
							jarry2.add(jarry.get(1));// 高开
							jarry2.add(jarry.get(4));// 关闭
							jarry2.add(jarry.get(2));// 高
							jarry2.add(jarry.get(3));// 低
							jarry2.add(jarry.get(5));// 总数量
							jarray.add(jarry2);
						}
					}
				}
			}
			Map<String, Object> reData = new HashMap<String, Object>();
			reData.put("chartData", jarray);
			json(SystemCode.code_1000, reData);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			json(SystemCode.code_1002, L("服务器出错，请稍后重试"));
		}
	}



	/**
	 * 5.3.5 获取首页行情图表（增量）
	 *
	 *
	 * - **请求参数**
	  | 参数名          | 类型     | 是否必须 | 描述                               |
	  | :----------- | :----- | :--- | :------------------------------- |
	  | currencyType | String | 是    | 货币类型：<br>BTC：比特币，LTC：莱特币，ETH：Ethereum，ETC：Ethereum Classic|
	  | exchangeType | String | 是    | 兑换货币类型：<br>CNY：人民币 BTC：比特币，LTC：莱特币，，ETH：Ethereum，ETC：Ethereum Classic |
	  | size| String | 否    | 返回记录条数默认50条      |
	  | since| String | 否    | 关盘的交易id 默认为0     |
	  - **返回结果**
	  | 参数名             | 类型              | 是否必须 | 示例      | 描述   |
	  | :-------------- | :-------------- | :--- | :------ | :--- |
	  | marketChartDatas| MarketChartData[]| 是    |      | 图表数据数组  |
	 */
	@Page(Viewer = JSON)
	public void indexMarketChartTrades() {
		setLan();
		/**
		 * 获取委托和交易历史记录数据
		 */
		try {
			JSONArray sizeJsonArr = new JSONArray();
			String currencyType = param("currencyType").toUpperCase();
			String exchangeType = param("exchangeType").toUpperCase();
			String market = getMarketName(currencyType, exchangeType);

			int since = request.getParameter("since") == null ? 0 : Integer.parseInt(request.getParameter("since"));
			int size = request.getParameter("size") == null ? 0 : Integer.parseInt(request.getParameter("size"));
			Market m = Market.getMarkeByName(market);
			if (m == null) {
				json(SystemCode.code_1001, "币种参数无效");
				return;
			}
			String data = DishDataCacheService.getSinceTrade(m.market, since);
			if(data!=null){
				JSONArray lastTradeArray  = JSONObject.parseArray(data);
				JSONArray sinceJsonArr = new JSONArray();
				if(since>0){
					for(int i=0;i<lastTradeArray.size();i++){
						JSONObject json = lastTradeArray.getJSONObject(i);
						if(json.getIntValue("tid")>since){
							sinceJsonArr.add(lastTradeArray.get(i));
						}
					}
				}else{
					sinceJsonArr = lastTradeArray;
				}


				if(size>0 && size!=50 && sinceJsonArr.size()>size){
					size =  size>50?50:size;

					if(sinceJsonArr!=null && sinceJsonArr.size()>0){
						int index = 0;
						if(size<sinceJsonArr.size()){
							index = sinceJsonArr.size() -size;
						}
						for(int i=index;i<sinceJsonArr.size();i++){
							sizeJsonArr.add(sinceJsonArr.get(i));
						}
					}
				}else{
					sizeJsonArr = sinceJsonArr;
				}

				//data = sizeJsonArr.toJSONString().replaceAll("\"", "");
			}

			//20170327 modify by suxinjie date字段补 '000';
			for (int i=0; i< sizeJsonArr.size(); i++) {
				JSONObject object = sizeJsonArr.getJSONObject(i);
//				innerJa.get(3).toString() + "000";
				object.put("date", object.get("date").toString() + "000");
			}

			Map<String, Object> reData = new HashMap<String, Object>();
			reData.put("marketChartDatas", sizeJsonArr);
			json(SystemCode.code_1000, reData);

		} catch (Exception ex) {
			json(SystemCode.code_1001, "币种参数无效");
			return;
		}
	}

	/**
	 * 5.3.12 设置首页显示行情模块
	 *
	 * - **请求参数**

	  | 参数名         | 类型     | 是否必须 | 描述      |
	  | :---------- | :----- | :--- | :------ |
	  | userId      | String | 是    | 用户id    |
	  | token       | String | 是    | 登录token |
	  | currencyType| String | 是    | 货币类型：BTC：比特币，LTC：莱特币，ETH：Ethereum，ETC：Ethereum Classic|
	  | entrustId| String | 是    | 交易id     |

	- **返回结果**

	| 参数名     | 类型     | 是否必须 | 示例      | 描述   |
	| :------ | :----- | :--- | :------ | :--- |
	 *
	 */
	@Page(Viewer = JSON)
	public void indexMarketSet() {
		setLan();
		String userId = param("userId");
		String token = param("token");
		String marketSets = param("marketSets");
		// marketSets[]

		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
			json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
			return;
		} else if (StringUtils.isEmpty(marketSets)) {
			json(SystemCode.code_1001, L("总部没有收到您的设置信息"));
			return;
		}

		// String[] marketSets =
		// {"abc","0"};//行情模块数组 下标：0，行情标识（btc123 api的symbol值） 1，位置（Integer）

		boolean isSetSuc = Cache.Set(MARKETSETS_KEY + userId, marketSets.toString());

		if (isSetSuc) {
			json(SystemCode.code_1000);
		} else {
			json(SystemCode.code_1002);
		}
	}

	/**
	 *
	 * 5.3.13 获取首页显示行情模块
	 * - **请求参数**

	  | 参数名         | 类型     | 是否必须 | 描述      |
	  | :---------- | :----- | :--- | :------ |
	  | userId      | String | 是    | 用户id    |
	  | token       | String | 是    | 登录token |

	- **返回结果**

	| 参数名     | 类型     | 是否必须 | 示例      | 描述   |
	| :------ | :----- | :--- | :------ | :--- |
	| marketSets  | String[][]| 是    | | 行情模块数组 下标：0，行情标识（btc123 api的symbol值）   1，位置（Integer）   |
	 *
	 */
	@Page(Viewer = JSON)
	public void getIndexMarketSet() {
		setLan();
		String userId = param("userId");
		String token = param("token");

		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
			json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
			return;
		}

		String marketSets = Cache.Get(MARKETSETS_KEY + userId);

		if (StringUtils.isNotEmpty(marketSets)) {
			Map<String, Object> reData = new HashMap<>();
			reData.put("marketSets", marketSets);
			json(SystemCode.code_1000, reData);
		} else {
			json(SystemCode.code_1002);
		}
	}

	@Page(Viewer = JSON)
	public void test() {
		Map<String, Object> reData = new HashMap<String, Object>();
		reData.put("suc", 1);
		json(SystemCode.code_1000, reData);
	}

	/**
	 * 5.3.3 获取汇率信息
	 *
	 * - **请求参数**

	  | 参数名       | 类型     | 是否必须 | 描述          |
	  | :-------- | :----- | :--- | :---------- |
	  | currencyA | String | 是    | 类型：CNY，USD等 |
	  | currencyB | String | 是    | 类型：CNY，USD等 |
	- **返回结果**
	| 参数名     | 类型      | 是否必须 | 示例      | 描述                      |
	| :------ | :------ | :--- | :------ | :---------------------- |
	| rate    | String  | 是    |         | 货币A兑B的汇率比例，<br>例：6322:1 |
	 *
	 */
	@Page(Viewer = JSON)
	public void exchangeRate() {
		setLan();
		String currencyA = param("currencyA").toLowerCase(); // String 是
		String currencyB = param("currencyB").toLowerCase(); // String 是
		try {
			double price = 6.4501;
			if ("cny".equals(currencyA) && "usd".equals(currencyB)) {
				if (null != Cache.Get(CACHE_CNYUSD_PRICE)) {
					price = Double.valueOf(Cache.Get(CACHE_CNYUSD_PRICE));
				} else {
					price = updateRade();
				}
			} else if ("cny".equals(currencyB) && "usd".equals(currencyA)) {
				if (null != Cache.Get(CACHE_CNYUSD_PRICE)) {
					price = Double.valueOf(Cache.Get(CACHE_CNYUSD_PRICE));
				} else {
					price = updateRade();
				}
			} else {
				json(SystemCode.code_1001, L("汇率转换出错"));
				return;
			}
			Map<String, Object> rtnData = new HashMap<String, Object>();
			rtnData.put("rate", price);
			json(SystemCode.code_1000, rtnData);
			return;
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_4001);
		}
		json(SystemCode.code_1001, L("汇率转换出错"));
	}

	final String CACHE_CNYUSD_PRICE = "cache_cnyusd_price";

	public double updateRade() {
		setLan();
		double price = 6.45D;
		try {
			String html = HttpUtil.doGet("http://download.finance.yahoo.com/d/quotes.csv?e=.csv&f=l1&s=USDCNY=x", null);
			price = Double.valueOf(html);
			Cache.Set(CACHE_CNYUSD_PRICE, price + "", 60 * 60);
		} catch (Exception e) {
			try {
				String html = HttpUtil.doGet("https://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote",
						null);
				String result = html.substring(html.indexOf("<field name=\"name\">USD/CNY</field>") + 34,
						html.indexOf("<field name=\"symbol\">CNY=X</field>"));
				result = result.replace("<field name=\"price\">", "").replace("</field>", "").trim();
				price = Double.valueOf(result);
				Cache.Set(CACHE_CNYUSD_PRICE, price + "", 60 * 60);
			} catch (IOException e1) {
				log.error(e1.toString(), e1);
			}
			log.error(e.toString(), e);
		}
		return price;
	}

	/**
	 * 5.3.7  当前委托
	 *
	 * - **请求参数**

	  | 参数名        | 类型     | 是否必须 | 描述                                       |
	  | :--------- | :----- | :--- | :--------------------------------------- |
	  | userId     | String | 是    | 用户id                                     |
	  | token      | String | 是    | 登录token                                  |
	  | type       | String | 否    | 类型<br/>0：卖出 1：买入  -1：不限制          默认 -1       |
	  | currencyType | String | 是    | 货币类型：<br>BTC：比特币，LTC：莱特币 ，，ETH：Ethereum，ETC：Ethereum Classic |
	  | exchangeType | String | 是    | 兑换货币类型：<br>CNY：人民币 BTC：比特币，LTC：莱特币 ，，ETH：Ethereum，ETC：Ethereum Classic|
	  | dayIn3     | String | 否    |    3天内数据  0：否    1：是  默认1    |
	  | timeFrom   | String | 否    | 委托时间-开始时间 时间戳                            |
	  | timeTo     | String | 否    | 委托时间-结束时间 时间戳                            |
	  | numberFrom | String | 否    | 委托数量-最低                                  |
	  | numberTo   | String | 否    | 委托数量-最高                                  |
	  | priceFrom  | String | 否    | 委托单价-最低                                  |
	  | priceTo    | String | 否    | 委托单价-最高                                  |
	  | status     | String | 否    | 状态<br/>0不限制 1 已取消成功 2 交易成功 3 交易中（未完全成交）-1计划中 默认：0 |
	  | pageIndex  | String | 否   | 页码 从1开始      默认：1                             |
	  | pageSize   | String | 否   | 每页显示数量 默认10， 最大200                             |

	- **返回结果**

	| 参数名           | 类型             | 是否必须 | 示例      | 描述           |
	| :------------ | :------------- | :--- | :------ | :----------- |
	| entrustTrades | EntrustTrade[] | 是    |         | 委托交易数组       |
	| pageIndex     | String         | 是    |         | 页码 从1开始      |
	| pageSize      | String         | 是    |         | 每页显示数量 最大200 |
	| totalPage    | String         | 是    |         | 总页数 |
	 */
	@Page(Viewer = JSON)
	public void entrustRecord() {
		setLan();
		int userId = intParam("userId");
		String userIdStr = userId + "";
		String token = param("token");
		String currencyType = param("currencyType").toUpperCase();
		String exchangeType = param("exchangeType").toUpperCase();

		if (isLogin(userIdStr, token) == SystemCode.code_1003 || isLogin(userIdStr, token) == SystemCode.code_402) {
			json(isLogin(userIdStr, token), L(isLogin(userIdStr, token).getValue()));
			return;
		}

		String marketName = getMarketName(currencyType, exchangeType);

		Market m = Market.getMarkeByName(marketName);
		if (m == null) {
			json(SystemCode.code_1001, L("错误的市场"));
			return;
		}




		/**
		 * 获取用户指定类型的交易管理数据
		 *
		 * @param webId
		 *            网站id 网站id（暂时都设置城8）
		 * @param userId
		 *            用户id 用户id
		 * @param pageIndex
		 *            页码从1开始
		 * @param pageSize
		 *            页码大小 10
		 * @param type
		 *            类型 0 卖出 1 买入 -1不限制
		 * @param timeFrom
		 *            //时间 System.currentTimeMillis()
		 * @param timeTo
		 * @param numberFrom//数量查询，数量等于用户提交的数量*Market.numberBixNormal
		 *            提交过来
		 * @param numberTo//数量查询
		 * @param priceFrom
		 *            最低价格
		 * @param priceTo
		 *            最高价格
		 * @param pagesize
		 *            页码大小 最大200
		 * @param status
		 *            订单状态 0不限制 1 已取消成功 2 交易成功 3 交易中（未完全成交）
		 * @return 返回的是json数据，格式为 count：总数量
		 *         record数组代表结果集合entrustId,unitPrice,numbers,completeNumber,
		 *         completeTotalMoney,types,submitTime,status
		 */

		// long lastTime=Long.parseLong(request.getParameter("lastTime"));
		int pageIndex = StringUtils.isEmpty(request.getParameter("pageIndex")) ? 1
				: Integer.parseInt(request.getParameter("pageIndex"));
		int type = StringUtils.isEmpty(request.getParameter("type")) ? -1
				: Integer.parseInt(request.getParameter("type"));
		long timeFrom = StringUtils.isEmpty(request.getParameter("timeFrom")) ? 0
				: Long.parseLong(request.getParameter("timeFrom"));
		long timeTo = StringUtils.isEmpty(request.getParameter("timeTo")) ? 0
				: Long.parseLong(request.getParameter("timeTo"));
		long numberFrom = StringUtils.isEmpty(request.getParameter("numberFrom")) ? 0
				: Long.parseLong(request.getParameter("numberFrom"));
		long numberTo = StringUtils.isEmpty(request.getParameter("numberTo")) ? 0
				: Long.parseLong(request.getParameter("numberTo"));
		long priceFrom = StringUtils.isEmpty(request.getParameter("priceFrom")) ? 0
				: Long.parseLong(request.getParameter("priceFrom"));
		long priceTo = StringUtils.isEmpty(request.getParameter("priceTo")) ? 0
				: Long.parseLong(request.getParameter("priceTo"));
		int pageSize = StringUtils.isEmpty(request.getParameter("pageSize")) ? 10
				: Integer.parseInt(request.getParameter("pageSize"));
		int status = StringUtils.isEmpty(request.getParameter("status")) ? 0
				: Integer.parseInt(request.getParameter("status"));

		String dayIn3 = request.getParameter("dayIn3");
		int dateTo = 0;
		if (!StringUtils.isEmpty(dayIn3) && Integer.parseInt(dayIn3) == 0) {
			dateTo = 5;
			pageIndex++;
		}

		RecordMessage myObj = new RecordMessage();
		myObj.setUserId(userId);
		myObj.setAuth("");
		myObj.setWebId(5);
		myObj.setTypes(type);
		myObj.setTimeFrom(timeFrom);
		myObj.setTimeTo(timeTo);
		myObj.setNumberFrom(numberFrom);
		myObj.setNumberTo(numberTo);
		myObj.setPriceFrom(priceFrom);
		myObj.setPriceTo(priceTo);
		myObj.setPageindex(pageIndex);
		myObj.setPageSize(pageSize);
		myObj.setStatus(status);
		myObj.setDateTo(dateTo);
		myObj.setMarket(m.market);
		try {
			Map<String, Object> msgMap = new HashMap<String, Object>();
            //String dataJsonStr = null;
            RecordMessage rtn2 = new RecordMessage();

            //app接口不走缓存，直接走数据库 add by buxianguan
            if (m.listenerOpen) {
                rtn2 = server.userrecordNoCache(myObj, m);
            } else {
                String param = HTTPTcp.ObjectToString(myObj);
                log.info("[请求转发] 请求转发到业务处理服务器 " + m.ip + ":" + m.port);
                String rtn = HTTPTcp.Post(m.ip, m.port, "/server/userrecordNoCache", param);

                rtn2 = (RecordMessage) HTTPTcp.StringToObject(rtn);
            }
            String dataJsonStr = "{" + rtn2.getMessage() + "}";

			JSONObject dataJson = JSONObject.parseObject(dataJsonStr);

			// msgMap.put("entrustTrades", dataJson.get("record"));
			JSONArray rtnJa = new JSONArray();


			JSONArray ja = JSONArray.parseArray(dataJson.getString("record"));
			pageSize = pageSize > 0 ? pageSize : 10;

			for (int i = 0; i < (pageSize > ja.size() ?  ja.size() : pageSize); i++) {
				JSONArray innerJa = ja.getJSONArray(i);

				net.sf.json.JSONObject jo = new net.sf.json.JSONObject();
				jo.put("entrustId", innerJa.get(0));// √
				jo.put("submitTime", innerJa.get(6));// √
				jo.put("type", innerJa.get(5));//
				jo.put("unitPrice", innerJa.get(1));//
				jo.put("number", innerJa.get(2));// √
				jo.put("completeNumber", innerJa.get(3));//
				BigDecimal completeTotalMoney = new BigDecimal(innerJa.getString(4));
        		BigDecimal completeNumber = new BigDecimal(innerJa.getString(3));
        		if (completeNumber.compareTo(BigDecimal.ZERO) == 0) {
					completeNumber = BigDecimal.ONE;
				}
        		BigDecimal average = completeTotalMoney.divide(completeNumber, 8, BigDecimal.ROUND_DOWN);
        		jo.put("junjia", average);//计算均价
				//jo.put("junjia", innerJa.get(4));//
				jo.put("completeTotalMoney", innerJa.get(4));//
				jo.put("status", innerJa.get(7));// √
				jo.put("entrust_total", new BigDecimal(innerJa.getString(1)).multiply(new BigDecimal(innerJa.getString(2))));// √
				jo.put("complete_rate", new BigDecimal(innerJa.getString(3)).divide(new BigDecimal(innerJa.getString(2)), 8, BigDecimal.ROUND_DOWN));// √

				rtnJa.add(jo);
			}

			msgMap.put("entrustTrades", rtnJa);

			msgMap.put("pageIndex", pageIndex);
			msgMap.put("pageSize", pageSize);
			int totalRecord = dataJson.getIntValue("count");
			// int totalPage = count%pageSize>0?count%pageSize+1:count%pageSize;
			int totalPage = (totalRecord + pageSize - 1) / pageSize;
			msgMap.put("totalPage", totalPage);
			json(SystemCode.code_1000, msgMap);

		} catch (Exception ex2) {
			// Response.append(jsoncallback+"([{\"lastTime\":0}])");
			log.error(ex2.toString(), ex2);
		}
	}

	/**
	 * 5.3.6 获取行情盘口深度
	 *
	 * - **请求参数**

	  | 参数名          | 类型     | 是否必须 | 描述                               |
	  | :----------- | :----- | :--- | :------------------------------- |
	  | length        | String | 是    | 数据长度，可传入 5，10，20，50                       |
	  | currencyType | String | 是    | 货币类型：<br>BTC：比特币，LTC：莱特币，，ETH：Ethereum，ETC：Ethereum Classic |
	  | exchangeType | String | 是    | 兑换货币类型：<br>CNY：人民币 BTC：比特币，LTC：莱特币，ETH：Ethereum，ETC：Ethereum Classic |



	- **返回结果**

	| 参数名     | 类型              | 是否必须 | 示例      | 描述   |
	| :------ | :-------------- | :--- | :------ | :--- |
	| asks    | String[][]| 是    |         | 卖方深度 数组说明：下标0：价格 下标1：数量 |
	| bids    | String[][]| 是    |         | 买方深度 数组说明：下标0：价格 下标1：数量 |
	| currentPrice | String | 是    |         | 当前价格 |
	 *
	 */
	@Page(Viewer = JSON)
	public void marketDepth() {
		setLan();
		try {
			String currencyType = param("currencyType").toUpperCase();
			String exchangeType = param("exchangeType").toUpperCase();
			String marketName = getMarketName(currencyType, exchangeType);

			Market m = Market.getMarkeByName(marketName);
			if (m == null) {
				json(SystemCode.code_1001);
				return;
			}
			long lastTime = System.currentTimeMillis();
			int length = 5;// 默认返回5档
			String lengthStr = request.getParameter("length");
			if (null != lengthStr && !"".equals(lengthStr)) {
				length = Integer.parseInt(request.getParameter("length"));
			}
			String sResult = "";

			length = 10;
			sResult = DishDataCacheService.getDishDepthData(m.market, length);
			if (sResult == null) {
				sResult = "{}";
			}
			sResult = sResult.replace("(", "").replace(")", "");
			if (sResult.startsWith("[")) {
				sResult = sResult.substring(1, sResult.length());
			}
			if (sResult.endsWith("]")) {
				sResult = sResult.substring(0, sResult.length() - 1);
			}
			JSONObject json = JSONObject.parseObject(sResult);
			Map<String, Object> o = new HashMap<String, Object>();
			o.put("asks", null == json ? "-" : json.get("listUp"));
			o.put("bids", null == json ? "-" : json.get("listDown"));
			o.put("currentPrice", null == json ? "-" : json.get("currentPrice"));
			json(SystemCode.code_1000, o);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

	/**
	 * 委托
	 * @deprecated
	 */
	@Page(Viewer = JSON)
	public void doEncryptEntrust() {
		setLan();
		try {
			String userId = param("userId");
			String token = param("token");

			String entrust = param("entrust");
			entrust = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(entrust), priKey));
			JSONObject entrustJson = JSONObject.parseObject(entrust);

			String currencyType = entrustJson.getString("currencyType");

			if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
				json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
				return;
			}
			String key = getMarketName(currencyType, null);
			Market m = Market.getMarkeByName(key);
			if (m == null) {
				// json("",false,L("错误的市场"));
				json(SystemCode.code_1001, L("错误的市场"));
				return;
			}

			int isBuy = entrustJson.getIntValue("type");// Integer.parseInt(request.getParameter("isBuy"));卖0买1

			int userid = intParam("userId");

			double unitPrice = DigitalUtil.roundDown(entrustJson.getIntValue("unitPrice"), m.exchangeBixDian);
			double number = DigitalUtil.roundDown(entrustJson.getIntValue("number"), m.numberBixDian);

			// long unitPrice = DigitalUtil.longMultiply(price,
			// m.exchangeBixShow);
			// long number = DigitalUtil.longMultiply(count, m.numberBixShow);

			int isPlan = entrustJson.getIntValue("isPlan");
			// boolean isPlan=isReal.equals("false");//true代表是计划委托
			// log.error("用户ID：" + userId + ",currencyType：" + currencyType +
			// ",price: " + price + ",count:" + count + ",isBuy:" + isBuy +
			// ",isPlan:" + isPlan + ",ip:" + ip());
			Message myObj = new Message();
			myObj.setUserId(userid);
			myObj.setWebId(5);
			myObj.setNumbers(BigDecimal.valueOf(number));

			myObj.setTypes(isBuy);
			myObj.setUnitPrice(BigDecimal.valueOf(unitPrice));
			myObj.setStatus(isPlan);// 0代表真实委托 1代表计划委托
			myObj.setMarket(m.market);
			String param = HTTPTcp.ObjectToString(myObj);
			Message rtn2 = new Message();
			if (m.listenerOpen) {
				rtn2 = server.Entrust(myObj, m);
			} else {
				String rtn = HTTPTcp.Post(m.ip, m.port, "/server/entrust", param);
				rtn2 = (Message) HTTPTcp.StringToObject(rtn);
			}
			// json(L(rtn2.getMessage()),true, "{\"code\" :" +
			// rtn2.getStatus()+"}");

			// Map<String, Object> reData = new HashMap<String, Object>();
			// reData.put("message", L(rtn2.getMessage()));
			// json(SystemCode.code_1000, reData);

			if (rtn2.getStatus() == 100) {
				json(SystemCode.code_1000, L(rtn2.getMessage()));
			} else {
				json(SystemCode.code_1001, L(rtn2.getMessage()));
			}

		} catch (Exception ex2) {
			json(SystemCode.code_1002, L("委托失败，交易大盘忙碌，请稍后再试，或者通知网站！"));
			log.error(ex2.toString(), ex2);
		}
	}


	/**
	 * 5.3.9 委托下单
	 *
	 * - **请求参数**

	  | 参数名          | 类型     | 是否必须 | 描述                         |
	  | :----------- | :----- | :--- | :------------------------- |
	  | userId       | String | 是    | 用户id                       |
	  | token        | String | 是    | 登录token                    |
	  | timeStamp | String | 是    | 时间戳                       |
	  | sign| String | 是    | 签名  RSA(user_id,token,timestamp)                       |
	  | type         | String | 是    | 类型 <br/> 1：买入 0：卖出         |
	  | currencyType | String | 是    | 货币类型：<br>BTC：比特币，LTC：莱特币 ，，ETH：Ethereum，ETC：Ethereum Classic|
	  | exchangeType | String | 是    | 兑换货币类型：<br>CNY：人民币 BTC：比特币，LTC：莱特币 ，ETH：Ethereum，ETC：Ethereum Classic |
	  | isPlan | String | 是    | 类型 <br/> 1：计划/委托交易  0：立即交易 |
	  | unitPrice    | String | 是    | 买入/卖出单价                    |
	  | number       | String | 是    | 数量                         |
	  | safePwd | String | 否    | 资金密码   （RSA加密）                    |
	  | fingerprint | String | 否    |  指纹识别码（RSA加密）  当有指纹密码传入时，优先判断指纹密码，通过则不验证其它密码，不通过，再验证其它密码。|

	- **返回结果**

	| 参数名     | 类型     | 是否必须 | 示例      | 描述   |
	| :------ | :----- | :--- | :------ | :--- |
	 *
	 */
	@Page(Viewer = JSON)
	public void doEntrust() {
		setLan();
		try {
			// 币种类型
			String currencyType = param("currencyType");
			// 兑换货币类型
			String exchangeType = param("exchangeType");

			String userId = userIdStr();
			String language = Cache.Get("user_lan_" + userId);
			if (StringUtils.isEmpty(language) || !lan.equals(language)) {
				Cache.Set("user_lan_" + userId, lan);
			}
			String token = param("token");
			if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
				json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
				return;
			}
			if(StringUtils.isNotBlank(Cache.Get(userOperation+userId))){
				json(SystemCode.code_4003, L("您的账户存在问题，已禁止交易。如有问题请与客服联系"));
				return;
			}
			// 指纹识别码，只要有传入，谷歌/短信、资金密码都不用验证
			String fingerprint = param("fingerprint");
			if (StringUtils.isNotBlank(fingerprint))
				fingerprint = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(fingerprint), priKey));
			// 资金密码 （RSA加密）
			String safePwd = param("safePwd");
			if (StringUtils.isNotBlank(safePwd))
				safePwd = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(safePwd), priKey));

			String marketName = getMarketName(currencyType, exchangeType);

			// 验证指纹或资金密码
			if (!fingerprintOrSafePwd(userId, safePwd, fingerprint, marketName)) {
				return;
			}

			Market m = Market.getMarkeByName(marketName);
			if (m == null) {
				// json("",false,L("错误的市场"));
				json(SystemCode.code_1001, L("错误的市场"));
				return;
			}

			// Integer.parseInt(request.getParameter("isBuy"));卖0买1
			int isBuy = intParam("type");

			int userid = Integer.valueOf(userId);

			double unitPrice = DigitalUtil.roundDown(doubleParam("unitPrice"), m.exchangeBixDian);
			double number = DigitalUtil.roundDown(doubleParam("number"), m.numberBixDian);

			/*start by xwz 20170930 增加最小成交数量的限制  update by kinghao 20181121 添加最大交易数量限制*/
			BigDecimal bixMinNum = BigDecimal.ONE;
			BigDecimal bixMaxNum = BigDecimal.ONE;
			try{
				bixMinNum = new BigDecimal(m.bixMinNum+"");
				if(bixMinNum == null || bixMinNum.compareTo(BigDecimal.ZERO) <=0){
					bixMinNum = BigDecimal.ONE;
				}
				bixMaxNum = new BigDecimal(m.bixMaxNum+"");
				if(bixMaxNum == null || bixMaxNum.compareTo(BigDecimal.ZERO) <=0){
					bixMaxNum = BigDecimal.ONE;
				}
			}catch (Exception e){
			}
			if(BigDecimal.valueOf(number).compareTo(bixMinNum)<0){
				/*start by xzhang 20170930 最小成交数量的限制科学计数法处理*/
				DecimalFormat df   = new DecimalFormat("######0.000000");
				String amountStr = df.format(m.bixMinNum);
				if(amountStr.indexOf(".") > 0){
					//正则表达
					//去掉后面无用的零
					amountStr = amountStr.replaceAll("0+?$", "");
					//如小数点后面全是零则去掉小数点
					amountStr = amountStr.replaceAll("[.]$", "");
				}
				/*end*/
				json(SystemCode.code_1001,Lan.LanguageFormat(lan,"委托失败-数量小于系统规定数量。", new String[]{amountStr, m.numberBi.toUpperCase()}));
				return;
			}
			if(BigDecimal.valueOf(number).compareTo(bixMaxNum)>0){
				/*start by xzhang 20170930 最小成交数量的限制科学计数法处理*/
				DecimalFormat df   = new DecimalFormat("######0.000000");
				String amountStr = df.format(m.bixMaxNum);
				if(amountStr.indexOf(".") > 0){
					//正则表达
					//去掉后面无用的零
					amountStr = amountStr.replaceAll("0+?$", "");
					//如小数点后面全是零则去掉小数点
					amountStr = amountStr.replaceAll("[.]$", "");
				}
				/*end*/
				json(SystemCode.code_1001,Lan.LanguageFormat(lan,"委托失败-数量大于系统规定数量。", new String[]{amountStr, m.numberBi.toUpperCase()}));
				return;
			}
			/*end*/

			int isPlan = 0;

			log.info("用户ID：" + userId + ",currencyType：" + currencyType + ",exchangeType:" + exchangeType + ",price: "
					+ unitPrice + ",count:" + number + ",isBuy:" + isBuy + ",isPlan:" + isPlan + ",ip:" + ip());

			Message myObj = new Message();
			myObj.setUserId(userid);
			myObj.setWebId(5);// 5表示手机端
			myObj.setNumbers(BigDecimal.valueOf(number));

			myObj.setTypes(isBuy);
			myObj.setUnitPrice(BigDecimal.valueOf(unitPrice));
			myObj.setStatus(isPlan);// 0代表真实委托 1代表计划委托
			myObj.setMarket(m.market);// 市场
			String param = HTTPTcp.ObjectToString(myObj);
			Message rtn2 = new Message();
			if (m.listenerOpen) {
				rtn2 = server.Entrust(myObj, m);
			} else {
				String rtn = HTTPTcp.Post(m.ip, m.port, "/server/entrust", param);
				rtn2 = (Message) HTTPTcp.StringToObject(rtn);
			}

			if (rtn2.getStatus() == 100) {
				// 增加返回委托单ID
				Map<String, Object> entrust = new HashMap<String, Object>();
				entrust.put("entrustId", rtn2.getNumbers());
				json(SystemCode.code_1000, L(rtn2.getMessage()), entrust);
			} else {
				json(SystemCode.code_1001, L(rtn2.getMessage()));
			}

		} catch (Exception ex2) {
			json(SystemCode.code_1002, L("内部异常："));
			log.error(ex2.toString(), ex2);
		}
	}


	/**getTickerArray
	 *  5.3.11 取消单笔交易
	 *
	 * - **请求参数**

	  | 参数名         | 类型     | 是否必须 | 描述      |
	  | :---------- | :----- | :--- | :------ |
	  | userId      | String | 是    | 用户id    |
	  | token       | String | 是    | 登录token |
	  | currencyType| String | 是    | 货币类型：BTC：比特币，LTC：莱特币，ETH：Ethereum，ETC：Ethereum Classic     |
	  | exchangeType| String | 是    | 兑换货币类型：<br>CNY：人民币 BTC：比特币，LTC：莱特币 ，ETH：Ethereum，ETC：Ethereum Classic |
	  | entrustId   | String | 是    | 交易id     |

	- **返回结果**

	| 参数名     | 类型     | 是否必须 | 示例      | 描述   |
	| :------ | :----- | :--- | :------ | :--- |
	 *
	 */
	@Page(Viewer = JSON)
	public void cancelEntrust() {
		setLan();
		String userId = param("userId");
		String token = param("token");
		String currencyType = param("currencyType").toUpperCase();
		String exchangeType = param("exchangeType").toUpperCase();

		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
			json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
			return;
		}

		String marketName = getMarketName(currencyType, exchangeType);

		Market m = Market.getMarkeByName(marketName);
		if (m == null) {
			json(SystemCode.code_1001, L("错误的市场"));
			return;
		}

		int userid = intParam("userId");
		int webid = 5;
		long entityId = StringUtils.isEmpty(request.getParameter("entrustId")) ? 0
				: Long.parseLong(request.getParameter("entrustId"));
		if (entityId == 0L) {
			json(SystemCode.code_1001, L("此委托单id为0"));
			return;
		}

		MessageCancle myObj = new MessageCancle();
		myObj.setUserId(userid);
		myObj.setWebId(webid);

		myObj.setEntrustId(entityId);//
		myObj.setStatus(0);
		myObj.setMarket(m.market);
		try {
			MessageCancle rtn2 = new MessageCancle();
			if (m.listenerOpen) {
				rtn2 = server.cancle(myObj, m);
				if (rtn2.getStatus() != 100 && rtn2.getStatus() != 200) {
					rtn2 = server.canclePlanEntrust(myObj, m);
				}
			} else {
				String param = HTTPTcp.ObjectToString(myObj);
				String rtn = HTTPTcp.DoRequest2(true, m.ip, m.port, "/server/cancle", param);
				String rtnPlan = HTTPTcp.DoRequest2(true, m.ip, m.port, "/server/canclePlanEntrust", param);
				rtn2 = (MessageCancle) HTTPTcp.StringToObject(rtn);
				if (rtn2.getStatus() != 100 && rtn2.getStatus() != 200) {
					rtn2 = (MessageCancle) HTTPTcp.StringToObject(rtnPlan);
				}
			}

			if (rtn2.getStatus() == 100 || rtn2.getStatus() == 200) {
				json(SystemCode.code_1000, L(rtn2.getMessage()));
			} else {
				json(SystemCode.code_1001, L(rtn2.getMessage()));
			}

		} catch (Exception ex2) {
			json(SystemCode.code_1002, L("委托失败，交易大盘忙碌，请稍后再试，或者通知网站！"));
			log.error(ex2.toString(), ex2);
		}
	}


	/**
	 * 5.3.10 批量取消交易
	 *
	 * - **请求参数**

	  | 参数名         | 类型     | 是否必须 | 描述      |
	  | :---------- | :----- | :--- | :------ |
	  | userId      | String | 是    | 用户id    |
	  | token       | String | 是    | 登录token |
	  | type | String | 是    | 取消类型  0 卖单  1 买单  -1 全部     |
	  | currencyType| String | 是    | 货币类型：BTC：比特币，LTC：莱特币，ETH：Ethereum，ETC：Ethereum Classic     |
	  | exchangeType | String | 是    | 兑换货币类型：<br>CNY：人民币 BTC：比特币，LTC：莱特币 ，ETH：Ethereum，ETC：Ethereum Classic |

	- **返回结果**

	| 参数名     | 类型     | 是否必须 | 示例      | 描述   |
	| :------ | :----- | :--- | :------ | :--- |

	 *
	 */
	@Page(Viewer = JSON)
	public void cancelBatchEntrust() {
		setLan();
		int userid = intParam("userId");
		String cacheSyncKey = "cancelmore_entrust_" + userid;
		synchronized ("cancelmore_entrust_" + userid) {
			try {
				String lock = Cache.Get(cacheSyncKey);
				if (null != lock) {
					json(SystemCode.code_1001, L("您的取消操作太频繁了，请稍后重试或刷新查看数据。"));
					return;
				}
				String userId = param("userId");
				String token = param("token");
				int type = intParam("type");
				String currencyType = param("currencyType").toUpperCase();
				String exchangeType = param("exchangeType").toUpperCase();

				if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
					json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
					return;
				}

				String marketName = getMarketName(currencyType, exchangeType);

				Market m = Market.getMarkeByName(marketName);
				if (m == null) {
					json(SystemCode.code_1001, L("错误的市场"));
					return;
				}

				int webid = 5;

				double priceLow = 0D;//
				double priceHigh = 0D;///
				// 按照区间设置 1取消买入 2取消卖出 3 取消所有

				MessageCancle myObj = new MessageCancle();
				myObj.setUserId(userid);
				myObj.setWebId(webid);
				myObj.setPriceLow(BigDecimal.valueOf(priceLow));
				myObj.setPriceHigh(BigDecimal.valueOf(priceHigh));

				/**
				 * 约定type值：0 卖出 1 买入 -1不限制 实际 1取消买入 2取消卖出 3 取消所有
				 */
				if (type == 0) {
					type = 2;
				} else if (type == 1) {
					type = 1;
				} else {
					type = 3;
				}

				myObj.setType(type);
				myObj.setMarket(m.market);

				MessageCancle rtn2 = new MessageCancle();
				if (m.listenerOpen) {
					rtn2 = server.cancelmore(myObj, m);
				} else {
					String param = HTTPTcp.ObjectToString(myObj);
					String rtn = HTTPTcp.DoRequest2(true, m.ip, m.port, "/server/cancelmore", param);
					log.info(rtn);
					rtn2 = (MessageCancle) HTTPTcp.StringToObject(rtn);
				}

				if (rtn2.getStatus() > 0) {
					json(SystemCode.code_1000, L("批量取消成功"));
				} else {
					json(SystemCode.code_1001, L("批量取消失败"));
				}

			} catch (Exception ex2) {
				json(SystemCode.code_1002, L("委托失败，交易大盘忙碌，请稍后再试，或者通知网站！"));
				log.error(ex2.toString(), ex2);
			} finally {
				try {
					Cache.Delete(cacheSyncKey);
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 *  5.2.30 获取各个平台配置信息-首页行情获取
	 *
	 *  - **请求参数**

	  | 参数名         | 类型     | 是否必须 | 描述      |
	  | :---------- | :----- | :--- | :------ |
	  | version      | Integer | 是    | 版本号    |

	- **返回结果**

	| 参数名     | 类型     | 是否必须 | 示例      | 描述   |
	| :------ | :----- | :--- | :------ | :--- |
	| platformSets| PlatformSet[] | 是    | | 平台配置数组  |
	| version | Integer | 是    | | 版本号  |
	 *
	 */
	@Page(Viewer = JSON)
	public void getPlatformSet() {
		setLan();
		int version = intParam("version");
		int curVersion = 1;
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		if (version != curVersion) {

		}
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("platformSets", list);
		retMap.put("version", curVersion);
		json(SystemCode.code_1000, retMap);
	}

	private Map<String, Object> getPlatformSet(String platformNameChn, String platformNameEn, String simpleName,
			String currency, String exchangeCurrency, int isVisible) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("symbol", simpleName.concat(currency).concat(exchangeCurrency).toLowerCase());
		map.put("name", platformNameChn);
		map.put("englishName", platformNameEn);
		map.put("currencyType", currency.toUpperCase());
		map.put("isVisible", isVisible);
		return map;
	}


	private JSONObject getMarketDepth(String string, Double[] doubles) {
		JSONObject jo = new JSONObject();
		jo.put("currency", string);
		List<String> optionals = new ArrayList<String>();
		NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(6);
		for (Double item : doubles) {
			optionals.add(
					format.format(item)/* BigDecimal.valueOf(item).toString() */);
		}

		jo.put("optional", optionals);
		return jo;
	}

	private JSONObject getMarketLength(String currency) {
		JSONObject jo = new JSONObject();
		jo.put("currency", currency);
		jo.put("optional", new Integer[] { 5, 10, 20, 50 });
		return jo;
	}

	/**
	 *  5.3.14 获取委托详情
	 *
	 * - **请求参数**

	  | 参数名         | 类型     | 是否必须 | 描述      |
	  | :---------- | :----- | :--- | :------ |
	  | userId      | String | 是    | 用户id    |
	  | token       | String | 是    | 登录token |
	  | currencyType | String | 是    | 货币类型：<br>BTC：比特币，LTC：莱特币 ，ETH：Ethereum，ETC：Ethereum Classic |
	  | exchangeType | String | 是    | 兑换货币类型：<br>CNY：人民币 BTC：比特币，LTC：莱特币，ETH：Ethereum，ETC：Ethereum Classic |
	  | entrustId | Integer | 是    |   委托id  |
	- **返回结果**

	| 参数名     | 类型     | 是否必须 | 示例      | 描述   |
	| :------ | :----- | :--- | :------ | :--- |
	| entrustOrders | 委托详情数组[EntrustOrder] | 是    |  | 委托详情数组   |
	 *
	 */
	@Page(Viewer = JSON)
	public void entrustDetails() {
		setLan();
		String userId = param("userId");
		String token = param("token");
		String entrustId = param("entrustId");
		Map<String, Object> recordMap = new HashMap<String, Object>();
		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
			json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
			return;
		}
		// 参数判断
		if (!StringUtils.isNumeric(entrustId)) {
			recordMap.put("entrustOrders", new JSONArray());
			json(SystemCode.code_1000, L("操作成功，委托ID参数必须是数字！"), recordMap);
			return;
		}

		int userIdInt = Integer.parseInt(userId);
		String currencyType = param("currencyType").toUpperCase();
		String exchangeType = param("exchangeType").toUpperCase();
		String marketName = getMarketName(currencyType, exchangeType);

		Market m = Market.getMarkeByName(marketName);
		if (m == null) {
			json(SystemCode.code_1001, L("找不到此币种!"));
			return;
		}
		try {
			RecordMessage myObj = new RecordMessage();
			myObj.setUserId(userIdInt);
			myObj.setWebId(5);
			myObj.setTypes(101);// 详情
			myObj.setMarket(m.getMarket());
			myObj.setMessage(entrustId);// 借用来保存id的

			RecordMessage rtn2;
			if(m.listenerOpen){
				rtn2 = server.userrecord(myObj,m);
			}else{
				String param=HTTPTcp.ObjectToString(myObj);
				String rtn=HTTPTcp.DoRequest2(true,m.ip,m.port,"/server/userrecord", param);
				rtn2 =(RecordMessage)HTTPTcp.StringToObject(rtn);
			}

			String recordJsonStr = "{" + rtn2.getMessage() + "}";
			JSONObject recordJson = JSONObject.parseObject(recordJsonStr);

			JSONArray rtnJa = new JSONArray();

			JSONArray ja = JSONArray.parseArray(recordJson.getString("record"));
			for (int i = 0; i < ja.size(); i++) {
				JSONArray innerJa = ja.getJSONArray(i);

				JSONObject jo = new JSONObject();
				jo.put("entrustId", innerJa.get(0));// √
				jo.put("transactionPrice", innerJa.get(1));// √
				jo.put("transactionTotalMoney", innerJa.get(2));// √
				jo.put("transactionNumber", innerJa.get(3));// √
				jo.put("transactionTime", innerJa.get(5));// √

				rtnJa.add(jo);
			}

			recordMap.put("entrustOrders", rtnJa);
			json(SystemCode.code_1000, recordMap);

		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			json(SystemCode.code_1002);
		}
	}

	@Page(Viewer = JSON)
	public void getVersion() {
		Map<String, Object> reData = new HashMap<String, Object>();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd G 'at' hh:mm:ss z");

		String timeStr = "2016-09-10 15:49:00";
		reData.put("version", timeStr);
		json(SystemCode.code_1000, reData);
	}


	/**
	 * 5.3.15 获取兑换币种集合
	 *	- **请求参数**

		  | 参数名         | 类型     | 是否必须 | 描述      |
		  | :---------- | :----- | :--- | :------ |


		- **返回结果**

		| 参数名     | 类型     | 是否必须 | 示例      | 描述   |
		| :------ | :----- | :--- | :------ | :--- |
		| exchangeBis| {btc:[eth,etc],ltc:[mg]} | 是    | |  交易币种数组  |
	 */
	@Page(Viewer = JSON)
	public void getExchangeBis(){
		Map<String,Object> reData = new HashMap<String,Object>();

		if (Market.markets != null) {
			Iterator<Entry<String, Market>> iter = Market.markets.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, Market> entry = iter.next();
				Market m = entry.getValue();

				String exChangeBi = m.getExchangeBiEn().toLowerCase();
				List<String> arr = (List<String>)reData.get(exChangeBi);
				if(arr==null){
					arr = new ArrayList<String>();
				}
				arr.add(m.getNumberBiEn().toLowerCase());
				reData.put(exChangeBi, arr);
			}
		}

		json(SystemCode.code_1000, reData);
	}

	/**
	 * 24小时买卖占比
	 *
	 * 请求路径：http://t.test.com/api/m/V1_1/tradingRatio

	 请求类型：GET

	 入参说明：

	 | 参数名称   | 参数说明 | 参数类型   | 是否必须 | 描述   |
	 | ------ | ---- | ------ | ---- | ---- |
	 | symbol | 市场     | String | Y    | ltc_btc |

	 出参说明：

	 | 参数名称      | 参数说明  | 参数类型   | 是否必须 | 描述   |
	 | --------- | ----- | ------ | ---- | ---- |
	 | code      | 操作码   | String | Y    |      |
	 | message   | 状态码描述 | String | Y    |      |
	 | datas     | 业务内容  | Object | Y    |      |
	 | buyRatio  | 买入占比  | String | Y    |      |
	 | sellRatio | 卖出占比  | String | Y    |      |

	 报文输出：

	 ```
	 {
		 "resMsg": {
			 "code": 1000,
			 "message": "操作成功",
			 "method": "entrustRecord"
		 },
		 "datas": {
			 "buyRatio":"60%",
			 "sellRatio":"40%":
		 }
	 }
	 ```
	 */
	@Page(Viewer = JSON)
	public void tradingRatio(){

		String symbol = param("symbol");
		if (!StringUtil.exist(symbol)) {
			symbol = "ltc_btc";
		} else {
			symbol = symbol.toLowerCase();
		}

		Map<String, Object> map = new HashMap<>();

		String buySell = Cache.Get(symbol + "_buy_sell");
		if (!StringUtil.exist(buySell)) {
			json(SystemCode.code_1000, map);
			return;
		}

		JSONObject jsonObject = JSONObject.parseObject(buySell);
		String buy = jsonObject.getString("buy");
		String sell = jsonObject.getString("sell");

		double divisor = DigitalUtil.add(Double.parseDouble(buy), Double.parseDouble(sell));
		if(divisor == 0){
			map.put("buyRatio", "0%");
			map.put("sellRatio", "0%");
		}else{
			map.put("buyRatio", DigitalUtil.roundDown(DigitalUtil.div(Double.parseDouble(buy) * 100, divisor), 0) + "%");
			map.put("sellRatio",  DigitalUtil.roundDown(DigitalUtil.div(Double.parseDouble(sell) * 100, divisor), 0) + "%");
		}



		json(SystemCode.code_1000, map);
	}

	/**
	 * 参数
	 * isBuy=1
	 * <p>
	 * buyPlanMoney
	 * buyTriggerPrice
	 * buyPlanPrice
	 * <p>
	 * sellPlanNumber
	 * sellTriggerPrice
	 * sellPlanPrice
	 * <p>
	 * // TODO: 2017/5/23 suxinjie 需要删除的国际化信息
	 * 1.
	 * 2.
	 * 3.
	 * 4.
	 * 5.
	 */
	@Page(Viewer = JSON)
	public void doPlanEntrust() {
		setLan();
		String userId = userIdStr();
		String token = param("token");
		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
			json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
			return;
		}

		String safePwd = param("safePassword");
		if (StringUtils.isNotBlank(safePwd)){
			try {
				safePwd = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(safePwd), priKey));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 币种类型
		String currencyType = param("currencyType");
		// 兑换货币类型
		String exchangeType = param("exchangeType");
		String marketName = getMarketName(currencyType, exchangeType);
//		int adminId = adminId();
		// 验证指纹或资金密码
		if (!fingerprintOrSafePwd(userId, safePwd, "", marketName)) {
			return;
		}
		Market m = Market.getMarket(marketName);
		if (m == null) {
			json(L("错误的市场"), false, "", true);
			return;
		}

		//买卖类型 1：买 0：卖
		String isBuyStr = param("isBuy");
		if (StringUtils.isBlank(isBuyStr)) {
			json(SystemCode.code_1001, L("参数错误"));
			return;
		}
		if (!"0".equals(isBuyStr) && !"1".equals(isBuyStr)) {
			json(SystemCode.code_1001, L("参数错误"));
			return;
		}
		int isBuy = intParam("isBuy");//Integer.parseInt(request.getParameter("isBuy"));

//		String userId = userId(true, true);
		if (StringUtils.isNotBlank(Cache.Get(userOperation + userId))) {
			json(SystemCode.code_1001, L("您的账户存在问题，已禁止交易。如有问题请与客服联系"));
			return;
		}
		// TODO: 2017/5/23 suxinjie 这里需要硬编码管理员ID
//		log.info("管理员ID：" + adminId);
//		if (adminId == 13 || adminId == 19) {
//			log.info("管理员ID：" + adminId + "代用户委托");
//		} else {
//            if (!safePwd(safePwd, userId, m.market, isBuy)) {
//                return;
//            }
//		}
		try {
			int userid = userId();

			BigDecimal currencyPrice = BigDecimal.ZERO; //当前市场价格
			double buyOne = 0d; //当前盘口的买一价格
			double sellOne = 0d; //当前盘口的卖一价格
			String ticker = DishDataCacheService.getHotData(m.market);//获取行情
			if (StringUtils.isNotEmpty(ticker)) {
				String[] arr = ticker.split(",");
				currencyPrice = arr[0] == null ? BigDecimal.ZERO : new BigDecimal(arr[0]);//市场价格
//				buyOne                  = arr[1] == null ? 0 : Double.parseDouble(arr[1]);//买一
//				sellOne                 = arr[2] == null ? 0 : Double.parseDouble(arr[2]);//卖一
			}

			//价格参数获取
			double buyPlanMoney = 0d; //计划委托金额
			double buyPlanNumber = 0d; //买入委托数量
			double sellPlanNumber = 0d; //计划委托数量（）

			double buyTriggerPrice = 0d; //买入触发价格
			double buyPlanPrice = 0d; //买入委托价格

			double sellTriggerPrice = 0d; //卖出触发价格
			double sellPlanPrice = 0d; //卖出委托价格


			if (isBuy == 1) {//计划买入
				buyPlanNumber =DigitalUtil.roundDown(doubleParam("number"), m.numberBixDian);
				buyTriggerPrice = DigitalUtil.roundDown(doubleParam("triggerPrice"), m.exchangeBixDian);//触发价
				buyPlanPrice = DigitalUtil.roundDown(doubleParam("planPrice"), m.exchangeBixDian);//委托价
				buyPlanMoney = DigitalUtil.roundUp(buyPlanNumber*buyPlanPrice, m.exchangeBixDian);//计划委托金额

//				if(buyPlanPrice >0 && BigDecimal.valueOf(buyPlanMoney).divide(BigDecimal.valueOf(buyPlanPrice), m.numberBixDian, BigDecimal.ROUND_DOWN).doubleValue()<=0){
//					json(L("委托失败，计划委托预计购买数量低于最小数量！"),false,"",true);
//					return;
//				}

				if(buyPlanPrice-Double.valueOf(String.valueOf(m.maxPrice)) >0){
					json(SystemCode.code_1001, L(Info.DoEntrustFaildHighPeice.getMessage()));
					return;
				}
				if (buyTriggerPrice <= 0) {
					json(SystemCode.code_1001, L("请输入触发价格"));
					return;
				}
				if (buyPlanPrice <= 0) {
					json(SystemCode.code_1001, L("请输入委托价格"));
					return;
				}


//				if (buyTriggerPrice <= 0 || buyPlanPrice <= 0 || buyPlanMoney <= 0) {
//					json(L("请输入订单停止价格,限制价格,金额"),false,"",true); //TODO 国际化 Please input order Stop Price\Limit Price\Amount\Total
//					return;
//				}

				/*
				 * start by xwz 20170926 增加提示成交金额小于规定金额
				 * 后面的方法也有同样的判断
				 * 如果此处不加提示成交金额验证，提示信息不准确
				 */
				if (m.getMinAmount() > 0 && BigDecimal.valueOf(buyPlanMoney).compareTo(BigDecimal.valueOf(m.getMinAmount())) < 0) {
					DecimalFormat df = new DecimalFormat("0.#########");
					json(SystemCode.code_1001,Lan.LanguageFormat(lan, "委托失败-成交金额小于系统规定金额", df.format(m.getMinAmount()) + m.exchangeBi.toUpperCase()));
					return;
				}
				/*end*/

				/*start by xwz 20170913 下单数量不能小于最小交易单位*/
				BigDecimal buyNumber = BigDecimal.valueOf(buyPlanMoney).divide(BigDecimal.valueOf(buyPlanPrice), m.numberBixDian, BigDecimal.ROUND_DOWN);
				BigDecimal bixMinNum = BigDecimal.ONE;
				BigDecimal bixMaxNum = BigDecimal.ONE;
				try {
					bixMinNum = new BigDecimal(m.bixMinNum + "");
					if (bixMinNum == null || bixMinNum.compareTo(BigDecimal.ZERO) <= 0) {
						bixMinNum = BigDecimal.ONE;
					}
					bixMaxNum = new BigDecimal(m.bixMaxNum + "");
					if (bixMaxNum == null || bixMaxNum.compareTo(BigDecimal.ZERO) <= 0) {
						bixMaxNum = BigDecimal.ONE;
					}
				} catch (Exception e) {

				}

				if (buyNumber.compareTo(bixMinNum) < 0) {
					/*start by gkl 20190408国际化修改*/
//					json(SystemCode.code_1001, Lan.LanguageFormat(lan, "委托失败-成交数量小于系统规定数量", new String[]{m.numberBi.toUpperCase(), m.bixMinNum + "", m.numberBi.toUpperCase()}));
					json(SystemCode.code_1001, Lan.LanguageFormat(lan, "委托失败-成交数量小于系统规定数量", new String[]{m.bixMinNum + "", m.numberBi.toUpperCase()}));
					/*end*/
					return;
				} if (buyNumber.compareTo(bixMaxNum) > 0) {
					/*start by gkl 20190408国际化修改*/
//					json(SystemCode.code_1001, Lan.LanguageFormat(lan, "委托失败-成交数量大于系统规定数量", new String[]{m.numberBi.toUpperCase(), m.bixMaxNum + "", m.numberBi.toUpperCase()}));
					json(SystemCode.code_1001, Lan.LanguageFormat(lan, "委托失败-成交数量大于系统规定数量", new String[]{m.bixMaxNum + "", m.numberBi.toUpperCase()}));
					/*end*/
					return;
				}
				/*end*/


			} else {//计划卖出
				//计划卖出数量
				sellPlanNumber = DigitalUtil.roundDown(doubleParam("number"), m.numberBixDian);
				//触发价
				sellTriggerPrice = DigitalUtil.roundDown(doubleParam("triggerPrice"), m.exchangeBixDian);
				//委托价
				sellPlanPrice = DigitalUtil.roundDown(doubleParam("planPrice"), m.exchangeBixDian);

//				if (sellTriggerPrice <= 0 || sellPlanNumber <= 0 || sellPlanPrice <= 0) {
//					json(L("请输入订单停止价格,限制价格,金额"),false,"",true);
//					return;
//				}
				if(sellPlanPrice-Double.valueOf(String.valueOf(m.maxPrice)) >0){
					json(SystemCode.code_1001, L(Info.DoEntrustFaildHighPeice.getMessage()));
					return;
				}
				if (sellTriggerPrice <= 0) {
					json(SystemCode.code_1001, L("请输入触发价格"));
					return;
				}
				if (sellPlanPrice <= 0) {
					json(SystemCode.code_1001, L("请输入委托价格"));
					return;
				}


				/*start by xwz 20170926 注释掉原来的成交数量数量判断*/
//				if(BigDecimal.valueOf(sellPlanNumber).setScale(3, BigDecimal.ROUND_DOWN).doubleValue()<=0){
//					json(L("委托失败， 计划委托卖出数量低于最小数量！"),false,"",true);
//					return;
//				}


/*
				/* start by xwz 20170926 增加提示成交金额小于规定金额
				 * 后面的方法也有同样的判断
				 * 如果此处不加提示成交金额验证，提示信息不准确
				 */
				if (m.getMinAmount() > 0 && BigDecimal.valueOf(sellPlanNumber).multiply(BigDecimal.valueOf(sellPlanPrice)).compareTo(BigDecimal.valueOf(m.getMinAmount())) < 0) {
					//防止double位数太多出现科学计数法  by Mark 2018/3/21
					DecimalFormat df = new DecimalFormat("0.#########");
					json(SystemCode.code_1001, Lan.LanguageFormat(lan, "委托失败-成交金额小于系统规定金额", df.format(m.getMinAmount()) + m.exchangeBi.toUpperCase()));
					return;
				}
				/*end*/

				/*start by xwz 20170913 下单数量不能小于最小交易单位
				update by kinghao 20181121 添加最大交易单位*/
				BigDecimal bixMinNum = BigDecimal.ONE;
				BigDecimal bixMaxNum = BigDecimal.ONE;
				try {
					bixMinNum = new BigDecimal(m.bixMinNum + "");
					if (bixMinNum == null || bixMinNum.compareTo(BigDecimal.ZERO) <= 0) {
						bixMinNum = BigDecimal.ONE;
					}
					bixMaxNum = new BigDecimal(m.bixMaxNum + "");
					if (bixMaxNum == null || bixMaxNum.compareTo(BigDecimal.ZERO) <= 0) {
						bixMaxNum = BigDecimal.ONE;
					}
				} catch (Exception e) {

				}

				if (BigDecimal.valueOf(sellPlanNumber).compareTo(bixMinNum) < 0) {
					/*start by gkl 20190408国际化修改*/
//					json(SystemCode.code_1001, Lan.LanguageFormat(lan, "委托失败-成交数量小于系统规定数量", new String[]{m.numberBi.toUpperCase(), m.bixMinNum + "", m.numberBi.toUpperCase()}));
					json(SystemCode.code_1001, Lan.LanguageFormat(lan, "委托失败-成交数量小于系统规定数量", new String[]{m.bixMinNum + "", m.numberBi.toUpperCase()}));
					/*end*/
					return;
				}
				if (BigDecimal.valueOf(sellPlanNumber).compareTo(bixMaxNum) > 0) {
					/*start by gkl 20190408国际化修改*/
//					json(SystemCode.code_1001, Lan.LanguageFormat(lan, "委托失败-成交数量大于系统规定数量", new String[]{m.numberBi.toUpperCase(), m.bixMaxNum + "", m.numberBi.toUpperCase()}));
					json(SystemCode.code_1001, Lan.LanguageFormat(lan, "委托失败-成交数量大于系统规定数量", new String[]{m.bixMaxNum + "", m.numberBi.toUpperCase()}));
					/*end*/
					return;
				}
				/*end*/

			}

			BigDecimal totalMoney = BigDecimal.valueOf(buyPlanMoney);//计划委托总金额（买入）
			BigDecimal number = BigDecimal.valueOf(sellPlanNumber);//计划委托总数量（卖出）

			BigDecimal unitPrice = BigDecimal.ZERO;//计划委托 追高、止损委托价格
			BigDecimal unitPriceProfit = BigDecimal.ZERO;//计划委托 抄底、止盈委托价格

			BigDecimal triggerPrice = BigDecimal.ZERO;//计划委托 追高、止损触发价格
			BigDecimal triggerPriceProfit = BigDecimal.ZERO;//计划委托 抄底、止盈触发价格

			/**
			 * 		   unitPrice    triggerPrice    unitPriceProfit    triggerPriceProfit
			 * 	买      追高委托      追高触发         抄底委托             抄底触发
			 * 	卖	    止损委托      止损触发         止盈委托             止盈触发
			 *
			 * 	通过触发价格和当前市场价格进行比较获取是追高还是抄底
			 */
			if (isBuy == 1) {
				if (new BigDecimal(buyTriggerPrice).compareTo(currencyPrice) > 0) {
					unitPrice = BigDecimal.valueOf(buyPlanPrice);
					triggerPrice = BigDecimal.valueOf(buyTriggerPrice);
				} else if (new BigDecimal(buyTriggerPrice).compareTo(currencyPrice) < 0) {
					unitPriceProfit = BigDecimal.valueOf(buyPlanPrice);
					triggerPriceProfit = BigDecimal.valueOf(buyTriggerPrice);
				} else {
					// TODO 返回信息告诉用户触发价不能和市场价相等
					json(SystemCode.code_1001, L("触发价不能与市场价相同"));
					return;
				}
			} else if (isBuy == 0) {
				if (new BigDecimal(sellTriggerPrice).compareTo(currencyPrice) > 0) {
					unitPriceProfit = BigDecimal.valueOf(sellPlanPrice);
					triggerPriceProfit = BigDecimal.valueOf(sellTriggerPrice);
				} else if (new BigDecimal(sellTriggerPrice).compareTo(currencyPrice) < 0) {
					unitPrice = BigDecimal.valueOf(sellPlanPrice);
					triggerPrice = BigDecimal.valueOf(sellTriggerPrice);
				} else {
					// TODO 返回信息告诉用户触发价不能和市场价相等
					json(SystemCode.code_1001, L("触发价不能与市场价相同"));
					return;
				}
			} else {
				//TODO 不存在的交易类型
				json(SystemCode.code_1001, L("不存在的交易类型"));
				return;
			}

			//String isReal=GetPrama(1);
			//log.error("用户ID：" + userId + ",price: " + price + ",count:" + count + ",isBuy:" + isBuy + ",isPlan: true,ip:" + ip() + ",resoureRequest:" + resoureRequest);
			Message myObj = new Message();
			myObj.setUserId(userid);
			myObj.setWebId(m.webId);
			myObj.setNumbers(number);//止盈止损总数量
			myObj.setMarket(m.market);
			myObj.setTypes(isBuy);//买卖方向
			myObj.setUnitPrice(unitPrice);//计划委托 追高、止损委托价格
			myObj.setStatus(1);//0代表真实委托   1代表计划委托
			myObj.setTriggerPrice(triggerPrice);//计划委托 追高、止损触发价格
			myObj.setUnitPriceProfit(unitPriceProfit);//计划委托  抄底、止盈委托价格
			myObj.setTriggerPriceProfit(triggerPriceProfit);//计划委托 抄底、止盈触发价格
			myObj.setTotalMoney(totalMoney);//追高抄底总金额

			try {
				Message rtn2 = null;//(Message)HTTPTcp.StringToObject(rtn);
				if (m.listenerOpen) {

					rtn2 = server.planEntrust(myObj, m);
				} else {
					String serverPath = "/server/planEntrust";
					String param = HTTPTcp.ObjectToString(myObj);
					String rtn = HTTPTcp.Post(m.ip, m.port, serverPath, param);
					rtn2 = (Message) HTTPTcp.StringToObject(rtn);
				}
//				json(L(rtn2.getMessage()), true, "{\"code\" :" + rtn2.getStatus() + "}", true);
				json(SystemCode.code_1000, L(rtn2.getMessage()));
			} catch (Exception ex2) {
				json(SystemCode.code_1001, L("委托失败，交易大盘忙碌，请稍后再试，或者通知网站！"));
			}

		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}


}

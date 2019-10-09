package com.world.controller.api.m;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.api.common.SystemCode;
import com.world.cache.Cache;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.model.entity.LegalTenderType;
import com.world.util.CommonUtil;
import com.world.util.DigitalUtil;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class V1_1 extends AbstractMobileUserAction {
	private static final long serialVersionUID = -4333874723709921756L;

	public static final String MARKETSETS_KEY ="marketSets_";
	
	private final String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJG14+94DEgzyd6G8+Ue+lpLKK9uIftpSZ7wvnX3jtw+6SUKldkvL1mYq9W8qIJD7w5t3YQIkVoWIlm5Eba5NcDYgfDC/QnYyr9zfDthlJECvQ8TC0wjy9cOtCC4FntewsqmGxLjTA17Zn0RJpsqXvNFjZEinR6IawvnlhPKJ/IwIDAQAB";
	private final String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIkbXj73gMSDPJ3obz5R76Wksor24h+2lJnvC+dfeO3D7pJQqV2S8vWZir1byogkPvDm3dhAiRWhYiWbkRtrk1wNiB8ML9CdjKv3N8O2GUkQK9DxMLTCPL1w60ILgWe17CyqYbEuNMDXtmfREmmype80WNkSKdHohrC+eWE8on8jAgMBAAECgYA0EsPx0FkEyf9szgnqNn55gBsbsnbhqpu391WjE9y/GUp0IdShqJ1EcIOENeevW2zYXCbn6mLmZzv6oqIzMuFtZ4GGbHvTsMNGtoBJsvIjV36FjdiXU7FAGqtUI+I/kFBvxFuKcil6JBFGKheQle2segoB9hAsKGoUSayAE5yjqQJBAMJllnMTMeuomhZxSQfuq4Ke3BAGGbbUfcCYnCoK1y9LBe3qXmynWYnc2caIHgbMdDiGYcTm1XOZ5lR/a2GP4HUCQQC0jiUFKWmWkx+MgverbA4QBoh+ff5M95c5T/8W2QbrUW7DV++aW4y+4D92Ei6nFcF1V8SSMxgDmqiz6pOqS243AkEAl6vlR6GZWHGyz4HR5kN8Q6yorEPmOjTubJ9lcJQGspqJZMhwpbuoa50JuRGow8svfo6yp4smzUwtXo4P/Q3hpQJAC8AIZrqYNYVjkzhet9gzXhWewmSerRGb1M4A8tKy4ZOOGsZZQHlewnlDiAKM6LDAw0sv7rfGg02IVxUYAQghpwJABiNcbBh7MnDfGaRZzE7SX/UwRn7OmGY7lFMBWadiQ/R5pKpdPrVmwdlTsefzb1acYy41LQCFCPKxVjv7sUduXQ==";

    @Page(Viewer = JSON)
    public void tickerArrayList() {
        this.getTickerArray();
    }
    @Page(Viewer = JSON)
    public void tickerDetail() {
        this.getTickerDetail();
    }

    @Page(Viewer = JSON)
    public void getMarketDepth() {
        this.getMarketDepthForApp();
    }
    /**
     *获取最新价
     */
    @Page(Viewer = JSON)
    public void topall() {
        this.getTopall();
    }
    /**
     *获取小数点位数
     */
    @Page(Viewer = JSON)
    public void bixDian() {
        this.getBixDian();
    }


    /**
     *获取用户历史委托（已成交和已取消）
     */
    @Page(Viewer = JSON)
    public void entrustHistory() {
        this.getEntrustHistory();
    }

    /**
     *获取用户历史委托详情
     */
    @Page(Viewer = JSON)
    public void entrustHistoryDetail() {
        this.getDetails();
    }

    /**
     *获取用户当前委托--计划单,限价单
     */
    @Page(Viewer = JSON)
    public void transRecordNow() {
        this.getTransRecordNow();
    }

    /**
     *获取用户当前委托--取消
     */
    @Page(Viewer = JSON)
    public void cancle() {
        this.doCancle();
    }
    /**
     *委托下单(限价单)
     */
    @Page(Viewer = JSON)
    public void entrust() {
        this.doEntrust();
    }

    /**
     *委托下单(计划单)
     */
    @Page(Viewer = JSON)
    public void planEntrust() {
        this.doPlanEntrust();
    }

//    @Page(Viewer = JSON)
//	public void getTickerArray() {
//		setLan();
//
//		// 如果用户不传userId,则默认法币类型为 美元
//		// 如果用户传了userId,则对用户进行鉴权,取memcached中的法币信息
//		String legalTender = "usd_$";
//		String cachePrice = "0";
//
//		String userId = param("userId");
//		String token = param("token");
//
//		/** start 20170901 xzhang  新增前端传递货币折算币种,兼容IOS折算币种老版本，优先取传递，其次去缓存。最后去默认*/
//		String legal_Tender = param("legal_tender");
//		String legalTenderParam = "";
//		if(StringUtils.isNotBlank(legal_Tender)){
//			if (!LegalTenderType.existKey(legal_Tender)) {
//				json(SystemCode.code_1001, "不支持的货币类型");
//				return;
//			}
//			LegalTenderType legalTenderType = LegalTenderType.valueOf(legal_Tender);
//			legalTenderParam = legalTenderType.getKey() + "_" + legalTenderType.getValue();
//		}
//		if(StringUtil.exist(legalTenderParam)){
//			legalTender = legalTenderParam;
//		}else if (StringUtil.exist(userId)) {
//			if (!isLogin(userId, token)) {
//				json(SystemCode.code_1003);
//				return;
//			}
//			String legalTenderCache = Cache.Get("user_legal_tender_" + userId);
//			legalTender = StringUtil.exist(legalTenderCache) ? legalTenderCache : legalTender;
//		}
//		/**end **/
//		cachePrice = Cache.Get("btc_" + legalTender.split("_")[0].toLowerCase());
//
//		try {
//			String exchangeType = param("exchangeType").toLowerCase();
//			String currencyTypes = request.getParameter("currencyTypes");
//			int time = intParam("step");// k线图时间类型
//			int size = intParam("size");// 获取k线数据数量
//			JSONArray currencyArr = new JSONArray();
//			/*if (StringUtils.isBlank(exchangeType)) {
//				json(SystemCode.code_1001, L("exchangeType参数不能为空！"));
//			}*/
//			/** start 20170927 xzhang  新增币种，直接读取配置文件中涉及的开放市场。取消展示限制*/
////			// FIXME add by suxinjie 20170806 一期需要展示的市场
////			List<String> marketShowVersion1 = new ArrayList<String>(){{
////				add("ltc");
////				add("eth");
////				add("gbc");
////			}};
//			/**end**/
//			/** start 20170918 xzhang  web端新增usdc市场，APP过滤usdc市场配置*/
//			List<String> marketShowVersion2 = new ArrayList<String>(){{
//				add("usdc");
//			}};
//			/**end**/
//			if (StringUtils.isBlank(currencyTypes)) {// 货币类型数组（不输入表示获取全部）
//				Map<String, Market> map = CommonUtil.sortMapByValue(Market.markets);
//				Iterator<Map.Entry<String, Market>> iter = map.entrySet().iterator();
//				while (iter.hasNext()) {
//					Map.Entry<String, Market> entry = iter.next();
//					Market m = entry.getValue();
//					//if(marketShowVersion1.contains(m.getNumberBiEn().toLowerCase())&&!marketShowVersion2.contains(m.getExchangeBiEn().toLowerCase())){
//					if(!marketShowVersion2.contains(m.getExchangeBiEn().toLowerCase())){
//						if (StringUtils.isNotBlank(exchangeType) && exchangeType.equalsIgnoreCase(m.getExchangeBiEn())) {
//							currencyArr.add(m.getNumberBiEn().toLowerCase()+"_"+exchangeType);
//						}else if(StringUtils.isBlank(exchangeType)){
//							currencyArr.add(m.getNumberBiEn().toLowerCase()+"_"+m.getExchangeBiEn().toLowerCase());
//						}
//					}
//				}
//			} else {//本币类型不为空
//				currencyTypes = currencyTypes.toLowerCase();
//				JSONArray arr = JSONObject.parseArray(currencyTypes);
//				if(StringUtils.isNotBlank(exchangeType)){//指定了兑换币种
//					for(int i=0;i<arr.size();i++){
//						String numberBi = arr.getString(i);
//						//if(marketShowVersion1.contains(numberBi)&&!marketShowVersion2.contains(numberBi)){
//						if(!marketShowVersion2.contains(numberBi)){
//							currencyArr.add(numberBi+"_"+exchangeType);
//						}
//					}
//				}else{//未指定兑换币种
//					for(int i=0;i<arr.size();i++){
//						String numberBi = arr.getString(i);
//						Map<String, Market> map = CommonUtil.sortMapByValue(Market.markets);
//						Iterator<Map.Entry<String, Market>> iter = map.entrySet().iterator();
//						while (iter.hasNext()) {
//							Map.Entry<String, Market> entry = iter.next();
//							Market m = entry.getValue();
//							//if(marketShowVersion1.contains(numberBi.toLowerCase())&&!marketShowVersion2.contains(m.getExchangeBiEn().toLowerCase())){
//							if(!marketShowVersion2.contains(m.getExchangeBiEn().toLowerCase())){
//								if (numberBi.equalsIgnoreCase(m.getNumberBiEn())) {
//									currencyArr.add(numberBi+"_"+m.getExchangeBiEn().toLowerCase());
//								}
//							}
//						}
//					}
//				}
//			}
//
//			JSONArray datas = new JSONArray();
//			for (Object currency : currencyArr) {
//				String marketName = currency.toString() ;
//				Market m = Market.getMarkeByName(marketName);
//				if (m == null) {
//					continue;
//				}
//				JSONArray jarray = new JSONArray();
//				// 处理K线数据 start
//				if (time > 0) {// 指定时间才进行处理
//					String rtn = data(time, m);
//
//					if (null != rtn && !"".equals(rtn)) {
//						JSONArray json = JSONObject.parseArray("[" + rtn + "]");
//						if (size > json.size() || size == 0) {
//							if (json.size() > 0) {
//								for (int i = json.size() - 1; i > -1; i--) {
//									JSONArray jarry = json.getJSONArray(i);
//									JSONArray jarry2 = new JSONArray();
//									if(jarry==null || jarry.size()==0){continue;}
//									jarry2.add(jarry.get(0));// 时间
//									jarry2.add(0);
//									jarry2.add(0);
//									jarry2.add(jarry.get(1));// 高开
//									jarry2.add(jarry.get(4));// 关闭
//									jarry2.add(jarry.get(2));// 高
//									jarry2.add(jarry.get(3));// 低
//									jarry2.add(jarry.get(5));// 总数量
//									jarray.add(jarry2);
//								}
//							}
//						} else {
//							if (json.size() > 0) {
//								for (int i = json.size() - 1; i > json.size() - size - 1; i--) {
//									JSONArray jarry = json.getJSONArray(i);
//									JSONArray jarry2 = new JSONArray();
//									if(jarry==null || jarry.size()==0){continue;}
//									jarry2.add(jarry.get(0));// 时间
//									jarry2.add(0);
//									jarry2.add(0);
//									jarry2.add(jarry.get(1));// 高开
//									jarry2.add(jarry.get(4));// 关闭
//									jarry2.add(jarry.get(2));// 高
//									jarry2.add(jarry.get(3));// 低
//									jarry2.add(jarry.get(5));// 总数量
//									jarray.add(jarry2);
//								}
//							}
//						}
//					}
//				}
//
//				// 处理K线数据 end
//
//				// 处理Ticker
//				String ticker = DishDataCacheService.getTicker(m.market);
//
//				if (StringUtils.isNotBlank(ticker)) {
//					JSONObject json = JSONObject.parseObject(ticker);
//					JSONObject tickerJson = json.getJSONObject("ticker");
//					tickerJson.put("highdollar", 0);
//					tickerJson.put("lowdollar", 0);
//					tickerJson.put("selldollar", 0);
//					tickerJson.put("highdollar", 0);
//					tickerJson.put("buydollar", 0);
//					tickerJson.put("dollar", 0);
//
//					BigDecimal legalTenderPrice = BigDecimal.ZERO;
//					if (StringUtil.exist(cachePrice)) {
//						legalTenderPrice = tickerJson.getBigDecimal("last").multiply(new BigDecimal(cachePrice));
//					}
//					tickerJson.put("legal_tender", legalTender.split("_")[1] + " " + DigitalUtil.roundDown(legalTenderPrice.doubleValue(), 2));
//
//					json.put("coinName", m.getNumberBi());
//					json.put("coinFullNameEn", m.getNumberBiFullName());
//					json.put("exeByRate", 1);
//					json.put("symbol", marketName);
//					json.put("moneyType", m.getNumberBiFundsType());// 币种类型
//					json.put("time", System.currentTimeMillis() / 1000);
//					json.put("type", 100);
//					json.put("cName", "比特全球");
//					json.put("name", "bitglobal");
//					json.put("tline", jarray);
//					datas.add(json);
//				}else{
//					JSONObject json = new JSONObject();
//					JSONObject tickerJson = new JSONObject();
//					tickerJson.put("highdollar", 0);
//					tickerJson.put("lowdollar", 0);
//					tickerJson.put("selldollar", 0);
//					tickerJson.put("highdollar", 0);
//					tickerJson.put("buydollar", 0);
//					tickerJson.put("dollar", 0);
//					tickerJson.put("legal_tender", legalTender.split("_")[1] + " 0.00");
//					tickerJson.put("high", 0);
//					tickerJson.put("low", 0);
//					tickerJson.put("sell", 0);
//					tickerJson.put("high", 0);
//					tickerJson.put("buy", 0);
//					tickerJson.put("last", 0);
//					tickerJson.put("weekRiseRate", 0);
//					tickerJson.put("monthRiseRate", 0);
//
//					json.put("coinName", m.getNumberBi());
//					json.put("coinFullNameEn", m.getNumberBiFullName());
//					json.put("exeByRate", 1);
//					json.put("symbol", marketName);
//					json.put("moneyType", m.getNumberBiFundsType());// 币种类型
//					json.put("time", System.currentTimeMillis() / 1000);
//					json.put("type", 100);
//					json.put("cName", "比特全球");
//					json.put("name", "bitglobal");
//					json.put("tline", jarray);
//					json.put("ticker", tickerJson);
//					datas.add(json);
//				}
//			}
//			Map<String, Object> retMap = new HashMap<String, Object>();
//			retMap.put("marketDatas", datas);
//			json(SystemCode.code_1000, retMap);
//
//		} catch (JSONException e) {
//			// TODO: handle exception
//			json(SystemCode.code_1001, L("json解析失败!"));
//		} catch (Exception e) {
//			// TODO: handle exception
//			log.error(e.toString(), e);
//			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
//		}
//	}
	
}

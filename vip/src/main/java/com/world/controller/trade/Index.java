//package com.world.controller.trade;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map.Entry;
//
//import com.alibaba.fastjson.JSONObject;
//import com.world.cache.Cache;
//import com.world.model.dao.level.UserVipLevelDao;
//import com.world.model.entity.Market;
//import com.world.util.string.StringUtil;
//import com.world.web.Page;
//import com.world.web.action.BaseAction;
//
//import cn.jpush.api.utils.StringUtils;
//
//
//
//public class Index extends BaseAction  {
//
//	private static final long serialVersionUID = 1L;
//
//	/***
//	 * 交易
//	 * @throws IOException
//	 */
//	@Page(Viewer = "/cn/trade/index.jsp")
//	public void index() {
//		String market = super.GetPrama(0);
//		if(StringUtils.isEmpty(market) || market.equals("null")){
//			market = Market.getDefMarketName();
//		}
//
//		JSONObject json = Market.getMarketByName(market);
//
//		List<String> moneyList = new ArrayList<String>();
//		Iterator<Entry<String,JSONObject>>  iter = Market.getMarketsMap().entrySet().iterator();
//		while(iter.hasNext()){
//			Entry<String,JSONObject> entry = iter.next();
//			JSONObject marketJson = entry.getValue();
//			if(!moneyList.contains(marketJson.getString("exchangeBi"))){
//				moneyList.add(marketJson.getString("exchangeBi"));
//			}
//			if(json==null){//市场有错,从配置中查找
//				String numberBi = market.split("_")[0];
//				if(numberBi.equalsIgnoreCase(marketJson.getString("numberBi")) || numberBi.equalsIgnoreCase(marketJson.getString("exchangeBi"))){
//					market = entry.getKey();
//					json = Market.getMarketByName(market);
//
//				}
//			}
//		}
//
//		if(json==null){//仍旧获取不到市场,获取默认市场
//			json =   Market.getMarketByName(Market.getDefMarketName());
//		}
//
//		String[] mergeDepth = new String[]{"0.000001","0.0001","0.0003","0.0005"};//默认档位
//
//
//		if(json!=null && json.containsKey("mergePrice") && !json.getString("mergePrice").equals("")){
//			mergeDepth = json.getString("mergePrice").split(",");
//		}
//		super.setAttr("mergeDepth", mergeDepth);//合并档位
//		super.setAttr("coinType", market.split("_")[0].toUpperCase());//本位币
//		super.setAttr("moneyType", market.split("_")[1].toUpperCase());//兑换币
//		super.setAttr("moneyList", moneyList);//兑换币集合
//		super.setAttr("market", market);//当前市场
//
//		JSONObject marketInfo = Market.getMarketByName(market);
//		String numberBiFullName = (String) marketInfo.get("numberBiFullName");
//		super.setAttr("numberBiFullName", StringUtil.exist(numberBiFullName)?numberBiFullName:market.split("_")[0].toUpperCase());
//
//		//获取所有市场
//		super.setAttr("markets", Market.getMarketsMap());
//
//		//20170327 modify by suxinjie 交易页面添加当前手续费 ( 市场手续费 * 等级优惠 = 当前手续费 )
//		int userId = userId();
//		BigDecimal feeRate = BigDecimal.ZERO;
//		if (userId > 0) {
//			BigDecimal feeDiscount =(BigDecimal)Cache.GetObj("user_vip_fee_discount_"+userId);
//			if (feeDiscount != null) {
//				feeRate = new BigDecimal(json.getDouble("feeRate")).multiply(feeDiscount);
//			} else {
//				UserVipLevelDao userVipLevelDao = new UserVipLevelDao();
//				BigDecimal feeRateDiscount = userVipLevelDao.getDiscountByVipRate(userId);
//				feeRate = new BigDecimal(json.getDouble("feeRate")).multiply(feeRateDiscount);
//			}
//			setAttr("feeRate", feeRate.multiply(new BigDecimal(100)).doubleValue() + "%");
//		} else {
//			setAttr("feeRate", new BigDecimal(json.getDouble("feeRate")).multiply(new BigDecimal(100)).doubleValue() + "%");
//		}
//	}
//
//	@Page(Viewer = "/cn/trade/index.jsp" , Cache = 60)
//	public void moneytype(){
//		String money = super.GetPrama(0);
//		String market ="";
//		if(StringUtils.isEmpty(money) || money.equals("null")){
//			market = Market.getDefMarketName();
//		}else{
//			Iterator<Entry<String,JSONObject>>  iter = Market.getMarketsMap().entrySet().iterator();
//			int defNo = 0;//市场序号
//			while(iter.hasNext()){
//				Entry<String,JSONObject> entry = iter.next();
//				JSONObject marketJson = entry.getValue();
//				if(money.equalsIgnoreCase(marketJson.getString("exchangeBi"))){
//					if(defNo==0 || defNo>marketJson.getIntValue("serNum")){//设置序号最小第一个为默认市场
//						market = entry.getKey();
//						defNo = marketJson.getIntValue("serNum");
//					}
//				}
//			}
//		}//end
//		try {
//			super.response.sendRedirect("/trade/"+market);
//		} catch (IOException e) {
//			log.error("内部异常", e);
//		}
//	}
//
//}
//

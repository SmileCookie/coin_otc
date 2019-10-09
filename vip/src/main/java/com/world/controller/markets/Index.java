package com.world.controller.markets;

import com.alibaba.fastjson.JSONObject;
import com.world.model.entity.Market;
import com.world.web.action.BaseAction;
import org.apache.commons.lang.StringUtils;

public class Index extends BaseAction {
	private static final long serialVersionUID = 1833277198306732411L;

	//Close By suxinjie 一期屏蔽该功能 老的行情图Close By suxinjie 一期屏蔽该功能
	//@Page(Viewer = "/cn/market/frame.jsp")
	public void index() {
		String market = super.GetPrama(0);
		if(StringUtils.isEmpty(market) || market.equals("null")){
			market = Market.getDefMarketName();
		}
		
		JSONObject m = Market.getMarketByName(market);
		if(m==null){
			m = Market.getMarketByName(Market.getDefMarketName());
		}
		setAttr("coinType", m.getString("numberBi"));
		setAttr("coinType_s",m.getString("numberBi").toLowerCase());
		setAttr("coinSymbol", m.getString("numberBiNote"));
		setAttr("coinName", m.getString("numberBiEn"));
		setAttr("moneyType", m.getString("exchangeBi"));
		setAttr("moneyType_s", m.getString("exchangeBi").toLowerCase());
		setAttr("moneySymbol", m.getString("exchangeBiNote"));
		setAttr("moneyName", m.getString("exchangeBiEn"));
		
		setAttr("currMarket", market);
		marketInit(market);
		//获取所有市场
		super.setAttr("markets", Market.getMarketsMap());
	}
	
	public void marketInit(String type){
		StringBuilder json = new StringBuilder("");
		json.append("[{");
		if(StringUtils.isNotEmpty(type)) {
			JSONObject market = Market.getMarketByName(type);
			
			if(market == null){
				return;
			}
			
			json.append("\"numberBi\":\"" + market.getString("numberBi") + "\"");
			json.append(",\"numberBiEn\":\"" + market.getString("numberBiEn") + "\"");
			json.append(",\"numberBiNote\":\"" + market.getString("numberBiNote")+ "\"");
			json.append(",\"numberBixNormal\":\""+ market.getString("numberBixNormal") + "\"");
			json.append(",\"numberBixShow\":\""+ market.getString("numberBixShow") + "\"");
			json.append(",\"numberBixDian\":\""+ market.getString("numberBixDian") + "\"");
			json.append(",\"exchangeBi\":\"" + market.getString("exchangeBi") + "\"");
			json.append(",\"exchangeBiEn\":\"" + market.getString("exchangeBiEn") + "\"");
			json.append(",\"exchangeBiNote\":\"" + market.getString("exchangeBiNote") + "\"");
			json.append(",\"market\":\"" + market.getString("market") + "\"");
			json.append(",\"exchangeBixNormal\":\"" + market.getString("exchangeBixNormal") + "\"");
			json.append(",\"exchangeBixShow\":\"" + market.getString("exchangeBixShow") + "\"");
			json.append(",\"exchangeBixDian\":\"" + market.getString("exchangeBixDian") + "\"");
			json.append(",\"entrustUrlBase\":\"" + market.getString("entrustUrlBase") + "\"");
			json.append(",\"feeRate\":\"" + market.getString("feeRate") + "\"");
			setAttr("market", market.getString("market"));
			setAttr("numberBi", market.getString("numberBi"));
			setAttr("numberBiEn", market.getString("numberBiEn"));
			setAttr("numberBiNote", market.getString("numberBiNote"));
			setAttr("numberBixDian", market.getString("numberBixDian"));
			setAttr("exchangeBi", market.getString("exchangeBi"));
			setAttr("exchangeBiEn", market.getString("exchangeBiEn"));
			setAttr("exchangeBiNote", market.getString("exchangeBiNote"));
			setAttr("exchangeBixDian", market.getString("exchangeBixDian"));
			setAttr("feeRate", market.getDouble("feeRate"));
	 	}
		json.append("}]");
		
		setAttr("marketData",json.toString());
	}
	
	//Close By suxinjie 一期屏蔽该功能 老的行情图
	//@Page(Viewer="/cn/market/kline/index.jsp")
	public void kline(){
		setAttr("tradesLimit",100);
		marketInit(param("symbol"));
		String symbol = param("symbol");
		
		setAttr("symbol", symbol);
		
		super.setAttr("markets", Market.getMarketsMap());
	}

}


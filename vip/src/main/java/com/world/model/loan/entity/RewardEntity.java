package com.world.model.loan.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.model.entity.coin.CoinProps;
import com.world.model.loan.worker.LoanAutoFactory;

public class RewardEntity extends Bean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RewardEntity() {
		super();
	}

	private BigDecimal converts=BigDecimal.ZERO;// 折合人民币设置默认为0
	private BigDecimal currentPrice=BigDecimal.ZERO;//当前价
	JSONObject prices = LoanAutoFactory.getPrices();
	private Date date;// 时间
	private int fundsType;// 类型
	private BigDecimal liXi;// 日收入
	private String allFt;//字符串类型
	private String onrFt;//￥、B、L、E

	/***
	 * 全部字符 rmb、btc、ltc、eth,etc
	 * @return
	 */
	public String getAllFt() {
		CoinProps coint = DatabasesUtil.coinProps(fundsType);
		allFt = coint.getPropTag();
		
		return allFt;
	}
	/***
	 * 单个字符 ￥、B、L、E
	 * @return
	 */
	public String getOnrFt() {
		CoinProps coint = DatabasesUtil.coinProps(fundsType);
		allFt = coint.getUnitTag();
		
		return onrFt;
	}
	/***
	 * 判断当前，类型为1 就是1，否则获取线上价格
	 * @return
	 */
	public BigDecimal getConverts() {
		CoinProps coint = DatabasesUtil.coinProps(fundsType);
		if(prices.containsKey(coint.getStag())){
			currentPrice = prices.getBigDecimal(coint.getStag());
		}else{
			currentPrice = BigDecimal.ONE;
		}
		 converts = liXi.multiply(currentPrice);//结合人民币=利息*当前实时价格
		 return converts;
	}
	
	public void setConverts(BigDecimal converts) {
		this.converts = converts;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getFundsType() {
		return fundsType;
	}

	public void setFundsType(int fundsType) {
		this.fundsType = fundsType;
	}
	
	public BigDecimal getLiXi() {
		return liXi;
	}

	public void setLiXi(BigDecimal liXi) {
		this.liXi = liXi;
	}
	
	public JSONObject getPrices() {
		return prices;
	}

	public void setPrices(JSONObject prices) {
		this.prices = prices;
	}
}

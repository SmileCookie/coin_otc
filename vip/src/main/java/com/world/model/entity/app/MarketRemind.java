package com.world.model.entity.app;

import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.LongIdEntity;


@Entity(noClassnameStored=true)
public class MarketRemind extends LongIdEntity {

	private static final long serialVersionUID = 7954754849968892906L;

	private String price;         //预警价格
	private String currency;      //当前币种
	private String exchange;      //交易币种
	private String userId;        //用户标记
	private String currencyPrice; //当前币种价格

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCurrencyPrice() {
		return currencyPrice;
	}

	public void setCurrencyPrice(String currencyPrice) {
		this.currencyPrice = currencyPrice;
	}

	/*private static final long serialVersionUID = 7954754849968892906L;
	private int status;//状态1开启 0 关闭
	private String high;//最高价
	private String low;//最低价
	private String userId;//
	private String symbol;//

	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getHigh() {
		return high==null?"":high;
	}
	public void setHigh(String high) {
		this.high = high;
	}
	public String getLow() {
		return low==null?"":low;
	}
	public void setLow(String low) {
		this.low = low;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}*/

	
}

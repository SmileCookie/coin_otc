package com.tenstar;

import java.io.Serializable;
import java.math.BigDecimal;

public class MessageCancle implements Serializable {
	private static final long serialVersionUID = 1344398344444777l;

	private int webId;// 站点id
	private String auth;// 认证字符串
	private int userId;//
	private long entrustId;// 委托id，取消单个的时候用
	private BigDecimal priceLow;// 最低价格
	private BigDecimal priceHigh;// 最高价格
	private BigDecimal numbers;// 数量
	private int type;// //0 按照区间设置 1取消买入  2取消卖出 3 取消所有
	private String message;
	private int status;// 返回的Info值
	private String market;
	public int getWebId() {
		return webId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setWebId(int webId) {
		this.webId = webId;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public long getEntrustId() {
		return entrustId;
	}

	public void setEntrustId(long entrustId) {
		this.entrustId = entrustId;
	}

	public BigDecimal getPriceLow() {
		return priceLow;
	}

	public void setPriceLow(BigDecimal priceLow) {
		this.priceLow = priceLow;
	}

	public BigDecimal getPriceHigh() {
		return priceHigh;
	}

	public void setPriceHigh(BigDecimal priceHigh) {
		this.priceHigh = priceHigh;
	}



	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public BigDecimal getNumbers() {
		return numbers;
	}

	public void setNumbers(BigDecimal numbers) {
		this.numbers = numbers;
	}

	
}

package com.tenstar;

import java.io.Serializable;
import java.math.BigDecimal;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;  
	  

	private int webId;//站点id
	private int types;//交易类型   0卖出 1买入 -1 取消
	private String auth;//认证字符串
	private int userId;//
	private String userName;//
	private BigDecimal numbers;//数量
	private BigDecimal unitPrice;//追高、止损单价
    private int status;//
    private String message;//
    private String code;
    private BigDecimal triggerPrice;//计划委托  追高、止损触发价格
    private BigDecimal unitPriceProfit;//抄底、止盈单价
    private BigDecimal triggerPriceProfit;//计划委托  抄底、止盈触发价格
    private BigDecimal totalMoney;//抄底追高总金额
    
    private String market;//市场
    
    //是否允许用户高价买低价卖或成交自己单据1=true,0=false, default 0;	guosj
    private int mt;
    
	public int getWebId() {
		return webId;
	}
	public void setWebId(int webId) {
		this.webId = webId;
	}
	public int getTypes() {
		return types;
	}
	public void setTypes(int types) {
		this.types = types;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getMt() {
		return mt;
	}
	public void setMt(int mt) {
		this.mt = mt;
	}
	public BigDecimal getNumbers() {
		return numbers;
	}
	public void setNumbers(BigDecimal numbers) {
		this.numbers = numbers;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public BigDecimal getTriggerPrice() {
		return triggerPrice;
	}
	public void setTriggerPrice(BigDecimal triggerPrice) {
		this.triggerPrice = triggerPrice;
	}
	public BigDecimal getUnitPriceProfit() {
		return unitPriceProfit;
	}
	public void setUnitPriceProfit(BigDecimal unitPriceProfit) {
		this.unitPriceProfit = unitPriceProfit;
	}
	public BigDecimal getTriggerPriceProfit() {
		return triggerPriceProfit;
	}
	public void setTriggerPriceProfit(BigDecimal triggerPriceProfit) {
		this.triggerPriceProfit = triggerPriceProfit;
	}
	public BigDecimal getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}
	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	
}

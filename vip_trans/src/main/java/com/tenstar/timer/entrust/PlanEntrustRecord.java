package com.tenstar.timer.entrust;

import java.io.Serializable;
import java.math.BigDecimal;

public class PlanEntrustRecord implements Serializable{
	private static final long serialVersionUID = -1777419685170893510L;

	public PlanEntrustRecord(long id, BigDecimal price, BigDecimal number, int webId,
			int userId, int types, BigDecimal srcNumbers, BigDecimal completeNumber,
			BigDecimal completeTotalMoney, long submitTime, int status,BigDecimal triggerPrice,BigDecimal priceProfit,BigDecimal triggerPriceProfit,BigDecimal totalMoney) {
		super();
		this.id = id;
		this.price = price;
		this.number = number;
		this.webId = webId;
		this.userId = userId;
		this.types = types;
		this.srcNumbers = srcNumbers;
		this.completeNumber = completeNumber;
		this.completeTotalMoney = completeTotalMoney;
		this.submitTime = submitTime;
		this.status = status;
		this.triggerPrice = triggerPrice;
		this.priceProfit = priceProfit;
		this.triggerPriceProfit = triggerPriceProfit;
		this.totalMoney = totalMoney;
	}
	public PlanEntrustRecord() {
		super();
	}
	private long id;
	private BigDecimal price;//低价
	private BigDecimal number; //总数量
	//主web
	private int webId;
	//用户id
	private int userId; 
	private int types;//1 买  0 卖
	private long freezId;//关联ID
	
	private BigDecimal srcNumbers;//原始委托数量
	private BigDecimal completeNumber;//已完成数量
	private BigDecimal completeTotalMoney;//完成总
	private long submitTime;
	private int status;
	private int matchTimes;//已经撮合次数
	private BigDecimal totalMoney;
	private BigDecimal triggerPrice;//追高、止损 计划 触发价格
	private BigDecimal triggerPriceProfit;//抄底、止盈  计划触发价格
	private BigDecimal priceProfit;//抄底、止盈 委托价格
	
	
	public int getMatchTimes() {
		return matchTimes;
	}
	public void setMatchTimes(int matchTimes) {
		this.matchTimes = matchTimes;
	}
	public BigDecimal getSrcNumbers() {
		return srcNumbers;
	}
	public void setSrcNumbers(BigDecimal srcNumbers) {
		this.srcNumbers = srcNumbers;
	}
	public BigDecimal getCompleteNumber() {
		return completeNumber;
	}
	public void setCompleteNumber(BigDecimal completeNumber) {
		this.completeNumber = completeNumber;
	}
	public BigDecimal getCompleteTotalMoney() {
		return completeTotalMoney;
	}
	public void setCompleteTotalMoney(BigDecimal completeTotalMoney) {
		this.completeTotalMoney = completeTotalMoney;
	}
	public long getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(long submitTime) {
		this.submitTime = submitTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getFreezId() {
		return freezId;
	}
	public void setFreezId(long freezId) {
		this.freezId = freezId;
	}
	public int getTypes() {
		return types;
	}
	public void setTypes(int types) {
		this.types = types;
	}
	public int getWebId() {
		return webId;
	}
	public void setWebId(int webId) {
		this.webId = webId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getNumber() {
		return number;
	}
	public void setNumber(BigDecimal number) {
		this.number = number;
	}
	public BigDecimal getTriggerPrice() {
		return triggerPrice;
	}
	public void setTriggerPrice(BigDecimal triggerPrice) {
		this.triggerPrice = triggerPrice;
	}
	
	public BigDecimal getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}
	public BigDecimal getTriggerPriceProfit() {
		return triggerPriceProfit;
	}
	public void setTriggerPriceProfit(BigDecimal triggerPriceProfit) {
		this.triggerPriceProfit = triggerPriceProfit;
	}
	public BigDecimal getPriceProfit() {
		return priceProfit;
	}
	public void setPriceProfit(BigDecimal priceProfit) {
		this.priceProfit = priceProfit;
	}
	
}

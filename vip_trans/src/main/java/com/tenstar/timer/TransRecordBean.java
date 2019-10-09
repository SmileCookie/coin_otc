package com.tenstar.timer;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;


/**
 * 交易详情记录
 * @author netpet
 */
public class TransRecordBean  extends Bean{
    private long transRecordId;
	private BigDecimal unitPrice;//单价
	private BigDecimal totalPrice;//总价
	private BigDecimal numbers;//比特币多少
	private long entrustIdBuy;
	private int userIdBuy;
	private long entrustIdSell; 
	private int userIdSell;
	private int types; 
	private long times; 
	private long timeMinute; 
	private int isCount;
	private int status;
	
	private int webIdBuy;
	private int webIdSell;
	
	public int getWebIdBuy() {
		return webIdBuy;
	}
	public void setWebIdBuy(int webIdBuy) {
		this.webIdBuy = webIdBuy;
	}
	public int getWebIdSell() {
		return webIdSell;
	}
	public void setWebIdSell(int webIdSell) {
		this.webIdSell = webIdSell;
	}
	public long getTransRecordId() {
		return transRecordId;
	}
	public void setTransRecordId(long transRecordId) {
		this.transRecordId = transRecordId;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public BigDecimal getNumbers() {
		return numbers;
	}
	public void setNumbers(BigDecimal numbers) {
		this.numbers = numbers;
	}
	public long getEntrustIdBuy() {
		return entrustIdBuy;
	}
	public void setEntrustIdBuy(long entrustIdBuy) {
		this.entrustIdBuy = entrustIdBuy;
	}
	public int getUserIdBuy() {
		return userIdBuy;
	}
	public void setUserIdBuy(int userIdBuy) {
		this.userIdBuy = userIdBuy;
	}
	public long getEntrustIdSell() {
		return entrustIdSell;
	}
	public void setEntrustIdSell(long entrustIdSell) {
		this.entrustIdSell = entrustIdSell;
	}
	public int getUserIdSell() {
		return userIdSell;
	}
	public void setUserIdSell(int userIdSell) {
		this.userIdSell = userIdSell;
	}
	public int getTypes() {
		return types;
	}
	public void setTypes(int types1) {
		types = types1;
	}
	public long getTimes() {
		return times;
	}
	public void setTimes(long times) {
		this.times = times;
	}
	public long getTimeMinute() {
		return timeMinute;
	}
	public void setTimeMinute(long timeMinute) {
		this.timeMinute = timeMinute;
	}
	public int getIsCount() {
		return isCount;
	}
	public void setIsCount(int isCount) {
		this.isCount = isCount;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}

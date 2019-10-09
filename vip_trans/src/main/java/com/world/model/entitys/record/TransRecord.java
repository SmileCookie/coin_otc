package com.world.model.entitys.record;

import com.world.data.big.table.TableInfo;
import com.world.data.big.table.UpdateWay;
import com.world.data.mysql.Bean;

import java.math.BigDecimal;

//购买成功的记录3天清理到新表
//取消记录直接清理到新表

//update by buxianguan 20171206 配合委托记录改版，迁移数据频率改成24小时
@TableInfo(databases = {"btcusdtentrust","dashbtcentrust","elfusdtentrust","eosbtcentrust","eosusdtentrust","etcbtcentrust","etcusdtentrust","ethbtcentrust","ethusdtentrust","kncusdtentrust","linkbtcentrust","ltcbtcentrust","ltcusdtentrust","omgbtcentrust","qtumusdtentrust","sntusdtentrust","zrxbtcentrust",
        "manabtcentrust","mcobtcentrust","lrcbtcentrust","dgdbtcentrust","vdsbtcentrust","vdsusdtentrust"
} , tableName = "transrecord" , tableDown = true , shardNum = 1 , field = "userIdBuy" , updateWay = UpdateWay.ASYNC, asyncFrequency = 300 , primaryKey = "transRecordId" ,
		conditions = {"times < (UNIX_TIMESTAMP(NOW()) - 86400) * 1000 and status>1 limit 0,100"})
public class TransRecord extends Bean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8970561569458517286L;
	private long transRecordId;
	private BigDecimal unitPrice;
	private BigDecimal totalPrice;
	private BigDecimal numbers;
	private BigDecimal feesBuy;
	private BigDecimal feesSell;
	private long entrustIdBuy;
	private int userIdBuy;
	private long entrustIdSell;
	private int userIdSell;
	private long types;
	private long times;
	private long timeMinute;
	private int status;
	private int isCount;
	private int webIdBuy;
	private int webIdSell;
	private int dealTimes;
	private int actStatus;//活动处理状态  0 不需要处理  1.待处理  2 已处理为奖励  3 已奖励
	
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
	public long getTypes() {
		return types;
	}
	public void setTypes(long types) {
		this.types = types;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getIsCount() {
		return isCount;
	}
	public void setIsCount(int isCount) {
		this.isCount = isCount;
	}
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
	public int getDealTimes() {
		return dealTimes;
	}
	public void setDealTimes(int dealTimes) {
		this.dealTimes = dealTimes;
	}
	public int getActStatus() {
		return actStatus;
	}
	public void setActStatus(int actStatus) {
		this.actStatus = actStatus;
	}

	public BigDecimal getFeesBuy() {
		return feesBuy;
	}

	public void setFeesBuy(BigDecimal feesBuy) {
		this.feesBuy = feesBuy;
	}

	public BigDecimal getFeesSell() {
		return feesSell;
	}

	public void setFeesSell(BigDecimal feesSell) {
		this.feesSell = feesSell;
	}
}

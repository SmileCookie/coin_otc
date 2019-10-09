package com.world.model.entitys;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.world.data.mysql.Bean;
/**
 * 交易详情记录
 * @author pc
 *
 */

public class TransRecord{

    private long transRecordId;
	private BigDecimal unitPrice;//单价
	private BigDecimal totalMoney;//总价
	private BigDecimal numbers;//比特币多少
	private int types;	//类型
	private long submitTime;
	private long entrustIdBuy;		//买方挂单id
	
	private long entrustIdSell;		//卖方挂单id
	
	/**
	 * 枚举类  /money/src/com/world/model/entity/score/TransStatus.java
	 * 	noNeed(0 , "无需处理") , need(1 , "待处理") , hasDeal(2 , "已处理，买卖双方已分") ,
	 *	fail(4 , "处理失败") , buyerHasDeal(5 , "已处理，买家已分") , 
	 *	sellerHasDeal(6 , "已处理，卖家已分") , noWeight(7 , "买卖均无需分权");
	 */
	private int status; //
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
	public BigDecimal getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}
	public BigDecimal getNumbers() {
		return numbers;
	}
	public void setNumbers(BigDecimal numbers) {
		this.numbers = numbers;
	}
	public long getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(long submitTime) {
		this.submitTime = submitTime;
	}
	public int getTypes() {
		return types;
	}
	public void setTypes(int types) {
		this.types = types;
	}
	public long getEntrustIdBuy() {
		return entrustIdBuy;
	}
	public void setEntrustIdBuy(long entrustIdBuy) {
		this.entrustIdBuy = entrustIdBuy;
	}
	public long getEntrustIdSell() {
		return entrustIdSell;
	}
	public void setEntrustIdSell(long entrustIdSell) {
		this.entrustIdSell = entrustIdSell;
	}
}

package com.world.model.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.world.data.mysql.Bean;
import com.world.model.entity.user.User;
import com.world.util.DigitalUtil;

/**
 * 交易详情记录
 * @author pc
 *
 */

public class TransRecord2 extends Bean{

    /**
	 * 
	 */
	private static final long serialVersionUID = -2314397625168812291L;
	private long transRecordId;
	private BigDecimal unitPrice;//单价
	private BigDecimal totalPrice;//总价
	private long btcs;//比特币多少
	private long entrustIdBuy;
	private int userIdBuy;
	private String userNameBuy;
	private long entrustIdSell;
	private int userIdSell;
	private String userNameSell;
	private int isBuy;


	private BigDecimal numbers;
	
	
	private long times;//成交时间
	private long timeMinute;//成交时间
	
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
	//是否被统计过
	private int isCount;
	
	private User buyer;//买家
	private User seller;//卖家
	
	private BigDecimal totalP;
	private double btc;
	
	public String getShowStatu(){
		String showStatu = "";
		if(status == 2){
			showStatu = "专业版买卖";
		}else if(status == 5){
			showStatu = "专业大众交叉买卖";
		}else if(status == 6){
			showStatu = "专业大众交叉买卖";
		}else if(status == 7){
			showStatu = "大众版买卖";
		}
		return showStatu;
	}
	
	public String getShowStatu2(){
		String showStatu = "";
		if(status == 7){
			showStatu = "大众版买卖";
		}else if(status == 5){
			showStatu = "大众版买";
		}else if(status == 6){
			showStatu = "大众版卖";
		}
		return showStatu;
	}
	
	public BigDecimal getTotalP() {
		switch(status){
		case 5:
		case 6:
			totalP =  totalPrice.divide(BigDecimal.valueOf(2), 4, BigDecimal.ROUND_DOWN);
			break;
		case 2:
		case 7:
			totalP = totalPrice;
			break;
		}
		
		return totalP;
	}
	public double getBtc() {
		switch(status){
		case 5:
		case 6:
			btc = DigitalUtil.div(DigitalUtil.div(btcs, 2), fee.btcFee, 7);
			break;
		case 2:
		case 7:
			btc = DigitalUtil.div(btcs, fee.btcFee, 7);
			break;
		}
		return btc;
	}
	
	public User getBuyer() {
		return buyer;
	}
	public void setBuyer(User buyer) {
		this.buyer = buyer;
	}
	public User getSeller() {
		return seller;
	}
	public void setSeller(User seller) {
		this.seller = seller;
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
	public void setNumbers(BigDecimal numbers) {
		this.numbers = numbers;
	}
	public BigDecimal getNumbers() {
		return numbers;
	}
	public long getBtcs() {
		return btcs;
	}
	public void setBtcs(long btcs) {
		this.btcs = btcs;
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
	public String getUserNameBuy() {
		return userNameBuy;
	}
	public void setUserNameBuy(String userNameBuy) {
		this.userNameBuy = userNameBuy;
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
	public String getUserNameSell() {
		return userNameSell;
	}
	public void setUserNameSell(String userNameSell) {
		this.userNameSell = userNameSell;
	}
	public int getIsBuy() {
		return isBuy;
	}
	public void setIsBuy(int isBuy) {
		this.isBuy = isBuy;
	}
	public int getIsCount() {
		return isCount;
	}
	public void setIsCount(int isCount) {
		this.isCount = isCount;
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

	public Date timeDate;

	public Date getTimeDate() {
		timeDate = new Date(times);
		return timeDate;
	}

	public void setTimeDate(Date timeDate) {
		this.timeDate = timeDate;
	}
}

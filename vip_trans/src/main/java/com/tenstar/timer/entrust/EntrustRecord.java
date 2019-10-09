package com.tenstar.timer.entrust;

import com.world.model.entitys.entrust.EntrustBean;

import java.io.Serializable;
import java.math.BigDecimal;

public class EntrustRecord implements Serializable{
	private static final long serialVersionUID = -1777419685170893510L;

	public EntrustRecord(long id, BigDecimal price, BigDecimal number, int webId,
			int userId, int types, BigDecimal srcNumbers, BigDecimal completeNumber,
			BigDecimal completeTotalMoney, long submitTime, int status,BigDecimal feeRate) {
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
		this.feeRate = feeRate;
	}
	public EntrustRecord() {
		super();
	}

    public static EntrustRecord transferRecordByBean(EntrustBean eb) {
        return new EntrustRecord(eb.getEntrustId(), eb.getUnitPrice(), eb.getNumbers().subtract(eb.getCompleteNumber()), eb.getWebId(), eb.getUserId(), eb.getTypes(),
                eb.getNumbers(), eb.getCompleteNumber(), eb.getCompleteTotalMoney(), eb.getSubmitTime(), eb.getStatus(), eb.getFeeRate());
    }

    private long id;
	private BigDecimal price;
	private BigDecimal number;
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
	private BigDecimal feeRate;//交易手续费费率

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
	public BigDecimal getFeeRate() {
		return feeRate;
	}
	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}


}

package com.world.model.balaccount.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.world.data.mysql.Bean;

/**
 * <p>标题: 充值记录对账实体类-从支付中心同步</p>
 * <p>描述: 充值记录对账实体类-从支付中心同步</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version 
 */
public class FinAccDetailsBean extends Bean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*编号*/
	private long id;
	/*交易编号*/
	private String txId;
	/*交易编号,代表txId加上系列号*/
	private String txIdN;
	/*唯一标识接口对接使用*/
	private String uuid;
	/*交易金额*/
	private BigDecimal tsAmount;
	/*资金类型*/
	private int fundType;
	/*状态*/
	private int status;
	/*来源地址*/
	private String fromAddr;
	/*提现的地址*/
	private String toAddr;
	/*记录发送或者接收时候的hash*/
	private String addHash;
	/*发送时间*/
	private Timestamp sendTime;
	/*钱包当前余额*/
	private BigDecimal amount;
	/*区块高度*/
	private int blockHeight;
	/*确认时间*/
	private Timestamp configTime;
	/*已经确认的次数*/
	private int confirmTimes;
	/*创建时间*/
	private Timestamp createTime;


	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTxIdN() {
		return txIdN;
	}
	public void setTxIdN(String txIdN) {
		this.txIdN = txIdN;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public BigDecimal getTsAmount() {
		return tsAmount;
	}
	public void setTsAmount(BigDecimal tsAmount) {
		this.tsAmount = tsAmount;
	}
	public int getFundType() {
		return fundType;
	}
	public void setFundType(int fundType) {
		this.fundType = fundType;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getFromAddr() {
		return fromAddr;
	}
	public void setFromAddr(String fromAddr) {
		this.fromAddr = fromAddr;
	}
	public String getToAddr() {
		return toAddr;
	}
	public void setToAddr(String toAddr) {
		this.toAddr = toAddr;
	}
	public String getAddHash() {
		return addHash;
	}
	public void setAddHash(String addHash) {
		this.addHash = addHash;
	}
	public Timestamp getSendTime() {
		return sendTime;
	}
	public void setSendTime(Timestamp sendTime) {
		this.sendTime = sendTime;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public int getBlockHeight() {
		return blockHeight;
	}
	public void setBlockHeight(int blockHeight) {
		this.blockHeight = blockHeight;
	}
	public Timestamp getConfigTime() {
		return configTime;
	}
	public void setConfigTime(Timestamp configTime) {
		this.configTime = configTime;
	}
	public int getConfirmTimes() {
		return confirmTimes;
	}
	public void setConfirmTimes(int confirmTimes) {
		this.confirmTimes = confirmTimes;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getTxId() {
		return txId;
	}
	public void setTxId(String txId) {
		this.txId = txId;
	}
}

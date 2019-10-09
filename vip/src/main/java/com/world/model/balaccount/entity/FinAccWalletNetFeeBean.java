package com.world.model.balaccount.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.world.data.mysql.Bean;

/**
 * <p>标题: 钱包流水同步查询</p>
 * <p>描述: 钱包流水同步查询finAccWalletBillDetails</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version 
 */
public class FinAccWalletNetFeeBean extends Bean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*主键自增长*/
	private long id;
	/*交易编号*/
	private String txId;
	/*代表txId加上系列号.格式为xxx_xxx*/
	private String txIdN;
	/*交易平台发给支付中心的*/
	private String uuid;
	/*钱包编号*/
	private String walId;
	/*钱包名称*/
	private String walName;
	/*钱包类型:1：充值热钱包，2：提现热钱包，3：冷钱包*/
	private String walType;
	/*交易金额*/
	private BigDecimal txAmount;
	/*资金类型*/
	private int fundsType;
	/*费用方向:1：充值热钱包到冷钱包，2：冷钱包到提现热钱包，3：提现热钱包到用户*/
	private int feeDirType;
	/*网络费*/
	private BigDecimal netCost;
	/*钱包当前余额*/
	private BigDecimal amount;
	/*区块高度*/
	private int blockHeight;
	/*确认时间*/
	private Timestamp configTime;
	/*创建时间*/
	private Timestamp createTime;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTxId() {
		return txId;
	}
	public void setTxId(String txId) {
		this.txId = txId;
	}
	public String getWalId() {
		return walId;
	}
	public void setWalId(String walId) {
		this.walId = walId;
	}
	public String getWalName() {
		return walName;
	}
	public void setWalName(String walName) {
		this.walName = walName;
	}
	public String getWalType() {
		return walType;
	}
	public void setWalType(String walType) {
		this.walType = walType;
	}
	public BigDecimal getTxAmount() {
		return txAmount;
	}
	public void setTxAmount(BigDecimal txAmount) {
		this.txAmount = txAmount;
	}
	public int getFundsType() {
		return fundsType;
	}
	public void setFundsType(int fundsType) {
		this.fundsType = fundsType;
	}
	public int getFeeDirType() {
		return feeDirType;
	}
	public void setFeeDirType(int feeDirType) {
		this.feeDirType = feeDirType;
	}
	public BigDecimal getNetCost() {
		return netCost;
	}
	public void setNetCost(BigDecimal netCost) {
		this.netCost = netCost;
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
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
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
}

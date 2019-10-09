package com.world.model.entity.usercap.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.world.data.mysql.Bean;

/**
 * <p>标题: 用户账单检查错误记录表</p>
 * <p>描述: 用户账单检查错误记录表</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version 
 */
public class UserBillMonErrorBean extends Bean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* 主键ID' */
	private int ubmid;
	/* 异常用户记录表id */
	private String aurId;
	/* bill表ID */
	private long id;
	private String userId;// 用户ID
	private String userName;
	private int type;// 0提现 1PPS收币 2PPLNS收币 5理财 6变现 7系统充值 8系统扣除
	private int status;
	private BigDecimal amount;
	private Timestamp sendTime;// 时间
	private String remark;
	private BigDecimal balance;
	private BigDecimal fees;// 手续费
	private String adminId;
	private String timestr;
	private String entrustId;
	private int fundsType;// 资金类型

	private String coinName;// 币种名称

	/* start by cxb 20170317 新增字段 */
	/* 是否已核算,账户管理每日结算使用,默认值0，核算中1，已核算2,3留存最后一条记录每个用户资金类型的 */
	private int isfinaaccount;
	/* 用户资金监控表监控编号 */
	private String ucmId;

	/* end */
	public int getUbmid() {
		return ubmid;
	}

	public void setUbmid(int ubmid) {
		this.ubmid = ubmid;
	}

	public String getAurId() {
		return aurId;
	}

	public void setAurId(String aurId) {
		this.aurId = aurId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Timestamp getSendTime() {
		return sendTime;
	}

	public void setSendTime(Timestamp sendTime) {
		this.sendTime = sendTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getFees() {
		return fees;
	}

	public void setFees(BigDecimal fees) {
		this.fees = fees;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public String getTimestr() {
		return timestr;
	}

	public void setTimestr(String timestr) {
		this.timestr = timestr;
	}

	public String getEntrustId() {
		return entrustId;
	}

	public void setEntrustId(String entrustId) {
		this.entrustId = entrustId;
	}

	public int getFundsType() {
		return fundsType;
	}

	public void setFundsType(int fundsType) {
		this.fundsType = fundsType;
	}

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	public int getIsfinaaccount() {
		return isfinaaccount;
	}

	public void setIsfinaaccount(int isfinaaccount) {
		this.isfinaaccount = isfinaaccount;
	}

	public String getUcmId() {
		return ucmId;
	}

	public void setUcmId(String ucmId) {
		this.ucmId = ucmId;
	}
}

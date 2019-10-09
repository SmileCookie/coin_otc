package com.world.model.entity.usercap.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.model.entity.coin.CoinProps;

/**
 * <p>标题: 异常用户记录表</p>
 * <p>描述: 异常用户记录表</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version 
 */
public class AbnUserRecordBean extends Bean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/* 主键，自增长 */
	private int id;
	/* 异常记录编号 */
	private String aurId;
	/* 监控编号,用户资金监控表 */
	private String ucmId;
	/* 用户ID */
	private String userId;
	/* 用户名 */
	private String userName;
	/* 资金类型 */
	private int fundsType;
	/* 检查时点余额bill */
	private BigDecimal checkBillAmount;
	/* 检查时点余额user */
	private BigDecimal checkPayUserAmount;
	/* 流水合计金额 */
	private BigDecimal billTotalAmount;
	/* 监控时间 */
	private Timestamp monTime;
	/* 处理备注 */
	private String dealRemark;
	/* 处理人编号 */
	private int dealUserId;
	/* 处理时间 */
	private Timestamp dealTime;
	/* 该用户资金类型最后一笔bill流水号 */
	/* 最后一笔流水号bill */
	private int billId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAurId() {
		return aurId;
	}

	public void setAurId(String aurId) {
		this.aurId = aurId;
	}

	public String getUcmId() {
		return ucmId;
	}

	public void setUcmId(String ucmId) {
		this.ucmId = ucmId;
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

	public int getFundsType() {
		return fundsType;
	}

	public void setFundsType(int fundsType) {
		this.fundsType = fundsType;
	}

	public BigDecimal getCheckBillAmount() {
		return checkBillAmount;
	}

	public void setCheckBillAmount(BigDecimal checkBillAmount) {
		this.checkBillAmount = checkBillAmount;
	}

	public BigDecimal getCheckPayUserAmount() {
		return checkPayUserAmount;
	}

	public void setCheckPayUserAmount(BigDecimal checkPayUserAmount) {
		this.checkPayUserAmount = checkPayUserAmount;
	}

	public BigDecimal getBillTotalAmount() {
		return billTotalAmount;
	}

	public void setBillTotalAmount(BigDecimal billTotalAmount) {
		this.billTotalAmount = billTotalAmount;
	}

	public Timestamp getMonTime() {
		return monTime;
	}

	public void setMonTime(Timestamp monTime) {
		this.monTime = monTime;
	}

	public String getDealRemark() {
		return dealRemark;
	}

	public void setDealRemark(String dealRemark) {
		this.dealRemark = dealRemark;
	}

	public int getDealUserId() {
		return dealUserId;
	}

	public void setDealUserId(int dealUserId) {
		this.dealUserId = dealUserId;
	}

	public Timestamp getDealTime() {
		return dealTime;
	}

	public void setDealTime(Timestamp dealTime) {
		this.dealTime = dealTime;
	}

	public int getBillId() {
		return billId;
	}

	public void setBillId(int billId) {
		this.billId = billId;
	}

	//获得币种
	public CoinProps getCoin(){
		return DatabasesUtil.coinProps(fundsType);
	}

	//获取币种名称
	public String getCoinName() {
		return getCoin().getPropTag();
	}

}

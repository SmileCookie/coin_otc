package com.world.model.entity.usercap.entity;

import java.sql.Timestamp;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.model.entity.coin.CoinProps;

/**
 * <p>标题: 用户资金监控表</p>
 * <p>描述: 用户资金监控表</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version 
 */
public class UserCapMonitorBean extends Bean  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/* 主键，自增长 */
	private int id;
	/* 监控编号 */
	private String ucmId;
	/* 监控时间 */
	private Timestamp monTime;
	/* 资金类型 */
	private int fundsType;
	/* 检查用户总数 */
	private int checkUserNum;
	/* 正常用户数 */
	private int correctUserNum;
	/* 异常用户数 */
	private int errorUuserNum;
	/* 检查结果,0默认，1正常，2异常 */
	private int checkResult;
	/* 处理备注 */
	private String dealRemark;
	/* 处理人编号 */
	private int dealUserId;
	/* 处理时间 */
	private Timestamp dealTime;
	/* 最后一笔流水号bill */
	private int billId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUcmId() {
		return ucmId;
	}

	public void setUcmId(String ucmId) {
		this.ucmId = ucmId;
	}

	public Timestamp getMonTime() {
		return monTime;
	}

	public void setMonTime(Timestamp monTime) {
		this.monTime = monTime;
	}

	public int getFundsType() {
		return fundsType;
	}

	public void setFundsType(int fundsType) {
		this.fundsType = fundsType;
	}

	public int getCheckUserNum() {
		return checkUserNum;
	}

	public void setCheckUserNum(int checkUserNum) {
		this.checkUserNum = checkUserNum;
	}

	public int getCorrectUserNum() {
		return correctUserNum;
	}

	public void setCorrectUserNum(int correctUserNum) {
		this.correctUserNum = correctUserNum;
	}

	public int getErrorUuserNum() {
		return errorUuserNum;
	}

	public void setErrorUuserNum(int errorUuserNum) {
		this.errorUuserNum = errorUuserNum;
	}

	public int getCheckResult() {
		return checkResult;
	}

	public void setCheckResult(int checkResult) {
		this.checkResult = checkResult;
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

package com.world.model.entity.financialproift;

import java.math.BigDecimal;

import com.world.data.mysql.Bean;

/**
 * 理财产品表
 */
public class FinancialProduct extends Bean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	/* 产品编号 */
	private String proId;
	/* 产品名称 */
	private String proName;
	/* 资金类型 */
	private int fundsType;
	/* 产品金额 */
	private BigDecimal proAmount;
	/* 产品期次 */
	private String proPeriod;
	/* 投资总人数 */
	private int proTotalUser;
	/* 投资总金额 */
	private BigDecimal proTotalAmount;
	/* 产品状态，0默认，1开启，2关闭 */
	private int proState;
	/* 产品开始时间 */
	private long proStartTime;
	/* 打底总金额 */
	private BigDecimal proBaseAmount;
	/* 打底总人数 */
	private int proBaseUser;
	
	private long sumProTotalUser;
	
	private BigDecimal sumProTotalAmount;
	
	private BigDecimal sumInvestUsdtAmount;
	
	private BigDecimal sumExpectProfitUsdt;
	
	public BigDecimal getSumInvestUsdtAmount() {
		return sumInvestUsdtAmount;
	}

	public void setSumInvestUsdtAmount(BigDecimal sumInvestUsdtAmount) {
		this.sumInvestUsdtAmount = sumInvestUsdtAmount;
	}

	public BigDecimal getSumExpectProfitUsdt() {
		return sumExpectProfitUsdt;
	}

	public void setSumExpectProfitUsdt(BigDecimal sumExpectProfitUsdt) {
		this.sumExpectProfitUsdt = sumExpectProfitUsdt;
	}

	public long getSumProTotalUser() {
		return sumProTotalUser;
	}

	public void setSumProTotalUser(long sumProTotalUser) {
		this.sumProTotalUser = sumProTotalUser;
	}

	public BigDecimal getSumProTotalAmount() {
		return sumProTotalAmount;
	}

	public void setSumProTotalAmount(BigDecimal sumProTotalAmount) {
		this.sumProTotalAmount = sumProTotalAmount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public int getFundsType() {
		return fundsType;
	}

	public void setFundsType(int fundsType) {
		this.fundsType = fundsType;
	}

	public BigDecimal getProAmount() {
		return proAmount;
	}

	public void setProAmount(BigDecimal proAmount) {
		this.proAmount = proAmount;
	}

	public String getProPeriod() {
		return proPeriod;
	}

	public void setProPeriod(String proPeriod) {
		this.proPeriod = proPeriod;
	}

	public int getProTotalUser() {
		return proTotalUser;
	}

	public void setProTotalUser(int proTotalUser) {
		this.proTotalUser = proTotalUser;
	}

	public BigDecimal getProTotalAmount() {
		return proTotalAmount;
	}

	public void setProTotalAmount(BigDecimal proTotalAmount) {
		this.proTotalAmount = proTotalAmount;
	}

	public int getProState() {
		return proState;
	}

	public void setProState(int proState) {
		this.proState = proState;
	}

	public long getProStartTime() {
		return proStartTime;
	}

	public void setProStartTime(long proStartTime) {
		this.proStartTime = proStartTime;
	}

	public BigDecimal getProBaseAmount() {
		return proBaseAmount;
	}

	public void setProBaseAmount(BigDecimal proBaseAmount) {
		this.proBaseAmount = proBaseAmount;
	}

	public int getProBaseUser() {
		return proBaseUser;
	}

	public void setProBaseUser(int proBaseUser) {
		this.proBaseUser = proBaseUser;
	}
}

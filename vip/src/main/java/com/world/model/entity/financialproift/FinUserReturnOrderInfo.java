package com.world.model.entity.financialproift;

import java.math.BigDecimal;
import java.util.Date;

import com.world.data.mysql.Bean;

public class FinUserReturnOrderInfo extends Bean{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*主键*/
	private int id;
	/*用户编号*/
	private int userId;
	/*用户名*/
	private String userName;
	/*投资时间*/
	private Date profitTime;
	/*复投时间*/
	private Date resetProfitTime;
	/*预期收益转换为USDT金额，出局金额*/
	private BigDecimal expectProfitUsdt;
	/*累积已获得的USDT*/
	private BigDecimal staticProfitSumUsdt;
	
	/*投资均价*/
	private BigDecimal investAvergPrice;
	/*1未出局用户，2出局用户*/
	private int returnType;
	/*批次*/
	private String batchNo;
	/*分配序号*/
	private int seqNo;
	/*0未分配，1已分配*/
	private int dealFlag;
	private String dealFlagDESC;
	private String returnTypeDESC;
	
	/*投资金额*/
	private BigDecimal investAmount;

	private BigDecimal recoveryUsdt;

	private Date dealTime;

	private int authPayFlag;

	public int getAuthPayFlag() {
		return authPayFlag;
	}

	public void setAuthPayFlag(int authPayFlag) {
		this.authPayFlag = authPayFlag;
	}

	public BigDecimal getInvestAvergPrice() {
		return investAvergPrice;
	}

	public void setInvestAvergPrice(BigDecimal investAvergPrice) {
		this.investAvergPrice = investAvergPrice;
	}

	public Date getDealTime() {
		return dealTime;
	}

	public void setDealTime(Date dealTime) {
		this.dealTime = dealTime;
	}

	public BigDecimal getRecoveryUsdt() {
		return recoveryUsdt;
	}

	public void setRecoveryUsdt(BigDecimal recoveryUsdt) {
		this.recoveryUsdt = recoveryUsdt;
	}

	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	private BigDecimal profit;

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public String getReturnTypeDESC() {
		return returnTypeDESC;
	}

	public void setReturnTypeDESC(String returnTypeDESC) {
		this.returnTypeDESC = returnTypeDESC;
	}

	public String getDealFlagDESC() {
		return dealFlagDESC;
	}

	public void setDealFlagDESC(String dealFlagDESC) {
		this.dealFlagDESC = dealFlagDESC;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getProfitTime() {
		return profitTime;
	}
	public void setProfitTime(Date profitTime) {
		this.profitTime = profitTime;
	}
	public Date getResetProfitTime() {
		return resetProfitTime;
	}
	public void setResetProfitTime(Date resetProfitTime) {
		this.resetProfitTime = resetProfitTime;
	}
	public BigDecimal getExpectProfitUsdt() {
		return expectProfitUsdt;
	}
	public void setExpectProfitUsdt(BigDecimal expectProfitUsdt) {
		this.expectProfitUsdt = expectProfitUsdt;
	}
	public BigDecimal getStaticProfitSumUsdt() {
		return staticProfitSumUsdt;
	}
	public void setStaticProfitSumUsdt(BigDecimal staticProfitSumUsdt) {
		this.staticProfitSumUsdt = staticProfitSumUsdt;
	}
	public int getReturnType() {
		return returnType;
	}
	public void setReturnType(int returnType) {
		this.returnType = returnType;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public int getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}
	public int getDealFlag() {
		return dealFlag;
	}
	public void setDealFlag(int dealFlag) {
		this.dealFlag = dealFlag;
	}
}

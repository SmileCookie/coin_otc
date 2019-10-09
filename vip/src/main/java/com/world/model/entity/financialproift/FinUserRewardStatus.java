package com.world.model.entity.financialproift;

import java.math.BigDecimal;
import java.util.Date;

import com.world.data.mysql.Bean;

@SuppressWarnings("serial")
public class FinUserRewardStatus extends Bean{
	
	private long    id;
	private String seqNo;
	private int distType;
	private int superNodeProfitCount;
	private BigDecimal distBal;
	private BigDecimal distBalOriginal;
	private BigDecimal yetAmt;
	private int distStatus;
	private int distFlag;
	private Date distStartTime;
	private Date distEndTime;
	private BigDecimal vdsPer;
	private BigDecimal usdtPer;
	private int       distNum;
	private Date distTime;
	private BigDecimal usdtPrice;
	private Date createTime;
	private Date updatetime;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public BigDecimal getDistBal() {
		return distBal;
	}
	public void setDistBal(BigDecimal distBal) {
		this.distBal = distBal;
	}
	
	public BigDecimal getYetAmt() {
		return yetAmt;
	}
	public void setYetAmt(BigDecimal yetAmt) {
		this.yetAmt = yetAmt;
	}
	public int getDistStatus() {
		return distStatus;
	}
	public void setDistStatus(int distStatus) {
		this.distStatus = distStatus;
	}
	public BigDecimal getVdsPer() {
		return vdsPer;
	}
	public void setVdsPer(BigDecimal vdsPer) {
		this.vdsPer = vdsPer;
	}
	public BigDecimal getUsdtPer() {
		return usdtPer;
	}
	public void setUsdtPer(BigDecimal usdtPer) {
		this.usdtPer = usdtPer;
	}
	public int getDistNum() {
		return distNum;
	}
	public void setDistNum(int distNum) {
		this.distNum = distNum;
	}
	
	public BigDecimal getUsdtPrice() {
		return usdtPrice;
	}
	public void setUsdtPrice(BigDecimal usdtPrice) {
		this.usdtPrice = usdtPrice;
	}
	
	public Date getDistTime() {
		return distTime;
	}
	public void setDistTime(Date distTime) {
		this.distTime = distTime;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public int getDistFlag() {
		return distFlag;
	}
	public void setDistFlag(int distFlag) {
		this.distFlag = distFlag;
	}
	public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	public Date getDistStartTime() {
		return distStartTime;
	}
	public void setDistStartTime(Date distStartTime) {
		this.distStartTime = distStartTime;
	}
	public Date getDistEndTime() {
		return distEndTime;
	}
	public void setDistEndTime(Date distEndTime) {
		this.distEndTime = distEndTime;
	}
	public int getDistType() {
		return distType;
	}
	public void setDistType(int distType) {
		this.distType = distType;
	}
	public int getSuperNodeProfitCount() {
		return superNodeProfitCount;
	}
	public void setSuperNodeProfitCount(int superNodeProfitCount) {
		this.superNodeProfitCount = superNodeProfitCount;
	}
	public BigDecimal getDistBalOriginal() {
		return distBalOriginal;
	}
	public void setDistBalOriginal(BigDecimal distBalOriginal) {
		this.distBalOriginal = distBalOriginal;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	
}

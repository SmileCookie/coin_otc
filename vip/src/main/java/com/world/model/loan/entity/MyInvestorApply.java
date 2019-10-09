package com.world.model.loan.entity;

import java.sql.Timestamp;

import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.LongIdEntity;
import com.world.model.entity.pay.PayUserBean;

@Entity(noClassnameStored = true)
public class MyInvestorApply extends LongIdEntity {

	private static final long serialVersionUID = 6861924468775467479L;

	private String userId;
	private String userName;
	private String investmentCurrency;
	private String investmentAmount;
	private String investmentCycle;
	private String investmentRate;
	private String guarantee;
	private String name;
	private String phone;
	private Timestamp date;
	private int status = 1;// 状态 1未处理 2通过并开启 3不通过
	
	private P2pUser p2pUser;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getInvestmentCurrency() {
		return investmentCurrency;
	}
	public void setInvestmentCurrency(String investmentCurrency) {
		this.investmentCurrency = investmentCurrency;
	}
	public String getInvestmentAmount() {
		return investmentAmount;
	}
	public void setInvestmentAmount(String investmentAmount) {
		this.investmentAmount = investmentAmount;
	}
	public String getInvestmentRate() {
		return investmentRate;
	}
	public void setInvestmentRate(String investmentRate) {
		this.investmentRate = investmentRate;
	}
	public String getGuarantee() {
		return guarantee;
	}
	public void setGuarantee(String guarantee) {
		this.guarantee = guarantee;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getInvestmentCycle() {
		return investmentCycle;
	}
	public void setInvestmentCycle(String investmentCycle) {
		this.investmentCycle = investmentCycle;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public P2pUser getP2pUser() {
		return p2pUser;
	}
	public void setP2pUser(P2pUser p2pUser) {
		this.p2pUser = p2pUser;
	}
	
}

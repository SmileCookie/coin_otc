package com.world.model.financial.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.world.data.mysql.Bean;
import com.world.model.entity.admin.AdminUser;

/**
 * 财务系统
 * 保存每小时各个账户的余额信息   做图表分析
 * @author Administrator
 *
 */

public class FinanChart extends Bean {
	
	public FinanChart() {
		super();
	}
	
	private static final long serialVersionUID = -8791435182844009949L;
  
	private int id;
    private int accountId;
    private int fundType;//资金类型  1为RMB   2为BTC    3为LTC
    private BigDecimal amount;
    
    private BigDecimal exceptAmount;
    
    private Timestamp createTime;
    private long groupTime;//保存到时间的小时
    
    private String memo;
    private int adminId;
    
    private BigDecimal funds;
    public BigDecimal getFunds() {
    	this.funds = amount;
		return funds;
	}
    
    public BigDecimal getTotalBalance(){
    	return amount.add(exceptAmount);
    }
    
    private AdminUser aUser;
    private FinanAccount account;

	public AdminUser getaUser() {
		return aUser;
	}

	public void setaUser(AdminUser aUser) {
		this.aUser = aUser;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public int getFundType() {
		return fundType;
	}

	public void setFundType(int fundType) {
		this.fundType = fundType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setFunds(BigDecimal funds) {
		this.funds = funds;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public FinanAccount getAccount() {
		return account;
	}

	public void setAccount(FinanAccount account) {
		this.account = account;
	}

	public long getGroupTime() {
		return groupTime;
	}

	public void setGroupTime(long groupTime) {
		this.groupTime = groupTime;
	}

	public BigDecimal getExceptAmount() {
		return exceptAmount;
	}

	public void setExceptAmount(BigDecimal exceptAmount) {
		this.exceptAmount = exceptAmount;
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}

 }   

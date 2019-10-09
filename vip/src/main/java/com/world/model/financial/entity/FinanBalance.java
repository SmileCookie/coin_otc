package com.world.model.financial.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.world.data.mysql.Bean;
import com.world.model.entity.admin.AdminUser;

/**
 * 财务系统
 * 公司账户表格(所有进出资金的账户)
 * @author Administrator
 *
 */

public class FinanBalance extends Bean {
	
	public static SimpleDateFormat sdf0 = new SimpleDateFormat("yyyyMMdd00");
	public static SimpleDateFormat sdf24 = new SimpleDateFormat("yyyyMMdd24");
	
	public FinanBalance() {
		super();
	}
	
	public FinanBalance(FinanAccount account, long dayTag, String memo){
		super();
		this.accountId = account.getId();
		this.fundType = account.getFundType();
		this.amount = account.getAmount();
		this.groupTime = dayTag;
		this.memo = memo;
	}
	
	private static final long serialVersionUID = -8791435182844009949L;
  
	private int id;
    private int accountId;
    private int fundType;//资金类型  1为RMB   2为BTC    3为LTC
    private BigDecimal amount;

    private int createId;//创建者id
    private Timestamp createTime;
    
    private long groupTime;//保存到时间的小时  yyyyMMdd00 - yyyyMMdd24
    private String memo;
    private boolean isDel;
    
    /*start by flym 20170606 添加新字段*/
    /*结算编号*/
    private String finId;
    /*上次结算金额*/
	private BigDecimal perAmount;
	/*收入金额*/
	private BigDecimal inAmount;
	/*支出金额*/
	private BigDecimal exAmount;
	/*发生额*/
	private BigDecimal ocAmount;
	/*当前累积实际提现金额*/
	private BigDecimal curTotalAmount;
	/*上次累积实际提现金额*/
	private BigDecimal perTotalAmount;
	/*实际提现金额*/
	private BigDecimal realTsAmount;
	/*end*/
    
    private BigDecimal funds;
    public BigDecimal getFunds() {
    	this.funds = amount;
		return funds;
	}
    
    public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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

	public boolean isDel() {
		return isDel;
	}

	public void setDel(boolean isDel) {
		this.isDel = isDel;
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

	public int getCreateId() {
		return createId;
	}

	public void setCreateId(int createId) {
		this.createId = createId;
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

	public BigDecimal getPerAmount() {
		return perAmount;
	}

	public void setPerAmount(BigDecimal perAmount) {
		this.perAmount = perAmount;
	}

	public BigDecimal getInAmount() {
		return inAmount;
	}

	public void setInAmount(BigDecimal inAmount) {
		this.inAmount = inAmount;
	}

	public BigDecimal getExAmount() {
		return exAmount;
	}

	public void setExAmount(BigDecimal exAmount) {
		this.exAmount = exAmount;
	}

	public BigDecimal getOcAmount() {
		return ocAmount;
	}

	public void setOcAmount(BigDecimal ocAmount) {
		this.ocAmount = ocAmount;
	}

	public BigDecimal getCurTotalAmount() {
		return curTotalAmount;
	}

	public void setCurTotalAmount(BigDecimal curTotalAmount) {
		this.curTotalAmount = curTotalAmount;
	}

	public BigDecimal getPerTotalAmount() {
		return perTotalAmount;
	}

	public void setPerTotalAmount(BigDecimal perTotalAmount) {
		this.perTotalAmount = perTotalAmount;
	}

	public BigDecimal getRealTsAmount() {
		return realTsAmount;
	}

	public void setRealTsAmount(BigDecimal realTsAmount) {
		this.realTsAmount = realTsAmount;
	}

	public String getFinId() {
		return finId;
	}

	public void setFinId(String finId) {
		this.finId = finId;
	}
 }   

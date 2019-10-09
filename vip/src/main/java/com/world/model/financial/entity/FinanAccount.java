package com.world.model.financial.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.world.data.mysql.Bean;
import com.world.model.entity.admin.AdminUser;

/**
 * 财务系统
 * 公司账户表格(所有进出资金的账户)
 * @author Administrator
 *
 */

public class FinanAccount extends Bean {
	public FinanAccount() {
		super();
	}
	
	public FinanAccount(String name, String memo, int fundType, BigDecimal funds, int adminId, int bankAccountId, BigDecimal rate, int type, String img){
		super();
		this.name = name;
		this.memo = memo;
		this.fundType = fundType;
		this.amount = funds;
		this.adminId = adminId;
		this.bankAccountId = bankAccountId;
		this.rate = rate;
		this.type = type;
		this.img = img;
	}
	
	private static final long serialVersionUID = -8791435182844009949L;
	
	private int id;
    private String name;
    private String memo;
    private int fundType;//资金类型  1为RMB   2为BTC    3为LTC        4为狗币
    private int type;//账户类型    1为充值       2为储备       3为提现    4为日常开支
    private BigDecimal amount;
    private int adminId;//所属管理员
    
    private int bankAccountId;//如果是充值账户，保存关联的充值账户id（针对资金类型），如果是自动打币，关联打币的钱包（针对币类型）
    private BigDecimal rate;//费率
    private long dayTag;//标记上下班   如果当前时间   
    
    private int createId;//创建者id
    private Timestamp createTime;
    private int updateId;//更新者id
    private Timestamp updateTime;
    
    private boolean isDel;
    private boolean isDefault;
    private BigDecimal exceptAmount;
    private String img;//标记人民币银行的图标
    
    public BigDecimal funds;
    
    /*start by flym 20170606 添加新字段*/
    private BigDecimal curTotalAmount;
    /*end*/
    
    public BigDecimal getFunds() {
    	this.funds = amount;
		return funds;
	}

	public void setFunds(BigDecimal funds) {
		this.funds = funds;
	}

	public BigDecimal getTotal(){
    	return amount.add(exceptAmount);
    }
    
    private AdminUser aUser;

	public AdminUser getaUser() {
		return aUser;
	}

	public void setaUser(AdminUser aUser) {
		this.aUser = aUser;
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public int getUpdateId() {
		return updateId;
	}

	public void setUpdateId(int updateId) {
		this.updateId = updateId;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public int getBankAccountId() {
		return bankAccountId;
	}

	public void setBankAccountId(int bankAccountId) {
		this.bankAccountId = bankAccountId;
	}

	public boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public long getDayTag() {
		return dayTag;
	}

	public void setDayTag(long dayTag) {
		this.dayTag = dayTag;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getExceptAmount() {
		return exceptAmount;
	}

	public void setExceptAmount(BigDecimal exceptAmount) {
		this.exceptAmount = exceptAmount;
	}

	public BigDecimal getCurTotalAmount() {
		return curTotalAmount;
	}
	
	public void setCurTotalAmount(BigDecimal curTotalAmount) {
		this.curTotalAmount = curTotalAmount;
	}
 }   

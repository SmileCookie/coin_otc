package com.world.model.financial.entity;

import java.sql.Timestamp;

import com.world.data.mysql.Bean;
import com.world.model.entity.admin.AdminUser;

/**
 * 财务系统
 * 资金收入与支出类型(记录资金的用途，收入或是支出)
 * @author Administrator
 *
 */

public class FinanUseType extends Bean {
	public FinanUseType() {
		super();
	}
	
	public FinanUseType(String name, int isIn, String memo, int turnRound, int fundType){
		super();
		this.name = name;
		this.isIn = isIn;
		this.memo = memo;
		this.turnRound = turnRound;
		this.fundType = fundType;
	}

	private static final long serialVersionUID = -8791435182844009949L;
  
	private int id;//自增id
    private String name;//名称
    private int isIn;//收入支出类型（1收入   2支出）
    private String memo; //备注     
    private int turnRound;//是否是内部流转(如果是流转要加载出流转账户    默认为0不流转, 1为流转用途) 
    private int fundType;
    private int type;//系统读取充值还是提现还是其他
    
    private int createId;//管理员id
    private Timestamp createTime;//创建时间
    
    private int updateId;//更新者id
    private Timestamp updateTime;
    
    private boolean isDel;//是否已删除
    
    public int getIsIn() {
		return isIn;
	}

	public void setIsIn(int isIn) {
		this.isIn = isIn;
	}

	private AdminUser aUser;

	public AdminUser getaUser() {
		return aUser;
	}

	public void setaUser(AdminUser aUser) {
		this.aUser = aUser;
	}

	public int getCreateId() {
		return createId;
	}

	public void setCreateId(int createId) {
		this.createId = createId;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTurnRound() {
		return turnRound;
	}

	public void setTurnRound(int turnRound) {
		this.turnRound = turnRound;
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

	public int getFundType() {
		return fundType;
	}

	public void setFundType(int fundType) {
		this.fundType = fundType;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

 }   

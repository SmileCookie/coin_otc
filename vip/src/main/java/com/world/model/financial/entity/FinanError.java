package com.world.model.financial.entity;

import java.sql.Timestamp;

import com.world.data.mysql.Bean;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.user.User;

/**
 * 财务系统
 * 公司账户表格(所有进出资金的账户)
 * @author Administrator
 *
 */

public class FinanError extends Bean {
	public FinanError() {
		super();
	}
	
	/**
	 * @param accountId
	 * @param useTypeId
	 * @param fundType
	 * @param funds
	 * @param fundsComm
	 * @param memo
	 * @param ip
	 */
	public FinanError(int accountId, int useTypeId, int fundType, double funds, double fundsComm,
			String memo, String ip){
		super();
		this.accountId = accountId;
		this.useTypeId = useTypeId;
		this.fundType = fundType;
		if(fundType == 1){
			this.money = funds;
			this.commission = fundsComm;
		}
		this.memo = memo;
		this.ip = ip;
	}
	
	private static final long serialVersionUID = -8791435182844009949L;
	
	private int id;
	private int accountId;//账户id
	private int useTypeId;//用途id
	
	private int fundType;//资金类型  1为RMB   2为BTC    3为LTC
	
	private double money;
	private double commission;//收入手续费
	
	private int userId;
	private String userName;
	
    private String memo;      
    private String ip;
    private int createId;
    private Timestamp createTime;
    private int updateId;
    private Timestamp updateTime;
    private boolean isDel;
    
    private long connectionId;
    private int status;//状态     0未处理    ， 1已处理(处理的时间更新 conectionId字段)
    
    private FinanAccount account;
    private FinanUseType useType;

    private User user;
    private AdminUser aUser;
    
	public AdminUser getaUser() {
		return aUser;
	}

	public void setaUser(AdminUser aUser) {
		this.aUser = aUser;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
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

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public FinanAccount getAccount() {
		return account;
	}

	public void setAccount(FinanAccount account) {
		this.account = account;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getFundType() {
		return fundType;
	}

	public void setFundType(int fundType) {
		this.fundType = fundType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getUseTypeId() {
		return useTypeId;
	}

	public void setUseTypeId(int useTypeId) {
		this.useTypeId = useTypeId;
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

	public FinanUseType getUseType() {
		return useType;
	}

	public void setUseType(FinanUseType useType) {
		this.useType = useType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(long connectionId) {
		this.connectionId = connectionId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

 }   

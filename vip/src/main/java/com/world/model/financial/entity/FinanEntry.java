package com.world.model.financial.entity;

import java.math.BigDecimal;
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

public class FinanEntry extends Bean {
	public FinanEntry() {
		super();
	}
	
	/**
	 * @param accountId
	 * @param useTypeId
	 * @param fundType
	 * @param funds
	 * @param fundsComm
	 * @param outFundsComm
	 * @param userId
	 * @param userName
	 * @param memo
	 * @param ip
	 * @param toAccountId     大于0时 fromAccountId=0
	 * @param fromAccountId   大于0时 toAccountId=0
	 */
	public FinanEntry(int accountId, int useTypeId, int fundType, BigDecimal funds, BigDecimal fundsComm,
			int userId, String userName, String memo, String ip, int toAccountId, int fromAccountId, long connId){
		super();
		this.accountId = accountId;
		this.useTypeId = useTypeId;
		this.fundType = fundType;
		this.funds = funds;
		this.fundsComm = fundsComm;
		this.userId = userId;
		this.userName = userName;
		this.memo = memo;
		this.ip = ip;
		this.toAccountId = toAccountId;
		this.fromAccountId = fromAccountId;
		this.connectionId = connId;
	}

	private static final long serialVersionUID = -8791435182844009949L;
	
	private int id;
  
	private int accountId;//账户id
	private int useTypeId;//用途id
	
	private int fundType;//资金类型  1为RMB   2为BTC    3为LTC
	
	private BigDecimal funds;
    private BigDecimal fundsComm;//人民币和比特币的公用变量（不做数据存储）
	private BigDecimal currentPrice;//当前价格

	private int userId;
	private String userName;
	
    private String memo;      
    private String ip;
    private int createId;
    private Timestamp createTime;
    private int updateId;
    private Timestamp updateTime;
    private boolean isDel;
    
    private int toAccountId;//周转到的账户
    private int fromAccountId;//周转出的账户（给toAcountId插入一条新记录）
    
    private BigDecimal balance;
    
    private long connectionId;
    
    private FinanAccount account;
    private FinanUseType useType;

    private User user;
    private AdminUser aUser;
    
    private String isIn;
    
    public void setIsIn(String isIn) {
		this.isIn = isIn;
	}

	public String getIsIn(){
    	if(fromAccountId > 0){
    		return "收入";
    	}else if(toAccountId > 0){
    		return "支出";
    	}
    	if(getFunds().compareTo(BigDecimal.ZERO) < 0){
    		return "支出";
    	}
    	
    	if(useType != null){
    		return useType.getIsIn() == 1 ? "收入" : "支出";
    	}
    	return "-";
    }
    
    public BigDecimal getBalance() {
		return balance;
	}

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

	public BigDecimal getFunds() {
		return funds;
	}

	public void setFunds(BigDecimal funds) {
		this.funds = funds;
	}

	public BigDecimal getFundsComm() {
		return fundsComm;
	}

	public void setFundsComm(BigDecimal fundsComm) {
		this.fundsComm = fundsComm;
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

	public int getToAccountId() {
		return toAccountId;
	}

	public void setToAccountId(int toAccountId) {
		this.toAccountId = toAccountId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFromAccountId() {
		return fromAccountId;
	}

	public void setFromAccountId(int fromAccountId) {
		this.fromAccountId = fromAccountId;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public long getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(long connectionId) {
		this.connectionId = connectionId;
	}

	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}

	public String accountName;
	public String useTypeName;

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getUseTypeName() {
		return useTypeName;
	}

	public void setUseTypeName(String useTypeName) {
		this.useTypeName = useTypeName;
	}


 }   

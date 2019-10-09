package com.world.model.entity.pay;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.model.entity.coin.CoinProps;

import java.math.BigDecimal;

/**
 * 用户所有的资金信息，包括借贷资金，以fundsType区分币种
 * @author Jackie Xu
 *
 */
public class PayUserBean extends Bean{
	public PayUserBean(){}
	public PayUserBean(String userId) {
		super();
		this.userId = userId;
	}
	
	private String databaseKey = "btc";//数据库
	public void setDatabaseKey(String databaseKey){
		this.databaseKey = databaseKey;
	} 
	public String getDatabaseKey() {
		return databaseKey;
	}
	
	private CoinProps coint;//当前的资金类型

	public CoinProps getCoint() {
		return coint;
	}
	public void setCoint(CoinProps coint) {
		this.coint = coint;
	}

	private static final long serialVersionUID = -7262326202758184600L;
	private String userId;
    private String userName;      
          
    private BigDecimal balance;      
    private BigDecimal freez;
    //活动冻结金额
    private BigDecimal eventFreez;

    private int fundsType;//资金类型
    private int busiId;//业务类型
    
    private BigDecimal dayCash;//日提现额度
    private BigDecimal timesCash;//次提现额度
    private BigDecimal dayFreeCash;//日免审额度
    private BigDecimal minCash;//最小提现额度
    
    //借贷相关字段
    private BigDecimal outWait = BigDecimal.ZERO;//等待借出的金额
	private BigDecimal inWait = BigDecimal.ZERO;//等待借入的金额
	private BigDecimal outSuccess = BigDecimal.ZERO;//借出成功的金额
	private BigDecimal inSuccess = BigDecimal.ZERO;//借入成功的金额
	private BigDecimal overdraft = BigDecimal.ZERO;//拖欠的金额
	
	private BigDecimal entrustThreshold = BigDecimal.ZERO;//自动委托临界值金额
	private BigDecimal loanLimit= BigDecimal.ZERO;//用户放贷范围
	
	private BigDecimal sqlLimit=BigDecimal.ZERO;//拼接类型放贷范围
	private BigDecimal beOutSql=BigDecimal.ZERO;//拼接类型待借出
	private BigDecimal outIngSql=BigDecimal.ZERO;//拼接类型借出中
	
//	private BigDecimal overdue = BigDecimal.ZERO;//滞纳金
	private BigDecimal interestOfDay = BigDecimal.ZERO;//日利息
	private BigDecimal withdrawFreeze = BigDecimal.ZERO;//提现冻结中的资产
	
	private BigDecimal canLoan = BigDecimal.ZERO;//动态变化字段，可借入的数量
	private BigDecimal netAssets = BigDecimal.ZERO;//动态变化字段，折合成当前币种的资产
	
	
//	private int status;//0   不可用   1 可用
//	private int loanOutStatus; //放贷状态,0为禁用, 1为启用
//	private int switchs;	 //自动放贷状态,0为禁用, 1为启用
//	private int freeSwitchs;	//自动放贷  前台免息开关, 0为禁用， 1为启用（ 只有内部人员才有资格用）
//	private int freeMasterSwitch;//自动放贷 后台免息总开关, 0为禁用， 1为启用（ 只有内部人员才有资格用）
//	private int userLend;		//用户放贷， 0为禁用， 1为启用（ 只有管理员控制才有资格用）
//	private int loanInStatus; //是否允许被借入,0为禁止, 1为允许
//	
//	private boolean repayLock;//是否有还款锁
//	private int repayLevel;//还款级别  不同的还款级别系统强制还款的检测频率不同级别越高  刷新频率越高   默认0   有借款时会更新为1
//	private int level;//表示用户当前的借贷等级，也表示用户的杠杆倍数  1倍  2倍  3倍    *****有借入时禁止调节杠杆值*****
//	
//	private Timestamp lastTime;//最后一次检查资金的时间
//	private Timestamp freezTime;//冻结时间不等于空，代表系统已强制还款，24小内不能免息借入
//	private int sysForce = 1;//是否允许系统平仓   1 允许 0不允许
//	
//	private int isSetFees;
//	private BigDecimal fees;//用户放贷手续费比例
	
	private BigDecimal unwindPrice = BigDecimal.ZERO;//预计平仓价格
	/*private int vipRate;			//用户等级
    
    public int getVipRate() {
		return vipRate;
	}
	public void setVipRate(int vipRate) {
		this.vipRate = vipRate;
	}*/
	
    private int firstChargedFlag;	//是否为有充过值: 0无, 1有
	
	public CoinProps getFt() {
		return DatabasesUtil.coinProps(fundsType);
	}
	public BigDecimal getTotal(){
		total = balance.add(freez);
		return total;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public BigDecimal getFreez() {
		return freez;
	}
	public void setFreez(BigDecimal freez) {
		this.freez = freez;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	private BigDecimal total;
    private String safeMobile;
    private String email;
    private String realName;
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public String getSafeMobile() {
		return safeMobile;
	}
	public void setSafeMobile(String safeMobile) {
		this.safeMobile = safeMobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public int getFundsType() {
		return fundsType;
	}
	public void setFundsType(int fundsType) {
		this.fundsType = fundsType;
	}
	public int getBusiId() {
		return busiId;
	}
	public void setBusiId(int busiId) {
		this.busiId = busiId;
	}
	public BigDecimal getDayCash() {
		if(dayCash == null || dayCash.compareTo(BigDecimal.ZERO) <= 0){
			dayCash = coint.getDayCash();
		}
		return dayCash;
	}
	public void setDayCash(BigDecimal dayCash) {
		this.dayCash = dayCash;
	}
	public BigDecimal getTimesCash() {
		if(timesCash == null || timesCash.compareTo(BigDecimal.ZERO) <= 0){
			timesCash = coint.getTimesCash();
		}
		return timesCash;
	}
	public void setTimesCash(BigDecimal timesCash) {
		this.timesCash = timesCash;
	}
	public BigDecimal getDayFreeCash() {
		if(dayFreeCash == null || dayFreeCash.compareTo(BigDecimal.ZERO) <= 0){
			dayFreeCash = coint.getDayFreetrial();
		}
		return dayFreeCash;
	}
	public void setDayFreeCash(BigDecimal dayFreeCash) {
		this.dayFreeCash = dayFreeCash;
	}
	public BigDecimal getMinCash() {
		if(minCash == null || minCash.compareTo(BigDecimal.ZERO) <= 0){
			minCash = coint.getMinCash();
		}
		return minCash;
	}
	public void setMinCash(BigDecimal minCash) {
		this.minCash = minCash;
	}
	public BigDecimal getOutWait() {
		return outWait;
	}
	public void setOutWait(BigDecimal outWait) {
		this.outWait = outWait;
	}
	public BigDecimal getInWait() {
		return inWait;
	}
	public void setInWait(BigDecimal inWait) {
		this.inWait = inWait;
	}
	public BigDecimal getOutSuccess() {
		return outSuccess;
	}
	public void setOutSuccess(BigDecimal outSuccess) {
		this.outSuccess = outSuccess;
	}
	public BigDecimal getInSuccess() {
		return inSuccess;
	}
	public void setInSuccess(BigDecimal inSuccess) {
		this.inSuccess = inSuccess;
	}
	public BigDecimal getOverdraft() {
		return overdraft;
	}
	public void setOverdraft(BigDecimal overdraft) {
		this.overdraft = overdraft;
	}
	public BigDecimal getEntrustThreshold() {
		return entrustThreshold;
	}
	public void setEntrustThreshold(BigDecimal entrustThreshold) {
		this.entrustThreshold = entrustThreshold;
	}
	public BigDecimal getLoanLimit() {
		return loanLimit;
	}
	public void setLoanLimit(BigDecimal loanLimit) {
		this.loanLimit = loanLimit;
	}
	public BigDecimal getSqlLimit() {
		return sqlLimit;
	}
	public void setSqlLimit(BigDecimal sqlLimit) {
		this.sqlLimit = sqlLimit;
	}
	public BigDecimal getBeOutSql() {
		return beOutSql;
	}
	public void setBeOutSql(BigDecimal beOutSql) {
		this.beOutSql = beOutSql;
	}
	public BigDecimal getOutIngSql() {
		return outIngSql;
	}
	public void setOutIngSql(BigDecimal outIngSql) {
		this.outIngSql = outIngSql;
	}
//	public BigDecimal getOverdue() {
//		return overdue;
//	}
//	public void setOverdue(BigDecimal overdue) {
//		this.overdue = overdue;
//	}
	public BigDecimal getInterestOfDay() {
		return interestOfDay;
	}
	public BigDecimal getWithdrawFreeze() {
		return withdrawFreeze;
	}
	public void setWithdrawFreeze(BigDecimal withdrawFreeze) {
		this.withdrawFreeze = withdrawFreeze;
	}
	public BigDecimal getCanLoan() {
		return canLoan;
	}
	public void setCanLoan(BigDecimal canLoan) {
		this.canLoan = canLoan;
	}
	public BigDecimal getNetAssets() {
		return netAssets;
	}
	public void setNetAssets(BigDecimal netAssets) {
		this.netAssets = netAssets;
	}
	public void setInterestOfDay(BigDecimal interestOfDay) {
		this.interestOfDay = interestOfDay;
	}
	/*public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getLoanOutStatus() {
		return loanOutStatus;
	}
	public void setLoanOutStatus(int loanOutStatus) {
		this.loanOutStatus = loanOutStatus;
	}
	public int getSwitchs() {
		return switchs;
	}
	public void setSwitchs(int switchs) {
		this.switchs = switchs;
	}
	public int getFreeSwitchs() {
		return freeSwitchs;
	}
	public void setFreeSwitchs(int freeSwitchs) {
		this.freeSwitchs = freeSwitchs;
	}
	public int getFreeMasterSwitch() {
		return freeMasterSwitch;
	}
	public void setFreeMasterSwitch(int freeMasterSwitch) {
		this.freeMasterSwitch = freeMasterSwitch;
	}
	public int getUserLend() {
		return userLend;
	}
	public void setUserLend(int userLend) {
		this.userLend = userLend;
	}
	public int getLoanInStatus() {
		return loanInStatus;
	}
	public void setLoanInStatus(int loanInStatus) {
		this.loanInStatus = loanInStatus;
	}
	public boolean isRepayLock() {
		return repayLock;
	}
	public void setRepayLock(boolean repayLock) {
		this.repayLock = repayLock;
	}
	public int getRepayLevel() {
		return repayLevel;
	}
	public void setRepayLevel(int repayLevel) {
		this.repayLevel = repayLevel;
	}
	public Timestamp getLastTime() {
		return lastTime;
	}
	public void setLastTime(Timestamp lastTime) {
		this.lastTime = lastTime;
	}
	public Timestamp getFreezTime() {
		return freezTime;
	}
	public void setFreezTime(Timestamp freezTime) {
		this.freezTime = freezTime;
	}
	public int getSysForce() {
		return sysForce;
	}
	public void setSysForce(int sysForce) {
		this.sysForce = sysForce;
	}
	public int getIsSetFees() {
		return isSetFees;
	}
	public void setIsSetFees(int isSetFees) {
		this.isSetFees = isSetFees;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public BigDecimal getFees() {
		return fees;
	}
	public void setFees(BigDecimal fees) {
		this.fees = fees;
	}*/
	public BigDecimal getUnwindPrice() {
		return unwindPrice;
	}
	public void setUnwindPrice(BigDecimal unwindPrice) {
		this.unwindPrice = unwindPrice;
	}
	public int getFirstChargedFlag() {
		return firstChargedFlag;
	}
	public void setFirstChargedFlag(int firstChargedFlag) {
		this.firstChargedFlag = firstChargedFlag;
	}

    public BigDecimal getEventFreez() {
        return eventFreez;
    }

    public void setEventFreez(BigDecimal eventFreez) {
        this.eventFreez = eventFreez;
    }
}


package com.world.model.loan.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.world.data.mysql.Bean;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.pay.PayUserWalletBean;
import com.world.model.entity.user.LoanLever;

/****
 * 用于记录借贷相关的用户信息
 * @author apple
 *
 */
public class P2pUser extends Bean{
	private static final long serialVersionUID = 4157825161356824240L;
	
	private BigDecimal totalAssets;//总资金折合
	private BigDecimal totalAssetsBtc;//比特币总资金折合

	private BigDecimal totalWalletAssets;
	private BigDecimal totalWalletAssetsBtc;

	private BigDecimal netAssets;//净资产折合
	private BigDecimal netWalletAssets;//钱包净资产折合


	private BigDecimal loanInAssets;//借入资产折合
	private BigDecimal loanOutAssets;//借出资产折合
	private BigDecimal totalAssetsSubOverdraft;//有效资金=总资产-欠息-一日利息(包含冻结)


	private BigDecimal availableSubOverdraft;//有效资金（不包含冻结）
	private BigDecimal availableWalletSubOverdraft;


	private BigDecimal overdraftConvert;//拖欠的金额折合
	private BigDecimal interestOfDayConvert;//一天的借贷利息

	/*start by xwz 20170620*/
	private BigDecimal netAssetsSubWithdrawFreeze;//净资产折合-提现冻结
	private BigDecimal netAssetsWalletSubWithdrawFreeze; //钱包净资产折合-提现冻结
	/**/



	/*** 数据库字段*/
	private String userId;
	private String userName;
	private int status;//0   不可用   1 可用
	private int loanOutStatus; //放贷状态,0为禁用, 1为启用
	private int switchs;	 //自动放贷状态,0为禁用, 1为启用
	private int freeSwitchs;	//自动放贷  前台免息开关, 0为禁用， 1为启用（ 只有内部人员才有资格用）
	private int freeMasterSwitch;//自动放贷 后台免息总开关, 0为禁用， 1为启用（ 只有内部人员才有资格用）
	private int userLend;		//用户放贷， 0为禁用， 1为启用（ 只有管理员控制才有资格用）
	private int loanInStatus; //是否允许被借入,0为禁止, 1为允许
	private boolean repayLock;//是否有还款锁
	private int repayLevel;//还款级别  不同的还款级别系统强制还款的检测频率不同级别越高  刷新频率越高   默认0   有借款时会更新为1
	private int level;//表示用户当前的借贷等级，也表示用户的杠杆倍数  1倍  2倍  3倍    *****有借入时禁止调节杠杆值*****
	private Timestamp lastTime;//最后一次检查资金的时间
	private Timestamp freezTime;//冻结时间不等于空，代表系统已强制还款，24小内不能免息借入
	private int sysForce = 1;//是否允许系统平仓   1 允许 0不允许
	private int isSetFees;
	private BigDecimal fees;//用户放贷手续费比例
	/** END */

	public Map<String, PayUserBean> funds;//资产详情
	public JSONObject prices;//当时的价格



	public Map<String, PayUserWalletBean> walletFunds;//资产详情
	public JSONObject walletPrices;//当时的价格

	public Map<String, PayUserWalletBean> getWalletFunds() {
		return walletFunds;
	}

	public void setWalletFunds(Map<String, PayUserWalletBean> walletFunds) {
		this.walletFunds = walletFunds;
	}

	public JSONObject getWalletPrices() {
		return walletPrices;
	}

	public void setWalletPrices(JSONObject walletPrices) {
		this.walletPrices = walletPrices;
	}

	public boolean hasLoanIn(){
		return loanInAssets.compareTo(BigDecimal.ZERO) > 0;
	}

	public LoanLevel getLever(){
		LoanLevel ll = (LoanLevel) EnumUtils.getEnumByKey(level, LoanLevel.class);
		if(ll == null){
			ll = LoanLevel.lever_1;
		}
		return ll;
	}

	public BigDecimal getNetAssetsWalletSubWithdrawFreeze() {
		return netAssetsWalletSubWithdrawFreeze;
	}

	public void setNetAssetsWalletSubWithdrawFreeze(BigDecimal netAssetsWalletSubWithdrawFreeze) {
		this.netAssetsWalletSubWithdrawFreeze = netAssetsWalletSubWithdrawFreeze;
	}

	public BigDecimal getNetWalletAssets() {
		return netWalletAssets;
	}

	public void setNetWalletAssets(BigDecimal netWalletAssets) {
		this.netWalletAssets = netWalletAssets;
	}

	public BigDecimal getAvailableWalletSubOverdraft() {
		return availableWalletSubOverdraft;
	}

	public void setAvailableWalletSubOverdraft(BigDecimal availableWalletSubOverdraft) {
		this.availableWalletSubOverdraft = availableWalletSubOverdraft;
	}

	public BigDecimal getBalance(String coint){
		if(funds != null){
			PayUserBean pay = funds.get(coint);
			if(pay != null){
				return pay.getBalance();
			}
		}
		return BigDecimal.ZERO;
	}

	public BigDecimal getTotalWalletAssets() {
		return totalWalletAssets;
	}

	public void setTotalWalletAssets(BigDecimal totalWalletAssets) {
		this.totalWalletAssets = totalWalletAssets;
	}

	public BigDecimal getTotalWalletAssetsBtc() {
		return totalWalletAssetsBtc;
	}

	public void setTotalWalletAssetsBtc(BigDecimal totalWalletAssetsBtc) {
		this.totalWalletAssetsBtc = totalWalletAssetsBtc;
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
	public BigDecimal getTotalAssets() {
		return totalAssets;
	}
	public void setTotalAssets(BigDecimal totalAssets) {
		this.totalAssets = totalAssets;
	}
	public BigDecimal getNetAssets() {
		return netAssets;
	}
	public void setNetAssets(BigDecimal netAssets) {
		this.netAssets = netAssets;
	}
	public BigDecimal getLoanInAssets() {
		return loanInAssets;
	}
	public void setLoanInAssets(BigDecimal loanInAssets) {
		this.loanInAssets = loanInAssets;
	}
	public BigDecimal getLoanOutAssets() {
		return loanOutAssets;
	}
	public void setLoanOutAssets(BigDecimal loanOutAssets) {
		this.loanOutAssets = loanOutAssets;
	}
	public BigDecimal getTotalAssetsSubOverdraft() {
		return totalAssetsSubOverdraft;
	}
	public void setTotalAssetsSubOverdraft(BigDecimal totalAssetsSubOverdraft) {
		this.totalAssetsSubOverdraft = totalAssetsSubOverdraft;
	}
	public BigDecimal getAvailableSubOverdraft() {
		return availableSubOverdraft;
	}
	public void setAvailableSubOverdraft(BigDecimal availableSubOverdraft) {
		this.availableSubOverdraft = availableSubOverdraft;
	}

	/*start by xwz 20170620*/
	public BigDecimal getNetAssetsSubWithdrawFreeze() {
		return netAssetsSubWithdrawFreeze;
	}

	public void setNetAssetsSubWithdrawFreeze(BigDecimal netAssetsSubWithdrawFreeze) {
		this.netAssetsSubWithdrawFreeze = netAssetsSubWithdrawFreeze;
	}
	/*end*/

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getRepayLevel() {
		return repayLevel;
	}

	public void setRepayLevel(int repayLevel) {
		this.repayLevel = repayLevel;
	}

	public int getStatus() {
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

	public BigDecimal getFees() {
		return fees;
	}

	public void setFees(BigDecimal fees) {
		this.fees = fees;
	}

	public BigDecimal getOverdraftConvert() {
		return overdraftConvert;
	}

	public void setOverdraftConvert(BigDecimal overdraftConvert) {
		this.overdraftConvert = overdraftConvert;
	}

	public Map<String, PayUserBean> getFunds() {
		return funds;
	}

	public void setFunds(Map<String, PayUserBean> funds) {
		this.funds = funds;
	}


	public String getRepayLevelShow(){
		if(repayLevel <= 40){
			return "低";
		}else if(repayLevel > 40 && repayLevel <= 70){
			return "中";
		}else if(repayLevel >= 100){
			return "已平仓";
		}else{
			return "高";
		}
	}

	public LoanLever getLoanLever(){
		LoanLever lever = (LoanLever)EnumUtils.getEnumByKey(level, LoanLever.class);
		return lever;
	}

	public JSONObject getPrices() {
		return prices;
	}

	public void setPrices(JSONObject prices) {
		this.prices = prices;
	}

	public BigDecimal getInterestOfDayConvert() {
		return interestOfDayConvert;
	}

	public void setInterestOfDayConvert(BigDecimal interestOfDayConvert) {
		this.interestOfDayConvert = interestOfDayConvert;
	}

	public BigDecimal getTotalAssetsBtc() {
		return totalAssetsBtc;
	}

	public void setTotalAssetsBtc(BigDecimal totalAssetsBtc) {
		this.totalAssetsBtc = totalAssetsBtc;
	}
}

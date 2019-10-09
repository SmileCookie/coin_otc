package com.world.model.entity.financialproift;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户认证支付信息
 */
public class UserFinancialInfo extends Bean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	/* 用户ID */
	private int userId;
	/* 用户名 */
	private String userName;
	/* 用户类型 */
	private String userType;
	/* 用户VID */
	private String userVID;
	/* 我的邀请码 */
	private String invitationCode;
	/* 推进人邀请码 */
	private String pInvitationCode;
	/* 邀请总人数 */
	private int invitationTotalNum;
	/* 0默认值，1已认证，2已支付 */
	private int authPayFlag;
	/*更新时间戳*/
	private long modifyTime;
	/*投资时间*/
	private long profitTime;
	private Date dateProfitTime;
	/*复投时间*/
	private Date resetProfitTime;
	/*预期收益USDT*/
	private BigDecimal expectProfitUsdt;
	/*投资金额USDT*/
	private BigDecimal investUsdtAmount;
	/*邀请人用户名*/
	private String invitationUserName;
	/*投资金额对应的矩阵*/
	private int matrixLevel;
	/*总投资金额*/
	private BigDecimal investAmount;
	/*分红权重*/
	private BigDecimal vipWeight;
	/*折算还剩余多少个VDS即将出局*/
	private BigDecimal outSurplusVDS;
	/*vds对应USDT折算平均价格*/
	private BigDecimal investAvergPrice;
	/*复投次数*/
	private int reinTimes;
	/*vip标志*/
	private int vipFlag;
	/*团队邀请人数*/
	private int teamsNumber;
	/*直接邀请人数*/
	private int directNumber;
	/*切换主账户*/
	private String switchAccount;
	/*保险投资金额多次叠加*/
	private BigDecimal insureAmount;
	/*保险均价多次加权*/
	private BigDecimal insureAvergPrice;
	/*保险投资权重*/
	private BigDecimal insureWeight;
	/*保险投资时间，每次更新*/
	private Date insureTime;
	/*保险预期收益转换为USDT金额，出局金额*/
	private BigDecimal insureExpectProfitUsdt;
	/*0默认，1保险出局*/
	private int insureOutFlag;
	
	public BigDecimal getInsureAmount() {
		return insureAmount;
	}

	public void setInsureAmount(BigDecimal insureAmount) {
		this.insureAmount = insureAmount;
	}

	public BigDecimal getInsureAvergPrice() {
		return insureAvergPrice;
	}

	public void setInsureAvergPrice(BigDecimal insureAvergPrice) {
		this.insureAvergPrice = insureAvergPrice;
	}

	public BigDecimal getInsureWeight() {
		return insureWeight;
	}

	public void setInsureWeight(BigDecimal insureWeight) {
		this.insureWeight = insureWeight;
	}

	public Date getInsureTime() {
		return insureTime;
	}

	public void setInsureTime(Date insureTime) {
		this.insureTime = insureTime;
	}

	public BigDecimal getInsureExpectProfitUsdt() {
		return insureExpectProfitUsdt;
	}

	public void setInsureExpectProfitUsdt(BigDecimal insureExpectProfitUsdt) {
		this.insureExpectProfitUsdt = insureExpectProfitUsdt;
	}

	public int getInsureOutFlag() {
		return insureOutFlag;
	}

	public void setInsureOutFlag(int insureOutFlag) {
		this.insureOutFlag = insureOutFlag;
	}

	public Date getResetProfitTime() {
		return resetProfitTime;
	}

	public void setResetProfitTime(Date resetProfitTime) {
		this.resetProfitTime = resetProfitTime;
	}

	public Date getDateProfitTime() {
		return dateProfitTime;
	}

	public void setDateProfitTime(Date dateProfitTime) {
		this.dateProfitTime = dateProfitTime;
	}

	public String getSwitchAccount() {
		return switchAccount;
	}

	public void setSwitchAccount(String switchAccount) {
		this.switchAccount = switchAccount;
	}

	public int getTeamsNumber() {
		return teamsNumber;
	}

	public void setTeamsNumber(int teamsNumber) {
		this.teamsNumber = teamsNumber;
	}

	public int getDirectNumber() {
		return directNumber;
	}

	public void setDirectNumber(int directNumber) {
		this.directNumber = directNumber;
	}

	public int getVipFlag() {
		return vipFlag;
	}

	public void setVipFlag(int vipFlag) {
		this.vipFlag = vipFlag;
	}

	public int getReinTimes() {
		return reinTimes;
	}

	public void setReinTimes(int reinTimes) {
		this.reinTimes = reinTimes;
	}

	public BigDecimal getInvestAvergPrice() {
		return investAvergPrice;
	}

	public void setInvestAvergPrice(BigDecimal investAvergPrice) {
		this.investAvergPrice = investAvergPrice;
	}

	public BigDecimal getOutSurplusVDS() {
		return outSurplusVDS;
	}

	public void setOutSurplusVDS(BigDecimal outSurplusVDS) {
		this.outSurplusVDS = outSurplusVDS;
	}

	public BigDecimal getVipWeight() {
		return vipWeight;
	}

	public void setVipWeight(BigDecimal vipWeight) {
		this.vipWeight = vipWeight;
	}

	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	public int getMatrixLevel() {
		return matrixLevel;
	}

	public void setMatrixLevel(int matrixLevel) {
		this.matrixLevel = matrixLevel;
	}

	public String getInvitationUserName() {
		return invitationUserName;
	}

	public void setInvitationUserName(String invitationUserName) {
		this.invitationUserName = invitationUserName;
	}

	public long getProfitTime() {
		return profitTime;
	}

	public void setProfitTime(long profitTime) {
		this.profitTime = profitTime;
	}

	public BigDecimal getInvestUsdtAmount() {
		return investUsdtAmount;
	}

	public void setInvestUsdtAmount(BigDecimal investUsdtAmount) {
		this.investUsdtAmount = investUsdtAmount;
	}

	public long getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(long modifyTime) {
		this.modifyTime = modifyTime;
	}

	public BigDecimal getExpectProfitUsdt() {
		return expectProfitUsdt;
	}

	public void setExpectProfitUsdt(BigDecimal expectProfitUsdt) {
		this.expectProfitUsdt = expectProfitUsdt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserVID() {
		return userVID;
	}

	public void setUserVID(String userVID) {
		this.userVID = userVID;
	}

	public String getInvitationCode() {
		return invitationCode;
	}

	public void setInvitationCode(String invitationCode) {
		this.invitationCode = invitationCode;
	}

	public String getpInvitationCode() {
		return pInvitationCode;
	}

	public void setpInvitationCode(String pInvitationCode) {
		this.pInvitationCode = pInvitationCode;
	}

	public int getInvitationTotalNum() {
		return invitationTotalNum;
	}

	public void setInvitationTotalNum(int invitationTotalNum) {
		this.invitationTotalNum = invitationTotalNum;
	}

	public int getAuthPayFlag() {
		return authPayFlag;
	}

	public void setAuthPayFlag(int authPayFlag) {
		this.authPayFlag = authPayFlag;
	}
}

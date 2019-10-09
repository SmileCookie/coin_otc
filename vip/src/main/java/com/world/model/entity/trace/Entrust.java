package com.world.model.entity.trace;

import java.math.BigDecimal;

import com.world.data.mysql.Bean;
import org.apache.commons.lang.StringUtils;

/**
 * 委托详情记录
 * @author pc
 *
 */

public class Entrust  extends Bean{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 2245241843231779897L;
	private long entrustId;//委托ID
	private BigDecimal unitPrice;//委托单价
	private BigDecimal numbers;//委托数量
	private BigDecimal totalMoney;//委托总价
	private BigDecimal completeNumber;//已完成数量
	private BigDecimal completeTotalMoney;//已完成金额
	private int webId;//委托途径 5：APP 6：API 8：网站
	private int types;//0:卖 1：买
    private int userId;//挂单用户ID
    private int status;//挂单成交状态
    private long submitTime ;//挂单委托时间
    private BigDecimal feeRate ;//手续费费率
    private String userName;
	private String userType;//用户类型

	public long getEntrustId() {
		return entrustId;
	}
	public void setEntrustId(long entrustId) {
		this.entrustId = entrustId;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public BigDecimal getNumbers() {
		return numbers;
	}
	public void setNumbers(BigDecimal numbers) {
		this.numbers = numbers;
	}
	public BigDecimal getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}
	public BigDecimal getCompleteNumber() {
		return completeNumber;
	}
	public void setCompleteNumber(BigDecimal completeNumber) {
		this.completeNumber = completeNumber;
	}
	public BigDecimal getCompleteTotalMoney() {
		return completeTotalMoney;
	}
	public void setCompleteTotalMoney(BigDecimal completeTotalMoney) {
		this.completeTotalMoney = completeTotalMoney;
	}
	public int getWebId() {
		return webId;
	}
	public void setWebId(int webId) {
		this.webId = webId;
	}
	public int getTypes() {
		return types;
	}
	public void setTypes(int types) {
		this.types = types;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(long submitTime) {
		this.submitTime = submitTime;
	}
	public BigDecimal getFeeRate() {
		return feeRate;
	}
	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserType() {
		if(StringUtils.isNotBlank(userType)){
			return userType;
		}
		return "-";
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
}

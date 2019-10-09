package com.world.model.entity;

import java.math.BigDecimal;

import com.world.data.mysql.Bean;
import org.apache.commons.lang.StringUtils;

/**
 * 交易详情记录
 * @author pc
 *
 */

public class Guadan  extends Bean{
//	平均价格   数量    用户
    private int userId;
	private String userName;
	private BigDecimal avgPrice;	//平均价格
	private BigDecimal numbers;		// 数量
	private int isBuy;
	private String userType; //账户类型：回购账户，刷量账户，抢盘口账户，打底账户

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
	public BigDecimal getAvgPrice() {
		return avgPrice;
	}
	public void setAvgPrice(BigDecimal avgPrice) {
		this.avgPrice = avgPrice;
	}
	public BigDecimal getNumbers() {
		return numbers;
	}
	public void setNumbers(BigDecimal numbers) {
		this.numbers = numbers;
	}
	public int getIsBuy() {
		return isBuy;
	}
	public void setIsBuy(int isBuy) {
		this.isBuy = isBuy;
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

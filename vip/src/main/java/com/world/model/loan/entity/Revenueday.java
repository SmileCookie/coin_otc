package com.world.model.loan.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.world.data.mysql.Bean;

/***
 * 每币种的利息收益，结合人民币收益
 * 
 * @author Administrator
 *
 */

public class Revenueday extends Bean {

	/**
	 * 给它一个唯一性
	 */
	private static final long serialVersionUID = -7470732595541448328L;

	// 生成构造
	public Revenueday() {
		super();
	}

	private int id;
	private String userId;
	private BigDecimal earnings;// 利息收益
	private BigDecimal converts;// 各币种收益折合价值
	private int fundsType;//资金类型
	private Timestamp earningTime;// 各币种利息收益时间
	private BigDecimal dailyAverage;//日平均值（30day）
	private BigDecimal thisMonthSum;//今月总汇
	private BigDecimal lastMonthSum;//上月总汇
	
	private BigDecimal day7Avg;//7日均值
	private BigDecimal day15Avg;//15日均值
	
	public BigDecimal getDailyAverage() {
		return dailyAverage;
	}

	public void setDailyAverage(BigDecimal dailyAverage) {
		this.dailyAverage = dailyAverage;
	}

	public BigDecimal getThisMonthSum() {
		return thisMonthSum;
	}

	public void setThisMonthSum(BigDecimal thisMonthSum) {
		this.thisMonthSum = thisMonthSum;
	}

	public BigDecimal getLastMonthSum() {
		return lastMonthSum;
	}

	public void setLastMonthSum(BigDecimal lastMonthSum) {
		this.lastMonthSum = lastMonthSum;
	}

	public Revenueday(String userId, int fundsType, BigDecimal earnings, BigDecimal converts, Timestamp earningTime) {
		super();
		this.userId = userId;
		this.fundsType = fundsType;
		this.earnings = earnings;
		this.converts = converts;
		this.earningTime = earningTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Timestamp getEarningTime() {
		return earningTime;
	}

	public void setEarningTime(Timestamp earningTime) {
		this.earningTime = earningTime;
	}

	public BigDecimal getDay7Avg() {
		return day7Avg;
	}

	public void setDay7Avg(BigDecimal day7Avg) {
		this.day7Avg = day7Avg;
	}

	public BigDecimal getEarnings() {
		return earnings;
	}

	public void setEarnings(BigDecimal earnings) {
		this.earnings = earnings;
	}

	public BigDecimal getConverts() {
		return converts;
	}

	public void setConverts(BigDecimal converts) {
		this.converts = converts;
	}

	public int getFundsType() {
		return fundsType;
	}

	public void setFundsType(int fundsType) {
		this.fundsType = fundsType;
	}

	public BigDecimal getDay15Avg() {
		return day15Avg;
	}

	public void setDay15Avg(BigDecimal day15Avg) {
		this.day15Avg = day15Avg;
	}

}

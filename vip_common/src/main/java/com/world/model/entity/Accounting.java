package com.world.model.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.LongIdEntity;

/****
 * 账务类
 * @author apple
 *
 */
@Entity(noClassnameStored = true)
public class Accounting extends LongIdEntity{

	private String web;
	//可用 = 可用余额 + 活期 + 定期
	private double cnyBalance;
	private double btcBalance;
	private double ltcBalance;
	private double ethBalance;
	private Timestamp times;//时间
	
	public double getCnyBalance() {
		return cnyBalance;
	}
	public void setCnyBalance(double cnyBalance) {
		this.cnyBalance = cnyBalance;
	}
	public double getBtcBalance() {
		return btcBalance;
	}
	public void setBtcBalance(double btcBalance) {
		this.btcBalance = btcBalance;
	}
	public double getLtcBalance() {
		return ltcBalance;
	}
	public void setLtcBalance(double ltcBalance) {
		this.ltcBalance = ltcBalance;
	}
	public Timestamp getTimes() {
		return times;
	}
	public void setTimes(Timestamp times) {
		this.times = times;
	}
	public String getWeb() {
		return web;
	}
	public void setWeb(String web) {
		this.web = web;
	}
	public double getEthBalance() {
		return ethBalance;
	}
	public void setEthBalance(double ethBalance) {
		this.ethBalance = ethBalance;
	}
	
}

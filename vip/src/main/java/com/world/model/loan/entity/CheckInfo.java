package com.world.model.loan.entity;

import java.sql.Timestamp;

import com.alibaba.fastjson.JSONObject;
import com.world.data.mysql.Bean;

public class CheckInfo extends Bean {
	
	private static final long serialVersionUID = 1L;
	/**
	 * id
	 */
	private int id;
	/**
	 * 用户ID
	 */
	private String userId;
	/**
	 * 价格
	 */
	private JSONObject prices;
	/**
	 * 余额
	 */
	private JSONObject balance;
	/**
	 * 借入
	 */
	private JSONObject borrowed;
	/**
	 * 平仓等级
	 */
	private int grade;
	/**
	 * 是否能发送短信（1、0），二十四小时内  每隔两个小时发送一次，直到三次
	 */
	private int sendSms;
	/**
	 * 增加时间
	 */
	private Timestamp addTime;
	
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
	public int getGrade() {
		return grade;
	}
	public void setGrade(int grade) {
		this.grade = grade;
	}
	public int getSendSms() {
		return sendSms;
	}
	public void setSendSms(int sendSms) {
		this.sendSms = sendSms;
	}
	public Timestamp getAddTime() {
		return addTime;
	}
	public void setAddTime(Timestamp addTime) {
		this.addTime = addTime;
	}
	public JSONObject getPrices() {
		return prices;
	}
	public void setPrices(JSONObject prices) {
		this.prices = prices;
	}
	public JSONObject getBalance() {
		return balance;
	}
	public void setBalance(JSONObject balance) {
		this.balance = balance;
	}
	public JSONObject getBorrowed() {
		return borrowed;
	}
	public void setBorrowed(JSONObject borrowed) {
		this.borrowed = borrowed;
	}
	
}

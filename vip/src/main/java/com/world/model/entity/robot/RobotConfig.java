package com.world.model.entity.robot;


import java.sql.Timestamp;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

@Entity(noClassnameStored = true, value = "RobotConfig")
public class RobotConfig extends StrBaseLongIdEntity {

	private static final long serialVersionUID = -2386115543279873856L;

	public RobotConfig() {
		super();
	}

	public RobotConfig(Datastore ds) {
		super(ds);
	}
	
	public RobotConfig(Datastore ds,String title,String currency,String account,double  lowPrice,double  highPrice,double  minAmount,
			double  maxAmount,long freq) {
		super(ds);
		this.title = title;
		this.currency = currency;
		this.account = account;
		this.lowPrice = lowPrice;
		this.highPrice = highPrice;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.freq = freq;
		
	}
	
	private String title;//机器人名称
	private String currency;//币种
	private String account;//操作账户
	private double  lowPrice;//委托最低价格
	private double  highPrice;//委托最高价格
	
	private double  minAmount;//委托最小数量
	private double  maxAmount;//委托最大数量
	
	private long freq;//挂单频率 毫秒
	
	private int status ; //启动状态 0：停止 1：运行
	
	private Timestamp createTime;//真正的发布时间

	private String opUserName;//真正的发布时间
	
	private int isRunning;//是否在运行状态
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public double getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(double lowPrice) {
		this.lowPrice = lowPrice;
	}

	public double getHighPrice() {
		return highPrice;
	}

	public void setHightPrice(double highPrice) {
		this.highPrice = highPrice;
	}

	public double getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(double minAmount) {
		this.minAmount = minAmount;
	}

	public double getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(double maxAmount) {
		this.maxAmount = maxAmount;
	}

	public long getFreq() {
		return freq;
	}

	public void setFreq(long freq) {
		this.freq = freq;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}


	public void setHighPrice(double highPrice) {
		this.highPrice = highPrice;
	}

	public String getOpUserName() {
		return opUserName;
	}

	public void setOpUserName(String opUserName) {
		this.opUserName = opUserName;
	}

	public int getIsRunning() {
		return isRunning;
	}

	public void setIsRunning(int isRunning) {
		this.isRunning = isRunning;
	}
	
	
	
}

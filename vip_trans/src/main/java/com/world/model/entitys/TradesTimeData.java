package com.world.model.entitys;

import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.LongIdEntity;

@Entity(value = "tradestimedata",noClassnameStored = true)
public class TradesTimeData extends LongIdEntity {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long times;
	private double high;
	private double low;
	private double middle;
	private double open;
	private double close;
	private double amount;
	
	
	public long getTimes() {
		return times;
	}
	public void setTimes(long times) {
		this.times = times;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getMiddle() {
		return middle;
	}
	public void setMiddle(double middle) {
		this.middle = middle;
	}
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	
}

package com.world.util.request;

import java.io.Serializable;
import java.math.BigDecimal;

public class Ticker implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal high;
	private BigDecimal low;
	private BigDecimal buy;
	private BigDecimal sell;
	private BigDecimal last;
	private BigDecimal vol;
	
	public BigDecimal getHigh() {
		return high;
	}
	public void setHigh(BigDecimal high) {
		this.high = high;
	}
	public BigDecimal getLow() {
		return low;
	}
	public void setLow(BigDecimal low) {
		this.low = low;
	}
	public BigDecimal getBuy() {
		return buy;
	}
	public void setBuy(BigDecimal buy) {
		this.buy = buy;
	}
	public BigDecimal getSell() {
		return sell;
	}
	public void setSell(BigDecimal sell) {
		this.sell = sell;
	}
	public BigDecimal getLast() {
		return last;
	}
	public void setLast(BigDecimal last) {
		this.last = last;
	}
	public BigDecimal getVol() {
		return vol;
	}
	public void setVol(BigDecimal vol) {
		this.vol = vol;
	}
}

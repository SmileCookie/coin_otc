package com.tenstar;

import java.io.Serializable;

public class AdminMoney implements Serializable {
	
	private long entrustId;
	private long transRecordId;
	private int webId;
	private int userId;
	private int pageIndex;
	private int type;
	private long timeFrom;
	private long timeTo;
	private long numberFrom;
	private long numberTo;
	private long priceFrom;
	private long priceTo;
	private long totalFrom;
	private long totalTo;
	private long pageSize;
	private int status;
	private String message;
	
	
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public long getEntrustId() {
		return entrustId;
	}
	public void setEntrustId(long entrustId) {
		this.entrustId = entrustId;
	}
	public long getTransRecordId() {
		return transRecordId;
	}
	public void setTransRecordId(long transRecordId) {
		this.transRecordId = transRecordId;
	}
	public int getWebId() {
		return webId;
	}
	public void setWebId(int webId) {
		this.webId = webId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getTimeFrom() {
		return timeFrom;
	}
	public void setTimeFrom(long timeFrom) {
		this.timeFrom = timeFrom;
	}
	public long getTimeTo() {
		return timeTo;
	}
	public void setTimeTo(long timeTo) {
		this.timeTo = timeTo;
	}
	public long getNumberFrom() {
		return numberFrom;
	}
	public void setNumberFrom(long numberFrom) {
		this.numberFrom = numberFrom;
	}
	public long getNumberTo() {
		return numberTo;
	}
	public void setNumberTo(long numberTo) {
		this.numberTo = numberTo;
	}
	public long getPriceFrom() {
		return priceFrom;
	}
	public void setPriceFrom(long priceFrom) {
		this.priceFrom = priceFrom;
	}
	public long getPriceTo() {
		return priceTo;
	}
	public void setPriceTo(long priceTo) {
		this.priceTo = priceTo;
	}
	public long getPageSize() {
		return pageSize;
	}
	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getTotalFrom() {
		return totalFrom;
	}
	public void setTotalFrom(long totalFrom) {
		this.totalFrom = totalFrom;
	}
	public long getTotalTo() {
		return totalTo;
	}
	public void setTotalTo(long totalTo) {
		this.totalTo = totalTo;
	}
	
	

}

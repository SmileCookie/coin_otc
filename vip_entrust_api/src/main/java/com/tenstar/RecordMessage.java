package com.tenstar;

import java.io.Serializable;

public class RecordMessage implements Serializable{
	private static final long serialVersionUID = 123235555L;  
	private int webId;//站点id
	private int types=-1;//类型   0 卖出  1 买入  -1不限制
	private int userId;//用户id
	private String message="";//消息
	private String auth="";//认证字符串
	private int pageindex=1;//页码
	private long timeFrom=0;//时间起始
	private long timeTo=0;//时间结束
	private long numberFrom=0;//数量其实
	private long  numberTo=0;//数量结束
	private long priceFrom=0;//最低价
	private long priceTo=0;//最高价
	private int pageSize=10;//分页大小最大200
	private int status=0;//订单状态 0不限制 1 已取消成功 2 交易成功 3 交易中（未完全成交）
	private int dateTo = 1;
	private int count;
	private String market="";

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public int getPageindex() {
		return pageindex;
	}
	public void setPageindex(int pageindex) {
		this.pageindex = pageindex;
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
	public int getDateTo() {
		return dateTo;
	}
	public void setDateTo(int dateTo) {
		this.dateTo = dateTo;
	}
	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	
}

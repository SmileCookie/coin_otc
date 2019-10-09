package com.tenstar;

import java.io.Serializable;

public class AdminWeiTuo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 获取委托记录,这里跟前台不一样的地方是unitprice=0代表取消的这条命令会显示出来，方便后台管理调试查看
	 * @param entrustId 委托id，指定的委托id，如果为0 不限制
	 * @param webId 网站id 网站id（暂时都设置城8）
	 * @param userId 用户id 用户id   0代表不限制
	 * @param pageIndex 页码从1开始
	 * @param pageSize 页码大小 10
	 * @param type 类型   0 卖出  1 买入  -1不限制 
	 * @param timeFrom //时间   System.currentTimeMillis()
	 * @param timeTo
	 * @param numberFrom//数量查询，数量等于用户提交的数量*Market.numberBixNormal    提交过来
	 * @param numberTo//数量查询
	 * @param priceFrom 最低价格
	 * @param priceTo 最高价格
	 * @param pagesize 页码大小 最大200
	 * @param status 订单状态 0不限制 1 已取消成功 2 交易成功 3 交易中（未完全成交） -1计划委托
	 * @return 返回的是json数据，格式为 count：总数量  record数组代表结果集合entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status
	 */
	private int webId;//网站Id
	private int userId;
	private int pageIndex;
	private int type;
	private long timeFrom;
	private long timeTo;
	private long numberFrom;
	private long numberTo;
	private long priceFrom;
	private long priceTo;
	private long pageSize;
	private int status;
	private String message;
	private String market;
	

	public String getMarket() {
		return market;
	}


	public void setMarket(String market) {
		this.market = market;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	private long entrustId;public long getEntrustId() {
		return entrustId;
	}


	public void setEntrustId(long entrustId) {
		this.entrustId = entrustId;
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



}

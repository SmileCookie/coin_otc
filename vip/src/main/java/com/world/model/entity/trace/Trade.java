package com.world.model.entity.trace;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.model.entity.coin.CoinProps;
/****
 * 网址分类
 * 
 * @author qin
 * 
 */
public class Trade extends Bean {
	
	private static final long serialVersionUID = 1L;
	public Trade() {
		super();
	}
	
	public Trade(String name,String url){
		this.name=name;
		this.url=url;
	}
	private int id;//ID
	private String name;//网址主域名
	private String descs;//网站名称
	private String url;//URL
	private String tickerUrl;//交易行情地址
	private String remark;//备注
	private BigDecimal lastPrice;//最新的价格
	private int isDeleted;//是否删除
	private Timestamp addTime;//添加时间
	private String symbol;//网站标识
	private int fundsType;
	private Timestamp lastTime;//最后刷新价格的时间
	
	public CoinProps getFt(){
		return DatabasesUtil.coinProps(fundsType);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTickerUrl() {
		return tickerUrl;
	}

	public void setTickerUrl(String tickerUrl) {
		this.tickerUrl = tickerUrl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Timestamp getAddTime() {
		return addTime;
	}

	public void setAddTime(Timestamp addTime) {
		this.addTime = addTime;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public int getFundsType() {
		return fundsType;
	}

	public void setFundsType(int fundsType) {
		this.fundsType = fundsType;
	}

	public Timestamp getLastTime() {
		return lastTime;
	}

	public void setLastTime(Timestamp lastTime) {
		this.lastTime = lastTime;
	}
	
}

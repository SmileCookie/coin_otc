package com.world.model.entity.record;

import java.math.BigDecimal;

import com.world.data.big.table.TableInfo;
import com.world.data.big.table.UpdateWay;
import com.world.data.mysql.Bean;
import com.world.util.date.TimeUtil;
import org.apache.commons.lang.StringUtils;


public class TransRecord extends Bean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8970561569458517286L;
	private long transRecordId;
	private BigDecimal unitPrice;
	private BigDecimal totalPrice;
	private BigDecimal numbers;
	private long entrustIdBuy;
	private int userIdBuy;
	private long entrustIdSell;
	private int userIdSell;
	private long types;
	private long times;
	private long timeMinute;
	private int status;
	private int isCount;
	private int webIdBuy;
	private int webIdSell;
	private int dealTimes;
	private int actStatus;//活动处理状态  0 不需要处理  1.待处理  2 已处理为奖励  3 已奖励

	private String timesView;
	private String statusView;
	private String typesView;

	private String userBuyType;//用户类型买
	private String userSellType;//用户类型卖

	public String getStatusView() {
		String StatusView = "-";
		if(status==0)
			StatusView="-";
		else if(status==1)
			StatusView = "<font style='color:orange;'>处理失败</font>";
		else if(status==2)
			StatusView = "<font style='color:green;'>处理成功</font>";
		return StatusView;
	}

	public String getTypesView() {
		String typesView = "<font style='color:#D75A46;'>买入</font>";
		if(this.types == -1){
			typesView = "<font style='color:#D75A4;'>取消</font>";
		}else if(this.types == 0){
			typesView = "<font style='color:#4775A9;'>卖出</font>";
		}
		if(this.entrustIdBuy == 0){
			typesView = "<font style='color:#4775A9;'>取消</font>";
		}
		if(this.entrustIdSell == 0){
			typesView = "<font style='color:#D75A46;'>取消</font>";
		}
		return typesView;
	}

	public String getTimesView() {
		return TimeUtil.parseDate(this.times);
	}


	public long getTransRecordId() {
		return transRecordId;
	}
	public void setTransRecordId(long transRecordId) {
		this.transRecordId = transRecordId;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public BigDecimal getNumbers() {
		return numbers;
	}
	public void setNumbers(BigDecimal numbers) {
		this.numbers = numbers;
	}
	public long getEntrustIdBuy() {
		return entrustIdBuy;
	}
	public void setEntrustIdBuy(long entrustIdBuy) {
		this.entrustIdBuy = entrustIdBuy;
	}
	public int getUserIdBuy() {
		return userIdBuy;
	}
	public void setUserIdBuy(int userIdBuy) {
		this.userIdBuy = userIdBuy;
	}
	public long getEntrustIdSell() {
		return entrustIdSell;
	}
	public void setEntrustIdSell(long entrustIdSell) {
		this.entrustIdSell = entrustIdSell;
	}
	public int getUserIdSell() {
		return userIdSell;
	}
	public void setUserIdSell(int userIdSell) {
		this.userIdSell = userIdSell;
	}
	public long getTypes() {
		return types;
	}
	public void setTypes(long types) {
		this.types = types;
	}
	public long getTimes() {
		return times;
	}
	public void setTimes(long times) {
		this.times = times;
	}
	public long getTimeMinute() {
		return timeMinute;
	}
	public void setTimeMinute(long timeMinute) {
		this.timeMinute = timeMinute;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getIsCount() {
		return isCount;
	}
	public void setIsCount(int isCount) {
		this.isCount = isCount;
	}
	public int getWebIdBuy() {
		return webIdBuy;
	}
	public void setWebIdBuy(int webIdBuy) {
		this.webIdBuy = webIdBuy;
	}
	public int getWebIdSell() {
		return webIdSell;
	}
	public void setWebIdSell(int webIdSell) {
		this.webIdSell = webIdSell;
	}
	public int getDealTimes() {
		return dealTimes;
	}
	public void setDealTimes(int dealTimes) {
		this.dealTimes = dealTimes;
	}
	public int getActStatus() {
		return actStatus;
	}
	public void setActStatus(int actStatus) {
		this.actStatus = actStatus;
	}

	public String getUserBuyType() {
		if(StringUtils.isNotBlank(userBuyType)){
			return userBuyType;
		}
		return "-";
	}

	public void setUserBuyType(String userBuyType) {
		this.userBuyType = userBuyType;
	}

	public String getUserSellType() {
		if(StringUtils.isNotBlank(userSellType)){
			return userSellType;
		}
		return "-";
	}

	public void setUserSellType(String userSellType) {
		this.userSellType = userSellType;
	}

}

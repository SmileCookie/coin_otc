package com.world.model.loan.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.world.data.mysql.Bean;
import com.world.model.loan.worker.LoanAutoFactory;
import com.world.util.date.TimeUtil;

/**
 * 抵扣券表
 *
 */
public class DeductCoupon extends Bean {
	
	private static final long serialVersionUID = 1L;

	public DeductCoupon(){
		super();
	}
	private int id;
	private String userId;// 使用用户id
	private String userName;// 使用用户名
	private Timestamp startTime;// 创建抵扣券时间
	private String title;// 标题
	private int getWay;// 抵扣券获取途径（1活动发放、2系统赠送）
	private int couponType;// 抵扣券类型（1抵扣券、2打折券、3限额抵扣券、4限额打折券）
	private String secretkey;// 抵扣券秘钥
	private int fundsType;// 抵扣币种
	private BigDecimal amountDeg;// 抵扣券额度
	private String useCondition;// 使用条件（描述信息）
	private int useState;// 使用状态（0未激活、1未使用、2已使用、3已过期、4禁止使用、5抵扣中）
	private Timestamp actTime;// 激活时间
	private Timestamp useTime;// 使用时间
	private Timestamp endTime;// 抵扣券过期时间
	private String batchMark;// 本批标识
	
	
	private BigDecimal hasLiXiAmou;// 抵扣利息
	private BigDecimal converAmou;// RMB根据线上价格折算币种抵扣价
	

	public DeductCoupon(String userId, String userName, Timestamp startTime, String title, int getWay, int couponType, String secretkey, int fundsType,
			BigDecimal amountDeg, String useCondition, int useState,Timestamp actTime, Timestamp useTime, Timestamp endTime, String batchMark) {
		super();
		this.userId = userId;
		this.userName=userName;
		this.startTime = startTime;
		this.couponType = couponType;
		this.secretkey = secretkey;
		this.fundsType = fundsType;
		this.amountDeg = amountDeg;
		this.useCondition = useCondition;
		this.endTime = endTime;
		this.useState = useState;
		this.actTime = actTime;
		this.useTime = useTime;
		this.title = title;
		this.getWay = getWay;
		this.batchMark = batchMark;
	}
	//输出页面的激活日期时间（日期时间）
		public String getActFormatTime(){
			if(actTime == null)
				return "";
			else
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(actTime.getTime()));
		}
	
	//输出页面的使用日期时间（日期时间）
	public String getUseFormatTime(){
		if(useTime == null)
			return "";
		else
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(useTime.getTime()));
	}
	
	//输出页面的过期日期（日期）
	public String getEndFormatTime(){
		if(endTime == null)
			return "";
		else
			return new SimpleDateFormat("yyyy-MM-dd").format(new Date(endTime.getTime()));
	}
	
	public BigDecimal getHasLiXiAmou() {
		return hasLiXiAmou == null ? BigDecimal.ZERO : hasLiXiAmou;
	}

	public void setHasLiXiAmou(BigDecimal hasLiXiAmou) {
		this.hasLiXiAmou = hasLiXiAmou;
	}

	public BigDecimal getConverAmou() {
		return converAmou;
	}
	//折算线上价
	//不要随意改动小数位，会造成数据准确性和逻辑的运行。
	public void setConverAmou(String coint) {
		if(amountDeg == null)
			amountDeg = BigDecimal.ZERO;
		BigDecimal couldUse = amountDeg;
		try {
			if(couldUse.doubleValue() > 0){
				JSONObject prices = LoanAutoFactory.getPrices();
				if(prices.containsKey(coint)){
					BigDecimal price = prices.getBigDecimal(coint);
					this.converAmou = couldUse.divide(price, 8, RoundingMode.DOWN);
				}else{
					this.converAmou = couldUse;
				}
			}else{
				this.converAmou = BigDecimal.ZERO;	
			}
		} catch (ArithmeticException e) {
			this.converAmou = BigDecimal.ZERO;	
		}
	}
	
	/**
	 * 是否过期
	 * @return
	 */
	public boolean isExp(){
		
		if (this.useState<=1 && TimeUtil.getNow().compareTo(this.getEndTime())>0) {
			return true;
		}
		return false;
	}
	
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public int getCouponType() {
		return couponType;
	}

	public void setCouponType(int couponType) {
		this.couponType = couponType;
	}

	public String getSecretkey() {
		return secretkey;
	}

	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}

	public int getFundsType() {
		return fundsType;
	}

	public void setFundsType(int fundsType) {
		this.fundsType = fundsType;
	}

	public BigDecimal getAmountDeg() {
		return amountDeg;
	}

	public void setAmountDeg(BigDecimal amountDeg) {
		this.amountDeg = amountDeg;
	}

	public String getUseCondition() {
		return useCondition;
	}

	public void setUseCondition(String useCondition) {
		this.useCondition = useCondition;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public int getUseState() {
		if(isExp()){
			return 3;	//过期
		}
		return useState;
	}

	public void setUseState(int useState) {
		this.useState = useState;
	}

	public Timestamp getUseTime() {
		return useTime;
	}

	public Timestamp getActTime() {
		return actTime;
	}
	public void setActTime(Timestamp actTime) {
		this.actTime = actTime;
	}

	public void setUseTime(Timestamp useTime) {
		this.useTime = useTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getGetWay() {
		return getWay;
	}

	public void setGetWay(int getWay) {
		this.getWay = getWay;
	}

	public String getBatchMark() {
		return batchMark;
	}

	public void setBatchMark(String batchMark) {
		this.batchMark = batchMark;
	}
	
	
}

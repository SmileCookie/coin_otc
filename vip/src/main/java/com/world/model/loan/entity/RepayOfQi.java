package com.world.model.loan.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.bean.BeanField;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.coin.CoinProps;
import com.world.util.DigitalUtil;
import com.world.util.date.TimeUtil;
/******
 * 分期还款  期数对应的还款
 * @author Administrator
 *
 */
public class RepayOfQi extends Bean{
	public RepayOfQi() {
		super();
	}
	public RepayOfQi(int loanRecordId, BigDecimal benJin,BigDecimal liXi, int status, 
			Timestamp actureDate,Timestamp forecastDate , BigDecimal fwf,int fundsType , String userId , String userName , String outUserId) {
		super();
		this.loanRecordId = loanRecordId;
		this.benJin = benJin;
		this.liXi = liXi;
		this.status = status;
		this.actureDate = actureDate;
		this.forecastDate = forecastDate;
		this.fwf = fwf;
		this.fundsType = fundsType;
		this.userId = userId;
		this.userName = userName;
		this.outUserId = outUserId;
	}
	private static final long serialVersionUID = -4317735007431501363L;
	private int id;//id
	private int loanRecordId;//借款ID
	private int qi;//期数
	private BigDecimal benJin;//本息
	private BigDecimal liXi;//利息
	private int status;//状态   拖欠  已还款
	private Timestamp actureDate;//实际还款日期
	private Timestamp forecastDate;//预计还款日期
	//private BigDecimal biLi = BigDecimal.ONE;
	private BigDecimal znj = BigDecimal.ZERO;//迟还款 滞纳金
	private BigDecimal fwf = BigDecimal.ZERO;//投资者 利息 服务费比例
	//private BigDecimal fwfBiLi = BigDecimal.ZERO;//投资者服务费比例
	private int fundsType;
	private String userId;
	private String userName;
	private String outUserId;//借出用户id
	
	private int dealStatus;//0无需处理 1未收集  2已收集
	private  BigDecimal amountDegLiXi;//抵扣券额度
	private int sourcetype;// 来源类型--8"网页"、5"手机APP"、6"接口API"
	
	@BeanField(persistence = false)
	private BigDecimal sumDeglx;//已用利息

	private BigDecimal amount;//本次借款总额
	private BigDecimal accruedRepay;//累计还款

	public BigDecimal getSumDeglx() {
		return sumDeglx;
	}
	public void setSumDeglx(BigDecimal sumDeglx) {
		this.sumDeglx = sumDeglx;
	}
	public BigDecimal getAmountDegLiXi() {
		return amountDegLiXi;
	}
	public void setAmountDegLiXi(BigDecimal amountDegLiXi) {
		this.amountDegLiXi = amountDegLiXi;
	}
	public int getSourcetype() {
		return sourcetype;
	}
	public void setSourcetype(int sourcetype) {
		this.sourcetype = sourcetype;
	}
	public int getDealStatus() {
		return dealStatus;
	}
	public void setDealStatus(int dealStatus) {
		this.dealStatus = dealStatus;
	}
	public String getOutUserId() {
		return outUserId;
	}
	public void setOutUserId(String outUserId) {
		this.outUserId = outUserId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getFundsType() {
		return fundsType;
	}
	public void setFundsType(int fundsType) {
		this.fundsType = fundsType;
	}
	
	public CoinProps getFt() {
		return DatabasesUtil.coinProps(fundsType);
	}
	/***
	 * 利息服务费
	 * @return
	 */
	public BigDecimal getLxFwf(){
		return liXi.multiply(fwf);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLoanRecordId() {
		return loanRecordId;
	}
	public void setLoanRecordId(int loanRecordId) {
		this.loanRecordId = loanRecordId;
	}
	public int getQi() {
		return qi;
	}
	public void setQi(int qi) {
		this.qi = qi;
	}
	public BigDecimal getBenJin() {
		return benJin;
	}
	public void setBenJin(BigDecimal benJin) {
		this.benJin = benJin;
	}
	public BigDecimal getLiXi() {
		return liXi;
	}
	public void setLiXi(BigDecimal liXi) {
		this.liXi = liXi;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Timestamp getActureDate() {
		return actureDate;
	}
	public void setActureDate(Timestamp actureDate) {
		this.actureDate = actureDate;
	}
	public Timestamp getForecastDate() {
		return forecastDate;
	}
	public void setForecastDate(Timestamp forecastDate) {
		this.forecastDate = forecastDate;
	}
//	public BigDecimal getBiLi() {
//		return biLi;
//	}
//	public void setBiLi(BigDecimal biLi) {
//		this.biLi = biLi;
//	}
	public BigDecimal getZnj() {
		return znj;
	}
	public void setZnj(BigDecimal znj) {
		this.znj = znj;
	}
	public BigDecimal getFwf() {
		return fwf;
	}
	public void setFwf(BigDecimal fwf) {
		this.fwf = fwf;
	}
//	public BigDecimal getFwfBiLi() {
//		return fwfBiLi;
//	}
//	public void setFwfBiLi(BigDecimal fwfBiLi) {
//		this.fwfBiLi = fwfBiLi;
//	}


	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAccruedRepay() {
		return accruedRepay;
	}

	public void setAccruedRepay(BigDecimal accruedRepay) {
		this.accruedRepay = accruedRepay;
	}

	public  RepayOfQiStatus getRepayStatus(){
		return (RepayOfQiStatus) EnumUtils.getEnumByKey(status, RepayOfQiStatus.class);
	}
	
	//逾期天数
		public int getYuQiDay(){
			RepayOfQiStatus rs = getRepayStatus();
			switch (rs) {
				case no:
				case yanshi:
					return TimeUtil.getIntervalDays(forecastDate , TimeUtil.getNow());
				case hasRepay :
				case yuqiyihuan :
					return TimeUtil.getIntervalDays(forecastDate , actureDate);
				default:
					return 0;
			}
		}
		
		//逾期利息  滞纳金：借款人逾期还息时，将每天产生利息*0.5%的滞纳金。
		public BigDecimal getYuQiLiXi(){
			BigDecimal lx = BigDecimal.ZERO;
			try {
				BigDecimal day = DigitalUtil.getBigDecimal(getYuQiDay());
				if(day.compareTo(BigDecimal.ZERO) > 0){
					lx = benJin.add(liXi).multiply(new BigDecimal("0.005")).multiply(day);
				}
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
			return lx;
		}
		
		public String getYuQiDayShow(){
			int day = getYuQiDay();
			String sh = "";
			if(day == 404404){
				sh = "—";
			}else{
				sh = String.valueOf(day);
			}
			
			sh = "<a href=\"javascript:;\" class=\"des\" mytitle=\"‘-’表示距离到期的天数\">" + sh;
			sh = sh + "</a>";
			return sh;
		}
}

package com.world.model.loan.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.bean.BeanField;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.coin.CoinProps;
import com.world.util.DigitalUtil;
import com.world.util.date.TimeUtil;

/****
 * 借贷成功的记录 包含转让借贷
 * @author apple
 *
 */
public class LoanRecord extends Bean{

	public LoanRecord() {
		super();
	}

	public LoanRecord(int loanId, boolean isIn, String inUserId, String inUserName, String outUserId, String outUserName, int fundsType, BigDecimal amount, int status, Timestamp createTime,
			BigDecimal balanceAmount, BigDecimal rate, BigDecimal hasRepay, String freezId, Timestamp nextRepayDate, int riskManage, int rateForm, BigDecimal rateAddVal, int deDuctCouponId) {
		
		super();
		this.loanId = loanId;
		this.isIn = isIn;
		this.inUserId = inUserId;
		this.inUserName = inUserName;
		this.outUserId = outUserId;
		this.outUserName = outUserName;
		this.fundsType = fundsType;
		this.amount = amount;
		this.status = status;
		this.createTime = createTime;
		this.balanceAmount = balanceAmount;
		this.rate = rate;
		this.hasRepay = hasRepay;
		this.freezId = freezId;
		this.nextRepayDate = nextRepayDate;
		this.riskManage = riskManage;
		this.rateForm = rateForm;
		this.rateAddVal = rateAddVal;
		this.deDuctCouponId = deDuctCouponId;
//		this.sourceType=sourceType;
	}
	
	private static final long serialVersionUID = 6651220215601522819L;
	private int id;
	private int loanId;//关联的借贷ID
	private boolean isIn;//loan是借入还是借出
	private String inUserId;//借入者ID
	private String inUserName;//借入者用户名
	
	private String outUserId;//借出者ID
	private String outUserName;//借出者用户名
	
	//资金类型  1RMB   2BTC   3LTC
	private int fundsType;
	
	private BigDecimal amount;//金额
	
	private int status;//状态
	
	private Timestamp createTime;//借贷成功时间
	
	private BigDecimal reward;//奖励金额
	private BigDecimal balanceAmount;//投标后借款剩余金额
	private BigDecimal rate;//利率
	private BigDecimal hasRepay;//已还本金金额
	private String freezId;
	private BigDecimal hasLx;//已还利息-------------
	private BigDecimal dikouLx;// 已抵扣利息
	private BigDecimal 	zheLx;// 折算线上价
	private BigDecimal arrearsLx;//拖欠利息
	private Timestamp nextRepayDate;//下次还款时间
	private int riskManage;//风险控制
	private boolean inUserLock;//借入者是否被锁定
	private BigDecimal withoutLxAmount;//免息额度
	private int withoutLxDays;//免息天数
	private int balanceWithoutLxDays;//剩余的免息天数
	private int rateForm;//利率形式
	private BigDecimal rateAddVal;//增长幅度
	private Timestamp repayDate;//还款时间
	private int tstatus;//转让状态
	private Timestamp transferStartDate;//对于接手转让者的起始时间
	private BigDecimal outUserFees;//放贷用户的手续费费率
	private int deDuctCouponId;// 抵扣券id
	private int sourceType;// 来源类型--8"网页"、5"手机APP"、6"接口API"
	private String recordStatusShow;//add by xwz 2017-06-18  国际化状态
	@BeanField(persistence = false)
	private BigDecimal thisRepay;//本次还款金额，不作为数据库字段
	@BeanField(persistence = false)
	private int source;//来源类型,只做传值
	
	@BeanField(persistence = false)
	private P2pUser p2pUser;//p2p用户

	public CoinProps getFt() {
		return DatabasesUtil.coinProps(fundsType);
	}
	
	/**
	 * 应还利息 = 本金*日利率*天数
	 */
	public BigDecimal getLx(){
		if(nextRepayDate == null){
			nextRepayDate = createTime;
		}
		BigDecimal difDay = new BigDecimal(TimeUtil.getDiffDay(TimeUtil.getNow(), nextRepayDate));
		if(nextRepayDate != null){
			difDay = difDay.add(BigDecimal.ONE);
		}
		int diffDay = difDay.intValue();
		
		//利息
		BigDecimal lx = BigDecimal.ZERO;
		
		if(withoutLxDays > 0) {
			BigDecimal jx = amount.subtract(withoutLxAmount);
			if(jx.compareTo(BigDecimal.ZERO) < 0){
				jx = BigDecimal.ZERO;
			}
			
			if(diffDay <= withoutLxDays){
				lx = jx.multiply(rate).multiply(difDay);
			}else{
				//免息期内的计息金额付息
				int bDay = diffDay - withoutLxDays;
				BigDecimal lx1 = jx.multiply(rate).multiply(DigitalUtil.getBigDecimal(withoutLxDays));
				
				lx = amount.multiply(rate).multiply(DigitalUtil.getBigDecimal(bDay)).add(lx1);
			}
		}else{
			lx = amount.multiply(rate).multiply(difDay);
		}
		//BigDecimal lx = amount.multiply(rate).multiply(difDay);
		return lx;
	}
	
	/***
	 * 需要还息 = lx - 已还利息
	 * @return
	 */
	public BigDecimal getNeedLx(){
		if(nextRepayDate == null){
			nextRepayDate = createTime;
		}
		
		BigDecimal difDay = new BigDecimal(TimeUtil.getDiffDay(TimeUtil.getNow(), nextRepayDate));
		
		if(nextRepayDate != null){
			difDay = difDay.add(BigDecimal.ONE);
		}
		
		int diffDay = difDay.intValue();
		
		//利息
		BigDecimal lx = BigDecimal.ZERO;
		
		
		if(withoutLxDays > 0) {
			BigDecimal jx = amount.subtract(withoutLxAmount);
			if(jx.compareTo(BigDecimal.ZERO) < 0){
				jx = BigDecimal.ZERO;
			}
			
			if(diffDay <= withoutLxDays){
				lx = jx.multiply(rate).multiply(difDay);
			}else{
				//免息期内的计息金额付息
				int bDay = diffDay - withoutLxDays;
				BigDecimal lx1 = jx.multiply(rate).multiply(DigitalUtil.getBigDecimal(withoutLxDays));
				
				lx = amount.multiply(rate).multiply(DigitalUtil.getBigDecimal(bDay)).add(lx1);
			}
		}else{
			lx = amount.multiply(rate).multiply(difDay);
		}
		return arrearsLx.add(lx);
	}
	
	/**
	 * 应还总额 = 本金+利息
	 */
	public BigDecimal getShouldRepayBX(){
		BigDecimal benXi = amount.add(getNeedLx());//amount.add(getLx());
		return benXi;
	}
	
	public BigDecimal getCouldRepay(){
		if(status == LoanRecordStatus.hasEnd.getKey() || status == LoanRecordStatus.forceSuccess.getKey()){
			return BigDecimal.ZERO;
		}
		return amount;
	}
	
	public BigDecimal getThisRepay() {
		return thisRepay==null?BigDecimal.ZERO:thisRepay;
	}

	public void setThisRepay(BigDecimal thisRepay) {
		this.thisRepay = thisRepay;
	}
	
	public int getSource() {
		return source == 0 ? 0 : source;
	}

	public void setSource(int source) {
		this.source = source;
	}
	
	public int getDeDuctCouponId() {
		return deDuctCouponId;
	}

	public void setDeDuctCouponId(int deDuctCouponId) {
		this.deDuctCouponId = deDuctCouponId;
	}

	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}
	
	public Timestamp getTransferStartDate() {
		return transferStartDate;
	}

	public void setTransferStartDate(Timestamp transferStartDate) {
		this.transferStartDate = transferStartDate;
	}

	public int getTstatus() {
		return tstatus;
	}

	public void setTstatus(int tstatus) {
		this.tstatus = tstatus;
	}

	public Timestamp getRepayDate() {
		return repayDate;
	}

	public void setRepayDate(Timestamp repayDate) {
		this.repayDate = repayDate;
	}

	//可以归到计息的金额
	public BigDecimal getJx(){
		int diffDay = TimeUtil.getDiffDay(TimeUtil.getNow(), this.getCreateTime());
		if(withoutLxDays > 0 && diffDay<=10){
			BigDecimal jx = amount.subtract(withoutLxAmount);
			if(jx.compareTo(BigDecimal.ZERO) < 0){
				jx = BigDecimal.ZERO;
			}
			return jx;
		}
		return amount;
	}
	
	public BigDecimal getArrearsLx() {
		return arrearsLx;
	}

	public void setArrearsLx(BigDecimal arrearsLx) {
		this.arrearsLx = arrearsLx;
	}

	/////判断今日是否还息     day(lastRepayDate)==day(now())
	/****
	 * <p>分给借出者的利息比例，一部分划给网站作服务费</p>
	 * @return
	 */
	public BigDecimal getFwfScale(){
		if(outUserFees != null && outUserFees.compareTo(BigDecimal.ZERO) >= 0){
			return outUserFees;
		}
		
		RiskType rt = getRiskType();
		if(rt != null){
			return rt.getFwfScale();
		}
		return BigDecimal.ZERO;
	}
	
	public RiskType getRiskType() {
		return (RiskType) EnumUtils.getEnumByKey(riskManage, RiskType.class);
	}
	
	public int getRiskManage() {
		return riskManage;
	}

	public void setRiskManage(int riskManage) {
		this.riskManage = riskManage;
	}

	public BigDecimal getRateShow(){
		return rate.multiply(new BigDecimal(100));
	}
	
	public LoanRecordStatus getRecordStatus(){
		return (LoanRecordStatus)EnumUtils.getEnumByKey(status, LoanRecordStatus.class);
	}

	public void setRecordStatusShow(String recordStatusShow){
		this.recordStatusShow = recordStatusShow;
	}
	public String getRecordStatusShow(){
		return recordStatusShow;
	}
	
	public int getLoanId() {
		return loanId;
	}

	public void setLoanId(int loanId) {
		this.loanId = loanId;
	}

	public boolean getIsIn() {
		return isIn;
	}

	public void setIsIn(boolean isIn) {
		this.isIn = isIn;
	}

	public String getInUserId() {
		return inUserId;
	}

	public void setInUserId(String inUserId) {
		this.inUserId = inUserId;
	}

	public String getInUserName() {
		return inUserName;
	}

	public void setInUserName(String inUserName) {
		this.inUserName = inUserName;
	}

	public String getOutUserId() {
		return outUserId;
	}

	public void setOutUserId(String outUserId) {
		this.outUserId = outUserId;
	}

	public String getOutUserName() {
		return outUserName;
	}

	public void setOutUserName(String outUserName) {
		this.outUserName = outUserName;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public Timestamp getNextRepayDate() {
		return nextRepayDate;
	}

	public void setNextRepayDate(Timestamp nextRepayDate) {
		this.nextRepayDate = nextRepayDate;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFundsType() {
		return fundsType;
	}
	public void setFundsType(int fundsType) {
		this.fundsType = fundsType;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public BigDecimal getReward() {
		return reward;
	}

	public void setReward(BigDecimal reward) {
		this.reward = reward;
	}

	public BigDecimal getRate() {
		return rate;
	}
	
	public void setLx(BigDecimal rate) {
		this.rate = rate;
	}
	
	public BigDecimal getHasRepay() {
		if(status == LoanRecordStatus.hasEnd.getKey() || status == LoanRecordStatus.forceSuccess.getKey()){
			if(hasRepay.compareTo(BigDecimal.ZERO) == 0){
				setHasRepay(amount);
				setAmount(BigDecimal.ZERO);
			}
		}
		return hasRepay;
	}

	public void setHasRepay(BigDecimal hasRepay) {
		this.hasRepay = hasRepay;
	}

	public String getFreezId() {
		return freezId;
	}

	public void setFreezId(String freezId) {
		this.freezId = freezId;
	}

	public BigDecimal getHasLx() {
		return hasLx;
	}

	public void setHasLx(BigDecimal hasLx) {
		this.hasLx = hasLx;
	}

	public BigDecimal getDikouLx() {
		return dikouLx;
	}

	public void setDikouLx(BigDecimal dikouLx) {
		this.dikouLx = dikouLx;
	}

	public BigDecimal getZheLx() {
		return zheLx;
	}

	public void setZheLx(BigDecimal zheLx) {
		this.zheLx = zheLx;
	}

	public BigDecimal getWithoutLxAmount() {
		return withoutLxAmount;
	}

	public void setWithoutLxAmount(BigDecimal withoutLxAmount) {
		this.withoutLxAmount = withoutLxAmount;
	}

	public int getWithoutLxDays() {
		return withoutLxDays;
	}

	public void setWithoutLxDays(int withoutLxDays) {
		this.withoutLxDays = withoutLxDays;
	}

	public boolean isInUserLock() {
		return inUserLock;
	}

	public void setInUserLock(boolean inUserLock) {
		this.inUserLock = inUserLock;
	}
	
	public int getBalanceWithoutLxDays() {
		return balanceWithoutLxDays;
	}

	public void setBalanceWithoutLxDays(int balanceWithoutLxDays) {
		this.balanceWithoutLxDays = balanceWithoutLxDays;
	}
	
	public int getRateForm() {
		return rateForm;
	}

	public void setRateForm(int rateForm) {
		this.rateForm = rateForm;
	}

	public BigDecimal getRateAddVal() {
		return rateAddVal;
	}

	public void setRateAddVal(BigDecimal rateAddVal) {
		this.rateAddVal = rateAddVal;
	}

	public BigDecimal getOutUserFees() {
		return outUserFees;
	}

	public void setOutUserFees(BigDecimal outUserFees) {
		this.outUserFees = outUserFees;
	}

	public P2pUser getP2pUser() {
		return p2pUser;
	}

	public void setP2pUser(P2pUser p2pUser) {
		this.p2pUser = p2pUser;
	}
	public String getInterestRateFormShow(){
		InterestRateForm ir = (InterestRateForm) EnumUtils.getEnumByKey(rateForm, InterestRateForm.class);
		if(ir != null){
			if(ir.getKey() > 1){
				return ir.getValue() + new DecimalFormat("#.###").format(rateAddVal.multiply(new BigDecimal(100))) + "%";
			}
		}
		return "固定";
	}
}

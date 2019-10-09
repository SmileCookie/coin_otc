package com.world.model.loan.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.coin.CoinProps;
import com.world.util.DigitalUtil;
/****
 * 借贷类  借入或借出的载体
 * @author apple
 */
public class Loan extends Bean{

	private static final long serialVersionUID = 6651220215601522819L;
	
	
	public Loan(){
		super();
	}
	
	public Loan(String userId, String userName, int fundsType, BigDecimal amount, int status, Timestamp createTime){
		super();
		this.userId = userId;
		this.userName = userName;
		this.fundsType = fundsType;
		this.amount = amount;
		this.status = status;
		this.createTime = createTime;
	}

	public Loan(boolean isIn , String userId, String userName, int fundsType, BigDecimal amount, int status, Timestamp createTime, BigDecimal interestRateOfDay,
			int repayment, BigDecimal lowestAmount, BigDecimal highestAmount, String password , int rateForm , BigDecimal rateAddVal){
		this(userId, userName, fundsType, amount, status, createTime);
		this.isIn = isIn;
		this.interestRateOfDay = interestRateOfDay;
		this.repayment = repayment;
		this.lowestAmount = lowestAmount;
		this.highestAmount = highestAmount;
		this.password = password;
		this.rateForm = rateForm;
		this.rateAddVal = rateAddVal;
	}

	public Loan(boolean isIn , String userId, String userName, int fundType, BigDecimal amount, int status, Timestamp createTime, BigDecimal interestRateOfDay,
			int repayment, BigDecimal lowestAmount, BigDecimal highestAmount, String password, int rateForm , BigDecimal rateAddVal,int riskManage,boolean withoutLx,boolean isLoop,BigDecimal sourceAmount){
		this(isIn, userId, userName, fundType, amount, status, createTime, interestRateOfDay, repayment, lowestAmount, highestAmount, password,rateForm , rateAddVal);
		this.riskManage = riskManage;
		this.withoutLx = withoutLx;
		this.isLoop = isLoop;
		this.sourceAmount = sourceAmount;
	}
	public Loan(boolean isIn , String userId, String userName, int fundType, BigDecimal amount, int status, Timestamp createTime, BigDecimal interestRateOfDay,
			int repayment, BigDecimal lowestAmount, BigDecimal highestAmount, String password, int rateForm , BigDecimal rateAddVal,int riskManage,boolean withoutLx,boolean isLoop,BigDecimal sourceAmount,int loanRecordId){
		this(isIn, userId, userName, fundType, amount, status, createTime, interestRateOfDay, repayment, lowestAmount, highestAmount, password,rateForm , rateAddVal, riskManage, withoutLx, isLoop,sourceAmount);
		this.loanRecordId = loanRecordId;
	}
	//多加了一个investMark
	//16/11/23 加了 sourceType 来源类型： 8"网页"，5"手机APP"，6"接口API"
	public Loan(boolean isIn , String userId, String userName, int fundType, BigDecimal amount, int status, Timestamp createTime, BigDecimal interestRateOfDay,
			int repayment, BigDecimal lowestAmount, BigDecimal highestAmount, String password, int rateForm , BigDecimal rateAddVal,int riskManage,boolean withoutLx,boolean isLoop,BigDecimal sourceAmount,int loanRecordId,int investMark){
		this(isIn, userId, userName, fundType, amount, status, createTime, interestRateOfDay, repayment, lowestAmount, highestAmount, password,rateForm , rateAddVal, riskManage, withoutLx, isLoop,sourceAmount);
		this.loanRecordId = loanRecordId;
		this.investMark=investMark;
//		this.sourceType=sourceType;
	}
	
	private int id;
	private boolean isIn;//是否是借入   false表示借出
	private String userId;
	private String userName;
	
	//资金类型  1RMB   2BTC   3LTC
	private int fundsType;
	//借的资金
	private BigDecimal amount;
	
	//0等待审核    2通过    3不通过
	private int status;
	
	private Timestamp createTime;
	
	private BigDecimal interestRateOfDay;//日利率
	private int repayment;//还款方式
	
	private BigDecimal lowestAmount;//最低投标限额
	private BigDecimal highestAmount;//最高限额
	private String password;//投资密码
	private int riskManage;//投资标的风险控制    1自担风险（费用20%利润）   2我只要本金币种（费用50%利润）
	
	private BigDecimal hasAmount;//已完成金额
	private Timestamp startDate;//开始投标时间
	
	private int inTimes;//投标次数
	
	private BigDecimal hasRepayment;//已还本息
	private Timestamp startLoanDate;//开始借款时间
	private BigDecimal totalRepayment;//总还款
	private BigDecimal fees;//借入者    保存的成交费   根据借款期限按比例收取
	private BigDecimal znj;//迟还款 滞纳金
	private BigDecimal fwf;//投资者 利息 服务费    10%
	
	private BigDecimal fwfBiLi;//投资者服务费比例
	private int rateForm;//利率形式
	private BigDecimal rateAddVal;//利率增量
	private boolean withoutLx;//是否允许使用免息券
	
	private boolean isLoop;//是否循环投资
	private int loanRecordId;//循环投资loanRecordId
	private BigDecimal sourceAmount; //初始总额
	private int investMark;//投资标识，0为手动，1为自动
	private int sourceType;//来源类型： 8"网页"，5"手机APP"，6"接口API"
	
	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}

	public int getInvestMark() {
		return investMark;
	}

	public void setInvestMark(int investMark) {
		this.investMark = investMark;
	}

	public BigDecimal getSourceAmount() {
		return sourceAmount;
	}

	public void setSourceAmount(BigDecimal sourceAmount) {
		this.sourceAmount = sourceAmount;
	}

	public boolean getIsLoop() {
		return isLoop;
	}

	public void setIsLoop(boolean isLoop) {
		this.isLoop = isLoop;
	}

	public int getLoanRecordId() {
		return loanRecordId;
	}

	public void setLoanRecordId(int loanRecordId) {
		this.loanRecordId = loanRecordId;
	}

	public boolean getWithoutLx() {
		return withoutLx;
	}

	public void setWithoutLx(boolean withoutLx) {
		this.withoutLx = withoutLx;
	}

	public RiskType getRiskType(){
		return (RiskType) EnumUtils.getEnumByKey(riskManage, RiskType.class);
	}
	
	public Repayment getRepayWay(){//获取还款方式
		return (Repayment) EnumUtils.getEnumByKey(repayment, Repayment.class);
	}
	
	public BigDecimal getRateAddVal() {
		return rateAddVal;
	}
	public void setRateAddVal(BigDecimal rateAddVal) {
		this.rateAddVal = rateAddVal;
	}
	public int getRateForm() {
		return rateForm;
	}
	public void setRateForm(int rateForm) {
		this.rateForm = rateForm;
	}
	/***
	 * 剩余可借贷金额
	 * @return
	 */
	public BigDecimal getBalance(){
		if(hasAmount == null){
			hasAmount = BigDecimal.ZERO;
		} 
		return amount.subtract(hasAmount);
	}
	/****
	 * 最高限额显示
	 * @return
	 */
	public String getGxeshow(){
		if(highestAmount.compareTo(BigDecimal.ZERO) <= 0){
			return "不限";
		}else{
			return highestAmount.setScale(1).toString();
		}
	}
	
	public BigDecimal getHasBidRate(){
		try{
//			MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
//			return hasAmount.divide(amount , mc).setScale(0).multiply(DigitalUtil.getBigDecimal(100)).setScale(0);
			
			return hasAmount.divide(amount , 3,  BigDecimal.ROUND_HALF_EVEN).multiply(DigitalUtil.getBigDecimal(100)).setScale(0 , RoundingMode.HALF_DOWN);
		}catch(Exception ex){
			log.error(ex.toString(), ex);
		}
		return BigDecimal.ZERO;
	}
	
	public int getHasBidRate2(){
		float src = getHasBidRate().floatValue();
		if(src > 0 && src < 1){
			src = 1;
		}else if(src > 99 && src < 100){
			src = 99;
		}
		int width =  (int)src;
		return width;
	}
	
	public BigDecimal getRateOfDayShow(){
		return interestRateOfDay.multiply(new BigDecimal(100));
	}
	
	public LoanStatus getLoanStatus(){
		return (LoanStatus)EnumUtils.getEnumByKey(status, LoanStatus.class);
	}
	
	public boolean getIsIn() {
		return isIn;
	}

	public void setIsIn(boolean isIn) {
		this.isIn = isIn;
	}

	public BigDecimal getHasAmount() {
		return hasAmount;
	}

	public void setHasAmount(BigDecimal hasAmount) {
		this.hasAmount = hasAmount;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public BigDecimal getBalanceAmount(){
		return amount.subtract(hasAmount);
	}
	
	public CoinProps getFt() {
		return DatabasesUtil.coinProps(fundsType);
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

	public BigDecimal getInterestRateOfDay() {
		return interestRateOfDay;
	}

	public void setInterestRateOfDay(BigDecimal interestRateOfDay) {
		this.interestRateOfDay = interestRateOfDay;
	}

	public int getRepayment() {
		return repayment;
	}

	public void setRepayment(int repayment) {
		this.repayment = repayment;
	}

	public BigDecimal getLowestAmount() {
		return lowestAmount;
	}

	public void setLowestAmount(BigDecimal lowestAmount) {
		this.lowestAmount = lowestAmount;
	}

	public BigDecimal getHighestAmount() {
		return highestAmount;
	}

	public void setHighestAmount(BigDecimal highestAmount) {
		this.highestAmount = highestAmount;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getInTimes() {
		return inTimes;
	}

	public void setInTimes(int inTimes) {
		this.inTimes = inTimes;
	}

	public BigDecimal getHasRepayment() {
		return hasRepayment;
	}

	public void setHasRepayment(BigDecimal hasRepayment) {
		this.hasRepayment = hasRepayment;
	}

	public Timestamp getStartLoanDate() {
		return startLoanDate;
	}

	public void setStartLoanDate(Timestamp startLoanDate) {
		this.startLoanDate = startLoanDate;
	}

	public BigDecimal getTotalRepayment() {
		return totalRepayment;
	}

	public void setTotalRepayment(BigDecimal totalRepayment) {
		this.totalRepayment = totalRepayment;
	}

	public BigDecimal getFees() {
		return fees;
	}

	public void setFees(BigDecimal fees) {
		this.fees = fees;
	}

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

	public BigDecimal getFwfBiLi() {
		return fwfBiLi;
	}

	public void setFwfBiLi(BigDecimal fwfBiLi) {
		this.fwfBiLi = fwfBiLi;
	}
	public int getRiskManage() {
		return riskManage;
	}
	public void setRiskManage(int riskManage) {
		this.riskManage = riskManage;
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

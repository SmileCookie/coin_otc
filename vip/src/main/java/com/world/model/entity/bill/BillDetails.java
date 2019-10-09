package com.world.model.entity.bill;

import com.world.data.big.table.TableInfo;
import com.world.data.big.table.UpdateWay;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.user.User;
import com.world.util.CommonUtil;
import com.world.util.date.TimeUtil;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;

/****
 * 账单明细
 * 在资金发生变化的情况下记录当前的账户资金详情
 * 
 * @author Administrator
 *@TableInfo(databases = {"vip_main"},targetDatabases={"vip_main_buckup"} , tableName = "bill" , tableDown = true , shardNum = 1 , field = "userId" , updateWay = UpdateWay.ASYNC , asyncFrequency = 300 , primaryKey = "id" , 
 *conditions = {"date < ? limit 0,200"} , conditionsParams={"getDownTableDate"})
 */
@TableInfo(databases = {"vip_main" } , tableName = "bill" , tableDown = true , shardNum = 1 , field = "userId" , updateWay = UpdateWay.ASYNC , asyncFrequency = 300 , primaryKey = "id" ,
conditions = {"isFinaAccount<3 and sendTime < ? order by id asc limit 0,500"},conditionsParams={"getDownTableDate"})
public class BillDetails extends Bean{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2965898812435540465L;
	private long id;
	private int type;//0提现 1PPS收币 2PPLNS收币 5理财 6变现 7系统充值  8系统扣除
	private int userId;//用户ID
	private String userName;
	private int status;
	private BigDecimal amount;
	private Timestamp sendTime;//时间
	private String remark;
	private BigDecimal balance;
	private BigDecimal fees;//手续费
	private String adminId;
	private String timestr;
	private String entrustId;
	private int fundsType;//资金类型
	private String strType;
	private String commandId;
	private String coinName;//币种名称

	//20171031 chendi 处理报表字段展示
	private String mainMarket;
	private String subMarket;
	private BigDecimal convertPrice;
	/*end*/
	
	/**
	 * 新增理财字段
	 */
	private BigDecimal vdsUsdtPrice;
	
	public BigDecimal getVdsUsdtPrice() {
		return vdsUsdtPrice;
	}

	public void setVdsUsdtPrice(BigDecimal vdsUsdtPrice) {
		this.vdsUsdtPrice = vdsUsdtPrice;
	}

	public BigDecimal getConvertPrice() {
		return convertPrice;
	}

	public void setConvertPrice(BigDecimal convertPrice) {
		this.convertPrice = convertPrice;
	}

	public String getMainMarket() {
		return mainMarket;
	}

	public void setMainMarket(String mainMarket) {
		this.mainMarket = mainMarket;
	}

	public String getSubMarket() {
		return subMarket;
	}

	public void setSubMarket(String subMarket) {
		this.subMarket = subMarket;
	}


	//20170826 xzhang 处理报表字段展示
	private String amountView;//报表金额展示处理

	/*start by cxb 20170317 新增字段*/
	/*是否已核算,账户管理每日结算使用,默认值0，核算中1，已核算2,3留存最后一条记录每个用户资金类型的*/
	private int isfinaaccount;
	/*用户资金监控表监控编号*/
	private String ucmId;
	/*end*/

	//20170826 xzhang 处理报表字段展示
	public String  getAmountView() {
		if(getBt() == null){
			return "";
		}
		if(getBt().getInout() == 0){
			return "…";
		}else if (getBt().getInout() == 1){
			return "+"+ CommonUtil.getAmountStr(this.amount,7)+"="+CommonUtil.getAmountStr(this.balance,7);
		}else if(getBt().getInout() == 2){
			return "-"+CommonUtil.getAmountStr(this.amount,7)+"="+CommonUtil.getAmountStr(this.balance,7);
		}
		return "";
	}


	public String getStrType() {
		return strType;
	}

	public void setStrType(String strType) {
		this.strType = strType;
	}

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public String getCoinName() {
		return getCoin()!=null?getCoin().getPropTag():coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	private User user;
	
	public BillType getBt(){
		/*start by xwz 20170608*/
		int tmpType = 0;
		if(StringUtils.isNotEmpty(strType)){
			try{
				tmpType = Integer.parseInt(strType);
			}catch (Exception e){
				tmpType = 0;
			}
		}

		if(tmpType > 0) {
			type = tmpType;
		}

		return (BillType)EnumUtils.getEnumByKey(type, BillType.class);
		/*end*/
	}

	/*start by xwz 20170608 新增*/
	public BillType getStrBt(){
		int tmpType = 0;
		if(StringUtils.isNotEmpty(strType)){
			try{
				tmpType = Integer.parseInt(strType);
			}catch (Exception e){
				tmpType = 0;
			}
		}
		return (BillType)EnumUtils.getEnumByKey(tmpType, BillType.class);
	}
	/*end*/
	
	public CoinProps getCoin(){
		 return DatabasesUtil.coinProps(fundsType);
	} 
	
	private String showType;

	public void setShowType(String showType) {
		this.showType = showType;
	}

	public String getShowType(){
		return getBt() != null ? getBt().getValue() : null;
	}

	private String inout;
	public void setInout(String inout) {
		this.inout = inout;
	}

	public String getInout(){
		BillType type = getBt();
		if(type == null){
			return "-";
		}
		return type.getInout() == 1 ? "收入" : "支出";
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Timestamp getSendTime() {
		return sendTime;
	}

	public void setSendTime(Timestamp sendTime) {
		this.sendTime = sendTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getFees() {
		return fees;
	}

	public void setFees(BigDecimal fees) {
		this.fees = fees;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTimestr() {
		return timestr;
	}

	public void setTimestr(String timestr) {
		this.timestr = timestr;
	}

	public String getEntrustId() {
		return entrustId;
	}

	public void setEntrustId(String entrustId) {
		this.entrustId = entrustId;
	}

	public int getFundsType() {
		return fundsType;
	}

	public void setFundsType(int fundsType) {
		this.fundsType = fundsType;
	}

	public Timestamp getDownTableDate(){
		return TimeUtil.getToday0Show(TimeUtil.getAfterDay(-1));
	}

	public int getIsfinaaccount() {
		return isfinaaccount;
	}

	public void setIsfinaaccount(int isfinaaccount) {
		this.isfinaaccount = isfinaaccount;
	}

	public String getUcmId() {
		return ucmId;
	}

	public void setUcmId(String ucmId) {
		this.ucmId = ucmId;
	}
}

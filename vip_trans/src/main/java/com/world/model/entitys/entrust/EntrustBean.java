package com.world.model.entitys.entrust;

import com.world.data.big.table.TableInfo;
import com.world.data.big.table.UpdateWay;
import com.world.data.mysql.Bean;

import java.math.BigDecimal;



/**
 * 委托的实例
 * 
 * 切分表条件：
 * 
 * 状态1   保留1天   取消成功  2016-5-30 取消单保留一天改为保留1小时
 * 状态2 unitPrice>0   交易成功      保留7天
 * 状态2 unitPrice=0   取消中介单   保留10分钟
 *
 * update by buxianguan 20171206 配合成交记录改版，迁移数据频率改成24小时
 * 
 * @author netpet
 *DATE_SUB("1997-12-31 23:59:59" , INTERVAL 1 DAY)
 */
@TableInfo(databases = {"btcusdtentrust","dashbtcentrust","elfusdtentrust","eosbtcentrust","eosusdtentrust","etcbtcentrust","etcusdtentrust","ethbtcentrust",
                        "ethusdtentrust","kncusdtentrust","linkbtcentrust","ltcbtcentrust","ltcusdtentrust","omgbtcentrust", "qtumusdtentrust","sntusdtentrust","zrxbtcentrust",
                        "manabtcentrust","mcobtcentrust","lrcbtcentrust","dgdbtcentrust","vdsbtcentrust","vdsusdtentrust"
                       } , tableName = "entrust" , tableDown = true , shardNum = 1 , field = "userId" , updateWay = UpdateWay.ASYNC , asyncFrequency = 300 , primaryKey = "entrustId" ,
		   conditions = {"submitTime < (UNIX_TIMESTAMP(NOW()) - 86400) * 1000 and status=1 limit 0,200",
						 "submitTime < (UNIX_TIMESTAMP(NOW()) - 86400) * 1000 and status=2 limit 0,200"
						})
public class EntrustBean extends Bean{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2145387088165480695L;
	//委托id
	private long entrustId;
	//单价
	private BigDecimal unitPrice; 
	//数量
	private BigDecimal numbers; 
	//总额
	private BigDecimal totalMoney;
	//完成数量
	private BigDecimal completeNumber; 
	//完成总额度
	private BigDecimal completeTotalMoney; 
	//归结到哪个web
	private int sumToWeb; 
	
	
	//主web  8网页   5 app    6 api  
	private int webId;
	
	
	//类型  0 卖出 1 购买  -1 取消
	private int types; 
	//用户id
	private int userId; 
	//状态 0起始 1取消 2交易成功 3交易一部分
	private int status; 
	//冻结id  对于取消记录的是需要取消的id
	private long freezeId; 
	//提交时间
	private long submitTime;
	//交易手续费
	private BigDecimal feeRate;
	
	//用户自带的委托单id
	private String customerOrderId;
	
	public long getEntrustId() {
		return entrustId;
	}
	public void setEntrustId(long entrustId) {
		this.entrustId = entrustId;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public BigDecimal getNumbers() {
		return numbers;
	}
	public void setNumbers(BigDecimal numbers) {
		this.numbers = numbers;
	}
	public BigDecimal getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}
	public BigDecimal getCompleteNumber() {
		return completeNumber;
	}
	public void setCompleteNumber(BigDecimal completeNumber) {
		this.completeNumber = completeNumber;
	}
	public BigDecimal getCompleteTotalMoney() {
		return completeTotalMoney;
	}
	public void setCompleteTotalMoney(BigDecimal completeTotalMoney) {
		this.completeTotalMoney = completeTotalMoney;
	}
	public int getSumToWeb() {
		return sumToWeb;
	}
	public void setSumToWeb(int sumToWeb) {
		this.sumToWeb = sumToWeb;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getFreezeId() {
		return freezeId;
	}
	public void setFreezeId(long freezeId) {
		this.freezeId = freezeId;
	}
	public long getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(long submitTime) {
		this.submitTime = submitTime;
	}
	public String getCustomerOrderId() {
		return customerOrderId;
	}
	public void setCustomerOrderId(String customerOrderId) {
		this.customerOrderId = customerOrderId;
	}
	public BigDecimal getFeeRate() {
		return feeRate;
	}
	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}
	
	
}
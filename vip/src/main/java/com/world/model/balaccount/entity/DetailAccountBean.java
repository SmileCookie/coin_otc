package com.world.model.balaccount.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import com.world.data.mysql.Bean;

/**
 * <p>标题: 充值对账实体类</p>
 * <p>描述: 充值对账实体类-提取字段</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version 
 */
public class DetailAccountBean extends Bean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*资金类型*/
	private int fundType;
	/*交易平台-流水号*/
	private String bgId;
	/*交易平台-充值金额*/
	private BigDecimal bgTxAmount;
	/*交易平台-区块高度*/
    private int bgBlockHeight;
    /*交易平台-确认时间*/
    private Timestamp bgConfigTime;
    /*交易平台-充值状态*/
    private int bgStatus;
	/*交易平台-充值id*/
	private long bgDetailsId;
	/*支付中心-流水号*/
	private String pmId;
	/*支付中心-充值金额*/
	private BigDecimal pmTxAmount;
	/*支付中心-区块高度*/
    private int pmBlockHeight;
    /*支付中心-确认时间*/
    private Timestamp pmConfigTime;
    /*支付中心-充值状态*/
    private int pmStatus;
    /*用于比较两边的充值金额是否相等*/
    private BigDecimal amountFlag;
    /*导出Excel case when 转换*/
    /*交易平台-充值状态*/
    private String strBgStatus;
    /*支付中心-充值状态*/
    private String strPmStatus;
    /*对账标志*/
    private String strAmountFlag;
    /*交易平台-区块高度*/
    private String strBgBlockHeight;
    /*支付中心-区块高度*/
    private String strPmBlockHeight;

	public int getFundType() {
		return fundType;
	}
	public void setFundType(int fundType) {
		this.fundType = fundType;
	}
	public String getBgId() {
		return bgId;
	}
	public void setBgId(String bgId) {
		this.bgId = bgId;
	}
	public BigDecimal getBgTxAmount() {
		return bgTxAmount;
	}
	public void setBgTxAmount(BigDecimal bgTxAmount) {
		this.bgTxAmount = bgTxAmount;
	}
	public int getBgBlockHeight() {
		return bgBlockHeight;
	}
	public void setBgBlockHeight(int bgBlockHeight) {
		this.bgBlockHeight = bgBlockHeight;
	}
	public Timestamp getBgConfigTime() {
		return bgConfigTime;
	}
	public void setBgConfigTime(Timestamp bgConfigTime) {
		this.bgConfigTime = bgConfigTime;
	}
	public int getBgStatus() {
		return bgStatus;
	}
	public void setBgStatus(int bgStatus) {
		this.bgStatus = bgStatus;
	}
	public String getPmId() {
		return pmId;
	}
	public void setPmId(String pmId) {
		this.pmId = pmId;
	}
	public BigDecimal getPmTxAmount() {
		return pmTxAmount;
	}
	public void setPmTxAmount(BigDecimal pmTxAmount) {
		this.pmTxAmount = pmTxAmount;
	}
	public int getPmBlockHeight() {
		return pmBlockHeight;
	}
	public void setPmBlockHeight(int pmBlockHeight) {
		this.pmBlockHeight = pmBlockHeight;
	}
	public Timestamp getPmConfigTime() {
		return pmConfigTime;
	}
	public void setPmConfigTime(Timestamp pmConfigTime) {
		this.pmConfigTime = pmConfigTime;
	}
	public int getPmStatus() {
		return pmStatus;
	}
	public void setPmStatus(int pmStatus) {
		this.pmStatus = pmStatus;
	}
	public BigDecimal getAmountFlag() {
		return amountFlag;
	}
	public void setAmountFlag(BigDecimal amountFlag) {
		this.amountFlag = amountFlag;
		if(null != amountFlag && amountFlag.compareTo(BigDecimal.ZERO) == 0) {
			this.amountFlag = BigDecimal.ZERO;
		}
	}
	public String getStrBgStatus() {
		return strBgStatus;
	}
	public void setStrBgStatus(String strBgStatus) {
		this.strBgStatus = strBgStatus;
	}
	public String getStrPmStatus() {
		return strPmStatus;
	}
	public void setStrPmStatus(String strPmStatus) {
		this.strPmStatus = strPmStatus;
	}
	public String getStrAmountFlag() {
		return strAmountFlag;
	}
	public void setStrAmountFlag(String strAmountFlag) {
		this.strAmountFlag = strAmountFlag;
	}
	public String getStrBgBlockHeight() {
		return strBgBlockHeight;
	}
	public void setStrBgBlockHeight(String strBgBlockHeight) {
		this.strBgBlockHeight = strBgBlockHeight;
	}
	public String getStrPmBlockHeight() {
		return strPmBlockHeight;
	}
	public void setStrPmBlockHeight(String strPmBlockHeight) {
		this.strPmBlockHeight = strPmBlockHeight;
	}
	public long getBgDetailsId() {
		return bgDetailsId;
	}
	public void setBgDetailsId(long bgDetailsId) {
		this.bgDetailsId = bgDetailsId;
	}
}

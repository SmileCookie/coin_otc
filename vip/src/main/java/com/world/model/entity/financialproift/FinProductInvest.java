package com.world.model.entity.financialproift;

import java.math.BigDecimal;
import java.util.Date;

import com.world.data.mysql.Bean;

public class FinProductInvest extends Bean {
    private static final long serialVersionUID = 1L;
    
    /**/
    private int id;
    /*用户ID*/
    private int userId;
    /*用户名*/
    private String userName;
    /*资金类型*/
    private int fundsType;
    /*产品编号*/
    private String proId;
    /*投资金额*/
    private BigDecimal investAmount;
    /*投资金额对应的矩阵*/
    private int matrixLevel;
    /*VIP增值权重*/
    private BigDecimal vipWeight;
    /*投资期次*/
    private String investProPeriod;
    /*投资时间*/
    private Date investTime;
    /*投资时VDS对应USDT价格*/
    private BigDecimal vdsUsdtPrice;
    /*投资转换为当时USDT金额*/
    private BigDecimal investUsdtAmount;
    /*预期收益转换为USDT金额*/
    private BigDecimal expectProfitUsdt;
    /*自动复投标志，0人工复投，1释放冻结资金触发复投*/
    private int doubleThrowFlag;
    /*VDS生态回馈处理标记,0是默认,1是已处理*/
    private int ecologySystemDealFlag;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public int getFundsType() {
		return fundsType;
	}
	public void setFundsType(int fundsType) {
		this.fundsType = fundsType;
	}
	public String getProId() {
		return proId;
	}
	public void setProId(String proId) {
		this.proId = proId;
	}
	public BigDecimal getInvestAmount() {
		return investAmount;
	}
	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}
	public int getMatrixLevel() {
		return matrixLevel;
	}
	public void setMatrixLevel(int matrixLevel) {
		this.matrixLevel = matrixLevel;
	}
	public BigDecimal getVipWeight() {
		return vipWeight;
	}
	public void setVipWeight(BigDecimal vipWeight) {
		this.vipWeight = vipWeight;
	}
	public String getInvestProPeriod() {
		return investProPeriod;
	}
	public void setInvestProPeriod(String investProPeriod) {
		this.investProPeriod = investProPeriod;
	}
	public Date getInvestTime() {
		return investTime;
	}
	public void setInvestTime(Date investTime) {
		this.investTime = investTime;
	}
	public BigDecimal getVdsUsdtPrice() {
		return vdsUsdtPrice;
	}
	public void setVdsUsdtPrice(BigDecimal vdsUsdtPrice) {
		this.vdsUsdtPrice = vdsUsdtPrice;
	}
	public BigDecimal getInvestUsdtAmount() {
		return investUsdtAmount;
	}
	public void setInvestUsdtAmount(BigDecimal investUsdtAmount) {
		this.investUsdtAmount = investUsdtAmount;
	}
	public BigDecimal getExpectProfitUsdt() {
		return expectProfitUsdt;
	}
	public void setExpectProfitUsdt(BigDecimal expectProfitUsdt) {
		this.expectProfitUsdt = expectProfitUsdt;
	}
	public int getDoubleThrowFlag() {
		return doubleThrowFlag;
	}
	public void setDoubleThrowFlag(int doubleThrowFlag) {
		this.doubleThrowFlag = doubleThrowFlag;
	}
	public int getEcologySystemDealFlag() {
		return ecologySystemDealFlag;
	}
	public void setEcologySystemDealFlag(int ecologySystemDealFlag) {
		this.ecologySystemDealFlag = ecologySystemDealFlag;
	}
}

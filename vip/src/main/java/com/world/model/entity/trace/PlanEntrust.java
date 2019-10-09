package com.world.model.entity.trace;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

public class PlanEntrust extends Bean {
    private static final long serialVersionUID = 1L;
    private long entrustId;//委托ID
    private BigDecimal unitPrice;//委托单价
    private BigDecimal numbers;//委托数量
    private BigDecimal totalMoney;//委托总价
    private BigDecimal completeNumber;//已完成数量
    private BigDecimal completeTotalMoney;//已完成金额
    private int sumToWeb;
    private int webId;//委托途径 5：APP 6：API 8：网站
    private int types;//0:卖 1：买
    private int userId;//挂单用户ID
    private int status;//挂单成交状态
    private long freezed;
    private long submitTime ;//挂单委托时间
    private BigDecimal feeRate ;//手续费费率
    private BigDecimal triggerPrice;
    private BigDecimal triggerPriceProfit;
    private BigDecimal unitPriceProfit;
    private long formalEntrustId;
    private String userName;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getFreezed() {
        return freezed;
    }

    public void setFreezed(long freezed) {
        this.freezed = freezed;
    }

    public void setEntrustId(long entrustId) {
        this.entrustId = entrustId;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setNumbers(BigDecimal numbers) {
        this.numbers = numbers;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public void setCompleteNumber(BigDecimal completeNumber) {
        this.completeNumber = completeNumber;
    }

    public void setCompleteTotalMoney(BigDecimal completeTotalMoney) {
        this.completeTotalMoney = completeTotalMoney;
    }

    public void setSumToWeb(int sumToWeb) {
        this.sumToWeb = sumToWeb;
    }

    public void setWebId(int webId) {
        this.webId = webId;
    }

    public void setTypes(int types) {
        this.types = types;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setSubmitTime(long submitTime) {
        this.submitTime = submitTime;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    public void setTriggerPrice(BigDecimal triggerPrice) {
        this.triggerPrice = triggerPrice;
    }

    public void setTriggerPriceProfit(BigDecimal triggerPriceProfit) {
        this.triggerPriceProfit = triggerPriceProfit;
    }

    public void setUnitPriceProfit(BigDecimal unitPriceProfit) {
        this.unitPriceProfit = unitPriceProfit;
    }

    public void setFormalEntrustId(long formalEntrustId) {
        this.formalEntrustId = formalEntrustId;
    }

    public long getEntrustId() {
        return entrustId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getNumbers() {
        return numbers;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public BigDecimal getCompleteNumber() {
        return completeNumber;
    }

    public BigDecimal getCompleteTotalMoney() {
        return completeTotalMoney;
    }

    public int getSumToWeb() {
        return sumToWeb;
    }

    public int getWebId() {
        return webId;
    }

    public int getTypes() {
        return types;
    }

    public int getUserId() {
        return userId;
    }

    public int getStatus() {
        return status;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public BigDecimal getTriggerPrice() {
        return triggerPrice;
    }

    public BigDecimal getTriggerPriceProfit() {
        return triggerPriceProfit;
    }

    public BigDecimal getUnitPriceProfit() {
        return unitPriceProfit;
    }

    public long getFormalEntrustId() {
        return formalEntrustId;
    }
}

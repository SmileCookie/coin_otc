package com.world.model.entity.backcapital;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

/**
 * @author buxianguan
 * @date 2017/11/15
 */
public class BcEntrustTransRecord extends Bean {
    private long id;
    private long batchId;
    private long entrustId;
    private BigDecimal completeTotalMoney;
    private BigDecimal completeNumber;
    private long entrustTime;
    private long transRecordId;
    private long userId;
    private String market;
    /**
     * 手续费金额，折算成USDT后
     */
    private BigDecimal amount;
    private BigDecimal feeRatio;
    /**
     * 手续费原始金额
     */
    private BigDecimal originAmount;
    /**
     * btc折算usdt的价格
     */
    private BigDecimal btcUsdtPrice;
    /**
     * 手续费类型(BTC、USDT)
     */
    private String currency;
    /**
     * 手续费百分比
     */
    private BigDecimal feePercent;
    private long transRecordTime;
    private BigDecimal unitPrice;
    private BigDecimal numbers;
    private BigDecimal totalPrice;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBatchId() {
        return batchId;
    }

    public void setBatchId(long batchId) {
        this.batchId = batchId;
    }

    public long getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(long entrustId) {
        this.entrustId = entrustId;
    }

    public BigDecimal getCompleteTotalMoney() {
        return completeTotalMoney;
    }

    public void setCompleteTotalMoney(BigDecimal completeTotalMoney) {
        this.completeTotalMoney = completeTotalMoney;
    }

    public BigDecimal getCompleteNumber() {
        return completeNumber;
    }

    public void setCompleteNumber(BigDecimal completeNumber) {
        this.completeNumber = completeNumber;
    }

    public long getEntrustTime() {
        return entrustTime;
    }

    public void setEntrustTime(long entrustTime) {
        this.entrustTime = entrustTime;
    }

    public long getTransRecordId() {
        return transRecordId;
    }

    public void setTransRecordId(long transRecordId) {
        this.transRecordId = transRecordId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFeeRatio() {
        return feeRatio;
    }

    public void setFeeRatio(BigDecimal feeRatio) {
        this.feeRatio = feeRatio;
    }

    public BigDecimal getOriginAmount() {
        return originAmount;
    }

    public void setOriginAmount(BigDecimal originAmount) {
        this.originAmount = originAmount;
    }

    public BigDecimal getBtcUsdtPrice() {
        return btcUsdtPrice;
    }

    public void setBtcUsdtPrice(BigDecimal btcUsdtPrice) {
        this.btcUsdtPrice = btcUsdtPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getFeePercent() {
        return feePercent;
    }

    public void setFeePercent(BigDecimal feePercent) {
        this.feePercent = feePercent;
    }

    public long getTransRecordTime() {
        return transRecordTime;
    }

    public void setTransRecordTime(long transRecordTime) {
        this.transRecordTime = transRecordTime;
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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}

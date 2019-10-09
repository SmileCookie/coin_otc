package com.world.model.service;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 虚拟币充值、提币明细
 * Created by micheal on 2016/10/25.
 */
public class CoinChangeDetails extends Bean {

    private long recordId;
    /**金额*/
    private BigDecimal amount;

    private BigDecimal fees;
    /**处理时间*/
    private Timestamp changeTime;
    /**商户平台同步ID*/
    private long merchantsSyncId;

    private String merchantOrderNo;

    public CoinChangeDetails() {

    }

    public CoinChangeDetails(long recordId, BigDecimal amount, Timestamp changeTime, long merchantsSyncId) {
        this.recordId = recordId;
        this.amount = amount;
        this.changeTime = changeTime;
        this.merchantsSyncId = merchantsSyncId;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public Timestamp getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Timestamp changeTime) {
        this.changeTime = changeTime;
    }

    public long getMerchantsSyncId() {
        return merchantsSyncId;
    }

    public void setMerchantsSyncId(long merchantsSyncId) {
        this.merchantsSyncId = merchantsSyncId;
    }

    public String getMerchantOrderNo() {
        return merchantOrderNo;
    }

    public void setMerchantOrderNo(String merchantOrderNo) {
        this.merchantOrderNo = merchantOrderNo;
    }
}

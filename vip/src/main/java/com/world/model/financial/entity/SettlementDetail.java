package com.world.model.financial.entity;

import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.LongIdEntity;
import java.sql.Timestamp;
/**
 * 与商户平台对账明细
 */
@Entity(noClassnameStored=true)
public class SettlementDetail extends LongIdEntity {

    private String merchantOrderNo;
    private long tradId;
    private long recordId;
    private int isIn;
    private boolean isCnyChargeByRemittance;
    private double mMoney;
    private double money;
    private Timestamp date;
    private Timestamp mdate;
    private int noticeStatus;
    private int status;
    private double mFees;
    private double fees;
    private String currency;
    private int unusually;

    public String getMerchantOrderNo() {
        return merchantOrderNo;
    }

    public void setMerchantOrderNo(String merchantOrderNo) {
        this.merchantOrderNo = merchantOrderNo;
    }

    public long getTradId() {
        return tradId;
    }

    public void setTradId(long tradId) {
        this.tradId = tradId;
    }

    public int getIsIn() {
        return isIn;
    }

    public void setIsIn(int isIn) {
        this.isIn = isIn;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public int getNoticeStatus() {
        return noticeStatus;
    }

    public void setNoticeStatus(int noticeStatus) {
        this.noticeStatus = noticeStatus;
    }

    public double getFees() {
        return fees;
    }

    public void setFees(double fees) {
        this.fees = fees;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getmMoney() {
        return mMoney;
    }

    public void setmMoney(double mMoney) {
        this.mMoney = mMoney;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getmFees() {
        return mFees;
    }

    public void setmFees(double mFees) {
        this.mFees = mFees;
    }

    public int getUnusually() {
        return unusually;
    }

    public void setUnusually(int unusually) {
        this.unusually = unusually;
    }

    public Timestamp getMdate() {
        return mdate;
    }

    public void setMdate(Timestamp mdate) {
        this.mdate = mdate;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public boolean isCnyChargeByRemittance() {
        return isCnyChargeByRemittance;
    }

    public void setCnyChargeByRemittance(boolean cnyChargeByRemittance) {
        isCnyChargeByRemittance = cnyChargeByRemittance;
    }

    public void compareMoneyIsUnusually(){
        this.setUnusually(this.getMoney() == this.getmMoney() &&
                this.getDate() != null &&
                this.getMdate() != null &&
                this.getDate().compareTo(this.getMdate()) == 0 ? 0 : 1);
    }
}

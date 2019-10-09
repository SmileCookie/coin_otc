package com.world.model.entity.statisticalReport;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

public class AliveUserDataCountVo extends Bean {
    private int id;
    private Date countDate;
    private int allDepositUserCount;
    private int depositUserCount;
    private int firstDepositCount;
    private int cashInCount;
    private int cashNullCount;
    private int cashNullNoLoginCount;
    private int transactionCount;
    private BigDecimal transactionFee;
    private BigDecimal userTransactionFeeUsdt;
    private BigDecimal companyTransactionFeeUsdt;
    private BigDecimal userTransactionFeeBtc;
    private BigDecimal companyTransactionFeeBtc;
    private String rechargeConversionRate;
    private String userId;
    private String churnRate;

    public String getChurnRate() {
        return churnRate;
    }

    public void setChurnRate(String churnRate) {
        this.churnRate = churnRate;
    }

    public BigDecimal getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(BigDecimal transactionFee) {
        this.transactionFee = transactionFee;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCountDate() {
        return countDate;
    }

    public void setCountDate(Date countDate) {
        this.countDate = countDate;
    }

    public int getAllDepositUserCount() {
        return allDepositUserCount;
    }

    public void setAllDepositUserCount(int allDepositUserCount) {
        this.allDepositUserCount = allDepositUserCount;
    }

    public int getDepositUserCount() {
        return depositUserCount;
    }

    public void setDepositUserCount(int depositUserCount) {
        this.depositUserCount = depositUserCount;
    }

    public int getFirstDepositCount() {
        return firstDepositCount;
    }

    public void setFirstDepositCount(int firstDepositCount) {
        this.firstDepositCount = firstDepositCount;
    }

    public int getCashInCount() {
        return cashInCount;
    }

    public void setCashInCount(int cashInCount) {
        this.cashInCount = cashInCount;
    }

    public int getCashNullCount() {
        return cashNullCount;
    }

    public void setCashNullCount(int cashNullCount) {
        this.cashNullCount = cashNullCount;
    }

    public int getCashNullNoLoginCount() {
        return cashNullNoLoginCount;
    }

    public void setCashNullNoLoginCount(int cashNullNoLoginCount) {
        this.cashNullNoLoginCount = cashNullNoLoginCount;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public BigDecimal getUserTransactionFeeUsdt() {
        return userTransactionFeeUsdt;
    }

    public void setUserTransactionFeeUsdt(BigDecimal userTransactionFeeUsdt) {
        this.userTransactionFeeUsdt = userTransactionFeeUsdt;
    }

    public BigDecimal getCompanyTransactionFeeUsdt() {
        return companyTransactionFeeUsdt;
    }

    public void setCompanyTransactionFeeUsdt(BigDecimal companyTransactionFeeUsdt) {
        this.companyTransactionFeeUsdt = companyTransactionFeeUsdt;
    }

    public BigDecimal getUserTransactionFeeBtc() {
        return userTransactionFeeBtc;
    }

    public void setUserTransactionFeeBtc(BigDecimal userTransactionFeeBtc) {
        this.userTransactionFeeBtc = userTransactionFeeBtc;
    }

    public BigDecimal getCompanyTransactionFeeBtc() {
        return companyTransactionFeeBtc;
    }

    public void setCompanyTransactionFeeBtc(BigDecimal companyTransactionFeeBtc) {
        this.companyTransactionFeeBtc = companyTransactionFeeBtc;
    }

    public String getRechargeConversionRate() {
        return rechargeConversionRate;
    }

    public void setRechargeConversionRate(String rechargeConversionRate) {
        this.rechargeConversionRate = rechargeConversionRate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

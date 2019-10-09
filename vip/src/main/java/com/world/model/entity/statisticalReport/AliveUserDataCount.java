package com.world.model.entity.statisticalReport;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

public class AliveUserDataCount extends Bean {
    private int id;
    private Date countDate;
    private Long allDepositUserCount;
    private Long depositUserCount;
    private Long firstDepositCount;
    private Long cashInCount;
    private Long cashNullCount;
    private Long cashNullNoLoginCount;
    private Long transactionCount;
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

    public Long getAllDepositUserCount() {
        return allDepositUserCount;
    }

    public void setAllDepositUserCount(Long allDepositUserCount) {
        this.allDepositUserCount = allDepositUserCount;
    }

    public Long getDepositUserCount() {
        return depositUserCount;
    }

    public void setDepositUserCount(Long depositUserCount) {
        this.depositUserCount = depositUserCount;
    }

    public Long getFirstDepositCount() {
        return firstDepositCount;
    }

    public void setFirstDepositCount(Long firstDepositCount) {
        this.firstDepositCount = firstDepositCount;
    }

    public Long getCashInCount() {
        return cashInCount;
    }

    public void setCashInCount(Long cashInCount) {
        this.cashInCount = cashInCount;
    }

    public Long getCashNullCount() {
        return cashNullCount;
    }

    public void setCashNullCount(Long cashNullCount) {
        this.cashNullCount = cashNullCount;
    }

    public Long getCashNullNoLoginCount() {
        return cashNullNoLoginCount;
    }

    public void setCashNullNoLoginCount(Long cashNullNoLoginCount) {
        this.cashNullNoLoginCount = cashNullNoLoginCount;
    }

    public Long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Long transactionCount) {
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

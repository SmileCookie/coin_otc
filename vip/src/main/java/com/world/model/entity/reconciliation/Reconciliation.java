package com.world.model.entity.reconciliation;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

public class Reconciliation extends Bean {
    private int id;
    private int fundsType;
    private String fundsTypeName;
    private BigDecimal recharge;
    private BigDecimal withdraw;
    private BigDecimal sysRecharge;
    private BigDecimal sysDeduction;
    private BigDecimal sysSort;
    private BigDecimal icoExchange;
    private BigDecimal sell;
    private BigDecimal buy;
    private BigDecimal transactionFee;
    private BigDecimal withdrawFee;
    private BigDecimal bookBalance;
    private Date reportDate;
    private BigDecimal internalAdjustmentPositive;
    private BigDecimal internalAdjustmentNegative;
    private BigDecimal externalAdjustmentPositive;
    private BigDecimal externalAdjustmentNegative;

    private BigDecimal backCapital;

    private BigDecimal backCapitalFail;

    private BigDecimal luckDrawCapital;

    public BigDecimal getLuckDrawCapital() {
        return luckDrawCapital;
    }

    public void setLuckDrawCapital(BigDecimal luckDrawCapital) {
        this.luckDrawCapital = luckDrawCapital;
    }

    public BigDecimal getBackCapital() {
        return backCapital;
    }

    public void setBackCapital(BigDecimal backCapital) {
        this.backCapital = backCapital;
    }

    public BigDecimal getInternalAdjustmentPositive() {
        return internalAdjustmentPositive;
    }

    public void setInternalAdjustmentPositive(BigDecimal internalAdjustmentPositive) {
        this.internalAdjustmentPositive = internalAdjustmentPositive;
    }

    public BigDecimal getInternalAdjustmentNegative() {
        return internalAdjustmentNegative;
    }

    public void setInternalAdjustmentNegative(BigDecimal internalAdjustmentNegative) {
        this.internalAdjustmentNegative = internalAdjustmentNegative;
    }

    public BigDecimal getExternalAdjustmentPositive() {
        return externalAdjustmentPositive;
    }

    public void setExternalAdjustmentPositive(BigDecimal externalAdjustmentPositive) {
        this.externalAdjustmentPositive = externalAdjustmentPositive;
    }

    public BigDecimal getExternalAdjustmentNegative() {
        return externalAdjustmentNegative;
    }

    public void setExternalAdjustmentNegative(BigDecimal externalAdjustmentNegative) {
        this.externalAdjustmentNegative = externalAdjustmentNegative;
    }

    public BigDecimal getBackCapitalFail() {
        return backCapitalFail;
    }

    public void setBackCapitalFail(BigDecimal backCapitalFail) {
        this.backCapitalFail = backCapitalFail;
    }

    public BigDecimal getSysSort() {
        return sysSort;
    }

    public void setSysSort(BigDecimal sysSort) {
        this.sysSort = sysSort;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFundsType() {
        return fundsType;
    }

    public void setFundsType(int fundsType) {
        this.fundsType = fundsType;
    }

    public String getFundsTypeName() {
        return fundsTypeName;
    }

    public void setFundsTypeName(String fundsTypeName) {
        this.fundsTypeName = fundsTypeName;
    }

    public BigDecimal getRecharge() {
        return recharge;
    }

    public void setRecharge(BigDecimal recharge) {
        this.recharge = recharge;
    }

    public BigDecimal getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(BigDecimal withdraw) {
        this.withdraw = withdraw;
    }

    public BigDecimal getSysRecharge() {
        return sysRecharge;
    }

    public void setSysRecharge(BigDecimal sysRecharge) {
        this.sysRecharge = sysRecharge;
    }

    public BigDecimal getSysDeduction() {
        return sysDeduction;
    }

    public void setSysDeduction(BigDecimal sysDeduction) {
        this.sysDeduction = sysDeduction;
    }

    public BigDecimal getIcoExchange() {
        return icoExchange;
    }

    public void setIcoExchange(BigDecimal icoExchange) {
        this.icoExchange = icoExchange;
    }

    public BigDecimal getSell() {
        return sell;
    }

    public void setSell(BigDecimal sell) {
        this.sell = sell;
    }

    public BigDecimal getBuy() {
        return buy;
    }

    public void setBuy(BigDecimal buy) {
        this.buy = buy;
    }

    public BigDecimal getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(BigDecimal transactionFee) {
        this.transactionFee = transactionFee;
    }

    public BigDecimal getWithdrawFee() {
        return withdrawFee;
    }

    public void setWithdrawFee(BigDecimal withdrawFee) {
        this.withdrawFee = withdrawFee;
    }

    public BigDecimal getBookBalance() {
        return bookBalance;
    }

    public void setBookBalance(BigDecimal bookBalance) {
        this.bookBalance = bookBalance;
    }
}

package com.world.model.entity.statisticalReport;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

public class BillAllCountVo extends Bean {
    private int coinType;
    private String userType;
    private BigDecimal userDeposit;
    private BigDecimal userCashIn;
    private BigDecimal companyDeposit;
    private BigDecimal companyCashIn;
    private BigDecimal transactionFee;
    private BigDecimal cashInFee;
    private BigDecimal deposit;
    private BigDecimal cashIn;
    private BigDecimal userRetainedFee;
    private BigDecimal companyRetainedFee;
    private int positionCount;
    private BigDecimal transactionFeeUser;
    private BigDecimal transactionFeeCompany;

    public BigDecimal getTransactionFeeUser() {
        return transactionFeeUser;
    }

    public void setTransactionFeeUser(BigDecimal transactionFeeUser) {
        this.transactionFeeUser = transactionFeeUser;
    }

    public BigDecimal getTransactionFeeCompany() {
        return transactionFeeCompany;
    }

    public void setTransactionFeeCompany(BigDecimal transactionFeeCompany) {
        this.transactionFeeCompany = transactionFeeCompany;
    }

    public int getPositionCount() {
        return positionCount;
    }

    public void setPositionCount(int positionCount) {
        this.positionCount = positionCount;
    }

    public BigDecimal getUserRetainedFee() {
        return userRetainedFee;
    }

    public void setUserRetainedFee(BigDecimal userRetainedFee) {
        this.userRetainedFee = userRetainedFee;
    }

    public BigDecimal getCompanyRetainedFee() {
        return companyRetainedFee;
    }

    public void setCompanyRetainedFee(BigDecimal companyRetainedFee) {
        this.companyRetainedFee = companyRetainedFee;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getCashIn() {
        return cashIn;
    }

    public void setCashIn(BigDecimal cashIn) {
        this.cashIn = cashIn;
    }

    private Date countDate;

    private String coinName;

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public int getCoinType() {
        return coinType;
    }

    public void setCoinType(int coinType) {
        this.coinType = coinType;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public BigDecimal getUserDeposit() {
        return userDeposit;
    }

    public void setUserDeposit(BigDecimal userDeposit) {
        this.userDeposit = userDeposit;
    }

    public BigDecimal getUserCashIn() {
        return userCashIn;
    }

    public void setUserCashIn(BigDecimal userCashIn) {
        this.userCashIn = userCashIn;
    }

    public BigDecimal getCompanyDeposit() {
        return companyDeposit;
    }

    public void setCompanyDeposit(BigDecimal companyDeposit) {
        this.companyDeposit = companyDeposit;
    }

    public BigDecimal getCompanyCashIn() {
        return companyCashIn;
    }

    public void setCompanyCashIn(BigDecimal companyCashIn) {
        this.companyCashIn = companyCashIn;
    }

    public BigDecimal getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(BigDecimal transactionFee) {
        this.transactionFee = transactionFee;
    }

    public BigDecimal getCashInFee() {
        return cashInFee;
    }

    public void setCashInFee(BigDecimal cashInFee) {
        this.cashInFee = cashInFee;
    }

    public Date getCountDate() {
        return countDate;
    }

    public void setCountDate(Date countDate) {
        this.countDate = countDate;
    }


}

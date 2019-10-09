package com.world.model.entity.statisticalReport;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class BillAllCount extends Bean {
    private int coinType;
    private BigInteger coinTypeId;
    private String userType;
    private BigDecimal userDeposit = BigDecimal.ZERO;
    private BigDecimal userCashIn = BigDecimal.ZERO;
    private BigDecimal companyDeposit = BigDecimal.ZERO;
    private BigDecimal companyCashIn = BigDecimal.ZERO;
    private BigDecimal transactionFee = BigDecimal.ZERO;
    private BigDecimal cashInFee = BigDecimal.ZERO;
    private BigDecimal deposit = BigDecimal.ZERO;
    private BigDecimal cashIn = BigDecimal.ZERO;
    private BigDecimal userRetainedFee = BigDecimal.ZERO;//用户账面留存资金-币币
    private BigDecimal userRetainedFeeOtc = BigDecimal.ZERO;//用户账面留存资金-OTC
    private BigDecimal userRetainedFeeWallet = BigDecimal.ZERO;//用户账面留存资金-Wallet
    private BigDecimal userRetainedFeeSum = BigDecimal.ZERO;//用户账面留存资金-各个账户合计
    private BigDecimal companyRetainedFee = BigDecimal.ZERO;//公司账面留存资金-币币
    private BigDecimal companyRetainedFeeOtc = BigDecimal.ZERO;//公司账面留存资金-OTC
    private BigDecimal companyRetainedFeeWallet = BigDecimal.ZERO;//公司账面留存资金-Wallet
    private BigDecimal companyRetainedFeeSum = BigDecimal.ZERO;//公司账面留存资金-各个账户合计
    private Long positionCount;
    private BigDecimal transactionFeeUser = BigDecimal.ZERO;
    private BigDecimal transactionFeeCompany = BigDecimal.ZERO;

    private BigDecimal backCapitalUserGbc = BigDecimal.ZERO;
    private BigDecimal backCapitalCompanyGbc = BigDecimal.ZERO;
    private BigDecimal backCapitalUserUsdt = BigDecimal.ZERO;
    private BigDecimal backCapitalCompanyUsdt = BigDecimal.ZERO;

    private Date countDate;

    private String coinName;

    public BigDecimal getBackCapitalUserGbc() {
        return backCapitalUserGbc;
    }

    public void setBackCapitalUserGbc(BigDecimal backCapitalUserGbc) {
        this.backCapitalUserGbc = backCapitalUserGbc;
    }

    public BigDecimal getBackCapitalCompanyGbc() {
        return backCapitalCompanyGbc;
    }

    public void setBackCapitalCompanyGbc(BigDecimal backCapitalCompanyGbc) {
        this.backCapitalCompanyGbc = backCapitalCompanyGbc;
    }

    public BigDecimal getBackCapitalUserUsdt() {
        return backCapitalUserUsdt;
    }

    public void setBackCapitalUserUsdt(BigDecimal backCapitalUserUsdt) {
        this.backCapitalUserUsdt = backCapitalUserUsdt;
    }

    public BigDecimal getBackCapitalCompanyUsdt() {
        return backCapitalCompanyUsdt;
    }

    public void setBackCapitalCompanyUsdt(BigDecimal backCapitalCompanyUsdt) {
        this.backCapitalCompanyUsdt = backCapitalCompanyUsdt;
    }

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

    public Long getPositionCount() {
        return positionCount;
    }

    public void setPositionCount(Long positionCount) {
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

    public BigDecimal getUserRetainedFeeOtc() {
        return userRetainedFeeOtc;
    }

    public void setUserRetainedFeeOtc(BigDecimal userRetainedFeeOtc) {
        this.userRetainedFeeOtc = userRetainedFeeOtc;
    }

    public BigDecimal getCompanyRetainedFeeOtc() {
        return companyRetainedFeeOtc;
    }

    public void setCompanyRetainedFeeOtc(BigDecimal companyRetainedFeeOtc) {
        this.companyRetainedFeeOtc = companyRetainedFeeOtc;
    }

    public BigDecimal getUserRetainedFeeWallet() {
        return userRetainedFeeWallet;
    }

    public void setUserRetainedFeeWallet(BigDecimal userRetainedFeeWallet) {
        this.userRetainedFeeWallet = userRetainedFeeWallet;
    }

    public BigDecimal getUserRetainedFeeSum() {
        return userRetainedFeeSum;
    }

    public void setUserRetainedFeeSum(BigDecimal userRetainedFeeSum) {
        this.userRetainedFeeSum = userRetainedFeeSum;
    }

    public BigDecimal getCompanyRetainedFeeWallet() {
        return companyRetainedFeeWallet;
    }

    public void setCompanyRetainedFeeWallet(BigDecimal companyRetainedFeeWallet) {
        this.companyRetainedFeeWallet = companyRetainedFeeWallet;
    }

    public BigDecimal getCompanyRetainedFeeSum() {
        return companyRetainedFeeSum;
    }

    public void setCompanyRetainedFeeSum(BigDecimal companyRetainedFeeSum) {
        this.companyRetainedFeeSum = companyRetainedFeeSum;
    }

    public BigInteger getCoinTypeId() {
        return coinTypeId;
    }

    public void setCoinTypeId(BigInteger coinTypeId) {
        this.coinTypeId = coinTypeId;
    }
}

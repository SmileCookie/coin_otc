package com.world.model.entity.mars;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

public class Mars extends Bean {
    private int id;
    private Date createDate;
    private Date transDate;
    private Date checkDate;
    private String summary;
    private int fundsType;
    private BigDecimal income;
    private BigDecimal expense;
    private String changePosition;
    private String changeType;
    private String CompanyChangeType;
    private String accountingType;
    private String operator;
    private String auditior;
    private String comment;
    private int checkState;
    private String fundsTypeName;


    public String getCompanyChangeType() {
        return CompanyChangeType;
    }

    public void setCompanyChangeType(String companyChangeType) {
        CompanyChangeType = companyChangeType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getFundsType() {
        return fundsType;
    }

    public void setFundsType(int fundsType) {
        this.fundsType = fundsType;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getExpense() {
        return expense;
    }

    public void setExpense(BigDecimal expense) {
        this.expense = expense;
    }

    public String getChangePosition() {
        return changePosition;
    }

    public void setChangePosition(String changePosition) {
        this.changePosition = changePosition;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getAccountingType() {
        return accountingType;
    }

    public void setAccountingType(String accountingType) {
        this.accountingType = accountingType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getAuditior() {
        return auditior;
    }

    public void setAuditior(String auditior) {
        this.auditior = auditior;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getCheckState() {
        return checkState;
    }

    public void setCheckState(int checkState) {
        this.checkState = checkState;
    }

    public String getFundsTypeName() {
        return fundsTypeName;
    }

    public void setFundsTypeName(String fundsTypeName) {
        this.fundsTypeName = fundsTypeName;
    }
}

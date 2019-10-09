package com.world.model.entity.financialproift;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author wei
 * @Date 2019-09-26 14:54
 * @Description
 **/
public class FinUsersontransfer extends Bean {
    private static final long serialVersionUID = 1L;
    private int id;
    /*用户编号*/
    private int sonUserId;
    /*用户名*/
    private String sonUserName;
    /*币种类型*/
    private String fundsType;
    /*释放金额（提币金额）*/
    private BigDecimal avaTransferAmount;
    /*释放贡献*/
    private BigDecimal douProfitAmount;
    /*划转类型*/
    private int transferType;
    /*划转名称*/
    private String transferName;
    /*主账号ID*/
    private int parentUserId;
    /*主账号用户名*/
    private String parentUserName;
    /*创建时间*/
    private Date createTime;
    /*备注*/
    private String tmp;

    private String AttributionNum;

    public String getAttributionNum() {
        return AttributionNum;
    }

    public void setAttributionNum(String attributionNum) {
        AttributionNum = attributionNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSonUserId() {
        return sonUserId;
    }

    public void setSonUserId(int sonUserId) {
        this.sonUserId = sonUserId;
    }

    public String getSonUserName() {
        return sonUserName;
    }

    public void setSonUserName(String sonUserName) {
        this.sonUserName = sonUserName;
    }

    public String getFundsType() {
        return fundsType;
    }

    public void setFundsType(String fundsType) {
        this.fundsType = fundsType;
    }

    public BigDecimal getAvaTransferAmount() {
        return avaTransferAmount;
    }

    public void setAvaTransferAmount(BigDecimal avaTransferAmount) {
        this.avaTransferAmount = avaTransferAmount;
    }

    public BigDecimal getDouProfitAmount() {
        return douProfitAmount;
    }

    public void setDouProfitAmount(BigDecimal douProfitAmount) {
        this.douProfitAmount = douProfitAmount;
    }

    public int getTransferType() {
        return transferType;
    }

    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }

    public String getTransferName() {
        return transferName;
    }

    public void setTransferName(String transferName) {
        this.transferName = transferName;
    }

    public int getParentUserId() {
        return parentUserId;
    }

    public void setParentUserId(int parentUserId) {
        this.parentUserId = parentUserId;
    }

    public String getParentUserName() {
        return parentUserName;
    }

    public void setParentUserName(String parentUserName) {
        this.parentUserName = parentUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }
}

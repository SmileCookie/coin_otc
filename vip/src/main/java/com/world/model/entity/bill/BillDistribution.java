package com.world.model.entity.bill;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @Title: 分叉币分发及抽奖分发
 * @Description: BillDistribution_table
 * @Company: atlas
 * @author: xzhang
 */
public class BillDistribution extends Bean {

    private int type;
    private BigDecimal amount;
    private Timestamp sendTime;//时间
    private int fundsType;//资金类型
    private String sourceRemark;//订单信息来源
    private String typeView;//类型国际化
    private String coinView;//币种展示
    private int status;//分发状态：抽奖实名认证前，抽奖获得为冻结金额

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTypeView() {
        return typeView;
    }

    public void setTypeView(String typeView) {
        this.typeView = typeView;
    }

    public String getCoinView() {
        return coinView;
    }

    public void setCoinView(String coinView) {
        this.coinView = coinView;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Timestamp getSendTime() {
        return sendTime;
    }

    public void setSendTime(Timestamp sendTime) {
        this.sendTime = sendTime;
    }

    public int getFundsType() {
        return fundsType;
    }

    public void setFundsType(int fundsType) {
        this.fundsType = fundsType;
    }

    public String getSourceRemark() {
        return sourceRemark;
    }

    public void setSourceRemark(String sourceRemark) {
        this.sourceRemark = sourceRemark;
    }
}

package com.world.model.entity.report;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

/**
 * @ClassName EntrustmentDis
 * @Description
 * @Author kinghao
 * @Date 2018/8/10   18:12
 * @Version 1.0
 * @Description 委托分布
 */

public class EntrustmentDis extends Bean {

    /**
     * 买入百分比
     **/
    private BigDecimal BuyPercentage;

    /**
     * 卖出百分比
     **/
    private BigDecimal SalePercentage;

    /**
     * 币种
     **/
    private int coinType;

    /**
     * 币种名称
     **/
    private String coinTypeName;
    /**
     * 0 流入  1 流出
     **/
    private int type;


    public BigDecimal getBuyPercentage() {
        return BuyPercentage;
    }

    public void setBuyPercentage(BigDecimal buyPercentage) {
        BuyPercentage = buyPercentage;
    }

    public BigDecimal getSalePercentage() {
        return SalePercentage;
    }

    public void setSalePercentage(BigDecimal salePercentage) {
        SalePercentage = salePercentage;
    }

    public int getCoinType() {
        return coinType;
    }

    public void setCoinType(int coinType) {
        this.coinType = coinType;
    }

    public String getCoinTypeName() {
        return coinTypeName;
    }

    public void setCoinTypeName(String coinTypeName) {
        this.coinTypeName = coinTypeName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

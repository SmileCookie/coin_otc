package com.world.model.entity.report;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

/**
 * @ClassName PlatformFunds
 * @Description
 * @Author kinghao
 * @Date 2018/8/7   16:36
 * @Version 1.0
 * @Description
 */

public class PlatformFunds extends Bean {

    private String fundsName;
    private int fundsType;
    private int dealType;
    private BigDecimal txAmount;

    public String getFundsName() {
        return fundsName;
    }

    public void setFundsName(String fundsName) {
        this.fundsName = fundsName;
    }

    public int getFundsType() {
        return fundsType;
    }

    public void setFundsType(int fundsType) {
        this.fundsType = fundsType;
    }

    public int getDealType() {
        return dealType;
    }

    public void setDealType(int dealType) {
        this.dealType = dealType;
    }

    public BigDecimal getTxAmount() {
        return txAmount;
    }

    public void setTxAmount(BigDecimal txAmount) {
        this.txAmount = txAmount;
    }
}

package com.world.model.financial.entity;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

/**
 * @Author Ethan
 * @Date 2019-08-04 14:11
 * @Description
 **/

public class FinSuperNode extends Bean {
    BigDecimal profitSum;
    BigDecimal profitUsdtSum;
    private int sNodeType ;
    private long cnt ;

    public int getsNodeType() {
        return sNodeType;
    }

    public void setsNodeType(int sNodeType) {
        this.sNodeType = sNodeType;
    }

    public long getCnt() {
        return cnt;
    }

    public void setCnt(long cnt) {
        this.cnt = cnt;
    }

    Integer status;

    public BigDecimal getProfitSum() {
        return profitSum;
    }

    public void setProfitSum(BigDecimal profitSum) {
        this.profitSum = profitSum;
    }

    public BigDecimal getProfitUsdtSum() {
        return profitUsdtSum;
    }

    public void setProfitUsdtSum(BigDecimal profitUsdtSum) {
        this.profitUsdtSum = profitUsdtSum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

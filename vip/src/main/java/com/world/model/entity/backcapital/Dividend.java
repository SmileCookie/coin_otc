package com.world.model.entity.backcapital;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/3/12下午4:03
 */
public class Dividend extends Bean {
    private long id;
    private BigDecimal balance;
    private int totalShareCount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public int getTotalShareCount() {
        return totalShareCount;
    }

    public void setTotalShareCount(int totalShareCount) {
        this.totalShareCount = totalShareCount;
    }
}

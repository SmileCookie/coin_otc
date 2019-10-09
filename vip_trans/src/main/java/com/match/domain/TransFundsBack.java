package com.match.domain;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/11/17 3:15 PM
 */
public class TransFundsBack extends Bean {
    private long id;
    private BigDecimal money;
    private long entrustId;
    private int userId;
    private int fundsType;
    private long times;

    public TransFundsBack() {
    }

    public TransFundsBack(BigDecimal money, long entrustId, int userId, int fundsType, long times) {
        this.money = money;
        this.entrustId = entrustId;
        this.userId = userId;
        this.fundsType = fundsType;
        this.times = times;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public long getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(long entrustId) {
        this.entrustId = entrustId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFundsType() {
        return fundsType;
    }

    public void setFundsType(int fundsType) {
        this.fundsType = fundsType;
    }

    public long getTimes() {
        return times;
    }

    public void setTimes(long times) {
        this.times = times;
    }
}

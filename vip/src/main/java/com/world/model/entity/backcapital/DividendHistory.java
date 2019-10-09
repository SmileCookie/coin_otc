package com.world.model.entity.backcapital;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/3/12下午4:03
 */
public class DividendHistory extends Bean {
    private long id;
    private String uniqueKey;
    private int number;
    private BigDecimal amount;
    private int shareCount;
    private long time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

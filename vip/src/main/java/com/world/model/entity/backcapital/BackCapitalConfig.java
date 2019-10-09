package com.world.model.entity.backcapital;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/3/8下午5:18
 */
public class BackCapitalConfig extends Bean {
    private long id;
    private int bcUserId;
    private int bcFrequency;
    private int feeRatio;
    private BigDecimal baseBalance;
    private int luckyUserId;
    private String withdrawFrequency;
    private String withdrawAddress;
    private String webUrl;
    private int bcTaskStatus;
    private int withdrawTaskStatus;
    private Timestamp updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBcUserId() {
        return bcUserId;
    }

    public void setBcUserId(int bcUserId) {
        this.bcUserId = bcUserId;
    }

    public int getBcFrequency() {
        return bcFrequency;
    }

    public void setBcFrequency(int bcFrequency) {
        this.bcFrequency = bcFrequency;
    }

    public int getFeeRatio() {
        return feeRatio;
    }

    public void setFeeRatio(int feeRatio) {
        this.feeRatio = feeRatio;
    }

    public BigDecimal getBaseBalance() {
        return baseBalance;
    }

    public void setBaseBalance(BigDecimal baseBalance) {
        this.baseBalance = baseBalance;
    }

    public int getLuckyUserId() {
        return luckyUserId;
    }

    public void setLuckyUserId(int luckyUserId) {
        this.luckyUserId = luckyUserId;
    }

    public String getWithdrawFrequency() {
        return withdrawFrequency;
    }

    public void setWithdrawFrequency(String withdrawFrequency) {
        this.withdrawFrequency = withdrawFrequency;
    }

    public String getWithdrawAddress() {
        return withdrawAddress;
    }

    public void setWithdrawAddress(String withdrawAddress) {
        this.withdrawAddress = withdrawAddress;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public int getBcTaskStatus() {
        return bcTaskStatus;
    }

    public void setBcTaskStatus(int bcTaskStatus) {
        this.bcTaskStatus = bcTaskStatus;
    }

    public int getWithdrawTaskStatus() {
        return withdrawTaskStatus;
    }

    public void setWithdrawTaskStatus(int withdrawTaskStatus) {
        this.withdrawTaskStatus = withdrawTaskStatus;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}
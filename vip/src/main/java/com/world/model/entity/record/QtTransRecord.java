package com.world.model.entity.record;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

/**
 * 量化成交单信息
 *
 * Created by suxinjie on 2017/8/21.
 */
public class QtTransRecord extends Bean {

    private long id;
    private long transRecordId;
    private long entrustId;
    private BigDecimal entrustPrice;
    private BigDecimal entrustNum;
    private int entrustType;
    private String entrustMarket;
    private long entrustUserId;
    private long entrustQtUserId;
    private int entrustStatus;
    private long addTime;

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public long getTransRecordId() {
        return transRecordId;
    }

    public void setTransRecordId(long transRecordId) {
        this.transRecordId = transRecordId;
    }

    public String getEntrustMarket() {
        return entrustMarket;
    }

    public void setEntrustMarket(String entrustMarket) {
        this.entrustMarket = entrustMarket;
    }

    public BigDecimal getEntrustNum() {
        return entrustNum;
    }

    public void setEntrustNum(BigDecimal entrustNum) {
        this.entrustNum = entrustNum;
    }

    public BigDecimal getEntrustPrice() {
        return entrustPrice;
    }

    public void setEntrustPrice(BigDecimal entrustPrice) {
        this.entrustPrice = entrustPrice;
    }

    public long getEntrustQtUserId() {
        return entrustQtUserId;
    }

    public void setEntrustQtUserId(long entrustQtUserId) {
        this.entrustQtUserId = entrustQtUserId;
    }

    public int getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(int entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

    public int getEntrustType() {
        return entrustType;
    }

    public void setEntrustType(int entrustType) {
        this.entrustType = entrustType;
    }

    public long getEntrustUserId() {
        return entrustUserId;
    }

    public void setEntrustUserId(long entrustUserId) {
        this.entrustUserId = entrustUserId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(long entrustId) {
        this.entrustId = entrustId;
    }
}

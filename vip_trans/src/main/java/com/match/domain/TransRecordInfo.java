package com.match.domain;

import com.world.data.mysql.Bean;
import com.world.model.entitys.record.TransRecord;

import java.math.BigDecimal;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/11/8 3:29 PM
 */
public class TransRecordInfo extends Bean {
    private long transRecordId;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BigDecimal numbers;
    private long entrustIdBuy;
    private int userIdBuy;
    private long entrustIdSell;
    private int userIdSell;
    private long types;
    private long times;
    private long timeMinute;
    private int status;
    private int webIdBuy;
    private int webIdSell;

    private MatchResultEnum matchResult;

    public TransRecordInfo() {
    }

    public TransRecordInfo(BigDecimal unitPrice, BigDecimal totalPrice, BigDecimal numbers, long entrustIdBuy, int userIdBuy,
                           long entrustIdSell, int userIdSell, long types, long times, long timeMinute, int webIdBuy, int webIdSell) {
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.numbers = numbers;
        this.entrustIdBuy = entrustIdBuy;
        this.userIdBuy = userIdBuy;
        this.entrustIdSell = entrustIdSell;
        this.userIdSell = userIdSell;
        this.types = types;
        this.times = times;
        this.timeMinute = timeMinute;
        this.webIdBuy = webIdBuy;
        this.webIdSell = webIdSell;
    }

    public static TransRecord convertToMem(TransRecordInfo transRecordInfo) {
        TransRecord tr = new TransRecord();
        tr.setTransRecordId(transRecordInfo.getTransRecordId());
        tr.setUnitPrice(transRecordInfo.getUnitPrice());
        tr.setTotalPrice(transRecordInfo.getTotalPrice());
        tr.setNumbers(transRecordInfo.getNumbers());
        tr.setEntrustIdBuy(transRecordInfo.getEntrustIdBuy());
        tr.setUserIdBuy(transRecordInfo.getUserIdBuy());
        tr.setEntrustIdSell(transRecordInfo.getEntrustIdSell());
        tr.setUserIdSell(transRecordInfo.getUserIdSell());
        tr.setTypes(transRecordInfo.getTypes());
        tr.setTimes(transRecordInfo.getTimes());
        tr.setTimeMinute(transRecordInfo.getTimeMinute());
        return tr;
    }

    public long getTransRecordId() {
        return transRecordId;
    }

    public void setTransRecordId(long transRecordId) {
        this.transRecordId = transRecordId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getNumbers() {
        return numbers;
    }

    public void setNumbers(BigDecimal numbers) {
        this.numbers = numbers;
    }

    public long getEntrustIdBuy() {
        return entrustIdBuy;
    }

    public void setEntrustIdBuy(long entrustIdBuy) {
        this.entrustIdBuy = entrustIdBuy;
    }

    public int getUserIdBuy() {
        return userIdBuy;
    }

    public void setUserIdBuy(int userIdBuy) {
        this.userIdBuy = userIdBuy;
    }

    public long getEntrustIdSell() {
        return entrustIdSell;
    }

    public void setEntrustIdSell(long entrustIdSell) {
        this.entrustIdSell = entrustIdSell;
    }

    public int getUserIdSell() {
        return userIdSell;
    }

    public void setUserIdSell(int userIdSell) {
        this.userIdSell = userIdSell;
    }

    public long getTypes() {
        return types;
    }

    public void setTypes(long types) {
        this.types = types;
    }

    public long getTimes() {
        return times;
    }

    public void setTimes(long times) {
        this.times = times;
    }

    public long getTimeMinute() {
        return timeMinute;
    }

    public void setTimeMinute(long timeMinute) {
        this.timeMinute = timeMinute;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getWebIdBuy() {
        return webIdBuy;
    }

    public void setWebIdBuy(int webIdBuy) {
        this.webIdBuy = webIdBuy;
    }

    public int getWebIdSell() {
        return webIdSell;
    }

    public void setWebIdSell(int webIdSell) {
        this.webIdSell = webIdSell;
    }

    public MatchResultEnum getMatchResult() {
        return matchResult;
    }

    public void setMatchResult(MatchResultEnum matchResult) {
        this.matchResult = matchResult;
    }
}

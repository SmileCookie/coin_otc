package com.world.model.entity.warnlog;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

/**
 * <p>保值记录异常记录表(笔数遗漏)</p>
 *
 * @author zhangwt
 * @date 2019/1/14 15:24
 */
public class WarnRecordAbnormal extends Bean {

    private long id;
    private String entrustMarket;
    private BigDecimal userNumbers;
    private BigDecimal hedgingNumbers;
    private BigDecimal numbers;
    private Long startTime;
    private Long endTime;
    private Long dateTime;
    private Integer scanningFrequency;
    private Integer scanningType;
    private Long saveTime;
    private Integer state;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEntrustMarket() {
        return entrustMarket;
    }

    public void setEntrustMarket(String entrustMarket) {
        this.entrustMarket = entrustMarket;
    }

    public BigDecimal getUserNumbers() {
        return userNumbers;
    }

    public void setUserNumbers(BigDecimal userNumbers) {
        this.userNumbers = userNumbers;
    }

    public BigDecimal getHedgingNumbers() {
        return hedgingNumbers;
    }

    public void setHedgingNumbers(BigDecimal hedgingNumbers) {
        this.hedgingNumbers = hedgingNumbers;
    }

    public BigDecimal getNumbers() {
        return numbers;
    }

    public void setNumbers(BigDecimal numbers) {
        this.numbers = numbers;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getScanningFrequency() {
        return scanningFrequency;
    }

    public void setScanningFrequency(Integer scanningFrequency) {
        this.scanningFrequency = scanningFrequency;
    }

    public Integer getScanningType() {
        return scanningType;
    }

    public void setScanningType(Integer scanningType) {
        this.scanningType = scanningType;
    }

    public Long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(Long saveTime) {
        this.saveTime = saveTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}

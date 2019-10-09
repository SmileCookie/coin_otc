package com.world.model.entity.report;

import com.world.data.mysql.Bean;

import java.util.Date;

public class OnlineNumPeakConfig extends Bean {

    private Integer id;
    private Integer configType;
    private Integer startHour;
    private Integer endHour;
    private Integer max;
    private Integer min;
    private Integer incr;
    private Date createTime;
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public OnlineNumPeakConfig setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getConfigType() {
        return configType;
    }

    public OnlineNumPeakConfig setConfigType(Integer configType) {
        this.configType = configType;
        return this;
    }

    public Integer getStartHour() {
        return startHour;
    }

    public OnlineNumPeakConfig setStartHour(Integer startHour) {
        this.startHour = startHour;
        return this;
    }

    public Integer getEndHour() {
        return endHour;
    }

    public OnlineNumPeakConfig setEndHour(Integer endHour) {
        this.endHour = endHour;
        return this;
    }

    public Integer getMax() {
        return max;
    }

    public OnlineNumPeakConfig setMax(Integer max) {
        this.max = max;
        return this;
    }

    public Integer getMin() {
        return min;
    }

    public OnlineNumPeakConfig setMin(Integer min) {
        this.min = min;
        return this;
    }

    public Integer getIncr() {
        return incr;
    }

    public OnlineNumPeakConfig setIncr(Integer incr) {
        this.incr = incr;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public OnlineNumPeakConfig setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public OnlineNumPeakConfig setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}

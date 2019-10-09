package com.world.model.entity.report;

import java.util.Date;
import com.world.data.mysql.Bean;

public class OnlineNumConfig extends Bean {

    private Integer id;
    private Integer configType;
    private Integer max;
    private Integer min;
    private Integer incr;
    private Integer curNum;
    private Date createTime;
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public OnlineNumConfig setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getConfigType() {
        return configType;
    }

    public OnlineNumConfig setConfigType(Integer configType) {
        this.configType = configType;
        return this;
    }

    public Integer getMax() {
        return max;
    }

    public OnlineNumConfig setMax(Integer max) {
        this.max = max;
        return this;
    }

    public Integer getMin() {
        return min;
    }

    public OnlineNumConfig setMin(Integer min) {
        this.min = min;
        return this;
    }

    public Integer getIncr() {
        return incr;
    }

    public OnlineNumConfig setIncr(Integer incr) {
        this.incr = incr;
        return this;
    }

    public Integer getCurNum() {
        return curNum;
    }

    public OnlineNumConfig setCurNum(Integer curNum) {
        this.curNum = curNum;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public OnlineNumConfig setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public OnlineNumConfig setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}

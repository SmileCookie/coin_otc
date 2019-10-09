package com.world.model.entity.statisticalReport;

import com.world.data.mysql.Bean;

import java.util.Date;

public class TimerLog extends Bean {

    private int id;
    private Date createTime;
    private String timerName;
    private Date beginTime;
    private Date endTime;
    private int timerDataCount;
    private String timerState;
    private String timerType;
    private String mark;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTimerName() {
        return timerName;
    }

    public void setTimerName(String timerName) {
        this.timerName = timerName;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getTimerDataCount() {
        return timerDataCount;
    }

    public void setTimerDataCount(int timerDataCount) {
        this.timerDataCount = timerDataCount;
    }

    public String getTimerState() {
        return timerState;
    }

    public void setTimerState(String timerState) {
        this.timerState = timerState;
    }

    public String getTimerType() {
        return timerType;
    }

    public void setTimerType(String timerType) {
        this.timerType = timerType;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}

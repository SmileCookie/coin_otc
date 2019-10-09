package com.world.model.entity.financialproift;


import java.math.BigDecimal;
import java.util.Date;

import com.world.data.mysql.Bean;

@SuppressWarnings("serial")
public class FinancialTask extends Bean {

    private long id;
    private String taskName;
    private int taskType;
    private String taskDesc;
    private int taskFlag;
    private Date taskTime;
    private Date distStartTime;
    private Date distEndTime;
    private Date callStartDate;
    private Date callEndDate;
    private int taskIndex;
    private int sumStep;
    private int nowStep;
    private BigDecimal triggerVal;
    private Date handleTime;
    private int taskError;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public int getTaskFlag() {
        return taskFlag;
    }

    public void setTaskFlag(int taskFlag) {
        this.taskFlag = taskFlag;
    }

    public Date getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(Date taskTime) {
        this.taskTime = taskTime;
    }

    public Date getDistStartTime() {
        return distStartTime;
    }

    public void setDistStartTime(Date distStartTime) {
        this.distStartTime = distStartTime;
    }

    public Date getDistEndTime() {
        return distEndTime;
    }

    public void setDistEndTime(Date distEndTime) {
        this.distEndTime = distEndTime;
    }

    public Date getCallStartDate() {
        return callStartDate;
    }

    public void setCallStartDate(Date callStartDate) {
        this.callStartDate = callStartDate;
    }

    public Date getCallEndDate() {
        return callEndDate;
    }

    public void setCallEndDate(Date callEndDate) {
        this.callEndDate = callEndDate;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
    }

    public int getTaskIndex() {
        return taskIndex;
    }

    public void setTaskIndex(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    public int getSumStep() {
        return sumStep;
    }

    public void setSumStep(int sumStep) {
        this.sumStep = sumStep;
    }

    public int getNowStep() {
        return nowStep;
    }

    public void setNowStep(int nowStep) {
        this.nowStep = nowStep;
    }

    public BigDecimal getTriggerVal() {
        return triggerVal;
    }

    public void setTriggerVal(BigDecimal triggerVal) {
        this.triggerVal = triggerVal;
    }

    public int getTaskError() {
        return taskError;
    }

    public void setTaskError(int taskError) {
        this.taskError = taskError;
    }
}
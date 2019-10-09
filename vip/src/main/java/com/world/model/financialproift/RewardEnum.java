package com.world.model.financialproift;

import com.sun.org.apache.regexp.internal.RE;

/**
 * @Author Ethan
 * @Date 2019-07-30 14:05
 **/
public enum RewardEnum {

    w1("NewUserRewardWork","新增用户奖励"),
    w2("SuperNodeProfitWork","超级节点奖励"),
    w12("EcoRewardAssignWork","VDS生态奖励");

    String taskType;
    String taskName;
    RewardEnum(String taskType,String taskName) {
        this.taskType=taskType;
        this.taskName=taskName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

}

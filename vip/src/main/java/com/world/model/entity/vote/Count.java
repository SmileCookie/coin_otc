package com.world.model.entity.vote;

import com.world.data.mysql.Bean;

public class Count extends Bean {
    private int id;                 //统计id
    private String userId;          //用户id
    private String activityId;      //活动id
    private int voteTimes;          //投票次数
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public int getVoteTimes() {
        return voteTimes;
    }

    public void setVoteTimes(int voteTimes) {
        this.voteTimes = voteTimes;
    }
}
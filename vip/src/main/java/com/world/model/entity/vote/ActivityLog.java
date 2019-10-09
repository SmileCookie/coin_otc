package com.world.model.entity.vote;

import com.world.data.mysql.Bean;

import java.util.Date;

public class ActivityLog  extends Bean{
    private int id;                 //投票记录id 主键
    private String activityId;      //投票活动id
    private String userId;          //投票用户id
    private int voteId;             //投票选项id
    private Date voteTime;          //投票时间
    private String voteIp;          //投票ip


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getVoteId() {
        return voteId;
    }

    public void setVoteId(int voteId) {
        this.voteId = voteId;
    }


    public Date getVoteTime() {
        return voteTime;
    }

    public void setVoteTime(Date voteTime) {
        this.voteTime = voteTime;
    }

    public String getVoteIp() {
        return voteIp;
    }

    public void setVoteIp(String voteIp) {
        this.voteIp = voteIp;
    }
}

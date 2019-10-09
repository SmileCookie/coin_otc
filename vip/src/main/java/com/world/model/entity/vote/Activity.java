package com.world.model.entity.vote;

import com.world.data.mysql.Bean;

import java.util.Date;

public class Activity extends Bean {
    private String activityId;                  //活动id  UUID4的生成规则
    private String activityNameJson;            //活动名称json，其中包含cn，hk，en
    private String activityRuleJson;            //活动规则json，其中包含cn，hk，en
    private String activityContentJson;         //活动详情json，其中包含cn，hk，en
    private Date startTime;                     //活动开始时间
    private Date endTime;                       //活动结束时间
    private int activityLimit;                //活动投票次数限制    1只能投一次  2每天投一次
    private String dataBaseName;                //活动币库名称
    private int state;                          //活动状态
    private String createUser;                  //活动创建人
    private Date createTime;                    //活动创建时间
    private int selectCount;                    //活动选项限制
    private String url;                         //活动地址
    private String activityNameSimple;
    private String activityNameTraditional;
    private String activityNameEnglish;
    private String activityRuleSimple;
    private String activityRuleTraditional;
    private String activityRuleEnglish;
    private String activityContentSimple;
    private String activityContentTraditional;
    private String activityContentEnglish;


    public String getActivityNameSimple() {
        return activityNameSimple;
    }

    public void setActivityNameSimple(String activityNameSimple) {
        this.activityNameSimple = activityNameSimple;
    }

    public String getActivityNameTraditional() {
        return activityNameTraditional;
    }

    public void setActivityNameTraditional(String activityNameTraditional) {
        this.activityNameTraditional = activityNameTraditional;
    }

    public String getActivityNameEnglish() {
        return activityNameEnglish;
    }

    public void setActivityNameEnglish(String activityNameEnglish) {
        this.activityNameEnglish = activityNameEnglish;
    }

    public String getActivityRuleSimple() {
        return activityRuleSimple;
    }

    public void setActivityRuleSimple(String activityRuleSimple) {
        this.activityRuleSimple = activityRuleSimple;
    }

    public String getActivityRuleTraditional() {
        return activityRuleTraditional;
    }

    public void setActivityRuleTraditional(String activityRuleTraditional) {
        this.activityRuleTraditional = activityRuleTraditional;
    }

    public String getActivityRuleEnglish() {
        return activityRuleEnglish;
    }

    public void setActivityRuleEnglish(String activityRuleEnglish) {
        this.activityRuleEnglish = activityRuleEnglish;
    }

    public String getActivityContentSimple() {
        return activityContentSimple;
    }

    public void setActivityContentSimple(String activityContentSimple) {
        this.activityContentSimple = activityContentSimple;
    }

    public String getActivityContentTraditional() {
        return activityContentTraditional;
    }

    public void setActivityContentTraditional(String activityContentTraditional) {
        this.activityContentTraditional = activityContentTraditional;
    }

    public String getActivityContentEnglish() {
        return activityContentEnglish;
    }

    public void setActivityContentEnglish(String activityContentEnglish) {
        this.activityContentEnglish = activityContentEnglish;
    }


    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityNameJson() {
        return activityNameJson;
    }

    public void setActivityNameJson(String activityNameJson) {
        this.activityNameJson = activityNameJson;
    }

    public String getActivityRuleJson() {
        return activityRuleJson;
    }

    public void setActivityRuleJson(String activityRuleJson) {
        this.activityRuleJson = activityRuleJson;
    }

    public String getActivityContentJson() {
        return activityContentJson;
    }

    public void setActivityContentJson(String activityContentJson) {
        this.activityContentJson = activityContentJson;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getActivityLimit() {
        return activityLimit;
    }

    public void setActivityLimit(int activityLimit) {
        this.activityLimit = activityLimit;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getSelectCount() {
        return selectCount;
    }

    public void setSelectCount(int selectCount) {
        this.selectCount = selectCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

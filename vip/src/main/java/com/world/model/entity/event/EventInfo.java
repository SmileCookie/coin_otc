package com.world.model.entity.event;

import com.world.constant.Const;
import com.world.data.mysql.Bean;

import java.util.Date;

public class EventInfo extends Bean {

    private String eventId;//活动主键,yyyyMMddHHmmSSsss',
    private String eventTitleJson;//活动标题,含繁简英三种语言',
    private String eventContentJson;//活动描述,含繁简英三种语言',
    private String eventRuleJson;//活动规则描述,含繁简英三种语言',
    private Date startTime;//活动开始时间',
    private Date endTime;//活动结束时间',
    private String status;//'活动状态：01 未开始，02:进行中，03:暂停， 04:结束  05:删除',
    private String createUserName;//活动创建人',
    private String eventType;//活动类型:01',
    private Date updateTime;//更新时间',
    private Date createTime;//创建时间',
    //辅助字段
    private String eventTitleCN;//活动标题,含繁简英三种语言',
    private String eventTitleHK;//活动标题,含繁简英三种语言',
    private String eventTitleEN;//活动标题,含繁简英三种语言',
    private String eventContentCN;//活动标题,含繁简英三种语言',
    private String eventContentHK;//活动标题,含繁简英三种语言',
    private String eventContentEN;//活动标题,含繁简英三种语言',
    private String eventRuleCN;//活动标题,含繁简英三种语言',
    private String eventRuleHK;//活动标题,含繁简英三种语言',
    private String eventRuleEN;//活动标题,含繁简英三种语言',
    private String statusView;
    private String titleOriginal;//原始信息，供账单转换显示

    public String getTitleOriginal() {
        return titleOriginal;
    }

    public void setTitleOriginal(String titleOriginal) {
        this.titleOriginal = titleOriginal;
    }

    public String getStatusView() {
        if (-1==this.getStartTime().compareTo(new Date())&&1==this.getEndTime().compareTo(new Date())
                &&!this.status.equals(Const.EVENT_STATUS_SUSPEND)&&!this.status.equals(Const.EVENT_STATUS_DEL)&&!this.status.equals(Const.EVENT_STATUS_OVER)){
            return Const.EVENT_STATUS.get(Const.EVENT_STATUS_ING);
        }
        if (1==this.getStartTime().compareTo(new Date())&&!this.status.equals(Const.EVENT_STATUS_DEL)){
            return Const.EVENT_STATUS.get(Const.EVENT_STATUS_UNSTART);
        }
        if (-1==this.getEndTime().compareTo(new Date())&&!this.status.equals(Const.EVENT_STATUS_DEL)){
            return Const.EVENT_STATUS.get(Const.EVENT_STATUS_OVER);
        }
        return Const.EVENT_STATUS.get(this.status);
    }

    public String getEventTitleCN() {
        return eventTitleCN;
    }

    public void setEventTitleCN(String eventTitleCN) {
        this.eventTitleCN = eventTitleCN;
    }

    public String getEventTitleHK() {
        return eventTitleHK;
    }

    public void setEventTitleHK(String eventTitleHK) {
        this.eventTitleHK = eventTitleHK;
    }

    public String getEventTitleEN() {
        return eventTitleEN;
    }

    public void setEventTitleEN(String eventTitleEN) {
        this.eventTitleEN = eventTitleEN;
    }

    public String getEventContentCN() {
        return eventContentCN;
    }

    public void setEventContentCN(String eventContentCN) {
        this.eventContentCN = eventContentCN;
    }

    public String getEventContentHK() {
        return eventContentHK;
    }

    public void setEventContentHK(String eventContentHK) {
        this.eventContentHK = eventContentHK;
    }

    public String getEventContentEN() {
        return eventContentEN;
    }

    public void setEventContentEN(String eventContentEN) {
        this.eventContentEN = eventContentEN;
    }

    public String getEventRuleCN() {
        return eventRuleCN;
    }

    public void setEventRuleCN(String eventRuleCN) {
        this.eventRuleCN = eventRuleCN;
    }

    public String getEventRuleHK() {
        return eventRuleHK;
    }

    public void setEventRuleHK(String eventRuleHK) {
        this.eventRuleHK = eventRuleHK;
    }

    public String getEventRuleEN() {
        return eventRuleEN;
    }

    public void setEventRuleEN(String eventRuleEN) {
        this.eventRuleEN = eventRuleEN;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventTitleJson() {
        return eventTitleJson;
    }

    public void setEventTitleJson(String eventTitleJson) {
        this.eventTitleJson = eventTitleJson;
    }

    public String getEventContentJson() {
        return eventContentJson;
    }

    public void setEventContentJson(String eventContentJson) {
        this.eventContentJson = eventContentJson;
    }

    public String getEventRuleJson() {
        return eventRuleJson;
    }

    public void setEventRuleJson(String eventRuleJson) {
        this.eventRuleJson = eventRuleJson;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}

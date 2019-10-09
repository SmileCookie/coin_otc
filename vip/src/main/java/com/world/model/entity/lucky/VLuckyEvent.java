package com.world.model.entity.lucky;

import com.world.data.mysql.Bean;
import com.world.model.entity.event.EventInfo;

/**
 * @Title: 用户抽奖接口展示数据
 * @Description: 用于用户抽奖接口展示数据
 * @Company: atlas
 * @author: xzhang
 */
public class VLuckyEvent{

    private String ViewFlag;//展示哪个页面
    private String isShow;//是否展示
    private String jackpotSize;//奖池已发放
    private String isVoteShow;//是否显示投票
    private String relateEventId;//关联活动Id
    private String userAmount;//用户已领取的奖金
//    private EventInfo eventInfo;
    private String eventId;//活动主键,yyyyMMddHHmmSSsss',
    private String eventTitleJson;//活动标题,含繁简英三种语言',
    private String eventContentJson;//活动描述,含繁简英三种语言',
    private String eventRuleJson;//活动规则描述,含繁简英三种语言',
    private String token;
    private String chance;//抽奖机会

    public String getChance() {
        return chance;
    }

    public void setChance(String chance) {
        this.chance = chance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getUserAmount() {
        return userAmount;
    }

    public void setUserAmount(String userAmount) {
        this.userAmount = userAmount;
    }

    public String getRelateEventId() {
        return relateEventId;
    }

    public void setRelateEventId(String relateEventId) {
        this.relateEventId = relateEventId;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }
    public String getJackpotSize() {
        return jackpotSize;
    }

    public void setJackpotSize(String jackpotSize) {
        this.jackpotSize = jackpotSize;
    }
    public String getViewFlag() {
        return ViewFlag;
    }

    public void setViewFlag(String viewFlag) {
        ViewFlag = viewFlag;
    }

//    public EventInfo getEventInfo() {
//        return eventInfo;
//    }
//
//    public void setEventInfo(EventInfo eventInfo) {
//        this.eventInfo = eventInfo;
//    }
    public String getIsVoteShow() {
        return isVoteShow;
    }

    public void setIsVoteShow(String isVoteShow) {
        this.isVoteShow = isVoteShow;
    }
}

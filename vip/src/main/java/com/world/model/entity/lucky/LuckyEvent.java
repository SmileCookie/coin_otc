package com.world.model.entity.lucky;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Title: 抽奖主体信息表
 * @Description: luckyevent_table
 * @Company: atlas
 * @author: xzhang
 */
public class LuckyEvent extends Bean {

    private String luckyId;//主键
    private String eventId;//活动id
    private String cycleLimitType;//'抽奖限制类型：01:每天几次；02活动期间几次；03：其他活动未开始或进行中；04：其他活动已结束',
    private String cycleLimitCount;//抽奖次数：cycleLimitType为01、02时，抽奖次数有值',
    private int limitCount;//本次活动用户总体能抽取多少',
    private String relateEventId;//关联其他活动ID
    private String isHighest;//是否获取最高票数
    private String isHighSyn;//是否同步最高票数
    private Date updateTime;//更新时间',
    private Date createTime;//创建时间',
    private String isDouble;//是否奖金翻倍:01未选择，02已选择
    private String isDoubleSyn;//是否同步奖金翻倍:01未同步，02：已同步



    private String ruleId;//主键,自增长',
    private int radixPoint;//抽奖获得最小小数位',
    private BigDecimal occurAmount;//已被领取金额',
    private Date startTime;//活动开始时间',
    private Date endTime;//活动结束时间',
    private BigDecimal jackpotSize;//奖池大小：抽奖类型为01设置上限时，为总奖池，当为02时则为单一规则中奖个数',
    private String eventTitleJson;//活动标题,含繁简英三种语言',

    public String getEventTitleJson() {
        return eventTitleJson;
    }

    public void setEventTitleJson(String eventTitleJson) {
        this.eventTitleJson = eventTitleJson;
    }

    public BigDecimal getJackpotSize() {
        return jackpotSize;
    }

    public void setJackpotSize(BigDecimal jackpotSize) {
        this.jackpotSize = jackpotSize;
    }

    public String getIsHighSyn() {
        return isHighSyn;
    }

    public void setIsHighSyn(String isHighSyn) {
        this.isHighSyn = isHighSyn;
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

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public BigDecimal getOccurAmount() {
        return occurAmount;
    }

    public void setOccurAmount(BigDecimal occurAmount) {
        this.occurAmount = occurAmount;
    }

    public int getRadixPoint() {
        return radixPoint;
    }

    public void setRadixPoint(int radixPoint) {
        this.radixPoint = radixPoint;
    }

    public String getRelateEventId() {
        return relateEventId;
    }

    public void setRelateEventId(String relateEventId) {
        this.relateEventId = relateEventId;
    }

    public String getIsHighest() {
        return isHighest;
    }

    public void setIsHighest(String isHighest) {
        this.isHighest = isHighest;
    }

    public String getLuckyId() {
        return luckyId;
    }

    public void setLuckyId(String luckyId) {
        this.luckyId = luckyId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getCycleLimitType() {
        return cycleLimitType;
    }

    public void setCycleLimitType(String cycleLimitType) {
        this.cycleLimitType = cycleLimitType;
    }

    public String getCycleLimitCount() {
        return cycleLimitCount;
    }

    public void setCycleLimitCount(String cycleLimitCount) {
        this.cycleLimitCount = cycleLimitCount;
    }

    public int getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(int limitCount) {
        this.limitCount = limitCount;
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

    public String getIsDouble() {
        return isDouble;
    }

    public void setIsDouble(String isDouble) {
        this.isDouble = isDouble;
    }

    public String getIsDoubleSyn() {
        return isDoubleSyn;
    }

    public void setIsDoubleSyn(String isDoubleSyn) {
        this.isDoubleSyn = isDoubleSyn;
    }

}

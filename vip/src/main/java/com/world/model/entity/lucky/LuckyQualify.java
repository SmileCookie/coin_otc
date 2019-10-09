package com.world.model.entity.lucky;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Title: 抽奖资格信息表
 * @Description: luckyqualify_table
 * @Company: atlas
 * @author: xzhang
 */
public class LuckyQualify extends Bean {

    private int qId;//主键,自增长',
    private String luckyId;//'抽奖活动主键',
    private String ruleId;//规则Id',
    private String userId;//规则Id',
    private Date startTime;//资格开始时间',
    private Date endTime;//资格结束时间',
    private BigDecimal occurAmount;//发生额',
    private String isReceive;//是否已领取：01:未领取；02已领取',
    private String source;//来源：01:用户触发；02:刷量',
    private Date updateTime;//更新时间',
    private Date createTime;//创建时间',
    private String isShow;//是否需要弹框提醒：01不需要，02需要，03已提醒

    private String ip;

    private String receiveCount;
    private int radixPoint;//抽奖获得最小小数位',

    private Long userCount;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getUserCount() {
        return userCount;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    public String getReceiveCount() {
        return receiveCount;
    }

    public void setReceiveCount(String receiveCount) {
        this.receiveCount = receiveCount;
    }

    public int getqId() {
        return qId;
    }

    public void setqId(int qId) {
        this.qId = qId;
    }

    public String getLuckyId() {
        return luckyId;
    }

    public void setLuckyId(String luckyId) {
        this.luckyId = luckyId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public BigDecimal getOccurAmount() {
        return occurAmount;
    }

    public void setOccurAmount(BigDecimal occurAmount) {
        this.occurAmount = occurAmount;
    }

    public String getIsReceive() {
        return isReceive;
    }

    public void setIsReceive(String isReceive) {
        this.isReceive = isReceive;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }

    public int getRadixPoint() {
        return radixPoint;
    }

    public void setRadixPoint(int radixPoint) {
        this.radixPoint = radixPoint;
    }
}

package com.world.model.entity.lucky;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Title: 抽奖规则信息表
 * @Description: luckyrule_table
 * @Company: atlas
 * @author: xzhang
 */
public class LuckyRule extends Bean {

    private int ruleId;//主键,自增长',
    private String luckyId;//抽奖活动主键',
    private String ruleType;//抽奖类型：01：设置上限；02组合规则',
    private int radixPoint;//抽奖获得最小小数位',
    private BigDecimal jackpotSize;//奖池大小：抽奖类型为01设置上限时，为总奖池，当为02时则为单一规则中奖个数',
    private BigDecimal startSize;//抽奖开始范围，大于等于',
    private BigDecimal endSize;//抽奖结束范围，小于',
    private BigDecimal hitProbability;//抽奖类型为02组合规则时，不为空',
    private BigDecimal occurAmount;//已被领取金额',
    private BigDecimal occurCount;//已领取次数',
    private String isUse;//是否可用：01:可用；02已失效',
    private Date updateTime;//更新时间',
    private Date createTime;//创建时间',
    private BigDecimal userOccurAmount;//用户发生额',

    public BigDecimal getUserOccurAmount() {
        return userOccurAmount;
    }

    public void setUserOccurAmount(BigDecimal userOccurAmount) {
        this.userOccurAmount = userOccurAmount;
    }

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public String getLuckyId() {
        return luckyId;
    }

    public void setLuckyId(String luckyId) {
        this.luckyId = luckyId;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public int getRadixPoint() {
        return radixPoint;
    }

    public void setRadixPoint(int radixPoint) {
        this.radixPoint = radixPoint;
    }

    public BigDecimal getJackpotSize() {
        return jackpotSize;
    }

    public void setJackpotSize(BigDecimal jackpotSize) {
        this.jackpotSize = jackpotSize;
    }

    public BigDecimal getStartSize() {
        return startSize;
    }

    public void setStartSize(BigDecimal startSize) {
        this.startSize = startSize;
    }

    public BigDecimal getEndSize() {
        return endSize;
    }

    public void setEndSize(BigDecimal endSize) {
        this.endSize = endSize;
    }

    public BigDecimal getHitProbability() {
        return hitProbability;
    }

    public void setHitProbability(BigDecimal hitProbability) {
        this.hitProbability = hitProbability;
    }

    public BigDecimal getOccurAmount() {
        return occurAmount;
    }

    public void setOccurAmount(BigDecimal occurAmount) {
        this.occurAmount = occurAmount;
    }

    public BigDecimal getOccurCount() {
        return occurCount;
    }

    public void setOccurCount(BigDecimal occurCount) {
        this.occurCount = occurCount;
    }

    public String getIsUse() {
        return isUse;
    }

    public void setIsUse(String isUse) {
        this.isUse = isUse;
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

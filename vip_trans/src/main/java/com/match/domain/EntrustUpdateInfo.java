package com.match.domain;

import java.math.BigDecimal;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/11/8 3:26 PM
 */
public class EntrustUpdateInfo {
    private long entrustId;
    private BigDecimal completeNumber;
    private BigDecimal completeTotalMoney;
    private int userId;
    private int status;
    private MatchResultEnum matchResult; //1.取消 2.不能成交 3.能成交

    public long getEntrustId() {
        return entrustId;
    }

    public EntrustUpdateInfo setEntrustId(long entrustId) {
        this.entrustId = entrustId;
        return this;
    }

    public BigDecimal getCompleteNumber() {
        return completeNumber;
    }

    public EntrustUpdateInfo setCompleteNumber(BigDecimal completeNumber) {
        this.completeNumber = completeNumber;
        return this;
    }

    public BigDecimal getCompleteTotalMoney() {
        return completeTotalMoney;
    }

    public EntrustUpdateInfo setCompleteTotalMoney(BigDecimal completeTotalMoney) {
        this.completeTotalMoney = completeTotalMoney;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public EntrustUpdateInfo setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public EntrustUpdateInfo setStatus(int status) {
        this.status = status;
        return this;
    }

    public MatchResultEnum getMatchResult() {
        return matchResult;
    }

    public void setMatchResult(MatchResultEnum matchResult) {
        this.matchResult = matchResult;
    }
}

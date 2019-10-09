package com.match.domain;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/5/24下午8:08
 */
public class Entrust extends Bean {
    //委托id
    private long entrustId;
    //单价
    private BigDecimal unitPrice;
    //数量
    private BigDecimal numbers;
    //总额
    private BigDecimal totalMoney;
    //完成数量
    private BigDecimal completeNumber;
    //完成总额度
    private BigDecimal completeTotalMoney;
    //归结到哪个web
    private int sumToWeb;
    //主web  8网页   5 app    6 api
    private int webId;
    //类型  0 卖出 1 购买  -1 取消
    private int types;
    //用户id
    private int userId;
    //状态 0起始 1取消 2交易成功 3交易一部分
    private int status;
    //冻结id  对于取消记录的是需要取消的id
    private long freezeId;
    //提交时间
    private long submitTime;
    //交易手续费
    private BigDecimal feeRate;
    //用户自带的委托单id
    private String customerOrderId;

    //数据库没有的扩展字段
    private BigDecimal srcNumbers;//原始委托数量
    private int matchTimes;//已经撮合次数

    public Entrust(){

    }

    public Entrust(long entrustId, BigDecimal unitPrice, BigDecimal numbers, BigDecimal totalMoney, int webId,
                   int userId, int types, BigDecimal srcNumbers, BigDecimal completeNumber,
                   BigDecimal completeTotalMoney, long submitTime, int status, BigDecimal feeRate) {
        this.entrustId = entrustId;
        this.unitPrice = unitPrice;
        this.numbers = numbers;
        this.totalMoney = totalMoney;
        this.webId = webId;
        this.userId = userId;
        this.types = types;
        this.srcNumbers = srcNumbers;
        this.completeNumber = completeNumber;
        this.completeTotalMoney = completeTotalMoney;
        this.submitTime = submitTime;
        this.status = status;
        this.feeRate = feeRate;
    }

    public long getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(long entrustId) {
        this.entrustId = entrustId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getNumbers() {
        return numbers;
    }

    public void setNumbers(BigDecimal numbers) {
        this.numbers = numbers;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public BigDecimal getCompleteNumber() {
        return completeNumber;
    }

    public void setCompleteNumber(BigDecimal completeNumber) {
        this.completeNumber = completeNumber;
    }

    public BigDecimal getCompleteTotalMoney() {
        return completeTotalMoney;
    }

    public void setCompleteTotalMoney(BigDecimal completeTotalMoney) {
        this.completeTotalMoney = completeTotalMoney;
    }

    public int getSumToWeb() {
        return sumToWeb;
    }

    public void setSumToWeb(int sumToWeb) {
        this.sumToWeb = sumToWeb;
    }

    public int getWebId() {
        return webId;
    }

    public void setWebId(int webId) {
        this.webId = webId;
    }

    public int getTypes() {
        return types;
    }

    public void setTypes(int types) {
        this.types = types;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getFreezeId() {
        return freezeId;
    }

    public void setFreezeId(long freezeId) {
        this.freezeId = freezeId;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(long submitTime) {
        this.submitTime = submitTime;
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    public String getCustomerOrderId() {
        return customerOrderId;
    }

    public void setCustomerOrderId(String customerOrderId) {
        this.customerOrderId = customerOrderId;
    }

    public BigDecimal getSrcNumbers() {
        return srcNumbers;
    }

    public void setSrcNumbers(BigDecimal srcNumbers) {
        this.srcNumbers = srcNumbers;
    }

    public int getMatchTimes() {
        return matchTimes;
    }

    public void setMatchTimes(int matchTimes) {
        this.matchTimes = matchTimes;
    }
}

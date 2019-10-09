package com.world.model.entity.billreconciliation;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>@Description: </p>
 *
 * @author shujianfang
 * @date 2018/11/17下午4:11
 */
public class Billreconciliation extends Bean {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 币种类型
     */
    private Integer fundstype;

    /**
     * 币种名称
     */
    private String fundstypeName;

    /**
     * 币币账户余额
     */
    private BigDecimal balance;

    /**
     * 用户转出
     */
    private BigDecimal rollout;

    /**
     * 用户转入
     */
    private BigDecimal shiftto;

    /**
     * 交易手续费
     */
    private BigDecimal transactionfee;

    /**
     * 报表日期
     */
    private Date reportdate;

    /**
     * 对账结果 0 ：正常 1： 异常
     */
    private Integer state;

    /**
     * 对账差额
     */
    private BigDecimal difference;


    /**
     * 当日总笔数
     */
    private Integer countsum;

    /**
     * 当日未读总笔数
     */
    private Integer unreadcount;

    /**
     * 最近时间
     */
    private Date recentlytime;

    public Integer getCountsum() {
        return countsum;
    }

    public void setCountsum(Integer countsum) {
        this.countsum = countsum;
    }

    public Integer getUnreadcount() {
        return unreadcount;
    }

    public void setUnreadcount(Integer unreadcount) {
        this.unreadcount = unreadcount;
    }

    public Date getRecentlytime() {
        return recentlytime;
    }

    public void setRecentlytime(Date recentlytime) {
        this.recentlytime = recentlytime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public BigDecimal getDifference() {
        return difference;
    }

    public void setDifference(BigDecimal difference) {
        this.difference = difference;
    }

    public String getFundstypeName() {
        return fundstypeName;
    }

    public void setFundstypeName(String fundstypeName) {
        this.fundstypeName = fundstypeName;
    }

    public Billreconciliation() {


    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFundstype() {
        return fundstype;
    }

    public void setFundstype(Integer fundstype) {
        this.fundstype = fundstype;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getRollout() {
        return rollout;
    }

    public void setRollout(BigDecimal rollout) {
        this.rollout = rollout;
    }

    public BigDecimal getShiftto() {
        return shiftto;
    }

    public void setShiftto(BigDecimal shiftto) {
        this.shiftto = shiftto;
    }

    public BigDecimal getTransactionfee() {
        return transactionfee;
    }

    public void setTransactionfee(BigDecimal transactionfee) {
        this.transactionfee = transactionfee;
    }

    public Date getReportdate() {
        return reportdate;
    }

    public void setReportdate(Date reportdate) {
        this.reportdate = reportdate;
    }
    
}

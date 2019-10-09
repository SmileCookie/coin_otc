package com.world.model.entity.reconciliation;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>@Description: </p>
 *
 * @author shujianfang
 * @date 2018/11/20下午6:40
 */
public class Generalledger extends Bean {

    private Integer id;
    private Integer fundstype;
    private String fundstypeName;
    //活动奖励
    private BigDecimal activityreward;
    //平台钱包账户余额
    private BigDecimal balance;
    //活动翻倍
    private BigDecimal doubleactivity;
    //用户充值
    private BigDecimal recharge;
    //钱包转出
    private BigDecimal rollout;
    //钱包转入
    private BigDecimal shiftto;
    //系统扣除
    private BigDecimal sysdeduction;
    //系统充值
    private BigDecimal sysrecharge;
    //系统分发
    private BigDecimal syssort;
    //用户提现
    private BigDecimal withdraw;
    private Date reportdate;
    // 0 ：正常     1： 异常
    private Integer state;
    //用户提现手续费
    private BigDecimal withdrawfee;

    //用户充值-用户提现
    private BigDecimal balanceVs;

    //外部调账负：904
    private BigDecimal externaladjustmentnegative;
    //外部调账正：903
    private BigDecimal externaladjustmentpositive;
    //内部调账负：902
    private BigDecimal internaladjustmentnegative;
    //内部调账正：901
    private BigDecimal internaladjustmentpositive;


//    private BigDecimal withdrawFee = BigDecimal.ZERO;//提现手续费
//    private BigDecimal internalAdjustmentPositive = BigDecimal.ZERO;//内部调账正
//    private BigDecimal internalAdjustmentNegative = BigDecimal.ZERO;//内部调账负
//    private BigDecimal externalAdjustmentPositive = BigDecimal.ZERO;//外部调账正
//    private BigDecimal externalAdjustmentNegative = BigDecimal.ZERO;//外部调账负

    public BigDecimal getWithdrawfee() {
        return withdrawfee;
    }

    public void setWithdrawfee(BigDecimal withdrawfee) {
        this.withdrawfee = withdrawfee;
    }

    public BigDecimal getExternaladjustmentnegative() {
        return externaladjustmentnegative;
    }

    public void setExternaladjustmentnegative(BigDecimal externaladjustmentnegative) {
        this.externaladjustmentnegative = externaladjustmentnegative;
    }

    public BigDecimal getExternaladjustmentpositive() {
        return externaladjustmentpositive;
    }

    public void setExternaladjustmentpositive(BigDecimal externaladjustmentpositive) {
        this.externaladjustmentpositive = externaladjustmentpositive;
    }

    public BigDecimal getInternaladjustmentnegative() {
        return internaladjustmentnegative;
    }

    public void setInternaladjustmentnegative(BigDecimal internaladjustmentnegative) {
        this.internaladjustmentnegative = internaladjustmentnegative;
    }

    public BigDecimal getInternaladjustmentpositive() {
        return internaladjustmentpositive;
    }

    public void setInternaladjustmentpositive(BigDecimal internaladjustmentpositive) {
        this.internaladjustmentpositive = internaladjustmentpositive;
    }


    public BigDecimal getBalanceVs() {
        return balanceVs;
    }

    public void setBalanceVs(BigDecimal balanceVs) {
        this.balanceVs = balanceVs;
    }

    //对账差额
    private BigDecimal difference;


    //当日总笔数
    private Integer countsum;

    //当日未读总笔数
    private Integer unreadcount;

    //最近时间
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

    public BigDecimal getActivityreward() {
        return activityreward;
    }

    public void setActivityreward(BigDecimal activityreward) {
        this.activityreward = activityreward;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getDoubleactivity() {
        return doubleactivity;
    }

    public void setDoubleactivity(BigDecimal doubleactivity) {
        this.doubleactivity = doubleactivity;
    }

    public BigDecimal getRecharge() {
        return recharge;
    }

    public void setRecharge(BigDecimal recharge) {
        this.recharge = recharge;
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

    public BigDecimal getSysdeduction() {
        return sysdeduction;
    }

    public void setSysdeduction(BigDecimal sysdeduction) {
        this.sysdeduction = sysdeduction;
    }

    public BigDecimal getSysrecharge() {
        return sysrecharge;
    }

    public void setSysrecharge(BigDecimal sysrecharge) {
        this.sysrecharge = sysrecharge;
    }

    public BigDecimal getSyssort() {
        return syssort;
    }

    public void setSyssort(BigDecimal syssort) {
        this.syssort = syssort;
    }

    public BigDecimal getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(BigDecimal withdraw) {
        this.withdraw = withdraw;
    }

    public Date getReportdate() {
        return reportdate;
    }

    public void setReportdate(Date reportdate) {
        this.reportdate = reportdate;
    }
}

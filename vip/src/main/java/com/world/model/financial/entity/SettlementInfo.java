package com.world.model.financial.entity;

import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.LongIdEntity;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.sql.Timestamp;

/***
 * 结算信息
 */
@Entity(noClassnameStored=true)
public class SettlementInfo extends LongIdEntity {

    private static final long serialVersionUID = 3033590607429172806L;
    /**chbtc充值总额*/
    private double totalCharge = 0;
    /**商户平台充值总额*/
    private double totalChargeMer = 0;

    /**提现总额*/
    private double totalWithdraw = 0;
    /**商户平台提现总额*/
    private double totalWithdrawMer = 0;

    /**手续费*/
    private double totalTrade = 0;
    /**手续费*/
    private double totalFees = 0;
    /**系统扣除*/
    private double sysReduce = 0;
    /**系统亏损*/
    private double sysLoss = 0;
    /**财务余额*/
    private double ftotalBalance = 0;
    /**上一次结算余额*/
    private double prevFtotalBalance = 0;
    /**币种*/
    private int coinType;//1 rmb 2 btc 3 ltc 4 eth 5 etc

	/*private long billId;//结算至当前bill
	private int finanentryId;//财务结算id*/

    /**结算开始时间*/
    private Timestamp startTime;
    /**结算结束时间*/
    private Timestamp endTime;

    private String userName;//结算人

    private String status;//0 未知   1 正常  2 异常

    private String memo;//备注

    public double getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(double totalCharge) {
        this.totalCharge = totalCharge;
    }

    public double getTotalWithdraw() {
        return totalWithdraw;
    }

    public void setTotalWithdraw(double totalWithdraw) {
        this.totalWithdraw = totalWithdraw;
    }

    public double getTotalFees() {
        return totalFees;
    }

    public void setTotalFees(double totalFees) {
        this.totalFees = totalFees;
    }

    public double getSysReduce() {
        return sysReduce;
    }

    public void setSysReduce(double sysReduce) {
        this.sysReduce = sysReduce;
    }

    public double getSysLoss() {
        return sysLoss;
    }

    public void setSysLoss(double sysLoss) {
        this.sysLoss = sysLoss;
    }

    public double getFtotalBalance() {
        return ftotalBalance;
    }

    public void setFtotalBalance(double ftotalBalance) {
        this.ftotalBalance = ftotalBalance;
    }

    public double getPrevFtotalBalance() {
        return prevFtotalBalance;
    }

    public void setPrevFtotalBalance(double prevFtotalBalance) {
        this.prevFtotalBalance = prevFtotalBalance;
    }

    public int getCoinType() {
        return coinType;
    }

    public void setCoinType(int coinType) {
        this.coinType = coinType;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public double getTotalTrade() {
        return totalTrade;
    }

    public void setTotalTrade(double totalTrade) {
        this.totalTrade = totalTrade;
    }

    public double getSurplus1(){
        return totalCharge - totalWithdraw + totalFees;
    }

    public double getSurplus2(){
        return ftotalBalance - prevFtotalBalance;
    }

    public double getTotalChargeMer() {
        return totalChargeMer;
    }

    public void setTotalChargeMer(double totalChargeMer) {
        this.totalChargeMer = totalChargeMer;
    }

    public double getTotalWithdrawMer() {
        return totalWithdrawMer;
    }

    public void setTotalWithdrawMer(double totalWithdrawMer) {
        this.totalWithdrawMer = totalWithdrawMer;
    }

    public String getStartTimeStr(){
        if(startTime != null){
            DateFormatUtils.format(startTime, "yyMMddHHmmss");
        }

        return "";
    }

    public String getEndTimeStr(){
        if(endTime != null){
            DateFormatUtils.format(endTime, "yyMMddHHmmss");
        }

        return "";
    }
}

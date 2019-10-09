package com.world.model.entity.pay;

import com.world.data.mysql.Bean;
import com.world.model.entity.coin.CoinProps;

import java.math.BigDecimal;

/**
 * @author Elysion
 * @Description:
 * @date 2018/7/25上午10:17
 */
public class PayUserWalletBean extends Bean {

    private Integer id;
    private int userId;
    private String userName;
    private BigDecimal balance;
    private BigDecimal freez;
    private BigDecimal dayFreeCash;
    private BigDecimal timesCash;
    private BigDecimal dayCash;
    private int fundsType;
    private BigDecimal total;
    private BigDecimal minCash;//最小提现额度

    private CoinProps coint;//当前的资金类型

    public CoinProps getCoint() {
        return coint;
    }
    public void setCoint(CoinProps coint) {
        this.coint = coint;
    }
    private BigDecimal withdrawFreeze;

    private BigDecimal netAssets = BigDecimal.ZERO;//动态变化字段，折合成当前币种的资产

    public BigDecimal getWithdrawFreeze() {
        return withdrawFreeze;
    }

    public void setWithdrawFreeze(BigDecimal withdrawFreeze) {
        this.withdrawFreeze = withdrawFreeze;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    public BigDecimal getTotal(){
        total = balance.add(freez).add(withdrawFreeze);
        return total;
    }

    public BigDecimal getMinCash() {
      /* if(minCash == null || minCash.compareTo(BigDecimal.ZERO) <= 0){
           minCash = coint.getMinCash();
        }*/
        return minCash;
    }
    public void setMinCash(BigDecimal minCash) {
        this.minCash = minCash;
    }

    public BigDecimal getNetAssets() {
        return netAssets;
    }

    public void setNetAssets(BigDecimal netAssets) {
        this.netAssets = netAssets;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getFreez() {
        return freez;
    }

    public void setFreez(BigDecimal freez) {
        this.freez = freez;
    }

    public int getFundsType() {
        return fundsType;
    }

    public void setFundsType(int fundsType) {
        this.fundsType = fundsType;
    }

    public BigDecimal getDayCash() {
       /* if(dayCash == null || dayCash.compareTo(BigDecimal.ZERO) <= 0){
           dayCash = coint.getDayCash();
       }*/
        return dayCash;
    }
    public void setDayCash(BigDecimal dayCash) {
        this.dayCash = dayCash;
    }
    public BigDecimal getTimesCash() {
      /* if(timesCash == null || timesCash.compareTo(BigDecimal.ZERO) <= 0){
            timesCash = coint.getTimesCash();
       }*/
        return timesCash;
    }
    public void setTimesCash(BigDecimal timesCash) {
        this.timesCash = timesCash;
    }
    public BigDecimal getDayFreeCash() {
       /* if(dayFreeCash == null || dayFreeCash.compareTo(BigDecimal.ZERO) <= 0){
            dayFreeCash = coint.getDayFreetrial();
       }*/
        return dayFreeCash;
    }
    public void setDayFreeCash(BigDecimal dayFreeCash) {
        this.dayFreeCash = dayFreeCash;
    }

}

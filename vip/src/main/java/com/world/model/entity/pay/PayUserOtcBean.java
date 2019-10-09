package com.world.model.entity.pay;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Elysion
 * @Description:
 * @date 2018/7/25下午4:22
 */
public class PayUserOtcBean extends Bean {
    private BigInteger id;
    private BigInteger userId;
    private BigDecimal balance;
    private BigInteger coinTypeId;
    private BigDecimal frozenWithdraw;
    private BigDecimal frozenFee;
    private BigDecimal frozenTrade;
    private BigDecimal storeFreez;


    public BigDecimal getStoreFreez() {
        return storeFreez;
    }

    public void setStoreFreez(BigDecimal storeFreez) {
        this.storeFreez = storeFreez;
    }

    private BigDecimal total;

    public BigDecimal getTotal() {
        return balance.add(frozenWithdraw).add(frozenFee).add(frozenTrade);
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigInteger getCoinTypeId() {
        return coinTypeId;
    }

    public void setCoinTypeId(BigInteger coinTypeId) {
        this.coinTypeId = coinTypeId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getFrozenWithdraw() {
        return frozenWithdraw;
    }

    public void setFrozenWithdraw(BigDecimal frozenWithdraw) {
        this.frozenWithdraw = frozenWithdraw;
    }

    public BigDecimal getFrozenFee() {
        return frozenFee;
    }

    public void setFrozenFee(BigDecimal frozenFee) {
        this.frozenFee = frozenFee;
    }

    public BigDecimal getFrozenTrade() {
        return frozenTrade;
    }

    public void setFrozenTrade(BigDecimal frozenTrade) {
        this.frozenTrade = frozenTrade;
    }
}

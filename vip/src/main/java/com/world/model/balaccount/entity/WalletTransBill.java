package com.world.model.balaccount.entity;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class WalletTransBill extends Bean {

    private long id;
    private String walId;
    private String walName;
    private int fundsType;
    private String uuid;
    private String txId;
    private BigDecimal txAmount;
    private BigDecimal fee;
    private int blockHeight;
    private Timestamp addTime;
    private Timestamp configTime;
    private int confirmTimes;
    private BigDecimal walBalance;
    private int dealType;
    private int walType;
    private String baseZoom;
    private Timestamp createTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWalId() {
        return walId;
    }

    public void setWalId(String walId) {
        this.walId = walId;
    }

    public String getWalName() {
        return walName;
    }

    public void setWalName(String walName) {
        this.walName = walName;
    }

    public int getFundsType() {
        return fundsType;
    }

    public void setFundsType(int fundsType) {
        this.fundsType = fundsType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public BigDecimal getTxAmount() {
        return txAmount;
    }

    public void setTxAmount(BigDecimal txAmount) {
        this.txAmount = txAmount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public int getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(int blockHeight) {
        this.blockHeight = blockHeight;
    }

    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }

    public Timestamp getConfigTime() {
        return configTime;
    }

    public void setConfigTime(Timestamp configTime) {
        this.configTime = configTime;
    }

    public int getConfirmTimes() {
        return confirmTimes;
    }

    public void setConfirmTimes(int confirmTimes) {
        this.confirmTimes = confirmTimes;
    }

    public BigDecimal getWalBalance() {
        return walBalance;
    }

    public void setWalBalance(BigDecimal walBalance) {
        this.walBalance = walBalance;
    }

    public int getDealType() {
        return dealType;
    }

    public void setDealType(int dealType) {
        this.dealType = dealType;
    }

    public int getWalType() {
        return walType;
    }

    public void setWalType(int walType) {
        this.walType = walType;
    }

    public String getBaseZoom() {
        return baseZoom;
    }

    public void setBaseZoom(String baseZoom) {
        this.baseZoom = baseZoom;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}

package com.world.model.balaccount.entity;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class WalletTransBillDetails extends Bean {

    /*主键自增长*/
    private long id;
    /*钱包编号*/
    private String walId;
    /*钱包名称*/
    private String walName;
    /*资金类型*/
    private int fundsType;
    /*唯一标识接口对接使用*/
    private String uuid;
    /*交易编号*/
    private String txId;
    /*交易金额*/
    private BigDecimal txAmount;
    /*手续费*/
    private BigDecimal fee;
    /*区块高度*/
    private int blockHeight;
    /*addTime*/
    private Timestamp addTime;
    /*确认时间*/
    private Timestamp configTime;
    /*已经确认的次数*/
    private int confirmTimes;
    /*钱包余额*/
    private BigDecimal walBalance;
    /*交易类型1 充值,2 提现(热提),3 冷钱包到热钱包转账(冷),4 热钱包到冷钱包转账(热冲).*/
    private int dealType;
    /*提现钱包类型1 充值热钱包,2 提现热钱包*/
    private int walType;
    /*倍数*/
    private String baseZoom;
    /*交易编号加上N*/
    private String txIdN;
    /*地址*/
    private String toAddress;
    /*交易N的交易金额*/
    private BigDecimal txNAmount;
    /*创建时间*/
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

    public String getTxIdN() {
        return txIdN;
    }

    public void setTxIdN(String txIdN) {
        this.txIdN = txIdN;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public BigDecimal getTxNAmount() {
        return txNAmount;
    }

    public void setTxNAmount(BigDecimal txNAmount) {
        this.txNAmount = txNAmount;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}


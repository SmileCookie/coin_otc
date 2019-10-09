package com.world.model.balaccount.entity;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.model.entity.coin.CoinProps;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by buxianguan on 17/8/2.
 */
public class FinAccWalletBill extends Bean {
    private static final long serialVersionUID = 1L;

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

    private String walTypeName;
    private String fundsTypeName;
    private String dealTypeName;
    private String strTxAmount;
    private String strFee;
    private String strWalBalance;

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

    public String getWalTypeName() {
        return walTypeName;
    }

    public void setWalTypeName(String walTypeName) {
        this.walTypeName = walTypeName;
    }

    public String getFundsTypeName() {
        CoinProps coinProps = DatabasesUtil.coinProps(this.fundsType);
        return coinProps.getDatabaseKey();
    }

    public void setFundsTypeName(String fundsTypeName) {
        this.fundsTypeName = fundsTypeName;
    }

    public String getDealTypeName() {
        switch (dealType){
            case 1:
                return "充值";
            case 2:
                return "提现(热提)";
            case 3:
                return "冷到热提";
            case 4:
                return "热冲到冷";
            case 5:
                return "其他到热提";
            case 6:
                return "其他到冷";
            case 7:
                return "冷到其他";
            case 8:
                return "热提到其他";
        }
        return "";
    }

    public void setDealTypeName(String dealTypeName) {
        this.dealTypeName = dealTypeName;
    }

    public String getStrTxAmount() {
        return txAmount.toPlainString();
    }

    public void setStrTxAmount(String strTxAmount) {
        this.strTxAmount = strTxAmount;
    }

    public String getStrFee() {
        return fee.toPlainString();
    }

    public void setStrFee(String strFee) {
        this.strFee = strFee;
    }

    public String getStrWalBalance() {
        return walBalance.toPlainString();
    }

    public void setStrWalBalance(String strWalBalance) {
        this.strWalBalance = strWalBalance;
    }
}

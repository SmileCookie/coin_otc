package com.world.model.ico.entity;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by xie on 2017/7/12.
 * ICO兑换数量配置
 */
public class ICOExchangeNumConfig extends Bean {

    private static final long serialVersionUID = 1L;

    private int id;
    private int saleType;	            //发售类型(1配售,2申购)
    private int fundsType;	            //资金类型
    private int exchangeNum;            //1个本币兑换GBC数量
    private Timestamp exchangeStartTime;//兑换开始时间
    private Timestamp exchangeEndTime;  //兑换结束时间
    private int exchangeOpenFlag;       //兑换开启标志(1开启,0不开启)

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSaleType() {
        return saleType;
    }

    public void setSaleType(int saleType) {
        this.saleType = saleType;
    }

    public int getFundsType() {
        return fundsType;
    }

    public void setFundsType(int fundsType) {
        this.fundsType = fundsType;
    }

    public int getExchangeNum() {
        return exchangeNum;
    }

    public void setExchangeNum(int exchangeNum) {
        this.exchangeNum = exchangeNum;
    }

    public Timestamp getExchangeStartTime() {
        return exchangeStartTime;
    }

    public void setExchangeStartTime(Timestamp exchangeStartTime) {
        this.exchangeStartTime = exchangeStartTime;
    }

    public Timestamp getExchangeEndTime() {
        return exchangeEndTime;
    }

    public void setExchangeEndTime(Timestamp exchangeEndTime) {
        this.exchangeEndTime = exchangeEndTime;
    }

    public int getExchangeOpenFlag() {
        return exchangeOpenFlag;
    }

    public void setExchangeOpenFlag(int exchangeOpenFlag) {
        this.exchangeOpenFlag = exchangeOpenFlag;
    }
}

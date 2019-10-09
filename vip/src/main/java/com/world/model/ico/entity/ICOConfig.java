package com.world.model.ico.entity;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by xie on 2017/7/12.
 * ICO主配置表
 */
public class ICOConfig extends Bean {

    private static final long serialVersionUID = 1L;

    private int id;                         //主键
    private int saleType;                   //发售类型1配售,2申购
    private int eachUserConfigNum;   //每个用户分配的额度
    private int saleTotalNum;        //发售总数量,申购时使用
    private int saleExchangeNum;     //已兑换数量,申购时使用
    private int importNum;           //上一轮导入的数量
    private Timestamp exchangeStartTime;    //兑换开始时间
    private Timestamp exchangeEndTime;      //兑换结束时间
    private int exchangeState;              //是否开启兑换标志
    private int disPlayFlag;                //前端是否显示
    private int beforeN;                    //前N名（配售使用）

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

    public int getEachUserConfigNum() {
        return eachUserConfigNum;
    }

    public void setEachUserConfigNum(int eachUserConfigNum) {
        this.eachUserConfigNum = eachUserConfigNum;
    }

    public int getSaleTotalNum() {
        return saleTotalNum;
    }

    public void setSaleTotalNum(int saleTotalNum) {
        this.saleTotalNum = saleTotalNum;
    }

    public int getSaleExchangeNum() {
        return saleExchangeNum;
    }

    public void setSaleExchangeNum(int saleExchangeNum) {
        this.saleExchangeNum = saleExchangeNum;
    }

    public int getImportNum() {
        return importNum;
    }

    public void setImportNum(int importNum) {
        this.importNum = importNum;
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

    public int getExchangeState() {
        return exchangeState;
    }

    public void setExchangeState(int exchangeState) {
        this.exchangeState = exchangeState;
    }

    public int getDisPlayFlag() {
        return disPlayFlag;
    }

    public void setDisPlayFlag(int disPlayFlag) {
        this.disPlayFlag = disPlayFlag;
    }

    public int getBeforeN() {
        return beforeN;
    }

    public void setBeforeN(int beforeN) {
        this.beforeN = beforeN;
    }
}
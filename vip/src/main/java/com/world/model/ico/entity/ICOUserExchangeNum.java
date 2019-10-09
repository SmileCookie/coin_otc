package com.world.model.ico.entity;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

/**
 * Created by xie on 2017/7/12.
 * 兑换数量实体类
 */
public class ICOUserExchangeNum  extends Bean {

    private static final long serialVersionUID = 1L;

    private int id;
    private String userId;          //用户编号
    private int saleType;           //发售类型(1配售,2申购)
    private int totalNum;    //系统分配的总数量
    private int exchangeNum;	//兑换数量

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getSaleType() {
        return saleType;
    }

    public void setSaleType(int saleType) {
        this.saleType = saleType;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getExchangeNum() {
        return exchangeNum;
    }

    public void setExchangeNum(int exchangeNum) {
        this.exchangeNum = exchangeNum;
    }
}

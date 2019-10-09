package com.world.model.entity.usercap.entity;

import com.world.data.mysql.Bean;

/**
 * 特殊地址实体类，现主要用于冷到其他地址
 *
 * Created by xie on 2017/9/4.
 */
public class SpecialAddress extends Bean{

    private String coinType;
    private String address;

    public SpecialAddress(){}

    public SpecialAddress(String coinType, String address){
        this.coinType = coinType;
        this.address = address;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "SpecialAddress{" +
                "coinType='" + coinType + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

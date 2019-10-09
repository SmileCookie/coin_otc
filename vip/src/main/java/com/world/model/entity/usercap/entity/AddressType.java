package com.world.model.entity.usercap.entity;

import com.world.model.entity.SysEnum;

/**
 * Created by xie on 2017/9/6.
 */
public enum AddressType implements SysEnum{

    CAP_MONITOR(10000001, "用户资金监控报错"),
    TRADE_ASSETS_ACCOUNT_HUIGOU(10000002,"交易资金汇集账户"),
    TRADE_ASSETS_ACCOUNT_SHUALIANG(10000003,"量化交易账号"),
    COLD_TO_OTHER(10000004 , "冷转其他地址"),
    HOT_TO_OTHER(10000005 , "热提转其他地址"),
    AUTO_DOWNLOAD_AMOUNT_LIMIT(10010001 , "小额自动打币-用户限额"),
    AUTO_DOWNLOAD_ACCOUNT_MIN(10010002 , "小额自动打币-热提钱包最低额"),
    AUTO_DOWNLOAD_USER_ACCOUNT_ID(10010003 , "小额自动打币-打币账号"),
    AUTO_DOWNLOAD_ACCOUNT_WARN_LIMIT(10010004 , "小额自动打币-余额预警值"),
    AUTO_DOWNLOAD_ACCOUNT_NOTICE(10010005 , "小额自动打币-提醒"),
    AUTO_RECHARGE_ACCOUNT_NOTICE(10010501 , "充值到账-提醒"),
    BLOCKBROWSER_TX_QUERY(10040001 , "区块浏览器-交易查询地址");

    private AddressType(int key, String value) {
        this.key = key;
        this.value = value;
    }

    private int key;
    private String value;

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}

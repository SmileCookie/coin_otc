package com.world.model.enums;

public enum TradeFrozenType {
    /**
     * 发布广告
     */
    AD_CREATE(0, "发布广告"),
    /**
     * 广告下架
     */
    AD_CANCEL(1, "广告下架"),
    /**
     * 交易取消
     */
    TRADE_CANCEL(2, "交易取消"),
    /**
     * 交易成功
     */
    TRADE_SUCCESS(3, "交易成功"),
    /**
     * 出售订单
     */
    SELL_RECORD(4, "出售订单");

    private int key;
    private String value;

    TradeFrozenType(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static String getValue(int key) {
        for (TradeFrozenType tft : TradeFrozenType.values()) {
            if (tft.getKey() == key) {
                return tft.getValue();
            }
        }
        return "";
    }
}

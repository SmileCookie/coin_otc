package com.world.model.entity.level;


import com.world.model.entity.SysEnum;

public enum StoreCancelRefuseReasonEnum implements SysEnum {
    STORE_CANCEL_REFUSE_REASON_1(1,"您有未完成订单，请及时处理"),
    STORE_CANCEL_REFUSE_REASON_2(2,"您有广告未下架，请及时处理"),
    STORE_CANCEL_REFUSE_REASON_3(3,"成为商家未达到有效时长");
    private int key;
    private String value;

    StoreCancelRefuseReasonEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static String getName(int key) {
        for (StoreCancelRefuseReasonEnum scrr : StoreCancelRefuseReasonEnum.values()) {
            if (scrr.getKey() == key) {
                return scrr.getValue();
            }
        }
        return "";
    }
}

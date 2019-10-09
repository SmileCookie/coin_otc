package com.world.model.entity.level;


import com.world.model.entity.SysEnum;

public enum StoreApplyRefuseReasonEnum implements SysEnum {

    STORE_APPLY_REFUSE_REASON(1,"账户资金有异常，请联系平台客服"),

    STORE_APPLY_REFUSE_REASON_HOUHUI(2,"用户主动放弃商家申请");
    private int key;
    private String value;

    StoreApplyRefuseReasonEnum(int key, String value) {
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
        for (StoreApplyRefuseReasonEnum sarr : StoreApplyRefuseReasonEnum.values()) {
            if (sarr.getKey() == key) {
                return sarr.getValue();
            }
        }
        return "";
    }


}


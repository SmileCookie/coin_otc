package com.world.model.balaccount.job.wallettrans;

/**
 * @Author Ethan
 * @Date 2019-07-17 10:49
 **/
public enum DealTypeForbbEnum {

    RECHARGE(1),//充值
    OTHER_TO_HOT(5),//其他到热提
    OTHER_TO_COLD(6),//其他到冷
    COLD_TO_OTHER(7),//冷到其他
    HOT_TO_OTHER(8);//热提到其他

    private int dealType;

    DealTypeForbbEnum(int dealType) {
        this.dealType = dealType;
    }

    public static boolean include(int dealType) {
        boolean include = false;
        for (DealTypeForbbEnum value : DealTypeForbbEnum.values()) {
            if (value.getDealType() == dealType) {
                include = true;
                break;
            }
        }
        return include;
    }

    public int getDealType() {
        return dealType;
    }

    public void setDealType(int dealType) {
        this.dealType = dealType;
    }
}

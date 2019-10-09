package com.world.model.enums;

import com.world.model.entity.SysEnum;

public enum BonusEnum implements SysEnum{

    LEVEL_BONUS(1, "建点奖励",1),
    VIP_BONUS(2, "指导奖励",1),
    UP_LEVEL_BONUS(3, "晋升奖励",1),
    //LEADER_BONUS(4, "全球领袖分红奖励",1),

    HARVEST_BONUS(6, "VDS生态回馈",2),
    SUPER_MASTER_BONUS(5, "VIP分红奖励",3),
    NEOS_BONUS(7, "新人加成",4),
    RECOVERY_BONUS(8, "回本加成",5);
   // ASCRIPTION_BONUS(9, "归属记录",6);




    private BonusEnum(int key, String value,int type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }


    private int key;
    private String value;
    private int type;

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static String getValue(int key) {
        for (BonusEnum bonusEnum : BonusEnum.values()) {
            if (bonusEnum.getKey() == key) {
                return bonusEnum.getValue();
            }
        }
        return "";
    }

    public static int getType(int key) {
        for (BonusEnum bonusEnum : BonusEnum.values()) {
            if (bonusEnum.getKey() == key) {
                return bonusEnum.getType();
            }
        }
        return 1;
    }


}

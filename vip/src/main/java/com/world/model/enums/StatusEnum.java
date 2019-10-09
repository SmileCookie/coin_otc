package com.world.model.enums;

import com.world.model.entity.SysEnum;

public enum StatusEnum implements SysEnum {

    FREEZ(0, "冻结中",1),
    FIRE(1, "已释放",1);




    private StatusEnum(int key, String value,int type) {
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
        for (StatusEnum bonusEnum : StatusEnum.values()) {
            if (bonusEnum.getKey() == key) {
                return bonusEnum.getValue();
            }
        }
        return "";
    }

    public static int getType(int key) {
        for (StatusEnum bonusEnum : StatusEnum.values()) {
            if (bonusEnum.getKey() == key) {
                return bonusEnum.getType();
            }
        }
        return 1;
    }




}

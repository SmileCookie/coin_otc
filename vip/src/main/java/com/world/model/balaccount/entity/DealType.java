package com.world.model.balaccount.entity;

import com.world.model.entity.SysEnum;

public enum DealType implements SysEnum {
    OtherToHot(5,"其他到热提"),
    OtherToCold(6,"其他到冷"),
    ColdToOther(7,"冷到其他"),
    HotToOther(8,"热提到其他");

    private DealType(int key, String value) {
        this.key = key;
        this.value = value;
    }

    private int key;
    private String value;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

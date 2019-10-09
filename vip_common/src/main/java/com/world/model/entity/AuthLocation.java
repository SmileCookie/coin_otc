package com.world.model.entity;

public enum AuthLocation  implements SysEnum{
    //正面
    AUTH_FRONTAL(1,"frontal"),
    //反面
    AUTH_BACK(1,"back"),
    //手持
    AUTH_LOAD(1,"load"),
    //视频
    AUTH_VIDEO(1,"video")
    ;
    private int key;
    private String value;

    AuthLocation(int key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public String getValue() {
        return value;
    }
}

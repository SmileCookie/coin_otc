package com.world.model.entity.user;

import java.nio.file.attribute.UserDefinedFileAttributeView;

/**
 * Created by xie on 2017/6/8.
 */
public enum UserAuthDegree {
    C1(1,"C1"),
    C2(2,"C2"),
    C3(3,"C3");

    private int degree;
    private String value;

    private UserAuthDegree(int degree, String value){
        this.degree = degree;
        this.value = value;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

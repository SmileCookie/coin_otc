package com.world.model.entity.level;

import com.google.code.morphia.Datastore;
import com.world.data.mongo.id.StrBaseLongIdEntity;

import java.sql.Timestamp;

/**
 * Created by xie on 2017/6/23.
 * 积分标志表
 * */
public class JifenSign extends StrBaseLongIdEntity {

    public JifenSign(){
        super(null);
    }

    public JifenSign(Datastore ds) {
        super(ds);
    }

    private String userId;  //用户ID
    private int jifenType;    //操作类型(枚举类：JifenType：注册，登陆等)
    private Timestamp operTime; //完成时间/最近完成时间
    private int compFlag;    //完成标志(0：未完成，1：已完成)

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getJifenType() {
        return jifenType;
    }

    public void setJifenType(int jifenType) {
        this.jifenType = jifenType;
    }

    public Timestamp getOperTime() {
        return operTime;
    }

    public void setOperTime(Timestamp operTime) {
        this.operTime = operTime;
    }

    public int getCompFlag() {
        return compFlag;
    }

    public void setCompFlag(int compFlag) {
        this.compFlag = compFlag;
    }
}

package com.world.model;

import com.world.model.entity.SysEnum;

/**
 * Created by gkl on 2018/10/10.
 */
public enum LockType implements SysEnum {
    /**
     * 注册
     */
    REGISTER(-1, "register"),
    /**
     * 登录
     */
    LOGIN(0, "login"),
    /**
     * 重置二次验证
     */
    RESET_SECOND_VERIFY(1, "resetSecondVerify"),
    /**
     * 忘记登录密码
     */
    FORGET_LOGIN_PWD(2, "forgetLoginPwd"),
    /**
     * 提币
     */
    WITHDRAW(3, "withdraw"),
    /**
     * 修改登录密码
     */
    UPD_LOGIN_PWD(3, "updLoginPwd"),
    /**
     * 设置资金密码中登录密码校验
     */
    SET_USER_INFO_LOGIN_PWD(9, "setUserInfoLoginPwd"),
    /**
     * 修改资金密码中登录密码验证
     */
    UPD_PAY_PWD(9, "updPayPwd"),
    /**
     * 录入手机号
     */
    SET_MOBILE(4, "setMobile"),
    /**
     * 修改手机号
     */
    UPD_MOBILE(5, "updMobile"),
    /**
     * 开启手机验证
     */
    OPEN_MOBILE_VERIFY(6, "openMobileVerify"),
    /**
     * 关闭手机验证
     */
    CLOSE_MOBILE_VERIFY(7, "closeMobileVerify"),
    /**
     * 开启谷歌验证
     */
    OPEN_GOOGLE_VERIFY(8, "openGoogleVerify"),
    /**
     * 开启谷歌验证
     */
    CLOSE_GOOGLE_VERIFY(9, "closeGoogleVerify"),
    ;
    private int key;
    private String value;

    LockType(int key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }
    public static String getValue(int key) {
        for (LockType lt : LockType.values()) {
            if (lt.getKey() == key) {
                return lt.getValue();
            }
        }
        return "";
    }
}

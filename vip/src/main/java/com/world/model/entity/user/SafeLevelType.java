package com.world.model.entity.user;

import com.world.model.entity.SysEnum;
import com.world.util.CommonUtil;

import java.util.Map;

/**
 * 用户安全级别
 * 交易需要资金密码&&登录需要谷歌验证码  高级
 * 交易需要资金密码||登录需要谷歌验证码  中级
 * 绑定邮箱或手机号或全部绑定           初级
 */
public enum SafeLevelType  implements SysEnum {
    SAFE_LEVEL_LOW(1, "低级") ,
    SAFE_LEVEL_MIDDLE(2 , "中级"),
    SAFE_LEVEL_HIGH(3 , "高级")
    ;

    private SafeLevelType(int key, String value) {
        this.key = key;
        this.value = value;
    }

    private int key;
    private String value;

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static final Map<Integer, String> MAP = CommonUtil.enumToMap(values());
}

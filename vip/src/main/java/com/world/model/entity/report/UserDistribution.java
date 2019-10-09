package com.world.model.entity.report;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

/**
 * @ClassName UserDistribution
 * @Description
 * @Author kinghao
 * @Date 2018/8/10   20:58
 * @Version 1.0
 * @Description
 */
public class UserDistribution extends Bean {


    /**
     * memcache key
     **/
    private String key;
    /**
     * 属性
     **/
    private String attribute;

    /**
     * 初始值;
     **/
    private BigDecimal initial;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public BigDecimal getInitial() {
        return initial;
    }

    public void setInitial(BigDecimal initial) {
        this.initial = initial;
    }
}

package com.world.model.entity.report;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

/**
 * @ClassName UserOnline
 * @Description
 * @Author kinghao
 * @Date 2018/8/21   10:15
 * @Version 1.0
 * @Description
 */
public class UserOnline extends Bean {

    /**
     * memcache key
     **/
    private String key;
    /**
     * 初始值;
     **/
    private BigDecimal initial;

    /**
     * @Author 备注
     **/
    private String remark;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public BigDecimal getInitial() {
        return initial;
    }

    public void setInitial(BigDecimal initial) {
        this.initial = initial;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

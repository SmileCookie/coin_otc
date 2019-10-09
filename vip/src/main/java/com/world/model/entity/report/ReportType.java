package com.world.model.entity.report;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName ReportType
 * @Description
 * @Author kinghao
 * @Date 2018/8/7   17:32
 * @Version 1.0
 * @Description
 */
public class ReportType extends Bean {
    /**
     * id
     **/
    private int id;

    /**
     * 报表控制类型
     **/
    private int dealType;

    /**
     * memcache key
     **/
    private String key;

    /**
     * 属性
     **/
    private String attribute;

    /**
     * 委托分布  平台资金类型
     **/
    private int type;
    /**
     * 初始值;
     **/
    private BigDecimal initial;

    /**
     * 随机区间开始值
     **/
    private String start;

    /**
     * 随机区间结束值
     **/
    private String end;

    /**
     * 真假数据控制按钮 默认为假 1'
     **/
    private int trueOrFalse;

    /**
     * 创建时间
     **/
    private Date createdTime;

    /**
     * 创建人
     **/
    private String createdBy;
    /**
     * 修改时间
     **/
    private Date updateTime;
    /**
     * 修改人
     **/
    private String updayeBy;
    /**
     * 备注
     **/
    private String remark;

    /**
     * 币种流入流出总额
     **/
    private int sumInitial;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDealType() {
        return dealType;
    }

    public void setDealType(int dealType) {
        this.dealType = dealType;
    }

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BigDecimal getInitial() {
        return initial;
    }

    public void setInitial(BigDecimal initial) {
        this.initial = initial;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getTrueOrFalse() {
        return trueOrFalse;
    }

    public void setTrueOrFalse(int trueOrFalse) {
        this.trueOrFalse = trueOrFalse;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdayeBy() {
        return updayeBy;
    }

    public void setUpdayeBy(String updayeBy) {
        this.updayeBy = updayeBy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getSumInitial() {
        return sumInitial;
    }

    public void setSumInitial(int sumInitial) {
        this.sumInitial = sumInitial;
    }
}

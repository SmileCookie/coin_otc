package com.tenstar.timer.chart;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

/**
 * Kline 打包数据
 */
public class ChartDataPacketBean extends Bean {

    private Long id; //
    private BigDecimal open; //高开
    private BigDecimal close; //关闭
    private BigDecimal high; //高
    private BigDecimal low; //低
    private Integer type; //类型  1分钟  2 小时 3 天
    private Long times; //时间
    private BigDecimal totalMoney; //在那个金额
    private Integer reality;// 是否真实数据 （0：断档自动填充数据；1：真实数据）

    public Long getId() {
        return id;
    }

    public ChartDataPacketBean setId(Long id) {
        this.id = id;
        return this;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public ChartDataPacketBean setOpen(BigDecimal open) {
        this.open = open;
        return this;
    }

    public BigDecimal getClose() {
        return close;
    }

    public ChartDataPacketBean setClose(BigDecimal close) {
        this.close = close;
        return this;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public ChartDataPacketBean setHigh(BigDecimal high) {
        this.high = high;
        return this;
    }

    public BigDecimal getLow() {
        return low;
    }

    public ChartDataPacketBean setLow(BigDecimal low) {
        this.low = low;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public ChartDataPacketBean setType(Integer type) {
        this.type = type;
        return this;
    }

    public Long getTimes() {
        return times;
    }

    public ChartDataPacketBean setTimes(Long times) {
        this.times = times;
        return this;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public ChartDataPacketBean setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
        return this;
    }

    public Integer getReality() {
        return reality;
    }

    public ChartDataPacketBean setReality(Integer reality) {
        this.reality = reality;
        return this;
    }
}

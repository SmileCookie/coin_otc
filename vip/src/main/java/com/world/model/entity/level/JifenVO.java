package com.world.model.entity.level;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by suxinjie on 2017/3/6.
 */
public class JifenVO {

    private String typeValue;
    private BigDecimal jifen = BigDecimal.ZERO;
    private String memo;
    private int ioType;
    private Timestamp addTime;

    public int getIoType() {
        return ioType;
    }

    public void setIoType(int ioType) {
        this.ioType = ioType;
    }

    public BigDecimal getJifen() {
        return jifen;
    }

    public void setJifen(BigDecimal jifen) {
        this.jifen = jifen;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }

}

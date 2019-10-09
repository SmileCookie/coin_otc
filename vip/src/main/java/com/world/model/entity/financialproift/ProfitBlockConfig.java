package com.world.model.entity.financialproift;

import java.util.Date;

import com.world.data.mysql.Bean;

public class ProfitBlockConfig extends Bean {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private long id;

    /**
     * 区块类型:
     * 1当前区块，全表只能有1个
     * 2分红区块，维护添加配置
     */
    private int blockType;

    /**
     * 区块高度
     */
    private long blockHeight;

    /**
     * 备注
     */
    private String blockRemark;

    /**
     * 创建时间
     */
    private Date addTime;

    /**
     * 修改时间
     */
    private Date modifyTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBlockType() {
        return blockType;
    }

    public void setBlockType(int blockType) {
        this.blockType = blockType;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getBlockRemark() {
        return blockRemark;
    }

    public void setBlockRemark(String blockRemark) {
        this.blockRemark = blockRemark;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }


}

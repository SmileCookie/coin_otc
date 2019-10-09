package com.world.model.entity.financialproift;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2019/8/135:15 PM
 */
public class FinDouProfitLog extends Bean {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private long id;
    /**
     * 用户id
     */
    private long userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 币种类型
     */
    private int fundsType;
    /**
     * 复投金额
     */
    private BigDecimal doubleThrowAmount;
    /**
     * 划入超级节点金额
     */
    private BigDecimal toSuperNodeAmount;
    /**
     * 复投次数
     */
    private int reinTimes;
    /**
     *自动复投标志，0人工复投，1, 为增投，2释放冻结资金触发 自动复投, 3为手动复投
     */
    private int doubleThrowFlag;
    /**
     * 创建时间
     */
    private Date creatTime;

    /**
     * doubleThrowAmount + toSuperNodeAmount
     */
    private BigDecimal lossAmount;
    /**
     *lossAmount * 5
     */
    private BigDecimal releaseAmount;
    /*是否批次*/
    private int batchNo;
    /*释放标识*/
    private String dealflagName;
    
	public String getDealflagName() {
		return dealflagName;
	}

	public void setDealflagName(String dealflagName) {
		this.dealflagName = dealflagName;
	}

	public int getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(int batchNo) {
		this.batchNo = batchNo;
	}

	public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getFundsType() {
        return fundsType;
    }

    public void setFundsType(int fundsType) {
        this.fundsType = fundsType;
    }

    public BigDecimal getDoubleThrowAmount() {
        return doubleThrowAmount;
    }

    public void setDoubleThrowAmount(BigDecimal doubleThrowAmount) {
        this.doubleThrowAmount = doubleThrowAmount;
    }

    public BigDecimal getToSuperNodeAmount() {
        return toSuperNodeAmount;
    }

    public void setToSuperNodeAmount(BigDecimal toSuperNodeAmount) {
        this.toSuperNodeAmount = toSuperNodeAmount;
    }

    public int getReinTimes() {
        return reinTimes;
    }

    public void setReinTimes(int reinTimes) {
        this.reinTimes = reinTimes;
    }

    public int getDoubleThrowFlag() {
        return doubleThrowFlag;
    }

    public void setDoubleThrowFlag(int doubleThrowFlag) {
        this.doubleThrowFlag = doubleThrowFlag;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public BigDecimal getLossAmount() {
        return lossAmount;
    }

    public void setLossAmount(BigDecimal lossAmount) {
        this.lossAmount = lossAmount;
    }

    public BigDecimal getReleaseAmount() {
        return releaseAmount;
    }

    public void setReleaseAmount(BigDecimal releaseAmount) {
        this.releaseAmount = releaseAmount;
    }
}

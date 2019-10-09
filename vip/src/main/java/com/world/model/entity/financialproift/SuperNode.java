package com.world.model.entity.financialproift;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

public class SuperNode extends Bean {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private long id;
    /**
     * 超级节点编号
     */
    private String sNodeSeq;
    /**
     * 超级节点名称
     */
    private String sNodeName;
    /**
     * 超级节点地址
     */
    private String sNodeAddr;
    /**
     * 超级节点余额
     */
    private BigDecimal sNodeBalance = BigDecimal.ZERO;
    /**
     * 超级节点转出金额
     */
    private BigDecimal sNodePayAmount = BigDecimal.ZERO;
    /**
     * 管理费率
     */
    private BigDecimal managerRate;
    /**
     * 节点类型
     */
    private int sNodeType;
    /*节点归属类型*/
    private int sNodeBelType;
    /*页面是否展示，统计时的标志*/
    private int sNodeShowFlag;
    /**
     * 状态：1正常，0停用
     */
    private Integer sNodeState;
    /**
     * 超级节点备注
     */
    private String sNodeRemark;
    /**
     * 超级节点添加时间
     */
    private Date sNodeAddTime;
    /**
     * 挖矿最近产出数量
     */
    private BigDecimal lateMiningAmount;
    /**
     * 可分红额度
     */
    private BigDecimal bonusAmount;
    /**
     * 超级节点修改时间
     */
    private Date sNodeModifyTime;

    private Date lateMiningTime;


    /**
     * 区块链查看链接
     */
    private String sNodeQueryLink;
    /*归属类型名称 1VIP分红，2新人加成*/
    private String sNodeBelName;

    public String getsNodeBelName() {
		return sNodeBelName;
	}

	public void setsNodeBelName(String sNodeBelName) {
		this.sNodeBelName = sNodeBelName;
	}

	public Long getLateMiningTime() {
        if(null != lateMiningTime){
            return lateMiningTime.getTime();
        }else{
            return 0L;
        }
    }
    
    public int getsNodeShowFlag() {
		return sNodeShowFlag;
	}

	public void setsNodeShowFlag(int sNodeShowFlag) {
		this.sNodeShowFlag = sNodeShowFlag;
	}

	public int getsNodeBelType() {
		return sNodeBelType;
	}

	public void setsNodeBelType(int sNodeBelType) {
		this.sNodeBelType = sNodeBelType;
	}

	public void setLateMiningTime(Date lateMiningTime) {
        this.lateMiningTime = lateMiningTime;
    }

    public BigDecimal getManagerRate() {
        return managerRate;
    }

    public void setManagerRate(BigDecimal managerRate) {
        this.managerRate = managerRate;
    }

    private Long sNodeNum;

    public String getsNodeQueryLink() {
        return sNodeQueryLink + sNodeAddr;
    }

    public BigDecimal getLateMiningAmount() {
        return lateMiningAmount;
    }

    public void setLateMiningAmount(BigDecimal lateMiningAmount) {
        this.lateMiningAmount = lateMiningAmount;
    }

    public void setsNodeQueryLink(String sNodeQueryLink) {
        this.sNodeQueryLink = sNodeQueryLink;
    }

    public BigDecimal getBonusAmount() {
        return bonusAmount;
    }

    public void setBonusAmount(BigDecimal bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    private BigDecimal sNodeTotalProfit = BigDecimal.ZERO;

    public Long getsNodeNum() {
        return sNodeNum;
    }

    public void setsNodeNum(Long sNodeNum) {
        this.sNodeNum = sNodeNum;
    }

    public BigDecimal getsNodeTotalProfit() {
        return sNodeTotalProfit;
    }

    public void setsNodeTotalProfit(BigDecimal sNodeTotalProfit) {
        this.sNodeTotalProfit = sNodeTotalProfit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getsNodeSeq() {
        return sNodeSeq;
    }

    public void setsNodeSeq(String sNodeSeq) {
        this.sNodeSeq = sNodeSeq;
    }

    public String getsNodeName() {
        return sNodeName;
    }

    public void setsNodeName(String sNodeName) {
        this.sNodeName = sNodeName;
    }

    public String getsNodeAddr() {
        return sNodeAddr;
    }

    public void setsNodeAddr(String sNodeAddr) {
        this.sNodeAddr = sNodeAddr;
    }

    public BigDecimal getsNodeBalance() {
        return sNodeBalance;
    }

    public void setsNodeBalance(BigDecimal sNodeBalance) {
        this.sNodeBalance = sNodeBalance;
    }

    public BigDecimal getsNodePayAmount() {
        return sNodePayAmount;
    }

    public void setsNodePayAmount(BigDecimal sNodePayAmount) {
        this.sNodePayAmount = sNodePayAmount;
    }

    public int getsNodeType() {
        return sNodeType;
    }

    public void setsNodeType(int sNodeType) {
        this.sNodeType = sNodeType;
    }

    public Integer getsNodeState() {
        return sNodeState;
    }

    public void setsNodeState(Integer sNodeState) {
        this.sNodeState = sNodeState;
    }

    public String getsNodeRemark() {
        return sNodeRemark;
    }

    public void setsNodeRemark(String sNodeRemark) {
        this.sNodeRemark = sNodeRemark;
    }

    public Date getsNodeAddTime() {
        return sNodeAddTime;
    }

    public void setsNodeAddTime(Date sNodeAddTime) {
        this.sNodeAddTime = sNodeAddTime;
    }

    public Date getsNodeModifyTime() {
        return sNodeModifyTime;
    }

    public void setsNodeModifyTime(Date sNodeModifyTime) {
        this.sNodeModifyTime = sNodeModifyTime;
    }
}

package com.world.model.entity.financialproift;

import java.math.BigDecimal;
import java.util.Date;

import com.world.data.mysql.Bean;

public class FinUserInsureInvest extends Bean {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /*主键*/
    private int id;
    /*用户ID*/
    private int userId;
    /*用户名*/
    private String userName;
    /*投资价格*/
    private BigDecimal investPrice;
    /*触发价格*/
    private BigDecimal triggerPrice;
    /*触发投资时真实价格*/
    private BigDecimal realPrice;
    /*投资矩阵*/
    private int investLevel;
    /*投资矩阵金额*/
    private BigDecimal investLevelAmount;
    /*担保投资资金*/
    private BigDecimal insureInvestAmount;
    /*投资剩余资金*/
    private BigDecimal insureInvestSurplusAmount;
    /*投资分数*/
    private int insureInvestNum;
    /*投资剩余份数*/
    private int insureInvestSurplusNum;
    /*推进人邀请码*/
    private String pInvitationCode;
    /*用户VID*/
    private String userVID;
    /*创建时间*/
    private Date createTime;
    /*触发时间*/
    private Date triggerTime;
    /*投资状态:0未触发暂未投资,1部分投资中,2投资完成,3已撤销*/
    private int investState;
    /*触发标志:0未触发，1是系统触发，2是用户触发*/
    private int triggerFlag;

    /**
     * 状态转译
     */
    private String triggerFlagDesc;
    /**
     * 设置排位转译
     */
    private String investStateDesc;
    /* 是否跳到排位页面*/
    private boolean JumpFlag = false;

    public boolean isJumpFlag() {
        return JumpFlag;
    }

    public void setJumpFlag(boolean jumpFlag) {
        JumpFlag = jumpFlag;
    }

    public String getInvestStateDesc() {
        return investStateDesc;
    }

    public void setInvestStateDesc(String investStateDesc) {
        this.investStateDesc = investStateDesc;
    }

    public String getTriggerFlagDesc() {
        return triggerFlagDesc;
    }

    public void setTriggerFlagDesc(String triggerFlagDesc) {
        this.triggerFlagDesc = triggerFlagDesc;
    }

    public int getInvestLevel() {
        return investLevel;
    }

    public void setInvestLevel(int investLevel) {
        this.investLevel = investLevel;
    }

    public BigDecimal getInvestLevelAmount() {
        return investLevelAmount;
    }

    public void setInvestLevelAmount(BigDecimal investLevelAmount) {
        this.investLevelAmount = investLevelAmount;
    }

    public int getInsureInvestNum() {
        return insureInvestNum;
    }

    public void setInsureInvestNum(int insureInvestNum) {
        this.insureInvestNum = insureInvestNum;
    }

    public int getInsureInvestSurplusNum() {
        return insureInvestSurplusNum;
    }

    public void setInsureInvestSurplusNum(int insureInvestSurplusNum) {
        this.insureInvestSurplusNum = insureInvestSurplusNum;
    }

    public String getpInvitationCode() {
        return pInvitationCode;
    }

    public void setpInvitationCode(String pInvitationCode) {
        this.pInvitationCode = pInvitationCode;
    }

    public String getUserVID() {
        return userVID;
    }

    public void setUserVID(String userVID) {
        this.userVID = userVID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getInvestPrice() {
        return investPrice;
    }

    public void setInvestPrice(BigDecimal investPrice) {
        this.investPrice = investPrice;
    }

    public BigDecimal getTriggerPrice() {
        return triggerPrice;
    }

    public void setTriggerPrice(BigDecimal triggerPrice) {
        this.triggerPrice = triggerPrice;
    }

    public BigDecimal getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(BigDecimal realPrice) {
        this.realPrice = realPrice;
    }

    public BigDecimal getInsureInvestAmount() {
        return insureInvestAmount;
    }

    public void setInsureInvestAmount(BigDecimal insureInvestAmount) {
        this.insureInvestAmount = insureInvestAmount;
    }

    public BigDecimal getInsureInvestSurplusAmount() {
        return insureInvestSurplusAmount;
    }

    public void setInsureInvestSurplusAmount(BigDecimal insureInvestSurplusAmount) {
        this.insureInvestSurplusAmount = insureInvestSurplusAmount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(Date triggerTime) {
        this.triggerTime = triggerTime;
    }

    public int getInvestState() {
        return investState;
    }

    public void setInvestState(int investState) {
        this.investState = investState;
    }

    public int getTriggerFlag() {
        return triggerFlag;
    }

    public void setTriggerFlag(int triggerFlag) {
        this.triggerFlag = triggerFlag;
    }
}

package com.world.model.balaccount.entity;


import com.world.util.string.StringUtil;

import java.math.BigDecimal;
import java.util.Date;

public class BalanceResp {
    
    //资金类型
    private String fundsType;
    //资金类型
    private String agreement;
    //资金类型名称
    private String fundsTypeName;
    //用户充值
    private String userRecharge;
    //用户提现
    private String userWithdraw;
    //热充发生额
    private String hotRechargeHappenedAmount;
    //热充到冷网络费
    private String hotToColdFee;
    //热充到冷
    private String hotToCold;
    //冷钱包发生额
    private String coldHappenedAmount;
    //冷到其他发生额
    private String coldToOtherHappenedAmount;
    //冷到其他网络费
    private String coldToOtherFee;
    //冷到热提网络费
    private String coldToHotFee;
    //冷到热提
    private String coldToHot;
    //热提发生额
    private String hotWithdrawHappenedAmount;
    //热提到用户网络费
    private String withdrawFee;
    //其他到冷发生额
    private String otherToColdHappenedAmount;
    //其他到热提发生额
    private String otherToHotHappenedAmount;
    //热提到其他发生额
    private String hotWithdrawToOtherHappenedAmount;
    //热提到其他网络费
    private String hotWithdrawToOtherFee;
    //热提到冷发生额
    private String hotWithdrawToColdHappenedAmount;
    //热提到冷网络费
    private String hotWithdrawToColdFee;

    //冷到热提合约消耗
    private String coldToHotContract = "0";
    //提现合约消耗
    private String userWithdrawContract = "0";

    //冷到其他合约消耗
    private String coldToOtherContract = "0";
    //热提到其他合约消耗
    private String hotWithdrawToOtherContract = "0";

    //区块高度
    private String blockHeight;
    //对账日期
    private String checkTime;

    private String  maxHeight;
    private String  minHeight;
    private Date maxTime;
    private Date  minTime;


    private BigDecimal otherHappenAmount;


    public BigDecimal getOtherHappenAmount() {
        return otherHappenAmount;
    }

    public void setOtherHappenAmount(BigDecimal otherHappenAmount) {
        this.otherHappenAmount = otherHappenAmount;
    }

    public String getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(String maxHeight) {
        this.maxHeight = maxHeight;
    }

    public String getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(String minHeight) {
        this.minHeight = minHeight;
    }

    public Date getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Date maxTime) {
        this.maxTime = maxTime;
    }

    public Date getMinTime() {
        return minTime;
    }

    public void setMinTime(Date minTime) {
        this.minTime = minTime;
    }

    public String getFundsType() {
        return fundsType;
    }

    public void setFundsType(String fundsType) {
        this.fundsType = fundsType;
    }

    public String getUserRecharge() {
        if(!StringUtil.exist(userRecharge)){
            userRecharge = "0.0";
        }
        return userRecharge;
    }

    public void setUserRecharge(String userRecharge) {
        this.userRecharge = userRecharge;
    }

    public String getUserWithdraw() {
        if(!StringUtil.exist(userWithdraw)){
            userWithdraw = "0.0";
        }
        return userWithdraw;
    }

    public void setUserWithdraw(String userWithdraw) {
        this.userWithdraw = userWithdraw;
    }

    public String getHotRechargeHappenedAmount() {
        if(!StringUtil.exist(hotRechargeHappenedAmount)){
            hotRechargeHappenedAmount = "0.0";
        }
        return hotRechargeHappenedAmount;
    }

    public void setHotRechargeHappenedAmount(String hotRechargeHappenedAmount) {
        this.hotRechargeHappenedAmount = hotRechargeHappenedAmount;
    }

    public String getHotToColdFee() {
        if(!StringUtil.exist(hotToColdFee)){
            hotToColdFee = "0.0";
        }
        return hotToColdFee;
    }

    public void setHotToColdFee(String hotToColdFee) {
        this.hotToColdFee = hotToColdFee;
    }

    public String getColdHappenedAmount() {
        if(!StringUtil.exist(coldHappenedAmount)){
            coldHappenedAmount = "0.0";
        }
        return coldHappenedAmount;
    }

    public void setColdHappenedAmount(String coldHappenedAmount) {
        this.coldHappenedAmount = coldHappenedAmount;
    }

    public String getColdToOtherHappenedAmount() {
        if(!StringUtil.exist(coldToOtherHappenedAmount)){
            coldToOtherHappenedAmount = "0.0";
        }
        return coldToOtherHappenedAmount;
    }

    public void setColdToOtherHappenedAmount(String coldToOtherHappenedAmount) {
        this.coldToOtherHappenedAmount = coldToOtherHappenedAmount;
    }

    public String getColdToOtherFee() {
        if(!StringUtil.exist(coldToOtherFee)){
            coldToOtherFee = "0.0";
        }
        return coldToOtherFee;
    }

    public void setColdToOtherFee(String coldToOtherFee) {
        this.coldToOtherFee = coldToOtherFee;
    }

    public String getColdToHotFee() {
        if(!StringUtil.exist(coldToHotFee)){
            coldToHotFee = "0.0";
        }
        return coldToHotFee;
    }

    public void setColdToHotFee(String coldToHotFee) {
        this.coldToHotFee = coldToHotFee;
    }

    public String getHotWithdrawHappenedAmount() {
        if(!StringUtil.exist(hotWithdrawHappenedAmount)){
            hotWithdrawHappenedAmount = "0.0";
        }
        return hotWithdrawHappenedAmount;
    }

    public void setHotWithdrawHappenedAmount(String hotWithdrawHappenedAmount) {
        this.hotWithdrawHappenedAmount = hotWithdrawHappenedAmount;
    }

    public String getWithdrawFee() {
        if(!StringUtil.exist(withdrawFee)){
            withdrawFee = "0.0";
        }
        return withdrawFee;
    }

    public void setWithdrawFee(String withdrawFee) {
        this.withdrawFee = withdrawFee;
    }

    public String getOtherToColdHappenedAmount() {
        if(!StringUtil.exist(otherToColdHappenedAmount)){
            otherToColdHappenedAmount = "0.0";
        }
        return otherToColdHappenedAmount;
    }

    public void setOtherToColdHappenedAmount(String otherToColdHappenedAmount) {
        this.otherToColdHappenedAmount = otherToColdHappenedAmount;
    }

    public String getOtherToHotHappenedAmount() {
        if(!StringUtil.exist(otherToHotHappenedAmount)){
            otherToHotHappenedAmount = "0.0";
        }
        return otherToHotHappenedAmount;
    }

    public void setOtherToHotHappenedAmount(String otherToHotHappenedAmount) {
        this.otherToHotHappenedAmount = otherToHotHappenedAmount;
    }

    public String getHotWithdrawToOtherHappenedAmount() {
        if(!StringUtil.exist(hotWithdrawToOtherHappenedAmount)){
            hotWithdrawToOtherHappenedAmount = "0.0";
        }
        return hotWithdrawToOtherHappenedAmount;
    }

    public void setHotWithdrawToOtherHappenedAmount(String hotWithdrawToOtherHappenedAmount) {
        this.hotWithdrawToOtherHappenedAmount = hotWithdrawToOtherHappenedAmount;
    }

    public String getHotWithdrawToOtherFee() {
        if(!StringUtil.exist(hotWithdrawToOtherFee)){
            hotWithdrawToOtherFee = "0.0";
        }
        return hotWithdrawToOtherFee;
    }

    public void setHotWithdrawToOtherFee(String hotWithdrawToOtherFee) {
        this.hotWithdrawToOtherFee = hotWithdrawToOtherFee;
    }

    public String getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(String blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public String getFundsTypeName() {
        return fundsTypeName;
    }

    public void setFundsTypeName(String fundsTypeName) {
        this.fundsTypeName = fundsTypeName;
    }

    public String getHotWithdrawToColdHappenedAmount() {
        if(!StringUtil.exist(hotWithdrawToColdHappenedAmount)){
            hotWithdrawToColdHappenedAmount = "0.0";
        }
        return hotWithdrawToColdHappenedAmount;
    }

    public void setHotWithdrawToColdHappenedAmount(String hotWithdrawToColdHappenedAmount) {
        this.hotWithdrawToColdHappenedAmount = hotWithdrawToColdHappenedAmount;
    }

    public String getHotWithdrawToColdFee() {
        if(!StringUtil.exist(hotWithdrawToColdFee)){
            hotWithdrawToColdFee = "0.0";
        }
        return hotWithdrawToColdFee;
    }

    public void setHotWithdrawToColdFee(String hotWithdrawToColdFee) {
        this.hotWithdrawToColdFee = hotWithdrawToColdFee;
    }

    public String getColdToHotContract() {
        if(!StringUtil.exist(coldToHotContract)){
            coldToHotContract = "0.0";
        }
        return coldToHotContract;
    }

    public void setColdToHotContract(String coldToHotContract) {
        this.coldToHotContract = coldToHotContract;
    }

    public String getUserWithdrawContract() {
        if(!StringUtil.exist(userWithdrawContract)){
            userWithdrawContract = "0.0";
        }
        return userWithdrawContract;
    }

    public void setUserWithdrawContract(String userWithdrawContract) {
        this.userWithdrawContract = userWithdrawContract;
    }

    public String getColdToOtherContract() {
        if(!StringUtil.exist(coldToOtherContract)){
            coldToOtherContract = "0.0";
        }
        return coldToOtherContract;
    }

    public void setColdToOtherContract(String coldToOtherContract) {
        this.coldToOtherContract = coldToOtherContract;
    }

    public String getHotWithdrawToOtherContract() {
        if(!StringUtil.exist(hotWithdrawToOtherContract)){
            hotWithdrawToOtherContract = "0.0";
        }
        return hotWithdrawToOtherContract;
    }

    public void setHotWithdrawToOtherContract(String hotWithdrawToOtherContract) {
        this.hotWithdrawToOtherContract = hotWithdrawToOtherContract;
    }

    public String getHotToCold() {
        if(!StringUtil.exist(hotToCold)){
            hotToCold = "0.0";
        }
        return hotToCold;
    }

    public void setHotToCold(String hotToCold) {
        this.hotToCold = hotToCold;
    }

    public String getColdToHot() {
        if(!StringUtil.exist(coldToHot)){
            coldToHot = "0.0";
        }
        return coldToHot;
    }

    public void setColdToHot(String coldToHot) {
        this.coldToHot = coldToHot;
    }

    public String getAgreement() {
        return agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }
}

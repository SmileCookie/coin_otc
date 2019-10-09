package com.world.model.entity.level;

import com.google.code.morphia.Datastore;
import com.world.data.mongo.id.StrBaseLongIdEntity;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2018/12/33:50 PM
 */
public class FuncJump extends StrBaseLongIdEntity {
    private static final long serialVersionUID = -5384331724346757875L;

    public FuncJump(){
        super(null);
    }

    public FuncJump(Datastore ds) {
        super(ds);
    }
    //序号
    private int seqNo;
    //用户ID
    private String userId;
    //注册
    private boolean regStatus;
    //登录
    private boolean loginState;
    //手机验证
    private boolean mobileState;
    //谷歌验证
    private boolean googleStatus;
    //首次充值
    private boolean fstStatus;
    //日常充值
    private boolean dayStatus;
    //首次交易
    private boolean coinStatus;
    //日常交易
    private boolean someCoinStatus;
    //积分类别（1：一次性（如绑定手机），2：周期性（如：登录），3：每次（如：充值获取积分））
    private int jifenCategory;
    //开启关闭手机验证标识
    private boolean isMobileClose;
    //开启关闭谷歌验证标识
    private boolean isGoogleClose;

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isRegStatus() {
        return regStatus;
    }

    public void setRegStatus(boolean regStatus) {
        this.regStatus = regStatus;
    }

    public boolean isLoginState() {
        return loginState;
    }

    public void setLoginState(boolean loginState) {
        this.loginState = loginState;
    }

    public boolean isMobileState() {
        return mobileState;
    }

    public void setMobileState(boolean mobileState) {
        this.mobileState = mobileState;
    }

    public boolean isGoogleStatus() {
        return googleStatus;
    }

    public void setGoogleStatus(boolean googleStatus) {
        this.googleStatus = googleStatus;
    }

    public boolean isFstStatus() {
        return fstStatus;
    }

    public void setFstStatus(boolean fstStatus) {
        this.fstStatus = fstStatus;
    }

    public boolean isDayStatus() {
        return dayStatus;
    }

    public void setDayStatus(boolean dayStatus) {
        this.dayStatus = dayStatus;
    }

    public boolean isSomeCoinStatus() {
        return someCoinStatus;
    }

    public void setSomeCoinStatus(boolean someCoinStatus) {
        this.someCoinStatus = someCoinStatus;
    }

    public boolean isCoinStatus() {
        return coinStatus;
    }

    public void setCoinStatus(boolean coinStatus) {
        this.coinStatus = coinStatus;
    }

    public int getJifenCategory() {
        return jifenCategory;
    }

    public void setJifenCategory(int jifenCategory) {
        this.jifenCategory = jifenCategory;
    }

    public boolean isMobileClose() {
        return isMobileClose;
    }

    public void setMobileClose(boolean mobileClose) {
        isMobileClose = mobileClose;
    }

    public boolean isGoogleClose() {
        return isGoogleClose;
    }

    public void setGoogleClose(boolean googleClose) {
        isGoogleClose = googleClose;
    }
}

package com.world.model.entity.user;

import java.io.Serializable;
import java.util.List;

/**
 * <p>@Description: </p>
 *
 * @author Sue
 * @date 2018/3/19上午11:13
 */
public class UserSession implements Serializable{

    private String sessionId;

    /**
     * 用户基本信息
     */
    private String uid;
    private String mobile;
    private String userName = "";
    private String nickname = "";
    private int locked;
    private String email;
    private String transPwd;
    private String rongCloudToken;
    private boolean isSafePwd;
    /**
     * google密钥
     */
    private String secret;
    /**
     * 是否开启谷歌验证
     */
    private Boolean isGoogleOpen = false;
    /**
     * 是否开启短信验证
     */
    private Boolean isSmsOpen = false;

    /**
     * 用户实名信息
     */
    private String cardName;
    private int cardStatus;
    private String reason;
    private String cardImageUrl;
    private String checkResult;

    /**
     * 消息信息
     */
    private long unReadMsgNum;

    public void buildUser(User user) {
        this.setUid(user.getId());
        this.setMobile(user.getUserContact().getSafeMobile());
        this.setNickname(user.getNickname());
        this.setLocked(user.getRepayLock());
        this.setEmail(user.getUserContact().getSafeEmail());
        this.setTransPwd(user.getSafePwd());
        this.setRongCloudToken(user.getRongCloudToken());
        this.setGoogleOpen(user.getGoogleOpen());
        this.setSmsOpen(user.getSmsOpen());
        this.setSecret(user.getUserContact().getSecret());
        this.setSafePwd(user.getHasSafePwd());
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public int getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(int cardStatus) {
        this.cardStatus = cardStatus;
    }

    public long getUnReadMsgNum() {
        return unReadMsgNum;
    }

    public void setUnReadMsgNum(long unReadMsgNum) {
        this.unReadMsgNum = unReadMsgNum;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTransPwd() {
        return transPwd;
    }

    public void setTransPwd(String transPwd) {
        this.transPwd = transPwd;
    }

    public String getRongCloudToken() {
        return rongCloudToken;
    }

    public void setRongCloudToken(String rongCloudToken) {
        this.rongCloudToken = rongCloudToken;
    }

    public String getCardImageUrl() {
        return cardImageUrl;
    }

    public void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public Boolean getGoogleOpen() {
        return isGoogleOpen;
    }

    public void setGoogleOpen(Boolean googleOpen) {
        isGoogleOpen = googleOpen;
    }

    public Boolean getSmsOpen() {
        return isSmsOpen;
    }

    public void setSmsOpen(Boolean smsOpen) {
        isSmsOpen = smsOpen;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isSafePwd() {
        return isSafePwd;
    }

    public void setSafePwd(boolean safePwd) {
        isSafePwd = safePwd;
    }
}

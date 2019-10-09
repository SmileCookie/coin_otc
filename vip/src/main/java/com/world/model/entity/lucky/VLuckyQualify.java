package com.world.model.entity.lucky;

/**
 * @Title: 用户抽奖资格展示数据
 * @Description: 用于用户抽奖资格对象转换数据
 * @Company: atlas
 * @author: xzhang
 */
public class VLuckyQualify {

    private String isLogin;
    private String jackpotSize;//奖池已发放
    private String userAmount;//用户已领取的奖金
    private String currAmount;//当前领取奖金
    private String ViewFlag;//展示哪个页面
    private String chance;//抽奖机会
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getChance() {
        return chance;
    }

    public void setChance(String chance) {
        this.chance = chance;
    }

    public String getCurrAmount() {
        return currAmount;
    }

    public void setCurrAmount(String currAmount) {
        this.currAmount = currAmount;
    }

    public String getViewFlag() {
        return ViewFlag;
    }

    public void setViewFlag(String viewFlag) {
        ViewFlag = viewFlag;
    }

    public String getUserAmount() {
        return userAmount;
    }

    public void setUserAmount(String userAmount) {
        this.userAmount = userAmount;
    }

    public String getJackpotSize() {
        return jackpotSize;
    }

    public void setJackpotSize(String jackpotSize) {
        this.jackpotSize = jackpotSize;
    }

    public String getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(String isLogin) {
        this.isLogin = isLogin;
    }
}

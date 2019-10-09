package com.world.model.entity.vote;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

public class Coin extends Bean {
    private int CoinId;
    private String coinNameJson;
    private String  img;
    private String urlJson;
    private String coinContentJson;
    private String coinFullNameJson;
    private int voteCount;
    private int realCount;
    private String rate;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public int getRealCount() {
        return realCount;
    }

    public void setRealCount(int realCount) {
        this.realCount = realCount;
    }

    public int getCoinId() {
        return CoinId;
    }

    public void setCoinId(int coinId) {
        CoinId = coinId;
    }

    public String getCoinNameJson() {
        return coinNameJson;
    }

    public void setCoinNameJson(String coinNameJson) {
        this.coinNameJson = coinNameJson;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrlJson() {
        return urlJson;
    }

    public void setUrlJson(String urlJson) {
        this.urlJson = urlJson;
    }

    public String getCoinContentJson() {
        return coinContentJson;
    }

    public void setCoinContentJson(String coinContentJson) {
        this.coinContentJson = coinContentJson;
    }

    public String getCoinFullNameJson() {
        return coinFullNameJson;
    }

    public void setCoinFullNameJson(String coinFullNameJson) {
        this.coinFullNameJson = coinFullNameJson;
    }
}

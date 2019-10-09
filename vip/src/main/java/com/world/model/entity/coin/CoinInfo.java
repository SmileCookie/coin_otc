package com.world.model.entity.coin;

import com.world.data.mysql.Bean;

/**
 * @ClassName CoinInfo
 * @Description
 * @Author kinghao
 * @Date 2018/8/15   15:54
 * @Version 1.0
 * @Description
 */
public class CoinInfo extends Bean {
    /**
     * id
     **/
    private int coinId;
    /**
     * 币种名称
     **/
    private String coinNameJson;
    /**
     * 币种img-html
     **/
    private String img;
    /**
     * 币种链接
     **/
    private String urlJson;
    /**
     * 币种描述
     **/
    private String coinContentJson;
    /**
     * 币种全称
     **/
    private String coinFullNameJson;

    /**
     * 币种简介
     **/
    private String introductionJson;
    /**
     * 币种状态
     **/
    private int status;

    /**
     * 币种国际化信息  CN简体中文  EN英文   HK繁体中文
     **/
    private String internationalization;

    public String getInternationalization() {
        return internationalization;
    }

    public void setInternationalization(String internationalization) {
        this.internationalization = internationalization;
    }

    public int getCoinId() {
        return coinId;
    }

    public void setCoinId(int coinId) {
        this.coinId = coinId;
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

    public String getIntroductionJson() {
        return introductionJson;
    }

    public void setIntroductionJson(String introductionJson) {
        this.introductionJson = introductionJson;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

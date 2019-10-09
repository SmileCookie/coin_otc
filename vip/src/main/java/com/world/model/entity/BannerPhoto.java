package com.world.model.entity;

import com.world.data.mysql.Bean;

import java.util.Date;

/**
 * <p>@Description: </p>
 *
 * @author shujianfang
 * @date 2018/7/17下午1:45
 */
public class BannerPhoto extends Bean {
    /**
     *  主键
     **/
    private Long id;

    /**
     *  图片名称
     **/
    private String bannerName;

    /**
     *  banner图链接
     **/
    private String bannerUrl;

    /**
     *  跳转链接
     **/
    private String linkUrl;
    /**
     *  状态，1-开启；0-关闭
     **/
    private Integer status;
    /**
     *  操作人
     **/
    private String checkUser;
    /**
     *  添加时间
     **/
    private Date addTime;

    /**
     * 跳转方式，0-当前页签；1-新页签'
     * @return
     */
    private Integer linkType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getBannerName() {
        return bannerName;
    }

    public void setBannerName(String bannerName) {
        this.bannerName = bannerName;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCheckUser() {
        return checkUser;
    }

    public void setCheckUser(String checkUser) {
        this.checkUser = checkUser;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Integer getLinkType() {
        return linkType;
    }

    public void setLinkType(Integer linkType) {
        this.linkType = linkType;
    }
}

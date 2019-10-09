package com.world.model.entity;

import com.world.data.mysql.Bean;

import java.util.Date;

/**
 * @author zhangwt
 * @date 2018/5/16 11:33
 */
public class FriendUrl extends Bean {

    public static final int FRIENDURL_ENABLE = 0;
    public static final int FRIENDURL_DISENABLE = 1;


    //主键
    private Long id;
    //是否启用0-是；1-否
    private Integer enableflag;
    //顺序
    private Integer ordernum;
    //图标
    private String icon;
    //名称
    private String name;
    //地址
    private String url;

    private Date updatetime;

    public FriendUrl() {
    }

    public FriendUrl(String name, String url, String icon, int orderNum) {
        this.name = name;
        this.url = url;
        this.icon =icon;
        this.ordernum = orderNum;
        this.enableflag = FRIENDURL_ENABLE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEnableflag() {
        return enableflag;
    }

    public void setEnableflag(Integer enableflag) {
        this.enableflag = enableflag;
    }

    public Integer getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(Integer ordernum) {
        this.ordernum = ordernum;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}

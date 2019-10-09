package com.world.model.entity;

import com.world.data.mysql.Bean;

import java.util.Date;

/**
 * <p>@Description: </p>
 *
 * @author shujianfang
 * @date 2018/7/17下午1:45
 */
public class BannerRelation extends Bean {
    /**
     * 主键
     **/
    private Integer id;

    /**
     * 主键
     **/
    private Integer groupid;

    /**
     * 主键
     **/
    private Integer photoid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupid() {
        return groupid;
    }

    public void setGroupid(Integer groupid) {
        this.groupid = groupid;
    }

    public Integer getPhotoid() {
        return photoid;
    }

    public void setPhotoid(Integer photoid) {
        this.photoid = photoid;
    }
}

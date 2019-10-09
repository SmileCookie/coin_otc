package com.world.model.entity.user;

import com.world.data.mysql.Bean;

/**
 * 用户收藏主表
 */
public class CollectMarket extends Bean {

    private int id;//主键
    private String userId;//用户Id
    private String collect;//收藏市场清单:1,2,3,4

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCollect() {
        return collect;
    }

    public void setCollect(String collect) {
        this.collect = collect;
    }
}

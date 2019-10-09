package com.world.model.entity.user;

import com.world.data.mysql.Bean;

/**
 * @ClassName ScreenMarket
 * @Description
 * @Author kinghao
 * @Date 2018/8/25   10:42
 * @Version 1.0
 * @Description
 */
public class ScreenMarket extends Bean {

    private int id;//主键
    private String userId;//用户Id
    private String screen;//收藏市场清单:1,2,3,4

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

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }
}

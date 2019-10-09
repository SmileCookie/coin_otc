package com.world.model.entity.backcapital;

import com.world.data.mysql.Bean;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/3/19下午5:48
 */
public class PrivateKeyCoords extends Bean {
    private long id;
    private int x;
    private int y;

    public String getXy() {
        return x + "," + y;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}

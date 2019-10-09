package com.world.model.entity.coin;

import com.world.data.mysql.Bean;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/7/24下午7:21
 */
public class CoinIntroduction extends Bean {
    private long id;
    private String coinName;
    private String fullName;
    private String brief;
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

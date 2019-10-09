package com.world.model.entity.recommendcoin;

import com.world.data.mysql.Bean;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2019/1/253:22 PM
 */
public class RecommendCoin extends Bean{
    /**
     * 主键
     */
    private Integer id ;
    /**
     * 市场
     */
    private String entrustMarket;
    /**
     * 推荐时间
     */
    private Long dateTime;
    /**
     * '0：未推荐  1：已推荐
     */
    private int recommend;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEntrustMarket() {
        return entrustMarket;
    }

    public void setEntrustMarket(String entrustMarket) {
        this.entrustMarket = entrustMarket;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }
}

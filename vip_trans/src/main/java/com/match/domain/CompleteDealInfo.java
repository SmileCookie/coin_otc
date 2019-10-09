package com.match.domain;

import com.world.model.Market;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/12/5 2:10 PM
 */
public class CompleteDealInfo {
    private long entrustId;
    private int userId;
    private Market market;

    public CompleteDealInfo(long entrustId, int userId, Market market) {
        this.entrustId = entrustId;
        this.userId = userId;
        this.market = market;
    }

    public long getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(long entrustId) {
        this.entrustId = entrustId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }
}

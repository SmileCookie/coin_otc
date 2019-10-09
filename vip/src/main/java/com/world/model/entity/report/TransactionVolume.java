package com.world.model.entity.report;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

/**
 * @ClassName TransactionVolume
 * @Description
 * @Author kinghao
 * @Date 2018/8/11   14:05
 * @Version 1.0
 * @Description
 */
public class TransactionVolume extends Bean {

    /**
     * 市场  ex：BTC/ETH
     **/
    private String marketName;

    /**
     * 交易量
     **/
    private BigDecimal volumeCache;

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public BigDecimal getVolumeCache() {
        return volumeCache;
    }

    public void setVolumeCache(BigDecimal volumeCache) {
        this.volumeCache = volumeCache;
    }
}

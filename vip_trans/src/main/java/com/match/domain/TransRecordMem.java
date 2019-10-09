package com.match.domain;

import com.world.model.Market;
import com.world.model.entitys.record.TransRecord;

/**
 * <p>@Description: 成交记录内存类</p>
 *
 * @author buxianguan
 * @date 2018/6/19下午8:48
 */
public class TransRecordMem {
    private TransRecord transRecord;
    private Market market;

    public TransRecordMem(TransRecord transRecord, Market market) {
        this.transRecord = transRecord;
        this.market = market;
    }

    public TransRecord getTransRecord() {
        return transRecord;
    }

    public void setTransRecord(TransRecord transRecord) {
        this.transRecord = transRecord;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }
}

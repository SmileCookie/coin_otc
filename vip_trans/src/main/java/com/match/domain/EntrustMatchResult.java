package com.match.domain;

import com.world.model.Market;

import java.util.List;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/11/8 3:56 PM
 */
public class EntrustMatchResult {
    private Market market;
    private List<EntrustUpdateInfo> entrustUpdateInfoList;
    private TransRecordInfo transRecordInfo;
    private TransFundsBack transFundsBack;

    public EntrustMatchResult() {
    }

    public EntrustMatchResult(Market market, List<EntrustUpdateInfo> entrustUpdateInfoList,
                              TransRecordInfo transRecordInfo, TransFundsBack transFundsBack) {
        this.market = market;
        this.entrustUpdateInfoList = entrustUpdateInfoList;
        this.transRecordInfo = transRecordInfo;
        this.transFundsBack = transFundsBack;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public List<EntrustUpdateInfo> getEntrustUpdateInfoList() {
        return entrustUpdateInfoList;
    }

    public void setEntrustUpdateInfoList(List<EntrustUpdateInfo> entrustUpdateInfoList) {
        this.entrustUpdateInfoList = entrustUpdateInfoList;
    }

    public TransRecordInfo getTransRecordInfo() {
        return transRecordInfo;
    }

    public void setTransRecordInfo(TransRecordInfo transRecordInfo) {
        this.transRecordInfo = transRecordInfo;
    }

    public TransFundsBack getTransFundsBack() {
        return transFundsBack;
    }

    public void setTransFundsBack(TransFundsBack transFundsBack) {
        this.transFundsBack = transFundsBack;
    }
}

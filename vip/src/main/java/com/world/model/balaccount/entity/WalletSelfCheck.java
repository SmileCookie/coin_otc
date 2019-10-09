package com.world.model.balaccount.entity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class WalletSelfCheck {
    private Map<Integer,BigDecimal> warmRechargeMap = new HashMap<>();
    private Map<Integer,BigDecimal> warmRechargeFinaccwalletbillMap = new HashMap<>();
    private Map<Integer,BigDecimal> warmWithdrawMap = new HashMap<>();
    private Map<Integer,BigDecimal> warmWithdrawFinaccwalletbillMap = new HashMap<>();
    private Map<Integer,BigDecimal> coldValueMap = new HashMap<>();
    private Map<Integer,BigDecimal> browserBalanceMap = new HashMap<>();
    private Map<Integer, String> apiReqFlag = new HashMap<Integer, String>();

    public Map<Integer, BigDecimal> getWarmRechargeMap() {
        return warmRechargeMap;
    }

    public void setWarmRechargeMap(Map<Integer, BigDecimal> warmRechargeMap) {
        this.warmRechargeMap = warmRechargeMap;
    }

    public Map<Integer, BigDecimal> getWarmRechargeFinaccwalletbillMap() {
        return warmRechargeFinaccwalletbillMap;
    }

    public void setWarmRechargeFinaccwalletbillMap(Map<Integer, BigDecimal> warmRechargeFinaccwalletbillMap) {
        this.warmRechargeFinaccwalletbillMap = warmRechargeFinaccwalletbillMap;
    }

    public Map<Integer, BigDecimal> getWarmWithdrawMap() {
        return warmWithdrawMap;
    }

    public void setWarmWithdrawMap(Map<Integer, BigDecimal> warmWithdrawMap) {
        this.warmWithdrawMap = warmWithdrawMap;
    }

    public Map<Integer, BigDecimal> getWarmWithdrawFinaccwalletbillMap() {
        return warmWithdrawFinaccwalletbillMap;
    }

    public void setWarmWithdrawFinaccwalletbillMap(Map<Integer, BigDecimal> warmWithdrawFinaccwalletbillMap) {
        this.warmWithdrawFinaccwalletbillMap = warmWithdrawFinaccwalletbillMap;
    }

    public Map<Integer, BigDecimal> getColdValueMap() {
        return coldValueMap;
    }

    public void setColdValueMap(Map<Integer, BigDecimal> coldValueMap) {
        this.coldValueMap = coldValueMap;
    }

    public Map<Integer, BigDecimal> getBrowserBalanceMap() {
        return browserBalanceMap;
    }

    public void setBrowserBalanceMap(Map<Integer, BigDecimal> browserBalanceMap) {
        this.browserBalanceMap = browserBalanceMap;
    }

	public Map<Integer, String> getApiReqFlag() {
		return apiReqFlag;
	}

	public void setApiReqFlag(Map<Integer, String> apiReqFlag) {
		this.apiReqFlag = apiReqFlag;
	}
}

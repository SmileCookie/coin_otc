package com.world.model.entity.pay;

import java.math.BigDecimal;

import com.world.data.mysql.Bean;

@SuppressWarnings("serial")
public class UserFunds extends Bean{
	public UserFunds(){}
	public UserFunds(String userId) {
		super();
		this.userId = userId;
	}
	
	private String userId;
    private String userName;
    //总资产
    private BigDecimal totalAssets = BigDecimal.ZERO;
    //净资产
    private BigDecimal netAssets = BigDecimal.ZERO;

    //BTC
    private BigDecimal availableBtc = BigDecimal.ZERO;
    private BigDecimal freezBtc = BigDecimal.ZERO;
    private BigDecimal canBtc = BigDecimal.ZERO;

    //ETH
    private BigDecimal availableEth = BigDecimal.ZERO;
    private BigDecimal freezEth = BigDecimal.ZERO;
    private BigDecimal canEth = BigDecimal.ZERO;
    
    //ETC
    private BigDecimal availableEtc = BigDecimal.ZERO;
    private BigDecimal freezEtc = BigDecimal.ZERO;
    private BigDecimal canEtc = BigDecimal.ZERO;
    
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public BigDecimal getTotalAssets() {
		return totalAssets;
	}
	public void setTotalAssets(BigDecimal totalAssets) {
		this.totalAssets = totalAssets;
	}
	public BigDecimal getNetAssets() {
		return netAssets;
	}
	public void setNetAssets(BigDecimal netAssets) {
		this.netAssets = netAssets;
	}
	public BigDecimal getAvailableBtc() {
		return availableBtc;
	}
	public void setAvailableBtc(BigDecimal availableBtc) {
		this.availableBtc = availableBtc;
	}
	public BigDecimal getFreezBtc() {
		return freezBtc;
	}
	public void setFreezBtc(BigDecimal freezBtc) {
		this.freezBtc = freezBtc;
	}
	public BigDecimal getCanBtc() {
		return canBtc;
	}
	public void setCanBtc(BigDecimal canBtc) {
		this.canBtc = canBtc;
	}
	public BigDecimal getAvailableEth() {
		return availableEth;
	}
	public void setAvailableEth(BigDecimal availableEth) {
		this.availableEth = availableEth;
	}
	public BigDecimal getFreezEth() {
		return freezEth;
	}
	public void setFreezEth(BigDecimal freezEth) {
		this.freezEth = freezEth;
	}
	public BigDecimal getCanEth() {
		return canEth;
	}
	public void setCanEth(BigDecimal canEth) {
		this.canEth = canEth;
	}
	public BigDecimal getAvailableEtc() {
		return availableEtc;
	}
	public void setAvailableEtc(BigDecimal availableEtc) {
		this.availableEtc = availableEtc;
	}
	public BigDecimal getFreezEtc() {
		return freezEtc;
	}
	public void setFreezEtc(BigDecimal freezEtc) {
		this.freezEtc = freezEtc;
	}
	public BigDecimal getCanEtc() {
		return canEtc;
	}
	public void setCanEtc(BigDecimal canEtc) {
		this.canEtc = canEtc;
	}
 }   


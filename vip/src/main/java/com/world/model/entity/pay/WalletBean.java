package com.world.model.entity.pay;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.world.data.mysql.Bean;

public class WalletBean extends Bean{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long walletId; 
	private int keysNumber; 
	private String name;
	private String privateKey; 
	private BigDecimal btcs;
	private Timestamp createDate;
	private int maxKeyNums;
	private int hasUsedNums;
	private boolean withdraw;//是否用于提现
	
	private String rpcIp;//RPC IP地址
	private String rpcPort;//RPC 端口
	private String sendAddress;//打币地址
	private int confirmTimes;//确认次数
	private int targetTimes; //
	
	public BigDecimal getBalance(){
		return btcs;
	}
	
	public boolean isWithdraw() {
		return withdraw;
	}
	public void setWithdraw(boolean withdraw) {
		this.withdraw = withdraw;
	}
	public int getHasUsedNums() {
		return hasUsedNums;
	}
	public void setHasUsedNums(int hasUsedNums) {
		this.hasUsedNums = hasUsedNums;
	}
	public int getMaxKeyNums() {
		return maxKeyNums;
	}
	public void setMaxKeyNums(int maxKeyNums) {
		this.maxKeyNums = maxKeyNums;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public long getWalletId() {
		return walletId;
	}
	public void setWalletId(long walletId) {
		this.walletId = walletId;
	}
	public int getKeysNumber() {
		return keysNumber;
	}
	
	public void setKeysNumber(int keysNumber) {
		this.keysNumber = keysNumber;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	public BigDecimal getBtcs() {
		return btcs;
	}
	public void setBtcs(BigDecimal btcs) {
		this.btcs = btcs;
	}

	public String getRpcIp() {
		return rpcIp;
	}

	public void setRpcIp(String rpcIp) {
		this.rpcIp = rpcIp;
	}

	public String getRpcPort() {
		return rpcPort;
	}

	public void setRpcPort(String rpcPort) {
		this.rpcPort = rpcPort;
	}

	public String getSendAddress() {
		return sendAddress;
	}

	public void setSendAddress(String sendAddress) {
		this.sendAddress = sendAddress;
	}

	public int getConfirmTimes() {
		return confirmTimes;
	}

	public void setConfirmTimes(int confirmTimes) {
		this.confirmTimes = confirmTimes;
	}

	public int getTargetTimes() {
		return targetTimes;
	}

	public void setTargetTimes(int targetTimes) {
		this.targetTimes = targetTimes;
	}
	
}

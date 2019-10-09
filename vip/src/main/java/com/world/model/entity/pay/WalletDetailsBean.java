package com.world.model.entity.pay;

import java.sql.Timestamp;

import com.world.data.mysql.Bean;

public class WalletDetailsBean extends Bean{
	
	private static final long serialVersionUID = 3803436097412708351L;
	private int id;
	private int walletId;//钱包ID
	private int ioType;//1收入 2 支出
	private long amount;//交易金额
	private long fee;//手续费
	private long walletAmount;
	private String des;//描述
	private Timestamp addDate;//时间

	public long getWalletAmount() {
		return walletAmount;
	}

	public void setWalletAmount(long walletAmount) {
		this.walletAmount = walletAmount;
	}

	public Timestamp getAddDate() {
		return addDate;
	}

	public void setAddDate(Timestamp addDate) {
		this.addDate = addDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getWalletId() {
		return walletId;
	}

	public void setWalletId(int walletId) {
		this.walletId = walletId;
	}

	public int getIoType() {
		return ioType;
	}

	public void setIoType(int ioType) {
		this.ioType = ioType;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public long getFee() {
		return fee;
	}

	public void setFee(long fee) {
		this.fee = fee;
	}
}

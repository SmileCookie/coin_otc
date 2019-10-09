package com.world.model.entitys.entrust;

import com.world.data.big.table.TableInfo;
import com.world.data.mysql.Bean;

@TableInfo(databases = {"ltcbtcentrust","etcbtcentrust","ethbtcentrust","zecbtcentrust","dashbtcentrust", "gbcusdtentrust","btcusdtentrust","ethusdtentrust","ltcusdtentrust","etcusdtentrust","zecusdtentrust","dashusdtentrust"} , tableName = "entrust_other" , primaryKey = "id")
public class EntrustOtherBean extends Bean{
	private long id;
	private int userId;
	private long customerOrderId;
	private long entrustId;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public long getCustomerOrderId() {
		return customerOrderId;
	}
	public void setCustomerOrderId(long customerOrderId) {
		this.customerOrderId = customerOrderId;
	}
	public long getEntrustId() {
		return entrustId;
	}
	public void setEntrustId(long entrustId) {
		this.entrustId = entrustId;
	}
	
}

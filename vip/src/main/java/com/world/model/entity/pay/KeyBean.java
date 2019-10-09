package com.world.model.entity.pay;

import java.sql.Timestamp;

import com.world.data.mysql.Bean;

/**
 * 充值地址实体
 * @author Jackie Xu
 *
 */
public class KeyBean  extends Bean{
	private static final long serialVersionUID = 1592195360760392139L;
	
	private int keyId;  
	private String keyPre;  
	private long userId; 
	private String userName;
	private Timestamp createTime;
	private String wallet;
	private int usedTimes;
	private int tag;
	private String addressTag;

	public int getKeyId() {
		return keyId;
	}
	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}
	public int getUsedTimes() {
		return usedTimes;
	}
	public void setUsedTimes(int usedTimes) {
		this.usedTimes = usedTimes;
	}
	public String getWallet() {
		return wallet;
	}
	public void setWallet(String wallet) {
		this.wallet = wallet;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getKeyPre() {
		return keyPre;
	}
	public void setKeyPre(String keyPre) {
		this.keyPre = keyPre;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}

	public String getAddressTag() {
		return addressTag;
	}

	public void setAddressTag(String addressTag) {
		this.addressTag = addressTag;
	}
}

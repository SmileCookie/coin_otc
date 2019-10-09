package com.world.model.entity.pay;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.sql.Timestamp;
public class ReceiveAddr extends Bean{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ReceiveAddr(){
		super();
	}
	
	public ReceiveAddr(String address, String userId, String userName, BigDecimal totalAmount, Timestamp createTime, int isChecked, String memo) {
		super();
		this.address = address;
		this.userId = userId;
		this.userName = userName;
		this.totalAmount = totalAmount;
		this.createTime = createTime;
		this.isChecked = isChecked;
		this.memo = memo;
	}

	public ReceiveAddr(String address, String userId, String userName, BigDecimal totalAmount, Timestamp createTime, int isChecked, String memo,String addressTag,Integer agreement) {
		super();
		this.address = address;
		this.userId = userId;
		this.userName = userName;
		this.totalAmount = totalAmount;
		this.createTime = createTime;
		this.isChecked = isChecked;
		this.memo = memo;
		this.addressTag = addressTag;
		this.agreement = agreement;
	}

	private long id; 
	private String address; 
	private String userId;
	private String userName;
	private BigDecimal totalAmount; 
	private Timestamp createTime;
	private int isChecked;
	private int isDefault;
	private int isDeleted;
	private String memo;
	private int auth;
	//地址标签
	private String addressTag;

	//是否锁定，非数据库字段，通过时间计算是否超过锁定时间
	private int lockStatus = 0;

	private Integer agreement;

	private Boolean canWithdraw = true;

	public Boolean getCanWithdraw() {
		return canWithdraw;
	}

	public void setCanWithdraw(Boolean canWithdraw) {
		this.canWithdraw = canWithdraw;
	}

	public Integer getAgreement() {
		return agreement;
	}

	public void setAgreement(Integer agreement) {
		this.agreement = agreement;
	}

	public int getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
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
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public int getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public int getIsChecked() {
		return isChecked;
	}

	public void setIsChecked(int isChecked) {
		this.isChecked = isChecked;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public int getAuth() {
		return auth;
	}
	public void setAuth(int auth) {
		this.auth = auth;
	}

	public String getAddressTag() {
		return addressTag;
	}

	public void setAddressTag(String addressTag) {
		this.addressTag = addressTag;
	}

	public int getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(int lockStatus) {
        this.lockStatus = lockStatus;
    }
}

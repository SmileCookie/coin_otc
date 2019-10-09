package com.world.model.entity.pay;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.world.data.mysql.Bean;
import com.world.data.mysql.bean.BeanField;

/***
 * 用户存款相关
 * @author apple
 *
 */
public class UserStorage extends Bean{
	public UserStorage(){}
	
	public UserStorage(String userId, int stype, BigDecimal storage,
			BigDecimal storageLixi, BigDecimal storageOfJiXi, BigDecimal lastLixi) {
		this.userId = userId;
		this.stype = stype;
		this.storage = storage;
		this.storageLixi = storageLixi;
		this.storageOfJiXi = storageOfJiXi;
		this.lastLixi = lastLixi;
	}

	public UserStorage(int stype, BigDecimal storage, BigDecimal storageLixi, BigDecimal storageOfJiXi) {
		super();
		this.stype = stype;
		this.storage = storage;
		this.storageLixi = storageLixi;
		this.storageOfJiXi = storageOfJiXi;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 639236594731016909L;
	private String userId;//用户id
	private String userName;//用户名
	private int stype;//存储类型   如:3月定期   6月定期  12月定期
	
	////////////////////定存相关////////////////////
	private BigDecimal storage;    //定存数量
	private BigDecimal storageLixi;//存币获利
	private BigDecimal storageOfJiXi;//计息中的存款
	private BigDecimal convertLixiCoin;//折合发息类型货币的金额  按存入时的币价格折算
	private Timestamp nextFaXiDate;//下次发放利息的时间
	private BigDecimal lastLixi;//最后发息金额
	
	@BeanField(persistence=false)
	private int level;	//推荐奖励用, 不需持久化

	public BigDecimal getLastLixi() {
		return lastLixi;
	}

	public void setLastLixi(BigDecimal lastLixi) {
		this.lastLixi = lastLixi;
	}

	public BigDecimal getConvertLixiCoin() {
		return convertLixiCoin;
	}

	public void setConvertLixiCoin(BigDecimal convertLixiCoin) {
		this.convertLixiCoin = convertLixiCoin;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getStype() {
		return stype;
	}

	public void setStype(int stype) {
		this.stype = stype;
	}

	public BigDecimal getStorage() {
		if(storage == null)
			storage = BigDecimal.ZERO;
		return storage;
	}

	public void setStorage(BigDecimal storage) {
		this.storage = storage;
	}

	public BigDecimal getStorageLixi() {
		if(storageLixi == null)
			storageLixi = BigDecimal.ZERO;
		return storageLixi;
	}

	public void setStorageLixi(BigDecimal storageLixi) {
		this.storageLixi = storageLixi;
	}

	public BigDecimal getStorageOfJiXi() {
		if(storageOfJiXi == null)
			storageOfJiXi = BigDecimal.ZERO;
		return storageOfJiXi;
	}

	public void setStorageOfJiXi(BigDecimal storageOfJiXi) {
		this.storageOfJiXi = storageOfJiXi;
	}

	public Timestamp getNextFaXiDate() {
		return nextFaXiDate;
	}

	public void setNextFaXiDate(Timestamp nextFaXiDate) {
		this.nextFaXiDate = nextFaXiDate;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}

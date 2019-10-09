package com.world.model.entity.level;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;

import com.world.data.mysql.bean.BeanField;
import org.apache.commons.lang.StringUtils;

import com.Lan;
import com.world.data.mysql.Bean;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.SysEnum;

public class Jifen extends Bean{
	private int id;
	private String userId;
	private String userName;
	private BigDecimal jifen = BigDecimal.ZERO;	//积分
	private String memo;	//描述
	private int type;	//积分类型, 对应JifenType
	private int ioType;	//进出类型,0:获得, 1:消费 
	private int status;	//状态:  0正常, 1减半,  2清零
	private Timestamp addTime;	//添加时间
	
	private int parentId;	//如果是减半/清零 状态的,则记录相关联的id (不用)
	private BigDecimal balance = BigDecimal.ZERO;	//结余积分	 (不用)
	private int continuityLoginTimes;	//连续登录天数
	// 临时字段，不需要持久化到数据库中
	@BeanField(persistence = false)
	private String typeValue;//积分类型值-根据中英文变化，临时属性

	@BeanField(persistence = false)
	private String typeShowNew;//积分类型值-根据中英文变化，临时属性
	public Jifen() {
	}
	
	public Jifen(String userId, String userName, BigDecimal jifen, String memo, int type, int ioType, Timestamp addTime) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.jifen = jifen;
		this.memo = memo;
		this.type = type;
		this.ioType = ioType;
		this.addTime = addTime;
	}
	
	public String getTypeValue(String lan){
		return Lan.Language(lan, this.getTypeShow());
	}
	
	public String getTypeValue() {
		if (StringUtils.isBlank(typeValue)) {
			return this.getTypeShow();
		}
		return typeValue;
	}

	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public BigDecimal getJifen() {
		return jifen;
	}
	public void setJifen(BigDecimal jifen) {
		this.jifen = jifen;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTypeShow() {
		String showType ="";
		SysEnum sysEnum = EnumUtils.getEnumByKey(type, JifenType.class);
		if(sysEnum!=null){
			showType = sysEnum.getValue();
		}
		return showType;
	}
	public int getIoType() {
		return ioType;
	}
	public void setIoType(int ioType) {
		this.ioType = ioType;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public Timestamp getAddTime() {
		return addTime;
	}
	public void setAddTime(Timestamp addTime) {
		this.addTime = addTime;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public int getContinuityLoginTimes() {
		return continuityLoginTimes;
	}

	public void setContinuityLoginTimes(int continuityLoginTimes) {
		this.continuityLoginTimes = continuityLoginTimes;
	}

	public String getTypeShowNew() {
		return typeShowNew;
	}

	public void setTypeShowNew(String typeShowNew) {
		this.typeShowNew = typeShowNew;
	}
}

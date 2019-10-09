package com.world.model.entity.app;

import java.util.Date;

import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.LongIdEntity;


@Entity(noClassnameStored=true)
public class AppSetting extends LongIdEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7954754849968892906L;
	private boolean isFreeFee;//是否免手续费
	private int areaVersion;//地区版本
	private int rechargeBankVersion;//充值银行版本
	private int countryInfoVersion;//国家信息版本
	private Date createtime;//上传时间
	private Date modifytime;//上传时间
	
	public boolean isFreeFee() {
		return isFreeFee;
	}
	public void setFreeFee(boolean isFreeFee) {
		this.isFreeFee = isFreeFee;
	}
	public int getCountryInfoVersion() {
		return countryInfoVersion;
	}
	public void setCountryInfoVersion(int countryInfoVersion) {
		this.countryInfoVersion = countryInfoVersion;
	}
	public int getAreaVersion() {
		return areaVersion;
	}
	public void setAreaVersion(int areaVersion) {
		this.areaVersion = areaVersion;
	}
	public int getRechargeBankVersion() {
		return rechargeBankVersion;
	}
	public void setRechargeBankVersion(int rechargeBankVersion) {
		this.rechargeBankVersion = rechargeBankVersion;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Date getModifytime() {
		return modifytime;
	}
	public void setModifytime(Date modifytime) {
		this.modifytime = modifytime;
	}
}

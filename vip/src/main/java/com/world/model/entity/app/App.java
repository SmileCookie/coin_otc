package com.world.model.entity.app;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.LongIdEntity;


@Entity(noClassnameStored=true)
public class App extends LongIdEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7954754849968892906L;
	private String name;//版本名称
	private String num;//版本号
	private String type;//ios，android
	private String url;//版本URL
	private String remark;//升级日志
	private Timestamp datetime;//上传时间
	private String key;//appKey
	private String secret;//appSecret
	private boolean released;//released

	private String size;//包大小
	private Timestamp updateDatetime;//更新时间

	private String cnName;
	private String enName;
	private String hkName;

	private String cnRemark;
	private String enRemark;
	private String hkRemark;
	private boolean isEnforceUpdate = false;

	public boolean getReleased() {
		return released;
	}
	public void setReleased(boolean released) {
		this.released = released;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRemark() {
		if(StringUtils.isEmpty(remark)) remark="";
		return remark;
	}
	public String getUpdateTime() {
		 SimpleDateFormat sdf=new SimpleDateFormat("yyyy年-MM月-dd日");
		 return sdf.format(datetime);
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Timestamp getDatetime() {
		return datetime;
	}
	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Timestamp getUpdateDatetime() {
		return updateDatetime;
	}

	public void setUpdateDatetime(Timestamp updateDatetime) {
		this.updateDatetime = updateDatetime;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getHkName() {
		return hkName;
	}

	public void setHkName(String hkName) {
		this.hkName = hkName;
	}

	public String getCnRemark() {
		return cnRemark;
	}

	public void setCnRemark(String cnRemark) {
		this.cnRemark = cnRemark;
	}

	public String getEnRemark() {
		return enRemark;
	}

	public void setEnRemark(String enRemark) {
		this.enRemark = enRemark;
	}

	public String getHkRemark() {
		return hkRemark;
	}

	public void setHkRemark(String hkRemark) {
		this.hkRemark = hkRemark;
	}

	public boolean isEnforceUpdate() {
		return isEnforceUpdate;
	}

	public void setEnforceUpdate(boolean enforceUpdate) {
		isEnforceUpdate = enforceUpdate;
	}
}

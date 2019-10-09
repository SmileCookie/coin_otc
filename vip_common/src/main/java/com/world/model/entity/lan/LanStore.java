package com.world.model.entity.lan;

import java.sql.Timestamp;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Indexed;
import com.world.data.mongo.id.StrBaseLongIdEntity;

/****
 * 语言包主体包
 * 
 * @author Administrator
 * 
 */
@Entity(noClassnameStored = true , value = "LanStore")
public class LanStore extends StrBaseLongIdEntity {
	
	public LanStore() {
		
	}

	public LanStore(Datastore ds) {
		super(ds);
	}

	private static final long serialVersionUID = -2386115543279873656L;
	
	//键
	@Indexed(name = "_lan_store_key_index_", unique = true)
	private String key;
	
	//中文值
	private String cnValue;
	
	//英文值
	private String enValue;
	
	//是否缓存当前语言下的值，0、不缓存，1、缓存
	private int isCacheValue;
	
	private Timestamp addTime;
	

	public String getValue(String lan){
		if(lan == null) return getCnValue();
		if("en".equalsIgnoreCase(lan))
			return getEnValue();
		else 
			return getCnValue();
	}
	
	public void setValue(String lan, String value){
		if(lan == null)  this.setCnValue(value);
		if("en".equalsIgnoreCase(lan))
			this.setEnValue(value);
		else 
			this.setCnValue(value);
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCnValue() {
		if(cnValue == null) cnValue = key;
		return cnValue;
	}

	public void setCnValue(String cnValue) {
		this.cnValue = cnValue;
	}

	public String getEnValue() {
		if(enValue == null) enValue = key;
		return enValue;
	}

	public void setEnValue(String enValue) {
		this.enValue = enValue;
	}

	public int getIsCacheValue() {
		return isCacheValue;
	}

	public void setIsCacheValue(int isCacheValue) {
		this.isCacheValue = isCacheValue;
	}

	public Timestamp getAddTime() {
		return addTime;
	}

	public void setAddTime(Timestamp addTime) {
		this.addTime = addTime;
	}
	
}

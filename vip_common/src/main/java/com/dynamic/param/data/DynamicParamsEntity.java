package com.dynamic.param.data;

import com.dynamic.param.BaseDynamicParamsConfig;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;


@Entity(value = "dynamic_params" , noClassnameStored = true)
public class DynamicParamsEntity extends StrBaseLongIdEntity{
	public DynamicParamsEntity() {
	}
	public DynamicParamsEntity(Datastore ds) {
		super(ds);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 8780649929815638021L;
	private String name;
	@Embedded
	private BaseDynamicParamsConfig config;
	private long version;//版本号以更新时刻的服务器时间戳标记
	private int adminId;
	
	public int getAdminId() {
		return adminId;
	}
	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	public BaseDynamicParamsConfig getConfig() {
		return config;
	}
	public void setConfig(BaseDynamicParamsConfig config) {
		this.config = config;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}

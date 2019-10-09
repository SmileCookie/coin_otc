package com.world.model.entity.user;

import java.sql.Timestamp;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

@Entity(noClassnameStored = true , value = "passlog")
public class Passlog extends StrBaseLongIdEntity{

	private static final long serialVersionUID = -6839179453330559967L;
	
	public Passlog(){
		
	}
	
	public Passlog(Datastore ds){
		super(ds);
	}
	
	private String userId;
	private String password;
	private String safePass;
	private int cover;//1为已重置，2为已还原 
	private String adminId;
	private String ip;
	private boolean isDeleted;
	private Timestamp updateTime;
	private Timestamp recoverTime;
	private int type;//1为重置登录密码   2为已还原      重置资金密码是新记录,3为已重置资金密码   4为已还原
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSafePass() {
		return safePass;
	}
	public void setSafePass(String safePass) {
		this.safePass = safePass;
	}
	public int getCover() {
		return cover;
	}
	public void setCover(int cover) {
		this.cover = cover;
	}
	public String getAdminId() {
		return adminId;
	}
	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	public Timestamp getRecoverTime() {
		return recoverTime;
	}
	public void setRecoverTime(Timestamp recoverTime) {
		this.recoverTime = recoverTime;
	}
}

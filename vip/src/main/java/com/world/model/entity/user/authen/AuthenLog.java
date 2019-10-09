package com.world.model.entity.user.authen;

import java.sql.Timestamp;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.user.User;

@Entity(noClassnameStored = true , value = "authenlog")
public class AuthenLog extends StrBaseLongIdEntity{

	private static final long serialVersionUID = -6839179453330559967L;
	
	public AuthenLog(){
		
	}
	
	public AuthenLog(Datastore ds){
		super(ds);
	}
	
	private String userId;
	private String adminId;
	private String des;
	private int type;
	private Timestamp time;
	private String ip;
	
	private User user;
	private AdminUser aUser;
	
	public AuthenType getAuthType(){
		return (AuthenType)EnumUtils.getEnumByKey(type, AuthenType.class);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public AdminUser getaUser() {
		return aUser;
	}

	public void setaUser(AdminUser aUser) {
		this.aUser = aUser;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
}

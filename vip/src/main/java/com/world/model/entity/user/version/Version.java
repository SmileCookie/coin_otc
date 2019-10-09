package com.world.model.entity.user.version;

import java.sql.Timestamp;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.user.User;

@Entity(noClassnameStored = true , value = "version")
public class Version extends StrBaseLongIdEntity{

	private static final long serialVersionUID = -6839179453330559967L;
	
	public Version(){
		
	}
	
	public Version(Datastore ds){
		super(ds);
	}
	
	private String userId;
	private int version;//想要申请的版本   1为专业版    2为贵宾版
	private int status;//申请的状态   普通-专业 （等待审核11   审核通过12  审核不通过  13）      专业-贵宾(等待审核16   审核通过17  审核不通过  18)    10为已撤销
	private String reasons;
	private Timestamp checkTime;
	private Timestamp appleyTime;
	private String adminId; 
	private int oldVersion;//申请之前的版本
	
	private User user;
	private AdminUser aUser;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Timestamp getAppleyTime() {
		return appleyTime;
	}

	public void setAppleyTime(Timestamp appleyTime) {
		this.appleyTime = appleyTime;
	}

	public String getReasons() {
		return reasons;
	}

	public void setReasons(String reasons) {
		this.reasons = reasons;
	}

	public Timestamp getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(Timestamp checkTime) {
		this.checkTime = checkTime;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
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

	public int getOldVersion() {
		return oldVersion;
	}

	public void setOldVersion(int oldVersion) {
		this.oldVersion = oldVersion;
	}
	
}

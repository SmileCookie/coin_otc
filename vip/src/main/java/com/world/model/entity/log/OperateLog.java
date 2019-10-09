package com.world.model.entity.log;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.user.User;

import java.sql.Timestamp;

/**
 * 操作日志
 */
@Entity(noClassnameStored = true , value = "operateLog")
public class OperateLog extends StrBaseLongIdEntity{

	public OperateLog(){

	}

	public OperateLog(Datastore ds){
		super(ds);
	}
	
	private String userId;

	private String userName;
	/** 类别 */
	private int category;
	/**内容*/
	private String content;

	private Timestamp time;

	private String ip;

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

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
}

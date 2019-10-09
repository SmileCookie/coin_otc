package com.world.model.entity.user;

import java.sql.Timestamp;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;


@Entity(noClassnameStored = true , value = "user_login_ip")
public class UserLoginIp extends StrBaseLongIdEntity {
	public UserLoginIp() {
		super();
	}
	public UserLoginIp(Datastore ds) {
		super(ds);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 4910192847543508026L;
	private String userId;
	private String ip;
	private String city;
	private Timestamp date;
	private int checked;
	private int terminal;// 终端 1:网页 2:app
	private String describe;// 备注，记录登录的详细描述
	
	private User user;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public int getChecked() {
		return checked;
	}
	public void setChecked(int checked) {
		this.checked = checked;
	}
	public int getTerminal() {
		if (terminal == 0) {
			return 1;
		}
		return terminal;
	}
	public void setTerminal(int terminal) {
		this.terminal = terminal;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	
}

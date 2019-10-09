package com.world.model.entity.user;

import java.sql.Timestamp;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.controller.combined.RegType;
import com.world.data.mongo.id.StrBaseLongIdEntity;
import com.world.model.entity.EnumUtils;
/**
 * 联合登录的用户信息
 * @author apple
 *
 */

@Entity(noClassnameStored = true , value = "combined")
public class Combined extends StrBaseLongIdEntity {
	public Combined(Datastore ds) {
		super(ds);
	}
	
	public Combined() {
	}
	
	private static final long serialVersionUID = 5867067354109308665L;

	private String userId;
	private String nickName;
	private String avatar;
	private int type;
	private String openId;//联合登录的用户标示重要信息
	private String accessToken;
	//0为未注册，1为新注册，2为已绑定
	private int status;
	private Timestamp addTime;
	private Timestamp regTime;//绑定或注册的时间
	
	public RegType getRegType(){
		return (RegType)EnumUtils.getEnumByKey(type, RegType.class);
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Timestamp getAddTime() {
		return addTime;
	}

	public void setAddTime(Timestamp addTime) {
		this.addTime = addTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Timestamp getRegTime() {
		return regTime;
	}

	public void setRegTime(Timestamp regTime) {
		this.regTime = regTime;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

}

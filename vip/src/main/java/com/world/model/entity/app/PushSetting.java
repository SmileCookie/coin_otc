package com.world.model.entity.app;

import java.sql.Timestamp;

import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.LongIdEntity;


@Entity(noClassnameStored=true)
public class PushSetting extends LongIdEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7954754849968892906L;
	private int type;//
	private int isOpen;//状态1开启 0 关闭
	private String sound = "default";
	private String userId;//
	private Timestamp createtime;//设置时间
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getIsOpen() {
		return isOpen;
	}
	public void setIsOpen(int isOpen) {
		this.isOpen = isOpen;
	}
	public String getSound() {
		return sound;
	}
	public void setSound(String sound) {
		this.sound = sound;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Timestamp getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Timestamp createtime) {
		this.createtime = createtime;
	}
	
}

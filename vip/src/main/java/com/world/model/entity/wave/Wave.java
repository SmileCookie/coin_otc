package com.world.model.entity.wave;

import java.sql.Timestamp;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

/***
 * 波动
 * @author Administrator
 *
 */
@Entity(value = "wave" , noClassnameStored = false)
public class Wave extends StrBaseLongIdEntity{
	public Wave() {
	}
	public Wave(Datastore ds) {
		super(ds);
	}

	private static final long serialVersionUID = -6289233546589064498L;

	private int type;//波动类型
	
	private double waveVal;//波动值
	
	private Timestamp date;//设置时间
	
	private String adminId;//设置者管理员ID
	private String des;//描述

	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getWaveVal() {
		return waveVal;
	}

	public void setWaveVal(double waveVal) {
		this.waveVal = waveVal;
	}
}

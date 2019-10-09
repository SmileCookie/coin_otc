package com.world.system.model;

import java.sql.Timestamp;

import org.bson.types.ObjectId;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

@Entity(value = "TipsMsg", noClassnameStored = true)
public class TipsMsg extends StrBaseLongIdEntity{
	
	private static final long serialVersionUID = -2386115543279873656L;
	
	private String content;
	
	private int status;
	
	private Timestamp addTime;
	
	private Timestamp seeTime;
	
	private int seeTimes;

	
	public TipsMsg(){}
	
	public TipsMsg(Datastore ds) {
		super(ds);
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Timestamp getAddTime() {
		return addTime;
	}

	public void setAddTime(Timestamp addTime) {
		this.addTime = addTime;
	}

	public Timestamp getSeeTime() {
		return seeTime;
	}

	public void setSeeTime(Timestamp seeTime) {
		this.seeTime = seeTime;
	}

	public int getSeeTimes() {
		return seeTimes;
	}

	public void setSeeTimes(int seeTimes) {
		this.seeTimes = seeTimes;
	}
}

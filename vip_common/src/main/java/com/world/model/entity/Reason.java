package com.world.model.entity;

import java.sql.Timestamp;

import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

@Entity(noClassnameStored = true , value = "reason")
public class Reason extends StrBaseLongIdEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String adminId;//添加人
	private String cont;//内容
	private Timestamp pubDate;//添加时间
	private int type;//类型
	
	private String showCont;
	public String getShowCont() {
		if(cont.length()>=32){
			showCont=cont.substring(0,31)+"…";
		}else{
			showCont=cont;
		}
		return showCont;
	}
	public void setShowCont(String showCont) {
		this.showCont = showCont;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getAdminId() {
		return adminId;
	}
	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}
	public String getCont() {
		return cont;
	}
	public void setCont(String cont) {
		this.cont = cont;
	}
	public Timestamp getPubDate() {
		return pubDate;
	}
	public void setPubDate(Timestamp pubDate) {
		this.pubDate = pubDate;
	}
}

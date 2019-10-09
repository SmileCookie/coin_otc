package com.world.model.entity.user;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

/****
 * 用户主表数据结构
 * 
 * @author Administrator
 * 
 */
@Entity(noClassnameStored = true , value = "country")
public class Country extends StrBaseLongIdEntity {
	public Country() {
	}

	public Country(Datastore ds) {
		super(ds);
	}

	private static final long serialVersionUID = -2386115543279873656L;
	
	private String name;
	private String des;
	private String code;
	private String position;//样式
	private String seq;//排序

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
}

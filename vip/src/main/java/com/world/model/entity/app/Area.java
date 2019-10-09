package com.world.model.entity.app;

import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.LongIdEntity;

@Entity(noClassnameStored=true)
public class Area extends LongIdEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7954754849968892906L;
	private String name;
	private int parentId;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
}

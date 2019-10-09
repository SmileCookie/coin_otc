package com.world.model.entity.admin.competence.role_plate;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;
/****
 * 角色 - 板块 
 * @author Administrator
 *
 */
@Entity(value = "role_plate" , noClassnameStored = true)
public class RolePlate extends StrBaseLongIdEntity {
	
	public RolePlate() {
		this(null);
	}
	
	public RolePlate(Datastore ds) {
		super(ds);
	}
	
	private String name;//板块名称
	private String des;//板块描述
	private String[] plateIds;//板块包含组
	
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

	public String getId() {
		return myId;
	}

	public void setId(String id) {
		this.myId = id;
	}

	public String[] getPlateIds() {
		return plateIds;
	}

	public void setPlateIds(String[] plateIds) {
		this.plateIds = plateIds;
	}
	
    
}

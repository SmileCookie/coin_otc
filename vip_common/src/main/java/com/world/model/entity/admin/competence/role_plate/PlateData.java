package com.world.model.entity.admin.competence.role_plate;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

@Entity(value = "plate_data" , noClassnameStored = true)
public class PlateData extends StrBaseLongIdEntity {
	public PlateData() {
		this(null);
	}
	
	public PlateData(Datastore ds) {
		super(ds);
	}
	private String path;//类路径
	private String dataId;//数据ID
	private String dataDes;//数据说明
	private boolean inRole;
	
	public boolean isInRole() {
		return inRole;
	}

	public void setInRole(boolean inRole) {
		this.inRole = inRole;
	}

	public String getId() {
		return myId;
	}

	public void setId(String id) {
		this.myId = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public String getDataDes() {
		return dataDes;
	}

	public void setDataDes(String dataDes) {
		this.dataDes = dataDes;
	}

}

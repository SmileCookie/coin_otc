package com.world.model.entity.ip;

import com.google.code.morphia.Datastore;
import com.world.data.mongo.id.StrBaseLongIdEntity;

public class Ips extends StrBaseLongIdEntity{
	public Ips() {
	}
	public Ips(Datastore ds) {
		super(ds);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -695868142468271323L;
	private String ips;

	public String getIps() {
		return ips;
	}

	public void setIps(String ips) {
		this.ips = ips;
	}
}

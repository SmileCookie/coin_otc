package com.world.model.entity.cache;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

@Entity(noClassnameStored = true , value = "mem")
public class Memcached  extends StrBaseLongIdEntity{

	private static final long serialVersionUID = -2386115543279873856L;

	public Memcached() {
		super();
	}
	
	public Memcached(Datastore ds) {
		super(ds);
	}
	
	private String key;//内存键值

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
}

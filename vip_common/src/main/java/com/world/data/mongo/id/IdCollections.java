package com.world.data.mongo.id;

import com.world.model.entity.SysEnum;

public enum IdCollections implements SysEnum{
	inout(1 , "inout"),
	freez(2 , "freez"),
	consume_details(3 , "consume_details");
	
	private int key;
	private String value;

	private IdCollections(int key, String value) {
		this.key = key;
		this.value = value;
	}

	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	
}

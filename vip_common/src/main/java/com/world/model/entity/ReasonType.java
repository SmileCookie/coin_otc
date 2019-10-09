package com.world.model.entity;

public enum ReasonType implements SysEnum {
	
	unpass_special(13, "专业版审核不通过"),
	unpass_guest(18, "贵宾版审核不通过");

	private ReasonType(int key, String value){
		this.key = key;
		this.value = value;
	}
	
	private int key;
	private String value;
	public void setKey(int key) {
		this.key = key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	
}

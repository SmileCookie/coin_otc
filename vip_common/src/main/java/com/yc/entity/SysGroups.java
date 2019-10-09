package com.yc.entity;

/****
 * 系统组，可服务的系统
 * @author Administrator
 *
 */
public enum SysGroups {
	vip(1,"交易平台");
	private SysGroups(int id, String value) {
		this.id = id;
		this.value = value;
	}
	private int id;
	private String value;
	
	public int getId() {
		return id;
	}
	public String getValue() {
		return value;
	}
}

package com.world.model.entity.admin;
/*******
 * 权限板块
 * @author Administrator
 *
 */
public enum PersBoard{
	comment(1,"评论");
	
	private PersBoard(int key, String value) {
		this.key = key;
		this.value = value;
	}
	private int key;
	private String value;
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}

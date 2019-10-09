package com.world.model.entity.level;


import com.world.model.entity.SysEnum;

/****
 * 事项类型----代办任务表(TdTodotask)
 * @author gkl
 *
 */
public enum ItemType implements SysEnum {
	googleVerify(1,"GO","Google审核","更改Google","1"),
	phoneVerify(2,"SJ","手机审核","更改手机","2"),
	identityAuthen(3,"SF","身份认证","认证审核","3");

	private ItemType(int id, String agencyCode, String todoName, String todoNodeName, String url) {
		this.id = id;
		AgencyCode = agencyCode;
		this.todoName = todoName;
		this.todoNodeName = todoNodeName;
		this.url = url;
	}

	/**
	 * id
	 */
	private int id;
	/**
	 * 待办编码
	 */
	private String AgencyCode;
	/**
	 * 事项名
	 */
	private String todoName;
	/**
	 * 事项节点
	 */
	private String todoNodeName;
	/**
	 * 弹框jsp页
	 */
	private String url;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAgencyCode() {
		return AgencyCode;
	}

	public void setAgencyCode(String agencyCode) {
		AgencyCode = agencyCode;
	}

	public String getTodoName() {
		return todoName;
	}

	public void setTodoName(String todoName) {
		this.todoName = todoName;
	}

	public String getTodoNodeName() {
		return todoNodeName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setTodoNodeName(String todoNodeName) {
		this.todoNodeName = todoNodeName;
	}
	public int getKey() {
		return id;
	}

	public String getValue() {
		return null;
	}
}

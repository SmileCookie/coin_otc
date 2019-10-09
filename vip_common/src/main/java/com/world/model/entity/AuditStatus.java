package com.world.model.entity;

public enum AuditStatus implements SysEnum{
	noSubmit(0 , "高级认证-未提交" , "a2nosubmit"),
	noAudite(1 , "高级认证-待审核" , "a2wait"), 
	pass(2 , "高级认证-通过" , "a2pass"),
	noPass(3 , "高级认证-不通过" , "a2nopass"),

	a1NoSubmit(4 , "初级认证-未提交" , "a1nosubmit"),
	a1NoAudite(5 , "初级认证-待审核" , "a1wait"),
	a1Pass(6 , "初级认证-通过" , "a1pass"),
	a1NoPass(7 , "初级认证-不通过" , "a1nopass"),

	c3submit(-1 , "C3认证-未提交" , "c3submit"),
	c3audite(-2 , "C3认证-待审核" , "c3audite"),
	c3pass(-3 , "C3认证-通过" , "c3pass"),
	c3unpass(-4 , "C3认证-不通过" , "c3unpass");



	
	private AuditStatus(int key, String value , String className) {
		this.key = key;
		this.value = value;
		this.className = className;
	}

	private int key;
	private String value;
	private String className;

	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	public String getClassName() {
		return className;
	}
}

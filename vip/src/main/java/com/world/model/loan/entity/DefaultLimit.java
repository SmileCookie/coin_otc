package com.world.model.loan.entity;

import com.world.data.mysql.Bean;
/***
 * @version 把配置的一些固定值，放到数据库里面，不用每次更改配置。
 * @author chenruidong
 *
 */
public class DefaultLimit extends Bean {

	private static final long serialVersionUID = 1L;

	
	public DefaultLimit(){
		super();
	}
	
	
	private int id;
	private String typeName;//类别名称
	private String keyName;//币种
	private String valueName;//值
	private String reMarks;//备注，解释这个默认值归属哪里

	private String[] dLimit;//数组获取
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	public String getValueName() {
		return valueName;
	}
	public void setValueName(String valueName) {
		this.valueName = valueName;
	}
	public String getReMarks() {
		return reMarks;
	}
	public void setReMarks(String reMarks) {
		this.reMarks = reMarks;
	}
	
	public DefaultLimit(String typeName,String keyName,String valueName) {
		super();
		this.typeName=typeName;
		this.keyName=keyName;
		this.valueName=valueName;
	}
	
	public String[] getdLimit() {
		return dLimit;
	}
	public void setdLimit(String[] dLimit) {
		this.dLimit = dLimit;
	}
}

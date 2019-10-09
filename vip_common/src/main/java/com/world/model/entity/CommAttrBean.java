package com.world.model.entity;

import com.world.data.mysql.Bean;

/**
 * <p>
 * 标题: 数据字典表
 * </p>
 * <p>
 * 描述: 数据字典表
 * </p>
 * <p>
 * 版权: Copyright (c) 2017
 * </p>
 * 
 * @author flym
 * @version
 */
public class CommAttrBean extends Bean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/* 主键，自增长 */
	private int attrId;
	/* 属性类型 */
	private int attrType;
	/* 属性类型说明 */
	private String attrTypeDesc;
	/* 参数编号(一般用户二级) */
	private String paraCode;
	/* 参数名称 */
	private String paraName;
	/* 参数值 */
	private String paraValue;
	/* 参数说明 */
	private String paraDesc;
	/* 配合attrId使用 */
	private int parentId;
	/* 属性状态 */
	private int attrState;

	public int getAttrId() {
		return attrId;
	}

	public void setAttrId(int attrId) {
		this.attrId = attrId;
	}

	public int getAttrType() {
		return attrType;
	}

	public void setAttrType(int attrType) {
		this.attrType = attrType;
	}

	public String getAttrTypeDesc() {
		return attrTypeDesc;
	}

	public void setAttrTypeDesc(String attrTypeDesc) {
		this.attrTypeDesc = attrTypeDesc;
	}

	public String getParaCode() {
		return paraCode;
	}

	public void setParaCode(String paraCode) {
		this.paraCode = paraCode;
	}

	public String getParaName() {
		return paraName;
	}

	public void setParaName(String paraName) {
		this.paraName = paraName;
	}

	public String getParaValue() {
		return paraValue;
	}

	public void setParaValue(String paraValue) {
		this.paraValue = paraValue;
	}

	public String getParaDesc() {
		return paraDesc;
	}

	public void setParaDesc(String paraDesc) {
		this.paraDesc = paraDesc;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getAttrState() {
		return attrState;
	}

	public void setAttrState(int attrState) {
		this.attrState = attrState;
	}

	public String getAttrStateDesc() {
		if(attrState == 0){
			return "停用";
		}else if(attrState == 1){
			return "启用";
		}
		return "";
	}

}

package com.world.model.loan.entity;

import com.world.model.entity.SysEnum;

/*****
 * 借款状态
 * @author Administrator
 *
 */
public enum LoanStatus implements SysEnum{
	
	waiting(0 , "未有借入" , "orange") , 
	part(1 , "部分借入" , "orange") , 
	canceled(2 , "已取消" , "gray") , 
	success(3 , "借入成功" , "green"),
	allRepay(4 , "已经收回" , "green")
	; 
	
	private LoanStatus(int key, String value , String color) {
		this.key = key;
		this.value = value;
		this.color = color;
	}
	public int key;
	public String value;
	public String color;
	
	public String getColor() {
		return color;
	}

	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}

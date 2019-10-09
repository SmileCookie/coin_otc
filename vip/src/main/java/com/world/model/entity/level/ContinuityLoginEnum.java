package com.world.model.entity.level;

import java.math.BigDecimal;

import com.world.model.entity.SysEnum;

/**
 *	积分类型 
 */
public enum ContinuityLoginEnum implements SysEnum{
	
	//-1
	
	//首次积分
	day1(0,"连续登录1天","登录","10"),
	day2(1,"连续登录2天","登录","20"),
	day3(2,"连续登录3天","登录","30"),
	day4(3,"连续登录4天","登录","40"),
	day5(4,"连续登录5天","登录","50"),
	day6(5,"连续登录6天","登录","60"),
	day7(6,"连续登录7天","登录","70"),
	;

	private int key;
	private String value;
	private String memo;
	private BigDecimal jifen;		//积分
	
	
	
	private ContinuityLoginEnum(int key, String value,String memo, String jifen) {
		this.key = key;
		this.value = value;
		this.memo = memo;
		this.jifen = new BigDecimal(jifen);
	}
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
	public BigDecimal getJifen() {
		return jifen;
	}
	public void setJifen(BigDecimal jifen) {
		this.jifen = jifen;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}

	
}

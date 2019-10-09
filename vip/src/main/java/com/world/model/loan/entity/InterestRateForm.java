package com.world.model.loan.entity;



import com.world.model.entity.SysEnum;

/****
 * 利率形式   固定  每周增加  每月增加
 * @author apple
 *
 */
public enum InterestRateForm implements SysEnum{
	fixed(1 , "固定") , week(2 , "每周增"), month(3 , "每月增");
	
	private InterestRateForm(int key, String value) {
		this.key = key;
		this.value = value;
	}

	private int key;
	private String value;

	@Override
	public int getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}

}

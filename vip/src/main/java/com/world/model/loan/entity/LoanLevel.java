package com.world.model.loan.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.world.model.entity.SysEnum;
import com.world.util.DigitalUtil;

/****
 * 借贷杠杆类别
 * @author apple
 *
 */
public enum LoanLevel implements SysEnum{
            
//	lever_1(1 , "一倍杠杆" , DigitalUtil.getBigDecimal(1)),
//	lever_1p5(2 , "一点五倍杠杆" , DigitalUtil.getBigDecimal(2.5)) ,
//	lever_2(3 , "两倍杠杆" , DigitalUtil.getBigDecimal(3)) ,
//	lever_2p5(4 , "两点五倍杠杆" , DigitalUtil.getBigDecimal(3.5)) ,
//	lever_3(5 , "三倍杠杆" , DigitalUtil.getBigDecimal(4));

	//可借入资产=净资产 * 杠杆系数
	lever_1(1 , "一倍杠杆" , DigitalUtil.getBigDecimal(1)),
	lever_2(2 , "二杠杠杆" , DigitalUtil.getBigDecimal(2)) ,
	lever_3(3 , "三倍杠杆" , DigitalUtil.getBigDecimal(3)) ,
	lever_4(4 , "四倍杠杆" , DigitalUtil.getBigDecimal(4)) ,
	lever_5(5 , "五倍杠杆" , DigitalUtil.getBigDecimal(5));

	private LoanLevel(int key, String value, BigDecimal bs) {
		this.key = key;
		this.value = value;
		this.bs = bs;
	}

	private int key;
	private String value;
	private BigDecimal bs;//借贷倍数 ,就是最多全部借入后  是 本金的总倍数
	public BigDecimal getInBili(){
//		return bs.subtract(BigDecimal.ONE);
		//
		return bs;
	}
	///基数比例  就是借贷资金/本金
	public BigDecimal getBaseBili(){
		return bs.divide(bs.subtract(BigDecimal.ONE), 5, RoundingMode.HALF_DOWN);
	}

	public int getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	public BigDecimal getBs() {
		return bs;
	}

	public void setBs(BigDecimal bs) {
		this.bs = bs;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

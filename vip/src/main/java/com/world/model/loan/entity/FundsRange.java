package com.world.model.loan.entity;

import java.math.BigDecimal;

import com.world.model.entity.SysEnum;
import com.world.util.DigitalUtil;

/*******
 * 资金范围
 * @author apple
 */
public enum FundsRange implements SysEnum{

	loanRate(1 , "借贷利率" , DigitalUtil.getBigDecimal(0.0001) , DigitalUtil.getBigDecimal(0.01));
	
	private FundsRange(int key, String value, BigDecimal min, BigDecimal max) {
		this.key = key;
		this.value = value;
		this.min = min;
		this.max = max;
	}

	private int key;
	private String value;
	private BigDecimal min;
	private BigDecimal max;

	public BigDecimal getMin() {
		return min;
	}

	public BigDecimal getMax() {
		return max;
	}

	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	
	public String error(BigDecimal amount){
		if(amount.compareTo(min) < 0){
			return value + "不能小于" + min.multiply(DigitalUtil.getBigDecimal(100)) + "%";
		}else if(amount.compareTo(max) > 0){
			return value + "不能大于" + max.multiply(DigitalUtil.getBigDecimal(100)) + "%";
		}
		return null;
	}
	
}

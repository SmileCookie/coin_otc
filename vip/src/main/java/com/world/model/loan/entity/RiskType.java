package com.world.model.loan.entity;

import java.math.BigDecimal;

import com.world.model.entity.SysEnum;
import com.world.util.DigitalUtil;

/***
 * 风险类型
 * @author apple
 */
public enum RiskType implements SysEnum{

	repayOtherBi(1 , "自担风险" , DigitalUtil.getBigDecimal(0.4)) , 
	repayOwnBi(2 , "还本金币种" , DigitalUtil.getBigDecimal(0.5));
	
	private RiskType(int key, String value,BigDecimal fwfScale) {
		this.key = key;
		this.value = value;
		this.fwfScale = fwfScale;
	}

	private int key;
	private String value;
	private BigDecimal fwfScale;//服务费比例

	public BigDecimal getFwfScale() {
		return fwfScale;
	}

	public void setFwfScale(BigDecimal fwfScale) {
		this.fwfScale = fwfScale;
	}

	@Override
	public int getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}
}

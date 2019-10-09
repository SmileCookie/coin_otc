package com.wallet.eth.bean;

import java.math.BigInteger;

import com.wallet.eth.format.EtherHexFormatUtils;

public enum EtherBlockStatus{
	
	earliest(1, "earliest") , 
	latest(2, "latest") , 
	pending(3, "pending"),
	number(4, "0")
	; 
	
	private EtherBlockStatus(int key, String value) {
		this.key = key;
		this.value = value;
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
		if(key > 3){
			return EtherHexFormatUtils.integerToHex(new BigInteger(value));
		}
		return value;
	}

	public static EtherBlockStatus init(String value){
		EtherBlockStatus es = EtherBlockStatus.number;
		es.value = value;
		return es;
	}
}

package com.world.model.dao.api;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.world.model.entity.SysEnum;

public enum SignatureCode implements SysEnum{

	code1(1, "success"),
	code2(2, "no such user"),
	code3(3, "internal error"),
	code4(4,  "can not verify through"),
	code5(5,  "invalid arguments"),
	code6(6, "request too frequently"),
	code7(7, "has been locked"),
	code8(8, "has not activated");
	
	private SignatureCode(int key, String value) {
		this.key = key;
		this.value = value;
	}
	
	private int key;
	private String value;
	
	@Override
	public int getKey() {
		// TODO Auto-generated method stub
		return key;
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return value;
	}
	
	private static Map<String , EnumSet> dateEnums = new HashMap<String, EnumSet>();
	
	public static EnumSet getAll(Class c){
		if(dateEnums.get(c.getName()) == null){
			dateEnums.put(c.getName(), EnumSet.allOf(c));
		}
		return dateEnums.get(c.getName());
	}
	
	
	public static SignatureCode getEnumByKey(int key , Class c){
		EnumSet curEs = getAll(c);
		if(curEs != null){
			Iterator it = curEs.iterator();
			while(it.hasNext()){
				SignatureCode stat=(SignatureCode) it.next();
				if(key == stat.getKey()){
					return stat;
				}
			}
		}
		return null;
	}
}
package com.world.system.tips;

import java.lang.reflect.Method;

public enum CommonSysTipType implements SysTipType{
	
	index("首页访问.go go go") {
		public Method[] getMethods() throws Exception{
				return null;//new Method[]{Index.class.getMethod("index" , new Class[]{})};
		}
	}
	;
	
	private CommonSysTipType(String cont) {
		this.cont = cont;
	}

	private String cont;//提示内容

	public String getCont() {
		return cont;
	}

	public abstract Method[] getMethods() throws Exception;
}

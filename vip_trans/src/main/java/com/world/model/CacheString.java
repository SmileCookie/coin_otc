package com.world.model;

import org.apache.log4j.Logger;

public class CacheString {

	private final static Logger log = Logger.getLogger(CacheString.class);

	public CacheString(String str, long ltimes, long ctimes) {
		super();
		this.str = str;
		this.ltimes = ltimes;
		this.ctimes = ctimes;
	}

	private String str;//字符串
	private long ltimes;//缓存开始时间
	
	private long ctimes;//缓存有效时间
	
	public boolean isAvailable(){
		try {
			if(str != null && (System.currentTimeMillis() - ltimes) <= ctimes){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return false;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public long getLtimes() {
		return ltimes;
	}

	public void setLtimes(long ltimes) {
		this.ltimes = ltimes;
	}

	public long getCtimes() {
		return ctimes;
	}

	public void setCtimes(long ctimes) {
		this.ctimes = ctimes;
	}
}

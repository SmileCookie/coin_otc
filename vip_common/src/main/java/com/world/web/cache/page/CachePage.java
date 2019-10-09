package com.world.web.cache.page;

import java.sql.Timestamp;

public class CachePage {

	private String url;
	private int cacheTimes;
	private Timestamp expiredDate;
	
	public Timestamp getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(Timestamp expiredDate) {
		this.expiredDate = expiredDate;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getCacheTimes() {
		return cacheTimes;
	}
	public void setCacheTimes(int cacheTimes) {
		this.cacheTimes = cacheTimes;
	}
	
	public boolean isExpired(){
		if(expiredDate != null){
			if((expiredDate.getTime() - System.currentTimeMillis()) <= 3000){//提前用户3s开始请求
				return true;
			}
		}
		return false;
	}
}

package com.api.request;

import java.util.Map;

import com.api.VipRequest;

public class VipUserManagerRequest implements VipRequest{
	private long timestamp;
	
	private Map<String , String> params;
	
	private String apiMethod;

	public void setApiMethod(String apiMethod){
		this.apiMethod = apiMethod;
	}
	
	public String getApiMethod() {
		return apiMethod;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long time) {
		this.timestamp = time;
	}

	public Map<String, String> getTextParams() {
		return params;
	}

	public void setTextParams(Map<String, String> params) {
		this.params = params;
	}

}

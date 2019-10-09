package com.api;

import java.util.Map;

public interface VipRequest{
	public void setApiMethod(String apiMethod);
	
	public String getApiMethod();
	
	public long getTimestamp();
	
	public void setTimestamp(long time);

	public Map<String , String> getTextParams();
	
	public void setTextParams(Map<String , String> params);
	
}

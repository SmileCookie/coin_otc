package com.api;

public interface VipClient {

	public VipResponse execute(VipRequest request) throws Exception;
	
	public VipResponse execute(VipRequest request, int connectTimeout, int readTimeout) throws Exception;
	
	public VipResponse execute(VipRequest request , String session) throws Exception;
	
	public void setReadTimeout(int readTimeout);
	public void setConnectTimeout(int connectTimeout);

}

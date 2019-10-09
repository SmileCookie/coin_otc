package com.api.monitor.sysinfo;

import java.util.HashMap;
import java.util.Map;

import com.api.VipClient;
import com.api.VipClientFactory;
import com.api.VipRequest;
import com.api.VipResponse;
import com.api.request.VipUserManagerRequest;
import com.api.util.monitor.UnixServerInfo;


public class SysInfoManager {
	private static SysInfoManager sysInfoManager;
	
	public static SysInfoManager getInstance(){
		if(sysInfoManager == null) 
			sysInfoManager = new SysInfoManager();
		return sysInfoManager;
	}
	
	public VipResponse addInfo(String serverType) throws Exception{
		VipClient client = VipClientFactory.getClient("monitor");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/addInfo");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("sysinfo", UnixServerInfo.getSaveInfo(serverType));
		request.setTextParams(params);
		
		return client.execute(request);
	}
}

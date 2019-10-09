package com.world.model.dao.worker;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.common.CrossDomainAccess;
import com.world.model.dao.task.Worker;
import com.world.model.entitys.WhiteIp;

public class IpListWorker extends Worker {

	private static final long serialVersionUID = -5589116084131547078L;

	public IpListWorker(String name, String des) {
		super(name, des);
	}

	private static Map<String, WhiteIp> whiteIps  = new HashMap<String, WhiteIp>();;
	
	public static Map<String, WhiteIp> getWhiteIps() {
		if(whiteIps == null){
			
			reInit();
		}
		return whiteIps;
	}
	
	private synchronized static void reInit(){
		try{
		JSONArray wips =  CrossDomainAccess.getWhiteIpFromVip();
		if(wips != null && wips.size() > 0){
			for(int i=0;i<wips.size();i++){
				JSONObject o =  wips.getJSONObject(i);
				WhiteIp wi = new WhiteIp();
				wi.setIp(o.getString("ip"));
				wi.setLimit(o.getIntValue("limit"));
				
				whiteIps.put(wi.getIp(), wi);
				log.warn("ip白名单的ip=" + wi.getIp() + "\tlimit=" + wi.getLimit());
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		super.run();
		log.info("开始同步白名单");
		reInit();
	}
	
	
}

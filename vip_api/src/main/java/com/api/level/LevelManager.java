package com.api.level;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.api.VipClient;
import com.api.VipClientFactory;
import com.api.VipRequest;
import com.api.VipResponse;
import com.api.request.VipUserManagerRequest;

public class LevelManager {
	
	private static LevelManager levelManager;
	
	public static LevelManager getInstance(){
		if(levelManager == null) 
			levelManager = new LevelManager();
		return levelManager;
	}
	
	public VipResponse addTransJifen(String buyUserId, String sellUserId, BigDecimal money) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/level/addTransJifen");
		
		Map<String , String> params = new HashMap<String , String>();
		
		params.put("buyUserId", buyUserId);
		params.put("sellUserId", sellUserId);
		params.put("money", money.toPlainString());
		request.setTextParams(params);
		
		return client.execute(request);
	}
	
	public VipResponse jifenForFreeCoupon(String userId, BigDecimal jifen) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/level/jifenForFreeCoupon");
		
		Map<String , String> params = new HashMap<String , String>();
		
		params.put("userId", userId);
		params.put("jifen", jifen.toPlainString());
		request.setTextParams(params);
		
		return client.execute(request);
	}
	
	
	public VipResponse getUserTotalJifen(String userId) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/level/getUserTotalJifen");
		
		Map<String , String> params = new HashMap<String , String>();
		
		params.put("userId", userId);
		request.setTextParams(params);
		
		return client.execute(request);

	}
	
	public int getUserVipRate(String userId) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/level/getUserVipRate");
		
		Map<String , String> params = new HashMap<String , String>();
		
		params.put("userId", userId);
		request.setTextParams(params);
		
		VipResponse resp = client.execute(request);
		
		JSONObject json = JSONObject.fromObject(resp.getMsg());
		json = JSONObject.fromObject(json.get("datas"));
		int totalJifen = json.getInt("vipRate");
		return totalJifen;
		
	}
	
	
	
}

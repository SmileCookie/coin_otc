package com.world.common;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.VipResponse;
import com.api.user.UserManager;



public class CrossDomainAccess {

	
	protected static Logger log = Logger.getLogger(VerifiUtil.class.getName());
	
	
	/**
	 * 通过接口远程访问VIP的用户API信息
	 * @param key
	 */
	public void getUserAPIByKey(String key){
		JSONObject userObject = null;
		try{
			//读取数据
			VipResponse response = UserManager.getInstance().getUserByAccessKey(key, 0);
			if(response == null || !response.getMsg().startsWith("{")){
				log.error(key+"VIP项目-API请求错误，返回内容：" + response.getMsg());
				return;
			}
			if(response.taskIsFinish()){
				JSONObject json = JSONObject.parseObject(response.getMsg());
				if(json.getBoolean("isSuc")){
					userObject = JSONObject.parseObject(json.getString("datas"));
					//最后一次查询时间
					userObject.put("lastTime", System.currentTimeMillis());
					VerifiUtil.users.put("user_object_json_" + key, userObject);
				}else{
					log.error(key+"VIP项目-API请求错误，返回内容：" + response.getMsg());
				}
			}else{
				log.error(key+"VIP项目-API请求错误，返回内容：" + response.getMsg());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 调用接口从VIP后台获取白名单数据
	 * @param key
	 * @author zhanglinbo 20161217
	 */
	public static JSONArray getWhiteIpFromVip(){
		JSONArray whiteIpArr = null;
		try{
			//读取数据
			VipResponse response = UserManager.getInstance().getWhiteIp();
			if(response == null || !response.getMsg().startsWith("{")){
				log.error("VIP项目-API请求白名单数据错误，返回内容：" + response.getMsg());
				return null;
			}
			if(response.taskIsFinish()){
				JSONObject json = JSONObject.parseObject(response.getMsg());
				if(json.getBoolean("isSuc")){
					whiteIpArr = JSONObject.parseArray(json.getString("datas"));
				}else{
					log.error("VIP项目-API请求白名单数据错误，返回内容：：" + response.getMsg());
				}
			}else{
				log.error("VIP项目-API请求白名单数据错误，返回内容：" + response.getMsg());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return whiteIpArr;
	}
}

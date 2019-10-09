package com.world.common;


import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.api.VipResponse;
import com.api.user.UserManager;
import com.api.util.MapSort;
import com.world.controller.api.Index;
import com.world.model.dao.worker.IpListWorker;
import com.world.model.entitys.UserRequestApi;
import com.world.model.entitys.WhiteIp;
import com.world.util.callback.AsynMethodFactory;
import com.world.util.ip.IpUtil;
import com.world.util.sign.EncryDigestUtil;

/**
 * API接口验证工具
 * @author guosj
 */
public class VerifiUtil {
	
	protected static Logger log = Logger.getLogger(VerifiUtil.class.getName());
	
	private static Map<String, UserRequestApi> rquestUsers = new ConcurrentHashMap<String, UserRequestApi>();
	
	public static Map<String, UserRequestApi> rquestIps = new ConcurrentHashMap<String, UserRequestApi>();

	//判断当前请求是否超限单个用户1秒钟最多请求 10次   （单个ip 1分钟不得超过 1000次  超过1000次将被锁定1小时）
	public boolean isTooQuickForKey(String key, int limits){
//		System.out.println("这个key的限制是"+key+"\tlimit="+limits);
		UserRequestApi ura = rquestUsers.get(key);
		long seconds = System.currentTimeMillis() / 1000;
		if(ura == null){
			ura = new UserRequestApi();
			ura.last = seconds;
			ura.userKey = key;
			ura.times = 1;
			rquestUsers.put(key, ura);
		}else{
			synchronized (ura) {
				if (seconds == ura.last) {//属于同一秒的请求
					ura.times++;
					if (ura.times > limits) {
						return true;
					}
				} else {//不属于同一秒的请求重设当前缓存
					ura.last = seconds;
					ura.times = 1;
				}
			}
		}
		return false;
	}
	
	private static String iplists = "";
	
	//单个ip 1分钟不得超过 1000次  超过1000次将被锁定1小时），返回了true，就要锁了
	public boolean isLockedForIp(String ip){
			int limits=1000;
			
			//从内存中读出iplist，检查这个ip，是否在里面，不在就返回false
			Map<String, WhiteIp> whiteIps = IpListWorker.getWhiteIps();
			WhiteIp getIp = whiteIps.get(ip);

			if(getIp!=null){
				limits = getIp.getLimit();
//				System.out.println("这个ip是有限制的"+ip+"\tlimit="+limits);
			}

			
			UserRequestApi ura = rquestIps.get(ip);
			long currentMillSeconds = System.currentTimeMillis();	//当前毫秒数
			long minute = currentMillSeconds / 1000 / 60;	//当前的分钟数，时间戳
			if(ura == null){
				ura = new UserRequestApi();
				ura.last = minute;
				ura.userKey = ip;
				ura.times = 1;
				rquestIps.put(ip, ura);
			}else {
				synchronized (ura) {
					//判断当前ip是否被锁定
					if (ura.unlockMiliSeconds > 0) {//被锁定
						if (ura.unlockMiliSeconds > currentMillSeconds) {
							ura.last = minute;
							ura.times = 0;
							log.error("已锁定ip=" + ip + ",limits=" + limits + ",ura.unlockMiliSeconds=" + ura.unlockMiliSeconds);
							return true;
						}
					}

					ura.addVersion();
					if (minute == ura.last) {//属于同一分钟的请求
						ura.times++;
						if (ura.times > limits) {
							///超限后锁定一个小时
							ura.unlockMiliSeconds = currentMillSeconds + 60 * 60 * 1000;
							log.error("超限后锁定一个小时,已锁定ip=" + ip + ",limits=" + limits + ",getIp对象=" + getIp);
							return true;
						}
					} else {//不属于同一秒的请求重设当前缓存
						ura.last = minute;
						ura.times = 1;
					}
				}
			}
			return false;
	}
	
	private Map<String,Object> map;
	//API方法固定的参数
	private String[] order = {"method", "accesskey", "customerOrderId", "price", "amount",  "tradeType", "currency"};
	private String[] cancelOrder = {"method", "accesskey", "id", "currency"};
	private String[] getOrder = {"method", "accesskey", "id", "currency"};
	private String[] getOrders = {"method", "accesskey", "tradeType", "currency", "pageIndex"};
	private String[] getOrdersNew = {"method", "accesskey", "tradeType", "currency", "pageIndex", "pageSize"};
	private String[] getOrdersIgnoreTradeType = {"method", "accesskey", "currency", "pageIndex", "pageSize"};
	private String[] getUnfinishedOrdersIgnoreTradeType = {"method", "accesskey", "currency", "pageIndex", "pageSize"};
	private String[] getAccountInfo = {"method", "accesskey"};
	private String[] getUserAddress = {"method", "accesskey", "currency"};
	private String[] getWithdrawAddress = {"method", "accesskey", "currency"};
	private String[] getWithdrawRecord = {"method", "accesskey", "currency", "pageIndex", "pageSize"};
	private String[] getChargeRecord = {"method", "accesskey", "currency", "pageIndex", "pageSize"};
	private String[] getCnyChargeRecord = {"method", "accesskey", "pageIndex", "pageSize"};
	private String[] getCnyWithdrawRecord = {"method", "accesskey", "pageIndex", "pageSize"};
	private String[] cancelWithdraw = {"method", "accesskey", "currency", "downloadId", "safePwd"};
	private String[] withdraw = {"method", "accesskey", "amount", "currency", "fees", "receiveAddr", "safePwd"};
	private String[] getTransRecord = {"method", "accesskey", "currency", "pageIndex", "pageSize", "sinceId"};
	private String[] getOrderTransRecord = {"method", "accesskey", "currency", "orderId", "pageIndex", "pageSize"};
	
	private static VerifiUtil verifiUtil;
	
	/**
	 * 用户基本信息，包括ip绑定列表、ip绑定信息、是否开启
	 */
	public static Map<String, Object> users = new java.util.concurrent.ConcurrentHashMap<String, Object>();
	
	//
	public static VerifiUtil getInstance(){
		if(verifiUtil != null)
			return verifiUtil;
		else 
			return new VerifiUtil();
	}
	
	public VerifiUtil(){
		map = new HashMap<String,Object>();
		map.put("order", order);
		map.put("cancelOrder", cancelOrder);
		map.put("getOrder", getOrder);
		map.put("getOrders", getOrders);
		map.put("getOrdersNew", getOrdersNew);
		map.put("getOrdersIgnoreTradeType", getOrdersIgnoreTradeType);
		map.put("getUnfinishedOrdersIgnoreTradeType", getUnfinishedOrdersIgnoreTradeType);
		map.put("getAccountInfo", getAccountInfo);
		map.put("getUserAddress", getUserAddress);
		map.put("getWithdrawAddress", getWithdrawAddress);
		map.put("getWithdrawRecord", getWithdrawRecord);
		map.put("cancelWithdraw", cancelWithdraw);
		map.put("withdraw", withdraw);
		map.put("getTransRecord", getTransRecord);
		map.put("getOrderTransRecord", getOrderTransRecord);
		map.put("getChargeRecord", getChargeRecord);
		map.put("getCnyChargeRecord", getCnyChargeRecord);
		map.put("getCnyWithdrawRecord", getCnyWithdrawRecord);
	}
	
	/**
	 * 获取指定按顺序的参数
	 * @return
	 */
	public String[] getFixedArguments(String method){
		return (String[]) map.get(method);
	}
	
	/**
	 * API访问请求验证
	 * @param info
	 * @param key
	 * @param secret
	 * @return
	 */
	public Object validateAuthAccess(Index index, HttpServletRequest request){
		boolean flag = false;
		try{
			String key = request.getParameter("accesskey");	//用户传递访问key
			long reqTime = 0L;	//用记的请求时间
			String authInfo = request.getParameter("sign"); 	//用户根据URL生成的加密串
			
			try {
				reqTime = new Long(request.getParameter("reqTime"));
			} catch (Exception e) {
				return SystemCode.code_3005;
			}
			
			//强制参数存在为空，不通过
			if(StringUtils.isEmpty(authInfo) || StringUtils.isEmpty(key) || StringUtils.isEmpty(String.valueOf(reqTime))){
				return SystemCode.code_3005;
			}
			
			//==============================Memory Cache 进行IP、访问限制验证===================================================
			//用户IP过滤
//			boolean ipLimit = true;
//			if(!ipLimit){
//				SystemCode code = SystemCode.code_1001;
//				code.setClassName("当前IP请求过于频繁，已列入黑名单，API交易接口已自动关闭");
//				code.setValue("当前IP请求过于频繁，已列入黑名单，API交易接口已自动关闭");
//				//关闭API交易接口
//				VipResponse response = UserManager.getInstance().closeUserAutoApi(key);
//				JSONObject json = JSONObject.fromObject(response.getMsg());
//				if(response.taskIsFinish()){
//					if(json.getBoolean("isSuc")){
//						return code;
//					}else{
//						return false;
//					}
//				}else{
//					return false;
//				}
//			}
			
			
			//从缓存里面获取用户API信息
			JSONObject userObject = getUserApiFromMap(key);
					
			//找不到用户信息
			if(userObject == null ){
				return SystemCode.code_1003;
			}
			
		
			log.info(key+"VIP项目-API请求，获取用户，返回内容：" + userObject.toString() );
			
			//用户访问时间跟次数限制(相当必要)
			Object accessLimit = accessLimit(request, key, userObject.containsKey("limit") ? userObject.getIntValue("limit") : 10);
			if(accessLimit instanceof SystemCode){
				return accessLimit;
			}else{
				if(!((Boolean)accessLimit))
					return SystemCode.code_4002;
			}
			//================================================================================================================================================
			
			//System.out.println("----------用户当前状态------------" + userObject.getInt("apiStatus"));
			//设置已经查询到的用户对象
			index.setJsonObject(userObject);
			
			if(userObject.getIntValue("apiStatus") == 1){//API接口开放交易
				//交易IP绑定
				if(StringUtils.isNotEmpty( userObject.getString("apiIpBind") )){
					//如果申请了绑定交易IP则验证IP审核状态
					if(userObject.getIntValue("apiIpStatus") == 2){
						String apiIpBindStr = userObject.getString("apiIpBind");
						String[] apiIpBindArr = apiIpBindStr.split(",");
						
						boolean hasIp = false;	//访问的ip，是否符合绑定的ip其中一个
						
						for(String apiIpBind : apiIpBindArr){
							//如果请求的IP与绑定的IP一致
							if(apiIpBind.equals(IpUtil.getIp(request))){//一致
								hasIp = true;
								break;
							}
						}
						if(!hasIp){
							return SystemCode.code_3006;
						}
					}
				}
				
				//服务器当前时间
				long currTime = System.currentTimeMillis();
				//用户请求时间在必须在服务器时间前后5分钟
				if(reqTime >= (currTime - 5*60*1000) && reqTime <= (currTime + 5*60*1000)){
					//获取用户加密的原信息
					Object info = getAuthorization(request);
					if(info instanceof SystemCode){
						return info;
					}else{
						//与用户执行相同的加密方法，传入相同的secret
						String encryInfo = (String)info;
						info = EncryDigestUtil.hmacSign((String)info, userObject.getString("apiSecret"));
						//与用户的加密信息作比较
						if(authInfo.equals(info)){
							flag = true;
//							//验证通过，记录本次请求时间
//							Data.Update("update btcuser set apiAuthTime=? where userId=?", new Object[]{reqTime, userObject.getString("userId")});
						}else{
							//验签失败后，再用ascii码值顺序排一次序
							try {
								info = getAuthorizationInMap(request);
								if(info instanceof SystemCode){
									return info;
								}else{
									encryInfo = (String)info;
									info = EncryDigestUtil.hmacSign((String)info, userObject.getString("apiSecret"));
									//与用户的加密信息作比较
									if(authInfo.equals(info)){
										flag = true;
									}
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							log.error(key+"VIP项目-API请求错误，验签出错，签名内容：" + encryInfo + "\t");
						}
					}
				}
			}else{
				//Cache.Set("user_"+key+"_api_locked", String.valueOf("locked"), 30*60);
				return SystemCode.code_4001;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			return SystemCode.code_1002;
		}
		return flag;
	}
	
	/**
	 * 用户加密原信息
	 * @param request
	 * @return
	 */	
	private Object getAuthorization(HttpServletRequest request){
		String method = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
		String[] fixedArguments = getFixedArguments(method);
		StringBuffer buffer = new StringBuffer();
		int i = 0;
		String value = "";
		for (String string : fixedArguments) {
			value = request.getParameter(string);
			
			//这些是需要进行url转码的参数
			try {
				if((string.equals("safePwd") || string.equals("receiveAddr")) && !StringUtils.isEmpty(value) ) {
					value = URLDecoder.decode(value, "utf-8");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(string.equals("customerOrderId") || string.equals("sUserName")){
				if(StringUtils.isEmpty(value)){//没有传递customerOrderId  无需参与签名
					continue;
				}
			}else{
				if(StringUtils.isEmpty(value)){
					System.out.println("api请求无效参数：" + string + "___" + value);
					return SystemCode.code_3005;
				}
			}
			if(i != 0){
				buffer.append("&");
			}
			buffer.append(string).append("=").append(value);
			i++;
		}
		return buffer.toString();
	}
	
	/**
	 * 用户加密原信息，放到map
	 * @param request
	 * @return
	 */	
	private Object getAuthorizationInMap(HttpServletRequest request){
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String[]> paramMap = request.getParameterMap();
		for (String paramName : paramMap.keySet()) {
			String value = request.getParameter(paramName);
			
			//这些是需要进行url转码的参数
			try {
				if((paramName.equals("safePwd") || paramName.equals("receiveAddr")) && !StringUtils.isEmpty(value) ) {
					value = URLDecoder.decode(value, "utf-8");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(paramName.equals("customerOrderId") || paramName.equals("sUserName") || paramName.equals("sign") || paramName.equals("reqTime")){
				if(StringUtils.isEmpty(value)){//没有传递customerOrderId  无需参与签名
					continue;
				}
			}else{
				if(StringUtils.isEmpty(value)){
					log.error("api请求无效参数：" + paramName + "___" + value);
					return SystemCode.code_3005;
				}
			}
			map.put(paramName, value);
		}
		return toStringMap(map);
	}
	
	public static String toStringMap(Map m){
		//按map键首字母顺序进行排序
		m = MapSort.sortMapByKey(m);
		
		StringBuilder sbl = new StringBuilder();
		for(Iterator<Entry> i = m.entrySet().iterator(); i.hasNext();){
			Entry e = i.next();
			Object o = e.getValue();
			String v = "";
			if(o == null){
				v = "";
			}else if(o instanceof String[]) {
				String[] s = (String[]) o;
				if(s.length > 0){
					v = s[0];
				}
			}else{
				v=o.toString();
			}
			if(!e.getKey().equals("sign") && !e.getKey().equals("reqTime") && !e.getKey().equals("tx")){
//				try {
//					sbl.append("&").append(e.getKey()).append("=").append(URLEncoder.encode(v, "utf-8"));
//				} catch (UnsupportedEncodingException e1) {
//					e1.printStackTrace();
					sbl.append("&").append(e.getKey()).append("=").append(v);
//				}
			}
		}
		String s = sbl.toString();
		if(s.length()>0){
			return s.substring(1);
		}
		return "";
	}
	
	
	
	/**
	 * api访问限制
	 * @param request
	 * @param key
	 * @param apiLimitCount	每个方法每秒钟限制次数
	 * @return
	 * @throws Exception
	 */
	public Object accessLimit(HttpServletRequest request, String key, int slimitTimes) throws Exception{
		key = key.trim();
		//默认最小
		//slimitTimes = 10;
		if(slimitTimes == 0) slimitTimes = 10;
		
		String ip = IpUtil.getIp(request);
		/***
		 * 单个用户1秒钟最多请求 10次   （单个ip 1分钟不得超过 1000次  超过1000次将被锁定1小时,一小时后自动解锁）
		 */
		if(isLockedForIp(ip)){
			return SystemCode.code_4001;
		}else{
			if(isTooQuickForKey(key, slimitTimes)){
				return SystemCode.code_4002;
			}else{
				return true;
			}
		}
		
		
		//请求method
//		String method = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
//		//上一次的请求时间
//		String oldReqTime = Cache.Get(method + "_"+key);
//		//服务器当前时间
//		long currTime = System.currentTimeMillis();
//		//缓存时间周期
//		int cycle = 30*60;
//		//一分钟内限制次数
//		int limitTimes = 1000;
//		//限制时间1分钟
//		int limitTime = 60 * 1000;
//		//还未请求过，或缓存已失效
//		if(StringUtils.isEmpty(oldReqTime)){	
//			Cache.Set(method+"_"+key, String.valueOf(currTime), cycle);
//			Cache.Set(method+"_"+key+"_one_min_times", "1", cycle);
//			Cache.Set(method+"_"+key+"_one_sec_times", "1", cycle);
//			Cache.Set(method+"_"+key+"_one_min_startTime", String.valueOf(currTime), cycle);
//			//Cache.Set("user_"+key+"_api_locked", String.valueOf("unlocked"), cycle);
//			return true;
//		}else{
//			//判断该用户是否被锁住API了
////			if("locked".equals(Cache.Get("user_"+key+"_api_locked"))){
////				return SystemCode.code_4001;
////			}
//			
//			//上一次的请求时间
//			long old = new Long(oldReqTime);
//			//--------------------------------------限制一分钟内请求次数------------------------------------
//			//请求次数
//			String times = Cache.Get(method+"_"+key+"_one_min_times");
//			//根据用户建议，method=order其实需要分开为2个方法BTC/LTC
//			if(method.equalsIgnoreCase("order")){
//				//限制次数增加500次
//				limitTimes += 500;
//			}
//			//第一次请求开始时间
//			String startTime = Cache.Get(method+"_"+key+"_one_min_startTime");
//			
//			if(StringUtils.isEmpty(startTime)){
//				//重新设置请求次数
//				Cache.Set(method+"_"+key+"_one_min_times", "1", cycle);
//				return true;
//			}
			
			//1分钟内请求超过limitTimes次，关闭该用户API交易接口，并清空缓存
//			if(Integer.parseInt(times) > limitTimes && currTime - new Long(startTime) < limitTime){
//				Cache.Delete(method+"_"+key);
//				Cache.Delete(method+"_"+key+"_one_min_times");
//				Cache.Delete(method+"_"+key+"_one_sec_times");
//				Cache.Delete(method+"_"+key+"_one_min_startTime");
//				//Cache.Set("user_"+key+"_api_locked", String.valueOf("locked"), cycle);
//				//关闭API交易接口
//				VipResponse response = UserManager.getInstance().closeUserAutoApi(key);
//				JSONObject json = JSONObject.fromObject(response.getMsg());
//				if(response.taskIsFinish()){
//					if(json.getBoolean("isSuc")){
//						return SystemCode.code_4001;
//					}
//				}
//			}else{
//				int t = 0;
//				//如果缓存时间与当前请求时间超过1分钟，则更新缓存的开始时间，请求次数归0
//				if(currTime - new Long(startTime) >= limitTime){
//					Cache.Set(method+"_"+key+"_one_min_startTime", String.valueOf(currTime), cycle);
//					t = 1;
//				}else{
//					//如果请求次数到达limitTimes次，则重新数数
//					if(Integer.parseInt(times) == (limitTimes + 1)){
//						t = 1;
//					}else{
//						t = Integer.parseInt(times) + 1;
//					}
//				}
//				//重新设置请求次数
//				Cache.Set(method+"_"+key+"_one_min_times", String.valueOf(t), cycle);
//			}
			
			
			
			//===========================================================================================
			//如果当前求比上一次请求时间>=1s
//			if(currTime - old >= 1000){
//				Cache.Set(method + "_"+key, String.valueOf(currTime), cycle);
//				Cache.Set(method + "_"+key+"_one_sec_times", "1", cycle);
//				return true;
//			}else{
//				//1秒钟内请求次数
//				int ost = Integer.parseInt(Cache.Get(method + "_"+key+"_one_sec_times"));
//				if(ost > slimitTimes){
//					//请求过于频繁
//					return SystemCode.code_4002;
//				}else{
//					Cache.Set(method + "_"+key, String.valueOf(currTime), cycle);
//					Cache.Set(method + "_"+key+"_one_sec_times", String.valueOf(ost+1), cycle);
//					return true;
//				}
//			}
//		}
	}
	
	
	/**
	 * 从缓存里面获取用户API信息。 如果缓存的信息超过5分钟，则通过异步线程同步最新的API信息
	 * @param key 用户的accessKey 
	 * @return API json 对象
	 */
	public JSONObject getUserApiFromMap(String key){
		JSONObject userObject = null;
		try{
			userObject = (JSONObject) users.get("user_object_json_" + key);
			
			//检测用户是否超出有效期,如果是，使用异步线程重新获取用户最新API信息
			if(userObject!=null && userObject.containsKey("lastTime")){
				long lastTime = userObject.getLong("lastTime");
				long currentTime = System.currentTimeMillis();
				if(currentTime-lastTime>5*60*1000){//超过五分钟让用户失效,重新获取最新用户API信息
					AsynMethodFactory.addWork(CrossDomainAccess.class, "getUserAPIByKey",new Object[]{key});
				}
				return userObject;
			}
			
			
			//是否需要重新查询用户
			if(userObject == null || !userObject.containsKey("limit")){
				VipResponse response = UserManager.getInstance().getUserByAccessKey(key, 0);
				log.info("重新获取API信息key："+key);
				if(response == null || !response.getMsg().startsWith("{")){
					log.error(key+"VIP项目-API请求错误，返回内容：" + response.getMsg());
					return null;
				}
				if(response.taskIsFinish()){
					JSONObject json = JSONObject.parseObject(response.getMsg());
					if(json.getBoolean("isSuc")){
						userObject = JSONObject.parseObject(json.getString("datas"));
						
						//最后一次查询时间
						userObject.put("lastTime", System.currentTimeMillis());
						users.put("user_object_json_" + key, userObject);
					}else{
						log.error(key+"VIP项目-API请求错误，返回内容：" + response.getMsg());
					}
				}else{
					log.error(key+"VIP项目-API请求错误，返回内容：" + response.getMsg());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return userObject;
	}
	
	
}

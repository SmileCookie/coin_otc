package com.world.controller.api.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.api.config.ApiConfig;
import com.api.util.MapSort;
import com.world.cache.Cache;
import com.world.controller.api.util.SystemCode;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.user.User;
import com.world.util.sign.EncryDigestUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * API接口验证工具
 * @author guosj
 */
public class VerifiUtil {
	
	static Logger log = Logger.getLogger(VerifiUtil.class.getName());
	
	private Map<String,Object> map;
	//API方法固定的参数
	private String[] order = {"method", "accesskey", "price", "amount",  "tradeType", "currency"};
	private String[] cancelOrder = {"method", "accesskey", "id", "currency"};
	private String[] getOrder = {"method", "accesskey", "id", "currency"};
	private String[] getOrders = {"method", "accesskey", "tradeType", "currency", "pageIndex"};
	private String[] getOrdersNew = {"method", "accesskey", "tradeType", "currency", "pageIndex", "pageSize"};
	private String[] getOrdersIgnoreTradeType = {"method", "accesskey", "currency", "pageIndex", "pageSize"};
	private String[] getUnfinishedOrdersIgnoreTradeType = {"method", "accesskey", "currency", "pageIndex", "pageSize"};
	private String[] getAccountInfo = {"method", "accesskey"};
	
	private static VerifiUtil verifiUtil;
	
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
	}
	
	/**
	 * 获取指定按顺序的参数
	 * @return
	 */
	public String[] getFixedArguments(String method){
		return (String[]) map.get(method);
	}
	
	/**
	 * @return
	 */
	public Object validateAuthAccess(HttpServletRequest request){
		boolean flag = false;
		try{
			String key = request.getParameter("accesskey");	//用户传递访问key
			long reqTime = new Long(request.getParameter("reqTime"));	//用记的请求时间
			String authInfo = request.getParameter("sign"); 	//用户根据URL生成的加密串
			
			//强制参数存在为空，不通过
			if(StringUtils.isEmpty(authInfo) || StringUtils.isEmpty(key) || StringUtils.isEmpty(String.valueOf(reqTime))){
				return SystemCode.code_3005;
			}
			
			//==============================Memory Cache 进行IP、访问限制验证===================================================
			//用户IP过滤
			boolean ipLimit = true;//Base.ipLimit(request, "apiIpLimit");
			if(!ipLimit){
				SystemCode code = SystemCode.code_1001;
				code.setClassName("当前IP请求过于频繁，已列入黑名单，API交易接口已自动关闭");
				code.setValue("当前IP请求过于频繁，已列入黑名单，API交易接口已自动关闭");
				//关闭API交易接口
				//Data.Update("update btcuser set apiStatus=1 where userId=?", new Object[]{new btcUserBean().getBtcUserByKey("*", key).getUserId()});
				return code;
			}
			
			//用户访问时间跟次数限制(相当必要)
			Object accessLimit = accessLimit(request, key);
			if(accessLimit instanceof SystemCode){
				return accessLimit;
			}else{
				if(!((Boolean)accessLimit))
					return SystemCode.code_4002;
			}
			//================================================================================================================================================
			
			
		}catch(Exception ex){
			log.error("内部异常", ex);
			return SystemCode.code_1002;
		}
		return flag;
	}
	
	
	private Object getCommonAuthorization(HttpServletRequest request){
		Map params = new HashMap(request.getParameterMap());
		
//		log.info(request.getRequestURI());
//		String method = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
//		String[] fixedArguments = getFixedArguments(method);
//		StringBuffer buffer = new StringBuffer();
//		int i = 0;
//		String value = "";
//		for (String string : fixedArguments) {
//			if(i != 0){
//				buffer.append("&");
//			}
//			value = request.getParameter(string);
//			if(StringUtils.isEmpty(value)){
//				return SystemCode.code_3005;
//			}
//			buffer.append(string).append("=").append(value);
//			i++;
//		}
		return toStringMap(params);
	}
	
	public String toStringMap(Map m){
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
			if(!e.getKey().equals("sign") && !e.getKey().equals("reqTime") && !e.getKey().equals("tx"))
				try {
					sbl.append("&").append(e.getKey()).append("=").append(URLEncoder.encode(v, "utf-8"));
				} catch (UnsupportedEncodingException e1) {
					log.error("内部异常", e1);
					sbl.append("&").append(e.getKey()).append("=").append(v);
				}
		}
		String s = sbl.toString();
		if(s.length()>0){
			return s.substring(1);
		}
		return "";
	}
	
	/**
	 * 用户加密原信息
	 * @param request
	 * @return
	 */	
	private Object getAuthorization(HttpServletRequest request){
		request.getParameterMap();
		
		
		log.info(request.getRequestURI());
		String method = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
		String[] fixedArguments = getFixedArguments(method);
		StringBuffer buffer = new StringBuffer();
		int i = 0;
		String value = "";
		for (String string : fixedArguments) {
			if(i != 0){
				buffer.append("&");
			}
			value = request.getParameter(string);
			if(StringUtils.isEmpty(value)){
				return SystemCode.code_3005;
			}
			buffer.append(string).append("=").append(value);
			i++;
		}
		return buffer.toString();
	}

	/**
	 * 获取当前请求的用户
	 * @param key
	 * @param secret
	 * @return
	 */
	/*public User getUser(String key){
		btcUserBean btcuser = new btcUserBean().getBtcUserByKey("*", key);
		User user = new UserDao().get(String.valueOf(btcuser.getUserId()));
		return user;
		
	}
	*/
	/**
	 * 获取当前请求的用户
	 * @return
	 */
	public User getUserById(String id){
		User user = new UserDao().get(id);
		return user;
	}
	
	/**
	 * 访问限制
	 * 请求同一个方法，1s内只能请求一次
	 * 请求不同的方法，1s可以请求多次
	 * 请求同一个方法，1m内超过规定次数，视为恶意攻击，自动关闭API交易接口
	 * @param request
	 * @return
	 */
	public Object accessLimit(HttpServletRequest request, String key){
		//请求method
		String method = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
		//上一次的请求时间
		String oldReqTime = Cache.Get(method + "_"+key);
		//服务器当前时间
		long currTime = System.currentTimeMillis();
		//缓存时间周期
		int cycle = 30*60;
		//一分钟内限制次数
		int limitTimes = 500;
		//限制时间1分钟
		int limitTime = 60 * 1000;
		//一秒钟内限制次数 
		int slimitTimes = 5;
		//还未请求过，或缓存已失效
		if(StringUtils.isEmpty(oldReqTime)){	
			Cache.Set(method+"_"+key, String.valueOf(currTime), cycle);
			Cache.Set(method+"_"+key+"_one_min_times", "1", cycle);
			Cache.Set(method+"_"+key+"_one_sec_times", "1", cycle);
			Cache.Set(method+"_"+key+"_one_min_startTime", String.valueOf(currTime), cycle);
			return true;
		}else{
			//上一次的请求时间
			long old = new Long(oldReqTime);
			//--------------------------------------限制一分钟内请求次数------------------------------------
			//请求次数
			String times = Cache.Get(method+"_"+key+"_one_min_times");
			//根据用户建议，method=order其实需要分开为2个方法BTC/LTC
			if(method.equalsIgnoreCase("order")){
				//限制次数增加300次
				limitTimes += 300;
			}
			//第一次请求开始时间
			String startTime = Cache.Get(method+"_"+key+"_one_min_startTime");
			//1分钟内请求超过limitTimes次，关闭该用户API交易接口，并清空缓存
			if(Integer.parseInt(times) > limitTimes && currTime - new Long(startTime) < limitTime){
				Cache.Delete(method+"_"+key);
				Cache.Delete(method+"_"+key+"_one_min_times");
				Cache.Delete(method+"_"+key+"_one_sec_times");
				Cache.Delete(method+"_"+key+"_one_min_startTime");
				//关闭API交易接口
				//Data.Update("update btcuser set apiStatus=1 where userId=?", new Object[]{new btcUserBean().getBtcUserByKey("*", key).getUserId()});
				return SystemCode.code_4001;
			}else{
				int t = 0;
				//如果缓存时间与当前请求时间超过1分钟，则更新缓存的开始时间，请求次数归0
				if(currTime - new Long(startTime) >= limitTime){
					Cache.Set(method+"_"+key+"_one_min_startTime", String.valueOf(currTime), cycle);
					t = 1;
				}else{
					//如果请求次数到达limitTimes次，则重新数数
					if(Integer.parseInt(times) == (limitTimes + 1)){
						t = 1;
					}else{
						t = Integer.parseInt(times) + 1;
					}
				}
				//重新设置请求次数
				Cache.Set(method+"_"+key+"_one_min_times", String.valueOf(t), cycle);
			}
			//===========================================================================================
			//如果当前求比上一次请求时间>=1s
			if(currTime - old >= 1000){
				Cache.Set(method + "_"+key, String.valueOf(currTime), cycle);
				Cache.Set(method + "_"+key+"_one_sec_times", "1", cycle);
				return true;
			}else{
				//1秒钟内请求次数
				int ost = Integer.parseInt(Cache.Get(method + "_"+key+"_one_sec_times"));
				if(ost > slimitTimes){
					//请求过于频繁
					return SystemCode.code_4002;
				}else{
					Cache.Set(method + "_"+key, String.valueOf(currTime), cycle);
					Cache.Set(method + "_"+key+"_one_sec_times", String.valueOf(ost+1), cycle);
					return true;
				}
			}
		}
	}
	
	
	/**
	 * 内网服务器访问认证
	 * @param request
	 * @return
	 */
	public Object validateAuthAccess(HttpServletRequest request, String reqIp){
		
	    String allows = ApiConfig.getValue("canIps");
	     
	    //IP与ACCESSKEY验证
	    JSONArray array = JSONArray.fromObject(allows);
	    //
	    String ip,accesskey,secretkey = "";
	    boolean flag = false;
	    for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(i);
            
            ip = (String)object.get("ip");
	    	accesskey = (String)object.get("accesskey");
	    	String userAccessKey =  request.getParameter("accesskey");
			if(ip.equals(reqIp) && userAccessKey!=null && accesskey.equalsIgnoreCase(userAccessKey.trim())){
				flag = true;
				secretkey = (String)object.get("secretkey");
		    	//secretkey = EncryDigestUtil.digest(secretkey);
				break;
			}
	    }
	    if(!flag){
	    	log.error("API内网访问较验，当前访问IP:" + reqIp + "未授权，不允许访问!");
	    	return SystemCode.code_1003;
	    }
	    
		String authInfo = request.getParameter("sign");
		Long reqTime = Long.parseLong(request.getParameter("reqTime"));//(Long) object.get("reqTime");
		
		long currTime = System.currentTimeMillis();
		long limitTime = 1*60*60*1000;
		//验证请求时间，必须在服务器的前后limitTime分钟
		if(!(reqTime.longValue() >= (currTime - limitTime) && reqTime.longValue() <= (currTime +limitTime))){
			return SystemCode.code_3007;
		}
		
		Object info = getCommonAuthorization(request);
		if(info instanceof SystemCode){
			return info;
		}else{
			//与用户执行相同的加密方法，传入相同的secret
			//log.info("加密：" + info + ":secret:" + secretkey);
			info = EncryDigestUtil.hmacSign((String)info, secretkey);
			//与用户的加密信息作比较
			if(authInfo.equals(info)){
				return true;
			}else{
				log.error("API内网访问较验，当前访问IP:" + reqIp + "已授权，密钥信息对比不匹配，不允许访问!!");
				return SystemCode.code_1003;
			}
		}
	}
}

package com.world.web.action;

import com.api.VipResponse;
import com.api.common.SystemCode;
import com.api.user.UserManager;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.model.entity.user.UserCommon;
import com.world.util.string.MD5;
import com.world.web.sso.SessionUser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobileUserAction extends BaseAction implements UserCommon {

	private static final String CHARSET = "UTF-8";
	
	final static String appLoginCache = "app_login_";
	
	final static int appDefaultTime = 30 * 24 * 60 * 60;
	public final static String appUid = GlobalConfig.session + "appuid_";

	public void setLan() {
		String lang = param("lang");
		if (StringUtils.isNotBlank(lang)) {
			if (lang.equals("0")) {
				lang = "hk";
			} else if(lang.equals("1")) {
				lang = "cn";
			} else if (lang.equals("2")) {
				lang = "en";
			} else if (lang.equals("3")) {
				lang = "jp";
			}  else if (lang.equals("4")) {
				lang = "kr";
			}  else {
				lang = "cn";
			}
		} else {
			lang = "cn";
		}

		lan = lang;
	}

	public void json(SystemCode code) {
		json(code, code.getValue(), null);
	}
	
	public void json(SystemCode code, String msg) {
		json(code, msg, null);
	}
	
	public void json(SystemCode code, Map<String, Object> datas) {
		json(code, code.getValue(), datas);
	}
	
	public void json(SystemCode code, String msg, Map<String, Object> datas) {
		PrintWriter out = null;
		try {
			response.setContentType("application/json;charset=" + CHARSET);
			out = response.getWriter();
			JSONObject json = new JSONObject();
			
			
			String whoInvokeMe = "";
			boolean isVersionGt5 = true;
			
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			for (StackTraceElement item : stackTraceElements) {
				if (item.getClassName().startsWith("com.world.controller.api.m")) {
					whoInvokeMe = item.getMethodName();
					
					/*String versionStr = item.getClassName().substring(item.getClassName().length()-1);
					if (Integer.valueOf(versionStr) > 5) 
						isVersionGt5 = true;*/
				}
			}
			
			if (isVersionGt5) {
				JSONObject resMsg = new JSONObject();
				
				resMsg.put("code", code.getKey());
				resMsg.put("message", msg);
				resMsg.put("method", whoInvokeMe);
				json.put("resMsg", resMsg);
				if (null != datas) {
					json.put("data", datas);
				}
			} else {
				json.put("code", code.getKey());
				json.put("message", msg);
				if (null != datas) {
					json.putAll(datas);
				}
			}
			out.write(json.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (null != out) {
					out.flush();
					out.close();
				}
			} catch (Exception e2) {}
		}
	}

	public void jsonForApp(SystemCode code, String msg, Object data) {
		PrintWriter out = null;
		try {
			response.setContentType("application/json;charset=" + CHARSET);
			out = response.getWriter();
			JSONObject json = new JSONObject();


			String whoInvokeMe = "";
			boolean isVersionGt5 = true;

			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			for (StackTraceElement item : stackTraceElements) {
				if (item.getClassName().startsWith("com.world.controller.api.m")) {
					whoInvokeMe = item.getMethodName();

					/*String versionStr = item.getClassName().substring(item.getClassName().length()-1);
					if (Integer.valueOf(versionStr) > 5)
						isVersionGt5 = true;*/
				}
			}

			if (isVersionGt5) {
				JSONObject resMsg = new JSONObject();

				resMsg.put("code", code.getKey());
				resMsg.put("message", msg);
				resMsg.put("method", whoInvokeMe);
				json.put("resMsg", resMsg);
				if (null != data) {
					json.put("data", data);
				}
			} else {
				json.put("code", code.getKey());
				json.put("message", msg);
				if (null != data) {
					Map<String, Object> datas = new HashMap<>();
					datas.put("data",data);
					json.putAll(datas);
				}
			}
			out.write(json.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (null != out) {
					out.flush();
					out.close();
				}
			} catch (Exception e2) {}
		}
	}

	public void writeMsg(String msg) {
		response.setContentType("text/plain;charset=" + CHARSET);
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {
			try {
				if (null != out) {
					out.write(msg);
					out.flush();
					out.close();
				}
			} catch (Exception e2) {}
		}
	}

	public void write2Json(Object obj) {
		PrintWriter out = null;
		try {
			response.setContentType("application/json;charset=" + CHARSET);
			out = response.getWriter();
			if (obj != null) {
				JSONArray json = JSONArray.fromObject(obj);
				out.write(json.toString());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (null != out) {
					out.flush();
					out.close();
				}
			} catch (Exception e2) {}
		}
	}

	public void write2JsonObj(Object obj) {
		PrintWriter out = null;
		try {
			response.setContentType("application/json;charset=" + CHARSET);
			out = response.getWriter();
			if (obj != null) {
				JSONObject json = JSONObject.fromObject(obj);
				out.write(json.toString());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (null != out) {
					out.flush();
					out.close();
				}
			} catch (Exception e2) {}
		}
	}
	
	public void toLogin(String userId, String userName, String ip) {
		try {
			String token = MD5.toMD5(userId + UUID.randomUUID().toString());
			String loginCacheKey = appLoginCache + userId;
			SessionUser su = new SessionUser();
			su.uid = userId;//用户id
			su.uname = userName;//用户名
			su.ltime = System.currentTimeMillis();//登录时间
			su.lip = ip;//登录ip
			su.lastTime = su.ltime;//最后活动时间
			su.token = token;
			
			Cache.SetObj(loginCacheKey, su, appDefaultTime);
		} catch(Exception e) {
			log.error(e.toString(), e);
		}
	}
	
	public boolean isLogin1(String userId, String token) {
		if (null == userId || "".equals(userId) || null == token || "".equals(token)) {
			return false;
		}
		if(token(userId).equals(token)) {
			return true;
		}
		return false;
	}

	public SystemCode isLogin(String userId, String token) {
		if (null == userId || "".equals(userId) || null == token || "".equals(token)) {
			return SystemCode.code_1003;
		}

		if(StringUtils.isEmpty(token(userId))){
			return SystemCode.code_1003;
		}else{
			if(token(userId).equals(token)) {
				return SystemCode.code_1000;
			}else{
				return SystemCode.code_402;
			}
		}
	}
	public String token(String userId) {
		String token = "";
		try {
			String loginCacheKey = appUid + userId;
			if (null != Cache.GetObj(loginCacheKey)) {
				Object obj = Cache.GetObj(loginCacheKey);
				if(null != obj){
					SessionUser sessionUser = (SessionUser) Cache.GetObj(obj.toString());
					if (null != sessionUser) {
						token = sessionUser.others.get("token").toString();
					}
				}
			}
	    } catch(Exception e) {
	    	log.error(e.toString(), e);
	    }
		return token;
	}
	
	protected String getMarketName(String currencyType, String exchangeType){
		String marketName="";
		marketName = currencyType+"_"+exchangeType;
		
		return marketName.toLowerCase();
	}
	
	/**
	 * 验证指纹或资金密码
	 * @param pass
	 * @param userId
	 * @return
	 */
	protected boolean fingerprintOrSafePwd(String userId, String safePwd, String fingerprint, String market){
		VipResponse response = null;
		try {
			Map<String , String> params = new HashMap<String , String>();
			params.put("userId", userId);
			params.put("safePwd", safePwd);
			params.put("fingerprint", fingerprint);
			params.put("market", market);
			params.put("lang", lan);
			
			response = new UserManager().validateFingerprintOrSafePwd(params);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		if(response != null){
			JSONObject jo = JSONObject.fromObject(response.getMsg());
			if(jo != null){
				if(jo.getBoolean("isSuc")){
					return true;
				}else{
					//Response.append(response.getMsg());
					int code = Integer.valueOf(response.getCode());
					json(SystemCode.getSystemCodeByKey(code), jo.getString("des"));
					return false;
				}
			}
		}
		json(SystemCode.code_1002);
		return false;
	}

}

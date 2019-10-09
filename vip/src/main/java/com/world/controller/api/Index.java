package com.world.controller.api;

import com.world.util.QcloudCosUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.web.Page;
import com.world.web.action.MobileUserAction;
import com.world.web.action.UserAction;
import com.world.web.sso.SessionUser;
import com.world.web.sso.session.Session;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.UUID;

public class Index extends UserAction{
	
	/**
	 * 提供给第三方验证用户是否登录状态
	 */
	@Page(Viewer = JSON)
	public void index() {
		int isLogined = 0;
		String sessionId = param("sid");
		int type = intParam("type");//0用户 1 管理员
		String ip = param("ip");
		if(sessionId.length() > 0){
			Session session = new Session(sessionId);
			SessionUser su = null;
			if(type == 1){
				su = (SessionUser) Cache.GetObj("ADMIN_" + sessionId);
			}else{
				su = session.getUser(ip);
			}
			if (su == null) {
	  			//
	  		}else {
	  			isLogined = 1;
	  		}
		}
		Response.append("{\"login\":"+isLogined+"}");
	}
	
	@Page(Viewer = JSON)
	public void isAppLogin() {
		String userId = param("userId");
		String token = request.getHeader("Authorization");
		if(StringUtils.isNotBlank(token))
			token = token.replace("Bearer ", "");
		int isLogined = 0;
		if (null == userId || "".equals(userId) || null == token || "".equals(token)) {
		}
		if(cacheToken(userId).equals(token)) {
			isLogined = 1;
		}
		JSONObject result = new JSONObject();
		result.put("login", isLogined);
		Response.append(result.toString());
	}

	public String cacheToken(String userId) {
		String token = "";
		try {
			String loginCacheKey = MobileUserAction.appLoginCache + userId;
			if (null != Cache.GetObj(loginCacheKey)) {
				SessionUser sessionUser = (SessionUser) Cache.GetObj(loginCacheKey);
				if (null != sessionUser) {
					token = sessionUser.token;
				}
			}
	    } catch(Exception e) {
			log.error("内部异常", e);
	    }
		return token;
	}


}
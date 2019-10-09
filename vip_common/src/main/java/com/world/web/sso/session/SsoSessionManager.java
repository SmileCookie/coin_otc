package com.world.web.sso.session;

import com.world.config.GlobalConfig;
import com.world.util.string.MD5;
import com.world.web.Pages;
import com.world.web.jwt.JwtUtil;
import org.apache.log4j.Logger;

import java.util.UUID;

public class SsoSessionManager {
	
	public final static String JSESSIONID = GlobalConfig.session + "JSESSIONID"; 
	public final static String LANGUAGE = GlobalConfig.session + "lan";
	static Logger log = Logger.getLogger(SsoSessionManager.class.getName());
	
	public static void initSessionId(Pages p){
		String sessionId = p.GetCookie(JSESSIONID);
		if(sessionId == null){
			log.info("页面url:" + p.request.getRequestURI() + ",cookie中JSESSIONID不存在,cookies:" + p.getCookieString());
			sessionId = newSession(p);
        }
		p.sessionId = sessionId;
	}
	
	public static String newSession(Pages p){
		UUID uuid = UUID.randomUUID();
		String sid = MD5.toMD5(uuid.toString());

		Session.addCookie(JSESSIONID , sid, -1, false,true, p);
		log.info("重置sid:" + sid + ",ip:" + p.ip() + ",url:" + p.request.getRequestURI());
		return sid;
	}
	
	public static void initSession(Pages p){
		if(p.sessionId.length() <= 0){
			initSessionId(p);
		}
		if(p.session == null && p.sessionId.length() > 0){
			p.session = new Session(p.sessionId);
		}
	}
	
	public static void initAppSession(Pages p){
		if(p.session == null){
			//app登录模块嵌入
			Session session = JwtUtil.getSession(p.request);
			p.session = session;
			if(session != null){
				p.sessionId = session.sessionId;
			}
		}
	}

	public static Session getSession(Pages p){
		if(p.sessionId.length() <= 0){
			initSessionId(p);
		}
		if(p.session == null && p.sessionId.length() > 0){
			p.session = new Session(p.sessionId);
		}
		return p.session;
	}
}

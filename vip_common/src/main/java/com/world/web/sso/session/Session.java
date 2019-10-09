package com.world.web.sso.session;


import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.util.sign.EncryDigestUtil;
import com.world.web.Pages;
import com.world.web.jwt.JwtUtil;
import com.world.web.sso.SessionUser;
import com.world.web.sso.rsa.RsaLoginUtil;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
public class Session {
	static Logger log = Logger.getLogger(Session.class.getName());
	final static int defaultTime = 8 * 60 * 60;

//	final static int appDefaultTime = 30 * 24 * 60 * 60;
	//12小时
	final static int appDefaultTime = 12 * 60 * 60;
	//15分钟
//	final static int appDefaultTime = 15 * 60;

	//12小时
	final static int MIN_3_APP = 11 * 60 * 60 * 1000;
//	final static int MIN_3_APP = 15 * 60 * 1000;
	//10分钟
	final static int MIN_3 = 600000;
	final String adminPre = "ADMIN_";
	protected String sessionId;
	private SessionUser sessionUser;
	private SessionUser sessionAdmin;
	private boolean isApp;
	private String appSign;

	public final static String aid = GlobalConfig.session + "aid"; 
	public final static String rid = GlobalConfig.session + "rid";
	public final static String vip = GlobalConfig.session + "vip";
	public final static String aname = GlobalConfig.session + "aname";
	public final static String uid = GlobalConfig.session + "uid";
	public final static String appUid = GlobalConfig.session + "appuid_";
	public final static String uname = GlobalConfig.session + "uname";
	public final static String uon =  GlobalConfig.session + "uon";
	public final static String other =  GlobalConfig.session + "other";
	public final static String ipauth =  GlobalConfig.session + "ipauth";
	public final static String googleauth =  GlobalConfig.session + "googleauth";
	public final static String smsauth =  GlobalConfig.session + "smsauth";
	public final static String SETDOMAIN = "." + GlobalConfig.baseDomain;

	public final static String SESSION_JSON_SUFFIX = "_json";
	
	public Session(String sessionId){ 
		if(sessionId != null)
			this.sessionId = sessionId;
	}
	
	public Session(String sessionId,boolean isApp){ 
		this.sessionId = sessionId;
		this.isApp = isApp;
	}
	
	public String getAppSign() {
		if(appSign == null){
			appSign = "";
		}
		return appSign;
	}

	public void setAppSign(String appSign) {
		this.appSign = appSign;
	}
    public SessionUser getUser(Pages p){
    	if(isApp){
    		sessionUser = (SessionUser) Cache.GetObj(sessionId);
    		if(sessionUser != null){//内存中是有的
    			boolean nupdate = false;//需要更新内存
    			try {
					if(sessionUser.nupdate){
						nupdate = true;
					}
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
    			
    			long now = System.currentTimeMillis();
    			if(((now - sessionUser.lastTime) > MIN_3_APP) || nupdate){//12小时后还在活跃
    				sessionUser.lastTime = now;
    				try {
						if(nupdate){
							sessionUser.nupdate = false;
						}
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
    				//重新设置登录,如果用户继续活跃延长登录时间
    				addAppUser(sessionUser, appDefaultTime, p,true);
    			}
    		}
    		return sessionUser;
    	}
    	
    	if(sessionUser == null){
    		String on = p.GetCookie(uon);
    		String name = p.GetCookie(uname);
    		if(on != null && on.equals("1")){
    			sessionUser = (SessionUser) Cache.GetObj(sessionId);
        		if(sessionUser != null){//内存中是有的
        			String pip = p.ip();
        			if(p.urlViewCode != null && p.urlViewCode.viewCode != null && p.urlViewCode.viewCode.ipCheck){
        				sessionUser.ipv = true;
        			}
        			if(sessionUser.ipv && !pip.equals("127.0.0.1")){
        				if(!sessionUser.lip.equals(p.ip())){
        					deleteUser(p);
        					return null;
        				}
        			}
        			boolean nupdate = false;//需要更新内存
        			try {
						if(sessionUser.nupdate){
							nupdate = true;
						}
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
        			
        			long now = System.currentTimeMillis();
        			if(((now - sessionUser.lastTime) > MIN_3) || nupdate){//三十分钟后还在活跃
        				sessionUser.lastTime = now;
        				try {
							if(nupdate){
								sessionUser.nupdate = false;
							}
						} catch (Exception e) {
							log.error(e.toString(), e);
						}
        				//重新设置登录,如果用户继续活跃延长登录时间
        				addUser(sessionUser, defaultTime, p);
        			}
        		}else{//内存中没有了
        			log.info(name + ",内存中没有找到，过期用户登录：" + sessionId+ ",url:" + p.request.getRequestURI());
        			expired(uon, "0" , p);
        		}
    		}else{//客户端已过期
    			if(on != null){
    				log.info(name + ",客户端on:"+on+"不存在了，过期用户" + sessionId + ",url:" + p.request.getRequestURI() + ",ip:" + p.ip());
        			deleteUser(p);
    			}else{
    				log.info(name + ",客户端on=null，过期用户" + sessionId + ",url:" + p.request.getRequestURI() + ",ip:" + p.ip());
    			}
    		}
    	}
    	return sessionUser;
    }
    
    public SessionUser getUser(String ip){
    	if(sessionUser == null){
    		sessionUser = (SessionUser) Cache.GetObj(sessionId);
    		if(sessionUser != null){
    			if(sessionUser.ipv && !ip.equals("127.0.0.1")){
    				if(!sessionUser.lip.equals(ip)){
    					sessionUser = null;
    				}
    			}
    		}
    	}
    	return sessionUser;
    }
    
    public SessionUser getAdmin(Pages p){
    	if(sessionAdmin == null){
    		sessionAdmin = (SessionUser) Cache.GetObj(adminPre + sessionId);
    		if(sessionAdmin != null){
    			String pip = p.ip();
    			if(sessionAdmin.ipv && !pip.equals("127.0.0.1")){
    				if(!sessionAdmin.lip.equals(pip)){

						log.info("IP不一致剔出登录  session记录的IP:" + sessionAdmin.lip + " ,当前IP:" + pip);

    					deleteAdmin(p);
    					return null;
    				}
    			}
    			long now = System.currentTimeMillis();
    			if((now - sessionAdmin.lastTime) > MIN_3){//三十分钟后还在活跃
    				sessionAdmin.lastTime = now;
    				addAdmin(sessionAdmin, defaultTime, p);
    			}
    		}else{
    			String on = p.GetCookie(Session.rid);
    			if(on != null){//过期客户端
    				expired(rid, "" , p);
    			}
    		}
    	}
    	return sessionAdmin;
    }
    
    public void deleteUser(Pages p){
    	if(sessionId != null){
    		log.info("用户退出系统sid:" + sessionId);
    		Cache.Delete(sessionId);
    		Cache.Delete(sessionId + SESSION_JSON_SUFFIX);
    		//删除rsa信息
			Cache.Delete(RsaLoginUtil.rsaSessionPre + sessionId);
    		if(sessionUser != null){
    			Cache.Delete(uid + sessionUser.uid);
    		}
    		sessionUser = null;
    	}
    	try {
    		if(getAdmin(p) == null){
    			expired(SsoSessionManager.JSESSIONID, sessionId , p);
    		}
    		expired(uon, "0" , p);
    		expired(uname, "" , p);
    		expired(uid, "" , p);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
    }
    
    public void expired(String key , String value , Pages p){
    	Cookie c = new Cookie(key, value);
		c.setMaxAge(0);// s为单位，2分钟
		c.setPath("/");
		c.setDomain(SETDOMAIN);
		p.response.addCookie(c);
    }
    
    public void deleteAdmin(Pages p){
    	if(sessionId != null){
    		sessionAdmin = null;
    		Cache.Delete(adminPre + sessionId);
    		//删除rsa信息
			Cache.Delete(RsaLoginUtil.rsaSessionPre + sessionId);
    		SsoSessionManager.newSession(p);
    	}
    	expired(aid, "0" , p);
    	expired(aname, "0" , p);
    	expired(rid, "0" , p);
    }
    
    public boolean addUser(SessionUser sessionUser , int remember , Pages p){
    	addCookie(uname, sessionUser.uname, remember, true,false, p);
		addCookie(uon, "1", remember, false,false, p);
		addCookie(uid, sessionUser.uid, remember, false,false, p);
//		addCookie(vip, sessionUser.rid, remember, false,false, p);
		addCookie(SsoSessionManager.JSESSIONID , p.sessionId, -1, false,true, p);
		log.info("用户：" + sessionUser.uname + "登录系统" + "sid:" + p.sessionId + ",ip:" + p.ip());
		if(sessionUser.others == null){
			sessionUser.others = new JSONObject();
		}
//		addCookie(other , sessionUser.others.toJSONString(), -1, false,false, p);
		Cache.Set(uid + sessionUser.uid, sessionId , remember);
    	Cache.SetObj(sessionId + SESSION_JSON_SUFFIX, JSONObject.toJSONString(sessionUser), remember);
    	return Cache.SetObj(sessionId, sessionUser , remember);
    }
    
    public boolean addAppUser(SessionUser sessionUser , int remember , Pages p,boolean isChangeSessionId){
    	UUID uuid = UUID.randomUUID();
    	if(!isChangeSessionId){
			sessionId = EncryDigestUtil.digestSha256(uuid.toString()+ "_" + System.currentTimeMillis());
		}

    	JSONObject jo = new JSONObject();
    	jo.put(uname, sessionUser.uname);
    	jo.put(uid, sessionUser.uid);
    	jo.put(SsoSessionManager.JSESSIONID, sessionId);
    	jo.put(uname, sessionUser.uname);
    	
    	
		log.info("用户：" + sessionUser.uname + "登录系统" + "sid:" + p.sessionId + ",ip:" + p.ip());
		if(sessionUser.others == null){
			sessionUser.others = new JSONObject();
			jo.put(other, sessionUser.others.toJSONString());
		}
		Cache.Set(appUid + sessionUser.uid, sessionId , appDefaultTime);
		
		try {
			log.info("json4loginUser:"+jo.toJSONString());
			String sign = JwtUtil.getJWTSign(jo.toJSONString());
			log.info("sign of current loginUser:"+sign);
			this.setAppSign(sign);
			if(sessionUser.others == null){
				JSONObject json = new JSONObject();
				json.put("token",sign);
				sessionUser.others = json;
			}else{
				sessionUser.others.put("token",sign);
			}
			log.info("sessionUser.others:"+sessionUser.others.toJSONString());
			//log.info(sign);
		} catch (InvalidKeyException e) {
			log.error(e.toString(), e);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.toString(), e);
		}

        Cache.SetObj(sessionId + SESSION_JSON_SUFFIX, JSONObject.toJSONString(sessionUser), appDefaultTime);
    	return Cache.SetObj(sessionId, sessionUser , appDefaultTime);
    }
    
    public boolean addAdmin(SessionUser sessionUser , int remember , Pages p){
    	addCookie(aid, sessionUser.uid, remember, false,false, p);
		addCookie(aname, sessionUser.uname, remember, true,false, p);
		addCookie(rid, sessionUser.rid, remember, false,false, p);
		addCookie(SsoSessionManager.JSESSIONID , p.sessionId, remember, false,true, p);
    	return Cache.SetObj(adminPre + sessionId, sessionUser , remember);
    }
    
    public static void addCookie(String name , String value , int time , boolean isDecode  , boolean isHttp, Pages p) {
		if(isDecode){
			try {
				value = URLEncoder.encode(value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.error(e.toString(), e);
			}
		}
		Cookie cu = new Cookie(name, value);
		cu.setMaxAge(time);// 存储时间
		cu.setDomain(SETDOMAIN);
		cu.setPath("/");
		if(isHttp){
			cu.setHttpOnly(true);
			//cu.setSecure(true);
		}
		p.response.addCookie(cu);
	}

    public static void resetOrAddCookie(String name , String value , int time , boolean isDecode, boolean isHttp, Pages p) {
        if(isDecode){
            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error(e.toString(), e);
            }
        }

        Cookie result = null;
        Cookie[] cookies = p.request.getCookies();
        if(null != cookies){
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    result = cookie;
                    result.setValue(value);
                    break;
                }
            }
        }

        if(null == result){
            result = new Cookie(name, value);
        }
        result.setMaxAge(time);// 存储时间
        result.setDomain(SETDOMAIN);
        result.setPath("/");
        if(isHttp){
            result.setHttpOnly(true);
        }
        p.response.addCookie(result);
    }
}

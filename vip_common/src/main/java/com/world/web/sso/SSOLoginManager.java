package com.world.web.sso;

import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.model.dao.admin.role.AdminRoleDao;
import com.world.model.entity.admin.role.AdminRole;
import com.world.web.Pages;
import com.world.web.sso.session.Session;
import com.world.web.sso.session.SsoSessionManager;
import org.apache.log4j.Logger;

public class SSOLoginManager {

	private final static Logger log = Logger.getLogger(SSOLoginManager.class.getName());
	
	public static void toLogin(Pages p , int remember , String userId , String userName , boolean isSafe , String ip, String rid ,String validType , boolean isAdmin){
		toLogin(p, remember, userId, userName, isSafe, ip, rid, validType, isAdmin, null);
	}
	
	/***
	 * 
	 * @param p
	 * @param remember  登录的有效时间
	 * @param userId   用户id
	 * @param userName 用户名
	 * @param isSafe  是否需要安全登录
	 * @param ip 登录ip
	 * @param rid
	 * @param validType
	 * @param isAdmin
	 */
	public static void toLogin(Pages p , int remember , String userId , String userName , boolean isSafe , String ip, String rid ,String validType , boolean isAdmin,JSONObject others){
		try{
			SsoSessionManager.initSession(p);
			SessionUser su = new SessionUser();
			su.uid = userId;//用户id
			su.uname = userName;//用户名
			su.ltime = System.currentTimeMillis();//登录时间
			su.lip = ip;//登录ip
			su.lastTime = su.ltime;//最后活动时间
			su.ipv = isAdmin;//ip验证  后端验证前端先不验证了
			su.rid = rid;//角色id
			su.validType = validType;//googlevalid
			su.others = others;
			
	    	if(isAdmin){//管理员登录
	    		AdminRoleDao arDao = new AdminRoleDao();
	    		AdminRole data = arDao.getById(rid);
	    		if(data.getPid() != null && data.getPid().length() > 0){
	    			su.prid = data.getPid();
	    		}
	    		p.session.addAdmin(su , remember , p);
	    	}else{
	    		if(others != null && others.getIntValue("app") != 0){
	    			p.session.addAppUser(su , remember , p,false);
	    		}else{
	    			p.session.addUser(su , remember , p);
	    		}
	    		
	    	}
	    }catch(Exception e){
	    	log.error(e.toString(), e);
	    }
	}
	
	public static void logout(Pages p , boolean isAdmin){
		SsoSessionManager.initSession(p);
		if(p.session != null){
			if(!isAdmin){
				p.session.deleteUser(p);
			}else{
				p.session.deleteAdmin(p);
			}
		}
	}
	
	public static void logout(String userId){
			String sessionId = Cache.Get(Session.uid + userId);
			if(sessionId != null){
				Cache.Delete(sessionId);
			}
	}
	/****
	 * 获取sessionUser
	 * @param userId
	 * @return
	 */
	public static SessionUser getSession(String userId){
		String sessionId = Cache.Get(Session.uid + userId);
		if(sessionId != null){
			SessionUser su = (SessionUser)Cache.GetObj(sessionId);
			return su;
		}
		return null;
	}
	/***
	 * 重置sessionUser
	 * @param userId
	 * @param su
	 */
	public static void resave(String userId , SessionUser su){
		int app = su.getOthers().getIntValue("app");
		String suid = Session.uid;
		if(app>0){
			suid = Session.appUid;
		}
		String sessionId = Cache.Get(suid + userId);
		if(sessionId != null && su != null){
			su.nupdate = true;
			Cache.SetObj(sessionId, su);
			Cache.SetObj(sessionId + Session.SESSION_JSON_SUFFIX, su);
		}
	}
	/***
	 * 修改等级
	 * @param userId
	 * @param vip
	 */
	public static void updateVip(String userId , int vip){
		SessionUser su = getSession(userId);
		
		if(su != null){
			su.rid = vip + "";
			resave(userId, su);
		}
	}
}

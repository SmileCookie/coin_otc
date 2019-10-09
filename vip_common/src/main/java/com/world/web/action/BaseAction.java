package com.world.web.action;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import com.alibaba.fastjson.JSONObject;
import com.world.util.string.StringUtil;
import org.apache.log4j.Logger;

import com.googleauth.GoogleAuthenticator;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.admin.AdminUser;
import com.world.util.date.TimeUtil;
import com.world.util.language.LanguageTag;
import com.world.util.page.Paging;
import com.world.web.Pages;
import com.world.web.defense.IpDefenseDao;
import com.world.web.param.ReqParamType;
import com.world.web.sso.SessionUser;
import com.world.web.sso.session.Session;
import com.world.web.sso.session.SsoSessionManager;

/***
 * 
*   
* 修改备注：  
* @version   
*
 */
public class BaseAction extends Pages {
	
	protected static Logger log = Logger.getLogger(BaseAction.class.getName());
	Paging p = new Paging();//
	public final int PAGE_SIZE = 10;//默认分页显示条数
	/****
	 * 获取request请求数据
	 * @param param
	 * @return
	 */
	protected String getByParam(String param) {
			String val = request.getParameter(param);
			try {
				//if(TranCharset.getEncoding(param).equals("GB2312")){
					//val = val != null ? new String(val.getBytes("ISO-8859-1"), "UTF-8") : "";
				//}
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
			return val;
	}


	protected Object param(int index , ReqParamType type) {
		String val = GetPrama(index);
		
		Object o = null;
		if(val.length() > 0){
			o = getValByType(val, type);
		}else{
			if(type.equals(ReqParamType.STRING) || type.equals(ReqParamType.TIMESTAMP)){
				o = getValByType("", type);
			}else{
				o = getValByType("0", type);
			}
		}
		return o;
	}
	
	protected Object param(String param , ReqParamType type) {
		String val = getByParam(param);
		if(val == null){
			val = "";
		}
		
		Object o = null;
		if(val.length() > 0){
			o = getValByType(val, type);
		}else{
			if(type.equals(ReqParamType.STRING)){
				o = getValByType("", type);
			}else if(type.equals(ReqParamType.TIMESTAMP)){
				o = null;
			}else{
				o = getValByType("0", type);
			}
			
		}
		return o;
	}
	
	protected Integer intParam(int index){
		return (Integer) param(index, ReqParamType.INT);
	}
	
	protected Double doubleParam(int index){
		return (Double) param(index, ReqParamType.DOUBLE);
	}
	
	protected Float floatParam(int index){
		return (Float) param(index, ReqParamType.FLOAT);
	}
	
	protected Long longParam(int index){
		return (Long) param(index, ReqParamType.LONG);
	}
	
	protected Boolean booleanParam(int index){
		return (Boolean) param(index, ReqParamType.BOOLEAN);
	}
	
	protected Integer intParam(String param){
		return (Integer) param(param, ReqParamType.INT);
	}
	
	protected Double doubleParam(String param){
		return (Double) param(param, ReqParamType.DOUBLE);
	}
	
	protected Float floatParam(String param){
		return (Float) param(param, ReqParamType.FLOAT);
	}
	
	protected Long longParam(String param){
		return (Long) param(param, ReqParamType.LONG);
	}
	
	protected Boolean booleanParam(String param){
		return (Boolean) param(param, ReqParamType.BOOLEAN);
	}
	
	protected Timestamp dateParam(String param){
		return (Timestamp) param(param, ReqParamType.TIMESTAMP);
	}
	
	protected Object getValByType(String val , ReqParamType type){
		return type.getVal(val);
	}
	
	//页面内转向
	protected void forward(String path){
		try {
			request.getRequestDispatcher(path).forward(request,response);
		} catch (ServletException e) {
			log.error(e.toString(), e);
		} catch (IOException e) {
			log.error(e.toString(), e);
		}
	}

	
	protected Timestamp now(){
		return TimeUtil.getNow();
	}
	/****
	 * 设置分页  调用此方法后前台列表  的分页页码部分即可显示
	 * @param total 总条数
	 * @param pageNo 当前页码
	 */
	protected void setPaging(int total , int pageNo){
		setPaging(total , pageNo , PAGE_SIZE);
	}
	/*****
	 * @param total 总记录数
	 * @param pageNo 页码
	 * @param pageSize 每页的显示数量
	 */
	protected String setPaging(int total , int pageNo ,int pageSize){
		//log.info("total:"+total+",pageNo:"+pageNo+",pageSize:"+pageSize);
		pageNo = pageNo <= 0 ? 1 : pageNo;
		
		String pn = p.getPaper(pageSize, pageNo, total, request,getLanTag());
		setAttr("pager", pn);
		return pn;
		//log.info(pn);
	}
	/**
	 * 
	 * @param total
	 * @param pageNo
	 * @param pageSize
	 * @param params 适合在ajax 后面需要其他路径时的操作
	 * @return
	 */
	protected String setPaging(int total , int pageNo ,int pageSize,String params){
		pageNo = pageNo <= 0 ? 1 : pageNo;
		
		String pn = p.getPaper(pageSize, pageNo, total, request,getLanTag(),params);
		setAttr("pager", pn);
		return pn;
	}
	
	/***
	 * 用于有tab项的分页查询
	 * @param total
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	protected String setTabPaging(int total , int pageNo ,int pageSize){
		String version = param(1);
		String params = "";
		if(version.length() > 0){
			params = coint.getStag() + "/" + version;
		}
		
		String pn = setPaging(total, pageNo, pageSize, params);
		return pn;
	}
	
	
	protected void go(String url) {
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			log.error(e.toString(), e);
		}
	}
	
	protected void notFound(){
		try {
			response.sendRedirect("/errors/404.jsp");
		} catch (IOException e) {
			log.error(e.toString(), e);
		}
	}
	
	/*****
	 * 后台提示页面
	 * @param tips
	 * @param nextPage
	 * @param removeCookie
	 */
	public void tip(String tips, String nextPage , boolean removeCookie) {
		setAttr("tips", tips);
		setAttr("nextPage", nextPage);
		setAttr("removeCookie", removeCookie);
		setAttr("close", false);
		forward("/cn/user/blank.jsp");
	}

	/**
	 * 配合前端改造
	 * @param tips
	 * @param nextPage
	 * @param removeCookie
	 */
	public void tipJson(String tips, String nextPage , boolean removeCookie) {
		Map<String, Object> result = new HashMap<>();

		result.put("tips", tips);
		result.put("nextPage", nextPage);
		result.put("removeCookie", removeCookie);
		result.put("close", false);
		result.put("forward","/cn/user/blank.jsp");

		json("ok", true, JSONObject.toJSONString(result), true);
	}
	
	public LanguageTag getLanTag(){
		int lanKey = 1;
		if("cn".equals(lan)){
			lanKey = 1;
		}else if("en".equals(lan)){
			lanKey = 2;
		}else if("hk".equals(lan) || "tw".equals(lan)){
			lanKey = 3;
		}else if("jp".equals(lan)){
			lanKey = 4;
		}else if("kr".equals(lan)){
			lanKey = 5;
		}
		return (LanguageTag) EnumUtils.getEnumByKey(lanKey, LanguageTag.class);
	}

	public LanguageTag getLanTag(String lang){

		int lanKey = 2;
		if (StringUtil.exist(lang)) {
			if(lang.equals("cn")){
				lanKey = 1;
			}else if(lang.equals("en")){
				lanKey = 2;
			} else if (lang.equals("hk") || lang.equals("tw")) {
				lanKey = 3;
			}else if(lang.equals("jp")){
				lanKey = 4;
			}else if(lang.equals("kr")){
				lanKey = 5;
			}
		}
		return (LanguageTag) EnumUtils.getEnumByKey(lanKey, LanguageTag.class);
	}
	
	public boolean codeCorrect(){
		return codeCorrect(JSON);
	}
	
	public boolean codeCorrect(String gs){
		try {
//			int needCode = Cache.Get("code_")
			
			long code = intParam("mCode");
			String userId = GetCookie(Session.aid);
			if(userId == null || userId.length() == 0){
				if(gs.equals(XML))
					WriteError("没有管理员权限。");
				else
					json("没有管理员权限。", false, "");
				//response.sendRedirect("/admin/login");
				return false;
			}
			
			AdminUser aUser = new AdminUserDao().get(userId);
			if(aUser.getSecret()==null || aUser.getSecret().length() == 0){
				int roleId = aUser.getAdmRoleId();
				if((roleId == 1 || roleId == 3 || roleId == 6) && !aUser.getAdmName().equals("admin")){
					if(gs.equals(XML))
						WriteError("您还没有谷歌认证。");
					else
						json("您还没有谷歌认证。", false, "");
					return false;
				}else{
					return true;
				}
			}
			if(isCorrect(aUser.getSecret(), code, gs)){
				return true;
			}
		} catch (Exception e1) {
			log.error(e1.toString(), e1);
			if(gs.equals(XML))
				WriteError(L("请输入谷歌验证码。"));
			else
				json(L("请输入谷歌验证码。"), false, "");
		}
		return false;
	}
	
	public static int times = 6;//允许错误的次数
	//判断移动设备验证码是否正确
	protected boolean isCorrect(String savedSecret, long code, String gs){
		
		if(code == 0){
			if(gs.equals(XML))
				WriteError(L("请输入移动设备上生成的验证码。"));
			else
				json(L("请输入移动设备上生成的验证码。"), false, "");
			
			return false;
		}
		
		Object o = request.getAttribute("adminId");
		String userId = null;
		if(o == null){
			if (session == null) {
				SsoSessionManager.initSession(this);
			}
			SessionUser su = session.getAdmin(this);
			if(su != null){
				userId = su.uid;
			}
			if(userId == null || userId.length() == 0){
				return false;
			}
		}else{
			userId = (String) o;
			request.setAttribute("adminId", null);
		}
		
		String key = userId+"_gauth";
		
		long t = System.currentTimeMillis();
		GoogleAuthenticator ga = new GoogleAuthenticator();
		ga.setWindowSize(3);

		boolean r = ga.check_code(savedSecret, code, t);
		if(!r){
			lock2Hours(gs, key, L("谷歌"));
			return false;
		}else{
			Object current = Cache.GetObj(key);
			if(current != null){
				int count = Integer.parseInt(current.toString());
				if(count >= times){
					if(gs.equals(XML))
						WriteError(L("谷歌验证码连续输入错误的次数太多，请2小时后再试。"));
					else
						json(L("谷歌验证码连续输入错误的次数太多，请2小时后再试。"), false, "");
					return false;
				}
				Cache.Delete(key);
			}
		}
		return true;
	}
	
	public void lock2Hours(String gs, String key, String tit){
		Object current = null;
		current = Cache.GetObj(key);
		if(current == null){
			Cache.SetObj(key, 1, 60*60*2);
			
			if(gs.equals(XML)){
				if((times-1) > 1){
					WriteError(tit+L("验证码输入有误，您还有%%次机会s",(times-1)+""));
				}else{
					WriteError(tit+L("验证码输入有误，您还有%%次机会",(times-1)+""));
				}
			}
			else{
				if((times-1) > 1){
					json(tit+L("验证码输入有误，您还有%%次机会s",(times-1)+""), false, "");
				}else{
					json(tit+L("验证码输入有误，您还有%%次机会",(times-1)+""), false, "");
				}

			}
		}else{
			int count = Integer.parseInt(current.toString());
			count++;
			Cache.SetObj(key, count, 60*60*2);
			if(count < times){
				if(gs.equals(XML)){
					if((times-1) > 1){
						WriteError(tit+L("验证码输入有误，您还有%%次机会s",(times-1)+""));
					}else{
						WriteError(tit+L("验证码输入有误，您还有%%次机会",(times-1)+""));
					}
				}else{
					if((times-1) > 1){
						json(tit+L("验证码输入有误，您还有%%次机会s",(times-1)+""), false, "");
					}else{
						json(tit+L("验证码输入有误，您还有%%次机会",(times-1)+""), false, "");
					}
				}
			}else{
				if(gs.equals(XML)){
					WriteError(tit+L("验证码连续输入错误的次数太多，请2小时后再试。",(times-1)+""));
				}else{
					json(tit+L("验证码连续输入错误的次数太多，请2小时后再试。",(times-1)+""), false, "");
				}
			}
		}
	}
	
	
	//验证手机
	protected boolean isCorrect(String code, String gs){
		String userId = GetCookie(Session.aid);
		if(userId == null || userId.length() == 0){
			Object o = request.getAttribute("adminId");
			if(o != null){
				userId = o.toString();
				request.setAttribute("adminId", null);
			}else{
				return false;
			}
		}
		
		if(code==null || "".equals(code)){
			if(gs.equals(XML))
				WriteError(L("请输入发送到您手机上的短信验证码。"));
			else
				json(L("请输入发送到您手机上的短信验证码。"), false, "");
			
			return false;
		}
		
		String checkCode = Cache.Get("valiC_"+userId);
		if(checkCode == null || "".equals(checkCode)){
			if(gs.equals(XML))
				WriteError(L("请获取短信验证码。"));
			else
				json(L("请获取短信验证码。"), false, "");
			
			return false;
		}
		
		String key = userId+"_mce";
		if(!checkCode.equals(code)){
			lock2Hours(gs, key, L("短信"));
			return false;
		}else{
			Object current = Cache.GetObj(key);
			if(current != null){
				int count = Integer.parseInt(current.toString());
				if(count >= times){
					if(gs.equals(XML))
						WriteError(L("短信验证码连续输入错误的次数太多，请2小时后再试。"));
					else
						json(L("短信验证码连续输入错误的次数太多，请2小时后再试。"), false, "");
					return false;
				}
				Cache.Delete(key);
			}
		}
		return true;
	}
	
	private static int forbidTimes = 200;//一小时请求的次数
	private static int time = 60;//60分钟
	private static String bid_ey = "forbid_";
	public boolean isForbid(){
		String ip = ip();
		log.info("========================当前验证用户IP：" + ip +",user:" + userName());
		String cacheIp = Cache.Get(bid_ey + ip);
		if (cacheIp == null) {
			Cache.Set(bid_ey + ip, "1_"+System.currentTimeMillis(), 60*60*2);
		} else {
			int count = Integer.parseInt(cacheIp.split("\\_")[0]);
			count++;
			if (count < forbidTimes) {
				Cache.Set(bid_ey + ip, count+"_"+cacheIp.split("\\_")[1], 60*60*2);
			} else if(count == forbidTimes) {
				long old = Long.parseLong(cacheIp.split("\\_")[1]);
				long now = System.currentTimeMillis();
				long minute = (now - old)/(1000*60);
				if(minute < time){
					log.info("===="+ip+"已禁====");
					Cache.Set(bid_ey + ip, forbidTimes+"_"+cacheIp.split("\\_")[1], 60*60*2);
					if (GlobalConfig.ipDefenseOpen) {
						try {
							///加入黑名单
							IpDefenseDao ipDefenseDao = new IpDefenseDao();
							ipDefenseDao.addHeibai(ip, 0, System.currentTimeMillis() + 60 * 60 * 2 * 1000);
						} catch (Exception e) {
							log.error(e.toString(), e);
						}
					}
					return true;
				}else{
					Cache.Delete(bid_ey + ip);
				}
			}else{
				log.info("====已锁定==="+count+"=");
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取缓存的用户信息
	 * @param appUid
	 * @param userId
	 * @return
	 */
	public SessionUser getSessionUser(String appUid,String userId){
		String loginCacheKey = appUid + userId;
		SessionUser sessionUser = null;
		if (null != Cache.GetObj(loginCacheKey)) {
			Object obj = Cache.GetObj(loginCacheKey);
			if (null != obj) {
				sessionUser = (SessionUser) Cache.GetObj(obj.toString());
			}
		}
		return sessionUser;
	}
}

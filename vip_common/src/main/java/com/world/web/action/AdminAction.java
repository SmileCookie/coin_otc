package com.world.web.action;

import java.io.IOException;
import java.util.List;

import com.world.model.dao.admin.competence.RoleFunctionManager;
import com.world.model.entity.admin.competence.MenuViewFunction;
import org.apache.commons.lang.StringUtils;

import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.data.mysql.Data;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.dao.ips.IpsDao;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.ip.Ips;
import com.world.web.sso.SessionUser;
import com.world.web.sso.session.SsoSessionManager;

/****
*   
* 类名称：UserAction  
* 类描述：  
* 修改备注：  
* @version   
*
*/
public class AdminAction extends BaseAction{

	/*****
	 * 获取当前用户ID
	 * @param isRidirect 是否返回历史页面  对于xml的请求isHistory=false
	 * @param end  是否结束方法，方法是否继续执行
	 * @param isIframe 是否处于iframe模式
	 * @return
	 */
	protected int adminId() {
		
		try {
			if (session == null) {
				SsoSessionManager.initSession(this);
			}
			SessionUser su = session.getAdmin(this);
			if(su != null){
				return Integer.parseInt(su.uid);
			}
		} catch (NumberFormatException e) {
			log.error(e.toString(), e);
		}
		return 0;
	}
	
	protected int roleId() {
		
		try {
			if (session == null) {
				SsoSessionManager.initSession(this);
			}
			SessionUser su = session.getAdmin(this);
			if(su != null){
				return Integer.parseInt(su.rid);
			}
		} catch (NumberFormatException e) {
			log.error(e.toString(), e);
		}
		return 0; 
	}
	
	
	protected String adminName(){ 
		
		if (session == null) {
			SsoSessionManager.initSession(this);
		}
		SessionUser su = session.getAdmin(this);
		if(su != null){
			return su.uname;
		}
		return "";
	}
	
	protected boolean couldSearch(){
		String key = "au_admin_"+adminId();
		Object current = Cache.GetObj(key);
		if(current != null){
			return true;
		}

		AdminUser aUser = new AdminUserDao().get(adminId()+"");
		try {
			if(aUser.getAdmName().equals("admin")){
				return true;
			}
			if(aUser.getSecret()==null || aUser.getSecret().length() == 0){
				response.sendRedirect("/admin/logout");
				return false;
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		
		long code = intParam("mCode");
		if(!isCorrect(aUser.getSecret(), code, JSON)){
			setAttr("error", "验证码输入错误。");
			return false;
		}
		Cache.SetObj(key, 1, 60*10);//一次正确之后，保存10分钟
		return true;
	}
	
	protected boolean ipCheck(){
		String ip = ip();
		String canIps = "";
		if(GlobalConfig.ipFromMysql){
			List list = (List) Data.GetOne("select * from ips", new Object[]{});
		    canIps = (String)list.get(1);
		}else{
			IpsDao idao = new IpsDao();
			Ips ips = idao.findOne(idao.getQuery());
			if(ips != null){
				canIps = idao.findOne(idao.getQuery().limit(1)).getIps();
			}else{
				ips = new Ips(idao.getDatastore());
				ips.setIps("127.0.0.1");
				idao.save(ips);
			}
		}
	    if(canIps == null || canIps.indexOf(ip) < 0){
	    	String[] ips = ip.split("[.]");
	    	if(canIps.contains("*.*")){
	    		String ql = ips[0] + "." + ips[1];
	    		String needIp = ql + ".*" + ".*";
	    		if(canIps.indexOf(needIp) < 0){
	    			return false;
	    		}else{//模糊匹配
	    			return true;
	    		}
	    	}else if(canIps.contains(".*")){
	    		String ql = ips[0] + "." + ips[1] + "." + ips[2];
	    		String needIp = ql + ".*";
	    		if(canIps.indexOf(needIp) < 0){
	    			return false;
	    		}else{//模糊匹配
	    			return true;
	    		}
	    	}
	    	return false;
	    }else{
	    	return true;
	    }
	}

	private static long starttime;
	private static long endtime;
	protected static long markStartTime(){ 
		starttime = System.currentTimeMillis();
		return starttime;
	}
	protected static long markEndTime(){
		endtime = System.currentTimeMillis();
		return endtime;
	}
	protected static void printCoseTime(String msg,String unit) {
		markEndTime();
		long unitvalue = 1;
		
		if(StringUtils.isBlank(msg)){
			msg = "";
		}
		if(StringUtils.isNotBlank(unit)){
			unit = unit.toLowerCase();
			if("ms".equals(unit)){
				unitvalue = 1;
			}else if ("s".equals(unit)) {
				unitvalue = 1000;
			}else{
				unitvalue = 1;
				unit = "ms";
			}
		}else{
			unit = "ms";
			unitvalue = 1;
		}
		log.debug("### " + msg + " 耗时:" + (endtime - starttime)/unitvalue + "(" + unit + ")");

	}

	//xzhang 20170824 新增单一权限控制方法，根据实际情况调用
	protected boolean checkRole(){
		int roleId = roleId();
		String urlLs = reqURI.toLowerCase();
		if (urlLs.indexOf('-') > 0)
			urlLs = urlLs.substring(0, urlLs.indexOf('-'));
		MenuViewFunction menuViewFunction = RoleFunctionManager.getFunction(urlLs, roleId+"");
		if(menuViewFunction == null){
			SessionUser su = session.getAdmin(this);
			if(su != null){
				if(su.prid != null){
					menuViewFunction = RoleFunctionManager.getFunction(urlLs, su.prid);
				}
			}
		}
		if (menuViewFunction == null) {
			return false;
		}
		return true;
	}
}

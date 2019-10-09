package com.world.controller.ad_admin.admin_login;

import java.util.List;

import com.world.cache.Cache;
import com.world.data.mysql.Data;
import com.world.model.dao.account.AdminLogin;
import com.world.model.dao.admin.logs.DailyRecordDao;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.admin.logs.DailyType;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.sso.SSOLoginManager;

public class Index extends AdminAction {

	@Page(Viewer = "/admins/login.jsp")
	public void index() {
		String ip = ip();
		log.info("ip:" + ip + "访问总后台登录。");
		try {
		    if(!ipCheck()){
		    	log.error("非法访问总后台客户IP:" + ip + "跳转到首页。");
		    	response.sendRedirect(MAIN_DOMAIN);
			    return;
		    }
			// response.getWriter().write("访问的页面2");
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}

	@Page(Viewer = ".xml")
	public void doLogin() {
		String uname = param("UserName");
		String upwd = param("Password");
		String code = param("mCode");
		long mCode = 0;
		if(code.trim().length() > 0){
			mCode = Long.parseLong(code);
		}
		
		AdminUserDao auDao = new AdminUserDao();
		AdminUser user = auDao.getByField("admName", uname);
		if(user == null){
			WriteError("用户不存在。");
			return;
		}
		
		if(user.getIsLocked() == 1){
			WriteError("已经被锁定。");
			return;
		}
		
		String validType = param("loginType");
		//SessionSet("validType",validType,24*3600*2);
		request.setAttribute("adminId", user.getAdmId());
		
		if(validType.equals("1")){
			if(user.getSecret()!=null && user.getSecret().length() > 0){
				if(!isCorrect(user.getSecret(), mCode, XML)){
					return;
				}
			}
		}else{
			if(!isCorrect(code, XML)){
				return;
			}
		}
		
		AdminLogin al = new AdminLogin();
		boolean dologin = al.DoLogin(uname, upwd, request, response,ip());
		if (dologin){
			
			Cache.Delete("valiC_"+user.getAdmId());
			SSOLoginManager.toLogin(this, 24*3600*2, user.getAdmId(), user.getAdmName(), true, ip(), String.valueOf(user.getAdmRoleId()), validType, true);
			//toLogin(user,"1",24*3600*2);
			Write("登录成功", true, "登录成功!");
			
			try {
				//插入一条管理员日志信息
				DailyType type = DailyType.adminLogin;
				new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "管理员登录", uname), user.getAdmId(), ip(), now());
			} catch (Exception e) {
				log.error("添加管理员日志失败", e);
			}
		}
		//else
			//Write("登录失败", false, "请检查您的用户名或者密码!");
	}

	@Page(Viewer = "/admin/login.jsp")
	public void LoginOut() {

	}

//	@Page(Viewer = JSON)
//	public void postCode(){
//		try {
//			//发送验证码
//			String admName = param("admName");
//			int adminId = adminId();
//
//			AdminUserDao auDao = new AdminUserDao();
//			AdminUser user = null;
//			if(adminId > 0){
//				user = auDao.get(adminId+"");
//			}else{
//				if(admName.length() == 0){
//					json("请输入用户名。", false, "");
//					return;
//				}
//				user = auDao.getByField("admName", admName);
//			}
//			if(user == null){
//				return;
//			}
//			String nCode = Cache.Get("valiC_"+user.getAdmId());
//			if(nCode != null){
//				json("继续使用上次的验证码。", true, "");
//				return;
//			}
//			
//			String code = MobileDao.GetRadomStr();
//			MobileDao mDao = new MobileDao();
//			mDao.sendSms(new User(adminId+"", user.getAdmName(), "cn"), ip(), PostCodeType.superManagerAuth.getValue(), PostCodeType.superManagerAuth.getDes()+ code, user.getTelphone());
//			log.info(code);
//			Cache.Set("valiC_"+user.getAdmId(), code, 10*60);
//			
//			String mobile = user.getTelphone();
//			json("短信已经发送到号码为"+new UserDao().shortMobile(mobile)+"的手机，请注意查收。", true, "");
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//		}
//	}
}

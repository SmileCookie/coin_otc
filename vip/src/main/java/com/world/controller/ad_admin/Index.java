package com.world.controller.ad_admin;

import java.io.OutputStream;
import java.net.URLDecoder;

import com.world.cache.Cache;
import com.world.model.dao.account.AdminLogin;
import com.world.model.dao.admin.logs.DailyRecordDao;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.user.MobileDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.admin.logs.DailyType;
import com.world.model.entity.user.User;
import com.world.util.qrcode.QRCodeGenerator;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import com.world.web.sso.SSOLoginManager;

@FunctionAction(jspPath = "/admins/" , des = "后台管理")
public class Index extends AdminAction {
	/*
	 * shouye
	 */
	@Page
	public void index() {
		try {
			go("/");
		} catch (Exception ex) {
		}
	}

	@Page(Viewer = "/admins/index.jsp")
	public void admin_manage() {
		try {

		} catch (Exception ex) {
		}
	}
	
	/**
	 * 功能:退出登录
	 */
	@Page
	public void logout() {
		try {

			try {
				SSOLoginManager.logout(this, true);
			} catch (Exception ex) {
				log.error("清空Cookies发生异常！", ex);
			}
			String refer = request.getHeader("Referer");
			log.debug("退出登录来源：" + refer);
			if (refer == null || refer == "")
				response.sendRedirect(VIP_DOMAIN);
			else
				response.sendRedirect(refer);
		} catch (Exception ex) {
			log.error("退出登录异常", ex);
		}
	}
	
	@Page(Viewer = JSON)
	public void postCode(){
		try {
			//发送验证码
			String admName = param("admName");
			int adminId = adminId();

			AdminUserDao auDao = new AdminUserDao();
			AdminUser user = null;
			if(adminId > 0){
				user = auDao.get(adminId+"");
			}else{
				if(admName.length() == 0){
					json("请输入用户名。", false, "");
					return;
				}
				user = auDao.getByField("admName", admName);
			}
			if(user == null){
				return;
			}
			String nCode = Cache.Get("valiC_"+user.getAdmId());
			if(nCode != null){
				json("继续使用上次的验证码。", true, "");
				return;
			}
			
			String code = MobileDao.GetRadomStr(1);
			MobileDao mDao = new MobileDao();
			mDao.sendSms(new User(adminId+"", user.getAdmName(), "cn"), ip(), PostCodeType.superManagerAuth.getValue(), String.format(PostCodeType.superManagerAuth.getDes(), code) , user.getTelphone());
			log.info(String.format(PostCodeType.superManagerAuth.getDes(), code));
			Cache.Set("valiC_"+user.getAdmId(), code, 10*60);
			
			String mobile = user.getTelphone();
			json("短信已经发送到号码为"+new UserDao().shortMobile(mobile)+"的手机，请注意查收。", true, "");
		} catch (Exception e) {
			log.error("发送验证码异常", e);
		}
	}
	
	@Page(Viewer = "")
	public void getGoogleAuthQr(){
		
		String secret = param("secret");
		String adminId = param("aid");
		String adminName = param("aname");
		if(secret == null || adminId == null || adminName == null){
			return;
		}
		
		try{
			response.setContentType( "image/png" );
			response.setHeader( "Pragma", "No-cache" );
			response.setHeader( "Cache-Control", "no-cache" );
			response.setDateHeader( "Expires", 0 );
			
			OutputStream os = response.getOutputStream();
			
			int width = 150;
			int height = 150;
			
			String myGoogleStr = String.format("otpauth://totp/%s%s%%3Fsecret%%3D%s" , "BitGlobal_ADMIN", "", secret);
			//参与生成二维码的文本
			String codec = URLDecoder.decode(myGoogleStr, "utf-8");
			//生成二维码图片
			QRCodeGenerator.encode(os, codec, width, height);
			
			os.flush();
			os.close();
		}catch(Exception ex){
			log.error("生成二维码异常", ex);
		}
	}
}


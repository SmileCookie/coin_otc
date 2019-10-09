package com.world.controller.manage.auth;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.cache.Cache;
import com.world.constant.Const;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.user.EmailDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.util.string.MD5;
import com.world.web.Page;
import com.world.web.action.ApproveAction;
import com.world.web.convention.annotation.FunctionAction;
import com.world.web.response.DataResponse;
import com.world.web.sso.session.ClientSession;
import com.yc.entity.SysGroups;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;

import java.sql.Timestamp;

@SuppressWarnings("serial")
@FunctionAction(jspPath = "/cn/manage/auth/logic/", des = "")
public class Logic extends ApproveAction {

	UserDao userDao = new UserDao();

//	@Page(Viewer = INDEX)
	public void index() {

	}

	@Page(Viewer = JSON)
	public void postEmail(){
		try {
			if(isForbid()){
				return;
			}
			initLoginUser();

            String step = param("step");
            if(step.equals("hassend") || !step.equals("repost")){
                //资金密码校验放在前面
                String payPwd = param("payPwd");
                if(!safePwdNew(payPwd, userIdStr(), JSON)){
                    return;
                }
            }

			//email发送频率控制 modify renfei
			int uid = userId();
			String key = String.format(Const.MessageSendLimit.EMAIL_SEND_TIME_CACHEKEY, uid);
			long now = System.currentTimeMillis();
			long preSendTime = NumberUtils.toLong(Cache.Get(key), -1);
			if(now - preSendTime < Const.MessageSendLimit.EMAIL_SEND_LIMIT){
				json(L("邮件发送频繁，请稍候再试。") , false , "");
				return;
			}else{
				Cache.Set(key, String.valueOf(now), (int)(Const.MessageSendLimit.EMAIL_SEND_LIMIT/ DateUtils.MILLIS_PER_SECOND) * 2);
			}

			String email = param("email");
			String editE = Cache.Get("editE_"+uid);

			UserContact uc = loginUser.getUserContact();
			if(uc.getEmailStatu() == 2 && uc.getSafeEmail() != null && editE==null){
				email = uc.getSafeEmail();
			}else{
//				if(editE!=null&&editE.indexOf("1_")==0){
//					email = editE.substring(2);
//				}
			}
			if(step.equals("hassend")){
				if (uc.getMobileStatu() == 2) {
					String mobileCode = param("mobileCode");
					// 检查短信验证码
					String codeRecvAddr = loginUser.getUserContact().getSafeMobile();
					if (StringUtils.isBlank(codeRecvAddr)) {
						codeRecvAddr = loginUser.getUserContact().getSafeEmail();
					}
					ClientSession clientSession = new ClientSession(ip(), codeRecvAddr, lan, PostCodeType.emailAuth.getValue(), false);
					DataResponse dr = clientSession.checkCode(mobileCode);
					if (!dr.isSuc()) {
						json(dr.getDes(), false, "");
						return;
					}
				}
				boolean res = userDao.emailEditValidated(email, userIdStr());
				if(!res){
					json(L("邮箱已存在"), false, "");
					return;
				}
			}else{
				if(step.equals("repost")){//重发邮箱
					if(!loginUser.getUserContact().isCanSend()){
						json(L("邮件发送频繁，请稍候再试。") , false , "");
						return;
					}

					//TODO 增加邮箱已验证的判断
//					if(editE == null || editE.indexOf("1_")!=0){
//						json(L("邮箱已验证！") , false , "");
//						return;
//					}
				}
			}

			String emailCode = MD5.toMD5(userId()+email+System.currentTimeMillis());

			UpdateResults<User> ur = userDao.updateEmailCode(loginUser.getId(), !step.equals("hassend") ? null : email, emailCode);
			if (!ur.getHadError()) {
				EmailDao eDao = new EmailDao();

				String info = eDao.getEmailHtml(loginUser, emailCode,this);
				SysGroups sg = SysGroups.vip;
				String title = L(SysGroups.vip.getValue()) + " " + L("邮箱认证");
				eDao.sendEmail(ip(), loginUser.getId(), loginUser.getUserName(), title, info, email);
				log.info(VIP_DOMAIN+"/ac/email?edit=true&step=third&emailCode="+emailCode+"&userId="+userId());
				Cache.Set("editE_"+userId(), "1_"+email, 10*60);
				json(L("邮件已发送"), true, "");

			}else{
				json(L("邮件发送失败"), false, "");
			}

		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

//	@Page(Viewer = "/en/u/safe/approve/email.jsp")
	public void saveEmail(){
		initLoginUser();

		String email = param("email");
		String emailCode = param("emailCode");
		String userId = userIdStr();

		UserContact uc = loginUser.getUserContact();
		if(emailCode != null && emailCode.length() > 0){
			if(uc.getEmailCode() == null || !uc.getEmailCode().equals(emailCode)){
				SetViewerPath("/error.jsp");
				return;
			}else{
				if(!uc.isCanReg()){//过期了
					SetViewerPath("/error.jsp");
					return;
				}
			}
		}
		email = email.trim().toLowerCase();

		boolean res = userDao.emailValidated(email);
		if(!res){
			json(L("邮箱已存在"), false, "");
			return;
		}

		Datastore ds = userDao.getDatastore();
		Query<User> q = ds.find(User.class, "_id", userId);
		UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

		ops.set("userContact.safeEmail", email);
		ops.set("email", email);
		ops.set("userContact.emailCode", "");
		ops.set("userContact.checkEmail", "");
		ops.set("userContact.emailStatu", AuditStatus.pass.getKey());
		ops.set("userContact.emailTime", new Timestamp(0));

		userDao.update(q, ops);

		setAttr("tab", "email");
	}

	/*@Page(Viewer = JSON)
	public void postCode(){
		try {
			boolean isEditeMobile = false;
			initLoginUser();

			UserContact uc = loginUser.getUserContact();
			String mobile = param("mobile");
			String step = param("step");

			////防止手机号码被修改的操作   ？？？？

			if(uc.getMobileStatu() == 2 && uc.getSafeMobile() != null){
				mobile = uc.getSafeMobile();
			}

			if(mobile == null || "".equals(mobile)){
				json(L("输入手机号码"), false, "");
				return;
			}

			if(!step.equals("one")){
				/////内存验证已经处于编辑状态了
				String editS = Cache.Get("editM_"+userId());
				if(editS !=null && editS.equals("1")){///这里添加验证 1.防止刷短信  2.防止走漏短信   之前的漏洞便在此处
					mobile = param("mobile");
					isEditeMobile = true;
				}
				//mobile = param("mobile");
				boolean res = userDao.mobileValidated(mobile);
				if(!res){
					json(L("手机号码已存在"), false, "");
					return;
				}
			}

			if(!isEditeMobile && !couldResend()){
				return;
			}

			if(locked(userIdStr())){
				return;
			}

			//Timestamp codeTime = uc.getCodeTime();
			//if(codeTime == null || System.currentTimeMillis() - codeTime.getTime() > 60000){//验证码发送超过1分钟之后才重新发送

				//发送验证码
				String code = MobileDao.GetRadomStr();
				log.info(code);
				int type = 0;
				MobileDao mDao = new MobileDao();
				if(mDao.couldSend(userIdStr())){
					if(uc.getMobileStatu()==2){
						type = PostCodeType.editMobile.getKey();
						mDao.sendSms(loginUser, ip(), PostCodeType.editMobile.getValue(), PostCodeType.editMobile.getDes()+code, mobile);
					}else{
						type = PostCodeType.mobileAuth.getKey();
						mDao.sendSms(loginUser, ip(), PostCodeType.mobileAuth.getValue(), PostCodeType.mobileAuth.getDes()+code, mobile);
					}
				}else{
					log.info("今日发送已超过规定的次数，不能再发送。");
					json(L("今日发送已超过规定的次数，不能再发送。"), false, "");
					return;
				}

				code = UserUtil.secretMobileCode(type, ip(), code);
				log.info(code);
				if(isEditeMobile){//修改模式
					Cache.Set("editM_mobile_code_"+userId(), mobile + "_" + code, 10*60);
					json(L("短信已经发送到号码为%%的手机，请注意查收。" , mobile), true, "");
				}else{
					UpdateResults<User> ur = userDao.updateMobileCode(loginUser.getId(), mobile, code);
					if (!ur.getHadError()) {
						if(step.equals("one"))
							mobile = null;//验证当前绑定手机的时候就不用更新check字段
						mobile = mobile==null ? userDao.shortMobile(uc.getSafeMobile()) : userDao.shortMobile(mobile);
						json(L("短信已经发送到号码为%%的手机，请注意查收。" , mobile), true, "");
						//json(L("验证码已发送", new String[]{mobile==null ? userDao.shortMobile(uc.getSafeMobile()) : userDao.shortMobile(mobile)}), true, "");

					}else{
						json(L("验证码发送失败"), false, "");
					}
				}
//			}else{
//
//				json(L("一分钟之后再重新获取"), false, "");
//			}

		} catch (Exception e) {
			json(L("内部错误"), false, "");
			log.error(e.toString(), e);
		}
	}*/

	@Page(Viewer = JSON)
	public void editMobile(){
		initLoginUser();

		String code = param("code");
		String payPwd = param("payPwd");

		if(!isCorrect(code, PostCodeType.editMobile.getKey())){//json(L("验证码错误"), false, "");
			return;
		}

		if(!hasEffective()){//json("验证码失效，请重新发送验证码。", false, "");
			return;
		}

		/*add by xwz 20170705 输错密码提示有几次机会*/
//		if(!safePwd(payPwd, userIdStr(), JSON)){
//			return;
//		}
		if(!safePwd(payPwd, userIdStr(), JSON)){
			return;
		}
		/*end*/


		Cache.Set("editM_"+userId(), "1", 10*60);
		//userDao.clearCodeTime(userIdStr());
		json(L("验证成功。"), true, "");
	}

	@Page(Viewer = JSON)
	public void mobileUnique() {
		try {
			initLoginUser();
			userId(false,true);

			String ip = ip();
			log.info("========================当前验证认证手机的用户IP：" + ip);
			String cacheIp = Cache.Get("email_"+ip);
			if(cacheIp == null){
				Cache.Set("email_"+ip, "1", 60*60);
			}else{
				int count = Integer.parseInt(cacheIp);
				if(count < 50){
					count++;
					Cache.Set("email_"+ip, ""+count, 60*60);
				}else{
					json(L("该手机已存在。"), false, "");
					return;
				}
			}

			String val = param("val");
			if (val == null || val.length() == 0) {
				return;
			}
			val = val.trim().toLowerCase();

			Boolean couldUse = userDao.mobileLoginCheckMobile(val);

			if (!couldUse) {
				json(L("手机号码已存在"), false, "");
				return;
			}

			json(L("有效的手机号码"), true, "");
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	@Page(Viewer = JSON)
	public void emailUnique() {
		initLoginUser();
		userId(false,true);

		String ip = ip();
		log.info("========================当前验证认证邮箱的用户IP：" + ip);
		String cacheIp = Cache.Get("email_"+ip);
		if(cacheIp == null){
			Cache.Set("email_"+ip, "1", 60*60);
		}else{
			int count = Integer.parseInt(cacheIp);
			if(count < 20){
				count++;
				Cache.Set("email_"+ip, ""+count, 60*60);
			}else{
				json(L("该邮箱已存在。"), false, "");
				return;
			}
		}

		try {
			String val = param("val");
			if (val == null || val.length() == 0) {
				return;
			}
			val = val.trim().toLowerCase();

			Boolean couldUse = false;

			if(loginUser.getUserContact().getEmailStatu() == AuditStatus.pass.getKey())
				couldUse = userDao.emailValidated(val);
			else
				couldUse = userDao.emailEditValidated(val, userIdStr());

			if (!couldUse) {
				json(L("邮箱已存在"), false, "");
				return;
			}

			json(L("有效的邮箱"), true, "");
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	@Page(Viewer = JSON)
	public void mobileValidate() {
		if(!emailNoSetTips()){
	    	 return;
	    }
		StringBuilder json = new StringBuilder("");
		json.append("{");

		String step = param("step");
		setAttr("curUser", loginUser);
		UserContact uc = loginUser.getUserContact();
		String mobile = uc.getSafeMobile();
		json.append("\"mobileStatu\" : \"" + (mobile != null&&mobile.length()>0 ? 2 : 0) + "\"");
		if(mobile != null&&mobile.length()>0) {
			if(!step.equals("one")){
				String editS = Cache.Get("editM_"+userId());
				if(editS==null || !editS.equals("1")){
					step = "one";
				}
			}
		}
		json.append(",\"step\" : \"" + step + "\"");
		json.append(",\"mobile\" : \"" + userDao.shortMobile(mobile) + "\"");
		json.append(",\"source\" : \"" + mobile + "\"");
		json.append(",\"showAudioButton\" : \"" + uc.isShowAudioButton() + "\"");
		json.append("}");
		json("success",true,json.toString(),true);
	}
}
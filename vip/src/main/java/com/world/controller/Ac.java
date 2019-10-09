package com.world.controller;

import com.Lan;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.cache.Cache;
import com.world.constant.Const;
import com.world.data.mysql.Data;
import com.world.model.dao.account.EncryptionPhoto;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.msg.MsgDao;
import com.world.model.dao.reward.RewardRecordDao;
import com.world.model.dao.user.CountryDao;
import com.world.model.dao.user.EmailDao;
import com.world.model.dao.user.MobileDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.VerifyUserInfoDao;
import com.world.model.dao.user.authen.AuthenticationDao;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.CointTable;
import com.world.model.entity.msg.TipType;
import com.world.model.entity.reward.RewardRecord;
import com.world.model.entity.reward.RewardSource;
import com.world.model.entity.user.Country;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.model.entity.user.VerifyUserInfo;
import com.world.model.entity.user.authen.Authentication;
import com.world.model.jifenmanage.JifenManage;
import com.world.model.singleton.SingletonThreadPool;
import com.world.util.date.TimeUtil;
import com.world.util.qrcode.QRCodeGenerator;
import com.world.util.sign.RSACoder;
import com.world.web.Page;
import com.world.web.Pages;
import com.world.web.action.ApproveAction;
import com.world.web.response.DataResponse;
import com.world.web.sso.rsa.RsaLoginUtil;
import com.world.web.sso.rsa.RsaUser;
import com.world.web.sso.session.ClientSession;
import com.yc.entity.SysGroups;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.io.OutputStream;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 用户密码找回
 * @author Jackie Xu
 *
 */
@SuppressWarnings("serial")
public class Ac extends ApproveAction {
	EmailDao eDao = new EmailDao();
	UserDao userDao = new UserDao();
	AuthenticationDao auDao = new AuthenticationDao();
	VerifyUserInfoDao vudao = new VerifyUserInfoDao();

//	@Page(Viewer = "/cn/services/password_find.jsp")
	public void password_find() {
		CountryDao cDao = new CountryDao();
		Query q = cDao.getQuery().order("code");
		List<Country> country = q.asList();
		setAttr("country", country);
	}
//	@Page(Viewer = "/cn/services/mobile_loss.jsp")
	public void mobile_loss() {
		CountryDao cDao = new CountryDao();
		List<Country> country = cDao.find().asList();
		setAttr("country", country);
		
//		EnumSet types = EnumUtils.getAll(CurrencyType.class);
//		setAttr("currencyTypes", types);
	}

//	@Page(Viewer = "/cn/services/safepwd_find.jsp")
	public void safepwd_find() {
		if(!IsLogin()){
			toLogin();
			return;
		}
		initLoginUser();
		if (null != loginUser) {
			UserContact uc = loginUser.getUserContact();
			setAttr("showAudioButton", uc.isShowAudioButton());
			setAttr("codeType", PostCodeType.resetPassword.getKey());
			setAttr("googleAuth", uc.getGoogleAu());
			setAttr("payGoogleAuth", loginUser.isPayGoogleAuth());
			setAttr("mobileStatu", uc.getMobileStatu());
			setAttr("emailStatu", uc.getEmailStatu());
		}
		CountryDao cDao = new CountryDao();
		Query q = cDao.getQuery().order("code");
		List<Country> country = q.asList();
		setAttr("country", country);
	}

//	@Page(Viewer = "/cn/services/answer_find.jsp")
	public void answer_find() {
//		initLoginUser();
//		UserContact uc = loginUser.getUserContact();
//		setAttr("showAudioButton", uc.isShowAudioButton());
//		setAttr("codeType", PostCodeType.resetPassword.getKey());
	}

	@Page(Viewer = JSON)
	public void password_dofind() {
		try {
			if(isForbid()){
				return;
			}

			String loginName = param("userName");
			String loginEmail = param("email");
			String mobile = param("mobile");
			String method = param("method");

			User user = null;
			if ("email".equals(method)) {
				userDao.setLan(lan);
				user = userDao.getUserByColumn(loginEmail, "userContact.safeEmail");
				if (null == user) {
					user = userDao.getUserByColumn(loginEmail, "email");
				}
				int userId = userId();
				if(userId > 0) {
					user = userDao.get(userIdStr());
					if(!user.getEmail().equals(loginEmail)){
						json(L("您输入的邮箱与当前登录的邮箱不一致。"), false, "");
						return;
					}
				}
				
				//图片验证码验证
				String code = param("code");
				// 需要显示验证码
				if (code == null) {
					Cache.Delete("CodeImage_" + sessionId);
					json(L("验证码错误"), false, "");
					return;
				}
				Boolean codeRight = CheckCode(code);
				if (!codeRight) {
					// 更新一下当前这个状态，说明错了一次
					// lt.UpdateStatus(userName);
					// 这是后不能记录状态，如果仅仅是验证码的问题，不然就失去意义了，黑客还是可以轻松锁定大量用户，此时应该继续返回就好
					// 但是这里必须强制清除验证码，不然用户还是会反复测试就可以算出的验证码了
					Cache.Delete("CodeImage_" + sessionId);
					json(L("验证码错误"), false, "");
					return;
				}
				if (user != null) {
					if(isCanNoDo(user.getUserName())){
						json(L("请联系客服找回登录密码"), false, "");
						return;
					}
					String userEmail = user.getUserContact().getEmailStatu()==AuditStatus.pass.getKey()?user.getUserContact().getSafeEmail() : user.getEmail();
					if (userEmail.equalsIgnoreCase(loginEmail)) {
						SysGroups sg = SysGroups.vip;
						String title = L(SysGroups.vip.getValue()) + " " + L("找回登录密码");
						eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, eDao.findLoginPwd(user , this), loginEmail);

						json(L("邮件已发送"), true, "");
					} else {
						json(L("邮箱不正确"), false, "");
					}
				} else {
					json(L("邮箱未注册"), false, "");
				}
			} else {
//				if (userDao.mobileValidatedWithUserName(mobile, loginName)) {
//					json(L("用户名与手机不匹配"), false, "");
//					return;
//				}
//				Query<User> q = userDao.getQuery();
//				q.field("userContact.safeMobile").contains(mobile);
//				user = userDao.findOne(q);

				String countryCode = param("countryCode");
				if (null == countryCode || "".equals(countryCode) || !countryCode.startsWith("+")) {
					countryCode = "+86";
				}
				String mobileCode = param("mobileCode");

				String mobileNumber = mobile;
				if (!mobile.startsWith("+")) {
					mobileNumber = countryCode + " " + mobile;
				}
				user = userDao.getUserByColumn(mobileNumber, "userContact.safeMobile");
				if (null == user) {
					json(L("该手机未注册"), false, "");
					return;
				}

				if(isCanNoDo(user.getUserName())){
						json(L("请联系客服找回登录密码"), false, "");
					return;
				}

				ClientSession clientSession = new ClientSession(ip(), mobileNumber, lan, PostCodeType.resetPassword.getValue(), true);
				DataResponse dr = clientSession.checkCode(mobileCode);
				if(!dr.isSuc()){
					json(dr.getDes() , false , dr.getDataStr());
					return;
				}
				json(L("验证通过"), true, "{\"url\":\""+findLoginPwd(user , this)+"\"}");
			}
		} catch (Exception e) {
			log.error("内部异常", e);
		}//1bo!uk
	}

	private boolean isCanNoDo(String userName){
		if(userName==null) userName="";
			return "18022685449".equals(userName.trim());
	}

	@Page(Viewer = "")
	public void password_usenew(){
		try {
			String userId = param("userId");
			String miyao = param("code");

			User user = userDao.findOne(userDao.getQuery().filter("_id =", userId).filter("userContact.miyao", miyao));
			if (user != null) {

				Date newPwdTime = user.getUserContact().getNewPwdTime();

				Calendar objCal = Calendar.getInstance();
				objCal.add(Calendar.HOUR_OF_DAY, -2);
				Timestamp date = new Timestamp(objCal.getTimeInMillis());

				if (newPwdTime.getTime() < date.getTime()) {// 申请找回登录密码已经过期
					tip(Lan.Language(lan, "链接已失效！"), MAIN_DOMAIN, false);
					return;
				}

				Datastore ds = userDao.getDatastore();
				Query<User> q = ds.find(User.class, "_id", userId).filter("userContact.miyao", miyao);
				UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
				ops.set("pwd", user.getUserContact().getNewPwd());
				ops.set("userContact.newPwd", "");
				ops.set("userContact.miyao", "");
				ops.inc("modifyTimes", 1);
				ops.set("pwdModifyTime", now());

				userDao.update(q, ops);
				log.info("用户"+userId+"的新密码启用成功。");
				Cache.Set("use_pwd_"+userId, "1", 60);
				tip(L("您的新密码已启用成功，请使用邮箱中的新密码重新登录本站。为保证您的账户安全，请及时修改登录密码。"), MAIN_DOMAIN, false);
				return;
			}else{
				if(Cache.Get("use_pwd_"+userId) == null){
					log.info("激活链接已过了一分钟，链接失效");
					tip(Lan.Language(lan, "出错了，链接已失效！"), MAIN_DOMAIN, false);
				}else{
					log.info("一分种之内的请求");
					Cache.Delete("use_pwd_"+userId);
					tip(L("您的新密码已启用成功，请使用邮箱中的新密码重新登录本站。为保证您的账户安全，请及时修改登录密码。"), MAIN_DOMAIN, false);
				}
			}
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	@Page(Viewer = JSON)
	public void safepwd_dofind() {
		int userId = userId();
		if(userId <= 0){
			json("您还没有登录，请先登录。", false, "");
			return;
		}

		if(isForbid()) {
			return;
		}

		try {

//			String loginId = param("userName");
			String passWord = param("passWord");
			String loginEmail = param("email");
			String method = param("method");
			String mobile = param("mobile");
			int type = intParam("type");

			//下为短信验证
//			String code = param("code");
//			if(!isCorrect(code)){//json(L("验证码错误"), false, "");
//				return;
//			}
//			if(!hasEffective()){//json("验证码失效，请重新发送验证码。", false, "");
//				return;
//			}
			initLoginUser();
			User user = loginUser;

			if (user != null) {

				if (user.getSafePwd() == null || user.getSafePwd() == "") {
					json(L("资金安全密码没有设置"), false, "");
					return;
				}

				String e = user.getUserContact().getEmailStatu()==AuditStatus.pass.getKey()?user.getUserContact().getSafeEmail() : user.getEmail();
				//String userMobile = user.getUserContact().getMobileStatu()==AuditStatus.pass.getKey()?user.getUserContact().getSafeMobile() : "";
				if ("email".equals(method)) {

					//图片验证码验证
					String code = param("code");
					// 需要显示验证码
					if (code == null) {
						Cache.Delete("CodeImage_" + sessionId);
						json(L("验证码错误"), false, "");
						return;
					}
					Boolean codeRight = CheckCode(code);
					if (!codeRight) {
						// 更新一下当前这个状态，说明错了一次
						// lt.UpdateStatus(userName);
						// 这是后不能记录状态，如果仅仅是验证码的问题，不然就失去意义了，黑客还是可以轻松锁定大量用户，此时应该继续返回就好
						// 但是这里必须强制清除验证码，不然用户还是会反复测试就可以算出的验证码了
						Cache.Delete("CodeImage_" + sessionId);
						json(L("验证码错误"), false, "");
						return;
					}

					if (e == null || !e.equalsIgnoreCase(loginEmail)) {
						json(L("邮箱不正确"), false, "");
						return;
					}
				}else if("mobile".equals(method)){
//					if (userDao.mobileValidatedWithUserName(mobile, loginId)) {
//						json(L("您还未设置手机号码，请通过邮箱找回"), false, "");
//						return;
//					}
//					if (null==userMobile || "".equals(userMobile)) {
//						json(L("您还未设置手机号码，请通过邮箱找回"), false, "");
//						return;
//					}
					if (userDao.mobileValidatedWithUserName(mobile, user.getUserName())) {
						json(L("手机号码不正确"), false, "");
						return;
					}
				}

				passWord = user.getEncryptedPwd(passWord);// MD5.toMD5(user.getId() + passWord);
				if (!passWord.equals(user.getPwd())) {
					json(L("密码不正确"), false, "");
					return;
				}
				if ("email".equals(method)) {
					SysGroups sg = SysGroups.vip;
					String title = L(SysGroups.vip.getValue()) + " "+ L("找回资金安全密码");
					eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, eDao.findSafePwd(user , this), loginEmail);
					json(L("邮件已发送"), true, "");
				}else if("mobile".equals(method)){
//					if (!codeIsCorrect(JSON, PostCodeType.resetPassword.getKey())) {
//						return;
//					}
					if (userDao.mobileValidatedWithUserName(mobile, user.getUserName())) {
						json(L("手机号码不正确"), false, "");
						return;
					}
					String countryCode = param("countryCode");
					if (null == countryCode || "".equals(countryCode) || !countryCode.startsWith("+")) {
						countryCode = "+86";
					}
					String mobileCode = param("mobileCode");

					String mobileNumber = mobile;
					if (!mobile.startsWith("+")) {
						mobileNumber = countryCode + " " + mobile;
					}

					ClientSession clientSession = new ClientSession(ip(), mobileNumber, lan, PostCodeType.resetSafePassword.getValue(), true);
					DataResponse dr = clientSession.checkCode(mobileCode);
					if(!dr.isSuc()){
						json(dr.getDes() , false , dr.getDataStr());
						return;
					}
					json(L("验证通过"), true, "{\"url\":\""+this.findSafePwd(user , this)+"\"}");
				}

//				log.info("${vip_domain }/service/resetsafepwd?userId=${curUser.id }&code=${miYao }&needlogin=true");

			} else {
				json(L("用户名不存在"), false, "");
			}
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	//找回资金安全密码的处理方法
	public String findSafePwd(User user , Pages p){
		long nowTime = System.currentTimeMillis();
		Timestamp nowDate = new Timestamp(nowTime);
		String miYao = EncryptionPhoto.encrypt(Long.toString(nowTime));

		try {
			Datastore ds = userDao.getDatastore();
			Query<User> q = ds.find(User.class, "_id", user.getId());
			UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
			ops.set("userContact.safePwdMiYao", miYao);
			ops.set("userContact.lastMiYaoTime", nowDate);
			userDao.update(q, ops);
		} catch (Exception e) {
			log.error("内部异常", e);
		}

		log.info(VIP_DOMAIN+"/ac/safepwd_reset?userId="+user.getId()+"&code="+miYao+"&needlogin=true");
		return "/ac/safepwd_reset?userId="+user.getId()+"&code="+miYao+"&needlogin=true";
//		p.setAttr("curUser", user);
//		p.setAttr("miYao", miYao);
//		p.lan = user.getLanguage() != null ? user.getLanguage() : p.lan;//未设置语言的选择当前页码语言
//		p.setAttr("lan", p.lan);
//
//		return p.newJsp("/cn/templet/find_safe_pwd.jsp");
	}

	//找回登录密码的处理方法
	public String findLoginPwd(User user , Pages p){
		long nowTime = System.currentTimeMillis();
		Timestamp nowDate = new Timestamp(nowTime);
		String miYao = EncryptionPhoto.encrypt(Long.toString(nowTime));
		try {
			Datastore ds = userDao.getDatastore();
			Query<User> q = ds.find(User.class, "_id", user.getId());
			UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
			ops.set("userContact.miyao", miYao);
			ops.set("userContact.lastMiYaoTime", nowDate);
			userDao.update(q, ops);
		} catch (Exception e) {
			log.error("内部异常", e);
		}

		p.setAttr("curUser", user);
		p.setAttr("code", miYao);
		p.lan = user.getLanguage() != null ? user.getLanguage() : p.lan;//未设置语言的选择当前页码语言
		p.setAttr("lan", p.lan);
		String url = VIP_DOMAIN+"/ac/password_reset?userId="+user.getId()+"&code="+miYao;
		log.info(url);
		return url;
	}

//	@Page(Viewer = "/cn/services/safepwd_reset.jsp")
	public void safepwd_reset() {
		if(userId(true, true).equals("")){
			return;
		}

		String userId = param("userId");
		String loginUserId = userIdStr();

		if (!loginUserId.equals(userId)) {
			tip(L("出错了！"), MAIN_DOMAIN, false);
			return;
		}
		String code = param("code");

		User user = userDao.findOne(userDao.getQuery().filter("_id =", userId).filter("userContact.safePwdMiYao", code));
		if (user != null) {

			Date safeMiYaoTime = user.getUserContact().getLastMiYaoTime();

			Calendar objCal = Calendar.getInstance();
			objCal.add(Calendar.HOUR, -2);
			Timestamp date = new Timestamp(objCal.getTimeInMillis());

			if (safeMiYaoTime.getTime() < date.getTime()) {// 申请找回资金安全密码已经过期
				tip(Lan.Language(lan, "出错了！"), MAIN_DOMAIN, false);
				return;
			}
			setAttr("userId", userId);
			setAttr("code", code);
			setAttr("googleAu", user.getUserContact().getGoogleAu()==2?true:false);
			//setAttr("mobileStatu", user.getUserContact().getMobileStatu()==2?true:false);
		} else {
			tip(Lan.Language(lan, "出错了！"), MAIN_DOMAIN, false);
		}
	}
	
	/**
	 * 挂失手机
	 */
	@Page(Viewer = JSON)
	public void mobile_loss_report() {
		
//		String safePwd = param("safePwd");//M
//		String mobileNumber = param("mobileNumber");//M
//		String realName = param("realName");//M
//		String cardId = param("cardId");//M
//		String googleCode = param("googleCode");
//		String regYear = param("regYear");
//		String regMonth = param("regMonth");
//		String firstDepositCurrency = param("firstDepositCurrency");
//		String firstDepositAmount = param("firstDepositAmount");
//		String firstWithdrawCurrency = param("firstWithdrawCurrency");
//		String firstWithdrawAmount = param("firstWithdrawAmount");
//		String newMobile = param("newMobile");
		
		try {
			/*boolean isTest = false;
			isTest = true;
			if(isTest) {
				json(L("isTest"), true, "");
				return;
			}*/

			String countryCode = param("countryCode");
			if (null == countryCode || "".equals(countryCode) || !countryCode.startsWith("+")) {
				countryCode = "+86";
			}
			String oldCountryCode = param("oldCountryCode");
			if (null == oldCountryCode || "".equals(oldCountryCode) || !oldCountryCode.startsWith("+")) {
				oldCountryCode = "+86";
			}
			String newMobileNumber = param("newMobileNumber");
			String newPhoneNumber = countryCode + " " + newMobileNumber;
			String newMobileCode = param("newMobileCode");
			String safePwd = param("safePwd");//M
			//新加入的验证参数
			String oldPhoneNumber = param("mobileNumber");//M
			String realName = param("realName");//M
			String cardId = param("cardId");//M
			String regYearMonth = param("regYearMonth");
			
//			String[] 
			
			Integer firstDepositCurrency = intParam("firstDepositCurrency");
			String firstDepositAmount = param("firstDepositAmount");
			Integer firstWithdrawCurrency = intParam("firstWithdrawCurrency");
			String firstWithdrawAmount = param("firstWithdrawAmount");
			
			int regYear = 0;
			int regMonth = 0;
			if (StringUtils.isNotBlank(regYearMonth)) {
				String[] yearMonth = regYearMonth.split("[-]");
				if(NumberUtils.isNumber(yearMonth[0]))
					regYear = Integer.valueOf(yearMonth[0]);
				if(NumberUtils.isNumber(yearMonth[1]))
					regMonth = Integer.valueOf(yearMonth[1]);
			}
			
			oldPhoneNumber = oldCountryCode + " " + oldPhoneNumber;
			
			User loginUser = userDao.getUserByColumn(oldPhoneNumber, "userContact.safeMobile");
			if (null == loginUser) {
				json(L("原手机号码填写错误!"), false, "");
				return;
			}
			String userIdStr = loginUser.get_Id();
			UserContact uc = loginUser.getUserContact();
			String desOldPhoneNumber = uc.getSafeMobile();
			
			if (!desOldPhoneNumber.equals(oldPhoneNumber) ) {
				json(L("原手机号码填写错误!"), false, "");
				return;
			}
			String userIp = ip();
			if (!safePwd(safePwd, userIdStr, JSON, false)) {
				return;
			}
			
			Authentication authentication = auDao.getByUserId(userIdStr);
			if(null == authentication || authentication.getStatus() != AuditStatus.pass.getKey()){
				json(L("您还未实名认证,请先进行实名认证!"), false, "");
				return;
			} else {
				if (!authentication.getRealName().equals(realName) || !authentication.getCardId().equals(cardId) ) {
					json(L("身份信息错误!"), false, "");
					return;
				}
			}

			if (!CheckRegex.isPhoneNumber(newPhoneNumber)) {
				json(L("请输入有效的手机号码!"), false, "");
				return;
			}

			if (uc.getGoogleAu() == AuditStatus.pass.getKey()) {
				long googleCode = longParam("googleCode");
				String savedSecret = loginUser.getUserContact().getSecret();
				if (0==googleCode) {
					json(L("请输入Google验证码"), false, "");
					return;
				}

				if (!isCorrect(savedSecret, googleCode, JSON)) {
					return;
				}
			}
			// 检查短信验证码
			ClientSession clientSession2 = new ClientSession(userIp, newPhoneNumber, lan, "手机挂失", false);
			DataResponse dr2 = clientSession2.checkCode(newMobileCode);
			if (!dr2.isSuc()) {
				json(dr2.getDes(), false, "");
				return;
			}

			// 先较验
			VerifyUserInfo bean = vudao.getVerifyingInfo(loginUser.getId(), 3);
			if (bean != null && bean.getStatus() == 0) {
				json(L("您于%%已经提交了申请，我们会尽快为您审核。",bean.getAddTimeShowString()), false, "");
				return;
			}
			// 插入申请记录
			VerifyUserInfo info = new VerifyUserInfo(vudao.getDatastore());
			info.setUserId(loginUser.getId());
			info.setUserName(loginUser.getUserName());
			info.setType(3);
			info.setMcode(countryCode);
			info.setInfo(newPhoneNumber);
			info.setBeforeInfo(uc.getSafeMobile());
			info.setAddTime(TimeUtil.getNow().getTime());
			info.setIp(ip());
			info.setVerifyOldInfo(0);
			
			//new verification conditions
			info.setRegYear(regYear);
			info.setRegMonth(regMonth);
			info.setFirstDepositCurrency(firstDepositCurrency);
			info.setFirstDepositAmount(firstDepositAmount);
			info.setFirstWithdrawCurrency(firstWithdrawCurrency);
			info.setFirstWithdrawAmount(firstWithdrawAmount);

			vudao.add(info);
			json(L("您的修改手机申请提交成功，稍后客服人员会联系您进行视频认证，请耐心等待。"), true, "");
		} catch (Exception e) {
			log.error("内部异常", e);
			json(L("出错了，请稍后重试"), false, "");
		}
	}

	/**
	 * 找回资金安全密码时重新设置资金安全密码的方法
	 */
	@Page(Viewer = JSON)
	public void safepwd_doreset() {
		try {
			initLoginUser();

			String userId = param("userId");
			String loginUserId = userIdStr();

			if (!loginUserId.equals(userId)) {
				json(L("资金安全密码修改失败"), false, "");
				return;
			}

			String code = param("code");
			String password = param("safePwd");
			int safeLevel = intParam("safeLevel");

			User user = userDao.findOne(userDao.getQuery().filter("_id =", userId).filter("userContact.safePwdMiYao", code));
			if (user != null) {

				Date safeMiYaoTime = user.getUserContact().getLastMiYaoTime();

				if (user.getUserContact().getGoogleAu() == 2) {
					long googleCode = longParam("googleCode");
					String savedSecret = user.getUserContact().getSecret();
					if ("".equals(googleCode)) {
						json(L("请输入Google验证码"), false, "");
						return;
					}

					if (!isCorrect(savedSecret, googleCode, JSON)) {
						return;
					}
				}

				Calendar objCal = Calendar.getInstance();
				objCal.add(Calendar.HOUR, -1);
				Timestamp date = new Timestamp(objCal.getTimeInMillis());

				if (safeMiYaoTime.getTime() < date.getTime()) {// 申请找回资金安全密码已经过期
					json(L("修改资金安全密码的请求已失效"), false, "");
					return;
				}

				//modify by xwz 20171220 资金密码不能和登录密码一致
				if(user.getEncryptedPwd(password).equals(user.getPwd())){
					json(L("资金密码应避免与帐户登录密码一致。"), false, "");
					return;
				}
				userDao.updateSecurityPwd(userId, password, safeLevel);

				Datastore ds = userDao.getDatastore();
				Query<User> q = ds.find(User.class, "_id", userId).filter("userContact.safePwdMiYao", code);
				UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
				ops.set("userContact.safePwdMiYao", "");

				UpdateResults<User> ur = userDao.update(q, ops);
				if (!ur.getHadError()){
					try {
						MobileDao mDao = new MobileDao();
						PostCodeType pType = PostCodeType.resetSafePwd;
						/*start by xzhang 20171031 短信服务临时解决方法，除+86外全发英文*/
						String title = L(pType.getValue());
						String content = L(pType.getDes());
                        //去掉该逻辑，所有都按照用户选择语言发送 modify by buxianguan 20190805
//						if(!MsgUtil.isContain(user.getUserContact().getSafeMobile())){
//							title = Lan.Language("en", pType.getValue());
//							content = Lan.Language("en", pType.getDes());
//						}
						/*end*/
						mDao.sendSms(user, ip(), title, content, user.getUserContact().getSafeMobile());
					} catch (Exception e) {
						log.error("内部异常", e);
					}
					json(L("资金安全密码修改成功"), true, "");
				}else
					json(L("资金安全密码修改失败"), false, "");
			}else{
				json(L("资金安全密码修改失败"), false, "");
			}

		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

//	@Page(Viewer = "/cn/services/password_reset.jsp")
	public void password_reset() {

		String userId = param("userId");

		String code = param("code");

		User user = userDao.findOne(userDao.getQuery().filter("_id =", userId).filter("userContact.miyao", code));
		if (user != null) {

			Date miYaoTime = user.getUserContact().getLastMiYaoTime();

			Calendar objCal = Calendar.getInstance();
			objCal.add(Calendar.HOUR, -2);
			Timestamp date = new Timestamp(objCal.getTimeInMillis());

			if (miYaoTime.getTime() < date.getTime()) {// 申请找回资金安全密码已经过期
				tip(Lan.Language(lan, "出错了！"), MAIN_DOMAIN, false);
				return;
			}
			setAttr("userId", userId);
			setAttr("code", code);
		} else {
			tip(Lan.Language(lan, "出错了！"), MAIN_DOMAIN, false);
		}
	}

	/**
	 * 找回密码时重新设置密码的方法
	 */
	@Page(Viewer = JSON)
	public void password_doreset() {
		try {
//			initLoginUser();
			String token = param("token");
//			String userId = param("userId");
			String password = param("pwd");
			int pwdLevel = intParam("pwdLevel");
			String email = param("email");
			User user = userDao.findOne("email", email);
			/*Start by guankaili 20190403 优化查询逻辑 */
//		User user = userDao.findOne(userDao.getQuery().filter("_id =", userId));
//			User user = userDao.findOne("_id", userId);
			/*End*/

			if (user != null) {
				String userId = user.getId();
				RsaUser rsaUser = RsaLoginUtil.getRsaUser(this);
				byte[] decodedData = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(password.replace(" ", "+")),rsaUser.getPriKey());
				password = new String(decodedData);
				Date miYaoTime = user.getUserContact().getLastMiYaoTime();

				Calendar objCal = Calendar.getInstance();
				objCal.add(Calendar.HOUR, -1);
				Timestamp date = new Timestamp(objCal.getTimeInMillis());
				//modify by xwz 20171220 资金密码不能和登录密码一致
				if(user.getEncryptedPwd(password).equals(user.getSafePwd())){
				    textViewErr("password", L("账户登录密码应避免与资金密码一致。"));
					return;
				}
				if(!com.world.util.string.EncryptionPhoto.checkToken(Const.function_forget_password,  user.getUserName(), token)){
					toast("越权请求");
					return;
				}
				userDao.updatePwd(userId, password, pwdLevel);

				Datastore ds = userDao.getDatastore();
				Query<User> q = ds.find(User.class, "_id", userId);
				UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
				ops.set("userContact.newPwd", "");
				ops.inc("modifyTimes", 1);
				ops.set("pwdModifyTime", now());


				UpdateResults<User> ur = userDao.update(q, ops);
				if (!ur.getHadError()) {
					// 修改后锁定24小时
					Cache.Delete(Const.function_forget_password+user.getUserName());
					toast(L("设置成功"),true);
				} else {
                    toast(L("设置失败"));
                }
			}else{
                toast(L("设置失败"));
			}

		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	@Page(Viewer = "")
	public void qrcode(){
		try{
			response.setContentType( "image/png" );
			response.setHeader( "Pragma", "No-cache" );
			response.setHeader( "Cache-Control", "no-cache" );
			response.setDateHeader( "Expires", 0 );
			
			OutputStream os = response.getOutputStream();
			
			int width = intParam("width");
			int height = intParam("height");
			if(param("code").length() == 0){
				return;
			}
			//参与生成二维码的文本
			String codec = URLDecoder.decode(param("code"), "utf-8");
			//生成二维码图片
			QRCodeGenerator.encode(os, codec, width, height);
			
			os.flush();
			os.close();
			
		}catch(Exception ex){
			log.error("内部异常", ex);
		}
	}
	
	private static boolean isReq = false;
	/**
	 * 邮箱认证的最后一步
	 */
//	@Page(Viewer = "/cn/manage/base/emailAuth.jsp")
	public void email() {
		initLoginUser();
		if(isReq){
			return;
		}
		isReq = true;

		String edit = param("edit");
		String step = param("step");
		String emailCode = param("emailCode");
		String userId = param("userId");

		loginUser = new UserDao().getUserById(userId);

		UserContact uc = loginUser.getUserContact();

		String email = null;
		if (step.equals("third")) {// 认证
			if (emailCode != null && emailCode.length() > 0) {
				if (uc.getEmailCode() == null || !uc.getEmailCode().equals(emailCode)) {
					if(Cache.Get("auth_email_"+userId) == null){
						log.info("激活链接已过了一分钟，链接失效");
//						tip(Lan.Language(lan, "出错了，链接已激活！"), VIP_DOMAIN+"/manage/auth/email?edit=true&step=next", false);
						//2017.8.14 xzhang 修改认证后跳转页面，跳转至账户页面。
						setAttr("tips", Lan.Language(lan, "出错了，链接已激活！"));
						setAttr("type", "1");
						setAttr("nextPage", "/manage/auth/email");
						setAttr("removeCookie", false);
						setAttr("close", false);
						setAttr("isSuc",true);
						forward("/cn/user/emailConfirm.jsp");
					}else{
						Cache.Delete("auth_email_"+userId);
//						tip(L("邮箱认证成功"), "/u/approve/email", false);
						//2017.8.14 xzhang 修改认证后跳转页面，跳转至账户页面。
						setAttr("tips", L("邮箱认证成功"));
						setAttr("isSuc",true);
						setAttr("type", "1");
						setAttr("nextPage", "/manage/auth/email");
						setAttr("removeCookie", false);
						setAttr("close", false);
						forward("/cn/user/emailConfirm.jsp");
					}
					isReq = false;
					return;
				} else {
					if (now().getTime() - uc.getEmailTime().getTime() > 24 * 60 * 60 * 1000) {// 过期了
//						tip(Lan.Language(lan, "出错了，链接已失效！"), VIP_DOMAIN+"/manage/auth/email?edit=true&step=next", false);
						//2017.8.14 xzhang 修改认证后跳转页面，跳转至账户页面。
						setAttr("tips", Lan.Language(lan, "出错了，链接已失效！"));
						setAttr("type", "1");
						setAttr("nextPage", "/manage/auth/email");
						setAttr("removeCookie", false);
						setAttr("close", false);
						setAttr("isSuc",true);
						forward("/cn/user/emailConfirm.jsp");
						isReq = false;
						return;
					}
				}
				email = uc.getCheckEmail();
			}

			if (email != null && email.length() > 0) {
				edit = "";
				Datastore ds = userDao.getDatastore();
				Query<User> q = ds.find(User.class, "_id", String.valueOf(userId));
				UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

				ops.set("userContact.safeEmail", email);
				ops.set("email", email);
				ops.set("userContact.emailCode", "");
				ops.set("userContact.checkEmail", "");
				ops.set("userContact.emailStatu", AuditStatus.pass.getKey());
				ops.set("userContact.emailTime", new Timestamp(0));

				userDao.update(q, ops);
				Cache.Delete("editE_" + userId());
				if (uc.getEmailStatu() != 2) {
					RewardRecordDao rrDao = new RewardRecordDao();
					// //执行奖励
					RewardRecord rr = new RewardRecord(rrDao.getDatastore());
					rr.setUserId(loginUser.getId());
					rr.setUserName(loginUser.getUserName());
					rr.setType(RewardSource.EmailCertification.getKey());
					rr.setDate(now());
					rr.setIp(ip());
					if (rrDao.reward(rr)) {// 奖励成功
											// "+RewardSource.EmailCertification.getBtc()/fee.btcFee+"
					// tip(Lan.LanguageFormat(lan, "邮箱认证成功，系统奖励您%%比特币",new
					// String[]{WebUtil.saveFourShow((float)RewardSource.EmailCertification.getBtc()/fee.btcFee)}),
					// "/u/pay/BtcDetails", false);
//						tip(L("邮箱认证成功"), "/manage/auth/email", false);
						//2017.8.14 xzhang 修改认证后跳转页面，跳转至账户页面。
						setAttr("tips", L("邮箱认证成功"));
						setAttr("nextPage", "/manage/auth/email");
						setAttr("removeCookie", false);
						setAttr("type", "1");
						setAttr("close", false);
						setAttr("isSuc",true);
						forward("/cn/user/emailConfirm.jsp");
						MsgDao.sendMsg(loginUser.getId(),loginUser.getUserName(), TipType.bandEmailSuc);
						Cache.Set("auth_email_"+userId, "1", 60);
						isReq = false;
						/*start by xwz 20170625 绑定邮箱加积分*/
						JifenManage jifenManager = new JifenManage(userId, 3, null, null, "VIP");//3:注册
						SingletonThreadPool.addJiFenThread(jifenManager);
						/*end*/
						return;
					}
				}
			}
		} else if (step.equals("next")) {

		}
		Cache.Set("editE_" + userId(), "2", 10 * 60);

		if (email == null)
			email = uc.getSafeEmail();
		int emailStatu = email != null ? 2 : 0;

		if (edit.equals("true")) {
			emailStatu = -1;
			setAttr("step", step);
		}

		setAttr("emailStatu", emailStatu);

		if (emailStatu == -1 && uc.getCheckEmail() != null && uc.getCheckEmail().length() > 0)
			email = uc.getCheckEmail();
		if (email == null)
			email = loginUser.getEmail();

		setAttr("email", userDao.shortEmail(email));
		setAttr("source", email);

		setAttr("tab", "email");
		isReq = false;
	}
	
//	@Page(Viewer = "/cn/manage/account/download/authAddress.jsp")
	public void authAddress() {
		try {
			String auth = param("auth");
			String type = param("type");
			if (null != Cache.Get(auth)) {
				int id = Integer.parseInt(Cache.Get(auth));
				
				String table = type+CointTable.receiveaddr;
				String sql = "update "+table+" set auth=1 where id=? and auth=0";
				if (Data.Update(sql, new Object[]{id}) > 0) {
					setAttr("isSuc", true);
					Cache.Delete(auth);
					return;
				}
			}
		} catch (Exception e) {
			log.error("内部异常", e);
		}
		setAttr("isSuc", false);
	}
}

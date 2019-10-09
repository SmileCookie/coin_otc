package com.world.controller;

import com.Lan;
import com.alibaba.fastjson.JSONObject;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.redis.RedisUtil;
import com.kafka.ProducerSend;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.constant.Const;
import com.world.data.mysql.Data;
import com.world.model.dao.extend.InvitationDao;
import com.world.model.dao.extend.ParttimeInviteDao;
import com.world.model.dao.extend.WalletCooperateDao;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.msg.MsgDao;
import com.world.model.dao.user.CountryDao;
import com.world.model.dao.user.EmailDao;
import com.world.model.dao.user.MobileDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.UserLoginIpDao;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.msg.TipType;
import com.world.model.entity.user.Country;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.model.enums.LoginStatus;
import com.world.model.jifenmanage.JifenManage;
import com.world.model.service.RegisterService;
import com.world.model.singleton.SingletonSingleThreadPool;
import com.world.rabbitmq.producer.InitPayUserWalletProducer;
import com.world.rabbitmq.producer.UserLoginLogProducer;
import com.world.util.CommonUtil;
import com.world.util.MsgToastKey;
import com.world.util.UserUtil;
import com.world.util.cookie.CookieUtil;
import com.world.util.date.TimeUtil;
import com.world.util.string.MD5;
import com.world.web.Page;
import com.world.web.action.ApproveAction;
import com.world.web.response.DataResponse;
import com.world.web.sso.SSOLoginManager;
import com.world.web.sso.session.ClientSession;
import com.world.web.sso.session.Session;
import com.world.web.sso.session.SsoSessionManager;
import com.yc.entity.SysGroups;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class Register extends ApproveAction {
	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger(Register.class.getName());

	private UserDao userDao = new UserDao();
	UserLoginIpDao uld = new UserLoginIpDao();
	RegisterService registerService = new RegisterService();

//	@Page(Viewer = "/cn/user/register.jsp")
	public void index() {
		setAttr("status", IsLogin());
		String tuid = GetCookie("tuijianid");
		if (tuid != null && tuid.length() > 0) {
			User u = userDao.getById(tuid);
			if (u != null)
				setAttr("recommId", u.getId());
		}
		String recommId = param(0);
		if (StringUtils.isNotBlank(recommId)) {
			AddCookie("tuijianid", recommId, 24 * 3600);
			setAttr("recommId", recommId);
		}

		CountryDao cDao = new CountryDao();
		Query q = cDao.getQuery().order("seq");
		List<Country> country = q.asList();
		setAttr("country", country);
	}

	/**
	 * 用户于注册和找回密码时发送验证码
	 */
	@Page(Viewer = JSON)
	public void sendCode(){
		String phoneNumberParamName = "phonenumber";
		String datas = "{\"id\" : \""+phoneNumberParamName+"\"}";
		String mobileCode;
		try {
			String countryCode = param("countryCode");
			if (null == countryCode || "".equals(countryCode) || !countryCode.startsWith("+")) {
				countryCode = "+86";
			}
			String phonenumber = param(phoneNumberParamName).trim();
			String code = param("code").trim();//图形验证码
			boolean graphicalCode = false;//是否有图形验证码
//			if(!countryCode.equals("+86") || code.length() == 4){//非中国账号
				graphicalCode = true;
				if(!CheckCode(code)){
					json(L("图形验证码错误，请重新输入。"), false, "{\"id\" : \"code\"}");
					return;
				}
//			}
			if(isForbid()){
				return;
			}

			int icodeType = intParam("codeType");
			PostCodeType postCodeType = (PostCodeType) EnumUtils.getEnumByKey(icodeType, PostCodeType.class);
			String codeType = postCodeType.getValue();


			if (session == null) {
				SsoSessionManager.initSession(this);
			}

			if(sessionId == null){
				json(L("系统出错了，请稍后重试"), false, datas);
				return;
			}

			String mobileNumber = countryCode + " " + phonenumber;

			if(!CheckRegex.isPhoneNumber(mobileNumber)){
				json(L("请输入合法的手机号！"), false, datas);
				return;
			}

			String ip = ip();

			ClientSession clientSession = new ClientSession(ip, mobileNumber, lan, codeType, graphicalCode);
			clientSession.rs = resoureRequest;

			DataResponse dr = clientSession.checkSend();//检测当前客户端是否能够发送

			if(!dr.isSuc()){
				json(dr.getDes() , false , dr.getDataStr());
				return;
			}//测试，暂时不验证这步

			boolean res = userDao.mobileLoginCheckMobile(mobileNumber);
			//当前ip验证是否注册过的所有手机号码24h不得超过x个
			clientSession.addCheckNumber();
			if (icodeType == PostCodeType.register.getKey()) {
				if (!res) {
					json(L("手机已注册，请使用未注册的手机！"), false, datas);
					return;
				}
			}

			MobileDao mDao = new MobileDao();
			mobileCode = MobileDao.GetRadomStr(1);

			int userId = userId();
			User user = null;
			if(userId > 0) {
				user = userDao.get(userIdStr());
				//2017.8.10 xzhang 修改找回登陆密码报错问题！
				if(!mobileNumber.equals(user.getUserContact().getSafeMobile())){
					json(L("您输入的手机号码与当前登录号码不一致。"), false, datas);
					return;
				}
			}
			/*start by xzhang 20171031 短信服务临时解决方法，除+86外全发英文*/
			String title = L(codeType);
			String content = String.format(L(postCodeType.getDes()), mobileCode);
            //去掉该逻辑，所有都按照用户选择语言发送 modify by buxianguan 20190805
//			if(!MsgUtil.isContain(mobileNumber)){
//				title = Lan.Language("en", codeType);
//				content = String.format(Lan.Language("en", postCodeType.getDes()), mobileCode);
//			}
			/*end*/
			if(mDao.sendSms(user == null ? new User() : user, ip(),
					title, content, mobileNumber)){
				if(clientSession.sendCode(mobileCode)){
					log.info(mobileCode+">>>"+String.format(L(postCodeType.getDes()), mobileCode));
					json(L("短信验证码已发送到您的手机，10分钟内有效"), true, datas);
					return;
				}
			}
		} catch (Exception e) {
			log.error("内部异常", e);
		}
		json(L("系统出错，请稍后..."), false, datas);
	}

	@Page(Viewer = JSON)
	public void postEmail(){
		try {
			if(isForbid()){
				return;
			}

			String email = param("email");
			String code = param("code");
			String tuijianId = param("recommId");

			// 验证码
			if (!CheckCode(code)) {
				json(L("你输入的验证码错误，请重新输入"), false, "");
				return;
			}

			boolean res = userDao.emailValidated(email);
			if (!res) {
				json(L("邮箱已注册！"), false, "");
				return;
			}

			String language = lan;//param("language");

//			String tuijianId = GetCookie("tuijianid");
			String recommenders = "";
			String recommId = "";
			if (tuijianId != null) {// 根据cookie设置推荐
				User userLs = userDao.getUserByColumn(tuijianId, "subDomain");
				if (userLs != null) {
					if (!userLs.isDeleted()) {
						recommenders = userLs.getUserName();
						recommId = userLs.getId();
					} else {
						json(L("你所用的推荐人是非法账户已被网站封号！"), false, "");
						return;
					}
				}
			}

			String emailCode = MD5.toMD5(System.currentTimeMillis()+email);

			User old = (User) Cache.GetObj("reging_user_"+email);
			if(old != null){
				userDao.delById(old.getId());
				Cache.Delete("reging_user_"+email);
			}

			User u = new User(userDao.getDatastore());
			u.setEmail(email);
			u.setRecommendName(recommenders);
			u.setRecommendId(recommId);
			u.setLoginIp(ip());
			u.setDeleted(false);

			UserContact uc = new UserContact();
			uc.setEmailCode(emailCode);
			uc.setCheckEmail(email);
			uc.setEmailTime(now());
			u.setUserContact(uc);

			String nid = userDao.addUser(u);

			if (nid != null) {
				EmailDao eDao = new EmailDao();

				String info = eDao.getRegEmailHtml(nid, language, email, emailCode,this);

				SysGroups sg = SysGroups.vip;
//				String title = L(SysGroups.vip.getValue()) + " " + L("邮箱注册");
				String title = L("注册激活");
				eDao.sendEmail(ip(), nid, email, title, info, email);
				log.info(email + "注册:" + VIP_DOMAIN+"/register?emailCode="+emailCode+"&nid="+nid);
				json(nid, true, "");
			}else{
				json(L("邮件发送失败"), false, "");
			}

		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

//	@Page(Viewer = "/cn/user/register.jsp", Cache = 300)
	public void postMobileCode(){
		try {
			if(isForbid()){
				return;
			}

			String phonenumber = param("phonenumber");
			String userName = param("phonenumber");
			if (!phonenumber.startsWith("+")) {
				phonenumber = "+86 " + phonenumber;
			}
			String vcode = param("code");
			String tuijianId = param("recommId");
			String codeType = "注册";
			ClientSession clientSession = new ClientSession(ip(), phonenumber, lan, codeType, false);

			DataResponse dr = clientSession.checkCode(vcode);

			if(!dr.isSuc()){
				json(dr.getDes() , false , dr.getDataStr());
				return;
			}

			boolean res = userDao.mobileLoginCheckMobile(phonenumber);
			if (!res) {
				json(L("手机已注册！"), false, "");
				return;
			}

			String language = lan;//param("language");

//			String tuijianId = GetCookie("tuijianid");
			String recommenders = "";
			String recommId = "";
			if (tuijianId != null) {// 根据cookie设置推荐
				User userLs = userDao.getUserByColumn(tuijianId, "subDomain");
				if (userLs != null) {
					if (!userLs.isDeleted()) {
						recommenders = userLs.getUserName();
						recommId = userLs.getId();
					} else {
						json(L("你所用的推荐人是非法账户已被网站封号！"), false, "");
						return;
					}
				}
			}

			String mobileCode = MD5.toMD5(System.currentTimeMillis()+phonenumber.replace(" ", ""));

			User old = (User) Cache.GetObj("reging_user_"+phonenumber.replace(" ", ""));
			if(old != null){
				userDao.delById(old.getId());
				Cache.Delete("reging_user_"+phonenumber.replace(" ", ""));
			}

			User u = new User(userDao.getDatastore());
			u.setUserName(userName);
			u.setRecommendName(recommenders);
			u.setRecommendId(recommId);
			u.setLoginIp(ip());
			u.setDeleted(false);

			UserContact uc = new UserContact();
			uc.setMobileCode(mobileCode);
			uc.setSafeMobile(phonenumber);
			u.setUserContact(uc);

			String nid = userDao.addUser(u);

			if (nid != null) {

				setAttr("mobileSuc", nid);
				setAttr("mobile", userDao.shortMobile(u.getUserContact().getSafeMobile()));

				setAttr("status", IsLogin());
			}

		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	@Page(Viewer = JSON)
	public void repostE(){
		if(isForbid()){
			return;
		}

		String nid = param("nid");
		User user = userDao.findOne(userDao.getQuery().filter("_id", nid).field("userName").equal(null));
		if(user != null && user.getUserContact().isCanReg() && user.getUserContact().isCanSend()){

			String email = user.getEmail();
			String emailCode = MD5.toMD5(System.currentTimeMillis()+email);

			UpdateResults<User> ur = userDao.updateEmailCode(nid, null, emailCode);
			if (!ur.getHadError()) {
				EmailDao eDao = new EmailDao();

				String info = eDao.getRegEmailHtml(nid, user.getLanguage(), email, emailCode,this);
				SysGroups sg = SysGroups.vip;
//				String title = L(SysGroups.vip.getValue()) + " " + L("邮箱注册");
                String title = L("注册激活");
				eDao.sendEmail(ip(), nid, email, title, info, email);

				json(L("邮件已发送"), true, "");
				return;
			}
		}
		json(L("邮件发送失败"), false, "");
	}

//	@Page(Viewer = "/cn/user/regFrame.jsp")
	public void emailRemind(){
		String email = param("email");
		setAttr("email", UserUtil.shortEmail(email));
		setAttr("nid", param("nid"));
	}

//	@Page(Viewer = JSON)
//	public void mobileReg() {
//		try {
//			String countryCode = param("countryCode");
//			if (null == countryCode || "".equals(countryCode) || !countryCode.startsWith("+")) {
//				countryCode = "+86";
//			}
//			String userName = param("phonenumber");
//			String mobileNumber = param("phonenumber");
//			String loginCheckMobile = param("phonenumber");//手机号（不带国家码）
//			String mobileCode = param("mobileCode");
//			String password = param("password");
//			int pwdLevel = intParam("pwdLevel");
//			String tuijianId = param("tuijianId");
//
//			boolean res = userDao.mobileLoginCheckMobile(mobileNumber);
//			if (!res) {
//				json(L("手机已注册，请重新填写。"), false, "");
//				return;
//			}
//
//			if (password.length() < 8) {
//					json(L("密码格式错误，请输入8-20位字符（字母、数字、符号），区分大小写"), false, "");
//				return;
//			}
//
//			if (!mobileNumber.startsWith("+")) {
//				mobileNumber = countryCode + " " + mobileNumber;
//			}
//
//			if (!UserUtil.checkNick(userName)) {
//				json(L("手机已注册，请重新填写。"), false, "");
//				return;
//			}
//			String userIp = ip();
//			if (!userIp.equals("127.0.0.1")) {
//				Query<User> q = userDao.getQuery(User.class).filter("loginIp =", userIp);
//				// //
//				long count = userDao.find(q).countAll();
//				log.error("ip：" + userIp + ",次数：" + count + ",你所在的ip跟踪程序已启动，如有恶意请停止，如果报警有误，请联系网站在线客服...");
//				if (count >= 3) {
//					json(L("您好，系统监测到您的IP可能存在大量刷注册推荐人奖励行为，因此本次注册不能被通过。如果您是真实用户，请通过邮箱support@bitglobal.com联系我们的客服，给您造成的不便敬请谅解！您当前IP：") + userIp, false, "");
//					return;
//				}
//			}
//
//
//			// 检查短信验证码
//			ClientSession clientSession = new ClientSession(userIp, mobileNumber, lan, PostCodeType.register.getValue(), false);
//			DataResponse dr = clientSession.checkCode(mobileCode);
//			if(!dr.isSuc()){
//				json(dr.getDes(), false, "");
//				return;
//			}
//
//			String recommenders = "";
//			String recommId = "";
//			User userLs = null;
//			if (tuijianId != null) {
//				userLs = userDao.get(tuijianId);
//				if (userLs != null) {
//					if (!userLs.isDeleted()) {
//						if (userLs.isLockRecommend()){
//							json(L("你所用的推荐人已被网站锁定！"), false, "");
//							return;
//						}
//						recommenders = userLs.getUserName();
//						recommId = userLs.getId();
//					} else {
//						json(L("你所用的推荐人是非法账户已被网站封号！"), false, "");
//						return;
//					}
//				}
//			}
//
//			//FIXME suxinjie 20170714 为了提高速度,事先获取ID,直接插入,省略后面的修改操作
//			final User user = new User(userDao.getDatastore());
//			String myId = user.incId();
//
//			user.setMyId(myId);
//			user.setLoginIp(ip());
//			user.setDeleted(false);
//			user.setUserName(userName);
//			user.setPwd(UserUtil.newSafeSecretMethod(myId, password));
//			user.setPwdLevel(pwdLevel);
//			user.setModifyTimes(0);
//			user.setSafePwd("");
//			user.setSafeLevel(0);
//			user.setRecommendId(recommId);
//			user.setRecommendName(recommenders);
//			user.setLastLoginTime(TimeUtil.getNow());
//			user.setLanguage(lan);
//			UserContact uc = new UserContact();
//			uc.setMobileCode(mobileCode);
//			uc.setmCode(countryCode);
//			uc.setSafeMobile(mobileNumber);
//			uc.setLoginCheckMobile(loginCheckMobile);
//			uc.setMobileStatu(AuditStatus.pass.getKey());
//			user.setUserContact(uc);
//			user.setActivationTime(now());
//			/*add by xwz 20170930 增加来来源统计*/
//			CookieUtil cookieUtil = new CookieUtil(request,response);
//			String utmSource = cookieUtil.getCookieValue("utmSource");//来源
//			String utmMedium = cookieUtil.getCookieValue("utmMedium");//介质
//			if(StringUtils.isNotBlank(utmSource)) {
//				user.setUtmSource(utmSource);
//			}
//			if(StringUtils.isNotBlank(utmMedium)) {
//				user.setUtmMedium(utmMedium);
//			}
//			/*end*/
//
//			if ((null == user.getUserName() || "".equals(user.getUserName())) && null != user.getUserContact().getSafeMobile()) {
//				user.setUserName(user.getUserContact().getSafeMobile().replace(" ", "").replace("+86", ""));
//			}
//
//			String nid = userDao.addUser(user);
//			Data.Update("insert into userinfo (id,userName,registerTime,uType) values (?,?,?,?)", new Object[]{nid, userName, TimeUtil.getDateStr(now(),"yyyy-MM-dd HH:mm:ss"),"01"});
//			if (null != nid) {
//				// 设置密码
////					UpdateResults<User> ur = userDao.updatePwd(nid, password, pwdLevel);
//
//				Cache.Delete("md5CurrentCodeImage_" + sessionId);
//@Page(Viewer = JSON)
//	public void mobileReg() {
//		try {
//			String countryCode = param("countryCode");
//			if (null == countryCode || "".equals(countryCode) || !countryCode.startsWith("+")) {
//				countryCode = "+86";
//			}
//			String userName = param("phonenumber");
//			String mobileNumber = param("phonenumber");
//			String loginCheckMobile = param("phonenumber");//手机号（不带国家码）
//			String mobileCode = param("mobileCode");
//			String password = param("password");
//			int pwdLevel = intParam("pwdLevel");
//			String tuijianId = param("tuijianId");
//
//			boolean res = userDao.mobileLoginCheckMobile(mobileNumber);
//			if (!res) {
//				json(L("手机已注册，请重新填写。"), false, "");
//				return;
//			}
//
//			if (password.length() < 8) {
//					json(L("密码格式错误，请输入8-20位字符（字母、数字、符号），区分大小写"), false, "");
//				return;
//			}
//
//			if (!mobileNumber.startsWith("+")) {
//				mobileNumber = countryCode + " " + mobileNumber;
//			}
//
//			if (!UserUtil.checkNick(userName)) {
//				json(L("手机已注册，请重新填写。"), false, "");
//				return;
//			}
//			String userIp = ip();
//			if (!userIp.equals("127.0.0.1")) {
//				Query<User> q = userDao.getQuery(User.class).filter("loginIp =", userIp);
//				// //
//				long count = userDao.find(q).countAll();
//				log.error("ip：" + userIp + ",次数：" + count + ",你所在的ip跟踪程序已启动，如有恶意请停止，如果报警有误，请联系网站在线客服...");
//				if (count >= 3) {
//					json(L("您好，系统监测到您的IP可能存在大量刷注册推荐人奖励行为，因此本次注册不能被通过。如果您是真实用户，请通过邮箱support@bitglobal.com联系我们的客服，给您造成的不便敬请谅解！您当前IP：") + userIp, false, "");
//					return;
//				}
//			}
//
//
//			// 检查短信验证码
//			ClientSession clientSession = new ClientSession(userIp, mobileNumber, lan, PostCodeType.register.getValue(), false);
//			DataResponse dr = clientSession.checkCode(mobileCode);
//			if(!dr.isSuc()){
//				json(dr.getDes(), false, "");
//				return;
//			}
//
//			String recommenders = "";
//			String recommId = "";
//			User userLs = null;
//			if (tuijianId != null) {
//				userLs = userDao.get(tuijianId);
//				if (userLs != null) {
//					if (!userLs.isDeleted()) {
//						if (userLs.isLockRecommend()){
//							json(L("你所用的推荐人已被网站锁定！"), false, "");
//							return;
//						}
//						recommenders = userLs.getUserName();
//						recommId = userLs.getId();
//					} else {
//						json(L("你所用的推荐人是非法账户已被网站封号！"), false, "");
//						return;
//					}
//				}
//			}
//
//			//FIXME suxinjie 20170714 为了提高速度,事先获取ID,直接插入,省略后面的修改操作
//			final User user = new User(userDao.getDatastore());
//			String myId = user.incId();
//
//			user.setMyId(myId);
//			user.setLoginIp(ip());
//			user.setDeleted(false);
//			user.setUserName(userName);
//			user.setPwd(UserUtil.newSafeSecretMethod(myId, password));
//			user.setPwdLevel(pwdLevel);
//			user.setModifyTimes(0);
//			user.setSafePwd("");
//			user.setSafeLevel(0);
//			user.setRecommendId(recommId);
//			user.setRecommendName(recommenders);
//			user.setLastLoginTime(TimeUtil.getNow());
//			user.setLanguage(lan);
//			UserContact uc = new UserContact();
//			uc.setMobileCode(mobileCode);
//			uc.setmCode(countryCode);
//			uc.setSafeMobile(mobileNumber);
//			uc.setLoginCheckMobile(loginCheckMobile);
//			uc.setMobileStatu(AuditStatus.pass.getKey());
//			user.setUserContact(uc);
//			user.setActivationTime(now());
//			/*add by xwz 20170930 增加来来源统计*/
//			CookieUtil cookieUtil = new CookieUtil(request,response);
//			String utmSource = cookieUtil.getCookieValue("utmSource");//来源
//			String utmMedium = cookieUtil.getCookieValue("utmMedium");//介质
//			if(StringUtils.isNotBlank(utmSource)) {
//				user.setUtmSource(utmSource);
//			}
//			if(StringUtils.isNotBlank(utmMedium)) {
//				user.setUtmMedium(utmMedium);
//			}
//			/*end*/
//
//			if ((null == user.getUserName() || "".equals(user.getUserName())) && null != user.getUserContact().getSafeMobile()) {
//				user.setUserName(user.getUserContact().getSafeMobile().replace(" ", "").replace("+86", ""));
//			}
//
//			String nid = userDao.addUser(user);
//			Data.Update("insert into userinfo (id,userName,registerTime,uType) values (?,?,?,?)", new Object[]{nid, userName, TimeUtil.getDateStr(now(),"yyyy-MM-dd HH:mm:ss"),"01"});
//			if (null != nid) {
//				// 设置密码
////					UpdateResults<User> ur = userDao.updatePwd(nid, password, pwdLevel);
//
//				Cache.Delete("md5CurrentCodeImage_" + sessionId);
//
//				try {
//					new Login().toLogin("1", 6 * 3600, user, userIp, false, userName, this);
//				} catch (Exception e) {
//					log.error("内部异常", e);
//				}
//
//				// TODO: 2017/7/15 add by suxinjie 优化点,可以线程或者队列处理
//				uld.add(user.getRealName(), user.getId(), user.getUserName(), ip(), 1, null);// 保存登录IP
//				/*start by xwz 20170712*/
//				JifenManage jifenManager = new JifenManage(user.getId(), 1, null, null, "VIP");//1：注册
//				SingletonThreadPool.addJiFenThread(jifenManager);
//
//				jifenManager = new JifenManage(user.getId(), 4, null, null, "VIP");//4：绑定手机
//				SingletonThreadPool.addJiFenThread(jifenManager);
//				/*end*/
//
//				json(L("注册成功！"), true, "");
//				Cache.Set("user_lan_" + user.getId(), lan);
//				MsgDao.sendMsg(nid, userName, TipType.registerSuc);
//			} else {
//				json(L("注册失败，请稍后重试。"), false, "");
//			}
//		} catch (Exception e) {
//			log.error("内部异常", e);
//			json(L("注册出错，请稍后重试。"), false, "");
//		}
//	}
//				try {
//					new Login().toLogin("1", 6 * 3600, user, userIp, false, userName, this);
//				} catch (Exception e) {
//					log.error("内部异常", e);
//				}
//
//				// TODO: 2017/7/15 add by suxinjie 优化点,可以线程或者队列处理
//				uld.add(user.getRealName(), user.getId(), user.getUserName(), ip(), 1, null);// 保存登录IP
//				/*start by xwz 20170712*/
//				JifenManage jifenManager = new JifenManage(user.getId(), 1, null, null, "VIP");//1：注册
//				SingletonThreadPool.addJiFenThread(jifenManager);
//
//				jifenManager = new JifenManage(user.getId(), 4, null, null, "VIP");//4：绑定手机
//				SingletonThreadPool.addJiFenThread(jifenManager);
//				/*end*/
//
//				json(L("注册成功！"), true, "");
//				Cache.Set("user_lan_" + user.getId(), lan);
//				MsgDao.sendMsg(nid, userName, TipType.registerSuc);
//			} else {
//				json(L("注册失败，请稍后重试。"), false, "");
//			}
//		} catch (Exception e) {
//			log.error("内部异常", e);
//			json(L("注册出错，请稍后重试。"), false, "");
//		}
//	}

	/**
	 * 注册
	 * */
	@Page(Viewer = JSON)
	public void emailReg() {
		try {

			String email = param("email");
			String password = param("password");
			//int pwdLevel = intParam("pwdLevel");
			String tuijianId = param("tuijianId");
			String code = param("code");
			//邮箱不能为空
			if(StringUtils.isEmpty(email)){
				json(L("请输入正确的电子邮件地址。"), false, "");
			}
			//邮箱格式
			if (!CheckRegex.isEmail(email)) {
				json(L("请输入正确的电子邮件地址。"), false, "");
				return;
			}
			/*Start by guankaili 20181225 优化注册逻辑 */
			String msg = null;
			//邮箱重复性验证
//			boolean res = userDao.emailValidated(email);
//			String msg = null;
//			if (!res) {
//				msg = CommonUtil.mapToJsonStr(msg,"email",L("请输入正确的电子邮件地址。"));
//				/*json(L("请输入正确的电子邮件地址。"), false, "\"email\"");
//				return;*/
//			}
			email = email.toLowerCase();
			User user = userDao.findOne("email", email);
			if(null != user){
				msg = CommonUtil.mapToJsonStr(msg,"email",L("请输入正确的电子邮件地址。"));
			}
			/*End*/

            //密码验证
            String regex = "^(?![A-Za-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{8,20}$" ;    //密码的组成至少要包括大小写字母、数字及标点符号的其中两项
            if(!password.matches(regex)){
                json(L("您的密码需要8-20位，包含字母，数字，符号的两种以上。"), false, "");
                return;
            }

			// 验证码CheckCode，删除缓存放在最后，防止前端重复请求check接口
			if (!CheckCodeOnly(code)) {
                Cache.Delete("CodeImage_" + sessionId);
				msg = CommonUtil.mapToJsonStr(msg,"code",L("图形验证码错误，请重新输入。"));
				/*json(L("你输入的验证码错误，请重新输入"), false, "\"code\"");
				return;*/
			}
			if(null != msg){
				json("", false, msg);
				return;
			}

			/*Start by guankaili 20190701 取消同一个IP无法注册多个账号问题(http://jira.oswaldlink.f3322.net:10080/browse/XJYPT-3164) */
			String userIp = ip();
//			if (!userIp.equals("127.0.0.1")) {
//				Query<User> q = userDao.getQuery(User.class).filter("loginIp =", userIp);
//
//				long count = userDao.find(q).countAll();
//				log.info("ip：" + userIp + ",次数：" + count + ",你所在的ip跟踪程序已启动，如有恶意请停止，如果报警有误，请联系网站在线客服...");
//				if (count >= 3) {
//					json(L("您好，系统监测到您的IP可能存在大量刷注册推荐人奖励行为，因此本次注册不能被通过。如果您是真实用户，请通过邮箱support@btcwinex.com联系我们的客服，给您造成的不便敬请谅解！您当前IP：") + userIp, false, "");
//					return;
//				}
//			}
			/*End  */
			String recommenders = "";
			String recommId = "";
			User userLs = null;
			if (tuijianId != null) {
				userLs = userDao.get(tuijianId);
				if (userLs != null) {
					if (!userLs.isDeleted()) {
						if (userLs.isLockRecommend()){
							json(L("你所用的推荐人已被网站锁定！"), false, "");
							return;
						}
						recommenders = userLs.getUserName();
						recommId = userLs.getId();
					} else {
						json(L("你所用的推荐人是非法账户已被网站封号！"), false, "");
						return;
					}
				}
			}
			/*Start by guankaili 20181225 优化注册逻辑 */
//			Query<User> q = userDao.getQuery();
//			User user = userDao.findOne(q.field("email").endsWithIgnoreCase(email));

			/*End*/
			if(null != user && !user.getUserContact().isCanReg()){
				userDao.delById(user.getId());
			}
			String emailCode = MD5.toMD5(System.currentTimeMillis()+email);

			user = new User(userDao.getDatastore());
			String myId = user.incId();

			user.setMyId(myId);
			user.setLanguage(lan);
			user.setLoginIp(ip());
			user.setDeleted(false);
			user.setPwd(UserUtil.newSafeSecretMethod(myId, password));
			user.setPwdLevel(0);
			user.setSafePwd("");
			//user.setSafeLevel(pwdLevel);
			user.setRecommendId(recommId);
			user.setRecommendName(recommenders);
			user.setLastLoginTime(TimeUtil.getNow());
			user.setEmail(email.toLowerCase());
			user.setPayEmailAuth(true);
			user.setPayGoogleAuth(true);
			user.setUserQualification(0);
			/*Start by guankaili 20181120 前端要求传引导页标识 */
			//引导标识
//			Cache.SetObj(Const.guide_flg+myId,1);
			user.setGuideFlg(true);
			/*end*/

			UserContact uc = new UserContact();
			uc.setEmailCode(emailCode);
			uc.setCheckEmail(email);
			uc.setEmailTime(now());
			uc.setCardStatu(4);
			user.setUserContact(uc);
			user.setUserName(email);
//			if ((null == user.getUserName() || "".equals(user.getUserName())) && null != user.getUserContact().getSafeMobile()) {
//				user.setUserName(user.getUserContact().getSafeMobile().replace(" ", "").replace("+86", ""));
//			}

			String nid = userDao.addUser(user);
			if (nid != null) {
				/*start by xwz 20170625 注册绑定邮箱加积分*/

				/*end*/
				// 设置密码gti
//				userDao.updatePwd(nid, password, pwdLevel);

				Cache.Set("user_lan_" + user.getId(), lan);
//				doForgotLoginCookie();
				EmailDao eDao = new EmailDao();

				String info = eDao.getRegEmailHtml(nid, lan, email, emailCode,this);
				SysGroups sg = SysGroups.vip;
//				String title = L(SysGroups.vip.getValue()) + " " + L("邮箱注册");
                String title = L("注册激活");
				log.info(email + "注册:" + VIP_DOMAIN+"/register/emailConfirm?emailCode="+emailCode);
				eDao.sendEmail(ip(), nid, email, title, info, email);
				json(nid, true, "");
				saveStore(nid,0);
			}else{
				json(L("邮件发送失败"), false, "\"email\"");
			}

            Cache.Delete("CodeImage_" + sessionId);
		} catch (Exception e) {
			log.error("10100103VIPZC【注册】 com.world.controller.Register#emailReg",e);
			json(L("注册出错，请稍后重试。"), false, "");
		}
	}


	public void saveStore(String userId,Integer status){
		String key = Const.USER__QUALIFICATION + userId;
		RedisUtil.hset(key,"qualification",status.toString(),60*60);
	}




	@Page(Viewer = JSON)
	public void reSendEmail() {
		try {
			String nid = param("nid");
			User user = userDao.getById(nid);
			if (null == user) {
				json(L("出错了"), false, "");
				return;
			}
			EmailDao eDao = new EmailDao();
			String email = user.getUserContact().getCheckEmail();
			if (null == email || "".equals(email)) {
				json(L("您已成功激活，可直接登录"), false, "");
				return;
			}
			String sendTimeStr = StringUtils.isNotEmpty(Cache.Get("reSendEmail_"+email)) ? Cache.Get("reSendEmail_"+email) : "0";
			Long sendTime = Long.valueOf(sendTimeStr);
			Long currentTime = System.currentTimeMillis();
			//验证码发送超过1分钟之后才重新发送
			if((currentTime - sendTime) < resendtimes){
				long second = 60-((currentTime-sendTime)/1000);
				if(second > 1){
					json(Lan.LanguageFormat(lan , "重复提交，请等待%%秒后再次尝试s" , second+""), false, "");
				}else{
					json(Lan.LanguageFormat(lan , "重复提交，请等待%%秒后再次尝试" , second+""), false, "");
				}
				return;
			}
			String emailCode = MD5.toMD5(System.currentTimeMillis()+email);
			userDao.updateEmailCode(nid, email, emailCode);
			String info = eDao.getRegEmailHtml(nid, lan, email, emailCode, this);
			SysGroups sg = SysGroups.vip;
//			String title = L(SysGroups.vip.getValue()) + " " + L("邮箱注册");
            String title = L("注册激活");
			int iResult = eDao.sendEmail(ip(), nid, email,title, info, email);
			if (iResult == 1) {
				Long time = System.currentTimeMillis();
				Cache.Set("reSendEmail_"+email,time.toString(),60);
			}
			log.info(email + "注册:" + VIP_DOMAIN+"/register/emailConfirm?emailCode="+emailCode);
			json(nid, true, "");
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	@Page(Viewer = JSON)
	public void reSendActEmail() {
		try {
//			String nid = param("nid");
//			User user = userDao.getById(nid);
			String emailParam = param("email");
			User user = userDao.findOne("email", emailParam);
			if (null == user) {
				json(L("出错了"), false, "");
				return;
			}
			String nid = user.getId();
			EmailDao eDao = new EmailDao();
			String email = user.getUserContact().getCheckEmail();
			if (null == email || "".equals(email)) {
				json(L("您已成功激活，可直接登录"), false, "");
				return;
			}
			String sendTimeStr = StringUtils.isNotEmpty(Cache.Get("reSendActEmail_"+email)) ? Cache.Get("reSendActEmail_"+email) : "0";
			Long sendTime = Long.valueOf(sendTimeStr);
			Long currentTime = System.currentTimeMillis();
			//验证码发送超过1分钟之后才重新发送
			if((currentTime - sendTime) < resendtimes){
				long second = 60-((currentTime-sendTime)/1000);
				if(second > 1){
					json(Lan.LanguageFormat(lan , "重复提交，请等待%%秒后再次尝试s" , second+""), false, "");
				}else{
					json(Lan.LanguageFormat(lan , "重复提交，请等待%%秒后再次尝试" , second+""), false, "");
				}
				return;
			}
			String emailCode = MD5.toMD5(System.currentTimeMillis()+email);
			userDao.updateEmailCode(nid, email, emailCode);
			String info = eDao.getForgetRegEmailHtml(nid, lan, email, emailCode, this);
			String title = L("忘记密码激活");
			int iResult = eDao.sendEmail(ip(), nid, email,title, info, email);
			if (iResult == 1) {
				Long time = System.currentTimeMillis();
				Cache.Set("reSendActEmail_"+email,time.toString(),60);
			}
			//设置cookie里值为正常登陆
			Session.resetOrAddCookie(GlobalConfig.session + "loginStatus", String.valueOf(LoginStatus.UNLOGIN.getKey()), -1, false, false, this);
			log.info(email + "注册:" + VIP_DOMAIN+"/register/ ?emailCode="+emailCode);
			json(nid, true, "");
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

//	@Page(Viewer = "/cn/user/emailTips.jsp")
	public void emailTips() {
		String nid = param("nid");
		int type = intParam("type");// 提示类型 1：注册邮件  2：重发注册邮件
		User user = userDao.get(nid);
		setAttr("email", UserUtil.shortEmail(user.getEmail()));
		setAttr("nid", nid);
		setAttr("type", type);
	}

	/*
	* 注册激活
	* */
	@Page(Viewer = "/cn/user/emailConfirm.jsp")
	public void emailConfirm() {
		String emailCode = param("emailCode");
		/*Start by guankaili 20181225 优化注册激活逻辑 */
//		User user = userDao.getUserByColumn(emailCode, "userContact.emailCode");
		User user = userDao.findOne("userContact.emailCode",emailCode);
		/*End*/
		boolean isSuc = false;
		String msg = "";
		if (null == user || null == user.getUserContact().getCheckEmail() || "".equals(user.getUserContact().getCheckEmail())) {
			Cookie userCollect = new Cookie("userCollectMarket", "");
			userCollect.setMaxAge(60 * 60 * 2);// s为单位，1个月60*60*24,存储一天
			userCollect.setDomain(Session.SETDOMAIN);
			userCollect.setPath("/");
			response.addCookie(userCollect);
			SSOLoginManager.logout(this, false);
			json(L("您已成功激活，可直接登录"), false, "");
			setAttr("isSuc", false);
			setAttr("type", "0");
			setAttr("msg", L("您已成功激活，可直接登录"));
			return;
		}
		if (user == null || !user.getUserContact().isCanReg()) {
			Cookie userCollect = new Cookie("userCollectMarket", "");
			userCollect.setMaxAge(60 * 60 * 2);// s为单位，1个月60*60*24,存储一天
			userCollect.setDomain(Session.SETDOMAIN);
			userCollect.setPath("/");
			response.addCookie(userCollect);
			SSOLoginManager.logout(this, false);
			setAttr("type", "1");
			isSuc = false;
			msg = "注册链接已过期，请重新注册";
		} else {
			Datastore ds = userDao.getDatastore();
			Query<User> q = ds.find(User.class, "_id", user.getId());
			UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
			if (null != user.getEmail()) {
				ops.set("userContact.safeEmail", user.getEmail());
				ops.set("userContact.emailStatu", AuditStatus.pass.getKey());
				ops.set("userContact.emailTime", new Timestamp(0));
			}
//			ops.set("userName", user.getEmail());
			ops.set("userContact.emailCode", "");
			ops.set("userContact.mobileCode", "");
			ops.set("userContact.checkEmail", "");
			/*start by chendi  添加激活时间*/
			ops.set("activationTime",new Date());
			/*start by chendi  添加资质已激活*/
			ops.set("userQualification",1);
			/*start by xwz 20170930 增加来源*/
			CookieUtil cookieUtil = new CookieUtil(request,response);
			String utmSource = cookieUtil.getCookieValue("utmSource");//来源
			String utmMedium = cookieUtil.getCookieValue("utmMedium");//介质
			if(StringUtils.isNotBlank(utmSource)) {
				ops.set("utmSource", utmSource);
			}
			if(StringUtils.isNotBlank(utmMedium)) {
				ops.set("utmMedium", utmMedium);
			}
			/*end*/
			UpdateResults<User> ur = userDao.update(q, ops);
			user.setUserName(user.getEmail());
			Data.Update("insert into userinfo (id,userName,registerTime,uType) values (?,?,?,?)", new Object[]{user.getId(), user.getEmail(), TimeUtil.getDateStr(user.getRegisterTime(),"yyyy-MM-dd HH:mm:ss"),"01"});
			if (!ur.getHadError()) {
				//注册积分d
				try {
					new Login().toLogin("1", 6 * 3600, user, ip(), false, user.getEmail(), this);

					/*Start by guankaili 20181229 添加消息队列 */
					// 保存登录IP
//					uld.add(user.getUserName(), user.getId(), user.getUserName(), ip(), 1, null);
					UserLoginLogProducer.send(user.getUserName(), user.getId(), user.getUserName(), ip(), 1, "");
					/*End*/
				} catch (Exception e) {
					log.error("10100103VIPJH【注册激活】 com.world.controller.Register#emailConfirm",e);
				}
//				msg = "邮箱验证成功！";
				msg = "恭喜您，注册成功!";
				isSuc = true;
				/*start by xwz 20170625 注册绑定邮箱加积分*/
//				JifenManage jifenManager = new JifenManage(user.getId(), 1, null, null,"VIP");//1:注册
//				SingletonThreadPool.addJiFenThread(jifenManager);
				JifenManage jifenManager = new JifenManage(user.getId(), 1, null, null,"VIP");//1:注册
				SingletonSingleThreadPool.addJiFenThread(jifenManager);
				jifenManager = new JifenManage(user.getId(), 2, null, null,"VIP");//2:登陆
				SingletonSingleThreadPool.addJiFenThread(jifenManager);
				InitPayUserWalletProducer.send(user.getId());
				/*end*/
				saveStore(user.getId(),1);
                //设置cookie里值为正常登陆
                Session.resetOrAddCookie(GlobalConfig.session + "loginStatus", String.valueOf(LoginStatus.HAS_LOGIN.getKey()), -1, false, false, this);
				/*Start by guankaili 20190516 用户激活动作埋点 */
				JSONObject jsonObject = new JSONObject();
//				jsonObject.put("userid",user.getId());
				jsonObject.put("registertime",user.getRegisterTime());
				ProducerSend producerSend = new ProducerSend();
				producerSend.sendMessage("activation", jsonObject.toString());
				log.info("推送驾驶舱用户激活动作埋点成功："+jsonObject.toString());
				/*end*/

			} else {
				msg = "验证失败，请稍后重试";
			}
		}
//		setAttr("type", "2");
		setAttr("isSuc", isSuc);
		setAttr("msg", L(msg));
	}

	/*
	 * 注册激活
	 * */
	@Page(Viewer = "/cn/user/emailConfirmFg.jsp")
	public void emailConfirmFg() {
		String emailCode = param("emailCode");
		/*Start by guankaili 20181225 优化注册激活逻辑 */
		User user = userDao.findOne("userContact.emailCode",emailCode);
		/*End*/
		boolean isSuc = false;
		String msg = "";
		if (null == user || null == user.getUserContact().getCheckEmail() || "".equals(user.getUserContact().getCheckEmail())) {
			Cookie userCollect = new Cookie("userCollectMarket", "");
			userCollect.setMaxAge(60 * 60 * 2);// s为单位，1个月60*60*24,存储一天
			userCollect.setDomain(Session.SETDOMAIN);
			userCollect.setPath("/");
			response.addCookie(userCollect);
			SSOLoginManager.logout(this, false);
			json(L("您已成功激活，可直接登录"), false, "");
			setAttr("isSuc", false);
			setAttr("type", "0");
			setAttr("msg", L("您已成功激活，可直接登录"));
			return;
		}
		if (user == null || !user.getUserContact().isCanReg()) {
			Cookie userCollect = new Cookie("userCollectMarket", "");
			userCollect.setMaxAge(60 * 60 * 2);// s为单位，1个月60*60*24,存储一天
			userCollect.setDomain(Session.SETDOMAIN);
			userCollect.setPath("/");
			response.addCookie(userCollect);
			SSOLoginManager.logout(this, false);
			setAttr("type", "1");
			isSuc = false;
			msg = "注册链接已过期，请重新注册";
		} else {
			Datastore ds = userDao.getDatastore();
			Query<User> q = ds.find(User.class, "_id", user.getId());
			UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
			if (null != user.getEmail()) {
				ops.set("userContact.safeEmail", user.getEmail());
				ops.set("userContact.emailStatu", AuditStatus.pass.getKey());
				ops.set("userContact.emailTime", new Timestamp(0));
			}
//			ops.set("userName", user.getEmail());
			ops.set("userContact.emailCode", "");
			ops.set("userContact.mobileCode", "");
			ops.set("userContact.checkEmail", "");
			/*start by chendi  添加激活时间*/
			ops.set("activationTime",new Date());
			/*start by chendi  添加资质已激活*/
			ops.set("userQualification",1);
			/*start by xwz 20170930 增加来源*/
			CookieUtil cookieUtil = new CookieUtil(request,response);
			String utmSource = cookieUtil.getCookieValue("utmSource");//来源
			String utmMedium = cookieUtil.getCookieValue("utmMedium");//介质
			if(StringUtils.isNotBlank(utmSource)) {
				ops.set("utmSource", utmSource);
			}
			if(StringUtils.isNotBlank(utmMedium)) {
				ops.set("utmMedium", utmMedium);
			}
			/*end*/
			UpdateResults<User> ur = userDao.update(q, ops);
			user.setUserName(user.getEmail());
			Data.Update("insert into userinfo (id,userName,registerTime,uType) values (?,?,?,?)", new Object[]{user.getId(), user.getEmail(), TimeUtil.getDateStr(user.getRegisterTime(),"yyyy-MM-dd HH:mm:ss"),"01"});
			if (!ur.getHadError()) {
				//注册积分d
				/*try {
					new Login().toLogin("1", 6 * 3600, user, ip(), false, user.getEmail(), this);

					*//*Start by guankaili 20181229 添加消息队列 *//*
					// 保存登录IP
//					uld.add(user.getUserName(), user.getId(), user.getUserName(), ip(), 1, null);
					UserLoginLogProducer.send(user.getUserName(), user.getId(), user.getUserName(), ip(), 1, "");
					*//*End*//*
				} catch (Exception e) {
					log.error("10100103VIPJH【注册激活】 com.world.controller.Register#emailConfirmFg",e);
				}*/
//				msg = "邮箱验证成功！";
				msg = "恭喜您，注册成功!";
				isSuc = true;
				/*start by xwz 20170625 注册绑定邮箱加积分*/
//				JifenManage jifenManager = new JifenManage(user.getId(), 1, null, null,"VIP");//1:注册
//				SingletonThreadPool.addJiFenThread(jifenManager);
				JifenManage jifenManager = new JifenManage(user.getId(), 1, null, null,"VIP");//1:注册
				SingletonSingleThreadPool.addJiFenThread(jifenManager);
				jifenManager = new JifenManage(user.getId(), 2, null, null,"VIP");//2:登陆
				SingletonSingleThreadPool.addJiFenThread(jifenManager);
				InitPayUserWalletProducer.send(user.getId());
				/*end*/
				saveStore(user.getId(),1);
				//设置cookie里值为正常登陆
				Session.resetOrAddCookie(GlobalConfig.session + "loginStatus", String.valueOf(LoginStatus.UNLOGIN.getKey()), -1, false, false, this);
				/*Start by guankaili 20190516 用户激活动作埋点 */
				JSONObject jsonObject = new JSONObject();
//				jsonObject.put("userid",user.getId());
				jsonObject.put("registertime",user.getRegisterTime());
				ProducerSend producerSend = new ProducerSend();
				producerSend.sendMessage("activation", jsonObject.toString());
				log.info("推送驾驶舱用户激活动作埋点成功："+jsonObject.toString());
				/*end*/

			} else {
				msg = "验证失败，请稍后重试";
			}
		}
//		setAttr("type", "2");
		setAttr("isSuc", isSuc);
		setAttr("msg", L(msg));
	}

	@Page(Viewer = ".xml")
	public void doRegister() {
		String emailSuc = param("emailSuc");
		String mobileSuc = param("mobileSuc");
		UserDao userDao = new UserDao();
		User user = null;
		if (null != mobileSuc && !"".equals(mobileSuc)) {
			user = userDao.get(mobileSuc);
			if(user == null){
				Write(L("注册超时，请重新注册"), false, L("注册超时，请重新注册"));
				return;
			}
		} else {
			user = userDao.get(emailSuc);
			if(user == null || !user.getUserContact().isCanReg()){
				Write(L("注册链接已失效，请重新注册"), false, L("注册链接已失效，请重新注册"));
				return;
			}
			String referer = request.getHeader("Referer");

			if (referer == null || !referer.contains("/register")) {
				return;
			}
		}

		String userIp = ip();

		try {

			String pwd = param("pwd");
			String nick = param("nick");
			int pwdLevel = intParam("pwdLevel");

			String safePwd = param("safePwd");
			int safeLevel = intParam("safeLevel");

			nick = nick.toLowerCase().replaceAll(" ", "");

			if (!UserUtil.checkNick(nick)) {
				Write(L("用户名不能含有特殊字符。"), false, "");
				return;
			}

			if (!userIp.equals("127.0.0.1")) {
				Query<User> q = userDao.getQuery(User.class).filter("loginIp =", userIp);
				// //
				long count = userDao.find(q).countAll();
				log.error("ip：" + userIp + ",次数：" + count + ",你所在的ip跟踪程序已启动，如有恶意请停止，如果报警有误，请联系网站在线客服...");
				if (count >= 20) {
					Write(L("您好，系统监测到您的IP可能存在大量刷注册推荐人奖励行为，因此本次注册不能被通过。如果您是真实用户，请通过邮箱support@btcwinex.com联系我们的客服，给您造成的不便敬请谅解！您当前IP：") + userIp, false, "");
					return;
				}
			}
			
			int result = userDao.nameValidated(nick);
			if (result == 1) {
				Write(L("用户名有关键字。"), false, nick);
				return;
			} else if (result == 2) {
				Write(L("用户名重复"), false, nick);
				return;
			} else if (result < 0) {
				Write("Nickname validation fails, please try again.", false, nick);
				return;
			}
			
			user.setUserName(nick);
			user.setPwd(pwd);
			user.setPwdLevel(pwdLevel);
			user.setSafePwd(safePwd);
			user.setSafeLevel(safeLevel);
			UpdateResults<User> ur = userDao.updateUser(user);
			if (!ur.getHadError()) {
				String nid = user.getId();
				
					Cache.Delete("md5CurrentCodeImage_" + sessionId);
					// 原来的方式有点问题
					new Login().toLogin("1", 6 * 3600, user, userIp, false, nick, this);
					
					Write(L("注册成功！"), true, "");
					MsgDao.sendMsg(nid, nick, TipType.registerSuc);
				} else {
					Write(L("注册失败，请重新注册"), false, "");
				}
				return;
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}

		Write(L("注册失败，请重新注册"), false, "");
	}
	
	@Page(Viewer = "/cn/user/regSucess.jsp")
	public void regSucess() {
		
	}


	@Page(Viewer = JSON)
	public void checkImgCode(){
		String code = param("code");
		Boolean flag = false;
		if(CheckCodeOnly(code)){
			flag = true;
		}else{
			json(L("图形验证码错误，请重新输入。"),false,"\"code\"");
			return;
		}
		json("",true,"");
	}


	/**
	 * 邀请页
	 */
	@Page(Viewer = JSON)
	public void invitation() {
		//邀请类型：媒体-0，名人-1
		String type = param("type");
		//媒体名称
		String name = param("name");
		//联系人
		String userName = param("userName");
		//国家码
		String code = param("code");
		//电话
		String mobile = param("mobile");
		//微信
		String wechat = param("wechat");
		//合作类型
		String cooperateType = param("cooperateType");
		//平台链接
		String platformLine = param("platformLine");
		if (StringUtils.isEmpty(code)) {
			code = "+86";
		}
		String mobileNum = code + " " + mobile;
		//提示信息
		String msg = "";
		if ("1".equals(type)) {
			if (StringUtils.isEmpty(name)) {
				msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请输入正确的姓名"));
			}
			if (StringUtils.isEmpty(platformLine)) {
				msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请输入正确的平台链接"));
			}
		} else {
			if (StringUtils.isEmpty(name)) {
				msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请输入正确的媒体名称"));
			}
			if (StringUtils.isEmpty(userName)) {
				msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请输入正确的联系人"));
			}
		}
		if (StringUtils.isEmpty(mobile)) {
			msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请输入正确的手机号码。"));
		}
		if (!CheckRegex.isPhoneNumber(mobileNum)) {
			msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请输入正确的手机号码。"));
		}
		if (StringUtils.isEmpty(wechat)) {
			msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请输入正确的微信号"));
		}

		if (StringUtils.isEmpty(cooperateType) || cooperateType.split(",").length < 1) {
			msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请选择合作方式"));
		}
		if (StringUtils.isNotEmpty(msg)) {
			json("", false, msg, true);
			return;
		}
		try {
			InvitationDao invitationDao = new InvitationDao();
			invitationDao.insertInvitation(name, type,0, mobileNum, userName, wechat, platformLine, cooperateType);
			json("录入成功", true, "", true);
		} catch (Exception ex) {
			log.error("内部异常", ex);
			json("录入失败", false, "", true);
		}

	}

	/**
	 * 钱包合作
	 */
	@Page(Viewer = JSON)
	public void walletCooperate() {
		//钱包名
		String walletName = param("walletName");
		//官网链接
		String websitesLink = param("websitesLink");
		//联系人
		String userName = param("userName");
		//电话
		String wechat = param("wechat");
		//合作类型
		String cooperateType = param("cooperateType");
		//提示信息
		String msg = "";
		if (StringUtils.isEmpty(walletName)) {
			msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请输入正确的钱包名称"));
		}
		if (StringUtils.isEmpty(websitesLink)) {
			msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请输入正确的官网链接"));
		}
		if (StringUtils.isEmpty(userName)) {
			msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请输入正确的联系人"));
		}
		if (StringUtils.isEmpty(wechat)) {
			msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请输入正确的微信号"));
		}
		if (StringUtils.isEmpty(cooperateType) || cooperateType.split(",").length < 1) {
			msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请选择合作方式"));
		}
		if (StringUtils.isNotEmpty(msg)) {
			json("", false, msg);
			return;
		}
		try {
			WalletCooperateDao walletCooperateDao = new WalletCooperateDao();
			walletCooperateDao.insertWalletCooperate(walletName, websitesLink, userName, wechat, cooperateType,0);
			json("录入成功", true, "");
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	/**
	 * 兼职邀请
	 */
	@Page(Viewer = JSON)
	public void partTimeJob() {
		//姓名
		String name = param("name");
		//国家码
		String code = param("code");
		//电话
		String mobile = param("mobile");
		//微信
		String wechat = param("wechat");
		//钱包地址
		String walletAddress = param("walletAddress");
		//申请职位
//        String applyPost = param("applyPost");
		if (StringUtils.isEmpty(code)) {
			code = "+86";
		}
		String mobileNum = code + " " + mobile;
		//提示信息
		String msg = "";
		if (StringUtils.isEmpty(name)) {
			msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请输入正确的姓名"));
		}
		if (StringUtils.isEmpty(mobile)) {
			msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请输入正确的手机号码。"));
		}
		if (!CheckRegex.isPhoneNumber(mobileNum)) {
			msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请输入正确的手机号码。"));
		}
		if (StringUtils.isEmpty(wechat)) {
			msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请输入正确的微信号"));
		}
		if (StringUtils.isEmpty(walletAddress)) {
			msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请输入正确的钱包地址"));
		}
//        if (StringUtils.isEmpty(applyPost) || applyPost.split(",").length < 1) {
//            msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, L("请选择合作方式"));
//        }
		if (StringUtils.isNotEmpty(msg)) {
			json("", false, msg);
			return;
		}
		try {
			ParttimeInviteDao parttimeInviteDao = new ParttimeInviteDao();
			parttimeInviteDao.insertParttimeInvite(name, mobileNum, wechat, walletAddress, "",0);
			json("录入成功", true, "");
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

}
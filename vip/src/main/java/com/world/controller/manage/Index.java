package com.world.controller.manage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.api.config.ApiConfig;
import com.file.config.FileConfig;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.kafka.ProducerSend;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.PayUserApiService;
import com.messi.user.util.ConstantCenter;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.constant.Const;
import com.world.controller.api.util.SystemCode;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.model.LimitType;
import com.world.model.dao.lucky.LuckyQualifyDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.UserLoginIpDao;
import com.world.model.dao.user.VerifyUserInfoDao;
import com.world.model.dao.user.authen.AuthenLogDao;
import com.world.model.dao.user.authen.AuthenticationDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.Market;
import com.world.model.entity.bill.BillDistribution;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.user.CollectMarket;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.model.entity.user.UserLoginIp;
import com.world.model.entity.user.VerifyUserInfo;
import com.world.model.entity.user.authen.Authentication;
import com.world.model.enums.LogCategory;
import com.world.model.enums.LoginStatus;
import com.world.model.loan.dao.InvestorApplyDao;
import com.world.model.loan.dao.P2pUserDao;
import com.world.model.loan.entity.MyInvestorApply;
import com.world.model.loan.entity.P2pUser;
import com.world.rabbitmq.producer.OperateLogInfoProducer;
import com.world.util.CommonUtil;
import com.world.util.Message;
import com.world.util.MsgToastKey;
import com.world.util.UserUtil;
import com.world.util.date.TimeUtil;
import com.world.util.language.SafeTipsTag;
import com.world.util.qrcode.QRCodeGenerator;
import com.world.util.sign.EncryDigestUtil;
import com.world.util.sign.RSACoder;
import com.world.util.string.MD5;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.ApproveAction;
import com.world.web.response.DataResponse;
import com.world.web.sso.rsa.RsaLoginUtil;
import com.world.web.sso.rsa.RsaUser;
import com.world.web.sso.session.SsoSessionManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;

import static com.world.cache.Cache.Get;
import static com.world.constant.Const.ReasonMap;

public class Index extends ApproveAction {
	private static final long serialVersionUID = 1L;

	UserDao userDao = new UserDao();
	VerifyUserInfoDao vudao = new VerifyUserInfoDao();
	InvestorApplyDao iaDao = new InvestorApplyDao();
	LuckyQualifyDao luckyQualifyDao = new LuckyQualifyDao();
	AuthenticationDao auDao = new AuthenticationDao();
	public final static String LIMIT_KEYWORD = "BTC,LTC,ETC,ETH,DASH,OMG,USDT,QTUM,EOS,ELF,SNT,IOST,ZRX,LINK,KNC,Bitglobal,Bitgloba,GBC,OTCPTA,QQ,微信,企鹅,微博,ICO,政府,习近平,官方,bit全球,otc,btcwinex";

	//	@Page(Viewer = "/cn/manage/index.jsp", des = "基本信息")
	public void index() {
		initLoginUser();

		setAttr("userName", loginUser.getUserName());
		setAttr("hasSafe", loginUser.getHasSafePwd());

		UserContact uc = loginUser.getUserContact();
		int emailStatu = uc.getEmailStatu();
		int mobileStatu = uc.getMobileStatu();

		setAttr("emailStatu", emailStatu);
		setAttr("mobileStatu", mobileStatu);
		setAttr("googleAuth", uc.getGoogleAu());
		setAttr("loginAuth", uc.getLoginGoogleAuth());

		setAttr("showEmail", uc.getShowEmail());
		setAttr("showMobile", uc.getShowMobile());
		setAttr("showAuth", uc.getShowAuth());
		setAttr("showLoginAuth", uc.getShowLoginAuth());

		UserDao dao = new UserDao();
		setAttr("email", emailStatu==AuditStatus.pass.getKey()?dao.shortEmail(uc.getSafeEmail()):"未认证");
		setAttr("mobile", mobileStatu==AuditStatus.pass.getKey()?dao.shortMobile(uc.getSafeMobile()):"");

		if(Get("forgetGo_"+userId()) != null){
			Cache.Delete("googleMcode_"+userId());
		}
		if(Get("forgetGo_"+userId()) != null){
			Cache.Delete("forgetGo_"+userId());
		}
		// 先较验
		Map<Integer, VerifyUserInfo> verifys = vudao.getVerifyMap(loginUser.getId());
		if(verifys.containsKey(1)){
			setAttr("mobileVerify", verifys.get(1).getAddTimeShow());
		}
		if(verifys.containsKey(2)){
			setAttr("googleVerify", verifys.get(2).getAddTimeShow());
		}

		Authentication au = new AuthenticationDao().getByUserId(loginUser.getId());
		int userId = userId();
		int authStatus = AuditStatus.a1NoSubmit.getKey();
		String reason = "";
		if (null != au) {
			authStatus = au.getStatus();
			reason = au.getReason();
		}
		setAttr("reason",reason);
		setAttr("authStatus", authStatus);
		setAttr("authStatusDesc", CommonUtil.getAuditStatusDesc(authStatus));
		/*start by xzhang 20171104 获取当前用户的安全级别*/
		setAttr("userSafeLevel",userDao.getSafeLevel(loginUser));
		/*end*/
//		log.info("查看用户safeLevel:"+loginUser.getUserName()+"为"+userDao.getSafeLevel(loginUser));
		if(uc.getIdCard() != null){
			setAttr("realName", loginUser.getRealName()==null||loginUser.getRealName().length()==0?null:loginUser.getRealName());
			setAttr("cardId", UserUtil.getShortCardId(uc.getIdCard()));
		}
	}

	@Page(Viewer = JSON)
	public void isTransSafe(){
		try {
			initLoginUser();
			boolean isTransSafe = userDao.isNeedSafePwd(loginUser);
			json(isTransSafe + "", true, "", true);
		} catch (Exception e) {
			log.error("内部异常", e);
			json("true", true, "", true);
		}
	}
	@Page(Viewer = JSON)
	public void isNotIpTransSafe(){
		try {
			initLoginUser();
			boolean isTransSafe =false ;
			String whiteIpSet= Get("whiteIp"+userId());
			if(null==whiteIpSet){
				whiteIpSet="";
			}
			String ip = ip();
			if(null!=loginUser.getLoginIp()&&loginUser.getLoginIp().equals(ip)||whiteIpSet.contains(ip)){
				json(isTransSafe + "", true, "", true);
			}else{
				isTransSafe = true;
				json(isTransSafe + "", true, "", true);
			}


		} catch (Exception e) {
			log.error("内部异常", e);
			json("true", true, "", true);
		}
	}

	//Close By suxinjie 一期屏蔽该功能
	//@Page(Viewer = JSON)
	public void ipLog(){
		try {
			UserLoginIpDao uliDao = new UserLoginIpDao();
			int pageIndex = intParam("pageIndex");
			int pageSize = 5;
			Query<UserLoginIp> query = uliDao.getQuery(UserLoginIp.class);
			query.filter("userId =", userIdStr());
			long total = uliDao.count(query);
			List<UserLoginIp> list = null;
			if(total > 0){
				list = uliDao.findPage(query.order("-date"),pageIndex, pageSize);
				if (lan.equalsIgnoreCase("en")) {
					for (UserLoginIp e : list) {
						e.setCity("Unknown");
					}
				}
			}

			Map<String, Object> page = new HashMap<String, Object>();
			page.put("pageIndex", pageIndex);
			page.put("totalCount", total);
			page.put("list", list);
			json("", true, JSONObject.toJSONString(page));
		} catch (Exception e) {
			log.error("内部异常", e);
			json("", false, "");
		}
	}

	/**
	 * 生成密钥
	 */
	@Page(Viewer = JSON)
	public void create(){
		try{
			initLoginUser();
			if (loginUser.getUserContact().getMobileStatu() == 2) {
				String code = param("code");
				if(!isCorrect(code)){//json(L("验证码错误"), false, "");
					return;
				}
				if(!hasEffective()){//json("验证码失效，请重新发送验证码。", false, "");
					return;
				}
			} else {
				UserContact uc = loginUser.getUserContact();
				userDao.setLan(lan);
				Message msg = userDao.isCorrect(loginUser, uc.getSecret(), longParam("googleCode"));
				if (!msg.isSuc()) {
					json(msg.getMsg(), false, "");
					return;
				}
			}
			String key = UUID.randomUUID().toString();//key
			String secret = UUID.randomUUID().toString();//密钥
			Data.Update("update btcuser set apiKey=?,apiSecret=? where userId=?", new Object[]{key ,EncryDigestUtil.digest(secret),loginUser.getId()});
			json(key + "," + secret, true, "");
			new UserDao().clearMobileCode(userIdStr());
			return;
		}catch(Exception ex){
			log.error("内部异常", ex);
			json(L("未知错误"), false, "");
		}
	}

//	@Page(Viewer = "/en/u/safe/codeCheck.jsp")
	public void codeCheck() {
	}

//	@Page(Viewer = "/en/u/safe/safePwdFrame.jsp")
	public void closeSafePwd() {
		initLoginUser();
//		UserContact uc = loginUser.getUserContact();
//		setAttr("googleAuth", uc.getGoogleAu());
		//手机和资金安全密码完全不需要，太麻烦，如果用户有资金安全密码就已经可以交易了，完全不需要关闭
		//setAttr("mobileStatu", uc.getMobileStatu());
		//
//		setAttr("codeType", PostCodeType.close2HPwd.getKey());
//		setAttr("showAudioButton", uc.isShowAudioButton());
	}

	AuthenLogDao logDao = new AuthenLogDao();
	//开启或关闭资金安全密码
	@Page(Viewer = JSON)
	public void useOrCloseSafePwd(){
		try {
			initLoginUser();
			String safePwd = param("payPass");
			int closeStatu = intParam("closeStatu");
			String userId = userIdStr();
            //校验资金密码
            DataResponse dr0 = this.checkVerifiCode(userId,MsgToastKey.TRANSACTION_PAY_PWD,ConstantCenter.UpdFunctionType.TRANSACTION_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
            if (!dr0.isSuc()) {
                json(L(dr0.getDes()), false, "", true);
                return;
            }
            RsaUser rsaUser = RsaLoginUtil.getRsaUser(this);
            byte[] decodedData2 = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(safePwd.replace(" ", "+")),rsaUser.getPriKey());
            safePwd = new String(decodedData2);
            log.info("资金密码："+safePwd);
            String returnVal = safePwdForApp(safePwd, loginUser.getId(),false);
            if(StringUtils.isNotEmpty(returnVal)){
                json(returnVal, false, "", true);
                return;
            }

			if (closeStatu <= 0) {// 开启

				if (userDao.isNeedSafePwd(loginUser)) {
					json(L("资金安全密码已经开启。"), true, "", true);
					return;
				}

				loginUser.setNeedSafePwd(true);

				UpdateResults<User> ur = userDao.setSafePwdExpirationTime(loginUser);

				if (!ur.getHadError()) {
					//logDao.insertOneRecord(AuthenType.closeSafePwd.getKey(), userId, "0", "手动开启资金安全密码。", ip());
					/*Start by guankaili 20181229 添加消息队列 */
					User user = userDao.get(userId);
//					new OperateLogDao().record(userDao.get(userId), LogCategory.USE_SAFE_PWD_SWITCH, "开启成功", ip(), request);
					OperateLogInfoProducer.send(user.getUserName(),user.getId(),LogCategory.USE_SAFE_PWD_SWITCH.getKey(),"开启成功", ip(),CommonUtil.getBrowserInfo(request));
					/*End*/
					json(L("资金安全密码成功开启。"), true, "", true);
					return;
				}else {
					json(L("开启失败!"), false, "", true);
				}
			} else {// 开启
//				if(!safePwd(safePwd, loginUser.getId(), JSON, true)){
//					return;
//				}
				Date dateNow = new Date();
				long expirationTime = closeStatu == 1 ? 0L : dateNow.getTime() + 1000*60*60*6;//| 关闭周期 0：永久关闭 2：6个小时|

				loginUser.setNeedSafePwd(false);
				loginUser.setSafePwdExpiration(expirationTime);

				UpdateResults<User> ur = userDao.setSafePwdExpirationTime(loginUser);

				if (!ur.getHadError()) {
					//logDao.insertOneRecord(AuthenType.closeSafePwd.getKey(), userId, "0", "成功关闭资金安全密码，时间："+(closeStatu==1?"永久":"6小时"), ip());
					/*Start by guankaili 20181229 添加消息队列 */
					User user = userDao.get(userId);
//					new OperateLogDao().record(userDao.get(userId), LogCategory.USE_SAFE_PWD_SWITCH, "关闭成功，时间："+(closeStatu==1?"永久":"6小时"), ip(), request);
					OperateLogInfoProducer.send(user.getUserName(),user.getId(),LogCategory.USE_SAFE_PWD_SWITCH.getKey(),"关闭成功，时间："+(closeStatu==1?"永久":"6小时"), ip(),CommonUtil.getBrowserInfo(request));
					/*End*/
					if(closeStatu == 1){
						json(L("资金安全密码关闭成功，如有需求，可手动开启。"), true, "", true);
					}else{
						json(L("资金安全密码关闭成功，六小时后自动开启。"), true, "", true);
					}
				}else {
					json(L("关闭失败!"), false, "", true);
				}
			}


//			if(safePwd.length() > 0){//关闭
//				if(!safePwd(safePwd, loginUser.getId(), JSON, true)){
//					return;
//				}
//
//				if(!loginUser.getUseSafePwd()){
//					json(L("资金安全密码已经关闭"), false, "", true);
//					return;
//				}
//				int times = closeStatu == 1 ? 30*24*60*60 : 6*60*60;
//
//				Cache.Set(User.use_pwd_key+userId(), closeStatu+"", times);
//				if(closeStatu == 1){
//					json(L("资金安全密码关闭成功，如有需求，可手动开启。"), true, "", true);
//				}else{
//					json(L("资金安全密码关闭成功，六小时后自动开启。"), true, "", true);
//				}
//				logDao.insertOneRecord(AuthenType.closeSafePwd.getKey(), userId()+"", "0", "成功关闭资金安全密码，时间："+(closeStatu==1?"永久":"6小时"), ip());
//			}else{//开启
//				if(loginUser.getUseSafePwd()){
//					json(L("资金安全密码已经开启。"), false, "", true);
//					return;
//				}
//
//				Cache.Delete(User.use_pwd_key+userId());
//				json(L("资金安全密码成功开启。"), true, "", true);
//
//				logDao.insertOneRecord(AuthenType.closeSafePwd.getKey(), userId()+"", "0", "手动开启资金安全密码。", ip());
//
//			}
		} catch (Exception e) {
			log.error("内部异常", e);
			json(L("操作失败"), false, "", true);
		}
	}

//	@Page(Viewer = "/en/u/safe/googleauth.jsp")
	public void forgetSec(){
		initLoginUser();

		Cache.Set("forgetGo_"+userId(), "1", 10*60);
		try {
			response.sendRedirect("/u/safe/googleAuth?oper=0&dealType=googleAuth&dealVal=0");
		} catch (IOException e) {
			log.error("内部异常", e);
		}
	}

//	@Page(Viewer = "/en/u/safe/conn.jsp")
	public void conn(){
		int type = intParam("type");
		setAttr("type", type);
		initLoginUser();
		String account = loginUser.getWeixin();
		if(type == 1){
			account = loginUser.getWeibo();
		}else if(type == 3){
			account = loginUser.getQq();
		}

		setAttr("account", account);
	}

	// 消息提醒
//	@Page(Viewer = "/cn/manage/msgsetting/index.jsp")
	public void msgSetting() {
		initLoginUser();
	}

	//开启或关闭功能
	@Page(Viewer = JSON)
	public void useOrCloseFun(){
		try {
			initLoginUser();
			int closeStatu = intParam("closeStatu");
			boolean isOpen = closeStatu <= 0;
			String attr = param("attr");
			LogCategory logCategory = getFunInfo(attr);
			String userId = userIdStr();

			String optName = isOpen ? L("开启") : L("关闭");
			UpdateResults<User> ur = userDao.setAttrSwitch(loginUser, attr, isOpen);
			if (!ur.getHadError()) {
				/*Start by guankaili 20181229 添加消息队列 */
				User user = userDao.get(userId);
//				new OperateLogDao().record(userDao.get(userId), logCategory, optName, ip(), request);
				OperateLogInfoProducer.send(user.getUserName(),user.getId(),logCategory.getKey(),optName, ip(),CommonUtil.getBrowserInfo(request));
				/*End*/
				json(optName + " " + L(logCategory.getValue()) + " " + L("成功。"), true, "", true);
				return;
			}else {
				json(L(optName + " " + L(logCategory.getValue()) + " " +"失败!"), false, "", true);
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			json(L("操作失败"), false, "", true);
		}
	}


	private LogCategory getFunInfo(String name){
		if(StringUtils.equals(name, "isWebLoginAppNotice")){
			return LogCategory.WEB_LOGIN_NOTICE_APP;
		}

		if(StringUtils.equals(name, "receiveDealSms")){
			return LogCategory.RECEIVE_DEAL_SMS;
		}
		if(StringUtils.equals(name, "receiveDealEmail")){
			return LogCategory.RECEIVE_DEAL_EMAIL;
		}
		if(StringUtils.equals(name, "receiveDealPush")){
			return LogCategory.RECEIVE_DEAL_PUSH;
		}

		throw new RuntimeException(L("开启或关闭的功能不存在"));

	}
		

	/*@Page(Viewer = JSON)
	public void doConn(){
		initLoginUser();
		int type = intParam("type");
		String safePwd = param("payPass");
		String account = param("account");

		if(loginUser != null && safePwd.length() > 0){//关闭
			if(!safePwd(safePwd, loginUser.getId(), JSON, true)){
				return;
			}

			UserDao userDao = new UserDao();

			Datastore ds = userDao.getDatastore();
			Query<User> q = ds.find(User.class, "_id", loginUser.get_Id());
			UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
			if(type == 1){
					List<User> users = userDao.find(userDao.getQuery().filter("weibo =", account)).asList();
					if(users != null && users.size() > 0){
						json(L("微博账号%%已经和网站其他账号关联！" , account), false, "", true);
						return;
					}
					ops.set("weibo", account);
					UpdateResults<User> ur = userDao.update(q, ops);
					if(ur.getError() == null){
						saveBindRecord(loginUser.get_Id(), loginUser.getUserName(), type, account);
						json(L("成功关联微博账号！"), true, "", true);
						return;
					}
			}else if(type == 2){
					List<User> users = userDao.find(userDao.getQuery().filter("weixin =", account)).asList();
					if(users != null && users.size() > 0){
						json(L("微信账号%%已经和网站其他账号关联！" , account), false, "", true);
						return;
					}
					ops.set("weixin", account);
					UpdateResults<User> ur = userDao.update(q, ops);
					if(ur.getError() == null){
						saveBindRecord(loginUser.get_Id(), loginUser.getUserName(), type, account);
						json(L("成功关联微信账号！"), true, "", true);
						return;
					}
			}else if(type == 3){
					List<User> users = userDao.find(userDao.getQuery().filter("qq =", account)).asList();
					if(users != null && users.size() > 0){
						json(L("QQ账号%%已经和网站其他账号关联！" , account), false, "", true);
						return;
					}
					ops.set("qq", account);
					UpdateResults<User> ur = userDao.update(q, ops);
					if(ur.getError() == null){
						saveBindRecord(loginUser.get_Id(), loginUser.getUserName(), type, account);
						json(L("成功关联QQ账号！"), true, "", true);
						return;
					}
			}
		}
		json(L("未知错误"), false, "", true);
		return;
	}*/

	/**
	 * 保存绑定记录
	 */
/*	public void saveBindRecord(String userId, String userName,  int type, String account){
		BindRecordDao dao = new BindRecordDao();
		//如果今天该用户已经绑定过了就不再记录了
		if(dao.getTodayRecord(userId, type).size() <= 0){
			BindRecord record = new BindRecord(dao.getDatastore());
			record.setUserId(userId);
			record.setUserName(userName);
			record.setType(type);
			record.setAccount(account);
			record.setAddTime(now());
			record.setStatus(0);
			record.setGive8btc(0);
			dao.save(record);
		}
	}*/

	@Page(Viewer = JSON)
	public void getUserBaseInfo() {
		StringBuilder sb = new StringBuilder("");
		initLoginUser();
		UserContact uc = loginUser.getUserContact();
		Authentication au = new AuthenticationDao().getByUserId(loginUser.getId());
		int emailStatu = uc.getEmailStatu();
		int mobileStatu = uc.getMobileStatu();
		String email = emailStatu==AuditStatus.pass.getKey()?userDao.shortEmail(uc.getSafeEmail()):"null";
		String mobile = mobileStatu==AuditStatus.pass.getKey()?userDao.shortMobile(uc.getSafeMobile()):"null";
		String realName = "null";
		boolean noauth = false;
		boolean a2noauth = false;
		int sxbAuth = -1;
		if(uc.getIdCard() != null){
			realName = loginUser.getRealName()==null||loginUser.getRealName().length()==0?null:loginUser.getRealName();
		}
		if(au == null){
			noauth = true;
			a2noauth = true;
		} else if (au.getStatus() != AuditStatus.a1Pass.getKey() && au.getStatus() != AuditStatus.pass.getKey()) {
			if (au.getStatus() != AuditStatus.pass.getKey()) {
				a2noauth = true;
			}
			if (!a2noauth && au.getStatus() != AuditStatus.a1Pass.getKey()) {
				noauth = true;
			}
		} else {
			sxbAuth = au.getSxbAuth();
		}
//		int emailStatu = uc.getEmailStatu();
//		int mobileStatu = uc.getMobileStatu();
//
//		setAttr("emailStatu", emailStatu);
//		setAttr("mobileStatu", mobileStatu);
//		setAttr("googleAuth", uc.getGoogleAu());
//		setAttr("loginAuth", uc.getLoginGoogleAuth());
//
//		setAttr("showEmail", uc.getShowEmail());
//		setAttr("showMobile", uc.getShowMobile());
//		setAttr("showAuth", uc.getShowAuth());
//		setAttr("showLoginAuth", uc.getShowLoginAuth());
//		UserDao dao = new UserDao();
//		setAttr("email", emailStatu==AuditStatus.pass.getKey()?dao.shortEmail(uc.getSafeEmail()):"未认证");
//		RemindDao rDao = new RemindDao();
//		Remind remind = rDao.findOne(rDao.getQuery().filter("userId", userIdStr()));
//		setAttr("remind", remind);
//		if(Cache.Get("forgetGo_"+userId()) != null){
//			Cache.Delete("googleMcode_"+userId());
//		}
//		if(Cache.Get("forgetGo_"+userId()) != null){
//			Cache.Delete("forgetGo_"+userId());
//		}
		sb.append("[{");
		sb.append("\"userName\" : \"" + loginUser.getUserName() + "\"");
		sb.append(",\"version\" : \"" + loginUser.getUserVersion().getValue() + "\"");
		sb.append(",\"weibo\" : \"" + loginUser.getWeibo() + "\"");
		sb.append(",\"weixin\" : \"" + loginUser.getWeixin() + "\"");
		sb.append(",\"qq\" : \"" + loginUser.getQq() + "\"");
		sb.append(",\"email\" : \"" + email + "\"");
		sb.append(",\"hasSafe\" : \"" + loginUser.getHasSafePwd() + "\"");
		sb.append(",\"useSafePwd\" : \"" + loginUser.getUseSafePwd() + "\"");
		sb.append(",\"mobile\" : \"" + mobile + "\"");
		sb.append(",\"googleAuth\" : \"" + uc.getLoginGoogleAuth() + "\"");
		sb.append(",\"realName\" : \"" + realName + "\"");
		sb.append(",\"a1noauth\" : \"" + noauth + "\"");
		sb.append(",\"a2noauth\" : \"" + a2noauth + "\"");
		sb.append(",\"sxbAuth\" : \"" + sxbAuth + "\"");
		sb.append("}]");
		json("sucess",true,sb.toString(),true);
	}

	@Page(Viewer = "")
	public void getGoogleAuthQr(){

		initLoginUser();

		String secret = param("secret");
		if(secret == null)
			return;

		try{
			response.setContentType( "image/png" );
			response.setHeader( "Pragma", "No-cache" );
			response.setHeader( "Cache-Control", "no-cache" );
			response.setDateHeader( "Expires", 0 );

			OutputStream os = response.getOutputStream();

			int width = 150;
			int height = 150;

			if (null != loginUser.getUserName()) {

				String myGoogleStr = String.format("otpauth://totp/%s:%s%%3Fsecret%%3D%s" , FileConfig.getValue("title"), URLEncoder.encode(loginUser.getUserName() , "utf-8"), secret);
				//参与生成二维码的文本
				String codec = URLDecoder.decode(myGoogleStr, "utf-8");
				//生成二维码图片
				QRCodeGenerator.encode(os, codec, width, height);

				os.flush();
				os.close();
			}
		}catch(Exception ex){
			log.error("内部异常", ex);
		}
	}

	@Page(Viewer = JSON)
	public void getUserInfo() {
		String userIdStr = userIdStr();
		JSONObject json = new JSONObject();

		User user = userDao.getById(userIdStr);

		boolean simpleAuth = false, depthAuth = false, emailAuth = false, mobileAuth = false, googleAuth = false,
				pwdStatus = false, safePwdStatus = false;
		if (null != user) {
			UserContact uc = user.getUserContact();
			if (null != uc) {
				if (uc.getEmailStatu() == 2) {
					emailAuth = true;
				}
				if (uc.getMobileStatu() == 2) {
					mobileAuth = true;
				}
				if (uc.getGoogleAu() == 2) {
					googleAuth = true;
				}
			}
			if (null != user.getPwd() && !"".equals(user.getPwd())) {
				pwdStatus = true;
			}
			if (null != user.getSafePwd() && !"".equals(user.getSafePwd())) {
				safePwdStatus = true;
			}
		}
		json.put("simpleAuth", simpleAuth);
		json.put("depthAuth", depthAuth);
		json.put("emailAuth", emailAuth);
		json.put("mobileAuth", mobileAuth);
		json.put("googleAuth", googleAuth);
		json.put("pwdStatus", pwdStatus);
		json.put("safePwdStatus", safePwdStatus);

		Response.append(jsonp(json.toString()));
	}

	@Page(Viewer = JSON)
	public void getAssets() {
		String userIdStr = userIdStr();
		JSONArray funds = UserCache.getUserFunds(userIdStr);

		Response.append(jsonp(funds.toString()));
	}

	/**
	 * 页面和资产初始化；web.asset
	 */
	@Page(Viewer = JSON)
	public void getAssetsDetail() {
		String userIdStr = userIdStr();
		FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/payUser");
		PayUserApiService payUserApiService = container.getFeignClient(PayUserApiService.class);
		String response = payUserApiService.getDetail(userIdStr);
//		// FIXME: 2017/7/20 suxinjie 设置为true,内部使用LinkdedHashMap,具备排序特性
//		JSONObject json = new JSONObject(true);
//		Map<String, JSONObject> marketsMap = Market.getMarketsMap();
//		JSONObject prices = LoanAutoFactory.getPrices();
//		Set<String> marketNames = Market.getAllMarketName();
//
//        //buxianguan 20171219 添加币种能否充值提现，不能在缓存里加，trans也会更新这个缓存
//        Map<Integer, CoinProps> coinFundsTypeMap = DatabasesUtil.getCoinPropFundsTypeMaps();
//        for(int i=0;i<funds.size();i++){
//			JSONObject obj = funds.getJSONObject(i);
//
//			BigDecimal total = obj.getBigDecimal("total");
//			//小数位截取在前端做，后端不处理 buxianguan
////			if(total.compareTo(new BigDecimal("0.000001")) <0){
////				total = BigDecimal.ZERO;
////			}else{
////				total = trimZeroAfterPoint(total);
////			}
//			obj.put("total",total+"");
//			BigDecimal freeze = obj.getBigDecimal("freeze");
////			if(freeze.compareTo(new BigDecimal("0.000001")) <0){
////				freeze = BigDecimal.ZERO;
////			}else{
////				freeze = trimZeroAfterPoint(freeze);
////			}
//			obj.put("freeze",freeze+"");
//
//			BigDecimal balance = obj.getBigDecimal("balance");
////			if(balance.compareTo(new BigDecimal("0.000001")) <0){
////				balance = BigDecimal.ZERO;
////			}else{
////				balance = trimZeroAfterPoint(balance);
////			}
//			obj.put("balance",balance+"");
//			/*start by xzhang 20171215 交易页面三期PRD:法币则算*/
//			String market = obj.getString("propTag").toString().toLowerCase()+"_usdt";
//			if(marketNames.contains(market)){
//				obj.put("usdExchange",prices.getBigDecimal(market));
//			}else{
//				market = obj.getString("propTag").toString().toLowerCase()+"_btc";
//				if(marketNames.contains(market)){
//					obj.put("usdExchange",prices.getBigDecimal(market).multiply(new BigDecimal(Get("btc_usdt"))));
//				}else{
//					obj.put("usdExchange","--");
//				}
//			}
//			if("USDT".equals(obj.getString("propTag"))) {
//				obj.put("usdExchange", new BigDecimal(1));
//			}
//            /*end*/
//			json.put(obj.getString("propTag"), obj);
//		}
		Response.append(jsonp(response));
	}


	/**
	 * 页面和资产初始化；web.asset
	 */
	@Page(Viewer = JSON)
	public void getAssetsOtcDetail() {
		String userIdStr = userIdStr();
		FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/payUser");
		PayUserApiService payUserApiService = container.getFeignClient(PayUserApiService.class);
		String response = payUserApiService.getOtcDetail(userIdStr);
//		JSONArray funds = UserCache.getUserOtcFunds(userIdStr);
//		// FIXME: 2017/7/20 suxinjie 设置为true,内部使用LinkdedHashMap,具备排序特性
//		JSONObject json = new JSONObject(true);
//		Map<String, JSONObject> marketsMap = Market.getMarketsMap();
//		JSONObject prices = LoanAutoFactory.getPrices();
//		Set<String> marketNames = Market.getAllMarketName();
//
//		//buxianguan 20171219 添加币种能否充值提现，不能在缓存里加，trans也会更新这个缓存
//		Map<Integer, CoinProps> coinFundsTypeMap = DatabasesUtil.getCoinPropFundsTypeMaps();
//		for(int i=0;i<funds.size();i++){
//			JSONObject obj = funds.getJSONObject(i);
//
//			BigDecimal total = obj.getBigDecimal("total");
//			//小数位截取在前端做，后端不处理 buxianguan
////			if(total.compareTo(new BigDecimal("0.000001")) <0){
////				total = BigDecimal.ZERO;
////			}else{
////				total = trimZeroAfterPoint(total);
////			}
//			obj.put("total",total+"");
//			BigDecimal freeze = obj.getBigDecimal("freeze");
////			if(freeze.compareTo(new BigDecimal("0.000001")) <0){
////				freeze = BigDecimal.ZERO;
////			}else{
////				freeze = trimZeroAfterPoint(freeze);
////			}
//			obj.put("freeze",freeze+"");
//
//			BigDecimal balance = obj.getBigDecimal("balance");
////			if(balance.compareTo(new BigDecimal("0.000001")) <0){
////				balance = BigDecimal.ZERO;
////			}else{
////				balance = trimZeroAfterPoint(balance);
////			}
//			obj.put("balance",balance+"");
//			/*start by xzhang 20171215 交易页面三期PRD:法币则算*/
//			String market = obj.getString("propTag").toString().toLowerCase()+"_usdt";
//			if(marketNames.contains(market)){
//				obj.put("usdExchange",prices.getBigDecimal(market));
//			}else{
//				market = obj.getString("propTag").toString().toLowerCase()+"_btc";
//				if(marketNames.contains(market)){
//					obj.put("usdExchange",prices.getBigDecimal(market).multiply(new BigDecimal(Get("btc_usdt"))));
//				}else{
//					obj.put("usdExchange","--");
//				}
//			}
//			if("USDT".equals(obj.getString("propTag"))) {
//				obj.put("usdExchange", new BigDecimal(1));
//			}
//            /*end*/
//			json.put(obj.getString("propTag"), obj);
//		}
		Response.append(jsonp(response));
	}




	@Page(Viewer = JSON)
	public void getWalletDetail() {
		String userIdStr = userIdStr();
		FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/payUser");
		PayUserApiService payUserApiService = container.getFeignClient(PayUserApiService.class);
		String response = payUserApiService.getWalletDetail(userIdStr);
//		JSONArray funds = UserCache.getUserWalletFunds(userIdStr);
//		// FIXME: 2017/7/20 suxinjie 设置为true,内部使用LinkdedHashMap,具备排序特性
//		JSONObject json = new JSONObject(true);
//		Map<String, JSONObject> marketsMap = Market.getMarketsMap();
//		JSONObject prices = LoanAutoFactory.getPrices();
//		Set<String> marketNames = Market.getAllMarketName();
//
//		//buxianguan 20171219 添加币种能否充值提现，不能在缓存里加，trans也会更新这个缓存
//		Map<Integer, CoinProps> coinFundsTypeMap = DatabasesUtil.getCoinPropFundsTypeMaps();
//		for(int i=0;i<funds.size();i++){
//			JSONObject obj = funds.getJSONObject(i);
//
//			BigDecimal total = obj.getBigDecimal("total");
//			//小数位截取在前端做，后端不处理 buxianguan
////			if(total.compareTo(new BigDecimal("0.000001")) <0){
////				total = BigDecimal.ZERO;
////			}else{
////				total = trimZeroAfterPoint(total);
////			}
//			obj.put("total",total+"");
//			BigDecimal freeze = obj.getBigDecimal("freeze");
////			if(freeze.compareTo(new BigDecimal("0.000001")) <0){
////				freeze = BigDecimal.ZERO;
////			}else{
////				freeze = trimZeroAfterPoint(freeze);
////			}
//			obj.put("freeze",freeze+"");
//
//			BigDecimal balance = obj.getBigDecimal("balance");
////			if(balance.compareTo(new BigDecimal("0.000001")) <0){
////				balance = BigDecimal.ZERO;
////			}else{
////				balance = trimZeroAfterPoint(balance);
////			}
//			obj.put("balance",balance+"");
//			/*start by xzhang 20171215 交易页面三期PRD:法币则算*/
//			String market = obj.getString("propTag").toString().toLowerCase()+"_usdt";
//			if(marketNames.contains(market)){
//				obj.put("usdExchange",prices.getBigDecimal(market));
//			}else{
//				market = obj.getString("propTag").toString().toLowerCase()+"_btc";
//				if(marketNames.contains(market)){
//					obj.put("usdExchange",prices.getBigDecimal(market).multiply(new BigDecimal(Get("btc_usdt"))));
//				}else{
//					obj.put("usdExchange","--");
//				}
//			}
//			if("USDT".equals(obj.getString("propTag"))) {
//				obj.put("usdExchange", new BigDecimal(1));
//			}
//            /*end*/
//
//			//添加币种能否充值提现
//			Integer fundsType = obj.getInteger("fundsType");
//			if (null != fundsType) {
//				CoinProps coinProps = coinFundsTypeMap.get(fundsType);
//				if (null != coinProps) {
//					obj.put("canCharge", coinProps.isCanCharge());
//					obj.put("canWithdraw", coinProps.isCanWithdraw());
//				}
//			}
//			json.put(obj.getString("propTag"), obj);
//		}
		Response.append(jsonp(response));
	}

    @Page(Viewer = JSON)
    public void getFinancialDetail() {
        String userIdStr = userIdStr();
        FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/payUser");
        PayUserApiService payUserApiService = container.getFeignClient(PayUserApiService.class);
        String response = payUserApiService.getFinancialDetail(userIdStr);
        Response.append(jsonp(response));
    }

	/**
	 * 获取币种列表
	 */
    @Page(Viewer = JSON)
    public void getAllCoinList() {
        String userIdStr = userIdStr();
        String transFrom = param("transFrom"); //1钱包 2：币币 3：法币 4：otc 5：理财
        String transTo = param("transTo"); //1：钱包 2：币币 3：法币 4：otc 5：理财

        if (StringUtils.isEmpty(transFrom) || StringUtils.isEmpty(transTo) || transFrom.equals(transTo)) {
            Response.append(jsonp(""));
            return;
        }

        String fundsFrom = getFundsDetail(transFrom, userIdStr);
        String fundsTo = getFundsDetail(transTo, userIdStr);

        JSONObject result = new JSONObject(true);
        //from 和 to 取交集
        LinkedHashMap<String, JSONObject> fundsFromMap = com.alibaba.fastjson.JSON.parseObject(fundsFrom, new TypeReference<LinkedHashMap<String, JSONObject>>() {
        });
        LinkedHashMap<String, JSONObject> fundsToMap = com.alibaba.fastjson.JSON.parseObject(fundsTo, new TypeReference<LinkedHashMap<String, JSONObject>>() {
        });

        for (Map.Entry<String, JSONObject> entry : fundsFromMap.entrySet()) {
            String key = entry.getKey();
            if (fundsToMap.containsKey(key)) {
                result.put(key, entry.getValue());
            }
        }

        Response.append(jsonp(result.toJSONString()));
    }

    private String getFundsDetail(String direction, String userIdStr) {
        FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url") + "/payUser");
        PayUserApiService payUserApiService = container.getFeignClient(PayUserApiService.class);

        switch (direction) {
            case "1":
                return payUserApiService.getWalletDetail(userIdStr);
            case "2":
                return payUserApiService.getDetail(userIdStr);
            case "3":
                return payUserApiService.getOtcDetail(userIdStr);
            case "4":
                return payUserApiService.getFuturesDetail(userIdStr);
            case "5":
                return payUserApiService.getFinancialDetail(userIdStr);
            default:
                return payUserApiService.getWalletDetail(userIdStr);
        }
    }

	//startBychendi
	@Page(Viewer = JSON)
	public void getCoin() {
		Map<Integer, CoinProps> coinFundsTypeMap = DatabasesUtil.getCoinPropFundsTypeMaps();
		JSONObject obj = new JSONObject();
		for(Integer key : coinFundsTypeMap.keySet() ){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("fundsType",coinFundsTypeMap.get(key).getFundsType());
			jsonObject.put("propTag",coinFundsTypeMap.get(key).getPropTag());
			jsonObject.put("canCharge",coinFundsTypeMap.get(key).isCanCharge());
			obj.put(coinFundsTypeMap.get(key).getPropTag(),jsonObject);
		}
		Response.append(jsonp(obj.toJSONString()));
	}

	//去掉小数点后的0和取整
	private BigDecimal trimZeroAfterPoint(BigDecimal val){
		DecimalFormat df = new DecimalFormat("###.#########");
		val = val.setScale(6,BigDecimal.ROUND_DOWN);
		return new BigDecimal(df.format(val));
	}

	//申请投资人表单模板页
//	@Page(Viewer = "/cn/manage/loan/applyForm.jsp")
	public void applyForm() {
		Map<String,CoinProps> coinMap =  DatabasesUtil.getCoinPropMaps();
		super.setAttr("coinMap", coinMap);
	}

	@Page(Viewer = JSON)
	public void doInvestorApply() {

		Query q = iaDao.getQuery();
		String userId = userIdStr();
		q.filter("userId", userId);
		MyInvestorApply ia = null;
		ia = iaDao.findOne(q);
		if (null != ia) {
			json(L("您已提交过申请，不能重复提交！"), false, "");
			return;
		}
		ia = new MyInvestorApply();
		P2pUser p2pUser = new P2pUserDao().getById(userId);
		String loanCoin = "";
		String loanCoinCNY = param("loanCoinCNY");
		String loanCoinBTC = param("loanCoinBTC");
		String loanCoinETH = param("loanCoinETH");
		String loanCoinETC = param("loanCoinETC");
		String loanCoinLTC = param("loanCoinLTC");
		if (StringUtils.isNotBlank(loanCoinCNY)) {
			if (StringUtils.isNotBlank(loanCoin)) {
				loanCoin += ",";
			}
			loanCoin += loanCoinCNY;
		}
		if (StringUtils.isNotBlank(loanCoinBTC)) {
			if (StringUtils.isNotBlank(loanCoin)) {
				loanCoin += ",";
			}
			loanCoin += loanCoinBTC;
		}
		if (StringUtils.isNotBlank(loanCoinETH)) {
			if (StringUtils.isNotBlank(loanCoin)) {
				loanCoin += ",";
			}
			loanCoin += loanCoinETH;
		}
		if (StringUtils.isNotBlank(loanCoinETC)) {
			if (StringUtils.isNotBlank(loanCoin)) {
				loanCoin += ",";
			}
			loanCoin += loanCoinETC;
		}
		if (StringUtils.isNotBlank(loanCoinLTC)) {
			if (StringUtils.isNotBlank(loanCoin)) {
				loanCoin += ",";
			}
			loanCoin += loanCoinLTC;
		}
		if (StringUtils.isBlank(loanCoin)) {
			json(L("请至少选择一种投资币种。"), false, "");
			return;
		}
		String loanAmount = param("loanAmount");
		String loanPeriod = param("loanPeriod");
		String loanRate = param("loanRate");
		String loanRisk = param("loanRisk");
		String loanName = param("loanName");
		String loanPhone = param("loanPhone");

		if (StringUtils.isBlank(loanName)) {
			json(L("请填写正确的姓名。"), false, "");
			return;
		}
		if (StringUtils.isBlank(loanPhone)) {
			json(L("请填写正确的手机号。"), false, "");
			return;
		}
		ia.setUserId(p2pUser.getUserId());
		ia.setUserName(p2pUser.getUserName());
		ia.setInvestmentCurrency(loanCoin);
		ia.setInvestmentAmount(loanAmount);
		ia.setInvestmentCycle(loanPeriod);
		ia.setInvestmentRate(loanRate);
		ia.setGuarantee(loanRisk);
		ia.setName(loanName);
		ia.setPhone(loanPhone);
		ia.setDate(TimeUtil.getNow());
		iaDao.save(ia);
		json(L("提交申请成功，请耐心等待我们的审核。"), true, "");

	}


	// ============== 新增接口 ==============


//	@Page(Viewer = "/cn/manage/loginlogs/index.jsp")
	public void toUserLoginHistroy() {
	}

	/**
	 * 获取用户登录历史记录
	 */
	@Page(Viewer = JSON)
	public void queryUserLoginHistroy() {
		int pageIndex = intParam("pageIndex");
		int pageSize = intParam("pageSize") == 0 ? PAGE_SIZE : intParam("pageSize");

		UserLoginIpDao userLoginIpDao = new UserLoginIpDao();
		// 查询总数量
		Query<UserLoginIp> query = userLoginIpDao.getQuery(UserLoginIp.class);
		query.filter("userId = ", userIdStr());
		long total = userLoginIpDao.count(query);

		// 查询分页列表
		List<UserLoginIp> list = new ArrayList<>();
		if (total > 0) {
			list = userLoginIpDao.getIps(userIdStr(), pageIndex, pageSize);
		}

		Map<String, Object> pageResult = new HashMap<>();
		pageResult.put("pageIndex", pageIndex);
		pageResult.put("totalCount", total);
		pageResult.put("list", list);

		json("", true, JSONObject.toJSONString(pageResult));
	}

	/**
	 * 委托时资金密码验证
	 * @return
	 */
	@Page(Viewer = JSON)
	public void safePwdForEnturst() {
		setLan();
		String payPass = param("payPass");
        try {
            payPass = URLDecoder.decode(payPass, "UTF-8");
        } catch (Exception e) {
            log.info("资金密码转义出错" + payPass);
        }
        try {
			initLoginUser();
			String userId = userIdStr();
			//判断资金密码是否正确
			int status = new UserDao().checkSecurityPwdForEntrust(payPass, userId);
			SafeTipsTag stt = getLanTag().getStt();
//		boolean flg = new UserDao().checkPayPwdForEntrust(payPass, userId);
//		if(flg){
//			log.info("资金密码输入正确");
//            json("", true, "");
//			return;
//		}
			/*Start by guankaili 20181119 币币WEB优化(B029) */
//        if (status == -4) {
//            String suoDing = L("交易功能已被锁定，请24小时之后再试。");
//            json(suoDing, false, "");
//        } else if (status == -2) {
//			String suoDing = L("资金密码输入错误超出限制，将锁定交易、提现、重置资金密码功能，请等待24小时之后尝试使用。");
//			json(suoDing, false, "");
//		} else if (status == -1) {
//			json(stt.getCuoWu(), false, "");
//		} else if (status == 0) {
//			json(stt.getWeiSheZhi(), false, "");
//		} else if(status == -3){
//			json("", true, "");
//		}else{
//			String jihui = L("资金密码错误，您还有%%次机会。",status+"");
//			json(jihui, false, "");
//		}
			int payLoginLock = doGetErrorTimes(loginUser.getId(),LimitType.PayLoginPassError);
			int payEmailLock = doGetErrorTimes(loginUser.getId(),LimitType.PayEmailPassError);
			int payMobileLock = doGetErrorTimes(loginUser.getId(),LimitType.PayMobilePassError);
			int payGoogleLock = doGetErrorTimes(loginUser.getId(),LimitType.PayGooglePassError);
			int withdrawPayPwdLock = doGetErrorTimes(loginUser.getId(),LimitType.WithdrawPayPwdPassError);

			Map<String, Object> result = new HashMap<>();
			result.put("ashLockStatus", 0);
			if((payLoginLock == -2) || (payEmailLock == -2)
					|| (payMobileLock == -2) || (payGoogleLock == -2)
					|| (withdrawPayPwdLock == -2)){
				result.put("ashLockStatus", 1);
			}
			if (loginUser.getSafePwdModifyTimes() > 1 && null != loginUser.getSafePwdModifyTime()
					&& TimeUtil.getOriginDiffDay(now(), loginUser.getSafePwdModifyTime()) < 1) {
				result.put("ashLockStatus", 1);
			}
			if (status == -2) {
				result.put("ashLockStatus", 1);
				String suoDing = L("资金密码输入次数超出限制，将锁定提现、修改资金密码、设置收款方式功能，请24小时之后再试");
				json(suoDing, false, JSONObject.toJSONString(result));
			} else if (status == -1) {
				json(stt.getCuoWu(), false, JSONObject.toJSONString(result));
			} else if (status == 0) {
				json(stt.getWeiSheZhi(), false, JSONObject.toJSONString(result));
			} else if(status == -3){
				json("", true, JSONObject.toJSONString(result));
			} else{
				String jihui = L("资金密码输入有误。");
				json(jihui, false, JSONObject.toJSONString(result));
			}
			/*end*/
		}catch (Exception e){
			log.error("10100503VIPJYPMJY【资金密码校验】 com.world.controller.manage.index#safePwdForEnturst",e);
		}
	}


	/*start by xzhang 20171215 交易页面三期PRD:*/
	/**
	 * @describe 用户收藏市场(用户触发)
	 * @return json
	 * 1.根据市场信息判断用户请求信息是否合法。
	 * 2.查询用户历史是否有市场收藏信息：
	 * 		Y:判断新增该市场是否已存在历史收藏中,(极少异常情况下会存在,由于cookie和数据库未正常同步)。
	 * 			Y：存在更新数据库，仅更新缓存。
	 * 			N：不存在根据数据库，并更新缓存数据。
	 * 		N：直接新增一条用户与市场的关联信息
	 *	缓存中作用：
	 *		Key：isUserCollect --> 标示用户是否存在收藏市场
	 *			作用：主要用于用户在未收藏任何市场情况下，该标示为0。限制用户每次请求读取缓存为空时，查询数据库操作。
	 *		Key：userCollect -->  存储用户关注信息
	 */
	@Page(Viewer = ".json")
	public void userCollect(){
		String market = param("market");
		if(!StringUtil.exist(market)){
			json("取消收藏失败,请求参数为空", false, null);
			return;
		}
		JSONObject marketJson = Market.getMarketByName(market.substring(0, market.indexOf("_hot")));
		if (null == marketJson) {
			json("取消收藏失败,市场不存在", false, null);
			return;
		}
		String fullName = marketJson.getString("numberBiFullName");
		String marketTmp = marketJson.getString("market")+"_hotdata_"+fullName;
		if (!market.equals(marketTmp)) {
			json("取消收藏失败，请求参数异常", false, null);
			return;
		}
		String userId = userIdStr();
		String  collect = "";
		try {
			CollectMarket collectMarket = (CollectMarket) Data.GetOne("SELECT id,userId,collect from collectmarket WHERE userId=? ", new Object[]{userId}, CollectMarket.class);
			if (collectMarket == null) {
				collect = market;
				Data.Update("insert into collectmarket (userId,collect) values (?,?) ", new Object[]{userId, collect});
			} else {
				collect = collectMarket.getCollect();
				if (collect.indexOf(market) == -1) {
					collect = collect + "-" + market;
					Data.Update("UPDATE collectmarket SET collect = '" + collect + "' WHERE userId = ? ", new Object[]{userId});
				}
			}
			Cache.Set("isUserCollect" + userId(), "1");
			Cache.Set("userCollect" + userId(), collect);
		}catch (Exception e){
			log.error("【收藏市场】用户："+userId+"收藏市场："+market+"异常，异常信息为：",e);
			json(L("收藏失败"), false, null);
			return;
		}
		json(L("收藏成功"), true, null);
	}
	/**
	 * @describe 用户取消收藏市场(用户触发)
	 * @return json
	 *1.根据市场信息判断用户请求信息是否合法。
	 * 2.查询用户历史是否有市场收藏信息：
	 * 		Y:判断该市场是否存在该记录中。
	 * 			1.如果仅收藏了该市场，那么直接删除该市场信息。
	 * 			2.如果收藏了该市场和其他市场，那么仅移除该市场信息。
	 * 			3.刷新缓存。
	 * 		N：直接不处理
	 */
	@Page(Viewer = ".json")
	public void closeCollect(){
		String market = param("market");

		if(!StringUtil.exist(market)){
			json("取消收藏失败,请求参数为空", false, null);
			return;
		}
		JSONObject marketJson = Market.getMarketByName(market.substring(0, market.indexOf("_hot")));
		if (null == marketJson) {
			json("取消收藏失败,市场不存在", false, null);
			return;
		}
		String fullName = marketJson.getString("numberBiFullName");
		String marketTmp = marketJson.getString("market")+"_hotdata_"+fullName;
		if (!market.equals(marketTmp)) {
			json("取消收藏失败，请求参数异常", false, null);
			return;
		}
		String userId = userIdStr();
		String  collect = "";
		try {
			CollectMarket collectMarket = (CollectMarket) Data.GetOne("SELECT id,userId,collect from collectmarket WHERE userId=? ", new Object[]{userId}, CollectMarket.class);
			if (collectMarket != null) {
				collect = collectMarket.getCollect();
				if (collect.indexOf(market) != -1) {
					if(collect.equals(market)){
						Data.Update("delete from collectmarket WHERE userId= ? ", new Object[]{userId});
						Cache.Set("isUserCollect" + userId(), "0");
						Cache.Delete("userCollect" + userId());
					}else{
						if(collect.startsWith(market)){
							collect = collect.replaceAll(market+"\\-","");
						}else{
							collect = collect.replaceAll("\\-"+market,"");
						}
						Data.Update("UPDATE collectmarket SET collect='" + collect + "' WHERE userId=? ", new Object[]{userId});
						Cache.Set("isUserCollect" + userId(), "1");
						Cache.Set("userCollect" + userId(), collect);
					}
				}
			}
		}catch (Exception e){
			log.error("【取消收藏】用户："+userId+"取消收藏市场："+market+"异常，异常信息为：",e);
			json(L("失败"), false, null);
			return;
		}
		json(L("成功"), true, null);
	}
	/*end*/

	/**
	 * 获取用户分发记录
	 */
	@Page(Viewer = JSON)
	public void queryUserDistribution() {
		int pageIndex = intParam("pageIndex");
		int pageSize = intParam("pageSize") == 0 ? 30 : intParam("pageSize");
		String type = param("type");
		String userId = userIdStr();
		List<BillDistribution> list  = new ArrayList<BillDistribution>();
		Long count = 0L;
		try {
			count = luckyQualifyDao.userDistributionCount(userId, type);
			// 查询分页列表

			if (count > 0) {
				list = luckyQualifyDao.userDistribution(userId, type, pageIndex, pageSize, lan);
			}
		}catch (Exception e){
			log.error("【查询分发记录】当前用户："+userId+"查询分发记录发生非受控异常，异常信息为：",e);
		}
		Map<String, Object> pageResult = new HashMap<>();
		pageResult.put("pageIndex", pageIndex);
		pageResult.put("totalCount", count);
		pageResult.put("list", list);
		json("", true, JSONObject.toJSONString(pageResult));
	}

	//分发记录跳转
//	@Page(Viewer = "/cn/manage/account/distriButionHistory.jsp")
	public void queryDistribution() {

	}

	/**
	 * 奖金翻倍弹框
	 */
	@Page(Viewer = JSON)
	public void popout() {
		String userId = userIdStr();
		String luckyDouble = Cache.Get("luckyDouble_"+userId);
		if(!StringUtil.exist(luckyDouble)){
			String amount = luckyQualifyDao.getUnShowInfo(userId);
			Cache.Set("luckyDouble_"+userId, "01");
			json("", true, JSONObject.toJSONString(amount));
		}else if("02".equals(luckyDouble)){
			String amount = luckyQualifyDao.getUnShowInfo(userId);
			Cache.Set("luckyDouble_"+userId, "01");
			json("", true, JSONObject.toJSONString(amount));
		}else{
			json("", true, JSONObject.toJSONString(""));
		}
	}

	/**
	 * 配合前端改造
	 */
	@Page(Viewer = JSON)
	public void user() {
		try {
			initLoginUserJson();
			String reason = "";
			Boolean isLock = false;
			Boolean isBlack = false;
			int lockState;
			Map<String, Object> result = new HashMap<>();
			Authentication auth = auDao.getByUserId(userIdStr());
			int authStatus = AuditStatus.a1NoSubmit.getKey();
			if (auth != null) {
				authStatus = auth.getStatus();
				if (!StringUtils.isBlank(auth.getReason())) {
					reason = L(ReasonMap.get(auth.getReason()));
				}
				lockState = auth.getLockStatus();
				if (lockState == 1) {
					Timestamp lockTime = auth.getLockTime();
					if(System.currentTimeMillis() - lockTime.getTime() < 72 * 60 * 60 * 1000){
						isLock = true;
					}
				}
				String value = Cache.Get(Const.black_key+userIdStr());
				if(StringUtils.isNotBlank(value)){
					isBlack = true;
				}
				result.put("lockTime", auth.getLockTime());
				result.put("isLock", isLock);
			}
			result.put("reason", reason);
			result.put("isBlack", isBlack);
			result.put("userName", loginUser.getUserName());
			result.put("nickname", loginUser.getNickname());
			result.put("color", loginUser.getColor());
			result.put("authStatus", authStatus);
			result.put("userSafeLevel", userDao.getSafeLevel(loginUser));
			result.put("previousLogin", loginUser.getPreviousLogin());
			result.put("loginIp", loginUser.getLoginIp());
			result.put("emailSatus", loginUser.getUserContact().getEmailStatu());
			result.put("email", loginUser.getEmail());
			result.put("mobileStatus", loginUser.getUserContact().getMobileStatu());
			//result.put("mobileStatus", 1);

			String mobile = loginUser.getUserContact().getSafeMobile();
			result.put("mobile", mobile);
			result.put("mobilec", loginUser.getUserContact().getmCode());
			result.put("hasSafe", loginUser.getHasSafePwd());
			result.put("googleAuth", loginUser.getUserContact().getGoogleAu());

			//判断是否需要二重验证
			String loginStatus = GetCookie(GlobalConfig.session + "loginStatus");
			if (StringUtils.isNotBlank(loginStatus) && loginStatus.equals(String.valueOf(LoginStatus.NEED_TWO_AUTHEN.getKey()))) {
				result.put("vipRate", 0);
			}else{
				result.put("vipRate", loginUser.getVipRate());
			}
			result.put("storeLevel", loginUser.getUserQualificationLevel());
			result.put("isSmsOpen", loginUser.getSmsOpen());
			result.put("isGoogleOpen",loginUser.getGoogleOpen());
			result.put("twoAuth",false);
	//		String twoAuthCheck = LimitType.NoSecondVrifyEmailError.name() + "_" + loginUser.getId();
	//		int twoAuthTimes = Integer.parseInt(twoAuthCheck.split("_")[0]);
	//		if(StringUtils.isNotEmpty(twoAuthCheck) && twoAuthTimes >=3){
	//			result.put("twoAuth",false);
	//		}
			if (userDao.isNeedSafePwd(loginUser)) {
				result.put("checkTrans",0);
			}else{
				User user = userDao.getUserById(loginUser.getId());
				if(user.isNeedSafePwd()){
					result.put("checkTrans",0);
				}else if(!user.isNeedSafePwd() && user.getSafePwdExpiration() == 0L){
					result.put("checkTrans",1);
				}else if(!user.isNeedSafePwd() && user.getSafePwdExpiration() > 0L){
					result.put("checkTrans",6);
				}
			}
			//安全设置锁定
			DataResponse dr0 = this.checkVerifiCode(loginUser.get_Id(),MsgToastKey.TRANSACTION_PAY_PWD,ConstantCenter.UpdFunctionType.TRANSACTION_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr0.isSuc()) {
				result.put("safeSet",true);
			}else{
				result.put("safeSet",false);
			}
			//check手机
			DataResponse mobileDr = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.OPEN_MOBILE_VERIFY, ConstantCenter.UpdFunctionType.OPEN_MOBILE_VERIFY, MsgToastKey.LOCK_24_HOUR);
			if (!mobileDr.isSuc()) {
				result.put("hasMobileCheckBox",false);
			}else{
				result.put("hasMobileCheckBox",true);
			}
			//资金密码锁定
			//校验设置资金密码登录密码
			DataResponse dr1 = this.checkVerifiCode(loginUser.get_Id(),MsgToastKey.SETTING_PAY_PWD,ConstantCenter.UpdFunctionType.SET_USER_INFO_LOGINPWD, MsgToastKey.LOCK_24_HOUR);
			//校验原登录密码
			DataResponse dr2 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD, MsgToastKey.LOCK_24_HOUR);

			//校验邮箱密码
			DataResponse dr3 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);

			//校验手机验证码
			DataResponse dr4 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_MOBILE, MsgToastKey.LOCK_24_HOUR);

			//校验谷歌验证码
			DataResponse dr5 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_GOOGLE, MsgToastKey.LOCK_24_HOUR);
			//校验谷歌验证码
			DataResponse dr6 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.WITHDRAWAL_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			//otc释放货币锁定
			DataResponse dr7 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.OTC_RELEASECOIN_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			DataResponse dr8 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.OTC_CAD_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if(!dr1.isSuc() || !dr2.isSuc() || !dr3.isSuc() || !dr4.isSuc() || !dr5.isSuc() || !dr6.isSuc() || !dr7.isSuc() || !dr8.isSuc() || checkSafePwdLock(loginUser)){
				result.put("ppwLock",true);
			}else{
				result.put("ppwLock",false);
			}
			/*Start by guankaili 20181120 前端要求传引导页标识 */
			//引导标识
	//		String guideFlg = Cache.Get(Const.guide_flg+userIdStr());
			result.put("guideFlg",loginUser.getGuideFlg());
			if(loginUser.getGuideFlg()){

				userDao.updateGuideFlg(loginUser.getId());
			}
	//		if(StringUtils.isNotEmpty(guideFlg)){
	//			Cache.Delete(Const.guide_flg+userIdStr());
	//		}else{
	//
	//		}
			/*end*/
			json("ok", true, JSONObject.toJSONString(result), true);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			json("出现异常", false, "", true);
		}
	}

	/**
	 * 是否可以充值提现
	 */
	@Page(Viewer = JSON)
	public void isCanOper() {
		//币种名
		String coinName = param("coinName");
		CoinProps coinProps = DatabasesUtil.coinPropsByName(coinName);
		if(null != coinProps){
			Map<String, Object> result = new HashMap<>();
			result.put("coinName",coinProps.getPropTag());
			Boolean canCharge = true;
			Boolean canWithdraw = true;
			if(coinName.equals("USDT")){
				CoinProps usdteCoinProps = DatabasesUtil.getUsdtAggrement(102);
				if(!coinProps.isCanCharge() && !usdteCoinProps.isCanCharge()){
					canCharge = false;
				}
				if(!coinProps.isCanWithdraw() && !usdteCoinProps.isCanWithdraw()){
					canWithdraw = false;
				}
				result.put("coinName",coinProps.getPropTag());
				result.put("canCharge",canCharge);
				result.put("canWithdraw",canWithdraw);
			}else{
				result.put("coinName",coinProps.getPropTag());
				result.put("canCharge",coinProps.isCanCharge());
				result.put("canWithdraw",coinProps.isCanWithdraw());
			}
			json("ok", true, JSONObject.toJSONString(result), true);
		}else{
			json("找不到此币种! ", false, null);
		}
	}
	/**
	 * 验证交易密码是否锁定
	 *
	 * @param user
	 * @return
	 */
	private boolean checkSafePwdLock(User user) {
		if (-1 == LimitType.SafePassError.GetStatusNew(user.get_Id())) {
			return true;
		}

		if (user.getSafePwdModifyTimes() > 1 && null != user.getSafePwdModifyTime()
				&& userDao.getDiffDay(now(), user.getSafePwdModifyTime()) < 1) {
			return true;
		}
		return false;
	}
	/**
	 * 16,保存昵称
	 */
	@Page(Viewer = JSON)
	public void saveNickName() {

		initLoginUserJson();
		String userId = userIdStr();
		String nickname = param("nickname");
		//String token = param("token");
		String color = param("color");
//		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
//			json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
//			return;
//		}
		User user = userDao.findOne("_id",userId);
		if (null == user) {
			json(SystemCode.code_3004, L(SystemCode.code_3004.getValue()),"");
			return;
		}
		//判断昵称是否含有关键字眼
		List<String> limitList = Arrays.asList(LIMIT_KEYWORD.split(","));
		if(!CollectionUtils.isEmpty(limitList)){
			for(String limitKeyword : limitList){
				if(nickname.toUpperCase().contains(limitKeyword.toUpperCase())){
					json(L("昵称不可包含非法符号"), false, JSONObject.toJSONString(L("昵称不可包含非法符号")));
					return;
				}
			}
		}
		User userCheck = userDao.findOne("nickname",nickname);
		if (null != userCheck) {
			json(L("该昵称已被占用"), false, JSONObject.toJSONString(L("该昵称已被占用")));
			return;
		}
		Datastore ds = userDao.getDatastore();
		Query<User> q = ds.find(User.class, "_id", userId);
		UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
		ops.set("nickname", nickname);
		ops.set("nick", nickname);
		if(StringUtils.isNotBlank(color)){
			ops.set("color", color);
		}else{
			ops.set("color","#9BC979");
		}

		UpdateResults<User> ur = userDao.update(q, ops);
		if (!ur.getHadError()) {
			json(L("设置成功"), true, JSONObject.toJSONString(L("设置成功")));
			return;
		} else {
			json(L("失败"), false, JSONObject.toJSONString(L("失败")));
			return;
		}
	}

}
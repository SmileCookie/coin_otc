package com.world.controller.admin.user.authen;

import com.file.config.FileConfig;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.cache.Cache;
import com.world.constant.Const;
import com.world.model.dao.ReasonDao;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.dao.lucky.LuckyRuleDao;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.user.CountryDao;
import com.world.model.dao.user.EmailDao;
import com.world.model.dao.user.MobileDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.authen.AuthenHistoryDao;
import com.world.model.dao.user.authen.AuthenLogDao;
import com.world.model.dao.user.authen.AuthenticationDao;
import com.world.model.dao.user.authen.IdcardDao;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.Reason;
import com.world.model.entity.ReasonType;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.user.User;
import com.world.model.entity.user.authen.AreaInfo;
import com.world.model.entity.user.authen.AuditType;
import com.world.model.entity.user.authen.AuthenHistory;
import com.world.model.entity.user.authen.AuthenType;
import com.world.model.entity.user.authen.Authentication;
import com.world.model.entity.user.authen.Idcard;
import com.world.util.AuthUtil;
import com.world.util.CommonUtil;
import com.world.web.Page;
import com.world.web.ReqParamType;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import com.world.web.sso.session.Session;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static com.world.constant.Const.ReasonMap;

@FunctionAction(jspPath = "/admins/user/authen/", des = "实名认证")
public class Index extends AdminAction {
	AuthenticationDao dao = new AuthenticationDao();
	UserDao userDao = new UserDao();
	AuthenLogDao logDao = new AuthenLogDao();
	AdminUserDao adminDao = new AdminUserDao();
	//AuthFundSetDao authFundSetDao = new AuthFundSetDao();
	IdcardDao idcardDao = new IdcardDao();
	//BankcardDao bankcardDao = new BankcardDao();
	AuthenticationDao auDao = new AuthenticationDao();
	AuthenHistoryDao ahDao = new AuthenHistoryDao();
	LuckyRuleDao luckyRuleDao = new LuckyRuleDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		int pageNo = intParam("page");
		String  userId = param("userId");
		String  userName = param("userName");//用户名
		String  realName = param("realName");//用户名
		String  cardId = param("cardId");//用户名
		String cardType = param("cardType");
		int  areaInfo = intParam("areaInfo");
		int type = CommonUtil.stringToInt(param("type"), -1);

		Query<Authentication> q = dao.getQuery();
		int pageSize = 20;
		q.filter("status !=",0);
		// 将参数保存为attribute
		try {
			if (userName.length()>0) {//用户名
				User u = new UserDao().getByField("userName", userName);
				if(u!=null) {
					q.filter("userId", u.getId());
				} else {
					q.filter("userId", -1);
				}
			}
			q.order("status,- submitTime");
			if(areaInfo > 0 ){
				q.filter("areaInfo", areaInfo);
			}
			if(realName.length()>0){//真实姓名
				Pattern pattern = Pattern.compile("^.*"  + realName+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				q.filter("realName", pattern);
			}

			if(cardId.length()>0){//身份证
				q.filter("cardId", cardId);
			}

			if(cardType.length()>0){//身份证
				q.filter("cardType", cardType);
			}

			if(type != -1){
				if(type == AuditType.corporate.getKey()){
					q.filter("type", type);
				}else{
					q.or(
							q.criteria("type").equal(type),
							q.criteria("type").doesNotExist()
					);
				}
			}

			/*if(tab.equals("a1wait")){
				q.filter("status", AuditStatus.a1NoAudite.getKey());
				q.order("submitTime");
			}else if("a1pass".equals(tab)){
				q.filter("status", AuditStatus.a1Pass.getKey());
				q.order("- checkTime");
			}else if("a1unpass".equals(tab)){
				q.filter("status", AuditStatus.a1NoPass.getKey());
				q.order("- checkTime");
			}

			if("blackList".equals(tab)){
				q.filter("isCardIdBlackList", true);
			}else if( !"all".equals(tab)){
				q.or(
						q.criteria("isCardIdBlackList").equal(null),
						q.criteria("isCardIdBlackList").equal(false)
				);
			}*/

			log.info("搜索的sql语句:" + q.toString());

			long total = dao.count(q);
			if (total > 0) {
				List<Authentication> dataList = dao.findPage(q, pageNo, pageSize);
				
				List<String> adminIds = new ArrayList<String>();
				List<String> userIds = new ArrayList<String>();
				for(Authentication au : dataList){
					if(au.getAdminId() != null && au.getAdminId().length() > 0)
						adminIds.add(au.getAdminId());
					userIds.add(au.getUserId());
				}
				
				AdminUserDao auDao = new AdminUserDao();
				if(adminIds.size() > 0){
					Map<String , AdminUser> users = auDao.getUserMapByIds(adminIds);
					for(Authentication au : dataList){
						au.setaUser(users.get(au.getAdminId()));
					}
				}
				
				UserDao uDao = new UserDao();
				Map<String, User> users = uDao.getUserMapByIds(userIds);
				for(Authentication au : dataList){
					au.setUser(users.get(au.getUserId()));
					String cardTrueId = au.getCardId();
					String cardFalseId = cardTrueId.substring(0,2) + "****" + cardTrueId.substring(cardTrueId.length()-2);
					if(au.getStatus() == AuditStatus.a1NoAudite.getKey()){
						au.setCheckTime(null);
					}
					au.setCardId(cardFalseId);
				}
				
				setAttr("dataList", dataList);
			}
			setPaging((int) total, pageNo, pageSize);
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	// ajax的调用
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}
	
	@Page(Viewer = "/admins/user/authen/see.jsp")
	public void see(){
		String id = param("id");
		Authentication au = dao.get(id);
		User u = userDao.getUserById(au.getUserId());
		setAttr("uId", id);


		Query ahq = ahDao.getQuery();
		ahq.order("-submitTime");
		ahq.filter("userId", au.getUserId());
		List<AuthenHistory> dataList = ahq.asList();
		if(CollectionUtils.isEmpty(dataList)){
			setAttr("authTimes", 0);
		}else{
			setAttr("authTimes", dataList.size());
		}
		if (null != au) {
			if(au.getType() == AuditType.corporate.getKey()){
				setAttr("businessLicenseImg", FileConfig.getValue("imgDomain1")+"/picauth?file="+au.getBusinessLicenseImg()+"&type=1");

			}else {
				setAttr("proofAddressImg", FileConfig.getValue("imgDomain1")+"/picauth?file=" + au.getProofAddressImg() + "&type=1");
				Idcard idcard = idcardDao.findOne(idcardDao.getQuery().filter("name =", au.getRealName()).filter("cardno =", au.getCardId()));
				if (null != idcard) {
					setAttr("idcardImg", null != idcard.getPhoto() ? idcard.getPhoto() : "");
				}
			}
			logDao.insertOneRecord(AuthenType.realname.getKey(),au.getUserId(), Integer.toString(adminId()), "查看实名认证信息。", ip());
			setAttr("au", au);
			setAttr("u", u);

			//setAttr("imagePrefix", Const.IMAGE_PREFIX_NEED_AUTH);
			setAttr("imagePrefix", FileConfig.getValue("imgDomain1")+"/picauth?type=1&file=");
		}
	}


	@Page(Viewer = "/admins/user/authen/onlySee.jsp")
	public void onlySee(){
		String id = param("id");
		Authentication au = dao.get(id);
		User u = userDao.getUserById(au.getUserId());
		setAttr("uId", id);

		Query ahq = ahDao.getQuery();
		ahq.order("-submitTime");
		ahq.filter("userId", au.getUserId());
		List<AuthenHistory> dataList = ahq.asList();
		if(CollectionUtils.isEmpty(dataList)){
			setAttr("authTimes", 0);
		}else{
			setAttr("authTimes", dataList.size());
		}

		if (null != au) {
			if(au.getType() == AuditType.corporate.getKey()){
				setAttr("businessLicenseImg", FileConfig.getValue("imgDomain1")+"/picauth?file="+au.getBusinessLicenseImg()+"&type=1");

			}else {
				setAttr("proofAddressImg", FileConfig.getValue("imgDomain1")+"/picauth?file=" + au.getProofAddressImg() + "&type=1");
				Idcard idcard = idcardDao.findOne(idcardDao.getQuery().filter("name =", au.getRealName()).filter("cardno =", au.getCardId()));
				if (null != idcard) {
					setAttr("idcardImg", null != idcard.getPhoto() ? idcard.getPhoto() : "");
				}
			}
			setAttr("au", au);
			setAttr("u", u);
			//setAttr("imagePrefix", Const.IMAGE_PREFIX_NEED_AUTH);
			setAttr("imagePrefix", FileConfig.getValue("imgDomain1")+"/picauth?type=1&file=");
		}
	}
	
	@Page(Viewer = JSON)
	public void seeIdCardImg() {
		String id = param("id");
		Authentication au = dao.get(id);
		String cacheKey = "get_idcardimg_" + au.getCardId();
		if (null != Cache.Get(cacheKey)) {
			json("返照操作太频繁了。", false, "");
			return;
		}
		Cache.Set(cacheKey, id, 10*60);
		Idcard idcard = idcardDao.getIdcardPhoto(au.getRealName(), au.getCardId());
		if (null != idcard) {
			json(idcard.getPhoto(), true, "");
		} else {
			json("身份证系统返照失败，姓名与身份证不一致，请勿通过该实名认证", false, "");
		}
	}
	
	@Page(Viewer = JSON)
	public void pass() {
		try {
			String id = param("vid");
			String reason = param("reason");
			String state = param("state");
			
			Authentication au = dao.get(id);


			if(au.getStatus() == AuditStatus.a1Pass.getKey()){
				json("操作失败。", false, "");
				return;
			}
			String adminId = Integer.toString(adminId());
			
			Datastore ds = dao.getDatastore();
			UpdateOperations<Authentication> operate = ds.createUpdateOperations(Authentication.class);
			operate.set("adminId", adminId);
			operate.set("checkTime", now());
			if (!StringUtils.isBlank(reason)) {
				operate.set("reason", reason);
				au.setReason(reason);
			}
			if ("1".equals(state)) { //通过
				operate.set("status", AuditStatus.a1Pass.getKey());
				au.setStatus(AuditStatus.a1Pass.getKey());
			/*start by xwz 2017-06-10*/
			} else if("2".equals(state)) { //不通过
				if(StringUtils.isBlank(reason)){
					json("请选择不通过原因。", false, "");
					return;
				}
				au.setStatus(AuditStatus.a1NoPass.getKey());
				operate.set("status", AuditStatus.a1NoPass.getKey());
			}
			/*end*/
			// 实名认证时，如果该身份已经认证过，则抹除该账号的推荐人关系。
			/*Query q1 = auDao.getQuery().filter("cardId", au.getCardId());
			long count = q1.countAll();
			if (count > 0) {
				Datastore ds2 = userDao.getDatastore();
				Query<User> q = ds2.find(User.class, "_id", au.getUserId());
				UpdateOperations<User> ops = ds2.createUpdateOperations(User.class);
				ops.set("recommendId", "");
				ops.set("recommendName", "");
				userDao.update(q, ops);
			}*/
			User user = new UserDao().get(au.getUserId());
			UpdateResults<Authentication> ur = dao.update(dao.getQuery(Authentication.class).filter("_id =", id), operate);
			PostCodeType pct = PostCodeType.authenPass;
			String title = L(pct.getValue());
			String content = L(pct.getDes());
			EmailDao eDao = new EmailDao();
			if (!ur.getHadError()) {
				new UserDao().updateRealName(au.getUserId(), au);
				if("1".equals(state)){ //通过
					logDao.insertOneRecord(AuthenType.realname.getKey(),au.getUserId(), adminId, "实名认证通过审核。", ip());
					pct = PostCodeType.authenPass;
					title = L(pct.getValue());
					content = L(pct.getDes());
				}else if("2".equals(state)){//不通过
					logDao.insertOneRecord(AuthenType.realname.getKey(),au.getUserId(), adminId, "实名认证不通过审核，原因："+L(ReasonMap.get(reason)), ip());
					pct = PostCodeType.authenNoPass;
					title = L(pct.getValue());
					content = L(pct.getDes());
				}
				//用户通过认证后，自动清除累计认证错误次数。
				/*Datastore ds1 = userDao.getDatastore();
				Query<User> q2 = ds1.find(User.class, "_id", userIdStr());
				userDao.update(q2, ds1.createUpdateOperations(User.class).set("failAuthTimes", 0));*/
				
				//高级实名认证积分 没这个需求了

				/*new JifenDao().updateJifen(JifenType.deepAuthen, user.getId());
				
				// 更新充值/提现记录
				String updateSql1 = "update Bank_Trade set Status=? where Status=? and Is_In=0 and User_Id=?";
				Data.Update(updateSql1, new Object[]{BankTradeStatus.WaitConfirm.getId(), BankTradeStatus.NEEDAUTH.getId(), au.getUserId()});
				String updateSql2 = "update Bank_Trade set Status=? where Status=? and Is_In=1 and User_Id=?";
				Data.Update(updateSql2, new Object[]{BankTradeStatus.WaitRecharge.getId(), BankTradeStatus.NEEDAUTH.getId(), au.getUserId()});
				dao.updateNeedAuth(Integer.parseInt(au.getUserId()), "1", 0);
				dao.updateNeedAuth(Integer.parseInt(au.getUserId()), "2", 0);
				*/
				try {
					ahDao.updateStatus(au.getUserId(), au.getStatus(), adminId, "");
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
				if("1".equals(state)){
					try {
						luckyRuleDao.unEventFreez(au.getUserId());
					}catch (Exception e){
						log.error(e.toString(), e);
					}
				}
				json("操作成功。", true, "");
				/*try {
					Pusher.push("您已成功通过该用户的实名认证，请登录查看。", user.getJpushKey(), MsgType.successOrFailToRealNameAuthentication);//非移动端登录，推送登录提示
				} catch (Exception e) {
					log.error("【极光推送】当前用户:"+user.get_Id()+",所用registrationId："+user.getJpushKey()+"推送："+MsgType.successOrFailToRealNameAuthentication.getValue()+"消息异常，异常信息为:", e);
				}*/


				if(!StringUtils.isBlank(user.getUserContact().getSafeMobile())){
				// 发送短信
					MobileDao mDao = new MobileDao();
				/*start by xzhang 20171031 /短信服务临时解决方法，除+86外全发英文*/
				//去掉该逻辑，所有都按照用户选择语言发送 modify by buxianguan 20190805
//					if(!MsgUtil.isContain(user.getUserContact().getSafeMobile())){
//						title = Lan.Language("en", pct.getValue());
//						content = Lan.Language("en", pct.getDes());
//					}
				/*end*/
					mDao.sendSms(user, ip(), title, content, user.getUserContact().getSafeMobile());
					log.info(user.getUserName() + "实名认证，发送短信" + pct.getDes());
				}else{
					//发送邮件
					//发邮箱
					if("2".equals(state)){
						content = eDao.getAuthFailEmailHtml(user, lan, user.getUserContact().getSafeEmail(),this,L(ReasonMap.get(reason)));
					}else{
						content = eDao.getAutoSendEmailHtml(lan,user.getUserName(), content);
					}
					eDao.sendEmail(ip(), user.get_Id(), user.getUserName(), title, content, user.getUserContact().getSafeEmail());
					log.info(user.getUserName() + "实名认证，发送邮件" + pct.getDes());
				}


			} else {
				json("审核失败。", false, "");
			}
			
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	public void updateVersion(Authentication au, String adminId){
		Datastore ds = dao.getDatastore();
		UpdateOperations<Authentication> operate = ds.createUpdateOperations(Authentication.class);
		operate.set("adminId", adminId);
		operate.set("checkTime", now());
		
		operate.set("status", AuditStatus.pass.getKey());
		
		UpdateResults<Authentication> ur = dao.update(dao.getQuery(Authentication.class).filter("_id =", au.getId()), operate);
		if (!ur.getHadError()) {
			au.setStatus(AuditStatus.pass.getKey());
			new UserDao().updateRealName(au.getUserId(), au);
			
			//高级实名认证积分
			/*if(StringUtils.isNotBlank(au.getPhoto())){
				User user = new UserDao().get(au.getUserId());
				new JifenDao().updateJifen(JifenType.login, user.getId());
			}*/

			logDao.insertOneRecord(au.getUserId(), adminId, "实名认证通过审核。", ip(), now());
		}
	}
	
	@Page(Viewer = JSON)
	public void passMore() {
		try {
			String ids = param("vids");
			
			String adminId = Integer.toString(adminId());
			
			String idArr[] = ids.split("\\,");
			for(int i = 0; i < idArr.length; i ++){
				Authentication au = dao.get(idArr[i]);
				updateVersion(au, adminId);
			}
			
			json("成功通过用户提交的实名认证请求。", true, "");
			
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
	
	@Page(Viewer="/admins/user/authen/reason.jsp")
	public void reason(){
		try {
			String id = param("id");
			String roleId = GetCookie(Session.rid);
			
			ReasonDao rDao = new ReasonDao();
			List<Reason> reasons = rDao.find(rDao.getQuery().filter("type", ReasonType.unpass_special.getKey())).asList();
			request.setAttribute("reasons", reasons);
			
			request.setAttribute("roleId", roleId);
			
			request.setAttribute("passType", param("passType"));
			request.setAttribute("operation", "unpass");
			request.setAttribute("beanId", id);
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	@Page(Viewer = JSON)
	public void unpass() {
		try {
			String id = param("vid");
			String reason = param("reason");
			
			int infoPass = intParam("infoPass");// 基本信息是否通过
			int idImgPass = intParam("idImgPass");// 身份证照片是否通过
			int bankPass = intParam("bankPass");// 银行信息是否通过 
			int proofAddressPass = intParam("proofAddressPass");// 住址证明是否通过
			
			Authentication au = dao.get(id);
			
			if(au.getStatus() == AuditStatus.noPass.getKey()){
				
				json("操作失败。", false, "");
				return;
			}
			
			String passType = param("passType");
			
			String adminId = Integer.toString(adminId());
			
			Datastore ds = dao.getDatastore();
			UpdateOperations<Authentication> operate = ds.createUpdateOperations(Authentication.class);
			operate.set("adminId", adminId);
			operate.set("checkTime", now());
			operate.set("reason", reason);
			
			if ("1".equals(passType)) {
				au.setStatus(AuditStatus.a1NoPass.getKey());
				operate.set("status", AuditStatus.a1NoPass.getKey());
			} else if("2".equals(passType)) {
				au.setStatus(AuditStatus.noPass.getKey());
				operate.set("status", AuditStatus.noPass.getKey());
				operate.set("infoPass", infoPass);
				operate.set("idImgPass", idImgPass);
				operate.set("bankPass", bankPass);

			} else{
				au.setStatus(AuditStatus.c3unpass.getKey());
				operate.set("status", AuditStatus.c3unpass.getKey());
				operate.set("proofAddressPass", proofAddressPass);
			}
			String cacheKey = "get_idcardimg_" + au.getCardId();
			if (null != Cache.Get(cacheKey)) {
				Cache.Delete(cacheKey);
			}
			UpdateResults<Authentication> ur = dao.update(dao.getQuery(Authentication.class).filter("_id =", id), operate);
			if (!ur.getHadError()) {
				new UserDao().updateRealName(au.getUserId(), au);
				logDao.insertOneRecord(au.getUserId(), adminId, "实名认证不通过审核，原因："+reason, ip(), now());
				
				try {
					ahDao.updateStatus(au.getUserId(), au.getStatus(), adminId, reason);
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
				json("成功不通过用户提交的实名认证请求。", true, "");
			} else {
				json("审核失败。", false, "");
			}
			// 更新认证总次数
			userDao.increaseValue(userIdStr(), "failAuthTimes");
			
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
	
	@Page(Viewer="/admins/reason.jsp")
	public void cancel(){
		try {
			String id = param("id");
			String roleId = GetCookie(Session.rid);
			
			ReasonDao rDao = new ReasonDao();
			List<Reason> reasons = rDao.find(rDao.getQuery().filter("type", ReasonType.unpass_special.getKey())).asList();
			request.setAttribute("reasons", reasons);
			
			request.setAttribute("roleId", roleId);
			
			request.setAttribute("operation", "cancel");
			request.setAttribute("beanId", id);
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
	
	@Page(Viewer = JSON)
	public void cancelAuthen() {
		try {
//			if(!codeCorrect(XML)){
//				return;
//			}
			String id = param("vid");
			String reason = param("reason");
			String adminId = Integer.toString(adminId());
			
			Authentication au = dao.get(id);
			
			Datastore ds = dao.getDatastore();
			UpdateOperations<Authentication> operate = ds.createUpdateOperations(Authentication.class);
			operate.set("adminId", adminId);
			operate.set("checkTime", now());
			operate.set("reason", reason);
			operate.set("cardId", "");
			operate.set("gegisterCity", "");
			operate.set("status", AuditStatus.noSubmit.getKey());
			
			UpdateResults<Authentication> ur = dao.update(dao.getQuery(Authentication.class).filter("_id =", id), operate);
			if (!ur.getHadError()) {
				au.setStatus(AuditStatus.noSubmit.getKey());
				new UserDao().updateRealName(au.getUserId(), au);
				logDao.insertOneRecord(au.getUserId(), adminId, "取消实名认证，原因："+reason+"，认证前的证件号码："+au.getCardId(), ip(), now());
				try {
					ahDao.updateStatus(au.getUserId(), au.getStatus(), adminId, au.getReason());
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
				json("成功取消该用户的实名认证。", true, "");
			} else {
				json("取消失败。", false, "");
			}
			
		} catch (Exception ex) {
			log.error("内部异常", ex);
			json("取消实名认证出现错误。", false, "");
		}
	}
	
	/**
	 * 特殊情况下，管理员通过用户实名认证
	 */
	@Page(Viewer = "/admins/user/authen/auth.jsp")
	public void authen(){
		String id = param("id");
		Authentication au = dao.get(id);
		setAttr("au", au);
		setAttr("userId", param("userId"));
	}
	
	@Page(Viewer = ".xml")
	public void doHandPass(){
		try {
			//手机短信验证
			String code = param("code");
			if(!isCorrect(code, XML)){
				return;
			}
			//谷歌验证码
			if(!codeCorrect(XML)){
   				return;
   			}
			
			String id = param("vid");
			String userId = param("userId");
			String realName = param("realName");
			String cardId = param("cardId");
			
			Authentication au = dao.get(id);
			if(au != null){
				Datastore ds = dao.getDatastore();
				UpdateOperations<Authentication> operate = ds.createUpdateOperations(Authentication.class);
				operate.set("adminId", adminId());
				operate.set("checkTime", now());
				operate.set("reason", "用户认证失败后管理员手动通过认证");
				operate.set("cardId", cardId);
				operate.set("realName", realName);
				operate.set("status", AuditStatus.pass.getKey());
				
				UpdateResults<Authentication> ur = dao.update(dao.getQuery(Authentication.class).filter("_id =", au.getId()), operate);
				if(!ur.getHadError()){
					au.setStatus(AuditStatus.pass.getKey());
					new UserDao().updateRealName(au.getUserId(), au);
					
					logDao.insertOneRecord(au.getUserId(), adminId()+"", "用户认证失败后，管理员手动通过认证，真实姓名："+realName + ", 身份证号：" + cardId, ip(), now());
					json("成功给该用户通过实名认证。", true, "");
				}else{
					WriteError("操作失败。");
				}
			}else{
				Authentication authen = new Authentication();
				authen.setUserId(userId);
				authen.setAdminId(adminId()+"");
				authen.setRealName(realName);
				authen.setCardId(cardId);
				authen.setAreaInfo(AreaInfo.dalu.getKey());
				authen.setIp(ip());
				authen.setSubmitTime(now());
				authen.setLoadImg("");
				authen.setStatus(AuditStatus.pass.getKey());
				if(!dao.updateAuth(authen).getHadError()){
					authen.setStatus(AuditStatus.pass.getKey());
					new UserDao().updateRealName(userId, authen);
					
					logDao.insertOneRecord(userId, adminId()+"", "管理员"+adminName()+"手动通过用户实名认证，认证的真实姓名："+realName + ", 身份证号：" + cardId, ip(), now());
					json("成功给该用户通过实名认证。", true, "");
				}else{
					WriteError("操作失败。");
				}
			}
			
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
	
	
	/*@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		try {
			int userId = intParam("id");
			int type = CommonUtil.stringToInt(param("type"));
			Query<AuthFundSet> q = authFundSetDao.getQuery()
					.filter("userId =", userId);
			if(type != 0 && userId == 0){
				q.filter("type", type);
				setAttr("type", type);
			}

			List<AuthFundSet> list = q.asList();
			AuthFundSet a = null;

			list = setIndividualDefault(userId, type, list);

			if (null != list && list.size() > 0) {
				a = list.get(0);

				//获取默认的
				if(userId>0){
					setAttr("defaultData", authFundSetDao.getDefault(type));
				}
			}else{
				a = new AuthFundSet();
				a.setUserId(userId);

				//获取默认的
				setAttr("defaultData", authFundSetDao.getDefault(type));
			}
			setAttr("data", a);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}*/

	/*private List<AuthFundSet> setIndividualDefault(int userId, int type, List<AuthFundSet> list) {
		Query<AuthFundSet> q;
		AuthFundSet a;
		if( (null == list || list.size() < 1 ) &&
                userId == 0 && type == AuditType.individual.getKey()){
            q = authFundSetDao.getQuery()
                    .filter("userId =", userId);
            list = q.asList();

            if(null != list && list.size() > 0){
                a = list.get(0);
                a.setType(type);

                Datastore ds = authFundSetDao.getDatastore();
                q = ds.find(AuthFundSet.class, "_id", a.getId());
                UpdateOperations<AuthFundSet> ops = ds.createUpdateOperations(AuthFundSet.class);
                ops.set("type", type);
                authFundSetDao.update(q, ops);
            }
        }
		return list;
	}
*/
	/*@Page(Viewer = XML)
	public void doAoru() {
		try {
			Long id = longParam("id");
			Double a1Recharge = doubleParam("a1Recharge");
			Double a2Recharge = doubleParam("a2Recharge");
			Double a1Download = doubleParam("a1Download");
			Double a2Download = doubleParam("a2Download");
			Double a1BtcDownload = doubleParam("a1BtcDownload");
			Double a2BtcDownload = doubleParam("a2BtcDownload");
			Double a1LtcDownload = doubleParam("a1LtcDownload");
			Double a2LtcDownload = doubleParam("a2LtcDownload");
			Double cashLimit = doubleParam("cashLimit");
			Double dayLimit = doubleParam("dayLimit");
			int limitSwitch = intParam("limitSwitch");
			int type = CommonUtil.stringToInt(param("type"));
			boolean isSuc = false;
			if (null != id && id > 0) {
				
				Datastore ds = authFundSetDao.getDatastore();
				Query<AuthFundSet> q = ds.find(AuthFundSet.class, "_id", id);
				UpdateOperations<AuthFundSet> ops = ds.createUpdateOperations(AuthFundSet.class);
				ops.set("a1Recharge", a1Recharge);
				ops.set("a1Download", a1Download);
				ops.set("a2Recharge", a2Recharge);
				ops.set("a2Download", a2Download);
				ops.set("a1BtcDownload", a1BtcDownload);
				ops.set("a2BtcDownload", a2BtcDownload);
				ops.set("a1LtcDownload", a1LtcDownload);
				ops.set("a2LtcDownload", a2LtcDownload);
				ops.set("cashLimit", cashLimit);
				ops.set("dayLimit", dayLimit);
				ops.set("limitSwitch", limitSwitch);

				AuthFundSet af = authFundSetDao.findOne(authFundSetDao.getQuery().filter("_id", id));
				af.setA1Recharge(a1Recharge);
				af.setA1Download(a1Download);
				af.setA2Recharge(a2Recharge);
				af.setA2Download(a2Download);
				af.setA1BtcDownload(a1BtcDownload);
				af.setA2BtcDownload(a2BtcDownload);
				af.setA1LtcDownload(a1LtcDownload);
				af.setA2LtcDownload(a2LtcDownload);
				af.setCashLimit(cashLimit);
				af.setDayLimit(dayLimit);
				af.setLimitSwitch(limitSwitch);
				printEntity(af, AuthFundSet.class);
				
				UpdateResults<AuthFundSet> ur = authFundSetDao.update(q, ops);
				if (!ur.getHadError()) {
					isSuc = true;
				}
			} else {
				AuthFundSet af = new AuthFundSet();
				af.setA1Recharge(a1Recharge);
				af.setA1Download(a1Download);
				af.setA2Recharge(a2Recharge);
				af.setA2Download(a2Download);
				af.setA1BtcDownload(a1BtcDownload);
				af.setA2BtcDownload(a2BtcDownload);
				af.setA1LtcDownload(a1LtcDownload);
				af.setA2LtcDownload(a2LtcDownload);
				af.setCashLimit(cashLimit);
				af.setDayLimit(dayLimit);
				af.setLimitSwitch(limitSwitch);
				af.setUserId(intParam("userId"));

				if(type > 0){
					af.setType(type);
				}

				if (null != authFundSetDao.save(af)) {
					isSuc = true;
				}
			}
			if (isSuc) {
				WriteRight("设置成功");
			} else {
				WriteError("设置失败");
			}
			
		} catch (Exception e) {
			log.error(e.toString(), e);
			WriteError("设置失败，程序出错");
		}
	}*/
	
	@Page(Viewer = "/admins/user/authen/bankinfo.jsp")
	public void bankInfo() {
		try {
			String id = param("id");
			Query q = dao.getQuery().filter("_id =", id);
			Authentication authen = dao.findOne(q);
			setAttr("data", authen);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}
	
	/*@Page(Viewer = XML)
	public void dobankcheck() {
		try {
			boolean isSuc = false;
			String id = param("id");
			Query q = dao.getQuery().filter("_id =", id);
			Authentication authen = dao.findOne(q);
			
			if (null != authen) {
				isSuc = bankcardDao.validBankcard(authen.getRealName(), authen.getCardId(), authen.getBankCard(), 
						authen.getBankTel(), authen.getBankCardType(), authen.getBankCvv2(), authen.getBankExpiredate());
				if (isSuc) {
					WriteRight("校验通过");
				} else {
					WriteError("校验不通过");
				}
			} else {
				WriteError("校验不通过");
			}
			
		} catch (Exception e) {
			log.error(e.toString(), e);
			WriteError("校验不通过，程序出错");
		}
	}*/
	
	@Page(Viewer = "/admins/user/authen/setAuthName.jsp")
	public void setAuthName() {
		try {
			String userId = param("id");
			User user = userDao.getById(userId);
			Authentication auth = dao.getByField("userId", userId);
			setAttr("user", user);
			setAttr("auth", auth);
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
	
	@Page(Viewer = XML)
	public void doSetAuthName() {
		try {
			String userId = param("id");
			String realName = param("realName");
			String idcard = param("idCard");
			int areaInfo = intParam("areaInfo");
			Datastore ds = dao.getDatastore();
			Query<Authentication> q = ds.find(Authentication.class, "userId", userId);
			UpdateOperations<Authentication> ops = ds.createUpdateOperations(Authentication.class);
			ops.set("realName", realName);
			ops.set("cardId", idcard);
			ops.set("areaInfo", areaInfo);
			UpdateResults<Authentication> ur = dao.update(q, ops);
			if (ur.getHadError()) {
				WriteError("设置失败！");
				return;
			}
			Datastore ds2 = userDao.getDatastore();
			Query<User> q2 = ds2.find(User.class, "_id", userId);
			UpdateOperations<User> ops2 = ds2.createUpdateOperations(User.class);
			ops2.set("realName", realName);
			UpdateResults<User> ur2 = userDao.update(q2, ops2);
			if (!ur2.getHadError()) {
				WriteRight("设置成功！");
			} else {
				WriteError("设置失败！");
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
			WriteError("设置失败！");
		}
	}
	
	private static void printEntity(Object obj, Class clzz){
		/*try {
			Method[] methods = clzz.getMethods();
			for(int i=0; i<methods.length; i++){
				Method method = methods[i];
				if(method.getName().startsWith("get") && method.getParameterTypes().length==0){
					log.info("变量名：" + method.getName() + "\t值：" + method.invoke(obj, null));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.toString(), e);
		}*/
	}

	@Page(des = "进入修改页面")
	public void preEdit() {
		String userId = param("userId");
		Authentication auth = auDao.getByField("userId", userId);
		setAttr("auth", auth);
		if(auth.getType() == AuditType.corporate.getKey()){
			forward("/admins/user/authen/enterpriseAuth.jsp");
		}else{
			CountryDao cDao = new CountryDao();
			setAttr("country", cDao.findAll());
			forward("/admins/user/authen/individualAuth.jsp");
		}
	}

	@Page(Viewer = JSON, des = "修改个人用户资料")
	public void individualSave() {
		try {
			String realName = param("realName");
			int area = intParam("area");
			String country = param("country");
			String cardId = param("cardId").toLowerCase();
			if (null == realName || "".equals(realName)) {
				json(L("请填写真实姓名"), true, "");
				return;
			}
			if (null == cardId || "".equals(cardId)) {
				json(L("请填写有效的身份证号码"), true, "");
				return;
			}
			boolean isPass = false, isDalu = true;
			if (area == AreaInfo.dalu.getKey()) {
				isPass = idcardDao.validIdcard(realName, cardId);
			} else {
				isPass = true;
				isDalu = false;
			}
			if (!isPass && isDalu) {
				json(L("证件信息验证不通过，请填写真实证件信息后重新提交认证"), false, "");
				return;
			}

			Query q1 = auDao.getQuery().filter("cardId", cardId).filter("userId !=", userIdStr());
			long count = q1.countAll();
			if (count >= Const.MAX_AUTH_AMOUNT) {
				json(L("您的证件申请实名认证次数超过限制，无法通过"), false, "");
				return;
			}

			area = AuthUtil.getArea(country);

			String userId = param("userId");
			Authentication au = auDao.getByField("userId", userId);
			au.setRealName(realName);
			au.setCardId(cardId);
			au.setAreaInfo(area);
			au.setCountryCode(country);

			if (!auDao.updateAuth(au).getHadError()) {
				json(L("保存成功。"), true, "");
				userDao.updateRealNameAndAuthType(userId, realName, AuditType.individual.getKey(), false);
			} else {
				json(L("保存失败。"), false, "");
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
			json(L("保存失败，请稍后重试"), false, "");
		}
	}

	@Page(Viewer = JSON, des = "修改企业人用户资料")
	public void enterpriseSave() {
		try {
			String userId = param("userId");
			String realName = param("realName");
			String legalPersonName = param("legalPersonName");
			String enterpriseRegisterNo = param("enterpriseRegisterNo");
			String organizationCode = param("organizationCode");
			Timestamp enterpriseRegisterDate = (Timestamp)param("enterpriseRegisterDate", ReqParamType.TIMESTAMP);
			String enterpriseRegisterAddr = param("enterpriseRegisterAddr");

			Authentication au = auDao.getByField("userId", userId);
			au.setRealName(realName);
			au.setLegalPersonName(legalPersonName);
			au.setEnterpriseRegisterNo(enterpriseRegisterNo);
			au.setOrganizationCode(organizationCode);
			au.setEnterpriseRegisterDate(enterpriseRegisterDate);
			au.setEnterpriseRegisterAddr(enterpriseRegisterAddr);

			if (!auDao.updateAuth(au).getHadError()) {
				json(L("保存成功。"), true, "");
				userDao.updateRealNameAndAuthType(userId, au.getRealName(), AuditType.corporate.getKey(), false);
			} else {
				json(L("保存失败。"), false, "");
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
			json(L("保存失败，请稍后重试"), false, "");
		}
	}
	
	@Page(Viewer = "/admins/user/authen/history.jsp")
	public void history() {
		String userId = param("userId");
		ahDao.insertOne(userId);
		Query q = ahDao.getQuery();
		q.order("-submitTime");
		q.filter("userId", userId);
		List<AuthenHistory> dataList = q.asList();
		if (null != dataList && dataList.size() > 0) {
			List<String> adminIds = new ArrayList<String>();
			Set<String> userIds = new HashSet<String>();
			for(AuthenHistory au : dataList){
				if(au.getAdminId() != null && au.getAdminId().length() > 0)
					adminIds.add(au.getAdminId());
				userIds.add(au.getUserId());
			}
			
			AdminUserDao auDao = new AdminUserDao();
			if(adminIds.size() > 0){
				Map<String , AdminUser> users = auDao.getUserMapByIds(adminIds);
				for(AuthenHistory au : dataList){
					au.setaUser(users.get(au.getAdminId()));
				}
			}
			
			UserDao uDao = new UserDao();
			Map<String, User> users = uDao.getUserMapByIds(userIds);
			for(AuthenHistory au : dataList){
				au.setUser(users.get(au.getUserId()));
			}
		}
		setAttr("dataList", dataList);
		setAttr("authTimes", dataList.size());
		setAttr("imagePrefix", FileConfig.getValue("imgDomain1")+"/picauth?type=1&file=");
	}
}


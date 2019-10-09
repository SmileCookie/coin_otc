package com.world.controller.manage.auth.pwd;

import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.google.code.morphia.query.UpdateResults;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.GoogleApiService;
import com.messi.user.feign.UserApiService;
import com.messi.user.util.ConstantCenter;
import com.world.cache.Cache;
import com.world.constant.Const;
import com.world.model.LimitType;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.user.EmailDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.authen.AuthenLogDao;
import com.world.model.entity.user.User;
import com.world.model.entity.user.authen.AuthenType;
import com.world.util.CommonUtil;
import com.world.util.Message;
import com.world.util.MsgToastKey;
import com.world.util.sign.RSACoder;
import com.world.util.string.EncryptionPhoto;
import com.world.web.Page;
import com.world.web.action.ApproveAction;
import com.world.web.response.DataResponse;
import com.world.web.sso.rsa.RsaLoginUtil;
import com.world.web.sso.rsa.RsaUser;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 保存登录密码和安全密码
 * @author Administrator
 *
 */
public class Index extends ApproveAction{
	static Logger logger = Logger.getLogger(Index.class.getName());
	UserDao userDao = new UserDao();
    /**
     * 密码最小长度
     */
	private static final int PASSWORD_MIN_LENGTH = 8;
	
	@Page
	public void index() {

	}
	
//	@Page(Viewer = "/cn/manage/index.jsp", des = "登录密码")
	public void log() {
		try {
			initLoginUser();
			boolean noPwd = false;
			if(loginUser.getPwdLevel() == 0 || loginUser.getPwd() == null || loginUser.getPwd().length() == 0){
				noPwd = true;
			}
			setAttr("noPwd", noPwd);
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

//	@Page(Viewer = "/cn/manage/index.jsp", des = "资金密码")
	public void safe() {
		try {
			initLoginUser();
			int userId =userId();
			User safeUser = userDao.getUserById(userId+"");
			request.setAttribute("hasSafePwd", safeUser.getIsSafePwd());
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	/**
	 * 配合前端改造
	 */
	@Page(Viewer = JSON)
	public void safeJson() {

		Map<String, Object> result = new HashMap<>();

		try {
			initLoginUserJson();
			int userId =userId();
			User safeUser = userDao.getUserById(userId+"");
			result.put("hasSafePwd", safeUser.getIsSafePwd());
			result.put("googleAuth", loginUser.getUserContact().getGoogleAu());
			result.put("mobileStatu", loginUser.getUserContact().getMobileStatu());

			json("ok", true, JSONObject.toJSONString(result), true);

		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	AuthenLogDao logDao = new AuthenLogDao();


	/**
	 * 修改登录密码验证
	 */
	@Page(Viewer = JSON)
	public void updLoginPwdCheck() {

		try {
			initLoginUser();
			String userId = userIdStr();

			//校验原登录密码
			DataResponse dr1 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr1.isSuc()) {
                json(L(dr1.getDes()), false, "");
				return;
			}
			//校验原登录密码
			DataResponse dr2 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr2.isSuc()) {
                json(L(dr2.getDes()), false, "");
				return;
			}
			//校验邮箱密码
			DataResponse dr3 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
			if (!dr3.isSuc()) {
                json(L(dr3.getDes()), false, "");
				return;
			}
			//校验手机验证码
			DataResponse dr4 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_MOBILE,MsgToastKey.LOCK_24_HOUR);
			if (!dr4.isSuc()) {
                json(L(dr4.getDes()), false, "");
				return;
			}
			//校验谷歌验证码
			DataResponse dr5 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_GOOGLE,MsgToastKey.LOCK_24_HOUR);
			if (!dr5.isSuc()) {
                json(L(dr5.getDes()), false, "");
				return;
			}
            //提示信息
            String msg = "";
			//原登录密码
			String loginPwd = param("password");
			//邮箱验证码
			String emailCode = param("emailcode");
			//谷歌验证码
			String googleCode = param("gcode");
			//手机验证码
			String smsCode = param("smscode");
			//安全验证：0-谷歌，1-手机
			String safeVerifyType = param("selectedCode");

//			if(StringUtils.isNotEmpty(googleCode)){
//				safeVerifyType = "0";
//			}
//			if(StringUtils.isNotEmpty(smsCode)){
//				safeVerifyType = "1";
//			}
			if(StringUtils.isBlank(loginPwd)){
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.loginPwd,L("请输入原登录密码。"));
			}else if(StringUtils.isBlank(emailCode)){
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.emailCode,L("请输入邮箱验证码。"));
			}else if("0".equals(safeVerifyType) && StringUtils.isBlank(googleCode)){
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.googleCode,L("请输入谷歌验证码。"));
			}else if("1".equals(safeVerifyType) && StringUtils.isBlank(smsCode)){
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode,L("请输入手机验证码。"));
			}
            if(StringUtils.isNotEmpty(msg)){
                json("", false, msg);
                return;
            }
			RsaUser rsaUser = RsaLoginUtil.getRsaUser(this);
			byte[] decodedData2 = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(loginPwd.replace(" ", "+")),rsaUser.getPriKey());
			loginPwd = new String(decodedData2);
			msg = doCheck(msg,userId, loginPwd, emailCode, googleCode,smsCode, safeVerifyType, true,MsgToastKey.UPD_LOGIN_PWD,MsgToastKey.LOCK_24_HOUR);
            if(StringUtils.isNotEmpty(msg)){
                json("", false, msg);
                return;
            }else{
				String token = EncryptionPhoto.getToken(Const.function_upd_login_pwd,userId);
				json(token, true, "");
            }
		} catch (Exception e) {
			log.error("10100303VIPXGLMJY【修改登录密码校验】 com.world.controller.manage.auth.pwd.index#updLoginPwdCheck",e);
            json(L("未知错误导致操作失败"), false, "");
		}

	}


	/**
	 * check登录密码或资金密码通用方法
	 * @param msg
	 * @param userId
	 * @param loginPwd
	 * @param emailCode
	 * @param googleCode
	 * @param safeVerifyType
	 * @param isUpdLoginPwd
	 */
	private String doCheck(String msg,String userId, String loginPwd, String emailCode, String googleCode,String smsCode, String safeVerifyType, boolean isUpdLoginPwd,String functionName,String lockTime) {
		//校验登录密码
		String uftLoginPwd = null;
		//校验谷歌
		String uftGoogle = null;
		//邮箱锁定校验
		LimitType ltEmail = null;
		//手机锁定校验
		LimitType ltMobile = null;
		//邮件typeCode
		PostCodeType pct = null;
		//短信typeCode
		PostCodeType pctMobile = null;
		//超过三次限制提示
		String googleMsgToast = "";
		String loginPwdMsgToast = "";
		if(isUpdLoginPwd){
			uftLoginPwd = String.valueOf(ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_PWD.getKey());
			uftGoogle = String.valueOf(ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_GOOGLE.getKey());
			ltEmail = LimitType.LoginEmailPassError;
			ltMobile = LimitType.LoginMobilePassError;
			pct = PostCodeType.updateLoginPassword;
			pctMobile = PostCodeType.updateLoginPasswordByMoblie;
			googleMsgToast = "验证码输入次数超出限制，将锁定修改登录密码功能，请24小时之后再试。";
			loginPwdMsgToast = "原登录密码输入次数超出限制，将锁定修改登录密码功能，请24小时之后再试。";
		}else{
			uftLoginPwd = String.valueOf(ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD.getKey());
			uftGoogle = String.valueOf(ConstantCenter.UpdFunctionType.UPD_PAY_PWD_GOOGLE.getKey());
			ltEmail = LimitType.PayEmailPassError;
			ltMobile = LimitType.PayMobilePassError;
			pct = PostCodeType.updateSafePassword;
			pctMobile = PostCodeType.updateSafePasswordByMobile;
			googleMsgToast = "验证码输入次数超出限制，将锁定重置资金密码及提现功能，请24小时之后再试。";
			loginPwdMsgToast = "登录密码输入次数超出限制，将锁定提现、修改登录密码、重置资金密码功能，请24小时之后再试。";
		}

		String url = ApiConfig.getValue("usecenter.url");
		FeignContainer container = new FeignContainer(url.concat("/user"));
		UserApiService userApiService = container.getFeignClient(UserApiService.class);
		Map<String,String> map = userApiService.checkLoginPwdApiN(userId,loginPwd,uftLoginPwd);
		User user = userDao.getById(userId);
		if(null != map){
			for(Map.Entry<String,String> entry : map.entrySet()){
				if("1".equals(entry.getKey())){

				}else{
					String returnVal = entry.getValue();
					if("-2".equals(returnVal)){
						returnVal = L(loginPwdMsgToast);
                        EmailDao eDao = new EmailDao();
                        String info = "";
                        String title = "";
                        if (isUpdLoginPwd) {
                            //修改登录密码时被锁定发送邮件
                            info = eDao.getWrongLimitEmailHtml(user, "您在操作修改登录密码功能时，原登录密码输入次数超出限制，为了您的账号安全，XX将锁定修改登录密码功能，请24小时之后再试。", this);
                            title = L("锁定修改登录密码");
                        } else {
                            //重置资金密码时被锁定发送邮件
                            info = eDao.getWrongLimitEmailHtml(user, "您在操作重置资金密码功能时，原登录密码输入次数超出限制，为了您的账号安全，XX将锁定重置资金密码、修改登录密码和提现功能，请24小时之后再试。", this);
							title = L("锁定重置资金密码");
						}
                        //锁定发送邮件
                        eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, user.getUserContact().getSafeEmail());
                    }else{
                        if(!isUpdLoginPwd){
                            returnVal = returnVal.replace("原登录密码", "登录密码");
                        }
					    String[] errorMsg = returnVal.split("#");
						if(errorMsg.length == 2){
							returnVal = L(errorMsg[0], errorMsg[1]);
						}else{
							returnVal = L(errorMsg[0]);
						}
                    }
					msg = CommonUtil.mapToJsonStr(msg,isUpdLoginPwd ? MsgToastKey.loginPwd : MsgToastKey.lpwd, returnVal);
				}
			}
		}
		String cLan = lan;
		//邮箱验证
		DataResponse dr = getDataResponse(user,pct,user.getEmail(),1,emailCode,ltEmail,functionName,lockTime);
		if (!dr.isSuc()) {
			msg = CommonUtil.mapToJsonStr(msg,MsgToastKey.emailCode,dr.getDes());
		}
		if(!"-1".equals(safeVerifyType)) {
			//谷歌校验
			if("0".equals(safeVerifyType)){
				FeignContainer container1 = new FeignContainer(url.concat("/google"));
				GoogleApiService googleApiService = container1.getFeignClient(GoogleApiService.class);
				Map<String,String> map1 = googleApiService.checkGoogleCodeApiN(googleCode,user.getUserContact().getSecret(),userId,uftGoogle);
				for(Map.Entry<String,String> entry1 : map1.entrySet()){
					if("1".equals(entry1.getKey())){
						//TODO 放下一步校验session
					}else{
						String returnVal = entry1.getValue();
						if("-2".equals(returnVal)){
							returnVal = L(googleMsgToast);
						}else{
                            returnVal = returnVal.replace("谷歌","");
                            String[] errorMsg = returnVal.split("#");
							if(errorMsg.length == 2){
								returnVal = L(errorMsg[0], errorMsg[1]);
							}else{
								returnVal = L(errorMsg[0]);
							}
						}
						msg = CommonUtil.mapToJsonStr(msg,MsgToastKey.googleCode,returnVal);
					}
				}
			}else if("1".equals(safeVerifyType)){
				//手机验证

				DataResponse dr1 = getDataResponse(user,pctMobile,user.getUserContact().getSafeMobile(),2,smsCode,ltMobile,functionName,lockTime);
				if (!dr1.isSuc()) {
					msg = CommonUtil.mapToJsonStr(msg,MsgToastKey.smsCode,dr1.getDes());
				}else{
					//TODO 放下一步校验session
				}
			}
		}
		return msg;
	}

	/**
	 * 修改登录密码，先测原始密码是否正确，则修改密码，否则返回false
	 */
	@Page(Viewer = JSON)
	public void logUpdate() {
		try {
			initLoginUser();
			String userId = userIdStr();

			//校验原登录密码
			DataResponse dr1 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr1.isSuc()) {
                json(L(dr1.getDes()), false, "");
				return;
			}
			//校验原登录密码
			DataResponse dr2 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr2.isSuc()) {
                json(L(dr2.getDes()), false, "");
				return;
			}
			//校验邮箱密码
			DataResponse dr3 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
			if (!dr3.isSuc()) {
                json(L(dr3.getDes()), false, "");
				return;
			}
			//校验手机验证码
			DataResponse dr4 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_MOBILE,MsgToastKey.LOCK_24_HOUR);
			if (!dr4.isSuc()) {
                json(L(dr4.getDes()), false, "");
				return;
			}
			//校验谷歌验证码
			DataResponse dr5 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_GOOGLE,MsgToastKey.LOCK_24_HOUR);
			if (!dr5.isSuc()) {
                json(L(dr5.getDes()), false, "");
				return;
			}

			log.info("用户："+userName()+"修改登录密码IP:"+ip());
//			if(!emailNoSet()){
//				WriteError(L("您还没有邮箱认证，请先进行邮箱认证。"));
//				return;
//			}
			boolean noPwd = false;
			if(loginUser.getPwdLevel() == 0 || loginUser.getPwd() == null || loginUser.getPwd().length() == 0){
				noPwd = true;
			}
            //提示信息
            String msg = "";
			//修改后密码
			String password = param("password");
			//确认密码
			String surePwd = param("confirmPwd");

			RsaUser rsaUser = RsaLoginUtil.getRsaUser(this);
			byte[] decodedData = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(surePwd.replace(" ", "+")),rsaUser.getPriKey());
			surePwd = new String(decodedData);

			byte[] decodedData2 = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(password.replace(" ", "+")),rsaUser.getPriKey());
			password = new String(decodedData2);

			if (!password.equals(surePwd)) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.loginPwd,L("两次密码输入不一致。"));
			}

			if (password.length() < 8) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.loginPwd,L("登录密码的长度不能少于8位。"));
			}

            //密码验证
            String regex = "^(?![A-Za-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{8,20}$";    //密码的组成至少要包括大小写字母、数字及标点符号的其中两项
            if (!password.matches(regex)) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.loginPwd, L("您的密码需要8-20位，包含字母，数字，符号的两种以上。"));
            }

			User user = userDao.getById(userId);

			LimitType lt = LimitType.SafePassError;


			if(!noPwd){
				if(lt.GetStatus(userId) == -1){
                    msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.loginPwd,L("密码已锁定"));
				}

				String oldPwd = user.getPwd();
				if(oldPwd.equals(user.getEncryptedPwd(password))){
                    msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.loginPwd,L("修改后的密码不能和原密码一致。"));
				}
			}

			//modify by xwz 20171220 资金密码不能和登录密码一致
			if(user.getEncryptedPwd(password).equals(user.getSafePwd())){
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.loginPwd,L("登录密码不得与资金密码一致。"));
			}
            if(StringUtils.isNotEmpty(msg)){
                json("", false, msg);
                return;
            }
			//防跳步
			String token = param("token");
			boolean flg = EncryptionPhoto.checkToken(Const.function_upd_login_pwd,userId,token);
			if(!flg){
				json(L("非法操作"), false, "");
				return;
			}
			int pwdLevel = intParam("pwdLevel");

			UpdateResults<User> ur = userDao.updatePwd(userId, password, pwdLevel);

			if (!ur.getHadError()) {
				Cache.Delete(Const.function_upd_login_pwd + userId);
				lt.ClearStatus(userId);
				json(L("修改成功"), true, "");
				logDao.insertOneRecord(AuthenType.modifyPwd.getKey(), userId()+"", "0", "成功修改登录密码。", ip());
			} else {
				json(L("修改失败"), false, "");
			}
		} catch (Exception e) {
			log.error("10100303VIPXGLM【修改登录密码】 com.world.controller.manage.auth.pwd.index#logUpdate",e);
			json(L("未知错误导致操作失败"), false, "");
		}
	}

	/**
	 * 先测原始密码是否正确，则修改密码，否则返回false
	 */
	@Page(Viewer = JSON)
	public void logBfUpdate() {

		try {
			String userName = param("email");
			log.info("用户："+userName()+"修改登录密码IP:"+ip());
//			if(!emailNoSet()){
//				WriteError(L("您还没有邮箱认证，请先进行邮箱认证。"));
//				return;
//			}
			String password = param("password");
			String confirmPwd = param("confirmPwd");
			User user = userDao.getUserByColumn(userName, "email");

			RsaUser rsaUser = RsaLoginUtil.getRsaUser(this);
			byte[] decodedData = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(confirmPwd.replace(" ", "+")),rsaUser.getPriKey());
			confirmPwd = new String(decodedData);

			byte[] decodedData2 = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(password.replace(" ", "+")),rsaUser.getPriKey());
			password = new String(decodedData2);

				String checkPwd = user.getEncryptedPwd(password);
				if(checkPwd.equals(user.getEncryptedPwd(user.getPwd()))){
				json(L("修改后的密码不能和原密码一致。"), false, "");
				return;
			}
			//modify by xwz 20171220 资金密码不能和登录密码一致
			if(user.getEncryptedPwd(password).equals(user.getSafePwd())){
				json(L("账户登录密码应避免与资金密码一致。"), false, "");
				return;
			}
			int pwdLevel = intParam("pwdLevel");
			UpdateResults<User> ur = userDao.updatePwd(user.get_Id(), password, pwdLevel);
			if (!ur.getHadError()) {
				json(L("操作成功"), true, "");
				logDao.insertOneRecord(AuthenType.modifyPwd.getKey(), userId()+"", "0", "成功修改登录密码。", ip());
			} else {
				json(L("操作失败"), false, "");
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			json(L("未知错误导致操作失败"), false, "");
		}
	}

	/**
	 * 修改资金密码验证，返回跳步的token
	 */
	@Page(Viewer = JSON)
	public void updPayPwdCheck() {
		try {
			String pwdParamKey = "lpwd";
			initLoginUser();
			String userId = userIdStr();
			//提示信息
			String msg = "";
			LimitType lt = LimitType.SafePassEntrustError;
			int entrustStatus = lt.GetStatus(loginUser.get_Id());
			if (entrustStatus == -1) {
				json(L("资金密码输入错误超出限制，锁定该帐户24小时，不得使用提现功能"), false, "");
				return;
			}
			//校验提现资金密码
			DataResponse dr1 = this.checkVerifiCode(userId,MsgToastKey.UPD_PAY_PWD,ConstantCenter.UpdFunctionType.WITHDRAWAL_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr1.isSuc()) {
                json(L(dr1.getDes()), false, "");
				return;
			}
			//校验原登录密码
			DataResponse dr2 = this.checkVerifiCode(userId,MsgToastKey.UPD_PAY_PWD,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr2.isSuc()) {
                json(L(dr2.getDes()), false, "");
				return;
			}
			//校验邮箱密码
			DataResponse dr3 = this.checkVerifiCode(userId,MsgToastKey.UPD_PAY_PWD,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
			if (!dr3.isSuc()) {
                json(L(dr3.getDes()), false, "");
				return;
			}
			//校验手机验证码
			DataResponse dr4 = this.checkVerifiCode(userId,MsgToastKey.UPD_PAY_PWD,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_MOBILE,MsgToastKey.LOCK_24_HOUR);
			if (!dr4.isSuc()) {
                json(L(dr4.getDes()), false, "");
				return;
			}
			//校验谷歌验证码
			DataResponse dr5 = this.checkVerifiCode(userId,MsgToastKey.UPD_PAY_PWD,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_GOOGLE,MsgToastKey.LOCK_24_HOUR);
			if (!dr5.isSuc()) {
                json(L(dr5.getDes()), false, "");
				return;
			}
			//otc发布广告资金密码输错
			DataResponse dr6 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.UPD_PAY_PWD,ConstantCenter.UpdFunctionType.OTC_CAD_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr6.isSuc()) {
				Message mesg = new Message();
				mesg.setMsg(lan, dr6.getDes());
				json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
				return;
			}
			//otc发布广告资金密码输错
			DataResponse dr7 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.UPD_PAY_PWD,ConstantCenter.UpdFunctionType.OTC_RELEASECOIN_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr7.isSuc()) {
				Message mesg = new Message();
				mesg.setMsg(lan, dr7.getDes());
				json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
				return;
			}
			// 验证交易密码是否锁定
			if (checkSafePwdLock(loginUser)) {
				json(L("为了您的帐户安全，使用重置资金密码功能修改资金密码后将锁定24小时，在此期间不能进行提现、修改密码等操作，可进行交易操作，请等待24个小时后自动解锁。"), false, "");
				return;
			}

			//原登录密码
			String loginPwd = param(pwdParamKey);
			//邮箱验证码
			String emailCode = param("emailcode");
			//谷歌验证码
			String googleCode = param("gcode");
			//手机验证码
			String smsCode = param("smscode");
			//安全验证：0-谷歌，1-手机
			String safeVerifyType = param("selectedCode");

//			if(StringUtils.isNotEmpty(googleCode)){
//				safeVerifyType = "0";
//			}
//			if(StringUtils.isNotEmpty(smsCode)){
//				safeVerifyType = "1";
//			}
			if(StringUtils.isBlank(loginPwd)){
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.lpwd,L("请输入原登录密码。"));
			}
			if(StringUtils.isBlank(emailCode)){
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.emailCode,L("请输入邮箱验证码。"));
			}
			if("0".equals(safeVerifyType) && StringUtils.isBlank(googleCode)){
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.googleCode,L("请输入谷歌验证码。"));
			}
			if("1".equals(safeVerifyType) && StringUtils.isBlank(smsCode)){
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode,L("请输入手机验证码。"));
			}
            if(StringUtils.isNotEmpty(msg)){
                json("", false, msg);
                return;
            }

			RsaUser rsaUser = RsaLoginUtil.getRsaUser(this);
			loginPwd = this.rsaDecrypt(loginPwd, rsaUser.getPriKey());

            msg = doCheck(msg,userId, loginPwd, emailCode, googleCode,smsCode, safeVerifyType, false,MsgToastKey.RESET_PAY_PWD,MsgToastKey.LOCK_24_HOUR);
            if(StringUtils.isNotEmpty(msg)){
                json("", false, msg);
                return;
            }else{
				String token = EncryptionPhoto.getToken(Const.function_upd_pay_pwd,userId);
				json(token, true, "");
            }
		} catch (Exception e) {
			log.error("10100303VIPXGPMJY【修改资金密码校验】 com.world.controller.manage.auth.pwd#updPayPwdCheck",e);
		}
	}

	//设置资金密码，重置资金密码，用的一个接口
	@Page(Viewer = ".xml")
	public void safeUpdate() {
		try {
			log.info("用户："+userName()+"修改资金安全密码IP:"+ip());
			String userId = userIdStr();
			User user = userDao.getById(userId);
			boolean hasSafePwd = user.getIsSafePwd();


			String functionName = MsgToastKey.SETTING_PAY_PWD;
			if(hasSafePwd){
			    functionName = MsgToastKey.UPD_PAY_PWD;
            }

			//校验提现资金密码
			DataResponse dr0 = this.checkVerifiCode(userId,functionName,ConstantCenter.UpdFunctionType.WITHDRAWAL_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr0.isSuc()) {
                json(L(dr0.getDes()), false, "");
				return;
			}

			//校验设置资金密码登录密码
			DataResponse dr1 = this.checkVerifiCode(userId,functionName,ConstantCenter.UpdFunctionType.SET_USER_INFO_LOGINPWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr1.isSuc()) {
                json(L(dr1.getDes()), false, "");
				return;
			}
			//校验原登录密码
			DataResponse dr2 = this.checkVerifiCode(userId,functionName,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr2.isSuc()) {
                json(L(dr2.getDes()), false, "");
				return;
			}
			//校验邮箱密码
			DataResponse dr3 = this.checkVerifiCode(userId,functionName,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
			if (!dr3.isSuc()) {
                json(L(dr3.getDes()), false, "");
				return;
			}
			//校验手机验证码
			DataResponse dr4 = this.checkVerifiCode(userId,functionName,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_MOBILE,MsgToastKey.LOCK_24_HOUR);
			if (!dr4.isSuc()) {
                json(L(dr4.getDes()), false, "");
				return;
			}
			//校验谷歌验证码
			DataResponse dr5 = this.checkVerifiCode(userId,functionName,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_GOOGLE,MsgToastKey.LOCK_24_HOUR);
			if (!dr5.isSuc()) {
                json(L(dr5.getDes()), false, "");
				return;
			}

			//18-设置资金密码，21-修改资金密码
			String type = param("type");
			//资金密码
			String password = param("password");
			//确认密码
			String confirmPwd = param("confirmPwd");
			//登录密码
			String lpwd = param("lpwd");
			// RSA解密
			RsaUser rsaUser = RsaLoginUtil.getRsaUser(this);
			password = this.rsaDecrypt(password, rsaUser.getPriKey());
			confirmPwd = this.rsaDecrypt(confirmPwd, rsaUser.getPriKey());
            //提示信息
            String msg = "";
			//密码验证
			//密码的组成至少要包括大小写字母、数字及标点符号的其中两项
			String regex = "^(?![A-Za-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{8,20}$" ;
			if(!password.matches(regex)){
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.loginPwd,L("您的密码需要8-20位，包含字母，数字，符号的两种以上。"));
			}
			if(!hasSafePwd){
				lpwd = this.rsaDecrypt(lpwd, rsaUser.getPriKey());
				//校验登录密码
				String uftLoginPwd = String.valueOf(ConstantCenter.UpdFunctionType.SET_USER_INFO_LOGINPWD.getKey());
				String url = ApiConfig.getValue("usecenter.url");
				FeignContainer container = new FeignContainer(url.concat("/user"));
				UserApiService userApiService = container.getFeignClient(UserApiService.class);
				Map<String,String> map = userApiService.checkLoginPwdApiN(userId,lpwd,uftLoginPwd);
				if(null != map) {
					if (map.containsKey("0")) {
                        String returnVal = map.get("0");
                        if("-2".equals(returnVal)){
                            returnVal = L("登录密码输入次数超出限制，将锁定设置资金密码功能，请等待24小时后自动解锁。");
							EmailDao eDao = new EmailDao();
							//设置资金密码时被锁定发送邮件
							String info = eDao.getWrongLimitEmailHtml(user, "您在操作设置资金密码功能时，原登录密码输入次数超出限制，为了您的账号安全，XX将锁定设置资金密码和提现功能，请24小时之后再试。", this);
							String title = L("设置资金密码");
							//锁定发送邮件
							eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, user.getUserContact().getSafeEmail());
                        }else{
                            returnVal = returnVal.replace("原登录密码", "登录密码");
                            String[] errorMsg = returnVal.split("#");
							if(errorMsg.length == 2){
								returnVal = L(errorMsg[0], errorMsg[1]);
							}else{
								returnVal = L(errorMsg[0]);
							}
                        }
                        msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.lpwd,returnVal);
					}
				}
			}
            if (StringUtils.isBlank(password)) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.loginPwd,L("请输入新资金密码。"));
            }

            if (StringUtils.isBlank(password)) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.loginPwd,L("请输入确认密码。"));
            }

			if (confirmPwd.length() < PASSWORD_MIN_LENGTH) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.loginPwd,L("资金安全密码的长度不能少于8位。"));
			}

			if (!password.equals(confirmPwd)) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.loginPwd,L("您的两次密码输入不一致。"));
            }

			// 验证交易密码是否锁定
            if (checkSafePwdLock(user)) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.loginPwd,L("为了您的帐户安全，使用重置资金密码功能修改资金密码后将锁定24小时，在此期间不能进行提现、修改密码等操作，可进行交易操作，请等待24个小时后自动解锁。"));
			}

			//modify by xwz 20171220 资金密码不能和登录密码一致
			if(user.getEncryptedPwd(password).equals(user.getPwd())){
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.loginPwd,L("资金密码不得与登录密码一致。"));
			}

//			if(user.getEncryptedPwd(password).equals(user.getSafePwd())){
//                this.postErrMsg(MsgToastKey.loginPwd, "修改后的密码不能和原密码一致。");
//                return;
//			}
            if(StringUtils.isNotEmpty(msg)){
                json("", false, msg);
                return;
            }
			//防跳步
			if(hasSafePwd){
				String token = param("token");
				boolean flg = EncryptionPhoto.checkToken(Const.function_upd_pay_pwd,userId,token);
				if(!flg){
					json(L("非法操作"), false, "");
					return;
				}
			}
			int safeLevel = intParam("safeLevel");
			UpdateResults<User> ur = userDao.updateSecurityPwd(userId, password, safeLevel);


            if (!ur.getHadError()) {
				AuthenLogDao logDao = new AuthenLogDao();
				logDao.insertOneRecord(AuthenType.modifySecurityPwd.getKey(), userId()+"", "0", "成功"+(hasSafePwd?"修改":"设置")+"资金安全密码。", ip());
				
				try {
					if (!hasSafePwd) {
						/*start by xzhang 20171104 设置资金密码后，交易验证设置自动设置为“每次交易均验证资金密码*/
						userDao.switchTradeAuthen(userDao.getById(userId),3, ip(),request);
						/*end*/
					} else {
						//删除防跳步操作
						Cache.Delete(Const.function_upd_pay_pwd + userId);
//						MobileDao mDao = new MobileDao();
//						PostCodeType postCodeType = PostCodeType.resetSafePwd;
//						if (null != user.getUserContact().getSafeMobile()) {
//							/*start by xzhang 20171031 短信服务临时解决方法，除+86外全发英文*/
//							String title = L(postCodeType.getValue());
//							String content = L(postCodeType.getDes());
//							if(!MsgUtil.isContain(user.getUserContact().getSafeMobile())){
//								title = Lan.Language("en", postCodeType.getValue());
//								content = Lan.Language("en", postCodeType.getDes());
//							}
//							/*end*/
//							mDao.sendSms(user, ip(), title, content, user.getUserContact().getSafeMobile());
//						}
					}
				} catch (Exception e) {
					log.error("内部异常", e);
				}
				if(hasSafePwd){
                    json(L("重置成功"), true, "");
                }else{
                    json(L("设置成功"), true, "");
                }
			} else {
                json(L("操作失败"), false, "");
			}
		} catch (Exception e) {
			log.error("10100303VIPSZPM【设置资金密码/修改资金密码】 com.world.controller.manage.auth.pwd.index#safeUpdate",e);
			WriteError(L("未知错误导致操作失败"));
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
     * RSA解密
     * @param cipher 密文
     * @param privateKey 紫瑶
     * @return
     * @throws Exception
     */
    private String rsaDecrypt(String cipher, String privateKey) throws Exception {
        byte[] bytes =  RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(cipher.replace(" ", "+")),privateKey);
        return new String(bytes);
    }


	/**
	 * 点击修改对登录密码进行校验
	 */
	@Page(Viewer = JSON)
	public void updVerify(){
		//0:登录密码-修改；1：资金密码-设置及修改；2：谷歌验证-开启及关闭；3：手机验证-开启及关闭；4：录入及修改手机，5：安全设置
		String opt = param("opt");
		initLoginUser();
		String userId = userIdStr();
		User user = userDao.getById(userId);
		if("0".equals(opt)){
			//校验原登录密码
			DataResponse dr1 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr1.isSuc()) {
				json(L(dr1.getDes()), false, "");
				return;
			}
			//校验原登录密码
			DataResponse dr2 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr2.isSuc()) {
				json(L(dr2.getDes()), false, "");
				return;
			}
			//校验邮箱密码
			DataResponse dr3 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
			if (!dr3.isSuc()) {
				json(L(dr3.getDes()), false, "");
				return;
			}
			//校验手机验证码
			DataResponse dr4 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_MOBILE,MsgToastKey.LOCK_24_HOUR);
			if (!dr4.isSuc()) {
				json(L(dr4.getDes()), false, "");
				return;
			}
			//校验谷歌验证码
			DataResponse dr5 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_GOOGLE,MsgToastKey.LOCK_24_HOUR);
			if (!dr5.isSuc()) {
				json(L(dr5.getDes()), false, "");
				return;
			}
			json(L("通过"), true, "");
			return;
		}else if("1".equals(opt)){
			// 验证交易密码是否锁定
			if (checkSafePwdLock(user)) {
				json(L("为了您的帐户安全，使用重置资金密码功能修改资金密码后将锁定24小时，在此期间不能进行提现、修改密码等操作，可进行交易操作，请等待24个小时后自动解锁。"), false, "");
				return;
			}
			if(!user.getIsSafePwd()){
				//校验设置资金密码登录密码
				DataResponse dr1 = this.checkVerifiCode(userId,MsgToastKey.SETTING_PAY_PWD,ConstantCenter.UpdFunctionType.SET_USER_INFO_LOGINPWD, MsgToastKey.LOCK_24_HOUR);
				if (!dr1.isSuc()) {
					json(L(dr1.getDes()), false, "");
					return;
				}
			}else{
				//校验提现资金密码
				DataResponse dr1 = this.checkVerifiCode(userId, MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.WITHDRAWAL_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
				if (!dr1.isSuc()) {
					json(L(dr1.getDes()), false, "");
					return;
				}
				//校验原登录密码
				DataResponse dr2 = this.checkVerifiCode(userId, MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
				if (!dr2.isSuc()) {
					json(L(dr2.getDes()), false, "");
					return;
				}
				//校验邮箱密码
				DataResponse dr3 = this.checkVerifiCode(userId, MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
				if (!dr3.isSuc()) {
					json(L(dr3.getDes()), false, "");
					return;
				}
				//校验手机验证码
				DataResponse dr4 = this.checkVerifiCode(userId, MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_MOBILE, MsgToastKey.LOCK_24_HOUR);
				if (!dr4.isSuc()) {
					json(L(dr4.getDes()), false, "");
					return;
				}
				//校验谷歌验证码
				DataResponse dr5 = this.checkVerifiCode(userId, MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_GOOGLE, MsgToastKey.LOCK_24_HOUR);
				if (!dr5.isSuc()) {
					json(L(dr5.getDes()), false, "");
					return;
				}
				//校验谷歌验证码
				DataResponse dr6 = this.checkVerifiCode(userId, MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.OTC_RELEASECOIN_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
				if (!dr6.isSuc()) {
					json(L(dr6.getDes()), false, "");
					return;
				}
				//校验谷歌验证码
				DataResponse dr7 = this.checkVerifiCode(userId, MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.OTC_CAD_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
				if (!dr7.isSuc()) {
					json(L(dr7.getDes()), false, "");
					return;
				}
			}
			json(L("通过"), true, "");
			return;
		}else if("2".equals(opt)){
			if(!user.getGoogleOpen()){
				//check谷歌
				DataResponse mobileDr = this.checkVerifiCode(userId,MsgToastKey.OPEN_GOOGLE_VERIFY, ConstantCenter.UpdFunctionType.OPEN_GOOGLE_VERIFY, MsgToastKey.LOCK_24_HOUR);
				if (!mobileDr.isSuc()) {
					json(L(mobileDr.getDes()), false, "");
					return;
				}
			}else{
				//check邮箱
				DataResponse emailDr = this.checkVerifiCode(userId,MsgToastKey.CLOSE_GOOGLE_VERIFY, ConstantCenter.UpdFunctionType.CLOSE_GOOGLE_VERIFY_EMAIL, MsgToastKey.LOCK_24_HOUR);
				if (!emailDr.isSuc()) {
					json(L(emailDr.getDes()), false, "");
					return;
				}
				//check谷歌
				DataResponse googleDr = this.checkVerifiCode(userId,MsgToastKey.CLOSE_GOOGLE_VERIFY, ConstantCenter.UpdFunctionType.CLOSE_GOOGLE_VERIFY_GOOGLE, MsgToastKey.LOCK_24_HOUR);
				if (!googleDr.isSuc()) {
					json(L(googleDr.getDes()), false, "");
					return;
				}
			}
			json(L("通过"), true, "");
			return;
		}else if("3".equals(opt)){

			String msgToast = "";
			if(!user.getSmsOpen()){
				msgToast = MsgToastKey.OPEN_MOBILE_VERIFY;
				//check手机
				DataResponse mobileDr = this.checkVerifiCode(userId,MsgToastKey.OPEN_MOBILE_VERIFY, ConstantCenter.UpdFunctionType.OPEN_MOBILE_VERIFY, MsgToastKey.LOCK_24_HOUR);
				if (!mobileDr.isSuc()) {
					json(L(mobileDr.getDes()), false, "");
					return;
				}
			}else{
				msgToast = MsgToastKey.CLOSE_MOBILE_VERIFY;
				//check邮箱
				DataResponse emailDr = this.checkVerifiCode(userId,MsgToastKey.CLOSE_MOBILE_VERIFY, ConstantCenter.UpdFunctionType.CLOSE_MOBILE_VERIFY_EMAIL, MsgToastKey.LOCK_24_HOUR);
				if (!emailDr.isSuc()) {
					json(L(emailDr.getDes()), false, "");
					return;
				}
				//check手机
				DataResponse mobileDr = this.checkVerifiCode(userId,MsgToastKey.CLOSE_MOBILE_VERIFY, ConstantCenter.UpdFunctionType.CLOSE_MOBILE_VERIFY_MOBILE, MsgToastKey.LOCK_24_HOUR);
				if (!mobileDr.isSuc()) {
					json(L(mobileDr.getDes()), false, "");
					return;
				}
			}
			//设置手机
			DataResponse setMoblieDr = this.checkVerifiCode(userId,msgToast, ConstantCenter.UpdFunctionType.SET_USER_INFO_MOBILE, MsgToastKey.LOCK_24_HOUR);
			if (!setMoblieDr.isSuc()) {
				json(L(setMoblieDr.getDes()), false, "");
				return;
			}
			//check原手机
			DataResponse oldMobileDr = this.checkVerifiCode(userId,msgToast, ConstantCenter.UpdFunctionType.UPD_USER_OLD_MOBILE, MsgToastKey.LOCK_24_HOUR);
			if (!oldMobileDr.isSuc()) {
				json(L(oldMobileDr.getDes()), false, "");
				return;
			}
			//check手机
			DataResponse newMobileDr = this.checkVerifiCode(userId,msgToast, ConstantCenter.UpdFunctionType.UPD_USER_MOBILE, MsgToastKey.LOCK_24_HOUR);
			if (!newMobileDr.isSuc()) {
				json(L(newMobileDr.getDes()), false, "");
				return;
			}
			json(L("通过"), true, "");
			return;
		}else if("4".equals(opt)){
			if(StringUtils.isEmpty(user.getUserContact().getSafeMobile())){
				//check录入手机
				DataResponse dr = this.checkVerifiCode(userId,MsgToastKey.SETTING_MOBILE, ConstantCenter.UpdFunctionType.SET_USER_INFO_MOBILE, MsgToastKey.LOCK_24_HOUR);
				if (!dr.isSuc()) {
					json(L(dr.getDes()), false, "");
					return;
				}
			}else{
				//check原手机
				DataResponse oldMobileDr = this.checkVerifiCode(userId,MsgToastKey.UPD_MOBILE, ConstantCenter.UpdFunctionType.UPD_USER_OLD_MOBILE, MsgToastKey.LOCK_24_HOUR);
				if (!oldMobileDr.isSuc()) {
					json(L(oldMobileDr.getDes()), false, "");
					return;
				}
				//check手机
				DataResponse newMobileDr = this.checkVerifiCode(userId,MsgToastKey.UPD_MOBILE, ConstantCenter.UpdFunctionType.UPD_USER_MOBILE, MsgToastKey.LOCK_24_HOUR);
				if (!newMobileDr.isSuc()) {
					json(L(newMobileDr.getDes()), false, "");
					return;
				}
			}
			json(L("通过"), true, "");
			return;
		}else{
			//校验资金密码
			DataResponse dr0 = this.checkVerifiCode(userId,MsgToastKey.TRANSACTION_PAY_PWD,ConstantCenter.UpdFunctionType.TRANSACTION_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr0.isSuc()) {
				json(L(dr0.getDes()), false, "", true);
				return;
			}
			json(L("通过"), true, "");
			return;
		}

	}

}

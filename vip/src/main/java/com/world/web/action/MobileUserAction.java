package com.world.web.action;

import com.api.config.ApiConfig;
import com.googleauth.GoogleAuthenticator;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.GoogleApiService;
import com.messi.user.util.ConstantCenter;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.constant.Const;
import com.world.controller.api.util.SystemCode;
import com.world.model.LimitType;
import com.world.model.dao.app.AppDao;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.user.EmailDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.VerifyUserInfoDao;
import com.world.model.dao.user.authen.AuthenticationDao;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.app.App;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserCommon;
import com.world.model.entity.user.UserContact;
import com.world.model.entity.user.VerifyUserInfo;
import com.world.model.entity.user.authen.Authentication;
import com.world.util.CommonUtil;
import com.world.util.Message;
import com.world.util.language.SafeTipsTag;
import com.world.util.sign.RSACoder;
import com.world.util.string.MD5;
import com.world.web.response.DataResponse;
import com.world.web.sso.SessionUser;
import com.world.web.sso.rsa.RsaUser;
import com.world.web.sso.session.ClientSession;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

@SuppressWarnings("serial")
public class MobileUserAction extends BaseAction implements UserCommon {

	private static final String CHARSET = "UTF-8";
	
	public final static String appLoginCache = "app_login_";
	final static String appNewLocationLoginCache = "app_newlocation_login_";
	public final static String appUid = GlobalConfig.session + "appuid_";
	
	final static int appDefaultTime = 30 * 24 * 60 * 60;
	
	final static String rsaUserCache = "app_rsa_user";
	
	final static int rsaTime = 30 * 24 * 60 * 60;

	public void setLan() {
		String lang = param("lang");
		if (StringUtils.isNotBlank(lang)) {
			if (lang.equals("0")) {
				lang = "hk";
			} else if(lang.equals("1")) {
				lang = "cn";
			} else if (lang.equals("2")) {
				lang = "en";
			} else if (lang.equals("3")) {
				lang = "jp";
			} else if (lang.equals("4")) {
				lang = "kr";
			} else {
				lang = "cn";
			}
		} else {
			lang = "cn";
		}
		lan = lang;
	}
	
	public void json(SystemCode code) {
		json(code, L(code.getValue()), null);
	}
	
	public void json(SystemCode code, String msg) {
		json(code, L(msg), null);
	}
	
	public void json(SystemCode code, Map<String, Object> datas) {
		json(code, L(code.getValue()), datas);
	}

	public void json(SystemCode code, List<Object> datas) {
		json(code, L(code.getValue()), datas);
	}
	
	public void json(SystemCode code, String msg, Object datas) {
		PrintWriter out = null;
		try {
			response.setContentType("application/json;charset=" + CHARSET);
			out = response.getWriter();
			JSONObject json = new JSONObject();
			
			
			String whoInvokeMe = "";
			boolean isVersionGt5 = true;
			
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			for (StackTraceElement item : stackTraceElements) {
				if (item.getClassName().startsWith("com.world.controller.api.m")) {
					whoInvokeMe = item.getMethodName();
					
					/*String versionStr = item.getClassName().substring(item.getClassName().length()-1);
					if (Integer.valueOf(versionStr) > 5) 
						isVersionGt5 = true;*/
				}
			}
			
			//if (isVersionGt5) {
				JSONObject resMsg = new JSONObject();
				
				resMsg.put("code", code.getKey());
				resMsg.put("message", L(msg));
				resMsg.put("method", whoInvokeMe);
				json.put("resMsg", resMsg);
				if (null != datas) {
					json.put("data", datas);
				}
			/*} else {
				json.put("code", code.getKey());
				json.put("message", L(msg));
				if (null != datas) {
					json.putAll(datas);
				}
			}*/
			out.write(json.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (null != out) {
					out.flush();
					out.close();
				}
			} catch (Exception e2) {
				log.error("内部异常", e2);
			}
		}
	}

	public void writeMsg(String msg) {
		response.setContentType("text/plain;charset=" + CHARSET);
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {
			try {
				if (null != out) {
					out.write(L(msg));
					out.flush();
					out.close();
				}
			} catch (Exception e2) {
				log.error("内部异常", e2);
			}
		}
	}

	public void write2Json(Object obj) {
		PrintWriter out = null;
		try {
			response.setContentType("application/json;charset=" + CHARSET);
			out = response.getWriter();
			if (obj != null) {
				JSONArray json = JSONArray.fromObject(obj);
				out.write(json.toString());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (null != out) {
					out.flush();
					out.close();
				}
			} catch (Exception e2) {
				log.error("内部异常", e2);
			}
		}
	}

	public void write2JsonObj(Object obj) {
		PrintWriter out = null;
		try {
			response.setContentType("application/json;charset=" + CHARSET);
			out = response.getWriter();
			if (obj != null) {
				JSONObject json = JSONObject.fromObject(obj);
				out.write(json.toString());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (null != out) {
					out.flush();
					out.close();
				}
			} catch (Exception e2) {
				log.error("内部异常", e2);
			}
		}
	}
	


	/**
	 * check手机或邮箱验证码
	 * @param uid
	 * @param pct
	 * @param sendType 1-邮箱验证，2-手机验证
	 * @param code
	 * @return
	 */
	public DataResponse getDataResponse(String uid, PostCodeType pct, String sendNum, int sendType, String code, LimitType lt, String functionName, String lockTime) {
		DataResponse dr = null;
		String userIp = ip();
		PostCodeType postCodeType = (PostCodeType) EnumUtils.getEnumByKey(pct.getKey(), PostCodeType.class);
		String codeType = postCodeType.getValue();
		if(sendType == 1){
			ClientSession clientSession = new ClientSession(userIp, sendNum, lan, codeType, false);
			dr = clientSession.checkCodeMailApp(uid,code,lt,functionName,lockTime);
		}else{
			ClientSession clientSession = new ClientSession(userIp, sendNum, lan, codeType, false);
			dr = clientSession.checkCodeApp(uid,code,lt,functionName,lockTime);
		}
//		EmailDao eDao = new EmailDao();
//		String info = "";
//		String title = "锁定%%功能";
//		if("cn".equals(lan)){
//			if(dr.getDes().contains("验证码输入次数超出限制")){
//				info = eDao.getWrongLimitEmailHtml(user, dr.getDes(), this);
//			}
//		}else if("en".equals(lan)){
//			if(dr.getDes().contains("Code input error too many times in a row that your close Google authentication has been locked.")){
//				info = eDao.getWrongLimitEmailHtml(user, dr.getDes(), this);
//			}
//		}else if("hk".equals(lan)){
//			if(dr.getDes().contains("驗證碼輸入次數超出限制")){
//				info = eDao.getWrongLimitEmailHtml(user, dr.getDes(), this);
//			}
//		}
//		//锁定发送邮件
//		eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, user.getUserContact().getSafeEmail());
		return dr;
	}

	/**
	 * 谷歌验证
	 * @param uid
	 * @param googleCode
	 * @param secret
	 * @param uft
	 * @param funType:1-登录二次验证,2-忘记密码谷歌验证
	 * @return
	 */
	public DataResponse verifyGoogle(String uid,String googleCode,String secret,ConstantCenter.UpdFunctionType uft, String lockTime,String funType){
		DataResponse dr = new DataResponse("", false, "{\"id\" : \"vercode\"}");
		String url = ApiConfig.getValue("usecenter.url");
		FeignContainer container1 = new FeignContainer(url.concat("/google"));
		GoogleApiService googleApiService = container1.getFeignClient(GoogleApiService.class);
		//校验谷歌
		String uftGoogle = String.valueOf(uft.getKey());
		String googleMsgToast = "";
		if("1".equals(funType)){
			googleMsgToast = String.format("验证码输入次数超出限制，将锁定登录功能，请%s小时之后再试。",lockTime);
		}else if("2".equals(funType)){
			googleMsgToast = String.format("验证码输入次数超出限制，将锁定忘记登录密码功能，请%s小时之后再试。",lockTime);
		}else{
			googleMsgToast = String.format("验证码输入次数超出限制，将锁定提现功能，请%s小时之后再试。",lockTime);
		}
		Map<String,String> map1 = googleApiService.checkGoogleCodeApiN(googleCode,secret,uid,uftGoogle);
		for(Map.Entry<String,String> entry1 : map1.entrySet()){
			if("1".equals(entry1.getKey())){
                dr.setSuc(true);
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
				dr.setDes(returnVal);
			}
		}
		return dr;
	}

	public void toNewLocationLogin(String userId, String userName, String ip) {
		try {
			String token = MD5.toMD5(userId + UUID.randomUUID().toString());
			String loginCacheKey = appNewLocationLoginCache + userId;
			SessionUser su = new SessionUser();
			su.uid = userId;//用户id
			su.uname = userName;//用户名
			su.ltime = System.currentTimeMillis();//登录时间
			su.lip = ip;//登录ip
			su.lastTime = su.ltime;//最后活动时间
			su.token = token;
			
			Cache.SetObj(loginCacheKey, su, appDefaultTime);
		} catch(Exception e) {
			log.error("内部异常", e);
		}
	}
	
	public boolean isLogin1(String userId, String token) {
		if (null == userId || "".equals(userId) || null == token || "".equals(token)) {
			return false;
		}
		if(token(userId).equals(token)) {
			return true;
		}
		return false;
	}

	public SystemCode isLogin(String userId, String token) {
		if (null == userId || "".equals(userId) || null == token || "".equals(token)) {
			return SystemCode.code_1003;
		}

		if(StringUtils.isEmpty(token(userId))){
			return SystemCode.code_1003;
		}else{
			if(token(userId).equals(token)) {
				return SystemCode.code_1000;
			}else{
				return SystemCode.code_402;
			}
		}
	}
	
	public boolean newLocationTokenCheck(String userId, String token) {
		if (null == userId || "".equals(userId) || null == token || "".equals(token)) {
			return false;
		}
		if(newLocationToken(userId).equals(token)) {
			return true;
		}
		return false;
	}
	
	public String token(String userId) {
		String token = "";
		try {
			String loginCacheKey = appUid + userId;
			if (null != Cache.GetObj(loginCacheKey)) {
				Object obj = Cache.GetObj(loginCacheKey);
				if(null != obj){
					SessionUser sessionUser = (SessionUser) Cache.GetObj(obj.toString());
					if (null != sessionUser) {
						token = sessionUser.others.get("token").toString();
					}
				}
			}
	    } catch(Exception e) {
			log.error("内部异常", e);
	    }
		return token;
	}
	
	public String newLocationToken(String userId) {
		String token = "";
		try {
			String loginCacheKey = appNewLocationLoginCache + userId;
			if (null != Cache.GetObj(loginCacheKey)) {
				SessionUser sessionUser = (SessionUser) Cache.GetObj(loginCacheKey);
				if (null != sessionUser) {
					token = sessionUser.token;
				}
			}
		} catch(Exception e) {
			log.error("内部异常", e);
		}
		return token;
	}
	
	public synchronized String getPubKey() {
		RsaUser ru = null;
		if (null != Cache.GetObj(rsaUserCache)) {
			ru = (RsaUser) Cache.GetObj(rsaUserCache);
		} else {
			ru = createRsaKey();
		}
		return ru.getPubKey();
	}
	
	public synchronized String getPriKey() {
		RsaUser ru = null;
		if (null != Cache.GetObj(rsaUserCache)) {
			ru = (RsaUser) Cache.GetObj(rsaUserCache);
		} else {
			ru = createRsaKey();
		}
		return ru.getPriKey();
	}
	
	public synchronized RsaUser createRsaKey() {
		RsaUser ru = null;
		try {
			ru = new RsaUser();
			Map<String, Object> keyMap = RSACoder.initKey();
			
			String publicKey = RSACoder.getPublicKey(keyMap).replace(" ", "").replace("\n", "").replace("\r", "");
			String privateKey = RSACoder.getPrivateKey(keyMap).replace(" ", "").replace("\n", "").replace("\r", "");
			ru.setPubKey(publicKey);
			ru.setPriKey(privateKey);
			
			Cache.SetObj(rsaUserCache, ru, rsaTime);
		} catch (Exception e) {
			log.error("内部异常", e);
		}
		return ru;
	}
	
	public int getTotalPage(int total, int pageSize) {
		int size = total / pageSize;// 总条数/每页显示的条数=总页数
		int mod = total % pageSize;// 最后一页的条数
		if (mod != 0)
			size++;
		return total == 0 ? 1 : size;
	}

	protected boolean authen(String userId){
		Authentication au = new AuthenticationDao().getByUserId(userId);
		if(au == null || au.getStatus() != AuditStatus.pass.getKey()){
			return false;
		}
		return true;
	}
	/**
	 * 看看用户是不是没有认证手机&&没有谷歌认证
	 * @return
	 */
	protected boolean isRunningBarely(User loginUser){
		if((StringUtils.isBlank(String.valueOf(loginUser.getUserContact().getGoogleAu())) || loginUser.getUserContact().getGoogleAu()!=2)
				&&(loginUser.getUserContact().getMobileStatu() != AuditStatus.pass.getKey())){
	    	 return true;
	    }
		return false;
	}
	
	//判断移动设备验证码是否正确
	protected boolean isGoogleCodeCorrect(String savedSecret, long code, String userId){
		
		VerifyUserInfo bean = new VerifyUserInfoDao().getVerifyingInfo(userId, 2);
		if (bean != null && bean.getStatus() == 0) {
			json(SystemCode.code_1001,L("您已经于%%提交了谷歌验证修改申请，请您耐心，我们将尽快为您审核!", bean.getAddTimeShowString()));
			return false;
		}
		
		if(code == 0){
			json(SystemCode.code_1001, L("请输入移动设备上生成的验证码。"));
			return false;
		}
		
		String key = userId+"_gauth";
		if(locked(userId, key)){
			return false;
		}
		
		long t = System.currentTimeMillis();
		GoogleAuthenticator ga = new GoogleAuthenticator();
		ga.setWindowSize(3);
		
		boolean r = ga.check_code(savedSecret, code, t);
		if(!r){
			lock2Hours(userId, key);
			return false;
		}else{
			Object current = Cache.GetObj(key);
			if(current != null){
				int count = Integer.parseInt(current.toString());
				if(count >= times){
					json(SystemCode.code_1001, L("您连续输入错误的次数太多，请2小时后再试。"));
					return false;
				}
				Cache.Delete(key);
			}
		}
		return true;
	}
	
	protected boolean safePwd(String pass , String userId){
		
		SafeTipsTag stt = getLanTag().getStt();
		
		int status = new UserDao().checkSecurityPwd(pass, userId);
		if(status==-2){
			json(SystemCode.code_1001, L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。"));
			return false;
		}else if(status==-1){
			json(SystemCode.code_1001, L(stt.getCuoWu())); 
			return false;
		}else if(status==0){
			json(SystemCode.code_1001, L(stt.getWeiSheZhi())); 
			return false;
		}
		return true;
	}
	
	protected boolean validateFingerprintOrRelatives(User loginUser,String fingerprint,String safePwd,String mobileCode,String googleCode,boolean isNew,boolean aboveAmount) throws Exception{
		String userId = loginUser.get_Id();
		UserContact uc = loginUser.getUserContact();
		boolean safePwdFlag=false,mobileCodeFlag=false,googleCodeFlag=false;
		
		if (StringUtils.isNotBlank(fingerprint)) {//有传指纹过来
			if (null == loginUser || StringUtils.isBlank(loginUser.getFingerprint())) {
				json(SystemCode.code_1001, "您还没有开启指纹验证");
				return false;
			}
			if (null == loginUser || null == loginUser.getFingerprint() || !loginUser.getFingerprint().equals(fingerprint)) {
				json(SystemCode.code_1001, "验证指纹失败，请重试");
				return false;
			}
		 }else {//没有传指纹
			 if (isNew || (!isNew && aboveAmount)) {//新卡或者旧卡超额=验证资金安全密码+谷歌/短信
				 safePwdFlag = true;
				 if (uc.getGoogleAu() == AuditStatus.pass.getKey() && loginUser.isPayGoogleAuth()) {//Google提现验证开关：如果开启了，当需要验证手机的时候登录Google验证，关闭了的话就使用短信验证
					 googleCodeFlag = true;
					 if (StringUtils.isBlank(safePwd) || StringUtils.isBlank(googleCode)){
						json(SystemCode.code_1022);
						return false;
					 }
				 }else {
					 mobileCodeFlag = true;
					 if (StringUtils.isBlank(safePwd) || StringUtils.isBlank(mobileCode)) {
						json(SystemCode.code_1014);
						return false;
					 }
				}
			 }else {
				 if (uc.getGoogleAu() == AuditStatus.pass.getKey() && loginUser.isPayGoogleAuth()) {//如果开启了Google提现验证，则不论金额都需要资金安全密码
					 googleCodeFlag = true;
					 safePwdFlag = true;
					 if (StringUtils.isBlank(safePwd) || StringUtils.isBlank(googleCode)){
						json(SystemCode.code_1022);
					 	return false;
					 }
				 }
			 }
			 
		}
		if (safePwdFlag) {
			if(!safePwd(safePwd, userId)){
				return false;
			}
		}
		if (googleCodeFlag) {
			long gCode = CommonUtil.stringToLong(googleCode);
			
			if (!isGoogleCodeCorrect(uc.getSecret(), gCode, userId)) {
				return false;
			}
		}
		if (mobileCodeFlag) {
			// 检查短信验证码
			ClientSession clientSession = new ClientSession(ip(), uc.getSafeMobile(), lan, "验证码", false);
			DataResponse dr = clientSession.checkCode(mobileCode);
			if(!dr.isSuc()){
				json(SystemCode.code_1001, L(dr.getDes()));
				return false;
			}
		}
		return true;
	}
	
	public void lock2Hours(String userId, String key){
		Object current = null;
		current = Cache.GetObj(key);
		
		String des = L("短信验证码输入有误，");
		if(key.endsWith("gauth")){
			des = L("谷歌验证码输入有误，");
		}
		
		if(current == null){
			Cache.SetObj(key, 1, 60*60*2);
			if((times - 1)>1){
				json(SystemCode.code_1015, des+L("您还有%%次机会s" , (times - 1) + ""));
			}else{
				json(SystemCode.code_1015, des+L("您还有%%次机会。" , (times - 1) + ""));
			}
		}else{
			int count = Integer.parseInt(current.toString());
			count++;
			Cache.SetObj(key, count, 60*60*2);
			if(count < times){
				if((times - count) > 1){
					json(SystemCode.code_1015, des+L("您还有%%次机会s" , (times - count) + ""));
				}else{
					json(SystemCode.code_1015, des+L("您还有%%次机会。" , (times - count) + ""));
				}
			}else{
				json(SystemCode.code_1015, L("您连续输入错误的次数太多，请2小时后再试。"));
			}
		}
	}
	
	public boolean locked(String userId, String key){
		Object current = null;
		current = Cache.GetObj(key);
		if(current != null){
			int count = Integer.parseInt(current.toString());
			if(count >= times){
				json(SystemCode.code_1015, L("您连续输入错误的次数太多，请2小时后再试。"));
				return true;
			}
		}
		return false;
	}
	
	public String getTailNumber(String card_Number) {
		if(card_Number!=null && card_Number.length()>4){
			int len = card_Number.length();
			card_Number=card_Number.substring(len-4,len);
		}else if(card_Number!=null && card_Number.length()<=4){
			
		} else return "";
		return card_Number;
	}

	/*public boolean validateMobileCode(int type){
		initLoginUser();
		String gs = "json";
		UserContact uc = loginUser.getUserContact();
		if(uc.getMobileStatu() != AuditStatus.pass.getKey()){
			WriteError(L("您还没有通过手机认证，请先进行手机认证。"));
	    	return false;
	    }
		
		String mCode = param("mCode");

		if (!isCorrect(mCode, type, gs)) {// json(L("验证码错误"), false, "");
			return false;
		}

		if (!hasEffective(gs)) {// json("验证码失效，请重新发送验证码。", false, "");
			return false;
		}
		return true;
	}

	protected boolean hasEffective(String gs){
		initLoginUser();
		Timestamp codeTime = loginUser.getUserContact().getCodeTime();
		if(codeTime != null && System.currentTimeMillis() - codeTime.getTime() > 60000*20){//验证码发送超过10分钟失效
			if(gs.equals(XML))
				WriteError(L("验证码失效，请重新发送验证码。"));
			else
				json(L("验证码失效，请重新发送验证码。"), false, "");
			return false;
		}
		return true;
	}*/
	
	public String decryptRSAParam(String paramName) throws Exception {
		String encryptValue = param(paramName);
		String decryptValue = "";
		if (StringUtils.isNotBlank(encryptValue)) {
			decryptValue = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(encryptValue), getPriKey()));
		}
		return decryptValue;
	}
	
	public boolean safeStrategyNeedGoogleAuth(int category, int type) {
        boolean googleAuthRequired = false;
        if (category == 1) {
            if (type == 2 || type == 4) {
                googleAuthRequired = true;
            }
        } else if (category == 3) {
            if (type == 2 || type == 3) {
                googleAuthRequired = true;
            }
        }
        return googleAuthRequired;
    }
	
	public Message isFingerprintCorrect(User loginUser, String fingerCode, String fingerprint) throws Exception {

        String destFingerprint = loginUser.getFingerprint();
            destFingerprint = encryptFingerprint(loginUser.get_Id(), fingerCode);
      
        Message msg = new Message();
        if (StringUtils.isBlank(loginUser.getFingerprint())) {
            msg.setCode(SystemCode.code_1001.getKey());
            msg.setMsg("指纹密码设置异常，请重新设置指纹密码");
        } else if (StringUtils.isBlank(destFingerprint) || !destFingerprint.equals(fingerprint)) {
            msg.setCode(SystemCode.code_1001.getKey());
            msg.setMsg("验证指纹失败，请重试");
        } else {
            msg.setSuc(true);
        }
        return msg;
    }
	
	
	public String encryptFingerprint(String userId, String fingerCode) throws Exception {
//		RSA(MD5(appsecret+用户id+用户token+128位随机验证码+ appKey))
		//参数处理
		String encryptValue = "";
		Map<String, String> parameters = getRequestParameterMap();
		//拿到客户端传来的appKey
		String appKey = parameters.get("appKey");
        if (StringUtils.isBlank(appKey))
			return null;
		//找到对应客户端
        String cacheSecret = getCacheSecret(appKey);
		if (null == cacheSecret)
			return null;
		//计算encryptValue
		encryptValue = cacheSecret + userId + token(userId) + fingerCode + appKey;
        encryptValue = MD5.toMD5(encryptValue).toLowerCase();
        encryptValue = encryptValue.toLowerCase();
//        if (StringUtils.isNotBlank(encryptValue)) {
//        	encryptValue = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(encryptValue), getPriKey()));
//		}
        return encryptValue;
	}
	
	private String getCacheSecret(String appKey) {
		AppDao appDao = new AppDao();
		App app = appDao.getByField("key", appKey);
		if (null == app) {
			return null;
		} else {
			String cacheSecret = Cache.Get(appKey);
			if (null == cacheSecret) {
				cacheSecret = app.getSecret();
				Cache.Set(appKey, app.getSecret());
			}
			return cacheSecret;
		}
	}
	
	/**
     * getRequestParameterMap
	 * @throws UnsupportedEncodingException 
     */
	private Map<String,String> getRequestParameterMap() throws UnsupportedEncodingException{
		 request.setCharacterEncoding(CHARSET);
		 Map<String, String[]> parameters = request.getParameterMap();
		 Map<String, String> properties = new HashMap<String, String>();
		 for (Map.Entry<String, String[]> e : parameters.entrySet()) {
			 String[] value = e.getValue();
			 if (value != null) {
				String valueStr = Arrays.toString(value);
				properties.put(e.getKey(), valueStr.substring(1, valueStr.length()-1));
			 } else {
				continue;
			 }
	     }
		 return properties;
	}

	/**
	 * check验证是否锁定
	 * @param userId
	 * @param lt
	 * @return
	 */
	public int doGetErrorTimes(String userId, LimitType lt) {
		int status = 0;
		status = lt.GetStatusNew(userId);
		if(status==-1){
			//已锁定
			return -2;
		}
		return status;
	}
}

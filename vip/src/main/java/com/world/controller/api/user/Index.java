package com.world.controller.api.user;

import cn.hutool.core.util.StrUtil;
import com.Lan;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.GoogleApiService;
import com.messi.user.feign.UserApiService;
import com.messi.user.util.ConstantCenter;
import com.world.cache.Cache;
import com.world.constant.Const;
import com.world.controller.CheckRegex;
import com.world.controller.api.common.util.VerifiUtil;
import com.world.controller.api.m.BaseMobileAction;
import com.world.controller.api.util.SystemCode;
import com.world.data.database.DatabasesUtil;
import com.world.model.LimitType;
import com.world.model.dao.api.ApiKeyDao;
import com.world.model.dao.iplist.WhiteIpDao;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.msg.NewsDao;
import com.world.model.dao.pay.DetailsDao;
import com.world.model.dao.pay.DownloadDao;
import com.world.model.dao.pay.KeyDao;
import com.world.model.dao.pay.ReceiveAddrDao;
import com.world.model.dao.user.EmailDao;
import com.world.model.dao.user.MobileDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.authen.AuthenLogDao;
import com.world.model.dao.user.authen.AuthenticationDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.api.ApiKey;
import com.world.model.entity.iplist.WhiteIp;
import com.world.model.entity.msg.News;
import com.world.model.entity.pay.KeyBean;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.model.entity.user.authen.AuthenType;
import com.world.model.entity.user.authen.Authentication;
import com.world.model.enums.LogCategory;
import com.world.model.jifenmanage.JifenManage;
import com.world.model.singleton.SingletonThreadPool;
import com.world.rabbitmq.producer.OperateLogInfoProducer;
import com.world.util.CommonUtil;
import com.world.util.Message;
import com.world.util.MsgToastKey;
import com.world.util.language.SafeTipsTag;
import com.world.util.sign.RSACoder;
import com.world.util.string.EncryptionPhoto;
import com.world.util.string.MD5;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.MobileUserAction;
import com.world.web.action.UserAction;
import com.world.web.response.DataResponse;
import com.world.web.sso.session.ClientSession;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Index extends UserAction{

	private VerifiUtil verifiUtil = VerifiUtil.getInstance();
	UserDao userDao = new UserDao();
	AuthenLogDao logDao = new AuthenLogDao();
	ApiKeyDao apiKeyDao = new ApiKeyDao();//api Key dao
	public final static String readNoticeKey = "user_notice_app_";
	private final String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIkbXj73gMSDPJ3obz5R76Wksor24h+2lJnvC+dfeO3D7pJQqV2S8vWZir1byogkPvDm3dhAiRWhYiWbkRtrk1wNiB8ML9CdjKv3N8O2GUkQK9DxMLTCPL1w60ILgWe17CyqYbEuNMDXtmfREmmype80WNkSKdHohrC+eWE8on8jAgMBAAECgYA0EsPx0FkEyf9szgnqNn55gBsbsnbhqpu391WjE9y/GUp0IdShqJ1EcIOENeevW2zYXCbn6mLmZzv6oqIzMuFtZ4GGbHvTsMNGtoBJsvIjV36FjdiXU7FAGqtUI+I/kFBvxFuKcil6JBFGKheQle2segoB9hAsKGoUSayAE5yjqQJBAMJllnMTMeuomhZxSQfuq4Ke3BAGGbbUfcCYnCoK1y9LBe3qXmynWYnc2caIHgbMdDiGYcTm1XOZ5lR/a2GP4HUCQQC0jiUFKWmWkx+MgverbA4QBoh+ff5M95c5T/8W2QbrUW7DV++aW4y+4D92Ei6nFcF1V8SSMxgDmqiz6pOqS243AkEAl6vlR6GZWHGyz4HR5kN8Q6yorEPmOjTubJ9lcJQGspqJZMhwpbuoa50JuRGow8svfo6yp4smzUwtXo4P/Q3hpQJAC8AIZrqYNYVjkzhet9gzXhWewmSerRGb1M4A8tKy4ZOOGsZZQHlewnlDiAKM6LDAw0sv7rfGg02IVxUYAQghpwJABiNcbBh7MnDfGaRZzE7SX/UwRn7OmGY7lFMBWadiQ/R5pKpdPrVmwdlTsefzb1acYy41LQCFCPKxVjv7sUduXQ==";
	/**
	 * 昵称限制字符
	 */
	public final static String LIMIT_KEYWORD = "BTC,LTC,ETC,ETH,DASH,OMG,USDT,QTUM,EOS,ELF,SNT,IOST,ZRX,LINK,KNC,Bitglobal,Bitgloba,GBC,OTCPTA,QQ,微信,企鹅,微博,ICO,政府,习近平,官方,bit全球,otc,btcwinex";
	/**
	 * 密码最小长度
	 */
	private static final int PASSWORD_MIN_LENGTH = 8;

	public static String l(String lan, String key) {
		return Lan.Language(lan, key);
	}

	public String l(String lan, String key, String str) {
		return Lan.LanguageFormat(lan, key, str);
	}

	public String getLan(String uid) {
		return StringUtil.exist(Cache.Get("user_lan_" + uid)) ? Cache.Get("user_lan_" + uid) : "en";
	}
	
	/**
	 * 是否开启安全密码
	 * @param
	 * @param
	 */
	@Page(Viewer = JSON)
	public void isUseSafePwd(){
		try{
			//签名验证
			Object flag = verifiUtil.validateAuthAccess(request, ip());
			if(flag instanceof SystemCode){
				WriteMsg3(((SystemCode)flag).getKey(), ((SystemCode) flag).getValue(), false, "");
				return;
			}else{
				if(!((Boolean)flag)){
					WriteMsg3(SystemCode.code_1003.getKey(), SystemCode.code_1003.getValue(), false, "");
					return;
				}
			}
			String userId = request.getParameter("userId");
			User user = verifiUtil.getUserById(userId);

			if(user == null){
				WriteMsg3(SystemCode.code_3004.getKey(), SystemCode.code_3004.getValue(), false, "");
				return;
			}

			WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getValue(), true, "{\"useSafePwd\":"+user.getUseSafePwd()+"}");
			return;
		} catch (Exception e) {
			log.error("内部异常", e);
			WriteMsg3(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue(), false, "");
		}
	}
	
	/**
	 * TODO modify by suxinjie 先只修改这一个方法为国际化,用WriteMsg4方法
	 *
	 * 是否通过安全认证
	 * @param
	 * @param
	 */
	@Page(Viewer = JSON)
	public void validateSafePwd(){
		try{
			//签名验证
			Object flag = verifiUtil.validateAuthAccess(request, ip());
			if(flag instanceof SystemCode){
				WriteMsg3(((SystemCode)flag).getKey(), ((SystemCode) flag).getValue(), false, "");
				return;
			}else{
				if(!((Boolean)flag)){
					WriteMsg4(SystemCode.code_1003.getKey(), SystemCode.code_1003.getValue(), false, "");
					return;
				}
			}
			String userId = request.getParameter("userId");
			String safePwd = request.getParameter("safePwd");
			String market = request.getParameter("market");//是否是交易
			String type = request.getParameter("type");//判断是否是买btq，类型是1代表买
			String use = request.getParameter("use");
			User user = verifiUtil.getUserById(userId);
			if(user == null){
				WriteMsg4(SystemCode.code_3004.getKey(), l(getLan(userId), SystemCode.code_3004.getValue()), false, "");
				return;
			}
			if(market != null){
				if(user.getRepayLock() == 1){
					WriteMsg4(SystemCode.code_4003.getKey(), l(getLan(userId), SystemCode.code_4003.getValue()), false, "");
					return;
				}else if(user.getRepayLock() == 2 && market.equals("btqdefault") && type.equals("1")){
					WriteMsg4(SystemCode.code_4005.getKey(), l(getLan(userId), SystemCode.code_4005.getValue()), false, "");
					return;
				}
			}
			if (null == market || userDao.isNeedSafePwd(user)||StringUtil.exist(safePwd)) {
				if(user.getUseSafePwd() || use!=null){
					int status = new UserDao().checkSecurityEntrustPwd(safePwd, user.getId());
					/*String ip=ip();
					if(status==1){
						String whiteIpSet=Cache.Get("whiteIp"+user.get_Id());
						if(null==whiteIpSet){
							whiteIpSet="";
						}
						if(!whiteIpSet.contains(ip)){
							Cache.Set("whiteIp"+user.get_Id(), whiteIpSet+","+ip, 24*3600);
						}
                      //去掉ip不一致验证


					}*/
					/*if (null != market) {
						status = new UserDao().transCheckSecurityPwd(safePwd, user.getId());
					}*/
					if(status == -2){
						WriteMsg4(SystemCode.code_1004.getKey(), l(getLan(userId), SystemCode.code_1004.getValue()), false, "");
						return;
					}
					if(status == -1){
						WriteMsg4(SystemCode.code_1005.getKey(), l(getLan(userId), SystemCode.code_1005.getValue()), false, "");
						return;
					}
					if(status == 0){
					//	WriteMsg3(SystemCode.code_1026.getKey(), SystemCode.code_1026.getValue(), false, "");
						return;
					}
				}
			}

			WriteMsg4(SystemCode.code_1000.getKey(),l(getLan(userId), SystemCode.code_1000.getValue()), true, "");
			return;
		} catch (Exception e) {
			log.error("内部异常", e);
			WriteMsg3(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue(), false, "");
		}
	}
	/**
	 * TODO modify by suxinjie 先只修改这一个方法为国际化,用WriteMsg4方法
	 *
	 * 是否通过安全认证
	 * @param
	 * @param
	 */
	@Page(Viewer = JSON)
	public void verfiSafePwd(){
		try{
			//签名验证
			Object flag = verifiUtil.validateAuthAccess(request, ip());
			if(flag instanceof SystemCode){
				WriteMsg3(((SystemCode)flag).getKey(), ((SystemCode) flag).getValue(), false, "");
				return;
			}else{
				if(!((Boolean)flag)){
					WriteMsg4(SystemCode.code_1003.getKey(), SystemCode.code_1003.getValue(), false, "");
					return;
				}
			}
			String userId = request.getParameter("userId");
			String safePwd = request.getParameter("safePwd");
			String market = request.getParameter("market");//是否是交易
			String use = request.getParameter("use");
			User user = verifiUtil.getUserById(userId);
			if(user == null){
				WriteMsg4(SystemCode.code_3004.getKey(), l(getLan(userId), SystemCode.code_3004.getValue()), false, "");
				return;
			}

			if (null == market || userDao.isNeedSafePwd(user)||StringUtil.exist(safePwd)) {
				if(user.getUseSafePwd() || use!=null){
					int status = new UserDao().checkPayPwd(safePwd, user.getId());
					if(status == -1){
						WriteMsg4(SystemCode.code_1005.getKey(), l(getLan(userId), SystemCode.code_1005.getValue()), false, "");
						return;
					}
					if(status == 0){
						//	WriteMsg3(SystemCode.code_1026.getKey(), SystemCode.code_1026.getValue(), false, "");
						return;
					}
				}
			}

			WriteMsg4(SystemCode.code_1000.getKey(),l(getLan(userId), SystemCode.code_1000.getValue()), true, "");
			return;
		} catch (Exception e) {
			log.error("内部异常", e);
			WriteMsg3(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue(), false, "");
		}
	}


	/**
	 * 验证指纹或资金安全密码
	 */
	@Page(Viewer = JSON)
	public void validateFingerprintOrSafePwd(){
		try{
			//签名验证
			Object flag = verifiUtil.validateAuthAccess(request, ip());
			if(flag instanceof SystemCode){
				WriteMsg4(((SystemCode)flag).getKey(), ((SystemCode) flag).getValue(), false, "");
				return;
			}else{
				if(!((Boolean)flag)){
					WriteMsg4(SystemCode.code_1003.getKey(), SystemCode.code_1003.getValue(), false, "");
					return;
				}
			}
			String userId = request.getParameter("userId");
			String safePwd = request.getParameter("safePwd");
			String fingerprint = request.getParameter("fingerprint");
			String fingerCode = request.getParameter("fingerCode");
			String apiClsName = request.getParameter("apiClsName");
			String market = request.getParameter("market");
			String lang = request.getParameter("lang");
			User user = verifiUtil.getUserById(userId);
			if(user == null){
				WriteMsg4(SystemCode.code_3004.getKey(), SystemCode.code_3004.getValue(), false, "");
				return;
			}
			if (StringUtils.isNotBlank(fingerprint) && StringUtils.isNotBlank(user.getFingerprint())) {//有指纹
				String destFingerprint = getDestFingerprint(user, fingerCode, apiClsName);
				if (StringUtils.isBlank(destFingerprint) || !destFingerprint.equals(fingerprint)) {
					WriteMsg4(SystemCode.code_1001.getKey(), L("验证指纹失败，请重试"), false, "");
				}else {
					WriteMsg4(SystemCode.code_1000.getKey(), SystemCode.code_1000.getValue(), true, "");//正确
				}
			}

			if ((StringUtils.isBlank(user.getFingerprint()) || StringUtils.isBlank(fingerprint)) && userDao.isNeedSafePwd(user)) {//没有指纹，开启了交易资金安全密码
//				SafeTipsTag stt = getLanTag().getStt();
				SafeTipsTag stt = getLanTag(lang).getStt();
//				int status = new UserDao().checkSecurityPwd(safePwd, userId);
//				if (null != market) {
//					status = new UserDao().transCheckSecurityPwd(safePwd, user.getId());
//				}
				int status = new UserDao().transCheckSecurityPwd(safePwd, user.getId());
				if (StringUtils.isBlank(safePwd)) {
					WriteMsg4(SystemCode.code_1013.getKey(), L(SystemCode.code_1013.getValue()), false, "");
				}else if(status==-2){
					//app调用此接口时国际化需要重新赋值
					lan = lang;
					WriteMsg4(SystemCode.code_1001.getKey(), L("资金密码输入次数超出限制，将锁定提现、修改资金密码、设置收款方式功能，请24小时之后再试"), false, "");
				}else if(status==-1){
					WriteMsg4(SystemCode.code_1005.getKey(), stt.getCuoWu(), false, "");
				}else if(status==0){
					WriteMsg4(SystemCode.code_1026.getKey(), stt.getWeiSheZhi(), false, "");
				}else {
					WriteMsg4(SystemCode.code_1000.getKey(), SystemCode.code_1000.getValue(), true, "");//正确
				}
			} else {
				WriteMsg4(SystemCode.code_1000.getKey(), SystemCode.code_1000.getValue(), true, "");//没有指纹，没有开启交易资金安全密码验证，直接返回
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			WriteMsg4(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue(), false, "");
		}
	}
	
	public String getDestFingerprint(User user, String fingerCode, String apiClsName) {
		if (!"V2_0".equalsIgnoreCase(apiClsName)) {
			return user.getFingerprint();
		}
//		RSA(MD5(appsecret+用户id+用户token+128位随机验证码+ appKey))
		//参数处理
		String encryptValue = "";
		//拿到客户端传来的appKey
		String appKey = request.getParameter("appKey");
        if (StringUtils.isBlank(appKey)) {
			return null;
		}
		//找到对应客户端
        String cacheSecret = Cache.Get(appKey);
		if (null == cacheSecret) {
			return null;
		}
		//计算encryptValue
		MobileUserAction mua = new MobileUserAction();
		encryptValue = cacheSecret + user.get_Id() + mua.token(user.get_Id()) + fingerCode + appKey;
        encryptValue = MD5.toMD5(encryptValue).toLowerCase();
        encryptValue = encryptValue.toLowerCase();
        
        /*Class clz = Class.forName("com.world.controller.api.m."+apiClsName);//com.world.controller.api.m.V2_0
        Object newInstance = clz.newInstance();
        Method method = clz.getMethod("getPriKey", null);
        Object rtn = method.invoke(newInstance);
        
        if (StringUtils.isNotBlank(encryptValue)) {
        	encryptValue = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(encryptValue), (String)rtn));
		}*/
        
        return encryptValue;
	}
	
	
	/**
	 * 自动交易API通过accesskey获取用户接口
	 * param accessKey 
	 * type 1:返回用户账户资金信息 0:返回API接口信息
	 * @return
	 */
	@Page(Viewer = JSON)
	public void getUserByAccessKey(){
		try{
			//签名验证
			Object flag = verifiUtil.validateAuthAccess(request, ip());
			if(flag instanceof SystemCode){
				WriteMsg3(((SystemCode)flag).getKey(), ((SystemCode) flag).getValue(), false, "");
				return;
			}else{
				if(!((Boolean)flag)){
					WriteMsg3(SystemCode.code_1003.getKey(), SystemCode.code_1003.getValue(), false, "");
					return;
				}
			}
			String key = URLDecoder.decode(param("key") , "utf-8");
			int type = Integer.parseInt(URLDecoder.decode(param("type") , "utf-8"));

			if(key.isEmpty()){
				WriteMsg(SystemCode.code_3005.getKey(), SystemCode.code_3005.getClassName());	//
				return;
			}
			ApiKey apiKey = apiKeyDao.findByKey(key);
			if(apiKey==null){//找不到对应的用户
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());	//
				return;
			}

			if(apiKey != null){
				if(apiKey.getIsAct() == 0 || apiKey.getIsLock()==1){
					WriteMsg3(SystemCode.code_4001.getKey(), SystemCode.code_4001.getValue(), false, "");
					return;
				}
				if(type == 0){
					StringBuilder sbr = new StringBuilder();
					sbr.append("{");
					sbr.append("\"userId\":").append("\""+apiKey.getUserId()+"\",");
					sbr.append("\"apiStatus\":").append(apiKey.getIsAct()).append(",");
					sbr.append("\"apiIpStatus\":").append(2).append(",");
					sbr.append("\"apiIpBind\":").append("\""+apiKey.getIpaddrs()+"\",");
					sbr.append("\"apiAuthTime\":").append("\""+apiKey.getAddTime()+"\",");
					sbr.append("\"apiKey\":").append("\""+apiKey.getAccesskey()+"\",");
					sbr.append("\"apiSecret\":").append("\""+apiKey.getSecretkey()+"\",");
					sbr.append("\"limit\":").append(10);
					sbr.append("}");
					WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getValue(), true, sbr.toString().replace("null", ""));
					return;
				}
				if(type == 1){
					/*User user = TradeService.getUser(btcuser);
					//PayUserBean pub = TradeService.getPayUser(btcuser);
					//btcUserBean btc = new btcUserBean(Integer.parseInt(user.getId())).getBtcUser("*");
					String result = TradeService.getAccountInfo(user);
					JSONObject json = JSONObject.fromObject(result);
					json.element("limit", btcuser.getApiLimitCount());*/
					
					JSONArray funds = UserCache.getUserFunds(apiKey.getUserId());
					JSONObject json = new JSONObject();
					json.put("result", funds);
					WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getValue(), true, json.toString());
					return;
				}
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			WriteMsg3(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue(), false, "");
		}
	}

	
	/**
	 * 自动交易API通过accesskey 获取充值地址的接口
	 * param accessKey
	 * @return
	 */
	@Page(Viewer = JSON)
	public void getUserAddress(){
		try{
			//签名验证
			Object flag = verifiUtil.validateAuthAccess(request, ip());
			if(flag instanceof SystemCode){
				WriteMsg3(((SystemCode)flag).getKey(), ((SystemCode) flag).getValue(), false, "");
				return;
			}else{
				if(!((Boolean)flag)){
					WriteMsg3(SystemCode.code_1003.getKey(), SystemCode.code_1003.getValue(), false, "");
					return;
				}
			}
			String key = URLDecoder.decode(param("key") , "utf-8");
			String currency = URLDecoder.decode(param("currency") , "utf-8");

			if(key.isEmpty()){
				WriteMsg(SystemCode.code_3005.getKey(), SystemCode.code_3005.getClassName());;	//
				return;
			}
			//btcUserBean btcuser = TradeService.getBtcUser(key);
			
			ApiKey  apiKey = apiKeyDao.findByKey(key);
			
			if(apiKey != null){
				if(apiKey.getIsAct() == 0 || apiKey.getIsLock()==1){//未启用或者被锁
					WriteMsg3(SystemCode.code_4001.getKey(), SystemCode.code_4001.getValue(), false, "");
					return;
				}
				if( !"".equals(currency) && DatabasesUtil.getCoinPropMaps().containsKey(currency) ){
					int userId = Integer.parseInt(apiKey.getUserId());
					//btcKeyBean keyBean = KeyDao.getBtcRechargeKey(userId, btcuser.getUserName());
					  KeyDao keyDao = new KeyDao();//钱包充值key dao
					  keyDao.setCoint(DatabasesUtil.getCoinPropMaps().get(currency));//绑定币种数据库
					  KeyBean keyBean = keyDao.getRechargeKey(userId,apiKey.getUserName());//获取充值地址
					if (null == keyBean) {
						WriteMsg3(SystemCode.code_1001.getKey(), L("没有充值地址"), false, "");
						return;
					}
					StringBuilder sbr = new StringBuilder();
					sbr.append("{");
					sbr.append("\"key\":").append("\""+keyBean.getKeyPre()+"\"");
					sbr.append("}");
					WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getValue(), true, sbr.toString().replace("null", ""));
					return;
				}else{
					WriteMsg3(SystemCode.code_3005.getKey(), SystemCode.code_3005.getValue(), false, "");
				}
			}else{
				//WriteMsg(SystemCode.code_3004.getKey(), "{" + SystemCode.code_3004.getClassName() + "}");;	//不存在该用户
				WriteMsg3(SystemCode.code_3004.getKey(), SystemCode.code_3004.getValue(), false, "");
				return;
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			WriteMsg3(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue(), false, "");
		}
	}
	
	
	
	
	
	/**
	 * 自动交易API通过accesskey获取提现地址的接口
	 * param accessKey
	 * @return
	 */
	@Page(Viewer = JSON)
	public void getWithdrawAddress(){
		try{
			//签名验证
			Object flag = verifiUtil.validateAuthAccess(request, ip());
			if(flag instanceof SystemCode){
				WriteMsg3(((SystemCode)flag).getKey(), ((SystemCode) flag).getValue(), false, "");
				return;
			}else{
				if(!((Boolean)flag)){
					WriteMsg3(SystemCode.code_1003.getKey(), SystemCode.code_1003.getValue(), false, "");
					return;
				}
			}
			String key = URLDecoder.decode(param("key") , "utf-8");
			String currency = URLDecoder.decode(param("currency") , "utf-8");

			if(key.isEmpty()){
				WriteMsg(SystemCode.code_3005.getKey(), SystemCode.code_3005.getClassName());;	//
				return;
			}
			
			ApiKey  apiKey = apiKeyDao.findByKey(key);
			if(apiKey != null){
				if(apiKey.getIsAct() == 0 || apiKey.getIsLock()==1){
					WriteMsg3(SystemCode.code_4001.getKey(), SystemCode.code_4001.getValue(), false, "");
					return;
				}
				if(!"".equals(currency) && DatabasesUtil.getCoinPropMaps().containsKey(currency)){
					ReceiveAddrDao recAddrDao = new ReceiveAddrDao();//提现地址 dao
					recAddrDao.setCoint(DatabasesUtil.getCoinPropMaps().get(currency));//绑定币种数据库
					WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getValue(), true, recAddrDao.findUserAuthAddrs(apiKey.getUserId()).toJSONString());
					return;
				}else{
					WriteMsg3(SystemCode.code_3005.getKey(), SystemCode.code_3005.getValue(), false, "");
				}
			}else{
				//WriteMsg(SystemCode.code_3004.getKey(), "{" + SystemCode.code_3004.getClassName() + "}");;	//不存在该用户
				WriteMsg3(SystemCode.code_3004.getKey(), SystemCode.code_3004.getValue(), false, "");
				return;
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			WriteMsg3(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue(), false, "");
		}
	}

	
	
	/**
	 * 自动交易API通过accesskey获取提现记录
	 * param accessKey
	 * @return
	 */
	@Page(Viewer = JSON)
	public void getWithdrawRecord(){
		try{
			//签名验证
			Object flag = verifiUtil.validateAuthAccess(request, ip());
			if(flag instanceof SystemCode){
				WriteMsg3(((SystemCode)flag).getKey(), ((SystemCode) flag).getValue(), false, "");
				return;
			}else{
				if(!((Boolean)flag)){
					WriteMsg3(SystemCode.code_1003.getKey(), SystemCode.code_1003.getValue(), false, "");
					return;
				}
			}
			String key = URLDecoder.decode(param("key") , "utf-8");
			String currency = URLDecoder.decode(param("currency") , "utf-8");
			int pageIndex = intParam("pageIndex");
			int pageSize = intParam("pageSize");

			if(key.isEmpty()){
				WriteMsg(SystemCode.code_3005.getKey(), SystemCode.code_3005.getClassName());;	//
				return;
			}
			//btcUserBean btcuser = TradeService.getBtcUser(key);
			ApiKey apiKey = apiKeyDao.findByKey(key);
			if(apiKey != null){
				if(apiKey.getIsAct() == 0 ||apiKey.getIsLock()==1){
					WriteMsg3(SystemCode.code_4001.getKey(), SystemCode.code_4001.getValue(), false, "");
					return;
				}
				if( !"".equals(currency) && DatabasesUtil.getCoinPropMaps().containsKey(currency) ){
					//int userId = Integer.parseInt( Long.valueOf(btcuser.getUserId()).toString() );
					DownloadDao bdDao = new DownloadDao();
					bdDao.setCoint(DatabasesUtil.getCoinPropMaps().get(currency));
					com.alibaba.fastjson.JSONObject json = bdDao.getRecord(apiKey.getUserId(), pageIndex, pageSize);
					WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getValue(), true, json.toJSONString());
					return;
				}else{
					WriteMsg3(SystemCode.code_3005.getKey(), SystemCode.code_3005.getValue(), false, "");
				}
			}else{
				//WriteMsg(SystemCode.code_3004.getKey(), "{" + SystemCode.code_3004.getClassName() + "}");;	//不存在该用户
				WriteMsg3(SystemCode.code_3004.getKey(), SystemCode.code_3004.getValue(), false, "");
				return;
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			WriteMsg3(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue(), false, "");
		}
	}

	
	
	/**
	 * 自动交易API通过accesskey获取充值记录
	 * param accessKey
	 * @return
	 */
	@Page(Viewer = JSON)
	public void getChargeRecord(){
		try{
			//签名验证
			Object flag = verifiUtil.validateAuthAccess(request, ip());
			if(flag instanceof SystemCode){
				WriteMsg3(((SystemCode)flag).getKey(), ((SystemCode) flag).getValue(), false, "");
				return;
			}else{
				if(!((Boolean)flag)){
					WriteMsg3(SystemCode.code_1003.getKey(), SystemCode.code_1003.getValue(), false, "");
					return;
				}
			}
			String key = URLDecoder.decode(param("key") , "utf-8");
			String currency = URLDecoder.decode(param("currency") , "utf-8");
			int pageIndex = intParam("pageIndex");
			int pageSize = intParam("pageSize");

			if(key.isEmpty()){
				WriteMsg(SystemCode.code_3005.getKey(), SystemCode.code_3005.getClassName());;	//
				return;
			}
			//btcUserBean btcuser = TradeService.getBtcUser(key);
			ApiKey apiKey = apiKeyDao.findByKey(key);
			if(apiKey != null){
				if(apiKey.getIsAct() == 0 || apiKey.getIsLock()==1){
					WriteMsg3(SystemCode.code_4001.getKey(), SystemCode.code_4001.getValue(), false, "");
					return;
				}
				if( !"".equals(currency) && DatabasesUtil.getCoinPropMaps().containsKey(currency) ){
					DetailsDao detailsDao = new DetailsDao(lan);
					detailsDao.setCoint(DatabasesUtil.getCoinPropMaps().get(currency));
					com.alibaba.fastjson.JSONObject json = detailsDao.getChargeRecord(apiKey.getUserId(), pageIndex, pageSize, -1);
					WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getValue(), true, json.toJSONString());
					return;
				}else{
					WriteMsg3(SystemCode.code_3005.getKey(), SystemCode.code_3005.getValue(), false, "");
				}
			}else{
				//WriteMsg(SystemCode.code_3004.getKey(), "{" + SystemCode.code_3004.getClassName() + "}");;	//不存在该用户
				WriteMsg3(SystemCode.code_3004.getKey(), SystemCode.code_3004.getValue(), false, "");
				return;
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			WriteMsg3(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue(), false, "");
		}
	}
	
	/**
	 * 关闭用户API访问状态
	 * param accessKey
	 * @return
	 */
	@Page(Viewer = JSON)
	public void closeUserAutoApi(){
		try{
			//签名验证
			Object flag = verifiUtil.validateAuthAccess(request, ip());
			if(flag instanceof SystemCode){
				WriteMsg3(((SystemCode)flag).getKey(), ((SystemCode) flag).getValue(), false, "");
				return;
			}else{
				if(!((Boolean)flag)){
					WriteMsg3(SystemCode.code_1003.getKey(), SystemCode.code_1003.getValue(), false, "");
					return;
				}
			}
			String key = URLDecoder.decode(param("key") , "utf-8");
			if(key.isEmpty()){
				WriteMsg(SystemCode.code_3005.getKey(), SystemCode.code_3005.getClassName());;	//
				return;
			}
			ApiKey apiKey  = apiKeyDao.findByKey(key);
			if(apiKey == null){
				WriteMsg(SystemCode.code_3004.getKey(), SystemCode.code_3004.getClassName());;	//不存在该用户
				return;
			}
			//关闭API交易接口
			int result = apiKeyDao.closeApiKey(apiKey.getUserId());
			if(result>0){
				//关闭之后，发条短信通知用户
				 MobileDao mDao = new MobileDao();
				 UserDao userDao = new UserDao();
				 User user = userDao.get(apiKey.getUserId()+"");
				 mDao.sendSms(user, ip(), L("关闭API自动交易"), L("尊敬的用户，由于您账户[%%]访问API接口过于频繁，暂时关闭您的API接口，如需开通请登录btcwinex.com，不便之处请见谅!",user.getUserName()), user.getUserContact().getSafeMobile());
			}

			WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getValue(), true, "");
		} catch (Exception e) {
			log.error("内部异常", e);
			WriteMsg3(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue(), false, "");
		}
	}

	
	
	/**
	 * 自动交易API通过accesskey进行提现操作
	 * param accessKey
	 * @return
	 */
	@Page(Viewer = JSON)
	public void withdraw(){
		try{
			//签名验证
			Object flag = verifiUtil.validateAuthAccess(request, ip());
			if(flag instanceof SystemCode){
				WriteMsg3(((SystemCode)flag).getKey(), ((SystemCode) flag).getValue(), false, "");
				return;
			}else{
				if(!((Boolean)flag)){
					WriteMsg3(SystemCode.code_1003.getKey(), SystemCode.code_1003.getValue(), false, "");
					return;
				}
			}
			String key = URLDecoder.decode(param("key") , "utf-8");
			String currency = URLDecoder.decode(param("currency") , "utf-8");
			BigDecimal amount = decimalParam("amount");
			String receiveAddr = URLDecoder.decode(param("receiveAddr") , "utf-8");
			double fees = doubleParam("fees");
			String payPass = URLDecoder.decode(param("safePwd") , "utf-8");
			//int itransfer = CommonUtil.stringToInt(param("itransfer")); // 是否内部转账

			if(key.isEmpty()){
				WriteMsg(SystemCode.code_3005.getKey(), SystemCode.code_3005.getClassName());;	//
				return;
			}
			ApiKey apiKey  = apiKeyDao.findByKey(key);

			if(apiKey != null){
				if(apiKey.getIsAct() == 0 || apiKey.getIsLock()==1){
					WriteMsg3(SystemCode.code_4001.getKey(), SystemCode.code_4001.getValue(), false, "");
					return;
				}
				//int userId = Integer.parseInt( Long.valueOf(btcuser.getUserId()).toString() );
			 	User user = userDao.findOne("_id",apiKey.getUserId());
				String ip = ip();
				if(!"".equals(currency) && DatabasesUtil.getCoinPropMaps().containsKey(currency)){
					DownloadDao  downloadDao = new DownloadDao();
					downloadDao.setCoint(DatabasesUtil.getCoinPropMaps().get(currency));
					Message msg = downloadDao.doBtcDownload(user, amount, receiveAddr, coint.getMinFees(), payPass, "", 0, ip, "", getLanTag(), true, false, null, lan, null,"");
					if (msg.isSuc()) {
						WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getValue(), true, "{\"downloadId\":"+msg.getData()+"}");
					} else {
						WriteMsg3(msg.getCode(), msg.getMsg(), true, "");
					}
					return;
				}else{
					WriteMsg3(SystemCode.code_3005.getKey(), SystemCode.code_3005.getValue(), false, "");
				}
			}else{
				//WriteMsg(SystemCode.code_3004.getKey(), "{" + SystemCode.code_3004.getClassName() + "}");;	//不存在该用户
				WriteMsg3(SystemCode.code_3004.getKey(), SystemCode.code_3004.getValue(), false, "");
				return;
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			WriteMsg3(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue(), false, "");
		}
	}

	
	/**
	 * 自动交易API通过accesskey进行获取白名单列表
	 * param accessKey
	 * @return
	 */
	@Page(Viewer = JSON)
	public void getWhiteIp(){
		try{
			
			JSONArray jArray  = new JSONArray();
			WhiteIpDao dao = new WhiteIpDao();
			Query<WhiteIp> q = dao.getQuery(WhiteIp.class);
			List<WhiteIp> dataList = q.asList();
			if(dataList!=null && !dataList.isEmpty()){
				for(WhiteIp whiteIp:dataList){
					jArray.add(whiteIp);
				}
			}
			WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getValue(), true, jArray.toJSONString());
		} catch (Exception e) {
			log.error("内部异常", e);
			WriteMsg3(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue(), false, "");
		}
	}
	
	/**
	 * 发送用户短信
	 * param userIds
	 */
	@Page(Viewer = JSON)
	public void sendSms(){
		try{
			//签名验证
			Object flag = verifiUtil.validateAuthAccess(request, ip());
			if(flag instanceof SystemCode){
				WriteMsg3(((SystemCode)flag).getKey(), ((SystemCode) flag).getValue(), false, "");
				return;
			}else{
				if(!((Boolean)flag)){
					WriteMsg3(SystemCode.code_1003.getKey(), SystemCode.code_1003.getValue(), false, "");
					return;
				}
			}

			String userIds = URLDecoder.decode(param("userIds") , "utf-8");
			String content = URLDecoder.decode(param("content") , "utf-8");

			String[] arrs = userIds.split(",");
			MobileDao mDao = new MobileDao();
			JSONArray jsonArray = new JSONArray();
			for (String userId : arrs) {
				if(StringUtils.isNotEmpty(userId)){
					User user = verifiUtil.getUserById(userId);
					if(user != null){
						UserContact uc = user.getUserContact();
						if(StringUtils.isNotEmpty(uc.getSafeMobile())){
							mDao.sendSms(user, "", "", L(content), uc.getSafeMobile());
							//jsonArray.add(JSONObject.fromObject(TradeService.getSendSmsCallBackMsg(SystemCode.code_1000.getKey(), userId, SystemCode.code_1000.getValue())));
						}else{
							//jsonArray.add(JSONObject.fromObject(TradeService.getSendSmsCallBackMsg(SystemCode.code_1001.getKey(), userId, "该用户尚未验证手机号!")));
						}
					}else{
						//jsonArray.add(JSONObject.fromObject(TradeService.getSendSmsCallBackMsg(SystemCode.code_3004.getKey(), userId, SystemCode.code_3004.getValue())));
					}
				}
			}

			WriteMsg3(SystemCode.code_1000.getKey(), SystemCode.code_1000.getValue(), true, jsonArray.toString());
		} catch (Exception e) {
			log.error("内部异常", e);
			WriteMsg3(SystemCode.code_1002.getKey(), SystemCode.code_1002.getValue(), false, "");
		}
	}









	//***********************************************************************  币币APP  **************************************************************************************//

	/**
	 * 修改登录密码验证
	 */
	@Page(Viewer = JSON)
	public void updLoginPwdCheck() {

		try {
			setLan();
			initLoginUser();
			String userId = userIdStr();
			String token = param("token");
			if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
				json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
				return;
			}
			//校验原登录密码
			DataResponse dr1 = this.checkVerifiCode(userId, MsgToastKey.UPD_LOGIN_PWD, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr1.isSuc()) {
				json(SystemCode.code_1001, L(dr1.getDes()),"");
				return;
			}
			//校验原登录密码
			DataResponse dr2 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr2.isSuc()) {
				json(SystemCode.code_1001, L(dr2.getDes()),"");
				return;
			}
			//校验邮箱密码
			DataResponse dr3 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
			if (!dr3.isSuc()) {
				json(SystemCode.code_1001, L(dr3.getDes()),"");
				return;
			}
			//校验手机验证码
			DataResponse dr4 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_MOBILE,MsgToastKey.LOCK_24_HOUR);
			if (!dr4.isSuc()) {
				json(SystemCode.code_1001, L(dr4.getDes()),"");
				return;
			}
			//校验谷歌验证码
			DataResponse dr5 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_GOOGLE,MsgToastKey.LOCK_24_HOUR);
			if (!dr5.isSuc()) {
				json(SystemCode.code_1001, L(dr5.getDes()),"");
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

			if(StringUtils.isBlank(loginPwd)){
				json(SystemCode.code_1001, L("请输入原登录密码。"),"");
				return;
			}else if(StringUtils.isBlank(emailCode)){
				json(SystemCode.code_1001, L("请输入邮箱验证码。"),"");
				return;
			}else if("0".equals(safeVerifyType) && StringUtils.isBlank(googleCode)){
				json(SystemCode.code_1001, L("请输入谷歌验证码。"),"");
				return;
			}else if("1".equals(safeVerifyType) && StringUtils.isBlank(smsCode)){
				json(SystemCode.code_1001, L("请输入手机验证码。"),"");
				return;
			}
			byte[] decodedData2 = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(loginPwd.replace(" ", "+")),priKey);
			loginPwd = new String(decodedData2);
			msg = doCheck(userId, loginPwd, emailCode, true,MsgToastKey.UPD_LOGIN_PWD,MsgToastKey.LOCK_24_HOUR);
			if(StringUtils.isNotEmpty(msg)){
				json(SystemCode.code_1001, L(msg),"");
				return;
			}else{
				String checkToken = EncryptionPhoto.getToken(Const.function_upd_login_pwd,userId);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("checkToken",checkToken);
				json(SystemCode.code_1000, "",jsonObject.toJSONString());
				return;
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			json(SystemCode.code_1001, L("内部异常"),"");
		}

	}
	/**2,修改登录密码（点击下一步调用）
	 * 修改登录密码验证
	 */
	@Page(Viewer = JSON)
	public void updLoginPwdCheckTwo() {

		try {
			setLan();
			initLoginUser();
			String userId = userIdStr();
			String token = param("token");
			if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
				json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
				return;
			}
			//校验原登录密码
			DataResponse dr1 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr1.isSuc()) {
				json(SystemCode.code_1001, dr1.getDes(), "");
				return;
			}
			//校验原登录密码
			DataResponse dr2 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr2.isSuc()) {
				json(SystemCode.code_1001, dr2.getDes(), "");
				return;
			}
			//校验邮箱密码
			DataResponse dr3 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
			if (!dr3.isSuc()) {
				json(SystemCode.code_1001, dr3.getDes(), "");
				return;
			}
			//校验手机验证码
			DataResponse dr4 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_MOBILE,MsgToastKey.LOCK_24_HOUR);
			if (!dr4.isSuc()) {
				json(SystemCode.code_1001, dr4.getDes(), "");
				return;
			}
			//校验谷歌验证码
			DataResponse dr5 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_GOOGLE,MsgToastKey.LOCK_24_HOUR);
			if (!dr5.isSuc()) {
				json(SystemCode.code_1001, dr5.getDes(), "");
				return;
			}
			//提示信息
			String msg = "";
			//谷歌验证码
			String googleCode = param("gcode");
			//手机验证码
			String smsCode = param("smscode");
			//安全验证：0-谷歌，1-手机
			String safeVerifyType = param("selectedCode");

			if(StringUtils.isNotEmpty(googleCode)){
				safeVerifyType = "0";
			}
			if(StringUtils.isNotEmpty(smsCode)){
				safeVerifyType = "1";
			}
			if("0".equals(safeVerifyType) && StringUtils.isBlank(googleCode)){
				json(SystemCode.code_1001, L("请输入谷歌验证码。"), "");
				return;
			}else if("1".equals(safeVerifyType) && StringUtils.isBlank(smsCode)){
				json(SystemCode.code_1001, L("请输入手机验证码。"), "");
				return;
			}
			String uftGoogle = String.valueOf(ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_GOOGLE.getKey());
			String googleMsgToast = "验证码输入次数超出限制，将锁定修改登录密码功能，请24小时之后再试。";
			msg = doCheckTwo(userId, googleCode,smsCode,PostCodeType.appUpdateLoginPasswordByMoblie,LimitType.LoginMobilePassError,uftGoogle,safeVerifyType, MsgToastKey.UPD_LOGIN_PWD,MsgToastKey.LOCK_24_HOUR,googleMsgToast);
			if(StringUtils.isNotEmpty(msg)){
				json(SystemCode.code_1001, msg, "");
				return;
			}else{
				json(SystemCode.code_1000, "","");
				return;
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			json(SystemCode.code_1002, L(" 内部异常"), "");
			return;
		}

	}
	/**
	 * 修改密码，否则返回false
	 */
	@Page(Viewer = JSON)
	public void logUpdate() {

		try {
			setLan();
			initLoginUser();
			String userId = userIdStr();
			String token = param("token");
			if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
				json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
				return;
			}
			//校验原登录密码
			DataResponse dr1 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr1.isSuc()) {
				json(SystemCode.code_1001, dr1.getDes(),"");
				return;
			}
			//校验原登录密码
			DataResponse dr2 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr2.isSuc()) {
				json(SystemCode.code_1001, dr2.getDes(),"");
				return;
			}
			//校验邮箱密码
			DataResponse dr3 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
			if (!dr3.isSuc()) {
				json(SystemCode.code_1001, dr3.getDes(),"");
				return;
			}
			//校验手机验证码
			DataResponse dr4 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_MOBILE,MsgToastKey.LOCK_24_HOUR);
			if (!dr4.isSuc()) {
				json(SystemCode.code_1001, dr4.getDes(),"");
				return;
			}
			//校验谷歌验证码
			DataResponse dr5 = this.checkVerifiCode(userId,MsgToastKey.UPD_LOGIN_PWD,ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_GOOGLE,MsgToastKey.LOCK_24_HOUR);
			if (!dr5.isSuc()) {
				json(SystemCode.code_1001, dr5.getDes(),"");
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

			byte[] decodedData = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(surePwd.replace(" ", "+")),priKey);
			surePwd = new String(decodedData);

			byte[] decodedData2 = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(password.replace(" ", "+")),priKey);
			password = new String(decodedData2);

			if (!password.equals(surePwd)) {
				json(SystemCode.code_1001, L("两次密码输入不一致。"),"");
				return;
			}

			if (password.length() < 8) {
				json(SystemCode.code_1001, L("登录密码的长度不能少于8位。"),"");
				return;
			}

			//密码验证
			String regex = "^(?![A-Za-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{8,20}$";    //密码的组成至少要包括大小写字母、数字及标点符号的其中两项
			if (!password.matches(regex)) {
				json(SystemCode.code_1001, L("您的密码需要8-20位，包含字母，数字，符号的两种以上。"),"");
				return;
			}

			User user = userDao.findOne("_id",userId);

			LimitType lt = LimitType.SafePassError;


			if(!noPwd){
				if(lt.GetStatus(userId) == -1){
					json(SystemCode.code_1001, L("密码已锁定"),"");
					return;
				}

//				String oldPwd = user.getPwd();
//				if(oldPwd.equals(user.getEncryptedPwd(password))){
//					json(SystemCode.code_1001, L("修改后的密码不能和原密码一致。"),"");
//					return;
//				}
			}

			//modify by xwz 20171220 资金密码不能和登录密码一致
			if(user.getEncryptedPwd(password).equals(user.getSafePwd())){
				json(SystemCode.code_1001, L("登录密码不得与资金密码一致。"),"");
				return;
			}
			//防跳步
			String checkToken = param("checkToken");
			boolean flg = EncryptionPhoto.checkToken(Const.function_upd_login_pwd,userId,checkToken);
			if(!flg){
				json(SystemCode.code_1001, L("非法操作"),"");
				return;
			}
			int pwdLevel = intParam("pwdLevel");

			UpdateResults<User> ur = userDao.updatePwd(userId, password, pwdLevel);

			if (!ur.getHadError()) {
				Cache.Delete(Const.function_upd_login_pwd + userId);
				lt.ClearStatus(userId);
				logDao.insertOneRecord(AuthenType.modifyPwd.getKey(), userId()+"", "0", "成功修改登录密码。", ip());
				json(SystemCode.code_1000, L("修改成功"),"");
				return;
			} else {
				json(SystemCode.code_1001, L("修改失败"),"");
				return;
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			json(SystemCode.code_1001, L(" 内部异常"),"");
			return;
		}
	}
	/**
	 * check登录密码或资金密码通用方法
	 * @param userId
	 * @param loginPwd
	 * @param emailCode
	 * @param googleCode
	 * @param safeVerifyType
	 * @param isUpdLoginPwd
	 */
//	private String doCheck(String userId, String loginPwd, String emailCode, String googleCode,String smsCode, String safeVerifyType, boolean isUpdLoginPwd,String functionName,String lockTime) {
//		//校验登录密码
//		String uftLoginPwd = null;
//		//校验谷歌
//		String uftGoogle = null;
//		//邮箱锁定校验
//		LimitType ltEmail = null;
//		//手机锁定校验
//		LimitType ltMobile = null;
//		//邮件typeCode
//		PostCodeType pct = null;
//		//短信typeCode
//		PostCodeType pctMobile = null;
//		//超过三次限制提示
//		String googleMsgToast = "";
//		String loginPwdMsgToast = "";
//		//修改登录密码
//		if (isUpdLoginPwd) {
//			uftLoginPwd = String.valueOf(ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_PWD.getKey());
//			uftGoogle = String.valueOf(ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_GOOGLE.getKey());
//			ltEmail = LimitType.LoginEmailPassError;
//			ltMobile = LimitType.LoginMobilePassError;
//			pct = PostCodeType.updateLoginPassword;
//			pctMobile = PostCodeType.updateLoginPasswordByMoblie;
//			googleMsgToast = "验证码输入次数超出限制，将锁定修改登录密码功能，请24小时之后再试。";
//			loginPwdMsgToast = "原登录密码输入次数超出限制，将锁定修改登录密码功能，请24小时之后再试。";
//		} else {
//			//修改资金密码
//			uftLoginPwd = String.valueOf(ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD.getKey());
//			uftGoogle = String.valueOf(ConstantCenter.UpdFunctionType.UPD_PAY_PWD_GOOGLE.getKey());
//			ltEmail = LimitType.PayEmailPassError;
//			ltMobile = LimitType.PayMobilePassError;
//			pct = PostCodeType.updateSafePassword;
//			pctMobile = PostCodeType.updateSafePasswordByMobile;
//			googleMsgToast = "验证码输入次数超出限制，将锁定重置资金密码及提现功能，请24小时之后再试。";
//			loginPwdMsgToast = "登录密码输入次数超出限制，将锁定提现、修改登录密码、重置资金密码功能，请24小时之后再试。";
//		}
//
//		String url = ApiConfig.getValue("usecenter.url");
//		FeignContainer container = new FeignContainer(url.concat("/user"));
//		UserApiService userApiService = container.getFeignClient(UserApiService.class);
//		Map<String, String> map = userApiService.checkLoginPwdApiN(userId, loginPwd, uftLoginPwd);
//		User user = userDao.getById(userId);
//		if (null != map) {
//			for (Map.Entry<String, String> entry : map.entrySet()) {
//				if ("1".equals(entry.getKey())) {
//
//				} else {
//					String returnVal = entry.getValue();
//					if ("-2".equals(returnVal)) {
//						returnVal = L(loginPwdMsgToast);
//						EmailDao eDao = new EmailDao();
//						String info = "";
//						String title = "";
//						if (isUpdLoginPwd) {
//							//修改登录密码时被锁定发送邮件
//							info = eDao.getWrongLimitEmailHtml(user, "您在操作修改登录密码功能时，原登录密码输入次数超出限制，为了您的账号安全，XX将锁定修改登录密码功能，请24小时之后再试。", this);
//							title = L("锁定修改登录密码");
//						} else {
//							//重置资金密码时被锁定发送邮件
//							info = eDao.getWrongLimitEmailHtml(user, "您在操作重置资金密码功能时，原登录密码输入次数超出限制，为了您的账号安全，XX将锁定重置资金密码、修改登录密码和提现功能，请24小时之后再试。", this);
//							title = L("锁定重置资金密码");
//						}
//						//锁定发送邮件
//						eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, user.getUserContact().getSafeEmail());
//					} else {
//						if (!isUpdLoginPwd) {
//							returnVal = returnVal.replace("原登录密码", "登录密码");
//						}
//						String[] errorMsg = returnVal.split("#");
//						returnVal = L(errorMsg[0], errorMsg[1]);
//					}
//					return returnVal;
//				}
//			}
//		}
//		//邮箱验证
//		DataResponse dr = getDataResponse(user, pct, user.getEmail(), 1, emailCode, ltEmail, functionName, lockTime);
//		if (!dr.isSuc()) {
//			return dr.getDes();
//		}
//		if (!"-1".equals(safeVerifyType)) {
//			//谷歌校验
//			if ("0".equals(safeVerifyType)) {
//				FeignContainer container1 = new FeignContainer(url.concat("/google"));
//				GoogleApiService googleApiService = container1.getFeignClient(GoogleApiService.class);
//				Map<String, String> map1 = googleApiService.checkGoogleCodeApiN(googleCode, user.getUserContact().getSecret(), userId, uftGoogle);
//				for (Map.Entry<String, String> entry1 : map1.entrySet()) {
//					if ("1".equals(entry1.getKey())) {
//						//TODO 放下一步校验session
//					} else {
//						String returnVal = entry1.getValue();
//						if ("-2".equals(returnVal)) {
//							returnVal = L(googleMsgToast);
//						} else {
////							returnVal = returnVal.replace("谷歌", "");
//							String[] errorMsg = returnVal.split("#");
//							returnVal = L(errorMsg[0], errorMsg[1]);
//						}
//						return returnVal;
//					}
//				}
//			} else if ("1".equals(safeVerifyType)) {
//				//手机验证
//
//				DataResponse dr1 = getDataResponse(user, pctMobile, user.getUserContact().getSafeMobile(), 2, smsCode, ltMobile, functionName, lockTime);
//				if (!dr1.isSuc()) {
//					return dr1.getDes();
//				} else {
//					//TODO 放下一步校验session
//				}
//			}
//		}
//		return "";
//	}
	/**
	 * check登录密码或资金密码通用方法
	 * @param userId
	 * @param loginPwd
	 * @param emailCode
	 * @param isUpdLoginPwd
	 */
	private String doCheck(String userId, String loginPwd, String emailCode, boolean isUpdLoginPwd,String functionName,String lockTime) {
		//校验登录密码
		String uftLoginPwd = null;
		//邮箱锁定校验
		LimitType ltEmail = null;
		//邮件typeCode
		PostCodeType pct = null;

		//超过三次限制提示
		String loginPwdMsgToast = "";
		//修改登录密码
		if (isUpdLoginPwd) {
			uftLoginPwd = String.valueOf(ConstantCenter.UpdFunctionType.UPD_LOGIN_PWD_PWD.getKey());
			ltEmail = LimitType.LoginEmailPassError;
			pct = PostCodeType.appUpdateLoginPassword;
			loginPwdMsgToast = "登录密码输入次数超出限制，将锁定修改登录密码功能，请24小时之后再试。";
		} else {
			//修改资金密码
			uftLoginPwd = String.valueOf(ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD.getKey());
			ltEmail = LimitType.PayEmailPassError;
			pct = PostCodeType.appupdateSafePassword;
			loginPwdMsgToast = "登录密码输入次数超出限制，将锁定提现、修改登录密码、重置资金密码功能，请24小时之后再试。";
		}

		String url = ApiConfig.getValue("usecenter.url");
		FeignContainer container = new FeignContainer(url.concat("/user"));
		UserApiService userApiService = container.getFeignClient(UserApiService.class);
		Map<String, String> map = userApiService.checkLoginPwdApiN(userId, loginPwd, uftLoginPwd);
		User user = userDao.findOne("_id",userId);
		if (null != map) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				if ("1".equals(entry.getKey())) {

				} else {
					String returnVal = entry.getValue();
					if ("-2".equals(returnVal)) {
						returnVal = L(loginPwdMsgToast);
						EmailDao eDao = new EmailDao();
						String info = "";
						String title = "";
						if (isUpdLoginPwd) {
							//修改登录密码时被锁定发送邮件
							info = eDao.getWrongLimitEmailHtml(user, "您在操作修改登录密码功能时，登录密码输入次数超出限制，为了您的账号安全，XX将锁定修改登录密码功能，请24小时之后再试。", this);
							title = L("锁定修改登录密码");
						} else {
							//重置资金密码时被锁定发送邮件
							info = eDao.getWrongLimitEmailHtml(user, "您在操作重置资金密码功能时，登录密码输入次数超出限制，为了您的账号安全，XX将锁定重置资金密码、修改登录密码和提现功能，请24小时之后再试。", this);
							title = L("锁定重置资金密码");
						}
						//锁定发送邮件
						eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, user.getUserContact().getSafeEmail());
					} else {
//						if (!isUpdLoginPwd) {
							returnVal = returnVal.replace("原登录密码", "登录密码");
//						}
						String[] errorMsg = returnVal.split("#");
						if(errorMsg.length == 2){
							returnVal = L(errorMsg[0], errorMsg[1]);
						}else{
							returnVal = L(errorMsg[0]);
						}
					}
					return returnVal;
				}
			}
		}
		//邮箱验证
		DataResponse dr = getDataResponse(user, pct, user.getEmail(), 1, emailCode, ltEmail, functionName, lockTime);
		if (!dr.isSuc()) {
			return dr.getDes();
		}
		return "";
	}

	/**
	 * check登录密码或资金密码通用方法
	 * @param userId
	 * @param googleCode
	 * @param safeVerifyType
	 */
	private String doCheckTwo(String userId,String googleCode,String smsCode, PostCodeType pctMobile,LimitType ltMobile,String uftGoogle,String safeVerifyType,String functionName,String lockTime,String googleMsgToast) {
		//超过三次限制提示
		String url = ApiConfig.getValue("usecenter.url");
		User user = userDao.findOne("_id",userId);
		if (!"-1".equals(safeVerifyType)) {
			//谷歌校验
			if ("0".equals(safeVerifyType)) {
				FeignContainer container1 = new FeignContainer(url.concat("/google"));
				GoogleApiService googleApiService = container1.getFeignClient(GoogleApiService.class);
				Map<String, String> map1 = googleApiService.checkGoogleCodeApiN(googleCode, user.getUserContact().getSecret(), userId, uftGoogle);
				for (Map.Entry<String, String> entry1 : map1.entrySet()) {
					if ("1".equals(entry1.getKey())) {
						//TODO 放下一步校验session
					} else {
						String returnVal = entry1.getValue();
						if ("-2".equals(returnVal)) {
							returnVal = L(googleMsgToast);
						} else {
							returnVal = returnVal.replace("谷歌", "");
							String[] errorMsg = returnVal.split("#");
							if(errorMsg.length == 2){
								returnVal = L(errorMsg[0], errorMsg[1]);
							}else{
								returnVal = L(errorMsg[0]);
							}
						}
						return returnVal;
					}
				}
			} else if ("1".equals(safeVerifyType)) {
				//手机验证
				DataResponse dr1 = getDataResponse(user, pctMobile, user.getUserContact().getSafeMobile(), 2, smsCode, ltMobile, functionName, lockTime);
				if (!dr1.isSuc()) {
					return dr1.getDes();
				}
			}
		}
		return "";
	}

	/**
	 * check手机或邮箱验证码
	 * @param user
	 * @param pct
	 * @param sendType 1-邮箱验证，2-手机验证
	 * @param code
	 * @return
	 */
	private DataResponse getDataResponse(User user,PostCodeType pct,String sendNum,int sendType,String code,LimitType lt,String functionName,String lockTime) {
		DataResponse dr = null;
		String userIp = ip();
		PostCodeType postCodeType = (PostCodeType) EnumUtils.getEnumByKey(pct.getKey(), PostCodeType.class);
		String codeType = postCodeType.getValue();
		if(sendType == 1){
			ClientSession clientSession = new ClientSession(userIp, sendNum, lan, codeType, false);
			dr = clientSession.checkCodeMailApp(user.getId(),code,lt,functionName,lockTime);
		}else{
			ClientSession clientSession = new ClientSession(userIp, sendNum, lan, codeType, false);
			dr = clientSession.checkCodeApp(user.getId(),code,lt,functionName,lockTime);
		}
		EmailDao eDao = new EmailDao();
		String info = "";
		String title = "锁定{}";
		if("cn".equals(lan)){
			if(dr.getDes().contains("验证码输入次数超出限制")){
				title = StrUtil.format(title,functionName);
				info = eDao.getWrongLimitEmailHtml(user, dr.getDes(), this);
				//锁定发送邮件
				eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, user.getUserContact().getSafeEmail());
			}
		}else if("en".equals(lan)){
			if(dr.getDes().contains("Code input error too many times in a row")){
				title = StrUtil.format(title,functionName);
				info = eDao.getWrongLimitEmailHtml(user, dr.getDes(), this);
				//锁定发送邮件
				eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, user.getUserContact().getSafeEmail());
			}
		}else if("hk".equals(lan)){
			if(dr.getDes().contains("驗證碼輸入次數超出限制")){
				title = StrUtil.format(title,functionName);
				info = eDao.getWrongLimitEmailHtml(user, dr.getDes(), this);
				//锁定发送邮件
				eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, user.getUserContact().getSafeEmail());
			}
		}else if("jp".equals(lan)){
			if(dr.getDes().contains("認証コードの入力回数が制限を超え")){
				title = L(functionName);
				info = eDao.getWrongLimitEmailHtml(user, dr.getDes(), this);
				//锁定发送邮件
				eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, user.getUserContact().getSafeEmail());
			}
		}else if("kr".equals(lan)){
			if(dr.getDes().contains("인증 번호를 잘못")){
				title = L(functionName);
				info = eDao.getWrongLimitEmailHtml(user, dr.getDes(), this);
				//锁定发送邮件
				eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, user.getUserContact().getSafeEmail());
			}
		}

		return dr;
	}

	/**
	 * 修改资金密码验证，返回跳步的token
	 */
	@Page(Viewer = JSON)
	public void updPayPwdCheck() {
		try {
			setLan();
			String pwdParamKey = "lpwd";
			initLoginUser();
			String userId = userIdStr();
			String token = param("token");
			if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
				json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
				return;
			}
			LimitType lt = LimitType.SafePassEntrustError;
			int entrustStatus = lt.GetStatus(loginUser.get_Id());
			if (entrustStatus == -1) {
				json(SystemCode.code_1001, L("资金密码输入错误超出限制，锁定该帐户24小时，不得使用提现功能"),"");
				return;
			}
			//校验提现资金密码
			DataResponse dr1 = this.checkVerifiCode(userId, MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.WITHDRAWAL_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr1.isSuc()) {
				json(SystemCode.code_1001, dr1.getDes(),"");
				return;
			}
			//校验原登录密码
			DataResponse dr2 = this.checkVerifiCode(userId, MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr2.isSuc()) {
				json(SystemCode.code_1001, dr2.getDes(),"");
				return;
			}
			//校验邮箱密码
			DataResponse dr3 = this.checkVerifiCode(userId, MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
			if (!dr3.isSuc()) {
				json(SystemCode.code_1001, dr3.getDes(),"");
				return;
			}
			//校验手机验证码
			DataResponse dr4 = this.checkVerifiCode(userId, MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_MOBILE, MsgToastKey.LOCK_24_HOUR);
			if (!dr4.isSuc()) {
				json(SystemCode.code_1001, dr4.getDes(),"");
				return;
			}
			//校验谷歌验证码
			DataResponse dr5 = this.checkVerifiCode(userId, MsgToastKey.UPD_PAY_PWD, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_GOOGLE, MsgToastKey.LOCK_24_HOUR);
			if (!dr5.isSuc()) {
				json(SystemCode.code_1001, dr5.getDes(),"");
				return;
			}
			//otc发布广告资金密码输错
			DataResponse dr6 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.UPD_PAY_PWD,ConstantCenter.UpdFunctionType.OTC_CAD_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr6.isSuc()) {
				json(SystemCode.code_1001, dr6.getDes(),"");
				return;
			}
			//otc发布广告资金密码输错
			DataResponse dr7 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.UPD_PAY_PWD,ConstantCenter.UpdFunctionType.OTC_RELEASECOIN_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr7.isSuc()) {
				json(SystemCode.code_1001, dr7.getDes(),"");
				return;
			}

			//提示信息
			String msg = "";
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
			if (StringUtils.isBlank(loginPwd)) {
				json(SystemCode.code_1001, L("请输入原登录密码。"),"");
				return;
			}
			if (StringUtils.isBlank(emailCode)) {
				json(SystemCode.code_1001, L("请输入邮箱验证码。"),"");
				return;
			}
			if ("0".equals(safeVerifyType) && StringUtils.isBlank(googleCode)) {
				json(SystemCode.code_1001, L("请输入谷歌验证码。"),"");
				return;
			}
			if ("1".equals(safeVerifyType) && StringUtils.isBlank(smsCode)) {
				json(SystemCode.code_1001, L("请输入手机验证码。"),"");
				return;
			}

			loginPwd = this.rsaDecrypt(loginPwd, priKey);

			msg = doCheck(userId, loginPwd, emailCode,false, MsgToastKey.RESET_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if (StringUtils.isNotEmpty(msg)) {
				json(SystemCode.code_1001, L(msg),"");
				return;
			} else {
				String checkToken = EncryptionPhoto.getToken(Const.function_upd_pay_pwd, userId);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("checkToken",checkToken);
				json(SystemCode.code_1000, "",jsonObject.toJSONString());
				return;
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			json(SystemCode.code_1002, L(" 内部异常"), "");
		}
	}
	/**
	 * 6.修改资金密码验证，返回跳步的token
	 */
	@Page(Viewer = JSON)
	public void updPayPwdCheckTwo() {
		try {
			setLan();
			initLoginUser();
			String userId = userIdStr();
			String token = param("token");
			if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
				json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
				return;
			}
			LimitType lt = LimitType.SafePassEntrustError;
			int entrustStatus = lt.GetStatus(loginUser.get_Id());
			if (entrustStatus == -1) {
				json(SystemCode.code_1001, L("资金密码输入错误超出限制，锁定该帐户24小时，不得使用提现功能"),"");
				return;
			}
			//校验提现资金密码
			DataResponse dr1 = this.checkVerifiCode(userId,MsgToastKey.UPD_PAY_PWD,ConstantCenter.UpdFunctionType.WITHDRAWAL_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr1.isSuc()) {
				json(SystemCode.code_1001, dr1.getDes(),"");
				return;
			}
			//校验原登录密码
			DataResponse dr2 = this.checkVerifiCode(userId,MsgToastKey.UPD_PAY_PWD,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr2.isSuc()) {
				json(SystemCode.code_1001, dr2.getDes(),"");
				return;
			}
			//校验邮箱密码
			DataResponse dr3 = this.checkVerifiCode(userId,MsgToastKey.UPD_PAY_PWD,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
			if (!dr3.isSuc()) {
				json(SystemCode.code_1001, dr3.getDes(),"");
				return;
			}
			//校验手机验证码
			DataResponse dr4 = this.checkVerifiCode(userId,MsgToastKey.UPD_PAY_PWD,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_MOBILE,MsgToastKey.LOCK_24_HOUR);
			if (!dr4.isSuc()) {
				json(SystemCode.code_1001, dr4.getDes(),"");
				return;
			}
			//校验谷歌验证码
			DataResponse dr5 = this.checkVerifiCode(userId,MsgToastKey.UPD_PAY_PWD,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_GOOGLE,MsgToastKey.LOCK_24_HOUR);
			if (!dr5.isSuc()) {
				json(SystemCode.code_1001, dr5.getDes(),"");
				return;
			}

			//otc发布广告资金密码输错
			DataResponse dr6 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.UPD_PAY_PWD,ConstantCenter.UpdFunctionType.OTC_CAD_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr6.isSuc()) {
				json(SystemCode.code_1001, dr6.getDes(),"");
				return;
			}
			//otc发布广告资金密码输错
			DataResponse dr7 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.UPD_PAY_PWD,ConstantCenter.UpdFunctionType.OTC_RELEASECOIN_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr7.isSuc()) {
				json(SystemCode.code_1001, dr7.getDes(),"");
				return;
			}
			//提示信息
			String msg = "";
			//谷歌验证码
			String googleCode = param("gcode");
			//手机验证码
			String smsCode = param("smscode");
			//安全验证：0-谷歌，1-手机
			String safeVerifyType = param("selectedCode");

			if(StringUtils.isNotEmpty(googleCode)){
				safeVerifyType = "0";
			}
			if(StringUtils.isNotEmpty(smsCode)){
				safeVerifyType = "1";
			}
			if("0".equals(safeVerifyType) && StringUtils.isBlank(googleCode)){
				json(SystemCode.code_1001,L("请输入谷歌验证码。"),"");
				return;
			}
			if("1".equals(safeVerifyType) && StringUtils.isBlank(smsCode)){
				json(SystemCode.code_1001,L("请输入手机验证码。"),"");
				return;
			}
			String uftGoogle = String.valueOf(ConstantCenter.UpdFunctionType.UPD_PAY_PWD_GOOGLE.getKey());
			String googleMsgToast = "验证码输入次数超出限制，将锁定重置资金密码及提现功能，请24小时之后再试。";
			msg = doCheckTwo(userId, googleCode,smsCode,PostCodeType.updateSafePasswordByMobile,LimitType.PayMobilePassError, uftGoogle,safeVerifyType, MsgToastKey.RESET_PAY_PWD,MsgToastKey.LOCK_24_HOUR,googleMsgToast);
			if(StringUtils.isNotEmpty(msg)){
				json(SystemCode.code_1001,msg,"");
				return;
			}else{
				json(SystemCode.code_1000, "","");
				return;
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			json(SystemCode.code_1002, L(" 内部异常"), "");
		}
	}

	//设置资金密码，重置资金密码，用的一个接口
	@Page(Viewer = ".xml")
	public void safeUpdate() {
		try {
			setLan();
			log.info("用户："+userName()+"修改资金安全密码IP:"+ip());
			String userId = userIdStr();
			String token = param("token");
			if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
				json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
				return;
			}
			User user = userDao.findOne("_id",userId);
			boolean hasSafePwd = user.getIsSafePwd();


			String functionName = MsgToastKey.SETTING_PAY_PWD;
			if(hasSafePwd){
				functionName = MsgToastKey.UPD_PAY_PWD;
			}

			//校验提现资金密码
			DataResponse dr0 = this.checkVerifiCode(userId,functionName,ConstantCenter.UpdFunctionType.WITHDRAWAL_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr0.isSuc()) {
				json(SystemCode.code_1001, L(dr0.getDes()),"");
				return;
			}

			//校验设置资金密码登录密码
			DataResponse dr1 = this.checkVerifiCode(userId,functionName,ConstantCenter.UpdFunctionType.SET_USER_INFO_LOGINPWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr1.isSuc()) {
				json(SystemCode.code_1001, L(dr1.getDes()),"");
				return;
			}
			//校验原登录密码
			DataResponse dr2 = this.checkVerifiCode(userId,functionName,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr2.isSuc()) {
				json(SystemCode.code_1001, L(dr2.getDes()),"");
				return;
			}
			//校验邮箱密码
			DataResponse dr3 = this.checkVerifiCode(userId,functionName,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
			if (!dr3.isSuc()) {
				json(SystemCode.code_1001, L(dr3.getDes()),"");
				return;
			}
			//校验手机验证码
			DataResponse dr4 = this.checkVerifiCode(userId,functionName,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_MOBILE,MsgToastKey.LOCK_24_HOUR);
			if (!dr4.isSuc()) {
				json(SystemCode.code_1001, L(dr4.getDes()),"");
				return;
			}
			//校验谷歌验证码
			DataResponse dr5 = this.checkVerifiCode(userId,functionName,ConstantCenter.UpdFunctionType.UPD_PAY_PWD_GOOGLE,MsgToastKey.LOCK_24_HOUR);
			if (!dr5.isSuc()) {
				json(SystemCode.code_1001, L(dr5.getDes()),"");
				return;
			}
			//otc发布广告资金密码输错
			DataResponse dr6 = this.checkVerifiCode(String.valueOf(userId), functionName,ConstantCenter.UpdFunctionType.OTC_CAD_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr6.isSuc()) {
				json(SystemCode.code_1001, dr6.getDes(),"");
				return;
			}
			//otc发布广告资金密码输错
			DataResponse dr7 = this.checkVerifiCode(String.valueOf(userId), functionName,ConstantCenter.UpdFunctionType.OTC_RELEASECOIN_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr7.isSuc()) {
				json(SystemCode.code_1001, dr7.getDes(),"");
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
			password = this.rsaDecrypt(password, priKey);
			confirmPwd = this.rsaDecrypt(confirmPwd, priKey);
			//密码验证
			//密码的组成至少要包括大小写字母、数字及标点符号的其中两项
			String regex = "^(?![A-Za-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{8,20}$" ;
			if(!password.matches(regex)){
				json(SystemCode.code_1001, L("您的密码需要8-20位，包含字母，数字，符号的两种以上。"),"");
				return;
			}
			if(!hasSafePwd){
				lpwd = this.rsaDecrypt(lpwd, priKey);
				//校验登录密码
				String uftLoginPwd = String.valueOf(ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD.getKey());
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
							String info = eDao.getWrongLimitEmailHtml(user, "您在操作设置资金密码功能时，登录密码输入次数超出限制，为了您的账号安全，XX将锁定设置资金密码和提现功能，请24小时之后再试。", this);
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
						json(SystemCode.code_1001, returnVal,"");
						return;
					}
				}
			}
			if (StringUtils.isBlank(password)) {
				json(SystemCode.code_1001, L("请输入新资金密码。"),"");
				return;
			}

			if (StringUtils.isBlank(password)) {
				json(SystemCode.code_1001, L("请输入确认密码。"),"");
				return;
			}

			if (confirmPwd.length() < PASSWORD_MIN_LENGTH) {
				json(SystemCode.code_1001, L("资金安全密码的长度不能少于8位。"),"");
				return;
			}

			if (!password.equals(confirmPwd)) {
				json(SystemCode.code_1001, L("您的两次密码输入不一致。"),"");
				return;
			}

			// 验证交易密码是否锁定
			if (checkSafePwdLock(user)) {
				json(SystemCode.code_1001, L("为了您的帐户安全，使用重置资金密码功能修改资金密码后将锁定24小时，在此期间不能进行提现、修改密码等操作，可进行交易操作，请等待24个小时后自动解锁。"),"");
				return;
			}

			//modify by xwz 20171220 资金密码不能和登录密码一致
			if(user.getEncryptedPwd(password).equals(user.getPwd())){
				json(SystemCode.code_1001, L("资金密码不得与登录密码一致。"),"");
				return;
			}

//			if(user.getEncryptedPwd(password).equals(user.getSafePwd())){
//                this.postErrMsg(MsgToastKey.loginPwd, "修改后的密码不能和原密码一致。");
//                return;
//			}

			//防跳步
			if(hasSafePwd){
				String checkToken = param("checkToken");
				boolean flg = EncryptionPhoto.checkToken(Const.function_upd_pay_pwd,userId,checkToken);
				if(!flg){
					json(SystemCode.code_1001, L("非法操作"),"");
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
						userDao.switchTradeAuthen(userDao.findOne("_id",userId),3, ip(),request);
						/*end*/
					} else {
						//删除防跳步操作
						Cache.Delete(Const.function_upd_pay_pwd + userId);
						MobileDao mDao = new MobileDao();
						PostCodeType postCodeType = PostCodeType.resetSafePwd;
						if (null != user.getUserContact().getSafeMobile()) {
							/*start by xzhang 20171031 短信服务临时解决方法，除+86外全发英文*/
							String title = L(postCodeType.getValue());
							String content = L(postCodeType.getDes());
//							if(!MsgUtil.isContain(user.getUserContact().getSafeMobile())){
//								title = Lan.Language("en", postCodeType.getValue());
//								content = Lan.Language("en", postCodeType.getDes());
//							}
							/*end*/
							mDao.sendSms(user, ip(), title, content, user.getUserContact().getSafeMobile());
						}
					}
				} catch (Exception e) {
					log.error("内部异常", e);
				}
				if(hasSafePwd){
					json(SystemCode.code_1000, L("重置成功"),"");
					return;
				}else{
					json(SystemCode.code_1000, L("设置成功"),"");
					return;
				}
			} else {
				json(SystemCode.code_1001, L("操作失败"),"");
				return;
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			WriteError(L(" 内部异常"));
		}
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
	 * 录入手机
	 */
	@Page(Viewer = JSON)
	public void setMobile() {
		setLan();
		initLoginUser();
		String userId = userIdStr();
		String token = param("token");
		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
			json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
			return;
		}
		//check录入手机
		DataResponse checkMobileDr = checkSetMobile(userId);
		if (!checkMobileDr.isSuc()) {
			json(SystemCode.code_1001, checkMobileDr.getDes(),"");
			return;
		}


		User user = userDao.findOne("_id",userId);
		//国家码
		String mCode = param("selectedCode");
		String mobile = param("mobile");
		//手机验证码
		String mobileCode = param("smscode");
		//是否开启手机安全验证，true:1 开启，false:0 关闭
		String isSmsOpen = param("mck");
		String mobileNumber = mobile;
		//手机验证
		PostCodeType pct = PostCodeType.setMobile;
		LimitType lt = LimitType.SetUserInfoMobileError;
		DataResponse dr1 = getDataResponse(user,pct,mobileNumber,2,mobileCode,lt,MsgToastKey.SETTING_MOBILE,MsgToastKey.LOCK_24_HOUR);
		if (!dr1.isSuc()) {
			json(SystemCode.code_1001, L(dr1.getDes()),"");
			return;
		}else{
			//TODO 放下一步校验session
		}
		if(StringUtils.isEmpty(mobile)){
			json(SystemCode.code_1001, L("请输入手机号。"),"");
			return;
		}
		if (!CheckRegex.isPhoneNumber(mobileNumber)) {
			json(SystemCode.code_1001, L("请输入正确的手机号码。"),"");
			return;
		}
		boolean res = userDao.mobileLoginCheckMobile(mobileNumber);
		if (!res) {
			json(SystemCode.code_1001, L("请输入正确的手机号码。"),"");
			return;
		}

		Datastore ds = userDao.getDatastore();
		Query<User> q = ds.find(User.class, "_id", userId);
		UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

		ops.set("userContact.mCode", mCode);
		ops.set("userContact.safeMobile", mobileNumber);
		//手机号（不带国家码）
		ops.set("userContact.loginCheckMobile", mobileNumber);
		ops.set("userContact.mobileCode", "");
		ops.set("userContact.mobileStatu", AuditStatus.pass.getKey());
		ops.set("userContact.checkMobile", "");
		ops.set("userContact.codeTime", new Timestamp(0));
		if("1".equals(isSmsOpen)){

			ops.set("isSmsOpen", true);
		}

		logDao.insertOneRecord(AuthenType.mobile.getKey(), loginUser.get_Id(), "0", "录入手机：" + mobile + "成功。", ip());

		UpdateResults<User> ur = userDao.update(q, ops);


		if (!ur.getHadError()) {
			json(SystemCode.code_1000, L("录入手机成功"),"");
			return;
		} else {
			json(SystemCode.code_1001, L("失败"),"");
			return;
		}
	}

	/**
	 * 录入手机check
	 * @param userId
	 */
	private DataResponse checkSetMobile(String userId) {
		//校验手机验证码
		DataResponse dr = this.checkVerifiCode(userId,MsgToastKey.SETTING_MOBILE, ConstantCenter.UpdFunctionType.SET_USER_INFO_MOBILE, MsgToastKey.LOCK_24_HOUR);
		return dr;
	}
	/**
	 * 修改手机手机验证
	 */
	@Page(Viewer = JSON)
	public void updMobileCheck() {
		setLan();
		String userId = userIdStr();
		String token = param("token");
		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
			json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
			return;
		}
		User user = userDao.findOne("_id",userId);
		//check原手机
		DataResponse oldMobileDr = this.checkVerifiCode(userId,MsgToastKey.UPD_MOBILE, ConstantCenter.UpdFunctionType.UPD_USER_OLD_MOBILE, MsgToastKey.LOCK_24_HOUR);
		if (!oldMobileDr.isSuc()) {
			json(SystemCode.code_1001, oldMobileDr.getDes(),"");
			return;
		}
		//check手机
		DataResponse newMobileDr = this.checkVerifiCode(userId,MsgToastKey.UPD_MOBILE, ConstantCenter.UpdFunctionType.UPD_USER_MOBILE, MsgToastKey.LOCK_24_HOUR);
		if (!newMobileDr.isSuc()) {
			json(SystemCode.code_1001, newMobileDr.getDes(),"");
			return;
		}
		String mobile = param("mobile");
		String mobileCode = param("smscode");

		//手机验证
		PostCodeType pct = PostCodeType.updMobileCheck;
		LimitType lt = LimitType.UpdMobileCheckError;
		DataResponse dr1 = getDataResponse(user,pct,mobile,2,mobileCode,lt,MsgToastKey.UPD_MOBILE,MsgToastKey.LOCK_24_HOUR);
		if (!dr1.isSuc()) {
			setAttr("pass", false);
			json(SystemCode.code_1001, dr1.getDes(),"");
			return;
		}else{
			setAttr("pass", true);
			String checkToken = EncryptionPhoto.getToken(Const.function_upd_mobile,userId);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("checkToken",checkToken);
			json(SystemCode.code_1000, "",jsonObject.toJSONString());
			return;
		}

	}
	/**
	 * 修改手机
	 */
	@Page(Viewer = JSON)
	public void updMobile() {
		setLan();
		initLoginUser();
		String userId = userIdStr();
		String token = param("token");
		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
			json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
			return;
		}
		User user = userDao.findOne("_id",userId);

		//国家码
		String mCode = param("selectedCode");
		String mobile = param("mobile");
		//手机验证码
		String mobileCode = param("smscode");
		//check原手机
		DataResponse oldMobileDr = this.checkVerifiCode(userId,MsgToastKey.UPD_MOBILE, ConstantCenter.UpdFunctionType.UPD_USER_OLD_MOBILE, MsgToastKey.LOCK_24_HOUR);
		if (!oldMobileDr.isSuc()) {
			json(SystemCode.code_1001, oldMobileDr.getDes(),"");
			return;
		}
		//check手机
		DataResponse newMobileDr = this.checkVerifiCode(userId,MsgToastKey.UPD_MOBILE, ConstantCenter.UpdFunctionType.UPD_USER_MOBILE, MsgToastKey.LOCK_24_HOUR);
		if (!newMobileDr.isSuc()) {
			json(SystemCode.code_1001, newMobileDr.getDes(),"");
			return;
		}
		String mobileNumber = mCode + " " + mobile;
		//提示信息
		String msg = "";
		//手机验证
		PostCodeType pct = PostCodeType.updMobile;
		LimitType lt = LimitType.UpdMobileError;
		DataResponse dr1 = getDataResponse(user,pct,mobileNumber,2,mobileCode,lt,MsgToastKey.UPD_MOBILE,MsgToastKey.LOCK_24_HOUR);
		if (!dr1.isSuc()) {
			json(SystemCode.code_1001, dr1.getDes(),"");
			return;
		}else{
			//TODO 放下一步校验session
		}
		if (!CheckRegex.isPhoneNumber(mobileNumber)) {
			json(SystemCode.code_1001, L("请输入正确的手机号码。"),"");
			return;
		}
		boolean res = userDao.mobileLoginCheckMobile(mobileNumber);
		if (!res) {
			json(SystemCode.code_1001, L("请输入正确的手机号码。"),"");
			return;
		}
		//防跳步
		String checkToken = param("checkToken");
		boolean flg = EncryptionPhoto.checkToken(Const.function_upd_mobile,userId,checkToken);
		if(!flg){
			json(SystemCode.code_1001, L("非法操作"),"");
			return;
		}
		Datastore ds = userDao.getDatastore();
		Query<User> q = ds.find(User.class, "_id", userId);
		UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

		ops.set("userContact.mCode", mCode);
		ops.set("userContact.safeMobile", mobileNumber);
		//手机号（不带国家码）
		ops.set("userContact.loginCheckMobile", mobileNumber);
		ops.set("userContact.mobileCode", "");
		ops.set("userContact.mobileStatu", AuditStatus.pass.getKey());
		ops.set("userContact.checkMobile", "");
		ops.set("userContact.codeTime", new Timestamp(0));

		logDao.insertOneRecord(AuthenType.mobile.getKey(), loginUser.get_Id(), "0", "修改手机：" + mobile + "成功。", ip());

		UpdateResults<User> ur = userDao.update(q, ops);


		if (!ur.getHadError()) {
			Cache.Delete(Const.function_upd_mobile + userId);
			json(SystemCode.code_1000, L("修改手机成功"),"");
			return;
		} else {
			json(SystemCode.code_1001, L("失败"),"");
			return;
		}
	}

	/**
	 * 开启手机验证
	 */
	@Page(Viewer = JSON)
	public void openMobileVerify() {
		setLan();
		String userId = userIdStr();
		String token = param("token");
		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
			json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
			return;
		}
		User user = userDao.findOne("_id",userId);
		//设置手机
		DataResponse setMoblieDr = this.checkVerifiCode(userId,MsgToastKey.OPEN_MOBILE_VERIFY, ConstantCenter.UpdFunctionType.SET_USER_INFO_MOBILE, MsgToastKey.LOCK_24_HOUR);
		if (!setMoblieDr.isSuc()) {
			json(SystemCode.code_1001, setMoblieDr.getDes(),"");
			return;
		}
		//check原手机
//        DataResponse oldMobileDr = this.checkVerifiCode(userId,MsgToastKey.UPD_MOBILE, ConstantCenter.UpdFunctionType.UPD_USER_OLD_MOBILE, MsgToastKey.LOCK_24_HOUR);
//        if (!oldMobileDr.isSuc()) {
//            json(L(oldMobileDr.getDes()), false, "");
//            return;
//        }
		//check手机
//        DataResponse newMobileDr = this.checkVerifiCode(userId,MsgToastKey.UPD_MOBILE, ConstantCenter.UpdFunctionType.UPD_USER_MOBILE, MsgToastKey.LOCK_24_HOUR);
//        if (!newMobileDr.isSuc()) {
//            json(L(newMobileDr.getDes()), false, "");
//            return;
//        }
		//check手机
		DataResponse mobileDr = this.checkVerifiCode(userId,MsgToastKey.OPEN_MOBILE_VERIFY, ConstantCenter.UpdFunctionType.OPEN_MOBILE_VERIFY, MsgToastKey.LOCK_24_HOUR);
		if (!mobileDr.isSuc()) {
			json(SystemCode.code_1001, mobileDr.getDes(),"");
			return;
		}

		//国家码
		String mCode = param("selectedCode");
		String mobile = param("mobile");
		String mobileCode = param("smscode");
		String mobileNumber = "";
//		if(StringUtils.isEmpty(mCode)){
//			mobileNumber = mobile;
//		}else{
//			mobileNumber = mCode + " " + mobile;
//		}
		mobileNumber = mobile;
		if (!CheckRegex.isPhoneNumber(mobileNumber)) {
			json(SystemCode.code_1001, L("请输入正确的手机号码。"),"");
			return;
		}
		if(StringUtils.isEmpty(user.getUserContact().getSafeMobile())){
			boolean res = userDao.mobileLoginCheckMobile(mobileNumber);
			if (!res) {
				json(SystemCode.code_1001, L("请输入正确的手机号码。"),"");
				return;
			}
		}
		//手机验证
		PostCodeType pct = PostCodeType.openMobileVerify;
		LimitType lt = LimitType.OpenMobileVerifyError;
		DataResponse dr1 = getDataResponse(user,pct,mobileNumber,2,mobileCode,lt,MsgToastKey.OPEN_MOBILE_VERIFY,MsgToastKey.LOCK_24_HOUR);
		if (!dr1.isSuc()) {
			json(SystemCode.code_1001, dr1.getDes(),"");
			return;
		}else{
//            String msg = CommonUtil.mapToJsonStr(MsgToastKey.mobile,L("开启手机验证成功"));
//            json(L("开启手机验证成功"), true, msg);
			//TODO 放下一步校验session
		}

		Datastore ds = userDao.getDatastore();
		Query<User> q = ds.find(User.class, "_id", userId);
		UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
//		if(StringUtils.isNotEmpty(msg)){
//			ops.set("hasMobileCheckBox", false);
//			userDao.update(q, ops);
//			json("", false, msg);
//			return;
//		}
		ops.set("isSmsOpen", true);
		ops.set("hasMobileCheckBox", true);
		ops.set("userContact.mCode", mCode);
		ops.set("userContact.safeMobile", mobileNumber);
		//手机号（不带国家码）
		ops.set("userContact.loginCheckMobile", mobileNumber);
		ops.set("userContact.mobileCode", "");
		ops.set("userContact.mobileStatu", AuditStatus.pass.getKey());
		ops.set("userContact.checkMobile", "");
		ops.set("userContact.codeTime", new Timestamp(0));

//        logDao.insertOneRecord(AuthenType.mobile.getKey(), loginUser.get_Id(), "0", "修改手机：" + mobile + "成功。", ip());

		UpdateResults<User> ur = userDao.update(q, ops);
		if (!ur.getHadError()) {
			/*start by xwz 20170625 开启谷歌加积分*/
			JifenManage jifenManager = new JifenManage(userId, 4, null, null, "VIP");
			SingletonThreadPool.addJiFenThread(jifenManager);
			json(SystemCode.code_1000, L("设置成功"),"");
			return;
		} else {
			json(SystemCode.code_1001, L("失败"),"");
			return;
		}
	}

	/**
	 * 关闭手机二次验证
	 */
	@Page(Viewer = JSON)
	public void closeMobileVerify() {
		setLan();
		String userId = userIdStr();
		String token = param("token");
		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
			json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
			return;
		}
		User user = userDao.findOne("_id",userId);
		String mobile = param("mobile");
		String mobileCode = param("smscode");
		String emailCode = param("emailcode");
		//提示信息
		String msg = "";
		//设置手机
		DataResponse setMoblieDr = this.checkVerifiCode(userId,MsgToastKey.SETTING_MOBILE, ConstantCenter.UpdFunctionType.SET_USER_INFO_MOBILE, MsgToastKey.LOCK_24_HOUR);
		if (!setMoblieDr.isSuc()) {
			json(SystemCode.code_1001, setMoblieDr.getDes(),"");
			return;
		}
		//check原手机
		DataResponse oldMobileDr = this.checkVerifiCode(userId,MsgToastKey.UPD_MOBILE, ConstantCenter.UpdFunctionType.UPD_USER_OLD_MOBILE, MsgToastKey.LOCK_24_HOUR);
		if (!oldMobileDr.isSuc()) {
			json(SystemCode.code_1001, oldMobileDr.getDes(),"");
			return;
		}
		//check手机
		DataResponse newMobileDr = this.checkVerifiCode(userId,MsgToastKey.UPD_MOBILE, ConstantCenter.UpdFunctionType.UPD_USER_MOBILE, MsgToastKey.LOCK_24_HOUR);
		if (!newMobileDr.isSuc()) {
			json(SystemCode.code_1001,newMobileDr.getDes(),"");
			return;
		}
		//check邮箱
		DataResponse emailDr = this.checkVerifiCode(userId,MsgToastKey.CLOSE_MOBILE_VERIFY, ConstantCenter.UpdFunctionType.CLOSE_MOBILE_VERIFY_EMAIL, MsgToastKey.LOCK_24_HOUR);
		if (!emailDr.isSuc()) {
			json(SystemCode.code_1001, emailDr.getDes(),"");
			return;
		}
		//check手机
		DataResponse mobileDr = this.checkVerifiCode(userId,MsgToastKey.CLOSE_MOBILE_VERIFY, ConstantCenter.UpdFunctionType.CLOSE_MOBILE_VERIFY_MOBILE, MsgToastKey.LOCK_24_HOUR);
		if (!mobileDr.isSuc()) {
			json(SystemCode.code_1001, mobileDr.getDes(),"");
			return;
		}

		if (!CheckRegex.isPhoneNumber(mobile)) {
			json(SystemCode.code_1001, L("请输入合法的手机号。"),"");
			return;
		}

		//邮箱验证
		PostCodeType pct = PostCodeType.closeEmailVerify;
		LimitType lt = LimitType.CloseMobileVerifyEmailError;
		DataResponse dr = getDataResponse(user,pct,user.getEmail(),1,emailCode,lt,MsgToastKey.CLOSE_MOBILE_VERIFY,MsgToastKey.LOCK_24_HOUR);
		if (!dr.isSuc()) {
			json(SystemCode.code_1001, dr.getDes(),"");
			return;
		}
		//手机验证
		PostCodeType pct1 = PostCodeType.closeMobileVerify;
		LimitType lt1 = LimitType.CloseMobileVerifyMobileError;
		DataResponse dr1 = getDataResponse(user,pct1,mobile,2,mobileCode,lt1,MsgToastKey.CLOSE_MOBILE_VERIFY,MsgToastKey.LOCK_24_HOUR);
		if (!dr1.isSuc()) {
			json(SystemCode.code_1001, dr1.getDes(),"");
			return;
		}
		Datastore ds = userDao.getDatastore();
		Query<User> q = ds.find(User.class, "_id", userId);
		UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
		ops.set("isSmsOpen", false);
		ops.set("userContact.mobileStatu", AuditStatus.pass.getKey());
		UpdateResults<User> ur = userDao.update(q, ops);
		if (!ur.getHadError()) {
			json(SystemCode.code_1000, L("手机验证关闭成功"),"");
			return;
		} else {
			json(SystemCode.code_1001, L("失败"),"");
			return;
		}

	}


	/**
	 * 获取google二维码密钥
	 */
	@Page(Viewer = JSON)
	public void getGoogleInfo() {
		setLan();
		String userId = userIdStr();
		String token = param("token");
		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
			json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
			return;
		}
		User user = userDao.findOne("_id",userId);

		//check谷歌
		DataResponse mobileDr = this.checkVerifiCode(userId,MsgToastKey.OPEN_GOOGLE_VERIFY, ConstantCenter.UpdFunctionType.OPEN_GOOGLE_VERIFY, MsgToastKey.LOCK_24_HOUR);
		if (!mobileDr.isSuc()) {
			json(SystemCode.code_1001, mobileDr.getDes(),"");
			return;
		}
		Cache.Delete(LimitType.OpenGoogleVerifyError.toString() +"_"+ userId);
		String userName = user.getUserName();
		if(StringUtils.isEmpty(userName)){
			userName = "btcwinex";
		}
		String url = ApiConfig.getValue("usecenter.url");
		FeignContainer container = new FeignContainer(url.concat("/google"));
		GoogleApiService googleApi = container.getFeignClient(GoogleApiService.class);
		Map<String,String> map = googleApi.getGoogleAuthQr("",userName,"btcwinex");
		if(null != map){
			json(SystemCode.code_1000,"",JSONObject.toJSONString(map));
			return;
		}else{
			json(SystemCode.code_1001,"获取谷歌信息失败","");
			return;
		}
	}



	/**
	 * 二次验证--google验证开启
	 */
	@Page(Viewer = JSON)
	public void openGoogleVerify(){
		setLan();
		String gcodeParam = "paycode";
		String googleCode = param(gcodeParam);
		String secret = param("payKey");
		String userId = userIdStr();
		String token = param("token");
		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
			json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
			return;
		}
		//check谷歌
		DataResponse mobileDr = this.checkVerifiCode(userId,MsgToastKey.OPEN_GOOGLE_VERIFY, ConstantCenter.UpdFunctionType.OPEN_GOOGLE_VERIFY, MsgToastKey.LOCK_24_HOUR);
		if (!mobileDr.isSuc()) {
			json(SystemCode.code_1001,mobileDr.getDes(),"");
			return;
		}

		String mobile = param("mobile");
		String mobileCode = param("mobile");
		String url = ApiConfig.getValue("usecenter.url");
		FeignContainer container = new FeignContainer(url.concat("/google"));
		GoogleApiService googleApi = container.getFeignClient(GoogleApiService.class);
		//最后一个参数对应用户中心的key
		Map<String,String> map = googleApi.checkGoogleCodeApiN(googleCode,secret,userId,String.valueOf(ConstantCenter.UpdFunctionType.OPEN_GOOGLE_VERIFY.getKey()));
		String key = "";
		String returnVal = "";
		String msg = "";
		for(Map.Entry<String, String> entry : map.entrySet()){
			key = entry.getKey();
			returnVal = entry.getValue().toString();
		}
		if("1".equals(key)){
			FeignContainer container1 = new FeignContainer(url.concat("/user"));
			UserApiService userApi = container1.getFeignClient(UserApiService.class);
			int state = userApi.updTwoVerifi(userId,mobile,secret,null,true);
			if (state != 0) {
				/*start by xwz 20170625 绑定谷歌认证*/
				JifenManage jifenManager = new JifenManage(userId, 5, null, null, "VIP");//5：谷歌认证
				SingletonThreadPool.addJiFenThread(jifenManager);
				json(SystemCode.code_1000,L("谷歌验证开启成功"),"");
				return;
			}else{
				json(SystemCode.code_1001,L("失败"),"");
				return;
			}
		}else{
			if("-2".equals(returnVal)){
				returnVal = L("验证码输入次数超出限制，将锁定开启谷歌验证功能，请24小时之后再试。");
			}else{
				//格式化次数
				returnVal = returnVal.replace("谷歌","");
				String[] errorMsg = returnVal.split("#");
				returnVal = L(errorMsg[0], errorMsg[1]);
			}
			json(SystemCode.code_1001,returnVal,"");
			return;
		}
	}

	/**
	 * 二次验证--google验证关闭
	 */
	@Page(Viewer = JSON)
	public void closeGoogleVerify(){
		setLan();
		String userId = userIdStr();
		String token = param("token");
		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
			json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
			return;
		}
		User user = userDao.findOne("_id",userId);

		//check邮箱
		DataResponse emailDr = this.checkVerifiCode(userId,MsgToastKey.CLOSE_GOOGLE_VERIFY, ConstantCenter.UpdFunctionType.CLOSE_GOOGLE_VERIFY_EMAIL, MsgToastKey.LOCK_24_HOUR);
		if (!emailDr.isSuc()) {
			json(SystemCode.code_1001,emailDr.getDes(),"");
			return;
		}
		//check谷歌
		DataResponse googleDr = this.checkVerifiCode(userId,MsgToastKey.CLOSE_GOOGLE_VERIFY, ConstantCenter.UpdFunctionType.CLOSE_GOOGLE_VERIFY_GOOGLE, MsgToastKey.LOCK_24_HOUR);
		if (!googleDr.isSuc()) {
			json(SystemCode.code_1001,googleDr.getDes(),"");
			return;
		}

		//邮箱验证码
		String emailCode = param("emailcode");
		String googleCode = param("gcode");
		String secret = user.getUserContact().getSecret();
		//提示信息
		String msg = "";
		//邮箱验证
		PostCodeType pct = PostCodeType.closeGoogleVerify;
		LimitType lt = LimitType.CloseGoogleVerifyEmailError;
		DataResponse dr = getDataResponse(user,pct,user.getUserContact().getSafeEmail(),1,emailCode,lt,MsgToastKey.CLOSE_GOOGLE_VERIFY,MsgToastKey.LOCK_24_HOUR);
		if (!dr.isSuc()) {
			json(SystemCode.code_1001,dr.getDes(),"");
			return;
		}

		String url = ApiConfig.getValue("usecenter.url");
		FeignContainer container = new FeignContainer(url.concat("/google"));
		GoogleApiService googleApi = container.getFeignClient(GoogleApiService.class);
		//最后一个参数对应用户中心的key
		Map<String,String> map = googleApi.checkGoogleCodeApiN(googleCode,secret,userId,String.valueOf(ConstantCenter.UpdFunctionType.CLOSE_GOOGLE_VERIFY_GOOGLE.getKey()));
		String key = "";
		String returnVal = "";
		for(Map.Entry<String, String> entry : map.entrySet()){
			key = entry.getKey();
			returnVal = entry.getValue().toString();
		}
		if(!"1".equals(key)){
			if("-2".equals(returnVal)){
				returnVal = L("验证码输入次数超出限制，将锁定关闭谷歌验证功能，请24小时之后再试。");
			}else{
				//格式化次数
				returnVal = returnVal.replace("谷歌","");
				String[] errorMsg = returnVal.split("#");
				returnVal = L(errorMsg[0], errorMsg[1]);
			}
			json(SystemCode.code_1001,returnVal,"");
			return;
		}
		FeignContainer container1 = new FeignContainer(url.concat("/user"));
		UserApiService userApi = container1.getFeignClient(UserApiService.class);
		int state = userApi.updTwoVerifi(userId,"","",null,false);
		if(state != 0){
			json(SystemCode.code_1000,L("谷歌验证关闭成功"),"");
			return;
		}else{
			json(SystemCode.code_1001,L("失败"),"");
			return;
		}
	}

	//开启或关闭资金安全密码
	@Page(Viewer = JSON)
	public void useOrCloseSafePwd(){
		try {
			setLan();
			initLoginUser();
			String userId = userIdStr();
			String token = param("token");
			if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
				json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
				return;
			}
			//校验提现资金密码
			DataResponse dr0 = this.checkVerifiCode(userId,MsgToastKey.TRANSACTION_PAY_PWD,ConstantCenter.UpdFunctionType.TRANSACTION_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
			if (!dr0.isSuc()) {
				json(SystemCode.code_1001, L(dr0.getDes()),"");
				return;
			}
			String safePwd = param("payPass");
			byte[] decodedData2 = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(safePwd.replace(" ", "+")),priKey);
			safePwd = new String(decodedData2);
			String returnVal = safePwdForApp(safePwd, loginUser.getId(),true);
			if(StringUtils.isNotEmpty(returnVal)){
				json(SystemCode.code_1001,returnVal,"");
				return;
			}
			int closeStatu = intParam("closeStatu");
			if (closeStatu <= 0) {// 开启

				if (userDao.isNeedSafePwd(loginUser)) {
					json(SystemCode.code_1001,L("资金安全密码已经开启。"),"");
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
					json(SystemCode.code_1000,L("资金安全密码成功开启。"),"");
					return;
				}else {
					json(SystemCode.code_1001,L("开启失败!"),"");
					return;
				}
			} else {// 开启

				Date dateNow = new Date();
				long expirationTime = closeStatu == 1 ? 0L: dateNow.getTime() + 1000*60*60*6;//| 关闭周期 0：永久关闭 2：6个小时|

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
						json(SystemCode.code_1000,L("资金安全密码关闭成功，如有需求，可手动开启。"),"");
						return;
					}else{
						json(SystemCode.code_1000,L("资金安全密码关闭成功，六小时后自动开启。"),"");
						return;
					}
				}else {
					json(SystemCode.code_1001,L("失败!"),"");
					return;
				}
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			json(SystemCode.code_1001,L("操作失败"),"");
			return;
		}
	}
	/**
	 * 18、用户信息
	 */
	@Page(Viewer = JSON)
	public void userInfo() {
		setLan();
		initLoginUser();
		String userId = userIdStr();
		User user = userDao.getUserById(userId);
		String token = param("token");
		if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
			json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
			return;
		}
		JSONObject json = new JSONObject();
		json.put("uid",user.getId());
		json.put("mobile",user.getUserContact().getSafeMobile());
		json.put("nickname",user.getNickname());
		json.put("email",user.getUserContact().getSafeEmail());
		json.put("googleOpen",user.getGoogleOpen());
		json.put("smsOpen",user.getSmsOpen());
		json.put("secret",user.getUserContact().getSecret());
		json.put("hasSafePwd",user.getHasSafePwd());
		if(StringUtils.isBlank(user.getColor())){
			json.put("color","#3E85A2");
		}else{
			json.put("color",user.getColor());
		}
		Authentication au = new AuthenticationDao().getByUserId(user.getId());
		json.put("cardName",au==null ? "" : (au.getStatus()==AuditStatus.a1Pass.getKey() ? au.getRealName() : ""));
		json.put("cardStatu",au==null ? AuditStatus.a1NoSubmit.getKey() : au.getStatus());
		json.put("reason",au==null ? "" : user.getCardReson());
		json.put("countryCode",au==null ? "" :au.getCountryCode());
		json.put("isTransSafe",user.isNeedSafePwd());
		json.put("safePwdExpiration",user.getSafePwdExpiration());
		//如果isTransSafe为true表示始终开启，如果isTransSafe为false并且safePwdExpiration为2表示关闭6小时
		boolean isTrans = userDao.isNeedSafePwd(user);
//		boolean isTrans = user.isNeedSafePwd() ? true : (!user.isNeedSafePwd() ? false : true);
		//checkTrans为true表示始终开启，checkTrans为false表示关闭6小时
		//0:始终开启。 6:关闭6小时，1:永久关闭
		if (userDao.isNeedSafePwd(user)) {
			json.put("checkTrans",0);
		}if(user.isNeedSafePwd()){
			json.put("checkTrans",0);
		}else if(!user.isNeedSafePwd() && user.getSafePwdExpiration() == 0L){
			json.put("checkTrans",1);
		}else if(!user.isNeedSafePwd() && user.getSafePwdExpiration() > 0L){
			json.put("checkTrans",6);
		}
		json.put("vipRate",user.getVipRate());
		json.put("unReadMsgNum",getUnReadMsgNum(userId));
		json.put("color",user.getColor());
		json(SystemCode.code_1000,SystemCode.code_1000.getValue(), json.toString());
	}

	public long getUnReadMsgNum(String userId) {
		setLan();

		// 1. 获取用户的已读公告ID列表(SET结构)
		// 2. 从数据库获取总的公告数量（注意通道）
		// 3. 相减获取未读公告数量
		Set<String> list = BaseMobileAction.getReadNoticeSet(userId);

		Query<News> query = new NewsDao().getQuery().retrievedFields(true, "_id", "baseId").filter("type", 1);

		if(StringUtils.isNotBlank(lan)){
			query.filter("language",lan);
		}

        long unreadNum = 0;
        List<News> newsList = query.asList();
        if (!CollectionUtils.isEmpty(newsList)) {
            for (News news : newsList) {
                if (!list.contains(StringUtils.isNotEmpty(news.getBaseId()) ? news.getBaseId() : news.getId())) {
                    // 记录未读
                    unreadNum++;
                }
            }
        }
        return unreadNum;
	}
	/**
	 * 16,保存昵称
	 */
	@Page(Viewer = JSON)
	public void saveNickName() {
	    setLan();
		initLoginUser();
		String userId = userIdStr();
		String nickname = param("nickname");

		String colors[]={"#E4A184","#9BC979","#E9BE69","#3E85A2","#76A4C8"};
		Random random=new Random();
		String color=colors[random.nextInt(5)];
		String token = param("token");
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
                    json(SystemCode.code_1001, L("昵称不可包含非法符号"),"");
                    return;
                }
            }
        }
        User userCheck = userDao.findOne("nickname",nickname);
        if (null != userCheck) {
            json(SystemCode.code_1001, L("该昵称已被占用"),"");
            return;
        }
        Datastore ds = userDao.getDatastore();
        Query<User> q = ds.find(User.class, "_id", userId);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
        ops.set("nickname", nickname);
        ops.set("nick", nickname);
        if(StringUtils.isNotBlank(color)){
            ops.set("color", color);
        }

		UpdateResults<User> ur = userDao.update(q, ops);
		if (!ur.getHadError()) {
			json(SystemCode.code_1000, L("设置成功"),"");
			return;
		} else {
			json(SystemCode.code_1001, L("失败"),"");
			return;
		}
	}

	public static void main(String[] args) {
		Random random=new Random();
		for(int a=0;a<100;a++){
			int b=random.nextInt(4);
			System.out.println(b);
		}

	}
}

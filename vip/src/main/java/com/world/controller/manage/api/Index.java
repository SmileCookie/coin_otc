package com.world.controller.manage.api;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.world.cache.Cache;
import com.world.controller.CheckRegex;
import com.world.model.dao.api.ApiKeyDao;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.api.ApiKey;
import com.world.model.entity.user.UserContact;
import com.world.util.Message;
import com.world.util.date.TimeUtil;
import com.world.util.sign.EncryDigestUtil;
import com.world.web.Page;
import com.world.web.action.ApproveAction;
import com.world.web.response.DataResponse;
import com.world.web.sso.session.ClientSession;


/**
 * 
 * @author zhanglinbo 20161224
 */
public class Index extends ApproveAction {
	
	private static final long serialVersionUID = 1L;
	//用户dao
	private UserDao userDao = new UserDao();
	//apiKeyDao
	private ApiKeyDao dao = new ApiKeyDao();
	
	/**
	 * api 设置首页
	 *
	 * Close By suxinjie 一期屏蔽该功能
	 */
//	@Page(Viewer = "/cn/api/index.jsp")
	public void index() {
		try {
			//先判断手机或邮箱是否验证
			if(!emailNoSetTips()){
		    	 return;
		    }
			//加载用户登录信息
			initLoginUser();
			UserContact uc = loginUser.getUserContact();
			setAttr("curUser", loginUser);
			//手机验证状态
			setAttr("mobileStatu", uc.getMobileStatu());
			//谷歌验证状态
			setAttr("googleAuth", uc.getGoogleAu());
			//查询用户APIKey是否存在
			ApiKey apiKey = dao.getUserApiKey(loginUser.getId());
			if(apiKey != null){
				if(StringUtils.isNotEmpty(apiKey.getIpaddrs())){
					String ipaddrs = apiKey.getIpaddrs();//绑定地址信息
					setAttr("ipaddrs", ipaddrs);
					setAttr("apiIpStatus", 2);//默认审核通过
				}else{
					setAttr("ipaddrs", "");
					setAttr("apiIpStatus", -1);
				}
				setAttr("accessKey",apiKey.getAccesskey());
				setAttr("apiStatus",apiKey.getIsAct());
				
				//从memcached获取有无sercet，有的话，显示
				String secret = Cache.Get("vip_api_secret_"+loginUser.getId() );
				if(secret!=null){
					setAttr("secretKey", secret);//显示一次然后删除
					Cache.Delete("vip_api_secret_"+loginUser.getId() );
				}
			}else{
				setAttr("ipaddrs", "");
				setAttr("apiIpStatus", -1);
				setAttr("accessKey","");
				setAttr("apiStatus",0);
			}
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	
	/**
	 * 开启生成apiKey或者关闭api
	 *
	 * Close By suxinjie 一期屏蔽该功能
	 */
//	@Page(Viewer=JSON)
	public void openOrClose(){
		int result = 0;//返回操作结果值
		try {
			String oper = param("oper");	//open的话是打开api操作，close是关闭api操作
			String mobileCode = param("mobileCode"); //获取手机验证码参数
			String ipaddrs = param("ipaddrs");//获取IP绑定地址
			
			
			
			//第一步、初始化用户信息
			initLoginUser();
			UserContact uc = loginUser.getUserContact();

			//第二步，验证手机验证码，有手机号码的情况下
			if (uc.getMobileStatu()==2) {
				// 检查短信验证码
				String codeRecvAddr = loginUser.getUserContact().getSafeMobile();
				if (StringUtils.isBlank(codeRecvAddr)) {
					codeRecvAddr = loginUser.getUserContact().getSafeEmail();
				}
				ClientSession clientSession = new ClientSession(ip(), codeRecvAddr, lan, PostCodeType.safeAuth.getValue(), false);
				DataResponse dr = clientSession.checkCode(mobileCode);
				if(!dr.isSuc()){
					json(dr.getDes(),false,"");
					return;
				}
			}
			//验证谷歌验证码
			if (uc.getGoogleAu()==2) {
				userDao.setLan(lan);
				Message msg = userDao.isCorrect(loginUser, uc.getSecret(), longParam("googleCode"));
				if (!msg.isSuc()) {
					json(msg.getMsg(), false, "");
					return;
				}
			}

			
			//第三步、关闭或打开API
			if(oper.equals("open")){	//打开api
				
				//用户已经锁定,不允许开启API交易
				if(loginUser.getRepayLock() == 1){
					json(L("用户交易已被锁定，不能开启API进行交易!"), false, "");
					return;
				}
				if(ipaddrs.length()>0){
					//用半角逗号隔开，验证是否格式正确
					String[] apiIpBindArr = ipaddrs.split(",");
					for(String apiIpBindItem : apiIpBindArr){
						if( !CheckRegex.isIP( apiIpBindItem ) ){
							json(L("你输入的是一个非法的IP地址段！IP段为：:xxx.xxx.xxx.xxx（xxx为0-255)！"), false, "");
							return;
						}
					}
				}
				
				
				//查询用户APIkey ,如果还没有则生成一个，如果有则更新
				ApiKey apiKey = dao.getUserApiKey(loginUser.getId());

				String accesskey = UUID.randomUUID().toString();//公钥
				String secretkey = UUID.randomUUID().toString();//私钥
				//加密一层
				secretkey = EncryDigestUtil.digest(secretkey);
				
				if(apiKey!=null){
					//Cache.Set("user_"+apiKey.getAccesskey()+"_api_locked", String.valueOf("unlocked"), 30*60);
					result = dao.openApiKey(loginUser.getId(),accesskey,secretkey, ipaddrs);
				}else{
					apiKey = new ApiKey();
					apiKey.setUserId(loginUser.getId());
					apiKey.setUserName(loginUser.getUserName());
					apiKey.setAccesskey(accesskey);
					apiKey.setSecretkey(secretkey);
					apiKey.setIpaddrs(ipaddrs);
					apiKey.setIsAct(1);
					apiKey.setIsLock(0);
					apiKey.setIsDel(0);
					apiKey.setAddTime(TimeUtil.getNow());
					result = dao.saveOne(apiKey);
				}
				if(result>0){//存入memcache,让页面显示一次
					Cache.Set("bit_api_secret_"+loginUser.getId(), secretkey, 60);	//把secret缓存60秒
				}
			}else if(oper.equals("close")){	//关闭api
				result = dao.closeApiKey(loginUser.get_Id());
			}
			
			if(result>0){
				if(oper.equals("open")){	//打开api
					json(L("您已经开启了API。"), true, "");
					
				}else if(oper.equals("close")){	//关闭api
					json(L("您已经关闭了API。"), true, "");
				}
			}else{
				json(L("操作失败。"), false, "");
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			json(L("出错了。"), false, "");
		}
	}
	
	/**
	 * 更新API绑定IP，进入绑定IP地址页面
	 *
	 * Close By suxinjie 一期屏蔽该功能
	 */
//	@Page(Viewer="/cn/api/updateIp.jsp")
	public void updateIp(){
		try {
			if(!emailNoSetTips()){
		    	 return;
		    }
			
			//初始化用户信息
			initLoginUser();
			UserContact uc = loginUser.getUserContact();
			setAttr("curUser", loginUser);
			setAttr("mobileStatu", uc.getMobileStatu());
			setAttr("googleAuth", uc.getGoogleAu());
			
			//获取API信息
			ApiKey apiKey = dao.getUserApiKey(loginUser.getId());
			if(apiKey != null){
				if(StringUtils.isNotEmpty(apiKey.getIpaddrs())){
					String apiIpBind = apiKey.getIpaddrs();
					setAttr("ipaddrs", apiIpBind);
					setAttr("apiIpStatus", 2);
				}else{
					setAttr("apiIpBind", "");
					setAttr("apiIpStatus", -1);
				}
				setAttr("apikey",apiKey.getAccesskey());
				setAttr("apiStatus",apiKey.getIsAct());
			}else{
				setAttr("apiIpBind", "");
				setAttr("apiIpStatus", -1);
			}
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	/**
	 * 保存用户绑定的IP地址
	 *
	 * Close By suxinjie 一期屏蔽该功能
	 */
//	@Page(Viewer=JSON)
	public void saveIpBind(){
		int result = 0;//定义操作结果返回值
		try {
			String mobileCode = param("mobileCode");//获取手机验证码参数
			String ipaddrs = param("ipaddrs");//获取绑定的IP地址
			//1、获取用户信息
			initLoginUser();
			UserContact uc = loginUser.getUserContact();
			
			//2、验证手机验证码，有手机号码的情况下
			if (uc.getMobileStatu()==2) {
				// 检查短信验证码
				String codeRecvAddr = loginUser.getUserContact().getSafeMobile();
				if (StringUtils.isBlank(codeRecvAddr)) {
					codeRecvAddr = loginUser.getUserContact().getSafeEmail();
				}
				ClientSession clientSession = new ClientSession(ip(), codeRecvAddr, lan, PostCodeType.safeAuth.getValue(), false);
				DataResponse dr = clientSession.checkCode(mobileCode);
				if(!dr.isSuc()){
					json(dr.getDes(),false,"");
					return;
				}
			}
				
			//验证谷歌验证码
			if (uc.getGoogleAu()==2) {
				userDao.setLan(lan);
				Message msg = userDao.isCorrect(loginUser, uc.getSecret(), longParam("googleCode"));
				if (!msg.isSuc()) {
					json(msg.getMsg(), false, "");
					return;
				}
			}
			
			//3、ip地址验证
			if(ipaddrs.length()>0){
				//用半角逗号隔开，验证是否格式正确
				String[] apiIpBindArr = ipaddrs.split(",");
				for(String apiIpBindItem : apiIpBindArr){
					if( !CheckRegex.isIP( apiIpBindItem ) ){
						json(L("你输入的是一个非法的IP地址段！IP段为：:xxx.xxx.xxx.xxx（xxx为0-255)！"), false, "");
						return;
					}
				}
			}
			
			//4、修改保存用户的API绑定的ip地址
			result = dao.updateApiKeyIpAddrs(loginUser.getId(), ipaddrs);
			
			if(result>0){
				json(L("修改成功"), true, "");
			}else{
				json(L("操作失败。"), false, "");
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			json(L("出错了。"), false, "");
		}
	}

	
	/**
	 * 进入重新生成API秘钥页面
	 *
	 * Close By suxinjie 一期屏蔽该功能
	 */
//	@Page(Viewer="/cn/api/createKey.jsp")
	public void createKey(){
		try {
			//邮件未验证检查
			if(!emailNoSetTips()){
		    	 return;
		    }
			//初始化登录用户信息
			initLoginUser();
			UserContact uc = loginUser.getUserContact();
			setAttr("curUser", loginUser);
			setAttr("mobileStatu", uc.getMobileStatu());
			setAttr("googleAuth", uc.getGoogleAu());
			
			//获取用户API 信息
			/*ApiKey apiKey = dao.getUserApiKey(loginUser.getId());
			if(apiKey != null){
				if(StringUtils.isNotEmpty(apiKey.getIpaddrs())){
					String apiIpBind = apiKey.getIpaddrs();
					setAttr("ipaddrs", apiIpBind);
					setAttr("apiIpStatus", 2);
				}else{
					setAttr("apiIpBind", "");
					setAttr("apiIpStatus", -1);
				}
				setAttr("apikey",apiKey.getAccesskey());
				setAttr("apiStatus",apiKey.getIsAct());
			}else{
				setAttr("apiIpBind", "");
				setAttr("apiIpStatus", -1);
			}*/

			
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	@Page(Viewer=JSON)
	public void doCreateKey(){
		try{
			
			String mobileCode = param("mobileCode");//手机验证码
			//1初始化用户信息
			initLoginUser();
			UserContact uc = loginUser.getUserContact();
			
			
			//2验证手机验证码，有手机号码的情况下
			if (uc.getMobileStatu()==2) {
				// 检查短信验证码
				String codeRecvAddr = loginUser.getUserContact().getSafeMobile();
				if (StringUtils.isBlank(codeRecvAddr)) {
					codeRecvAddr = loginUser.getUserContact().getSafeEmail();
				}
				ClientSession clientSession = new ClientSession(ip(), codeRecvAddr, lan, PostCodeType.safeAuth.getValue(), false);
				DataResponse dr = clientSession.checkCode(mobileCode);
				if(!dr.isSuc()){
					json(dr.getDes(),false,"");
					return;
				}
			}
				
			//验证谷歌验证码
			if (uc.getGoogleAu()==2) {
				userDao.setLan(lan);
				Message msg = userDao.isCorrect(loginUser, uc.getSecret(), longParam("googleCode"));
				if (!msg.isSuc()) {
					json(msg.getMsg(), false, "");
					return;
				}
			}
			
			//重新生成秘钥信息
			String accessKey = UUID.randomUUID().toString();//key
			String secretKey = UUID.randomUUID().toString();//密钥
			secretKey =  EncryDigestUtil.digest(secretKey);
			//更新到数据库
			dao.updateApiKey(loginUser.getId(), accessKey, secretKey);
			Cache.Set("vip_api_secret_"+loginUser.getId(), secretKey, 60);	//把secret缓存60秒
			json("", true, "");
			return;
		}catch(Exception ex){
			log.error("内部异常", ex);
			json(L("未知错误"), false, "");
		}
	}
	
}


package com.world.controller;

import cn.hutool.core.util.StrUtil;
import com.Lan;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.GoogleApiService;
import com.messi.user.feign.UserApiService;
import com.messi.user.util.ConstantCenter;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.constant.Const;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.model.LimitType;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.pay.DetailsDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.user.EmailDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.UserLoginIpDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.pay.DetailsSummaryBean;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.model.enums.LogCategory;
import com.world.model.enums.LoginStatus;
import com.world.model.jifenmanage.JifenManage;
import com.world.model.singleton.SingletonThreadPool;
import com.world.rabbitmq.producer.InitPayUserWalletProducer;
import com.world.rabbitmq.producer.OperateLogInfoProducer;
import com.world.rabbitmq.producer.UserLoginLogProducer;
import com.world.util.CommonUtil;
import com.world.util.Message;
import com.world.util.MsgToastKey;
import com.world.util.cookie.CookieUtil;
import com.world.util.device.HttpRequestDeviceUtils;
import com.world.util.jpush.MsgType;
import com.world.util.jpush.Pusher;
import com.world.util.qiniu.QiNiuUtil;
import com.world.util.sign.RSACoder;
import com.world.util.string.EncryptionPhoto;
import com.world.util.string.MD5;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.Pages;
import com.world.web.action.ApproveAction;
import com.world.web.response.DataResponse;
import com.world.web.sso.SSOLoginManager;
import com.world.web.sso.SessionUser;
import com.world.web.sso.rsa.RsaLoginUtil;
import com.world.web.sso.rsa.RsaUser;
import com.world.web.sso.session.ClientSession;
import com.world.web.sso.session.Session;
import com.world.web.sso.session.SsoSessionManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends ApproveAction {
    private static final long serialVersionUID = 1L;
    private static final DetailsDao detailsDao = new DetailsDao();

    private static final int aliviableMAXTimes =  60 * 1440;//sessoin有效时间 24个小时

    public static final int codeAvailableMAXTimes = 3;//验证码的有效验证次数

    static Logger logger = Logger.getLogger(Login.class.getName());

    private UserDao userDao = new UserDao();
    private EmailDao eDao = new EmailDao();
    UserLoginIpDao uld = new UserLoginIpDao();
    @Page
    public void index() {
        try {
            response.sendRedirect(VIP_DOMAIN + "/bw/login/");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

	/*start by xzhang 201701010 zendesk跨系统会话同步*/

    /**
     * 1.1 当前无用户登录，则根据语言环境跳转到指定工单系统页面，无登录状态。
     * 1.2 当前用户已登录，且存在邮箱。则同步当前用户会话信息到工单系统；
     * 语言环境根据用户工单信息设置展示，默认在工单系统配置；会话同步不会传递语言环境。
     * 1.3 当前用户已登录，但不存在邮箱。则无法同步用户会话信息；则根据语言环境跳转到指定工单系统页面，无登录状态。
     * 异常：
     * 屏蔽所有异常，展示未登录状态下工单系统
     */
    @Page()
    public void zendesk() {
        String viewFlag = param("viewFlag");
        try {
            User user = userDao.getUserById(userIdStr());
            if (user != null) {
                UserContact userContact = user.getUserContact();
                // Given a user instance
                JWTClaimsSet jwtClaims = new JWTClaimsSet();
                jwtClaims.setIssueTime(new Date());
                jwtClaims.setJWTID(UUID.randomUUID().toString());
                jwtClaims.setCustomClaim("name", user.getUserName());
                jwtClaims.setCustomClaim("email", userContact.getSafeEmail());
                if (userContact != null && userContact.getSafeEmail() != null && !"".equals(userContact.getSafeEmail())) {
                    jwtClaims.setCustomClaim("email", userContact.getSafeEmail());
                } else if (userContact != null && userContact.getSafeMobile() != null && !"".equals(userContact.getSafeMobile())) {
                    String[] mobiles = userContact.getSafeMobile().split(" ");
                    if (mobiles != null && mobiles.length > 1) {
                        jwtClaims.setCustomClaim("email", mobiles[1] + "@btcwinex.com");
                    } else {
                        if (user.getUserName().indexOf("@") != -1) {
                            jwtClaims.setCustomClaim("email", user.getUserName());
                        } else {
                            jwtClaims.setCustomClaim("email", user.getUserName() + "@btcwinex.com");
                        }
                    }
                } else {
                    if (user.getUserName().indexOf("@") != -1) {
                        jwtClaims.setCustomClaim("email", user.getUserName());
                    } else {
                        jwtClaims.setCustomClaim("email", user.getUserName() + "@btcwinex.com");
                    }
                }
                // Create JWS header with HS256 algorithm
                JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
                header.setContentType("text/plain");
                // Create JWS object
                JWSObject jwsObject = new JWSObject(header, new Payload(jwtClaims.toJSONObject()));
                // Create HMAC signer
                JWSSigner signer = new MACSigner(GlobalConfig.SHARED_KEY.getBytes());
                try {
                    jwsObject.sign(signer);
                } catch (Exception e) {
                    log.error("当前用户" + user.get_Id() + "签名失败，失败原因：", e);
                    throw e;
                }
                String returnTo = GlobalConfig.SUBDOMAIN + "/hc/zh-cn";
                String jwtString = jwsObject.serialize();
                String redirectUrl = GlobalConfig.SUBDOMAIN + "/access/jwt?jwt=" + jwtString;
                if (lan == null || "".equals(lan) || "cn".equals(lan)) {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/zh-cn";
                } else if ("en".equals(lan)) {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/en-us";
                }else if ("jp".equals(lan)) {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/ja";
                }else if ("kr".equals(lan)) {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/ko";
                }else {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/zh-tw";
                }
                if (StringUtil.exist(viewFlag) && viewFlag.equals("notice")) {
                    redirectUrl = redirectUrl + "&return_to=" + GlobalConfig.SUBDOMAIN + L("公告链接");
                } else if (StringUtil.exist(viewFlag) && viewFlag.equals("app_down")) {
                    redirectUrl = redirectUrl + "&return_to=" + GlobalConfig.SUBDOMAIN + L("安装说明链接");
                } else if (StringUtil.exist(viewFlag) && viewFlag.equals("googleauth")) {
                    redirectUrl = redirectUrl + "&return_to=" + GlobalConfig.SUBDOMAIN + L("谷歌验证说明链接");
                } else {
                    redirectUrl = redirectUrl + "&return_to=" + returnTo;
                }
                go(redirectUrl);
                return;
            } else {
                String returnTo = GlobalConfig.SUBDOMAIN + "/hc/zh-cn";
                if (lan == null || "".equals(lan) || "cn".equals(lan)) {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/zh-cn";
                } else if ("en".equals(lan)) {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/en-us";
                }else if ("jp".equals(lan)) {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/ja";
                }else if ("kr".equals(lan)) {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/ko";
                }else {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/zh-tw";
                }
                if (StringUtil.exist(viewFlag) && viewFlag.equals("notice")) {
                    returnTo = GlobalConfig.SUBDOMAIN + L("公告链接");
                } else if (StringUtil.exist(viewFlag) && viewFlag.equals("app_down")) {
                    returnTo = GlobalConfig.SUBDOMAIN + L("安装说明链接");
                } else if (StringUtil.exist(viewFlag) && viewFlag.equals("googleauth")) {
                    returnTo = GlobalConfig.SUBDOMAIN + L("谷歌验证说明链接");
                }
                go(returnTo);
                return;
            }
        } catch (Exception e) {
            String returnTo = GlobalConfig.SUBDOMAIN + "/hc/zh-cn";
            if (lan != null && "en".equals(lan)) {
                returnTo = GlobalConfig.SUBDOMAIN + "/hc/en-us";
            } else if (lan != null && ("hk".equals(lan) || "tw".equals(lan))) {
                returnTo = GlobalConfig.SUBDOMAIN + "/hc/zh-tw";
            }
            if (StringUtil.exist(viewFlag) && viewFlag.equals("notice")) {
                returnTo = GlobalConfig.SUBDOMAIN + L("公告链接");
            } else if (StringUtil.exist(viewFlag) && viewFlag.equals("app_down")) {
                returnTo = GlobalConfig.SUBDOMAIN + L("安装说明链接");
            } else if (StringUtil.exist(viewFlag) && viewFlag.equals("googleauth")) {
                returnTo = GlobalConfig.SUBDOMAIN + L("谷歌验证说明链接");
            }
            go(returnTo);
            return;
        }
    }
    /*end*/

    private static final String COOKIE_KEY_OF_LOGIN_STATUS = GlobalConfig.session + "loginStatus";

    /*
    * 登录
    * */
    @Page(Viewer = JSON)
    public void doLogin() {
        try {
            if (isForbid()) {
                json( L("请求过于频繁，请稍后再试"), false,"", true);
                logger.warn(ip() + " 因频繁访问,已被禁止");
                return;
            }
            String msg = null;
            String userName = param("nike");
            String passWord = param("pwd");
            String srcDomain = param("domain");
            String returnTo = param("returnTo");
            String fromUrl = param("fromUrl");
            if (StringUtils.isEmpty(userName)) {
                json(L("电子邮件不得为空。"), false, "", true);
                return;
            }

            if(StringUtils.isNotEmpty(Cache.Get(userName+loginLock))){
                json( L("登录功能已被锁定，请2小时之后再试。"), false,"", true);
                return;
            }
            if (StringUtils.isEmpty(passWord)) {
                json(L("密码不得为空。"), false, "\"password\"", true);
                return;
            }
            if (!checkEmail(userName)) {
                msg = CommonUtil.mapToJsonStr(msg, "email", L("请输入正确的电子邮件地址。"));
                /*json(L("请输入正确的电子邮件地址"), false, "\"email\"", true);
                return;*/
            }


            String countryCode = param("countryCode");
            if (null == countryCode || "".equals(countryCode) || !countryCode.startsWith("+")) {
                countryCode = "+86";
            }
            // 需要显示验证码
            String safe = param("safe");
            String userIp = ip();

            String loginIpKey = "login_ip_" + userIp;
            int loginTimes = 1;
            Integer ci = (Integer) Cache.GetObj(loginIpKey);

            if (null != ci) {
                loginTimes = ci + 1;
                if (loginTimes > 60) {
                    json(L("您所在ip操作登录太频繁了，请过一小时后再操作"), false, "", true);
                    return;
                }
            }
            Cache.SetObj(loginIpKey, loginTimes, 60 * 60);

            if (safe == null || !safe.equals("0")) {
                // 只能是安全和不安全两种
                safe = "1";
            }
            String pub_tag = param("pubTag").replace(" ", "+");
            log.info("客户端请求参数中的公钥：" + pub_tag);
            if (passWord.length() > 0) {
                RsaUser rsaUser = RsaLoginUtil.getRsaUser(this);
                log.info(rsaUser.getPubKey());
                if (rsaUser != null) {
                    byte[] decodedData;
                    try {
                        if (!rsaUser.getPubKey().trim().equals(pub_tag)) {//更换了公钥
                            com.alibaba.fastjson.JSONObject jo = new com.alibaba.fastjson.JSONObject();
                            jo.put("id", "pub_tag");
                            jo.put("tag", rsaUser.getPubKey());
                            json(L("出错了，请重新提交信息！"), false, jo.toJSONString(), true);
                            log.info("出错了，请重新提交信息！");
                            return;
                        }
                        decodedData = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(passWord.replace(" ", "+")), rsaUser.getPriKey());
                        passWord = new String(decodedData);
                        //log.info("解密后密码=" + pwd);
                    } catch (Exception e) {
                        com.alibaba.fastjson.JSONObject jo = new com.alibaba.fastjson.JSONObject();
                        jo.put("id", "pub_tag");
                        jo.put("tag", rsaUser.getPubKey());
                        jo.put("pwd", passWord);
                        json(L("系统故障，请点击重试1001"), false, jo.toJSONString(), true);
//						log.info("密码RSA解密出错，私钥：" + rsaUser.getPriKey());
                        log.error("内部异常", e);
                        return;
                    }
                }
            }

            UserDao userDao = new UserDao();
            User safeUser = null;
            /*Start by guankaili 20181225 此变量无用 */
//            int loginType = 0;
            /*End*/
            if (userName != null) {
                userName = userName.toLowerCase().trim();
            }
			/*start by xwz 2017-06-10*/
//			if(CheckRegex.isEmail(userName)){
//				loginType = 1;//邮箱
//				safeUser = userDao.getUserByColumn(userName, "userContact.safeEmail");
//			}else if(CheckRegex.isPhoneNumber(countryCode + " " + userName)){
//				loginType = 2;//手机
//				safeUser = userDao.getUserByColumn(countryCode + " " + userName, "userContact.safeMobile");
//			}
//                loginType = 1;//邮箱
            /*Start by guankaili 20181225 优化登录逻辑 */
//            safeUser = userDao.getUserByColumn(userName, "userContact.safeEmail");
            safeUser = userDao.findOne("userContact.safeEmail",userName);
            if (null == safeUser) {
                safeUser = userDao.findOne("email",userName);
                if (null != safeUser && StringUtils.isBlank(safeUser.getUserContact().getSafeEmail())) {
                    passWord = safeUser.getEncryptedPwd(passWord);
                    String retPwdMsg = loginLimit(safeUser,userName,passWord,true);
                    if (StringUtils.isEmpty(retPwdMsg)) {
                        // 跳转到查看邮件提示页面
                        json(VIP_DOMAIN + "/bw/active?nid=" + safeUser.getId() + "&type=2&email="+safeUser.getEmail(), false, "{\"isRoute\":true}", true);
                        return;
                    }else{
                        json(retPwdMsg, false, "", true);
                        return;
                    }
                }else{
                    String retNameMsg1 = loginLimit(safeUser,userName,"",false);
                    if(StringUtils.isNotEmpty(retNameMsg1)){
                        json(retNameMsg1, false, "", true);
                        return;
                    }
                }
            }
            /*End*/
            /*end*/
            String retNameMsg = loginLimit(safeUser,userName,"",false);
            if(StringUtils.isNotEmpty(retNameMsg)){
                json(retNameMsg, false, "", true);
                return;
            }
            if (null != safeUser) {
                DataResponse dr1 = this.checkVerifiCode(safeUser.getId(), MsgToastKey.LOGIN_MOBILE_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_MOBILE, MsgToastKey.LOCK_2_HOUR);
                if (!dr1.isSuc()) {
                    json(L("登录功能已被锁定，请2小时之后再试。"), false, "", true);
                    return;
                }
                //check谷歌
                DataResponse googleDr = this.checkVerifiCode(safeUser.getId(), MsgToastKey.LOGIN_MOBILE_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_GOOGLE, MsgToastKey.LOCK_2_HOUR);
                if (!googleDr.isSuc()) {
                    json(L("登录功能已被锁定，请2小时之后再试。"), false, "");
                    return;
                }
            }

            int remember = 2;// Integer.parseInt(param("remember"));
            remember = remember * 3600;


            boolean isCheckCode = false;
            if (loginTimes > 2 && safeUser!=null) {
                // 需要显示验证码
                String code = request.getParameter("code");
                if (code == null || "".equals(code)) {
                    Cache.Delete("CodeImage_" + sessionId);
                    /*json(L("为了您的账户安全，请输入验证码"), false, "{\"status\":0}", true);*/
                    msg = CommonUtil.mapToJsonStr(msg, "code",L("为了您的账户安全，请输入验证码"));
                    json("", false, msg);
                    return;
                }
                if (!CheckCode(code)) {
                    Cache.Delete("CodeImage_" + sessionId);
                    msg = CommonUtil.mapToJsonStr(msg, "code",L("图形验证码错误，请重新输入。"));
                           /* json(L("图形验证码输入错误！"), false, "{\"status\":0}", true);
                            return;*/
                }
                isCheckCode = true;
            }


            // 如果是邮箱登录，判断邮箱是否已认证
            /*Start by guankaili 20181225 删除此步，与上面343行获取用户信息重复 */
            /*if (loginType == 1 && null == safeUser) {
                safeUser = userDao.getUserByColumn(userName, "email");
                if (null != safeUser && StringUtils.isBlank(safeUser.getUserContact().getSafeEmail()) && safeUser.getUserContact().isCanReg()) {
                    passWord = safeUser.getEncryptedPwd(passWord);
                    if (!safeUser.getPwd().equals(passWord)) {
                        *//*json(L("电子邮件或密码错误。"), false, "", true);
                        return;*//*
                        msg = CommonUtil.mapToJsonStr(msg, "password", L("电子邮件或密码错误。"));
                    }
                    // 跳转到查看邮件提示页面
                    json(VIP_DOMAIN + "/v2/active?nid=" + safeUser.getId() + "&type=2&email="+safeUser.getEmail(), false, "{\"isRoute\":true}", true);
                    return;
                }else{
                    msg = CommonUtil.mapToJsonStr(msg, "password", L("电子邮件或密码错误。"));
                }
            }*/

            /*End*/
            if(null != msg){
                json("", false, msg);
                return;
            }
            LimitType lt = LimitType.LoginError;
            int status = lt.GetStatus(safeUser.getId());

            if (status == 0) {

                Cookie c = new Cookie("LoginCode", "1");
                c.setMaxAge(60 * 60 * 2);// s为单位，1个月60*60*24,存储一天
                c.setDomain(Session.SETDOMAIN);
                c.setPath("/");
                response.addCookie(c);
                if (!isCheckCode) {
                    // 需要显示验证码
                    String code = request.getParameter("code");
                    if (code == null || "".equals(code)) {
                        Cache.Delete("CodeImage_" + sessionId);
                        msg = CommonUtil.mapToJsonStr(msg, "code",L("为了您的账户安全，请输入验证码"));
                        json("", false, msg);
                        return;
                    }
                    Boolean codeRight = CheckCode(code);
                    if (!codeRight) {
                        Cache.Delete("CodeImage_" + sessionId);
                        /*json(L("验证码错误"), false, "", true);*/
                        msg = CommonUtil.mapToJsonStr(msg, "code",L("图形验证码错误，请重新输入。"));
                    }
                }
            } else if (status == -1) {
                // 已经被锁定了禁止使用了
                json(L("密码已锁定"), false, "", true);
                return;
            }

            // 获取第一条数据的list格式返回
            if (safeUser != null) {

                passWord = safeUser.getEncryptedPwd(passWord);
                String retPwdMsg = loginLimit(safeUser,userName,passWord,true);
                if (StringUtils.isNotEmpty(retPwdMsg)) {
                    json(retPwdMsg, false, "", true);
                    return;
                }else {
                    Cookie killMyCookie = new Cookie("LoginCode", null);
                    killMyCookie.setMaxAge(0);
                    killMyCookie.setPath("/");
                    killMyCookie.setDomain(GlobalConfig.baseDomain);
                    response.addCookie(killMyCookie);

                    if (safeUser.isFreez()) {
                        log.info("该账户已冻结，暂时不能登录。");
                        json(L("该账户已冻结，暂时不能登录。"), false, "", true);
                        return;
                    }

                    boolean diffIpAuthen = false;
                    if (safeUser.isDiffAreaLoginNoCheck()) {//不用异地登录验证
                        uld.clearLoginCache(sessionId, userIp, response);
                        lt.ClearStatus(safeUser.get_Id());// 成功操作了，要重新计量
                    } else {//去掉异地登录验证
//        				Object obj = uld.getLoginCache(sessionId, userIp);//查一查是不是异地登录
//        				if (null != obj || uld.needCheckMobile(safeUser, userIp)) {//发现是，就不保存本次IP地址
//        					diffIpAuthen = true;
//        				}
                    }
                    if(null != msg){
                        json("", false, msg);
                        return;
                    }
                    toLogin(safe, remember, safeUser, userIp, diffIpAuthen, userName, this);
                    /*start by xwz 20170625 登录加积分*/
                    JifenManage jifenManager = new JifenManage(safeUser.get_Id(), 2, null, null, "VIP");//2代表登录
                    SingletonThreadPool.addJiFenThread(jifenManager);
                    /*end*/
                    /*start by guankaili 20181211 登录发邮件*/
                    if(!safeUser.getGoogleOpen() && !safeUser.getSmsOpen()){
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if("en".equals(lan)){
                            format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                        }
                        String loginTime = format.format(new Date());
//                        String info = StrUtil.format("您的账号已成功登录XX，登录IP地址：{}，登录时间：{}。",ip(),loginTime);
                        String emailInfo = eDao.getLoginEmailHtmlByInfo(safeUser, ip(), loginTime, this);
                        String title = L("登录成功");
                        //锁定发送邮件
                        eDao.sendEmail(ip(), safeUser.getId(), safeUser.getUserName(), title, emailInfo, safeUser.getUserContact().getSafeEmail());
                        /*start by guankaili 20190529 敏感用户预警*/
                        if(safeUser.isWarningUser()){
                            WarningUserAlarm(safeUser, loginTime);
                        }
                        /*end*/
                    }
                    /*end*/
                    String toUrl = VIP_DOMAIN + "/bw/trade/";
                    Cache.Delete(loginIpKey);
                    try {
                        // 清除RSA
                        RsaLoginUtil.removeRsaUser(this);
                    } catch (Exception e) {
                        log.error("内部异常", e);
                    }
                    String data = "";
                    if (safeUser.getGoogleOpen()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("loginGoogleAuth", safeUser.getGoogleOpen());
                        data = jsonObject.toJSONString();
                        toUrl = VIP_DOMAIN + "/login";
                    }
                    if (safeUser.getSmsOpen()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("loginNeedSmsOpen", safeUser.getSmsOpen());
                        data = jsonObject.toJSONString();
                        toUrl = VIP_DOMAIN + "/login";
                    }
                    boolean goSafe = false;
                    if (!safeUser.getHasSafePwd()) {
                        goSafe = true;
                    }
                    String voteUrl = "/vote";
                    String voteUrls = "/vote/";
                    String authUrl = "/authen";
                    if (voteUrl.equals(fromUrl) || voteUrls.equals(fromUrl)) {
                        toUrl = VIP_DOMAIN + voteUrl;
                    }
                    if (authUrl.equals(fromUrl)) {
                        toUrl = VIP_DOMAIN + authUrl;
                    }
                    if (fromUrl.indexOf("lucky") != -1 || fromUrl.indexOf("lottery") != -1) {
                        toUrl = VIP_DOMAIN + "/lottery";
                    }
                    int loginStatus; // 未登录
                    if (safeUser.getGoogleOpen() || safeUser.getSmsOpen()) {
                        loginStatus = LoginStatus.NEED_TWO_AUTHEN.getKey(); //需要二重验证
                    } else {
                        loginStatus = LoginStatus.HAS_LOGIN.getKey(); // 正常登录
                        UserLoginLogProducer.send(userName, safeUser.getId(), safeUser.getUserName(), userIp, 1, "");
                    }
                    Session.addCookie(COOKIE_KEY_OF_LOGIN_STATUS, String.valueOf(loginStatus), -1, false, false, this);

                    //初始化一下 pay_user_* 防止老用户没有初始化资金表
                    InitPayUserWalletProducer.send(safeUser.getId());

					/*start by xwz 登录时设置语言缓存(防止缓存被清空)
                    * modify by xwz 2017-11-08 登录时根据登录前的语言设置登录后的语言环境和用户语言
					* */
                    if (!lan.equals(safeUser.getLanguage())) {
                        try {
                            userDao.updateUserLanguage(safeUser.getId(), lan);
                            safeUser.setLanguage(lan);
                            Cache.Set("user_lan_" + safeUser.getId(), lan);
                        } catch (Exception e) {
                            log.error("web用户登录，修改用户语言出错，错误信息：" + e.toString());
                        }
                    }

                    String language = Cache.Get("user_lan_" + safeUser.getId());
                    if (StringUtils.isEmpty(language) || !lan.equals(language)) {
                        Cache.Set("user_lan_" + safeUser.getId(), lan);
                    }
                    session.addCookie(SsoSessionManager.LANGUAGE, lan, Integer.MAX_VALUE, false, false, this);

                    logger.info(">>> 2-设置cookie信息 [" + COOKIE_KEY_OF_LOGIN_STATUS + "=" + String.valueOf(loginStatus) + "] ,[" + SsoSessionManager.LANGUAGE + "=" + lan + "]");
					/*end*/

                    //20170328 modify by suxinjie 网页登录成功给app发送推送提醒
                    String jpushKey = safeUser.getJpushKey();
                    if (jpushKey != null && !"".equals(jpushKey)) {
                        try {

                            Pusher.push(L("已检测到您的账户在浏览器端登录,IP : %%", userIp), jpushKey, MsgType.loginRemind);
                        } catch (Exception e) {
                            log.error("极光推送异常, jpush-key : " + jpushKey, e);
                        }
                    }
                    CookieUtil cookieUtil = new CookieUtil(request, response);

                    /*start by xzhang 20171215  交易页面三期PRD:同步用户未登录和登录后的收藏信息*/
                    Cookie userCollect = new Cookie("userCollectMarket", userDao.getUserCollect(safeUser.getId(), cookieUtil.getCookieValue("userCollectMarket")));
                    userCollect.setMaxAge(60 * 60 * 2);// s为单位，1个月60*60*24,存储一天
                    userCollect.setDomain(Session.SETDOMAIN);
                    userCollect.setPath("/");
                    response.addCookie(userCollect);
                    /*end*/

                    /*start by kinghao 20180825  新交易平台： 处理看板信息*/
                    Cookie userScreen = new Cookie("multiTrade", userDao.getUserScreen(safeUser.getId(), cookieUtil.getCookieValue("multiTrade")));
                    userScreen.setMaxAge(0);
                    userScreen.setPath("/");
                    response.addCookie(userScreen);
                    logger.info("[用户看板] 读取看板Cookie存储,销毁Cookie："+ userScreen.getName());
                    /*end*/

                    /*start by xzhang 201701010 zendesk跨系统会话同步*/
                    try {
                        UserContact userContact = safeUser.getUserContact();
                        // Given a user instance
                        // Compose the JWT claims set
                        JWTClaimsSet jwtClaims = new JWTClaimsSet();
                        jwtClaims.setIssueTime(new Date());
                        jwtClaims.setJWTID(UUID.randomUUID().toString());
                        jwtClaims.setCustomClaim("name", safeUser.getUserName());
                        if (userContact != null && userContact.getSafeEmail() != null && !"".equals(userContact.getSafeEmail())) {
                            jwtClaims.setCustomClaim("email", userContact.getSafeEmail());
                        } else if (userContact != null && userContact.getSafeMobile() != null && !"".equals(userContact.getSafeMobile())) {
                            String[] mobiles = userContact.getSafeMobile().split(" ");
                            if (mobiles != null && mobiles.length > 1) {
                                jwtClaims.setCustomClaim("email", mobiles[1] + "@btcwinex.com");
                            } else {
                                if (safeUser.getUserName().indexOf("@") != -1) {
                                    jwtClaims.setCustomClaim("email", safeUser.getUserName());
                                } else {
                                    jwtClaims.setCustomClaim("email", safeUser.getUserName() + "@btcwinex.com");
                                }
                            }
                        } else {
                            if (safeUser.getUserName().indexOf("@") != -1) {
                                jwtClaims.setCustomClaim("email", safeUser.getUserName());
                            } else {
                                jwtClaims.setCustomClaim("email", safeUser.getUserName() + "@btcwinex.com");
                            }
                        }

                        // Create JWS header with HS256 algorithm
                        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
                        header.setContentType("text/plain");
                        // Create JWS object
                        JWSObject jwsObject = new JWSObject(header, new Payload(jwtClaims.toJSONObject()));
                        // Create HMAC signer
                        JWSSigner signer = new MACSigner(GlobalConfig.SHARED_KEY.getBytes());
                        try {
                            jwsObject.sign(signer);
                        } catch (com.nimbusds.jose.JOSEException e) {
                            log.error("当前用户" + safeUser.get_Id() + "签名失败，失败原因：", e);
                            setAttr("returnTo", "");
                            json(toUrl, true, data, true);
                            return;
                        }
                        // Serialise to JWT compact form
                        String jwtString = jwsObject.serialize();
                        String redirectUrl = GlobalConfig.SUBDOMAIN + "/access/jwt?jwt=" + jwtString;
                        if (returnTo != null && !"".equals(returnTo)) {
                            setAttr("returnTo", "");
                            json(redirectUrl, true, data, true);
                        } else {
                            setAttr("returnTo", "");
                            data = sessionId;
                            //json(toUrl, true, data, true);

                            json(toUrl, true, "{\"sessionId\":\"" + data + "\"}", true);
                        }
                    } catch (Exception e) {
                        log.error("当前用户" + safeUser.get_Id() + "会话同步失败，失败原因：", e);
                        json(toUrl, true, "", true);
                    }
                    /**end**/
                }
            }
            if(null != msg){
                json("", false, msg);
                return;
            }
        } catch (Exception e) {
            log.error("10100203VIPDL【登录】 com.world.controller.Login#doLogin",e);
            json(L("登录失败"), false, "", true);
        }
    }

    /**
     * 校验登录邮箱和密码
     * @param user
     * @param email
     * @param password
     * @param flg
     * @return
     */
    public String loginLimit(User user,String email ,String password,boolean flg) {

        int status = new UserDao().checkLoginTimes(user,email, password,flg);
        String msg = "";
        if (status == -2) {
            msg = L("您的账户被锁定，请%%小时后尝试", MsgToastKey.LOCK_2_HOUR);
        } else if (status == -3) {
            return msg;
        } else {
            if(lan.equals("en")){
                if(status > 1){
                    msg = L("用户名或密码错误，您还有%%次机会s", String.valueOf(status));
                }else{
                    msg = L("用户名或密码错误，您还有%%次机会", String.valueOf(status));
                }
            }else{
                msg = L("用户名或密码错误，您还有%%次机会", String.valueOf(status));
            }
        }
        return msg;
    }

    /**
     * 敏感用户预警
     * @param safeUser
     * @param loginTime
     */
    private void WarningUserAlarm(User safeUser, String loginTime) {
        JSONArray funds = UserCache.getUserFunds(safeUser.getId());
        String tipInfo = "普通用户【{}】已上线，上线时间为【{}】，账户余额{},请运营人员注意！";
        String balanceStr = "";
        if(!funds.isEmpty()){
            for (int i = 0; i < funds.size(); i++) {
                JSONObject obj = funds.getJSONObject(i);
                BigDecimal balance = obj.getBigDecimal("balance");
                String coinName = obj.getString("propTag");
                if(balance.compareTo(BigDecimal.ZERO) > 0){
                    if(StringUtils.isEmpty(balanceStr)){
                        balanceStr = "【"+balance.toPlainString()+coinName+"】";
                    }else{
                        balanceStr += "、"+"【"+balance.toPlainString()+coinName+"】";
                    }
                }
            }
        }else{
            PayUserDao payUserDao = new PayUserDao();
            List<PayUserBean> list = payUserDao.getHoldCoin(safeUser.getId());
            if(CollectionUtils.isNotEmpty(list)){
                for(PayUserBean payUserBean : list){
                    String balance = payUserBean.getBalance().toPlainString();
                    String coinName = DatabasesUtil.coinProps(payUserBean.getFundsType()).getPropTag();
                    if(StringUtils.isEmpty(balanceStr)){
                        balanceStr = "【"+balance+coinName+"】";
                    }else{
                        balanceStr = "、"+"【"+balance+coinName+"】";
                    }
                }
            }
        }
        if(StringUtils.isEmpty(balanceStr)){
            balanceStr = "【此用户未持有币种。】";
        }
        log.info("10100203MGYH【敏感用户】"+ StrUtil.format(tipInfo,safeUser.getId(),loginTime,balanceStr));
    }


    public boolean needAuthen() {
        initLoginUser();
        if (loginUser != null) {
            SessionUser su = session.getUser(this);
            JSONObject others = su.getOthers();
            boolean twice = false;
            if (others != null) {
                if (others.getBooleanValue("loginNeedSmsOpen")) {
                    setAttr("needSmsOpen", "1");
                    twice = true;
                }
                if (others.getBooleanValue("loginNeedGoogleAuth")) {
                    setAttr("needGoogle", "1");
                    twice = true;
                }
            }
            if (!twice) {
                go("/manage");
            } else {
                setAttr("loginUser", loginUser);
            }
            return twice;
        }
        return false;
    }















    /*
     * 登陆二次认证
     * */
    @Page(Viewer = JSON)
    public void doLoginAuthen() {
        try {
            if (isForbid()) {
                return;
            }
            String errorMsg = null;
            initLoginUser();
            if (loginUser != null) {
                SessionUser su = session.getUser(this);

                JSONObject others = su.getOthers();
                boolean googleAuth = false;
                boolean isSmsOpen = false;
                if(StringUtils.isNotEmpty(Cache.Get(loginUser.getUserName()+loginLock))){
                    json( L("登录功能已被锁定，请2小时之后再试。"), false,"", true);
                    return;
                }


                //手机验证
                PostCodeType pct = PostCodeType.logVIP;
                LimitType lType = LimitType.LoginMobileError;
                String mobileCode = param("mobileCode");
                // 检查短信验证码
                String codeRecvAddr = loginUser.getUserContact().getSafeMobile();
                if (StringUtils.isBlank(codeRecvAddr)) {
                    codeRecvAddr = loginUser.getUserContact().getSafeEmail();
                }
                DataResponse dr1 = this.checkVerifiCode(loginUser.getId(),MsgToastKey.LOGIN_MOBILE_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_MOBILE, MsgToastKey.LOCK_2_HOUR);
                if (!dr1.isSuc()) {
                    json( L("登录功能已被锁定，请2小时之后再试。"), false,"", true);
                    return;
                }
                //check谷歌
                DataResponse googleDr = this.checkVerifiCode(loginUser.getId(),MsgToastKey.LOGIN_MOBILE_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_GOOGLE, MsgToastKey.LOCK_2_HOUR);
                if (!googleDr.isSuc()) {
                    json(L("登录功能已被锁定，请2小时之后再试。"), false, "");
                    return;
                }

                if (others != null) {
                    //登录短信二次验证
                    isSmsOpen = others.getBooleanValue("loginNeedSmsOpen");
                    //登录谷歌二次验证
                    googleAuth = others.getBooleanValue("loginNeedGoogleAuth");
                }
                if (loginUser.getSmsOpen()) {
                    isSmsOpen = true;
                }
                if (loginUser.getGoogleOpen()) {
                    googleAuth = true;
                }
                Message message = new Message();
                Boolean flag = false;
                long code = longParam("code");
                if (googleAuth && code !=0) {
                    if (code == 0L) {
                        json(L("密码已锁定"), false, "", true);
                        return;
                    }
                    UserContact uc = loginUser.getUserContact();
                    String savedSecret = uc.getSecret();

                    message = isCorrectLoginMsg(savedSecret, code, JSON,errorMsg);
                    errorMsg = message.getMsg();
                    /*if (!isCorrectLogin(savedSecret, code, JSON)) {
                        return;
                    }*/
                    if(message.isSuc()){
                        flag = true;
                    }
                }

                if (isSmsOpen && !StringUtils.isEmpty(mobileCode) && !flag) {
                    if ("0".equals(mobileCode)) {
                        json(L("密码已锁定"), false, "", true);
                        return;
                    }

                    /*Start by guankaili 20190104 运营后台添加功能锁修改 */
    //                ClientSession clientSession = new ClientSession(ip(), codeRecvAddr, lan, PostCodeType.logVIP.getValue(), false);
    //                DataResponse dr = clientSession.checkCodeWithTime(mobileCode, 2, true);
    //                if (!dr.isSuc()) {
    //                    if (dr.getDes().equals(Lan.LanguageFormat(lan, "验证码输入错误次数过多,已被锁定,请%%小时后重试。", "2"))) {
    //                        dr.setDes(L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"));
    //                    }
    //                    errorMsg = CommonUtil.mapToJsonStr(message.getMsg(),"codesms",dr.getDes());
    //                }
                    //手机验证

                    DataResponse dr = getDataResponse(loginUser,pct,codeRecvAddr,2,mobileCode,lType, MsgToastKey.LOGIN_MOBILE_VERIFY,MsgToastKey.LOCK_2_HOUR);
                    if (!dr.isSuc()) {
                        errorMsg = CommonUtil.mapToJsonStr(message.getMsg(),"codesms",dr.getDes());
                    }
                    if(dr.isSuc()){
                        flag = true;
                    }
                }
                if(!googleAuth && !isSmsOpen && !flag){
                    //防止直接掉接口
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("isJump",true);
                    json(L("登录账号已失效"), false, jsonObject.toJSONString());
                    return;
                }
                if(StringUtils.isNotEmpty(errorMsg) && !flag){
                    json("", false, errorMsg);
                    return;
                }
                String key = loginUser.getId() + "_gauth";
                Cache.Delete(key);
                if(StringUtils.isNotEmpty(loginUser.getUserContact().getSafeMobile())){
                    ClientSession clientSession = new ClientSession(ip(), loginUser.getUserContact().getSafeMobile(), lan, PostCodeType.logVIP.getValue(), false);
                    clientSession.deleteSession(2);
                    clientSession.clearDeviceNumberLock(loginUser.getUserContact().getSafeMobile());
                }
                /*Start by guankaili 20181229 添加消息队列 */
    //            new OperateLogDao().record(loginUser, LogCategory.OTHER_PLACE_LOGIN, "登录成功", ip(), request);
                OperateLogInfoProducer.send(loginUser.getUserName(),loginUser.getId(),LogCategory.OTHER_PLACE_LOGIN.getKey(),"登录成功", ip(),CommonUtil.getBrowserInfo(request));
                /*End*/
               /* if(!flag){
                    json(L("未知错误"), false, "");
                    return;
                }*/
                Session.addCookie(COOKIE_KEY_OF_LOGIN_STATUS, String.valueOf(LoginStatus.HAS_LOGIN.getKey()), -1, false, false, this);

                LimitType lt = LimitType.LoginError;
                String userIp = ip();
                uld.clearLoginCache(sessionId, userIp, response);
                userDao.clearMobileCode(loginUser.getId());
                lt.ClearStatus(loginUser.get_Id());// 成功操作了，要重新计量
                /*Start by guankaili 20181229 添加消息队列 */
    //            uld.add(su.getOthers().getString("loginName"), loginUser.getId(), loginUser.getUserName(), userIp, 1, "");// 保存登录IP
                UserLoginLogProducer.send(su.getOthers().getString("loginName"), loginUser.getId(), loginUser.getUserName(), userIp, 1, "");
                /*End*/
                /*start by guankaili 20181211 登录发邮件*/
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if("en".equals(lan)){
                    format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                }
                String loginTime = format.format(new Date());
    //            String info = StrUtil.format("您的账号已成功登录XX，登录IP地址：{}，登录时间：{}。",ip(),loginTime);
                String emailInfo = eDao.getLoginEmailHtmlByInfo(loginUser, ip(), loginTime, this);
                String title = L("登录成功");
                //锁定发送邮件
                eDao.sendEmail(ip(), loginUser.getId(), loginUser.getUserName(), title, emailInfo, loginUser.getUserContact().getSafeEmail());
                /*end*/
                /*start by guankaili 20190529 敏感用户预警*/
                if(loginUser.isWarningUser()){
                    WarningUserAlarm(loginUser, loginTime);
                }
                /*end*/
                su.others.put("loginNeedSmsOpen", false);
                su.others.put("loginNeedGoogleAuth", false);
                SSOLoginManager.resave(loginUser.getId(), su);

                session.expired(session.smsauth, "false", this);
                session.expired(session.googleauth, "false", this);
                Map map = new HashMap();
                map.put("isRoute",true);
                map.put("sessionId",sessionId);
                String newMsg = JSONObject.toJSONString(map);
                json(VIP_DOMAIN + "/bw/trade/", true, newMsg);
                return;
            }
        }catch (Exception e){
            log.error("10100203VIPDLRZ【登录二次认证】 com.world.controller.Login#doLoginAuthen",e);
        }
    }

    // 完成登录相关的操作
    public void toLogin(String safe, int remember, User safeUser, String ip, boolean diffIpAuthen, String loginName, Pages p) {
        try {
            JSONObject json = new JSONObject();
            UserContact uc = safeUser.getUserContact();
            if (null != uc) {
                json.put("emailStatu", uc.getEmailStatu());
                json.put("mobileStatu", uc.getMobileStatu());
                json.put("googleAuth", uc.getGoogleAu());
            }

            String pwdStatus = "1", safePwdStatus = "1";
            if (null != safeUser.getPwd() && !"".equals(safeUser.getPwd())) {
                pwdStatus = "2";
            }
            if (null != safeUser.getSafePwd() && !"".equals(safeUser.getSafePwd())) {
                safePwdStatus = "2";
            }
            json.put("pwdStatus", pwdStatus);
            json.put("safePwdStatus", safePwdStatus);
            json.put("ipNeedAuthen", diffIpAuthen);
            json.put("loginNeedSmsOpen", safeUser.getSmsOpen());
            json.put("loginNeedGoogleAuth", safeUser.isLoginGoogleAuth());
            json.put("loginName", loginName);

            logger.info(">>> userContact : " + json.toString());

            if (diffIpAuthen) {
                /*Start by guankaili 20181229 添加消息队列 */
//                new OperateLogDao().record(safeUser, LogCategory.OTHER_PLACE_LOGIN, "异地登录待验证", ip(), request);
                OperateLogInfoProducer.send(loginUser.getUserName(),loginUser.getId(),LogCategory.OTHER_PLACE_LOGIN.getKey(),"异地登录待验证", ip(),CommonUtil.getBrowserInfo(request));
                /*End*/
            }

            //添加异地登录和谷歌认证的cookie
            p.session.addCookie(session.ipauth, String.valueOf(diffIpAuthen), remember, false, false, p);
            p.session.addCookie(session.googleauth, String.valueOf(safeUser.isLoginGoogleAuth()), remember, false, false, p);

            logger.info(">>> 1-设置cookie信息 [" + session.ipauth + "=" + String.valueOf(diffIpAuthen) + "] ,[" + session.googleauth + "=" + String.valueOf(safeUser.isLoginGoogleAuth()) + "]");

            SSOLoginManager.toLogin(p, remember, safeUser.getId(), safeUser.getUserName(), true, ip, safeUser.getVipRate() + "", "", false, json);

            Datastore ds = userDao.getDatastore();
            Query<User> query = ds.find(User.class, "_id", safeUser.getId());
            userDao.update(query, ds.createUpdateOperations(User.class).set("previousLogin", safeUser.getLastLoginTime() == null ? now() : safeUser.getLastLoginTime()).set("lastLoginTime", now()).set("previousLoginIp", safeUser.getLoginIp() == null ? ip : safeUser.getLoginIp()).set("loginIp", ip).set("trueIp", safeUser.getLoginIp() == null ? ip : safeUser.getLoginIp()));

            //更新内存里,cookie里的vip等级
            SSOLoginManager.updateVip(safeUser.getId() + "", safeUser.getVipRate());

        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    @Page(Viewer = ".xml")
    public void logout() {
        try {
            User user = userDao.getUserById(userIdStr());
            String toUrl = LOGIN;
            if (user == null) {
                log.info("用户登出异常 : user is null ,可能是session已失效");
                response.sendRedirect(toUrl);
                return;
            }
            /*Start by guankaili 20181229 添加消息队列 */
//            new OperateLogDao().record(user, LogCategory.LOGOUT, "退出登录", ip(), request);
            OperateLogInfoProducer.send(user.getUserName(),user.getId(),LogCategory.LOGOUT.getKey(),"退出登录", ip(),CommonUtil.getBrowserInfo(request));
            /*End*/
            /*start by xzhang 20171215 交易页面三期PRD:用户退出清空缓存*/
          /*  Cookie userCollect = new Cookie("userCollectMarket", "");
            userCollect.setMaxAge(60 * 60 * 2);// s为单位，1个月60*60*24,存储一天
            userCollect.setDomain(Session.SETDOMAIN);
            userCollect.setPath("/");
            response.addCookie(userCollect);*/

            SSOLoginManager.logout(this, false);
            WriteRight("Logout success!");

            if (HttpRequestDeviceUtils.isMobileDevice(request)) {//手机访问
                toUrl = MOBILE_DOMAIN;
            }
            response.sendRedirect(toUrl);
        } catch (IOException e) {
            log.error("内部异常", e);
        }

    }

    @Page(Viewer = ".xml")
    public void activityLogout() {
        SSOLoginManager.logout(this, false);
        WriteRight("Logout success!");
        try {
            response.sendRedirect(VIP_DOMAIN + "/activity");
        } catch (IOException e) {
            log.error("内部异常", e);
        }
    }

    @Page(Viewer = JSON)
    public void getPubTag() {
        RsaUser rsaUser = RsaLoginUtil.getRsaUser(this);
        json("", true, "{\"pubTag\":\"" + rsaUser.getPubKey() + "\"}");
    }

    public String getPwdRegTarStr() {
        String saveCode = Cache.Get("CodeImage_" + sessionId);
        String md5 = MD5.toMD5(saveCode + "_yanghe");
        Cache.Set("md5CurrentCodeImage_" + sessionId, md5, 60 * 60 * 2);
        return md5;
    }

//    @Page(Viewer = "/cn/user/emailTips.jsp")
    public void emailTips() {
        String nid = param("nid");
        int type = intParam("type");// 提示类型 1：注册邮件  2：重发注册邮件
        User user = userDao.get(nid);
        setAttr("email", userDao.shortEmail(user.getEmail()));
        setAttr("nid", nid);
        setAttr("type", type);
    }

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }



    @Page(Viewer = JSON)
    public void checkCode() {
        String userIp = ip();
        String codeRecvAddr = StringUtils.isNotBlank(param("email")) ? param("email").toLowerCase().trim() : "";
        String code = param("code");
        String type = param("type");
        Boolean flag = booleanParam("flag");
        String token = param("token");
        int sendType = 1 ;
        String function = Const.function_forget_password;
        if (loginUser != null) {
            initLoginUser();
            codeRecvAddr = loginUser.getUserName();
            function = Const.function_forget_two_auth;

//            if(StringUtils.isNotEmpty(Cache.Get(loginUser.getUserName()+loginLock))){
//                json( L("登录功能已被锁定，请2小时之后再试。"), false,"", true);
//                return;
//            }
        }

        // 前置基础校验
        User user = userDao.getUserByColumn(codeRecvAddr, "email");
        if (user == null) {
            this.toast(L("用户不存在"));
            return;
        }

        if(StringUtils.isEmpty(type)){
            codeRecvAddr = user.getUserContact().getSafeMobile();
            sendType = 2;
        }
        int icodeType = intParam("codeType");//验证码类型
        PostCodeType postCodeType = (PostCodeType) EnumUtils.getEnumByKey(icodeType, PostCodeType.class);
        String codeType = postCodeType.getValue();
        ClientSession clientSession = new ClientSession(userIp, codeRecvAddr, lan, codeType, false);
        DataResponse dr = null;
        if(sendType == 1){
            if(!flag){
//                if((null == loginUser && StringUtils.isNotEmpty(Cache.Get(LimitType.ForgetPassword.name()+"_"+codeRecvAddr))) ||(null == loginUser && StringUtils.isNotEmpty(Cache.Get(LimitType.ForgetPassword.name()+"_"+user.getUserName())))){
//                    dr = new DataResponse();
//                    dr.setDes(L("验证码输入次数超出限制，将锁定忘记密码功能，请24小时之后再试。"));
//                }else{
                    /*Start by guankaili 20190104 功能锁添加 */
//                    dr = clientSession.checkCodeMail(code);
//                    if(dr.getDes().equals(Lan.LanguageFormat(lan, "验证码输入错误次数过多,已被锁定,请24小时后重试。", ""))){
//                        dr.setDes(L("验证码输入次数超出限制，将锁定忘记密码功能，请24小时之后再试"));
//                        if(null == Cache.Get(LimitType.ForgetPassword.name()+"_"+codeRecvAddr)){
//                            Cache.Set(LimitType.ForgetPassword.name()+"_"+codeRecvAddr,"3",aliviableMAXTimes);
//                        }
//                    }
                    //邮箱验证
//                    PostCodeType pct = PostCodeType.closeEmailVerify;
                    LimitType lt = null;
                    if(null == loginUser){
                        lt = LimitType.ForgetPwdEmailError;
                        dr = getDataResponse(user,postCodeType,codeRecvAddr,1,code,lt,MsgToastKey.FORGORT_PWD_EMAIL_VERIFY,MsgToastKey.LOCK_24_HOUR);
                    }else{
                        lt = LimitType.NoSecondVrifyEmailError;
                        dr = getDataResponse(user,postCodeType,codeRecvAddr,1,code,lt,MsgToastKey.CLOSE_SECOND_VERIFY_MOBILE_VERIFY,MsgToastKey.LOCK_24_HOUR);
                    }
                    /*End*/
//                }
            }else{
                dr = clientSession.checkOnlyCode(code);
            }
        } else {
            if(icodeType == PostCodeType.logVIP.getKey()){
                dr = clientSession.checkCodeWithTime(code, 2, !flag);
            }else{
                if(!flag){
                    /*Start by guankaili 20190104 功能锁添加 */
//                    dr = clientSession.checkCode(code);
//                    if(dr.getDes().equals(Lan.LanguageFormat(lan, "验证码输入错误次数过多,已被锁定,请24小时后重试。", ""))){
//                        dr.setDes(L("验证码输入次数超出限制，将锁定忘记密码功能，请24小时之后再试"));
//                        if(null == Cache.Get(LimitType.ForgetPassword.name()+"_"+user.getUserName())){
//                            Cache.Set(LimitType.ForgetPassword.name()+"_"+user.getUserName(),"3",aliviableMAXTimes);
//                        }
//                    }
                    DataResponse dr1 = this.checkVerifiCode(user.getId(),MsgToastKey.FORGET_LOGIN_PWD, ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_MOBILE, MsgToastKey.LOCK_24_HOUR);
                    if (!dr1.isSuc()) {
                        json( L("忘记登录密码功能已被锁定，请24小时之后再试。"), false,"");
                        return;
                    }
                    //check谷歌
                    DataResponse googleDr = this.checkVerifiCode(user.getId(),MsgToastKey.FORGET_LOGIN_PWD, ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_GOOGLE, MsgToastKey.LOCK_24_HOUR);
                    if (!googleDr.isSuc()) {
                        json(L("忘记登录密码功能已被锁定，请24小时之后再试。"), false, "");
                        return;
                    }
                    //手机验证
                    LimitType lt1 = LimitType.ForgetPwdMobileError;
                    dr = getDataResponse(user,postCodeType,codeRecvAddr,2,code,lt1,MsgToastKey.FORGORT_PWD_MOBILE_VERIFY,MsgToastKey.LOCK_24_HOUR);
                    /*End*/
                }else{
                    dr = clientSession.checkOnlyCode(code);
                }
            }

        }
        //关闭二次验证时，session中已存在部分用户信息
        if (loginUser != null) {
            if (sendType == 2) {//短信
                if (dr.getDes().equals(Lan.LanguageFormat(lan, "验证码输入错误次数过多,已被锁定,请%%小时后重试。", "2"))) {
                    dr.setDes(L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"));
                    if (null == Cache.Get(loginUser.getUserName() + loginLock)) {
                        Cache.Set(loginUser.getUserName() + loginLock, "3", 2 * 60 * 60);
                    }
                }
            } else {//邮箱
                if (dr.getDes().equals(Lan.LanguageFormat(lan, "验证码输入次数超出限制，将锁定忘记登录密码功能，请24小时之后再试。", ""))) {
                    dr.setDes(L("验证码输入次数超出限制，将锁定关闭二次验证功能，请24小时之后再试。"));
                    if (null == Cache.Get(LimitType.ForgetGoogle.name() + "_" + codeRecvAddr)) {
                        Cache.Set(LimitType.ForgetGoogle.name() + "_" + codeRecvAddr, "3", aliviableMAXTimes);
                    }
                }
            }
        }
        if (dr.isSuc()) {
            String miyao = "";

            if(StringUtils.isEmpty(token)){
                 miyao = EncryptionPhoto.getToken(function,user.getUserName());
            }
            successToken(user.get_Id(), miyao);
            return;
        }
        if(sendType == 1){
            this.textViewErr("emcode", dr.getDes());
        }else{
            this.textViewErr("smscode", dr.getDes());
        }
    }



    @Page(Viewer = JSON)
    public void userCheckType() {
        JSONObject jsonObject = new JSONObject();
        String email = StringUtils.isNotBlank(param("email")) ? param("email").toLowerCase() : "";
        User user = userDao.getUserByColumn(email, "email");
        if (null == user) {
            json("用户不存在，请输入正确的用户名", false, "");
            return;
        }
        //用户是否有充值过
        jsonObject.put("isRecharge", false);
        List<DetailsSummaryBean> detailsBeanList = Data.QueryT("select * from detailssummary where userId = ?",new Object[]{user.get_Id()},DetailsSummaryBean.class);
        if(CollectionUtils.isNotEmpty(detailsBeanList)){
            jsonObject.put("isRecharge", true);
        }
        //用户是否有实名认证过
        if (AuditStatus.a1Pass.getKey() == user.getUserContact().getCardStatu()) {
            jsonObject.put("isAuthen", true);
        } else {
            jsonObject.put("isAuthen", false);
        }
        //用户是否设置资金密码
        jsonObject.put("isSafePwd", user.getHasSafePwd());
        Response.append(jsonObject.toString());
        return;
    }


    @Page(Viewer = JSON)
    public void addressAuth() {
        String propTag = param("propTag");
        String address = param("address");
        String userName = param("userName");
        String token = param("token");
        initLoginUser();
        userName = loginUser.getUserName();

        User user = userDao.getUserByColumn(userName, "email");
        if (null == user) {
            toast(L("用户不存在，请输入正确的用户名"));
            return;
        }
        DataResponse dr1 = this.checkVerifiCode(user.getId(),MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_ADDRESS, MsgToastKey.LOCK_24_HOUR);
        if (!dr1.isSuc()) {
            json( L("充值地址输入次数超出限制，将锁定关闭二次验证功能，请24小时之后再试。"), false,"");
            return;
        }
        DataResponse dr2 = this.checkVerifiCode(user.getId(),MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
        if (!dr2.isSuc()) {
            json( L("资金密码输入次数超出限制，将锁定关闭二次验证功能，请24小时之后再试。"), false,"");
            return;
        }
        /*Start by guankaili 20181226 改为调用用户中心接口 */
        /*String count = Cache.Get(userName + "_address");
        int maxErrorCount = 0;
        if (StringUtils.isNotEmpty(count)) {
            maxErrorCount = Integer.valueOf(count);
            if (maxErrorCount > codeAvailableMAXTimes) {
                textViewErr("payAddress", L("充值地址输入次数超出限制，将锁定关闭二次验证功能，请24小时之后再试。"));
                if(null == Cache.Get(LimitType.addAddress.name()+"_"+userName)){
                    Cache.Set(LimitType.addAddress.name()+"_"+userName,"3",aliviableMAXTimes);
                }
                return;
            }
        }
        if (StringUtils.isEmpty(propTag)) {
            textViewErr("payAddress", L("请输入正确的币种信息"));
            return;
        }
        CoinProps coinProps = DatabasesUtil.coinProps(propTag);
        DetailsBean detailsBean = detailsDao.queryOneByAddress(address, coinProps.getFundsType(),loginUser.get_Id());
        if (null == detailsBean) {
            Cache.Set(userName + "_address", String.valueOf(maxErrorCount), aliviableMAXTimes);
            maxErrorCount ++;
            if((codeAvailableMAXTimes-maxErrorCount) > 1){
                textViewErr("payAddress", (Lan.LanguageFormat(lan , "输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会s" , String.valueOf(codeAvailableMAXTimes-maxErrorCount))));
            }else{
                textViewErr("payAddress", (Lan.LanguageFormat(lan , "输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会。" , String.valueOf(codeAvailableMAXTimes-maxErrorCount))));
            }
            Cache.Set(userName + "_address", String.valueOf(maxErrorCount));
            return;
        }*/
        Cache.Delete(userName + "_address");
        String url = ApiConfig.getValue("usecenter.url");
        FeignContainer container = new FeignContainer(url.concat("/user"));
        UserApiService userApiService = container.getFeignClient(UserApiService.class);
        Map<String, String> retMap = userApiService.addressAuthApiN(user.getId(),propTag.toLowerCase(),address,user.getUserContact().getSafeEmail(),ip());
        for(Map.Entry<String, String> entry : retMap.entrySet()){
            String returnVal = entry.getValue();
            String key = entry.getKey();
            if(!"1".equals(key)){
                String[] errorMsg = returnVal.split("#");
                String msgInfo = errorMsg[0];
                if(errorMsg.length != 2){
                    textViewErr("payAddress", L("充值地址输入次数超出限制，将锁定关闭二次验证功能，请24小时之后再试。"));
                }else{
                    String chances = errorMsg[1];
                    textViewErr("payAddress", Lan.LanguageFormat(lan , msgInfo ,chances));
                }
                return;
            }
        }
        /*End*/
        if(!EncryptionPhoto.checkToken(Const.function_forget_two_auth,userName,token)){
            toast("越权请求");
            return;
        }
        user.setGoogleOpen(false);
        user.setSmsOpen(false);
        userDao.save(user);
        if(loginUser != null){
            SessionUser su = session.getUser(this);
            session.expired(COOKIE_KEY_OF_LOGIN_STATUS, "0", this);
            LimitType lt = LimitType.LoginError;
            String userIp = ip();
            uld.clearLoginCache(sessionId, userIp, response);
            userDao.clearMobileCode(loginUser.getId());
            lt.ClearStatus(loginUser.get_Id());// 成功操作了，要重新计量
            /*Start by guankaili 20181229 添加消息队列 */
            // 保存登录IP
//            uld.add(su.getOthers().getString("loginName"), loginUser.getId(), loginUser.getUserName(), userIp, 1, "");
            UserLoginLogProducer.send(su.getOthers().getString("loginName"), loginUser.getId(), loginUser.getUserName(), userIp, 1, "");
            /*End*/
            su.others.put("loginNeedSmsOpen", false);
            su.others.put("loginNeedGoogleAuth", false);
            SSOLoginManager.resave(loginUser.getId(), su);

            session.expired(session.smsauth, "false", this);
            session.expired(session.googleauth, "false", this);
            Cache.Delete(Const.function_forget_two_auth + userName);
            redirectTrue(VIP_DOMAIN + "/bw/trade/");
        }else{
            redirect(VIP_DOMAIN + "/bw/login");
        }
        return;
    }


    @Page(Viewer = JSON)
    public void idCardAuth() {
        String userName = param("userName");
        String imgUrl = param("imgUrl");
        String token = param("token");
        initLoginUser();
        userName = loginUser.getUserName();
        if (StringUtils.isEmpty(userName)) {
            json("请输入用户名", false, "");
            return;
        }

        User user = userDao.getUserByColumn(userName, "email");
        if (null == user) {
            json("用户不存在，请输入正确的用户名", false, "");
            return;
        }
        DataResponse dr1 = this.checkVerifiCode(user.getId(),MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_ADDRESS, MsgToastKey.LOCK_24_HOUR);
        if (!dr1.isSuc()) {
            json( L("充值地址输入次数超出限制，将锁定关闭二次验证功能，请24小时之后再试。"), false,"");
            return;
        }
        DataResponse dr2 = this.checkVerifiCode(user.getId(),MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
        if (!dr2.isSuc()) {
            json( L("资金密码输入次数超出限制，将锁定关闭二次验证功能，请24小时之后再试。"), false,"");
            return;
        }
        if (StringUtils.isEmpty(imgUrl)) {
            json("上传图片不能为空", false, "");
            return;
        }
        if(!EncryptionPhoto.checkToken(Const.function_forget_two_auth,userName,token)){
            toast("越权请求");
            return;
        }
        String sql = "insert into doublecheck (`userId`, `type`, `createTime`, `url`) values (?,?,?,?)";
        Object[] param = new Object[]{user.get_Id(), 1, now(),imgUrl};
        int state = Data.Insert(sql, param);
        Cache.Delete(Const.function_forget_two_auth + userName);
        /*start by xzhang 20171215 交易页面三期PRD:用户退出清空缓存*/
        Cookie userCollect = new Cookie("userCollectMarket", "");
        userCollect.setMaxAge(60 * 60 * 2);// s为单位，1个月60*60*24,存储一天
        userCollect.setDomain(Session.SETDOMAIN);
        userCollect.setPath("/");
        response.addCookie(userCollect);

        SSOLoginManager.logout(this, false);
//        WriteRight("Logout success!");
        json(VIP_DOMAIN, true, "");
        return;
}

    @Page(Viewer = JSON)
    public void checkIsLock() {
        try {
            initLoginUser();

            if (null == loginUser) {
                json(L("未登录系统，请先登录!"), false, "{\"isLogin\" : false}");
                return;
            }

            String userName = loginUser.getUserName();

            User user = userDao.getUserByColumn(userName, "email");
            DataResponse dr1 = this.checkVerifiCode(user.getId(),MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_ADDRESS, MsgToastKey.LOCK_24_HOUR);
            if (!dr1.isSuc()) {
                json( L("关闭二次验证功能已被锁定，请24小时之后再试。"), false,"");
                return;
            }
            DataResponse dr2 = this.checkVerifiCode(user.getId(),MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
            if (!dr2.isSuc()) {
                json( L("关闭二次验证功能已被锁定，请24小时之后再试。"), false,"");
                return;
            }
            json( L("验证通过"), true,"");
        }catch (Exception e){
            log.error("出错了", e);
        }

    }

    @Page(Viewer = JSON)
    public void checkSafe() {
        String userName = param("userName");
        String safePwd = param("safePwd");
//        String token = param("token");
//        if(!EncryptionPhoto.checkToken(Const.function_forget_two_auth,userName,token)){
//            toast("越权请求");
//            return;
//        }
        initLoginUser();
        userName = loginUser.getUserName();

        User user = userDao.getUserByColumn(userName, "email");
        if (null == user) {
            toast(L("用户不存在，请输入正确的用户名"));
            return;
        }
        DataResponse dr1 = this.checkVerifiCode(user.getId(),MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_ADDRESS, MsgToastKey.LOCK_24_HOUR);
        if (!dr1.isSuc()) {
            json( L("充值地址输入次数超出限制，将锁定关闭二次验证功能，请24小时之后再试。"), false,"");
            return;
        }
        DataResponse dr2 = this.checkVerifiCode(user.getId(),MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
        if (!dr2.isSuc()) {
            json( L("资金密码输入次数超出限制，将锁定关闭二次验证功能，请24小时之后再试。"), false,"");
            return;
        }
        if (StringUtils.isEmpty(safePwd)) {
            textViewErr("payPwd", L("请输入资金密码"));
            return;
        }
        int status = new UserDao().checkSecurityPwdForSecendVerify(safePwd, user.get_Id());
        if(status==-2){
            textViewErr("payPwd", L("资金密码输入次数超出限制，将锁定关闭二次验证功能，请24小时之后再试。"));
            if(null == Cache.Get(LimitType.ForgetGoogle.name()+"_"+userName)){
                Cache.Set(LimitType.ForgetGoogle.name()+"_"+userName,"3",aliviableMAXTimes);
            }
            return;
        }else if(status==0){
            toast(L("资金密码未设置"));
            return;
        }else if(status==-3){
            user.setGoogleOpen(false);
            user.setSmsOpen(false);
            userDao.save(user);
            if(loginUser != null){
                SessionUser su = session.getUser(this);
                session.expired(COOKIE_KEY_OF_LOGIN_STATUS, "0", this);
                LimitType lt = LimitType.LoginError;
                String userIp = ip();
                uld.clearLoginCache(sessionId, userIp, response);
                userDao.clearMobileCode(loginUser.getId());
                lt.ClearStatus(loginUser.get_Id());// 成功操作了，要重新计量
                /*Start by guankaili 20181229 添加消息队列 */
                // 保存登录IP
//                uld.add(su.getOthers().getString("loginName"), loginUser.getId(), loginUser.getUserName(), userIp, 1, "");
                UserLoginLogProducer.send(su.getOthers().getString("loginName"), loginUser.getId(), loginUser.getUserName(), userIp, 1, "");
                /*End*/
                su.others.put("loginNeedSmsOpen", false);
                su.others.put("loginNeedGoogleAuth", false);
                SSOLoginManager.resave(loginUser.getId(), su);

                session.expired(session.smsauth, "false", this);
                session.expired(session.googleauth, "false", this);
                Cache.Delete(Const.function_forget_two_auth + userName);
                redirectTrue(VIP_DOMAIN + "/bw/trade/");
            }else{
                redirect(VIP_DOMAIN + "/bw/login");
            }
            return;
        }else{
            if(status > 1){
                textViewErr("payPwd", L("输入有误，您还有%%次机会s。",status+""));
            }else{
                textViewErr("payPwd", L("输入有误，您还有%%次机会。",status+""));
            }
            return;
        }


    }


    @Page(Viewer = JSON)
    public void userStateCheck(){
        String userName = StringUtils.isNotBlank(param("email")) ? param("email").toLowerCase().trim() : "";
        JSONObject jsonObject = new JSONObject();
        User user = userDao.getUserByColumn(userName, "email");
        if (null == user) {
            toast(L("用户不存在，请输入正确的用户名"));
            return;
        }
        jsonObject.put("isGoogleOpen", user.getGoogleOpen());
        jsonObject.put("isSmsOpen", user.getSmsOpen());
        Response.append(jsonObject.toString());
        return;

    }


    @Page(Viewer = JSON)
    public void userState(){
        initLoginUser();
        JSONObject jsonObject = new JSONObject();
        if (loginUser != null) {
//            SessionUser su = session.getUser(this);
//            JSONObject others = su.getOthers();
            boolean googleAuth = false;
            boolean isSmsOpen = false;
//            if (others != null) {
//                isSmsOpen = others.getBooleanValue("loginNeedSmsOpen");//登录短信二次验证
//                googleAuth = others.getBooleanValue("loginNeedGoogleAuth");//登录谷歌二次验证
//            }
            if (loginUser.getSmsOpen()) {
                isSmsOpen = true;
            }
            if (loginUser.getGoogleOpen()) {
                googleAuth = true;
            }
            jsonObject.put("isGoogleOpen", googleAuth);
            jsonObject.put("isSmsOpen", isSmsOpen);
            Response.append(jsonObject.toString());
            return;
        }
        json(L("未知错误"), false, "");
    }



    @Page(Viewer = JSON)
    public void checkGoogle(){
        String userName = StringUtils.isNotBlank(param("email")) ? param("email").toLowerCase().trim() : "";
        String code = param("code");
//        JSONObject jsonObject = new JSONObject();

        if (StringUtils.isEmpty(code) || !StringUtils.isNumeric(code)) {
            this.textViewErr("gcode", L("请输入移动设备上生成的验证码"));
            return;
        }

        User user = userDao.getUserByColumn(userName, "email");
        if (null == user) {
            toast(L("用户不存在，请输入正确的用户名"));
            return;
        }
        DataResponse dr1 = this.checkVerifiCode(user.getId(),MsgToastKey.FORGET_LOGIN_PWD, ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_MOBILE, MsgToastKey.LOCK_24_HOUR);
        if (!dr1.isSuc()) {
            json( L("忘记登录密码功能已被锁定，请24小时之后再试。"), false,"");
            return;
        }
        //check谷歌
        DataResponse googleDr = this.checkVerifiCode(user.getId(),MsgToastKey.FORGET_LOGIN_PWD, ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_GOOGLE, MsgToastKey.LOCK_24_HOUR);
        if (!googleDr.isSuc()) {
            json(L("忘记登录密码功能已被锁定，请24小时之后再试。"), false, "");
            return;
        }
        /*String key = LimitType.ForgetPwdGoogleError.name()+"_"+user.get_Id();

        // 验证Google Code
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(3);
        boolean r = ga.check_code(user.getUserContact().getSecret(), Long.parseLong(code), System.currentTimeMillis());
        if (r) {
            success();
            return;
        }
        // 记录错误次数
        int surplusTimes = recordErrTimes(key, times, 60 * 60 * 2 );
        if (surplusTimes > 0) {
            if(surplusTimes > 1){
                textViewErr("gcode", L("验证码输入有误，您还有%%次机会s", String.valueOf(surplusTimes)));
            }else{
                textViewErr("gcode", L("验证码输入有误，您还有%%次机会", String.valueOf(surplusTimes)));
            }
            return;
        }
        if(null == loginUser){
            textViewErr("gcode", L("验证码输入次数超出限制，将锁定忘记密码功能，请24小时之后再试。"));
            if(null == Cache.Get(LimitType.ForgetPassword.name()+"_"+userName)){
                Cache.Set(LimitType.ForgetPassword.name()+"_"+userName,"3",aliviableMAXTimes);
            }
        }else{
            textViewErr("gcode", L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"));
            if(null == Cache.Get(userName+loginLock)){
                Cache.Set(userName+loginLock,"3",aliviableMAXTimes);
            }
        }*/

        String url = ApiConfig.getValue("usecenter.url");
        FeignContainer container = new FeignContainer(url.concat("/google"));
        GoogleApiService googleApi = container.getFeignClient(GoogleApiService.class);
        String functionType = String.valueOf(ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_GOOGLE.getKey());
//        if(null == loginUser){
//            functionType = String.valueOf(ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_GOOGLE.getKey());
//        }else{
//            functionType = String.valueOf(ConstantCenter.UpdFunctionType.LOGIN_GOOGLE.getKey());
//        }
        //最后一个参数对应用户中心的key
        Map<String,String> map = googleApi.checkGoogleCodeApiN(code,user.getUserContact().getSecret(),user.getId(),functionType);
        String key = "";
        String returnVal = "";
        for(Map.Entry<String, String> entry : map.entrySet()){
            key = entry.getKey();
            returnVal = entry.getValue().toString();
        }
        if("1".equals(key)){
            success();
            return;
        }else{
            if("-2".equals(returnVal)){
                returnVal = L("验证码输入次数超出限制，将锁定忘记登录密码功能，请24小时之后再试。");
                textViewErr("gcode", returnVal);
            }else{
                //格式化次数
                returnVal = returnVal.replace("谷歌","");
                String[] errorMsg = returnVal.split("#");
                if(errorMsg.length == 2){
                    returnVal = L(errorMsg[0], errorMsg[1]);
                }else{
                    returnVal = L(errorMsg[0]);
                }
                textViewErr("gcode", returnVal);
            }
        }

    }

    /**
     * 判断是否锁定
     * @param key
     * @param maxTimes
     * @return
     */
    private boolean isLocked(String key, int maxTimes) {
        Object current = Cache.GetObj(key);
        if (current != null &&
                Integer.parseInt(current.toString()) >= maxTimes) {
            return true;
        }
        return false;
    }

    /**
     * 记录错误次数
     * @param key 缓存键值
     * @param maxTimes 最大错误次数
     * @param expireSeconds 过期时长（单位:毫秒）
     * @return 剩余错误次数
     */
    public int recordErrTimes(String key, int maxTimes, int expireSeconds) {
        Object curCount = Cache.GetObj(key);
        if (curCount == null) {
            curCount = 0;
        }
        int errCount = Integer.parseInt(curCount.toString());
        errCount++;
        Cache.SetObj(key, errCount, expireSeconds);
        return maxTimes > errCount ? maxTimes - errCount : 0;
    }





    @Page(Viewer = JSON)
    public void uploadToken() {
        JSONObject js = new JSONObject();
        String token = QiNiuUtil.getUpToken();
        if (StringUtils.isBlank(token)) {
            json(L("图片上传失败。"), false, "");
            return;
        } else {
            js.put("host", QiNiuUtil.getHost());
            js.put("token", token);
            json(L("图片上传成功，请耐心等待审核。"), true, js.toJSONString());
            return;
        }
    }


    @Page(Viewer = JSON)
    public void checkRegister() {
        String uid = param("userId");
        if(StringUtils.isEmpty(uid)){
            json(VIP_DOMAIN + "/bw/login", false, "");
            return;
        }
        User user = userDao.getById(uid);
        if(null == user){
            json(VIP_DOMAIN + "/bw/login", false, "");
            return;
        }else{
            json("", true, "");
            return;
        }

    }

    /**
     * 忘记密码清除用户信息缓存cookie的key
     */
    @Page(Viewer = JSON)
    public void forgotLoginCookie() {
        doForgotLoginCookie();
    }






}
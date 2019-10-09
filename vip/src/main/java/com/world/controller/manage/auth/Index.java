package com.world.controller.manage.auth;

import com.Lan;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.googleauth.GoogleAuthenticator;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.GoogleApiService;
import com.messi.user.feign.UserApiService;
import com.messi.user.util.ConstantCenter;
import com.messi.user.vo.AuthenticationVo;
import com.messi.user.vo.R;
import com.redis.RedisUtil;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.constant.Const;
import com.world.controller.CheckRegex;
import com.world.controller.api.util.SystemCode;
import com.world.data.mysql.Data;
import com.world.model.LimitType;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.tdTodotask.TdTodotaskDao;
import com.world.model.dao.user.CountryDao;
import com.world.model.dao.user.MobileDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.VerifyUserInfoDao;
import com.world.model.dao.user.authen.AuthenHistoryDao;
import com.world.model.dao.user.authen.AuthenLogDao;
import com.world.model.dao.user.authen.AuthenticationDao;
import com.world.model.dao.user.authen.IdcardBlackListDao;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.level.ItemType;
import com.world.model.entity.level.StoreApplyRefuseReasonEnum;
import com.world.model.entity.level.StoreCancelRefuseReasonEnum;
import com.world.model.entity.otc.StoreAuthentication;
import com.world.model.entity.user.Country;
import com.world.model.entity.user.TradeAuthenType;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.model.entity.user.VerifyUserInfo;
import com.world.model.entity.user.authen.AuditType;
import com.world.model.entity.user.authen.AuthUtil;
import com.world.model.entity.user.authen.AuthenHistory;
import com.world.model.entity.user.authen.AuthenType;
import com.world.model.entity.user.authen.Authentication;
import com.world.model.enums.LogCategory;
import com.world.model.jifenmanage.JifenManage;
import com.world.model.singleton.SingletonThreadPool;
import com.world.rabbitmq.producer.OperateLogInfoProducer;
import com.world.util.CommonUtil;
import com.world.util.Message;
import com.world.util.MsgToastKey;
import com.world.util.QcloudCosUtil;
import com.world.util.UserUtil;
import com.world.util.date.TimeUtil;
import com.world.util.qiniu.QiNiuUtil;
import com.world.util.string.EncryptionPhoto;
import com.world.web.Page;
import com.world.web.ReqParamType;
import com.world.web.action.ApproveAction;
import com.world.web.response.DataResponse;
import com.world.web.sso.session.ClientSession;
import com.world.web.sso.session.SsoSessionManager;
import com.yc.util.MsgUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.BeanUtils;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.world.constant.Const.ReasonMap;

@SuppressWarnings("serial")
public class Index extends ApproveAction {

    AuthenticationDao auDao = new AuthenticationDao();
    UserDao userDao = new UserDao(lan);
    VerifyUserInfoDao vudao = new VerifyUserInfoDao();
    AuthenLogDao logDao = new AuthenLogDao();
    TdTodotaskDao tdTodotaskDao = new TdTodotaskDao();
    /**
     * 昵称限制字符
     */
    public final static String LIMIT_KEYWORD = "BTC,LTC,ETC,ETH,DASH,OMG,USDT,QTUM,EOS,ELF,SNT,IOST,ZRX,LINK,KNC,Bitglobal,Bitgloba,GBC,OTCPTA,QQ,微信,企鹅,微博,ICO,政府,习近平,官方,bit全球";
//    @Page(Viewer = "/cn/manage/auth/index.jsp", des = "安全设置")
    public void index() {
        initLoginUser();

        JSONObject diffAreaLogin = new JSONObject();
        diffAreaLogin.put("showStatus", loginUser.isDiffAreaLoginNoCheck() ? "已关闭" : "已开启");
        diffAreaLogin.put("button", loginUser.isDiffAreaLoginNoCheck() ? "开启" : "关闭");
        diffAreaLogin.put("oper", loginUser.isDiffAreaLoginNoCheck() ? 1 : 0);

        JSONObject isTransSafe = new JSONObject();
        boolean needSafe = userDao.isNeedSafePwd(loginUser);
        isTransSafe.put("showStatus", needSafe ? "已开启" : "已关闭");
        isTransSafe.put("button", needSafe ? "关闭" : "开启");
        isTransSafe.put("oper", needSafe ? 1 : 0);

        JSONObject loginGoogleAuth = new JSONObject();
        loginGoogleAuth.put("showStatus", loginUser.isLoginGoogleAuth() ? "已开启" : "已关闭");
        loginGoogleAuth.put("button", loginUser.isLoginGoogleAuth() ? "关闭" : "开启");
        loginGoogleAuth.put("oper", loginUser.isLoginGoogleAuth() ? 0 : 1);

        JSONObject payGoogleAuth = new JSONObject();
        payGoogleAuth.put("showStatus", loginUser.isPayGoogleAuth() ? "已开启" : "已关闭");
        payGoogleAuth.put("button", loginUser.isPayGoogleAuth() ? "关闭" : "开启");
        payGoogleAuth.put("oper", loginUser.isPayGoogleAuth() ? 0 : 1);

        JSONObject payMobileAuth = new JSONObject();
        payMobileAuth.put("showStatus", loginUser.isPayMobileAuth() ? "已开启" : "已关闭");
        payMobileAuth.put("button", loginUser.isPayMobileAuth() ? "关闭" : "开启");
        payMobileAuth.put("oper", loginUser.isPayMobileAuth() ? 0 : 1);

        JSONObject payEmailAuth = new JSONObject();
        payEmailAuth.put("showStatus", loginUser.isPayEmailAuth() ? "已开启" : "已关闭");
        payEmailAuth.put("button", loginUser.isPayEmailAuth() ? "关闭" : "开启");
        payEmailAuth.put("oper", loginUser.isPayEmailAuth() ? 0 : 1);

        setAttr("diffAreaLogin", diffAreaLogin);
        setAttr("isTransSafe", isTransSafe);
        setAttr("loginGoogleAuth", loginGoogleAuth);
        setAttr("payGoogleAuth", payGoogleAuth);
        setAttr("payMobileAuth", payMobileAuth);
        setAttr("payEmailAuth", payEmailAuth);

        UserContact uc = loginUser.getUserContact();
        int emailStatu = uc.getEmailStatu();
        int mobileStatu = uc.getMobileStatu();

        setAttr("emailStatu", emailStatu);
        setAttr("mobileStatu", mobileStatu);
        setAttr("googleAuth", uc.getGoogleAu());
        setAttr("loginAuth", uc.getLoginGoogleAuth());


        initLoginUser();

        setAttr("tradeAuthenTypeMap", TradeAuthenType.MAP);

        uc = loginUser.getUserContact();
        setAttr("hasGoogleAtuh", uc.getGoogleAu() == 2);
        setAttr("hasMobileAtuh", uc.getMobileStatu() == 2);
        setAttr("hasEmailAuth", uc.getEmailStatu() == 2);

        userDao.syncLoginAuthen(loginUser);
        userDao.syncTradeAuthen(loginUser);
        userDao.syncWitchdrawAuthen(loginUser);
    }

    /**
     * 配合前端改造
     */
    @Page(Viewer =JSON, des = "安全设置")
    public void authInit() {
        initLoginUserJson();

        userDao.syncLoginAuthen(loginUser);
        userDao.syncTradeAuthen(loginUser);
        userDao.syncWitchdrawAuthen(loginUser);

        UserContact uc = loginUser.getUserContact();

        Map<String, Object> result = new HashMap<>(10);
        result.put("hasGoogleAtuh", uc.getGoogleAu() == 2);
        result.put("hasMobileAtuh", uc.getMobileStatu() == 2);
        result.put("hasEmailAuth", uc.getEmailStatu() == 2);
        result.put("loginAuthenType", loginUser.getLoginAuthenType());
        result.put("tradeAuthenType", loginUser.getTradeAuthenType());
        result.put("withdrawAuthenType", loginUser.getWithdrawAuthenType());
        result.put("loginAuthenTypeName", L(loginUser.getLoginAuthenTypeName()));
        result.put("tradeAuthenTypeName", L(loginUser.getTradeAuthenTypeName()));
        result.put("withdrawAuthenTypeName", L(loginUser.getWithdrawAuthenTypeName()));
        result.put("withdrawAddressAuthenType", loginUser.getWithdrawAddressAuthenType());



        json("ok", true, JSONObject.toJSONString(result), true);
    }

    /**
     * 最大认证次数
     */
    public static final int MAX_AUTH_AMOUNT = CommonUtil.stringToInt(GlobalConfig.getValue("max_auth_amount"), 15);

//    @Page(Viewer = "/cn/manage/index.jsp", des = "邮箱认证")
    public void email() {
        initLoginUser();

        String edit = param("edit");
        String step = param("step");
        String emailCode = param("emailCode");
        setAttr("curUser", loginUser);
        UserContact uc = loginUser.getUserContact();

        if (step.equals("third")) {// 认证
            if (emailCode != null && emailCode.length() > 0) {
                if (uc.getEmailCode() == null || !uc.getEmailCode().equals(emailCode)) {
                    tip(L("出错了！"), "/manage/auth/email?edit=true&step=next", false);
                    return;
                } else {
                    if (now().getTime() - uc.getEmailTime().getTime() > 24 * 60 * 60 * 1000) {// 过期了
                        tip(L("出错了！"), "/manage/auth/email?edit=true&step=next", false);
                        return;
                    }
                }
            }

        }

        String email = uc.getSafeEmail();
        int emailStatu = email != null && email.length() > 0 ? 2 : 0;

        String editS = Cache.Get("editE_" + userId());
        if (edit.equals("true")) {
            emailStatu = -1;
            if (editS == null || editS.indexOf("1_") != 0) {
                step = "one";
            } else if (!editS.equals("2")) {
                step = "next";
            }

            setAttr("step", step);
        }

        setAttr("emailStatu", emailStatu);

        if (emailStatu == -1 && uc.getCheckEmail() != null && uc.getCheckEmail().length() > 0 && editS != null){
            email = uc.getCheckEmail();
        }
        if (email == null) {
            email = uc.getSafeEmail();
        }

        setAttr("email", UserUtil.shortEmail(email));
        setAttr("source", email);

        setAttr("tab", "email");
        if (StringUtil.isBlank(loginUser.getSafePwd())) {
            setAttr("safeAuth", true);
        }
        setAttr("mobileStatu", uc.getMobileStatu());

    }

    /**
     * 配合前端改造
     */
    @Page(Viewer = JSON, des = "邮箱认证")
    public void emailJson() {
        initLoginUserJson();

        Map<String, Object> result = new HashMap<>();

        String edit = param("edit");
        String step = param("step");
        String emailCode = param("emailCode");
//        setAttr("curUser", loginUser);
        UserContact uc = loginUser.getUserContact();

        if (step.equals("third")) {// 认证
            if (emailCode != null && emailCode.length() > 0) {
                if (uc.getEmailCode() == null || !uc.getEmailCode().equals(emailCode)) {
                    tipJson(L("出错了！"), "/manage/auth/email?edit=true&step=next", false);
                    return;
                } else {
                    if (now().getTime() - uc.getEmailTime().getTime() > 24 * 60 * 60 * 1000) {// 过期了
                        tipJson(L("出错了！"), "/manage/auth/email?edit=true&step=next", false);
                        return;
                    }
                }
            }
        }

        String email = uc.getSafeEmail();
        int emailStatu = email != null && email.length() > 0 ? 2 : 0;

        String editS = Cache.Get("editE_" + userId());
        if (edit.equals("true")) {
            emailStatu = -1;
            if (editS == null || editS.indexOf("1_") != 0) {
                step = "one";
            } else if (!editS.equals("2")) {
                step = "next";
            }

            result.put("step", step);
        }

        result.put("emailStatu", emailStatu);

        if (emailStatu == -1 && uc.getCheckEmail() != null && uc.getCheckEmail().length() > 0 && editS != null){
            email = uc.getCheckEmail();
        }
        if (email == null) {
            email = uc.getSafeEmail();
        }

        result.put("email", UserUtil.shortEmail(email));
        result.put("source", email);

        result.put("tab", "email");
        if(StringUtil.isBlank(loginUser.getSafePwd())){
            result.put("safeAuth", true);
        }
        result.put("mobileStatu", uc.getMobileStatu());

        json("ok", true, JSONObject.toJSONString(result), true);
    }

//    @Page(Viewer = "/cn/manage/index.jsp", des = "手机认证")
    public void mobile() {
        // if(!emailNoSetTips()){
        // return;
        // }
        initLoginUser();
        String edit = param("edit");
        String step = param("step");
        setAttr("curUser", loginUser);
        UserContact uc = loginUser.getUserContact();
        String mobile = uc.getSafeMobile();

        setAttr("nowStat", mobile != null && mobile.length() > 0 ? 2 : 0);
        if (edit.length() == 0) {
            setAttr("mobileStatu", mobile != null && mobile.length() > 0 ? 2 : 0);
        } else {
            setAttr("mobileStatu", -1);// 修改
            if (!step.equals("one")) {
                String editS = Cache.Get("editM_" + userId());
                if (editS == null || !editS.equals("1")) {
                    step = "one";
                }
            }
            setAttr("step", step);
        }
        setAttr("mobile", userDao.shortMobile(mobile));
        setAttr("source", mobile);

        CountryDao cDao = new CountryDao();
        Query q = cDao.getQuery().order("code");
        List<Country> country = q.asList();
        setAttr("country", country);

        // request.getLocalAddr();

        setAttr("showAudioButton", uc.isShowAudioButton());
        if ("one".equals(step)) setAttr("codeType", PostCodeType.editMobile.getKey());
        else setAttr("codeType", PostCodeType.mobileAuth.getKey());

        // 先较验
        VerifyUserInfo bean = vudao.getVerifyingInfo(loginUser.getId(), 1);
        if (bean != null) {
            setAttr("verifyUserInfo", bean);
        }

        setAttr("googleAuth", uc.getGoogleAu());
    }

    /**
     * 配合前端改造
     */
    @Page(Viewer = JSON)
    public void mobileJson() {

        Map<String, Object> result = new HashMap<>();

        initLoginUserJson();
        String edit = param("edit");
        String step = param("step");
//        setAttr("curUser", loginUser);
        UserContact uc = loginUser.getUserContact();
        String mobile = uc.getSafeMobile();

        result.put("nowStat", mobile != null && mobile.length() > 0 ? 2 : 0);
        if (edit.length() == 0) {
            result.put("mobileStatu", mobile != null && mobile.length() > 0 ? 2 : 0);
        } else {
            result.put("mobileStatu", -1);// 修改
            if (!step.equals("one")) {
                String editS = Cache.Get("editM_" + userId());
                if (editS == null || !editS.equals("1")) {
                    step = "one";
                }
            }
            result.put("step", step);
        }
        result.put("phonenum", userDao.shortMobile(mobile));
        result.put("source", mobile);

        CountryDao cDao = new CountryDao();
        Query q = cDao.getQuery().order("code");
        List<Country> country = q.asList();
        result.put("country", country);

        // request.getLocalAddr();

        result.put("showAudioButton", uc.isShowAudioButton());
        if ("one".equals(step)) setAttr("codeType", PostCodeType.editMobile.getKey());
        else result.put("codeType", PostCodeType.mobileAuth.getKey());

        // 先较验
        VerifyUserInfo bean = vudao.getVerifyingInfo(loginUser.getId(), 1);
        if (bean != null) {
            result.put("verifyUserInfo", bean);
        }

        result.put("googleAuth", uc.getGoogleAu());

        json("ok", true, JSONObject.toJSONString(result), true);
    }

    @Page(Viewer = JSON)
    public void authMobileSendCode() {
        String mobileCode;
        try {
            initLoginUser();
            if (null == loginUser) {
                json(L("您没有登录，请登录后重新操作"), false, "");
                return;
            }
            String countryCode = param("mCode");
            if (null == countryCode || "".equals(countryCode) || !countryCode.startsWith("+")) {
                countryCode = "+86";
            }
            String phonenumber = param("mobile").trim();
            boolean graphicalCode = true;//是否有图形验证码
//			String cacheUserId = Cache.Get(modifyMobileCache + loginUser.getId());
//			if(cacheUserId != null){
//				graphicalCode = true;
//				String code = param("code").trim();//图形验证码
//				if(!CheckCode(code)){
//					json(L("图形验证码输入错误！"), false, "{\"id\" : \"code\"}");
//					return;
//				}
//			}

            String code = param("code").trim();//图形验证码
            if (!CheckCode(code)) {
                json(L("图形验证码错误，请重新输入。"), false, "{\"id\" : \"code\"}");
                return;
            }

//			PostCodeType postCodeType = PostCodeType.mobileAuth;
//			if(graphicalCode){
//				postCodeType = PostCodeType.editMobile;
//			}
//			String codeType = postCodeType.getValue();
            String codeType = param("codeType");
            PostCodeType postCodeType = PostCodeType.mobileAuth;
            if (codeType.equals("2")) {
                postCodeType = PostCodeType.mobileAuth;
            } else if (codeType.equals("3")) {
                postCodeType = PostCodeType.editMobile;
            }

            if (isForbid()) {
                return;
            }

            if (session == null) {
                SsoSessionManager.initSession(this);
            }

            if (sessionId == null) {
                json(L("系统出错了，请稍后重试"), false, "");
                return;
            }

            String mobileNumber = countryCode + " " + phonenumber;

            if (!CheckRegex.isPhoneNumber(mobileNumber)) {
                json(L("请输入合法的手机号！"), false, "");
                return;
            }

            String ip = ip();

            ClientSession clientSession = new ClientSession(ip, mobileNumber, lan, codeType, graphicalCode);
            clientSession.rs = resoureRequest;

            DataResponse dr = clientSession.checkSend();//检测当前客户端是否能够发送

            if (!dr.isSuc()) {
                json(dr.getDes(), false, dr.getDataStr());
                return;
            }//测试，暂时不验证这步

            boolean res = userDao.mobileLoginCheckMobile(mobileNumber);
            //当前ip验证是否注册过的所有手机号码24h不得超过x个authMobile
            clientSession.addCheckNumber();
            if (!res) {
                json(L("手机号已经被使用，请使用未注册手机！"), false, "");
                return;
            }

            MobileDao mDao = new MobileDao();
            mobileCode = MobileDao.GetRadomStr(1);
            /*start by xzhang 20171031 短信服务临时解决方法，除+86外全发英文*/
            String title = L(codeType);
            String content = String.format(L(postCodeType.getDes()), mobileCode);
            //去掉该逻辑，所有都按照用户选择语言发送 modify by buxianguan 20190805
//            if (!MsgUtil.isContain(mobileNumber)) {
//                title = Lan.Language("en", codeType);
//                content = String.format(Lan.Language("en", postCodeType.getDes()), mobileCode);
//            }
            /*end*/
            if (mDao.sendSms(loginUser, ip(), title, content, mobileNumber)) {
                if (clientSession.sendCode(mobileCode)) {
                    log.info(mobileCode + ">>>" + String.format(L(postCodeType.getDes()), mobileCode));
                    json(L("短信验证码已发送到您的手机，10分钟内有效"), true, "");
                    return;
                }
            }
        } catch (Exception e) {
            log.error("内部异常", e);
        }
        json(L("系统出错，请稍后..."), false, "");
    }

    @Page(Viewer = JSON, ipCheck = true)
    public void authMobile() {
        initLoginUser();
        UserContact uc = loginUser.getUserContact();
        String userId = userIdStr();

        String mCode = param("mCode");
        String mobile = param("mobile");
        String mobileCode = param("mobileCode");
        String userIp = ip();
        String codeType = param("codeType");

        String mobileNumber = mCode + " " + mobile;
        if (!CheckRegex.isPhoneNumber(mobileNumber)) {
            json(L("请输入合法的手机号！"), false, "");
            return;
        }

//        if (uc.getMobileStatu() == 2 && uc.getSafeMobile() != null) {
//            json(L("您的账号已经认证了手机%%，请勿重复操作。", userDao.shortMobile(uc.getSafeMobile())), false, "");
//            return;
//        }
        boolean res = userDao.mobileLoginCheckMobile(mobile);
        if (!res) {
            json(L("手机号码已存在"), false, "");
            return;
        }

        if (uc.getGoogleAu() == 2) {
            long gCode = CommonUtil.stringToLong(param("googleCode"), -1);
            userDao.setLan(lan);
            Message msg = userDao.isCorrect(loginUser, uc.getSecret(), gCode);
            if (!msg.isSuc()) {
                json(msg.getMsg(), false, "");
                return;
            }
        }

        // 检查短信验证码
        ClientSession clientSession = new ClientSession(userIp, mobileNumber, lan, codeType, false);
        DataResponse dr = clientSession.checkCode(mobileCode);
        if (!dr.isSuc()) {
            json(dr.getDes(), false, "");
            return;
        }

        // 检查邮件验证码
        String emailCode = param("emailCode");
        ClientSession clientSession2 = new ClientSession(userIp, uc.getSafeEmail(), lan, PostCodeType.safeAuth.getValue(), false);
        DataResponse dr2 = clientSession2.checkCode(emailCode);
        if (!dr2.isSuc()) {
            json(dr2.getDes().replace(L("短信"), L("邮件")), false, "");
            return;
        }

        Datastore ds = userDao.getDatastore();
        Query<User> q = ds.find(User.class, "_id", userId);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set("userContact.mCode", mCode);
        ops.set("userContact.safeMobile", mobileNumber);
        ops.set("userContact.loginCheckMobile", mobile);//手机号（不带国家码）
        ops.set("userContact.mobileCode", "");
        ops.set("userContact.mobileStatu", AuditStatus.pass.getKey());
        ops.set("userContact.checkMobile", "");
        ops.set("userContact.codeTime", new Timestamp(0));

        logDao.insertOneRecord(AuthenType.mobile.getKey(), loginUser.get_Id(), "0", "用手机：" + mobile + "认证成功。", ip());

        UpdateResults<User> ur = userDao.update(q, ops);


        if (!ur.getHadError()) {
            json(L("手机认证成功"), true, "");
            /*start by xwz 20170625 绑定邮箱加积分*/
            JifenManage jifenManager = new JifenManage(userId, 4, null, null, "VIP");//1:注册
            SingletonThreadPool.addJiFenThread(jifenManager);
            /*end*/
        } else {
            json(L("手机认证失败"), false, "");
        }
    }

//    @Page(Viewer = "/cn/manage/index.jsp", des = "修改手机")
    public void mobileModify() {
        initLoginUser();
        String method = param("method");// 1：使用旧手机修改 2：使用视频审核修改
        setAttr("curUser", loginUser);
        UserContact uc = loginUser.getUserContact();
        String mobile = uc.getSafeMobile();

        setAttr("mobile", userDao.shortMobile(mobile));
        CountryDao cDao = new CountryDao();
        Query q = cDao.getQuery().order("code");
        List<Country> country = q.asList();
        setAttr("country", country);
        setAttr("method", method);

    }

    private String modifyMobileCache = "modify_mobile_";

    @Page(Viewer = JSON)
    public void doMobileModifyStepOne() {
        try {
            initLoginUser();
            UserContact uc = loginUser.getUserContact();
            String mobileNumber = uc.getSafeMobile();
            String userIp = ip();
            String mobileCode = param("mobileCode");
            // 检查短信验证码
            ClientSession clientSession = new ClientSession(userIp, mobileNumber, lan, PostCodeType.editMobile.getValue(), false);
            DataResponse dr = clientSession.checkCode(mobileCode);
            if (!dr.isSuc()) {
                json(L(dr.getDes()), false, "");
                return;
            }
            String userId = userIdStr();
            Cache.Set(modifyMobileCache + userId, userId, 10 * 60);
            json("", true, "");
        } catch (Exception e) {
            log.error("内部异常", e);
            json(L("出错了，请稍后重试"), false, "");
        }
    }

    /**
     * 设置手机号
     */
    @Page(Viewer = JSON)
    public void setMobile() {
        try {
            initLoginUser();
            UserContact uc = loginUser.getUserContact();
            String userId = userIdStr();
            //提示信息
            String msg = "";
            //check录入手机
            DataResponse checkMobileDr = checkSetMobile(userId);
            if (!checkMobileDr.isSuc()) {
                json(L(checkMobileDr.getDes()), false, "");
                return;
            }


            User user = userDao.getById(userId);
            //国家码
            String mCode = param("selectedCode");
            String mobile = param("mobile");
            //手机验证码
            String mobileCode = param("smscode");
            //是否开启手机安全验证，true:1 开启，false:0 关闭
            String isSmsOpen = param("mck");
            String mobileNumber = mCode + " " + mobile;
            //手机验证
            PostCodeType pct = PostCodeType.setMobile;
            LimitType lt = LimitType.SetUserInfoMobileError;
            DataResponse dr1 = getDataResponse(user,pct,mobileNumber,2,mobileCode,lt,MsgToastKey.SETTING_MOBILE,MsgToastKey.LOCK_24_HOUR);
            if (!dr1.isSuc()) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode,L(dr1.getDes()));
            }else{
                //TODO 放下一步校验session
            }
            if(StringUtils.isEmpty(mobile)){
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.mobile,L("请输入手机号。"));
            }
            if (!CheckRegex.isPhoneNumber(mobileNumber)) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.mobile,L("请输入正确的手机号码。"));
            }
            boolean res = userDao.mobileLoginCheckMobile(mobileNumber);
            if (!res) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.mobile,L("请输入正确的手机号码。"));
            }

            if(StringUtils.isNotEmpty(msg)){
                json("", false, msg);
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
                /*Start by guankaili 20181204 开启手机验证增加积分 */
                if("1".equals(isSmsOpen)){
                    JifenManage jifenManager = new JifenManage(userId, 4, null, null, "VIP");
                    SingletonThreadPool.addJiFenThread(jifenManager);
                }
                /*end*/
                json(L("录入手机成功"), true, "");

            } else {
                json(L("失败"), false, "");
            }
        }catch (Exception e){
            log.error("10100303VIPSZSJ【设置手机号】 com.world.controller.manage.auth.index#setMobile",e);
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
     * 修改手机号
     */
    @Page(Viewer = JSON)
    public void updMobile() {
        try{
            initLoginUser();
            String userId = userIdStr();
            User user = userDao.getById(userId);
            String oldMobile = user.getUserContact().getSafeMobile();

            //国家码
            String mCode = param("selectedCode");
            String mobile = param("mobile");
            //手机验证码
            String mobileCode = param("smscode");
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
            String mobileNumber = mCode + " " + mobile;
            //提示信息
            String msg = "";
            //手机验证
            PostCodeType pct = PostCodeType.updMobile;
            LimitType lt = LimitType.UpdMobileError;
            DataResponse dr1 = getDataResponse(user,pct,mobileNumber,2,mobileCode,lt,MsgToastKey.UPD_MOBILE,MsgToastKey.LOCK_24_HOUR);
            if (!dr1.isSuc()) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode,L(dr1.getDes()));
            }else{
                //TODO 放下一步校验session
            }
            if (!CheckRegex.isPhoneNumber(mobileNumber)) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.mobile,L("请输入正确的手机号码。"));
            }
            boolean res = userDao.mobileLoginCheckMobile(mobileNumber);
            if (!res) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.mobile,L("请输入正确的手机号码。"));
            }

            if(StringUtils.isNotEmpty(msg)){
                json("", false, msg);
                return;
            }
            //防跳步
            String token = param("token");
            boolean flg = EncryptionPhoto.checkToken(Const.function_upd_mobile,userId,token);
            if(!flg){
                json(L("非法操作"), false, "");
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
                json(L("修改手机成功"), true, "");
            } else {
                json(L("失败"), false, "");
            }
        }catch (Exception e){
            log.error("10100303VIPXGSJ【修改手机号】 com.world.controller.manage.auth.index#updMobile",e);
        }
    }

    /**
     * 修改手机号验证
     */
    @Page(Viewer = JSON)
    public void updMobileCheck() {
        try {
            String userId = userIdStr();
            User user = userDao.getById(userId);
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
            String mobile = param("mobile");
            String mobileCode = param("smscode");

            //手机验证
            PostCodeType pct = PostCodeType.updMobileCheck;
            LimitType lt = LimitType.UpdMobileCheckError;
            DataResponse dr1 = getDataResponse(user,pct,mobile,2,mobileCode,lt,MsgToastKey.UPD_MOBILE,MsgToastKey.LOCK_24_HOUR);
            if (!dr1.isSuc()) {
                setAttr("pass", false);
                String msg = CommonUtil.mapToJsonStr(MsgToastKey.smsCode,dr1.getDes());
                json("", false, msg);
                return;
            }else{
                setAttr("pass", true);
                String token = EncryptionPhoto.getToken(Const.function_upd_mobile,userId);
                json(token, true, "");
                //TODO 放下一步校验session
            }
        }catch (Exception e){
            log.error("10100303VIPXGSJJY【修改手机号校验】 com.world.controller.manage.auth.index#updMobileCheck",e);
        }
    }

    /**
     * 二次验证--设置并开启手机验证
     */
    @Page(Viewer = JSON)
    public void openMobileVerify() {
        try{
            String userId = userIdStr();
            User user = userDao.getById(userId);
            //设置手机
            DataResponse setMoblieDr = this.checkVerifiCode(userId,MsgToastKey.OPEN_MOBILE_VERIFY, ConstantCenter.UpdFunctionType.SET_USER_INFO_MOBILE, MsgToastKey.LOCK_24_HOUR);
            if (!setMoblieDr.isSuc()) {
                json(L(setMoblieDr.getDes()), false, "");
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
                json(L(mobileDr.getDes()), false, "");
                return;
            }

            //国家码
            String mCode = param("selectedCode");
            String mobile = param("mobile");
            String mobileCode = param("smscode");
            String mobileNumber = "";
            //提示信息
            String msg = "";
            if(StringUtils.isEmpty(mCode)){
                mobileNumber = mobile;
            }else{
                mobileNumber = mCode + " " + mobile;
            }

            if (!CheckRegex.isPhoneNumber(mobileNumber)) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.mobile,L("请输入正确的手机号码。"));
            }
            if(StringUtils.isEmpty(user.getUserContact().getSafeMobile())){
                boolean res = userDao.mobileLoginCheckMobile(mobileNumber);
                if (!res) {
                    msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.mobile,L("请输入正确的手机号码。"));
                }
            }
            //手机验证
            PostCodeType pct = PostCodeType.openMobileVerify;
            LimitType lt = LimitType.OpenMobileVerifyError;
            DataResponse dr1 = getDataResponse(user,pct,mobileNumber,2,mobileCode,lt,MsgToastKey.OPEN_MOBILE_VERIFY,MsgToastKey.LOCK_24_HOUR);
            if (!dr1.isSuc()) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode,L(dr1.getDes()));
            }else{
    //            String msg = CommonUtil.mapToJsonStr(MsgToastKey.mobile,L("开启手机验证成功"));
    //            json(L("开启手机验证成功"), true, msg);
                //TODO 放下一步校验session
            }

            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            if(StringUtils.isNotEmpty(msg)){
                ops.set("hasMobileCheckBox", false);
                userDao.update(q, ops);
                json("", false, msg);
                return;
            }
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

            logDao.insertOneRecord(AuthenType.mobile.getKey(), userId, "0", "修改手机：" + mobile + "成功。", ip());

            UpdateResults<User> ur = userDao.update(q, ops);
            if (!ur.getHadError()) {
                /*start by xwz 20170625 开启谷歌加积分*/
                JifenManage jifenManager = new JifenManage(userId, 4, null, null, "VIP");
                SingletonThreadPool.addJiFenThread(jifenManager);
                json(L("设置成功"), true, "");
            } else {
                json(L("设置失败"), false, "");
            }
        }catch (Exception e){
            log.error("10100303VIPSZSJYZ【设置手机号验证】 com.world.controller.manage.auth.index#openMobileVerify",e);
        }
    }

    /**
     * 二次验证--手机验证关闭
     */
    @Page(Viewer = JSON)
    public void closeMobileVerify() {
        try {
            String userId = userIdStr();
            User user = userDao.getById(userId);
            String mobile = param("mobile");
            String mobileCode = param("smscode");
            String emailCode = param("emailcode");
            //提示信息
            String msg = "";
            //设置手机
            DataResponse setMoblieDr = this.checkVerifiCode(userId,MsgToastKey.SETTING_MOBILE, ConstantCenter.UpdFunctionType.SET_USER_INFO_MOBILE, MsgToastKey.LOCK_24_HOUR);
            if (!setMoblieDr.isSuc()) {
                json(L(setMoblieDr.getDes()), false, "");
                return;
            }
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

            if (!CheckRegex.isPhoneNumber(mobile)) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.mobile,L("请输入合法的手机号。"));
            }

            //邮箱验证
            PostCodeType pct = PostCodeType.closeEmailVerify;
            LimitType lt = LimitType.CloseMobileVerifyEmailError;
            DataResponse dr = getDataResponse(user,pct,user.getEmail(),1,emailCode,lt,MsgToastKey.CLOSE_MOBILE_VERIFY,MsgToastKey.LOCK_24_HOUR);
            if (!dr.isSuc()) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.emailCode,L(dr.getDes()));
            }
            //手机验证
            PostCodeType pct1 = PostCodeType.closeMobileVerify;
            LimitType lt1 = LimitType.CloseMobileVerifyMobileError;
            DataResponse dr1 = getDataResponse(user,pct1,mobile,2,mobileCode,lt1,MsgToastKey.CLOSE_MOBILE_VERIFY,MsgToastKey.LOCK_24_HOUR);
            if (!dr1.isSuc()) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode,L(dr1.getDes()));
            }
            if(StringUtils.isNotEmpty(msg)){
                json("", false, msg);
                return;
            }
            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("isSmsOpen", false);
            ops.set("userContact.mobileStatu", AuditStatus.pass.getKey());
            UpdateResults<User> ur = userDao.update(q, ops);
            if (!ur.getHadError()) {
                json(L("手机验证关闭成功"), true, "");
            } else {
                json(L("失败"), false, "");
            }
        }catch (Exception e){
            log.error("10100303VIPGBSJYZJY【关闭手机号验证校验】 com.world.controller.manage.auth.index#closeMobileVerify",e);
        }
    }

    /**
     * check手机或邮箱验证码
     * @param user
     * @param pct
     * @param sendType 1-邮箱验证，2-手机验证
     * @param code
     * @return
     */
//    private DataResponse getDataResponse(User user,PostCodeType pct,String sendNum,int sendType,String code,LimitType lt,String functionName,String lockTime) {
//        DataResponse dr = null;
//        String userIp = ip();
//        PostCodeType postCodeType = (PostCodeType) EnumUtils.getEnumByKey(pct.getKey(), PostCodeType.class);
//        String codeType = postCodeType.getValue();
//        if(sendType == 1){
//            ClientSession clientSession = new ClientSession(userIp, sendNum, lan, codeType, false);
//            dr = clientSession.checkCodeMailNew(user.getId(),code,lt,functionName,lockTime);
//        }else{
//            ClientSession clientSession = new ClientSession(userIp, sendNum, lan, codeType, false);
//            dr = clientSession.checkCodeNew(user.getId(),code,lt,functionName,lockTime);
//        }
//        return dr;
//    }

    /**
     * 二次验证--google验证开启 获取谷歌验证信息
     */
    @Page(Viewer = JSON)
    public void getGoogleInfo() {
        try {
            String userId = userIdStr();
            User user = userDao.getById(userId);

            //check谷歌
            DataResponse mobileDr = this.checkVerifiCode(userId,MsgToastKey.OPEN_GOOGLE_VERIFY, ConstantCenter.UpdFunctionType.OPEN_GOOGLE_VERIFY, MsgToastKey.LOCK_24_HOUR);
            if (!mobileDr.isSuc()) {
                json(L(mobileDr.getDes()), false, "");
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
                json("ok", true, JSONObject.toJSONString(map), true);
                return;
            }else{
                String msg = CommonUtil.mapToJsonStr(MsgToastKey.googleCode,L("获取谷歌信息失败"));
                json("获取谷歌信息失败", false, msg);
                return;
            }
        }catch (Exception e){
            log.error("10100303VIPGGINFO【获取谷歌验证信息】 com.world.controller.manage.auth.index#getGoogleInfo",e);
        }
    }



    /**
     * 二次验证--设置并开启谷歌验证
     */
    @Page(Viewer = JSON)
    public void openGoogleVerify(){
        try {
            String gcodeParam = "paycode";
            String googleCode = param(gcodeParam);
            String secret = param("payKey");
            String userId = userIdStr();
            //check谷歌
            DataResponse mobileDr = this.checkVerifiCode(userId,MsgToastKey.OPEN_GOOGLE_VERIFY, ConstantCenter.UpdFunctionType.OPEN_GOOGLE_VERIFY, MsgToastKey.LOCK_24_HOUR);
            if (!mobileDr.isSuc()) {
                json(L(mobileDr.getDes()), false, "");
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
                    logDao.insertOneRecord(AuthenType.google.getKey(),userIdStr(), "0", "发起谷歌验证修改请求。", ip());
                    json(L("谷歌验证开启成功"), true, "");
                    return;
                }else{
                    json(L("失败"), false, "");
                    return;
                }
            }else{
                if("-2".equals(returnVal)){
                    returnVal = L("验证码输入次数超出限制，将锁定开启谷歌验证功能，请24小时之后再试。");
                }else{
                    //格式化次数
                    returnVal = returnVal.replace("谷歌","");
                    String[] errorMsg = returnVal.split("#");
                    if(errorMsg.length == 2){
                        returnVal = L(errorMsg[0], errorMsg[1]);
                    }else{
                        returnVal = L(errorMsg[0]);
                    }
                }
                msg = CommonUtil.mapToJsonStr(msg, gcodeParam,returnVal);
                json("", false, msg);
                return;
            }
        }catch (Exception e){
            log.error("10100303VIPSZGG【设置谷歌验证】 om.world.controller.manage.auth.index#openGoogleVerify",e);
        }
    }

    /**
     * 二次验证--关闭谷歌验证
     */
    @Page(Viewer = JSON)
    public void closeGoogleVerify(){
        try {
            String userId = userIdStr();
            User user = userDao.getById(userId);

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
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.emailCode,L(dr.getDes()));
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
                    if(errorMsg.length == 2){
                        returnVal = L(errorMsg[0], errorMsg[1]);
                    }else{
                        returnVal = L(errorMsg[0]);
                    }
                }
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.googleCode,returnVal);
            }
            if(StringUtils.isNotEmpty(msg)){
                json("", false, msg);
                return;
            }
            FeignContainer container1 = new FeignContainer(url.concat("/user"));
            UserApiService userApi = container1.getFeignClient(UserApiService.class);
            int state = userApi.updTwoVerifi(userId,"","",null,false);
            if(state != 0){
                json(L("谷歌验证关闭成功"), true, "");
                return;
            }else{
                json(L("失败"), false, "");
                return;
            }
        }catch (Exception e){
            log.error("10100303VIPGBGG【关闭谷歌验证】 om.world.controller.manage.auth.index#closeGoogleVerify",e);
        }
    }




    @Page(Viewer = JSON)
    public void doMobileModify() {
        try {
            initLoginUser();
            String userId = userIdStr();
            String cacheUserId = Cache.Get(modifyMobileCache + userId);
            int verifyOldInfo = 0;
            String method = param("method");
            if ("1".equals(method) && (null == cacheUserId || !userId.equals(cacheUserId))) {
                json(L("出错了，请稍后重试"), false, "{\"needStepOne\":1}");
                return;
            }

            if (null != cacheUserId) {
                verifyOldInfo = 1;
            }
            UserContact uc = loginUser.getUserContact();
            String userIp = ip();
            String safePwd = param("safePwd");
            if (!safePwd(safePwd, userIdStr(), JSON, false)) {
                return;
            }

            String countryCode = param("countryCode");
            if (null == countryCode || "".equals(countryCode) || !countryCode.startsWith("+")) {
                countryCode = "+86";
            }
            String newMobileNumber = param("newMobileNumber");
            String newPhoneNumber = countryCode + " " + newMobileNumber;
            String newMobileCode = param("newMobileCode");

            if (!CheckRegex.isPhoneNumber(newPhoneNumber)) {
                json(L("请输入有效的手机号码!"), false, "");
                return;
            }

            if (loginUser.getUserContact().getGoogleAu() == 2) {
                long googleCode = longParam("googleCode");
                String savedSecret = loginUser.getUserContact().getSecret();
                if (0 == googleCode) {
                    json(L("请输入Google验证码"), false, "");
                    return;
                }

                if (!isCorrect(savedSecret, googleCode, JSON)) {
                    return;
                }
            }

            String codeType = param("codeType");
            // 检查短信验证码
            ClientSession clientSession = new ClientSession(userIp, newPhoneNumber, lan, codeType, false);
            boolean res = userDao.mobileLoginCheckMobile(newPhoneNumber);
            //当前ip验证是否注册过的所有手机号码24h不得超过x个
            clientSession.addCheckNumber();
            if (!res) {
                json(L("手机号已经被使用，请使用未注册手机！"), false, "");
                return;
            }
            DataResponse dr2 = clientSession.checkCode(newMobileCode);
            if (!dr2.isSuc()) {
                json(dr2.getDes(), false, "");
                return;
            }

            // 先较验
            VerifyUserInfo bean = vudao.getVerifyingInfo(loginUser.getId(), 1);
            if (bean != null && bean.getStatus() == 0) {
                json(L("您于%%已经提交了申请，我们会尽快为您审核。", bean.getAddTimeShowString()), false, "");
                return;
            }
            // 插入申请记录
            VerifyUserInfo info = new VerifyUserInfo(vudao.getDatastore());
            info.setUserId(loginUser.getId());
            info.setUserName(loginUser.getUserName());
            info.setType(1);
            info.setMcode(countryCode);
            info.setInfo(newPhoneNumber);
            info.setBeforeInfo(uc.getSafeMobile());
            info.setAddTime(TimeUtil.getNow().getTime());
            info.setIp(ip());
            info.setVerifyOldInfo(verifyOldInfo);

            String id = vudao.add(info);
            //插入代办任务信息
            addTdTodotask(ItemType.phoneVerify.getAgencyCode(),Integer.valueOf(id),ItemType.phoneVerify.getTodoName(),ItemType.phoneVerify.getTodoNodeName(),loginUser.getId(),loginUser.getUserName(),ItemType.phoneVerify.getUrl());
            if ("1".equals(method)) {
                json(L("您的修改手机申请提交成功，客服将尽快为您审核，请耐心等待。"), true, "");
            } else {
                json(L("您的修改手机申请提交成功，稍后客服人员会联系您进行视频认证，请耐心等待。"), true, "");
            }
            log.info("01YYSPTX【修改手机申请】:" + "用户:" + loginUser.getUserName() + "发起了修改手机申请，请客服人员立即处理。");
            logDao.insertOneRecord(AuthenType.mobileApply.getKey(),userId, "0", "发起手机修改请求。", ip());
        } catch (Exception e) {
            log.error("内部异常", e);
            json(L("出错了，请稍后重试"), false, "");
        }
    }




//    @Page(Viewer = "/cn/manage/index.jsp", des = "Google认证")
    public void google() {

        try {
            initLoginUser();
            String method = param("method");
            UserContact uc = loginUser.getUserContact();
            setAttr("curUser", loginUser);
            int googleAuth = uc.getGoogleAu();
            String secret = uc.getSecret();

            setAttr("googleAuth", googleAuth);
            setAttr("mobileStatu", uc.getMobileStatu());
            setAttr("emailStatu", uc.getEmailStatu());
            secret = GoogleAuthenticator.generateSecretKey();
            setAttr("name", "Btcwinex:" + loginUser.getId());
            setAttr("secret", secret);
            if (googleAuth == 2) {
                VerifyUserInfo bean = vudao.getVerifyingInfo(loginUser.getId(), 2);
                setAttr("verifyUserInfo", bean);
            }
            setAttr("method", method);
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    /**
     * 配合前端改造
     */
    @Page(Viewer = JSON)
    public void googleJson() {

        Map<String, Object> result = new HashMap<>();

        try {
            initLoginUserJson();
            String method = param("method");
            UserContact uc = loginUser.getUserContact();
//            setAttr("curUser", loginUser);
            int googleAuth = uc.getGoogleAu();
            String secret = uc.getSecret();

            result.put("googleAuth", googleAuth);
            result.put("mobileStatu", uc.getMobileStatu());
            result.put("emailStatu", uc.getEmailStatu());
            secret = GoogleAuthenticator.generateSecretKey();
            result.put("name", "Btcwinex:" + loginUser.getId());
            result.put("secret", secret);
            if (googleAuth == 2) {
                VerifyUserInfo bean = vudao.getVerifyingInfo(loginUser.getId(), 2);
                result.put("verifyUserInfo", bean);
            }
            result.put("method", method);

            json("ok", true, JSONObject.toJSONString(result), true);
        } catch (Exception e) {
            log.error("内部异常", e);
        }

    }

    @Page(Viewer = JSON)
    public void openGoogleAuth() {
        try {
            initLoginUser();
            long gCode = longParam("gCode");
            String secret = param("secret");
            String mobileCode = param("mobileCode");
            String userIp = ip();
            if (!isCorrect(secret, gCode, JSON)) {
                return;
            }
            UserContact uc = loginUser.getUserContact();
            // 检查短信验证码
            String codeRecvAddr = loginUser.getUserContact().getSafeMobile();
            if (StringUtils.isBlank(codeRecvAddr)) {
                codeRecvAddr = loginUser.getUserContact().getSafeEmail();
            }
            ClientSession clientSession = new ClientSession(userIp, codeRecvAddr, lan, PostCodeType.googleAuth.getValue(), false);
            DataResponse dr = clientSession.checkCode(mobileCode);
            if (!dr.isSuc()) {
                json(dr.getDes(), false, "");
                return;
            }

            if (uc.getSecret() == null || StringUtils.isEmpty(uc.getSecret())) {
                String userId = userIdStr();
                Datastore ds = userDao.getDatastore();
                Query<User> q = ds.find(User.class, "_id", userId);
                UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
                ops.set("userContact.googleAu", 2);
                ops.set("userContact.secret", secret);
                ops.set("payGoogleAuth", true);
                UpdateResults<User> ur = userDao.update(q, ops);
                if (!ur.getHadError()) {
                    json(L("您已成功开启谷歌验证"), true, "");
                    logDao.insertOneRecord(AuthenType.google.getKey(),userId, "0", "绑定谷歌认证成功。", ip());
                    /*start by xwz 20170625 绑定谷歌认证*/
                    JifenManage jifenManager = new JifenManage(userId, 5, null, null, "VIP");//5：谷歌认证
                    SingletonThreadPool.addJiFenThread(jifenManager);
                    /*end*/
                    /*start by xzhang 20171104 系统中所有设置谷歌验证功能，在设置成功后，系统自动将用户的登录验证设置为“登录密码+Google验证码”*/
                    userDao.switchLoginAuthen(userDao.getById(userId), 2, userIp, request);
                    /*end*/
                } else {
                    json(L("出错了，请稍后重试"), false, "");
                }
            } else {
                // 先较验
                VerifyUserInfo bean = vudao.getVerifyingInfo(loginUser.getId(), 2);
                if (bean != null && bean.getStatus() == 0) {
                    json(L("您于%%已经提交了申请，我们会尽快为您审核。", bean.getAddTimeShowString()), false, "");
                    log.info("01YYSPTX【修改谷歌验证】:" + "用户:" + loginUser.getUserName() + "发起了修改谷歌验证申请，请客服人员立即处理。");
                    logDao.insertOneRecord(AuthenType.googleApply.getKey(),userIdStr(), "0", "发起谷歌验证修改请求。", ip());
                    return;
                }
                // 用户是修改GOOGLE认证
                // 插入申请记录
                VerifyUserInfo info = new VerifyUserInfo(vudao.getDatastore());
                info.setUserId(loginUser.getId());
                info.setUserName(loginUser.getUserName());
                info.setType(2);// Google
                info.setInfo(secret);
                info.setBeforeInfo(uc.getSecret());
                info.setAddTime(TimeUtil.getNow().getTime());
                info.setIp(ip());

                String id = vudao.add(info);
                //插入代办任务信息
                log.info("01YYSPTX【修改谷歌验证】:" + "用户:" + loginUser.getUserName() + "发起了修改谷歌验证申请，请客服人员立即处理。");
                logDao.insertOneRecord(AuthenType.googleApply.getKey(),userIdStr(), "0", "发起谷歌验证修改请求。", ip());
                addTdTodotask(ItemType.googleVerify.getAgencyCode(),Integer.valueOf(id),ItemType.googleVerify.getTodoName(),ItemType.googleVerify.getTodoNodeName(),loginUser.getId(),loginUser.getUserName(),ItemType.googleVerify.getUrl());
                json(L("申请成功，客服将尽快为您审核，请耐心等待。"), true, "");
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(L("出错了，请稍后重试"), false, "");
        }
    }

//    @Page(Viewer = "/cn/manage/index.jsp", des = "关闭Google认证")
    public void closeGoogleAuth() {
        initLoginUser();
    }

    @Page(Viewer = JSON)
    public void doCloseGoogleAuth() {
        try {
            initLoginUser();
            long gCode = longParam("gCode");
            UserContact uc = loginUser.getUserContact();
            String secret = uc.getSecret();
            if (null == secret || "".equals(secret)) {
                json(L("您还未开启Google验证。"), false, "");
                return;
            }
            if (uc.getMobileStatu() != 2) {
                json("您还没有进行手机认证不能关闭Google认证，请先进行手机认证后再操作。", false, "");
                return;
            }
            String mobileCode = param("mobileCode");
            String userIp = ip();
            if (!isCorrect(secret, gCode, JSON)) {
                return;
            }
            // 检查短信验证码
            String codeRecvAddr = loginUser.getUserContact().getSafeMobile();
            if (StringUtils.isBlank(codeRecvAddr)) {
                codeRecvAddr = loginUser.getUserContact().getSafeEmail();
            }
            ClientSession clientSession = new ClientSession(userIp, codeRecvAddr, lan, PostCodeType.safeAuth.getValue(), false);
            DataResponse dr = clientSession.checkCode(mobileCode);
            if (!dr.isSuc()) {
                json(dr.getDes(), false, "");
                return;
            }

            String userId = userIdStr();
            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("userContact.googleAu", 1);
            ops.set("userContact.secret", "");
            UpdateResults<User> ur = userDao.update(q, ops);
            if (!ur.getHadError()) {
                logDao.insertOneRecord(AuthenType.google.getKey(),userId, "0", "关闭谷歌认证成功。", ip());
                json(L("您已成功关闭谷歌验证"), true, "");
            } else {
                json(L("出错了，请稍后重试"), false, "");
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(L("出错了，请稍后重试"), false, "");
        }
    }

    public int getArea(String country) {
        int area;
        if ("+86".equals(country)) {
            area = 1;
        } else if ("+852".equals(country) || "+853".equals(country)) {
            area = 2;
        } else if ("+886".equals(country)) {
            area = 3;
        } else {
            area = 4;
        }
        return area;
    }

    //Close By suxinjie 一期屏蔽该功能
    //@Page(Viewer = "/cn/manage/auth/index.jsp", des = "异地登录验证")
    public void smsLoginCheck() {
        try {
            initLoginUser();
            setAttr("curUser", loginUser);

            int oper = intParam("ope");

            setAttr("oper", oper);
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    //Close By suxinjie 一期屏蔽该功能
    //@Page(Viewer = "/cn/manage/auth/index.jsp", des = "登录Google验证")
    public void loginGoogleAuth() {
        try {
            initLoginUser();
            UserContact uc = loginUser.getUserContact();
            if (uc.getGoogleAu() != 2) {
                setAttr("needAuth", true);
                //tip("您还没开启Google认证！点击<a target='_blank' style='color:#3366CC' href='"+VIP_DOMAIN+"/u/auth/google?oper=0&dealType=googleAuth&dealVal=99'>Google认证</a>", VIP_DOMAIN+"/u/auth/google?oper=0&dealType=googleAuth&dealVal=99", false);
            }
            setAttr("curUser", loginUser);

            int oper = intParam("ope");

            setAttr("oper", oper);
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    //Close By suxinjie 一期屏蔽该功能
    //@Page(Viewer = "/cn/manage/auth/index.jsp", des = "提币手机验证")
    public void payMobileAuth() {
        try {
            initLoginUser();
            UserContact uc = loginUser.getUserContact();
            if (uc.getMobileStatu() != 2) {
                setAttr("needAuth", true);
            }
            setAttr("curUser", loginUser);
            setAttr("mobileStatu", uc.getMobileStatu());

            int oper = intParam("ope");

            setAttr("oper", oper);
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    //Close By suxinjie 一期屏蔽该功能
    //@Page(Viewer = "/cn/manage/auth/index.jsp", des = "提币Google验证")
    public void payGoogleAuth() {
        try {
            initLoginUser();
            UserContact uc = loginUser.getUserContact();
            if (uc.getGoogleAu() != 2) {
                setAttr("needAuth", true);
                //tip("您还没开启Google认证！点击<a target='_blank' style='color:#3366CC' href='"+VIP_DOMAIN+"/u/auth/google?oper=0&dealType=googleAuth&dealVal=99'>Google认证</a>", VIP_DOMAIN+"/u/auth/google?oper=0&dealType=googleAuth&dealVal=99", false);
            }
            setAttr("curUser", loginUser);
            setAttr("mobileStatu", uc.getMobileStatu());
            setAttr("emailStatu", uc.getEmailStatu());

            int oper = intParam("ope");

            setAttr("oper", oper);
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    //Close By suxinjie 一期屏蔽该功能
    //@Page(Viewer = "/cn/manage/auth/index.jsp", des = "提币邮箱验证")
    public void payEmailAuth() {
        try {
            initLoginUser();
            UserContact uc = loginUser.getUserContact();
            if (uc.getEmailStatu() != 2) {
                setAttr("needAuth", true);
                //tip("您还没开启Google认证！点击<a target='_blank' style='color:#3366CC' href='"+VIP_DOMAIN+"/u/auth/google?oper=0&dealType=googleAuth&dealVal=99'>Google认证</a>", VIP_DOMAIN+"/u/auth/google?oper=0&dealType=googleAuth&dealVal=99", false);
            }
            setAttr("curUser", loginUser);

            int oper = intParam("ope");

            setAttr("oper", oper);
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    @Page(Viewer = JSON)
    public void changeSmsLoginCheck() {
        try {
            initLoginUser();
            int oper = intParam("ope");
            String mobileCode = param("mobileCode");
            String userIp = ip();
            // 检查短信验证码
            if (loginUser.getUserContact().getMobileStatu() == 2) {
                String codeRecvAddr = loginUser.getUserContact().getSafeMobile();
                if (StringUtils.isBlank(codeRecvAddr)) {
                    codeRecvAddr = loginUser.getUserContact().getSafeEmail();
                }
                ClientSession clientSession = new ClientSession(userIp, codeRecvAddr, lan, PostCodeType.safeAuth.getValue(), false);
                DataResponse dr = clientSession.checkCode(mobileCode);
                if (!dr.isSuc()) {
                    json(dr.getDes(), false, "");
                    return;
                }
            } else {
                userDao.setLan(lan);
                Message msg = userDao.isCorrect(loginUser, loginUser.getUserContact().getSecret(), longParam("googleCode"));
                if (!msg.isSuc()) {
                    json(msg.getMsg(), false, "");
                    return;
                }
            }
            String userId = userIdStr();
//			UserContact uc = loginUser.getUserContact();
//			if (uc.getGoogleAu() != 2) {
//				json(L("您还没开启Google认证，请开启Google认证后重试。"), false, "");
//				return;
//			}
            boolean dealVal = false;
            String des = "";

            if (oper == 0) {
                if (loginUser.isDiffAreaLoginNoCheck()) {
                    json(L("异地登录短信验证码验证已关闭。"), false, "");
                    return;
                }
                dealVal = true;
                des = "成功关闭异地登录短信验证码验证。";
            } else if (oper == 1) {
                if (!loginUser.isDiffAreaLoginNoCheck()) {
                    json(L("异地登录短信验证码验证已开启。"), false, "");
                    return;
                }
                dealVal = false;
                des = "成功开启异地登录短信验证码验证。";
            } else {
                json(L("您已关闭异地登录短信验证码验证。"), false, "");
                return;
            }
            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("diffAreaLoginNoCheck", dealVal);
            UpdateResults<User> ur = userDao.update(q, ops);
            if (!ur.getHadError()) {
                json(L(des), true, "");
            }

            /*Start by guankaili 20181229 添加消息队列 */
            User user = userDao.get(userId);
//            new OperateLogDao().record(userDao.get(userId), LogCategory.OTHER_PLACE_LOGIN_SWITCH, oper == 1 ? "开启成功" : "关闭成功", ip(), request);
            OperateLogInfoProducer.send(user.getUserName(),user.getId(),LogCategory.OTHER_PLACE_LOGIN_SWITCH.getKey(), oper == 1 ? "开启成功" : "关闭成功", ip(),CommonUtil.getBrowserInfo(request));
            /*End*/
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    @Page(Viewer = JSON)
    public void changeLoginGoogleAuth() {
        try {
            initLoginUser();
            int oper = intParam("ope");

            boolean dealVal = false;
            String des = "";

            long googleCode = longParam("googleCode");
            if (!isCorrect(loginUser.getUserContact().getSecret(), googleCode, JSON)) {
                return;
            }

            String mobileCode = param("mobileCode");
            String userIp = ip();
            String userId = userIdStr();
            UserContact uc = loginUser.getUserContact();
            if (uc.getGoogleAu() != 2) {
                json(L("您还没开启Google认证，请开启Google认证后重试。"), false, "");
                return;
            }
            // 检查短信验证码
            String codeRecvAddr = loginUser.getUserContact().getSafeMobile();
            if (StringUtils.isBlank(codeRecvAddr)) {
                codeRecvAddr = loginUser.getUserContact().getSafeEmail();
            }
            ClientSession clientSession = new ClientSession(userIp, codeRecvAddr, lan, PostCodeType.safeAuth.getValue(), false);
            DataResponse dr = clientSession.checkCode(mobileCode);
            if (!dr.isSuc()) {
                json(dr.getDes(), false, "");
                return;
            }

            if (oper == 0) {
                if (!loginUser.isLoginGoogleAuth()) {
                    json(L("登录Google验证码验证已关闭。"), false, "");
                    return;
                }
                dealVal = false;
                des = "已成功关闭登录Google验证码验证。";
            } else if (oper == 1) {
                if (loginUser.isLoginGoogleAuth()) {
                    json(L("登录Google验证码验证已开启。"), false, "");
                    return;
                }

                int safeBu = loginUser.getUserContact().getGoogleAu();

                if (safeBu != 2) {
                    json(L("请先开启Google认证。"), false, "");
                    return;
                }

                dealVal = true;
                des = "成功开启登录Google验证码验证。";
            } else {
                json(L("已成功关闭登录Google验证码验证。"), false, "");
                return;
            }

            /*Start by guankaili 20181229 添加消息队列 */
            User user = userDao.get(userId);
//            new OperateLogDao().record(userDao.get(userId), LogCategory.LOGIN_WITH_GOOGLE_CODE_SWITCH, oper == 1 ? "开启成功" : "关闭成功", ip(), request);
            OperateLogInfoProducer.send(user.getUserName(),user.getId(),LogCategory.LOGIN_WITH_GOOGLE_CODE_SWITCH.getKey(), oper == 1 ? "开启成功" : "关闭成功", ip(),CommonUtil.getBrowserInfo(request));
            /*End*/
            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("loginGoogleAuth", dealVal);
            UpdateResults<User> ur = userDao.update(q, ops);
            if (!ur.getHadError()) {
                json(L(des), true, "");
            }
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    //Close By suxinjie 一期屏蔽该功能
    //@Page(Viewer = JSON)
    public void changePayGoogleAuth() {
        try {
            initLoginUser();
            int oper = intParam("ope");

            boolean dealVal = false;
            String des = "";
            long googleCode = CommonUtil.stringToLong(param("googleCode"));
            if (!isCorrect(loginUser.getUserContact().getSecret(), googleCode, JSON)) {
                return;
            }

            String mobileCode = param("mobileCode");
            String userIp = ip();

            if (oper == 0) {
                if (!loginUser.isPayGoogleAuth()) {
                    json(L("提币Google验证码验证已关闭。"), false, "");
                    return;
                }
                if (loginUser.getUserContact().getMobileStatu() != 2 || StringUtils.isBlank(loginUser.getUserContact().getSafeMobile())) {
                    json(L("您没有进行手机认证不能关闭Google提币验证码验证。"), false, "");
                    return;
                }
                if (!loginUser.isPayMobileAuth()) {
                    json(L("您已关闭提币短信验证不能关闭提币Google验证。"), false, "");
                    return;
                }
                dealVal = false;
                des = "已成功关闭提币Google验证。";
            } else if (oper == 1) {
                if (loginUser.isPayGoogleAuth()) {
                    json(L("提币Google验证已开启。"), false, "");
                    return;
                }

                int safeBu = loginUser.getUserContact().getGoogleAu();

                if (safeBu != 2) {
                    json(L("请先开启Google认证。"), false, "");
                    return;
                }

                dealVal = true;
                des = "成功开启提币Google验证。";
            } else {
                json(L("已成功关闭提币Google验证。"), false, "");
                return;
            }

            String userId = userIdStr();
            /*Start by guankaili 20181229 添加消息队列 */
            User user = userDao.get(userId);
//            new OperateLogDao().record(userDao.get(userId), LogCategory.WITHDRAW_WITH_GOOGLE_CODE_SWITCH, oper == 1 ? "开启成功" : "关闭成功", ip(), request);
            OperateLogInfoProducer.send(user.getUserName(),user.getId(),LogCategory.WITHDRAW_WITH_GOOGLE_CODE_SWITCH.getKey(), oper == 1 ? "开启成功" : "关闭成功", ip(),CommonUtil.getBrowserInfo(request));
            /*End*/
            // 检查短信验证码
            String codeRecvAddr = loginUser.getUserContact().getSafeMobile();
            if (StringUtils.isBlank(codeRecvAddr)) {
                codeRecvAddr = loginUser.getUserContact().getSafeEmail();
            }
            ClientSession clientSession = new ClientSession(userIp, codeRecvAddr, lan, PostCodeType.safeAuth.getValue(), false);
            DataResponse dr = clientSession.checkCode(mobileCode);
            if (!dr.isSuc()) {
                json(dr.getDes(), false, "");
                return;
            }

            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("payGoogleAuth", dealVal);
            UpdateResults<User> ur = userDao.update(q, ops);
            if (!ur.getHadError()) {
                json(L(des), true, "");
            }
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    //Close By suxinjie 一期屏蔽该功能
    //@Page(Viewer = JSON)
    public void changePayMobileAuth() {
        try {
            initLoginUser();
            int oper = intParam("ope");

            boolean dealVal = false;
            String des = "";

            String mobileCode = param("mobileCode");
            String userIp = ip();

            if (oper == 0) {
                if (!loginUser.isPayMobileAuth()) {
                    json(L("提币短信验证码验证已关闭。"), false, "");
                    return;
                }
                if (!loginUser.isPayGoogleAuth()) {
                    json(L("您已关闭提币Google验证不能关闭提币短信验证。"), false, "");
                    return;
                }
                dealVal = false;
                des = "已成功关闭提币短信验证。";
            } else if (oper == 1) {
                if (loginUser.isPayMobileAuth()) {
                    json(L("提币Google验证码验证已开启。"), false, "");
                    return;
                }

                int safeBu = loginUser.getUserContact().getMobileStatu();

                if (safeBu != 2) {
                    json(L("请先进行手机认证。"), false, "");
                    return;
                }

                dealVal = true;
                des = "成功开启提币短信验证。";
            } else {
                json(L("已成功关闭提币短信验证。"), false, "");
                return;
            }

            String userId = userIdStr();
            /*Start by guankaili 20181229 添加消息队列 */
            User user = userDao.get(userId);
//            new OperateLogDao().record(userDao.get(userId), LogCategory.WITHDRAW_WITH_SMS_SWITCH, oper == 1 ? "开启成功" : "关闭成功", ip(), request);
            OperateLogInfoProducer.send(user.getUserName(),user.getId(),LogCategory.WITHDRAW_WITH_SMS_SWITCH.getKey(), oper == 1 ? "开启成功" : "关闭成功", ip(),CommonUtil.getBrowserInfo(request));
            /*End*/
            // 检查短信验证码
            String codeRecvAddr = loginUser.getUserContact().getSafeMobile();
            ClientSession clientSession = new ClientSession(userIp, codeRecvAddr, lan, PostCodeType.safeAuth.getValue(), false);
            DataResponse dr = clientSession.checkCode(mobileCode);
            if (!dr.isSuc()) {
                json(dr.getDes(), false, "");
                return;
            }

            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("payMobileAuth", dealVal);
            UpdateResults<User> ur = userDao.update(q, ops);
            if (!ur.getHadError()) {
                json(L(des), true, "");
            }
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    @Page(Viewer = JSON)
    public void changePayEmailAuth() {
        try {
            initLoginUser();
            int oper = intParam("ope");

            boolean dealVal = false;
            String des = "";

            String mobileCode = param("mobileCode");
            String userIp = ip();
            // 检查短信验证码
            String codeRecvAddr = loginUser.getUserContact().getSafeMobile();
            if (StringUtils.isBlank(codeRecvAddr)) {
                codeRecvAddr = loginUser.getUserContact().getSafeEmail();
            }
            ClientSession clientSession = new ClientSession(userIp, codeRecvAddr, lan, PostCodeType.safeAuth.getValue(), false);
            DataResponse dr = clientSession.checkCode(mobileCode);
            if (!dr.isSuc()) {
                json(dr.getDes(), false, "");
                return;
            }

            if (oper == 0) {
                if (!loginUser.isPayEmailAuth()) {
                    json(L("支付邮箱验证已关闭。"), false, "");
                    return;
                }
                dealVal = false;
                des = "已成功关闭支付邮箱验证码验证。";
            } else if (oper == 1) {
                if (loginUser.isPayEmailAuth()) {
                    json(L("支付邮箱验证码验证已开启。"), false, "");
                    return;
                }

                int safeBu = loginUser.getUserContact().getEmailStatu();

                if (safeBu != 2) {
                    json(L("请先进行邮箱认证。"), false, "");
                    return;
                }

                dealVal = true;
                des = "成功开启支付邮箱验证码验证。";
            } else {
                json(L("已成功关闭支付邮箱验证码验证。"), false, "");
                return;
            }
            String userId = userIdStr();
            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("payEmailAuth", dealVal);
            UpdateResults<User> ur = userDao.update(q, ops);
            if (!ur.getHadError()) {
                json(L(des), true, "");
            }
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    /**
     * 实名验证页面处理
     */
    //Close By xiewenzheng 屏蔽该功能
//	@Page(Viewer = "/cn/manage/index.jsp", des = "实名认证")
    public void simple() {

        initLoginUser();
        setAttr("curUser", loginUser);
        CountryDao cDao = new CountryDao();
        setAttr("country", cDao.findAll());
        Authentication auth = auDao.getByField("userId", userIdStr());

        if (auth != null) {
            if (auth.getStatus() > 0 && auth.getStatus() < 4) {
                try {
                    response.sendRedirect(VIP_DOMAIN + "/manage/auth/depth");
                    return;
                } catch (IOException e) {
                    log.error("内部异常", e);
                }
            }
            if (null != auth.getCardId() && auth.getCardId().length() == 0) {
                auth.setCardId(loginUser.getUserContact().getIdCard());
            }
            setAttr("auth", auth);
        }

        String type = param("type");
        boolean isTypeNull = StringUtils.isBlank(type);
        String pagefward = "";
        if (auth == null && isTypeNull) {
            pagefward = "switchType";
            //forward("/cn/u/auth/switchType.jsp");
        } else if ((auth != null && auth.getType() == AuditType.corporate.getKey()) || (!isTypeNull && type.equals(String.valueOf(AuditType.corporate.getKey())))) {
            pagefward = "enterpriseAuth";
            //forward("/cn/u/auth/enterpriseAuth.jsp");
        } else {
            pagefward = "simpleAuth";
            //forward("/cn/u/auth/simpleAuth.jsp");
        }
        setAttr("pagefward", pagefward);
    }





	/*start by xwz 2017-06-09*/

    /*
    * 调入实名认证页面
    *
    * */
    //Close By xiewenzheng 屏蔽该功能
//	@Page(Viewer = "/cn/manage/withdraw/index.jsp", des = "提现额度")
    public void userAuthIndex() {
        initLoginUser();
        CountryDao cDao = new CountryDao();
        Query q = cDao.getQuery().order("code");
        List<Country> countrylist = q.asList();
        setAttr("country", countrylist);
        //根据代码(code)获取国家（des）

        Authentication auth = auDao.getByUserId(userIdStr());
        if (auth == null || auth.getStatus() == 0) {
            auth = new Authentication();
            auth.setStatus(AuditStatus.a1NoSubmit.getKey());

        } else {
            for (Country country : countrylist) {
                if (country.getCode().equals(auth.getCountryCode())) {
                    setAttr("countryName", country.getDes());
                }
            }
        }
        setAttr("auth", auth);
    }

    /**
     * 个人初级实名验证保存
     */
    //Close By xiewenzheng 屏蔽该功能
    //	@Page(Viewer = JSON)
    public void simpleSave() {
        try {
            initLoginUser();
            Authentication auth = auDao.getByUserId(userIdStr());
            if (auth != null && auth.getAuthType() != 0) {
                json(L("不符合认证条件"), false, "");
                return;
            }
            Message msg = AuthUtil.saveSimpleIndividualAuth(loginUser, lan, request, ip());
            json(msg.getMsg(), msg.isSuc(), "");
            reloadLoginUser();
        } catch (Exception e) {
            json(L("初级认证出错，请稍后重试"), false, "");
        }
    }


    //验证证件号是否存在
    //Close By xiewenzheng 屏蔽该功能
//	@Page(Viewer = JSON)
    public void isExsitIdNumber() {
        String cardId = param("idNumber");
        IdcardBlackListDao idcardBlackListDao = new IdcardBlackListDao();
        boolean isIdcardBlackList = idcardBlackListDao.isBlackList(cardId);
        if (isIdcardBlackList) {
            json("", true, "true");
        } else {
            json("", true, "false");
        }
        return;
    }
    /*end*/

    /**
     * 个人高级实名认证
     */
    //Close By xiewenzheng 屏蔽该功能
//	@Page(Viewer = "/cn/manage/index.jsp", des="高级实名认证" )
    public void depth() {
        initLoginUser();
        Authentication auth = auDao.getByUserId(userIdStr());
        if ((auth == null || auth.getStatus() != AuditStatus.a1Pass.getKey()) && ((auth == null || (auth != null && auth.getStatus() >= AuditStatus.a1NoSubmit.getKey())))) {
            try {
                response.sendRedirect("/manage/auth/simple");
            } catch (IOException e) {
                log.error("内部异常", e);
            }
            return;
        }

        if (auth != null) {
            if (auth.getType() == AuditType.corporate.getKey()) {
                go(VIP_DOMAIN + "/manage/auth/depthEnterprise");
                return;
            }
            if (null != auth.getCardId() && auth.getCardId().length() == 0) {
                auth.setCardId(loginUser.getUserContact().getIdCard());
            }
            if (auth.getStatus() == 6) {

                Datastore ds = auDao.getDatastore();
                Query<Authentication> q = ds.find(Authentication.class, "userId", loginUser.getId());
                UpdateOperations<Authentication> ops = ds.createUpdateOperations(Authentication.class);
                ops.set("simplePass", true);
                auDao.update(q, ops);

                auth.setSimplePass(true);
            }
            setAttr("auth", auth);
        }

        CountryDao cDao = new CountryDao();
        setAttr("country", cDao.findAll());
    }

    /**
     * 个人高级实名认证保存
     */
    //Close By xiewenzheng 屏蔽该功能
//	@Page(Viewer = JSON)
    public void depthSave() {
        initLoginUser();
        try {
            if (loginUser.getFailAuthTimes() > Const.MAX_AUTH_FAIL_TIMES) {
                json(L("对不起，由于您的不正当操作，导致账号无法实名认证，如需认证，请联系人工客服处理。"), false, "");
                return;
            }

            String userId = userIdStr();
            String ip = ip();

            String frontalImg = param("frontalImg");    //正面证件照
            String backImg = param("backImg");            //证件背面照
            String loadImg = param("loadImg");            //手持证件照
            /*start by xwz 2016-06-09*/
            String startDate = param("startDate");        //证件签发日期
            String endDate = param("endDate");        //证件到期日期
			/*end*/
			/*start by xwz 2016-06-09*/
//			String country = param("country");
            String realName = param("realName");
            String cardId = param("cardId");
//			String bankCard = param("bankCard");		//银行卡号
//			String bankTel = param("bankTel");			//银行预留手机号
//			String bankCardType = param("bankCardType");//银行卡类型
            int area = intParam("area");                //地区
//			String proofAddressImg = param("addressImg");


//			if (null == country || "".equals(country)) {
//				json(L("请选择证件所在区域"), false, "");
//				return;
//			}

            if (null == frontalImg || "".equals(frontalImg)) {
                json(L("请上传证件正面照"), false, "");
                return;
            }
            if (null == backImg || "".equals(backImg)) {
                json(L("请上传证件背面照"), false, "");
                return;
            }
            if (null == loadImg || "".equals(loadImg)) {
                json(L("请上传手持证件照"), false, "");
                return;
            }
            if (null == startDate || "".equals(startDate)) {
                json(L("请填写证件签发日期"), false, "");
                return;
            }
            if (null == endDate || "".equals(endDate)) {
                json(L("请填写证件到期日期"), false, "");
                return;
            }
			/*start by xwz 2017-06-09*/
//			if (null == bankCard || "".equals(bankCard)) {
//				json(L("请填写有效的银行卡号"), false, "");
//				return;
//			}
//			if (null == bankTel || "".equals(bankTel)) {
//				json(L("请填写有效的银行预留手机号"), false, "");
//				return;
//			}

//			if (area < 1) {
//				json(L("请选择有效的证件地区"), false, "");
//				return;
//			}
//			if (null == realName || "".equals(realName)) {
//				json(L("请填写有效的证件姓名"), false, "");
//				return;
//			}
//			if (null == cardId || "".equals(cardId)) {
//				json(L("请填写有效的证件号"), false, "");
//				return;
//			}
//
//			area = AuthUtil.getArea(country);
//			if (area > 1) {
//				if (null == proofAddressImg || "".equals(proofAddressImg)) {
//					json(L("请上传住址证明"), false, "");
//					return;
//				}
//			}

            // 更新认证总次数
            userDao.increaseValue(userIdStr(), "authTimes");

            Authentication auth = auDao.getByUserId(userIdStr());
			/*start by xwz 2017-6-9 将判断条件isSimplePass 改为 auth.getStatus() == 2 && auth.getAuthType() == 1*/
//			if (null != auth && auth.isSimplePass()) {
            if (null != auth && (auth.getStatus() == 6 || auth.getStatus() == 3)) {
                area = auth.getAreaInfo();
                realName = auth.getRealName();
                cardId = auth.getCardId();
            } else {

				/*start by xwz 2017-07-09*/
                json(L("不符合认证条件"), false, "");
                return;
//				if (auDao.isLimit(cardId, userIdStr())) {
//					json(AuthUtil.getMsgAndRecordFailTimes(
//							userDao, loginUser, lan, "您的证件申请实名认证次数超过限制，无法通过", true), false, "");
//					return;
//				}
            }

//			if (null == bankCardType || "".equals(bankCardType)) {
//				bankCardType = "1";
//			}

//			String bankCvv2 = param("bankCvv2");
//			String bankExpiredate = param("bankExpiredate");
			/*end*/
            Authentication au = new Authentication(auDao.getDatastore());
            au.setIp(ip);
            au.setSubmitTime(now());
            au.setAreaInfo(area);
//			au.setCountryCode(country);
            au.setRealName(realName);
            au.setCardId(cardId);
            au.setUserId(userId);

            au.setServiceStatu(3);// 比对一致
            au.setImgCode("");
            au.setPhoto("");

//			au.setBankCard(bankCard);
//			au.setBankTel(bankTel);
//			au.setBankCardType(bankCardType);
//			au.setBankCvv2(bankCvv2);
//			au.setBankExpiredate(bankExpiredate);
            au.setFrontalImg(frontalImg);
            au.setBackImg(backImg);
            au.setLoadImg(loadImg);
//			au.setProofAddressImg(proofAddressImg);
            au.setAuthType(2);
			/*start by xwz 2017-06-09*/
            au.setStartDate(startDate);
            au.setEndDate(endDate);
			/*end*/


			/*start by xwz*/
//			IdcardBlackListDao idcardBlackListDao = new IdcardBlackListDao();
//			boolean isIdcardBlackList = idcardBlackListDao.isBlackList(cardId);
//			au.setCardIdBlackList(isIdcardBlackList);
//			IdcardDao idcardDao = new IdcardDao();
//			boolean isPass = false, isDalu = true;
//			if (area == AreaInfo.dalu.getKey()) {
//				//isPass = idcardDao.validIdcard(realName, cardId);注释身份证接口验证先
//				isPass=true;
//			} else {
//				isPass = true;
//				isDalu = false;
//			}
//			if (!isPass && isDalu) {
//				json(AuthUtil.getMsgAndRecordFailTimes(
//						userDao, loginUser, lan, "证件信息验证不通过，请填写真实证件信息后重新提交认证", true), false, "");
//				return;
//			} else {
//				au.setSimplePass(true);
//				//au.setAuthType(1);
//			}

            au.setSimplePass(true);
			/*end*/
            au.setStatus(AuditStatus.noAudite.getKey());

            if (!auDao.updateAuth(au).getHadError()) {
                AuthenHistoryDao ahDao = new AuthenHistoryDao();
                try {
                    AuthenHistory ah = ahDao.getAuthHis(au);
                    ahDao.save(ah);
                } catch (Exception e) {
                    log.error(e.toString(), e);
                }
                json(L("资料提交成功，请耐心等待审核。"), true, "");
            } else {
                json(L("保存失败。"), false, "");
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(L("认证出错，请稍后重试"), false, "");
        }
    }


    /**
     * 开启身份认证
     */
    //open By chendi
    @Page(Viewer = JSON)
    public void AuthSave() {
        initLoginUser();
        try {
            String userId = userIdStr();
            if (StringUtils.isBlank(userId)) {
                json("用户不存在", false, "");
                return;
            }
            String value = Cache.Get(Const.black_key+userIdStr());
            if(StringUtils.isNotBlank(value)){
                json(L("保存失败。"), false, "");
                return;
            }
            String errorMsg = null;
            String frontalImg = param("frontalImg");    //正面证件照
            String backImg = param("backImg");            //证件背面照
            String loadImg = param("loadImg");            //手持证件照
            User user = userDao.getById(userId);
            String ip = ip();
            String firstName = param("firstName");
            String lastName = param("lastName");
            String realName = lastName + firstName;
            log.info("收到的实名认证姓名："+realName);
            String cardId = param("cardId");            //证件号
            String startDate = param("startDate");		//证件签发日期
            String endDate = param("endDate");		//证件到期日期
            String countryCode = param("countryCode");
            String countryName = param("countName");
            String cardType = param("cardType");
            /*if (StringUtils.isNotBlank(frontalImg)) {
                frontalImg = QcloudCosUtil.getHost() + frontalImg;
            }
            if (StringUtils.isNotBlank(backImg)) {
                backImg = QcloudCosUtil.getHost() + backImg;
            }
            if (StringUtils.isNotBlank(loadImg)) {
                loadImg = QcloudCosUtil.getHost() + loadImg;
            }*/
            /*if("1".equals(cardType)){
                if (StringUtils.isBlank(cardId) || cardId.length() <= 4) {
                    errorMsg = CommonUtil.mapToJsonStr(errorMsg,"card",)
                    json(L("身份证不合法"), false, "\"cardId\"");
                    return;
                }
            }else{
                if (StringUtils.isBlank(cardId) || cardId.length() <= 4) {
                    json(L("护照不合法"), false, "\"cardId\"");
                    return;
                }
            }*/
            if (StringUtils.isBlank(cardType)) {
                json(L("认证类型不能为空"), false, "\"cardType\"");
                return;
            }
            if (StringUtils.isBlank(lastName)) {
                errorMsg = CommonUtil.mapToJsonStr(errorMsg,"lastName", L("请输入姓氏"));
            }
            if (StringUtils.isBlank(firstName)) {
                errorMsg = CommonUtil.mapToJsonStr(errorMsg,"firstName", L("请输入名字"));
            }
            if (null != errorMsg) {
                json("", false, errorMsg);
                return;
            }

            if (StringUtils.isBlank(realName)) {
                json(L("用户名不能为空"), false, "\"cardType\"");
                return;
            }
            int area = 3;                //海外地区
//            String startDate = param("startDate");
//            String endDate = param("endDate");
            if ("+86".equals(countryCode)) {
                area = 1; //大陆
            } else if ("+852".equals(countryCode) || "+853".equals(countryCode) || "+886".equals(countryCode)) {
                area = 2; //港澳台
            }

            //身份证号码转大写
            cardId = cardId.toUpperCase();
            Query<Authentication> q = auDao.getQuery();
            q.filter("cardId", cardId);
            q.filter("cardType", cardType);
            List<Authentication> dataList = auDao.findPage(q, 1, 20);
            Authentication auth = auDao.getByUserId(userIdStr());
            if(auth != null ){
                int lockState = auth.getLockStatus();
                if (lockState == 1) {
                    Timestamp lockTime = auth.getLockTime();
                    if(System.currentTimeMillis() - lockTime.getTime() <= 72 * 60 * 60 * 1000){
                        json(L("保存失败。"), false, "");
                        return;
                    }
                }
            }
            if (!CollectionUtils.isEmpty(dataList)) {
                for (Authentication authentication : dataList) {
                    if (authentication.getStatus() != AuditStatus.a1NoPass.getKey()) {
                        if("1".equals(cardType)){
                            errorMsg = CommonUtil.mapToJsonStr(errorMsg,"cardId",L("您的身份证号码已存在"));
                            json("", false, errorMsg);
                            return;
                        } else {
                            errorMsg = CommonUtil.mapToJsonStr(errorMsg,"cardId",L("您的护照号码已存在"));
                            json("", false, errorMsg);
                            return;
                        }
                    }


                }
            }
           /* String path = uploadImg(frontalImg);
            String backImgPath= uploadImg(backImg);
            String loadImgPath= uploadImg(loadImg);

            if(path.equals("")){
                json(L("保存失败。"), false, "");
                return;
            }*/
            //   Authentication auth = auDao.getByUserId(userIdStr());
           /* if (null != auth && (auth.getStatus() == -4)) {
                frontalImg = auth.getFrontalImg();
                backImg = auth.getBackImg();
                loadImg = auth.getLoadImg();
                realName = auth.getRealName();
                cardId = auth.getCardId();
                area = auth.getAreaInfo();
            } else {
                json(L("不符合认证条件"), false, "");
                return;
            }*/
//            Authentication au = new Authentication(auDao.getDatastore());
//            au.setIp(ip);
//            au.setSubmitTime(now());
//            au.setAreaInfo(area);
//            au.setRealName(realName);
//            au.setCardId(cardId);
//            au.setUserId(userId);
//            au.setFrontalImg(frontalImg);
//            au.setBackImg(backImg);
//            au.setLoadImg(loadImg);
//            au.setStatus(AuditStatus.a1NoAudite.getKey());
//            au.setServiceStatu(3);// 比对一致
//            au.setImgCode("");
//            au.setCountryName(countryName);
//            au.setStartDate(startDate);
//            au.setEndDate(endDate);
//            au.setPhoto("");
//            au.setCountryCode(countryCode);
//            au.setCardType(cardType);
//            au.setSimplePass(true);
            String url = ApiConfig.getValue("usecenter.url");
            FeignContainer container = new FeignContainer(url+"/user");
            UserApiService userApiService = container.getFeignClient(UserApiService.class);
            AuthenticationVo auv = new AuthenticationVo();
            auv.setIp(ip);
            auv.setRealName(realName);
            auv.setCardId(cardId);
            auv.setUserId(userId);
            auv.setFrontalImg(frontalImg);
            auv.setBackImg(backImg);
            auv.setLoadImg(loadImg);
            auv.setStatus(AuditStatus.a1NoAudite.getKey());
            // 比对一致
            auv.setCountryName(countryName);
            auv.setCountryCode(countryCode);
            auv.setCardType(cardType);
            auv.setStartDate(startDate);
            auv.setEndDate(endDate);
//            BeanUtils.copyProperties(au,auv);
            R r = userApiService.authSaveApin(auv);
            if(!r.getFlag()){
                json(L(r.getMsg()), r.getFlag(), "");
                return;
            };
            json(L("资料提交成功，请耐心等待审核。"), true, "");
            log.info("10100301VIPKQSFRZ【实名认证】:" + "用户:" + user.getUserName() + "申请了实名认证，请客服人员立即处理。");
            logDao.insertOneRecord(AuthenType.realnameApply.getKey(),userId, "0", "发起实名认证请求。", ip());
            return;
        } catch (Exception e) {
            log.error("10100303VIPKQSFRZ【开启身份认证】 com.world.controller.manage.auth.index#AuthSave",e);
            json(L("认证出错，请稍后重试"), false, "");
            return;
        }
    }

    /**
     * 企业用户提交初级认证
     */
    //Close By xiewenzheng 屏蔽该功能
//	@Page(Viewer = JSON)
    public void enterpriseSave() {
        initLoginUser();
        try {
            if (loginUser.getFailAuthTimes() > Const.MAX_AUTH_FAIL_TIMES) {
                json(L("对不起，由于您的不正当操作，导致账号无法实名认证，如需认证，请联系人工客服处理。"), false, "");
                return;
            }

            String tipMsg = AuthUtil.overTimsInADayTip(loginUser, userDao, lan);
            if (tipMsg != null) {
                json(tipMsg, false, "");
                return;
            }

            initLoginUser();
            String userId = userIdStr();

            Authentication au = checkAndFillEnterpriseForm(userId);

            if (au == null) {
                return;
            }
            // 更新认证总次数
            Datastore ds1 = userDao.getDatastore();
            Query<User> q2 = ds1.find(User.class, "_id", userIdStr());
            UpdateOperations<User> ops1 = ds1.createUpdateOperations(User.class);
            ops1.inc("authTimes");
            userDao.update(q2, ops1);

            au.setAuthType(1);
            if (!auDao.updateAuth(au).getHadError()) {
                AuthenHistoryDao ahDao = new AuthenHistoryDao();
                try {
                    AuthenHistory ah = ahDao.getAuthHis(au);
                    ahDao.save(ah);
                } catch (Exception e) {
                    log.error("内部异常", e);
                }
//				String msg = isDalu?"初级认证成功。":"提交初级实名认证成功，请等待客服审核";
                String msg = "初级认证成功。";
                //用户通过认证后，自动清除累计认证错误次数。
                userDao.update(q2, ds1.createUpdateOperations(User.class).set("failAuthTimes", 0));
                // 更新充值/提现记录  认证与提现充值无关
				/*String updateSql1 = "update Bank_Trade set Status=? where Status=? and Is_In=0 and User_Id=?";
				Data.Update(updateSql1,
						new Object[] { BankTradeStatus.WaitConfirm.getId(), BankTradeStatus.NEEDAUTH.getId(), userId });
				String updateSql2 = "update Bank_Trade set Status=? where Status=? and Is_In=1 and User_Id=?";
				Data.Update(updateSql2, new Object[] { BankTradeStatus.WaitRecharge.getId(),
						BankTradeStatus.NEEDAUTH.getId(), userId });*/
				/*auDao.updateNeedAuth(Integer.parseInt(userId), "1", 0);
				auDao.updateNeedAuth(Integer.parseInt(userId), "2", 0);*/
                json(L(msg), true, "");

                AuthUtil.deleteOverTimsInADayLimit(userId);

                userDao.updateRealNameAndAuthType(userId, au.getRealName(), AuditType.corporate.getKey(), true);
                reloadLoginUser();

            } else {
                json(L("保存失败。"), false, "");
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(L("初级认证出错，请稍后重试"), false, "");
        }
    }

    //Close By xiewenzheng 屏蔽该功能
//	@Page(Viewer = "/cn/manage/auth/depthEnterpriseAuth.jsp")
    public void depthEnterprise() {
        Authentication au = auDao.getByField("userId", userIdStr());
        if (au == null || au.getType() == AuditType.individual.getKey()) {
            go(VIP_DOMAIN + "/manage/auth/simple");
            return;
        }

        if (au != null) {
            updateBaseAuditPass(au);
            setAttr("auth", au);
        }
    }

    private void updateBaseAuditPass(Authentication au) {
        if (au.getStatus() == 6 && !au.isSimplePass()) {
            initLoginUser();
            Datastore ds = auDao.getDatastore();
            Query<Authentication> q = ds.find(Authentication.class, "userId", loginUser.getId());
            UpdateOperations<Authentication> ops = ds.createUpdateOperations(Authentication.class);
            au.setSimplePass(true);
            ops.set("simplePass", au.isSimplePass());
            auDao.update(q, ops);
        }
    }

    //Close By xiewenzheng 屏蔽该功能
//	@Page(Viewer = JSON)
    public void depthEnterpriseSave() {
        initLoginUser();
        try {
            if (loginUser.getFailAuthTimes() > Const.MAX_AUTH_FAIL_TIMES) {
                json(L("对不起，由于您的不正当操作，导致账号无法实名认证，如需认证，请联系人工客服处理。"), false, "");
                return;
            }

            String ip = ip();

            String businessLicenseImg = param("businessLicenseImg");
            String taxRegistrationCertificateImg = param("taxRegistrationCertificateImg");
            String organizationCodeImg = param("organizationCodeImg");
            String frontalImg = param("frontalImg");
            String backImg = param("backImg");
            String linkerFrontalImg = param("linkerFrontalImg");
            String linkerBackImg = param("linkerBackImg");
            Authentication au = auDao.getByField("userId", userIdStr());

            if (StringUtils.isBlank(businessLicenseImg) && StringUtils.isBlank(au.getBusinessLicenseImg())) {
                json(L("请上传营业执照"), false, "");
                return;
            }
            if (StringUtils.isBlank(taxRegistrationCertificateImg) && StringUtils.isBlank(au.getTaxRegistrationCertificateImg())) {
                json(L("请上传税务登记证"), false, "");
                return;
            }
            if (StringUtils.isBlank(organizationCodeImg) && StringUtils.isBlank(au.getOrganizationCodeImg())) {
                json(L("请上传组织机构代码证"), false, "");
                return;
            }

            if (StringUtils.isBlank(frontalImg) && StringUtils.isBlank(au.getFrontalImg())) {
                json(L("请上传法人身份证正面照"), false, "");
                return;
            }

            if (StringUtils.isBlank(backImg) && StringUtils.isBlank(au.getBackImg())) {
                json(L("请上传法人身份证背面照"), false, "");
                return;
            }

            if (StringUtils.isBlank(linkerFrontalImg) && StringUtils.isBlank(au.getLinkerFrontalImg())) {
                json(L("请上传联系人身份证正面照"), false, "");
                return;
            }

            if (StringUtils.isBlank(linkerBackImg) && StringUtils.isBlank(au.getLinkerBackImg())) {
                json(L("请上传联系人身份证背面照"), false, "");
                return;
            }

            String enterpriseRegisterNo = null;
            if (null != au && au.isSimplePass()) {
                enterpriseRegisterNo = au.getEnterpriseRegisterNo();
            } else {
                json(L("请先填写初级认证资料"), false, "");
                return;
            }


            Query q1 = auDao.getQuery().filter("enterpriseRegisterNo", enterpriseRegisterNo);
            long count = q1.countAll();
            if (count >= Const.MAX_AUTH_AMOUNT) {
                json(L("您的证件申请实名认证次数超过限制，无法通过"), false, "");
                return;
            }
            // 更新认证总次数
            Datastore ds1 = userDao.getDatastore();
            Query<User> q2 = ds1.find(User.class, "_id", userIdStr());
            UpdateOperations<User> ops1 = ds1.createUpdateOperations(User.class);
            ops1.inc("authTimes");
            userDao.update(q2, ops1);

            au.setIp(ip);
            au.setSubmitTime(now());
            au.setServiceStatu(3);// 比对一致
            au.setImgCode("");
            au.setPhoto("");
            au.setFrontalImg(frontalImg);
            au.setBackImg(backImg);
            au.setBusinessLicenseImg(businessLicenseImg);
            au.setTaxRegistrationCertificateImg(taxRegistrationCertificateImg);
            au.setOrganizationCodeImg(organizationCodeImg);
            au.setLinkerFrontalImg(linkerFrontalImg);
            au.setLinkerBackImg(linkerBackImg);

            au.setStatus(AuditStatus.noAudite.getKey());

            if (!auDao.updateAuth(au).getHadError()) {
                AuthenHistoryDao ahDao = new AuthenHistoryDao();
                try {
                    AuthenHistory ah = ahDao.getAuthHis(au);
                    ahDao.save(ah);
                } catch (Exception e) {
                    log.error(e.toString(), e);
                }
                json(L("资料提交成功，请耐心等待审核。"), true, "");
            } else {
                json(L("保存失败。"), false, "");
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(L("认证出错，请稍后重试"), false, "");
        }
    }

    private Authentication checkAndFillEnterpriseForm(String userId) {
        String realName = param("realName");
        String legalPersonName = param("legalPersonName");
        //String country = param("countryCodeHid");
        String enterpriseRegisterNo = param("enterpriseRegisterNo");
        String organizationCode = param("organizationCode");
        Timestamp enterpriseRegisterDate = (Timestamp) param("enterpriseRegisterDate", ReqParamType.TIMESTAMP);
        String enterpriseRegisterAddr = param("enterpriseRegisterAddr");

        if (StringUtils.isBlank(realName)) {
            json(L("请填写企业名称"), true, "");
            return null;
        }
        if (StringUtils.isBlank(legalPersonName)) {
            json(L("请填写法人"), true, "");
            return null;
        }
        if (StringUtils.isBlank(enterpriseRegisterNo)) {
            json(L("请填写企业注册号"), true, "");
            return null;
        }
        if (StringUtils.isBlank(organizationCode)) {
            json(L("请输入组织机构代码"), true, "");
            return null;
        }
        if (null == enterpriseRegisterDate) {
            json(L("请填写注册日期"), true, "");
            return null;
        }
        if (StringUtils.isBlank(enterpriseRegisterAddr)) {
            json(L("请填写注册地址"), true, "");
            return null;
        }


        Query q1 = auDao.getQuery().filter("enterpriseRegisterNo", enterpriseRegisterNo);
        long count = q1.countAll();
        if (count >= Const.MAX_AUTH_AMOUNT) {
            json(L("您的证件申请实名认证次数超过限制，无法通过"), false, "");
            return null;
        }

        String ip = ip();
        Authentication au = new Authentication(auDao.getDatastore());
        au.setType(AuditType.corporate.getKey());
        au.setRealName(realName);
        au.setLegalPersonName(legalPersonName);
        au.setEnterpriseRegisterNo(enterpriseRegisterNo);
        au.setOrganizationCode(organizationCode);
        au.setEnterpriseRegisterDate(enterpriseRegisterDate);
        au.setEnterpriseRegisterAddr(enterpriseRegisterAddr);
        //au.setAreaInfo(area);
        au.setIp(ip);
        au.setSubmitTime(now());
        au.setUserId(userId);
        //au.setCountryCode(country);

        au.setServiceStatu(3);// 比对一致
        au.setImgCode("");
        au.setPhoto("");
        au.setSimplePass(true);
        au.setAuthType(2);

        AuthenLogDao logDao = new AuthenLogDao();
        logDao.insertOneRecord(userId, "0", "企业用户：通过初级认证。", ip, now());

//			au.setStatus(isDalu?AuditStatus.a1Pass.getKey():AuditStatus.a1NoAudite.getKey());
        au.setStatus(AuditStatus.a1Pass.getKey());

        return au;
    }


//    @Page(Viewer = "/cn/manage/auth/switchAuth.jsp")
    public void switchAuth() {
        try {
            initLoginUser();
            setAttr("curUser", loginUser);

            int category = intParam("category");
            int type = intParam("type");

            setAttr("category", category);
            setAttr("type", type);
            UserContact uc = loginUser.getUserContact();
            setAttr("hasGoogleAtuh", uc.getGoogleAu() == 2);
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    /*
    * 提现地址验证
    * */
    @Page(Viewer = JSON)
    public void changeAuth() {
        try {
            initLoginUser();

            boolean dealVal = false;
            String des = "";

            int category = intParam("category");
            int type = intParam("type");

            UserContact uc = loginUser.getUserContact();
            if (category == 2) {
                if (type != TradeAuthenType.TRADE_PASSWORD.getKey()) { // 验证交易密码
                    String safePwd = param("safePwd");
                    if (!safePwdNew(safePwd, loginUser.getId(), JSON, false)) {
                        return;
                    }
                }
            } else if (category != 4) { //提现地址校验模式切换不需要短信/邮箱和Google验证码
                String mobileCode = param("mobileCode");
                String userIp = ip();

                boolean hasGoogleAtuh = uc.getGoogleAu() == 2;
                if (hasGoogleAtuh) {
                    long googleCode = CommonUtil.stringToLong(param("googleCode"));
                    if (!isCorrect(uc.getSecret(), googleCode, JSON)) {
                        return;
                    }
                }

                // 检查短信验证码
                String codeRecvAddr = uc.getSafeMobile();
                if (StringUtils.isBlank(codeRecvAddr)) {
                    codeRecvAddr = uc.getSafeEmail();
                }
                ClientSession clientSession = new ClientSession(userIp, codeRecvAddr, lan, PostCodeType.safeAuth.getValue(), false);
                DataResponse dr = clientSession.checkCode(mobileCode);
                if (!dr.isSuc()) {
                    json(L(dr.getDes()), false, "");
                    return;
                }
            }

            Message msg = null;
            switch (category) {
                case 1:        // 登录验证
                    msg = userDao.switchLoginAuthen(loginUser, type, ip(), request);
                    break;
                case 2:        // 交易验证
                    msg = userDao.switchTradeAuthen(loginUser, type, ip(), request);
                    break;
                case 3:        // 提现验证
                    msg = userDao.switchWithdrawAuthen(loginUser, type, ip(), request);
                    break;
                case 4:        // 提现地址验证
                    msg = userDao.switchWithdrawAddressAuthen(loginUser, type, ip(), request);
                    break;
                default:
                    json(L("设置失败：没有此选项。"), false, "");
                    return;
            }

            json(L(msg.getMsg()), msg.isSuc(), "");
        } catch (Exception e) {
            log.error("10100303VIPTXDZYZ【提现地址验证】 com.world.controller.manage.auth.index#changeAuth",e);
            json(L("操作失败"), false, "");
        }
    }

//    @Page(Viewer = "/cn/manage/index.jsp", des = "身份认证")
    public void authentication() {
        Authentication auth = auDao.getByUserId(userIdStr());
        int authStatus = AuditStatus.a1NoSubmit.getKey();
        String reason = "";
        Boolean isLock = false;
        Boolean isBlack = false;
        int lockState = 0;
        if (null != auth) {
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
            setAttr("lockTime", auth.getLockTime());
        }
        setAttr("reason", reason);
        setAttr("isLock", isLock);
        setAttr("isBlack", isBlack);
        setAttr("authStatus", authStatus);
    }

    /**
     * 配合前端改造
     */
    @Page(Viewer = JSON)
    public void authenticationJson() {
        Map<String, Object> result = new HashMap<>();

        Authentication auth = auDao.getByUserId(userIdStr());
        int authStatus = AuditStatus.a1NoSubmit.getKey();
        String reason = "";
        Boolean isLock = false;
        Boolean isBlack = false;
        int lockState;
        if (null != auth) {
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
        }
        result.put("reason", reason);
        result.put("isLock", isLock);
        result.put("isBlack", isBlack);
        result.put("authStatus", authStatus);
        //增加商家认证的状态
        Map<String,String> map = getStoreStatus(userIdStr());
        if(map != null && 0 < map.size()){
            String storeStatus = map.get("storeStatus");
            if(storeStatus.equals("-1")){//用户可进行商家认证
                result.put("storeStatus", -1);
            }else{
                result.put("storeStatus", Integer.valueOf(storeStatus));
                result.put("storeType", map.get("storeType"));
                if(map.get("storeType").equals("1")){
                    result.put("storeReason", L(StoreApplyRefuseReasonEnum.getName(Integer.valueOf(map.get("storeReason")))));
                }else if(map.get("storeType").equals("2")){
                    result.put("storeReason", L(StoreCancelRefuseReasonEnum.getName(Integer.valueOf(map.get("storeReason")))));
                }

            }
        }
        json("ok", true, JSONObject.toJSONString(result), true);
    }





    public Map<String,String> getStoreStatus(String userId){
        String key = Const.STORE_KEY + userId;
        Map<String,String> storeMap = RedisUtil.hget(key);
        try {
            if(storeMap != null && 0 < storeMap.size()){
                return storeMap;
            }else{
                storeMap = getStoreStatusFromMysql(userId);
            }
        }catch (Exception e){
            log.error("获取缓存中用户商家状态失败",e);

        }
        return storeMap;


    }


    public Map<String,String> getStoreStatusFromMysql(String userId){
        String key = Const.STORE_KEY + userId;
        Map<String,String> map = new HashMap<>();
        String sql = "select user_id as userId,type,status,reason from store_authentication where user_id = ? order by id desc limit 1";
        try {
            StoreAuthentication storeAuthentication = (StoreAuthentication) Data.GetOne("messi_otc",sql, new Object[]{userId},StoreAuthentication.class);
            if(storeAuthentication != null){
                map.put("storeStatus",storeAuthentication.getStatus().toString());
                map.put("storeType",storeAuthentication.getType().toString());
                map.put("storeReason",storeAuthentication.getReason().toString());
                RedisUtil.hset(key,"storeStatus",storeAuthentication.getStatus().toString(),0);
                RedisUtil.hset(key,"storeType",storeAuthentication.getType().toString(),0);
                RedisUtil.hset(key,"storeReason",storeAuthentication.getReason().toString(),0);
            }else{
                map.put("storeStatus","-1");//未申请
                RedisUtil.hset(key,"storeStatus","-1",0);
            }
        }catch (Exception e){
            log.error("获取数据库中用户商家状态失败",e);
            map = null;
        }
        return map;
    }





//    @Page(Viewer = "/cn/manage/index.jsp", des = "认证类型")
    public void authtype() {
        CountryDao cDao = new CountryDao();
        try {
            Query q = cDao.getQuery().order("code");
            List<Country> countrylist = q.asList();
            String userIdStr = userIdStr();
            User user = userDao.getById(userIdStr);
            setAttr("country", countrylist);
            //根据代码(code)获取国家（des）
            Authentication auth = auDao.getByUserId(userIdStr());
            int authStatus = AuditStatus.a1NoSubmit.getKey();
            String reason = "";
            if (null != auth) {
                authStatus = auth.getStatus();
                reason = auth.getReason();
            }
            if (authStatus == AuditStatus.a1NoSubmit.getKey() || authStatus == AuditStatus.a1NoPass.getKey() || authStatus == 0) {
                setAttr("reason", reason);
                setAttr("authStatus", authStatus);
                for (Country country : countrylist) {
                    if (null != user.getUserContact().getmCode()) {
                        if (country.getCode().equals(user.getUserContact().getmCode())) {
                            setAttr("countryCode", country.getCode());
                        } else {
                            setAttr("countryCode", "+86");
                        }
                    }
                }
                setAttr("auth", auth);
            } else {
                response.sendRedirect(VIP_DOMAIN);
            }
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    /**
     * 配合前端改造
     */
    @Page(Viewer = JSON)
    public void authTypeJson() {
        Map<String, Object> result = new HashMap<>();

        CountryDao cDao = new CountryDao();
        try {
            Query q = cDao.getQuery().order("code");
            List<Country> countrylist = q.asList();
            String userIdStr = userIdStr();
            User user = userDao.getById(userIdStr);
            result.put("country", countrylist);
            //根据代码(code)获取国家（des）
            Authentication auth = auDao.getByUserId(userIdStr());
            int authStatus = AuditStatus.a1NoSubmit.getKey();
            String reason = "";
            if (null != auth) {
                authStatus = auth.getStatus();
                reason = auth.getReason();
            }
            if (authStatus == AuditStatus.a1NoSubmit.getKey() || authStatus == AuditStatus.a1NoPass.getKey() || authStatus == 0) {
                setAttr("reason", reason);
                setAttr("authStatus", authStatus);
                for (Country country : countrylist) {
                    if (null != user.getUserContact().getmCode()) {
                        if (country.getCode().equals(user.getUserContact().getmCode())) {
                            result.put("countryCode", country.getCode());
                        } else {
                            result.put("countryCode", "+86");
                        }
                    }
                }
                result.put("auth", auth);
            } else {
                result.put("redirect", VIP_DOMAIN);
            }

            json("success", true, com.alibaba.fastjson.JSONObject.toJSONString(result), true);
        } catch (Exception e) {
            log.error("内部异常", e);
        }


    }

//    @Page(Viewer = "/cn/manage/index.jsp", des = "身份证认证")
    public void idcardauth() {
        String idCard = param("idCard");
        String firstName = param("firstName");
        String lastName = param("lastName");
        String idCardFaceImg = param("idCardFaceImg");
        String idCardBackImg = param("idCardBackImg");


        if (!StringUtils.isBlank(idCardFaceImg)) {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] b = new byte[0];
            try {
                b = decoder.decodeBuffer(idCardFaceImg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            String path = QiNiuUtil.uploadFile(b, "222.jpg", QiNiuUtil.getUpToken());
            path = path + "222.jpg";

        }
    }


//    @Page(Viewer = "/cn/manage/index.jsp", des = "护照认证")
    public void passportauth() {

    }

    @Page(Viewer = JSON)
    public void imgUpload() {


    }


    @Page(Viewer = JSON)
    public void uploadImg() {
        String img = param("img");
        String type = param("type");
        String userId = userIdStr();
        String imgPath = "";
        try {
            if (StringUtils.isBlank(userId)) {
                json(L("图片上传失败。"), false, "");
                return;
            } else {
                if (!StringUtils.isBlank(img)) {
                    BASE64Decoder decoder = new BASE64Decoder();
                    byte[] b = new byte[0];
                    try {
                        b = decoder.decodeBuffer(img);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 处理数据
                    for (int i = 0; i < b.length; ++i) {
                        if (b[i] < 0) {
                            b[i] += 256;
                        }
                    }
                    String imgName = UUID.randomUUID().toString() + ".jpg";
                    imgPath = QiNiuUtil.uploadFile(b, imgName, QiNiuUtil.getUpToken());
                    if (!StringUtils.isBlank(imgPath)) {
                        imgPath = imgPath + imgName;
                    }
                }
                if (!StringUtils.isBlank(imgPath)) {
                    Authentication au = new Authentication(auDao.getDatastore());
                    au.setUserId(userId);
                    if ("1".equals(type)) {
                        au.setFrontalImg(imgPath);
                        au.setCardType("1");
                    } else if ("2".equals(type)) {
                        au.setBackImg(imgPath);
                        au.setCardType("1");
                    } else if ("3".equals(type)) {
                        au.setLoadImg(imgPath);
                        au.setCardType("1");
                    } else if ("4".equals(type)) {
                        au.setFrontalImg(imgPath);
                        au.setCardType("2");
                    } else if ("5".equals(type)) {
                        au.setLoadImg(imgPath);
                        au.setCardType("2");
                    }

                    if (!auDao.updateAuthImg(au).getHadError()) {
                        json(L("图片上传成功，请耐心等待审核。"), true, "");
                        return;

                    } else {
                        json(L("图片上传失败。"), false, "");
                        return;
                    }

                } else {
                    json(L("图片上传失败。"), false, "");
                    return;
                }
            }

        } catch (Exception e) {
            log.error("内部异常", e);
            json(L("图片上传失败。"), false, "");
            return;
        }
    }


    @Page(Viewer = JSON)
    public void uploadToken() {
        JSONObject js = new JSONObject();
        String token = QcloudCosUtil.getPostToken(QcloudCosUtil.getDefaultExpiredMills());
        if (StringUtils.isBlank(token)) {
            json(L("图片上传失败。"), false, "");
            return;
        } else {
            js.put("host", QcloudCosUtil.getHost());
            js.put("token", token);
            js.put("key", QcloudCosUtil.getUploadDir() + DateFormatUtils.format(new Date(), "yyyyMMdd")  + "/AUTH/" + DigestUtils.md5Hex(UUID.randomUUID().toString()));
            json(L("图片上传成功，请耐心等待审核。"), true, js.toJSONString());
            return;
        }
    }

    /**
     * 新增代办任务
     *
     * @param AgencyCode
     * @param taskId
     * @param todoName
     * @param todoNodeName
     * @param userId
     * @param userName
     * @param url
     * @return
     */
    public int addTdTodotask(String AgencyCode, int taskId, String todoName, String todoNodeName, String userId, String userName, String url){
        String busid = getBusId(AgencyCode);
        int state = tdTodotaskDao.addTdTodotask(busid,taskId,todoName,todoNodeName,userId,userName,url);
        return state;
    }

    /**
     * 获取单号
     *
     * @param AgencyCode
     * @return
     */
    private String getBusId(String AgencyCode) {
        String busId = "";
        String busIdMax = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String busIdLike = AgencyCode.concat(sdf.format(new Date()));
        if(org.apache.commons.lang3.StringUtils.isNotBlank(busIdLike)){
            busIdMax = tdTodotaskDao.getMaxBusId(busIdLike);
        }
        busId = getCommonNm(busIdLike,AgencyCode,busIdMax);
        return busId;
    }

    /**
     * 获取单号
     *
     * @param assembleStr 组合串
     * @param type 待办编码：GO,SJ,SF
     * @param maxNm 今日最大单号
     * @return
     */
    public static String getCommonNm(String assembleStr,String type ,String maxNm){
        String nmSeq = "";
        if(org.apache.commons.lang3.StringUtils.isBlank(maxNm)){
            nmSeq = assembleStr.concat("000001");
        }else{
            String currentDateStr = maxNm.substring(0, 10);
            String nmStr = maxNm.substring(maxNm.length()-5,maxNm.length());
            Integer smInt = Integer.valueOf(nmStr) + 1;
            String nm = String.format("%06d", smInt);
            nmSeq = currentDateStr.concat(nm);
        }
        return nmSeq;
    }

    /**
     * otcweb 保存昵称生成图片保存
     */
    @Page(Viewer = JSON)
    public void saveWebNickName() throws IOException {
        setLan();
        initLoginUser();
        String userId = userIdStr();
        String nickname = param("nickname");
        String token = param("token");
        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()),"");
            return;
        }
        User user = userDao.findOne("_id",userId);
        if (null == user) {
            json(SystemCode.code_3004, L(SystemCode.code_3004.getValue()),"");
            return;
        }
        //判断昵称是否含有关键字眼
        List<String> limitList = Arrays.asList(LIMIT_KEYWORD.split(","));
        if(!org.springframework.util.CollectionUtils.isEmpty(limitList)){
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
        File file=CreateNamePictureUtil.generateImg(nickname,"头像");
       String url= QcloudCosUtil.upload(file,file.getName().substring(file.getName().lastIndexOf(".")));
        Query<User> q = ds.find(User.class, "_id", userId);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
        ops.set("nickname", nickname);
        ops.set("nick", nickname);
        ops.set("photo",url);
        UpdateResults<User> ur = userDao.update(q, ops);
        if (!ur.getHadError()) {
            json(SystemCode.code_1000, L("设置成功"),"");
            return;
        } else {
            json(SystemCode.code_1001, L("失败"),"");
            return;
        }
    }

    public static void main(String[] args) throws IOException {
        UserDao userDao = new UserDao("cn");
        Datastore ds = userDao.getDatastore();
        Query<User> q = ds.find(User.class, "_id", 00001);
        File file=CreateNamePictureUtil.generateImg("li","头像");
        String url= QcloudCosUtil.upload(file,file.getName().substring(file.getName().lastIndexOf(".")));
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
        ops.set("nickname", "li");
        ops.set("nick", "li");
        ops.set("photoUrl",url);
        UpdateResults<User> ur = userDao.update(q, ops);
    }

}
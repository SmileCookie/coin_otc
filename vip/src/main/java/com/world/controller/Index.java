package com.world.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;

import javax.servlet.http.Cookie;

import com.api.config.ApiConfig;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.UserApiService;
import com.messi.user.vo.AuthenticationVo;
import com.messi.user.vo.R;
import com.world.constant.Const;
import com.world.controller.api.util.SystemCode;
import com.world.model.dao.financialproift.FinDouProfitLogDao;
import com.world.model.dao.user.authen.AuthenLogDao;
import com.world.model.dao.user.authen.AuthenticationDao;
import com.world.model.entity.*;
import com.world.model.entity.financialproift.FinDouProfitLog;
import com.world.model.entity.user.authen.AuthenType;
import com.world.model.entity.user.authen.Authentication;
import com.world.util.*;
import com.world.util.date.TimeUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.Lan;
import com.alibaba.fastjson.JSONObject;
import com.kafka.ProducerSend;
import com.redis.RedisUtil;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.Query;
import com.world.model.dao.BannerPhotoDao;
import com.world.model.dao.BannerRelationDao;
import com.world.model.dao.FundsintroductionDao;
import com.world.model.dao.financialproift.FinancialSuperNodeDao;
import com.world.model.dao.friendurl.FriendUrlDao;
import com.world.model.dao.jifen.FuncJumpDao;
import com.world.model.dao.level.IntegralRuleDao;
import com.world.model.dao.level.UserVipLevelDao;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.msg.NewsDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.seo.SeoDao;
import com.world.model.dao.user.EmailDao;
import com.world.model.dao.user.MobileDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.financialproift.FinancialProduct;
import com.world.model.entity.financialproift.SuperNode;
import com.world.model.entity.level.IntegralRule;
import com.world.model.entity.level.UserVipLevel;
import com.world.model.entity.msg.News;
import com.world.model.entity.seo.Seo;
import com.world.model.entity.user.User;
import com.world.model.enums.BonusEnum;
import com.world.util.cookie.CookieUtil;
import com.world.util.ip.IpUtil;
import com.world.util.string.MD5;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import com.world.web.response.DataResponse;
import com.world.web.sso.session.ClientSession;
import com.world.web.sso.session.Session;
import com.world.web.sso.session.SsoSessionManager;
import com.yc.util.MsgUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Index extends BaseAction {

    private static final long serialVersionUID = 1L;
    private static final String keyPre = "superNode_";
    PayUserDao payDao = new PayUserDao();
    SeoDao seoDao = new SeoDao();
    FuncJumpDao funcJumpDao = new FuncJumpDao();
    FundsintroductionDao fundsinDao = new FundsintroductionDao();
    IntegralRuleDao integralRuleDao = new IntegralRuleDao();
    FinancialSuperNodeDao financialSuperNodeDao = new FinancialSuperNodeDao();
    AuthenticationDao auDao = new AuthenticationDao();
    AuthenLogDao logDao = new AuthenLogDao();

    /***
     * 首页
     * @throws IOException
     */
//	@Page(Viewer = "/cn/room.jsp" , Cache = 60)
//	public void index() {
//		setAttr("coint", coint);
//		long currentTime = System.currentTimeMillis();
//		setAttr("currentime", currentTime);
//		boolean isSale = false;
//		setAttr("isSale", isSale);
//		Timestamp now = new Timestamp(System.currentTimeMillis());
//		NewsDao nd = new NewsDao();
//		List<News> rds = nd.findPage(nd.getQuery(News.class).filter("type <>", 1).filter("type <>", 2).order("-isTop,-topTime,-pubTime"), 1, 6);
//
//		//增加发布时间判断和置顶排序 zhanglinbo 20170210
//		List<News> ggs = nd.findPage(nd.getQuery(News.class).filter("type", 1).filter("pubTime <=", now).order("-isTop,-topTime,-pubTime"), 1, 6);
//		List<News> news4seo = nd.findPage(nd.getQuery(News.class).filter("type", 2).filter("pubTime <=", now).order("-isTop,-topTime,-pubTime"), 1, 6);
//
//		setAttr("ggs", ggs);
//		setAttr("rds", rds);
//		setAttr("news4seo", news4seo);
//
//	}
    @Page()
    public void index() {
        setAttr("coint", coint);
        long currentTime = System.currentTimeMillis();
        setAttr("currentime", currentTime);
        boolean isSale = false;
        setAttr("isSale", isSale);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        NewsDao nd = new NewsDao();
        List<News> rds = nd.findPage(nd.getQuery(News.class).filter("type <>", 1).filter("type <>", 2).order("-isTop,-topTime,-pubTime"), 1, 6);

        //增加发布时间判断和置顶排序 zhanglinbo 20170210
        List<News> ggs = nd.findPage(nd.getQuery(News.class).filter("type", 1).filter("pubTime <=", now).order("-isTop,-topTime,-pubTime"), 1, 6);
        List<News> news4seo = nd.findPage(nd.getQuery(News.class).filter("type", 2).filter("pubTime <=", now).order("-isTop,-topTime,-pubTime"), 1, 6);
        List<News> newTitles = nd.findPage(nd.getQuery(News.class).filter("language", lan).filter("type", 1).filter("pubTime <=", now).order("-isTop,-topTime,-pubTime"), 1, 6);
        setAttr("ggs", ggs);
        setAttr("rds", rds);
        setAttr("news4seo", news4seo);
        if (newTitles != null && newTitles.size() > 0 && newTitles.get(0) != null) {
            setAttr("newTitle", newTitles.get(0).getTitle());
            setAttr("newTitleId", newTitles.get(0).getId());
        } else {
            setAttr("newTitle", "");
            setAttr("newTitleId", "");
        }

        String ip = IpUtil.getIp(request);
        CookieUtil cookieUtil = new CookieUtil(request, response);
        String currencyCur = cookieUtil.getCookieValue("currency");
        if (!StringUtil.exist(currencyCur)) {
            String currency = "USD";
            String paramCurrency = param("currency");
            //国家
            String province = "";
            if (!StringUtil.exist(paramCurrency)) {
                //是否是外网IP
                if (IpUtil.isIpv4(ip) && IpUtil.isPublicIp(ip)) {
                    if (StringUtils.isNotBlank(Cache.Get("ip_currency_" + ip))) {
                        currency = Cache.Get("ip_currency_" + ip);
                    } else {
                        province = IpUtil.getProvinceBySina(ip);
                        if ("中国".equals(province)) {
                            currency = "CNY";
                        } else {
                            currency = "USD";
                        }
                        Cache.Set("ip_currency_" + ip, currency, 5 * 60);
                    }
                }
            } else {
                currency = paramCurrency.toUpperCase();
            }
            Cookie userCurrency = new Cookie("currency", currency);
            userCurrency.setMaxAge(60 * 60 * 2);// s为单位，1个月60*60*24,存储一天
            userCurrency.setDomain(Session.SETDOMAIN);
            userCurrency.setPath("/");
            response.addCookie(userCurrency);
        }


        String lanCur = cookieUtil.getCookieValue(SsoSessionManager.LANGUAGE);
        if (!StringUtil.exist(lanCur)) {
            try {
                String curLan = "en";
                /* TODO 目前只支持英文，如果国际化，需要打开这个配置 add by buxianguan 20190306
                //国家
                String province = "";
                //是否是外网IP
                if (IpUtil.isIpv4(ip) && IpUtil.isPublicIp(ip)) {
                    if (StringUtils.isNotBlank(Cache.Get("ip_" + ip))) {
                        curLan = Cache.Get("ip_" + ip);
                    } else {
                        province = IpUtil.getProvinceBySina(ip);
                        if ("中国".equals(province)) {
                            curLan = "cn";
                        } else if ("香港".equals(province) || "澳门".equals(province) || "台湾".equals(province)) {
                            curLan = "hk";
                        } else {
                            curLan = "en";
                        }
                        Cache.Set("ip_" + ip, curLan, 5 * 60);
                    }
                }*/
                setAttr("lan", curLan);
                Cookie lanCookie = new Cookie(SsoSessionManager.LANGUAGE, curLan);
                lanCookie.setHttpOnly(false);
                //失效时间7天
                lanCookie.setMaxAge(60 * 60 * 24 * 7);
                lanCookie.setPath("/");
                lanCookie.setDomain(GlobalConfig.baseDomain);
                response.addCookie(lanCookie);
            } catch (Exception e) {
                log.error("VIP设置语言失败，失败信息为：", e);
            }
        }

        /*modify by xwz 应前端要求，去掉国际化*/

        String url = "/cn/room.jsp";
        String isMoblile = request.getHeader("Host");
        if (StringUtils.isNotEmpty(isMoblile)) {
            if (isMoblile.startsWith("m")) {
                url = "/cn/roommb.jsp";
            }
        }
//        boolean isMobile = IsMobile.check(request);
//        boolean isIpad = IsMobile.isIpad(request);
//
//        String url = (isMobile && !isIpad) ? "/cn/roommb.jsp" : "/cn/room.jsp";

        try {
            ToJsp(url);
            return;
        } catch (Exception e) {
            log.error("内部异常", e);
        }
        /*end*/
//		String url = "/cn/room";
//		try{
//			if(lan.equals("cn")){
//				url = url + ".jsp";
//			}else if("tw".equals(lan) || "hk".equals(lan)){
//				url = url + "_hk.jsp";
//			}else if("en".equals(lan)){
//				url = url + "_en.jsp";
//			}else{
//				url = url + ".jsp";
//			}
////			request.getRequestDispatcher(url).forward(request,response);
//
//			ToJsp(url);
//			return;
//		}catch(Exception e) {
//			log.error("内部异常", e);
//		}
    }

    /**
     * 设置语言类型
     */
    @Page(Viewer = JSON)
    public void setLan() {
        String lan = param("lan");
        if (!lan.equals("en") && !lan.equals("hk") && !lan.equals("tw") && !lan.equals("es") && !lan.equals("jp") && !lan.equals("kr")) {
            lan = "cn";
        }
        Session.addCookie(SsoSessionManager.LANGUAGE, lan, 60 * 60 * 24 * 7, false, false, this);
        String userId = userIdStr();
        try {
            if (StringUtils.isNotBlank(userId)) {
                userDao.updateUserLanguage(userId, lan);
                Cache.Set("user_lan_" + userId, lan);
            }
        } catch (Exception e) {
            log.error("修改用户语言出错userId:" + userId + ",lan" + lan, e);
        }
        setAttr("lan", lan);
        json("success", true, "", true);
    }

    /**
     * 低于IE9版本的IE浏览器跳出页面
     *
     * @date 2015-7-11
     */
    @Page(Viewer = "/cn/lowerIe8Error.jsp")
    public void warning() {

    }


    @Page(Viewer = "/cn/trade/kline.jsp")
    public void kline() {
        setAttr("tradesLimit", 100);
        String symbol = param("symbol");
        setAttr("symbol", symbol);
        //获取所有市场
        super.setAttr("markets", Market.getMarketsMap());
    }


    private UserDao userDao = new UserDao();

    @Page(Viewer = JSON)
    public void userSendCode() {
        try {
            boolean graphicalCode = true;//是否有图形验证码
            int icodeType = intParam("codeType");//验证码类型
            String userId = userIdStr();//用户ID
            String userName = param("userName");//用户姓名
            int type = 1;// 发送类型 1：手机  2：邮件
            String sendType = param("type");//1：邮件， 为空则短信
            //国家码
            String mCode = param("selectedCode");
            String mobile = param("mobile");//手机号
//            if(StringUtils.isEmpty(sendType)){
//                if(StringUtils.isEmpty(mobile)){
//                    json(L("请输入手机号"), false, "");
//                    return;
//                }
//            }
            User user = null;

            if (StringUtils.isNotEmpty(userName)) {
                user = userDao.getByField("userName", userName.toLowerCase());
            } else {
                user = userDao.getById(userId);
            }
            if (null == user) {
                if (icodeType == 16) {
                    String errorMsg = "";
                    errorMsg = CommonUtil.mapToJsonStr(errorMsg, "email", L("请输入正确的电子邮件地址。"));
                    json("", false, errorMsg);
                    return;
                } else {
                    json(L("用户不存在，请重新输入"), false, "");
                    return;
                }
            }else{
                /*Start by guankaili 20190807 忘记密码如果没激活跳转到验证链接 */
                if(icodeType == PostCodeType.resetPassword.getKey() && StringUtils.isBlank(user.getUserContact().getSafeEmail())){
                    Map map = new HashMap();
//                    map.put("nid",user.getId());
                    map.put("isConfirm",false);
                    json("", false, JSONObject.toJSONString(map));
                    return;
                }
                /*End*/

                if(icodeType == PostCodeType.setMobile.getKey() || icodeType == PostCodeType.updMobile.getKey() || icodeType == PostCodeType.openMobileVerify.getKey()){
                    //录入手机和绑定手机和没绑定手机开启二次验证要取前端传递的参数
                    User userMobile = null;
                    //绑定手机号时验证该手机号是否已存在
                    if(StringUtils.isNotEmpty(mobile)){
                        userMobile = userDao.getUserInfo( mCode + " " + mobile,user.getId());
                    }
                    if(userMobile != null){
                        json(L("请输入正确的手机号码。"), false, "");
                        return;
                    }
                }
            }

            PostCodeType postCodeType = (PostCodeType) EnumUtils.getEnumByKey(icodeType, PostCodeType.class);
            if (null == postCodeType) {
                json(L("参数错误"), false, "");
                return;
            }
            String codeType = postCodeType.getValue();

            String currency = param("currency");
            if (currency.length() > 0) {
                currency = currency.toUpperCase();
            }
            if (postCodeType.equals(PostCodeType.cash)) {
                if (userDao.checkPhoneLock(userId)) {
                    json(L("您的账号修改了手机，暂时无法发送提现短信，请24小时后重试。"), false, "");
                    return;
                }
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

            String sendAddr = user.getUserContact().getSafeMobile();

            if ((null == sendAddr || "".equals(sendAddr)) && StringUtils.isEmpty(mobile)) {
                String email = user.getUserContact().getSafeEmail();
                if (null != email && !"".equals(email)) {
                    type = 2;
                    sendAddr = email;
                } else {
                    json(L("needMobileAuth"), false, "");
                    return;
                }
            } else {
                if (StringUtils.isNotEmpty(mobile)) {
                    sendAddr = mCode + " " + mobile;
                }
            }
            if (StringUtils.isNotEmpty(sendType)) {
                type = 2;
                sendAddr = user.getEmail();
            }
            String ip = ip();

            ClientSession clientSession = new ClientSession(ip, sendAddr, lan, codeType, graphicalCode);
            clientSession.rs = resoureRequest;

            DataResponse dr = clientSession.checkSend();//检测当前客户端是否能够发送

            if (!dr.isSuc() && !dr.getDes().equals(L("请输入图形验证码！"))) {
                json(dr.getDes(), false, "");
                return;
            }//测试，暂时不验证这步

            //当前ip验证是否注册过的所有手机号码24h不得超过x个
            clientSession.addCheckNumber();

            String radomCode = MobileDao.GetRadomStr(type);
            if (type == 1) {
                //校验手机号是否正确
                if (!CheckRegex.isPhoneNumber(sendAddr)) {
                    String msg = CommonUtil.mapToJsonStr("", MsgToastKey.mobile, L("请输入正确的手机号码。"));
                    json("", false, msg);
//                    json(L("请输入正确的手机号码。"), false, "");
                    return;
                }
                MobileDao mDao = new MobileDao();
                /*start by xzhang 20171031 短信服务临时解决方法，除+86外全发英文*/
                String title = L(codeType, currency);
                String content = String.format(L(postCodeType.getDes(), currency), radomCode);
                //去掉该逻辑，所有都按照用户选择语言发送 modify by buxianguan 20190805
//                if (!MsgUtil.isContain(sendAddr)) {
//                    title = Lan.LanguageFormat("en", codeType, currency);
//                    content = String.format(Lan.LanguageFormat("en", postCodeType.getDes(), currency), radomCode);
//                }
                /*end*/
                if (mDao.sendSms(user, ip(), title, content, sendAddr)) {
                    if (icodeType == PostCodeType.logVIP.getKey()) {
                        //登录锁定sessoin有效时间为2个小时
                        if (clientSession.sendCodeWithTime(radomCode, 2 * 60 * 60 * 1000)) {
                            log.info(radomCode + ">>>" + String.format(L(postCodeType.getDes(), currency), radomCode));
                            json(L("短信验证码已发送到您的手机，10分钟内有效"), true, "");
                            return;
                        }
                    } else {
                        if (clientSession.sendCode(radomCode)) {
                            log.info(radomCode + ">>>" + String.format(L(postCodeType.getDes(), currency), radomCode));
                            json(L("短信验证码已发送到您的手机，10分钟内有效"), true, "");
                            return;
                        }
                    }
                }
            } else {
                EmailDao eDao = new EmailDao();
                String content = L(postCodeType.getEmailInfo(), currency);
                String info = eDao.getCodeEmailHtmlByInfo(user, content, radomCode, this);
                String title = L(postCodeType.getEmailTitle(), currency);
                log.info("发送邮箱验证码，title:" + title);
//                String title = L(SysGroups.vip.getValue()) + " " + L("邮箱验证码");
                int iResult = eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, sendAddr);
                if (iResult == 1) {
                    if (clientSession.sendCode(radomCode)) {
                        log.info(radomCode);
                        json(L("验证码已发送到您的邮箱%%，请登录邮箱查看，10分钟内有效。", UserUtil.shortEmail(sendAddr)), true, "");
                        return;
                    }
                } else if (iResult == 2) {
                    json(L("您今天发送的验证码已超过限制。"), false, "");
                    return;
                } else {
                    json(L("发送失败，请稍后重试"), false, "");
                    return;
                }

            }
        } catch (Exception e) {
            log.error("内部异常", e);
        }
        json(L("系统出错，请稍后..."), false, "");
    }

    /**
     * newrelic监听端口,判断服务是否存活
     */
    @Page(Viewer = JSON)
    public void heartbeat() {
        json("heart-beat", true, "{\"status\":\"alive\"}");
    }

    @Page(Viewer = JSON)
    public void fileaction() {
        int i = 1 + 1;
        log.info("------------------------------------------------------------------------");
    }

    private static final String POINTEXCHANGENUMCACHE = "pointExchangeNumCache";    //缓存兑换数量

    //	@Page(Viewer = JSON,Cache = 60)
    public void getCurrentExchangeInfo() {
        long currentTime = System.currentTimeMillis();//当前时间
        long startTime = 1501214400000l;    //2017-07-28 12:00:00
        long endTime = 1502078400000l;      //2017-08-07 12:00:00
        BigDecimal totalNum = new BigDecimal("10000000");     //发行总量
        BigDecimal surplusNum = new BigDecimal("10000000");    //剩余总量
        BigDecimal rate = BigDecimal.ZERO;
//		BigDecimal currentExchangeNum = BigDecimal.ZERO;     //当前申购总量
        Map<String, Object> returnMap = new HashMap<>();
//      TODO: 2017/7/22 兑换结束时间要不要判断
        if (currentTime >= startTime) {
            if (currentTime > endTime) {
                currentTime = endTime;
            }
            BigDecimal pointExchangeNum = BigDecimal.ZERO;//点位兑换数量
            int point = (int) ((currentTime - startTime) / (15 * 60 * 1000));//取点位，五分钟一个点
            String getPointExchangeSql = "select IFNULL((curvedata),0) as curvedata from icocurvedata where id = ?";

            List<BigDecimal> pointExchangeList = (List<BigDecimal>) Data.GetOne(getPointExchangeSql, new Object[]{point});
            if (pointExchangeList != null) {
                pointExchangeNum = pointExchangeList.get(0).multiply(new BigDecimal("10000"));
                try {
                    String cacheExchangeNumStr = Cache.Get(POINTEXCHANGENUMCACHE);
                    if (StringUtils.isNotEmpty(cacheExchangeNumStr)) {
                        BigDecimal cacheExchangeNum = new BigDecimal(cacheExchangeNumStr);
                        if (cacheExchangeNum.compareTo(pointExchangeNum) >= 0) {//缓存兑换数数量大于数据库兑换数量
                            pointExchangeNum = cacheExchangeNum;
                        } else {
                            Cache.Set(POINTEXCHANGENUMCACHE, pointExchangeNum.toString());
                        }
                    } else {
                        Cache.Set(POINTEXCHANGENUMCACHE, pointExchangeNum.toString());
                    }
                } catch (Exception e) {
                    log.error("取兑换数量出错，错误信息" + e.toString());
                }
                //兑换进
                rate = pointExchangeNum.multiply(new BigDecimal("100")).divide(totalNum, 0, BigDecimal.ROUND_DOWN);
                if (rate.compareTo(BigDecimal.ONE) > 100) {   //当rate>100
                    rate = new BigDecimal("100");
                }
                surplusNum = surplusNum.subtract(pointExchangeNum);
                if (surplusNum.compareTo(BigDecimal.ZERO) < 0) {//当surplusNum小于0
                    surplusNum = BigDecimal.ZERO;
                }
                if (rate.compareTo(BigDecimal.ONE) < 1) {    //当rate<1
                    rate = BigDecimal.ONE;
                }
            }
            returnMap.put("totalNum", totalNum);
            returnMap.put("surplusNum", surplusNum);
            returnMap.put("rate", rate + "%");
        } else {
            returnMap.put("totalNum", totalNum);
            returnMap.put("surplusNum", surplusNum);
            returnMap.put("rate", "0%");
        }
        json("success", true, com.alibaba.fastjson.JSON.toJSONString(returnMap));
    }


    /**
     * 首页获取交易时间
     */
//	@Page(Viewer = JSON)
    public void getDealTime() {
        Map<String, Object> returnMap = new HashMap<>();
        long currentTime = System.currentTimeMillis();//当前时间
        returnMap.put("dealTime", "1502172000000");//8月8日14:00开启交易
        returnMap.put("currentTime", currentTime);
        json("success", true, com.alibaba.fastjson.JSON.toJSONString(returnMap));
    }


    /*start by xzhang 20171215 交易页面三期PRD:法币*/

    /**
     * 设置法币类型
     */
    @Page(Viewer = JSON)
    public void setCurrency() {
        String ip = IpUtil.getIp(request);
        String currency = "USD";
        String paramCurrency = param("currency");
        //国家
        String province = "";
        if (!StringUtil.exist(paramCurrency)) {
            //是否是外网IP
            if (IpUtil.isIpv4(ip) && IpUtil.isPublicIp(ip)) {
                if (StringUtils.isNotBlank(Cache.Get("ip_currency_" + ip))) {
                    currency = Cache.Get("ip_currency_" + ip);
                } else {
                    province = IpUtil.getProvinceBySina(ip);
                    if ("中国".equals(province)) {
                        currency = "CNY";
                    } else {
                        currency = "USD";
                    }
                    Cache.Set("ip_currency_" + ip, currency, 5 * 60);
                }
            }
        } else {
            currency = paramCurrency.toUpperCase();
        }
        try {
            Cookie userCurrency = new Cookie("currency", currency);
            userCurrency.setMaxAge(60 * 60 * 2);// s为单位，1个月60*60*24,存储一天
            userCurrency.setDomain(Session.SETDOMAIN);
            userCurrency.setPath("/");
            response.addCookie(userCurrency);
        } catch (Exception e) {
            log.info("Ip:" + ip + "恶意刷接口");
        }
        json("success", true, com.alibaba.fastjson.JSON.toJSONString(currency), true);
    }
    /*end*/


    /**
     * 返回友链列表
     */
    private static final String FRIENDURL = "friendurlCache";    //友链列表

    @Page(Viewer = JSON)
    public void getFriendUrl() {
        Query<FriendUrl> query = null;
        String friendurlCache = Cache.Get(FRIENDURL);
        if (StringUtils.isEmpty(friendurlCache)) {
            FriendUrlDao dao = new FriendUrlDao();
            query = dao.getQuery();
            query.setSql("select * from friendurl");
            query.setCls(FriendUrl.class);
            query.append(" and enableFlag = 0");
            query.append("order by orderNum asc, id asc");
            List<FriendUrl> friendUrls = query.getList();
            friendurlCache = com.alibaba.fastjson.JSON.toJSONString(friendUrls);
            /*Start by guankaili 20190403 优化查询逻辑 */
            Cache.Set(FRIENDURL, friendurlCache, 0);//永久
            /*End*/
        }

        json("success", true, friendurlCache);
    }


    @Page(Viewer = JSON)
    public void getPhotoUrl() {
        try {
//            String bannerGroup = param("bannerGroup");
            boolean isMobile = IsMobile.check(request);
            boolean isIpad = IsMobile.isIpad(request);

            String bannerGroup = (isMobile && !isIpad) ? "spotAPP" : "spotWeb";

            String bannerPhotoCache = "";
            if ("spotAPP".equals(bannerGroup)) {
                bannerPhotoCache = getBannerPhotoForApp(bannerGroup);
            } else {
                bannerPhotoCache = getBannerPhotoForWeb(bannerGroup);
            }
            if (StringUtils.isEmpty(bannerPhotoCache)) {
                json(L("Banner图片数据为空，请联系管理员。"), false, "");
                return;
            }
//            if (StringUtils.isEmpty(bannerPhotoCache) || "[]".equals(bannerPhotoCache)) {
//                if (StringUtils.isNotEmpty(bannerGroup)) {
//                    //获取关联图片数据ID
//                    BannerRelationDao bannerRelationDao = new BannerRelationDao();
//                    Query<BannerRelation> query = bannerRelationDao.getQuery();
//                    query.setSql("SELECT * FROM bannerrelation");
//                    query.setCls(BannerRelation.class);
//                    query.append("groupid =( SELECT id FROM bannergroup WHERE bannergroup = '" + bannerGroup + "')");
//                    List<BannerRelation> bannerRelation = query.getList();
//                    if (CollectionUtils.isEmpty(bannerRelation)){
//                        json(L("Banner图片数据为空，请联系管理员。"), false, "");
//                        return;
//                    }
//                    //查询图片明细  插入缓存
//                    BannerPhotoDao photo = new BannerPhotoDao();
//                    Query<BannerPhoto> queryPhoto = photo.getQuery();
//                    queryPhoto.setSql("select * from bannerPhoto");
//                    queryPhoto.setCls(BannerPhoto.class);
//                    String countSql = null;
//                    List<Integer> ids = new ArrayList<>();
//                    for (BannerRelation banner : bannerRelation){
//                        ids.add(banner.getPhotoid());
//                    }
//                    countSql = "id in ("+ids.toString().replaceAll("\\[|\\]", "")+")";
//                    queryPhoto.append(countSql);
//                    queryPhoto.append(" and `status` = 1");
//                    List<BannerPhoto> bannerPhotoList = queryPhoto.getList();
//                    if (bannerPhotoList == null || bannerPhotoList.size() < 1) {
//                        json(L("Banner图片数据为空，请联系管理员。"), false, "");
//                        return;
//                    }
//                    bannerPhotoCache = com.alibaba.fastjson.JSON.toJSONString(bannerPhotoList);
//                    Cache.Set("bannerPhoto", bannerPhotoCache, 60 * 60 * 1);
//                } else {
//                    log.error("Banner组 bannerGroup 不可为空！");
//                }
//            }
            json("success", true, bannerPhotoCache);
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    /**
     * 获取首页banner为web
     *
     * @param bannerGroup
     * @return
     */
    public String getBannerPhotoForWeb(String bannerGroup) {
        String bannerPhotoCache = Cache.Get("bannerPhotoWeb");
        if (StringUtils.isEmpty(bannerPhotoCache) || "[]".equals(bannerPhotoCache)) {
            if (StringUtils.isNotEmpty(bannerGroup)) {
                //获取关联图片数据ID
                BannerRelationDao bannerRelationDao = new BannerRelationDao();
                Query<BannerRelation> query = bannerRelationDao.getQuery();
                query.setSql("SELECT * FROM bannerrelation");
                query.setCls(BannerRelation.class);
                query.append("groupid =( SELECT id FROM bannergroup WHERE bannergroup = '" + bannerGroup + "')");
                List<BannerRelation> bannerRelation = query.getList();
                if (CollectionUtils.isEmpty(bannerRelation)) {
                    json(L("Banner图片数据为空，请联系管理员。"), false, "");
                    return "";
                }
                //查询图片明细  插入缓存
                BannerPhotoDao photo = new BannerPhotoDao();
                Query<BannerPhoto> queryPhoto = photo.getQuery();
                queryPhoto.setSql("select * from bannerPhoto");
                queryPhoto.setCls(BannerPhoto.class);
                String countSql = null;
                List<Integer> ids = new ArrayList<>();
                for (BannerRelation banner : bannerRelation) {
                    ids.add(banner.getPhotoid());
                }
                countSql = "id in (" + ids.toString().replaceAll("\\[|\\]", "") + ")";
                queryPhoto.append(countSql);
                queryPhoto.append(" and `status` = 1");
                List<BannerPhoto> bannerPhotoList = queryPhoto.getList();
                if (bannerPhotoList == null || bannerPhotoList.size() < 1) {
                    json(L("Banner图片数据为空，请联系管理员。"), false, "");
                    return "";
                }
                bannerPhotoCache = com.alibaba.fastjson.JSON.toJSONString(bannerPhotoList);
                Cache.Set("bannerPhotoWeb", bannerPhotoCache, 60 * 60 * 1);

            } else {
                log.error("Banner组 bannerGroup 不可为空！");
            }
        }
        return bannerPhotoCache;
    }

    /**
     * 获取首页banner为app
     *
     * @param bannerGroup
     * @return
     */
    public String getBannerPhotoForApp(String bannerGroup) {
        String bannerPhotoCache = Cache.Get("bannerPhotoApp");
        if (StringUtils.isEmpty(bannerPhotoCache) || "[]".equals(bannerPhotoCache)) {
            if (StringUtils.isNotEmpty(bannerGroup)) {
                //获取关联图片数据ID
                BannerRelationDao bannerRelationDao = new BannerRelationDao();
                Query<BannerRelation> query = bannerRelationDao.getQuery();
                query.setSql("SELECT * FROM bannerrelation");
                query.setCls(BannerRelation.class);
                query.append("groupid =( SELECT id FROM bannergroup WHERE bannergroup = '" + bannerGroup + "')");
                List<BannerRelation> bannerRelation = query.getList();
                if (CollectionUtils.isEmpty(bannerRelation)) {
                    json(L("Banner图片数据为空，请联系管理员。"), false, "");
                    return "";
                }
                //查询图片明细  插入缓存
                BannerPhotoDao photo = new BannerPhotoDao();
                Query<BannerPhoto> queryPhoto = photo.getQuery();
                queryPhoto.setSql("select * from bannerPhoto");
                queryPhoto.setCls(BannerPhoto.class);
                String countSql = null;
                List<Integer> ids = new ArrayList<>();
                for (BannerRelation banner : bannerRelation) {
                    ids.add(banner.getPhotoid());
                }
                countSql = "id in (" + ids.toString().replaceAll("\\[|\\]", "") + ")";
                queryPhoto.append(countSql);
                queryPhoto.append(" and `status` = 1");
                List<BannerPhoto> bannerPhotoList = queryPhoto.getList();
                if (bannerPhotoList == null || bannerPhotoList.size() < 1) {
                    json(L("Banner图片数据为空，请联系管理员。"), false, "");
                    return "";
                }
                bannerPhotoCache = com.alibaba.fastjson.JSON.toJSONString(bannerPhotoList);
                Cache.Set("bannerPhotoApp", bannerPhotoCache, 60 * 60 * 1);

            } else {
                log.error("Banner组 bannerGroup 不可为空！");
            }
        }
        return bannerPhotoCache;
    }

    @Page(Viewer = JSON)
    public void coinAll() {
        Set<String> coinAll = new HashSet<>();
        String[] strNew = null;
        try {
            Map<String, JSONObject> marketMaps = Market.getMarketsMap();//获取盘口配置信息
            if (marketMaps != null && !marketMaps.isEmpty()) {
                Set<String> market = marketMaps.keySet();
                for (String str : market) {
                    strNew = str.split("_");
                    coinAll.add(strNew[0]);
                }
            }
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
        json("success", true, String.valueOf(coinAll.size()));
    }


    /**
     * 保存
     */
    @Page(Viewer = JSON)
    public void saveSeo() {
        Seo seo = new Seo(seoDao.getDatastore());
        seo.setTith("ddd");
        seo.setTitle("新闻");
        seo.setUrl("bw/news");
        seoDao.addSeo(seo);
    }

    /**
     * 查询
     */
    @Page(Viewer = JSON)
    public void getSeoList() {
        List<Seo> list = seoDao.getSeoList();
        json("ok", true, JSONObject.toJSONString(list), true);
    }


    @Page(Viewer = "/cn/user/sitemap.jsp")
    public void sitemap() {
        List<Seo> list = seoDao.getSeoList();
        setAttr("seoList", list);
    }

    @Page(Viewer = "/cn/user/downApp_ios.jsp")
    public void downApp_ios() {

    }

    @Page(Viewer = "/cn/user/downApp_And.jsp")
    public void downApp_And() {

    }

    @Page(Viewer = "/cn/user/downApp.jsp")
    public void downApp() {

    }

//    @Page(Viewer = "/cn/user/downApp_I.jsp")
//    public void downApp_I(){
//
//    }


    /**
     * 登录和未登录时的手续费
     */
    @Page(Viewer = JSON)
    public void getMarketFee() {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, JSONObject> marketsMap = Market.getMarketsMap();
        for (Map.Entry<String, JSONObject> entry : marketsMap.entrySet()) {
            String marketName = entry.getKey();
            String buyFeeRate = entry.getValue().getDouble("takerFeeRate").toString();
            String sellFeeRate = entry.getValue().getDouble("makerFeeRate").toString();
            Map<String, String> map = new HashMap<>();
            map.put("marketName", marketName);
            map.put("buyFeeRate", buyFeeRate);// 吃单
            map.put("sellFeeRate", sellFeeRate);//  挂单
            list.add(map);
        }
        json("success", true, com.alibaba.fastjson.JSONObject.toJSONString(list));

    }

    /**
     * 获取提现手续费
     */
    @Page(Viewer = JSON)
    public void getMinFees() {
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        Map<String, CoinProps> coinPropMaps = DatabasesUtil.getCoinPropMaps();
        for (Map.Entry<String, CoinProps> entry : coinPropMaps.entrySet()) {
            String coinName = entry.getKey();
            String minFees = entry.getValue().getMinFees().toPlainString();
            jsonObject.put(coinName, minFees);
        }
        json("success", true, jsonObject.toJSONString());
    }

    @Page(Viewer = JSON)
    public void getLevel() {
        UserVipLevelDao userVipLevelDao = new UserVipLevelDao();
        List<UserVipLevel> userVipList = userVipLevelDao.getList();

        Map<String, Object> levelResult = new HashMap<>(1);
        levelResult.put("userVipList", userVipList);

        json("ok", true, com.alibaba.fastjson.JSONObject.toJSONString(levelResult), true);
    }

    /**
     * 推送驾驶舱浏览首页埋点
     */
    @Page(Viewer = JSON)
    public void browseHome() {
        String hostHeader = request.getHeader("Referer");
        if (StringUtils.isEmpty(hostHeader)) {
            json("ok", true, L("非法操作"));
            return;
        }
        String key = GlobalConfig.session + GlobalConfig.cockpit;
        String cockpit = GetCookie(key);
        if (StringUtils.isEmpty(cockpit)) {
            UUID uuid = UUID.randomUUID();
            cockpit = MD5.toMD5(uuid.toString());
            AddCookie(key, cockpit, 999999999);
        }
        //市场名
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cookie", cockpit);
        jsonObject.put("ip", ip());
        jsonObject.put("entrustmarket", "");
        jsonObject.put("userid", "");
        jsonObject.put("browsetime", System.currentTimeMillis());
        ProducerSend producerSend = new ProducerSend();
        producerSend.sendMessage("browsehome", jsonObject.toString());
        log.info("推送驾驶舱浏览首页埋点成功：" + jsonObject.toString());
        json("ok", true, "");
    }

    /**
     * 推送驾驶舱浏览交易页埋点
     */
    @Page(Viewer = JSON)
    public void browseTrade() {
        String hostHeader = request.getHeader("Referer");
        if (StringUtils.isEmpty(hostHeader)) {
            json("ok", true, L("非法操作"));
            return;
        }
        String key = GlobalConfig.session + GlobalConfig.cockpit;
        String cockpit = GetCookie(key);
        if (StringUtils.isEmpty(cockpit)) {
            UUID uuid = UUID.randomUUID();
            cockpit = MD5.toMD5(uuid.toString());
            AddCookie(key, cockpit, 999999999);
        }
        //市场名
        String market = param("market");
        String userId = userIdStr();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cookie", cockpit);
        jsonObject.put("entrustmarket", market);
        jsonObject.put("userid", userId);
        jsonObject.put("ip", ip());
        jsonObject.put("browsetime", System.currentTimeMillis());
        ProducerSend producerSend = new ProducerSend();
        producerSend.sendMessage("browse", jsonObject.toString());
        log.info("推送驾驶舱浏览交易页埋点成功-browse：" + jsonObject.toString());
        ProducerSend producerSend1 = new ProducerSend();
        producerSend1.sendMessage("browsehome", jsonObject.toString());
        log.info("推送驾驶舱浏览交易页埋点成功-browsehome：" + jsonObject.toString());
        json("ok", true, "");
    }

    @Page(Viewer = JSON)
    public void descript() {
        int fundsType = intParam("fundsType");
        if (fundsType <= 0) {
            json("ok", true, "", true);
            return;
        }
        /*String map = RedisUtil.get("funds_introduction_recharge_" + fundsType);
        if (StringUtils.isNotEmpty(map)) {
            Fundsintroduction fundsintroduction = JSONObject.toJavaObject(JSONObject.parseObject(map), Fundsintroduction.class);
            Fundsintroduction fundsintroduction1 = new Fundsintroduction();
            fundsintroduction1.setFundsType(fundsintroduction.getFundsType());
            fundsintroduction1.setType(fundsintroduction.getType());
            Fundsintroduction fun = this.copyBean(fundsintroduction, fundsintroduction1);
            json("ok", true, com.alibaba.fastjson.JSONObject.toJSONString(fun), true);
            return;
        }*/
        List<Integer> fundsTypeList = new ArrayList<>();
        fundsTypeList.add(fundsType);
        if (fundsType == 10) {
            fundsTypeList.add(102);
        }
        List<Fundsintroduction> fundsintroductionList = fundsinDao.getFunds(fundsTypeList, 0);
        if (CollectionUtils.isEmpty(fundsintroductionList)) {
            json("ok", true, "", true);
            return;
        }
        if (fundsintroductionList.size() == 1) {
            Fundsintroduction fundsintroduction1 = new Fundsintroduction();
            fundsintroduction1.setFundsType(fundsintroductionList.get(0).getFundsType());
            fundsintroduction1.setType(fundsintroductionList.get(0).getType());
            fundsintroduction1.setCoinName(DatabasesUtil.getUsdtAggrement(fundsintroductionList.get(0).getFundsType()).getPropTag().toLowerCase());
            Fundsintroduction fun = this.copyBean(fundsintroductionList.get(0), fundsintroduction1);
            json("ok", true, com.alibaba.fastjson.JSONObject.toJSONString(fun), true);
            return;
        } else {
            List<Fundsintroduction> fundsintroductionList1 = new ArrayList<>();
            for (Fundsintroduction fundsintroduction : fundsintroductionList) {
                Fundsintroduction fundsintroduction1 = new Fundsintroduction();
                fundsintroduction1.setFundsType(fundsintroductionList.get(0).getFundsType());
                fundsintroduction1.setType(fundsintroductionList.get(0).getType());
                fundsintroduction1.setCoinName(DatabasesUtil.getUsdtAggrement(fundsintroduction.getFundsType()).getPropTag().toLowerCase());
                Fundsintroduction fun = this.copyBean(fundsintroduction, fundsintroduction1);
                fundsintroductionList1.add(fun);
            }
            json("ok", true, com.alibaba.fastjson.JSONObject.toJSONString(fundsintroductionList1), true);
            return;
        }


    }


    /**
     * 提现说明
     */
    @Page(Viewer = JSON)
    public void withdrawalDescript() {
        int fundsType = intParam("fundsType");
        if (fundsType <= 0) {
            json("ok", true, "", true);
            return;
        }

        List<Integer> fundsTypeList = new ArrayList<>();
        fundsTypeList.add(fundsType);
        if (fundsType == 10) {
            fundsTypeList.add(102);
        }
        List<Fundsintroduction> fundsintroductionList = fundsinDao.getFunds(fundsTypeList, 1);
        if (CollectionUtils.isEmpty(fundsintroductionList)) {
            json("ok", true, "", true);
            return;
        }
        if (fundsintroductionList.size() == 1) {
            Fundsintroduction fundsintroduction1 = new Fundsintroduction();
            fundsintroduction1.setFundsType(fundsintroductionList.get(0).getFundsType());
            fundsintroduction1.setType(fundsintroductionList.get(0).getType());
            fundsintroduction1.setCoinName(DatabasesUtil.getUsdtAggrement(fundsintroductionList.get(0).getFundsType()).getPropTag().toLowerCase());
            Fundsintroduction fun = this.copyBean(fundsintroductionList.get(0), fundsintroduction1);
            json("ok", true, com.alibaba.fastjson.JSONObject.toJSONString(fun), true);
            return;
        } else {
            List<Fundsintroduction> fundsintroductionList1 = new ArrayList<>();
            for (Fundsintroduction fundsintroduction : fundsintroductionList) {
                Fundsintroduction fundsintroduction1 = new Fundsintroduction();
                fundsintroduction1.setFundsType(fundsintroductionList.get(0).getFundsType());
                fundsintroduction1.setType(fundsintroductionList.get(0).getType());
                fundsintroduction1.setCoinName(DatabasesUtil.getUsdtAggrement(fundsintroduction.getFundsType()).getPropTag().toLowerCase());
                Fundsintroduction fun = this.copyBean(fundsintroduction, fundsintroduction1);
                fundsintroductionList1.add(fun);
            }
            json("ok", true, com.alibaba.fastjson.JSONObject.toJSONString(fundsintroductionList1), true);
            return;
        }
    }

    private Fundsintroduction copyBean(Fundsintroduction fundsintroduction, Fundsintroduction fundsintroduction1) {
        switch (lan) {
            case "en":
                fundsintroduction1.setDescript(fundsintroduction.getDescriptEN());
                break;
            case "jp":
                fundsintroduction1.setDescript(fundsintroduction.getDescriptJP());
                break;
            case "kr":
                fundsintroduction1.setDescript(fundsintroduction.getDescriptKR());
                break;
            case "tw":
                fundsintroduction1.setDescript(fundsintroduction.getDescriptHK());
            default:
                fundsintroduction1.setDescript(fundsintroduction.getDescriptCN());
        }

        return fundsintroduction1;

    }

    @Page(Viewer = JSON)
    public void integralRule() {

        List<String> list = RedisUtil.getList("integralRule");
        if (list.size() > 0) {
            //如果缓存取不到从mongo取，取完放到缓存里
            json("ok", true, com.alibaba.fastjson.JSONObject.parseArray(list.toString()).toJSONString(), true);
        } else {
            List<IntegralRule> dataList = integralRuleDao.getList();
            RedisUtil.listObjectAddList("integralRule", dataList);
            json("ok", true, com.alibaba.fastjson.JSONObject.parseArray(dataList.toString()).toJSONString(), true);
        }

    }

    /**
     * 产品&超级节点信息
     */
    @Page(Viewer = JSON)
    public void productSuperNode() {
        try {
            /**
             * 获取基础信息，直接从Redis获取
             * sNodeNum:超级主节点数量
             * sNodeTotalProfit:产出总量
             * currentBlock/profitBlock:当前区块/分红区块
             */
            String sNodeNum = RedisUtil.get("financial_sNodeNum");
            if (StringUtils.isEmpty(sNodeNum)) {
                sNodeNum = "0";
                log.info("理财报警WARN，报警信息：超级主节点数量获取异常，获取值 = " + sNodeNum);
            }
            /*存放时，存放整数*/
            String sNodeTotalProfit = RedisUtil.get("financial_sNodeTotalProfit");
            if (StringUtils.isEmpty(sNodeTotalProfit)) {
                sNodeTotalProfit = "0";
                log.info("理财报警WARN，报警信息：超级主节点收益获取异常，获取值 = " + sNodeTotalProfit);
            }
            String currentBlock = RedisUtil.get("financial_currentBlock");
            if (StringUtils.isEmpty(currentBlock)) {
                currentBlock = "0";
                log.info("理财报警WARN，报警信息：VDS当前区块高度获取异常，获取值 = " + currentBlock);
            }
            String profitBlock = RedisUtil.get("financial_profitBlock");
            if (StringUtils.isEmpty(profitBlock)) {
                profitBlock = "0";
                log.info("理财报警WARN，报警信息：VDS分红区块高度获取异常，获取值 = " + profitBlock);
            }
            /**
             * 获取基础信息
             * profitWeight:分红权重
             * proTotalAmount:投资总额		在Redis中设置超时时间为10秒，获取不到从数据库读取
             * proTotalUser:投资人数		在Redis中设置超时时间为10秒，获取不到从数据库读取
             */
            String profitWeight = "0";
            String proTotalUser = RedisUtil.get("financial_proTotalUser");
            String proTotalAmount = RedisUtil.get("financial_proTotalAmount");
            if (StringUtils.isEmpty(proTotalUser)) {
                proTotalUser = "0";
            }
            if (StringUtils.isEmpty(proTotalAmount)) {
                proTotalAmount = "0";
            }
            BigDecimal bdProTotalAmount = BigDecimal.ZERO;
            if (!StringUtils.isEmpty(proTotalAmount)) {
                try {
                    bdProTotalAmount = BigDecimal.valueOf(Long.valueOf(proTotalAmount));
                } catch (Exception e) {
                    bdProTotalAmount = BigDecimal.ZERO;
                }
            }
            log.info("sNodeNum = " + sNodeNum + ", sNodeTotalProfit = " + sNodeTotalProfit);
            log.info("currentBlock = " + currentBlock + ", profitBlock = " + profitBlock);
            log.info("proTotalAmount = " + proTotalAmount + ", proTotalUser = " + proTotalUser);
//            String sql = "";
            /*总投资人数，总投资金额*/
            /*总分红权重，人数*100*/
            String profitWeightTotal = RedisUtil.get("financial_profitWeightTotal");
            log.info("profitWeightTotal = " + profitWeightTotal);
            if (StringUtils.isEmpty(profitWeightTotal)) {
                profitWeightTotal = "0";
            }

            /*分红倒计时 xx日yy时zz分 */
            String bonusSurplusTime = L("分红倒计时");
            long currentTime = System.currentTimeMillis();
            long bonusTime = 1565208000000L;
            String strBonusTime = "";
            strBonusTime = RedisUtil.get("financial_bonusTime");
            try {
                bonusTime = Long.valueOf(strBonusTime);
            } catch (Exception e) {
                bonusTime = 1565208000000L;
            }
            log.info("bonusTime = " + bonusTime);
            long surplusTime = bonusTime - currentTime;
            /*计算XX天XX时XX分*/
            String bonusDays = "00";
            String bonusHours = "00";
            String bonusMinute = "00";
            try {
                if (surplusTime / (24 * 3600 * 1000) < 0) {
                    bonusDays = "00";
                } else {
                    bonusDays = surplusTime / (24 * 3600 * 1000) + "";
                }
                if ((surplusTime - (24 * 3600 * 1000 * Integer.valueOf(bonusDays))) / (3600 * 1000) < 0) {
                    bonusHours = "00";
                } else {
                    bonusHours = (surplusTime - (24 * 3600 * 1000 * Integer.valueOf(bonusDays))) / (3600 * 1000) + "";
                }
                if ((surplusTime - (24 * 3600 * 1000 * Integer.valueOf(bonusDays)) - (3600 * 1000 * Integer.valueOf(bonusHours))) / (60 * 1000) < 0) {
                    bonusMinute = "00";
                } else {
                    bonusMinute = (surplusTime - (24 * 3600 * 1000 * Integer.valueOf(bonusDays)) - (3600 * 1000 * Integer.valueOf(bonusHours))) / (60 * 1000) + "";
                }
// 	 	 		bonusSurplusTime = bonusDays + "xx" + bonusHours + "yy" + bonusMinute + "zz";
                if (bonusDays.length() == 1) {
                    bonusDays = "0" + bonusDays;
                }
                if (bonusHours.length() == 1) {
                    bonusHours = "0" + bonusHours;
                }
                if (bonusMinute.length() == 1) {
                    bonusMinute = "0" + bonusMinute;
                }
            } catch (Exception e) {

            }
            bonusSurplusTime = bonusSurplusTime.replaceAll("xx", bonusDays);
            bonusSurplusTime = bonusSurplusTime.replaceAll("yy", bonusHours);
            bonusSurplusTime = bonusSurplusTime.replaceAll("zz", bonusMinute);

            /*VDS生态回馈*/
            String vdsEcologyBack = "0";
            try {
                vdsEcologyBack = (bdProTotalAmount.multiply(BigDecimal.valueOf(0.05))).setScale(0, BigDecimal.ROUND_DOWN) + "";
            } catch (Exception e) {
                vdsEcologyBack = "0";
            }
            /*拼装返回值*/
            Map<String, Object> result = new HashMap<>();
            result.put("profitWeight", profitWeight);
            result.put("proTotalAmount", proTotalAmount);
            result.put("proTotalUser", proTotalUser);
            result.put("sNodeNum", sNodeNum);
            result.put("sNodeTotalProfit", sNodeTotalProfit);
            result.put("currentBlock", currentBlock);
            result.put("profitBlock", profitBlock);
            result.put("profitWeightTotal", profitWeightTotal);
            /*限时返利，剩余时间*/
            String surplusHour = "0";
            result.put("surplusHour", surplusHour);
            /*拼装返回值*/
            result.put("vdsEcologyBack", vdsEcologyBack);
            /*分红倒计时*/
            result.put("bonusSurplusTime", bonusSurplusTime);
            log.info("result = " + JSONObject.toJSONString(result));
            /*接口返回*/
            json("ok", true, JSONObject.toJSONString(result), true);
        } catch (Exception e) {
            json("ok", false, null, true);
            log.info("理财报警ERROR:productSuperNode", e);
        }
    }

    @Page(Viewer = JSON)
    public void getSuperNodeInfo() {
        try {
            int currentPage = intParam("pageIndex");
            ;//当前页数
            if (currentPage <= 0) {
                currentPage = 0;
            }
            int sort = 0;  //排序， 0 正常， 1：倒叙
            int sNodeType = 0;//1自建，2新增
            int sNodeBelType = 0;
            try {
                sort = intParam("sort");  //排序， 0 正常， 1：倒叙
                sNodeType = intParam("sNodeType");
                ;//1自建，2新增
                sNodeBelType = intParam("sNodeBelType");
            } catch (Exception e) {

            }
            /*String response = RedisUtil.get(keyPre+currentPage);
            if(!StringUtils.isEmpty(response)){
                json("ok", true, response);
                return;
            }*/
            Query<SuperNode> query = financialSuperNodeDao.getQuery();
            query.setSql("select sNodeAddr, sNodeBalance, sNodePayAmount, lateMiningAmount, lateMiningTime, managerRate, sNodeQueryLink, sNodeBelType from fin_supernode");
            query.append(" sNodeShowFlag=1");
            if (sNodeType != 0) {
                query.append(" sNodeType=" + sNodeType);
            }
            if (sNodeBelType != 0) {
                if (sNodeBelType == 1) {
                    query.append(" sNodeBelType = 1");
                } else if (sNodeBelType == 2) {
                    query.append(" sNodeBelType != 1 ");
                }
            }
            if (sort != 0) {
                query.append("order by lateMiningAmount desc");  //增加排序
            } else {
                query.append("order by lateMiningTime desc");
            }
            query.setDatabase("vip_financial");
            query.setCls(SuperNode.class);
            query.setParams(new Object[]{});
            int total = query.count();
            List<SuperNode> superNodeList = new ArrayList<>();
            /*sNodeBelName 归属类型判断 1 是VIP分红 对应 sNodeBelType 1，2是新人加成 对应 sNodeBelType 2,3*/
            if (total > 0) {
                //分页查询
                superNodeList = query.getPageList(currentPage, 50);
                for (SuperNode superNode : superNodeList) {
                    BigDecimal bonusAmount = (superNode.getsNodeBalance().subtract(superNode.getsNodePayAmount())).multiply((BigDecimal.ONE.subtract(superNode.getManagerRate())));
                    superNode.setBonusAmount(bonusAmount);
                    sNodeBelType = superNode.getsNodeBelType();
                    if (1 == sNodeBelType) {
                        superNode.setsNodeBelName(L("VIP分红"));
                    } else {
                        superNode.setsNodeBelName(L("回本加成"));
                    }
                }
            }
            Map<String, Object> page = new HashMap<String, Object>();
            page.put("pageIndex", currentPage);
            page.put("totalCount", total);
            page.put("list", superNodeList);
            //RedisUtil.set(keyPre+currentPage,JSONObject.toJSONString(page),30);
            json("ok", true, JSONObject.toJSONString(page));
        } catch (Exception e) {
            log.info("获取超级节点信息失败", e);
        }

    }

    @Page(Viewer = JSON)
    public void superNodeProduceInfo() {
        //初创节点数量
        String homeMadeNodeShowNum = RedisUtil.get("fin_homeMadeNodeShowNum");
        if (StringUtils.isBlank(homeMadeNodeShowNum) ) {
            homeMadeNodeShowNum = "0";
        }

        //初创节点累积收益
        String homeMadeNodeTotalProfit = RedisUtil.get("fin_homeMadeNodeTotalProfit");
        if (StringUtils.isBlank(homeMadeNodeTotalProfit) ) {
            homeMadeNodeTotalProfit = "0";
        }

        //初创节点已发放收益
        String homeMadeNodePayProfit = RedisUtil.get("fin_homeMadeNodePayProfit");
        if (StringUtils.isBlank(homeMadeNodePayProfit)  ) {
            homeMadeNodePayProfit = "0";
        }

        //固定节点数量
        String fixedMadeNodeShowNum = RedisUtil.get("fin_fixedMadeNodeShowNum");
        if (StringUtils.isBlank(fixedMadeNodeShowNum)  ) {
            fixedMadeNodeShowNum = "0";
        }

        //固定节点累积收益
        String fixedMadeNodeTotalProfit = RedisUtil.get("fin_fixedMadeNodeTotalProfit");
        if (StringUtils.isBlank(fixedMadeNodeTotalProfit) ) {
            fixedMadeNodeTotalProfit = "0";
        }

        //固定节点已发放收益
        String fixedMadeNodePayProfit = RedisUtil.get("fin_fixedMadeNodePayProfit");
        if (StringUtils.isBlank(fixedMadeNodePayProfit) ) {
            fixedMadeNodePayProfit = "0";
        }

        //动态节点数量
        String trendsMadeNodeShowNum = RedisUtil.get("fin_trendsMadeNodeShowNum");
        if (StringUtils.isBlank(trendsMadeNodeShowNum) ) {
            trendsMadeNodeShowNum = "0";
        }

        //动态节点累积收益
        String trendsMadeNodeTotalProfit = RedisUtil.get("fin_trendsMadeNodeTotalProfit");
        if (StringUtils.isBlank(trendsMadeNodeTotalProfit) ) {
            trendsMadeNodeTotalProfit = "0";
        }

        //动态节点已发放收益
        String trendsMadeNodePayProfit = RedisUtil.get("fin_trendsMadeNodePayProfit");
        if (StringUtils.isBlank(trendsMadeNodePayProfit) ) {
            trendsMadeNodePayProfit = "0";
        }

        Integer bonus=0;
        Integer accelerator=0;
        //节点用作静态和保险分红字段显示 初期-固定-动态
        try {
             bonus = Integer.valueOf(homeMadeNodeShowNum) - Integer.valueOf(fixedMadeNodeShowNum) - Integer.valueOf(trendsMadeNodeShowNum);

            //固定超级主节点提示字段 固定+动态
             accelerator = Integer.valueOf(fixedMadeNodeShowNum) + Integer.valueOf(trendsMadeNodeShowNum);
        }catch (Exception e){
            log.info("保险分红字段查询为null",e);
        }
        String resultMsg = L("初创吐司");

        resultMsg = resultMsg.replaceAll("xxx", "" + bonus);
        resultMsg = resultMsg.replaceAll("yyy", "" + accelerator);

        String fixedMsg = L("固定吐司");
        fixedMsg = fixedMsg.replaceAll("xxx", "" + fixedMadeNodeShowNum);

        String trendsMsg = L("动态吐司");
        trendsMsg = trendsMsg.replaceAll("xxx", "" + trendsMadeNodeShowNum);


        Map<String, String> map = new HashMap<>();
        map.put("homeMadeNodeShowNum", homeMadeNodeShowNum);
        map.put("homeMadeNodeTotalProfit", homeMadeNodeTotalProfit);
        map.put("homeMadeNodePayProfit", homeMadeNodePayProfit);
        map.put("fixedMadeNodeShowNum", fixedMadeNodeShowNum);
        map.put("fixedMadeNodeTotalProfit", fixedMadeNodeTotalProfit);
        map.put("trendsMadeNodeShowNum", trendsMadeNodeShowNum);
        map.put("trendsMadeNodeTotalProfit", trendsMadeNodeTotalProfit);
        map.put("trendsMadeNodePayProfit", trendsMadeNodePayProfit);
        map.put("fixedMadeNodePayProfit", fixedMadeNodePayProfit);
        map.put("homeMadeNodeTips", resultMsg);
        map.put("fixedMadeNodeTips", fixedMsg);
        map.put("trendsMadeNodeTips", trendsMsg);

        json("ok", true, JSONObject.toJSONString(map));


    }


    @Page(Viewer = JSON)
    public void getBonusType() {
        Map<String, Object> bonusMap = new HashMap<>();
        for (BonusEnum bonusEnum : BonusEnum.values()) {
            bonusMap.put(String.valueOf(bonusEnum.getKey()), L(bonusEnum.getValue()));
        }
        //Response.append(bonusMap);
        json("ok", true, JSONObject.toJSONString(bonusMap));
    }


    public static void main(String[] args) {
        Index index = new Index();
        //index.getBonusList();
        index.getBonusType();
        //index.coinAll();
    }

}


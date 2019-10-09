package com.world.web;

import com.Lan;
import com.file.config.FileConfig;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.data.database.DatabasesUtil;
import com.world.model.dao.admin.competence.RoleFunctionManager;
import com.world.model.entity.admin.competence.MenuViewFunction;
import com.world.model.entity.coin.CoinProps;
import com.world.util.WebUtil;
import com.world.util.callback.AsynMethodFactory;
import com.world.util.device.HttpRequestDeviceUtils;
import com.world.util.ip.IpUtil;
import com.world.util.string.StringUtil;
import com.world.util.xml.XmlData;
import com.world.util.xss.XssUtil;
import com.world.web.action.Action;
import com.world.web.convention.annotation.ActionCache;
import com.world.web.defense.AccessLogFactory;
import com.world.web.response.DataResponse;
import com.world.web.sso.SSOLoginManager;
import com.world.web.sso.SessionUser;
import com.world.web.sso.session.Session;
import com.world.web.sso.session.SsoSessionManager;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * 基础的pages页面
 */
public class Pages implements Action, Serializable {
    private static final long serialVersionUID = -1101785277330906714L;

    protected static Logger log = Logger.getLogger(Pages.class.getName());

    public static long totalPageCount = 0;//页面总访问次数
    // / 请求基础配置
    public ServletContext context;
    // / 请求对象
    public HttpServletRequest request;
    // / 输出对象
    public HttpServletResponse response;
    private static final String COOKIE_KEY_OF_LOGIN_STATUS = GlobalConfig.session + "loginStatus";
    public Session session;
    public String sessionId = "";
    protected Hashtable<String, String> UrlPrama;
    protected Hashtable<String, String> Cookies;
    // 缓存输出
    protected StringBuilder Response;
    // 视图路径
    protected String viewerPath = "";
    // 缓存输出多长时间
    protected int cacheTime = 0;
    // 服务器带过来的一些参数
    public UrlViewCode urlViewCode;
    protected String jspBasePkg = "";
    protected String functionDes = "";
    protected MenuViewFunction mvf = null;// 当前视图相关
    public String lan = "cn";// / 默认语言
    protected boolean FORCE_CROSS_DOMAIN = false;//是否需要跨域
    public CoinProps coint = null;
    public Map<String, Object> freeMarker = null;
    public String reqURI = "";
    public String contentType = CONTENT_TEXT_HTML;
    public String jsonCallBack = null;
    public boolean siteMesh = false;//是否被siteMesh装饰
    protected int resoureRequest = 0;// 默认为  浏览器请求0   1.app

    // add by suxinjie 20170718 多域名切换,覆盖接口中的VIP_DOMAIN
    public String VIP_DOMAIN = FileConfig.getValue("vip");
    public String MAIN_DOMAIN = FileConfig.getValue("main");
    public String LOGIN = FileConfig.getValue("login");
    public String MOBILE_DOMAIN = FileConfig.getValue("mobile");

    public String ipNeedAuthen = "ipNeedAuthen";
    public String loginNeedGoogleAuth = "loginNeedGoogleAuth";

    /**
     * 获取资源
     *
     * @param key key
     * @return 国际化语言
     */
    public String L(String key) {
        return Lan.Language(lan, key);
    }

    public String L(String key, String... format) {
        return Lan.LanguageFormat(lan, key, format);
    }

    public Pages() {
    }

    /**
     * 功能：初始化构造函数
     *
     * @param _context  上下文
     * @param _request  请求
     * @param _response 回复
     * @return 是否成功，如果失败就不继续乡下进行了
     */
    public String BaseInit(ServletContext _context, HttpServletRequest _request, HttpServletResponse _response, UrlViewCode uvc) {
        if (uvc.viewCode != null && uvc.viewCode.viewerType == ViewerType.FREEMARKER) {
            freeMarker = new HashMap<String, Object>();
        }
        viewerPath = uvc.viewCode.viewerPath;
        urlViewCode = uvc;
        cacheTime = uvc.viewCode.cacheTime;

        totalPageCount++;
        context = _context;
        request = _request;
        response = _response;

        Response = new StringBuilder();
        UrlPrama = new Hashtable<String, String>();
        Cookies = new Hashtable<String, String>();

        try {
            request.setCharacterEncoding(DEFAULT_ENCODE);
        } catch (UnsupportedEncodingException e) {
            log.error(e.toString(), e);
        }
        setAttr("market", GlobalConfig.market);
        setAttr("currency", GlobalConfig.currency);
        setAttr("currencyN", GlobalConfig.currencyN);

        setAttr("session_header", GlobalConfig.session);
        setAttr("static_domain", STATIC_DOMAIN);
////    TODO: 2017/7/10 suxinjie 域名切换
//		setAttr("main_domain", MAIN_DOMAIN);
//		setAttr("vip_domain", VIP_DOMAIN);

//		log.error(">>>>>>>>>>>>>>>>>>>.  request.getHeader(host) = " + request.getScheme() +"://" + _request.getHeader("host"));
        String main_domain = MAIN_DOMAIN;
        // FIXME: 2017/7/21 suxinjie https需要通过request.getScheme()获取,跟Nginx和tomcat配置有关
        VIP_DOMAIN = GlobalConfig.scheme + "://" + request.getHeader("host");
        MAIN_DOMAIN = GlobalConfig.scheme + "://" + request.getHeader("host");
        MOBILE_DOMAIN = GlobalConfig.scheme + "://" + request.getHeader("host");
        setAttr("vip_domain", GlobalConfig.scheme + "://" + request.getHeader("host"));
        setAttr("main_domain", GlobalConfig.scheme + "://" + request.getHeader("host"));

        setAttr("p2p_domain", P2P_DOMAIN);
        setAttr("trans_domain", TRANS_DOMAIN);
        setAttr("m_domain", MOBILE_DOMAIN);
        setAttr("baseDomain", GlobalConfig.baseDomain);
        setAttr("marketLan", L(GlobalConfig.market));
        setAttr("currencyLan", L(GlobalConfig.currency));
        setAttr("func_des", uvc.viewCode.des);
        String iframe = request.getParameter("iframe");
        boolean frame = iframe != null && iframe.equals("1") ? true : false;
        setAttr("isFrame", frame);
        reqURI = request.getRequestURI();
        String surl = reqURI.toLowerCase();
        String pureUrl = "";
        String queryStr = request.getQueryString();
        String url = surl;//
        FORCE_CROSS_DOMAIN = booleanParam("cdomain");

        Object sobj = request.getAttribute("site_mesh_filter_1");
        siteMesh = sobj == null ? false : (Boolean) sobj;//当前路径是否启用了装饰器

        String sResourceRequest = request.getParameter("rs");

        resoureRequest = sResourceRequest == null || !StringUtils.isNumeric(sResourceRequest) ? 0 : Integer.parseInt(sResourceRequest);

        if (GlobalConfig.databaseSelectOpen) {
            coint = coinProps();//全局为coint付值
        }
        if (url.indexOf("?") > 0)
            url = url.substring(0, url.indexOf("?"));
            pureUrl = url;
        url = url.substring(url.lastIndexOf("/") + 1);
        if (url.indexOf(".") > 0)
            url = url.substring(0, url.indexOf('.'));
        String[] ls = url.split("-");
        for (int j = 0; j < ls.length; j++) {
            UrlPrama.put(Integer.toString(j), ls[j]);
        }

        if (StringUtils.isNotBlank(queryStr)) {
            queryStr = "?" + queryStr;
        }

        Cookie rcookie[] = request.getCookies();
        if (rcookie != null) {
            for (int i = 0; i < rcookie.length; i++) {
                if (!Cookies.containsKey(rcookie[i].getName().toLowerCase()))
                    Cookies.put(rcookie[i].getName().toLowerCase(), rcookie[i].getValue());
            }
        }

        String ip = IpUtil.getIp(request);

//        String redisKey = ip + "_" + pureUrl;
//        long count = Cache.incr(redisKey, 1L);
//        if (count == 1) {
//        //设置有效期1秒
//           Cache.expire(redisKey,1);
//        }
//        if (count > 10) {
//            return "caching";
//        }
        
        //log4j增加uid、uri字段 add by buxianguan
        MDC.put("uid", userIdStr());
        MDC.put("uri", reqURI);
        MDC.put("ip", ip);
        MDC.put("requestId", System.currentTimeMillis());

        // FIXME: 2017/5/22 修改为从cookie获取
//		if(resoureRequest <= 0){
//			lan = GetCookie(SsoSessionManager.LANGUAGE);
//		}else if(resoureRequest == 1){
//			lan = "cn";
//		}else if(resoureRequest == 2){
//			lan = "en";
//		}
//        lan = GlobalConfig.defaultLanguage;
//        Cookie lanCookie = new Cookie(SsoSessionManager.LANGUAGE, lan);
//        lanCookie.setHttpOnly(false);
//        //失效时间7天
//        lanCookie.setMaxAge(60 * 60 * 24 * 7);
//        lanCookie.setPath("/");
//        lanCookie.setDomain(GlobalConfig.baseDomain);
//        response.addCookie(lanCookie);
        // FIXME: 2017/7/24 suxinjie 有xss风险
        /*start by gkl 20190223 只留英文后期需要中英繁将注释打开即可*/
        lan = GetCookie(SsoSessionManager.LANGUAGE);
        if (StringUtil.exist(lan)) {
            if (XssUtil.containsXSS(lan)) {
                lan = GlobalConfig.defaultLanguage;
            }
        } else {
            lan = GlobalConfig.defaultLanguage;
            try {
                //全路径
                String urlTmp = request.getRequestURL().toString();
                if (main_domain != null && (urlTmp.equals(main_domain + "/")
                        || urlTmp.equals(main_domain)
//                        || urlTmp.equals(main_domain + "/trade/")
//                        || urlTmp.equals(main_domain + "/trade")
//                        || urlTmp.equals(main_domain + "/v2/trade/")
//                        || urlTmp.equals(main_domain + "/v2/trade")
//                        || urlTmp.equals(main_domain + "/lottery/")
//                        || urlTmp.equals(main_domain + "/lottery")
//                        || urlTmp.equals(TRANS_DOMAIN + "/getExchangeRate/")
//                        || urlTmp.equals(TRANS_DOMAIN + "/getExchangeRate")
//                        || urlTmp.equals(TRANS_DOMAIN + "/line/topall/")
//                        || urlTmp.equals(TRANS_DOMAIN + "/line/topall")
//                        || urlTmp.equals(main_domain + "/msg/newsOrAnnList/")
//                        || urlTmp.equals(main_domain + "/msg/newsOrAnnList")
//                        || urlTmp.equals(main_domain + "/manage/level/getLevelInfo/")
//                        || urlTmp.equals(main_domain + "/manage/level/getLevelInfo")
//                        || urlTmp.equals(main_domain + "/manage/account/getUserTotalAssest/")
//                        || urlTmp.equals(main_domain + "/manage/account/getUserTotalAssest")
//						|| urlTmp.equals(main_domain+"/vote/activity/")
//						|| urlTmp.equals(main_domain+"/vote/activity")
                )) {
                    //国家
                    String province = "";
                    //是否是外网IP
//					if(IpUtil.isIpv4(ip) && IpUtil.isPublicIp(ip)){
//						if(StringUtils.isNotBlank(Cache.Get("ip_" + ip))){
//							lan = Cache.Get("ip_" + ip);
//						}else{
//							province = IpUtil.getProvinceBySina(ip);
//							if("中国".equals(province)){
//								lan = "cn";
//							}else if("香港".equals(province) || "澳门".equals(province) || "台湾".equals(province)){
//								lan = "hk";
//							}else{
//								lan = "en";
//							}
//							Cache.Set("ip_" + ip, lan, 5*60);
//						}
//						log.info("ip,provinceLan,province分别是："+ip+","+lan+"," + province);
//					}
                    Cookie lanCookie = new Cookie(SsoSessionManager.LANGUAGE, lan);
                    lanCookie.setHttpOnly(false);
                    //失效时间7天
                    lanCookie.setMaxAge(60 * 60 * 24 * 7);
                    lanCookie.setPath("/");
                    lanCookie.setDomain(GlobalConfig.baseDomain);
                    response.addCookie(lanCookie);
                }
            } catch (Exception e) {
                log.error("通过ip获取国家语言失败");
            }

        }
    /*End*/
        setAttr("lan", lan);

        if (uvc.viewCode.viewerType != ViewerType.DEFAULT) {
            if (uvc.viewCode.viewerType == ViewerType.XML) {
                if (!FORCE_CROSS_DOMAIN) {//非强制跨域
                    contentType = CONTENT_TEXT_XML;
                } else {
                    contentType = CONTENT_TEXT_JAVASCRIPT;
                }
            } else if (uvc.viewCode.viewerType == ViewerType.JSON) {
                contentType = CONTENT_TEXT_JAVASCRIPT;
                jsonCallBack = param("callback");

                if (jsonCallBack.length() <= 0) {
                    jsonCallBack = param("jsoncallback");
                }
            } else if (uvc.viewCode.viewerType == ViewerType.FREEMARKER) {
                contentType = CONTENT_TEXT_HTML;
            }
        }
        //设置响应头信息 - Content-Type
        response.setContentType(contentType);
        String cacheUrl = null;
        try {
            cacheUrl = getCacheUrl();
        } catch (Exception e1) {
            log.error(e1.toString(), e1);
        }
        if (cacheUrl != null) {
            String rtn = Cache.Get(cacheUrl);
            if (rtn != null) {
                try {
                    if (jsonCallBack != null && jsonCallBack.length() > 0) {//跨域访问？？
                        rtn = jsonCallBack + rtn.substring(rtn.indexOf("("));
                    }
                    response.addHeader("CNDServer", "nproxy_cache_server");
                    //log.info("已缓存的路径：" + cacheUrl + "从缓存取出并返回[]");
                    if (siteMesh) {//response交给sitemesh处理
                        request.setAttribute("cache_page_content_x", rtn);
                    } else {
                        response.getWriter().write(rtn);
                    }
                    return "caching";
                } catch (Exception e) {
                    log.error(e.toString(), e);
                }
            }
        }
        response.addHeader("CNDServer", "cache_server_1");
        return path(surl, queryStr);
    }

    private void initSession() {
        if (resoureRequest >= 1) {//app请求 1中文版app请求  2 英文版请求
            SsoSessionManager.initAppSession(this);
        } else {//浏览器请求
            // 初始化SESSION
            SsoSessionManager.initSession(this);
        }
    }

    private String path(String surl, String queryStr) {
        // 这里统一做一次判断,如果用户访问的是用户管理目录,那么一定是要求登录的,所以这里强制转向到登陆页面
        String getName = this.getClass().getPackage().getName();
        if (GlobalConfig.openAccessLog) {//开启日志
            try {
                Object uname = Cookies.get(Session.uname);
                Object aname = Cookies.get(Session.aname);
                String ue = uname == null ? "" : uname.toString();
                String ae = aname == null ? "" : aname.toString();
                queryStr = queryStr == null ? "" : queryStr;
                AsynMethodFactory.addWork(AccessLogFactory.class, "saveToJVM", new Object[]{surl, queryStr, ae, ue, ip()});
                //AccessLogFactory.saveToJVM(surl, queryStr, ae, ue, ip());
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
        }

        if (getName.indexOf(GlobalConfig.userPath + ".") == 0 || getName.equals(GlobalConfig.userPath)) {
            SessionUser su = null;
            initSession();
            if (session != null) {
                su = session.getUser(this);
            }

            if (session == null || su == null) {
                if (contentType.equals(CONTENT_TEXT_JAVASCRIPT)) {

                    json(L("未登录系统，请先登录!"), false, "{\"isLogin\" : false}");
                    return "device-false";
                } else {
                    toLogin();
                }
                return "false";
            } else {
                /*if("4".equals(Cookies.get(COOKIE_KEY_OF_LOGIN_STATUS).toString())){
					json(L("未登录系统，请先登录!"), false, "{\"isLogin\" : false}");
					session.deleteUser(this);
					log.info("未登录系统，我就给你踢出去了");
					return "device-false";
				}*/
                if (su.others != null && (su.others.getBooleanValue("ipNeedAuthen") || su.others.getBooleanValue("      325513")) && contentType.equals(CONTENT_TEXT_JAVASCRIPT)) {//需要ip授权和谷歌登录认证，手机端访问
//					json(L("需要进行验证"), false, "{\"diffIpLogin\" : "+su.others.getBooleanValue("ipNeedAuthen")+", \"loginGoogleAuth\" : "+su.others.getBooleanValue("loginNeedGoogleAuth")+"}");
                    return "device-false";
                }
                if (su.others != null && su.others.getBooleanValue("ipNeedAuthen")) {
                    Session.addCookie(session.ipauth, su.others.getString("ipNeedAuthen"), 3600, false, false, this);

                    log.info("userId=" + userIdStr() + ", check cookie [ipNeedAuthen], ipNeedAuthen=" + su.others.getBooleanValue("ipNeedAuthen"));

                    if (contentType.equals(CONTENT_TEXT_JAVASCRIPT)) {
//						json(L("异地登录系统，请先认证!"), false, "{\"diffIpLogin\" : true}");
                        return "device-false";
                    } else {
                        log.info("userId=" + userIdStr() + ", ipNeedAuthen - contentType=[" + contentType + "], CONTENT_TEXT_JAVASCRIPT=[" + CONTENT_TEXT_JAVASCRIPT + "]");

//						toLogin();
                        toHome();
                    }
                    return "false";
                }

                if (su.others != null && su.others.getBooleanValue("freezed")) {//需要ip授权

                    log.info("userId=" + userIdStr() + ", check cookie [freezed], freezed=" + su.others.getBooleanValue("freezed"));

                    if (contentType.equals(CONTENT_TEXT_JAVASCRIPT)) {
                        json(L("账户异常，请联系管理员!"), true, "{\"isLogin\" : false}");
                        return "device-false";
                    } else {
                        log.info("userId=" + userIdStr() + ", freezed - contentType=[" + contentType + "], CONTENT_TEXT_JAVASCRIPT=[" + CONTENT_TEXT_JAVASCRIPT + "]");

                        toFreezPage();
                    }
                    return "false";
                }

                if (su.others != null && su.others.getBooleanValue("loginNeedGoogleAuth")) {//校验谷歌认证
                    Session.addCookie(session.googleauth, su.others.getString("loginNeedGoogleAuth"), 3600, false, false, this);

                    log.info("userId=" + userIdStr() + ", check cookie [loginNeedGoogleAuth], loginNeedGoogleAuth=" + su.others.getBooleanValue("loginNeedGoogleAuth"));

                    // FIXME: 2017/7/26 suxinjie 重点排查, 涉及到跨域
                    if (contentType.equals(CONTENT_TEXT_JAVASCRIPT)) {
//						json(L("为了您的账户安全，登录时需要进行Google验证。"), false, "{\"loginGoogleAuth\" : true}");
                        return "device-false";
                    } else {

                        log.info("userId=" + userIdStr() + ", loginNeedGoogleAuth - contentType=[" + contentType + "], CONTENT_TEXT_JAVASCRIPT=[" + CONTENT_TEXT_JAVASCRIPT + "]");

//						toLogin();
                        toHome();
                    }
                    return "false";
                }

            }
        } else if (getName.indexOf("com.world.controller.auth") == 0) {

        } else if (getName.indexOf("com.world.controller.ad_admin") == 0 || getName.indexOf("com.world.controller.admin") == 0) {

            //是否开启管理后台访问
            if (!GlobalConfig.adminPageOpen) {
                json("", true, "吃屎去吧..");
                return "false";
            }

            // 初始化sessionID
            boolean redrict = false;
            SsoSessionManager.initSessionId(this);
            // 后台登录页面本身
            boolean isLogin = getName.indexOf("com.world.controller.ad_admin.admin_login") == 0;
            SsoSessionManager.initSession(this);
            // 如果是管理后台需要先判断一下用户的登录状态
            SessionUser su = session.getAdmin(this);
            if (session == null || su == null) {
                if (!isLogin) {
                    if (reqURI.equals("/ad_admin") || reqURI.equals("/ad_admin/") || reqURI.equals("/admin")) {
                        return "false";
                    }
                    // 如果没有登录的话就转到登录页面
                    redrict("1-没有通过身份验证,请正常登陆后台");
                    return "false";
                } else {
                    if (!VIP_DOMAIN.contains(request.getServerName())) {
                        redrict("2-没有通过身份验证,请正常登陆后台");
                        return "false";
                    }
                }
            }
            setAttr("logAdmin", su);
            if (!isLogin) {//非登录页面作权限过滤
                String urlLs = surl;

                if (urlLs.indexOf('-') > 0)
                    urlLs = urlLs.substring(0, urlLs.indexOf('-'));


                //新角色登陆之后无法加载菜单（2017-04-05）modify by xiaobei
                if (!urlLs.equals("/ad_admin/admin_manage") && !urlLs.equals("/admin") && !urlLs.equals("/ad_admin") && !urlLs.equals("/admin/competence/menu/rolejsons") && !urlLs.equals("/admin/redrictrole") && !urlLs.equals("/ad_admin/logout")) {
                    //20170824 xzhang 运营管理权限控制
                    ///超级管理员的权限控制跳过
                    if (su.rid.equals("1")) {
                        return "true";
                    } else {
                        mvf = RoleFunctionManager.getFunction(urlLs, su.rid);
                        if (mvf == null && su.prid != null) {
                            mvf = RoleFunctionManager.getFunction(urlLs, su.prid);
                        }
                        if (mvf == null) {
                            redrictRole("当前页面的操作权限不存在,直接转向");
                            return "false";
                        } else {
                            setAttr("admin_permissions", "");
                            return "true";
                        }
                    }
                }
            }
        }
        return "true";
    }

    private void redrictRole(String msg) {
        try {
            log.info("访问当前页面权限不足,消息如下:" + msg + "response.getContentType():" + response.getContentType());
            if (CONTENT_TEXT_XML.equals(response.getContentType()) || CONTENT_TEXT_JAVASCRIPT.equals(response.getContentType())) {
                Write("权限不足", false, "权限不足。");
                return;
                //				json("权限不足", false, "权限不足");
            } else {
                response.sendRedirect(VIP_DOMAIN + "/admin/redrictRole");
            }
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
    }


    public String getCookieString() {
        StringBuilder sbl = new StringBuilder();
        Cookie rcookie[] = request.getCookies();
        if (rcookie != null) {
            for (int i = 0; i < rcookie.length; i++) {
                sbl.append(rcookie[i].getName().toLowerCase() + ":" + rcookie[i].getValue());
            }
        }
        return sbl.toString();
    }

    public String userName() {
        if (session == null) {
            initSession();
        }
        if (session != null) {
            SessionUser su = session.getUser(this);
            if (su != null) {
                return su.uname;
            }
        }
        return "";
    }

    public int validateType() {
        if (session == null) {
            initSession();
        }
        int level = 0;
        if (session != null) {
            SessionUser su = session.getUser(this);
            if (su != null && su.validType != null && su.validType.length() > 0) {
                level = Integer.parseInt(su.validType);
            }
        }
//		if(level == 0){
//			level = 1;
//		}
        return level;
    }

    //static String onlyDomain = MOBILE_DOMAIN.substring(6);
    // 跳转去登录
    public void toLogin() {
        try {
//			boolean mobileDomain = request.getRequestURL().toString().contains(MOBILE_DOMAIN);
//			if(mobileDomain){
//				response.sendRedirect(MOBILE_DOMAIN + "/login?iframe=" + param("iframe"));
//			}else{
            String iframe = param("iframe");
            if (iframe.length() <= 0) {
                response.sendRedirect(VIP_DOMAIN + "/login");
            } else {
                response.sendRedirect(VIP_DOMAIN + "/login?iframe=" + param("iframe"));
            }
//			}
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    public void toHome() {
        try {
            response.sendRedirect(VIP_DOMAIN + "/");
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    public void toIpAhen() {
        try {
            response.sendRedirect(VIP_DOMAIN + "/login/ipAuthen");
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    public void toGoogleAhen() {
        try {
            response.sendRedirect(VIP_DOMAIN + "/login/googleAuthen");
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    public void toFreezPage() {
        try {
            response.sendRedirect(VIP_DOMAIN + "/freezed");
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    public boolean IsLogin() {
        if (session == null) {
            initSession();
        }
        if (session != null) {
            return session.getUser(this) != null;
        }
        return false;
    }

    public int userId() {
        return Integer.parseInt(userIdStr());
    }

    public String userIdStr() {
        if (session == null) {
            initSession();
        }
        if (session != null) {
            SessionUser su = session.getUser(this);
            setAttr("sessionUser", su);
            if (su != null) {
                return su.uid;
            }
        }
        return "0";
    }

    public void doForgotLoginCookie() {
        Cookie userCollect = new Cookie("userCollectMarket", "");
        userCollect.setMaxAge(60 * 60 * 2);// s为单位，1个月60*60*24,存储一天
        userCollect.setDomain(Session.SETDOMAIN);
        userCollect.setPath("/");
        response.addCookie(userCollect);
        SSOLoginManager.logout(this, false);
    }

    protected Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    protected Integer intParam(int index) {
        return (Integer) param(index, ReqParamType.INT);

    }

    protected Object getValByType(String val, ReqParamType type) {
        return type.getVal(val);
    }

    protected Object param(int index, ReqParamType type) {
        String val = GetPrama(index);

        Object o = null;
        if (val.length() > 0) {
            o = getValByType(val, type);
        } else {
            if (type.equals(ReqParamType.STRING)
                    || type.equals(ReqParamType.TIMESTAMP)) {
                o = getValByType("", type);
            } else {
                o = getValByType("0", type);
            }
        }
        return o;
    }

    public void setAttr(String key, Object value) {
        if (freeMarker != null) {
            freeMarker.put(key, value);
        }
        request.setAttribute(key, value);
    }

    /****
     * 获取request请求数据
     * @param param
     * @return
     */
    protected String getByParam(String param) {
        String val = request.getParameter(param);
        return val;
    }

    protected Double doubleParam(int index) {
        return (Double) param(index, ReqParamType.DOUBLE);
    }

    protected Float floatParam(int index) {
        return (Float) param(index, ReqParamType.FLOAT);
    }

    protected Long longParam(int index) {
        return (Long) param(index, ReqParamType.LONG);
    }

    protected Boolean booleanParam(int index) {
        return (Boolean) param(index, ReqParamType.BOOLEAN);
    }

    protected Integer intParam(String param) {
        return (Integer) param(param, ReqParamType.INT);
    }

    protected Double doubleParam(String param) {
        return (Double) param(param, ReqParamType.DOUBLE);
    }

    protected Float floatParam(String param) {
        return (Float) param(param, ReqParamType.FLOAT);
    }

    protected Long longParam(String param) {
        return (Long) param(param, ReqParamType.LONG);
    }

    protected Boolean booleanParam(String param) {
        return (Boolean) param(param, ReqParamType.BOOLEAN);
    }

    protected Timestamp dateParam(String param) {
        return (Timestamp) param(param, ReqParamType.TIMESTAMP);
    }

    protected BigDecimal decimalParam(String param) {
        return (BigDecimal) param(param, ReqParamType.BIGDECIMAL);
    }

    protected Object param(String param, ReqParamType type) {
        String val = getByParam(param);
        if (val == null) {
            val = "";
        }
        val = WebUtil.delScript(val);
        Object o = null;
        if (val.length() > 0) {
            o = getValByType(val, type);
        } else {
            if (type.equals(ReqParamType.STRING)) {
                o = getValByType("", type);
            } else if (type.equals(ReqParamType.TIMESTAMP)) {
                o = null;
            } else {
                o = getValByType("0", type);
            }

        }
        return o;
    }

    // 页面内转向
    protected void forward(String path) {
        try {
            request.getRequestDispatcher(path).forward(request, response);
        } catch (ServletException e) {
            log.error(e.toString(), e);
        } catch (IOException e) {
            log.error(e.toString(), e);
        }
    }

    /****
     * 返回当前请求者所在的ip地址
     *
     * @return
     */
    public String ip() {
        return IpUtil.getIp(request);
    }

    public String param(String name) {
        String rtn = request.getParameter(name);
        if (rtn == null)
            return "";
        else {
//			try {
//				rtn = new String(rtn.getBytes("iso-8859-1"),"UTF-8");
//			} catch (UnsupportedEncodingException e) {
//				log.error(e.toString(), e);
//			}
            rtn = WebUtil.delScript(rtn);
//			if(WebUtil.sqlValidate(rtn)){
//				rtn = "非法传参！";
//			}
            return rtn;
        }
    }

    /**
     * 功能：检查当前请求的验证码是否正确,一次性的，用过一次后就失效了
     *
     * @param code 代码
     * @return 是否相等
     */
    public boolean CheckCode(String code) {
        code = code.toLowerCase().trim();
        // log.info(code);
        if (StringUtils.isBlank(code))
            return false;
        initSession();
        String saveCode = Cache.Get("CodeImage_" + sessionId);
        if (code.equals(saveCode)) {
            Cache.Delete("CodeImage_" + sessionId);
            return true;
        } else {
            // 强制清除缓存
            Cache.Delete("CodeImage_" + sessionId);
            return false;
        }
    }

    /**
     * 功能：检查当前请求的验证码是否正确,一次性的，用过一次后就失效了
     *
     * @param code 代码
     * @return 是否相等
     */
    public boolean CheckCodeOnly(String code) {
        code = code.toLowerCase().trim();
        // log.info(code);
        if (StringUtils.isBlank(code))
            return false;
        initSession();
        String saveCode = Cache.Get("CodeImage_" + sessionId);
        log.info("前端输入code："+code+",缓存存入的code："+saveCode+",key："+"CodeImage_" + sessionId);
        if (code.equals(saveCode)) {
            return true;
        } else {
            return false;
        }
    }


    private void redrict(String msg) {
        try {
            log.info("访问当前页面权限不足,消息如下:" + msg);
            response.sendRedirect(VIP_DOMAIN + "/ad_admin/admin_login");
            return;
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
    }

    /**
     * 功能：设置缓存时间
     *
     * @param t
     */
    public void setCacheTime(int t) {
        cacheTime = t;
    }

    /**
     * 功能:获取pages的一个cookie,如果存在就进行url解码,不区分大小写
     *
     * @param name 名称
     * @return
     */
    public String GetCookie(String name) {
        name = name.toLowerCase();
        if (Cookies.containsKey(name)) {
            try {
                return URLDecoder.decode(Cookies.get(name).toString(), DEFAULT_ENCODE);
            } catch (Exception e) {
                log.error(e.toString(), e);
                return null;
            }
        } else
            return null;
    }

    /**
     * 功能:增加一个coolkie,注意,这个函数会因为viewer被设置成jsp等第三方servlet而导致失败,无法输出
     *
     * @param name    cookie名称
     * @param value   cookie的值
     * @param seconds 保持的秒数
     */
    public void AddCookie(String name, String value, int seconds) {
        Cookie c = new Cookie(name, value);
        c.setMaxAge(seconds);// 存储半年
        response.addCookie(c);
    }

    // 获取url后面的参数，从第一个可以服务的函数向后开始算起，0开始，如果是用户目录，用户名称会成为第一个
    public String GetPrama(int id) {
        try {
            if (urlViewCode == null || urlViewCode.urlPramas == null) {
                return "";
            }
            if (id >= urlViewCode.urlPramas.length || id < 0)
                return "";
            else
                return java.net.URLDecoder
                        .decode(urlViewCode.urlPramas[id], "UTF-8")
                        .replace("'", "‘").replace("\"", "”")
                        .replace("{", "｛").replace("}", "｝")
                        .replace("select", "se").replace("<", "")
                        .replace(">", "").replace("update", "")
                        .replace("<", "").replace(">", "")
                        .replace("delete", "").replace("___", ".")
                        .replace("__", "-");
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            return "";
        }
    }

    public String param(int id) {
        return GetPrama(id);
    }

    /**
     * 功能:返回一个bean的xml表示形式
     *
     * @param bean javabeans
     * @return xml字符串, 不包含头部的
     */
    public String BeanToXml(Object bean) {
        XStream xstream = new XStream(new DomDriver());
        String xml = xstream.toXML(bean);// 转换成xml
        return xml;
    }

    /**
     * 功能:直接将一个beans输出给客户端
     *
     * @param bean 原始javabeans
     */
    public void WriteBeanToXml(Object bean) {
        XStream xstream = new XStream(new DomDriver());
        Response.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + xstream.toXML(bean));// 转换成xml
    }

    // 功能:获取视图路径
    public String GetViewerPath() {
        return viewerPath;
    }

    /**
     * 功能:设置视图路径
     *
     * @param path 根目录绝对路径 /Useradmin/me.jsp
     */
    public void SetViewerPath(String path) {
        this.viewerPath = path;
    }

    // / 获取列表参数
    // / <param name="index">参数位置</param>
    // / <returns>返回的值</returns>
    public String GetUrlPrama(int index) {
        String ind = Integer.toString(index);
        if (UrlPrama.containsKey(ind))
            return UrlPrama.get(ind).toString().toLowerCase().replace("'", "‘")
                    .replace("\"", "”").replace("{", "｛").replace("}", "｝")
                    .replace("select", "se").replace("<", "").replace(">", "")
                    .replace("update", "").replace("<", "").replace(">", "")
                    .replace("delete", "").replace("___", ".")
                    .replace("__", "-");
        else
            return "";
    }

    /***
     * 写一个信息的xml，固定格式的，方便客户端读取
     * @param des  <param name="des">描述</param>
     * @param trueOrFalse   <param name="trueOrFalse">信息状态</param>
     * @param data  <param name="data">信息数据</param>
     */
    public void Write(String des, boolean trueOrFalse, String data) {
        if (FORCE_CROSS_DOMAIN) {//强制跨域
            if (!data.startsWith("{") && !data.startsWith("[")) {
                data = "{data : \"" + data + "\"}";
            }
            json(des, trueOrFalse, data, true);
        } else {
            if (!contentType.equals(CONTENT_TEXT_XML)) {
                contentType = CONTENT_TEXT_XML;
            }
            Response.append(XmlData.BuildMessageXml(WebUtil.transHtmByXml(des), trueOrFalse, data));
            response.setContentType(contentType);
        }
    }

    /**
     * 输出正确反馈信息
     *
     * @param data
     */
    public void WriteRight(String data) {
        Write(data, true, data);
    }

    /**
     * 输出错误信息
     *
     * @param data
     */
    public void WriteError(String data) {
        Write(data, false, data);
    }

    /*****
     * post json字符串给客户端
     * @param des 对处理结果的描述
     * @param trueOrFalse 请求是否成功
     * @param data 结果附加字符串 json
     * @param crossDdomain 跨域支持
     */
    public void json(String des, boolean trueOrFalse, String data, boolean crossDdomain) {
        //FIXME suxinjie 暂时添加,后期需要修改 k线图请求单独处理:不加content_type,不加callback(数据),直接返回内容
        if (des.equalsIgnoreCase("k-line")) {
//			response.setHeader("Access-Control-Allow-Origin", "*");
            response.setContentType(CONTENT_TEXT_HTML);

//			DataResponse dr = new DataResponse(des, trueOrFalse, data);
            Response.append(data);

            return;
        }

        //原处理逻辑不变,该跨域还是跨域
        if (!contentType.equals(CONTENT_TEXT_JAVASCRIPT)) {
            contentType = CONTENT_TEXT_JAVASCRIPT;
            response.setContentType(contentType);
        }

        DataResponse dr = new DataResponse(des, trueOrFalse, data);
        if (crossDdomain || FORCE_CROSS_DOMAIN) {
            if (jsonCallBack == null || jsonCallBack.length() == 0) {
                Response.append(dr.getJsonResponseStr());
            } else {
                Response.append(jsonCallBack + "(" + dr.getJsonResponseStr() + ")");
            }
        } else {
            Response.append(dr.getJsonResponseStr());
        }
    }

    public void json(String des, boolean trueOrFalse, String data) {
        json(des, trueOrFalse, data, false);
    }

    /****
     * 将当前servlet定位到新的jsp返回所需的字符串后重新回归
     *
     * @param viewPath
     *            重新定位的jsp路径
     * @return
     */
    public String newJsp(String viewPath) {
        String oldViewPath = GetViewerPath();

        SetViewerPath(viewPath);
        try {
            RequestDispatcher rd = context.getRequestDispatcher(viewerPath); // 定向的页面
            WrapperResponse wrapperResponse = new WrapperResponse(response);
            rd.include(request, wrapperResponse);
            SetViewerPath(oldViewPath);// 设置回来
            response.setContentType(contentType);
            return wrapperResponse.getResult();
        } catch (Exception ex) {
            // 不存在页面，这里空置是否要输出
            log.error(ex.toString(), ex);
            try {
                response.getWriter().println(
                        "执行页面不存在：" + viewerPath + ",或者发生如下错误：\n"
                                + ex.toString());
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
        }
        response.setContentType(contentType);
        SetViewerPath(oldViewPath);// 设置回来
        return null;
    }

    private static Hashtable<String, String> lanJsp = new Hashtable<String, String>();
    private static String root = null;

    // /将当前前请求转向到一个指定Url
    public void ToJsp() {
        try {
            if (Response.length() > 0) {
                Response.delete(0, Response.length());
            }
        } catch (Exception e1) {
            log.error(e1.toString(), e1);
        }

        contentType = CONTENT_TEXT_HTML;
        response.setContentType(contentType);

        RequestDispatcher rd = null;
        String noView = "cn";//不存在时的寻址路径
        if (viewerPath.indexOf("/en/") == 0 && lan.equals("cn")) {// 中文模式
            viewerPath = "/" + lan + viewerPath.substring(3);
            noView = "en";
        } else {//其他模式
            noView = "cn";
        }

//		2017-03-01修改,直接转化到正常视图
        // 这里可以做hash表缓存结果，就不用每次都查询文件了
//		Object obj = lanJsp.get(viewerPath);
//		boolean exists = false;
//		if (obj == null) {
//			if(root == null){
//				PathUtil pu = new PathUtil();
//				///如果是jar  在linux下会以file:开头
//				root = pu.getWebRoot().replace("file:", "");
//				if(root.endsWith("/")){
//					root = root.substring(0 , root.length() -1);
//				}
//				log.info("path:" + root + viewerPath);
//			}
//			File v = new File(root + viewerPath);
//			// log.info(pu.getWebRoot()+"不存在==============");
//			if (v.exists()) {
//				exists = true;
//				lanJsp.put(viewerPath, "1");
//			} else {
//				lanJsp.put(viewerPath, "0");
//			}
//
//		} else {
//			if (obj.toString().equals("1"))
//				exists = true;
//		}
//		//String urlAll = reqURI;
//		if (!exists) {//默认为cn视图
//			rd = context.getRequestDispatcher("/" + noView + viewerPath.substring(3)); // 如果目标文件不存才就请求老的继承
//			log.info("转化到"+noView+"视图：" + "/" + noView + viewerPath.substring(3) + "," + viewerPath);
//		} else{
//			rd = context.getRequestDispatcher(viewerPath); // 定向的页面
//			log.info("正常视图：" + viewerPath + ",当前访问：");
//		}
        rd = context.getRequestDispatcher(viewerPath); // 定向的页面
        log.info("正常视图：" + viewerPath + ",当前访问：" + reqURI);

        try {
//			if (request.getQueryString() != null) {
//				urlAll += "?" + request.getQueryString();
//			}
            WrapperResponse wrapperResponse = new WrapperResponse(response);
            rd.include(request, wrapperResponse);
            Response.append(wrapperResponse.getResult());
            // 语言替换
//			if (cacheTime > 0 && GlobalConfig.cacheOpen && !GlobalConfig.outCacheOpen){
//				Cache.Set(lan + urlAll.toLowerCase(), rtn, cacheTime);
//			}
//			Response.append(b)
            response.getWriter().write(Response.toString());
            cachePage();
        } catch (Exception ex) {
            // 不存在页面，这里空置是否要输出
            log.error(ex.toString() + viewerPath, ex);
            try {
                forward("/notfound");
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
        }
    }

    public void ToJsp(String path) {
        try {
            if (Response.length() > 0) {
                Response.delete(0, Response.length());
            }
        } catch (Exception e1) {
            log.error(e1.toString(), e1);
        }
        contentType = CONTENT_TEXT_HTML;
        response.setContentType(contentType);
        RequestDispatcher rd = null;
        String noView = "cn";//不存在时的寻址路径
        if (path.indexOf("/en/") == 0 && lan.equals("cn")) {// 中文模式
            path = "/" + lan + path.substring(3);
            noView = "en";
        } else {//其他模式
            noView = "cn";
        }

        rd = context.getRequestDispatcher(path); // 定向的页面
        log.info("正常视图：" + path + ",当前访问：" + reqURI);

        try {
//			if (request.getQueryString() != null) {
//				urlAll += "?" + request.getQueryString();
//			}
            WrapperResponse wrapperResponse = new WrapperResponse(response);
            rd.include(request, wrapperResponse);
            Response.append(wrapperResponse.getResult());
            response.getWriter().write(Response.toString());
            cachePage();
        } catch (Exception ex) {
            // 不存在页面，这里空置是否要输出
            log.error(ex.toString() + path, ex);
            try {
                forward("/notfound");
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
        }
    }

    // 交给模板引擎进行解析
    public void ToVelocity() {

    }


    // 交给模板引擎进行解析
    public void ToXml() {
        try {
            PrintWriter out = response.getWriter();
            out.write(Response.toString());
            cachePage();
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
    }

    public void toJson() {
        try {
            PrintWriter out = response.getWriter();
            out.write(Response.toString());
            cachePage();
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
    }

    // 交给xsl进行解析
    public void ToXsl() {

    }

    /**
     * freemarker 模板引擎
     *
     * @param configuration
     */
    public void toFtl(Configuration configuration) {
        try {
            Template template = configuration.getTemplate(viewerPath);
            response.setContentType("text/html; charset=" + template.getEncoding());
            template.process(freeMarker, response.getWriter());
        } catch (IOException e1) {
            log.error(e1.toString(), e1);
        } catch (TemplateException e) {
            log.error(e.toString(), e);
        }
    }

    private String getCacheUrl() throws Exception {
        String cacheUrl = null;
        ActionCache actionCache = null;
        if (urlViewCode.viewCode != null && urlViewCode.viewCode.actionCache != null) {
            cacheTime = urlViewCode.viewCode.actionCache.cacheTime();//后者覆盖前者的原则
            actionCache = urlViewCode.viewCode.actionCache;
        }

        if (cacheTime > 0 && GlobalConfig.cacheOpen && !GlobalConfig.outCacheOpen) {
            cacheUrl = reqURI;
            if (request.getQueryString() != null) {
                cacheUrl += "?" + request.getQueryString();
            }
            cacheUrl = WebUtil.removeParams(cacheUrl, "callback", "jsoncallback", "_");

            cacheUrl = lan + cacheUrl.toLowerCase();
            if (actionCache != null) {
                if (!actionCache.proxyCache()) {
                    cacheUrl = "not_proxy" + actionCache.split() + cacheUrl;
                }
                if (actionCache.userCache()) {//用户缓存
                    String uid = Cookies.get(Session.uid);
                    String ue = uid == null ? "" : uid;
                    if (ue.length() > 0) {
                        cacheUrl = ue + actionCache.split() + cacheUrl;
                    } else {
                        return null;
                    }
                }

                if (actionCache.pageCache()) {//分页缓存
                    int curPage = intParam(actionCache.pageKey());
                    if (curPage <= 0) {
                        curPage = 1;
                    }

                    if (curPage <= actionCache.maxPage()) {
                        //cacheUrl = curPage + actionCache.split() + cacheUrl;
                    } else {
                        return null;
                    }
                }
            }
        }
        return cacheUrl;
    }

    private void cachePage() {
        String cacheUrl = null;
        try {
            cacheUrl = getCacheUrl();
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        if (cacheUrl != null) {
            if (cacheTime > 0) {
                if (siteMesh) {
                    if (urlViewCode.viewCode.viewerType == ViewerType.JSP) {//需要装饰的jsp
                        request.setAttribute("site_mesh_need_cache", new Object[]{cacheUrl, cacheTime});
                    } else {
                        Cache.Set(cacheUrl, Response.toString(), cacheTime);
                    }
                } else {
                    Cache.Set(cacheUrl, Response.toString(), cacheTime);
                }
            }
        }
    }

    public String jsonp(String json) {//域名验证的跨域 不和法的域名返回空值
//		String refer = request.getHeader("Referer");
//		if(refer != null && refer.indexOf(GlobalConfig.baseDomain) >= 0){
//			return param("callback") + "(" + json + ")";
//		}else{
//			return param("callback") + "({})";
//		}
        return param("callback") + "(" + json + ")";
    }

    public boolean isMobile() {
        if (HttpRequestDeviceUtils.isMobileDevice(request)) {//手机访问
            return true;
        } else {
            return false;
        }
    }

    public CoinProps coinProps() {
        String ct = request.getParameter("coint");
        if (ct == null) {
            ct = GetPrama(0);
        }

//		if(ct == null){
//			ct = "btc";
//		}
        coint = DatabasesUtil.coinProps(ct);
        setAttr("coint", coint);
        return coint;
    }

    public void index() {
        log.info("执行父类index");
    }

    public void ajax() {
        log.info("执行父类ajax");
    }

    public String getResponseStr() {
        return Response.toString();
    }


}

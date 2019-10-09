package com.world.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import com.Lan;
import com.api.config.ApiConfig;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.CheckCodeApiService;
import com.messi.user.feign.GoogleApiService;
import com.messi.user.feign.UserApiService;
import com.messi.user.util.ConstantCenter;
import com.world.config.GlobalConfig;
import com.world.constant.Const;
import com.world.model.LimitType;
import com.world.model.LockType;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.entity.CacheKeys;
import com.world.model.entity.EnumUtils;
import com.world.util.CommonUtil;
import com.world.util.MsgToastKey;
import com.world.web.response.DataResponse;
import com.world.web.sso.SessionUser;
import com.world.web.sso.session.ClientSession;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.api.VipResponse;
import com.api.user.UserManager;
import com.world.cache.Cache;
import com.world.controller.api.util.SystemCode;
import com.world.lang.exception.NoUserLogException;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.authen.AuthenticationDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserCommon;
import com.world.model.entity.user.UserContact;
import com.world.model.entity.user.authen.Authentication;
import com.world.util.Message;
import com.world.util.WebUtil;
import com.world.util.language.SafeTipsTag;

/****
 * @version
 *
 */
@SuppressWarnings("serial")
public class UserAction extends BaseAction implements UserCommon {
    protected User loginUser = null;
    protected PayUserBean payUser;
    /**
     * 响应报文DES字段 信息
     */
    protected static final String DES_MSG_SYSTEM_PROMPT = "系统提示";
    public final static String appUid = GlobalConfig.session + "appuid_";
    protected void initLoginUser() {
        String userId = userId() + "";
        if (loginUser == null) {
            loginUser = new UserDao().getUserById(userId);
        }
        if (loginUser != null) {
            UserContact uc = loginUser.getUserContact();
            setAttr("googleAuth", uc.getGoogleAu());
            setAttr("payMobileAuth", loginUser.isPayMobileAuth());
            setAttr("payGoogleAuth", loginUser.isPayGoogleAuth());
            setAttr("payEmailAuth", loginUser.isPayEmailAuth());
            setAttr("mobileStatu", uc.getMobileStatu());
            setAttr("emailStatu", uc.getEmailStatu());
            setAttr("smsOpen",loginUser.getSmsOpen());
            setAttr("curUser", loginUser);

        }
    }

    /**
     * 配合前端改造
     */
    protected void initLoginUserJson() {
        String userId = userId() + "";
        if (loginUser == null) {
            loginUser = new UserDao().getUserById(userId);
        }
    }

    protected void reloadLoginUser() {
        loginUser = null;
        initLoginUser();
    }

    /****
     * 获取当前用户ID ， 无登陆时默认不返回历史页面
     * @return
     */

    protected String userId(boolean isHistory) {
        return userId(isHistory, false);
    }

    /*****
     * 获取当前用户ID
     * @param isHistory 是否返回历史页面  对于xml的请求isHistory=false
     * @param end  是否结束方法，方法是否继续执行
     * @param isIframe 是否处于iframe模式
     * @return
     */
    protected String userId(boolean isHistory, boolean end, boolean isIframe) {
        String uid = "";
//		boolean isLogin=Status.Check(request);
//		if(isLogin){

//		}

        String userId = userIdStr();
        if (userId == null || "0".equals(userId.trim())) {
            //不作处理
        } else {
            uid = userId;
        }

        if (uid.length() <= 0 && end) {
            if (isHistory) {
                if (isIframe) {//frame 模式
                    log.info("xx:" + request.getHeader("Referer"));
                    request.setAttribute("referer", request.getHeader("Referer"));
                    SetViewerPath("/users/loginIframe.jsp");
                    return "";
                } else {
                    log.info("xx:" + request.getHeader("Referer"));
                    request.setAttribute("referer", request.getHeader("Referer"));
                    String urlLs = request.getRequestURI().toLowerCase();

                    if (!urlLs.startsWith("/user/login")) {
                        try {
                            response.sendRedirect(VIP_DOMAIN + "/user/login");
                        } catch (IOException e) {
                            log.error("内部异常", e);
                        }
                    }

                    return "";
                }
            } else {
                Write(NO_LOGIN, false, "");
                ToXml();
            }
            throw new NoUserLogException(NO_LOGIN);
        }
        return uid;
    }

    protected void initLoginUserForJson(Map<String,Object> returnMap) {
        String userId = userId() + "";
        if (loginUser == null) {
            loginUser = new UserDao().getUserById(userId);
        }
        if (loginUser != null) {
            UserContact uc = loginUser.getUserContact();
            returnMap.put("googleAuth", uc.getGoogleAu());
            returnMap.put("payMobileAuth", loginUser.isPayMobileAuth());
            returnMap.put("payGoogleAuth", loginUser.isPayGoogleAuth());
            returnMap.put("payEmailAuth", loginUser.isPayEmailAuth());
            returnMap.put("mobileStatu", uc.getMobileStatu());
            returnMap.put("emailStatu", uc.getEmailStatu());
//            returnMap.put("curUser", loginUser);
        }
    }

    protected String userId(boolean isHistory, boolean end) {
        return userId(isHistory, end, false);
    }

    protected boolean safePwdNoSetTips() {
        initLoginUser();
        SafeTipsTag stt = getLanTag().getStt();
        if (loginUser.getSafePwd() == null || loginUser.getSafePwd().length() <= 0) {
            tip(stt.getWeiSheZhi(), VIP_DOMAIN + "/u/safe/safepwd/payset", false);
            return false;
        }
        return true;
    }

    /**
     * 邮箱认证
     *
     * @return
     */
    protected boolean emailNoSet() {
        initLoginUser();
        if (loginUser.getUserContact().getEmailStatu() != AuditStatus.pass.getKey()) {
            return false;
        }
        return true;
    }

    /**
     * 邮箱认证
     *
     * @return
     */
    protected boolean emailNoSetTips(String tips) {
        initLoginUser();
        if (loginUser.getUserContact().getEmailStatu() != AuditStatus.pass.getKey()) {
            tip(tips + "点击<a target='_blank' style='color:#3366CC' href='" + VIP_DOMAIN + "/manage/auth/email'>邮箱认证</a>", VIP_DOMAIN + "/manage/auth/email", false);
            return false;
        }
        return true;
    }

    /**
     * 邮箱认证
     *
     * @return
     */
    protected boolean emailNoSetTips() {
        initLoginUser();
        if (loginUser.getUserContact().getEmailStatu() != AuditStatus.pass.getKey()) {
            tip("未进行邮箱认证！点击<a target='_blank' style='color:#3366CC' href='" + VIP_DOMAIN + "/manage/auth/email'>邮箱认证</a>", VIP_DOMAIN + "/manage/auth/email", false);
            return false;
        }
        return true;
    }

    protected boolean mobileNoSetTips() {
        initLoginUser();
        if (loginUser.getUserContact().getMobileStatu() != AuditStatus.pass.getKey()) {
            tip("未进行安全认证！点击<a target='_blank' style='color:#3366CC' href='" + VIP_DOMAIN + "/u/auth/mobile'>手机认证</a>", VIP_DOMAIN + "/u/auth/mobile", false);
            return false;
        }
        return true;
    }

    /**
     * 采用邮箱注册且没有开启手机认证的用户在用户“提现”的时候强制使用谷歌验证器
     *
     * @return
     */
    protected boolean googleNoSetTips() {
        initLoginUser();
        if (loginUser.getUserContact().getGoogleAu() != AuditStatus.pass.getKey() && loginUser.getUserContact().getMobileStatu() != AuditStatus.pass.getKey()) {
            // tip("未进行谷歌认证！点击<a target='_blank' style='color:#3366CC' href='"+VIP_DOMAIN+"/u/auth/google?oper=0&dealType=googleAuth&dealVal=99'>谷歌认证</a>", VIP_DOMAIN+"/u/auth/google?oper=0&dealType=googleAuth&dealVal=99", false);
            return false;
        }
        return true;
    }

    /**
     * 实名认证
     *
     * @return
     */
    protected boolean authenNoSetTips() {
        Authentication au = new AuthenticationDao().getByUserId(userIdStr());

        if (au == null || au.getStatus() != AuditStatus.pass.getKey()) {
            tip("未进行实名认证！点击进行<a target='_blank' style='color:#3366CC' href='" + VIP_DOMAIN + "/u/safe/auth'>实名认证</a>", VIP_DOMAIN + "/u/safe/auth", false);
            return false;
        }
        return true;
    }

    /**
     * 简化认证
     *
     * @return
     */
    protected boolean realNameNoSetTips() {
        initLoginUser();
        if (loginUser.getRealName() == null || loginUser.getRealName().length() == 0) {
            tip("未进行实名认证！点击进行<a target='_blank' style='color:#3366CC' href='" + VIP_DOMAIN + "/u/safe#step3'>实名认证</a>", VIP_DOMAIN + "/u/safe#step3", false);
            return false;
        }
        return true;
    }

    protected boolean authen(String userId) {
        Authentication au = new AuthenticationDao().getByUserId(userId);
        if (au == null || au.getStatus() != AuditStatus.pass.getKey()) {
            return false;
        }
        return true;
    }

    protected boolean safePwd(String pass, String userId) {
        return safePwd(pass, userId, XML, false);
    }

    protected boolean safePwd(String pass, String userId, String gs) {
        return safePwd(pass, userId, gs, false);
    }

    protected boolean safePwdNew(String pass, String userId, String gs) {
        return safePwdNew(pass, userId, gs, false);
    }

    /*start by xwz 20170703*/
    protected boolean safePwdNew(String pass, String userId, String gs, boolean crossDomain) {

        SafeTipsTag stt = getLanTag().getStt();

        int status = new UserDao().checkSecurityPwdNew(pass, userId);
        if (status == -2) {
            String suoDing = L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。");
            if (gs.equals(XML))
                WriteError(suoDing);
            else
                json(suoDing, false, "", crossDomain);
            return false;
        } else if (status == -1) {
            if (gs.equals(XML))
                WriteError(stt.getCuoWu());
            else
                json(stt.getCuoWu(), false, "", crossDomain);
            return false;
        } else if (status == 0) {
            if (gs.equals(XML))
                WriteError(WebUtil.transHtmByXml(stt.getWeiSheZhi()));
            else
                json(stt.getWeiSheZhi(), false, "", crossDomain);
            return false;
        } else if(status == -3){
            return true;
        }else{
            String jihui = L("抱歉，资金密码错误，还有%%次机会。",status+"");
            log.info(jihui);
            if (gs.equals(XML))
                WriteError(jihui);
            else
                json(jihui, false, "", crossDomain);
            return false;
        }
    }
    /*end*/
    protected boolean safePwd(String pass, String userId, String gs, boolean crossDomain) {

        SafeTipsTag stt = getLanTag().getStt();

        int status = new UserDao().checkSecurityPwd(pass, userId);
        if (status == -2) {
            String suoDing = L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。");
            if (gs.equals(XML))
                WriteError(suoDing);
            else
                json(suoDing, false, "", crossDomain);
            return false;
        } else if (status == -1) {
            if (gs.equals(XML))
                WriteError(stt.getCuoWu());
            else
                json(stt.getCuoWu(), false, "", crossDomain);
            return false;
        } else if (status == 0) {
            if (gs.equals(XML))
                WriteError(WebUtil.transHtmByXml(stt.getWeiSheZhi()));
            else
                json(stt.getWeiSheZhi(), false, "", crossDomain);
            return false;
        }
        return true;
    }
    protected String safePwdForApp(String pass, String userId,boolean isApp) {
        FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url").concat("/user"));
        UserApiService userApi = container.getFeignClient(UserApiService.class);
        String uftPayPwd = String.valueOf(ConstantCenter.UpdFunctionType.TRANSACTION_PAY_PWD.getKey());
        Map<String, String> map = userApi.checkPayPwdApiN(userId,pass,uftPayPwd);
        if(null != map){
            for(Map.Entry<String,String> entry : map.entrySet()){
                if("1".equals(entry.getKey())){

                }else{
                    String returnVal = entry.getValue();
                    if("-2".equals(returnVal)){
                        if(isApp){

                            returnVal = L("资金密码输入错误超出限制，将锁定交易验证设置功能，请等待24小时之后尝试使用");
                        }else{
                            returnVal = L("资金密码输入错误超出限制，将锁定交易验证设置功能，请等待24小时之后尝试使用").concat("_5");
                        }
                    }else{
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
        return "";
    }

    protected Message safePwd(String pass, String userId, boolean crossDomain) {
        Message msg = new Message();
        SafeTipsTag stt = getLanTag().getStt();

        int status = new UserDao().checkSecurityPwd(pass, userId);
        if (status == -2) {
            String suoDing = L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。");
            msg.setCode(SystemCode.code_1004.getKey());
            msg.setMsg(suoDing);
        } else if (status == -1) {
            msg.setCode(SystemCode.code_1005.getKey());
            msg.setMsg(stt.getCuoWu());
        } else if (status == 0) {
            msg.setCode(SystemCode.code_1005.getKey());
            msg.setMsg(stt.getWeiSheZhi());
        } else {
            msg.setSuc(true);
        }
        return msg;
    }

    /**
     * @param pass
     * @param userId
     * @param gs
     * @param isExceptLocked 是否排除锁定密码的情况
     * @param crossDomain
     * @return
     */
    protected boolean safePwd(String pass, String userId, String gs, boolean isExceptLocked, boolean crossDomain) {
        if (isExceptLocked) {
            SafeTipsTag stt = getLanTag().getStt();

            int status = new UserDao().checkSecurityPwd(pass, userId, true);
            if (status == -1) {
                if (gs.equals(XML))
                    WriteError(stt.getCuoWu());
                else
                    json(stt.getCuoWu(), false, "", crossDomain);
                return false;
            } else if (status == 0) {
                if (gs.equals(XML))
                    WriteError(WebUtil.transHtmByXml(stt.getWeiSheZhi()));
                else
                    json(stt.getWeiSheZhi(), false, "", crossDomain);
                return false;
            }
            return true;
        } else {
            return safePwd(pass, userId, gs, crossDomain);
        }
    }

    /**
     * 输出API返回信息
     *
     * @param errorCode
     * @param message
     */
    public void WriteMsg(int errorCode, String message) {
        message = L(message);
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        buffer.append("\"code\":");
        buffer.append(errorCode).append(",");
        buffer.append("\"message\":");
        buffer.append("\"").append(message).append("\"");
        buffer.append("}");
        Response.append(buffer.toString());
    }

    /**
     * 输出API返回信息
     *
     * @param errorCode
     * @param message
     */
    public void WriteMsg2(int errorCode, String message, String id) {
        message = L(message);
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        buffer.append("\"code\":");
        buffer.append(errorCode).append(",");
        buffer.append("\"message\":");
        buffer.append("\"").append(message).append("\",");
        buffer.append("\"id\":");
        buffer.append("\"").append(id).append("\"");
        buffer.append("}");
        Response.append(buffer.toString());
    }

    /**
     * 输出API返回信息
     *
     * @param errorCode
     * @param des
     */
    public void WriteMsg3(int errorCode, String des, boolean isSuc, String datas) {
        des = L(des);
        if (StringUtils.isEmpty(datas)) {
            datas = "{}";
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        buffer.append("\"code\":");
        buffer.append(errorCode).append(",");
        buffer.append("\"message\":");
        buffer.append("{\"des\": \"" + des + "\", \"isSuc\": " + String.valueOf(isSuc) + ", \"data\": " + datas + "}");
        buffer.append("}");
        Response.append(buffer.toString());
    }

    //FIXME add by suxinjie 20170715 WriteMsg3和WriteMsg4的差别仅仅是des没有国际化
    public void WriteMsg4(int errorCode, String des, boolean isSuc, String datas) {
        if (StringUtils.isEmpty(datas)) {
            datas = "{}";
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        buffer.append("\"code\":");
        buffer.append(errorCode).append(",");
        buffer.append("\"message\":");
        buffer.append("{\"des\": \"" + des + "\", \"isSuc\": " + String.valueOf(isSuc) + ", \"data\": " + datas + "}");
        buffer.append("}");
        Response.append(buffer.toString());

        log.info(">>>>>> response : " + buffer.toString());
    }

    protected void initPayUser() {

    }

    private static int time = 60;//60分钟
    private static int times = 200;//一小时请求的次数
    private static String bid_ey = "forbid_";

    public boolean isForbid() {
        String ip = ip();
        log.info("========================当前验证用户IP：" + ip + ",user:" + userName());
        String cacheIp = Cache.Get(bid_ey + ip);
        if (cacheIp == null) {
            Cache.Set(bid_ey + ip, "1_" + System.currentTimeMillis(), 60 * 60 * 2);
        } else {
            int count = Integer.parseInt(cacheIp.split("\\_")[0]);
            count++;
            if (count < times) {
                Cache.Set(bid_ey + ip, count + "_" + cacheIp.split("\\_")[1], 60 * 60 * 2);
            } else if (count == times) {
                long old = Long.parseLong(cacheIp.split("\\_")[1]);
                long now = System.currentTimeMillis();
                long minute = (now - old) / (1000 * 60);
                if (minute < time) {
                    log.info("====" + ip + "已禁====");
                    Cache.Set(bid_ey + ip, times + "_" + cacheIp.split("\\_")[1], 60 * 60 * 2);
                    return true;
                } else {
                    Cache.Delete(bid_ey + ip);
                }
            } else {
                log.info("====已锁定===" + count + "=");
                return true;
            }
        }
        return false;
    }

    //jsp返回信息
    protected boolean hasLoan() {
        initLoginUser();
//		if(loginUser.getRepayLock() > 0){
//			tip("您在p2p借贷中有借入的资产，系统不允许您提现。", VIP_DOMAIN+"/u/", false);
//	    	return true;
//		}

//		Object obj = allowWithdrawal();
//		if(obj instanceof Boolean){
//			if(!(Boolean)obj){
//				tip("内部错误，请稍候再试。", VIP_DOMAIN+"/u/", false);
//				return true;
//			}
//	    }else{
//	    	tip(obj.toString(), VIP_DOMAIN+"/u/", false);
//	    	return true;
//	    }
        return false;
    }

    //xml返回信息
    protected String hasError() {
//		initLoginUser();
//		if(loginUser.getRepayLock() > 0){
//	    	return "您在p2p借贷中有借入的资产或者有未成功的借入，系统不允许您提现。";
//		}
//		Object obj = allowWithdrawal();
//		if(obj instanceof Boolean){
//			if(!(Boolean)obj){
//				return "内部错误，请稍候再试。";
//			}
//		}else{
//			tip(obj.toString(), VIP_DOMAIN+"/u/", false);
//			return obj.toString();
//		}
        return null;
    }

    private Object allowWithdrawal() {
        try {
            VipResponse response = UserManager.getInstance().allowWithdrawal(userIdStr());
            if (response.taskIsFinish()) {
                JSONObject json = JSONObject.fromObject(response.getMsg());
                if ((Boolean) json.get("isSuc")) {
                    return true;
                } else {
                    return json.get("des");
                }
            }
        } catch (Exception e) {
            log.error("内部异常", e);
        }
        return false;
    }

    /**
     * 发布错误消息
     *
     * @param toastKey Toast键值
     * @param message 消息内容
     */
    protected void postErrMsg(String toastKey, String message) {
        json(L(DES_MSG_SYSTEM_PROMPT), false, CommonUtil.mapToJsonStr(toastKey,L(message)));
    }

    /**
     * 发布成功消息
     * @param message 消息内容
     */
    protected void postSuccessMsg(String message) {
        json(L(message), true, "");
    }


    public void json(SystemCode code, String msg,String data) {
        PrintWriter out = null;
        try {
            response.setContentType("application/json;charset=" + "UTF-8");
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
            if (StringUtils.isNotEmpty(data)) {
                json.put("data", data);
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
            }else {
                lang = "cn";
            }
        } else {
            lang = "cn";
        }

        lan = lang;
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
        log.info("前端传的token:"+token+",获取的用户ID："+userId);
        if (null == userId || "".equals(userId) || StringUtils.isEmpty(token)) {
            return SystemCode.code_1003;
        }

        if(StringUtils.isEmpty(token(userId))){
            log.info("用户ID："+userId);
            return SystemCode.code_1003;
        }else{
            if(token(userId).equals(token)) {
                return SystemCode.code_1000;
            }else{
                return SystemCode.code_402;
            }
        }
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
    /**
     * 校验锁定key
     * @param userId
     * @param functionName
     * @param updFunctionType
     * @param lockTime
     * @return
     */
    public DataResponse checkVerifiCode(String userId, String functionName, ConstantCenter.UpdFunctionType updFunctionType, String lockTime) {
        String type =  String.valueOf(updFunctionType.getKey());
        DataResponse dr = new DataResponse("", false, "{\"id\" : \"vercode\"}");
        String url = ApiConfig.getValue("usecenter.url");
        FeignContainer container = new FeignContainer(url.concat("/checkCode"));
        CheckCodeApiService checkCodeApi = container.getFeignClient(CheckCodeApiService.class);
        Map<String,String> map = checkCodeApi.getErrorTimesN(String.valueOf(userId),type);
        String errorMessage = "";
        for(Map.Entry<String, String> entry : map.entrySet()){
            String key = entry.getKey();
            String returnVal = entry.getValue();
            //验证成功，直接返回
            if("1".equals(key)){
                dr.setSuc(true);
                return dr;
            }

            if("0".equals(key)){
                //验证锁定，直接返回
                if("-2".equals(returnVal)){
                    returnVal = StrUtil.format(MsgToastKey.TIME_OVER_LIMIT_MSG,functionName,lockTime);
                    dr.setDes(Lan.LanguageFormat(lan , returnVal , ""));
                    return dr;
                }
                errorMessage = returnVal;
                break;
            }
        }

        //真是好代码啊！！！
//        errorMessage = errorMessage.replace("谷歌验证码输入有误","验证码输入有误");
//        errorMessage = errorMessage.replace("短信验证码输入有误","验证码输入有误");
//        errorMessage = errorMessage.replace("邮箱验证码输入有误","验证码输入有误");

        //格式化次数
        String[] errorMsg = errorMessage.split("#");
        dr.setDes(Lan.LanguageFormat(lan, errorMsg[0], errorMsg[1]));
        return dr;
    }
}

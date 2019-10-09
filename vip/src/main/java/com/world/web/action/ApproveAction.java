package com.world.web.action;

import cn.hutool.core.util.StrUtil;
import com.Lan;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.googleauth.GoogleAuthenticator;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.CheckCodeApiService;
import com.messi.user.util.ConstantCenter;
import com.world.cache.Cache;
import com.world.model.LimitType;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.user.EmailDao;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserCommon;
import com.world.model.entity.user.UserContact;
import com.world.util.CommonUtil;
import com.world.util.Message;
import com.world.util.MsgToastKey;
import com.world.util.UserUtil;
import com.world.web.response.DataResponse;
import com.world.web.sso.session.ClientSession;
import net.rubyeye.xmemcached.exception.MemcachedException;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 手机短信验证码过滤Action
 * 1分钟重发
 * 10分钟内有效
 * 错误三次锁定2小时
 *
 * @author Administrator
 */
public class ApproveAction extends UserAction implements UserCommon {

    public static int times = 5;//允许错误的次数
    public static int resendtimes = 1000 * 60 * 2;//8分钟之后重新获取
    public static String googleMcode = "googleMcode_";//谷歌认证时保存手机短信的键值
    public static String loginLock = "_loginLock"; //二次验证登录锁定
    public static String loginLockTrue = "1";    //上锁状态


    /**
     * @param code
     * @param  传入类型，类型必须一致
     * @return
     */
    protected boolean isCorrect(String code) {
        return isCorrect(code, 0, JSON);
    }

    /**
     * @param code
     * @param type 传入类型，类型必须一致
     * @return
     */
    protected boolean isCorrect(String code, int type) {
        return isCorrect(code, type, JSON);
    }

    private boolean needGet(String checkCode, int type) {
        if (checkCode == null || "".equals(checkCode)) {
            return true;
        }
        if (type > 0) {
            String[] checkCodes = checkCode.split("\\_");
            int checkType = Integer.parseInt(checkCodes[0]);
            if (checkType != type) {
                return true;
            }
        }
        return false;
    }

    private boolean isError(String checkCode, String code, int type) {
        if (type == 0) {
            code = UserUtil.secretMethod(code + ip());
            checkCode = checkCode.substring(checkCode.lastIndexOf("_") + 1, checkCode.length());
            if (!checkCode.equals(code)) {
                return true;
            }
        } else {
            code = UserUtil.secretMobileCode(type, ip(), code);
            if (!checkCode.equals(code)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isCorrect(String code, int type, String gs) {
        initLoginUser();
        String checkCode = loginUser.getUserContact().getMobileCode();

        if (code == null || "".equals(code)) {
            if (gs.equals(XML))
                WriteError(L("请输入发送到您手机上的短信验证码。"));
            else
                json(L("请输入发送到您手机上的短信验证码。"), false, "");

            return false;
        }

        if (needGet(checkCode, type)) {
            if (gs.equals(XML))
                WriteError(L("请获取短信验证码。"));
            else
                json(L("请获取短信验证码。"), false, "");

            return false;
        }

        String key = loginUser.getId() + "_mce";
        if (isError(checkCode, code, type)) {
            lock2Hours(loginUser.getId(), gs, key);
            return false;
        } else {
            Object current = Cache.GetObj(key);
            if (current != null) {
                int count = Integer.parseInt(current.toString());
                if (count >= times) {
                    if (gs.equals(XML))
                        WriteError(L("您连续输入错误的次数太多，请2小时后再试。"));
                    else
                        json(L("您连续输入错误的次数太多，请2小时后再试。"), false, "");
                    return false;
                }
                Cache.Delete(key);
            }
        }
        return true;
    }

    protected boolean couldResend() {
        initLoginUser();

        Timestamp codeTime = loginUser.getUserContact().getCodeTime();
        if (codeTime != null && System.currentTimeMillis() - codeTime.getTime() < resendtimes) {//验证码发送超过8分钟之后才重新发送
            long minute = 2 - (System.currentTimeMillis() - codeTime.getTime()) / (1000 * 60);
            json(L("%%分钟之后再重新获取", minute + ""), false, "");
            return false;
        }
        return true;
    }

    protected boolean hasEffective() {
        return hasEffective(JSON);
    }

    protected boolean hasEffective(String gs) {
        initLoginUser();
        Timestamp codeTime = loginUser.getUserContact().getCodeTime();
        if (codeTime != null && System.currentTimeMillis() - codeTime.getTime() > 60000 * 20) {//验证码发送超过10分钟失效
            if (gs.equals(XML))
                WriteError(L("验证码失效，请重新发送验证码。"));
            else
                json(L("验证码失效，请重新发送验证码。"), false, "");
            return false;
        }
        return true;
    }

    @Override
    public void lock2Hours(String userId, String gs, String key) {
        Object current =null;
        current = Cache.GetObj(key);
        String des = L("短信验证码输入有误，");
        if (key.endsWith("gauth")) {
            des = L("谷歌验证码输入有误，");
        }
        if (current == null) {
            current=0;
        }
        int count = Integer.parseInt(current.toString());
        count++;
        Cache.Set(key, String.valueOf(count), 60 * 60 * 2);
        if (count < times) {
            if (gs.equals(XML)) {
                if((times - count) > 1){
                    WriteError(des + L("您还有%%次机会s", (times - count) + ""));
                }else{
                    WriteError(des + L("您还有%%次机会。", (times - count) + ""));
                }

            } else {
                if((times - count) > 1){
                    json(des + L("您还有%%次机会s", (times - count) + ""), false, "");
                }else{
                    json(des + L("您还有%%次机会。", (times - count) + ""), false, "");
                }
            }
        } else {
            if (gs.equals(XML)) {
                WriteError(L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"));
            } else {
                json(L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"), false, "");
            }
        }
    }



    public String lock2HoursStr(String userId, String gs, String key) {
        String current = Cache.Get(key);
        //统一提示验证码输入有误，不区分短信和谷歌
        String des = L("验证码输入有误，");
//        String des = L("短信验证码输入有误，");
//        if (key.endsWith("gauth") || key.startsWith("LoginGoogleError")) {
//            des = L("谷歌验证码输入有误，");
//        }
        if (StringUtils.isEmpty(current)) {
            //比如记录错误登录次数，那么如果正确登录就不需要了
            Cache.Set(key,"0_"+System.currentTimeMillis());
            current="0";
        }else{
            current = current.toString().split("_")[0];
        }
        int count = Integer.parseInt(current.toString());
        count++;
        Cache.Set(key, String.valueOf(count)+"_"+System.currentTimeMillis(), 60 * 60 * 2);
        if (count < times) {
            if (gs.equals(XML)) {
                if((times - count) > 1){
                    WriteError(des + L("您还有%%次机会s", (times - count) + ""));
                }else{
                    WriteError(des + L("您还有%%次机会。", (times - count) + ""));
                }
            } else {
                if((times - count) > 1){
                    des = des + L("您还有%%次机会s", (times - count) + "");
                }else{
                    des = des + L("您还有%%次机会。", (times - count) + "");
                }
               /* json(des + L("您还有%%次机会", (times - count) + ""), false, "");*/
            }
        } else {
            if (gs.equals(XML)) {
                Cache.Set(loginUser.getUserName()+loginLock,loginLockTrue,2*60*60);
                WriteError(L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"));
            } else {
                Cache.Set(loginUser.getUserName()+loginLock,loginLockTrue,2*60*60);
                des = L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。");
               /* json(L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"), false, "");*/
            }
        }
        return des;
    }


    public String lock2HoursStrTixian(String userId, String gs, String key) {
        Object current =null;
        current = Cache.GetObj(key);
        String des = L("短信验证码输入有误，");
        if (key.endsWith("gauth")) {
            des = L("谷歌验证码输入有误，");
        }
        if (current == null) {
            current=0;
        }
        int count = Integer.parseInt(current.toString());
        count++;
        Cache.Set(key, String.valueOf(count), 60 * 60 * 24);
        if (count < times) {
            if (gs.equals(XML)) {
                if((times - count) > 1){
                    WriteError(des + L("您还有%%次机会s", (times - count) + ""));
                }else{
                    WriteError(des + L("您还有%%次机会。", (times - count) + ""));
                }
            } else {
                if((times - count) > 1){
                    des = des + L("您还有%%次机会s", (times - count) + "");
                }else{
                    des = des + L("您还有%%次机会。", (times - count) + "");
                }
               /* json(des + L("您还有%%次机会", (times - count) + ""), false, "");*/
            }
        } else {
            if (gs.equals(XML)) {
                WriteError(L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"));
            } else {
                des = L("验证码输入错误次数超出限制，锁定该帐户24小时，不得使用提现功能。");
               /* json(L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"), false, "");*/
            }
        }
        return des;
    }








    public boolean locked(String userId) {
        return locked(userId, userId + "_mce", JSON);
    }

    public boolean locked(String userId, String key, String gs) {
        Object current = null;
        current = Cache.GetObj(key);
        if (current != null) {
            int count = Integer.parseInt(current.toString());
            if (count >= times) {
                if (gs.equals(XML))
                    WriteError(L("您连续输入错误的次数太多，请2小时后再试。"));
                else
                    json(L("您连续输入错误的次数太多，请2小时后再试。"), false, "");
                return true;
            }
        }
        return false;
    }


    public boolean lockedDownload(String userId, String key, String gs) {
        Object current = null;
        current = Cache.GetObj(key);
        if (current != null) {
            int count = Integer.parseInt(current.toString());
            if (count >= times) {
                if (gs.equals(XML))
                    WriteError(L("您连续输入错误的次数太多，请24小时后再试。"));
                else
                   /* json(L("您连续输入错误的次数太多，请2小时后再试。"), false, "");*/
                return true;
            }
        }
        return false;
    }








    public boolean lockedLogin(String userId, String key, String gs) {
        String current = Cache.Get(key);
        if (StringUtils.isNotEmpty(current)) {
//            int count = Integer.parseInt(current.toString());
            int count= Integer.parseInt(current.toString().split("_")[0]);
            if (count >= times) {
                if (gs.equals(XML)){
                    WriteError(L("您连续输入错误的次数太多，请2小时后再试。"));
                }else{
                    //json(L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"), false, "");
                    Cache.Set(loginUser.getUserName() + loginLock, loginLockTrue, 2 * 60 * 60);
                }
                return true;
            }
        }
        return false;
    }

    public String lockedLoginMsg(String userId, String key, String gs,String errorMsg) {
        Object current = null;
        current = Cache.GetObj(key);
        if (current != null) {
            int count = Integer.parseInt(current.toString());
            if (count >= times) {
                if (gs.equals(XML))
                    WriteError(L("您连续输入错误的次数太多，请2小时后再试。"));
                else
                    errorMsg = CommonUtil.mapToJsonStr(errorMsg, "gcode", L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"));
                Cache.Set(loginUser.getUserName()+loginLock,loginLockTrue,2*60*60);
            }
        }
        return errorMsg;
    }



    //判断移动设备验证码是否正确
    protected boolean isCorrect(String savedSecret, long code, String gs) {
        initLoginUser();

        if (code == 0) {
            if (gs.equals(XML))
                WriteError(L("请输入移动设备上生成的验证码。"));
            else
                json(L("请输入移动设备上生成的验证码。"), false, "");

            return false;
        }

        String key = loginUser.getId() + "_gauth";
        if (locked(loginUser.getId(), key, gs)) {
            return false;
        }

        long t = System.currentTimeMillis();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(3);

        boolean r = ga.check_code(savedSecret, code, t);
        if (!r) {
            lock2Hours(loginUser.getId(), gs, key);
            return false;
        } else {
            Object current = Cache.GetObj(key);
            if (current != null) {
                int count = Integer.parseInt(current.toString());
                if (count >= times) {
                    if (gs.equals(XML)) {
                        WriteError(L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"));
                    } else {
                        json(L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"), false, "");
                    }
                    return false;
                }
                Cache.Delete(key);
            }
        }
        return true;
    }




    //判断移动设备验证码是否正确
    protected Message isCorrectLoginMsg(String savedSecret, long code, String gs,String errorMsg) {
        initLoginUser();
        Message msg = new Message();
        msg.setMsg("");
//        String key = loginUser.getId() + "_gauth";
        String key = LimitType.LoginGoogleError.toString()+"_"+loginUser.getId();
        if (lockedLogin(loginUser.getId(), key, gs)) {
            errorMsg = CommonUtil.mapToJsonStr(errorMsg, "gcode",L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"));
        }

        long t = System.currentTimeMillis();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(3);

        boolean r = ga.check_code(savedSecret, code, t);
        if (!r) {
 /*           lock2Hours(loginUser.getId(), gs, key);
            return false;*/
            String des = lock2HoursStr(loginUser.getUserName(), gs, key);
            errorMsg = CommonUtil.mapToJsonStr(errorMsg,"codegoogle",des);
        } else {
            String current = Cache.Get(key);
            if (StringUtils.isNotEmpty(current)) {
                int count = Integer.parseInt(current.toString().split("_")[0]);
                if (count >= times) {
                    if (gs.equals(XML)) {
                        WriteError(L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"));
                    } else {
                       // json(L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"), false, "");
                        errorMsg = CommonUtil.mapToJsonStr(errorMsg, "gcode", L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"));
                        Cache.Set(loginUser.getUserName()+loginLock,loginLockTrue,2*60*60);
                    }
                   /* return false;*/
                }
                Cache.Delete(key);
            }
        }
        if(null != errorMsg){
            msg.setMsg(errorMsg);
            return msg;
        }
        msg.setSuc(true);
        return msg;
    }


    //判断移动设备验证码是否正确(未登录状态)
    protected Message isCorrectMsg(String savedSecret, long code, String gs, String userId,String errorMsg) {
        Message msg = new Message();
        msg.setMsg("");
        initLoginUser();
        String key = userId + "_gauth";
        if (lockedDownload(userId, key, gs)) {
            errorMsg = L("验证码输入错误次数超出限制，锁定该帐户24小时，不得使用提现功能。");
           /* msg.setMsg(errorMsg);*/
        }
        long t = System.currentTimeMillis();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(3);

        boolean r = ga.check_code(savedSecret, code, t);
        if (!r) {
            String des = lock2HoursStrTixian(userId, gs, key);
            errorMsg = des;
        } else {
            Object current = Cache.GetObj(key);
            if (current != null) {
                int count = Integer.parseInt(current.toString());
                if (count >= times) {
                    if (gs.equals(XML))
                        WriteError(L("您连续输入错误的次数太多，请24小时后再试。"));
                    else
                        errorMsg = L("验证码输入错误次数超出限制，锁定该帐户24小时，不得使用提现功能。");
                       /* json(, false, "");*/
                }
                Cache.Delete(key);
            }
        }
        if(null != errorMsg){
            msg.setMsg(errorMsg);
            return msg;
        }
        msg.setSuc(true);
        return msg;
    }




    //判断移动设备验证码是否正确
    protected boolean isCorrectLogin(String savedSecret, long code, String gs) {
        initLoginUser();

        if (code == 0) {
            if (gs.equals(XML))
                WriteError(L("请输入移动设备上生成的验证码。"));
            else
                json(L("请输入移动设备上生成的验证码。"), false, "");

            return false;
        }

        String key = loginUser.getId() + "_gauth";
        if (lockedLogin(loginUser.getId(), key, gs)) {
            return false;
        }

        long t = System.currentTimeMillis();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(3);

        boolean r = ga.check_code(savedSecret, code, t);
        if (!r) {
            lock2Hours(loginUser.getId(), gs, key);
            return false;
        } else {
            Object current = Cache.GetObj(key);
            if (current != null) {
                int count = Integer.parseInt(current.toString());
                if (count >= times) {
                    if (gs.equals(XML)) {
                        WriteError(L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"));
                    } else {
                        json(L("验证码输入次数超出限制，将锁定登录功能，请2小时之后再试。"), false, "");
                        Cache.Set(loginUser.getUserName()+loginLock,loginLockTrue,2*60*60);
                    }
                    return false;
                }
                Cache.Delete(key);
            }
        }
        return true;
    }






    //判断移动设备验证码是否正确(未登录状态)
    protected boolean isCorrect(String savedSecret, long code, String gs, String userId) {
        initLoginUser();
        if (code == 0) {
            if (gs.equals(XML))
                WriteError(L("请输入移动设备上生成的验证码。"));
            else
                json(L("请输入移动设备上生成的验证码。"), false, "");

            return false;
        }
        String key = userId + "_gauth";
        if (locked(userId, key, gs)) {
            return false;
        }
        long t = System.currentTimeMillis();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(3);

        boolean r = ga.check_code(savedSecret, code, t);
        if (!r) {
            lock2Hours(userId, gs, key);
            return false;
        } else {
            Object current = Cache.GetObj(key);
            if (current != null) {
                int count = Integer.parseInt(current.toString());
                if (count >= times) {
                    if (gs.equals(XML))
                        WriteError(L("您连续输入错误的次数太多，请2小时后再试。"));
                    else
                        json(L("您连续输入错误的次数太多，请2小时后再试。"), false, "");
                    return false;
                }
                Cache.Delete(key);
            }
        }
        json("", true, "");
        return true;
    }






    public static void clearMobile(String userId) {
        Cache.Delete(userId + "_mce");
    }

    public static void clearGoogle(String userId) {
        Cache.Delete(userId + "_gauth");
    }

    public boolean codeIsCorrect(String gs) {
        return codeIsCorrect(gs, 0);
    }

    public boolean codeIsCorrect(String gs, int type) {
        initLoginUser();
        UserContact uc = loginUser.getUserContact();
        if (uc.getMobileStatu() != AuditStatus.pass.getKey()) {
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

        if (uc.getGoogleAu() == AuditStatus.pass.getKey()) {
            String savedSecret = uc.getSecret();
            long code = longParam("code");

            if (!isCorrect(savedSecret, code, gs)) {
                return false;
            }

        }
        return true;
    }

    /**
     * 通过类型指定GOOGLE还是短信验证,在GOOGLE验证未开启的情况下验证短信.
     *
     * @param gs
     * @param type
     * @return
     */
    public boolean codeIsCorrect(String gs, int type, int codeType) {
        initLoginUser();
        UserContact uc = loginUser.getUserContact();
        if (type == 1) {
            if (uc.getMobileStatu() != AuditStatus.pass.getKey()) {
                WriteError(L("您还没有通过手机认证，请先进行手机认证。"));
                return false;
            }

            String mCode = param("mCode");

            if (!isCorrect(mCode, codeType, gs)) {// json(L("验证码错误"), false, "");
                return false;
            }

            if (!hasEffective(gs)) {// json("验证码失效，请重新发送验证码。", false, "");
                return false;
            }
        }
        if (type == 2) {
            if (uc.getGoogleAu() == AuditStatus.pass.getKey()) {
                String savedSecret = uc.getSecret();
                long code = longParam("code");

                if (!isCorrect(savedSecret, code, gs)) {
                    return false;
                }

            } else {
                if (uc.getMobileStatu() != AuditStatus.pass.getKey()) {
                    WriteError(L("您还没有通过手机认证，请先进行手机认证。"));
                    return false;
                }

                String mCode = param("mCode");

                if (!isCorrect(mCode, codeType, gs)) {// json(L("验证码错误"), false, "");
                    return false;
                }

                if (!hasEffective(gs)) {// json("验证码失效，请重新发送验证码。", false, "");
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Toast 提示
     * @param errMsg 提示信息
     */
    protected void toast(String errMsg) {
        json(errMsg, false, "");
    }

    /**
     * Toast 提示
     * @param errMsg 提示信息
     */
    protected void toast(String errMsg,Boolean flag) {
        json(errMsg, flag, "");
    }

    /**
     * 文本框错误
     * @param textViewName
     * @param errMsg
     */
    protected void textViewErr(String textViewName, String errMsg) {
        JSONObject data = new JSONObject();
        data.put(textViewName, errMsg);
        json(StringUtils.EMPTY, false, data.toJSONString());
    }

    /**
     * 是否存在文本错误
     * @return
     */
    protected boolean hasTextViewErr() {
        return !textViewErr.isEmpty();
    }

    /**
     * 文本框错误
     */
    protected void textViewErr() {
        json(StringUtils.EMPTY, false, textViewErr.toJSONString());
        clearTextViewErr();
    }

    /**
     * 重定向
     * @param url
     */
    protected void redirect(String url) {
        json(url, false, redirectData.toJSONString());
    }

    /**
     * 重定向
     * @param url
     */
    protected void redirectTrue(String url) {
        json(url, true, redirectData.toJSONString());
    }


    private JSONObject textViewErr = new JSONObject();
    private static JSONObject redirectData = new JSONObject();
    static {
        redirectData.put("isRoute", true);
    }

    /**
     * 清空
     */
    protected void clearTextViewErr() {
        textViewErr.clear();
    }

    /**
     * 添加
     * @param textViewName
     * @param errMsg
     * @return
     */
    protected void addTextViewErr(String textViewName, String errMsg) {
        textViewErr.put(textViewName, errMsg);
    }

    /**
     * 成功
     */
    protected void success() {
        success("");
    }

    /**
     * 成功
     * @param data
     */
    protected void success(String data) {
        json("", true, data);
    }

    /**
     * 成功並且返回token
     * @param data
     */
    protected void successToken(String data,String token) {
        json(token, true, data);
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
        errorMessage = errorMessage.replace("谷歌验证码输入有误","验证码输入有误");
        errorMessage = errorMessage.replace("短信验证码输入有误","验证码输入有误");
        errorMessage = errorMessage.replace("邮箱验证码输入有误","验证码输入有误");

        //格式化次数
        String[] errorMsg = errorMessage.split("#");
        dr.setDes(Lan.LanguageFormat(lan, errorMsg[0], errorMsg[1]));
        return dr;
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

    /**
     * check手机或邮箱验证码
     * @param user
     * @param pct
     * @param sendType 1-邮箱验证，2-手机验证
     * @param code
     * @return
     */
    public DataResponse getDataResponse(User user, PostCodeType pct, String sendNum, int sendType, String code, LimitType lt, String functionName, String lockTime) {
        DataResponse dr = null;
        String userIp = ip();
        PostCodeType postCodeType = (PostCodeType) EnumUtils.getEnumByKey(pct.getKey(), PostCodeType.class);
        String codeType = postCodeType.getValue();
        if(sendType == 1){
            ClientSession clientSession = new ClientSession(userIp, sendNum, lan, codeType, false);
            dr = clientSession.checkCodeMailNew(user.getId(),code,lt,functionName,lockTime);
        }else{
            ClientSession clientSession = new ClientSession(userIp, sendNum, lan, codeType, false);
            dr = clientSession.checkCodeNew(user.getId(),code,lt,functionName,lockTime);
        }
        EmailDao eDao = new EmailDao();
        String info = "";
        String title = "";
        if("cn".equals(lan)){
            if(dr.getDes().contains("验证码输入次数超出限制")){
                title = L(functionName);
                info = eDao.getWrongLimitEmailHtml(user, dr.getDes(), this);
                //锁定发送邮件
                eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, user.getUserContact().getSafeEmail());
            }
        }else if("en".equals(lan)){
            if(dr.getDes().contains("Code input error too many times in a row")){
                title =  L(functionName);
                info = eDao.getWrongLimitEmailHtml(user, dr.getDes(), this);
                //锁定发送邮件
                eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, user.getUserContact().getSafeEmail());
            }
        }else if("hk".equals(lan)){
            if(dr.getDes().contains("驗證碼輸入次數超出限制")){
                title = L(functionName);
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
}

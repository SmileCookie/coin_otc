package com.world.model.entity;

/**
 * <p>@Description: Cache key</p>
 *
 * @author Sue
 * @date 2018/3/8下午10:13
 */
public class CacheKeys {

    /**
     * 是否开启了手机验证
     * @param uid
     * @return
     */
    public static String getIsSmsOpenKey(String uid){
        return "smsOpen_user_" + uid;
    }

    /**
     * 是否开启了谷歌验证
     * @param uid
     * @return
     */
    public static String getIsGoogleOpenKey(String uid){
        return "googleOpen_user_" + uid;
    }

    /**
     * 用户信息
     * @param uid
     * @return
     */
    public static String getUserInfoKey(String uid){
        return "user_info_" + uid;
    }

    /**
     * 用户锁定的功能
     * @param uid
     * @param func
     * @return
     */
    public static String getFunctionLockKey(String uid,String func){
        return "user_" + uid + "_lock_func_" + func;
    }

    /**
     * 系统配置key
     * @param key
     * @return
     */
    public static String getSysConfigKey(String key){
        return "sys:config:" + key;
    }

    /**
     * 用户token
     * @param userId
     * @return
     */
    public static String getUserIdKey(String userId) {
        return "user:" + userId + ":token";
    }

    /**
     * 用户消息
     * @param channel   app或者web
     * @param userId    用户ID
     * @return
     */
    public static String getUserMsgKey(int channel, String userId) {
        return "user:" + userId + ":channel:" + channel;
    }

    /**
     * 用户Session
     * @param uid
     * @param sessionId
     * @return
     */
    public static String getUserSessionKey(String uid, String sessionId) {
        return "user:" + uid + ":session:" + sessionId;
    }

    /**
     * 用户短信
     * @param mobile
     * @param type
     * @return
     */
    public static String getUserSmsKey(String mobile, int type) {
        return "mobile:" + mobile + ":sms:" + type;
    }

    /**
     * 用户短信发送数量（24小时）
     * @param mobile    手机号
     * @param mode      模式：1-主动，0-被动
     * @return
     */
    public static String getUserSmsNumKey(String mobile, int mode) {
        return "mobile:" + mobile + ":num";
    }

    /**
     * 用户发送短信频率记录，60s可以发一次
     * @param mobile
     * @return
     */
    public static String getUserSmsCycle(String mobile) {
        return "mobile:" + mobile + ":cycle";
    }

    /**
     * 用户邮件
     * @param email
     * @param type
     * @return
     */
    public static String getUserEmailSmsKey(String email, int type) {
        return "email:" + email.toLowerCase() + ":sms:" + type;
    }

    /**
     * 用户发送邮件频率记录，60s可以发一次
     * @param email
     * @return
     */
    public static String getUserEmailSmsCycle(String email) {
        return "email:" + email + ":cycle";
    }

    /**
     * 获取邮箱验证码key
     * @param uid
     * @return
     */
    public static String getEmailCodeKey(String uid) {
        return "user:" + uid + ":emailCode:update:login:password";
    }

    /**
     * 获取交易邮箱验证码key
     * @param uid
     * @return
     */
    public static String getTradeEmailCodeKey(String uid) {
        return "user:" + uid + ":emailCode:update:trade:password";
    }

    /**
     * 获取忘记密码邮箱验证码key
     * @param uid
     * @return
     */
    public static String getForgetEmailCodeKey(String uid) {
        return "user:" + uid + ":emailCode:forget:login:password";
    }

    /**
     * 无二次验证邮箱验证码key
     * @param uid
     * @return
     */
    public static String getNoVerifyEmailCodeKey(String uid) {
        return "user:" + uid + ":emailCode:noverify:login:password";
    }

    /**
     * 获取异常订单聊天记录key
     * @param id
     * @return
     */
    public static String getErrorRecordChatKey(long id) {
        return "error_record_complain_"+id+"";
    }
    /**
     * 获取异常订单发送短信记录key
     * @param id
     * @return
     */
    public static String getErrorRecordSendSmsKey(long id) {
        return "error_record_send_sms_"+id+"";
    }

    /**
     * 获取手机验证码key
     * @param uid
     * @return
     */
    public static String getMobileCodeKey(String uid) {
        return "user:" + uid + ":mobileCode:update:password";
    }

    /**
     * 获取谷歌密钥key
     * @param uid
     * @return
     */
    public static String getGoogleSecretKey(String uid) {
        return "user:" + uid + ":google:secrect:key";
    }

}

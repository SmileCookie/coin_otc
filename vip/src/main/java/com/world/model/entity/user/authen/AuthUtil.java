package com.world.model.entity.user.authen;

import static com.world.model.entity.AuditStatus.a1NoAudite;
import static com.world.model.entity.AuditStatus.a1Pass;
import static com.world.model.entity.AuditStatus.noAudite;
import static com.world.model.entity.AuditStatus.noPass;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.Lan;
import com.alibaba.fastjson.JSONObject;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.world.cache.Cache;
import com.world.constant.Const;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.authen.AuthenHistoryDao;
import com.world.model.dao.user.authen.AuthenLogDao;
import com.world.model.dao.user.authen.AuthenticationDao;
import com.world.model.dao.user.authen.IdcardBlackListDao;
import com.world.model.dao.user.authen.IdcardDao;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.user.User;
import com.world.util.Message;
import com.world.util.date.TimeUtil;
import com.world.util.request.HttpUtil;

/**
 */
public final class AuthUtil {

    static Logger log = Logger.getLogger(AuthUtil.class.getName());
    
    private static final String APPKEY = "";
	private static final String OUTPUT = "json";

	
	/**
	 * 使用本接口可快速查询身份证号码与姓名是否一致，
	 * 经连接官方身份证中心联网核查后输出结果，
	 * 结果分为三种：一致、不一致、无此身份证号码。
	 * 
	 * 返回json
	 *  err	是否符合身份证号码格式 0：符合 ， -1：不符合
		address	unicode格式身份证所在地 （err:-1时无此结果）
		sex	性别（err:-1时无此结果） M：男性 ， F：女性
		birthday	生日信息（err:-1时无此结果）
	 */
	public static Idcard getIdcard(String name, String cardno) {
		Idcard idcard = null;
		try {
			String url = "http://api.id98.cn/api/idcard";
			Map<String, String> params = new HashMap<String, String>();
			params.put("appkey", APPKEY);
			params.put("name", name);
			params.put("cardno", cardno);
			params.put("output", OUTPUT);
			JSONObject json = HttpUtil.getJson(url, params, 10000, 10000, false);
//			JSONObject json = JSONObject.parseObject("{\"code\":1,\"data\":{\"address\":\"广东省中山市中山市\",\"birthday\":\"1987-05-05\",\"err\":0,\"sex\":\"M\"},\"isok\":1}");
			log.info(json.toJSONString());
			if (null != json && json.size() > 0) {
				if (json.containsKey("isok") && json.getInteger("isok") == 1
					&& json.containsKey("code") && json.getInteger("code") == 1
					&& json.containsKey("data")) {
					idcard = new Idcard();
					idcard.setName(name);
					idcard.setCardno(cardno);
					idcard.setAddress(json.getJSONObject("data").getString("address"));
					idcard.setSex(json.getJSONObject("data").getString("sex"));
					idcard.setBirthday(json.getJSONObject("data").getString("birthday"));
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return idcard;
	}
	
	/**
	 * 使用本接口可快速查询身份证号码与姓名是否一致，
	 * 经连接官方身份证中心联网核查后输出结果，
	 * 结果分为三种：一致、不一致、无此身份证号码。
	 * 
	 * 返回json
	 *  err	是否符合身份证号码格式 0：符合 ， -1：不符合
		address	unicode格式身份证所在地 （err:-1时无此结果）
		sex	性别（err:-1时无此结果） M：男性 ， F：女性
		birthday	生日信息（err:-1时无此结果）
	 */
	public static Idcard getIdcardPhoto(String name, String cardno) {
		Idcard idcard = null;
		try {
			String url = "http://api.id98.cn/api/idcard-photo";
			Map<String, String> params = new HashMap<String, String>();
			params.put("appkey", APPKEY);
			params.put("name", name);
			params.put("cardno", cardno);
			params.put("output", OUTPUT);
			JSONObject json = HttpUtil.getJson(url, params, 10000, 10000, false);
//			JSONObject json = JSONObject.parseObject("{\"code\":1,\"data\":{\"address\":\"广东省中山市中山市\",\"birthday\":\"1987-05-05\",\"err\":0,\"sex\":\"M\"},\"isok\":1,\"photo\":\"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\"}");
			log.info(json.toJSONString());
			if (null != json && json.size() > 0) {
				if (json.containsKey("isok") && json.getInteger("isok") == 1
						&& json.containsKey("code") && json.getInteger("code") == 1
						&& json.containsKey("data") && json.containsKey("photo")) {
					idcard = new Idcard();
					idcard.setName(name);
					idcard.setCardno(cardno);
					idcard.setAddress(json.getJSONObject("data").getString("address"));
					idcard.setSex(json.getJSONObject("data").getString("sex"));
					idcard.setBirthday(json.getJSONObject("data").getString("birthday"));
					idcard.setPhoto(json.getString("photo"));
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return idcard;
	}

    private AuthUtil() {
    }

    public static String getMsgAndRecordFailTimes(UserDao userDao, User user, String lan, String msg, boolean isIncrFailAuthTimes) {
    	int failAuthTimes = user.getFailAuthTimes();
    	if(isIncrFailAuthTimes){
    		failAuthTimes ++;
    		userDao.increaseValue(user.getId(), "failAuthTimes");
    	}
        return Lan.LanguageFormat(lan, msg, "") + Lan.LanguageFormat(lan, "，", "") +
                Lan.LanguageFormat(lan, "认证错误%%次。", String.valueOf(failAuthTimes));
    }

    public static String overTimsInADayTip(User user, UserDao userDao, String lan) {
        String cacheKey = "simple_auth_" + user.getId();
        int authTimes = 1;
        if (null != Cache.GetObj(cacheKey)) {
            authTimes = (Integer) Cache.GetObj(cacheKey) + 1;
            if (authTimes > 2) {
                return getMsgAndRecordFailTimes(userDao, user, lan, "您提交实名认证操作过于频繁，请明天再试", false);
            }
        }
        Cache.SetObj(cacheKey, authTimes, 60 * 60 * 24);

        return null;
    }

    public static void deleteOverTimsInADayLimit(String userId) {
        String cacheKey = "simple_auth_" + userId;
        Cache.Delete(cacheKey);
    }

    /**
     * 提交个人用户初级实名认证
     * @param loginUser
     * @param lan
     * @param request
     * @param ip
     * @return
     */
    public static Message saveSimpleIndividualAuth(User loginUser, String lan, HttpServletRequest request, String ip) {
        Message msg = new Message();
        if (loginUser.getFailAuthTimes() > Const.MAX_AUTH_FAIL_TIMES) {
            msg.setMsg(Lan.LanguageFormat(lan, "对不起，由于您的不正当操作，导致账号无法实名认证，如需认证，请联系人工客服处理。", ""));
            return msg;
        }

        UserDao userDao = new UserDao();

        String realName = request.getParameter("realName");
        String country = request.getParameter("country");
        String cardId = request.getParameter("cardId").toLowerCase();
        if (StringUtils.isBlank(realName)) {
            msg.setMsg(Lan.LanguageFormat(lan, "请填写您的真实姓名", ""));
            return msg;
        }
        if (StringUtils.isBlank(cardId)) {
            msg.setMsg(Lan.LanguageFormat(lan, "请填写有效的身份证号码", ""));
            return msg;
        }
        if (StringUtils.isBlank(country)) {
            msg.setMsg(Lan.LanguageFormat(lan, "请选择证件所属国家", ""));
            return msg;
        }

        String tipMsg = overTimsInADayTip(loginUser, userDao, lan);
        if (tipMsg != null) {
        	msg.setMsg(tipMsg);
        	return msg;
        }
        
        String userId = loginUser.getId();

        // 更新认证总次数
        userDao.increaseValue(userId, "authTimes");

        AuthenticationDao auDao = new AuthenticationDao();
        
        // 实名认证时，如果该身份已经认证过，则抹除该账号的推荐人关系。
        if (auDao.isExistsIdcard(cardId)) {
            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("recommendId", "");
            ops.set("recommendName", "");
            userDao.update(q, ops);
        }
        
        
        if (auDao.isLimit(cardId, userId)) {
            msg.setMsg(getMsgAndRecordFailTimes(userDao, loginUser, lan, "您的证件申请实名认证次数超过限制，无法通过", true));
            return msg;
        }

        IdcardBlackListDao idcardBlackListDao = new IdcardBlackListDao();
        boolean isIdcardBlackList = idcardBlackListDao.isBlackList(cardId);
        int area = getArea(country);
        if (!isIdcardBlackList) {
            boolean isPass = false, isDalu = true;
            IdcardDao idcardDao = new IdcardDao();
            if (area == AreaInfo.dalu.getKey()) {
                isPass = true;//idcardDao.validIdcard(realName, cardId);
            } else {
                isPass = true;
                isDalu = false;
            }
            /*start by xwz 2017-06-09*/
            if (!isPass && isDalu) {
//                msg.setMsg(getMsgAndRecordFailTimes(userDao, loginUser, lan, "证件信息验证不通过，请填写真实证件信息后重新提交认证", true));
//                return msg;
            }
            /*end*/

            AuthenLogDao logDao = new AuthenLogDao();
            logDao.insertOneRecord(userId, "0", isDalu ? "个人用户：查询成功，比对一致，通过初级认证。" : "个人用户：非大陆身份证自动通过初级认证。", ip, TimeUtil.getNow());
        }

        Authentication au = new Authentication(auDao.getDatastore());
        au.setRealName(realName);
        au.setCardId(cardId);
        au.setAreaInfo(area);
        au.setIp(ip);
        au.setSubmitTime(TimeUtil.getNow());
        //au.setAreaInfo(AreaInfo.dalu.getKey());
        au.setUserId(userId);
        au.setCountryCode(country);

        au.setServiceStatu(3);// 比对一致
        au.setImgCode("");
        au.setPhoto("");
        au.setSimplePass(!isIdcardBlackList);
        au.setCardIdBlackList(isIdcardBlackList);
        au.setAuthType(1);

//      au.setStatus((isIdcardBlackList ? AuditStatus.a1NoAudite : AuditStatus.a1Pass).getKey());
        /*start by xwz*/
        au.setStatus((isIdcardBlackList ? AuditStatus.a1NoPass : AuditStatus.a1Pass).getKey());
        /*end*/

        if (!auDao.updateAuth(au).getHadError()) {
            AuthenHistoryDao ahDao = new AuthenHistoryDao();
            try {
                AuthenHistory ah = ahDao.getAuthHis(au);
                ahDao.save(ah);
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
//				String msg = isDalu?"初级认证成功。":"提交初级实名认证成功，请等待客服审核";
            String msgStr = null;
            if (isIdcardBlackList) {
                msgStr = "提交初级实名认证成功，请等待客服审核。";
            } else {
                msgStr = "初级认证成功。";
            }

            deleteOverTimsInADayLimit(userId);

            userDao.updateRealNameAndAuthType(userId, realName, AuditType.individual.getKey(), true);

            msg.setSuc(true);
            msg.setMsg(Lan.LanguageFormat(lan, msgStr, ""));
        } else {
            msg.setMsg(Lan.LanguageFormat(lan, "保存失败。", ""));
        }

        return msg;
    }

    public static int getArea(String country) {
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

    /**
     * 验证身份证
     * @param userId
     * @param cardId
     * @return
     */
    public static Message validateCardId(String userId, String cardId){
        Message msg = new Message();
        AuthenticationDao auDao = new AuthenticationDao();
        Authentication au = auDao.getByUserId(userId);
        if (null != au && (au.getStatus() == 2 || au.getStatus() == 6)) {
            if (StringUtils.isBlank(cardId)) {
                msg.setMsg("您的账户已通过实名认证，必须填写证件号。");
                return msg;
            }
            if ((au.getType() == AuditType.individual.getKey() && !cardId.equalsIgnoreCase(au.getCardId()))
                    || (au.getType() == AuditType.corporate.getKey() && !cardId.equalsIgnoreCase(au.getEnterpriseRegisterNo()))) {
                msg.setMsg("证件号验证不通过，请重新填写。");
                return msg;
            }
        }
        msg.setSuc(true);
        return msg;
    }

    /**
     * 是否待实名审核中或需要实名
     * @param limitStatus
     * @return
     */
    public static boolean isInOrNeedAuth(int limitStatus){
        return limitStatus >= -5 && limitStatus <= -2;
    }

    /**
     * 获取实名认证结果
     * @param au        实名认证信息
     * @param total     总金额
     * @param a1Money   初级实名认证额度a1    （初级通过，则允许的额度是 < a2）
     * @param a2Money   高级实名认证的额度a2
     * @return 1:当前额度不限,-2初级实名,-3高级实名, -4初级认证审核中, -5高级认证审核中
     */
    public static int getAuthResult(Authentication au, double total, double a1Money, double a2Money){
        // 小于a1和通过的高级认证的忽略
        if( (total >= a1Money && (au == null ||
                (!au.isDepthPass() && au.getStatus() != noAudite.getKey() && au.getStatus() != a1NoAudite.getKey()) )) ) {
            // 大于等于A2额度，或者处于高级认证-不通过或高级认证-未提交
            if ( (total >= a2Money && ( au == null || !au.isDepthPass() )) ||
                    (au != null && total >= a1Money && au.getStatus() == noPass.getKey())){
                return -3;  // 提示需要高级实名认证
            }
            // 通过初级且总额度小于a2
            else if(au != null && au.getStatus() == a1Pass.getKey() && total < a2Money){
                return 1;   // 不提示
            }

            if(au != null){
                if(au.getStatus() == a1NoAudite.getKey()){
                    return -4;	// 初级认证审核中
                }

                if(au.getStatus() == noAudite.getKey()){
                    return -5;	// 高级认证审核中
                }
            }

            return -2;      // 提示需要初级实名认证
        }

        if(au != null && total >= a1Money){
            if(au.getStatus() == a1NoAudite.getKey()){
                return -4;	// 初级认证审核中
            }

            if(au.getStatus() == noAudite.getKey()){
                return -5;	// 高级认证审核中
            }
        }

        return 1;
    }
}

package com.world.util;

import static com.world.model.entity.AuditStatus.a1NoAudite;
import static com.world.model.entity.AuditStatus.a1Pass;
import static com.world.model.entity.AuditStatus.noAudite;
import static com.world.model.entity.AuditStatus.noPass;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.Lan;
import com.api.common.SystemCode;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.world.cache.Cache;
import com.world.constant.Const;
import com.world.data.mysql.Data;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.authen.AuthenHistoryDao;
import com.world.model.dao.user.authen.AuthenLogDao;
import com.world.model.dao.user.authen.AuthenticationDao;
import com.world.model.dao.user.authen.IdcardBlackListDao;
import com.world.model.dao.user.authen.IdcardDao;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.pay.BankTradeStatus;
import com.world.model.entity.user.User;
import com.world.model.entity.user.authen.AreaInfo;
import com.world.model.entity.user.authen.AuditType;
import com.world.model.entity.user.authen.AuthenHistory;
import com.world.model.entity.user.authen.Authentication;
import com.world.util.date.TimeUtil;

/**
 * @author Micheal Chao
 * @version v0.1
 *          createTime 2016/7/15 10:10
 */
public final class AuthUtil {

    static Logger log = Logger.getLogger(AuthUtil.class.getName());

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
            if (authTimes > 6) {
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
                isPass = idcardDao.validIdcard(realName, cardId);
            } else {
                isPass = true;
                isDalu = false;
            }
            if (!isPass && isDalu) {
                msg.setMsg(getMsgAndRecordFailTimes(userDao, loginUser, lan, "证件信息验证不通过，请填写真实证件信息后重新提交认证", true));
                return msg;
            }

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
        au.setStatus((isIdcardBlackList ? AuditStatus.a1NoAudite : AuditStatus.a1Pass).getKey());

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
                // 更新充值/提现记录
               /* 认证与充值提现无关了，先注释 zhanglinbo
                * String updateSql1 = "update Bank_Trade set Status=? where Status=? and Is_In=0 and User_Id=?";
                Data.Update(updateSql1,
                        new Object[]{BankTradeStatus.WaitConfirm.getId(), BankTradeStatus.NEEDAUTH.getId(), userId});
                String updateSql2 = "update Bank_Trade set Status=? where Status=? and Is_In=1 and User_Id=?";
                Data.Update(updateSql2, new Object[]{BankTradeStatus.WaitRecharge.getId(),
                        BankTradeStatus.NEEDAUTH.getId(), userId});
                auDao.updateNeedAuth(Integer.parseInt(userId), "1", 0);
                auDao.updateNeedAuth(Integer.parseInt(userId), "2", 0);*/

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
     * 检查实名认证结果
     * @param userId
     * @param authenType
     * @param type
     * @param amount
     * @return
     */
    public static Message verify(int userId, int authenType, int type, double amount){
        Message message = new Message();
       /* int limitStatus = LimitUtil.moneyLimit(userId, authenType, type, amount);
        if(AuthUtil.isInOrNeedAuth(limitStatus)){
            SystemCode code = null;
            if (limitStatus == -2) {
                code = SystemCode.code_5003;
            }else if(limitStatus == -3){
                code = SystemCode.code_5004;
            }else if(limitStatus == -4){
                code = SystemCode.code_5005;
            }else{
                code = SystemCode.code_5006;
            }

            message.setMsg(code.getValue());
            return message;
        }
        暂时注释 zhanglinbo
*/
        message.setSuc(true);
        return message;
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

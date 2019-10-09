package com.world.model.dao.user;

import com.Lan;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.googleauth.GoogleAuthenticator;
import com.world.cache.Cache;
import com.world.constant.Const;
import com.world.controller.api.util.SystemCode;
import com.world.data.mongo.MongoDao;
import com.world.data.mysql.Data;
import com.world.model.LimitType;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.LegalTenderType;
import com.world.model.entity.Market;
import com.world.model.entity.user.CollectMarket;
import com.world.model.entity.user.LoginAuthenType;
import com.world.model.entity.user.SafeLevelType;
import com.world.model.entity.user.TradeAuthenType;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.model.entity.user.UserScreen;
import com.world.model.entity.user.WithdrawAddressAuthenType;
import com.world.model.entity.user.WithdrawAuthenType;
import com.world.model.entity.user.authen.Authentication;
import com.world.model.enums.LogCategory;
import com.world.rabbitmq.producer.OperateLogInfoProducer;
import com.world.util.CommonUtil;
import com.world.util.Message;
import com.world.util.MsgToastKey;
import com.world.util.UserUtil;
import com.world.util.date.TimeUtil;
import com.world.util.language.LanguageTag;
import com.world.util.language.SafeTipsTag;
import com.world.util.string.KeyWordFilter;
import com.world.util.string.StringUtil;
import com.world.web.action.ApproveAction;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


public class UserDao extends MongoDao<User, String> {
    //	protected UserDao(Datastore ds) {
//		 super(ds);
//	}
    public UserDao() {

    }

    public UserDao(String lan) {
        super.lan = lan;
    }

    Logger logger = Logger.getLogger(UserDao.class);

    public User getUserById(String userId) {
        /*Start by guankaili 20190403 优化查询逻辑 */
        /*Query<User> q = null;

        q = getQuery(User.class).filter("_id =", userId);
        return super.findOne(q);*/
        User user = this.findOne("_id", userId);
        return user;
        /*End*/
    }

    /*start by xwz 增加根据ID获取语言的方法*/
    public String getUserLanguageById(String userId) {
        Query<User> q = null;
        String language = Cache.Get("user_lan_" + userId);
        if (StringUtils.isEmpty(language)) {
            q = getQuery(User.class).filter("_id =", userId);
            language = super.findOne(q).getLanguage();
        } else {
            User user = this.getById(userId);
            Cache.Set("user_lan_" + userId, user.getLanguage());
        }

        if (StringUtils.isNotEmpty(language)) {
            return language;
        }
        return "en";
    }

    /*end*/
    //根据某个字段获取用户
    public User getUserByColumn(String obj, String column) {
        Query<User> q = null;
        q = getQuery(User.class).filter(column, obj);
        q.field("isDeleted").notEqual(true);
//		q.or(
//				q.criteria("isDeleted").equal(null),
//				q.criteria("isDeleted").equal(false)
//		);
        return super.findOne(q);
    }


    /*start by xwz 2017-06-10*/
    //根据某个字段获取用户
    public User getUserByColumnForMobile(String obj, String column) {
        Query<User> q = null;
        Pattern pattern = Pattern.compile(".*" + obj, Pattern.CASE_INSENSITIVE);
        q = getQuery(User.class).filter(column, pattern);
        q.field("isDeleted").notEqual(true);
//		q.or(
//				q.criteria("isDeleted").equal(null),
//				q.criteria("isDeleted").equal(false)
//		);
        return super.findOne(q);
    }

    /*end*/
    /*start by xwz 2017-06-10*/
    //根据某个字段获取用户
    public User getUserByLoginCheckMobile(String loginCheckMobile, String column) {
        Query<User> q = null;
        q = getQuery(User.class).filter(column, loginCheckMobile);
        q.field("isDeleted").notEqual(true);
        return super.findOne(q);
    }

    /*end*/


    public List<User> search(Query<User> q, int pageIndex, int pageSize) {
        q.offset((pageIndex - 1) * pageSize).limit(pageSize);
        return super.find(q).asList();
    }

    //UserName,PassWord,Email,RegisterTime,TuiJianId,TuijianUserName,SafeLevel
    public String addUser(User u) {
        u.setRegisterTime(now());
        /**start by xzhang 20170829 注册用户初始化设置用户为普通用户**/
        u.setCustomerType(Const.CUSTOMER_TYPE_NORMAL);
        u.setCustomerOperation(Const.CUSTOMER_OPERATION_NO_LIMIT);
        /*end*/
        u.setSafeLevel(0);
        u.setLoginAuthenType(getLoginAuthenType(u.isLoginGoogleAuth(), u.isDiffAreaLoginNoCheck()));
        u.setTradeAuthenType(TradeAuthenType.NO_TRADE_PASSWORD.getKey());
        //2017.08.16 xzhang 修改开启关闭谷歌验证提现验证选择项问题 JYPT-974
        u.setWithdrawAuthenType(getWithdrawAuthenType(u.getHasSafePwd(), u.isPayMobileAuth(), u.isPayEmailAuth(), u.isPayGoogleAuth(), u.getUserContact().getGoogleAu()));
        String nid = super.save(u).getId().toString();
//		FeignContainer container = new FeignContainer("http://127.0.0.1:8081/user");
//		UserApiService userApiService = container.getFeignClient(UserApiService.class);
//		UserSaveVo uv = new UserSaveVo();
//		BeanUtils.copyProperties(u, uv);
//		String nid = userApiService.register(uv);
        logger.info("成功添加一条新数据，主键：" + nid);
        return nid;
    }

    //注册成功之后更新用户信息
    public UpdateResults<User> updateUser(User user) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", user.getId());
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
        ops.set("pwd", UserUtil.newSafeSecretMethod(user.getId(), user.getPwd()));
        ops.set("pwdLevel", user.getPwdLevel());
        //ops.set("registerTime", now());

        ops.set("safePwd", UserUtil.newSafeSecretMethod(user.getId(), user.getSafePwd()));
        ops.set("safeLevel", user.getSafeLevel());
        ops.inc("modifyTimes", 1);
        ops.set("pwdModifyTime", now());
        ops.set("userName", user.getUserName());

        if (null != user.getEmail()) {
            ops.set("userContact.safeEmail", user.getEmail());
            ops.set("userContact.emailStatu", AuditStatus.pass.getKey());
            ops.set("userContact.emailTime", new Timestamp(0));
        }
        if (null != user.getUserContact().getSafeMobile()) {
            ops.set("userContact.safeMobile", user.getUserContact().getSafeMobile());
            ops.set("userContact.mobileStatu", AuditStatus.pass.getKey());
        }
        ops.set("userContact.emailCode", "");
        ops.set("userContact.mobileCode", "");
        ops.set("userContact.checkEmail", "");

        /*start by xwz 20170930 增加来源统计*/
        if (StringUtils.isNotEmpty(user.getUtmSource())) {
            ops.set("utmSource", user.getUtmSource());
        }
        if (StringUtils.isNotEmpty(user.getUtmMedium())) {
            ops.set("utmMedium", user.getUtmMedium());
        }
        /*end*/

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    // 更新微信关注状态
    public UpdateResults<User> updateWxUserSubscribe(String userId, boolean subscribe) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", userId);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
        ops.set("weixinSubscribe", subscribe);
        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    //单独更新用户密码的方法
    public UpdateResults<User> updatePwd(String uid, String pwd, int pwdLevel) {
        User u = getById(uid);
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
        ops.set("pwd", UserUtil.newSafeSecretMethod(uid, pwd));
        if (pwdLevel > 0) {
            ops.set("pwdLevel", pwdLevel);
        }
        ops.inc("modifyTimes", 1);
        ops.set("pwdModifyTime", now());
        if ((null == u.getUserName() || "".equals(u.getUserName())) && null != u.getUserContact().getSafeMobile()) {
            ops.set("userName", u.getUserContact().getSafeMobile().replace(" ", "").replace("+86", ""));
        }

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    /****
     * 单独更新用户资金安全密码的方法
     * 修改资金密码
     * @param uid
     * @param securityPwd
     * @param safeLevel
     * @return
     */
    public UpdateResults<User> updateSecurityPwd(String uid, String securityPwd, int safeLevel) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
//        User safeUser = super.findOne(q);

        //return UserUtil.newSafeSecretMethod(myId, pwd);
        //String spwd = safeUser.getSafeEncryptedPwd(uid, securityPwd);
        ops.set("safePwd", UserUtil.newSafeSecretMethod(uid, securityPwd));
        ops.inc("safePwdModifyTimes", 1);
        ops.inc("modifyTimes", 1);
        ops.set("safePwdModifyTime", now());
        if (safeLevel > 0) {
            ops.set("safeLevel", safeLevel);
        }

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    //解除资金安全密码锁定的方法
    public UpdateResults<User> releaseSecurityPwd(String uid) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
        ops.set("safePwdModifyTime", 0);

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    //单独更新用户资金安全密码的方法
    public UpdateResults<User> updateSecurityPwd(String uid, String securityPwd) {
        return updateSecurityPwd(uid, securityPwd, 40);
    }

    //更新，发送手机短信到邮箱的时间
    public UpdateResults<User> updateCodeEmailTime(String uid) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set("userContact.mobileCodeEmailTime", now());

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    //邮箱验证时更新验证码
    public UpdateResults<User> updateEmailCode(String uid, String email, String emailCode) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set("userContact.emailCode", emailCode);

        if (email != null && email.length() > 0) {
            ops.set("userContact.checkEmail", email);
        }
        ops.set("userContact.emailTime", now());

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    //手机绑定时更新验证码
    public UpdateResults<User> updateMobileCode(String uid, String mobile, String mobileCode) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set("userContact.mobileCode", mobileCode);

        if (mobile != null && mobile.length() > 0) {
            ops.set("userContact.checkMobile", mobile);
        }
        ops.set("userContact.codeTime", now());

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    public UpdateResults<User> clearCodeTime(String uid) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set("userContact.codeTime", new Timestamp(0));

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    public UpdateResults<User> clearMobileCode(String uid) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set("userContact.mobileCode", "");
        ops.set("userContact.codeTime", new Timestamp(0));

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    public UpdateResults<User> setSafePwdExpirationTime(User user) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", user.get_Id());
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set("isNeedSafePwd", user.isNeedSafePwd());
        ops.set("safePwdExpiration", user.getSafePwdExpiration());

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    public boolean isNeedSafePwd(User user) {
		/*String usekey = Cache.Get(User.use_pwd_key+user.get_Id());//1开启 0 关闭 2 关闭6 H

		boolean isNeedSafePwd = true;
		long safePwdExpiration = 0L;

		*//**
         * 这个判断是为了兼容之前的功能，现在是不用缓存的值了，之前有些用户设置了，所以要把缓存里的删掉
         *//*
		if(usekey != null){
			Integer i = Integer.valueOf(usekey);
			if (i > 1) {
				safePwdExpiration = new Date().getTime();
				isNeedSafePwd = false;
			}else {
				isNeedSafePwd = false;
			}
			user.setNeedSafePwd(isNeedSafePwd);
			user.setSafePwdExpiration(safePwdExpiration);
			this.setSafePwdExpirationTime(user);
			Cache.Delete(User.use_pwd_key+user.get_Id());
		}

		Query<User> query = getQuery(User.class).filter("_id =", user.get_Id());

		if (!user.isNeedSafePwd()) {
			long nowTime = new Date().getTime();
			if(0 != user.getSafePwdExpiration() && nowTime > user.getSafePwdExpiration()){
				//现在时间已经大于设置的不用资金安全密码的时间了,此后都是需要资金安全密码的了,于是开启资金安全密码开关
				Datastore ds = super.getDatastore();
				UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
				ops.set("isNeedSafePwd", true);
				super.update(query, ops);
				isNeedSafePwd = true;
			} else {
				isNeedSafePwd = false;
			}
		}else {
			isNeedSafePwd = true;
		}
		return isNeedSafePwd;*/

        this.syncTradeAuthen(user);
        return user.getTradeAuthenType() == TradeAuthenType.TRADE_PASSWORD.getKey();
    }

    //更新googel身份验证的secret
    public UpdateResults<User> updateSecret(String uid, String secret) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set("userContact.secret", secret);

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    public UpdateResults<User> updateGoogleAu(String uid, int val) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set("userContact.googleAu", val);

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    //更新验证状态
    public UpdateResults<User> updateAuthStatus(String uid, String field, int val) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set(field, val);

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    public UpdateResults<User> updateVersion(String uid, int val) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set("version", val);

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    public UpdateResults<User> updateChargeRan(String uid, double val) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set("chargeRan", val);

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    /**
     * 实名认证审核更新用户信息
     *
     * @param uid
     * @param au
     * @return
     */
    public UpdateResults<User> updateRealName(String uid, Authentication au) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
        if (null != au.getRealName() && (
                au.getStatus() == AuditStatus.a1Pass.getKey() || au.getStatus() == AuditStatus.pass.getKey())) {
            ops.set("realName", au.getRealName());
            if (StringUtils.isNotBlank(au.getCardId())) {
                ops.set("userContact.idCard", au.getCardId());
            }
        }

        ops.set("userContact.cardStatu", au.getStatus());

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    /**
     * 十星宝审核更新用户信息
     *
     * @param uid
     * @param status
     * @return
     */
    public UpdateResults<User> updateSxbStatu(String uid, int status) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set("userContact.sxbStatu", status);

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    /**
     * 是否关闭资金安全密码
     *
     * @param uid
     * @param closeTime 设置为现在时间则为关闭，否则为开启
     * @return
     */
    public UpdateResults<User> useOrCloseSafePwd(String uid, Timestamp closeTime) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set("userContact.closeSafePwdTime", closeTime);

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    //更新用户是否被锁定
    public UpdateResults<User> updateRepayLock(String uid, int status) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set("repayLock", status);

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    public JSONArray authenMemo(User user, String type, String val, String ip) {
        JSONArray jsonArray = JSONArray.fromObject(user.getUserContact().getMemo());
        JSONObject jsonObject = new JSONObject();

        if (type.equals("email")) {
            if (user.getUserContact().getEmailStatu() == AuditStatus.pass.getKey()) {
                jsonObject.put("operDesc", "用户 " + user.getUserName() + " 在" + now() + "修改认证邮箱为：" + val + ",IP为：" + ip);
            } else {
                jsonObject.put("operDesc", "用户 " + user.getUserName() + " 在" + now() + "通过邮箱认证，邮箱为：" + val + ",IP为：" + ip);
            }
        } else {
            if (user.getUserContact().getMobileStatu() == AuditStatus.pass.getKey()) {
                jsonObject.put("operDesc", "用户 " + user.getUserName() + " 在" + now() + "修改认证手机为：" + val + ",IP为：" + ip);
            } else {
                jsonObject.put("operDesc", "用户 " + user.getUserName() + " 在" + now() + "通过手机认证，手机为：" + val + ",IP为：" + ip);
            }
        }

        jsonObject.put("operTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now()));
        jsonArray.add(jsonObject);

        return jsonArray;
    }


    /**
     * @param userName
     * @return 1为敏感词      2为已存在       0为正常
     */
    public int nameValidated(String userName) {
        try {
            boolean minganci = KeyWordFilter.hasFilter(userName);
            if (minganci) {
                return 1;
            }
            Query<User> q = getQuery(User.class);
//			q.or(
//					q.criteria("userName").equal(userName),
//					q.criteria("userContact.safeEmail").equal(userName),
//					q.criteria("userContact.checkEmail").equal(userName),
//					q.criteria("userContact.safeMobile").equal(userName),
//					q.criteria("userContact.checkMobile").equal(userName)
//			);
            User user = super.findOne(q.filter("userName", userName));
            if (user != null) {
                return 2;
            } else {
                return 0;
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return -1;
    }

    public boolean emailValidated(String email) {
        try {
            Query<User> q1 = getQuery(User.class);
//			q.or(
//				q.criteria("email").equal(email),
//				q.criteria("userContact.safeEmail").equal(email)
//			);

            Pattern pattern = Pattern.compile("^" + email + "$", Pattern.CASE_INSENSITIVE);
            q1.filter("email", pattern);

            User user = super.findOne(q1);//q.filter("email", email)

            if (user != null && !user.getEmail().equalsIgnoreCase(email)) {
                user = null;
            }
            if (user == null) {
                Query<User> q2 = getQuery(User.class);
                q2.filter("userContact.safeEmail", pattern);

                user = super.findOne(q2);//q.filter("email", email)
                //user = super.findOne(q.field("userContact.safeEmail").endsWithIgnoreCase(email));//q.filter("email", email)
            }

            if (user == null) {
                return true;
            } else if (user.getUserName() == null) {
                //Cache.SetObj("reging_user_"+email, user, 30*60);
                return !user.getUserContact().isCanReg();
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return false;
    }

    public boolean emailEditValidated(String email, String userId) {
        try {
            Query<User> q1 = getQuery(User.class).filter("_id <>", userId);
//			q.or(
//				q.criteria("email").equal(email),
//				q.criteria("userName").equal(email),
//				q.criteria("userContact.safeEmail").equal(email)
//			);
//			User user = super.findOne(q.field("email").endsWithIgnoreCase(email));
//
//			if(user != null && !user.getEmail().equalsIgnoreCase(email)){
//				user = null;
//			}

            Pattern pattern = Pattern.compile("^" + email + "$", Pattern.CASE_INSENSITIVE);
            q1.filter("email", pattern);

            User user = super.findOne(q1);//q.filter("email", email)

            if (user != null && !user.getEmail().equalsIgnoreCase(email)) {
                user = null;
            }
            if (user == null) {
                Query<User> q2 = getQuery(User.class).filter("_id <>", userId);
                q2.filter("userContact.safeEmail", pattern);

                user = super.findOne(q2);//q.filter("email", email)
                //user = super.findOne(q.field("userContact.safeEmail").endsWithIgnoreCase(email));//q.filter("email", email)
            }
            if (user == null) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return false;
    }


    public boolean mobileValidated(String mobile) {
        try {
            Query<User> q = getQuery(User.class);
            mobile = mobile.indexOf(" ") > 0 ? mobile.split("\\ ")[1] : mobile;
//			q.or(
//				q.criteria("userContact.safeMobile").equal(mobile),
//				q.criteria("userContact.safeMobile").contains(mobile)
//				//q.criteria("userName").equal(mobile)
//			);

            User user = super.findOne(q.field("userContact.safeMobile").contains(mobile));
            if (user == null) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return false;
    }

    /*add by xwz 20170711 校验loginCheckMobile是否重复*/
    public boolean mobileLoginCheckMobile(String loginCheckMobile) {
//        if (loginCheckMobile.contains(" ")) {
//            loginCheckMobile = loginCheckMobile.substring(loginCheckMobile.indexOf(" ") + 1);
//        }
        try {
            Query<User> q = getQuery(User.class);
            User user = super.findOne(q.filter("userContact.loginCheckMobile", loginCheckMobile));
            if (user == null) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return false;
    }

    public boolean mobileValidatedWithUserName(String mobile, String userName) {
        try {
            Query<User> q = getQuery(User.class);
            mobile = mobile.indexOf(" ") > 0 ? mobile.split("\\ ")[1] : mobile;
//			q.or(
//				q.criteria("userContact.safeMobile").equal(mobile),
//				q.criteria("userContact.safeMobile").contains(mobile)
//				//q.criteria("userName").equal(mobile)
//			);

            User user = super.findOne(q.filter("userName", userName).field("userContact.safeMobile").contains(mobile));
            if (user == null) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return false;
    }

    /**
     * 验证身份证号
     *
     * @param idCard
     * @return
     */
    public boolean cardValidated(String idCard) {
        try {
            Query<User> q = getQuery(User.class);
            q.filter("userContact.idCard", idCard);

            User user = super.findOne(q);
            if (user == null) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return false;
    }

    public String shortEmail(String email) {
        if (email != null && email.length() > 0) {
            email = email.indexOf("@") > 4 ? email.split("@")[0].substring(0, 4) + "****@" + email.split("@")[1] : email;
        }
        return email;
    }

    public String shortMobile(String mobile) {
        if (mobile != null && mobile.length() > 0) {
            mobile = mobile.length() > 4 ? mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4, mobile.length()) : mobile;
        }
        return mobile;
    }

    //不显示 国家编码+86
    public String shortMobile(String mobile, String mCode) {
        if (mobile != null && mobile.length() > 0) {
            if (mCode != null && mobile.indexOf(mCode) == 0) {
                int index = mCode.length();
                mobile = mobile.substring(index).trim();
            }
            mobile = mobile.length() > 4 ? mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4, mobile.length()) : mobile;
        }
        return mobile;
    }

    /**
     * 功能：资金安全密码是否正确
     *
     * @param safePwd
     * @param userId
     * @return -2:被锁定24小时  -1：错误，》=0正确 -4:已经被锁了
     */
    public boolean checkPayPwdForEntrust(String safePwd, String userId) {
        User user = getById(userId);
        safePwd = user.getEncryptedPwd(safePwd);

        logger.debug("safePwd:" + safePwd);
        if (safePwd.equals(user.getSafePwd())) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * 功能：资金安全密码是否正确
     *
     * @param safePwd
     * @param userId
     * @return -2:被锁定24小时  -1：错误，》=0正确 -4:已经被锁了
     */
    public int checkSecurityPwdForEntrust(String safePwd, String userId) {
        LimitType lt = LimitType.SafePassEntrustError;
        int status = lt.GetStatusNew(userId);
//        status = lt.GetStatusNew(userId);
        User user = getById(userId);
        safePwd = user.getEncryptedPwd(safePwd);
        //已经锁定
        if (status == -1) {
            if (safePwd.equals(user.getSafePwd())) {
                return -3;
            }
            return -4;
        }



        logger.debug("safePwd:" + safePwd);

        if (user != null && user.getSafePwd() != null && user.getSafePwd().length() > 0) {
            if (safePwd.equals(user.getSafePwd())) {
                lt.ClearStatus(userId);//输入正确了，清除
                return -3;
            } else {
                lt.UpdateStatus(userId);//记录一次
                status = lt.GetStatusNew(userId);
                if (status == -1) {    //已经锁定
                    // 锁定提现业务
                    Cache.Set("WithdrawPayPwdPassError_"+userId, Cache.Get(lt.toString() + "_" +userId));
                    return -2;
                }
                return lt.GetStatusNew(userId);
            }
        } else {
            return 0;//资金安全密码未设置
        }
    }
    /**
     * 功能：资金安全密码是否正确不锁定
     *
     * @param safePwd
     * @param userId
     * @return =1：错误，》=0正确
     */
    public Boolean checkSecurityPwdNoLock(String safePwd, String userId) {
        User user = getById(userId);
        safePwd = user.getEncryptedPwd(safePwd);
        if (!safePwd.equals(user.getSafePwd())) {
            return false;
        }
        return true;

    }



    /**
     * 功能：资金安全密码是否正确
     *
     * @param safePwd
     * @param userId
     * @return -2:被锁定24小时  -1：错误，》=0正确
     */
    public int checkSecurityPwdNew(String safePwd, String userId) {
        LimitType lt = LimitType.NoSecondVrifyPayPwdError;
        int status = lt.GetStatusNew(userId);
        if (status == -1) {
            return -2;//已锁定
        }

        User user = getById(userId);

        if (user.getSafePwdModifyTimes() > 1 && null != user.getSafePwdModifyTime()
                && getDiffDay(now(), user.getSafePwdModifyTime()) < 1) {
            return -2;
        }

        safePwd = user.getEncryptedPwd(safePwd);

        logger.debug("safePwd:" + safePwd);

        if (user != null && user.getSafePwd() != null && user.getSafePwd().length() > 0) {
            if (safePwd.equals(user.getSafePwd())) {
                lt.ClearStatus(userId);//输入正确了，清除
                return -3;
            } else {
                lt.UpdateStatus(userId);//记录一次
                status = lt.GetStatusNew(userId);
                if (status == -1) {    //已经锁定
                    return -2;
                }
                return lt.GetStatusNew(userId);
            }
        } else {
            return 0;//资金安全密码未设置
        }
    }

    /**
     * 功能：资金安全密码是否正确
     *
     * @param safePwd
     * @param userId
     * @return -2:被锁定24小时  -1：错误，》=0正确
     */
    public int checkSecurityPwdForSecendVerify(String safePwd, String userId) {
        LimitType lt = LimitType.NoSecondVrifyPayPwdError;
        int status = lt.GetStatusNew(userId);
        if (status == -1) {
            return -2;//已锁定
        }

        User user = getById(userId);

        safePwd = user.getEncryptedPwd(safePwd);

        logger.debug("safePwd:" + safePwd);

        if (user != null && user.getSafePwd() != null && user.getSafePwd().length() > 0) {
            if (safePwd.equals(user.getSafePwd())) {
                lt.ClearStatus(userId);//输入正确了，清除
                return -3;
            } else {
                lt.UpdateStatus(userId);//记录一次
                status = lt.GetStatusNew(userId);
                if (status == -1) {    //已经锁定
                    return -2;
                }
                return lt.GetStatusNew(userId);
            }
        } else {
            return 0;//资金安全密码未设置
        }
    }


    /**
     * 功能：资金安全密码是否正确
     *
     * @param safePwd
     * @param userId
     * @return -2:被锁定24小时  -1：错误，》=0正确
     */
    public int checkSecurityDownload(String safePwd, String userId) {
        LimitType lt = LimitType.WithdrawPayPwdPassError;
        int status = lt.GetStatusNew(userId);
        if (status == -1) {
            return -2;//已锁定
        }

        User user = getById(userId);

        if (user.getSafePwdModifyTimes() > 1 && null != user.getSafePwdModifyTime()
                && getDiffDay(now(), user.getSafePwdModifyTime()) < 1) {
            return -2;
        }

        safePwd = user.getEncryptedPwd(safePwd);

        logger.debug("safePwd:" + safePwd);

        if (user != null && user.getSafePwd() != null && user.getSafePwd().length() > 0) {
            if (safePwd.equals(user.getSafePwd())) {
                lt.ClearStatus(userId);//输入正确了，清除
                return -3;
            } else {
                lt.UpdateStatus(userId);//记录一次
                status = lt.GetStatusNew(userId);
                if (status == -1) {    //已经锁定
                    return -2;
                }
                return lt.GetStatusNew(userId);
            }
        } else {
            return 0;//资金安全密码未设置
        }
    }




    /**
     * 交易时验证密码
     *
     * @param safePwd
     * @param userId
     * @return
     */
    public int checkSecurityPwdForDeal(String safePwd, String userId) {
        LimitType lt = LimitType.SafePassError;
        int status = lt.GetStatusNew(userId);
        User user = getById(userId);
        if (status == -1) {
            if (safePwd.equals(user.getSafePwd())) {
                lt.ClearStatus(userId);//输入正确了，清除
                return -3;
            } else {
                return -2;//已锁定
            }
        }

        if (user.getSafePwdModifyTimes() > 1 && null != user.getSafePwdModifyTime()
                && getDiffDay(now(), user.getSafePwdModifyTime()) < 1) {
            return -2;
        }

        safePwd = user.getEncryptedPwd(safePwd);

        logger.debug("safePwd:" + safePwd);

        if (user != null && user.getSafePwd() != null && user.getSafePwd().length() > 0) {
            if (safePwd.equals(user.getSafePwd())) {
                lt.ClearStatus(userId);//输入正确了，清除
                return -3;
            } else {
                lt.UpdateStatus(userId);//记录一次
                status = lt.GetStatusNew(userId);
                if (status == -1) {    //已经锁定
                    return -2;
                }
                return lt.GetStatusNew(userId);
            }
        } else {
            return 0;//资金安全密码未设置
        }
    }

    /**
     * 功能：资金安全密码是否正确
     *
     * @param safePwd
     * @param userId
     * @return -2:被锁定24小时  -1：错误，》=0正确
     */
    public int checkSecurityPwd(String safePwd, String userId) {
        LimitType lt = LimitType.SafePassError;
        int status = lt.GetStatus(userId);
        if (status == -1) {
            return -2;//已锁定
        }

        User user = getById(userId);

        if (user.getSafePwdModifyTimes() > 1 && null != user.getSafePwdModifyTime()
                && getDiffDay(now(), user.getSafePwdModifyTime()) < 1) {
            return -2;
        }

        safePwd = user.getEncryptedPwd(safePwd);

        logger.debug("safePwd:" + safePwd);

        if (user != null && user.getSafePwd() != null && user.getSafePwd().length() > 0) {
            if (safePwd.equals(user.getSafePwd())) {
                lt.ClearStatus(userId);//输入正确了，清除
                return 1;
            } else {
                lt.UpdateStatus(userId);//记录一次
                return -1;
            }
        } else {
            return 0;//资金安全密码未设置
        }
    }



    /**
     * 功能：资金安全密码是否正确
     *
     * @param safePwd
     * @param userId
     * @return -2:被锁定24小时  -1：错误，》=0正确
     */
    public int checkSecurityEntrustPwd(String safePwd, String userId) {
        LimitType lt = LimitType.SafePassEntrustError;
        int status = lt.GetStatus(userId);
        if (status == -1) {
            return -2;//已锁定
        }

        User user = getById(userId);

        if (user.getSafePwdModifyTimes() > 1 && null != user.getSafePwdModifyTime()
                && getDiffDay(now(), user.getSafePwdModifyTime()) < 1) {
            return -2;
        }

        safePwd = user.getEncryptedPwd(safePwd);

        logger.debug("safePwd:" + safePwd);

        if (user != null && user.getSafePwd() != null && user.getSafePwd().length() > 0) {
            if (safePwd.equals(user.getSafePwd())) {
                lt.ClearStatus(userId);//输入正确了，清除
                return 1;
            } else {
                lt.UpdateStatus(userId);//记录一次
                return -1;
            }
        } else {
            return 0;//资金安全密码未设置
        }
    }


    /**
     * 功能：资金安全密码是否正确
     *
     * @param safePwd
     * @param userId
     * @return -2:被锁定24小时  -1：错误，》=0正确
     */
    public int checkPayPwd(String safePwd, String userId) {

        User user = getById(userId);

//        if (user.getSafePwdModifyTimes() > 1 && null != user.getSafePwdModifyTime()
//                && getDiffDay(now(), user.getSafePwdModifyTime()) < 1) {
//            return -2;
//        }

        safePwd = user.getEncryptedPwd(safePwd);

        logger.debug("safePwd:" + safePwd);

        if (user != null && user.getSafePwd() != null && user.getSafePwd().length() > 0) {
            if (safePwd.equals(user.getSafePwd())) {
                return 1;
            } else {
                return -1;
            }
        } else {
            //资金安全密码未设置
            return 0;
        }
    }



    /**
     * @param safePwd
     * @param userId
     * @param isExceptlimit 是否排除锁定和修改过密码的限制
     * @return
     */
    public int checkSecurityPwd(String safePwd, String userId, boolean isExceptlimit) {
        if (isExceptlimit) {
            LimitType lt = LimitType.SafePassError;
            User user = getById(userId);
            safePwd = user.getSafeEncryptedPwdCombin(safePwd);

            logger.debug("safePwd:" + safePwd);

            if (user != null && user.getSafePwd() != null && user.getSafePwd().length() > 0) {
                if (safePwd.contains(user.getSafePwd())) {
                    lt.ClearStatus(userId);//输入正确了，清除
                    return 1;
                } else {
                    lt.UpdateStatus(userId);//记录一次
                    return -1;
                }
            } else {
                return 0;//资金安全密码未设置
            }
        } else {
            return checkSecurityPwd(safePwd, userId);
        }
    }

    public int getDiffDay(Timestamp endTime, Timestamp startTime) {
        long intervalMilli = endTime.getTime() - startTime.getTime();
        return (int) (intervalMilli / (24 * 60 * 60 * 1000));
    }

    /**
     * 功能：资金安全密码是否正确
     *
     * @param safePwd
     * @param userId
     * @return -1：错误，》=0正确
     */
    public int transCheckSecurityPwd(String safePwd, String userId) {
        LimitType lt = LimitType.SafePassError;

        User user = getById(userId);

        safePwd = user.getEncryptedPwd(safePwd);
        logger.debug("safePwd:" + safePwd);
        int status = lt.GetStatusNew(userId);
        //已经锁定
        if (status == -1) {
            if (safePwd.equals(user.getSafePwd())) {
                return -3;
            }
            return -1;
        }


        if (user != null && user.getSafePwd() != null && user.getSafePwd().length() > 0) {
            if (safePwd.equals(user.getSafePwd())) {
                lt.ClearStatus(userId);//输入正确了，清除
                return 1;
            } else {
                lt.UpdateStatus(userId);//记录一次
                status = lt.GetStatusNew(userId);
                if (status == -1) {    //已经锁定
                    // 锁定提现业务
                    Cache.Set("WithdrawPayPwdPassError_"+userId, Cache.Get(lt.toString() + "_" +userId));
                    return -2;
                }
                return -1;
            }
        } else {
            return 0;//资金安全密码未设置
        }
    }

    /**
     * 手机是否被锁定
     *
     * @param userId
     * @return
     */
    public boolean checkPhoneLock(String userId) {
        boolean isLock = false;

        User user = getById(userId);
        UserContact uc = user.getUserContact();
        if (null != uc.getModifyMobileTime() && TimeUtil.getDiffDay(now(), uc.getModifyMobileTime()) < 1) {
            isLock = true;
        }
        return isLock;
    }

    /*****
     *
     * @param ids
     * @return
     */
    public Map<String, User> getUserMapByIds(List<String> ids) {
        Map<String, User> maps = new LinkedHashMap<String, User>();
        List<User> users = super.getListByIds(ids);
        if (users != null && users.size() > 0) {
            for (User u : users) {
                maps.put(u.get_Id(), u);
            }
        }
        return maps;
    }

    public Message isFingerprintCorrect(User user, String fingerprint) {
        Message msg = new Message();
        if (!user.getFingerprint().equals(fingerprint)) {
            msg.setCode(SystemCode.code_1001.getKey());
            msg.setMsg(L("验证指纹失败，请重试"));
        } else {
            msg.setSuc(true);
        }
        return msg;
    }

    /*start by xwz 20170706*/
//	public Message safePwd(String pass, String userId, LanguageTag lanTag){
//		SafeTipsTag stt = lanTag.getStt();
//		Message msg = new Message();
//		int status = new UserDao().checkSecurityPwd(pass, userId);
//		if(status==-2){
//			msg.setCode(SystemCode.code_1004.getKey());
//			msg.setMsg(L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。"));
//		}else if(status==-1){
//			msg.setCode(SystemCode.code_1005.getKey());
//			msg.setMsg(L("资金安全密码错误"));
//		}else if(status==1){
//			msg.setSuc(true);
//		} else {
//			msg.setCode(SystemCode.code_1026.getKey());
//			msg.setMsg(L("您未设置资金密码，请设置资金密码后重试"));
//		}
//		return msg;
//	}
//

    public Message safePwd(String pass, String userId, LanguageTag lanTag) {
        //2017.8.14 xzhang 修改交易验证设置错误信息提示
        SafeTipsTag stt = lanTag.getStt();
        lan = getUserLanguageById(userId);
        Message msg = new Message();
        int status = new UserDao().checkSecurityPwdNew(pass, userId);
        if (status == -2) {
            msg.setCode(SystemCode.code_1004.getKey());
            msg.setMsg(L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。"));
        } else if (status == -3) {
            msg.setSuc(true);
        } else if (status == 0) {
            msg.setCode(SystemCode.code_1026.getKey());
            msg.setMsg(L("您未设置资金密码，请设置资金密码后重试"));
        } else {
            msg.setCode(SystemCode.code_1005.getKey());
//			msg.setMsg(LanFormat("抱歉，资金密码错误，还有%%次机会。",status+""));
            //2017.8.14 xzhang 修改交易验证设置错误信息提示
            msg.setMsg(Lan.LanguageFormat(lan, "资金密码输入错误，还有%%次机会。", status + ""));
        }
        return msg;
    }
    /*end*/

    //判断移动设备验证码是否正确
    public Message isCorrect(String lan, User user, String savedSecret, long code) {
        Message msg = new Message();
        if (code == 0) {
            msg.setCode(SystemCode.code_1001.getKey());
            msg.setMsg(L("请输入移动设备上生成的验证码。"));
            return msg;
        }

        String key = user.getId() + "_gauth";
        msg = checkLocked(user.getId(), key);
        if (!msg.isSuc()) {
            return msg;
        }

        long t = System.currentTimeMillis();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(3);

        boolean r = ga.check_code(savedSecret, code, t);
        if (!r) {
            return checkLock2Hours(lan, user.getId(), key);
        } else {
            Object current = Cache.GetObj(key);
            if (current != null) {
                int count = Integer.parseInt(current.toString());
                if (count >= ApproveAction.times) {
                    msg.setCode(SystemCode.code_1001.getKey());
                    msg.setMsg(L("您连续输入错误的次数太多，请2小时后再试。"));
                    return msg;
                }
                Cache.Delete(key);
            }
        }
        msg.setSuc(true);
        return msg;
    }


    //判断移动设备验证码是否正确
    public Message isCorrect(User user, String savedSecret, long code) {
        Message msg = new Message();
        if (code == 0) {
            msg.setCode(SystemCode.code_1001.getKey());
            msg.setMsg(L("请输入移动设备上生成的验证码。"));
            return msg;
        }

        String key = user.getId() + "_gauth";
        msg = checkLocked(user.getId(), key);
        if (!msg.isSuc()) {
            return msg;
        }

        long t = System.currentTimeMillis();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(3);

        boolean r = ga.check_code(savedSecret, code, t);
        if (!r) {
            return checkLock2Hours(user.getId(), key);
        } else {
            Object current = Cache.GetObj(key);
            if (current != null) {
                int count = Integer.parseInt(current.toString());
                if (count >= ApproveAction.times) {
                    msg.setCode(SystemCode.code_1001.getKey());
                    msg.setMsg(L("您连续输入错误的次数太多，请2小时后再试。"));
                    return msg;
                }
                Cache.Delete(key);
            }
        }
        msg.setSuc(true);
        return msg;
    }

    public Message checkLocked(String userId, String key) {
        Message msg = new Message();
        Object current = null;
        current = Cache.GetObj(key);
        if (current != null) {
            int count = Integer.parseInt(current.toString());
            if (count >= ApproveAction.times) {
                msg.setCode(SystemCode.code_1001.getKey());
                msg.setMsg(L("您连续输入错误的次数太多，请2小时后再试。"));
                return msg;
            }
        }
        msg.setSuc(true);
        return msg;
    }

    public Message checkLock2Hours(String lan, String userId, String key) {
        Message msg = new Message();
        Object current = null;
        current = Cache.GetObj(key);

        String des = "短信验证码输入有误，";
        if (key.endsWith("gauth")) {
            des = "谷歌验证码输入有误，";
        }

        if (current == null) {
            Cache.SetObj(key, 1, 60 * 60 * 2);
            if((ApproveAction.times - 1) > 1){
                msg.setMsg(Lan.Language(lan, des) + Lan.LanguageFormat(lan, "您还有%%次机会s", (ApproveAction.times - 1) + ""));
            }else{
                msg.setMsg(Lan.Language(lan, des) + Lan.LanguageFormat(lan, "您还有%%次机会。", (ApproveAction.times - 1) + ""));
            }
        } else {
            int count = Integer.parseInt(current.toString());
            count++;
            Cache.SetObj(key, count, 60 * 60 * 2);
            if (count < ApproveAction.times) {
                if((ApproveAction.times - count) > 1){
                    msg.setMsg(Lan.Language(lan, des) + Lan.LanguageFormat(lan, "您还有%%次机会s", (ApproveAction.times - count) + ""));
                }else{
                    msg.setMsg(Lan.Language(lan, des) + Lan.LanguageFormat(lan, "您还有%%次机会。", (ApproveAction.times - count) + ""));
                }

            } else {
                msg.setMsg(L("您连续输入错误的次数太多，请2小时后再试。"));
            }
        }
        msg.setCode(SystemCode.code_1001.getKey());
        return msg;
    }

    public Message checkLock2Hours(String userId, String key) {
        Message msg = new Message();
        Object current = null;
        current = Cache.GetObj(key);

        String des = "短信验证码输入有误，";
        if (key.endsWith("gauth")) {
            des = "谷歌验证码输入有误，";
        }

        if (current == null) {
            Cache.SetObj(key, 1, 60 * 60 * 2);
            if((ApproveAction.times - 1) > 1){
                msg.setMsg(L(des) + Lan.LanguageFormat(lan, "您还有%%次机会s", (ApproveAction.times - 1) + ""));
            }else{
                msg.setMsg(L(des) + Lan.LanguageFormat(lan, "您还有%%次机会。", (ApproveAction.times - 1) + ""));
            }
        } else {
            int count = Integer.parseInt(current.toString());
            count++;
            Cache.SetObj(key, count, 60 * 60 * 2);
            if (count < ApproveAction.times) {
                if((ApproveAction.times - count) > 1){
                    msg.setMsg(L(des) + String.format(L("您还有%%次机会s"), (ApproveAction.times - count) + ""));
                }else{
                    msg.setMsg(L(des) + String.format(L("您还有%%次机会。"), (ApproveAction.times - count) + ""));
                }
            } else {
                msg.setMsg(L("您连续输入错误的次数太多，请2小时后再试。"));
            }
        }
        msg.setCode(SystemCode.code_1001.getKey());
        return msg;
    }

    /**
     * 增量更新保存属性值
     *
     * @param userId
     * @param attrName
     * @return
     */
    public boolean increaseValue(String userId, String attrName) {
        Datastore ds1 = this.getDatastore();
        Query<User> q2 = ds1.find(User.class, "_id", userId);
        UpdateOperations<User> ops1 = ds1.createUpdateOperations(User.class);
        ops1.inc(attrName);

        return this.update(q2, ops1).getError() != null;
    }

    public Message switchLoginAuthen(User user, int type, String ip, HttpServletRequest request) {
        lan = user.getLanguage();
        Message msg = new Message();
        boolean isLoginGoogleAuth = false,
                isDiffAreaLoginNoCheck = false;
        UserContact uc = user.getUserContact();

        if (LoginAuthenType.PASSWORD.getKey() == type) {
            // 关闭 登录Google验证码
            isLoginGoogleAuth = false;

            // 关闭 异地登录验证（短信/邮件）
            isDiffAreaLoginNoCheck = true;
        } else if (LoginAuthenType.PASSWORD_GOOGLE.getKey() == type) {
            if (!uc.isOpenGoogleAuth()) {
                msg.setMsg(L("操作失败，您还未开启Google认证。"));
                return msg;
            }

            // 开启 登录Google验证码
            isLoginGoogleAuth = true;

            // 关闭 异地登录验证（短信/邮件）
            isDiffAreaLoginNoCheck = true;
        } else if (LoginAuthenType.PASSWORD_DIFFERENT_PLACE_SMS_OR_EMAIL.getKey() == type) {
            if (!uc.isPassMobileAuth() && !uc.isPassEmailAuth()) {
                msg.setMsg(L("操作失败，您还未开启手机或Email认证。"));
                return msg;
            }

            // 关闭 登录Google验证码
            isLoginGoogleAuth = false;

            // 开启 异地登录验证（短信/邮件）
            isDiffAreaLoginNoCheck = false;
        } else if (LoginAuthenType.PASSWORD_GOOGLE_DIFFERENT_PLACE_SMS_OR_EMAIL.getKey() == type) {
            if (!uc.isPassMobileAuth() && !uc.isPassEmailAuth()) {
                msg.setMsg(L("操作失败，您还未开启手机或Email认证。"));
                return msg;
            }
            if (!uc.isOpenGoogleAuth()) {
                msg.setMsg(L("操作失败，您还未开启Google认证。"));
                return msg;
            }

            // 开启 登录Google验证码
            isLoginGoogleAuth = true;

            // 开启 异地登录验证（短信/邮件）
            isDiffAreaLoginNoCheck = false;
        } else {
            msg.setCode(SystemCode.code_1019.getKey());
            msg.setMsg(SystemCode.code_1019.getValue());
            return msg;
        }

        boolean result = this.switchLoginAuthen(user, isLoginGoogleAuth, isDiffAreaLoginNoCheck, ip, request, true);

        if (result) {
            msg.setMsg(L("操作成功"));
            msg.setSuc(true);
        } else {
            msg.setMsg(L("操作失败"));
        }
        return msg;
    }


    public int getLoginAuthenType(boolean isLoginGoogleAuth, boolean isDiffAreaLoginNoCheck) {
        int loginAuthenType = 0;
        if (isLoginGoogleAuth && !isDiffAreaLoginNoCheck) {
            loginAuthenType = LoginAuthenType.PASSWORD_GOOGLE_DIFFERENT_PLACE_SMS_OR_EMAIL.getKey();
        } else if (isLoginGoogleAuth && isDiffAreaLoginNoCheck) {
            loginAuthenType = LoginAuthenType.PASSWORD_GOOGLE.getKey();
        } else if (!isLoginGoogleAuth && !isDiffAreaLoginNoCheck) {
            loginAuthenType = LoginAuthenType.PASSWORD_DIFFERENT_PLACE_SMS_OR_EMAIL.getKey();
        } else {
            loginAuthenType = LoginAuthenType.PASSWORD.getKey();
        }

        return loginAuthenType;
    }


    public Message switchTradeAuthen(User user, int type, String ip, HttpServletRequest request) {
        lan = user.getLanguage();//add by xwz
        Message msg = new Message();
        boolean isNeedSafePwd = false;
        long safePwdExpiration = 0L;
        if (TradeAuthenType.TRADE_PASSWORD.getKey() == type) {  // 每次交易均验证资金密码
            isNeedSafePwd = true;
            safePwdExpiration = 0L;
        } else if (TradeAuthenType.NO_TRADE_PASSWORD.getKey() == type ||
                TradeAuthenType.NO_TRADE_PASSWORD_FOR_6H.getKey() == type) {  // 6小时内免输资金密码  每次交易均验证资金密码
            boolean isNoTradePassword = TradeAuthenType.NO_TRADE_PASSWORD.getKey() == type;
            Date dateNow = new Date();
            long expirationTime = isNoTradePassword ? 0L : dateNow.getTime() + 1000 * 60 * 60 * 6;
            isNeedSafePwd = false;
            safePwdExpiration = expirationTime;
        } else {
            msg.setCode(SystemCode.code_1019.getKey());
            msg.setMsg(SystemCode.code_1019.getValue());
            return msg;
        }

        int result = this.switchTradeAuthen(user, isNeedSafePwd, safePwdExpiration, ip, request, true);
        if (result == 1) {
            msg.setMsg(L("设置成功"));
            msg.setSuc(true);
        } else if (result == 2) {
            msg.setMsg(L("操作失败，请先设置资金安全密码"));
        } else if (result == 3) {
            msg.setMsg(L("操作失败，您已选择此项"));
        } else {
            msg.setMsg(L("操作失败"));
        }

        return msg;
    }

    public Message switchWithdrawAuthen(User user, int type, String ip, HttpServletRequest request) {
        lan = user.getLanguage();
        Message msg = new Message();
        boolean payMobileAuth = false,
                payEmailAuth = false,
                payGoogleAuth = false;
        int flag = CommonUtil.checkMobileNoOrEmail(user);
        UserContact uc = user.getUserContact();
        if (WithdrawAuthenType.TRADE_PASSWORD_SMS_OR_EMAIL_CODE.getKey() == type) {  // 资金密码+短信/邮件验证码
            if (flag == 1) {
                if (!uc.isPassMobileAuth()) {
                    msg.setMsg(L("操作失败，您还未开启手机认证。"));
                    return msg;
                }
                payMobileAuth = true;
            } else if (flag == 2) {
                payEmailAuth = true;
            }
        } else if (WithdrawAuthenType.TRADE_PASSWORD_GOOGLE_CODE.getKey() == type) { // 资金密码+Google验证码
            if (!uc.isOpenGoogleAuth()) {
                msg.setMsg(L("操作失败，您还未开启Google认证。"));
                return msg;
            }

            payGoogleAuth = true;
        } else if (WithdrawAuthenType.TRADE_PASSWORD_SMS_OR_EMAIL_GOOGLE_CODE.getKey() == type) { // 资金密码+短信/邮件验证码+Google验证码
            if (!uc.isOpenGoogleAuth()) {
                msg.setMsg(L("操作失败，您还未开启Google认证。"));
                return msg;
            }

            payGoogleAuth = true;
            if (flag == 1) {
                payMobileAuth = true;
            } else if (flag == 2) {
                payEmailAuth = true;
            }
        } else {
            msg.setCode(SystemCode.code_1019.getKey());
            msg.setMsg(SystemCode.code_1019.getValue());
            return msg;
        }

        int result = this.switchWithdrawAuthen(user, payMobileAuth, payEmailAuth, payGoogleAuth, ip, request, true);

        if (result == 1) {
            msg.setMsg(L("操作成功"));
            msg.setSuc(true);
        } else if (result == 2) {
            msg.setMsg(L("操作失败，请先设置资金安全密码"));
        } else if (result == 3) {
            msg.setMsg(L("操作失败，您已选择此项"));
        } else {
            msg.setMsg(L("操作失败"));
        }
        return msg;
    }

    /**
     * 切换提现地址验证模式 add by buxianguan
     *
     * @param user
     * @param type
     * @return
     */
    public Message switchWithdrawAddressAuthen(User user, int type, String ip, HttpServletRequest request) {
        Message msg = new Message();

        if (!WithdrawAddressAuthenType.MAP.containsKey(type)) {
            msg.setCode(SystemCode.code_1019.getKey());
            msg.setMsg(SystemCode.code_1019.getValue());
            return msg;
        }

        if (type == user.getWithdrawAddressAuthenType()
                || (type == WithdrawAddressAuthenType.ORIGINAL.getKey() && user.getWithdrawAddressAuthenType() == 0)) {
            msg.setMsg(L("操作失败，您已选择此项"));
            return msg;
        }

        Datastore ds = getDatastore();
        Query<User> q = ds.find(User.class, "_id", user.getId());
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
        ops.set("withdrawAddressAuthenType", type);
        ops.set("withdrawAddressAuthenModifyTime", now());

        //记录是否首次开启安全模式
        if (user.getWithdrawAddressAuthenType() == 0) {
            ops.set("withdrawAddressAuthenSwitchStatus", 1);
        } else {
            ops.set("withdrawAddressAuthenSwitchStatus", 2);
        }

        UpdateResults<User> ur = update(q, ops);
        if (!ur.getHadError()) {
            user.setWithdrawAddressAuthenType(type);
            /*Start by guankaili 20181229 添加消息队列 */
            //记录操作日志
//            new OperateLogDao().record(user, LogCategory.WITHDRAW_ADDRESS_AUTHEN_SETTING, "选择“" + WithdrawAddressAuthenType.MAP.get(type) + "”", ip, request);
            OperateLogInfoProducer.send(user.getUserName(),user.getId(),LogCategory.WITHDRAW_ADDRESS_AUTHEN_SETTING.getKey(),"选择“" + WithdrawAddressAuthenType.MAP.get(type) + "”", ip,CommonUtil.getBrowserInfo(request));
            /*End*/
            msg.setMsg(L("操作成功"));
            msg.setSuc(true);
            return msg;
        }

        msg.setMsg(L("操作失败"));
        return msg;
    }

    public int getTradeAuthenType(boolean hasSetSafePwd, boolean isNeedSafePwd, long safePwdExpiration) {
//        if (!hasSetSafePwd) {
//            return TradeAuthenType.NO_TRADE_PASSWORD.getKey();
//        }

        int tradeAuthenType = 0;
        long nowTime = TimeUtil.getNow().getTime();
        if (!isNeedSafePwd && safePwdExpiration == 0) {
            tradeAuthenType = TradeAuthenType.NO_TRADE_PASSWORD.getKey();
        } else if (!isNeedSafePwd && safePwdExpiration > nowTime) {
            tradeAuthenType = TradeAuthenType.NO_TRADE_PASSWORD_FOR_6H.getKey();
        } else if (isNeedSafePwd) {
            tradeAuthenType = TradeAuthenType.TRADE_PASSWORD.getKey();
        }

        return tradeAuthenType;
    }


    public int getWithdrawAuthenType(boolean hasSafePwd, boolean isPayMobileAuth, boolean isPayEmailAuth, boolean isPayGoogleAuth, int googleAu) {
        if (!hasSafePwd) {
            return 0;
        }
        //默认密码加谷歌 modify by zhanglinbo 20170311
        int withdrawAuthenType = WithdrawAuthenType.TRADE_PASSWORD_GOOGLE_CODE.getKey();
        boolean isOpenSmsOrEmail = isPayMobileAuth || isPayEmailAuth;
        if (isOpenSmsOrEmail && isPayGoogleAuth) {//3 手机验证+谷歌
            withdrawAuthenType = WithdrawAuthenType.TRADE_PASSWORD_SMS_OR_EMAIL_GOOGLE_CODE.getKey();
        } else if (!isOpenSmsOrEmail && isPayGoogleAuth) {//2：谷歌
            withdrawAuthenType = WithdrawAuthenType.TRADE_PASSWORD_GOOGLE_CODE.getKey();
            //2017.08.16 xzhang 修改开启关闭谷歌验证提现验证选择项问题 JYPT-974 添加1==googleAu判断
        } else if ((isOpenSmsOrEmail && !isPayGoogleAuth) || 1 == googleAu) {//1:密码+手机验证
            withdrawAuthenType = WithdrawAuthenType.TRADE_PASSWORD_SMS_OR_EMAIL_CODE.getKey();
        }

        return withdrawAuthenType;
    }

    /**
     * 修改用户的真实姓名和认证类型
     *
     * @param userId
     * @param realName
     * @param authType
     * @return
     */
    public UpdateResults<User> updateRealNameAndAuthType(String userId, String realName, int authType, boolean isResetFailAuthTimes) {
        Datastore ds = getDatastore();
        Query<User> q = ds.find(User.class, "_id", userId);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
        ops.set("realName", realName);
        ops.set("authenType", authType);
        if (isResetFailAuthTimes) {
            ops.set("failAuthTimes", 0);
        }
        UpdateResults<User> ur = update(q, ops);

        return ur;
    }

    /*****
     *
     * @param ids
     * @return
     */
    public Map<String, User> getUserMapByIds(Set<String> ids) {
        Map<String, User> maps = new LinkedHashMap<String, User>();
        List<User> users = super.getListByIds(new ArrayList<>(ids));
        if (users != null && users.size() > 0) {
            for (User u : users) {
                maps.put(u.get_Id(), u);
            }
        }
        return maps;
    }

    /**
     * 更新用户语言
     * <p>
     * 更新时,将语言信息放入到memcached一份,供trans使用
     * <p>
     * add by xwz 20170621
     *
     * @param userId
     * @param lan
     * @return
     */
    public UpdateResults<User> updateUserLanguage(String userId, String lan) {
        Datastore ds = getDatastore();
        Query<User> q = ds.find(User.class, "_id", userId);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
        ops.set("language", lan);
        UpdateResults<User> ur = super.update(q, ops);
        Cache.Set("user_lan_" + userId, lan);
        return ur;
    }


    /**
     * 更新用户法币信息
     * <p>
     * 更新时,将法币信息放入到memcached一份,供trans使用
     *
     * @param userId          用户ID
     * @param legalTenderType 货币信息
     * @return
     */
    public UpdateResults<User> updateUserLegalTender(String userId, LegalTenderType legalTenderType) {
        Datastore ds = getDatastore();
        Query<User> q = ds.find(User.class, "_id", userId);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
        ops.set("currency", legalTenderType.getKey());
        ops.set("currencyN", legalTenderType.getValue());
        UpdateResults<User> ur = super.update(q, ops);
        Cache.Set("user_legal_tender_" + userId, legalTenderType.getKey() + "_" + legalTenderType.getValue());
        return ur;
    }


    //更新用户vip等级信息
    public UpdateResults<User> updateUserVipRate(User user) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", user.getId());
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set("vipRate", user.getVipRate());

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }

    /**
     * 根据用户ID累加积分
     *
     * @param userId   用户ID
     * @param addJifen 此次累加的积分
     * @return 返回更新对象
     */
    public UpdateResults<User> updateUserJifen(String userId, double addJifen) {
        Datastore ds = super.getDatastore();
        UpdateResults<User> ur = null;
        Query<User> q = ds.find(User.class, "_id", userId);
        User user = super.findOne(q);
        if (user != null) {
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("totalJifen", user.getTotalJifen() + addJifen);
            ur = super.update(q, ops);
        }

        return ur;
    }

    public UpdateResults<User> setAttrSwitch(User user, String attr, boolean value) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", user.get_Id());
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set(attr, value);

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }


    public void syncTradeAuthen(User user) {
        switchTradeAuthen(user, user.isNeedSafePwd(), user.getSafePwdExpiration(), null, null, false);
    }

    /**
     * 选择交易安全策略选项
     *
     * @param user
     * @param isNeedSafePwd
     * @param safePwdExpiration
     * @param ip
     * @param request
     * @return 1：操作成功
     * 2：操作失败，请先设置资金安全密码
     * 3：操作失败，您已选择此项
     * 0：操作失败
     */
    public int switchTradeAuthen(User user, boolean isNeedSafePwd, long safePwdExpiration, String ip, HttpServletRequest request, boolean isRecordLog) {
        String userId = user.get_Id();
        //boolean hasSetSafePwd = user.getHasSafePwd();

        int tradeAuthenType = 0;
		        /*if(hasSetSafePwd) {
		            if( !isNeedSafePwd &&
		                    safePwdExpiration > 0 &&
		                    safePwdExpiration <= TimeUtil.getNow().getTime()){
		                isNeedSafePwd = true;
		                safePwdExpiration = 0;
		            }

		            tradeAuthenType = getTradeAuthenType(user.getHasSafePwd(), isNeedSafePwd, safePwdExpiration);
		        }else if(user.getTradeAuthenType() == 0){
		            log.error("userName: "+ user.getUserName()+", 操作失败，请先设置资金安全密码。");
		            return 2;
		        }*/
        if (!isNeedSafePwd &&
                safePwdExpiration > 0 &&
                safePwdExpiration <= TimeUtil.getNow().getTime()) {
            isNeedSafePwd = true;
            safePwdExpiration = 0;
        }

        tradeAuthenType = getTradeAuthenType(user.getHasSafePwd(), isNeedSafePwd, safePwdExpiration);

        if (tradeAuthenType == user.getTradeAuthenType()) {
            if (!user.getHasSafePwd() && tradeAuthenType == 1) {
                return 2;
            }

            if (isRecordLog) {
                log.warn("userName: " + user.getUserName() + ", 不需要重复选择交易安全策略选项。");
            }
            return 3; // 不需要重复交易安全策略选项
        }

        Datastore ds = getDatastore();
        Query<User> q = ds.find(User.class, "_id", userId);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
        ops.set("isNeedSafePwd", isNeedSafePwd);
        ops.set("safePwdExpiration", safePwdExpiration);
        ops.set("tradeAuthenType", tradeAuthenType);
        UpdateResults<User> ur = update(q, ops);

        boolean isSucc = !ur.getHadError();
        if (isSucc) {
            user.setTradeAuthenType(tradeAuthenType);

            if (isRecordLog) {
                /*Start by guankaili 20181229 添加消息队列 */
//                new OperateLogDao().record(user, LogCategory.TRADE_AUTHEN_SETTING, "选择“" + TradeAuthenType.MAP.get(tradeAuthenType) + "”", ip, request);
                OperateLogInfoProducer.send(user.getUserName(),user.getId(),LogCategory.TRADE_AUTHEN_SETTING.getKey(),"选择“" + TradeAuthenType.MAP.get(tradeAuthenType) + "”", ip,CommonUtil.getBrowserInfo(request));
                /*End*/
            }
        }
        return isSucc ? 1 : 0;
    }


    /**
     * 同步更新登录安全策略选项
     *
     * @param user
     */
    public void syncLoginAuthen(User user) {
        switchLoginAuthen(user, user.isLoginGoogleAuth(), user.isDiffAreaLoginNoCheck(), null, null, false);
    }

    public boolean switchLoginAuthen(User user, boolean isLoginGoogleAuth, boolean isDiffAreaLoginNoCheck,
                                     String ip, HttpServletRequest request, boolean isRecordLog) {
        int loginAuthenType = getLoginAuthenType(isLoginGoogleAuth, isDiffAreaLoginNoCheck);

        if (loginAuthenType == user.getLoginAuthenType()) {
            if (isRecordLog) {
                log.warn("userName: " + user.getUserName() + ", 不需要重复重复登录安全策略选项。");
            }
            return false;
        }

        Datastore ds = getDatastore();
        Query<User> q = ds.find(User.class, "_id", user.getId());
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
        ops.set("loginGoogleAuth", isLoginGoogleAuth);
        ops.set("diffAreaLoginNoCheck", isDiffAreaLoginNoCheck);
        ops.set("loginAuthenType", loginAuthenType);
        UpdateResults<User> ur = update(q, ops);

        boolean isSucc = !ur.getHadError();
        if (isSucc) {
            user.setLoginAuthenType(loginAuthenType);

            if (isRecordLog) {
                /*Start by guankaili 20181229 添加消息队列 */
//                new OperateLogDao().record(user, LogCategory.LOGIN_AUTHEN_SETTING, "选择“" + LoginAuthenType.MAP.get(loginAuthenType) + "”", ip, request);
                OperateLogInfoProducer.send(user.getUserName(),user.getId(),LogCategory.LOGIN_AUTHEN_SETTING.getKey(),"选择“" + LoginAuthenType.MAP.get(loginAuthenType) + "”", ip,CommonUtil.getBrowserInfo(request));
                /*End*/
            }
        }
        return isSucc;
    }


    public void syncWitchdrawAuthen(User user) {
        switchWithdrawAuthen(user, user.isPayMobileAuth(), user.isPayEmailAuth(), user.isPayGoogleAuth(), null, null, false);
    }

    /**
     * 选择提现安全策略选项
     *
     * @param user
     * @param isPayMobileAuth
     * @param isPayEmailAuth
     * @param isPayGoogleAuth
     * @param ip
     * @param request
     * @return 1：操作成功
     * 2：操作失败，请先设置资金安全密码
     * 3：操作失败，您已选择此项
     * 0：操作失败
     */
    public int switchWithdrawAuthen(User user, boolean isPayMobileAuth, boolean isPayEmailAuth,
                                    boolean isPayGoogleAuth, String ip, HttpServletRequest request, boolean isRecordLog) {
        if (!user.getHasSafePwd()) {
            log.warn("userName: " + user.getUserName() + ", 选择交易策略选项前，先设置资金安全密码。");
            return 2; // 操作失败，请先设置资金安全密码
        }

        //2017.08.16 xzhang 修改开启关闭谷歌验证提现验证选择项问题 JYPT-974
        int withdrawAuthenType = getWithdrawAuthenType(user.getHasSafePwd(), isPayMobileAuth, isPayEmailAuth, isPayGoogleAuth, user.getUserContact().getGoogleAu());
        if (withdrawAuthenType == user.getWithdrawAuthenType()) {
            if (isRecordLog) {
                log.warn("userName: " + user.getUserName() + ", 不需要重复选择提现安全策略选项。");
            }
            return 3; // 操作失败，您已选择此项
        }

        Datastore ds = getDatastore();
        Query<User> q = ds.find(User.class, "_id", user.getId());
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
        ops.set("payMobileAuth", isPayMobileAuth);
        ops.set("payEmailAuth", isPayEmailAuth);
        ops.set("payGoogleAuth", isPayGoogleAuth);
        ops.set("withdrawAuthenType", withdrawAuthenType);
        UpdateResults<User> ur = update(q, ops);

        if (!ur.getHadError()) {
            user.setWithdrawAuthenType(withdrawAuthenType);
            if (isRecordLog) {
                /*Start by guankaili 20181229 添加消息队列 */
//                new OperateLogDao().record(user, LogCategory.WITHDRAW_AUTHEN_SETTING, "选择“" + WithdrawAuthenType.MAP.get(withdrawAuthenType) + "”", ip, request);
                OperateLogInfoProducer.send(user.getUserName(),user.getId(),LogCategory.WITHDRAW_AUTHEN_SETTING.getKey(),"选择“" + WithdrawAuthenType.MAP.get(withdrawAuthenType) + "”", ip,CommonUtil.getBrowserInfo(request));
                /*End*/
            }
            return 1;
        }

        return 0;
    }

    /**
     * 获取用户最小ID
     *
     * @return
     */
    public int getMinUserId() {
        int id = 0;
        Query<User> q = getQuery(User.class);
        q.order("_id").limit(1);
        List<User> userList = super.find(q).asList();
        if (null != userList) {
            try {
                id = new Integer(userList.get(0).get_Id());
            } catch (Exception e) {
                log.error("获取用户最小id出错，错误信息：" + e.toString(), e);
            }
        }
        return id;
    }

    /*start by xzhang 20171215 交易页面三期PRD:同步用户未登录和登录后的收藏信息*/

    /**
     * 获取用户收藏(用于用户登录同步收藏信息)
     *
     * @param userId
     * @param cookieValue
     * @return 1.判断浏览器中的cookie是存在用户未登录状态下关注的市场信息。
     * 1.1 N：不存在的情况下，查询缓存是否存在用户收藏市场标示。
     * 1.1.1 当标示不为0的情况下，要么缓存标示不存在要么用户存在但未同步缓存标示。
     * 1.1.2 查询用户缓存收藏市场信息，判断信息是否存在。
     * 1.1.3 如果缓存不存在，则查询数据库。不管数据库存不存在收藏信息，都同步缓存标示和缓存市场信息。缓存标示0：不存在，1：存在
     * 1.2 Y:存在的情况下，查询缓存是否存在用户收藏市场信息。
     * 1.2.1 如果缓存中市场信息不存在，则查询数据库信息。
     * 1.2.2 判断用户历史收藏信息是否存在。
     * 1.2.3 如果不存在历史收藏信息，判断cookie中拼接信息是否合法，主要判断是否存在该市场信息，和字符串是否正确。**|**|**
     * 数据合法性校验通过后，将数据存入数据库，并同步缓存信息
     * 1.2.4 如果存在历史收藏信息，判断cookie中拼接信息是否合法，主要判断是否存在该市场信息，和字符串是否正确。并历史是否包含cookie信息。
     * 将cookie收藏信息增量新增到用户收藏历史信息。
     * cookie：
     * key：userCollectMarket
     * value：gbc_usdt_hotdata_Globalcoin|etc_btc_hotdata_Ethereum+Classic中线隔开
     */
    public String getUserCollect(String userId, String cookieValue) {
        String isCollect = "";
        String collect = "";
        cookieValue = "";//暂时登录时获取服务器数据覆盖本地cookie
        try {
            if (!StringUtil.exist(cookieValue)) {
                //1.1 不存在的情况下，查询缓存是否存在用户收藏市场标示。
                isCollect = Cache.Get("isUserCollect" + userId);
                if (!"0".equals(isCollect)) {
                    collect = Cache.Get("userCollect" + userId);
                    if (!StringUtil.exist(collect)) {
                        CollectMarket collectMarket = (CollectMarket) Data.GetOne("SELECT userId,collect from collectmarket WHERE userId=? ", new Object[]{userId}, CollectMarket.class);
                        if (collectMarket != null) {
                            collect = collectMarket.getCollect();
                            Cache.Set("isUserCollect" + userId, "1");
                        } else {
                            Cache.Set("isUserCollect" + userId, "0");
                        }
                        Cache.Set("userCollect" + userId, collect);
                    } else {
                        Cache.Set("isUserCollect" + userId, "1");
                    }
                }
            } else {
                //1.2 Y:存在的情况下，查询缓存是否存在用户收藏市场信息。
                cookieValue = cookieValue.replaceAll("\\+", " ");
                collect = Cache.Get("userCollect" + userId);
                if (!StringUtil.exist(collect)) {
                    CollectMarket collectMarket = (CollectMarket) Data.GetOne("SELECT userId,collect from collectmarket WHERE userId=? ", new Object[]{userId}, CollectMarket.class);
                    if (collectMarket != null) {
                        collect = collectMarket.getCollect();
                    }
                }
                if (!StringUtil.exist(collect)) {
                    //1.2.3 如果不存在历史收藏信息
                    String[] cookieArr = cookieValue.split("\\-");
                    collect = "";
                    for (int i = 0; i < cookieArr.length; i++) {
                        com.alibaba.fastjson.JSONObject marketJson = Market.getMarketByName(cookieArr[i].substring(0, cookieArr[i].indexOf("_hot")));
                        if (null != marketJson) {
                            String fullName = marketJson.getString("numberBiFullName");
                            String marketTmp = marketJson.getString("market") + "_hotdata_" + fullName;
                            if (cookieArr[i].equals(marketTmp)) {
                                collect += ("-" + cookieArr[i]);
                            }
                        }
                    }
                    if (StringUtil.exist(collect)) {
                        collect = collect.substring(1, collect.length());
                        Data.Update("insert into collectmarket (userId,collect) values (?,?) ", new Object[]{userId, collect});
                        Cache.Set("isUserCollect" + userId, "1");
                        Cache.Set("userCollect" + userId, collect);
                    } else {
                        Cache.Set("isUserCollect" + userId, "0");
                    }
                } else {
                    //1.2.4 如果存在历史收藏信息
                    int oldCollectsize = collect.length();
                    String[] cookieArr = cookieValue.split("\\-");
                    for (int i = 0; i < cookieArr.length; i++) {
                        if (StringUtil.exist(cookieArr[i])) {
                            com.alibaba.fastjson.JSONObject marketJson = Market.getMarketByName(cookieArr[i].substring(0, cookieArr[i].indexOf("_hot")));
                            if (null != marketJson) {
                                String fullName = marketJson.getString("numberBiFullName");
                                String marketTmp = marketJson.getString("market") + "_hotdata_" + fullName;
                                if (cookieArr[i].equals(marketTmp)) {
                                    if (collect.indexOf(cookieArr[i]) == -1) {
                                        collect += ("-" + cookieArr[i]);
                                    }
                                }
                            }
                        }
                    }
                    int newCollectsize = collect.length();
                    if (newCollectsize != oldCollectsize) {
                        Data.Update("UPDATE collectmarket SET collect = '" + collect + "' WHERE userId = ? ", new Object[]{userId});
                        Cache.Set("isUserCollect" + userId, "1");
                        Cache.Set("userCollect" + userId, collect);
                    }
                }
            }
        } catch (Exception e) {
            log.error("【获取用户收藏】查询用户：" + userId + "的收藏市场异常，异常信息为：", e);
        }
        if (StringUtil.exist(collect)) {
            collect = collect.replaceAll(" ", "\\+");
        }
        return collect;
    }
    /*end*/


    /*start by kinghao 20180825 新交易平台 :同步用户未登录和登录后的看板信息*/

    /**
     * 获取用户收藏(用于用户登录同步收藏信息)
     *
     * @param userId
     * @param cookieValue
     * @return 1.判断浏览器中的cookie是存在用户未登录状态下关注的市场信息。
     * 1.1 N：不存在的情况下，查询缓存是否存在用户看板标示。
     * 1.1.1 当标示不为0的情况下，要么缓存标示不存在要么用户存在但未同步缓存标示。
     * 1.1.2 查询用户缓存看板信息，判断信息是否存在。
     * 1.1.3 如果缓存不存在，则查询数据库。不管数据库存不存在看板信息，都同步缓存标示和缓存市场信息。缓存标示0：不存在，1：存在
     * 1.2 Y:存在的情况下，查询缓存是否存在用户收看板信息。
     * 1.2.1 如果缓存中市场信息不存在，则查询数据库信息。
     * 1.2.2 判断用户历史看板信息是否存在。
     * 1.2.3 如果不存在历史看板信息，判断cookie中拼接信息是否合法，主要判断是否存在该市场信息，和字符串是否正确。**-**-**
     * 数据合法性校验通过后，将数据存入数据库，并同步缓存信息
     * 1.2.4 如果存在历史看板信息，判断cookie中拼接信息是否合法，更新数据库的看板数据
     * cookie：
     * key：multiTrade
     * value：BTC/ETH-BAT/ETH-BTC/ETC中线隔开
     */
    public String getUserScreen(String userId, String cookieValue) {
        String screen = "---";
        //暂时登录时获取服务器数据覆盖本地cookie
        cookieValue=cookieValue+" ";
        try {
            //1.2 Y:存在的情况下，查询缓存是否存在用户看板信息。
            screen = Cache.Get("userScreen" + userId);
            List<UserScreen> list = new ArrayList<>();
            if (!StringUtil.exist(screen) || "---".equals(screen)) {
                if ("---".equals(cookieValue.trim()) || StringUtils.isEmpty(cookieValue.trim())) {
                    list = Data.QueryT("select id , userId , multiScreen ,groupByScreen, createTime , createBy from userscreen  where userId=?  order by groupByScreen", new Object[]{userId}, UserScreen.class);
                    String createTime = TimeUtil.parseDate(System.currentTimeMillis());
                    if (list.size() < 4) {
                        StringBuilder insertBatchSql = new StringBuilder("insert into userscreen (userId, multiScreen, groupByScreen, createTime, createBy) values ");
                        for (int j = 0; j < 4-list.size(); j++) {
                            insertBatchSql.append("(").append(userId).append(",'',").append(j+1).append(",'").append(createTime).append("',").append(userId).append("),");
//                            Data.Insert("insert into userscreen (userId , multiScreen,groupByScreen ,createTime, createBy) values(?,?,?,?,?)",
//                                    new Object[]{userId, "", j + 1, new Timestamp(System.currentTimeMillis()), userId});
                        }
                        insertBatchSql = insertBatchSql.deleteCharAt(insertBatchSql.length() - 1).append(";");
                        Data.Insert(insertBatchSql.toString(), new Object[]{});
                        logger.info("[看板] 不存在历史记录，且Cookie为空，"+userId+"插入默认看板信息");
                        screen = "---";
                    }
                } /*else {
                    String[] cookieArr = cookieValue.split("\\-");
                    //1.2.4 如果存在历史看板信息，更新数据库数据
                    //1.2.4 如果存在历史看板信息，更新数据库数据
                    List<UserScreen> userScreenList = Data.QueryT("select id , userId , multiScreen ,groupByScreen, createTime , createBy from userscreen where userId=? order by groupByScreen ", new Object[]{userId}, UserScreen.class);
                    if (userScreenList.size() > 0) {
                        for (int i = 0; i < cookieArr.length; i++) {
                            Data.Update("update  userscreen set multiScreen= ? where userId =? and groupByScreen=?",
                                    new Object[]{cookieArr[i], userId, i + 1});
                        }
                        logger.info("[看板] 缓存为空，存在历史记录，且Cookie不为空，"+userId+"更新看板信息"+cookieArr.length+"条:"+cookieValue);
                    } else {
                        for (int i = 0; i < cookieArr.length; i++) {
                            Data.Insert("insert into userscreen (userId , multiScreen,groupByScreen ,createTime, createBy) values(?,?,?,?,?)",
                                    new Object[]{userId, cookieArr[i], i + 1, new Timestamp(System.currentTimeMillis()), userId});
                        }
                        logger.info("[看板] 不存在历史记录，且Cookie不为空，"+userId+"插入看板信息"+cookieArr.length+"条");
                    }
                    screen = cookieValue;
                }*/
                Cache.Set("userScreen" + userId, screen);
            } /*else {
                if ("---".equals(cookieValue.trim())) {
                    List<UserScreen> userScreenList = Data.QueryT("select id , userId , multiScreen ,groupByScreen, createTime , createBy from userscreen where userId=? order by groupByScreen ", new Object[]{userId}, UserScreen.class);
                    StringBuffer stringBuffers = new StringBuffer();
                    for (int k = 0; k < userScreenList.size(); k++) {
                        if (k == userScreenList.size() - 1) {
                            stringBuffers.append(userScreenList.get(k).getMultiScreen());
                        } else {
                            stringBuffers.append(userScreenList.get(k).getMultiScreen() + "-");
                        }
                    }
                    screen = stringBuffers.toString();
                    Cache.Set("userScreen" + userId, screen);
                } else {
                    //1.2.4 如果存在历史看板信息，更新数据库数据
                    String[] cookieArr = cookieValue.split("\\-");
                    for (int i = 0; i < cookieArr.length; i++) {
                        Data.Update("update  userscreen set multiScreen= ? where userId =? and groupByScreen=?",
                                new Object[]{cookieArr[i], userId, i + 1});
                    }
                    screen = cookieValue;
                    Cache.Set("userScreen" + userId, screen);
                    logger.info("[看板] 缓存不为空，存在历史记录，且Cookie不为空，"+userId+"插入看板信息"+cookieArr.length+"条："+cookieValue);

                }
            }*/

        } catch (Exception e) {
            log.error("【获取用户看板】查询用户：" + userId + "的看板异常，异常信息为：", e);
        }
        return screen;
    }


    /**
     * 获取用户安全级别
     *
     * @param user
     * @return SafeLevelType
     */
    public String getSafeLevel(User user) {

        boolean isLoginGoogle = user.isLoginGoogleAuth();
        boolean isTradeAuthe = false;
        /** 修改用户安全级别 谷歌验证或者资金验证（中），并且（高）    by kinghao 20180515 **/
        if (TradeAuthenType.NO_TRADE_PASSWORD_FOR_6H.getKey() == user.getTradeAuthenType() || TradeAuthenType.TRADE_PASSWORD.getKey() == user.getTradeAuthenType()) {
            isTradeAuthe = true;
        }
        if (isTradeAuthe && isLoginGoogle) {
            return SafeLevelType.SAFE_LEVEL_HIGH.getKey() + "";
        } else if (isTradeAuthe || isLoginGoogle) {
            return SafeLevelType.SAFE_LEVEL_MIDDLE.getKey() + "";
        } else {
            return SafeLevelType.SAFE_LEVEL_LOW.getKey() + "";
        }
    }

    //更新导航标志
    public UpdateResults<User> updateGuideFlg(String uid) {
        Datastore ds = super.getDatastore();
        Query<User> q = ds.find(User.class, "_id", uid);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

        ops.set("guideFlg", false);

        UpdateResults<User> ur = super.update(q, ops);
        return ur;
    }



    /**
     * 校验登录邮箱和密码
     * @param email
     * @param password
     * @param flg 是否校验密码
     * @return
     */
    public int checkLoginTimes(User user,String email,String password,boolean flg) {
        LimitType lt = LimitType.LoginError;
        int status = lt.GetStatusNew(email);
        if (status == -1) {
            //已锁定
            return -2;
        }
        if(null == user){
            //记录一次
            lt.UpdateStatus(email);
            status = lt.GetStatusNew(email);
            //已经锁定
            if (status == -1) {
                return -2;
            }
            return lt.GetStatusNew(email);
        }
        if(flg){
            if(!password.equals(user.getPwd())){
                //记录一次
                lt.UpdateStatus(email);
                status = lt.GetStatusNew(email);
                //已经锁定
                if (status == -1) {
                    return -2;
                }
                return lt.GetStatusNew(email);
            }
        }
        if(null != user && password.equals(user.getPwd())){
            //输入正确了，清除
            lt.ClearStatus(email);
            return -3;
        }
        return -3;
    }

    public User getUserInfo(String mobile,String userId){
        Query<User> q2 = getQuery(User.class).filter("_id <>", userId);
        q2.filter("userContact.safeMobile", mobile);
        User user = super.findOne(q2);
        return user;
    }
}

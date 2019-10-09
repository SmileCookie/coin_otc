package com.world.controller.admin.user;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Pattern;

import com.world.constant.Const;
import com.world.controller.api.m.BaseMobileAction;
import com.world.model.entity.statisticalReport.AliveUserCountVo;
import com.yc.entity.SysGroups;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.util.EncryDigestUtil;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.cache.Cache;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.model.LimitType;
import com.world.model.dao.admin.logs.DailyRecordDao;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.dao.app.MarketRemindDao;
import com.world.model.dao.pay.DownloadDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.user.CountryDao;
import com.world.model.dao.user.EmailDao;
import com.world.model.dao.user.MobileDao;
import com.world.model.dao.user.PasslogDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.UserLoginIpDao;
import com.world.model.dao.user.authen.AuthenLogDao;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.admin.logs.DailyRecord;
import com.world.model.entity.admin.logs.DailyType;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DetailsBean;
import com.world.model.entity.pay.DownloadBean;
import com.world.model.entity.pay.KeyBean;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.pay.ReceiveAddr;
import com.world.model.entity.user.Country;
import com.world.model.entity.user.Passlog;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserLoginIp;
import com.world.model.entity.user.authen.AuthUtil;
import com.world.model.entity.user.authen.AuthenType;
import com.world.model.entity.user.version.UserVersion;
import com.world.util.UserUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.action.ApproveAction;
import com.world.web.convention.annotation.FunctionAction;
import com.world.web.sso.SSOLoginManager;

import static com.world.constant.Const.CUSTOMER_OPERATION;
import static com.world.constant.Const.CUSTOMER_TYPE;

@FunctionAction(jspPath = "/admins/user/", des = "用户管理")
public class Index extends AdminAction {
    UserDao dao = new UserDao();
    UserLoginIpDao userLoginIpDao = new UserLoginIpDao();
    AdminUserDao adminDao = new AdminUserDao();
    PayUserDao payDao = new PayUserDao();
    DownloadDao dbdao = new DownloadDao();
    MarketRemindDao remindDao = new MarketRemindDao();
    AuthenLogDao logDao = new AuthenLogDao();

    @Page(Viewer = DEFAULT_INDEX)
    public void index() {
        // 获取参数
        String tab = param("tab");
        int pageNo = intParam("page");
        String userId = param("userId");
        String userName = param("userName").trim();//用户名
        String realName = param("realName").trim();
        String loginIp = param("loginIp").trim();//登录ip
        String recommendName = param("recommendName").trim();
        String mobile = param("mobile").trim();
        String email = param("email").trim();
        boolean needReward = booleanParam("needReward");
        String memo = param("memo");
        int freez = intParam("freez");
        int forbidOut = intParam("forbidOut");
        int combine = intParam("combine");
        String customerType = param("customerType");
        String customerOperation = param("customerOperation");

        String qq = param("qq");
        String weixin = param("weixin");
        String weibo = param("weibo");

        Query<User> q = dao.getQuery();
        int pageSize = 20;

        // 将参数保存为attribute
        try {
            if (userId.length() > 0) {
                q.filter("_id", userId);
            }
            // 构建查询条件
            if (userName.length() > 0) {//用户名
                //Pattern pattern = Pattern.compile("^.*"  + userName+  ".*$" ,  Pattern.CASE_INSENSITIVE);
                q.filter("userName =", userName);
            }
            if (realName.length() > 0) {//用户名
                //Pattern pattern = Pattern.compile("^.*"  + realName+  ".*$" ,  Pattern.CASE_INSENSITIVE);
                q.filter("realName =", realName);
            }
            if (loginIp.length() > 0) {//登录ip
                //Pattern pattern = Pattern.compile("^.*"  + loginIp+  ".*$" ,  Pattern.CASE_INSENSITIVE);
                q.filter("loginIp =", loginIp);
            }
            if (recommendName.length() > 0) {//登录ip
                //Pattern pattern = Pattern.compile("^.*"  + recommendName+  ".*$" ,  Pattern.CASE_INSENSITIVE);
                q.filter("recommendName =", recommendName);
            }
            if (mobile.length() > 0) {//登录ip
                Pattern pattern = Pattern.compile("^.*" + mobile + ".*$", Pattern.CASE_INSENSITIVE);
                q.filter("userContact.safeMobile", pattern);
            }
            if (email.length() > 0) {//登录ip
                //Pattern pattern = Pattern.compile("^.*"  + email+  ".*$" ,  Pattern.CASE_INSENSITIVE);
                q.filter("email =", email);
            }

            if (qq.length() > 0) {
//				Pattern pattern = Pattern.compile("^.*"  + qq+  ".*$" ,  Pattern.CASE_INSENSITIVE);
                q.filter("qq =", qq);
            }
            if (weixin.length() > 0) {
                //Pattern pattern = Pattern.compile("^.*"  + weixin+  ".*$" ,  Pattern.CASE_INSENSITIVE);
                q.filter("weixin =", weixin);
            }
            if (weibo.length() > 0) {
//				Pattern pattern = Pattern.compile("^.*"  + weibo+  ".*$" ,  Pattern.CASE_INSENSITIVE);
                q.filter("weibo =", weibo);
            }

            if (needReward) {
                q.filter("recNeedReward", needReward);
            }

            if (memo.length() > 0) {
                if (memo.equals("6")) {
                    q.field("memo").equal(null);
                } else {
                    q.field("memo").startsWith(memo);
                }
            }

            if (freez > 0) {
                q.filter("freez", true);
            }
            if (StringUtils.isNotEmpty(customerType)) {
                q.filter("customerType", customerType);
            }
            if (StringUtils.isNotEmpty(customerOperation)) {
                q.filter("customerOperation", customerOperation);
            }
            if (forbidOut > 0) {
                if (forbidOut == 1) {
                    q.filter("forbidOutMoney", 1);
                } else if (forbidOut == 2) {
                    q.filter("allowFromBWIn", 1);
                }
            }
            if (combine > 0) {
                if (combine == 5) {
                    q.field("otherUId").notEqual(null).field("otherUId").notEqual("0");
                }
            }

            if (tab.length() == 0) tab = "real";

            if (tab.equals("real")) {
            } else if ("del".equals(tab)) {
                q.filter("isDeleted", true);
            } else if ("spe".equals(tab)) {
                q.filter("version", UserVersion.specialty.getKey());
            } else if ("comm".equals(tab)) {
                q.filter("version", UserVersion.common.getKey());
            } else if ("vip1".equals(tab)) {
                q.filter("version", UserVersion.vip1.getKey());
            } else if ("noreg".equals(tab)) {
                q.field("userName").equal(null);
            }

            if (!"noreg".equals(tab)) {
                q.field("userName").notEqual(null);
            }

            if ("add".equals(tab)) {
                q.field("adminId").notEqual(null);
            } else {
                q.field("adminId").equal(null);
//				q.or(
//						q.criteria("adminId").equal(null),
//						q.criteria("adminId").equal("0")
//				);
            }

            if (!"del".equals(tab)) {
                q.field("isDeleted").notEqual(true);
//				q.or(
//						q.criteria("isDeleted").equal(null),
//						q.criteria("isDeleted").equal(false)
//				);
            }
            long total = 0;
            String queryCondition = q.toString();

            log.info("搜索的sql语句:" + queryCondition);

            String user_total_key = EncryDigestUtil.digest(queryCondition);
            //log.info("user_total_key:" + user_total_key);
            String cacheTotal = Cache.Get(user_total_key);
            if (cacheTotal != null) {
                total = Long.parseLong(cacheTotal);
            } else {
                total = dao.count(q);
                Cache.Set(user_total_key, String.valueOf(total), 60);//应付翻页
            }


            //查数量时就不用排序了
            q.order("- registerTime");
            setAttr("tab", tab);
            DailyRecordDao rDao = new DailyRecordDao();
            if (total > 0) {
                List<User> dataList = dao.findPage(q, pageNo, pageSize);
                List<String> uIdList = new ArrayList<String>();

                String userIds = "";

                for (User u : dataList) {
                    userIds += "," + u.get_Id();
                    uIdList.add(u.getId());
                }

                Map<String, String> memoMap = new HashMap<String, String>();
                Map<String, Integer> memoMapCount = new HashMap<String, Integer>();
                List<DailyRecord> records = rDao.find(rDao.getQuery().filter("userId in", uIdList).order("-createTime")).asList();
                if (null != records && records.size() > 0) {
                    for (DailyRecord dr : records) {
                        int c = 0;
                        if (memoMapCount.containsKey(dr.getUserId())) {
                            c = memoMapCount.get(dr.getUserId());
                        }
                        if (c >= 5) {
                            continue;
                        }
                        memoMapCount.put(dr.getUserId(), c + 1);

                        String memoStr = "";
                        if (memoMap.containsKey(dr.getUserId())) {
                            memoStr = memoMap.get(dr.getUserId());
                        }

                        if (!"".equals(memoStr)) {
                            memoStr += "<br/>";
                        }
                        memoStr += dr.getMemo();
                        memoMap.put(dr.getUserId(), memoStr);

                    }
                }

                setAttr("dataList", dataList);
                setAttr("itemCount", total);
            }
            setAttr("adminName", adminName());
            setPaging((int) total, pageNo, pageSize);
            setAttr("map", Const.CUSTOMER_TYPE);
            setAttr("operationMap", Const.CUSTOMER_OPERATION);
        } catch (Exception ex) {

        }
    }

    // ajax的调用
    @Page(Viewer = DEFAULT_AJAX)
    public void ajax() {
        index();
    }

    @Page(Viewer = "/admins/user/info.jsp")
    public void show() {
        try {
            String userId = param("userId");
            User user = dao.get(userId);
            setAttr("user", user);

            boolean isLockSafePwd = false;
            if (user.getSafePwdModifyTimes() > 1 && null != user.getSafePwdModifyTime() && getDiffDay(now(), user.getSafePwdModifyTime()) < 1) {
                isLockSafePwd = true;
            }
            setAttr("isLockSafePwd", isLockSafePwd);

            JSONArray userArray = new JSONArray();
            Map<String, CoinProps> coinMap = DatabasesUtil.getCoinPropMaps();
            for (Entry<String, CoinProps> entry : coinMap.entrySet()) {
                JSONObject userObj = getUserInfo(userId, entry.getValue());
                userObj.put("coint", entry.getKey().toUpperCase());
                userObj.put("unitTag", entry.getValue().getUnitTag());
                userObj.put("url", entry.getValue().getWeb());
                userArray.add(userObj);
            }
            setAttr("userArray", userArray);

            //密码重置记录
            PasslogDao pDao = new PasslogDao();
            setAttr("pwdLog", pDao.getLogByType(userId, 1));
            setAttr("safeLog", pDao.getLogByType(userId, 3));
            //最近登录ip
            setAttr("ips", new UserLoginIpDao().getIps(userId));

            setAttr("records", findDailyRecord(userId));

            String mCode = user.getUserContact().getmCode();
            if (mCode == null || mCode.length() == 0) {
                mCode = "+86";
            }
            if (StringUtils.isNotEmpty(mCode)) {
                CountryDao dao = new CountryDao();
                Country country = dao.getByField("code", mCode);
                setAttr("country", country);
            }
            setAttr("isDiffAreaLoginNoCheck", user.isDiffAreaLoginNoCheck());
            setAttr("logs", new AuthenLogDao().findAuthenLog(userId));

            Integer cacheAuthTimes = (Integer) Cache.GetObj("simple_auth_" + userId);
            int failAuthTimes = user.getFailAuthTimes();
            setAttr("cacheAuthTimes", cacheAuthTimes == null ? 0 : cacheAuthTimes);
            setAttr("failAuthTimes", failAuthTimes);
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    private List<DailyRecord> findDailyRecord(String userId) {
        DailyRecordDao rDao = new DailyRecordDao();
        List<DailyRecord> records = rDao.find(rDao.getQuery().filter("userId", userId).order("-createTime").limit(5)).asList();
        List<String> adminIds = new ArrayList<String>();
        for (DailyRecord u : records) {
            if (u.getAdminId() != null && u.getAdminId().length() > 0) adminIds.add(u.getAdminId());
        }

        if (adminIds.size() > 0) {
            Map<String, AdminUser> users = new AdminUserDao().getUserMapByIds(adminIds);
            for (DailyRecord u : records) {
                u.setaUser(users.get(u.getAdminId()));
            }
        }
        return records;
    }

    //获取用户资产信息
    private JSONObject getUserInfo(String userId, CoinProps coint) {
        PayUserBean usr = payDao.getById(Integer.parseInt(userId), coint.getFundsType());

        BigDecimal balance = usr.getBalance();
        BigDecimal freez = usr.getFreez();
        BigDecimal dayCash = usr.getDayCash();
        BigDecimal timesCash = usr.getTimesCash();
        BigDecimal dayFreeCash = usr.getDayFreeCash();

        DownloadDao bdDao = new DownloadDao();
        bdDao.setCoint(coint);
        BigDecimal hasUse = bdDao.getTodayFreeCash(userId);

        DecimalFormat df = new DecimalFormat("0.00######");
        JSONObject object = new JSONObject();
        object.put("total", df.format(usr.getTotal()));
        object.put("balance", df.format(balance));
        object.put("freez", df.format(freez));
        object.put("dayCash", df.format(dayCash));
        object.put("timesCash", df.format(timesCash));
        object.put("dayFreeCash", df.format(dayFreeCash));
        object.put("hasUse", df.format(hasUse));

        DetailsBean btcDetails = (DetailsBean) Data.GetOne("SELECT IFNULL(SUM(amount),0) AS amount FROM " + coint.getStag() + "details WHERE (type = 1 OR type = 7) AND `Status` = 2 AND UserId = ?", new Object[]{userId}, DetailsBean.class);
        object.put("chargeTotal", df.format(btcDetails.getAmount()));

        DownloadBean btcDownload = (DownloadBean) Data.GetOne("SELECT IFNULL(SUM(amount),0) AS amount FROM " + coint.getStag() + "download WHERE `status` = 2 AND userId = ?", new Object[]{userId}, DownloadBean.class);
        object.put("cashTotal", df.format(btcDownload.getAmount()));

        List<Bean> btcKeys = (List<Bean>) Data.Query("SELECT * FROM " + coint.getStag() + "key WHERE userId = ? order by keyId desc", new Object[]{userId}, KeyBean.class);
        object.put("chargeAddr", btcKeys);

        List<Bean> btcAddresss = (List<Bean>) Data.Query("SELECT * FROM " + coint.getStag() + "receiveaddr WHERE userId = ? order by createTime", new Object[]{userId}, ReceiveAddr.class);
        object.put("cashAddr", btcAddresss);

        return object;
    }

    public int getDiffDay(Timestamp endTime, Timestamp startTime) {
        long intervalMilli = endTime.getTime() - startTime.getTime();
        return (int) (intervalMilli / (24 * 60 * 60 * 1000));
    }


    @Page(Viewer = "/admins/user/addAuditor.jsp")
    public void addAuditor() {
        try {
            String userId = param("userId");
            setAttr("userId", userId);

            List<AdminUser> list = adminDao.getListByField("admRoleId", 8);
            setAttr("list", list);
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    @Page(Viewer = JSON)
    public void doAdd() {
        try {
            String userId = param("userId");
            String adminId = param("adminId");

            User user = dao.getById(userId);
            boolean hasAdd = false;
            if (user.getAdminId() != null && user.getAdminId().length() > 0) {
                //已分配过客服
                hasAdd = true;
            }

            Datastore ds = dao.getDatastore();
            UpdateOperations<User> operate = ds.createUpdateOperations(User.class);
            operate.set("adminId", adminId);

            UpdateResults<User> ur = dao.update(dao.getQuery(User.class).filter("_id =", userId), operate);
            if (!ur.getHadError()) {

                AdminUser aUser = adminDao.getById(adminId);
                ds = adminDao.getDatastore();
                adminDao.update(adminDao.getQuery().filter("_id =", adminId), ds.createUpdateOperations(AdminUser.class).set("customers", aUser.getCustomers() + 1));

                if (hasAdd) {
                    aUser = adminDao.getById(user.getAdminId());
                    adminDao.update(adminDao.getQuery().filter("_id =", user.getAdminId()), ds.createUpdateOperations(AdminUser.class).set("customers", aUser.getCustomers() - 1));
                }

                json("操作成功。", true, "");
            } else {
                json("操作失败。", false, "");
            }

        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }


    @Page(Viewer = "/admins/user/modifyUserName.jsp")
    public void modifyUserName() {
        String userId = param("userId");
        if (null != userId) {
            User user = dao.getById(userId);
            setAttr("curUser", user);
        }
    }

    @Page(Viewer = JSON)
    public void doModifyUserName() {
        try {
            if (!codeCorrect(JSON)) {
                return;
            }
            String userId = param("userId");
            String userName = param("userName");

            User user = dao.getById(userId);

            Datastore ds = dao.getDatastore();
            UpdateOperations<User> operate = ds.createUpdateOperations(User.class);
            operate.set("userName", userName);

            UpdateResults<User> ur = dao.update(dao.getQuery(User.class).filter("_id =", userId), operate);
            if (!ur.getHadError()) {
                try {
                    //插入一条管理员日志信息
                    DailyType type = DailyType.adminOperate;
                    new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "修改用户用户名", user.getUserName()), String.valueOf(adminId()), ip(), now());
                } catch (Exception e) {
                    log.error("添加管理员日志失败", e);
                }
                json("操作成功。", true, "");
            } else {
                json("操作失败。", false, "");
            }

        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }


    @Page(Viewer = XML)
    public void randomFunc() {
        try {
            String userId = param("userId");
            boolean use = booleanParam("useRandom");

            Datastore ds = dao.getDatastore();
            UpdateOperations<User> operate = ds.createUpdateOperations(User.class);
            operate.set("useRandom", use);
            UpdateResults<User> ur = dao.update(dao.getQuery(User.class).filter("_id =", userId), operate);
            if (!ur.getHadError()) {

                Write("操作成功。", true, "");
            } else {
                Write("操作失败。", false, "");
            }

        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    @Page(Viewer = ".xml")
    public void doDel() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            String userId = param("id");

//			String roleId = GetCookie(Session.rid);
//			if(!roleId.equals("1") && !roleId.equals("2")){
//				Write("权限不足", false, "权限不足。");
//				return;
//			}

            String userIds[] = userId.split("\\,");

            Datastore ds = dao.getDatastore();
            Query<User> q = ds.find(User.class, "_id in", userIds);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("isDeleted", true);

            UpdateResults<User> ur = dao.update(q, ops);
            if (!ur.getHadError()) {
                SSOLoginManager.logout(userId);

                Write("删除成功", true, "删除成功。");
            } else {
                Write("删除失败", false, "删除失败。");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
            Write("未知错误导致删除失败！", false, "");
        }

    }

    @Page(Viewer = ".xml")
    public void doUnlockPhoneCode() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            String userId = param("userId");

            User user = dao.getById(userId);

            if (null != user.getUserContact().getSafeMobile() && !"".equals(user.getUserContact().getSafeMobile())) {
                String m = user.getUserContact().getSafeMobile();
                m = m.replace(" ", "");
                String sessionInfoId = "s_p_" + m;
                Cache.Delete(sessionInfoId);
            }

            WriteRight("解锁成功");
        } catch (Exception ex) {
            log.error("内部异常", ex);
            WriteError("未知错误导致！");
        }

    }

    @Page(Viewer = ".xml")
    public void doUnlockSafePwd() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            String userId = param("userId");

            UpdateResults<User> ur = dao.releaseSecurityPwd(userId);
            if (!ur.getHadError()) {
                WriteRight("解锁成功");
            } else {
                WriteRight("解锁失败");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
            WriteError("未知错误导致！");
        }

    }

    @Page(Viewer = ".xml")
    public void doReturn() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            String userId = param("id");

            String userIds[] = userId.split("\\,");

            Datastore ds = dao.getDatastore();
            Query<User> q = ds.find(User.class, "_id in", userIds);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("isDeleted", false);

            UpdateResults<User> ur = dao.update(q, ops);
            if (!ur.getHadError()) {
                WriteRight("还原成功");
            } else {
                WriteRight("还原失败");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
            Write("未知错误导致还原失败！", false, "");
        }

    }

    @Page(Viewer = "/admins/user/mobile.jsp")
    public void mobile() {
        String userId = param("userId");
        User user = dao.get(userId);
        setAttr("mCode", user.getUserContact().getmCode());
        setAttr("mobile", user.getUserContact().getSafeMobile());
    }

    @Page(Viewer = ".xml")
    public void passMobile() {
        try {
            //手机短信验证
            String code = param("code");
            if (!isCorrect(code, XML)) {
                return;
            }
            //谷歌验证码
            if (!codeCorrect(XML)) {
                return;
            }
            String userId = param("userId");
            String mCode = param("mCode1");
            String mobile = param("mobile");
            String modifyUserName = param("modifyUserName");
            boolean isModifyUserName = false;
            if (modifyUserName.equals("on")) {
                isModifyUserName = true;
            }

            User user = dao.get(userId);

            if (user != null) {

//				if(!dao.mobileValidated(mobile)){
//					WriteError("手机号码已经存在。");
//					return;
//				}

                if (mobile.indexOf("+") < 0) {
                    mobile = mCode + " " + mobile;
                }

                int mobileStatu = user.getUserContact().getMobileStatu();
                Datastore ds = dao.getDatastore();
                Query<User> q = ds.find(User.class, "_id", userId);
                UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

                ops.set("userContact.mCode", mCode);
                ops.set("userContact.safeMobile", mobile);
                ops.set("userContact.mobileCode", "");
                ops.set("userContact.mobileStatu", AuditStatus.pass.getKey());
                ops.set("userContact.checkMobile", "");
                ops.set("userContact.codeTime", new Timestamp(0));
                if (isModifyUserName) {
                    ops.set("userName", mobile.replace(mCode, "").replace("+86", "").replace(" ", ""));
                }

                UpdateResults<User> ur = dao.update(q, ops);

                if (!ur.getHadError()) {
                    if (mobileStatu == AuditStatus.pass.getKey()) {
                        WriteRight("认证手机修改成功。");
                        logDao.insertOneRecord(AuthenType.mobile.getKey(), userId, String.valueOf(adminId()), "管理员" + adminName() + "修改认证手机为：" + mobile, ip());
                        try {
                            //插入一条管理员日志信息
                            DailyType type = DailyType.userMobileUpdate;
                            new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "修改用户手机", user.getUserName(), mobile), String.valueOf(adminId()), ip(), now());
                        } catch (Exception e) {
                            log.error("添加管理员日志失败", e);
                        }
                    } else {
                        WriteRight("手机认证成功。");
                        try {
                            logDao.insertOneRecord(AuthenType.mobile.getKey(), userId, String.valueOf(adminId()), "管理员" + adminName() + "通过手机认证，认证手机为：" + mobile, ip());
                            //插入一条管理员日志信息
                            DailyType type = DailyType.userMobilePass;
                            new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "认证用户手机", user.getUserName(), mobile), String.valueOf(adminId()), ip(), now());
                        } catch (Exception e) {
                            log.error("添加管理员日志失败", e);
                        }
                    }
                } else {
                    WriteError("操作失败。");
                }
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    @Page(Viewer = XML)
    public void cancelMobile() {
        try {
            //手机短信验证
//			String code = param("code");
//			if(!isCorrect(code, XML)){
//				return;
//			}
            //谷歌验证码
            if (!codeCorrect(XML)) {
                return;
            }

            String userId = param("userId");
            User user = dao.get(userId);

            if (user.getUserContact().getMobileStatu() != AuditStatus.pass.getKey()) {
                WriteError("该用户还没有通过手机认证");
                return;
            }

            Datastore ds = dao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

            ops.set("userContact.mCode", "");
            ops.set("userContact.safeMobile", "");
            ops.set("userContact.mobileCode", "");
            ops.set("userContact.mobileStatu", AuditStatus.noSubmit.getKey());
            ops.set("userContact.checkMobile", "");
            ops.set("userContact.codeTime", new Timestamp(0));

            UpdateResults<User> ur = dao.update(q, ops);

            if (!ur.getHadError()) {

                try {
                    //插入一条管理员日志信息
                    DailyType type = DailyType.userMobileCancel;
                    new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "取消用户手机", user.getUserName(), user.getUserContact().getSafeMobile()), String.valueOf(adminId()), ip(), now());
                } catch (Exception e) {
                    log.error("添加管理员日志失败", e);
                }
                logDao.insertOneRecord(AuthenType.mobile.getKey(), userId, adminId() + "", "管理员" + adminName() + "取消手机认证。", ip());
                WriteRight("该用户手机认证已取消");
            } else {
                WriteError("取消手机认证失败");
            }

        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    @Page(Viewer = "/admins/user/email.jsp")
    public void email() {
        String userId = param("userId");
        User user = dao.get(userId);
        setAttr("email", user.getUserContact().getSafeEmail());
    }

    @Page(Viewer = ".xml")
    public void passEmail() {
        try {
            String userId = param("userId");
            String email = param("email");

            //手机短信验证
            String code = param("code");
            if (!isCorrect(code, XML)) {
                return;
            }
            //谷歌验证码
            if (!codeCorrect(XML)) {
                return;
            }

            User user = dao.get(userId);

            if (user != null) {

//				if(!dao.emailValidated(email)){
//					WriteError("邮箱已经存在。");
//					return;
//				}
                String adminId = String.valueOf(adminId());
                int emailStatu = user.getUserContact().getEmailStatu();

                Datastore ds = dao.getDatastore();
                Query<User> q = ds.find(User.class, "_id", userId);
                UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

                ops.set("userContact.safeEmail", email);
                ops.set("email", email);
                ops.set("userContact.emailCode", "");
                ops.set("userContact.checkEmail", "");
                ops.set("userContact.emailStatu", AuditStatus.pass.getKey());
                ops.set("userContact.emailTime", new Timestamp(0));

                UpdateResults<User> ur = dao.update(q, ops);

                if (!ur.getHadError()) {
                    if (emailStatu == AuditStatus.pass.getKey()) {
                        WriteRight("认证邮箱修改成功。");
                        logDao.insertOneRecord(AuthenType.email.getKey(), userId, adminId, "管理员" + adminName() + "修改认证邮箱为：" + email, ip());
                        try {
                            //插入一条管理员日志信息
                            DailyType type = DailyType.userEmailUpdate;
                            new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "修改用户邮箱", user.getUserName(), email), String.valueOf(adminId()), ip(), now());
                        } catch (Exception e) {
                            log.error("添加管理员日志失败", e);
                        }
                    } else {
                        WriteRight("邮箱认证成功。");
                        logDao.insertOneRecord(AuthenType.email.getKey(), userId, adminId, "管理员" + adminName() + "通过邮箱认证，认证邮箱为：" + email, ip());
                        try {
                            //插入一条管理员日志信息
                            DailyType type = DailyType.userEmailPass;
                            new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "认证用户邮箱", user.getUserName(), email), String.valueOf(adminId()), ip(), now());
                        } catch (Exception e) {
                            log.error("添加管理员日志失败", e);
                        }
                    }
                } else {
                    WriteError("操作失败。");
                }

            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    @Page(Viewer = XML)
    public void cancelEmail() {
        try {
            if (true) return;
            String userId = param("userId");
            User user = dao.get(userId);

            if (user.getUserContact().getEmailStatu() != AuditStatus.pass.getKey()) {
                WriteError("该用户还没有通过邮箱认证");
                return;
            }

            Datastore ds = dao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

            ops.set("userContact.safeEmail", "");
            ops.set("userContact.emailCode", "");
            ops.set("userContact.checkEmail", "");
            ops.set("userContact.emailStatu", AuditStatus.noSubmit.getKey());
            ops.set("userContact.emailTime", new Timestamp(0));

            UpdateResults<User> ur = dao.update(q, ops);

            if (!ur.getHadError()) {
                WriteRight("该用户邮箱认证已取消");

                try {
                    //插入一条管理员日志信息
                    DailyType type = DailyType.userEmailCancel;
                    new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "取消用户邮箱", user.getUserName(), user.getUserContact().getSafeEmail()), String.valueOf(adminId()), ip(), now());
                } catch (Exception e) {
                    log.error("添加管理员日志失败", e);
                }
            } else {
                WriteError("取消邮箱认证失败");
            }

        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }


    @Page(Viewer = XML)
    public void setCanDifBankRealName() {
        try {
            String userId = param("userId");
            int val = intParam("val");
            int update = new PayUserDao().update("update pay_user set canDifBankRealName=" + val + " where user_id=" + userId, new Object[]{});

            if (update > 0) {
                WriteRight("操作成功");
            } else {
                WriteError("操作失败");
            }


        } catch (Exception ex) {
            log.error("内部异常", ex);
            WriteError("操作失败");
        }
    }

    /**
     * 设置/修改推荐人
     */
    @Page(Viewer = "/admins/user/recommen.jsp")
    public void updateTuijian() {
        String userId = param("userId");

        User user = dao.get(userId);
        setAttr("user", user);
    }

    /**
     * 保存修改
     */
    @Page(Viewer = ".xml")
    public void saveTuijian() {
        if (!codeCorrect(XML)) {
            return;
        }
        try {
            String userId = param("userId");
            String userName = param("userName");

            User user = dao.get(userId);

            String tuijianUserId = "";
            if (userName != null && userName.length() > 0) {
                User tuijianUser = dao.getByField("userName", userName);
                if (tuijianUser == null) {
                    WriteError("设置的推荐用户不存在。");
                    return;
                }
                tuijianUserId = tuijianUser.getId();
                if (tuijianUserId.equals(userId)) {
                    WriteError("推荐人不能是用户自己。");
                    return;
                }
                if (tuijianUser.getRecommendName().equals(user.getUserName())) {
                    WriteError("推荐人不能互相推荐。");
                    return;
                }
            } else {
                userName = "";
            }

            //String memo = adminName() + "于" + now() + "修改推荐人为" + userName + ",ip为：" + ip() + "<br/>\n";

            Datastore ds = dao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

            //JSONObject object = user.getMemo() != null ? JSONObject.fromObject(user.getMemo()) : new JSONObject();
            //object.put(System.currentTimeMillis(), memo);
            ops.set("recommendId", tuijianUserId);
            ops.set("recommendName", userName);
            //ops.set("memo", object.toString());

            UpdateResults<User> ur = dao.update(q, ops);

            if (!ur.getHadError()) {
                Write("设置成功", true, "推荐人修改成功。");

                try {
                    //插入一条管理员日志信息
                    DailyType type = DailyType.userRecommenUpdate;
                    new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "修改推荐人", user.getUserName(), userName, user.getRecommendName()), String.valueOf(adminId()), ip(), now());
                } catch (Exception e) {
                    log.error("添加管理员日志失败", e);
                }
            } else {
                Write("修改失败。", false, "修改失败。");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    /**
     * 更改用户的提现额度
     */
    @Page(Viewer = "/admins/user/dayCash.jsp")
    public void dayCash() {
        String userId = param("userId");
        String type = param("type");

        User user = dao.get(userId);
        setAttr("user", user);

        PayUserBean payUser = payDao.getById(Integer.parseInt(userId), coint.getFundsType());
        setAttr("dayCash", type.equals("dayCash") ? payUser.getDayCash() : type.equals("timesCash") ? payUser.getTimesCash() : payUser.getDayFreeCash());

        setAttr("type", type);
    }

    /**
     * 保存修改
     */
    @Page(Viewer = ".xml")
    public void saveDayCash() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }

            String userId = param("userId");
            BigDecimal dayCash = decimalParam("dayCash");
            String column = param("type");
            int count = Data.Update("UPDATE pay_user SET " + column + " = ? WHERE userId = ? AND fundsType = ?", new Object[]{dayCash, userId, coint.getFundsType()});
            if (count > 0) {
                User user = dao.get(userId);
                try {
                    //插入一条管理员日志信息
                    DailyType type = DailyType.userDayCash;
                    new DailyRecordDao().insertOneRecord(type, "成功修改用户" + user.getUserName() + "的" + coint.getTag() + " " + column + "额度为：" + String.valueOf(dayCash), String.valueOf(adminId()), ip(), now());

                    logDao.insertOneRecord(AuthenType.cashSet.getKey(), userId, String.valueOf(adminId()), "管理员" + adminName() + "修改" + coint.getTag() + "的" + column + "额度为：" + String.valueOf(dayCash), ip());
                } catch (Exception e) {
                    log.error("添加管理员日志失败", e);
                }

                Write("修改成功", true, "提现额度修改成功。");
            } else {
                Write("修改失败。", false, "修改失败。");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    // 清理一个用户的安全密码
    @Page(Viewer = ".xml")
    public void clearsafelock() {
        if (!codeCorrect(XML)) {
            return;
        }
        try {
            String userid = GetPrama(0);
            LimitType.SafePassError.ClearStatus(userid);
            WriteRight("成功为该用户解除安全密码锁定！");
        } catch (Exception ex) {
            try {
                WriteError("为该用户解除安全密码锁定失败！");
            } catch (Exception e) {
                log.error("内部异常", e);
            }
        }
    }

    // 清理一个用户的ip登录限制
    @Page(Viewer = ".xml")
    public void unLockIps() {
        if (!codeCorrect(XML)) {
            return;
        }
        try {
            String userId = GetPrama(0);
            List<UserLoginIp> userLoginIpsList = userLoginIpDao.getAllIps(userId);
            for (UserLoginIp u : userLoginIpsList) {
                Cache.Delete(sessionId + "_" + u.getIp());
            }
            WriteRight("成功为该用户解除异地登录锁定！");
        } catch (Exception ex) {
            try {
                WriteError("为该用户解除异地登录锁定失败！");
            } catch (Exception e) {
                log.error("内部异常", e);
            }
        }
    }

    // 关闭一个用户的ip登录限制短信验证
    @Page(Viewer = ".xml")
    public void closedIpsSmsValidated() {
        if (!codeCorrect(XML)) {
            return;
        }
        try {
            String userId = GetPrama(0);
            Datastore ds = dao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

            ops.set("diffAreaLoginNoCheck", true);
            dao.update(q, ops);
            WriteRight("成功为该用户关闭IP登录短信限制！");
        } catch (Exception ex) {
            try {
                WriteError("为该用户关闭IP登录短信失败！");
            } catch (Exception e) {
                log.error("内部异常", e);
            }
        }
    }

    // 开启一个用户的ip登录限制短信验证
    @Page(Viewer = ".xml")
    public void openIpsSmsValidated() {
        if (!codeCorrect(XML)) {
            return;
        }
        try {
            String userId = GetPrama(0);
            Datastore ds = dao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

            ops.set("diffAreaLoginNoCheck", false);
            dao.update(q, ops);
            WriteRight("成功为该用户开启IP登录短信限制！");
        } catch (Exception ex) {
            log.error("内部异常", ex);
            WriteError("为该用户开启IP登录短信失败！");
        }
    }

    // 清理登录密码锁定
    @Page(Viewer = ".xml")
    public void clearlock() {
        if (!codeCorrect(XML)) {
            return;
        }
        try {
            String userid = GetPrama(0);
            LimitType.LoginError.ClearStatus(userid);
            WriteRight("成功为该用户解除登录密码锁定！");
        } catch (Exception ex) {
            log.error("内部异常", ex);
            WriteError("为该用户解除登录密码锁定失败！");
        }
    }

    // 解锁手机短信
    @Page(Viewer = ".xml")
    public void clearMobile() {
        if (!codeCorrect(XML)) {
            return;
        }
        try {
            String userId = GetPrama(0);
            ApproveAction.clearMobile(userId);

            WriteRight("成功为该用户解除手机锁定！");
        } catch (Exception ex) {
            log.error("内部异常", ex);
            WriteError("为该用户解除手机锁定失败！");
        }
    }

    // 解锁双重认证
    @Page(Viewer = ".xml")
    public void clearGoogle() {
        try {
            String userId = GetPrama(0);
            ApproveAction.clearGoogle(userId);

            WriteRight("成功为该用户解除谷歌认证锁定！");
        } catch (Exception ex) {
            log.error("内部异常", ex);
            WriteError("为该用户解除谷歌认证锁定失败！");
        }
    }

    // 取消双重认证
    @Page(Viewer = ".xml")
    public void cancelGoogle() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            String userId = param("userId");
            User user = dao.get(userId);
            if (!new UserDao().updateGoogleAu(userId, 0).getHadError()) {
                WriteRight("成功取消该用户谷歌双重验证");

                try {
                    //插入一条管理员日志信息
                    DailyType type = DailyType.userGoogleCancel;
                    new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "取消用户谷歌认证", user.getUserName(), user.getUserContact().getSecret()), String.valueOf(adminId()), ip(), now());
                } catch (Exception e) {
                    log.error("添加管理员日志失败", e);
                }
            } else {
                WriteError("为该用户取消谷歌认证失败！");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
            WriteError("为该用户取消谷歌认证失败！");
        }
    }

//	// 赠送推荐人
//	@Page(Viewer = ".xml")
//	public void zsRecommender() {
//		try {
//			UserDao userDao = new UserDao();
//			RewardRecordDao rrDao = new RewardRecordDao();
//			String userId = param("uid");
//			User u = getUserByUserId(Integer.parseInt(userId));
//			int recommendUserId = 0;
//			User recommendUser = null;
//			if(u.getRecommendName() != null){//卖家推荐人
//				recommendUser = new UserDao().getUserByColumn(u.getRecommendName(),"userName");
//				if(recommendUser != null){
//					recommendUserId = Integer.parseInt(recommendUser.getId());
//				}
//			}
//			//推荐人奖励
//			if(recommendUserId > 0 && u.isRecNeedReward()){
//			////执行奖励
//				RewardRecord rr2 = new RewardRecord(rrDao.getDatastore());
//				rr2.setUserId(recommendUser.getId());
//				rr2.setUserName(recommendUser.getUserName());
//				rr2.setType(RewardSource.recommendUserPhoneCertification.getKey());
//				rr2.setDate(now());
//				rr2.setIp(ip());
//				rr2.setOther(u.getUserName());
//
//				Datastore ds = userDao.getDatastore();
//				Query<User> q = ds.find(User.class, "_id", userId);
//				UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
//				ops = ds.createUpdateOperations(User.class);
//				ops.set("recNeedReward", false);
//				userDao.update(q, ops);
//
//				if(rrDao.reward(rr2)){
//					WriteRight("成功奖励推荐人。");
//					return;
//				}
//			}
//
//		} catch (Exception ex) {
//			WriteError("为该用户赠送推荐人失败！");
//			log.error(ex.toString(), ex);
//		}
//		WriteError("奖励推荐人失败");
//	}

    @Page(Viewer = "/admins/user/clearIp.jsp")
    public void clearIp() {

    }

    // 清理ip
    @Page(Viewer = ".xml")
    public void doClear() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            String ip = param("ip");
            int due = 0;
            List<User> list = dao.find(dao.getQuery().filter("loginIp", ip)).asList();
            if (list != null && list.size() >= 3) {

                Datastore ds = dao.getDatastore();
                Query<User> q = ds.find(User.class, "loginIp", ip);
                UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

                ops.set("loginIp", "");

                UpdateResults<User> ur = dao.update(q, ops);
                if (!ur.getHadError()) {
                    due = 1;
                }
            }

            String cacheIp = Cache.Get("email_" + ip);
            if (cacheIp != null) {
                int count = Integer.parseInt(cacheIp);
                if (count >= 19) {
                    Cache.Delete("email_" + ip);
                }
                due = 2;
            }

            if (due == 2) {
                WriteRight("缓存清理成功。");
            } else if (due == 1) {
                WriteRight("IP清理成功。");
            } else {
                WriteError("找不到此IP，不需要清理。");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
            WriteError("操作失败。");
        }
    }

    @Page
    public void loginuser() {
        try {
            if (!codeCorrect(JSON)) {
                return;
            }
            String username = param("uid");
            User user = dao.getByField("_id", username);

            String userId = user.getId();
            int remember = 3600;
            SSOLoginManager.toLogin(this, remember, userId, user.getUserName(), true, ip(), user.getVipRate() + "", "", false);

            try {
                //插入一条管理员日志信息
                DailyType type = DailyType.userLogin;
                new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "登录用户账号", user.getUserName()), String.valueOf(adminId()), ip(), now());
            } catch (Exception e) {
                log.error("添加管理员日志失败", e);
            }

            response.sendRedirect(VIP_DOMAIN + "/manage/");

        } catch (Exception ex) {
            log.error("内部异常", ex);
            try {
                response.getWriter().write("登录失败");
            } catch (IOException e) {
                log.error("内部异常-inner", ex);
            }
        }
    }

    PasslogDao pDao = new PasslogDao();

    @Page(Viewer = ".xml")
    public void updatePwd() {
        try {

            if (!codeCorrect(XML)) {
                return;
            }
            String userId = GetPrama(0);
            User user = dao.getUserById(userId);

            Passlog logs = pDao.findOne(pDao.getQuery().filter("userId", userId).filter("isDeleted", false).filter("type", 1));
            if (logs != null) {
                WriteError("登录密码已经初始化，现在还未还原");
                return;
            }

            String password = user.getPwd();

            logs = new Passlog(pDao.getDatastore());
            logs.setUserId(userId);
            logs.setAdminId("" + adminId());
            logs.setPassword(password);
            logs.setIp(ip());
            logs.setCover(1);
            logs.setUpdateTime(now());
            logs.setType(1);

            pDao.save(logs);

            String newPwd = EmailDao.GetRadomStr();
            dao.updatePwd(userId, newPwd, 40);

            try {
                //插入一条管理员日志信息
                DailyType type = DailyType.userUpdatePwd;
                new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "重置登录密码", user.getUserName(), ""), String.valueOf(adminId()), ip(), now());
            } catch (Exception e) {
                log.error("添加管理员日志失败", e);
            }
            try {
                EmailDao eDao = new EmailDao();
                user.setEmail(user.getUserContact().getEmailStatu() == AuditStatus.pass.getKey() ? user.getUserContact().getSafeEmail() : user.getEmail());
                String info = eDao.getPwdEmailHtml(user, newPwd, "pwd", this);
                SysGroups sg = SysGroups.vip;
                String title = L(SysGroups.vip.getValue()) + " " + L("重置登录密码");
                eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, user.getEmail());
            } catch (Exception e) {
                log.error(e.toString(), e);
            }

            WriteRight(user.getUserName() + L("的登录密码已成功初始化，邮件已发送至") + UserUtil.shortEmail(user.getEmail()) + "。");//，初始化的密码为【"+newPwd+"】
        } catch (Exception ex) {
            log.error("内部异常", ex);
            Write(L("登录密码初始化失败"), false, "");
        }
    }

    @Page(Viewer = ".xml")
    public void restorePwd() {

        try {
            String userId = GetPrama(0);
            Passlog logs = pDao.findOne(pDao.getQuery().filter("userId", userId).filter("isDeleted", false).filter("type", 1));
            if (logs == null) {
                Write("当前用户登录密码没有初始化信息", false, "");
                return;
            } else {
                String password = logs.getPassword();
                Datastore ds = dao.getDatastore();
                Query<User> q = ds.find(User.class, "_id", userId);
                UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
                ops.set("pwd", password);

                UpdateResults<User> ur = dao.update(q, ops);
                if (!ur.getHadError()) {
                    pDao.update(pDao.getQuery().filter("userId", userId).filter("type", 1), pDao.getDatastore().createUpdateOperations(Passlog.class).set("type", 2));
                }
            }

            try {
                User user = dao.get(userId);
                //插入一条管理员日志信息
                DailyType type = DailyType.userRestorePwd;
                new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "还原登录密码", user.getUserName()), String.valueOf(adminId()), ip(), now());
            } catch (Exception e) {
                log.error("添加管理员日志失败", e);
            }

            Write("登录密码还原成功", true, "登录密码还原成功");
        } catch (Exception ex) {
            log.error("内部异常", ex);
            Write("登录密码还原失败", false, "");
        }
    }

    @Page(Viewer = ".xml")
    public void updateSafePwd() {
        try {

            if (!codeCorrect(XML)) {
                return;
            }
            String userId = GetPrama(0);
            User user = dao.getUserById(userId);

            Passlog logs = pDao.findOne(pDao.getQuery().filter("userId", userId).filter("isDeleted", false).filter("type", 3));
            if (logs != null) {
                WriteError("安全密码已经初始化，现在还未还原");
                return;
            }

            String safePwd = user.getSafePwd();

            logs = new Passlog(pDao.getDatastore());
            logs.setUserId(userId);
            logs.setAdminId("" + adminId());
            logs.setSafePass(safePwd);
            logs.setIp(ip());
            logs.setCover(1);
            logs.setUpdateTime(now());
            logs.setType(3);

            pDao.save(logs);

            String newPwd = EmailDao.GetRadomStr();
            dao.updateSecurityPwd(userId, newPwd);

            try {
                //插入一条管理员日志信息
                DailyType type = DailyType.userUpdateSafePwd;
                new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "重置安全密码", user.getUserName(), ""), String.valueOf(adminId()), ip(), now());
            } catch (Exception e) {
                log.error("添加管理员日志失败", e);
            }

            try {
                EmailDao eDao = new EmailDao();
                user.setEmail(user.getUserContact().getEmailStatu() == AuditStatus.pass.getKey() ? user.getUserContact().getSafeEmail() : user.getEmail());
                String info = eDao.getPwdEmailHtml(user, newPwd, "safe", this);
                SysGroups sg = SysGroups.vip;
                String title = L(SysGroups.vip.getValue()) + " " + L("重置登录密码");
                eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, user.getEmail());
            } catch (Exception ex) {
                log.error("内部异常", ex);
            }

            WriteRight(user.getUserName() + "的安全密码已成功初始化，邮件已发送至" + UserUtil.shortEmail(user.getEmail()) + "。");
        } catch (Exception ex) {
            Write("安全密码初始化失败", false, "");
        }
    }

    @Page(Viewer = ".xml")
    public void restoreSafePwd() {

        try {
            String userId = GetPrama(0);
            Passlog logs = pDao.findOne(pDao.getQuery().filter("userId", userId).filter("isDeleted", false).filter("type", 3));
            if (logs == null) {
                Write("当前用户安全密码没有初始化信息", false, "");
                return;
            } else {
                String safePwd = logs.getSafePass();
                Datastore ds = dao.getDatastore();
                Query<User> q = ds.find(User.class, "_id", userId);
                UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
                ops.set("safePwd", safePwd);

                UpdateResults<User> ur = dao.update(q, ops);
                if (!ur.getHadError()) {
                    pDao.update(pDao.getQuery().filter("userId", userId).filter("type", 3), pDao.getDatastore().createUpdateOperations(Passlog.class).set("type", 4));
                }
            }

            try {
                //插入一条管理员日志信息
                User user = dao.get(userId);
                DailyType type = DailyType.userRestoreSafePwd;
                new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "还原安全密码", user.getUserName()), String.valueOf(adminId()), ip(), now());
            } catch (Exception ex) {
                log.error("内部异常", ex);
            }

            Write("安全密码还原成功", true, "安全密码还原成功");
        } catch (Exception ex) {
            log.error("内部异常", ex);
            Write("安全密码还原失败", false, "");
        }
    }

    /**
     * 发送短信验证码到邮箱
     */
    @Page(Viewer = ".xml")
    public void sendEmail() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            String userId = param("userId");
            User user = dao.getUserById(userId);

            Timestamp time = user.getUserContact().getMobileCodeEmailTime();
            if (time != null && now().getTime() - time.getTime() <= 60 * 2 * 1000) {
                WriteError("两分钟之内不能重复发送邮件。");
                return;
            }
            user.setEmail(user.getUserContact().getEmailStatu() == AuditStatus.pass.getKey() ? user.getUserContact().getSafeEmail() : user.getEmail());
            try {
                //插入一条管理员日志信息
                DailyType type = DailyType.sendMobileCode;
                new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "发送邮件接收短信验证码", user.getUserName(), user.getUserContact().getMobileCode(), UserUtil.shortEmail(user.getEmail())), String.valueOf(adminId()), ip(), now());
            } catch (Exception e) {
                log.error("添加管理员日志失败", e);
            }

            try {
                EmailDao eDao = new EmailDao();

                int type = 0;
                String ip = "";
                String code = user.getUserContact().getMobileCode();
                if (StringUtils.isNotEmpty(code) && code.indexOf("_") > 0) {
                    type = Integer.parseInt(code.split("\\_")[0]);
                    ip = code.split("\\_")[1];
                }
                code = MobileDao.GetRadomStr(1);
                log.info(code);

                String info = eDao.getCodeEmailHtml(user, code, this);
                SysGroups sg = SysGroups.vip;
                String title = L(SysGroups.vip.getValue()) + " " + L("发送手机验证码");
                eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, user.getEmail());

                dao.updateCodeEmailTime(userId);

                if (type > 0) {
                    code = UserUtil.secretMobileCode(type, ip, code);
                }
                UpdateResults<User> ur = dao.updateMobileCode(userId, null, code);
                if (!ur.getHadError()) {
                    log.info("验证码更新成功");
                } else {
                    log.info("验证码更新失败");
                }

            } catch (Exception ex) {
                log.error("内部异常", ex);
            }

            WriteRight(user.getUserName() + "的短信验证码已发送邮件至" + UserUtil.shortEmail(user.getEmail()) + "。");
        } catch (Exception ex) {
            log.error("内部异常", ex);
            Write("发送邮件失败", false, "");
        }
    }

    @Page(Viewer = "/admins/user/addMemo.jsp")
    public void addMemo() {
        try {
            String userId = request.getParameter("userId");
//    		DailyRecordDao rDao = new DailyRecordDao();
//    		DailyRecord record = rDao.findOne(rDao.getQuery().filter("userId", userId).order("-createTime"));
//    		if(record == null)
//    			setAttr("memo", "");
//    		else
//    			setAttr("memo", record.getMemo());
            setAttr("userId", userId);
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    @Page(Viewer = XML)
    public void saveMemo() {
        try {
            String memo = request.getParameter("memo");
            String userId = request.getParameter("userId");
            String loadImg = param("loadImg");
            //插入一条管理员日志信息
            DailyType type = DailyType.userMemo;
            String result = new DailyRecordDao().insertOneRecord(type, userId, memo, String.valueOf(adminId()), ip(), now(), loadImg);
            //log.info(result);
            if (StringUtils.isNotEmpty(result)) Write("成功添加用户备注信息", true, "");
            else Write("添加用户备注信息失败", false, "");
        } catch (Exception ex) {
            log.error("内部异常", ex);
            Write("添加用户备注信息失败", false, "");
        }
    }

    /**
     * 标记比特币充值地址
     */
    @Page(Viewer = XML)
    public void tagBtcAddr() {
        try {
            String btckeyid = request.getParameter("btckeyid");
            String coint = param("coint");
            int i = Data.Update("update " + coint + "key set usedTimes=1,tag=1 where keyId=?", new Object[]{btckeyid});
            if (i > 0) WriteRight("已成功标记该" + coint + "充值地址");
            else WriteError("标记该" + coint + "充值地址失败");
        } catch (Exception ex) {
            log.error("内部异常", ex);
            WriteError("标记该" + coint + "充值地址失败");
        }
    }

    /**
     * 绑定比特币充值地址，矿池用户不想频繁换地址，可以给用户绑定充值地址，标记地址tag为2(只有未使用过的地址才可以标记)。读地址时，读使用次数=0或者tag=2的。
     */
    @Page(Viewer = XML)
    public void BindBtcAddr() {
        try {
            String userId = param("userId");
            String btcKeyId = param("btckeyid");

            KeyBean bKey = (KeyBean) Data.GetOne("SELECT * FROM btckey WHERE userId = ? AND btckeyId = ?", new Object[]{userId, btcKeyId}, KeyBean.class);
            if (bKey == null) {
                WriteError("地址不存在。");
                return;
            }
            int tag = 0;
            String des = "";
            if (bKey.getTag() == 2) {//已绑定，本次解绑
                log.info("解除用户绑定的地址。");
                des = "该地址已成功解除绑定。";
            } else {
                KeyBean bindKey = (KeyBean) Data.GetOne("SELECT * FROM btckey WHERE userId = ? AND tag = 2", new Object[]{userId}, KeyBean.class);
                if (bindKey != null) {
                    WriteError("该用户已绑定过地址，不能再绑定其他地址。");
                    return;
                }
                if (bKey.getUsedTimes() <= 0) {
                    tag = 2;
                    des = "该地址已成功绑定";
                } else {
                    WriteError("该地址已充过值，绑定地址时只能绑定新地址。");
                    return;
                }
            }

            int i = Data.Update("update btckey set tag=? where btcKeyId=?", new Object[]{tag, btcKeyId});
            if (i > 0) {
                WriteRight(des);
            } else {
                WriteError("更新数据失败。");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
            WriteError("更新数据异常。");
        }
    }

    @Page(Viewer = ".xml")
    public void doClearNoreg() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }

            String userId = param("userId");
            Query<User> q = dao.getQuery().field("userName").equal(null);
            if (userId.length() > 0) {
                q.filter("_id", userId);
            }
            dao.deleteByQuery(q);
            WriteRight("清理成功");
        } catch (Exception ex) {
            log.error("内部异常", ex);
            Write("未知错误导致还原失败！", false, "");
        }

    }

    /****
     * 买家 添加备注
     */
    @Page(Viewer = "/admins/user/visit.jsp")
    public void visit() {
        String userId = param("userId");
        User user = dao.get(userId);
        request.setAttribute("userId", user.getId());
        request.setAttribute("memo", user.getMemo());
    }

    /****
     * 跳转设置级别页面
     */
    @Page(Viewer = "/admins/user/user_version.jsp")
    public void versionPage() {
        String userId = param("userId");
        User user = dao.get(userId);
//		request.setAttribute("userId", user.getId());
//		request.setAttribute("version", user.getVersion());
        setAttr("user", user);
    }

    /****
     * 设置级别
     */
    @Page(Viewer = ".xml")
    public void setVersion() {
        try {
            String userId = param("userId");
            User user = dao.get(userId);
//			int oldVersion = user.getVersion();//原版本
            int version = intParam("version");

            Datastore ds = dao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("version", version);
            UpdateResults<User> ur = dao.update(q, ops);

            if (!ur.getHadError()) {
                int count = Data.Update("UPDATE pay_user SET version = ? WHERE user_Id = ?", new Object[]{version, userId});
                if (count > 0) {
                    //插入一条管理员日志信息
                    DailyType type = DailyType.adminOperate;
                    String sVersion = version == 0 ? "大众版" : "专业版";
                    if (version == 2) sVersion = "贵宾版";
                    new DailyRecordDao().insertOneRecord(type, "成功设置账户[" + user.getUserName() + "] 为 [" + sVersion + "]", String.valueOf(adminId()), ip(), now());
                    Write("设置成功", true, "设置级别成功。");
                } else {
                    Write("设置失败。", false, "设置失败。");
                }
            } else {
                Write("设置失败。", false, "设置失败。");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    @Page(Viewer = ".xml")
    public void addVisit() {
        //获取备注信息
        String userId = param("userId");
        String memo = request.getParameter("memo");
        Datastore ds = dao.getDatastore();
        Query<User> q = ds.find(User.class, "_id", userId);
        UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
        ops.set("memo", memo);

        UpdateResults<User> ur = dao.update(q, ops);
        if (!ur.getHadError()) {//备注成功

            //插入一条管理员日志信息
            DailyType type = DailyType.userVisit;
            new DailyRecordDao().insertOneRecord(type, userId, memo, String.valueOf(adminId()), ip(), now(), "");

            Write("拜访成功", true, "");
        } else {
            Write("添加备注失败", false, "");
        }
    }

    @Page(Viewer = XML)
    public void doLimitAccessApi() {
        if (!codeCorrect(XML)) {
            return;
        }
        try {
            String userId = param("userId");
            int apiLimitCount = intParam("apiLimitCount");

            int rtn = Data.Update("update btcuser set apiLimitCount=? where userId=?", new Object[]{apiLimitCount, userId});
            if (rtn > 0) Write("操作成功", true, "");
            else Write("操作失败", false, "");
        } catch (Exception ex) {
            log.error("内部异常", ex);
            Write("内部错误", false, "");
        }
    }

    @Page(Viewer = XML)
    public void apiKey() {
        String accesskey = UUID.randomUUID().toString();//key
        String secretkey = UUID.randomUUID().toString();//密钥
        String secret = EncryDigestUtil.digest(secretkey);
        String result = "{";
        result += "\"accesskey\": \"" + accesskey + "\",";
        result += "\"secretkey\": \"" + secretkey + "\",";
        result += "\"secret\": \"" + secret + "\"";
        result += "}";
        Write(result, true, "");
    }

	/*@Page(Viewer = "")
    public void exportMobile(){
		try {
			String area =  URLDecoder.decode(param("area") , "utf-8");

			if(StringUtils.isEmpty(area)){
				return;
			}

			UserMobileTemp temp = (UserMobileTemp) Data.GetOne("select * from userMobileTemp order by id desc", null, UserMobileTemp.class);

			Query<User> q = dao.getQuery();
			List<User> users;
			if(temp == null){
				users = q.asList();
			}else{
				User user = dao.get(temp.getUserId());
				users = q.filter("registerTime >", user.getRegisterTime()).filter("userContact.mobileStatu =", 2).order("registerTime").asList();
			}

			List<UserMobileTemp> temps = new ArrayList<UserMobileTemp>();

			for (User user : users) {
				UserMobileTemp t = new UserMobileTemp();
				t.setUserId(user.getId());
				t.setUserName(user.getUserName());
				t.setMobileNumber(user.getUserContact().getSafeMobile());
				String mobile = user.getUserContact().getSafeMobile();
				if(StringUtils.isEmpty(mobile))
					continue;
				if(mobile.indexOf("+") >= 0)
					mobile = mobile.substring(mobile.indexOf(" ") + 1);
				if(mobile.length() < 7)
					continue;
				mobile = mobile.substring(0, 7);

				MobileArea obj = (MobileArea) Data.GetOne("select * from mobile_area where mobile_number=?", new Object[]{mobile}, MobileArea.class);

				if(obj != null){
					t.setMobileArea(obj.getMobile_area());
					t.setMobileType(obj.getMobile_type());
				}
				temps.add(t);
			}

			List<OneSql> sqls = new ArrayList<OneSql>();
			DataDaoSupport dao = new DataDaoSupport();
			for (UserMobileTemp userMobileTemp : temps) {
				sqls.add(dao.getTransInsertSql(userMobileTemp));
			}

			List<OneSql> sqls2 = new ArrayList<OneSql>();
			for (int i = 0; i < sqls.size(); i++) {
				OneSql one = sqls.get(i);
				sqls2.add(one);
				if(i % 100 == 0){
					Data.doTrans(sqls2);
					sqls2 = new ArrayList<OneSql>();
				}
				if(i == sqls.size() - 1)
					Data.doTrans(sqls2);
			}

			List<Bean> areas = Data.Query("select * from userMobileTemp where mobileArea like '%"+area+"%'", null, UserMobileTemp.class);

			String [] column = {"userName","mobileNumber","mobileArea","mobileType"};
			String [] tabHead = {"用户名称","手机号码","归属地","网络类型"};
			HSSFWorkbook workbook = ExcelManager.exportNormal(areas, column, tabHead);
			OutputStream out = response.getOutputStream();
	        response.setHeader("Content-disposition", "attachment;filename="+ URLEncoder.encode("excel_user_mobile_info.xls", "UTF-8"));
	        response.setContentType("application/msexcel;charset=UTF-8");
	        workbook.write(out);
	        out.flush();
	        out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.toString(), e);
		}
	}*/

	/*@Page(Viewer = "")
    public void exportUser(){
		try {
			if(!codeCorrect(XML)){
				return;
			}
			List<User> needUser = getUserList();

			String [] column = {"userName","realName","safePwd","email","money"};
			String [] tabHead = {"用户名称","真实姓名","认证手机","认证邮箱","用户资产（CNY）"};
			HSSFWorkbook workbook = ExcelManager.exportNormal(needUser, column, tabHead);
			OutputStream out = response.getOutputStream();
			response.setHeader("Content-disposition", "attachment;filename="+ URLEncoder.encode("excel_user_info.xls", "UTF-8"));
			response.setContentType("application/msexcel;charset=UTF-8");
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}*/

    @Page(Viewer = "/admins/user/seniorExport.jsp")
    public void seniorExport() {

    }

	/*@Page(Viewer = "")
    public void seniorExportUser(){
		try {
			if(!codeCorrect(XML)){
				return;
			}
			List<User> needUser = getSeniorUserList();

			String [] column = {"userName","realName","safePwd","email","registerTime","lastLoginTime"};
			String [] tabHead = {"用户名称","真实姓名","认证手机","认证邮箱","注册时间","最后登录时间"};
			HSSFWorkbook workbook = ExcelManager.exportNormal(needUser, column, tabHead);
			OutputStream out = response.getOutputStream();
			response.setHeader("Content-disposition", "attachment;filename="+ URLEncoder.encode("excel_user_info.xls", "UTF-8"));
			response.setContentType("application/msexcel;charset=UTF-8");
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}*/

    @Page(Viewer = "/admins/user/sendSms.jsp")
    public void sendSms() {
        setAttr("assets", decimalParam("assets"));
        setAttr("nt", intParam("nt"));
    }

    @Page(Viewer = XML)
    public void doSendSms() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            List<User> needUser = new ArrayList<User>();

            String userName = param("userName");
            if (StringUtils.isNotBlank(userName)) {
                User user = dao.getByField("userName", userName);
                if (user == null) {
                    WriteError("用户名不存在。");
                    return;
                }

                if (StringUtils.isNotBlank(user.getUserContact().getSafeMobile())) {
                    user.setSafePwd(user.getUserContact().getSafeMobile());
                    needUser.add(user);
                }
            } else {
                needUser = null;//getUserList();
            }

            int type = intParam("type");
            String title = param("title");
            String content = param("content");

            int count = 0;
            if (type == 1) {//发短信
                MobileDao mDao = new MobileDao();
                for (User user : needUser) {
                    if (StringUtils.isNotBlank(user.getSafePwd())) {
                        mDao.sendSms(user, ip(), title, "尊敬的" + user.getUserName() + "，" + content, user.getSafePwd());
                        count++;
                    }
                }
                WriteRight("发送成功，本次共发送短信给" + count + "个用户。");
            } else if (type == 2) {
                EmailDao eDao = new EmailDao();
                for (User user : needUser) {
                    if (StringUtils.isNotBlank(user.getEmail()) && user.getEmail().indexOf("@") > 0) {
                        eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, eDao.getSysEmailHtml(user, content, this), user.getEmail());
                        count++;
                    }
                }
                WriteRight("发送成功，本次共发送邮件给" + count + "个用户。");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
            WriteError("内部错误");
        }
    }

    @Page(Viewer = ".xml")
    public void freez() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            String userId = param("id");
            User user = dao.get(userId);
            boolean freez = true;
            if (user.isFreez()) {
                freez = false;
            }

            Datastore ds = dao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("freez", freez);

            UpdateResults<User> ur = dao.update(q, ops);
            if (!ur.getHadError()) {
                if (freez) {
                    SSOLoginManager.logout(userId);
                    /*start by xwz 20171113 冻结用户删除app登录的Cache*/
                    Object appLoginCache = Cache.GetObj(BaseMobileAction.appLoginCache+userId);
                    if(appLoginCache != null){
                        Cache.Delete(BaseMobileAction.appLoginCache+userId);
                    }
					/*end*/
                    Write("用户账户冻结成功。", true, "用户账户冻结成功。");
                } else {
                    Write("用户账户解冻成功。", true, "用户账户解冻成功。");
                }

                try {
                    //插入一条管理员日志信息
                    DailyType type = DailyType.userMemo;
                    new DailyRecordDao().insertOneRecord(type, "成功" + (freez ? "冻结" : "解冻") + "用户名为：" + user.getUserName() + "的账户。", String.valueOf(adminId()), ip(), now());
                } catch (Exception e) {
                    log.error("添加管理员日志失败", e);
                }
            } else {
                Write("操作失败", false, "操作失败。");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
            Write("未知错误导致操作失败！", false, "");
        }

    }

    @Page(Viewer = ".xml")
    public void lockRecommend() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            String userId = param("id");
            User user = dao.get(userId);
            if (null == user) {
                Write("操作失败", false, "操作失败。");
                return;
            }

            Datastore ds = dao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("lockRecommend", true);

            UpdateResults<User> ur = dao.update(q, ops);
            if (!ur.getHadError()) {
                WriteRight("操作成功");
            } else {
                WriteError("操作失败");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
            WriteError("未知错误导致操作失败！");
        }

    }

    @Page(Viewer = ".xml")
    public void unLockRecommend() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            String userId = param("id");
            User user = dao.get(userId);
            if (null == user) {
                Write("操作失败", false, "操作失败。");
                return;
            }

            Datastore ds = dao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("lockRecommend", false);

            UpdateResults<User> ur = dao.update(q, ops);
            if (!ur.getHadError()) {
                WriteRight("操作成功");
            } else {
                WriteError("操作失败");
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            WriteError("未知错误导致操作失败！");
        }

    }

    @Page(Viewer = ".xml")
    public void releaseLoginGoogle() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            String userId = param("id");
            User user = dao.get(userId);
            if (null == user) {
                Write("操作失败", false, "操作失败。");
                return;
            }

            Datastore ds = dao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("loginGoogleAuth", false);

            UpdateResults<User> ur = dao.update(q, ops);
            if (!ur.getHadError()) {
                WriteRight("操作成功");
            } else {
                WriteError("操作失败");
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            WriteError("未知错误导致操作失败！");
        }

    }

    @Page(Viewer = ".xml")
    public void changeReceiveDealSms() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            String userId = param("userId");
            int type = intParam("type");
            User user = dao.get(userId);
            if (null == user) {
                Write("操作失败", false, "操作失败。");
                return;
            }

            Datastore ds = dao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("receiveDealSms", type == 1 ? true : false);

            UpdateResults<User> ur = dao.update(q, ops);
            if (!ur.getHadError()) {
                WriteRight("操作成功");
            } else {
                WriteError("操作失败");
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            WriteError("未知错误导致操作失败！");
        }

    }


    @Page(Viewer = XML)
    public void loginCheck() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }

            String userId = param("userId");
            User user = dao.get(userId);
            if (user == null) {
                Write("用户不存在。", false, "");
                return;
            }

            String des = "";
            UpdateOperations<User> ops = dao.getUpdateOperations();
            if (user.isDiffAreaLoginNoCheck()) {
                des = "异地登录手机验证开启";
                ops.set("diffAreaLoginNoCheck", false);
            } else if (!user.isDiffAreaLoginNoCheck()) {
                des = "异地登录手机验证关闭";
                ops.set("diffAreaLoginNoCheck", true);
            }
            UpdateResults<User> res = dao.update(dao.getQuery().filter("_id =", userId), ops);

            if (!res.getHadError()) {
                Write("操作成功！", true, "操作成功！");

                try {
                    //插入一条管理员日志信息
                    DailyType type = DailyType.adminOperate;
                    new DailyRecordDao().insertOneRecord(type, "设置用户" + user.getUserName() + des + "。", String.valueOf(adminId()), ip(), now());
                } catch (Exception e) {
                    log.error("添加管理员日志失败", e);
                }
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
            Write("未知错误！", false, "");
        }
    }

    /**
     * 清空认证错误次数
     */
    @Page(Viewer = ".xml", des = "清空认证错误次数")
    public void clearFailAuthTimes() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            String userId = param("id");
            User user = dao.get(userId);
            if (null == user) {
                Write("操作失败", false, "操作失败。");
                return;
            }
            Datastore ds = dao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            UpdateResults<User> ur = dao.update(q, ops.set("failAuthTimes", 0));
            if (!ur.getHadError()) {
                WriteRight("操作成功");
            } else {
                WriteError("操作失败");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
            WriteError("未知错误导致操作失败！");
        }
    }

    /**
     * 清空认证次数缓存
     */
    @Page(Viewer = ".xml", des = "清空认证次数缓存")
    public void clearAuthTimes() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            String userId = param("id");
            User user = dao.get(userId);
            if (null == user) {
                Write("操作失败", false, "操作失败。");
                return;
            }
            AuthUtil.deleteOverTimsInADayLimit(userId);
            Object cacheAuthTimes = request.getAttribute("cacheAuthTimes");
            if (cacheAuthTimes == null) {
                WriteRight("操作成功");
            } else {
                WriteError("操作失败");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
            WriteError("未知错误导致操作失败！");
        }
    }

	/*start by xzhang  20170829 修改客户类型方法*/

    /**
     * 修改客户类型页面展示
     */
    @Page(Viewer = "/admins/user/modifyCustomeType.jsp")
    public void showCustomerType() {
        String userId = param("userId");
        User user = dao.get(userId);
        setAttr("map", Const.CUSTOMER_TYPE);
        setAttr("user", user);
    }

    @Page(Viewer = "/admins/user/modifyCustomeOperation.jsp")
    public void showCustomerOperation() {
        String userId = param("userId");
        User user = dao.get(userId);
        setAttr("operationMap", CUSTOMER_OPERATION);
        setAttr("user", user);
    }

    /**
     * 保存修改客户类型
     */
    @Page(Viewer = ".xml")
    public void modifyCustomerType() {
        try {
            String userId = param("userId");
            String customerType = param("customerType");

            User user = dao.get(userId);
            if (user == null) {
                Write("获取用户信息失败", true, "获取用户信息失败。");
            }
            String customerTypeOld = user.getCustomerType();
            if (customerType.equals(user.getCustomerType())) {
                Write("客户类型未变更，无需提交", true, "客户类型未变更，无需提交。");
            }
            Datastore ds = dao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

            ops.set("customerType", customerType);
            UpdateResults<User> ur = dao.update(q, ops);

            if (!ur.getHadError()) {
                Write("设置成功", true, "客户类型修改成功。");
                try {
                    //插入一条管理员日志信息
                    DailyType type = DailyType.userCustomerType;
                    new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "修改客户类型", user.getUserName(), CUSTOMER_TYPE.get(customerType), CUSTOMER_TYPE.get(customerTypeOld)), String.valueOf(adminId()), ip(), now());
                } catch (Exception e) {
                    log.error("添加管理员日志失败", e);
                }
            } else {
                Write("修改失败。", false, "修改失败。");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }
	/*end*/


    /**
     * 保存修改客户操作类型
     */
    @Page(Viewer = ".xml")
    public void modifyCustomerOperation() {
        try {
            String userId = param("userId");
            String customerOperation = param("customerOperation");

            User user = dao.get(userId);
            if (user == null) {
                Write("获取用户信息失败", true, "获取用户信息失败。");
            }
            String customerTypeOld = user.getCustomerOperation();
            if (customerOperation.equals(user.getCustomerOperation())) {
                Write("客户操作类型未变更，无需提交", true, "客户操作类型未变更，无需提交。");
            }
            Datastore ds = dao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

            ops.set("customerOperation", customerOperation);
            UpdateResults<User> ur = dao.update(q, ops);

            if (!ur.getHadError()) {
                Write("设置成功", true, "客户类型修改成功。");
                try {
                    //插入一条管理员日志信息
                    DailyType type = DailyType.userCustomerOperation;
                    new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "修改客户操作类型", user.getUserName(), CUSTOMER_OPERATION.get(customerOperation), CUSTOMER_OPERATION.get(customerTypeOld)), String.valueOf(adminId()), ip(), now());
                } catch (Exception e) {
                    log.error("添加管理员日志失败", e);
                }
            } else {
                Write("修改失败。", false, "修改失败。");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }
	/*end*/


    /**
     * 设置/修改UV,PV
     */
    @Page(Viewer = "/admins/user/recommen1.jsp")
    public void updateUpv() {
        int id = intParam("id");
        String sql = "select * from aliveusercount where id=?";
        AliveUserCountVo aliveUserCountVo = new AliveUserCountVo();
        aliveUserCountVo = (AliveUserCountVo) Data.GetOne("messi_ods", sql, new Object[]{id}, AliveUserCountVo.class);
        setAttr("user", aliveUserCountVo);
    }


    /**
     * 保存修改
     */
    @Page(Viewer = ".xml")
    public void saveUpv() {
        int id = intParam("id");
        int pvCount = intParam("pvCount");
        Long uvCount = longParam("uvCount");
        int accessingIp = intParam("accessingIp");
        Long registerCount = longParam("registerCount");
        String registrationConversionRate = "0.00";
        String sql = "update aliveusercount set pv=?,uv=?,accessingIp=?,registrationConversionRate=? where id=?";
        if (uvCount != 0L) {
            double f1 = new BigDecimal((float) registerCount / uvCount).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
            double f2 = (float) (f1 * 100);
            BigDecimal f = new BigDecimal(f2);
            double f3 = f.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            DecimalFormat df = new DecimalFormat("0.00");
            registrationConversionRate = df.format(f3);
        }

        int rtn = Data.Update("messi_ods", sql, new Object[]{pvCount, uvCount, accessingIp,registrationConversionRate , id}); if (rtn != -1) {
            Write("修改成功", true, "修改成功。");
        } else {

            Write("修改失败", true, "修改失败。");
        }
    }


    /*
    /**
     * 修改PV ,UV页面展示
     */
    @Page(Viewer = "/admins/user/recommen1.jsp")
    public void showCustomerType1() {
        int id = intParam("id");
        String sql = "select id as id,pv as pvCount,uv as uvCount,ip as ipCount,accessingIp as accessingIp,registerCount as registerCount,allRegisterCount as allRegisterCount,loginCount as loginCount,loginRate as loginRate,registrationConversionRate as registrationConversionRate,countDate as countDate from aliveusercount where id=?";
        AliveUserCountVo aliveUserCountVo = new AliveUserCountVo();
        aliveUserCountVo = (AliveUserCountVo) Data.GetOne("messi_ods", sql, new Object[]{id}, AliveUserCountVo.class);
        setAttr("user", aliveUserCountVo);
    }


}
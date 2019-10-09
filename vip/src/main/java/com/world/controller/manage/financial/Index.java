package com.world.controller.manage.financial;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.PayUserApiService;
import com.redis.RedisUtil;
import com.world.cache.Cache;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.financialproift.FinDouProfitLogDao;
import com.world.model.dao.financialproift.FinUserInsureInvestDao;
import com.world.model.dao.financialproift.FinUserReturnOrderInfoDao;
import com.world.model.dao.financialproift.FinUsersontransferDao;
import com.world.model.dao.financialproift.FinancialBonusDao;
import com.world.model.dao.financialproift.UserFinancialInfoDao;
import com.world.model.dao.financialproift.UserInviteRelaDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.financialproift.FinDouProfitLog;
import com.world.model.entity.financialproift.FinProductInvest;
import com.world.model.entity.financialproift.FinUserInsureInvest;
import com.world.model.entity.financialproift.FinUserReturnOrderInfo;
import com.world.model.entity.financialproift.FinUsersontransfer;
import com.world.model.entity.financialproift.FinancialBonus;
import com.world.model.entity.financialproift.UserFinancialInfo;
import com.world.model.entity.financialproift.UserInviteRela;
import com.world.model.entity.user.User;
import com.world.model.enums.BonusEnum;
import com.world.model.enums.StatusEnum;
import com.world.model.financialproift.userfininfo.thread.ResetProfitThread;
import com.world.util.financialproift.FinancialProiftUtils;
import com.world.util.request.HttpUtil;
import com.world.util.sign.RSACoder;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.sso.rsa.RsaLoginUtil;
import com.world.web.sso.rsa.RsaUser;

public class Index extends UserAction {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    FinancialBonusDao financialBonusDao = new FinancialBonusDao();
    UserInviteRelaDao userInviteRelaDao = new UserInviteRelaDao();
    FinDouProfitLogDao finDouProfitLogDao = new FinDouProfitLogDao();
    private UserDao userDao = new UserDao();

    @Page(Viewer = JSON)
    public void userSwitchSonAccount() {
        initLoginUser();
        try {
            /*调用用户信息*/
            String userId = loginUser.get_Id();
//            String userName = loginUser.getUserName();
            /**
             * 支持子账号切换校验
             */
            String sonUserId = param("sonUserId");
            String sonUserName = param("sonUserName");
            /*校验标志*/
            boolean userSonRelationFlag = userSonRelationCheck(userId, sonUserName, sonUserId);
            if (userSonRelationFlag) {
                /*是子账号，切换数据处理逻辑*/
//                userId = sonUserId;
//                userName = sonUserName;
                /**
                 * 检查设置资金账户初始化
                 */
                setUserSonCapitalAccount(sonUserId, sonUserName);
            }
            json(L("切换成功"), true, null, true);
        } catch (Exception e) {
            json(L("切换失败"), false, null, true);
            log.info("理财报警ERROR:保险账户切换失败", e);
        }
    }
    
    @SuppressWarnings("unchecked")
//    @Page(Viewer = JSON)
    public void userSonTransferUsdt() {
        initLoginUser();
        try {
            /*调用用户信息*/
            String userId = loginUser.get_Id();
            String userName = loginUser.getUserName();
            /**
             * 支持子账号切换校验
             */
            String sonUserId = param("sonUserId");
            String sonUserName = param("sonUserName");
            /*校验标志*/
            boolean userSonRelationFlag = userSonRelationCheck(userId, sonUserName, sonUserId);
            if (userSonRelationFlag) {
                /*是子账号，切换数据处理逻辑*/
                userId = sonUserId;
                userName = sonUserName;
            } else {
                log.info("理财报警WARN:非法访问 = " + userId);
                json(L("非法访问"), false, null, true);
                return;
            }

            /*释放金额*/
            String avaTransferAmount = param("avaTransferAmount");
            BigDecimal bdAvaTransferAmount = BigDecimal.ZERO;
            try {
                bdAvaTransferAmount = new BigDecimal(avaTransferAmount);
            } catch (Exception e) {
                json(L("非法输入，请确认后输入"), false, null, true);
                log.info("理财报警ERROR:子账号USDT提取失败, userId = " + userId + ", avaTransferAmount = " + avaTransferAmount);
                return;
            }

            /*查询需要的相关字段*/
            String sql = "";
            /*先检查资金是否有足够的资金*/
            sql = "select balance from pay_user_financial where userid = " + userId + " and fundstype = 10";
            log.info("userAvaTransferAmount sql = " + sql);
            List<BigDecimal> listPayUserFinancial = (List<BigDecimal>) Data.GetOne("vip_main", sql, null);
            BigDecimal sonUserUsdtAmount = BigDecimal.ZERO;
            if (null == listPayUserFinancial || listPayUserFinancial.size() < 1) {
                json(L("理财账户初始化失败，请联系客服"), false, null, true);
                log.info("理财报警ERROR:子账号USDT理财账户初始化失败，请联系客服" + userId);
                return;
            } else {
                if (null != listPayUserFinancial.get(0)) {
                    sonUserUsdtAmount = listPayUserFinancial.get(0);
                } else {
                    json(L("待划转资金不足，请确认后划转"), false, null, true);
                    log.info("理财报警WARN:子账号USDT划转资金不足, userId = " + userId + ", sonUserUsdtAmount = " + sonUserUsdtAmount + ", bdAvaTransferAmount = " + bdAvaTransferAmount);
                    return;
                }
                log.info("userProfitAmount = " + sonUserUsdtAmount);
                if (sonUserUsdtAmount.compareTo(bdAvaTransferAmount) < 0 || bdAvaTransferAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    json(L("待划转资金不足，请确认后划转"), false, null, true);
                    log.info("理财报警WARN:子账号USDT划转资金不足, userId = " + userId + ", sonUserUsdtAmount = " + sonUserUsdtAmount + ", bdAvaTransferAmount = " + bdAvaTransferAmount);
                    return;
                }
            }

            /**
             * 开启事务处理
             */
            List<OneSql> sqls = new ArrayList<>();
            TransactionObject txObj = new TransactionObject();
            long currentTime = System.currentTimeMillis();
            /**
             * 将子账号资金转入主账号
             * 子账号划出，记录流水
             * 主账号接收，记录流水
             */
            /*子账号先划出*/
            /*子账号 更新pay_user_financial	理财资金表 balance*/
            sql = "update pay_user_financial set balance = balance - " + bdAvaTransferAmount + " "
                    + "where userid = " + userId + " and fundstype = 10 and balance - " + bdAvaTransferAmount + " >= 0";
            log.info("userAvaTransferAmount sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));
            /*子账号 插入bill_financial 流水类型 5304 子账号划出 */
            sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
                    + "remark, vdsUsdtPrice, investProPeriod, matrixLevel, businessId) "
                    + "select " + userId + ", '" + userName + "', 5304, " + bdAvaTransferAmount + ", "
                    + "" + currentTime + ", (balance + profit + insureInvestFreezeAmount), "
                    + "10, '子账号划出', '" + loginUser.getUserName() + "', 0, "
                    + "0, 0, " + loginUser.get_Id() + " "
                    + "from pay_user_financial where userid = " + userId + " and fundstype = 10 for update";
            log.info("userAvaTransferAmount sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));

            /*主账号接收*/
            /*主账号 更新pay_user_financial	理财资金表 balance*/
            sql = "update pay_user_financial set balance = balance + " + bdAvaTransferAmount + " "
                    + "where userid = " + loginUser.get_Id() + " and fundstype = 10 ";
            log.info("userProductInfoPay pay_user_financial sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));
            /*主账号 插入bill_financial 流水类型 5303 子账号划入 */
            sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
                    + "remark, vdsUsdtPrice, investProPeriod, matrixLevel, businessId) "
                    + "select " + loginUser.get_Id() + ", '" + loginUser.getUserName() + "', 5303, " + bdAvaTransferAmount + ", "
                    + "" + currentTime + ", (balance + profit + insureInvestFreezeAmount), "
                    + "10, '子账号划入', '" + userName + "', 0, "
                    + "0, 0, " + userId + " "
                    + "from pay_user_financial where userid = " + loginUser.get_Id() + " and fundstype = 10 for update";
            log.info("userAvaTransferAmount sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));

            /*保存到划转表*/
            sql = "insert into fin_usersontransfer (sonUserId, sonUserName, fundsType, avaTransferAmount, douProfitAmount, "
                    + "transferType, transferName, parentUserId, parentUserName, createTime) "
                    + "values (" + userId + ", '" + userName + "', '10', " + avaTransferAmount + ", 0, "
                    + "2, '静态收益', " + loginUser.get_Id() + ", '" + loginUser.getUserName() + "', now() )";
            log.info("userAvaTransferAmount sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_financial"));

            txObj.excuteUpdateList(sqls);
            if (txObj.commit()) {

            } else {
                log.info("理财报警ERROR:子账号USDT提取失败 = " + userId);
                json(L("提取失败"), false, null, true);
                return;
            }
            /*保存成功*/
            log.info("理财报警INFO:子账号USDT提取成功 = " + userId);
            json(L("提取成功"), true, null, true);
        } catch (Exception e) {
            json(L("提取失败"), false, null, true);
            log.info("理财报警ERROR:子账号提取USDT失败", e);
        }
    }


    @SuppressWarnings("unchecked")
//    @Page(Viewer = JSON)
    public void userSetRank() {
        initLoginUser();
        try {
            /*调用用户信息*/
            String userId = loginUser.get_Id();
            String userName = loginUser.getUserName();
            /*保险ID*/
            String insureId = param("insureId");
            /*邀请码*/
            String pInvitationCode = param("pInvitationCode");
            /*自动生成数量*/
            String setRankNum = param("setRankNum");
            int intSetRankNum = 0;
            /**
             * 防止有并发设置在进行
             */
            String setRankFlag = RedisUtil.get("fin_userSetRank_" + userId);
            if (StringUtils.isEmpty(setRankFlag)) {
                RedisUtil.set("fin_userSetRank_" + userId, "true", 120);
            } else {
                log.info("系统繁忙，请稍后再试");
                json(L("系统繁忙，请稍后再试"), false, null, true);
                return;
            }
            
            /*获取vds_usdt实时价格*/
            String vdsUsdtPrice = Cache.Get("vds_usdt_l_price");
            log.info("vdsUsdtPrice = " + vdsUsdtPrice);
            if (StringUtils.isEmpty(vdsUsdtPrice)) {
                json(L("系统繁忙，请稍后再试"), false, null, true);
                log.info("理财报警ERROR:获取VDSUSDT成交价格异常");
                return;
            }
            BigDecimal bdVdsUsdtPrice = BigDecimal.ZERO;
            try {
                bdVdsUsdtPrice = new BigDecimal(vdsUsdtPrice);
            } catch (Exception e) {
                bdVdsUsdtPrice = BigDecimal.ZERO;
            }
            if (bdVdsUsdtPrice.compareTo(BigDecimal.ZERO) <= 0) {
                json(L("系统繁忙，请稍后再试"), false, null, true);
                log.info("理财报警ERROR:获取VDSUSDT成交价格异常");
                return;
            }
            /**
             * 参数校验,先分别设置校验不通过时的返回值
             */
            boolean crFlag = false;
//            String insureIdCR = "";
            String pInvitationCodeCR = "";
            String setRankNumCR = "";
            /*查询SQL*/
            String sql = "";
            /*拼装返回值*/
            Map<String, Object> result = new HashMap<>();
            if (StringUtils.isEmpty(pInvitationCode)) {
                pInvitationCodeCR = "请输入邀请码";
                crFlag = true;
            } else if (pInvitationCode.length() != 8) {
                pInvitationCodeCR = "邀请码不正确，请核对后再输入";
                crFlag = true;
            }
            try {
                intSetRankNum = Integer.parseInt(setRankNum);
            } catch (Exception e) {
                intSetRankNum = 0;
                setRankNumCR = "直推数量错误";
                crFlag = true;
            }
            if (intSetRankNum > 10) {
                setRankNumCR = "1次最多直推10个子账号";
                crFlag = true;
            }
            /**
             * 基础信息校验检查返回
             */
            if (crFlag) {
                if (!StringUtils.isEmpty(pInvitationCodeCR)) {
                    result.put("pInvitationCodeCR", L(pInvitationCodeCR));
                }
                if (!StringUtils.isEmpty(setRankNumCR)) {
                    result.put("setRankNumCR", L(setRankNumCR));
                }
                log.info("result = " + result);
                json("ok", false, JSONObject.toJSONString(result), true);
                return;
            }

            /**
             * 邀请码、VID有效性校验
             */
            /*邀请人用户名*/
            String invitationUserName = "";
            sql = "select userName from fin_userfinancialinfo where invitationCode = '" + pInvitationCode + "'";
            log.info("userProductInfoSave sql = " + sql);
            List<String> listPInvitationCode = (List<String>) Data.GetOne("vip_financial", sql, null);
            if (null != listPInvitationCode) {
                /*个数检查*/
                invitationUserName = listPInvitationCode.get(0);
                if (StringUtils.isEmpty(invitationUserName)) {
                    pInvitationCodeCR = "邀请码不正确，请核对后再输入";
                    crFlag = true;
                }
            } else {
                pInvitationCodeCR = "邀请码不正确，请核对后再输入";
                crFlag = true;
            }

            /*校验保险信息 必须是部分投资中,或未投资 investState */
            sql = "select id, userId from fin_userinsureinvest where id = " + insureId + " and userId = " + userId + " "
                    + "and investState in (0, 1) and triggerFlag != 0 and insureInvestSurplusNum >= " + intSetRankNum;
            log.info("sql = " + sql);
            FinUserInsureInvest finUserInsureInvest = (FinUserInsureInvest) Data.GetOne("vip_financial", sql, null, FinUserInsureInvest.class);
            if (null == finUserInsureInvest) {
                setRankNumCR = "投保信息状态错误";
                log.info("投保信息状态错误");
                crFlag = true;
            }

            /*VID校验，如果有不通过项执行返回*/
            if (crFlag) {
                if (!StringUtils.isEmpty(setRankNumCR)) {
                    result.put("setRankNumCR", L(setRankNumCR));
                }
                if (!StringUtils.isEmpty(pInvitationCodeCR)) {
                    result.put("pInvitationCodeCR", L(pInvitationCodeCR));
                }
                log.info("result = " + result);
                json("ok", false, JSONObject.toJSONString(result), true);
                return;
            }

            /**
             * 基础校验通过，开始循环生成
             */
            sql = "select count(*) cnt from fin_userfinancialinfo where parentUserId = " + userId;
            log.info("sql = " + sql);
            int curSonUserCount = 0;
            List<Long> listSonUsers = (List<Long>) Data.GetOne("vip_financial", sql, null);
            if (null != listSonUsers) {
                /*个数检查*/
                curSonUserCount = listSonUsers.get(0).intValue();
            }
            log.info("curSonUserCount = " + curSonUserCount);
            /*子账号生成*/
            String sonUserId = "";
            String sonUserName = "";

            /*保存操作标志，支付操作标志*/
            boolean saveState = false;
            boolean payState = false;
            /*后续操作标志*/
            boolean dealState = false;
            for (int i = 1; i <= intSetRankNum; i++) {
                /*重置标志*/
                saveState = false;
                payState = false;
                dealState = false;
                /**
                 * 子账号用户名
                 */
                User user = new User(userDao.getDatastore());
                sonUserId = user.incId();
                sonUserName = userName + "-" + (i + curSonUserCount);
                log.info("sonUserId = " + sonUserId + ", sonUserName = " + sonUserName);
                /*保存*/
                saveState = userProductInfoSaveSetRank(sonUserId, sonUserName, pInvitationCode, userId, userName);
                if (saveState) {
                    try {
                        payState = userProductInfoPaySetRank(insureId, sonUserId, sonUserName, 6, bdVdsUsdtPrice, userId, userName);
                    } catch (Exception e) {
                        log.info("理财报警ERROR:险投保失败 = " + userId, e);
                    }
                }
                if (!payState) {
                    /*保存成功，支付失败，回滚保存信息*/
                    sql = "delete from fin_userfinancialinfo where userId = " + sonUserId + " and authPayFlag = 1 ";
                    log.info("sql = " + sql);
                    Data.Delete("vip_financial", sql, null);
                }
                if (saveState && payState) {
                    dealState = true;
                    try {
                        /*子账号资金初始化*/
                        setUserSonCapitalAccount(sonUserId, sonUserName);
                    } catch (Exception e) {
                        log.info("理财报警ERROR:保险投保子账号资金初始化失败 = " + sonUserId, e);
                    }
                }
                if (!dealState) {
                    log.info("排位保存失败");
                    json(L("排位保存失败"), false, null, true);
                    return;
                }
            }
            /*更新投资状态*/
            sql = "update fin_userinsureinvest set investState = case when insureInvestSurplusNum = 0 then 2 "
                    + "when insureInvestNum = insureInvestSurplusNum then 0 else 1 end where id = " + insureId + "";
            log.info("sql = " + sql);
            Data.Update("vip_financial", sql, null);

            /*解锁*/
            RedisUtil.delete("fin_userSetRank_" + userId);
            /*保存成功*/
            log.info("理财报警INFO:排位保存成功 = " + userId + ", 直推人数  = " + intSetRankNum);
            json(L("排位保存成功"), true, null, true);
        } catch (Exception e) {
            json(L("排位保存失败"), false, null, true);
            log.info("理财报警ERROR:保险投保失败", e);
        }
    }


    @SuppressWarnings("unchecked")
    public boolean userProductInfoSaveSetRank(String sonUserId, String sonUserName, String pInvitationCode, String userId, String userName) {
        boolean saveState = false;
        try {
            /*邀请人用户名*/
            String invitationUserName = "";
            String sql = "";
            sql = "select userName from fin_userfinancialinfo where invitationCode = '" + pInvitationCode + "'";
            log.info("userProductInfoSave sql = " + sql);
            List<String> listPInvitationCode = (List<String>) Data.GetOne("vip_financial", sql, null);
            if (null != listPInvitationCode) {
                /*个数检查*/
                invitationUserName = listPInvitationCode.get(0);
                if (StringUtils.isEmpty(invitationUserName)) {
                    return saveState;
                }
            } else {
                return saveState;
            }
            /**
             * 先调用接口，如果接口无法正常返回，则不再保存
             */
            /*调用接口进行数据对接保存*/
            boolean saveFlag = true;
            try {
                /*调用接口进行数据对接保存 doPostData*/
                Map<String, Object> objectMap = new HashMap<String, Object>();
                objectMap.put("userId", sonUserId);
                objectMap.put("username", sonUserName);
                objectMap.put("address", sonUserName);
                objectMap.put("recUser", invitationUserName);
                log.info("objectMap = " + objectMap);
                String urlFinancial = ApiConfig.getValue("urlfinancial.url");
                urlFinancial += "/vdsapollo/op/register";
                log.info("urlFinancial = " + urlFinancial);
                String resultInterface = "";
                /*接口返回码和返回消息*/
                String resultInterfaceCode = "";
                String resultInterfaceMsg = "";
                resultInterface = doPostData(urlFinancial, objectMap);
                JSONObject jsonResultInterface = com.alibaba.fastjson.JSONObject.parseObject(resultInterface);
                log.info("jsonResultInterface = " + jsonResultInterface);
                if (null != jsonResultInterface) {
                    if (null != jsonResultInterface.getString("code")) {
                        resultInterfaceCode = jsonResultInterface.getString("code");
                    }
                    if (null != jsonResultInterface.getString("message")) {
                        resultInterfaceMsg = jsonResultInterface.getString("message");
                    }
                    log.info("resultInterfaceCode = " + resultInterfaceCode);
                    if (!"200".equals(resultInterfaceCode)) {
                        if ("400".equals(resultInterfaceCode)) {
                            if (!"用户已存在".equals(resultInterfaceMsg)) {
                                saveFlag = false;
                                log.info("理财报警INTERFACE:userProductInfoSave = " + jsonResultInterface);
                            }
                        } else {
                            saveFlag = false;
                            log.info("理财报警INTERFACE:userProductInfoSave = " + jsonResultInterface);
                        }
                    }
                } else {
                    saveFlag = false;
                    log.info("理财报警INTERFACE:userProductInfoSave = " + jsonResultInterface);
                }
            } catch (Exception e) {
                saveFlag = false;
                log.info("理财报警INTERFACE:userProductInfoSave", e);
            }

            if (saveFlag) {
                /*新增保存*/
                sql = "insert into fin_userfinancialinfo (userId, userName, userVID, pInvitationCode, invitationUserName, authPayFlag, "
                        + "parentUserId, parentUserName) values "
                        + "('" + sonUserId + "', '" + sonUserName + "', '', '" + pInvitationCode + "', '" + invitationUserName + "' , 1, "
                        + "'" + userId + "', '" + userName + "')";
                log.info("userProductInfoSave sql = " + sql);
                int intInsert = Data.Insert("vip_financial", sql, null);
                if (intInsert < 1) {
                    return saveState;
                } else {
                    saveState = true;
                }
            } else {
                return saveState;
            }
        } catch (Exception e) {
            log.info("理财报警ERROR:排位保存失败 = " + sonUserId, e);
        }
        return saveState;
    }

    @SuppressWarnings("unchecked")
    public boolean userProductInfoPaySetRank(String id, String sonUserId, String sonUserName, int intTargetMatrixLevel,
                                             BigDecimal bdVdsUsdtPrice, String userId, String userName) {
        boolean payState = false;
        try {
            String sql = "";
            /*参数校验,先分别设置校验不通过时的返回值*/
            /*获取产品状态标志，先从Redis获取,存放时间10秒*/
            String proState = RedisUtil.get("financial_proState");
            String proAmount = RedisUtil.get("financial_proAmount");
            BigDecimal bdProAmount = BigDecimal.ZERO;
            log.info("proState = " + proState + ", proAmount = " + proAmount + ", bdProAmount = " + bdProAmount);

            /*投资金额*/
            bdProAmount = new BigDecimal(proAmount);

            log.info("proState = " + proState + ", proAmount = " + proAmount + ", bdProAmount = " + bdProAmount);
            if (!"1".equals(proState)) {
                /*该产品理财投资已结束！*/
                log.info("理财报警WARN:保险投资保存支付设置排位该产品理财投资已结束");
                return payState;
            }

            /*获取矩阵级别对应的投资金额*/
            BigDecimal targetLevelAmount = FinancialProiftUtils.giveMatrixLevelProfitAmount(intTargetMatrixLevel);
            BigDecimal difLevelAmount = targetLevelAmount;
            /*vip标志判断*/
            int vipFlag = 0;
            if (difLevelAmount.compareTo(bdProAmount) >= 0) {
                vipFlag = 1;
            }
            if (intTargetMatrixLevel != 6) {
                log.info("您投资的收益产品暂未开启，请选择投资188产品！" + sonUserId + ", intTargetMatrixLevel = " + intTargetMatrixLevel);
                return payState;
            }
            /**
             * 投资188 Vollar可以获得100分的权重；0.53
             * 投资 88 Vollar可以获得45分的权重；0.511
             * 投资 38 Vollar可以获得20分的权重；0.52
             * 投资 18 Vollar可以获得10分的权重；0.55
             * 投资  8 Vollar可以获得4.5分的权重；0.625
             * 投资  2 Vollar可以获得1分的权重；0.5
             */
            BigDecimal targetVipWeight = FinancialProiftUtils.giveVIPWeight(intTargetMatrixLevel);
            /*先检查资金是否有足够的资金*/
            sql = "select insureInvestFreezeAmount from pay_user_financial where userid = " + userId + " and fundstype = 51";
            log.info("userProductInfoPay pay_user_financial balance sql = " + sql);
            List<BigDecimal> listPayUserFinancial = (List<BigDecimal>) Data.GetOne("vip_main", sql, null);
            BigDecimal userBalance = BigDecimal.ZERO;
            if (null == listPayUserFinancial || listPayUserFinancial.size() < 1) {
                log.info("理财报警ERROR:保险投资保存支付设置排位理财账户初始化失败，请联系客服" + sonUserId);
                return payState;
            } else {
                if (null != listPayUserFinancial.get(0)) {
                    userBalance = listPayUserFinancial.get(0);
                }
                if (userBalance.compareTo(difLevelAmount) < 0) {
                    return payState;
                }
            }
            /**
             * 产品状态为开启，而且满足支付条件，开始进行支付处理。开启事务。
             * 1、插入表productinvest		产品投资表
             * 2、更新product				理财产品表		proTotalUser，proTotalAmount
             * 3、更新userfinancialinfo	用户理财信息表	authPayFlag	0默认值，1已认证，2已支付.invitationCode
             * 4、更新pay_user_financial	理财资金表		balance
             * 5、插入bill_financial 		理财流水表		理财投资188 流水类型 5301 支出
             */
            /*投资金额和预计收益金额预先处理*/
            BigDecimal xxxUsdt = bdVdsUsdtPrice.multiply(difLevelAmount).setScale(4, BigDecimal.ROUND_DOWN);
            BigDecimal yyyUsdt = bdVdsUsdtPrice.multiply(difLevelAmount.multiply(BigDecimal.valueOf(1.5))).setScale(4, BigDecimal.ROUND_DOWN);

            List<OneSql> sqls = new ArrayList<>();
            TransactionObject txObj = new TransactionObject();
            long currentTime = System.currentTimeMillis();
            /*1、插入表productinvest 产品投资表*/
            sql = "insert into fin_productinvest (userId, userName, fundsType, proId, investAmount, investProPeriod, "
                    + "vdsUsdtPrice, investUsdtAmount, expectProfitUsdt, investTime, matrixLevel, vipWeight) values "
                    + "(" + sonUserId + ", '" + sonUserName + "', 51, 'BWFP1', " + difLevelAmount + ", 1, " + bdVdsUsdtPrice + ", "
                    + "" + xxxUsdt + ", " + yyyUsdt + ", now(), " + intTargetMatrixLevel + ", " + targetVipWeight + " )";
            log.info("userProductInfoPay productinvest sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_financial"));

            /*2、更新product 理财产品表 proTotalUser，proTotalAmount*/
            sql = "update fin_product set proTotalUser = proTotalUser + 1, proTotalAmount = proTotalAmount + " + difLevelAmount + "";
            log.info("userProductInfoPay product sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_financial"));

            /*3、更新userfinancialinfo 用户理财信息表 authPayFlag 0默认值，1已认证，2已支付。invitationCode*/
            /**
             * 新增字段：investAvergPrice, investAmount, expectProfitUsdt, outSurplusVDS
             * bdInvestAvergPrice 新均价
             * curExpectProfitUsdt 原始预期1.5倍收益
             */
            /*根据UUID生成邀请码*/
            String invitationCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            /*此处如果报错，用数据库唯一索引拦截，程序不在判断*/
            sql = "update fin_userfinancialinfo set authPayFlag = 2, modifyTime = now(), profitTime = now(), "
                    + "invitationCode = '" + invitationCode + "', "
                    + "matrixLevel = " + intTargetMatrixLevel + ", "
                    + "vipWeight = " + targetVipWeight + ", "
                    + "investAvergPrice = " + bdVdsUsdtPrice + ", "
                    + "investAmount = " + targetLevelAmount + ", "
                    + "expectProfitUsdt = " + yyyUsdt + ", "
                    + "outSurplusVDS = " + targetLevelAmount + ", "
                    + "vipFlag = " + vipFlag + " where userId = " + sonUserId + " and authPayFlag = 1 ";
            log.info("userProductInfoPay userfinancialinfo sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_financial"));

            sql = "update fin_userinsureinvest set "
                    + "insureInvestSurplusAmount = insureInvestSurplusAmount - investLevelAmount, "
                    + "insureInvestSurplusNum = insureInvestSurplusNum - 1 "
                    + "where id = " + id + " and triggerFlag = 1 "
                    + "and insureInvestSurplusAmount - investLevelAmount >=0 "
                    + "and insureInvestSurplusNum - 1 >= 0 ";
            log.info("InsureInvestTriggerThread...sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_financial"));

            /*4、更新pay_user_financial	理财资金表 balance*/
            sql = "update pay_user_financial set insureInvestFreezeAmount = insureInvestFreezeAmount - " + difLevelAmount + " "
                    + "where userid = " + userId + " and fundstype = 51 and insureInvestFreezeAmount - " + difLevelAmount + " >= 0";
            log.info("userProductInfoPay pay_user_financial sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));
            /*5、插入bill_financial 理财担保投资1份，子账号排位支付投资188时记账 流水类型 5302 支出*/
            sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
                    + "remark, vdsUsdtPrice, investProPeriod, matrixLevel) "
                    + "select " + userId + ", '" + userName + "', 5302, " + difLevelAmount + ", "
                    + "" + currentTime + ", (balance + profit + insureInvestFreezeAmount), "
                    + "51, '理财保险投资', '" + sonUserId + "', " + bdVdsUsdtPrice + ", '1', " + intTargetMatrixLevel + " "
                    + "from pay_user_financial where userid = " + userId + " and fundstype = 51 for update";
            log.info("userProductInfoPay bill_financial sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));

            txObj.excuteUpdateList(sqls);
            if (txObj.commit()) {
                /*支付成功*/
                log.info("用户【 " + sonUserId + "】支付成功");
            } else {
                return payState;
            }

            /*调用激活接口*/
            /*调用接口进行数据对接激活*/
            boolean rollBackFlag = false;

            try {
                /*调用接口进行数据对接保存 doPostData*/
                Map<String, Object> objectMap = new HashMap<String, Object>();
                objectMap.put("userId", sonUserId);
                objectMap.put("oldLevel", 0);
                objectMap.put("level", intTargetMatrixLevel);
                objectMap.put("levelNum", difLevelAmount);
                objectMap.put("vdsPrice", bdVdsUsdtPrice);
                objectMap.put("expectProfit", yyyUsdt);
                log.info("objectMap = " + objectMap);
                String urlFinancial = ApiConfig.getValue("urlfinancial.url");
                urlFinancial += "/vdsapollo/op/activate";
                log.info("urlFinancial = " + urlFinancial);
                String resultInterface = "";
                /*接口返回码和返回消息*/
                String resultInterfaceCode = "";
                String resultInterfaceMsg = "";
                resultInterface = doPostData(urlFinancial, objectMap);
                JSONObject jsonResultInterface = com.alibaba.fastjson.JSONObject.parseObject(resultInterface);
                log.info("jsonResultInterface = " + jsonResultInterface);
                if (null != jsonResultInterface) {
                    if (null != jsonResultInterface.getString("code")) {
                        resultInterfaceCode = jsonResultInterface.getString("code");
                    }
                    if (null != jsonResultInterface.getString("message")) {
                        resultInterfaceMsg = jsonResultInterface.getString("message");
                    }
                    log.info("resultInterfaceCode = " + resultInterfaceCode);
                    if (!"200".equals(resultInterfaceCode)) {
                        if ("400".equals(resultInterfaceCode)) {
                            if (!"用户已激活".equals(resultInterfaceMsg)) {
                                rollBackFlag = true;
                                log.info("理财报警INTERFACE:保险投资保存支付设置排位userProductInfoPay = " + jsonResultInterface);
                            }
                        } else {
                            rollBackFlag = true;
                            log.info("理财报警INTERFACE:保险投资保存支付设置排位userProductInfoPay = " + jsonResultInterface);
                        }
                    }
                } else {
                    rollBackFlag = true;
                    log.info("理财报警INTERFACE:保险投资保存支付设置排位userProductInfoPay = " + jsonResultInterface);
                }
            } catch (Exception e) {
                rollBackFlag = true;
                log.info("理财报警INTERFACE:保险投资保存支付设置排位userProductInfoPay", e);
            }


            /**
             * 回滚操作，此处暂时先不回滚
             * 流水此处可删除，但是如果是复投就不能直接这么做
             */
            if (rollBackFlag) {
                sqls = new ArrayList<>();
                txObj = new TransactionObject();
                /*1、回滚，插入表productinvest 产品投资表*/
                sql = "delete from fin_productinvest where userId = " + sonUserId + " and investProPeriod = 1 "
                        + "and matrixLevel = " + intTargetMatrixLevel + "";
                log.info("userProductInfoPay productinvest sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_financial"));

                /*2、回滚，更新product 理财产品表 proTotalUser，proTotalAmount*/
                sql = "update fin_product set proTotalUser = proTotalUser - 1, proTotalAmount = proTotalAmount - " + difLevelAmount + "";
                log.info("userProductInfoPay product sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                /*3、更新userfinancialinfo 用户理财信息表 authPayFlag 0默认值，1已认证，2已支付。invitationCode*/
                /**
                 * 新增字段：investAvergPrice, investAmount, expectProfitUsdt, outSurplusVDS
                 * bdInvestAvergPrice 新均价
                 * curExpectProfitUsdt 原始预期1.5倍收益
                 */
                sql = "update fin_userfinancialinfo set authPayFlag = 1, invitationCode = '', vipFlag = 0 where userId = " + sonUserId;
                log.info("userProductInfoPay userfinancialinfo sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_financial"));

                sql = "update fin_userinsureinvest set "
                        + "insureInvestSurplusAmount = insureInvestSurplusAmount + investLevelAmount, "
                        + "insureInvestSurplusNum = insureInvestSurplusNum + 1 "
                        + "where id = " + id + " and triggerFlag = 1 "
                        + "and insureInvestSurplusAmount + investLevelAmount <= insureInvestAmount "
                        + "and insureInvestSurplusNum + 1 <= insureInvestNum ";
                log.info("InsureInvestTriggerThread...sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_financial"));

                /*4、更新pay_user_financial	理财资金表 balance*/
                sql = "update pay_user_financial set insureInvestFreezeAmount = insureInvestFreezeAmount + " + difLevelAmount + " "
                        + "where userid = " + userId + " and fundstype = 51 ";
                log.info("userProductInfoPay pay_user_financial sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_main"));

                /*5、插入bill_financial 理财流水表 理财投资188 流水类型 5301 支出*/
                sql = "delete from bill_financial where userid = " + sonUserId + " and fundstype = 51 and type = 5302 "
                        + "and matrixLevel = " + intTargetMatrixLevel + "";
                log.info("userProductInfoPay bill_financial sql = " + sql);
                sqls.add(new OneSql(sql, -1, null, "vip_main"));

                /*回滚处理*/
                txObj.excuteUpdateList(sqls);
                if (txObj.commit()) {
                    /*支付成功*/
                    log.info("理财报警WARN:保险投资保存支付设置排位用户【 " + sonUserId + "】回滚处理成功");
                } else {
                    log.info("理财报警ERROR:保险投资保存支付设置排位支付回滚失败,userId = " + sonUserId);
                    return payState;
                }
            }
            /*更新用户理财账户资金 支付 或增投,或复投*/
//            try {
//                Cache.Delete("user_financial_" + userId);
//                FeignContainer feignContainer = new FeignContainer(ApiConfig.getValue("usecenter.url") + "/payUser");
//                PayUserApiService payUserApi = feignContainer.getFeignClient(PayUserApiService.class);
//                payUserApi.getFinancialDetail(userId);
//            } catch (Exception e) {
//                log.info("理财报警WARN:getFinancialDetail获取理财账户VDS余额异常" + userId);
//            }
            /*保存成功*/
            /*188vollar=当前多少usdt*/
//            	log.info("支付成功 188Vollar≈" + (bdVdsUsdtPrice.multiply(BigDecimal.valueOf(188)))
//            			+ "USDT 到期收益率为" + (bdVdsUsdtPrice.multiply(BigDecimal.valueOf(282))) + "USDT");
            String resultMsg = "您相当于出售 188 Vollar 获得 xxx USDT<br />理论收益为 yyy USDT";
            resultMsg = resultMsg.replaceAll("xxx", "" + xxxUsdt);
            resultMsg = resultMsg.replaceAll("yyy", "" + yyyUsdt);
            resultMsg = resultMsg.replaceAll("188", "" + difLevelAmount);
            log.info("理财报警INFO:保险投资保存支付设置排位 = " + sonUserId + " " + resultMsg);
            payState = true;
        } catch (Exception e) {
            log.info("理财报警ERROR:保险投资保存支付设置排位userProductInfoPay", e);
        }
        return payState;
    }

    

    /**
     * 子账号资金初始化
     */
    @SuppressWarnings("unchecked")
    public void setUserSonCapitalAccount(String userId, String userName) {
        /*SQL*/
        String sql = "";
        /**
         * select * from pay_user_wallet where userId = 1779376;
         * select * from pay_user_financial where userId = 1779376;
         */
        long cntWallent = 0;
        long cntFinancial = 0;
        sql = "select count(*) cnt from pay_user_wallet where userId = " + userId;
        log.info("sql = " + sql);
        List<Long> listWallent = (List<Long>) Data.GetOne("vip_main", sql, null);
        if (null != listWallent) {
            /*个数检查*/
            cntWallent = listWallent.get(0).intValue();
        }

        sql = "select count(*) cnt from pay_user_financial where userId = " + userId;
        log.info("sql = " + sql);
        List<Long> listFinancial = (List<Long>) Data.GetOne("vip_main", sql, null);
        if (null != listFinancial) {
            /*个数检查*/
            cntFinancial = listFinancial.get(0).intValue();
        }

        /*开启事务*/
        List<OneSql> sqls = new ArrayList<>();
        TransactionObject txObj = new TransactionObject();
        if (cntWallent < 1) {
            sql = "insert into pay_user_wallet (userId, userName, fundsType) values (" + userId + ", '" + userName + "', 51)";
            log.info("sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));

            sql = "insert into pay_user_wallet (userId, userName, fundsType) values (" + userId + ", '" + userName + "', 10)";
            log.info("sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));
        }

        if (cntFinancial < 1) {
            sql = "insert into pay_user_financial (userId, userName, fundsType) values (" + userId + ", '" + userName + "', 51)";
            log.info("sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));

            sql = "insert into pay_user_financial (userId, userName, fundsType) values (" + userId + ", '" + userName + "', 10)";
            log.info("sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));
        }

        if (cntWallent < 1 || cntFinancial < 1) {
            txObj.excuteUpdateList(sqls);
            if (txObj.commit()) {
                log.info("理财报警INFO:保险投资，子账号资金初始化成功 = " + userId);
            } else {
                log.info("理财报警ERROR:保险投资，子账号资金初始化失败 = " + userId);
            }
        }
    }

    @Page(Viewer = JSON)
    public void userFinancialInfo() {
        initLoginUser();
        try {
            /*调用用户信息*/
            String userId = loginUser.get_Id();
            String sql = "";
            /*获取用户认证支付信息*/
            sql = "select userVID, pInvitationCode, authPayFlag, matrixLevel from fin_userfinancialinfo where userId = " + userId;
            log.info("userFinancialInfo sql = " + sql);
            UserFinancialInfo userFinancialInfo = (UserFinancialInfo) Data.GetOne("vip_financial", sql, null, UserFinancialInfo.class);
            /**
             * userVID	vid地址
             * pInvitationCode	推荐人邀请码
             * userFinVdsAmount	可用数量
             * authPayFlag	认证和支付标识 '0默认值，1已认证，2已支付',
             */
            String userVID = "";
            String pInvitationCode = "";
            String authPayFlag = "0";
            String userFinVdsAmount = "0";
            String matrixLevel = "0";
            /*获取查询信息*/
            if (null != userFinancialInfo) {
                if (null != userFinancialInfo.getUserVID()) {
                    userVID = userFinancialInfo.getUserVID();
                }
                if (null != userFinancialInfo.getpInvitationCode()) {
                    pInvitationCode = userFinancialInfo.getpInvitationCode();
                }
                matrixLevel = userFinancialInfo.getMatrixLevel() + "";
                authPayFlag = userFinancialInfo.getAuthPayFlag() + "";
            }
            /*获取用户理财账户可用VDS*/
            try {
                FeignContainer feignContainer = new FeignContainer(ApiConfig.getValue("usecenter.url") + "/payUser");
                PayUserApiService payUserApi = feignContainer.getFeignClient(PayUserApiService.class);
                String strVDSFinancial = payUserApi.getFinancialDetail(userId);

                JSONObject jsonResult = com.alibaba.fastjson.JSONObject.parseObject(strVDSFinancial);
                if (null != jsonResult) {
                    JSONObject dataVDS = (JSONObject) jsonResult.get("VDS");
                    if (null != dataVDS && !StringUtils.isEmpty(dataVDS.getString("balance"))) {
                        userFinVdsAmount = dataVDS.getString("balance");
                    }
                }
            } catch (Exception e) {
                log.info("理财报警WARN:getFinancialDetail获取理财账户VDS余额异常");
            }

            /*拼装返回值*/
            Map<String, Object> result = new HashMap<>();
            result.put("userVID", userVID);
            result.put("pInvitationCode", pInvitationCode);
            result.put("userFinVdsAmount", userFinVdsAmount);
            result.put("authPayFlag", authPayFlag);
            result.put("matrixLevel", matrixLevel);
            log.info("result = " + JSONObject.toJSONString(result));
            json("ok", true, JSONObject.toJSONString(result), true);
        } catch (Exception e) {
            json("ok", false, null, true);
            log.info("理财报警ERROR:userFinancialInfo", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Page(Viewer = JSON)
    public void userVidUpdate() {
        initLoginUser();
        try {
            /**
             * userVID	vid地址
             * pInvitationCode	推荐人邀请码
             */
            /*调用用户信息*/
            String userId = loginUser.get_Id();
            /**
             * 支持子账号切换校验
             */
            String sonUserId = param("sonUserId");
            String sonUserName = param("sonUserName");
            /*校验标志*/
            boolean userSonRelationFlag = userSonRelationCheck(userId, sonUserName, sonUserId);
            if (userSonRelationFlag) {
                /*是子账号，切换数据处理逻辑*/
                userId = sonUserId;
//                userName = sonUserName;
            }
            String userVID = param("userVID");
            /*参数校验,先分别设置校验不通过时的返回值*/
            boolean crFlag = false;
            String uvidCR = "";
            if (StringUtils.isEmpty(userVID)) {
                uvidCR = "请输入VID地址";
                crFlag = true;
            } else if (userVID.length() != 35) {
                uvidCR = "VID地址位数输入错误,VID地址长度为35位";
                crFlag = true;
            }
            /*拼装返回值*/
            Map<String, Object> result = new HashMap<>();
            /*基础校验完成，如果有不通过项执行返回*/
            if (crFlag) {
                if (!StringUtils.isEmpty(uvidCR)) {
                    result.put("uvidCR", L(uvidCR));
                }
                log.info("result = " + result);
                json("ok", false, JSONObject.toJSONString(result), true);
                return;
            }

            /**
             * 验证vid是否正确,调用钱包接口
             * 调用接口的方法 配置参数vidcheck.url=http://192.168.2.243:8000
             * /checkVid?address=Vcba8sTRrQZJWH2osvuTgWWLApbJ9Tmzhxz
             * VcWVnyyUaeeoi8mWB2MRwjef59aHtEcUYF1
             */
            String url = ApiConfig.getValue("vidcheck.url");
            url += "/checkVid?address=" + userVID;
            log.info("url = " + url);
            String strResult = HttpUtil.doGet(url, null, 1000 * 60, 1000 * 60);
//			String strResult = "{'msg':'成功','code':200,'data':{'isVid':true}}";
            //.parseObject(result);
            JSONObject jsonResult = com.alibaba.fastjson.JSONObject.parseObject(strResult);
            log.info("jsonResult = " + jsonResult);
            /**
             * 解析校验返回信息
             * {"msg":"未能正确查询到vid相关交易信息，请确认！","code":-1}
             * {"msg":"成功","code":200,"data":{"isVid":true}}
             * {"msg":"成功","code":200,"data":{"isVid":false}}
             */
            String jsonResultCode = "";
            String jsonResultIsVid = "";
            if (null != jsonResult) {
                if (null != jsonResult.getString("code")) {
                    jsonResultCode = jsonResult.getString("code");
                }
            }
            log.info("jsonResultCode = " + jsonResultCode);
            if ("-1".equals(jsonResultCode)) {
                /*VID校验不通过，请核对后再输入*/
                uvidCR = "VID校验不通过，请核对后再输入";
                crFlag = true;
            } else if ("500".equals(jsonResultCode)) {
                /*系统繁忙，请稍后再试*/
                uvidCR = "系统繁忙，请稍后再试";
                crFlag = true;
                log.info("理财报警ERROR:调用vid校验接口异常");
            } else if ("200".equals(jsonResultCode)) {
                if (null != jsonResult.getString("data")) {
                    /*获取返回的所有数据数组，转换成对象集合*/
                    JSONObject datasArray = (JSONObject) jsonResult.get("data");
                    if (null != datasArray) {
                        jsonResultIsVid = datasArray.getString("isVid");
                    }
                }
                log.info("jsonResultIsVid = " + jsonResultIsVid);
                if (!"true".equals(jsonResultIsVid)) {
                    uvidCR = "VID校验不通过，请核对后再输入";
                    crFlag = true;
                }
            }
            /*判断VID是否使用过*/
            String sql = "";
            sql = "select count(*) cnt from fin_userfinancialinfo where userVID = '" + userVID + "' and userVID != '' ";
            log.info("userProductInfoSave sql = " + sql);
            List<Integer> listUserVid = (List<Integer>) Data.GetOne("vip_financial", sql, null);
            if (null != listUserVid) {
                /*个数检查*/
                if ("1".equals(listUserVid.get(0) + "")) {
                    uvidCR = "VID已被使用，请确认后再输入";
                    crFlag = true;
                }
            }
            log.info("uvidCR = " + uvidCR);
            /*VID校验，如果有不通过项执行返回*/
            if (crFlag) {
                if (!StringUtils.isEmpty(uvidCR)) {
                    result.put("uvidCR", L(uvidCR));
                }
                log.info("result = " + result);
                json("ok", false, JSONObject.toJSONString(result), true);
                return;
            }

            /*新增保存*/
            sql = "update fin_userfinancialinfo set userVID = '" + userVID + "' where userId = " + userId;
            log.info("userProductInfoSave sql = " + sql);
            int intInsert = Data.Update("vip_financial", sql, null);
            if (intInsert < 1) {
                json(L("保存失败"), false, null, true);
                log.info("理财报警：ERROR保存信息失败 sql = " + sql);
                return;
            }
            /*保存成功*/
            log.info("理财报警INFO:用户更新VID成功 = " + userId);
            json(L("保存成功"), true, null, true);
        } catch (Exception e) {
            json(L("保存失败"), false, null, true);
            log.info("理财报警ERROR:userProductInfoSave", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Page(Viewer = JSON)
    public void userProductInfoSave() {
        initLoginUser();
        try {
            /**
             * userVID	vid地址
             * pInvitationCode	推荐人邀请码
             */
            /*调用用户信息*/
            String userId = loginUser.get_Id();
            String userName = loginUser.getUserName();
            String userVID = param("userVID");
            String pInvitationCode = param("pInvitationCode");
            /*邀请人用户名*/
            String invitationUserName = "";
            /*参数校验,先分别设置校验不通过时的返回值*/
            boolean crFlag = false;
            String uvidCR = "";
            String picCR = "";
            if (StringUtils.isEmpty(userVID)) {

            } else if (userVID.length() > 35) {
                uvidCR = "VID地址输入错误";
                crFlag = true;
            }
            if (StringUtils.isEmpty(pInvitationCode)) {
                picCR = "请输入邀请码";
                crFlag = true;
            } else if (pInvitationCode.length() > 8) {
                picCR = "邀请码输入错误";
                crFlag = true;
            }
            /*拼装返回值*/
            Map<String, Object> result = new HashMap<>();
            /*基础校验完成，如果有不通过项执行返回*/
            if (crFlag) {
                if (!StringUtils.isEmpty(uvidCR)) {
                    result.put("uvidCR", L(uvidCR));
                }
                if (!StringUtils.isEmpty(picCR)) {
                    result.put("picCR", L(picCR));
                }
                log.info("result = " + result);
                json("ok", false, JSONObject.toJSONString(result), true);
                return;
            }

            /*校验邀请码是否合法以及是否存在,先校验长度*/
            if (pInvitationCode.length() != 8) {
                picCR = "邀请码不正确，请核对后再输入";
                crFlag = true;
            }
            String sql = "";
            sql = "select userName from fin_userfinancialinfo where invitationCode = '" + pInvitationCode + "'";
            log.info("userProductInfoSave sql = " + sql);
            List<String> listPInvitationCode = (List<String>) Data.GetOne("vip_financial", sql, null);
            if (null != listPInvitationCode) {
                /*个数检查*/
                invitationUserName = listPInvitationCode.get(0);
                if (StringUtils.isEmpty(invitationUserName)) {
                    picCR = "邀请码不正确，请核对后再输入";
                    crFlag = true;
                }
            } else {
                picCR = "邀请码不正确，请核对后再输入";
                crFlag = true;
            }
            /**
             * 验证vid是否正确,调用钱包接口
             * 调用接口的方法 配置参数vidcheck.url=http://192.168.2.243:8000
             * /checkVid?address=Vcba8sTRrQZJWH2osvuTgWWLApbJ9Tmzhxz
             * VcWVnyyUaeeoi8mWB2MRwjef59aHtEcUYF1
             */
            if (null != userVID && userVID.length() == 35) {
                /*35位长度才校验*/
                String url = ApiConfig.getValue("vidcheck.url");
                url += "/checkVid?address=" + userVID;
                log.info("url = " + url);
                String strResult = HttpUtil.doGet(url, null, 1000 * 60, 1000 * 60);
//				String strResult = "{'msg':'成功','code':200,'data':{'isVid':true}}";
                //.parseObject(result);
                JSONObject jsonResult = com.alibaba.fastjson.JSONObject.parseObject(strResult);
                log.info("jsonResult = " + jsonResult);
                /**
                 * 解析校验返回信息
                 * {"msg":"未能正确查询到vid相关交易信息，请确认！","code":-1}
                 * {"msg":"成功","code":200,"data":{"isVid":true}}
                 * {"msg":"成功","code":200,"data":{"isVid":false}}
                 */
                String jsonResultCode = "";
                String jsonResultIsVid = "";
                if (null != jsonResult) {
                    if (null != jsonResult.getString("code")) {
                        jsonResultCode = jsonResult.getString("code");
                    }
                }
                log.info("jsonResultCode = " + jsonResultCode);
                if ("-1".equals(jsonResultCode)) {
                    /*VID校验不通过，请核对后再输入*/
                    uvidCR = "VID校验不通过，请核对后再输入";
                    crFlag = true;
                } else if ("500".equals(jsonResultCode)) {
                    /*系统繁忙，请稍后再试*/
                    uvidCR = "系统繁忙，请稍后再试";
                    crFlag = true;
                    log.info("理财报警ERROR:调用vid校验接口异常");
                } else if ("200".equals(jsonResultCode)) {
                    if (null != jsonResult.getString("data")) {
                        /*获取返回的所有数据数组，转换成对象集合*/
                        JSONObject datasArray = (JSONObject) jsonResult.get("data");
                        if (null != datasArray) {
                            jsonResultIsVid = datasArray.getString("isVid");
                        }
                    }
                    log.info("jsonResultIsVid = " + jsonResultIsVid);
                    if (!"true".equals(jsonResultIsVid)) {
                        uvidCR = "VID校验不通过，请核对后再输入";
                        crFlag = true;
                    }
                }
            }

            /*判断VID是否使用过*/
            sql = "select count(*) cnt from fin_userfinancialinfo where userVID = '" + userVID + "' and userVID != '' ";
            log.info("userProductInfoSave sql = " + sql);
            List<Integer> listUserVid = (List<Integer>) Data.GetOne("vip_financial", sql, null);
            if (null != listUserVid) {
                /*个数检查*/
                if ("1".equals(listUserVid.get(0) + "")) {
                    uvidCR = "VID已被使用，请确认后再输入";
                    crFlag = true;
                }
            }
            log.info("uvidCR = " + uvidCR + ", picCR = " + picCR);
            /*VID校验，如果有不通过项执行返回*/
            if (crFlag) {
                if (!StringUtils.isEmpty(uvidCR)) {
                    result.put("uvidCR", L(uvidCR));
                }
                if (!StringUtils.isEmpty(picCR)) {
                    result.put("picCR", L(picCR));
                }
                log.info("result = " + result);
                json("ok", false, JSONObject.toJSONString(result), true);
                return;
            }
            /**
             * 先调用接口，如果接口无法正常返回，则不再保存
             */
            /*调用接口进行数据对接保存*/
            boolean saveFlag = true;
            try {
                /*调用接口进行数据对接保存 doPostData*/
                Map<String, Object> objectMap = new HashMap<String, Object>();
                objectMap.put("userId", userId);
                objectMap.put("username", userName);
                objectMap.put("address", userName);
                objectMap.put("recUser", invitationUserName);
                log.info("objectMap = " + objectMap);
                String urlFinancial = ApiConfig.getValue("urlfinancial.url");
                urlFinancial += "/vdsapollo/op/register";
                log.info("urlFinancial = " + urlFinancial);
                String resultInterface = "";
                /*接口返回码和返回消息*/
                String resultInterfaceCode = "";
                String resultInterfaceMsg = "";
                resultInterface = doPostData(urlFinancial, objectMap);
                JSONObject jsonResultInterface = com.alibaba.fastjson.JSONObject.parseObject(resultInterface);
                log.info("jsonResultInterface = " + jsonResultInterface);
                if (null != jsonResultInterface) {
                    if (null != jsonResultInterface.getString("code")) {
                        resultInterfaceCode = jsonResultInterface.getString("code");
                    }
                    if (null != jsonResultInterface.getString("message")) {
                        resultInterfaceMsg = jsonResultInterface.getString("message");
                    }
                    log.info("resultInterfaceCode = " + resultInterfaceCode);
                    if (!"200".equals(resultInterfaceCode)) {
                        if ("400".equals(resultInterfaceCode)) {
                            if (!"用户已存在".equals(resultInterfaceMsg)) {
                                saveFlag = false;
                                log.info("理财报警INTERFACE:userProductInfoSave = " + jsonResultInterface);
                            }
                        } else {
                            saveFlag = false;
                            log.info("理财报警INTERFACE:userProductInfoSave = " + jsonResultInterface);
                        }
                    }
                } else {
                    saveFlag = false;
                    log.info("理财报警INTERFACE:userProductInfoSave = " + jsonResultInterface);
                }
            } catch (Exception e) {
                saveFlag = false;
                log.info("理财报警INTERFACE:userProductInfoSave", e);
            }

            if (saveFlag) {
                /*新增保存*/
                sql = "insert into fin_userfinancialinfo (userId, userName, userVID, pInvitationCode, invitationUserName, authPayFlag) values "
                        + "('" + userId + "', '" + userName + "', '" + userVID + "', '" + pInvitationCode + "', '" + invitationUserName + "' , 1)";
                log.info("userProductInfoSave sql = " + sql);
                int intInsert = Data.Insert("vip_financial", sql, null);
                if (intInsert < 1) {
                    json(L("保存失败"), false, null, true);
                    log.info("理财报警：ERROR保存信息失败 sql = " + sql);
                    return;
                }
                /*保存成功*/
                log.info("理财报警INFO:用户保存成功 = " + userId);
                json(L("保存成功"), true, null, true);
            } else {
                json(L("保存失败"), false, null, true);
            }
        } catch (Exception e) {
            json(L("保存失败"), false, null, true);
            log.info("理财报警ERROR:userProductInfoSave", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Page(Viewer = JSON)
    public void userProductInfoPay() {
        initLoginUser();
        try {
            /*调用用户信息*/
            String userId = loginUser.get_Id();
            String userName = loginUser.getUserName();
            /*投资金额对应的矩阵等级*/
            String targetMatrixLevel = param("matrixLevel");
            String sql = "";
            /*获取vds_usdt实时价格*/
            String vdsUsdtPrice = Cache.Get("vds_usdt_l_price");
            log.info("vdsUsdtPrice = " + vdsUsdtPrice);
            if (StringUtils.isEmpty(vdsUsdtPrice)) {
                json(L("系统繁忙，请稍后再试"), false, null, true);
                log.info("理财报警ERROR:获取VDSUSDT成交价格异常");
                return;
            }
            BigDecimal bdVdsUsdtPrice = BigDecimal.ZERO;
            try {
                bdVdsUsdtPrice = new BigDecimal(vdsUsdtPrice);
            } catch (Exception e) {
                bdVdsUsdtPrice = BigDecimal.ZERO;
            }
            if (bdVdsUsdtPrice.compareTo(BigDecimal.ZERO) <= 0) {
                json(L("系统繁忙，请稍后再试"), false, null, true);
                log.info("理财报警ERROR:获取VDSUSDT成交价格异常");
                return;
            }
            /*参数校验,先分别设置校验不通过时的返回值*/
            /*获取产品状态标志，先从Redis获取,存放时间10秒*/
            String proState = RedisUtil.get("financial_proState");
            String proAmount = RedisUtil.get("financial_proAmount");
            BigDecimal bdProAmount = BigDecimal.ZERO;
//            log.info("proState = " + proState + ", proAmount = " + proAmount + ", bdProAmount = " + bdProAmount);
//            Map<String, String> productMap = new HashMap<String, String>();
//            if (StringUtils.isEmpty(proState) || StringUtils.isEmpty(proAmount)) {
//				/*如果非空字符串，而且不是1，表示暂未开启或者已关闭状态*/
//				/*重新冲数据库获取*/
//                productMap = resetProductRedis();
//                proState = (String) productMap.get("proState");
//                proAmount = (String) productMap.get("proAmount");
//            }
            /*投资金额*/
            bdProAmount = new BigDecimal(proAmount);

            log.info("proState = " + proState + ", proAmount = " + proAmount + ", bdProAmount = " + bdProAmount);
            if (!"1".equals(proState)) {
                /*该产品理财投资已结束！*/
                json(L("该产品理财投资已结束"), false, null, true);
                log.info("理财报警WARN:该产品理财投资已结束");
                return;
            }

//			if (bdProAmount.compareTo(new BigDecimal(188)) < 0) {
//				log.info("理财报警ERROR:产品投资价格小于188");
//				json(L("系统繁忙，请稍后再试"), false, null, true);
//				return;
//			}
            /*判断用户是否满足支付条件，同时检查是否已支付过*/
            /*'0默认值，1已认证，2已支付',*/
            String authPayFlag = "0";
            /*当前投资矩阵*/
            String curMatrixLevel = "0";
            /*投资次数*/
            int reinTimes = 0;
            BigDecimal curExpectProfitUsdt = BigDecimal.ZERO;
            BigDecimal curOutSurplusVDS = BigDecimal.ZERO;
            BigDecimal curInvestAvergPrice = BigDecimal.ZERO;
            int curVipFlag = 0;
            Date curResetProfitTime = null;
            sql = "select authPayFlag, matrixLevel, expectProfitUsdt, outSurplusVDS, investAvergPrice, "
            	+ "vipFlag, reinTimes, outFlag, resetProfitTime "
                + "from fin_userfinancialinfo where userId = " + userId;
            UserFinancialInfo userFinancialInfo = (UserFinancialInfo) Data.GetOne("vip_financial", sql, null, UserFinancialInfo.class);
            if (null != userFinancialInfo) {
                authPayFlag = userFinancialInfo.getAuthPayFlag() + "";
                curMatrixLevel = userFinancialInfo.getMatrixLevel() + "";
                curExpectProfitUsdt = userFinancialInfo.getExpectProfitUsdt();
                curOutSurplusVDS = userFinancialInfo.getOutSurplusVDS();
                curInvestAvergPrice = userFinancialInfo.getInvestAvergPrice();
                curVipFlag = userFinancialInfo.getVipFlag();
                reinTimes = userFinancialInfo.getReinTimes();
                curResetProfitTime = userFinancialInfo.getResetProfitTime();
            }
            /**
             * 当前投资矩阵级别和目标投资矩阵级别比较
             * 如果目标投资矩阵<=当前投资矩阵，则不允许投资
             */
            int intCurMatrixLevel = 0;
            int intTargetMatrixLevel = 0;
            try {
                intCurMatrixLevel = Integer.parseInt(curMatrixLevel);
                intTargetMatrixLevel = Integer.parseInt(targetMatrixLevel);
            } catch (Exception e) {
                log.info("理财报警ERROR:用户当前级别和目标级别异常:" + userId + ", curMatrixLevel = " + curMatrixLevel + ", targetMatrixLevel = " + targetMatrixLevel, e);
                json(L("非法输入，请确认后输入"), false, null, true);
                return;
            }
            /*增投标志*/
            boolean addProfitAmountFlag = false;
            /*复投标志*/
            boolean resetProfitAmountFlag = false;
            /*获取矩阵级别对应的投资金额*/
            BigDecimal curLevelAmount = FinancialProiftUtils.giveMatrixLevelProfitAmount(intCurMatrixLevel);
            BigDecimal targetLevelAmount = FinancialProiftUtils.giveMatrixLevelProfitAmount(intTargetMatrixLevel);
            BigDecimal difLevelAmount = targetLevelAmount;
            /*vip标志判断*/
            int vipFlag = 0;
            if (difLevelAmount.compareTo(bdProAmount) >= 0) {
                vipFlag = 1;
            }
//            BigDecimal difLevelAmount = targetLevelAmount.subtract(curLevelAmount);
            log.info("curLevelAmount = " + curLevelAmount + ", targetLevelAmount = " + targetLevelAmount + ", difLevelAmount = " + difLevelAmount);
            log.info("authPayFlag = " + authPayFlag + ", curMatrixLevel = " + curMatrixLevel);
            if (intTargetMatrixLevel != 6) {
                json(L("您投资的收益产品暂未开启，请选择投资188产品"), false, null, true);
                log.info("您投资的收益产品暂未开启，请选择投资188产品！" + userId + ", intTargetMatrixLevel = " + intTargetMatrixLevel);
//				json(L("非法访问，此投资金额暂未开启"), false, null, true);
//				log.info("理财报警WARN:非法访问，此投资金额暂未开启！" + userId + ", intTargetMatrixLevel = " + intTargetMatrixLevel);
                return;
            } else if ("3".equals(authPayFlag)) {
                resetProfitAmountFlag = true;
//                json(L("非法访问，请进行复投"), false, null, true);
//                log.info("理财报警WARN:复投中：" + userId + ", intTargetMatrixLevel = " + intTargetMatrixLevel);
//                return;
            } else if ("2".equals(authPayFlag) && (intCurMatrixLevel >= intTargetMatrixLevel)) {
                /*该产品理财投资已支付成功，不能重复支付*/
                json(L("该产品理财投资已支付成功，请确认投资金额"), false, null, true);
                log.info("理财报警WARN:该产品理财投资已支付成功，不能重复支付" + userId);
                return;
            } else if ("0".equals(authPayFlag)) {
                /*支付失败，请先确认vid和推荐人邀请码已保存！*/
                json(L("支付失败，请先确认VID和推荐人邀请码已保存"), false, null, true);
                log.info("理财报警ERROR:用户绕过保存，直接进行支付操作" + userId);
                return;
            } else if ("2".equals(authPayFlag) && (intCurMatrixLevel < intTargetMatrixLevel)) {
                /*增投*/
                addProfitAmountFlag = true;
            }
            /**
             * 投资188 Vollar可以获得100分的权重；0.53
             * 投资 88 Vollar可以获得45分的权重；0.511
             * 投资 38 Vollar可以获得20分的权重；0.52
             * 投资 18 Vollar可以获得10分的权重；0.55
             * 投资  8 Vollar可以获得4.5分的权重；0.625
             * 投资  2 Vollar可以获得1分的权重；0.5
             */
            BigDecimal targetVipWeight = FinancialProiftUtils.giveVIPWeight(intTargetMatrixLevel);
            BigDecimal curVipWeight = FinancialProiftUtils.giveVIPWeight(intCurMatrixLevel);
            /*先检查资金是否有足够的资金*/
            sql = "select balance from pay_user_financial where userid = " + userId + " and fundstype = 51";
            log.info("userProductInfoPay pay_user_financial balance sql = " + sql);
            List<BigDecimal> listPayUserFinancial = (List<BigDecimal>) Data.GetOne("vip_main", sql, null);
            BigDecimal userBalance = BigDecimal.ZERO;
            if (null == listPayUserFinancial || listPayUserFinancial.size() < 1) {
                json(L("理财账户初始化失败，请联系客服"), false, null, true);
                log.info("理财报警ERROR:理财账户初始化失败，请联系客服" + userId);
                return;
            } else {
                if (null != listPayUserFinancial.get(0)) {
                    userBalance = listPayUserFinancial.get(0);
                }
                log.info("userBalance = " + userBalance);
                if (userBalance.compareTo(difLevelAmount) < 0) {
                    json(L("理财账户资金不足，请先进行充值或者划转"), false, null, true);
                    log.info("理财报警WARN:理财账户资金不足，请先进行充值或者划转" + userId + ", userBalance = " + userBalance);
                    return;
                }
            }
            /**
             * 产品状态为开启，而且满足支付条件，开始进行支付处理。开启事务。
             * 1、插入表productinvest		产品投资表
             * 2、更新product				理财产品表		proTotalUser，proTotalAmount
             * 3、更新userfinancialinfo	用户理财信息表	authPayFlag	0默认值，1已认证，2已支付.invitationCode
             * 4、更新pay_user_financial	理财资金表		balance
             * 5、插入bill_financial 		理财流水表		理财投资188 流水类型 5301 支出
             */
            /*投资金额和预计收益金额预先处理*/
            BigDecimal xxxUsdt = bdVdsUsdtPrice.multiply(difLevelAmount).setScale(4, BigDecimal.ROUND_DOWN);
            BigDecimal yyyUsdt = bdVdsUsdtPrice.multiply(difLevelAmount.multiply(BigDecimal.valueOf(1.5))).setScale(4, BigDecimal.ROUND_DOWN);
            /**
             * 如果是增投，计算复投的均价和最终受益
             * 第一次投：2 价格5
             * 第二次投：6 价格10
             * 收益为： 2 * 5 * 1.5 + 6 * 10 * 1.5 = 105
             * 均价为：70 / 8 保留4位小数
             * 新增字段：investAvergPrice, investAmount, expectProfitUsdt, outSurplusVDS
             */
            BigDecimal bdInvestAvergPrice = BigDecimal.ZERO;
            if (addProfitAmountFlag) {
                if (curExpectProfitUsdt.compareTo(BigDecimal.ZERO) <= 0) {
                    json(L("理财账户增投信息初始化失败，请联系客服"), false, null, true);
                    log.info("理财报警WARN:理财账户增投信息初始化失败，请联系客服" + userId);
                    return;
                }
                bdInvestAvergPrice = (yyyUsdt.add(curExpectProfitUsdt))
                        .divide(targetLevelAmount.multiply(BigDecimal.valueOf(1.5)), 4, BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            }
            log.info("bdInvestAvergPrice = " + bdInvestAvergPrice);

            List<OneSql> sqls = new ArrayList<>();
            TransactionObject txObj = new TransactionObject();
            long currentTime = System.currentTimeMillis();
            /*1、插入表productinvest 产品投资表*/
            /*投资标志类型*/
            int doubleThrowFlag = 0;
            if (resetProfitAmountFlag) {
            	doubleThrowFlag = 3;
            } else if (addProfitAmountFlag) {
            	doubleThrowFlag = 1;
            }
            if (addProfitAmountFlag || resetProfitAmountFlag) {
                /*增投或复投,注意投资次数*/
            	/*自动复投标志，0首投，1, 为增投，2释放冻结资金触发 自动复投, 3为手动复投*/
                sql = "insert into fin_productinvest (userId, userName, fundsType, proId, investAmount, investProPeriod, "
                    + "vdsUsdtPrice, investUsdtAmount, expectProfitUsdt, investTime, matrixLevel, vipWeight, doubleThrowFlag) values "
                    + "(" + userId + ", '" + userName + "', 51, 'BWFP1', " + difLevelAmount + ", (1 + " + (reinTimes + 1) + "), "
                    + "" + bdVdsUsdtPrice + ", " + xxxUsdt + ", " + yyyUsdt + ", now(), " + intTargetMatrixLevel + ", "
                    + "" + targetVipWeight + ", " + doubleThrowFlag + " )";
            } else {
                /*第一次投资，投资次数1*/
                sql = "insert into fin_productinvest (userId, userName, fundsType, proId, investAmount, investProPeriod, "
                    + "vdsUsdtPrice, investUsdtAmount, expectProfitUsdt, investTime, matrixLevel, vipWeight) values "
                    + "(" + userId + ", '" + userName + "', 51, 'BWFP1', " + difLevelAmount + ", 1, " + bdVdsUsdtPrice + ", "
                    + "" + xxxUsdt + ", " + yyyUsdt + ", now(), " + intTargetMatrixLevel + ", "
                    + "" + targetVipWeight + " )";
            }
            log.info("userProductInfoPay productinvest sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_financial"));
            
            /*2、更新product 理财产品表 proTotalUser，proTotalAmount*/
            if (addProfitAmountFlag) {
                /*增投，人数不增加，只增加金额*/
                sql = "update fin_product set proTotalAmount = proTotalAmount + " + difLevelAmount + "";
                log.info("userProductInfoPay product sql = " + sql);
            } else {
                sql = "update fin_product set proTotalUser = proTotalUser + 1, proTotalAmount = proTotalAmount + " + difLevelAmount + "";
                log.info("userProductInfoPay product sql = " + sql);
            }
            sqls.add(new OneSql(sql, 1, null, "vip_financial"));
            /*3、更新userfinancialinfo 用户理财信息表 authPayFlag 0默认值，1已认证，2已支付。invitationCode*/
            /**
             * 新增字段：investAvergPrice, investAmount, expectProfitUsdt, outSurplusVDS
             * bdInvestAvergPrice 新均价
             * curExpectProfitUsdt 原始预期1.5倍收益
             */
            if (addProfitAmountFlag) {
                /*增投修改增投后的矩阵级别及以上新增信息*/
                sql = "update fin_userfinancialinfo set "
                    + "matrixLevel = " + intTargetMatrixLevel + ", "
                    + "vipWeight = " + targetVipWeight + ", "
                    + "investAvergPrice = " + bdInvestAvergPrice + ", "
                    + "investAmount = " + targetLevelAmount + ", "
                    + "expectProfitUsdt = (" + curExpectProfitUsdt + " + " + yyyUsdt + "), "
                    + "outSurplusVDS = (outSurplusVDS + " + difLevelAmount + "), "
                    + "vipFlag = " + vipFlag + " where userId = " + userId;
                log.info("userProductInfoPay userfinancialinfo sql = " + sql);
            } else if (resetProfitAmountFlag) {
                /*复投修改新的预期收益和复投时间,投资次数*/
                sql = "update fin_userfinancialinfo set authPayFlag = 2, reinTimes = " + (reinTimes + 1) + ", "
                    + "investAvergPrice = " + bdVdsUsdtPrice + ", "
                    + "investAmount = " + targetLevelAmount + ", "
                    + "expectProfitUsdt = " + yyyUsdt + ", "
                    + "outSurplusVDS = " + difLevelAmount + ", "
                    + "resetProfitTime = now() "
                    + "where userId = " + userId;
                log.info("userProductInfoPay userfinancialinfo sql = " + sql);
            } else {
                /*根据UUID生成邀请码*/
                String invitationCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                /*此处如果报错，用数据库唯一索引拦截，程序不在判断*/
                sql = "update fin_userfinancialinfo set authPayFlag = 2, modifyTime = now(), profitTime = now(), "
                    + "invitationCode = '" + invitationCode + "', "
                    + "matrixLevel = " + intTargetMatrixLevel + ", "
                    + "vipWeight = " + targetVipWeight + ", "
                    + "investAvergPrice = " + bdVdsUsdtPrice + ", "
                    + "investAmount = " + targetLevelAmount + ", "
                    + "expectProfitUsdt = " + yyyUsdt + ", "
                    + "outSurplusVDS = " + targetLevelAmount + ", "
                    + "vipFlag = " + vipFlag + " where userId = " + userId;
                log.info("userProductInfoPay userfinancialinfo sql = " + sql);
            }
            sqls.add(new OneSql(sql, 1, null, "vip_financial"));
            /*4、更新pay_user_financial	理财资金表 balance*/
            sql = "update pay_user_financial set balance = balance - " + difLevelAmount + " "
                + "where userid = " + userId + " and fundstype = 51 and balance - " + difLevelAmount + " >= 0";
            log.info("userProductInfoPay pay_user_financial sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));
            /*5、插入bill_financial 理财流水表 理财投资188 流水类型 5301 支出*/
            if (addProfitAmountFlag || resetProfitAmountFlag) {
                sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
                    + "remark, vdsUsdtPrice, matrixLevel, investProPeriod) "
                    + "select " + userId + ", '" + userName + "', 5301, " + difLevelAmount + ", "
                    + "" + currentTime + ", (balance + profit + insureInvestFreezeAmount), "
                    + "51, '理财投资', '', " + vdsUsdtPrice + ", " + intTargetMatrixLevel + ", (1 + " + (reinTimes + 1) + ") "
                    + "from pay_user_financial where userid = " + userId + " and fundstype = 51 for update";
            } else {
                sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
                    + "remark, vdsUsdtPrice, matrixLevel, investProPeriod) "
                    + "select " + userId + ", '" + userName + "', 5301, " + difLevelAmount + ", "
                    + "" + currentTime + ", (balance + profit + insureInvestFreezeAmount), "
                    + "51, '理财投资', '', " + vdsUsdtPrice + ", " + intTargetMatrixLevel + ", 1 "
                    + "from pay_user_financial where userid = " + userId + " and fundstype = 51 for update";
            }
            log.info("userProductInfoPay bill_financial sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));

            txObj.excuteUpdateList(sqls);
            if (txObj.commit()) {
                /*支付成功*/
                log.info("用户【 " + userId + "】支付成功");
            } else {
                json(L("支付失败"), false, null, true);
                return;
            }

            /*调用激活接口*/
            /*调用接口进行数据对接激活*/
            boolean rollBackFlag = false;
            if (!addProfitAmountFlag && !resetProfitAmountFlag) {
                try {
                    /*调用接口进行数据对接保存 doPostData*/
                    Map<String, Object> objectMap = new HashMap<String, Object>();
                    objectMap.put("userId", userId);
                    objectMap.put("oldLevel", curMatrixLevel);
                    objectMap.put("level", intTargetMatrixLevel);
                    objectMap.put("levelNum", difLevelAmount);
                    objectMap.put("vdsPrice", bdVdsUsdtPrice);
                    objectMap.put("expectProfit", yyyUsdt);
                    log.info("objectMap = " + objectMap);
                    String urlFinancial = ApiConfig.getValue("urlfinancial.url");
                    urlFinancial += "/vdsapollo/op/activate";
                    log.info("urlFinancial = " + urlFinancial);
                    String resultInterface = "";
                    /*接口返回码和返回消息*/
                    String resultInterfaceCode = "";
                    String resultInterfaceMsg = "";
                    resultInterface = doPostData(urlFinancial, objectMap);
                    JSONObject jsonResultInterface = com.alibaba.fastjson.JSONObject.parseObject(resultInterface);
                    log.info("jsonResultInterface = " + jsonResultInterface);
                    if (null != jsonResultInterface) {
                        if (null != jsonResultInterface.getString("code")) {
                            resultInterfaceCode = jsonResultInterface.getString("code");
                        }
                        if (null != jsonResultInterface.getString("message")) {
                            resultInterfaceMsg = jsonResultInterface.getString("message");
                        }
                        log.info("resultInterfaceCode = " + resultInterfaceCode);
                        if (!"200".equals(resultInterfaceCode)) {
                            if ("400".equals(resultInterfaceCode)) {
                                if (!"用户已激活".equals(resultInterfaceMsg)) {
                                    rollBackFlag = true;
                                    log.info("理财报警INTERFACE:userProductInfoPay = " + jsonResultInterface);
                                }
                            } else {
                                rollBackFlag = true;
                                log.info("理财报警INTERFACE:userProductInfoPay = " + jsonResultInterface);
                            }
                        }
                    } else {
                        rollBackFlag = true;
                        log.info("理财报警INTERFACE:userProductInfoPay = " + jsonResultInterface);
                    }
                } catch (Exception e) {
                    rollBackFlag = true;
                    log.info("理财报警INTERFACE:userProductInfoPay", e);
                }
            } else {
                /*增投逻辑*/
                try {
                    /*调用接口进行数据对接保存 doPostData*/
                    Map<String, Object> objectMap = new HashMap<String, Object>();
                    objectMap.put("userId", userId);
                    objectMap.put("oldLevel", curMatrixLevel);
                    objectMap.put("level", intTargetMatrixLevel);
                    objectMap.put("levelNum", targetLevelAmount);
                    objectMap.put("vdsPrice", bdVdsUsdtPrice);
                    objectMap.put("expectProfit", yyyUsdt);
                    /*投资类型（2为增投，3为复投）*/
                    if (addProfitAmountFlag) {
                        objectMap.put("investType", 2);
                    } else if (resetProfitAmountFlag) {
                        objectMap.put("investType", 3);
                    }
                    /*复投次数*/
                    objectMap.put("reinTimes", (reinTimes + 1));
                    log.info("objectMap = " + objectMap);
                    String urlFinancial = ApiConfig.getValue("urlfinancial.url");
                    urlFinancial += "/vdsapollo/op/increaseOrRein";
                    log.info("urlFinancial = " + urlFinancial);
                    String resultInterface = "";
                    /*接口返回码和返回消息*/
                    String resultInterfaceCode = "";
                    String resultInterfaceMsg = "";
                    resultInterface = doPostData(urlFinancial, objectMap);
                    JSONObject jsonResultInterface = com.alibaba.fastjson.JSONObject.parseObject(resultInterface);
                    log.info("jsonResultInterface = " + jsonResultInterface);
                    if (null != jsonResultInterface) {
                        if (null != jsonResultInterface.getString("code")) {
                            resultInterfaceCode = jsonResultInterface.getString("code");
                        }
                        if (null != jsonResultInterface.getString("message")) {
                            resultInterfaceMsg = jsonResultInterface.getString("message");
                        }
                        log.info("resultInterfaceCode = " + resultInterfaceCode);
                        if (!"200".equals(resultInterfaceCode)) {
                            if ("400".equals(resultInterfaceCode)) {
                                if (!"用户已激活FFF".equals(resultInterfaceMsg)) {
                                    rollBackFlag = true;
                                    log.info("理财报警INTERFACE:userProductInfoPay = " + jsonResultInterface);
                                }
                            } else {
                                rollBackFlag = true;
                                log.info("理财报警INTERFACE:userProductInfoPay = " + jsonResultInterface);
                            }
                        }
                    } else {
                        rollBackFlag = true;
                        log.info("理财报警INTERFACE:userProductInfoPay = " + jsonResultInterface);
                    }
                } catch (Exception e) {
                    rollBackFlag = true;
                    log.info("理财报警INTERFACE:userProductInfoPay", e);
                }
            }

            /**
             * 回滚操作，此处暂时先不回滚
             * 流水此处可删除，但是如果是复投就不能直接这么做
             */
            if (rollBackFlag) {
                sqls = new ArrayList<>();
                txObj = new TransactionObject();
                /*1、回滚，插入表productinvest 产品投资表*/
                if (addProfitAmountFlag || resetProfitAmountFlag) {
                    sql = "delete from fin_productinvest where userId = " + userId + " and investProPeriod = (1 + " + (reinTimes + 1) + ") "
                        + "and matrixLevel = " + intTargetMatrixLevel + "";
                } else {
                    sql = "delete from fin_productinvest where userId = " + userId + " and investProPeriod = 1 "
                        + "and matrixLevel = " + intTargetMatrixLevel + "";
                }
                log.info("userProductInfoPay productinvest sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_financial"));

                /*2、回滚，更新product 理财产品表 proTotalUser，proTotalAmount*/
                if (addProfitAmountFlag) {
                    /*增投，人数不增加，只增加金额*/
                    sql = "update fin_product set proTotalAmount = proTotalAmount - " + difLevelAmount + "";
                    log.info("userProductInfoPay product sql = " + sql);
                } else {
                    sql = "update fin_product set proTotalUser = proTotalUser - 1, proTotalAmount = proTotalAmount - " + difLevelAmount + "";
                    log.info("userProductInfoPay product sql = " + sql);
                }
                sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                /*3、更新userfinancialinfo 用户理财信息表 authPayFlag 0默认值，1已认证，2已支付。invitationCode*/
                /**
                 * 新增字段：investAvergPrice, investAmount, expectProfitUsdt, outSurplusVDS
                 * bdInvestAvergPrice 新均价
                 * curExpectProfitUsdt 原始预期1.5倍收益
                 */
                if (addProfitAmountFlag) {
                    /*增投只修改增投后的矩阵级别*/
                    sql = "update fin_userfinancialinfo set "
                        + "matrixLevel = " + intCurMatrixLevel + ", "
                        + "vipWeight = " + curVipWeight + ", "
                        + "investAvergPrice = " + curInvestAvergPrice + ", "
                        + "investAmount = " + curLevelAmount + ", "
                        + "expectProfitUsdt = " + curExpectProfitUsdt + ", "
                        + "outSurplusVDS = " + curOutSurplusVDS + ", "
                        + "vipFlag = " + curVipFlag + " where userId = " + userId;
                    log.info("userProductInfoPay userfinancialinfo sql = " + sql);
                } else if (resetProfitAmountFlag) {
                    /*复投*/
                    sql = "update fin_userfinancialinfo set authPayFlag = 3, reinTimes = " + (reinTimes) + ", "
                        + "investAvergPrice = " + curInvestAvergPrice + ", "
                        + "investAmount = " + curLevelAmount + ", "
                        + "expectProfitUsdt = " + curExpectProfitUsdt + ", "
                        + "outSurplusVDS = " + curOutSurplusVDS + ", "
                        + "resetProfitTime = " + curResetProfitTime + " "
                        + "where userId = " + userId;
                    log.info("userProductInfoPay userfinancialinfo sql = " + sql);
                } else {
                    sql = "update fin_userfinancialinfo set authPayFlag = 1, invitationCode = '', "
                        + "matrixLevel = " + intCurMatrixLevel + ", "
                        + "vipWeight = " + curVipWeight + ", "
                        + "investAvergPrice = " + curInvestAvergPrice + ", "
                        + "investAmount = " + curLevelAmount + ", "
                        + "expectProfitUsdt = " + curExpectProfitUsdt + ", "
                        + "outSurplusVDS = " + curOutSurplusVDS + ", "
                        + "vipFlag = 0 where userId = " + userId;
                    log.info("userProductInfoPay userfinancialinfo sql = " + sql);
                }
                sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                /*4、更新pay_user_financial	理财资金表 balance*/
                sql = "update pay_user_financial set balance = balance + " + difLevelAmount + " "
                    + "where userid = " + userId + " and fundstype = 51 ";
                log.info("userProductInfoPay pay_user_financial sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_main"));
                /*5、插入bill_financial 理财流水表 理财投资188 流水类型 5301 支出*/
                if (addProfitAmountFlag || resetProfitAmountFlag) {
                    sql = "delete from bill_financial where userid = " + userId + " and fundstype = 51 and type = 5301 "
                        + "and matrixLevel = " + intTargetMatrixLevel + " and investProPeriod = (1 + " + (reinTimes + 1) + ") ";
                } else {
                    sql = "delete from bill_financial where userid = " + userId + " and fundstype = 51 and type = 5301 "
                        + "and matrixLevel = " + intTargetMatrixLevel + " and investProPeriod = 1 ";
                }
                log.info("userProductInfoPay bill_financial sql = " + sql);
                sqls.add(new OneSql(sql, -1, null, "vip_main"));
                /*回滚处理*/
                txObj.excuteUpdateList(sqls);
                if (txObj.commit()) {
                    /*支付成功*/
                    log.info("理财报警WARN:用户【 " + userId + "】回滚处理成功");
                } else {
                    log.info("理财报警ERROR:支付回滚失败,userId = " + userId);
                }
                json(L("支付失败"), false, null, true);
                return;
            }
            /*更新用户理财账户资金 支付 或增投,或复投*/
            try {
                Cache.Delete("user_financial_" + userId);
                FeignContainer feignContainer = new FeignContainer(ApiConfig.getValue("usecenter.url") + "/payUser");
                PayUserApiService payUserApi = feignContainer.getFeignClient(PayUserApiService.class);
                payUserApi.getFinancialDetail(userId);
                
                /*回本每周主表更新*/
                sql = "update fin_userreturnorderinfo set authPayFlag = 2 where userId = " + userId;
                log.info("sql = " + sql);
                Data.Update("vip_financial", sql, null);
                
                /*回本全表更新*/
                sql = "update fin_userreturnorderinfoall set authPayFlag = 2 where userId = " + userId;
                log.info("sql = " + sql);
                Data.Update("vip_financial", sql, null);
            } catch (Exception e) {
                log.info("理财报警WARN:getFinancialDetail获取理财账户VDS余额异常" + userId);
            }
            /*保存成功*/
            if (bdVdsUsdtPrice.compareTo(BigDecimal.ZERO) > 0) {
                /*188vollar=当前多少usdt*/
//            	log.info("支付成功 188Vollar≈" + (bdVdsUsdtPrice.multiply(BigDecimal.valueOf(188)))
//            			+ "USDT 到期收益率为" + (bdVdsUsdtPrice.multiply(BigDecimal.valueOf(282))) + "USDT");
                String resultMsg = L("您投资约等于到期收益为");
                resultMsg = resultMsg.replaceAll("xxx", "" + xxxUsdt);
                resultMsg = resultMsg.replaceAll("yyy", "" + yyyUsdt);
                resultMsg = resultMsg.replaceAll("188", "" + difLevelAmount);

//            	/*查询本周新人加成*/
//            	String platNewVipWeekNotPayAmount = RedisUtil.get("financial_platNewVipWeekNotPayAmount");
//            	if (StringUtils.isEmpty(platNewVipWeekNotPayAmount)) {
//            		platNewVipWeekNotPayAmount = "0";
//            	}
//            	
//            	resultMsg = resultMsg.replaceAll("zzz", platNewVipWeekNotPayAmount);
                log.info("理财报警INFO = " + userId + " " + resultMsg);
                json(resultMsg, true, null, true);
            } else {
                json(L("支付成功"), true, null, true);
            }
        } catch (Exception e) {
            json(L("支付失败"), false, null, true);
            log.info("理财报警ERROR:userProductInfoPay", e);
        }
    }

//    @SuppressWarnings("unchecked")
//	@Page(Viewer = JSON)
//    public void userDoubleThrowPay() {
//        initLoginUser();
//        try {
//			/*调用用户信息*/
//            String userId = loginUser.get_Id();
//            String userName = loginUser.getUserName();
//			/*投资金额对应的矩阵等级*/
//            String targetMatrixLevel = param("matrixLevel");
//            String sql = "";
//			/*获取vds_usdt实时价格*/
//            String vdsUsdtPrice = Cache.Get("vds_usdt_l_price");
//            log.info("vdsUsdtPrice = " + vdsUsdtPrice);
//            if (StringUtils.isEmpty(vdsUsdtPrice)) {
//                json(L("系统繁忙，请稍后再试"), false, null, true);
//                log.info("理财报警ERROR:获取VDSUSDT成交价格异常");
//                return;
//            }
//            BigDecimal bdVdsUsdtPrice = BigDecimal.ZERO;
//            try {
//                bdVdsUsdtPrice = new BigDecimal(vdsUsdtPrice);
//            } catch (Exception e) {
//                bdVdsUsdtPrice = BigDecimal.ZERO;
//            }
//            if (bdVdsUsdtPrice.compareTo(BigDecimal.ZERO) <= 0) {
//                json(L("系统繁忙，请稍后再试"), false, null, true);
//                log.info("理财报警ERROR:获取VDSUSDT成交价格异常");
//                return;
//            }
//			/*参数校验,先分别设置校验不通过时的返回值*/
//			/*获取产品状态标志，先从Redis获取,存放时间10秒*/
//            String proState = RedisUtil.get("financial_proState");
//            String proAmount = RedisUtil.get("financial_proAmount");
//            BigDecimal bdProAmount = BigDecimal.ZERO;
//            log.info("proState = " + proState + ", proAmount = " + proAmount + ", bdProAmount = " + bdProAmount);
////            Map<String, String> productMap = new HashMap<String, String>();
////            if (StringUtils.isEmpty(proState) || StringUtils.isEmpty(proAmount)) {
////				/*如果非空字符串，而且不是1，表示暂未开启或者已关闭状态*/
////				/*重新冲数据库获取*/
////                productMap = resetProductRedis();
////                proState = (String) productMap.get("proState");
////                proAmount = (String) productMap.get("proAmount");
////            }
//			/*投资金额*/
//            bdProAmount = new BigDecimal(proAmount);
//
//            log.info("proState = " + proState + ", proAmount = " + proAmount + ", bdProAmount = " + bdProAmount);
//            if (!"1".equals(proState)) {
//				/*该产品理财投资已结束！*/
//                json(L("该产品理财投资已结束"), false, null, true);
//                log.info("理财报警WARN:该产品理财投资已结束");
//                return;
//            }
//			
//			/*判断用户是否满足支付条件，同时检查是否已支付过*/
//			/*0默认值，1已认证，2已支付(复投成功),3 复投中*/
//            String authPayFlag = "0";
//			/*当前投资矩阵*/
//            String curMatrixLevel = "0";
//            BigDecimal curExpectProfitUsdt = BigDecimal.ZERO;
//            BigDecimal curOutSurplusVDS = BigDecimal.ZERO;
//            BigDecimal curInvestAvergPrice = BigDecimal.ZERO;
//			/*复投次数*/
//            int reinTimes = 0;
//            sql = "select authPayFlag, matrixLevel, expectProfitUsdt, outSurplusVDS, investAvergPrice, reinTimes from fin_userfinancialinfo "
//                    + "where userId = " + userId;
//            UserFinancialInfo userFinancialInfo = (UserFinancialInfo) Data.GetOne("vip_financial", sql, null, UserFinancialInfo.class);
//            if (null != userFinancialInfo) {
//                authPayFlag = userFinancialInfo.getAuthPayFlag() + "";
//                curMatrixLevel = userFinancialInfo.getMatrixLevel() + "";
//                curExpectProfitUsdt = userFinancialInfo.getExpectProfitUsdt();
//                curOutSurplusVDS = userFinancialInfo.getOutSurplusVDS();
//                curInvestAvergPrice = userFinancialInfo.getInvestAvergPrice();
//                reinTimes = userFinancialInfo.getReinTimes();
//            }
//            /**
//             * 当前投资矩阵级别和目标投资矩阵级别比较
//             * 如果目标投资矩阵<=当前投资矩阵，则不允许投资
//             */
//            int intCurMatrixLevel = 0;
//            int intTargetMatrixLevel = 0;
//            try {
//                intCurMatrixLevel = Integer.parseInt(curMatrixLevel);
//                intTargetMatrixLevel = Integer.parseInt(targetMatrixLevel);
//            } catch (Exception e) {
//                log.info("理财报警ERROR:用户当前级别和目标级别异常:" + userId + ", curMatrixLevel = " + curMatrixLevel + ", targetMatrixLevel = " + targetMatrixLevel, e);
//                json(L("非法输入，请确认后输入"), false, null, true);
//                return;
//            }
//			/*获取矩阵级别对应的投资金额*/
//            BigDecimal curLevelAmount = giveMatrixLevelProfitAmount(intCurMatrixLevel);
//            BigDecimal targetLevelAmount = giveMatrixLevelProfitAmount(intTargetMatrixLevel);
////			BigDecimal difLevelAmount = targetLevelAmount.subtract(curLevelAmount);
//            log.info("curLevelAmount = " + curLevelAmount + ", targetLevelAmount = " + targetLevelAmount);
//            log.info("authPayFlag = " + authPayFlag + ", curMatrixLevel = " + curMatrixLevel);
//            if (intTargetMatrixLevel >= 7) {
//                json(L("非法访问，此投资金额暂未开启"), false, null, true);
//                log.info("理财报警WARN:非法访问，此投资金额暂未开启！" + userId + ", intTargetMatrixLevel = " + intTargetMatrixLevel);
//                return;
//            } else if (!"3".equals(authPayFlag)) {
//                json(L("非法访问，不能进行复投"), false, null, true);
//                log.info("理财报警WARN:非法访问，不能进行复投：" + userId + ", authPayFlag = " + authPayFlag);
//                return;
//            }
//            if (targetLevelAmount.compareTo(curLevelAmount) <= 0 && intTargetMatrixLevel < 6) {
//                json(L("复投金额必须大于上次投资金额"), false, null, true);
//                log.info("理财报警WARN:复投金额必须大于上次投资金额" + userId + ", targetLevelAmount = " + targetLevelAmount + ", curLevelAmount = " + curLevelAmount);
//                return;
//            }
//
//            /**
//             * 投资188 Vollar可以获得100分的权重；0.53
//             * 投资 88 Vollar可以获得45分的权重；0.511
//             * 投资 38 Vollar可以获得20分的权重；0.52
//             * 投资 18 Vollar可以获得10分的权重；0.55
//             * 投资  8 Vollar可以获得4.5分的权重；0.625
//             * 投资  2 Vollar可以获得1分的权重；0.5
//             */
//            BigDecimal targetVipWeight = giveVIPWeight(intTargetMatrixLevel);
//            BigDecimal curVipWeight = giveVIPWeight(intCurMatrixLevel);
//			/*先检查资金是否有足够的资金*/
//            sql = "select balance, reInvestment from pay_user_financial where userid = " + userId + " and fundstype = 51";
//            log.info("userProductInfoPay pay_user_financial balance sql = " + sql);
//            List<BigDecimal> listPayUserFinancial = (List<BigDecimal>) Data.GetOne("vip_main", sql, null);
//			/*用户余额，用户复投基金*/
//            BigDecimal userBalance = BigDecimal.ZERO;
//            BigDecimal userReInvestment = BigDecimal.ZERO;
//            if (null == listPayUserFinancial || listPayUserFinancial.size() < 1) {
//                json(L("理财账户初始化失败，请联系客服"), false, null, true);
//                log.info("理财报警ERROR:理财账户初始化失败，请联系客服" + userId);
//                return;
//            } else {
//                if (null != listPayUserFinancial.get(0)) {
//                    userBalance = listPayUserFinancial.get(0);
//                }
//                if (null != listPayUserFinancial.get(1)) {
//                    userReInvestment = listPayUserFinancial.get(1);
//                }
//                if ((userBalance.add(userReInvestment)).compareTo(targetLevelAmount) < 0) {
//                    json(L("理财账户资金不足，请先进行充值或者划转"), false, null, true);
//                    log.info("理财报警WARN:复投资金不足" + userId + ", userBalance = " + userBalance + ", userReInvestment = " + userReInvestment);
//                    return;
//                }
//            }
//            /**
//             * 进行复投，开始进行支付处理。开启事务。
//             * 1、插入表productinvest		产品投资表
//             * 2、更新product				理财产品表		proTotalUser，proTotalAmount
//             * 3、更新userfinancialinfo	用户理财信息表	authPayFlag	0默认值，1已认证，2已支付.invitationCode
//             * 4、更新pay_user_financial	理财资金表		balance
//             * 5、插入bill_financial 		理财流水表		理财投资188 流水类型 5301 支出
//             */
//			/*投资金额和预计收益金额预先处理*/
//            BigDecimal xxxUsdt = bdVdsUsdtPrice.multiply(targetLevelAmount).setScale(4, BigDecimal.ROUND_DOWN);
//            BigDecimal yyyUsdt = bdVdsUsdtPrice.multiply(targetLevelAmount.multiply(BigDecimal.valueOf(1.5))).setScale(4, BigDecimal.ROUND_DOWN);
////        	BigDecimal bdInvestAvergPrice = BigDecimal.ZERO;
//
//            List<OneSql> sqls = new ArrayList<>();
//            TransactionObject txObj = new TransactionObject();
//            long currentTime = System.currentTimeMillis();
//	        /*1、插入表productinvest 产品投资表*/
//            sql = "insert into fin_productinvest (userId, userName, fundsType, proId, investAmount, investProPeriod, "
//                    + "vdsUsdtPrice, investUsdtAmount, expectProfitUsdt, investTime, matrixLevel) values "
//                    + "(" + userId + ", '" + userName + "', 51, 'BWFP1', " + targetLevelAmount + ", (1 + " + (reinTimes + 1) + "), " + bdVdsUsdtPrice + ", "
//                    + "" + xxxUsdt + ", " + yyyUsdt + ", now(), " + intTargetMatrixLevel + " )";
//            log.info("userProductInfoPay productinvest sql = " + sql);
//            sqls.add(new OneSql(sql, 1, null, "vip_financial"));
//			/*2、更新product 理财产品表 proTotalUser，proTotalAmount*/
//			/*复投，人数不增加，只增加金额*/
//            sql = "update fin_product set proTotalAmount = proTotalAmount + " + targetLevelAmount + "";
//            log.info("userProductInfoPay product sql = " + sql);
//            sqls.add(new OneSql(sql, 1, null, "vip_financial"));
//			/*3、更新userfinancialinfo 用户理财信息表 authPayFlag 0默认值，1已认证，2已支付。invitationCode*/
//            /**
//             * 新增字段：investAvergPrice, investAmount, expectProfitUsdt, outSurplusVDS
//             * bdInvestAvergPrice 新均价
//             * curExpectProfitUsdt 原始预期1.5倍收益
//             */
//            sql = "update fin_userfinancialinfo set authPayFlag = 2, "
//                    + "matrixLevel = " + intTargetMatrixLevel + ", "
//                    + "vipWeight = " + targetVipWeight + ", "
//                    + "investAvergPrice = " + vdsUsdtPrice + ", "
//                    + "investAmount = " + targetLevelAmount + ", "
//                    + "expectProfitUsdt = " + yyyUsdt + ", "
//                    + "outSurplusVDS = " + targetLevelAmount + " where userId = " + userId + " and authPayFlag = 3 ";
//            log.info("userProductInfoPay userfinancialinfo sql = " + sql);
//            sqls.add(new OneSql(sql, 1, null, "vip_financial"));
//			/*4、更新pay_user_financial	理财资金表 balance*/
//            /**
//             * dtpUserBalance 计算需要扣除多少余额，如果有复投基金，先用复投基金
//             * 转入超级节点资金	toSuperNodeAmount
//             */
//            BigDecimal dtpUserBalance = BigDecimal.ZERO;
//            dtpUserBalance = userReInvestment.compareTo(targetLevelAmount) >= 0 ? BigDecimal.ZERO : targetLevelAmount.subtract(userReInvestment);
//            BigDecimal toSuperNodeAmount = BigDecimal.ZERO;
//            toSuperNodeAmount = userReInvestment.compareTo(targetLevelAmount) >= 0 ? userReInvestment.subtract(targetLevelAmount) : BigDecimal.ZERO;
//            sql = "update pay_user_financial set balance = balance - " + dtpUserBalance + ", reInvestment = 0 "
//                    + "where userid = " + userId + " and fundstype = 51 "
//                    + "and balance - " + dtpUserBalance + " >= 0 and reInvestment - " + userReInvestment + " >= 0 ";
//            log.info("userProductInfoPay pay_user_financial sql = " + sql);
//            sqls.add(new OneSql(sql, 1, null, "vip_main"));
//            /**
//             * 5、插入bill_financial
//             * 先记录1条 5372 增加，即使用了复投基金
//             * 再使用1条消耗 投资金额
//             */
//            if (userReInvestment.compareTo(BigDecimal.ZERO) > 0) {
//                sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
//                        + "remark, vdsUsdtPrice, investProPeriod, matrixLevel) "
//                        + "select " + userId + ", '" + userName + "', 5372, " + targetLevelAmount.subtract(dtpUserBalance) + "), "
//                        + "" + currentTime + ", (balance + profit + " + targetLevelAmount.subtract(dtpUserBalance) + "), "
//                        + "51, '复投基金复投', '', " + vdsUsdtPrice + ", (1 + " + (reinTimes + 1) + "), " + intTargetMatrixLevel + " "
//                        + "from pay_user_financial where userid = " + userId + " and fundstype = 51 for update";
//                log.info("userProductInfoPay bill_financial sql = " + sql);
//                sqls.add(new OneSql(sql, 1, null, "vip_main"));
//            }
//			/*常规投资*/
//            sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
//                    + "remark, vdsUsdtPrice, investProPeriod, matrixLevel) "
//                    + "select " + userId + ", '" + userName + "', 5301, " + targetLevelAmount + "), "
//                    + "" + currentTime + ", (balance + profit + insureInvestFreezeAmount), "
//                    + "51, '理财投资', '', " + vdsUsdtPrice + ", (1 + " + (reinTimes + 1) + "), " + intTargetMatrixLevel + " "
//                    + "from pay_user_financial where userid = " + userId + " and fundstype = 51 for update";
//            log.info("userProductInfoPay bill_financial sql = " + sql);
//            sqls.add(new OneSql(sql, 1, null, "vip_main"));
//            if (toSuperNodeAmount.compareTo(BigDecimal.ZERO) > 0) {
//				/*将多余的资金转入超级节点*/
//                /**
//                 * 复投基金多余资金转入 5386	pay_user_financial balance = balance + douProfitAmount
//                 * lvwa1900@163.com
//                 * 用户ID：1216832
//                 */
//                String fundsTypeComp = "5386";
//                String fundsNameComp = "复投基金转入超级节点搭建";
//                sql = "update pay_user_financial set balance = balance + " + toSuperNodeAmount + " where userid = 1216832 and fundstype = 51 ";
//                log.info("理财报警:HierarchyThread sql = " + sql);
//                sqls.add(new OneSql(sql, 1, null, "vip_main"));
//				/*1216832 记录流水fundsTypeComp  fundsNameComp */
//                sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
//                        + "remark, vdsUsdtPrice, investProPeriod, matrixLevel) "
//                        + "select 1216832, userName, " + fundsTypeComp + ", " + toSuperNodeAmount + ", " + currentTime + " , (balance + profit + insureInvestFreezeAmount), "
//                        + "51, '" + fundsNameComp + "', '" + userId + "', 0, 0, 0 "
//                        + "from pay_user_financial where userid = 1216832 and fundstype = 51 for update";
//                log.info("理财报警:HierarchyThread sql = " + sql);
//                sqls.add(new OneSql(sql, 1, null, "vip_main"));
//            }
//
//            txObj.excuteUpdateList(sqls);
//            if (txObj.commit()) {
//            	/*支付成功*/
//                log.info("用户【 " + userId + "】支付成功");
//            } else {
//                json(L("支付失败"), false, null, true);
//                return;
//            }
//            
//            /*调用复投接口*/
//        	/*调用接口进行数据对接激活*/
//            boolean rollBackFlag = false;
//			/*复投逻辑*/
//            try {
//				/*调用接口进行数据对接保存 doPostData*/
//                Map<String, Object> objectMap = new HashMap<String, Object>();
//                objectMap.put("userId", userId);
//                objectMap.put("level", intTargetMatrixLevel);
//                objectMap.put("levelNum", targetLevelAmount);
//                objectMap.put("vdsPrice", bdVdsUsdtPrice);
//                objectMap.put("expectProfit", yyyUsdt);
//				/*投资类型（2为增投，3为复投）*/
//                objectMap.put("investType", 3);
//				/*复投次数*/
//                objectMap.put("reinTimes", (reinTimes + 1));
//                log.info("objectMap = " + objectMap);
//                String urlFinancial = ApiConfig.getValue("urlfinancial.url");
//                urlFinancial += "/vdsapollo/op/increaseOrRein";
//                log.info("urlFinancial = " + urlFinancial);
//                String resultInterface = "";
//				/*接口返回码和返回消息*/
//                String resultInterfaceCode = "";
//                String resultInterfaceMsg = "";
//                resultInterface = doPostData(urlFinancial, objectMap);
//                JSONObject jsonResultInterface = com.alibaba.fastjson.JSONObject.parseObject(resultInterface);
//                log.info("jsonResultInterface = " + jsonResultInterface);
//                if (null != jsonResultInterface) {
//                    if (null != jsonResultInterface.getString("code")) {
//                        resultInterfaceCode = jsonResultInterface.getString("code");
//                    }
//                    if (null != jsonResultInterface.getString("message")) {
//                        resultInterfaceMsg = jsonResultInterface.getString("message");
//                    }
//                    log.info("resultInterfaceCode = " + resultInterfaceCode);
//                    if (!"200".equals(resultInterfaceCode)) {
//                        if ("400".equals(resultInterfaceCode)) {
//                            if (!"用户已激活FFF".equals(resultInterfaceMsg)) {
//                                rollBackFlag = true;
//                                log.info("理财报警INTERFACE:userProductInfoPay = " + jsonResultInterface);
//                            }
//                        } else {
//                            rollBackFlag = true;
//                            log.info("理财报警INTERFACE:userProductInfoPay = " + jsonResultInterface);
//                        }
//                    }
//                } else {
//                    rollBackFlag = true;
//                    log.info("理财报警INTERFACE:userProductInfoPay = " + jsonResultInterface);
//                }
//            } catch (Exception e) {
//                rollBackFlag = true;
//                log.info("理财报警INTERFACE:userProductInfoPay", e);
//            }
//
//            /**
//             * 回滚操作
//             * 流水此处可删除，但是如果是复投就不能直接这么做
//             */
//            if (rollBackFlag) {
//                sqls = new ArrayList<>();
//                txObj = new TransactionObject();
//				/*1、回滚，插入表productinvest 产品投资表*/
//		        
//		        /*1、插入表productinvest 产品投资表*/
////				sql = "insert into fin_productinvest (userId, userName, fundsType, proId, investAmount, investProPeriod, "
////					+ "vdsUsdtPrice, investUsdtAmount, expectProfitUsdt, investTime, matrixLevel) values "
////					+ "(" + userId + ", '" + userName + "', 51, 'BWFP1', " + targetLevelAmount + ", (1 + " + (reinTimes + 1) + "), " + bdVdsUsdtPrice + ", "
////					+ "" + xxxUsdt + ", " + yyyUsdt + ", now(), " + intTargetMatrixLevel + " )";
////				log.info("userProductInfoPay productinvest sql = " + sql);
////				sqls.add(new OneSql(sql, 1, null, "vip_financial"));
//
//
//                sql = "delete from fin_productinvest "
//                        + "where userId = " + userId + " and investProPeriod = (1 + " + (reinTimes + 1) + ") "
//                        + "and matrixLevel = " + intTargetMatrixLevel + "";
//                log.info("userProductInfoPay productinvest sql = " + sql);
//                sqls.add(new OneSql(sql, 1, null, "vip_financial"));
//				
//				/*2、回滚，更新product 理财产品表 proTotalUser，proTotalAmount*/
//                sql = "update fin_product set proTotalAmount = proTotalAmount - " + targetLevelAmount + "";
//                log.info("userProductInfoPay product sql = " + sql);
//                sqls.add(new OneSql(sql, 1, null, "vip_financial"));
//				/*3、更新userfinancialinfo 用户理财信息表 authPayFlag 0默认值，1已认证，2已支付。invitationCode*/
//                /**
//                 * 新增字段：investAvergPrice, investAmount, expectProfitUsdt, outSurplusVDS
//                 * bdInvestAvergPrice 新均价
//                 * curExpectProfitUsdt 原始预期1.5倍收益
//                 */
//
////				sql = "update fin_userfinancialinfo set authPayFlag = 2, "
////						+ "matrixLevel = " + intTargetMatrixLevel + ", "
////						+ "vipWeight = " + targetVipWeight + ", "
////						+ "investAvergPrice = " + vdsUsdtPrice + ", "
////						+ "investAmount = " + targetLevelAmount + ", "
////						+ "expectProfitUsdt = " + yyyUsdt + ", "
////						+ "outSurplusVDS = " + targetLevelAmount + " where userId = " + userId + " and authPayFlag = 3 ";
////					log.info("userProductInfoPay userfinancialinfo sql = " + sql);
////					sqls.add(new OneSql(sql, 1, null, "vip_financial"));
//				
//				
//				/*增投只修改增投后的矩阵级别*/
//                sql = "update fin_userfinancialinfo set authPayFlag = 3, "
//                        + "matrixLevel = " + intCurMatrixLevel + ", "
//                        + "vipWeight = " + curVipWeight + ", "
//                        + "investAvergPrice = " + curInvestAvergPrice + ", "
//                        + "investAmount = " + curLevelAmount + ", "
//                        + "expectProfitUsdt = " + curExpectProfitUsdt + ", "
//                        + "outSurplusVDS = " + curOutSurplusVDS + " where userId = " + userId;
//                log.info("userProductInfoPay userfinancialinfo sql = " + sql);
//                sqls.add(new OneSql(sql, 1, null, "vip_financial"));
//				/*4、更新pay_user_financial	理财资金表 balance*/
//
////				sql = "update pay_user_financial set balance = balance - " + dtpUserBalance + ", reInvestment = 0 "
////						+ "where userid = " + userId + " and fundstype = 51 "
////						+ "and balance - " + dtpUserBalance + " >= 0 and reInvestment - " + userReInvestment + " >= 0 ";
//
//                sql = "update pay_user_financial set balance = balance + " + dtpUserBalance + ", "
//                        + "reInvestment = " + userReInvestment + " "
//                        + "where userid = " + userId + " and fundstype = 51 ";
//                log.info("userProductInfoPay pay_user_financial sql = " + sql);
//                sqls.add(new OneSql(sql, 1, null, "vip_main"));
//				
//				/*删除5372流水*/
//                if (userReInvestment.compareTo(BigDecimal.ZERO) > 0) {
////					sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
////							+ "remark, vdsUsdtPrice, investProPeriod, matrixLevel) "
////							+ "select " + userId + ", '" + userName + "', 5372, " + targetLevelAmount.subtract(dtpUserBalance) + "), "
////							+ "" + currentTime + ", (balance + profit + insureInvestFreezeAmount + " + targetLevelAmount.subtract(dtpUserBalance) + "), "
////							+ "51, '复投基金复投', '', " + vdsUsdtPrice + ", (1 + " + (reinTimes + 1) + "), " + intTargetMatrixLevel + " "
////							+ "from pay_user_financial where userid = " + userId + " and fundstype = 51 for update";
//
//                    sql = "delete from bill_financial where userid = " + userId + " and fundstype = 51 and type = 5372 "
//                            + "and matrixLevel = (1 + " + (reinTimes + 1) + ")";
//                    log.info("userProductInfoPay bill_financial sql = " + sql);
//                    sqls.add(new OneSql(sql, -1, null, "vip_main"));
//                }
//				/*删除常规复投记录*/
////				sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
////						+ "remark, vdsUsdtPrice, investProPeriod, matrixLevel) "
////						+ "select " + userId + ", '" + userName + "', 5301, " + targetLevelAmount + "), "
////						+ "" + currentTime + ", (balance + profit + insureInvestFreezeAmount), "
////						+ "51, '理财投资', '', " + vdsUsdtPrice + ", (1 + " + (reinTimes + 1) + "), " + intTargetMatrixLevel + " "
////						+ "from pay_user_financial where userid = " + userId + " and fundstype = 51 for update";
//
//                sql = "delete from bill_financial where userid = " + userId + " and fundstype = 51 and type = 5301 "
//                        + "and matrixLevel = (1 + " + (reinTimes + 1) + ")";
//                log.info("userProductInfoPay bill_financial sql = " + sql);
//                sqls.add(new OneSql(sql, -1, null, "vip_main"));
//				
//				/*将多余的资金转入超级节点*/
//                /**
//                 * 复投基金多余资金转入 5386	pay_user_financial balance = balance + douProfitAmount
//                 * lvwa1900@163.com
//                 * 用户ID：1216832
//                 */
//                if (toSuperNodeAmount.compareTo(BigDecimal.ZERO) > 0) {
////					sql = "update pay_user_financial set balance = balance + " + toSuperNodeAmount + " where userid = 1216832 and fundstype = 51 ";
////					log.info("理财报警:HierarchyThread sql = " + sql);
////					sqls.add(new OneSql(sql, 1, null, "vip_main"));
////					/*1216832 记录流水fundsTypeComp  fundsNameComp */
////					sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
////						+ "remark, vdsUsdtPrice, investProPeriod, matrixLevel) "
////						+ "select 1216832, userName, " + fundsTypeComp + ", " + toSuperNodeAmount + ", now() , (balance + profit + insureInvestFreezeAmount), "
////						+ "51, '" + fundsNameComp + "', '" + userId + "', 0, 0, 0 "
////						+ "from pay_user_financial where userid = 1216832 and fundstype = 51 for update";
////					log.info("理财报警:HierarchyThread sql = " + sql);
////					sqls.add(new OneSql(sql, 1, null, "vip_main"));
//
//                    sql = "update pay_user_financial set balance = balance - " + toSuperNodeAmount + " where userid = 1216832 and fundstype = 51";
//                    log.info("userProductInfoPay pay_user_financial sql = " + sql);
//                    sqls.add(new OneSql(sql, 1, null, "vip_main"));
//					
//					/*删除流水*/
//                    sql = "delete from bill_financial where userid = 1216832 and fundstype = 51 and type = 5386 "
//                            + "and matrixLevel = (1 + " + (reinTimes + 1) + ")";
//                    log.info("userProductInfoPay bill_financial sql = " + sql);
//                    sqls.add(new OneSql(sql, -1, null, "vip_main"));
//                }
//				/*回滚处理*/
//                txObj.excuteUpdateList(sqls);
//                if (txObj.commit()) {
//	            	/*支付成功*/
//                    log.info("理财报警WARN:用户【 " + userId + "】回滚处理成功");
//                } else {
//                    log.info("理财报警ERROR:支付回滚失败,userId = " + userId);
//                }
//                json(L("支付失败"), false, null, true);
//                return;
//            }
//			/*更新用户理财账户资金 手动复投*/
//            try {
//                Cache.Delete("user_financial_" + userId);
//                FeignContainer feignContainer = new FeignContainer(ApiConfig.getValue("usecenter.url") + "/payUser");
//                PayUserApiService payUserApi = feignContainer.getFeignClient(PayUserApiService.class);
//                payUserApi.getFinancialDetail(userId);
//            } catch (Exception e) {
//                log.info("理财报警WARN:getFinancialDetail获取理财账户VDS余额异常" + userId);
//            }
//            /*保存成功*/
//            if (bdVdsUsdtPrice.compareTo(BigDecimal.ZERO) > 0) {
//            	/*188vollar=当前多少usdt*/
////            	log.info("支付成功 188Vollar≈" + (bdVdsUsdtPrice.multiply(BigDecimal.valueOf(188)))
////            			+ "USDT 到期收益率为" + (bdVdsUsdtPrice.multiply(BigDecimal.valueOf(282))) + "USDT");
//                String resultMsg = L("您投资约等于到期收益为");
//                resultMsg = resultMsg.replaceAll("xxx", "" + xxxUsdt);
//                resultMsg = resultMsg.replaceAll("yyy", "" + yyyUsdt);
//                resultMsg = resultMsg.replaceAll("188", "" + targetLevelAmount);
//                log.info("理财报警INFO = " + userId + " " + resultMsg);
//                json(resultMsg, true, null, true);
//            } else {
//                json(L("支付成功"), true, null, true);
//            }
//        } catch (Exception e) {
//            json(L("支付失败"), false, null, true);
//            log.info("理财报警ERROR:userProductInfoPay", e);
//        }
//    }

    @SuppressWarnings("unchecked")
    @Page(Viewer = JSON)
    public void userFinCenRewardInfo() {
        initLoginUser();
        try {
            /*调用用户信息*/
            String userId = loginUser.get_Id();
            String userName = loginUser.getUserName();
            /**
             * 支持子账号切换校验
             */
            String sonUserId = param("sonUserId");
            String sonUserName = param("sonUserName");
            /*校验标志*/
            boolean userSonRelationFlag = userSonRelationCheck(userId, sonUserName, sonUserId);
            if (userSonRelationFlag) {
                /*是子账号，切换数据处理逻辑*/
                userId = sonUserId;
                userName = sonUserName;
            }
            /*定义接口返回值*/
            /*层级建点奖励*/
            String hierarchyBuildAmount = "0";
            String hierarchyBuildAmountUsdt = "0";
            /*直推执导奖励*/
            String pushGuidanceAmount = "0";
            String pushGuidanceAmountUsdt = "0";
            /*级别晋升奖励*/
            String levelPromotionAmount = "0";
            String levelPromotionAmountUsdt = "0";
            /*全球领袖分红奖励*/
            String leaderBonusAmount = "0";
            String leaderBonusAmountUsdt = "0";
            /*全球领袖分红权重*/
            String leaderBonusWeight = "0";
//			/*VIP分红奖励*/
//			String superNodeAmount = "0";
//			String superNodeAmountUsdt = "0";
//			/*VIP分红权重*/
//			String superNodeWeight = "0";
//			/*生态体系参与奖励*/
//			String ecologySystemAmount = "0";
//			String ecologySystemAmountUsdt = "0";
            /*新增VIP增值奖励*/
            String newVipUserAmount = "0";
            String newVipUserAmountUsdt = "0";
            /*层级建点奖励（层级）*/
            String hierarchyBuildFloor = "0";
            /*直推执导奖励（百分比）*/
            String pushGuidanceRatio = "0";
            /*级别晋升奖励（百分比）*/
            String levelPromotionRatio = "0";

            /*查询需要的相关字段*/
            String sql = "";
            sql = "select user_id, bonus_type, sum(bonus_price) bonus_price, sum(true_price) true_price from t_bonus "
                    + "where user_id = " + userId + " and deal_flag = 0 group by user_id, bonus_type ";
            log.info("userFinCenRewardInfo sql = " + sql);
            List<Bean> listFinancialBonus = (List<Bean>) Data.Query("vdsapollo", sql, null, FinancialBonus.class);
            log.info("listFinancialBonus.size = " + listFinancialBonus.size());

            /*定义循环变量*/
            FinancialBonus financialBonus;
            int bonusType = 0;
            BigDecimal bonusPrice = BigDecimal.ZERO;
            BigDecimal truePrice = BigDecimal.ZERO;
            if (null != listFinancialBonus && listFinancialBonus.size() > 0) {
                for (int i = 0; i < listFinancialBonus.size(); i++) {
                    financialBonus = (FinancialBonus) listFinancialBonus.get(i);
                    bonusType = financialBonus.getBonus_type();
                    bonusPrice = financialBonus.getBonus_price().setScale(3, BigDecimal.ROUND_DOWN);
                    truePrice = financialBonus.getTrue_price().setScale(3, BigDecimal.ROUND_DOWN);
                    if (1 == bonusType) {
                        hierarchyBuildAmount = bonusPrice + "";
                        hierarchyBuildAmountUsdt = truePrice + "";
                    } else if (2 == bonusType) {
                        pushGuidanceAmount = bonusPrice + "";
                        pushGuidanceAmountUsdt = truePrice + "";
                    } else if (3 == bonusType) {
                        levelPromotionAmount = bonusPrice + "";
                        levelPromotionAmountUsdt = truePrice + "";
                    } else if (4 == bonusType) {
                        leaderBonusAmount = bonusPrice + "";
                        leaderBonusAmountUsdt = truePrice + "";
                    }
                }
            }

            /*层级建点奖励（层级）*/
            int invitationActiveUser = 0;
            sql = "select count(*) cnt from fin_userfinancialinfo "
                    + "where invitationUserName = '" + userName + "' and authPayFlag != 1 ";
            log.info("userFinCenRewardInfo sql = " + sql);
            List<Long> listHierarchyBuildFloor = (List<Long>) Data.GetOne("vip_financial", sql, null);
            if (null != listHierarchyBuildFloor) {
                invitationActiveUser = listHierarchyBuildFloor.get(0).intValue();
            }
            hierarchyBuildFloor = FinancialProiftUtils.giveHierarchyBuildFloor(invitationActiveUser) + "";
            /*直推执导奖励（百分比）*/
            pushGuidanceRatio = FinancialProiftUtils.givePushGuidanceRatio(invitationActiveUser) + "";
            /**
             * 级别晋升奖励（百分比）
             * 全球领袖分红权重
             */
//			int intLevelPromotionRatio = 0;
            sql = "select level, weight from t_user where id = " + userId;
            log.info("userFinCenRewardInfo sql = " + sql);
            List<Integer> listLevelPromotionRatio = (List<Integer>) Data.GetOne("vdsapollo", sql, null);
            if (null != listLevelPromotionRatio) {
                levelPromotionRatio = listLevelPromotionRatio.get(0) + "";
                leaderBonusWeight = listLevelPromotionRatio.get(1) + "";
            }
//			levelPromotionRatio = intLevelPromotionRatio + "";

//			sql = "select weight from t_user where id = " + userId;
//			log.info("userFinCenInfo sql = " + sql);
//			List<Integer> listLeaderBonusWeight = (List<Integer>) Data.GetOne("vdsapollo", sql, null);
//			if (null != listLeaderBonusWeight) {
//				if (null != listLeaderBonusWeight.get(0)) {
//					leaderBonusWeight = listLeaderBonusWeight.get(0) + "";
//				}
//			}
            /*全球领袖分红奖励（百分比）*/
            String leaderBonusRatio = "0";
            BigDecimal bdLeaderBonusRatio = BigDecimal.ZERO;
            String platLeaderBonusWeight = RedisUtil.get("financial_platLeaderBonusWeight");
            BigDecimal bdPlatLeaderBonusWeight = BigDecimal.ZERO;
            if (StringUtils.isEmpty(platLeaderBonusWeight)) {
                platLeaderBonusWeight = "0";
            }
            /*计算*/
            BigDecimal bdLeaderBonusWeight = BigDecimal.ZERO;
            try {
                bdLeaderBonusWeight = BigDecimal.valueOf(Long.valueOf(leaderBonusWeight));
                bdPlatLeaderBonusWeight = BigDecimal.valueOf(Long.valueOf(platLeaderBonusWeight));
            } catch (Exception e) {
                bdLeaderBonusWeight = BigDecimal.ZERO;
                bdPlatLeaderBonusWeight = BigDecimal.ZERO;
            }
            if (bdLeaderBonusWeight.compareTo(BigDecimal.ZERO) <= 0) {
                bdLeaderBonusRatio = BigDecimal.ZERO;
                leaderBonusRatio = "0";
            } else if (bdPlatLeaderBonusWeight.compareTo(BigDecimal.ZERO) <= 0) {
                bdLeaderBonusRatio = BigDecimal.ZERO;
                leaderBonusRatio = "0";
            } else {
                bdLeaderBonusRatio = (bdLeaderBonusWeight.multiply(new BigDecimal(100))).divide(bdPlatLeaderBonusWeight, 2, BigDecimal.ROUND_DOWN);
                leaderBonusRatio = bdLeaderBonusRatio + "";
            }

            /*平台总生态回馈*/
            String platEcologySystemAmount = RedisUtil.get("financial_platEcologySystemAmount");
            String platEcologySystemAmountUsdt = RedisUtil.get("financial_platEcologySystemAmountUsdt");
            if (StringUtils.isEmpty(platEcologySystemAmount)) {
                platEcologySystemAmount = "0";
            }
            if (StringUtils.isEmpty(platEcologySystemAmountUsdt)) {
                platEcologySystemAmountUsdt = "0";
            }
            /**
             * platSuperNodePayAmount			超级主节点累积分配
             * platSuperNodePayAmountUsdt		超级主节点累积分配折算成USDT
             * platSuperNodeNotPayAmount		超级主节点待分配
             * platSuperNodeNotPayAmountUsdt	超级主节点待分配折算成USDT
             * platNewVipWeekAmount				本周新VIP加成
             * platNewVipWeekAmountUsdt			本周新VIP加成折算成USDT
             */
            String platSuperNodePayAmount = RedisUtil.get("financial_platSuperNodePayAmount");
            String platSuperNodePayAmountUsdt = RedisUtil.get("financial_platSuperNodePayAmountUsdt");
            String platSuperNodeNotPayAmount = RedisUtil.get("financial_platSuperNodeNotPayAmount");
            String platSuperNodeNotPayAmountUsdt = RedisUtil.get("financial_platSuperNodeNotPayAmountUsdt");
            String platNewVipWeekAmount = RedisUtil.get("financial_platNewVipWeekAmount");
            String platNewVipWeekAmountUsdt = RedisUtil.get("financial_platNewVipWeekAmountUsdt");
            /*本周新VIP加成未分配VDS	RedisUtil.set("financial_platNewVipWeekNotPayAmount", "0", 0);*/
            String platNewVipWeekNotPayAmount = RedisUtil.get("financial_platNewVipWeekNotPayAmount");
            /*本周未释放贡献 platReleaseNotPayAmount*/
            String platReleaseNotPayAmount = RedisUtil.get("financial_platReleaseNotPayAmount");
            /*平台累积释放 platReleasePayAmount*/
            String platReleasePayAmount = RedisUtil.get("financial_platReleasePayAmount");

            if (StringUtils.isEmpty(platSuperNodePayAmount)) {
                platSuperNodePayAmount = "0";
            }
            if (StringUtils.isEmpty(platSuperNodePayAmountUsdt)) {
                platSuperNodePayAmountUsdt = "0";
            }
            if (StringUtils.isEmpty(platSuperNodeNotPayAmount)) {
                platSuperNodeNotPayAmount = "0";
            }
            if (StringUtils.isEmpty(platSuperNodeNotPayAmountUsdt)) {
                platSuperNodeNotPayAmountUsdt = "0";
            }
            if (StringUtils.isEmpty(platNewVipWeekAmount)) {
                platNewVipWeekAmount = "0";
            }
            if (StringUtils.isEmpty(platNewVipWeekAmountUsdt)) {
                platNewVipWeekAmountUsdt = "0";
            }
            if (StringUtils.isEmpty(platNewVipWeekNotPayAmount)) {
                platNewVipWeekNotPayAmount = "0";
            }
            if (StringUtils.isEmpty(platReleaseNotPayAmount)) {
                platReleaseNotPayAmount = "0";
            }
            if (StringUtils.isEmpty(platReleasePayAmount)) {
                platReleasePayAmount = "0";
            }
            /*全球领袖分红池*/
            String platLeaderBonusAmount = RedisUtil.get("financial_platLeaderBonusAmount");
            if (StringUtils.isEmpty(platLeaderBonusAmount)) {
                platLeaderBonusAmount = "0";
            }
            /*拼装返回值*/
            Map<String, Object> result = new HashMap<>();
            /*层级建点奖励*/
            result.put("hierarchyBuildAmount", hierarchyBuildAmount);
            result.put("hierarchyBuildAmountUsdt", hierarchyBuildAmountUsdt);
            /*直推执导奖励*/
            result.put("pushGuidanceAmount", pushGuidanceAmount);
            result.put("pushGuidanceAmountUsdt", pushGuidanceAmountUsdt);
            /*级别晋升奖励*/
            result.put("levelPromotionAmount", levelPromotionAmount);
            result.put("levelPromotionAmountUsdt", levelPromotionAmountUsdt);
            /*全球领袖分红奖励*/
            result.put("leaderBonusAmount", leaderBonusAmount);
            result.put("leaderBonusAmountUsdt", leaderBonusAmountUsdt);
            /*全球领袖分红权重*/
            result.put("leaderBonusWeight", leaderBonusWeight);
            /*VIP分红奖励*/
//			result.put("superNodeAmount", superNodeAmount);
//			result.put("superNodeAmountUsdt", superNodeAmountUsdt);
//			/*VIP分红权重*/
//			result.put("superNodeWeight", superNodeWeight);
            /*生态体系参与奖励*/
//			result.put("ecologySystemAmount", ecologySystemAmount);
//			result.put("ecologySystemAmountUsdt", ecologySystemAmountUsdt);
            /*新增VIP增值奖励*/
            result.put("newVipUserAmount", newVipUserAmount);
            result.put("newVipUserAmountUsdt", newVipUserAmountUsdt);
            /*层级建点奖励（层级）*/
            result.put("hierarchyBuildFloor", hierarchyBuildFloor);
            /*直推执导奖励（百分比）*/
            result.put("pushGuidanceRatio", pushGuidanceRatio);
            /*级别晋升奖励（百分比）*/
            result.put("levelPromotionRatio", levelPromotionRatio);
            /*全球领袖分红奖励（百分比）*/
            result.put("leaderBonusRatio", leaderBonusRatio);
            result.put("platLeaderBonusWeight", platLeaderBonusWeight);

            /*平台生态体系总数*/
            result.put("platEcologySystemAmount", platEcologySystemAmount);
            result.put("platEcologySystemAmountUsdt", platEcologySystemAmountUsdt);
            /**
             * platSuperNodePayAmount			超级主节点累积分配
             * platSuperNodePayAmountUsdt		超级主节点累积分配折算成USDT
             * platSuperNodeNotPayAmount		超级主节点待分配
             * platSuperNodeNotPayAmountUsdt	超级主节点待分配折算成USDT
             * platNewVipWeekAmount				本周新VIP加成
             * platNewVipWeekAmountUsdt			本周新VIP加成折算成USDT
             */
            result.put("platSuperNodePayAmount", platSuperNodePayAmount);
            result.put("platSuperNodePayAmountUsdt", platSuperNodePayAmountUsdt);
            result.put("platSuperNodeNotPayAmount", platSuperNodeNotPayAmount);
            result.put("platSuperNodeNotPayAmountUsdt", platSuperNodeNotPayAmountUsdt);
            result.put("platNewVipWeekAmount", platNewVipWeekAmount);
            result.put("platNewVipWeekAmountUsdt", platNewVipWeekAmountUsdt);
            result.put("platNewVipWeekNotPayAmount", platNewVipWeekNotPayAmount);
            result.put("platReleaseNotPayAmount", platReleaseNotPayAmount);
            result.put("platReleasePayAmount", platReleasePayAmount);
            /*全球领袖分红池*/
            result.put("platLeaderBonusAmount", platLeaderBonusAmount);

            log.info("result = " + JSONObject.toJSONString(result));
            json("ok", true, JSONObject.toJSONString(result), true);
        } catch (Exception e) {
            json("ok", false, null, true);
            log.info("理财报警ERROR:userFinCenRewardInfo", e);
        }
    }

    public boolean userSonRelationCheck(String userId, String userName, String sonUserId) {
        boolean userSonRelationFlag = false;
        
        /*版本不需要，统一返回false*/
        return userSonRelationFlag;
        
//        /**
//         * 支持子账号切换校验
//         */
//        /*查询SQL*/
//        String sql = "";
//        if (StringUtils.isEmpty(sonUserId)) {
//            return userSonRelationFlag;
//        }
//        /*判断子账号是否归属此用户*/
//        sql = "select count(*) cnt from fin_userfinancialinfo where parentUserId = '" + userId + "' and userId = '" + sonUserId + "' ";
//        int sonUserCount = 0;
//        List<Long> listSonUser = (List<Long>) Data.GetOne("vip_financial", sql, null);
//        if (null != listSonUser) {
//            /*个数检查*/
//            sonUserCount = listSonUser.get(0).intValue();
//        }
//        log.info("sonUserCount = " + sonUserCount);
//        if (1 == sonUserCount) {
//            userSonRelationFlag = true;
//        }
//
//        return userSonRelationFlag;
    }

    @SuppressWarnings("unchecked")
    @Page(Viewer = JSON)
    public void userAvaTransferAmount() {
        /*动态收益资金释放*/
        initLoginUser();
        try {
            /*调用用户信息*/
            String userId = loginUser.get_Id();
            String userName = loginUser.getUserName();
            /**
             * 支持子账号切换校验
             */
            String sonUserId = param("sonUserId");
            String sonUserName = param("sonUserName");
            /*校验标志*/
            boolean userSonRelationFlag = userSonRelationCheck(userId, sonUserName, sonUserId);
            if (userSonRelationFlag) {
                /*是子账号，切换数据处理逻辑*/
                userId = sonUserId;
                userName = sonUserName;
            }

            /*释放金额*/
            String avaTransferAmount = param("avaTransferAmount");
            String capitalPwd = param("capitalPwd");
            BigDecimal bdAvaTransferAmount = BigDecimal.ZERO;
            try {
                bdAvaTransferAmount = new BigDecimal(avaTransferAmount);
            } catch (Exception e) {
                json(L("非法输入，请确认后输入"), false, null, true);
                log.info("理财报警ERROR:划转金额转换失败, userId = " + userId + ", avaTransferAmount = " + avaTransferAmount);
                return;
            }

            /**
             * 资金密码判断
             */
            if (StringUtils.isEmpty(loginUser.getSafePwd())) {
                json(L("请先设置资金密码"), false, null, true);
                return;
            }
            RsaUser rsaUser = RsaLoginUtil.getRsaUser(this);
            byte[] decodedData2 = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(capitalPwd), rsaUser.getPriKey());
            capitalPwd = new String(decodedData2);
            if (!new UserDao().checkSecurityPwdNoLock(capitalPwd, loginUser.get_Id())) {
                log.info("理财报警WARN:划转金额转换密码输入错误, userId = " + userId);
                json(L("资金密码输入有误。"), false, null, true);
                return;
            }

            /*查询需要的相关字段*/
            String sql = "";
            /*先检查资金是否有足够的资金*/
            sql = "select profit from pay_user_financial where userid = " + userId + " and fundstype = 51";
            log.info("userAvaTransferAmount sql = " + sql);
            List<BigDecimal> listPayUserFinancial = (List<BigDecimal>) Data.GetOne("vip_main", sql, null);
            BigDecimal userProfitAmount = BigDecimal.ZERO;
            if (null == listPayUserFinancial || listPayUserFinancial.size() < 1) {
                json(L("理财账户初始化失败，请联系客服"), false, null, true);
                log.info("理财报警ERROR:理财账户初始化失败，请联系客服" + userId);
                return;
            } else {
                if (null != listPayUserFinancial.get(0)) {
                    userProfitAmount = listPayUserFinancial.get(0);
                } else {
                    json(L("待划转资金不足，请确认后划转"), false, null, true);
                    log.info("理财报警WARN:划转资金不足, userId = " + userId + ", userProfitAmount = " + userProfitAmount + ", bdAvaTransferAmount = " + bdAvaTransferAmount);
                    return;
                }
                log.info("userProfitAmount = " + userProfitAmount);
                if (userProfitAmount.compareTo(bdAvaTransferAmount) < 0 || bdAvaTransferAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    json(L("待划转资金不足，请确认后划转"), false, null, true);
                    log.info("理财报警WARN:划转资金不足, userId = " + userId + ", userProfitAmount = " + userProfitAmount + ", bdAvaTransferAmount = " + bdAvaTransferAmount);
                    return;
                }
            }
            /**
             * 开启事务处理
             */
            List<OneSql> sqls = new ArrayList<>();
            TransactionObject txObj = new TransactionObject();
            long currentTime = System.currentTimeMillis();
            /*资金处理，扣除5%复投基金*/
            BigDecimal balanceAmount = bdAvaTransferAmount.multiply(BigDecimal.valueOf(0.8));
            BigDecimal douProfitAmount = bdAvaTransferAmount.subtract(balanceAmount);
            String fundsType = "5371";
            String fundsName = "复投基金";
            /*获取vds_usdt实时价格*/
            String vdsUsdtPrice = Cache.Get("vds_usdt_l_price");
            /*修改金额*/
            sql = "update pay_user_financial set balance = balance + " + balanceAmount + " , profit = profit - " + bdAvaTransferAmount + ", "
                    + "reInvestment = reInvestment + " + douProfitAmount + " "
                    + "where userId = " + userId + " and fundsType = 51 and profit >= " + bdAvaTransferAmount + " ";
            log.info("userAvaTransferAmount sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));
            /*记录流水*/
            sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
                    + "remark, vdsUsdtPrice, investProPeriod, matrixLevel) "
                    + "select " + userId + ", userName, " + fundsType + ", " + douProfitAmount + ", "
                    + "" + currentTime + " , (balance + profit + insureInvestFreezeAmount), "
                    + "51, '" + fundsName + "', '', " + vdsUsdtPrice + ", 0, 0 "
                    + "from pay_user_financial where userid = " + userId + " and fundstype = 51 for update";
            log.info("userAvaTransferAmount sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));

            if (userSonRelationFlag) {
                /**
                 * 将子账号资金转入主账号
                 * 子账号划出，记录流水
                 * 主账号接收，记录流水
                 */
                /*子账号先划出*/
                /*子账号 更新pay_user_financial	理财资金表 balance*/
                sql = "update pay_user_financial set balance = balance - " + balanceAmount + " "
                        + "where userid = " + userId + " and fundstype = 51 and balance - " + balanceAmount + " >= 0";
                log.info("userAvaTransferAmount sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_main"));
                /*子账号 插入bill_financial 流水类型 5304 子账号划出 */
                sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
                        + "remark, vdsUsdtPrice, investProPeriod, matrixLevel, businessId) "
                        + "select " + userId + ", '" + userName + "', 5304, " + balanceAmount + ", "
                        + "" + currentTime + ", (balance + profit + insureInvestFreezeAmount), "
                        + "51, '子账号划出', '" + loginUser.getUserName() + "', " + vdsUsdtPrice + ", "
                        + "0, 0, " + loginUser.get_Id() + " "
                        + "from pay_user_financial where userid = " + userId + " and fundstype = 51 for update";
                log.info("userAvaTransferAmount sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_main"));

                /*主账号接收*/
                /*主账号 更新pay_user_financial	理财资金表 balance*/
                sql = "update pay_user_financial set balance = balance + " + balanceAmount + " "
                        + "where userid = " + loginUser.get_Id() + " and fundstype = 51 ";
                log.info("userProductInfoPay pay_user_financial sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_main"));
                /*主账号 插入bill_financial 流水类型 5303 子账号划入 */
                sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
                        + "remark, vdsUsdtPrice, investProPeriod, matrixLevel, businessId) "
                        + "select " + loginUser.get_Id() + ", '" + loginUser.getUserName() + "', 5303, " + balanceAmount + ", "
                        + "" + currentTime + ", (balance + profit + insureInvestFreezeAmount), "
                        + "51, '子账号划入', '" + userName + "', " + vdsUsdtPrice + ", "
                        + "0, 0, " + userId + " "
                        + "from pay_user_financial where userid = " + loginUser.get_Id() + " and fundstype = 51 for update";
                log.info("userAvaTransferAmount sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_main"));

                /*保存到划转表*/
                sql = "insert into fin_usersontransfer (sonUserId, sonUserName, fundsType, avaTransferAmount, douProfitAmount, "
                        + "transferType, transferName, parentUserId, parentUserName, createTime) "
                        + "values (" + userId + ", '" + userName + "', '51', " + avaTransferAmount + ", " + douProfitAmount + ", "
                        + "1, '释放', " + loginUser.get_Id() + ", '" + loginUser.getUserName() + "', now() )";
                log.info("userAvaTransferAmount sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_financial"));
            }

            txObj.excuteUpdateList(sqls);
            if (txObj.commit()) {
                try {
                    ResetProfitThread resetProfitThread = new ResetProfitThread(userId, userName);
                    resetProfitThread.run();
                } catch (Exception e) {
                    log.info("理财报警ERROR:动态收益划转，复投失败" + userId);
                }

//				ResetProfitSinglePool.addRestProfitThread(resetProfitThread);
//	            Cache.Delete("user_financial_" + userId);
            } else {
                json(L("释放失败"), false, null, true);
                return;
            }
            /*保存成功*/
            log.info("理财报警INFO:用户资金释放成功 = " + userId + ", 可释放资金 = " + userProfitAmount + ", 本次释放资金 = " + bdAvaTransferAmount);
            json(L("释放成功"), true, null, true);
        } catch (Exception e) {
            json(L("释放失败"), false, null, true);
            log.info("理财报警ERROR:userAvaTransferAmount", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Page(Viewer = JSON)
    public void userFinCenInfo() {
        initLoginUser();
        try {
            /*调用用户信息*/
            String userId = loginUser.get_Id();
            String userName = loginUser.getUserName();
            /*子账号标志*/
            String sonUserFlag = "false";
            /**
             * 支持子账号切换校验
             */
            String sonUserId = param("sonUserId");
            String sonUserName = param("sonUserName");
            /*校验标志*/
            boolean userSonRelationFlag = userSonRelationCheck(userId, sonUserName, sonUserId);
            if (userSonRelationFlag) {
                /*是子账号，切换数据处理逻辑*/
                userId = sonUserId;
                userName = sonUserName;
                sonUserFlag = "true";
            }

            String userProfit = "0";
            String userProfitUsdt = "0";
            String investAmount = "0";
            String invitationCode = "";
            String pInvitationCode = "";
            String invitationTotalNum = "0";
            /*?invitationCode=*/
            String invitationLinks = "https://www.btcwinex.com/bw/signup";
            String userVID = "";
            /*理财账户VDS可用金额*/
            String userFinVdsAmount = "0";
            String authPayFlag = "0";
            /*邀请人数更新时间，支付时间，支付金额及预期收益金额*/
            String investUsdtAmount = "0";
            String expectProfitUsdt = "0";
            String profitTime = "";
            String modifyTime = "";
            /*协议价格*/
            String investAvergPrice = "1";
            /*邀请人用户名*/
            String pInvitationUserName = "";
            /*超级节点奖励权重*/
            String superNodeWeight = "0";
            /*查询需要的相关字段*/
            String sql = "";
            sql = "select invitationUserName, userVID, invitationCode, pInvitationCode, invitationTotalNum, investAvergPrice, "
            	+ "authPayFlag, UNIX_TIMESTAMP(modifyTime) modifyTime, UNIX_TIMESTAMP(profitTime) profitTime, vipWeight, "
            	+ "(investAmount * investAvergPrice) investUsdtAmount, expectProfitUsdt, investAmount "
            	+ "from fin_userfinancialinfo where userId = " + userId ;
            log.info("userFinCenInfo sql = " + sql);
            UserFinancialInfo userFinancialInfo = (UserFinancialInfo) Data.GetOne("vip_financial", sql, null, UserFinancialInfo.class);
            if (null != userFinancialInfo) {
                if (null != userFinancialInfo.getUserVID()) {
                    userVID = userFinancialInfo.getUserVID();
                }
                if (null != userFinancialInfo.getInvitationCode()) {
                    invitationCode = userFinancialInfo.getInvitationCode();
//					invitationLinks += invitationCode;
                }
                if (null != userFinancialInfo.getpInvitationCode()) {
                    pInvitationCode = userFinancialInfo.getpInvitationCode();
                }
                if (null != userFinancialInfo.getInvitationUserName()) {
                    pInvitationUserName = userFinancialInfo.getInvitationUserName();
                }
                investUsdtAmount = userFinancialInfo.getInvestUsdtAmount().setScale(3, BigDecimal.ROUND_DOWN) + "";
                expectProfitUsdt = userFinancialInfo.getExpectProfitUsdt().setScale(3, BigDecimal.ROUND_DOWN) + "";
                investAmount = userFinancialInfo.getInvestAmount().setScale(0, BigDecimal.ROUND_DOWN) + "";
                investAvergPrice = userFinancialInfo.getInvestAvergPrice().setScale(4, BigDecimal.ROUND_DOWN) + "";
                modifyTime = userFinancialInfo.getModifyTime() * 1000 + "";
                profitTime = userFinancialInfo.getProfitTime() * 1000 + "";
                authPayFlag = userFinancialInfo.getAuthPayFlag() + "";
                superNodeWeight = userFinancialInfo.getVipWeight().setScale(1, BigDecimal.ROUND_DOWN) + "";
            } else {
//				log.info("理财报警ERROR:理财中心没有获取到用户基本信息，非法访问！");
            }
            /*从缓存获取层级人数*/
            invitationTotalNum = RedisUtil.get("financial_invitationTotalNum_" + userId);
            if (StringUtils.isEmpty(invitationTotalNum)) {
                invitationTotalNum = "0";
            }
            /*线上物理上级用户名*/
            String physicsSupName = "";
            sql = "select ffb.username from t_user_node ffa, t_user ffb where ffa.node_id = ffb.id and ffa.user_id = " + userId;
            log.info("userFinCenInfo sql = " + sql);
            List<String> listPhysicsSupName = (List<String>) Data.GetOne("vdsapollo", sql, null);
            if (null != listPhysicsSupName) {
                physicsSupName = listPhysicsSupName.get(0);
            }
            /*直接邀请激活人数*/
            String directInvitationSucNum = "";
            sql = "select count(*) cnt from fin_userfinancialinfo where pInvitationCode = '" + invitationCode + "' and authPayFlag != 1";
            log.info("userFinCenInfo sql = " + sql);
            List<Integer> listDirectInvitationSucNum = (List<Integer>) Data.GetOne("vip_financial", sql, null);
            if (null != listDirectInvitationSucNum) {
                directInvitationSucNum = listDirectInvitationSucNum.get(0) + "";
            }
            /*直接邀请人数*/
            String directInvitationNum = "0";
//            sql = "select count(*) cnt from fin_userfinancialinfo where pInvitationCode = '" + invitationCode + "' ";
//            log.info("userFinCenInfo sql = " + sql);
//            List<Integer> listDirectInvitationNum = (List<Integer>) Data.GetOne("vip_financial", sql, null);
//            if (null != listDirectInvitationNum) {
//                directInvitationNum = listDirectInvitationNum.get(0) + "";
//            }

            /*总投资人数，总投资金额*/
//            BigDecimal intProTotalUser = BigDecimal.ZERO;
//            BigDecimal bdProTotalAmount = BigDecimal.ZERO;
            /*投资人数*/
            String proTotalUser = RedisUtil.get("financial_proTotalUser");
            String proTotalAmount = RedisUtil.get("financial_proTotalAmount");
            String sumInvestUsdtAmount = RedisUtil.get("financial_sumInvestUsdtAmount");
            if (StringUtils.isEmpty(proTotalUser)) {
                proTotalUser = "0";
            }
            if (StringUtils.isEmpty(proTotalAmount)) {
                proTotalAmount = "0";
            }
            if (StringUtils.isEmpty(sumInvestUsdtAmount)) {
                sumInvestUsdtAmount = "0";
            }

//            intProTotalUser = BigDecimal.valueOf(Long.valueOf(proTotalUser));

            /*拼装返回值*/
            Map<String, Object> result = new HashMap<>();
            result.put("userProfit", userProfit);
            result.put("investAmount", investAmount);
            result.put("invitationCode", invitationCode);
            result.put("pInvitationCode", pInvitationCode);
            result.put("invitationTotalNum", invitationTotalNum);
            result.put("invitationLinks", invitationLinks);
            result.put("userVID", userVID);
            result.put("userName", userName);
            result.put("userFinVdsAmount", userFinVdsAmount);
            result.put("authPayFlag", authPayFlag);
            result.put("investUsdtAmount", investUsdtAmount);
            result.put("expectProfitUsdt", expectProfitUsdt);
            result.put("profitTime", profitTime);
            result.put("modifyTime", modifyTime);
            result.put("userProfitUsdt", userProfitUsdt);
            /*邀请人用户名*/
            result.put("pInvitationUserName", pInvitationUserName);
            /*线上物理上级用户名*/
            result.put("physicsSupName", physicsSupName);
            /*直接邀请激活人数*/
            result.put("directInvitationSucNum", directInvitationSucNum);
            /*直接邀请人数*/
            result.put("directInvitationNum", directInvitationNum);
            /*总投资人数，总投资金额*/
            result.put("proTotalAmount", proTotalAmount);
            result.put("proTotalUser", proTotalUser);
            result.put("sumInvestUsdtAmount", sumInvestUsdtAmount);
            /*协议价*/
            result.put("investAvergPrice", investAvergPrice);
            /*超级节点奖励权重*/
            result.put("superNodeWeight", superNodeWeight);
            /*子账号标志*/
            result.put("sonUserFlag", sonUserFlag);

            log.info("result = " + JSONObject.toJSONString(result));
            json("ok", true, JSONObject.toJSONString(result), true);
        } catch (Exception e) {
            json("ok", false, null, true);
            log.info("理财报警ERROR:userFinCenInfo", e);
        }
    }

    @Page(Viewer = JSON)
    public void userFinProfitBill() {
        initLoginUser();
        try {
            /*调用用户信息*/
            String userId = loginUser.get_Id();
            /**
             * 支持子账号切换校验
             */
            String sonUserId = param("sonUserId");
            String sonUserName = param("sonUserName");
            /*校验标志*/
            boolean userSonRelationFlag = userSonRelationCheck(userId, sonUserName, sonUserId);
            if (userSonRelationFlag) {
                /*是子账号，切换数据处理逻辑*/
                userId = sonUserId;
//                userName = sonUserName;
            }
            String sql = "";
            sql = "select investTime, investAmount, investProPeriod, vdsUsdtPrice, investUsdtAmount, expectProfitUsdt, doubleThrowFlag, "
                    + "vipWeight from fin_productinvest where userId = " + userId + " and fundsType = 51 order by id desc ";
            log.info("fin_productinvest sql = " + sql);
            List<Bean> listFinProductInvest = (List<Bean>) Data.Query("vip_financial", sql, null, FinProductInvest.class);
            List<Map<String, String>> resutList = new ArrayList<Map<String, String>>();
            Map<String, String> resultMap = new HashMap<String, String>();
            /*定义循环变量*/
            BigDecimal amount = BigDecimal.ZERO;
            BigDecimal vdsUsdtPrice = BigDecimal.ZERO;
            BigDecimal vipWeight = BigDecimal.ZERO;
            BigDecimal investUsdtAmount = BigDecimal.ZERO;
            BigDecimal expectProfitUsdt = BigDecimal.ZERO;
            String profitType = "首投";
//			/*投资期次*/
//			String investProPeriod = "1";
            /*自动复投标志，0 首投，1 增投，2释放冻结资金触发 自动复投, 3 手动复投*/
            int doubleThrowFlag = 0;
            if (null == listFinProductInvest || listFinProductInvest.size() < 1) {
                json("ok", true, JSONObject.toJSONString(resutList), true);
                return;
            } else {
                for (int i = 0; i < listFinProductInvest.size(); i++) {
                    FinProductInvest finProductInvest = (FinProductInvest) listFinProductInvest.get(i);
                    resultMap = new HashMap<String, String>();
                    /*重新赋值*/
                    resultMap.put("createTime", finProductInvest.getInvestTime().getTime() + "");
//					if (!StringUtils.isEmpty(billDetails.getStrType())) {
//						resultMap.put("typeName", L(billDetails.getStrType()));
//					}
                    doubleThrowFlag = finProductInvest.getDoubleThrowFlag();
                    if (0 == doubleThrowFlag) {
                        profitType = "首投";
                    } else if (1 == doubleThrowFlag) {
                        profitType = "增投";
                    } else if (2 == doubleThrowFlag) {
                        profitType = "自动复投";
                    } else if (3 == doubleThrowFlag) {
                        profitType = "手动复投";
                    }

                    amount = finProductInvest.getInvestAmount().setScale(2, BigDecimal.ROUND_DOWN);
                    vdsUsdtPrice = finProductInvest.getVdsUsdtPrice().setScale(4, BigDecimal.ROUND_DOWN);
                    vipWeight = finProductInvest.getVipWeight().setScale(2, BigDecimal.ROUND_DOWN);
                    investUsdtAmount = finProductInvest.getInvestUsdtAmount().setScale(4, BigDecimal.ROUND_DOWN);
                    expectProfitUsdt = finProductInvest.getExpectProfitUsdt().setScale(4, BigDecimal.ROUND_DOWN);
                    resultMap.put("fundsTypeName", "Vollar");
                    resultMap.put("amount", amount + "");
                    resultMap.put("vdsUsdtPrice", vdsUsdtPrice + "");
                    resultMap.put("usdtAmount", investUsdtAmount + "");
                    resultMap.put("expectProfitUsdt", expectProfitUsdt + "");
//					if (!StringUtils.isEmpty(billDetails.getRemark())) {
//						resultMap.put("remark", L(billDetails.getRemark()));
//					}
                    resultMap.put("vipWeight", vipWeight + "");
                    resultMap.put("profitType", L(profitType));
                    resutList.add(resultMap);
                }
            }
//			log.info(JSONObject.toJSONString(resutList));
            json("ok", true, JSONObject.toJSONString(resutList), true);
        } catch (Exception e) {
            json("ok", false, null, true);
            log.info("理财报警ERROR:userFinProfitBill", e);
        }
    }

    @Page(Viewer = JSON)
    public void userFinVdsBalance() {
        initLoginUser();
        try {
            /*调用用户信息*/
            String userId = loginUser.get_Id();
            /**
             * 支持子账号切换校验
             */
            String sonUserId = param("sonUserId");
            String sonUserName = param("sonUserName");
            /*校验标志*/
            boolean userSonRelationFlag = userSonRelationCheck(userId, sonUserName, sonUserId);
            if (userSonRelationFlag) {
                /*是子账号，切换数据处理逻辑*/
                userId = sonUserId;
//                userName = sonUserName;
            }

            /*获取用户理财账户可用VDS*/
            String userFinVdsAmount = "0";
            /*已获收益*/
            String avaTotalProfitAmount = "0";
            /*待划转*/
            String avaTransferAmount = "0";
            /*复投资金*/
            String douProfitAmount = "0";
            /*balance, profit, profitUsdt, reInvestment, reInvestmentUsdt, realizedPnl, realizedPnlUsdt, staticProfitSum, staticProfitSumUsdt*/
            /*获取vds_usdt实时价格*/
            String vdsUsdtPrice = Cache.Get("vds_usdt_l_price");
            if (StringUtils.isEmpty(vdsUsdtPrice)) {
                vdsUsdtPrice = "0";
            } else {
                vdsUsdtPrice = new BigDecimal(vdsUsdtPrice).setScale(4, BigDecimal.ROUND_DOWN) + "";
            }
            /*超级节点分红奖励*/
            String superNodeAmount = "0";
            String superNodeAmountUsdt = "0";
            /*生态体系参与奖励*/
            String ecologySystemAmount = "0";
            String ecologySystemAmountUsdt = "0";
            /*新增VIP增值奖励*/
            String newVipUserAmount = "0";
            String newVipUserAmountUsdt = "0";
            /*静态收益累积*/
            String staticProfitSum = "0";
            String staticProfitSumUsdt = "0";
            /**
             * curstaticProfit			当前静态收益每次复投清空
             * curstaticProfitUsdt		当前静态收益折算USDT每次复投清空
             */
            String curstaticProfit = "0";
            String curstaticProfitUsdt = "0";
            /*USDT可提取额度*/
            String userFinUsdtAmount = "0";
            try {
//				Cache.Delete("user_financial_" + userId);
                FeignContainer feignContainer = new FeignContainer(ApiConfig.getValue("usecenter.url") + "/payUser");
                PayUserApiService payUserApi = feignContainer.getFeignClient(PayUserApiService.class);
                String strVDSFinancial = payUserApi.getFinancialDetail(userId);
                JSONObject jsonResult = com.alibaba.fastjson.JSONObject.parseObject(strVDSFinancial);
                log.info("jsonResult = " + jsonResult);
                if (null != jsonResult) {
                    JSONObject dataVDS = (JSONObject) jsonResult.get("VDS");
                    JSONObject dataUSDT = (JSONObject) jsonResult.get("USDT");
                    if (null != dataUSDT) {
                        if (!StringUtils.isEmpty(dataUSDT.getString("balance"))) {
                            userFinUsdtAmount = new BigDecimal(dataUSDT.getString("balance")).setScale(3, BigDecimal.ROUND_DOWN) + "";
                        }
                    }
                    if (null != dataVDS) {
                        if (!StringUtils.isEmpty(dataVDS.getString("balance"))) {
                            userFinVdsAmount = new BigDecimal(dataVDS.getString("balance")).setScale(3, BigDecimal.ROUND_DOWN) + "";
                        }
                        if (!StringUtils.isEmpty(dataVDS.getString("realizedPnl"))) {
                            avaTotalProfitAmount = new BigDecimal(dataVDS.getString("realizedPnl")).setScale(3, BigDecimal.ROUND_DOWN) + "";
                        }
                        if (!StringUtils.isEmpty(dataVDS.getString("profit"))) {
                            avaTransferAmount = new BigDecimal(dataVDS.getString("profit")).setScale(3, BigDecimal.ROUND_DOWN) + "";
                        }
                        if (!StringUtils.isEmpty(dataVDS.getString("reInvestment"))) {
                            douProfitAmount = new BigDecimal(dataVDS.getString("reInvestment")).setScale(3, BigDecimal.ROUND_DOWN) + "";
                        }

                        /*超级节点分红奖励*/
                        if (!StringUtils.isEmpty(dataVDS.getString("superNodeAmount"))) {
                            superNodeAmount = new BigDecimal(dataVDS.getString("superNodeAmount")).setScale(3, BigDecimal.ROUND_DOWN) + "";
                        }
                        if (!StringUtils.isEmpty(dataVDS.getString("superNodeAmountUsdt"))) {
                            superNodeAmountUsdt = new BigDecimal(dataVDS.getString("superNodeAmountUsdt")).setScale(3, BigDecimal.ROUND_DOWN) + "";
                        }
                        /*生态体系参与奖励*/
                        if (!StringUtils.isEmpty(dataVDS.getString("ecologySystemAmount"))) {
                            ecologySystemAmount = new BigDecimal(dataVDS.getString("ecologySystemAmount")).setScale(3, BigDecimal.ROUND_DOWN) + "";
                        }
                        if (!StringUtils.isEmpty(dataVDS.getString("ecologySystemAmountUsdt"))) {
                            ecologySystemAmountUsdt = new BigDecimal(dataVDS.getString("ecologySystemAmountUsdt")).setScale(3, BigDecimal.ROUND_DOWN) + "";
                        }
                        /*新增VIP增值奖励*/
                        if (!StringUtils.isEmpty(dataVDS.getString("newVipUserAmount"))) {
                            newVipUserAmount = new BigDecimal(dataVDS.getString("newVipUserAmount")).setScale(3, BigDecimal.ROUND_DOWN) + "";
                        }
                        if (!StringUtils.isEmpty(dataVDS.getString("newVipUserAmountUsdt"))) {
                            newVipUserAmountUsdt = new BigDecimal(dataVDS.getString("newVipUserAmountUsdt")).setScale(3, BigDecimal.ROUND_DOWN) + "";
                        }
                        /*静态收益累积*/
                        if (!StringUtils.isEmpty(dataVDS.getString("staticProfitSum"))) {
                            staticProfitSum = new BigDecimal(dataVDS.getString("staticProfitSum")).setScale(3, BigDecimal.ROUND_DOWN) + "";
                        }
                        if (!StringUtils.isEmpty(dataVDS.getString("staticProfitSumUsdt"))) {
                            staticProfitSumUsdt = new BigDecimal(dataVDS.getString("staticProfitSumUsdt")).setScale(3, BigDecimal.ROUND_DOWN) + "";
                        }
                        /**
                         * curstaticProfit			当前静态收益每次复投清空
                         * curstaticProfitUsdt		当前静态收益折算USDT每次复投清空
                         */
                        if (!StringUtils.isEmpty(dataVDS.getString("curstaticProfit"))) {
                            curstaticProfit = new BigDecimal(dataVDS.getString("curstaticProfit")).setScale(3, BigDecimal.ROUND_DOWN) + "";
                        }
                        if (!StringUtils.isEmpty(dataVDS.getString("curstaticProfitUsdt"))) {
                            curstaticProfitUsdt = new BigDecimal(dataVDS.getString("curstaticProfitUsdt")).setScale(3, BigDecimal.ROUND_DOWN) + "";
                        }
                    }
                }
            } catch (Exception e) {
                log.info("理财报警WARN:getFinancialDetail获取理财账户VDS余额异常" + userId);
            }

            /*拼装返回值*/
            Map<String, Object> result = new HashMap<>();
            result.put("userFinVdsAmount", userFinVdsAmount);
            result.put("avaTotalProfitAmount", avaTotalProfitAmount);
            result.put("avaTransferAmount", avaTransferAmount);
            result.put("douProfitAmount", douProfitAmount);
            result.put("vdsUsdtPrice", vdsUsdtPrice);
            /*超级节点*/
            result.put("superNodeAmount", superNodeAmount);
            result.put("superNodeAmountUsdt", superNodeAmountUsdt);
            /*生态体系参与奖励*/
            result.put("ecologySystemAmount", ecologySystemAmount);
            result.put("ecologySystemAmountUsdt", ecologySystemAmountUsdt);
            /*新增VIP增值奖励*/
            result.put("newVipUserAmount", newVipUserAmount);
            result.put("newVipUserAmountUsdt", newVipUserAmountUsdt);
            /*静态收益累积*/
            result.put("staticProfitSum", staticProfitSum);
            result.put("staticProfitSumUsdt", staticProfitSumUsdt);
            /**
             * curstaticProfit			当前静态收益每次复投清空
             * curstaticProfitUsdt		当前静态收益折算USDT每次复投清空
             */
            result.put("curstaticProfit", curstaticProfit);
            result.put("curstaticProfitUsdt", curstaticProfitUsdt);
            /*USDT可提取额度*/
            result.put("userFinUsdtAmount", userFinUsdtAmount);

            log.info("result = " + JSONObject.toJSONString(result));
            /*接口返回*/
            json("ok", true, JSONObject.toJSONString(result), true);
        } catch (Exception e) {
            json("ok", false, null, true);
            log.info("理财报警ERROR:userFinVdsBalance获取用户理财VDS约异常", e);
        }
    }

    @SuppressWarnings({"unchecked", "unused"})
    @Page(Viewer = JSON)
    public void userRepearInvestment() {
        initLoginUser();
        try {
            /*调用用户信息*/
            String userId = loginUser.get_Id();
            /*进行复投*/
            String sql = "";
            /**
             * 先判断产品是否正常开启
             */
            /*获取产品状态标志，先从获取,存放时间10秒*/
            String proState = RedisUtil.get("financial_proState");
            String proAmount = RedisUtil.get("financial_proAmount");
            BigDecimal bdProAmount = BigDecimal.valueOf(Long.parseLong(proAmount));
            log.info("proState = " + proState + ", proAmount = " + proAmount + ", bdProAmount = " + bdProAmount);
//            Map<String, String> productMap = new HashMap<String, String>();
//            if (StringUtils.isEmpty(proState) || StringUtils.isEmpty(proAmount)) {
//				/*如果非空字符串，而且不是1，表示暂未开启或者已关闭状态*/
//				/*重新冲数据库获取*/
//                productMap = resetProductRedis();
//                proState = (String) productMap.get("proState");
//                proAmount = (String) productMap.get("proAmount");
//            }
            bdProAmount = new BigDecimal(proAmount);
            log.info("proState = " + proState + ", proAmount = " + proAmount + ", bdProAmount = " + bdProAmount);
            if (!"1".equals(proState)) {
                /*该产品理财投资已结束！*/
                json(L("该产品理财投资已结束"), false, null, true);
                log.info("理财报警WARN:该产品理财投资已结束");
                return;
            }
            if (bdProAmount.compareTo(new BigDecimal(188)) < 0) {
                log.info("理财报警ERROR:产品投资价格小于188");
                json(L("系统繁忙，请稍后再试"), false, null, true);
                return;
            }

            /*预期的1.5倍投资*/
            BigDecimal bdExpectProfitUsdt = BigDecimal.ZERO;
            /*当前期次*/
            String investProPeriod = "";
            sql = "select expectProfitUsdt, investProPeriod from fin_productinvest where userId = " + userId + " order by id desc";
            log.info("userRepearInvestment = " + sql);
            List<Object> listExpectProfitUsdt = (List<Object>) Data.GetOne("vip_financial", sql, null);
            if (null != listExpectProfitUsdt && listExpectProfitUsdt.size() > 0) {
                if (null != listExpectProfitUsdt.get(0)) {
                    /*.setScale(2, BigDecimal.ROUND_DOWN)*/
                    bdExpectProfitUsdt = (BigDecimal) listExpectProfitUsdt.get(0);
                    investProPeriod = (String) listExpectProfitUsdt.get(1);
                }
            }

            /*本期次已获得的总收益*/
            BigDecimal bdUserProfitTotalUsdt = BigDecimal.ZERO;
            /*该期次获得的收益*/
            sql = "";
            log.info("userRepearInvestment = " + sql);
            List<BigDecimal> listUserProfitTotalUsdt = (List<BigDecimal>) Data.GetOne("vip_main", sql, null);
            if (null != listUserProfitTotalUsdt && listUserProfitTotalUsdt.size() > 0) {
                if (null != listUserProfitTotalUsdt.get(0)) {
                    bdUserProfitTotalUsdt = listUserProfitTotalUsdt.get(0);
                }
            }
            /*累积收益大于等于预期收益，可以进行复投*/
            if (bdExpectProfitUsdt.compareTo(bdUserProfitTotalUsdt) <= 0) {

            } else {
                log.info("理财报警WARN:用户【" + userId + "】, 尚未达到复投要求：累积收益 = " + bdUserProfitTotalUsdt + ", 预期收益 = " + bdExpectProfitUsdt);
                json(L("尚未达到复投要求"), false, null, true);
                return;
            }

            /**
             * 进行复投处理
             * 1、
             * 2、
             * 3、
             * 、、、
             */


            List<Map<String, String>> resutList = new ArrayList<Map<String, String>>();
            json("ok", true, JSONObject.toJSONString(resutList), true);
        } catch (Exception e) {
            json("ok", false, null, true);
            log.info("理财报警ERROR:userFinProfitBill", e);
        }
    }

    @Page(Viewer = JSON)
    public void userInvitationChart() {
        initLoginUser();
        try {
            /*调用用户信息*/
            String userName = param("username");
            String currentUserName = loginUser.getUserName();
            String userId = loginUser.get_Id();
            /**
             * 支持子账号切换校验
             */
            String sonUserId = param("sonUserId");
            String sonUserName = param("sonUserName");
            /*校验标志*/
            boolean userSonRelationFlag = userSonRelationCheck(userId, sonUserName, sonUserId);
            if (userSonRelationFlag) {
                /*是子账号，切换数据处理逻辑*/
                userId = sonUserId;
                currentUserName = sonUserName;
            }

            if (StringUtils.isEmpty(userName)) {
                userName = loginUser.getUserName();
            }
//			Map<String, Object> objectMap = new HashMap<String, Object>();
            /*从vip_financial库，userfinancialinfo 表获取*/
            /*http://192.168.2.83:8888/getOrgChart?username=11a@qq.com*/
            String urlFinancial = ApiConfig.getValue("urlfinancial.url");
            urlFinancial += "/getOrgChart?username=" + userName + "&currentUserName=" + currentUserName;
            log.info("urlFinancial = " + urlFinancial);
            String resultInterface = "";
            /*接口返回码和返回消息*/
            resultInterface = doPostData(urlFinancial, null);
//			JSONObject jsonResultInterface = com.alibaba.fastjson.JSONObject.parseObject(resultInterface);
//			log.info("jsonResultInterface = " + resultInterface);
//			response.
            /*Response.append(jsonp(response));*/
            json("ok", true, resultInterface, true);
        } catch (Exception e) {
            json("ok", false, null, true);
            log.info("理财报警ERROR:userFinProfitBill", e);
        }
    }

    /**
     * 复投资金转入超级节点记录
     */
    @Page(Viewer = JSON)
    public void doubleThrowToSuperNode() {
        initLoginUser();
        try {
            int currentPage = intParam("pageIndex");
            //当前页数
            if (currentPage <= 0) {
                currentPage = 0;
            }
            Query<FinDouProfitLog> query = finDouProfitLogDao.getQuery();
            query.setSql("select id,userName,doubleThrowAmount,toSuperNodeAmount,creatTime,batchNo from fin_dou_profit_log order by creatTime desc");

            query.setDatabase("vip_financial");
            query.setCls(FinDouProfitLog.class);
            query.setParams(new Object[]{});
            int total = query.count();
            List<FinDouProfitLog> finDouProfitLogs = new ArrayList<>();
            BigDecimal lossAmountSum = BigDecimal.ZERO;
            if (total > 0) {
                //分页查询
                finDouProfitLogs = query.getPageList(currentPage, 50);
                int batchNo = 0;
                String dealflagName = "";
                for (FinDouProfitLog finDouProfitLog : finDouProfitLogs) {
                    BigDecimal lossAmount = finDouProfitLog.getDoubleThrowAmount().add(finDouProfitLog.getToSuperNodeAmount()).setScale(4, BigDecimal.ROUND_DOWN);
                    BigDecimal releaseAmount = lossAmount.multiply(new BigDecimal("5")).setScale(4, BigDecimal.ROUND_DOWN);
                    lossAmountSum = lossAmountSum.add(lossAmount);
                    finDouProfitLog.setLossAmount(lossAmount);
                    finDouProfitLog.setReleaseAmount(releaseAmount);
                    String userName = finDouProfitLog.getUserName();
                    String userNameBefor = userName.substring(0, userName.indexOf("@"));
                    if (userNameBefor.length() > 2) {
                        userNameBefor = userNameBefor.substring(0, 2) + "****";
                        userName = userNameBefor + userName.substring(userName.indexOf("@"));
                    }
                    finDouProfitLog.setUserName(userName);
                    /**
                     * batchNo 如果是0 未发放，大于0 已发放
                     */
                    batchNo = finDouProfitLog.getBatchNo();
                    if (0 == batchNo) {
                        dealflagName = L("冻结中");
                    } else if (batchNo > 0) {
                        dealflagName = L("已释放");
                    }
                    finDouProfitLog.setDealflagName(dealflagName);
                }
            }
            /*平台累积释放 platReleasePayAmount*/
            String platReleasePayAmount = RedisUtil.get("financial_platReleasePayAmount");
            if (StringUtils.isEmpty(platReleasePayAmount)) {
                platReleasePayAmount = "0";
            }
            Map<String, Object> page = new HashMap<String, Object>();
            page.put("platReleasePayAmount", platReleasePayAmount);
            page.put("pageIndex", currentPage);
            page.put("totalCount", total);
            page.put("lossAmountSum", lossAmountSum.setScale(4, BigDecimal.ROUND_DOWN));
            page.put("list", finDouProfitLogs);
            json("ok", true, JSONObject.toJSONString(page));
        } catch (Exception e) {
            log.info("获取超级节点信息失败", e);
        }

    }

    @Page(Viewer = JSON)
    public void userInvitationNum() {
        initLoginUser();
        try {
            String userId = loginUser.get_Id();
            String invitationTotalNum = RedisUtil.get("financial_invitationTotalNum_" + userId);
            if (StringUtils.isEmpty(invitationTotalNum)) {
                invitationTotalNum = "0";
            }
            /**
             * {"totalNum":"1"}
             */
            String resultInterface = "{\"totalNum\":\"" + invitationTotalNum + "\"}";

            json("ok", true, resultInterface, true);
        } catch (Exception e) {
            json("ok", false, null, true);
            log.info("理财报警ERROR:userFinProfitBill", e);
        }
    }

    public String doPostData(String url, Map<String, Object> objectMap) throws Exception {
        String result = "";
        try {
            String dataJson = com.alibaba.fastjson.JSON.toJSONString(objectMap);
            jodd.http.HttpRequest request = jodd.http.HttpRequest.post(url);
            request.query("data", dataJson);
            jodd.http.HttpResponse response = request.send();
            result = response.bodyText();
        } catch (Exception e) {
            throw new Exception(e);
        }
        return result;
    }

//    public Map<String, String> resetProductRedis() {
//		/*返回值*/
//        Map<String, String> productMap = new HashMap<String, String>();
//		/*重新冲数据库获取*/
//        String sql = "";
//        sql = "select proState, proAmount from fin_product order by id asc";
//        log.info("resetProductRedis = " + sql);
//        FinancialProduct financialProduct = (FinancialProduct) Data.GetOne("vip_financial", sql, null, FinancialProduct.class);
////		if (null != financialProduct) {
////		}
//		/*获取产品状态标志，先从Redis获取,存放时间10秒*/
//        String proState = "";
//        BigDecimal bdProAmount = BigDecimal.ZERO;
//        proState = financialProduct.getProState() + "";
//        bdProAmount = financialProduct.getProAmount();
//        RedisUtil.set("financial_proState", proState, 10);
//        RedisUtil.set("financial_proAmount", bdProAmount + "", 10);
//        productMap.put("proAmount", bdProAmount + "");
//        productMap.put("proState", proState);
//        return productMap;
//    }

    public void userRegister() {
        /*调用接口进行数据对接保存*/
        boolean saveFlag = true;
        try {
            /*调用接口进行数据对接保存 doPostData*/
            Map<String, Object> objectMap = new HashMap<String, Object>();
            /*从vip_financial库，userfinancialinfo 表获取*/
//			objectMap.put("userId", userId);
//			objectMap.put("username", userName);
//			objectMap.put("address", userVID);
//			objectMap.put("recUser", invitationUserName);
            log.info("objectMap = " + objectMap);
            String urlFinancial = ApiConfig.getValue("urlfinancial.url");
            urlFinancial += "/vdsapollo/op/register";
            log.info("urlFinancial = " + urlFinancial);
            String resultInterface = "";
            /*接口返回码和返回消息*/
            String resultInterfaceCode = "";
            String resultInterfaceMsg = "";
            resultInterface = doPostData(urlFinancial, objectMap);
            JSONObject jsonResultInterface = com.alibaba.fastjson.JSONObject.parseObject(resultInterface);
            log.info("jsonResultInterface = " + jsonResultInterface);
            if (null != jsonResultInterface) {
                if (null != jsonResultInterface.getString("code")) {
                    resultInterfaceCode = jsonResultInterface.getString("code");
                }
                if (null != jsonResultInterface.getString("message")) {
                    resultInterfaceMsg = jsonResultInterface.getString("message");
                }
                log.info("resultInterfaceCode = " + resultInterfaceCode);
                if (!"200".equals(resultInterfaceCode)) {
                    if ("400".equals(resultInterfaceCode)) {
                        if (!"用户已存在".equals(resultInterfaceMsg)) {
                            saveFlag = false;
                        }
                    } else {
                        saveFlag = false;
                    }
                }
            } else {
                saveFlag = false;
            }
        } catch (Exception e) {
            saveFlag = false;
        }
        if (saveFlag) {
            /*保存成功*/
        } else {
            /*保存失败*/
        }
    }

    public void userActivate() {
        /*调用接口进行数据对接激活*/
        boolean activateFlag = true;
        try {
            /*调用接口进行数据对接保存 doPostData*/
            Map<String, Object> objectMap = new HashMap<String, Object>();
//			objectMap.put("userId", userId);
//			objectMap.put("level", 6);
//			objectMap.put("levelNum", 188);
//			objectMap.put("vdsPrice", bdVdsUsdtPrice);
//			objectMap.put("expectProfit", yyyUsdt);
            log.info("objectMap = " + objectMap);
            String urlFinancial = ApiConfig.getValue("urlfinancial.url");
            urlFinancial += "/vdsapollo/op/activate";
            log.info("urlFinancial = " + urlFinancial);
            String resultInterface = "";
            /*接口返回码和返回消息*/
            String resultInterfaceCode = "";
            String resultInterfaceMsg = "";
            resultInterface = doPostData(urlFinancial, objectMap);
            JSONObject jsonResultInterface = com.alibaba.fastjson.JSONObject.parseObject(resultInterface);
            log.info("jsonResultInterface = " + jsonResultInterface);
            if (null != jsonResultInterface) {
                if (null != jsonResultInterface.getString("code")) {
                    resultInterfaceCode = jsonResultInterface.getString("code");
                }
                if (null != jsonResultInterface.getString("message")) {
                    resultInterfaceMsg = jsonResultInterface.getString("message");
                }
                log.info("resultInterfaceCode = " + resultInterfaceCode);
                if (!"200".equals(resultInterfaceCode)) {
                    if ("400".equals(resultInterfaceCode)) {
                        if (!"用户已激活".equals(resultInterfaceMsg)) {
                            activateFlag = false;
                        }
                    } else {
                        activateFlag = false;
                    }
                }
            } else {
                activateFlag = false;
            }
        } catch (Exception e) {
            activateFlag = false;
        }
        if (activateFlag) {
            /*保存成功*/
        } else {
            /*保存失败*/
        }
    }

//	public void transferToInvitationUser() {
//		try {
//			String sql = "";
//			sql = "select id, payUserId, payUserName, acceptUserId, acceptUserName, dealState from fin_invitationuserpay where dealState = 0 ";
//			log.info("TransferToInvitationUser sql = " + sql);
//			List<Bean> listInvitationUserPay = (List<Bean>) Data.Query("vip_financial", sql, null, InvitationUserPay.class);
//			log.info("listInvitationUserPay.size = " + listInvitationUserPay.size());
//			/*开启事务*/
//			List<OneSql> sqls = new ArrayList<>();
//	        TransactionObject txObj = new TransactionObject();
//	        /*字段*/
//	        int id = 0;
//	        String payUserId = "";
//	        String payUserName = "";
//	        String acceptUserId = "";
//	        String acceptUserName = "";
//	        long currentTime = System.currentTimeMillis();
//	        /*转出和转给拼装*/
//	        String payRemark = "";
//	        String acceptRemark = "";
//	        InvitationUserPay invitationUserPay = null;
//			if (null != listInvitationUserPay && listInvitationUserPay.size() > 0) {
//				for (int i = 0; i < listInvitationUserPay.size(); i++) {
//					/**
//					 * 1、每次处理1条。每次使用1个事务。
//					 */
//					/*重置*/
//					sqls = new ArrayList<>();
//					txObj = new TransactionObject();
//					invitationUserPay = (InvitationUserPay) listInvitationUserPay.get(i);
//					/*赋予新值*/
//					id = invitationUserPay.getId();
//					payUserId = invitationUserPay.getPayUserId() + "";
//					payUserName = invitationUserPay.getPayUserName();
//					acceptUserId = invitationUserPay.getAcceptUserId() + "";
//					acceptUserName = invitationUserPay.getAcceptUserName();
//					currentTime = System.currentTimeMillis();
//					/*转出和转给拼装*/
//					payRemark = "邀请" + acceptUserName + "转出";
//					acceptRemark = payUserName + "邀请转入";
//					/**
//					 * 开始处理：将邀请人的资金划转给需要划转的人
//					 * 1、将划出人的资金减少。
//					 * 2、将接收人的资金增加。
//					 */
//					/*1、转出 pay_user_financial表扣减balance*/
//					sql = "update pay_user_financial set balance = balance - 188 "
//						+ "where userid = " + payUserId + " and fundstype = 51 and balance - 188 >= 0";
//					log.info("TransferToInvitationUser sql = " + sql);
//					sqls.add(new OneSql(sql, 1, null, "vip_main"));
//					/*2、转出 插入bill_financial 邀请XXX转出 5303 支出*/
//					sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, remark) "
//						+ "select " + payUserId + ", '" + payUserName + "', 5303, 188, " + currentTime + ", (balance + freez), 51, '邀请转出', "
//						+ "'" + payRemark + "' from pay_user_financial where userid = " + payUserId + " and fundstype = 51 for update";
//					log.info("userProductInfoPay bill_financial sql = " + sql);
//					sqls.add(new OneSql(sql, 1, null, "vip_main"));
//					/*3、转入 pay_user_financial表增加balance*/
//					sql = "update pay_user_financial set balance = balance + 188 "
//						+ "where userid = " + acceptUserId + " and fundstype = 51";
//					log.info("TransferToInvitationUser sql = " + sql);
//					sqls.add(new OneSql(sql, 1, null, "vip_main"));
//					/*4、转入 插入bill_financial XXX邀请转入 5304 支出*/
//					sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, remark) "
//						+ "select " + acceptUserId + ", '" + acceptUserName + "', 5304, 188, " + currentTime + ", (balance + freez), 51, '邀请转入', "
//						+ "'" + acceptRemark + "' from pay_user_financial where userid = " + acceptUserId + " and fundstype = 51 for update";
//					log.info("userProductInfoPay bill_financial sql = " + sql);
//					sqls.add(new OneSql(sql, 1, null, "vip_main"));
//					txObj.excuteUpdateList(sqls);
//		            if (txObj.commit()) {
//		            	sql = "update fin_invitationuserpay set dealState = 1, dealTime = now() where id = " + id;
//		            	log.info("TransferToInvitationUser sql = " + sql);
//		            	Data.Update("vip_financial", sql, null);
//		            	log.info("执行成功 = " + id);
//		            } else {
//		            	sql = "update fin_invitationuserpay set dealState = 2, dealTime = now() where id = " + id;
//		            	log.info("TransferToInvitationUser sql = " + sql);
//		            	Data.Update("vip_financial", sql, null);
//		            	log.info("执行失败 = " + id);
//		            	break;
//		            }
//				}
//			} else {
//				/*没有需要处理的数据*/
//			}
//		} catch (Exception e) {
//			log.info("理财报警ERROR:TransferToInvitationUser", e);
//		}
//	}

    @Page(Viewer = JSON)
    public void userHierarchyInfo() {
        initLoginUser();
        int total = 0;
        List<UserInviteRela> userInviteRelaArrayList = new ArrayList<>();
        try {
            //int currentPage = 1;
            int currentPage = intParam("pageIndex");
            if (currentPage <= 0) {
                currentPage = 1;
            }
            //String userId = "1003614";
            String userId = loginUser.get_Id();
            String sql = "select hierarchyLevel,hierarchyTotalNum,userActiveNum,userEmptyNum,userNoActiveNum from t_user_lnvite_rela "
                    + "where user_id = " + Long.valueOf(userId);
            Query<UserInviteRela> queryTotal = userInviteRelaDao.getQuery();
            queryTotal.setSql(sql);
            queryTotal.setDatabase("vdsapollo");
            queryTotal.setCls(UserInviteRela.class);
            total = queryTotal.count();
            if (total > 0) {
                userInviteRelaArrayList = queryTotal.getPageList(currentPage, 10);
                Map<String, Object> page = new HashMap<String, Object>();
                page.put("pageIndex", currentPage);
                page.put("totalCount", total);
                page.put("list", userInviteRelaArrayList);
                json("ok", true, JSONObject.toJSONString(page));
                return;
            }
        } catch (Exception e) {
            log.info("获取用户层级占位信息失败", e);
        }
        Map<String, Object> page = new HashMap<String, Object>();
        page.put("pageIndex", 1);
        page.put("totalCount", 0);
        page.put("totalWeight", BigDecimal.ZERO);
        page.put("list", new ArrayList<UserInviteRela>());
        json("ok", false, JSONObject.toJSONString(page));
    }


    @Page(Viewer = JSON)
    public void getBonusList() {
        initLoginUser();
        try {
            String mark = "回馈VID地址：";
            int currentPage = intParam("pageIndex");
            if (currentPage <= 0) {
                currentPage = 1;
            }
            String userId = loginUser.get_Id();
            /**
             * 支持子账号切换校验
             */
            String sonUserId = param("sonUserId");
            String sonUserName = param("sonUserName");
            /*校验标志*/
            boolean userSonRelationFlag = userSonRelationCheck(userId, sonUserName, sonUserId);
            if (userSonRelationFlag) {
                /*是子账号，切换数据处理逻辑*/
                userId = sonUserId;
//                userName = sonUserName;
            }
            Integer bonusType = intParam("bonusType");  //类型
            String startTime = param("startDate");
            String endTime = param("endDate");
            if (bonusType == null || bonusType.equals(0)) {
                bonusType = 1;
            }
            int total = 0;
            BigDecimal sum_bonus_usdt_amount = BigDecimal.ZERO;
            BigDecimal sum_bonus_vds_amount = BigDecimal.ZERO;
            BigDecimal financial_profitWeightTotal = BigDecimal.ZERO;
            List<FinancialBonus> financialBonusList = new ArrayList<>();
            if (BonusEnum.getType(bonusType) == 1) {//收益表查询
                String sql = "select ifnull(sum(bonus_price),0) as sum_bonus_vds_amount, ifnull(sum(true_price),0)  as sum_bonus_usdt_amount from t_bonus "
                        + "where user_id = " + userId + " and deal_flag = 1 and bonus_type = ?";
                Query<FinancialBonus> queryTotal = financialBonusDao.getQuery();
                queryTotal.setSql(sql);
                queryTotal.setDatabase("vdsapollo");
                queryTotal.setCls(FinancialBonus.class);
                queryTotal.setParams(new Object[]{bonusType});
                FinancialBonus financialBonusSum = queryTotal.getOne();
                sum_bonus_usdt_amount = financialBonusSum.getSum_bonus_usdt_amount();
                sum_bonus_vds_amount = financialBonusSum.getSum_bonus_vds_amount();
                Query<FinancialBonus> query = financialBonusDao.getQuery();
                query.setSql("select id,bonus_time,bonus_type,bonus_price,vds_price,true_price,deal_flag as dealflag,from_user_id, bonus_floor,bonus_ratio,bonus_profit_amount,from_user_name,bonus_user_level from t_bonus");
                query.append(" bonus_type = " + bonusType);
                query.append(" user_id = " + userId);
                if (StringUtils.isNotEmpty(startTime)) {
                    query.append(" and bonus_time>=cast('" + Timestamp.valueOf(startTime) + "'as datetime)");
                }
                if (StringUtils.isNotEmpty(endTime)) {
                    query.append(" and bonus_time<=cast('" + Timestamp.valueOf(endTime) + "'as datetime)");
                }
                query.append(" order by bonus_time desc");
                query.setDatabase("vdsapollo");
                query.setCls(FinancialBonus.class);
                query.setParams(new Object[]{});
                total = query.count();
                if (total > 0) {
                    //分页查询
                    financialBonusList = query.getPageList(currentPage, 30);
                    for (FinancialBonus financialBonus : financialBonusList) {
                        financialBonus.setBonus_type_name(L(BonusEnum.getValue(financialBonus.getBonus_type())));
                        financialBonus.setBonus_price(financialBonus.getBonus_price().setScale(3, BigDecimal.ROUND_DOWN));
                        financialBonus.setTrue_price(financialBonus.getTrue_price().setScale(3, BigDecimal.ROUND_DOWN));
                        financialBonus.setVds_price(financialBonus.getVds_price().setScale(3, BigDecimal.ROUND_DOWN));
                        financialBonus.setDealflagName(L(StatusEnum.getValue(financialBonus.getDealflag())));
                        String userName = financialBonus.getFrom_user_name();
                        String userNameBefor = userName.substring(0, userName.indexOf("@"));
                        if (userNameBefor.length() > 2) {
                            userNameBefor = userNameBefor.substring(0, 2) + "****";
                            userName = userNameBefor + userName.substring(userName.indexOf("@"));
                        }
                        financialBonus.setFrom_user_name(userName);
                        BigDecimal level = financialBonus.getBonus_ratio().multiply(new BigDecimal("100"));
                        financialBonus.setRemark("");
                        financialBonus.setUp_level(level.stripTrailingZeros().toPlainString() + "%");
                        if (financialBonus.getBonus_type() == BonusEnum.UP_LEVEL_BONUS.getKey()) {
                            long count = level.setScale(0, BigDecimal.ROUND_UP).stripTrailingZeros().longValue();
                            financialBonus.setUp_level(count + L("级") + "(" + count + "%)");
                            financialBonus.setBonus_amount_level9(financialBonus.getBonus_profit_amount().multiply(new BigDecimal("0.09")).setScale(3, BigDecimal.ROUND_DOWN));
                        }
                    }
                }

            } else if (BonusEnum.getType(bonusType) == 2) {
                //收益详情表查询
/*				String sql = "select sum(usdtAmount) as sum_bonus_vds_amount, sum(usdtPrice)  as sum_bonus_usdt_amount from fin_profit_assign_detail "
						+ "where profitUserid = " + userId + " and flag = 1 and profitType = ?";
				Query<FinancialBonus> queryTotal = financialBonusDao.getQuery();
				queryTotal.setSql(sql);
				queryTotal.setDatabase("vip_financial");
				queryTotal.setCls(FinancialBonus.class);
				queryTotal.setParams(new Object[]{bonusType});
				FinancialBonus financialBonusSum = queryTotal.getOne();*/
                //替换为从redis中获取
                String totalAmount = RedisUtil.get("financial_platEcologySystemAmountUsdt");
                if (StringUtils.isNotEmpty(totalAmount)) {
                    sum_bonus_usdt_amount = new BigDecimal(totalAmount);
                    sum_bonus_vds_amount = new BigDecimal(totalAmount);
                }
                Query<FinancialBonus> queryDetails = financialBonusDao.getQuery();
                queryDetails.setSql("select id,createtime as bonus_time ,profitType as bonus_type,profitAmount as bonus_price,usdtPrice as vds_price, usdtAmount as true_price,flag as dealflag,assignVid as remark from fin_profit_assign_detail");
                queryDetails.append(" profitUserid = " + userId);
                queryDetails.append(" profitType = " + bonusType);
                if (StringUtils.isNotEmpty(startTime)) {
                    queryDetails.append(" and createtime>=cast('" + Timestamp.valueOf(startTime) + "'as datetime)");
                }
                if (StringUtils.isNotEmpty(endTime)) {
                    queryDetails.append(" and createtime<=cast('" + Timestamp.valueOf(endTime) + "'as datetime)");
                }
                queryDetails.append(" order by createtime desc");
                queryDetails.setDatabase("vip_financial");
                queryDetails.setCls(FinancialBonus.class);
                queryDetails.setParams(new Object[]{});
                total = queryDetails.count();
                if (total > 0) {
                    //分页查询
                    financialBonusList = queryDetails.getPageList(currentPage, 30);
                    for (FinancialBonus financialBonus : financialBonusList) {
                        financialBonus.setBonus_type_name(L(BonusEnum.getValue(financialBonus.getBonus_type())));
                        financialBonus.setBonus_price(financialBonus.getBonus_price().setScale(3, BigDecimal.ROUND_DOWN));
                        financialBonus.setTrue_price(financialBonus.getTrue_price().setScale(3, BigDecimal.ROUND_DOWN));
                        financialBonus.setVds_price(financialBonus.getVds_price().setScale(3, BigDecimal.ROUND_DOWN));
                        financialBonus.setDealflagName(L(StatusEnum.getValue(financialBonus.getDealflag())));
                        financialBonus.setUp_level("");
                        if (financialBonus.getBonus_type() == BonusEnum.HARVEST_BONUS.getKey()) {
                            StringBuilder sb = new StringBuilder(financialBonus.getRemark());
                            financialBonus.setRemark(L(mark) + sb.replace(2, sb.length() - 3, "***").toString());
                        }
                    }
                }

            } else if (BonusEnum.getType(bonusType) == 3) {
                //VIP 分红奖励
                String totalAmount = RedisUtil.get("financial_platSuperNodePayAmountUsdt");
                if (StringUtils.isNotEmpty(totalAmount)) {
                    sum_bonus_usdt_amount = new BigDecimal(totalAmount);
                    sum_bonus_vds_amount = new BigDecimal(totalAmount);
                }
                String totalWeight = RedisUtil.get("financial_profitWeightTotal");
                if (StringUtils.isNotEmpty(totalWeight)) {
                    financial_profitWeightTotal = new BigDecimal(totalWeight);
                }

                Query<FinancialBonus> queryDetails = financialBonusDao.getQuery();
                queryDetails.setSql("select id,createtime as bonus_time ,superNodeProfitCount,superNodeProfitVipWeight,profitType as bonus_type,profitAmount as bonus_price,usdtPrice as vds_price, usdtAmount as true_price,flag as dealflag from fin_profit_supernode_detail");
                queryDetails.append(" profitUserid = " + userId);
                queryDetails.append(" profitType = " + bonusType);
                if (StringUtils.isNotEmpty(startTime)) {
                    queryDetails.append(" and createtime>=cast('" + Timestamp.valueOf(startTime) + "'as datetime)");
                }
                if (StringUtils.isNotEmpty(endTime)) {
                    queryDetails.append(" and createtime<=cast('" + Timestamp.valueOf(endTime) + "'as datetime)");
                }
                queryDetails.append(" order by createtime desc");
                queryDetails.setDatabase("vip_financial");
                queryDetails.setCls(FinancialBonus.class);
                queryDetails.setParams(new Object[]{});
                total = queryDetails.count();
                if (total > 0) {
                    //分页查询
                    financialBonusList = queryDetails.getPageList(currentPage, 30);
                    for (FinancialBonus financialBonus : financialBonusList) {
                        financialBonus.setBonus_type_name(L(BonusEnum.getValue(financialBonus.getBonus_type())));
                        financialBonus.setBonus_price(financialBonus.getBonus_price().setScale(3, BigDecimal.ROUND_DOWN));
                        financialBonus.setTrue_price(financialBonus.getTrue_price().setScale(3, BigDecimal.ROUND_DOWN));
                        financialBonus.setVds_price(financialBonus.getVds_price().setScale(3, BigDecimal.ROUND_DOWN));
                        financialBonus.setDealflagName(L(StatusEnum.getValue(financialBonus.getDealflag())));
                        financialBonus.setSuperNodeProfitVipWeight(financialBonus.getSuperNodeProfitVipWeight().setScale(3, BigDecimal.ROUND_DOWN));
                        financialBonus.setSuperNodeProfitCountStr(0 == financialBonus.getSuperNodeProfitCount() ? L("释放贡献") : String.valueOf(financialBonus.getSuperNodeProfitCount()));
                    }
                }
            } else if (BonusEnum.getType(bonusType) == 4) {
                String totalAmount = RedisUtil.get("financial_platNewVipWeekAmountUsdt");
                if (StringUtils.isNotEmpty(totalAmount)) {
                    sum_bonus_usdt_amount = new BigDecimal(totalAmount);
                    sum_bonus_vds_amount = new BigDecimal(totalAmount);
                }
                Query<FinancialBonus> queryDetails = financialBonusDao.getQuery();
                queryDetails.setSql("select id,distStartTime,distEndTime,newVipWeekUser,newVipWeekAmount,profitType as bonus_type,profitAmount as bonus_price,usdtPrice as vds_price, usdtAmount as true_price,flag as dealflag from fin_profit_newvip_detail");
                queryDetails.append(" profitUserid = " + userId);
                queryDetails.append(" profitType = " + bonusType);
                if (StringUtils.isNotEmpty(startTime)) {
                    queryDetails.append(" and createtime>=cast('" + Timestamp.valueOf(startTime) + "'as datetime)");
                }
                if (StringUtils.isNotEmpty(endTime)) {
                    queryDetails.append(" and createtime<=cast('" + Timestamp.valueOf(endTime) + "'as datetime)");
                }
                queryDetails.append(" order by createtime desc");
                queryDetails.setDatabase("vip_financial");
                queryDetails.setCls(FinancialBonus.class);
                queryDetails.setParams(new Object[]{});
                total = queryDetails.count();
                if (total > 0) {
                    //分页查询
                    financialBonusList = queryDetails.getPageList(currentPage, 30);
                    for (FinancialBonus financialBonus : financialBonusList) {
                        financialBonus.setBonus_type_name(L(BonusEnum.getValue(financialBonus.getBonus_type())));
                        financialBonus.setBonus_price(financialBonus.getBonus_price().setScale(3, BigDecimal.ROUND_DOWN));
                        financialBonus.setTrue_price(financialBonus.getTrue_price().setScale(3, BigDecimal.ROUND_DOWN));
                        financialBonus.setVds_price(financialBonus.getVds_price().setScale(3, BigDecimal.ROUND_DOWN));
                        financialBonus.setDealflagName(L(StatusEnum.getValue(financialBonus.getDealflag())));
                        financialBonus.setNewVipWeekAmount(financialBonus.getNewVipWeekAmount().setScale(3, BigDecimal.ROUND_DOWN));
                    }
                }

            }else if (BonusEnum.getType(bonusType) == 5) {
                String sql = " select  id, userId, investAmount , userName, profitTime, resetProfitTime, expectProfitUsdt, staticProfitSumUsdt, returnType, batchNo, seqNo, dealFlag,dealTime,authPayFlag,investAvergPrice  from fin_userreturnorderinfoall where 1=1 and dealFlag = 1 and userId = " + userId;
                if (StringUtils.isNotEmpty(startTime)) {
                    sql += " and resetProfitTime>=cast('" + Timestamp.valueOf(startTime) + "'as datetime)";
                }
                if (StringUtils.isNotEmpty(endTime)) {
                    sql += " and resetProfitTime<=cast('" + Timestamp.valueOf(endTime) + "'as datetime)";
                }
                FinUserReturnOrderInfoDao dao = new FinUserReturnOrderInfoDao();
                Query<FinUserReturnOrderInfo> queryTotal = dao.getQuery();
                queryTotal.setSql(sql);
                queryTotal.setDatabase("vip_financial");
                queryTotal.setCls(FinUserReturnOrderInfo.class);
                 total = queryTotal.count();
                List<FinUserReturnOrderInfo> list = new ArrayList<>();
                //累计获得usdt
                BigDecimal cumulative = BigDecimal.ZERO;
                if (total > 0) {
                    list = queryTotal.getPageList(currentPage, 30);
                    for (FinUserReturnOrderInfo inverst : list) {
                        //投资基数
                        BigDecimal investAmount = inverst.getInvestAmount().multiply(inverst.getInvestAvergPrice()).setScale(3, BigDecimal.ROUND_DOWN);
                        if (inverst.getDealFlag() == 0) {
                            inverst.setDealFlagDESC(L("未回本"));
                        } else {
                            inverst.setDealFlagDESC(L("已回本"));
                        }
                        if (inverst.getAuthPayFlag() == 2) {
                            inverst.setReturnTypeDESC(L("已复投"));
                        } else {
                            inverst.setReturnTypeDESC(L("未复投"));
                        }
                        /*理论收益 3位小数*/
                        String userNames = inverst.getUserName();
                        String userNameBefor = userNames.substring(0, userNames.indexOf("@"));
                        if (userNameBefor.length() > 2) {
                            userNameBefor = userNameBefor.substring(0, 2) + "****";
                            userNames = userNameBefor + userNames.substring(userNames.indexOf("@"));
                        }

                        inverst.setUserName(userNames);
                        //回本数量截取3位小数
                        BigDecimal recoveyUsdt = inverst.getExpectProfitUsdt().subtract(inverst.getStaticProfitSumUsdt())
                                .setScale(3, BigDecimal.ROUND_DOWN);

                        if (recoveyUsdt.compareTo(BigDecimal.ZERO) < 0) {
                            inverst.setRecoveryUsdt(BigDecimal.ZERO);
                        } else {
                        	/*.setScale(4, BigDecimal.ROUND_DOWN);*/
                            inverst.setRecoveryUsdt(recoveyUsdt);
                        }
                        cumulative = cumulative.add(inverst.getRecoveryUsdt());

                        inverst.setInvestAmount(investAmount);
                    }
                }
                Map<String, Object> page = new HashMap<String, Object>();
                page.put("pageIndex", currentPage);
                page.put("totalCount", total);
                page.put("list", list);
                page.put("sum_bonus_usdt_amount",cumulative);
                json("ok", true, JSONObject.toJSONString(page));
                return;
            }else if(BonusEnum.getType(bonusType) == 6){
                String sql = "";
                sql = "select id,createTime,sonUserName,concat(avaTransferAmount,'VOLLAR') as avaTransferAmount,concat(douProfitAmount,'VOLLAR') as douProfitAmount,transferType,parentUserName,concat(avaTransferAmount-douProfitAmount,'VOLLAR') " +
                        "as AttributionNum from fin_usersontransfer where fundsType=51 and parentUserId=" + userId;

                if (userSonRelationFlag) {
                    /*是子账号，切换数据处理逻辑*/
                    userId = sonUserId;
//                    sonUserName = sonUserName;
                    sql = "select createTime,sonUserName,concat(avaTransferAmount,'VOLLAR') as avaTransferAmount,concat(douProfitAmount,'VOLLAR') as douProfitAmount,transferType,parentUserName,concat(avaTransferAmount-douProfitAmount,'VOLLAR') " +
                            "as AttributionNum from fin_usersontransfer where fundsType=51 and sonUserId=" + userId;

                }
                FinUsersontransferDao dao = new FinUsersontransferDao();
                Query<FinUsersontransfer> queryTotal = dao.getQuery();
                queryTotal.setSql(sql);
                queryTotal.setDatabase("vip_financial");
                queryTotal.setCls(FinUsersontransfer.class);
                 total = queryTotal.count();
                List<FinUsersontransfer> list = queryTotal.getPageList(currentPage, 30);
                Map<String, Object> page = new HashMap<String, Object>();
                page.put("pageIndex", currentPage);
                page.put("totalCount", total);
                page.put("list", list);
                json("ok", true, JSONObject.toJSONString(page));
                return;
            }
            Map<String, Object> page = new HashMap<String, Object>();
            page.put("pageIndex", currentPage);
            page.put("totalCount", total);
            page.put("totalWeight", financial_profitWeightTotal);
            page.put("sum_bonus_usdt_amount", sum_bonus_usdt_amount);
            page.put("sum_bonus_vds_amount", sum_bonus_vds_amount);
            page.put("list", financialBonusList);
            json("ok", true, JSONObject.toJSONString(page));
            return;
        } catch (Exception e) {
            log.info("获取收益列表失败", e);
        }
        Map<String, Object> page = new HashMap<String, Object>();
        page.put("pageIndex", 1);
        page.put("totalCount", 0);
        page.put("totalWeight", BigDecimal.ZERO);
        page.put("sum_bonus_usdt_amount", BigDecimal.ZERO);
        page.put("sum_bonus_vds_amount", BigDecimal.ZERO);
        page.put("list", new ArrayList<FinancialBonus>());
        json("ok", false, JSONObject.toJSONString(page));
    }

    /**
     * 撤销保险
     */
    @Page(Viewer = JSON)
    public void userCancelInsureinvest() {
        initLoginUser();
        try {
            String strId = param("id");
            int intId = 0;
            if (StringUtils.isEmpty(strId)) {
                json(L("无效参数"), false, null, true);
                return;
            }
            try {
                intId = Integer.parseInt(strId);
            } catch (Exception e) {
                json(L("无效参数"), false, null, true);
                return;
            }
            String sql = "";
            /*先查询担保投资表*/
            //sql = "select triggerPrice,investLevelAmount,insureInvestAmount,insureInvestNum from fin_userinsureinvest where id=" + intId + "and investState = 0 and now() > date_add(createTime,interval 5 MINUTE)";
            sql = "select triggerPrice,investLevelAmount,insureInvestAmount,insureInvestNum from fin_userinsureinvest where id=" + intId + "and investState = 0 )";
            FinUserInsureInvest finUserInsureInvest = (FinUserInsureInvest) Data.GetOne("vip_financial", sql, null);
//            if (finUserInsureInvest == null) {
//                log.info("id:" + intId + "没有查询到资金信息" + sql);
//                json(L("一小时内不允许撤销"), false, null, true);
//                return;
//            }
            List<OneSql> sqls = new ArrayList<>();
            TransactionObject txObj = new TransactionObject();
            /*扣减冻结资金，把余额加回来*/
            sql = "update pay_user_financial set balance = balance + " + finUserInsureInvest.getInsureInvestAmount() + ", "
                    + "insureInvestFreezeAmount = insureInvestFreezeAmount - " + finUserInsureInvest.getInsureInvestAmount() + " "
                    + "where userId = " + finUserInsureInvest.getUserId() + " and fundsType = 51 and insureInvestFreezeAmount >= " + finUserInsureInvest.getInsureInvestAmount() + " ";
            log.info("扣减冻结资金，把余额加回来sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));
            sql = "update fin_userinsureinvest set investState = 3  where id = " + intId + " and investState = 0 ";
            log.info("修改fin_userinsureinvest:" + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_financial"));
            txObj.excuteUpdateList(sqls);
            json(L("撤销成功"), true, null, true);
        } catch (Exception e) {
            json(L("险投保失败"), false, null, true);
            log.info("理财报警ERROR:撤销保险失败", e);
        }
    }

    /**
     * 用户担保投资查询
     */
    @Page(Viewer = JSON)
    public void queryUserInsureinvest() {
        initLoginUser();
        try {
            int pageNo = intParam("pageIndex");
            int pageSize = intParam("pageSize");

            if (pageNo < 1) {
                pageNo = 1;
            }
            if (pageSize < 1) {
                pageSize = 1;
            }
            String userId = loginUser.get_Id();
            String sql = "select id,triggerPrice,investLevelAmount,insureInvestAmount,insureInvestSurplusNum,insureInvestNum,investState,createTime from fin_userinsureinvest where userId=" + userId;
            FinUserInsureInvestDao dao = new FinUserInsureInvestDao();
            Query<FinUserInsureInvest> queryTotal = dao.getQuery();
            queryTotal.setSql(sql);
            queryTotal.setDatabase("vip_financial");
            queryTotal.setCls(FinUserInsureInvest.class);
            int total = queryTotal.count();
            List<FinUserInsureInvest> list = new ArrayList<>();
            if (total > 0) {
                list = queryTotal.getPageList(pageNo, pageSize);
                for (FinUserInsureInvest inverst : list) {
                    if (inverst.getInvestState() == 0) {
                        inverst.setTriggerFlagDesc(L("撤销保险"));
                    } else if (inverst.getInvestState() == 1) {
                        inverst.setTriggerFlagDesc(L("已投"));
                    } else if (inverst.getInvestState() == 2) {
                        inverst.setTriggerFlagDesc(L("已投"));
                    } else {
                        inverst.setTriggerFlagDesc(L("已撤销"));
                    }
                    if (inverst.getInvestState() == 3) {
                        inverst.setInvestStateDesc("-");
                    } else if (inverst.getTriggerFlag() != 0 && inverst.getInsureInvestSurplusNum() == 0) {
                        inverst.setInvestStateDesc(L("设置"));
                    }
                    if (inverst.getInvestState() == 0) {
                        inverst.setInvestStateDesc("-");
                    } else {
                        inverst.setInvestStateDesc(L("已设置") + "(" + inverst.getInsureInvestSurplusNum() + "/" + inverst.getInsureInvestNum() + ")");
                    }
                    if (inverst.getInvestState() == 1 || inverst.getInvestState() == 0) {
                        inverst.setJumpFlag(true);
                    }
                }
            }
            Map<String, Object> page = new HashMap<String, Object>();
            page.put("pageIndex", pageNo);
            page.put("totalCount", total);
            page.put("list", list);
            json("ok", true, JSONObject.toJSONString(page));
            return;
        } catch (Exception e) {
            log.info("理财报警ERROR:用户担保投资查询异常", e);
        }
        Map<String, Object> page = new HashMap<String, Object>();
        page.put("pageIndex", 1);
        page.put("totalCount", 0);
        page.put("list", new ArrayList<FinUserInsureInvest>());
        json("ok", true, JSONObject.toJSONString(page));

    }

    /**
     * 保险投资子账号列表
     */
    @SuppressWarnings("unchecked")
    @Page(Viewer = JSON)
    public void useInsureInvestSonInfos() {
        initLoginUser();
        try {
            String userId = loginUser.get_Id();
            int pageNo = intParam("pageIndex");
            int pageSize = intParam("pageSize");
            if (pageNo < 1) {
                pageNo = 1;
            }
            if (pageSize < 1) {
                pageSize = 1;
            }
            String sql = "";
            sql = "select id, userId,userName,invitationCode,userVID from fin_userfinancialinfo " +
                    "where parentUserId =" + userId + " and authPayFlag != 1 ";
            UserFinancialInfoDao dao = new UserFinancialInfoDao();
            Query<UserFinancialInfo> queryTotal = dao.getQuery();
            queryTotal.setSql(sql);
            queryTotal.setDatabase("vip_financial");
            queryTotal.setCls(UserFinancialInfo.class);
            int total = queryTotal.count();
            List<UserFinancialInfo> list = new ArrayList<>();
            /**
             * 直接邀请人数
             */
            String directInvitationSucNum = "0";
            if (total > 0) {
                list = queryTotal.getPageList(pageNo, pageSize);
                String invitationCode = list.get(0).getpInvitationCode();
                sql = "select count(*) cnt from fin_userfinancialinfo where pInvitationCode = '" + invitationCode + "' and authPayFlag = 2";
                List<Integer> listDirectInvitationSucNum = (List<Integer>) Data.GetOne("vip_financial", sql, null);
                if (null != listDirectInvitationSucNum) {
                    directInvitationSucNum = listDirectInvitationSucNum.get(0) + "";
                }
            }
            //循环插入团队邀请人数和直接邀请人数
            for (UserFinancialInfo userFinancialInfo : list) {
                /**
                 * 团队邀请人数
                 */
                String invitationTotalNum = RedisUtil.get("financial_invitationTotalNum_" + userFinancialInfo.getUserId());
                if (StringUtils.isBlank(invitationTotalNum)) {
                    invitationTotalNum = "0";
                }
                userFinancialInfo.setSwitchAccount(L(userFinancialInfo.getSwitchAccount()));
                userFinancialInfo.setTeamsNumber(Integer.valueOf(invitationTotalNum));
                userFinancialInfo.setDirectNumber(Integer.valueOf(directInvitationSucNum));
            }
            Map<String, Object> page = new HashMap<String, Object>();
            page.put("pageIndex", pageNo);
            page.put("totalCount", total);
            page.put("list", list);
            json("ok", true, JSONObject.toJSONString(page));
            return;
        } catch (Exception e) {
            log.info("理财报警ERROR:保险投资子账号查询异常", e);
        }
        Map<String, Object> page = new HashMap<String, Object>();
        page.put("pageIndex", 1);
        page.put("totalCount", 0);
        page.put("list", new ArrayList<UserFinancialInfo>());
        json("ok", true, JSONObject.toJSONString(page));
    }

    /**
     * 账号划入划出列表
     */
    @Page(Viewer = JSON)
    public void userTransferBill() {
        initLoginUser();
        try {
            String sonUserId = param("sonUserId");
            String sonUserName = param("sonUserName");
            //String transferName= param("transferName");
            int pageSize = intParam("pageSize");
            int currentPage = intParam("pageIndex");
            String userId = loginUser.get_Id();
            String sql = "";
            sql = "select id,createTime,sonUserName,concat(avaTransferAmount,'VOLLAR') as avaTransferAmount,concat(douProfitAmount,'VOLLAR') as douProfitAmount,transferType,parentUserName,concat(avaTransferAmount-douProfitAmount,'VOLLAR') " +
                    "as AttributionNum from fin_usersontransfer where fundsType=51 and parentUserId=" + userId;
//            if(StringUtils.isNotEmpty(transferName)){
//                sql+= " and transferName="+transferName;
//            }
            boolean userSonRelationFlag = userSonRelationCheck(userId, sonUserName, sonUserId);
            if (userSonRelationFlag) {
                /*是子账号，切换数据处理逻辑*/
                userId = sonUserId;
//                sonUserName = sonUserName;
                sql = "select createTime,sonUserName,concat(avaTransferAmount,'VOLLAR') as avaTransferAmount,concat(douProfitAmount,'VOLLAR') as douProfitAmount,transferType,parentUserName,concat(avaTransferAmount-douProfitAmount,'VOLLAR') " +
                        "as AttributionNum from fin_usersontransfer where fundsType=51 and sonUserId=" + userId;
//                if(StringUtils.isNotEmpty(transferName)){
//                    sql+= " and transferName="+transferName;
//                }
            }
            FinUsersontransferDao dao = new FinUsersontransferDao();
            Query<FinUsersontransfer> queryTotal = dao.getQuery();
            queryTotal.setSql(sql);
            queryTotal.setDatabase("vip_financial");
            queryTotal.setCls(FinUsersontransfer.class);
            int total = queryTotal.count();
            List<FinUsersontransfer> list = queryTotal.getPageList(currentPage, pageSize);
            Map<String, Object> page = new HashMap<String, Object>();
            page.put("pageIndex", currentPage);
            page.put("totalCount", total);
            page.put("list", list);
            json("ok", true, JSONObject.toJSONString(page));
            return;
        } catch (Exception e) {
            log.info("理财报警ERROR:账号划入划出列表异常", e);
        }
        Map<String, Object> page = new HashMap<String, Object>();
        page.put("pageIndex", 1);
        page.put("totalCount", 0);
        page.put("list", new ArrayList<FinUsersontransfer>());
        json("ok", true, JSONObject.toJSONString(page));
    }


    /**
     * 主账号及子账号邀请码下拉选
     */
    @Page(Viewer = JSON)
    public void userAndSonInvitationCode() {
        initLoginUser();
        try {
            String userId = loginUser.get_Id();
            String sql = "";
            sql = "select username ,invitationCode from fin_userfinancialinfo where userId=" + userId + " or parentUserId=" + userId;

            UserFinancialInfoDao dao = new UserFinancialInfoDao();
            Query<UserFinancialInfo> queryTotal = dao.getQuery();
            queryTotal.setSql(sql);
            queryTotal.setDatabase("vip_financial");
            queryTotal.setCls(UserFinancialInfo.class);
            List<UserFinancialInfo> list = queryTotal.getList();
            Map<String, String> map = new HashMap<>();
            for (UserFinancialInfo userFinancialInfo : list) {
                map.put(userFinancialInfo.getInvitationCode(), userFinancialInfo.getUserName()
                        + "   " + userFinancialInfo.getInvitationCode());
            }
            JSONArray jsonArray = new JSONArray();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                JSONObject json = new JSONObject();
                json.put("name", entry.getValue());
                json.put("value", entry.getKey());
                jsonArray.fluentAdd(json);
            }
            json("ok", true, JSONObject.toJSONString(jsonArray));
            return;
        } catch (Exception e) {
            log.info("理财报警ERROR:主账号及子账号邀请码下拉选异常", e);
        }
        JSONArray jsonArray = new JSONArray();
        json("ok", true, JSONObject.toJSONString(jsonArray));
    }

    /**
     * 设置排位可用份数
     *
     * @param
     */
    @SuppressWarnings("unchecked")
	@Page(Viewer = JSON)
    public void queryUserinSureinvestNum() {
        try {
            String id = param("id");
            log.info("canshu" + id);
            String sql = "";
            sql = "select insureInvestNum-insureInvestSurplusNum as num from fin_userinsureinvest where id =" + id;
            List<Long> num = (List<Long>) Data.GetOne("vip_financial", sql, null);
            if (num.size() > 0 && num != null) {
                json("ok", true, num.get(0).toString());
            } else {
                json("ok", true, "0");
            }
            return;
        } catch (Exception e) {
            log.info("理财报警ERROR:设置排位可用份数" + e.getMessage());
        }
        json("ok", true, "0");
    }


    /**
     * 用户收益列表
     * userReturnOrderInfo
     */
    @Page(Viewer = JSON)
    public void returnUserOrderWork() {
        try {
            int pageNo = intParam("pageIndex");
            int pageSize = intParam("pageSize");

            if (pageNo < 1) {
                pageNo = 1;
            }
            if (pageSize < 1) {
                pageSize = 1;
            }

            String userName = param("userName");
            String dealFlag =param("sNodeBelType");
            String sql = "";
            sql = "select  id, userId, userName, profitTime, resetProfitTime, expectProfitUsdt, staticProfitSumUsdt, "
            	+ "returnType, batchNo, seqNo, dealFlag, authPayFlag from fin_userreturnorderinfo where 1 = 1 ";
            
            if(StringUtils.isNotEmpty(userName)){
                sql += " and userName = '" + userName + "' ";
            }

            /**
             * 前端传的 0是全部 1是已回本 2是未回本 所以才这么处理的
             */
            if(StringUtils.isNotEmpty(dealFlag) && !"0".equals(dealFlag)){
                if(dealFlag.equals("2")){
                   dealFlag = "0" ;
                }

                sql += " and dealFlag = " + dealFlag;
            }

            sql += " ORDER BY dealFlag, authPayFlag, id" ;
            FinUserReturnOrderInfoDao dao = new FinUserReturnOrderInfoDao();
            Query<FinUserReturnOrderInfo> queryTotal = dao.getQuery();
            queryTotal.setSql(sql);
            queryTotal.setDatabase("vip_financial");
            queryTotal.setCls(FinUserReturnOrderInfo.class);
            int total = queryTotal.count();
            List<FinUserReturnOrderInfo> list = new ArrayList<>();
            if (total > 0) {
                list = queryTotal.getPageList(pageNo, pageSize);
                for (FinUserReturnOrderInfo inverst : list) {
                	if (inverst.getDealFlag() == 0) {
                		inverst.setDealFlagDESC(L("未回本"));
                		inverst.setReturnTypeDESC("-");
                	} else {
                		inverst.setDealFlagDESC(L("已回本"));
                		if (inverst.getAuthPayFlag() == 2) {
                			inverst.setReturnTypeDESC(L("已复投"));
                		} else {
                			inverst.setReturnTypeDESC(L("未复投"));
                		}
                	}
//                    if (inverst.getDealFlag() == 0) {
//                        inverst.setDealFlagDESC(L("未回本"));
//                    } else {
//                        inverst.setDealFlagDESC(L("已回本"));
//                    }
//                    if(inverst.getDealFlag() == 0) {
//                        inverst.setReturnTypeDESC("-");
//                    }else if (inverst.getAuthPayFlag() == 2) {
//                        inverst.setReturnTypeDESC(L("已复投"));
//                    } else {
//                        inverst.setReturnTypeDESC(L("未复投"));
//                    }
                    String userNames = inverst.getUserName();
                    String userNameBefor = userNames.substring(0, userNames.indexOf("@"));
                    if (userNameBefor.length() > 2) {
                        userNameBefor = userNameBefor.substring(0, 2) + "****";
                        userNames = userNameBefor + userNames.substring(userNames.indexOf("@"));
                    }
                    inverst.setUserName(userNames);
                    inverst.setProfit(inverst.getExpectProfitUsdt());
                }
            }
            Map<String, Object> page = new HashMap<String, Object>();
            page.put("pageIndex", pageNo);
            page.put("totalCount", total);
            page.put("list", list);
            json("ok", true, JSONObject.toJSONString(page));
            return;
        } catch (Exception e) {
            log.info("理财报警ERROR:用户收益列表", e);
        }
        Map<String, Object> page = new HashMap<String, Object>();
        page.put("pageIndex", 1);
        page.put("totalCount", 0);
        page.put("list", new ArrayList<FinUserInsureInvest>());
        json("ok", true, JSONObject.toJSONString(page));

    }




    public static void main(String[] args) {
//        Index index = new Index();
//        index.userHierarchyInfo();
        BigDecimal a= BigDecimal.ZERO;
        BigDecimal b= BigDecimal.ONE;
        a=a.add(b);
        System.out.println(a);
        System.out.println(b);
    }


}

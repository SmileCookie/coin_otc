package com.world.controller.manage.account;

import com.Lan;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.DowoloadApiService;
import com.messi.user.feign.GoogleApiService;
import com.messi.user.feign.UserApiService;
import com.messi.user.util.ConstantCenter;
import com.messi.user.vo.DownloadVo;
import com.messi.user.vo.R;
import com.world.cache.Cache;
import com.world.constant.Const;
import com.world.controller.api.util.SystemCode;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.LimitType;
import com.world.model.dao.financialproift.UserFinancialInfoDao;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.pay.DownloadDao;
import com.world.model.dao.pay.DownloadSummaryDao;
import com.world.model.dao.pay.PayUserWalletDao;
import com.world.model.dao.pay.ReceiveAddrDao;
import com.world.model.dao.user.EmailDao;
import com.world.model.dao.user.MobileDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.CointTable;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.Price;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.financialproift.UserFinancialInfo;
import com.world.model.entity.pay.DownloadBean;
import com.world.model.entity.pay.PayUserWalletBean;
import com.world.model.entity.pay.ReceiveAddr;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.model.entity.user.WithdrawAddressAuthenType;
import com.world.model.entity.usercap.dao.CommAttrDao;
import com.world.model.entity.usercap.entity.AddressType;
import com.world.model.entity.usercap.entity.CommAttrBean;
import com.world.model.loan.dao.P2pUserDao;
import com.world.rabbitmq.producer.OperateLogInfoProducer;
import com.world.util.CommonUtil;
import com.world.util.Message;
import com.world.util.MsgToastKey;
import com.world.util.UserUtil;
import com.world.util.date.TimeUtil;
import com.world.util.sign.RSACoder;
import com.world.util.string.EncryptionPhoto;
import com.world.util.string.MD5;
import com.world.web.Page;
import com.world.web.action.ApproveAction;
import com.world.web.response.DataResponse;
import com.world.web.sso.rsa.RsaLoginUtil;
import com.world.web.sso.rsa.RsaUser;
import com.world.web.sso.session.ClientSession;
import com.yc.entity.SysGroups;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class Download extends ApproveAction {

    private static final long serialVersionUID = 1L;

    private DownloadDao ddao = new DownloadDao();
    private PayUserWalletDao dao = new PayUserWalletDao();
    private UserDao userDao = new UserDao();
    private ReceiveAddrDao receiveDao = new ReceiveAddrDao();
    private P2pUserDao p2pUserDao = new P2pUserDao();
    private DownloadSummaryDao downloadSummaryDao = new DownloadSummaryDao();
    private CommAttrDao commAttrDao = new CommAttrDao();
    private UserFinancialInfoDao userFinancialInfoDao = new UserFinancialInfoDao();


    /**
     * 配合前端改造
     */
    @Page(Viewer = JSON)
    public void indexJson() {
        Map<String, Object> result = new HashMap<>();
        initLoginUserJson();
        result.put("withdrawAddressAuthenType", loginUser.getWithdrawAddressAuthenType());
        result.put("addAddressStatus", 0);
        int addAddressLock = doGetErrorTimes(loginUser.getId(), LimitType.addAddress);
        if (addAddressLock == -2) {
            result.put("addAddressStatus", 1);
            json(L("新增提现地址功能已被锁定，请24小时之后再试"), false, "");
            return;
        }
        json("success", true, com.alibaba.fastjson.JSONObject.toJSONString(result), true);
    }

    @Page
    public void index() {
        //TODO jsp页面跳转，之后下掉jsp
        try {
            response.sendRedirect(VIP_DOMAIN);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    //Close By suxinjie 一期屏蔽该功能
    //@Page(Viewer = "/cn/manage/account/download/historyAddr.jsp")
    public void historyAddr() {
        String userId = userIdStr();
        receiveDao.setCoint(coint);
        List<ReceiveAddr> list = receiveDao.findAddr(userId);
        request.setAttribute("myBanks", list);
    }

    /**
     * 获取用户提现地址列表
     */
    @Page(Viewer = JSON)
    public void historyAddrJSON() {
        String userId = userIdStr();
        receiveDao.setCoint(coint);
        List<ReceiveAddr> list = receiveDao.findAddr(userId);
        json("", true, JSONObject.toJSONString(list));
    }

    /**
     * 分页获取用户提现地址列表
     */
    @Page(Viewer = JSON)
    public void getAddressPage() {
        initLoginUser();
        receiveDao.setCoint(coint);

        int pageIndex = intParam("pageIndex");
        if (pageIndex == 0) {
            pageIndex = 1;
        }
        int pageSize = intParam("pageSize");
        if (pageSize == 0) {
            pageSize = 5;
        }

        List<ReceiveAddr> list = new ArrayList<>();
        int count = receiveDao.findAddrCount(loginUser.getId());
        if (count > 0) {
            list = receiveDao.findAddrPage(loginUser.getId(), pageSize * (pageIndex - 1), pageSize);
            Map<String, CoinProps> coinPropMaps = DatabasesUtil.getNewCoinPropMaps();
            for (ReceiveAddr receiveAddr : list) {
                if(receiveAddr.getAgreement() != null){
                    CoinProps coinProps =  DatabasesUtil.getUsdtAggrement(receiveAddr.getAgreement());
                    if(!coinProps.isCanWithdraw()){
                        receiveAddr.setCanWithdraw(coinProps.isCanWithdraw());
                    }
                }else{
                    CoinProps coinProps =  DatabasesUtil.getUsdtAggrement(coint.getFundsType());
                    if(!coinProps.isCanWithdraw()){
                        receiveAddr.setCanWithdraw(coinProps.isCanWithdraw());
                    }
                }
                //列表地址锁定的条件：
                //安全模式 && 地址创建时间>切换模式时间 && 地址创建时间在24小时之内
                if (loginUser.getWithdrawAddressAuthenType() == WithdrawAddressAuthenType.SECURITY.getKey()
                        && receiveAddr.getCreateTime().after(loginUser.getWithdrawAddressAuthenModifyTime())
                        && TimeUtil.getOriginDiffDay(now(), receiveAddr.getCreateTime()) < 1) {
                    receiveAddr.setLockStatus(1);
                    if(receiveAddr.getAgreement()==null){
                        receiveAddr.setAgreement(0);//临时约定魔法值0
                    }
                }
            }
        }

        JSONObject result = new JSONObject();
        result.put("pageIndex", pageIndex);
        result.put("totalCount", count);
        result.put("list", list);
        json("", true, result.toJSONString());
    }

    //    @Page(Viewer = "/cn/manage/account/download/address.jsp")
    public void address() {
        initLoginUser();
    }

    //    @Page(Viewer = "/cn/manage/account/download/indexDetails.jsp")
    public void downloadDetails() {
        initLoginUser();
        String userId = loginUser.getId();

        ddao.setCoint(coint);
        PayUserWalletBean payuser = dao.getById(Integer.parseInt(userId), coint.getFundsType());
        //配置文件中每次最多可提现数量
        BigDecimal everyTimeCash = payuser.getTimesCash();

        //该币种今天已经提现数量
        BigDecimal todayCash = BigDecimal.ZERO;
        //该币种今天可以提现总数，默认配置文件值
        BigDecimal dayCash = payuser.getDayCash();

        //封装提现额度
        //获取用户是否通过身份认证
        Map<String, Object> userDownloadLimit = downloadSummaryDao.getDownloadLimit(loginUser.getId());
        int authResult = (int) userDownloadLimit.get("authResult");
        BigDecimal downloadLimit = (BigDecimal) userDownloadLimit.get("downloadLimit");
        setAttr("authResult", authResult);
        setAttr("downloadLimit", downloadLimit);

        //获取今日已提现btc金额
        BigDecimal todayCashBTC = downloadSummaryDao.getTodayBtcAmount(loginUser.getId());
        BigDecimal available = downloadLimit.subtract(todayCashBTC);
        setAttr("availableDownload", available.compareTo(BigDecimal.ZERO) > 0 ? available : BigDecimal.ZERO);

        //获取币种对btc价格
        BigDecimal price = Price.getCoinBtcPrice(coint.getStag());
        //如果该币种有市场，根据总额度计算提现数据
        if (price.compareTo(BigDecimal.ZERO) > 0) {
            if (authResult == 1) {
                //能够提升的最大额度，已经通过认证的为0
                setAttr("canMaxAmount", 0);
            } else {
                BigDecimal maxDownload = (BigDecimal) userDownloadLimit.get("maxDownload");
                setAttr("canMaxAmount", maxDownload.divide(price, 8, BigDecimal.ROUND_DOWN));
            }
            dayCash = downloadLimit.divide(price, 8, BigDecimal.ROUND_DOWN);
            //转换后的该币种的已经提现数量
            todayCash = todayCashBTC.divide(price, 8, BigDecimal.ROUND_UP);
        } else {
            todayCash = ddao.getTodayCash(userId);
            setAttr("canMaxAmount", 0);
        }

        setAttr("todayCash", todayCash);
        setAttr("everyTimeCash", everyTimeCash);
        setAttr("dayCash", dayCash);

        BigDecimal thisTimeCouldCash = ddao.getThisTimeCouldCash(payuser.getBalance(), todayCash, everyTimeCash, dayCash);
        BigDecimal canWithdraw = p2pUserDao.getCanWithdraw(userId, loginUser.getUserName(), thisTimeCouldCash, coint.getStag());

        setAttr("canWithdraw", canWithdraw);
        setAttr("fees", coint.getMinFees());

        BigDecimal todayFreeCash = ddao.getTodayFreeCash(userId);
        BigDecimal couldFreeCash = payuser.getDayFreeCash().subtract(todayFreeCash);
        couldFreeCash = couldFreeCash.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : couldFreeCash;
        setAttr("couldFreeCash", couldFreeCash);

        setAttr("freeCash", payuser.getDayFreeCash());

        setAttr("codeType", PostCodeType.cash.getKey());
        if (StringUtils.isBlank(loginUser.getSafePwd())) {
            setAttr("safeAuth", true);
        }

        JSONArray funds = UserCache.getUserFunds(userIdStr());
        for (int i = 0; i < funds.size(); i++) {
            JSONObject obj = funds.getJSONObject(i);
            if (obj.getString("propTag").equals(coint.getPropTag())) {
                setAttr("balance", obj.getBigDecimal("balance").doubleValue());
                setAttr("freeze", obj.getBigDecimal("freeze").doubleValue());
                break;
            }
        }

        int lockStatus = 0;
        if (loginUser.getSafePwdModifyTimes() > 1 && null != loginUser.getSafePwdModifyTime()
                && TimeUtil.getOriginDiffDay(now(), loginUser.getSafePwdModifyTime()) < 1) {
            lockStatus = 1;
            setAttr("lockStatus", 1);
            setAttr("lockTips", L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。"));
        }

        int withdrawPayPassLock = doGetErrorTimes(loginUser.getId(), LimitType.WithdrawPayPwdPassError);
        int withdrawEmailLock = doGetErrorTimes(loginUser.getId(), LimitType.WithdrawEmailPassError);
        int withdrawSmsLock = doGetErrorTimes(loginUser.getId(), LimitType.WithdrawMobilePassError);
        int withdrawGoogleLock = doGetErrorTimes(loginUser.getId(), LimitType.WithdrawGooglePassError);
        if ((withdrawPayPassLock == -2) || (withdrawEmailLock == -2) || (withdrawSmsLock == -2) || (withdrawGoogleLock == -2)) {
            setAttr("lockStatus", 1);
        }
        receiveDao.setCoint(coint);
        long addressId = longParam("addressId");
        ReceiveAddr receive = receiveDao.getById(addressId);
        if (null == receive || !receive.getUserId().equals(userId)) {
            if (lockStatus == 0) {
                setAttr("lockStatus", 1);
                setAttr("lockTips", L("提币地址不存在"));
            }
        } else {
            setAttr("memo", receive.getMemo());
            setAttr("address", receive.getAddress());

            if (lockStatus == 0) {
                //提现锁定条件
                //非首次切换模式 && 切换时间<24小时
                //安全模式 && 地址创建时间在24小时之内
                if (loginUser.getWithdrawAddressAuthenSwitchStatus() == 2
                        && TimeUtil.getOriginDiffDay(now(), loginUser.getWithdrawAddressAuthenModifyTime()) < 1) {
                    setAttr("lockStatus", 1);
                    setAttr("lockTips", L("您的帐户因切换模式被锁定，在此期间不能进行提现操作，请等待24小时后自动解锁。"));
                } else if (loginUser.getWithdrawAddressAuthenType() == WithdrawAddressAuthenType.SECURITY.getKey()
                        && receive.getCreateTime().after(loginUser.getWithdrawAddressAuthenModifyTime())
                        && TimeUtil.getOriginDiffDay(now(), receive.getCreateTime()) < 1) {
                    setAttr("lockStatus", 1);
                    setAttr("lockTips", L("您当前为“安全模式”，新增提现地址后将被锁定24小时。"));
                } else {
                    setAttr("lockStatus", 0);
                }
            }
        }
    }

    /**
     * 重置资金密码置灰
     */
    @Page(Viewer = JSON)
    public void resetPayPwdAsh() {
        initLoginUser();
        int payLoginLock = doGetErrorTimes(loginUser.getId(), LimitType.PayLoginPassError);
        int payEmailLock = doGetErrorTimes(loginUser.getId(), LimitType.PayEmailPassError);
        int payMobileLock = doGetErrorTimes(loginUser.getId(), LimitType.PayMobilePassError);
        int payGoogleLock = doGetErrorTimes(loginUser.getId(), LimitType.PayGooglePassError);
        int withdrawPayPwdLock = doGetErrorTimes(loginUser.getId(), LimitType.WithdrawPayPwdPassError);

        Map<String, Object> result = new HashMap<>();
        result.put("ashLockStatus", 0);
        if ((payLoginLock == -2) || (payEmailLock == -2)
                || (payMobileLock == -2) || (payGoogleLock == -2)
                || (withdrawPayPwdLock == -2)) {
            result.put("ashLockStatus", 1);
        }
        if (loginUser.getSafePwdModifyTimes() > 1 && null != loginUser.getSafePwdModifyTime()
                && TimeUtil.getOriginDiffDay(now(), loginUser.getSafePwdModifyTime()) < 1) {
            result.put("ashLockStatus", 1);
        }
        json("ok", true, JSONObject.toJSONString(result), true);
    }


    /**
     * 配合前端改造
     */
    @Page(Viewer = JSON)
    public void downloadDetailsJson() {
        initLoginUser();
        coinProps();
        Map<String, Object> result = new HashMap<>();
        dao.setCoint(coint);
        initLoginUserJson();
        String userId = loginUser.getId();
        result.put("payMobileAuth", loginUser.isPayMobileAuth());
        result.put("payGoogleAuth", loginUser.isPayGoogleAuth());
        result.put("payEmailAuth", loginUser.isPayEmailAuth());

        ddao.setCoint(coint);
        PayUserWalletBean payuser = dao.getById(Integer.parseInt(userId), coint.getFundsType());
        payuser.setCoint(coint);
        //配置文件中每次最多可提现数量
        BigDecimal everyTimeCash = payuser.getTimesCash();

        //该币种今天已经提现数量
        BigDecimal todayCash = BigDecimal.ZERO;
        //该币种今天可以提现总数，默认配置文件值
        BigDecimal dayCash = payuser.getDayCash();

        //封装提现额度
        //获取用户是否通过身份认证
        Map<String, Object> userDownloadLimit = downloadSummaryDao.getDownloadLimit(loginUser.getId());
        int authResult = (int) userDownloadLimit.get("authResult");
        BigDecimal downloadLimit = (BigDecimal) userDownloadLimit.get("downloadLimit");
        result.put("authResult", authResult);
        result.put("downloadLimit", downloadLimit);

        //获取今日已提现btc金额
        BigDecimal todayCashBTC = downloadSummaryDao.getTodayBtcAmount(loginUser.getId());
        BigDecimal available = downloadLimit.subtract(todayCashBTC);
        result.put("availableDownload", available.compareTo(BigDecimal.ZERO) > 0 ? available : BigDecimal.ZERO);

        //获取币种对btc价格
        BigDecimal price = Price.getCoinBtcPrice(coint.getStag());
        //如果该币种有市场，根据总额度计算提现数据
        if (price.compareTo(BigDecimal.ZERO) > 0) {
            if (authResult == 1) {
                //能够提升的最大额度，已经通过认证的为0
                result.put("canMaxAmount", 0);
            } else {
                BigDecimal maxDownload = (BigDecimal) userDownloadLimit.get("maxDownload");
                result.put("canMaxAmount", maxDownload.divide(price, 8, BigDecimal.ROUND_DOWN));
            }
            dayCash = downloadLimit.divide(price, 8, BigDecimal.ROUND_DOWN);
            //转换后的该币种的已经提现数量
            todayCash = todayCashBTC.divide(price, 8, BigDecimal.ROUND_UP);
        } else {
            todayCash = ddao.getTodayCash(userId);
            result.put("canMaxAmount", 0);
        }

        //判断是否开启谷歌验证或者短信验证
        User user = userDao.getUserById(String.valueOf(userId));
        Boolean googleFlag = false;
        Boolean mobileFlag = false;
        if (user.getGoogleOpen()) {
            googleFlag = true;
        }
        if (user.getSmsOpen()) {
            mobileFlag = true;
        }
        result.put("googleOpen", googleFlag);
        result.put("smsOpen", mobileFlag);
        result.put("safePwd", user.getSafePwd());

        result.put("todayCash", todayCash);

        result.put("everyTimeCash", coint.getTimesCash());
        result.put("dayCash", coint.getDayCash());

        BigDecimal thisTimeCouldCash = ddao.getThisTimeCouldCash(payuser.getBalance(), todayCash, everyTimeCash, dayCash);
        BigDecimal canWithdraw = p2pUserDao.getCanWithdraw(userId, loginUser.getUserName(), thisTimeCouldCash, coint.getStag());

        result.put("canWithdraw", canWithdraw);
        result.put("fees", coint.getMinFees());
        result.put("usdtefees", DatabasesUtil.getUsdtAggrement(102).getMinFees());

        result.put("minD", coint.getMinCash());
        result.put("usdteminD", DatabasesUtil.getUsdtAggrement(102).getMinCash());
        BigDecimal todayFreeCash = ddao.getTodayFreeCash(userId);
        BigDecimal couldFreeCash = payuser.getDayFreeCash().subtract(todayFreeCash);
        couldFreeCash = couldFreeCash.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : couldFreeCash;
        result.put("couldFreeCash", couldFreeCash);

        result.put("freeCash", coint.getDayFreetrial());
        //提现确认次数
        result.put("outConfirmTimes", coint.getOutConfirmTimes());

        result.put("codeType", PostCodeType.cash.getKey());
        if (StringUtils.isBlank(loginUser.getSafePwd())) {
            result.put("safeAuth", true);
        }

        result.put("balance", payuser.getBalance());
        result.put("freeze", payuser.getFreez());

//        JSONArray funds = UserCache.getUserFunds(userIdStr());
//        for (int i = 0; i < funds.size(); i++) {
//            JSONObject obj = funds.getJSONObject(i);
//            if (obj.getString("propTag").equals(coint.getPropTag())) {
//                result.put("balance", obj.getBigDecimal("balance").doubleValue());
//                result.put("freeze", obj.getBigDecimal("freeze").doubleValue());
//                break;
//            }
//        }

        int lockStatus = 0;

        //校验设置资金密码登录密码
        DataResponse dr1 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
        if (!dr1.isSuc()) {
            lockStatus = 1;
            result.put("lockStatus", 1);
            result.put("lockTips", L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。"));
        }

        //校验邮箱密码
        DataResponse dr2 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
        if (!dr2.isSuc()) {
            lockStatus = 1;
            result.put("lockStatus", 1);
            result.put("lockTips", L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。"));
        }
        //校验手机验证码
        DataResponse dr3 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_MOBILE, MsgToastKey.LOCK_24_HOUR);
        if (!dr3.isSuc()) {
            lockStatus = 1;
            result.put("lockStatus", 1);
            result.put("lockTips", L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。"));
        }
        //校验谷歌验证码
        DataResponse dr4 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_GOOGLE, MsgToastKey.LOCK_24_HOUR);
        if (!dr4.isSuc()) {
            lockStatus = 1;
            result.put("lockStatus", 1);
            result.put("lockTips", L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。"));
        }
        //校验谷歌验证码是否锁定
        DataResponse dr5 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.WITHDRAWAL_GOOGLE, MsgToastKey.LOCK_24_HOUR);
        if (!dr5.isSuc()) {
            lockStatus = 1;
            result.put("lockStatus", 1);
            result.put("lockTips", L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。"));
        }
        //校验手机验证码是否锁定
        DataResponse dr6 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.WITHDRAWAL_MOBILE, MsgToastKey.LOCK_24_HOUR);
        if (!dr6.isSuc()) {
            lockStatus = 1;
            result.put("lockStatus", 1);
            result.put("lockTips", L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。"));
        }
        //校验资金密码是否锁定
        DataResponse dr7 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.WITHDRAWAL_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
        if (!dr7.isSuc()) {
            lockStatus = 1;
            result.put("lockStatus", 1);
            result.put("lockTips", L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。"));
        }
        //校验邮箱验证码是否锁定
        DataResponse dr8 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.WITHDRAWAL_EMAIL, MsgToastKey.LOCK_24_HOUR);
        if (!dr8.isSuc()) {
            lockStatus = 1;
            result.put("lockStatus", 1);
            result.put("lockTips", L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。"));
        }

        if (loginUser.getSafePwdModifyTimes() > 1 && null != loginUser.getSafePwdModifyTime()
                && TimeUtil.getOriginDiffDay(now(), loginUser.getSafePwdModifyTime()) < 1) {
            lockStatus = 1;
            result.put("lockStatus", 1);
            result.put("lockTips", L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。"));
        }
        /*if(StringUtils.isNotEmpty(Cache.Get(LimitType.withdrawEmail.name()+"_"+loginUser.getUserName())) || StringUtils.isNotEmpty(Cache.Get(LimitType.withdrawSms.name()+"_"+loginUser.getUserName())) || StringUtils.isNotEmpty(Cache.Get(LimitType.withdrawGoogle.name()+"_"+loginUser.getUserName())) || StringUtils.isNotEmpty(Cache.Get(LimitType.withdrawZijin.name()+"_"+loginUser.getUserName()))){
            lockStatus = 1;
            result.put("lockStatus", 1);
            result.put("lockTips", L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。"));
        }
        //验证资金密码
        LimitType lt = LimitType.SafePassEntrustError;
        int entrustStatus = lt.GetStatus(loginUser.get_Id());
        if (entrustStatus == -1) {
            lockStatus = 1;
            result.put("lockStatus", 1);
            result.put("lockTips", L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。"));
        }*/

        receiveDao.setCoint(coint);
        long addressId = longParam("addressId");
        ReceiveAddr receive = receiveDao.getById(addressId);
        if (null == receive || !receive.getUserId().equals(userId)) {
            if (lockStatus == 0) {
                lockStatus = 1;
                result.put("lockStatus", 1);
                result.put("lockTips", L("提币地址不存在"));
            }
        } else {
            result.put("memo", receive.getMemo());
            result.put("address", receive.getAddress());
            result.put("addressTag", receive.getAddressTag());

            if (lockStatus == 0) {
                //提现锁定条件
                //非首次切换模式 && 切换时间<24小时
                //安全模式 && 地址创建时间在24小时之内
                if (loginUser.getWithdrawAddressAuthenSwitchStatus() == 2
                        && TimeUtil.getOriginDiffDay(now(), loginUser.getWithdrawAddressAuthenModifyTime()) < 1) {
                    result.put("lockStatus", 1);
                    result.put("lockTips", L("您的帐户因切换模式被锁定，在此期间不能进行提现操作，请等待24小时后自动解锁。"));
                } else if (loginUser.getWithdrawAddressAuthenType() == WithdrawAddressAuthenType.SECURITY.getKey()
                        && receive.getCreateTime().after(loginUser.getWithdrawAddressAuthenModifyTime())
                        && TimeUtil.getOriginDiffDay(now(), receive.getCreateTime()) < 1) {
                    result.put("lockStatus", 1);
                    result.put("lockTips", L("您当前为“安全模式”，新增提现地址后将被锁定24小时。"));
                } else {
                    result.put("lockStatus", 0);
                }
            }
        }

        json("success", true, com.alibaba.fastjson.JSONObject.toJSONString(result), true);
    }

    /**
     * 新增提现地址
     */
    @Page(Viewer = JSON)
    public void addAddress() {
        try {
            initLoginUser();
            String errorMsg = null;
            String address = request.getParameter("address");
            //地址标签
            String addressTag = request.getParameter("addressTag");
            String mobileCode = param("mobileCode");
            String memo = param("memo");
            String agreement = param("agreement");
            //地址标签长度校验
            if (StringUtils.isNotBlank(memo) && memo.length() > 20) {
                json(L("标签不得超过20个字符"), false, "");
                return;
            }
            if (StringUtils.isNotEmpty(agreement) && DatabasesUtil.getUsdtAggrement(Integer.valueOf(agreement)) == null) {
                json(L("PARAM ERROR"), false, "");
                return;
            }
            int addAddressLock = doGetErrorTimes(loginUser.getId(), LimitType.addAddress);
            if (addAddressLock == -2) {
//            if(StringUtils.isNotEmpty(Cache.Get(LimitType.addAddress.name()+"_"+loginUser.getId()))){
                json(L("新增提现地址功能已被锁定，请24小时之后再试"), false, "");
                return;
            }
            receiveDao.setCoint(coint);
            Message msg = receiveDao.addReceiveAddrNew(lan, loginUser, mobileCode, memo, address, ip(), getLanTag(), errorMsg, addressTag,StringUtils.isEmpty(agreement)?null:Integer.valueOf(agreement));
            if (!msg.isSuc()) {
                json("", msg.isSuc(), L(msg.getMsg()));
                return;
            }
            // 发送MQ消息
            OperateLogInfoProducer.sendAddDownloadAddr(loginUser.getId(), coint.getFundsType(), address);
            json(L("设置成功"), true, "");
        } catch (Exception e) {
            log.error("10100403VIPXZTXSDZ【新增提现地址】 com.world.controller.manage.account.Download#addAddress", e);
            json(L("操作失败"), false, "");
        }
    }

    /**
     * 校验地址信息
     */
    @Page(Viewer = JSON)
    public void addAddressCheck() {
        try {
            String address = request.getParameter("address");
            Boolean check = UserUtil.checkAddress(coint.getStag(), address);
            if (check) {
                json(L("地址校验成功。"), true, "");
            } else {
                json(L("地址校验失败。"), false, "");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
            json(L("操作失败"), false, "");
        }
    }

    //Close By suxinjie 一期屏蔽该功能
    //@Page(Viewer = "/cn/manage/account/download/addbank.jsp")
    public void add() {
        int userId = userId();

        int receiveId = intParam("receiveId");
        if (receiveId > 0) {
            ReceiveAddr receive = (ReceiveAddr) Data.GetOne("SELECT * from " + coint.getStag() + CointTable.receiveaddr + " where userId = ? AND id = ?", new Object[]{userId, receiveId}, ReceiveAddr.class);
            setAttr("receive", receive);
        }

        initLoginUser();
        UserContact uc = loginUser.getUserContact();
        setAttr("googleAuth", uc.getGoogleAu());
        setAttr("mobileStatu", uc.getMobileStatu());
        setAttr("showAudioButton", uc.isShowAudioButton());
        setAttr("codeType", PostCodeType.addAddr.getKey());
    }

    //Close By suxinjie 一期屏蔽该功能，废弃
    //@Page(Viewer = ".xml")
    public void doAdd() {
        try {
            initLoginUser();
            String address = request.getParameter("address");
            String auth = param("auth");
            if ("on".equals(auth)) {
                auth = "1";
            } else {
                auth = "0";
            }
            String safePwd = param("safepwd");
            String mobileCode = param("mobileCode");
            String memo = param("memo");
            int googleCode = intParam("googleCode");
            int receiveId = intParam("receiveId");

            Message msg = receiveDao.doAddReceiveAddr(lan, loginUser, safePwd, mobileCode, googleCode, memo, receiveId, address, ip(), getLanTag());
            if (msg.isSuc()) {
                UserContact uc = loginUser.getUserContact();
                if ("1".equals(auth) && null != uc.getSafeEmail()) {
                    String uuid = MD5.toMD5(UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis());
                    Cache.Set(uuid, "1", 2 * 60 * 60);
                    EmailDao eDao = new EmailDao();
                    String info = eDao.getAddAddrEmailHtml(loginUser, lan, uc.getSafeEmail(), uuid, coint.getTag(), this);
                    SysGroups sg = SysGroups.vip;
                    String title = L(SysGroups.vip.getValue()) + " " + L("认证地址");
                    eDao.sendEmail(ip(), loginUser.getId(), loginUser.getUserName(), title, info, uc.getSafeEmail());
                    msg.setMsg(L("成功添加%%接收地址，请登录您的邮箱进行认证地址确认。", coint.getTag()));
                }
                WriteRight(msg.getMsg());
            } else {
                Write(msg.getCode() + "", false, msg.getMsg());
            }
            new UserDao().clearMobileCode(userIdStr());
        } catch (Exception ex) {
            log.error("内部异常", ex);
            WriteError("error:");
        }
    }

    @Page(Viewer = JSON)
    public void sendAddrAuthEmail() {
        long addrId = longParam("id");
        String currency = param("currency");
        initLoginUser();

        //email发送频率控制 modify renfei
        int uid = userId();
        String key = String.format(Const.MessageSendLimit.EMAIL_SEND_TIME_CACHEKEY, uid);
        long now = System.currentTimeMillis();
        long preSendTime = NumberUtils.toLong(Cache.Get(key), -1);
        if (now - preSendTime < Const.MessageSendLimit.EMAIL_SEND_LIMIT) {
            json(L("邮件发送频繁，请稍候再试。"), false, "");
            return;
        } else {
            Cache.Set(key, String.valueOf(now), (int) (Const.MessageSendLimit.EMAIL_SEND_LIMIT / DateUtils.MILLIS_PER_SECOND) * 2);
        }

        UserContact uc = loginUser.getUserContact();
        if (uc.getEmailStatu() != 2) {
            json("您还没有进行邮箱认证，请进行邮箱认证后重试。", true, "");
            return;
        }

        String uuid = MD5.toMD5(UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis());
        Cache.Set(uuid, addrId + "", 2 * 60 * 60);
        EmailDao eDao = new EmailDao();
        String info = eDao.getAddAddrEmailHtml(loginUser, lan, uc.getSafeEmail(), uuid, currency.toUpperCase(), this);
        log.info(VIP_DOMAIN + "/ac/authAddress?auth=" + uuid + "&type=" + currency.toUpperCase() + "&needlogin=true");
        SysGroups sg = SysGroups.vip;
        String title = L(SysGroups.vip.getValue()) + " " + L("认证地址");
        eDao.sendEmail(ip(), loginUser.getId(), loginUser.getUserName(), title, info, uc.getSafeEmail());
        json("成功发送认证邮件，请登录您的邮箱进行认证地址确认。", true, "");
    }

    @Page(Viewer = JSON)
    public void doDel() {
        try {
            long id = longParam("receiveId");

            int count = Data.Update("update " + coint.getStag() + CointTable.receiveaddr + " set isdeleted = 1 where id=? and userid=?", new Object[]{id, userIdStr()});
            if (count > 0) {
                json(L("删除成功"), true, "");
            } else {
                json(L("删除失败"), true, "");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    /**
     * 修改提现地址
     */
    @Page(Viewer = JSON)
    public void updateReceiveAddr() {
        try {
            long id = longParam("receiveId");
            String memo = param("memo");
            //地址标签长度校验
            if (StringUtils.isNotBlank(memo) && memo.length() > 20) {
                json(L("标签不得超过20个字符"), false, "");
                return;
            }

            int count = Data.Update("update " + coint.getStag() + CointTable.receiveaddr + " set memo = ? where id=? and userid=?", new Object[]{memo, id, userIdStr()});
            if (count > 0) {
                json(L("修改成功"), true, "");
            } else {
                json(L("修改失败"), true, "");
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    //Close By suxinjie 一期屏蔽该功能
//	@Page(Viewer = XML)
    public void setDefault() {
        try {
            long id = longParam("receiveId");
            String userId = userIdStr();

            ReceiveAddr addr = (ReceiveAddr) Data.GetOne("select * from " + coint.getStag() + CointTable.receiveaddr + " where userid=? AND id = ? and isdeleted = 0", new Object[]{userId, id}, ReceiveAddr.class);
            if (addr == null || addr.getIsDefault() == 1) {
                WriteRight("");
                return;
            }

            List<OneSql> sqls = new ArrayList<OneSql>();
            sqls.add(new OneSql("update " + coint.getStag() + CointTable.receiveaddr + " set isDefault = 1 where id=? and userid=?", 1, new Object[]{id, userId}));
            sqls.add(new OneSql("update " + coint.getStag() + CointTable.receiveaddr + " set isDefault = 0 where id<>? and userid=? AND isDeleted = 0", -2, new Object[]{id, userId}));

            if (Data.doTrans(sqls)) {
                WriteRight(L("操作成功"));
            } else {
                WriteError(L("操作失败"));
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }


    /*
     * 提现校验
     * */
    @Page(Viewer = JSON)
    public void submit() {
        try {
            String payPass = param("safePwd");
            String mobileCode = param("mobileCode");
            String emailCode = param("emailCode");
            String googleCode = param("googleCode");
            String errorMsg = null;
            BigDecimal amount = decimalParam("amount");
            int fundsType = intParam("fundsType");
            initLoginUser();
            if (loginUser.getCustomerOperation().equals(Const.CUSTOMER_OPERATION_NO_CASH)) {
                json(L("提交失败，当前账户无提现权限"), false, "");
                return;
            }
            if (StringUtils.isEmpty(payPass)) {
                json(L("请输入资金密码"), false, "");
                return;
            }
            if (StringUtils.isEmpty(mobileCode) && StringUtils.isEmpty(googleCode)) {
                //二级安全验证不能为空 至少要有一个
                json(L("失败"), false, "");
                return;
            }
            //资金密码解密
            RsaUser rsaUser = RsaLoginUtil.getRsaUser(this);
            byte[] decodedData2 = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(payPass), rsaUser.getPriKey());
            payPass = new String(decodedData2);

            //校验设置资金密码登录密码
            DataResponse dr1 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
            if (!dr1.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr1.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }

            //校验邮箱密码
            DataResponse dr2 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
            if (!dr2.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr2.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }
            //校验手机验证码
            DataResponse dr3 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_MOBILE, MsgToastKey.LOCK_24_HOUR);
            if (!dr3.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr3.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }
            //校验谷歌验证码
            DataResponse dr4 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_GOOGLE, MsgToastKey.LOCK_24_HOUR);
            if (!dr4.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr4.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }
            //校验谷歌验证码是否锁定
            DataResponse dr5 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.WITHDRAWAL_GOOGLE, MsgToastKey.LOCK_24_HOUR);
            if (!dr5.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr5.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }
            //校验手机验证码是否锁定
            DataResponse dr6 = this.checkVerifiCode(loginUser.get_Id(), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.WITHDRAWAL_MOBILE, MsgToastKey.LOCK_24_HOUR);
            if (!dr6.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr6.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }
            //校验提现资金密码
            DataResponse dr7 = this.checkVerifiCode(userIdStr(), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.WITHDRAWAL_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
            if (!dr7.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr7.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }
            //校验提现资金密码
            DataResponse dr8 = this.checkVerifiCode(userIdStr(), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.WITHDRAWAL_EMAIL, MsgToastKey.LOCK_24_HOUR);
            if (!dr8.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr8.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }

            //验证码类型
            int icodeType = intParam("codeType");
            PostCodeType postCodeType = (PostCodeType) EnumUtils.getEnumByKey(icodeType, PostCodeType.class);
            errorMsg = doCheck(errorMsg, loginUser.get_Id(), payPass, emailCode, googleCode, mobileCode, MsgToastKey.WITHDRAWAL, MsgToastKey.LOCK_24_HOUR, postCodeType);
            //验证资金密码
        /*LimitType lt = LimitType.SafePassEntrustError;
        int entrustStatus = lt.GetStatus(loginUser.get_Id());
        if (entrustStatus == -1) {
            errorMsg = CommonUtil.mapToJsonStr(errorMsg,"payPwd",L("资金密码输入错误超出限制，锁定该帐户24小时，不得使用重置资金密码和提现功能"));
//            if(StringUtils.isNotEmpty(Cache.Get(LimitType.withdrawZijin.name()+"_" + loginUser.getUserName()))){
//                Cache.Set(LimitType.withdrawZijin.name()+"_" + loginUser.getUserName(),"1",24 * 60 * 60);
//            }
        }
        int status = new UserDao().checkSecurityDownload(payPass, loginUser.get_Id());
        if (status == -2) {
            errorMsg = CommonUtil.mapToJsonStr(errorMsg,"payPwd",L("资金密码输入错误超出限制，锁定该帐户24小时，不得使用重置资金密码和提现功能"));
            if(StringUtils.isEmpty(Cache.Get(LimitType.withdrawZijin.name()+"_" + loginUser.getUserName()))){
                Cache.Set(LimitType.withdrawZijin.name()+"_" + loginUser.getUserName(),"1",24 * 60 * 60);
            }
        } else if (status == 0) {
            json("资金密码未设置", false, "");
            return;
        } else if (status == -3) {
            *//*json("", true, "");
            return;*//*
        } else {
            errorMsg = CommonUtil.mapToJsonStr(errorMsg,"payPwd",L("资金密码输入有误，您还有%%次机会", status + ""));
        }
        //检查谷歌验证码
        if(StringUtils.isNotEmpty(googleCode)){
            Message message = isCorrectMsg(loginUser.getUserContact().getSecret(), Long.parseLong(googleCode), JSON, loginUser.get_Id(),null);
            if(Lan.LanguageFormat(lan, "验证码输入错误次数超出限制，锁定该帐户24小时，不得使用提现功能。", "").equals(message.getMsg())){
                if(StringUtils.isEmpty(Cache.Get(LimitType.withdrawGoogle.name()+"_" + loginUser.getUserName()))){
                    Cache.Set(LimitType.withdrawGoogle.name()+"_" + loginUser.getUserName(),"1",24 * 60 * 60);
                }
            }
            if(!message.isSuc()){
                errorMsg = CommonUtil.mapToJsonStr(errorMsg,"gcode",message.getMsg());
            }

        }
        //检查邮箱验证码
        int sendType = 1;
        String codeRecvAddr = loginUser.getUserName();
        int icodeType = intParam("codeType");//验证码类型
        PostCodeType postCodeType = (PostCodeType) EnumUtils.getEnumByKey(icodeType, PostCodeType.class);
        String codeType = postCodeType.getValue();
        DataResponse dr = null;
        if(codeRecvAddr != null) {
            ClientSession clientSession = new ClientSession(ip(), codeRecvAddr, lan, codeType, false);
            dr = clientSession.checkOnlyCode(emailCode);
            if(dr.getDes().equals(Lan.LanguageFormat(lan, "验证码输入错误次数过多,已被锁定,请24小时后重试。", ""))){
                dr.setDes(Lan.LanguageFormat(lan, "验证码输入错误次数超出限制，锁定该帐户24小时，不得使用提现功能。", ""));
                if(StringUtils.isEmpty(Cache.Get(LimitType.withdrawEmail.name()+"_" + loginUser.getUserName()))){
                    Cache.Set(LimitType.withdrawEmail.name()+"_" + loginUser.getUserName(),"1",24 * 60 * 60);
                }
            }
            if(!dr.isSuc()){
                errorMsg = CommonUtil.mapToJsonStr(errorMsg,"emailcode",dr.getDes());
            }
        }
        if(StringUtils.isNotEmpty(mobileCode)) {
            ClientSession clientSession = new ClientSession(ip(), loginUser.getUserContact().getSafeMobile(), lan, codeType, false);
            dr = clientSession.checkOnlyCode(mobileCode);
            if(dr.getDes().equals(Lan.LanguageFormat(lan, "验证码输入错误次数过多,已被锁定,请24小时后重试。", ""))){
                dr.setDes(Lan.LanguageFormat(lan, "验证码输入错误次数超出限制，锁定该帐户24小时，不得使用提现功能。", ""));
                if(StringUtils.isEmpty(Cache.Get(LimitType.withdrawSms.name()+"_" + loginUser.getUserName()))){
                    Cache.Set(LimitType.withdrawSms.name()+"_" + loginUser.getUserName(),"1",24 * 60 * 60);
                }
            }
            errorMsg = CommonUtil.mapToJsonStr(errorMsg,"smscode",dr.getDes());
        }*/
            /*Start by guankaili 20190107 币币划转到我的钱包 */
//        String url = ApiConfig.getValue("usecenter.url");
//        FeignContainer container = new FeignContainer(url+"/capitalTransfer");
//        CapitalTransferApiService capitalTransferApiService = container.getFeignClient(CapitalTransferApiService.class);
//        Boolean flag = capitalTransferApiService.transfer(Const.pay_user_bg, Const.pay_user_wallet, amount, coint.getFundsType(), userIdStr());
//        //划转完成
//        if (flag) {
//            log.info("用户"+loginUser.getUserName()+"("+userIdStr()+")划转成功");
//
//        }else{
//            json("", false, "用户"+loginUser.getUserName()+"("+userIdStr()+")划转失败");
//            log.info("用户"+loginUser.getUserName()+"("+userIdStr()+")划转失败");
//        }
            /*End*/
//        UserCache.resetUserWalletFundsFromDatabase(userIdStr());
//        PayUserWalletBean  payUserWallet = dao.getById(userIdStr()+"", coint.getFundsType());

            //配置文件中每次最多可提现数量
            BigDecimal everyTimeCash = coint.getTimesCash();
            if (everyTimeCash.compareTo(BigDecimal.ZERO) > 0 && everyTimeCash.compareTo(amount) < 0) {
                /*Start by guankaili 20181123 优化提现提示语句逻辑 */
//                BigDecimal overNum = everyTimeCash.subtract(amount).abs();
                String returnMsg = Lan.LanguageFormat(lan, "超出币种单笔提现额度", new String[]{everyTimeCash + "", coint.getPropEnName()});
//                errorMsg = CommonUtil.mapToJsonStr(errorMsg,"cashAmount","超出币种单笔提现额度");
                errorMsg = CommonUtil.mapToJsonStr(errorMsg, "cashAmount", returnMsg);
                /*end*/
            }

            //该币种今天已经提现数量
//        BigDecimal todayCash = BigDecimal.ZERO;
//        //该币种今天可以提现总数，默认配置文件值
//        BigDecimal dayCash = coint.getDayCash();
//
//        //获取币种对btc价格
//        BigDecimal price = Price.getCoinBtcPrice(coint.getStag());
//        //如果该币种有市场，根据总额度计算提现数据
//        if(price.compareTo(BigDecimal.ZERO) > 0){                //获取用户总提现额度，跟实名认证关联，btc
//            Map<String, Object> userDownloadLimit = downloadSummaryDao.getDownloadLimit(loginUser.getId());
//            BigDecimal downloadLimit = (BigDecimal) userDownloadLimit.get("downloadLimit");
//
//            dayCash = downloadLimit.divide(price, 8, BigDecimal.ROUND_DOWN);
//            //获取今天已经提现数量，btc
//            BigDecimal todayCashBTC = downloadSummaryDao.getTodayBtcAmount(userIdStr());
//            //转换后的该币种的已经提现数量
//            todayCash = todayCashBTC.divide(price, 8, BigDecimal.ROUND_UP);
//        }else{
//            todayCash = ddao.getTodayCash(userIdStr());
//        }

            /*Start by guankaili 20190110 额度校验在此不校验 */
            //取最小本次提现值
//        BigDecimal thisTimeCouldCash = ddao.getThisTimeCouldCash(payUserWallet.getBalance(), todayCash, everyTimeCash, dayCash);
//        //取最小允许提现值
//        BigDecimal canWithdraw = p2pUserDao.getCanWithdraw(userIdStr()+"", loginUser.getUserName(), thisTimeCouldCash, coint.getStag());
//
//        if (canWithdraw.compareTo(amount) < 0) {
//            String returnMsg = Lan.LanguageFormat(lan, "每日提现额度", new String[]{coint.getPropEnName(),dayCash + ""});
//            errorMsg = CommonUtil.mapToJsonStr(errorMsg,"cashAmount",returnMsg);
//        }
            /*End*/
            if (StringUtils.isNotEmpty(errorMsg)) {
                json("", false, errorMsg);
                return;
            }
            /*Start by guankaili 20181213 添加防跳步逻辑 */
            String token = EncryptionPhoto.getToken(Const.function_cash_withdrawal, loginUser.getId());
            json(token, true, "");
            /*End*/
            return;
        } catch (Exception e) {
            log.error("10100403VIPTXJY【提现校验】 com.world.controller.manage.account.Download#submit", e);
        }
        json(L("失败"), false, "");
    }


    /**
     * check提现校验
     *
     * @param msg
     * @param userId
     * @param safePwd
     * @param emailCode
     * @param googleCode
     * @param postCodeType
     */
    private String doCheck(String msg, String userId, String safePwd, String emailCode, String googleCode, String smsCode, String functionName, String lockTime, PostCodeType postCodeType) {
        //校验登录密码
        String uftPayPwd = null;
        //校验谷歌
        String uftGoogle = null;
        //邮箱锁定校验
        LimitType ltEmail = null;
        //手机锁定校验
        LimitType ltMobile = null;
        //邮件typeCode
//        PostCodeType pct = null;
        //短信typeCode
//        PostCodeType pctMobile = null;
        //超过三次限制提示
        String googleMsgToast = "";
        String payPwdMsgToast = "";
        uftPayPwd = String.valueOf(ConstantCenter.UpdFunctionType.WITHDRAWAL_PAY_PWD.getKey());
        uftGoogle = String.valueOf(ConstantCenter.UpdFunctionType.WITHDRAWAL_GOOGLE.getKey());
        ltEmail = LimitType.WithdrawEmailPassError;
        ltMobile = LimitType.WithdrawMobilePassError;
//        pct = PostCodeType.updateLoginPassword;
//        pctMobile = PostCodeType.updateLoginPasswordByMoblie;
        googleMsgToast = "验证码输入错误次数超出限制，锁定该帐户24小时，不得使用提现功能。";
        payPwdMsgToast = "资金密码输入错误超出限制，锁定该帐户24小时，不得使用重置资金密码和提现功能";

        String url = ApiConfig.getValue("usecenter.url");
        FeignContainer container = new FeignContainer(url.concat("/user"));
        UserApiService userApiService = container.getFeignClient(UserApiService.class);
        Map<String, String> map = userApiService.checkPayPwdApiN(userId, safePwd, uftPayPwd);
        User user = userDao.getById(userId);
        if (null != map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (!"1".equals(entry.getKey())) {
                    String returnVal = entry.getValue();
                    if ("-2".equals(returnVal)) {
                        returnVal = L(payPwdMsgToast);
                        EmailDao eDao = new EmailDao();
                        String info = "";
                        String title = "";
                        info = eDao.getWrongLimitEmailHtml(user, returnVal, this);
                        title = L("提现");
                        //锁定发送邮件
                        eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, user.getUserContact().getSafeEmail());
                    } else {
                        String[] errorMsg = returnVal.split("#");
                        if (errorMsg.length == 2) {
                            returnVal = L(errorMsg[0], errorMsg[1]);
                        } else {
                            returnVal = L(errorMsg[0]);
                        }
                    }
                    msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.payPwd, returnVal);
                }
            }
        }
        //邮箱验证
        /*zwt 提现不再进行邮箱验证 20190726**/

//        DataResponse dr = getDataResponse(user,postCodeType,user.getEmail(),1,emailCode,ltEmail,functionName,lockTime);
//        if (!dr.isSuc()) {
//            msg = CommonUtil.mapToJsonStr(msg,MsgToastKey.emailCode,dr.getDes());
//        }

        if (StringUtils.isNotEmpty(googleCode)) {
            //谷歌校验
            FeignContainer container1 = new FeignContainer(url.concat("/google"));
            GoogleApiService googleApiService = container1.getFeignClient(GoogleApiService.class);
            Map<String, String> map1 = googleApiService.checkGoogleCodeApiN(googleCode, user.getUserContact().getSecret(), userId, uftGoogle);
            for (Map.Entry<String, String> entry1 : map1.entrySet()) {
                if (!"1".equals(entry1.getKey())) {
                    String returnVal = entry1.getValue();
                    if ("-2".equals(returnVal)) {
                        returnVal = L(googleMsgToast);
                    } else {
                        returnVal = returnVal.replace("谷歌", "");
                        String[] errorMsg = returnVal.split("#");
                        if (errorMsg.length == 2) {
                            returnVal = L(errorMsg[0], errorMsg[1]);
                        } else {
                            returnVal = L(errorMsg[0]);
                        }
                    }
                    msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.googleCode, returnVal);
                }
            }
        }
        if (StringUtils.isNotEmpty(smsCode)) {
            //手机验证
            DataResponse dr1 = getDataResponse(user, postCodeType, user.getUserContact().getSafeMobile(), 2, smsCode, ltMobile, functionName, lockTime);
            if (!dr1.isSuc()) {
                msg = CommonUtil.mapToJsonStr(msg, MsgToastKey.smsCode, dr1.getDes());
            }
        }
        /*zwt end**/

        return msg;
    }


    /**
     * 初始化配置信息
     */
    public List<String> initConfig() {
        try {
            /*提现审核提醒手机号*/
            List<String> noticePhoneList = null;
//            获取手机号列表
            List<CommAttrBean> commAttrBeanList = commAttrDao.queryListByAttrTypeAndParaCode(AddressType.AUTO_DOWNLOAD_ACCOUNT_NOTICE.getKey(), "01");
            noticePhoneList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(commAttrBeanList)) {
                for (CommAttrBean commAttrBean : commAttrBeanList) {
                    noticePhoneList.add(commAttrBean.getParaValue());
                }
            }
            return noticePhoneList;
        } catch (Exception e) {
            log.error("获取充值到帐接受人员手机号初始化配置信息出错，错误信息：", e);
        }
        return null;
    }


    /**
     * 提现
     */
    @Page(Viewer = JSON)
    public void doSubmit() {
        try {
//			initLoginUser();
//			DownloadDao downloadDao = new DownloadDao();
            initLoginUser();
//            DownloadDao downloadDao = new DownloadDao();
            int userId = userId();
            Integer fundsType = intParam("fundsType");
            CoinProps coinProps = DatabasesUtil.coinProps(fundsType);
            if (null != coinProps) {
                //是否支持提现
                boolean canWithdraw = coinProps.isCanWithdraw();
                if (!canWithdraw) {
                    json(L("%%提现暂停", coinProps.getPropTag()), false, "");
                    return;
                }
            }
            //校验提现资金密码
            DataResponse dr0 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.WITHDRAWAL_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
            if (!dr0.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr0.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }
            //验证资金密码
            LimitType lt = LimitType.SafePassEntrustError;
            int entrustStatus = lt.GetStatus(loginUser.get_Id());
            if (entrustStatus == -1) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr0.getDes());
                json(L("资金密码输入错误超出限制，锁定该帐户24小时，不得使用重置资金密码和提现功能"), mesg.isSuc(), mesg.getData());
                return;
//            if(StringUtils.isNotEmpty(Cache.Get(LimitType.withdrawZijin.name()+"_" + loginUser.getUserName()))){
//                Cache.Set(LimitType.withdrawZijin.name()+"_" + loginUser.getUserName(),"1",24 * 60 * 60);
//            }
            }
            //校验设置资金密码登录密码
            DataResponse dr1 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
            if (!dr1.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr1.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }

            //校验邮箱密码
            DataResponse dr2 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
            if (!dr2.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr2.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }
            //校验手机验证码
            DataResponse dr3 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_MOBILE, MsgToastKey.LOCK_24_HOUR);
            if (!dr3.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr3.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }
            //校验谷歌验证码
            DataResponse dr4 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_GOOGLE, MsgToastKey.LOCK_24_HOUR);
            if (!dr4.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr4.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }
            //校验谷歌验证码是否锁定
            DataResponse dr5 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.WITHDRAWAL_GOOGLE, MsgToastKey.LOCK_24_HOUR);
            if (!dr5.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr5.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }
            //校验手机验证码是否锁定
            DataResponse dr6 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.WITHDRAWAL_MOBILE, MsgToastKey.LOCK_24_HOUR);
            if (!dr6.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr6.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }
            //校验提现资金密码
            DataResponse dr8 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.WITHDRAWAL_EMAIL, MsgToastKey.LOCK_24_HOUR);
            if (!dr8.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr8.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }

            //otc发布广告资金密码输错
            DataResponse dr9 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.OTC_CAD_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
            if (!dr9.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr9.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }
            //otc发布广告资金密码输错
            DataResponse dr10 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.OTC_RELEASECOIN_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
            if (!dr10.isSuc()) {
                Message mesg = new Message();
                mesg.setMsg(lan, dr10.getDes());
                json(L(mesg.getMsg()), mesg.isSuc(), mesg.getData());
                return;
            }

            if (userId <= 0) {
                json(L("提交失败，请稍后重试"), false, "");
                return;
            }
            BigDecimal account = decimalParam("cashAmount").setScale(8, BigDecimal.ROUND_DOWN);
//            /*start by xzhang 20170829 明确指明过滤该种类型的账户不允许提现 JYPT-1246*/
//            if (loginUser.getCustomerOperation().contains(Const.CUSTOMER_OPERATION_NO_CASH)) {
//                json(L("提交失败，当前账户无提现权限"), false, "");
//                return;
//            }
//            /**end*/
//			if(!googleNoSetTips()){
//				json(L("系统检测到您没有进行手机认证或Google认证，暂时不能进行提现业务，为了您的账号安全，请进行手机认证或Google认证。"), false, "");
//				return;
//			}
//
//            if (coint.getDatabaseKey().equalsIgnoreCase("neo")) {
//                if (account.compareTo(account.setScale(0, BigDecimal.ROUND_DOWN)) != 0) {
//                    json(L("NEO提现数量不允许存在小数"), false, "");
//                    return;
//                }
//            }
            String receiveAddr = request.getParameter("receiveAddress");
            BigDecimal fees = decimalParam("fees").setScale(8, BigDecimal.ROUND_DOWN);
            String payPass = param("safePwd");
            //资金密码解密
            RsaUser rsaUser = RsaLoginUtil.getRsaUser(this);
            byte[] decodedData2 = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(payPass), rsaUser.getPriKey());
            payPass = new String(decodedData2);
            String mobileCode = param("mobileCode");
            String emailCode = param("emailCode");
            String googleCode = param("googleCode");

            String liuyan = request.getParameter("liuyan");
            String memo = request.getParameter("memo");
            String opUnique = loginUser.get_Id() + "_" + param("opUnique");


            //验证提现地址是否锁定
            ReceiveAddr bab = (ReceiveAddr) Data.GetOne("select * from " + coint.getStag() + CointTable.receiveaddr + " where userId=? and address=? and isDeleted=0", new Object[]{userId, receiveAddr}, ReceiveAddr.class);
            Message msgAddress = new Message();
            if (bab == null) {
                msgAddress.setCode(SystemCode.code_1001.getKey());
                msgAddress.setMsg(lan, "提币地址不存在");
                msgAddress.setSuc(false);
                json(L(msgAddress.getMsg()), msgAddress.isSuc(), msgAddress.getData());
                log.error("提币地址不存在 " + bab);
                return;
            }
            if(bab.getAgreement() != null){
                coinProps = DatabasesUtil.getUsdtAggrement(bab.getAgreement());
            }
            if (loginUser.getWithdrawAddressAuthenType() == WithdrawAddressAuthenType.SECURITY.getKey()
                    && bab.getCreateTime().after(loginUser.getWithdrawAddressAuthenModifyTime())
                    && TimeUtil.getOriginDiffDay(now(), bab.getCreateTime()) < 1) {
                msgAddress.setCode(SystemCode.code_1001.getKey());
                msgAddress.setMsg("提现地址已经锁定");
                msgAddress.setSuc(false);
                json(L(msgAddress.getMsg()), msgAddress.isSuc(), msgAddress.getData());
                return;
            }

//            String sql = "select userId, userVID,authPayFlag from fin_userfinancialinfo where userId = ?";
//            UserFinancialInfo userFinancialInfo = null;
//            userFinancialInfoDao.setDatabase("vip_financial");
//            userFinancialInfo = (UserFinancialInfo) userFinancialInfoDao.get(sql,new Object[]{userId},UserFinancialInfo.class);
//            if(userFinancialInfo != null){
//                if(userFinancialInfo.getAuthPayFlag() != 1 && StringUtils.isNotEmpty(userFinancialInfo.getUserVID())){
//                    log.info("当前用户已设置VID地址"+userId);
//                }else{
//                    log.info("当前用户未设置VID地址"+userId);
//                    json(L("请绑定VID地址"), false, "");
//                    return;
//                }
//            }
//            Message message = downloadDao.checkCode(emailCode, 1, 8, loginUser, ip());
//            if (!message.isSuc()) {
//                json(L(message.getMsg()), message.isSuc(), message.getData());
//                return;
//            }
            // 不是手机API，验证
            boolean dynamicPass = false;
            // 检查短信验证码
            if (loginUser.getSmsOpen() && mobileCode != "") {
//                Message msg = downloadDao.checkCode(mobileCode, 2, 8, loginUser, ip());
//                if (!msg.isSuc()) {
//                    json(L(msg.getMsg()), msg.isSuc(), msg.getData());
//                    return;
//                }
                dynamicPass = true;
                String key = loginUser.getId() + "_gauth";
                Cache.Delete(key);
                if (StringUtils.isNotEmpty(loginUser.getUserContact().getSafeMobile())) {
                    ClientSession clientSession = new ClientSession(ip(), loginUser.getUserContact().getSafeMobile(), lan, "8", false);
                    clientSession.deleteSession(2);
                    clientSession.clearDeviceNumberLock(loginUser.getUserContact().getSafeMobile());
                }
            }
            if (loginUser.getGoogleOpen() && StringUtils.isNotBlank(googleCode) && !dynamicPass) {
                Message mesg = new Message();
                //谷歌验证
                UserContact userContact = loginUser.getUserContact();
                String secret = userContact.getSecret();
                Map<String, String> flagMap = new HashMap<String, String>();
                FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url") + "/google");
                GoogleApiService downloadSubmitService = container.getFeignClient(GoogleApiService.class);
                flagMap = downloadSubmitService.checkGoogleCodeApiN(googleCode, secret, String.valueOf(userId), String.valueOf(ConstantCenter.UpdFunctionType.WITHDRAWAL_GOOGLE.getKey()));
                String key = "";
                String returnVal = "";
                for (Map.Entry<String, String> entry : flagMap.entrySet()) {
                    key = entry.getKey();
                    returnVal = entry.getValue().toString();
                }
                if (key.equals("1")) {
                    mesg.setMsg(lan, returnVal);
                } else {
                    if ("-2".equals(returnVal)) {
                        returnVal = L("谷歌验证码输入错误超出限制，锁定该帐户24小时，不得使用重置资金密码和提现功能");
                    } else {
                        returnVal = returnVal.replace("谷歌", "");
                        String[] errorMsg = returnVal.split("#");
                        if (errorMsg.length == 2) {
                            returnVal = L(errorMsg[0], errorMsg[1]);
                        } else {
                            returnVal = L(errorMsg[0]);
                        }
                    }
                    json(returnVal, mesg.isSuc(), mesg.getData());
                    return;
                }
                /*Start by guankaili 20181213 添加防跳步逻辑 */
                //防跳步
//                String token = param("token");
//                boolean flg = EncryptionPhoto.checkToken(Const.function_cash_withdrawal,userIdStr(),token);
//                if(!flg){
//                    json(L("非法操作"), false, "");
//                    return;
//                }
                /*end*/
                dynamicPass = true;
                String keyG = loginUser.getId() + "_gauth";
                Cache.Delete(keyG);
                if (StringUtils.isNotEmpty(loginUser.getUserContact().getSafeMobile())) {
                    ClientSession clientSession = new ClientSession(ip(), loginUser.getUserContact().getSafeMobile(), lan, "8", false);
                    clientSession.deleteSession(2);
                    clientSession.clearDeviceNumberLock(loginUser.getUserContact().getSafeMobile());
                }
            }
            /*if (loginUser.getUserContact().getGoogleAu() == 2 || loginUser.getUserContact().getMobileStatu() == 2) {
                if (!dynamicPass) {
                    json(L("您未开启短信和Google提现验证，请开启短信或Google提现验证后重试"), false, "");
                    return;
                }
            }*/
            DownloadVo downloadVo = new DownloadVo();
            downloadVo.setUserId(String.valueOf(userId));
            downloadVo.setCashAmount(account);
            downloadVo.setFees(fees);
            downloadVo.setPropTag(coinProps.getPropTag().toLowerCase());
            downloadVo.setReceiveAddress(receiveAddr);
            downloadVo.setSafePwd(payPass);
            downloadVo.setMobileCode(mobileCode);
            downloadVo.setEmailCode(emailCode);
            downloadVo.setGoogleCode(googleCode);
            downloadVo.setLiuyan(liuyan);
            downloadVo.setMemo(memo);
            downloadVo.setOpUnique(opUnique);
            downloadVo.setFundsType(fundsType);
            downloadVo.setIp(ip());
            FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url") + "/download");
            DowoloadApiService dowoloadApiService = container.getFeignClient(DowoloadApiService.class);
            R r = dowoloadApiService.doSubmit(downloadVo);

            log.info("download uc return:" + JSONObject.toJSONString(r));
            if (r.getFlag()) {

                /*Start by gkl 充值到帐后发送短信到手机 后期会删除*/
                //发短信
                try {
                    List<String> noticePhoneList = initConfig();
                    MobileDao mDao = new MobileDao();
                    //钉钉通知
                    String finalContent = "用户：" + loginUser.getUserName() + ",用户ID【" + userId + "】，于" + TimeUtil.getDateToString(new Date()) + "申请提现【" + account.toPlainString() + "】【" + coint.getTag() + "】，请客服人员立即处理。";
                    log.info("10100401VIPTX【提现审核】:" + finalContent);
//                    noticePhoneList.forEach(noticePhone -> {
//                        mDao.sendSms(loginUser, ip(), Lan.Language(lan, "充值到账提醒"), finalContent, "+86 ".concat(noticePhone));
//                    });
                    for (String noticePhone : noticePhoneList) {
                        mDao.sendSms(loginUser, ip(), Lan.Language(lan, "充值到账提醒"), finalContent, "+86 ".concat(noticePhone));
                    }
                } catch (Exception e) {
                    log.error("发送小额打币提醒失败，错误信息：", e);
                }
                /*End*/
                json(L(r.getMsg()), true, "");
                return;
            } else {
                json(L(r.getMsg(), r.getParam()), false, "");
                return;
            }


//			int userId = userId();
//			if (userId <= 0) {
//				json(L("提交失败，请稍后重试"), false, "");
//				return;
//			}
//			/*start by xzhang 20170829 明确指明过滤该种类型的账户不允许提现 JYPT-1246*/
//			if(loginUser.getCustomerOperation().contains(Const.CUSTOMER_OPERATION_NO_CASH)){
//				json(L("提交失败，当前账户无提现权限"), false, "");
//				return;
//			}
//			/**end*/
//			if(!googleNoSetTips()){
//				json(L("系统检测到您没有进行手机认证或Google认证，暂时不能进行提现业务，为了您的账号安全，请进行手机认证或Google认证。"), false, "");
//				return;
//			}
//			BigDecimal account = decimalParam("cashAmount").setScale(8, BigDecimal.ROUND_DOWN);
//			if(coint.getDatabaseKey().equalsIgnoreCase("neo")){
//				if(account.compareTo(account.setScale(0, BigDecimal.ROUND_DOWN)) != 0){
//					json(L("NEO提现数量不允许存在小数"), false, "");
//					return;
//				}
//			}
//			BigDecimal fees = decimalParam("fees").setScale(8, BigDecimal.ROUND_DOWN);
//			String payPass = param("safePwd");
//			String mobileCode = param("mobileCode");
//			String emailCode = param("emailCode");
//			long googleCode = longParam("googleCode");
//			String liuyan = request.getParameter("liuyan");
//			if (null == liuyan || "".equals(liuyan)) {
//				liuyan = "用户提现"+coint.getTag();
//			}
//			String memo = request.getParameter("memo");
//			if (StringUtils.isNotBlank(memo) && memo.length() > 10) {
//				json(L("备注不得超过10个字符"), false, "");
//				return;
//			}
//
//			//add by suxinjie 20170622 添加提币限制
//			PayUserWalletBean  payuser = dao.getById(userId+"", coint.getFundsType());
//
//            //配置文件中每次最多可提现数量
//            BigDecimal everyTimeCash = payuser.getTimesCash();
//            if (everyTimeCash.compareTo(BigDecimal.ZERO) > 0 && everyTimeCash.compareTo(account) < 0) {
//                json(L("单笔限额最多为%%%%", everyTimeCash.toPlainString(), coint.getPropTag()), false, "");
//                return;
//            }
//
//            //该币种今天已经提现数量
//            BigDecimal todayCash = BigDecimal.ZERO;
//            //该币种今天可以提现总数，默认配置文件值
//            BigDecimal dayCash = payuser.getDayCash();
//
//            //获取币种对btc价格
//            BigDecimal price = Price.getCoinBtcPrice(coint.getStag());
//            //如果该币种有市场，根据总额度计算提现数据
//            if(price.compareTo(BigDecimal.ZERO) > 0){                //获取用户总提现额度，跟实名认证关联，btc
//                Map<String, Object> userDownloadLimit = downloadSummaryDao.getDownloadLimit(loginUser.getId());
//                BigDecimal downloadLimit = (BigDecimal) userDownloadLimit.get("downloadLimit");
//
//                dayCash = downloadLimit.divide(price, 8, BigDecimal.ROUND_DOWN);
//                //获取今天已经提现数量，btc
//                BigDecimal todayCashBTC = downloadSummaryDao.getTodayBtcAmount(String.valueOf(userId));
//                //转换后的该币种的已经提现数量
//                todayCash = todayCashBTC.divide(price, 8, BigDecimal.ROUND_UP);
//            }else{
//                todayCash = ddao.getTodayCash(String.valueOf(userId));
//            }
//
//			BigDecimal thisTimeCouldCash = ddao.getThisTimeCouldCash(payuser.getBalance(), todayCash, everyTimeCash, dayCash);
//			BigDecimal canWithdraw = p2pUserDao.getCanWithdraw(userId+"", loginUser.getUserName(), thisTimeCouldCash, coint.getStag());
//
//            if (canWithdraw.compareTo(account) < 0) {
//
//                json(L("24小时内最多可再次提现%%%%。", canWithdraw.toPlainString(), coint.getPropTag()), false, "");
//                return;
//            }
//
//			String ip = ip();
//			Message msgR = ddao.doBtcDownload(loginUser, account, receiveAddr, fees, payPass, mobileCode, googleCode, ip, liuyan, getLanTag(), false, false, opUnique, lan, memo,emailCode);
//			json(L(msgR.getMsg()), msgR.isSuc(), msgR.getData());
        } catch (Exception e) {
            log.error("10100404VIPTX【提现】 com.world.controller.manage.account.Download#doSubmit", e);
            json(L("未知错误导致提现失败"), false, "");
        }
    }

    /**
     * 提现页面新增接口，注意新版改造的jsp接口没有改这个json接口，如果以后要用，参考jsp接口重写（add by buxianguan）
     */
    @Page(Viewer = JSON)
    public void downloadCoinInfo() {
        Map<String, Object> returnMap = new HashMap<>();

        initLoginUserForJson(returnMap);
        String userId = loginUser.getId();
        // cookie中记录的登录用户的Id
        //采用邮箱注册且没有开启手机认证的用户在用户“提现”的时候强制使用谷歌验证器
        if (!googleNoSetTips()) {
            returnMap.put("noPhoneNoGoogle", true);
        }


        //最新提现记录
        ddao.setCoint(coint);
        dao.setCoint(coint);
        DownloadBean download = ddao.getLast(userId);
        //此处风险
        returnMap.put("download", download);

        //今日已提
        PayUserWalletBean payuser = dao.getById(Integer.parseInt(userId), coint.getFundsType());
        BigDecimal todayCash = ddao.getTodayCash(userId);
        BigDecimal dayCash = payuser.getDayCash();
        BigDecimal everyTimeCash = payuser.getTimesCash();
        returnMap.put("todayCash", todayCash);
        returnMap.put("everyTimeCash", everyTimeCash);
        returnMap.put("dayCash", dayCash);

        BigDecimal thisTimeCouldCash = ddao.getThisTimeCouldCash(payuser.getBalance(), todayCash, everyTimeCash, dayCash);
        BigDecimal canWithdraw = p2pUserDao.getCanWithdraw(userId, loginUser.getUserName(), thisTimeCouldCash, coint.getStag());

        returnMap.put("canWithdraw", canWithdraw);
        returnMap.put("fees", coint.getMinFees());

        returnMap.put("usdtefees", DatabasesUtil.getUsdtAggrement(102).getMinFees());

        BigDecimal todayFreeCash = ddao.getTodayFreeCash(userId);
        BigDecimal couldFreeCash = payuser.getDayFreeCash().subtract(todayFreeCash);
        couldFreeCash = couldFreeCash.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : couldFreeCash;
        returnMap.put("couldFreeCash", couldFreeCash);

        returnMap.put("freeCash", payuser.getDayFreeCash());

        returnMap.put("codeType", PostCodeType.cash.getKey());
        if (StringUtils.isBlank(loginUser.getSafePwd())) {
            returnMap.put("safeAuth", true);
        }

        Map<String, CoinProps> coinMap = DatabasesUtil.getNewCoinPropMaps();
        returnMap.put("coinMap", coinMap);

        //suxinjie 20170313 添加余额和冻结资金
        JSONArray funds = UserCache.getUserWalletFunds(userIdStr());
        for (int i = 0; i < funds.size(); i++) {
            JSONObject obj = funds.getJSONObject(i);
            if (obj.getString("propTag").equals(coint.getPropTag())) {
                returnMap.put("balance", obj.getBigDecimal("balance").doubleValue());
                returnMap.put("freeze", obj.getBigDecimal("freeze").doubleValue());
            }
        }

        ddao.setCoint(coint);
        Query<DownloadBean> query = ddao.getQuery();
        query.setSql("select * from " + ddao.getTableName());
        query.setCls(DownloadBean.class);

        int page = intParam("page");
        int pageSize = intParam("pageSize") == 0 ? PAGE_SIZE : intParam("pageSize");

        query.append(" userId='" + userId + "' and isDel=0 ");
        int total = query.count();
        if (total > 0) {
            query.append("order by id desc");
            //分页查询
            List<DownloadBean> downloads = ddao.findPage(page, pageSize);
            returnMap.put("dataList", downloads);
        }

        String pn = setPaging(total, page, pageSize);
        returnMap.put("pager", pn);
        json("", true, com.alibaba.fastjson.JSON.toJSONString(returnMap));
    }


    public static void main(String[] args) {
        /*for (int i = 0; i <= 40; i++) {
            Map<String, String> fund = (Map<String, String>) Cache.GetObj("user_wallet_funds_loan_1003407");
            Map<String, PayUserBean> funds = new HashMap<>();
            for (String key : fund.keySet()) {
                PayUserBean payUserBean = (PayUserBean) com.alibaba.fastjson.JSON.parseObject(fund.get(key), PayUserBean.class);
                BigDecimal.ZERO.add(payUserBean.getTotal());
                if (payUserBean.getFreez() == null || payUserBean.getBalance() == null || payUserBean.getWithdrawFreeze() == null) {
                    System.out.println("当前币种" + payUserBean.getFundsType());
                }
                funds.put(key, payUserBean);
            }
        }*/
        /*try {
            log.info("8888888888");
            *//*Cache.Set("testkey","1",5*1000);*//*
         *//*Thread.sleep(10 * 1000);*//*
            log.info("0999999999"+Cache.Get("testkey"));
        }catch (Exception e){

        }*/
        //验证资金密码
        LimitType lt = LimitType.SafePassEntrustError;
        int entrustStatus = lt.GetStatus("1003385");
        /*if (entrustStatus == -1) {
            result.put("lockStatus", 1);
            result.put("lockTips", L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。"));
        }*/
        return;

    }

    @Page(Viewer = JSON)
    public void checkSafe() {
        initLoginUser();
        String userName = loginUser.getUserName();
        String safePwd = param("safePwd");
        if (StringUtils.isEmpty(userName)) {
            json("请输入用户名", false, "");
            return;
        }
        User user = userDao.getUserByColumn(userName, "email");
        if (null == user) {
            json("用户不存在，请输入正确的用户名", false, "");
            return;
        }
        if (StringUtils.isEmpty(safePwd)) {
            json("请输入资金密码", false, "");
            return;
        }
        int status = new UserDao().checkSecurityPwdNew(safePwd, user.get_Id());
        if (status == -2) {
            json(L("您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。"), false, "");
            return;
        } else if (status == 0) {
            json("资金密码未设置", false, "");
            return;
        } else if (status == -3) {
            json("", true, "");
            return;
        } else {
            json(L("抱歉，原资金密码错误，还有%%次机会。", status + ""), false, "");
            return;
        }

    }

    @Page(Viewer = JSON)
    public void checkCode() {
        String userIp = ip();
        String code = param("code");
        String type = param("type");
        Boolean flag = booleanParam("flag");
        int sendType = 1;

        if (StringUtils.isEmpty(code)) {
            json("验证码不能为空", false, "");
            return;
        }
        String codeRecvAddr = loginUser.getUserName();
        User user = new User();
        user = userDao.getUserByColumn(codeRecvAddr, "email");
        if (StringUtils.isEmpty(type)) {
            codeRecvAddr = user.getUserContact().getSafeMobile();
            sendType = 2;
        }
        int icodeType = intParam("codeType");//验证码类型
        PostCodeType postCodeType = (PostCodeType) EnumUtils.getEnumByKey(icodeType, PostCodeType.class);
        String codeType = postCodeType.getValue();
        ClientSession clientSession = new ClientSession(userIp, codeRecvAddr, lan, codeType, false);
        DataResponse dr = null;
        if (sendType == 1) {
            if (!flag) {
                dr = clientSession.checkCodeMail(code);
            } else {
                dr = clientSession.checkOnlyCode(code);
            }
        } else {
            if (!flag) {
                dr = clientSession.checkCode(code);
            } else {
                dr = clientSession.checkOnlyCode(code);
            }

        }
        if (!dr.isSuc()) {
            json(dr.getDes(), false, "");
            return;
        } else {
            json("", true, user.get_Id());
        }

    }


    @Page(Viewer = JSON)
    public void checkGoogle() {
        String userName = loginUser.getUserName();
        String code = param("code");
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isEmpty(userName)) {
            json("请输入用户名", false, "");
            return;
        }
        if (StringUtils.isEmpty(code)) {
            json("请输入谷歌验证码", false, "");
            return;
        }
        User user = userDao.getUserByColumn(userName, "email");
        if (null == user) {
            json("用户不存在，请输入正确的用户名", false, "");
            return;
        }

        if (!isCorrect(user.getUserContact().getSecret(), Long.parseLong(code), JSON, user.get_Id())) {
            json("", true, "");
            return;
        }

    }


}
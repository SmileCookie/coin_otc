package com.world.controller.api.m;

import cn.hutool.core.util.StrUtil;
import com.Lan;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.file.config.FileConfig;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.googleauth.GoogleAuthenticator;
import com.kafka.ProducerSend;
import com.messi.common.core.utils.PageUtils;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.CapitalTransferApiService;
import com.messi.user.feign.CheckCodeApiService;
import com.messi.user.feign.DownloadRecordApiService;
import com.messi.user.feign.DowoloadApiService;
import com.messi.user.feign.PayUserApiService;
import com.messi.user.feign.PayUserOtcApiService;
import com.messi.user.feign.RechargeRecordApiService;
import com.messi.user.feign.UserApiService;
import com.messi.user.util.ConstantCenter;
import com.messi.user.vo.AssetEvaluationVO;
import com.messi.user.vo.AuthenticationVo;
import com.messi.user.vo.BillOtcVO;
import com.messi.user.vo.DetailsSummaryBeanVo;
import com.messi.user.vo.DownloadRecordBo;
import com.messi.user.vo.DownloadRecordVo;
import com.messi.user.vo.DownloadSummaryBeanVo;
import com.messi.user.vo.DownloadVo;
import com.messi.user.vo.R;
import com.messi.user.vo.RechargeRecordBo;
import com.messi.user.vo.RechargeRecordVo;
import com.messi.user.vo.TransRecordBo;
import com.messi.user.vo.TransRecordVo;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.redis.RedisUtil;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.constant.Const;
import com.world.controller.CheckRegex;
import com.world.controller.api.util.SystemCode;
import com.world.data.database.DatabasesUtil;
import com.world.data.database.FormatUtils;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.LimitType;
import com.world.model.LockType;
import com.world.model.coin.service.CoinInfoService;
import com.world.model.dao.FundsintroductionDao;
import com.world.model.dao.app.AppDao;
import com.world.model.dao.app.AppSettingDao;
import com.world.model.dao.app.MarketRemindDao;
import com.world.model.dao.app.PushSettingDao;
import com.world.model.dao.bill.BillDetailDao;
import com.world.model.dao.collectMarket.CollectMarketDao;
import com.world.model.dao.financialproift.FinUserReturnOrderInfoDao;
import com.world.model.dao.financialproift.FinUsersontransferDao;
import com.world.model.dao.financialproift.FinancialBonusDao;
import com.world.model.dao.financialproift.FinancialSuperNodeDao;
import com.world.model.dao.financialproift.UserFinancialInfoDao;
import com.world.model.dao.jifen.JifenDao;
import com.world.model.dao.level.IntegralRuleDao;
import com.world.model.dao.level.UserVipLevelDao;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.msg.MsgDao;
import com.world.model.dao.msg.NewsDao;
import com.world.model.dao.otc.OtcFrozenBillDao;
import com.world.model.dao.pay.DownloadDao;
import com.world.model.dao.pay.DownloadSummaryDao;
import com.world.model.dao.pay.KeyDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.pay.ReceiveAddrDao;
import com.world.model.dao.recommendcoin.RecommendCoinDao;
import com.world.model.dao.user.CountryDao;
import com.world.model.dao.user.EmailDao;
import com.world.model.dao.user.MobileDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.UserLoginIpDao;
import com.world.model.dao.user.VerifyUserInfoDao;
import com.world.model.dao.user.authen.AuthenLogDao;
import com.world.model.dao.user.authen.AuthenticationDao;
import com.world.model.dao.user.authen.IdcardBlackListDao;
import com.world.model.dao.user.authen.IdcardDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.CacheKeys;
import com.world.model.entity.CointTable;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.LegalTenderType;
import com.world.model.entity.Market;
import com.world.model.entity.SysEnum;
import com.world.model.entity.app.App;
import com.world.model.entity.app.AppSetting;
import com.world.model.entity.app.MarketRemind;
import com.world.model.entity.app.PushSetting;
import com.world.model.entity.bill.BillDetails;
import com.world.model.entity.bill.BillType;
import com.world.model.entity.coin.CoinInfo;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.financialproift.FinProductInvest;
import com.world.model.entity.financialproift.FinUserReturnOrderInfo;
import com.world.model.entity.financialproift.FinUsersontransfer;
import com.world.model.entity.financialproift.FinancialBonus;
import com.world.model.entity.financialproift.FinancialProduct;
import com.world.model.entity.financialproift.SuperNode;
import com.world.model.entity.financialproift.UserFinancialInfo;
import com.world.model.entity.level.IntegralRule;
import com.world.model.entity.level.Jifen;
import com.world.model.entity.level.JifenVO;
import com.world.model.entity.level.UserVipLevel;
import com.world.model.entity.level.VipRate;
import com.world.model.entity.msg.News;
import com.world.model.entity.msg.TipType;
import com.world.model.entity.otc.OtcFrozenBill;
import com.world.model.entity.pay.ChinaBank;
import com.world.model.entity.pay.ChinaBank4Mobile;
import com.world.model.entity.pay.ChinaBankHelp;
import com.world.model.entity.pay.DownloadSummaryBean;
import com.world.model.entity.pay.KeyBean;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.pay.ReceiveAddr;
import com.world.model.entity.recommendcoin.RecommendCoin;
import com.world.model.entity.user.CollectMarket;
import com.world.model.entity.user.Country;
import com.world.model.entity.user.TradeAuthenType;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.model.entity.user.VerifyUserInfo;
import com.world.model.entity.user.WithdrawAddressAuthenType;
import com.world.model.entity.user.authen.AuthUtil;
import com.world.model.entity.user.authen.AuthenType;
import com.world.model.entity.user.authen.Authentication;
import com.world.model.enums.BonusEnum;
import com.world.model.enums.CoinChargeStatus;
import com.world.model.enums.CoinDownloadStatus;
import com.world.model.enums.StatusEnum;
import com.world.model.financialproift.userfininfo.thread.ResetProfitThread;
import com.world.model.jifenmanage.JifenManage;
import com.world.model.loan.dao.P2pUserDao;
import com.world.model.loan.entity.P2pUser;
import com.world.model.loan.worker.LoanAutoFactory;
import com.world.model.singleton.SingletonSingleThreadPool;
import com.world.model.singleton.SingletonThreadPool;
import com.world.rabbitmq.producer.InitPayUserWalletProducer;
import com.world.rabbitmq.producer.UserLoginLogProducer;
import com.world.util.CommonUtil;
import com.world.util.DigitalUtil;
import com.world.util.Message;
import com.world.util.MsgToastKey;
import com.world.util.QcloudCosUtil;
import com.world.util.UserUtil;
import com.world.util.date.TimeUtil;
import com.world.util.financialproift.FinancialProiftUtils;
import com.world.util.jpush.MsgType;
import com.world.util.jpush.Pusher;
import com.world.util.language.SafeTipsTag;
import com.world.util.request.HttpUtil;
import com.world.util.sign.RSACoder;
import com.world.util.string.EncryptionPhoto;
import com.world.util.string.MD5;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.Pages;
import com.world.web.action.Action;
import com.world.web.action.MobileUserAction;
import com.world.web.response.DataResponse;
import com.world.web.sso.SSOLoginManager;
import com.world.web.sso.SessionUser;
import com.world.web.sso.session.ClientSession;
import com.world.web.sso.session.SsoSessionManager;
import com.yc.entity.SysGroups;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.beetl.sql.core.engine.PageQuery;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import static com.world.controller.api.util.SystemCode.*;


/**
 * 手机app接口v1.0.1
 *
 * @author dongzhihui
 * @modify xiewenzheng 2017.03.16
 */
public abstract class BaseMobileAction extends MobileUserAction {

    private final String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJG14+94DEgzyd6G8+Ue+lpLKK9uIftpSZ7wvnX3jtw+6SUKldkvL1mYq9W8qIJD7w5t3YQIkVoWIlm5Eba5NcDYgfDC/QnYyr9zfDthlJECvQ8TC0wjy9cOtCC4FntewsqmGxLjTA17Zn0RJpsqXvNFjZEinR6IawvnlhPKJ/IwIDAQAB";
    private final String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIkbXj73gMSDPJ3obz5R76Wksor24h+2lJnvC+dfeO3D7pJQqV2S8vWZir1byogkPvDm3dhAiRWhYiWbkRtrk1wNiB8ML9CdjKv3N8O2GUkQK9DxMLTCPL1w60ILgWe17CyqYbEuNMDXtmfREmmype80WNkSKdHohrC+eWE8on8jAgMBAAECgYA0EsPx0FkEyf9szgnqNn55gBsbsnbhqpu391WjE9y/GUp0IdShqJ1EcIOENeevW2zYXCbn6mLmZzv6oqIzMuFtZ4GGbHvTsMNGtoBJsvIjV36FjdiXU7FAGqtUI+I/kFBvxFuKcil6JBFGKheQle2segoB9hAsKGoUSayAE5yjqQJBAMJllnMTMeuomhZxSQfuq4Ke3BAGGbbUfcCYnCoK1y9LBe3qXmynWYnc2caIHgbMdDiGYcTm1XOZ5lR/a2GP4HUCQQC0jiUFKWmWkx+MgverbA4QBoh+ff5M95c5T/8W2QbrUW7DV++aW4y+4D92Ei6nFcF1V8SSMxgDmqiz6pOqS243AkEAl6vlR6GZWHGyz4HR5kN8Q6yorEPmOjTubJ9lcJQGspqJZMhwpbuoa50JuRGow8svfo6yp4smzUwtXo4P/Q3hpQJAC8AIZrqYNYVjkzhet9gzXhWewmSerRGb1M4A8tKy4ZOOGsZZQHlewnlDiAKM6LDAw0sv7rfGg02IVxUYAQghpwJABiNcbBh7MnDfGaRZzE7SX/UwRn7OmGY7lFMBWadiQ/R5pKpdPrVmwdlTsefzb1acYy41LQCFCPKxVjv7sUduXQ==";

    public final String market_remind_set_key_btc = "market_remind_set_key_btc_";
    public final String market_remind_set_key_ltc = "market_remind_set_key_ltc_";
    static int appDefaultTime = 30 * 24 * 60 * 60;
    private final String loginTerminal = "app_v1.1";
    public final static String appUid = GlobalConfig.session + "appuid_";
    public final static String readNoticeKey = "user_notice_app_";
    public final static String readNoticeIpKey = "user_notice_app_ip_";
    UserFinancialInfoDao userFinancialInfoDao = new UserFinancialInfoDao();

    static Logger logger = Logger.getLogger(V1_1.class.getName());

    FinancialBonusDao financialBonusDao = new FinancialBonusDao();

    UserDao userDao = new UserDao();
    DownloadDao bdDao = new DownloadDao();
    PayUserDao payUserDao = new PayUserDao();
    MarketRemindDao marketRemindDao = new MarketRemindDao();
    AuthenLogDao logDao = new AuthenLogDao();
    AuthenticationDao auDao = new AuthenticationDao();
    VerifyUserInfoDao vudao = new VerifyUserInfoDao();
    AppDao appDao = new AppDao();
    KeyDao keyDao = new KeyDao();
    ReceiveAddrDao receiveDao = new ReceiveAddrDao();
    PushSettingDao pushSettingDao = new PushSettingDao();
    AppSettingDao settingDao = new AppSettingDao();
    P2pUserDao p2pUserDao = new P2pUserDao();
    CollectMarketDao collectMarketDao = new CollectMarketDao();
    OtcFrozenBillDao otcFrozenBillDao = new OtcFrozenBillDao();
    private DownloadSummaryDao downloadSummaryDao = new DownloadSummaryDao();
    private EmailDao eDao = new EmailDao();
    FundsintroductionDao fundsinDao = new FundsintroductionDao();
    @Page(Viewer = JSON)
    public void version() {
        setLan();
        String version = param("2");

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("version", version);
        json(SystemCode.code_1000, map);
    }


    UserLoginIpDao uld = new UserLoginIpDao();


    @Page(Viewer = JSON)
    public void registerWithEmail() {
        setLan();
        try {
            String email = param("email");
            String password = param("password");
            String code = param("code");
            //校验是否锁定

            DataResponse dr1 = this.checkVerifiCode(email, MsgToastKey.REGISTER, ConstantCenter.UpdFunctionType.OTC_APP_REGISTER, MsgToastKey.LOCK_2_HOUR);
            if (!dr1.isSuc()) {
                json(SystemCode.code_1001, dr1.getDes());
                return;
            }
            //邮箱不能为空
            if (StringUtils.isEmpty(email)) {
                json(SystemCode.code_1001, L("请输入正确的邮箱"));
                return;
            } else {
                email = email.toLowerCase();
            }
            //邮箱格式
            if (!CheckRegex.isEmail(email)) {
                json(SystemCode.code_1001, L("请输入正确的邮箱"));
                return;
            }
            //邮箱已注册
            User user = userDao.findOne("email", email);
            if(null != user){
                json(SystemCode.code_1001, L("请输入正确的邮箱"));
                return;
            }
            //check密码
            if (StringUtils.isBlank(password)) {
                json(SystemCode.code_1001, L("请输入登录密码"));
                return;
            } else {
                password = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(password), priKey));
            }
            //密码验证
            //密码的组成至少要包括大小写字母、数字及标点符号的其中两项
            String regex = "^(?![A-Za-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{8,20}$";
            if (!password.matches(regex)) {
                json(SystemCode.code_1001, L("您的密码需要8-20位，包含字母，数字，符号的两种以上。"));
                return;
            }

            // 验证码CheckCode
            String func = String.valueOf(LockType.REGISTER.getValue());
            ClientSession clientSession = new ClientSession(ip(), email, lan, String.valueOf(PostCodeType.appRegVIP.getValue()), false);
            DataResponse dr = clientSession.checkCodeMailApp(email, code, LimitType.RegisterPassError, MsgToastKey.REGISTER, MsgToastKey.LOCK_2_HOUR);
            if (!dr.isSuc()) {
                json(SystemCode.code_1001, L(dr.getDes()));
                return;
            }
            String userIp = ip();
			/*Start by guankaili 20190701 取消同一个IP无法注册多个账号问题(http://jira.oswaldlink.f3322.net:10080/browse/XJYPT-3164) */
//			if (!userIp.equals("127.0.0.1")) {
//				Query<User> q = userDao.getQuery(User.class).filter("loginIp =", userIp);
//				// //
//				long count = userDao.find(q).countAll();
//				if (count >= 3) {
//                    log.error("ip：" + userIp + ",次数：" + count + ",你所在的ip跟踪程序已启动，如有恶意请停止，如果报警有误，请联系网站在线客服...");
//					json(SystemCode.code_1001, L("您好，系统监测到您的IP可能存在大量刷注册推荐人奖励行为，因此本次注册不能被通过。如果您是真实用户，请通过邮箱support@btcwinex.com联系我们的客服，给您造成的不便敬请谅解！您当前IP：") + userIp);
//					return;
//				}
//			}
			/*End  */
            String recommenders = "";
            String recommId = "";
            if (null != user && !user.getUserContact().isCanReg()) {
                userDao.delById(user.getId());
            }
            String emailCode = MD5.toMD5(System.currentTimeMillis() + email);

            user = new User(userDao.getDatastore());
            user.setLanguage(lan);
            user.setCurrency(GlobalConfig.currency);
            user.setMarket(GlobalConfig.market);
            user.setCurrencyN(GlobalConfig.currencyN);
            user.setLoginIp(userIp);
            user.setDeleted(false);
            user.setModifyTimes(0);
            user.setPwd(UserUtil.newSafeSecretMethod(user.getId(), password));
            user.setPwdLevel(0);
            user.setSafePwd("");
            user.setSafeLevel(0);
            user.setRecommendId(recommId);
            user.setRecommendName(recommenders);
            user.setLastLoginTime(TimeUtil.getNow());
            user.setEmail(email);
            user.setUserQualification(1);
            user.setUserName(email);
            user.setPreviousLogin(user.getLastLoginTime() == null ? TimeUtil.getNow() : user.getLastLoginTime());
            user.setLastLoginTime(TimeUtil.getNow());
            user.setTrueIp(user.getLoginIp() == null ? userIp : user.getLoginIp());
            user.setUserQualification(1);
            UserContact uc = new UserContact();
            uc.setEmailCode(emailCode);
            uc.setCheckEmail(email);
            uc.setEmailTime(com.messi.user.util.TimeUtil.getNow());
            uc.setSafeEmail(user.getEmail());
            uc.setEmailStatu(AuditStatus.pass.getKey());
            uc.setEmailTime(new Timestamp(0));
            uc.setMobileCode("");
            uc.setCheckEmail("");
            uc.setCardStatu(4);
            user.setUserContact(uc);
            String nid = userDao.addUser(user);
            if (StringUtils.isNotEmpty(nid)) {
                user.set_Id(nid);
                UpdateResults<User> ur = userDao.updatePwd(nid, password, 0);
                Cache.Delete("md5CurrentCodeImage_" + sessionId);

//				userLs = userDao.get(user.getRecommendId());

                Map<String, Object> map = new HashMap<String, Object>();
                Map<String, Object> userMap = getUserInfo(user);

                //生成token 发送给用户
                String token = MD5.toMD5(nid + UUID.randomUUID().toString());
                String loginCacheKey = appLoginCache + nid;
                SessionUser su = new SessionUser();
                su.uid = nid;//用户id
                su.uname = email;//用户名
                su.ltime = System.currentTimeMillis();//登录时间
                su.lip = userIp;//登录ip
                su.lastTime = su.ltime;//最后活动时间
                su.token = token;

                Cache.SetObj(loginCacheKey, su, 30 * 24 * 60 * 60);

                map.put("token", token);
                map.put("userInfo", userMap);
                map.put("userId", nid);
                json(SystemCode.code_1000, map);
                MsgDao.sendMsg(nid, user.getUserName(), TipType.registerSuc);
                Data.Update("insert into userinfo (id,userName,registerTime,uType) values (?,?,?,?)", new Object[]{nid, email, TimeUtil.getDateStr(now(), "yyyy-MM-dd HH:mm:ss"), "01"});
				/*start by xwz 20170625 注册和绑定手机加积分*/
                JifenManage jifenManager = new JifenManage(user.getId(), 1, null, null, "VIP");//1：注册
                SingletonSingleThreadPool.addJiFenThread(jifenManager);
                jifenManager = new JifenManage(user.getId(), 4, null, null, "VIP");//4：绑定手机
                SingletonSingleThreadPool.addJiFenThread(jifenManager);
				/*end*/
				/*Start by guankaili 20181229 添加消息队列 */
                // 保存登录IP
//				uld.add(user.getRealName(), user.getId(), user.getUserName(), ip(), 2, loginTerminal);
                UserLoginLogProducer.send(user.getUserName(), user.getId(), user.getUserName(), ip(), 2, loginTerminal);
                InitPayUserWalletProducer.send(user.getId());
				/*End*/
				/*Start by guankaili 20190516 用户激活动作埋点 */
                JSONObject jsonObject = new JSONObject();
//				jsonObject.put("userid",user.getId());
                jsonObject.put("registertime",user.getRegisterTime());
                ProducerSend producerSend = new ProducerSend();
                producerSend.sendMessage("activation", jsonObject.toString());
                log.info("推送驾驶舱用户激活动作埋点成功："+jsonObject.toString());
				/*end*/
                json(SystemCode.code_1000, L("注册成功!"));
            } else {
                json(SystemCode.code_1001, L("注册失败，请重新注册!"));
            }
        }catch(Exception e){
            log.error("内部异常", e);
            json(SystemCode.code_1001, L("注册出错，请稍后重试。"));
        }
    }

    /**
     * 登录
     */
    @Page(Viewer = JSON)
    public void doLogin() {
        try {
            setLan();
            //【第一步】接收参数

            /**
             * **请求参数**

             | 参数名      | 类型     | 是否必须 | 描述         |
             | :------- | :----- | :--- | :--------- |
             | userName | String | 是    | 用户名/手机号/邮箱 |
             | password | String | 是    | 登录密码（RSA加密）       |
             **/
            String email = param("email");
            String password = param("password");
            if(StringUtils.isEmpty(email)){
                json(SystemCode.code_1001, L("用户名或密码错误"));
                return;
            }
            if(StringUtils.isEmpty(password)){
                json(SystemCode.code_1001, L("用户名或密码错误"));
                return;
            }
            email = email.toLowerCase().trim();
            //【第二步】解密RSA参数
            password = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(password), priKey));

            //【第三步】查询用户资料
            User safeUser = userDao.findOne("userContact.safeEmail",email);
            String retNameMsg = loginLimit(safeUser,email,"",false);
            if(StringUtils.isNotEmpty(retNameMsg)){
                json(SystemCode.code_1001,retNameMsg);
                return;
            }
            //用户找不到，返回找不到用户
//			if (null == safeUser) {
//				json(SystemCode.code_1001, L("用户名或密码错误"));
//				return;
//			}
            String userId = safeUser.getId();
            //校验是否锁定
            DataResponse dr1 = this.checkVerifiCode(userId, MsgToastKey.LOGIN, ConstantCenter.UpdFunctionType.LOGIN_MOBILE, MsgToastKey.LOCK_2_HOUR);
            if (!dr1.isSuc()) {
                json(SystemCode.code_1001, dr1.getDes());
                return;
            }
            DataResponse dr2 = this.checkVerifiCode(userId, MsgToastKey.LOGIN, ConstantCenter.UpdFunctionType.LOGIN_GOOGLE, MsgToastKey.LOCK_2_HOUR);
            if (!dr2.isSuc()) {
                json(SystemCode.code_1001, dr2.getDes());
                return;
            }

            //【第五步】加密密码与数据库的密码字段判断
            String encryptionPwd = safeUser.getEncryptedPwd(password);
            String retPwdMsg = loginLimit(safeUser,email,encryptionPwd,true);
            if (StringUtils.isNotEmpty(retPwdMsg)) {
                json(SystemCode.code_1001,retPwdMsg);
                return;
            }
//			if (!encryptionPwd.equals(safeUser.getPwd())) {
//				json(SystemCode.code_1001, L("用户名或密码错误"));
//				return;
//			}

            if(safeUser.isFreez()){
                json(SystemCode.code_1001, L("该账户已冻结，暂时不能登录。"));
                return;
            }
            //封装用户对象数据到json
//			Map<String, Object> userMap = new HashMap<>();
//			userMap.put("uid",safeUser.getId());
//			userMap.put("vipRate",safeUser.getVipRate());
//			userMap.put("loginNeedSmsOpen",safeUser.getSmsOpen());
//			userMap.put("loginNeedGoogleAuth",safeUser.getGoogleOpen());
//			userMap.put("mobile",safeUser.getUserContact().getSafeMobile());
//			userMap.put("secret",safeUser.getUserContact().getSecret());
//			String userInfo = userMap.toString();
//			Cache.Set(CacheKeys.getUserInfoKey(safeUser.getId()),userInfo);
//			log.info("登录信息(userInfo)："+userInfo);
//			json(SystemCode.code_1000,"" , userMap);


			/*start by xwz 登录成功之后修改用户语言*/
            if(!lan.equals(safeUser.getLanguage())){
                try{
                    userDao.updateUserLanguage(safeUser.getId(),lan);
                    safeUser.setLanguage(lan);
                    Cache.Set("user_lan_" + safeUser.getId(), lan);
                }catch(Exception e){
                    log.error("app用户登录，修改用户语言出错，错误信息：" + e.toString());
                }
            }
            String language = Cache.Get("user_lan_" + safeUser.getId());
            if (StringUtils.isEmpty(language) || !lan.equals(language)) {
                Cache.Set("user_lan_" + safeUser.getId(), lan);
            }
            Map<String, Object> userMap = new HashMap<>();
			/*start by guankaili 20181211 登录发邮件*/
            if(!safeUser.getGoogleOpen() && !safeUser.getSmsOpen()){
                JSONObject json = buildUserSession(safeUser);
                SessionUser su = toLogin(this,safeUser.getId(), safeUser.getUserName(), ip(),json);
                userMap.put("others",su.getOthers());
                UserLoginLogProducer.send(safeUser.getUserName(), safeUser.getId(), safeUser.getUserName(), ip(), 2, loginTerminal);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if("en".equals(lan)){
                    format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                }
                String loginTime = format.format(new Date());
                String emailInfo = eDao.getLoginEmailHtmlByInfo(safeUser, ip(), loginTime, this);
                String title = L("登录成功");
                //锁定发送邮件
                eDao.sendEmail(ip(), safeUser.getId(), safeUser.getUserName(), title, emailInfo, safeUser.getUserContact().getSafeEmail());
				/*start by guankaili 20190529 敏感用户预警*/
                if(safeUser.isWarningUser()){
                    WarningUserAlarm(safeUser, loginTime);
                }
				/*end*/
            }
			/*end*/
            userMap.put("uid",safeUser.getId());
            userMap.put("vipRate",safeUser.getVipRate());
            userMap.put("loginNeedSmsOpen",safeUser.getSmsOpen());
            userMap.put("loginNeedGoogleAuth",safeUser.getGoogleOpen());
            userMap.put("mobile",safeUser.getUserContact().getSafeMobile());
            userMap.put("secret",safeUser.getUserContact().getSecret());
            userMap.put("hasSafePwd",safeUser.getHasSafePwd());
            String userInfo = userMap.toString();
            Cache.Set(CacheKeys.getUserInfoKey(safeUser.getId()),userInfo);
            //初始化一下 pay_user_* 防止老用户没有初始化资金表
            InitPayUserWalletProducer.send(safeUser.getId());

            json(SystemCode.code_1000,"" ,userMap);

        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("登录失败"));
        }
    }

    /**
     * 校验登录邮箱和密码
     * @param user
     * @param email
     * @param password
     * @param flg
     * @return
     */
    public String loginLimit(User user,String email ,String password,boolean flg) {

        int status = new UserDao().checkLoginTimes(user,email, password,flg);
        String msg = "";
        if (status == -2) {
            msg = L("您的账户被锁定，请%%小时后尝试", MsgToastKey.LOCK_2_HOUR);
        } else if (status == -3) {
            return msg;
        } else {
            if(lan.equals("en")){
                if(status > 1){
                    msg = L("用户名或密码错误，您还有%%次机会s", String.valueOf(status));
                }else{
                    msg = L("用户名或密码错误，您还有%%次机会", String.valueOf(status));
                }
            }else{
                msg = L("用户名或密码错误，您还有%%次机会", String.valueOf(status));
            }
        }
        return msg;
    }
	/*start by xzhang 201701010 zendesk跨系统会话同步*/

    /**
     * 1.1 当前无用户登录，则根据语言环境跳转到指定工单系统页面，无登录状态。
     * 1.2 当前用户已登录，且存在邮箱。则同步当前用户会话信息到工单系统；
     * 语言环境根据用户工单信息设置展示，默认在工单系统配置；会话同步不会传递语言环境。
     * 1.3 当前用户已登录，但不存在邮箱。则无法同步用户会话信息；则根据语言环境跳转到指定工单系统页面，无登录状态。
     * 异常：
     * 屏蔽所有异常，展示未登录状态下工单系统
     */
    public void doZendesk() {
        String viewFlag = param("viewFlag");
        try {
            User user = userDao.getUserById(userIdStr());
            if (user != null) {
                UserContact userContact = user.getUserContact();
                // Given a user instance
                JWTClaimsSet jwtClaims = new JWTClaimsSet();
                jwtClaims.setIssueTime(new Date());
                jwtClaims.setJWTID(UUID.randomUUID().toString());
                jwtClaims.setCustomClaim("name", user.getUserName());
                jwtClaims.setCustomClaim("email", userContact.getSafeEmail());
                if (userContact != null && userContact.getSafeEmail() != null && !"".equals(userContact.getSafeEmail())) {
                    jwtClaims.setCustomClaim("email", userContact.getSafeEmail());
                } else if (userContact != null && userContact.getSafeMobile() != null && !"".equals(userContact.getSafeMobile())) {
                    String[] mobiles = userContact.getSafeMobile().split(" ");
                    if (mobiles != null && mobiles.length > 1) {
                        jwtClaims.setCustomClaim("email", mobiles[1] + "@btcwinex.com");
                    } else {
                        if (user.getUserName().indexOf("@") != -1) {
                            jwtClaims.setCustomClaim("email", user.getUserName());
                        } else {
                            jwtClaims.setCustomClaim("email", user.getUserName() + "@btcwinex.com");
                        }
                    }
                } else {
                    if (user.getUserName().indexOf("@") != -1) {
                        jwtClaims.setCustomClaim("email", user.getUserName());
                    } else {
                        jwtClaims.setCustomClaim("email", user.getUserName() + "@btcwinex.com");
                    }
                }
                // Create JWS header with HS256 algorithm
                JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
                header.setContentType("text/plain");
                // Create JWS object
                JWSObject jwsObject = new JWSObject(header, new Payload(jwtClaims.toJSONObject()));
                // Create HMAC signer
                JWSSigner signer = new MACSigner(GlobalConfig.SHARED_KEY.getBytes());
                try {
                    jwsObject.sign(signer);
                } catch (Exception e) {
                    log.error("当前用户" + user.get_Id() + "签名失败，失败原因：", e);
                    throw e;
                }
                String returnTo = GlobalConfig.SUBDOMAIN + "/hc/zh-cn";
                String jwtString = jwsObject.serialize();
                String redirectUrl = GlobalConfig.SUBDOMAIN + "/access/jwt?jwt=" + jwtString;
                if (lan == null || "".equals(lan) || "cn".equals(lan)) {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/zh-cn";
                } else if ("en".equals(lan)) {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/en-us";
                }else if ("jp".equals(lan)) {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/ja";
                }else if ("kr".equals(lan)) {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/ko";
                }else {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/zh-tw";
                }
                if (StringUtil.exist(viewFlag) && viewFlag.equals("notice")) {
                    redirectUrl = redirectUrl + "&return_to=" + GlobalConfig.SUBDOMAIN + L("公告链接");
                } else if (StringUtil.exist(viewFlag) && viewFlag.equals("app_down")) {
                    redirectUrl = redirectUrl + "&return_to=" + GlobalConfig.SUBDOMAIN + L("安装说明链接");
                } else if (StringUtil.exist(viewFlag) && viewFlag.equals("googleauth")) {
                    redirectUrl = redirectUrl + "&return_to=" + GlobalConfig.SUBDOMAIN + L("谷歌验证说明链接");
                } else {
                    redirectUrl = redirectUrl + "&return_to=" + returnTo;
                }
                go(redirectUrl);
                return;
            } else {
                String returnTo = GlobalConfig.SUBDOMAIN + "/hc/zh-cn";
                if (lan == null || "".equals(lan) || "cn".equals(lan)) {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/zh-cn";
                } else if ("en".equals(lan)) {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/en-us";
                }else if ("jp".equals(lan)) {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/ja";
                }else if ("kr".equals(lan)) {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/ko";
                } else {
                    returnTo = GlobalConfig.SUBDOMAIN + "/hc/zh-tw";
                }
                if (StringUtil.exist(viewFlag) && viewFlag.equals("notice")) {
                    returnTo = GlobalConfig.SUBDOMAIN + L("公告链接");
                } else if (StringUtil.exist(viewFlag) && viewFlag.equals("app_down")) {
                    returnTo = GlobalConfig.SUBDOMAIN + L("安装说明链接");
                } else if (StringUtil.exist(viewFlag) && viewFlag.equals("googleauth")) {
                    returnTo = GlobalConfig.SUBDOMAIN + L("谷歌验证说明链接");
                }
                go(returnTo);
                return;
            }
        } catch (Exception e) {
            String returnTo = GlobalConfig.SUBDOMAIN + "/hc/zh-cn";
            if (lan != null && "en".equals(lan)) {
                returnTo = GlobalConfig.SUBDOMAIN + "/hc/en-us";
            } else if (lan != null && ("hk".equals(lan) || "tw".equals(lan))) {
                returnTo = GlobalConfig.SUBDOMAIN + "/hc/zh-tw";
            }
            if (StringUtil.exist(viewFlag) && viewFlag.equals("notice")) {
                returnTo = GlobalConfig.SUBDOMAIN + L("公告链接");
            } else if (StringUtil.exist(viewFlag) && viewFlag.equals("app_down")) {
                returnTo = GlobalConfig.SUBDOMAIN + L("安装说明链接");
            } else if (StringUtil.exist(viewFlag) && viewFlag.equals("googleauth")) {
                returnTo = GlobalConfig.SUBDOMAIN + L("谷歌验证说明链接");
            }
            go(returnTo);
            return;
        }
    }
	/*end*/
    /**
     * 登录---拼图
     */
//	@Page(Viewer = JSON)
    public void doCheckJigsawPuzzle(){
        try {
            setLan();
            String uid = userIdStr();
//			String uid = param("uid");
            User safeUser = userDao.getUserById(uid);
            //用户找不到，返回找不到用户
            if (null == safeUser) {
                json(SystemCode.code_1001, L("用户名或密码错误"));
                return;
            }
            //校验是否锁定
            DataResponse dr1 = this.checkVerifiCode(uid, MsgToastKey.LOGIN, ConstantCenter.UpdFunctionType.LOGIN_MOBILE, MsgToastKey.LOCK_2_HOUR);
            if (!dr1.isSuc()) {
                json(SystemCode.code_1001, dr1.getDes(),"");
                return;
            }
            DataResponse dr2 = this.checkVerifiCode(uid, MsgToastKey.LOGIN, ConstantCenter.UpdFunctionType.LOGIN_GOOGLE, MsgToastKey.LOCK_2_HOUR);
            if (!dr2.isSuc()) {
                json(SystemCode.code_1001, dr2.getDes(),"");
                return;
            }
            JSONObject json = buildUserSession(safeUser);
            SessionUser su = toLogin(this,safeUser.getId(), safeUser.getUserName(), ip(),json);
			/*start by xwz 登录成功之后修改用户语言*/
            if(!lan.equals(safeUser.getLanguage())){
                try{
                    userDao.updateUserLanguage(safeUser.getId(),lan);
                    safeUser.setLanguage(lan);
                    Cache.Set("user_lan_" + safeUser.getId(), lan);
                }catch(Exception e){
                    log.error("app用户登录，修改用户语言出错，错误信息：" + e.toString());
                }
            }
            String language = Cache.Get("user_lan_" + safeUser.getId());
            if (StringUtils.isEmpty(language) || !lan.equals(language)) {
                Cache.Set("user_lan_" + safeUser.getId(), lan);
            }
			/*start by guankaili 20181211 登录发邮件*/
            if(!safeUser.getGoogleOpen() && !safeUser.getSmsOpen()){
                UserLoginLogProducer.send(safeUser.getUserName(), safeUser.getId(), safeUser.getUserName(), ip(), 2, loginTerminal);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if("en".equals(lan)){
                    format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                }
                String loginTime = format.format(new Date());
                String emailInfo = eDao.getLoginEmailHtmlByInfo(safeUser, ip(), loginTime, this);
                String title = L("登录成功");
                //锁定发送邮件
                eDao.sendEmail(ip(), safeUser.getId(), safeUser.getUserName(), title, emailInfo, safeUser.getUserContact().getSafeEmail());
				/*start by guankaili 20190529 敏感用户预警*/
                if(safeUser.isWarningUser()){
                    WarningUserAlarm(safeUser, loginTime);
                }
				/*end*/
            }
			/*end*/
            //初始化一下 pay_user_* 防止老用户没有初始化资金表
            InitPayUserWalletProducer.send(safeUser.getId());

            json(SystemCode.code_1000,"" ,su);

			/*end*/
            //		String time = DateUtil.now();
            //		String mobile = safeUser.getUserContact().getSafeMobile();
            //		if(StringUtils.isNotEmpty(mobile)){
            //			if (mobile.contains(" ")) {
            //				mobile = mobile.substring(mobile.indexOf(" ") + 1);
            //			}
            //			MobileDao mDao = new MobileDao();
            //			mDao.sendSms(safeUser, ip(), title, "尊敬的" + user.getUserName() + "，" + content, mobile);
            //		}else {
            //			EmailDao eDao = new EmailDao();
            //			String info = eDao.getWrongLimitEmailHtml(safeUser, "您的账号验证码输入次数超出限制，为了您的账号安全，XX将锁定重置资金密码功能，24小时请稍后再试。", this);
            //			String title = L("锁定重置资金密码");
            //			eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, info, user.getUserContact().getSafeEmail());
            //		}
        } catch (Exception e) {
            log.error("登录内部异常", e);
            json(SystemCode.code_1002, L("登录失败"));
        }
    }

    /**
     * 二次验证
     */
    @Page(Viewer = JSON)
    public void doSecondVerify(){
        try {
            setLan();
            //功能类型:0-登录
            String funcType = param("funcType");
            //验证方式:1-手机，0-谷歌
            String type = param("type");
            String email = param("email");
            //邮箱
            User user = userDao.findOne("userContact.safeEmail",email);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            //手机号
            String mobile = user.getUserContact().getSafeMobile();
            //谷歌密钥
            String secret = user.getUserContact().getSecret();
            //是否开启了手机验证
            boolean loginNeedSmsOpen = user.getSmsOpen();
            //手机验证码
            String smsCode = param("smsCode");
            //是否开启谷歌验证
            boolean loginNeedGoogleAuth = user.getGoogleOpen();
            //谷歌验证码
            String googleCode = param("googleCode");

            if(StringUtils.isEmpty(funcType) || StringUtils.isEmpty(type)){
                json(SystemCode.code_1001);
                return;
            }
            //校验是否锁定
            DataResponse dr1 = this.checkVerifiCode(user.getId(), MsgToastKey.LOGIN, ConstantCenter.UpdFunctionType.LOGIN_MOBILE, MsgToastKey.LOCK_2_HOUR);
            if (!dr1.isSuc()) {
                json(SystemCode.code_1001, dr1.getDes());
                return;
            }
            DataResponse dr2 = this.checkVerifiCode(user.getId(), MsgToastKey.LOGIN, ConstantCenter.UpdFunctionType.LOGIN_GOOGLE, MsgToastKey.LOCK_2_HOUR);
            if (!dr2.isSuc()) {
                json(SystemCode.code_1001, dr2.getDes());
                return;
            }

            //手机验证
            if("1".equals(type)){
                if(StringUtils.isEmpty(smsCode)){
                    json(SystemCode.code_1001, L("请输入短信验证码"));
                    return;
                }
                //手机验证
                PostCodeType pct = PostCodeType.logVIP;
                LimitType lt = LimitType.LoginMobileError;
                if(loginNeedSmsOpen && StringUtils.isNotEmpty(smsCode)){
                    DataResponse dr = getDataResponse(user.getId(),pct,mobile,2,smsCode,lt,MsgToastKey.LOGIN,MsgToastKey.LOCK_2_HOUR);
                    if (!dr.isSuc()) {
                        json(SystemCode.code_1001, L(dr.getDes()));
                        return;
                    }
                }
            }else{//谷歌验证
                if(StringUtils.isEmpty(googleCode)){
                    json(SystemCode.code_1001, L("请输入短信验证码"));
                    return;
                }
                ConstantCenter.UpdFunctionType uft = ConstantCenter.UpdFunctionType.LOGIN_GOOGLE;
                if(loginNeedGoogleAuth && StringUtils.isNotEmpty(googleCode)){
                    DataResponse dr = verifyGoogle(user.getId(),googleCode,secret,uft,MsgToastKey.LOCK_2_HOUR,"1");
                    if (!dr.isSuc()) {
                        json(SystemCode.code_1001, L(dr.getDes()));
                        return;
                    }
                }
            }
            JSONObject json = buildUserSession(user);
            SessionUser su = toLogin(this,user.getId(), user.getUserName(), ip(),json);
			/*start by xwz 登录成功之后修改用户语言*/
            if(!lan.equals(user.getLanguage())){
                try{
                    userDao.updateUserLanguage(user.getId(),lan);
                    user.setLanguage(lan);
                    Cache.Set("user_lan_" + user.getId(), lan);
                }catch(Exception e){
                    log.error("app用户登录，修改用户语言出错，错误信息：" + e.toString());
                }
            }
            String language = Cache.Get("user_lan_" + user.getId());
            if (StringUtils.isEmpty(language) || !lan.equals(language)) {
                Cache.Set("user_lan_" + user.getId(), lan);
            }
			/*start by guankaili 20181211 登录发邮件*/
            UserLoginLogProducer.send(user.getUserName(), user.getId(), user.getUserName(), ip(), 2, loginTerminal);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if("en".equals(lan)){
                format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
            }
            String loginTime = format.format(new Date());
//            String info = StrUtil.format("您的账号已成功登录XX，登录IP地址：{}，登录时间：{}。",ip(),loginTime);
            String emailInfo = eDao.getLoginEmailHtmlByInfo(user, ip(), loginTime, this);
            String title = L("登录成功");
            //锁定发送邮件
            eDao.sendEmail(ip(), user.getId(), user.getUserName(), title, emailInfo, user.getUserContact().getSafeEmail());
			/*end*/
			/*start by guankaili 20190529 敏感用户预警*/
            if(user.isWarningUser()){
                WarningUserAlarm(user, loginTime);
            }
			/*end*/
            json(SystemCode.code_1000, L(SystemCode.code_1000.getValue()),su);
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("登录失败"));
        }
    }

    /**
     * 敏感用户预警
     * @param safeUser
     * @param loginTime
     */
    private void WarningUserAlarm(User safeUser, String loginTime) {
        JSONArray funds = UserCache.getUserFunds(safeUser.getId());
        String tipInfo = "普通用户【{}】已上线，上线时间为【{}】，账户余额{},请运营人员注意！";
        String balanceStr = "";
        if(!funds.isEmpty()){
            for (int i = 0; i < funds.size(); i++) {
                JSONObject obj = funds.getJSONObject(i);
                BigDecimal balance = obj.getBigDecimal("balance");
                String coinName = obj.getString("propTag");
                if(balance.compareTo(BigDecimal.ZERO) > 0){
                    if(StringUtils.isEmpty(balanceStr)){
                        balanceStr = "【"+balance.toPlainString()+coinName+"】";
                    }else{
                        balanceStr += "、"+"【"+balance.toPlainString()+coinName+"】";
                    }
                }
            }
        }else{
            PayUserDao payUserDao = new PayUserDao();
            List<PayUserBean> list = payUserDao.getHoldCoin(safeUser.getId());
            if(CollectionUtils.isNotEmpty(list)){
                for(PayUserBean payUserBean : list){
                    String balance = payUserBean.getBalance().toPlainString();
                    String coinName = DatabasesUtil.coinProps(payUserBean.getFundsType()).getPropTag();
                    if(StringUtils.isEmpty(balanceStr)){
                        balanceStr = "【"+balance+coinName+"】";
                    }else{
                        balanceStr = "、"+"【"+balance+coinName+"】";
                    }
                }
            }
        }
        if(StringUtils.isEmpty(balanceStr)){
            balanceStr = "【此用户未持有币种。】";
        }
        log.info("10100203MGYH【敏感用户】"+ StrUtil.format(tipInfo,safeUser.getId(),loginTime,balanceStr));
    }
    /**
     * 无法提供谷歌和短信验证码---邮箱验证
     */
    @Page(Viewer = JSON)
    public void doCheckNoSecondVerify(){
        try {
            setLan();
            //邮箱
            String email = param("email");
            //邮箱code
            String code = param("code");
            //邮箱
            User user = userDao.findOne("userContact.safeEmail",email);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            //校验邮箱是否锁定
            DataResponse dr0 = this.checkVerifiCode(user.getId(),MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_EMAIL, MsgToastKey.LOCK_24_HOUR);
            if (!dr0.isSuc()) {
                json(SystemCode.code_1001, L(dr0.getDes()));
                return;
            }
            //校验充值地址是否锁定
            DataResponse dr1 = this.checkVerifiCode(user.getId(),MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_ADDRESS, MsgToastKey.LOCK_24_HOUR);
            if (!dr1.isSuc()) {
                json(SystemCode.code_1001, L(dr1.getDes()));
                return;
            }
            //校验资金密码是否锁定
            DataResponse dr2 = this.checkVerifiCode(user.getId(),MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
            if (!dr2.isSuc()) {
                json(SystemCode.code_1001, L(dr2.getDes()));
                return;
            }
            // 验证码CheckCode
            String func = String.valueOf(LockType.RESET_SECOND_VERIFY.getValue());
            String lockKey = CacheKeys.getFunctionLockKey(email,func);

            DataResponse dr = getDataResponse(user.getId(),PostCodeType.appResetSecondVerify,email,1,code,LimitType.NoSecondVrifyEmailError,MsgToastKey.RESET_SECOND_VERIFY,MsgToastKey.LOCK_24_HOUR);
            if (!dr.isSuc()) {
                json(SystemCode.code_1001,L(dr.getDes()));
                return;
            }
            String url = ApiConfig.getValue("usecenter.url");
            FeignContainer container = new FeignContainer(url.concat("/user"));
            UserApiService userApiService = container.getFeignClient(UserApiService.class);
            Map<String,Boolean> infoMap = userApiService.userCheckTypeApiN(user.getId());
            if(infoMap.size() == 1 && !infoMap.get("error")){
                json(SystemCode.code_3004);
                return;
            }

            //封装用户对象数据到jsonx
            Map<String, Object> map = new HashMap<String, Object>();
            String checkToken = EncryptionPhoto.getToken(Const.function_reset_second_verify,user.getId());
            map.put("checkToken", checkToken);
            map.put("data", infoMap);
            json(SystemCode.code_1000,"", JSONObject.toJSONString(map));
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    /**
     * 无法提供谷歌和短信验证码---校验地址
     * @return
     */
    @Page(Viewer = JSON)
    public void doCheckAddress(){

        try {
            setLan();
            //币种
            String propTag = param("propTag");
            //重置地址
            String address = param("address");
            String email = param("email");
            //邮箱
            User user = userDao.findOne("userContact.safeEmail",email);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            //校验邮箱是否锁定
            DataResponse dr0 = this.checkVerifiCode(user.getId(),MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_EMAIL, MsgToastKey.LOCK_24_HOUR);
            if (!dr0.isSuc()) {
                json(SystemCode.code_1001, L(dr0.getDes()));
                return;
            }
            //校验充值地址是否锁定
            DataResponse dr1 = this.checkVerifiCode(user.getId(),MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_ADDRESS, MsgToastKey.LOCK_24_HOUR);
            if (!dr1.isSuc()) {
                json(SystemCode.code_1001, L(dr1.getDes()));
                return;
            }
            //校验资金密码是否锁定
            DataResponse dr2 = this.checkVerifiCode(user.getId(),MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
            if (!dr2.isSuc()) {
                json(SystemCode.code_1001, L(dr2.getDes()));
                return;
            }
            //防跳步
//			String checkToken = param("checkToken");
//			boolean flg = EncryptionPhoto.checkToken(Const.function_reset_second_verify,uid,checkToken);
//			if(!flg){
//				json(SystemCode.code_1001, L("非法操作"));
//				return;
//			}
            String url = ApiConfig.getValue("usecenter.url");
            FeignContainer container = new FeignContainer(url.concat("/user"));
            UserApiService userApiService = container.getFeignClient(UserApiService.class);
            Map<String,Boolean> infoMap = userApiService.userCheckTypeApiN(user.getId());
            if(null != infoMap) {
                boolean isRecharge = infoMap.get("isRecharge");
                if(isRecharge){
                    Map<String, String> retMap = userApiService.addressAuthApiN(user.getId(),propTag.toLowerCase(),address,user.getUserContact().getSafeEmail(),ip());
                    if(null != retMap){
                        for(Map.Entry<String,String> entry : retMap.entrySet()){
                            String returnVal = entry.getValue();
                            if("1".equals(entry.getKey())){
                                json(SystemCode.code_1000, L("操作成功"));
                                return;
                            }else{

                                if("-2".equals(returnVal)){
                                    returnVal = L("充值地址输入次数超出限制，将锁定关闭二次验证功能，请24小时之后再试。");
                                }else{
                                    String[] errorMsg = returnVal.split("#");
                                    if(errorMsg.length == 2){
                                        returnVal = L(errorMsg[0], errorMsg[1]);
                                    }else{
                                        returnVal = L(errorMsg[0]);
                                    }
                                }
                                json(SystemCode.code_1001, returnVal);
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    /**
     * 无法提供谷歌和短信验证码---校验手持照片
     * @return
     */
    @Page(Viewer = JSON)
    public void doIdCardAuth(){

        try {
            setLan();
            MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
            MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
            MultipartFile file = multipartRequest.getFile("file");
            String email = param("email");
            //邮箱
            User user = userDao.findOne("userContact.safeEmail",email);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }

            //校验邮箱是否锁定
            DataResponse dr0 = this.checkVerifiCode(user.getId(),MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_EMAIL, MsgToastKey.LOCK_24_HOUR);
            if (!dr0.isSuc()) {
                json(SystemCode.code_1001, L(dr0.getDes()));
                return;
            }
            //校验充值地址是否锁定
            DataResponse dr1 = this.checkVerifiCode(user.getId(),MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_ADDRESS, MsgToastKey.LOCK_24_HOUR);
            if (!dr1.isSuc()) {
                json(SystemCode.code_1001, L(dr1.getDes()));
                return;
            }
            //校验资金密码是否锁定
            DataResponse dr2 = this.checkVerifiCode(user.getId(),MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
            if (!dr2.isSuc()) {
                json(SystemCode.code_1001, L(dr2.getDes()));
                return;
            }
            if (file.isEmpty()) {
                json(SystemCode.code_1001, L("请上传认证图片"));
                return;
            }
            //防跳步
            String checkToken = multipartRequest.getParameter("checkToken");
            boolean flg = EncryptionPhoto.checkToken(Const.function_reset_second_verify,user.getId(),checkToken);
            if(!flg){
                json(SystemCode.code_1001, L("非法操作"));
                return;
            }
            String url = ApiConfig.getValue("usecenter.url");
            FeignContainer container = new FeignContainer(url.concat("/user"));
            UserApiService userApiService = container.getFeignClient(UserApiService.class);
            Map<String,Boolean> infoMap = userApiService.userCheckTypeApiN(user.getId());
            if(null != infoMap) {
                boolean isAuthen = infoMap.get("isAuthen");
                if(isAuthen){

                    String cardFront = QcloudCosUtil.uploadSuffix(file);
                    Map<String, String> retMap = userApiService.idCardAuthApiN(String.valueOf(user.getId()),cardFront);
                    for(Map.Entry<String, String> entry : retMap.entrySet()){
                        String returnVal = entry.getValue();
                        String key = entry.getKey();
                        if("1".equals(key)){
                            json(SystemCode.code_1000, returnVal);
                        }else{
                            json(SystemCode.code_1001, returnVal);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    /**
     * 无法提供谷歌和短信验证码---校验资金密码
     * @returnf
     */
    @Page(Viewer = JSON)
    public void doCheckSafePwd(){

        try {
            setLan();
            //资金密码
            String safePwd = param("safePwd");
            if(StringUtils.isEmpty(safePwd)){
                json(SystemCode.code_1001, L("请输入资金密码"));
                return;
            }
            String email = param("email");
            //邮箱
            User user = userDao.findOne("userContact.safeEmail",email);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            String uid = user.getId();
            //解密新密码
            byte[] decodedData2 = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(safePwd.replace(" ", "+")),priKey);
            safePwd = new String(decodedData2);
            //校验是否锁定
            DataResponse dr1 = this.checkVerifiCode(uid, MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_EMAIL, MsgToastKey.LOCK_24_HOUR);
            if (!dr1.isSuc()) {
                json(SystemCode.code_1001, dr1.getDes());
                return;
            }
            DataResponse dr2 = this.checkVerifiCode(uid, MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_ADDRESS, MsgToastKey.LOCK_24_HOUR);
            if (!dr2.isSuc()) {
                json(SystemCode.code_1001, dr2.getDes());
                return;
            }
            DataResponse dr3 = this.checkVerifiCode(uid, MsgToastKey.RESET_SECOND_VERIFY, ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
            if (!dr3.isSuc()) {
                json(SystemCode.code_1001, dr3.getDes());
                return;
            }

            String url = ApiConfig.getValue("usecenter.url");
            FeignContainer container = new FeignContainer(url.concat("/user"));
            UserApiService userApiService = container.getFeignClient(UserApiService.class);
            Map<String,Boolean> infoMap = userApiService.userCheckTypeApiN(uid);
            if(null != infoMap) {
                boolean isSafePwd = infoMap.get("isSafePwd");
                if(isSafePwd){
                    Map<String, String> retMap = userApiService.checkSafePwdApiN(uid,safePwd, String.valueOf(ConstantCenter.UpdFunctionType.LOGIN_RESET_SECOND_VERIFY_PAY_PWD.getKey()),ip());
                    if(null != retMap){
                        for(Map.Entry<String,String> entry : retMap.entrySet()){
                            String returnVal = entry.getValue();
                            if("1".equals(entry.getKey())){
                                json(SystemCode.code_1000, L("操作成功"));
                                return;
                            }else{

                                if("-2".equals(returnVal)){
                                    returnVal = L("资金密码输入次数超出限制，将锁定关闭二次验证功能，请24小时之后再试。");
                                }else{
                                    String[] errorMsg = returnVal.split("#");
                                    if(errorMsg.length == 2){
                                        returnVal = L(errorMsg[0], errorMsg[1]);
                                    }else{
                                        returnVal = L(errorMsg[0]);
                                    }
                                }
                                json(SystemCode.code_1001, returnVal);
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    /**
     * 忘记登录密码---邮箱验证
     */
    @Page(Viewer = JSON)
    public void doCheckEmailForForgetPwd(){
        try {
            setLan();
            //邮箱
            String email = param("email");
            //邮箱code
            String code = param("code");
            User user = userDao.findOne("userContact.safeEmail",email);
            if (null == user) {
                json(SystemCode.code_1001,L("请输入正确的邮箱"));
                return;
            }
            if(StringUtils.isBlank(code)){
                json(SystemCode.code_1001, L("请输入邮件验证码。"));
                return;
            }
            if(StringUtils.isEmpty(email)){
                json(SystemCode.code_1001,L("请输入正确的电子邮件地址。"));
                return;
            }
            //校验是否锁定
            DataResponse dr1 = this.checkVerifiCode(user.getId(), MsgToastKey.FORGET_LOGIN_PWD, ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
            if (!dr1.isSuc()) {
                json(SystemCode.code_1001, dr1.getDes());
                return;
            }
            DataResponse dr2 = this.checkVerifiCode(user.getId(), MsgToastKey.FORGET_LOGIN_PWD, ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_MOBILE, MsgToastKey.LOCK_24_HOUR);
            if (!dr2.isSuc()) {
                json(SystemCode.code_1001, dr2.getDes());
                return;
            }
            DataResponse dr3 = this.checkVerifiCode(user.getId(), MsgToastKey.FORGET_LOGIN_PWD, ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_GOOGLE, MsgToastKey.LOCK_24_HOUR);
            if (!dr3.isSuc()) {
                json(SystemCode.code_1001, dr3.getDes());
                return;
            }
            // 验证码CheckCode
            DataResponse dr = getDataResponse(user.getId(),PostCodeType.appForgetLoginPwd,email,1,code,LimitType.ForgetPwdEmailError,MsgToastKey.FORGET_LOGIN_PWD,MsgToastKey.LOCK_24_HOUR);
            if (!dr.isSuc()) {
                json(SystemCode.code_1001,L(dr.getDes()));
                return;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            String checkToken = EncryptionPhoto.getToken(Const.function_forgot_login_pwd,user.getId());
            map.put("checkToken", checkToken);
            map.put("uid", user.getId());
            map.put("smsOpen", user.getSmsOpen());
            map.put("googleOpen", user.getGoogleOpen());
            map.put("mobile", user.getUserContact().getSafeMobile());
            json(SystemCode.code_1000,"", JSONObject.toJSONString(map));
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    /**
     * 忘记密码---手机验证
     */
    @Page(Viewer = JSON)
    public void doCheckMobileForForgetPwd(){
        try {
            setLan();
            //手机号
            String mobile = param("mobile");
            //短信code
            String code = param("code");
            if(StringUtils.isBlank(code)){
                json(SystemCode.code_1001, L("请输入短信验证码。"));
                return;
            }
            if(StringUtils.isEmpty(mobile)){
                json(SystemCode.code_1001,L("请输入手机号。"));
                return;
            }
//			String mobileNumber = code + " " + mobile;
            String email = param("email");
            //邮箱
            User user = userDao.findOne("userContact.safeEmail",email);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            //校验是否锁定
            DataResponse dr1 = this.checkVerifiCode(user.getId(), MsgToastKey.FORGET_LOGIN_PWD, ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
            if (!dr1.isSuc()) {
                json(SystemCode.code_1001, dr1.getDes());
                return;
            }
            DataResponse dr2 = this.checkVerifiCode(user.getId(), MsgToastKey.FORGET_LOGIN_PWD, ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_MOBILE, MsgToastKey.LOCK_24_HOUR);
            if (!dr2.isSuc()) {
                json(SystemCode.code_1001, dr2.getDes());
                return;
            }
            DataResponse dr3 = this.checkVerifiCode(user.getId(), MsgToastKey.FORGET_LOGIN_PWD, ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_GOOGLE, MsgToastKey.LOCK_24_HOUR);
            if (!dr3.isSuc()) {
                json(SystemCode.code_1001, dr3.getDes());
                return;
            }
            // 验证码CheckCode
            DataResponse dr = getDataResponse(user.getId(),PostCodeType.appForgetLoginPwdByMobile,mobile,2,code,LimitType.ForgetPwdMobileError,MsgToastKey.FORGET_LOGIN_PWD,MsgToastKey.LOCK_24_HOUR);
            if (!dr.isSuc()) {
                json(SystemCode.code_1001,L(dr.getDes()));
                return;
            }
            json(SystemCode.code_1000, L("操作成功"));
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    /**
     * 忘记密码---谷歌验证
     */
    @Page(Viewer = JSON)
    public void doCheckGoogleForForgerPwd(){
        try {
            setLan();
            //谷歌code
            String code = param("code");
            String email = param("email");
            //邮箱
            User user = userDao.findOne("userContact.safeEmail",email);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            //校验是否锁定
            DataResponse dr1 = this.checkVerifiCode(user.getId(), MsgToastKey.FORGET_LOGIN_PWD, ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
            if (!dr1.isSuc()) {
                json(SystemCode.code_1001, dr1.getDes());
                return;
            }
            DataResponse dr2 = this.checkVerifiCode(user.getId(), MsgToastKey.FORGET_LOGIN_PWD, ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_MOBILE, MsgToastKey.LOCK_24_HOUR);
            if (!dr2.isSuc()) {
                json(SystemCode.code_1001, dr2.getDes());
                return;
            }
            DataResponse dr3 = this.checkVerifiCode(user.getId(), MsgToastKey.FORGET_LOGIN_PWD, ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_GOOGLE, MsgToastKey.LOCK_24_HOUR);
            if (!dr3.isSuc()) {
                json(SystemCode.code_1001, dr3.getDes());
                return;
            }

            ConstantCenter.UpdFunctionType uft = ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_GOOGLE;
            // 验证码CheckCode
            DataResponse dr = verifyGoogle(user.getId(),code,user.getUserContact().getSecret(),uft,MsgToastKey.LOCK_24_HOUR,"2");
            if (!dr.isSuc()) {
                json(SystemCode.code_1001, L(dr.getDes()));
                return;
            }
            json(SystemCode.code_1000, L("操作成功"));
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    /**
     * 忘记登录密码
     */
    @Page(Viewer = JSON)
    public void doForget() {
        try {
            setLan();
            //新密码
            String newPassword = param("newPassword");
            //确认密码
            String confirmPwd = param("confirmPwd");

            if(StringUtils.isEmpty(newPassword)){
                json(SystemCode.code_1001, L("请输入新登录密码"));
                return;
            }
            if(StringUtils.isEmpty(confirmPwd)){
                json(SystemCode.code_1001, L("请输入确认密码"));
                return;
            }
            String email = param("email");
            //邮箱
            User user = userDao.findOne("userContact.safeEmail",email);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            String uid = user.getId();
            //校验是否锁定
            DataResponse dr1 = this.checkVerifiCode(uid, MsgToastKey.FORGET_LOGIN_PWD, ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
            if (!dr1.isSuc()) {
                json(SystemCode.code_1001, dr1.getDes());
                return;
            }
            DataResponse dr2 = this.checkVerifiCode(uid, MsgToastKey.FORGET_LOGIN_PWD, ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_MOBILE, MsgToastKey.LOCK_24_HOUR);
            if (!dr2.isSuc()) {
                json(SystemCode.code_1001, dr2.getDes());
                return;
            }
            DataResponse dr3 = this.checkVerifiCode(uid, MsgToastKey.FORGET_LOGIN_PWD, ConstantCenter.UpdFunctionType.FORGET_LOGIN_PWD_GOOGLE, MsgToastKey.LOCK_24_HOUR);
            if (!dr3.isSuc()) {
                json(SystemCode.code_1001, dr3.getDes());
                return;
            }
            //解密新密码
            byte[] decodedData2 = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(newPassword.replace(" ", "+")),priKey);
            newPassword = new String(decodedData2);
            //确认确认密码
            byte[] decodedData = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(confirmPwd.replace(" ", "+")),priKey);
            confirmPwd = new String(decodedData);

            if(!newPassword.equals(confirmPwd)){
                json(SystemCode.code_1001, L("您的两次密码输入不一致。"));
                return;
            }
            //密码的组成至少要包括大小写字母、数字及标点符号的其中两项
            String regex = "^(?![A-Za-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{8,20}$";
            if (!newPassword.matches(regex)) {
                json(SystemCode.code_1001, L("您的密码需要8-20位，包含字母，数字，符号的两种以上。"));
                return;
            }

            int safeLevel = 0;
            if (newPassword.length() > 8){
                safeLevel = 85;
            }else{
                safeLevel = 50;
            }
            //modify by buxianguan 20180111 资金密码不能和登录密码一致
            if(user.getEncryptedPwd(newPassword).equals(user.getSafePwd())){
                json(SystemCode.code_1001, L("登录密码不得与资金密码一致。"));
                return;
            }
            //防跳步
            String checkToken = param("checkToken");
            boolean flg = EncryptionPhoto.checkToken(Const.function_forgot_login_pwd,uid,checkToken);
            if(!flg){
                json(SystemCode.code_1001, L("非法操作"));
                return;
            }
            UpdateResults<User> ur = userDao.updatePwd(user.get_Id(), newPassword, safeLevel);
            if (ur.getHadError()) {
                json(SystemCode.code_1001, L("修改密码出错，请稍后重试。"));
                return;
            }

//			SessionUser su = toLogin(this,user.getId(), user.getUserName(), ip());
//
//			Map<String, Object> map = new HashMap<String, Object>();
//			Map<String, Object> userMap = getUserInfo(user);
//			map.put("userInfo", userMap);
//			map.put("token", token(user.getId()));
            json(SystemCode.code_1000, L("设置成功"));
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    /*start*/
    /**
     * @describe 用户收藏市场(用户触发)
     * @return json
     * 1.根据市场信息判断用户请求信息是否合法。
     * 2.查询用户历史是否有市场收藏信息：
     * 		Y:判断新增该市场是否已存在历史收藏中,(极少异常情况下会存在,由于cookie和数据库未正常同步)。
     * 			Y：存在更新数据库，仅更新缓存。
     * 			N：不存在根据数据库，并更新缓存数据。
     * 		N：直接新增一条用户与市场的关联信息
     *	缓存中作用：
     *		Key：isUserCollect --> 标示用户是否存在收藏市场
     *			作用：主要用于用户在未收藏任何市场情况下，该标示为0。限制用户每次请求读取缓存为空时，查询数据库操作。
     *		Key：userCollect -->  存储用户关注信息
     */
    @Page(Viewer = ".json")
    public void doUserCollect(){
        setLan();
        String userId = userIdStr();
        String token = param("token");
        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        String marketStr = param("market").replace("/","_");
        if(StringUtils.isEmpty(marketStr)){
            return;
        }
        String[] marketArr = marketStr.split(",");
        for(String market : marketArr){
            JSONObject marketJson = Market.getMarketByName(market);
            if (null == marketJson) {
                json(SystemCode.code_1001, L("收藏失败,市场不存在"));
                return;
            }
            String fullName = marketJson.getString("numberBiFullName");
            market = market.toLowerCase()+"_hotdata_"+fullName;
            String marketTmp = marketJson.getString("market")+"_hotdata_"+fullName;
            if (!market.equalsIgnoreCase(marketTmp)) {
                json(SystemCode.code_1001, L("收藏失败，请求参数异常"));
                return;
            }
            String  collect = "";
            try {
                CollectMarket collectMarket = (CollectMarket) Data.GetOne("SELECT id,userId,collect from collectmarket WHERE userId=? ", new Object[]{userId}, CollectMarket.class);
                if (collectMarket == null) {
                    collect = market;
                    Data.Update("insert into collectmarket (userId,collect) values (?,?) ", new Object[]{userId, collect});
                } else {
                    collect = collectMarket.getCollect();
                    if (collect.indexOf(market) == -1) {
                        collect = collect + "-" + market;
                        Data.Update("UPDATE collectmarket SET collect = '" + collect + "' WHERE userId = ? ", new Object[]{userId});
                    }
                }
                Cache.Set("isUserCollect" + userId(), "1");
                Cache.Set("userCollect" + userId(), collect);
            }catch (Exception e){
                log.error("【收藏市场】用户："+userId+"收藏市场："+market+"异常，异常信息为：",e);
                json(SystemCode.code_1002, L("内部异常") + e.getMessage());
                return;
            }
        }
        json(SystemCode.code_1000, L("收藏成功"));
    }
    /**
     * @describe 用户取消收藏市场(用户触发)
     * @return json
     *1.根据市场信息判断用户请求信息是否合法。
     * 2.查询用户历史是否有市场收藏信息：
     * 		Y:判断该市场是否存在该记录中。
     * 			1.如果仅收藏了该市场，那么直接删除该市场信息。
     * 			2.如果收藏了该市场和其他市场，那么仅移除该市场信息。
     * 			3.刷新缓存。
     * 		N：直接不处理
     */
    @Page(Viewer = ".json")
    public void doCloseCollect(){
        setLan();
        String userId = userIdStr();
        String token = param("token");
        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        String market = param("market");

        if(!StringUtil.exist(market)){
            json(SystemCode.code_1001, L("取消收藏失败,请求参数为空"));
            return;
        }
        market = market.replace("/","_");
        JSONObject marketJson = Market.getMarketByName(market);
        if (null == marketJson) {
            json(SystemCode.code_1001, L("取消收藏失败,市场不存在"));
            return;
        }
        String fullName = marketJson.getString("numberBiFullName");
        market = market.toLowerCase()+"_hotdata_"+fullName;
        String marketTmp = marketJson.getString("market")+"_hotdata_"+fullName;
        if (!market.equals(marketTmp)) {
            json(SystemCode.code_1001, L("取消收藏失败，请求参数异常"));
            return;
        }
        String  collect = "";
        try {
            CollectMarket collectMarket = (CollectMarket) Data.GetOne("SELECT id,userId,collect from collectmarket WHERE userId=? ", new Object[]{userId}, CollectMarket.class);
            if (collectMarket != null) {
                collect = collectMarket.getCollect();
                if (collect.indexOf(market) != -1) {
                    if(collect.equals(market)){
                        Data.Update("delete from collectmarket WHERE userId= ? ", new Object[]{userId});
                        Cache.Set("isUserCollect" + userId(), "0");
                        Cache.Delete("userCollect" + userId());
                    }else{
                        if(collect.startsWith(market)){
                            collect = collect.replace(market+"-","");
                        }else{
                            collect = collect.replace("-"+market,"");
                        }
                        Data.Update("UPDATE collectmarket SET collect='" + collect + "' WHERE userId=? ", new Object[]{userId});
                        Cache.Set("isUserCollect" + userId(), "1");
                        Cache.Set("userCollect" + userId(), collect);
                    }
                }
            }
        }catch (Exception e){
            log.error("【取消收藏】用户："+userId+"取消收藏市场："+market+"异常，异常信息为：",e);
            json(SystemCode.code_1002, L("内部异常") + e.getMessage());
            return;
        }
        json(SystemCode.code_1000, L("取消收藏成功"));
    }
    /**
     * 获取收藏的市场
     */
    @Page(Viewer = JSON)
    public void collects() {
        try {
            setLan();
            //用户ID
            String uid = userIdStr();
            String token = param("token");
            if (isLogin(uid, token) == SystemCode.code_1003 || isLogin(uid, token) == SystemCode.code_402) {
                json(isLogin(uid, token), L(isLogin(uid, token).getValue()));
                return;
            }
            //过滤条件
//			String market = param("market");
            CollectMarket collectMarket = collectMarketDao.getCollectMarket(uid);
            String collect = "";
            List<String> list = new ArrayList<>();
            if(null != collectMarket){
                collect = collectMarket.getCollect();
                if(StringUtils.isNotEmpty(collect)){
                    String[] collectArr = collect.split("-");
                    for(String col : collectArr){
                        list.add(col);
                    }
                }
//				Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
                json(SystemCode.code_1000, L(SystemCode.code_1000.getValue()), list);
            }

        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常") + e.getMessage());
            return;
        }
    }
    /*end*/
    //**************我的钱包********START****************
    FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/payUser");
    PayUserApiService payUserApi = container.getFeignClient(PayUserApiService.class);
    FeignContainer container1 = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/rechargeRecord");
    RechargeRecordApiService rechargeRecordApi = container1.getFeignClient(RechargeRecordApiService.class);
    FeignContainer container2 = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/downloadRecord");
    DownloadRecordApiService downloadRecordApi = container2.getFeignClient(DownloadRecordApiService.class);
    FeignContainer container3 = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/capitalTransfer");
    CapitalTransferApiService capitalTransferApi = container3.getFeignClient(CapitalTransferApiService.class);
    FeignContainer container4 = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/download");
    DowoloadApiService dowoloadApi = container4.getFeignClient(DowoloadApiService.class);
    FeignContainer container5 = new FeignContainer(ApiConfig.getValue("usecenter.url").concat("/user"));
    UserApiService userApi = container5.getFeignClient(UserApiService.class);
    FeignContainer container6 = new FeignContainer(ApiConfig.getValue("usecenter.url").concat("/payUserOtc"));
    PayUserOtcApiService payUserOtcApi = container6.getFeignClient(PayUserOtcApiService.class);
    /**
     * 查看钱包资金信息
     */
    @Page(Viewer = JSON)
    public void getWalletDetail() {
        try {
            setLan();
            String userId = userIdStr();
            String coinTender = param("coinTender");
            String legalTender = param("legalTender");
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            String detail = payUserApi.getWalletDetail(userId);
            AssetEvaluationVO vo = this.getAssetEvaluation(coinTender);
            //获取用户是否通过身份认证
            Map<String, Object> userDownloadLimit = downloadSummaryDao.getDownloadLimit(userId);
            int authResult = (int) userDownloadLimit.get("authResult");
            BigDecimal downloadLimit = (BigDecimal) userDownloadLimit.get("downloadLimit");
            Map map = new HashMap();
            map.put("detail",detail);
            //认证是否通过
            map.put("authResult", authResult);
            //每日限制额度
            map.put("downloadLimit", downloadLimit);
            if(null != vo){

                map.put("walletCoin",vo.getWalletCoin());
            }else{
                map.put("walletCoin","0");
            }
            //获取折算法币汇率
            String rate = getConvertRate(legalTender);
            map.put("rate",rate);
            json(SystemCode.code_1000,"", map);
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常"));
            return;
        }

    }

    /**
     * 查看币币交易账户资金信息
     */
    @Page(Viewer = JSON)
    public void getBiBiDetail() {
        try {
            setLan();
            String userId = userIdStr();
            String token = param("token");
            String legalTender = param("legalTender");
            if(StringUtils.isEmpty(legalTender)){
                json(SystemCode.code_1001, L(SystemCode.code_1001.getValue()));
                return;
            }
            String coinTender = param("coinTender");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            String detail = payUserApi.getDetail(userId);
            AssetEvaluationVO vo = this.getAssetEvaluation(coinTender);
            //封装提现额度
            //获取用户是否通过身份认证
            Map<String, Object> userDownloadLimit = downloadSummaryDao.getDownloadLimit(userId);
            int authResult = (int) userDownloadLimit.get("authResult");
            BigDecimal downloadLimit = (BigDecimal) userDownloadLimit.get("downloadLimit");

            Map map = new HashMap();
            map.put("detail",detail);
            //认证是否通过
            map.put("authResult", authResult);
            //每日限制额度
            map.put("downloadLimit", downloadLimit);
            //折算总资金
            if(null != vo){

                map.put("biBiCoin",vo.getBiBiCoin());
            }else{
                map.put("biBiCoin","0");
            }
            //获取折算法币汇率
            String rate = getConvertRate(legalTender);
            map.put("rate",rate);
            json(SystemCode.code_1000,"", map);
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常"));
            return;
        }

    }

    /**
     * 获取折算法币汇率
     * @param legalTender
     * @return
     */
    public String getConvertRate(String legalTender){
        String converCoin = "usdt_".concat(legalTender).toLowerCase();
        String rate = StringUtil.exist(Cache.Get(converCoin))?Cache.Get(converCoin):"1";
        return rate;
    }

    /**
     * 查看币法账户资金信息
     */
    @Page(Viewer = JSON)
    public void getOtcDetail() {
        try {
            setLan();
            String userId = userIdStr();
            String token = param("token");
            String coinTender = param("coinTender");
            String legalTender = param("legalTender");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            String detail = payUserApi.getOtcDetail(userId);
            AssetEvaluationVO vo = this.getAssetEvaluation(coinTender);
            Map map = new HashMap();
            map.put("detail",detail);
            if(null != vo){

                map.put("biFaCoin",vo.getBiFaCoin());
            }else{
                map.put("biFaCoin","0");
            }

            map.put("storeFreez",vo.getStoreFreez());

            //获取折算法币汇率
            String rate = getConvertRate(legalTender);
            map.put("rate",rate);
            json(SystemCode.code_1000,"", map);
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常"));
            return;
        }

    }

    /**
     * 查看充值记录
     */
    @Page(Viewer = JSON)
    public void getRechargeList(){
        try {
            setLan();
            //页码
            int pageIndex = Integer.parseInt(param("pageIndex"));
            //每页数量
            int pageSize = Integer.parseInt(param("pageSize"));
            //币种
            Long coinTypeId = Long.valueOf(param("coinTypeId"));
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            RechargeRecordVo vo = new RechargeRecordVo();
            vo.setFundsType(coinTypeId.intValue());
            vo.setUserId(userId);
            vo.setTimeTab(0);
            vo.setPageIndex(pageIndex);
            vo.setPageSize(pageSize);
            RechargeRecordBo bo = rechargeRecordApi.getRecord(vo);
            if(null != bo){
                List<DetailsSummaryBeanVo> list = bo.getList();
                for(DetailsSummaryBeanVo detailsSummary : list){
                    String showStatu = getShowStatu(detailsSummary.getType(),detailsSummary.getStatus());
                    detailsSummary.setShowStatuLan(showStatu);
                }

            }
            json(SystemCode.code_1000,"", bo);
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常"));
            return;
        }

    }

    /**
     * 查看提现记录
     */
    @Page(Viewer = JSON)
    public void getWithdrawList(){
        try {
            setLan();
            //页码
            int pageIndex = Integer.parseInt(param("pageIndex"));
            //每页数量
            int pageSize = Integer.parseInt(param("pageSize"));
            //币种
            Long coinTypeId = Long.valueOf(param("coinTypeId"));
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            DownloadRecordVo vo = new DownloadRecordVo();
            vo.setFundsType(coinTypeId.intValue());
            vo.setUserId(userId);
            vo.setTimeTab(0);
            vo.setPageIndex(pageIndex);
            vo.setPageSize(pageSize);
            DownloadRecordBo bo = downloadRecordApi.getList(vo);
            if(null != bo){
                List<DownloadSummaryBeanVo> list = bo.getList();
                for(DownloadSummaryBeanVo downloadSummary : list){
                    String showStat = getShowStat(downloadSummary.getStatus(),downloadSummary.getCommandId());
                    log.info("打币状态："+downloadSummary.getStatus()+"，打币提示："+showStat);
                    downloadSummary.setShowStatLan(showStat);
                }
            }
            json(SystemCode.code_1000,"", bo);
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常"));
            return;
        }

    }

    /**
     * 查看划转记录
     */
    @Page(Viewer = JSON)
    public void getTranceList(){
        try {
            setLan();
            //页码
            int pageIndex = Integer.parseInt(param("pageIndex"));
            //每页数量
            int pageSize = Integer.parseInt(param("pageSize"));
            //划转起止
            int from = Integer.parseInt(param("from"));
            //币种
            Long coinTypeId = Long.valueOf(param("coinTypeId"));
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            TransRecordVo vo = new TransRecordVo();
            vo.setFundsType(coinTypeId.intValue());
            vo.setUserId(userId);
            vo.setTimeTab(0);
            vo.setFrom(from);
            vo.setPageIndex(pageIndex);
            vo.setPageSize(pageSize);
            TransRecordBo bo = capitalTransferApi.getRecord(vo);
            json(SystemCode.code_1000,"", bo);
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常"));
            return;
        }

    }

    /**
     * 充值状态
     * @param type
     * @param status
     * @return
     */
    public String getShowStatu(int type,int status) {
        if (type == 1) {
            switch(status) {
                case 0:
                    return L("确认中");
                case 1:
                    return L("失败");
                case 2:
                    return L("成功");
                default:
                    return L("确认中");
            }
        } else {
            return "-";
        }
    }

    /**
     * 提现状态
     * @param status
     * @param commandId
     * @return
     */
    public String getShowStat(int status,Long commandId) {
        String showStat = "";
        switch(status) {
            case 0:
                if (commandId > 0L) {
                    showStat = L("打币中");
                } else {
                    showStat = L("待处理");
                }
                break;
            case 1:
                showStat = L("失败");
                break;
            case 2:
                showStat = L("成功");
                break;
            case 3:
                showStat = L("已取消");
                break;
            case 4:
            case 5:
                showStat = L("打币中");
                break;
            case 6:
            case 7:
                showStat = L("发送中");
                break;
            default:
                break;

        }

        return showStat;
    }
    /**
     * 获取充值地址
     */
    @Page(Viewer = JSON)
    public void rechargeCoinInfo(){
        try {
            setLan();
            String userId = userIdStr();
//			String userId = param("userId");
//			if (!isLogin(userId, token)) {
//				json(SystemCode.code_1003, L(SystemCode.code_1003.getValue()));
//				return;
//			}
            String returnMap = payUserApi.rechargeCoinInfoN(userId);
            if(StringUtils.isNotEmpty(returnMap)){
                json(SystemCode.code_1000, "",returnMap);
                return;
            }else{
                json(SystemCode.code_1001, L("没有充值地址"));
                return;
            }
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常"));
            return;
        }

    }


    /**
     * 保存提现信息
     */
    @Page(Viewer = JSON)
    public void saveWithdrawalInfo(){
        try {
            setLan();
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            //验证资金密码
            LimitType lt = LimitType.SafePassEntrustError;
            int entrustStatus = lt.GetStatus(userId);
            if (entrustStatus == -1) {
                json(SystemCode.code_1001,L("资金密码输入错误超出限制，锁定该帐户24小时，不得使用重置资金密码和提现功能"));
                return;
            }
            //校验提现资金密码
            DataResponse dr1 = this.checkVerifiCode(userId, MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_PWD, MsgToastKey.LOCK_24_HOUR);
            if (!dr1.isSuc()) {
                json(SystemCode.code_1001, dr1.getDes(),"");
                return;
            }
            //校验邮箱密码
            DataResponse dr2 = this.checkVerifiCode(userId, MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_EMAIL, MsgToastKey.LOCK_24_HOUR);
            if (!dr2.isSuc()) {
                json(SystemCode.code_1001, dr2.getDes(),"");
                return;
            }
            //校验手机验证码
            DataResponse dr3 = this.checkVerifiCode(userId, MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_MOBILE, MsgToastKey.LOCK_24_HOUR);
            if (!dr3.isSuc()) {
                json(SystemCode.code_1001, dr3.getDes(),"");
                return;
            }
            //校验谷歌验证码
            DataResponse dr4 = this.checkVerifiCode(userId, MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.UPD_PAY_PWD_GOOGLE, MsgToastKey.LOCK_24_HOUR);
            if (!dr4.isSuc()) {
                json(SystemCode.code_1001, dr4.getDes(),"");
                return;
            }
            //校验资金密码
            DataResponse dr5 = this.checkVerifiCode(userId, MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.WITHDRAWAL_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
            if (!dr5.isSuc()) {
                json(SystemCode.code_1001, dr5.getDes(),"");
                return;
            }
            //校验手机验证码
            DataResponse dr6 = this.checkVerifiCode(userId, MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.WITHDRAWAL_MOBILE, MsgToastKey.LOCK_24_HOUR);
            if (!dr6.isSuc()) {
                json(SystemCode.code_1001, dr6.getDes(),"");
                return;
            }
            //校验谷歌验证码
            DataResponse dr7 = this.checkVerifiCode(userId, MsgToastKey.WITHDRAWAL, ConstantCenter.UpdFunctionType.WITHDRAWAL_GOOGLE, MsgToastKey.LOCK_24_HOUR);
            if (!dr7.isSuc()) {
                json(SystemCode.code_1001, dr7.getDes(),"");
                return;
            }
            //otc发布广告资金密码输错
            DataResponse dr8 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.WITHDRAWAL,ConstantCenter.UpdFunctionType.OTC_CAD_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
            if (!dr8.isSuc()) {
                json(SystemCode.code_1001, dr8.getDes(),"");
                return;
            }
            //otc发布广告资金密码输错
            DataResponse dr9 = this.checkVerifiCode(String.valueOf(userId), MsgToastKey.WITHDRAWAL,ConstantCenter.UpdFunctionType.OTC_RELEASECOIN_PAY_PWD, MsgToastKey.LOCK_24_HOUR);
            if (!dr9.isSuc()) {
                json(SystemCode.code_1001, dr9.getDes(),"");
                return;
            }
            User user = userDao.findOne("_id",userId);
            if(user.getCustomerOperation().equals(Const.CUSTOMER_OPERATION_NO_CASH)){
                json(SystemCode.code_1001,L("提交失败，当前账户无提现权限"));
                return;
            }

            long addressId = longParam("addressId");
            receiveDao.setCoint(coint);
            ReceiveAddr receive = receiveDao.getById(addressId);
            if (null == receive || !receive.getUserId().equals(userId)) {
                log.info("提币地址不存在-地址校验：【"+addressId+"】"+"【"+request.getParameter("coint")+"】");
                json(SystemCode.code_1001, "提币地址不存在");
                return;
            }else{
                if(receive.getAgreement() != null){
                    coint = DatabasesUtil.getUsdtAggrement(receive.getAgreement());
                }
                //提现锁定条件
                //非首次切换模式 && 切换时间<24小时
                //安全模式 && 地址创建时间在24小时之内
                if (user.getWithdrawAddressAuthenSwitchStatus() == 2
                        && TimeUtil.getOriginDiffDay(now(), user.getWithdrawAddressAuthenModifyTime()) < 1) {
                    json(SystemCode.code_1001, "您的帐户因切换模式被锁定，在此期间不能进行提现操作，请等待24小时后自动解锁。");
                    return;
                }
                if(user.getWithdrawAddressAuthenType() == WithdrawAddressAuthenType.SECURITY.getKey()
                        && receive.getCreateTime().after(user.getWithdrawAddressAuthenModifyTime())
                        && TimeUtil.getOriginDiffDay(now(), receive.getCreateTime()) < 1) {
                    json(SystemCode.code_1001, "您当前为“安全模式”，新增提现地址后将被锁定24小时。");
                    return;
                }
            }
            //币种
            String coinTypeIdStr = param("coinTypeId");
            //提币数量
            String amountStr = param("amount");
            //交易密码
            String safePwd = param("safePwd");
            //提现地址
//			String address = param("address");
            //邮箱验证码
            String emailCode = param("emailCode");
//			//邮箱验证码:1-短信；2-谷歌
//			String checkType = param("checkType");
            //短信验证码
            String smsCode = param("smsCode");
            //谷歌验证码
            String googleCode = param("googleCode");

            if(StringUtils.isEmpty(coinTypeIdStr)){
                json(SystemCode.code_1001, L("请选择币种"));
                return;
            }
            //币种
            int coinTypeId = Integer.valueOf(coinTypeIdStr);
            CoinProps coinProps = DatabasesUtil.coinProps(coinTypeId);
            if(null == coinProps){
                json(SystemCode.code_1001, L("找不到此币种!"));
                return;
            }else{
                //是否支持提现
                boolean canWithdraw = coinProps.isCanWithdraw();
                if(!canWithdraw){
                    json(SystemCode.code_1001,L("%%提现暂停",coinProps.getPropTag()));
                    return;
                }
            }
            if(StringUtils.isEmpty(amountStr)){
                json(SystemCode.code_1001, L("%%提现数量不得为空",coinProps.getPropTag()));
                return;
            }
//			if(StringUtils.isEmpty(address)){
//				json(SystemCode.code_1001, L("请输入提现地址"));
//				return;
//			}
            if(StringUtils.isEmpty(safePwd)){
                json(SystemCode.code_1001, L("请输入资金密码"));
                return;
            }
//			if(StringUtils.isEmpty(emailCode)){
//				json(SystemCode.code_1001, L("请输入邮件验证码"));
//				return;
//			}
            String checkType;
            if (StringUtils.isNotEmpty(smsCode)) {
                //短信验证
                checkType = "1";
            } else if (StringUtils.isNotEmpty(googleCode)) {
                //谷歌验证
                checkType = "2";
            } else {
                json(SystemCode.code_1001, L("失败"));
                return;
            }

//			if("1".equals(checkType) && StringUtils.isEmpty(smsCode)){
//				json(SystemCode.code_1001, L("请输入短信验证码"));
//				return;
//			}
//			if("2".equals(checkType) && StringUtils.isEmpty(googleCode)){
//				json(SystemCode.code_1001, L("请输入谷歌验证码。"));
//				return;
//			}


            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }

//			String sql = "select userId, userVID from fin_userfinancialinfo where userId = ?";
//			UserFinancialInfo userFinancialInfo = null;
//			userFinancialInfoDao.setDatabase("vip_financial");
//			userFinancialInfo = (UserFinancialInfo) userFinancialInfoDao.get(sql,new Object[]{userId},UserFinancialInfo.class);
//			if(userFinancialInfo != null){
//				if(userFinancialInfo.getAuthPayFlag() != 1 && StringUtils.isNotEmpty(userFinancialInfo.getUserVID())){
//					log.info("当前用户已设置VID地址"+userId);
//				}else{
//					log.info("当前用户未设置VID地址"+userId);
//					json(SystemCode.code_1001, L("请绑定VID地址"));
//					return;
//				}
//			}


            //校验资金密码
            byte[] decodedData = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(safePwd.replace(" ", "+")),priKey);
            safePwd = new String(decodedData);
            Map<String, String> retMap = userApi.checkPayPwdApiN(userId,safePwd, String.valueOf(ConstantCenter.UpdFunctionType.WITHDRAWAL_PAY_PWD.getKey()));

            for(Map.Entry<String,String> entry : retMap.entrySet()){
                String returnVal = entry.getValue();
                if("1".equals(entry.getKey())){
//					json(SystemCode.code_1000, L("操作成功"));
                    break;
                }else{

                    if("-2".equals(returnVal)){
                        returnVal = L("资金密码输入次数超出限制，将锁定提现、修改资金密码、设置收款方式功能，请24小时之后再试");
                    }else{
                        String[] errorMsg = returnVal.split("#");
                        if(errorMsg.length == 2){
                            returnVal = L(errorMsg[0], errorMsg[1]);
                        }else{
                            returnVal = L(errorMsg[0]);
                        }
                    }
                    json(SystemCode.code_1001, returnVal);
                    return;
                }
            }
//			//校验邮箱
//			DataResponse dr = getDataResponse(user.getId(),PostCodeType.appWithdraw,user.getUserContact().getSafeEmail(),1,emailCode,LimitType.WithdrawEmailPassError,MsgToastKey.WITHDRAWAL,MsgToastKey.LOCK_24_HOUR);
//			if (!dr.isSuc()) {
//				json(SystemCode.code_1001,L(dr.getDes()));
//				return;
//			}
            //校验短信
            if(user.getSmsOpen() && "1".equals(checkType)){
                DataResponse smsOpenDr = getDataResponse(user.getId(),PostCodeType.appWithdraw,user.getUserContact().getSafeMobile(),2,smsCode,LimitType.WithdrawMobilePassError,MsgToastKey.WITHDRAWAL,MsgToastKey.LOCK_24_HOUR);
                if (!smsOpenDr.isSuc()) {
                    json(SystemCode.code_1001,L(smsOpenDr.getDes()));
                    return;
                }
            }

            //校验谷歌
            if(user.getGoogleOpen() && "2".equals(checkType)){
                ConstantCenter.UpdFunctionType uft = ConstantCenter.UpdFunctionType.WITHDRAWAL_GOOGLE;
                DataResponse googleOpendDr = verifyGoogle(user.getId(),googleCode,user.getUserContact().getSecret(),uft,MsgToastKey.LOCK_24_HOUR,"3");
                if (!googleOpendDr.isSuc()) {
                    json(SystemCode.code_1001, L(googleOpendDr.getDes()));
                    return;
                }
            }
            //提现数量
            BigDecimal amount = new BigDecimal(amountStr);
//			DownloadInfoVo vo = dowoloadApi.getInfo(user.getId(),coinTypeId);
            DownloadVo downloadVo = new DownloadVo();
            downloadVo.setUserId(String.valueOf(userId));
            downloadVo.setCashAmount(amount);
            downloadVo.setFees(coint.getMinFees());
            downloadVo.setPropTag(coint.getPropTag().toLowerCase());
            downloadVo.setReceiveAddress(receive.getAddress());
            downloadVo.setSafePwd(safePwd);
            downloadVo.setMobileCode(smsCode);
            downloadVo.setEmailCode(emailCode);
            downloadVo.setGoogleCode(googleCode);
            downloadVo.setLiuyan("");
            downloadVo.setMemo("");
            downloadVo.setFundsType(coinTypeId);
            downloadVo.setIp(ip());


            com.messi.user.vo.R r = dowoloadApi.doSubmit(downloadVo);
            if(r.getFlag()){
                String finalContent = "用户："+user.getUserName()+",用户ID【"+userId+"】，于"+TimeUtil.getDateToString(new Date())+"申请提现【"+amount.toPlainString()+"】【"+coint.getTag()+"】，请客服人员立即处理。";
                log.info("10100401VIPTX【提现审核-APP】:" + finalContent);
//				log.info("BIBITXSH【提现审核申请】:" + "用户:" + user.getNickname() + "发起了提现审核,请客服人员尽快处理");
                json(SystemCode.code_1000, L("提现成功"));
            }else{
                String message = FormatUtils.formatForVip(r.getMsg(), r.getParam());
                json(SystemCode.code_1001, L(message));
            }
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常"));
            return;
        }

    }
    /**
     * 划转
     */
    @Page(Viewer = JSON)
    public void doTransfer(){
        try {
            setLan();
            //划转起
            int from = Integer.parseInt(param("from"));
            //划转止
            int to = Integer.parseInt(param("to"));
            //提币数量
            String amountStr = param("amount");
            //币种
            int coinTypeId = Integer.valueOf(param("coinTypeId"));
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.findOne("_id",userId);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            CoinProps coinProps = DatabasesUtil.coinProps(coinTypeId);
            if(null == coinProps){
                json(SystemCode.code_1001, L("找不到此币种!"));
                return;
            }
            if(StringUtils.isEmpty(amountStr)){
                json(SystemCode.code_1001, L("%%提现数量不得为空",coinProps.getPropTag()));
                return;
            }
            //提现数量
            BigDecimal amount = new BigDecimal(amountStr);
            boolean flg = capitalTransferApi.transfer(from,to,amount,coinTypeId,userId);
            if(flg){
                json(SystemCode.code_1000, L("划转成功"));
            }else{
                json(SystemCode.code_1001, L("划转失败"));
            }
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常"));
            return;
        }

    }

    /**
     * 获取OTC资金信息
     */
    @Page(Viewer = JSON)
    public void queryPayUserOtc(){
        try {
            setLan();
            //页码
            int pageIndex = Integer.parseInt(param("pageIndex"));
            //每页数量
            int pageSize = Integer.parseInt(param("pageSize"));
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.findOne("_id",userId);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("page",pageIndex);
            params.put("limit",pageSize);
            params.put("userId",userId);
            //查询列表数据
            PageQuery pageQuery = payUserOtcApi.queryPayUserOtc(params);
            PageUtils pageUtil = new PageUtils(pageQuery.getList(),
                    Integer.valueOf(String.valueOf(pageQuery.getTotalRow())),
                    Integer.valueOf(String.valueOf(pageQuery.getPageNumber())),
                    Integer.valueOf(String.valueOf(pageQuery.getPageSize())));
            json(SystemCode.code_1000, "",pageUtil);
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常") + e.getMessage());
            return;
        }
    }
    /**
     * 获取OTC资金记录
     */
    @Page(Viewer = JSON)
    public void queryBillOtc(){
        try {
            setLan();
            //页码
            int pageIndex = Integer.parseInt(param("pageIndex"));
            //每页数量
            int pageSize = Integer.parseInt(param("pageSize"));

            int coinTypeId = Integer.parseInt(param("coinTypeId"));
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.findOne("_id",userId);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("page",pageIndex);
            params.put("limit",pageSize);
            params.put("userId",userId);
            params.put("coinTypeId",coinTypeId);
            //查询列表数据
            PageQuery pageQuery = payUserOtcApi.queryBillOtc(params);
            PageUtils pageUtil = new PageUtils(pageQuery.getList(),
                    Integer.valueOf(String.valueOf(pageQuery.getTotalRow())),
                    Integer.valueOf(String.valueOf(pageQuery.getPageNumber())),
                    Integer.valueOf(String.valueOf(pageQuery.getPageSize())));
            json(SystemCode.code_1000, "",pageUtil);
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常"));
            return;
        }
    }
    /**
     * 获取OTC交易记录详情
     */
    @Page(Viewer = JSON)
    public void findBillOtc(){
        try {
            setLan();
            //id
            Long id = Long.valueOf(param("id"));
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.findOne("_id",userId);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            //查询数据
            BillOtcVO billOtcVO = payUserOtcApi.getBillOtc(id);
            if(null != billOtcVO){
                json(SystemCode.code_1000, "",billOtcVO);
            }else {
                json(SystemCode.code_1001, "error");
            }
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常"));
            return;
        }
    }

    /**
     * 查询OTC冻结记录列表
     */
    @Page(Viewer = JSON)
    public void queryOtcFrozenBill(){
        try {
            setLan();
            //页码
            int pageIndex = Integer.parseInt(param("pageIndex"));
            //每页数量
            int pageSize = Integer.parseInt(param("pageSize"));
            //币种
            String coinTypeId = param("coinTypeId");
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.findOne("_id",userId);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            if(pageSize == 0){
                pageSize = PAGE_SIZE;
            }
            List<OtcFrozenBill> otcFrozenBills = otcFrozenBillDao.quertOtcFrozenBill(pageIndex-1,pageSize,coinTypeId,userId);
            int count = otcFrozenBillDao.getOtcFrozenBillCount(coinTypeId,userId);
            Map<String, Object> map = new HashMap<>();
            map.put("list", otcFrozenBills);
            map.put("pageIndex", pageIndex);
            map.put("pageSize", pageSize);
            map.put("total", count);
            map.put("totalPage", getTotalPage(count, pageSize));
            json(SystemCode.code_1000, map);
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常"));
            return;
        }
    }

    /**
     * 获取冻结交易记录详情
     */
    @Page(Viewer = JSON)
    public void findOtcFrozenBill(){
        try {
            setLan();
            String id = param("id");
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.findOne("_id",userId);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            //查询数据
            OtcFrozenBill otcFrozenBill = otcFrozenBillDao.findOtcFrozenBill(id);
            json(SystemCode.code_1000, "",otcFrozenBill);
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常"));
            return;
        }
    }

    /**
     * 获取用户资产评估包含钱包、币法、币币
     */
    @Page(Viewer = JSON)
    public void getAssetEvaluation(){
        try {
            setLan();
            String coinTender = param("coinTender");
            String legalTender = param("legalTender");
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.findOne("_id",userId);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            //查询数据
            AssetEvaluationVO vo = payUserApi.getAssetEvaluation(userId,coinTender,legalTender);
            json(SystemCode.code_1000, "",vo);
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常"));
            return;
        }
    }
    public AssetEvaluationVO getAssetEvaluation(String coinTender){
        try {
            setLan();
            String userId = userIdStr();
            //查询数据
            AssetEvaluationVO vo = payUserApi.getAssetEvaluation(userId,coinTender,"USD");
            return vo;
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常"));

        }
        return null;
    }
    /**
     * 取消提现
     */
    @Page(Viewer = JSON)
    public void doCancelDownload(){
        try {
            setLan();
            Long downloadId = Long.valueOf(param("downloadId"));
            int fundsType = Integer.valueOf(param("fundsType"));
            //临时处理，把102的币种类型转换成10，为了兼容
            if (fundsType == 102) {
                fundsType = 10;
            }

            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.findOne("_id",userId);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            DownloadDao drDao = new DownloadDao();
            CoinProps coinProps = DatabasesUtil.coinProps(fundsType);
            drDao.setCoint(coinProps);
//			DownloadBean downloadBean = (DownloadBean) drDao.findOne(downloadId);
            DownloadSummaryBean downloadBean = (DownloadSummaryBean) downloadSummaryDao.getOne(downloadId);
            if(null != downloadBean){
                if(downloadBean.getStatus() == 0 && downloadBean.getCommandId()> 0){
                    json(SystemCode.code_1001,L("该笔提现已确认，不可取消"));
                    return;
                }
                if("VDS生态回馈提现".equals(downloadBean.getRemark())){
                    json(SystemCode.code_1001,L("该笔提现为VDS生态回馈提现，不可取消"));
                    return;
                }
            }else{
                json(SystemCode.code_1001, L("内部异常"));
                return;
            }
            boolean flg = dowoloadApi.cancelDownload(userId,downloadId,fundsType,1);
            if(flg){
                json(SystemCode.code_1000, "操作成功！");
            }else {
                json(SystemCode.code_1001, "操作失败。");
            }
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常"));
            log.error("内部异常", e);
            return;
        }
    }
    /**
     * 分页获取用户提现地址列表
     */
    @Page(Viewer = JSON)
    public void getAddressPage() {
        setLan();
        String userId = userIdStr();
        String token = param("token");
        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        User user = userDao.findOne("_id",userId);
        if (null == user) {
            json(SystemCode.code_3004);
            return;
        }
        receiveDao.setCoint(coint);

        int pageIndex = intParam("pageIndex");
        if (pageIndex == 0) {
            pageIndex = 1;
        }
        int pageSize = intParam("pageSize");
        if (pageSize == 0) {
            pageSize = 5;
        }
        String agreement = param("agreement");
        List<ReceiveAddr> list = new ArrayList<>();
        int count = receiveDao.findAddrCount(userId);
        if (count > 0) {
            if("102".equals(agreement)){
                list = receiveDao.findAddrPageForUsdte(userId, pageSize * (pageIndex - 1), pageSize);
            }else{
                list = receiveDao.findAddrPageForApp(userId, pageSize * (pageIndex - 1), pageSize);
            }
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
                if (user.getWithdrawAddressAuthenType() == WithdrawAddressAuthenType.SECURITY.getKey()
                        && receiveAddr.getCreateTime().after(user.getWithdrawAddressAuthenModifyTime())
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
        json(SystemCode.code_1000, "",result.toJSONString());
    }

    /**
     * 添加提现地址，新
     */
    @Page(Viewer = JSON)
    public void doAddAddress() {
        try {
            setLan();
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.findOne("_id",userId);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            String address = param("address");
            String addressTag = param("addressTag");
            String mobileCode = param("mobileCode");
            String memo = param("memo");
            String agreement = param("agreement");
            //地址标签长度校验
            if (StringUtils.isNotBlank(memo) && memo.length() > 20) {
                json(SystemCode.code_1001, L("标签不得超过20个字符"));
                return;
            }
            if (StringUtils.isNotEmpty(agreement) && DatabasesUtil.getUsdtAggrement(Integer.valueOf(agreement)) == null) {
                json(L("PARAM ERROR"), false, "");
                return;
            }
            int addAddressLock = doGetErrorTimes(userId,LimitType.addAddress);
            if(addAddressLock == -2){
                json(SystemCode.code_1001, L("新增提现地址功能已被锁定，请24小时之后再试"));
                return;
            }

            receiveDao.setCoint(coint);
            Message msg = receiveDao.addReceiveAddrForApp(lan, user, mobileCode, memo, address, ip(),addressTag,StringUtils.isEmpty(agreement)?null:Integer.valueOf(agreement));
            if(!msg.isSuc()){
//				json("", msg.isSuc(), L(msg.getMsg()));
//				com.alibaba.fastjson.JSONObject obj = com.alibaba.fastjson.JSONObject.parseObject(msg.getMsg());
//				String toast = obj.getString("address");
                json(SystemCode.code_1001, msg.getMsg());
                return;
            }
            json(SystemCode.code_1000, L("设置成功"));
        } catch (Exception ex) {
            log.error("内部异常", ex);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }


    /**
     * 折算法币
     */
    @Page(Viewer = JSON)
    public void getConvertRate(){
        try {
            String convertLegal = param("convertLegal");
            BigDecimal convertRate = payUserApi.getConvertRateN(convertLegal.toUpperCase());
            Map<String,Object> map = new HashMap<>();
            map.put("rate",convertRate);
            json(SystemCode.code_1000,"", JSONObject.toJSONString(map));
        }catch (Exception e){
            json(SystemCode.code_1002, L("内部异常"));
            return;
        }
    }
    //**************我的钱包********END******************


    /**
     * 个人资产
     */
    @Page(Viewer = JSON)
    public void getUserAssets() {
        try {
            setLan();
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.findOne("_id",userId);
            if(user.isFreez()){
                json(SystemCode.code_1001, L("该账户已冻结，暂时不能操作。"));
                return;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("token", token);

            Map<String, JSONArray> accountMap = getUserAssetInfo(user);
            /** start 20170930 xzhang  放开USDC限制*/
//			if (accountMap != null) {
//				JSONArray jSONArray = (JSONArray)accountMap.get("balances");//得到每个key多对用value的值
//				for(int i=0;i<jSONArray.size();i++){
//					JSONObject json = jSONArray.getJSONObject(i);
//					String tag = json.getString("propTag");
//					if(tag!=null ) {
//						String imgTag = tag.toLowerCase();
//						if (tag.equals("USDC")) {
//							jSONArray.remove(i);
//						}
//					}
//				}
//			}
            /** end*/
            P2pUser p2pUser = new P2pUser();
            p2pUser.setUserId(userId);
            Map<String, PayUserBean> payUsers = UserCache.getUserFundsLoan(p2pUser.getUserId());

            JSONObject prices = LoanAutoFactory.getPrices();
            p2pUserDao.resetAsset(payUsers, prices, p2pUser);//计算用户的总资产USD
            p2pUserDao.resetBtcAssets(payUsers, prices, p2pUser);//计算总资产
            map.put("totalAmount", p2pUser.getTotalAssets());//折合总资产
            map.put("userAccount", accountMap);
            json(SystemCode.code_1000, map);
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    /**
     * 获取交易货币配置
     */
    @Page(Viewer = JSON)
    public void getCurrencySet() {
        setLan();
        int version = intParam("version");
        int curVersion = 1;
        List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
        Map<String,CoinProps> coinMaps =   DatabasesUtil.getCoinPropMaps();

        if(coinMaps!=null){

            Iterator<Entry<String,CoinProps>> iter = coinMaps.entrySet().iterator();
            while(iter.hasNext()){
                Entry<String,CoinProps> entry = iter.next();
                String key = entry.getKey();
                CoinProps coin = entry.getValue();

                Map<String, Object> coinMap = new HashMap<String, Object>();
                coinMap.put("currency",  coin.getPropTag());
                coinMap.put("symbol",coin.getUnitTag());
                coinMap.put("name", coin.getPropCnName());
                coinMap.put("englishName",  coin.getPropTag());
				/*if(key.equalsIgnoreCase("eth")){
					coinMap.put("coinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_eth_type_sm@3x.png");
					coinMap.put("financeCoinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_eth_finance.png");
				}else if(key.equalsIgnoreCase("etc")){
					coinMap.put("coinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_etc_type_sm@3x.png");
					coinMap.put("financeCoinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_etc_finance.png");
				}else{
					coinMap.put("coinUrl", "");
					coinMap.put("financeCoinUrl", "");
				}	*/
                if(coin.getPropTag()!=null ){
                    String imgTag = coin.getPropTag().toLowerCase();
//						coinMap.put("coinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_"+imgTag+"_type_sm@3x.png");
                    coinMap.put("coinUrl", STATIC_DOMAIN + "/statics/img/common/"+imgTag+".png");
                    coinMap.put("financeCoinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_"+imgTag+"_finance.png");

                }
                coinMap.put("prizeRange", "0.05");

                coinMap.put("dayFreetrial", coin.getDayFreetrial());//日免审额度
                coinMap.put("dayCash", coin.getDayCash());//每日允许提现的额度
                coinMap.put("timesCash", coin.getTimesCash());//次提现额度
                coinMap.put("minFees", coin.getMinFees());//交易手续费
                coinMap.put("inConfirmTimes", coin.getInConfirmTimes());//充值到账的确认次数
                coinMap.put("outConfirmTimes",coin.getOutConfirmTimes());//允许提现的确认次数
                coinMap.put("minCash", coin.getMinCash());//最小提现额度


                JSONArray jMLs = new JSONArray();
                jMLs.add(getMarketLength(key.toUpperCase()));

                coinMap.put("marketDepth", new JSONArray());
                coinMap.put("marketLength", jMLs);

                list.add(coinMap);
            }
        }

        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("currencySets", list);
        retMap.put("version", curVersion);
        json(SystemCode.code_1000, retMap);
    }

    /**
     * 实名认证
     */
    @Page(Viewer = JSON)
    public void doAuthSave() {
        try {
            setLan();
            String userId = userIdStr();
            if ("0".equals(userId)) {
                json(SystemCode.code_1001,L("用户不存在。"));
                return;
            }
//			String value = Cache.Get(Const.black_key+userIdStr());
//			if(StringUtils.isNotBlank(value)){
//				json(SystemCode.code_1001,L("保存失败。"));
//				return;
//			}
            MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
            MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
            List<MultipartFile> files = multipartRequest.getFiles("file");
            //证件类型：1-身份证，2-护照
            String cardType = multipartRequest.getParameter("cardType");
            //正面证件照
            String frontalImg = "";
            //证件背面照
            String backImg = "";
            //手持证件照
            String loadImg = "";

            if(CollectionUtils.isNotEmpty(files) && files.size() >= 2){

                //身份证
                if("1".equals(cardType)){
                    //正面
                    MultipartFile frontalFile = files.get(0);
                    frontalImg = QcloudCosUtil.uploadSuffix(frontalFile);
                    //反面
                    MultipartFile backFile = files.get(1);
                    backImg = QcloudCosUtil.uploadSuffix(backFile);
                    //手持
                    MultipartFile loadFile = files.get(2);
                    loadImg = QcloudCosUtil.uploadSuffix(loadFile);
                }else{
                    //正面
                    MultipartFile frontalFile = files.get(0);
                    frontalImg = QcloudCosUtil.uploadSuffix(frontalFile);
                    //手持
                    MultipartFile loadFile = files.get(1);
                    loadImg = QcloudCosUtil.uploadSuffix(loadFile);
                }
            }else{
                json(SystemCode.code_1001, L("请上传认证图片"));
                return;
            }
            String errorMsg = null;

            User user = userDao.findOne("_id",userId);
            String ip = ip();
            String firstName = multipartRequest.getParameter("firstName");
            String lastName = multipartRequest.getParameter("lastName");
            String realName = lastName + firstName;
            log.info("收到的实名认证姓名："+realName);
            //证件号
            String cardId = multipartRequest.getParameter("cardId");
            //国际区号
            String countryCode = multipartRequest.getParameter("countryCode");
            //国籍
            String countryName = multipartRequest.getParameter("countryName");
//			if (StringUtils.isNotBlank(frontalImg)) {
//				frontalImg = QcloudCosUtil.getHost() + frontalImg;
//			}
//			if (StringUtils.isNotBlank(backImg)) {
//				backImg = QcloudCosUtil.getHost() + backImg;
//			}
//
//			if (StringUtils.isNotBlank(loadImg)) {
//				loadImg = QcloudCosUtil.getHost() + loadImg;
//			}
            if (StringUtils.isBlank(cardType)) {
                json(SystemCode.code_1001,L("认证类型不能为空"));
                return;
            }
            if (StringUtils.isBlank(realName)) {
                json(SystemCode.code_1001,L("用户名不能为空"));
                return;
            }
            //海外地区
            int area = 3;
            if ("+86".equals(countryCode)) {
                //大陆
                area = 1;
            } else if ("+852".equals(countryCode) || "+853".equals(countryCode) || "+886".equals(countryCode)) {
                //港澳台
                area = 2;
            }
            com.google.code.morphia.query.Query<Authentication> q = auDao.getQuery();
            q.filter("cardId", cardId);
            q.filter("cardType", cardType);
            List<Authentication> dataList = auDao.findPage(q, 1, 20);
//			Authentication auth = auDao.getByUserId(userIdStr());
//			if(auth != null ){
//				//判断锁定状态
//				int lockState = auth.getLockStatus();
//				if (lockState == 1) {
//					Timestamp lockTime = auth.getLockTime();
//					if(System.currentTimeMillis() - lockTime.getTime() <= 72 * 60 * 60 * 1000){
//						json(SystemCode.code_1001,L("保存失败。"));
//						return;
//					}
//				}
//			}
            if (!CollectionUtils.isEmpty(dataList)) {
                for (Authentication authentication : dataList) {
                    if (authentication.getStatus() != AuditStatus.a1NoPass.getKey()) {
                        if("1".equals(cardType)){
                            json(SystemCode.code_1001,L("您的身份证号码已存在"));
                            return;
                        } else {
                            json(SystemCode.code_1001,L("您的护照号码已存在"));
                            return;
                        }
                    }


                }
            }

//			Authentication au = new Authentication(auDao.getDatastore());
//			au.setIp(ip);
//			au.setSubmitTime(now());
//			au.setAreaInfo(area);
//			au.setRealName(realName);
//			au.setCardId(cardId);
//			au.setUserId(userId);
//			au.setFrontalImg(frontalImg);
//			au.setBackImg(backImg);
//			au.setLoadImg(loadImg);
//			au.setStatus(AuditStatus.a1NoAudite.getKey());
//			// 比对一致
//			au.setServiceStatu(3);
//			au.setImgCode("");
//			au.setCountryName(countryName);
//			au.setPhoto("");
//			au.setCountryCode(countryCode);
//			au.setCardType(cardType);
//			au.setSimplePass(true);
            String url = ApiConfig.getValue("usecenter.url");
            //http请求
            FeignContainer container = new FeignContainer(url+"/user");
            //调用指定类的接口
            UserApiService userApiService = container.getFeignClient(UserApiService.class);
            AuthenticationVo auv = new AuthenticationVo();
            auv.setIp(ip);
            auv.setRealName(realName);
            auv.setCardId(cardId);
            auv.setUserId(userId);
            auv.setFrontalImg(frontalImg);
            auv.setBackImg(backImg);
            auv.setLoadImg(loadImg);
            auv.setStatus(AuditStatus.a1NoAudite.getKey());
            // 比对一致
            auv.setCountryName(countryName);
            auv.setCountryCode(countryCode);
            auv.setCardType(cardType);
            //存入数据库
            R r = userApiService.authSaveApin(auv);
            if(!r.getFlag()){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if("en".equals(lan)){
                    format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                }
                String loginTime = format.format(new Date());
                String errMsg =L(r.getMsg(), loginTime);
                json(SystemCode.code_1001, errMsg);
                return;
            }
            //json(L("资料提交成功，请耐心等待审核。"), true, "");
            json(SystemCode.code_1000,"资料提交成功，请耐心等待审核。");
            log.info("10100301VIPKQSFRZ【实名认证-APP】:" + "用户:" + user.getUserName() + "申请了实名认证，请客服人员立即处理。");
            logDao.insertOneRecord(AuthenType.realnameApply.getKey(),userId, "0", "发起实名认证请求。", ip());
            return;
        } catch (Exception e) {
            log.error("内部异常", e);
            //json(L("认证出错，请稍后重试"), false, "");
            json(SystemCode.code_1001,L("认证出错，请稍后重试"));
            return;
        }
    }

    /**
     * 1、 返回用户手续费信息。
     * 2、预留该接口扩展功能，作为后续字段扩展。作为用户明细统一出口
     */
    @Page(Viewer = JSON)
    public void getUserFee() {

        Map<String, Object> map = new HashMap<>();
        String userId = userIdStr();
        try {
            if ("0".equals(userId)) {
                json(SystemCode.code_1001,L("用户不存在。"));
                return;
            }
            User user = userDao.findOne("_id",userId);
            if(user.isFreez()){
                json(SystemCode.code_1001, L("该账户已冻结，暂时不能操作。"));
                return;
            }
            Map<String, com.alibaba.fastjson.JSONObject> marketMaps = CommonUtil.sortMapByValue(Market.getMarketsMap());//获取盘口配置信息
            map.put("userId", userId);
            BigDecimal buyFeeRate = BigDecimal.ZERO;
            BigDecimal sellFeeRate = BigDecimal.ZERO;
            BigDecimal feeDiscount = (BigDecimal) Cache.GetObj("user_vip_fee_discount_" + userId);
            if (feeDiscount == null) {
                UserVipLevelDao userVipLevelDao = new UserVipLevelDao();
                VipRate oldVip = (VipRate) EnumUtils.getEnumByKey(user.getVipRate(), VipRate.class);
                if (oldVip == null) {
                    oldVip = VipRate.vip0;
                }
                feeDiscount = userVipLevelDao.getDiscountByVipRate(oldVip.getId());
            }
            if (marketMaps != null && !marketMaps.isEmpty()) {
                for (Map.Entry<String, com.alibaba.fastjson.JSONObject> entry : marketMaps.entrySet()) {
                    List<Object> feeRateList = new ArrayList<Object>();
                    com.alibaba.fastjson.JSONObject market = entry.getValue();
                    com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                    buyFeeRate = new BigDecimal(market.getDouble("takerFeeRate")).multiply(feeDiscount);
                    sellFeeRate = new BigDecimal(market.getDouble("makerFeeRate")).multiply(feeDiscount);
                    jsonObject.put("buyFeeRate", buyFeeRate.multiply(new BigDecimal(100)).setScale(3, BigDecimal.ROUND_DOWN).toPlainString());
                    jsonObject.put("sellFeeRate", sellFeeRate.multiply(new BigDecimal(100)).setScale(3, BigDecimal.ROUND_DOWN).toPlainString());
                    feeRateList.add(jsonObject);
                    map.put(market.getString("market"), feeRateList);
                }
            }
        } catch (Exception e) {
            log.error("当前用户" + String.valueOf(userId) + "获取手续费信息异常，异常信息为：", e);
            json(SystemCode.code_1002, L("获取手续费失败"));
        }
        json(SystemCode.code_1000,"成功",net.sf.json.JSONObject.fromObject(map).toString());
    }

    /**
     * 校验锁定key
     * @param uid
     * @param functionName
     * @param lockType
     * @param lockTime
     * @return
     */
    public DataResponse checkVerifiCode1(String uid, String functionName, LockType lockType, String lockTime) {
        DataResponse dr = new DataResponse("", false, "{\"id\" : \"vercode\"}");
        String func = lockType.getValue();
        String lockKey = CacheKeys.getFunctionLockKey(uid,func);
        int value = Cache.GetObj(lockKey) != null ? Integer.valueOf(Cache.GetObj(lockKey).toString()) : 0;
        if(value != 1){
            dr.setSuc(true);
        }else{
            String returnVal = StrUtil.format(MsgToastKey.TIME_OVER_LIMIT_MSG,functionName,lockTime);
            dr.setDes(Lan.LanguageFormat(lan , returnVal , ""));

        }
        return dr;
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
//		errorMessage = errorMessage.replace("谷歌验证码输入有误","验证码输入有误");
//		errorMessage = errorMessage.replace("短信验证码输入有误","验证码输入有误");
//		errorMessage = errorMessage.replace("邮箱验证码输入有误","验证码输入有误");

        //格式化次数
        String[] errorMsg = errorMessage.split("#");
        dr.setDes(Lan.LanguageFormat(lan, errorMsg[0], errorMsg[1]));
        return dr;
    }
    public SessionUser toLogin(Pages p , String userId, String userName, String ip,JSONObject json) {
        try {
            SsoSessionManager.initSession(p);
//            String token = MD5.toMD5(userId + UUID.randomUUID().toString());
//            String loginCacheKey = appLoginCache + userId;
            SessionUser su = new SessionUser();
            //用户id
            su.uid = userId;
            //用户名
            su.uname = userName;
            //登录时间
            su.ltime = System.currentTimeMillis();
            //登录ip
            su.lip = ip;
            //最后活动时间
            su.lastTime = su.ltime;
            su.others = json;
            p.session.addAppUser(su , 0 , p,false);
            log.info("用户缓存数据："+su.others.toJSONString());
            return su;
        } catch(Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
        return null;
    }

    /**
     * 缓存
     * @param user
     * @return
     */
    public JSONObject buildUserSession(User user) {
        JSONObject json = new JSONObject();
        json.put("uid",user.getId());
        json.put("mobile",StringUtils.isNotEmpty(user.getUserContact().getSafeMobile()) ? user.getUserContact().getSafeMobile() : "");
        json.put("nickname",StringUtils.isNotEmpty(user.getNickname()) ? user.getNickname() : "");
        json.put("email",user.getUserContact().getSafeEmail());
        json.put("safePwd",user.getSafePwd());
        json.put("googleOpen",user.getGoogleOpen());
        json.put("smsOpen",user.getSmsOpen());
        json.put("secret",StringUtils.isNotEmpty(user.getUserContact().getSecret()) ? user.getUserContact().getSecret() : "");
        json.put("hasSafePwd",user.getHasSafePwd());
//        String token = this.token(user.getId());

//        UserSession userSession = new UserSession();
//        userSession.buildUser(user);
//        userSession.setSessionId(token);

        Authentication au = new AuthenticationDao().getByUserId(user.getId());
        json.put("cardName",au==null ? "" : (au.getStatus()==AuditStatus.a1Pass.getKey() ? au.getRealName() : ""));
        json.put("cardStatu",au==null ? AuditStatus.a1NoSubmit.getKey() : au.getStatus());
        json.put("reason",au==null ? "" : user.getCardReson());
//        userSession.setCardName(au==null ? "" : (au.getStatus()==AuditStatus.a1Pass.getKey() ? au.getRealName() : ""));
//        userSession.setCardStatus(au==null ? AuditStatus.a1NoSubmit.getKey() : au.getStatus());
//        userSession.setReason(au==null ? "" : user.getCardReson());
//		userSession.setUnReadMsgNum(msgService.getUnReadMsgNum(user.getId(), Constant.MsgChannel.APP));

        // 先删除旧的session信息，在插入新的，不用等到过期
//        Cache.Delete(CacheKeys.getUserSessionKey(user.getId(), "*"));
//        Cache.SetObj(CacheKeys.getUserSessionKey(user.getId(), token), userSession, appDefaultTime);
        return json;
    }


    //******************************app新接口*********************************************




    private Map<String, Object> getlogin2Info(boolean needGACode, boolean needDynamicCode) {
        Map<String, Object> login2 = new HashMap<String, Object>();
        login2.put("needGoogleCode", needGACode==true?1:0);
        login2.put("needDynamicCode", needDynamicCode==true?1:0);
        return login2;
    }
    private void emptyValues(Map<String, Object> inner, List<String> exceptions) {
        // TODO Auto-generated method stub
        for (String key : inner.keySet()) {
            if(exceptions.contains(key))
                continue;
            inner.put(key,"");
        }
    }

    public void checkCountryCode(String uid) {
        setLan();
        User u = userDao.findOne("_id",uid);
        if (null == u) {
            return;
        }
        UserContact uc = u.getUserContact();
        if (null == uc) {
            return;
        }
        if (uc.getMobileStatu() == 2 && StringUtils.isBlank(uc.getmCode()) && !uc.getSafeMobile().startsWith("+")
                && CheckRegex.isPhoneNumber("+86 " + uc.getSafeMobile())) {

            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", uid);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("userContact.mCode", "+86");
            ops.set("userContact.safeMobile", "+86 " + uc.getSafeMobile());

            userDao.update(q, ops);
        }
    }

    /**
     * 兼容APP老接口
     */
    @Deprecated
    @Page(Viewer = JSON)
    public void checkCode() {
        setLan();
        try {
            String countryCode = param("countryCode");
            if (null == countryCode || "".equals(countryCode) || !countryCode.startsWith("+")) {
                countryCode = "+86";
            }
            String mobileNumber = "", email = "";
            String encryptNumber = param("encryptNumber").trim();
            String encryptEmail = param("encryptEmail").trim();
            String dynamicCode = param("dynamicCode").trim();
            int icodeType = intParam("type");
            if(icodeType <= 0) {
                json(SystemCode.code_1001,L("参数错误"));
                return;
            }


            PostCodeType postCodeType = (PostCodeType) EnumUtils.getEnumByKey(icodeType, PostCodeType.class);
            String codeType = postCodeType.getValue();

            ClientSession clientSession = null;
            boolean isEmail = false;
            if (StringUtils.isBlank(encryptNumber)) {
                email = StringUtils.isNotBlank(encryptEmail)? new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(encryptEmail), priKey)).toLowerCase():"";
                if (StringUtils.isNotBlank(email) && !CheckRegex.isEmail(email)) {
                    json(SystemCode.code_1001, L("邮箱格式不正确，请重新填写"));
                    return;
                }
                isEmail = true;
                clientSession = new ClientSession(ip(), email, lan, codeType, false);
            } else {
                mobileNumber = StringUtils.isNotBlank(encryptNumber)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(encryptNumber), priKey)):"";
                if(StringUtils.isNotBlank(mobileNumber) && !CheckRegex.isPhoneNumber(countryCode + " " + mobileNumber)){
                    json(SystemCode.code_3005, L("手机号码格式错误"));
                    return;
                }
                mobileNumber = countryCode + " " +  mobileNumber;
                clientSession = new ClientSession(ip(), mobileNumber, lan, codeType, false);
            }
            DataResponse dr = clientSession.checkCode(dynamicCode,false);

            if(!dr.isSuc()){
                json(SystemCode.code_1024, dr.getDes());
                return;
            }
            json(SystemCode.code_1000, L("操作成功"));
        }catch(Exception e){
            log.error("校验验证码异常：" +e.toString());
            json(SystemCode.code_1001,L("内部错误"));
        }
    }


    @Deprecated
    @Page(Viewer = JSON)
    public void sendCodeVersion(){
        setLan();
        json(SystemCode.code_1001,L("当前版本过低，请及时更新。"));
        return;
    }

    /**
     * 发送邮箱验证码
     */
    @Page(Viewer = JSON)
    public void sendEmailCodeForApp(){
        setLan();
        try {
            String email = param("email");
            //是否有图形验证码
            boolean graphicalCode = false;
            if (StringUtils.isBlank(email)) {
                json(SystemCode.code_1001, L("请输入邮箱"));
                return;
            }

            if (StringUtils.isNotBlank(email) && !CheckRegex.isEmail(email)) {
                json(SystemCode.code_1001, L("请输入正确的邮箱地址"));
                return;
            }

            int icodeType = intParam("type");
            PostCodeType postCodeType = (PostCodeType) EnumUtils.getEnumByKey(icodeType, PostCodeType.class);
            String codeType = postCodeType.getValue();

            if(isForbid()){
                return;
            }

            if (session == null) {
                SsoSessionManager.initSession(this);
            }

            if(sessionId == null){
                json(SystemCode.code_1001, L("系统出错了，请稍后重试"));
                return;
            }

            String ip = ip();
            String receiveAddr = email;
            ClientSession clientSession = new ClientSession(ip, receiveAddr, lan, codeType, graphicalCode);
            clientSession.rs = 1;
            //检测当前客户端是否能够发送
            DataResponse dr = clientSession.checkSend();

            if(!dr.isSuc()){
                json(SystemCode.code_1001, dr.getDes());
                return;
            }//测试，暂时不验证这步

            //当前ip验证是否注册过的所有手机号码24h不得超过x个
            clientSession.addCheckNumber();

            Map<String, Object> extraValidations = new HashMap<String, Object>();
            if (icodeType == 1 || icodeType==71) {
                User registeredUser = userDao.getUserByColumn(email, "userContact.safeEmail");
                if (null != registeredUser) {
                    json(SystemCode.code_3005, L("请输入正确的邮箱地址"));
                    return;
                }
            }
            // 发送类型 1：手机  2：邮件
            String dynamicCode = MobileDao.GetRadomStr(2);
            EmailDao eDao = new EmailDao();
            User user = userDao.getByField("userContact.safeEmail", email);
            if (null == user) {
                user = new User();
                user.setUserName(email);
                user.set_Id("");
            }
            //20170823 xzhang  将用户语言根据本次请求临时设置为当前环境语境，不会更新缓存和数据库。   BITI-572
            user.setLanguage(lan);
            String info = eDao.getCodeEmailHtmlByInfo(user, postCodeType.getEmailInfo(),dynamicCode, this);
            SysGroups sg = SysGroups.vip;
            String title = L(SysGroups.vip.getValue()) + " " + L("邮箱验证码");
            int iResult = eDao.sendEmail(ip, user.getId(), email, title, info, email);
            if (iResult == 1) {
                if(clientSession.sendCode(dynamicCode)){
                    log.info("APP邮件验证码：" + dynamicCode);
                    Map<String, Object> retData = new HashMap<String, Object>();
                    retData.put("isEmailCode", "1");
                    retData.put("userId", user.get_Id());
                    retData.put("login2", extraValidations);
                    json(SystemCode.code_1000, L("验证码已发送到您的邮箱%%，请登录邮箱查看，10分钟内有效。", userDao.shortEmail(email)), retData);
                    return;
                } else {
                    json(SystemCode.code_1001, L("发送失败，请稍后重试"));
                    return;
                }
            } else if (iResult == 2) {
                json(SystemCode.code_1001, L("您今天发送的验证码已超过限制。"));
                return;
            } else {
                json(SystemCode.code_1001, L("发送失败，请稍后重试"));
                return;
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    @Page(Viewer = JSON)
    public void sendMobileCodeForApp(){
        setLan();
        try {
            boolean graphicalCode = true;
            int icodeType = intParam("type");
            String userId = param("userId");


            User user = userDao.findOne("_id",userId);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            PostCodeType postCodeType = (PostCodeType) EnumUtils.getEnumByKey(icodeType, PostCodeType.class);
            String codeType = postCodeType.getValue();

            if(isForbid()){
                return;
            }

            String currency = param("currency");
            if(currency.length() > 0){
                currency = currency.toUpperCase();
            }

            String sendAddr = user.getUserContact().getSafeMobile();

            String ip = ip();

            ClientSession clientSession = new ClientSession(ip, sendAddr, lan, codeType, graphicalCode);
            clientSession.rs = resoureRequest;

            //检测当前客户端是否能够发送
            DataResponse dr = clientSession.checkSend();

            if(!dr.isSuc()){
                json(SystemCode.code_1001, dr.getDes());
                return;
            }

            //当前ip验证是否注册过的所有手机号码24h不得超过x个
            clientSession.addCheckNumber();
            String radomCode = MobileDao.GetRadomStr(1);
            MobileDao mDao = new MobileDao();
			/*start by xzhang 20171031 短信服务临时解决方法，除+86外全发英文*/
            String title = L(codeType, currency);
            String content = String.format(L(postCodeType.getDes(), currency), radomCode);
            //去掉该逻辑，所有都按照用户选择语言发送 modify by buxianguan 20190805
//			if(!MsgUtil.isContain(sendAddr)){
//				title = Lan.LanguageFormat("en", codeType, currency);
//				content = String.format(Lan.LanguageFormat("en", postCodeType.getDes(), currency), radomCode);
//			}
			/*end*/
            if(mDao.sendSms(user, ip, title, content, sendAddr)){
                if(clientSession.sendCode(radomCode)){
                    log.info("APP短信验证码：" + content);
                    json(SystemCode.code_1000, L("短信验证码已发送到您的手机，10分钟内有效"));
                    return;
                }
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    @Page(Viewer = JSON)
    public void doUserSendCode(){
        setLan();
        try {
            boolean graphicalCode = true;
            //验证码类型
            int icodeType = intParam("codeType");
            String token = param("token");
            //1：邮件， 为空则短信
            String sendType = param("type");
            //国家码
//			String mCode = param("selectedCode");
            String userId = "";
            String sendAddr = "";
            User user = null;
            if (icodeType == 65) {
                if (!newLocationTokenCheck(userId, token)) {
                    json(SystemCode.code_1003);
                    return;
                }
            }

            if(StringUtils.isNotEmpty(token)){
                userId = userIdStr();
                if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                    json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                    return;
                }
                user = userDao.findOne("_id",userId);
                if (null == user) {
                    json(SystemCode.code_3004);
                    return;
                }
                if("1".equals(sendType)){
                    sendAddr = user.getUserContact().getSafeEmail();
                }else{
                    if(icodeType == PostCodeType.appUpdMobile.getKey() || icodeType == PostCodeType.appSetMobile.getKey()){
                        //录入手机和绑定手机和没绑定手机开启二次验证要取前端传递的参数
                        User userMobile = null;
                        sendAddr = param("mobile");
                        if(StringUtils.isNotEmpty(sendAddr)){
                            userMobile = userDao.getUserInfo(sendAddr,user.getId());
                        }
                        if(userMobile != null){
                            json(SystemCode.code_1001,L("请输入正确的手机号码。"));
                            return;
                        }
                    }else if(icodeType == PostCodeType.appOpenMobileVerify.getKey()){
                        sendAddr = param("mobile");
                        if(StringUtils.isEmpty(sendAddr)){
                            sendAddr = user.getUserContact().getSafeMobile();
                        }else{
                            User userMobile = null;
                            if(StringUtils.isNotEmpty(sendAddr)){
                                userMobile = userDao.getUserInfo(sendAddr,user.getId());
                            }
                            if(userMobile != null){
                                json(SystemCode.code_1001,L("请输入正确的手机号码。"));
                                return;
                            }
                        }
                    }else{
                        sendAddr = user.getUserContact().getSafeMobile();
                    }
                }
            }else{
                //国家码
//				String mCode = param("selectedCode");
                //手机号
                String mobile = param("mobile");
                //邮箱
                String email = param("email");
                if(!"1".equals(sendType)){
//					sendAddr = mCode + " " + mobile;
                    sendAddr = mobile;

                    user = userDao.findOne("userContact.safeMobile", sendAddr);
                    if (null == user) {
                        user = new User();
                        user.setUserName(email);
                        user.set_Id("");
                    }
                }else{
                    sendAddr = email;
                    user = userDao.findOne("email", sendAddr);
                    if (null == user) {
                        //注册时判断该邮箱是否被注册
                        if(icodeType == PostCodeType.appRegVIP.getKey()){
                            user = new User();
                            user.setUserName(email);
                            user.set_Id("");
                        }else{
                            json(SystemCode.code_1001,L("请输入正确的邮箱"));
                            return;
                        }
                    }else{
                        //注册时判断该邮箱是否被注册
                        if(icodeType == PostCodeType.appRegVIP.getKey()){
                            json(SystemCode.code_1001,L("请输入正确的邮箱"));
                            return;
                        }
                    }
                }
            }

            PostCodeType postCodeType = (PostCodeType) EnumUtils.getEnumByKey(icodeType, PostCodeType.class);
            String codeType = postCodeType.getValue();

            if(isForbid()){
                return;
            }

            String currency = param("currency");
            if(currency.length() > 0){
                currency = currency.toUpperCase();
            }

            String ip = ip();

            ClientSession clientSession = new ClientSession(ip, sendAddr, lan, codeType, graphicalCode);
            clientSession.rs = resoureRequest;
            //检测当前客户端是否能够发送
            DataResponse dr = clientSession.checkSend();

            if(!dr.isSuc()){
                json(SystemCode.code_1001, dr.getDes());
                return;
            }//测试，暂时不验证这步

            //当前ip验证是否注册过的所有手机号码24h不得超过x个
            clientSession.addCheckNumber();
            String radomCode = "";
            if (!"1".equals(sendType)) {
                if(sendAddr.contains("+86")){
                    if (!CheckRegex.isPhoneNumber(sendAddr)) {
                        json(SystemCode.code_1001, L("请输入正确的手机号码。"));
                        return;
                    }
                }
                radomCode = MobileDao.GetRadomStr(1);
                MobileDao mDao = new MobileDao();
				/*start by xzhang 20171031 短信服务临时解决方法，除+86外全发英文*/
                String title = L(codeType, currency);
                String content = String.format(L(postCodeType.getDes(), currency), radomCode);
//				if(!MsgUtil.isContain(sendAddr)){
//					title = Lan.LanguageFormat("en", codeType, currency);
//					content = String.format(Lan.LanguageFormat("en", postCodeType.getDes(), currency), radomCode);
//				}
				/*end*/
                if(mDao.sendSms(user, ip, title, content, sendAddr)){
                    if(clientSession.sendCode(radomCode)){
                        log.info("APP短信验证码：" + content);
                        json(SystemCode.code_1000, L("短信验证码已发送到您的手机，10分钟内有效"));
                        return;
                    }
                }
            } else {
                radomCode = MobileDao.GetRadomStr(0);
                EmailDao eDao = new EmailDao();
                String content = L(postCodeType.getEmailInfo(), currency);
                String info = eDao.getCodeEmailHtmlByInfo(user, content, radomCode, this);
                String title = L(postCodeType.getEmailTitle(), currency);
                int iResult = eDao.sendEmail(ip, user.getId(), user.getUserName(), title, info, sendAddr);
                if (iResult == 1) {
                    if(clientSession.sendCode(radomCode)){
                        log.info(radomCode);
//						Map<String, Object> retData = new HashMap<String, Object>();
//						retData.put("isEmailCode", "1");
                        json(SystemCode.code_1000, L("验证码已发送到您的邮箱%%，请登录邮箱查看，10分钟内有效。"));
                        return;
                    } else {
                        json(SystemCode.code_1001, L("发送失败，请稍后重试"));
                        return;
                    }
                } else if (iResult == 2) {
                    json(SystemCode.code_1001, L("您今天发送的验证码已超过限制。"));
                    return;
                } else {
                    json(SystemCode.code_1001, L("发送失败，请稍后重试"));
                    return;
                }
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }


    /**
     * 手机短信修改密码
     */
    @Page(Viewer = JSON)
    public void changePwd() {
        setLan();
        try {
            int method = intParam("method");
            String countryCode = param("countryCode");
            String mobileNumber = param("mobileNumber");
            String dynamicCode = param("dynamicCode");
            dynamicCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode), priKey));
            String newPassword = param("newPassword");
            String email = param("email");
            String googleCode = param("googleCode");

            googleCode = StringUtils.isNotBlank(googleCode)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(googleCode), priKey)):"";
            newPassword = StringUtils.isNotBlank(newPassword)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(newPassword), priKey)):"";

            User user = null;
            ClientSession clientSession = null;
            Query<User> q = userDao.getQuery(User.class);
            if (method == 1) {
                if (null == countryCode || "".equals(countryCode) || !countryCode.startsWith("+")) {
                    countryCode = "+86";
                }
                if (!mobileNumber.startsWith("+")) {
                    mobileNumber = countryCode + " " + mobileNumber;
                }
                // 检查短信验证码
                clientSession = new ClientSession(ip(), mobileNumber, lan, PostCodeType.resetPassword.getValue(), false);
                q.filter("userContact.safeMobile", mobileNumber);
            } else {
                // 检查邮件验证码
                clientSession = new ClientSession(ip(), email, lan, PostCodeType.resetPassword.getValue(), false);
                q.filter("userContact.safeEmail", email);
            }
            DataResponse dr = clientSession.checkCode(dynamicCode);
            if(!dr.isSuc()){
                json(SystemCode.code_1024, dr.getDes());
                return;
            }
            user = userDao.findOne(q);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            } else {
//				boolean needGACode = false;
                if(user.isLoginGoogleAuth()) {//该用户开启了谷歌登录验证
//					needGACode = true;
                    long gCode = CommonUtil.stringToLong(googleCode);
                    if (method==1 && !isGoogleCodeCorrect(user.getUserContact().getSecret(), gCode, user.get_Id())) {
                        return;
                    }
                }
            }

            int safeLevel = 0;
            if (newPassword.length() > 8)
                safeLevel = 85;
            else
                safeLevel = 50;

            //modify by buxianguan 20180111 资金密码不能和登录密码一致
            if(user.getEncryptedPwd(newPassword).equals(user.getSafePwd())){
                json(SystemCode.code_1001, L("账户登录密码应避免与资金密码一致。"));
                return;
            }

            UpdateResults<User> ur = userDao.updatePwd(user.get_Id(), newPassword, safeLevel);
            if (ur.getHadError()) {
                json(SystemCode.code_1001, L("修改密码出错，请稍后重试。"));
                return;
            }
//			toLogin(user.getId(), user.getUserName(), ip());

            Map<String, Object> map = new HashMap<String, Object>();
            Map<String, Object> userMap = getUserInfo(user);
            map.put("userInfo", userMap);
            map.put("token", token(user.getId()));
            json(SystemCode.code_1000, map);
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    @Page(Viewer = JSON)
    public void getTradeData() {
        setLan();
        String symbol = param("symbol");

        if (StringUtils.isBlank(symbol))
            symbol = "btc";

        symbol = symbol.toLowerCase();

        try {

            String key = "BTC123_TRADE_DATA_";

            String data = Cache.Get(key + symbol);

            if (StringUtils.isBlank(data)) {

                String url = "https://www.btc123.com/api/btcTrade";
                if ("ltc".equalsIgnoreCase(symbol))
                    url = "https://www.btc123.com/api/ltcTrade";
                String callback = HttpUtil.doGet(url, null, 1500, 2000);
                if (StringUtils.isNotBlank(callback) && callback.startsWith("{")) {
                    com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(callback);
                    String datas = json.getJSONArray("datas").toJSONString();
                    if (StringUtils.isBlank(datas)) {
                        datas = "[]";
                    } else {
                        // 缓存30秒
                        Cache.Set(key + symbol, datas, 30);
                    }
                    data = datas;
                }
            }

            if (StringUtils.isBlank(data))
                data = "[]";

            Map<String, Object> reData = new HashMap<String, Object>();
            reData.put("marketChartData", reData);
            json(SystemCode.code_1000, reData);
        } catch (IOException e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    /**
     * http://www.vip.com/api/m/getAccountData
     */
    @Page(Viewer = JSON)
    public void getAccountData() {
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.findOne("_id",userId);
            if(user.isFreez()){
                json(SystemCode.code_1001, L("该账户已冻结，暂时不能操作。"));
                return;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            Map<String, Object> userMap = getUserInfo(user);
            map.put("token", token);

            map.put("userInfo", userMap);

            Map<String, JSONArray> accountMap = getUserAssetInfo(user);

            map.put("userAccount", accountMap);

            json(SystemCode.code_1000, map);
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    @Page(Viewer = JSON)
    public void getUserInfo() {
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.findOne("_id",userId);
            if(user.isFreez()){
                json(SystemCode.code_1001, L("该账户已冻结，暂时不能操作。"));
                return;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            Map<String, Object> userMap = getUserInfo(user);
            map.put("token", token);

            map.put("userInfo", userMap);

            json(SystemCode.code_1000, map);
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }


    public Map<String, JSONArray> getUserAssetInfo(User user) {

        String userIdStr = user.get_Id();

        Map<String, JSONArray> accountMap = new HashMap<>();

        JSONArray funds = UserCache.getUserFunds(userIdStr);//getBalanceArray();

        //buxianguan 20171110 添加币种能否充值提现，不能在这里的缓存里加，trans也会更新这个缓存
        Map<Integer, CoinProps> coinFundsTypeMap = DatabasesUtil.getCoinPropFundsTypeMaps();
        for (int i = 0; i < funds.size(); i++) {
            JSONObject obj = funds.getJSONObject(i);
            Integer fundsType = obj.getInteger("fundsType");
            if (null != fundsType) {
                CoinProps coinProps = coinFundsTypeMap.get(fundsType);
                if (null != coinProps) {
                    obj.put("canCharge", coinProps.isCanCharge());//是否支持充值
                    obj.put("canWithdraw", coinProps.isCanWithdraw());//是否支持提现
                }
            }
        }

//		for(int i=0;i<funds.size();i++){
//			JSONObject json = funds.getJSONObject(i);
//			String tag = json.getString("propTag");
//			if(tag!=null ){
//				String imgTag = tag.toLowerCase();
//				/*if(tag.equals("ETH")){
//					json.put("coinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_eth_type_sm@3x.png");
//					json.put("financeCoinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_eth_finance.png");
//				}else if(tag.equals("ETC")){
//					json.put("coinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_etc_type_sm@3x.png");
//					json.put("financeCoinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_etc_finance.png");
//				}else{//其他图标还没提供
//					json.put("coinUrl", "");
//					json.put("financeCoinUrl", "");
//				}*/
////				json.put("coinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_"+imgTag+"_type_sm@3x.png");
////				json.put("financeCoinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_"+imgTag+"_finance.png");
//
//			}
//		}

        accountMap.put("balances", funds);
        return accountMap;
    }



    private Map<String, Object> getUserInfo(User user) {
        userDao.syncLoginAuthen(user);
        userDao.syncWitchdrawAuthen(user);
        Map<String, Object> userMap = new HashMap<String, Object>();

        String userId = user.get_Id();

        userMap.put("userId", userId);
        userMap.put("userName", user.getUserName());


        String mobile = user.getUserContact().getSafeMobile();
        if(StringUtils.isNotBlank(mobile) && mobile.startsWith("+")){
            String[] arr = mobile.split(" ");
            if(arr.length>1){
                mobile = arr[1];//增加过滤手机号前面的国家区号
            }
        }
        userMap.put("mobileNumber", mobile);
        userMap.put("email", StringUtils.isBlank(user.getUserContact().getSafeEmail())?"":user.getUserContact().getSafeEmail());

//		isHadSecurePassword String 是 是否设置有资金密码：0：否，1：是
//		isCloseSecurePassword String 是 是否交易时关闭资金密码：0：否，1：是
        int isHadSecurePassword = user.getHasSafePwd()?1:0;
        userMap.put("isHadSecurePassword", isHadSecurePassword);
        boolean isNeedSafePwd = userDao.isNeedSafePwd(user);
        int safePwdPeriod = 0;
        if (isNeedSafePwd) {
            safePwdPeriod = 1;
        }
        userMap.put("safePwdPeriod", safePwdPeriod);

        //			| juaUserId | String| 是    | | 为空时表示未绑定JUA|
        //			| bwUserId | String| 是    | | 为空时表示未绑定bw|
        //			| realName | String| 是    | | 为空时表示未认证|
        //			| googleAuth | Integer| 是    | 1| 1已通过谷歌认证0未通过谷歌认证|
        //谷歌双重验证是否开启(0未验证     1验证未开启     2已开启)
        int googleAuth = user.getUserContact().getGoogleAu();
        userMap.put("googleAuth", googleAuth==AuditStatus.pass.getKey()?1:0);

        Authentication authentication = auDao.getByUserId(userId);
        if(null == authentication){
            userMap.put("identityAuthStatus", AuditStatus.a1NoSubmit.getKey());
            userMap.put("realName", "");
        } else {
            userMap.put("identityAuthStatus", authentication.getStatus());
            userMap.put("realName", StringUtils.isBlank(authentication.getRealName())?"":authentication.getRealName());
        }

        userMap.put("loginSmsCheck", user.isDiffAreaLoginNoCheck()?0:1);
        userMap.put("loginGoogleAuth", user.isLoginGoogleAuth()?1:0);
        userMap.put("payGoogleAuth", user.isPayGoogleAuth()?1:0);
        userMap.put("paySmsAuth", user.isPayMobileAuth()?1:0);
        userMap.put("userOpenId", userOpenId(user));
        userMap.put("countryCode",user.getUserContact().getmCode());
        userMap.put("loginAuthenType", user.getLoginAuthenType());
        userMap.put("tradeAuthenType", user.getTradeAuthenType());
        userMap.put("withdrawAuthenType", user.getWithdrawAuthenType());
        userMap.put("isSmsOpen", user.getSmsOpen());
        userMap.put("isGoogleOpen", user.getGoogleOpen());
        userMap.put("mobile", user.getUserContact().getSafeMobile());
        CommonUtil.nullToEmpty(userMap);
        return userMap;
    }

    /**
     * 修改密码
     */
    @Page(Viewer = JSON)
    public void resetPwd() {
        setLan();
        int type = intParam("type");//1 login password 2 transaction password

        if (type == 1) {
            resetLoginPwd();
        } else if (type == 2) {
            resetSafePwd();
        } else {
            json(SystemCode.code_1019);
        }
    }
    /**
     * http://www.vip.com/api/m/resetPwd
     */
    @Page(Viewer = JSON)
    public void resetLoginPwd() {
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");
            String oldPassword = param("oldPassword");
            String newPassword = param("newPassword");
            String googleCode = param("googleCode");
            String dynamicCode = param("dynamicCode");

            //【第二步】解密RSA参数
            oldPassword = StringUtils.isNotBlank(oldPassword)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(oldPassword), priKey)):"";
            newPassword = StringUtils.isNotBlank(newPassword)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(newPassword), priKey)):"";
            googleCode = StringUtils.isNotBlank(googleCode)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(googleCode), priKey)):"";
            dynamicCode = StringUtils.isNotBlank(dynamicCode)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode), priKey)):"";

            int needDynamicCode = 0;
            int needGoogleCode = 0;
            Map<String, Object> retData = new HashMap<>();
            retData.put("needDynamicCode", needDynamicCode);
            retData.put("needGoogleCode", needGoogleCode);

            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.getUserById(userId);
            if(user.isFreez()){
                json(SystemCode.code_1001, L("该账户已冻结，暂时不能操作。"), retData);
                return;
            }

            boolean isOldPwdExisted = true;
            if(user.getPwdLevel() == 0 || StringUtils.isBlank(user.getPwd()) ){
                isOldPwdExisted = false;
            }
            //有密码 但 客户端没有传新旧两个密码
            if (isOldPwdExisted && (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) ) {
                json(SystemCode.code_1001, L("新旧密码都要输入噢!"), retData);
                return;
            }

            LimitType lt = LimitType.SafePassError;

            if(isOldPwdExisted){
                String checkPwd = user.getEncryptedPwd(oldPassword);
                if(lt.GetStatus(userId) == -1){
                    json(SystemCode.code_1001, L("密码已锁定"), retData);
                    return;
                }

                if(!checkPwd.equals(user.getPwd())){
                    lt.UpdateStatus(userId);
//					json(SystemCode.code_1001, L("原始密码不正确"), retData);
                    //2017.8.14 xzhang 重置登录密码错误提示
                    json(SystemCode.code_1001, L("旧密码输入有误"), retData);
                    return;
                }

                if(checkPwd.equals(user.getEncryptedPwd(newPassword))){
                    json(SystemCode.code_1001, L("修改后的密码不能和原密码一致。"), retData);
                    return;
                }

            }

            //modify by xwz 20171220 资金密码不能和登录密码一致
            if(user.getEncryptedPwd(newPassword).equals(user.getSafePwd())){
                json(SystemCode.code_1001, L("账户登录密码应避免与资金密码一致。"), retData);
                return;
            }

            UserContact userContact = user.getUserContact();

            boolean isGoogleRequired = false;			//需要google验证
            boolean isDynamicCodeRequired = false;		//需要短信验证
            boolean isGACodePassed = true;		        //是否通过google认证
            boolean isDynamicCodePassed = true;		    //是否通过短信认证

            //【第八步】判断Google验证码是否正确
            if(StringUtils.isBlank(googleCode)) {
                //是否开启Google登录验证
                if(userContact.getGoogleAu() == AuditStatus.pass.getKey()) {
                    isGACodePassed = false;
                    isGoogleRequired = true;
                } else {
                    isGACodePassed = true;
                }
            } else {
                //判断google验证码是否正确
                long gCode = CommonUtil.stringToLong(googleCode);
                if (isGoogleCodeCorrect(userContact.getSecret(), gCode, user.get_Id())) {
                    isGACodePassed = true;
                } else {
                    isGACodePassed = false;
                }
            }

            //【第八步】判断dynamicCode是否正确
            DataResponse dr = null;
            if(StringUtils.isBlank(dynamicCode)) {
                //硬性要求，要这个dynamicCode
                isDynamicCodePassed = false;
                isDynamicCodeRequired = true;
            } else {
                //判断DynamicCode是否正确
                String codeRecvAddr = userContact.getSafeMobile();//找一找有没有手机号码
                if (StringUtils.isBlank(codeRecvAddr))
                    codeRecvAddr = user.getUserContact().getSafeEmail();//没找到手机号码那就只有邮箱了，用来验证

                ClientSession clientSession = new ClientSession(ip(), codeRecvAddr, lan, PostCodeType.safeAuth.getValue(), false);
                dr = clientSession.checkCode(dynamicCode);
                if(dr.isSuc()){	//正确
                    isDynamicCodePassed = true;
                }else {//验证码不正确
                    isDynamicCodePassed = false;
                }
            }

            //【第九步】是否需要进入二次验证
            if(isGoogleRequired && isDynamicCodeRequired) {
                //1020
                needDynamicCode = 1;
                needGoogleCode = 1;
                json(SystemCode.code_1020, retData);
                return;
            } else {
                if(isGoogleRequired) {
                    needGoogleCode = 1;
                    json(SystemCode.code_1018, retData);
                    return;
                } else if(isDynamicCodeRequired) {
                    needDynamicCode = 1;
                    json(SystemCode.code_1017, retData);
                    return;
                }
            }

            //【第十步】是否通过二次验证
            if (isDynamicCodePassed && isGACodePassed) {
                //开始改密
                int safeLevel = 0;
                if(newPassword.length()>8) safeLevel = 85; else safeLevel = 50;

                UpdateResults<User> ur = userDao.updatePwd(userId, newPassword, safeLevel);

                if (!ur.getHadError()) {
                    logDao.insertOneRecord(AuthenType.modifyPwd.getKey(), userId+"", "0", "成功修改登录密码。", ip());
                    json(SystemCode.code_1000);
                } else {
                    json(SystemCode.code_1001, retData);
                }

            } else {
                if(!isDynamicCodePassed) {
                    //短信不通过验证
                    json(SystemCode.code_1024, dr.getDes(), retData);
                    return;
                }

                if(!isGACodePassed) {
                    //Google不通过验证
                    json(SystemCode.code_1001, L("谷歌验证码错误!"), retData);
                    return;
                }
            }

        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }
    /**
     * http://www.vip.com/api/m/resetSafePwd
     */
    @Page(Viewer = JSON)
    public void resetSafePwd() {
        setLan();
        try {
            int icodeType = intParam("icodeType");
            String userId = param("userId");
            String token = param("token");
            String oldPassword = param("oldPassword");
            String newPassword = param("newPassword");
            String googleCode = param("googleCode");
            String dynamicCode = param("dynamicCode");

            //【第二步】解密RSA参数
            oldPassword = StringUtils.isNotBlank(oldPassword)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(oldPassword), priKey)):"";
            newPassword = StringUtils.isNotBlank(newPassword)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(newPassword), priKey)):"";
            googleCode = StringUtils.isNotBlank(googleCode)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(googleCode), priKey)):"";
            dynamicCode = StringUtils.isNotBlank(dynamicCode)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode), priKey)):"";

            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.getUserById(userId);
            if(user.isFreez()){
                json(SystemCode.code_1001, L("该账户已冻结，暂时不能操作。"));
                return;
            }

            boolean isSet = false;
            int status = userDao.transCheckSecurityPwd(oldPassword, userId);
            if(status==-2){
                json(SystemCode.code_1001, L("资金安全密码已经被锁定"));
                return;
            }else if(status==-1){
//				json(SystemCode.code_1001, L("您的原始资金安全密码不正确。"));
                //2017.8.14 xzhang 修改密码原始资金密码错误提示修改
                json(SystemCode.code_1001, L("旧密码输入有误"));
                return;
            }else if(status==0){
                isSet = true;
            }

            //String safePwdRegex = "^(?![0-9]+$)(?![a-zA-Z]+$)(?![*[#@!~%^&*]]+$)[0-9A-Za-z*[#@!~%^&*]]{8,20}$";

            String safePwdRegex = "^(?![0-9]+$)(?![a-zA-Z]+$)(?!([^(0-9a-zA-Z)]|[\\(\\)])+$)([^(0-9a-zA-Z)]|[\\(\\)]|[a-zA-Z]|[0-9]){8,20}$";
            if(!newPassword.matches(safePwdRegex)){
                json(SystemCode.code_1001, L("资金安全密码建议由8-20位字母、数字和特殊符号组成，不能是纯数字或字母!"));
                return;
            }

            if(user.getSafePwd().equals(user.getEncryptedPwd(newPassword))){
                json(SystemCode.code_1001, L("修改后的密码不能和原密码一致。"));
                return;
            }

            UserContact userContact = user.getUserContact();

            boolean isGoogleRequired = false;			//需要google验证
            boolean isDynamicCodeRequired = false;		//需要短信验证
            boolean isGACodePassed = true;		        //是否通过google认证
            boolean isDynamicCodePassed = true;		    //是否通过短信认证

            //【第八步】判断Google验证码是否正确
            if(StringUtils.isBlank(googleCode)) {
                //是否开启Google登录验证
                if(userContact.getGoogleAu() == AuditStatus.pass.getKey()) {
                    isGACodePassed = false;
                    isGoogleRequired = true;
                } else {
                    isGACodePassed = true;
                }
            } else {
                //判断google验证码是否正确
                long gCode = CommonUtil.stringToLong(googleCode);
                if (isGoogleCodeCorrect(userContact.getSecret(), gCode, user.get_Id())) {
                    isGACodePassed = true;
                } else {
                    isGACodePassed = false;
                }
            }

            //【第八步】判断dynamicCode是否正确
            DataResponse dr = null;
            if(StringUtils.isBlank(dynamicCode)) {
                //硬性要求，要这个dynamicCode
                isDynamicCodePassed = false;
                isDynamicCodeRequired = true;
            } else {
                //判断DynamicCode是否正确
                String codeRecvAddr = userContact.getSafeMobile();//找一找有没有手机号码
                if (StringUtils.isBlank(codeRecvAddr))
                    codeRecvAddr = user.getUserContact().getSafeEmail();//没找到手机号码那就只有邮箱了，用来验证
                String type=PostCodeType.safeAuth.getValue();
                if(icodeType == 18){
                    type = PostCodeType.setSafePassword.getValue();
                }else if(icodeType == 21){
                    type = PostCodeType.updateSafePassword.getValue();
                }
                ClientSession clientSession = new ClientSession(ip(), codeRecvAddr, lan, type, false);
                dr = clientSession.checkCode(dynamicCode);
                if(dr.isSuc()){	//正确
                    isDynamicCodePassed = true;
                }else {//验证码不正确
                    isDynamicCodePassed = false;
                }
            }

            //【第九步】是否需要进入二次验证
            if(isGoogleRequired && isDynamicCodeRequired) {
                //1020
                json(SystemCode.code_1020);
                return;
            } else {
                if(isGoogleRequired) {
                    json(SystemCode.code_1018);
                    return;
                } else if(isDynamicCodeRequired) {
                    json(SystemCode.code_1017);
                    return;
                }
            }

            //【第十步】是否通过二次验证
            if (isDynamicCodePassed && isGACodePassed) {
                //开始改密
                int safeLevel = 0;
                if(newPassword.length()>8) safeLevel = 85; else safeLevel = 50;
                //modify by xwz 20171220 资金密码不能和登录密码一致
                if(user.getEncryptedPwd(newPassword).equals(user.getPwd())){
                    json(SystemCode.code_1001, L("资金密码应避免与帐户登录密码一致。"));
                    return;
                }
                UpdateResults<User> ur = userDao.updateSecurityPwd(userId, newPassword, safeLevel);

                if (!ur.getHadError()) {
                    logDao.insertOneRecord(AuthenType.modifySecurityPwd.getKey(), userId+"", "0", "成功"+(isSet?"设置":"修改")+"资金安全密码。", ip());

                    try {
                        boolean isOldPwdExisted = user.getIsSafePwd();
                        if (!isOldPwdExisted) {
                        } else {
                            MobileDao mDao = new MobileDao();
                            PostCodeType postCodeType = PostCodeType.resetSafePwd;
                            if (null != user.getUserContact().getSafeMobile()) {
								/*start by xzhang 20171031 短信服务临时解决方法，除+86外全发英文*/
                                String title = L(postCodeType.getValue());
                                String content = L(postCodeType.getDes());
                                //去掉该逻辑，所有都按照用户选择语言发送 modify by buxianguan 20190805
//								if(!MsgUtil.isContain(user.getUserContact().getSafeMobile())){
//									title = Lan.Language("en", postCodeType.getValue());
//									content = Lan.Language("en", postCodeType.getDes());
//								}
								/*end*/
                                mDao.sendSms(user, ip(), title, content, user.getUserContact().getSafeMobile());
                            }
                        }
                    } catch (Exception e) {
                        log.error("内部异常", e);
                    }

                    json(SystemCode.code_1000);
                } else {
                    json(SystemCode.code_1001);
                }

            } else {
                if(!isDynamicCodePassed) {
                    //短信不通过验证
                    json(SystemCode.code_1024, dr.getDes());
                    return;
                }

                if(!isGACodePassed) {
                    //Google不通过验证
                    json(SystemCode.code_1001, L("谷歌验证码错误!"));
                    return;
                }
            }

        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    @Page(Viewer = JSON)
    public void updateAppVersion(){
        Map<String, Object> map = new HashMap<String, Object>();

        setLan();
        String client = param("client");
        App app = appDao.findLastVesion(client);
        if(null==app){
            json(SystemCode.code_1000, map);
            return;
        }

//		if("0".equals(Constants.modeKey)) { // 生产
//			map.put("url", "https://www.vip.com/mobile/download");
//		} else if("1".equals(Constants.modeKey)) { // 开发
//			map.put("url", "https://www.vip.com/mobile/download");
//		} else if ("2".equals(Constants.modeKey)) { // 测试
//			map.put("url", "https://www.vip.com/mobile/download");
//		}

        map.put("url", app.getUrl());
        map.put("version", app.getNum());
        map.put("released", app.getReleased());
        map.put("content", JSONObject.parseObject(app.getRemark()).get(lan));
        map.put("title", JSONObject.parseObject(app.getName()).get(lan));
        map.put("size", app.getSize());
        map.put("isEnforceUpdate", app.isEnforceUpdate());
        json(SystemCode.code_1000, map);
    }
    @Page(Viewer = JSON)
    public void getCardStatus(){
        setLan();
        String userId = userIdStr();
        String token = param("token");
        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        User user = userDao.getUserById(userId);
        Authentication auth = auDao.getByUserId(userId);
        int authStatus = AuditStatus.a1NoSubmit.getKey();
        boolean isLock = false;
        Timestamp lockTime = null;
        if (auth != null) {
            authStatus = auth.getStatus();
            int lockState = auth.getLockStatus();
            if (lockState == 1) {
                lockTime = auth.getLockTime();
                if(System.currentTimeMillis() - lockTime.getTime() <= 72 * 60 * 60 * 1000){
                    isLock = true;
                }
            }
        }
        Object value = Cache.GetObj(Const.black_key+userId);
        boolean isBlackUser = false;
        if(null != value){
            isBlackUser = true;
        }
        JSONObject json = new JSONObject();
        json.put("authStatus",authStatus);
        log.info("未通过原因："+user.getUserContact().getCardReson());
        json.put("cardReson",StringUtils.isNotBlank(user.getUserContact().getCardReson()) ? L(user.getUserContact().getCardReson()) : "");
        json.put("cardName",user.getUserContact().getCardName());
        json.put("countryCode",auth==null ? "" :auth.getCountryCode());
        json.put("isBlackUser",isBlackUser);
        json.put("isLock",isLock);
        json.put("lockTime",lockTime);
        json(SystemCode.code_1000,SystemCode.code_1000.getValue(), json.toString());
    }
    /**
     * 获取个人历史提现地址
     * http://www.vip.com/api/m/getWithdrawAddress
     *
     */
    @Page(Viewer = JSON)
    public void getWithdrawAddress(){
        setLan();
        String userId = param("userId");
        String token = param("token");
        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        String currencyType = param("currencyType");

        Map<String, CoinProps> cointMap = DatabasesUtil.getCoinPropMaps();
        if(!cointMap.containsKey(currencyType.toLowerCase())){
            currencyType = "BTC";
        }

        currencyType = currencyType.toLowerCase();

        String tableName = currencyType+"receiveAddr";
        String sql = "select * from "+tableName+" where userid=? and isdeleted = 0 order by createTime desc";
        List<Bean> addrs = Data.Query(sql, new Object[] { userId }, ReceiveAddr.class);

        Map<String, Object> addMap = new HashMap<String, Object>();

//		提现地址  WithdrawAddress
//		id String 是 地址ID
//		address String 是 地址
//		memo String 是 备注
        JSONArray ja = new JSONArray();
        if(null != addrs && addrs.size()>0){
            for (Bean bean : addrs) {
                JSONObject addJo = new JSONObject();
                ReceiveAddr addr = (ReceiveAddr) bean;
                addJo.put("id", addr.getId());
                addJo.put("address", addr.getAddress());
                addJo.put("memo", StringUtils.isBlank(addr.getMemo())?"":addr.getMemo());

                ja.add(addJo);
            }
        }
        addMap.put("withdrawAddrs", ja);
        json(SystemCode.code_1000, addMap);
    }
    /**
     * 获取个人充值地址
     * http://www.vip.com/api/m/getRechargeAddress
     *
     */
    @Page(Viewer = JSON)
    public void getRechargeAddress(){
        setLan();
        String userId = param("userId");
        String token = param("token");
        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        int userIdInt = Integer.parseInt(userId);
        String currencyType = param("currencyType");
        if(StringUtils.isNotBlank(currencyType)){
            currencyType = currencyType.toLowerCase();
        }

        User user = userDao.findOne("_id",userId);

        if(user.getUserContact().getGoogleAu() != AuditStatus.pass.getKey() && user.getUserContact().getMobileStatu() != AuditStatus.pass.getKey()){
            json(SystemCode.code_1023);
            return;
        }

        JSONArray ja = new JSONArray();
        Map<String, CoinProps> cointMap = DatabasesUtil.getCoinPropMaps();
        if(StringUtils.isNotBlank(currencyType)){//有传币种获取该币种的地址
            CoinProps coint = cointMap.get(currencyType);
            if(coint!=null){
                keyDao.setCoint(coint);
                KeyBean key = keyDao.getRechargeKey(userIdInt, user.getUserName());
                if(key == null){
                    key = new KeyBean();
                    key.setKeyId(0);
                    key.setKeyPre("");
                }
                JSONObject btcKeyJo = new JSONObject();
                btcKeyJo.put("id", key.getKeyId());
                btcKeyJo.put("address", key.getKeyPre());
                btcKeyJo.put("currencyType",currencyType);

                ja.add(btcKeyJo);

            }else{
                json(SystemCode.code_1001,L("找不到此币种!"));
                return;
            }
        }else{//没传币种，获取全部
            for(Entry<String, CoinProps> entry : cointMap.entrySet()){
                keyDao.setCoint(entry.getValue());
                KeyBean key = keyDao.getRechargeKey(userIdInt, user.getUserName());
                if(key == null){
                    key = new KeyBean();
                    key.setKeyId(0);
                    key.setKeyPre("");
                }
                JSONObject btcKeyJo = new JSONObject();
                btcKeyJo.put("id", key.getKeyId());
                btcKeyJo.put("address", key.getKeyPre());
                btcKeyJo.put("currencyType", entry.getKey().toUpperCase());

                if (StringUtils.isBlank(currencyType)) {
                    ja.add(btcKeyJo);
                } else if(currencyType.toLowerCase().equals(entry.getKey())){
                    ja.add(btcKeyJo);
                }
            }
        }




        if(ja.size() == 0){
            json(SystemCode.code_1001,L("找不到此币种!"));
            return;
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rechargeAddrs", ja);
        json(SystemCode.code_1000, map);
    }
    /**
     * 提交BTC/LTC提现操作
     * http://www.vip.com/api/m/withdraw
     *
     */
    @Page(Viewer = JSON)
    public void withdraw(){
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            String currencyType = param("currencyType");

            currencyType = currencyType.toLowerCase();
            CoinProps coint = DatabasesUtil.coinProps(currencyType);

            User loginUser = userDao.findOne("_id",userId);

			/*start by xzhang 20170829 明确指明过滤该种类型的账户不允许提现 JYPT-1246*/
            if(loginUser == null){
                json(SystemCode.code_1002);
                return;
            }
			/*start by xzhang 20170829 明确指明过滤该种类型的账户不允许提现*/
            if(Const.CUSTOMER_OPERATION_NO_CASH.equals(loginUser.getCustomerOperation())){
                json(SystemCode.code_1001, L("提交失败，当前账户无提现权限"));
                return;
            }
            /**end*/
            BigDecimal cashAmount = decimalParam("cashAmount").setScale(8, BigDecimal.ROUND_DOWN);
            if(currencyType.equalsIgnoreCase("neo")){
                if(cashAmount.compareTo(cashAmount.setScale(0, BigDecimal.ROUND_DOWN)) != 0){
                    json(SystemCode.code_1001, L("NEO提现必须为整数"));
                    return;
                }
            }
            String receiveAddr = param("receiveAddress");
            String liuyan = param("liuyan");
            if(StringUtils.isBlank(liuyan)){
                liuyan = "用户提现"+coint.getPropTag();
            }

		/*	if(loginUser.getUserContact().getGoogleAu() != AuditStatus.pass.getKey() && loginUser.getUserContact().getMobileStatu() != AuditStatus.pass.getKey()){
				json(SystemCode.code_1023);
				return;
		    }*/

//			参数名 类型 是否必须 描述
//			userId String 是 用户id
//			token String 是 登录token
//			currencyType String 是 货币类型：BTC：比特币，LTC：莱特币
//			cashAmount Double 是 提现金额 BTC/LTC
//			receiveAddress String 是 接收地址
//			liuyan String 否 留言
//			safePwd String 否 资金密码 （RSA加密）
//			googleCode String 否 google验证码（RSA加密）
//			dynamicCode String 否 动态验证码（RSA加密）

//			int limitStatus = LimitUtil.moneyLimit(userIdInt, 3, Double.parseDouble(cashAmount));//Double.parseDouble(cashAmount) <=1?1:-1;//
//			if (limitStatus == -2 || limitStatus == -3) {
//				json(SystemCode.code_1011);
//				return;
//			}

            String fingerprint = param("fingerprint");// 指纹识别码，只要有传入，谷歌/短信、资金密码都不用验证
            if(StringUtils.isNotBlank(fingerprint))
                fingerprint = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(fingerprint),priKey));
            String googleCode = param("googleCode");//谷歌验证码
            if(StringUtils.isNotBlank(googleCode))
                googleCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(googleCode),priKey));
            String dynamicCode = param("dynamicCode");//短信验证码
            String emailCode = param("emailCode");//邮箱验证码
            if(StringUtils.isNotBlank(dynamicCode))
                dynamicCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode),priKey));
            String safePwd = param("safePwd");//资金密码 （RSA加密）
            if(StringUtils.isNotBlank(safePwd))
                safePwd = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(safePwd),priKey));

            if(isRunningBarely(loginUser)){
                json(SystemCode.code_1023);
                return;
            }

            boolean aboveAmount = true;
            boolean isNewAddr = isANewAddr(currencyType,userId,receiveAddr);

            //20180412 xzhang 用户提交是新提现地址的话，增加一条提现地址信息
            try{
                if(isNewAddr){
                    ReceiveAddr receive = new ReceiveAddr(receiveAddr, userId, loginUser.getUserName(), BigDecimal.ZERO, now(), 0, "APP Withdraw Address");
                    List<OneSql> sqls = new ArrayList<OneSql>();
                    receiveDao.setCoint(coint);
                    sqls.add(receiveDao.saveAddr(receive));
                    if (!Data.doTrans(sqls)) {
                        log.error("用户："+userId+"维护币种"+currencyType+"的提现地址："+receiveAddr+"发生事务回滚");
                    }
                }
            }catch (Exception e){
                log.error("用户："+userId+"维护币种"+currencyType+"的提现地址："+receiveAddr+"异常，异常信息为：", e);
            }

            if (!validateFingerprintOrRelatives(loginUser, fingerprint, safePwd, dynamicCode, dynamicCode, googleCode)) {
                return;
            }
            String ip = ip();

            long googleCodeLong = 0L;
//			if(StringUtils.isNotBlank(googleCode) && NumberUtils.isDigits(googleCode))
//				googleCodeLong = CommonUtil.stringToLong(googleCode);

            DownloadDao dDao = new DownloadDao();
            dDao.setCoint(coint);
            Message msg = dDao.doBtcDownload(loginUser, cashAmount, receiveAddr, coint.getMinFees(), safePwd, dynamicCode, googleCodeLong, ip, liuyan, getLanTag(), false, true, null, lan, null,emailCode);
            if (msg.isSuc()) {
                json(SystemCode.code_1000);
            } else {
                json(SystemCode.getSystemCodeByKey(msg.getCode()), msg.getMsg());
            }
            return;
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    private boolean isANewAddr(String currencyType, String userId, String receiveAddr) {
        boolean isNewAddr = false;

        String tableName = currencyType.toLowerCase()+CointTable.receiveaddr;
        String sql = "select * from "+tableName+" where userid=? and address=? and isdeleted = 0";
        List<Bean> addrs = Data.Query(sql, new Object[] { userId,receiveAddr }, ReceiveAddr.class);
        if (null == addrs || addrs.size() <= 0)
            isNewAddr = true;

        return isNewAddr;
    }

    /**
     * 取消提现
     */
    @Page(Viewer = JSON)
    public void cancelWithdraw(){
        setLan();
        try{
            //验证安全密码
            String userId = param("userId");
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            String currencyType = param("currencyType");

            CoinProps coint = DatabasesUtil.coinProps(currencyType);
            currencyType = coint.getStag();
            currencyType = currencyType.toLowerCase();
            //取消开始
            UserDao userDao = new UserDao();
            User user = userDao.findOne("_id",userId);
            String fingerprint = param("fingerprint");// 指纹识别码，只要有传入，谷歌/短信、资金密码都不用验证

            if(StringUtils.isNotBlank(fingerprint)) {
                String fingerCode = param("fingerCode");
                Message fingerMsg = isFingerprintCorrect(user, fingerCode, fingerprint);
                if (!fingerMsg.isSuc()) {
                    json(code_1001, fingerMsg.getMsg());
                    return;
                }
				/*fingerprint = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(fingerprint),priKey));
				if (!user.getFingerprint().equals(fingerprint)) {
					json(SystemCode.code_1001, L("验证指纹失败，请重试"));
					return;
				}*/
            } else {
                String safePwd = param("safePwd");//资金密码 （RSA加密）
                if(StringUtils.isEmpty(safePwd)) {
                    json(SystemCode.code_1001, L("资金安全密码不正确。"));
                    return;
                }
                safePwd = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(safePwd),priKey));
                Message passMsg = userDao.safePwd(safePwd, userId + "", getLanTag());
                if(!passMsg.isSuc()){
                    json(SystemCode.code_1001, L(passMsg.getMsg()));
                    return;
                }
            }

            long withdrawId = longParam("withdrawId");
            Message msg = bdDao.doCancelCash(userId, withdrawId, ip(), coint);
            json(msg.getScode(), L(msg.getMsg()));

        }catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }


    @Page(Viewer = JSON)
    public void getBillType(){
        setLan();
        String userId = param("userId");
        String token = param("token");
        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        JSONArray array = new JSONArray();
        array.add(BillType.getObjByType(BillType.recharge));
        array.add(BillType.getObjByType(BillType.download));
        array.add(BillType.getObjByType(BillType.buy));
        array.add(BillType.getObjByType(BillType.sell));

        Map<String, Object> root = new HashMap<String, Object>();
        root.put("billTypes", array);
        json(SystemCode.code_1000, root);
    }

    private static Map<String, Map<String,Long>> REQUEST_TIMES_MAP = new HashMap<String, Map<String,Long>>();

//	@Page(Viewer = JSON)
//	public void searchBill(){
//		String userId = param("userId");
//		if(CommonUtil.cannotRequest(REQUEST_TIMES_MAP, userId)){
//			json(SystemCode.code_4002);
//			return;
//		}
//
//		setLan();
//		try {
//
//			String token = param("token");
//			if (!isLogin(userId, token)) {
//				json(SystemCode.code_1003);
//				return;
//			}
//			//查询条件
//			String currencyType = param("currencyType").toLowerCase();
//			String memo = param("memo");
//			Timestamp startTime = dateParam("startTime");
//			Timestamp endTime = dateParam("endTime");
//			int type = intParam("type");
//			int dataType = intParam("dataType");
//			int pageIndex = intParam("pageIndex");
//			int pageSize = intParam("pageSize");
//			if(pageSize == 0){
//				pageSize = PAGE_SIZE;
//			}
//
//			CoinProps coint = DatabasesUtil.coinProps(currencyType);
//			BillDetailDao bwDao = new BillDetailDao();
//			BeanProxy bp = MysqlDownTable.getProxy("bill");
//			/*if(dataType == 1){
//				bwDao.setDatabase(bp.tableInfo.targetDatabases()[0]);
//			}	暂时没有备份库
//			*/
//			com.world.data.mysql.Query query = bwDao.getQuery();
//			query.setSql("select * from bill");
//			query.setCls(BillDetails.class);
//
//			query.append(" and userId =" + userId);
//
//			if(type > 0){
//				query.append(" and type = " + type);
//			}
//
//			if(currencyType.length() > 0){
//				query.append(" and fundsType = "+coint.getFundsType());
//			}
//
//			if(startTime != null){
//				query.append(" and sendTime >= '"+startTime+"'");
//			}
//
//			if(endTime != null){
//				query.append(" and sendTime <= '"+endTime+"'");
//			}
//
//			List<BillDetails> weight = new ArrayList<BillDetails>();
//				query.append("order by id desc");
//				//分页查询
//				weight = bwDao.findPage(pageIndex, pageSize);
//
//			Map<String, Object> reData = new HashMap<String, Object>();
//
//			JSONArray ja = new JSONArray();
//			for (Bean bean : weight) {
//				BillDetails bd = (BillDetails) bean;
//				JSONObject jo = new JSONObject();
//				jo.put("id", bd.getId());
//				jo.put("type", bd.getType());
//				jo.put("typeName", bd.getBt().getValue());
//				jo.put("change", bd.getAmount());
//				jo.put("balance", bd.getBalance());
//				jo.put("currencyType", bd.getCoinName());
//				jo.put("billDate", bd.getSendTime().getTime());
//				jo.put("showType",bd.getShowType());
//				if(bd.getBt().getInout()==1){
//					jo.put("show","+"+bd.getAmount());
//				}else if(bd.getBt().getInout()==2){
//					jo.put("show","-"+bd.getAmount());
//				}else{
//					jo.put("show",bd.getAmount());
//				}
//
//				ja.add(jo);
//			}
//
//			log.info("IP: "+ ip() +", 用户ID["+ userId +"]请求API V1.6 searchBill()方法, sql: " + query.getSql());
//
//			reData.put("billDetails", ja);
//
//			reData.put("pageIndex", pageIndex);
//			reData.put("pageSize", pageSize);
//			reData.put("totalPage", 0); // getTotalPage(total, pageSize)
//			json(SystemCode.code_1000, reData);
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//			json(SystemCode.code_1002, L("内部异常") + e.getMessage());
//		}
//	}

    @Page(Viewer = JSON)
    public void searchBill(){
        setLan();
        String userId = param("userId");
        if(CommonUtil.cannotRequest(REQUEST_TIMES_MAP, userId)){
            json(SystemCode.code_4002);
            return;
        }
        try {

            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            //查询条件
            String currencyType = param("currencyType").toLowerCase();
            int pageIndex = intParam("pageIndex");
            int pageSize = intParam("pageSize");
            if(pageSize == 0){
                pageSize = PAGE_SIZE;
            }

            CoinProps coint = DatabasesUtil.coinProps(currencyType);
            String downloadTable = coint.getStag() + CointTable.download;
            String detailsTable = coint.getStag() + CointTable.details;

            StringBuilder sql = new StringBuilder();
//			sql.append("select * from ( ");
//			sql.append("select * from ( ");
//			//			-- 提现
//			sql.append("select ");
//			sql.append("2 as type,");
//			sql.append("dd.amount as amount,");
//			sql.append("dd.toAddress as remark,");
//			sql.append("dd.submitTime as sendTime,");
//			sql.append("dd.status as status ");
//			sql.append("from ").append(downloadTable).append(" dd ");
//			sql.append("where dd.userId= '").append(userId).append("' ");
//			sql.append("and dd.isDel=0 ");
//			sql.append("order by dd.id desc ");
//			sql.append(") t ");
//			sql.append("union all ");
//			//			-- 充值
//			sql.append("( ");
//			sql.append("select ");
//			sql.append("ds.type as type,");
//			sql.append("ds.amount as amount,");
//			sql.append("'' as remark,");
//			sql.append("ds.sendTime as sendTime,");
//			sql.append("ds.status as status ");
//			sql.append("from ").append(detailsTable).append(" ds ");
//			sql.append("where ds.userId= '").append(userId).append("' ");
//			sql.append("and ds.type=1 ");
//			sql.append("order by ds.detailsId desc)) t1 ");
//			sql.append("order by t1.sendTime desc ");

			/*start by xwz 20170601 */
            sql.append("select * from ( ");
            sql.append("select * from ( ");
            //			-- 提现
            sql.append("select ");
            sql.append("cast(dd.commandId as char) as commandId ,");
            sql.append("'2' as strType,");
            sql.append("dd.amount as amount,");
            sql.append("dd.toAddress as remark,");
            sql.append("dd.submitTime as sendTime,");
            sql.append("dd.status as status ");
            sql.append("from ").append(downloadTable).append(" dd ");
            sql.append("where dd.userId= '").append(userId).append("' ");
            sql.append("and dd.isDel=0 ");
            sql.append("order by dd.id desc ");
            sql.append(") t ");
            sql.append("union all ");
            //			-- 充值
            sql.append("( ");
            sql.append("select ");
            sql.append("'' as commandId,");
            sql.append("cast(ds.type as char) as strType,");
            sql.append("ds.amount as amount,");
            sql.append("'' as remark,");
            sql.append("ds.sendTime as sendTime,");
            sql.append("ds.status as status ");
            sql.append("from ").append(detailsTable).append(" ds ");
            sql.append("where ds.userId= '").append(userId).append("' ");
            sql.append("and ds.type=1 ");
            sql.append("order by ds.detailsId desc)) t1 ");
            sql.append("order by t1.sendTime desc ");
			/*end*/

            BillDetailDao bwDao = new BillDetailDao();
            com.world.data.mysql.Query query = bwDao.getQuery();
            query.setSql(sql.toString());
            query.setCls(BillDetails.class);

            List<BillDetails> weight;
            //分页查询
            weight = bwDao.findPage(pageIndex, pageSize);

            Map<String, Object> reData = new HashMap<>();

            JSONArray ja = new JSONArray();
            for (Bean bean : weight) {
                BillDetails bd = (BillDetails) bean;
                JSONObject jo = new JSONObject();
                jo.put("type", bd.getStrType());
//				jo.put("typeName", bd.getBt().getValue());
                jo.put("currencyType", bd.getCoinName());
                jo.put("billDate", bd.getSendTime().getTime());
                jo.put("showType",L(bd.getShowType()));
                jo.put("toAddr", bd.getRemark());
                if(bd.getStrBt().getInout()==1){
                    jo.put("show","+"+bd.getAmount());
                }else if(bd.getStrBt().getInout()==2){
                    jo.put("show","-"+bd.getAmount());
                }else{
                    jo.put("show",bd.getAmount());
                }
//				if(bd.getStatus() == 0){
//					jo.put("status", "processing");
//				}else if(bd.getStatus() == 1){
//					jo.put("status", "faild");
//				}else if(bd.getStatus() == 2){
//					jo.put("status", "successful");
//				}else if(bd.getStatus() == 3){
//					jo.put("status", "cancel");
//				}
                //对状态进行国际化 
                String status = "";
                if (bd.getStrType().equals("1")) {  //充值 
                    SysEnum sysEnum = EnumUtils.getEnumByKey(bd.getStatus(), CoinChargeStatus.class);
                    if(sysEnum != null){
                        status = L(sysEnum.getValue());
                    }
                } else if (bd.getStrType().equals("2")) {	//提币 
//					int commandId = 根据时间和用户ID 查询数据库

                    boolean flag = bd.getStatus()==0 && Integer.parseInt(bd.getCommandId())>0;

                    SysEnum sysEnum = EnumUtils.getEnumByKey(flag ? 5 : bd.getStatus(), CoinDownloadStatus.class);
                    if(sysEnum != null){
                        status = L(sysEnum.getValue());
                    }
                }
                jo.put("status", L(status));
                ja.add(jo);
            }

            log.info("IP: "+ ip() +", 用户ID["+ userId +"]请求API V1.6 searchBill()方法, sql: " + query.getSql());

            reData.put("billDetails", ja);

            reData.put("pageIndex", pageIndex);
            reData.put("pageSize", pageSize);
            reData.put("totalPage", 0); // getTotalPage(total, pageSize)
            json(SystemCode.code_1000, reData);
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    @Page(Viewer = JSON)
//	public void searchWithdraw(){
//		setLan();
//		try{
//			String userId = param("userId");
//			String token = param("token");
//			if (!isLogin(userId, token)) {
//				json(SystemCode.code_1003);
//				return;
//			}
//			int status = intParam("status");
//			Timestamp startTime = dateParam("startTime");
//			Timestamp endTime = dateParam("endTime");
//			int pageIndex = intParam("pageIndex");
//			int pageSize = intParam("pageSize");
//
//			if(pageSize == 0){
//				pageSize = 10;
//			}
//
//			String currencyType = param("currencyType");
//			if(StringUtils.isNotBlank(currencyType)) currencyType = currencyType.toLowerCase();
//
//			CoinProps coint = DatabasesUtil.coinProps(currencyType);
//
//			com.world.data.mysql.Query<DownloadBean> query = bdDao.getQuery();
//			bdDao.setCoint(coint);
//			query.setSql("select * from "+bdDao.getTableName());
//			query.setCls(DownloadBean.class);
//
//			query.append(" userId='" + userId + "' and isDel=0 ");
//
//			if (status > -1) {
//				query.append(" and status=" + status);
//			}
//
//			int total = query.count();
//			List<DownloadBean> downloads = new ArrayList<>();
//			if(total > 0){
//				query.append("order by submitTime desc");
//				//分页查询
//				downloads = bdDao.findPage(pageIndex, pageSize);
//				for (DownloadBean btcBean : downloads) {
//					btcBean.setRemark(btcBean.getRemarkExHTML());
//				}
//			}
//
//			Map<String, Object> root = new HashMap<>();
//			root.put("withdrawDetails", downloads);
//			root.put("pageIndex", pageIndex);
//			root.put("pageSize", pageSize);
//			root.put("totalPage", getTotalPage(total, pageSize));
//			json(SystemCode.code_1000, root);
//		} catch (Exception e) {
//			log.error("内部异常", e);
//			json(SystemCode.code_1002, L("内部异常") + e.getMessage());
//		}
//	}

    protected boolean validateFingerprintOrRelatives(User loginUser, String fingerprint, String safePwd, String mobileCode, String emailCode, String googleCode) throws Exception {
        String userId = loginUser.get_Id();
        UserContact uc = loginUser.getUserContact();
        int safePwdFlag = 0, mobileCodeFlag = 0, emailCodeFlag = 0, googleCodeFlag = 0;
        boolean lacksafePwdFlag = false,
                lackmobileCodeFlag = false,
                lackemailCodeFlag = false,
                lackgoogleCodeFlag = false;
        Message msg = new Message();
        Map<String, Object> retData = new HashMap<>();
        if (StringUtils.isNotBlank(fingerprint)) {//有传指纹过来
            String fingerCode = param("fingerCode");
            Message fingerMsg = isFingerprintCorrect(loginUser, fingerCode, fingerprint);
            if (!fingerMsg.isSuc()) {
                json(code_1001, fingerMsg.getMsg(), retData);
                return false;
            }
        } else {//没有传指纹
            safePwdFlag = 1;
            if (uc.getGoogleAu() == AuditStatus.pass.getKey() && loginUser.isPayGoogleAuth()) {//Google提现验证开关：如果开启了，当需要验证手机的时候登录Google验证，关闭了的话就使用短信验证
                googleCodeFlag = 1;
            }
            mobileCodeFlag = 1;
            if (!loginUser.isPayMobileAuth()) {

                mobileCodeFlag = 0;
            }
            if (uc.getMobileStatu() != AuditStatus.pass.getKey() && loginUser.isPayEmailAuth()) {
                emailCodeFlag = 1;
            }
        }
        retData.put("needSafePwd", safePwdFlag);
        retData.put("needMobileCode", mobileCodeFlag);
        retData.put("needEmailCode", emailCodeFlag);
        retData.put("needGoogleCode", googleCodeFlag);
        if (safePwdFlag == 1) {
            if (StringUtils.isBlank(safePwd)) {
//				msg.setMsg("本次操作需要您填写资金密码");
                lacksafePwdFlag = true;
            } else {
                msg = userDao.safePwd(safePwd, userId, getLanTag());
                if (!msg.isSuc()) {
                    SystemCode safePwdErrorCode = getSystemCodeByKey(msg.getCode());
                    json(safePwdErrorCode, retData);
                    return false;
                }
            }

        }
        if (googleCodeFlag == 1) {
            if (StringUtils.isBlank(googleCode)) {
//				msg.setMsg(msg.getMsg()+"&Google");
                lackgoogleCodeFlag = true;
            } else {
                long gCode = CommonUtil.stringToLong(googleCode);
                userDao.setLan(lan);
                msg = userDao.isCorrect(loginUser, uc.getSecret(), gCode);
                if (!msg.isSuc()) {
                    json(code_1001, L(msg.getMsg()), retData);
                    return false;
                }
            }
        }
        if (mobileCodeFlag == 1) {
            if (StringUtils.isBlank(mobileCode)) {
//				msg.setMsg(msg.getMsg()+"&Google"+"&MobileCode");
                lackmobileCodeFlag = true;
            } else {
                // 检查短信验证码
                ClientSession clientSession = new ClientSession(ip(), uc.getSafeMobile(), lan, PostCodeType.cash.getValue(), false);
                DataResponse dr = clientSession.checkCode(mobileCode);
                if (!dr.isSuc()) {
                    json(code_1001, L(dr.getDes()), retData);
                    return false;
                }
            }
        }
        if (emailCodeFlag == 1) {
            if (StringUtils.isBlank(emailCode)) {
//				msg.setMsg(msg.getMsg()+"&Google"+"&MobileCode"+"&emailCode");
                lackemailCodeFlag = true;
            } else {
                // 检查邮件验证码
                ClientSession clientSession = new ClientSession(ip(), uc.getSafeEmail(), lan, PostCodeType.cash.getValue(), false);
                DataResponse dr = clientSession.checkCode(emailCode);
                if (!dr.isSuc()) {
                    json(code_1001, L(dr.getDes()), retData);
                    return false;
                }
            }
        }
        if (lacksafePwdFlag ||
                lackmobileCodeFlag ||
                lackemailCodeFlag ||
                lackgoogleCodeFlag) {
            //国际化detail 2017-03-17
            String detail = L("本次操作需要您填写") + " ";

            detail += lacksafePwdFlag ? L("资金密码&") : "";
            detail += lackmobileCodeFlag ? L("短信验证码&") : "";
            detail += lackemailCodeFlag ? L("邮箱验证码&") : "";
            detail += lackgoogleCodeFlag ? L("谷歌验证码&") : "";

            detail = detail.substring(0, detail.length() - 1);
            msg.setMsg(detail);
            json(code_1001, msg.getMsg(), retData);
            return false;
        }
        return true;
    }


    @Page(Viewer = JSON)
    public void updateWithdrawAddressMemo() {
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            String currencyType = param("currencyType");
            currencyType = currencyType.toLowerCase();

            int userIdInt = intParam("userId");
            int withdrawAddressId = intParam("withdrawAddressId");
            String memo = param("memo");

            if (withdrawAddressId > 0) {
                String tableName = currencyType + CointTable.receiveaddr;
                int count = Data.Update("UPDATE "+tableName+" SET memo = ? Where userId = ? AND id = ?", new Object[] { memo, userIdInt, withdrawAddressId });

                if (count > 0) {
                    json(SystemCode.code_1000);
                } else {
                    json(SystemCode.code_1001, L("操作失败，请稍后重试"));
                }
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    @Page(Viewer = JSON)
    public void addWithdrawAddress() {
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            String currencyType = param("currencyType");
            currencyType = currencyType.toLowerCase();
            String address = request.getParameter("withdrawAddress");
            address = address.trim();
            int withdrawAddressId = intParam("withdrawAddressId");
            String memo = param("memo");

            String fingerprint = param("fingerprint");// 指纹识别码，只要有传入，谷歌/短信、资金密码都不用验证
            if(StringUtils.isNotBlank(fingerprint))
                fingerprint = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(fingerprint),priKey));
            String googleCode = param("googleCode");
            if(StringUtils.isNotBlank(googleCode))
                googleCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(googleCode),priKey));
            String dynamicCode = param("dynamicCode");
            if(StringUtils.isNotBlank(dynamicCode))
                dynamicCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode),priKey));
            String safePwd = param("safePwd");//资金密码 （RSA加密）
            if(StringUtils.isNotBlank(safePwd))
                safePwd = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(safePwd),priKey));

            long googleCodeLong = 0L;
            if(StringUtils.isNotBlank(googleCode) && NumberUtils.isDigits(googleCode))
                googleCodeLong = CommonUtil.stringToLong(googleCode);

            CoinProps coint = DatabasesUtil.coinProps(currencyType);
            User user = userDao.get(userId);
            receiveDao.setCoint(coint);
            Message msg = receiveDao.doAddReceiveAddr(lan,user, safePwd, dynamicCode, (int)googleCodeLong, memo, withdrawAddressId, address, ip(), getLanTag());
            if(msg.isSuc()){
                json(SystemCode.code_1000);
            }else{
                json(msg.getScode(), msg.getMsg());
            }

        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    @Page(Viewer = JSON)
    public void setFingerprint() {
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            String fingerprint = param("fingerprint");
            fingerprint = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(fingerprint), priKey));

            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("fingerprint", fingerprint);
            UpdateResults<User> ur = userDao.update(q, ops);
            if (!ur.getHadError()) {
                json(SystemCode.code_1000);
            } else {
                json(SystemCode.code_1001, L("操作失败，请稍后重试"));
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    @Page(Viewer = JSON)
    public void setRegistrationID() {
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            String registrationID = param("registrationID");
            //registrationID = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(registrationID), priKey));

            String oldRegId = userDao.findOne("_id",userId).getJpushKey();

            if(StringUtils.isNotBlank(oldRegId)){
                log.info("用户"+userId+"老的jpushKey is:"+oldRegId);
            }else {
                log.info("用户"+userId+"老的jpushKey is empty");
            }

            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

            User user = userDao.getUserByColumn(registrationID, "jpushKey");
            if (null !=user) {
                user.setJpushKey("");
                userDao.save(user);
            }

            ops.set("jpushKey", registrationID);
            UpdateResults<User> ur = userDao.update(q, ops);
            if (!ur.getHadError()) {
                if(StringUtils.isNotBlank(oldRegId)  && StringUtils.isNotBlank(registrationID) && !oldRegId.equals(registrationID)){
                    log.info("用户"+userId+"老的jpushKey is:"+oldRegId+"&新的jpushKey is:"+registrationID);
                    try {
                        Pusher.push(L("您的账号在新设备上登录了，如非您本人操作，请及时修改密码!"), oldRegId,MsgType.abnormalLogin);
                    } catch (Exception e) {
                        log.error("【极光推送】当前用户:"+userId+",所用registrationId："+oldRegId+"推送："+MsgType.abnormalLogin.getValue()+"消息异常，异常信息为:", e);
                    }
                }
                json(SystemCode.code_1000);
            } else {
                json(SystemCode.code_1001, L("操作失败，请稍后重试"));
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    /**
     * 行情价格提醒设置

     @Page(Viewer = JSON)
     public void setMarketReminds() {
     setLan();
     String userId = param("userId");
     String token = param("token");

     if (!isLogin(userId, token)) {
     json(SystemCode.code_1003);
     return;
     }
     //		status String 是 开关状态，0：关闭 1：开启
     //		high String 是 高价位
     //		low String 是 低价位
     String marketReminds = request.getParameter("marketReminds");
     JSONArray remindsAry = JSONArray.parseArray(marketReminds);
     for (Object o : remindsAry) {
     JSONObject jsonObject = (JSONObject) o;
     int status = jsonObject.getIntValue("status");
     String high = jsonObject.getString("high");
     String low = jsonObject.getString("low");
     String currencyType = jsonObject.getString("currencyType");
     Map<String, CoinProps> types = DatabasesUtil.getCoinPropMaps();

     if (!types.containsKey(currencyType.toLowerCase())) {
     continue;
     }
     Query<MarketRemind> query = marketRemindDao.getQuery();
     query.filter("userId", userId);
     query.or(
     query.criteria("symbol").equal(currencyType.toLowerCase()),
     query.criteria("symbol").equal(currencyType.toUpperCase())
     );
     QueryResults<MarketRemind> results = marketRemindDao.find(query);
     //			results.asList();
     long count = results.countAll();
     if (count > 0) {
     marketRemindDao.deleteByQuery(query);
     }

     MarketRemind entity = new MarketRemind();

     entity.setStatus(status);
     entity.setHigh(high);
     entity.setLow(low);
     entity.setSymbol(currencyType.toLowerCase());
     entity.setUserId(userId);

     String nid = marketRemindDao.save(entity).getId().toString();

     }
     json(SystemCode.code_1000);
     }
     */

    /**
     * 获取行情价格提醒设置
     * http://www.vip.com/api/m/getMarketReminds

     @Page(Viewer = JSON)
     public void getMarketReminds() {
     setLan();
     String userId = param("userId");
     String token = param("token");

     if (!isLogin(userId, token)) {
     json(SystemCode.code_1003);
     return;
     }
     List<MarketRemind> reminds = marketRemindDao.getList(userId);

     JSONArray marketReminds = new JSONArray();
     for (MarketRemind e : reminds) {
     JSONObject jsonObject = new JSONObject();
     jsonObject.put("status", e.getStatus() + "");
     jsonObject.put("high", e.getHigh());
     jsonObject.put("low", e.getLow());
     jsonObject.put("currencyType", e.getSymbol().toUpperCase());
     jsonObject.put("currentPrice", MarketPrices.get().getString(e.getSymbol()));

     marketReminds.add(jsonObject);
     }

     Map<String, Object> reData = new HashMap<String, Object>();
     reData.put("marketReminds", marketReminds);
     log.info("当前价格提醒设置:" + reData);
     json(SystemCode.code_1000, reData);
     }
     */

    @Page(Viewer = JSON)
    public void getCounterFee() {
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");

            boolean isLoginUser = false;
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            } else {
                isLoginUser = true;
            }
            String currencyType = param("currencyType");

            JSONArray ja = new JSONArray();
            Map<String, CoinProps> coinMap = DatabasesUtil.getCoinPropMaps();
            for(Entry<String, CoinProps> entry : coinMap.entrySet()){
                JSONObject obj = new JSONObject();

                if(currencyType.length() > 0){
                    if(entry.getKey().equals(currencyType.toLowerCase())){
                        obj.put("currencyType", entry.getKey());
                        obj.put("counterFee", entry.getValue().getMinFees());
                        ja.add(obj);
                    }
                }else{
                    obj.put("currencyType", entry.getKey());
                    obj.put("counterFee", entry.getValue().getMinFees());
                    ja.add(obj);
                }
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("feeInfos", ja);
            json(SystemCode.code_1000, map);
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    @Page(Viewer = JSON)
    public void printPrice() {
        setLan();
        String name2="btcdefault"+"_hotdata";
        BigDecimal currentBtcPrice = UserCache.getBtcPrice();
        String data=Cache.Get(name2);
        Map<String, Object> reData = new HashMap<String, Object>();
        reData.put("ary", data);
        reData.put("curBtc", currentBtcPrice);
        json(SystemCode.code_1000, reData);
    }

    @Page(Viewer = JSON)
    public void getGoogleSecret(){
        setLan();
        String userId = param("userId");
        String token = param("token");

        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        Map<String, Object> reData = new HashMap<String, Object>();
        String secret = GoogleAuthenticator.generateSecretKey();
        reData.put("secret", secret);

        String tips = "";

//		String fileName="statics/GoogleAuthTip.html";
        String realPath=request.getServletContext().getRealPath(getGoogleAuthTipHtml(lan));

        File file=new File(realPath);
        try {
            //http://twww.vip.com/statics/GoogleAuthTip.html
            Document doc = Jsoup.parse(file, "UTF-8");
            Element secretEle = doc.getElementById("secret");
            Element webnameEle = doc.getElementById("webname");
            Elements elements = doc.getAllElements();
            for (Element e : elements) {
                log.info(e.toString());
            }
            secretEle.text(secret);
            webnameEle.text(WEB_NAME);

            tips = doc.toString();
        } catch (IOException e) {
            log.error("内部异常", e);
        }

        reData.put("tips", tips);
        json(SystemCode.code_1000,reData);
    }

    //获取谷歌认证提示地址
    private static String getGoogleAuthTipHtml(String lan){
        String path = "/statics/GoogleAuthTip";
        switch(lan){
            case "en":
            case "cn":
            case "hk":
            case "Jr":
            case "de":

                path += "_" + lan;
                break;
            case "tw":
                path += "_hk";
                break;
            default:
                path += "_en";
                break;
        }
        return path + ".html";
    }


    @Page(Viewer = JSON)
    public void setGoogleCode() {
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");
            int type = intParam("type");//操作类型 1 设置/修改谷歌验证 0 关闭谷歌验证|

            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User loginUser = new UserDao().get(userId);
            // 先较验 用户修改申请审核中，此时不能设置，不能关闭，谷歌验证
            VerifyUserInfo bean = vudao.getVerifyingInfo(loginUser.getId(), 2);
            if (bean != null && bean.getStatus() == 0) {
                json(SystemCode.code_1021);
                return;
            }

            String googleCode = param("googleCode");
            if(StringUtils.isNotBlank(googleCode))
                googleCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(googleCode), priKey));

            log.info("参数Googlecode="+googleCode);

            long gCode = CommonUtil.stringToLong(googleCode, -1);

            String dynamicCode = param("dynamicCode");
            if(StringUtils.isNotBlank(dynamicCode))
                dynamicCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode), priKey));
            log.info("参数mobileCode="+dynamicCode);

            String userIp = ip();
            UserContact uc = loginUser.getUserContact();
            String sendAddr = uc.getSafeMobile();
            if (StringUtils.isBlank(sendAddr)) {
                sendAddr = uc.getSafeEmail();
            }
            // 检查短信验证码
            ClientSession clientSession = new ClientSession(userIp, sendAddr, lan, PostCodeType.googleAuth.getValue(), false);
            DataResponse dr = clientSession.checkCode(dynamicCode);
            if (!dr.isSuc()) {
                json(SystemCode.code_1024, dr.getDes());
                return;
            }

            String secret = type==1?param("secret"):uc.getSecret();//设置或修改 用传过来的secret，关闭则用用户原有的secret
            log.info("secret="+secret);

            if (StringUtils.isBlank(secret) && type ==0) {//没有认证却要关闭
                json(SystemCode.code_1001,L("您还未开启Google认证"));
                return;
            }

            if (!isGoogleCodeCorrect(secret, gCode, userId)) {
                return;
            }

            int googleAu = 0;//谷歌双重验证是否开启(0未验证     1验证未开启     2已开启)
            if (type == 1 && StringUtils.isBlank(uc.getSecret())) {//设置
                logDao.insertOneRecord(AuthenType.google.getKey(),userId, "0", "成功绑定谷歌认证。", ip());
                googleAu = 2;
            } else if (type == 1 && StringUtils.isNotBlank(uc.getSecret())) {//修改，且已有secret，查查是不是在审核中，审核中驳回，非审核中插入申请记录
                // 用户是修改GOOGLE认证
                // 插入申请记录
                VerifyUserInfo info = new VerifyUserInfo(vudao.getDatastore());
                info.setUserId(loginUser.getId());
                info.setUserName(loginUser.getUserName());
                info.setType(2);// Google
                info.setInfo(secret);
                info.setBeforeInfo(uc.getSecret());
                info.setAddTime(TimeUtil.getNow().getTime());
                info.setIp(ip());

                vudao.add(info);
                json(SystemCode.code_1000,L("申请成功，客服将尽快为您审核，请耐心等待。"));
                log.info("01YYSPTX【修改谷歌验证】:" + "用户:" + loginUser.getUserName() + "发起了修改谷歌验证申请，请客服人员立即处理。");
                logDao.insertOneRecord(AuthenType.googleApply.getKey(),userId, "0", "发起谷歌验证修改请求。", ip());
                return;
            } else {//关闭，清空secret
                logDao.insertOneRecord(AuthenType.google.getKey(),userId, "0", "关闭谷歌认证。", ip());
                googleAu = 1;
                secret = "";
            }

            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("userContact.googleAu", googleAu);
            ops.set("userContact.secret", secret);
            ops.set("payGoogleAuth", true);
            UpdateResults<User> ur = userDao.update(q, ops);
            if (!ur.getHadError()) {

                json(SystemCode.code_1000/*,"您已成功开启谷歌验证!"*/);
                //2017.08.16 xzhang BITA-533 谷歌验证通过未给用户加积分
                if(1==type){
                    JifenManage  jifenManager = new JifenManage(loginUser.getId(), 5, null, null, "VIP");//5：谷歌验证获得100积分
                    SingletonThreadPool.addJiFenThread(jifenManager);
                }
            } else {
                json(SystemCode.code_1001, L("出错了，请稍后重试!"));
            }

        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    @Page(Viewer = JSON)
    public void changeDynamicCodeAuth() {
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");

            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User loginUser = userDao.get(userId);
            UserContact uc = loginUser.getUserContact();
            int oper = intParam("operation");//操作类型 0 关闭 1 开启     |
            int authType = intParam("authType");//| authType| Integer| 是    | 验证类型 1：异地登录验证2：提现验证     |

            boolean dealVal = false;
            String des = "";
            String authTypeStr = authType == 1?L("异地登录") : L("提现");
            String columnName = "";//authType == 1?"diffAreaLoginNoCheck":"payMobileAuth";
            if (authType == 1) {
                columnName = "diffAreaLoginNoCheck";
            } else if (authType == 2) {
                if (uc.getMobileStatu() == AuditStatus.pass.getKey()) {
                    columnName = "payMobileAuth";
                } else {
                    columnName = "payEmailAuth";
                }
            } else {
                json(SystemCode.code_1019);
            }

            if (oper == 0) {
//				if (loginUser.isDiffAreaLoginNoCheck()) {
//					json(SystemCode.code_1001,L("异地登录短信验证码验证已关闭。"));
//					return;
//				}
                if (authType == 2 && !loginUser.isPayGoogleAuth()) {
                    json(SystemCode.code_1001, L("您没有开启提现Google验证不能关闭提现短信验证。"));
                    return;
                }
                dealVal = true;
                if (authType==2) dealVal = false;
                des = "成功关闭%%动态验证码验证。";
            } else if (oper == 1) {
//				if (!loginUser.isDiffAreaLoginNoCheck()) {
//					json(SystemCode.code_1001,L("异地登录短信验证码验证已开启。"));
//					return;
//				}
                dealVal = false;
                if (authType==2) dealVal = true;
                des = "成功开启%%动态验证码验证。";
            } else {
                json(SystemCode.code_1019);
                return;
            }
            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set(columnName, dealVal);
            UpdateResults<User> ur = userDao.update(q, ops);
            if (!ur.getHadError()) {
                json(SystemCode.code_1000, L(des,authTypeStr));
            }
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    //开启或关闭安全密码
    @Page(Viewer = JSON)
    public void useOrCloseSafePwd(){
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");

            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User loginUser = userDao.get(userId);
            int period = intParam("period");//是    | 关闭周期 1：始终开启 0：永久关闭 2：6个小时|

            if (period == 1) {//开启
                if(userDao.isNeedSafePwd(loginUser)){
                    json(SystemCode.code_1001,L("资金安全密码已经开启。"));
                    return;
                }

                loginUser.setNeedSafePwd(true);

                UpdateResults<User> ur = userDao.setSafePwdExpirationTime(loginUser);

                if (!ur.getHadError()) {
                    logDao.insertOneRecord(AuthenType.closeSafePwd.getKey(), userId, "0", "手动开启安全密码。", ip());

                    json(SystemCode.code_1000,L("安全密码成功开启。"));
                    return;
                }else {
                    json(SystemCode.code_1001,L("开启资金安全密码失败。"));
                }

            } else if (period == 0 || period ==2) {//关闭: 0 permanently 2 6 Hours
                String safePwd = param("safePwd");
                if(StringUtils.isNotBlank(safePwd))
                    safePwd = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(safePwd),priKey));

                if(!safePwd(safePwd, userId)){
                    return;
                }
                Date dateNow = new Date();
                long expirationTime = period == 0 ? 0L: dateNow.getTime() + 1000*60*60*6;//| 关闭周期 0：永久关闭 2：6个小时|

                loginUser.setNeedSafePwd(false);
                loginUser.setSafePwdExpiration(expirationTime);

                UpdateResults<User> ur = userDao.setSafePwdExpirationTime(loginUser);

                if (!ur.getHadError()) {
                    logDao.insertOneRecord(AuthenType.closeSafePwd.getKey(), userId, "0", "成功关闭安全密码，时间："+(period==0?"永久":"6小时"), ip());

                    if(period == 0){
                        json(SystemCode.code_1000,L("安全密码关闭成功，如有需求，可手动开启。"));
                    }else{
                        json(SystemCode.code_1000,L("安全密码关闭成功，六小时后自动开启。"));
                    }
                }else {
                    json(SystemCode.code_1001,L("关闭失败!"));
                }

            }else {
                json(SystemCode.code_1019);
            }

        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    @Page(Viewer = JSON)
    public void changeGoogleAuth() {
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");

            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User loginUser = userDao.get(userId);
            int oper = intParam("operation");//操作类型 0 关闭 1 开启     |
            int authType = intParam("authType");//| authType| Integer| 是    | 验证类型 1：登录验证2：提现验证     |

            boolean dealVal = false;
            String des = "";

            UserContact uc = loginUser.getUserContact();

//			boolean destAuthSwitch = authType == 1?loginUser.isLoginGoogleAuth():loginUser.isPayGoogleAuth();
            String authTypeStr = authType == 1? L("登录") : L("支付");
            String columnName = authType == 1?"loginGoogleAuth":"payGoogleAuth";

            if (oper == 0) {//close
                // 先较验 用户修改申请审核中，此时不能关闭相关谷歌验证
                VerifyUserInfo bean = vudao.getVerifyingInfo(loginUser.getId(), 2);
                if (bean != null && bean.getStatus() == 0) {
                    json(SystemCode.code_1021);
                    return;
                }
                if (uc.getMobileStatu() != 2 && authType == 2) {
                    json(SystemCode.code_1001,L("您没有进行手机认证不能关闭Google支付验证码验证。"));
                    return;
                }
                if (authType == 2 && !loginUser.isPayMobileAuth()) {
                    json(SystemCode.code_1001, L("您没有开启提现短信验证不能关闭提现Google验证。"));
                    return;
                }
//				if (authType == 2) {
                String googleCode = param("googleCode");
                if(StringUtils.isNotBlank(googleCode)){
                    googleCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(googleCode), priKey));
                    log.info("参数Googlecode="+googleCode);
                    if (!NumberUtils.isNumber(googleCode)) {
                        json(SystemCode.code_1001,L("谷歌验证码错误!"));
                        return;
                    }
                    long gCode = CommonUtil.stringToLong(googleCode);

                    if (uc.getGoogleAu() != AuditStatus.pass.getKey()) {
                        json(SystemCode.code_1001,L("您还没开启Google认证，请开启Google认证后重试。"));
                        return;
                    }
                    if (!isGoogleCodeCorrect(uc.getSecret(), gCode, userId)) {
                        return;
                    }
                }else {
                    json(SystemCode.code_1015);
                    return;
                }
//				}
				/*if (!destAuthSwitch) {
					json(SystemCode.code_1001,L(authTypeStr+"Google验证码验证已关闭。"));
					return;
				}*/
                dealVal = false;
                des = "已成功关闭%%Google验证码验证。";
            } else if (oper == 1) {//open
				/*if (destAuthSwitch) {
					json(SystemCode.code_1001,L(authTypeStr+"Google验证码验证已开启。"));
					return;
				}*/
                //当用户修改申请审核中时，谷歌认证状态为0=未验证,此时不能开启相关谷歌验证
                VerifyUserInfo bean = vudao.getVerifyingInfo(loginUser.getId(), 2);
                if (bean != null) {
                    json(SystemCode.code_1021);
                    return;
                }

				/*int safeBu = uc.getGoogleAu();
				if (safeBu != 2) {
					json(SystemCode.code_1001,L("请先开启Google认证。"));
					return;
				}*/

                dealVal = true;
                des = "成功开启%%Google验证码验证。";
            } else {
                json(SystemCode.code_1019);
                return;
            }

            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set(columnName, dealVal);
            UpdateResults<User> ur = userDao.update(q, ops);
            if (!ur.getHadError()) {
                json(SystemCode.code_1000,L(des, authTypeStr));
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    @Page(Viewer = JSON)
    public void changePayEmailAuth() {
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");

            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User loginUser = userDao.get(userId);
            int oper = intParam("operation");//操作类型 0 关闭 1 开启     |
            String dynamicCode = param("dynamicCode");
            if (StringUtils.isNotBlank(dynamicCode))
                dynamicCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode), priKey));

            boolean dealVal = false;
            String des = "";

            String userIp = ip();
            // 检查短信验证码
            String codeRecvAddr = loginUser.getUserContact().getSafeMobile();
            if (StringUtils.isBlank(codeRecvAddr)) {
                codeRecvAddr = loginUser.getUserContact().getSafeEmail();
            }
            ClientSession clientSession = new ClientSession(userIp, codeRecvAddr, lan, PostCodeType.safeAuth.getValue(), false);
            DataResponse dr = clientSession.checkCode(dynamicCode);
            if (!dr.isSuc()) {
                json(SystemCode.code_1024,L(dr.getDes()));
                return;
            }

            if (oper == 0) {
                dealVal = false;
                des = "已成功关闭支付邮箱验证码验证。";
            } else if (oper == 1) {
                int emailAuth = loginUser.getUserContact().getEmailStatu();
                if (emailAuth != 2) {
                    json(SystemCode.code_1001,L("请先进行邮箱认证。"));
                    return;
                }
                dealVal = true;
                des = "成功开启支付邮箱验证码验证。";
            } else {
                json(SystemCode.code_1019);
                return;
            }
            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("payEmailAuth", dealVal);
            UpdateResults<User> ur = userDao.update(q, ops);
            if (!ur.getHadError()) {
                json(SystemCode.code_1000, L(des));
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    @Page(Viewer = JSON)
    public void changePayMobileAuth() {
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");

            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User loginUser = userDao.get(userId);
            int oper = intParam("operation");//操作类型 0 关闭 1 开启     |
            String dynamicCode = param("dynamicCode");
            if (StringUtils.isNotBlank(dynamicCode))
                dynamicCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode), priKey));

            boolean dealVal = false;
            String des = "";

            String userIp = ip();

            if (oper == 0) {
                // 检查短信验证码
                String codeRecvAddr = loginUser.getUserContact().getSafeMobile();
                ClientSession clientSession = new ClientSession(userIp, codeRecvAddr, lan, PostCodeType.safeAuth.getValue(), false);
                DataResponse dr = clientSession.checkCode(dynamicCode);
                if (!dr.isSuc()) {
                    json(SystemCode.code_1024,L(dr.getDes()));
                    return;
                }
                dealVal = false;
                des = "已成功关闭提现短信验证。";
            } else if (oper == 1) {
                int mobileAuth = loginUser.getUserContact().getMobileStatu();
                if (mobileAuth != 2) {
                    json(SystemCode.code_1001,L("请先进行手机认证。"));
                    return;
                }
                dealVal = true;
                des = "成功开启提现短信验证。";
            } else {
                json(SystemCode.code_1019);
                return;
            }
            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("payMobileAuth", dealVal);
            UpdateResults<User> ur = userDao.update(q, ops);
            if (!ur.getHadError()) {
                json(SystemCode.code_1000, L(des));
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常"));
        }
    }

    public int getDiffDay(Timestamp endTime, Timestamp startTime) {
        long intervalMilli = endTime.getTime() - startTime.getTime();
        return (int) intervalMilli;
    }

    // 完成登录相关的操作
    private void wapLogin(String safe, int remember, User safeUser, String ip) {
        try {
            com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
            UserContact uc = safeUser.getUserContact();
            Authentication au = new AuthenticationDao().getByUserId(safeUser.get_Id());
            if (null != uc) {
                json.put("emailStatu", uc.getEmailStatu());
                json.put("mobileStatu", uc.getMobileStatu());
                json.put("googleAuth", uc.getGoogleAu());
            }
            if (null != au) {
                json.put("auth", au.getStatus());
            }
            String pwdStatus = "1", safePwdStatus = "1";
            if (null != safeUser.getPwd() && !"".equals(safeUser.getPwd())) {
                pwdStatus = "2";
            }
            if (null != safeUser.getSafePwd() && !"".equals(safeUser.getSafePwd())) {
                safePwdStatus = "2";
            }
            json.put("pwdStatus", pwdStatus);
            json.put("safePwdStatus", safePwdStatus);
            json.put("ipNeedAuthen", false);
            json.put("loginNeedGoogleAuth", false);
            SSOLoginManager.toLogin(this, remember, safeUser.getId(), safeUser.getUserName(), true, ip, safeUser.getVipRate()+"", "", false, json);

            Datastore ds = userDao.getDatastore();
            Query<User> query = ds.find(User.class, "_id", safeUser.getId());
            userDao.update(query, ds.createUpdateOperations(User.class).set("previousLogin", safeUser.getLastLoginTime() == null ? now() : safeUser.getLastLoginTime()).set("lastLoginTime", now()).set("loginIp", ip).set("trueIp", safeUser.getLoginIp() == null ? ip : safeUser.getLoginIp()));

            //更新内存里,cookie里的vip等级
            SSOLoginManager.updateVip(safeUser.getId(), safeUser.getVipRate());

        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    @Page(Viewer = JSON)
    public void getUserOpenId(){
        setLan();
        String userId = param("userId");
        String token = param("token");

        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        User loginUser = userDao.getUserById(userId);
        if(loginUser.isFreez()){
            json(SystemCode.code_1001, L("该账户已冻结，暂时不能操作。"));
            return;
        }

        String nowTime = new Date().getTime()+"";

        String uid = MD5.toMD5( MD5.toMD5(userId)+MD5.toMD5(nowTime) );//生成uid

        Map<String, Object> reData = new HashMap<String, Object>();

        if (StringUtils.isEmpty(loginUser.getWapUid())) {//如果已经有记录过了,就不再记录了

            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("wapUid", uid);
            UpdateResults<User> ur = userDao.update(q, ops);
            if (ur.getHadError()) {
                json(SystemCode.code_1001);
                return;
            }else {
                reData.put("userOpenId", uid);
            }
        }else {
            reData.put("userOpenId", loginUser.getWapUid());
        }
        json(SystemCode.code_1000,reData);
    }

    private String userOpenId(User user){
        setLan();
        String userId = user.getId();
        String nowTime = System.currentTimeMillis()+"";

        String uid = MD5.toMD5( MD5.toMD5(userId)+MD5.toMD5(nowTime) );//生成uid

        if (StringUtils.isEmpty(user.getWapUid())) {//如果已经有记录过了,就不再记录了

            Datastore ds = userDao.getDatastore();
            Query<User> q = ds.find(User.class, "_id", userId);
            UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
            ops.set("wapUid", uid);
            UpdateResults<User> ur = userDao.update(q, ops);
            ur.getHadError();
        }else {
            uid = user.getWapUid();
        }
        return uid;
    }


    @Page(Viewer = JSON)
    public void reSendEmail() {
        setLan();
        String nid = param("userId");
        User user = userDao.findOne("_id",nid);
        if (null == user) {
            json(SystemCode.code_3004);
            return;
        }
        EmailDao eDao = new EmailDao();
        String email = user.getUserContact().getCheckEmail();
        if (StringUtils.isBlank(email)) {
            json(SystemCode.code_1001,L("账号已激活!"));
            return;
        }
        String emailCode = MD5.toMD5(System.currentTimeMillis()+email);
        userDao.updateEmailCode(nid, email, emailCode);
        String info = eDao.getRegEmailHtml(nid, lan, email, emailCode, this);
        SysGroups sg = SysGroups.vip;
        String title = L(SysGroups.vip.getValue()) + " " + L("邮箱注册");
        eDao.sendEmail(ip(), nid, email, title, info, email);
        log.info(email + "注册:" + VIP_DOMAIN+"/register/emailConfirm?emailCode="+emailCode);
//		Map<String, Object> reData = new HashMap<String, Object>();
//		reData.put("userId", nid);
        json(SystemCode.code_1000,L("发送成功,请您登录邮箱激活帐号!"));
    }

    @Page(Viewer = JSON)
    public void getCountries(){
        setLan();
        CountryDao countryDao = new CountryDao();
//		List<Country> country = countryDao.find().asList();

        int version = intParam("version");

        Map<String, Object> reData = new HashMap<String, Object>();
        JSONArray ja = new JSONArray();
        AppSetting appSetting = settingDao.findOne(settingDao.getQuery());

        //if (version<appSetting.getCountryInfoVersion()) {
        Query<Country> q = countryDao.getQuery();
        long total = countryDao.count(q);
        if (total > 0) {
            List<Country> dataList = countryDao.find().asList();
            for (Country country : dataList) {
                JSONObject jo = new JSONObject();
                jo.put("id", country.getId());
//					jo.put("name", country.getName());
                jo.put("des", country.getDes());
                jo.put("code", country.getCode());

                ja.add(jo);
            }
        }
        //}
        reData.put("countries", ja);
        reData.put("version", 1);

        json(SystemCode.code_1000,reData);

    }

    @Page(Viewer = JSON)
    public void getRecommendGuide(){
        setLan();
        String userId = param("userId");
        String token = param("token");

        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }

        String recommendGuide = "";

        String realPath=request.getServletContext().getRealPath("/statics/commonTip.html");

        File file=new File(realPath);
        try {
            Document doc = Jsoup.parse(file, "UTF-8");
            Element desDiv = doc.getElementById("recommendGuide");
            doc.body().replaceWith(desDiv);
            recommendGuide = doc.toString();
        } catch (IOException e) {
            log.error("内部异常", e);
        }


        Map<String, Object> reData = new HashMap<String, Object>();

        reData.put("recommendGuide", recommendGuide);
        reData.put("recommendLink", VIP_DOMAIN+"/user/register/"+userId);

        reData.put("recommendTitle", L("邀请好友一起赚大钱"));
        reData.put("recommendContent", L("喊你的小伙伴一起来，有富同享"));
        reData.put("shareImg", STATIC_DOMAIN + "/statics/img/v2/recommend-share.png");

        json(SystemCode.code_1000,reData);

    }

    @Page(Viewer = JSON)
    public void getVersion() {
        Map<String, Object> reData = new HashMap<String, Object>();

        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd G 'at' hh:mm:ss z");

        String timeStr = "2016-08-19 14:49:00";
        reData.put("version", timeStr);
        json(SystemCode.code_1000, reData);
    }


    /**
     */
    @Page(Viewer = JSON)
    public void getRechargeBank() {
        setLan();
//		String userId = param("userId");
//		String token = param("token");
        int version = intParam("version");
        log.info("传入的Version="+version);
		/*if (!isLogin(userId, token)) {
			json(code_1003);
			return;
		}*/

//		code	Integer	是	1000	返回码
//		message	String	是	SUCCESS	说明
//		rechargeBanks	RechargeBank[]	是		充值银行数组

//		| id | Integer| 是    | |  ID  |
//		| name | String | 是    | | 名称   |
//		| tag | String | 是    | | 标签  |
//		| img | String | 是    || 图标路径   |

        List<ChinaBank> chinaBanks =ChinaBankHelp.getAllBanksHC();

        Map<String, Object> reData = new HashMap<String, Object>();
        List<Map<String, Object>> ary = new ArrayList<Map<String, Object>>();

        AppSetting appSetting = settingDao.findOne(settingDao.getQuery());
        if (appSetting!=null && version < appSetting.getRechargeBankVersion()) {
            log.info("新版rechargeBankVersion="+appSetting.getRechargeBankVersion());
//			for (ChinaBank bean : chinaBanks) {
//				bean.setImg(ChinaBank4Mobile.getChinabanks4mobile().get(bean.getTag()));
//			}

            for (ChinaBank bean : chinaBanks) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("id", bean.getId());
                item.put("name", bean.getValue());
                item.put("tag", bean.getTag());
//				item.put("img", "http://192.168.2.33:8880/ts"+bean.getImg());
                item.put("img", STATIC_DOMAIN+ChinaBank4Mobile.getChinabanks4mobile().get(bean.getTag()));

                ary.add(item);
            }
        }else{

            for (ChinaBank bean : chinaBanks) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("id", bean.getId());
                item.put("name", bean.getValue());
                item.put("tag", bean.getTag());
//				item.put("img", "http://192.168.2.33:8880/ts"+bean.getImg());
                item.put("img", STATIC_DOMAIN+ChinaBank4Mobile.getChinabanks4mobile().get(bean.getTag()));

                ary.add(item);
            }
        }
        reData.put("rechargeBanks", ary);
        reData.put("version", appSetting==null?"1":appSetting.getRechargeBankVersion());

        json(SystemCode.code_1000, reData);
    }




    private JSONObject getMarketLength(String currency) {
        JSONObject jo = new JSONObject();
        jo.put("currency", currency);
        jo.put("optional", new Integer[]{5, 10, 20, 50});
        return jo;
    }


    /**
     * 更改验证
     */
    @Page(Viewer = JSON)
    public void changeAuth() {
        try {
            setLan();
            String userId = param("userId");
            String token = param("token");

            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User loginUser = userDao.findOne("_id",userId);

            int category = intParam("category");
            int type = intParam("type");
            String safePwd = decryptRSAParam("safePwd");
            String dynamicCode = decryptRSAParam("dynamicCode");
            String fingerprint = decryptRSAParam("fingerprint");
            String googleCode = decryptRSAParam("googleCode");

            //确定需要的验证信息
            //检查哪些验证信息没有传
            //提示用户输入
            //验证用户输入
            boolean googleAuthRequired = safeStrategyNeedGoogleAuth(category, type);
            if (googleAuthRequired && loginUser.getUserContact().getGoogleAu() != AuditStatus.pass.getKey()) {
                json(SystemCode.code_1001, L("您还没开启Google认证，请开启Google认证后重试。"));
                return;
            }

            if (!changeAuthValidate(loginUser, fingerprint, safePwd, dynamicCode, dynamicCode, googleCode, category, type)) {
                return;
            }

            Message msg = null;
            switch (category) {
                case 1:        // 登录验证
                    msg = userDao.switchLoginAuthen(loginUser, type, ip(), request);
                    break;
                case 2:        // 交易验证
                    msg = userDao.switchTradeAuthen(loginUser, type, ip(), request);
                    break;
                case 3:            // 提现验证
                    msg = userDao.switchWithdrawAuthen(loginUser, type, ip(), request);
                    break;
                default:
                    json(SystemCode.code_1019);
                    return;
            }
            if (msg.isSuc()) {
                json(SystemCode.code_1000, L(msg.getMsg()));
            } else {
                json(SystemCode.code_1001, L(msg.getMsg()));
            }
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常") + e.getMessage());
        }
    }

    private boolean changeAuthValidate(User loginUser, String fingerprint, String safePwd, String mobileCode, String emailCode, String googleCode, int category, int type) throws Exception {
        String userId = loginUser.get_Id();
        UserContact uc = loginUser.getUserContact();
        int safePwdFlag = 0, mobileCodeFlag = 0, emailCodeFlag = 0, googleCodeFlag = 0;
        boolean lacksafePwdFlag = false,
                lackmobileCodeFlag = false,
                lackemailCodeFlag = false,
                lackgoogleCodeFlag = false;

        Map<String, Object> retData = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(fingerprint)) {//有传指纹过来
            String fingerCode = param("fingerCode");
            Message fingerMsg = isFingerprintCorrect(loginUser, fingerCode, fingerprint);
            if (!fingerMsg.isSuc()) {
                json(SystemCode.code_1001, fingerMsg.getMsg(), retData);
                return false;
            }
        } else {//没有传指纹
            if (category == 2) {
                if (type != TradeAuthenType.TRADE_PASSWORD.getKey()) { // 验证交易密码
                    safePwdFlag = 1;
                }
            } else {
                googleCodeFlag = uc.getGoogleAu() == 2 ? 1 : 0;
                if (uc.getMobileStatu() != AuditStatus.pass.getKey()) {
                    emailCodeFlag = 1;
                } else {
                    mobileCodeFlag = 1;
                }
            }
        }
        retData.put("needSafePwd", safePwdFlag);
        retData.put("needMobileCode", mobileCodeFlag);
        retData.put("needEmailCode", emailCodeFlag);
        retData.put("needGoogleCode", googleCodeFlag);

        Message msg = new Message();

        if (safePwdFlag == 1) {
            if (StringUtils.isBlank(safePwd)) {
                lacksafePwdFlag = true;
            } else {
                msg = userDao.safePwd(safePwd, userId, getLanTag());
                if (!msg.isSuc()) {
                    SystemCode safePwdErrorCode = SystemCode.getSystemCodeByKey(msg.getCode());
                    //2017.8.14 xzhang 修改交易验证设置错误信息提示
                    safePwdErrorCode.setValue(msg.getMsg());
                    json(safePwdErrorCode, retData);
                    return false;
                }
            }

        }
        if (googleCodeFlag == 1) {
            if (StringUtils.isBlank(googleCode)) {
                lackgoogleCodeFlag = true;
            } else {
                long gCode = CommonUtil.stringToLong(googleCode);
                userDao.setLan(lan);
                msg = userDao.isCorrect(loginUser, uc.getSecret(), gCode);
                if (!msg.isSuc()) {
                    json(SystemCode.code_1001, L(msg.getMsg()), retData);
                    return false;
                }
            }
        }
        if (mobileCodeFlag == 1) {
            if (StringUtils.isBlank(mobileCode)) {
                lackmobileCodeFlag = true;
            } else {
                // 检查短信验证码
                ClientSession clientSession = new ClientSession(ip(), uc.getSafeMobile(), lan, PostCodeType.safeAuth.getValue(), false);
                DataResponse dr = clientSession.checkCode(mobileCode);
                if (!dr.isSuc()) {
                    json(SystemCode.code_1001, L(dr.getDes()), retData);
                    return false;
                }
            }
        }
        if (emailCodeFlag == 1) {
            if (StringUtils.isBlank(emailCode)) {
                lackemailCodeFlag = true;
            } else {
                // 检查邮件验证码
                ClientSession clientSession = new ClientSession(ip(), uc.getSafeEmail(), lan, PostCodeType.safeAuth.getValue(), false);
                DataResponse dr = clientSession.checkCode(emailCode);
                if (!dr.isSuc()) {
                    json(SystemCode.code_1001, L(dr.getDes()), retData);
                    return false;
                }
            }
        }
        if (lacksafePwdFlag ||
                lackmobileCodeFlag ||
                lackemailCodeFlag ||
                lackgoogleCodeFlag) {

            String detail = L("本次操作需要您填写") + " ";

            detail += lacksafePwdFlag ? L("资金密码&") : "";
            detail += lackmobileCodeFlag ? L("短信验证码&") : "";
            detail += lackemailCodeFlag ? L("邮箱验证码&") : "";
            detail += lackgoogleCodeFlag ? L("谷歌验证码&") : "";

            detail = detail.substring(0, detail.length() - 1);
            msg.setMsg(detail);
            json(SystemCode.code_1001, L(msg.getMsg()), retData);
            return false;
        }
        return true;
    }


	/*@Page(Viewer = JSON)
	public void getProclamations(){
		setLan();
		String type = param("type");
		String title = param("keyword");
//			boolean isTop = intParam("isTop") == 1 ? true : false;
		int currentPage = intParam("page") == 0 ? 0 : intParam("page");
		int pageSize = intParam("pageSize") == 0 ? PAGE_SIZE : intParam("pageSize");

		NewsDao nd = new NewsDao();
		//modify by xwz 20170824 app只返回公告
		Query<News> query = nd.getQuery(News.class).order("-pubTime").filter("type", 1);
//			query.filter("isTop", isTop);

		//1-公告;2-新闻
//		if ("1".equals(type)) {
//			query = query.filter("type", 1);
//		} else if ("2".equals(type)) {
//			query = query.filter("type", 2);
//		} else {
//
//		}

		if(StringUtils.isNotBlank(lan)){
			query.filter("language",lan);
		}

		if(title.length()>0){
			Pattern pattern = Pattern.compile("^.*"  + title+  ".*$" ,  Pattern.CASE_INSENSITIVE);
			query.filter("title", pattern);
		}

		long total = query.countAll();
		List<News> articles = nd.findPage(query, currentPage, pageSize);
		Map<String, Object> reData = new HashMap<>();
		JSONArray ja = new JSONArray();
		if (null != articles && articles.size() > 0) {
			for (News item : articles) {
				JSONObject jo = new JSONObject();
				jo.put("id", item.getId());
				jo.put("title", item.getTitle());
//					jo.put("content", item.getContent());
				jo.put("summary", item.getDigest());
				jo.put("publishTime", item.getPubTime()==null?"":item.getPubTime().getTime());
				jo.put("type", item.getType());
				jo.put("link", Action.MAIN_DOMAIN+"/msg/details-"+item.getId());

				ja.add(jo);
			}
		}

		reData.put("articles", ja);
		reData.put("pageIndex", currentPage);
		reData.put("pageSize", pageSize);
		reData.put("totalPage", getTotalPage((int)total, pageSize));

		json(SystemCode.code_1000, reData);

	}*/

    /**
     * 设置用户推送通知配置
     */
    @Page(Viewer = JSON)
    public void setPushSettings() {
        setLan();
        String userId = param("userId");
        String token = param("token");

        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        String pushSettings = request.getParameter("settings");
        if (null == pushSettings) {
            json(SystemCode.code_1001, L("请选择配置!"));
            return;
        }
        JSONArray settingAry = JSONArray.parseArray(pushSettings);
        for (Object o : settingAry) {
            JSONObject jsonObject = (JSONObject) o;
            int type = jsonObject.getIntValue("type");
            int isOpen = jsonObject.getIntValue("isOpen");
            String sound = jsonObject.getString("sound");

            Query<PushSetting> query = pushSettingDao.getQuery();
            query.filter("userId", userId);
            query.filter("type", type);

            QueryResults<PushSetting> results = pushSettingDao.find(query);

            long count = results.countAll();
            if (count > 0) {
                pushSettingDao.deleteByQuery(query);
            }

            PushSetting entity = new PushSetting();

            entity.setCreatetime(now());

            entity.setIsOpen(isOpen);
            if (StringUtils.isNotBlank(sound))
                entity.setSound(sound);
            entity.setUserId(userId);
            entity.setType(type);

            String nid = pushSettingDao.save(entity).getId().toString();

        }
        json(SystemCode.code_1000);
    }

    /**
     * 获取用户推送通知配置
     *
     */
    @Page(Viewer = JSON)
    public void getPushSettings() {
        setLan();
        String userId = param("userId");
        String token = param("token");

        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        List<PushSetting> settings = pushSettingDao.getList(userId);

        JSONArray settingsJsongAry = new JSONArray();
        for (PushSetting e : settings) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", e.getType() + "");
            jsonObject.put("isOpen", e.getIsOpen());
            jsonObject.put("sound", e.getSound());
            settingsJsongAry.add(jsonObject);
        }

        Map<String, Object> reData = new HashMap<String, Object>();
        reData.put("settings", settingsJsongAry);
        json(SystemCode.code_1000, reData);
    }

    public String decryptRSAParam(String paramName) throws Exception {
        String encryptValue = param(paramName);
        String decryptValue = "";
        if (StringUtils.isNotBlank(encryptValue)) {
            decryptValue = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(encryptValue),priKey ));
        }
        return decryptValue;
    }
//	@Page(Viewer = JSON)
//	public void getPublicKey() {
//		String key = getPubKey();
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("publicKey", key);
//		json(SystemCode.code_1000, map);
//	}


    /**
     * 4.38 提交初级实名认证
     *
     * Close By suxinjie 一期屏蔽该功能
     *
     * @since 2.1
     */
    //@Page(Viewer = JSON)
    public void simpleIdentityAuth() {
        setLan();
        String userId = param("userId");
        String token = param("token");
        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }

        try {
            User user = new UserDao().getUserById(userId);
            Message msg = AuthUtil.saveSimpleIndividualAuth(user, lan, request, ip());
            if (msg.isSuc()) {
                Map<String, Object> datas = new HashedMap();
                Authentication authentication = new AuthenticationDao().getByUserId(userId);
                datas.put("status", authentication.getStatus());

                json(SystemCode.code_1000, msg.getMsg(), datas);
            } else {
                json(SystemCode.code_1001, msg.getMsg());
            }

        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("初级认证出错，请稍后重试"));
        }
    }

    /**
     * 4.40 获取实名认证状态
     */
    @Page(Viewer = JSON)
    public void getIdentityAuthStatus() {
        setLan();
        String userId = param("userId");
        String token = param("token");

        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        Authentication au = auDao.getByUserId(userId);
        Map<String, Object> reData = new HashMap<String, Object>();
        if (null == au) {
            reData.put("status", AuditStatus.a1NoSubmit.getKey());
            au = new Authentication();
        } else {
            reData.put("status", au.getStatus());
        }
        reData.put("statusShow", EnumUtils.getEnumByKey(au.getStatus(), AuditStatus.class).getValue());

        reData.put("fileBasePath", FileConfig.getValue("imgDomain1")+"/picauth?file=");
//			reData.put("fileBasePath", "http://192.168.4.23:8980/fs/picauth?file=");
        reData.put("realName", StringUtils.isBlank(au.getRealName()) ? "" : au.getRealName());
        reData.put("failReason", StringUtils.isBlank(au.getReason()) ? "" : au.getReason());
        reData.put("frontalImg", StringUtils.isBlank(au.getFrontalImg()) ? "" : au.getFrontalImg());
        reData.put("backImg", StringUtils.isBlank(au.getBackImg()) ? "" : au.getBackImg());
        reData.put("loadImg", StringUtils.isBlank(au.getLoadImg()) ? "" : au.getLoadImg());
        reData.put("proofAddressImg", StringUtils.isBlank(au.getProofAddressImg()) ? "" : au.getProofAddressImg());
        reData.put("bankCardId", StringUtils.isBlank(au.getBankCard()) ? "" : au.getBankCard());
        reData.put("bankTel", StringUtils.isBlank(au.getBankTel()) ? "" : au.getBankTel());
        reData.put("bankCardType", StringUtils.isBlank(au.getBankCardType()) ? "" : au.getBankCardType());
        reData.put("cardId", StringUtils.isBlank(au.getCardId()) ? "" : au.getCardId());
        reData.put("country", StringUtils.isBlank(au.getCountryCode()) ? "" : au.getCountryCode());
        reData.put("detailStatus", new Integer[]{au.getInfoPass(), au.getIdImgPass(), au.getBankPass(), au.getProofAddressPass()});
        reData.put("bankId", au.getBankCardType());

        json(SystemCode.code_1000, reData);

    }

    /**
     * 4.39 提交高级实名认证
     */
    @Page(Viewer = JSON)
    public void depthIdentityAuth() {
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");

            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }

            String ip = ip();

            depthIdentityAuthSince2_1(userId, ip);

        } catch (Exception e) {
            log.error("内部异常", e);
            json(code_1002, L("内部异常") + e.getMessage());
        }
    }

    private void depthIdentityAuthSince2_1(String userId, String ip) {
        String frontalImg = param("frontalImg");        // 身份证正面照
        String backImg = param("backImg");              // 身份证背面照
        String loadImg = param("loadImg");              // 手持身份证照
        String bankCardId = param("bankCardId");        // 银行卡号
        String bankTel = param("bankTel");              // 银行预留手机号
        String bankId = param("bankId");                // 银行id
        String proofAddressImg = param("proofAddressImg");  // 住址证明照

	        /*String realName = param("realName");
	        String cardId = param("cardId").toLowerCase();
	        String country = param("country");*/
        int type = intParam("type");//|操作类型 1：保存2：提交审核|

        User user = userDao.getUserById(userId);
        Authentication au = auDao.getByUserId(userId);

        IdcardDao idcardDao = new IdcardDao();
        boolean isPass = false, isDalu = au.isDaluUser();
        if (isDalu) {
            isPass = idcardDao.validIdcard(au.getRealName(), au.getCardId());
        } else {
            isPass = true;
        }
        if (!isPass && isDalu) {
            json(code_1001, AuthUtil.getMsgAndRecordFailTimes(
                    userDao, user, lan, "证件信息验证不通过，请填写真实证件信息后重新提交认证", true));
            return;
        }

        IdcardBlackListDao idcardBlackListDao = new IdcardBlackListDao();
        boolean isIdcardBlackList = idcardBlackListDao.isBlackList(au.getCardId());
        au.setCardIdBlackList(isIdcardBlackList);
        au.setSimplePass(true);

        if (type == 1 || type == 2) { // 保存或提交数据

            if(type == 2) { //提交审核
                if (StringUtils.isBlank(frontalImg)) {
                    json(code_1001, L("请上传证件正面照"));
                    return;
                }
                if (StringUtils.isBlank(backImg)) {
                    json(code_1001, L("请上传证件背面照"));
                    return;
                }
                if (StringUtils.isBlank(loadImg)) {
                    json(code_1001, L("请上传手持证件照"));
                    return;
                }

                if (au.isDaluUser()) { // 大陆用户银行卡信息认证
                    if (StringUtils.isBlank(bankCardId)) {
                        json(code_1001, L("请填写有效的银行卡号"));
                        return;
                    }

                    if (StringUtils.isBlank(bankTel)) {
                        json(code_1001, L("请填写有效的银行预留手机号"));
                        return;
                    }
                } else { // 非大陆用户，提供住址证明
                    if (StringUtils.isBlank(proofAddressImg)) {
                        json(code_1001, L("请上传住址证明"));
                        return;
                    }
                }

                au.setStatus(AuditStatus.noAudite.getKey());
            }

            au.setFrontalImg(frontalImg);
            au.setBackImg(backImg);
            au.setLoadImg(loadImg);

            au.setProofAddressImg(proofAddressImg);

            au.setBankCard(bankCardId);
            au.setBankTel(bankTel);
            au.setBankId(bankId);
        } else {
            json(code_1001, L("未知操作类型!"));
            return;
        }

        au.setBankCardType(StringUtils.isBlank(bankId) ? "1" : bankId);
        au.setIp(ip);
        au.setSubmitTime(now());
        au.setUserId(userId);
        au.setServiceStatu(3);// 比对一致
        au.setImgCode("");
        au.setPhoto("");

        if (!auDao.updateAuth(au).getHadError()) {
            Map<String, Object> datas = new HashedMap();
            datas.put("status", au.getStatus());
            json(code_1000, L("资料提交成功，请耐心等待审核。"), datas);
        } else {
            json(code_1001, L("保存失败。"));
        }
    }

    public int getArea(String country) {
        return AuthUtil.getArea(country);
    }


    // ============== 新增接口 ==============

    UserVipLevelDao userVipLevelDao = new UserVipLevelDao();
    IntegralRuleDao integralRuleDao = new IntegralRuleDao();
    JifenDao jifenDao = new JifenDao();

    /**
     * 获取当前用户等级
     *
     */
    @Page(Viewer = JSON)
    public void getUserLevel() {
        setLan();
        String userId = param("userId");
        String token = param("token");

        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }

        Map<String, Object> map = new HashMap<>();

        User u = userDao.findOne("_id",userId);
        List<UserVipLevel> userVipList =  userVipLevelDao.getList();

        // 等级更新的操作
        double totalJifen = u.getTotalJifen();
        VipRate oldVip = (VipRate)EnumUtils.getEnumByKey(u.getVipRate(), VipRate.class);
        if (totalJifen < 0) {
            totalJifen = 0;
            log.error("\n ==== 用户" + u.getUserName() + "的总积分为负数,请检查  ====");
        }
        if(oldVip==null){
            oldVip = VipRate.vip0;
        }

        BigDecimal feeDiscount = userVipLevelDao.getDiscountByVipRate(oldVip.getId());
        //List<UserVipLevel> userVipList =  userVipLevelDao.getList();
        VipRate newVip = userVipLevelDao.getVipRateByJiFen(BigDecimal.valueOf(totalJifen));
        if (newVip != oldVip) {
            // 需要upgrade
            feeDiscount = userVipLevelDao.getDiscountByVipRate(newVip.getId());
            u.setVipRate(newVip.getId());
            UpdateResults<User> ur = userDao.updateUserVipRate(u);
            if (ur.getUpdatedCount() > 0) {
                // 更新内存里,cookie里的vip等级
                SSOLoginManager.updateVip(u.getId() + "", newVip.getId());

            }
        }
        //缓存用户的手续费折扣率  为计算手续费准备
        Cache.SetObj("user_vip_fee_discount_"+u.getId(), feeDiscount);

        int currentRate = u.getVipRate();
        double currentPoints = u.getTotalJifen();
        boolean isFull = false;
        double currentRateBeginPoint = 0;
        double nextRateBeginPoint = 0;
        int nextRate;

        if (currentRate >= 10) {
            isFull = true;
            nextRate = 10;
            currentRate = 9;
        } else {
            isFull = false;
            nextRate = currentRate + 1;
        }

        for (UserVipLevel level : userVipList) {
            if (level.getVipRate() == currentRate) {
                currentRateBeginPoint = level.getJifen();
            }
            if (level.getVipRate() == nextRate) {
                nextRateBeginPoint = level.getJifen();
            }
        }

        map.put("currentRate", currentRate);
        map.put("currentPoints", currentPoints);
        map.put("nextRateBeginPoint", nextRateBeginPoint);
        map.put("currentRateBeginPoint", currentRateBeginPoint);
        map.put("nextRate", nextRate);
        map.put("isFull", isFull);

        json(SystemCode.code_1000, map);
    }

    /**
     * 获取积分规则和等级描述
     *
     */
    @Page(Viewer = JSON)
    public void getVipInfoAndntegralRule() {
        setLan();
        String userId = param("userId");
        String token = param("token");

        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }

        Map<String, Object> map = new HashMap<>();

        List<UserVipLevel> userVipList =  userVipLevelDao.getList();
        if (CollectionUtils.isNotEmpty(userVipList)) {
            map.put("userVipLevelList", userVipList);
        }
		/*start by xzhang 20170918 积分规则展示处理*/
        try {
            //2017.08.11 xzhang 修改获取积分描述信息国际化问题
            IntegralRule integralRule = null;
            List<IntegralRule> integralRuleList = integralRuleDao.getList();
            if (CollectionUtils.isNotEmpty(integralRuleList)) {
                for (IntegralRule rule : integralRuleList) {
                    if ("日常交易".equals(rule.getType()) || "账户留存资金".equals(rule.getType()) || "日常充值".equals(rule.getType())) {
                        rule.setRule(String.valueOf((long) (Double.parseDouble(rule.getRule()) * 100)));
                    }
                    rule.setMemo(L(rule.getMemo()));
                    rule.setType(L(rule.getType()));
                    if (L("后台操作积分").equals(rule.getType())) {
                        integralRule = rule;
                    }
                }
                if(integralRule != null){
                    integralRuleList.remove(integralRule);
                }
                map.put("integralRuleList", integralRuleList);
            }
        }catch (Exception e){
            //为防止数据库脚本异常
            log.error("【积分规则】用户："+userId+"积分展示异常，异常信息为："+e);
        }
		/*end*/
        json(SystemCode.code_1000, map);
    }

    /**
     * 获取积分详情
     */
    @Page(Viewer = JSON)
    public void getRateDetail() {
        setLan();
        String userId = param("userId");
        String token = param("token");
        int currentPage = intParam("page");
        int pageSize = intParam("pageSize");

        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }

        Map<String, Object> map = new HashMap<>();
        pageSize = pageSize == 0 ? PAGE_SIZE : pageSize;

        com.world.data.mysql.Query<Jifen> query = jifenDao.getQuery();
        query.setSql("select ioType, addTime, type, jifen, memo from jifen where userId = '" + userId + "' and type != 30 order by id desc");
        query.setCls(Jifen.class);
        List<Jifen> list = jifenDao.findPage(currentPage, pageSize);
        List<JifenVO> result = new ArrayList<>();
        //2017.08.11 xzhang 修改积分明细列表中的积分类型国际化问题
        Map<String,String> ruleMap = integralRuleDao.getRuleMap();
        //FIXME 将memo处理为英文,支持国际化后这里需要更改
        for (Jifen jifen : list) {

            String typeShow = ruleMap.get(jifen.getType() + "");
            if(StringUtil.exist(typeShow)){
                jifen.setTypeShowNew(L("积分规则-" + typeShow));
            }else{
                jifen.setTypeShowNew(L("积分留存"));
            }
            if (jifen.getIoType()==0) {
                jifen.setMemo("Add credit");
            } else {
                jifen.setMemo("Deduct credit");
            }

            String type_en = jifen.getTypeValue("en");
            jifen.setTypeValue(type_en);

            JifenVO vo = new JifenVO();
            vo.setAddTime(jifen.getAddTime());
            vo.setIoType(jifen.getIoType());
            vo.setMemo(jifen.getMemo());
            vo.setTypeValue(jifen.getTypeShowNew());
            vo.setJifen(jifen.getJifen());
            result.add(vo);
        }

        map.put("details", result);
        json(SystemCode.code_1000, map);
    }

    /**
     * 获取用户未读公告-首页(一期显示一条)
     *
     * 1.查询最新公告ID
     * 2.获取缓存中的用户公告ID
     * 3.比较,如果未读则返回给app,并标记为已读
     */
    @Page(Viewer = JSON)
    public void getUserUnReadNoticeOne() {
        setLan();
        String userId = userIdStr();

        NewsDao nd = new NewsDao();
        Query<News> query = nd.getQuery(News.class);
        Date time = new Date();

        query.order("-pubTime").filter("pubTime <", time).filter("type", 1).limit(1);
        if(StringUtils.isNotBlank(lan)){
            query.filter("language",lan);
        }

        News notice = nd.findOne(query);
        String lantmp = lan.equalsIgnoreCase("cn") ? "" : lan.toLowerCase();
        String cacheNoticeId = Cache.Get("user_notice_" + lantmp + userId);
        String cacheNoticeIpId = Cache.Get("user_notice_ip_" + lantmp + userId);
        //登录
        if(!"0".equals(userId)){
            if (notice != null && (cacheNoticeId == null || !Integer.valueOf(cacheNoticeId).equals(Integer.valueOf(notice.getId())))) {

                Map<String, Object> result = getUnNotice("user_notice_" + lantmp + userId,readNoticeKey,userId,notice);

//				Cache.Set("user_notice_" + lantmp + userId, notice.getId());
//				String readNoticeIds = Cache.Get(readNoticeKey+userId);
//				if(StringUtils.isNotEmpty(readNoticeIds)){
//					String[] readNoticeIdsAttr = readNoticeIds.split(",");
//					List list = Arrays.asList(readNoticeIdsAttr);
//					if(!list.contains(notice.getId())){
//						Cache.append(readNoticeKey+userId,","+notice.getId());
//					}
//				}else{
//					Cache.Set(readNoticeKey+userId,notice.getId());
//				}
//				Map<String, Object> result = new HashMap<>();
//				result.put("id", notice.getId());
//				result.put("title", notice.getTitle());
//				result.put("content", notice.getContent());
//				result.put("summary", notice.getDigest());
//				result.put("publishTime", notice.getPubTime()==null?"":notice.getPubTime().getTime());

                json(SystemCode.code_1000, result);
            }else{
                json(SystemCode.code_1000);
            }
            //未登录
        }else{
            if (notice != null && (cacheNoticeIpId == null || !Integer.valueOf(cacheNoticeIpId).equals(Integer.valueOf(notice.getId())))) {
                Map<String, Object> result = getUnNotice("user_notice_" + lantmp + userId,readNoticeIpKey,ip(),notice);
                json(SystemCode.code_1000, result);
            }else{
                json(SystemCode.code_1000);
            }
        }

    }

    public Map<String, Object> getUnNotice(String key,String readKey,String userId,News notice){
        Cache.Set(key, notice.getId());
        String readNoticeIds = Cache.Get(readKey+userId);
        Map<String, Object> result = null;
        if(StringUtils.isNotEmpty(readNoticeIds)){
            String[] readNoticeIdsAttr = readNoticeIds.split(",");
            List list = Arrays.asList(readNoticeIdsAttr);
            if(!list.contains(notice.getId())){
                Cache.append(readKey+userId,","+notice.getId());
                result = new HashMap<>();
                result.put("id", notice.getId());
                result.put("title", notice.getTitle());
                result.put("content", notice.getContent());
                result.put("summary", notice.getDigest());
                result.put("publishTime", notice.getPubTime()==null?"":notice.getPubTime().getTime());
            }
        }else{
            Cache.Set(readKey+userId,notice.getId());
        }


        return result;
    }

    /**
     * 用户读取公告
     *
     * 判断公告是否是最新的,更新缓存(用户ID-公告ID)
     */
    @Page(Viewer = JSON)
    public void doReadNotice() {
        setLan();
        String userId = userIdStr();
        String token = param("token");
        String noticeId = param("noticeId");
        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        if(StringUtils.isBlank(noticeId)){
            NewsDao nd = new NewsDao();
            Query<News> query = nd.getQuery(News.class);
            Date time = new Date();

            query.filter("type", 1).filter("pubTime <", time);
            if(StringUtils.isNotBlank(lan)){
                query.filter("language",lan);
            }
            query.order("-pubTime").limit(1);
            News notice = nd.findOne(query);
            if(notice != null){
                noticeId = notice.getId();
            }
        }
        String lantmp = lan.equalsIgnoreCase("cn") ? "" : lan.toLowerCase();
        String cacheNoticeId = Cache.Get("user_notice_" + lantmp + userId);
        if (!StringUtil.exist(cacheNoticeId) || !noticeId.equals(cacheNoticeId)) {
            Cache.Set("user_notice_" + lantmp + userId, noticeId);
        }
        json(SystemCode.code_1000);

    }

    /**
     * 获取用户持有的币种
     * @return
     */
    @Page(Viewer = JSON)
    public void getUserHasCoin() {
        String userId = userIdStr();
        String token = param("token");
        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        List list = new ArrayList();
        com.alibaba.fastjson.JSONArray funds = UserCache.getUserFunds(userId);
        for (int i = 0; i < funds.size(); i++) {
            com.alibaba.fastjson.JSONObject obj = funds.getJSONObject(i);
            BigDecimal balance = obj.getBigDecimal("balance");
            if(balance.compareTo(BigDecimal.ZERO) > 0){
                if(!"usdt".equals(obj.getString("propTag").toLowerCase())){

                    list.add(obj.getString("propTag"));
                }
            }
        }
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("data",list);
        json(SystemCode.code_1000, "",JSONObject.toJSONString(retMap));
    }

    /**
     * 公告列表
     */
    @Page(Viewer = JSON)
    public void getProclamationsList(){
        //设置语言
        setLan();
        //页码
        int currentPage = intParam("page") == 0 ? 0 : intParam("page");
        //分页条数
        int pageSize = intParam("pageSize") == 0 ? PAGE_SIZE : intParam("pageSize");

        NewsDao nd = new NewsDao();
        //降序
//		com.google.code.morphia.query.Query<News> query = nd.getQuery(News.class).order("-pubTime").filter("type", 1);
        com.google.code.morphia.query.Query<News> query = nd.getQuery(News.class).filter("type", 1).filter("pubTime <=", new Date());;

        if(StringUtils.isNotBlank(lan)){
            query.filter("language",lan);
        }
        //总数
        long total = query.countAll();
        if(total > 0){
            query.order("-isTop,-pubTime");
        }
        //执行sql
        List<News> articles = nd.findPage(query, currentPage, pageSize);
        Map<String, Object> reData = new HashMap<>();
        Set<String> list = getReadNoticeSet(userIdStr());
        JSONArray ja = new JSONArray();
        if (null != articles && articles.size() > 0) {
            for (News item : articles) {
                JSONObject jo = new JSONObject();
                if(list.contains(StringUtils.isNotEmpty(item.getBaseId()) ? item.getBaseId() : item.getId())){
                    jo.put("read", true);
                }else{
                    jo.put("read", false);
                }
                jo.put("id", item.getId());
                jo.put("title", item.getTitle());
                jo.put("summary", item.getDigest());
                jo.put("publishTime", item.getPubTime()==null?"":item.getPubTime().getTime());
                jo.put("type", item.getType());
                jo.put("link", Action.MAIN_DOMAIN+"/msg/details-"+item.getId());
                ja.add(jo);
            }
        }
//		else{
//			json(SystemCode.code_1001,"内部错误" );
//			return;
//		}
        reData.put("list", ja);
        reData.put("pageIndex", currentPage);
        reData.put("pageSize", pageSize);
        reData.put("totalPage", getTotalPage((int)total, pageSize));

        json(SystemCode.code_1000,"操作成功" ,reData);

    }

    /**
     * 获取用户已读列表
     * @return
     */
    public static Set<String> getReadNoticeSet(String userId) {
        String readNoticeIds = Cache.Get(readNoticeKey+ userId);
        if(StringUtils.isNotEmpty(readNoticeIds)) {
            return new HashSet<>(Arrays.asList(readNoticeIds.split(",")));
        }
        return new HashSet<>();
    }

    //获取公告详情(json格式)
    public void doCheckNotice() {
        //设置语言
        setLan();
        try {
            String id = param("id");
            NewsDao nd = new NewsDao();
            News news = new News();
            if (id.length() > 0) {
                news = nd.getById(id,"_id","title","content","pubTime","source","sourceLink","type","digest","keyword","photo");
                if(null == news){
                    json(SystemCode.code_1001, L("公告已结束"));
                    return;
                }else{
                    json(SystemCode.code_1000);
                    return;
                }

            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
            //json(L("获取失败！") , false , "" , true)
            json(SystemCode.code_1001,L("获取失败！"));
        }
    }
    //获取公告详情(json格式)
    public void getNewsdetails() {
        //设置语言
        setLan();
        String userId = userIdStr();
        try {
            String id = param("id");
            NewsDao nd = new NewsDao();
            News news = new News();
            if (id.length() > 0) {
                news = nd.getById(id);
                if(null == news){
                    json(SystemCode.code_1001, L("公告已结束"));
                    return;
                }
                dealPubTimeShow(news);
                //登录时变为已读
                if(!"0".equals(userId)){
                    doReadNoticeOne(userId,StringUtils.isNotEmpty(news.getBaseId()) ? news.getBaseId() : news.getId());
                }
            }
            setAttr("curData", news);
//            json(SystemCode.code_1000, "操作成功","/admins/api/noticeDetail.jsp");
        } catch (Exception ex) {
            log.error("内部异常", ex);
            //json(L("获取失败！") , false , "" , true)
//			json(SystemCode.code_1001,L("获取失败！"));
        }
    }
    @Page(Viewer = JSON)
    public void getSafeModelType() {
        //设置语言
        setLan();
        String userId = userIdStr();
        User u = userDao.findOne("_id",userId);
        /**提现地址验证类型 0 1 初级模式 2 安全模式*/
        json(SystemCode.code_1000, "操作成功",u.getWithdrawAddressAuthenType());
    }
    private void dealPubTimeShow(News news){
        //创建时间
        String pubTimeStr = null;
        //发布时间
        Timestamp pubTime = news.getPubTime();
        long timeDiff = System.currentTimeMillis() - pubTime.getTime();
        //与当前时间之差
        if(timeDiff > 24*60*60*1000 ||timeDiff < 0){
            String text = "MM-dd-yyyy HH:mm";
            if(lan.equals("cn") || lan.equals("hk") || lan.equals("tw")){
                text = "yyyy-MM-dd HH:mm";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(text);

            pubTimeStr = sdf.format(pubTime);
        }else{
            pubTimeStr = millisConvertTime(timeDiff);
        }
        news.setPubTimeStr(pubTimeStr);
    }

    //毫秒数转成时间
    private String millisConvertTime(long time) {
        String hourDes = L("小时");
        String minDes = L("分钟");
        long hours = (time % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (time % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (time % (1000 * 60 * 60 * 60)) / (1000);
        if(hours == 0 && minutes == 0){
            return L("现在");
        }else{
            return (hours == 0 ? "" : (hours + " " + hourDes)) + " "  +
                    (minutes == 0 ? "" : (minutes + " " + minDes)) + " "+
                    L("前");
        }
    }
    /**
     * 读取公告
     */
    @Page(Viewer = JSON)
    public void doReadNoticeOne(String userId,String noticeId){
//		setLan();
//		String userId = userIdStr();
//		String token = param("token");
//		String noticeId = param("noticeId");
//		if (!isLogin(userId, token)) {
//			json(SystemCode.code_1003);
//			return;
//		}
//		if(StringUtils.isEmpty(noticeId)){
//			json(SystemCode.code_3005);
//			return;
//		}
        String readNoticeIds = Cache.Get(readNoticeKey+userId);
        if(StringUtils.isNotEmpty(readNoticeIds)){
            String[] readNoticeIdsAttr = readNoticeIds.split(",");
            List list = Arrays.asList(readNoticeIdsAttr);
            if(!list.contains(noticeId)){
                Cache.append(readNoticeKey+userId,","+noticeId);
            }
        }else{
            Cache.Set(readNoticeKey+userId,noticeId);
        }
    }

    /**
     * 读取公告-全部
     */
    @Page(Viewer = JSON)
    public void doReadNoticeAll(){
        setLan();
        String userId = userIdStr();
        String token = param("token");
        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        NewsDao nd = new NewsDao();
        com.google.code.morphia.query.Query<News> query = nd.getQuery(News.class).order("-pubTime").filter("type", 1);

        if(StringUtils.isNotBlank(lan)){
            query.filter("language",lan);
        }

        QueryResults<News> qr = nd.find(query);
        List<News> newsList = qr.asList();
        Set<String> readNotices = getReadNoticeSet(userIdStr());
        for(News news : newsList){
            String id = StringUtils.isNotEmpty(news.getBaseId()) ? news.getBaseId() : news.getId();
            readNotices.add(id);
        }
        if (readNotices.size() > 0) {
            Cache.Set(readNoticeKey+userId, String.join(",", readNotices));
        }

        json(SystemCode.code_1000);
        return;
    }
    /**
     * 是否需要交易密码
     */
    @Page(Viewer = JSON)
    public void doIsNeedSafePwd() {
        setLan();
        String userId = userIdStr();
        String token = param("token");
        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        User user = userDao.findOne("_id",userId);
        JSONObject json = new JSONObject();
        Boolean isNeedSafePwd = userDao.isNeedSafePwd(user);
        json.put("isNeedSafePwd",isNeedSafePwd);
        if(StringUtils.isNotEmpty(user.getSafePwd())){
            json.put("isHasSafePwd",true);
        }else{
            json.put("isHasSafePwd",false);
        }
        json(SystemCode.code_1000,SystemCode.code_1000.getValue(), json.toString());
    }

    /**
     * 设置价格预警
     */
    @Page(Viewer = JSON)
    public void setMarketReminds() {
        setLan();
        String userId = param("userId");
        String token = param("token");

        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }

        String currency = param("currency");
        String exchange = param("exchange");
        double price = doubleParam("price");
        double currencyPrice = doubleParam("currencyPrice");

        //简单校验参数合法性,不能为空
        if(!StringUtil.exist(currency)
                || !StringUtil.exist(exchange)
                || currencyPrice < 0 ){
            json(SystemCode.code_1001);
            return;
        }

        //价格不能为空
        if(price <= 0){
            json(SystemCode.code_1027);
            return;
        }

        Map<String, CoinProps> types = DatabasesUtil.getCoinPropMaps();
        if (!types.containsKey(currency.toLowerCase())) {
            json(SystemCode.code_1009);
            return;
        }

        Query<MarketRemind> query1 = marketRemindDao.getQuery();
        query1.filter("userId", userId);
        List<MarketRemind> list = query1.asList();
        if (list.size() >= 10) {
            json(SystemCode.code_1001, "提醒数量不能超过10个");// TODO: 2017/4/27 国际化
            return ;
        }

        Query<MarketRemind> query = marketRemindDao.getQuery();
        query
                .filter("userId", userId)
                .filter("currency", currency.toLowerCase())
                .filter("exchange", exchange.toLowerCase())
                .filter("price", BigDecimal.valueOf(price).toPlainString())
                .filter("currencyPrice", BigDecimal.valueOf(currencyPrice).toPlainString());

        QueryResults<MarketRemind> results = marketRemindDao.find(query);
        long count = results.countAll();
        if (count > 0) {
            marketRemindDao.deleteByQuery(query);
        }

        MarketRemind entity = new MarketRemind();

        entity.setPrice(BigDecimal.valueOf(price).toPlainString());
        entity.setCurrency(currency.toLowerCase());
        entity.setExchange(exchange.toLowerCase());
        entity.setUserId(userId);
        entity.setCurrencyPrice(BigDecimal.valueOf(currencyPrice).toPlainString());

        String nid = marketRemindDao.save(entity).getId().toString();
        Map<String, Object> map = new HashMap<>();
        map.put("id", nid);

        json(SystemCode.code_1000, map);
    }

    /**
     * 获取价格预警
     */
    @Page(Viewer = JSON)
    public void getMarketReminds() {
        setLan();
        String userId = param("userId");
        String token = param("token");

        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }

        List<MarketRemind> reminds = marketRemindDao.getList(userId);

        JSONArray marketReminds = new JSONArray();
        for (MarketRemind e : reminds) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", e.getId());
            jsonObject.put("price",e.getPrice());
            jsonObject.put("currency", e.getCurrency());
            jsonObject.put("exchange", e.getExchange());

            marketReminds.add(jsonObject);
        }

        Map<String, Object> reData = new HashMap<>();
        reData.put("marketReminds", marketReminds);
        log.info("当前价格提醒设置:" + reData);
        json(SystemCode.code_1000, reData);
    }

    /**
     * 取消一条价格预警
     */
    @Page(Viewer = JSON)
    public void delMarketReminds() {
        setLan();
        String userId = param("userId");
        String token = param("token");

        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }

        String id = param("id");

        boolean exist = marketRemindDao.existRemindByUid(userId, id);
        if (exist) {
            marketRemindDao.deleteMarketRemind(id);
            json(SystemCode.code_1000);
            return;
        }

        json(SystemCode.code_1001, "预警信息不存在");// TODO: 2017/5/16 国际化

    }

    /**
     * 获取其他网站的价格信息
     */
    @Page(Viewer = JSON)
    public void cointPriceFromOther() {
        setLan();
        String userId = param("userId");
        String token = param("token");

        String symbol = param("symbol");
        /** start 20170901 xzhang  新增前端传递货币折算币种*/
        String legal_Tender = param("legal_tender");
        String legalTender = "usd_$";
        String legalTenderParam = "";
        if(StringUtils.isNotBlank(legal_Tender)){
            legal_Tender = legal_Tender.toUpperCase();
            if (!LegalTenderType.existKey(legal_Tender)) {
                json(SystemCode.code_1001, "不支持的货币类型");
                return;
            }
            LegalTenderType legalTenderType = LegalTenderType.valueOf(legal_Tender);
            legalTenderParam = legalTenderType.getKey() + "_" + legalTenderType.getValue();
        }
        /**end **/
        List<Object> list = new ArrayList<>();

        if (!StringUtil.exist(symbol)) {
            symbol = "ltc_btc";
        } else {
            symbol = symbol.toLowerCase();
        }

        if (symbol.equals("gbc_btc")||symbol.equals("gbc_usdt")) { //gbc没有外网价格，直接返回
            json(SystemCode.code_1000, list);
            return;
        }
        log.info("symbol=" + symbol);
        /** start 20170901 xzhang  兼容IOS折算币种老版本，优先取传递，其次去缓存。最后去默认**/
        if(StringUtil.exist(legalTenderParam)){
            legalTender = legalTenderParam;
        }else if (StringUtil.exist(userId)) {
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            legalTender = Cache.Get("user_legal_tender_" + userId); // --> usd_$
            if (!StringUtil.exist(legalTender)) {
                legalTender =  "usd_$";
            }
        }
        /**end **/

        String btcLegalPrice = Cache.Get("btc_" + legalTender.split("_")[0].toLowerCase());
        if(symbol.endsWith("_usdt")){
            if("usd".equals(legalTender.split("_")[0].toLowerCase())){
                btcLegalPrice = "1";
            }else{
                btcLegalPrice = Cache.Get("usdt_" + legalTender.split("_")[0].toLowerCase());
            }
        }
        if (!StringUtil.exist(btcLegalPrice)) {
            json(SystemCode.code_1001, list);
            return;
        }

        // bitfinex
        String bitfinexMarkets = Cache.Get("market_price_bitfinex");
        if (StringUtil.exist(bitfinexMarkets)) {
            JSONObject jsonObject = JSONObject.parseObject(bitfinexMarkets);
            if (null != jsonObject.getJSONObject(symbol)) {
                Map<String, String> bitfinex = new HashMap<>();
                bitfinex.put("from", "Bitfinex");
                bitfinex.put("last_price", jsonObject.getJSONObject(symbol).getString("last"));
                bitfinex.put("last_price_legal_tender", legalTender.split("_")[1] + " " + DigitalUtil.roundDown(new BigDecimal(jsonObject.getJSONObject(symbol).getString("last")).multiply(new BigDecimal(btcLegalPrice)).doubleValue(), 2));
                bitfinex.put("vol", String.valueOf(DigitalUtil.roundDown(Double.parseDouble(jsonObject.getJSONObject(symbol).getString("quoteVolume")), 2)));

                list.add(bitfinex);
            }
        }

        // poloniex
        String poloniexMarkets = Cache.Get("market_price_poloniex");
        if (StringUtil.exist(poloniexMarkets)) {
            JSONObject jsonObject = JSONObject.parseObject(poloniexMarkets);
            if (null != jsonObject.getJSONObject(symbol)) {
                Map<String, String> poloniex = new HashMap<>();
                poloniex.put("from", "Poloniex");
                poloniex.put("last_price", jsonObject.getJSONObject(symbol).getString("last"));
                poloniex.put("last_price_legal_tender", legalTender.split("_")[1] + " " + DigitalUtil.roundDown(new BigDecimal(jsonObject.getJSONObject(symbol).getString("last")).multiply(new BigDecimal(btcLegalPrice)).doubleValue(), 2));
                poloniex.put("vol", String.valueOf(DigitalUtil.roundDown(Double.parseDouble(jsonObject.getJSONObject(symbol).getString("quoteVolume")), 2)));

                list.add(poloniex);
            }
        }
		/*start by xzhang 20170904 JYPT-1275 新增okcoin，Huobi和Binance交易市场行情*/
        // OkCoin
        String OkCoinMarkets = Cache.Get("market_price_OkCoin");
        if (StringUtil.exist(OkCoinMarkets)) {
            JSONObject jsonObject = JSONObject.parseObject(OkCoinMarkets);
            if (null != jsonObject.getJSONObject(symbol)) {
                Map<String, String> OkCoin = new HashMap<>();
                OkCoin.put("from", "OKEX");
                OkCoin.put("last_price", jsonObject.getJSONObject(symbol).getString("last"));
                OkCoin.put("last_price_legal_tender", legalTender.split("_")[1] + " " + DigitalUtil.roundDown(new BigDecimal(jsonObject.getJSONObject(symbol).getString("last")).multiply(new BigDecimal(btcLegalPrice)).doubleValue(), 2));
                OkCoin.put("vol", String.valueOf(DigitalUtil.roundDown(Double.parseDouble(jsonObject.getJSONObject(symbol).getString("quoteVolume")), 2)));

                list.add(OkCoin);
            }
        }
        //Huobi
        String HuobiMarkets = Cache.Get("market_price_Huobi");
        if (StringUtil.exist(HuobiMarkets)) {
            JSONObject jsonObject = JSONObject.parseObject(HuobiMarkets);
            if (null != jsonObject.getJSONObject(symbol)) {
                Map<String, String> Huobi = new HashMap<>();
                Huobi.put("from", "Huobi");
                Huobi.put("last_price", jsonObject.getJSONObject(symbol).getString("last"));
                Huobi.put("last_price_legal_tender", legalTender.split("_")[1] + " " + DigitalUtil.roundDown(new BigDecimal(jsonObject.getJSONObject(symbol).getString("last")).multiply(new BigDecimal(btcLegalPrice)).doubleValue(), 2));
                Huobi.put("vol", String.valueOf(DigitalUtil.roundDown(Double.parseDouble(jsonObject.getJSONObject(symbol).getString("quoteVolume")), 2)));
                list.add(Huobi);
            }
        }
        //bter
        String bterMarkets = Cache.Get("market_price_bter");
        if (StringUtil.exist(bterMarkets)) {
            JSONObject jsonObject = JSONObject.parseObject(bterMarkets);
            if (null != jsonObject.getJSONObject(symbol)) {
                Map<String, String> bter = new HashMap<>();
                bter.put("from", "Bter");
                bter.put("last_price", jsonObject.getJSONObject(symbol).getString("last"));
                bter.put("last_price_legal_tender", legalTender.split("_")[1] + " " + DigitalUtil.roundDown(new BigDecimal(jsonObject.getJSONObject(symbol).getString("last")).multiply(new BigDecimal(btcLegalPrice)).doubleValue(), 2));
                bter.put("vol", String.valueOf(DigitalUtil.roundDown(Double.parseDouble(jsonObject.getJSONObject(symbol).getString("quoteVolume")), 2)));

                list.add(bter);
            }
        }

        //Binance
        String binanceMarkets = Cache.Get("market_price_Binance");
        if (StringUtil.exist(binanceMarkets)) {
            JSONObject jsonObject = JSONObject.parseObject(binanceMarkets);
            if (null != jsonObject.getJSONObject(symbol)) {
                Map<String, String> binance = new HashMap<>();
                binance.put("from", "Binance");
                binance.put("last_price", jsonObject.getJSONObject(symbol).getString("last"));
                binance.put("last_price_legal_tender", legalTender.split("_")[1] + " " + DigitalUtil.roundDown(new BigDecimal(jsonObject.getJSONObject(symbol).getString("last")).multiply(new BigDecimal(btcLegalPrice)).doubleValue(), 2));
                binance.put("vol", String.valueOf(DigitalUtil.roundDown(Double.parseDouble(jsonObject.getJSONObject(symbol).getString("quoteVolume")), 2)));
                list.add(binance);
            }
        }
        /**end*/
        //Bitfinex,Poloniex,OkCoin,Huobi,Bter,Binance
        log.info("同步其他行情数据详情："+list.toString());
        json(SystemCode.code_1000, list);
    }

    /**
     * 获取支持的折算货币
     */
    @Page(Viewer = JSON)
    public void getSupportLegalTender() {
        setLan();
        String userId = param("userId");
		/*start by xzhang 20170901 屏蔽登录状态校验
//		String token = param("token");
//
//		if (!isLogin(userId, token)) {
//			json(SystemCode.code_1003);
//			return;
//		}
        /**end*/
        Map<String, Object> result = new HashMap<>();
        result.put("legal_tender", LegalTenderType.getkeys());

        String defaultCurrency = Cache.Get("user_legal_tender_" + userId);
        if (!StringUtil.exist(defaultCurrency)) {
            defaultCurrency = "USD";
        } else {
            defaultCurrency = defaultCurrency.split("_")[0];
        }

        result.put("default", defaultCurrency);

        json(SystemCode.code_1000, result);
    }

    /**
     * 设置折算货币
     */
    @Page(Viewer = JSON)
    public void setLegalTender() {
        setLan();
        String userId = param("userId");
        String token = param("token");

        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }

        String legalTender = param("legal_tender");
        if (!LegalTenderType.existKey(legalTender)) {
            json(SystemCode.code_1001, "不支持的货币类型"); // TODO: 2017/7/7 添加国际化信息
            return;
        }

        userDao.updateUserLegalTender(userId, LegalTenderType.valueOf(legalTender));

        json(SystemCode.code_1000);
    }

    /**
     * 设置用户语言
     */
    @Page(Viewer = JSON)
    public void setLanguage() {
        String userId = param("userId");
        String token = param("token");

        if (isLogin(userId, token) != SystemCode.code_1003 && isLogin(userId, token) != SystemCode.code_402) {
            try{
                String lan = param("lan").toLowerCase();
                if(!lan.equals("en") && !lan.equals("hk") && !lan.equals("tw") && !lan.equals("es")){
                    lan = "cn";
                }
                userDao.updateUserLanguage(userId,lan);
            }catch (Exception e){
                log.error("app设置语言失败，userId：" + userId);
                json(SystemCode.code_1001);
                return;
            }
        }
//		if (isLogin(userId, token)) {
//			try{
//				String lan = param("lan").toLowerCase();
//				if(!lan.equals("en") && !lan.equals("hk") && !lan.equals("tw") && !lan.equals("es")){
//					lan = "cn";
//				}
//				userDao.updateUserLanguage(userId,lan);
//			}catch (Exception e){
//				log.error("app设置语言失败，userId：" + userId);
//				json(SystemCode.code_1001);
//				return;
//			}
//		}
        json(SystemCode.code_1000);
    }

    /**
     * 委托时验证密码
     //	 * @param pass
     //	 * @param userId
     * @return
     */
    @Page(Viewer = JSON)
    public void doSafePwdForEnturst() {
        setLan();
        String payPass = param("payPass");
        try {
//			payPass = URLDecoder.decode(payPass, "UTF-8");
            payPass = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(payPass), priKey));
        } catch (Exception e) {
            log.info("资金密码转义出错" + payPass);
        }
        String userId = userIdStr();
        User user = userDao.findOne("_id",userId);
        if (null == user) {
            json(SystemCode.code_3004);
            return;
        }
        //判断资金密码是否正确
        int status = new UserDao().checkSecurityPwdForEntrust(payPass, userId);
        SafeTipsTag stt = getLanTag().getStt();
//
        int payLoginLock = doGetErrorTimes(userId,LimitType.PayLoginPassError);
        int payEmailLock = doGetErrorTimes(userId,LimitType.PayEmailPassError);
        int payMobileLock = doGetErrorTimes(userId,LimitType.PayMobilePassError);
        int payGoogleLock = doGetErrorTimes(userId,LimitType.PayGooglePassError);
        int withdrawPayPwdLock = doGetErrorTimes(userId,LimitType.WithdrawPayPwdPassError);

        Map<String, Object> result = new HashMap<>();
        result.put("ashLockStatus", 0);
        if((payLoginLock == -2) || (payEmailLock == -2)
                || (payMobileLock == -2) || (payGoogleLock == -2)
                || (withdrawPayPwdLock == -2)){
            result.put("ashLockStatus", 1);
        }

        if (user.getSafePwdModifyTimes() > 1 && null != user.getSafePwdModifyTime()
                && TimeUtil.getOriginDiffDay(now(), user.getSafePwdModifyTime()) < 1) {
            result.put("ashLockStatus", 1);
        }
        if (status == -2) {
            result.put("ashLockStatus", 1);
            String suoDing = L("资金密码输入次数超出限制，将锁定提现、修改资金密码、设置收款方式功能，请24小时之后再试");
            json(SystemCode.code_1001, suoDing,JSONObject.toJSONString(result));
            return;
        } else if (status == -1) {
            json(SystemCode.code_1001, stt.getCuoWu(),JSONObject.toJSONString(result));
            return;
        } else if (status == 0) {
            json(SystemCode.code_1001, stt.getWeiSheZhi(),JSONObject.toJSONString(result));
            return;
        } else if(status == -3){
            json(SystemCode.code_1000, L(SystemCode.code_1000.getValue()),JSONObject.toJSONString(result));
            return;
        } else{
            String jihui = L("资金密码输入有误。");
            json(SystemCode.code_1001,jihui,JSONObject.toJSONString(result));
            return;
        }
		/*end*/
    }

    /**
     * 获取用户资金信息
     */
    @Page(Viewer = JSON)
    public void getPayUserInfo() {
        setLan();
        String userId = userIdStr();
        String token = param("token");
        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        User user = userDao.findOne("_id",userId);
        if (null == user) {
            json(SystemCode.code_3004);
            return;
        }
        String coinName = param("coinName");
        CoinProps coinProps = DatabasesUtil.coinPropsByName(coinName);
        if(null == coinProps){
            json(SystemCode.code_1001,L("找不到此币种! "));
            return;
        }
        BigDecimal balance = payUserDao.getBalance(Integer.parseInt(userId),coinProps.getFundsType());
        Map<String,Object> map = new HashMap<>();
        map.put("balance", balance);
        json(SystemCode.code_1000, "",map);
    }

    /**
     * 获取推荐币
     */
    @Page(Viewer = JSON)
    public void getRecommendCoin(){
        RecommendCoinDao recommendCoinDao = new RecommendCoinDao();
        List<RecommendCoin> list = recommendCoinDao.getRecommendCoinList();
        Map<String, Object> map = new HashMap<>();
        map.put("data",list);
        json(SystemCode.code_1000,"" ,map);
    }

    /**
     * 16.交易时调用是否开启交易设置
     */
    @Page(Viewer = JSON)
    public void isTransSafe(){
        try {
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.findOne("_id",userId);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            boolean isTransSafe = userDao.isNeedSafePwd(user);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("isTransSafe", isTransSafe+"");
            json(SystemCode.code_1000,"" , JSONObject.toJSONString(map));
        } catch (Exception e) {
            log.error("内部异常", e);
            json("true", true, "", true);
        }
    }

    /**
     * 市场信息
     */
    @Page(Viewer = JSON)
    public void getMarket() {
        String market = param("market");

        if(!StringUtil.exist(market)){
            json(SystemCode.code_1001, L("请求参数为空"));
            return;
        }
        String userId = userIdStr();
        market = market.replace("/","_").toLowerCase();
        com.alibaba.fastjson.JSONObject marketInfo = Market.getMarketByName(market);
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        if(null != marketInfo){
            BigDecimal buyFeeRate = BigDecimal.ZERO;
            BigDecimal sellFeeRate = BigDecimal.ZERO;
            BigDecimal feeDiscount = BigDecimal.ZERO;
            User user = userDao.findOne("_id",userId);
            if(null != user){
                Object feeDiscountObj = Cache.GetObj("user_vip_fee_discount_"+userId);
                if(feeDiscountObj == null){
                    UserVipLevelDao userVipLevelDao = new UserVipLevelDao();
                    UserVipLevel userVipLevel = userVipLevelDao.getByVipRate(user.getVipRate());
                    if(null != userVipLevel){
                        feeDiscount = new BigDecimal(String.valueOf(userVipLevel.getDiscount()));
                    }
                }else{
                    feeDiscount = (BigDecimal) feeDiscountObj;
                }
                buyFeeRate = new BigDecimal(marketInfo.getDouble("takerFeeRate").toString()).multiply(feeDiscount);
                sellFeeRate = new BigDecimal(marketInfo.getDouble("makerFeeRate").toString()).multiply(feeDiscount);
                jsonObject.put("buyFeeRate", buyFeeRate.toPlainString());
                jsonObject.put("sellFeeRate", sellFeeRate.toPlainString());
            }else{
                jsonObject.put("buyFeeRate", marketInfo.getDouble("takerFeeRate").toString());
                jsonObject.put("sellFeeRate", marketInfo.getDouble("makerFeeRate").toString());
            }
        }
        json(SystemCode.code_1000, "",jsonObject);

    }
    /**
     * 获取币种配置信息.web
     */
    @Page(Viewer = JSON)
    public void getCoin() {
        JSONArray result = new JSONArray();
        try {
            Map<String, CoinProps> coinMap = DatabasesUtil.getCoinPropMaps();
            for (Map.Entry<String, CoinProps> entry : coinMap.entrySet()) {
                JSONObject coinJson = new JSONObject();
                coinJson.put("name", entry.getValue().getPropTag());
                coinJson.put("code", entry.getValue().getFundsType());
                result.add(coinJson);
            }
        } catch (Exception e) {
            logger.error("获取币种配置信息异常", e);
        }
        json(SystemCode.code_1000, "",result.toString());
    }

    /**
     * 币种介绍
     */
    @Page(Viewer = JSON)
    public void getCoinInfo() {
        setLan();
        JSONObject result = new JSONObject();
        String coinName = param("coinName");
        try {
            CoinInfo coinInfo = new CoinInfo();
            coinInfo.setCoinNameJson(coinName);
            coinInfo.setInternationalization(lan);
            if (StringUtils.isBlank(coinInfo.getCoinNameJson()) ||
                    StringUtils.isBlank(coinInfo.getInternationalization())) {
                json(SystemCode.code_1001,L("参数非法！ "));
            }
            log.info("币种名："+coinInfo.getCoinNameJson()+",国际化："+coinInfo.getInternationalization());
            CoinInfoService coinInfoService = new CoinInfoService();
            CoinInfo coin = coinInfoService.getInfoCoin(coinInfo);
            if (coin == null) {
//				json(SystemCode.code_1001,L("未获取到对应币种信息！"));
                json(SystemCode.code_1000);
                return;
            }
            result = (JSONObject) JSONObject.toJSON(coin);
        } catch (Exception e) {
            logger.error("获取币种介绍信息异常！coin：" + coinName, e);
        }

        json(SystemCode.code_1000, "",result.toString());
    }

    /**
     * 删除提现地址
     */
    @Page(Viewer = JSON)
    public void doAddressDel() {
        try {
            setLan();
            long id = longParam("receiveId");
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            int count = Data.Update("update " + coint.getStag() + CointTable.receiveaddr + " set isdeleted = 1 where id=? and userid=?", new Object[]{id, userIdStr()});
            if (count > 0) {
                json(SystemCode.code_1000);
            } else {
                json(SystemCode.code_1001);
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    /**
     * 推送驾驶舱浏览首页埋点
     */
    @Page(Viewer = JSON)
    public void doBrowseHome(){

        String cockpit = param("deviceId");
        //市场名
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cookie",cockpit);
        jsonObject.put("ip",ip());
        jsonObject.put("entrustmarket","");
        jsonObject.put("userid","");
        jsonObject.put("browsetime",System.currentTimeMillis());
        ProducerSend producerSend = new ProducerSend();
        producerSend.sendMessage("browsehome", jsonObject.toString());
        log.info("推送驾驶舱浏览首页埋点成功："+jsonObject.toString());
        json("ok", true, "");
    }
    /**
     * 推送驾驶舱浏览交易页埋点
     */
    @Page(Viewer = JSON)
    public void doBrowseTrade(){

        String cockpit = param("deviceId");
        //市场名
        String market = param("market");
        String userId = userIdStr();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cookie",cockpit);
        jsonObject.put("entrustmarket",market);
        jsonObject.put("userid",userId);
        jsonObject.put("ip",ip());
        jsonObject.put("browsetime",System.currentTimeMillis());
        ProducerSend producerSend = new ProducerSend();
        producerSend.sendMessage("browse", jsonObject.toString());
        ProducerSend producerSend1 = new ProducerSend();
        producerSend1.sendMessage("browsehome", jsonObject.toString());
        log.info("推送驾驶舱浏览交易页埋点成功："+jsonObject.toString());
        json("ok", true, "");
    }

    /**
     * 设置语言类型
     */
    @Page(Viewer = JSON)
    public void doSetLan() {
        setLan();
        String lan = this.lan;
        if (!lan.equals("en") && !lan.equals("hk") && !lan.equals("tw") && !lan.equals("es") && !lan.equals("jp") && !lan.equals("kr")) {
            lan = "cn";
        }
        String userId = userIdStr();
        try {
            if (StringUtils.isNotBlank(userId)) {
                userDao.updateUserLanguage(userId, lan);
                Cache.Set("user_lan_" + userId, lan);
            }
        } catch (Exception e) {
            log.error("修改用户语言出错userId:" + userId + ",lan" + lan, e);
        }
        json(SystemCode.code_1000);
    }

    /**
     * 产品&超级节点信息
     */
    @Page(Viewer = JSON)
    public void doProductSuperNode() {
        try {
            setLan();
            /**
             * 获取基础信息，直接从Redis获取
             * sNodeNum:超级主节点数量
             * sNodeTotalProfit:产出总量
             * currentBlock/profitBlock:当前区块/分红区块
             */
            String sNodeNum = RedisUtil.get("financial_sNodeNum");
            if (StringUtils.isEmpty(sNodeNum)) {
                sNodeNum = "0";
                log.info("理财报警WARN，报警信息：超级主节点数量获取异常，获取值 = " + sNodeNum);
            }
            /*存放时，存放整数*/
            String sNodeTotalProfit = RedisUtil.get("financial_sNodeTotalProfit");
            if (StringUtils.isEmpty(sNodeTotalProfit)) {
                sNodeTotalProfit = "0";
                log.info("理财报警WARN，报警信息：超级主节点收益获取异常，获取值 = " + sNodeTotalProfit);
            }
            String currentBlock = RedisUtil.get("financial_currentBlock");
            if (StringUtils.isEmpty(currentBlock)) {
                currentBlock = "0";
                log.info("理财报警WARN，报警信息：VDS当前区块高度获取异常，获取值 = " + currentBlock);
            }
            String profitBlock = RedisUtil.get("financial_profitBlock");
            if (StringUtils.isEmpty(profitBlock)) {
                profitBlock = "0";
                log.info("理财报警WARN，报警信息：VDS分红区块高度获取异常，获取值 = " + profitBlock);
            }
            /**
             * 获取基础信息
             * profitWeight:分红权重
             * proTotalAmount:投资总额		在Redis中设置超时时间为10秒，获取不到从数据库读取
             * proTotalUser:投资人数		在Redis中设置超时时间为10秒，获取不到从数据库读取
             */
            String profitWeight = "0";
            String proTotalUser = RedisUtil.get("financial_proTotalUser");
            String proTotalAmount = RedisUtil.get("financial_proTotalAmount");
            if (StringUtils.isEmpty(proTotalUser)) {
                proTotalUser = "0";
            }
            if (StringUtils.isEmpty(proTotalAmount)) {
                proTotalAmount = "0";
            }
            BigDecimal bdProTotalAmount = BigDecimal.ZERO;
            if (!StringUtils.isEmpty(proTotalAmount)) {
                try {
                    bdProTotalAmount = BigDecimal.valueOf(Long.valueOf(proTotalAmount));
                } catch (Exception e) {
                    bdProTotalAmount = BigDecimal.ZERO;
                }
            }
            log.info("sNodeNum = " + sNodeNum + ", sNodeTotalProfit = " + sNodeTotalProfit);
            log.info("currentBlock = " + currentBlock + ", profitBlock = " + profitBlock);
            log.info("proTotalAmount = " + proTotalAmount + ", proTotalUser = " + proTotalUser);
//            String sql = "";
            /*总投资人数，总投资金额*/
            /*总分红权重，人数*100*/
            String profitWeightTotal = RedisUtil.get("financial_profitWeightTotal");
            log.info("profitWeightTotal = " + profitWeightTotal);
            if (StringUtils.isEmpty(profitWeightTotal)) {
                profitWeightTotal = "0";
            }

            /*分红倒计时 xx日yy时zz分 */
            String bonusSurplusTime = L("分红倒计时");
            long currentTime = System.currentTimeMillis();
            long bonusTime = 1565208000000L;
            String strBonusTime = "";
            strBonusTime = RedisUtil.get("financial_bonusTime");
            try {
                bonusTime = Long.valueOf(strBonusTime);
            } catch (Exception e) {
                bonusTime = 1565208000000L;
            }
            log.info("bonusTime = " + bonusTime);
            long surplusTime = bonusTime - currentTime;
            /*计算XX天XX时XX分*/
            String bonusDays = "00";
            String bonusHours = "00";
            String bonusMinute = "00";
            try {
                if (surplusTime / (24 * 3600 * 1000) < 0) {
                    bonusDays = "00";
                } else {
                    bonusDays = surplusTime / (24 * 3600 * 1000) + "";
                }
                if ((surplusTime - (24 * 3600 * 1000 * Integer.valueOf(bonusDays))) / (3600 * 1000) < 0) {
                    bonusHours = "00";
                } else {
                    bonusHours = (surplusTime - (24 * 3600 * 1000 * Integer.valueOf(bonusDays))) / (3600 * 1000) + "";
                }
                if ((surplusTime - (24 * 3600 * 1000 * Integer.valueOf(bonusDays)) - (3600 * 1000 * Integer.valueOf(bonusHours))) / (60 * 1000) < 0) {
                    bonusMinute = "00";
                } else {
                    bonusMinute = (surplusTime - (24 * 3600 * 1000 * Integer.valueOf(bonusDays)) - (3600 * 1000 * Integer.valueOf(bonusHours))) / (60 * 1000) + "";
                }
// 	 	 		bonusSurplusTime = bonusDays + "xx" + bonusHours + "yy" + bonusMinute + "zz";
                if (bonusDays.length() == 1) {
                    bonusDays = "0" + bonusDays;
                }
                if (bonusHours.length() == 1) {
                    bonusHours = "0" + bonusHours;
                }
                if (bonusMinute.length() == 1) {
                    bonusMinute = "0" + bonusMinute;
                }
            } catch (Exception e) {

            }
            bonusSurplusTime = bonusSurplusTime.replaceAll("xx", bonusDays);
            bonusSurplusTime = bonusSurplusTime.replaceAll("yy", bonusHours);
            bonusSurplusTime = bonusSurplusTime.replaceAll("zz", bonusMinute);

            /*VDS生态回馈*/
            String vdsEcologyBack = "0";
            try {
                vdsEcologyBack = (bdProTotalAmount.multiply(BigDecimal.valueOf(0.05))).setScale(0, BigDecimal.ROUND_DOWN) + "";
            } catch (Exception e) {
                vdsEcologyBack = "0";
            }
            /*拼装返回值*/
            Map<String, Object> result = new HashMap<>();
            result.put("profitWeight", profitWeight);
            result.put("proTotalAmount", proTotalAmount);
            result.put("proTotalUser", proTotalUser);
            result.put("sNodeNum", sNodeNum);
            result.put("sNodeTotalProfit", sNodeTotalProfit);
            result.put("currentBlock", currentBlock);
            result.put("profitBlock", profitBlock);
            result.put("profitWeightTotal", profitWeightTotal);
            /*限时返利，剩余时间*/
            String surplusHour = "0";
            result.put("surplusHour", surplusHour);
            /*拼装返回值*/
            result.put("vdsEcologyBack", vdsEcologyBack);
            /*分红倒计时*/
            result.put("bonusSurplusTime", L(bonusSurplusTime));
            log.info("result = " + JSONObject.toJSONString(result));
            /*接口返回*/
            json(SystemCode.code_1000, "", JSONObject.toJSONString(result));
        } catch (Exception e) {
            json(SystemCode.code_1001);
            log.error("LCBJ-APP理财报警ERROR:productSuperNode", e);
        }
    }

    @Page(Viewer = JSON)
    public void doUserFinancialInfo() {
        try {
            /*调用用户信息*/
            setLan();
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }

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
            json(SystemCode.code_1000, "", JSONObject.toJSONString(result));
        } catch (Exception e) {
            json(SystemCode.code_1001);
            log.error("LCBJ-APP理财报警ERROR:userFinancialInfo", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Page(Viewer = JSON)
    public void doUserProductInfoSave() {
        try {
            setLan();
            /**
             * userVID	vid地址
             * pInvitationCode	推荐人邀请码
             */
            /*调用用户信息*/
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.getUserById(userId);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            String userName = user.getUserName();

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
                json(SystemCode.code_1001, L(uvidCR));
                return;
            }
            if (StringUtils.isEmpty(pInvitationCode)) {
                picCR = "请输入邀请码";
                json(SystemCode.code_1001, L(picCR));
                return;
            } else if (pInvitationCode.length() > 8) {
                picCR = "邀请码输入错误";
                json(SystemCode.code_1001, L(picCR));
                return;
            }

            /*校验邀请码是否合法以及是否存在,先校验长度*/
            if (pInvitationCode.length() != 8) {
                picCR = "邀请码不正确，请核对后再输入";
                json(SystemCode.code_1001, L(picCR));
                return;
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
                    json(SystemCode.code_1001, L(picCR));
                    return;
                }
            } else {
                picCR = "邀请码不正确，请核对后再输入";
                json(SystemCode.code_1001, L(picCR));
                return;
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
                    json(SystemCode.code_1001, L(uvidCR));
                    return;
                } else if ("500".equals(jsonResultCode)) {
                    /*系统繁忙，请稍后再试*/
                    uvidCR = "系统繁忙，请稍后再试";
                    json(SystemCode.code_1001, L(uvidCR));
                    log.info("理财报警ERROR:调用vid校验接口异常");
                    return;
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
                        json(SystemCode.code_1001, L(uvidCR));
                        return;
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
                    json(SystemCode.code_1001, L(uvidCR));
                    return;
                }
            }
            log.info("uvidCR = " + uvidCR + ", picCR = " + picCR);

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
                json(SystemCode.code_1000, L("成功"));
            } else {
                json(SystemCode.code_1001, L("失败"));
            }
        } catch (Exception e) {
            json(SystemCode.code_1001);
            log.error("LCBJ-APP理财报警ERROR:userProductInfoSave", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Page(Viewer = JSON)
    public void doUserProductInfoPay() {
        try {
            setLan();
            /*调用用户信息*/
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.getUserById(userId);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            String userName = user.getUserName();

            /*投资金额对应的矩阵等级*/
            String targetMatrixLevel = param("matrixLevel");
            String sql = "";
            /*获取vds_usdt实时价格*/
            String vdsUsdtPrice = Cache.Get("vds_usdt_l_price");
            log.info("vdsUsdtPrice = " + vdsUsdtPrice);
            if (StringUtils.isEmpty(vdsUsdtPrice)) {
                json(SystemCode.code_1001, L("系统繁忙，请稍后再试"));
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
                json(SystemCode.code_1001, L("系统繁忙，请稍后再试"));
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
                json(SystemCode.code_1001, L("该产品理财投资已结束"));
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
                json(SystemCode.code_1001, L("非法输入，请确认后输入"));
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
                json(SystemCode.code_1001, L("您投资的收益产品暂未开启，请选择投资188产品"));
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
                json(SystemCode.code_1001, L("该产品理财投资已支付成功，请确认投资金额"));
                log.info("理财报警WARN:该产品理财投资已支付成功，不能重复支付" + userId);
                return;
            } else if ("0".equals(authPayFlag)) {
                /*支付失败，请先确认vid和推荐人邀请码已保存！*/
                json(SystemCode.code_1001, L("支付失败，请先确认VID和推荐人邀请码已保存"));
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
                json(SystemCode.code_1001, L("理财账户初始化失败，请联系客服"));
                log.info("理财报警ERROR:理财账户初始化失败，请联系客服" + userId);
                return;
            } else {
                if (null != listPayUserFinancial.get(0)) {
                    userBalance = listPayUserFinancial.get(0);
                }
                log.info("userBalance = " + userBalance);
                if (userBalance.compareTo(difLevelAmount) < 0) {
                    json(SystemCode.code_1001, L("理财账户资金不足，请先进行充值或者划转"));
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
                    json(SystemCode.code_1001, L("理财账户增投信息初始化失败，请联系客服"));
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
                json(SystemCode.code_1001, L("支付失败"));
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
                json(SystemCode.code_1001, L("支付失败"));
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
                json(SystemCode.code_1000, resultMsg);
            } else {
                json(SystemCode.code_1000, L("支付成功"));
            }
        } catch (Exception e) {
            json(SystemCode.code_1001, L("支付失败"));
            log.info("理财报警ERROR:userProductInfoPay", e);
        }



    }

    @Page(Viewer = JSON)
    public void getBonusType() {
        setLan();
        JSONArray array = new JSONArray();
        Map<String,String> bonusMap = new HashMap<>();
        for (BonusEnum bonusEnum : BonusEnum.values()) {
            bonusMap.put(String.valueOf(bonusEnum.getKey()),L(bonusEnum.getValue()));
        }
        for (Map.Entry<String, String> entry : bonusMap.entrySet()) {
            //bonusMap.put(String.valueOf(bonusEnum.getKey()),L(bonusEnum.getValue()));
            com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
            json.put("name", entry.getValue());
            json.put("type", entry.getKey());
            array.fluentAdd(json);
        }
        json(SystemCode.code_1000, "", array);
    }


    @Page(Viewer = JSON)
    public void doUserAvaTransferAmount() {

        try {
            setLan();
            /*调用用户信息*/
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            /*调用用户信息*/
            User user = userDao.getUserById(userId);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            String userName = user.getUserName();

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
                json(SystemCode.code_1001, L("非法输入，请确认后输入"));
                log.info("理财报警ERROR:划转金额转换失败, userId = " + userId + ", avaTransferAmount = " + avaTransferAmount);
                return;
            }

            /**
             * 资金密码判断
             */
            if (StringUtils.isEmpty(user.getSafePwd())) {
                json(SystemCode.code_1001, L("请先设置资金密码"));
                return;
            }
//            RsaUser rsaUser = RsaLoginUtil.getRsaUser(this);
            byte[] decodedData2 = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(capitalPwd), priKey);
            capitalPwd = new String(decodedData2);
            if (!new UserDao().checkSecurityPwdNoLock(capitalPwd, user.get_Id())) {
                log.info("理财报警WARN:划转金额转换密码输入错误, userId = " + userId);
                json(SystemCode.code_1001, L("资金密码输入有误。"));
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
                json(SystemCode.code_1001, L("理财账户初始化失败，请联系客服"));
                log.info("理财报警ERROR:理财账户初始化失败，请联系客服" + userId);
                return;
            } else {
                if (null != listPayUserFinancial.get(0)) {
                    userProfitAmount = listPayUserFinancial.get(0);
                } else {
                    json(SystemCode.code_1001, L("待划转资金不足，请确认后划转"));
                    log.info("理财报警WARN:划转资金不足, userId = " + userId + ", userProfitAmount = " + userProfitAmount + ", bdAvaTransferAmount = " + bdAvaTransferAmount);
                    return;
                }
                log.info("userProfitAmount = " + userProfitAmount);
                if (userProfitAmount.compareTo(bdAvaTransferAmount) < 0 || bdAvaTransferAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    json(SystemCode.code_1001, L("待划转资金不足，请确认后划转"));
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
                        + "51, '子账号划出', '" + user.getUserName() + "', " + vdsUsdtPrice + ", "
                        + "0, 0, " + user.get_Id() + " "
                        + "from pay_user_financial where userid = " + userId + " and fundstype = 51 for update";
                log.info("userAvaTransferAmount sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_main"));

                /*主账号接收*/
                /*主账号 更新pay_user_financial	理财资金表 balance*/
                sql = "update pay_user_financial set balance = balance + " + balanceAmount + " "
                        + "where userid = " + user.get_Id() + " and fundstype = 51 ";
                log.info("userProductInfoPay pay_user_financial sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_main"));
                /*主账号 插入bill_financial 流水类型 5303 子账号划入 */
                sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
                        + "remark, vdsUsdtPrice, investProPeriod, matrixLevel, businessId) "
                        + "select " + user.get_Id() + ", '" + user.getUserName() + "', 5303, " + balanceAmount + ", "
                        + "" + currentTime + ", (balance + profit + insureInvestFreezeAmount), "
                        + "51, '子账号划入', '" + userName + "', " + vdsUsdtPrice + ", "
                        + "0, 0, " + userId + " "
                        + "from pay_user_financial where userid = " + user.get_Id() + " and fundstype = 51 for update";
                log.info("userAvaTransferAmount sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_main"));

                /*保存到划转表*/
                sql = "insert into fin_usersontransfer (sonUserId, sonUserName, fundsType, avaTransferAmount, douProfitAmount, "
                        + "transferType, transferName, parentUserId, parentUserName, createTime) "
                        + "values (" + userId + ", '" + userName + "', '51', " + avaTransferAmount + ", " + douProfitAmount + ", "
                        + "1, '释放', " + user.get_Id() + ", '" + user.getUserName() + "', now() )";
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
                json(SystemCode.code_1001, L("释放失败"));
                return;
            }
            /*保存成功*/
            log.info("理财报警INFO:用户资金释放成功 = " + userId + ", 可释放资金 = " + userProfitAmount + ", 本次释放资金 = " + bdAvaTransferAmount);
            json(SystemCode.code_1000, L("释放成功"));
        } catch (Exception e) {
            json(SystemCode.code_1001, L("释放失败"));
            log.info("理财报警ERROR:userAvaTransferAmount", e);
        }

    }

    @Page(Viewer = JSON)
    public void getBonusList() {

        try {
            setLan();
            /*调用用户信息*/
            String userId = userIdStr();
            log.info("bonus列表userId" + userId);
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            String mark = "回馈VID地址：";
            int currentPage = intParam("pageIndex");
            if (currentPage <= 0) {
                currentPage = 1;
            }

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
                com.world.data.mysql.Query<FinancialBonus> queryTotal = financialBonusDao.getQuery();
                queryTotal.setSql(sql);
                queryTotal.setDatabase("vdsapollo");
                queryTotal.setCls(FinancialBonus.class);
                queryTotal.setParams(new Object[]{bonusType});
                FinancialBonus financialBonusSum = queryTotal.getOne();
                sum_bonus_usdt_amount = financialBonusSum.getSum_bonus_usdt_amount();
                sum_bonus_vds_amount = financialBonusSum.getSum_bonus_vds_amount();
                com.world.data.mysql.Query<FinancialBonus> query = financialBonusDao.getQuery();
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
                com.world.data.mysql.Query<FinancialBonus> queryDetails = financialBonusDao.getQuery();
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

                com.world.data.mysql.Query<FinancialBonus> queryDetails = financialBonusDao.getQuery();
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
                com.world.data.mysql.Query<FinancialBonus> queryDetails = financialBonusDao.getQuery();
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
                com.world.data.mysql.Query queryTotal = dao.getQuery();
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
                    Map<String, Object> page = new HashMap<String, Object>();
                    page.put("pageIndex", currentPage);
                    page.put("totalCount", total);
                    page.put("totalWeight", financial_profitWeightTotal);
                    page.put("sum_bonus_usdt_amount", cumulative.toString());
                    log.info("sum_bonus_usdt_amount的值tostring:" + sum_bonus_usdt_amount.toString());
                    log.info("sum_bonus_usdt_amount的值:" + sum_bonus_usdt_amount);
                    page.put("sum_bonus_vds_amount", sum_bonus_vds_amount.toString());
                    page.put("list", list);
                    json(SystemCode.code_1000, "", JSONObject.toJSONString(page));
                    return;
                }

            }else if(BonusEnum.getType(bonusType) == 6){
                String sql = "";
                sql = "select id,createTime,sonUserName,concat(avaTransferAmount,'VOLLAR') as avaTransferAmount,concat(douProfitAmount,'VOLLAR') as douProfitAmount,transferType,parentUserName,concat(avaTransferAmount-douProfitAmount,'VOLLAR') " +
                        "as AttributionNum from fin_usersontransfer where fundsType=51 and parentUserId=" + userId;

                if (userSonRelationFlag) {
                    /*是子账号，切换数据处理逻辑*/
                    userId = sonUserId;
                    sonUserName = sonUserName;
                    sql = "select createTime,sonUserName,concat(avaTransferAmount,'VOLLAR') as avaTransferAmount,concat(douProfitAmount,'VOLLAR') as douProfitAmount,transferType,parentUserName,concat(avaTransferAmount-douProfitAmount,'VOLLAR') " +
                            "as AttributionNum from fin_usersontransfer where fundsType=51 and sonUserId=" + userId;

                }
                FinUsersontransferDao dao = new FinUsersontransferDao();
                com.world.data.mysql.Query queryTotal = dao.getQuery();
                queryTotal.setSql(sql);
                queryTotal.setDatabase("vip_financial");
                queryTotal.setCls(FinUsersontransfer.class);
                total = queryTotal.count();
                List<FinUsersontransfer> list = queryTotal.getPageList(currentPage, 30);  Map<String, Object> page = new HashMap<String, Object>();
                page.put("pageIndex", currentPage);
                page.put("totalCount", total);
                page.put("totalWeight", financial_profitWeightTotal);
                page.put("sum_bonus_usdt_amount", sum_bonus_usdt_amount.toString());
                log.info("sum_bonus_usdt_amount的值tostring:" + sum_bonus_usdt_amount.toString());
                log.info("sum_bonus_usdt_amount的值:" + sum_bonus_usdt_amount);
                page.put("sum_bonus_vds_amount", sum_bonus_vds_amount.toString());
                page.put("list", list);
                json(SystemCode.code_1000, "", JSONObject.toJSONString(page));
                return;
            }
            Map<String, Object> page = new HashMap<String, Object>();
            page.put("pageIndex", currentPage);
            page.put("totalCount", total);
            page.put("totalWeight", financial_profitWeightTotal);
            page.put("sum_bonus_usdt_amount", sum_bonus_usdt_amount.toString());
            log.info("sum_bonus_usdt_amount的值tostring:" + sum_bonus_usdt_amount.toString());
            log.info("sum_bonus_usdt_amount的值:" + sum_bonus_usdt_amount);
            page.put("sum_bonus_vds_amount", sum_bonus_vds_amount.toString());
            page.put("list", financialBonusList);
            json(SystemCode.code_1000, "", JSONObject.toJSONString(page));
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
        json(SystemCode.code_1000, "", JSONObject.toJSONString(page));
        log.info("bonus列表" + JSONObject.toJSONString(page));
    }

    @SuppressWarnings("unchecked")
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


    @Page(Viewer = JSON)
    public void doUserInvitationChart() {
        try {
            setLan();
            /*调用用户信息*/
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.findOne("_id", userId);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            /*调用用户信息*/
            String userName = user.getUserName();
            String urlFinancial = ApiConfig.getValue("urlfinancial.url");
            urlFinancial += "/getOrgChart?username=" + userName;
            log.info("urlFinancial = " + urlFinancial);
            String resultInterface = "";
            /*接口返回码和返回消息*/
            resultInterface = doPostData(urlFinancial, null);
            json(SystemCode.code_1000, resultInterface);
        } catch (Exception e) {
            json(SystemCode.code_1000);
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

    public Map<String, String> resetProductRedis() {
        /*返回值*/
        Map<String, String> productMap = new HashMap<String, String>();
        /*重新冲数据库获取*/
        String sql = "";
        sql = "select proState, proAmount from fin_product order by id asc";
        log.info("resetProductRedis = " + sql);
        FinancialProduct financialProduct = (FinancialProduct) Data.GetOne("vip_financial", sql, null, FinancialProduct.class);
//		if (null != financialProduct) {
//		}
        /*获取产品状态标志，先从Redis获取,存放时间10秒*/
        String proState = "";
        BigDecimal bdProAmount = BigDecimal.ZERO;
        proState = financialProduct.getProState() + "";
        bdProAmount = financialProduct.getProAmount();
        RedisUtil.set("financial_proState", proState, 10);
        RedisUtil.set("financial_proAmount", bdProAmount + "", 10);
        productMap.put("proAmount", bdProAmount + "");
        productMap.put("proState", proState);
        return productMap;
    }
//	/**
//	 * 1.获取借币概览接口
//	 * add by xwz 20170701
//	 */
//	@Page(Viewer = JSON)
//	public void getUserAssetsInfo() {
//		String userId = userIdStr();
//		String token = param("token");
//		// TODO: 2017/7/21 连调后加验证//
//		if (!isLogin(userId, token)) {
//			//json(SystemCode.code_1003);
//			//return;
//		}
//		try {
//			initLoginUser(userId);
//			LevelWorker.add(p2pUser);
//			//净资产
//			BigDecimal netAssets = p2pUser.getNetAssets();
//			//借入资产
//			BigDecimal loanInAssets = p2pUser.getLoanInAssets();
//			Map<String, Object> returnMap = new HashMap();
//			returnMap.put("netAssets", netAssets.setScale(4, BigDecimal.ROUND_DOWN).toString());
//			returnMap.put("loanInAssets", loanInAssets.setScale(4, BigDecimal.ROUND_DOWN).toString());
//			json(SystemCode.code_1000, com.alibaba.fastjson.JSON.toJSONString(returnMap));
//		} catch (Exception e) {
//			json(SystemCode.code_1001);
//		}
//	}

//	private void initLoginUser(String userId){
//		User loginUser = userDao.get(userId);
//		if (p2pUser == null) {
//			p2pUser = p2pUserDao.getById(userId, loginUser.getUserName());
//			p2pUserDao.initLoanUser(p2pUser);
//		}
//	}

//	/**
//	 * 2.借币列表
//	 */
//	@Page(Viewer = JSON)
//	public void getLoanRecordsList() {
//		String userId = param("userId");
//		String token = param("token");
//		if (!isLogin(userId, token)) {
//			//json(SystemCode.code_1003);
//			//return;
//		}
//		//查询类型,1:未还清，2：已还清
//		int type = intParam("type");
//		int pageIndex = intParam("pageIndex");
//		int pageSize = intParam("pageSize");
//		if(pageSize == 0){
//			pageSize = 10;
//		}
//		//重置用户资产
//		initLoginUser(userId);
//		Map<String, Object> returnMap = getLoanRecordList(userId, type, pageIndex, pageSize);
//		if(returnMap != null){
//			json(SystemCode.code_1000, returnMap);
//		}else{
//			json(SystemCode.code_1000, "");
//		}
//	}

    /**
     * @param userId
     * @param type 1：未还清，2：已还清
     * @param pageIndex
     * @param pageSize
     * @return
     */
//	private Map<String, Object> getLoanRecordList(String userId, int type, int pageIndex, int pageSize) {
//		LoanRecordDao recordDao = new LoanRecordDao();
//		try {
//			String cointFundsType = request.getParameter("coint");
//			recordDao.setCoint(coint);
//			com.world.data.mysql.Query query = recordDao.getQuery();
//			query.setSql("select * from loanrecord");
//			query.setCls(LoanRecord.class);
//			//增加判断条件（不限币种）
//			if (StringUtils.isNotEmpty(cointFundsType)) {
//				query.append("fundsType=" + coint.getFundsType());
//			}
//			query.append("inUserId='" + userId + "'");
//			if (type == 1) {    //未还款
//				query.append("status in (1,3)");
//			} else if (type == 2) {//已还款
//				query.append("status in (2,4)");
//			}
//			query.append("ORDER BY field(status,3,0,1,2,4), createTime desc");
//			List<Map<String, Object>> loanRecordList = new ArrayList<Map<String, Object>>();
//			Map<String, Object> page = new HashMap<String, Object>();
//			// 将参数保存为attribute
//			try {
//				int total = recordDao.count();
//				if (total > 0) {
//					List<LoanRecord> dataList = recordDao.findPage(pageIndex, pageSize);
//					for (LoanRecord loanRecord : dataList) {
//						Map<String, Object> loanRecordMap = new HashMap<String, Object>();
//						loanRecordMap.put("id", loanRecord.getId());//记录ID
//						String propTag = DatabasesUtil.coinProps(loanRecord.getFundsType()).getPropTag();
//						loanRecordMap.put("coinName", propTag);//借贷币种
//						// 图片路径
//						loanRecordMap.put("coinPicUrl", STATIC_DOMAIN + "/statics/img/common/" + propTag.toLowerCase() + ".png");
//						//借贷总金额
//						loanRecordMap.put("amount", loanRecord.getAmount());
//						//利率
//						loanRecordMap.put("rate", loanRecord.getRate());
//						// TODO: 2017/7/21 累计利息（总利息）
//						loanRecordMap.put("interests", loanRecord.getNeedLx());//应还利息
//						// TODO: 2017/7/21 已还百分比
//						loanRecordMap.put("hasRepayRate", loanRecord.getHasRepay().divide(loanRecord.getAmount(), 2, BigDecimal.ROUND_DOWN));
//						//已还比例
//						// 已借天数
//						int days = TimeUtil.getIntervalDays(new Timestamp(System.currentTimeMillis()), loanRecord.getCreateTime()) + 1;
//						//已借天数
//						loanRecordMap.put("days", days);
//						//状态
//						loanRecordMap.put("status", loanRecord.getStatus());
//						//状态描述
//						loanRecordMap.put("statusShow", L(EnumUtils.enumToMap(LoanRecordStatus.values()).get(loanRecord.getStatus())));
//						//最后还款时间
//						loanRecordMap.put("repayTime", loanRecord.getRepayDate());
//						//1.还款中，2.已还清,3.需平仓,4:平仓还款
//						if (loanRecord.getStatus() == 3) {
////                            loanRecordMap.put("showRepayButton",checkIsShowRepayButton() ? 1 : 0);
//						} else if (loanRecord.getStatus() == 1) {
////                            loanRecordMap.put("showRepayButton",1);
//						}
//						loanRecordList.add(loanRecordMap);
//					}
//					page.put("pageIndex", pageIndex);
//					page.put("totalCount", total);
//					page.put("loanRecordList", loanRecordList);
//					return page;
//				}
//			} catch (Exception ex) {
//				log.error("内部异常", ex);
//			}} catch (Exception e) {
//			log.error("内部异常", e);}
//		return null;
//	}


    /**
     * 3.获取借币页面信息
     *
     */
//	@Page(Viewer = JSON)
//	public void getLoanInPageInfo() {
//		String userId = param("userId");
//		String token = param("token");
//		Map<String, Object> loanInInfoMap = new LinkedHashMap();
//
//		try{
//			initLoginUser(userId);
//			LevelWorker.add(p2pUser);
//			//净资产
//			BigDecimal netAssets = p2pUser.getNetAssets();
//			//借入资产
//			BigDecimal loanInAssets = p2pUser.getLoanInAssets();
//			Map<String, Object> returnMap = new HashMap();
//			returnMap.put("netAssets", netAssets.setScale(4,BigDecimal.ROUND_DOWN).toString());
//			returnMap.put("loanInAssets", loanInAssets.setScale(4,BigDecimal.ROUND_DOWN).toString());
//			json(SystemCode.code_1000, com.alibaba.fastjson.JSON.toJSONString(returnMap));
//		}catch(Exception e){
//			json(SystemCode.code_1001);
//		}
//	}


//	@Page(Viewer = JSON)
//	public void getLoanRecordsList1() {
//		String userId = userIdStr();
//		String token = param("token");
//		Map<String, Object> loanInInfoMap = new HashMap<>();
//		if (!isLogin(userId, token)) {
//			json(SystemCode.code_1003);
//			return;
//		}
//
//		PayUserDao payUserDao = new PayUserDao();
//		Map<String, PayUserBean>  payUserMaps = payUserDao.getFundsLoanMap(userId);
//		Map<String, CoinProps> coinMap = DatabasesUtil.getCoinPropMaps();
//		for(Entry<String, PayUserBean> entry : payUserMaps.entrySet()){
//			PayUserBean payUserBean = entry.getValue();
//			String coinName = entry.getKey();
//			loanInInfoMap.put("coinName", coinName.toUpperCase());
//			loanInInfoMap.put("fullCoinName", coinMap.get(coinName).getPropEnName());
//			loanInInfoMap.put("coinPicUrl", STATIC_DOMAIN + "/statics/img/common/"+coinName + ".png");
//			loanInInfoMap.put("level", p2pUser.getLevel());
//			loanInInfoMap.put("rate", payUserBean.getRate());
//			loanInInfoMap.put("surplusLoanIn", payUserBean.getCanLoan());
//		}
//		json(SystemCode.code_1000, com.alibaba.fastjson.JSON.toJSONString(loanInInfoMap));
//
//
//	}

//	/**
//	 * 4.申请借币
//	 * add by xwz 20170701
//	 */
//	@Page(Viewer = JSON)
//	public void applyLoan() {
//		String userId = param("userId");
//		String token = param("token");
//
//		if (!isLogin(userId, token)) {
//			json(SystemCode.code_1003);
//			return;
//		}
//		BigDecimal amount = decimalParam("amount").setScale(3, BigDecimal.ROUND_DOWN);//数量
//		String cointName = param("coint");//币种类型
//		String userName = userName();
//
//		int freeCouponId = intParam("freecouponId");
//		int deductcouponId = intParam("deductcouponId");
//		if (deductcouponId < 0) {
//			json(L("后台-融资融币借入-借币提示-1"), false, "");
//			return;
//		}
//
//		if (cointName.length()<=0) {
//			json(L("后台-融资融币借入-借币提示-2"), false, "");
//			return;
//		}
//
//
//		/*抵扣券处理	Start
//		查找是否有这张抵扣券*/
//		DeductCoupon dCoupon = null;
//		if (deductcouponId > 0) {
//			// 抵扣券的状态不能使用： 0未激活、1、未使用、2已使用、3已过期、4已禁用 、5等待还款中，
//			dCoupon = (DeductCoupon) dCouponDao.findIdKey(deductcouponId);
//			if (dCoupon == null) {
//				json(L("后台-融资融币借入-借币提示-3"), false, "", false);
//				return;
//			}
//			if (!dCoupon.getUserId().equals(userId)) {
//				json(L("后台-融资融币借入-借币提示-4"), false, "", false);
//				return;
//			}
//			dCoupon.setConverAmou(coint.getStag());// 折换线上价格
//			if (dCoupon.getConverAmou().compareTo(amount) > 0) {
//				json(L("后台-融资融币借入-借币提示-5"), false, "", false);
//				return;
//			}
//			if (dCoupon.getUseState() == 0) {
//				json(L("后台-融资融币借入-借币提示-6"), false, "");
//				return;
//			}
//			if (dCoupon.getUseState() == 2) {
//				json(L("后台-融资融币借入-借币提示-7"), false, "");
//				return;
//			}
//			if (dCoupon.getUseState() == 3) {
//				json(L("后台-融资融币借入-借币提示-8"), false, "");
//				return;
//			}
//			if (dCoupon.getUseState() == 4) {
//				json(L("后台-融资融币借入-借币提示-9"), false, "");
//				return;
//			}
//			if (dCoupon.getUseState() == 5) {
//				json(L("后台-融资融币借入-借币提示-10"), false, "");
//				return;
//			}
//		}
//		/*		抵扣券处理	End*/
//
//		BigDecimal minUnit = defaDao.getLimitBigDecimal(coint.getStag(), DefaultLimitType.p2pMinLoan.getValue());
//		// 自动匹配一个借出标
//		com.world.data.mysql.Query query = inDao.getQuery();
//		query.setSql("select loan.* from loan join p2puser on loan.userId=p2puser.userId " +
//				"where loan.amount-loan.hasamount>=? and loan.amount-loan.hasamount>=? and loan.isin=0 and loan.status <= 1");
//		query.setCls(Loan.class);
//		List<Object> params = new ArrayList<Object>();
//		params.add(amount);
//		params.add(minUnit);
//
//		//先不增加投资验证
////		query.append("p2puser.loanOutStatus=1");
//
//		String order = "order by ";
//		if (freeCouponId > 0) {
//			query.append("loan.withoutLx=1 ");
//		}else{
//			query.append("loan.withoutLx=0 ");
////			order +="loan.withoutLx asc,";
//		}
//
//		query.append("loan.fundstype=?");
//		params.add(coint.getFundsType());
//
//		order+=" loan.interestRateOfDay asc, loan.createtime asc ";
//		query.append(order);
//		query.setParams(params.toArray());
//
//
//		Loan loan = (Loan) query.getOne();
//		if (loan == null) {
//			params.set(0, 0);
//			query.setSql("select max(amount-hasamount) amount from (" + query.getSql() + ") a ");
//			query.setParams(params.toArray());
//			Loan loan2 = (Loan) query.getOne();
//			if (freeCouponId > 0) {
//				if (loan2 != null && loan2.getAmount() != null) {
//					json(L("后台-融资融币借入-借币提示-12",
//							loan2.getAmount().setScale(2, RoundingMode.DOWN).toPlainString()), false, "",false);
//					return;
//				} else {
//					json(L("后台-融资融币借入-借币提示-13"), false, "",false);
//					return;
//				}
//			} else {
//				if (loan2 != null && loan2.getAmount() != null) {
//					json(L("后台-融资融币借入-借币提示-14",
//							loan2.getAmount().setScale(2, RoundingMode.DOWN).toPlainString()), false, "",false);
//					return;
//				} else {
//					json(L("后台-融资融币借入-借币提示-15"), false, "",false);
//					return;
//				}
//			}
//		}else {
//			//费率
//			BigDecimal p2pOutRate = defaDao.getLimitBigDecimal(coint.getStag(), DefaultLimitType.p2pOutRate.getValue());
//			if(p2pOutRate.compareTo(BigDecimal.ZERO) == 0){
//				p2pOutRate = DigitalUtil.getBigDecimal(0.1);
//			}
//			int confirm = intParam("confirm"); //如果用户确认
//			int onekeyentrust = intParam("onekeyentrust"); //如果一键杠杆
//
//			if(confirm==0 && onekeyentrust==0){
//				if(loan.getInterestRateOfDay().compareTo(p2pOutRate.movePointLeft(2)) > 0){
//					json(L("后台-融资融币借入-借币提示-16", p2pOutRate + "%", loan.getInterestRateOfDay()
//							.movePointRight(2).stripTrailingZeros().toPlainString() + "%"), false, "\"needConfirm\"", false);
//					return;
//				}
//			}
//
//		}
//
//		if(freeCouponId==0 && loan.getWithoutLx()){
//			log.error("非免息借款匹配错了免息贷款!!! userid=" + userId + ", username=" + userName + ", amount=" + amount + ", loan.UserName=" + loan.getUserName() + ", loan.id=" + loan.getId() + "," );
//			log.error("借款sql::" + query.getSql());
//			log.error("借款参数::" + Arrays.toString(query.getParams()));
//
//			//jiahuaxyz的ID
////			UserManager.getInstance().pushToApp(
////					"115016",1,
////					"非免息借款匹配错了免息贷款!!! userid=" + userId + ", username=" + userName + ", amount=" + amount
////							+ ", loan.UserName=" + loan.getUserName() + ", loan.id=" + loan.getId() + ",");
//
//		}
//
//		RiskType riskType = (RiskType) EnumUtils.getEnumByKey(1, RiskType.class); // 默认---自担风险
//
//		//TODO:来源---WEB
//		DataResponse dr = recordDao.doLoan(this, loan.getId(), amount, userId, userName, riskType, freeCouponId, BigDecimal.ZERO, deductcouponId, SourceType.WEB.getKey());
//
//		json(L(dr.getDes()), dr.isSuc(), dr.getDataStr(),false);
//	}


    /**
     * 5.获取还币页面信息
     *
     */
//	@Page(Viewer = JSON)
//	public void getRepayPageInfo() {
//		String userId = param("userId");
//		String token = param("token");
//
//		if (!isLogin(userId, token)) {
//			json(SystemCode.code_1003);
//			return;
//		}
//		String json = "{\n" +
//				"\"resMsg\": {\n" +
//				"\"code\": 1000,\n" +
//				"\"message\": \"操作成功\",\n" +
//				"\"method\": \"getUserAssetsInfo\"\n" +
//				"},\n" +
//				"\"datas\": {\n" +
//				"\"amount\": 10,\n" +
//				"\"interests\":0.1,\n" +
//				"\"amount\": 10.1,\n" +
//				"\"balance\":8.34\n" +
//				"}";
//		json(SystemCode.code_1000,json);
//	}


    /**
     * 6.还币接口
     * add by xw 20170701
     */
//	@Page(Viewer = JSON)
//	public void repayLoan() {
//		String userId = param("userId");
//		String token = param("token");
//
//		if (!isLogin(userId, token)) {
//			json(SystemCode.code_1003);
//			return;
//		}
//		try {
//			int id = intParam("id");
//			BigDecimal repay = BigDecimal.ZERO;
//			try{
//				repay = decimalParam("repay");// 还款金额
//			}catch(Exception e){
//				json(L("后台-融资融币借入-还款提示-6"), false, "");
//				return;
//			}
//
//			int repayType = intParam("repayType");// 还款方式
//
//			LoanRecordDao loanRecordDao = new LoanRecordDao();
//			loanRecordDao.setLan(lan);
//			LoanRecord lr = (LoanRecord) Data.GetOne("select * from LoanRecord where id=? and inUserId=? AND status in (?,?)",
//					new Object[] { id, userId, LoanRecordStatus.Returning.getKey(), LoanRecordStatus.forceRepay.getKey()}, LoanRecord.class);
//
//			if (lr != null) {
//				CoinProps coint = DatabasesUtil.coinProps(lr.getFundsType());
//				BigDecimal minUnit = defaDao.getLimitBigDecimal(coint.getStag(), DefaultLimitType.p2pMinLoan.getValue());
//				if (lr.getAmount().compareTo(BigDecimal.ZERO) > 0) {
//					minUnit = lr.getAmount().compareTo(minUnit) > 0 ? minUnit : lr.getAmount();
//				}

//				// 部分还款
//				if (repayType == 1) {
//					if (repay.compareTo(minUnit) < 0) {
//						json(L("还款金额不能小于最小还款金额%%.", minUnit + coint.getPropTag()), false, "");
//						return;
//					}
//
//					if (lr.getAmount().subtract(repay).compareTo(minUnit) < 0 && repay.compareTo(lr.getAmount()) < 0) {
//						json(L("部分还款之后剩余还款金额不能小于%%.", minUnit + coint.getPropTag()), false, "");
//						return;
//					}
//
//					if (repay.compareTo(lr.getAmount()) > 0) {
//						json(L("还款金额不能大于可还款的金额。"), false, "");
//						return;
//					}
//
//					lr.setThisRepay(repay);
//				}

    /*start by xwz 2016-06-09*/
//				if (repayType == 0) {// 全部还款
//					if(repay.compareTo(lr.getCouldRepay().add(lr.getNeedLx())) != 0){
//						throw new Exception("该客户非法还款");
//					}
//				}else if(repayType == 1){// 部分还款
//					if (repay.compareTo(minUnit) < 0) {
//						json(L("后台-融资融币借入-还款提示-1", minUnit + coint.getPropTag()), false, "");
//						return;
//
//					}
//
//					if (lr.getAmount().subtract(repay).compareTo(minUnit) < 0 && repay.compareTo(lr.getAmount()) < 0) {
//						json(L("后台-融资融币借入-还款提示-2", minUnit + coint.getPropTag()), false, "");
//						return;
//					}
//
//					if (repay.compareTo(lr.getAmount()) > 0) {
//						json(L("后台-融资融币借入-还款提示-3"), false, "");
//						return;
//					}
//					lr.setThisRepay(repay);
//				}else{
//					throw new Exception("该客户非法还款");
//				}
//				/*end*/
//
//				P2pUser p2pUser = new P2pUserDao().getById(lr.getOutUserId());
//				if (p2pUser.getIsSetFees() == 1) {
//					// 设置用户自己的费率
//					lr.setOutUserFees(p2pUser.getFees());
//				}
//				//TODO:来源---Web
//				lr.setSource(SourceType.WEB.getKey());
//				DataResponse dr = loanRecordDao._repayNew(lr);
//
//				if (dr.isSuc()) {
//					json(L("后台-融资融币借入-还款提示-4"), true, "");
//				} else {
//					json(L(dr.getDes()), false, "");
//				}
//				return;
//			}
//		} catch (Exception e) {
//			log.error("内部异常", e);
//		}
//		json(L("后台-融资融币借入-还款提示-5"), false, "");
//	}

    @SuppressWarnings("unchecked")
    @Page(Viewer = JSON)
    public void doUserFinCenInfo() {
        try {
            /*调用用户信息*/
            setLan();
            /*调用用户信息*/
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.getUserById(userId);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            String userName = user.getUserName();
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

            log.info("result = " + JSONObject.toJSONString(result));
            json(SystemCode.code_1000, "", JSONObject.toJSONString(result));
        } catch (Exception e) {
            log.info("理财报警ERROR:userFinCenInfo", e);
            json(SystemCode.code_1001);
        }

    }

    @Page(Viewer = JSON)
    public void doUserFinProfitBill() {
        try {
            setLan();
            /*调用用户信息*/
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }

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
                    + "vipWeight from fin_productinvest where userId = " + userId + " and fundsType = 51";
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
            Map<String, Object> map = new HashMap<>();
            map.put("list", resutList);
            json(SystemCode.code_1000, "", JSONObject.toJSONString(map));

        } catch (Exception e) {
            json(SystemCode.code_1000, "", null);
            log.info("理财报警ERROR:userFinProfitBill", e);
        }
    }

    @Page(Viewer = JSON)
    public void doUserFinVdsBalance() {
        try {
            setLan();
            /*调用用户信息*/
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
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
            json(SystemCode.code_1000, "", JSONObject.toJSONString(result));
        } catch (Exception e) {
            json(SystemCode.code_1001);
            log.info("理财报警ERROR:userFinVdsBalance获取用户理财VDS约异常", e);
        }
    }

    @Page(Viewer = JSON)
    public void getSuperNodeInfo() {
        try {
            int currentPage = intParam("pageIndex");
            ;//当前页数
            int pageSize = intParam("pageSize");
            ;//当前页数

            int sort = intParam("sort");  //排序， 0 正常， 1：倒叙
            int sNodeType = intParam("sNodeType");
            ;//1自建，2新增
            FinancialSuperNodeDao financialSuperNodeDao = new FinancialSuperNodeDao();
            com.world.data.mysql.Query<SuperNode> query = financialSuperNodeDao.getQuery();
            query.setSql("select sNodeAddr,sNodeBalance,sNodePayAmount,lateMiningAmount,lateMiningTime,managerRate,sNodeQueryLink from fin_supernode");
            if (sNodeType != 0) {
                query.append(" sNodeType=" + sNodeType);
            }
            if (sort != 0) {
                query.append("order by lateMiningAmount desc");  //增加排序
            } else {
                query.append("order by lateMiningTime desc");
            }
            query.setDatabase("vip_financial");
            query.setCls(SuperNode.class);
            query.setParams(new Object[]{});
            int total = query.count();
            List<SuperNode> superNodeList = new ArrayList<>();
            if (total > 0) {
                //分页查询
                superNodeList = query.getPageList(currentPage, pageSize);
                for (SuperNode superNode : superNodeList) {
                    BigDecimal bonusAmount = (superNode.getsNodeBalance().subtract(superNode.getsNodePayAmount())).multiply((BigDecimal.ONE.subtract(superNode.getManagerRate())));
                    superNode.setBonusAmount(bonusAmount);

                }
            }
            Map<String, Object> page = new HashMap<String, Object>();
            page.put("pageIndex", currentPage);
            page.put("totalCount", total);
            page.put("list", superNodeList);
            //RedisUtil.set(keyPre+currentPage,JSONObject.toJSONString(page),30);
            json(SystemCode.code_1000, "", JSONObject.toJSONString(page));
        } catch (Exception e) {
            log.info("获取超级节点信息失败", e);
        }

    }

    @Page(Viewer = JSON)
    public void getFundsDetail() {
        try {
            setLan();
            String userId = userIdStr();
            String token = param("token");
            String coinTender = param("coinTender");
            String legalTender = param("legalTender");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            String detail = payUserApi.getFinancialDetail(userId);
            AssetEvaluationVO vo = this.getAssetEvaluation(coinTender);
            Map map = new HashMap();
            map.put("detail", detail);
            if (null != vo) {

                map.put("financialCoin", vo.getFinancialCoin());
            } else {
                map.put("financialCoin", "0");
            }
            //获取折算法币汇率
            String rate = getConvertRate(legalTender);
            map.put("rate", rate);
            json(SystemCode.code_1000, "", map);
        } catch (Exception e) {
            json(SystemCode.code_1001, L("内部异常"));
            return;
        }
    }


    @Page(Viewer = JSON)
    public void doUserFinCenRewardInfo() {
        try {
            setLan();
            /*调用用户信息*/
            String userId = userIdStr();
            String token = param("token");
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            User user = userDao.getUserById(userId);
            if (null == user) {
                json(SystemCode.code_3004);
                return;
            }
            String userName = user.getUserName();
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
                    + "where invitationUserName = '" + userName + "' and authPayFlag != 1";
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
                bdLeaderBonusRatio = bdLeaderBonusWeight.divide(bdPlatLeaderBonusWeight, 2, BigDecimal.ROUND_DOWN);
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
            /*全球领袖分红池*/
            result.put("platLeaderBonusAmount", platLeaderBonusAmount);

            log.info("result = " + JSONObject.toJSONString(result));
            json(SystemCode.code_1000, "", JSONObject.toJSONString(result));
        } catch (Exception e) {
            json(SystemCode.code_1001);
            log.info("理财报警ERROR:userFinCenRewardInfo", e);
        }

    }
}

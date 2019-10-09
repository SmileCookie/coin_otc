package com.world.constant;

import com.file.config.FileConfig;
import com.world.config.GlobalConfig;
import com.world.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by micheal on 2016/10/23.
 */
public class Const {

    public interface MessageSendLimit {//信息发送限制  add by renfei

        long EMAIL_SEND_LIMIT = DateUtils.MILLIS_PER_MINUTE;//email发送频率限制
        long SMS_SEND_LIMIT = DateUtils.MILLIS_PER_MINUTE;//sms发送频率限制

        String EMAIL_SEND_TIME_CACHEKEY = "sendEmailTime_%d";
    }


    public final static String STORE_KEY = "store_authentication_";

    public final static String USER__QUALIFICATION = "user_qualification_";


    /**
     * 向商户平台确认提币前设置commandId为999
     */
    public final static long COMMAND_ID_BEFORE_WITHDRAW = 9999;

    /**
     * 登录密码输入错误次数，大于等于该值就会记录到用户操作日志中
     */
    public final static int LOGIN_PWD_ERROR_TIMES_FOR_LOG = 3;

    public final static String IMAGE_PREFIX_NEED_AUTH = FileConfig.getValue("imgDomain1") + "/picauth?type=1&file=";

    /*start by chendi  */
    public final static String Transer_Success = "Transfer Success";
    public final static String Extract_Success = "DataEctract Success";
    public final static String Import_Success = "DataImport Success";
    public final static String Type_Mysql_Transfer = "1";
    public final static String Type_Mongo_Extract = "2";
    public final static String Type_Mongo_Import = "3";

    public final static String AuthReason1 = "8";
    public final static String AuthReason2 = "9";
    public final static String AuthReason3 = "10";
    public final static String AuthReason4 = "11";


    public static final String function_forget_two_auth = "forget_two_auth_";

    public static final String function_forget_password = "forget_password_";
    /**
     * 忘记登录密码
     */
    public static final String function_forgot_login_pwd = "forgot_login_pwd_";
    /**
     * 修改手机号(放跳步)
     */
    public static final String function_upd_mobile = "upd_moblie_";
    /**
     * 修改登录密码(放跳步)
     */
    public static final String function_upd_login_pwd = "upd_login_pwd_";
    /**
     * 修改资金密码(放跳步)
     */
    public static final String function_upd_pay_pwd = "upd_pay_pwd_";
    /**
     * 提现(放跳步)
     */
    public static final String function_cash_withdrawal = "cash_withdrawal_";
    /**
     * 重置二次验证
     */
    public static final String function_reset_second_verify = "reset_second_verify_";
    /**
     * 获取身份认证验证码token的key
     */
    public static final String function_auth_qr_verify = "auth_qr_verify_";
    /**
     * 保存身份认证信息key
     */
    public static final String function_save_auth_info = "save_auth_info_";

    public final static String ContinentEN1 = "欧洲";
    public final static String ContinentEN2 = "亚洲";
    public final static String ContinentEN3 = "非洲";
    public final static String ContinentEN4 = "北美洲";
    public final static String ContinentEN5 = "南美洲";
    public final static String ContinentEN6 = "大洋洲";
    public final static String ContinentEN7 = "南极洲";


    public final static String ContinentHK1 = "欧洲";
    public final static String ContinentHK2 = "亚洲";
    public final static String ContinentHK3 = "非洲";
    public final static String ContinentHK4 = "北美洲";
    public final static String ContinentHK5 = "南美洲";
    public final static String ContinentHK6 = "大洋洲";
    public final static String ContinentHK7 = "南极洲";



    public static final String OTC_COIN_TYPE_KEY = "sys:config:otc_coin_type_key";

    /* end */
    public final static String black_key = "black_";
    /*Start by guankaili 20181120 前端要求传引导页标识 */
    //引导标识
    public final static String guide_flg = "guide_flg_";
    /*end*/
    /**
     * 最大认证次数
     */
    public static final int MAX_AUTH_AMOUNT = CommonUtil.stringToInt(GlobalConfig.getValue("max_auth_amount"), 1);

    /**
     * 最大认证错误提交次数
     */
    public static final int MAX_AUTH_FAIL_TIMES = 5;


    public static final boolean IS_VALIDATE_DALU_IDCARD = CommonUtil.stringToBoolean(GlobalConfig.getValue("is.validate.dalu.idcard"), true);

    public static List<Integer> canWithdrawBtcLtcUserIdList = null;

    static {
        String canWithdrawBtcLtcUserIds = GlobalConfig.getValue("can_withdraw_btc_ltc_user_ids");
        if (StringUtils.isNotBlank(canWithdrawBtcLtcUserIds)) {
            String[] arr = canWithdrawBtcLtcUserIds.split(",");
            canWithdrawBtcLtcUserIdList = new ArrayList<>(canWithdrawBtcLtcUserIds.length());
            for (String userId : arr) {
                if (StringUtils.isBlank(userId)) {
                    continue;
                }

                int userIdInt = CommonUtil.stringToInt(userId.trim());
                if (userIdInt > 0) {
                    canWithdrawBtcLtcUserIdList.add(userIdInt);
                }
            }
        }
    }

    public static final long TICKER_REQUEST_PERIOD = 3000L;//行情请求时间间隔
    public static final long TRADE_REQUEST_PERIOD = 4000L;//行情请求时间间隔
    public static final long DEPTH_REQUEST_PERIOD = 4000L;//行情请求时间间隔
    public static final String CACHE_TICKER_TRADE_LIST_KEY = "ticker_list_key";//各大交易所配置信息list cache key
    public static final String CACHE_KLINE_TRADE_LIST_KEY = "kline_trade_list_key";//各大交易所配置信息list cache key K线
    public static final String CACHE_TOP_KLINE_TRADE_LIST_KEY = "top_kline_trade_list_key";//各大交易所配置信息list cache key K线顶部list
    public final static String CACHE_LASTEST_WEIBO = "weibo_cache_key";//最新微博 cache key
    public static final String CACHE_USDCNY = "cache_USDCNY";//美元汇率 cache key
    public static final String CACHE_OTHER_COINS_TICKERS = "other_coins_tickers";//山寨币行情 cache key
    public static final String CACHE_TRADE_CHART_DATA_BTC = "trade_chart_data_btc";//交易页面chart缓存数据BTC
    public static final String CACHE_TRADE_CHART_DATA_LTC = "trade_chart_data_ltc";//交易页面chart缓存数据LTC

    public interface RechargeStatus {
        int CONFIRMING = 0;//确认中状态
        int SUCCESS = 2;//充值成功,财务到账
        int FAIL = 1;//充值地址错误的数据,一般不会出现
    }


    public interface WithdrawStatus {

        int PENDING = 0;//打币中
        int SUCCESS = 2;//提现成功,财务扣减
        int FAIL = 1;//提币地址错误

        int CANCELED = 3;//取消提币

    }
    /*start by chendi 20171028 拆分客户类型配置*/
    /**
     * 用户类型：01-普通用户
     **/
    public static final String CUSTOMER_TYPE_NORMAL = "01";
    /**
     * 用户类型：07-公司账户-量化交易
     **/
    public static final String CUSTOMER_TYPE_COUNT_TRADE = "07";
    /**
     * 用户类型：04-公司账户-融资融币
     **/
    public static final String CUSTOMER_TYPE_FINANCING = "04";
    /**
     * 用户类型：05-测试用户
     **/
    public static final String CUSTOMER_TYPE_TEST = "05";
    /**
     * 用户类型：06-其他用户
     **/
    public static final String Mysql_Transfer = "1";
    public static final String Mongo_User_Extract = "2";
    public static final String Mongo_User_Import = "3";
    public static final String Mongo_UserLoginIp_Extract = "4";
    public static final String Mongo_UserLoginIp_Import = "5";
    public static final String Bill_Count = "6";
    public static final String Bill_ALL_Count = "7";
    public static final String All_User_Data_Count = "8";
    public static final String All_User_Count = "9";
    public static final String Capital_Account = "10";
    public static final String bill_otc_Transfer = "11";
    public static final String bill_wallet_Transfer = "12";
    public static final String bill_financial_Transfer = "26";
    
    public static final String payUserTypeOtc = "pay_user_otc";


    public static final String CUSTOMER_TYPE_OTHER = "06";


    public static final String CUSTOMER_OPERATION_NO_TRADE = "01";

    public static final String CUSTOMER_OPERATION_NO_CASH = "02";

    public static final String CUSTOMER_OPERATION_NO_LIMIT = "03";
    public static final Map<String, String> CUSTOMER_TYPE = new HashMap<String, String>();

    static {
        CUSTOMER_TYPE.put(CUSTOMER_TYPE_NORMAL, "普通用户");
        CUSTOMER_TYPE.put(CUSTOMER_TYPE_COUNT_TRADE, "公司账户-量化交易");
        CUSTOMER_TYPE.put(CUSTOMER_TYPE_FINANCING, "公司账户-融资融币");
        CUSTOMER_TYPE.put(CUSTOMER_TYPE_TEST, "公司账户-测试用户");
        CUSTOMER_TYPE.put(CUSTOMER_TYPE_OTHER, "其他用户");

    }


    public static final Map<String, String> CUSTOMER_OPERATION = new HashMap<String, String>();

    static {
        CUSTOMER_OPERATION.put(CUSTOMER_OPERATION_NO_TRADE, "交易受限");
        CUSTOMER_OPERATION.put(CUSTOMER_OPERATION_NO_CASH, "提现受限");
        CUSTOMER_OPERATION.put(CUSTOMER_OPERATION_NO_LIMIT, "正常用户");

    }

    public static final Map<String, String> TIMER_NAME = new HashMap<String, String>();

    static {
        TIMER_NAME.put(Mysql_Transfer, "mysql数据转移");
        TIMER_NAME.put(Mongo_User_Extract, "mongo用户数据抽取");
        TIMER_NAME.put(Mongo_User_Import, "mongo用户数据导入");
        TIMER_NAME.put(Mongo_UserLoginIp_Extract, "mongo登陆记录数据抽取");
        TIMER_NAME.put(Mongo_UserLoginIp_Import, "mongo登陆记录数据导入");
        TIMER_NAME.put(Bill_Count, "用户资金报表统计");
        TIMER_NAME.put(Bill_ALL_Count, "用户资金累计报表");
        TIMER_NAME.put(All_User_Data_Count, "活跃用户数据统计");
        TIMER_NAME.put(All_User_Count, "活跃用户统计");
        TIMER_NAME.put(Capital_Account, "交易平台资金总账");
        TIMER_NAME.put(bill_wallet_Transfer, "bill_wallet数据转移");
        TIMER_NAME.put(bill_otc_Transfer, "bill_otc数据转移");
        TIMER_NAME.put(bill_financial_Transfer, "bill_financial数据转移");
    }

    public static final Map<String,String> payUserTypeMap = new HashMap<>();
    static {

        payUserTypeMap.put("1","pay_user_wallet");
        payUserTypeMap.put("2","pay_user");
        payUserTypeMap.put("3","pay_user_otc");
        payUserTypeMap.put("4","pay_user_futures");
        payUserTypeMap.put("5","pay_user_financial");

    }



    public static final Map<String, String> ReasonMap = new HashMap<>();

    static {
        ReasonMap.put(AuthReason1, "图像经过处理");
        ReasonMap.put(AuthReason2, "图像不清晰");
        ReasonMap.put(AuthReason3, "证件图像类型不符");
        ReasonMap.put(AuthReason4, "平台仅支持满16周岁的用户进行交易");
    }


    public static final Map<String, String> UserDistributionENMap = new HashMap<>();

    static {
        UserDistributionENMap.put(ContinentEN1, "Europe");
        UserDistributionENMap.put(ContinentEN2, "Asia");
        UserDistributionENMap.put(ContinentEN3, "Africa");
        UserDistributionENMap.put(ContinentEN4, "North America");
        UserDistributionENMap.put(ContinentEN5, "South America");
        UserDistributionENMap.put(ContinentEN6, "Oceania");
        UserDistributionENMap.put(ContinentEN7, "Antarctica");

    }

    public static final Map<String, String> UserDistributionHKMap = new HashMap<>();
    static {

        UserDistributionHKMap.put(ContinentHK1, "歐州");
        UserDistributionHKMap.put(ContinentHK2, "亞州");
        UserDistributionHKMap.put(ContinentHK3, "非州");
        UserDistributionHKMap.put(ContinentHK4, "北美州");
        UserDistributionHKMap.put(ContinentHK5, "南美州");
        UserDistributionHKMap.put(ContinentHK6, "大洋州");
        UserDistributionHKMap.put(ContinentHK7, "南極州");
    }


    public static final String recharge = "recharge";
    public static final String withdraw = "withdraw";
    public static final String sysRecharge = "sysRecharge";
    public static final String sysDeduction = "sysDeduction";
    public static final String sysSort = "sysSort";
    public static final String icoExchange = "icoExchange";
    public static final String sell = "sell";
    public static final String buy = "buy";
    public static final String transactionFee = "transactionFee";
    public static final String withdrawFee = "withdrawFee";
    public static final String bookBalance = "bookBalance";
    public static final String internalAdjustmentPositiveFlag = "internalAdjustmentPositiveFlag";
    public static final String internalAdjustmentNegativeFlag = "internalAdjustmentNegativeFlag";
    public static final String externalAdjustmentPositiveFlag = "externalAdjustmentPositiveFlag";
    public static final String externalAdjustmentNegativeFlag = "externalAdjustmentNegativeFlag";
    public static final String backCapitalFlag = "backCapitalFlag";
    public static final String backCapitalFailFlag = "backCapitalFailFlag";
    public static final String luckDrawCapital = "luckDrawCapital";


    public static final List<String> ReconciliationList = new ArrayList<>();

    static {
        ReconciliationList.add(recharge);
        ReconciliationList.add(withdraw);
        ReconciliationList.add(sysRecharge);
        ReconciliationList.add(sysDeduction);
        ReconciliationList.add(sysSort);
        ReconciliationList.add(icoExchange);
        ReconciliationList.add(sell);
        ReconciliationList.add(buy);
        ReconciliationList.add(transactionFee);
        ReconciliationList.add(withdrawFee);
        ReconciliationList.add(bookBalance);
        ReconciliationList.add(internalAdjustmentPositiveFlag);
        ReconciliationList.add(internalAdjustmentNegativeFlag);
        ReconciliationList.add(externalAdjustmentPositiveFlag);
        ReconciliationList.add(externalAdjustmentNegativeFlag);
        ReconciliationList.add(backCapitalFlag);
        ReconciliationList.add(luckDrawCapital);
        ReconciliationList.add(backCapitalFailFlag);

    }


    /**end**/

    /*start by xzhang 20171106 新增活动状态*/
    /**
     * 活动状态：01 未开始，02:进行中，03:暂停， 04:结束  05:删除
     **/
    public static final String EVENT_STATUS_UNSTART = "01";
    /**
     * 活动状态：02:进行中
     **/
    public static final String EVENT_STATUS_ING = "02";
    /**
     * 活动状态：03:暂停
     **/
    public static final String EVENT_STATUS_SUSPEND = "03";
    /**
     * 活动状态：04:结束
     **/
    public static final String EVENT_STATUS_OVER = "04";
    /**
     * 活动状态：05:删除
     **/
    public static final String EVENT_STATUS_DEL = "05";

    public static final Map<String, String> EVENT_STATUS = new HashMap<String, String>();

    static {
        EVENT_STATUS.put(EVENT_STATUS_UNSTART, "未开始");
        EVENT_STATUS.put(EVENT_STATUS_ING, "进行中");
        EVENT_STATUS.put(EVENT_STATUS_SUSPEND, "暂停");
        EVENT_STATUS.put(EVENT_STATUS_OVER, "结束");
        EVENT_STATUS.put(EVENT_STATUS_DEL, "删除");
    }
    /**活动类型:01:抽奖**/
    public static final String EVENT_TYPE_LUCKY = "01";

    /**抽奖规则是否可用：01:可用**/
    public static final String LUCKY_RULE_USABLE = "01";
    /**抽奖规则是否可用：02已失效**/
    public static final String LUCKY_RULE_DISABLE = "02";

    /**抽奖类型：01：设置上限**/
    public static final String LUCKY_RULE_TYPE_MAX = "01";
    /**抽奖类型：02组合规则**/
    public static final String LUCKY_RULE_TYPE_ZH = "02";

    /**抽奖按钮是否展示：0：不展示**/
    public static final String LUCKY_IS_UNSHOW = "0";
    /** 抽奖按钮是否展示：1：展示**/
    public static final String LUCKY_IS_SHOW = "1";

    /**
     * 抽奖规则：01:每天几次；
     **/
    public static final String LUCKY_RULE_DAY = "01";
    /**
     * 抽奖规则：02活动期间几次；
     **/
    public static final String LUCKY_RULE_CYCLE = "02";
    /**
     * 抽奖规则：03：其他活动未开始或进行中
     *
     **/
    public static final String LUCKY_RULE_ING = "03";
    /**
     * 抽奖规则：04：其他活动已结束
     **/
    public static final String LUCKY_RULE_END= "04";

    /**
     * 是否取票数最高的选项:01:否
     **/
    public static final String LUCKY_HIGHT_NO = "01";
    /**
     * 是否取票数最高的选项:02：是
     **/
    public static final String LUCKY_HIGHT_YES = "02";


    /**
     * 抽奖展示页面：01未开始页面
     **/
    public static final String LUCKY_VIEW_UNSTART = "01";
    /**
     * 抽奖展示页面：02全部中出
     **/
    public static final String LUCKY_VIEW_ALL = "02";

    /**
     * 抽奖展示页面：03可抽奖”页面
     **/
    public static final String LUCKY_VIEW_USABLE = "03";
    /**
     * 抽奖展示页面：04 无权限抽奖
     **/
    public static final String LUCKY_VIEW_NO_ACCESS = "04";
    /**
     * 抽奖展示页面：05 抽奖次数用完
     **/
    public static final String LUCKY_VIEW_USE_UP = "05";
    /**
     * 抽奖展示页面：06 抽奖资格失效
     **/
    public static final String LUCKY_VIEW_LOSE = "06";

    /**
     * 抽奖展示页面：07 无可用抽奖次数,抽奖达到上限
     **/
    public static final String LUCKY_VIEW_LIMIT = "07";

    /**end**/

    /**
     * 锁定2小时
     */
    public static final int LOCK_2_HOUR = 120;
    /**
     * 锁定24小时
     */
    public static final int LOCK_24_HOUR = 1440;

    /**
     * 钱包账户
     */
    public final static int pay_user_wallet = 1;
    /**
     * 币币账户
     */
    public final static int pay_user_bg = 2;
    /**
     * 币币账户
     */
    public final static int pay_user_financial = 5;

    /**
     * 刷量账号
     */
    public final static String BRUSH_USER = "10202000";

    /**
     * 实名认证过期时间-5分钟
     */
    public final static int AUTH_EXPIRE = 5*60*1000;
    public final static int AUTH_EXPIRE_SECONDS = 5*60;
    public final static String AUTH_RANDOM = "auth_random_";
}




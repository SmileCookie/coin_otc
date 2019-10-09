package com.world.model.backcapital.constant;

/**
 * @author buxianguan
 * @date 2017/11/16
 */
public class BackCapitalConst {
    //回购市场
    public final static String BACK_CAPITAL_MARKET = "gbc_usdt";

    //回购币种类型，usdt=10
    public final static int BACK_CAPITAL_FUNDSTYPE = 10;

    //回购提现币种类型，gbc=9
    public final static int BACK_CAPITAL_WITHDRAW_FUNDSTYPE = 9;

    //回购市场所在数据库
    public final static String BACK_CAPITAL_DB_NAME = "gbcusdtentrust";

    //回购时间缓存key
    public final static String BACK_CAPITAL_TIME_CACHE_KEY = "BACK_CAPITAL_TIME";

    //回购资金缓存key
    public final static String BACK_CAPITAL_LAST_CAPITAL_CACHE_KEY = "BACK_CAPITAL_LAST_CAPITAL";

    //回购数量缓存key
    public final static String BACK_CAPITAL_LAST_NUMBER_CACHE_KEY = "BACK_CAPITAL_LAST_NUMBER";

    //20条回购资金缓存key，包括0
    public final static String BACK_CAPITAL_TWENTY_CAPITAL_CACHE_KEY = "BACK_CAPITAL_TWENTY_CAPITAL";

    //用户与我相关第一页缓存key
    public final static String BACK_CAPITAL_USER_ENTRUST_CACHE_KEY = "BACK_CAPITAL_USER_ENTRUST_%s";

    //回购平均速度缓存key
    public final static String BACK_CAPITAL_AVG_SPEED_CACHE_KEY = "BACK_CAPITAL_AVG_SPEED";

    //回购配置缓存key
    public final static String BACK_CAPITAL_CONFIG_CACHE_KEY = "BACK_CAPITAL_CONFIG";

    //回购提现时间缓存key
    public final static String BACK_CAPITAL_WITHDRAW_TIME_CACHE_KEY = "BACK_CAPITAL_WITHDRAW_TIME";

    //回购提现累积金额缓存key
    public final static String BACK_CAPITAL_WITHDRAW_CASH_CACHE_KEY = "BACK_CAPITAL_WITHDRAW_CASH";

    //回购提现最后一次金额缓存key
    public final static String BACK_CAPITAL_WITHDRAW_LAST_CASH_CACHE_KEY = "BACK_CAPITAL_WITHDRAW_LAST_CASH";

    //回购分红汇总信息缓存key
    public final static String BACK_CAPITAL_DIVIDEND_INFO_CACHE_KEY = "BACK_CAPITAL_DIVIDEND_INFO";

    //回购分红历史信息缓存key
    public final static String BACK_CAPITAL_DIVIDEND_HISTORY_INFO_CACHE_KEY = "BACK_CAPITAL_DIVIDEND_HISTORY_INFO";

    //回购累积分红量缓存key
    public final static String BACK_CAPITAL_DIVIDEND_TOTAl_AMOUNT_INFO_CACHE_KEY = "BACK_CAPITAL_DIVIDEND_TOTAl_AMOUNT";

    //gbc持股私钥地图坐标缓存key
    public final static String GBC_PRIVATE_KEY_COORDS_LIST_CACHE_KEY = "GBC_PRIVATE_KEY_COORDS_LIST";

}

package com.world.model.dao.user.mem;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.PayUserApiService;
import com.world.cache.Cache;
import com.world.model.dao.auto.worker.TickerDataWorker;
import com.world.model.dao.cache.BaseCommonCache;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.pay.PayUserOtcDao;
import com.world.model.dao.pay.PayUserWalletDao;
import com.world.model.entity.Market;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.pay.PayUserOtcBean;
import com.world.model.entity.pay.PayUserWalletBean;
import com.world.model.loan.worker.LoanAutoFactory;
import com.world.util.DigitalUtil;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/****
 * 用户缓存处理
 *
 * @author Administrator
 */
public class UserCache implements BaseCommonCache {
    private static Logger log = Logger.getLogger(UserCache.class);
    private final static String totalAssetsBaseKey = "totalAssetsKey_";
    private final static String userJsonDataKey = "userJsonDataKey_";
    private static PayUserDao puDao = new PayUserDao();
    private static PayUserWalletDao puwDao = new PayUserWalletDao();
    private static PayUserOtcDao putDao = new PayUserOtcDao();

    ///用会员资产信息KEY  返回到用户前台信息
    private final static String userFundsKey = "user_funds_";
    ///用会员钱包资产信息KEY  返回到用户前台信息
    private final static String userWalletFundsKey = "user_wallet_funds_";

    private final static String userOtcFundsKey = "user_otc_funds_";


    private final static String userFundsAndLoanKey = "user_funds_loan_";

    private final static String userWalletFundsAndLoanKey = "user_wallet_funds_loan_";

    private final static String userOtcFundsAndLoanKey = "user_otc_funds_loan_";

    private final static int cacheSeconds = 30;

    private final static int cacheOneHourSeconds = 60 * 60;

    public static boolean setTotalAssetsJson(int userId, String totalJson) {
        return Cache.Set(totalAssetsBaseKey + userId, totalJson, cacheSeconds);
    }

    public static String getTotal(int userId) {
        String data = Cache.Get(totalAssetsBaseKey + userId);
        if (data == null) {
            data = "{\"total\" : 0}";
        }
        return data;
    }

    public static void removeTotal(int userId) {
        Cache.Delete(totalAssetsBaseKey + userId);
    }

    public static boolean setUserJson(int userId, String totalJson) {
        return Cache.Set(userJsonDataKey + userId, totalJson, cacheSeconds);
    }

    /****
     * 获取当前用户的资金信息
     *r
     * @param userId
     * @return 用户资金信息
     */
    public static Map<String, PayUserBean> getUserFundsLoan(String userId) {
        Map<String, String> fund = (Map<String, String>) Cache.GetObj(userFundsAndLoanKey + userId);
        Map<String, PayUserBean> funds = new HashMap<>();
        if (null != fund) {
            for (String key : fund.keySet()) {
                String s = fund.get(key);
                PayUserBean payUserBean = (PayUserBean) JSON.parseObject(s,PayUserBean.class);
                //JSONObject.parseObject(JSONObject.toJSON(fund.get(key)).toString());
                funds.put(key, payUserBean);
            }
        }
        // Map<String, PayUserBean> mapp = (Map<String, PayUserBean>) fund;
        //String  str = (String)Cache.GetObj(userFundsAndLoanKey + userId);
        // Map<String,PayUserBean> funds = (Map<String,PayUserBean>) JSON.parse(str);
        //Map<String, PayUserBean> funds = (Map<String, PayUserBean>) Cache.GetObj(userFundsAndLoanKey + userId);
        if (funds.size() == 0) {///重置一下
            resetUserFundsFromDatabase(userId);
            fund = (Map<String, String>) Cache.GetObj(userFundsAndLoanKey + userId);
            if (null != fund) {
                for (String key : fund.keySet()) {
                    PayUserBean payUserBean = (PayUserBean) JSON.parseObject(fund.get(key),PayUserBean.class);
                    funds.put(key, payUserBean);
                }
            }
        } else {
            log.debug("从内存中读取到用户：[" + userId + "]资金包含借贷信息。");
        }
        return funds;
    }


    /****
     * 获取当前用户的资金信息
     *
     * @param userId
     * @return 用户资金信息
     */
    public static Map<String, PayUserWalletBean> getUserWalletFundsLoan(String userId) {
        Map<String, String> fund = (Map<String, String>) Cache.GetObj(userWalletFundsAndLoanKey + userId);
        Map<String, PayUserWalletBean> funds = new HashMap<>();
        if (null != fund) {
            for (String key : fund.keySet()) {
                PayUserWalletBean payUserBean = (PayUserWalletBean) JSON.parseObject(fund.get(key),PayUserWalletBean.class);
                funds.put(key, payUserBean);
            }
        }
        // Map<String,PayUserWalletBean> funds = (Map<String,PayUserWalletBean>) JSON.parse(str);
        //Map<String, PayUserWalletBean> funds = (Map<String, PayUserWalletBean>) Cache.GetObj(userWalletFundsAndLoanKey + userId);
        if (funds.size() == 0) {///重置一下
            resetUserWalletFundsFromDatabase(userId);
            fund = (Map<String, String>) Cache.GetObj(userWalletFundsAndLoanKey + userId);
            for (String key : fund.keySet()) {
                PayUserWalletBean payUserBean = (PayUserWalletBean) JSON.parseObject(fund.get(key),PayUserWalletBean.class);
                funds.put(key, payUserBean);
            }
        } else {
            log.debug("从内存中读取到用户：[" + userId + "]资金包含借贷信息。");
        }
        if (funds == null) {
            funds = new LinkedHashMap<String, PayUserWalletBean>();
        }
        return funds;
    }


    /****
     * 获取当前用户的资金信息
     *
     * @param userId
     * @return 用户资金信息
     */
    public static JSONArray getUserFunds(String userId) {
        JSONArray funds = (JSONArray) Cache.GetObj(userFundsKey + userId);
        if (funds == null) {///重置一下
            resetUserFundsFromDatabase(userId);
            funds = (JSONArray) Cache.GetObj(userFundsKey + userId);
        } else {
            log.debug("从内存中读取到用户：[" + userId + "]资金");
        }
        if (funds == null) {
            funds = new JSONArray();
        }
        return funds;
    }

    /****
     * 获取当前用户的OTC资金信息
     *
     * @param userId
     * @return 用户资金信息
     */
    public static JSONArray getUserOtcFunds(String userId) {
        JSONArray funds = (JSONArray) Cache.GetObj(userOtcFundsKey + userId);
        if (funds == null) {///重置一下
            resetUserOtcFundsFromDatabase(userId);
            funds = (JSONArray) Cache.GetObj(userOtcFundsKey + userId);
        } else {
            log.debug("从内存中读取到用户：[" + userId + "]资金");
        }
        if (funds == null) {
            funds = new JSONArray();
        }
        return funds;
    }

    /****
     * 获取当前用户的钱包资金信息
     *
     * @param userId
     * @return 用户资金信息
     */
    public static JSONArray getUserWalletFunds(String userId) {
        JSONArray funds = (JSONArray) Cache.GetObj(userWalletFundsKey + userId);
        if (funds == null) {///重置一下
            resetUserWalletFundsFromDatabase(userId);
            funds = (JSONArray) Cache.GetObj(userWalletFundsKey + userId);
        } else {
            log.debug("从内存中读取到用户：[" + userId + "]资金");
        }
        if (funds == null) {
            funds = new JSONArray();
        }
        return funds;
    }

    /***
     * 重设用户的资金信息到数据库
     *
     * @param userId
     * @return
     */
    public static void resetUserFundsFromDatabase(String userId) {
        FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/payUser");
        PayUserApiService payUserApiService = container.getFeignClient(PayUserApiService.class);
        payUserApiService.getDetail(userId);
    }

    /***
     * 重设用户的资金信息到数据库
     *
     * @param userId
     * @return
     */
    public static void resetUserOtcFundsFromDatabase(String userId) {
        FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/payUser");
        PayUserApiService payUserApiService = container.getFeignClient(PayUserApiService.class);
        payUserApiService.getOtcDetail(userId);
    }

    /***
     * 初始化用户理财账户资金表数据，直接调UC代码
     *
     * @param userId
     * @return
     */
    public static void resetUserFinancialFunds(String userId) {
        FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/payUser");
        PayUserApiService payUserApiService = container.getFeignClient(PayUserApiService.class);
        payUserApiService.getFinancialDetail(userId);
    }

    /***
     * 重设用户的钱包资金信息到数据库
     *
     * @param userId
     * @return
     */
    public static void resetUserWalletFundsFromDatabase(String userId) {
        FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/payUser");
        PayUserApiService payUserApiService = container.getFeignClient(PayUserApiService.class);
        payUserApiService.getWalletDetail(userId);
    }

    /***
     * 需要重置的类型
     *
     * @param userId
     * @return
     */
    public static boolean resetUserFunds(String userId) {
        log.info("重置用户：[" + userId + "]资金" + Thread.currentThread().getName());
        Map<String, PayUserBean> userMaps = puDao.getFundsLoanMap(userId);
        JSONArray funds = puDao.getFundsArray(userMaps);
        return setUserFunds(userId, funds, userMaps);
    }

    /***
     * 需要重置的类型
     *
     * @param userId
     * @return
     */
    public static boolean resetUserOtcFunds(String userId) {
        log.info("重置用户：[" + userId + "]资金" + Thread.currentThread().getName());
        Map<String, PayUserOtcBean> userMaps = putDao.getFundsLoanMap(userId);
        JSONArray funds = putDao.getFundsArray(userMaps);
        return setUserOtcFunds(userId, funds, userMaps);
    }

    /***
     * 需要重置的类型
     *
     * @param userId
     * @return
     */
    public static boolean resetUserWalletFunds(String userId) {
        log.info("重置用户钱包：[" + userId + "]资金" + Thread.currentThread().getName());
        Map<String, PayUserWalletBean> userMaps = puwDao.getWalletFundsLoanMap(userId);
        JSONArray funds = puwDao.getFundsArray(userMaps);
        return setUserWalletFunds(userId, funds, userMaps);
    }


    /****
     * 获取当前系统所有币种的价格
     */
    public static JSONObject getPrices() {
        JSONObject prices = new JSONObject();
        Set<String> marketNames = Market.getAllMarketName();
        for (String name : marketNames) {
            BigDecimal price = getPriceByKey(name + "_l_price");
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                price = BigDecimal.ONE;
            }
            prices.put(name, price);
        }
        try {
            resetPrices(prices);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        return prices;
    }

    /**
     * 获取本市场没有的价格
     * <p/>
     * // TODO: 2017/9/8 suxinjie 修改了缓存price的结构,如果这里需要外网价格填补空缺的话,需要在这里修改key或者修改定时任务的key
     *
     * @return
     */
    public static void resetPrices(JSONObject prices) {
        try {
            Iterator<String> it = prices.keySet().iterator();
            JSONObject tickerPrices = TickerDataWorker.getCachePricesByTicker();
            while (it.hasNext()) {
                String key = it.next();
                BigDecimal price = prices.getBigDecimal(key);
                if (price.compareTo(BigDecimal.ONE) == 0) {
                    price = tickerPrices.getBigDecimal(key);
                    if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                        price = BigDecimal.ONE;
                    }
                }
                prices.put(key, price);
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    public static BigDecimal getBtcPrice() {
        return getPriceByKey(lastBtcPriceKey);
    }

    public static BigDecimal getPriceByKey(String key) {
        BigDecimal btc = BigDecimal.ZERO;
        String ms = Cache.Get(key);
        if (ms != null) {
            btc = DigitalUtil.getBigDecimal(ms);
        }
        if (btc.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("异常的价格导致程序无法继续了");
        }
        return btc;
    }

    /****
     * @param userId    用户ID
     * @param userFunds 用户资金信息数组    0 可用RMB 1冻结RMB   2 可用BTC 3 冻结BTC  4 可用LTC  5 冻结LTC 6 可用BTQ 7 冻结BTQ 8 资产折合RMB
     * @return
     */
    public static boolean setUserFunds(String userId, JSONArray userFunds, Map<String, PayUserBean> userMaps) {
        if (userFunds != null && userMaps != null) {
            Cache.SetObj(userFundsKey + userId, userFunds, 30);
            Map<String, String> stringObjectMap = new HashMap<>();
            for (String key : userMaps.keySet()) {
                String js = JSON.toJSONString(userMaps.get(key));
                stringObjectMap.put(key,js);
            }
            Cache.SetObj(userFundsAndLoanKey + userId, stringObjectMap, 30);
            return true;
        }
        return false;
    }

    /****
     * @param userId    用户ID
     * @param userFunds 用户资金信息数组    0 可用RMB 1冻结RMB   2 可用BTC 3 冻结BTC  4 可用LTC  5 冻结LTC 6 可用BTQ 7 冻结BTQ 8 资产折合RMB
     * @return
     */
    public static boolean setUserOtcFunds(String userId, JSONArray userFunds, Map<String, PayUserOtcBean> userMaps) {
        if (userFunds != null && userMaps != null) {
            Cache.SetObj(userOtcFundsKey + userId, userFunds, 30);
            Map<String, String> stringObjectMap = new HashMap<>();
            for (String key : userMaps.keySet()) {
                String js = JSON.toJSONString(userMaps.get(key));
                stringObjectMap.put(key,js);
            }
            Cache.SetObj(userOtcFundsAndLoanKey + userId, stringObjectMap, 30);
            return true;
        }
        return false;
    }

    /****
     * @param userId    用户ID
     * @param userFunds 用户资金信息数组    0 可用RMB 1冻结RMB   2 可用BTC 3 冻结BTC  4 可用LTC  5 冻结LTC 6 可用BTQ 7 冻结BTQ 8 资产折合RMB
     * @return
     */
    public static boolean setUserWalletFunds(String userId, JSONArray userFunds, Map<String, PayUserWalletBean> userMaps) {
        //获取对应币种
        //币种名称
        if (userFunds != null && userMaps != null) {
            Cache.SetObj(userWalletFundsKey + userId, userFunds, 30);
            Map<String, String> stringObjectMap = new HashMap<>();
            for (String key : userMaps.keySet()) {
                PayUserWalletBean p = userMaps.get(key);
                String js = JSON.toJSONString(userMaps.get(key));
                stringObjectMap.put(key,js);
            }
            Cache.SetObj(userWalletFundsAndLoanKey + userId, stringObjectMap, 30);
            return true;
        }
        return false;
    }


    public static void removeUserFunds(int userId) {
        Cache.Delete(userFundsKey + userId);
    }

    /**
     * 计算用户折合总资产
     *
     * @param userId
     * @return
     */
    public static BigDecimal getUserBTCAssest(String userId) {
        Map<String, PayUserBean> funds = UserCache.getUserFundsLoan(userId);

        JSONObject prices = LoanAutoFactory.getPrices();
        log.info("计算用户折合总资产，市场价格：" + prices);
        //resetAsset(funds, prices, p2pUser);//计算用户的总资产
        //不包含借贷的全部资产=净资产，如果有借入：净资产=可用总资产-借入金额；如果有借出：净资产=总资产-借出金额。
        BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal netAssets = BigDecimal.ZERO;
        BigDecimal inSuccess = BigDecimal.ZERO;
        BigDecimal outSuccess = BigDecimal.ZERO;

        BigDecimal totalAssetsBtc = BigDecimal.ZERO;
        BigDecimal netAssetsBtc = BigDecimal.ZERO;
        BigDecimal inSuccessBtc = BigDecimal.ZERO;
        BigDecimal outSuccessBtc = BigDecimal.ZERO;

        for (Entry<String, PayUserBean> entry : funds.entrySet()) {
            String key = entry.getKey();
            PayUserBean payUser = entry.getValue();
            /*start by xwz 20170911 积分按usd折算*/
//            if (prices.containsKey(key)) {
//                BigDecimal price = prices.getBigDecimal(key);
//                totalAssets = totalAssets.add(payUser.getTotal().multiply(price));
//                inSuccess = inSuccess.add(payUser.getInSuccess().multiply(price));
//                outSuccess = outSuccess.add(payUser.getOutSuccess().multiply(price));
//            }
            if (prices.containsKey(key + "_usdt")) {//对USD（）
                BigDecimal price = prices.getBigDecimal(key + "_usdt");
                totalAssets = totalAssets.add(payUser.getTotal().multiply(price));
                inSuccess = inSuccess.add(payUser.getInSuccess().multiply(price));
                outSuccess = outSuccess.add(payUser.getOutSuccess().multiply(price));
            } else if (prices.containsKey(key + "_btc")) {//对BTC
                BigDecimal price = prices.getBigDecimal(key + "_btc");
                totalAssetsBtc = totalAssetsBtc.add(payUser.getTotal().multiply(price));
                inSuccessBtc = inSuccessBtc.add(payUser.getInSuccess().multiply(price));
                outSuccessBtc = outSuccessBtc.add(payUser.getOutSuccess().multiply(price));
            } else {//USD本身
                totalAssets = totalAssets.add(payUser.getTotal());
                inSuccess = inSuccess.add(payUser.getInSuccess());
                outSuccess = outSuccess.add(payUser.getOutSuccess());
            }
            /*end*/
        }

        //btc转成usd
        if (prices.containsKey("btc_usdt") && totalAssetsBtc.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal price = prices.getBigDecimal("btc_usdt");
            totalAssets = totalAssets.add(totalAssetsBtc.multiply(price));
            inSuccess = inSuccess.add(inSuccessBtc.multiply(price));
            outSuccess = outSuccess.add(outSuccessBtc.multiply(price));
        }

        netAssets = totalAssets;
        if (inSuccess.compareTo(BigDecimal.ZERO) > 0) {
            netAssets = totalAssets.subtract(inSuccess);
        } else if (outSuccess.compareTo(BigDecimal.ZERO) > 0) {
            netAssets = totalAssets.subtract(outSuccess);
        }
        log.info("计算用户折合总资产，用户ID：" + userId + "，折合后净资产" + netAssets);
        return netAssets;
    }

//    public static BigDecimal getUserBTCAssest(String userId) {
//        Map<String, PayUserBean> funds = UserCache.getUserFundsLoan(userId);
//
//        JSONObject prices = LoanAutoFactory.getPrices();
//        //resetAsset(funds, prices, p2pUser);//计算用户的总资产
//        //不包含借贷的全部资产=净资产，如果有借入：净资产=可用总资产-借入金额；如果有借出：净资产=总资产-借出金额。
//        BigDecimal totalAssets = BigDecimal.ZERO;
//        BigDecimal netAssets = BigDecimal.ZERO;
//        BigDecimal inSuccess = BigDecimal.ZERO;
//        BigDecimal outSuccess = BigDecimal.ZERO;
//
//        for (Entry<String, PayUserBean> entry : funds.entrySet()) {
//            String key = entry.getKey();
//            PayUserBean payUser = entry.getValue();
//
//            if (prices.containsKey(key)) {
//                BigDecimal price = prices.getBigDecimal(key);
//                totalAssets = totalAssets.add(payUser.getTotal().multiply(price));
//                inSuccess = inSuccess.add(payUser.getInSuccess().multiply(price));
//                outSuccess = outSuccess.add(payUser.getOutSuccess().multiply(price));
//            } else {
//                totalAssets = totalAssets.add(payUser.getTotal());
//                inSuccess = inSuccess.add(payUser.getInSuccess());
//                outSuccess = outSuccess.add(payUser.getOutSuccess());
//            }
//        }
//
//        netAssets = totalAssets;
//        if (inSuccess.compareTo(BigDecimal.ZERO) > 0) {
//            netAssets = totalAssets.subtract(inSuccess);
//        } else if (outSuccess.compareTo(BigDecimal.ZERO) > 0) {
//            netAssets = totalAssets.subtract(outSuccess);
//        }
//
//        return netAssets;
//    }

}

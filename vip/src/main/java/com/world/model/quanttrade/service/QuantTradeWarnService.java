package com.world.model.quanttrade.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.qt.caesar.api.service.HedgeService;
import com.world.config.GlobalConfig;
import com.world.data.database.DatabasesUtil;
import com.world.model.backcapital.service.BackCapitalService;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.warnlog.WarnRecordAbnormalDao;
import com.world.model.entity.Market;
import com.world.model.entity.backcapital.BackCapitalConfig;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.record.QtTransRecord;
import com.world.model.entity.record.TransRecord;
import com.world.model.quanttrade.dao.QtTransRecordDao;
import com.world.model.quanttrade.dao.TransRecordDao;
import com.world.model.service.BrushAccountService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 量化交易异常预警service
 * Created by buxianguan on 17/8/8.
 */
public class QuantTradeWarnService {
    private final static Logger log = Logger.getLogger("alarmAll");

    private TransRecordDao transRecordDao = new TransRecordDao();
    private QtTransRecordDao qtTransRecordDao = new QtTransRecordDao();
    private WarnRecordAbnormalDao warnRecordAbnormalDao = new WarnRecordAbnormalDao();
    private PayUserDao payUserDao = new PayUserDao();
    private UserDao userDao = new UserDao();
    private BackCapitalService backCapitalService = new BackCapitalService();
    private BrushAccountService brushAccountService = new BrushAccountService();

    private static String API_KEY = "bitglobal-admin";
    private static String API_SECRET = "TljO85wihe+hISBCloZrrd6u6dAbjmAELv1EVJyk8TdarCHYfRIbslOAQ5VZnqk7yxCcM32Mx3/p5mvtngxXQJZCljVe+fmw8EiuMfrlqxJo2dq9HeJWjy9tKv9grpE/nKynk4LJHXmmZ8CKTHb6gZEEY5WQCCY3J6Yrzjo8AJ/wckWrZnJCPtN9j1uBT5xhrhRo8V+e4tA";
    private static String API_HOST = GlobalConfig.getValue("caesarHost");

    //量化交易账户余额阈值配置
    private final static Map<String, String> userBalanceLimitMap = new HashMap<String, String>() {{
        put("1000095", "btc:25,eth:100,ltc:500,dash:100,zec:100,etc:1000,neo:300,usdt:10000");
        put("1000028", "btc:50,eth:100,ltc:500,dash:75,zec:75,etc:1000,neo:300,omg:500");
    }};

    //量化交易账户余额+冻结资金阈值配置
    private final static Map<String, String> userBalanceFreezLimitMap = new HashMap<String, String>() {{
        put("1000095", "btc:50,eth:200,ltc:1000,dash:200,zec:200,etc:2000,neo:500,usdt:20000");
        put("1000028", "btc:100,eth:150,ltc:1000,dash:100,zec:100,etc:2000,neo:500,omg:1000");
    }};

    //GBC市场数据库对应的刷量账户
    private final static Map<String, String> gbcMarketDBUserMap = new HashMap<String, String>() {{
        put("gbcusdtentrust", "1002781");
    }};

    //GBC刷量账户余额阈值配置
    private final static Map<String, String> gbcUserBalanceLimitMap = new HashMap<String, String>() {{
        put("1002781", "gbc:5000,usdt:5000");
    }};

    //GBC刷量账户余额+冻结资金阈值配置
    private final static Map<String, String> gbcUserBalanceFreezLimitMap = new HashMap<String, String>() {{
        put("1002781", "gbc:10000,usdt:10000");
    }};


    //自成交账户余额阈值配置
    private final static Map<String, String> sellDealBalanceLimitMap = new HashMap<String, String>() {{
        put("1002795", "btc:0.5,usdt:3000");
    }};

    //挂撤单账户余额阈值配置
    private final static Map<String, String> entrustCancelBalanceLimitMap = new HashMap<String, String>() {{
        put("1000038", "btc:0.5,usdt:3000");
    }};

    /**
     * 量化交易账号交易监控：与用户成交
     */
    public void checkTransRecord(long startTime, long endTime) {
        Map<String, JSONObject> markets = Market.getMarketsMap();
        for (Map.Entry<String, JSONObject> entry : markets.entrySet()) {
            String market = entry.getKey();
            JSONObject marketJson = entry.getValue();

            String marketDBName = marketJson.getString("db");
            List<String> brushUserIds = brushAccountService.getBrushAccouts();
            if (CollectionUtils.isNotEmpty(brushUserIds)) {
                String systemUserIds = Joiner.on(",").skipNulls().skipNulls().join(brushUserIds);
                checkNoSelfTrans(marketDBName, market, systemUserIds, "量化账号", startTime, endTime);
            }
        }
    }

    /**
     * 量化交易账号剩余资金监控
     */
    public void checkBalance() {
        //监控资金余额
        checkBalance("量化账号", userBalanceLimitMap, userBalanceFreezLimitMap);

        //自成交账号剩余资金监控
        checkBalance("自成交账号", sellDealBalanceLimitMap, new HashMap<String, String>());

        //挂撤单账号剩余资金监控
        checkBalance("挂撤单账号", entrustCancelBalanceLimitMap, new HashMap<String, String>());
    }

    /**
     * GBC刷量账号监控：1、剩余资金。2、剩余资金+冻结资金。3、非本用户自身买卖成交的报警。
     */
    public void checkGBCHightFrequencyTrans(long startTime, long endTime) {
        //监控资金余额
        checkBalance("GBC刷量账号", gbcUserBalanceLimitMap, gbcUserBalanceFreezLimitMap);

        //获取回购账号
        BackCapitalConfig config = backCapitalService.getConfig();
        if (null == config) {
            return;
        }
        int bcUserId = config.getBcUserId();

        //监控刷量账号非本用户自身买卖成交
        for (Map.Entry<String, String> entry : gbcMarketDBUserMap.entrySet()) {
            String dbName = entry.getKey();
            String userId = entry.getValue();
            checkGBCNoSelfTrans(dbName, userId, "GBC刷量账号", bcUserId, startTime, endTime);
        }
    }

    /**
     * 回购账号成交记录报警，非刷量账号成交
     */
    public void checkBackCapital(long startTime, long endTime) {
        //获取回购账号
        BackCapitalConfig config = backCapitalService.getConfig();
        if (null == config) {
            return;
        }
        int bcUserId = config.getBcUserId();

        for (Map.Entry<String, String> entry : gbcMarketDBUserMap.entrySet()) {
            String dbName = entry.getKey();
            String userId = entry.getValue();
            checkBackCapitalTrans(dbName, String.valueOf(bcUserId), "回购账号", userId, startTime, endTime);
            break;
        }
    }

    /**
     * 量化保值对账：与用户成交的单子是否都保值完成
     */
    public void checkHedgeCount(long startTime, long endTime) {
        //获取保值程序的对冲记录
        Map counts = HedgeService.count(API_HOST, API_KEY, API_SECRET, String.valueOf(startTime), String.valueOf(endTime));
        //分各个市场开始对账
        Map<String, JSONObject> markets = Market.getMarketsMap();
        for (Map.Entry<String, JSONObject> entry : markets.entrySet()) {
            String market = entry.getKey();
            JSONObject marketJson = entry.getValue();

            String marketDBName = marketJson.getString("db");
            List<String> brushUserIds = brushAccountService.getBrushAccouts();
            if (CollectionUtils.isNotEmpty(brushUserIds)) {
                String systemUserIds = Joiner.on(",").skipNulls().skipNulls().join(brushUserIds);
                checkNoSelfTransWithoutCount(marketDBName, systemUserIds, market, counts, startTime, endTime);
            }
        }
    }

    /**
     * 监控某人买或者卖的交易记录
     */
    public void checkBuyOrSell(String database, String userId, String userName, long startTime, long endTime) {
        String errorLogInfo = "Exception: %s【%s】挂单被其他用户【%s(%s)(买方)，%s(%s)(卖方)】成交，成交编号【%s】，成交信息：数量*价格=总价【%s*%s=%s】，市场【%s】。";

        transRecordDao.setDatabase(database);

        //获取某人买或者卖的交易记录
        List<TransRecord> list = transRecordDao.getUserTrans(userId, startTime, endTime);
        if (CollectionUtils.isNotEmpty(list)) {
            for (TransRecord transRecord : list) {
                //记录错误日志，用于监控
                log.info(String.format(errorLogInfo, userName, userId, getUserNameById(transRecord.getUserIdBuy(), userId, userName), transRecord.getUserIdBuy(),
                        getUserNameById(transRecord.getUserIdSell(), userId, userName), transRecord.getUserIdSell(), transRecord.getTransRecordId(),
                        transRecord.getNumbers().toPlainString(), transRecord.getUnitPrice().toPlainString(), transRecord.getTotalPrice().toPlainString(),
                        database));
            }
        }
    }

    /**
     * 监控跟别人成交的交易记录 不保存
     */
    private void checkNoSelfTransWithoutCount(String database, String systemUserIds, String market, Map counts, long startTime, long endTime) {
        if (database.indexOf("vds") >= 0) {
            return;
        }
        String errorLogInfo = "对冲遗漏预警：市场[%s]， 在[%s]-[%s]内，用户成交单[%s]笔,保值成功[%s]笔,剩余[%s]笔未发现记录,请及时查看！";
        transRecordDao.setDatabase(database);

        //获取跟别人成交的交易记录
        List<TransRecord> list = transRecordDao.getNotAllSelfTrans(systemUserIds, startTime, endTime);
        if (CollectionUtils.isNotEmpty(list)) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startTimeStr = dateFormat.format(new Date(startTime));
            String endTimeStr = dateFormat.format(new Date(endTime));

            int userSize = list.size();
            if (null == counts) {
                //保值0笔 预警
                log.info(String.format(errorLogInfo, market, startTimeStr, endTimeStr, userSize, 0, userSize));
                warnRecordAbnormalDao.saveWarnLog(market, new BigDecimal(userSize), BigDecimal.ZERO, new BigDecimal(userSize), startTime, endTime);
                return;
            }
            Object obj = counts.get(market);
            if (null == obj) {
                //保值0笔 预警
                log.info(String.format(errorLogInfo, market, startTimeStr, endTimeStr, userSize, 0, userSize));
                warnRecordAbnormalDao.saveWarnLog(market, new BigDecimal(userSize), BigDecimal.ZERO, new BigDecimal(userSize), startTime, endTime);
                return;
            }
            Integer count = (Integer) obj;
            if (count != userSize) {
                //保值数量异常 预警
                log.info(String.format(errorLogInfo, market, startTimeStr, endTimeStr, userSize, count, userSize - count));
                warnRecordAbnormalDao.saveWarnLog(market, new BigDecimal(userSize), new BigDecimal(count), new BigDecimal(userSize - count), startTime, endTime);
            }
        }
    }

    /**
     * 监控跟别人成交的交易记录 并保存
     */
    private void checkNoSelfTrans(String database, String market, String systemUserIds, String userName, long startTime, long endTime) {
        String errorLogInfo = "刷量与用户成交:【%s】市场【%s(%s)(买方) 与 %s(%s)(卖方)】成交，成交编号【%s】，成交信息：数量【%s】价格【%s】总价【%s】";

        transRecordDao.setDatabase(database);

        //获取跟别人成交的交易记录
        List<TransRecord> list = transRecordDao.getNotAllSelfTrans(systemUserIds, startTime, endTime);
        if (CollectionUtils.isNotEmpty(list)) {
            //插入数据库，等待保值获取
            qtTransRecordDao.setDatabase("vip_main");

            for (TransRecord transRecord : list) {
                QtTransRecord qtTransRecord = convertTr(transRecord, market, systemUserIds);
                if (qtTransRecord != null) {
                    int rtn = qtTransRecordDao.insertIgnore(qtTransRecord);
                    if (rtn > 0) {
                        log.info("普通用户与量化成交, 市场=" + market + ", 量化用户=" + systemUserIds + ", 处理时间=" + startTime + "," + endTime);

                        //记录错误日志，用于监控
                        log.info(String.format(errorLogInfo, market, getUserNameById(transRecord.getUserIdBuy(), systemUserIds, userName), transRecord.getUserIdBuy(),
                                getUserNameById(transRecord.getUserIdSell(), systemUserIds, userName), transRecord.getUserIdSell(), transRecord.getTransRecordId(),
                                transRecord.getNumbers().stripTrailingZeros().toPlainString(), transRecord.getUnitPrice().stripTrailingZeros().toPlainString(),
                                transRecord.getTotalPrice().stripTrailingZeros().toPlainString()));
                    }
                }
            }
        }
    }

    /**
     * 监控跟别人成交的交易记录，排除回购账号
     */
    private void checkGBCNoSelfTrans(String database, String userId, String userName, int bcUserId, long startTime, long endTime) {
        String errorLogInfo = "Exception: %s【%s】挂单被其他用户【%s(%s)(买方)，%s(%s)(卖方)】成交，成交编号【%s】，成交信息：数量*价格=总价【%s*%s=%s】，市场【%s】。";

        transRecordDao.setDatabase(database);

        //获取跟别人成交的交易记录
        List<TransRecord> list = transRecordDao.getNotSelfTrans(userId, startTime, endTime);
        if (CollectionUtils.isNotEmpty(list)) {
            for (TransRecord transRecord : list) {
                //排除回购账号
                if (transRecord.getUserIdBuy() == bcUserId || transRecord.getUserIdSell() == bcUserId) {
                    continue;
                }
                //记录错误日志，用于监控
                log.info(String.format(errorLogInfo, userName, userId, getUserNameById(transRecord.getUserIdBuy(), userId, userName), transRecord.getUserIdBuy(),
                        getUserNameById(transRecord.getUserIdSell(), userId, userName), transRecord.getUserIdSell(), transRecord.getTransRecordId(),
                        transRecord.getNumbers().toPlainString(), transRecord.getUnitPrice().toPlainString(), transRecord.getTotalPrice().toPlainString(),
                        database));
            }
        }
    }

    /**
     * 回购账户监控，排除跟刷量账号成交
     */
    private void checkBackCapitalTrans(String database, String userId, String userName, String anotherUserId, long startTime, long endTime) {
        String errorLogInfo = "Exception: %s【%s】挂单被普通用户【%s(%s)(买方)，%s(%s)(卖方)】成交，成交编号【%s】，成交信息：数量*价格=总价【%s*%s=%s】，市场【%s】。";

        transRecordDao.setDatabase(database);

        //获取跟别人成交的交易记录
        List<TransRecord> list = transRecordDao.getNoTwoUserTrans(userId, anotherUserId, startTime, endTime);
        if (CollectionUtils.isNotEmpty(list)) {
            for (TransRecord transRecord : list) {
                //记录错误日志，用于监控
                log.info(String.format(errorLogInfo, userName, userId, getUserNameById(transRecord.getUserIdBuy(), userId, userName), transRecord.getUserIdBuy(),
                        getUserNameById(transRecord.getUserIdSell(), userId, userName), transRecord.getUserIdSell(), transRecord.getTransRecordId(),
                        transRecord.getNumbers().toPlainString(), transRecord.getUnitPrice().toPlainString(), transRecord.getTotalPrice().toPlainString(),
                        database));
            }
        }
    }

    private void checkBalance(String userName, Map<String, String> balanceLimitMap, Map<String, String> balanceFreezLimitMap) {
        //监控资金余额
        for (Map.Entry<String, String> entry : balanceLimitMap.entrySet()) {
            String userId = entry.getKey();
            String balanceLimit = entry.getValue();
            checkBalance(userId, userName, balanceLimit);
        }

        //监控资金余额+冻结资金
        for (Map.Entry<String, String> entry : balanceFreezLimitMap.entrySet()) {
            String userId = entry.getKey();
            String balanceLimit = entry.getValue();
            checkBalanceFreez(userId, userName, balanceLimit);
        }
    }

    private void checkBalance(String userId, String userName, String balanceLimit) {
        String errorLogInfo = "Exception: %s【%s】【%s】币种当前剩余资金【%s】小于预警值【%s】。";

        String[] eachCoins = balanceLimit.split(",");
        //获取账号资金
        Map<Integer, PayUserBean> payUserBeanMap = getPayUserMap(userId);
        if (null == payUserBeanMap) {
            errorLogInfo = "Exception: %s【%s】获取账号剩余资金为空！";
            log.info(String.format(errorLogInfo, userName, userId));
            return;
        }

        for (String eachCoin : eachCoins) {
            String[] coins = eachCoin.split(":");
            BigDecimal minBalance = new BigDecimal(coins[1]);

            //获取资金类型
            CoinProps coinProps = DatabasesUtil.coinProps(coins[0]);

            PayUserBean payUserBean = payUserBeanMap.get(coinProps.getFundsType());
            if (null != payUserBean) {
                if (payUserBean.getBalance().compareTo(minBalance) < 0) {
                    //记录错误日志，用于监控
                    log.info(String.format(errorLogInfo, userName, userId, coins[0], payUserBean.getBalance().toPlainString(), minBalance.toPlainString()));
                }
            } else {
                //记录错误日志，用于监控
                log.info(String.format(errorLogInfo, userName, userId, coins[0], BigDecimal.ZERO.toPlainString(), minBalance.toPlainString()));
            }
        }
    }

    private void checkBalanceFreez(String userId, String userName, String balanceLimit) {
        String errorLogInfo = "Exception: %s【%s】【%s】币种当前剩余资金+冻结资金【%s】小于预警值【%s】。";

        String[] eachCoins = balanceLimit.split(",");
        //获取账号资金
        Map<Integer, PayUserBean> payUserBeanMap = getPayUserMap(userId);
        if (null == payUserBeanMap) {
            errorLogInfo = "Exception: %s【%s】获取账号剩余资金+冻结资金为空！";
            log.info(String.format(errorLogInfo, userName, userId));
            return;
        }

        for (String eachCoin : eachCoins) {
            String[] coins = eachCoin.split(":");
            BigDecimal minBalance = new BigDecimal(coins[1]);

            //获取资金类型
            CoinProps coinProps = DatabasesUtil.coinProps(coins[0]);

            PayUserBean payUserBean = payUserBeanMap.get(coinProps.getFundsType());
            if (null != payUserBean) {
                BigDecimal balanceFreez = payUserBean.getBalance().add(payUserBean.getFreez());
                if (balanceFreez.compareTo(minBalance) < 0) {
                    //记录错误日志，用于监控
                    log.info(String.format(errorLogInfo, userName, userId, coins[0], balanceFreez.toPlainString(), minBalance.toPlainString()));
                }
            } else {
                //记录错误日志，用于监控
                log.info(String.format(errorLogInfo, userName, userId, coins[0], BigDecimal.ZERO.toPlainString(), minBalance.toPlainString()));
            }
        }
    }

    private Map<Integer, PayUserBean> getPayUserMap(String userId) {
        //获取账号资金
        List<PayUserBean> payUserBeans = payUserDao.getFunds(userId);
        if (CollectionUtils.isEmpty(payUserBeans)) {
            return null;
        }

        Map<Integer, PayUserBean> payUserBeanMap = new HashMap<>();
        for (PayUserBean payUserBean : payUserBeans) {
            payUserBeanMap.put(payUserBean.getFundsType(), payUserBean);
        }

        return payUserBeanMap;
    }

    private String getUserNameById(int userId, String systemUserIds, String systemUserName) {
        if (systemUserIds.contains(String.valueOf(userId))) {
            return systemUserName;
        }
        return "普通用户";
    }

    /**
     * 将系统交易信息转化为量化交易信息
     * <p>
     * 需要对冲的行为和用户的行为是一样的
     *
     * @param tr
     * @return
     */
    private QtTransRecord convertTr(TransRecord tr, String market, String qtUserIds) {
        QtTransRecord qtTransRecord = new QtTransRecord();

        Integer type = Integer.parseInt(String.valueOf(tr.getTypes()));
        if (type == 0 || type == 1) {
            if (qtUserIds.contains(String.valueOf(tr.getUserIdBuy()))) { // 买单是我,则我卖
                qtTransRecord.setEntrustId(tr.getEntrustIdSell());
                qtTransRecord.setEntrustUserId(tr.getUserIdSell());
                qtTransRecord.setEntrustType(0);
                qtTransRecord.setEntrustQtUserId(tr.getUserIdBuy());
            } else if (qtUserIds.contains(String.valueOf(tr.getUserIdSell()))) { // 卖单是我,则我买
                qtTransRecord.setEntrustId(tr.getEntrustIdBuy());
                qtTransRecord.setEntrustUserId(tr.getUserIdBuy());
                qtTransRecord.setEntrustType(1);
                qtTransRecord.setEntrustQtUserId(tr.getUserIdSell());
            } else {

            }
        } else {
            log.error("订单成交类型不是买或卖, 无法执行入库操作");
            return qtTransRecord;
        }

        qtTransRecord.setTransRecordId(tr.getTransRecordId());
        qtTransRecord.setEntrustPrice(tr.getUnitPrice());
        qtTransRecord.setEntrustNum(tr.getNumbers());
        qtTransRecord.setEntrustMarket(market);
        qtTransRecord.setEntrustStatus(0);
        qtTransRecord.setAddTime(tr.getTimes());

        return qtTransRecord;

    }

    public static void main(String[] args) {
        QuantTradeWarnService test = new QuantTradeWarnService();
//        test.checkGBCHightFrequencyTrans(System.currentTimeMillis() - 60 * 60 * 1000);
//        test.checkTransRecord(0, System.currentTimeMillis());
//        test.checkBalance();
//        test.saveQtDealOrder(0, System.currentTimeMillis());
//        test.checkBackCapital(0, System.currentTimeMillis());
//        test.checkBalance();
//        test.checkGBCHightFrequencyTrans(1);
//        test.checkGBCHightFrequencyTrans(System.currentTimeMillis() - 60 * 60 * 1000);
//        test.saveQtDealOrder(System.currentTimeMillis() - 60 * 60 * 1000);
        test.checkTransRecord(0, System.currentTimeMillis());
    }

}

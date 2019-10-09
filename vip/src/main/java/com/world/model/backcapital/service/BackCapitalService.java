package com.world.model.backcapital.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redis.RedisUtil;
import com.world.cache.Cache;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.backcapital.constant.BackCapitalConst;
import com.world.model.backcapital.dao.BackCapitalConfigDao;
import com.world.model.backcapital.dao.DividendDao;
import com.world.model.backcapital.dao.DividendHistoryDao;
import com.world.model.backcapital.dao.EntrustRecordDao;
import com.world.model.backcapital.dao.PrivateKeyCoordsDao;
import com.world.model.dao.pay.DownloadDao;
import com.world.model.dao.pay.FundsDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.trace.EntrustDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.backcapital.BackCapitalConfig;
import com.world.model.entity.backcapital.BcEntrustTransRecord;
import com.world.model.entity.backcapital.Dividend;
import com.world.model.entity.backcapital.DividendHistory;
import com.world.model.entity.backcapital.PrivateKeyCoords;
import com.world.model.entity.backcapital.CountDownInfo;
import com.world.model.entity.bill.BillType;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DownloadBean;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.trace.Entrust;
import com.world.util.date.TimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author buxianguan
 * @date 2017/11/23
 */
public class BackCapitalService {
    private final static Logger logger = Logger.getLogger(BackCapitalService.class);

    private PayUserDao payUserDao = new PayUserDao();
    private DownloadDao downloadDao = new DownloadDao();
    private BackCapitalConfigDao configDao = new BackCapitalConfigDao();
    private EntrustDao entrustDao = new EntrustDao();
    private DividendDao dividendDao = new DividendDao();
    private EntrustRecordDao entrustRecordDao = new EntrustRecordDao();
    private FundsDao fundsDao = new FundsDao();
    private DividendHistoryDao dividendHistoryDao = new DividendHistoryDao();
    private PrivateKeyCoordsDao privateKeyCoordsDao = new PrivateKeyCoordsDao();

    public BackCapitalConfig getConfig() {
        BackCapitalConfig config = null;
        String configCache = Cache.Get(BackCapitalConst.BACK_CAPITAL_CONFIG_CACHE_KEY);
        if (StringUtils.isBlank(configCache)) {
            config = configDao.getConfig();
            if (null == config) {
                logger.error("从数据库里获取回购配置为空！");
                return null;
            }
            Cache.SetObj(BackCapitalConst.BACK_CAPITAL_CONFIG_CACHE_KEY, JSONObject.toJSONString(config), 60 * 60);
        } else {
            config = JSONObject.parseObject(configCache, BackCapitalConfig.class);
        }
        return config;
    }

    public void updateFeeRatio(BackCapitalConfig config, int feeRatio) {
        configDao.updateFeeRatio(feeRatio);
        //更新缓存
        config.setFeeRatio(feeRatio);
        Cache.SetObj(BackCapitalConst.BACK_CAPITAL_CONFIG_CACHE_KEY, JSONObject.toJSONString(config), 60 * 60);
    }

    /**
     * 回购账户资金处理，根据手续费50%或者100%区分是否提现和转移数量
     *
     * @param config
     * @return
     */
    public boolean userFundsTransfer(BackCapitalConfig config) {
        long nowTime = System.currentTimeMillis();

        //回购账户资产
        String bcUserId = String.valueOf(config.getBcUserId());
        PayUserBean payUser = payUserDao.getByUserIdAndFundsType(bcUserId, BackCapitalConst.BACK_CAPITAL_WITHDRAW_FUNDSTYPE);
        if (null == payUser) {
            logger.error("[回购提现] 回购账户资产为空！");
            return false;
        }

        //回购收益资金类型
        CoinProps coin = DatabasesUtil.coinProps(BackCapitalConst.BACK_CAPITAL_WITHDRAW_FUNDSTYPE);

        //回购手续费百分比，0:50% 全部分红，1:100% 一半分红一半抽奖
        int feeRatio = config.getFeeRatio();
        BigDecimal withdrawAmount = payUser.getBalance();
        BigDecimal transferAmount = BigDecimal.ZERO;
        if (feeRatio == 1) {
            withdrawAmount = withdrawAmount.divide(new BigDecimal(2), 9, BigDecimal.ROUND_DOWN);
            transferAmount = payUser.getBalance().subtract(withdrawAmount);
        }

        //最小提现额度
        BigDecimal minCash = coin.getMinCash();
        if (withdrawAmount.compareTo(minCash) <= 0) {
            //更新本次执行时间缓存
            Cache.Set(BackCapitalConst.BACK_CAPITAL_WITHDRAW_TIME_CACHE_KEY, nowTime / 1000 + "", 60 * 60);
            //更新最后一次提现金额
            Cache.SetObj(BackCapitalConst.BACK_CAPITAL_WITHDRAW_LAST_CASH_CACHE_KEY, withdrawAmount, 60 * 60);
            return true;
        }

        //提现信息
        String receiveAddr = config.getWithdrawAddress();
        Timestamp withdrawTime = new Timestamp(nowTime);

        //系统自动发起提现
        downloadDao.setCoint(coin);
        List<OneSql> sqls = downloadDao.systemDownload(bcUserId, "回购账户", withdrawAmount, withdrawTime, receiveAddr);

        //将余额转入到抽奖账号，记录bill表
        if (feeRatio == 1 && transferAmount.compareTo(BigDecimal.ZERO) > 0) {
            //回购账户转出
            sqls.addAll(fundsDao.subtractMoney(transferAmount, bcUserId, "回购账户", BillType.InnerAccountOut.getValue(),
                    BillType.InnerAccountOut.getKey(), BackCapitalConst.BACK_CAPITAL_WITHDRAW_FUNDSTYPE, BigDecimal.ZERO, "0", true));
            //抽奖账户转入
            String luckyUserId = String.valueOf(config.getLuckyUserId());
            sqls.addAll(fundsDao.addMoney(transferAmount, luckyUserId, "抽奖账户", BillType.InnerAccountIn.getValue(),
                    BillType.InnerAccountIn.getKey(), BackCapitalConst.BACK_CAPITAL_WITHDRAW_FUNDSTYPE, BigDecimal.ZERO, "0", true));
        }

        if (Data.doTrans(sqls)) {
            UserCache.resetUserFundsFromDatabase(bcUserId);
            //更新本次执行时间缓存
            Cache.Set(BackCapitalConst.BACK_CAPITAL_WITHDRAW_TIME_CACHE_KEY, nowTime / 1000 + "", 60 * 60);
            //更新最后一次提现金额
            Cache.SetObj(BackCapitalConst.BACK_CAPITAL_WITHDRAW_LAST_CASH_CACHE_KEY, withdrawAmount, 60 * 60);
            //更新平台累积提现总量
            BigDecimal totalCash = downloadDao.getTotalCash(bcUserId);
            Cache.SetObj(BackCapitalConst.BACK_CAPITAL_WITHDRAW_CASH_CACHE_KEY, totalCash, 60 * 60);
            return true;
        } else {
            logger.error("[回购提现] 事务执行失败! oneSqls: " + JSONObject.toJSONString(sqls));
        }

        return false;
    }

    /**
     * 根据频率和上次时间计算倒计时
     */
    public long getCountDown(int frequency, String cacheKey) {
        long countDown = 0;
        long lastTime = 0;
        String lastTimeCache = Cache.Get(cacheKey);
        if (StringUtils.isBlank(lastTimeCache)) {
            countDown = frequency;
        } else {
            lastTime = Long.parseLong(lastTimeCache);
            //倒计时多加1秒，防止回购定时任务执行耗时久，没刷出结果
            countDown = frequency - (System.currentTimeMillis() / 1000 - lastTime) + 1;
            if (countDown < 0) {
                countDown = 0;
            }
            if (countDown > frequency) {
                countDown = frequency;
            }
        }
        return countDown;
    }

    /**
     * 获取最近一次回购资金，如果是0，读取最近一次非0数据
     */
    public BigDecimal getLastBackCapital(String bcUserId) {
        BigDecimal lastBackCapital = (BigDecimal) Cache.GetObj(BackCapitalConst.BACK_CAPITAL_LAST_CAPITAL_CACHE_KEY);
        if (null == lastBackCapital) {
            lastBackCapital = BigDecimal.ZERO;
            List<Entrust> entrustCache = getEntrustsFromCacheOrder();
            if (CollectionUtils.isEmpty(entrustCache)) {
                Entrust entrust = entrustDao.getOneEntrustByUser(bcUserId, BackCapitalConst.BACK_CAPITAL_DB_NAME);
                if (null != entrust) {
                    lastBackCapital = entrust.getCompleteTotalMoney();
                    Cache.SetObj(BackCapitalConst.BACK_CAPITAL_LAST_CAPITAL_CACHE_KEY, lastBackCapital, 60);
                }
            } else {
                lastBackCapital = entrustCache.get(0).getCompleteTotalMoney();
            }
        }
        return lastBackCapital;
    }

    /**
     * 获取最近一次回购量，如果是0，就读取0
     */
    public BigDecimal getLastBackCapitalNumbers() {
        BigDecimal lastNumbers = (BigDecimal) Cache.GetObj(BackCapitalConst.BACK_CAPITAL_LAST_NUMBER_CACHE_KEY);
        if (null == lastNumbers) {
            lastNumbers = BigDecimal.ZERO;
            BcEntrustTransRecord record = entrustRecordDao.getLastEntrust();
            if (null != record) {
                lastNumbers = record.getCompleteNumber();
            }
            Cache.SetObj(BackCapitalConst.BACK_CAPITAL_LAST_NUMBER_CACHE_KEY, lastNumbers, 60);
        }
        return lastNumbers;
    }

    /**
     * 获取平台能提现的余额
     */
    public BigDecimal getWithdrawAmount(BackCapitalConfig config) {
        String bcUserId = String.valueOf(config.getBcUserId());

        BigDecimal withdrawAmount = BigDecimal.ZERO;
        //从缓存中获取用户资金
        JSONArray funds = UserCache.getUserFunds(bcUserId);
        for (int i = 0; i < funds.size(); i++) {
            JSONObject fund = funds.getJSONObject(i);
            Integer fundsType = fund.getInteger("fundsType");
            if (null != fundsType && fundsType == BackCapitalConst.BACK_CAPITAL_WITHDRAW_FUNDSTYPE) {
                withdrawAmount = fund.getBigDecimal("balance");
                break;
            }
        }

        //回购手续费百分比，0:50% 全部分红，1:100% 一半分红一半抽奖
        int feeRatio = config.getFeeRatio();
        if (feeRatio == 1) {
            withdrawAmount = withdrawAmount.divide(new BigDecimal(2), 9, BigDecimal.ROUND_DOWN);
        }

        return withdrawAmount;
    }

    /**
     * 获取平台累积提现总量
     */
    public BigDecimal getHistoryWithdrawAmount(BackCapitalConfig config) {
        BigDecimal totalCash = (BigDecimal) Cache.GetObj(BackCapitalConst.BACK_CAPITAL_WITHDRAW_CASH_CACHE_KEY);
        if (null == totalCash) {
            String bcUserId = String.valueOf(config.getBcUserId());
            CoinProps coin = DatabasesUtil.coinProps(BackCapitalConst.BACK_CAPITAL_WITHDRAW_FUNDSTYPE);
            downloadDao.setCoint(coin);
            totalCash = downloadDao.getTotalCash(bcUserId);
            Cache.SetObj(BackCapitalConst.BACK_CAPITAL_WITHDRAW_CASH_CACHE_KEY, totalCash, 60 * 60);
        }
        return totalCash;
    }

    /**
     * 获取平台最近一次提现金额
     */
    public BigDecimal getLastWithdrawAmount(BackCapitalConfig config) {
        BigDecimal lastCash = (BigDecimal) Cache.GetObj(BackCapitalConst.BACK_CAPITAL_WITHDRAW_LAST_CASH_CACHE_KEY);
        if (null == lastCash) {
            String bcUserId = String.valueOf(config.getBcUserId());
            CoinProps coin = DatabasesUtil.coinProps(BackCapitalConst.BACK_CAPITAL_WITHDRAW_FUNDSTYPE);
            downloadDao.setCoint(coin);
            DownloadBean downloadBean = downloadDao.getUserLastDownload(bcUserId);
            if (null != downloadBean) {
                lastCash = downloadBean.getAmount();
            } else {
                lastCash = BigDecimal.ZERO;
            }
            Cache.SetObj(BackCapitalConst.BACK_CAPITAL_WITHDRAW_LAST_CASH_CACHE_KEY, lastCash, 60 * 60);
        }
        return lastCash;
    }

    /**
     * 获取平台最近一次提现时间
     */
    private long getLastWithdrawTime(BackCapitalConfig config) {
        long lastTime = 0;
        String lastTimeCache = Cache.Get(BackCapitalConst.BACK_CAPITAL_WITHDRAW_TIME_CACHE_KEY);
        if (StringUtils.isBlank(lastTimeCache)) {
            String bcUserId = String.valueOf(config.getBcUserId());
            CoinProps coin = DatabasesUtil.coinProps(BackCapitalConst.BACK_CAPITAL_WITHDRAW_FUNDSTYPE);
            downloadDao.setCoint(coin);
            DownloadBean downloadBean = downloadDao.getUserLastDownload(bcUserId);
            if (null != downloadBean) {
                lastTime = downloadBean.getSubmitTime().getTime() / 1000;
//                lastTime = TimeUtil.getTimeBegin(downloadBean.getSubmitTime()).getTime() / 1000;
            } else {
                //如果是刚上线，默认上次转出时间为本周一+配置时间，便于计算下次转出时间
                Date monday = TimeUtil.getMondayOFWeekDate();
                String sp[] = config.getWithdrawFrequency().split(":");
                long timeSeconds = Integer.parseInt(sp[0]) * 60 * 60 + Integer.parseInt(sp[1]) * 60;
                lastTime = monday.getTime() / 1000 + timeSeconds;
            }
            Cache.Set(BackCapitalConst.BACK_CAPITAL_WITHDRAW_TIME_CACHE_KEY, lastTime + "", 60 * 60);
        } else {
            lastTime = Long.parseLong(lastTimeCache);
        }
        return lastTime;
    }

    /**
     * 获取分红汇总信息
     */
    public Dividend getDividend() {
        Dividend dividendInfo = (Dividend) Cache.GetObj(BackCapitalConst.BACK_CAPITAL_DIVIDEND_INFO_CACHE_KEY);
        if (null == dividendInfo) {
            dividendInfo = dividendDao.getDividendInfo();
            if (null != dividendInfo) {
                Cache.SetObj(BackCapitalConst.BACK_CAPITAL_DIVIDEND_INFO_CACHE_KEY, dividendInfo, 60 * 60);
            }
        }
        return dividendInfo;
    }

    /**
     * 获取最近一次分红信息
     */
    public DividendHistory getLastDividendHistory() {
        DividendHistory dividendHistory = (DividendHistory) Cache.GetObj(BackCapitalConst.BACK_CAPITAL_DIVIDEND_HISTORY_INFO_CACHE_KEY);
        if (null == dividendHistory) {
            dividendHistory = dividendHistoryDao.getLastDividendHistory();
            Cache.SetObj(BackCapitalConst.BACK_CAPITAL_DIVIDEND_HISTORY_INFO_CACHE_KEY, dividendHistory, 60 * 60);
        }
        return dividendHistory;
    }

    /**
     * 获取累积分红量
     *
     * @return
     */
    public BigDecimal getTotalDividendAmout() {
        BigDecimal totalAmount = (BigDecimal) Cache.GetObj(BackCapitalConst.BACK_CAPITAL_DIVIDEND_TOTAl_AMOUNT_INFO_CACHE_KEY);
        if (null == totalAmount) {
            totalAmount = dividendHistoryDao.getTotalDividendAmount();
            Cache.SetObj(BackCapitalConst.BACK_CAPITAL_DIVIDEND_TOTAl_AMOUNT_INFO_CACHE_KEY, totalAmount, 60 * 60);
        }
        return totalAmount;
    }

    /**
     * 获取全球持股私钥分布坐标，格式为：
     */
    public String getCoordsList() {
        String coordsListCache = Cache.Get(BackCapitalConst.GBC_PRIVATE_KEY_COORDS_LIST_CACHE_KEY);
        if (StringUtils.isBlank(coordsListCache)) {
            List<String> strCoordsList = new ArrayList<>();
            List<PrivateKeyCoords> coordsList = privateKeyCoordsDao.getCoordsList();
            for (PrivateKeyCoords coords : coordsList) {
                strCoordsList.add(coords.getXy());
            }
            coordsListCache = JSONObject.toJSONString(strCoordsList);
            Cache.SetObj(BackCapitalConst.GBC_PRIVATE_KEY_COORDS_LIST_CACHE_KEY, coordsListCache, 60 * 60);
        }
        return coordsListCache;
    }

    /**
     * 分红倒计时时间，单位秒，规则为：默认当月月底，如果上一次分红不超过本月15号，默认为本月月底，超过后，默认为下一个月月底
     */
    public CountDownInfo dividendCountDown(String withdrawFrequency) {
        long nowTime = System.currentTimeMillis() / 1000;
        long thisTime = 0;
        long frequency = 0;

        String sp[] = withdrawFrequency.split(":");
        long timeSeconds = (Integer.parseInt(sp[0]) + 1) * 60 * 60 + Integer.parseInt(sp[1]) * 60;

        DividendHistory dividendHistory = getLastDividendHistory();
        if (dividendHistory.getId() == 0) {
            //如果一次都没分红，分红时间为本月最后一天+配置的平台转出时间+1小时
            thisTime = TimeUtil.getMonthLastZero() / 1000 + timeSeconds;
            //分红频率，时间为一个月，转成分钟
            Calendar cal = Calendar.getInstance();
            frequency = cal.getActualMaximum(Calendar.DAY_OF_MONTH) * 24 * 60;
        } else {
            //获取上一次分红时间
            long time = dividendHistory.getTime();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);

            //获取天数
            int days = cal.get(Calendar.DAY_OF_MONTH);
            if (days < 15) {
                //本次分红时间，默认上一次分红月底
                thisTime = TimeUtil.getMonthLastZeroByCal(cal) / 1000 + timeSeconds;
            } else {
                //本次分红时间为上一次分红下个月月底
                cal.add(Calendar.MONTH, 1);
                thisTime = TimeUtil.getMonthLastZeroByCal(cal) / 1000 + timeSeconds;
            }

            frequency = (thisTime - time / 1000) / 60;
        }

        long countDown = (thisTime - nowTime) / 60;
        return new CountDownInfo(frequency, countDown);
    }

    /**
     * 获取提现倒计时信息
     *
     * @param config
     * @return
     */
    public CountDownInfo getWithdrawCountDown(BackCapitalConfig config) {
        //获取上次转出时间
        long lastTime = getLastWithdrawTime(config);

        //上次转出时间的凌晨时间
        long lastZeroTime = TimeUtil.getTimeBegin(lastTime * 1000) / 1000;

        //配置时间转成秒
        String sp[] = config.getWithdrawFrequency().split(":");
        long timeSeconds = Integer.parseInt(sp[0]) * 60 * 60 + Integer.parseInt(sp[1]) * 60;

        //平台下次转出时间为上次转出时间后推7天，时间为配置的时间
        long thisTime = lastZeroTime + 7 * 24 * 60 * 60 + timeSeconds;
//        if (thisTime <= lastTime) {
//            thisTime = thisTime + 1 * 24 * 60 * 60;
//        }

        //获取本月最后一天的时间
        long monthLastDay = TimeUtil.getMonthLastZero() / 1000;
        //如果上次转出时间是本月最后一天的时间，说明月底已经转过了，7天后再转
        if (lastZeroTime < monthLastDay && thisTime > (monthLastDay + timeSeconds)) {
            thisTime = monthLastDay + timeSeconds;
        }

        long frequency = thisTime - lastTime;
        long countDown = thisTime - System.currentTimeMillis() / 1000;

        return new CountDownInfo(frequency, countDown);
    }

    /**
     * 保存分红信息
     *
     * @param balance
     * @param totalShareCount
     */
    public void saveDividend(BigDecimal balance, int totalShareCount) {
        if (null != balance) {
            dividendDao.updateBalance(balance);
        }
        if (totalShareCount >= 0) {
            dividendDao.updateShareCount(totalShareCount);
        }

        //更新缓存
        Dividend dividendInfo = dividendDao.getDividendInfo();
        if (null != dividendInfo) {
            Cache.SetObj(BackCapitalConst.BACK_CAPITAL_DIVIDEND_INFO_CACHE_KEY, dividendInfo, 60 * 60);
        }
    }

    public void saveDividendHistory(String uniqueKey, BigDecimal amount, int shareCount, long time) {
        DividendHistory dividendHistory = dividendHistoryDao.getByUniqueKey(uniqueKey);
        if (null == dividendHistory) {
            int lastNumber = dividendHistoryDao.getLastNumber();
            int thisNumber = lastNumber + 1;
            dividendHistoryDao.insert(uniqueKey, thisNumber, amount, shareCount, time);
        } else {
            dividendHistoryDao.update(uniqueKey, amount, shareCount, time);
        }

        //更新缓存
        dividendHistory = dividendHistoryDao.getLastDividendHistory();
        Cache.SetObj(BackCapitalConst.BACK_CAPITAL_DIVIDEND_HISTORY_INFO_CACHE_KEY, dividendHistory, 60 * 60);

        BigDecimal totalAmount = dividendHistoryDao.getTotalDividendAmount();
        Cache.SetObj(BackCapitalConst.BACK_CAPITAL_DIVIDEND_TOTAl_AMOUNT_INFO_CACHE_KEY, totalAmount, 60 * 60);
    }

    public Entrust getEntrustFromCache(long entrustId) {
        Map<Long, Entrust> entrustMap = getEntrustMapFromCache();
        return entrustMap.get(entrustId);
    }

    public List<Entrust> getEntrustsFromCache() {
        JSONArray records = getRecordsFromCache();
        return transferToEntrusts(records);
    }

    public List<Entrust> getEntrustsFromCacheOrder() {
        JSONArray records = getRecordsFromCache();
        List<Entrust> entrusts = transferToEntrusts(records);
        if (CollectionUtils.isNotEmpty(entrusts)) {
            Collections.sort(entrusts, new Comparator<Entrust>() {
                @Override
                public int compare(Entrust o1, Entrust o2) {
                    return Long.compare(o2.getEntrustId(), o1.getEntrustId());
                }
            });
        }
        return entrusts;
    }

    private Map<Long, Entrust> getEntrustMapFromCache() {
        JSONArray records = getRecordsFromCache();
        return transferToEntrustMap(records);
    }

    private List<Entrust> transferToEntrusts(JSONArray records) {
        Set<Long> entrustSet = new HashSet<>();
        List<Entrust> result = new ArrayList<>();
        for (int j = 0; j < records.size(); j++) {
            JSONArray array = (JSONArray) records.get(j);
            if (array.getBigDecimal(4).compareTo(BigDecimal.ZERO) > 0) {
                long entrustId = array.getLong(0);
                if (entrustSet.contains(entrustId)) {
                    continue;
                }
                Entrust entrust = new Entrust();
                entrust.setEntrustId(entrustId);
                entrust.setCompleteNumber(array.getBigDecimal(3));
                entrust.setCompleteTotalMoney(array.getBigDecimal(4));
                entrust.setSubmitTime(array.getLong(6));
                result.add(entrust);
                entrustSet.add(entrustId);
            }
        }
        return result;
    }

    private Map<Long, Entrust> transferToEntrustMap(JSONArray records) {
        Map<Long, Entrust> entrustMap = new HashMap<>();
        for (int j = 0; j < records.size(); j++) {
            JSONArray array = (JSONArray) records.get(j);
            if (array.getBigDecimal(4).compareTo(BigDecimal.ZERO) > 0) {
                long entrustId = array.getLong(0);
                if (entrustMap.containsKey(entrustId)) {
                    continue;
                }
                Entrust entrust = new Entrust();
                entrust.setEntrustId(entrustId);
                entrust.setCompleteNumber(array.getBigDecimal(3));
                entrust.setCompleteTotalMoney(array.getBigDecimal(4));
                entrust.setSubmitTime(array.getLong(6));
                entrustMap.put(entrust.getEntrustId(), entrust);
            }
        }
        return entrustMap;
    }

    private JSONArray getRecordsFromCache() {
        JSONArray records = new JSONArray();

        BackCapitalConfig config = getConfig();
        if (null == config) {
            return records;
        }

        String data = RedisUtil.get(BackCapitalConst.BACK_CAPITAL_MARKET + "_userrecord_" + config.getBcUserId());
        if (StringUtils.isBlank(data)) {
            return records;
        }

        JSONObject json = JSONObject.parseObject("{" + data + "}");
        JSONArray status2Record = json.getJSONArray("hrecord");
        JSONArray status3Record = json.getJSONArray("record");


        if (null != status2Record && status2Record.size() > 0) {
            records.addAll(status2Record);
        }
        if (null != status3Record && status3Record.size() > 0) {
            records.addAll(status3Record);
        }

        return records;
    }

    public static void main(String[] args) {
//        String sss = "13:00";
//        String sss2 = "00:40";
//        Date monday = TimeUtil.getMondayOFWeekDate();
//        String sp[] = sss.split(":");
//        long timeSeconds = Integer.parseInt(sp[0]) * 60 * 60 + Integer.parseInt(sp[1]) * 60;
//        long lastTime = monday.getTime() / 1000 + timeSeconds;
//
//        lastTime = System.currentTimeMillis() / 1000;
//
//        //上次转出时间的凌晨时间
//        long lastZeroTime = TimeUtil.getTimeBegin(lastTime * 1000) / 1000;
//
//
//        String sp2[] = sss2.split(":");
//        long timeSeconds2 = Integer.parseInt(sp2[0]) * 60 * 60 + Integer.parseInt(sp2[1]) * 60;
//        //平台下次转出时间为上次转出时间后推7天，时间为配置的时间
//        long thisTime = lastZeroTime + 0 * 24 * 60 * 60 + timeSeconds2;
//
//
//        //获取本月最后一天的时间
//        long monthLastDay = TimeUtil.getMonthLastZero() / 1000 + timeSeconds2;
//        if (thisTime > monthLastDay) {
//            thisTime = monthLastDay;
//        }
//
//        long frequency = thisTime - lastTime;
//
//        long countDown = thisTime - System.currentTimeMillis() / 1000;
//        System.out.println(frequency);
//        System.out.println(countDown);
//        Cache.Delete(BackCapitalConst.BACK_CAPITAL_DIVIDEND_HISTORY_INFO_CACHE_KEY);

        BigDecimal withdrawAmount = new BigDecimal("0.22");
        withdrawAmount = withdrawAmount.divide(new BigDecimal(2), 9, BigDecimal.ROUND_DOWN);
        System.out.println(withdrawAmount.toPlainString());
    }


}

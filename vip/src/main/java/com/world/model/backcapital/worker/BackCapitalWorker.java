package com.world.model.backcapital.worker;

import com.alibaba.fastjson.JSONObject;
import com.tenstar.HTTPTcp;
import com.tenstar.Message;
import com.world.cache.Cache;
import com.world.data.id.IdWorkerUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.backcapital.constant.BackCapitalConst;
import com.world.model.backcapital.dao.EntrustRecordDao;
import com.world.model.backcapital.service.BackCapitalService;
import com.world.model.dao.fee.FeeDao;
import com.world.model.dao.pay.FundsDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.task.Worker;
import com.world.model.dao.trace.EntrustDao;
import com.world.model.entity.Market;
import com.world.model.entity.backcapital.BackCapitalConfig;
import com.world.model.entity.backcapital.BcEntrustTransRecord;
import com.world.model.entity.bill.BillType;
import com.world.model.entity.financial.fee.Fee;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.trace.Entrust;
import com.world.model.loan.worker.LoanAutoFactory;
import com.world.util.callback.AsynMethodFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 回购功能定时任务
 * Created by buxianguan on 17/8/4.
 */
public class BackCapitalWorker extends Worker {
    private final static Logger logger = Logger.getLogger(BackCapitalWorker.class);
    private final static Logger alarmLogger = Logger.getLogger("alarmAll");

    private PayUserDao payUserDao = new PayUserDao();
    private FeeDao feeDao = new FeeDao();
    private FundsDao fundsDao = new FundsDao();
    private EntrustRecordDao entrustRecordDao = new EntrustRecordDao();
    private EntrustDao entrustDao = new EntrustDao();
    private BackCapitalService backCapitalService = new BackCapitalService();

    private volatile boolean running = false;

    public BackCapitalWorker() {
    }

    public BackCapitalWorker(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
        try {
            super.run();

            if (running) {
                logger.info("[回购功能] 上一个定时任务还没有执行完毕,等待下一个轮询");
                return;
            }
            running = true;

            //获取市场配置
            JSONObject market = Market.getMarketByName(BackCapitalConst.BACK_CAPITAL_MARKET);
            if (null == market) {
                logger.error("[回购功能] " + BackCapitalConst.BACK_CAPITAL_MARKET + " 市场配置信息为空！");
                return;
            }
            String dbName = market.getString("db");

            //获取回购参数配置
            BackCapitalConfig config = backCapitalService.getConfig();
            if (null == config) {
                logger.error("[回购功能] 从数据库获取回购配置信息为空！");
                return;
            }

            if (config.getBcTaskStatus() != 1) {
                logger.info("[回购功能] 回购任务为非启动状态，不回购");
                return;
            }

            //回购账户userId
            String bcUserId = String.valueOf(config.getBcUserId());
            //回购执行频率
            int frequency = config.getBcFrequency();
            //回购手续费百分比
            int feeRatio = config.getFeeRatio();
            BigDecimal feePercent = new BigDecimal(feeRatio == 0 ? 0.5 : 1);

            //计算上次执行时间间隔是否超过执行频率，如果缓存为空，从数据库中获取
            long lastTime = 0L;
            String lastTimeCache = Cache.Get(BackCapitalConst.BACK_CAPITAL_TIME_CACHE_KEY);
            if (StringUtils.isBlank(lastTimeCache)) {
                BcEntrustTransRecord record = entrustRecordDao.getLastEntrust();
                if (null != record) {
                    lastTime = record.getEntrustTime() / 1000;
                    Cache.Set(BackCapitalConst.BACK_CAPITAL_TIME_CACHE_KEY, lastTime + "");
                }
            } else {
                lastTime = Long.parseLong(lastTimeCache);
            }

            if ((System.currentTimeMillis() / 1000 - lastTime) < frequency) {
                return;
            }

            logger.info("[回购功能] 回购功能开始...");

            JSONObject prices = LoanAutoFactory.getPrices();
            //回购手续费汇总
            FeeSummaryResult feeSummaryResult = feeSummary(prices);
            if (null == feeSummaryResult) {
                return;
            }
            BigDecimal usdtAmount = feeSummaryResult.usdtAmount;

            //回购资金乘以手续费比例
            usdtAmount = usdtAmount.multiply(feePercent).setScale(9, BigDecimal.ROUND_DOWN);
            ;
            //回购手续费记录之和
            BigDecimal feeUsdtAmount = usdtAmount;
            logger.info("[回购功能] fee表同步的回购手续费转换成usdt金额, usdtAmount:" + usdtAmount);

            //监控回购账户可用资金余额，少于配置的预警值报警，默认200个usdt
            PayUserBean payUser = payUserDao.getByUserIdAndFundsType(bcUserId, BackCapitalConst.BACK_CAPITAL_FUNDSTYPE);
            if (null == payUser) {
                logger.error("[回购功能] 回购账号usdt资金不存在！");
                return;
            }
            BigDecimal baseBalance = config.getBaseBalance();
            BigDecimal userBalance = payUser.getBalance().add(payUser.getFreez());
            if (userBalance.compareTo(baseBalance) < 0) {
                if (userBalance.compareTo(baseBalance.subtract(BigDecimal.TEN)) < 0) {
                    logger.error("[回购功能] 回购账户余额小于打底金额超过10个USDT! baseBalance:" + baseBalance.toPlainString() +
                            ",userBalance:" + userBalance.toPlainString());

                    alarmLogger.info("Exception: [回购功能] 回购账户余额小于打底金额超过10个USDT，请运营同学关注，如果持续报警请及时充值! 打底金额:" + baseBalance.toPlainString() +
                            ",回购账户余额:" + userBalance.toPlainString());
                }

                //回购资金减去回购账户余额跟打底金额的差额
                usdtAmount = usdtAmount.subtract(baseBalance.subtract(userBalance));
                logger.info("[回购功能] 回购手续费减去回购账户余额跟打底金额的差额后的金额, usdtAmount:" + usdtAmount);
            }

            //判断是否小于最小成交金额
            BigDecimal minAmount = market.getBigDecimal("minAmount");
            if (null != minAmount && minAmount.compareTo(BigDecimal.ZERO) > 0 && usdtAmount.compareTo(minAmount) < 0) {
                logger.info("[回购功能] 成交金额小于系统规定金额！minAmount:" + minAmount.toPlainString() + ",totalAmount:" + usdtAmount.toPlainString());
                noEntrustHandle();
                return;
            }

            //根据回购资金计算回购价格和数量
            BigDecimal[] priceAndNumbers = getPriceNumberByAmount(usdtAmount, dbName);
            if (null == priceAndNumbers) {
                noEntrustHandle();
                return;
            }

            logger.info("[回购功能] 计算出回购价格和数量, priceAndNumbers:" + JSONObject.toJSONString(priceAndNumbers));

            BigDecimal price = priceAndNumbers[0];
            BigDecimal numbers = priceAndNumbers[1];
            if (price.compareTo(BigDecimal.ZERO) <= 0 || numbers.compareTo(BigDecimal.ZERO) <= 0) {
                logger.error("[回购功能] 未定位到能成交掉回购金额的卖盘数量和价格，本次不回购！");
                alarmLogger.info("Exception: [回购功能] 未定位到能成交掉回购金额的卖盘数量和价格，本次不回购，请运营同学关注，如果持续报警请及时卖盘挂单，保证回购正常运行！回购金额：" + usdtAmount.toPlainString());
                noEntrustHandle();
                return;
            }

            //数量按照币种配置的数量小数点位数取 DOWN
            numbers = numbers.setScale(market.getIntValue("numberBixDian"), RoundingMode.DOWN);

            //判断是否小于最小成交数量
            BigDecimal bixMinNum = market.getBigDecimal("bixMinNum");
            if (null != bixMinNum && bixMinNum.compareTo(BigDecimal.ZERO) > 0 && numbers.compareTo(bixMinNum) < 0) {
                logger.info("[回购功能] 成交数量小于系统规定数量！bixMinNum:" + bixMinNum.toPlainString() + ",numbers:" + numbers.toPlainString());
                noEntrustHandle();
                return;
            }

            /* start by kinghao 20181122 添加最大交易量验证*/
            //判断是否大于最大成交数量
            BigDecimal bixMaxNum = market.getBigDecimal("bixMaxNum");
            if (null != bixMaxNum && numbers.compareTo(bixMaxNum) > 0) {
                logger.info("[回购功能] 成交数量大于系统规定数量！bixMaxNum:" + bixMaxNum.toPlainString() + ",numbers:" + numbers.toPlainString());
                noEntrustHandle();
                return;
            }
            /*end*/

            //再次判断是否小于最小成交金额，因为有数量的截取
            if (null != minAmount && minAmount.compareTo(BigDecimal.ZERO) > 0 && price.multiply(numbers).compareTo(minAmount) < 0) {
                logger.info("[回购功能] 成交金额小于系统规定金额！minAmount:" + minAmount.toPlainString() + ",totalAmount2:" + price.multiply(numbers).toPlainString());
                noEntrustHandle();
                return;
            }

            //如果回购数量超过50个GBC，运营报警
            if (numbers.compareTo(new BigDecimal(50)) > 0) {
                alarmLogger.info("Exception: [回购功能] 本次回购的数量超过50GBC，请运营同学关注，具体回购数量:" + numbers.toPlainString());
            }

            //处理用户财务信息,添加bill
            List<OneSql> sqls = fundsDao.addMoney(feeUsdtAmount, bcUserId, "回购账户", BillType.backCapitalInAccount.getValue(),
                    BillType.backCapitalInAccount.getKey(), BackCapitalConst.BACK_CAPITAL_FUNDSTYPE, BigDecimal.ZERO, "0", true);
            //修改fee操作状态
            sqls.add(feeDao.updateFlagDone(feeSummaryResult.maxFeeId));
            if (Data.doTrans(sqls)) {
                //下买单
                EntrustResult entrustResult = buy(market, bcUserId, price, numbers);

                long entrustId = entrustResult.entrustId;
                long batchId = IdWorkerUtil.getId();
                BigDecimal amount = price.multiply(numbers);
                Set<Long> userIdsSet = new HashSet<>();
                long entrustTime = System.currentTimeMillis();

                if (entrustResult.code == 100) {
                    //委托成功
                    //插入当前批次手续费transRecord记录和委托的对应关系
                    for (Fee fee : feeSummaryResult.feeList) {
                        BcEntrustTransRecord entrustRecord = transferByFee(fee, batchId, entrustId, amount, numbers, feeUsdtAmount,
                                entrustTime, feeSummaryResult.btcUsdtPrice, feePercent);
                        entrustRecordDao.insert(entrustRecord);
                        userIdsSet.add(fee.getUserId());
                    }
                } else {
                    //委托失败
                    //如果不是成交金额和成交数量不足，日志报警
                    if (entrustResult.code != 140 && entrustResult.code != 141) {
                        logger.error("[回购功能] 委托买单失败，错误信息非成交金额和数量不足，需要关注！entrustResult:" + JSONObject.toJSONString(entrustResult));
                    }

                    //插入一条0资金的记录占位
                    amount = BigDecimal.ZERO;
                    numbers = BigDecimal.ZERO;
                    BcEntrustTransRecord thisRecord = new BcEntrustTransRecord();
                    thisRecord.setBatchId(batchId);
                    thisRecord.setEntrustId(0);
                    thisRecord.setCompleteTotalMoney(amount);
                    thisRecord.setCompleteNumber(numbers);
                    thisRecord.setEntrustTime(entrustTime);
                    entrustRecordDao.insert(thisRecord);

                    //回购账户资金减去手续费
                    List<OneSql> backSqls = fundsDao.subtractMoney(feeUsdtAmount, bcUserId, "回购账户", BillType.backCapitalOutAccount.getValue(),
                            BillType.backCapitalOutAccount.getKey(), BackCapitalConst.BACK_CAPITAL_FUNDSTYPE, BigDecimal.ZERO, "0", true);
                    //fee表flag状态恢复0，用于下次回购统计
                    backSqls.add(feeDao.updateFlagUnDone(feeSummaryResult.minFeeId, feeSummaryResult.maxFeeId));
                    if (!Data.doTrans(backSqls)) {
                        logger.error("[回购功能] 回购账户还原资金和fee表恢复flag状态事务执行失败！资金:" + feeUsdtAmount + ",minFeeId:" + feeSummaryResult.minFeeId
                                + ",maxFeeId:" + feeSummaryResult.maxFeeId);
                    }
                }

                //更新缓存
                updateCache(userIdsSet, entrustTime, numbers);
            } else {
                logger.error("[回购功能] 更新回购账户财务信息和修改fee表状态事务执行失败！");
                noEntrustHandle();
            }

            logger.info("[回购功能] 回购功能结束...");
        } catch (Exception e) {
            logger.error("[回购功能] 执行异常！", e);
        } finally {
            running = false;
        }
    }

    /**
     * 汇总手续费记录，包含币种转换usdt逻辑
     */
    private FeeSummaryResult feeSummary(JSONObject prices) {
        //查询当前fee表用于回购记录最大的id，用于后续的update
        Fee maxIdFee = feeDao.getBackCapitalMaxId();
        if (null == maxIdFee || maxIdFee.getId() <= 0) {
            logger.info("[回购功能] fee表没有需要同步的回购手续费");
            noEntrustHandle();
            return null;
        }

        //查询maxId下的未处理的回购手续费记录
        List<Fee> fees = feeDao.getBackCapitalFeeByMaxId(maxIdFee.getId());
        if (CollectionUtils.isEmpty(fees)) {
            logger.info("[回购功能] fee表没有需要同步的回购手续费");
            noEntrustHandle();
            return null;
        }

        int minFeeId = maxIdFee.getId();
        BigDecimal usdtAmount = BigDecimal.ZERO;

        //将手续费按照成交记录聚合，用于记录买卖双方
        Map<String, List<Fee>> transRecordFeeMap = new HashMap<>();
        for (Fee fee : fees) {
            List<Fee> feeList = transRecordFeeMap.get(fee.getTransRecordId() + "_" + fee.getMarket());
            if (CollectionUtils.isEmpty(feeList)) {
                feeList = new ArrayList<>();
            }
            feeList.add(fee);
            transRecordFeeMap.put(fee.getTransRecordId() + "_" + fee.getMarket(), feeList);

            if (fee.getId() < minFeeId) {
                minFeeId = fee.getId();
            }
        }

        logger.info("[回购功能] fee表同步的回购手续费, minFeeId:" + minFeeId + ", maxFeeId:" + maxIdFee.getId());

        //累加，把btc按照市价转换成usdt
        BigDecimal btcUsdtPrice = prices.getBigDecimal("btc_usdt");
        logger.info("[回购功能] btc转换成usdt的市价，btcUsdtPrice:" + btcUsdtPrice);

        //处理双方手续费折算
        List<Fee> transRecordFees = new ArrayList<>();
        for (Map.Entry<String, List<Fee>> entry : transRecordFeeMap.entrySet()) {
            List<Fee> everyTransRecordFees = entry.getValue();
            if (everyTransRecordFees.size() > 2) {
                continue;
            }

            //usdt或者btc一方手续费信息
            Fee baseFee = null;
            //另一方手续费信息
            Fee anotherFee = null;
            for (Fee fee : everyTransRecordFees) {
                if ("btc".equalsIgnoreCase(fee.getNumberBi()) && "usdt".equalsIgnoreCase(fee.getExchangeBi())) {
                    if ("usdt".equalsIgnoreCase(fee.getCurrency())) {
                        baseFee = fee;
                    } else {
                        anotherFee = fee;
                    }
                } else {
                    if ("usdt".equalsIgnoreCase(fee.getCurrency()) || "btc".equalsIgnoreCase(fee.getCurrency())) {
                        baseFee = fee;
                    } else {
                        anotherFee = fee;
                    }
                }
            }

            boolean hasHandle = false;
            //如果买卖双方都有手续费，并且能取到双方vip等级，则根据usdt或者btc一方的手续费计算另外一方手续费，累积
            if (null != baseFee && null != anotherFee) {
                BigDecimal baseFeeDiscount = (BigDecimal) Cache.GetObj("user_vip_fee_discount_" + baseFee.getUserId());
                BigDecimal anotherFeeDiscount = (BigDecimal) Cache.GetObj("user_vip_fee_discount_" + anotherFee.getUserId());
//                BigDecimal baseFeeDiscount = new BigDecimal("0.8");
//                BigDecimal anotherFeeDiscount = new BigDecimal("0.6");
                logger.info("[回购功能] usdt或者btc一方手续费折扣信息, userId:" + baseFee.getUserId() + ", feeDiscount:" + baseFeeDiscount
                        + "; 另外一方手续费折扣信息, userId:" + anotherFee.getUserId() + ", feeDiscount:" + anotherFeeDiscount);
                if (null != baseFeeDiscount && baseFeeDiscount.compareTo(BigDecimal.ZERO) > 0
                        && null != anotherFeeDiscount && anotherFeeDiscount.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal baseAmount = baseFee.getAmount();
                    BigDecimal anotherAmount = baseAmount.divide(baseFeeDiscount, 9, BigDecimal.ROUND_DOWN)
                            .multiply(anotherFeeDiscount).setScale(9, BigDecimal.ROUND_DOWN);

                    if ("usdt".equalsIgnoreCase(baseFee.getCurrency())) {
                        baseFee.setUsdtAmount(baseAmount);
                        anotherFee.setUsdtAmount(anotherAmount);
                    } else {
                        baseAmount = baseAmount.multiply(btcUsdtPrice).setScale(9, BigDecimal.ROUND_DOWN);
                        anotherAmount = anotherAmount.multiply(btcUsdtPrice).setScale(9, BigDecimal.ROUND_DOWN);

                        baseFee.setUsdtAmount(baseAmount);
                        anotherFee.setUsdtAmount(anotherAmount);
                    }
                    usdtAmount = usdtAmount.add(baseAmount).add(anotherAmount);
                    transRecordFees.add(baseFee);
                    transRecordFees.add(anotherFee);
                    hasHandle = true;
                }
            }
            if (!hasHandle) {
                //如果有usdt或者btc一方手续费，直接累积
                if (null != baseFee) {
                    BigDecimal baseAmount = baseFee.getAmount();
                    if ("usdt".equalsIgnoreCase(baseFee.getCurrency())) {
                        baseFee.setUsdtAmount(baseAmount);
                    } else {
                        baseAmount = baseAmount.multiply(btcUsdtPrice).setScale(9, BigDecimal.ROUND_DOWN);

                        baseFee.setUsdtAmount(baseAmount);
                    }
                    usdtAmount = usdtAmount.add(baseAmount);
                    transRecordFees.add(baseFee);
                }

                //如果有另外一方手续费，则根据usdt或者btc市场的价格计算手续费，累积
                if (null != anotherFee) {
                    BigDecimal price = prices.getBigDecimal(anotherFee.getNumberBi().toLowerCase() + "_" + anotherFee.getExchangeBi().toLowerCase());
                    if (null != price) {
                        BigDecimal anotherAmount = anotherFee.getAmount().multiply(price).setScale(9, BigDecimal.ROUND_DOWN);
                        if ("usdt".equalsIgnoreCase(anotherFee.getExchangeBi())) {
                            anotherFee.setUsdtAmount(anotherAmount);
                        } else {
                            anotherAmount = anotherAmount.multiply(btcUsdtPrice).setScale(9, BigDecimal.ROUND_DOWN);

                            anotherFee.setUsdtAmount(anotherAmount);
                        }
                        usdtAmount = usdtAmount.add(anotherAmount);
                        transRecordFees.add(anotherFee);
                    }
                }
            }
        }

        if (CollectionUtils.isEmpty(transRecordFees)) {
            logger.info("[回购功能] fee表没有需要同步的回购手续费");
            noEntrustHandle();
            return null;
        }

        return new FeeSummaryResult(minFeeId, maxIdFee.getId(), transRecordFees, usdtAmount, btcUsdtPrice);
    }

    /**
     * 本次回购没有委托处理
     * 1.插入一条0资金的记录占位
     * 2.更新缓存记录
     */
    private void noEntrustHandle() {
        long batchId = IdWorkerUtil.getId();
        long entrustTime = System.currentTimeMillis();

        BcEntrustTransRecord thisRecord = new BcEntrustTransRecord();
        thisRecord.setBatchId(batchId);
        thisRecord.setEntrustId(0);
        thisRecord.setEntrustTime(entrustTime);
        thisRecord.setCompleteTotalMoney(BigDecimal.ZERO);
        thisRecord.setCompleteNumber(BigDecimal.ZERO);
        entrustRecordDao.insert(thisRecord);

        //更新缓存
        updateCache(null, entrustTime, BigDecimal.ZERO);
    }

    /**
     * 回购更新缓存
     */
    private void updateCache(Set<Long> userIdsSet, long entrustTime, BigDecimal numbers) {
        if (CollectionUtils.isNotEmpty(userIdsSet)) {
            //异步更新用户与我相关回购委托第一页缓存
            AsynMethodFactory.addWork(BackCapitalWorker.class, "updateUserEntrustCache", new Object[]{userIdsSet});
        }

        //异步更新资金占比20条数据，包括0占位的
        AsynMethodFactory.addWork(BackCapitalWorker.class, "updateCapitalsCache", new Object[]{});

        //异步更新回购平均速度，分钟级别
        AsynMethodFactory.addWork(BackCapitalWorker.class, "updateAvgSpeed", new Object[]{});

        //更新回购数量缓存
        Cache.SetObj(BackCapitalConst.BACK_CAPITAL_LAST_NUMBER_CACHE_KEY, numbers, 60);

        //更新本次执行时间缓存
        Cache.Set(BackCapitalConst.BACK_CAPITAL_TIME_CACHE_KEY, entrustTime / 1000 + "");
    }

    /**
     * 更新用户与我相关第一页缓存
     * 多取一条，用于显示是否还有下一页
     *
     * @param userIdsSet
     */
    public void updateUserEntrustCache(Set<Long> userIdsSet) {
        long startTime = System.currentTimeMillis();

        for (long userId : userIdsSet) {
            List<BcEntrustTransRecord> userEntrusts = entrustRecordDao.getUserEntrusts(userId, 21);
            String cacheKey = String.format(BackCapitalConst.BACK_CAPITAL_USER_ENTRUST_CACHE_KEY, userId);
            Cache.SetObj(cacheKey, userEntrusts, 2 * 60 * 60);
        }

        //如果异步任务耗时超过30秒，报警
        if (System.currentTimeMillis() - startTime > 30 * 1000) {
            logger.error("[回购功能] 异步更新用户与我相关第一页缓存，耗时超过30秒，cost:" + (System.currentTimeMillis() - startTime) + "ms");
        }
    }

    /**
     * 更新20条回购资金缓存，包括0
     */
    public void updateCapitalsCache() {
        long startTime = System.currentTimeMillis();

        List<BcEntrustTransRecord> entrustRecords = entrustRecordDao.getEntrustPrice();
        Cache.SetObj(BackCapitalConst.BACK_CAPITAL_TWENTY_CAPITAL_CACHE_KEY, entrustRecords, 60);

        //如果异步任务耗时超过30秒，报警
        if (System.currentTimeMillis() - startTime > 30 * 1000) {
            logger.error("[回购功能] 异步更新20条回购资金缓存，耗时超过30秒，cost:" + (System.currentTimeMillis() - startTime) + "ms");
        }
    }

    /**
     * 更新回购平均速度，分钟级别
     */
    public void updateAvgSpeed() {
        try {
            long avgSpeedTime = 30 * 60 * 1000;
            BigDecimal avgSpeed = BigDecimal.ZERO;

            long timeBefore = System.currentTimeMillis() - avgSpeedTime;
            boolean haveBeforeTime = entrustRecordDao.isHaveBeforeTime(timeBefore);
            //超过半小时
            if (haveBeforeTime) {
                BigDecimal totalNumbers = entrustRecordDao.getTotalNumbersByTime(timeBefore);
                avgSpeed = totalNumbers.divide(new BigDecimal(30), 9, BigDecimal.ROUND_DOWN);
            } else {
                BackCapitalConfig config = backCapitalService.getConfig();
                Entrust entrust = entrustDao.getOneEntrustByUser(String.valueOf(config.getBcUserId()), BackCapitalConst.BACK_CAPITAL_DB_NAME);
                if (null != entrust) {
                    int bcFrequency = config.getBcFrequency();
                    avgSpeed = entrust.getCompleteNumber().multiply(new BigDecimal(60.0 / bcFrequency));
                }
            }

            Cache.SetObj(BackCapitalConst.BACK_CAPITAL_AVG_SPEED_CACHE_KEY, avgSpeed);
        } catch (Exception e) {
            logger.error("异步更新回购平均速度异常！", e);
        }
    }

    private BcEntrustTransRecord transferByFee(Fee fee, long batchId, long entrustId, BigDecimal amount, BigDecimal numbers,
                                               BigDecimal usdtAmount, long entrustTime, BigDecimal btcUsdtPrice, BigDecimal feePercent) {
        BcEntrustTransRecord entrustRecord = new BcEntrustTransRecord();
        entrustRecord.setBatchId(batchId);
        entrustRecord.setEntrustId(entrustId);
        entrustRecord.setTransRecordId(fee.getTransRecordId());
        entrustRecord.setTransRecordTime(fee.getTransRecordTime());
        entrustRecord.setUserId(fee.getUserId());
        entrustRecord.setMarket(fee.getNumberBi() + "/" + fee.getExchangeBi());
        entrustRecord.setBtcUsdtPrice(btcUsdtPrice);
        entrustRecord.setCurrency(fee.getCurrency());
        entrustRecord.setFeePercent(feePercent);
        entrustRecord.setOriginAmount(fee.getAmount());

        BigDecimal feeUsdtAmount = fee.getUsdtAmount();
        feeUsdtAmount = feeUsdtAmount.multiply(feePercent).setScale(9, RoundingMode.DOWN);
        entrustRecord.setAmount(feeUsdtAmount);

        //手续费占比，这笔手续费/本次回购金额
        BigDecimal feeRatio = feeUsdtAmount.divide(usdtAmount, 9, RoundingMode.DOWN);
        if (feeRatio.compareTo(BigDecimal.ONE) > 0) {
            feeRatio = BigDecimal.ONE;
        }
        entrustRecord.setFeeRatio(feeRatio);

        entrustRecord.setCompleteTotalMoney(amount);
        entrustRecord.setCompleteNumber(numbers);
        entrustRecord.setEntrustTime(entrustTime);
        entrustRecord.setUnitPrice(fee.getUnitPrice());
        entrustRecord.setNumbers(fee.getNumbers());
        entrustRecord.setTotalPrice(fee.getTotalPrice());

        return entrustRecord;
    }

    /**
     * 根据回购资金计算回购价格和数量
     *
     * @param amount
     * @param dbName
     * @return
     */
    private BigDecimal[] getPriceNumberByAmount(BigDecimal amount, String dbName) {
        //获取卖盘50档数据
        List<Entrust> entrusts = entrustDao.getSellUnDoneEntrust(50, dbName);
        if (CollectionUtils.isEmpty(entrusts)) {
            logger.error("[回购功能] 获取卖盘50档数据为空！");
            return null;
        }

        BigDecimal addAmount = BigDecimal.ZERO;
        BigDecimal addNumbers = BigDecimal.ZERO;
        BigDecimal price = BigDecimal.ZERO;
        for (Entrust entrust : entrusts) {
            BigDecimal entrustPrice = entrust.getUnitPrice();
            BigDecimal entrustNumbers = entrust.getNumbers();
            BigDecimal entrustAmount = entrustPrice.multiply(entrustNumbers);
            if (addAmount.add(entrustAmount).compareTo(amount) < 0) {
                addAmount = addAmount.add(entrustAmount);
                addNumbers = addNumbers.add(entrustNumbers);
            } else {
                BigDecimal numbers = amount.subtract(addAmount).divide(entrustPrice, 9, BigDecimal.ROUND_DOWN);
                addNumbers = addNumbers.add(numbers);
                price = entrustPrice;
                break;
            }
        }

        return new BigDecimal[]{price, addNumbers};
    }

    /**
     * 委托买单
     *
     * @param market
     * @param userId
     * @param unitPrice
     * @param numbers
     * @return
     */
    public EntrustResult buy(JSONObject market, String userId, BigDecimal unitPrice, BigDecimal numbers) {
        EntrustResult result = new EntrustResult();
        try {
            logger.info("[回购功能] 委托买单请求参数：numbers=" + numbers + ",unitPrice=" + unitPrice);

            Message myObj = new Message();
            myObj.setUserId(Integer.parseInt(userId));
            myObj.setWebId(6);
            myObj.setNumbers(numbers);
            myObj.setTypes(1); //0：卖 1：买
            myObj.setUnitPrice(unitPrice);
            myObj.setStatus(0);
            myObj.setMarket(market.getString("market"));

            String param = HTTPTcp.ObjectToString(myObj);
            String rtn = HTTPTcp.Post(market.getString("ip"), market.getIntValue("port"), "/server/entrust", param);
            Message rtn2 = (Message) HTTPTcp.StringToObject(rtn);
            logger.info("[回购功能] 委托买单返回结果：message=" + rtn2.getMessage() + ",code=" + rtn2.getStatus() + ",entrustId=" + rtn2.getNumbers());

            result.code = rtn2.getStatus();
            result.entrustId = rtn2.getNumbers().longValue();
            result.message = rtn2.getMessage();
        } catch (Exception e) {
            logger.error("[回购功能] 委托买单失败，numbers=" + numbers + ",unitPrice=" + unitPrice, e);
        }
        return result;
    }

    class EntrustResult {
        int code = 0;
        long entrustId = -1;
        String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public long getEntrustId() {
            return entrustId;
        }

        public void setEntrustId(long entrustId) {
            this.entrustId = entrustId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    class FeeSummaryResult {
        int minFeeId;
        int maxFeeId;
        List<Fee> feeList;
        BigDecimal usdtAmount;
        BigDecimal btcUsdtPrice;

        FeeSummaryResult(int minFeeId, int maxFeeId, List<Fee> feeList, BigDecimal usdtAmount, BigDecimal btcUsdtPrice) {
            this.minFeeId = minFeeId;
            this.maxFeeId = maxFeeId;
            this.feeList = feeList;
            this.usdtAmount = usdtAmount;
            this.btcUsdtPrice = btcUsdtPrice;
        }
    }

    public static void main(String[] args) {
        BackCapitalWorker backCapitalWorker = new BackCapitalWorker("", "");
//        JSONObject market = Market.getMarketByName(BackCapitalConst.BACK_CAPITAL_MARKET);
//        backCapitalWorker.buy(market, "84", new BigDecimal(0.1), new BigDecimal(0.3));
//        Cache.Delete(BackCapitalConst.BACK_CAPITAL_TIME_CACHE_KEY);
//        BigDecimal amount = new BigDecimal("5.97575");
//        backCapitalWorker.getPriceNumberByAmount(amount, "ltcbtcentrust");
//        backCapitalWorker.run();
//        backCapitalWorker.updateAvgSpeed();
        BigDecimal baseBalance = new BigDecimal(200);
        BigDecimal aaa = new BigDecimal(199);
        if (aaa.compareTo(baseBalance.subtract(BigDecimal.ONE)) < 0) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
    }
}
package com.match.entrust;

import com.match.domain.Entrust;
import com.match.domain.EntrustUpdateInfo;
import com.match.domain.MatchResultEnum;
import com.match.domain.TransFundsBack;
import com.match.domain.TransRecordInfo;
import com.match.domain.TransRecordMem;
import com.tenstar.TimeUtil;
import com.tenstar.timer.entrust.DataArray;
import com.world.model.Market;
import com.world.model.entitys.record.TransRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>@Description: 撮合交易</p>
 *
 * @author buxianguan
 * @date 2018/5/16下午4:15
 */
public class MemEntrustMatchProcessor extends TimerTask {
    private static final Logger logger = LoggerFactory.getLogger(MemEntrustMatchProcessor.class);

    public static DataArray da;

    private static AtomicLong virtualTransRecordId = new AtomicLong(System.currentTimeMillis());

    private Market market;

    public MemEntrustMatchProcessor(Market market) {
        this.market = market;
        da = new DataArray(market.market, market.maxPrice);
    }

    @Override
    public void run() {
        try {
            //撮合开关开启时，执行撮合逻辑
//            if (MemEntrustSwitchService.getMatchSwitch(market.market)) {
            Entrust entrust = null;
            do {
                entrust = MemEntrustDataProcessor.getNoMatchingEntrust(market);
                if (null != entrust) {
                    processOne(market, entrust);
                }
            } while (null != entrust);
//            }
        } catch (Exception e) {
            logger.error(market.market + " [撮合交易] 获得未撮合委托错误！", e);
        }
    }

    private void processOne(Market market, Entrust entrust) {
        try {
            long start = System.currentTimeMillis();
            if (entrust.getTypes() == -1 && entrust.getFreezeId() > 0) {
                cancel(entrust, market);
            } else if (entrust.getTypes() == -1) {
                EntrustUpdateInfo entrustUpdateInfo = new EntrustUpdateInfo();
                entrustUpdateInfo.setEntrustId(entrust.getEntrustId()).setStatus(2).setUserId(entrust.getUserId()).setMatchResult(MatchResultEnum.UPDATE);

                //异步通知数据库更新
                EntrustUpdateProcessor.addQueue(market, Arrays.asList(entrustUpdateInfo), null, null);

                //移除未撮合委托
                MemEntrustDataProcessor.removeNoMatchingEntrust(entrust.getEntrustId(), market);

                logger.error(market.market + " [撮合交易] 有一个异常的取消命令，没有冻结Id！委托Id：" + entrust.getEntrustId());
            } else if (entrust.getTypes() == 0 || entrust.getTypes() == 1) {
                doEntrust(entrust, market);
            } else {
                EntrustUpdateInfo entrustUpdateInfo = new EntrustUpdateInfo();
                entrustUpdateInfo.setEntrustId(entrust.getEntrustId()).setStatus(2).setUserId(entrust.getUserId()).setMatchResult(MatchResultEnum.UPDATE);

                //异步通知数据库更新
                EntrustUpdateProcessor.addQueue(market, Arrays.asList(entrustUpdateInfo), null, null);

                //移除未撮合委托
                MemEntrustDataProcessor.removeNoMatchingEntrust(entrust.getEntrustId(), market);

                logger.error(market.market + " [撮合交易] 处理委托：有一个异常的type为" + entrust.getTypes() + "命令！");
            }
            logger.info(market.market + " [撮合交易] processOne：" + entrust.getEntrustId() + "，耗时：" + (System.currentTimeMillis() - start) + " 毫秒。");
        } catch (Exception e) {
            logger.error(market.market + " [撮合交易] 处理单条委托异常！", e);
        }
    }

    /**
     * 取消委托
     */
    public void cancel(Entrust er, Market market) {
        try {

            //从内存中获取原始委托，单笔撮合，数据只可能存在于未成交数据中，如果没有说明已经成交（批量撮合不适用）
            Entrust originalEr = getOriginalEntrustForCancel( er, market);

            //现在不能这么做，因为委托更新异步执行，虽然撮合完了，但是数据库还没有更新，导致获取到错误数据，需要依赖内存值
            /*//保险起见，取消的时候先从数据库读取原始委托
            long originalEntrustId = er.getFreezeId();
            Entrust originalEr = (Entrust) Data.GetOne(market.db, "select * from Entrust where entrustId=?", new Object[]{originalEntrustId}, Entrust.class);

            if (originalEr == null) {
                logger.info(market.market + " [撮合交易] 取消委托错误，发现一个没有原始记录的取消");
                //移除未撮合列表
                MemEntrustDataProcessor.removeNoMatchingEntrust(er.getEntrustId(), market);
                return;
            }*/

            List<EntrustUpdateInfo> entrustUpdateInfoList = new ArrayList<>();
            //校验原纪录是否已经撮合完成
            if (null == originalEr || originalEr.getStatus() == 2 || originalEr.getNumbers().compareTo(BigDecimal.ZERO) <= 0 || originalEr.getStatus() == 1) {
                logger.info(market.market + " [撮合交易] 取消委托，原始委托已经处理完毕，无需操作");

                EntrustUpdateInfo entrustUpdateInfo = new EntrustUpdateInfo();
                entrustUpdateInfo.setEntrustId(er.getEntrustId()).setUserId(er.getUserId()).setStatus(2).setMatchResult(MatchResultEnum.UPDATE);
                entrustUpdateInfoList.add(entrustUpdateInfo);

                //异步通知数据库更新
                EntrustUpdateProcessor.addQueue(market, entrustUpdateInfoList, null, null);

                //移除未撮合列表
                MemEntrustDataProcessor.removeNoMatchingEntrust(er.getEntrustId(), market);
                //内存移除原始委托
                if (null != originalEr) {
                    MemEntrustDataProcessor.removeFromMarketsNoDealEntrustMap(originalEr.getTypes(), originalEr.getUnitPrice(), originalEr.getEntrustId(), market);
                }
                return;
            }

            //更新原始委托SQL
            EntrustUpdateInfo originEntrustUpdateInfo = new EntrustUpdateInfo();
            originEntrustUpdateInfo.setEntrustId(originalEr.getEntrustId()).setUserId(originalEr.getUserId()).setMatchResult(MatchResultEnum.CANCEL);
            entrustUpdateInfoList.add(originEntrustUpdateInfo);

            //更新取消委托SQL
            EntrustUpdateInfo entrustUpdateInfo = new EntrustUpdateInfo();
            entrustUpdateInfo.setEntrustId(er.getEntrustId()).setStatus(2).setUserId(er.getUserId()).setMatchResult(MatchResultEnum.UPDATE);
            entrustUpdateInfoList.add(entrustUpdateInfo);

            //处理资金回退
            BigDecimal money = BigDecimal.ZERO;
            int fundsType = 0;
            if (originalEr.getTypes() == 1) {
                money = originalEr.getTotalMoney().subtract(originalEr.getCompleteTotalMoney());
                fundsType = market.exchangeBiFundsType;
            } else if (originalEr.getTypes() == 0) {
                //内存中的这个数量是剩余数量，无需相减了，如果从数据库获取，需要减去已完成数量
                money = originalEr.getNumbers();
                fundsType = market.numberBiFundsType;
            }
            TransFundsBack transFundsBack = new TransFundsBack(money, originalEr.getEntrustId(), originalEr.getUserId(), fundsType, TimeUtil.getNow().getTime());

            //异步通知数据库更新
            EntrustUpdateProcessor.addQueue(market, entrustUpdateInfoList, null, transFundsBack);

            //内存移除原始委托
            MemEntrustDataProcessor.removeFromMarketsNoDealEntrustMap(originalEr.getTypes(), originalEr.getUnitPrice(), originalEr.getEntrustId(), market);
            //移除未撮合列表
            MemEntrustDataProcessor.removeNoMatchingEntrust(er.getEntrustId(), market);
        } catch (Exception e) {
            logger.error(market.market + " [撮合交易] 取消委托处理失败", e);
        }
    }

    /**
     * 取消委托时获取原始委托
     *
     * @param er
     * @param market
     * @return
     */
    private Entrust getOriginalEntrustForCancel(Entrust er, Market market) {

        long originalEntrustId = er.getFreezeId();
        BigDecimal originalPrice = er.getUnitPrice();
        int compareResult = originalPrice.compareTo(BigDecimal.ZERO);
        Entrust originalEr;
        if (compareResult == 0) {
            //不知道原始委托的types和价格，需要从两个map中获取，可以优化，增加types标识
            for (MemEntrustDataProcessor.PriceDepth priceDepth : MemEntrustDataProcessor.marketsNoDealBuyEntrustMap.get(market.market).values()) {
                originalEr = priceDepth.get(originalEntrustId);
                if (null != originalEr) {
                   return originalEr;
                }
            }

            for (MemEntrustDataProcessor.PriceDepth priceDepth : MemEntrustDataProcessor.marketsNoDealSellEntrustMap.get(market.market).values()) {
                originalEr = priceDepth.get(originalEntrustId);
                if (null != originalEr) {
                    return originalEr;
                }
            }
            return null;
        }
        if (compareResult > 0) {
            // 原始委托是买入 @see com.tenstar.timer.entrust.Interface#doCancle() #doCancleForBrush()
            MemEntrustDataProcessor.PriceDepth priceDepth = MemEntrustDataProcessor.marketsNoDealBuyEntrustMap.get(market.market).get(originalPrice);
            if (priceDepth != null) {
                return priceDepth.get(originalEntrustId);
            }
        }

        // 原始委托是卖出
        MemEntrustDataProcessor.PriceDepth priceDepth = MemEntrustDataProcessor.marketsNoDealSellEntrustMap.get(market.market).get(originalPrice.abs());
        if (priceDepth != null) {
            return priceDepth.get(originalEntrustId);
        }

        return null;
    }

    /***
     * 处理新的买单
     * @param er
     */
    public void doEntrust(Entrust er, Market m) {
        BigDecimal price = er.getUnitPrice();
        NavigableMap<BigDecimal, MemEntrustDataProcessor.PriceDepth> canTrans = null;
        if (er.getTypes() == 1) {
            TreeMap<BigDecimal, MemEntrustDataProcessor.PriceDepth> sellEntrustsMap = MemEntrustDataProcessor.marketsNoDealSellEntrustMap.get(m.market);
            canTrans = sellEntrustsMap.headMap(price, true);
        } else {
            TreeMap<BigDecimal, MemEntrustDataProcessor.PriceDepth> buyEntrustsMap = MemEntrustDataProcessor.marketsNoDealBuyEntrustMap.get(m.market);
            canTrans = buyEntrustsMap.headMap(price, true);
        }

        if (canTrans == null || canTrans.size() <= 0) {
            //无法成交
            noTrans(er, m);
        } else {
            //撮合成交
            doTrans(canTrans, er, m);
        }
    }

    private void noTrans(Entrust er, Market market) {
        try {
            //说明没有符合条件的记录，更新记录状态为3，归入撮合被动委托内存中
            EntrustUpdateInfo entrustUpdateInfo = new EntrustUpdateInfo();
            entrustUpdateInfo.setEntrustId(er.getEntrustId()).setUserId(er.getUserId()).setStatus(3).setMatchResult(MatchResultEnum.NO_TRANS);

            //异步通知数据库更新
            EntrustUpdateProcessor.addQueue(market, Arrays.asList(entrustUpdateInfo), null, null);

            //移除未撮合列表
            MemEntrustDataProcessor.removeNoMatchingEntrust(er.getEntrustId(), market);
            //添加到被动委托内存中
            MemEntrustDataProcessor.addToMarketsNoDealEntrustMap(er, market);
        } catch (Exception e) {
            logger.error(market.market + " [撮合交易] 更新未撮合记录为没有成交状态异常！", e);
        }
    }

    /***
     * 撮合成交
     * @param canTrans
     * @param initiativeEntrust 主动委托
     */
    private void doTrans(NavigableMap<BigDecimal, MemEntrustDataProcessor.PriceDepth> canTrans, Entrust initiativeEntrust, Market market) {
        logger.info(market.market + " [撮合交易] 开始撮合交易，委托id：" + initiativeEntrust.getEntrustId());

        //主动委托状态，0初始化，2完全成交，3部分成交
        int initiativeStatus = 0;

        Map.Entry<BigDecimal, MemEntrustDataProcessor.PriceDepth> entry = canTrans.firstEntry();
        while (entry != null && initiativeStatus != 2) {
            BigDecimal canPrice = entry.getKey();

            //校验主被动委托价格，主动买价必须小于被动卖价，主动卖价必须大于被动买价
            if (initiativeEntrust.getTypes() == 1 && canPrice.compareTo(initiativeEntrust.getUnitPrice()) > 0) {
                return;
            }
            if (initiativeEntrust.getTypes() == 0 && canPrice.compareTo(initiativeEntrust.getUnitPrice()) < 0) {
                return;
            }

            MemEntrustDataProcessor.PriceDepth priceDepth = entry.getValue();
            //循环当前价位的被动委托，直到当前价位无被动委托或者主动委托已经完全成交
            while (null != priceDepth && !priceDepth.isEmpty() && initiativeStatus != 2) {
                long start = System.currentTimeMillis();

                Map.Entry<Long, Entrust> firstEntry = priceDepth.firstEntry();
                Entrust passiveEntrust = firstEntry.getValue();

                //被动委托剩余数量
                BigDecimal passiveNumbers = passiveEntrust.getNumbers();
//                logger.info(market.market + " [撮合交易] passiveId:" + passiveEntrust.getEntrustId() + "，passiveNumbers:" + passiveNumbers);

                //主动委托剩余数量
                BigDecimal initiativeNumbers = initiativeEntrust.getNumbers();
//                logger.info(market.market + " [撮合交易] initiativeId:" + initiativeEntrust.getEntrustId() + "，initiativeNumbers:" + initiativeNumbers);

                //本次能成交的数量
                BigDecimal thisNumbers = initiativeNumbers;
                //主被动委托的状态
                int passiveStatus = 3;
                initiativeStatus = 3;
                if (initiativeNumbers.compareTo(passiveNumbers) > 0) {
                    thisNumbers = passiveNumbers;
                    passiveStatus = 2;
                } else if (initiativeNumbers.compareTo(passiveNumbers) == 0) {
                    passiveStatus = 2;
                    initiativeStatus = 2;
                } else {
                    initiativeStatus = 2;
                }

                //本次交易的钱
                BigDecimal thisMoney = Market.totalMoneyDown(passiveEntrust.getUnitPrice(), thisNumbers);

                TransFundsBack transFundsBack = null;
                //如果被动成交
                if (passiveStatus == 2) {
                    //当前委托单已经成交完了，删掉
                    priceDepth.remove(passiveEntrust.getEntrustId());

                    //如果是买单，判断是否需要回退资金
                    if (passiveEntrust.getTypes() == 1) {
                        transFundsBack = fundsBackHandle(passiveEntrust, thisMoney);
                    }
                } else {
                    //更新被动剩余数量
                    passiveEntrust.setNumbers(passiveEntrust.getNumbers().subtract(thisNumbers));
                    passiveEntrust.setCompleteNumber(passiveEntrust.getCompleteNumber().add(thisNumbers));
                    passiveEntrust.setCompleteTotalMoney(passiveEntrust.getCompleteTotalMoney().add(thisMoney));
                    priceDepth.subDepth(thisNumbers);
                }

                //如果主动成交
                if (initiativeStatus == 2) {
                    //移除未撮合列表
                    MemEntrustDataProcessor.removeNoMatchingEntrust(initiativeEntrust.getEntrustId(), market);

                    //如果是买单，判断是否需要回退资金
                    if (initiativeEntrust.getTypes() == 1) {
                        transFundsBack = fundsBackHandle(initiativeEntrust, thisMoney);
                    }
                } else {
                    //更新循环内存中的数据
                    initiativeEntrust.setNumbers(initiativeEntrust.getNumbers().subtract(thisNumbers));
                    initiativeEntrust.setCompleteNumber(initiativeEntrust.getCompleteNumber().add(thisNumbers));
                    initiativeEntrust.setCompleteTotalMoney(initiativeEntrust.getCompleteTotalMoney().add(thisMoney));
                }

                //异步执行数据库更新
                asyncDBUpdate(market, initiativeEntrust, initiativeStatus, passiveEntrust, passiveStatus, thisNumbers, thisMoney, transFundsBack);

                logger.info(market.market + " [撮合交易] 处理委托：(" + initiativeEntrust.getEntrustId() + "," + passiveEntrust.getEntrustId() + ")，耗时：" + (System.currentTimeMillis() - start) + " 毫秒。");
            }

            if (null == priceDepth || priceDepth.isEmpty()) {
                canTrans.remove(canPrice);
                entry = canTrans.firstEntry();
            }
        }

        //如果主动委托未撮合
        if (initiativeStatus == 0) {
            //更新数据库为3，变成被动委托，异步更新
            EntrustUpdateInfo entrustUpdateInfo = new EntrustUpdateInfo();
            entrustUpdateInfo.setEntrustId(initiativeEntrust.getEntrustId()).setUserId(initiativeEntrust.getUserId()).setStatus(3).setMatchResult(MatchResultEnum.NO_TRANS);
            EntrustUpdateProcessor.addQueue(market, Arrays.asList(entrustUpdateInfo), null, null);

            initiativeStatus = 3;
        }

        //如果主动委托未完全成交
        if (initiativeStatus == 3) {
            //移除未撮合列表
            MemEntrustDataProcessor.removeNoMatchingEntrust(initiativeEntrust.getEntrustId(), market);
            //把委托添加到未成交委托列表中
            MemEntrustDataProcessor.addToMarketsNoDealEntrustMap(initiativeEntrust, market);
        }

    }

    /**
     * 数据库更新异步执行
     */
    private void asyncDBUpdate(Market market, Entrust initiativeEntrust, int initiativeStatus, Entrust passiveEntrust, int passiveStatus, BigDecimal thisNumbers,
                               BigDecimal thisMoney, TransFundsBack transFundsBack) {
        //组装委托记录更新信息，异步执行
        List<EntrustUpdateInfo> entrustUpdateInfoList = new ArrayList<>();

        //主动委托更新信息
        EntrustUpdateInfo initiativeUpdateInfo = new EntrustUpdateInfo();
        initiativeUpdateInfo.setEntrustId(initiativeEntrust.getEntrustId()).setUserId(initiativeEntrust.getUserId()).setStatus(initiativeStatus)
                .setCompleteNumber(thisNumbers).setCompleteTotalMoney(thisMoney).setMatchResult(MatchResultEnum.CAN_TRANS);
        entrustUpdateInfoList.add(initiativeUpdateInfo);

        //被动委托更新信息
        EntrustUpdateInfo passiveUpdateInfo = new EntrustUpdateInfo();
        passiveUpdateInfo.setEntrustId(passiveEntrust.getEntrustId()).setUserId(passiveEntrust.getUserId()).setStatus(passiveStatus)
                .setCompleteNumber(thisNumbers).setCompleteTotalMoney(thisMoney).setMatchResult(MatchResultEnum.CAN_TRANS);
        entrustUpdateInfoList.add(passiveUpdateInfo);

        //组装成交记录信息，异步执行
        TransRecordInfo transRecordInfo = null;
        int userIdBuy = 0;
        if (initiativeEntrust.getTypes() == 1) {
            userIdBuy = initiativeEntrust.getUserId();
            transRecordInfo = new TransRecordInfo(passiveEntrust.getUnitPrice(), thisMoney, thisNumbers, initiativeEntrust.getEntrustId(), initiativeEntrust.getUserId(),
                    passiveEntrust.getEntrustId(), passiveEntrust.getUserId(), initiativeEntrust.getTypes(), TimeUtil.getNow().getTime(), TimeUtil.getMinuteFirst().getTime(),
                    initiativeEntrust.getWebId(), passiveEntrust.getWebId());
        } else {
            userIdBuy = passiveEntrust.getUserId();
            transRecordInfo = new TransRecordInfo(passiveEntrust.getUnitPrice(), thisMoney, thisNumbers, passiveEntrust.getEntrustId(), passiveEntrust.getUserId(),
                    initiativeEntrust.getEntrustId(), initiativeEntrust.getUserId(), initiativeEntrust.getTypes(), TimeUtil.getNow().getTime(), TimeUtil.getMinuteFirst().getTime(),
                    passiveEntrust.getWebId(), initiativeEntrust.getWebId());
        }

        //为了加快页面速度，先认为撮合已经成功，添加到成交记录列表中，异步执行
        TransRecord transRecord = new TransRecord();
        transRecord.setTimes(TimeUtil.getNow().getTime());
        transRecord.setTimeMinute(TimeUtil.getMinuteFirst().getTime());
        transRecord.setUnitPrice(passiveEntrust.getUnitPrice());
        transRecord.setTotalPrice(thisMoney);
        transRecord.setNumbers(thisNumbers);
        //id是虚拟的，因为还没有入库
        transRecord.setTransRecordId(virtualTransRecordId.getAndIncrement());
        transRecord.setTypes(initiativeEntrust.getTypes());
        transRecord.setUserIdBuy(userIdBuy);
        TransRecordMem transRecordMem = new TransRecordMem(transRecord, market);
        MemTransRecordProcessor.add(transRecordMem);

        EntrustUpdateProcessor.addQueue(market, entrustUpdateInfoList, transRecordInfo, transFundsBack);
    }

    /**
     * 处理买单结余，需要返回用户
     *
     * @param entrust
     * @param thisMoney
     */
    private TransFundsBack fundsBackHandle(Entrust entrust, BigDecimal thisMoney) {
        BigDecimal completeMoney = entrust.getCompleteTotalMoney().add(thisMoney);
        if (completeMoney.compareTo(entrust.getTotalMoney()) < 0) {
            return new TransFundsBack(entrust.getTotalMoney().subtract(completeMoney),
                    entrust.getEntrustId(), entrust.getUserId(), market.exchangeBiFundsType, TimeUtil.getNow().getTime());
        }
        return null;
    }
}

package com.match.entrust;

import com.match.domain.Entrust;
import com.world.data.mysql.Data;
import com.world.model.Market;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * <p>@Description: 撮合数据加载</p>
 *
 * @author buxianguan
 * @date 2018/5/16上午11:13
 */
public class MemEntrustDataProcessor extends TimerTask {
    public static Logger logger = Logger.getLogger(MemEntrustDataProcessor.class);

    /**
     * 未成交买卖单SQL
     */
    private final static String NO_DEAL_SELL_SQL = "select * from entrust where types=0 and completeNumber<numbers and status=3 order by unitPrice asc,entrustId asc";
    private final static String NO_DEAL_BUY_SQL = " select * from entrust where types=1 and completeNumber<numbers and status=3 order by unitPrice desc,entrustId asc";

    /**
     * 未撮合委托单SQL
     */
    private final static String NO_MATCH_ENTRUST_SQL = "select * from entrust where status=0 order by entrustId asc";
    private final static String ONE_ENTRUST_SQL = "select * from entrust where entrustId=?";

    /**
     * 未成交买卖单treeMap
     */
    public static Map<String, TreeMap<BigDecimal, PriceDepth>> marketsNoDealBuyEntrustMap = new HashMap<>();
    public static Map<String, TreeMap<BigDecimal, PriceDepth>> marketsNoDealSellEntrustMap = new HashMap<>();

    /**
     * 未撮合委托单treeMap
     */
    public static Map<String, TreeMap<Long, Entrust>> marketsNoMatchingEntrustMap = new HashMap<>();

    private static Map<String, Boolean> initMap = new HashMap<>();
    public static volatile boolean reloadResult = false;
    public static volatile boolean reloadDBResult = false;
    public static volatile boolean needReload = false;

    /**
     * 获取未撮合数据超过5分钟，重新从数据库load数据，防止由于内存问题，导致撮合停止
     */
    private final static long backUpLoadMatchEntrustFrequency = 5 * 60 * 1000;
    private static long lastGetNoMatchEntrustTime = System.currentTimeMillis();

    public MemEntrustDataProcessor() {
    }

    @Override
    public void run() {
        timingReload();
    }

    private static void reload(Market m) {
        logger.info(m.market + " [撮合交易] 重新从数据库加载撮合原始数据开始");
        long s1 = System.currentTimeMillis();

        marketsNoMatchingEntrustMap.put(m.market, new TreeMap<>());

        //初始化未成交买卖委托map
        //买单按价格从高到低排序
        TreeMap<BigDecimal, PriceDepth> noDealBuyEntrustsMap = new TreeMap<>(Comparator.reverseOrder());
        //卖单按价格从低到高排序
        TreeMap<BigDecimal, PriceDepth> noDealSellEntrustsMap = new TreeMap<>();

        //组装买单被动委托列表
        List<Entrust> noDealBuyEntrusts = Data.QueryT(m.db, NO_DEAL_BUY_SQL, new Object[]{}, Entrust.class);
        if (noDealBuyEntrusts.size() > 0) {
            entrustToMap(noDealBuyEntrusts, noDealBuyEntrustsMap);
        }
        marketsNoDealBuyEntrustMap.put(m.market, noDealBuyEntrustsMap);

        //组装卖单被动委托列表，增加是否与买单重合逻辑，如果重合，重新走撮合
        List<Entrust> noDealSellEntrusts = Data.QueryT(m.db, NO_DEAL_SELL_SQL, new Object[]{}, Entrust.class);
        if (noDealSellEntrusts.size() > 0) {
            boolean needNoMatch = true;
            for (Entrust entrust : noDealSellEntrusts) {
                //组装未完成的数量
                entrust.setSrcNumbers(entrust.getNumbers());
                entrust.setNumbers(entrust.getNumbers().subtract(entrust.getCompleteNumber()));

                BigDecimal price = entrust.getUnitPrice();

                if (needNoMatch) {
                    NavigableMap<BigDecimal, PriceDepth> canTrans = noDealBuyEntrustsMap.headMap(price, true);
                    if (canTrans != null && canTrans.size() > 0) {
                        //添加到主动委托列表中，走撮合
                        addNoMatchingEntrust(entrust, m);
                    } else {
                        needNoMatch = false;
                    }
                }
                if (!needNoMatch) {
                    PriceDepth priceDepth = noDealSellEntrustsMap.get(price);
                    if (priceDepth == null) {
                        noDealSellEntrustsMap.put(price, new PriceDepth().put(entrust));
                    } else {
                        priceDepth.put(entrust);
                    }
                }
            }
        }
        marketsNoDealSellEntrustMap.put(m.market, noDealSellEntrustsMap);

        //组装未撮合委托单
        loadNoMatchEntrustFromDB(m);

        initMap.put(m.market, true);

        logger.info(m.market + " [撮合交易] 重新从数据库加载撮合原始数据完成，共耗时：" + (System.currentTimeMillis() - s1) + " 毫秒。");
        logger.info(m.market + " [撮合交易] 买盘数据价格区间长度：" + (marketsNoDealBuyEntrustMap.size()) + "，委托单数量：" + noDealBuyEntrusts.size()
                + "；卖盘数据价格区间长度：" + (marketsNoDealSellEntrustMap.size()) + "，委托单数量：" + noDealSellEntrusts.size());
    }

    public static boolean isInit(Market m) {
        Boolean initResult = initMap.get(m.market);
        return null == initResult ? false : initResult;
    }

    /**
     * 定期从数据库加载撮合数据到内存中
     */
    public synchronized static void timingReload() {
        try {
            needReload = false;
            reloadResult = false;
            EntrustUpdateProcessor.resetQueue();
            reloadDBResult = false;
            for (Map.Entry<String, Market> marketEntry : Market.markets.entrySet()) {
                Market market = marketEntry.getValue();
                if (market.listenerOpen) {
                    reload(market);

                    //增加重试，防止由于数据库链接异常导致加载失败，重试3次，每次间隔3秒钟
                    int reloadCount = 1;
                    while (marketsNoMatchingEntrustMap.get(market.market).isEmpty()
                            && marketsNoDealBuyEntrustMap.get(market.market).isEmpty()
                            && marketsNoDealSellEntrustMap.get(market.market).isEmpty()
                            && reloadCount < 3) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                        }
                        reload(market);
                        reloadCount++;
                    }
                    if (marketsNoMatchingEntrustMap.get(market.market).isEmpty()
                            && marketsNoDealBuyEntrustMap.get(market.market).isEmpty()
                            && marketsNoDealSellEntrustMap.get(market.market).isEmpty()) {
                        logger.error(market.market + " [撮合交易] 从数据库加载撮合数据到内存中错误，加载盘口数据为空！！！");
                    }
                }
            }
        } catch (Exception e) {
            logger.error(" [撮合交易] 定期从数据库加载撮合数据到内存中错误！", e);
        } finally {
            reloadResult = true;
            reloadDBResult = true;
        }
    }

    private static void entrustToMap(List<Entrust> entrustList, TreeMap<BigDecimal, PriceDepth> entrustsMap) {
        for (Entrust entrust : entrustList) {
            //组装未完成的数量
            entrust.setSrcNumbers(entrust.getNumbers());
            entrust.setNumbers(entrust.getNumbers().subtract(entrust.getCompleteNumber()));

            BigDecimal price = entrust.getUnitPrice();
            PriceDepth priceDepth = entrustsMap.get(price);
            if (priceDepth == null) {
                entrustsMap.put(price, new PriceDepth().put(entrust));
            } else {
                priceDepth.put(entrust);
            }
        }
    }

    private static void loadNoMatchEntrustFromDB(Market m) {
        List<Entrust> noMatchEntrusts = Data.QueryT(m.db, NO_MATCH_ENTRUST_SQL, new Object[]{}, Entrust.class);
        logger.info(m.market + " [撮合交易] 查找未匹配过的委托单，发现了：" + noMatchEntrusts.size() + "个。");
        if (noMatchEntrusts.size() > 0) {
            for (Entrust entrust : noMatchEntrusts) {
                //封装未成交数量
                entrust.setSrcNumbers(entrust.getNumbers());
                entrust.setNumbers(entrust.getNumbers().subtract(entrust.getCompleteNumber()));
                addNoMatchingEntrust(entrust, m);
            }
        }
    }

    public synchronized static void addNoMatchingEntrust(Entrust entrust, Market m) {
        try {
            TreeMap<Long, Entrust> noMatchingMap = marketsNoMatchingEntrustMap.get(m.market);
            if (null == noMatchingMap) {
                noMatchingMap = new TreeMap<>();
                marketsNoMatchingEntrustMap.put(m.market, noMatchingMap);
            }
            noMatchingMap.put(entrust.getEntrustId(), entrust);
        } catch (Exception e) {
            logger.error(m.market + " [撮合交易] 添加未撮合委托内存失败！entrustId=" + entrust.getEntrustId(), e);
        }
    }

    public static boolean containsNoMatchingEntrust(long id, Market m) {
        try {
            return marketsNoMatchingEntrustMap.get(m.market).containsKey(id);
        } catch (Exception e) {
            logger.error(m.market + " [撮合交易] 判断委托是否存在未撮合内存中失败！entrustId=" + id, e);
            return false;
        }
    }

    public synchronized static void removeNoMatchingEntrust(long id, Market m) {
        try {
            marketsNoMatchingEntrustMap.get(m.market).remove(id);
        } catch (Exception e) {
            logger.error(m.market + " [撮合交易] 移除未撮合委托内存失败！entrustId=" + id, e);
        }
    }

    /**
     * 把委托添加到未成交委托列表中
     */
    public static void addToMarketsNoDealEntrustMap(Entrust entrust, Market market) {
        TreeMap<BigDecimal, PriceDepth> entrustsMap = new TreeMap<>();
        if (entrust.getTypes() == 1) {
            entrustsMap = marketsNoDealBuyEntrustMap.get(market.market);
        } else if (entrust.getTypes() == 0) {
            entrustsMap = marketsNoDealSellEntrustMap.get(market.market);
        }
        PriceDepth priceDepth = entrustsMap.get(entrust.getUnitPrice());
        if (priceDepth == null) {
            entrustsMap.put(entrust.getUnitPrice(), new PriceDepth().put(entrust));
        } else {
            priceDepth.put(entrust);
        }
    }

    /**
     * 从被动委托列表中移除记录
     */
    public static void removeFromMarketsNoDealEntrustMap(int types, BigDecimal price, long entrustId, Market m) {
        TreeMap<BigDecimal, PriceDepth> entrustsMap = new TreeMap<>();
        if (types == 1) {
            entrustsMap = marketsNoDealBuyEntrustMap.get(m.market);
        } else if (types == 0) {
            entrustsMap = marketsNoDealSellEntrustMap.get(m.market);
        }
        PriceDepth records = entrustsMap.get(price);
        if (null != records && records.size() > 0) {
            records.remove(entrustId);
        }
        if (null == records || records.size() <= 0) {
            entrustsMap.remove(price);
        }
    }

    /***
     * 获取未撮合委托
     */
    public static Entrust getNoMatchingEntrust(Market market) {
        if (!reloadResult) {
            return null;
        }

        if (needReload) {
            timingReload();
            return null;
        }

        Entrust entrust = null;

        //增加从数据库load数据逻辑，防止内存导致撮合停止
        if ((System.currentTimeMillis() - lastGetNoMatchEntrustTime) > backUpLoadMatchEntrustFrequency) {
            timingReload();
            lastGetNoMatchEntrustTime = System.currentTimeMillis();
        }

        TreeMap<Long, Entrust> noMatchingMap = marketsNoMatchingEntrustMap.get(market.market);

        try {
            if (noMatchingMap != null && noMatchingMap.size() > 0) {
                Map.Entry<Long, Entrust> erEntry = noMatchingMap.firstEntry();
                if (null == erEntry || null == erEntry.getValue()) {
                    return null;
                }

                entrust = erEntry.getValue();

                lastGetNoMatchEntrustTime = System.currentTimeMillis();

                //防止某个有问题的挂单导致停止撮合
                entrust.setMatchTimes(entrust.getMatchTimes() + 1);
                if (entrust.getMatchTimes() > 10) {
                    logger.error(market.market + " [撮合交易] 未撮合委托已经超过10次没匹配成功，entrustId=" + entrust.getEntrustId());
                    removeNoMatchingEntrust(entrust.getEntrustId(), market);
                    return null;
                }
            }
        } catch (Exception e) {
            logger.error(market.market + " [撮合交易] 获取未撮合委托失败！", e);
        }

        return entrust;
    }

    /***
     * 修复内存操作
     */
    public static void refreshCurrentEntrust(long entrustId, Market market) {
        logger.error(market.market + " [撮合交易] 修复委托单：" + entrustId);
        Entrust entrust = Data.GetOneT(market.db, ONE_ENTRUST_SQL, new Object[]{entrustId}, Entrust.class);

        if (null != entrust) {
            //封装未成交数量
            entrust.setSrcNumbers(entrust.getNumbers());
            entrust.setNumbers(entrust.getNumbers().subtract(entrust.getCompleteNumber()));
        }

        if (null == entrust) {
            removeNoMatchingEntrust(entrustId, market);
        } else if (entrust.getStatus() == 2) {
            removeNoMatchingEntrust(entrust.getEntrustId(), market);
            removeFromMarketsNoDealEntrustMap(entrust.getTypes(), entrust.getUnitPrice(), entrust.getEntrustId(), market);
        } else if (entrust.getStatus() == 3 && entrust.getNumbers().compareTo(entrust.getCompleteNumber()) > 0) {
            //如果委托记录没有完全成交，从主动委托移除，重新添加到被动委托中
            addToMarketsNoDealEntrustMap(entrust, market);
            removeNoMatchingEntrust(entrustId, market);
        } else if (entrust.getStatus() == 0) {
            //如果委托记录还是初始值，重新加载到主动委托中
            addNoMatchingEntrust(entrust, market);
        }
    }

    /**
     * 返回200档数据 买盘
     *
     * @param market 币种市场名称
     * @return 字符串二维数组
     * @author zhanglinbo 20170119
     */
    public static String[][] getBuyEntrustMap(String market) {
        int buyIndex = 0;//计数下标
        String[][] buyArr = new String[200][3];

        if (!reloadResult) {
            return null;
        }
        try {
            for (Map.Entry<BigDecimal, PriceDepth> entry : marketsNoDealBuyEntrustMap.get(market).entrySet()) {
                BigDecimal priceKey = entry.getKey();
                BigDecimal sNumber = entry.getValue().depth;
                if (sNumber.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                if (buyIndex < 200) {
                    String[] data = new String[3];
                    data[0] = String.valueOf(priceKey);
                    data[1] = String.valueOf(sNumber);
                    data[2] = "";
                    buyArr[buyIndex] = data;
                } else {
                    break;
                }
                buyIndex++;
            }
        } catch (Exception e) {
            logger.info("返回买盘200档数据异常，因为重新加载内存数据冲突，可以忽略");
            return null;
        }

        return buyArr;
    }

    /**
     * 返回200档数据 卖盘
     *
     * @param market 币种市场名称
     * @return 字符串二维数组
     * @author zhanglinbo 20170119
     */
    public static String[][] getSellEntrustMap(String market) {
        int sellIndex = 0;//计数下标
        String[][] sellArr = new String[200][3];

        if (!reloadResult) {
            return null;
        }
        try {
            for (Map.Entry<BigDecimal, PriceDepth> entry : marketsNoDealSellEntrustMap.get(market).entrySet()) {
                BigDecimal priceKey = entry.getKey();
                BigDecimal sNumber = entry.getValue().depth;
                if (sNumber.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                if (sellIndex < 200) {
                    String[] data = new String[3];
                    data[0] = String.valueOf(priceKey);
                    data[1] = String.valueOf(sNumber);
                    data[2] = "";
                    sellArr[sellIndex] = data;
                } else {
                    break;
                }
                sellIndex++;
            }
        } catch (Exception e) {
            logger.info("返回卖盘200档数据异常，因为重新加载内存数据冲突，可以忽略");
            return null;
        }

        return sellArr;
    }

    /**
     * 获取最新买一卖一
     *
     * @return
     */
    public static BigDecimal[] getBuyOneAndSellOne(Market m) {
        Boolean marketListened = initMap.get(m.market);
        if (null != marketListened && marketListened) {
            BigDecimal sellOne = BigDecimal.ZERO;
            BigDecimal buyOne = BigDecimal.ZERO;
            TreeMap<BigDecimal, PriceDepth> buyEntrustsMap = marketsNoDealBuyEntrustMap.get(m.market);
            TreeMap<BigDecimal, PriceDepth> sellEntrustsMap = marketsNoDealSellEntrustMap.get(m.market);

            if (sellEntrustsMap != null && sellEntrustsMap.size() > 0) {
                sellOne = sellEntrustsMap.firstKey();
            }
            if (buyEntrustsMap != null && buyEntrustsMap.size() > 0) {
                buyOne = buyEntrustsMap.firstKey();
            }

            return new BigDecimal[]{buyOne == null ? BigDecimal.ZERO : buyOne, sellOne == null ? BigDecimal.ZERO : sellOne};
        }
        return null;
    }


    static class PriceDepth {
        public TreeMap<Long, Entrust> entrusts = new TreeMap<>();
        public BigDecimal depth = BigDecimal.ZERO;

        public PriceDepth put(Entrust entrust) {
            entrusts.put(entrust.getEntrustId(), entrust);
            depth = depth.add(entrust.getSrcNumbers().subtract(entrust.getCompleteNumber()));
            return this;
        }

        public int size() {
            return entrusts.size();
        }

        public void remove(Long entrustId) {
            Entrust entrust = entrusts.remove(entrustId);
            depth = depth.subtract(entrust.getSrcNumbers().subtract(entrust.getCompleteNumber()));
        }

        public Entrust get(Long entrustId) {
            return entrusts.get(entrustId);
        }

        public boolean isEmpty() {
            return entrusts.isEmpty();
        }

        public Map.Entry<Long, Entrust> firstEntry() {
            return entrusts.firstEntry();
        }

        public void subDepth(BigDecimal number) {
            depth = depth.subtract(number);
        }
    }
}

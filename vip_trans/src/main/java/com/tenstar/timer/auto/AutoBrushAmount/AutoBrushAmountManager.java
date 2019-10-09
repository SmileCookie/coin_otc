package com.tenstar.timer.auto.AutoBrushAmount;

import com.match.entrust.MemEntrustMatchProcessor;
import com.tenstar.TimeUtil;
import com.tenstar.timer.TransRecordBean;
import com.world.config.GlobalConfig;
import com.world.data.mysql.Data;
import com.world.model.Market;
import com.world.model.daos.chart.ChartManager;
import com.world.model.entitys.record.TransRecord;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by renfei on 17/5/24.
 */
public enum AutoBrushAmountManager {
    INSTANCE;

    private static Logger log = Logger.getLogger(AutoBrushAmountManager.class.getName());

    private static Logger syncTradesLogP = Logger.getLogger("syncTradesLogP");

    private static final BigDecimal BASE_GAP = BigDecimal.valueOf(0.98);//卖一买一基础差距,如果大于这个值则不再刷量

    private static long displayTime = DateUtils.MILLIS_PER_MINUTE * 5; //增长曲线有效时间

    private static BigDecimal lastPrice = BigDecimal.ZERO;//记录上一次的成交价

    private static int yy ;


    private static final AtomicBoolean needSub = new AtomicBoolean(Boolean.FALSE);


    /**
     * 从其他站同步交易数据作为价格预热
     * @param price
     * @param amount
     * @param orderTypes
     * @param m
     */
    public void syncTrades(BigDecimal buyOne, BigDecimal sellOne, BigDecimal price, BigDecimal amount, String orderTypes, Market m){

        String prod = GlobalConfig.getValue("isProd");

        Boolean isProd = Boolean.valueOf(prod);


        if(isProd){
            if(needSub.get()){
                BigDecimal[] buysell = ChartManager.getLbuyOneAndSellOne(m);
                if(buysell == null){
                    log.error("买一，卖一尚未初始化,不能刷单！");
                    return;
                }
                BigDecimal buy = buysell[0];
                BigDecimal sell = buysell[1];


                BigDecimal bitglobalPrice = ChartManager.getPrice(m);// FIXME 这个价格波动特别大......具体看autobrushamount.log日志


//        BigDecimal pr = bitglobalPrice.divide(price, 10, RoundingMode.HALF_UP);//根据交易价格比例缩放
//        BigDecimal finalPrice = price.divide(pr, 10, RoundingMode.HALF_UP);


                BigDecimal finalPrice;
                if("buy".equalsIgnoreCase(orderTypes)){
                    BigDecimal pr = buyOne.divide(buy, 10, RoundingMode.HALF_UP);//根据交易价格比例缩放
                    finalPrice = price.divide(pr, 10, RoundingMode.HALF_UP);

                    //如果计算的最终价格小于买1则修改为买1价格
                    if(finalPrice.compareTo(buy) < 0){
                        finalPrice = buy;
                    }

                    insertRecord(finalPrice, amount, 1,m);

                }else{


                    BigDecimal pr = sellOne.divide(sell, 10, RoundingMode.HALF_UP);//根据交易价格比例缩放
                    finalPrice = price.divide(pr, 10, RoundingMode.HALF_UP);

                    //如果计算的最终价格大于卖1则修改为卖1价格
                    if(finalPrice.compareTo(sell) > 0){
                        finalPrice = sell;
                    }

                    insertRecord(finalPrice, amount, 0,m);
                }

                syncTradesLogP.info("===sync trades==="+ String.format("bitglobalBuyOne:%s, bitglobalSellOne:%s, bitglobalPrice:%s, " +
                                "buyOne:%s,sellOne:%s,price:%s,amount:%s,orderTypes:%s,coinType:%s,finalPrice:%s",
                        buy, sell, bitglobalPrice, buyOne, sellOne, price, amount, orderTypes, m.getMarket(), finalPrice));

            }else{
                if("buy".equalsIgnoreCase(orderTypes)){


                    BigDecimal[] buysell = ChartManager.getLbuyOneAndSellOne(m);
                    if(buysell != null){
                        BigDecimal buy = buysell[0];

                        if(price.compareTo(buy) < 0){
//                        needSub.set(Boolean.TRUE);
//                        return;
                        }
                    }

                    insertRecord(price, amount, 1,m);

                }else{

                    BigDecimal[] buysell = ChartManager.getLbuyOneAndSellOne(m);
                    if(buysell != null){
                        BigDecimal sell = buysell[1];

                        if(price.compareTo(sell) > 0){
//                        needSub.set(Boolean.TRUE);
//                        return;
                        }
                    }

                    insertRecord(price, amount, 0,m);
                }



                syncTradesLogP.info("===sync trades==="+ String.format("buyOne:%s,sellOne:%s,price:%s,amount:%s,orderTypes:%s,coinType:%s",
                        buyOne, sellOne, price, amount, orderTypes, m.getMarket()));
            }

        }else{
            if("buy".equalsIgnoreCase(orderTypes)){


                insertRecord(price, amount, 1,m);

            }else{

                insertRecord(price, amount, 0,m);
            }

            syncTradesLogP.info("===sync trades==="+ String.format("buyOne:%s,sellOne:%s,price:%s,amount:%s,orderTypes:%s,coinType:%s",
                    buyOne, sellOne, price, amount, orderTypes, m.getMarket()));
        }

    }

    /**
     * 插入一条成交记录
     * @param price
     * @param amount
     * @param types
     */
    public void insertRecord(BigDecimal price, BigDecimal amount, int types, Market m) {
        int userIdBuy = 1089;
        int userIdSell = 1066;

        long now = TimeUtil.getNow().getTime();
        long nowMinu = TimeUtil.getMinuteFirst().getTime();

        int status = 2;//将他设置成成功，避免资金问题

        int webIdBuy = 8;
        int webIdSell = 8;

        BigDecimal thisMoney =price.multiply(amount);
        long autoId=com.tenstar.autoId.getId(m.getMarket()+"entrust",m.db);
        long autoId2=com.tenstar.autoId.getId(m.getMarket()+"entrust",m.db);
        long recordId = Data.Insert(m.db,
                "INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell,status,actStatus) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                new Object[] {
                        price,
                        thisMoney,
                        amount,
                        autoId,
                        userIdBuy,
                        autoId2,
                        userIdSell,
                        types,//当前记录是买行为还是卖行为
                        now,
                        nowMinu,
                        webIdBuy,
                        webIdSell,
                        status, //将他设置成成功，避免资金问题
                        0
                });
        //更新队列
        MemEntrustMatchProcessor.da.updateTicker( price, amount, types,m);
//        MemEntrustMatchProcessor.da.updateRecord(recordId,unitPrice,numbers,types,TimeUtil.getNow().getTime());
        log.info("说明：[刷量]新增数据transrecord_id:" + recordId);
        //重新生成成交记录
//        MemEntrustMatchProcessor.da.SetTrade();

        //更新缓存数据
        boolean isSame = Boolean.FALSE;
        //动态增加成交记录到缓存。提高性能速度
        TransRecord tr = new TransRecord();
        tr.setTransRecordId(recordId);
        tr.setUnitPrice(price);
        tr.setTotalPrice(thisMoney);
        tr.setNumbers(amount);
        tr.setEntrustIdBuy(autoId);
        tr.setUserIdBuy(userIdBuy);
        tr.setEntrustIdSell(autoId2);
        tr.setUserIdSell(userIdSell);
        tr.setTypes(types);
        tr.setTimes(now);
        tr.setTimeMinute(nowMinu);
        tr.setStatus(status);
        tr.setIsCount(0);
        tr.setWebIdBuy(webIdBuy);
        tr.setWebIdSell(webIdSell);

        ChartManager.addNewTransRecord(tr,m);

        TransRecordBean beb = new TransRecordBean();
        beb.setTransRecordId(recordId);
        beb.setUnitPrice(price);
        beb.setTotalPrice(thisMoney);
        beb.setNumbers(amount);
        beb.setEntrustIdBuy(autoId);
        beb.setUserIdBuy(userIdBuy);
        beb.setEntrustIdSell(autoId2);
        beb.setUserIdSell(userIdSell);
        beb.setTypes(types);
        beb.setTimes(now);
        beb.setTimeMinute(nowMinu);
        beb.setStatus(status);
        beb.setIsCount(0);
        beb.setWebIdBuy(webIdBuy);
        beb.setWebIdSell(webIdSell);
        MemEntrustMatchProcessor.da.setTraderecordToMem(userIdBuy,beb , false,m);
        MemEntrustMatchProcessor.da.setTraderecordToMem(userIdSell,beb , isSame,m);
    }



    /**
     * 刷量逻辑
     *
     * FIXME 后续代码可以迁移到Caesar
     */
    @Deprecated
    public void brushAmount(BigDecimal amount, Market m){


        /**
         * 下面是制造买单卖单的价格的逻辑.这个逻辑是之前chbtc的,可以进行修改
         *
         *
         * FIXME 可以重新定制
         */
        BigDecimal[] buysell = ChartManager.getLbuyOneAndSellOne(m);
        if(buysell == null){
            log.error("买一，卖一尚未初始化,不能刷单！");
            return;
        }
        BigDecimal buy = buysell[0];
        BigDecimal sell = buysell[1];

        log.info(String.format("当前买一卖一价格:%s,%s", buy, sell));

        //如果买一卖一价格相差比较近则不处理
        if(buy.divide(sell, 4, BigDecimal.ROUND_HALF_UP).compareTo(BASE_GAP) >=0 ){
            log.error("买一，卖一相差较近不需要刷量！");
            return;
        }


        //价格
        BigDecimal rate = this.randomPrice(buy, sell);



        long r = System.currentTimeMillis() % displayTime;

        /**
         *
         * 写入买卖数据
         *
         */
        if(RandomUtils.nextInt(10) % 2 == 0){//买


            if(lastPrice.compareTo(BigDecimal.ZERO) == 0){

                lastPrice = buy;
            }

            BigDecimal price;

            if(r == 0){
                if(yy == 0){//升价
                    price = lastPrice.add(rate);
                }else{//降价
                    price = lastPrice.subtract(rate);
                }
            }else{
                yy = RandomUtils.nextInt(2);
                if(yy == 0){//升价
                    price = lastPrice.add(rate);
                }else{//降价
                    price = lastPrice.subtract(rate);
                }
            }

            if(price.compareTo(sell) >= 0){
                log.error("制造的买价格超过卖1价格,退出！"+price);
                return;
            }


            //在这个位置下一个买记录
            insertRecord(price, amount, 1,m);
            log.info("[刷买]已成功刷一笔买单，价格=" + price + "，数量="+amount);

            lastPrice = price;
        }else{//卖

            BigDecimal price = sell.subtract(rate);

            //在这个位置下一个卖记录
            insertRecord(price, amount, 0,m);
            log.info("[刷买]已成功刷一笔卖单，价格=" + price + "，数量="+amount);

        }



    }

    /**
     * 通过买一卖一价格随即一个买卖合理的增幅差价
     * @param buy
     * @param sell
     * @return
     */
    @Deprecated
    public BigDecimal randomPrice(BigDecimal buy, BigDecimal sell){

        //求买一卖一中间的差额
        BigDecimal subData = sell.subtract(buy);

        int d = RandomUtils.nextInt(10);// FIXME 需要调整
        double percent = (double)d / 100d;

        BigDecimal result = subData.multiply(BigDecimal.valueOf(percent));
        return result;

    }
}

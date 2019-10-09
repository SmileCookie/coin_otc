//package com.world.model.backcapital;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.tenstar.HTTPTcp;
//import com.tenstar.Message;
//import com.tenstar.MessageCancle;
//import com.world.model.dao.fee.FeeDao;
//import com.world.model.dao.pay.PayUserDao;
//import com.world.model.dao.task.Worker;
//import com.world.model.entity.Market;
//import com.world.model.entity.financial.fee.Fee;
//import com.world.model.entity.pay.PayUserBean;
//import com.world.model.entity.usercap.dao.CommAttrDao;
//import com.world.model.entity.usercap.entity.CommAttrBean;
//import com.world.util.date.TimeUtil;
//import com.world.util.request.HttpUtil;
//import com.world.web.action.Action;
//import org.apache.commons.lang.math.RandomUtils;
//import org.apache.log4j.Logger;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 回购功能定时任务
// * Created by buxianguan on 17/8/4.
// */
//public class BackCapitalWorkerBack extends Worker {
//    private final static Logger log = Logger.getLogger(BackCapitalWorkerBack.class);
//
//    private CommAttrDao commAttrDao = new CommAttrDao();
//    private PayUserDao payUserDao = new PayUserDao();
//    private FeeDao feeDao = new FeeDao();
//
//    private volatile boolean running = false;
//
//    public BackCapitalWorkerBack(String name, String des) {
//        super(name, des);
//    }
//
//    @Override
//    public void run() {
//        try {
//            super.run();
//
//            log.info("回购功能定时任务开始...");
//
//            if (running) {
//                log.info(super.getName() + "-上一个任务还没有执行完毕,等待下一个轮询");
//                return;
//            }
//            running = true;
//
//            //获取今天8点到昨天8点之间的手续费汇总数据
//            BigDecimal dbExchangeFee = get24HourExchangeFee();
//            if (null == dbExchangeFee || dbExchangeFee.compareTo(BigDecimal.ZERO) <= 0) {
//                log.info("手续费汇总数据<=0, dbExchangeFee=" + dbExchangeFee);
//                return;
//            }
//
//            //根据公式计算本次回购资金
//            BigDecimal backCapital = givePoolCapitalByExchangeFee(dbExchangeFee);
//            log.info("手续费汇总数据：dbExchangeFee = " + dbExchangeFee + ",本次回购资金：backCapital = " + backCapital);
//
//            //获取回购账户userId
//            CommAttrBean commAttrBean = commAttrDao.queryByAttrTypeAndParaCode(10000002, "01");
//            if (null == commAttrBean) {
//                log.error("回购账户为null！");
//                return;
//            }
//            String userId = commAttrBean.getParaValue();
//
//            //获取回购账户可用资金（缓冲池）BTC
//            PayUserBean payUser = payUserDao.getByUserIdAndFundsType(userId, 2);
//            if (null == payUser) {
//                log.error("获取回购账户可用资金为null！");
//                return;
//            }
//            log.info("回购账户可用资金为：" + payUser.getBalance().toPlainString());
//
//            //判断是否启动回购任务
//            if (payUser.getBalance().compareTo(backCapital) < 0) {
//                log.info("回购账户资金<本次回购资金，不启动回购任务");
//                return;
//            }
//
//            //本次剩余回购资金
//            BigDecimal surplusBackCapital = backCapital;
//            //回购任务开始时间
//            long startTime = System.currentTimeMillis();
//
//            while (true) {
//                //获取委托下单的等待时间
//                int sleepTime = getSleepTime(userId, backCapital, surplusBackCapital);
//                if (sleepTime < 0) {
//                    break; //获取等待时间内部出错了
//                }
//                Thread.sleep(sleepTime * 1000);
//
//                //判断是否最后一笔下单，如果是，撤销之前委托单，防止委托单一直不成交
//                //移到这里执行，防止获取买一卖一价异常，无法撤销未成交的委托单
//                if (surplusBackCapital.compareTo(backCapital.multiply(new BigDecimal("0.02"))) <= 0) {
//                    //撤销之前委托单，防止委托单一直不成交
//                    BigDecimal maxPrice = new BigDecimal("1000000");
//                    cancel(userId, "gbc", BigDecimal.ZERO, maxPrice);
//                }
//
//                //获取买1 Y 和卖1价 X
//                BigDecimal[] buyOneSellOne = getBuyOneSellOne("gbc_btc");
//                if (null == buyOneSellOne) {
//                    log.error("获取买一卖一价异常！");
//                    break;
//                }
//
//                BigDecimal buyOne = buyOneSellOne[0];
//                BigDecimal sellOne = buyOneSellOne[1];
//                log.info("买一价卖一价:buyOne=" + buyOne + ",sellOne=" + sellOne);
//
//                //撤单价格上限：买价的(1-4%)
//                BigDecimal cancelPrice = buyOne.multiply(new BigDecimal("0.96"));
//                //撤单
//                cancel(userId, "gbc", BigDecimal.ZERO, cancelPrice);
//
//                //判断是否最后一笔下单，如果是，将本次回购剩余资金全部委托下单，价格卖价的150%
//                if (surplusBackCapital.compareTo(backCapital.multiply(new BigDecimal("0.02"))) <= 0) {
//                    BigDecimal unitPrice = sellOne.multiply(new BigDecimal("1.5"));
//                    log.info("最后一笔下单:capital=" + surplusBackCapital + ",unitPrice=" + unitPrice);
//
//                    //委托下买单
//                    buy(userId, "gbc", surplusBackCapital, unitPrice);
//                    break;
//                }
//
//                /* 产品需求，暂时去掉生命周期的判断
//                //判断回购生命周期，如果超过5小时，将本次回购剩余资金全部委托下单，价格卖价的150%
//                if ((System.currentTimeMillis() - startTime) > 5 * 60 * 60 * 1000) {
//                    BigDecimal unitPrice = sellOne.multiply(new BigDecimal("1.5"));
//                    log.info("生命周期超过5小时，最后一笔下单:capital=" + surplusBackCapital + ",unitPrice=" + unitPrice);
//
//                    //委托下买单
//                    buy(userId, "gbc", surplusBackCapital, unitPrice);
//                    break;
//                }*/
//
//                //根据公式决定执行哪个策略
//                int strategy = decideStrategy(buyOne, sellOne);
//                log.info("执行策略：" + strategy);
//                switch (strategy) {
//                    case 1:
//                        surplusBackCapital = doStrategyOne(userId, backCapital, surplusBackCapital, buyOne);
//                        break;
//                    case 2:
//                        surplusBackCapital = doStrategyTwo(userId, backCapital, surplusBackCapital, sellOne);
//                        break;
//                }
//            }
//        } catch (Exception e) {
//            log.error(e, e);
//        } finally {
//            running = false;
//        }
//    }
//
//    /**
//     * 获取 今天早上8点到上一天早上8点 24小时之内的手续费汇总(BTC)
//     */
//    private BigDecimal get24HourExchangeFee() {
//        long startTime = 0l;
//        long endTime = 0l;
//
//        Timestamp now = TimeUtil.getNow();
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String dayStr = sdf.format(now);
//        Timestamp compareTime = Timestamp.valueOf(dayStr + " 08:00:00");
//
//        Calendar c = Calendar.getInstance();
//        c.setTime(compareTime);
//        if (now.compareTo(compareTime) < 0) {
//            c.add(Calendar.DATE, -1);
//            endTime = c.getTimeInMillis();
//
//            c.add(Calendar.DATE, -1);
//            startTime = c.getTimeInMillis();
//        } else {
//            endTime = compareTime.getTime();
//            c.add(Calendar.DATE, -1);
//            startTime = c.getTimeInMillis();
//        }
//
//        //获取 交易手续费 汇总数据
//        Fee fee = feeDao.getSumFeeByTypeAndTime(1, "BTC", startTime, endTime);
//        return fee.getAmount();
//    }
//
//    /**
//     * 0-0.25		0.05
//     * 0.25-0.5		0.1
//     * 0.5-0.75		0.15
//     * 0.75-1 		0.2
//     * ... 以此类推
//     * <p/>
//     * 根据24小时成交量折合的千分之2手续费来计算从本次回购任务的资金量
//     *
//     * @param dbExchangeFee 24小时成交量折合的手续费收益
//     */
//    private BigDecimal givePoolCapitalByExchangeFee(BigDecimal dbExchangeFee) {
//        /*返回本次回购资金*/
//        BigDecimal backCapital = null;
//        /*交易手续费基数，缓存池资金基数*/
//        BigDecimal exchangeFeeBase = new BigDecimal("0.25").setScale(2);
//        BigDecimal poolCapitalBase = new BigDecimal("0.05").setScale(2);
//
//		/*小于0.25*/
//        if (dbExchangeFee.compareTo(exchangeFeeBase) <= 0) {
//            backCapital = poolCapitalBase;
//        } else {
//            //大于0.25时的处理
//            //dbExchangeFee 除以 exchangeFeeBase 向上取整再乘以 pullCapitalBase
//            backCapital = dbExchangeFee.divide(exchangeFeeBase, 0, RoundingMode.UP);
//            backCapital = backCapital.multiply(poolCapitalBase);
//        }
//        return backCapital;
//    }
//
//    /**
//     * 根据回购账户汇集资金是否超过单次回购资金来确定等待时间（除去本次回购剩余资金）
//     */
//    private int getSleepTime(String userId, BigDecimal backCapital, BigDecimal thisSurplusBackCapital) {
//        try {
//            //获取回购账户可用资金（缓冲池）BTC
//            PayUserBean payUser = payUserDao.getByUserIdAndFundsType(userId, 2);
//            if (null == payUser) {
//                log.error("获取回购账户可用资金为null！");
//                return -1;
//            }
//
//            int max = 0;
//            int min = 0;
//
//            //除去本次回购剩余资金，回购账户可用的资金数
//            BigDecimal totalSurplusCapital = payUser.getBalance().subtract(thisSurplusBackCapital);
//            log.info("除去本次回购剩余资金，回购账户可用的资金数=" + totalSurplusCapital);
//
//            //如果没有超过单次回购资金，等待时间60s到180s
//            if (totalSurplusCapital.compareTo(backCapital) < 0) {
//                max = 180;
//                min = 60;
//            } else if (totalSurplusCapital.compareTo(backCapital.multiply(new BigDecimal(2))) < 0) {
//                //如果没有超过2倍的单次回购资金，等待时间60s到120s
//                max = 120;
//                min = 60;
//            } else {
//                //如果超过2倍的单次回购资金，等待时间0s到60s
//                max = 60;
//                min = 0;
//            }
//
//            int sleepTime = RandomUtils.nextInt(max - min + 1) + min;
//            log.info("sleepTime=" + sleepTime);
//
//            return sleepTime;
//        } catch (Exception e) {
//            log.error(e, e);
//        }
//
//        return -1;
//    }
//
//    /**
//     * 确定执行何种策略
//     */
//    private int decideStrategy(BigDecimal buyOne, BigDecimal sellOne) {
//        //(X-Y-Y*2%>0) -> (X-1.02Y>0) -> (X>1.02Y)
//        BigDecimal incrBuy = buyOne.multiply(new BigDecimal("1.02"));
//        if (sellOne.compareTo(incrBuy) > 0) {
//            return 1; //策略一
//        } else {
//            return 2; //策略二
//        }
//    }
//
//    /**
//     * 策略一：补盘口
//     */
//    private BigDecimal doStrategyOne(String userId, BigDecimal backCapital, BigDecimal surplusBackCapital, BigDecimal buyOne) {
//        //1.回购任务资金(backCapital)的2% 拆分成4等份（金额）
//        //2.价格按照买1价差额5% 递增
//        //3.注意拆分成4份的时候,不可按照数学公式,最后1笔=总量-前3份
//        BigDecimal entrustCapital = backCapital.multiply(new BigDecimal("0.02"));
//        BigDecimal eachEntrustCapital = entrustCapital.divide(new BigDecimal("4")); //每次的金额
//        BigDecimal surplusEntrustCapital = entrustCapital;
//
//        //循环委托下单
//        for (int i = 0; i < 4; i++) {
//            BigDecimal incr = new BigDecimal("0.005").setScale(3);
//            BigDecimal unitPrice = buyOne.multiply(BigDecimal.ONE.add(incr.multiply(new BigDecimal(i + 1))));
//            if (i == 3) {
//                //最后一笔，取总量减去前3笔的差值
//                eachEntrustCapital = surplusEntrustCapital;
//            } else {
//                surplusEntrustCapital = surplusEntrustCapital.subtract(eachEntrustCapital);
//            }
//
//            //委托下买单
//            buy(userId, "gbc", eachEntrustCapital, unitPrice);
//
//            //更新本次剩余回购资金
//            surplusBackCapital = surplusBackCapital.subtract(eachEntrustCapital);
//            log.info("本次剩余回购资金：" + surplusBackCapital);
//        }
//        return surplusBackCapital;
//    }
//
//    /**
//     * 策略二：三角委托
//     */
//    private BigDecimal doStrategyTwo(String userId, BigDecimal backCapital, BigDecimal surplusBackCapital, BigDecimal sellOne) {
//        //1.回购任务资金(backCapital)的1% 按比例拆成3份 (5:3:2)（资金）
//        //2.3份价格按照卖1价差额5% 递减
//        //3.注意拆分成3份的时候,不可按照数学公式,最后1笔=总量-前2份（资金）
//        BigDecimal entrustCapital = backCapital.multiply(new BigDecimal("0.01"));
//        BigDecimal tenEachEntrustCapital = entrustCapital.divide(BigDecimal.TEN);
//        BigDecimal surplusEntrustCapital = entrustCapital;
//        int[] share = {5, 3, 2};
//
//        //循环委托下单
//        for (int i = 0; i < 3; i++) {
//            BigDecimal decr = new BigDecimal("0.005").setScale(3);
//            BigDecimal unitPrice = sellOne.multiply(BigDecimal.ONE.subtract(decr.multiply(new BigDecimal(i))));
//            BigDecimal eachEntrustCapital = tenEachEntrustCapital.multiply(new BigDecimal(share[i]));
//            if (i == 2) {
//                //最后一笔，取总量减去前2笔的差值
//                eachEntrustCapital = surplusEntrustCapital;
//            } else {
//                surplusEntrustCapital = surplusEntrustCapital.subtract(eachEntrustCapital);
//            }
//
//            //委托下买单
//            buy(userId, "gbc", eachEntrustCapital, unitPrice);
//
//            //更新本次剩余回购资金
//            surplusBackCapital = surplusBackCapital.subtract(eachEntrustCapital);
//            log.info("本次剩余回购资金：" + surplusBackCapital);
//        }
//        return surplusBackCapital;
//    }
//
//    /**
//     * 获取买一卖一价
//     */
//    private BigDecimal[] getBuyOneSellOne(String coinType) {
//        try {
//            Map<String, String> params = new HashMap<>();
//            params.put("coinType", coinType);
//            JSONObject json = HttpUtil.getJson(Action.TRANS_DOMAIN + "/server/buyOneSellOne", params, 3000, 3000, false);
//            if (null == json) {
//                log.error("获取买一卖一价 return null");
//                return null;
//            }
//            if (null == json.getInteger("status") || json.getInteger("status") != 0) {
//                log.error("获取买一卖一价 return status not 1, return:" + json.toJSONString());
//                return null;
//            }
//
//            JSONArray message = json.getJSONArray("message");
//            BigDecimal buyOne = new BigDecimal(String.valueOf(message.get(0)));
//            BigDecimal sellOne = new BigDecimal(String.valueOf(message.get(1)));
//
//            return new BigDecimal[]{buyOne, sellOne};
//        } catch (IOException e) {
//            log.error(e, e);
//        }
//
//        return null;
//    }
//
//    /**
//     * 撤销一定范围内的委托单
//     */
//    private void cancel(String userId, String currency, BigDecimal priceLow, BigDecimal priceHigh) {
//        try {
//            JSONObject m = getMarketByCurrency(currency);
//            if (m == null) {
//                log.error(currency + " 币种找不到对应的市场,无法撤销委托单");
//                return;
//            }
//
//            log.info("撤单最高价：" + priceHigh);
//
//            int type = 1;//0// 按照区间设置 1取消买入 2取消卖出 3 取消所有
//
//            MessageCancle myObj = new MessageCancle();
//            myObj.setUserId(Integer.parseInt(userId));
//            myObj.setWebId(6); //TODO 这个值是多少？
//            myObj.setPriceLow(priceLow);
//            myObj.setPriceHigh(priceHigh);
//            myObj.setType(type);
//            myObj.setMarket(m.getString("market"));
//
//            String param = HTTPTcp.ObjectToString(myObj);
//            String rtn = HTTPTcp.Post(m.getString("ip"), m.getIntValue("port"), "/server/cancelmore", param);
//            MessageCancle rtn2 = (MessageCancle) HTTPTcp.StringToObject(rtn);
//            log.info("撤单返回结果：message=" + rtn2.getMessage() + ",status=" + rtn2.getStatus());
//
//        } catch (Exception ex) {
//            log.error(ex, ex);
//        }
//    }
//
//    /**
//     * 委托下买单
//     *
//     * @param userId
//     * @param currency
//     * @param entrustCapital 下单资金
//     * @param unitPrice      下单价格
//     */
//    private void buy(String userId, String currency, BigDecimal entrustCapital, BigDecimal unitPrice) {
//        try {
//            JSONObject m = getMarketByCurrency(currency);
//            if (m == null) {
//                log.error(currency + " 币种找不到对应的市场，无法进行委托下单");
//                return;
//            }
//
//            log.info("委托下买单原始参数：entrustCapital=" + entrustCapital + ",unitPrice=" + unitPrice);
//
//            //数量等于下单资金数除以价格，按照币种配置的数量小数点位数取 DOWN
//            BigDecimal number = entrustCapital.divide(unitPrice, m.getIntValue("numberBixDian"), RoundingMode.DOWN);
//            //价格按照币种配置的数量小数点位数取 DOWN
//            unitPrice = unitPrice.setScale(m.getIntValue("exchangeBixDian"), RoundingMode.DOWN);
//
//            log.info("委托下买单请求参数：number=" + number + ",unitPrice=" + unitPrice);
//
//            Message myObj = new Message();
//            myObj.setUserId(Integer.parseInt(userId));
//            myObj.setWebId(6); //TODO 这个值是多少？
//            myObj.setNumbers(number);
//            myObj.setTypes(1); //0：卖 1：买
//            myObj.setUnitPrice(unitPrice);
//            myObj.setStatus(0);
//            myObj.setMarket(m.getString("market"));//市场名称
//
//            String param = HTTPTcp.ObjectToString(myObj);
//            String rtn = HTTPTcp.Post(m.getString("ip"), m.getIntValue("port"), "/server/entrust", param);
//            Message rtn2 = (Message) HTTPTcp.StringToObject(rtn);
//
//            log.info("委托下买单返回结果：message=" + rtn2.getMessage() + ",code=" + rtn2.getStatus() + ",entrustId=" + rtn2.getNumbers());
//        } catch (Exception ex) {
//            log.error(ex.toString(), ex);
//        }
//    }
//
//    /**
//     * 根据币种获取盘口配置的市场信息
//     */
//    private JSONObject getMarketByCurrency(String currency) {
//        Map<String, JSONObject> marketMaps = Market.getMarketsMap();//获取盘口配置信息
//        JSONObject m = null;//市场信息
//        if (marketMaps != null && !marketMaps.isEmpty()) {
//            for (Map.Entry<String, JSONObject> entry : marketMaps.entrySet()) {
//                JSONObject temp = entry.getValue();
//                if (temp.getString("numberBi").equalsIgnoreCase(currency)) {
//                    m = temp;
//                    break;
//                }
//            }
//        }
//        return m;
//    }
//
//    public static void main(String[] args) {
//        BackCapitalWorkerBack worker = new BackCapitalWorkerBack("BackCapitalWorker", "回购功能定时任务");
//        worker.run();
//    }
//
//}

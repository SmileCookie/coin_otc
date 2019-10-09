package com.world.model.balaccount.job.wallettrans;

import com.alibaba.fastjson.JSON;
import com.world.data.database.DatabasesUtil;
import com.world.model.balaccount.dao.FinAccWalDailyAccDao;
import com.world.model.balaccount.entity.WalletSelfCheck;
import com.world.model.balaccount.job.wallettrans.thread.WalletTransThread;
import com.world.model.dao.task.Worker;
import com.world.model.entity.coin.CoinProps;
import com.world.util.date.TimeUtil;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WalletTransWork extends Worker {

    FinAccWalDailyAccDao finAccWalDailyAccDao = new FinAccWalDailyAccDao();
    /*上次更新时间默认为当天,即明天凌晨执行数据同步; 如果要改为前天:TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(1));*/
    private Timestamp lastUpdateTime = TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1));

    /*记录日志*/
    private String logContent = "";

    private static Logger log = Logger.getLogger(WalletTransWork.class);

    public WalletTransWork(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
        try {

            /**
             * 本定时任务分为两部分
             * 1、第一部分发送请求到支付中心API请求钱包对账流水
             * 2、进行钱包资金自检，将自检异常的信息预警到钉钉报警群
             */

            /**
             * 第一部分：发送请求到支付中心API请求钱包对账流水
             */
            /*1.1、记录钱包流水同步开始时间*/
            long startTime = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date nowDate = new Date();
            String workStartTime = sdf.format(nowDate);
            log.info("【新对账WalletTransWork】开始同步支付中心钱包流水,workStartTime:" + workStartTime);

            /*1.2、创建一个可重用固定线程数的线程池*/
            ExecutorService finAccWalBillWorkPool = Executors.newFixedThreadPool(1);

            /*1.3、获取已上线币种配置*/
            Map<String, CoinProps> newCoinProps = DatabasesUtil.getNewCoinPropMaps();
            Iterator<Map.Entry<String, CoinProps>> newItemCoinProps = newCoinProps.entrySet().iterator();

            /*1.4、循环进行钱包流水抽取，从支付中心同步钱包流水
             *   fundType 资金类型
             *   fundTypeName 资金名称
             * */
            int fundType = 0;
            String fundTypeName = "";
            while (newItemCoinProps.hasNext()) {
                Map.Entry<String, CoinProps> entryCoinProps = newItemCoinProps.next();
                CoinProps coinProps = entryCoinProps.getValue();
                fundType = coinProps.getFundsType();
                fundTypeName = entryCoinProps.getKey();
                log.info("【新对账WalletTransWork】钱包流水同步币种名称fundTypeName = " + fundTypeName + ", 币种编号fundType = " + fundType);

                /*当fundType > 0,创建线程,按每个币种进行处理查询*/
                if (fundType > 0) {
                    WalletTransThread walletTransThread = new WalletTransThread(fundType, fundTypeName, coinProps);
                    finAccWalBillWorkPool.execute(walletTransThread);
                } else {
                    log.info("【新对账WalletTransWork】币种" + fundTypeName + "fundType<=0,无法同步!...");
                }
            }

            /*1.5 按过去执行已提交任务的顺序发起一个有序的关闭，但是不接受新任务。如果已经关闭，则调用没有其他作用。*/
            finAccWalBillWorkPool.shutdown();

            /*1.6 校验同步钱包流水执行状态.10s一次*/
            while (true) {
                /*如果关闭后所有任务都已完成，则返回 true。注意，除非首先调用 shutdown 或 shutdownNow，否则 isTerminated 永不为 true。*/
                if (finAccWalBillWorkPool.isTerminated()) {
                    break;
                } else {
                    log.info("【新对账WalletTransWork】钱包对账流水同步查询进行中...");
                    Thread.sleep(10 * 1000L);
                }
            }

            /*1.7、记录钱包流水同步结束时间*/
            log.info("【新对账WalletTransWork】钱包流水同步查询结束!!!【核算耗时：" + (System.currentTimeMillis() - startTime) + "】");

            /**
             * 第二部分：进行钱包资金自检，将自检异常的信息预警到钉钉报警群
             */

            /*2.1、记录钱包资金自检开始时间*/
            nowDate = new Date();
            workStartTime = sdf.format(nowDate);
            log.info("【新对账WalletTransWork】开始进行钱包资金自检,checkStartTime:" + workStartTime);

            /*2.2、所需变量定义*/
            int fundsType = 0;
            String fundsTypeName = "";

            String msgSuccessCold = "QBTX【冷钱包自检正常的币种有】";
            String msgErrorCold = "QBZJ【冷钱包自检异常的币种有】";
            boolean msgErrorFlagCold = false;

            String msgSuccessHot = "QBTX【热冲热提钱包自检正常的币种有】";
            String msgErrorHot = "QBZJ【热冲热提钱包自检异常的币种有】";
            boolean msgErrorFlagHot = false;

            String msgNo = "QBTX【暂未配置币种冷钱包余额查询地址,无法检查冷钱包余额的币种有】";
            String msgNull = "QBTX【FinAccWalDailyAccDao#getWalletSelf 钱包数据为空的有】";
            StringBuffer nullStringBuffer = new StringBuffer();

            /**
             * 2.3、获取区块钱包和平台同步钱包流水金额统计信息
             * 从运营后台接口 /walletBalance/queryList 获取walletTranBill钱包每日对账信息
             * 从支付中心接口 /openapi/tradingcenter/finance/hotWalletBalance 获取热冲热提同步余额
             * 爬虫获取区块冷钱包余额
             */
            WalletSelfCheck walletSelfCheck = finAccWalDailyAccDao.getWalletSelf();
            log.info("【新对账WalletTransWork】WalletSelfCheck对象信息：" + JSON.toJSONString(walletSelfCheck));

            Map<Integer, BigDecimal> warmRechargeMap = walletSelfCheck.getWarmRechargeMap();//钱包流水-热充
            Map<Integer, BigDecimal> warmWithdrawMap = walletSelfCheck.getWarmWithdrawMap();//钱包流水-热提
            Map<Integer, BigDecimal> coldValueMap = walletSelfCheck.getColdValueMap();//钱包流水-冷钱包

            Map<Integer, BigDecimal> warmRechargeFinaccwalletbillMap = walletSelfCheck.getWarmRechargeFinaccwalletbillMap();//支付中心-热充同步余额
            Map<Integer, BigDecimal> warmWithdrawFinaccwalletbillMap = walletSelfCheck.getWarmWithdrawFinaccwalletbillMap();//支付中心-热提同步余额
            Map<Integer, BigDecimal> browserBalanceMap = walletSelfCheck.getBrowserBalanceMap();//法宠-区块链浏览器冷钱包同步余额

            Map<Integer, String> apiReqFlag = walletSelfCheck.getApiReqFlag();//是否配置了爬虫

            /*2.4、获取配置文件中的币种信息*/
            Iterator<Map.Entry<String, CoinProps>> iteCoinPropss = newCoinProps.entrySet().iterator();

            /*2.5、循环新币种配置文件，校验同步到的热充热提和冷钱包发生额是否与区块查询的一致*/
            while (iteCoinPropss.hasNext()) {

                /*2.5.1、获取币种配置信息*/
                Map.Entry<String, CoinProps> entryCoinProps = iteCoinPropss.next();
                CoinProps coinProps = entryCoinProps.getValue();
                fundsType = coinProps.getFundsType();
                fundsTypeName = entryCoinProps.getKey();
                int agreement = DatabasesUtil.newCoinPropsByName(fundsTypeName).getAgreement();
                log.info("【新对账WalletTransWork】钱包资金自检币种名称fundsTypeName = " + fundsTypeName
                        + ", 币种编号fundsType = " + fundsType + ",agreement=" + agreement);

                // 若agreement不等于0，则使用agreement值替换fundsType作为fundsType
                if (agreement != 0) {
                    fundsType = agreement;
                }

                /*2.5.2、循环该币种的热冲，热提及钱包Map 增加空指针业务处理 modify by Ethan 2019.7.16*/
                if (warmRechargeMap == null) {
                    nullStringBuffer.append("【新对账WalletTransWork-币种" + fundsTypeName + "热充warmRechargeMap为空】");
                    break;
                }
                if (warmWithdrawMap == null) {
                    nullStringBuffer.append("【新对账WalletTransWork-币种" + fundsTypeName + "热提warmWithdrawMap为空】");
                    break;
                }
                if (coldValueMap == null) {
                    nullStringBuffer.append("【新对账WalletTransWork-币种" + fundsTypeName + "冷钱包coldValueMap为空】");
                    break;
                }
                if (browserBalanceMap == null) {
                    nullStringBuffer.append("【新对账WalletTransWork-币种" + fundsTypeName + "爬虫余额browserBalanceMap为空】");
                    break;
                }
                if (warmRechargeFinaccwalletbillMap == null) {
                    nullStringBuffer.append("【新对账WalletTransWork-币种" + fundsTypeName + "热充对账warmRechargeFinaccwalletbillMap为空】");
                    break;
                }
                if (warmWithdrawFinaccwalletbillMap == null) {
                    nullStringBuffer.append("【新对账WalletTransWork-币种" + fundsTypeName + "热提对账warmWithdrawFinaccwalletbillMap为空】");
                    break;
                }

                /*2.5.3、获取币种热充、热提、和冷钱包余额*/
                BigDecimal warmRecharge = warmRechargeMap.get(fundsType);
                BigDecimal warmWithdraw = warmWithdrawMap.get(fundsType);
                BigDecimal coldValue = coldValueMap.get(fundsType);

                BigDecimal warmRechargeFinaccwalletbill = warmRechargeFinaccwalletbillMap.get(fundsType);
                BigDecimal warmWithdrawFinaccwalletbill = warmWithdrawFinaccwalletbillMap.get(fundsType);
                BigDecimal browserBalance = browserBalanceMap.get(fundsType);

                String apiReq = apiReqFlag.get(fundsType);

                log.info("【新对账WalletTransWork】校验币种：" + fundsTypeName + ", fundsType = " + fundsType);
                log.info(" rechargeVS = " + warmRecharge.subtract(warmRechargeFinaccwalletbill).abs());
                log.info(" withdrawVS = " + warmWithdraw.subtract(warmWithdrawFinaccwalletbill).abs());
                log.info(" coldVS = " + coldValue.subtract(browserBalance == null ? BigDecimal.ZERO : browserBalance).abs());

                /*2.5.4、配置冷钱包地址的币种进行冷钱包对账*/
                if (browserBalanceMap.containsKey(fundsType) && "has".equals(apiReq)) {

                    /*币种 eth - 8位小数点之后的误差不算误差*/
                    if ("eth".equals(fundsTypeName)) {
                        if ((coldValue.compareTo(browserBalance) == 0)
                                || (coldValue.subtract(browserBalance).abs().compareTo(new BigDecimal("0.0000001")) <= 0)) {
                            msgSuccessCold += fundsTypeName + ",";
                            log.info("【新对账WalletTransWork】当前币种资金对账正常" + fundsTypeName);
                        } else {
                            msgErrorCold += "【币种" + fundsTypeName;
                            if (browserBalanceMap.size() > 0) {
                                if (coldValue.compareTo(browserBalance) != 0) {
                                    if (coldValue.subtract(browserBalance).abs().compareTo(new BigDecimal("0.00000001")) >= 0) {
                                        msgErrorCold += ",冷钱包计算余额：" + coldValue + " ,冷钱包区块查询余额:" + browserBalance;
                                    }
                                }
                                //调用备用接口
                                msgErrorFlagCold = true;
                            }
                            msgErrorCold += "】";
                        }
                        /*币种 etc - 8位小数点之后的误差不算误差*/
                    } else if ("etc".equals(fundsTypeName)) {
                        if ((coldValue.compareTo(browserBalance) == 0)
                                || (coldValue.subtract(browserBalance).abs().compareTo(new BigDecimal("0.00000001")) <= 0)) {
                            msgSuccessCold += fundsTypeName + ",";
                            log.info("【新对账WalletTransWork】当前币种资金对账正常" + fundsTypeName);
                        } else {
                            msgErrorCold += "【币种" + fundsTypeName;
                            if (browserBalanceMap.size() > 0) {
                                if (coldValue.compareTo(browserBalance) != 0) {
                                    if (coldValue.subtract(browserBalance).abs().compareTo(new BigDecimal("0.00000001")) >= 0) {
                                        msgErrorCold += ",冷钱包计算余额：" + coldValue + " ,冷钱包区块查询余额:" + browserBalance;
                                    }
                                }
                                //调用备用接口
                                msgErrorFlagCold = true;
                            }
                            msgErrorCold += "】";
                        }
                    } else {
                        if ((coldValue.compareTo(browserBalance) == 0)
                                || (coldValue.subtract(browserBalance).abs().compareTo(new BigDecimal("0.00000001")) <= 0)) {
                            msgSuccessCold += fundsTypeName + ",";
                            log.info("【新对账WalletTransWork】当前币种资金对账正常" + fundsTypeName);
                        } else {
                            msgErrorCold += "【币种" + fundsTypeName;
                            if (browserBalanceMap.size() > 0) {
                                if (coldValue.compareTo(browserBalance) != 0) {
                                    if (coldValue.subtract(browserBalance).abs().compareTo(new BigDecimal("0.00000001")) >= 0) {
                                        msgErrorCold += ",冷钱包计算余额：" + coldValue + " ,冷钱包区块查询余额:" + browserBalance;
                                    }
                                }
                                //调用备用接口
                                msgErrorFlagCold = true;
                            }
                            msgErrorCold += "】";
                        }
                    }
                } else {
                    msgNo += fundsTypeName + ",";
                }

                /*2.5.5、热充热提比对*/
                if (!"btg".equals(fundsTypeName)) {

                    /*新增判断，8位小数点之后的误差不算误差*/
                    if ((warmRecharge.compareTo(warmRechargeFinaccwalletbill) == 0
                            && warmWithdraw.compareTo(warmWithdrawFinaccwalletbill) == 0)
                            || (warmRecharge.subtract(warmRechargeFinaccwalletbill).abs().compareTo(new BigDecimal("0.00000001")) <= 0
                            && warmWithdraw.subtract(warmWithdrawFinaccwalletbill).abs().compareTo(new BigDecimal("0.00000001")) <= 0)) {
                        msgSuccessHot += fundsTypeName + ",";
                    } else {
                        msgErrorFlagHot = true;
                        msgErrorHot += "【币种" + fundsTypeName;
                        if (warmRecharge.compareTo(warmRechargeFinaccwalletbill) != 0) {

                            /*如果是ETH，6位小数点之后的误差不算误差，其他币种8位小数点之后的误差不算误差*/
                            if (!"eth".equals(fundsTypeName)) {
                                if (warmRecharge.subtract(warmRechargeFinaccwalletbill).abs().compareTo(new BigDecimal("0.00000001")) >= 0) {
                                    msgErrorHot += ",热冲计算余额：" + warmRecharge + " ,热充同步余额:" + warmRechargeFinaccwalletbill;
                                }
                            } else {
                                if (warmRecharge.subtract(warmRechargeFinaccwalletbill).abs().compareTo(new BigDecimal("0.000001")) >= 0) {
                                    msgErrorHot += ",热冲计算余额：" + warmRecharge + " ,热充同步余额:" + warmRechargeFinaccwalletbill;
                                }
                            }
                        }
                        if (warmWithdraw.compareTo(warmWithdrawFinaccwalletbill) != 0) {

                            /*如果是ETH，6位小数点之后的误差不算误差，其他币种8位小数点之后的误差不算误差*/
                            if (!"eth".equals(fundsTypeName)) {
                                if (warmWithdraw.subtract(warmWithdrawFinaccwalletbill).abs().compareTo(new BigDecimal("0.00000001")) >= 0) {
                                    msgErrorHot += ",热提计算余额：" + warmWithdraw + " ,热提同步余额:" + warmWithdrawFinaccwalletbill;
                                }
                            } else {
                                if (warmWithdraw.subtract(warmWithdrawFinaccwalletbill).abs().compareTo(new BigDecimal("0.000001")) >= 0) {
                                    msgErrorHot += ",热提计算余额：" + warmWithdraw + " ,热提同步余额:" + warmWithdrawFinaccwalletbill;
                                }
                            }
                        }
                    }
                    msgErrorHot += "】";

                }
            }

            log.info("【新对账WalletTransWork】各币种钱包资金自检结束.");

            /*2.6、预警msgError信息到钉钉*/

            /*2.6.1、在9-19点的0-5分，打印钱包自检成功的币种或未配置冷钱包的币种信息*/
            Date curDate = new Date();
            int curHours = curDate.getHours();
            int curMinutes = curDate.getMinutes();
            if (curMinutes >= 0 && curMinutes < 5 && curHours >= 9 && curHours <= 19) {
                log.info(msgSuccessCold);
                log.info(msgSuccessHot);
                log.info(msgNo);
            }

            /*2.6.2、如果有自检异常的币种，则报警到钉钉*/
            if (msgErrorFlagCold) {
                log.info(msgErrorCold);
            }
            if (msgErrorFlagHot) {
                log.info(msgErrorHot);
            }

            /*2.6.3、如果查询支付中心或运营后台或爬虫爬取到的MAP为空，也需要报警到钉钉*/
            if (nullStringBuffer.length() > 0) {
                log.info(msgNull + nullStringBuffer.toString());
            }

            /*2.7、记录钱包资金自检结束时间*/
            log.info("【新对账WalletTransWork】各币种钱包资金自检结束!!!【核算耗时：" + (System.currentTimeMillis() - startTime) + "】");

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    public void common(WalletSelfCheck walletSelfCheck, int fundsType, String
            msgError, Map.Entry<String, CoinProps> entryCoinProps) {
        Map<Integer, BigDecimal> coldValueMap = walletSelfCheck.getColdValueMap();
        Map<Integer, BigDecimal> browserBalanceMap = walletSelfCheck.getBrowserBalanceMap();
        Map<Integer, String> apiReqFlag = walletSelfCheck.getApiReqFlag();
        BigDecimal coldValue = coldValueMap.get(fundsType);
        BigDecimal browserBalance = browserBalanceMap.get(fundsType);
        String apiReq = apiReqFlag.get(fundsType);
        log.info("校验币种：" + entryCoinProps.getKey() + ", fundsType = " + fundsType);
        log.info(" = " + coldValue.subtract(browserBalance).abs());
        msgError += "【币种" + entryCoinProps.getKey();
        if (browserBalanceMap.containsKey(fundsType) && "has".equals(apiReq)) { //配置冷钱包地址的币种进行全部对账
            if (browserBalanceMap.size() > 0) {
                if (coldValue.compareTo(browserBalance) != 0) {
                    if (coldValue.subtract(browserBalance).abs().compareTo(new BigDecimal("0.00000001")) >= 0) {
                        msgError += ",冷钱包计算余额：" + coldValue + " ,冷钱包区块查询余额:" + browserBalance;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        WalletTransWork walletTransWork = new WalletTransWork("WalletTransWork", "冷钱包余额查询");
        walletTransWork.run();
    }


}

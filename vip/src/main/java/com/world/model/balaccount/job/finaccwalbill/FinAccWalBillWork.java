package com.world.model.balaccount.job.finaccwalbill;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.world.model.balaccount.dao.FinAccWalDailyAccDao;
import com.world.model.balaccount.entity.WalletSelfCheck;
import org.apache.log4j.Logger;
import com.world.data.database.DatabasesUtil;
import com.world.model.balaccount.job.finaccwalbill.thread.FinAccWalBillThread;
import com.world.model.dao.task.Worker;
import com.world.model.entity.coin.CoinProps;
import com.world.util.date.TimeUtil;

/**
 * <p>标题: 钱包流水同步查询</p>
 * <p>描述: 钱包流水同步查询从支付中心同步数据</p>
 * <p>版权: Copyright (c) 2017</p>
 *
 * @author flym
 */
public class FinAccWalBillWork extends Worker {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /*上次更新时间默认为当天,即明天凌晨执行数据同步; 如果要改为前天:TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(1));*/
    private Timestamp lastUpdateTime = TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1));

    //	/*sql语句*/
//	private String sql = "";
    /*记录日志*/
    private String logContent = "";

    private static Logger log = Logger.getLogger(FinAccWalBillWork.class);

    public FinAccWalBillWork(String name, String des) {
        super(name, des);
    }

    FinAccWalDailyAccDao finAccWalDailyAccDao = new FinAccWalDailyAccDao();

    @Override
    public void run() {
        try {
            /*时间控制*/
            Timestamp tsTodayTime = TimeUtil.getTodayFirst();
            Date nowDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            /*现在时间获取*/
            String strNowTime = sdf.format(nowDate);
            log.info("FinAccDetaislWork...strNowTime = " + strNowTime + ", tsTodayTime = " + tsTodayTime + ", lastUpdateTime = " + lastUpdateTime);
            /*00点10分之后执行 strNowTime.indexOf("00:1") >= 0 && */
//			if(tsTodayTime.compareTo(lastUpdateTime) > 0) {
            /*记录日志*/
//			SysLogDao sysLogDao = new SysLogDao();
//			String logId = "adminMonitor-FinAccDownloadWork-2-admin-" + System.currentTimeMillis();
            logContent = "开始进行钱包流水同步查询";
//            sysLogDao.addSysLog("admin", logId, logId, "adminMonitor", "FinAccDownloadWork", 2, logContent , "admin", "admin", "");
            log.info("logContent");
			/*记录核算开始时间*/
            long startTime = System.currentTimeMillis();
			/*创建一个可重用固定线程数的线程池*/
            ExecutorService finAccWalBillWorkPool = Executors.newFixedThreadPool(1);
			/*循环币种进行充值流水抽取，即从支付中心同步充值流水*/
            Map<String, CoinProps> mapCoinProps = DatabasesUtil.getCoinPropMaps();
            Iterator<Entry<String, CoinProps>> iteCoinProps = mapCoinProps.entrySet().iterator();
			/*资金类型2btc,3...*/
            int fundType = 0;
            String fundTypeName = "";
            while (iteCoinProps.hasNext()) {
                Entry<String, CoinProps> entryCoinProps = iteCoinProps.next();
                CoinProps tmpCoinProps = entryCoinProps.getValue();
                fundType = tmpCoinProps.getFundsType();
                fundTypeName = entryCoinProps.getKey();
                log.info("fundTypeName = " + fundTypeName + ", fundType = " + fundType);
                if (fundType > 0) {
					/*创建线程,按每个币种进行处理查询*/
                    FinAccWalBillThread finAccWalBillThread = new FinAccWalBillThread(fundType, fundTypeName);
//						finAccDetailsThread.run();
                    finAccWalBillWorkPool.execute(finAccWalBillThread);
                } else {
                    log.info("没有此币种,无法同步!...");
                }
            }
			
			/*按过去执行已提交任务的顺序发起一个有序的关闭，但是不接受新任务。如果已经关闭，则调用没有其他作用。*/
            finAccWalBillWorkPool.shutdown();

            while (true) {
				/*如果关闭后所有任务都已完成，则返回 true。注意，除非首先调用 shutdown 或 shutdownNow，否则 isTerminated 永不为 true。*/
                if (finAccWalBillWorkPool.isTerminated()) {
                    break;
                } else {
                    log.info("钱包流水同步查询对账进行中...");
                    Thread.sleep(10 * 1000L);
                }
            }

            long endTime = System.currentTimeMillis();
            logContent = "钱包流水同步查询结束!!!【核算耗时：" + (endTime - startTime) + "】";
            log.info(logContent);
            
            /*开始钱包资金自检*/
            log.info("开始钱包资金自检...");
//            Date curDate = new Date();
//            int curHours = curDate.getHours();
//            int curMinutes = curDate.getMinutes();
//            if (curMinutes >= 0 && curMinutes < 5 && curHours>= 9 && curHours <= 19) {
            int fundsType = 0;
            WalletSelfCheck walletSelfCheck = finAccWalDailyAccDao.getWalletSelf();
            Map<Integer, BigDecimal> warmRechargeMap = walletSelfCheck.getWarmRechargeMap();
            Map<Integer, BigDecimal> warmRechargeFinaccwalletbillMap = walletSelfCheck.getWarmRechargeFinaccwalletbillMap();
            Map<Integer, BigDecimal> warmWithdrawMap = walletSelfCheck.getWarmWithdrawMap();
            Map<Integer, BigDecimal> warmWithdrawFinaccwalletbillMap = walletSelfCheck.getWarmWithdrawFinaccwalletbillMap();
            Map<Integer, BigDecimal> coldValueMap = walletSelfCheck.getColdValueMap();
            Map<Integer, BigDecimal> browserBalanceMap = walletSelfCheck.getBrowserBalanceMap();
            Map<Integer, String> apiReqFlag = walletSelfCheck.getApiReqFlag();
            //获取配置文件中的币种信息
            Iterator<Entry<String, CoinProps>> iteCoinPropss = mapCoinProps.entrySet().iterator();
            String msgSuccess = "QBTX【所有钱包自检正常的币种有";
            String msgError = "QBZJ【自检异常的币种有】";
            String msgNo = "QBTX【暂未配置币种冷钱包余额查询地址,无法检查冷钱包余额的币种有";
            boolean msgErrorFlag = false;
            while (iteCoinPropss.hasNext()) {
                Entry<String, CoinProps> entryCoinProps = iteCoinPropss.next();
                CoinProps tmpCoinProps = entryCoinProps.getValue();
                fundsType = tmpCoinProps.getFundsType();
                /*循环该币种的热冲，热提及钱包金额*/
                BigDecimal warmRecharge = warmRechargeMap.get(fundsType);
                BigDecimal warmRechargeFinaccwalletbill = warmRechargeFinaccwalletbillMap.get(fundsType);
                BigDecimal warmWithdraw = warmWithdrawMap.get(fundsType);
                BigDecimal warmWithdrawFinaccwalletbill = warmWithdrawFinaccwalletbillMap.get(fundsType);
                BigDecimal coldValue = coldValueMap.get(fundsType);
                BigDecimal browserBalance = browserBalanceMap.get(fundsType);
                String apiReq = apiReqFlag.get(fundsType);
                log.info("校验币种：" + entryCoinProps.getKey() + ", fundsType = " + fundsType);
                log.info(" = " + warmRecharge.subtract(warmRechargeFinaccwalletbill).abs());
                log.info(" = " + warmWithdraw.subtract(warmWithdrawFinaccwalletbill).abs());
                log.info(" = " + coldValue.subtract(browserBalance).abs());
                    /*新增判断，8位小数点之后的误差不算误差*/
                if (browserBalanceMap.containsKey(fundsType) && "has".equals(apiReq)) { //配置冷钱包地址的币种进行全部对账
                    if ((warmRecharge.compareTo(warmRechargeFinaccwalletbill) == 0 && warmWithdraw.compareTo(warmWithdrawFinaccwalletbill) == 0 && coldValue.compareTo(browserBalance) == 0) || (warmRecharge.subtract(warmRechargeFinaccwalletbill).abs().compareTo(new BigDecimal("0.00000001")) <= 0 && warmWithdraw.subtract(warmWithdrawFinaccwalletbill).abs().compareTo(new BigDecimal("0.00000001")) <= 0 && coldValue.subtract(browserBalance).abs().compareTo(new BigDecimal("0.00000001")) <= 0)) {
                        msgSuccess += "," + entryCoinProps.getKey();
                        log.info("当前币种资金对账正常" + entryCoinProps.getKey());
                    } else {
                        if(!"eth".equals(entryCoinProps.getKey())){
                            msgError += "【币种" + entryCoinProps.getKey();
                            if (warmRecharge.compareTo(warmRechargeFinaccwalletbill) != 0) {
                                msgError += ",热冲计算余额：" + warmRecharge + " ,热充同步余额:" + warmRechargeFinaccwalletbill;
                            }
                        }
                        if (warmWithdraw.compareTo(warmWithdrawFinaccwalletbill) != 0) {
                            msgError += ",热提计算余额：" + warmWithdraw + " ,热提同步余额:" + warmWithdrawFinaccwalletbill;
                        }
                        if (browserBalanceMap.size() > 0) {
                            if (coldValue.compareTo(browserBalance) != 0) {
                                msgError += ",冷钱包计算余额：" + coldValue + " ,冷钱包区块查询余额:" + browserBalance;
                            }
                        }
                        // log.info("币种" + entryCoinProps.getKey() + "资金有误 " + msgError);
                        msgError += "】";
                        msgErrorFlag = true;
                    }
                } else { //未配置冷钱包地址的币种至进行热充和热提的对账
                        msgNo += "," + entryCoinProps.getKey();
//                    	amount1.subtract(balance1).abs().compareTo(new BigDecimal("0.00000001")) > 0
                    	/*新增判断，8位小数点之后的误差不算误差*/
                    if ((warmRecharge.compareTo(warmRechargeFinaccwalletbill) == 0 && warmWithdraw.compareTo(warmWithdrawFinaccwalletbill) == 0) || (warmRecharge.subtract(warmRechargeFinaccwalletbill).abs().compareTo(new BigDecimal("0.00000001")) <= 0 && warmWithdraw.subtract(warmWithdrawFinaccwalletbill).abs().compareTo(new BigDecimal("0.00000001")) <= 0)) {
                        msgSuccess += "," + entryCoinProps.getKey();
                 //       log.info("当前币种资金对账正常，不校验冷钱包：" + entryCoinProps.getKey());
                    } else {
                        if(!"eth".equals(entryCoinProps.getKey())){
                            msgError += "【币种" + entryCoinProps.getKey();    
                            if (warmRecharge.compareTo(warmRechargeFinaccwalletbill) != 0) {
                                msgError += ",热冲计算余额：" + warmRecharge + " ,热充同步余额:" + warmRechargeFinaccwalletbill;
                            }
                        }
                        if (warmWithdraw.compareTo(warmWithdrawFinaccwalletbill) != 0) {
                            msgError += ",热提计算余额：" + warmWithdraw + " ,热提同步余额:" + warmWithdrawFinaccwalletbill;
                        }

                        // log.info("币种" + entryCoinProps.getKey() + "资金有误 " + msgError);
                        msgError += "】";
                        msgErrorFlag = true;
                    }
                }
            }
            Date curDate = new Date();
            int curHours = curDate.getHours();
            int curMinutes = curDate.getMinutes();
            if (curMinutes >= 0 && curMinutes < 5 && curHours>= 9 && curHours <= 19) {
                log.info(msgSuccess + "】");
                log.info(msgNo + "】");
            }
                /*如果有自检异常的币种，则报警到钉钉*/
            if (msgErrorFlag) {
                log.info(msgError + "】");
            }
//            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    public static void main(String[] args) {
        FinAccWalBillWork finAccWalBillWork = new FinAccWalBillWork("FinAccWalBillWork", "钱包流水同步查询");
        finAccWalBillWork.run();
    }

}

package com.world.model.balaccount.job.finaccwalbill.thread;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.world.data.database.DatabasesUtil;
import com.world.model.balaccount.dao.FinAccWalDailyAccDao;
import com.world.model.balaccount.entity.BalanceResp;
import com.world.model.dao.billreconciliation.BillReconciliationDao;
import com.world.model.dao.reconciliation.ReconciliationDao;
import com.world.model.dao.task.Worker;
import com.world.model.entity.billreconciliation.Billreconciliation;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.reconciliation.Generalledger;
import com.world.util.date.TimeUtil;
import com.world.util.request.HttpUtil;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class WalletCheckThread extends Worker {
    private static Logger log = Logger.getLogger(WalletCheckThread.class);
    FinAccWalDailyAccDao finAccWalDailyAccDao = new FinAccWalDailyAccDao();
    ReconciliationDao reconciliationDao = new ReconciliationDao();
    BillReconciliationDao billReconciliationDao = new BillReconciliationDao();

    public WalletCheckThread(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {

        log.info("WalletCheckThread ... run");

        //区块VS钱包差额【区块VS钱包差额-报警】
        String msgSuccess = "CWTX【财务核对:交易平台VS钱包正常】";
        String msgError = "CWHD【财务核对:交易平台VS钱包异常】";

        //钱包资金总账差额【交易平台内部差额-报警】
        String qbzjceSuccess = "CWTX【交易平台内部差额正常】";
        String qbzjceError = "CWHD【交易平台内部差额异常】";

        try {
            Date now = new Date();
            int hours = now.getHours();
            int minutes = now.getMinutes();
            if (hours == 8 && minutes >= 30 && hours <= 9) {

                log.info("按计划在8点30到9点之间执行:" + now);

                // 获取币种配置信息
                Map<String, CoinProps> coinPropsMap = DatabasesUtil.getCoinPropMaps();

                // lastDay 昨日此时
                // begin 昨日0点
                // end 昨日23：59：59
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date lastDay = TimeUtil.getSpecifiedDayBefore(new Date());
                Date begin = TimeUtil.dayBegin(lastDay);
                Date end = TimeUtil.dayEnd(lastDay);
                String strEndTime = sdf.format(end);

                /*调用接口的方法*/
                /*获取钱包每日对账，对账日期范围为小于strEndTime，运营后台：数据中心 > 支付中心对账 > 钱包每日对账*/
                String url = ApiConfig.getValue("snowmanboss.url");
                url += "/walletBalance/queryList";
                Map<String, String> params = new HashMap<String, String>();
                params.put("checkTimeStart", "2018-01-01 00:00:00");
                params.put("checkTimeEnd", strEndTime);
                log.info("====== checkTimeEnd:" + strEndTime);
                log.info("====== url = " + url);
                String strResult = HttpUtil.doPost(url, params, 10000, 10000, false);
                JSONObject json = JSONObject.parseObject(strResult);
                JSONObject jsonObject = json.getJSONObject("data");
                //钱包每日对账List
                List<BalanceResp> balanceRespList = (List<BalanceResp>) JSONArray.parseArray(jsonObject.getString("list"), BalanceResp.class);
                if (balanceRespList == null || balanceRespList.size() <= 0) {
                    msgError += "【获取钱包每日对账数据 balanceRespList 为空】";
                }

                if (balanceRespList != null && balanceRespList.size() > 0) {

                    //交易平台钱包对账 List
                    log.info("====== begin:" + sdf.format(begin));
                    log.info("====== end:" + sdf.format(end));

                    List<Generalledger> generalledgerList = reconciliationDao.generalledgerList(sdf.format(begin), sdf.format(end));
                    if (generalledgerList == null || generalledgerList.size() <= 0) {
                        msgError += "【 未查询到 messi_ods 库 generalledger 表昨日统计数据 】";
                    }

                    if (generalledgerList != null || generalledgerList.size() > 0) {
                        for (Generalledger generalledger : generalledgerList) {
                            for (BalanceResp finAccWalDailyAccBeanVo : balanceRespList) {
                                if (String.valueOf(generalledger.getFundstype()).equals(finAccWalDailyAccBeanVo.getFundsType())) {

                                    CoinProps coinProps = DatabasesUtil.coinProps(generalledger.getFundstype());
                                    String fundsTypeName = coinProps.getPropTag();

                                    String useRecharge = finAccWalDailyAccBeanVo.getUserRecharge() == null ? "0" : finAccWalDailyAccBeanVo.getUserRecharge();
                                    String useWithdraw = finAccWalDailyAccBeanVo.getUserWithdraw() == null ? "0" : finAccWalDailyAccBeanVo.getUserWithdraw();

                                    //钱包用户充值-钱包用户提现
                                    BigDecimal wallet = new BigDecimal(useRecharge).subtract(new BigDecimal(useWithdraw));

                                    //充值 - 提现 + 提现手续费 + 内正 - 内负 + 外正 - 外负
                                    BigDecimal recharge = generalledger.getRecharge() == null ? BigDecimal.ZERO : generalledger.getRecharge();
                                    BigDecimal withdraw = generalledger.getWithdraw() == null ? BigDecimal.ZERO : generalledger.getWithdraw();
                                    BigDecimal withdrawFee = generalledger.getWithdrawfee() == null ? BigDecimal.ZERO : generalledger.getWithdrawfee();
                                    BigDecimal interPositive = generalledger.getInternaladjustmentpositive() == null ? BigDecimal.ZERO : generalledger.getInternaladjustmentpositive();
                                    BigDecimal interNegtive = generalledger.getInternaladjustmentnegative() == null ? BigDecimal.ZERO : generalledger.getInternaladjustmentnegative();
                                    BigDecimal exterPositive = generalledger.getExternaladjustmentpositive() == null ? BigDecimal.ZERO : generalledger.getExternaladjustmentpositive();
                                    BigDecimal exterNegtive = generalledger.getExternaladjustmentnegative() == null ? BigDecimal.ZERO : generalledger.getExternaladjustmentnegative();

                                    BigDecimal balance = recharge.subtract(withdraw).add(withdrawFee)
                                            .add(interPositive).subtract(interNegtive)
                                            .add(exterPositive).subtract(exterNegtive);

                                    // ETH 精确到小数点6位，其他币种精确到小数点8位
                                    BigDecimal compareNum = generalledger.getFundstype() == 6 ?
                                            new BigDecimal("0.000001") : new BigDecimal("0.00000001");

                                    if (wallet.subtract(balance).abs().compareTo(compareNum) <= 0) {
                                        msgSuccess += "【币种名称:" + fundsTypeName + "," + "交易平台金额:" + balance + "钱包金额:" + wallet + ",账务一致】";
                                    } else {
                                        msgError += "【币种名称:" + fundsTypeName + "," + "交易平台金额:" + balance + "钱包金额:" + wallet + ",账务不一致】";
                                    }
                                }
                            }
                        }
                    }
                    log.info(msgSuccess);
                    if (!msgError.equals("CWHD【财务核对:交易平台VS钱包异常】")) {
                        log.info(msgError);
                    }
                }


                //1、查询billreconciliation列表，得到统计数据
                List<Billreconciliation> billReconciliationList = billReconciliationDao.getList(lastDay, lastDay, 0);
                if (billReconciliationList == null || billReconciliationList.size() <= 0) {
                    qbzjceError += "【 未查询到 messi_ods 库 billreconciliation 表昨日统计数据】";
                }

                //2、添加币种名称
                for (Billreconciliation billreconciliation : billReconciliationList) {
                    for (Map.Entry<String, CoinProps> entry : coinPropsMap.entrySet()) {
                        CoinProps coint = entry.getValue();
                        if (billreconciliation.getFundstype() == coint.getFundsType()) {
                            billreconciliation.setFundstypeName(coint.getPropTag());
                        }
                    }
                }

                // 3、计算交易平台内部差值
                for (Billreconciliation billreconciliation : billReconciliationList) {
                    //用户转入-用户转出 =币币账户余额 +交易手续费
                    BigDecimal charge = billreconciliation.getShiftto()
                            .subtract(billreconciliation.getRollout())
                            .subtract(billreconciliation.getBalance())
                            .subtract(billreconciliation.getTransactionfee());

                    if (charge.compareTo(BigDecimal.ZERO) == 0) {
                        qbzjceSuccess += "【币种名称:" + billreconciliation.getFundstypeName() + "," + "交易平台内部差额账务正常】";
                    } else {
                        qbzjceError += "【币种名称:" + billreconciliation.getFundstypeName() + "," + "交易平台内部差额账务异常】【异常差额为:" + charge + "】";
                    }
                }

                log.info(qbzjceSuccess);

                //如果没有异常，则不打印
                if (!qbzjceError.equals("CWHD【交易平台内部差额异常】")) {
                    log.info(qbzjceError);
                }

            } else {
                log.info("未在计划区间内:" + now);
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    public static void main(String[] args) {
        WalletCheckThread walletCheckThread = new WalletCheckThread("", "");
        walletCheckThread.run();
    }

}

package com.world.model.balaccount.job.wallettrans.thread;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.balaccount.dao.WalletTransBillDao;
import com.world.model.balaccount.entity.DealType;
import com.world.model.balaccount.entity.WalletTransBill;
import com.world.model.balaccount.job.wallettrans.DealTypeForbbEnum;
import com.world.model.entity.coin.CoinProps;
import com.world.util.date.TimeUtil;
import com.world.util.request.HttpUtil;
import com.world.util.string.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WalletTransThread extends Thread {

    /*币种编号2=btc*/
    private int fundType;
    /*币种名称btc*/
    private String fundTypeName;

    private CoinProps coinProps;
    /*sql语句*/
    private String sql = "";
    /*拼接后保存到数据库的SQL*/
    /*finPayCenterWalletBill*/
    private String batchBillTxSql = "";
    private String batchBillTxNSql = "";
    private String batchBillTxSql1 = "";
    private String batchBillTxSqlTemplate = "";
    private String batchBillTxSql1Template = "";
    private ConcurrentHashMap<Integer, Map<String, String>> concurrentMergeMap = null;
    private static Logger log = Logger.getLogger(WalletTransThread.class);

    public WalletTransThread(int fundType, String fundTypeName, CoinProps coint) {
        this.fundType = fundType;
        this.fundTypeName = fundTypeName;
        this.coinProps = coint;
    }

    @Override
    public void run() {


        /** 1.1 记录本次线程处理的币种关键信息，例如：
         * fundType = 2, fundTypeName = btc
         * fundType = 10, fundTypeName = usdt
         * fundType = 10, fundTypeName = usdte
         */
        log.info("【新对账WalletTransWork】WalletTransThread-钱包流水同步币种名称fundTypeName = " + fundTypeName + ", 币种编号fundType = " + fundType);

        try {

            /** 1.2 变量定义*/
            /*记录核算开始时间*/
            long startTime = System.currentTimeMillis();
            /*报表使用的dealType*/
            int dealTypeForbb = 0;
            /*钱包流水类*/
            WalletTransBillDao walletTransBillDao = new WalletTransBillDao();
            /*币种类型代码*/
            int newFundsType = fundType;


            /*1.3 查询该币种已同步到钱包流水的时间
             * 注意：fundsType=10的币种USDT有两个币种，基于OMNI和基于ERC20协议的USDT，通过agreement字段进行区分
             * */
            sql = "select max(addTime) addTime from wallettransbill where fundsType = " + newFundsType + " ";
            if (fundTypeName.toLowerCase().equals("usdte")) {
                sql = sql + " and agreement = " + coinProps.getAgreement();
            }
            log.info("【新对账WalletTransWork】查询wallettransbill最大钱包流水同步时间sql = " + sql);

            /*1.4 判断获取的时间addTime如果不为空，则设置查询时间createTime为addTime，否则设置为当前小时0分0s*/
            WalletTransBill walletTransBill = null;
            walletTransBill = walletTransBillDao.getT(sql, null, WalletTransBill.class);
            Timestamp createTime = null;
            if (walletTransBill != null && walletTransBill.getAddTime() != null) {
                createTime = walletTransBill.getAddTime();
            } else {
                createTime = TimeUtil.getHourFirst();
            }
            log.info("【新对账WalletTransWork】查询到createTime = " + createTime);

            /**
             * 1.5 交易平台查询支付中心接口，如果createTime为空或者0，返回所有记录。
             * 发送createTime>0，返回从createTime之后的数据
             */
            String url = ApiConfig.getValue("tradingcenter.url");
            url += "/openapi/tradingcenter/finance/walletTrans/" + fundTypeName.toUpperCase() + "?createTime=" + createTime.getTime();
            log.info("【新对账WalletTransWork】支付中心钱包流水查询url = " + url);
            String strResult = HttpUtil.doGet(url, null, 10000, 10000);
            JSONObject jsonResult = com.alibaba.fastjson.JSONObject.parseObject(strResult);

            /*1.6 解析返回的币种钱包流水信息*/
            if (null != jsonResult) {

                /*添加钉钉预警*/
                List<String> alarmList = new ArrayList<>();

                /*获取返回的所有数据数组，转换成对象集合*/
                JSONArray datasArray = (JSONArray) jsonResult.get("data");
                if (null != datasArray && datasArray.size() > 0) {

                    /*代表txId,uuid:唯一标识接口对接使用.walId:钱包ID,walName:钱包名称,txIdN */
                    String txId = "", uuid = "", sendWalletName = "", receiveWalletName = "", vn = "", toAddress = "", txIdN = "";
                    /*确认时间*/
                    Timestamp configTime = null, addTime = null;
                    int blockHeight = 0, confirmTimes = 0, dealType = 0, walType = 0;
                    /*交易金额,手续费,钱包余额,倍数*/
                    BigDecimal txAmount = null, txNAmount = null, fee = null, walletBalance = null;

                    for (int i = 0; i < datasArray.size(); i++) {

                        log.info("【新对账WalletTransWork】第" + (i + 1) + "条钱包流水信息 = " + datasArray.get(i));

                        /*获取单条对象记录集合*/
                        JSONObject detailInfo = (JSONObject) datasArray.get(i);
//						fundsType = detailInfo.getString("fundType");
                        /*获取各字段信息*/
                        dealType = detailInfo.getIntValue("dealType");
                        dealTypeForbb = dealType;//报表使用
                        txId = detailInfo.getString("txId");
                        sql = "select * from wallettransbill where fundsType = " + fundType + " and txId = '" + txId + "' and dealType = " + dealType;
                        /*if(fundTypeName.toLowerCase().equals("usdte")){
                            sql =  sql + " and agreement = " + coinProps.getAgreement();
                        }*/
                        WalletTransBill walletTransBillOld = walletTransBillDao.getT(sql, null, WalletTransBill.class);
                        if (walletTransBillOld != null) {
                            log.info("【新对账WalletTransWork】txId=" + txId + ",dealType=" + dealType + ":该流水信息已存在，忽略处理。");
                            continue;
                        }

                        uuid = detailInfo.getString("uuid");
                        fee = detailInfo.getBigDecimal("fee");
                        addTime = detailInfo.getTimestamp("createTime");
                        txAmount = detailInfo.getBigDecimal("amount").setScale(9, BigDecimal.ROUND_DOWN);
                        configTime = new java.sql.Timestamp(detailInfo.getTimestamp("dealTime").getTime() * 1000);
                        blockHeight = detailInfo.getIntValue("blockHeight");
                        confirmTimes = detailInfo.getIntValue("confirmations");
                        sendWalletName = detailInfo.getString("sendWalletName");
                        receiveWalletName = detailInfo.getString("receiveWalletName");
                        walletBalance = detailInfo.getBigDecimal("walletBalance");
                        int dealFeeType = 0;


                        //modify by kinghao 20190121  预防时间戳非毫秒级
                        long time = 9999999999L;
                        if (detailInfo.getTimestamp("dealTime").getTime() > time) {
                            configTime = new java.sql.Timestamp(detailInfo.getTimestamp("dealTime").getTime());
                        }
                        //end

                        if (sendWalletName == null) {
                            sendWalletName = "";
                        }
                        if (receiveWalletName == null) {
                            sendWalletName = "";
                        }
                        JSONArray addrsArray = (JSONArray) detailInfo.get("toAddressList");
                        if (dealType == 11) {
                            batchBillTxSqlTemplate = "('" + uuid + "', '" + txId + "', " + fundType + ", " + (coinProps.getAgreement() == 0 ? fundType : coinProps.getAgreement()) + ", {txAmount}, {fee}, "
                                    + "{dealType}," + blockHeight + ",'" + addTime + "','" + configTime + "', "
                                    + confirmTimes + ", '{sendWalletName}', '{receiveWalletName}'," + walletBalance + ","
                                    + dealFeeType + ","
                                    + "'" + TimeUtil.getNow() + "'),";

                            batchBillTxSql1Template = "('" + uuid + "', '" + txId + "', " + fundType + ", " + (coinProps.getAgreement() == 0 ? fundType : coinProps.getAgreement()) + ", {txAmount}, {fee}, "
                                    + "{dealType}," + blockHeight + ",'" + addTime.getTime() + "','" + configTime.getTime() + "', "
                                    + confirmTimes + ", '{sendWalletName}', '{receiveWalletName}'," + walletBalance + ","
                                    + dealFeeType + ","
                                    + "'" + TimeUtil.getNow().getTime()
                                    + "'," + null + ","
                                    + 0 + ","
                                    + 0 + ","
                                    + 0 + "),";
                            concurrentMergeMap = new ConcurrentHashMap<>();

                        } else {
                            batchBillTxSql += "('" + uuid + "', '" + txId + "', " + fundType + ", " + (coinProps.getAgreement() == 0 ? fundType : coinProps.getAgreement()) + "," + txAmount + ", " + fee + ", "
                                    + dealType + "," + blockHeight + ",'" + addTime + "','" + configTime + "', "
                                    + confirmTimes + ", '" + sendWalletName + "', '" + receiveWalletName + "'," + walletBalance + ","
                                    + dealFeeType + ","
                                    + "'" + TimeUtil.getNow() + "'),";

                            batchBillTxSql1 += "('" + uuid + "', '" + txId + "', " + fundType + ", " + (coinProps.getAgreement() == 0 ? fundType : coinProps.getAgreement()) + "," + txAmount + ", " + fee + ", "
                                    + dealType + "," + blockHeight + ",'" + addTime.getTime() + "','" + configTime.getTime() + "', "
                                    + confirmTimes + ", '" + sendWalletName + "', '" + receiveWalletName + "'," + walletBalance + ","
                                    + dealFeeType + ","
                                    + "'" + TimeUtil.getNow().getTime()
                                    + "'," + null + ","
                                    + 0 + ","
                                    + 0 + ","
                                    + 0 + "),";
                        }
//						}
                        /***Start by gkl 添加钉钉预警 20190514***/
                        if (dealType == DealType.OtherToCold.getKey()) {
                            alarmList.add("10400001TASKCZDZ 冷钱包发生变动：类型：【" + DealType.OtherToCold.getValue() + "】 ，金额：【" + txAmount.toPlainString() + "】 【" + fundTypeName + "】，时间：【" + TimeUtil.getNow() + "】");
                        } else if (dealType == DealType.ColdToOther.getKey()) {
                            alarmList.add("10400001TASKCZDZ 冷钱包发生变动：类型：【" + DealType.ColdToOther.getValue() + "】 ，金额：【" + txAmount.toPlainString() + "】 【" + fundTypeName + "】，时间：【" + TimeUtil.getNow() + "】");
                        } else if (dealType == DealType.OtherToHot.getKey()) {
                            alarmList.add("10400001TASKCZDZ 热提钱包发生变动：类型：【" + DealType.OtherToHot.getValue() + "】 ，金额：【" + txAmount.toPlainString() + "】 【" + fundTypeName + "】，时间：【" + TimeUtil.getNow() + "】");
                        } else if (dealType == DealType.HotToOther.getKey()) {
                            alarmList.add("10400001TASKCZDZ 热提钱包发生变动：类型：【" + DealType.HotToOther.getValue() + "】 ，金额：【" + txAmount.toPlainString() + "】 【" + fundTypeName + "】，时间：【" + TimeUtil.getNow() + "】");
                        }
                        /***End***/
                        /*toAddressList*/
                        /*获取返回的所有数据数组，转换成对象集合第二层*/
                        for (int j = 0; j < addrsArray.size(); j++) {
                            log.info("【新对账】addrsArray.get(j) = " + addrsArray.get(j));
                            /*获取单条对象记录集合*/
                            JSONObject addrsInfo = (JSONObject) addrsArray.get(j);
                            log.info("【新对账】addrsInfo = " + addrsInfo);
                            vn = addrsInfo.getString("vn");
                            toAddress = addrsInfo.getString("toAddress");
                            txNAmount = addrsInfo.getBigDecimal("amount");
                            int dealTypes = addrsInfo.getIntValue("dealType");
                            String sendWalletNames = addrsInfo.getString("sendWalletName");
                            String receiveWalletNames = addrsInfo.getString("receiveWalletName");
                            String walletBalances = addrsInfo.getString("walletBalance");
                            /** start by kinghao 20190111*/
                            String addressTag = "";
                            if (detailInfo.containsKey("memo")) {
                                addressTag = detailInfo.getString("memo");
                            } else if (addrsInfo.containsKey("memo")) {
                                addressTag = addrsInfo.getString("memo");
                            }
                            /**end*/
                            int orgDealType = dealType;
                            int parentType = orgDealType;
                            if (dealTypes > 0) {
                                orgDealType = dealTypes;
                            }

                            if (StringUtil.exist(walletBalances)) {
                                walletBalance = new BigDecimal(walletBalances);
                            }
                            if (StringUtil.exist(sendWalletNames)) {
                                sendWalletName = sendWalletNames;
                            }
                            if (StringUtil.exist(receiveWalletNames)) {
                                receiveWalletName = receiveWalletNames;
                            }
                            txIdN = txId + "_" + vn;
                            log.info("【新对账】vn = " + vn + ", toAddress = " + toAddress + ", txNAmount = " + txNAmount);

                            batchBillTxNSql += "('" + uuid + "', '" + txId + "', " + fundType + ", " + (coinProps.getAgreement() == 0 ? fundType : coinProps.getAgreement()) + "," + txAmount + "," + fee + ","
                                    + parentType + "," + orgDealType + "," + blockHeight + ", '" + addTime + "', '" + configTime + "', "
                                    + confirmTimes + ", '" + sendWalletName + "', '" + receiveWalletName + "', " + walletBalance + ",'"
                                    + txIdN + "', '" + toAddress + "', " + txNAmount + ", '" + TimeUtil.getNow() + "','" + addressTag + "'),";

                            /* start 将dealType==11类型交易按outlist拆分，按拆分后的dealType分组合并 手续费划归dealType==2记录中 modity by Ethan  2019.7.15*/
                            if (parentType == 11) {
                                if (!concurrentMergeMap.containsKey(orgDealType)) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("txAmount", String.valueOf(txNAmount));
                                    map.put("sendWalletName", sendWalletName);
                                    map.put("receiveWalletName", receiveWalletName);
                                    concurrentMergeMap.put(orgDealType, map);
                                } else {
                                    Map<String, String> map = concurrentMergeMap.get(orgDealType);
                                    BigDecimal txAmountS = new BigDecimal((String) map.get("txAmount"));
                                    map.put("txAmount", String.valueOf(txAmountS.add(txNAmount)));
                                    concurrentMergeMap.put(orgDealType, map);
                                }

                                if (j == (addrsArray.size() - 1)) {
                                    String finalFee = String.valueOf(fee);
                                    concurrentMergeMap.forEach((iDealType, iMap) -> {
                                        batchBillTxSql += batchBillTxSqlTemplate
                                                .replace("{txAmount}", iMap.get("txAmount"))
                                                .replace("{fee}", iDealType == 2 ? finalFee : String.valueOf(BigDecimal.ZERO))
                                                .replace("{sendWalletName}", iMap.get("sendWalletName"))
                                                .replace("{receiveWalletName}", iMap.get("receiveWalletName"))
                                                .replace("{dealType}", String.valueOf(iDealType));
                                        batchBillTxSql1 += batchBillTxSql1Template
                                                .replace("{txAmount}", iMap.get("txAmount"))
                                                .replace("{fee}", iDealType == 2 ? finalFee : String.valueOf(BigDecimal.ZERO))
                                                .replace("{sendWalletName}", iMap.get("sendWalletName"))
                                                .replace("{receiveWalletName}", iMap.get("receiveWalletName"))
                                                .replace("{dealType}", String.valueOf(iDealType));
                                    });
                                }
                            }
                            /* end */
                        }
                    }
                }
                List<OneSql> sqls;
                if (!"".equals(batchBillTxSql)) {
                    batchBillTxSql = batchBillTxSql.substring(0, batchBillTxSql.length() - 1) + ";";
                    /*Bill保存到数据库中*/
                    String delSql = "DELETE FROM wallettransbill WHERE addTime > '" + createTime + "' and fundsType = " + fundType;
                    if (fundTypeName.toLowerCase().equals("usdte")) {
                        delSql = delSql + " and agreement = " + coinProps.getAgreement();
                    }
                    sql = "insert into wallettransbill(uuid, txId, fundsType,agreement, txAmount, fee, dealType, blockHeight, addTime, configTime, confirmTimes, "
                            + "sendWallet, receiveWallet, walBalance,dealFeeType,createTime) values " + batchBillTxSql;
                    log.info("sql = " + sql);
                    batchBillTxNSql = batchBillTxNSql.substring(0, batchBillTxNSql.length() - 1) + ";";

                    /*BillDetail保存到数据库中*/
                    String delSql1 = "DELETE FROM wallettransbilldetails WHERE addTime > '" + createTime + "' and fundsType = " + fundType;
                    String detailSql = "insert into wallettransbilldetails(uuid, txId, fundsType,agreement, txAmount, fee,parentType, dealType, blockHeight, addTime, configTime, confirmTimes,  "
                            + "sendWallet, receiveWallet, walBalance, txIdN, toAddress, txNAmount, createTime,addressTag) values " + batchBillTxNSql;

                    sqls = new ArrayList<OneSql>();
                    sqls.add(new OneSql(delSql, -2, new Object[]{}));
                    sqls.add(new OneSql(delSql1, -2, new Object[]{}));
                    sqls.add(new OneSql(sql, -2, new Object[]{}));
                    sqls.add(new OneSql(detailSql, -2, new Object[]{}));
                    boolean flg = Data.doTrans(sqls);
                    if (flg) {
                        if (!CollectionUtils.isEmpty(alarmList)) {
                            for (String alarm : alarmList) {
                                log.info(alarm);
                            }
                        }
                    }
                }
                List<OneSql> sqlsFee = new ArrayList<OneSql>();
                if (!"".equals(batchBillTxSql1)) {
                    if (DealTypeForbbEnum.include(dealTypeForbb)) {
                        batchBillTxSql1 = batchBillTxSql1.substring(0, batchBillTxSql1.length() - 1) + ";";
                        sql = "insert ignore into plat_fee_account_choice (uuid,txId,fundsType,agreement,txAmount,fee,dealType,blockHeight,addTime,configTime,confirmTimes, " +
                                "sendWallet,receiveWallet,walBalance,dealFeeType,createTime,tmp,confirmation,status,checkId) values " + batchBillTxSql1;
                        System.out.println(sql);
                        sqlsFee.add(new OneSql(sql, -2, new Object[]{}));
                        Data.doTrans(sqlsFee);
                    }
                }
                long endTime = System.currentTimeMillis();
                log.info("【新对账】钱包流水记录对账币种【" + fundTypeName + "】【核算耗时：" + (endTime - startTime) + "】");
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

    }


   /* public static void main(String[] args) {
        WalletTransThread walletTransThread= new WalletTransThread(2,"BTC");
        walletTransThread.run();
    }*/
}

package com.world.model.dao.auto.worker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.google.common.collect.Maps;
import com.world.constant.Const;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.dao.pay.DetailsDao;
import com.world.model.dao.pay.DetailsSummaryDao;
import com.world.model.dao.task.Worker;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DetailsBean;
import com.world.model.entity.pay.KeyBean;
import com.world.util.request.HttpUtil;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * jack.d.monkey
 */
public class RechargeTurnoverWorker extends Worker {

    private static final long serialVersionUID = -5589116084131547078L;

    private static final DetailsDao detailsDao = new DetailsDao();
    private static final DetailsSummaryDao detailsSummaryDao = new DetailsSummaryDao();

    public RechargeTurnoverWorker(String name, String des, boolean autoReplace) {
        super(name, des, autoReplace);
    }

    @Override
    public void run() {
        super.run();
        Map<String, CoinProps> map = DatabasesUtil.getNewCoinPropMaps();
        for (Entry<String, CoinProps> entry : map.entrySet()) {
            CoinProps coint = entry.getValue();
            detailsDao.setCoint(coint);//detailsDao用的时候 每次都要重新设置一下币种......
            initFactory(coint);
        }
    }

    private void initFactory(CoinProps coint) {
        String currency = coint.getStag();
        log.info("同步" + currency.toUpperCase() + "交易记录");
        try {
            //获取从交易中心交易记录
            Map<String, String> param = Maps.newHashMap();
            param.put("coin_type", currency);
            String jsonS = HttpUtil.doGetv2(ApiConfig.getValue("tradingcenter.url") + "/openapi/tradingcenter/rechargeTrans", param, 80000, 80000);

            log.info("==RechargeTurnoverWorker==返回报文:" + jsonS);

            JSONObject json = JSONObject.parseObject(jsonS);

            int status = json.getInteger("status");
            int fundsType = coint.getFundsType();
            if(coint.getPropTag().toLowerCase().equals("usdte")){
                fundsType = coint.getAgreement();
            }
            JSONArray dataArray = json.getJSONArray("data");

            if (status == 0 && dataArray.size() > 0) {
                for (int i = 0; i < dataArray.size(); i++) {
                    long now = System.currentTimeMillis();
                    JSONObject txJson = dataArray.getJSONObject(i);

                    String txid = txJson.getString("txId");
                    int confirmations = txJson.getIntValue("confirmations");
                    BigDecimal amount = txJson.getBigDecimal("amount").setScale(9, BigDecimal.ROUND_DOWN);
                    String rechargeAddress = txJson.getString("address");
                    long timeStamp = txJson.getLong("time_stamp");
                    String wallet = txJson.getString("wallet");
                    long blockHeight = txJson.getLong("block_height");//区块高度
                    //地址标签 start by kinghao
                    String addressTag = txJson.getString("memo");
                    //end
                    //检查detail表中是否存在
                    int count = detailsDao.count(txid);

                    if (count == 0) {

                        DetailsBean charge = new DetailsBean();
                        KeyBean lkb = null;
                        //获取用户信息
                        if (coint.isERC()) {
                            lkb = (KeyBean) Data.GetOne("select * from ethKey where keyPre=? and wallet=?", new Object[]{rechargeAddress, wallet}, KeyBean.class);
                            /** start by kinghao 20190111*/
                        } else if ("eos".equals(coint.getDatabaseKey())) {
                            lkb = (KeyBean) Data.GetOne("select * from eosKey where keyPre=? and wallet=? and addressTag=?", new Object[]{rechargeAddress, wallet, addressTag}, KeyBean.class);
                            /** end*/
                        } else {
                            lkb = (KeyBean) Data.GetOne("select * from " + coint.getStag() + "Key where keyPre=? and wallet=?", new Object[]{rechargeAddress, wallet}, KeyBean.class);
                        }
                        if (lkb != null) {
                            charge.setUserId(lkb.getUserId() + "");
                            charge.setUserName(lkb.getUserName());
                        } else {
                            charge.setUserId("0");
                            charge.setUserName("");
                        }


                        charge.setConfirmTimes(confirmations);//确认次数
                        //得到blockhash值
                        charge.setAddHash(txid);//记录发送或接受时的hash值
                        charge.setConfigTime(new Timestamp(now));
                        charge.setAmount(amount);
                        charge.setToAddr(rechargeAddress);//币接受地址
                        charge.setFromAddr("");//币发送地址
                        charge.setAddressTag(addressTag);
                        charge.setStatus(Const.RechargeStatus.CONFIRMING);

                        charge.setWallet(wallet);
                        charge.setSendTime(new Timestamp(timeStamp));//发送int时间
                        charge.setBlockHeight((int) blockHeight);
                        log.info("插入一条" + coint.getTag() + "充值记录txid：" + txid + ";timeStamp:" + timeStamp + ";confirmations:" + confirmations + ";目标确认次数为:" + coint.getInConfirmTimes());
                        OneSql sql = detailsDao.saveOne(charge);

                        int id = Data.Insert(sql.getSql(), sql.getPrams());
                        //充值汇总表插入当前数据
                        OneSql summarySql = detailsSummaryDao.saveOne(charge, id, fundsType);
                        Data.Insert(summarySql.getSql(), summarySql.getPrams());
                    } else {
                        DetailsBean bean = detailsDao.queryOneByTxid(txid);

                        if (bean != null && bean.getStatus() == Const.RechargeStatus.CONFIRMING) {//只有待确认0时才会继续确认数据

                            //查询一下交易地址是否是本系统的地址,如果不是则status设置为1
                            //验证当前处理币种时候是ERC20币种
                            //true 校验地址为ETH地址库
                            //false 校验地址为当前币种地址库

                            Map<String, CoinProps> coinMaps = DatabasesUtil.getNewCoinPropMaps();//币种Map
                            CoinProps coinProps = coinMaps.get(currency);
                            if (coinProps.isERC()) {
                                currency = "eth";
                            }
                            /*update by kinghao 20190111 */
                            List<Long> list = new ArrayList<>();
                            if ("eos".equals(currency)) {
                                String countSql = "select count(1) from " + currency + "Key" + " where keyPre=? and addressTag=?";
                                list = (List<Long>) Data.GetOne(countSql, new Object[]{rechargeAddress, addressTag});
                            } else {
                                String countSql = "select count(1) from " + currency + "Key" + " where keyPre=? ";
                                list = (List<Long>) Data.GetOne(countSql, new Object[]{rechargeAddress});

                            }
                            /*end*/

                            StringBuilder updateSql = new StringBuilder("update " + detailsDao.getTableName() + " set confirmTimes = ?, configTime = ?, blockHeight = ? ");
                            StringBuilder updateSummarySql = new StringBuilder("update detailssummary set confirmTimes = ?, configTime = ?, blockHeight = ? ");

                            if (0 == bean.getConfirmTimes()) {
                                updateSql.append(", sendTime = '" + new Timestamp(timeStamp) + "' ");
                                updateSummarySql.append(", sendTime = '" + new Timestamp(timeStamp) + "' ");
                            }
                            if (CollectionUtils.isEmpty(list) || list.get(0) == 0) {
                                updateSql.append(", sendTime = '" + new Timestamp(timeStamp) + "', status = ?  where addHash = ?");
                                updateSummarySql.append(", sendTime = '" + new Timestamp(timeStamp) + "', status = ?  where fundsType = ? and addHash = ? ");

                                Data.Update(updateSql.toString(), new Object[]{confirmations, new Timestamp(now), blockHeight, Const.RechargeStatus.FAIL, txid});
                                Data.Update(updateSummarySql.toString(), new Object[]{confirmations, new Timestamp(now), blockHeight, Const.RechargeStatus.FAIL, fundsType, txid});
                            } else {

                                updateSql.append(", sendTime = '" + new Timestamp(timeStamp) + "' where addHash = ?");
                                updateSummarySql.append(", sendTime = '" + new Timestamp(timeStamp) + "' where fundsType = ? and addHash = ?");
                                Data.Update(updateSql.toString(), new Object[]{confirmations, new Timestamp(now), blockHeight, txid});
                                Data.Update(updateSummarySql.toString(), new Object[]{confirmations, new Timestamp(now), blockHeight, fundsType, txid});
                            }
                            log.info("update更新" + coint.getTag() + "充值记录txid：" + txid + ";timeStamp:" + timeStamp + ";confirmations:" + confirmations + ";目标确认次数为:" + coint.getInConfirmTimes());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("RFSE:获取充值交易流水异常，异常信息：", e);
        }
    }

    public static void main(String[] args) {
        Map<String, CoinProps> map = DatabasesUtil.getCoinPropMaps();
        for (Entry<String, CoinProps> entry : map.entrySet()) {
            CoinProps coint = entry.getValue();
            if ("eos".equals(coint.getDatabaseKey())) {
                detailsDao.setCoint(coint);//detailsDao用的时候 每次都要重新设置一下币种......
                RechargeTurnoverWorker rechargeTurnoverWorker = new RechargeTurnoverWorker("", "", true);
                rechargeTurnoverWorker.initFactory(coint);
            }
        }

    }

}

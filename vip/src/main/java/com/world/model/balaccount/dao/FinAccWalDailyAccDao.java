package com.world.model.balaccount.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.messi.user.vo.CoinTypeVO;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.balaccount.entity.*;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.usercap.dao.CommAttrDao;
import com.world.model.entity.usercap.entity.CommAttrBean;
import com.world.util.request.HttpUtil;
import com.world.util.string.StringUtil;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>标题: 钱包每日对账DAO</p>
 * <p>描述: 钱包每日对账DAO</p>
 * <p>版权: Copyright (c) 2017</p>
 *
 * @author flym
 */
public class FinAccWalDailyAccDao extends DataDaoSupport<FinAccWalDailyAccBean> {
    Logger logger = Logger.getLogger(FinAccWalDailyAccDao.class);

    private static final long serialVersionUID = 1L;


    ColdWalletBalanceDao coldWalletBalanceDao = new ColdWalletBalanceDao();

    /**
     * 获取热充钱包，热提钱包，区块链冷钱包余额，然后
     * 封装到WalletSelfCheck 对象中
     *
     * @return
     */
    public WalletSelfCheck getWalletSelf() {

        //本方法返回对象
        WalletSelfCheck walletSelfCheck = new WalletSelfCheck();

        //钱包流水-热冲发生额
        Map<Integer, BigDecimal> warmRechargeMap = new HashMap<>();
        //钱包流水-热提发生额
        Map<Integer, BigDecimal> warmWithdrawMap = new HashMap<>();
        //钱包流水-冷钱包发生额
        Map<Integer, BigDecimal> coldValueMap = new HashMap<>();

        //支付中心-热充同步余额
        Map<Integer, BigDecimal> warmRechargeFinaccwalletbillMap = new HashMap<>();
        //支付中心-热提同步余额
        Map<Integer, BigDecimal> warmWithdrawFinaccwalletbillMap = new HashMap<>();
        //爬虫-区块链浏览器余额
        Map<Integer, BigDecimal> browserBalanceMap = new HashMap<>();

        try {
            /*1.1 调用运营后台钱包每日对账接口，获取三个钱包发生额*/
            String url = ApiConfig.getValue("snowmanboss.url");
            url += "/walletBalance/queryList";
            Map<String, String> params = new HashMap<String, String>();
            logger.info("【新对账WalletTransWork】运营后台/walletBalance/queryList-url = " + url);
            String walletBalanceStr = HttpUtil.doPost(url, params, 10000, 20000, false);
            JSONObject walletBalanceJson = JSONObject.parseObject(walletBalanceStr);
            JSONObject walletBalanceData = walletBalanceJson.getJSONObject("data");
            List<BalanceResp> balanceRespList = (List<BalanceResp>) JSONArray.parseArray(walletBalanceData.getString("list"), BalanceResp.class);
            if (balanceRespList.size() > 0) {
                for (BalanceResp balanceResp : balanceRespList) {
                    //钱包流水-热冲发生额
                    warmRechargeMap.put(Integer.parseInt(balanceResp.getFundsType()), new BigDecimal(balanceResp.getHotRechargeHappenedAmount()));
                    //钱包流水-热提发生额
                    warmWithdrawMap.put(Integer.parseInt(balanceResp.getFundsType()), new BigDecimal(balanceResp.getHotWithdrawHappenedAmount()));
                    //钱包流水-冷钱包发生额
                    coldValueMap.put(Integer.parseInt(balanceResp.getFundsType()), new BigDecimal(balanceResp.getColdHappenedAmount()));
                }
            }

            //返回结果设置到 WalletSelfCheck 对象中
            walletSelfCheck.setWarmRechargeMap(warmRechargeMap);
            walletSelfCheck.setWarmWithdrawMap(warmWithdrawMap);
            walletSelfCheck.setColdValueMap(coldValueMap);

            /*1.2 调用支付中心接口获取热冲热提钱包同步余额,返回结果设置到 WalletSelfCheck 对象中*/
//            warmRechargeFinaccwalletbillMap = getWarmRechargeFinaccwalletbillMap();//获取热充钱包金额
//            warmWithdrawFinaccwalletbillMap = getWarmWithdrawFinaccwalletbillMap();//获取热提钱包金额
            //热冲同步余额
            walletSelfCheck.setWarmRechargeFinaccwalletbillMap(warmRechargeFinaccwalletbillMap);
            //热提同步余额
            walletSelfCheck.setWarmWithdrawFinaccwalletbillMap(warmWithdrawFinaccwalletbillMap);
            getHotFinaccwalletBill(walletSelfCheck);

            /*1.3 获取冷钱包区块浏览器MAP,返回结果设置到 WalletSelfCheck 对象中*/
            browserBalanceMap = getBrowserBalanceMap();
            walletSelfCheck.setBrowserBalanceMap(browserBalanceMap);

            /*1.4 遍历币种信息,进行结果后续处理 */
            Map<String, CoinProps> newCoinProps = DatabasesUtil.getNewCoinPropMaps();
            Iterator<Entry<String, CoinProps>> iteCoinProps = newCoinProps.entrySet().iterator();
            String fundTypeName = "";
            int fundsType = 0;
            while (iteCoinProps.hasNext()) {
                Entry<String, CoinProps> entryCoinProps = iteCoinProps.next();
                CoinProps tmpCoinProps = entryCoinProps.getValue();
                fundsType = tmpCoinProps.getFundsType();
                fundTypeName = entryCoinProps.getKey();
                /*判断是否配置了comm_attr中的API接口查询, 注意一点要放在赋值前面*/
                if (browserBalanceMap.containsKey(fundsType)) {
                    /*配置标志*/
                    logger.debug("has = " + fundsType);
                    walletSelfCheck.getApiReqFlag().put(fundsType, "has");
                }
                /*赋值防止空指针*/
                if (null == walletSelfCheck.getWarmRechargeMap().get(fundsType)) {
                    walletSelfCheck.getWarmRechargeMap().put(fundsType, BigDecimal.ZERO);
                }
                if (null == walletSelfCheck.getWarmRechargeFinaccwalletbillMap().get(fundsType)) {
                    walletSelfCheck.getWarmRechargeFinaccwalletbillMap().put(fundsType, BigDecimal.ZERO);
                }
                if (null == walletSelfCheck.getWarmWithdrawMap().get(fundsType)) {
                    walletSelfCheck.getWarmWithdrawMap().put(fundsType, BigDecimal.ZERO);
                }
                if (null == walletSelfCheck.getWarmWithdrawFinaccwalletbillMap().get(fundsType)) {
                    walletSelfCheck.getWarmWithdrawFinaccwalletbillMap().put(fundsType, BigDecimal.ZERO);
                }
                if (null == walletSelfCheck.getColdValueMap().get(fundsType)) {
                    walletSelfCheck.getColdValueMap().put(fundsType, BigDecimal.ZERO);
                }
                if (null == walletSelfCheck.getBrowserBalanceMap().get(fundsType)) {
                    walletSelfCheck.getBrowserBalanceMap().put(fundsType, BigDecimal.ZERO);
                }
            }
        } catch (IOException ie) {
            logger.error("【新对账WalletTransWork】运营后台/walletBalance/queryList接口连接超时", ie);
        } catch (Exception e) {
            logger.error("【新对账WalletTransWork】查询本地余额失败!", e);
        }
        return walletSelfCheck;
    }


    /**
     * 支付中心获取热充同步余额和热提同步余额
     *
     * @param walletSelfCheck
     */
    private void getHotFinaccwalletBill(WalletSelfCheck walletSelfCheck) {

        try {
            String url = ApiConfig.getValue("tradingcenter.url");
            url += "/openapi/tradingcenter/finance/hotWalletBalance" + "?fundsTypeName=";
            String strResult = HttpUtil.doGet(url, null, 10000, 10000);
            logger.info("【新对账WalletTransWork】支付中心/finance/hotWalletBalance-url = " + url + ",返回结果=" + strResult);
            JSONObject jsonResult = com.alibaba.fastjson.JSONObject.parseObject(strResult);
            /*解析返回值*/
            if (null != jsonResult) {
                /*获取返回的所有数据数组，转换成对象集合*/
                JSONArray datasArray = (JSONArray) jsonResult.get("data");
                if (null != datasArray && datasArray.size() > 0) {
                    for (int i = 0; i < datasArray.size(); i++) {
                        JSONObject detailInfo = (JSONObject) datasArray.get(i);
                        String fundsTypeName = detailInfo.getString("fundsTypeName");
                        BigDecimal rechargeWalletBalance = BigDecimal.ZERO;
                        BigDecimal withdrawWalletBalance = BigDecimal.ZERO;
                        try {
                            rechargeWalletBalance = new BigDecimal(detailInfo.getString("rechargeBalance"));
                            withdrawWalletBalance = new BigDecimal(detailInfo.getString("withdrawBalance"));
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }

                        // 获取币种代码，若agreement不等于0，则使用agreement值替换fundsType作为fundsType
                        int fundsType = DatabasesUtil.newCoinPropsByName(fundsTypeName).getFundsType();
                        int agreement = DatabasesUtil.newCoinPropsByName(fundsTypeName).getAgreement();
                        if (agreement != 0) {
                            fundsType = agreement;
                        }

                        //热冲同步余额
                        walletSelfCheck.getWarmRechargeFinaccwalletbillMap().put(fundsType, rechargeWalletBalance);
                        //热提同步余额
                        walletSelfCheck.getWarmWithdrawFinaccwalletbillMap().put(fundsType, withdrawWalletBalance);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

    }

    /**
     * 爬虫爬取各币种区块浏览器冷钱包同步余额
     *
     * @return
     */
    public Map<Integer, BigDecimal> getBrowserBalanceMap() {
        List<ColdWalletBalanceBean> list = coldWalletBalanceDao.findAllColdWalletBalance();
        Map<Integer, BigDecimal> browserBalanceMap = new HashMap<>();
        for (ColdWalletBalanceBean vo : list) {
            browserBalanceMap.put(vo.getFundsType(), vo.getBalance());
        }
        return browserBalanceMap;
    }


    public static void main(String[] args) throws Exception {
        FinAccWalDailyAccDao finAccWalDailyAccDao = new FinAccWalDailyAccDao();
    }
}



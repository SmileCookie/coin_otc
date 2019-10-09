package com.world.controller.api.m;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redis.RedisUtil;
import com.world.cache.Cache;
import com.world.controller.api.util.SystemCode;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.LegalTenderType;
import com.world.model.entity.Market;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.user.User;
import com.world.model.loan.entity.P2pUser;
import com.world.model.loan.worker.LoanAutoFactory;
import com.world.util.CommonUtil;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class V1_2 extends BaseMobileAction {

    static Logger logger = Logger.getLogger(V1_2.class.getName());

    /**
     * 交易总览
     * 1.用户未登录状态下，仅显示各个市场下每个币种的单价，且key都存在设置默认"0.00"
     * 2.用户登录状态下，显示各个市场下的折算资金和各个市场份额等
     *
     * 1.遍历市场集合marketTab，且当前市场存在集合中：
     *     1.1 如果marketTab存在该市场，则获取该市场下币种列表(marketInfo)。
     *     1.2 获取当前币种的简写，单价及用户持有量并计算合计值(symbolInfo)，并add到symbolList中。（小数点根据配置文件获取）
     *     1.3 将该币种列表(symbolList)add到该市场币种列表(marketInfo)中。并将币种列表重新put覆盖marketTab中的信息
     * 2.遍历市场集合marketTab，且当前市场不存在集合中：
     *     2.1 如果marketTab不存在该市场，则以该市场（exchangeBi）为折算单位折算用户总资产（convertFund）。（小数点根据totalFundScale获取）
     *     2.2 获取该市场响应币种用户的可用余额（usableFund）和冻结余额（freezeFund）（小数点根据配置文件获取）
     *     2.2 获取当前币种的简写，单价及用户持有量并计算合计值(symbolInfo),并add到symbolList。（小数点根据配置文件获取）
     *     2.3 将该币种列表(symbolList)、总资产(convertFund)、可用余额(usablmarketTab中的信息eFund)和冻结余额(freezeFund)到该市场币种列表(marketInfo)中.
     *     2.4 获取当前市场exchangeBi的外汇兑换比例（exchangeRate（），并put到marketInfo。
     *     2.5 最后，以exchangeBi为key将市场币种列表(marketInfo) put到marketTab中。
     */
    @Page(Viewer = JSON)
    public void tradeOverview() {

        String userId = param("userId");
        String token = param("token");
        User user = null;
        Map<String,Integer> totalFundScale = new HashMap<String,Integer>();//各个市场的精确值，（资金折算，可用余额，冻结金额，合计资金）
        totalFundScale.put("USDT",2);
        totalFundScale.put("BTC",6);
        if(StringUtil.exist(userId)){
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            user = userDao.findOne("_id",userId);
            if(user.isFreez()){
                json(SystemCode.code_1001, L("该账户已冻结，暂时不能操作。"));
                return;
            }
        }
        Map<String, Object> marketTab = new HashMap<String, Object>();//市场集合Tab
        Map<String, Object> marketInfo = null;
        BigDecimal convertBigDecimal = BigDecimal.ZERO;//折算资金

        String numberBi = null;//币种
        String exchangeBi = null;//市场
        String numberBixDian = null;//小数点
        JSONArray symbolList = null;//币种列表
        Map<String, String> symbolInfo = null;//单一币种信息
        String cachePrice = "0";
        cachePrice = Cache.Get("btc_usdt");
        JSONObject priceJson = LoanAutoFactory.getPrices();
        try{
            Map<String, JSONObject> marketMaps =  CommonUtil.sortMapByValue(Market.getMarketsMap());//获取盘口配置信息
            if(marketMaps!=null && !marketMaps.isEmpty()){
                for(Map.Entry<String, JSONObject> entry :marketMaps.entrySet()){
                    JSONObject m = entry.getValue();
                    numberBi = m.getString("numberBi");
                    exchangeBi = m.getString("exchangeBi");
                    numberBixDian = m.getString("numberBixDian");
                    if(marketTab.containsKey(exchangeBi)){
                        convertBigDecimal = new BigDecimal(String.valueOf(marketInfo.get("convertFund")));
                        marketInfo = (Map)marketTab.get(exchangeBi);
                        symbolList = (JSONArray)marketInfo.get("symbolList");
                        symbolInfo = new HashMap<String, String>();
                        symbolInfo.put("propTag",numberBi);
                        //单价
                        Object priceObject =  priceJson.get(numberBi.toLowerCase()+"_"+exchangeBi.toLowerCase());
                        if(null != priceObject&& StringUtil.exist(priceObject.toString())){
                            symbolInfo.put("lastPrice",priceObject.toString());
                        }
                        if(StringUtil.exist(userId)){
                            symbolInfo.put("holdCount",getHoldCount(user.get_Id(),numberBi,numberBixDian));
                            symbolInfo.put("totalFund",(new BigDecimal(symbolInfo.get("lastPrice")).multiply(new BigDecimal(symbolInfo.get("holdCount")))).setScale(totalFundScale.get(exchangeBi),BigDecimal.ROUND_DOWN).toPlainString());
                            convertBigDecimal = convertBigDecimal.add(new BigDecimal(symbolInfo.get("totalFund")));
                        }else{
                            symbolInfo.put("holdCount","0");
                            symbolInfo.put("totalFund","0");
                        }
                        symbolList.add(symbolInfo);
                        marketInfo.put("symbolList",symbolList);
    //                    marketInfo.put("convertFund",convertBigDecimal.setScale(totalFundScale.get(exchangeBi),BigDecimal.ROUND_HALF_UP).toPlainString());
                        marketTab.put(exchangeBi,marketInfo);
                    }else{
                        symbolList = new JSONArray();
                        symbolInfo = new HashMap<String, String>();
                        symbolInfo.put("propTag",numberBi);
                        //单价
                        Object priceObject =  priceJson.get(numberBi.toLowerCase()+"_"+exchangeBi.toLowerCase());
                        if(null != priceObject&& StringUtil.exist(priceObject.toString())){
                            symbolInfo.put("lastPrice",priceObject.toString());
                        }
                        if(StringUtil.exist(userId)){
                            symbolInfo.put("holdCount",getHoldCount(user.get_Id(),numberBi,numberBixDian));
                            symbolInfo.put("totalFund",(new BigDecimal(symbolInfo.get("lastPrice")).multiply(new BigDecimal(symbolInfo.get("holdCount")))).setScale(totalFundScale.get(exchangeBi),BigDecimal.ROUND_DOWN).toPlainString());
                            convertBigDecimal = convertBigDecimal.add(new BigDecimal(symbolInfo.get("totalFund")));
                        }else{
                            symbolInfo.put("holdCount","0");
                            symbolInfo.put("totalFund","0");
                        }
                        symbolList.add(symbolInfo);
                        marketInfo = new HashMap<String, Object>();
                        marketInfo.put("symbolList",symbolList);
    //                    marketInfo.put("convertFund",convertBigDecimal.setScale(totalFundScale.get(exchangeBi),BigDecimal.ROUND_HALF_UP).toPlainString());
                        if(StringUtil.exist(userId)){
                            marketInfo.put("usableFund",(getFreezeFund(user.get_Id(),exchangeBi,totalFundScale.get(exchangeBi).toString())).get("balance"));
                            marketInfo.put("freezeFund",(getFreezeFund(user.get_Id(),exchangeBi,totalFundScale.get(exchangeBi).toString())).get("freeze"));
                            P2pUser p2pUser = new P2pUser();
                            p2pUser.setUserId(userId);
                            Map<String, PayUserBean> payUsers = UserCache.getUserFundsLoan(p2pUser.getUserId());
                            JSONObject prices = LoanAutoFactory.getPrices();
                            p2pUserDao.resetAsset(payUsers, prices, p2pUser);//计算用户的总资产USD
                            p2pUserDao.resetBtcAssets(payUsers, prices, p2pUser);//计算总资产
                            if("BTC".equals(exchangeBi)){
                                marketInfo.put("convertFund",p2pUser.getTotalAssetsBtc().setScale(totalFundScale.get(exchangeBi),BigDecimal.ROUND_DOWN).toPlainString());//Btc数量
                            }else{
                                marketInfo.put("convertFund",(p2pUser.getTotalAssets()).setScale(totalFundScale.get(exchangeBi),BigDecimal.ROUND_DOWN).toPlainString());//美元数量
                            }
                        }else{
                            marketInfo.put("usableFund","0.00");
                            marketInfo.put("freezeFund","0.00");
                            marketInfo.put("convertFund","0.00");
                        }
                        marketInfo.put("exchangeRate",exchangeRate(exchangeBi));
                        marketTab.put(exchangeBi,marketInfo);
                    }
                }
            }
        }catch (Exception e){
            logger.error("【交易总览】当前用户："+userId+"获取交易信息异常，异常信息为："+e);
            json(SystemCode.code_1002);
            return;
        }
        json(SystemCode.code_1000, marketTab);
    }
    /**
     * 用户资产，新增法币折算legalTender
     * 1.参数不为空，校验合法性通过，优先取值。
     * 2.参数为空，优先根据用户id取缓存进行折算。缓存中无则默认取美元
     */
    @Page(Viewer = JSON)
    public void getUserAssets() {
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");
            String legalTender = param("legalTender");

            String cachePrice = "1";
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            String legalTenderParam = "";
            if(StringUtils.isNotBlank(legalTender)){
                if (!LegalTenderType.existKey(legalTender)) {
                    json(SystemCode.code_1001, "不支持的货币类型");
                    return;
                }
                LegalTenderType legalTenderType = LegalTenderType.valueOf(legalTender);
                legalTenderParam = legalTenderType.getKey() + "_" + legalTenderType.getValue();
            }
            if(StringUtil.exist(legalTenderParam)){
                legalTender = legalTenderParam;
            }else {
                String legalTenderCache = Cache.Get("user_legal_tender_" + userId);
                legalTender = StringUtil.exist(legalTenderCache) ? legalTenderCache : "usd_$";
            }
            cachePrice = Cache.Get("usdt_" + legalTender.split("_")[0].toLowerCase());
            User user = userDao.findOne("_id",userId);
            if(user.isFreez()){
                json(SystemCode.code_1001, L("该账户已冻结，暂时不能操作。"));
                return;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("token", token);
            Map<String, JSONArray> accountMap = getUserAssetInfo(user);
            P2pUser p2pUser = new P2pUser();
            p2pUser.setUserId(userId);
            Map<String, PayUserBean> payUsers = UserCache.getUserFundsLoan(p2pUser.getUserId());
            JSONObject prices = LoanAutoFactory.getPrices();
            p2pUserDao.resetAsset(payUsers, prices, p2pUser);//计算用户的总资产USD
            p2pUserDao.resetBtcAssets(payUsers, prices, p2pUser);//计算总资产
            map.put("convertFund", (p2pUser.getTotalAssets().multiply(new BigDecimal(cachePrice))).setScale(2,BigDecimal.ROUND_DOWN).toPlainString());//法币折算
            map.put("totalAmount", p2pUser.getTotalAssetsBtc());//折合总资产
            map.put("userAccount", accountMap);
            json(SystemCode.code_1000, map);
        } catch (Exception e) {
            log.error("内部异常", e);
            json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
        }
    }

//    private Map<String, JSONArray> getUserAssetInfo(User user) {
//        String userIdStr = user.get_Id();
//        Map<String, JSONArray> accountMap = new HashMap<>();
//        JSONArray funds = UserCache.getUserFunds(userIdStr);
//
//        //buxianguan 20171110 添加币种能否充值提现，不能在这里的缓存里加，trans也会更新这个缓存
//        Map<Integer, CoinProps> coinFundsTypeMap = DatabasesUtil.getCoinPropFundsTypeMaps();
//        for (int i = 0; i < funds.size(); i++) {
//            JSONObject obj = funds.getJSONObject(i);
//            Integer fundsType = obj.getInteger("fundsType");
//            if (null != fundsType) {
//                CoinProps coinProps = coinFundsTypeMap.get(fundsType);
//                if (null != coinProps) {
//                    obj.put("canCharge", coinProps.isCanCharge());//是否支持充值
//                    obj.put("canWithdraw", coinProps.isCanWithdraw());//是否支持提现
//                }
//            }
//        }
//
//        accountMap.put("balances", funds);
//        return accountMap;
//    }

    private String getHoldCount(String _Id,String symbolType,String scale){
        int  scaleInt = 4;
        if(StringUtil.exist(scale)){
            scaleInt = Integer.parseInt(scale);
        }
        JSONArray funds = UserCache.getUserFunds(_Id);
        for(int i=0; i<funds.size(); i++) {
            JSONObject obj = funds.getJSONObject(i);
            if (obj.getString("propTag").equals(symbolType)) {
                return obj.getBigDecimal("balance").setScale(scaleInt,BigDecimal.ROUND_DOWN).toPlainString();
            }
        }
        return "0";
    }

    private Map getFreezeFund(String _Id,String symbolType,String scale){
        Map map = new HashMap<String,String>();
        map.put("freeze","0.00");
        map.put("balance","0.00");
        int  scaleInt = 4;
        if(StringUtil.exist(scale)){
            scaleInt = Integer.parseInt(scale);
        }
        JSONArray funds = UserCache.getUserFunds(_Id);
        for(int i=0; i<funds.size(); i++) {
            JSONObject obj = funds.getJSONObject(i);
            if (obj.getString("propTag").equals(symbolType)) {
                map.put("freeze",obj.getBigDecimal("freeze").setScale(scaleInt,BigDecimal.ROUND_DOWN).toPlainString());
                map.put("balance",obj.getBigDecimal("balance").setScale(scaleInt,BigDecimal.ROUND_DOWN).toPlainString());
                return map;
            }
        }
        return map;
    }

    private Map exchangeRate(String exchangeBi){
        exchangeBi = exchangeBi.toLowerCase();
        Map<String,String> map = new HashMap<String,String>();
        map.put("USD",Cache.Get(exchangeBi+"_usdt"));
        map.put("CNY", Cache.Get(exchangeBi+"_cny"));
        map.put("AUD", Cache.Get(exchangeBi+"_aud"));
        map.put("EUR", Cache.Get(exchangeBi+"_eur"));
        map.put("GBP", Cache.Get(exchangeBi+"_gbp"));
        if("usdt".equals(exchangeBi)){
            map.put("USD","1");
        }
        return map;
    }

    /**
     * 根据用户登录的IP地址，获取当前的折算法币
     * 预留接口，暂时默认返回人民币。
     */
    @Page(Viewer = JSON)
    public void getLegalTender(){
        String ip = param("ip");
        //http://ip.taobao.com/service/getIpInfo.php?ip=202.198.16.3
        Map map = new HashMap<String,String>();
        map.put("legalTender","CNY");
        json(SystemCode.code_1000, map);
    }

    @Page(Viewer = JSON)
    public void superNodeProduceInfo() {
        setLan();

        //初创节点数量
        String homeMadeNodeShowNum = RedisUtil.get("fin_homeMadeNodeShowNum");
        if (StringUtils.isBlank(homeMadeNodeShowNum) ) {
            homeMadeNodeShowNum = "0";
        }

        //初创节点累积收益
        String homeMadeNodeTotalProfit = RedisUtil.get("fin_homeMadeNodeTotalProfit");
        if (StringUtils.isBlank(homeMadeNodeTotalProfit) ) {
            homeMadeNodeTotalProfit = "0";
        }

        //初创节点已发放收益
        String homeMadeNodePayProfit = RedisUtil.get("fin_homeMadeNodePayProfit");
        if (StringUtils.isBlank(homeMadeNodePayProfit)  ) {
            homeMadeNodePayProfit = "0";
        }

        //固定节点数量
        String fixedMadeNodeShowNum = RedisUtil.get("fin_fixedMadeNodeShowNum");
        if (StringUtils.isBlank(fixedMadeNodeShowNum)  ) {
            fixedMadeNodeShowNum = "0";
        }

        //固定节点累积收益
        String fixedMadeNodeTotalProfit = RedisUtil.get("fin_fixedMadeNodeTotalProfit");
        if (StringUtils.isBlank(fixedMadeNodeTotalProfit) ) {
            fixedMadeNodeTotalProfit = "0";
        }

        //固定节点已发放收益
        String fixedMadeNodePayProfit = RedisUtil.get("fin_fixedMadeNodePayProfit");
        if (StringUtils.isBlank(fixedMadeNodePayProfit) ) {
            fixedMadeNodePayProfit = "0";
        }

        //动态节点数量
        String trendsMadeNodeShowNum = RedisUtil.get("fin_trendsMadeNodeShowNum");
        if (StringUtils.isBlank(trendsMadeNodeShowNum)  ) {
            trendsMadeNodeShowNum = "0";
        }

        //动态节点累积收益
        String trendsMadeNodeTotalProfit = RedisUtil.get("fin_trendsMadeNodeTotalProfit");
        if (StringUtils.isBlank(trendsMadeNodeTotalProfit) ) {
            trendsMadeNodeTotalProfit = "0";
        }

        //动态节点已发放收益
        String trendsMadeNodePayProfit = RedisUtil.get("fin_trendsMadeNodePayProfit");
        if (StringUtils.isBlank(trendsMadeNodePayProfit) ) {
            trendsMadeNodePayProfit = "0";
        }

        Integer bonus=0;
        Integer accelerator=0;
        //节点用作静态和保险分红字段显示 初期-固定-动态
        try {
            bonus = Integer.valueOf(homeMadeNodeShowNum) - Integer.valueOf(fixedMadeNodeShowNum) - Integer.valueOf(trendsMadeNodeShowNum);

            //固定超级主节点提示字段 固定+动态
            accelerator = Integer.valueOf(fixedMadeNodeShowNum) + Integer.valueOf(trendsMadeNodeShowNum);
        }catch (Exception e){
            log.info("保险分红字段查询为null",e);
        }
        String resultMsg = L("初创吐司");

        resultMsg = resultMsg.replaceAll("xxx", "" + bonus);
        resultMsg = resultMsg.replaceAll("yyy", "" + accelerator);

        String fixedMsg = L("固定吐司");
        fixedMsg = fixedMsg.replaceAll("xxx", "" + fixedMadeNodeShowNum);

        String trendsMsg = L("固定吐司");
        trendsMsg = trendsMsg.replaceAll("xxx", "" + trendsMadeNodeShowNum);


        Map<String, String> map = new HashMap<>();
        map.put("homeMadeNodeShowNum", homeMadeNodeShowNum);
        map.put("homeMadeNodeTotalProfit", homeMadeNodeTotalProfit);
        map.put("homeMadeNodePayProfit", homeMadeNodePayProfit);
        map.put("fixedMadeNodeShowNum", fixedMadeNodeShowNum);
        map.put("fixedMadeNodeTotalProfit", fixedMadeNodeTotalProfit);
        map.put("trendsMadeNodeShowNum", trendsMadeNodeShowNum);
        map.put("trendsMadeNodeTotalProfit", trendsMadeNodeTotalProfit);
        map.put("trendsMadeNodePayProfit", trendsMadeNodePayProfit);
        map.put("fixedMadeNodePayProfit", fixedMadeNodePayProfit);
        map.put("homeMadeNodeTips", resultMsg.toString());
        map.put("fixedMadeNodeTips", fixedMsg.toString());
        map.put("trendsMadeNodeTips", trendsMsg.toString());

        json(SystemCode.code_1000, "",JSONObject.toJSONString(map));



    }
}
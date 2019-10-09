package com.world.data.database;

import com.Lan;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.config.json.JsonConfig;
import com.world.data.mysql.Data;
import com.world.model.dao.recharge.ChargeManagementDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.recharge.ChargeManagement;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DatabasesUtil {
    private static Logger log = Logger.getLogger(DatabasesUtil.class.getName());

    private static Map<String, CoinProps> coinPropMaps = null;

    private static Map<String, CoinProps> newCoinPropMaps = null;

    private static Map<String, CoinProps> coinPropMapsInter = null;

    private static Map<Integer, CoinProps> coinPropFundsTypeMaps = null;

    private static Map<Integer, CoinProps> usdtMaps = null;

    private static Map<Integer, Map<String, Integer>> coinMap = null;

    public static String databases = "|";

    private static volatile Long curTime = 0L;


    /**
     * 获取币种信息
     * @return
     */
    public static synchronized Map<String, CoinProps> getCoinPropMaps() {
        if (coinPropMaps == null) {
            initCoinProp();
        }
        if (coinMap == null || System.currentTimeMillis() - DateUtils.MILLIS_PER_SECOND * 10 > curTime) {
            coinMap = (Map<Integer, Map<String, Integer>>) Cache.GetObj("CHARGE_WITHDRAW");
            if (coinMap == null) {
                coinMap = new LinkedHashMap<>();
                List<ChargeManagement> chargeManagementList = new ChargeManagementDao().findList();
                for (ChargeManagement coinMapData : chargeManagementList) {
                    Map<String, Integer> managementMapData = new LinkedHashMap<>();
                    managementMapData.put("canCharge", coinMapData.getRecharge());
                    managementMapData.put("canWithdraw", coinMapData.getWithdraw());
                    managementMapData.put("display", coinMapData.getDisplay());
                    coinMap.put(coinMapData.getFundstype(), managementMapData);
                }
                Cache.SetObj("CHARGE_WITHDRAW", coinMap);
            }
            curTime = System.currentTimeMillis();
        }
        for (Entry<String, CoinProps> entry : coinPropMaps.entrySet()) {
            for (Entry<Integer, Map<String, Integer>> coinMapData : coinMap.entrySet()) {
                if (entry.getValue().getFundsType() == coinMapData.getKey()) {
                    Boolean recharge = coinMapData.getValue().get("canCharge") == 1 ? true : false;
                    Boolean withdraw = coinMapData.getValue().get("canWithdraw") == 1 ? true : false;
                    Boolean display = true;
                    if(coinMapData.getValue().get("display") != null){
                        display = coinMapData.getValue().get("display") == 1 ? true : false;
                    }
                    entry.getValue().setCanCharge(recharge);
                    entry.getValue().setCanWithdraw(withdraw);
                    entry.getValue().setDisplay(display);
                }
            }
        }
        coinPropMaps.remove("usdte");
        return coinPropMaps;
    }





    /**
     * 获取币种信息,包含USDTE
     * @return
     */
    public static synchronized Map<String, CoinProps> getNewCoinPropMaps() {
        if (newCoinPropMaps == null) {
            initCoinProp();
        }
        if (coinMap == null || System.currentTimeMillis() - DateUtils.MILLIS_PER_SECOND * 10 > curTime) {
            coinMap = (Map<Integer, Map<String, Integer>>) Cache.GetObj("CHARGE_WITHDRAW");
            if (coinMap == null) {
                coinMap = new LinkedHashMap<>();
                List<ChargeManagement> chargeManagementList = new ChargeManagementDao().findList();
                for (ChargeManagement coinMapData : chargeManagementList) {
                    Map<String, Integer> managementMapData = new LinkedHashMap<>();
                    managementMapData.put("canCharge", coinMapData.getRecharge());
                    managementMapData.put("canWithdraw", coinMapData.getWithdraw());
                    managementMapData.put("display", coinMapData.getDisplay());
                    coinMap.put(coinMapData.getFundstype(), managementMapData);
                }
                Cache.SetObj("CHARGE_WITHDRAW", coinMap);
            }
            curTime = System.currentTimeMillis();
        }
        for (Entry<String, CoinProps> entry : newCoinPropMaps.entrySet()) {
            for (Entry<Integer, Map<String, Integer>> coinMapData : coinMap.entrySet()) {
                if (entry.getValue().getFundsType() == coinMapData.getKey()) {
                    Boolean recharge = coinMapData.getValue().get("canCharge") == 1 ? true : false;
                    Boolean withdraw = coinMapData.getValue().get("canWithdraw") == 1 ? true : false;
                    Boolean display = true;
                    if(coinMapData.getValue().get("display") != null){
                        display = coinMapData.getValue().get("display") == 1 ? true : false;
                    }
                    entry.getValue().setCanCharge(recharge);
                    entry.getValue().setCanWithdraw(withdraw);
                    entry.getValue().setDisplay(display);
                }
                if (entry.getValue().getAgreement() == coinMapData.getKey()) {
                    Boolean recharge = coinMapData.getValue().get("canCharge") == 1 ? true : false;
                    Boolean withdraw = coinMapData.getValue().get("canWithdraw") == 1 ? true : false;
                    Boolean display = true;
                    if(coinMapData.getValue().get("display") != null){
                        display = coinMapData.getValue().get("display") == 1 ? true : false;
                    }
                    entry.getValue().setCanCharge(recharge);
                    entry.getValue().setCanWithdraw(withdraw);
                    entry.getValue().setDisplay(display);
                }
                }
        }
        return newCoinPropMaps;

    }

    /**
     * 获取币种信息,包含USDTE
     * @return
     */
    public static synchronized CoinProps getUsdtAggrement(Integer agreement) {
        if(usdtMaps == null){
            initUsdtAggrement();
        }
        return usdtMaps.get(agreement);
    }

    /**
     * 初始化币种类型集合
     */
    private static void initUsdtAggrement() {
        usdtMaps = new LinkedHashMap<>();

        JSONArray vipConfig = (JSONArray) JsonConfig.getValue("coinProp");

        for (int i = 0; i < vipConfig.size(); i++) {
            JSONObject coin = vipConfig.getJSONObject(i);
            CoinProps cp = getCoin(coin);
            if (coin != null && cp != null) {
                if(cp.getFundsType() == 10 && cp.getAgreement() != 0){
                    usdtMaps.put(cp.getAgreement(),cp);
                }else{
                    usdtMaps.put(cp.getFundsType(),cp);
                }
            }
        }
    }

    /**
     * 重新加载币种信息
     */
    public static synchronized boolean reloadCoinProp() {
        try {
            log.info("重新加载币种信息：开始");
            initCoinProp();
            log.info("重新加载币种信息：结束");
            return true;
        } catch (Exception e) {
            log.info("重新加载币种信息失败");
            return false;
        }

    }

    /**
     * 初始化币种信息
     */
    private static void initCoinProp() {
        databases = "|";
        coinPropMaps = new LinkedHashMap<>();
        newCoinPropMaps = new LinkedHashMap<>();
        usdtMaps = new LinkedHashMap<>();
        JSONArray vipConfig = (JSONArray) JsonConfig.getValue("coinProp");

        for (int i = 0; i < vipConfig.size(); i++) {
            JSONObject coin = vipConfig.getJSONObject(i);
            CoinProps cp = getCoin(coin);
            if (coin != null && cp != null) {
                databases += coin.getString("key") + "|";
                coinPropMaps.put(coin.getString("key"), cp);
                newCoinPropMaps.put(coin.getString("key"), cp);
                if(cp.getFundsType() == 10 && cp.getAgreement() != 0){
                    usdtMaps.put(cp.getAgreement(),cp);
                }else{
                    usdtMaps.put(cp.getFundsType(),cp);
                }
            }
        }
        // add by suxinjie 20171120 币种根据serNum进行排序
        if (coinPropMaps != null) {
            Map<String, CoinProps> sortedMap = new LinkedHashMap<>();
            List<Entry<String, CoinProps>> entryList = new ArrayList<>(coinPropMaps.entrySet());
            Collections.sort(entryList, new Comparator<Entry<String, CoinProps>>(){

                @Override
                public int compare(Entry<String, CoinProps> e1, Entry<String, CoinProps> e2) {
                    int serNum1 = e1.getValue().getSerNum();
                    int serNum2 = e2.getValue().getSerNum();

                    return serNum1 - serNum2;
                }
            });

            Iterator<Entry<String, CoinProps>> iter = entryList.iterator();
            Entry<String, CoinProps> tmpEntry;
            while (iter.hasNext()) {
                tmpEntry = iter.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }

            coinPropMaps = sortedMap;
        }
        if (newCoinPropMaps != null) {
            Map<String, CoinProps> sortedMap = new LinkedHashMap<>();
            List<Entry<String, CoinProps>> entryList = new ArrayList<>(newCoinPropMaps.entrySet());
            Collections.sort(entryList, new Comparator<Entry<String, CoinProps>>(){

                @Override
                public int compare(Entry<String, CoinProps> e1, Entry<String, CoinProps> e2) {
                    int serNum1 = e1.getValue().getSerNum();
                    int serNum2 = e2.getValue().getSerNum();

                    return serNum1 - serNum2;
                }
            });

            Iterator<Entry<String, CoinProps>> iter = entryList.iterator();
            Entry<String, CoinProps> tmpEntry;
            while (iter.hasNext()) {
                tmpEntry = iter.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }

            newCoinPropMaps = sortedMap;
        }
    }

    /**
     * start by xwz 2017-06-09 ps:国际化币种全称
     *
     * TODO 融资融币使用，后续可能弃用
     * @param lan
     * @return
     */
    public static synchronized Map<String, CoinProps> getCoinPropMapsInter(String lan) {
        if (coinPropMapsInter == null) {
            coinPropMapsInter = new LinkedHashMap<String, CoinProps>();
            JSONArray vipConfig = (JSONArray) JsonConfig.getValue("coinProp");
            for (int i = 0; i < vipConfig.size(); i++) {

                JSONObject coin = vipConfig.getJSONObject(i);
                for (String str : coin.keySet()) {
                    if (str.equals("coinFullNameEn")) {
                        coin.put(str, Lan.Language(lan, (String) coin.get(str)));
                    }

                }
                CoinProps cp = getCoin(coin);
                if (coin != null && cp != null) {
                    databases += coin.getString("key") + "|";
                    coinPropMapsInter.put(coin.getString("key"), cp);
                }
            }
        }
        return coinPropMapsInter;
    }

    /**
     * 获取币种类型集合
     * @return
     */
    public static synchronized Map<Integer, CoinProps> getCoinPropFundsTypeMaps() {
        if (coinPropFundsTypeMaps == null) {
            initCoinPropFundsTypeMaps();
        }
        return coinPropFundsTypeMaps;
    }

    /**
     * 初始化币种类型集合
     */
    private static void initCoinPropFundsTypeMaps() {
        coinPropFundsTypeMaps = new LinkedHashMap<>();

        JSONArray vipConfig = (JSONArray) JsonConfig.getValue("coinProp");

        for (int i = 0; i < vipConfig.size(); i++) {
            JSONObject coin = vipConfig.getJSONObject(i);
            CoinProps cp = getCoin(coin);
            if (coin != null && cp != null) {
                coinPropFundsTypeMaps.put(cp.getFundsType(), cp);
            }
        }
    }

    /**
     * 重新加载币种类型集合
     */
    public static synchronized boolean reloadCoinPropFundsTypeMaps() {
        try {
            log.info("重新加载币种类型集合:开始");
            initCoinPropFundsTypeMaps();
            log.info("重新加载币种类型集合:结束");
            return true;
        } catch (Exception e) {
            log.error("重新加载币种类型集合失败");
            return false;
        }
    }

    public static CoinProps getCoinPropsByFundsType(int fundsType) {
        if (coinPropFundsTypeMaps == null) {
            getCoinPropFundsTypeMaps();
        }
        CoinProps curCoin = coinPropFundsTypeMaps.get(fundsType);
        if (null == curCoin) {
            curCoin = coinProps("btc");
        }
        return curCoin;
    }

    public static CoinProps getCoin(JSONObject coin) {
        CoinProps curCoin = null;
        String IN_DATABASE = null;
        if (coin.containsKey("key")) {
            IN_DATABASE = coin.getString("key");
        }
        if (IN_DATABASE != null && IN_DATABASE.length() > 0) {
            IN_DATABASE = IN_DATABASE.toLowerCase();
            curCoin = new CoinProps(coin.getIntValue("fundsType"), IN_DATABASE, coin.getString("value"), coin.getString("propTag"), coin.getString("outData"), coin.getString("unitTag"));
            curCoin.setWeb(coin.getString("weburl"));
            curCoin.setPriceKey(coin.getString("priceKey"));
            curCoin.setCoin(coin.getBooleanValue("coin"));
            curCoin.setDayFreetrial(coin.getBigDecimal("dayFreetrial"));
            curCoin.setDayCash(coin.getBigDecimal("dayCash"));
            curCoin.setValue(coin.getString("value"));
            curCoin.setTimesCash(coin.getBigDecimal("timesCash"));
            curCoin.setMinFees(coin.getBigDecimal("minFees"));
            curCoin.setMinCash(coin.getBigDecimal("minCash"));
            curCoin.setInConfirmTimes(coin.getIntValue("inConfirmTimes"));
            curCoin.setOutConfirmTimes(coin.getIntValue("outConfirmTimes"));
            curCoin.setPropEnName(coin.getString("coinFullNameEn"));
            curCoin.setERC(coin.getBooleanValue("isERC"));
            curCoin.setAgreement(coin.getIntValue("agreement"));
            /*start by kinghao 20181121 图片url*/
            curCoin.setImgUrl(coin.getString("imgUrl"));
            /*end*/
            if(null != coin.getBoolean("canCharge")){
                curCoin.setCanCharge(coin.getBoolean("canCharge"));
            }
            if(null != coin.getBoolean("canWithdraw")){
                curCoin.setCanWithdraw(coin.getBoolean("canWithdraw"));
            }
            curCoin.setSerNum(coin.getIntValue("serNum"));

            try {
                createTable(IN_DATABASE);
            } catch (Exception e) {
                log.error("创建表异常", e);
                throw new RuntimeException("创建表异常");
            }
        }
        return curCoin;
    }

    public static CoinProps coinProps(String IN_DATABASE) {
        IN_DATABASE = IN_DATABASE.toLowerCase();

        coinPropMaps = getCoinPropMaps();

        CoinProps curCoin = coinPropMaps.get(IN_DATABASE);

        if (curCoin == null) {//默认btc
            curCoin = coinPropMaps.get("btc");
            if (curCoin == null) {
                curCoin = new CoinProps(2, "btc", "比特币", "BTC", GlobalConfig.mysqlDb_1_btc, "฿");
                curCoin.setWeb("https://blockchain.info/zh-cn/address/");
                coinPropMaps.put(curCoin.getDatabaseKey(), curCoin);
            }
        }
        return curCoin;
    }

    public static CoinProps coinProps(int fundsType) {
        coinPropMaps = getCoinPropMaps();
        CoinProps curCoin = null;
        for (Entry<String, CoinProps> entry : coinPropMaps.entrySet()) {
            if (entry.getValue().getFundsType() == fundsType) {
                curCoin = entry.getValue();
                break;
            }
        }
        if (curCoin == null) {
            curCoin = coinProps("btc");
        }

        return curCoin;
    }

    public static CoinProps coinPropsByName(String coinName) {
        coinPropMaps = getCoinPropMaps();
        CoinProps curCoin = null;
        for (Entry<String, CoinProps> entry : coinPropMaps.entrySet()) {
            if (entry.getValue().getPropTag().toLowerCase().equals(coinName.toLowerCase())) {
                curCoin = entry.getValue();
                break;
            }
        }

        return curCoin;
    }

    public static CoinProps newCoinPropsByName(String coinName) {
        newCoinPropMaps = getNewCoinPropMaps();
        CoinProps curCoin = null;
        for (Entry<String, CoinProps> entry : newCoinPropMaps.entrySet()) {
            if (entry.getValue().getPropTag().toLowerCase().equals(coinName.toLowerCase())) {
                curCoin = entry.getValue();
                break;
            }
        }
        return curCoin;
    }

    /**
     * 判断是否是数据库相关的字段
     *
     * @param param
     * @return
     */
    public static boolean isDatabase(String param) {
        return databases.indexOf(param.toLowerCase()) >= 0;
    }

    private static String[] cointTables = new String[]{"details", "download", "key", "receiveaddr"};
    private static String hasExistTable = "btc";

    private static void createTable(String key) {
        try {
            for (int i = 0; i < cointTables.length; i++) {
                String table = key + cointTables[i];
                String mainTable = hasExistTable + cointTables[i];

                String createSql = "show create table " + mainTable;
                List ress = (List) Data.GetOne(createSql, new Object[]{});

                if (ress != null) {
                    String srcCreateSql = (String) ress.get(1);
                    String createTable = "CREATE TABLE `" + mainTable + "`";
                    if (srcCreateSql.startsWith(createTable)) {
                        String sql = srcCreateSql.replace(createTable, "CREATE TABLE IF NOT EXISTS `" + table + "`");
                        Data.Update(sql, null);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    public static void main(String[] args) {
        DatabasesUtil.getNewCoinPropMaps();
    }

}

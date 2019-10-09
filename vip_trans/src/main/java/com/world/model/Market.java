package com.world.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tenstar.Arith;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.config.json.JsonConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

public class Market {
    protected static Logger log = Logger.getLogger(Market.class.getName());

    public String numberBi;
    public String numberBiEn;
    public String numberBiNote;
    public long numberBixNormal;
    public long numberBixShow;//"+//显示的计量单位是最小存储单位的倍数,不相等的时候会缩小到指定单位（等于小数点位数）
    public int numberBixDian;//=3;"+//小数点后几位，如果有小数的话
    public String exchangeBi;//=\"比特币\";"+
    public String exchangeBiEn;//=\"BTC\";"+
    public String exchangeBiNote;//=\"฿\";"+
    public String market;//=\"btqdefault\";"+
    public long exchangeBixNormal;//=100000000;"+//标准单位是最小存储单位的位数
    public long exchangeBixShow;//=100000000;"+//显示的计量单位是最小存储单位的倍数
    public int exchangeBixDian;//=4;"+//小数点后几位，如果有小数的话
    public String entrustUrlBase;//=\"/\";"+

    public int webId;
    public String ip;
    public int port;
    public String hashSec;//加密公钥
    public int serNum;//市场序号。序号越小越靠前排序
    public String mergePrice;//盘口档位合并价格

    /**
     * 这个字段废弃，拆分成买方卖方手续费
     */
    public double feeRate;//交易手续费费率

    public int maxPrice;

    public String db;//币种对应数据库名
    public boolean listenerOpen;//盘口引擎是否启动
    public int numberBiFundsType;//币种资金类型（对应用户资产表的资金类型）
    public int exchangeBiFundsType;//兑换币种资金类型（对应用户资产表的资金类型）
    public double minAmount;//最小成交金额
    public double maxAmount;//最da成交金额
    public String numberBiFullName;//本币币种全称
    public double bixMinNum;//最小交易数量
    public double bixMaxNum;//最大交易数量  add by kinghao 20181121


    public double takerFeeRate;//买方交易手续费费率
    public double makerFeeRate;//卖方交易手续费费率

    /**
     * 是否是简化版
     *
     * @return
     */
    public String toString() {
        String ls = "var numberBi=\"" + numberBi + "\";" +
                "var numberBiEn=\"" + numberBiEn + "\";" +
                "var numberBiEn=\"" + numberBiFullName + "\";" +
                "var numberBiNote=\"" + numberBiNote + "\";" +
                "var numberBixNormal=" + numberBixNormal + ";" +//标准单位是最小存储单位的位数
                "var numberBixShow=" + numberBixShow + ";" +//显示的计量单位是最小存储单位的倍数,不相等的时候会缩小到指定单位（等于小数点位数）
                "var numberBixDian=" + numberBixDian + ";" +//小数点后几位，如果有小数的话
                "var exchangeBi=\"" + exchangeBi + "\";" +
                "var exchangeBiEn=\"" + exchangeBiEn + "\";" +
                "var exchangeBiNote=\"" + exchangeBiNote + "\";" +
                "var market=\"" + market + "\";" +
                "var exchangeBixNormal=" + exchangeBixNormal + ";" +//标准单位是最小存储单位的位数
                "var exchangeBixShow=" + exchangeBixShow + ";" +//显示的计量单位是最小存储单位的倍数
                "var exchangeBixDian=" + exchangeBixDian + ";" +//小数点后几位，如果有小数的话
                "var entrustUrlBase=\"" + entrustUrlBase + "\";" +
                "var db=\"" + db + "\";" +
                "var minAmount=\"" + minAmount + "\";" +
                "var bixMaxNum=\"" + bixMaxNum + "\";" +  // add by kinghao 20181121 添加单笔最大交易限额字段
                "var bixMinNum=\"" + bixMinNum + "\";" +
                "var feeRate=\"" + feeRate + "\";" +
                "var takerFeeRate=\"" + takerFeeRate + "\";" +
                "var makerFeeRate=\"" + makerFeeRate + "\";";
        return ls;
    }

    // 所有市场
    public static Map<String, Market> markets = new HashMap<>();
    // 所有市场分类
    public static Set<String> marketTypes = new HashSet<>();

    /**
     * 初始化市场配置
     */
    static {
        initMarket();
    }

    /**
     * 重新加载市场配置
     */
    public static synchronized boolean reloadMarket() {
        try {
            log.info("重新加载市场配置(market)：开始");
            initMarket();
            log.info("重新加载市场配置(market)：结束");
            return true;
        } catch (Exception e) {
            log.error("重新加载市场配置(market)失败");
            return false;
        }

    }

    /**
     * 初始化市场配置
     */
    private static void initMarket() {
        //初始化markets配置文件
        try {
            Cache.Delete("ALL_MARKETS");
            JSONArray jas = (JSONArray) JsonConfig.getValue("markets");
            log.info("从配置文件获取市场信息："+jas.toJSONString());
            if (jas != null && jas.size() > 0) {
                markets = new HashMap<>();
                marketTypes = new HashSet<>();

                for (Object o : jas) {
                    JSONObject jo = (JSONObject) o;
                    Market m = fillData(jo);
                    if (StringUtils.isNotBlank(m.numberBiEn) && StringUtils.isNotBlank(m.market)) {
                        markets.put(m.market, m);
                        marketTypes.add(m.market.split("_")[1]);
                    }

                }
                Cache.SetObj("ALL_MARKETS", getAllMarkets());
//                Cache.SetObj("ALL_MARKET_TYPES", marketTypes);

            }
        } catch (Exception e) {
            log.error("初始化市场配置失败", e);
            throw new RuntimeException("初始化市场配置失败");
        }
    }

    /**
     * 将json对象转化为Market 对象
     *
     * @param jo json 对象
     * @return
     */
    public static Market fillData(JSONObject jo) {
        Market m = new Market();
        m.numberBi = jo.containsKey("numberBi") ? jo.getString("numberBi") : "";
        m.numberBiEn = jo.containsKey("numberBiEn") ? jo.getString("numberBiEn") : "";
        m.numberBiNote = jo.containsKey("numberBiNote") ? jo.getString("numberBiNote") : "";
        m.numberBixNormal = jo.containsKey("numberBixNormal") ? Long.parseLong(jo.getString("numberBixNormal")) : 0;
        m.numberBixShow = jo.containsKey("numberBixShow") ? Long.parseLong(jo.getString("numberBixShow")) : 0;
        m.numberBixDian = jo.containsKey("numberBixDian") ? Integer.parseInt((jo.getString("numberBixDian"))) : 0;
        m.exchangeBi = jo.containsKey("exchangeBi") ? jo.getString("exchangeBi") : "";
        m.exchangeBiEn = jo.containsKey("exchangeBiEn") ? jo.getString("exchangeBiEn") : "";
        m.exchangeBiNote = jo.containsKey("exchangeBiNote") ? jo.getString("exchangeBiNote") : "";
        m.market = jo.containsKey("market") ? jo.getString("market") : "";
        m.exchangeBixNormal = jo.containsKey("exchangeBixNormal") ? Long.parseLong(jo.getString("exchangeBixNormal")) : 0;
        m.exchangeBixShow = jo.containsKey("exchangeBixShow") ? Long.parseLong(jo.getString("exchangeBixShow")) : 0;
        m.exchangeBixDian = jo.containsKey("exchangeBixDian") ? Integer.parseInt(jo.getString("exchangeBixDian")) : 0;
        m.entrustUrlBase = jo.containsKey("entrustUrlBase") ? jo.getString("entrustUrlBase") : "";
        m.webId = jo.containsKey("webId") ? Integer.parseInt(jo.getString("webId")) : 0;
        m.ip = jo.containsKey("ip") ? jo.getString("ip") : "";
        m.port = jo.containsKey("port") ? Integer.parseInt(jo.getString("port")) : 0;
        m.hashSec = jo.containsKey("hashSec") ? jo.getString("hashSec") : "";
        m.serNum = jo.containsKey("serNum") ? jo.getIntValue("serNum") : 999;
        m.mergePrice = jo.containsKey("mergePrice") ? jo.getString("mergePrice") : "";
        m.maxPrice = jo.containsKey("maxPrice") ? Integer.parseInt(jo.getString("maxPrice")) : 0;
        m.feeRate = jo.containsKey("feeRate") ? Double.parseDouble(jo.getString("feeRate")) : 0;
        m.db = jo.containsKey("db") ? jo.getString("db") : "default";
//        m.listenerOpen = jo.containsKey("listenerOpen") ? jo.getBoolean("listenerOpen") : false;

        // update by suxinjie 20171125 为了提高发版速度,这里根据main.market来设置listenerOpen状态
        m.listenerOpen = false;
        Set<String> listenMarke = GlobalConfig.listenMarket;
        if (listenMarke.contains(m.market.trim())) {
            m.listenerOpen = true;
        }

        m.numberBiFundsType = jo.containsKey("numberBiFundsType") ? Integer.parseInt(jo.getString("numberBiFundsType")) : 0;
        m.exchangeBiFundsType = jo.containsKey("exchangeBiFundsType") ? Integer.parseInt(jo.getString("exchangeBiFundsType")) : 0;
        m.minAmount = jo.containsKey("minAmount") ? Double.parseDouble(jo.getString("minAmount")) : 0;

        m.numberBiFullName = jo.containsKey("numberBiFullName") ? jo.getString("numberBiFullName") : "";
        m.bixMinNum = jo.containsKey("bixMinNum") ? Double.parseDouble(jo.getString("bixMinNum")) : 1;
        /*start by kinghao 20181121  单笔最大交易限额*/
        m.bixMaxNum = jo.containsKey("bixMaxNum") ? Double.parseDouble(jo.getString("bixMaxNum")) : 100;
        /*end*/

        m.takerFeeRate = jo.containsKey("takerFeeRate") ? Double.parseDouble(jo.getString("takerFeeRate")) : 0;
        m.makerFeeRate = jo.containsKey("makerFeeRate") ? Double.parseDouble(jo.getString("makerFeeRate")) : 0;

        return m;
    }


    public static Market getMarkeByName(String name) {

        Market market = markets.get(name);
        //如果这个市场真不存在就会出现死循环
//        if(null == market){
//            log.info("没有获取到市场"+name+"信息");
//            market = getMarkeByName(name);
//        }
        return market;
    }


    public static JSONObject getAllMarkets() {
        JSONObject json = new JSONObject();
        Map<String, Market> marketsMap = Market.markets;
        if (marketsMap != null && !marketsMap.isEmpty()) {
            Iterator<Entry<String, Market>> iter = marketsMap.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, Market> entry = iter.next();
                String key = entry.getKey();
                Market market = entry.getValue();
                JSONObject o = new JSONObject();
                o.put("numberBi", market.numberBi);
                o.put("numberBiEn", market.numberBiEn);
                o.put("numberBiNote", market.numberBiNote);
                o.put("numberBixNormal", market.numberBixNormal);
                o.put("numberBixShow", market.numberBixShow);
                o.put("numberBixDian", market.numberBixDian);
                o.put("exchangeBi", market.exchangeBi);
                o.put("exchangeBiEn", market.exchangeBiEn);
                o.put("exchangeBiNote", market.exchangeBiNote);
                o.put("market", market.market);
                o.put("exchangeBixNormal", market.exchangeBixNormal);
                o.put("exchangeBixShow", market.exchangeBixShow);
                o.put("exchangeBixDian", market.exchangeBixDian);
                o.put("entrustUrlBase", market.entrustUrlBase);
                o.put("feeRate", market.feeRate);
                o.put("serNum", market.serNum);
                o.put("db", market.db);
                o.put("ip", "127.0.0.1");
                o.put("mergePrice", market.mergePrice);
                o.put("maxPrice", market.maxPrice);
                o.put("port", "8080");
                o.put("numberBiFundsType", market.numberBiFundsType);
                o.put("exchangeBiFundsType", market.exchangeBiFundsType);
                o.put("minAmount", market.minAmount);
                o.put("bixMinNum", market.bixMinNum);
                o.put("bixMaxNum", market.bixMaxNum);
                o.put("numberBiFullName", market.numberBiFullName);
                o.put("listenerOpen", market.listenerOpen);
                o.put("takerFeeRate", market.takerFeeRate);
                o.put("makerFeeRate", market.makerFeeRate);
                json.put(key, o);
            }
        }
        return json;
    }

    /**
     * 获取一个市场
     *
     * @return
     */
    public static synchronized Market getMarket(String name) {
        try {
            Market m = markets.get(name);
            if (m != null) {
                return m;
            }

            JSONArray jas = (JSONArray) JsonConfig.getValue("markets");
            if (jas != null && jas.size() > 0) {
                for (Object o : jas) {
                    JSONObject jo = (JSONObject) o;
                    m = fillData(jo);
                    if (StringUtils.isNotBlank(m.numberBiEn) && StringUtils.isNotBlank(m.market)) {
                        //log.info(m.toString());
                        markets.put(m.market, m);
                    }

                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return markets.get(name);
    }

    /**
     * 返回配置文件配置的币种信息
     * Map<Integer,String[]> key:fundsType 币种类型 ; String[] {币种英文：BTC ，标示代号：฿ }
     *
     * @return
     */
    public static Map<Integer, String[]> getCoinMap() {
        Map<Integer, String[]> coinMap = new HashMap<Integer, String[]>();
        Iterator<Entry<String, Market>> iter = Market.markets.entrySet().iterator();
        while (iter.hasNext()) {
            Market m = iter.next().getValue();
            String[] coin = new String[]{m.numberBi, m.numberBiNote};
            coinMap.put(m.numberBiFundsType, coin);

            coin = new String[]{m.exchangeBi, m.exchangeBiNote};
            coinMap.put(m.exchangeBiFundsType, coin);
        }

        return coinMap;
    }

    /**
     * 返回配置文件配置的币种信息
     * @return
     */
    public static List<Map<String,Object>> getBixDian() {
        List<Map<String,Object>> list = new ArrayList<>();
        Iterator<Entry<String, Market>> iter = Market.markets.entrySet().iterator();
        while (iter.hasNext()) {
            Market m = iter.next().getValue();
            Map<String,Object> map = new HashMap<>();
            map.put("market",m.getMarket());
            map.put("numberBixDian",m.getNumberBixDian());
            map.put("exchangeBixDian",m.getExchangeBixDian());
            map.put("mergePrice",m.getMergePrice());
            list.add(map);
        }

        return list;
    }

    //格式化金钱 3.456 变成3.45
    public static double formatMoney(BigDecimal num, Market m) {
        // double numNew=Arith.div(num, m.exchangeBixNormal,m.exchangeBixDian);

        double numNew = num.setScale(m.exchangeBixDian, BigDecimal.ROUND_DOWN).doubleValue();
        return numNew;
    }

    //双重格式化，保留应有的小数点位数，避免麻烦
    public static double formatNumber(BigDecimal num, Market m) {
        //double numNew=Arith.div(num, m.numberBixNormal,m.numberBixDian);
        double numNew = num.setScale(m.numberBixDian, BigDecimal.ROUND_DOWN).doubleValue();
        return numNew;
    }

    //格式化金钱 3.456 变成3.45
    public static BigDecimal formatMoneyToBigDecimal(BigDecimal num, Market m) {
        BigDecimal numNew = num.setScale(m.exchangeBixDian, BigDecimal.ROUND_DOWN);
        return numNew;
    }

    //双重格式化，保留应有的小数点位数，避免麻烦
    public static BigDecimal formatNumberToBigDecimal(BigDecimal num, Market m) {
        BigDecimal numNew = num.setScale(m.numberBixDian, BigDecimal.ROUND_DOWN);
        return numNew;
    }

    //金额与数量的小数点位数和
    public static double formatMoneyAndNumber(BigDecimal num, Market m) {
        // double numNew=Arith.div(num, m.exchangeBixNormal*m.numberBixNormal,m.exchangeBixDian+m.numberBixDian);

        double numNew = num.setScale(m.exchangeBixDian + m.numberBixDian, BigDecimal.ROUND_DOWN).doubleValue();
        return numNew;
    }

    //计算单价*数量的总金额
    public static BigDecimal formatTotalMoney(BigDecimal unitPrice, BigDecimal numbers) {
        return unitPrice.multiply(numbers);
    }

    public static BigDecimal totalMoney(BigDecimal unitPrice, BigDecimal numbers) {
        return unitPrice.multiply(numbers).setScale(9, BigDecimal.ROUND_UP);
    }

    public static BigDecimal totalMoneyDown(BigDecimal unitPrice, BigDecimal numbers) {
        return unitPrice.multiply(numbers).setScale(9, BigDecimal.ROUND_DOWN);
    }

    //格式化商品,用于显示,将基础整数位的商品格式化成需要的显示
    public static BigDecimal ffNumber(BigDecimal num, Market m) {
        // double numNew=Arith.div(num, m.numberBixNormal,m.numberBixDian);
        // numNew=Arith.mul(numNew, m.numberBixNormal);
        BigDecimal numNew = num.setScale(m.numberBixDian, BigDecimal.ROUND_UP);
        return numNew;
    }

    //比如给出2345554.返回2345000
    public static BigDecimal ffMoney(BigDecimal num, Market m) {
        BigDecimal numNew = num.setScale(m.exchangeBixDian, BigDecimal.ROUND_UP);
        return numNew;
    }

    public static double formatTotalMoneyDoule(BigDecimal unitPrice, BigDecimal numbers, Market m) {
        double unitP = formatMoney(unitPrice, m);

        double numb = formatNumber(numbers, m);

        double t = Arith.mul(unitP, numb);

        t = Arith.round(t, m.exchangeBixDian + m.numberBixDian);
        return t;
    }

    //反向格式化商品，用来将部分double类型的传值转化成BigDecimal
    public static BigDecimal formatMoneyLong(double num, Market m) {

        return BigDecimal.valueOf(num).divide(BigDecimal.ONE, m.exchangeBixDian, BigDecimal.ROUND_DOWN);

    }

    //反向格式化商品，用来将部分double类型的传值转化成long类型进行委托
    public static BigDecimal formatNumberLong(double num, Market m) {
        return BigDecimal.valueOf(num).divide(BigDecimal.ONE, m.numberBixDian, BigDecimal.ROUND_DOWN);
    }


    public String getNumberBi() {
        return numberBi;
    }


    public void setNumberBi(String numberBi) {
        this.numberBi = numberBi;
    }


    public String getNumberBiEn() {
        return numberBiEn;
    }


    public void setNumberBiEn(String numberBiEn) {
        this.numberBiEn = numberBiEn;
    }


    public String getNumberBiNote() {
        return numberBiNote;
    }


    public void setNumberBiNote(String numberBiNote) {
        this.numberBiNote = numberBiNote;
    }


    public long getNumberBixNormal() {
        return numberBixNormal;
    }


    public void setNumberBixNormal(long numberBixNormal) {
        this.numberBixNormal = numberBixNormal;
    }


    public long getNumberBixShow() {
        return numberBixShow;
    }


    public void setNumberBixShow(long numberBixShow) {
        this.numberBixShow = numberBixShow;
    }


    public int getNumberBixDian() {
        return numberBixDian;
    }


    public void setNumberBixDian(int numberBixDian) {
        this.numberBixDian = numberBixDian;
    }


    public String getExchangeBi() {
        return exchangeBi;
    }


    public void setExchangeBi(String exchangeBi) {
        this.exchangeBi = exchangeBi;
    }


    public String getExchangeBiEn() {
        return exchangeBiEn;
    }


    public void setExchangeBiEn(String exchangeBiEn) {
        this.exchangeBiEn = exchangeBiEn;
    }


    public String getExchangeBiNote() {
        return exchangeBiNote;
    }


    public void setExchangeBiNote(String exchangeBiNote) {
        this.exchangeBiNote = exchangeBiNote;
    }


    public String getMarket() {
        return market;
    }


    public void setMarket(String market) {
        this.market = market;
    }


    public long getExchangeBixNormal() {
        return exchangeBixNormal;
    }


    public void setExchangeBixNormal(long exchangeBixNormal) {
        this.exchangeBixNormal = exchangeBixNormal;
    }


    public long getExchangeBixShow() {
        return exchangeBixShow;
    }


    public void setExchangeBixShow(long exchangeBixShow) {
        this.exchangeBixShow = exchangeBixShow;
    }


    public int getExchangeBixDian() {
        return exchangeBixDian;
    }


    public void setExchangeBixDian(int exchangeBixDian) {
        this.exchangeBixDian = exchangeBixDian;
    }


    public String getEntrustUrlBase() {
        return entrustUrlBase;
    }


    public void setEntrustUrlBase(String entrustUrlBase) {
        this.entrustUrlBase = entrustUrlBase;
    }


    public int getWebId() {
        return webId;
    }


    public void setWebId(int webId) {
        this.webId = webId;
    }


    public String getIp() {
        return ip;
    }


    public void setIp(String ip) {
        this.ip = ip;
    }


    public int getPort() {
        return port;
    }


    public void setPort(int port) {
        this.port = port;
    }


    public String getHashSec() {
        return hashSec;
    }


    public void setHashSec(String hashSec) {
        this.hashSec = hashSec;
    }


    public int getSerNum() {
        return serNum;
    }


    public void setSerNum(int serNum) {
        this.serNum = serNum;
    }


    public double getFeeRate() {
        return feeRate;
    }


    public void setFeeRate(double feeRate) {
        this.feeRate = feeRate;
    }


    public int getMaxPrice() {
        return maxPrice;
    }


    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }


    public boolean isListenerOpen() {
        return listenerOpen;
    }


    public void setListenerOpen(boolean listenerOpen) {
        this.listenerOpen = listenerOpen;
    }


    public int getNumberBiFundsType() {
        return numberBiFundsType;
    }


    public void setNumberBiFundsType(int numberBiFundsType) {
        this.numberBiFundsType = numberBiFundsType;
    }


    public int getExchangeBiFundsType() {
        return exchangeBiFundsType;
    }


    public void setExchangeBiFundsType(int exchangeBiFundsType) {
        this.exchangeBiFundsType = exchangeBiFundsType;
    }


    public String getMergePrice() {
        return mergePrice;
    }


    public void setMergePrice(String mergePrice) {
        this.mergePrice = mergePrice;
    }

    public double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(double minAmount) {
        this.minAmount = minAmount;
    }

    public String getNumberBiFullName() {
        return numberBiFullName;
    }

    public void setNumberBiFullName(String numberBiFullName) {
        this.numberBiFullName = numberBiFullName;
    }

    public double getTakerFeeRate() {
        return takerFeeRate;
    }

    public void setTakerFeeRate(double takerFeeRate) {
        this.takerFeeRate = takerFeeRate;
    }

    public double getMakerFeeRate() {
        return makerFeeRate;
    }

    public void setMakerFeeRate(double makerFeeRate) {
        this.makerFeeRate = makerFeeRate;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public double getBixMinNum() {
        return bixMinNum;
    }

    public void setBixMinNum(double bixMinNum) {
        this.bixMinNum = bixMinNum;
    }

    public double getBixMaxNum() {
        return bixMaxNum;
    }

    public void setBixMaxNum(double bixMaxNum) {
        this.bixMaxNum = bixMaxNum;
    }
}

package com.world.model.daos.chart;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.match.entrust.MemEntrustDataProcessor;
import com.match.entrust.MemEntrustMatchProcessor;
import com.tenstar.timer.chart.ChartData;
import com.tenstar.timer.chart.ChartDataBean;
import com.tenstar.timer.chart.ChartDataPacketBean;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.model.entitys.record.TransRecord;
import com.world.util.WebUtil;
import com.world.util.date.TimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ChartManager {
    private final static Logger log = Logger.getLogger(ChartManager.class);

    ///不同时间间隔下的K线数据
    //private static Map<Integer, TreeMap<Long, ChartDataBean>> minuteFreqChartDatas = new HashMap<Integer, TreeMap<Long, ChartDataBean>>();//最近x天 1440

    //private static Map<Integer, TreeMap<Long, ChartDataBean>> hourFreqChartDatas = new HashMap<Integer, TreeMap<Long, ChartDataBean>>();//最近30天 24 * 30

    //private static Map<Integer, TreeMap<Long, ChartDataBean>> dayFreqChartDatas = new HashMap<Integer, TreeMap<Long, ChartDataBean>>();//最近两年365 * 2

    private static Map<String,Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>> marketsMinuteFreqChartDatas = new HashMap<String,Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>>();
    private static Map<String,Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>> marketsHourFreqChartDatas = new HashMap<String,Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>>();
    private static Map<String,Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>> marketsDayFreqChartDatas = new HashMap<String,Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>>();

    //不同时间K线锁
    private static final int MAX_RECORDS_NUM = 3000;
    private static final int PAGE_SIZE = 3000;

    // FIXME 20170305 suxinjie K-Line支持的步长设置硬编码.
    //private static TreeMap<Long, JSONObject> lastTransRecords = new TreeMap<Long, JSONObject>();//最后3000条成交记录
    private static Map<String,ConcurrentSkipListMap<Long, JSONObject>> marketsLastTransRecords = new HashMap<String,ConcurrentSkipListMap<Long, JSONObject>>();
    private static Integer[] minuteFreqs = new Integer[]{1, 3, 5, 15, 30};//K线分钟间隔值
    private static Integer[] hourFreqs = new Integer[]{1, 2, 4, 6, 12};//K线小时间隔值
    private static Integer[] dayFreqs = new Integer[]{1, 3, 5, 7, 30};//K线天间隔值

    private static Integer[][] mhdFreqs = new Integer[][]{minuteFreqs, hourFreqs, dayFreqs};

    private static int[] types = new int[]{1, 2, 3};//1 分 2时 3天

    private static long[] intervals = new long[]{60 * 1000, 60 * 60 * 1000, 24 * 60 * 60 * 1000};//分、时、天 间隔毫秒数
    private final static long BASE_TIME = Timestamp.valueOf("2000-01-01 00:00:00").getTime();

    private static Long addingMinute;//正在添加中的分钟数

    private static Map<String, Double> last24RiseRate = new HashMap<>();//24小时涨跌幅

    //未保存到数据库做统计的分钟交易
    private static TreeMap<Long, List<TransRecord>> noSaveToMysqlsData = new TreeMap<Long, List<TransRecord>>();

    private static Map<String,StatisticsFor24Hour> statisticsFor24HourMap = new HashMap<String,StatisticsFor24Hour>();
    public static Map<String,BigDecimal> lpriceMap = new HashMap<String,BigDecimal>();//保存各币种的最后价格
    public static Map<String,Boolean> lisBuyMap = new HashMap<String,Boolean>();//保存各币种的最后买卖方向true为买
    public static Map<String,ConcurrentSkipListMap<Long,JSONObject>> lastTradeMap = new HashMap<String,ConcurrentSkipListMap<Long,JSONObject>>();//保存各币种的最后50条成交记录

    /**
     * 获取系统支持的K-Line步长
     * @return
     */
    public static Set<String> getSupportedResolutions() {
        Set<String> supportedResolutions = new LinkedHashSet<>();
        for(int i=0; i<mhdFreqs.length; i++) {
            for(int j=0; j<mhdFreqs[i].length; j++) {
                if (i == 0) {
                    supportedResolutions.add(mhdFreqs[i][j] + "min");
                }
                if (i == 1) {
                    supportedResolutions.add(mhdFreqs[i][j] + "hour");
                }
                if (i == 2) {
                    supportedResolutions.add(mhdFreqs[i][j] + "day");
                }
            }
        }
        return supportedResolutions;
    }

    /***
     * 获取最新成交价
     * @return
     */
    public static BigDecimal getPrice(Market m){
        if(lpriceMap.containsKey(m.market)){
            return lpriceMap.get(m.market);
        }
        return BigDecimal.ZERO;
    }

    public static boolean lisBuy(Market m){
        if(lisBuyMap.containsKey(m.market)){
            return lisBuyMap.get(m.market);
        }
        return false;
    }

    public static Double getLast24RiseRate(Market m){
        if(last24RiseRate.containsKey(m.market)){
            return last24RiseRate.get(m.market);
        }
        return 0d;
    }

    public static void setLast24RiseRate(Market m, double riseRate){
        last24RiseRate.put(m.market, riseRate);
    }

    public static BigDecimal[] getLbuyOneAndSellOne(Market m){
        BigDecimal[] buysell = MemEntrustDataProcessor.getBuyOneAndSellOne(m);
        if(buysell != null && buysell[0].compareTo(BigDecimal.ZERO) > 0 && buysell[1].compareTo(BigDecimal.ZERO) > 0){
            return buysell;
        }
        return null;
    }

    /***
     * 获取买一卖一
     * @return
     */
    public static double[] getBuyOneAndSellOne(Market m){
        BigDecimal[] buysell =  MemEntrustDataProcessor.getBuyOneAndSellOne(m);
        if(buysell != null && buysell[0].compareTo(BigDecimal.ZERO)> 0 && buysell[1].compareTo(BigDecimal.ZERO) > 0){
            return new double[]{Market.formatMoney(buysell[0],m), Market.formatMoney(buysell[1],m)};
        }
        return null;
    }

	/*private static Ticker ticker = null;

	public static Ticker getTicker(){
		if(ticker == null){
			if(statisticsFor24Hour == null){
				init24Hour();
			}
			ticker = new Ticker();
			ticker.max = statisticsFor24Hour.getHigh();
			ticker.min = statisticsFor24Hour.getLow();
			ticker.lastPrice = statisticsFor24Hour.getClose();
		}

		return ticker;
	}*/

    public static StatisticsFor24Hour get24HourInfo(Market m){
        return statisticsFor24HourMap.get(m.market);
    }


    public static Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>> getFreqChartDatas(int type,Market m){
        StatisticsFor24Hour statisticsFor24Hour = statisticsFor24HourMap.get(m.market);
        if(statisticsFor24Hour == null){
            init24Hour(m);
        }
        Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>> minuteFreqChartDatas = marketsMinuteFreqChartDatas.get(m.market);
        if(minuteFreqChartDatas==null){
            minuteFreqChartDatas = new HashMap<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>();
            marketsMinuteFreqChartDatas.put(m.market, minuteFreqChartDatas);
        }

        Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>> hourFreqChartDatas = marketsHourFreqChartDatas.get(m.market);
        if(hourFreqChartDatas==null){
            hourFreqChartDatas = new HashMap<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>();
            marketsHourFreqChartDatas.put(m.market, hourFreqChartDatas);
        }

        Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>> dayFreqChartDatas = marketsDayFreqChartDatas.get(m.market);
        if(dayFreqChartDatas==null){
            dayFreqChartDatas = new HashMap<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>();
            marketsDayFreqChartDatas.put(m.market, dayFreqChartDatas);
        }
        if(type == 1){
            return minuteFreqChartDatas;
        }else if(type == 2){
            return hourFreqChartDatas;
        }else if(type == 3){
            return  dayFreqChartDatas;
        }else{
            return null;
        }
    }



    /***
     * 缓存分、时、天k线数据
     */
    public static void cacheKline(Market m){

        if(MemEntrustMatchProcessor.da== null){
            return;
        }

        try {
            for(int type : types){
                Integer[] freqs = mhdFreqs[type - 1];
                long interval = intervals[type - 1] / 1000;

                for(int freq : freqs){
                    String rtn = getJson(type, freq, m);
//					Cache.Set(m.market + "_getchar" + freq * interval, rtn, 2*60);

                    DishDataCacheService.setKline(m.market, String.valueOf(freq * interval), rtn, 2 * 60);
                }
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }


    /***
     * 返回对应类型的k线数据   最多返回1000条数据
     * @param type  1 分 2 时 3 天
     * @param inteval 间隔时间 如 5分 10分 15分
     * @return
     */
    public static String getJson(int type, int inteval,Market m){
        Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>> freqChartData = getFreqChartDatas(type,m);

        ConcurrentSkipListMap<Long, ChartDataBean> chartData = freqChartData.get(inteval);

        if(chartData == null || chartData.size() <= 0){
            return "[]";
        }
        StringBuilder sbl = new StringBuilder();

        //int cursor = 0;

        for(Entry<Long, ChartDataBean> entry : chartData.entrySet()){
            //			if(cursor >= 1000){
            //				break;
            //			}
            ChartDataBean td = entry.getValue();
            if(td.getTotalNumber().compareTo(BigDecimal.ZERO)>0){
                td.setMiddle(td.getTotalMoney().divide(td.getTotalNumber(),m.exchangeBixDian,RoundingMode.CEILING));
                //[1457845800,0.0,2720.0,0.0,2720.0,1.53]
                sbl.append(",["+td.getTimes()/1000+","+Market.formatMoney(td.getOpen(),m)+","
                        +Market.formatMoney(td.getHigh(),m)+","+Market.formatMoney(td.getLow(),m)+","+Market.formatMoney(td.getClose(),m)+","
                        +Market.formatNumber(td.getTotalNumber(),m)+"]");

                //cursor++;
            }
        }
        if(sbl.toString().length() > 0){
            return sbl.toString().substring(1);
        }else{
            return "[]";
        }
    }

    /**
     * 统计24小时之内的图表数据
     */
    private static synchronized void init24Hour(Market m){
        StatisticsFor24Hour statisticsFor24Hour = statisticsFor24HourMap.get(m.market);
        if(statisticsFor24Hour == null){
            for(int type : types){
                initByType(type,m);
            }

            TreeMap<Long, ChartDataBean> last1440Minutes = new TreeMap<Long, ChartDataBean>();
            long startTimes = TimeUtil.getAfterDayDate(TimeUtil.getMinuteFirst(), -1).getTime();
            List<Bean> lists = Data.Query(m.db,"select * from chartdata where type=? and times>=? order by times", new Object[]{1 , startTimes}, ChartDataBean.class);
            for(Bean b : lists){
                ChartDataBean cd = (ChartDataBean) b;
                last1440Minutes.put(cd.getTimes(), cd);
            }

            List<TransRecord> transrecords = Data.QueryT(m.db,"SELECT transRecordId,unitPrice,numbers,TYPES,times,userIdBuy FROM transrecord ORDER BY transRecordId DESC LIMIT 0,?",
                    new Object[]{MAX_RECORDS_NUM}, TransRecord.class);

            //数据有可能被迁移了，所以需要获取all表数据做整合
            List<TransRecord> transrecordsAll = Data.QueryT(m.db,"SELECT transRecordId,unitPrice,numbers,TYPES,times,userIdBuy FROM transrecord_all ORDER BY transRecordId DESC LIMIT 0,?",
                    new Object[]{MAX_RECORDS_NUM}, TransRecord.class);
            if (CollectionUtils.isNotEmpty(transrecordsAll)) {
                transrecords.addAll(transrecordsAll);
            }
            //按照transrecordId排序
            transrecords.sort((arg0, arg1) -> (int) (arg1.getTransRecordId() - arg0.getTransRecordId()));
            //截取 MAX_RECORDS_NUM 数量
            transrecords = transrecords.subList(0, transrecords.size() > MAX_RECORDS_NUM ? MAX_RECORDS_NUM : transrecords.size());

            int ix = 0;
            //初始化成交记录 并初始化价格
            for(Bean b : transrecords){
                TransRecord tr = (TransRecord) b;
                addTransRecordToRecords(tr,m);
                setLastTrade50(tr, m);
                if(ix == 0){//初始化最新成交价
                    lpriceMap.put(m.market, tr.getUnitPrice());
                    lisBuyMap.put(m.market, tr.getTypes() == 1);
                }
                ix++;
            }

            statisticsFor24Hour = new StatisticsFor24Hour(last1440Minutes);

            //获取历史今天之前有成交的最后一天记录
            long todayStartTime = TimeUtil.getTodayFirst().getTime();
            List<Bean> lastDealDayCharts = Data.Query(m.db,"select * from chartdata where type=? and times<? order by times desc limit 1", new Object[]{3 , todayStartTime}, ChartDataBean.class);
            if(CollectionUtils.isNotEmpty(lastDealDayCharts)){
                ChartDataBean cd = (ChartDataBean)lastDealDayCharts.get(0);
                statisticsFor24Hour.setLastDealDayChart(cd);
            }

            statisticsFor24HourMap.put(m.market, statisticsFor24Hour);
        }

        //lastTradeMap.put(m.getMarket(), getSince(0, 50, m));
    }

    private static void addTransRecordToRecords(TransRecord tr,Market m){
        ConcurrentSkipListMap<Long, JSONObject> lastTransRecords = marketsLastTransRecords.get(m.market);
        if(lastTransRecords==null){
            lastTransRecords = new ConcurrentSkipListMap<Long, JSONObject>();//最后3000条成交记录
            marketsLastTransRecords.put(m.market, lastTransRecords);
        }
        lastTransRecords.put(tr.getTransRecordId(), getObjFromTransRecord(tr,m));

        if(lastTransRecords.size() > MAX_RECORDS_NUM){
            lastTransRecords.pollFirstEntry();
        }
    }

    private static JSONObject getObjFromTransRecord(TransRecord tr,Market m){
        JSONObject jo = new JSONObject();
        jo.put("date", tr.getTimes() / 1000);
        jo.put("dateTimes", tr.getTimes());
        jo.put("price", Market.formatMoney(tr.getUnitPrice(),m));
        jo.put("amount", Market.formatNumber(tr.getNumbers(),m));
        jo.put("tid", tr.getTransRecordId());
        jo.put("type", tr.getTypes() == 1 ? "buy" : "sell");
        jo.put("trade_type", tr.getTypes() == 1 ? "bid" : "ask");
//        jo.put("userIdBuy", tr.getUserIdBuy());
        return jo;
    }


    /***
     * 内存初始化
     *
     * 分钟的保存最近7日数据    1440 * 7 = 10080
     * 小时保存最近30天数据     24 * 30 = 720
     * 天保存最近730天数据      1 * 730 = 730
     *
     * @param type
     * @param m
     */
    private static void initByType(int type,Market m){
        Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>> minuteFreqChartDatas = marketsMinuteFreqChartDatas.get(m.market);
        if(minuteFreqChartDatas==null){
            minuteFreqChartDatas = new HashMap<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>();
            marketsMinuteFreqChartDatas.put(m.market, minuteFreqChartDatas);
        }

        Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>> hourFreqChartDatas = marketsHourFreqChartDatas.get(m.market);
        if(hourFreqChartDatas==null){
            hourFreqChartDatas = new HashMap<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>();
            marketsHourFreqChartDatas.put(m.market, hourFreqChartDatas);
        }

        Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>> dayFreqChartDatas = marketsDayFreqChartDatas.get(m.market);
        if(dayFreqChartDatas==null){
            dayFreqChartDatas = new HashMap<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>();
            marketsDayFreqChartDatas.put(m.market, dayFreqChartDatas);
        }


        long startTimes = 0;
        Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>> freqChartDatas = null;
        Integer[] freqs = null;
        if(type == 1){
            startTimes = TimeUtil.getAfterDayDate(TimeUtil.getMinuteFirst(), -7).getTime();
            freqChartDatas = minuteFreqChartDatas;
            freqs = minuteFreqs;
        }else if(type == 2){
            startTimes = TimeUtil.getAfterDayDate(TimeUtil.getHourFirst(), -30).getTime();
            freqChartDatas = hourFreqChartDatas;
            freqs = hourFreqs;
        }else if(type == 3){
            startTimes = TimeUtil.getAfterDayDate(TimeUtil.getTodayFirst(), -730).getTime();
            freqChartDatas = dayFreqChartDatas;
            freqs = dayFreqs;
        }else{
            return;
        }

        List<Bean> mlists = Data.Query(m.db,"select * from chartdata where type=? and times>=? order by times", new Object[]{type , startTimes}, ChartDataBean.class);

        for(Bean b : mlists){
            ChartDataBean scdb = (ChartDataBean) b;
            ////为不同点位的时间初始化数据
            for(int frq : freqs){
                ChartDataBean cdb = (ChartDataBean) WebUtil.deepCopyObj(scdb);

                long dianwei = getPointTime(cdb.getTimes(), type, frq);

                ConcurrentSkipListMap<Long, ChartDataBean> minuteChartDatas = freqChartDatas.get(frq);

                if(minuteChartDatas == null){
                    minuteChartDatas = new ConcurrentSkipListMap<Long, ChartDataBean>();
                    freqChartDatas.put(frq, minuteChartDatas);
                }

                ChartDataBean current = minuteChartDatas.get(dianwei);
                if(current == null){//当前点位无数据
                    cdb.setTimes(dianwei);
                    minuteChartDatas.put(dianwei, cdb);
                }else{
                    if(cdb.getHigh().compareTo(current.getHigh())>0){
                        current.setHigh(cdb.getHigh());//最高
                    }

                    if(cdb.getLow().compareTo(current.getLow()) < 0){
                        current.setLow(cdb.getLow());//最高
                    }
                    current.setTotalMoney(current.getTotalMoney().add(cdb.getTotalMoney()));
                    current.setTotalNumber(current.getTotalNumber().add(cdb.getTotalNumber()));
                    current.setMiddle(current.getTotalMoney().divide(current.getTotalNumber(),m.exchangeBixDian,RoundingMode.CEILING));
                    current.setClose(cdb.getClose());
                }
            }
        }

    }

    /**
     * 获取Kline 点位时间
     * @param timeMinute 时间(整分钟）
     * @param type Kline 类型
     * @param frq Kline 序号
     * @return
     */
    private static long getPointTime(long timeMinute, int type, int frq) {
        long truncateTime;
        if (type == 3) {
            if (frq == 7) {
                // 一周
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(new Date(timeMinute));
                cal.setFirstDayOfWeek(GregorianCalendar.MONDAY); // 设置一个星期的第一天为星期1，默认是星期日
                cal.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal.getTime().getTime();
            }

            if (frq == 30) {
                // 一个月
                return DateUtils.truncate(new Date(timeMinute), Calendar.MONTH).getTime();
            }
        }
        // 其他情况按照时间间隔计算
        long cha = (timeMinute - BASE_TIME) % (frq * intervals[type - 1]);
        return timeMinute - cha;
    }

    /***
     * 添加最新成交记录到内存
     * @param tr
     */
    public static void addNewTransRecord(TransRecord tr,Market m){
        StatisticsFor24Hour statisticsFor24Hour = statisticsFor24HourMap.get(m.market);
        if(statisticsFor24Hour == null){
            init24Hour(m);
        }

        lpriceMap.put(m.market, tr.getUnitPrice());
        lisBuyMap.put(m.market, tr.getTypes() == 1);

        setLastTrade50(tr, m);

        for(int type : types){
            resetData(tr, type, m);
        }

        addTransRecordToRecords(tr,m);
    }

    /**
     * 更新内存中的最新价格
     *
     * @param tr
     * @param m
     */
    public static void setNewPrice(TransRecord tr,Market m){
        lpriceMap.put(m.market, tr.getUnitPrice());
        lisBuyMap.put(m.market, tr.getTypes() == 1);
    }

    /**
     * 设置最新成交的50条交易记录
     * @param tr 交易记录对象
     * @param m 市场
     * @author zhanglinbo 20170117
     */
    public static void setLastTrade50(TransRecord tr,Market m){
        ConcurrentSkipListMap<Long,JSONObject>  lastTradeMap50 = lastTradeMap.get(m.getMarket());
        if(lastTradeMap50==null){
            lastTradeMap50 = new ConcurrentSkipListMap<Long,JSONObject>();
            lastTradeMap.put(m.getMarket(), lastTradeMap50);
        }
        //添加最新的记录到最后50条记录里面
        lastTradeMap50.put(tr.getTransRecordId(), getObjFromTransRecord(tr,m));

        if(lastTradeMap50.size() > 60){
            lastTradeMap50.pollFirstEntry();
        }

    }

    /**
     * 获取最新成交的50条记录 替换getSince 方法， 该方法有多线程不安全，会报错
     * @param m 市场对象
     * @return JSONArray
     * @author zhanglinbo
     */
    public static JSONArray getLastTrade50(Market m){
//        JSONArray jas = new JSONArray();
        ConcurrentSkipListMap<Long,JSONObject>  lastTradeMap50 = lastTradeMap.get(m.getMarket());
        if(lastTradeMap50==null){
            lastTradeMap50 = new ConcurrentSkipListMap<Long,JSONObject>();
            lastTradeMap.put(m.getMarket(), lastTradeMap50);
        }

        Set<Long> lastKeys = lastTradeMap50.descendingKeySet();
        int i = 0;
        JSONArray needjas = new JSONArray();
        for(Long key : lastKeys){
            JSONObject jo = lastTradeMap50.get(key);
            if (null != jo) {
                needjas.add(jo);
            }
            i++;
            //最新需求，成交记录显示60条，web端和app一致
            if(i >= 60){
                break;
            }
        }

//        for(int j = needjas.size() - 1; j >= 0; j--){
//            JSONObject jos = needjas.getJSONObject(j);
//            jas.add(jos);
//        }

        return needjas;
    }

    private static void reset24Minute(TransRecord tr){

    }

    public static JSONArray getSince(long since,Market m){
        return getSince(since, PAGE_SIZE,m);
    }

    /***
     * 返回since后的成交记录
     * @param since
     * @return
     */
    public static JSONArray getSince(long since, int pageSize,Market m){
        JSONArray jas = new JSONArray();

        ConcurrentSkipListMap<Long, JSONObject> lastTransRecords = marketsLastTransRecords.get(m.market);
        if(lastTransRecords==null){
            lastTransRecords = new ConcurrentSkipListMap<Long, JSONObject>();//最后3000条成交记录
            marketsLastTransRecords.put(m.market, lastTransRecords);
        }

        try {
            if(since <= 0){//返回最后50条数据
                Set<Long> lastKeys = lastTransRecords.descendingKeySet();
                int i = 0;
                JSONArray needjas = new JSONArray();
                for(Long key : lastKeys){
                    JSONObject jo = lastTransRecords.get(key);
                    needjas.add(jo);
                    i++;
                    if(i >= pageSize){
                        break;
                    }
                }
                for(int j = needjas.size() - 1; j >= 0; j--){
                    JSONObject jos = needjas.getJSONObject(j);
                    jas.add(jos);
                }

            }else{
                JSONObject sinceObj = lastTransRecords.get(since);

                if(sinceObj == null){
                    List<Bean> transrecords = Data.Query(m.db,"SELECT transRecordId,unitPrice,numbers,types,times,userIdBuy FROM transrecord where unitPrice>0 and transRecordId>? ORDER BY transRecordId LIMIT 0,?",
                            new Object[]{since, pageSize}, TransRecord.class);
                    for(Bean b : transrecords){
                        TransRecord tr = (TransRecord) b;
                        jas.add(getObjFromTransRecord(tr,m));
                    }
                }else{
                    int i = 0;
                    NavigableMap<Long, JSONObject> tails = lastTransRecords.tailMap(since, false);
                    for(Entry<Long, JSONObject> entry : tails.entrySet()){
                        JSONObject jo = entry.getValue();
                        jas.add(jo);
                        i++;
                        if(i >= pageSize){
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        return jas;
    }

    private static void resetData(TransRecord tr, int type,Market m){
        Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>> minuteFreqChartDatas = marketsMinuteFreqChartDatas.get(m.market);
        if(minuteFreqChartDatas==null){
            minuteFreqChartDatas = new HashMap<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>();
            marketsMinuteFreqChartDatas.put(m.market, minuteFreqChartDatas);
        }

        Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>> hourFreqChartDatas = marketsHourFreqChartDatas.get(m.market);
        if(hourFreqChartDatas==null){
            hourFreqChartDatas = new HashMap<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>();
            marketsHourFreqChartDatas.put(m.market, hourFreqChartDatas);
        }

        Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>> dayFreqChartDatas = marketsDayFreqChartDatas.get(m.market);
        if(dayFreqChartDatas==null){
            dayFreqChartDatas = new HashMap<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>();
            marketsDayFreqChartDatas.put(m.market, dayFreqChartDatas);
        }

        Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>> frechartDatas = null;
        long times = tr.getTimeMinute();
        Integer[] freqs = null;

        long deleteTimes = 0;
        if(type == 1){
            frechartDatas = minuteFreqChartDatas;
            freqs = minuteFreqs;
            deleteTimes = TimeUtil.getAfterDayDate(new Timestamp(times), -7).getTime();
        }else if(type == 2){
            frechartDatas = hourFreqChartDatas;
            freqs = hourFreqs;
            deleteTimes = TimeUtil.getAfterDayDate(new Timestamp(times), -30).getTime();
        }else if(type == 3){
            frechartDatas = dayFreqChartDatas;
            freqs = dayFreqs;
            deleteTimes = TimeUtil.getAfterDayDate(new Timestamp(times), -730).getTime();
        }else{
            return;
        }

        for(int frq : freqs){
            ConcurrentSkipListMap<Long, ChartDataBean> chartDatas = frechartDatas.get(frq);
            if(chartDatas == null){
                chartDatas = new ConcurrentSkipListMap<Long, ChartDataBean>();
                frechartDatas.put(frq, chartDatas);
            }
            long dangwei = getPointTime(times, type, frq);

            ChartDataBean current = chartDatas.get(dangwei);

            if(current == null){
                BigDecimal open = tr.getUnitPrice();
                BigDecimal close = tr.getUnitPrice();
                BigDecimal high = tr.getUnitPrice();
                BigDecimal low = tr.getUnitPrice();
                BigDecimal totalMoney = tr.getTotalPrice().multiply(BigDecimal.valueOf(2));
                BigDecimal totalNumbers = tr.getNumbers().multiply(BigDecimal.valueOf(2));
                log.info(totalMoney + " " + totalNumbers + " " + m.exchangeBixDian);
                BigDecimal middlePrice = totalMoney.divide(totalNumbers,m.exchangeBixDian,RoundingMode.CEILING);
                long startTransId = tr.getTransRecordId();
                long endTransId = tr.getTransRecordId();
                current = new ChartDataBean(open, close, high, low, middlePrice, type, dangwei, totalMoney, totalNumbers, startTransId, endTransId);
                current.setSaveToMysql(false);
                chartDatas.put(dangwei, current);

                try {
                    ////删除掉需要淘汰的
                    SortedMap<Long, ChartDataBean> needDeletes = chartDatas.headMap(deleteTimes);
                    if(needDeletes.size() > 0){
                        List<Long> dids = new ArrayList<Long>();

                        for(Entry<Long, ChartDataBean> entry2 : needDeletes.entrySet()){
                            dids.add(entry2.getKey());
                        }
                        for(Long did : dids){
                            chartDatas.remove(did);
                        }
                    }
                } catch (Exception e) {
                    log.error(e.toString(), e);
                }


            }else{
                if(tr.getUnitPrice().compareTo(current.getHigh())>0){
                    current.setHigh(tr.getUnitPrice());//最高
                }

                if(tr.getUnitPrice().compareTo(current.getLow())<0){
                    current.setLow(tr.getUnitPrice());//最高
                }

                /*
                // FIXME 这地方的交易量和交易金额存在造假处理  renfei  20170628
                // jira http://jira.mindasset.f3322.net:10080/browse/JYPT-680  将乘以两倍的逻辑删除掉
                */
                // 行业知识：买一笔卖一笔，成交后成交量应该算两次，所以需要把数量和金额乘以2，而不是造假 modify By buxianguan 20190220
                current.setTotalMoney(current.getTotalMoney().add(tr.getTotalPrice().multiply(BigDecimal.valueOf(2))));
                current.setTotalNumber(current.getTotalNumber().add(tr.getNumbers().multiply(BigDecimal.valueOf(2))));

                current.setMiddle(current.getTotalMoney().divide(current.getTotalNumber(),m.exchangeBixDian,RoundingMode.CEILING));
                current.setClose(tr.getUnitPrice());
            }
        }
    }


    ////50s执行一次数据库保存
    public static void saveToMysqlNew(Market m){
        StatisticsFor24Hour statisticsFor24Hour = statisticsFor24HourMap.get(m.market);
        if(statisticsFor24Hour == null){
            init24Hour(m);
        }
        //long inteval = intervals[type - 1];
        //保存修改前一分钟的数据
        try {
            Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>> minuteFreqChartDatas = marketsMinuteFreqChartDatas.get(m.market);
            if(minuteFreqChartDatas==null){
                minuteFreqChartDatas = new HashMap<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>();
                marketsMinuteFreqChartDatas.put(m.market, minuteFreqChartDatas);
            }

            Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>> hourFreqChartDatas = marketsHourFreqChartDatas.get(m.market);
            if(hourFreqChartDatas==null){
                hourFreqChartDatas = new HashMap<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>();
                marketsHourFreqChartDatas.put(m.market, hourFreqChartDatas);
            }

            Map<Integer, ConcurrentSkipListMap<Long, ChartDataBean>> dayFreqChartDatas = marketsDayFreqChartDatas.get(m.market);
            if(dayFreqChartDatas==null){
                dayFreqChartDatas = new HashMap<Integer, ConcurrentSkipListMap<Long, ChartDataBean>>();
                marketsDayFreqChartDatas.put(m.market, dayFreqChartDatas);
            }

            long times = TimeUtil.getMinuteFirst().getTime() - 60 * 1000;

            for(int type : types){
                ConcurrentSkipListMap<Long, ChartDataBean> frechartDatas = null;

                if(type == 1){
                    frechartDatas = minuteFreqChartDatas.get(1);
                }else if(type == 2){
                    frechartDatas = hourFreqChartDatas.get(1);
                    times = TimeUtil.getHourFirst(new Timestamp(times)).getTime();
                }else if(type == 3){
                    frechartDatas = dayFreqChartDatas.get(1);
                    times = TimeUtil.getTodayFirst(new Timestamp(times)).getTime();
                }else{
                    return;
                }


                if(frechartDatas == null){
                    continue;
                }

                ChartDataBean td = frechartDatas.get(times);

                if(td == null){
                    continue;
                }
                if(type == 1){
                    statisticsFor24Hour.addMinute(td);
                }

                ChartDataBean td2 = (ChartDataBean) Data.GetOne(m.db,"select * from chartdata where times=? and type=?", new Object[] {times, type }, ChartDataBean.class);

                BigDecimal open = BigDecimal.ZERO;// 一分钟之内的第一个
                BigDecimal close = BigDecimal.ZERO;
                long startTransId = 0;
                long endTransId = 0;
                BigDecimal totalMoney = BigDecimal.ZERO;
                BigDecimal numbers = BigDecimal.ZERO;
                BigDecimal lowUnitPrice = BigDecimal.ZERO;// 最低价
                BigDecimal highUnitPrice = BigDecimal.ZERO;// 最高价
                BigDecimal middleUnitPrice = BigDecimal.ZERO;// 平均价格
                String dueSql = "";
                if(td2 != null) {
                    open = td.getOpen();
                    startTransId = td.getStartTransId();

                    close = td.getClose();
                    endTransId = td.getEndTransId();
                    totalMoney = td.getTotalMoney();
                    numbers = td.getTotalNumber();
                    lowUnitPrice = td.getLow();
                    highUnitPrice = td.getHigh();
                    middleUnitPrice = totalMoney.divide(numbers,m.exchangeBixDian,RoundingMode.CEILING);

                    dueSql = "Update chartdata set close=?,high=?,low=?,middle=?,totalMoney=?,totalNumber=?,endTransId=?  where chartDataId=?";
                    Data.Update(m.db,dueSql, new Object[] { close, highUnitPrice, lowUnitPrice, middleUnitPrice, totalMoney, numbers,
                            endTransId, td2.getChartDataId() });
                } else {
                    open = td.getOpen();
                    close = td.getClose();
                    startTransId = td.getStartTransId();
                    endTransId = td.getEndTransId();
                    totalMoney = td.getTotalMoney();
                    numbers = td.getTotalNumber();
                    lowUnitPrice = td.getLow();
                    highUnitPrice = td.getHigh();
                    middleUnitPrice = td.getMiddle();

                    dueSql = "INSERT INTO  chartdata(open,  close,  high,  low,  middle,  type, times,  totalMoney,  totalNumber, startTransId,endTransId) values (?,?,?,?,?,?,?,?,?,?,?)";
                    Data.Insert(m.db,dueSql, new Object[] { open, close,highUnitPrice, lowUnitPrice, middleUnitPrice, type, times, totalMoney, numbers, startTransId, endTransId});

                    if(type == 3){
                        //更新历史今天之前有成交的最后一天记录
                        long todayStartTime = TimeUtil.getTodayFirst().getTime();
                        List<Bean> lastDealDayCharts = Data.Query(m.db,"select * from chartdata where type=? and times<? order by times desc limit 1", new Object[]{3 , todayStartTime}, ChartDataBean.class);
                        if(CollectionUtils.isNotEmpty(lastDealDayCharts)){
                            ChartDataBean cd = (ChartDataBean)lastDealDayCharts.get(0);
                            statisticsFor24Hour.setLastDealDayChart(cd);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    @Deprecated
    public static void addTransRecord(TransRecord tr,Market m){
        StatisticsFor24Hour statisticsFor24Hour = statisticsFor24HourMap.get(m.market);
        if(statisticsFor24Hour == null){
            init24Hour(m);
        }

        long times = tr.getTimeMinute();
        List<TransRecord> lists = noSaveToMysqlsData.get(times);
        if(lists == null){
            lists = new ArrayList<TransRecord>();
            noSaveToMysqlsData.put(times, lists);
        }
        lists.add(tr);
        addingMinute = times;
    }

    //open,  close,  high,  low,  middle,  type, times,  totalMoney,  totalNumber, startTransId, endTransId
    @Deprecated
    public static void saveToMysql(Market m){
        StatisticsFor24Hour statisticsFor24Hour = statisticsFor24HourMap.get(m.market);
        if(addingMinute == null){
            return;
        }
        //保存当前添加中之前的分钟
        SortedMap<Long, List<TransRecord>> needSaves = noSaveToMysqlsData;//.headMap(addingMinute);
        List<Long> deletes = new ArrayList<Long>();
        if(needSaves.size() > 0){
            for(Entry<Long, List<TransRecord>> entry : needSaves.entrySet()){
                List<TransRecord> records = entry.getValue();
                Long times = entry.getKey();

                if(records == null || records.size() <= 0){
                    continue;
                }

                BigDecimal open = BigDecimal.ZERO;
                BigDecimal close = BigDecimal.ZERO;
                BigDecimal high = BigDecimal.ZERO;
                BigDecimal low = BigDecimal.ZERO;
                BigDecimal totalMoney = BigDecimal.ZERO;
                BigDecimal totalNumbers = BigDecimal.ZERO;
                BigDecimal middlePrice = BigDecimal.ZERO;//平均价
                long startTransId = 0;
                long endTransId = 0;

                int type = 1;
                int i = 0;
                int size = records.size();

                for(TransRecord record : records){
                    BigDecimal price = record.getUnitPrice();
                    i++;
                    if(i == 1){
                        open = price;//开盘
                        startTransId = record.getTransRecordId();
                    }

                    if(i == size){
                        close = price;//收盘
                        endTransId = record.getTransRecordId();
                    }

                    if(high == null || (price.compareTo(high) > 0)){
                        high = price;//最高
                    }

                    if(low == null || (price.compareTo(low) < 0)){
                        low = price;//最低
                    }
                    totalMoney = totalMoney.add(record.getTotalPrice());//总成交额
                    totalNumbers = totalNumbers.add(record.getNumbers());//成交量
                }

                middlePrice = totalMoney.divide(totalNumbers,m.exchangeBixDian,RoundingMode.CEILING);

                ChartDataBean minute = new ChartDataBean(open, close, high, low, middlePrice, type, times, totalMoney, totalNumbers.multiply(BigDecimal.valueOf(2)), startTransId, endTransId);
                if(statisticsFor24Hour == null){
                    init24Hour(m);
                }
                log.info("++++++++++++++++++++++++++++++++++++:times:" + times + ",addingMinute:" + addingMinute);
                if(times.equals(addingMinute)){//由于当前分钟的仍处于调整期
                    statisticsFor24Hour.addMinute(minute);

                    ChartData.updateM(open, totalMoney, totalNumbers, low, high, middlePrice, close, startTransId, endTransId, times, false,m);
                    continue;
                }

                String dueSql="INSERT INTO  chartdata  (open,  close,  high,  low,  middle,  type, times,  totalMoney,  totalNumber, startTransId, endTransId) values (?,?,?,?,?,?,?,?,?,?,?)";
                int count = Data.Insert(dueSql,new Object[]{open,close,high,low,middlePrice,type,times,totalMoney,totalNumbers.multiply(BigDecimal.valueOf(2)), startTransId, endTransId});

                if(count > -1){
                    statisticsFor24Hour.addMinute(minute);
                    ChartData.updateM(open, totalMoney, totalNumbers, low, high, middlePrice, close, startTransId, endTransId, times, true,m);
                    deletes.add(times);
                    log.info("save minute and remove from memory:" + new Timestamp(times));
                }
            }
        }

        if(deletes.size() > 0){
            for(Long timess : deletes){
                noSaveToMysqlsData.remove(timess);
            }
        }
    }

    /**
     * 将Kline 数据打包后保存到数据库
     * @param m
     */
    public static void packetToMysql(Market m) {
        // 查询最新数据
        ChartDataPacketBean packetBean = Data.GetOneT(m.db,"select * from chartdata_packet where type=101 order by times desc limit 1", new Object[] {}, ChartDataPacketBean.class);
        List<ChartDataBean> chartDataBeans;
        BigDecimal close;
        Long times;
        if (packetBean != null) {
            close = packetBean.getClose();
            times = packetBean.getTimes();
           chartDataBeans = Data.QueryT(m.db,"select * from chartdata where times > ? and type=1 order by times asc limit 5000", new Object[]{packetBean.getTimes()}, ChartDataBean.class);
        } else {
            close = null;
            times = null;
            chartDataBeans = Data.QueryT(m.db,"select * from chartdata where type=1 order by times asc limit 5000", new Object[]{}, ChartDataBean.class);
        }
        if (chartDataBeans != null) {
            // 开始同步数据
            for (ChartDataBean chartDataBean : chartDataBeans) {
                Data.doTrans(doSyncChartData(m, times, close, chartDataBean));
                times = chartDataBean.getTimes();
                close = chartDataBean.getClose();
            }
        }
    }

    /**
     * 同步数据
     * @param preTimes 上一点位时间
     * @param preClose 上一收盘价格
     * @param chartDataBean
     */
    private static List<OneSql> doSyncChartData(Market m, Long preTimes, BigDecimal preClose, ChartDataBean chartDataBean) {
        List<OneSql> sqls = new ArrayList<>();
        if (preTimes != null && preClose != null) {
            long diff = chartDataBean.getTimes() - preTimes;
            long milliTimes = 60000;
            if (diff > milliTimes) {
                // 存在断档
                long count = diff/milliTimes - 1;
                if (count <= 30) {
                    // 超过30分钟无数据，即认为已停机维护
                    long nextTime = preTimes;
                    for (int i = 0; i < count; i++) {
                        nextTime = nextTime + milliTimes;
                        ChartDataBean virtualBean = new ChartDataBean();
                        virtualBean.setOpen(preClose);
                        virtualBean.setClose(preClose);
                        virtualBean.setHigh(preClose);
                        virtualBean.setLow(preClose);
                        virtualBean.setTotalMoney(BigDecimal.ZERO);
                        virtualBean.setTimes(nextTime);
                        Data.doTrans(doPacket(m, virtualBean, false));
                    }
                }
            }
        }

        sqls.addAll(doPacket(m, chartDataBean, true));
        return sqls;
    }

    /**
     * 打包构建SQL脚本
     * @param chartDataBean
     * @param isReality
     * @return
     */
    private static List<OneSql> doPacket(Market m, ChartDataBean chartDataBean, boolean isReality) {
        List<OneSql> sqls = new ArrayList<>();
        for (int type : types) {
            Integer[] frqs;
            if (type == 1) {
                frqs = minuteFreqs;
            } else if (type == 2) {
                frqs = hourFreqs;
            } else if (type == 3) {
                frqs = dayFreqs;
            } else {
                continue;
            }
            for (int frq : frqs) {
                // 计算点位
                long pointTime = getPointTime(chartDataBean.getTimes(), type, frq);
                int packetType = getPacketType(type, frq);
                ChartDataPacketBean packetBean = Data.GetOneT(m.db,"select * from chartdata_packet where times = ? and type = ?",
                        new Object[]{pointTime, packetType}, ChartDataPacketBean.class);
                if (packetBean == null) {
                    sqls.add(new OneSql("insert into chartdata_packet(`open`, `close`, high, low, `type`, times, totalMoney, reality) VALUE(?, ?, ?, ?, ?, ?, ?, ?)",
                            -1, new Object[]{
                                    chartDataBean.getOpen(),
                                    chartDataBean.getClose(),
                                    chartDataBean.getHigh(),
                                    chartDataBean.getLow(),
                                    packetType,
                                    pointTime,
                                    chartDataBean.getTotalMoney(),
                                    isReality ? 1 : 0
                            }, m.db)
                    );
                } else {
                    if (isReality) {
                        BigDecimal open;
                        BigDecimal close;
                        BigDecimal high;
                        BigDecimal low;
                        BigDecimal totalMoney;
                        if (packetBean.getReality() == 0) {
                            open = chartDataBean.getOpen();
                            close = chartDataBean.getClose();
                            high = chartDataBean.getHigh();
                            low = chartDataBean.getLow();
                            totalMoney = chartDataBean.getTotalMoney();
                        } else {
                            open = packetBean.getOpen();
                            close = chartDataBean.getClose();
                            high = packetBean.getHigh().compareTo(chartDataBean.getHigh()) < 0 ? chartDataBean.getHigh() : packetBean.getHigh();
                            low = packetBean.getLow().compareTo(chartDataBean.getLow()) > 0 ? chartDataBean.getLow() : packetBean.getLow();
                            totalMoney = packetBean.getTotalMoney().add(chartDataBean.getTotalMoney());
                        }
                        sqls.add(new OneSql("update chartdata_packet set `open` = ? , `close` = ? , high = ? , low = ? , totalMoney = ?, reality = 1 WHERE id = ?",
                                        1, new Object[]{
                                        open,
                                        close,
                                        high,
                                        low,
                                        totalMoney,
                                        packetBean.getId()
                                }, m.db)
                        );
                    }
                }
            }
        }
        return sqls;
    }

    /**
     * 获取类型
     * @param type
     * @param frq
     * @return
     */
    private static int getPacketType(int type, int frq) {
        return type * 100 + frq;
    }
}

package com.tenstar.timer.chart;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tenstar.TimeUtil;
import com.world.cache.Cache;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.model.daos.chart.ChartDataFactory;
import com.world.model.daos.chart.ChartDataType;
import com.world.model.daos.chart.ChartList;
import com.world.model.daos.chart.ChartManager;
import com.world.model.daos.chart.StatisticsFor24Hour;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entitys.record.TransRecord;
import com.world.util.CommonUtil;
import com.world.util.WebUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * 图表数据相关产生
 *
 * @author pc
 */
@SuppressWarnings("rawtypes")
public class ChartData extends TimerTask {
    public static Logger log = Logger.getLogger(ChartData.class);
    public static long times = System.currentTimeMillis();
    public static boolean doOne = false;

    public static Map<String, ChartArray> caMap = new HashMap<String, ChartArray>();
    Market m;

    public ChartData(Market m) {
        ChartArray ca = new ChartArray(m.market, 360, 1000, 3600);
        caMap.put(m.market, ca);
        log.info("数据图表系统启动成功！");
        setHotData(m);// 生成一次
        this.m = m;
    }

    public static void main(String[] args) {
        log.info(TimeUtil.getMinuteFirst().getTime());
    }

    /**
     * 顶部数据
     */
    public static String setHotData(Market m) {
        try {
            ChartArray ca = caMap.get(m.market);
            double[] hll = ca.dataMinute.get24();// 也可以用分钟统计，可能会慢一些
            StatisticsFor24Hour sfh = ChartManager.get24HourInfo(m);//获取24小时统计信息
            TreeMap<Long, ChartDataBean> last1440Map = sfh.getLast1440Minutes();
            // 24小时前的开盘价格 【北京时间0点】
            BigDecimal last24Price = BigDecimal.ZERO;
            BigDecimal last6Price = BigDecimal.ZERO;
            // 24小时前的收盘价
            BigDecimal priceOf24HAgo = BigDecimal.ZERO;
            /* 涨跌幅按照今天之前有历史成交的最后一笔成交价格计算，即如果昨天没有成交，取前天，以此类推 */
            //获取历史今天之前有成交的最后一天记录
            ChartDataBean lastDealDayChart = sfh.getLastDealDayChart();
            if(null != lastDealDayChart){
                last24Price = lastDealDayChart.getClose();
            }

            //价格趋势走势，获取一天的36个节点
            StringBuffer trendPrice = new StringBuffer();
            int size = last1440Map.size();
            if (last1440Map != null && !last1440Map.isEmpty()) {
                try {
                    Iterator<Entry<Long, ChartDataBean>> iterChart = last1440Map.entrySet().iterator();
                    priceOf24HAgo = last1440Map.firstEntry().getValue().getClose();

                    last6Price = sfh.getSix();
                    if (last6Price.compareTo(BigDecimal.ZERO) == 0) {
                        last6Price = last24Price;
                    }

                    if (size <= 36) {
                        while (iterChart.hasNext()) {
                            Entry<Long, ChartDataBean> entry = iterChart.next();
                            ChartDataBean bean = entry.getValue();
                            Long time = entry.getKey();
                            trendPrice.append(",[" + time + "," + bean.getClose() + "]");
                        }
                    } else if (size < 72) {
                        int start = size - 36;
                        int len = 0;
                        while (iterChart.hasNext()) {
                            len++;
                            if (len < start) continue;
                            start++;
                            Entry<Long, ChartDataBean> entry = iterChart.next();
                            ChartDataBean bean = entry.getValue();
                            Long time = entry.getKey();
                            trendPrice.append(",[" + time + "," + bean.getClose() + "]");
                        }
                    } else {
                        int yushu = size / 36;
                        int len = 0;
                        while (iterChart.hasNext()) {
                            if (size % yushu == 0) {
                                len++;
                                Entry<Long, ChartDataBean> entry = iterChart.next();
                                ChartDataBean bean = entry.getValue();
                                Long time = entry.getKey();
                                trendPrice.append(",[" + time + "," + bean.getClose() + "]");
                                if (len >= 35) break;
                            } else {
                                Entry<Long, ChartDataBean> entry = iterChart.next();
                            }
                            size--;
                        }
                    }
                } catch (Exception e) {
                    log.info("价格趋势走势异常！", e);
                }
            }
            String trend = "[]";
            if (trendPrice.length() > 0) {
                trend = trendPrice.substring(1);
            }

            double[] buysell = ChartManager.getBuyOneAndSellOne(m);
            if (buysell == null) {
                log.error("市场:" + m.market + " 买一卖一尚未初始化, 不能设置hotdata！");
                return null;
            }

            //24小时涨跌幅
            double riseRate = 0;
            if (last24Price.compareTo(BigDecimal.ZERO) > 0) {
                riseRate = ChartManager.getPrice(m).subtract(last24Price).divide(last24Price, 4, BigDecimal.ROUND_DOWN)
                        .multiply(BigDecimal.valueOf(100)).doubleValue();
            }
            /*start by xzhang 20170901 设置6小时涨跌幅计算规则*/
            //6小时涨跌幅
            double hour6RiseRate = 0;
            if (last6Price.compareTo(BigDecimal.ZERO) > 0) {
                hour6RiseRate = ChartManager.getPrice(m).subtract(last6Price).divide(last6Price, 4, BigDecimal.ROUND_DOWN)
                        .multiply(BigDecimal.valueOf(100)).doubleValue();
            }
            /*end*/

            //周涨跌幅
            double weekRiseRate = getRiseRate(1, m).doubleValue();
            //30日涨跌幅
            double monthRiseRate = getRiseRate(2, m).doubleValue();
            //90日涨跌幅
            double month3RiseRate = getRiseRate(3, m).doubleValue();
            //180日涨跌幅
            double month6RiseRate = getRiseRate(4, m).doubleValue();

            ChartManager.setLast24RiseRate(m, riseRate);

            String end = ChartManager.getPrice(m) + ","
                    + BigDecimal.valueOf(buysell[0]).toPlainString() + ","
                    + BigDecimal.valueOf(buysell[1]).toPlainString() + ","
                    + BigDecimal.valueOf(hll[0]).toPlainString() + ","
                    + BigDecimal.valueOf(hll[1]).toPlainString() + ","
                    + BigDecimal.valueOf(hll[2]).toPlainString() + ","
                    + last24Price
                    + ",[" + trend + "],"
                    + riseRate + ","
                    + BigDecimal.valueOf(hll[3]).toPlainString() + "";

            String outData = "{\"ticker\":{\"high\":\"" + BigDecimal.valueOf(hll[0]).toPlainString()
                    + "\",\"low\":\"" + BigDecimal.valueOf(hll[1]).toPlainString() + "\",\"buy\":\""
                    + BigDecimal.valueOf(buysell[0]).toPlainString() + "\",\"sell\":\""
                    + BigDecimal.valueOf(buysell[1]).toPlainString()
                    + "\",\"last\":\""
                    + ChartManager.getPrice(m)
                    + "\",\"vol\":\"" + hll[2] + "\",\"riseRate\":" + new BigDecimal(riseRate).toPlainString() + ",\"weekRiseRate\":" + new BigDecimal(weekRiseRate).toPlainString() + ",\"monthRiseRate\":" + new BigDecimal(monthRiseRate).toPlainString() + ",\"totalBtcNum\":" + hll[3] + ",\"hour6RiseRate\":" + new BigDecimal(hour6RiseRate).toPlainString() + ",\"month3RiseRate\":" + new BigDecimal(month3RiseRate).toPlainString() + ",\"month6RiseRate\":" + new BigDecimal(month6RiseRate).toPlainString() + "}}";

            //挪到redis里
            DishDataCacheService.setHotData(m.market, end, 10 * 60 * 60);
            //存放在redis中
            DishDataCacheService.setTicker(m.market, outData, 10 * 60 * 60);

            //缓存24小时成交量，用于计算平台所有市场24小时成交量
            Cache.Set(String.format(ChartConstant.MARKET_24HOUR_VOLUME_CACHE_KEY, m.market), BigDecimal.valueOf(hll[3]).toPlainString(), 24 * 60 * 60);

            //缓存市场热数据，提供给api调用
            JSONObject hotDataApiJson = new JSONObject();
            hotDataApiJson.put("buy", BigDecimal.valueOf(buysell[0]).toPlainString());
            hotDataApiJson.put("high", BigDecimal.valueOf(hll[0]).toPlainString());
            hotDataApiJson.put("last", ChartManager.getPrice(m).stripTrailingZeros().toPlainString());
            hotDataApiJson.put("low", BigDecimal.valueOf(hll[1]).toPlainString());
            hotDataApiJson.put("sell", BigDecimal.valueOf(buysell[1]).toPlainString());
            // 成交量
            BigDecimal vol = BigDecimal.valueOf(hll[2]);
            hotDataApiJson.put("vol", vol.toPlainString());
            hotDataApiJson.put("change", String.valueOf(riseRate));
            // 成交额
            BigDecimal totalMoney = BigDecimal.valueOf(hll[3]);
            hotDataApiJson.put("totalMoney", totalMoney.toPlainString());
            // 北京时间0点开盘价格
            hotDataApiJson.put("dayOpen", last24Price.toPlainString());
            // 24小时前开盘价格
            hotDataApiJson.put("open", priceOf24HAgo.toPlainString());
            // 平均价格【成交额/成交量】
            hotDataApiJson.put("avgPrice", totalMoney.divide(vol.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : vol, m.exchangeBixDian, BigDecimal.ROUND_DOWN));
            // 币种英文全称
            hotDataApiJson.put("fullName", m.numberBiFullName);
            // 币种英文简称
            hotDataApiJson.put("shortName", m.numberBiEn);
            // 图片URL
            CoinProps coinProps = DatabasesUtil.coinProps(m.numberBiFundsType);
            hotDataApiJson.put("imgUrl", coinProps != null ? coinProps.getImgUrl() : "");
            DishDataCacheService.setALlMarketHotData(m.market, hotDataApiJson.toJSONString(), 5 * 60);

            return outData;
        } catch (Exception e) {
            log.error(e.toString(), e);
            return null;
        }
    }

    public static String GetTiker(Market m) {
        return setHotData(m);
    }

    @Override
    public void run() {

        //if (SystemStatus.chartDataRuning) {
        try {
            //if (SystemStatus.chartDataNewWork) {// 在固定周期内或者主动状态被触发就会执行
            //SystemStatus.chartDataNewWork = false;
                /*	int dueWork = 0;
					while (updateMinute(m)) {
						dueWork++;// 一直执行到没有任务为止
					}
					log.info(m.market+"成功处理：" + dueWork + "条交易记录");
					*/
            //ChartManager.saveToMysql();

            // log.info("例行循环图表");
            // 做一个循环，不断获取需要处理的数据
//					long now = TimeUtil.getMinuteFirst().getTime();
//					if (now > lastminute) {
//						lastminute = now;
//						// 更新内存数据
//						ChartDataType type = ChartDataFactory.getTypesInstance().get(1); // 分钟
//						type.add(now);
//					}
//					now = TimeUtil.getHourFirst().getTime();
//					if (now > lasthour) {
//						lasthour = now;
//						ChartDataType type = ChartDataFactory.getTypesInstance().get(2); // 分钟
//						type.add(now);
//					}
//					now = TimeUtil.getTodayFirst().getTime();
//					if (now > lastday) {
//						lastday = now;
//						ChartDataType type = ChartDataFactory.getTypesInstance().get(3); // 分钟
//						type.add(now);
//					}


            times = System.currentTimeMillis();// 增加一个单位跳出本循环
            setHotData(m);
//				} else{
//					Thread.sleep(500);
//				}
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

    }
    //}

    /**
     * 生成图表数据
     */
    public void makeChartData() {

    }

    private static long lastminute = TimeUtil.getMinuteFirst().getTime();
    private static long lasthour = TimeUtil.getHourFirst().getTime();
    private static long lastday = TimeUtil.getTodayFirst().getTime();

    /**
     * 查找最新一个没有更新的交易，一次会完成所有的数据
     *
     * @param m
     */
    public static boolean updateMinute(Market m) {

        List<TransRecord> trs = Data.QueryT(m.db, "select * from TransRecord where iscount=0 and status>0 and times<=? order by transRecordId limit 0,100",
                new Object[]{TimeUtil.getNow().getTime()}, TransRecord.class);
        if (trs == null || trs.size() <= 0)
            return false;

        String ids = "";
        for (TransRecord tr : trs) {
            ids += "," + tr.getTransRecordId();
            if (tr.getUnitPrice().compareTo(BigDecimal.ZERO) > 0 && tr.getNumbers().compareTo(BigDecimal.ZERO) > 0) {// 避免性能瓶颈,需要把未处理的取消给处理一下
                ChartManager.addNewTransRecord(tr, m);
            }
        }

        if (ids.length() > 0) {
            ids = ids.substring(1);
            Data.Update(m.db, "update transrecord set iscount=1 where transRecordId in(" + ids + ")", new Object[]{});
        }
        return true;


        // 统计同一分钟内的数据 ,sum(case when then btcs end)
//		List li = (List) Data.GetOne("SELECT SUM(totalPrice),SUM(numbers),MIN(unitPrice),MAX(unitPrice) FROM transrecord where timeMinute=?  and unitPrice>0 and iscount=1  ",
//						new Object[] {timeMinute });
//
//		if (li == null || li.get(0) == null)
//			return false;
//
//		long totalPrice = Long.parseLong(li.get(0).toString());
//		long numbers = Long.parseLong(li.get(1).toString());
//		long lowUnitPrice = Long.parseLong(li.get(2).toString());// 最低价
//		long highUnitPrice = Long.parseLong(li.get(3).toString());// 最高价
//		long middleUnitPrice = totalPrice / numbers;// 平均价格
//
//		if (numbers > 0) {
//			middleUnitPrice = totalPrice / numbers;// 平均价格
//		}
//
//		List li1 = (List) Data.GetOne("SELECT unitPrice,transRecordId FROM transrecord where timeMinute=? and unitPrice>0 order by times asc limit 0,1",
//						new Object[] { timeMinute });
//		long open = Long.parseLong(li1.get(0).toString());// 开盘价格
//		long startTransId = Long.parseLong(li1.get(1).toString());
//		List li2 = (List) Data.GetOne("SELECT unitPrice,transRecordId FROM transrecord where timeMinute=? and unitPrice>0 order by times desc limit 0,1",
//						new Object[] { timeMinute });
//		long close = Long.parseLong(li2.get(0).toString());// 收盘价格
//		long endTransId = Long.parseLong(li2.get(1).toString());
//
//		ChartDataBean td = (ChartDataBean) Data.GetOne(
//				"select * from ChartData where times=? and type=1",
//				new Object[] { timeMinute }, ChartDataBean.class);
//		String dueSql = "";
//		int count = 0;
//		if (td != null) {
//			// log.info("有数据了，进行更新操作！");
//			dueSql = "Update ChartData set close=?,high=?,low=?,middle=?,totalMoney=?,totalNumber=?,endTransId=? where chartDataId=?";
//			count = Data.Update(dueSql, new Object[] { close, highUnitPrice,
//					lowUnitPrice, middleUnitPrice, totalPrice, numbers * 2,
//					endTransId, td.getChartDataId() });
//
//		} else {
//			dueSql = "INSERT INTO  ChartData  (open,  close,  high,  low,  middle,  type, times,  totalMoney,  totalNumber, startTransId, endTransId) values (?,?,?,?,?,?,?,?,?,?,?)";
//			count = Data.Insert(dueSql, new Object[] { open, close,
//					highUnitPrice, lowUnitPrice, middleUnitPrice, 1,
//					timeMinute, totalPrice, numbers * 2, startTransId,
//					endTransId });
//
//		}
    }

    public static boolean updateM(BigDecimal open, BigDecimal totalPrice, BigDecimal numbers, BigDecimal lowUnitPrice, BigDecimal highUnitPrice, BigDecimal middleUnitPrice, BigDecimal close, long startTransId, long endTransId, long times, boolean sanveMysql, Market m) {
        ChartDataType type = ChartDataFactory.getTypesInstance().get(1); // 分钟

        ChartDataBean last = getChartData(times, 1, m);

        if (last == null) {
            return false;
        }

        updateChart(1, open, totalPrice, numbers, lowUnitPrice, highUnitPrice, middleUnitPrice, close, startTransId, endTransId, last);
        type.refreshChartMap(last, times);
        log.info("更新当前分钟：" + new Timestamp(times) + "数量为：" + numbers + ",sanveMysql:" + sanveMysql);
        // 将这分钟内的数据全部更新
        // Data.Update("update transrecord set iscount=1 where timeMinute=? and unitPrice>0",
        // new Object[]{timeMinute});
        ChartDataBean tdn = new ChartDataBean();
        tdn.setOpen(open);
        tdn.setClose(close);
        tdn.setStartTransId(startTransId);
        tdn.setEndTransId(endTransId);
        tdn.setTimes(times);
        tdn.setHigh(highUnitPrice);
        tdn.setLow(lowUnitPrice);
        tdn.setMiddle(middleUnitPrice);
        tdn.setTotalMoney(totalPrice);
        tdn.setTotalNumber(numbers);

        return updateHour(tdn, sanveMysql, m);
    }


    /**
     * @param time
     * @param itype 1分钟 2小时  3天
     * @return
     */
    private static ChartDataBean getChartData(long time, int itype, Market m) {
        ChartDataType type = ChartDataFactory.getTypesInstance().get(itype); // 分钟
        ChartList clist = type.chartMap.get(1);
        ChartDataBean last = clist.lists.getLast();

		/*if(last.getTimes() != time){//防止丢包
			for(int i = clist.lists.size() - 1; i >= 0; i--){
				if(last.getTimes() == time){
					last = clist.lists.get(i);
					break;
				}
			}
		}*/

        if (last != null) {
            if (last.getTimes() < time) {//新的时间
                type.add(time, m);//？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？
                last = clist.lists.getLast();
            }
        }


        return last;
    }

    private static ChartDataBean lastHourChartData = null;//最后一次处理的chartData数据

    private static ChartDataBean lastDayChartData = null;//最后一次处理的chartData数据

    public static boolean updateHour(ChartDataBean td, boolean saveMysql, Market m) {
        ChartDataType type = ChartDataFactory.getTypesInstance().get(2); // 分钟
        long tsFirst = TimeUtil.getHourFirst(td.getTimes());
        BigDecimal open = BigDecimal.ZERO;// 一分钟之内的第一个
        BigDecimal close = BigDecimal.ZERO;
        long startTransId = 0;
        long endTransId = 0;
        BigDecimal totalMoney = BigDecimal.ZERO;
        BigDecimal numbers = BigDecimal.ZERO;
        BigDecimal lowUnitPrice = BigDecimal.ZERO;// 最低价
        BigDecimal highUnitPrice = BigDecimal.ZERO;// 最高价
        BigDecimal middleUnitPrice = BigDecimal.ZERO;// 平均价格


        ChartDataBean last = getChartData(tsFirst, 2, m);
        if (last == null) {
            return false;
        }
        if (!saveMysql) {
            if (lastHourChartData == null || TimeUtil.getHourFirst(lastHourChartData.getTimes()) != tsFirst) {
                open = td.getOpen();
                close = td.getClose();
                startTransId = td.getStartTransId();
                endTransId = td.getEndTransId();
                totalMoney = td.getTotalMoney();
                numbers = td.getTotalNumber();
                lowUnitPrice = td.getLow();
                highUnitPrice = td.getHigh();
                middleUnitPrice = td.getMiddle();
            } else {
                close = td.getClose();
                endTransId = td.getEndTransId();
                totalMoney = last.getTotalMoney().add(td.getTotalMoney()).subtract(lastHourChartData.getTotalMoney());
                numbers = last.getTotalNumber().add(td.getTotalNumber()).subtract(lastHourChartData.getTotalNumber());
                lowUnitPrice = td.getLow().compareTo(last.getLow()) < 0 ? td.getLow() : last.getLow();
                highUnitPrice = (td.getHigh().compareTo(last.getHigh()) > 0) ? td.getHigh() : last.getHigh();
                middleUnitPrice = totalMoney.divide(numbers, m.exchangeBixDian, RoundingMode.CEILING);
            }

            lastHourChartData = (ChartDataBean) WebUtil.deepCopyObj(td);
            updateChart(2, open, totalMoney, numbers, lowUnitPrice, highUnitPrice, middleUnitPrice, close, startTransId,
                    endTransId, last);

            type.refreshChartMap(last, tsFirst);


            return updateDay(td, false, m);
        }

        ChartDataBean td2 = (ChartDataBean) Data.GetOne(m.db, "select * from chartdata where times=? and type=2", new Object[]{tsFirst}, ChartDataBean.class);

        String dueSql = "";
        int count = 0;
        if (td2 != null) {
            open = td2.getOpen();
            startTransId = td2.getStartTransId();

            close = td.getClose();
            endTransId = td.getEndTransId();
            totalMoney = td2.getTotalMoney().add(td.getTotalMoney());
            numbers = td2.getTotalNumber().add(td.getTotalNumber());
            lowUnitPrice = td.getLow().compareTo(td2.getLow()) < 0 ? td.getLow() : td2.getLow();
            highUnitPrice = (td.getHigh().compareTo(td2.getHigh()) > 0) ? td.getHigh() : td2.getHigh();
            middleUnitPrice = totalMoney.divide(numbers, m.exchangeBixDian, RoundingMode.CEILING);

            dueSql = "Update ChartData set close=?,high=?,low=?,middle=?,totalMoney=?,totalNumber=?,endTransId=?  where chartDataId=?";
            count = Data.Update(dueSql, new Object[]{close, highUnitPrice, lowUnitPrice, middleUnitPrice, totalMoney, numbers,
                    endTransId, td2.getChartDataId()});
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
            count = Data.Insert(dueSql, new Object[]{open, close, highUnitPrice, lowUnitPrice, middleUnitPrice, 2, tsFirst, totalMoney, numbers, startTransId, endTransId});
        }

        if (count > 0) {
            updateChart(2, open, totalMoney, numbers, lowUnitPrice, highUnitPrice, middleUnitPrice, close, startTransId,
                    endTransId, last);

            return updateDay(td, true, m);

        } else {
            return false;
        }
    }

    public static boolean updateDay(ChartDataBean td, boolean saveMysql, Market m) {
        long tsFirst = TimeUtil.getTodayFirst(td.getTimes());
        ChartDataType type = ChartDataFactory.getTypesInstance().get(3); // 分钟
        BigDecimal open = BigDecimal.ZERO;// 一分钟之内的第一个
        BigDecimal close = BigDecimal.ZERO;
        long startTransId = 0;
        long endTransId = 0;
        BigDecimal totalMoney = BigDecimal.ZERO;
        BigDecimal numbers = BigDecimal.ZERO;
        BigDecimal lowUnitPrice = BigDecimal.ZERO;// 最低价
        BigDecimal highUnitPrice = BigDecimal.ZERO;// 最高价
        BigDecimal middleUnitPrice = BigDecimal.ZERO;// 平均价格


        ChartDataBean last = getChartData(tsFirst, 3, m);
        if (last == null) {
            return false;
        }
        if (!saveMysql) {
            if (lastDayChartData == null || TimeUtil.getTodayFirst(lastDayChartData.getTimes()) != tsFirst) {
                open = td.getOpen();
                close = td.getClose();
                startTransId = td.getStartTransId();
                endTransId = td.getEndTransId();
                totalMoney = td.getTotalMoney();
                numbers = td.getTotalNumber();
                lowUnitPrice = td.getLow();
                highUnitPrice = td.getHigh();
                middleUnitPrice = td.getMiddle();
            } else {
                close = td.getClose();
                endTransId = td.getEndTransId();
                totalMoney = last.getTotalMoney().add(td.getTotalMoney()).subtract(lastDayChartData.getTotalMoney());
                numbers = last.getTotalNumber().add(td.getTotalNumber()).subtract(lastDayChartData.getTotalNumber());
                lowUnitPrice = td.getLow().compareTo(last.getLow()) < 0 ? td.getLow() : last.getLow();
                highUnitPrice = (td.getHigh().compareTo(last.getHigh()) > 0) ? td.getHigh() : last.getHigh();
                middleUnitPrice = totalMoney.divide(numbers, m.exchangeBixDian, RoundingMode.CEILING);
            }
            lastDayChartData = td;
            updateChart(3, open, totalMoney, numbers, lowUnitPrice, highUnitPrice, middleUnitPrice, close, startTransId,
                    endTransId, last);

            type.refreshChartMap(last, tsFirst);
            return true;
        }


        ChartDataBean td2 = (ChartDataBean) Data.GetOne(m.db, "select * from chartdata where times=? and type=3", new Object[]{tsFirst}, ChartDataBean.class);

        String dueSql = "";
        int count = 0;
        if (td2 != null) {
            open = td2.getOpen();
            startTransId = td2.getStartTransId();

            close = td.getClose();
            endTransId = td.getEndTransId();
            totalMoney = td2.getTotalMoney().add(td.getTotalMoney());
            numbers = td2.getTotalNumber().add(td.getTotalNumber());
            lowUnitPrice = td.getLow().compareTo(td2.getLow()) < 0 ? td.getLow() : td2.getLow();
            highUnitPrice = (td.getHigh().compareTo(td2.getHigh()) > 0) ? td.getHigh() : td2.getHigh();
            middleUnitPrice = totalMoney.divide(numbers, m.exchangeBixDian, RoundingMode.CEILING);

            dueSql = "Update ChartData set close=?,high=?,low=?,middle=?,totalMoney=?,totalNumber=?,endTransId=?  where chartDataId=?";
            count = Data.Update(dueSql, new Object[]{close, highUnitPrice, lowUnitPrice, middleUnitPrice, totalMoney, numbers,
                    endTransId, td2.getChartDataId()});
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
            count = Data.Insert(dueSql, new Object[]{open, close, highUnitPrice, lowUnitPrice, middleUnitPrice, 3, tsFirst, totalMoney, numbers, startTransId, endTransId});
        }
        if (count > 0) {
            updateChart(3, open, totalMoney, numbers, lowUnitPrice, highUnitPrice, middleUnitPrice, close, startTransId,
                    endTransId, last);
            return true;
        } else {
            return false;
        }
    }

    private static void updateChart(int charttype, BigDecimal open, BigDecimal totalPrice,
                                    BigDecimal numbers, BigDecimal lowUnitPrice, BigDecimal highUnitPrice,
                                    BigDecimal middleUnitPrice, BigDecimal close, long startTransId, long endTransId, ChartDataBean last) {

        last.setTotalMoney(totalPrice.multiply(BigDecimal.valueOf(2)));
        last.setTotalNumber(numbers.multiply(BigDecimal.valueOf(2)));
        last.setOpen(open);
        last.setClose(close);
        last.setLow(lowUnitPrice);
        last.setHigh(highUnitPrice);
        last.setMiddle(middleUnitPrice);
        last.setStartTransId(startTransId);
        last.setEndTransId(endTransId);
        ChartDataType type = ChartDataFactory.getTypesInstance().get(charttype); // 分钟

        type.refreshChartMap(last, last.getTimes());
    }

    /**
     * 获取指定类型的涨跌幅度
     *
     * @param riseType 1:周涨跌幅   2： 1月涨跌幅  3:3个月  4:6个月
     * @param m        市场对象
     * @return 对应的涨跌幅
     */
    private static BigDecimal getRiseRate(int riseType, Market m) {

        //涨跌幅度
        BigDecimal riseRate = BigDecimal.ZERO;
        BigDecimal open = BigDecimal.ZERO;//开盘价格
        BigDecimal close = BigDecimal.ZERO;//收盘价格

        long timestamp = 0;
        if (riseType == 1) {//获取7天前时间
            timestamp = CommonUtil.addDay(new Timestamp(System.currentTimeMillis()), -7).getTime() / 1000;
        } else if (riseType == 2) {
            timestamp = CommonUtil.addDay(new Timestamp(System.currentTimeMillis()), -30).getTime() / 1000;
        } else if (riseType == 3) {
            timestamp = CommonUtil.addDay(new Timestamp(System.currentTimeMillis()), -90).getTime() / 1000;
        } else if (riseType == 4) {
            timestamp = CommonUtil.addDay(new Timestamp(System.currentTimeMillis()), -180).getTime() / 1000;
        } else {
            return riseRate;
        }

        String kline = ChartManager.getJson(3, 1, m);
        if (StringUtils.isNotBlank(kline) && !kline.equals("[]")) {
            kline = "[" + kline + "]";
            JSONArray arr = JSONObject.parseArray(kline);
            if (arr != null && arr.size() > 0) {
                for (int i = arr.size() - 1; i >= 0; i--) {
                    JSONArray dataArr = arr.getJSONArray(i);
                    long times = dataArr.getLongValue(0);//k线数据
                    if (i == arr.size() - 1) {//最后一条数据，获取它的收盘价为当前价
                        close = dataArr.getBigDecimal(4);//收盘价
                    } else if (times <= timestamp) {
                        open = dataArr.getBigDecimal(4);//收盘价
                        break;
                    }
                }
                if (open.compareTo(BigDecimal.ZERO) != 0) {
                    riseRate = close.subtract(open).divide(open, 4, RoundingMode.CEILING).multiply(BigDecimal.valueOf(100));
                }

            }
        }

        return riseRate;
    }

}

package com.world.model.daos.chart;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.tenstar.timer.chart.ChartDataBean;
import com.world.model.Market;
import com.world.util.WebUtil;
import com.world.util.date.TimeUtil;

/***
 * K线类型  分钟K线   小时K线
 * @author apple
 *
 */
public class ChartDataType {

	public ChartDataType(int dataType , Map<Integer , ChartList> initChartMap) {
		super();
		if(dataType == 1){
			this.interval = 60 * 1000;
		}if(dataType == 2){
			this.interval = 60 * 60 * 1000;
		}if(dataType == 3){
			this.interval = 24 * 60 * 60 * 1000;
		}
		this.dataType = dataType;
		this.initChartMap = initChartMap;
		
	}

	protected static Logger log = Logger.getLogger(ChartDataType.class.getName());
	public Map<Integer , ChartList> chartMap;
	private Map<Integer , ChartList> initChartMap;
	
	//最后从数据库中取出的分钟数
	private long lastMinute = 0;
	//单次提取的分钟数据
	private Map<Long , ChartDataBean> every = new HashMap<Long , ChartDataBean>();
	
    private ChartDataDao chartDataDao = new ChartDataDao();
	
	private ChartDataBean lastChartDataBean;
	private Map<Integer,ChartDataBean> lastFreqChartDataBeans = new HashMap<Integer, ChartDataBean>();
	
	private long interval;//类型间隔的毫秒数  例如：分间隔 60 * 1000   小时  ：60 * 60 * 1000
	private int dataType;//数据库中的类型   1.分  2.小时  3.天
	
	
	/*
	 * method ==================================================
	 */
	
	
	
	//add by guojiahua
	private ChartDataBean getLastFreqChartDataBean(Integer freq){
		if (lastFreqChartDataBeans.get(freq)==null) {
			lastFreqChartDataBeans.put(freq, new ChartDataBean());
		}
		return lastFreqChartDataBeans.get(freq);
	}
	
	public String getJson(int intervalType,Market m){
		if(chartMap == null){
			init(m);
		}
		//log.info("intervalType:" + intervalType);
		ChartList cl = chartMap.get(intervalType);
		if(cl == null){
			return "[]";
		}
		StringBuilder sbl = new StringBuilder();
		for(ChartDataBean td : cl.lists){
			
			if(td.getTotalNumber().compareTo(BigDecimal.ZERO)>0){
				td.setMiddle(td.getTotalMoney().divide(td.getTotalNumber(),m.exchangeBixDian,RoundingMode.CEILING));
				//[1457845800,0.0,2720.0,0.0,2720.0,1.53]
				 sbl.append(",["+td.getTimes()/1000+","+Market.formatMoney(td.getOpen(),m)+","
				   +Market.formatMoney(td.getHigh(),m)+","+Market.formatMoney(td.getLow(),m)+","+Market.formatMoney(td.getClose(),m)+","
				   +Market.formatNumber(td.getTotalNumber(),m)+"]");
			}
				   
		}
		
		if(sbl.toString().length() > 0){
			return sbl.toString().substring(1);
		}
		
        return sbl.toString();
	}
	//bitcoinwisdom 返回数据格式 
	public String getJson(int intervalType, int type,Market m){
		if(chartMap == null){
			init(m);
		}
		//log.info("intervalType:" + intervalType);
		ChartList cl = chartMap.get(intervalType);
		if(cl == null){
			return "[]";
		}
		StringBuilder sbl = new StringBuilder();
		for(ChartDataBean td : cl.lists){
			
			if(td.getTotalNumber().compareTo(BigDecimal.ZERO)>0){
				 td.setMiddle(td.getTotalMoney().divide(td.getTotalNumber(),m.exchangeBixDian,RoundingMode.CEILING));
				 sbl.append(",["+td.getTimes()/1000+","+Market.formatMoney(td.getOpen(),m)+","+Market.formatMoney(td.getHigh(),m)+","+
				 Market.formatMoney(td.getLow(),m)+","+Market.formatMoney(td.getClose(),m)+","
				 +Market.formatNumber(td.getTotalNumber(),m)+",0,0,"+Market.formatMoneyAndNumber(td.getTotalMoney(),m)+"]");
			}
//				 log.info("=========="+td.getTotalMoney()+"-----"+Market.formatMoneyAndNumber(td.getTotalMoney()));
		}
		
		if(sbl.toString().length() > 0){
			return sbl.toString().substring(1);
		}
		
        return sbl.toString();
	}
	
	
	
	private StatAssistBean stat = null;
	private int count =0;
	private long nextTime=0;
	private boolean exit=false;
	
	
	private synchronized void init(Market m){
		if(chartMap == null){
			long start = 0;
			if (this.dataType == 1) {
				start = TimeUtil.getAfterDayDate(TimeUtil.getMinuteFirst(), -7).getTime();
			} else if (this.dataType == 2) {
				start = TimeUtil.getAfterDayDate(TimeUtil.getHourFirst(), -30 * 6).getTime();
			} else if (this.dataType == 3) {
				start = TimeUtil.getAfterDayDate(TimeUtil.getTodayFirst(), -300 ).getTime();
			}
			
			nextTime=0;
			count=0;
			exit = false;
			while (!exit) {
				add(nextTime==0? start : nextTime,m);
			}
			log.info("add() count = " + count);
			
			chartMap = initChartMap;
			log.info("chartMap size(K): " + WebUtil.objectSize(chartMap)/1024 + "====================");
		}
		
		
//		//for test
//		log.info("START ! ===================================");
//		Set<Integer> keys = initChartMap.keySet();
////		keys.remove(1);
//		for(Integer freq : keys){
//			ChartList lls = initChartMap.get(freq);
//			LinkedList<ChartDataBean> lists = lls.lists;
//			log.info("freq " + freq);
//			for (ChartDataBean cdb : lists) {
//				long times = cdb.getTimes();
//				 String m  = new Date(times).toLocaleString();
//				log.info(m);
//			}
//		}
//		log.info("END ! ===================================");
	}


	
	public void addNew(long times){
		ChartDataBean cdb = new ChartDataBean();
		
	}

	/***
	 * 添加times到当前时间的记录
	 * @param times
	 */
	public void add(long times,Market m){
		long now = 0;
		if(this.dataType==1){
			now = TimeUtil.getMinuteFirst().getTime();
		}else if(this.dataType==2){
			now = TimeUtil.getHourFirst().getTime();
		}else if(this.dataType==3){
			now = TimeUtil.getTodayFirst().getTime();
		}
		if(times > now){
			exit=true;
			lastMinute = 0;
			return;
		}
		
		if(times >= lastMinute){//最多取出120条数据
			long nextMinute = times + 120 * interval;
			//获取最新产生的此类型下的120条数据
			every = chartDataDao.getCharts(dataType , times, nextMinute,m);
			lastMinute = nextMinute;
		}
		
		
		ChartDataBean cdb = every.get(times); 
		
		if(cdb == null){
			if(lastChartDataBean == null){
				cdb = new ChartDataBean();
			}else{
				cdb = lastChartDataBean.clone();
				cdb.setOpen(lastChartDataBean.getOpen());
				cdb.setClose(lastChartDataBean.getClose());
				cdb.setHigh(lastChartDataBean.getHigh());
				cdb.setLow(lastChartDataBean.getLow());
				cdb.setMiddle(lastChartDataBean.getMiddle());
				cdb.setTotalMoney(BigDecimal.ZERO);
			}
			if(cdb.getTotalNumber().doubleValue() > 0){
				cdb.setMiddle(cdb.getTotalMoney().divide(cdb.getTotalNumber(),m.exchangeBixDian,RoundingMode.CEILING));
			}
			
			cdb.setTimes(times);
			cdb.setTotalNumber(BigDecimal.ZERO);
		}
		//cdb is ready
		refreshChartMap(cdb, times);
		
		lastChartDataBean = cdb;
		
		
		times += interval;
		
		count++;
		nextTime = times;
		if (count % 1000 == 0) {
			// 防止在递归时栈满
			exit = false;
			return;
		}
		
		add(times,m);//继续下一分钟
	}

    /**
     *
     * @param cdb
     * @param times
     * @deprecated 应该不再使用  Jack 2019-02-26
     * @see ChartManager#initByType(int, Market)
     */
	public void refreshChartMap(ChartDataBean cdb, long times){
		Set<Integer> keys = initChartMap.keySet();
		
		for(Integer freq : keys){
			
			ChartList lls = initChartMap.get(freq);
			if(lls == null){
				lls = new ChartList();
				lls.max = 1440 ;
//				lls.max = 1440 / freq;
			}
			
			
			////寻找当前时间点位所在的时间戳
			
			long cha = (cdb.getMinuteCha()) % (freq * interval);
			
			long dianwei = cdb.getTimes() - cha;
//log.info(freq + ":----------dianwei:" + new Timestamp(dianwei));
			

			boolean needAdd = false;

			if((lls.tool == null || lls.tool.getTimes() != dianwei) && cha != 0 && freq!=1){
				lls.tool = cdb;
				lls.tool = getLastFreqChartDataBean(freq).clone();
				lls.tool.setOpen(getLastFreqChartDataBean(freq).getOpen());
				lls.tool.setClose(getLastFreqChartDataBean(freq).getClose());
				lls.tool.setHigh(getLastFreqChartDataBean(freq).getHigh());
				lls.tool.setLow(getLastFreqChartDataBean(freq).getLow());
				lls.tool.setMiddle(getLastFreqChartDataBean(freq).getMiddle());
				lls.tool.setTotalMoney(cdb.getTotalMoney());
				lls.tool.setTotalNumber(cdb.getTotalNumber());
				lls.tool.setTimes(dianwei);
				
				StatAssistBean.getInstance(freq).reset();
				lastFreqChartDataBeans.put(freq, lls.tool);//保存上一个
				
				needAdd = true;
			}
			
			if(cha == 0){//寻找时间点位 如：五分线点位  5 10 15 20 25 30 35 40
				
				lls.tool = cdb;//开始下一个tool
				//设置
				if(freq!=1){
//						log.info();
//						log.info("cdb_times: " + times + ": " + new java.util.Date(times).toLocaleString() + " ===================");
//						log.info("freq: " + freq + " ===================");
					
					
					if(StatAssistBean.getInstance(freq).records.size()!=0){
						
//							StatAssistBean.printRecords(freq);
						
						lls.tool = cdb.clone();
						lls.tool.setTimes(times);	//统计到前一个时段，如5，6，7，8，9 统计到5
						lls.tool.setOpen(StatAssistBean.getInstance(freq).getOpen());
						lls.tool.setClose(StatAssistBean.getInstance(freq).getClose());
						lls.tool.setHigh(StatAssistBean.getInstance(freq).getHigh());
						lls.tool.setLow(StatAssistBean.getInstance(freq).getLow());
						lls.tool.setMiddle(StatAssistBean.getInstance(freq).getMiddle());
						lls.tool.setTotalMoney(StatAssistBean.getInstance(freq).getTotalMoney());
						lls.tool.setTotalNumber(StatAssistBean.getInstance(freq).getTotalNumber());
						
//						log.info("freq: " + freq + " STAT: \n" + lls.tool);
						
					}else{
						lls.tool = getLastFreqChartDataBean(freq).clone();
						lls.tool.setOpen(getLastFreqChartDataBean(freq).getOpen());
						lls.tool.setClose(getLastFreqChartDataBean(freq).getClose());
						lls.tool.setHigh(getLastFreqChartDataBean(freq).getHigh());
						lls.tool.setLow(getLastFreqChartDataBean(freq).getLow());
						lls.tool.setMiddle(getLastFreqChartDataBean(freq).getMiddle());
						lls.tool.setTotalMoney(BigDecimal.ZERO);
						lls.tool.setTotalNumber(BigDecimal.ZERO);
						lls.tool.setTimes(times);
					}
					
					StatAssistBean.getInstance(freq).reset();
					lastFreqChartDataBeans.put(freq, lls.tool);//保存上一个
				}
				
				needAdd = true;
			}
			
			if(needAdd){
				ChartDataBean last = null;
				
				if(!lls.lists.isEmpty()){
					last = lls.lists.getLast();
				}
				
				if(last != null && last.getTimes() == lls.tool.getTimes()){//重设last值
					last.setOpen(lls.tool.getOpen());
					last.setClose(lls.tool.getClose());
					last.setHigh(lls.tool.getHigh());
					last.setLow(lls.tool.getLow());
					last.setMiddle(lls.tool.getMiddle());
					last.setTotalMoney(lls.tool.getTotalMoney());
					last.setTotalNumber(lls.tool.getTotalNumber());
				}else{
					if (lls.lists.size() < lls.max) {
						lls.lists.add(lls.tool);// 添加上一个tool
					} else {
						lls.lists.poll();// 移除第一个
						lls.lists.offer(lls.tool);// 添加新的
					}
				}
			}
			
			//非1时段的
			if(freq != 1){
				stat = StatAssistBean.getInstance(freq);
				stat.add(cdb);
				
				ChartDataBean last = null;
				
				if(!lls.lists.isEmpty()){
					last = lls.lists.getLast();
				}
				if(last != null && last.getTimes() == lls.tool.getTimes()){//重设last值
					last.setOpen(StatAssistBean.getInstance(freq).getOpen());
					last.setClose(StatAssistBean.getInstance(freq).getClose());
					last.setHigh(StatAssistBean.getInstance(freq).getHigh());
					last.setLow(StatAssistBean.getInstance(freq).getLow());
					last.setMiddle(StatAssistBean.getInstance(freq).getMiddle());
					last.setTotalMoney(StatAssistBean.getInstance(freq).getTotalMoney());
					last.setTotalNumber(StatAssistBean.getInstance(freq).getTotalNumber());
				}
			}
			initChartMap.put(freq, lls);
		}
	}
	
	
}



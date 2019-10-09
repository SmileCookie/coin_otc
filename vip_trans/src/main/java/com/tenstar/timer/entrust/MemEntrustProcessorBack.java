//package com.tenstar.timer.entrust;
//
//import com.alibaba.fastjson.JSON;
//import com.tenstar.Info;
//import com.tenstar.SystemStatus;
//import com.tenstar.TimeUtil;
//import com.world.cache.Cache;
//import com.world.data.mysql.Data;
//import com.world.data.mysql.OneSql;
//import com.world.model.Market;
//import com.world.model.daos.chart.ChartManager;
//import com.world.model.entity.LegalTenderType;
//import com.world.model.entitys.entrust.EntrustBean;
//import com.world.model.entitys.summary.TransactionSummary;
//import com.world.util.string.StringUtil;
//import org.apache.log4j.Logger;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.NavigableMap;
//import java.util.TreeMap;
//
///***
// * 内存交易撮合引擎
// * @author apple
// *
// */
//public class MemEntrustProcessorBack {
//	public static Logger log = Logger.getLogger(MemEntrustProcessorBack.class);
//	public static long times=System.currentTimeMillis();
//	public static boolean doOne=false;
//
//	private final static String sellSql = "select * from entrust where types=0 and completeNumber<numbers and status=3  order by unitPrice asc,entrustId asc";
//	private final static String buySql = "select * from entrust where  types=1 and completeNumber<numbers and status=3  order by unitPrice desc,entrustId asc";
//
//	private final static String entrustSql = "select * from entrust where  entrustId=? and completeNumber<numbers and status=3";
//
//	private final static String noMatchEntrustSql = "select * from entrust where status=0 order by entrustId asc";
//
//	private final static String CACHE_EID = "cjson_eid_";//缓存单个委托
//	private final static int CACHE_EID_TIME = 2 * 60 * 60;
//
//	private static long lastAddNoMatchEntrustSeconds = 0;//最后一次添加未成交委托单的时间
//	private final static long refreshNoMatchEntrustFrequency = 20 * 60 * 1000;
//
//
//	//定义全局存放各币种的委托Map<币种,treeMap>
//	private static Map<String, TreeMap<BigDecimal, TreeMap<Long,EntrustRecord>>> marketsBuyEntrustMap = new HashMap<String, TreeMap<BigDecimal, TreeMap<Long,EntrustRecord>>>();
//
//	private static Map<String, TreeMap<BigDecimal, TreeMap<Long,EntrustRecord>>> marketsSellEntrustMap = new HashMap<String, TreeMap<BigDecimal, TreeMap<Long,EntrustRecord>>>();
//
//	private static Map<String, TreeMap<Long, EntrustRecord>> marketsNoMatchingsMap = new HashMap<String, TreeMap<Long, EntrustRecord>>();
//
//	private static Map<String,Boolean> initMap = new HashMap<String,Boolean>();
//
//	//缓存盘口200条委托
//	public static Map<String, String[][]> marketsBuyEntrustArr200 = new HashMap<String, String[][]>();
//
//	public static Map<String, String[][]> marketsSellEntrustArr200 = new HashMap<String, String[][]>();
//
//
//	public static boolean isInit(Market m){
//		return initMap.get(m.market);
//	}
//
//	static{
//		Iterator<Entry<String,Market>> iter = Market.markets.entrySet().iterator();
//		while(iter.hasNext()){
//			Market m = iter.next().getValue();
//			if(m.listenerOpen){
//				init(m);
//			}
//		}
//	}
//
//
//
//	/**
//	 * 返回200档数据 买盘
//	 * @param market 币种市场名称
//	 * @return 字符串二维数组
//	 * @author zhanglinbo 20170119
//	 */
//	public static String[][] getBuyEntrustMap(String market){
//		return 	marketsBuyEntrustArr200.get(market);
//	}
//
//	/**
//	 * 返回200档数据 卖盘
//	 * @param market 币种市场名称
//	 * @return 字符串二维数组
//	 * @author zhanglinbo 20170119
//	 */
//	public static String[][] getSellEntrustMap(String market){
//		return 	marketsSellEntrustArr200.get(market);
//	}
//
//	private static boolean ifAddNoMatchFromMysql(){
//		if((System.currentTimeMillis() - lastAddNoMatchEntrustSeconds) > refreshNoMatchEntrustFrequency){
//			return true;
//		}
//		return false;
//	}
//
//	/***
//	 * 获取未撮合委托
//	 * @return
//	 */
//	public static EntrustRecord getNoMatchingEntrust(Market m){
//		if(ifAddNoMatchFromMysql()){
//			loadNoMatchEntrustFromDB(m);
//		}
//		EntrustRecord er = null;
//		TreeMap<Long, EntrustRecord> noMatchings = marketsNoMatchingsMap.get(m.market);
//		try {
//			if(noMatchings != null && noMatchings.size() > 0){
//				Entry<Long, EntrustRecord> erEntry = noMatchings.firstEntry();
//				if(erEntry != null){
//					er = erEntry.getValue();
//				}
//			 }
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//		}
//		if(er != null){///防止某个有问题的挂单导致停止撮合
//			er.setMatchTimes(er.getMatchTimes() + 1);
//			if(er.getMatchTimes() > 100){
//				noMatchings.remove(er.getId());
//				return null;
//			} else {
//                //从数据库里检查一遍数据，防止并发时导致重复处理 add by buxianguan
//                EntrustBean dbEntrust = (EntrustBean) Data.GetOne(m.db, "select * from entrust where entrustId=? and status=0",
//                        new Object[]{er.getId()}, EntrustBean.class);
//                if (null == dbEntrust) {
//                    noMatchings.remove(er.getId());
//                    return null;
//                }
//            }
//        }
//		return er;
//	}
//
//	public synchronized static boolean addNoMatchingEntrust(EntrustRecord er,Market m){
//		try {
//			TreeMap<Long, EntrustRecord> noMatchings = marketsNoMatchingsMap.get(m.market);
//			if(noMatchings == null){
//				noMatchings = new TreeMap<Long, EntrustRecord>();
//				marketsNoMatchingsMap.put(m.market,noMatchings);
//			}
//			noMatchings.put(er.getId(), er);
//			setCacheForEntrustRecord(er,m);
//
//			return true;
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//			return false;
//		}
//	}
//
//	private static void setCacheForEntrustRecord(EntrustRecord beb,Market m){
//		if(beb.getTypes() >= 0){//非取消命令委托
//			try {
//				//entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status
//				StringBuilder sb=new StringBuilder();
//		        sb.append(",["+beb.getId()+","+Market.formatMoney(beb.getPrice(),m)+","+
//		                	Market.formatNumber(beb.getSrcNumbers(),m)+","+Market.formatNumber(beb.getCompleteNumber(),m)+","+
//		                	Market.formatMoneyAndNumber(beb.getCompleteTotalMoney(),m)+","+beb.getTypes()+","+beb.getSubmitTime()+","+beb.getStatus()+","+beb.getFeeRate()+"]");
//		        String rtn = sb.toString();
//
//		        if(rtn.length()==0)
//		        	rtn= "\"record\":[]";
//		        else
//		        	rtn= "\"record\":["+rtn.substring(1)+"]";
//
//				Cache.SetObjNoReply(CACHE_EID + m.market + "_" + beb.getId(), rtn, CACHE_EID_TIME);
//			} catch (Exception e) {
//				log.error(e.toString(), e);
//			}
//		}
//	}
//
//
//	public static void setCacheForEntrustRecord(long id, String rtn,Market m){
//		Cache.SetObjNoReply(CACHE_EID + m.market + "_" + id, rtn, CACHE_EID_TIME);
//	}
//
//	public synchronized static boolean containsNoMatchingEntrust(long id,Market m){
//		try {
//			return marketsNoMatchingsMap.get(m.market).containsKey(id);
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//			return false;
//		}
//	}
//
//	public synchronized static boolean removeNoMatchingEntrust(long id,Market m){
//		try {
//			marketsNoMatchingsMap.get(m.market).remove(id);
//			//noMatchings.remove(id);
//			return true;
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//			return false;
//		}
//	}
//
//	//添加未撮合委托单
//	public static synchronized void loadNoMatchEntrustFromDB(Market m){
//		if(ifAddNoMatchFromMysql()){
//			lastAddNoMatchEntrustSeconds = System.currentTimeMillis();
//			log.info(m.market+"查找未匹配过的委托单......");
//			//未撮合委托单
//			List<EntrustBean> noMathchEntrusts = Data.QueryT(m.db,noMatchEntrustSql,new Object[] {}, EntrustBean.class);
//			log.info(m.market+"查找未匹配过的委托单......发现了：" + noMathchEntrusts.size() + "个。");
//			if(noMathchEntrusts.size() > 0){
//				for(EntrustBean eb : noMathchEntrusts){
//					EntrustRecord er = new EntrustRecord(eb.getEntrustId(), eb.getUnitPrice(), eb.getNumbers().subtract(eb.getCompleteNumber()), eb.getWebId(),eb.getUserId(), eb.getTypes(),
//							eb.getNumbers(),eb.getCompleteNumber(),eb.getCompleteTotalMoney(),eb.getSubmitTime(),eb.getStatus(),eb.getFeeRate());
//					er.setFreezId(eb.getFreezeId());
//					addNoMatchingEntrust(er,m);
//				}
//			}
//		}else{
//			log.info(m.market+"并发查询未成交的委托单，此次无需查找了......");
//		}
//	}
//	/**
//	 * 获取最新买一卖一
//	 * @return
//	 */
//	public static synchronized BigDecimal[] getBuyOneAndSellOne(Market m){
//		if(initMap.containsKey(m.market) && initMap.get(m.market)){
//			BigDecimal sellOne = BigDecimal.ZERO;
//			BigDecimal buyOne = BigDecimal.ZERO;
//			TreeMap<BigDecimal, TreeMap<Long,EntrustRecord>> buyEntrustsMap  = marketsBuyEntrustMap.get(m.market);
//			TreeMap<BigDecimal, TreeMap<Long,EntrustRecord>> sellEntrustsMap  = marketsSellEntrustMap.get(m.market);
//
//			if(sellEntrustsMap != null && sellEntrustsMap.size() > 0){
//				sellOne = sellEntrustsMap.firstKey();
//			}
//			if(buyEntrustsMap != null && buyEntrustsMap.size() > 0){
//				buyOne = buyEntrustsMap.firstKey();
//			}
//			log.info("[买一卖一价格] 市场:" + m.market + ", 买一价:"+ buyOne +", 卖一价:" + sellOne);
//			return new BigDecimal[]{buyOne, sellOne};
//		}
//		return null;
//	}
//
//	private static synchronized void init(Market m){
//		log.info(m.market+"开始初始化交易盘数据......");
//		long s1 = System.currentTimeMillis();
//
//		TreeMap<Long, EntrustRecord> noMatchings = marketsNoMatchingsMap.get(m.market);//未撮合委托单
//		if(noMatchings==null){
//			noMatchings = new TreeMap<Long, EntrustRecord> ();
//			marketsNoMatchingsMap.put(m.market, noMatchings);
//		}
//
//		TreeMap<BigDecimal, TreeMap<Long,EntrustRecord>> buyEntrustsMap = marketsBuyEntrustMap.get(m.market);
//		if(buyEntrustsMap == null){
//			 buyEntrustsMap = new TreeMap<BigDecimal, TreeMap<Long,EntrustRecord>>(new Comparator<BigDecimal>() {
//				@Override
//				public int compare(BigDecimal o1, BigDecimal o2) {
//					return o2.compareTo(o1);
//				}
//			});//按价格从高到低排序
//			marketsBuyEntrustMap.put(m.market, buyEntrustsMap);
//		}
//		TreeMap<BigDecimal, TreeMap<Long,EntrustRecord>> sellEntrustsMap  = marketsSellEntrustMap.get(m.market);
//		if(sellEntrustsMap==null){
//			sellEntrustsMap = new TreeMap<BigDecimal, TreeMap<Long,EntrustRecord>>();//按价格从低到高排序
//			marketsSellEntrustMap.put(m.market, sellEntrustsMap);
//		}
//		//清空数据
//		noMatchings.clear();
//		buyEntrustsMap.clear();
//		sellEntrustsMap.clear();
//
//		//未撮合委托单
//		loadNoMatchEntrustFromDB(m);
//
//
//		//找到第一个合适的价格
//		List<EntrustBean> buyEntrusts = Data.QueryT(m.db,buySql,new Object[] {}, EntrustBean.class);
//		if(buyEntrusts.size() > 0){
//			for(EntrustBean eb : buyEntrusts){
//				BigDecimal price = eb.getUnitPrice();
//				TreeMap<Long,EntrustRecord> trans = buyEntrustsMap.get(price);//可成交价列表
//				EntrustRecord newEntrust = new EntrustRecord(eb.getEntrustId(), price, eb.getNumbers().subtract(eb.getCompleteNumber()), eb.getWebId(),eb.getUserId(), eb.getTypes(),
//						eb.getNumbers(),eb.getCompleteNumber(),eb.getCompleteTotalMoney(),eb.getSubmitTime(),eb.getStatus(),eb.getFeeRate());
//				if(trans == null){//委托价格比最低的卖单小 无法成交
//					trans = new TreeMap<Long,EntrustRecord>();
//					trans.put(eb.getEntrustId(), newEntrust);
//					buyEntrustsMap.put(price, trans);
//				}else{//撮合成交
//					trans.put(eb.getEntrustId(), newEntrust);
//				}
//			}
//		}
//
//		List<EntrustBean> sellEntrusts = Data.QueryT(m.db,sellSql,new Object[] {}, EntrustBean.class);
//		if(sellEntrusts.size() > 0){
//			for(EntrustBean eb : sellEntrusts){
//				BigDecimal price = eb.getUnitPrice();
//				TreeMap<Long,EntrustRecord> trans = sellEntrustsMap.get(price);//可成交价列表
//				EntrustRecord newEntrust = new EntrustRecord(eb.getEntrustId(), price, eb.getNumbers().subtract(eb.getCompleteNumber()), eb.getWebId(),eb.getUserId(), eb.getTypes(),
//						eb.getNumbers(),eb.getCompleteNumber(),eb.getCompleteTotalMoney(),eb.getSubmitTime(),eb.getStatus(),eb.getFeeRate());
//				//EntrustRecord newEntrust = new EntrustRecord(eb.getEntrustId(), price, eb.getNumbers()-eb.getCompleteNumber(), eb.getWebId(),eb.getUserId(), eb.getTypes());
//				if(trans == null){//委托价格比最低的卖单小 无法成交
//					trans = new TreeMap<Long,EntrustRecord>();
//					trans.put(eb.getEntrustId(), newEntrust);
//					sellEntrustsMap.put(price, trans);
//				}else{//撮合成交
//					trans.put(eb.getEntrustId(), newEntrust);
//				}
//			}
//		}
//		resetPanEntrustArr200(m.market);
//		initMap.put(m.market, true);
//		long s6 = System.currentTimeMillis();
//		log.info(m.market+"开始初始化交易盘数据完成共耗时：" + (s6 - s1));
//		log.info(m.market+"买盘数据价格区间长度：" + (buyEntrustsMap.size()) + ",委托单数量：" + buyEntrusts.size());
//		log.info(m.market+"卖盘数据价格区间长度：" + (sellEntrustsMap.size()) + ",委托单数量：" + sellEntrusts.size());
//	}
//
//	/***
//	 * 修复内存操作
//	 * @param er
//	 */
//	private static synchronized void refreshCurrentEntrust(EntrustRecord er,Market m){
//		log.error("修复委托单：" + er.getId());
//		EntrustBean eb = Data.GetOneT(m.db,entrustSql, new Object[]{er.getId()}, EntrustBean.class);
//		BigDecimal price = er.getPrice();
//		TreeMap<BigDecimal, TreeMap<Long,EntrustRecord>> buyEntrustsMap  = marketsBuyEntrustMap.get(m.market);
//		TreeMap<BigDecimal, TreeMap<Long,EntrustRecord>> sellEntrustsMap  = marketsSellEntrustMap.get(m.market);
//		TreeMap<Long, EntrustRecord> records = null;
//		if(er.getTypes()==1){
//			records = buyEntrustsMap.get(price);
//		}else if(er.getTypes()==0){
//			records = sellEntrustsMap.get(price);
//		}
//
//		if(eb == null){
//		    if(records != null && records.size() > 0){
//			    try {
//					records.remove(er.getId());
//				} catch (Exception e) {
//					log.error(e.toString(), e);
//				}
//		    }
//		}else{
//			if(records == null){
//				records = new TreeMap<Long,EntrustRecord>();
//		    }
//			EntrustRecord newEntrust = new EntrustRecord(eb.getEntrustId(), price, eb.getNumbers().subtract(eb.getCompleteNumber()), eb.getWebId(),eb.getUserId(), eb.getTypes(),
//					eb.getNumbers(),eb.getCompleteNumber(),eb.getCompleteTotalMoney(),eb.getSubmitTime(),eb.getStatus(),eb.getFeeRate());
//			records.put(er.getId(), newEntrust);
//			setCacheForEntrustRecord(newEntrust,m);
//		}
//		removeNoMatchingEntrust(er.getId(),m);
//	}
//
//	/***
//	 * 处理新的买单
//	 * @param eb
//	 */
//	public synchronized static Info doEntrust(EntrustRecord eb,Market m){
//		if(!initMap.get(m.market)){
//			init(m);
//		}
//		Info ifo = null;
//		BigDecimal price = eb.getPrice();
//		TreeMap<BigDecimal, TreeMap<Long,EntrustRecord>> sellEntrustsMap = marketsSellEntrustMap.get(m.market);
//		TreeMap<BigDecimal, TreeMap<Long,EntrustRecord>> buyEntrustsMap = marketsBuyEntrustMap.get(m.market);
//
//		NavigableMap<BigDecimal, TreeMap<Long,EntrustRecord>> canTrans = null;
//		if(eb.getTypes() == 1){
//			canTrans = sellEntrustsMap.headMap(price, true);
//		}else{
//			canTrans = buyEntrustsMap.headMap(price, true);
//		}
//
//		if(canTrans == null || canTrans.size() <= 0){//无法成交
//			ifo = noTrans(eb,m);
//
//			if(ifo != null){//处理状态并刷新用户缓存纪录
//				EntrustRecord newEntrust = eb;//new EntrustRecord(eb.getEntrustId(), price, eb.getNumbers() - eb.getCompleteNumber(), eb.getWebId(),eb.getUserId(),eb.getTypes());
//				TreeMap<Long,EntrustRecord> records = null;//当前价位下的买单
//				if(eb.getTypes() == 1){
//					records = buyEntrustsMap.get(price);//当前价位下的买单
//				}else if(eb.getTypes() == 0){
//					records = sellEntrustsMap.get(price);//当前价位下的卖单
//				}
//				if(records == null){
//					records = new TreeMap<Long,EntrustRecord>();
//					records.put(eb.getId(), newEntrust);
//					if(eb.getTypes() == 1){
//						buyEntrustsMap.put(price, records);
//					}else if(eb.getTypes() == 0){
//						sellEntrustsMap.put(price, records);
//					}
//				}else{
//					records.put(eb.getId(), newEntrust);
//				}
//			}else{
//				ifo = Info.DoCancleFaildNoOrder;
//			}
//		}else{//撮合成交
//			ifo = doTrans(canTrans, eb,m);
//		}
//
//		return ifo;
//	}
//
//	/***
//	 * 撮合成交
//	 * @param canTrans
//	 * @param beb
//	 */
//	private static Info doTrans(NavigableMap<BigDecimal, TreeMap<Long,EntrustRecord>> canTrans, EntrustRecord beb,Market m){
//
//		Entry<BigDecimal, TreeMap<Long,EntrustRecord>> entry = canTrans.firstEntry();
//		long start0 = System.currentTimeMillis();
//		log.info("[撮合交易] 开始撮合交易：" + beb.getId());
//		while(entry != null){
//			EntrustRecord er = null;
//			BigDecimal canPrice = entry.getKey();
//			TreeMap<Long,EntrustRecord> records = entry.getValue();
//			if(records != null && records.size() > 0){
//				Entry<Long, EntrustRecord> firstEntry = records.firstEntry();
//				er = firstEntry.getValue();
//			}
//			while(er != null){
//				if(beb.getTypes() == 1 && er.getPrice().compareTo(beb.getPrice()) > 0){//卖价必须小于等于买价
//					return Info.DueEntrustSuccessUnDo;
//				}
//
//				if(beb.getTypes() == 0 && er.getPrice().compareTo(beb.getPrice()) < 0){//卖价必须小于等于买价
//					return Info.DueEntrustSuccessUnDo;
//				}
//				BigDecimal numbers = beb.getNumber();
//				//说明可以成交，测试是否有计划委托，如果有就更新并跳出本次循环更改计划委托状态，从而让计划委托提前进入交易流程
//				//如果自己的价格有重叠会跳过计划委托，等待别人成交
//				//对于卖，说明价格下降，寻找卖出的计划委托抢先卖
//
//				BigDecimal nextNumbers = er.getNumber();
//				// 本次交易的btc
//				BigDecimal thisNumbers = numbers;
//				if (numbers.compareTo(nextNumbers)>0) {
//					thisNumbers = nextNumbers;
//				}
//				//本次交易的钱
////				BigDecimal thisMoney =Market.formatTotalMoney(er.getPrice(),thisNumbers);
//				// update by suxinjie JYPT-1489 修改相乘超过9位小数问题
//				BigDecimal thisMoney =Market.totalMoney(er.getPrice(),thisNumbers);
//				List<OneSql> sqls =  new ArrayList<>();
//
//				int status0 = 3;
//				if(numbers.compareTo(nextNumbers)<=0){
//					status0=2;
//				}
//				//更改当前tricker委托方的状态
//
//				/***
//				 * + status in(0,3) 2014-10-11 21:21   填补漏洞 ，否则可能出现取消的委托被生成  transrecord
//				 */
//				sqls.add(new OneSql(
//						"update entrust set status=?,completeNumber=completeNumber+?,completeTotalMoney=completeTotalMoney+? where entrustId=? and completeNumber+?<=numbers and status in(0,3)",
//						1, new Object[] { status0,thisNumbers, thisMoney, beb.getId(), thisNumbers},m.db));
//
//				int status = 3;
//				if(numbers.compareTo(nextNumbers)>=0){
//					status=2;
//				}
//				//更改被动原maker委托方的状态
//				sqls.add(new OneSql(
//						"update entrust set status=?,completeNumber=completeNumber+?,completeTotalMoney=completeTotalMoney+? where entrustId=? and completeNumber+?<=numbers and status in(0,3)",
//						1, new Object[] {status, thisNumbers, thisMoney,er.getId(),thisNumbers},m.db));
//
//				long userId;
//
//				//产生记录
//			 	if(beb.getTypes()==1){
//					sqls.add(new OneSql(
//							"INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell,actStatus) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
//							1, new Object[] {
//									er.getPrice(),
//									thisMoney,
//									thisNumbers,
//									beb.getId(),
//									beb.getUserId(),
//									er.getId(),
//									er.getUserId(),
//									beb.getTypes(),//当前记录是买行为还是卖行为
//									TimeUtil.getNow().getTime(),
//									TimeUtil.getMinuteFirst().getTime(),
//									beb.getWebId(),
//									er.getWebId(),
//									1
//							},m.db));
//
//					userId = beb.getUserId();
//
//				}else{
//					sqls.add(new OneSql(
//							"INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell,actStatus) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
//							1, new Object[] {
//									er.getPrice(),
//									thisMoney,
//									thisNumbers,
//									er.getId(),
//									er.getUserId(),
//									beb.getId(),
//									beb.getUserId(),
//									beb.getTypes(),//当前记录是买行为还是卖行为
//									TimeUtil.getNow().getTime(),
//									TimeUtil.getMinuteFirst().getTime(),
//									er.getWebId(),
//									beb.getWebId(),
//									1
//							},m.db));
//				}
//				//上面问号的意思是没卖双方在这里可能没有判断好，可能引起问题，时间不足
//				long start = System.currentTimeMillis();
//				if(Data.doTrans(sqls)) {
//					BigDecimal erPrice = er.getPrice();
//					int erUserId = er.getUserId();
//
//					// 交易摘要从这里获取初始价格,数量,下面不同的情景设置不同的交易类型和用户ID即可
//					EntrustRecord erBak = new EntrustRecord();
//					erBak.setCompleteNumber(thisNumbers);
//					erBak.setPrice(er.getPrice());
//
//					if(status == 2){//被动委托单已完成
//						er.setStatus(2);
//						er.setCompleteNumber(er.getCompleteNumber().add(thisNumbers));
//						er.setCompleteTotalMoney(er.getCompleteTotalMoney().add(thisMoney));
//
//						// 交易摘要,被动方处理
//						erBak.setTypes(er.getTypes());
//						erBak.setUserId(er.getUserId());
//						updateTransactionSymmary(erBak, m);
//
//						records.remove(er.getId());//当前委托单已经成交完了，删掉
//						if(records.size()<=0){
//							canTrans.remove(canPrice);
//						}
//
//						setCacheForEntrustRecord(er,m);
//						if(!records.isEmpty()){
//							if(records != null && records.size() > 0){
//								Entry<Long, EntrustRecord> firstEntry = records.firstEntry();
//								er = firstEntry.getValue();
//							}
//						}else{
//							er = null;
//						}
//					}else{
//						er.setNumber(er.getNumber().subtract(thisNumbers));
//						er.setCompleteNumber(er.getCompleteNumber().add(thisNumbers));
//						er.setCompleteTotalMoney(er.getCompleteTotalMoney().add(thisMoney));
//						setCacheForEntrustRecord(er,m);
//
//						// 交易摘要,被动方处理
//						erBak.setTypes(er.getTypes());
//						erBak.setUserId(er.getUserId());
//						updateTransactionSymmary(erBak, m);
//					}
//
//					//更新循环内存中的数据
//					beb.setNumber(beb.getNumber().subtract(thisNumbers));
//					beb.setCompleteNumber(beb.getCompleteNumber().add(thisNumbers));
//					beb.setCompleteTotalMoney(beb.getCompleteTotalMoney().add(thisMoney));
//
//					long start1 = System.currentTimeMillis();
//					log.info("[撮合交易] 处理委托任务总耗时:" + (start1 - start0) + ", 事物提交耗时:" + (start1 - start));
//					MemEntrustMatchProcessor.da.UpdateEntrust(erPrice, thisNumbers, beb.getTypes(),m);
//
//					//更新单个用户成交记录
//
//					//更新卖一
//					//List l=(List)Data.GetOne("select max(transrecordId) from transrecord", new Object[]{});
//					//long transrecordId=(l==null||l.get(0)==null)?0:Long.parseLong(l.get(0).toString());
//					//更新队列
//					//MemEntrustMatchProcessor.da.updateRecord(transrecordId,erPrice,thisNumbers,beb.getTypes(),TimeUtil.getNow().getTime());
//					MemEntrustMatchProcessor.da.getTop(erUserId,m);
//					MemEntrustMatchProcessor.da.getTop(beb.getUserId(),m);
//					log.debug(m.market+"执行一次交易成功");
//					log.info("[撮合交易] 处理市场:" + m.market+  " 委托任务的事物耗时(后续任务耗时):" + (System.currentTimeMillis() - start1));
//					if (numbers.compareTo(thisNumbers) > 0) {//只要还有没买完毕的
//						start1 = System.currentTimeMillis();
//						log.info("[撮合交易] 处理市场:" + m.market+" 委托任务的事物耗时getTop耗时:" + (System.currentTimeMillis() - start1));
//						setCacheForEntrustRecord(beb,m);
//
//						// 交易摘要,主动方处理
//						erBak.setTypes(beb.getTypes());
//						erBak.setUserId(beb.getUserId());
//						updateTransactionSymmary(erBak, m);
//
//						continue;
//					}else{
//						beb.setStatus(2);
//						removeNoMatchingEntrust(beb.getId(),m);
//						start1 = System.currentTimeMillis();
//						log.info("[撮合交易] 处理市场:" + m.market+" 委托任务的事物耗时getTop耗时:" + (System.currentTimeMillis() - start1));
//						setCacheForEntrustRecord(beb,m);
//
//						// 交易摘要,主动方处理
//						erBak.setTypes(beb.getTypes());
//						erBak.setUserId(beb.getUserId());
//						updateTransactionSymmary(erBak, m);
//
//						return Info.DueEntrustSuccess;
//					}
//
//				} else {
//					///处理失败，刷新一下当前价位下的挂单
//					log.debug("[撮合交易] 市场:" + m.market + " 执行事物失败重新设置当前档位内存");
//					refreshCurrentEntrust(beb,m);
//					refreshCurrentEntrust(er,m);
//					return Info.DueEntrustFaildProError;
//				}
//			}
//
//			if(er == null){//按下一个价位撮合成交
//				canTrans.remove(entry.getKey());
//				entry = canTrans.firstEntry();
//			}
//		}
//
//		if(entry == null && (beb.getNumber().compareTo(BigDecimal.ZERO) > 0)){//主动单  部分成交
//			TreeMap<BigDecimal, TreeMap<Long,EntrustRecord>> sellEntrustsMap = marketsSellEntrustMap.get(m.market);
//			TreeMap<BigDecimal, TreeMap<Long,EntrustRecord>> buyEntrustsMap = marketsBuyEntrustMap.get(m.market);
//			TreeMap<Long, EntrustRecord> records = null;
//			  if(beb.getTypes()==1){
//				  records = buyEntrustsMap.get(beb.getPrice());
//			  }else if(beb.getTypes()==0){
//				  records = sellEntrustsMap.get(beb.getPrice());
//			  }
//
//			  int rtn = Data.Update(m.db,"update Entrust set status=3 where entrustId=?", new Object[]{beb.getId()});
//			  if(rtn > -1){
//				  beb.setStatus(3);
//				  EntrustRecord newEntrust = new EntrustRecord(beb.getId(), beb.getPrice(), beb.getNumber(), beb.getWebId(),beb.getUserId(), beb.getTypes(),
//						  beb.getSrcNumbers(),beb.getCompleteNumber(),beb.getCompleteTotalMoney(),beb.getSubmitTime(),beb.getStatus(),beb.getFeeRate());
//				  if(records != null){
//					  records.put(beb.getId(), newEntrust);
//				  }else{
//					  records = new TreeMap<Long,EntrustRecord>();
//				  	  records.put(beb.getId(), newEntrust);
//				  	  if(beb.getTypes()==1){
//				  		  buyEntrustsMap.put(beb.getPrice(), records);
//					  }else if(beb.getTypes()==0){
//						  sellEntrustsMap.put(beb.getPrice(), records);
//					  }
//				  }
//				  removeNoMatchingEntrust(beb.getId(),m);
//				  setCacheForEntrustRecord(beb,m);
//				  MemEntrustMatchProcessor.da.getTop(beb.getUserId(),m);
//				  //MemEntrustMatchProcessor.da.UpdateEntrustNo(beb.getPrice(),beb.getNumber(),beb.getTypes());
//			  }
//			  return Info.DueEntrustSuccessUnDo;
//		}
//		return Info.DueEntrustSuccess;
//	}
//
//	/***
//	 * 取消单
//	 * @param eb
//	 */
//	private static void cancel(EntrustBean eb,Market m){
//		TreeMap<Long, EntrustRecord> records = null;
//		  if(eb.getTypes()==1){
//			  records = marketsBuyEntrustMap.get(m.market).get(eb.getUnitPrice());
//			  //  records = buyEntrustsMap.get(eb.getUnitPrice());
//		  }else if(eb.getTypes()==0){
//			  //records = sellEntrustsMap.get(eb.getUnitPrice());
//			  records = marketsSellEntrustMap.get(m.market).get(eb.getUnitPrice());
//		  }
//
//		  if(records != null && records.size() > 0){
//			  records.remove(eb.getEntrustId());
//		  }
//
//		  EntrustRecord er = new EntrustRecord(eb.getEntrustId(), eb.getUnitPrice(), eb.getNumbers().subtract(eb.getCompleteNumber()), eb.getWebId(),eb.getUserId(), eb.getTypes(),
//					eb.getNumbers(),eb.getCompleteNumber(),eb.getCompleteTotalMoney(),eb.getSubmitTime(),eb.getStatus(),eb.getFeeRate());
//		  setCacheForEntrustRecord(er,m);
//	}
//
//	/**
//	 * 取消委托
//	 * 委托表最终0 原始状态  1取消  2成功 3 交易一部分
//	 * @param beb
//	 * @return
//	 */
//public static Info cancle(EntrustRecord beb,Market m){
//    try{
//		EntrustBean bebNew=(EntrustBean)Data.GetOne(m.db,"select * from Entrust where entrustId=?", new Object[]{beb.getFreezId()},EntrustBean.class);
//		if(bebNew==null){
//			 log.info("错误，发现一个没有原始记录的取消");
//			 removeNoMatchingEntrust(beb.getId(),m);
//			 return Info.DueCancleFaildNoFouce;
//		}
//
//		//已经成功或者已经取消的时候 4已经处理过一次 5预取消  这里可以判定数量一定不能相等
//		if((bebNew.getStatus()==2||bebNew.getStatus()==1)||bebNew.getNumbers()==bebNew.getCompleteNumber()){//只要不是未处理或者未完全处理，就无需处理 -1是计划委托类型
//			log.info("已经处理完毕，所以无需操作");
//			int rtn1 = Data.Update(m.db,"update entrust set status=2 where entrustId=?", new Object[]{beb.getId()});
//			int rtn2 = Data.Update(m.db,"update entrust set status=1 where entrustId=? and status<>2 and status<>-1", new Object[]{bebNew.getEntrustId()});
//			//if((rtn1 > -1) && (rtn2 > -1)){
//				removeNoMatchingEntrust(beb.getId(),m);
//				log.info("移除委托：" + beb.getId());
//				bebNew.setStatus(1);
//				cancel(bebNew,m);
//			//}
//			return Info.DueCancleFaildHasDued;
//		}
//
//		List<OneSql> sqls = new ArrayList<OneSql>();
//		//取消目标，到这里，说明前面的肯定处理过了，所以状态一定//,TotalMoney=CompleteTotalMoney
//		//取消委托，判断被取消的委托是否有成交，如果有部分成交，取消后更新状态为2 ：已完成 ，如果没有成交，取消更新状态为1:已取消
//		int cancelStatus = 1;
//		if(bebNew.getCompleteNumber().compareTo(BigDecimal.ZERO)>0){
//			cancelStatus=2;
//		}
//		sqls.add(new OneSql("update entrust set status=? where entrustId=?",
//				1,
//				new Object[]{cancelStatus,bebNew.getEntrustId()} ,
//				m.db
//		));
//		//更新当前这个取消命令
//		sqls.add(new OneSql("update entrust set status=2 where entrustId=?",
//				1,
//				new Object[]{beb.getId()},
//				m.db
//		));
//		//已经交易完毕并且没有需要取消的
//		if(bebNew.getTotalMoney() == bebNew.getCompleteTotalMoney()){
//			int rtn = Data.Update(m.db,"update entrust set status=2 where entrustId=?",
//					  new Object[]{beb.getId()}
//					);
//			if(rtn > -1){
//				removeNoMatchingEntrust(beb.getId(),m);
//				bebNew.setStatus(2);
//				cancel(bebNew,m);
//			}
//			log.error("已经处理完毕资金，还要求取消："+bebNew.getTotalMoney()+":"+bebNew.getCompleteTotalMoney());
//			return Info.DueCancleFaildHasDued;
//		}
//		//产生记录
//		if(bebNew.getTypes()==1){
//			Object obj=Data.GetOne(m.db,"select * from transrecord where unitPrice=0 and numbers=0 and entrustIdBuy=?", new Object[]{bebNew.getEntrustId()});
//			if(obj==null){//对于买会存在多冻结的问题，所以可能已经产生了返还命令，然后这边如果再取消，相当与就多了一次
//				sqls.add(new OneSql(
//					"INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
//					1, new Object[] {
//					0,
//					bebNew.getTotalMoney().subtract(bebNew.getCompleteTotalMoney()),//剩余的所有钱   可能出现负职，因为总资金四舍五入的问题
//					bebNew.getNumbers().subtract(bebNew.getCompleteNumber()),//剩余的所有都取消
//					bebNew.getEntrustId(),//卖卖记录一样的
//					bebNew.getUserId(),
//					0,//买卖记录一样的
//					0,
//					bebNew.getTypes(),
//					TimeUtil.getNow().getTime(),
//					TimeUtil.getMinuteFirst().getTime(),
//					bebNew.getWebId(),
//					0
//				},m.db));
//			}
//		}else if(bebNew.getTypes()==0){
//			sqls.add(new OneSql(
//					"INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
//					1, new Object[]{
//							0,
//							0,//剩余的所有钱买房
//							bebNew.getNumbers().subtract(bebNew.getCompleteNumber()),//剩余的所有都取消
//							0,//卖卖记录一样的
//							0,
//							bebNew.getEntrustId(),//买卖记录一样的
//							bebNew.getUserId(),
//							bebNew.getTypes(),
//							TimeUtil.getNow().getTime(),
//							TimeUtil.getMinuteFirst().getTime(),
//							0,
//							bebNew.getWebId()
//					},
//					m.db ));
//			}
//
//		  if(Data.doTrans(sqls)){
//			  bebNew.setStatus(1);
//			  removeNoMatchingEntrust(beb.getId(),m);
//			  cancel(bebNew,m);
//
//			  MemEntrustMatchProcessor.da.getTop(beb.getUserId(),m);
//			  //MemEntrustMatchProcessor.da.updateCancleEntrust(bebNew.getUnitPrice(), bebNew.getNumbers()-bebNew.getCompleteNumber(),bebNew.getTypes()==1);
//			  return Info.DueCancleSuccess;
//		  }else{
//			  //移除掉不处理了  防止交易大盘停住
//			  removeNoMatchingEntrust(beb.getId(),m);
//			  return Info.DueCancleFaildPromError;
//		  }
//		}catch(Exception ex){
//			log.error(ex.toString(), ex);
//			return Info.DueCancleFaildPromError;
//		}
//
//	}
//
//
//	private static Info noTrans(EntrustRecord beb,Market m){
//		//说明没有符合条件的记录，更新记录为已经部分成交（其实本次成交为0）
//		try {
//			//long s2 = System.currentTimeMillis();
//			int rtn = Data.Update(m.db,"update entrust set status=3 where entrustId=? and status=0", new Object[]{beb.getId()});
//			if(rtn > -1){
//				beb.setStatus(3);
//				removeNoMatchingEntrust(beb.getId(),m);
//				//long s3 = System.currentTimeMillis();
//				//log.info("没有可成交单:" + (s3 - s2));
//				MemEntrustMatchProcessor.da.getTop(beb.getUserId(),m);
//				//MemEntrustMatchProcessor.da.UpdateEntrustNo(beb.getPrice(),beb.getNumber(),beb.getTypes());
//				setCacheForEntrustRecord(beb,m);
//			}
//			return Info.DueEntrustSuccessUnDo;
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//		}
//
//		return null;
//	}
//
//
//	public static Info processOne(Market m){
//		try{
//			   //所有新任务才会引起变化
//			   EntrustRecord er = getNoMatchingEntrust(m);
//			   if(er==null){
//				    //log.info("没有任务");
//					return Info.NoMission;
//			   }
////               log.info(m.market+"=========================================================");
//               log.info("[处理单条委托] 委托市场:" + m.market + ", 委托编号:" + er.getId());
////               log.info(m.market+"=========================================================");
//
//			if(er.getTypes()==-1 && er.getFreezId() > 0){
//				long start = System.currentTimeMillis();
//				Info i = cancle(er,m);//cancle(beb);
//				log.info("entrustProcessOne取消耗时：" + (System.currentTimeMillis() - start));
//				if(i==Info.DueCancleSuccess){//更新内存数据
//					//SystemStatus.moneyNewWork=true;
//					SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.moneyNewWork, true);
//					//SystemStatus.chartDataNewWork=true;
//					SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.chartDataNewWork, true);
//					//SystemStatus.exchangeNewWork=true;
//				}else{
//					//SystemStatus.chartDataNewWork=true;
//					SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.chartDataNewWork, true);
//					log.error("取消委托发生错误："+i.toString());
//				}
//				//重置盘口200档数据
//				resetPanEntrustArr200(m.market);
//				return i;
//			}else if(er.getTypes()==-1){
//				int rtn = Data.Update(m.db,"update entrust set status=2 where entrustId=?", new Object[]{er.getId()});
//				//if(rtn > -1){
//					er.setStatus(2);
//					removeNoMatchingEntrust(er.getId(),m);
//					setCacheForEntrustRecord(er,m);
//					log.error("有一个异常的取消命令！" + er.getId());
////				}else{
////					log.error("error:" + er.getId());
////				}
//				return Info.DueEntrustFaildUnKonwType;
//			}else if(er.getTypes()==0||er.getTypes()==1){
//				long start = System.currentTimeMillis();
//				Info i = doEntrust(er,m);//entrust(beb);
//				log.info("entrustProcessOne处理委托["+er.getId()+"]耗时：" + (System.currentTimeMillis() - start) + ",Info:" + i.getMessage());
//				if(i==Info.DueEntrustSuccess||i==Info.DueEntrustSuccessUnDo){//更新内存数据
//					//SystemStatus.moneyNewWork=true;
//					//SystemStatus.chartDataNewWork=true;
//					SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.moneyNewWork, true);
//					SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.chartDataNewWork, true);
//					//重置盘口200档数据
//					resetPanEntrustArr200(m.market);
//					//SystemStatus.exchangeNewWork=true;
//
//					//TODO 更新用户成交记录
//
//					// TODO: 2017/6/26 add by suxinjie 更新交易摘要
////					updateTransactionSymmary(er, m);
//				}
//				//通知更新委托视图
//				return i;
//			}else {
//				int rtn = Data.Update(m.db,"update Entrust set status=2 where EntrustId=?", new Object[]{+er.getId()});
//				if(rtn > -1){
//					er.setStatus(2);
//					removeNoMatchingEntrust(er.getId(),m);
//					setCacheForEntrustRecord(er,m);
//				}
//				log.error("有一个异常的type为"+er.getTypes()+"命令！");
//				return Info.DueError;
//			}
//
//		}catch(Exception ex){
//			log.error(ex.toString(), ex);
//			return Info.DueEntrustFaildUnKonwType;
//		}
//	}
//
//	/**
//	 * 重置盘口200档数据
//	 * @param market 币种名称
//	 * @author zhanglinbo 20170119
//	 */
//	public static void resetPanEntrustArr200(String market){
//		int sellIndex = 0;//计数下标
//		int buyIndex = 0;//计数下标
//		String[][]  buyArr  = new String[200][3];
//		String[][]  sellArr = new String[200][3];
//		long t1 =System.currentTimeMillis();
//		for(Entry<BigDecimal,TreeMap<Long,EntrustRecord>> entry : marketsSellEntrustMap.get(market).entrySet()){
//
//			BigDecimal priceKey = entry.getKey();
//			TreeMap<Long, EntrustRecord> v = entry.getValue();
//			BigDecimal snumber = BigDecimal.ZERO;
//			StringBuffer sellUserIdBuffer = new StringBuffer();
//			for(Entry<Long,EntrustRecord> sentry : v.entrySet()){
//				EntrustRecord er = sentry.getValue();
//				snumber =snumber.add((er.getSrcNumbers().subtract(er.getCompleteNumber())));
//				sellUserIdBuffer.append(er.getUserId()).append("a");
//			}
//			if(snumber.compareTo(BigDecimal.ZERO)<=0){
//				continue;
//			}
//			if(sellIndex<200){
//				String[] data = new String[3];
//				data[0] = String.valueOf(priceKey);
//				data[1] = String.valueOf(snumber);
//				data[2] = String.valueOf(sellUserIdBuffer.substring(0,sellUserIdBuffer.length()-1));
//				sellArr[sellIndex] =data;
//			}else{
//				break;
//			}
//			sellIndex++;
//		}
//		//保存到缓存
//		marketsSellEntrustArr200.put(market, sellArr);
//
//		for(Entry<BigDecimal,TreeMap<Long,EntrustRecord>> entry : marketsBuyEntrustMap.get(market).entrySet()){
//			BigDecimal priceKey = entry.getKey();
//			TreeMap<Long, EntrustRecord> v = entry.getValue();
//			BigDecimal snumber = BigDecimal.ZERO;
//			/*start by xzhang 20171215 交易页面三期PRD:添加用户ID*/
//			StringBuffer buyUserIdBuffer = new StringBuffer();
//			for(Entry<Long,EntrustRecord> sentry : v.entrySet()){
//				EntrustRecord er = sentry.getValue();
//				snumber = snumber.add(er.getSrcNumbers().subtract(er.getCompleteNumber()));
//				buyUserIdBuffer.append(er.getUserId()).append("a");
//			}
//			if(snumber.compareTo(BigDecimal.ZERO)<=0){
//				continue;
//			}
//
//			if(buyIndex<200){
//				String[] data = new String[3];
//				data[0] = String.valueOf(priceKey);
//				data[1] = String.valueOf(snumber);
//				data[2] = String.valueOf(buyUserIdBuffer.substring(0,buyUserIdBuffer.length()-1));
//				buyArr[buyIndex] =data;
//				/*end*/
//			}else{
//				break;
//			}
//			buyIndex++;
//		}
//		//保存到缓存
//		marketsBuyEntrustArr200.put(market, buyArr);
//		long t2 =System.currentTimeMillis();
//		//log.error("重置盘口200档数据耗时："+(t2-t1)+"毫秒。");
//	}
//
//	/**
//	 * 更新交易摘要
//	 * @param er 撮合对象
//     */
//	public static void updateTransactionSymmary(EntrustRecord er, Market m) {
//
//		/*start by xzhang 20171215 交易页面三期PRD:摘要折算法币*/
//		for (LegalTenderType tenderType : LegalTenderType.values()) {
//			BigDecimal transPrice = er.getPrice();
//			BigDecimal legalPrice = new BigDecimal(1);
//			String legal_convert = "";
//			if(m.getMarket().toLowerCase().contains("_usdt")){
//				if("USD".equals(tenderType.getKey())){
//					legalPrice = ChartManager.getPrice(m);
//					transPrice = transPrice;
//				}else{
//					legal_convert = StringUtil.exist(Cache.Get("usdt_"+tenderType.getKey().toLowerCase()))?Cache.Get("usdt_"+tenderType.getKey().toLowerCase()):"1";
//					legalPrice = ChartManager.getPrice(m).multiply(new BigDecimal(legal_convert));
//					transPrice = transPrice.multiply(new BigDecimal(legal_convert));
//				}
//			}else if(m.getMarket().toLowerCase().contains("_btc")){
//				if("USD".equals(tenderType.getKey())){
//					legal_convert = StringUtil.exist(Cache.Get("btc_usdt"))?Cache.Get("btc_usdt"):"1";
//					legalPrice = ChartManager.getPrice(m).multiply(new BigDecimal(legal_convert));
//					transPrice = transPrice.multiply(new BigDecimal(legal_convert));
//				}else{
//					legal_convert = StringUtil.exist(Cache.Get("btc_"+tenderType.getKey().toLowerCase()))?Cache.Get("btc_"+tenderType.getKey().toLowerCase()):"1";
//					legalPrice = ChartManager.getPrice(m).multiply(new BigDecimal(legal_convert));
//					transPrice = transPrice.multiply(new BigDecimal(legal_convert));
//				}
//			}else{
//				log.error("【交易摘要】尚未维护"+m.getMarket()+"该市场信息");
//				break;
//			}
////			record.setPrice(transPrice);
//			String summaryKey = "transaction_summary_" + m.getMarket().toLowerCase() +"_"+tenderType.getKey().toLowerCase()+ "_" + er.getUserId();
//			TransactionSummary transactionSummary;
//
//			//买为正,卖为负
//			BigDecimal num = er.getTypes() == 1 ? er.getCompleteNumber() : er.getCompleteNumber().negate();
//			BigDecimal currencyPrice = legalPrice;//ChartManager.getPrice(m);
//
//			String summaryJson = Cache.Get(summaryKey);
//			if (!StringUtil.exist(summaryJson)) {
//				transactionSummary = new TransactionSummary();
//				transactionSummary.setNum(num);
//				transactionSummary.setTransactionPrice(transPrice);
//				transactionSummary.setCost(num.multiply(transPrice));
//				transactionSummary.setNetAmount(num);
//				transactionSummary.setCostPrice(transPrice);
//
////			//盈亏 = | |市价| - |成本价| | * |净额|
////			transactionSummary.setProfitLoss(
////					currencyPrice.abs().subtract(transactionSummary.getCostPrice().abs()).abs()
////							.multiply(transactionSummary.getNetAmount().abs()));
//
//				Cache.Set(summaryKey, JSON.toJSONString(transactionSummary));
//				continue;
//			}
//
//			TransactionSummary ts = JSON.parseObject(summaryJson, TransactionSummary.class);
//
//			//净额=0的话会导致后续处理异常,所以盈亏用当前价格基于上一次的数据来计算
//			BigDecimal cost = ts.getCost().add(num.multiply(transPrice));
//			BigDecimal netAmount = ts.getNetAmount().add(num);
//
//			transactionSummary = new TransactionSummary();
//			transactionSummary.setNum(num);
//			transactionSummary.setTransactionPrice(transPrice);
//			transactionSummary.setCost(cost);
//			transactionSummary.setNetAmount(netAmount);
//
//			if (netAmount.compareTo(BigDecimal.ZERO) == 0) {
//				transactionSummary.setCostPrice(BigDecimal.ZERO);
////			//当 净额==0 的时候,盈亏=成本
////			transactionSummary.setProfitLoss(transactionSummary.getCost());
//
//				Cache.Set(summaryKey, JSON.toJSONString(transactionSummary));
//				continue;
//			}
//
//			transactionSummary.setCostPrice(cost.divide(netAmount, 5, BigDecimal.ROUND_HALF_EVEN));
//			transactionSummary.setProfitLoss(
//					currencyPrice.abs().subtract(ts.getCostPrice().abs()).abs()
//							.multiply(ts.getNetAmount().abs()));
//
//			Cache.Set(summaryKey, JSON.toJSONString(transactionSummary));
//		}
//		/*end*/
//	}
//}

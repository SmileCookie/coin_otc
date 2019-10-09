package com.tenstar.timer.entrust;

import com.tenstar.Info;
import com.tenstar.InfoEntrust;
import com.world.data.mysql.Data;
import com.world.model.Market;
import com.world.model.daos.chart.ChartManager;
import com.world.model.entitys.entrust.PlanEntrustBean;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

public class MemPlanEntrustProcessor {

	public static Logger log = Logger.getLogger(MemPlanEntrustProcessor.class);
	public static long times=System.currentTimeMillis();
	
	//计划买入 追高计划委托
	private final static String  highPlanSql = "select * from plan_entrust where types=1 and triggerPrice>0  and unitPrice>0 and  status=-1  order by triggerPrice asc,entrustId asc";
	
	//计划买入 抄底计划委托
	private final static String lowPlanSql = "select * from plan_entrust where types=1 and triggerPriceProfit>0 and unitPriceProfit>0 and status=-1  order by triggerPriceProfit desc,entrustId asc";
	
	//计划卖出  止损计划委托
	private final static String stopPlanSql = "select * from plan_entrust where  types=0 and triggerPrice >0 and unitPrice>0 and status=-1  order by triggerPrice desc,entrustId asc";
	
	//计划卖出  止赢计划委托
	private final static String profitPlanSql = "select * from plan_entrust where types=0 and triggerPriceProfit>0 and unitPriceProfit>0 and  status=-1  order by triggerPriceProfit asc,entrustId asc";
		
	//定义全局存放各币种的委托Map<币种,treeMap>
	//追高计划委托集合
	private static Map<String,TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>>> highPlanEntrustsMap = new HashMap<String, TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>>>();//按价格从低到高排序
	//抄底计划委托集合
	private static Map<String,TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>>> lowPlanEntrustsMap = new HashMap<String, TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>>>();//按价格从高到低排序
	
	//止盈计划委托集合
	private static Map<String, TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>>> profitPlanEntrustsMap = new HashMap<String, TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>>>();//按价格从低到高排序
	//止损计划委托集合
	private static Map<String, TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>>> stopPlanEntrustsMap = new HashMap<String, TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>>>();//按价格从高到低排序
	
	
	//初始化标识
	private static Map<String,Boolean> initMap = new HashMap<String,Boolean>();
	
	public static boolean isInit(Market m){
		return initMap.get(m.market);
	}
	
	static{
		Iterator<Entry<String,Market>> iter = Market.markets.entrySet().iterator();
		while(iter.hasNext()){
			Market m = iter.next().getValue();
			if(m.listenerOpen){
				init(m);
			}
			
		}
		
	}
	
	/**
	 * 类初始化加载数据到缓存
	 */
	public static void init(Market m){
		long s1 = System.currentTimeMillis();//开始时间
		
		//追高买进集合
		TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> buyHighPlanEntrustsMap =  getBuyHighPlanEntrustsMap(m);
		//抄底买进集合
		TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> buyLowPlanEntrustsMap  = getBuyLowPlanEntrustsMap(m);
		
		//高价卖出止盈集合
		TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> sellHighPlanEntrustsMap = getSellHighPlanEntrustsMap(m);
		
		//低价卖出止损集合
		TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> sellLowPlanEntrustsMap  = getSellLowPlanEntrustsMap(m);
		
		
		buyHighPlanEntrustsMap.clear();
		buyLowPlanEntrustsMap.clear();
		
		sellHighPlanEntrustsMap.clear();
		sellLowPlanEntrustsMap.clear();
		
		//获取追高计划买入记录，以触发价为键
		List<PlanEntrustBean> highPlanEntrusts = Data.QueryT(m.db,highPlanSql,new Object[] {}, PlanEntrustBean.class);
		if(highPlanEntrusts.size() > 0){
			for(PlanEntrustBean eb : highPlanEntrusts){
				BigDecimal triggerPrice  = eb.getTriggerPrice();
				TreeMap<Long,PlanEntrustRecord> planTrans = buyHighPlanEntrustsMap.get(triggerPrice);//可扫描价列表
				PlanEntrustRecord newEntrustRecord = new PlanEntrustRecord(eb.getEntrustId(), eb.getUnitPrice(), eb.getNumbers().subtract(eb.getCompleteNumber()), eb.getWebId(),eb.getUserId(), eb.getTypes(),
						eb.getNumbers(),eb.getCompleteNumber(),eb.getCompleteTotalMoney(),eb.getSubmitTime(),eb.getStatus(),eb.getTriggerPrice(),eb.getUnitPriceProfit(),eb.getTriggerPriceProfit(),eb.getTotalMoney());
				if(planTrans == null){//Map里面还不存在该价格记录
					planTrans = new TreeMap<Long,PlanEntrustRecord>();
					planTrans.put(eb.getEntrustId(), newEntrustRecord);
					buyHighPlanEntrustsMap.put(triggerPrice, planTrans);
				}else{//添加到触发价格对应记录
					planTrans.put(eb.getEntrustId(), newEntrustRecord);
				}
			}
		}
		
		
		//获取抄底计划买入记录，以触发价为主键
		List<PlanEntrustBean> lowPlanEntrusts = Data.QueryT(m.db,lowPlanSql,new Object[] {}, PlanEntrustBean.class);
		if(lowPlanEntrusts.size() > 0){
			for(PlanEntrustBean eb : lowPlanEntrusts){
				BigDecimal triggerPrice = eb.getTriggerPriceProfit();
				TreeMap<Long,PlanEntrustRecord> palnTrans = buyLowPlanEntrustsMap.get(triggerPrice);//可扫描价列表
				PlanEntrustRecord newPlanEntrust = new PlanEntrustRecord(eb.getEntrustId(), eb.getUnitPrice(), eb.getNumbers().subtract(eb.getCompleteNumber()), eb.getWebId(),eb.getUserId(), eb.getTypes(),
						eb.getNumbers(),eb.getCompleteNumber(),eb.getCompleteTotalMoney(),eb.getSubmitTime(),eb.getStatus(),eb.getTriggerPrice(),eb.getUnitPriceProfit(),eb.getTriggerPriceProfit(),eb.getTotalMoney());
				if(palnTrans == null){//Map里面还不存在该价格记录
					palnTrans = new TreeMap<Long,PlanEntrustRecord>();
					palnTrans.put(eb.getEntrustId(), newPlanEntrust);
					buyLowPlanEntrustsMap.put(triggerPrice, palnTrans);
				}else{//添加到触发价格对应记录
					palnTrans.put(eb.getEntrustId(), newPlanEntrust);
				}
			}
		}
		
		
		//获取 止盈计划买卖出记录，以触发价为键
		List<PlanEntrustBean> profitPlanEntrusts = Data.QueryT(m.db,profitPlanSql,new Object[] {}, PlanEntrustBean.class);
		if(profitPlanEntrusts.size() > 0){
			for(PlanEntrustBean eb : profitPlanEntrusts){
				BigDecimal triggerPrice = eb.getTriggerPriceProfit();
				TreeMap<Long,PlanEntrustRecord> planTrans = sellHighPlanEntrustsMap.get(triggerPrice);//可扫描价列表
				PlanEntrustRecord newEntrustRecord = new PlanEntrustRecord(eb.getEntrustId(), eb.getUnitPrice(), eb.getNumbers().subtract(eb.getCompleteNumber()), eb.getWebId(),eb.getUserId(), eb.getTypes(),
						eb.getNumbers(),eb.getCompleteNumber(),eb.getCompleteTotalMoney(),eb.getSubmitTime(),eb.getStatus(),eb.getTriggerPrice(),eb.getUnitPriceProfit(),eb.getTriggerPriceProfit(),eb.getTotalMoney());
				if(planTrans == null){//Map里面还不存在该价格记录
					planTrans = new TreeMap<Long,PlanEntrustRecord>();
					planTrans.put(eb.getEntrustId(), newEntrustRecord);
					sellHighPlanEntrustsMap.put(triggerPrice, planTrans);
				}else{//添加到触发价格对应记录
					planTrans.put(eb.getEntrustId(), newEntrustRecord);
				}
			}
		}
		
		
		//获取止损、抄底计划卖出记录，以触发价为主键
		List<PlanEntrustBean> stopPlanEntrusts = Data.QueryT(m.db,stopPlanSql,new Object[] {}, PlanEntrustBean.class);
		if(stopPlanEntrusts.size() > 0){
			for(PlanEntrustBean eb : stopPlanEntrusts){
				BigDecimal triggerPrice = eb.getTriggerPrice();
				TreeMap<Long,PlanEntrustRecord> palnTrans = sellLowPlanEntrustsMap.get(triggerPrice);//可扫描价列表
				PlanEntrustRecord newPlanEntrust = new PlanEntrustRecord(eb.getEntrustId(), eb.getUnitPrice(), eb.getNumbers().subtract(eb.getCompleteNumber()), eb.getWebId(),eb.getUserId(), eb.getTypes(),
						eb.getNumbers(),eb.getCompleteNumber(),eb.getCompleteTotalMoney(),eb.getSubmitTime(),eb.getStatus(),eb.getTriggerPrice(),eb.getUnitPriceProfit(),eb.getTriggerPriceProfit(),eb.getTotalMoney());
				if(palnTrans == null){//Map里面还不存在该价格记录
					palnTrans = new TreeMap<Long,PlanEntrustRecord>();
					palnTrans.put(eb.getEntrustId(), newPlanEntrust);
					sellLowPlanEntrustsMap.put(triggerPrice, palnTrans);
				}else{//添加到触发价格对应记录
					palnTrans.put(eb.getEntrustId(), newPlanEntrust);
				}
			}
		}
		initMap.put(m.market, true);
		long s6 = System.currentTimeMillis();
		log.info("开始初始化计划委托盘数据完成共耗时：" + (s6 - s1));
		log.info("买盘追高计划数据价格区间长度：" + (buyHighPlanEntrustsMap.size()) + ",买盘追高计划委托单数量：" + highPlanEntrusts.size());
		log.info("买盘抄底计划数据价格区间长度：" + (buyLowPlanEntrustsMap.size()) + ",买盘抄底计划委托单数量：" + lowPlanEntrusts.size());
		log.info("卖盘止盈计划数据价格区间长度：" + (sellHighPlanEntrustsMap.size()) + ",卖盘止盈计划委托单数量：" + profitPlanEntrusts.size());
		log.info("卖盘止损数据价格区间长度：" + (sellLowPlanEntrustsMap.size()) + ",卖盘止损计划委托单数量：" + stopPlanEntrusts.size());
	}
	
	/**
	 * 处理计划委托触发转成正式委托
	 */
	public static void processOne(Market m){
		//最新成交价
		BigDecimal  lastPrice = ChartManager.getPrice(m);
		//log.info("one:"+one);
		   if(lastPrice.compareTo(BigDecimal.ZERO)==0){
			   return ;
		   }
		if(!isInit(m)){
			init(m);
		}
		int nums=0;
		//log.info("最新成交价格："+lastPrice);
		long start = System.currentTimeMillis();
		
		TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> buyHighPlanEntrustsMap =  getBuyHighPlanEntrustsMap(m);
		
		TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> buyLowPlanEntrustsMap  = getBuyLowPlanEntrustsMap(m);
		
		//高价卖出止盈集合
		TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> sellHighPlanEntrustsMap = getSellHighPlanEntrustsMap(m);
		
		//低价卖出止损集合
		TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> sellLowPlanEntrustsMap  = getSellLowPlanEntrustsMap(m);
		
		NavigableMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> canProfitEntrustMap = null;
		NavigableMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> canStopEntrustMap = null;
		NavigableMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> canHighEntrustMap = null;
		NavigableMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> canLowEntrustMap = null;
		if(lastPrice.compareTo(BigDecimal.ZERO)>0){
			//检查卖盘 止盈计划委托价格是否触发 ,止盈：计划委托触发价<=最新成交价格，进行触发
			canProfitEntrustMap = sellHighPlanEntrustsMap.headMap(lastPrice,true);
		 
			//检测卖盘 止损计划委托价格是否触发，止损：计划委托触发价>=最新成交价格，进行触发
			canStopEntrustMap = sellLowPlanEntrustsMap.headMap(lastPrice,true);
			
			//检查买盘 追高计划委托价格是否触发 ,追高 ：计划委托触发价<=最新成交价格，进行触发
			canHighEntrustMap = buyHighPlanEntrustsMap.headMap(lastPrice,true);
		 
			//检测买盘 抄底计划委托价格是否触发， 抄底：计划委托触发价>=最新成交价格，进行触发
			canLowEntrustMap =  buyLowPlanEntrustsMap.headMap(lastPrice,true);
		}
		//log.info("canEntrustMap："+canBuyEntrustMap+" canSellEntrustMap:"+canSellEntrustMap);
		
		//1、处理 卖盘止盈计划委托
		nums += processPlanEntrust(canProfitEntrustMap, 0,m);
		
		//2、处理卖盘 止损 计划委托
		nums += processPlanEntrust(canStopEntrustMap, 1,m);
		
		//3、处理买盘追高计划委托
		nums += processPlanEntrust(canHighEntrustMap, 2,m);
		
		//4、处理买盘 抄底计划委托
		nums += processPlanEntrust(canLowEntrustMap, 3,m);
			
		long end = System.currentTimeMillis();
//		log.info("[计划委托处理] 处理计划委托"+nums+"个，完成共耗时：" + (end-start));
	}

	/**
	 * 将待处理计划委托集合数据，转化为正式委托
	 * @param planEntrustMap 待处理计划委托集合数据
	 * @param strategyType 计划委托策略类型 0：止盈 1：止损  2：追高 3：抄底
	 * @return 返回成功处理记录条数
	 * @author zhanglinbo 20161019
	 */
	
	public static int processPlanEntrust(NavigableMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> planEntrustMap, int strategyType,Market m){
		int nums = 0;//成功处理记录条数
		if(planEntrustMap!=null && !planEntrustMap.isEmpty()){
			Entry<BigDecimal, TreeMap<Long, PlanEntrustRecord>> entry = planEntrustMap.firstEntry();
			while(entry != null){
				PlanEntrustRecord per = null;
				TreeMap<Long,PlanEntrustRecord> records = entry.getValue();
				if(records != null && records.size() > 0){
					Entry<Long, PlanEntrustRecord> firstEntry = records.firstEntry();
					per = firstEntry.getValue();
				}
				
				while(per != null){
					//计算委托价格和数量
					BigDecimal unitPrice = BigDecimal.ZERO;
					BigDecimal number =BigDecimal.ZERO;
					if(per.getTypes()==1){//买入计划
						if(strategyType==2){//追高
							unitPrice = per.getPrice();
							number = per.getNumber();//兼容旧的计划委托 之前有存数量，但无总金额
						}else if(strategyType==3){//抄底
							unitPrice = per.getPriceProfit();
						}
						if(number.compareTo(BigDecimal.ZERO)<=0){//数量<=0；根据总金额与单价的价格进行计算数量。
							number = per.getTotalMoney().divide(unitPrice,m.numberBixDian,BigDecimal.ROUND_DOWN);
						}
					}else{//卖出计划
						if(strategyType==0){//止盈委托价
							unitPrice = per.getPriceProfit();
						}else if(strategyType==1){//止损
							unitPrice = per.getPrice();
						}
						number = per.getNumber();//止盈数量
					}
					
					if(unitPrice.compareTo(BigDecimal.ZERO)>0 && number.compareTo(BigDecimal.ZERO)>0){//单价和数量都不为0的时候才进行转化正式委托
						InfoEntrust  in = Interface.doPlan2Entrust(per.getTypes(), per.getUserId(),number,unitPrice,per.getWebId(),per.getWebId(),0, per.getTotalMoney(),per.getId(),m);
						if(in.in.equals(Info.DoEntrustSuccess)){//如果没成功就返回状态信息
							//1、委托成功,移除 止盈 缓存集合记录
							records.remove(per.getId());
							//2、移除同计划委托的止盈/止损数据(同一计划委托只能触发一个方向)
							removeEntrust(per.getTypes(),per.getTriggerPriceProfit(),per.getTriggerPrice(),per.getId(),m);
							nums++;
							if(records != null && !records.isEmpty()){
								if(records != null && records.size() > 0){
									Entry<Long, PlanEntrustRecord> firstEntry = records.firstEntry();
									per = firstEntry.getValue();
								}
							}else{
								per = null;
							}
						}else{//处理计划委托转正式委托失败打印日志
							//失败会造成死循环，故直接返回 退出 ，等待下一次再处理。
							per.setMatchTimes(per.getMatchTimes()+1);
							log.error(in.in.getMessage());
							if(per.getMatchTimes()>10){//超过10次下单不成功，从缓存集合移除
								//1、无法下单的计划,  缓存集合记录
								records.remove(per.getId());
								//获取下一个计划继续执行
								if(records != null && !records.isEmpty()){
									if(records != null && records.size() > 0){
										Entry<Long, PlanEntrustRecord> firstEntry = records.firstEntry();
										per = firstEntry.getValue();
									}
								}else{
									per = null;
								}
							}
						}
					}else{//数据有问题
						//从缓存移除，继续下一条操作
						records.remove(per.getId());
						if(records != null && !records.isEmpty()){
							if(records != null && records.size() > 0){
								Entry<Long, PlanEntrustRecord> firstEntry = records.firstEntry();
								per = firstEntry.getValue();
							}
						}else{
							per = null;
						}
					}
				}//end while per
				if(per == null){//按下一个价位进行委托
					planEntrustMap.remove(entry.getKey());
					entry = planEntrustMap.firstEntry();
				}
			}//end while entry
		}//end if
		
		return nums;
	}
	
	
	/**
	 * 移除同一策略的记录
	 * @param type 买卖类型 1：买  0 ：卖
	 * @param triggerPriceProfit 触发高价
	 * @param triggerPrice 触发低价
	 * @param entrustId 委托ID
	 * @return 移除成功
	 */
	public static boolean removeEntrust(int type,BigDecimal triggerPriceProfit ,BigDecimal triggerPrice,long entrustId,Market m){
		try{
			TreeMap<Long,PlanEntrustRecord> tempMap = null;
			
			
			if(type==1){//买入计划
				TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> buyHighPlanEntrustsMap = getBuyHighPlanEntrustsMap(m);
				
				if(triggerPrice.compareTo(BigDecimal.ZERO)>0){//追高触发价
					tempMap =  buyHighPlanEntrustsMap.get(triggerPrice);
					if(tempMap!=null){
						tempMap.remove(entrustId);
						if(tempMap==null || tempMap.isEmpty()){//如果此价位已经没有记录，移除对象
							buyHighPlanEntrustsMap.remove(triggerPrice);
						}
					}else{
						buyHighPlanEntrustsMap.remove(triggerPrice);
					}
				}
				TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> buyLowPlanEntrustsMap = getBuyLowPlanEntrustsMap(m);
				
				if(triggerPriceProfit.compareTo(BigDecimal.ZERO)>0){//抄底触发价
					tempMap =  buyLowPlanEntrustsMap.get(triggerPriceProfit);
					if(tempMap!=null){
						tempMap.remove(entrustId);
						if(tempMap==null || tempMap.isEmpty()){//如果此价位已经没有记录，移除对象
							buyLowPlanEntrustsMap.remove(triggerPriceProfit);
						}
					}else{
						buyLowPlanEntrustsMap.remove(triggerPriceProfit);
					}
				}
				
			}else{//卖出计划
				if(triggerPriceProfit.compareTo(BigDecimal.ZERO)>0){//止盈触发价
					TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> sellHighPlanEntrustsMap = getSellHighPlanEntrustsMap(m);
					tempMap =  sellHighPlanEntrustsMap.get(triggerPriceProfit);
					if(tempMap!=null){
						tempMap.remove(entrustId);
						if(tempMap==null || tempMap.isEmpty()){//如果此价位已经没有记录，移除对象
							sellHighPlanEntrustsMap.remove(triggerPriceProfit);
						}
					}else{
						sellHighPlanEntrustsMap.remove(triggerPriceProfit);
					}
				}
				TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> sellLowPlanEntrustsMap = getSellLowPlanEntrustsMap(m);
				
				if(triggerPrice.compareTo(BigDecimal.ZERO)>0){//卖委托
					tempMap =  sellLowPlanEntrustsMap.get(triggerPrice);
					if(tempMap!=null){
						tempMap.remove(entrustId);
						if(tempMap==null || tempMap.isEmpty()){//如果此价位已经没有记录，移除对象
							sellLowPlanEntrustsMap.remove(triggerPrice);
						}
					}else{
						sellLowPlanEntrustsMap.remove(triggerPrice);
					}
				}
			}
			
			
			return true;
		}catch(Exception e){
			log.error(e.toString(), e);
		}
		return false;
	}
	
	/**
	 * 添加记录 到缓存集合
	 * @param triggerPriceProfit 抄底、止盈触发价
	 * @param triggerPrice 追高、止损触发价格
	 * @param type 买卖类型 1：买  0：卖
	 * @param entrustId 计划委托ID
	 * @param planEntrustRecord 计划委托对象
	 * @return 处理是否成功
	 */
	public static boolean addEntrust(BigDecimal triggerPriceProfit, BigDecimal triggerPrice,int type,long entrustId,PlanEntrustRecord planEntrustRecord,Market m){
		try{
			
			
			if(type==1){//买入计划
				if(triggerPrice.compareTo(BigDecimal.ZERO)>0){//追高委托集合增加数据
					TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> buyHighPlanEntrustsMap = getBuyHighPlanEntrustsMap(m);
					TreeMap<Long,PlanEntrustRecord> priceMap =  buyHighPlanEntrustsMap.get(triggerPrice);
					 if(priceMap==null){
						 priceMap = new TreeMap<Long,PlanEntrustRecord>();
					 }
					 priceMap.put(entrustId, planEntrustRecord);
					 buyHighPlanEntrustsMap.put(triggerPrice, priceMap);
				}
				
				if(triggerPriceProfit.compareTo(BigDecimal.ZERO)>0){//抄底委托集合增加数据
					TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> buyLowPlanEntrustsMap = getBuyLowPlanEntrustsMap(m);
					
					TreeMap<Long,PlanEntrustRecord> priceMap =  buyLowPlanEntrustsMap.get(triggerPriceProfit);
					 if(priceMap==null){
						 priceMap = new TreeMap<Long,PlanEntrustRecord>();
					 }
					 priceMap.put(entrustId, planEntrustRecord);
					 buyLowPlanEntrustsMap.put(triggerPriceProfit, priceMap);
				}
				
			}else{//卖出计划
				if(triggerPriceProfit.compareTo(BigDecimal.ZERO)>0){//追高、止盈委托集合增加数据
					TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> sellHighPlanEntrustsMap = getSellHighPlanEntrustsMap(m);
					
					TreeMap<Long,PlanEntrustRecord> priceMap =  sellHighPlanEntrustsMap.get(triggerPriceProfit);
					 if(priceMap==null){
						 priceMap = new TreeMap<Long,PlanEntrustRecord>();
					 }
					 priceMap.put(entrustId, planEntrustRecord);
					 sellHighPlanEntrustsMap.put(triggerPriceProfit, priceMap);
				}
				if(triggerPrice.compareTo(BigDecimal.ZERO)>0){//抄底、止损委托集合增加数据
					TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> sellLowPlanEntrustsMap = getSellLowPlanEntrustsMap(m);
					
					TreeMap<Long,PlanEntrustRecord> priceMap =  sellLowPlanEntrustsMap.get(triggerPrice);
					 if(priceMap==null){
						 priceMap = new TreeMap<Long,PlanEntrustRecord>();
					 }
					 priceMap.put(entrustId, planEntrustRecord);
					 sellLowPlanEntrustsMap.put(triggerPrice, priceMap);
				}
			}
			return true;
		}catch(Exception e){
			log.error(e.toString(), e);
		}
		return false;
	}
	
	
	/**
	 * 获取高价追高集合委托
	 * @param m 币种市场参数
	 * @return treeMap 按价格从低到高排序
	 */
	private static TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> getBuyHighPlanEntrustsMap(Market m){
			TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> buyHighPlanEntrustsMap = highPlanEntrustsMap.get(m.market);
			if(buyHighPlanEntrustsMap == null){
				buyHighPlanEntrustsMap = new TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>>();//按价格从低到高排序
				highPlanEntrustsMap.put(m.market, buyHighPlanEntrustsMap);
			}
			return buyHighPlanEntrustsMap;
	}
	
	/**
	 * 获取低价抄底集合委托
	 * @param m 币种市场参数
	 * @return treeMap 按价格从高到底排序
	 */
	private static TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> getBuyLowPlanEntrustsMap(Market m){
			TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> buyLowPlanEntrustsMap  = lowPlanEntrustsMap.get(m.market);
			if(buyLowPlanEntrustsMap==null){
				buyLowPlanEntrustsMap = new TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>>(new Comparator<BigDecimal>() {
					@Override
					public int compare(BigDecimal o1, BigDecimal o2) {
						return o2.compareTo(o1);
					}
				});//按价格从高到底排序
				lowPlanEntrustsMap.put(m.market, buyLowPlanEntrustsMap);
			}
			return buyLowPlanEntrustsMap;
	}
	
	/**
	 * 获取高价止盈集合委托
	 * @param m 币种市场参数
	 * @return treeMap 按价格从低到高排序
	 */
	private static TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> getSellHighPlanEntrustsMap(Market m){
		//高价卖出止盈集合
		TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> sellHighPlanEntrustsMap = profitPlanEntrustsMap.get(m.market);
		if(sellHighPlanEntrustsMap == null){
			sellHighPlanEntrustsMap = new TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>>();//按价格从低到高排序
			profitPlanEntrustsMap.put(m.market, sellHighPlanEntrustsMap);
		}
		return sellHighPlanEntrustsMap;
	}
	
	/**
	 * 获取低价卖出止损集合委托
	 * @param m 币种市场参数
	 * @return treeMap 按价格从高到底排序
	 */
	private static TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> getSellLowPlanEntrustsMap(Market m){
		//低价卖出止损集合
		TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>> sellLowPlanEntrustsMap  = stopPlanEntrustsMap.get(m.market);
		if(sellLowPlanEntrustsMap==null){
			sellLowPlanEntrustsMap = new TreeMap<BigDecimal, TreeMap<Long,PlanEntrustRecord>>(new Comparator<BigDecimal>() {
				@Override
				public int compare(BigDecimal o1, BigDecimal o2) {
					return o2.compareTo(o1);
				}
			});//按价格从高到底排序
			stopPlanEntrustsMap.put(m.market, sellLowPlanEntrustsMap);
		}	
		return sellLowPlanEntrustsMap;
	}
	
	
}

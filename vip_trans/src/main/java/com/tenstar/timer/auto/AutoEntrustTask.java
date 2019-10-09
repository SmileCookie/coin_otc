package com.tenstar.timer.auto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.tenstar.Arith;
import com.tenstar.HTTPTcp;
import com.tenstar.timer.entrust.Interface;
import com.world.cache.Cache;
import com.world.data.mysql.Data;
import com.world.model.Market;
import com.world.model.daos.chart.ChartManager;


//在这里进行两个账户之间的各种委托，主要是形成真实的没卖

/**
 * 自动委托
 * @author netpet
 *
 */
public class AutoEntrustTask extends TimerTask{
	public static Logger log = Logger.getLogger(AutoEntrustTask.class);
	public static long second=0;
	
	static Random commonRandom = new Random();
	public static com.tenstar.RobotEntrustConfig rc=null;
	public static boolean isChange=false;
	
	private static int depthSize;
	private static double[][] depth;
	
	private Market m;
	
	public AutoEntrustTask(Market m){  
		this.m = m;
		ReloadConfig(m);
	}

	//重新加载config
	public void ReloadConfig(Market m){
		String cacheKey = "systemDefaultRobotEntrustConfig"+m.numberBiEn;
		if (!"cny".equalsIgnoreCase(m.exchangeBiEn) && !"rmb".equalsIgnoreCase(m.exchangeBiEn) && !"btq".equalsIgnoreCase(m.numberBiEn)) {
			cacheKey += m.exchangeBiEn;
		}
		Object obj=Cache.Get(cacheKey);
		if(obj==null){
			List l=(List)Data.GetOne(m.db,"select * from autoEntrustConfig where names=? and isDefault=1", new Object[]{"systemDefault"});
			if(l==null){
				rc=new com.tenstar.RobotEntrustConfig();
				String objs=HTTPTcp.ObjectToString(rc);
				Data.Insert(m.db,"INSERT INTO autoEntrustConfig (NAMES, objs, isDefault, notes, times)VALUES(?,?,?,?,?);" ,new Object[]{
						"systemDefault",
						objs,
						1, 
						"系统创建",
						System.currentTimeMillis()
				});
				Cache.Set(cacheKey,objs,24*3600);
				obj=objs;
			}else{
				obj= l.get(2);
				Cache.Set(cacheKey, l.get(2).toString(),24*3600);
			}
		}
		
	    rc=(com.tenstar.RobotEntrustConfig)HTTPTcp.StringToObject(obj.toString());
	    depthSize = rc.getDangwei();
	}
	
	//
	@Override
	public void run() {
		   second++;
		   if(second<10){//等待10秒钟
			    //log.info("等待系统启动");
			     return;
		   }
		   //30秒钟重新加载一次数据
		   if(second%30==0){
			   ReloadConfig(m);
		   }
		   //每5秒执行一次
		   /*if(second%5==0){
			   if(SystemStatus.autoRuning) { 
					try {
						if(rc.getIsStart() == 0){
							//log.info("@@---委托机器人不走了");
							return;
						}
						depth = MemEntrustMatchProcessor.da.getTopDepth(depthSize);
						
						if(depth == null){
							log.info("@@---没有委托数据");
							return;
						}
						//第一步，委托是否挂满档位，如果没有挂满则填充满
						stepOne();
						//第二步，区间差额填充,并委托数量叠加
						stepTwo();
						//第三步，取消偏离当前价格的委托
						stepThree();
					} catch (Exception ex) {
						log.error(ex.toString(), ex);
					}
				}
		   }*/
		}
		
	
		/**
		 * 第一步,委托是否挂满档位，如果没有挂满则填充满
		 */
		public static void stepOne(Market m){
			boolean flag = false;
			double qujianDifference = getQujianDifference(rc.getQujianDifference());
			double price = 0;
			    //卖盘
			   for (int i = 0;i<depthSize;i++) {
				   if(i >= 1){
					   //此档位没有挂单
					   if(depth[i][2] == 1 || depth[i][0] == 0 ||  depth[i][1] == 0){
						  if(price == 0)
							  price = Arith.add(depth[i-1][0], qujianDifference);
						  else 
							  price = Arith.add(price, qujianDifference);
						  double num = getOverNum(m);
						  Interface.doEntrust(0, rc.getUserId(), Market.formatNumberLong(num,m), Market.formatMoneyLong(price,m), rc.getWebId(), rc.getWebId(), 0, 1, null,m);
						  log.info("@----已成功委托卖出"+num+m.numberBiEn+"，其中价格为："+price);
						  flag = true;
					   }
					   
				   }
			   }
			   price = 0;
			   //买盘
			   for (int i = depthSize;i<depth.length;i++) {
				   if(i > depthSize){
					  //此档位没有挂单
					   if(depth[i][0] == 0 || depth[i][1] == 0){
						  if(price == 0)
							  price = Arith.sub(depth[i-1][0], qujianDifference);
						  else 
							  price = Arith.sub(price, qujianDifference);
						  double num = getOverNum(m);
						  Interface.doEntrust(1, rc.getUserId(), Market.formatNumberLong(num,m), Market.formatMoneyLong(price,m), rc.getWebId(), rc.getWebId(), 0, 1, null,m);
						  log.info("@----已成功委托买入"+num+m.numberBiEn+"，其中价格为："+price);
						  flag = true;
					   }
				   }
			   }
			   
			   if(flag)
				   isChange = true;
			   else 
				   isChange = false;
		}
		
		/**
		 * 第二步,区间差额填充,并委托数量叠加
		 */
		public static void stepTwo(Market m){
			if(isChange)
				return;
			
			BigDecimal current= ChartManager.getPrice(m);//Market.formatMoney(MemEntrustMatchProcessor.da.lastPrice);
			double qujianDifference = getQujianDifference(rc.getQujianDifference());
			double qujianMaxNum = getQujianMaxNum(rc.getQujianMaxNum());
			
			int j = 1;
			for (int i = 0;i<depth.length;i++) {
				if(i == depthSize - 1) {
					j++;
					continue;
				}
				if(j <= depth.length - 1){
					double price1 = 0;
					double price2 = 0;
					if(i < depthSize - 1){
						if(i == 0){
						   price1 = depth[depthSize][0];
						   price2 = depth[i][0];
						}else{
						   price1 = depth[i][0];
						   price2 = depth[j][0];
						}
						//区间差额大于设定差额,需区间差额填充
						if(Math.abs(Arith.sub(price1, price2)) > qujianDifference){
							int type = 0;
							if(i == 1){
								type = Math.abs(Arith.sub(current.doubleValue(), price1)) > Math.abs(Arith.sub(current.doubleValue(), price2)) ? 0 : 1;
							}
							if("LTC".equalsIgnoreCase(m.numberBiEn)){
								 //Interface.doEntrustMore(rc.getWebId(), rc.getUserId(),  Market.formatMoneyLong(price1),  Market.formatMoneyLong(price2), Market.formatNumberLong(qujianMaxNum), type);
								 double num = radomDouble(qujianMaxNum/2, qujianMaxNum, 3);
								 Interface.doEntrust(0, rc.getUserId(), Market.formatNumberLong(num,m), Market.formatMoneyLong(Arith.div((price2 + price1), 2, 3),m), rc.getWebId(), rc.getWebId(), 0, 1, null,m);
								 log.info("@----执行委托卖单填充到区间"+price1+"-" +price2+"，本次委托数量为："+ num + m.numberBiEn);
							}
							if("BTC".equalsIgnoreCase(m.numberBiEn)){
								 Interface.doEntrustMore(rc.getWebId(), rc.getUserId(),  Market.formatMoneyLong(price1,m),  Market.formatMoneyLong(price2,m), Market.formatNumberLong(qujianMaxNum,m), type,m);
								 log.info("@----执行多笔委托卖单填充到区间"+price1+"-" +price2+"，本次委托数量为："+ qujianMaxNum + m.numberBiEn);
							}
							//每次区间填充都在高价位上做委托数量叠加
							int split = commonRandom.nextInt(rc.getOverNum() + 1);
							for (int x = 0 ; x < split ; x ++) {
								double num = getOverNum(m);
								Interface.doEntrust(0, rc.getUserId(), Market.formatNumberLong(num,m), Market.formatMoneyLong(price2,m), rc.getWebId(), rc.getWebId(), 0, 1, null,m);
								log.info("@----已成功委托卖出"+num+m.numberBiEn+"，其中价格为："+price2);
							}
						}
					}else{
						price1 = depth[i][0];
						price2 = depth[j][0];
						//区间差额大于设定差额,需区间差额填充
						if(Math.abs(Arith.sub(price1, price2)) > qujianDifference){
							log.info("@----执行多笔委托买单填充到区间"+price1+"-" +price2+"，本次委托数量为："+ qujianMaxNum + m.numberBiEn);
							if("LTC".equalsIgnoreCase(m.numberBiEn)){
								 //Interface.doEntrustMore(rc.getWebId(), rc.getUserId(),  Market.formatMoneyLong(price1),  Market.formatMoneyLong(price2), Market.formatNumberLong(qujianMaxNum), type);
								 double num = radomDouble(qujianMaxNum/2, qujianMaxNum, 3);
								 Interface.doEntrust(1, rc.getUserId(), Market.formatNumberLong(num,m), Market.formatMoneyLong(Arith.div((price2 + price1), 2, 3),m), rc.getWebId(), rc.getWebId(), 0, 1, null,m);
								 log.info("@----执行委托买单填充到区间"+price1+"-" +price2+"，本次委托数量为："+ num + m.numberBiEn);
							}
							if("BTC".equalsIgnoreCase(m.numberBiEn)){
								 Interface.doEntrustMore(rc.getWebId(), rc.getUserId(),  Market.formatMoneyLong(price2,m),  Market.formatMoneyLong(price1,m), Market.formatNumberLong(qujianMaxNum,m), 1,m);
								 log.info("@----执行多笔委托买单填充到区间"+price1+"-" +price2+"，本次委托数量为："+ qujianMaxNum + m.numberBiEn);
							}
							//每次区间填充都在高价位上做委托数量叠加
							int split = commonRandom.nextInt(rc.getOverNum() + 1);
							for (int x = 0 ; x < split; x ++) {
								double num = getOverNum(m);
								Interface.doEntrust(1, rc.getUserId(), Market.formatNumberLong(num,m), Market.formatMoneyLong(price1,m), rc.getWebId(), rc.getWebId(), 0, 1, null,m);
								log.info("@----已成功委托卖出"+num+m.numberBiEn+"，其中价格为："+price1);
							}
						}
					}
				}
				j++;
			}
		}
		
		/**
		 * 第三步,取消偏离当前价格的委托
		 */
		public static void stepThree(Market m){
			//isChange = true;
			if(isChange)
				return;
			BigDecimal current= ChartManager.getPrice(m);//Market.formatMoney(MemEntrustMatchProcessor.da.lastPrice);
			
			if(current.compareTo(BigDecimal.ZERO) <= 0){
				return;
			}
			//卖盘
			for (int i = 0;i<depthSize;i++) {
				if(depth[i][0] - current.doubleValue() > rc.getQujianCancel()){
					//double priceHigh = depth[depthSize-1][0];
					double priceHigh = 1000000;
					boolean flag = false;
					Interface.doCancle(rc.getWebId(), rc.getUserId(),  Market.formatMoneyLong(depth[i][0],m),  Market.formatMoneyLong(priceHigh,m), 0,m);
					log.info("@----批量取消"+m.numberBiEn+"卖盘成功，其中价格最低价为："+depth[i][0]+"，最高价为："+ priceHigh);
					if(!flag){
						break;
					}
				}
			}
			   //买盘
			   for (int i = depthSize;i<depth.length;i++) {
				   if(current.doubleValue() - depth[i][0] > rc.getQujianCancel()){
						//double priceLow = depth[depth.length-1][0];
					    double priceLow = 1;
						Interface.doCancle(rc.getWebId(), rc.getUserId(),  Market.formatMoneyLong(priceLow,m),  Market.formatMoneyLong(depth[i][0],m), 0,m);
						log.info("@----批量取消"+m.numberBiEn+"买盘成功，其中价格最高价为："+depth[i][0]+"，最低价为："+ priceLow);
						break;
					}
			   }
		}
		 
		private static double getQujianMaxNum(int qujianMaxNum){
			return Arith.div(qujianMaxNum, 100, 3);
		}
		
		private static double getQujianDifference(int qujianDifference){
			return Arith.div(qujianDifference, 10, 3);
		}
		
		private static double getOverNum(Market m){
			double num = 0.01;
			  if("LTC".equalsIgnoreCase(m.numberBiEn)){
				  num = radomDouble(1, 5, 3);
			  }
			  if("BTC".equalsIgnoreCase(m.numberBiEn)){
				  num = radomDouble(0.01, 0.1, 3);
			  }
			 return num;
		}
		
		private static double radomDouble(double min, double max, int points){
			double result = Arith.round(min+Math.random()*max, points);
			if (result > max){
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					log.error(e.toString(), e);
				}
				return radomDouble(min, max, points);
			}else{
				return result;
			}
		}
		
}

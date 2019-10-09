package com.tenstar.timer.auto;

import com.match.entrust.MemEntrustMatchProcessor;
import com.tenstar.Arith;
import com.tenstar.HTTPTcp;
import com.tenstar.Info;
import com.tenstar.SystemStatus;
import com.tenstar.TimeUtil;
import com.tenstar.radom;
import com.world.cache.Cache;
import com.world.data.mysql.Data;
import com.world.model.Market;
import com.world.model.daos.chart.ChartManager;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;


//在这里进行两个账户之间的各种委托，主要是形成真实的没卖

/**
 * 自动委托
 * @author netpet
 *
 */
public class AutoTask extends TimerTask{
	public static Logger log = Logger.getLogger(AutoTask.class);
	public static long times=System.currentTimeMillis();
	public static long second=0;
	
	
	public static long getTimeSpaceTM=0;
	public static long getTimesSpaceBuyIn=0;
	public static long getTimesSpaceSellIn=0;
	public static long getTimesSpaceBuyOut=0;
	public static long getTimesSpaceSellOut=0;
	
	static Random commonRandom = new Random();
	public static com.tenstar.robotConfig rc=null;
	static int current;
	public static boolean isChange=false;
	
	private Market m;
	public AutoTask(Market m){  
		this.m = m;
		ReloadConfig(m);
	}

	//重新加载config
	public void ReloadConfig(Market m){
		
		String cacheKey = "systemDefaultRobotConfig"+m.numberBiEn;
		if (!"cny".equalsIgnoreCase(m.exchangeBiEn) && !"rmb".equalsIgnoreCase(m.exchangeBiEn) && !"btq".equalsIgnoreCase(m.numberBiEn)) {
			cacheKey += m.exchangeBiEn;
		}
		
		Object obj=Cache.Get(cacheKey);
		if(obj==null){
			List l=(List)Data.GetOne(m.db,"select * from autoConfig where names=? and isDefault=1", new Object[]{"systemDefault"});
			if(l==null){
				rc=new com.tenstar.robotConfig();
				String objs=HTTPTcp.ObjectToString(rc);
				Data.Insert(m.db,"INSERT INTO autoconfig (NAMES, objs, isDefault, notes, times)VALUES(?,?,?,?,?);" ,new Object[]{
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
		
	    rc=(com.tenstar.robotConfig)HTTPTcp.StringToObject(obj.toString());

	
		//设置变量
		getTimeSpaceTM=rc.getTimeSpaceTM()*10;
		getTimesSpaceBuyIn=rc.getTimesSpaceBuyIn()*10;
		getTimesSpaceSellIn=rc.getTimesSpaceSellIn()*10;
		getTimesSpaceBuyOut=rc.getTimesSpaceBuyOut()*10;
		getTimesSpaceSellOut=rc.getTimesSpaceSellOut()*10;
	}
	
	//
	@Override
	public void run() {

		   second++;
		   if(second<50){//等待10秒钟
			    //log.info("等待系统启动");
			     return;
		   }
		   //5秒钟重新加载一次数据
		   if(second%(50)==0){
			   ReloadConfig(m);
		   }
		
			/*if(SystemStatus.autoRuning) { 
				try {
					//log.info(times);
					long sp=System.currentTimeMillis()-times;
					
				
							
					if(rc.getIsStart() == 0)
					{
						return;
					}
					//second%(rc.getTimeSpaceTM()*10)==0
					if(second%(getTimeSpaceTM)==0||second%(getTimesSpaceBuyIn)==0||second%(getTimesSpaceSellIn)==0||second%(getTimesSpaceBuyOut)==0||second%(getTimesSpaceSellOut)==0||SystemStatus.autoNewWork){//是否需要更新当前价格
						//重置一下时间
					
						
					//if (sp>rc.getTimeSpaceTM()*1000||sp>rc.getTimesSpaceBuyIn()*1000||sp>rc.getTimesSpaceSellIn()*1000||sp>rc.getTimesSpaceBuyOut()*1000||sp>rc.getTimesSpaceSellOut()*1000||SystemStatus.autoNewWork) {//在固定周期内或者主动状态被触发就会执行
						SystemStatus.autoNewWork=false;
					      //	log.info("例行循环");
						  //做一个循环，不断获取需要处理的数据
						int dueWork=0;
						ProcessOne(sp);
//						while(Info.NoMission!=ProcessOne()){
//							dueWork++; 
//							break;
//						}
						if(dueWork>0)
						  log.info("处理了"+dueWork+"个自动委托任务");
						times=System.currentTimeMillis();//增加一个单位跳出本循环
						 
					} 
					else
						Thread.sleep(10);

					
				} catch (Exception ex) {
					log.error(ex.toString(), ex);
				}
				try{
				Thread.sleep(10);
			}catch(Exception ex){
				}
			}*/
		}
		
	
		/**
		 * 处理一个任务
		 * @return 是否处理成功
		 */
		public static Info ProcessOne(long sp,Market m){
			
			   BigDecimal current = ChartManager.getPrice(m);//MemEntrustMatchProcessor.da.lastPrice;

			   BigDecimal[] buysell = ChartManager.getLbuyOneAndSellOne(m);
			   
			   if(buysell == null){
				   return null;
			   }
			   BigDecimal minSellPrice = buysell[1];//MemEntrustMatchProcessor.da.getSellOne();
			   BigDecimal maxbuyPrice=buysell[0];//MemEntrustMatchProcessor.da.getBuyOne();
			
			   BigDecimal minSellPriceSys= minSellPrice; 
			   minSellPriceSys = minSellPriceSys.add(BigDecimal.valueOf(rc.getSafePriceQuJian()));//
			   BigDecimal maxbuyPriceSys=maxbuyPrice;
			   maxbuyPrice = maxbuyPrice.subtract(BigDecimal.valueOf(rc.getSafePriceQuJian()));//
			   
			  // log.info("最后成交价格："+lastpPrice+" 真实卖一:"+sellPrice+"真实买一:"+buyPrice+"系统委托卖一:"+minSellPriceSys+"系统委托买一:"+maxbuyPriceSys);
		
			   BigDecimal highMiddle=minSellPrice;
			   
			   if(minSellPriceSys.compareTo(BigDecimal.ZERO)!=0&&minSellPrice.compareTo(minSellPriceSys)>0)
				   highMiddle=minSellPriceSys;
			   BigDecimal lowMiddle=maxbuyPrice;
			   if(maxbuyPriceSys.compareTo(maxbuyPrice)>0&&maxbuyPriceSys.compareTo(BigDecimal.ZERO)!=0)
				   lowMiddle=maxbuyPriceSys;
			   current=lowMiddle.add(highMiddle).divide(BigDecimal.valueOf(2),m.exchangeBixDian);
			  // log.info("最后中间价："+current+" 真实卖一:"+minSellPrice+"真实买一:"+maxbuyPrice+"系统委托卖一:"+minSellPriceSys+"系统委托买一:"+maxbuyPriceSys);
			//	log.info(sp+":"+rc.getTimeSpaceTM()+":"+second+":"+getTimeSpaceTM);
	 
		     if(second%(getTimeSpaceTM)==0){   
		    		if(rc.getIsStartTrade() == 0)
					{
						return null;
					}
				   liang(m);
				   isChange=true;
					getTimeSpaceTM=radom.radomLong(rc.getTimeSpaceTM(),60)*10; 
					log.info(getTimeSpaceTM);
	        }
			  
	
			if(second%(getTimesSpaceBuyIn)==0){//内盘买入档
				log.info(second+"=========="+getTimesSpaceBuyIn);
				  try{ 
						entrustQuJian(Market.formatMoney(maxbuyPrice.subtract(BigDecimal.valueOf(1)),m),Market.formatMoney(maxbuyPrice,m)-rc.getEntrustRobotQuJian(),rc.getNumberTotalBuyIn(),true,rc.getDuiQiBaiFenBiBuyIn(),rc.getSplitNumberBuyIn(),rc.getMaxDangweiBuyIn(),m);
				         isChange=true;
				        // MemEntrustMatchProcessor.da.SetTrade(m);
				         getTimesSpaceBuyIn=radom.radomLong(rc.getTimesSpaceBuyIn(),30)*10; 
				   }catch(Exception ex){
					   log.error("内盘买入档出错"+ex.toString(), ex);
				   } 
			} 
			
			
			if(second%(getTimesSpaceSellIn)==0){//内盘卖出档
				  try{
				         entrustQuJian(Market.formatMoney(minSellPriceSys,m)+rc.getEntrustRobotQuJian(),Market.formatMoney(minSellPriceSys.add(BigDecimal.valueOf(1)),m),rc.getNumberTotalSellIn(),false,rc.getDuiQiBaiFenBiSellIn(),rc.getSplitNumberSellIn(),rc.getMaxDangweiSellIn(),m);
				         isChange=true;
				         getTimesSpaceSellIn=radom.radomLong(rc.getTimesSpaceSellIn(),30)*10; 
				   }catch(Exception ex){
					   log.error("内盘卖出档出错"+ex.toString(), ex);
				   }
			}
			 
			if(second%(getTimesSpaceBuyOut)==0){//外盘买入档
				 try{
			         entrustQuJian(Market.formatMoney(maxbuyPrice,m)-rc.getEntrustRobotQuJian(),rc.getLowPriceBuyOut(),rc.getNumberTotalBuyOut(),true,rc.getDuiQiBaiFenBiBuyOut(),rc.getSplitNumberBuyOut(),rc.getMaxDangweiBuyOut(),m);
			         isChange=true;
			         getTimesSpaceBuyOut=radom.radomLong(rc.getTimesSpaceBuyOut(),50)*10; 
			   }catch(Exception ex){
				   
				   log.error("外盘买入档出错"+ex.toString(), ex);
				    
			   }
			}
			
			if(second%(getTimesSpaceSellOut)==0){//外盘卖出档
				   try{
				         entrustQuJian(rc.getHighPriceSellOut(),Market.formatMoney(minSellPriceSys,m)+rc.getEntrustRobotQuJian(),rc.getNumberTotalSellOut(),false,rc.getDuiQiBaiFenBiSellOut(),rc.getSplitNumberSellOut(),rc.getMaxDangweiSellOut(),m);
				         isChange=true;
				         getTimesSpaceSellOut=radom.radomLong(rc.getTimesSpaceSellOut(),50)*10; 
				   }catch(Exception ex){
					   log.error("外盘卖出档"+ex.toString(), ex);
				   }
			}
			
			if(isChange){
				//刷新
				 isChange=false;
			}
	

			return Info.DueEntrustSuccess;
		}
		
		/**
		 * 进行一次成交
		 */
		public static void vitureTrans(){
		   	
		}
		
		static int[] baifenbi=new int[]{80,70,50,40,20,30,40,60,80,100,120,150,130,100,120,140,110,100,90,110,120,150,120,90};
		
		 
		/**
		 * 用来凭空产生一单
		 * 一次委托的量
		 * @return
		 */
		public static void liang(Market m){
			//百分币
			int per=100;
			int minutem=TimeUtil.getMinuteFirstInDay(System.currentTimeMillis());
			if(minutem==0)
				minutem=1;
			int hour=minutem/60;
			if(hour>23)
				hour=23;
				per=baifenbi[hour];
				
				long total=rc.getTotalTransEveryDay();
				int beishu=radom.radomInt(100);
				//实际挂出去的应该是现实的一半
				if(beishu>90)
					total=total*200/200;
				else if(beishu>70)
					total=total*110/200;
				else if(beishu>50)
					total=total*60/200;
				else if(beishu>30)
					total=total*30/200;
				else
					total=total*10/200;
				
			double split=Arith.div(total*rc.getTimeSpaceTM(),60*60*24);
			
			log.info("split:"+split);
			split=split*per/100;//使之所符合当前时间
			log.info("split:"+split);
			int percentTwo=radom.radomInt(30, 170); 
			
			split=split*percentTwo/100;//使之所符合
			log.info("split:"+split);
			
			split=radomSet(split);
			log.info("split:"+split);
			
			int isBuy=radom.radomInt(100);
			if(rc.getEntrustQuShi()>0){
				//涨势的情况下大多是买入的
				if(isBuy>40)
					isBuy=1;
				else
					isBuy=0;
			}else{
				if(isBuy>60)
					isBuy=1;
				else
					isBuy=0;
			}
		
			
			BigDecimal minSellPrice= MemEntrustMatchProcessor.da.getSellOne();
			
			
			minSellPrice = minSellPrice.subtract(BigDecimal.ONE);
			BigDecimal maxbuyPrice=MemEntrustMatchProcessor.da.getBuyOne();
			
			
		
			
			maxbuyPrice = maxbuyPrice.add(BigDecimal.ONE)  ;

			int percentThree=radom.radomInt(2, 99);
			
			double unitPrice=Arith.div((minSellPrice.subtract(maxbuyPrice)).multiply(BigDecimal.valueOf(percentThree)).doubleValue(),100)+maxbuyPrice.doubleValue();
			unitPrice=Arith.div(unitPrice,100);
			unitPrice=radomSet(unitPrice);
			
			log.info(minSellPrice+":"+":"+maxbuyPrice+":"+per+":"+":"+percentTwo+":"+percentThree+":"+unitPrice);
			
			//split=split*Market.numberBixNormal;
		
			BigDecimal number=Market.formatNumberLong(split,m);
			BigDecimal price=Market.formatMoneyLong(unitPrice,m);
			if(price.compareTo(maxbuyPrice)<=0)
				price=maxbuyPrice.add(BigDecimal.ONE);
			if(price.compareTo(minSellPrice)>=0)
				price=minSellPrice.subtract(BigDecimal.ONE); 
			log.info("split:"+split);
			vitureTrans(price,number,isBuy,m); 
			
			
			
			//进行真实的委托买卖
			
			//SystemStatus.chartDataNewWork=true;
			SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.chartDataNewWork,true);
		    //重新生成成交记录
		    //MemEntrustMatchProcessor.da.SetTrade(m);
		}
		
		/**
		 * 进行一笔虚拟成交 
		 * @param unitPrice 单价
		 * @param number 数量
		 * @param type 类型 0卖 1 买
		 */
		public static void vitureTrans(BigDecimal unitPrice,BigDecimal number,int type,Market m){
			long autoId=com.tenstar.autoId.getId(m.getMarket()+"entrust",m.db);
			long autoId2=com.tenstar.autoId.getId(m.getMarket()+"entrust",m.db);
			int count=Data.Insert(m.db,
					"INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell,status,actStatus) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					 new Object[] {
							unitPrice,
							unitPrice.multiply(number),
							number,
							autoId,
							1089,
							autoId2,
							1066,
							type,//当前记录是买行为还是卖行为
							TimeUtil.getNow().getTime(),
							TimeUtil.getMinuteFirst().getTime(),
							8,
							8,
							2, //将他设置成成功，避免资金问题
							0
				});
			//更新队列
			long recordId = count;
			//MemEntrustMatchProcessor.da.updateRecord(recordId,unitPrice,number,type,TimeUtil.getNow().getTime());
			log.info("说明："+count);
		}
		
		/**
		 * 取消虚拟撤单，并且生成新的订单
		 * 解决由用户委托引起的撤单
		 * @param isBuy  是否是在购买
		 * @param currentPrice  当前价格
		 */
		public static int cancleVitureEntrust(boolean isBuy,long currentPrice,Market m){
			String sql=isBuy?"SELECT price,btcEntrust,btcEntrustSys FROM entrustlevel WHERE (btcEntrust>0 or btcEntrustSys>0) and price<=?":
				"SELECT price,btcEntrust,btcEntrustSys FROM entrustlevel WHERE (btcEntrust<0 or btcEntrustSys<0) and  price>=? ";
			log.info("==============================================================================================================================="+sql); 
			List lsell=Data.Query(m.db,sql, new Object[]{currentPrice});
			if(lsell==null||lsell.size()==0){
				log.info("没有数据");
				return 0;//没有数据，当然不太可能存在
			}
			boolean hasSys=false;
			
			for(int i=0;i<lsell.size();i++){//本循环旨在确定是否有系统
				List one=(List)lsell.get(i);
				long btcSys=Long.parseLong(one.get(2).toString());
				if(btcSys!=0)
					hasSys=true;
			}
			if(!hasSys){
				log.info("没有需要成交的虚拟数据");
				return 0;
			}

			//把用户第一笔真实单子之前的先模拟成交掉，
			//1 取得最后成交时间，避免时间错位，把本比成交时间提前几秒钟，
			
			long secNew=System.currentTimeMillis();
			
	        //if(splitPrice!=0){//说明是有分界点的
	        //log.info("分界点是："+splitPrice);
			List li=(List)Data.GetOne(m.db,"select time from transrecord order by transRecordId desc limit 0,1", new Object[]{});
		   //要确定一个合理的成交时间
			if(li!=null&&li.get(0)!=null){
				//有最后成交记录
				long now=secNew;
				long last=Timestamp.valueOf(li.get(0).toString()).getTime();
				long sec=(now-last)/1000;//间隔时间
			
				log.info("秒钟数："+sec);
				if(sec>10)//十秒钟内随机
				{
					 int rm = commonRandom.nextInt(10);
					 if(rm<2)
						 rm=1;//至少差一秒
					 secNew=now-rm*1000;//倒退回去这么久
				}
				else if(sec<1)//不能穿透，直接用当前秒钟
				{
					 secNew=now;
				}else
				{
					 int rm = commonRandom.nextInt(Integer.parseInt(Long.toString(sec)));
					 secNew=now-rm*1000;//倒退回去这么久
				}
			}
			
				//User ubuy = new User("88520", "imbuyer", null);
				//User usell = new User("88521", "imseller", null);
				long entrustBuyId = 0;
				long entrustSellId = 0;
				if(isBuy){
					entrustBuyId = 1;
				}else{
					entrustSellId = 1;
				}
				double uPrice=0;
				log.info("zheli "+lsell.size());
				//时间订好了	
				for(int i=0;i<lsell.size();i++){ //本循环用于找到分界点
					List one=isBuy?(List)lsell.get(i):(List)lsell.get(lsell.size()-i-1);
					int p=Math.abs(Integer.parseInt(one.get(0).toString())); 
					long btc=Math.abs(Long.parseLong(one.get(1).toString()));
					long btcSys=Math.abs(Long.parseLong(one.get(2).toString()));
					uPrice=Arith.div(p, 100);
					if(btc!=0&&btcSys==0)
						break;
					if(btcSys!=0){//相等的也不成交
						if(btcSys<0)
							btcSys=-btcSys;
						
						int nid = Data.Insert(m.db,"INSERT INTO  transrecord (unitPrice,totalPrice, btcs, entrustIdBuy, userIdBuy, userNameBuy, entrustIdSell, userIdSell, userNameSell, isBuy, time, timeMinute, isCount,status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 
						new Object[]{
//								uPrice,Arith.div(DigitalUtil.longMultiply(uPrice, btcSys),fee.btcFee),btcSys,entrustBuyId,Integer.parseInt(ubuy.getId()), ubuy.getUserName(),entrustSellId,Integer.parseInt(usell.get_Id()),usell.getUserName(),
//						isBuy?1:0,
//						 new Timestamp(secNew),
//						TimeUtil.getMinuteFirst(),
//						3,7
				      });
						
						
					}
					if(btc!=0)//只要大于0就不用了
						break;
				}
				log.info("zheli ");
				int count=0;
				if(isBuy) 
					count=Data.Update(m.db,"update entrustlevel SET btcEntrustSys=0 where btcEntrustSys>0 and price<=?", new Object[]{currentPrice});
				else
					count=Data.Update(m.db,"update entrustlevel SET btcEntrustSys=0 where btcEntrustSys<0 and price>=?", new Object[]{currentPrice});
				 
				log.info("zheli ");
				//MarketCache.resetCurrentPrice(uPrice);///顺序很重要
				//时间订好了	
	            return count;
	}
		 
			/**
			 * 一个不等于0和最大值的max int
			 * @param max
			 * @return 随机数
			 */
		private static int radomInt(int max){
			if(max<0)
				max=-max;
			if(max==0)
				max=100;
			 int rm =0;
			 while(rm==0)
				 rm= commonRandom.nextInt(max);
			 return rm;
		}
		
		/**
		 * 区间委托
		 * @param priceHigh 区间最高价格
		 * @param priceLow 区间最低价格
		 * @param maxEntityNumber 最大区间委托
		 * @param gaoWeiDuiQi 是否是高位对齐 是的话就意味着排列趋向高位
		 * @param zuiXiaoDuiQiBaiFenBi 最小对其的百分比也就意味着随机区间的另一半位置不能超过这个设置的百分比
		 * @param split 本次操作分割的数量，分割次数
		 * @param maxDangWei 整个区间的最大档位
		 */
		private static void entrustQuJian(double priceHigh,double priceLow,double maxEntityNumber,boolean gaoWeiDuiQi,int zuiXiaoDuiQiBaiFenBi,int split,int maxDangWei,Market m){
		
			
		 int rm =radomInt(100);
			// long [] l=MemEntrustMatchProcessor.da.getTotal(Market.formatMoneyInt(priceLow,m), Market.formatMoneyInt(priceHigh,m),1);
			//long hasEntityNumber=l[0]/m.numberBixNormal;
		 	long hasEntityNumber  =0;
	 
			 double nowNum=(maxEntityNumber-hasEntityNumber);
			
			 if(nowNum<5)
				 nowNum=10;
			 
			    	nowNum=nowNum/rm;
			    	if(nowNum>70)
			    		nowNum=radomInt(70);
			    	else if(nowNum<1)
			    		nowNum=radomInt(10);
			//log.info("本轮委托数量："+nowNum);  
			int rmDuiQi=rm;
			if(rmDuiQi<zuiXiaoDuiQiBaiFenBi)
				rmDuiQi=zuiXiaoDuiQiBaiFenBi;

		
			if(gaoWeiDuiQi) // 高位对其，也就是买盘
			{
				priceLow=(priceHigh-priceLow)*rmDuiQi/100+priceLow;//基本采取一个倒梯子型;
			} 
			else//低位对齐，也就是卖盘
				priceHigh=(priceHigh-priceLow)*rmDuiQi/100+priceLow;//基本采取一个倒梯子型;
			
			int isBuy=gaoWeiDuiQi?1:0;
			int rmNum =radomInt(split);//随机的分割数量
			   if(rmNum<2)
				   rmNum=2;
		
		  // double diDangwei=l[1];//Integer.parseInt(lCount.get(0)==null?"0":lCount.get(0).toString());//低端区域的档位数量
			   double diDangwei =0;
		   log.info("查询委托："+priceHigh+":最高委托"+maxEntityNumber+":本论委托"+nowNum+":diDangwei:"+diDangwei+":hasEntityNumber:"+hasEntityNumber);
		   
			if(diDangwei==0)
			{ 
			     log.info("0000000000:将进行委托"+(gaoWeiDuiQi?"买盘":"卖盘")+"委托操作，最低价格："+priceLow+"最高价格："+priceHigh+" 购买数量："+nowNum);
			     doEntrust(gaoWeiDuiQi,priceLow,priceHigh,nowNum,rmNum,m);//进行买入非常多的多比
		         doEntrust(gaoWeiDuiQi,priceLow,priceHigh,1,rmNum+1,m);//进行买入委托非常小的多比
			}
			else if(hasEntityNumber>=maxEntityNumber&&diDangwei>=maxDangWei)//两个条件同时满足就直接减少
			 {	 //随机进行取消
				 
				 int can[]=getSomeId(priceLow,priceHigh,rmNum+3,true,m); 
				 log.info("======================================11111111111:进行取消：========================================"+can.length);
				 for(int i=0;i<can.length;i++){
					 if(can[i]!=0){
						log.info("cancle:"+can[i]);
						// MemEntrustMatchProcessor.da.updateCancleSystem(can[i],0);
					 }
				 }
				 doEntrust(gaoWeiDuiQi,priceLow,priceHigh,1,rmNum+1,m);//进行买入委托非常小的多比
			 }
			 else if(hasEntityNumber<=maxEntityNumber&&diDangwei>=maxDangWei){//仅仅是档位数量多了，但是数量还不够，直接在当前档位增加
				 
				 int can[]=getSomeId(priceLow,priceHigh,rmNum,true,m); 
				 double[][] entrustModel=GetRadom(priceLow,priceHigh,nowNum,10,1,gaoWeiDuiQi);
				 log.info("222222222222:hasEntityNumber:"+hasEntityNumber);
				 for(int i=0;i<can.length;i++){ 
					 double[] one = entrustModel[i];
					 if(can[i]!=0)  
					 {
						 log.info(can[i]+":"+radomSet(one[1])+":"+Market.formatNumberLong(radomSet(one[1]),m));
						/* if(gaoWeiDuiQi)
							 MemEntrustMatchProcessor.da.updateEntrustSystem(can[i],-Market.formatNumberLong(radomSet(one[1]),m));
						 else
							 MemEntrustMatchProcessor.da.updateEntrustSystem(can[i],Market.formatNumberLong(radomSet(one[1]),m));
				*/
					 }
					 //log.info("仅仅是档位数量多了，但是数量"+hasEntityNumber+"还不够"+maxEntityNumber+",随机增加"+(gaoWeiDuiQi?"低位":"高位")+"："+can[i]+":"+one[1]); 
				 }
				 doEntrust(gaoWeiDuiQi,priceLow,priceHigh,1,rmNum+3,m);//进行买入委托非常小的多比
			 }
			 else if(hasEntityNumber>=maxEntityNumber&&diDangwei<=maxDangWei){//数量够了，但是档位还不够，增加1的档位
				log.info("数量够了，但是档位还不够，增加1的档位");
				 doEntrust(gaoWeiDuiQi,priceLow,priceHigh,1,rmNum+1,m);//进行买入委托非常小的多比
			 }
			 else{
				 log.info("不知道什么可能");
				 doEntrust(gaoWeiDuiQi,priceLow,priceHigh,nowNum,rmNum,m);//进行买入非常多的多比
		         doEntrust(gaoWeiDuiQi,priceLow,priceHigh,1,rmNum+1,m);//进行买入委托非常小的多比
			 }
			
			//更新数据
				
		}
	
			
	  

		/**
		 * 获取随机的需要取消的id，因为mysql随机获取id有问题，所以用这种办法，价格区间只能限定在一定的区间，两个价格区间不要大于200最好
		 * @param priceLow 最低价
		 * @param priceHigh 最高价
		 * @param num 数量
		 * @param isBuy 是买还是卖，买入的一方，价格依靠最低价随机，卖出的一方，依靠最高价依靠
		 */
		static int[]  getSomeId(double priceLow,double priceHigh,int num,boolean gaoWieDuiQi,Market m){
			/*
			int begin=Market.formatMoneyInt(priceLow,m);
			int end=Market.formatMoneyInt(priceHigh,m);
			 return MemEntrustMatchProcessor.da.getSomePrice(begin, end, num, gaoWieDuiQi, 1);
			*/
			return new int[1];
		}
			 
		
		//100的整数，而且将数字80%的几率都设置成整数，12的几率设置成一位小数，8的几率是2位
		public static int get100Int(double price){
		
			//price*100
			String uPrice=Double.toString(Arith.round(Arith.mul(price, 100) , 0));
			 int rm = commonRandom.nextInt(100);
			int rtn=Integer.parseInt(uPrice.substring(0,uPrice.indexOf('.')));
			if(rm<=70)
				rtn=(rtn/100)*100;
			else if(rm<=85)
				rtn=(rtn/10)*10;
	
			return rtn;
		}
		//格式化2位
			public static double radomSet(double value){
				 int rm = commonRandom.nextInt(100);
				DecimalFormat df=null;
				if(rm<40)
					df=new DecimalFormat("#.");
				else if(rm<85)
					df=new DecimalFormat("#.#");
				else
					df=new DecimalFormat("#.##");
				
				String st=df.format(value);
				if(Double.parseDouble(st)==0){
					if(rm<10)
					   st="0.01";
					else if(rm<20)
						st="0.02";
					else if(rm<30)
						st="0.03";
					else if(rm<40)
						st="0.04";
					else if(rm<50)
						st="0.05";
					else if(rm<60)
						st="0.06";
					else if(rm<70)
						st="0.07";
					else if(rm<90)
						st="0.02";
					else
						st="0.1";
				}
				double s=Double.parseDouble(st);
			//	log.info("====================="+Long.parseLong(rtn));
			//	double s=0;
				return s;
			}
			
			/**
			 * 获取指定的随机数数组
			 * @param minUnitPrice 最小的价格
			 * @param maxUnitPrice 最高的价格
			 * @param btcNumber 比特币数量
			 * @param splitNum 分割数量
			 * @param type 类型   1 均匀分布
			 * @param gaoWeiDuiQi  是否是按照价格从上向下对其
			 * @return 一个数组
			 */
			private static double[][] GetRadom(double minUnitPrice,double maxUnitPrice,double btcNumber,int splitNum,int type,boolean gaoWeiDuiQi){
				minUnitPrice=minUnitPrice*100;//精确到分
				maxUnitPrice=maxUnitPrice*100;//精确到分
				btcNumber=btcNumber*1000;//精确到0.001
				
				
				log.info("minUnitPrice:"+minUnitPrice+"maxUnitPrice"+maxUnitPrice+"btcNumber:"+btcNumber);
				
				double qujian=maxUnitPrice-minUnitPrice;

				double[][] array = new double[300][];//随便固定一个数量
				
				double base=gaoWeiDuiQi?maxUnitPrice:minUnitPrice;
				double baseBtc=btcNumber;
				int i=0;
				for(;i<splitNum;i++){
					
					double fudu=qujian/splitNum;//直线类型的每次幅度是这样的
					double btcSplit=btcNumber/splitNum;
					 int rm =radomInt(160);
					 fudu=fudu*(180-rm)/100;//左右摆动20%
					 int rm2 = radomInt(160);
					 btcSplit=btcSplit*(180-rm2)/100;//也是左右摆动20%
			 
					
					 
					 if(gaoWeiDuiQi){
						 base-=fudu; 
					 }else{
						 base+=fudu;
					 }
					 
					 //log.info("对齐："+base+":"+i+":"+fudu+":"+gaoWeiDuiQi+":"+baseBtc);
					 
					 
					 baseBtc-=btcSplit;
					 if(baseBtc<=0||base>maxUnitPrice||base<minUnitPrice)
						 break;
			
					    array[i]= new double[]{base/100,btcSplit/1000};
				}

				return array;
			}
			
			
			public static void main(String[] args){
				double[][] entrustModel=GetRadom(1000.00,1100.00,20,10,1,true);
				for(int j=0;j<100;j++){
				    int rmNum1 = commonRandom.nextInt(6);
				   if(rmNum1<1)
					   rmNum1=1;
				    //double newNum=10*rmNum1*Market.numberBixNormal;
				  //  log.info(";;;;;"+newNum/Market.numberBixNormal);
				}
				 
	            for(int i = 0; i < entrustModel.length; i ++){
					double[] one = entrustModel[i];
					if(one!=null)
					log.info(":::"+one[0]+"   -------------------   "+one[1]);
	             }
			}
			
		//只进行虚假委托
		static void doEntrust(boolean gaoWeiDuiQi,double minUnitPrice,double maxUnitPrice,double btcNumber,int split,Market m) {
			try {
			
				

				double entrustNumber = 0;
				double totalPrice = 0;
				int count = 0;
				
				//String rtn = "";
				
				double[][] entrustModel =GetRadom(minUnitPrice,maxUnitPrice,btcNumber,split,1,gaoWeiDuiQi);
						//SillySort.sort(SillyUtil.getRandomArray(minUnitPrice, maxUnitPrice, btcNumber, isBuy), isBuy);
				log.info(minUnitPrice+":"+maxUnitPrice+":"+btcNumber+":"+entrustModel.length);
				for(int i = 0; i < entrustModel.length; i ++){
					
					double[] one = entrustModel[i];
			        if(one==null)
			        	continue;
					//log.info(one[0]+":"+get100Int(one[0])+"============="+one[1]+"======"+radomSet(one[1])+":"+Market.formatNumberLong(radomSet(one[1])));
					//定时器处理
					int rtn=0;
					/*if(gaoWeiDuiQi)
						MemEntrustMatchProcessor.da.updateEntrustSystem(get100Int(one[0]),-Market.formatNumberLong(radomSet(one[1]),m));
					else
						MemEntrustMatchProcessor.da.updateEntrustSystem(get100Int(one[0]),Market.formatNumberLong(radomSet(one[1]),m));
					*/
					//rtn = entity.entrust(isBuy, userId, one[1], one[0]);
					entrustNumber++;//
					if(rtn>0){ 
						if(gaoWeiDuiQi){
							maxUnitPrice = one[0];
						}else{
							minUnitPrice = one[0];
						}
						entrustNumber =0;// DigitalUtil.add(entrustNumber, one[1]);
						totalPrice =0;// DigitalUtil.add(totalPrice, DigitalUtil.mul(one[0], one[1]));
						count ++;
					}
					
				} 
				if (entrustNumber > 0){
					if(gaoWeiDuiQi){
						log.info("已成功委托"+count+"笔记录买入"+entrustNumber+"，其中委托买入的最高价为："+maxUnitPrice);
					}else{
						log.info("已成功委托"+count+"笔记录卖出"+entrustNumber+"，其中委托卖出的最低价为："+minUnitPrice);
					}
				}else
					log.info("没有成功个委托一笔数据");
			} catch (Exception e) {
				log.error(e.toString(), e);
				
			}
		}
}

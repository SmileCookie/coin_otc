package com.tenstar.timer.auto;

import com.match.entrust.MemEntrustMatchProcessor;
import com.tenstar.HTTPTcp;
import com.tenstar.Info;
import com.tenstar.UserConfig;
import com.tenstar.radom;
import com.tenstar.timer.entrust.Interface;
import com.world.cache.Cache;
import com.world.data.mysql.Data;
import com.world.model.Market;
import com.world.model.daos.chart.ChartManager;
import com.world.model.entitys.entrust.EntrustBean;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

/**
 * 用户资金的自动委托
 * @author netpet
 *
 */
public class AutoTaskUser extends TimerTask{
	public static Logger log = Logger.getLogger(AutoTaskUser.class);
	public static long times=System.currentTimeMillis();
	public static long second=0;
	
	
	
	public static long getTimeSpaceTM=0;
	public static long getTimesSpaceBuyIn=0;
	public static long getTimesSpaceSellIn=0;
	public static long getTimesSpaceBuyOut=0;
	public static long getTimesSpaceSellOut=0;

	
	//用户配置文件集合，每个文件控制一个用户
	public static UserConfig[] ucs=null;
	public static int  max=1000;
	
	static Random commonRandom = new Random();
	
	static int current;
	public static boolean isChange=false;
	private Market m;
	
	public AutoTaskUser(Market m){
		ReloadConfig(m);
		this.m = m;
	}

	//重新加载config
	public void ReloadConfig(Market m){
		
		String cacheKey = "userAutoConfig"+m.numberBiEn;
		if (!"cny".equalsIgnoreCase(m.exchangeBiEn) && !"rmb".equalsIgnoreCase(m.exchangeBiEn) && !"btq".equalsIgnoreCase(m.numberBiEn)) {
			cacheKey += m.exchangeBiEn;
		}
		
		if(ucs==null)
			ucs=new UserConfig[max]; 
		for(int i=0;i<max;i++)
			ucs[i]=null;
		
		List l=(List)Data.Query(m.db,"select * from autoConfig where userId>0 and isDefault=1 limit 0,"+max, new Object[]{});
		if(l!=null&&l.size()>0){
			for(int i=0;i<l.size();i++){
				List one=(List)l.get(i);
				ucs[i]=(com.tenstar.UserConfig)HTTPTcp.StringToObject(one.get(2).toString());
				Cache.Set(cacheKey+ucs[i].getUserId(), one.get(2).toString(),24*365*3600); 
			}
		}
	}

	/**
	 * 更新一个用户的数据
	 * @param uc
	 */
	public static void updateuserData(UserConfig uc){
		boolean isUpdated=false;
		int nowIndex=0;
		for(int i=0;i<max;i++){
			   if(ucs[i]==null){
				   nowIndex++;
				    break;
			   }
			   
			   if(uc.getUserId()==ucs[i].getUserId())
			   {
				   ucs[i]=uc;
				   nowIndex++;
				   isUpdated=true;
				   break;
			   }
		   }
		if(nowIndex<(max-1))
			 ucs[nowIndex]=uc;
	}
	//
	@Override
	public void run() {

		   second++;
		   if(second<500){//等待10秒钟
			    //log.info("等待系统启动");
			     return;
		   }

		   for(int i=0;i<max;i++){
			   if(ucs[i]!=null&&second%(ucs[i].getTimeSpace()*100)==0){
				   ProcessOne(ucs[i],m); 
			   }
		   }
		}
		//允许可以自动交易的对方id
	static int[] useridAuto=new int[]{105187,105190};
		/**
		 * 处理一个任务
		 * @return 是否处理成功
		 */
		public static Info ProcessOne(UserConfig uc,Market m){
			if(uc.getIsStart()==0)
				return null;
			   
			   BigDecimal current=ChartManager.getPrice(m);
			   
			   if(current.compareTo(BigDecimal.ZERO) <= 0){
				   return null;
			   }
			   BigDecimal minSellPrice= MemEntrustMatchProcessor.da.getSellOne();
			   BigDecimal maxbuyPrice=MemEntrustMatchProcessor.da.getBuyOne();
			   BigDecimal minSellPriceSys= minSellPrice;
			   
			  int qushi=uc.getEntrustQuShi();//委托趋势 上涨趋势就主动挂单买入 下跌趋势就一直主动挂单卖出
			   List l=Data.Query(m.db,"select *   from entrust where userid=? and status=3  ", new Object[]{
					   uc.getUserId()
					  // current-uc.getEntrustQuJian()*Market.exchangeBixNormal,
					 //  current+uc.getEntrustQuJian()*Market.exchangeBixNormal
			   },EntrustBean.class);
			   
			   int countSell=0;
			   BigDecimal sellLiang=BigDecimal.ZERO;
			   int countBuy=0;
			   BigDecimal buyLiang=BigDecimal.ZERO;
			   //这里是临时性的  对方用户id
			   int otherUserId=105187;
			   if(uc.getUserId()==105187)
				   otherUserId=105190;
			   
			   if(l!=null&&l.size()>0){
				   for(int i=0;i<l.size();i++){
					   EntrustBean one=(EntrustBean)l.get(i);
					   BigDecimal li=(one.getNumbers());
					   if(li.compareTo(BigDecimal.ZERO)>0){
						   countSell++;
						   sellLiang = sellLiang.add(li) ;
					   }
					   else if(li.compareTo(BigDecimal.ZERO)<0){
						   countBuy++;
						   sellLiang = sellLiang.add(li) ;//都是加
					   }
				   }
			   }
			   //如果可以随机成交一些订单
			   //第一，看是否有指定id的用户订单，如果有就直接可以成交,查找真实用户之间的对方id有无委托，如果有就可以帮他成交掉
			   List entrustOther=Data.Query(m.db,"select * from entrust where userid=? and status=3  and unitPrice>? and unitPrice<? ", new Object[]{
					   otherUserId,
					   maxbuyPrice,
					   minSellPrice
			   },EntrustBean.class);
			   if(entrustOther!=null&&entrustOther.size()>0){
				   int  countSellOther=0;
				   BigDecimal sellLiangOther=BigDecimal.ZERO;
				   int countBuyOther=0;
				   BigDecimal buyLiangOther=BigDecimal.ZERO;
				   for(int i=0;i<entrustOther.size();i++){
					   EntrustBean one=(EntrustBean)entrustOther.get(i);
					   BigDecimal li=(one.getNumbers());
					   if(li.compareTo(BigDecimal.ZERO)>0){
						   countSellOther++;
						   sellLiangOther = sellLiangOther.add(li) ;
					   }
					   else if(li.compareTo(BigDecimal.ZERO)<0){
						   countBuyOther++;
						   sellLiangOther = sellLiangOther.add(li);//都是加
					   }
				   }
				   if(countBuyOther>countSellOther){
					   //进行卖给他操作
					  //
					   log.info("准备卖出："+countBuyOther+":"+(maxbuyPrice.subtract(BigDecimal.valueOf(1))));
					   Interface.doEntrust(0, uc.getUserId(), BigDecimal.valueOf(countBuyOther), maxbuyPrice.subtract(BigDecimal.valueOf(1)), 8, 8, 0, 0, null,m);
					   
				   }else
				   {
					   //进行买操作
					   log.info("准备买入："+countSellOther+":"+minSellPrice.subtract(BigDecimal.valueOf(1)));
					   Interface.doEntrust(1, uc.getUserId(), BigDecimal.valueOf(countSellOther), minSellPrice.subtract(BigDecimal.valueOf(1)), 8, 8, 0, 0, null,m);
				   }
				   
			   }
			   
			   //第二，看是否有获利的订单，如果有就直接成交吗如果没有等待
			   if(sellLiang.compareTo(uc.getEntrustMaxSell())<0){
				   
				   //委托一些卖单
				   int numberSp=0;
				   if(m.numberBiEn.equals("BTC"))
					   numberSp=radom.radomInt(1, 10);
				   else if(m.numberBiEn.equals("LTC"))
					   numberSp=radom.radomInt(20, 200);
				   if(numberSp==0)
					   return null;
				  // log.info("批量委托卖单："+(current)+":"+(current+uc.getEntrustQuJian()*m.exchangeBixNormal)+":"+numberSp);
				   Interface.doEntrustMore(0, uc.getUserId(), current,
						   current.add(BigDecimal.valueOf(uc.getEntrustQuJian())), BigDecimal.valueOf(numberSp), 0,m);
				   
			   }
			   
			   if(buyLiang.compareTo(uc.getExtrustMaxBuy())<0){
				   //委托一些买单
				   //委托一些卖单
				   int numberSp=0;
				   if(m.numberBiEn.equals("BTC"))
					   numberSp=radom.radomInt(1, 10);
				   else if(m.numberBiEn.equals("LTC"))
					   numberSp=radom.radomInt(20, 200);
				   if(numberSp==0)
					    return null;
				    
				  // log.info("批量委托买单："+(current-uc.getEntrustQuJian()*m.exchangeBixNormal)+":"+(current)+":"+numberSp);
				   Interface.doEntrustMore(0, uc.getUserId(), current.subtract(BigDecimal.valueOf(uc.getEntrustQuJian())),
						   current, BigDecimal.valueOf(numberSp), 1,m);
			   }
			   
			   if(countSell>uc.getEntrustMaxDangWei().intValue()){
				   //随机撤销一些卖单
				   if(l!=null&&l.size()>0){
					   for(int i=0;i<l.size();i++){
						   EntrustBean one=(EntrustBean)l.get(i);
						   if(one.getUnitPrice().compareTo(current.add(BigDecimal.valueOf(uc.getEntrustQuJian())))>0)//跑到价格体系之外了，需要撤销｛
						   {
							   log.info("随机撤销卖单"+one.getEntrustId());
							   Interface.doCancle(uc.getUserId(), one.getEntrustId(),m);
						   }
					   }
				   }
			   }
			   
			   if(countBuy>uc.getEntrustMaxDangWei().intValue()){
				   //随机撤销一些买单
				   //随机撤销一些卖单
				   if(l!=null&&l.size()>0){
					   for(int i=0;i<l.size();i++){
						   EntrustBean one=(EntrustBean)l.get(i);
						   if(one.getUnitPrice().compareTo(current.add(BigDecimal.valueOf(uc.getEntrustQuJian())))<0)//跑到价格体系之外了，需要撤销
						   {  log.info("随机撤销买单"+one.getEntrustId());
							     Interface.doCancle(uc.getUserId(), one.getEntrustId(),m);
						   }
					   }
				   }
			   }
			   
			   


			return Info.DueEntrustSuccess;
		}
		
}
